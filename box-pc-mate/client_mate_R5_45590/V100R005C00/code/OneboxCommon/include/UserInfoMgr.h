#ifndef _ONEBOX_USERINFO_MGR_H_
#define _ONEBOX_USERINFO_MGR_H_

#include "UserContext.h"
struct StorageUserInfo;
struct DeclarationInfo;
struct LastAccessTerminal;

class ONEBOX_DLL_EXPORT UserInfoMgr
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

	virtual void setUserId(const int64_t userId) = 0;

	virtual void setUserName(const std::wstring& userName) = 0;

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo) = 0;

	virtual LastAccessTerminal getLastAccessTerminal() = 0;

	virtual DeclarationInfo getDeclarationInfo() = 0;

	virtual std::wstring getHostName() = 0;

	virtual int32_t getNetworkType() = 0;

};

#endif