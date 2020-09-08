#ifndef _ONEBOX_LOCAL_MEMORY_FILE_H_
#define _ONEBOX_LOCAL_MEMORY_FILE_H_

#include "CommonDefine.h"
#include "AsyncTaskCommon.h"
#include "IFile.h"
#include "TransTask.h"
#include "Utility.h"

#ifndef MAX_LOCAL_MEMORY_BUF_SIZE
#define MAX_LOCAL_MEMORY_BUF_SIZE (10*1024*1024)
#endif

struct LocalMemoryBuf
{
	unsigned char* buf;
	uint32_t length;

	LocalMemoryBuf()
		:buf(NULL)
		,length(0) { }
};

struct LocalMemoryFileBuf : public LocalMemoryBuf
{
	int64_t start;
	uint32_t offset;

	LocalMemoryFileBuf()
		:start(0)
		,offset(0) { }
};

class LocalMemoryFile
{
public:
	LocalMemoryFile(
		AsyncTransBlock& block, 
		IFile* localFile, 
		ITransmit* transmit, 
		TransTaskErrorCode& errorCode, 
		const LocalMemoryFileBuf& buf)
		:block_(block)
		,localFile_(localFile)
		,transmit_(transmit)
		,errorCode_(errorCode)
		,buf_(buf)
	{
	}

	~LocalMemoryFile()
	{
		if (NULL != buf_.buf)
		{
			delete buf_.buf;
		}
	}

	bool isComplete()
	{
		return ((block_.blockOffset + block_.blockSize) == block_.offset);
	}

	LocalMemoryBuf& getBuf()
	{
		return buf_;
	}

	int32_t read(const uint32_t size, unsigned char* buffer)
	{
		if (size <= 0 || NULL == buffer)
		{
			return 0;
		}
		if (isComplete())
		{
			return 0;
		}

		int32_t bytesRead = size;
		if ((block_.offset + bytesRead) > (block_.blockOffset + block_.blockSize))
		{
			bytesRead = int32_t(block_.blockOffset + block_.blockSize - block_.offset);
		}
		if ((buf_.offset + bytesRead) > buf_.length)
		{
			int32_t bytesPartRead = int32_t(buf_.length - buf_.offset);
			memcpy_s(buffer, size, buf_.buf + buf_.offset, bytesPartRead);
			// read more file content to buffer
			buf_.start = buf_.start + buf_.length;
			buf_.offset = 0;
			buf_.length = MAX_LOCAL_MEMORY_BUF_SIZE;
			if ((buf_.start + buf_.length) > (block_.blockOffset + block_.blockSize))
			{
				buf_.length = uint32_t(block_.blockOffset + block_.blockSize - buf_.start);
			}
			int32_t nRead = localFile_->read(buf_.start, buf_.length, buf_.buf);
			if (RT_OK != localFile_->error() || nRead != buf_.length)
			{
				HSLOG_ERROR("LocalMemoryFile", localFile_->error(), "failed to read local file %s.", 
					SD::Utility::String::wstring_to_string(localFile_->path()).c_str());
				errorCode_.SetErrorCode(RT_FILE_READ_ERROR);
				return 0;
			}
			buf_.length = nRead;
			memcpy_s(buffer + bytesPartRead, size - bytesPartRead, buf_.buf, bytesRead - bytesPartRead);
			buf_.offset += (bytesRead - bytesPartRead);
		}
		else
		{
			memcpy_s(buffer, size, buf_.buf + buf_.offset, bytesRead);
			buf_.offset += bytesRead;
		}

		// update the block offset
		block_.offset += bytesRead;
		// update the progress
		(void)notifyProgress(bytesRead);

		return bytesRead;
	}

	int32_t write(const unsigned char* buffer, const uint32_t size)
	{
		if (size <= 0 || NULL == buffer)
		{
			return 0;
		}
		if (isComplete())
		{
			return 0;
		}
		int32_t bytesWrite = size;
		if ((bytesWrite + buf_.offset) >= buf_.length)
		{
			int32_t bytesPartWrite = buf_.length - buf_.offset;
			memcpy_s(buf_.buf + buf_.offset, buf_.length - buf_.offset, buffer, bytesPartWrite);
			int32_t nWrite = localFile_->write(buf_.buf, buf_.length, buf_.start);
			if (RT_OK != localFile_->error())
			{
				HSLOG_ERROR("LocalMemoryFile", RT_FILE_WRITE_ERROR, "failed to write local file %s.", 
					SD::Utility::String::wstring_to_string(localFile_->path()).c_str());
				errorCode_.SetErrorCode(RT_FILE_WRITE_ERROR);
				return 0;
			}
			// if the write size is not exactly what we want, try again
			if (nWrite != buf_.length)
			{
				int32_t nWriteAgain = localFile_->write(buf_.buf + nWrite, buf_.length - nWrite, buf_.start + nWrite);
				if (RT_OK != localFile_->error() || nWriteAgain <= 0 || (nWriteAgain + nWrite) != buf_.length)
				{
					HSLOG_ERROR("LocalMemoryFile", localFile_->error(), "failed to write local file %s.", 
						SD::Utility::String::wstring_to_string(localFile_->path()).c_str());
					errorCode_.SetErrorCode(RT_FILE_WRITE_ERROR);
					return 0;
				}
			}

			// update the block offset
			block_.offset += buf_.length;

			buf_.start = buf_.start + buf_.length;
			buf_.offset = 0;
			buf_.length = MAX_LOCAL_MEMORY_BUF_SIZE;
			if ((buf_.start + buf_.length) > (block_.blockOffset + block_.blockSize))
			{
				buf_.length = uint32_t(block_.blockOffset + block_.blockSize - buf_.start);
			}
			memcpy_s(buf_.buf, buf_.length, buffer + bytesPartWrite, bytesWrite - bytesPartWrite);
			buf_.offset += bytesWrite - bytesPartWrite;			
		}
		else
		{
			memcpy_s(buf_.buf + buf_.offset, buf_.length - buf_.offset, buffer, bytesWrite);
			buf_.offset += bytesWrite;
			// write the last piece to local file
			if ((block_.blockOffset + block_.blockSize) == (buf_.start + buf_.offset))
			{
				int32_t nWrite = localFile_->write(buf_.buf, buf_.offset, buf_.start);
				if (RT_OK != localFile_->error())
				{
					HSLOG_ERROR("LocalMemoryFile", localFile_->error(), "failed to write local file %s.", 
						SD::Utility::String::wstring_to_string(localFile_->path()).c_str());
					errorCode_.SetErrorCode(RT_FILE_WRITE_ERROR);
					return 0;
				}
				// if the write size is not exactly what we want, try again
				if (nWrite != buf_.offset)
				{
					int32_t nWriteAgain = localFile_->write(buf_.buf + nWrite, buf_.offset - nWrite, buf_.start + nWrite);
					if (nWriteAgain <= 0 || (nWriteAgain + nWrite) != buf_.offset)
					{
						HSLOG_ERROR("LocalMemoryFile", localFile_->error(), "failed to write local file %s.", 
							SD::Utility::String::wstring_to_string(localFile_->path()).c_str());
						errorCode_.SetErrorCode(RT_FILE_WRITE_ERROR);
						return 0;
					}
				}

				// update the block offset
				block_.offset += buf_.offset;
			}
		}

		// update the progress
		// for download, should not update the block information
		(void)notifyProgress(bytesWrite, false);

		return bytesWrite;
	}

private:
	int32_t notifyProgress(size_t size, bool updateBlock = true)
	{
		boost::mutex::scoped_lock lock(mutex_);
		AsyncTransBlocks& transBlocks = transmit_->getTransTask()->getTransDetailNode()->data->blocks;
		
		int64_t transedSize = 0;
		if (updateBlock)
		{
			// update the block offset
			transBlocks.blocks[0].offset += size;
			transedSize = transBlocks.blocks[0].offset;
		}
		else
		{
			for (uint32_t i = 0; i < transBlocks.blockNum; ++i)
			{
				transedSize += (transBlocks.blocks[i].offset - transBlocks.blocks[i].blockOffset);
			}
		}

		ITransmitNotifies notifies = transmit_->getNotifies();
		for (ITransmitNotifies::iterator it = notifies.begin(); it != notifies.end(); ++it)
		{
			(*it)->notifyProgress(transmit_, transedSize, size);
		}
		return RT_OK;
	}

private:
	AsyncTransBlock& block_;
	IFile* localFile_;
	ITransmit* transmit_;
	TransTaskErrorCode& errorCode_;
	LocalMemoryFileBuf buf_;
	boost::mutex mutex_;
};

class LocalMemoryFileFactory
{
public:
	static LocalMemoryFileFactory* create(ITransmit* transmit, TransTaskErrorCode& errorCode)
	{
		LocalMemoryFileFactory* instance = new (std::nothrow)LocalMemoryFileFactory(transmit, errorCode);
		return instance;
	}

	~LocalMemoryFileFactory()
	{
		//for (LocalMemoryFiles::iterator it = localMemoryFiles_.begin(); 
		//	it != localMemoryFiles_.end(); ++it)
		//{
		//	LocalMemoryFile* localMemoryFile = *it;
		//	LocalMemoryBuf& buf = localMemoryFile->getBuf();
		//	if (NULL != buf.buf)
		//	{
		//		delete buf.buf;
		//	}
		//	delete localMemoryFile;
		//}
		//localMemoryFiles_.clear();
	}

	LocalMemoryFile* createLocalMemoryFile(AsyncTransBlock& block, IFile* localFile, bool read = true)
	{
		boost::mutex::scoped_lock lock(mutex_);
		LocalMemoryFileBuf buf;
		//for (LocalMemoryFiles::iterator it = localMemoryFiles_.begin(); 
		//	it != localMemoryFiles_.end(); ++it)
		//{
		//	if ((*it)->isComplete())
		//	{
		//		LocalMemoryBuf& tempBuf = (*it)->getBuf();
		//		buf.buf = tempBuf.buf;
		//		buf.length = tempBuf.length;
		//		localMemoryFiles_.erase(it);
		//		break;
		//	}
		//}
		//if (NULL == buf.buf)
		{
			// keep the buffer size MAX_LOCAL_MEMORY_BUF_SIZE
			// the buffer may be reused
			buf.length = uint32_t(block.blockSize);
			if (buf.length > MAX_LOCAL_MEMORY_BUF_SIZE)
			{
				buf.length = MAX_LOCAL_MEMORY_BUF_SIZE;
			}
			buf.buf = new (std::nothrow)unsigned char[buf.length];
			if (NULL == buf.buf)
			{
				errorCode_.SetErrorCode(RT_MEMORY_MALLOC_ERROR);
				return NULL;
			}
		}
		buf.start = block.offset;

		if (read)
		{
			// read file content to buf
			int32_t bytesRead = localFile->read(buf.start, buf.length, buf.buf);
			if (RT_OK != localFile->error())
			{
				HSLOG_ERROR("LocalMemoryFile", localFile->error(), "failed to read file %s.", 
					SD::Utility::String::wstring_to_string(localFile->path()).c_str());
				errorCode_.SetErrorCode(RT_FILE_READ_ERROR);
				return NULL;
			}
			buf.length = bytesRead;
		}

		LocalMemoryFile* localMemoryFile = new (std::nothrow)LocalMemoryFile(block, localFile, transmit_, errorCode_, buf);
		if (NULL == localMemoryFile)
		{
			errorCode_.SetErrorCode(RT_MEMORY_MALLOC_ERROR);
			return NULL;
		}
		//localMemoryFiles_.push_back(localMemoryFile);
		return localMemoryFile;
	}

protected:
	LocalMemoryFileFactory(ITransmit* transmit, TransTaskErrorCode& errorCode)
		:transmit_(transmit)
		,errorCode_(errorCode)
	{

	}

private:
	ITransmit* transmit_;
	TransTaskErrorCode& errorCode_;
	boost::mutex mutex_;
	typedef std::list<LocalMemoryFile*> LocalMemoryFiles;
	LocalMemoryFiles localMemoryFiles_;
};

#endif
