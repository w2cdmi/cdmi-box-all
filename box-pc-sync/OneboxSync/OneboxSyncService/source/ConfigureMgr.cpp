#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include <Windows.h>

using namespace SD;

#define CONF_CONFIGURE_SECTION (L"CONFIGURE")
#define CONF_USER_DATA_PATH_KEY (L"UserDataPath")
#define CONF_METADATA_PATH_KEY (L"MetaDataRootPath")
#define CONF_MONITOR_PATH_KEY (L"MonitorRootPath")
#define CONF_CACHE_PATH_KEY (L"CachePath")
#define CONF_CACHE_TRASH_PATH_KEY (L"CacheTrashPath")
#define CONF_CACHE_DATA_PATH_KEY (L"CacheDataPath")
#define CONF_VIRTUAL_FOLDER_NAME_KEY (L"VirtualFolderName")
#define CONF_REMOTE_DETECTOR_PERIOD_KEY (L"RemoteDetectorPeriod")
#define CONF_LOCAL_DETECTOR_PERIOD_KEY (L"LocalDetectorPeriod")
#define CONF_SYNC_MODEL_KEY (L"SyncModel")

#define CONF_TRANS_TASK_SECTION (L"TRANSTASK")
#define CONF_MAX_ASYNC_TRANS_TASK_KEY (L"MaxAsyncTransTask")
#define CONF_MAX_ASYNC_TRANS_THREAD_KEY (L"MaxAsyncTransThread")
#define CONF_ALGORITHM_KEY (L"Algorithm")
#define CONF_LOW_MD5_SIZE_KEY (L"LowMD5Size")
#define CONF_MIDDLE_MD5_SIZE_KEY (L"MiddleMD5Size")

#define CONF_VERSION_SECTION (L"VERSION")
#define CONF_VERSION_KEY (L"Version")

#define CONF_NETWORK_SECTION (L"NETWORK")
#define CONF_SERVER_URL_KEY (L"StorageServerURL")
#define CONF_MAX_UPLOAD_SPEED_KEY (L"MaxUploadSpeed")
#define CONF_MAX_DWONLOAD_SPEED_KEY (L"MaxDownloadSpeed")
#define CONF_PROXY_SEVER_URL_KEY (L"ProxyServerURL")
#define CONF_PROXY_SEVER_PORT_KEY (L"ProxyServerPort")
#define CONF_PROXY_USERNAME_KEY (L"ProxyUsername")
#define CONF_PROXY_PASSWORD_KEY (L"ProxyPassword")
#define CONF_USE_SSL_KEY (L"UseSSL")
#define CONF_USE_PROXY_KEY (L"UseProxy")
#define CONF_USE_PROXY_AUTHEN_KEY (L"UseProxyAuthen")
#define CONF_USE_SPEED_LIMIT_KEY (L"UseSpeedLimit")
#define CONF_REQUEST_TIME_OUT_KEY (L"RequestTimeOut")

#define CONF_USERINFO_SECTION (L"USERINFO")
#define CONF_USERNAME_KEY (L"UserName")
#define CONF_PASSWORD_KEY (L"PassWord")
#define CONF_ACCOUNTGUID_KEY (L"AccountGuid")
#define CONF_USER_DOMAIN_KEY (L"UserDomain")
#define CONF_DEVICE_NAME_KEY (L"DeviceName")
#define CONF_STORAGEDOMAIN_KEY (L"StorageDomain")
#define CONF_LOGIN_TYPE_KEY (L"LoginType")

#define CONF_FILTER_SECTION (L"FILTER")
#define CONF_NAME_EXT_KEY (L"NameExt")
#define CONF_NAME_START_KEY (L"NameStart")
#define CONF_NAME_END_KEY (L"NameEnd")
#define CONF_NAME_KEY (L"Name")
#define CONF_UPLOAD_NAME_EXT_KEY (L"NameExt")
#define CONF_UPLOAD_PERIOD_KEY (L"UploadFilterPeriod")

#define DEFAULT_VERSION (L"1.0.0.0")
#define DEFAULT_MAX_TRANS_TASK 5
#define DEFAULT_MAX_TRANS_THREAD 1
#define DEFAULT_MAX_ASYNC_TASK 5
#define DEFAULT_MAX_ASYNC_THREAD 5
#define DEFAULT_REQUEST_TIMEOUT (60)
#define DEFAULT_USER_DATA_NAME (L"UserData")
#define DEFAULT_CACHE_NAME (L".OneboxCache")
#define DEFAULT_CACHE_TRASH_NAME (L"Trash")
#define DEFAULT_CACHE_DATA_NAME (L"Data")
#define DEFAULT_VIRTUAL_FOLDER_NAME (L"Onebox")
#define DEFAULT_REMOTE_DETECTOR_PERIOD (120)
#define DEFAULT_LOCAL_DETECTOR_PERIOD (15*60)
#define DEFAULT_UPLOAD_FILTER_PERIOD (24*60)
#define DEFAULT_LOW_MD5_SIZE (256) // 256 byte
#define DEFAULT_MIDDLE_MD5_SIZE (256*1024) // 256KB

#define DEFAULT_FILTER_NAME_EXT (L"tmp;lnk")
#define DEFAULT_FILTER_NAME_END (L".")
#define DEFAULT_FILTER_NAME_START (L".;~;CON.;PRN.;AUX.;NUL.;COM1.;COM2.;COM3.;COM4.;COM5.;COM6.;COM7.;COM8.;COM9.;LPT1.;LPT2.;LPT3.;LPT4.;LPT5.;LPT6.;LPT7.;LPT8.;LPT9.")
#define DEFAULT_FILTER_NAME (L"desktop.ini;thumbs.db;CON;PRN;AUX;NUL;COM1;COM2;COM3;COM4;COM5;COM6;COM7;COM8;COM9;LPT1;LPT2;LPT3;LPT4;LPT5;LPT6;LPT7;LPT8;LPT9")
#define DEFAULT_UPLOAD_FILTER_EXT (L"pst;nsf;ost")

class ConfigureMgrImpl : public ConfigureMgr
{
public:
	ConfigureMgrImpl(UserContext* userContext, const std::wstring& confPath);

	virtual int32_t serialize();

	virtual int32_t unserialize();

	virtual Configure* getConfigure();

	virtual SyncRules* getSyncRules();

private:
	UserContext* userContext_;
	std::wstring confPath_;
	std::auto_ptr<Configure> configure_;
	std::auto_ptr<SyncRules> syncRules_;
};

ConfigureMgr* ConfigureMgr::create(UserContext* userContext, const std::wstring& confPath)
{
	return static_cast<ConfigureMgr*>(new ConfigureMgrImpl(userContext, confPath));
}

ConfigureMgrImpl::ConfigureMgrImpl(UserContext* userContext, const std::wstring& confPath)
	:userContext_(userContext)
	,confPath_(confPath)
	,configure_(new Configure)
{
	try
	{
		//load the configure file
		unserialize();
		
		//load sync rules
		syncRules_ = SyncRules::create();
	}
	catch(...){}
}

int32_t ConfigureMgrImpl::serialize()
{
	if (confPath_.empty())
	{
		return RT_INVALID_PARAM;
	}
	if (!Utility::FS::is_exist(confPath_))
	{
		return RT_FILE_NOEXIST_ERROR;
	}

	CInIHelper iniHelper(confPath_);

	iniHelper.SetString(CONF_VERSION_SECTION, CONF_VERSION_KEY, configure_->version());

	//iniHelper.SetString(CONF_CONFIGURE_SECTION, CONF_USER_DATA_PATH_KEY, Utility::FS::format_path(configure_->userDataPath()));
	iniHelper.SetString(CONF_CONFIGURE_SECTION,CONF_MONITOR_PATH_KEY, Utility::FS::format_path(configure_->monitorRootPath()));
	iniHelper.SetInt32(CONF_CONFIGURE_SECTION,CONF_SYNC_MODEL_KEY, configure_->syncModel());
	
	//iniHelper.SetString(CONF_CONFIGURE_SECTION, CONF_CACHE_PATH_KEY, Utility::FS::format_path(configure_->cachePath()));
	//iniHelper.SetString(CONF_CONFIGURE_SECTION, CONF_VIRTUAL_FOLDER_NAME_KEY, configure_->virtualFolderName());

	//iniHelper.SetInt32(CONF_CONFIGURE_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, configure_->asyncTransTaskPoolConf().maxThreads);
	//iniHelper.SetInt32(CONF_CONFIGURE_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY, configure_->asyncTransTaskPoolConf().maxTasks);

	iniHelper.SetString(CONF_NETWORK_SECTION, CONF_SERVER_URL_KEY, configure_->serverUrl());
	iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_SSL_KEY, configure_->useSSL());
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_REQUEST_TIME_OUT_KEY, configure_->requestTimeout());
	iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, configure_->speedLimitConf().useSpeedLimit);
	iniHelper.SetInt64(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, configure_->speedLimitConf().maxUploadSpeed);
	iniHelper.SetInt64(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, configure_->speedLimitConf().maxDownloadSpeed);
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_KEY, configure_->proxyInfo().useProxy);
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_AUTHEN_KEY, configure_->proxyInfo().useProxyAthen);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_URL_KEY, configure_->proxyInfo().proxyServer);
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_PORT_KEY, configure_->proxyInfo().proxyPort);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_USERNAME_KEY, configure_->proxyInfo().proxyUserName);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_PASSWORD_KEY, Utility::String::encrypt_string(configure_->proxyInfo().proxyPassword));

	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_USERNAME_KEY, configure_->userName());
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_PASSWORD_KEY, Utility::String::encrypt_string(configure_->password()));
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_ACCOUNTGUID_KEY, Utility::String::encrypt_string(configure_->accountGuid()));
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_USER_DOMAIN_KEY, configure_->userDomain());
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_DEVICE_NAME_KEY, configure_->deviceName());
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_STORAGEDOMAIN_KEY, configure_->storageDomain());
	iniHelper.SetInt32(CONF_USERINFO_SECTION, CONF_LOGIN_TYPE_KEY, configure_->loginType());

	return RT_OK;
}

int32_t ConfigureMgrImpl::unserialize()
{
	if (confPath_.empty())
	{
		return RT_INVALID_PARAM;
	}
	if (!Utility::FS::is_exist(confPath_))
	{
		return RT_FILE_NOEXIST_ERROR;
	}

	CInIHelper iniHelper(confPath_);
	configure_->configureRootPath(confPath_);
	configure_->version(iniHelper.GetString(CONF_VERSION_SECTION, CONF_VERSION_KEY, DEFAULT_VERSION));

	configure_->userDataPath(Utility::FS::get_work_directory()+PATH_DELIMITER+Utility::FS::format_path(iniHelper.GetString(CONF_CONFIGURE_SECTION, CONF_USER_DATA_PATH_KEY, DEFAULT_USER_DATA_NAME)));

	std::wstring tempRootPath = Utility::FS::format_path(iniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_MONITOR_PATH_KEY, L""));
	// rtrim all the '\'
	std::wstring::size_type pos = tempRootPath.rfind(PATH_DELIMITER);
	while (!tempRootPath.empty() && pos == (tempRootPath.length()-1))
	{
		tempRootPath = tempRootPath.substr(0, pos);
		pos = tempRootPath.rfind(PATH_DELIMITER);
	}
	configure_->monitorRootPath(tempRootPath);

	configure_->virtualFolderName(iniHelper.GetString(CONF_CONFIGURE_SECTION, CONF_VIRTUAL_FOLDER_NAME_KEY, DEFAULT_VIRTUAL_FOLDER_NAME));
	
	std::wstring cachePath, cacheTrashPath, cacheDataPath;
	//cachePath = iniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_CACHE_PATH_KEY, L"");
	cachePath = /*cachePath.empty()?*/(configure_->monitorRootPath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_NAME)/*:cachePath*/;
	configure_->cachePath(Utility::FS::format_path(cachePath));
	cacheTrashPath = /*cacheTrashPath.empty()?*/(configure_->cachePath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_TRASH_NAME)/*:cacheTrashPath*/;
	//cacheTrashPath = iniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_CACHE_TRASH_PATH_KEY, L"");
	configure_->cacheTrashPath(Utility::FS::format_path(cacheTrashPath));
	//cacheDataPath = iniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_CACHE_DATA_PATH_KEY, L"");
	cacheDataPath = /*cacheDataPath.empty()?*/(configure_->cachePath()+std::wstring(PATH_DELIMITER)+DEFAULT_CACHE_DATA_NAME)/*:cacheDataPath*/;
	configure_->cacheDataPath(Utility::FS::format_path(cacheDataPath));
	// if the monitor root path is exist then create cache data dirctory
	if (Utility::FS::is_directory(configure_->monitorRootPath()) && !Utility::FS::is_directory(cacheDataPath))
	{
		Utility::FS::create_directories(cacheDataPath);
		// set the cach path hiden
		DWORD attr = GetFileAttributes(configure_->cachePath().c_str());
		attr |= FILE_ATTRIBUTE_HIDDEN;
		SetFileAttributes(configure_->cachePath().c_str(), attr);
	}

	configure_->syncModel(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_SYNC_MODEL_KEY, 1));

	AsyncTransTaskPoolConf asyncTransTaskPoolConf;
	asyncTransTaskPoolConf.maxThreads = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, DEFAULT_MAX_TRANS_THREAD);
	asyncTransTaskPoolConf.maxTasks = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY, DEFAULT_MAX_TRANS_TASK);
	configure_->asyncTransTaskPoolConf(asyncTransTaskPoolConf);

	AlgorithmConf algorithmConf;
	algorithmConf.algorithm = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_ALGORITHM_KEY, FileSignature::MD5);
	algorithmConf.lowMD5Size = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_LOW_MD5_SIZE_KEY, DEFAULT_LOW_MD5_SIZE);
	algorithmConf.middleMD5Size = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MIDDLE_MD5_SIZE_KEY, DEFAULT_MIDDLE_MD5_SIZE);
	configure_->algorithmConf(algorithmConf);

	configure_->remoteDetectorPeriod(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_REMOTE_DETECTOR_PERIOD_KEY, DEFAULT_REMOTE_DETECTOR_PERIOD));
	configure_->localDetectorPeriod(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_LOCAL_DETECTOR_PERIOD_KEY, DEFAULT_LOCAL_DETECTOR_PERIOD));
	configure_->uploadFilterPeriod(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_UPLOAD_PERIOD_KEY, DEFAULT_UPLOAD_FILTER_PERIOD));

	configure_->serverUrl(iniHelper.GetString(CONF_NETWORK_SECTION, CONF_SERVER_URL_KEY, L""));
	configure_->useSSL(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_SSL_KEY, 0)!=0);
	configure_->requestTimeout(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_REQUEST_TIME_OUT_KEY, DEFAULT_REQUEST_TIMEOUT));

	SpeedLimitConf speedLimitConf;
	speedLimitConf.useSpeedLimit = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, 0)!=0);
	speedLimitConf.maxUploadSpeed = iniHelper.GetInt64(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, 0);
	speedLimitConf.maxDownloadSpeed = iniHelper.GetInt64(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, 0);
	configure_->speedLimitConf(speedLimitConf);

	ProxyInfo proxyInfo;
	proxyInfo.useProxy = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_KEY, 0)!=0);
	proxyInfo.useProxyAthen = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_AUTHEN_KEY, 0)!=0);
	proxyInfo.proxyServer = iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_URL_KEY, L"");
	proxyInfo.proxyPort = iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_PORT_KEY, 8080);
	proxyInfo.proxyUserName = iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_USERNAME_KEY, L"");
	proxyInfo.proxyPassword = Utility::String::decrypt_string(iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_PASSWORD_KEY, L""));
	configure_->proxyInfo(proxyInfo);

	configure_->userName(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_USERNAME_KEY, L""));
	configure_->password(Utility::String::decrypt_string(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_PASSWORD_KEY, L"")));
	configure_->accountGuid(Utility::String::decrypt_string(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_ACCOUNTGUID_KEY, L"")));
	configure_->userDomain(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_USER_DOMAIN_KEY, L""));
	configure_->deviceName(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_DEVICE_NAME_KEY, L""));
	configure_->storageDomain(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_STORAGEDOMAIN_KEY, L""));
	configure_->loginType(iniHelper.GetInt32(CONF_USERINFO_SECTION, CONF_LOGIN_TYPE_KEY, LoginTypeDomain));

	FilterStr filterStr;
	filterStr.nameFilter = iniHelper.GetString(CONF_FILTER_SECTION, CONF_NAME_KEY, DEFAULT_FILTER_NAME);
	filterStr.nameStartFilter = iniHelper.GetString(CONF_FILTER_SECTION, CONF_NAME_START_KEY, DEFAULT_FILTER_NAME_START);
	filterStr.nameEndFilter = iniHelper.GetString(CONF_FILTER_SECTION, CONF_NAME_END_KEY, DEFAULT_FILTER_NAME_END);
	filterStr.nameExtFilter = iniHelper.GetString(CONF_FILTER_SECTION, CONF_NAME_EXT_KEY, DEFAULT_FILTER_NAME_EXT);
	configure_->filterStr(filterStr);

	std::wstring uploadFilterStr;
	uploadFilterStr = iniHelper.GetString(CONF_FILTER_SECTION, CONF_UPLOAD_NAME_EXT_KEY, DEFAULT_UPLOAD_FILTER_EXT);
	configure_->uploadFilterStr(uploadFilterStr);

	return RT_OK;
}

Configure* ConfigureMgrImpl::getConfigure()
{
	return configure_.get();
}

SyncRules* ConfigureMgrImpl::getSyncRules()
{
	return syncRules_.get();
}
