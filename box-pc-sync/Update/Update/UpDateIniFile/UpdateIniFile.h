#pragma once
#ifndef _CONFIGGURE_H_
#define _CONFIGGURE_H_

#include <string>
#include <map>
#include "IniHelper.h"
#include "../Utility/Utility.h"


#define CONF_CONFIGURE_SECTION (L"CONFIGURE")
#define CONF_USER_DATA_PATH_KEY (L"UserDataPath")
#define CONF_METADATA_PATH_KEY (L"MetaDataRootPath")
#define CONF_MONITOR_PATH_KEY (L"MonitorRootPath")
#define CONF_CACHE_PATH_KEY (L"CachePath")
#define CONF_MAX_TRANS_TASK (L"MaxTransTask")
#define CONF_MAX_TRANS_THREAD (L"MaxTransThread")
#define CONF_MAX_ASYNC_TASK (L"MaxAsyncTask")
#define CONF_MAX_ASYNC_THREAD (L"MaxAsyncThread")
#define CONF_VIRTUAL_FOLDER_NAME_KEY (L"VirtualFolderName")

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
#define CONF_USE_SSL (L"UseSSL")
#define CONF_USE_PROXY (L"UseProxy")
#define CONF_USE_PROXY_AUTHEN (L"UseProxyAuthen")
#define CONF_USE_SPEED_LIMIT (L"UseSpeedLimit")
#define CONF_REQUEST_TIME_OUT (L"RequestTimeOut")

#define CONF_USERINFO_SECTION (L"USERINFO")
#define CONF_USERNAME_KEY (L"UserName")
#define CONF_PASSWORD_KEY (L"PassWord")
#define CONF_USER_DOMAIN_KEY (L"UserDomain")
#define CONF_DEVICE_NAME_KEY (L"DeviceName")
#define CONF_STORAGEDOMAIN_KEY (L"StorageDomain")
#define CONF_LOGIN_TYPE_KEY (L"LoginType")
#define CONF_LOGIN_BOOTSTARTRUN_KEY (L"BootStartRun")

using namespace std;

class UpdateIniFile
{
public:
	UpdateIniFile(void);
	~UpdateIniFile(void);

public:
	bool UpdateConfigFile(void);
	bool GetVersion(wstring& strOldVersion,wstring& strNewVersion);
private:
	void GetPersistValue(void);

private:
	wstring m_strFilePath;
	wstring m_UpdateFilePath;
	map<wstring,map<wstring,wstring>> PersistValue;
	map<wstring, wstring> NewValue;
};

#endif

