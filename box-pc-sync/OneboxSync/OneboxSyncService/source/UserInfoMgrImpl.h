#include "UserInfoMgr.h"
#include "NetworkMgr.h"
#include "ConfigureMgr.h"
#include "CredentialMgr.h"
#include "Utility.h"
#include <boost/thread/mutex.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("UserInfoMgr")
#endif

using namespace SD;

class UserInfoMgrImpl : public UserInfoMgr
{
public:
	UserInfoMgrImpl(UserContext* userContext);

	virtual ~UserInfoMgrImpl();

	virtual int32_t authen(const std::wstring& userName, const std::wstring& password);

	virtual int32_t domainAuthen();

	virtual int32_t autoAuthen();

	virtual int32_t logout();

	virtual std::wstring getAccount();

	virtual int64_t getUserId();

	virtual std::wstring getUserName();

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo);

	virtual int32_t listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray);

private:
	int32_t parseAuthenRsp(LoginRespInfo& authenRsp);

	int32_t getDeviceInfo();

private:
	UserContext* userContext_;
	std::wstring account_;
	int64_t userId_;
	std::wstring userName_;
	std::wstring deviceId_;
	std::wstring osVersion_;
	std::wstring hostName_;
	boost::mutex mutex_;
};
