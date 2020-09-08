#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include <Windows.h>

using namespace SD;

#define CONF_CONFIGURE_SECTION (L"CONFIGURE")
#define CONF_USER_DATA_PATH_KEY (L"UserDataPath")
#define CONF_UPLOAD_FILTER_PERIOD_KEY (L"UploadFilterPeriod")
#define CONF_DELAY_LOADING_KEY (L"DelayLoading")
#define CONF_DISABLE_ATTR_KEY (L"DisableAttr")

#define CONF_TRANS_TASK_SECTION (L"TRANSTASK")
#define CONF_MAX_ASYNC_TRANS_TASK_KEY (L"MaxAsyncTransTask")
#define CONF_MAX_ASYNC_TRANS_THREAD_KEY (L"MaxAsyncTransThread")
#define CONF_ALGORITHM_KEY (L"Algorithm")
#define CONF_LOW_MD5_SIZE_KEY (L"LowMD5Size")
#define CONF_MIDDLE_MD5_SIZE_KEY (L"MiddleMD5Size")
#define CONF_TRANS_BIG_FILE_SIZE (L"BigFileSize")
#define CONF_TRANS_MAX_PART_SIZE (L"MaxPartSize")
#define CONF_TRANS_MIN_PART_SIZE (L"MinPartSize")
#define CONF_TRANS_MAX_PART_COUNT (L"MaxPartCount")
#define CONF_TRANS_MULTI_THREADS_LIMIT_SPEED (L"MultiThreadsSpeedLimit")
#define CONF_TRANS_RETRY_TASK_ERRORCODES (L"RetryTaskErrorCodes")
#define CONF_TRANS_RETRY_TASK_INTERVAL (L"RetryTaskInterval")
#define CONF_TRANS_RETRY_TASK_TIMES (L"RetryTaskTimes")

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
#define CONF_REQUEST_LOW_SPEED_LIMIT_KEY (L"RequestLowSpeedLimit")
#define CONF_REQUEST_CONNECT_TIME_OUT_KEY (L"RequestConnectTimeOut")
#define CONF_CAPATH (L"CAPath")

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

#define DEFAULT_UPLOAD_FILTER_PERIOD (10080) // minute
#define DEFAULT_DELAY_LOADING (1000) // milliseconds
#define DEFAULT_DISABLE_ATTR (6)	//FILE_ATTRIBUTE_HIDDEN|FILE_ATTRIBUTE_SYSTEM
#define DEFAULT_VERSION (L"1.0.0.0")
#define DEFAULT_MAX_ASYNC_TASK (5)
#define DEFAULT_MAX_ASYNC_THREAD (5)
#define DEFAULT_ASYNC_TASK (3)
#define DEFAULT_ASYNC_THREAD (3)
#define DEFAULT_MIN_ASYNC_TASK (2)
#define DEFAULT_MIN_ASYNC_THREAD (2)
#define DEFAULT_REQUEST_TIMEOUT (60*3) // seconds
#define DEFAULT_REQUEST_LOWSPEEDLIMIT (5*1024) // B/s
#define DEFAULT_REQUEST_CONNECT_TIMEOUT (5)
#define DEFAULT_USER_DATA_NAME (L"UserData")
#define DEFAULT_LOW_MD5_SIZE (256)
#define DEFAULT_MIDDLE_MD5_SIZE (256*1024)
#define DEFAULT_CANAME (L"onebox.crt")
#define DEFAULT_FILTER_NAME_EXT (L"tmp;lnk")
#define DEFAULT_FILTER_NAME_END (L".")
#define DEFAULT_FILTER_NAME_START (L".;~;CON.;PRN.;AUX.;NUL.;COM1.;COM2.;COM3.;COM4.;COM5.;COM6.;COM7.;COM8.;COM9.;LPT1.;LPT2.;LPT3.;LPT4.;LPT5.;LPT6.;LPT7.;LPT8.;LPT9.")
#define DEFAULT_FILTER_NAME (L"CON;PRN;AUX;NUL;COM1;COM2;COM3;COM4;COM5;COM6;COM7;COM8;COM9;LPT1;LPT2;LPT3;LPT4;LPT5;LPT6;LPT7;LPT8;LPT9")

#define DEFAULT_MIN_PART_SIZE (5*1024*1024)
#define DEFAULT_MAX_PART_SIZE (500*1024*1024)
#define DEFAULT_MAX_PART_COUNT (10000)
#define DEFAULT_MULTI_THREADS_SPEED_LIMIT (1024*1024) // 1MB/s
#define DEFAULT_RETRY_TASK_INTERVAL (30*1000)
#define DEFAULT_RETRY_TASK_TIMES (5)
#define DEFAULT_RETRY_TASK_ERRORCODES (L"-1500;-2006;-2007;-2023;-2026;-2028;-2035;-2052;-2055;-2056;32;-103;-9002;-9003;-1401;-1403;-1007,-1009")
#define DEFAULT_BIG_FILE_SIZE (100*1024*1024)

class ConfigureMgrImpl : public ConfigureMgr
{
public:
	ConfigureMgrImpl(UserContext* userContext, const std::wstring& confPath);

	virtual int32_t serialize();

	virtual int32_t unserialize();

	virtual Configure* getConfigure();

	virtual int32_t updateUserConfigurePath(const int64_t userId, const int64_t parentId);

private:
	UserContext* userContext_;
	std::wstring userConfPath_;
	std::auto_ptr<Configure> configure_;
};

ConfigureMgr* ConfigureMgr::create(UserContext* userContext, const std::wstring& confPath)
{
	return static_cast<ConfigureMgr*>(new ConfigureMgrImpl(userContext, confPath));
}

ConfigureMgrImpl::ConfigureMgrImpl(UserContext* userContext, const std::wstring& confPath)
	:userConfPath_(Utility::FS::format_path(confPath))
	,userContext_(userContext)
	,configure_(new Configure)
{
	try
	{
		// C:/Program Files/Onebox/config.ini
		// other settings are loaded from user config (eg:C:/Program Files/Onebox/UserData/10/config.ini)
		configure_->appPath(Utility::FS::get_work_directory());
		configure_->appUserDataPath(Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+DEFAULT_USER_DATA_NAME);
		configure_->appConfigPath(configure_->appPath()+PATH_DELIMITER+DEFAULT_CONFIG_NAME);
		if (Utility::FS::is_exist(configure_->appConfigPath()))
		{
			CInIHelper iniHelper(configure_->appConfigPath());
			configure_->version(iniHelper.GetString(CONF_VERSION_SECTION, CONF_VERSION_KEY, DEFAULT_VERSION));
			configure_->serverUrl(iniHelper.GetString(CONF_NETWORK_SECTION, CONF_SERVER_URL_KEY, L""));
			configure_->useSSL(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_SSL_KEY, 1)!=0);

			ProxyInfo proxyInfo;
			proxyInfo.useProxy = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_KEY, 0)!=0);
			proxyInfo.useProxyAthen = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_AUTHEN_KEY, 0)!=0);
			proxyInfo.proxyServer = iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_URL_KEY, L"");
			proxyInfo.proxyPort = iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_PORT_KEY, 8080);
			proxyInfo.proxyUserName = iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_USERNAME_KEY, L"");
			proxyInfo.proxyPassword = Utility::String::decrypt_string(iniHelper.GetString(CONF_NETWORK_SECTION, CONF_PROXY_PASSWORD_KEY, L""));
			configure_->proxyInfo(proxyInfo);

			configure_->requestTimeout(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_REQUEST_TIME_OUT_KEY, DEFAULT_REQUEST_TIMEOUT));
			configure_->requestLowSpeedLimit(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_REQUEST_LOW_SPEED_LIMIT_KEY, DEFAULT_REQUEST_LOWSPEEDLIMIT));
			configure_->requestConnectTimeout(iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_REQUEST_CONNECT_TIME_OUT_KEY, DEFAULT_REQUEST_CONNECT_TIMEOUT));

			configure_->capath(iniHelper.GetString(CONF_NETWORK_SECTION, CONF_CAPATH, configure_->appPath()+PATH_DELIMITER+DEFAULT_CANAME));

			AsyncTransTaskPoolConf asyncTransTaskPoolConf;
			asyncTransTaskPoolConf.maxThreads = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, 0);
			asyncTransTaskPoolConf.maxTasks = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY, 0);
			if (asyncTransTaskPoolConf.maxThreads > DEFAULT_MAX_ASYNC_THREAD || 
				asyncTransTaskPoolConf.maxThreads < DEFAULT_MIN_ASYNC_THREAD) 
				asyncTransTaskPoolConf.maxThreads = DEFAULT_ASYNC_THREAD;
			if (asyncTransTaskPoolConf.maxTasks > DEFAULT_MAX_ASYNC_TASK || 
				asyncTransTaskPoolConf.maxTasks < DEFAULT_MIN_ASYNC_TASK) 
				asyncTransTaskPoolConf.maxTasks = DEFAULT_ASYNC_TASK;
			configure_->asyncTransTaskPoolConf(asyncTransTaskPoolConf);

			configure_->bigFileSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_BIG_FILE_SIZE, DEFAULT_BIG_FILE_SIZE));
			configure_->maxPartSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_SIZE, DEFAULT_MAX_PART_SIZE));
			configure_->minPartSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_MIN_PART_SIZE, DEFAULT_MIN_PART_SIZE));
			configure_->maxPartCount(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_COUNT, DEFAULT_MAX_PART_COUNT));
			configure_->retryTaskErrorCodes(iniHelper.GetString(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_SIZE, DEFAULT_RETRY_TASK_ERRORCODES));
			configure_->retryTaskInterval(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_RETRY_TASK_INTERVAL, DEFAULT_RETRY_TASK_INTERVAL));
			configure_->retryTaskTimes(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_RETRY_TASK_TIMES, DEFAULT_RETRY_TASK_TIMES));

			configure_->uploadFilterPeriod(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_UPLOAD_FILTER_PERIOD_KEY, DEFAULT_UPLOAD_FILTER_PERIOD));
			configure_->delayLoading(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_DELAY_LOADING_KEY, DEFAULT_DELAY_LOADING));
			configure_->disableAttr(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_DISABLE_ATTR_KEY, DEFAULT_DISABLE_ATTR));
			// if the userConfPath is a valid path, unserialize the configure by userConfPath
			if (!userConfPath_.empty())
			{
				(void)unserialize();
			}
		}
	}
	catch(...){}
}

int32_t ConfigureMgrImpl::serialize()
{
	if (userConfPath_.empty())
	{
		return RT_INVALID_PARAM;
	}
	if (!Utility::FS::is_exist(userConfPath_))
	{
		Utility::FS::create_directories(Utility::FS::get_parent_path(userConfPath_));
	}

	CInIHelper iniHelper(userConfPath_);

	//iniHelper.SetString(CONF_CONFIGURE_SECTION, CONF_USER_DATA_PATH_KEY, Utility::FS::format_path(configure_->userDataPath()));

	iniHelper.SetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, configure_->asyncTransTaskPoolConf().maxThreads);
	iniHelper.SetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY, configure_->asyncTransTaskPoolConf().maxTasks);

	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_KEY, configure_->proxyInfo().useProxy);
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_PROXY_AUTHEN_KEY, configure_->proxyInfo().useProxyAthen);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_URL_KEY, configure_->proxyInfo().proxyServer);
	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_PROXY_SEVER_PORT_KEY, configure_->proxyInfo().proxyPort);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_USERNAME_KEY, configure_->proxyInfo().proxyUserName);
	//iniHelper.SetString(CONF_NETWORK_SECTION, CONF_PROXY_PASSWORD_KEY, configure_->proxyInfo().proxyPassword);

	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_USERNAME_KEY, configure_->userName());
	iniHelper.SetString(CONF_USERINFO_SECTION, CONF_PASSWORD_KEY, configure_->password());
	//iniHelper.SetString(CONF_USERINFO_SECTION, CONF_ACCOUNTGUID_KEY, configure_->accountGuid());
	//iniHelper.SetString(CONF_USERINFO_SECTION, CONF_USER_DOMAIN_KEY, configure_->userDomain());
	//iniHelper.SetString(CONF_USERINFO_SECTION, CONF_DEVICE_NAME_KEY, configure_->deviceName());
	//iniHelper.SetString(CONF_USERINFO_SECTION, CONF_STORAGEDOMAIN_KEY, configure_->storageDomain());
	iniHelper.SetInt32(CONF_USERINFO_SECTION, CONF_LOGIN_TYPE_KEY, configure_->loginType());

	//iniHelper.SetInt32(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, configure_->localSpeedLimitConf().useSpeedLimit);
	//iniHelper.SetInt64(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, configure_->localSpeedLimitConf().maxUploadSpeed);
	//iniHelper.SetInt64(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, configure_->localSpeedLimitConf().maxDownloadSpeed);

	return RT_OK;
}

int32_t ConfigureMgrImpl::unserialize()
{
	if (userConfPath_.empty())
	{
		return RT_INVALID_PARAM;
	}

	if (!Utility::FS::is_exist(userConfPath_))
	{
		return RT_FILE_NOEXIST_ERROR;
	}

	CInIHelper iniHelper(userConfPath_);
	configure_->configureRootPath(userConfPath_);
	configure_->userDataPath(Utility::FS::get_parent_path(userConfPath_));

	AsyncTransTaskPoolConf asyncTransTaskPoolConf;
	asyncTransTaskPoolConf.maxThreads = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, 0);
	asyncTransTaskPoolConf.maxTasks = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY, 0);
	if (asyncTransTaskPoolConf.maxThreads <= 0 || asyncTransTaskPoolConf.maxTasks <= 0)
	{
		asyncTransTaskPoolConf = configure_->asyncTransTaskPoolConf();
	}
	if (asyncTransTaskPoolConf.maxThreads > DEFAULT_MAX_ASYNC_THREAD || 
		asyncTransTaskPoolConf.maxThreads < DEFAULT_MIN_ASYNC_THREAD)
	{
		asyncTransTaskPoolConf.maxThreads = DEFAULT_MIN_ASYNC_THREAD;
	}
	if (asyncTransTaskPoolConf.maxTasks > DEFAULT_MAX_ASYNC_TASK || 
		asyncTransTaskPoolConf.maxTasks < DEFAULT_MIN_ASYNC_TASK)
	{
		asyncTransTaskPoolConf.maxTasks = DEFAULT_MIN_ASYNC_TASK;
	}
	configure_->asyncTransTaskPoolConf(asyncTransTaskPoolConf);

	configure_->bigFileSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_BIG_FILE_SIZE, DEFAULT_BIG_FILE_SIZE));
	configure_->maxPartSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_SIZE, DEFAULT_MAX_PART_SIZE));
	configure_->minPartSize(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_MIN_PART_SIZE, DEFAULT_MIN_PART_SIZE));
	configure_->maxPartCount(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_COUNT, DEFAULT_MAX_PART_COUNT));
	configure_->multiThreadsSpeedLimit(iniHelper.GetInt64(CONF_TRANS_TASK_SECTION, CONF_TRANS_MULTI_THREADS_LIMIT_SPEED, DEFAULT_MULTI_THREADS_SPEED_LIMIT));
	configure_->retryTaskErrorCodes(iniHelper.GetString(CONF_TRANS_TASK_SECTION, CONF_TRANS_MAX_PART_SIZE, DEFAULT_RETRY_TASK_ERRORCODES));
	configure_->retryTaskInterval(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_RETRY_TASK_INTERVAL, DEFAULT_RETRY_TASK_INTERVAL));
	configure_->retryTaskTimes(iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_TRANS_RETRY_TASK_TIMES, DEFAULT_RETRY_TASK_TIMES));

	AlgorithmConf algorithmConf;
	algorithmConf.algorithm = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_ALGORITHM_KEY, Fingerprint::MD5);
	algorithmConf.lowMD5Size = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_LOW_MD5_SIZE_KEY, DEFAULT_LOW_MD5_SIZE);
	algorithmConf.middleMD5Size = iniHelper.GetInt32(CONF_TRANS_TASK_SECTION, CONF_MIDDLE_MD5_SIZE_KEY, DEFAULT_MIDDLE_MD5_SIZE);
	configure_->algorithmConf(algorithmConf);

	SpeedLimitConf speedLimitConf;
	speedLimitConf.useSpeedLimit = (iniHelper.GetInt32(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, 0)!=0);
	speedLimitConf.maxUploadSpeed = iniHelper.GetInt64(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, 0);
	speedLimitConf.maxDownloadSpeed = iniHelper.GetInt64(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, 0);
	configure_->localSpeedLimitConf(speedLimitConf);

	configure_->uploadFilterPeriod(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_UPLOAD_FILTER_PERIOD_KEY, DEFAULT_UPLOAD_FILTER_PERIOD));
	configure_->delayLoading(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_DELAY_LOADING_KEY, DEFAULT_DELAY_LOADING));
	configure_->disableAttr(iniHelper.GetInt32(CONF_CONFIGURE_SECTION, CONF_DISABLE_ATTR_KEY, DEFAULT_DISABLE_ATTR));

	configure_->userName(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_USERNAME_KEY, L""));
	configure_->password(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_PASSWORD_KEY, L""));
	configure_->accountGuid(iniHelper.GetString(CONF_USERINFO_SECTION, CONF_ACCOUNTGUID_KEY, L""));
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

	return RT_OK;
}

Configure* ConfigureMgrImpl::getConfigure()
{
	return configure_.get();
}

int32_t ConfigureMgrImpl::updateUserConfigurePath(const int64_t userId, const int64_t parentId)
{
	if (userId <= 0)
	{
		return RT_INVALID_PARAM;
	}

	// user data path, <user id is 1>eg: C:/Program Files/Onebox/UserData/1/1/Config.ini
	std::wstring strUserId = Utility::String::type_to_string<std::wstring>(userId);
	std::wstring strParentId = Utility::String::type_to_string<std::wstring>(parentId);
	std::wstring userConfigurePath = getConfigure()->appUserDataPath()+PATH_DELIMITER+
		strParentId+PATH_DELIMITER+strUserId+PATH_DELIMITER+DEFAULT_CONFIG_NAME;

	userConfPath_ = userConfigurePath;
	// if the config file is exist, first unserialize
	if (Utility::FS::is_exist(userConfPath_))
	{
		return unserialize();
	}
	// create configure file
	return serialize();
}
