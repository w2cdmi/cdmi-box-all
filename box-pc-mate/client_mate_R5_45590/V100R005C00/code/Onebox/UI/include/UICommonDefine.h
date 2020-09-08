#pragma  once

#define SECURITY_WIN32
#include <Security.h>
#include <regex>
#include <cstdlib>
#include "Utility.h"
#include "CommonDefine.h"

#pragma comment(lib, "Secur32.lib")

#define DEFAULT_USER_DATA_NAME (L"UserData")
#define ONEBOX_REG_USERID_NAME (L"UserID")
#define HWND_REG_NAME (L"hWnd")
#define ONEBOX_USESTATUS_NAME (L"UseStatus")

#define DEFAULT_USER_SETTINGS_WEB (L"user/settings")
#define DEFAULT_USER_HELP_WEB_CH (L"static/help/zh/helpcenter.html")
#define DEFAULT_USER_HELP_WEB_EN (L"static/help/en/helpcenter.html")

#define CURL_ERROR_COULDNTRESOLVHOST (-2006)
#define	CURL_ERROR_COULDNTCONNECT (-2007)

enum LogoutType
{
	NO,
	YES,
	RESTART, 
	CHANGELANG
};

enum UI_TIMERID
{
	SCROLL_TIMERID,
	LOGIN_TIMERID,
	SIMPLENOTICE_TIMERID,
	AUTOEXPAND_TIMER,
	SETTASKNOTICE_TIMER,
	LOGININGO_TIMER,
	NOTIFYICON_TIMER
};

enum UI_LANGUGE
{
	CHINESE = 2052,
	ENGLISH = 2057,
	DEFAULT = -1
};

enum UI_BALLOON
{
	BALLOON_NO,
	BALLOON_YES
};

enum ONEBOX_USESTATUS
{
	NOTFIRSTUSED,
	FIRSTUSED
};

static std::wstring GetInstallPath()
{
	std::wstring wstrInstallPath = L"";
	if (0 != SD::Utility::Registry::get(HKEY_LOCAL_MACHINE,ONEBOX_REG_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath) &&
		0 != SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REGHKCU_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath))
	{
		return L"";
	}
	if (wstrInstallPath.length() !=0 && (wstrInstallPath[wstrInstallPath.length()-1] != L'\\' || 
		wstrInstallPath[wstrInstallPath.length()-1] != L'/'))
	{
		wstrInstallPath += PATH_DELIMITER;
	}
	wstrInstallPath = SD::Utility::String::replace_all(wstrInstallPath,L"/",L"\\");
	return wstrInstallPath;
}

static DWORD  GetUserID()
{
	DWORD UserId = 0;
	SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REG_PATH,ONEBOX_REG_USERID_NAME,UserId);
	return UserId;
}

static std::wstring GetdomianName()
{	
	wchar_t szBuffer[MAX_PATH] = {0};
	ULONG len = MAX_PATH;
	if (0 == ::GetUserNameEx(NameSamCompatible, szBuffer, &len))
	{
		return L"";
	}
	std::wstring domainName=std::wstring(szBuffer);
	std::wstring::size_type pos = domainName.find_last_of(L"\\");
	if (std::wstring::npos != pos &&  (0 != pos))
	{
		domainName=domainName.substr(pos+1);
	}

	return domainName;
}

static DWORD GetUseStatus()
{
	DWORD UseStatus = (DWORD)ONEBOX_USESTATUS::NOTFIRSTUSED;
	SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REG_PATH,ONEBOX_USESTATUS_NAME,UseStatus);
	return UseStatus;
}

static bool IsEmailAddressValid(std::wstring emailAddress)
{
	std::regex pattern("([0-9A-Za-z\\-_\\.]+)@([0-9a-z]+\\.[a-z]{2,3}(\\.[a-z]{2})?)");
	if (std::regex_match(SD::Utility::String::wstring_to_string(emailAddress),pattern))
	{
		return true;
	}
	return false;
}

static BOOL IsAdministratorUser()
{    
	static HMODULE hModule = NULL;    
	if( !hModule )         
		hModule = LoadLibrary(_T("shell32.dll"));    
	if( !hModule )        
		return TRUE;       
	typedef BOOL (__stdcall *FunctionIsUserAdmin)();    
	FunctionIsUserAdmin pfnIsUserAnAdmin = (FunctionIsUserAdmin)GetProcAddress(hModule, "IsUserAnAdmin");     
	if (pfnIsUserAnAdmin)         
		return pfnIsUserAnAdmin();    
	return TRUE;
}