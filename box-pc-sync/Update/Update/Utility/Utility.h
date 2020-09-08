#pragma once
#ifndef _UTILITY_H_
#define _UTILITY_H_

#include <string>



//注册表项
#ifndef CSE_APP_REG_PATH
#define CSE_APP_REG_PATH (L"SOFTWARE\\Chinasoft\\OneBox\\Setting")
#endif

#ifndef CSE_APP_OLD_REG_PATH
#define CSE_APP_OLD_REG_PATH (L"SOFTWARE\\Chinasoft\\Huawei CloudDrive\\Setting")
#endif

//注册表中程序安装路径
#ifndef CSE_APP_REG_PRO_PATH
#define CSE_APP_REG_PRO_PATH (L"AppPath")
#endif

#ifndef CSE_APP_VERSION
#define CSE_APP_VERSION (L"MainVersion")
#endif

// 配置文件路径
#ifndef CSE_INI_FILE
#define CSE_INI_FILE (L"Config.ini")
#endif

// 升级配置文件路径
#ifndef CSE_UPDATEINI_FILE
#define CSE_UPDATEINI_FILE (L"Update\\Config.ini")
#endif

// 升级配置文件路径
#ifndef CSE_DB_FILE
#define CSE_DB_FILE (L"UserData")
#endif


using namespace std;

namespace Utility
{
	 bool  GetMainVersion(wstring& wstrMainVersion);
	 bool GetAppPath(wstring& wstrAppPath);
	 wstring s2ws(const string&  str);
	 string  ws2s(const wstring& wstr);
	 bool is_exist(const std::wstring& path);
	 string replace_all(const string& src, const string& oldvalue, const string& newvalue);
	 wstring replace_all(const wstring& src, const wstring& oldvalue, const wstring& newvalue);
	 int GetRegString(const wstring &wstrRegPath, const wstring &wstrKeyName, wstring &wstrValue);
	 int remove(const std::wstring& path);
	 int remove_all(const std::wstring& path);
}

#endif
