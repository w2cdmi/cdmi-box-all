#pragma  once

#include "UICommonDefine.h"
#include "InIHelper.h"
#include "Utility.h"

#define CONF_TRANS_TASK_SECTION (L"TRANSTASK")
#define CONF_MAX_ASYNC_TRANS_TASK_KEY (L"MaxAsyncTransTask")
#define CONF_MAX_ASYNC_TRANS_THREAD_KEY (L"MaxAsyncTransThread")

#define CONF_NETWORK_SECTION (L"NETWORK")
#define CONF_MAX_UPLOAD_SPEED_KEY (L"MaxUploadSpeed")
#define CONF_MAX_DWONLOAD_SPEED_KEY (L"MaxDownloadSpeed")
#define CONF_USE_SPEED_LIMIT_KEY (L"UseSpeedLimit")

#define CONF_USERINFO_SECTION (L"USERINFO")
#define CONF_USERNAME_KEY (L"UserName")
#define CONF_PASSWORD_KEY (L"PassWord")
#define CONF_REMPASSWORD_KEY (L"RemPassWord")
#define CONF_AUTOLOGIN_KEY (L"AutoLogin")
#define CONF_LOGIN_TYPE_KEY (L"LoginType")
#define CONF_LOGOUT_KEY (L"IsLogOut")
#define CONF_IS_FIRST_RUN_KEY (L"IsFirstRun")

#define CONF_SETTINGS_SECTION (L"SETTINGS")
#define CONF_AUTO_RUN_KEY (L"AutoRun")
#define CONF_LANGUAGE_KEY (L"Language")
//#define CONF_SYSTEM_BUBBLEREMIND_KEY (L"BubbleRemind")
#define CONF_SYSTEM_BUBBLEREMIND_KEY (L"SystemBubbleRemind")
#define CONF_RESOURCE_BUBBLEREMIND_KEY (L"ResourceBubbleRemind")
#define CONF_DOWNLOADPATH_KEY (L"DownloadPath")
#define CONF_HISTORYVER_KEY (L"HistoryVer")
#define CONF_OFFICEONEBOXPATH_KEY (L"OfficeOneboxPath")
#define CONF_OFFICEONEBOXPATH_ID_KEY (L"OfficeOneboxPathId")
#define CONF_MONITORROOTPATH_KEY (L"MonitorRootPath")
#define CONF_PAGE_LIMIT_KEY (L"PageLimit")
#define CONF_OPEN_NAME_EXT (L"OpenNameExt")
#define CONF_THUMB_NAME_EXT (L"ThumbNameExt")

#define DEFAULT_PAGE_LIMIT_NUM (100)
#define DEFAULT_OPEN_NAME_EXT (L"doc;docx;txt;csv;execl;xls;xlsx;ppt;pptx;pdf;png;bmp;img;ico;jpg;jpeg;docm;xlsm;pptm")
#define DEFAULT_THUMB_NAME_EXT (L"jpg;jpeg;gif;bmp;png")

template<typename T>
static T GetUserConfValue(const std::wstring& section, const std::wstring& valueKey, const T& defaultValue)
{
	DWORD UserId = GetUserID();
	if (0 == UserId)
	{
		return defaultValue;
	}
	std::wstring configPath = SD::Utility::FS::get_system_user_app_path();
	if (L"" == configPath)
	{
		return defaultValue;
	}
	configPath += PATH_DELIMITER+std::wstring(ONEBOX_APP_DIR)+PATH_DELIMITER;
	configPath += DEFAULT_USER_DATA_NAME;
	configPath += PATH_DELIMITER;
	configPath += SD::Utility::String::type_to_string<std::wstring,DWORD>(UserId);
	configPath += PATH_DELIMITER;
	configPath += SD::Utility::String::type_to_string<std::wstring,DWORD>(UserId);
	configPath += PATH_DELIMITER;
	configPath += DEFAULT_CONFIG_NAME;
	CInIHelper InIHelper(SD::Utility::FS::format_path(configPath.c_str()));
	return InIHelper.GetValue<T>(section,valueKey,defaultValue);
}

template<typename T>
static void SetUserConfValue(const std::wstring& section, const std::wstring& valueKey, T const & defaultValue)
{
	DWORD UserId = GetUserID();
	if (0 == UserId)
	{
		return;
	}
	std::wstring configPath = SD::Utility::FS::get_system_user_app_path();
	if (L"" == configPath)
	{
		return;
	}
	configPath += +PATH_DELIMITER+std::wstring(ONEBOX_APP_DIR)+PATH_DELIMITER;
	configPath += DEFAULT_USER_DATA_NAME;
	configPath += PATH_DELIMITER;
	configPath += SD::Utility::String::type_to_string<std::wstring,DWORD>(UserId);
	configPath += PATH_DELIMITER;
	configPath += SD::Utility::String::type_to_string<std::wstring,DWORD>(UserId);
	configPath += PATH_DELIMITER;
	configPath += DEFAULT_CONFIG_NAME;
	CInIHelper InIHelper(SD::Utility::FS::format_path(configPath.c_str()));
	InIHelper.SetValue<T>(section,valueKey,defaultValue);
}
