/******************************************************************************
Description  : ”√ªß Ù–‘
*******************************************************************************/
#ifndef _USERINFO_H_
#define _USERINFO_H_

#include "CommonValue.h"

#define DEFAULT_CLIENT_TYPE "pc"
#define DEFAULT_CLIENT_SN "640F6EA7E50B4834A37D07C8555E2F0E"
#define DEFAULT_CLIENT_OS "Win 7"
#define DEFAULT_CLIENT_NAME "Default Client Name"
#define DEFAULT_CLIENT_VERSION "1.0.0.0"

enum NetworkType
{
	NT_UGREEN = 1,
	NT_UYELLOW,
	NT_UBLUE,
	NT_SGREEN,
	NT_SYELLOW,
	NT_BLUE,
	NT_INTERNET,
	NT_IACCESS,
	NT_RDVM,
	NT_NOTRDVM,
	NT_HUAWEIEMPLOYEE,
	NT_WHITELISTNETWORK = 88,
	NT_BLACKLISTNETWORK = 99,
	NT_OTHER
};

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

struct LastAccessTerminal
{
	std::string deviceType;
	std::string deviceName;
	std::string deviceOS;
	std::string deviceAgent;
	std::string lastAccessIP;
	int64_t lastAccessAt;

	LastAccessTerminal()
		:deviceType("")
		,deviceName("")
		,deviceOS("")
		,deviceAgent("")
		,lastAccessIP("")
		,lastAccessAt(0)
	{
	}
	LastAccessTerminal(const LastAccessTerminal& lastAccessTerminal)
	{
		deviceType = lastAccessTerminal.deviceType;
		deviceName = lastAccessTerminal.deviceName;
		deviceOS = lastAccessTerminal.deviceOS;
		deviceAgent = lastAccessTerminal.deviceAgent;
		lastAccessIP = lastAccessTerminal.lastAccessIP;
		lastAccessAt = lastAccessTerminal.lastAccessAt;
	}
};

struct  DeclarationInfo
{
	bool needsigndeclare;
	std::string declarationID;
	std::string appId;
	std::string clientType;
	int64_t createAt;
	std::string declarationText;

	DeclarationInfo()
		:needsigndeclare(false)
		,declarationID("")
		,appId("")
		,clientType("")
		,createAt(0)
		,declarationText("")
	{

	}
	DeclarationInfo(const DeclarationInfo& declarationInfo)
	{
		needsigndeclare = declarationInfo.needsigndeclare;
		declarationID = declarationInfo.declarationID;
		appId = declarationInfo.appId;
		clientType = declarationInfo.clientType;
		createAt = declarationInfo.createAt;
		declarationText = declarationInfo.declarationText;
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
	bool needChangePassword;
	DeclarationInfo declarationInfo;
	LastAccessTerminal lastAccessTerminal;
	int32_t networkType;

	LoginRespInfo()
		:user_id(0L)
		,login_name("")
		,cloud_userid(0)
		,token("")
		,refreshToken("")
		,expiredAt(0)
		,maxUploadSpeed(0L)
		,maxDownloadSpeed(0L)
		,needChangePassword(false)
		,networkType(-1)
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

struct SysRoleInfo
{
	std::string name;
	std::string description;
	int8_t status;
	int8_t browse;
	int8_t preview;
	int8_t download;
	int8_t upload;
	int8_t edit;
	int8_t permissionDelete;
	int8_t publishLink;
	int8_t authorize;

	SysRoleInfo()
		:name("")
		,description("")
		,status(0)
		,browse(0)
		,preview(0)
		,download(0)
		,upload(0)
		,edit(0)
		,permissionDelete(0)
		,publishLink(0)
		,authorize(0)
	{
	}
};

struct SysRoleInfoEx
{
	std::string name;
	std::string description;
	int8_t status;

	SysRoleInfoEx()
		:name("")
		,description("")
		,status(0)
	{
	}
};

typedef std::vector<SysRoleInfoEx> SysRoleInfoExList;

#endif
