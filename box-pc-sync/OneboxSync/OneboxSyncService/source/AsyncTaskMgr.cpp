#include "AsyncTaskMgr.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "NotifyMgr.h"
#include "NetworkMgr.h"
#include "DataBaseMgr.h"
#include "SyncFileSystemMgr.h"
#include "FilterMgr.h"
#include "GlobalVariable.h"
#include "UserInfoMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("AsyncTaskMgr")
#endif

#ifndef MAX_TRANS_TASK_WAIT_NUM
#define MAX_TRANS_TASK_WAIT_NUM (50)
#endif

#define SCHEDULE_TASK_INTERVAL_LAZY 200
#define SCHEDULE_TASK_INTERVAL 1

class AsyncTaskMgrImpl;

class TransTaskNotify : public CISSPNotify
{
public:
	TransTaskNotify(AsyncTaskMgrImpl* parent, UserContext* userContext);

	virtual ~TransTaskNotify();

	// task go to 'queued' status
	virtual void TaskQueueNotify(const CISSPTaskPtr ptrTask);

	// task go to 'runing' status
	virtual void TaskRunningNotify(const CISSPTaskPtr ptrTask);

	// task go to 'complete' status
	virtual void TaskCompleteNotify(const CISSPTaskPtr ptrTask);

	// custom notify
	virtual void TaskCustomNotiry(const CISSPTaskPtr ptrTask);

private:
	UserContext* userContext_;
	AsyncTaskMgrImpl* parent_;
};

class AsyncTaskMgrImpl : public AsyncTaskMgr
{
private:
	UserContext* userContext_;
	CISSPThreadPool *asyncTransTaskPool_;
	callback_type callback_[ATT_Invalid];
	CISSPNotifyPtr notify_;
	std::list<CISSPTaskPtr> tasks_;
	boost::thread scheduleThread_;
	boost::mutex mutex_;

private:
	int32_t init()
	{
		notify_ = CISSPNotifyPtr(static_cast<CISSPNotify*>(new TransTaskNotify(this, userContext_)));
		if (NULL == notify_.get())
		{
			return RT_INVALID_PARAM;
		}

		asyncTransTaskPool_ = new CISSPThreadPool;
		AsyncTransTaskPoolConf conf = userContext_->getConfigureMgr()->getConfigure()->asyncTransTaskPoolConf();
		int32_t ret = asyncTransTaskPool_->StartupThreadPool(conf.maxThreads, conf.maxTasks);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "start async trans task thread pool failed.");
			return ret;
		}

		(void)addCallback(boost::bind(&AsyncTaskMgrImpl::manualAsyncTaskCallback, this, _1, _2), ATT_Upload_Manual);
		(void)addCallback(boost::bind(&AsyncTaskMgrImpl::manualAsyncTaskCallback, this, _1, _2), ATT_Download_Manual);
		(void)addCallback(boost::bind(&AsyncTaskMgrImpl::attachementsAsyncTaskCallback, this, _1, _2), ATT_Upload_Attachements);

		userContext_->getGlobalVariable()->globalHandler()->addHandler(boost::bind(&AsyncTaskMgrImpl::updateTransSpeed, this));

		return RT_OK;
	}

	int32_t release()
	{
		stop();

		if (NULL != asyncTransTaskPool_)
		{
			asyncTransTaskPool_->InterruptThreadPool();
			delete asyncTransTaskPool_;
			asyncTransTaskPool_ = NULL;
		}

		return RT_OK;
	}

	void updateTransSpeed()
	{
		userContext_->getNotifyMgr()->notify(
			NOTIFY_PARAM(NOTIFY_MSG_SPEED, 
			Utility::String::format_string(L"%I64d", userContext_->getNetworkMgr()->getUploadSpeed()), 
			Utility::String::format_string(L"%I64d", userContext_->getNetworkMgr()->getDownloadSpeed())));
	}

	void schedule()
	{
		try
		{
			bool lastNoTask = false;
			while (true)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(lastNoTask?SCHEDULE_TASK_INTERVAL_LAZY:SCHEDULE_TASK_INTERVAL));

				userContext_->getGlobalVariable()->globalHandler()->invok();

				if (asyncTransTaskPool_->IsIdle())
				{
					TransTaskTable* transTaskTable = userContext_->getDataBaseMgr()->getTransTaskTable();
					assert(NULL != transTaskTable);

					std::shared_ptr<AsyncTransTaskNode> transTaskNode(new AsyncTransTaskNode);
					int32_t ret = transTaskTable->getTopNode(*transTaskNode.get());
					if (RT_OK != ret)
					{
						if(RT_SQLITE_NOEXIST != ret)
						{
							HSLOG_ERROR(MODULE_NAME, ret, "get trans task from database failed.");
						}

						lastNoTask = true;

						continue;
					}
					lastNoTask = false;

					CISSPTaskPtr transTaskPtr(static_cast<CISSPTask*>(TransTask::create(userContext_, transTaskNode, notify_)));

					if (RT_OK != transTaskTable->updateStatus(AsyncTaskId(transTaskNode->id), ATS_Running))
					{
						continue;
					}
					if (!asyncTransTaskPool_->PushBackTaskFast(transTaskPtr))
					{
						transTaskTable->updateStatus(AsyncTaskId(transTaskNode->id), ATS_Waiting);
						HSLOG_ERROR(MODULE_NAME, transTaskPtr->GetErrorCode(), "push trans task to thread pool failed.");
						continue;
					}

					// add the task to the list for user operation (cancel, delete etc...)
					{
						boost::mutex::scoped_lock lock(mutex_);
						tasks_.push_back(transTaskPtr);
					}
				}
			}
		}
		catch(boost::thread_interrupted)
		{
			// when thread interrupt, should update the speed to zero
			userContext_->getNotifyMgr()->notify(
				NOTIFY_PARAM(NOTIFY_MSG_SPEED, 
				Utility::String::format_string(L"%d", 0), 
				Utility::String::format_string(L"%d", 0)));

			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "async trans task schedule interrupted.");
		}
	}

public:
	AsyncTaskMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		for (int32_t i = 0; i < ATT_Invalid; ++i)
		{
			callback_[i] = NULL;
		}
		init();
	}

	virtual ~AsyncTaskMgrImpl()
	{
		release();
	}

	virtual int32_t addCallback(callback_type callback, AsyncTaskType type)
	{
		callback_[type] = callback;		
		return RT_OK;
	}

	virtual int32_t start()
	{
		(void)stop();
		(void)resumeAllTask();
		scheduleThread_ = boost::thread(boost::bind(&AsyncTaskMgrImpl::schedule, this));

		return RT_OK;
	}

	virtual int32_t stop()
	{
		scheduleThread_.interrupt();
		scheduleThread_.join();

		asyncTransTaskPool_->CancelAllTask();
		asyncTransTaskPool_->WaitAllTaskFinish();
		
		return RT_OK;
	}

	virtual bool isIdle()
	{
		return  (MAX_TRANS_TASK_WAIT_NUM >= 
			userContext_->getDataBaseMgr()->getTransTaskTable()->getCount(ATS_Waiting));
	}

	virtual int32_t upload(const Path& localPath, const Path& remoteParent)
	{
		if (INVALID_ID == localPath.id() || INVALID_ID == remoteParent.id())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"upload file %I64d:%s failed, invalid param.", localPath.id(), 
				Utility::String::wstring_to_string(localPath.name()).c_str());
			return RT_INVALID_PARAM;
		}

		HSLOG_TRACE(MODULE_NAME, RT_OK, 
			"upload file %I64d:%s.", localPath.id(), 
			Utility::String::wstring_to_string(localPath.name()).c_str());
	
		// the group id is empty
		AsyncTaskId id(Utility::String::type_to_string<std::wstring>(localPath.id()), L"", ATT_Upload);
		AsyncTransTaskNode transTaskNode(id, 
			Utility::String::type_to_string<std::wstring>(remoteParent.id()), 
			localPath.name(), 
			Utility::String::type_to_string<std::wstring>(userContext_->getUserInfoMgr()->getUserId()));

		int32_t ret = userContext_->getDataBaseMgr()->getTransTaskTable()->addNode(transTaskNode);
		if (RT_SQLITE_EXIST == ret)
		{
			ret = resumeTask(transTaskNode);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"upload file %I64d:%s failed, can not add to database.", localPath.id(), 
				Utility::String::wstring_to_string(localPath.name()).c_str());
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t upload(const std::wstring& localPath, const int64_t& remoteParent, const int64_t& owner_id, const std::wstring& groupId)
	{
		if (localPath.empty() || INVALID_ID == remoteParent)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"upload file %s:%I64d failed, invalid param.", 
				Utility::String::wstring_to_string(localPath).c_str(), 
				remoteParent);
			return RT_INVALID_PARAM;
		}

		if (userContext_->getFilterMgr()->isStaticFilter(localPath) || userContext_->getFilterMgr()->isKiaFilter(localPath))
		{
			HSLOG_ERROR(MODULE_NAME, RT_OK, "file/ folder %s is filterd.", 
				Utility::String::wstring_to_string(localPath).c_str());
			return RT_OK;
		}

		HSLOG_TRACE(MODULE_NAME, RT_OK, 
			"upload file %s:%I64d.", 
			Utility::String::wstring_to_string(localPath).c_str(), 
			remoteParent);

		// the group id is a GUID
		AsyncTaskId id(localPath, groupId, ATT_Upload_Manual);
		AsyncTransTaskNode transTaskNode(id, 
			Utility::String::type_to_string<std::wstring>(remoteParent), 
			Utility::FS::get_file_name(localPath), 
			Utility::String::type_to_string<std::wstring>(owner_id));

		// set the priority for the task
		transTaskNode.priority = PRIORITY_LEVEL3;
		int32_t ret = userContext_->getDataBaseMgr()->getTransTaskTable()->addNode(transTaskNode);
		if (RT_SQLITE_EXIST == ret)
		{
			ret = resumeTask(transTaskNode);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"upload file %s:%I64d failed, can not add to database.", 
				Utility::String::wstring_to_string(localPath).c_str(), 
				remoteParent);
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t upload(const std::wstring& localPath, const std::wstring& remoteParent, const std::wstring& groupId)
	{
		if (localPath.empty() || remoteParent.empty() || groupId.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"upload file %s:%s failed, invalid param.", 
				Utility::String::wstring_to_string(localPath).c_str(), 
				Utility::String::wstring_to_string(remoteParent).c_str());
			return RT_INVALID_PARAM;
		}
		if (userContext_->getFilterMgr()->isStaticFilter(localPath) || userContext_->getFilterMgr()->isKiaFilter(localPath))
		{
			HSLOG_ERROR(MODULE_NAME, RT_OK, "file/ folder %s is filterd.", 
				Utility::String::wstring_to_string(localPath).c_str());
			return RT_OK;
		}

		HSLOG_TRACE(MODULE_NAME, RT_OK, 
			"upload file %s:%s.", 
			Utility::String::wstring_to_string(localPath).c_str(), 
			Utility::String::wstring_to_string(remoteParent).c_str());

		// the group id is a GUID
		AsyncTaskId id(localPath, groupId, ATT_Upload_Attachements);
		AsyncTransTaskNode transTaskNode(id, remoteParent, 
			Utility::FS::get_file_name(localPath), 
			Utility::String::type_to_string<std::wstring>(userContext_->getUserInfoMgr()->getUserId()));

		// set the priority for the task
		transTaskNode.priority = PRIORITY_LEVEL2;
		int32_t ret = userContext_->getDataBaseMgr()->getTransTaskTable()->addNode(transTaskNode);
		if (RT_SQLITE_EXIST == ret)
		{
			ret = resumeTask(transTaskNode);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"upload file %s:%s failed, can not add to database.", 
				Utility::String::wstring_to_string(localPath).c_str(), 
				Utility::String::wstring_to_string(remoteParent).c_str());
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t download(const Path& remotePath, const Path& localParent)
	{
		if (INVALID_ID == remotePath.id() || INVALID_ID == localParent.id())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"download file %I64d:%s failed, invalid param.", remotePath.id(), 
				Utility::String::wstring_to_string(remotePath.name()).c_str());
			return RT_INVALID_PARAM;
		}

		HSLOG_TRACE(MODULE_NAME, RT_OK, 
			"download file %I64d:%s.", remotePath.id(), 
			Utility::String::wstring_to_string(remotePath.name()).c_str());

		AsyncTaskId id(Utility::String::type_to_string<std::wstring>(remotePath.id()), L"", ATT_Download);
		AsyncTransTaskNode transTaskNode(id, 
			Utility::String::type_to_string<std::wstring>(localParent.id()), 
			remotePath.name(), 
			Utility::String::type_to_string<std::wstring>(userContext_->getUserInfoMgr()->getUserId()));

		int32_t ret = userContext_->getDataBaseMgr()->getTransTaskTable()->addNode(transTaskNode);
		if (RT_SQLITE_EXIST == ret)
		{
			ret = resumeTask(transTaskNode);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"download file %I64d:%s failed, can not add to database.", remotePath.id(), 
				Utility::String::wstring_to_string(remotePath.name()).c_str());
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t cancelTask(const AsyncTaskId& taskId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		TransTask* transTask = NULL;
		for (std::list<CISSPTaskPtr>::iterator it = tasks_.begin(); it != tasks_.end(); ++it)
		{
			transTask = dynamic_cast<TransTask*>((*it).get());
			if (transTask->getTaskNode().id.id == taskId.id && 
				transTask->getTaskNode().id.group == taskId.group && 
				transTask->getTaskNode().id.type == taskId.type)
			{
				transTask->CancelTask();
				//transTask->WaitCompleted();
				tasks_.erase(it);
				return RT_OK;
			}
		}

		HSLOG_EVENT(MODULE_NAME, RT_ERROR, "task is not running, task id: %s.", 
			Utility::String::wstring_to_string(taskId.id).c_str());

		return RT_OK;
	}

	virtual int32_t cancelTask(const AsyncTaskIds& taskIds)
	{
		int32_t ret = RT_OK;
		for (AsyncTaskIds::const_iterator it = taskIds.begin(); it != taskIds.end(); ++it)
		{
			(void)cancelTask(*it);
		}
		return ret;
	}

	virtual int32_t delTask(const AsyncTaskId& taskId)
	{
		(void)cancelTask(taskId);

		return userContext_->getDataBaseMgr()->getTransTaskTable()->deleteNode(taskId);
	}

	virtual int32_t delTask(const AsyncTaskIds& taskIds)
	{
		int32_t ret = RT_OK;

		(void)cancelTask(taskIds);

		CHECK_RESULT(userContext_->getDataBaseMgr()->getTransTaskTable()->deleteNodes(taskIds));

		// task type of ATT_Download should delete the cache data
		for (AsyncTaskIds::const_iterator it = taskIds.begin(); it != taskIds.end(); ++it)
		{
			if (ATT_Download == it->type && it->group.empty())
			{
				(void)Utility::FS::remove(userContext_->getConfigureMgr()->getConfigure()->cacheDataPath() + PATH_DELIMITER + it->id);
			}
		}
		return ret;
	}

	virtual int32_t resumeTask(const AsyncTaskId& taskId)
	{
		AsyncTransTaskNode taskNode;
		int32_t ret = RT_ERROR;
		CHECK_RESULT(userContext_->getDataBaseMgr()->getTransTaskTable()->getNode(taskId, taskNode));
		return resumeTask(taskNode);
	}

	virtual int32_t resumeTask(const AsyncTaskIds& taskIds)
	{
		int32_t ret = RT_OK;
		for (AsyncTaskIds::const_iterator it = taskIds.begin(); it != taskIds.end(); ++it)
		{
			if (RT_OK != resumeTask(*it))
			{
				ret = RT_ERROR;
			}
		}
		return ret;
	}

	int32_t resumeTask(const AsyncTransTaskNode& taskNode)
	{
		int32_t ret = RT_OK;
		TransTaskTable* transTaskTable = userContext_->getDataBaseMgr()->getTransTaskTable();
		assert(NULL != transTaskTable);

		AsyncTransTaskNode tempNode;
		CHECK_RESULT(transTaskTable->getNode(taskNode.id, tempNode));

		// if the task exist and the name or parent is changed, 
		// 1. should update the task information 
		// 2. interrupt the task 
		// 3. update the task status to ATS_Waiting
		if (tempNode.name != taskNode.name || tempNode.parent != taskNode.parent)
		{
			tempNode.name = taskNode.name;
			tempNode.parent = taskNode.parent;
			CHECK_RESULT(transTaskTable->updateNode(tempNode));

			if (ATS_Running == tempNode.status)
			{
				return cancelTask(taskNode.id);
			}

			CHECK_RESULT(transTaskTable->updateStatus(taskNode.id, ATS_Waiting));

			return RT_OK;
		}

		if (ATS_Running == tempNode.status)
		{
			return RT_OK;
		}		

		return transTaskTable->updateStatus(taskNode.id, ATS_Waiting);
	}

	virtual int32_t resumeAllTask()
	{
		return userContext_->getDataBaseMgr()->getTransTaskTable()->updateStatus(ATS_Running, ATS_Waiting);
	}

	virtual int32_t delAllTask()
	{
		AsyncTransTaskNodes asyncTransTaskNodes;
		(void)userContext_->getDataBaseMgr()->getTransTaskTable()->getNodes(ATT_Download, asyncTransTaskNodes);
		// task type of ATT_Download should delete the cache data
		for (AsyncTransTaskNodes::const_iterator it = asyncTransTaskNodes.begin(); it != asyncTransTaskNodes.end(); ++it)
		{
			if (ATT_Download == it->id.type && it->id.group.empty())
			{
				(void)Utility::FS::remove(userContext_->getConfigureMgr()->getConfigure()->cacheDataPath() + PATH_DELIMITER + it->id.id);
			}
		}
		(void)userContext_->getDataBaseMgr()->getTransTaskTable()->getNodes(ATT_Upload, asyncTransTaskNodes);
		
		AsyncTaskIds taskIds;
		for (AsyncTransTaskNodes::const_iterator it = asyncTransTaskNodes.begin(); it != asyncTransTaskNodes.end(); ++it)
		{
			taskIds.push_back(AsyncTaskId(it->id.id, it->id.group, it->id.type));
		}
		return userContext_->getDataBaseMgr()->getTransTaskTable()->deleteNodes(taskIds);
	}

	virtual int32_t getTransSize(int64_t& downloadSize, int64_t& uploadSize)
	{
		boost::mutex::scoped_lock lock(mutex_);
		TransTask* asyncTask = NULL;
		if(tasks_.empty())
		{
			return RT_FILE_NOEXIST_ERROR;
		}
		for (std::list<CISSPTaskPtr>::iterator it = tasks_.begin(); it != tasks_.end(); ++it)
		{
			asyncTask = dynamic_cast<TransTask*>(it->get());
			assert(NULL != asyncTask);
			if(Key_LocalID==asyncTask->getTaskNode().id.type)
			{
				uploadSize += asyncTask->getTransLen();
			}
			else
			{
				downloadSize += asyncTask->getTransLen();
			}
		}
		return RT_OK;
	}

	callback_type getCallback(AsyncTaskType type)
	{
		return callback_[type];
	}

	int32_t manualAsyncTaskCallback(CISSPTaskPtr ptrTask, CallbackType type)
	{
		if(AsyncTaskMgr::CT_UpdateNode == type)
		{
			TransTask* asyncTransTask = dynamic_cast<TransTask*>(ptrTask.get());
			assert(NULL != asyncTransTask);

			AsyncTransTaskNode& transTaskNode = asyncTransTask->getTaskNode();

			if (!asyncTransTask->IsError() && !asyncTransTask->IsCancel())
			{
				if (Utility::FS::is_directory(transTaskNode.id.id))
				{
					// add children node
					Path localPath;
					localPath.path(transTaskNode.id.id);
					LIST_FOLDER_RESULT result;
					int32_t ret = userContext_->getSyncFileSystemMgr()->listFolder(localPath, result, ADAPTER_FOLDER_TYPE_LOCAL);
					if (RT_OK != ret)
					{
						HSLOG_ERROR(MODULE_NAME, ret, "list folder of %s failed.", 
							Utility::String::wstring_to_string(transTaskNode.id.id).c_str());
						return ret;
					}

					FILE_DIR_INFO* fileDirInfo = static_cast<FILE_DIR_INFO*>(asyncTransTask->getCustomInfo().content);
					std::wstring temp = L"";
					for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
					{
						temp = transTaskNode.id.id + PATH_DELIMITER +it->name;
						ret = upload(temp, fileDirInfo->id, Utility::String::string_to_type<int64_t>(transTaskNode.ownerId),
							transTaskNode.id.group);
						if (RT_OK != ret)
						{
							HSLOG_ERROR(MODULE_NAME, ret, "add child node of %s failed.", 
								Utility::String::wstring_to_string(temp).c_str());
							continue;
						}
					}
				}
			}
		}
		return RT_OK;
	}

	int32_t attachementsAsyncTaskCallback(CISSPTaskPtr ptrTask, CallbackType type)
	{
		if(AsyncTaskMgr::CT_UpdateNode == type)
		{
			TransTask* asyncTransTask = dynamic_cast<TransTask*>(ptrTask.get());
			assert(NULL != asyncTransTask);

			AsyncTransTaskNode& transTaskNode = asyncTransTask->getTaskNode();

			if (!asyncTransTask->IsError() && !asyncTransTask->IsCancel())
			{
				CustomNotifyType customType = (CustomNotifyType)asyncTransTask->getCustomInfo().type;
				FILE_DIR_INFO* fileDirInfo = static_cast<FILE_DIR_INFO*>(asyncTransTask->getCustomInfo().content);
				if (FolderInfo == customType)
				{
					// convert the parent to id information (the old parent is the path information)
					transTaskNode.parent = Utility::String::type_to_string<std::wstring>(fileDirInfo->id);
				}
				else if (FileInfo == customType)
				{
					// fill the userDefine with the file id information
					transTaskNode.userDefine = Utility::String::type_to_string<std::wstring>(fileDirInfo->id);
					return userContext_->getDataBaseMgr()->getTransTaskTable()->updateNode(transTaskNode);
				}				
			}
		}
		return RT_OK;
	}
};

AsyncTaskMgr* AsyncTaskMgr::create(UserContext* userContext)
{
	return static_cast<AsyncTaskMgr*>(new AsyncTaskMgrImpl(userContext));
}

TransTaskNotify::TransTaskNotify(AsyncTaskMgrImpl* parent, UserContext* userContext)
	:parent_(parent)
	,userContext_(userContext)
{

}

TransTaskNotify::~TransTaskNotify()
{

}

// task go to 'queued' status
void TransTaskNotify::TaskQueueNotify(const CISSPTaskPtr ptrTask)
{

}

// task go to 'runing' status
void TransTaskNotify::TaskRunningNotify(const CISSPTaskPtr ptrTask)
{

}

// task go to 'complete' status
void TransTaskNotify::TaskCompleteNotify(const CISSPTaskPtr ptrTask)
{
	TransTask* asyncTransTask = dynamic_cast<TransTask*>(ptrTask.get());
	assert(NULL != asyncTransTask);
	
	AsyncTransTaskNode& transTaskNode = asyncTransTask->getTaskNode();

	if (NULL != parent_->getCallback(transTaskNode.id.type))
	{
		parent_->getCallback(transTaskNode.id.type)(ptrTask, AsyncTaskMgr::CT_TaskComplete);
	}

	if (asyncTransTask->IsCompletedWithSuccess())
	{
		// upload attachements should not delete the task node from database
		if (ATT_Upload_Attachements == transTaskNode.id.type)
		{
			(void)userContext_->getDataBaseMgr()->getTransTaskTable()->updateStatus(transTaskNode.id, ATS_Complete);
		}
		else
		{
			parent_->delTask(transTaskNode.id);
		}

		// update UI process
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_UPDATE, 
			transTaskNode.id.id, 
			transTaskNode.id.group, 
			Utility::String::type_to_string<std::wstring>(transTaskNode.id.type),
			L"1.00"));
	}
	else if (asyncTransTask->IsCompletedWithCancel())
	{
		parent_->cancelTask(transTaskNode.id);

		userContext_->getDataBaseMgr()->getTransTaskTable()->updateStatus(transTaskNode.id, ATS_Cancel);

		// update UI to delete task
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_DELETE, 
			transTaskNode.id.id, 
			transTaskNode.id.group, 
			Utility::String::type_to_string<std::wstring>(transTaskNode.id.type)));	
	}
	else if (asyncTransTask->IsCompletedWithError())
	{
		parent_->cancelTask(transTaskNode.id);

		// only if error occur update the priority
		(void)userContext_->getDataBaseMgr()->getTransTaskTable()->updatePriority(transTaskNode.id);

		(void)userContext_->getDataBaseMgr()->getTransTaskTable()->updateStatus(transTaskNode.id, ATS_Error);

		// update UI to delete task
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_DELETE, 
			transTaskNode.id.id, 
			transTaskNode.id.group, 
			Utility::String::type_to_string<std::wstring>(transTaskNode.id.type)));			
	}
}

// custom notify
void TransTaskNotify::TaskCustomNotiry(const CISSPTaskPtr ptrTask)
{
	TransTask* asyncTransTask = dynamic_cast<TransTask*>(ptrTask.get());
	assert(NULL != asyncTransTask);

	AsyncTransTaskNode& transTaskNode = asyncTransTask->getTaskNode();

	if (NULL != parent_->getCallback(transTaskNode.id.type))
	{
		asyncTransTask->SetErrorCode(
			parent_->getCallback(transTaskNode.id.type)(ptrTask, AsyncTaskMgr::CT_UpdateNode));
	}
}
