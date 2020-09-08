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
		:algorithm(Fingerprint::Invalid)
		,lowMD5Size(0)
		,middleMD5Size(0){}
};

class Configure
{
private:
	std::wstring version_;

	std::wstring appPath_;
	std::wstring appConfigPath_;
	std::wstring appUserDataPath_;

	std::wstring serverUrl_;
	bool useSSL_;
	ProxyInfo proxyInfo_;
	int32_t requestTimeout_;
	int32_t requestLowSpeedLimit_;
	int32_t requestConnectTimeout_;
	std::wstring capath_;
	SpeedLimitConf localSpeedLimitConf_;
	SpeedLimitConf remoteSpeedLimitConf_;
	SpeedLimitConf speedLimitConf_;

	AsyncTransTaskPoolConf asyncTransTaskPoolConf_;	
	AlgorithmConf algorithmConf_;
	int64_t multiThreadsSpeedLimit_;

	FilterStr filterStr_;

	std::wstring configureRootPath_;
	std::wstring userDataPath_;

	std::wstring userName_;
	std::wstring password_;
	std::wstring userDomain_;
	std::wstring accountGuid_;
	std::wstring deviceName_;
	std::wstring storageDomain_;
	int32_t loginType_;

	int64_t bigFileSize_;
	int64_t maxPartSize_;
	int64_t minPartSize_;
	int32_t maxPartCount_;
	std::wstring retryTaskErrorCodes_;
	int32_t retryTaskInterval_;
	int32_t retryTaskTimes_;

	int32_t	uploadFilterPeriod_;
	int32_t delayLoading_;
	int32_t disableAttr_;

public:
	Configure()
		:version_(L"")
		,appPath_(L"")
		,appConfigPath_(L"")
		,appUserDataPath_(L"")
		,serverUrl_(L"")
		,useSSL_(true)
		,requestTimeout_(0)
		,requestLowSpeedLimit_(0)
		,requestConnectTimeout_(0)
		,loginType_(0)
		,configureRootPath_(L"")
		,userDataPath_(L"")
		,userName_(L"")
		,password_(L"")
		,userDomain_(L"")
		,accountGuid_(L"")
		,deviceName_(L"")
		,storageDomain_(L"")
		,capath_(L"")
		,multiThreadsSpeedLimit_(0)
		,bigFileSize_(0)
		,maxPartSize_(0)
		,minPartSize_(0)
		,maxPartCount_(0)
		,retryTaskErrorCodes_(L"")
		,retryTaskInterval_(0)
		,retryTaskTimes_(0)
		,uploadFilterPeriod_(0)
		,delayLoading_(0)
		,disableAttr_(0)
	{

	}

	virtual ~Configure(){}

	FUNC_DEFAULT_SET_GET(std::wstring, appPath);
	FUNC_DEFAULT_SET_GET(std::wstring, appConfigPath);
	FUNC_DEFAULT_SET_GET(std::wstring, appUserDataPath);
	FUNC_DEFAULT_SET_GET(std::wstring, serverUrl);
	FUNC_DEFAULT_SET_GET(bool, useSSL);
	FUNC_DEFAULT_SET_GET(ProxyInfo, proxyInfo);
	FUNC_DEFAULT_SET_GET(AsyncTransTaskPoolConf, asyncTransTaskPoolConf);
	FUNC_DEFAULT_SET_GET(std::wstring, version);
	FUNC_DEFAULT_SET_GET(int32_t, loginType);
	FUNC_DEFAULT_SET_GET(int32_t, requestTimeout);
	FUNC_DEFAULT_SET_GET(int32_t, requestLowSpeedLimit);
	FUNC_DEFAULT_SET_GET(int32_t, requestConnectTimeout);
	FUNC_DEFAULT_SET_GET(SpeedLimitConf, localSpeedLimitConf);
	FUNC_DEFAULT_SET_GET(SpeedLimitConf, remoteSpeedLimitConf);
	FUNC_DEFAULT_SET_GET(SpeedLimitConf, speedLimitConf);
	FUNC_DEFAULT_SET_GET(AlgorithmConf, algorithmConf);
	FUNC_DEFAULT_SET_GET(int64_t, multiThreadsSpeedLimit);
	FUNC_DEFAULT_SET_GET(FilterStr, filterStr);
	FUNC_DEFAULT_SET_GET(std::wstring, configureRootPath);
	FUNC_DEFAULT_SET_GET(std::wstring, userDataPath);
	FUNC_DEFAULT_SET_GET(std::wstring, userName);
	FUNC_DEFAULT_SET_GET(std::wstring, password);
	FUNC_DEFAULT_SET_GET(std::wstring, userDomain);
	FUNC_DEFAULT_SET_GET(std::wstring, accountGuid);
	FUNC_DEFAULT_SET_GET(std::wstring, deviceName);
	FUNC_DEFAULT_SET_GET(std::wstring, storageDomain);
	FUNC_DEFAULT_SET_GET(std::wstring, capath);
	FUNC_DEFAULT_SET_GET(int64_t, bigFileSize);
	FUNC_DEFAULT_SET_GET(int64_t, maxPartSize);
	FUNC_DEFAULT_SET_GET(int64_t, minPartSize);
	FUNC_DEFAULT_SET_GET(int32_t, maxPartCount);
	FUNC_DEFAULT_SET_GET(std::wstring, retryTaskErrorCodes);
	FUNC_DEFAULT_SET_GET(int32_t, retryTaskInterval);
	FUNC_DEFAULT_SET_GET(int32_t, retryTaskTimes);
	FUNC_DEFAULT_SET_GET(int32_t, uploadFilterPeriod);
	FUNC_DEFAULT_SET_GET(int32_t, delayLoading);
	FUNC_DEFAULT_SET_GET(int32_t, disableAttr);
};

#endif
