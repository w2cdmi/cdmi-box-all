#include "BackupAllTransTask.h"
#include "Utility.h"
#include "AsyncTaskMgr.h"
#include "TransTask.h"
#include "PathMgr.h"
#include "BackupAllDbMgr.h"
#include "SyncFileSystemMgr.h"
#include "ConfigureMgr.h"
#include "BackupAllMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllTransTask")
#endif

using namespace SD::Utility;

struct TransNotifyInfo
{
	std::wstring source;
	std::wstring name;
	int64_t size;
	bool isSuccess;
	int32_t errorCode;
	int64_t remoteParent;
	int64_t remoteId;
};
typedef std::list<TransNotifyInfo> TransNotifyInfoList;

class BackupTransNotify : public ITransmitNotify
{
public:
	BackupTransNotify(UserContext* userContext):userContext_(userContext)
	{
		writeThread_ = boost::thread(boost::bind(&BackupTransNotify::writeDbAsync, this));
	}

	~BackupTransNotify()
	{
		writeThread_.interrupt();
		writeThread_.join();
	}

	// if you do not care about the notify message, return 0
	virtual int32_t notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote)
	{
		// only care about the finished transmit status
		if (transmit->getStatus() != TRANSMIT_END)
		{
			return RT_OK;
		}

		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		// only care about backup tasks
		if (transDetailNode->root->type != ATT_Backup)
		{
			return RT_OK;
		}

		TransNotifyInfo transNode;
		transNode.source = transDetailNode->source;
		transNode.name = transDetailNode->name;
		transNode.size = transDetailNode->size;
		transNode.isSuccess = !(transTask->IsError() || transTask->IsCancel());
		transNode.errorCode = transTask->GetErrorCode();
		transNode.remoteParent = remote.parent;
		transNode.remoteId = remote.id;
		{
			boost::mutex::scoped_lock lock(mutex_);
			transNodes_.push_back(transNode);
			if(transNode.isSuccess)
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "upload path: %s.", SD::Utility::String::wstring_to_string(transNode.source).c_str());
			}
			else
			{
				SERVICE_ERROR(MODULE_NAME, transNode.errorCode, "failed upload path: %s.", SD::Utility::String::wstring_to_string(transNode.source).c_str());
			}
		}

		BATaskInfo* pBATaskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(!pBATaskInfo)
		{
			return RT_OK;
		}

		{
			boost::mutex::scoped_lock lock(sizeMutex_);
			std::map<std::wstring, int64_t>::iterator it = transSizeInfo_.find(transDetailNode->source);
			if(transSizeInfo_.end()!=it)
			{
				pBATaskInfo->transSize = pBATaskInfo->transSize - it->second;
				transSizeInfo_.erase(it);
			}
		}
		return RT_OK;
	}

	virtual void notifyProgress(ITransmit* transmit, const int64_t transedSize, const int64_t transIncrement, const int64_t sizeIncrement)
	{
		AsyncTransDetailNode& transDetailNode = transmit->getTransTask()->getTransDetailNode();
		if (transDetailNode->root->type != ATT_Backup)
		{
			return;
		}

		BATaskInfo* pBATaskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(!pBATaskInfo)
		{
			return;
		}

		{
			boost::mutex::scoped_lock lock(sizeMutex_);
			std::map<std::wstring, int64_t>::iterator it = transSizeInfo_.find(transDetailNode->source);
			if(transSizeInfo_.end()==it)
			{
				transSizeInfo_.insert(std::make_pair(transDetailNode->source, transedSize));
				pBATaskInfo->transSize += transedSize;
				pBATaskInfo->curUpload = transDetailNode->source;
			}
			else
			{
				it->second += transIncrement;
				pBATaskInfo->transSize += transIncrement;
			}
		}
	}

	virtual int32_t batchPreuploadNotify(const AsyncTransDetailNode& transDetailNode, const UploadUrlList& uploadUrlList, const FileList& flashUploadList, const FailedList& failedList) 
	{
		SERVICE_INFO(MODULE_NAME, RT_OK, "Backup batch prepare upload notify. Upload size: %d, Flash upload size: %d, failed size: %d ", uploadUrlList.size(), flashUploadList.size(), failedList.size());
		std::wstring parentPath = SD::Utility::FS::get_parent_path(transDetailNode->source);

		TransNotifyInfo transNode;
		FileItem file;
		std::wstring name;

		std::auto_ptr<PrintObj> printObj = PrintObj::create();
		for(FileList::const_iterator it = flashUploadList.begin(); it != flashUploadList.end(); ++it)
		{
			file = *it;
			name = SD::Utility::String::utf8_to_wstring(file.name());
			transNode.source = parentPath + PATH_DELIMITER + name;
			transNode.name = name;
			transNode.size = file.size();
			transNode.isSuccess = true;
			transNode.errorCode = RT_OK;
			transNode.remoteParent = file.parent();
			transNode.remoteId = file.id();
			{
				boost::mutex::scoped_lock lock(mutex_);
				transNodes_.push_back(transNode);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(transNode.source));
			}
		}
		if(!flashUploadList.empty())
		{
			HSLOG_TRACE(MODULE_NAME, RT_OK, "flash upload path: [%s]", printObj->getMsg().c_str());
		}
		BATaskInfo* pBATaskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(!name.empty() && pBATaskInfo)
		{
			pBATaskInfo->curUpload = parentPath + PATH_DELIMITER + name;
		}

		std::auto_ptr<PrintObj> printObj2 = PrintObj::create();
		FailedInfo failedInfo;
		for(FailedList::const_iterator it = failedList.begin(); it != failedList.end(); ++it)
		{
			failedInfo = *it;
			transNode.source = failedInfo.source;
			transNode.name = SD::Utility::String::utf8_to_wstring(failedInfo.name);
			transNode.size = failedInfo.size;
			transNode.isSuccess = false;
			transNode.errorCode = failedInfo.errorCode;
			transNode.remoteParent = SD::Utility::String::string_to_type<int64_t>(transDetailNode->parent);
			{
				boost::mutex::scoped_lock lock(mutex_);
				transNodes_.push_back(transNode);
				printObj2->lastField<std::string>(SD::Utility::String::wstring_to_string(transNode.source));
			}
		}
		if(!failedList.empty())
		{
			HSLOG_TRACE(MODULE_NAME, RT_OK, "failed upload path: [%s]", printObj2->getMsg().c_str());
		}

		return RT_OK;	
	};

private:
	void writeDbAsync()
	{
		while(true)
		{
			if(transNodes_.empty())
			{
				boost::this_thread::sleep(boost::posix_time::seconds(1));
				continue;
			}

			TransNotifyInfoList transNodes;
			{
				boost::mutex::scoped_lock lock(mutex_);
				transNodes.swap(transNodes_);
			}
			std::map<int64_t, int64_t> successInfo;
			int64_t successSize = 0;
			std::map<int64_t, int32_t> failedInfo;
			int64_t failedSize = 0;
			TransNotifyInfoList::const_iterator it = transNodes.begin();
			std::wstring lastdisk = it->source.substr(0,1);
			for(; it != transNodes.end(); ++it)
			{
				if(lastdisk!=it->source.substr(0,1))
				{
					BackupAllLocalDb* pLastLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(lastdisk);
					pLastLocalDb->updateUploadInfo(successInfo, successSize);
					pLastLocalDb->updateUploadFailed(failedInfo, failedSize);
					successInfo.clear();
					failedInfo.clear();
					successSize = 0;
					failedSize = 0;
					lastdisk = it->source.substr(0,1);
				}
				BackupAllLocalDb* pLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->source);
				int64_t localId;
				int64_t rowId = pLocalDb->getRowId(it->remoteParent, it->name, it->isSuccess, localId);
				if(-1==rowId)
				{
					//É¾³ýÈÎÎñ
					userContext_->getAsyncTaskMgr()->deleteErrorTask(BACKUPALL_GROUPID, it->source);
					SERVICE_INFO(MODULE_NAME, RT_OK, "delete path: %s.", SD::Utility::String::wstring_to_string(it->source).c_str());
					continue;
				}

				if (it->isSuccess)
				{
					std::wstring extName = FS::get_extension_name(it->source);
					if(L"pst"==extName||L"ost"==extName||L"nsf"==extName)
					{
						BackupAllLocalDb* pTempLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(lastdisk);
						pTempLocalDb->updatePstTime(localId);
					}
					successInfo.insert(std::make_pair(rowId, it->remoteId));
					successSize += it->size;
				}
				// error or cancel
				else
				{
					failedInfo.insert(std::make_pair(rowId, it->errorCode));
					failedSize += it->size;
				}
			}
			BackupAllLocalDb* pLastLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(lastdisk);
			pLastLocalDb->updateUploadInfo(successInfo, successSize);
			pLastLocalDb->updateUploadFailed(failedInfo, failedSize);
		}
	}

private:
	UserContext* userContext_;

	boost::thread writeThread_;
	boost::mutex mutex_;
	TransNotifyInfoList transNodes_;
	std::map<std::wstring, int64_t> transSizeInfo_;
	boost::mutex sizeMutex_;
};

class BackupAllTransTaskImpl : public BackupAllTransTask
{
public:
	BackupAllTransTaskImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual void setTransmitNotify()
	{
		userContext_->getAsyncTaskMgr()->setTransmitNotify(ATT_Backup, new BackupTransNotify(userContext_));
	}

	virtual int32_t upload(const std::wstring& localPath, const int64_t& remoteParent, const int64_t& size)
	{
		AsyncUploadTaskParam uploadInfo(new st_AsyncUploadTaskParam);
		uploadInfo->localPath = localPath;
		uploadInfo->remoteParentId = remoteParent;
		uploadInfo->fileType = FILE_TYPE_FILE;
		uploadInfo->size = size;
		AsyncUploadTaskParams uploadTasks;
		uploadTasks.push_back(uploadInfo);
		return userContext_->getAsyncTaskMgr()->addAsyncUploadTasks(BACKUPALL_GROUPID, uploadTasks);
	}

	virtual int32_t uploadSubFiles(const BATaskLocalNode& parentNode, const std::map<std::wstring, int64_t>& subFiles)
	{
		AsyncUploadTaskParams uploadTasks;
		for(std::map<std::wstring, int64_t>::const_iterator it = subFiles.begin();
			it != subFiles.end(); ++it)
		{
			AsyncUploadTaskParam uploadInfo(new st_AsyncUploadTaskParam);
			uploadInfo->localPath = parentNode.baseInfo.path + PATH_DELIMITER + it->first;
			uploadInfo->remoteParentId = parentNode.remoteId;
			uploadInfo->fileType = FILE_TYPE_FILE;
			uploadInfo->size = it->second;
			uploadTasks.push_back(uploadInfo);
		}
		return userContext_->getAsyncTaskMgr()->addAsyncUploadTasks(BACKUPALL_GROUPID, uploadTasks);
	}

private:
	UserContext* userContext_;
};

BackupAllTransTask* BackupAllTransTask::instance_ = NULL;

BackupAllTransTask* BackupAllTransTask::getInstance(UserContext* userContext)
{
	if (NULL == instance_)
	{
		instance_ = new BackupAllTransTaskImpl(userContext);
	}
	return instance_;
}