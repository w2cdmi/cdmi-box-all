#ifndef _ONEBOX_CONFIGGURE_H_
#define _ONEBOX_CONFIGGURE_H_

#include "CommonDefine.h"

enum LoginType
{
	LoginTypeManual,
	LoginTypeDomain,
	LoginTypeAuto
};

struct SpeedLimitConf 
{
	bool useSpeedLimit;
	int64_t maxUploadSpeed;
	int64_t maxDownloadSpeed;

	SpeedLimitConf()
		:useSpeedLimit(true)
		,maxUploadSpeed(0L)
		,maxDownloadSpeed(0L){}
};

struct ProxyInfo
{
	bool useProxy;
	bool useProxyAthen;
	std::wstring proxyServer;
	uint16_t proxyPort;
	std::wstring proxyUserName;
	std::wstring proxyPassword;

	ProxyInfo()
		:useProxy(false)
		,useProxyAthen(false)
		,proxyServer(L"")
		,proxyPort(8080)
		,proxyUserName(L"")
		,proxyPassword(L""){}
};

struct AsyncTransTaskPoolConf
{
	int32_t maxThreads;
	int32_t maxTasks;

	AsyncTransTaskPoolConf()
		:maxThreads(0)
		,maxTasks(0){}
};

struct FilterStr 
{
	std::wstring nameFilter;
	std::wstring nameStartFilter;
	std::wstring nameEndFilter;
	std::wstring nameExtFilter;

	FilterStr()
		:nameFilter(L"")
		,nameStartFilter(L"")
		,nameEndFilter(L"")
		,nameExtFilter(L""){}
};

struct AlgorithmConf
{
	int32_t algorithm;
	int32_t lowMD5Size;
	int32_t middleMD5Size;

	AlgorithmConf()
		:algorithm(FileSignature::Invalid)
		,lowMD5Size(0)
		,middleMD5Size(0){}
};

class Configure
{
private:
	std::wstring configureRootPath_;
	
	std::wstring version_;

	std::wstring monitorRootPath_;
	std::wstring userDataPath_;
	std::wstring cachePath_;
	std::wstring cacheTrashPath_;
	std::wstring cacheDataPath_;
	std::wstring virtualFolderName_;
	int32_t syncModel_;

	std::wstring serverUrl_;
	int32_t requestTimeout_;
	bool useSSL_;
	SpeedLimitConf speedLimitConf_;
	ProxyInfo proxyInfo_;

	std::wstring userName_;
	std::wstring password_;
	std::wstring userDomain_;
	std::wstring accountGuid_;
	std::wstring deviceName_;
	std::wstring storageDomain_;
	int32_t loginType_;

	AsyncTransTaskPoolConf asyncTransTaskPoolConf_;
	AlgorithmConf algorithmConf_;
	int32_t remoteDetectorPeriod_;
	int32_t localDetectorPeriod_;
	FilterStr filterStr_;
	int32_t uploadFilterPeriod_;
	std::wstring uploadFilterStr_;

public:
	Configure()
		:configureRootPath_(L"")
		,version_(L"")
		,monitorRootPath_(L"")
		,userDataPath_(L"")
		,cachePath_(L"")
		,cacheTrashPath_(L"")
		,cacheDataPath_(L"")
		,virtualFolderName_(L"")
		,serverUrl_(L"")
		,userName_(L"")
		,password_(L"")
		,userDomain_(L"")
		,accountGuid_(L"")
		,deviceName_(L"")
		,storageDomain_(L"")
		,loginType_(0)
		,requestTimeout_(0)
		,useSSL_(true)
		,remoteDetectorPeriod_(0)
		,localDetectorPeriod_(0)
		,uploadFilterPeriod_(0)
		,uploadFilterStr_(L""){}

	virtual ~Configure(){}

	FUNC_DEFAULT_SET_GET(std::wstring, configureRootPath);
	FUNC_DEFAULT_SET_GET(std::wstring, version);
	FUNC_DEFAULT_SET_GET(std::wstring, monitorRootPath);
	FUNC_DEFAULT_SET_GET(int32_t, syncModel);
	FUNC_DEFAULT_SET_GET(std::wstring, userDataPath);
	FUNC_DEFAULT_SET_GET(std::wstring, cachePath);
	FUNC_DEFAULT_SET_GET(std::wstring, cacheTrashPath);
	FUNC_DEFAULT_SET_GET(std::wstring, cacheDataPath);
	FUNC_DEFAULT_SET_GET(std::wstring, virtualFolderName);
	FUNC_DEFAULT_SET_GET(std::wstring, serverUrl);
	FUNC_DEFAULT_SET_GET(std::wstring, userName);
	FUNC_DEFAULT_SET_GET(std::wstring, password);
	FUNC_DEFAULT_SET_GET(std::wstring, userDomain);
	FUNC_DEFAULT_SET_GET(std::wstring, accountGuid);
	FUNC_DEFAULT_SET_GET(std::wstring, deviceName);
	FUNC_DEFAULT_SET_GET(std::wstring, storageDomain);
	FUNC_DEFAULT_SET_GET(int32_t, loginType);
	FUNC_DEFAULT_SET_GET(int32_t, requestTimeout);
	FUNC_DEFAULT_SET_GET(bool, useSSL);
	FUNC_DEFAULT_SET_GET(SpeedLimitConf, speedLimitConf);
	FUNC_DEFAULT_SET_GET(ProxyInfo, proxyInfo);
	FUNC_DEFAULT_SET_GET(AsyncTransTaskPoolConf, asyncTransTaskPoolConf);
	FUNC_DEFAULT_SET_GET(AlgorithmConf, algorithmConf);
	FUNC_DEFAULT_SET_GET(int32_t, remoteDetectorPeriod);
	FUNC_DEFAULT_SET_GET(int32_t, localDetectorPeriod);
	FUNC_DEFAULT_SET_GET(FilterStr, filterStr);
	FUNC_DEFAULT_SET_GET(int32_t, uploadFilterPeriod);
	FUNC_DEFAULT_SET_GET(std::wstring, uploadFilterStr);
};

#endif
