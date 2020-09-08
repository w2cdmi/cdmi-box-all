#ifndef _ONEBOX_CACHE_USERINFO_H_
#define _ONEBOX_CACHE_USERINFO_H_

#include "ShareResMgr.h"
#include "UserInfo.h"

#define SQLITE_CACHE_USERINFO (L"cacheUserInfo.db")
#define TABLE_USER ("tb_user")
#define USER_ROW_ID ("id")
#define USER_ROW_NAME ("name")
#define USER_ROW_TYPE ("type")
#define USER_ROW_LOGINNAME ("login_name")
#define USER_ROW_EMAIL ("email")
#define USER_ROW_DEPARTMENT ("department")

class CacheUserInfo
{
public:
	virtual ~CacheUserInfo(){}

	static CacheUserInfo* create(UserContext* userContext, const std::wstring& parent);

	//virtual std::wstring addUser(int64_t id) = 0;

	virtual int32_t addUser(const ShareUserInfo& userInfo) = 0;

	virtual int32_t addUser(const ShareUserInfoList& userInfoList) = 0;
	
	virtual int32_t getUser(const std::string& keyword, ShareUserInfoList& userInfoList, int32_t limit) = 0;

	virtual std::string getUserName(int64_t teamId, int64_t id) = 0;

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo) = 0;
};

#endif