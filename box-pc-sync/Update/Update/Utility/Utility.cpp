#include "../stdafx.h"
#include "Utility.h"
#include <utility>
#include <boost/filesystem.hpp>
#include <boost/algorithm/string.hpp>
namespace fs=boost::filesystem;

bool Utility::GetAppPath(wstring& wstrAppPath)
{
    int iRet = GetRegString(CSE_APP_REG_PATH,  CSE_APP_REG_PRO_PATH, wstrAppPath) ;
   
	if (0 == iRet)
    {
		int tmp = wstrAppPath.find_last_of(L'\\');

	    if (tmp !=  wstrAppPath.length()-1)
		{
			wstrAppPath = wstrAppPath+(L"\\");
		}

        return true;
    }

	return false;
}

bool Utility::GetMainVersion(wstring& wstrMainVersion)
{
	int iRet = 0;
	iRet = GetRegString(CSE_APP_REG_PATH,  CSE_APP_VERSION, wstrMainVersion);

	 if(0 != iRet)
	 {
		iRet = GetRegString(CSE_APP_OLD_REG_PATH,  CSE_APP_VERSION, wstrMainVersion);
		
		if(0 !=iRet )
		{
			 return false;
		}
	 }

	 return true;
}

wstring Utility::s2ws(const string&  str)
 {
 	wstring  wstr;

	if(str.empty())
	{
		return wstr;
	}

	int iWstrLen =  MultiByteToWideChar(CP_ACP,0,str.c_str(),-1,NULL,0);
	LPWSTR lpwsBuf = new(std::nothrow) WCHAR[iWstrLen];
	//new出错判断
	if(NULL == lpwsBuf)
	{
		return wstr;
	}

	memset(lpwsBuf,0,iWstrLen * sizeof(WCHAR));
	int nResult = MultiByteToWideChar(CP_ACP,0,str.c_str(),-1,lpwsBuf,iWstrLen);
	
	//转码出错处理
	if(0 == nResult)
    { 
		delete []lpwsBuf;
        return wstr;
    }

	wstr = lpwsBuf;
	delete []lpwsBuf;
    return  wstr;
 }

string  Utility::ws2s(const wstring& wstr)
{
	string  str;
	if(wstr.empty())
	{
		return str;
	}

	int istrLen = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,NULL,0,NULL,NULL);

	LPSTR lpsBuf = new(std::nothrow) CHAR[istrLen];
	//new出错判断
	if(NULL == lpsBuf)
	{
		return str;
	}

	memset(lpsBuf,0,istrLen * sizeof(CHAR));
	int nResult = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,lpsBuf,istrLen,NULL,NULL);	
	
	//转码出错处理
	if(0 == nResult)
    { 
		delete []lpsBuf;
        return str;
    }

	str = lpsBuf;
	delete []lpsBuf;
    return  str;
}

bool Utility::is_exist(const std::wstring& path)
{
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
}

int Utility::GetRegString(const wstring &wstrRegPath, const wstring &wstrKeyName, wstring &wstrValue)
{
    HKEY hkey;
    LONG lErrCode;
    DWORD   dwPathSize   =   512;
    BYTE bPath[512] = {0};
    lErrCode = RegOpenKeyEx(HKEY_LOCAL_MACHINE , (LPCWSTR)(wstrRegPath.c_str()),
        NULL,  
        KEY_QUERY_VALUE,
        &hkey);
    if (ERROR_SUCCESS == lErrCode)
    {
        lErrCode = RegQueryValueEx(hkey,  (LPCWSTR)(wstrKeyName.c_str()),  NULL, NULL,  (LPBYTE)bPath,  &dwPathSize);
    }

    if (ERROR_SUCCESS == lErrCode )
    {
        wstrValue.append((wchar_t *) bPath);
        if (!wstrValue.empty())
        {
            Utility::replace_all(wstrValue, L"/", L"\\");
        }
    }
    RegCloseKey(hkey);	
    return lErrCode;
}


string Utility::replace_all(const string& src, const string& oldvalue, const string& newvalue)
{
	std::string tmp = src;
	boost::algorithm::replace_all(tmp, oldvalue, newvalue);
	return tmp;
}

wstring Utility::replace_all(const wstring& src, const wstring& oldvalue, const wstring& newvalue)
{
	std::wstring tmp = src;
	boost::algorithm::replace_all(tmp, oldvalue, newvalue);
	return tmp;
}

int Utility::remove(const std::wstring& path)
{
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
}

int Utility::remove_all(const std::wstring& path)
{
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
}
