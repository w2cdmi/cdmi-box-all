#include "CommonTransmitNotify.h"
#include "TransTask.h"
#include "Utility.h"
#include "NotifyMgr.h"
#include "NetworkMgr.h"
#include "PathMgr.h"
#include "LocalFile.h"
#include "Utility.h"
#include "RestParam.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("CommonTransmitNotify")
#endif

class CommonTransmitNotify::Impl
{
public:
	Impl(UserContext* userContext, TransTableMgr* transTableMgr)
		:userContext_(userContext)
		,transTableMgr_(transTableMgr)
	{

	}

	int32_t notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote)
	{
		// only care about the finished transmit status
		if (transmit->getStatus() != TRANSMIT_END)
		{
			return RT_OK;
		}

		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();

		// first check cancel, then error, finally success
		if (transTask->IsCancel())
		{
			int32_t ret = doCancel(transTask, transDetailNode);
			if (RT_OK != ret)
			{
				TransTaskErrorCode errorCode(transTask);
				errorCode.SetErrorCode(ret);
				return doError(transTask, transDetailNode);
			}
			return RT_OK;
		}
		else if (transTask->IsError())
		{
			return doError(transTask, transDetailNode);
		}
		else
		{
			int32_t ret = doSuccess(transmit, remote);
			if (RT_OK != ret)
			{
				TransTaskErrorCode errorCode(transTask);
				errorCode.SetErrorCode(ret);
				return doError(transTask, transDetailNode);
			}
			return RT_OK;
		}
		return RT_OK;
	}

	void notifyProgress(ITransmit* transmit, const int64_t transedSize, const int64_t transIncrement, const int64_t sizeIncrement)
	{
		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		(void)transTableMgr_->getRootTable(transDetailNode->root->type)->updateTransedSizeAndSize(
			transDetailNode->root->group, transIncrement, sizeIncrement);
		// folder should update the detail node
		if (sizeIncrement != 0 && transDetailNode->root->fileType != FILE_TYPE_FILE)
		{
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
			if (detailTable)
			{
				(void)detailTable->updateSize(transDetailNode->source, sizeIncrement);
			}
		}

		int tickNow = GetTickCount();
		if ((tickNow - tick_) > 1000)
		{
			AsyncTransRootNode transRootNode;
			if (RT_OK == transTableMgr_->getRootTable(transDetailNode->root->type)->getNode(
				transDetailNode->root->group, transRootNode))
			{
				(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
					NOTIFY_MSG_TRANS_TASK_UPDATE, 
					transDetailNode->root->group, 
					Utility::String::type_to_string<std::wstring>(transRootNode->type), 
					Utility::String::type_to_string<std::wstring>(transRootNode->transedSize), 
					Utility::String::type_to_string<std::wstring>(transRootNode->size)));
			}
			tick_ = tickNow;
		}
	}

private:
	int32_t doError(TransTask* transTask, AsyncTransDetailNode& transDetailNode)
	{
		// if the task's root is a file
		// 1. update the status and error code in the root table
		// 2. notify error message to UI
		// if the task's root is not a file, update the status and error code in the detail table
		if (transDetailNode->root->fileType != FILE_TYPE_FILE)
		{
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
			if (NULL != detailTable.get())
			{
				(void)detailTable->updateStatusAndErrorCode(
					transDetailNode->source, ATS_Error, transTask->GetErrorCode());

				// Remove current virtual parent and set the next virtual parent
				if(transDetailNode->statusEx == ATSEX_VirtualParent)
				{
					detailTable->setNextVirtualParent(transDetailNode);
				}
				
			}
			return RT_OK;
		}

		(void)transTableMgr_->getRootTable(transDetailNode->root->type)->updateStatusAndErrorCode(
			transDetailNode->root->group, ATS_Error, transTask->GetErrorCode());

		(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_ERROR, 
			transDetailNode->root->group, 
			Utility::String::type_to_string<std::wstring>(transDetailNode->root->type)));
		return RT_OK;
	}

	int32_t doCancel(TransTask* transTask, AsyncTransDetailNode& transDetailNode)
	{
		// if the task is cancel, do nothing, because cancel must caused by user call cancelTask
		// if the task is canceled by the program stop, keep the running status and priority,
		// so next time when the program restart, the task can be scheduled with a high priority
		// in cancelTask, all the status have been modified
		return RT_OK;
	}

	int32_t doSuccess(ITransmit* transmit, const FILE_DIR_INFO& remote)
	{
		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		if (transDetailNode->fileType == FILE_TYPE_FILE)
		{
			if(ATSEX_VirtualParent == transDetailNode->statusEx)
			{
				int32_t ret = batchPreUpload(transDetailNode, transmit);
				if(RT_CANCEL == ret)
				{
					return RT_OK;
				}
				if(RT_OK != ret)
				{
					batchPreuploadErrorHandler(transDetailNode, transmit->getTransTask());
				}
			}
		}
		else
		{
			int32_t ret = RT_OK;
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
			if (NULL == detailTable.get())
			{
				return RT_ERROR;
			}
			if (transDetailNode->root->type == ATT_Download)
			{
				ret = detailTable->updateParentAndStatus(transDetailNode->source, transDetailNode->parent + PATH_DELIMITER + transDetailNode->name);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "update parent information failed, %s.", 
						Utility::String::wstring_to_string(transDetailNode->source).c_str());
					return ret;
				}
			}
			else
			{
				ret = detailTable->updateParent(transDetailNode->source, Utility::String::type_to_string<std::wstring>(remote.id));
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "update parent information failed, %s.", 
						Utility::String::wstring_to_string(transDetailNode->source).c_str());
					return ret;
				}

				// Update sub folders, virtual parent and big files to waiting status
				ret = detailTable->updateStatusForBatchUpload(Utility::String::type_to_string<std::wstring>(remote.id));
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "update status for batch prepare upload failed. Parent: %I64d.", remote.id);					
					return ret;
				}
			}			
		}

		// upload task should notify UI to refresh
		if (transDetailNode->root->type != ATT_Download)
		{
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
				NOTIFY_MSG_TRANS_TASK_REFRESH_UI, 
				Utility::String::type_to_string<std::wstring>(transDetailNode->root->userType), 
				Utility::String::type_to_string<std::wstring>(transDetailNode->root->userId), 
				transDetailNode->parent));
		}

		// if the task's root is a file
		// 1. delete the node from the root table
		// 2. notify complete message to UI
		// 3. push the node to complete table
		// if the task's root is not a file, delete the node from the detail table
		if (transDetailNode->root->fileType != FILE_TYPE_FILE)
		{
			TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
			if (NULL != detailTable.get())
			{
				if(transDetailNode->statusEx != ATSEX_VirtualParent)
				{
					(void)detailTable->deleteNode(transDetailNode->source);
				}
				else
				{
					(void)detailTable->checkAndDeleteNode(transDetailNode);
				}
			}
			return RT_OK;
		}

		if (transDetailNode->root->type == ATT_Upload || transDetailNode->root->type == ATT_Download)
		{
			AsyncTransCompleteNode completeNode(new st_AsyncTransCompleteNode);
			completeNode->group = transDetailNode->root->group;
			completeNode->source = transDetailNode->root->source;
			completeNode->parent = transDetailNode->root->parent;
			completeNode->name = transDetailNode->root->name;
			completeNode->type = transDetailNode->root->type;
			completeNode->fileType = transDetailNode->root->fileType;
			completeNode->userId = transDetailNode->root->userId;
			completeNode->userType = transDetailNode->root->userType;
			completeNode->userName = transDetailNode->root->userName;
			completeNode->size = transDetailNode->root->size;
			(void)transTableMgr_->getCompleteTable()->addNode(completeNode);
			(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
				NOTIFY_MSG_TRANS_TASK_COMPLETE, 
				transDetailNode->root->group, 
				Utility::String::type_to_string<std::wstring>(transDetailNode->root->type)));
		}
		(void)transTableMgr_->getRootTable(transDetailNode->root->type)->deleteNode(transDetailNode->root->group);

		return RT_OK;
	}

	int32_t batchPreUpload(AsyncTransDetailNode& transDetailNode, ITransmit* transmit)
	{
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
		if (NULL == detailTable.get())
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Can not find the detail table: %s", transDetailNode->root->group);
			return RT_ERROR;
		}
		TransTask*  transTask = transmit->getTransTask();

		BatchPreUploadRequest request;
		BatchPreUploadResponse response;
		request.parent = Utility::String::string_to_type<int64_t>(transDetailNode->parent);
		request.tokenTimeout = DEFAULT_TOKEN_TIMEOUT;

		Page page;
		page.start = 0;
		page.offset = MAX_LIMIT;

		int32_t ret;
		while(true)
		{
			AsyncTransDetailNodes nodes;
			ret = detailTable->listFilesByStatusEx(transDetailNode->parent, ATSEX_Uninitial, page, nodes);
			if(RT_OK != ret)
			{
				SERVICE_ERROR(MODULE_NAME, ret, "list file failed. Group: %s, Parent: %s.", 
					transDetailNode->root->group , Utility::String::wstring_to_string(transDetailNode->source).c_str());
				return RT_ERROR;
			}
			if(nodes.empty())
			{
				break;
			}
			FilePreUploadInfoList uploadInfoList;
			std::list<std::wstring> filteredList; 
			FailedList localFailedList; 
			FailedInfo localFaildInfo;
			AsyncTransDetailNode node;
			for (AsyncTransDetailNodes::iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				if(transTask->IsCancel())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_CANCEL, "batch preupload canceled.");
					return RT_CANCEL;
				}
				node = *it;
				// Files do not need to perform batch prepare upload
				if(node->size >= BATCH_PREUPLOAD_FILE_SIZE)
				{
					filteredList.push_back(node->source);
					continue;
				}
				FilePreUploadInfo preUploadInfo;

				preUploadInfo.name = Utility::String::wstring_to_utf8(node->name);
				preUploadInfo.size = node->size;

				// Calculate md5
				Fingerprint fingerprint;
				fingerprint.algorithm = Fingerprint::MD5;
				getFingerprint(fingerprint, node->source, transTask);
				preUploadInfo.md5 = fingerprint.fingerprint;
				preUploadInfo.blockMD5 = fingerprint.blockFingerprint;

				// Get modify time and create time for prepare upload
				Path localPath = userContext_->getPathMgr()->makePath();
				localPath.path(transDetailNode->source);	
				IFilePtr localFile(static_cast<IFile*>(new LocalFile(localPath, userContext_)));
				if (NULL == localFile.get())
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Can not get the file: %s", localPath.path());
					localFaildInfo.name = Utility::String::wstring_to_string(node->name);
					localFaildInfo.source = node->source;
					localFaildInfo.size = node->size;
					localFaildInfo.errorCode = RT_MEMORY_MALLOC_ERROR;
					localFailedList.push_back(localFaildInfo);
					continue;
				}
				FILE_DIR_INFO fileDirInfo;
				ret = localFile->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
				if(RT_OK != ret)
				{
					SERVICE_ERROR(MODULE_NAME, ret, "Can not get the file: %s", localPath.path());
					localFaildInfo.name = Utility::String::wstring_to_string(node->name);
					localFaildInfo.source = node->source;
					localFaildInfo.size = node->size;
					localFaildInfo.errorCode = ret;
					localFailedList.push_back(localFaildInfo);
				}
				preUploadInfo.contentCreatedAt = fileDirInfo.ctime;
				preUploadInfo.contentModifiedAt = fileDirInfo.mtime;

				uploadInfoList.push_back(preUploadInfo);
			}

			// Update files which do not need to perform batch prepare upload to waiting status
			if(!filteredList.empty())
			{
				ret = detailTable->batchUpdateStatus(filteredList);
				if(RT_OK != ret)
				{
					return ret;
				}
				SERVICE_INFO(MODULE_NAME, RT_OK, "Update filtered files to waiting status. File size: %d", filteredList.size());
			}

			request.fileList = uploadInfoList;

			if(uploadInfoList.empty())
			{
				return RT_OK;
			}

			//MAKE_CLIENT(client);
			MAKE_UPLOAD_CLIENT(client);
			ret = client().batchPreUpload(transDetailNode->root->userId, request, response);
			if(RT_OK != ret)
			{
				return ret;
			}

			ret = batchPreuploadHandler(transmit, uploadInfoList, response, localFailedList);
			if(RT_OK != ret)
			{
				return ret;
			}

			// Notify backup
			if(ATT_Backup == transDetailNode->root->type)
			{
				ITransmitNotifies notifies = transTask->getTransmitNotifies();
				for(ITransmitNotifies::iterator it = notifies.begin(); it != notifies.end(); ++it)
				{
					(*it)->batchPreuploadNotify(transDetailNode, response.uploadUrlList, response.uploadedList, response.failedList);
				}
			}
		}
		return ret;
	}

	int32_t batchPreuploadHandler(ITransmit* transmit, const FilePreUploadInfoList& uploadInfoList, BatchPreUploadResponse& response, FailedList& localFailedList)
	{
		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		int32_t uploadRet = fileUploadHandler(transDetailNode, uploadInfoList, response.uploadUrlList);
		int32_t flashUploadRet = flashUploadHandler(transmit, response.uploadedList);
		int32_t failedRet = failedHandler(transDetailNode, uploadInfoList, response.failedList, localFailedList);
		return flashUploadRet|failedRet|uploadRet;
	}

	int32_t fileUploadHandler(const AsyncTransDetailNode& transDetailNode, const FilePreUploadInfoList& uploadInfoList, const UploadUrlList urlList)
	{
		if(urlList.empty() || uploadInfoList.empty())
		{
			return RT_OK;
		}
		std::wstring parentPath = Utility::FS::get_parent_path(transDetailNode->source);

		AsyncTransDataNodes nodes;
		std::list<std::wstring> sources;
		for(UploadUrlList::const_iterator it = urlList.begin(); it != urlList.end(); ++it)
		{
			for(FilePreUploadInfoList::const_iterator info = uploadInfoList.begin(); info != uploadInfoList.end(); ++info)
			{
				if(info->name == it->name)
				{
					AsyncTransDataNode node(new (std::nothrow)st_AsyncTransDataNode);
					if(NULL == node.get())
					{
						return RT_MEMORY_MALLOC_ERROR;
					}
					Fingerprint fingerprint;
					fingerprint.algorithm = Fingerprint::MD5;
					fingerprint.fingerprint = info->md5;
					fingerprint.blockFingerprint = info->blockMD5;
					node->group = transDetailNode->root->group;
					node->fingerprint = fingerprint;
					node->source = parentPath +  PATH_DELIMITER + Utility::String::utf8_to_wstring(it->name);
					node->userDefine = Utility::String::utf8_to_wstring(it->uploadUrl) + L"/" + Utility::String::type_to_string<std::wstring>(it->fileId);
					nodes.push_back(node);
					sources.push_back(node->source);
					break;
				}
			}

		}
		TransSerializer* serializer = transTableMgr_->getDataTable();
		int32_t ret = serializer->addNodes(nodes);
		if(RT_OK != ret)
		{
			return ret;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
		if (NULL == detailTable.get())
		{
			SERVICE_WARN(MODULE_NAME, RT_ERROR, "Can not find the detail table: %s", transDetailNode->root->group);
			return RT_ERROR;
		}
		ret = detailTable->batchUpdateStatus(sources);
		return ret;
	}

	int32_t flashUploadHandler(ITransmit* transmit, const FileList& uploadedList)
	{
		if(uploadedList.empty())
		{
			return RT_OK;
		}
		TransTask* transTask = transmit->getTransTask();
		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
		if (NULL == detailTable.get())
		{
			SERVICE_WARN(MODULE_NAME, RT_ERROR, "Can not find the detail table: %s", transDetailNode->root->group);
			return RT_ERROR;
		}

		std::wstring parentPath = Utility::FS::get_parent_path(transDetailNode->source);
		std::wstring source;

		std::list<std::wstring> sourcesList;
		int64_t totalSize = 0;
		FileItem file;
		for(FileList::const_iterator it = uploadedList.begin(); it != uploadedList.end(); ++it)
		{
			file = *it;
			source = parentPath + PATH_DELIMITER + Utility::String::utf8_to_wstring(file.name());
			sourcesList.push_back(source);
			totalSize += file.size();
		}

		int32_t ret = detailTable->deleteNodes(sourcesList);
		notifyProgress(transmit, totalSize, totalSize, 0);
		return ret;
	}

	int32_t failedHandler(const AsyncTransDetailNode& transDetailNode, const FilePreUploadInfoList& uploadInfoList, FailedList& failedList, FailedList& localFailedList)
	{
		if(!failedList.empty())
		{
			std::wstring parentPath = Utility::FS::get_parent_path(transDetailNode->source);
			for(FailedList::iterator failedInfo = failedList.begin(); failedInfo != failedList.end(); ++failedInfo)
			{
				for(FilePreUploadInfoList::const_iterator fileInfo = uploadInfoList.begin(); fileInfo != uploadInfoList.end(); ++fileInfo)
				{
					if(failedInfo->name == fileInfo->name)
					{
						failedInfo->source = parentPath + Utility::String::string_to_wstring(failedInfo->name);
						failedInfo->size = fileInfo->size;
						failedInfo->errorCode = RT_ERROR;
						continue;
					}
				}
			}
		}
		if(!localFailedList.empty())
		{
			failedList.splice(failedList.end(), localFailedList);
		}
		if(failedList.empty())
		{
			return RT_OK;
		}

		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
		if (NULL == detailTable.get())
		{
			SERVICE_WARN(MODULE_NAME, RT_ERROR, "Can not find the detail table: %s", transDetailNode->root->group);
			return RT_ERROR;
		}
		std::wstring parentPath = Utility::FS::get_parent_path(transDetailNode->source);
		std::wstring source;
		std::list<std::wstring> sourceList;

		for(FailedList::const_iterator it = failedList.begin(); it != failedList.end(); ++it)
		{
			source = parentPath + PATH_DELIMITER + Utility::String::utf8_to_wstring(it->name);
			sourceList.push_back(source);
		}

		return detailTable->batchUpdateParentAndErrorCode(sourceList, transDetailNode->parent, RT_ERROR);
	}

	int32_t batchPreuploadErrorHandler(const AsyncTransDetailNode& transDetailNode, TransTask* transTask)
	{
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(transDetailNode->root->group);
		if (NULL == detailTable.get())
		{
			SERVICE_WARN(MODULE_NAME, RT_ERROR, "Can not find the detail table: %s", transDetailNode->root->group);
			return RT_ERROR;
		}
		// Remove current virtual parent and set the next virtual parent
		detailTable->setNextVirtualParent(transDetailNode);
		return RT_OK;
	}

private:
	UserContext* userContext_;
	TransTableMgr* transTableMgr_;
	DWORD tick_;
	static const int32_t MAX_LIMIT = 100;
	static const int64_t DEFAULT_TOKEN_TIMEOUT = 36000000;
};

CommonTransmitNotify::CommonTransmitNotify(UserContext* userContext, TransTableMgr* transTableMgr)
	:impl_(new Impl(userContext, transTableMgr))
{

}

int32_t CommonTransmitNotify::notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote)
{
	return impl_->notify(transmit, local, remote);
}

void CommonTransmitNotify::notifyProgress(ITransmit* transmit, const int64_t transedSize, const int64_t transIncrement, const int64_t sizeIncrement)
{
	return impl_->notifyProgress(transmit, transedSize, transIncrement, sizeIncrement);
}
