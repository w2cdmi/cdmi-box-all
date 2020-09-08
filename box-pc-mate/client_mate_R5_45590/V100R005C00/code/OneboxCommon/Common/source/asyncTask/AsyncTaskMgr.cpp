#include "AsyncTaskMgr.h"
#include "TransTask.h"
#include "TransTableMgr.h"
#include "TransTaskScheduler.h"
#include "TransTaskScanner.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "UserContextMgr.h"
#include "OutlookTransmitNotify.h"
#include "WorkModeMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("AsyncTaskMgr")
#endif

#ifndef TRANS_TABLE_ROOT_DIR_NAME
#define TRANS_TABLE_ROOT_DIR_NAME (L"TransTaskTables")
#endif

typedef std::map<AsyncTransType, std::auto_ptr<ITransmitNotify>> TransmitNotifies;

class AsyncTaskMgrImpl : public AsyncTaskMgr, public IWorkModeChangeNotify
{
private:
	boost::mutex mutex_;
	bool init_;
	UserContext* userContext_;
	TransmitNotifies transmitNotifies_;
	std::auto_ptr<TransTableMgr> transTableMgr_;
	std::auto_ptr<TransTaskScanner> scanner_;
	std::auto_ptr<TransTaskScheduler> scheduler_;

	// outlook
	std::auto_ptr<OutlookTable> outlookTable_;

public:
	AsyncTaskMgrImpl(UserContext* userContext)
		:userContext_(userContext)
		,init_(false)
	{
	}

	virtual ~AsyncTaskMgrImpl()
	{
		try
		{
			release();
		}
		catch(...) {}
	}

	virtual int32_t changeWorkMode(const WorkMode& mode)
	{
		boost::mutex::scoped_lock lock(mutex_);

		if (mode == WorkMode_Online)
		{
			if (NULL == scanner_.get() || NULL == scheduler_.get())
			{
				return RT_ERROR;
			}
			if (RT_OK != scanner_->run())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "start scanner failed.");
				return RT_ERROR;
			}
			if (RT_OK != scheduler_->run())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "start scheduler failed.");
				return RT_ERROR;
			}
			return RT_OK;
		}
		else if (mode == WorkMode_Offline)
		{
			if (NULL != scanner_.get())
			{
				(void)scanner_->interrupt();
			}
			if (NULL != scheduler_.get())
			{
				(void)scheduler_->interrupt();
			}
			return RT_OK;
		}
		return RT_INVALID_PARAM;
	}

	virtual int32_t init()
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (init_)
		{
			return RT_OK;
		}

		// init database
		std::wstring strUserId = Utility::String::type_to_string<std::wstring>(userContext_->id.id);
		std::wstring transTableRootPath = userContext_->getConfigureMgr()->getConfigure()->appUserDataPath() + 
			PATH_DELIMITER + strUserId + PATH_DELIMITER + 
			TRANS_TABLE_ROOT_DIR_NAME;

		transTableMgr_.reset(new (std::nothrow)TransTableMgr(userContext_, transTableRootPath));
		if (NULL == transTableMgr_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		// init outlook database		
		std::wstring outlookTablePath = userContext_->getConfigureMgr()->getConfigure()->appUserDataPath() + 
			PATH_DELIMITER + strUserId + PATH_DELIMITER + strUserId + PATH_DELIMITER + TFN_OUTLOOK;
		outlookTable_.reset(new (std::nothrow)OutlookTable(outlookTablePath));
		if (NULL == outlookTable_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		// init outlook transmit notify
		ITransmitNotify*notify = new (std::nothrow)OutlookTransmitNotify(userContext_, transTableMgr_.get(), outlookTable_.get());
		if (NULL == notify)
		{
			return RT_MEMORY_MALLOC_ERROR;
		}
		setTransmitNotify(ATT_Upload_Outlook, notify);

		// init scanner
		scanner_.reset(new (std::nothrow)TransTaskScanner(userContext_, transTableMgr_.get()));
		if (NULL == scanner_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}
		// init scheduler
		scheduler_.reset(new (std::nothrow)TransTaskScheduler(userContext_, transTableMgr_.get()));
		if (NULL == scheduler_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		// update all the running tasks to waiting for re-schedule
		// now backup and other type of tasks are separated
		TransRootTable* rootTable = transTableMgr_->getRootTable(ATT_Backup);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "get root table of backup failed.");
			return RT_ERROR;
		}
		AsyncTransRootNodes rootNodes;
		TransDetailTablePtr detailTable;
		(void)rootTable->getNodes(ATS_Running, rootNodes, PAGE_OBJ);
		for (AsyncTransRootNodes::iterator it = rootNodes.begin(); it != rootNodes.end(); ++it)
		{
			if ((*it)->fileType != FILE_TYPE_FILE)
			{
				detailTable = transTableMgr_->getDetailTable((*it)->group);
				if (NULL != detailTable.get())
				{
					(void)detailTable->updateStatus(ATS_Running, ATS_Waiting);
				}
			}
		}
		(void)rootTable->updateStatus(ATS_Running, ATS_Waiting);

		rootTable = transTableMgr_->getRootTable(ATT_Upload);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "get root table failed.");
			return RT_ERROR;
		}
		rootNodes.clear();
		(void)rootTable->getNodes(ATS_Running, rootNodes, PAGE_OBJ);
		for (AsyncTransRootNodes::iterator it = rootNodes.begin(); it != rootNodes.end(); ++it)
		{
			if ((*it)->fileType != FILE_TYPE_FILE)
			{
				detailTable = transTableMgr_->getDetailTable((*it)->group);
				if (NULL != detailTable.get())
				{
					(void)detailTable->updateStatus(ATS_Running, ATS_Waiting);
				}
			}
		}
		(void)rootTable->updateStatus(ATS_Running, ATS_Waiting);

		(void)transTableMgr_->removeZombieDetailTable();

		init_ = true;

		userContext_->getWorkmodeMgr()->addWorkModeChangeNotify(this);

		return RT_OK;
	}

	virtual int32_t release()
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (!init_)
		{
			return RT_OK;
		}		
		
		if (NULL != scanner_.get())
		{
			scanner_->interrupt();
			scanner_.reset(NULL);
		}
		if (NULL != scheduler_.get())
		{
			scheduler_->interrupt();
			scheduler_.reset(NULL);
		}

		transmitNotifies_.clear();

		init_ = false;

		return RT_OK;
	}

	virtual int32_t setTransmitNotify(const AsyncTransType type, ITransmitNotify* notify)
	{
		TransmitNotifies::iterator it = transmitNotifies_.find(type);
		if (transmitNotifies_.end() != it)
		{
			transmitNotifies_.erase(it);
		}
		transmitNotifies_[type].reset(notify);
		return RT_OK;
	}

	virtual ITransmitNotify* getTransmitNotify(const AsyncTransType type)
	{
		TransmitNotifies::iterator it = transmitNotifies_.find(type);
		if (transmitNotifies_.end() != it)
		{
			return it->second.get();
		}
		return NULL;
	}

	virtual int32_t upload(
		const std::wstring& group, /* group id, used to be a GUID */
		const Path& localPath, /* path, type is required */
		const Path& remoteParent, /* id, ownerId is required */
		const AsyncTransType type
		)
	{
		if (localPath.path().empty() || INVALID_ID == remoteParent.id() || 
			remoteParent.ownerId() == INVALID_ID)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, local path: %s, remote parent id: %I64d, user id: %I64d.", 
				Utility::String::wstring_to_string(localPath.path()).c_str(), 
				remoteParent.id(), remoteParent.ownerId());
			return RT_INVALID_PARAM;
		}

		UserContext* userContext = UserContextMgr::getInstance()->getUserContext(remoteParent.ownerId());
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"get user context pointer failed, user id is %I64d.", 
				remoteParent.ownerId());
			return RT_ERROR;
		}

		int32_t ret = RT_OK;

		AsyncTransRootNode rootNode(new st_AsyncTransRootNode);
		rootNode->group = group;
		rootNode->source = localPath.path();
		rootNode->parent = Utility::String::type_to_string<std::wstring>(remoteParent.id());
		rootNode->name = Utility::FS::get_file_name(localPath.path());
		rootNode->type = type;
		rootNode->fileType = localPath.type();
		rootNode->userId = userContext->id.id;
		rootNode->userType = userContext->id.type;
		rootNode->userName = userContext->id.name;
		rootNode->status = ATS_Waiting;
		if (rootNode->fileType != FILE_TYPE_FILE)
		{
			rootNode->statusEx = ATSEX_Scanning;
		}
		else
		{
			rootNode->size = Utility::FS::get_file_size(localPath.path());
		}
		// add detail node
		if (rootNode->fileType != FILE_TYPE_FILE)
		{
			AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
			detailNode->source = rootNode->source;
			detailNode->parent = rootNode->parent;
			detailNode->name = rootNode->name;
			detailNode->fileType = rootNode->fileType;
			detailNode->status = ATS_Waiting;
			detailNode->statusEx = ATSEX_Scanning;
			detailNode->root = rootNode;
			// if the detail table is not exist, create the detail table
			std::auto_ptr<TransDetailTable> detailTable(transTableMgr_->createDetailTable(detailNode->root->group));
			if (NULL == detailTable.get())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
					Utility::String::wstring_to_string(group).c_str());
				return RT_ERROR;
			}
			ret = detailTable->addNode(detailNode);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "add detail node failed.");
				return ret;
			}
		}
		// add root node
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get root table.");
			return RT_ERROR;
		}
		ret = rootTable->addNode(rootNode);
		if (RT_OK != ret && RT_SQLITE_EXIST != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add root node failed.");
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t download(
		const std::wstring& group, /* group id, used to be a GUID */
		const Path& remotePath, /* id, name, type, ownerId is required */
		const Path& localParent, /* path is required */
		const int64_t size, /* if file, size is required*/
		const AsyncTransType type
		)
	{
		if (remotePath.id() == INVALID_ID || remotePath.name().empty() || 
			remotePath.ownerId() == INVALID_ID || localParent.path().empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, remote id: %I64d, remote name: %s, user id: %I64d, local parent: %s.", 
				remotePath.id(), Utility::String::wstring_to_string(remotePath.name()).c_str(), 
				remotePath.ownerId(), Utility::String::wstring_to_string(localParent.path()).c_str());
			return RT_INVALID_PARAM;
		}

		UserContext* userContext = UserContextMgr::getInstance()->getUserContext(remotePath.ownerId());
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"get user context pointer failed, user id is %I64d.", 
				remotePath.ownerId());
			return RT_ERROR;
		}

		int32_t ret = RT_OK;

		AsyncTransRootNode rootNode(new st_AsyncTransRootNode);
		rootNode->group = group;
		rootNode->source = Utility::String::type_to_string<std::wstring>(remotePath.id());
		rootNode->parent = localParent.path();
		rootNode->name = remotePath.name();
		rootNode->type = type;
		rootNode->fileType = remotePath.type();
		rootNode->userId = userContext->id.id;
		rootNode->userType = userContext->id.type;
		rootNode->userName = userContext->id.name;
		rootNode->status = ATS_Waiting;
		if (rootNode->fileType != FILE_TYPE_FILE)
		{
			rootNode->statusEx = ATSEX_Scanning;
		}
		else
		{
			rootNode->size = size;
		}
		// add detail node
		if (rootNode->fileType != FILE_TYPE_FILE)
		{
			AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
			detailNode->source = rootNode->source;
			detailNode->parent = rootNode->parent;
			detailNode->name = rootNode->name;
			detailNode->fileType = rootNode->fileType;
			detailNode->status = ATS_Waiting;
			detailNode->statusEx = ATSEX_Scanning;
			detailNode->root = rootNode;
			// if the detail table is not exist, create the detail table
			std::auto_ptr<TransDetailTable> detailTable(transTableMgr_->createDetailTable(detailNode->root->group));
			if (NULL == detailTable.get())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
					Utility::String::wstring_to_string(group).c_str());
				return RT_ERROR;
			}
			ret = detailTable->addNode(detailNode);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "add detail node failed.");
				return ret;
			}
		}
		// add root node
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get root table.");
			return RT_ERROR;
		}
		ret = rootTable->addNode(rootNode);
		if (RT_OK != ret && RT_SQLITE_EXIST != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add root node failed.");
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t uploads(const AsyncUploadTaskParamExs& tasks, const AsyncTransType type, const int64_t userId)
	{
		if (userId < 0)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, user id is %I64d.", userId)
			return RT_INVALID_PARAM;
		}
		if (tasks.empty())
		{
			return RT_OK;
		}

		UserContext* userContext = UserContextMgr::getInstance()->getUserContext(userId);
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"get user context pointer failed, user id is %I64d.", 
				userId);
			return RT_ERROR;
		}

		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "get root table of type %d failed.", type);
			return RT_ERROR;
		}

		int32_t ret = RT_OK;
		AsyncTransRootNodes rootNodes;
		AsyncUploadTaskParamEx asyncUploadTaskPramEx;
		for (AsyncUploadTaskParamExs::const_iterator it = tasks.begin(); it != tasks.end(); ++it)
		{
			asyncUploadTaskPramEx = *it;
			AsyncTransRootNode rootNode(new st_AsyncTransRootNode);
			rootNode->group = asyncUploadTaskPramEx->group;
			rootNode->source = asyncUploadTaskPramEx->localPath;
			rootNode->parent = Utility::String::type_to_string<std::wstring>(asyncUploadTaskPramEx->remoteParentId);
			rootNode->name = Utility::FS::get_file_name(asyncUploadTaskPramEx->localPath);
			rootNode->type = type;
			rootNode->fileType = asyncUploadTaskPramEx->fileType;
			rootNode->userId = userContext->id.id;
			rootNode->userType = userContext->id.type;
			rootNode->userName = userContext->id.name;
			rootNode->size = asyncUploadTaskPramEx->size;
			rootNode->status = ATS_Waiting;
			if (rootNode->fileType != FILE_TYPE_FILE)
			{
				rootNode->statusEx = ATSEX_Scanning;
				// add detail node
				AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
				detailNode->source = rootNode->source;
				detailNode->parent = rootNode->parent;
				detailNode->name = rootNode->name;
				detailNode->fileType = rootNode->fileType;
				detailNode->status = ATS_Waiting;
				detailNode->statusEx = ATSEX_Scanning;
				detailNode->root = rootNode;
				// if the detail table is not exist, create the detail table
				std::auto_ptr<TransDetailTable> detailTable(transTableMgr_->createDetailTable(detailNode->root->group));
				if (NULL == detailTable.get())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
						Utility::String::wstring_to_string(rootNode->group).c_str());
					return RT_ERROR;
				}
				ret = detailTable->addNode(detailNode);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "add detail node failed.");
					return ret;
				}
			}
			rootNodes.push_back(rootNode);
		}

		ret = rootTable->addNodes(rootNodes);
		if (RT_OK !=  ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add upload root nodes failed.");
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t downloads(const AsyncDownloadTaskParamExs& tasks, const AsyncTransType type, const int64_t userId)
	{
		if (userId < 0)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, user id is %I64d.", userId)
				return RT_INVALID_PARAM;
		}
		if (tasks.empty())
		{
			return RT_OK;
		}

		UserContext* userContext = UserContextMgr::getInstance()->getUserContext(userId);
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"get user context pointer failed, user id is %I64d.", 
				userId);
			return RT_ERROR;
		}

		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "get root table of type %d failed.", type);
			return RT_ERROR;
		}

		int32_t ret = RT_OK;
		AsyncTransRootNodes rootNodes;
		AsyncDownloadTaskParamEx asyncDownloadTaskPramEx;
		for (AsyncDownloadTaskParamExs::const_iterator it = tasks.begin(); it != tasks.end(); ++it)
		{
			asyncDownloadTaskPramEx = *it;
			AsyncTransRootNode rootNode(new st_AsyncTransRootNode);
			rootNode->group = asyncDownloadTaskPramEx->group;
			rootNode->source = Utility::String::type_to_string<std::wstring>(asyncDownloadTaskPramEx->remoteId);
			rootNode->parent = asyncDownloadTaskPramEx->localParentPath;
			rootNode->name = asyncDownloadTaskPramEx->name;
			rootNode->type = type;
			rootNode->fileType = asyncDownloadTaskPramEx->fileType;
			rootNode->userId = userContext->id.id;
			rootNode->userType = userContext->id.type;
			rootNode->userName = userContext->id.name;
			rootNode->size = asyncDownloadTaskPramEx->size;
			rootNode->status = ATS_Waiting;
			if (rootNode->fileType != FILE_TYPE_FILE)
			{
				rootNode->statusEx = ATSEX_Scanning;
				// add detail node
				AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
				detailNode->source = rootNode->source;
				detailNode->parent = rootNode->parent;
				detailNode->name = rootNode->name;
				detailNode->fileType = rootNode->fileType;
				detailNode->status = ATS_Waiting;
				detailNode->statusEx = ATSEX_Scanning;
				detailNode->root = rootNode;
				// if the detail table is not exist, create the detail table
				std::auto_ptr<TransDetailTable> detailTable(transTableMgr_->createDetailTable(detailNode->root->group));
				if (NULL == detailTable.get())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
						Utility::String::wstring_to_string(rootNode->group).c_str());
					return RT_ERROR;
				}
				ret = detailTable->addNode(detailNode);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "add detail node failed.");
					return ret;
				}
			}
			rootNodes.push_back(rootNode);
		}

		ret = rootTable->addNodes(rootNodes);
		if (RT_OK !=  ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add download root nodes failed.");
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t beginAddAsyncTasks(const std::wstring& group, const int64_t userId, const AsyncTransType type)
	{
		if (group.empty() || userId == INVALID_ID)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, group is %s, user id is %I64d.", 
				Utility::String::wstring_to_string(group).c_str(), userId);
			return RT_INVALID_PARAM;
		}

		UserContext* userContext = UserContextMgr::getInstance()->getUserContext(userId);
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"get user context pointer failed, user id is %I64d.", 
				userId);
			return RT_ERROR;
		}

		AsyncTransRootNode rootNode(new st_AsyncTransRootNode);
		rootNode->group = group;
		rootNode->type = type;
		rootNode->fileType = FILE_TYPE_DIR;
		rootNode->userId = userContext->id.id;
		rootNode->userType = userContext->id.type;
		rootNode->userName = userContext->id.name;
		rootNode->status = ATS_Waiting;
		rootNode->statusEx = ATSEX_AddingTasks;
		rootNode->priority = DEFAULT_PRIORITY;

		// add root node
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get root table.");
			return RT_ERROR;
		}
		int32_t ret = rootTable->addNode(rootNode);
		if (RT_SQLITE_EXIST == ret)
		{
			ret = rootTable->addStatusEx(group, ATSEX_AddingTasks);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "add root node adding tasks statusEx failed.");
				return ret;
			}
			ret = rootTable->updateStatus(group, ATS_Waiting);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "add root node update status to waiting failed.");
				return ret;
			}
			return RT_OK;
		}
		else if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add root node failed.");
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t addAsyncUploadTasks(const std::wstring& group, const AsyncUploadTaskParams& tasks)
	{
		if (group.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "invalid param, group is %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_INVALID_PARAM;
		}
		if (tasks.empty())
		{
			return RT_OK;
		}
		TransDetailTable* detailTable = transTableMgr_->createDetailTable(group);
		if (NULL == detailTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}

		AsyncTransDetailNodes detailNodes;
		AsyncUploadTaskParam asyncUploadTaskPram;

		bool isVirtualStatusSet = false;
		for (AsyncUploadTaskParams::const_iterator it = tasks.begin(); it != tasks.end(); ++it)
		{
			asyncUploadTaskPram = *it;
			AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
			detailNode->source = asyncUploadTaskPram->localPath;
			detailNode->parent = Utility::String::type_to_string<std::wstring>(asyncUploadTaskPram->remoteParentId);
			detailNode->name = Utility::FS::get_file_name(asyncUploadTaskPram->localPath);
			detailNode->fileType = asyncUploadTaskPram->fileType;
			detailNode->size = asyncUploadTaskPram->size;
			detailNode->status = ATS_Waiting;

			if (detailNode->fileType != FILE_TYPE_FILE)
			{
				detailNode->statusEx = ATSEX_Scanning;
			}
			
			// Set first file to VirtualParent status for batch prepare upload
			if (asyncUploadTaskPram->size < BATCH_PREUPLOAD_FILE_SIZE)
			{
				if(!isVirtualStatusSet)
				{
					detailNode->statusEx = ATSEX_VirtualParent;
					isVirtualStatusSet = true;
					SERVICE_INFO(MODULE_NAME, RT_OK, "Set batch preupload virtual parent: %s", 
						Utility::String::wstring_to_string(detailNode->source).c_str());
				}
				else
				{
					detailNode->statusEx = ATSEX_Uninitial;
				}
			}
			detailNodes.push_back(detailNode);
		}

		int32_t ret = detailTable->checkAndAddNodes(detailNodes);
		if (RT_SQLITE_EXIST == ret)
		{
			AsyncTransDetailNodes repeatedNodes;
			ret = detailTable->getRepeatedNodes(repeatedNodes, detailNodes);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "failed to get repeated ndoes from detail table of %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return ret;
			}
			std::list<std::wstring> sources;
			for (AsyncTransDetailNodes::iterator it = repeatedNodes.begin(); it != repeatedNodes.end(); ++it)
			{
				AsyncTransDetailNode detailNode = *it;
				ret = scheduler_->interruptRunningTask(detailNode->root->group, detailNode->source);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "failed interrupt task, source is %s, group is %s.", 
						Utility::String::wstring_to_string(detailNode->source).c_str(), 
						Utility::String::wstring_to_string(detailNode->root->group).c_str());
					return ret;
				}
				sources.push_back(detailNode->source);
			}
			ret = detailTable->deleteNodes(sources);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "failed to delete repeated detail nodes in detail table of %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return ret;
			}
			ret = detailTable->checkAndAddNodes(detailNodes);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add detail nodes failed.");
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t addAsyncDownloadTasks(const std::wstring& group, const AsyncDownloadTaskParams& tasks)
	{
		if (group.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "invalid param, group is %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_INVALID_PARAM;
		}
		if (tasks.empty())
		{
			return RT_OK;
		}
		TransDetailTable* detailTable = transTableMgr_->createDetailTable(group);
		if (NULL == detailTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "create the detail table of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		AsyncTransDetailNodes detailNodes;
		AsyncDownloadTaskParam asyncDownloadTaskPram;
		for (AsyncDownloadTaskParams::const_iterator it = tasks.begin(); it != tasks.end(); ++it)
		{
			asyncDownloadTaskPram = *it;
			AsyncTransDetailNode detailNode(new st_AsyncTransDetailNode);
			detailNode->source = Utility::String::type_to_string<std::wstring>(asyncDownloadTaskPram->remoteId);
			detailNode->parent = asyncDownloadTaskPram->localParentPath;
			detailNode->name = asyncDownloadTaskPram->name;
			detailNode->fileType = asyncDownloadTaskPram->fileType;
			detailNode->size = asyncDownloadTaskPram->size;
			detailNode->status = ATS_Waiting;
			if (detailNode->fileType != FILE_TYPE_FILE)
			{
				detailNode->statusEx = ATSEX_Scanning;
			}
			detailNodes.push_back(detailNode);
		}

		int32_t ret = detailTable->addNodes(detailNodes);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "add detail nodes failed.");
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t endAddAsyncTasks(const std::wstring& group, const AsyncTransType type)
	{
		if (group.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid param, group is %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_INVALID_PARAM;
		}
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get root table.");
			return RT_ERROR;
		}
		int32_t ret = rootTable->removeStatusEx(group, ATSEX_AddingTasks);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "remove root node adding tasks statusEx failed.");
			return ret;
		}

		return RT_OK;
	}

	virtual int32_t pauseTask(const std::wstring& group)
	{
		int32_t ret = scheduler_->interruptRunningTask(group);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "interrupt running task of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t pauseTask(const AsyncTransType type)
	{
		int32_t ret = scheduler_->interruptRunningTask(type);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "interrupt running task of type %d failed.", type);
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t resumeTask(const std::wstring& group)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(group);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}

		AsyncTransRootNode rootNode;
		int32_t ret = rootTable->getNode(group, rootNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to get root node of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		if (rootNode->fileType != FILE_TYPE_FILE)
		{
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
			if (NULL == detailTable.get())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
					"failed to get detail table of task %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return RT_ERROR;
			}
			ret = detailTable->updateStatus(AsyncTransStatus(ATS_Cancel|ATS_Error), ATS_Waiting);
			if (RT_OK != ret)
			{
				return ret;
			}
		}
		ret = rootTable->updateStatus(group, ATS_Waiting);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to update root table status of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t resumeTask(const AsyncTransType type)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of type task %d.", type);
			return RT_ERROR;
		}

		AsyncTransRootNodes rootNodes;
		int32_t ret = rootTable->getNodes(type, rootNodes, PAGE_OBJ);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to get root nodes of type task %d.", type);
			return ret;
		}
		AsyncTransRootNode rootNode;
		for (AsyncTransRootNodes::iterator it = rootNodes.begin(); it != rootNodes.end(); ++it)
		{
			rootNode = *it;
			if ((rootNode->status&(ATS_Cancel|ATS_Error)) == 0)
			{
				continue;
			}
			if (rootNode->fileType != FILE_TYPE_FILE)
			{
				TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(rootNode->group);
				if (NULL == detailTable.get())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
						"failed to get detail table of task %s.", 
						Utility::String::wstring_to_string(rootNode->group).c_str());
					return RT_ERROR;
				}
				ret = detailTable->updateStatus(AsyncTransStatus(ATS_Cancel|ATS_Error), ATS_Waiting);
				if (RT_OK != ret)
				{
					return ret;
				}
			}
		}
		ret = rootTable->updateStatus(type, ATS_Waiting);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to update root table status of task type %d.", type);
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t deleteTask(const std::wstring& group)
	{
		int32_t ret = scheduler_->interruptRunningTask(group);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "interrupt running task of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		TransRootTable* rootTable = transTableMgr_->getRootTable(group);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		ret = rootTable->deleteNode(group);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		return transTableMgr_->removeDetailTable(group);
	}

	virtual int32_t deleteTask(const AsyncTransType type)
	{
		int32_t ret = scheduler_->interruptRunningTask(type);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "interrupt running task of type %d failed.", type);
			return ret;
		}
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of type task %d.", type);
			return RT_ERROR;
		}
		ret = rootTable->deleteNodes(type);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete root table of task type %d.", type);
			return ret;
		}
		return transTableMgr_->removeDetailTable(type);
	}

	virtual int32_t getTask(const std::wstring& group, AsyncTransRootNode& node)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(group);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		return rootTable->getNode(group, node);
	}

	virtual int32_t getTasks(const AsyncTransType type, AsyncTransRootNodes& nodes, const Page& page)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of type task %d.", type);
			return RT_ERROR;
		}
		return rootTable->getNodes(type, nodes, page);
	}

	virtual int32_t getTasksCount(const AsyncTransType type)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(type);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of type task %d.", type);
			return RT_ERROR;
		}
		return rootTable->getNodesCount(type);
	}

	virtual int32_t getTasksCount(const std::wstring& group, AsyncTransStatus status)
	{
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
		if (NULL == detailTable.get())
		{
			return RT_SQLITE_NOEXIST;
		}
		return detailTable->getNodesCount(status);
	}

	virtual int32_t getErrorTasks(const std::wstring& group, AsyncTransDetailNodes& nodes, const Page& page)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(group);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		AsyncTransRootNode rootNode;
		int32_t ret = rootTable->getNode(group, rootNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to get root node of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		if (rootNode->fileType == FILE_TYPE_FILE)
		{
			AsyncTransDetailNode detailNode(new (std::nothrow)st_AsyncTransDetailNode);
			if (NULL == detailNode.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}
			detailNode->root = rootNode;
			detailNode->source = rootNode->source;
			detailNode->parent = rootNode->parent;
			detailNode->name = rootNode->name;
			detailNode->fileType = rootNode->fileType;
			detailNode->size = rootNode->size;
			detailNode->status = rootNode->status;
			detailNode->statusEx = rootNode->statusEx;
			detailNode->errorCode = rootNode->errorCode;
			nodes.push_back(detailNode);
			return RT_OK;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
		if (NULL == detailTable.get())
		{
			return RT_FILE_NOEXIST_ERROR;
		}
		return detailTable->getNodes(ATS_Error, nodes, page);
	}

	virtual int32_t getErrorTasksCount(const std::wstring& group)
	{
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
		if (NULL == detailTable.get())
		{
			return RT_SQLITE_NOEXIST;
		}
		return detailTable->getNodesCount(ATS_Error);
	}

	virtual int32_t deleteErrorTask(const std::wstring& group, const std::wstring source)
	{
		TransRootTable* rootTable = transTableMgr_->getRootTable(group);
		if (NULL == rootTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get root table of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		AsyncTransRootNode rootNode;
		int32_t ret = rootTable->getNode(group, rootNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to get root node of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		if (rootNode->fileType == FILE_TYPE_FILE)
		{
			ret = rootTable->deleteNode(group);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"failed to delete root node of task %s.", 
					Utility::String::wstring_to_string(group).c_str());
				return ret;
			}
			return RT_OK;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
		if (NULL == detailTable.get())
		{
			return RT_SQLITE_NOEXIST;
		}
		ret = detailTable->deleteNode(source);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete detail node of task %s.", 
				Utility::String::wstring_to_string(source).c_str());
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t deleteHistoricalTask(const std::wstring& group)
	{
		TransCompleteTable* completeTable = transTableMgr_->getCompleteTable();
		if (NULL == completeTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get complete table.");
			return RT_ERROR;
		}
		int32_t ret = completeTable->deleteNode(group);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete complete node of task %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t deleteHistoricalTask()
	{
		TransCompleteTable* completeTable = transTableMgr_->getCompleteTable();
		if (NULL == completeTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get complete table.");
			return RT_ERROR;
		}
		int32_t ret = completeTable->deleteNodes(AsyncTransType(ATT_Upload|ATT_Download));
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete complete node of task type %d.", 
				ATT_Upload|ATT_Download);
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t getHistoricalTask(const std::wstring& group, AsyncTransCompleteNode& node)
	{
		TransCompleteTable* completeTable = transTableMgr_->getCompleteTable();
		if (NULL == completeTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get complete table.");
			return RT_ERROR;
		}
		return completeTable->getNode(group, node);
	}

	virtual int32_t getHistoricalTasks(AsyncTransCompleteNodes& nodes, const Page& page)
	{
		TransCompleteTable* completeTable = transTableMgr_->getCompleteTable();
		if (NULL == completeTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get complete table.");
			return RT_ERROR;
		}
		return completeTable->getNodes(AsyncTransType(ATT_Upload|ATT_Download), nodes, page);
	}

	virtual int32_t addHistoricalTask(const AsyncTransCompleteNode& node)
	{
		TransCompleteTable* completeTable = transTableMgr_->getCompleteTable();
		if (NULL == completeTable)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get complete table.");
			return RT_ERROR;
		}
		int32_t ret = completeTable->addNode(node);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to delete complete node of task [%s, %s, %s, %s].", 
				Utility::String::wstring_to_string(node->group).c_str(), 
				Utility::String::wstring_to_string(node->parent).c_str(), 
				Utility::String::wstring_to_string(node->name).c_str());
			return ret;
		}
		return RT_OK;
	}

	virtual int32_t deleteTasks(const std::wstring& group, const std::list<std::wstring>& sources)
	{
		if (group.empty() || sources.empty())
		{
			return RT_INVALID_PARAM;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(group);
		if (NULL == detailTable.get())
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "failed to get detail table of %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return RT_ERROR;
		}
		int32_t ret = detailTable->deleteNodes(sources);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to delete detail nodes of %s.", 
				Utility::String::wstring_to_string(group).c_str());
			return ret;
		}
		return RT_OK;
	}
};

AsyncTaskMgr* AsyncTaskMgr::create(UserContext* userContext)
{
	return static_cast<AsyncTaskMgr*>(new AsyncTaskMgrImpl(userContext));
}
