#include "LocalMappingFile.h"
#include "Utility.h"
#include <boost/thread.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalMappingFile")
#endif

LocalMappingFile::LocalMappingFile(const Path& path, UserContext* userContext)
	:LocalFile(path, userContext)
	,fileHandle_(INVALID_HANDLE_VALUE)
	,mappingHandle_(NULL)
{
	memset(&si_, 0, sizeof(SYSTEM_INFO));
}

LocalMappingFile::~LocalMappingFile()
{
	try
	{
		close();
	}
	catch(...){}
}

int32_t LocalMappingFile::open(const OpenMode mode)
{
	boost::mutex::scoped_lock lock(mutex_);
	if (path_.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_ERROR;

	GetSystemInfo(&si_);

	DWORD accessFlag = (OpenRead == mode)?GENERIC_READ:GENERIC_READ|GENERIC_WRITE;
	DWORD shareFlag = (OpenRead == mode)?FILE_SHARE_READ:FILE_SHARE_READ|FILE_SHARE_WRITE;

	fileHandle_ = CreateFile(std::wstring(L"\\\\?\\"+path_).c_str(), 
		accessFlag, 
		shareFlag, 
		NULL, 
		OPEN_EXISTING, 
		FILE_ATTRIBUTE_NORMAL, 
		NULL);   
	if (INVALID_HANDLE_VALUE == fileHandle_) 
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "open local mapping file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	// Create the file-mapping object.
	DWORD pageFlag = (OpenRead == mode)?PAGE_READONLY:PAGE_READWRITE;
	mappingHandle_ = CreateFileMapping(fileHandle_, 
		NULL, 
		pageFlag, 
		(DWORD)(property_.size>>32), 
		(DWORD)(property_.size&0xFFFFFFFF), 
		NULL);  
	if (NULL == mappingHandle_) 
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "create local mapping file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	return RT_OK;
}

int32_t LocalMappingFile::close()
{
	mappingHandle_.CloseHandle();
	fileHandle_.CloseHandle();
	return RT_OK;
}

int32_t LocalMappingFile::read(const int64_t& offset, const uint32_t size, unsigned char* buffer)
{
	return RT_NOT_IMPLEMENT;
}

int32_t LocalMappingFile::write(const unsigned char* buffer, const uint32_t size, const int64_t& offset)
{
	boost::mutex::scoped_lock lock(mutex_);

	if (NULL == mappingHandle_ || 0 == si_.dwAllocationGranularity)
	{
		error(RT_FILE_OPEN_ERROR);
		return RT_FILE_OPEN_ERROR;
	}

	if (NULL == buffer)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_ERROR;

	int64_t remainSize = property_.size - offset;
	if (0 == remainSize)
	{
		return RT_OK;
	}

	int64_t writeSize = remainSize>size?size:remainSize;  
	int64_t alignSize  = offset%si_.dwAllocationGranularity;
	int64_t offsetPos = offset-alignSize;
	uint32_t mapLength = (uint32_t)(writeSize+alignSize);

	LPVOID fileMappingBuf = MapViewOfFile(mappingHandle_, 
		FILE_MAP_WRITE, 
		(DWORD)(offsetPos>>32), 
		(DWORD)(offsetPos&0xffffffff), 
		mapLength);
	if (NULL == fileMappingBuf)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "mapping local file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	memcpy((char*)fileMappingBuf+alignSize, buffer, (size_t)writeSize);

	RETRY(5)
	{
		if (FlushViewOfFile((char*)fileMappingBuf+alignSize, (SIZE_T)writeSize))
		{
			break;
		}
		boost::this_thread::sleep(boost::posix_time::milliseconds(100));
	}
	
	ret = GetLastError();
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "flush local mapping file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	if (!UnmapViewOfFile(fileMappingBuf))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "unmapping local file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	return (int32_t)writeSize;
}