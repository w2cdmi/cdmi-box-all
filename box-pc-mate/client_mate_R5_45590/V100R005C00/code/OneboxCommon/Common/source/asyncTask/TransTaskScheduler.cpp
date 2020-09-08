#include "TransTaskScheduler.h"
#include <boost/thread.hpp>
#include "ConfigureMgr.h"
#include "TransTask.h"
#include "UserContextMgr.h"
#include "AsyncTaskMgr.h"
#include "Utility.h"
#include "NotifyMgr.h"
#include "NetworkMgr.h"
#include "CommonTransmitNotify.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTaskScheduler")
#endif

#define SCHEDULE_TASK_INTERVAL_LAZY (200)
#define SCHEDULE_TASK_INTERVAL (1)
#define DEFAULT_BACKUP_POOL_THREADS (1)

typedef std::list<CISSPTaskPtr> TransTasks;

struct RetryTransTask
{
	AsyncTransDetailNode node;
	uint32_t retryTimes;
	DWORD lastRunTime;
};
typedef std::list<RetryTransTask> RetryTransTasks;

class TransTaskScheduler::Impl
{
public:
	class TransTaskNotify : public CISSPNotify
	{
	public:
		TransTaskNotify(const std::wstring& retryErrorCodes, Impl* scheduler)
			:scheduler_(scheduler)
		{
			init(retryErrorCodes);
		}

		// task go to 'complete' status
		virtual void TaskCompleteNotify(const CISSPTaskPtr ptrTask)
		{
			scheduler_->removeFromRunningTasks(ptrTask);
			if (ptrTask->IsCompletedWithSuccess())
			{
				scheduler_->removeFromRetryTasks(ptrTask);
			}
			else if (ptrTask->IsCompletedWithError())
			{
				// backup task need not retry
				if (((TransTask*)(ptrTask.get()))->getTransDetailNode()->root->type != ATT_Backup)
				{
					int32_t errorCode = ptrTask->GetErrorCode();
					for (std::list<int32_t>::iterator it = errorCodes_.begin(); 
						it != errorCodes_.end(); ++it)
					{
						if (errorCode == *it)
						{
							scheduler_->pushToRetryTasks(ptrTask);
							return;
						}
					}
					scheduler_->removeFromRetryTasks(ptrTask);
				}
			}
		}

	private:
		void init(const std::wstring& retryErrorCodes)
		{
			std::vector<std::wstring> result;
			Utility::String::split(retryErrorCodes, result, L";");
			for (std::vector<std::wstring>::iterator it = result.begin(); 
				it != result.end(); ++it)
			{
				if (!it->empty())
				{
					errorCodes_.push_back(Utility::String::string_to_type<int32_t>(*it));
				}
			}
		}

	private:
		Impl* scheduler_;
		std::list<int32_t> errorCodes_;
	};

	Impl(UserContext* userContext, TransTableMgr* transTableMgr)
		:userContext_(userContext)
		,transTableMgr_(transTableMgr)
		,runningTransTaskNum_(0)
		,runningBackupTransTaskNum_(0)
	{
		try
		{
			(void)init();
		}
		catch(...) {}
	}

	~Impl()
	{
		try
		{
			(void)release();
		}
		catch(...) {}
	}

	int32_t run()
	{
		(void)interrupt();
		thread_ = boost::thread(boost::bind(&Impl::schedule, this));
		return RT_OK;
	}

	int32_t interrupt()
	{
		try
		{
			thread_.interrupt();
			thread_.join();

			{
				// update detail status to waiting for auto transmit next time
				boost::mutex::scoped_lock lock(mutex_);
				TransTask* task = NULL;
				TransDetailTablePtr detailTable;
				for (TransTasks::iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
				{
					task = dynamic_cast<TransTask*>((*it).get());
					AsyncTransDetailNode& detailNode = task->getTransDetailNode();
					if (!task->IsCompleted())
					{
						if (detailNode->root->fileType != FILE_TYPE_FILE)
						{
							detailTable = transTableMgr_->getDetailTable(detailNode->root->group);
							if (NULL == detailTable.get())
							{
								HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
									"failed to get detail table of task %s.", 
									Utility::String::wstring_to_string(detailNode->root->group).c_str());
								continue;
							}
							int32_t ret = detailTable->updateStatus(detailNode->source, ATS_Waiting);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, 
									"failed to update detail table status to cancel, task source is %s.", 
									Utility::String::wstring_to_string(detailNode->source).c_str());
								continue;
							}
						}
					}
				}
			}

			if (NULL != backupTransTaskPool_.get())
			{
				backupTransTaskPool_->CancelAllTask();
				backupTransTaskPool_->WaitAllTaskFinish();
			}
			if (NULL != transTaskPool_.get())
			{
				transTaskPool_->CancelAllTask();
				transTaskPool_->WaitAllTaskFinish();
			}
		}
		catch(...) {}
		return RT_OK;
	}

	int32_t interruptRunningTask(const std::wstring& group, bool async)
	{
		TransTasks interruptTasks;
		TransTask* task = NULL;
		int32_t ret = RT_OK;
		// get all tasks need to be interrupted
		// if we do not do like this, may cause dead lock when user interrupt a task with sync mode
		{
			boost::mutex::scoped_lock lock(mutex_);
			AsyncTransDetailNode detailNode;
			TransDetailTablePtr detailTable;
			for (TransTasks::iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
			{
				task = dynamic_cast<TransTask*>((*it).get());
				detailNode = task->getTransDetailNode();
				if (detailNode->root->group == group)
				{
					if (!task->IsCompleted())
					{
						interruptTasks.push_back(*it);
						// update detail status to cancel
						if (detailNode->root->fileType != FILE_TYPE_FILE)
						{
							detailTable = transTableMgr_->getDetailTable(detailNode->root->group);
							if (NULL == detailTable.get())
							{
								HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
									"failed to get detail table of task %s.", 
									Utility::String::wstring_to_string(detailNode->root->group).c_str());
								return RT_ERROR;
							}
							ret = detailTable->updateStatus(detailNode->source, ATS_Cancel);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, 
									"failed to update detail table status to cancel, task source is %s.", 
									Utility::String::wstring_to_string(detailNode->source).c_str());
								return ret;
							}
						}
					}
				}
			}

			// update root status to cancel
			TransRootTable* rootTable = transTableMgr_->getRootTable(group);
			if (NULL == rootTable)
			{
				HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
					"failed to get root table of group task %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return RT_INVALID_PARAM;
			}
			ret = rootTable->updateStatus(group, ATS_Cancel);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"failed to update root table status to cancel, task group is %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return ret;
			}
		}
		// interrupt the tasks
		for (TransTasks::iterator it = interruptTasks.begin(); it != interruptTasks.end(); ++it)
		{
			task = dynamic_cast<TransTask*>((*it).get());
			if (!task->IsCompleted())
			{
				task->CancelTask();
				if (!async)
				{
					task->WaitCompleted();
				}
			}
		}
		return RT_OK;
	}

	int32_t interruptRunningTask(const AsyncTransType type, bool async)
	{
		TransTasks interruptTasks;
		TransTask* task = NULL;
		int32_t ret = RT_OK;
		// get all tasks need to be interrupted
		// if we do not do like this, may cause dead lock when user interrupt a task with sync mode
		{
			boost::mutex::scoped_lock lock(mutex_);
			AsyncTransDetailNode detailNode;
			TransDetailTablePtr detailTable = NULL;
			for (TransTasks::iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
			{
				task = dynamic_cast<TransTask*>((*it).get());
				detailNode = task->getTransDetailNode();
				if ((detailNode->root->type&type) != 0)
				{
					if (!task->IsCompleted())
					{
						interruptTasks.push_back(*it);
						// update detail status to cancel
						if (detailNode->root->fileType != FILE_TYPE_FILE)
						{
							detailTable = transTableMgr_->getDetailTable(detailNode->root->group);
							if (NULL == detailTable.get())
							{
								HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
									"failed to get detail table of task %s.", 
									Utility::String::wstring_to_string(detailNode->root->group).c_str());
								return RT_ERROR;
							}
							ret = detailTable->updateStatus(detailNode->source, ATS_Cancel);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, 
									"failed to update detail table status to cancel, task source is %s.", 
									Utility::String::wstring_to_string(detailNode->source).c_str());
								return ret;
							}
						}
						continue;
					}
				}
			}

			// update root status to cancel
			TransRootTable* rootTable = transTableMgr_->getRootTable(type);
			if (NULL == rootTable)
			{
				HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
					"failed to get root table of type task %d.", type);
				return RT_INVALID_PARAM;
			}
			ret = rootTable->updateStatus(type, ATS_Cancel);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"failed to update root table status to cancel, task type is %d.", type);
				return ret;
			}
		}

		// interrupt the tasks
		for (TransTasks::iterator it = interruptTasks.begin(); it != interruptTasks.end(); ++it)
		{
			task = dynamic_cast<TransTask*>((*it).get());
			if (!task->IsCompleted())
			{
				task->CancelTask();
				if (!async)
				{
					task->WaitCompleted();
				}
			}
		}
		return RT_OK;
	}

	int32_t interruptRunningTask(const std::wstring& group, const std::wstring& source)
	{
		TransTask* interruptTask = NULL;
		TransTask* task = NULL;
		int32_t ret = RT_OK;
		// get all tasks need to be interrupted
		// if we do not do like this, may cause dead lock when user interrupt a task with sync mode
		{
			boost::mutex::scoped_lock lock(mutex_);
			AsyncTransDetailNode detailNode;
			TransDetailTablePtr detailTable;
			for (TransTasks::iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
			{
				task = dynamic_cast<TransTask*>((*it).get());
				detailNode = task->getTransDetailNode();
				if (detailNode->source == source && detailNode->root->group == group)
				{
					if (!task->IsCompleted())
					{
						interruptTask = task;
						// update detail status to cancel
						if (detailNode->root->fileType != FILE_TYPE_FILE)
						{
							detailTable = transTableMgr_->getDetailTable(detailNode->root->group);
							if (NULL == detailTable.get())
							{
								HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
									"failed to get detail table of task %s.", 
									Utility::String::wstring_to_string(detailNode->root->group).c_str());
								return RT_ERROR;
							}
							ret = detailTable->updateStatus(detailNode->source, ATS_Cancel);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, 
									"failed to update detail table status to cancel, task source is %s.", 
									Utility::String::wstring_to_string(detailNode->source).c_str());
								return ret;
							}
						}
					}
					break;
				}
			}
		}
		// interrupt the task
		if (NULL != interruptTask)
		{
			if (!interruptTask->IsCompleted())
			{
				interruptTask->CancelTask();
				interruptTask->WaitCompleted();
			}
		}
		return RT_OK;
	}

	void removeFromRunningTasks(const CISSPTaskPtr ptrTask)
	{
		TransTask* transTask = dynamic_cast<TransTask*>(ptrTask.get());
		if (NULL == transTask)
		{
			return;
		}
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		// erase the task from the running tasks
		boost::mutex::scoped_lock lock(mutex_);
		for (TransTasks::iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
		{
			if (transTask == it->get())
			{
				if (transDetailNode->root->type == ATT_Backup)
				{
					--runningBackupTransTaskNum_;
				}
				else
				{
					--runningTransTaskNum_;
				}
				runningTasks_.erase(it);
				break;
			}
		}
	}

	void pushToRetryTasks(const CISSPTaskPtr ptrTask)
	{
		boost::mutex::scoped_lock lock(mutex_);
		TransTask* transTask = dynamic_cast<TransTask*>(ptrTask.get());
		if (NULL == transTask)
		{
			return;
		}
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		for (RetryTransTasks::iterator it = retryTasks_.begin(); it != retryTasks_.end(); ++it)
		{
			AsyncTransDetailNode& tmpTransDetailNode = it->node;
			if (transDetailNode->root->group == tmpTransDetailNode->root->group && 
				transDetailNode->source == tmpTransDetailNode->source)
			{
				if ((int32_t)(++(it->retryTimes)) > userContext_->getConfigureMgr()->getConfigure()->retryTaskTimes())
				{
					retryTasks_.erase(it);
					return;
				}
				it->lastRunTime = GetTickCount();
				return;
			}
		}

		RetryTransTask retryTask;
		retryTask.node = transDetailNode;
		retryTask.retryTimes = 0;
		retryTask.lastRunTime = GetTickCount();
		retryTasks_.push_back(retryTask);
	}

	void removeFromRetryTasks(const CISSPTaskPtr ptrTask)
	{
		boost::mutex::scoped_lock lock(mutex_);
		TransTask* transTask = dynamic_cast<TransTask*>(ptrTask.get());
		if ( NULL == transTask )
		{
			return;
		}
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		for (RetryTransTasks::iterator it = retryTasks_.begin(); it != retryTasks_.end(); ++it)
		{
			AsyncTransDetailNode& tmpTransDetailNode = it->node;
			if (transDetailNode->root->group == tmpTransDetailNode->root->group && 
				transDetailNode->source == tmpTransDetailNode->source)
			{
				retryTasks_.erase(it);
				return;
			}
		}
	}

private:
	int32_t init()
	{
		release();

		// init trans task pool
		transTaskPool_.reset(new (std::nothrow)CISSPThreadPool);
		if (NULL == transTaskPool_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}
		AsyncTransTaskPoolConf conf = userContext_->getConfigureMgr()->getConfigure()->asyncTransTaskPoolConf();
		int32_t ret = transTaskPool_->StartupThreadPool(conf.maxThreads, conf.maxTasks);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "start async trans task thread pool failed.");
			return ret;
		}

		// init backup trans pool
		backupTransTaskPool_.reset(new (std::nothrow)CISSPThreadPool);
		if (NULL == backupTransTaskPool_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}
		//ret = backupTransTaskPool_->StartupThreadPool(DEFAULT_BACKUP_POOL_THREADS, DEFAULT_BACKUP_POOL_THREADS);
		ret = backupTransTaskPool_->StartupThreadPool(conf.maxThreads, conf.maxTasks);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "start async backup trans task thread pool failed.");
			return ret;
		}

		// init common transmit notify
		commonTransmitNotify_.reset(new (std::nothrow)CommonTransmitNotify(userContext_, transTableMgr_));
		if (NULL == commonTransmitNotify_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		// init trans task notify
		transTaskNotify_.reset(new (std::nothrow)TransTaskNotify(
			userContext_->getConfigureMgr()->getConfigure()->retryTaskErrorCodes(), this));
		if (NULL == transTaskNotify_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		return ret;
	}

	int32_t release()
	{
		if (NULL != backupTransTaskPool_.get())
		{
			backupTransTaskPool_->InterruptThreadPool();
		}
		if (NULL != transTaskPool_.get())
		{
			transTaskPool_->InterruptThreadPool();
		}
		return RT_OK;
	}

	void schedule()
	{
		try
		{
			bool lastNoTask = false, lastNoBackupTask = false, lastNoNormalTask = false;
			int32_t scheduleMagic = 0;
			AsyncTransType type = ATT_Upload;
			while (true)
			{
				boost::this_thread::interruption_point();

				updateTransSpeed();

				if( scheduleMagic > 20 ) scheduleMagic = 0;

				if (((scheduleMagic++) % 2) != 0)
				{
					// test if the thread pool is idle
					if (backupTransTaskPool_->GetThreadsNumber() <= runningBackupTransTaskNum_)
					{
						continue;
					}
					type = ATT_Backup;
					if (RT_OK != scheduleImpl(backupTransTaskPool_.get(), type, runningBackupTransTaskNum_))
					{
						lastNoBackupTask = true;
					}
				}
				else
				{
					// test if the thread pool is idle
					if (transTaskPool_->GetThreadsNumber() <= runningTransTaskNum_)
					{
						continue;
					}
					type = ATT_Upload;
					if (RT_OK != scheduleImpl(transTaskPool_.get(), type, runningTransTaskNum_))
					{
						lastNoNormalTask = true;
					}
				}

				lastNoTask = (lastNoNormalTask && lastNoBackupTask);

				SLEEP(boost::posix_time::milliseconds(lastNoTask?SCHEDULE_TASK_INTERVAL_LAZY:SCHEDULE_TASK_INTERVAL));
			}
		}
		catch(boost::thread_interrupted&)
		{
			(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_TRANS_SPEED, L"0", L"0"));
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "async trans task schedule interrupted.");
		}
	}

	int32_t scheduleImpl(CISSPThreadPool* threadPool, const AsyncTransType type, uint32_t& transTaskNum)
	{
		if (NULL == threadPool)
		{
			return RT_INVALID_PARAM;
		}

		boost::mutex::scoped_lock lock(mutex_);
		//////////////////////////////////////////////////////////////////////////
		// 1. get root table
		// 2. get root nodes
		// 3. get detail nodes
		// 4. update the root node's status to running
		// 5. update the detail node's status to running
		// 6. push task to thread pool
		//////////////////////////////////////////////////////////////////////////
		uint32_t leftThreads = threadPool->GetThreadsNumber() - transTaskNum;
		// get root table
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root node, task type is %d.", type);
			return RT_ERROR;
		}
		// get root nodes
		AsyncTransRootNodes transRootNodes;
		AsyncTransDetailNodes transDetailNodes;
		int32_t ret = rootTable->getTopNodes(transRootNodes, threadPool->GetThreadsNumber());
		if (RT_OK != ret && RT_SQLITE_NOEXIST != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"get trans task from root table failed, task type is %d.", type);
			return ret;
		}
		// get detail nodes
		ret = getDetailNodes(transDetailNodes, rootTable, transRootNodes, type, leftThreads);
		if (RT_OK != ret)
		{
			return ret;
		}

		if (transDetailNodes.empty())
		{
			// if no backup trans tasks, restore the backup trans tasks
			if (type == ATT_Backup)
			{
				return restoreTransBackupTasks();
			}
			return RT_SQLITE_NOEXIST;
		}

		for (AsyncTransDetailNodes::iterator it = transDetailNodes.begin(); 
			it != transDetailNodes.end(); ++it)
		{
			AsyncTransDetailNode& transDetailNode = *it;
			TransDetailTablePtr detailTable;
			if (transDetailNode->root->fileType != FILE_TYPE_FILE)
			{
				detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
				if (FILE_TYPE_FILE != transDetailNode->root->type && 
					NULL == detailTable.get())
				{
					updateStatusAndErrorCode(transDetailNode, rootTable, detailTable, ATS_Error, RT_ERROR);
					return RT_ERROR;
				}
			}

			// create trans task
			UserContextId userContextId;
			userContextId.id = transDetailNode->root->userId;
			userContextId.type = transDetailNode->root->userType;
			userContextId.name = transDetailNode->root->userName;
			UserContext* userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
			if (NULL == userContext)
			{
				(void)updateStatusAndErrorCode(transDetailNode, rootTable, detailTable, ATS_Error, RT_ERROR);
				return RT_ERROR;
			}
			ITransmitNotifies transmitNotifies;
			transmitNotifies.push_back(commonTransmitNotify_.get());
			ITransmitNotify* notify = userContext_->getAsyncTaskMgr()->getTransmitNotify(transDetailNode->root->type);
			if(NULL != notify)
			{
				transmitNotifies.push_back(notify);
			}
			TransSerializer* serializer = transTableMgr_->getDataTable();
			CISSPTaskPtr transTaskPtr(static_cast<CISSPTask*>(TransTask::create(
				userContext, 
				transDetailNode, 
				serializer, 
				transmitNotifies, 
				transTaskNotify_)));
			if (NULL == transTaskPtr.get())
			{
				(void)updateStatusAndErrorCode(transDetailNode, rootTable, detailTable, ATS_Error, RT_MEMORY_MALLOC_ERROR);
				return RT_MEMORY_MALLOC_ERROR;
			}
			// first, update the root node's status to running
			ret = rootTable->updateStatus(transDetailNode->root->group, ATS_Running);
			if (RT_OK != ret)
			{
				(void)rootTable->updateStatusAndErrorCode(transDetailNode->root->group, ATS_Error, ret);
				return ret;
			}
			// then, update the detail node's status to running
			if (transDetailNode->root->fileType != FILE_TYPE_FILE)
			{
				ret = detailTable->updateStatus(transDetailNode->source, ATS_Running);
				if (RT_OK != ret)
				{
					updateStatusAndErrorCode(transDetailNode, rootTable, detailTable, ATS_Error, ret);
					return ret;
				}
			}
			// last, push the task into thread pool
			++transTaskNum;
			runningTasks_.push_back(transTaskPtr);
			lock.unlock();
			threadPool->PushBackTask(transTaskPtr);
			lock.lock();
		}
		return RT_OK;
	}

	int32_t getDetailNodes(AsyncTransDetailNodes& transDetailNodes, 
		TransRootTable* rootTable, 
		AsyncTransRootNodes& transRootNodes, 
		const AsyncTransType type, 
		const uint32_t leftThreads)
	{
		// get detail nodes from the retry tasks first
		(void)getDetailNodesByRetryTasks(transDetailNodes, transRootNodes, type, leftThreads);
		if (transDetailNodes.size() >= leftThreads)
		{
			return RT_OK;
		}
		// first round, get detail nodes which have not transmit
		int32_t ret = getDetailNodesDefault(transDetailNodes, rootTable, transRootNodes, type, leftThreads);
		if (RT_OK != ret)
		{
			return ret;
		}
		if (transDetailNodes.size() >= leftThreads)
		{
			return RT_OK;
		}
		// second round, get detail nodes by the the running tasks
		ret = getDetailNodesByRunningTasks(transDetailNodes, rootTable, transRootNodes, type, leftThreads);
		if (RT_OK != ret)
		{
			return ret;
		}
		return RT_OK;
	}

	int32_t getDetailNodesByRetryTasks(AsyncTransDetailNodes& transDetailNodes, 
		AsyncTransRootNodes& transRootNodes, 
		const AsyncTransType type, 
		const uint32_t leftThreads)
	{
		if (retryTasks_.empty())
		{
			return RT_SQLITE_NOEXIST;
		}
		RetryTransTask& retryTask = retryTasks_.front();
		if ((GetTickCount() - retryTask.lastRunTime) < 
			(DWORD)userContext_->getConfigureMgr()->getConfigure()->retryTaskInterval())
		{
			return RT_SQLITE_NOEXIST;
		}
		if (((retryTask.node->root->type&ATT_Backup)^(type&ATT_Backup)) != 0)
		{
			return RT_SQLITE_NOEXIST;
		}
		// when retry, the task maybe canceled
		if (retryTask.node->root->status == ATS_Cancel)
		{
			retryTasks_.pop_front();
			return RT_SQLITE_NOEXIST;
		}
		// only one error file can transmit in a group
		TransTask* transTask = NULL;
		for (TransTasks::const_iterator it = runningTasks_.begin(); it != runningTasks_.end(); ++it)
		{
			transTask = dynamic_cast<TransTask*>(it->get());
			AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
			if (transDetailNode->root->group == retryTask.node->root->group)
			{
				return RT_SQLITE_NOEXIST;
			}
		}
		// in following cases, the error task can be scheduled
		// 1. the transRootNodes size is less than leftThreads
		// 2. the error task's root is in transRootNodes, and no other tasks in this root are running
		// 3. the error task's root is not in transRootNodes, but there are some waiting roots in transRootNodes
		bool canSchedule = (transRootNodes.size() < leftThreads);
		if (!canSchedule)
		{
			AsyncTransRootNodes::iterator pos = transRootNodes.end();
			for (AsyncTransRootNodes::iterator it = transRootNodes.begin(); 
				it != transRootNodes.end(); ++it)
			{
				if (retryTask.node->root->group == (*it)->group)
				{
					pos = it;
					canSchedule = true;
					break;
				}
				if ((*it)->status == ATS_Waiting)
				{
					pos = it;
					canSchedule = true;
				}
			}
			if (canSchedule && pos != transRootNodes.end())
			{
				transRootNodes.erase(pos);
			}
		}
		if (!canSchedule)
		{
			return RT_SQLITE_NOEXIST;
		}

		RetryTransTask task = retryTask;
		if (retryTask.node->root->fileType != FILE_TYPE_FILE)
		{
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(retryTask.node->root->group);
			if (detailTable)
			{
				AsyncTransDetailNode transDetailNode;
				int32_t ret = detailTable->getNode(retryTask.node->source, transDetailNode);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "failed to get detail node of %s.", 
						Utility::String::wstring_to_string(retryTask.node->source).c_str());
				}
				else
				{
					// update the node, because the node may be update
					retryTask.node = transDetailNode;
					transDetailNodes.push_back(retryTask.node);
				}
			}
		}
		else
		{
			transDetailNodes.push_back(retryTask.node);
		}
				
		task.lastRunTime = GetTickCount();
		retryTasks_.pop_front();
		retryTasks_.push_back(task);
		return RT_OK;
	}

	int32_t getDetailNodesDefault(AsyncTransDetailNodes& transDetailNodes, 
		TransRootTable* rootTable, 
		AsyncTransRootNodes& transRootNodes, 
		const AsyncTransType type, 
		const uint32_t leftThreads)
	{
		for (AsyncTransRootNodes::iterator it = transRootNodes.begin(); 
			it != transRootNodes.end();)
		{
			if (transDetailNodes.size() >= leftThreads)
			{
				break;
			}
			AsyncTransRootNode transRootNode = *it;
			if (transRootNode->status != ATS_Waiting)
			{
				++it;
				continue;
			}
			it = transRootNodes.erase(it);
			// file
			if (transRootNode->fileType == FILE_TYPE_FILE)
			{
				AsyncTransDetailNode transDetailNode(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == transDetailNode.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				transDetailNode->root = transRootNode;
				transDetailNode->source = transRootNode->source;
				transDetailNode->parent = transRootNode->parent;
				transDetailNode->name = transRootNode->name;
				transDetailNode->fileType = transRootNode->fileType;
				transDetailNode->size = transRootNode->size;
				transDetailNode->status = transRootNode->status;
				transDetailNode->statusEx = transRootNode->statusEx;
				transDetailNodes.push_back(transDetailNode);
			}
			// folder
			else
			{
				TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transRootNode->group);
				if (NULL == detailTable.get())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get detail table of %s.", 
						Utility::String::wstring_to_string(transRootNode->group).c_str());
					return RT_ERROR;
				}
				AsyncTransDetailNodes tmpDetailNodes;
				int32_t ret = detailTable->getTopNodes(tmpDetailNodes);
				if (RT_OK != ret)
				{
					if (RT_SQLITE_NOEXIST != ret)
					{
						HSLOG_ERROR(MODULE_NAME, ret, "failed to get detail nodes from detail table %s.", 
							Utility::String::wstring_to_string(transRootNode->group).c_str());
						return ret;
					}
					// check folder is complete or error
					completeOrErrorRootNode(transRootNode, rootTable, detailTable);
					continue;
				}
				transDetailNodes.push_back(tmpDetailNodes.front());
			}
		}
		return RT_OK;
	}

	int32_t getDetailNodesByRunningTasks(AsyncTransDetailNodes& transDetailNodes, 
		TransRootTable* rootTable, 
		AsyncTransRootNodes& transRootNodes, 
		const AsyncTransType type, 
		const uint32_t leftThreads)
	{
		if (transRootNodes.empty())
		{
			return RT_OK;
		}
		int32_t ret = RT_OK;
		TransTask* transTask = NULL;
		int64_t bigFileSize = userContext_->getConfigureMgr()->getConfigure()->bigFileSize();
		uint32_t count = leftThreads - transDetailNodes.size();
		for (AsyncTransRootNodes::reverse_iterator it = transRootNodes.rbegin(); 
			it != transRootNodes.rend(); ++it)
		{
			const AsyncTransRootNode& transRootNode = *it;
			// check if any file and big file transmit in this root
			bool bigFileFlag = false, transmitFlag = false;
			for (TransTasks::iterator itor = runningTasks_.begin(); itor != runningTasks_.end(); ++itor)
			{
				transTask = static_cast<TransTask*>(itor->get());
				AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
				if (transDetailNode->root->group == transRootNode->group)
				{
					transmitFlag = true;
					if (transDetailNode->size >= bigFileSize)
					{
						bigFileFlag = true;
						break;
					}
				}
			}
			
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transRootNode->group);
			if (NULL == detailTable.get())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get detail table of %s.", 
					Utility::String::wstring_to_string(transRootNode->group).c_str());
				return RT_ERROR;
			}
			AsyncTransDetailNodes tmpDetailNodes;
			if (it != (--transRootNodes.rend()))
			{
				if (!transmitFlag)
				{
					ret = detailTable->getTopNodes(tmpDetailNodes, 0);
					if (RT_OK == ret)
					{
						--count;
					}
				}
			}
			else
			{
				if (count > 0)
				{
					ret = detailTable->getTopNodes(tmpDetailNodes, bigFileFlag?bigFileSize:0, count);
					if (RT_OK == ret && !bigFileFlag)
					{
						for (AsyncTransDetailNodes::iterator itor = tmpDetailNodes.begin(); 
							itor != tmpDetailNodes.end();)
						{
							if ((*itor)->size >= bigFileSize)
							{
								if (bigFileFlag)
								{
									itor = tmpDetailNodes.erase(itor);
									continue;
								}
								bigFileFlag = true;
							}
							++itor;
						}
					}
				}
			}
			if (RT_OK != ret)
			{
				if (RT_SQLITE_NOEXIST != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "failed to get detail nodes from detail table %s.", 
						Utility::String::wstring_to_string(transRootNode->group).c_str());
					return ret;
				}
				// check folder is complete or error
				completeOrErrorRootNode(transRootNode, rootTable, detailTable);
				continue;
			}
			if (!tmpDetailNodes.empty())
			{
				transDetailNodes.splice(transDetailNodes.end(), tmpDetailNodes);
				if (transDetailNodes.size() >= leftThreads)
				{
					break;
				}
			}
		}
		return RT_OK;
	}

	void completeOrErrorRootNode(const AsyncTransRootNode& transRootNode, 
		TransRootTable* rootTable, 
		TransDetailTablePtr detailTable)
	{
		// the tasks is adding...
		if ((transRootNode->statusEx&ATSEX_AddingTasks) != 0)
		{
			return;
		}
		// scanner may not add tasks as soon as possible, wait a moment
		// check if any node in detail table
		// if not, means task has transmit completed
		// so delete the root node and the detail table
		if (0 == detailTable->getNodesCount(AsyncTransStatus(~0)))
		{
			if (transRootNode->type == ATT_Upload || transRootNode->type == ATT_Download)
			{
				AsyncTransCompleteNode completeNode(new st_AsyncTransCompleteNode);
				completeNode->group = transRootNode->group;
				completeNode->source = transRootNode->source;
				completeNode->parent = transRootNode->parent;
				completeNode->name = transRootNode->name;
				completeNode->type = transRootNode->type;
				completeNode->fileType = transRootNode->fileType;
				completeNode->userId = transRootNode->userId;
				completeNode->userType = transRootNode->userType;
				completeNode->userName = transRootNode->userName;
				completeNode->size = transRootNode->size;
				(void)transTableMgr_->getCompleteTable()->addNode(completeNode);
				// notify complete message to UI
				(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
					NOTIFY_MSG_TRANS_TASK_COMPLETE, 
					transRootNode->group, 
					Utility::String::type_to_string<std::wstring>(transRootNode->type)));
			}
			(void)rootTable->deleteNode(transRootNode->group);
			// database file is locked, no need to remove detail table (it's always failed)
			(void)transTableMgr_->removeDetailTable(transRootNode->group);
			return;
		}
		if (detailTable->isError())
		{
			(void)rootTable->updateStatusAndErrorCode(transRootNode->group, ATS_Error, RT_ERROR);
			(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
				NOTIFY_MSG_TRANS_TASK_ERROR, 
				transRootNode->group, 
				Utility::String::type_to_string<std::wstring>(transRootNode->type)));
		}
	}

	int32_t updateStatusAndErrorCode(const AsyncTransDetailNode& transDetailNode, 
		TransRootTable* rootTable, 
		TransDetailTablePtr detailTable, 
		const AsyncTransStatus status, 
		const int32_t errorCode = RT_OK)
	{
		int32_t ret = RT_OK;
		if (transDetailNode->root->fileType == FILE_TYPE_FILE)
		{
			if (status == ATS_Error)
			{
				ret = rootTable->updateStatusAndErrorCode(transDetailNode->root->group, status, errorCode);
			}
			else
			{
				ret = rootTable->updateStatus(transDetailNode->root->group, status);
			}
		}
		else
		{
			// in multi thread case, the detail table maybe null
			// like the root node is deleted when schedule ...
			if (NULL == detailTable.get())
			{
				return rootTable->updateStatus(transDetailNode->root->group, ATS_Error);
			}
			if (status == ATS_Error)
			{
				ret = detailTable->updateStatusAndErrorCode(transDetailNode->source, status, errorCode);
			}
			else
			{
				ret = detailTable->updateStatus(transDetailNode->source, status);
			}
		}
		return ret;
	}

	int32_t restoreTransBackupTasks()
	{
		TransRootTable *rootTable = transTableMgr_->getRootTable(ATT_Backup);
		if (NULL == rootTable)
		{
			return RT_INVALID_PARAM;
		}
		AsyncTransRootNodes transRootNodes;
		if (RT_OK != rootTable->getTopNodes(transRootNodes, 1))
		{
			return RT_CONTINUE;
		}
		// check if any backup tasks is running
		if (runningBackupTransTaskNum_ > 0)
		{
			return RT_CONTINUE;
		}
		const AsyncTransRootNode& transRootNode = transRootNodes.front();
		if (!transRootNode)
		{
			return RT_INVALID_PARAM;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transRootNode->group);
		if (!detailTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get detail table of %s.", 
				Utility::String::wstring_to_string(transRootNode->group).c_str());
			return RT_ERROR;
		}
		return detailTable->restoreVirtualParent();
	}

	void updateTransSpeed()
	{
		static uint32_t lastTick = GetTickCount();
		static int64_t lastUploadSpeed = 0, lastDownloadSpeed = 0;
		static int32_t idleCount = 0;
		uint32_t tick = GetTickCount();
		if (tick < lastTick)
		{
			lastTick = tick;
			return;
		}
		if ((tick-lastTick) >= 1000)
		{
			lastTick = tick;
			// keep the credential information valid here
			//userContext_->getCredentialMgr()->updateCredentialInfo();

			int64_t uploadSpeed = userContext_->getNetworkMgr()->getUploadSpeed();
			int64_t downloadSpeed = userContext_->getNetworkMgr()->getDownloadSpeed();
			if (lastUploadSpeed == uploadSpeed && lastDownloadSpeed == downloadSpeed)
			{
				if (++idleCount < 30)
				{
					return;
				}
				idleCount = 0;
			}
			lastUploadSpeed = uploadSpeed;
			lastDownloadSpeed = downloadSpeed;
			(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_TRANS_SPEED, 
				Utility::String::type_to_string<std::wstring>(uploadSpeed), 
				Utility::String::type_to_string<std::wstring>(downloadSpeed)));
		}		
	}

private:
	UserContext* userContext_;
	TransTableMgr* transTableMgr_;
	boost::thread thread_;
	std::auto_ptr<CISSPThreadPool> transTaskPool_;
	std::auto_ptr<CISSPThreadPool> backupTransTaskPool_;
	CISSPNotifyPtr transTaskNotify_;
	TransTasks runningTasks_;
	RetryTransTasks retryTasks_;
	uint32_t runningTransTaskNum_;
	uint32_t runningBackupTransTaskNum_;
	std::auto_ptr<ITransmitNotify> commonTransmitNotify_;
	boost::mutex mutex_;
};

TransTaskScheduler::TransTaskScheduler(UserContext* userContext, TransTableMgr* transTableMgr)
	:impl_(new Impl(userContext, transTableMgr))
{
	
}

int32_t TransTaskScheduler::run()
{
	return impl_->run();
}

int32_t TransTaskScheduler::interrupt()
{
	return impl_->interrupt();
}

int32_t TransTaskScheduler::interruptRunningTask(const std::wstring& group, bool async)
{
	return impl_->interruptRunningTask(group, async);
}

int32_t TransTaskScheduler::interruptRunningTask(const AsyncTransType type, bool async)
{
	return impl_->interruptRunningTask(type, async);
}

int32_t TransTaskScheduler::interruptRunningTask(const std::wstring& group, const std::wstring& source)
{
	return impl_->interruptRunningTask(group, source);
}
