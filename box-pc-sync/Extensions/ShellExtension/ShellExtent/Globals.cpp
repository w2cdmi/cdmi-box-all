#include "Globals.h"
#include "InIHelper.h"
#include "Utility.h"
#include "ThriftClient.h"

using namespace Onebox::ShellExt;
using namespace SD;

static std::wstring retriveRealPath(const std::wstring& path)
{
	if (path.empty())
	{
		return L"";
	}
	try
	{
		std::auto_ptr<WCHAR> buf(new WCHAR[MAX_PATH]);
		memset(buf.get(), 0, MAX_PATH);
		DWORD length = GetLongPathName(path.c_str(), buf.get(), MAX_PATH);
		if (0 == length)
		{
			return L"";
		}
		else if (length > MAX_PATH)
		{
			buf.reset(new WCHAR[length]);
			memset(buf.get(), 0, length);
			if (0 == GetLongPathName(path.c_str(), buf.get(), length))
			{
				return L"";
			}
		}
		return std::wstring(buf.get());
	}
	catch(...)
	{
		return L"";
	}
}

IconStatus Global::getOverlayIconStatus(const std::wstring& path)
{
	if (path.empty())
	{
		return IconInvalid;
	}

	int32_t overlayIconStatus = SyncServiceClientWrapper::getInstance()->getOverlayIconStatus(retriveRealPath(path));
	if (0 > overlayIconStatus || overlayIconStatus >= OverlayIcon_Status::OverlayIcon_Status_Invalid)
	{
		return IconInvalid;
	}

	IconStatus iconStatus = IconInvalid;
	switch (overlayIconStatus)
	{
	case OverlayIcon_Status::OverlayIcon_Status_Synced:
		iconStatus = IconSynced;
		break;
	case OverlayIcon_Status::OverlayIcon_Status_Syncing:
		iconStatus = IconSyncing;
		break;
	case OverlayIcon_Status::OverlayIcon_Status_NoActionDelete:
		iconStatus = IconNoAction;
		break;
	default:
		break;
	}
	return iconStatus;
}

bool Global::isSynced(const std::wstring& path)
{
	if (path.empty())
	{
		return false;
	}

	int64_t remoteId = -1;
	if (RT_OK != SyncServiceClientWrapper::getInstance()->getRemoteId(retriveRealPath(path), remoteId))
	{
		return false;
	}

	return (0 < remoteId);
}

bool Global::isSyncFile(const std::wstring& path)
{
	if (path.empty())
	{
		return false;
	}

	// get app path from registry
	std::wstring appPath = L"";
	if (RT_OK != Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, APP_PATH_REG_NAME, appPath))
	{
		return false;
	}

	// get sync dir from config in app path
	CInIHelper iniHelper(appPath.append(CONF_FILE));
	std::wstring syncDir = iniHelper.GetString(CONFIG_SECTION, CONFIG_MONITORPATH_KEY, L"");
	if (syncDir.empty())
	{
		return false;
	}

	syncDir = Utility::FS::format_path(syncDir);
	syncDir = Utility::String::rtrim(syncDir, L"\\");
	syncDir += L"\\";
	std::wstring temp = Utility::FS::format_path(retriveRealPath(path));
	return (Utility::String::to_lower(syncDir)==Utility::String::to_lower(temp.substr(0, syncDir.length())));
}


bool Global::isMonitorRootPath(const std::wstring& path)
{
	if (path.empty())
	{
		return false;
	}

	// get app path from registry
	std::wstring appPath = L"";
	if (RT_OK != Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, APP_PATH_REG_NAME, appPath))
	{
		return false;
	}

	// get sync dir from config in app path
	CInIHelper iniHelper(appPath.append(CONF_FILE));
	std::wstring syncDir = iniHelper.GetString(CONFIG_SECTION, CONFIG_MONITORPATH_KEY, L"");
	if (syncDir.empty())
	{
		return false;
	}

	syncDir = Utility::FS::format_path(syncDir);
	syncDir = Utility::String::rtrim(syncDir, L"/");
	std::wstring temp = Utility::FS::format_path(path);
	return (Utility::String::to_lower(syncDir)==Utility::String::to_lower(temp));
}

int32_t Global::setShare(const std::wstring& path)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}
	return SyncServiceClientWrapper::getInstance()->setShare(retriveRealPath(path));
}

int32_t Global::setShareLink(const std::wstring& path)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}
	return SyncServiceClientWrapper::getInstance()->setShareLink(retriveRealPath(path));
}

int32_t Global::uploadFileOperation(const std::wstring& path)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}

	return SyncServiceClientWrapper::getInstance()->notify(retriveRealPath(path));
}

int32_t Global::getSyncServiceStatus()
{
	// get service status from registry
	int32_t status = Service_Status::Service_Status_Uninitial;
	int32_t ret = Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, SERVER_STATUS_REG_NAME, status);
	if (RT_OK != ret)
	{
		return status;
	}
	return status;
}

bool Global::isSyncServiceNormal()
{
	// check the OneboxSync process is exist
	HANDLE hStorageServiceStopEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT);
	if (NULL == hStorageServiceStopEvent)
	{
		return false;
	}
	CloseHandle(hStorageServiceStopEvent);

	int32_t status = Global::getSyncServiceStatus();
	if(status == Service_Status::Service_Status_Online || 
		status == Service_Status::Service_Status_Pause)
	{
		return true;
	}

	return false;
}

std::wstring Global::getLangPath()
{
	std::wstring fileName = LANG_ZH_PATH;
	LCID lcid = GetSystemDefaultUILanguage();
	if(lcid != 0x804)
	{   
		fileName = LANG_EN_PATH;
	}

	std::wstring langPath = L"";
	if (RT_OK != Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, APP_PATH_REG_NAME, langPath))
	{
		return L"";
	}
	
	langPath = Utility::FS::format_path(langPath);
	langPath = Utility::String::rtrim(langPath, L"/");

	return (langPath+L"/"+fileName);
}
