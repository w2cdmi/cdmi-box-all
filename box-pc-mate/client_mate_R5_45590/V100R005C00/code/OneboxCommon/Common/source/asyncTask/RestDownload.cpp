#include "RestDownload.h"
#include "CommonValue.h"
#include "LocalMappingFile.h"
#include "RestFile.h"
#include "PathMgr.h"
#include "Utility.h"
#include "FilterMgr.h"
#include "ConfigureMgr.h"
#include "TransDataTable.h"
#include "LocalMemoryFile.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestDownload")
#endif

class RestDownload::Impl : public ITransmit
{
public:
	Impl(UserContext* userContext, TransTask* transTask)
		:ITransmit(transTask)
		,errorCode_(transTask)
		,userContext_(userContext)
		,remoteFile_(NULL)
		,localFile_(NULL)
		,threadGroup_(NULL)
		,localMemoryFileFactory_(LocalMemoryFileFactory::create(this, errorCode_))
	{

	}

	virtual ~Impl()
	{
		if (NULL != threadGroup_)
			delete threadGroup_;

		if( NULL != localMemoryFileFactory_)
		{
			delete localMemoryFileFactory_;
			localMemoryFileFactory_ = NULL;
		}
	}

	virtual void transmit()
	{
		setStatus(TRANSMIT_INIT_START);
		int32_t ret = prepareDownload();
		setStatus(TRANSMIT_INIT_END);
		if (RT_FILE_EXIST_ERROR == ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}
		setStatus(TRANSMIT_START);
		ret = startDownload();
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}

		ret = completeDownload();
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
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
		
		int32_t ret = finishDownload();
		errorCode_.SetErrorCode(ret);

		// transmit has succeed, delete the transmit data information
		if (!transTask_->IsError() && !transTask_->IsCancel())
		{
			(void)serializer_->deleteNode(transTask_->getTransDetailNode()->data->group, 
				transTask_->getTransDetailNode()->data->source);
		}

		ret = notify(lFileDirInfo, rFileDirInfo);
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}
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
	int32_t prepareDownload()
	{
		int32_t ret = RT_OK;

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();

		// create local file
		// local file path need the real local path (absolute path)
		// to avoid cover the old file, add the .tmp extension in the old file name
		// when download success, restore the file name
		Path localPath = userContext_->getPathMgr()->makePath();
		localPath.path(transDetailNode->parent + PATH_DELIMITER + transDetailNode->name + L".tmp");
		localFile_.reset(static_cast<IFile*>(new LocalMappingFile(localPath, userContext_)));
		if (NULL == localFile_.get())
		{
			return RT_INVALID_PARAM;
		}

		// create remote file
		// remote path need the id
		Path remotePath = userContext_->getPathMgr()->makePath();
		remotePath.id(Utility::String::string_to_type<int64_t>(transDetailNode->source));
		remotePath.ownerId(transDetailNode->root->userId);
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

		FILE_DIR_INFO fileDirInfo;
		fileDirInfo.size = transDetailNode->size;
		// update remote file's size
		restFile->property(fileDirInfo, PROPERTY_SIZE);
		// update local file's size
		localFile_->property(fileDirInfo, PROPERTY_SIZE);

		// in following cases should compute the blocks information
		// 1. the block information not exists
		// 2. the block information exists but the local file not exists
		// 3. the block information exists, but the modify time is not the same (file has changed)
		bool computeBlocks = (transDetailNode->data->blocks.blockNum == 0);
		if (!computeBlocks)
		{
			computeBlocks = !localFile_->isExist();
		}
		//if (!computeBlocks)
		//{
		//	ret = restFile->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
		//	if (RT_OK != ret)
		//	{
		//		HSLOG_ERROR(MODULE_NAME, ret, 
		//			"download failed, get remote file %s content information failed.", 
		//			Utility::String::wstring_to_string(localFile_->path()).c_str());
		//		return ret;
		//	}
		//	if (transDetailNode->data->mtime != fileDirInfo.mtime)
		//	{
		//		// the size may changed, update the task size
		//		transDetailNode->size = fileDirInfo.size;
		//		computeBlocks = true;
		//	}
		//}
		if (computeBlocks)
		{
			// update the mtime in data node
			//transDetailNode->data->mtime = fileDirInfo.mtime;

			// create an empty file for download
			ret = LocalFile::create(localFile_->path(), localFile_->property().size);
			//// if the cache path is removed, call configure()->unserialize() 
			//// to rebuild the cache path
			//if (ERROR_PATH_NOT_FOUND == ret)
			//{
			//	userContext_->getConfigureMgr()->unserialize();
			//	ret = LocalFile::create(localFile_->path(), localFile_->property().size);
			//}
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"create the empty file %s failed.", 
					Utility::String::wstring_to_string(localFile_->path()).c_str());
				return ret;
			}
			initTransBlocks();
		}

		return RT_OK;
	}

	int32_t startDownload()
	{
		if ( NULL != threadGroup_)
		{
			delete threadGroup_;
		}

		if (0 == localFile_->property().size)
		{
			return RT_OK;
		}

		if (RT_OK != localFile_->open(OpenWrite))
		{
			return localFile_->error();
		}

		threadGroup_ = new (std::nothrow)boost::thread_group();
		if (NULL == threadGroup_)
		{
			return RT_MEMORY_MALLOC_ERROR;
		}

		(void)serializer_->replaceNode(transTask_->getTransDetailNode()->data);

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		for (uint32_t i = 0; i < transDetailNode->data->blocks.blockNum; ++i)
		{
			int64_t transLen = transDetailNode->data->blocks.blocks[i].offset - 
				transDetailNode->data->blocks.blocks[i].blockOffset;
			// the block has complete
			if (transLen == transDetailNode->data->blocks.blocks[i].blockSize)
			{
				continue;
			}
			(void)threadGroup_->create_thread(boost::bind(
				&Impl::threadDownloadBlock, this, i));
		}

		threadGroup_->join_all();

		return RT_OK;
	}

	int32_t completeDownload()
	{
		localFile_->close();
		return RT_OK;
	}

	void initTransBlocks()
	{
		int32_t threadNum = getThreadNum();
		if (0 >= threadNum)
		{
			return;
		}
		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		AsyncTransBlocks& transBlocks = transDetailNode->data->blocks;
		transBlocks.blockNum = threadNum;
		transBlocks.blocks = new (std::nothrow)AsyncTransBlock[threadNum];
		if (NULL == transBlocks.blocks)
		{
			transBlocks.blockNum = 0;
			errorCode_.SetErrorCode(RT_MEMORY_MALLOC_ERROR);
			return;
		}
		int64_t size = transDetailNode->size;
		int64_t threadSize = size/(int64_t)threadNum, blockOffset = 0, blockSize = 0, blockIndex = 0;
		while (size > 0 && blockIndex < threadNum)
		{
			// the last piece
			if (blockIndex == (threadNum -1))
			{
				blockSize = size;
			}
			else
			{
				blockSize = threadSize;
			}
			transBlocks.blocks[blockIndex].blockSize = blockSize;
			transBlocks.blocks[blockIndex].blockOffset = blockOffset;
			transBlocks.blocks[blockIndex].offset = blockOffset;
			size -= blockSize;
			blockOffset += blockSize;
			++blockIndex;
		}
	}

	void threadDownloadBlock(uint32_t block)
	{
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if (NULL == restFile)
		{
			errorCode_.SetErrorCode(RT_INVALID_PARAM);
			return;
		}
		AsyncTransBlock& transBlock = transTask_->getTransDetailNode()->data->blocks.blocks[block];
		if (transBlock.blockOffset + transBlock.blockSize == transBlock.offset)
		{
			return;
		}
		std::auto_ptr<LocalMemoryFile> localMemoryFile(localMemoryFileFactory_->createLocalMemoryFile(
			transBlock, localFile_.get(), false));
		if (NULL == localMemoryFile.get())
		{
			errorCode_.SetErrorCode(RT_ERROR);
			return;
		}
		(void)restFile->download(downloadCallback, localMemoryFile.get(), transBlock.offset, 
			transBlock.blockOffset + transBlock.blockSize - transBlock.offset, 
			progressCallback, transTask_);
		if (RT_OK != restFile->error())
		{
			HSLOG_ERROR(MODULE_NAME, restFile->error(), "read remote file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			errorCode_.SetErrorCode(restFile->error());
			return;
		}
	}

	int32_t finishDownload()
	{
		int32_t ret = RT_OK;
		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		if (transTask_->IsError() || transTask_->IsCancel())
		{
			// if the file is forbidden by the security matrix 
			// or the user account is disabled
			// delete the file (temporary file)
			if ((SECURITY_MATRIX_FORBIDDEN == transTask_->GetErrorCode() || 
				ACCOUNT_DISABLE == transTask_->GetErrorCode()) && 
				NULL != localFile_.get())
			{
				// 1. rollback the transmit size
				// 2. clear the blocks information and transmit size information
				// 3. remove the local temporary file
				int64_t transedSize = 0;
				for (uint32_t i = 0; i < transDetailNode->data->blocks.blockNum; ++i)
				{
					transedSize += (transDetailNode->data->blocks.blocks[i].offset - 
						transDetailNode->data->blocks.blocks[i].blockOffset);
				}
				for (ITransmitNotifies::iterator it = notifies_.begin(); it != notifies_.end(); ++it)
				{
					(*it)->notifyProgress(this, -transedSize, transDetailNode->size);
				}
				(void)serializer_->deleteNode(transDetailNode->data->group, transDetailNode->data->source);
				ret = localFile_->remove();
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "remove the secury forbidden file %s failed.", 
						Utility::String::wstring_to_string(localFile_->path()).c_str());
				}
			}
			// save the transmit data information
			if(transDetailNode->data)
			{
				(void)serializer_->replaceNode(transDetailNode->data);
			}
			return transTask_->GetErrorCode();
		}

		// change the mtime and ctime of the local file
		RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
		if ( NULL == restFile )
		{
			return RT_INVALID_PARAM;
		}

		FILE_DIR_INFO fileDirInfo;
		ret = restFile->getContentProperty(fileDirInfo, PROPERTY_MASK_ALL);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"redirect download file %s failed, failed to get content property.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return ret;
		}
		(void)localFile_->setProperty(fileDirInfo, PROPERTY_MASK(PROPERTY_CREATE|PROPERTY_MODIFY));

		// restore the file name
		ret = Utility::FS::rename(localFile_->path(), transDetailNode->parent + PATH_DELIMITER + transDetailNode->name);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"redirect download file %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return ret;
		}

		return ret;
	}

	int32_t getThreadNum()
	{
		Configure* configure = userContext_->getConfigureMgr()->getConfigure();
		if (configure->speedLimitConf().maxDownloadSpeed > 0 && 
			configure->speedLimitConf().maxDownloadSpeed < configure->multiThreadsSpeedLimit())
		{
			return LowTaskThreadNum;
		}

		int64_t size = transTask_->getTransDetailNode()->size;
		if (HightTaskFileSizeLevel >= size)
		{
			return LowTaskThreadNum;
		}
		return MiddleTaskThreadNum;
	}

	int32_t checkValid()
	{
		if (userContext_->getFilterMgr()->isFilter(transTask_->getTransDetailNode()->name))
		{
			return RT_DIFF_FILTER;
		}
		return RT_OK;
	}

	static int32_t downloadCallback(void *buffer, size_t bufferSize, void *callbackData)
	{
		assert(callbackData);
		assert(buffer);
		DataBuffer* dataBuffer = (DataBuffer*)callbackData;
		LocalMemoryFile* localMemoryFile = (LocalMemoryFile*)dataBuffer->pBuf;
		return localMemoryFile->write((unsigned char*)buffer, bufferSize);
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

private:
	TransTaskErrorCode errorCode_;
	UserContext* userContext_;
	IFilePtr remoteFile_;
	IFilePtr localFile_;
	boost::thread_group* threadGroup_;
	boost::mutex mutex_;
	LocalMemoryFileFactory *localMemoryFileFactory_;
};

RestDownload::RestDownload(UserContext* userContext, TransTask* transTask)
	:ITransmit(transTask)
	,impl_(new Impl(userContext, transTask))
{
}

void RestDownload::transmit()
{
	return impl_->transmit();
}

void RestDownload::finishTransmit()
{
	return impl_->finishTransmit();
}

void RestDownload::addNotify(ITransmitNotify* notify)
{
	return impl_->addNotify(notify);
}

void RestDownload::setSerializer(TransSerializer* serializer)
{
	return impl_->setSerializer(serializer);
}
