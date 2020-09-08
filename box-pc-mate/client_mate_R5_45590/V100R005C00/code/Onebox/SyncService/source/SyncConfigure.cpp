#include "SyncConfigure.h"
#include "InIHelper.h"
#include "ConfigureMgr.h"
#include "Utility.h"

#define CONF_SYNC_SECTION (L"SYNCHRONIZE")
#define CONF_MONITOR_PATH_KEY (L"MonitorRootPath")
#define CONF_CACHE_PATH_KEY (L"CachePath")
#define CONF_CACHE_TRASH_PATH_KEY (L"CacheTrashPath")
#define CONF_CACHE_DATA_PATH_KEY (L"CacheDataPath")
#define CONF_VIRTUAL_FOLDER_NAME_KEY (L"VirtualFolderName")
#define CONF_REMOTE_DETECTOR_PERIOD_KEY (L"RemoteDetectorPeriod")
#define CONF_LOCAL_DETECTOR_PERIOD_KEY (L"LocalDetectorPeriod")
#define CONF_SYNC_MODE_KEY (L"SyncMode")
#define CONF_UPLOAD_PERIOD_KEY (L"UploadFilterPeriod")
#define CONF_UPLOAD_NAME_EXT_KEY (L"NameExt")

#define DEFAULT_CACHE_NAME (L".OneboxCache")
#define DEFAULT_CACHE_TRASH_NAME (L"Trash")
#define DEFAULT_CACHE_DATA_NAME (L"Data")
#define DEFAULT_VIRTUAL_FOLDER_NAME (L"Onebox")
#define DEFAULT_REMOTE_DETECTOR_PERIOD (10)
#define DEFAULT_LOCAL_DETECTOR_PERIOD (15*60)
#define DEFAULT_UPLOAD_FILTER_PERIOD (24*60)
#define DEFAULT_UPLOAD_FILTER_EXT (L"pst;nsf;ost")

using namespace SD;

SyncConfigure* SyncConfigure::instance_ = NULL;

static std::wstring formatRootPath(const std::wstring& path)
{
	std::wstring temp = Utility::FS::format_path(path);
	// rtrim all the '\'
	if (!temp.empty())
	{
		std::wstring::size_type pos = temp.find_last_not_of(PATH_DELIMITER);
		temp = temp.substr(0, pos);
	}
	return temp;
}

SyncConfigure* SyncConfigure::getInstance(UserContext* userContext)
{
	if (NULL == instance_)
	{
		instance_ = new SyncConfigure(userContext);
	}
	return instance_;
}

SyncConfigure::SyncConfigure(UserContext* userContext)
	:userContext_(userContext)
{
	CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath());
	
	monitorRootPath(formatRootPath(iniHelper.GetString(CONF_SYNC_SECTION,CONF_MONITOR_PATH_KEY, L"")));
	virtualFolderName(iniHelper.GetString(CONF_SYNC_SECTION, CONF_VIRTUAL_FOLDER_NAME_KEY, DEFAULT_VIRTUAL_FOLDER_NAME));
	cachePath(Utility::FS::format_path(monitorRootPath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_NAME));
	cacheTrashPath(cachePath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_TRASH_NAME);
	cacheDataPath(cachePath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_DATA_NAME);
	syncMode(iniHelper.GetInt32(CONF_SYNC_SECTION, CONF_SYNC_MODE_KEY, 1));

	remoteDetectorPeriod(iniHelper.GetInt32(CONF_SYNC_SECTION, CONF_REMOTE_DETECTOR_PERIOD_KEY, DEFAULT_REMOTE_DETECTOR_PERIOD));
	localDetectorPeriod(iniHelper.GetInt32(CONF_SYNC_SECTION, CONF_LOCAL_DETECTOR_PERIOD_KEY, DEFAULT_LOCAL_DETECTOR_PERIOD));

	uploadFilterPeriod(iniHelper.GetInt32(CONF_SYNC_SECTION, CONF_UPLOAD_PERIOD_KEY, DEFAULT_UPLOAD_FILTER_PERIOD));
	uploadFilterStr(iniHelper.GetString(CONF_SYNC_SECTION, CONF_UPLOAD_NAME_EXT_KEY, DEFAULT_UPLOAD_FILTER_EXT));

	// if the monitor root path is exist then create cache data directory
	if (Utility::FS::is_directory(monitorRootPath()) && !Utility::FS::is_directory(cacheDataPath()))
	{
		Utility::FS::create_directories(cacheDataPath());
		// hide the cache path
		DWORD attr = GetFileAttributes(cachePath().c_str());
		if ( INVALID_FILE_ATTRIBUTES != attr)
		{
			attr |= FILE_ATTRIBUTE_HIDDEN;
			SetFileAttributes(cachePath().c_str(), attr);
		}
	}
}