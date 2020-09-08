#include "RestUpload.h"
#include "RestFile.h"
#include "LocalFile.h"
#include "Utility.h"
#include "PathMgr.h"
#include "DataBaseMgr.h"
#include "NotifyMgr.h"
#include "ConfigureMgr.h"
#include "SyncTimeCalc.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestUpload")
#endif

#define UNUSE_PART_ID (-1)
static int64_t MAX_FILE_SIZE = 52428800000L; // 10000*TRANSMIT_PART_SIZE

RestUpload::RestUpload(UserContext* userContext, TransTask* transTask)
	:userContext_(userContext)
	,transTask_(transTask)
	,curPartialId_(0)
	,threadGroup_(NULL)
	,remoteFile_(NULL)
	,localFile_(NULL)
{
	partialIds_.clear();
}

RestUpload::~RestUpload()
{
	if (NULL != threadGroup_)
		delete threadGroup_;
}

void RestUpload::transmit()
{
	//userContext_->getSyncTimeCalc()->startUploadTimer();
	int32_t ret = prepareUpload();
	if(RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}

	if (TRANSMIT_PART_SIZE >= localFile_->property().size)
	{
		ret = totalUpload();
		transTask_->SetErrorCode(ret);
		return;
	}

	ret = startPartialUpload();
	if(RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}

	ret = completePartialUpload();
	if(RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
	}
}

void RestUpload::finishTransmit()
{
	localFile_->close();
	remoteFile_->close();

	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	if (NULL == restFile)
	{
		transTask_->SetErrorCode(RT_INVALID_PARAM);
		return;
	}
	if ((!transTask_->IsError() && !transTask_->IsCancel()) || 
		(FILE_CREATED == transTask_->GetErrorCode()))
	{
		int32_t ret = RT_OK;
		std::auto_ptr<FILE_DIR_INFO> fileDirInfo(new FILE_DIR_INFO);
		if (FILE_CREATED == transTask_->GetErrorCode())
		{
			*fileDirInfo = restFile->property();
			transTask_->SetErrorCode(RT_OK);
		}
		else
		{
			ret = restFile->getProperty(*(fileDirInfo.get()), PROPERTY_MASK_ALL);
			//userContext_->getSyncTimeCalc()->stopUploadTimer(fileDirInfo->size);
		}

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "get upload file info of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			transTask_->SetErrorCode(ret);
		}
		else
		{
			// the ctime and mtime is local file's create time and modify time
			(void)localFile_->getProperty(*fileDirInfo, PROPERTY_MASK(PROPERTY_CREATE|PROPERTY_MODIFY));
			transTask_->getCustomInfo().type = FileInfo;
			transTask_->getCustomInfo().content = (void*)fileDirInfo.get();
			transTask_->notifyCustomInfo();
		}

		// when complete transmit, but the task is error, should clear the uploadURL information
		// if not, next time will use the last uploadURL to re-upload, will never success
		if (transTask_->IsError())
		{
			transTask_->getTaskNode().userDefine = L"";
			(void)userContext_->getDataBaseMgr()->getTransTaskTable()->updateNode(transTask_->getTaskNode());
		}
	}
	else
	{
		// when auto upload and local file is not exist, return OK
		if (!localFile_->isExist() && 
			(ATT_Upload_Manual == transTask_->getTaskNode().id.type || 
			(ATT_Upload_Attachements == transTask_->getTaskNode().id.type)))
		{
			transTask_->SetErrorCode(RT_OK);
		}
	}
}

int32_t RestUpload::prepareUpload()
{
	int32_t ret = RT_OK;

	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();

	// create local file
	// local path just neet the full path for upload
	Path localPath = userContext_->getPathMgr()->makePath();
	if (ATT_Upload == transTaskNode.id.type)
	{
		localPath.id(Utility::String::string_to_type<int64_t>(transTaskNode.id.id));
		localPath.path(userContext_->getDataBaseMgr()->getLocalTable()->getPath(localPath.id()));
	}
	else if (ATT_Upload_Manual == transTaskNode.id.type || ATT_Upload_Attachements == transTaskNode.id.type)
	{
		localPath.path(transTaskNode.id.id);
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	
	localFile_ = IFilePtr(static_cast<IFile*>(new LocalFile(localPath, userContext_)));
	assert(NULL != localFile_.get());

	// create remote file
	// remote path need the parent and name
	Path remotePath = userContext_->getPathMgr()->makePath();
	remotePath.parent(Utility::String::string_to_type<int64_t>(transTaskNode.parent));
	remotePath.ownerId(Utility::String::string_to_type<int64_t>(transTaskNode.ownerId));
	remotePath.name(transTaskNode.name);
	remoteFile_ = IFilePtr(static_cast<IFile*>(new RestFile(remotePath, userContext_)));
	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	assert(NULL != restFile);	

	FILE_DIR_INFO fileDirInfo;
	if (!transTaskNode.signature.valid())
	{
		// 1. get remote file property to determinate the siganture algorithm
		// 2. if the remote file is not exist, use the default signature algorithm
		// 3. caculate the siganture of the local file to determinate upload or not
		transTaskNode.signature.algorithm = userContext_->getConfigureMgr()->getConfigure()->algorithmConf().algorithm;
		// if the remote id is exist, fill into the remote file
		ret = userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localPath.id(), fileDirInfo.id);
		if (RT_OK == ret)
		{
			remoteFile_->property(fileDirInfo, PROPERTY_ID);
			ret = restFile->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
			if (RT_OK == ret)
			{
				// the signature in cloud server may not caculated, and the algorithm may be invalid
				// in this case, use the default algorithm
				if (fileDirInfo.signature.valid())
				{
					transTaskNode.signature.algorithm = fileDirInfo.signature.algorithm;
				}				
			}
			else if (HTTP_NOT_FOUND != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "get the remote signature of %s failed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				return ret;
			}
		}

		transTaskNode.signature = transTask_->getSignature(localPath.path());
		if (!transTaskNode.signature.valid())
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "get siganture of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return transTask_->GetErrorCode();
		}

		// if the signature is the same, do not upload
		if (fileDirInfo.signature == transTaskNode.signature)
		{
			return FILE_CREATED;
		}
	}

	// set the signature for prepare upload
	fileDirInfo.signature = transTaskNode.signature;
	restFile->property(fileDirInfo, PROPERTY_SIGNATURE);

	// get size, modify time and create time for prepare upload
	ret = localFile_->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get local file of %s mtime and ctime failed.", 
			Utility::String::wstring_to_string(localFile_->path()).c_str());
		return ret;
	}

	if(MAX_FILE_SIZE < fileDirInfo.size)
	{
		return RT_DIFF_BIGFILE;
	}

	restFile->property(fileDirInfo, PROPERTY_SIZE);
	restFile->contentCreate(fileDirInfo.ctime);
	restFile->contentModify(fileDirInfo.mtime);

	// if uploadURL is not empty, get uploaded partials
	if (!transTaskNode.userDefine.empty() && TRANSMIT_PART_SIZE < localFile_->property().size)
	{
		// userDefine = uploadURL/fileID
		std::wstring::size_type pos = transTaskNode.userDefine.find_last_of(L'/');
		std::wstring uploadURL = transTaskNode.userDefine.substr(0, pos);
		int64_t fileID = Utility::String::string_to_type<int64_t>(transTaskNode.userDefine.substr(pos+1));

		restFile->uploadURL(uploadURL);
		fileDirInfo.id = fileID;
		restFile->property(fileDirInfo, PROPERTY_ID);

		PartList uploadPartials;
		ret = restFile->getUploadParts(uploadPartials);
		// file may upload success, but transtask may not, 
		// clear the uploadURL to re-upload
		if ((HTTP_NOT_FOUND == ret)||(HTTP_PRECONDITION_FAILED == ret)||(HTTP_INTERNAL_ERROR==ret))
		{
			HSLOG_TRACE(MODULE_NAME, ret, "re-upload file %s.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			transTaskNode.userDefine = L"";
			(void)userContext_->getDataBaseMgr()->getTransTaskTable()->updateNode(transTaskNode);
			return ret;
		}
		if (RT_OK != ret)
		{
			return ret;
		}

		// add upload task to UI
		(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_INSERT, 
			transTaskNode.id.id, 
			transTaskNode.id.group, 
			Utility::String::type_to_string<std::wstring>(transTaskNode.id.type), 
			Utility::String::type_to_string<std::wstring>(localFile_->property().size), 
			localFile_->path()));

		// initial the upload patials
		return initPartialIds(uploadPartials);
	}

	// prepare upload
	ret = restFile->preUpload();
	if (RT_OK != ret)
	{
		return ret;
	}
	// the same file has already exist in remote server
	else if (FILE_CREATED == restFile->error())
	{
		return FILE_CREATED;
	}

	// save the uploadURL, fileID and file signature for upload next time
	// userDefine = uploadURL/fileID
	transTaskNode.userDefine = restFile->uploadURL() + L"/" + 
		Utility::String::type_to_string<std::wstring>(restFile->property().id);
	userContext_->getDataBaseMgr()->getTransTaskTable()->updateNode(transTaskNode);
	
	// add upload task to UI
	(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_TRANS_TASK_INSERT, 
		transTaskNode.id.id, 
		transTaskNode.id.group, 
		Utility::String::type_to_string<std::wstring>(transTaskNode.id.type), 
		Utility::String::type_to_string<std::wstring>(localFile_->property().size), 
		localFile_->path()));

	// initial the upload patials
	return initPartialIds();
}

int32_t RestUpload::totalUpload()
{
	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	LocalFile* localFile = dynamic_cast<LocalFile*>(localFile_.get());
	
	assert(NULL != restFile && NULL != localFile);

	int32_t ret = localFile->open();
	if (RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return ret;
	}
	
	boost::shared_ptr<unsigned char> buffer((unsigned char*)malloc(TRANSMIT_PART_SIZE), freeWrapper);
	if (NULL == buffer.get())
	{
		ret = RT_MEMORY_MALLOC_ERROR;
		HSLOG_ERROR(MODULE_NAME, ret, "malloc buf of local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return ret;
	}

	memset(buffer.get(), 0, TRANSMIT_PART_SIZE);
	int32_t nRead = localFile->read(0, TRANSMIT_PART_SIZE, buffer.get());
	if (RT_OK != localFile->error())
	{
		ret = localFile->error();
		HSLOG_ERROR(MODULE_NAME, ret, "read local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return ret;
	}

	if (nRead != localFile_->property().size)
	{
		ret = RT_FILE_READ_ERROR;
		HSLOG_ERROR(MODULE_NAME, ret, "read local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return ret;
	}

	ret = restFile->totalUpload(buffer.get(),static_cast<int64_t>(nRead));
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "total upload local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return ret;
	}

	return RT_OK;
}

int32_t RestUpload::startPartialUpload()
{
	// upload the first partial
	partialUpload(false);
	int32_t ret = transTask_->GetErrorCode();
	if (RT_OK != ret)
	{
		return ret;
	}

	if ( NULL != threadGroup_)
	{
		delete threadGroup_;
	}
	threadGroup_ = new boost::thread_group();

	for (int32_t i = 0; i < getThreadNum(); ++i)
	{
		(void)threadGroup_->create_thread(boost::bind(
			&RestUpload::partialUpload, this, true));
	}

	threadGroup_->join_all();

	return RT_OK;
}

int32_t RestUpload::getThreadNum()
{
	// if the speed limit is enable, the thread number is LowTaskThreadNum (1)
	SpeedLimitConf speedLimitConf = userContext_->getConfigureMgr()->getConfigure()->speedLimitConf();
	if (speedLimitConf.useSpeedLimit)
	{
		return LowTaskThreadNum;
	}

	int64_t size = localFile_->property().size;
	if (LowTaskFileSizeLevel >= size)
	{
		return LowTaskThreadNum;
	}
	else if (HightTaskFileSizeLevel >= size)
	{
		return MiddleTaskThreadNum;
	}
	else
	{
		return HightTaskThreadNum;
	}

	return 0;
}

void RestUpload::partialUpload(bool loop)
{
	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	LocalFile* localFile = dynamic_cast<LocalFile*>(localFile_.get());

	assert(NULL != restFile && NULL != localFile);

	int32_t ret = localFile->open();
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "open local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return;
	}
	
	boost::shared_ptr<unsigned char> buffer((unsigned char *)malloc(TRANSMIT_PART_SIZE), freeWrapper);
	if (NULL == buffer.get())
	{
		ret = RT_MEMORY_MALLOC_ERROR;
		HSLOG_ERROR(MODULE_NAME, ret, "malloc buf of local file of %s failed.", 
			Utility::String::wstring_to_string(localFile->path()).c_str());
		transTask_->SetErrorCode(ret);
		return;
	}

	do 
	{
		if (transTask_->IsError() || transTask_->IsCancel())
		{
			return;
		}

		int32_t partialID = getPartialId();

		if (UNUSE_PART_ID == partialID)
		{
			return;
		}

		memset(buffer.get(), 0, TRANSMIT_PART_SIZE);

		int64_t offset = (int64_t)partialID*TRANSMIT_PART_SIZE;
		int32_t nRead = localFile->read(offset, TRANSMIT_PART_SIZE, buffer.get());
		ret = localFile->error();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read local file of %s failed.", 
				Utility::String::wstring_to_string(localFile->path()).c_str());
			transTask_->SetErrorCode(ret);
			return;
		}

		ret = restFile->uploadPart(buffer.get(), static_cast<int64_t>(nRead), partialID); 
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "part upload local file of %s failed.", 
				Utility::String::wstring_to_string(localFile->path()).c_str());
			transTask_->SetErrorCode(ret);
			return;
		}

		// update UI process
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_UPDATE, 
			transTask_->getTaskNode().id.id, 
			transTask_->getTaskNode().id.group, 
			Utility::String::type_to_string<std::wstring>(transTask_->getTaskNode().id.type),
			Utility::String::format_string(L"%1.2f", getProcess()), 
			localFile_->path(), 
			Utility::String::type_to_string<std::wstring>(localFile_->property().size)));

	} while (loop);
}

int32_t RestUpload::completePartialUpload()
{
	if (transTask_->IsError() || transTask_->IsCancel())
	{
		return RT_OK;
	}

	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	assert(NULL != restFile);

	int32_t ret = restFile->completeUploadPart();
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"complete upload local file of %s failed.", 
			Utility::String::wstring_to_string(localFile_->path()).c_str());
		transTask_->SetErrorCode(ret);
		return ret;
	}

	return RT_OK;
}

int32_t RestUpload::getPartialId()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (partialIds_.empty())
	{
		return UNUSE_PART_ID;
	}

	if (curPartialId_ >= (int32_t)partialIds_.size())
	{
		return UNUSE_PART_ID;
	}
	int32_t partId = partialIds_[curPartialId_];
	++curPartialId_;

	return partId;
}

int32_t RestUpload::initPartialIds(const std::vector<int32_t>& completePartialIds)
{
	int64_t size = localFile_->property().size;
	if (TRANSMIT_PART_SIZE >= size)
	{
		return RT_OK;
	}

	int32_t maxPart = (int32_t)(size / TRANSMIT_PART_SIZE);
	if (((int64_t)maxPart*TRANSMIT_PART_SIZE) < size)
	{
		++maxPart;
	}

	for (int32_t i = 0; i < maxPart; ++i)
	{
		partialIds_.push_back(i);
	}

	int32_t index = 0;
	for (std::vector<int32_t>::const_iterator it = completePartialIds.begin(); 
		it != completePartialIds.end(); ++it)
	{
		index = *it;
		if (0 > index || index >= maxPart)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "invalid param, initial upload part ids failed.");
			return RT_INVALID_PARAM;
		}
		partialIds_[index] = UNUSE_PART_ID;
	}
	for (std::vector<int32_t>::iterator it = partialIds_.begin(); it != partialIds_.end();)
	{
		if (UNUSE_PART_ID == (*it))
		{
			it = partialIds_.erase(it);
			continue;
		}
		++it;
	}

	return RT_OK;
}

float RestUpload::getProcess()
{
	boost::mutex::scoped_lock lock(mutex_);
	int64_t size = localFile_->property().size;
	if (size <= TRANSMIT_PART_SIZE)
	{
		return 1.0f;
	}
	int32_t leftPartSize = partialIds_.size() - curPartialId_;
	int64_t leftTransLen = (int64_t)leftPartSize * TRANSMIT_PART_SIZE;
	if (0 != size%TRANSMIT_PART_SIZE)
	{
		leftTransLen = (int64_t)(leftPartSize-1)*TRANSMIT_PART_SIZE+size%TRANSMIT_PART_SIZE;
	}
	if (leftTransLen >= size)
	{
		return 1.0f;
	}
	return ((float)(size - leftTransLen) / size);
}

int64_t RestUpload::getTransLen()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (NULL == localFile_.get())
	{
		return 0;
	}
	int64_t size = localFile_->property().size;
	if (size <= TRANSMIT_PART_SIZE)
	{
		return size;
	}
	if(partialIds_.empty())
	{
		return 0;
	}
	int32_t leftPartSize = partialIds_.size() - curPartialId_;
	int64_t leftTransLen = (int64_t)leftPartSize * TRANSMIT_PART_SIZE;
	if (0 != size%TRANSMIT_PART_SIZE)
	{
		leftTransLen = (int64_t)(leftPartSize-1)*TRANSMIT_PART_SIZE+size%TRANSMIT_PART_SIZE;
	}
	if (leftTransLen >= size)
	{
		return 0;
	}
	return size - leftTransLen;
}