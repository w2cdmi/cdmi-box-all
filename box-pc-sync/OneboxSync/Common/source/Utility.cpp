#pragma warning(disable:4996) // disable 4996 warning

#define _CRT_RAND_S
#include <stdlib.h>
#include "Utility.h"

#ifdef ENABLE_UTILITY_STRING
#include <windows.h>
#include <sstream>
#include <boost/algorithm/string.hpp>
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_generators.hpp>
#include <boost/uuid/uuid_io.hpp>
#endif

#ifdef ENABLE_UTILITY_FS
#include <sys/stat.h> 
#include <time.h>
#ifdef USE_BOOST_FS
#include <boost/filesystem.hpp>
namespace fs=boost::filesystem;
#else
#include <windows.h>
#endif
#define PATH_CONFLICT_SUFFIX (L".conflict")
#endif

#ifdef ENABLE_UTILITY_MD5
#include "md5.h"
#endif

#ifdef ENABLE_UTILITY_SHA1
#include "CSha1.h"
#endif

#ifdef ENABLE_UTILITY_ENCRYPT
#include "Aes.h"
static const char* default_password_key = "$B@C#CF+A&2_7%97~93()C4!90^A862*";
#endif

#ifdef ENABLE_UTILITY_FS
int32_t SD::Utility::FS::remove(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		fs::remove(Path);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	if (path.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}

	if (3 >= path.length())
	{
		return ERROR_ACCESS_DENIED;
	}
	std::wstring sys_path = std::wstring(L"\\\\?\\"+path);
	DWORD attrib = GetFileAttributes(sys_path.c_str());
	if (INVALID_FILE_ATTRIBUTES == attrib)
	{
		return GetLastError();
	}
	// delete directory
	if (FILE_ATTRIBUTE_DIRECTORY&attrib)
	{
		if (!RemoveDirectory(sys_path.c_str()))
		{
			return GetLastError();
		}
	}
	// delete file
	else
	{		
		if (!DeleteFile(sys_path.c_str()))
		{
			return GetLastError();
		}
	}
	return ERROR_SUCCESS;
#endif
}

#ifndef USE_BOOST_FS
static int32_t remove_directories(const std::wstring& dir)
{
	WIN32_FIND_DATA wfd;
	HANDLE hFind = FindFirstFile(std::wstring(dir+L"\\*").c_str(),&wfd);
	if(hFind==INVALID_HANDLE_VALUE)
	{
		return GetLastError();
	}
	std::wstring tempName = L"";
	bool is_child_failed = false;
	bool isOK = false;
	while(FindNextFile(hFind, &wfd))
	{
		tempName = wfd.cFileName;
		if (L"." == tempName || L".." == tempName)
		{
			continue;
		}
		// delete file
		if (!(wfd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY))
		{
			// if error occur, delete the next
			isOK = false;
			// retry for 5 times
			for (int32_t i = 0; i < 5; ++i)
			{
				if (DeleteFile((dir+L"\\"+tempName).c_str()))
				{
					isOK = true;
					break;
				}
			}
			if (!isOK)
			{
				is_child_failed = true;
			}
		}
		// recurse to delete directory
		else if (wfd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)
		{
			if (ERROR_SUCCESS != remove_directories(dir+L"\\"+tempName))
			{
				is_child_failed = true;
			}
		}
	}
	FindClose(hFind);

	// delete self
	// retry for 5 times
	isOK = false;
	for (int32_t i = 0; i < 5; ++i)
	{
		if (RemoveDirectory(dir.c_str()))
		{
			isOK = true;
			break;
		}
	}
	if (!isOK)
	{
		return GetLastError();
	}
	return ERROR_SUCCESS;
}
#endif

int32_t SD::Utility::FS::remove_all(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		fs::remove_all(Path);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	if (path.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}

	if (3 >= path.length())
	{
		return ERROR_ACCESS_DENIED;
	}
	std::wstring sys_path = std::wstring(L"\\\\?\\"+path);
	DWORD attrib = GetFileAttributes(sys_path.c_str());
	if (INVALID_FILE_ATTRIBUTES == attrib)
	{
		return GetLastError();
	}
	// delete directories
	if (FILE_ATTRIBUTE_DIRECTORY&attrib)
	{
		return remove_directories(sys_path);
	}
	// delete file
	else
	{		
		if (!DeleteFile(sys_path.c_str()))
		{
			return GetLastError();
		}
	}
	return ERROR_SUCCESS;
#endif
}

int32_t SD::Utility::FS::rename(const std::wstring& oldpath, const std::wstring& newpath)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath oldPath(oldpath);
		fs::wpath newPath(newpath);
		fs::rename(oldpath, newpath);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	if (oldpath.empty() || newpath.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}
	if (!MoveFile(std::wstring(L"\\\\?\\"+oldpath).c_str(), std::wstring(L"\\\\?\\"+newpath).c_str()))
	{
		return GetLastError();
	}
	return ERROR_SUCCESS;
#endif
}

int32_t SD::Utility::FS::create_directory(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		fs::create_directory(Path);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	if (path.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}
	if (3 >= path.length())
	{
		UINT value = GetDriveType(std::wstring(path+L"\\").c_str());
		if (DRIVE_UNKNOWN == value || DRIVE_NO_ROOT_DIR == value)
		{
			return ERROR_PATH_NOT_FOUND;
		}
		return ERROR_SUCCESS;
	}
	if (!CreateDirectory(std::wstring(L"\\\\?\\"+path).c_str(), NULL))
	{
		return GetLastError();
	}
	return ERROR_SUCCESS;
#endif
}

int32_t SD::Utility::FS::create_directories(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		fs::create_directories(Path);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	if (path.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}
	int32_t ret = ERROR_SUCCESS;
	if (3 >= path.length())
	{
		UINT value = GetDriveType(std::wstring(path+L"\\").c_str());
		if (DRIVE_UNKNOWN == value || DRIVE_NO_ROOT_DIR == value)
		{
			return ERROR_PATH_NOT_FOUND;
		}
		return ERROR_SUCCESS;
	}
	std::wstring sys_path = std::wstring(L"\\\\?\\"+path);
	DWORD attrib = GetFileAttributes(sys_path.c_str());
	// if directory exist, return OK
	if (INVALID_FILE_ATTRIBUTES != attrib)
	{
		return ERROR_SUCCESS;
	}
	// if directory is not exist, create the parent first
	ret = GetLastError();
	if (ERROR_PATH_NOT_FOUND!=ret && ERROR_FILE_NOT_FOUND!=ret)
	{
		return ret;
	}
	std::wstring parent = get_parent_name(path);
	if (parent.empty())
	{
		return ERROR_PATH_NOT_FOUND;
	}
	ret = create_directories(parent);
	if (ERROR_SUCCESS != ret)
	{
		return ret;
	}
	if (!CreateDirectory(sys_path.c_str(), NULL))
	{
		ret = GetLastError();
		if (ERROR_FILE_EXISTS == ret)
		{
			return ERROR_SUCCESS;
		}
		return ret;
	}
	return ERROR_SUCCESS;
#endif
}

int32_t SD::Utility::FS::copy_file(const std::wstring& oldpath, const std::wstring& newpath)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath oldPath(oldpath);
		fs::wpath newPath(newpath);
		fs::copy_file(oldpath, newpath);
	}
	catch(boost::system::system_error& e)
	{
		return e.code().value();
	}
	return 0;
#else
	return ERROR_NOT_SUPPORTED;
#endif
}

bool SD::Utility::FS::is_directory(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		return fs::is_directory(Path);
	}
	catch(boost::system::system_error& e)
	{
		(void)e;
		return false;
	}
	return false;
#else
	if (path.empty())
	{
		return false;
	}
	DWORD attrib = GetFileAttributes(std::wstring(L"\\\\?\\"+path).c_str());
	if (INVALID_FILE_ATTRIBUTES == attrib)
	{
		return false;
	}
	else
	{
		return (0 != (attrib&FILE_ATTRIBUTE_DIRECTORY));
	}
#endif
}

bool SD::Utility::FS::is_exist(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		return fs::exists(Path);
	}
	catch(boost::system::system_error& e)
	{
		(void)e;
		return false;
	}
	return false;
#else
	if (path.empty())
	{
		return false;
	}
	if (3 >= path.length())
	{
		UINT value = GetDriveType(std::wstring(path+L"\\").c_str());
		if (DRIVE_UNKNOWN == value || DRIVE_NO_ROOT_DIR == value)
		{
			return false;
		}
		return true;
	}
	DWORD attrib = GetFileAttributes(std::wstring(L"\\\\?\\"+path).c_str());
	if (INVALID_FILE_ATTRIBUTES == attrib)
	{
		int32_t ret = GetLastError();
		return (ERROR_PATH_NOT_FOUND!=ret && ERROR_FILE_NOT_FOUND!=ret);
	}
	else
	{
		return true;
	}
#endif
}

int64_t SD::Utility::FS::get_file_size(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	try
	{
		fs::wpath Path(path);
		return fs::file_size(Path);
	}
	catch(boost::system::system_error& e)
	{
		(void)e;
		return -1;
	}
	return -1;
#else
	if (path.empty())
	{
		return -1;
	}
	WIN32_FILE_ATTRIBUTE_DATA wfad;
	memset(&wfad, 0, sizeof(WIN32_FILE_ATTRIBUTE_DATA));
	if (!GetFileAttributesEx(std::wstring(L"\\\\?\\"+path).c_str(), 
		GetFileExInfoStandard, 
		&wfad))
	{
		return -1;
	}
	
	int64_t size = wfad.nFileSizeHigh;
	size = (size<<32)+wfad.nFileSizeLow;
	return size;
#endif
}

std::wstring SD::Utility::FS::get_file_name(const std::wstring& path)
{
	//std::wstring tmp = SD::Utility::String::replace_all(path, L"\\", L"/");
	std::wstring::size_type nPos = path.find_last_of(L"\\");
	if (std::wstring::npos == nPos)
	{
		return L"";
	}
	return path.substr(nPos + 1, path.length() - nPos);
}

std::wstring SD::Utility::FS::get_conflict_newname(const std::wstring& name)
{
	std::wstring temp_name;
	std::wstring extension_name;
	std::wstring::size_type nPos = name.find_last_of(L".");
	if (std::wstring::npos == nPos)
	{
		temp_name = name;
		extension_name = L"";
	}
	else
	{
		temp_name = name.substr(0,  nPos);
		extension_name = name.substr(nPos, name.length() - nPos + 1);
	}

	char chBuf[128] = {0};
    time_t now = time(NULL);
	tm tmNow;
	localtime_s(&tmNow, &now);
	strftime(chBuf , 127 , "%Y%m%d%H%M%S", &tmNow);
	std::wstring strTime = String::string_to_wstring(std::string(chBuf));

	std::wstring conflict_name = L"." + strTime + PATH_CONFLICT_SUFFIX;

	return temp_name + conflict_name + extension_name;
}

std::wstring SD::Utility::FS::get_extension_name(const std::wstring& path)
{
	std::wstring::size_type nPos = path.find_last_of(L".");
	if (std::wstring::npos == nPos)
	{
		return L"";
	}
	return path.substr(nPos + 1, path.length() - nPos);
}

std::wstring SD::Utility::FS::get_parent_name(const std::wstring& path)
{
	//std::wstring tmp = SD::Utility::String::replace_all(path, L"\\", L"/");
	std::wstring::size_type nPos = path.find_last_of(L"\\");
	if (std::wstring::npos == nPos)
	{
		return L"";
	}
	return path.substr(0, nPos);
}

std::wstring SD::Utility::FS::get_topdir_name(const std::wstring& path)
{
	//std::wstring tmp = SD::Utility::String::replace_all(path, L"\\", L"/");
	std::wstring tmp = path.substr(1, path.length()-1);
	std::wstring::size_type nPos = tmp.find_first_of(L"\\");
	if (std::wstring::npos == nPos)
	{
		return tmp;
	}
	return tmp.substr(0, nPos);
}

int64_t SD::Utility::FS::get_file_createtime(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	if (path.empty())
	{
		return INVALID_TIME;
	}

	struct _stat64 st_stat;
	if (0 != _wstati64(path.c_str(), &st_stat))
	{
		return INVALID_TIME;
	}
	// format the precision to ms
	return st_stat.st_ctime*1000;
#else
	if (path.empty())
	{
		return -1;
	}
	WIN32_FILE_ATTRIBUTE_DATA wfad;
	memset(&wfad, 0, sizeof(WIN32_FILE_ATTRIBUTE_DATA));
	if (!GetFileAttributesEx(std::wstring(L"\\\\?\\"+path).c_str(), 
		GetFileExInfoStandard, 
		&wfad))
	{
		return -1;
	}

	int64_t time = wfad.ftCreationTime.dwHighDateTime;
	time = (time<<32)+wfad.ftCreationTime.dwLowDateTime;
	SD::Utility::DateTime dateTime(time, SD::Utility::Windows);
	return dateTime.getCrtFileTime();
#endif
}

int64_t SD::Utility::FS::get_file_modifytime(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	if (path.empty())
	{
		return INVALID_TIME;
	}

	struct _stat64 st_stat;
	if (0 != _wstati64(path.c_str(), &st_stat))
	{
		return INVALID_TIME;
	}
	// format the precision to ms
	return st_stat.st_mtime*1000;
#else
	if (path.empty())
	{
		return -1;
	}
	WIN32_FILE_ATTRIBUTE_DATA wfad;
	memset(&wfad, 0, sizeof(WIN32_FILE_ATTRIBUTE_DATA));
	if (!GetFileAttributesEx(std::wstring(L"\\\\?\\"+path).c_str(), 
		GetFileExInfoStandard, 
		&wfad))
	{
		return -1;
	}

	int64_t time = wfad.ftLastWriteTime.dwHighDateTime;
	time = (time<<32)+wfad.ftLastWriteTime.dwLowDateTime;
	DateTime dateTime(time, Windows);
	return dateTime.getCrtFileTime();
#endif
}

int64_t SD::Utility::FS::get_file_accesstime(const std::wstring& path)
{
#ifdef USE_BOOST_FS
	if (path.empty())
	{
		return INVALID_TIME;
	}

	struct _stat64 st_stat;
	if (0 != _wstati64(path.c_str(), &st_stat))
	{
		return INVALID_TIME;
	}
	// format the precision to ms
	return st_stat.st_atime*1000;
#else
	if (path.empty())
	{
		return -1;
	}
	WIN32_FILE_ATTRIBUTE_DATA wfad;
	memset(&wfad, 0, sizeof(WIN32_FILE_ATTRIBUTE_DATA));
	if (!GetFileAttributesEx(std::wstring(L"\\\\?\\"+path).c_str(), 
		GetFileExInfoStandard, 
		&wfad))
	{
		return -1;
	}

	int64_t time = wfad.ftLastAccessTime.dwHighDateTime;
	time = (time<<32)+wfad.ftLastAccessTime.dwLowDateTime;
	DateTime dateTime(time, Windows);
	return dateTime.getCrtFileTime();
#endif
}

std::wstring SD::Utility::FS::get_current_sys_time()
{
	time_t now = time(NULL);
	std::wstringstream nowStr;
	nowStr<<now;
	return nowStr.str();
}

std::wstring SD::Utility::FS::get_work_directory()
{
	std::auto_ptr<wchar_t> buffer(new wchar_t[MAX_PATH]);
	memset(buffer.get(), 0, MAX_PATH);
	DWORD len = GetCurrentDirectory(MAX_PATH, buffer.get());
	if (0 == len)
	{
		return L"";
	}
	if (len < MAX_PATH)
	{
		return std::wstring(buffer.get());
	}

	buffer.reset(new wchar_t[len]);
	memset(buffer.get(), 0, len);
	len = GetCurrentDirectory(len, buffer.get());
	if (0 == len)
	{
		return L"";
	}
	return std::wstring(buffer.get());
}

std::wstring SD::Utility::FS::format_path(const std::wstring& path)
{
	if (path.empty())
	{
		return L"";
	}
	return Utility::String::replace_all(path, L"/", L"\\");
}

std::string SD::Utility::FS::format_path(const std::string& path)
{
	if (path.empty())
	{
		return "";
	}
	return Utility::String::replace_all(path, "/", "\\");
}

#endif

#ifdef ENABLE_UTILITY_STRING
std::wstring SD::Utility::String::replace_all(const std::wstring& src, const std::wstring& oldvalue, const std::wstring& newvalue)
{
	std::wstring tmp = src;
	boost::algorithm::replace_all(tmp, oldvalue, newvalue);
	return tmp;
}

std::string SD::Utility::String::replace_all(const std::string& src, const std::string& oldvalue, const std::string& newvalue)
{
	std::string tmp = src;
	boost::algorithm::replace_all(tmp, oldvalue, newvalue);
	return tmp;
}

std::wstring SD::Utility::String::string_to_wstring(const std::string& str)
{
	std::wstring  wstr;
	if(str.empty())
	{
		return wstr;
	}

	int32_t iWstrLen =  MultiByteToWideChar(CP_ACP,0,str.c_str(),-1,NULL,0);
	LPWSTR lpwsBuf = new(std::nothrow) WCHAR[iWstrLen];
	if(NULL == lpwsBuf)
	{
		return wstr;
	}

	memset(lpwsBuf,0,iWstrLen * sizeof(WCHAR));
	int32_t nResult = MultiByteToWideChar(CP_ACP,0,str.c_str(),-1,lpwsBuf,iWstrLen);
	
	if(0 == nResult)
    { 
		delete []lpwsBuf;
        return wstr;
    }

	wstr = lpwsBuf;
	delete []lpwsBuf;
    return  wstr;
}

std::string  SD::Utility::String::wstring_to_string(const std::wstring& wstr)
{
	std::string  str;
	if(wstr.empty())
	{
		return str;
	}

	int32_t istrLen = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,NULL,0,NULL,NULL);

	LPSTR lpsBuf = new(std::nothrow) CHAR[istrLen];
	if(NULL == lpsBuf)
	{
		return str;
	}

	memset(lpsBuf,0,istrLen * sizeof(CHAR));
	int32_t nResult = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,lpsBuf,istrLen,NULL,NULL);	
	if(0 == nResult)
    { 
		delete []lpsBuf;
        return str;
    }

	str = lpsBuf;
	delete []lpsBuf;
    return  str;
}

std::wstring  SD::Utility::String::utf8_to_wstring(const std::string& str)
{
	std::wstring wstr;
	if(str.empty())
	{
		return wstr;
	}

	int32_t iWstrLen = MultiByteToWideChar(CP_UTF8,0,str.c_str(),-1,NULL,0);
	LPWSTR lpwsBuf = new(std::nothrow) WCHAR[iWstrLen];
	if(NULL == lpwsBuf)
	{
		return wstr;
	}

	memset(lpwsBuf,0,iWstrLen * sizeof(WCHAR));
	int32_t nResult = MultiByteToWideChar(CP_UTF8,0,str.c_str(),-1,lpwsBuf,iWstrLen);
	if(0 == nResult)
    { 
		delete []lpwsBuf;
        return wstr;
    }

	wstr = lpwsBuf;
	delete []lpwsBuf;
    return  wstr;
}

std::string SD::Utility::String::wstring_to_utf8(const std::wstring& wstr)
{
	std::string str;
	if(wstr.empty())
	{
		return str;
	}

	int32_t istrLen = WideCharToMultiByte(CP_UTF8,0,wstr.c_str(),-1,NULL,0,NULL,NULL);
	LPSTR lpsBuf = new(std::nothrow) CHAR[istrLen];
	if(NULL == lpsBuf)
	{
		return str;
	}

	memset(lpsBuf,0,istrLen * sizeof(CHAR));
	int32_t nResult = WideCharToMultiByte(CP_UTF8,0,wstr.c_str(),-1,lpsBuf,istrLen,NULL,NULL);	
	if(0 == nResult)
    { 
		delete []lpsBuf;
        return str;
    }

	str = lpsBuf;
	delete []lpsBuf;
    return  str;
}

std::wstring SD::Utility::String::ltrim(const std::wstring& src, const std::wstring& value)
{
	if (src.empty() || value.empty())
	{
		return src;
	}	
	std::wstring::size_type pos = src.find(value);
	if (std::wstring::npos == pos || (0 != pos))
	{
		return src;
	}
	return src.substr(value.length(), src.length()-value.length());
}

std::wstring SD::Utility::String::rtrim(const std::wstring& src, const std::wstring& value)
{
	if (src.empty() || value.empty())
	{
		return src;
	}
	std::wstring::size_type pos = src.rfind(value);
	if (std::wstring::npos == pos || (pos != (src.length()-value.length())))
	{
		return src;
	}
	return src.substr(0, pos);
}

std::string SD::Utility::String::ltrim(const std::string& src, const std::string& value)
{
	if (src.empty() || value.empty())
	{
		return src;
	}	
	std::string::size_type pos = src.find(value);
	if (std::string::npos == pos || (0 != pos))
	{
		return src;
	}
	return src.substr(value.length(), src.length()-value.length());
}

std::string SD::Utility::String::rtrim(const std::string& src, const std::string& value)
{
	if (src.empty() || value.empty())
	{
		return src;
	}
	std::string::size_type pos = src.rfind(value);
	if (std::string::npos == pos || (pos != (src.length()-value.length())))
	{
		return src;
	}
	return src.substr(0, pos);
}

static std::string FormatStringArgs(const char *cFormat, va_list args)
{
	int32_t lNum;
	std::string strRtn;
	uint32_t ulSize = 1024;

	char* pcBuffer = new(std::nothrow) char[ulSize];
	if (NULL == pcBuffer)
	{
		return "";
	}

#pragma warning(push)
#pragma warning(disable: 4127)
	while (true)
#pragma warning(pop)
	{
#ifdef _WIN32
		lNum = vsnprintf_s(pcBuffer, ulSize, _TRUNCATE, cFormat, args);
#else
		lNum = vsnprintf(pcBuffer, ulSize, cFormat, args);
#endif

		if ((lNum > -1) && (lNum < (int32_t) ulSize))
		{
			strRtn = pcBuffer;
			delete[] pcBuffer;
			return strRtn;
		}

		ulSize = (lNum > -1) ? (uint32_t) (lNum + 1) : ulSize * 2;

		delete[] pcBuffer;
		pcBuffer = new(std::nothrow) char[ulSize];
		if (NULL == pcBuffer)
		{
			return "";
		}
	}
}

static std::wstring FormatStringArgs(const wchar_t *cFormat, va_list args)
{
	int32_t lNum;
	std::wstring strRtn;
	uint32_t ulSize = 1024;

	wchar_t* pcBuffer = new(std::nothrow) wchar_t[ulSize];
	if (NULL == pcBuffer)
	{
		return L"";
	}

#pragma warning(push)
#pragma warning(disable: 4127)
	while (true)
#pragma warning(pop)
	{
#ifdef _WIN32
		lNum = _vsnwprintf_s(pcBuffer, ulSize, _TRUNCATE, cFormat, args);
#else
		lNum = vsnprintf(pcBuffer, ulSize, cFormat, args);
#endif

		if ((lNum > -1) && (lNum < (int32_t) ulSize))
		{
			strRtn = pcBuffer;
			delete[] pcBuffer;
			return strRtn;
		}

		ulSize = (lNum > -1) ? (uint32_t) (lNum + 1) : ulSize * 2;

		delete[] pcBuffer;
		pcBuffer = new(std::nothrow) wchar_t[ulSize];
		if (NULL == pcBuffer)
		{
			return L"";
		}
	}
}

std::wstring SD::Utility::String::format_string(const wchar_t* format, ...)
{
	va_list va;
	va_start(va, format);
	std::wstring strRtn = FormatStringArgs(format, va);
	va_end(va);

	return strRtn;
}

std::string SD::Utility::String::format_string(const char* format, ...)
{
	va_list va;
	va_start(va, format);
	std::string strRtn = FormatStringArgs(format, va);
	va_end(va);

	return strRtn;
}

std::wstring SD::Utility::String::to_lower(const std::wstring& str)
{
	std::wstring::size_type len = str.size()+1;
	wchar_t *buf = new wchar_t[len];
	if (NULL == buf)
	{
		return L"";
	}
	memset(buf, 0, len*sizeof(wchar_t));
	wcscpy_s(buf, len, str.data());
	for (std::wstring::size_type i = 0; i < len; ++i)
	{
		buf[i] = ((buf[i]>=L'A'&&buf[i]<=L'Z')?(L'a'+buf[i]-L'A'):buf[i]);
	}
	std::wstring out = buf;
	delete buf;
	return out;
}

std::wstring SD::Utility::String::to_upper(const std::wstring& str)
{
	std::wstring::size_type len = str.size()+1;
	wchar_t *buf = new wchar_t[len];
	if (NULL == buf)
	{
		return L"";
	}
	memset(buf, 0, len*sizeof(wchar_t));
	wcscpy_s(buf, len, str.data());
	for (std::wstring::size_type i = 0; i < len; ++i)
	{
		buf[i] = ((buf[i]>=L'a'&&buf[i]<=L'z')?(L'A'+buf[i]-L'a'):buf[i]);
	}
	std::wstring out = buf;
	delete buf;
	return out;
}

std::string SD::Utility::String::to_lower(const std::string& str)
{
	std::string::size_type len = str.size();
	char *buf = new char[len];
	if (NULL == buf)
	{
		return "";
	}
	memset(buf, 0, len*sizeof(char));
	strcpy_s(buf, len, str.data());
	for (std::string::size_type i = 0; i < len; ++i)
	{
		buf[i] = ((buf[i]>='A'&&buf[i]<='Z')?('a'+buf[i]-'A'):buf[i]);
	}
	std::string out = buf;
	delete buf;
	return out;
}

std::string SD::Utility::String::to_upper(const std::string& str)
{
	std::string::size_type len = str.size();
	char *buf = new char[len];
	if (NULL == buf)
	{
		return "";
	}
	memset(buf, 0, len*sizeof(char));
	strcpy_s(buf, len, str.data());
	for (std::string::size_type i = 0; i < len; ++i)
	{
		buf[i] = ((buf[i]>='a'&&buf[i]<='z')?('A'+buf[i]-'a'):buf[i]);
	}
	std::string out = buf;
	delete buf;
	return out;
}

std::wstring SD::Utility::String::gen_uuid()
{
	boost::uuids::uuid uuid = boost::uuids::random_generator()();
	std::wstringstream stream;
	stream << uuid;
	return stream.str();
}

int32_t SD::Utility::String::get_random_num(uint32_t min, uint32_t max)
{
	if (min >= max)
	{
		return 0;
	}

	uint32_t random;
	rand_s(&random);
	return (min+(random%(max-min+1)));
}

std::wstring SD::Utility::String::create_random_string()
{
	using namespace SD::Utility::String;

	wchar_t* array[3] = 
	{
		L"0123456789", 
		L"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
		L"-+.!@#$^&*"
	};

	// the length of random string is between 8 and 20
	//int32_t str_len = get_random_num(8, 20);
	int32_t str_len = 8;
	wchar_t *random_string = new wchar_t[str_len+1];
	if(NULL == random_string)
	{
		return L"";
	}
	memset(random_string, 0, (str_len+1)*sizeof(wchar_t));
	size_t len = 0;
	for (size_t i = 0; i < sizeof(array)/sizeof(wchar_t*); ++i)
	{
		len += wcslen(array[i]);
	}
	for (size_t i = 0; i < (size_t)str_len; ++i)
	{
		if (3>i)
		{
			random_string[i] = array[i][get_random_num(0, (uint32_t)wcslen(array[i])-1)];
		}
		else
		{
			int32_t tmp = get_random_num(0, 2);
			random_string[i] = array[tmp][get_random_num(0, (uint32_t)wcslen(array[tmp])-1)];
		}
	}

	// change the sequence of the random string
	for (int32_t k = 0; k < 50; ++k)
	{
		for(int32_t i = 0; i < str_len; ++i)
		{
			int32_t j = get_random_num(0, str_len-1);
			wchar_t tmp = random_string[i];
			random_string[i] = random_string[j];
			random_string[j] = tmp;
		}
	}

	std::wstring str = random_string;
	delete random_string;

	return str;
}
#endif

#ifdef ENABLE_UTILITY_MD5
std::wstring SD::Utility::MD5::getMD5ByString(const char* szSource)
{
	CMD5 md5;
	md5.reset();
	md5.update(szSource);
	return SD::Utility::String::string_to_wstring(md5.toString());
}

std::wstring SD::Utility::MD5::getMD5ByFile(const wchar_t* szSource)
{
	CMD5 md5;
	md5.reset();
	ifstream fs(szSource, std::ios::binary);
	if (fs.fail())
	{
		return L"";
	}
	md5.update(fs);
	return SD::Utility::String::string_to_wstring(md5.toString());
}
#endif

#ifdef ENABLE_UTILITY_SHA1
std::wstring SD::Utility::SHA1::getSHA1ByString(const char* szSource)
{
	CSha1 sha1;
	sha1.Reset();
	if (!sha1.Update(szSource))
	{
		return L"";
	}
	if (!sha1.Result())
	{
		return L"";
	}
	return SD::Utility::String::string_to_wstring(sha1.ToString());
}

std::wstring SD::Utility::SHA1::getSHA1ByFile(const wchar_t* szSource)
{
	CSha1 sha1;
	sha1.Reset();
	std::fstream fs(szSource, std::ios::binary|std::ios::in);
	if (fs.fail())
	{
		return L"";
	}
	if (!sha1.Update(fs))
	{
		return L"";
	}
	if (!sha1.Result())
	{
		return L"";
	}
	return SD::Utility::String::string_to_wstring(sha1.ToString());
}
#endif

#ifdef ENABLE_UTILITY_ENCRYPT
std::wstring SD::Utility::String::encrypt_string(const std::wstring& str)
{
	if (str.empty())
	{
		return L"";
	}
	CAes aes(default_password_key);
	std::string strTmpIn = SD::Utility::String::wstring_to_string(str);
	std::string strTmpOut = aes.EncryptPassword(strTmpIn);	
	return SD::Utility::String::string_to_wstring(strTmpOut);
}

std::wstring SD::Utility::String::decrypt_string(const std::wstring& str)
{
	if (str.empty())
	{
		return L"";
	}
	CAes aes(default_password_key);
	std::string strTmpIn = SD::Utility::String::wstring_to_string(str);
	std::string strTmpOut = aes.DecryptPassword(strTmpIn);
	return SD::Utility::String::string_to_wstring(strTmpOut);
}
#endif
