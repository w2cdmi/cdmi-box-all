#ifndef __ONEBOX__USERINFO_H_
#define __ONEBOX__USERINFO_H_

#include "CommonValue.h"
#include <vector>

#define DEFAULT_CLIENT_TYPE "pc"
#define DEFAULT_CLIENT_SN "640F6EA7E50B4834A37D07C8555E2F0E"
#define DEFAULT_CLIENT_OS "Win7 64bit"
#define DEFAULT_CLIENT_NAME "Default Client Name"
#define DEFAULT_CLIENT_VERSION "1.0.0.0"

struct LoginReqInfo
{
	int64_t app_id;
    std::string loginName;
	std::string password;
	std::string domain;
	LoginReqInfo():app_id(0L), loginName(""), password(""), domain("")
	{
	}
};

struct LoginRespInfo
{
	int64_t user_id;
	std::string login_name;
	int64_t cloud_userid;
	//std::string token_type;
	std::string token;
	std::string refreshToken;
	int32_t expiredAt;
	int64_t maxUploadSpeed;
	int64_t maxDownloadSpeed;

	LoginRespInfo()
		:user_id(0L)
		,login_name("")
		,cloud_userid(0)
		,token("")
		,refreshToken("")
		,expiredAt(0)
		,maxUploadSpeed(0L)
		,maxDownloadSpeed(0L)
	{
	}

	bool isValid()
	{
		return (0 < cloud_userid 
			&& !token.empty() 
			&& !refreshToken.empty() 
			&& 0 < expiredAt 
			&& !login_name.empty());
	}
};

struct StorageUserInfo
{
	int64_t user_id;
	std::string login_name;
	std::string name;
	std::string email;
	std::string description;
	int64_t spaceQuota;
	int64_t spaceUsed;
	int8_t status;
	int32_t regionId;
	int64_t fileCount;
	int32_t maxVersions;

	StorageUserInfo()
		:user_id(0L)
		,login_name("")
		,name("")
		,email("")
		,description("")
		,spaceQuota(-1)
		,spaceUsed(0L)
		,status(0)
		,regionId(-1)
		,fileCount(0L)
		,maxVersions(-1)
	{
	}
};

struct RegionIdInfo
{
	int64_t id;
	std::string name;
	std::string description;
	RegionIdInfo():id(0L), name(""), description("")
	{
	}
};

typedef std::vector<RegionIdInfo> RegionIdInfoArray;

#endif
