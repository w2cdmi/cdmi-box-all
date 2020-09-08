#ifndef _ShellExtent_GLOBALS_H_
#define _ShellExtent_GLOBALS_H_

#include "CommonDefine.h"
#include<Windows.h>
#include <sstream>

#ifdef _WIN64
#define APP_PATH_GEG_PATH (L"SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting")
#else
#define APP_PATH_GEG_PATH (L"SOFTWARE\\Chinasoft\\Onebox\\Setting")
#endif

#define APP_PATH_REG_NAME (L"AppPath")
#define SERVER_STATUS_REG_NAME (L"LoginState")

#define CONF_FILE (L"\\Config.ini")
#define CONFIG_SECTION (L"CONFIGURE")
#define CONFIG_MONITORPATH_KEY (L"MonitorRootPath")

#define LANG_ZH_PATH (L"\\Language\\Chinese.ini")
#define LANG_EN_PATH (L"\\Language\\English.ini")
#define LANG_MSG_SECTION (L"Main")

enum IconPriority
{
	IconPrioritySyncNoAction,
    IconPrioritySyncing,
    IconPriorityFailed,
    IconPrioritySynced,
    IconPriorityInvalid
};

enum IconStatus
{
	IconSynced,
	IconSyncing,
	IconNoAction,
	IconInvalid
};

enum  CtxMenu_Operation
{
    CtxMenu_Operation_Share,
    CtxMenu_Operation_OutChain
};

namespace Onebox
{
	namespace ShellExt
	{
		namespace Global
		{
			IconStatus getOverlayIconStatus(const std::wstring& path);
			bool isSynced(const std::wstring& path);
			bool isSyncFile(const std::wstring& path);
			bool isMonitorRootPath(const std::wstring& path);
			int32_t setShare(const std::wstring& path);
			int32_t setShareLink(const std::wstring& path);
			int32_t uploadFileOperation(const std::wstring& path);
			int32_t getSyncServiceStatus();
			bool isSyncServiceNormal();
			std::wstring getLangPath();
		}
	}
}

#endif
