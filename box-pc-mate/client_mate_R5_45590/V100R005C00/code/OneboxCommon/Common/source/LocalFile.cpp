#include "LocalFile.h"
#include "Utility.h"
#include "SmartHandle.h"
#include <windows.h>
#include <stdio.h>
#include <io.h>
#include <WinIoCtl.h>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalFile")
#endif

LocalFile::LocalFile(const Path& path, UserContext* userContext)
	:IFile(path)
	,userContext_(userContext)
	,file_(INVALID_HANDLE_VALUE)
	,fd_(-1)
{
}

LocalFile::~LocalFile()
{
	try
	{
		close();
	}
	catch(...){}
}

int32_t LocalFile::remove()
{
	if (path_.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	if (!Utility::FS::is_exist(path_))
	{
		error(RT_FILE_NOEXIST_ERROR);	
		HSLOG_ERROR(MODULE_NAME, RT_FILE_NOEXIST_ERROR, 
			"remove local file %s failed, file not exist.", 
			Utility::String::wstring_to_string(path_).c_str());
		return RT_FILE_NOEXIST_ERROR;
	}

	int32_t ret = Utility::FS::remove(path_);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"remove local file %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		return ret;
	}

	return RT_OK;
}

int32_t LocalFile::rename(const std::wstring& new_name)
{
	if (path_.empty() || new_name.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	if (Utility::FS::get_file_name(path_) == new_name)
	{
		error(RT_FILE_EXIST_ERROR);
		HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR, 
			"rename local file %s failed, same path exist.", 
			Utility::String::wstring_to_string(path_).c_str());
		return RT_FILE_EXIST_ERROR;
	}

	std::wstring newPath = Utility::FS::get_parent_path(path_) + PATH_DELIMITER + new_name;
	int32_t ret = Utility::FS::rename(path_, newPath);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"rename local file %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		return ret;
	}

	property_.name = new_name;

	return RT_OK;
}

int32_t LocalFile::copy(const Path& new_parent, bool autoRename)
{
	return RT_NOT_IMPLEMENT;
}

int32_t LocalFile::move(const Path& new_parent, bool autoRename)
{
	std::wstring newParent = new_parent.path();
	if (path_.empty() || newParent.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	if (Utility::FS::get_parent_path(path_) == newParent)
	{
		error(RT_FILE_EXIST_ERROR);
		HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR, 
			"move local file %s failed, same path exist.", 
			Utility::String::wstring_to_string(path_).c_str());
		return RT_FILE_EXIST_ERROR;
	}

	std::wstring newPath = newParent + PATH_DELIMITER + Utility::FS::get_file_name(path_);
	int32_t ret = Utility::FS::rename(path_, newPath);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"move local file %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		return ret;
	}

	property_.parent = new_parent.parent();

	return RT_OK;
}

bool LocalFile::isExist()
{
	if (path_.empty())
	{
		error(RT_INVALID_PARAM);
		return false;
	}

	return Utility::FS::is_exist(path_);
}

int32_t LocalFile::open(const OpenMode mode)
{
	if (path_.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	if (INVALID_HANDLE_VALUE != file_)
	{
		return RT_OK;
	}

	int32_t ret = RT_OK;
	DWORD dwDesiredAccess = GENERIC_READ;
	DWORD dwShareMode = FILE_SHARE_READ;
	if (OpenWrite == mode)
	{
		dwDesiredAccess |= GENERIC_WRITE;
	}
	else if (OpenRead == mode)
	{
		dwShareMode |= FILE_SHARE_WRITE;
	}
	file_ = CreateFile(std::wstring(L"\\\\?\\"+path_).c_str(), 
		dwDesiredAccess, 
		dwShareMode, 
		NULL, 
		OPEN_EXISTING, 
		FILE_ATTRIBUTE_NORMAL, 
		NULL);
	if (INVALID_HANDLE_VALUE == file_)
	{
		HSLOG_ERROR(MODULE_NAME, GetLastError(), "open file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(RT_FILE_OPEN_ERROR);
		return RT_FILE_OPEN_ERROR;
	}

	return RT_OK;

	/*if (NULL != file_)
	{
	return RT_OK;
	}

	std::wstring openMode = L"rb";
	int flag = _SH_DENYWR;
	if (OpenWrite == mode)
	{
	openMode = L"wb";
	}
	file_ = _wfsopen(path_.c_str(),openMode.c_str(),flag);
	if (NULL == file_)
	{
	error(RT_FILE_OPEN_ERROR);
	return RT_FILE_OPEN_ERROR;
	}

	return RT_OK;*/
}

int32_t LocalFile::close()
{
	file_.CloseHandle();
	return RT_OK;
}

int32_t LocalFile::read(const int64_t& offset, const uint32_t size, unsigned char* buffer)
{
	boost::mutex::scoped_lock lock(mutex_);

	if (INVALID_HANDLE_VALUE == file_)
	{
		error(RT_FILE_OPEN_ERROR);
		return RT_FILE_OPEN_ERROR;
	}

	if (NULL == buffer)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}
	
	int32_t ret = RT_OK;
	LARGE_INTEGER tempOffset;
	tempOffset.LowPart = (DWORD)offset&0xffffffff;
	tempOffset.HighPart = (DWORD)(offset>>32);
	if (!SetFilePointerEx(file_, 
		tempOffset, 
		NULL, 
		FILE_BEGIN))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "seek file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	DWORD dwRead = 0, dwTempRead = 0, dwReadSize = size;
	do
	{
		if (!ReadFile(file_, buffer, dwReadSize, &dwTempRead, NULL))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "read file of %s failed.", 
				Utility::String::wstring_to_string(path_).c_str());
			error(ret);
			return ret;
		}
		dwReadSize -= dwTempRead;
		dwRead += dwTempRead;
	}while (0 < dwReadSize && 0 < dwTempRead);

	return dwRead;	

	/*if (NULL == file_)
	{
	error(RT_FILE_OPEN_ERROR);
	return RT_FILE_OPEN_ERROR;
	}

	if (NULL == buffer)
	{
	error(RT_INVALID_PARAM);
	return RT_INVALID_PARAM;
	}

	if (RT_OK != _fseeki64(file_, offset, SEEK_SET))
	{
	error(RT_FILE_SEEK_ERROR);
	return RT_FILE_SEEK_ERROR;
	}

	uint32_t nRead = 0;
	while (nRead < size)
	{
	nRead += (int32_t)fread(buffer + nRead, sizeof(unsigned char), size - nRead, file_);
	if (feof(file_) != 0)
	{
	break;
	}
	if (ferror(file_) != 0)
	{
	error(RT_FILE_READ_ERROR);
	return RT_FILE_READ_ERROR;
	}
	}

	return nRead;*/
}

int32_t LocalFile::write(const unsigned char* buffer, const uint32_t size, const int64_t& offset)
{
	boost::mutex::scoped_lock lock(mutex_);

	if (INVALID_HANDLE_VALUE == file_)
	{
		error(RT_FILE_OPEN_ERROR);
		return RT_FILE_OPEN_ERROR;
	}

	if (NULL == buffer)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	LARGE_INTEGER tempOffset;
	tempOffset.LowPart = (DWORD)offset&0xffffffff;
	tempOffset.HighPart = (DWORD)(offset>>32);
	if (!SetFilePointerEx(file_, 
		tempOffset, 
		NULL, 
		FILE_BEGIN))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "seek file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	DWORD dwWrite = 0;
	if (!WriteFile(file_, buffer, size, &dwWrite, NULL))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "write file of %s failed.", 
			Utility::String::wstring_to_string(path_).c_str());
		error(ret);
		return ret;
	}

	return dwWrite;

	/*if (NULL == file_)
	{
	error(RT_FILE_OPEN_ERROR);
	return RT_FILE_OPEN_ERROR;
	}

	if (NULL == buffer)
	{
	error(RT_INVALID_PARAM);
	return RT_INVALID_PARAM;
	}

	if (RT_OK !=  _fseeki64(file_, offset, SEEK_SET))
	{
	error(RT_FILE_SEEK_ERROR);
	return RT_FILE_SEEK_ERROR;
	}

	uint32_t nWrite = (uint32_t)fwrite(buffer, sizeof(unsigned char), size, file_);
	if (ferror(file_) != 0)
	{
	error(RT_FILE_WRITE_ERROR);
	return RT_FILE_WRITE_ERROR;
	}

	return nWrite;*/
}

int32_t LocalFile::getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	if (path_.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}
	
	FILE_DIR_INFO fileDirInfo = getPropertyByPath(path_);

	property.type = fileDirInfo.type;
	property_.type = property.type;

	if (PROPERTY_ID&mask)
	{
		property.id = fileDirInfo.id;
		property_.id = property.id;
	}
	if (PROPERTY_SIZE&mask)
	{
		property.size = fileDirInfo.size;
		property_.size = property.size;
	}
	if (PROPERTY_CREATE&mask)
	{
		property.ctime = fileDirInfo.ctime;
		property_.ctime = property.ctime;
	}
	if (PROPERTY_MODIFY&mask)
	{
		property.mtime = fileDirInfo.mtime;
		property_.mtime = property.mtime;
	}

	return RT_OK;
}

int32_t LocalFile::setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	if (PROPERTY_CREATE&mask && PROPERTY_MODIFY&mask)
	{
		if (0 >= property.ctime || 0 >= property.mtime)
		{
			return RT_INVALID_PARAM;
		}

		FILETIME ctime, mtime;
		ctime.dwLowDateTime = property.ctime&0xffffffff;
		ctime.dwHighDateTime = (property.ctime>>32)&0xffffffff;
		mtime.dwLowDateTime = property.mtime&0xffffffff;
		mtime.dwHighDateTime = (property.mtime>>32)&0xffffffff;

		int32_t ret = RT_OK;

		SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path_).c_str(), 
			GENERIC_READ|GENERIC_WRITE, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL, 
			OPEN_ALWAYS, 
			FILE_ATTRIBUTE_NORMAL, 
			NULL);
		if (INVALID_HANDLE_VALUE == hFile)
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "CreateFile %s failed.", 
				Utility::String::wstring_to_string(path_).c_str());
			return ret;
		}
		if (!SetFileTime(hFile, &ctime, NULL, &mtime))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, 
				"set local file of %s attributes failed.", 
				Utility::String::wstring_to_string(path_).c_str());
			return ret;
		}
	}
	return RT_OK;
}

int32_t LocalFile::create(const std::wstring& path, const int64_t& size)
{
	if (path.empty() || size < 0L)
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	SmartHandle hFile = CreateFile((L"\\\\?\\"+path).c_str(), 
		GENERIC_READ|GENERIC_WRITE, 
		FILE_SHARE_READ|FILE_SHARE_WRITE, 
		NULL, 
		OPEN_ALWAYS, 
		FILE_ATTRIBUTE_NORMAL, 
		NULL);
	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "CreateFile %s failed.", 
			Utility::String::wstring_to_string(path).c_str());
		return ret;
	}

	LARGE_INTEGER offset;
	offset.LowPart = (DWORD)(size&0xffffffff);
	offset.HighPart = (DWORD)(size>>32);
	if (!SetFilePointerEx(hFile, offset, NULL, FILE_BEGIN))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "SetFilePointerEx %s failed.", 
			Utility::String::wstring_to_string(path).c_str());
		return ret;
	}

	if (!SetEndOfFile(hFile))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "SetEndOfFile %s failed.", 
			Utility::String::wstring_to_string(path).c_str());
		return ret;
	}

	return ret;
}

FILE_DIR_INFO LocalFile::getPropertyByPath(const std::wstring& path)
{
	int32_t ret = RT_OK;
	FILE_DIR_INFO property;
	if (path.empty())
	{
		return property;
	}

	DWORD attr = FILE_ATTRIBUTE_NORMAL;
	if (Utility::FS::is_local_root(path)||Utility::FS::is_directory(path))
	{
		property.type = FILE_TYPE_DIR;
		attr |= FILE_FLAG_BACKUP_SEMANTICS;
	}
	SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(), 
		GENERIC_READ, 
		FILE_SHARE_READ|FILE_SHARE_WRITE, 
		NULL, 
		OPEN_EXISTING, 
		attr, 
		NULL);
	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get id failed, open handle failed.");
		return property;
	}

	BY_HANDLE_FILE_INFORMATION bhfi;
	(void)memset_s(&bhfi, sizeof(BY_HANDLE_FILE_INFORMATION), 0, sizeof(BY_HANDLE_FILE_INFORMATION));

	if (!GetFileInformationByHandle(hFile, &bhfi))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"GetFileInformationByHandle failed.");
		return property;
	}

	int64_t id = bhfi.nFileIndexHigh;
	id = (id<<32)+bhfi.nFileIndexLow;
	property.id = id;
	int64_t ctime = bhfi.ftCreationTime.dwHighDateTime;
	ctime = (ctime<<32)+bhfi.ftCreationTime.dwLowDateTime;
	property.ctime = ctime;
	int64_t mtime = bhfi.ftLastWriteTime.dwHighDateTime;
	mtime = (mtime<<32)+bhfi.ftLastWriteTime.dwLowDateTime;
	property.mtime = mtime;
	int64_t size = bhfi.nFileSizeHigh;
	size = (size<<32)+bhfi.nFileSizeLow;
	property.size = size;

	return property;
}

bool LocalFile::isOpen(const std::wstring& path)
{
	//if (path.empty())
	//{
	//	return false;
	//}

	//FILE *pFile = NULL;

	//if (Utility::FS::is_exist(path))
	//{
	//	// here to erase the file's hidden attribute
	//	DWORD dwAttribute = GetFileAttributes(path.c_str());
	//	if (INVALID_FILE_ATTRIBUTES == dwAttribute)
	//	{
	//		return false;
	//	}
	//	if (FILE_ATTRIBUTE_READONLY&dwAttribute)
	//	{
	//		SetFileAttributes(path.c_str(), dwAttribute&(~FILE_ATTRIBUTE_READONLY));
	//	}
	//	pFile = _wfsopen(path.c_str(),L"rb",_SH_DENYRW);
	//}
	//else
	//{
	//	return false;
	//}
	//if (NULL == pFile)
	//{
	//	return true;
	//}

	//fclose(pFile);

	return false;
}

int64_t LocalFile::li64toll(const LARGE_INTEGER& li64)
{
	int64_t ll_ = 0L;
	ll_ = li64.HighPart;
	ll_ = (ll_<<32)+li64.LowPart;
	return ll_;
}

int64_t LocalFile::filetimetoll(const FILETIME& filetime)
{
	int64_t ll_ = 0L;
	ll_ = filetime.dwHighDateTime;
	ll_ = (ll_<<32)+filetime.dwLowDateTime;
	return ll_;
}
