#ifndef _ONEBOX_USERINFO_MGR_H_
#define _ONEBOX_USERINFO_MGR_H_

#include "UserContext.h"
#include "UserInfo.h"

class UserInfoMgr
{
public:
	virtual ~UserInfoMgr(){}

	static UserInfoMgr* create(UserContext* userContext);

	virtual int32_t authen(const std::wstring& account, const std::wstring& password) = 0;

	virtual int32_t domainAuthen() = 0;

	virtual int32_t autoAuthen() = 0;

	virtual int32_t logout() = 0;

	virtual std::wstring getAccount() = 0;

	virtual int64_t getUserId() = 0;

	virtual std::wstring getUserName() = 0;

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo) = 0;

	virtual int32_t listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray) = 0;
};

#endif