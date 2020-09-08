#include "RestDownload.h"
#include "NotifyMgr.h"
#include "ConfigureMgr.h"
#include "LocalMappingFile.h"
#include "RestFile.h"
#include "PathMgr.h"
#include "DataBaseMgr.h"
#include "Utility.h"
#include "SyncTimeCalc.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestDownload")
#endif

RestDownload::RestDownload(UserContext* userContext, TransTask* transTask)
	:userContext_(userContext)
	,transTask_(transTask)
	,remoteFile_(NULL)
	,localFile_(NULL)
	,threadGroup_(NULL)
{
}

RestDownload::~RestDownload()
{
	if (NULL != threadGroup_)
		delete threadGroup_;
}

void RestDownload::transmit()
{
	//userContext_->getSyncTimeCalc()->startDownloadTimer();
	int32_t ret = prepareDownload();
	if (RT_FILE_EXIST_ERROR == ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}
	if (RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}

	ret = startDownload();
	if (RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}

	ret = completeDownload();
	if (RT_OK != ret)
	{
		transTask_->SetErrorCode(ret);
		return;
	}
}

void RestDownload::finishTransmit()
{
	if ((!transTask_->IsError() && !transTask_->IsCancel()) 
		|| (RT_FILE_EXIST_ERROR == transTask_->GetErrorCode()))
	{
		if (RT_FILE_EXIST_ERROR == transTask_->GetErrorCode())
		{
			transTask_->SetErrorCode(RT_OK);
		}

		if (RT_OK == transTask_->GetErrorCode())
		{
			AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();
			std::wstring newPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(
				Utility::String::string_to_type<int64_t>(transTaskNode.parent)) + PATH_DELIMITER + transTaskNode.name;

			// change the mtime and ctime of the local file
			RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
			assert(NULL != restFile);
			std::auto_ptr<FILE_DIR_INFO> fileDirInfo(new FILE_DIR_INFO);
			int32_t ret = restFile->getContentProperty(*fileDirInfo, PROPERTY_MASK_ALL);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"redirect download file %s failed, failed to get content property.", 
					Utility::String::wstring_to_string(newPath).c_str());
				transTask_->SetErrorCode(ret);
				return;
			}
			(void)localFile_->setProperty(*fileDirInfo, PROPERTY_MASK(PROPERTY_CREATE|PROPERTY_MODIFY));
			// get the file information again to ensure the file information is the same as the file system
			ret = localFile_->getProperty(*(fileDirInfo.get()), PROPERTY_MASK_ALL);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"get download file %s info failed.", 
					Utility::String::wstring_to_string(newPath).c_str());
				transTask_->SetErrorCode(ret);
			}
			else
			{
				transTask_->getCustomInfo().type = FileInfo;
				transTask_->getCustomInfo().content = (void*)fileDirInfo.get();
				transTask_->notifyCustomInfo();

				if (!transTask_->IsError())
				{
					localFile_->close();

					// move file from cache path to local path
					std::wstring oldPath = userContext_->getConfigureMgr()->getConfigure()->cacheDataPath() + PATH_DELIMITER + transTaskNode.id.id;
					ret = Utility::FS::rename(oldPath, newPath);
					if (RT_OK != ret)
					{
						HSLOG_ERROR(MODULE_NAME, ret, 
							"redirect download file %s failed.", 
							Utility::String::wstring_to_string(newPath).c_str());
						transTask_->SetErrorCode(ret);
					}
				}
			}
		}
	}
}

int32_t RestDownload::prepareDownload()
{
	int32_t ret = RT_OK;

	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();

	// create local file
	// local path just neet the full path for download
	Path localPath = userContext_->getPathMgr()->makePath();
	localPath.path(userContext_->getConfigureMgr()->getConfigure()->cacheDataPath() + PATH_DELIMITER + transTaskNode.id.id);
	localFile_ = IFilePtr(static_cast<IFile*>(new LocalMappingFile(localPath, userContext_)));
	assert(NULL != localFile_.get());

	// create remote file
	// remote path need the id
	Path remotePath = userContext_->getPathMgr()->makePath();
	remotePath.id(Utility::String::string_to_type<int64_t>(transTaskNode.id.id));
	remotePath.ownerId(Utility::String::string_to_type<int64_t>(transTaskNode.ownerId));
	remoteFile_ = IFilePtr(static_cast<IFile*>(new RestFile(remotePath, userContext_)));
	RestFile* restFile = dynamic_cast<RestFile*>(remoteFile_.get());
	assert(NULL != restFile);

	// update the file size
	FILE_DIR_INFO fileDirInfo;
	ret = restFile->getProperty(fileDirInfo, PROPERTY_MASK_ALL);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"download failed, get remote file %s content information failed.", 
			Utility::String::wstring_to_string(localFile_->path()).c_str());
		return ret;
	}

	// update local file's size
	localFile_->property(fileDirInfo, PROPERTY_SIZE);

	// add upload task to UI
	std::wstring localRealPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(
		Utility::String::string_to_type<int64_t>(transTaskNode.parent))+PATH_DELIMITER+transTaskNode.name;

	(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_TRANS_TASK_INSERT, 
		transTaskNode.id.id, 
		transTaskNode.id.group, 
		Utility::String::type_to_string<std::wstring>(transTaskNode.id.type), 
		Utility::String::type_to_string<std::wstring>(remoteFile_->property().size), 
		localRealPath));

	// first download and if the block number is not zero and cache file is not exist
	// should compute the block information
	if (0 == transTaskNode.blocks.blockNum || 
		(0 != transTaskNode.blocks.blockNum && !localFile_->isExist()))
	{
		// create an empty file for download
		ret = LocalFile::create(localFile_->path(), localFile_->property().size);
		// if the cache path is removed, call configure()->unserialize() 
		// to rebuild the cache path
		if (ERROR_PATH_NOT_FOUND == ret)
		{
			userContext_->getConfigureMgr()->unserialize();
			ret = LocalFile::create(localFile_->path(), localFile_->property().size);
		}
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"create the empty file %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			return ret;
		}
		computeBlockSize();
	}

	return RT_OK;
}

int32_t RestDownload::startDownload()
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

	threadGroup_ = new boost::thread_group();

	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();
	for (uint32_t i=0; i<transTaskNode.blocks.blockNum; ++i)
	{
		int64_t transLen = transTaskNode.blocks.blocks[i].offset - 
			transTaskNode.blocks.blocks[i].blockOffset;
		// the block has complete
		if (transLen == transTaskNode.blocks.blocks[i].blockSize)
		{
			continue;
		}
		(void)threadGroup_->create_thread(boost::bind(
			&RestDownload::threadDownloadBlock, this, i));
	}

	threadGroup_->join_all();

	return RT_OK;
}

int32_t RestDownload::completeDownload()
{
	localFile_->close();
	return RT_OK;
}

void RestDownload::computeBlockSize()
{
	int32_t threadNum = getThreadNum();
	if ( 0 >= threadNum )
	{
		return;
	}

	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();

	transTaskNode.blocks.blockNum = threadNum;
	transTaskNode.blocks.blocks = new AsyncTransTaskBlock[threadNum];

	int64_t fileSize = remoteFile_->property().size;
	int64_t threadSize = (int64_t)((fileSize/TRANSMIT_PART_SIZE)*TRANSMIT_PART_SIZE/threadNum);  
	int64_t blockOffset = 0;
	int64_t blockSize = 0;
	int32_t blockIndex = 0;

	// compute every single thread and it's offset
	while (fileSize > 0 && blockIndex < threadNum )
	{
		// the last piece
		if (blockIndex == (threadNum -1))
		{
			blockSize = fileSize;
		}
		else
		{
			blockSize = threadSize;
		}
		transTaskNode.blocks.blocks[blockIndex].blockSize = blockSize;
		transTaskNode.blocks.blocks[blockIndex].blockOffset = blockOffset;
		transTaskNode.blocks.blocks[blockIndex].offset = blockOffset;
		fileSize -= blockSize;
		blockOffset += blockSize;
		++blockIndex;
	}
}

void RestDownload::threadDownloadBlock(uint32_t block)
{
	std::wstring localRealPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(
		Utility::String::string_to_type<int64_t>(transTask_->getTaskNode().parent))+PATH_DELIMITER+
		transTask_->getTaskNode().name;

	int32_t ret = RT_ERROR;

	if (block >= transTask_->getTaskNode().blocks.blockNum)
	{
		return;
	}

	uint32_t blockSize = getDownloadBlockSize();
	boost::shared_ptr<unsigned char> buffer((unsigned char *)malloc(blockSize), freeWrapper);
	if (NULL == buffer.get())
	{
		transTask_->SetErrorCode(RT_MEMORY_MALLOC_ERROR);
		return;
	}

	AsyncTransTaskBlock& transTaskBlock = transTask_->getTaskNode().blocks.blocks[block];

	while(true)
	{
		if (transTask_->IsError() || transTask_->IsCancel())
		{
			break;
		}

		// complete the download
		int64_t transLen = transTaskBlock.offset - transTaskBlock.blockOffset;
		if (transLen >= transTaskBlock.blockSize)
		{
			break;
		}

		memset(buffer.get(), 0, blockSize);
		int64_t lessLen = transTaskBlock.blockSize - transLen;
		int64_t readLen = lessLen>blockSize?blockSize:lessLen;
		int32_t nRead = remoteFile_->read(transTaskBlock.offset, (uint32_t)readLen, buffer.get());
		ret = remoteFile_->error();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read remote file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			transTask_->SetErrorCode(ret);
			break;
		}

		uint32_t  nWrite = localFile_->write(buffer.get(), static_cast<uint32_t>(nRead), transTaskBlock.offset);
		ret = localFile_->error();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "write local file of %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			transTask_->SetErrorCode(ret);
			break;
		}

		// update the offset
		transTaskBlock.offset += (int64_t)nWrite;

		// update the database
		ret = userContext_->getDataBaseMgr()->getTransTaskTable()->updateNode(transTask_->getTaskNode());
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "update the task information of local file %s failed.", 
				Utility::String::wstring_to_string(localFile_->path()).c_str());
			transTask_->SetErrorCode(ret);
			break;
		}

		// update UI process
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_UPDATE, 
			transTask_->getTaskNode().id.id, 
			transTask_->getTaskNode().id.group, 
			Utility::String::type_to_string<std::wstring>(transTask_->getTaskNode().id.type),
			Utility::String::format_string(L"%1.2f", getProcess()), 
			localRealPath, 
			Utility::String::type_to_string<std::wstring>(remoteFile_->property().size)));
	}
}

int32_t RestDownload::getThreadNum()
{
	// if the speed limit is enable, the thread number is LowTaskThreadNum (1)
	SpeedLimitConf speedLimitConf = userContext_->getConfigureMgr()->getConfigure()->speedLimitConf();
	if (speedLimitConf.useSpeedLimit)
	{
		return LowTaskThreadNum;
	}

	int64_t size = remoteFile_->property().size;
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

uint32_t RestDownload::getDownloadBlockSize()
{
	int64_t blockSize = TRANSMIT_PART_SIZE;
	int64_t fileSize = remoteFile_->property().size;
	blockSize = (fileSize > blockSize ? blockSize : fileSize);
	return (uint32_t)blockSize;
}

float RestDownload::getProcess()
{
	int64_t fileSize = remoteFile_->property().size;
	if (0 == fileSize)
	{
		return 1.0f;
	}
	AsyncTransTaskBlocks& transTaskBlocks = transTask_->getTaskNode().blocks;
	int64_t transLen = 0;
	for (uint32_t i = 0; i < transTaskBlocks.blockNum; ++i)
	{
		transLen += transTaskBlocks.blocks[i].offset - transTaskBlocks.blocks[i].blockOffset;
	}
	return ((float)transLen / fileSize);
}

int64_t RestDownload::getTransLen()
{
	AsyncTransTaskBlocks& transTaskBlocks = transTask_->getTaskNode().blocks;
	int64_t transLen = 0;
	for (uint32_t i = 0; i < transTaskBlocks.blockNum; ++i)
	{
		transLen += transTaskBlocks.blocks[i].offset - transTaskBlocks.blocks[i].blockOffset;
	}
	return transLen;
}
