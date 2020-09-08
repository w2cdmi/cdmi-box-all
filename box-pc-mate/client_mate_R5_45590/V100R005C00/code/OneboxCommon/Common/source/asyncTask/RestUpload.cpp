#include "RestUpload.h"
#include "CommonValue.h"
#include "RestFile.h"
#include "LocalFile.h"
#include "Utility.h"
#include "PathMgr.h"
#include "ConfigureMgr.h"
#include "FilterMgr.h"
#include "TransDataTable.h"
#include "LocalMemoryFile.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestUpload")
#endif

#define INVALID_PART_ID (-1)
#define PART_SPEED_PARAM (60)

struct SpeedUnit
{
	DWORD time;
	DWORD tick;
	int64_t size;

	SpeedUnit()
		:time(0)
		,tick(GetTickCount())
		,size(0) { }
};

class UploadSpeed
{
public:
	UploadSpeed()
		:lastSpeed_(0)
	{

	}

	SpeedUnit getSpeedUnit()
	{
		return SpeedUnit();
	}

	int64_t getSpeed()
	{
		boost::mutex::scoped_lock lock(mutex_);
		return lastSpeed_;
	}

	void updateSpeed(SpeedUnit& speedUnit, const int64_t size)
	{
		boost::mutex::scoped_lock lock(mutex_);
		DWORD tickNow = GetTickCount();
		if (speedUnit.tick >= tickNow)
		{
			return;
		}
		speedUnit.size = size;
		speedUnit.time = tickNow - speedUnit.tick;
		lastSpeed_ = (speedUnit.size * 1000) / speedUnit.time;
	}

private:
	boost::mutex mutex_;
	int64_t lastSpeed_; // unit(B/s)
};

class RestUpload::Impl : public ITransmit
{
public:
	Impl(UserContext* userContext, TransTask* transTask)
		:ITransmit(transTask)
		,errorCode_(transTask)
		,userContext_(userContext)
		,threadGroup_(NULL)
		,remoteFile_(NULL)
		,localFile_(NULL)
		,partCount_(0)
		,localMemoryFileFactory_(LocalMemoryFileFactory::create(this, errorCode_))
		,maxFileSize_(userContext_->getConfigureMgr()->getConfigure()->minPartSize()*
		userContext_->getConfigureMgr()->getConfigure()->maxPartCount())
	{
	}

	virtual void transmit()
	{
		setStatus(TRANSMIT_INIT_START);
		int32_t ret = prepareUpload();
		setStatus(TRANSMIT_INIT_END);
		if (ret == FILE_CREATED)
		{
			errorCode_.SetErrorCode(RT_OK);
			return;
		}
		if(RT_OK != ret)
		{		
			errorCode_.SetErrorCode(ret);
			return;
		}
		setStatus(TRANSMIT_START);
		if (userContext_->getConfigureMgr()->getConfigure()->minPartSize() 
			>= localFile_->property().size)
		{
			ret = totalUpload();
			errorCode_.SetErrorCode(ret);
			return;
		}

		ret = startPartialUpload();
		if(RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}

		ret = completePartialUpload();
		if(RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
		}
	}

	virtual void finishTransmit()
	{
		// update the transmit status
		setStatus(TRANSMIT_END);

		FILE_DIR_INFO lFileDirInfo, rFileDirInfo;

		if (NULL != localFile_.get())
		{
			lFileDirInfo = localFile_->property();
			localFile_->close();
		}
		if (NULL != remoteFile_.get())
		{
			rFileDirInfo = remoteFile_->property();
			remoteFile_->close();
		}

		// transmit has succeed, delete the transmit data information
		if (!transTask_->IsError() && !transTask_->IsCancel())
		{
			(void)serializer_->deleteNode(transTask_->getTransDetailNode()->data->group, 
				transTask_->getTransDetailNode()->data->source);
		}
		else
		{
			// update the upload URL (the upload URL may changed)
			RestFile* restFile = static_cast<RestFile*>(remoteFile_.get());
			if (NULL != restFile)
			{
				AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
				// userDefine = uploadURL/fileID
				if (transDetailNode->data && !transDetailNode->data->userDefine.empty() && 
					INVALID_ID != restFile->property().id)
				{
					transDetailNode->data->userDefine = restFile->uploadURL() + L"/" + 
						Utility::String::type_to_string<std::wstring>(restFile->property().id);
				}
			}
		}

		int32_t ret = notify(lFileDirInfo, rFileDirInfo);
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}
	}

	int32_t notifyProgress(const int64_t transIncement, const int64_t sizeIncrement = 0)
	{
		boost::mutex::scoped_lock lock(mutex_);
		AsyncTransBlocks& transBlocks = transTask_->getTransDetailNode()->data->blocks;

		int64_t transedSize = transIncement;
		if (transBlocks.blockNum != 0 && NULL != transBlocks.blocks)
		{
			// update the block offset
			transBlocks.blocks[0].offset += transIncement;
			transedSize = transBlocks.blocks[0].offset;
		}

		for (ITransmitNotifies::iterator it = notifies_.begin(); it != notifies_.end(); ++it)
		{
			(*it)->notifyProgress(this, transedSize, transIncement, sizeIncrement);
		}
		return RT_OK;
	}

	int32_t notify(const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote)
	{
		int32_t ret = RT_OK;
		for (ITransmitNotifies::iterator it = notifies_.begin(); it != notifies_.end(); ++it)
		{
			ret = (*it)->notify(this, local, remote);
			if (RT_OK != ret)
			{
				errorCode_.SetErrorCode(ret);
				continue;
			}
		}
		return RT_OK;
	}

private:
	int32_t prepareUpload()
	{
		int32_t ret = RT_OK;

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();

		// create local file
		// local path need the real local file path (absolute path)
		Path localPath = userContext_->getPathMgr()->makePath();
		localPath.path(transDetailNode->source);	
		localFile_.reset(static_cast<IFile*>(new LocalFile(localPath, userContext_)));
		if (NULL == localFile_.get())
		{
			return RT_INVALID_PARAM;
		}

		// create remote file
		// remote path need the parent and name
		Path remotePath = userContext_->getPathMgr()->makePath();
		remotePath.parent(Utility::String::string_to_type<int64_t>(transDetailNode->parent));
		remotePath.ownerId(transDetailNode->root->userId);
		remotePath.name(transDetailNode->name);
		remoteFile_.reset(static_cast<IFile*>(new RestFile(remotePath, userContext_)));
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if (NULL == restFile)
		{
			return RT_INVALID_PARAM;
		}

		ret = checkValid();
		if (RT_OK != ret)
		{
			return ret;
		}

		// check if transmit data is exist
		ret = serializer_->getNode(transDetailNode->root->group, transDetailNode->source, transDetailNode->data);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to get data node from database, group is %s, source is %s.", 
				Utility::String::wstring_to_string(transDetailNode->root->group), 
				Utility::String::wstring_to_string(transDetailNode->source));
			return ret;
		}

		// check if the file has changed
		if (isFileChanged())
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "the modify time of local file %s has changed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
		}

		FILE_DIR_INFO fileDirInfo;
		// get size, modify time and create time for prepare upload
		ret = localFile_->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"get property of local file %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return ret;
		}

		if(maxFileSize_ < fileDirInfo.size)
		{
			return RT_DIFF_BIGFILE;
		}

		if (!transDetailNode->data->fingerprint.valid())
		{
			transDetailNode->data->fingerprint.algorithm = userContext_->getConfigureMgr()->getConfigure()->algorithmConf().algorithm;
			ret = getFingerprint(transDetailNode->data->fingerprint, localPath.path(), transTask_);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "get fingerprint of local file %s failed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				return ret;
			}
		}

		// set the fingerprint for prepare upload
		fileDirInfo.fingerprint = transDetailNode->data->fingerprint;
		restFile->property(fileDirInfo, PROPERTY_SIGNATURE);
		restFile->property(fileDirInfo, PROPERTY_SIZE);
		restFile->contentCreate(fileDirInfo.ctime);
		restFile->contentModify(fileDirInfo.mtime);

		// 1. total upload, uploadUrl is exist, can not call prepareUpload
		// 2. part upload, uploadUrl is exist, but transmit blocks is not initial, can not call getUploadParts
		// 3. part upload, uploadUrl is exist, and transmit blocks is initialed, get the uploaded parts
		// if uploadURL is not empty, get uploaded partials
		PartInfoList uploadPartials;
		if (!transDetailNode->data->userDefine.empty())
		{
			// userDefine = uploadURL/fileID
			std::wstring::size_type pos = transDetailNode->data->userDefine.find_last_of(L'/');
			std::wstring uploadURL = transDetailNode->data->userDefine.substr(0, pos);
			int64_t fileID = Utility::String::string_to_type<int64_t>(transDetailNode->data->userDefine.substr(pos+1));

			restFile->uploadURL(uploadURL);
			fileDirInfo.id = fileID;
			restFile->property(fileDirInfo, PROPERTY_ID);
			if(userContext_->getConfigureMgr()->getConfigure()->minPartSize() 
				< localFile_->property().size && transDetailNode->data->blocks.blockNum != 0)
			{
				ret = restFile->getUploadParts(uploadPartials);
				// file may upload success, but transmit task may not, 
				// in this case, re transmit the file
				if (HTTP_NOT_FOUND == ret || HTTP_PRECONDITION_FAILED == ret)
				{
					HSLOG_TRACE(MODULE_NAME, ret, "re-upload file %s.", 
						Utility::String::wstring_to_string(localFile_->path()).c_str());
					(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
					// here should rollback the transmit size
					notifyProgress(-transDetailNode->data->blocks.blocks[0].offset);
					return RT_RETRY;
				}
			}
			// initial the upload partials
			return initTransBlocks(uploadPartials);
		}

		// prepare upload
		UploadType uploadType = (userContext_->getConfigureMgr()->getConfigure()->minPartSize() 
			< localFile_->property().size ? Upload_MultiPart : Upload_Sigle);
		ret = restFile->preUpload(uploadType);
		if (RT_OK != ret)
		{
			return ret;
		}
		// the same file has already exist in remote server
		else if (FILE_CREATED == restFile->error())
		{
			(void)notifyProgress(transDetailNode->size);
			return FILE_CREATED;
		}

		// userDefine = uploadURL/fileID
		transDetailNode->data->userDefine = restFile->uploadURL() + L"/" + 
			Utility::String::type_to_string<std::wstring>(restFile->property().id);
		// save the modify time to check if the file changed when upload next time
		transDetailNode->data->mtime = localFile_->property().mtime;

		// initial the upload partials
		return initTransBlocks(uploadPartials);
	}

	int32_t totalUpload()
	{
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if (NULL == restFile)
		{
			return RT_INVALID_PARAM;
		}

		int32_t ret = localFile_->open();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "open local file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			errorCode_.SetErrorCode(ret);
			return ret;
		}

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		AsyncTransBlock transBlock = transDetailNode->data->blocks.blocks[0];
		std::auto_ptr<LocalMemoryFile> localMemoryFile(localMemoryFileFactory_->createLocalMemoryFile(
			transBlock, localFile_.get()));
		if (NULL == localMemoryFile.get())
		{
			return transTask_->GetErrorCode();
		}
		ret = restFile->totalUpload(uploadCallback, localMemoryFile.get(), transDetailNode->size, progressCallback, transTask_);
		if (HTTP_FORBIDDEN == ret)
		{
			ret = restFile->refreshUploadURL();
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "refresh uploadURL of file %s failed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				errorCode_.SetErrorCode(RT_RETRY);
				(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
				// here should rollback the transmit size
				notifyProgress(-transDetailNode->data->blocks.blocks[0].offset);
				// re-transmit the file
				return RT_RETRY;
			}
			// rollback the transmit size
			notifyProgress(-(transBlock.offset - transBlock.blockOffset));
			// reset the blocks information
			transBlock.offset = 0;
			localMemoryFile.reset(localMemoryFileFactory_->createLocalMemoryFile(
				transBlock, localFile_.get()));
			if (NULL == localMemoryFile.get())
			{
				return transTask_->GetErrorCode();
			}
			// retry
			ret = restFile->totalUpload(uploadCallback, localMemoryFile.get(), transDetailNode->size, progressCallback, transTask_);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "total upload local file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			errorCode_.SetErrorCode(ret);
			(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
			// here should rollback the transmit size
			notifyProgress(-transDetailNode->data->blocks.blocks[0].offset);
			return ret;
		}
		// check if the file has changed
		if (isFileChanged())
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "the modify time of local file %s has changed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			errorCode_.SetErrorCode(RT_RETRY);
			return RT_RETRY;
		}
		return RT_OK;
	}

	int32_t startPartialUpload()
	{
		int32_t ret = localFile_->open();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "open local file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return ret;
		}

		// upload the first partial
		partUpload(false);
		ret = transTask_->GetErrorCode();
		if (RT_OK != ret)
		{
			return ret;
		}

		threadGroup_.reset(new (std::nothrow)boost::thread_group());
		if (NULL == threadGroup_.get())
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		int32_t threadNum = getThreadNum();
		for (int32_t i = 0; i < threadNum; ++i)
		{
			(void)threadGroup_->create_thread(boost::bind(
				&Impl::partUpload, this, true));
		}

		threadGroup_->join_all();

		return RT_OK;
	}

	int32_t getThreadNum()
	{
		Configure* configure = userContext_->getConfigureMgr()->getConfigure();
		if (configure->speedLimitConf().maxUploadSpeed > 0 && 
			configure->speedLimitConf().maxUploadSpeed < configure->multiThreadsSpeedLimit())
		{
			return LowTaskThreadNum;
		}

		int64_t size = localFile_->property().size;
		if (HightTaskFileSizeLevel >= size)
		{
			return LowTaskThreadNum;
		}
		return MiddleTaskThreadNum;
	}

	void partUpload(bool loop)
	{
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if (NULL == restFile)
		{
			errorCode_.SetErrorCode(RT_INVALID_PARAM);
			return;
		}
		do 
		{
			if (transTask_->IsCancel() || transTask_->IsError())
			{
				return;
			}
			AsyncTransBlock transBlock;
			int32_t part = getTransBlock(transBlock);
			if (part == INVALID_PART_ID)
			{
				return;
			}

			std::auto_ptr<LocalMemoryFile> localMemoryFile(localMemoryFileFactory_->createLocalMemoryFile(
				transBlock, localFile_.get()));
			if (NULL == localMemoryFile.get())
			{
				return;
			}
			SpeedUnit speedUnit = speed_.getSpeedUnit();
			int32_t ret = restFile->uploadPart(uploadCallback, localMemoryFile.get(), part, transBlock.blockSize, progressCallback, transTask_);
			speed_.updateSpeed(speedUnit, transBlock.offset - transBlock.blockOffset);
			if (HTTP_FORBIDDEN == ret)
			{
				ret = restFile->refreshUploadURL();
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "refresh uploadURL of file %s failed.", 
						Utility::String::wstring_to_string(localFile_->path()).c_str());
					AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
					(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
					// here should rollback the transmit size
					notifyProgress(-transDetailNode->data->blocks.blocks[0].offset);
					// re-transmit the file
					errorCode_.SetErrorCode(RT_RETRY);
					return;
				}
				// rollback the transmit size
				notifyProgress(-(transBlock.offset - transBlock.blockOffset));
				// reset the blocks information
				transBlock.offset = transBlock.blockOffset;
				localMemoryFile.reset(localMemoryFileFactory_->createLocalMemoryFile(
					transBlock, localFile_.get()));
				if (NULL == localMemoryFile.get())
				{
					return;
				}
				ret = restFile->uploadPart(uploadCallback, localMemoryFile.get(), part, transBlock.blockSize, progressCallback, transTask_);
			}
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "part upload local file of %s failed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				errorCode_.SetErrorCode(ret);
				return;
			}
			// check if the file has changed
			if (isFileChanged())
			{
				HSLOG_EVENT(MODULE_NAME, RT_OK, "the modify time of local file %s has changed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				errorCode_.SetErrorCode(RT_RETRY);
				return;
			}
		} while (loop);
	}

	int32_t completePartialUpload()
	{
		if (transTask_->IsCancel() || transTask_->IsError())
		{
			return RT_OK;
		}

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if(NULL == restFile || transDetailNode->size 
			< userContext_->getConfigureMgr()->getConfigure()->minPartSize())
		{
			return RT_INVALID_PARAM;
		}

		PartList partList;
		for (uint32_t i = 0; i < partCount_; ++i)
		{
			// the remote part is begin with 1
			partList.push_back(i + 1);
		}

		int32_t ret = restFile->completeUploadPart(partList);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"complete upload local file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			errorCode_.SetErrorCode(ret);
			(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
			// here should rollback the transmit size
			notifyProgress(-transDetailNode->data->blocks.blocks[0].offset);
			// if complete failed, re-transmit the file
			return RT_RETRY;
		}

		return RT_OK;
	}

	int32_t getTransBlock(AsyncTransBlock& transBlock)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (uploadParts_.empty())
		{
			return INVALID_PART_ID;
		}
		int32_t part = uploadParts_.front();
		uploadParts_.pop_front();
		AsyncTransBlock& block = transTask_->getTransDetailNode()->data->blocks.blocks[0];
		// last part
		if (part == (partCount_ - 1))
		{
			int64_t size = transTask_->getTransDetailNode()->size;
			if ((partCount_ * block.blockSize) > size)
			{
				transBlock.blockOffset = part * block.blockSize;
				transBlock.offset = transBlock.blockOffset;
				transBlock.blockSize = size - transBlock.blockOffset;
			}
			else
			{
				transBlock.blockSize = block.blockSize;
				transBlock.blockOffset = part * block.blockSize;
				transBlock.offset = transBlock.blockOffset;
			}
		}
		else
		{
			transBlock.blockSize = block.blockSize;
			transBlock.blockOffset = part * block.blockSize;
			transBlock.offset = transBlock.blockOffset;
		}
		return part;
	}

	int32_t checkValid()
	{
		if (Utility::FS::is_local_root(localFile_->path()))
		{
			return RT_OK;
		}
		if (userContext_->getFilterMgr()->isFilter(localFile_->path()))
		{
			return RT_DIFF_FILTER;
		}
		if (userContext_->getFilterMgr()->isKIA(localFile_->path()))
		{
			return RT_DIFF_KIA;
		}
		if (!userContext_->getFilterMgr()->isValid(localFile_->path()))
		{
			return RT_DIFF_INVALID_FILE_NAME;
		}

		return RT_OK;
	}

	int32_t initTransBlocks(const PartInfoList& completePartials)
	{
		int32_t ret = RT_OK;
		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		AsyncTransBlocks& transBlocks = transDetailNode->data->blocks;
		int64_t size = transDetailNode->size;
		if (size <= userContext_->getConfigureMgr()->getConfigure()->minPartSize())
		{
			if (transBlocks.blockNum != 0)
			{
				// here should rollback the transmit size
				(void)notifyProgress(-transBlocks.blocks[0].offset);
				delete transBlocks.blocks;
				transBlocks.blocks = NULL;
			}
			transBlocks.blockNum = 1;
			transBlocks.blocks = new (std::nothrow)AsyncTransBlock[1];
			if (NULL == transBlocks.blocks)
			{
				transBlocks.blockNum = 0;
				return RT_MEMORY_MALLOC_ERROR;
			}
			transBlocks.blocks[0].blockSize = size;
		}
		else if (completePartials.empty())
		{
			if (transBlocks.blockNum != 0)
			{
				// here should rollback the transmit size
				(void)notifyProgress(-transBlocks.blocks[0].offset);
				delete transBlocks.blocks;
				transBlocks.blocks = NULL;
			}
			transBlocks.blockNum = 1;
			transBlocks.blocks = new (std::nothrow)AsyncTransBlock[1];
			if (NULL == transBlocks.blocks)
			{
				transBlocks.blockNum = 0;
				return RT_MEMORY_MALLOC_ERROR;
			}
			int64_t transBlocksSize = speed_.getSpeed() * PART_SPEED_PARAM;
			if (transBlocksSize < userContext_->getConfigureMgr()->getConfigure()->minPartSize())
			{
				transBlocksSize = userContext_->getConfigureMgr()->getConfigure()->minPartSize();
			}
			else if (transBlocksSize > userContext_->getConfigureMgr()->getConfigure()->maxPartSize())
			{
				transBlocksSize = userContext_->getConfigureMgr()->getConfigure()->maxPartSize();
			}
			transBlocks.blocks[0].blockSize = transBlocksSize;
			partCount_ = uint32_t(size / transBlocks.blocks[0].blockSize);
			if ((partCount_ * transBlocks.blocks[0].blockSize) < size)
			{
				++partCount_;
			}
			for (uint32_t i = 0; i < partCount_; ++i)
			{
				uploadParts_.push_back(i);
			}
		}
		else
		{
			if (transBlocks.blockNum == 0 || NULL == transBlocks.blocks)
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "transmit blocks information lost.");
				return RT_ERROR;
			}
			int64_t localTransedSize = transBlocks.blocks[0].offset, remoteTransedSize = 0;

			uint32_t transedMaxPart = 0;
			for (PartInfoList::const_iterator it = completePartials.begin(); 
				it != completePartials.end(); ++it)
			{
				if ((it->partId - 1) > (int32_t)transedMaxPart)
				{
					transedMaxPart = it->partId - 1;
				}
				if (it->size != transBlocks.blocks[0].blockSize)
				{
					// the remote part id is begin with 1
					uploadParts_.push_back(it->partId - 1);
				}
				remoteTransedSize += it->size;
			}
			partCount_ = uint32_t(size / transBlocks.blocks[0].blockSize);
			if ((partCount_ * transBlocks.blocks[0].blockSize) < size)
			{
				++partCount_;
			}
			for (uint32_t i = transedMaxPart + 1; i < partCount_; ++i)
			{
				uploadParts_.push_back(i);
			}
			
			if (localTransedSize != remoteTransedSize)
			{
				// here should rollback the transmit size
				(void)notifyProgress(remoteTransedSize - localTransedSize);
				// update transmit block offset information
				transBlocks.blocks[0].offset = remoteTransedSize;
			}
		}
		ret = serializer_->replaceNode(transTask_->getTransDetailNode()->data);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"init trans blocks information failed.");
			return ret;
		}
		return RT_OK;
	}

	bool isFileChanged()
	{
		if (!localFile_)
		{
			return false;
		}
		FILE_DIR_INFO fileDirInfo;
		int32_t ret = localFile_->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "get property of local file %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return false;
		}
		// file last modify time or file size change
		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		if ((transDetailNode->data && transDetailNode->data->mtime != 0 && 
			transDetailNode->data->mtime != fileDirInfo.mtime) || 
			fileDirInfo.size != transDetailNode->size)
		{
			// re-transmit the file
			(void)serializer_->deleteNode(transDetailNode->root->group, transDetailNode->source);
			// rollback the transmit size and the file size
			int64_t transedSize = 0;
			if (transDetailNode->data->blocks.blockNum > 0)
			{
				transedSize = transDetailNode->data->blocks.blocks[0].offset;
			}
			(void)notifyProgress(-transedSize, fileDirInfo.size - transDetailNode->size);
			// update the file size
			transDetailNode->size = fileDirInfo.size;

			return true;
		}
		return false;
	}

	static int32_t uploadCallback(void *buffer, size_t bufferSize, void *callbackData)
	{
		assert(callbackData);
		assert(buffer);
		DataBuffer* dataBuffer = (DataBuffer*)callbackData;
		LocalMemoryFile* localMemoryFile = (LocalMemoryFile*)dataBuffer->pBuf;
		return localMemoryFile->read(bufferSize, (unsigned char*)buffer);
	}

	static int32_t progressCallback(void *clientp, long long dltotal, 
		long long dlnow, long long ultotal, 
		long long ulnow)
	{
		assert(clientp);
		TransTask* transTask = (TransTask*)clientp;
		if (transTask->IsCancel() || transTask->IsError())
		{
			return RT_CANCEL;
		}
		return RT_OK;
	}

public:
	TransTaskErrorCode errorCode_;
	UserContext* userContext_;
	IFilePtr remoteFile_;
	IFilePtr localFile_;
	std::auto_ptr<boost::thread_group> threadGroup_;
	std::auto_ptr<LocalMemoryFileFactory> localMemoryFileFactory_;
	boost::mutex mutex_;
	uint32_t partCount_;
	PartList uploadParts_;
	int64_t maxFileSize_;
	static UploadSpeed speed_;
};

UploadSpeed RestUpload::Impl::speed_;

RestUpload::RestUpload(UserContext* userContext, TransTask* transTask)
	:ITransmit(transTask)
	,impl_(new Impl(userContext, transTask))
{
	
}

void RestUpload::transmit()
{
	return impl_->transmit();
}

void RestUpload::finishTransmit()
{
	return impl_->finishTransmit();
}

void RestUpload::addNotify(ITransmitNotify* notify)
{
	return impl_->addNotify(notify);
}

void RestUpload::setSerializer(TransSerializer* serializer)
{
	return impl_->setSerializer(serializer);
}