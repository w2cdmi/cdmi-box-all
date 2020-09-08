#include "JsonParser.h"
#include "Util.h"
#include "Utility.h"

using namespace SD;

#define PARSEJSON_STRING_LEAF(KEY)				\
	std::string KEY = "";							\
	if(jsonObj[#KEY].isString())				\
	{											\
		KEY = jsonObj[#KEY].asString();			\
	}											

#define PARSEJSON_INT_LEAF(KEY)					\
	int32_t KEY = 0;								\
	if(jsonObj[#KEY].isInt())					\
	{											\
		KEY = jsonObj[#KEY].asInt();			\
	}		

#define PARSEJSON_INT64_LEAF(KEY)				\
	int64_t KEY = 0;					\
	if(jsonObj[#KEY].isUInt64())				\
	{											\
		KEY = (int64_t)jsonObj[#KEY].asUInt64();	\
	}		

#define PARSEJSON_UINT64_LEAF(KEY)				\
	int64_t KEY = 0;							\
	if(jsonObj[#KEY].isInt64())					\
	{											\
		KEY = (uint64_t)jsonObj[#KEY].asInt64();\
	}		

#define PARSEJSON_BOOL_LEAF(KEY)				\
	bool KEY = false;							\
	if(jsonObj[#KEY].isBool())					\
	{											\
		KEY = jsonObj[#KEY].asBool();			\
	}											

#define PARSE_STRING_LEAF(KEY)					\
	std::string KEY;									\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
	{											\
		return ret;							\
	}

#define PARSE_INT_LEAF(KEY)						\
	int32_t KEY;									\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
	{											\
		return ret;							\
	}

#define PARSE_INT64_LEAF(KEY)					\
	int64_t KEY;								\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
	{											\
		return ret;							\
	}

#define PARSE_UINT64_LEAF(KEY)					\
	uint64_t KEY;						\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
	{											\
		return ret;							\
	}

#define PARSE_BOOL_LEAF(KEY)					\
	bool KEY;									\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
	{											\
		return ret;							\
	}

#define PARSE_JSON_LEAF(KEY)					\
	Json::Value KEY;									\
	ret = parser.parseLeaf(#KEY, KEY);			\
	if(RT_OK != ret)							\
{											\
	return ret;							\
}

int32_t JsonParser::parseListFolderResult(DataBuffer &dataBuf,
										std::list<FileItem*>& fileItems,
										int64_t& total_cnt)
{
	Parser<FileItem> parser(dataBuf);
	int32_t ret;

	PARSE_INT_LEAF(totalCount);
	total_cnt = (int32_t)(totalCount);

	ret = parser.parseList("folders", &getFolderUnitFromJson, fileItems);
	if(RT_OK != ret)
	{
		return ret;
	}

	ret = parser.parseList("files", &getFolderUnitFromJson, fileItems);
	return ret;
}

int32_t JsonParser::parseFileFolders(DataBuffer &dataBuf, std::list<FileItem*>& fileItems)
{
	Parser<FileItem> parser(dataBuf);
	int32_t ret = parser.parseList("folders", &getFolderUnitFromJson, fileItems);
	if(RT_OK != ret)
	{
		return ret;
	}
	return parser.parseList("files", &getFolderUnitFromJson, fileItems);
}

int32_t JsonParser::getFolderUnitFromJson(const Json::Value& jsonObj, FileItem &objectUnit)
{
	int32_t ret = RT_OK;
	if(jsonObj.isNull())
	{
		return RT_INVALID_PARAM;
	}

	PARSEJSON_INT64_LEAF(id);
	objectUnit.id(id);

	PARSEJSON_INT_LEAF(type);
	objectUnit.type((FILE_TYPE)type);

	PARSEJSON_STRING_LEAF(name);
	objectUnit.name(name);

	PARSEJSON_INT64_LEAF(parent);
	objectUnit.parent(parent);

	PARSEJSON_INT64_LEAF(ownedBy);
	objectUnit.ownerId(ownedBy);

	PARSEJSON_INT64_LEAF(size);
	objectUnit.size(size);

	PARSEJSON_INT64_LEAF(createdAt);
	objectUnit.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSEJSON_INT64_LEAF(modifiedAt);
	objectUnit.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSEJSON_STRING_LEAF(objectId);
	objectUnit.version(objectId);

	PARSEJSON_BOOL_LEAF(isShare);
	objectUnit.isShare(isShare);

	PARSEJSON_BOOL_LEAF(isSync);
	objectUnit.isSync(isSync);

	PARSEJSON_BOOL_LEAF(isEncrypt);
	objectUnit.isEncrypt(isEncrypt);

	PARSEJSON_BOOL_LEAF(isSharelink);
	objectUnit.isSharelink(isSharelink);

	FileSignature signature;
	if(!jsonObj["sha1"].isNull())
	{
		signature.algorithm = FileSignature::SHA1;
		signature.signature = jsonObj["sha1"].asString();
	}
	else if (!jsonObj["md5"].isNull())
	{
		signature.algorithm = FileSignature::MD5;
		signature.signature = jsonObj["md5"].asString();
	}
	objectUnit.signature(signature);

	return ret;
}

int32_t JsonParser::parseLoginRespInfo(DataBuffer &dataBuf, LoginRespInfo& loginResp)
{
	Parser<LoginRespInfo> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(userId);
	loginResp.user_id = userId;

	PARSE_STRING_LEAF(loginName);
	loginResp.login_name = loginName;

	PARSE_UINT64_LEAF(cloudUserId);
	loginResp.cloud_userid = cloudUserId;

	PARSE_STRING_LEAF(token);
	loginResp.token = token;

	PARSE_STRING_LEAF(refreshToken);
	loginResp.refreshToken = refreshToken;

	PARSE_INT_LEAF(timeout);
	loginResp.expiredAt = timeout;

	PARSE_INT64_LEAF(uploadQos);
	loginResp.maxUploadSpeed = uploadQos;

	PARSE_INT64_LEAF(downloadQos);
	loginResp.maxDownloadSpeed = downloadQos;

	return ret;
}

int32_t JsonParser::parseLoginRespInfoEx(DataBuffer &dataBuf, LoginRespInfo& loginResp)
{
	Parser<LoginRespInfo> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(cloudUserId);
	loginResp.user_id = cloudUserId;

	PARSE_STRING_LEAF(username);
	loginResp.login_name = username;

	PARSE_UINT64_LEAF(userId);
	loginResp.cloud_userid = userId;

	PARSE_STRING_LEAF(token);
	loginResp.token = token;

	PARSE_STRING_LEAF(refreshToken);
	loginResp.refreshToken = refreshToken;

	PARSE_INT_LEAF(toExpiredAt);
	loginResp.expiredAt = toExpiredAt;

	PARSE_INT64_LEAF(uploadQos);
	loginResp.maxUploadSpeed = uploadQos;

	PARSE_INT64_LEAF(downloadQos);
	loginResp.maxDownloadSpeed = downloadQos;

	return ret;
}

int32_t JsonParser::parseFileObj(DataBuffer &dataBuf, FileItem& fileItem)
{
	Parser<FileItem> parser(dataBuf);

	std::string tempStr;
	int32_t ret;

	PARSE_INT64_LEAF(id);
	fileItem.id(id);

	PARSE_INT_LEAF(type);
	fileItem.type((FILE_TYPE)type);

	PARSE_STRING_LEAF(name);
	fileItem.name(name);

	PARSE_STRING_LEAF(description);
	fileItem.description(description);

	PARSE_INT64_LEAF(parent);
	fileItem.parent(parent);
	
	PARSE_INT64_LEAF(ownedBy);
	fileItem.ownerId(ownedBy);

	PARSE_INT64_LEAF(createdBy);
	fileItem.createdBy(createdBy);

	PARSE_INT64_LEAF(modifiedBy);
	fileItem.modifiedBy(modifiedBy);

	PARSE_UINT64_LEAF(size);
	fileItem.size(size);

	PARSE_INT_LEAF(status);
	fileItem.status(status);

	PARSE_INT64_LEAF(createdAt);
	fileItem.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedAt);
	fileItem.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(contentCreatedAt);
	if (0 != contentCreatedAt)
	{
		fileItem.contentCreatedAt(Utility::DateTime(contentCreatedAt, Utility::UtcType::Unix).getWindowsFileTime());
	}	

	PARSE_INT64_LEAF(contentModifiedAt);
	if (0 != contentModifiedAt)
	{
		fileItem.contentModifiedAt(Utility::DateTime(contentModifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
	}

	PARSE_STRING_LEAF(objectId);
	fileItem.version(objectId);

	PARSE_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSE_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSE_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSE_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	FileSignature signature;
	if (RT_OK == parser.parseLeafSafe("sha1", signature.signature))
	{
		signature.algorithm = FileSignature::SHA1;
	}
	else if (RT_OK == parser.parseLeafSafe("md5", signature.signature))
	{
		signature.algorithm = FileSignature::MD5;
	}
	fileItem.signature(signature);

	return ret;
}

int32_t JsonParser::parseExistFileObj(DataBuffer &dataBuf, FileItem& fileItem)
{
	std::string jsonStr;
	Json::Reader reader;
	Json::Value jsonObj = Json::nullValue;

	if ((NULL != dataBuf.pBuf) && (0 < dataBuf.lOffset))
	{
		jsonStr.assign((char *)dataBuf.pBuf);
		reader.parse(jsonStr, jsonObj);
	}

	//Json::Value jsonObj = jsonRoot["file"];

	int32_t ret = RT_OK;	

	PARSEJSON_INT64_LEAF(id);
	fileItem.id(id);

	PARSEJSON_INT_LEAF(type);
	fileItem.type((FILE_TYPE)type);

	PARSEJSON_STRING_LEAF(name);
	fileItem.name(name);

	PARSEJSON_INT64_LEAF(parent);
	fileItem.parent(parent);

	PARSEJSON_INT64_LEAF(ownedBy);
	fileItem.ownerId(ownedBy);

	PARSEJSON_INT64_LEAF(size);
	fileItem.size(size);

	PARSEJSON_INT64_LEAF(createdAt);
	fileItem.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSEJSON_INT64_LEAF(modifiedAt);
	fileItem.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
	
	PARSEJSON_INT64_LEAF(contentCreatedAt);
	fileItem.contentCreatedAt(Utility::DateTime(contentCreatedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSEJSON_INT64_LEAF(contentModifiedAt);
	fileItem.contentModifiedAt(Utility::DateTime(contentModifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSEJSON_STRING_LEAF(objectId);
	fileItem.version(objectId);

	PARSEJSON_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSEJSON_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSEJSON_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSEJSON_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	FileSignature signature;
	if (!jsonObj["sha1"].isNull())
	{
		signature.algorithm = FileSignature::SHA1;
		signature.signature = jsonObj["sha1"].asString();
	}
	else if (!jsonObj["md5"].isNull())
	{
		signature.algorithm = FileSignature::MD5;
		signature.signature = jsonObj["md5"].asString();
	}
	fileItem.signature(signature);

	return ret;
}

int32_t JsonParser::parseFolderObj(DataBuffer &dataBuf, FileItem& fileItem)
{
	Parser<FileItem> parser(dataBuf);

	std::string tempStr;
	int32_t ret;

	PARSE_INT64_LEAF(id);
	fileItem.id(id);

	PARSE_INT_LEAF(type);
	fileItem.type((FILE_TYPE)type);

	PARSE_STRING_LEAF(name);
	fileItem.name(name);

	PARSE_STRING_LEAF(description);
	fileItem.description(description);

	PARSE_INT64_LEAF(size);
	fileItem.size(size);

	PARSE_BOOL_LEAF(status);
	fileItem.status(status);

	PARSE_INT64_LEAF(parent);
	fileItem.parent(parent);

	PARSE_INT64_LEAF(ownedBy);
	fileItem.ownerId(ownedBy);

	PARSE_INT64_LEAF(createdBy);
	fileItem.createdBy(Utility::DateTime(createdBy, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(createdAt);
	fileItem.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedAt);
	fileItem.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(contentCreatedAt);
	fileItem.contentCreatedAt(Utility::DateTime(contentCreatedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(contentModifiedAt);
	fileItem.contentModifiedAt(Utility::DateTime(contentModifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSE_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSE_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSE_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	return ret;
}

int32_t JsonParser::parseUploadInfo(DataBuffer &dataBuf, UploadInfo& uploadInfo)
{
	Parser<UploadInfo> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(fileId);
	uploadInfo.file_id = fileId;

	//PARSE_STRING_LEAF(upload_ip);
	//uploadInfo.upload_id = upload_ip;

	PARSE_STRING_LEAF(uploadUrl);
	uploadInfo.upload_url = uploadUrl;

	//PARSE_STRING_LEAF(upload_id);
	//uploadInfo.upload_id = upload_id;

	return ret;
}

int32_t JsonParser::parseDownloadInfo(DataBuffer &dataBuf, std::string& downloadUrl)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["downloadUrl"].isString())				
	{											
		downloadUrl = jsonObj["downloadUrl"].asString();			
	}		

	return RT_OK;
}

int32_t JsonParser::parsePartInfo(DataBuffer &dataBuf, PartInfoList& partInfoList)
{
	Parser<PartInfo> parser(dataBuf);
	Json::Value jsonList;
	parser.parseList("parts", jsonList);
	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PartInfo partInfo;
		PARSEJSON_INT_LEAF(partId);
		partInfo.partId = partId;
		PARSEJSON_INT_LEAF(size);
		partInfo.size = size;

		partInfoList.push_back(partInfo);
	}
	return RT_OK;
}

int32_t JsonParser::parseShareNodeList(DataBuffer &dataBuf, ShareNodeList& shareNodes, int32_t& total_cnt)
{
	int ret = RT_OK;

	Parser<ShareNode> parser(dataBuf);
	Json::Value jsonList;
	
	PARSE_INT_LEAF(totalCount);
	total_cnt = (int32_t)totalCount;

	parser.parseList("contents", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		ShareNode shareNode;

		PARSEJSON_INT64_LEAF(id);
		shareNode.id(id);
		PARSEJSON_INT_LEAF(type);
		shareNode.type(type);
		PARSEJSON_INT64_LEAF(ownerId);
		shareNode.ownerId(ownerId);
		PARSEJSON_STRING_LEAF(ownerName);
		shareNode.ownerName(ownerName);
		PARSEJSON_INT64_LEAF(sharedUserId);
		shareNode.sharedUserId(sharedUserId);
		PARSEJSON_INT_LEAF(sharedUserType);
		shareNode.sharedUserType(sharedUserType);
		PARSEJSON_STRING_LEAF(sharedUserName);
		shareNode.sharedUserName(sharedUserName);
		PARSEJSON_STRING_LEAF(sharedUserLoginName);
		shareNode.sharedUserLoginName(sharedUserLoginName);
		PARSEJSON_STRING_LEAF(sharedUserDescription);
		shareNode.sharedUserDescription(sharedUserDescription);
		PARSEJSON_INT64_LEAF(inodeId);
		shareNode.inodeId(inodeId);
		PARSEJSON_STRING_LEAF(name);
		shareNode.name(name);
		PARSEJSON_INT64_LEAF(modifiedAt);
		shareNode.modifiedAt(modifiedAt);
		PARSEJSON_INT64_LEAF(modifiedBy);
		shareNode.modifiedBy(modifiedBy);
		PARSEJSON_STRING_LEAF(roleName);
		shareNode.roleName(roleName);
		PARSEJSON_INT_LEAF(status);
		shareNode.status(status);
		PARSEJSON_INT64_LEAF(size);
		shareNode.size(size);

		shareNodes.push_back(shareNode);
	}
	return RT_OK;
}

int32_t JsonParser::parseShareUserId(DataBuffer &dataBuf, int64_t& userId)
{
	int ret = RT_OK;

	Parser<ShareUserInfo> parser(dataBuf);
	PARSE_INT64_LEAF(cloudUserId);
	userId = cloudUserId;

	return ret;
}

int32_t JsonParser::parseShareUserInfoList(DataBuffer &dataBuf, ShareUserInfoList& shareUserInfos)
{
	int ret = RT_OK;

	Parser<ShareUserInfo> parser(dataBuf);
	Json::Value jsonList;
	
	//PARSE_INT_LEAF(totalCount);
	//total_cnt = (int32_t)totalCount;

	parser.parseList("users", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		ShareUserInfo shareUserInfo;

		PARSEJSON_STRING_LEAF(description);
		shareUserInfo.department(description);
		//PARSEJSON_STRING_LEAF(domain);
		//shareUserInfo.domain(domain);
		PARSEJSON_STRING_LEAF(email);
		shareUserInfo.email(email);
		PARSEJSON_INT64_LEAF(cloudUserId);
		shareUserInfo.id(cloudUserId);
		//PARSEJSON_STRING_LEAF(label);
		//shareUserInfo.label(label);
		PARSEJSON_STRING_LEAF(loginName);
		shareUserInfo.loginName(loginName);		
		PARSEJSON_STRING_LEAF(name);
		shareUserInfo.name(name);
		//PARSEJSON_STRING_LEAF(objectSid);
		//shareUserInfo.objectSid(objectSid);
		//PARSEJSON_INT_LEAF(recycleDays);
		//shareUserInfo.recycleDays(recycleDays);
		PARSEJSON_INT_LEAF(regionId);
		shareUserInfo.regionId(regionId);
		PARSEJSON_INT64_LEAF(spaceQuota);
		shareUserInfo.spaceQuota(spaceQuota);
		PARSEJSON_INT64_LEAF(spaceUsed);
		shareUserInfo.spaceUsed(spaceUsed);
		PARSEJSON_STRING_LEAF(status);
		shareUserInfo.status(status);
		//PARSEJSON_INT_LEAF(type);
		//shareUserInfo.type(type);

		shareUserInfos.push_back(shareUserInfo);
	}
	return RT_OK;
}

int32_t JsonParser::parseShareLinkNode(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode)
{
	Parser<ShareLinkNode> parser(dataBuf);
	int32_t ret = RT_OK;

	PARSE_STRING_LEAF(id);
	shareLinkNode.id(id);

	PARSE_INT64_LEAF(iNodeId);
	shareLinkNode.iNodeId(iNodeId);

	PARSE_INT64_LEAF(ownedBy);
	shareLinkNode.ownedBy(ownedBy);

	PARSE_STRING_LEAF(url);
	shareLinkNode.url(url);

	PARSE_STRING_LEAF(plainAccessCode);
	shareLinkNode.plainAccessCode(plainAccessCode);

	PARSE_INT64_LEAF(effectiveAt);
	shareLinkNode.effectiveAt(effectiveAt);

	PARSE_INT64_LEAF(expireAt);
	shareLinkNode.expireAt(expireAt);

	PARSE_INT64_LEAF(createdAt);
	shareLinkNode.createdAt(createdAt);

	PARSE_INT64_LEAF(modifiedAt);
	shareLinkNode.modifiedAt(modifiedAt);

	PARSE_INT64_LEAF(createdBy);
	shareLinkNode.createdBy(createdBy);

	PARSE_INT64_LEAF(modifiedBy);
	shareLinkNode.createdBy(modifiedBy);

	PARSE_INT64_LEAF(creator);
	shareLinkNode.creator(creator);

	return ret;
}


int32_t JsonParser::parseServerSysConfig(DataBuffer &dataBuf, ServerSysConfig& serverSysConfig)
{
	serverSysConfig.loginFailNotify(true);
	serverSysConfig.notRemberMe(false);
	serverSysConfig.complexCode(true);
	serverSysConfig.loginFailNum(5);

	std::string tmp_str;
	Json::Value jsonList(Json::arrayValue);
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_STRING_LEAF(option);
		PARSEJSON_STRING_LEAF(value);

		//[{"option":"linkAccessKeyRule","value":"simple"/"complex"},{"option":"systemMaxVersions","value":"200"}]
		if("linkAccessKeyRule" == option)
		{
			serverSysConfig.complexCode("complex"==value);
		}
	}

	return RT_OK;
}

int32_t JsonParser::parseIncSyncPeriod(DataBuffer &dataBuf, int64_t& incSyncPeriod)
{
	std::string tmp_str;
	Json::Value jsonList(Json::arrayValue);
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_STRING_LEAF(option);
		PARSEJSON_STRING_LEAF(value);

		//[{"option":"linkAccessKeyRule","value":"simple"/"complex"},{"option":"systemMaxVersions","value":"200"}]
		if("incSyncPeriod" == option)
		{
			incSyncPeriod = SD::Utility::String::string_to_type<int64_t>(value);
			break;
		}
	}

	return RT_OK;
}

int32_t JsonParser::parseUpdateInfo(DataBuffer &dataBuf, UpdateInfo& updateInfo)
{
	Parser<UpdateInfo> parser(dataBuf);
	int32_t ret;

	PARSE_STRING_LEAF(versionInfo);
	updateInfo.versionInfo = versionInfo;

	PARSE_STRING_LEAF(downloadUrl);
	updateInfo.downloadUrl = downloadUrl;

	return ret;
}

int32_t JsonParser::parseFileInfoByShareLink(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode, FileItem& fileItem)
{
	Parser<ShareLinkNode> parser(dataBuf);
	int ret;

	std::string tmp_str;
	Json::Reader reader;
	Json::Value jsonsharelink = Json::nullValue;
	Json::Value jsonfileitem = Json::nullValue;

	tmp_str.assign((char *)dataBuf.pBuf, 0, sizeof(ShareLinkNode));
	reader.parse(tmp_str, jsonsharelink);

	PARSE_STRING_LEAF(id);
	shareLinkNode.id(id);

	PARSE_INT64_LEAF(iNodeId);
	shareLinkNode.iNodeId(iNodeId);

	PARSE_INT64_LEAF(ownedBy);
	shareLinkNode.ownedBy(ownedBy);

	PARSE_STRING_LEAF(url);
	shareLinkNode.url(url);

	PARSE_STRING_LEAF(plainAccessCode);
	shareLinkNode.plainAccessCode(plainAccessCode);

	PARSE_INT64_LEAF(effectiveAt);
	shareLinkNode.effectiveAt(effectiveAt);

	PARSE_INT64_LEAF(expireAt);
	shareLinkNode.expireAt(expireAt);

	PARSE_INT64_LEAF(createdAt);
	shareLinkNode.createdAt(createdAt);

	PARSE_INT64_LEAF(modifiedAt);
	shareLinkNode.modifiedAt(modifiedAt);

	PARSE_INT64_LEAF(modifiedBy);
	shareLinkNode.createdBy(modifiedBy);

	PARSE_INT64_LEAF(creator);
	shareLinkNode.creator(creator);


	tmp_str.assign((char *)dataBuf.pBuf, sizeof(ShareLinkNode), sizeof(FileItem));
	reader.parse(tmp_str, jsonfileitem);

#if 0
	PARSE_UINT64_LEAF(id);
	fileItem.objId(TypeToString<unsigned long long>(id));

	PARSE_INT_LEAF(type);
	fileItem.type((CS_FILE_TYPE)type);

	PARSE_STRING_LEAF(name);
	fileItem.name(name);

	PARSE_STRING_LEAF(description);
	fileItem.description(description);

	PARSE_UINT64_LEAF(parent);
	fileItem.parent(TypeToString<unsigned long long>(parent));

	PARSE_UINT64_LEAF(ownedBy);
	fileItem.ownerId(TypeToString<unsigned long long>(ownedBy));

	PARSE_UINT64_LEAF(createdBy);
	fileItem.createdBy(TypeToString<unsigned long long>(createdBy));

	PARSE_UINT64_LEAF(modifiedBy);
	fileItem.modifiedBy(TypeToString<unsigned long long>(modifiedBy));

	PARSE_UINT64_LEAF(size);
	fileItem.size(size);

	PARSE_INT_LEAF(status);
	fileItem.size(status);

	PARSE_UINT64_LEAF(createdAt);
	fileItem.createTime(TypeToString<unsigned long long>(createdAt));

	PARSE_UINT64_LEAF(modifiedAt);
	fileItem.modifieTime(TypeToString<unsigned long long>(modifiedAt));

	PARSE_UINT64_LEAF(contentCreatedAt);
	fileItem.contentCreatedAt(TypeToString<unsigned long long>(contentCreatedAt));

	PARSE_UINT64_LEAF(contentModifiedAt);
	fileItem.contentModifiedAt(TypeToString<unsigned long long>(contentModifiedAt));

	PARSE_STRING_LEAF(version);
	fileItem.version(version);

	PARSE_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSE_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSE_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSE_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	PARSE_STRING_LEAF(sha1);
	fileItem.sha1(sha1);
#endif

	return RT_OK;
}

int32_t JsonParser::parseServerUrl(DataBuffer &dataBuf, std::string& serverurl)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["serverUrl"].isString())				
	{											
		serverurl = jsonObj["serverUrl"].asString() + serverurl;			
	}		

	return RT_OK;
}

int32_t JsonParser::parseTeamspaces(DataBuffer &dataBuf, TeamspaceNodes& teamspaces, int64_t& total)
{
	Parser<TeamspaceNode> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(totalCount);
	total = totalCount;
	if(total <= 0)
	{
		return FAILED_TO_PARSEJSON;
	}

	Json::Value jsonList;
	ret = parser.parseList("teamspaces", jsonList);
	if(RT_OK != ret)
	{
		return ret;
	}

	for(Json::Value::ArrayIndex i = 0; i < jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		TeamspaceNode teamspace;

		PARSEJSON_INT64_LEAF(id);
		teamspace.id(id);
		PARSEJSON_STRING_LEAF(name);
		teamspace.name(name);
		PARSEJSON_STRING_LEAF(description);
		teamspace.description(description);
		PARSEJSON_INT_LEAF(status);
		teamspace.status((TeamspaceStatus)status);
		PARSEJSON_INT_LEAF(curNumbers);
		teamspace.curNumbers(curNumbers);
		PARSEJSON_INT64_LEAF(createdBy);
		teamspace.createdBy(createdBy);
		PARSEJSON_STRING_LEAF(createdByUserName);
		teamspace.createdByUserName(createdByUserName);
		PARSEJSON_INT64_LEAF(ownedBy);
		teamspace.ownedBy(ownedBy);
		PARSEJSON_STRING_LEAF(ownedByUserName);
		teamspace.ownedByUserName(ownedByUserName);
		PARSEJSON_INT64_LEAF(createdAt);
		teamspace.createdAt(createdAt);
		PARSEJSON_INT64_LEAF(spaceQuota);
		teamspace.spaceQuota(spaceQuota);
		PARSEJSON_INT64_LEAF(spaceUsed);
		teamspace.spaceUsed(spaceUsed);
		PARSEJSON_INT_LEAF(maxVersions);
		teamspace.maxVersions(maxVersions);
		PARSEJSON_INT_LEAF(maxMembers);
		teamspace.maxMembers(maxMembers);

		teamspaces.push_back(teamspace);
	}
	return RT_OK;
}

int32_t JsonParser::parseTeamspaceMemberships(DataBuffer &dataBuf, TeamspaceMemberships& teamspaceMemberships, int64_t& total)
{
	Parser<TeamspaceMembership> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(totalCount);
	total = totalCount;
	if(total <= 0)
	{
		return FAILED_TO_PARSEJSON;
	}

	Json::Value jsonList;
	ret = parser.parseList("memberships", jsonList);
	if(RT_OK != ret)
	{
		return ret;
	}

	for(Json::Value::ArrayIndex i = 0; i < jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		TeamspaceMembership teamspaceMembership;

		PARSEJSON_INT64_LEAF(id);
		teamspaceMembership.id(id);
		PARSEJSON_STRING_LEAF(teamRole);
		teamspaceMembership.teamRole(convertTeamspaceRoleType(teamRole));
		PARSEJSON_STRING_LEAF(role);
		teamspaceMembership.role(role);

		Json::Value jsonTmp = jsonObj;
		{			
			jsonObj = jsonTmp["teamspace"];
			TeamspaceNode teamspace;
			PARSEJSON_INT64_LEAF(id);
			teamspace.id(id);
			PARSEJSON_STRING_LEAF(name);
			teamspace.name(name);
			PARSEJSON_STRING_LEAF(description);
			teamspace.description(description);
			PARSEJSON_INT_LEAF(status);
			teamspace.status((TeamspaceStatus)status);
			PARSEJSON_INT_LEAF(curNumbers);
			teamspace.curNumbers(curNumbers);
			PARSEJSON_INT64_LEAF(createdBy);
			teamspace.createdBy(createdBy);
			PARSEJSON_STRING_LEAF(createedByUserName);
			teamspace.createdByUserName(createedByUserName);
			PARSEJSON_INT64_LEAF(ownedBy);
			teamspace.ownedBy(ownedBy);
			PARSEJSON_STRING_LEAF(ownedByUserName);
			teamspace.ownedByUserName(ownedByUserName);
			PARSEJSON_INT64_LEAF(createdAt);
			teamspace.createdAt(createdAt);
			PARSEJSON_INT64_LEAF(spaceQuota);
			teamspace.spaceQuota(spaceQuota);
			PARSEJSON_INT64_LEAF(spaceUsed);
			teamspace.spaceUsed(spaceUsed);
			PARSEJSON_INT_LEAF(maxVersions);
			teamspace.maxVersions(maxVersions);
			PARSEJSON_INT_LEAF(maxMembers);
			teamspace.maxMembers(maxMembers);

			teamspaceMembership.teamspace(teamspace);
		}

		{
			jsonObj = jsonTmp["member"];
			TeamspaceMemberNode teamspaceMember;
			PARSEJSON_INT64_LEAF(id);
			teamspaceMember.id(id);
			PARSEJSON_STRING_LEAF(type);
			teamspaceMember.type(convertTeamspaceMemberType(type));
			PARSEJSON_STRING_LEAF(loginName);
			teamspaceMember.loginName(loginName);
			PARSEJSON_STRING_LEAF(description);
			teamspaceMember.description(description);

			teamspaceMembership.teamspaceMember(teamspaceMember);
		}

		teamspaceMemberships.push_back(teamspaceMembership);
	}
	return RT_OK;
}

int32_t JsonParser::parseTeamspace(DataBuffer &dataBuf, TeamspaceNode& teamspace)
{
	Parser<TeamspaceNode> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(id);
	teamspace.id(id);
	PARSE_STRING_LEAF(name);
	teamspace.name(name);
	PARSE_STRING_LEAF(description);
	teamspace.description(description);
	PARSE_INT_LEAF(status);
	teamspace.status((TeamspaceStatus)status);
	PARSE_INT_LEAF(curNumbers);
	teamspace.curNumbers(curNumbers);
	PARSE_INT64_LEAF(createdBy);
	teamspace.createdBy(createdBy);
	PARSE_STRING_LEAF(createedByUserName);
	teamspace.createdByUserName(createedByUserName);
	PARSE_INT64_LEAF(ownedBy);
	teamspace.ownedBy(ownedBy);
	PARSE_STRING_LEAF(ownedByUserName);
	teamspace.ownedByUserName(ownedByUserName);
	PARSE_INT64_LEAF(createdAt);
	teamspace.createdAt(createdAt);
	PARSE_INT64_LEAF(spaceQuota);
	teamspace.spaceQuota(spaceQuota);
	PARSE_INT64_LEAF(spaceUsed);
	teamspace.spaceUsed(spaceUsed);
	PARSE_INT_LEAF(maxVersions);
	teamspace.maxVersions(maxVersions);
	PARSE_INT_LEAF(maxMembers);
	teamspace.maxMembers(maxMembers);

	return RT_OK;
}

int32_t JsonParser::parseTeamspaceMembership(DataBuffer &dataBuf, TeamspaceMembership& teamspaceMembership)
{
	Parser<TeamspaceMembership> parser(dataBuf);
	int32_t ret;

	PARSE_INT64_LEAF(id);
	teamspaceMembership.id(id);
	PARSE_STRING_LEAF(teamRole);
	teamspaceMembership.teamRole(convertTeamspaceRoleType(teamRole));
	PARSE_STRING_LEAF(role);
	teamspaceMembership.role(role);

	{
		PARSE_JSON_LEAF(teamspace);
		Json::Value jsonObj = teamspace;
		TeamspaceNode teamspaceNode;
		PARSEJSON_INT64_LEAF(id);
		teamspaceNode.id(id);
		PARSEJSON_STRING_LEAF(name);
		teamspaceNode.name(name);
		PARSEJSON_STRING_LEAF(description);
		teamspaceNode.description(description);
		PARSEJSON_INT_LEAF(status);
		teamspaceNode.status((TeamspaceStatus)status);
		PARSEJSON_INT_LEAF(curNumbers);
		teamspaceNode.curNumbers(curNumbers);
		PARSEJSON_INT64_LEAF(createdBy);
		teamspaceNode.createdBy(createdBy);
		PARSEJSON_STRING_LEAF(createedByUserName);
		teamspaceNode.createdByUserName(createedByUserName);
		PARSEJSON_INT64_LEAF(ownedBy);
		teamspaceNode.ownedBy(ownedBy);
		PARSEJSON_STRING_LEAF(ownedByUserName);
		teamspaceNode.ownedByUserName(ownedByUserName);
		PARSEJSON_INT64_LEAF(createdAt);
		teamspaceNode.createdAt(createdAt);
		PARSEJSON_INT64_LEAF(spaceQuota);
		teamspaceNode.spaceQuota(spaceQuota);
		PARSEJSON_INT64_LEAF(spaceUsed);
		teamspaceNode.spaceUsed(spaceUsed);
		PARSEJSON_INT_LEAF(maxVersions);
		teamspaceNode.maxVersions(maxVersions);
		PARSEJSON_INT_LEAF(maxMembers);
		teamspaceNode.maxMembers(maxMembers);

		teamspaceMembership.teamspace(teamspaceNode);
	}

	{
		PARSE_JSON_LEAF(member);
		Json::Value jsonObj = member;
		TeamspaceMemberNode teamspaceMemberNode;
		PARSEJSON_INT64_LEAF(id);
		teamspaceMemberNode.id(id);
		PARSEJSON_STRING_LEAF(type);
		teamspaceMemberNode.type(convertTeamspaceMemberType(type));
		PARSEJSON_STRING_LEAF(loginName);
		teamspaceMemberNode.loginName(loginName);
		PARSEJSON_STRING_LEAF(description);
		teamspaceMemberNode.description(description);

		teamspaceMembership.teamspaceMember(teamspaceMemberNode);
	}

	return RT_OK;
}

int32_t JsonParser::parseCurUserInfo(DataBuffer &writeBuf, StorageUserInfo& storageUserInfo)
{
	Parser<StorageUserInfo> parser(writeBuf);
	int ret = RT_OK;

	PARSE_INT64_LEAF(cloudUserId);
	storageUserInfo.user_id = cloudUserId;
	PARSE_STRING_LEAF(loginName);
	storageUserInfo.login_name = loginName;
	PARSE_STRING_LEAF(name);
	storageUserInfo.name = name;
	PARSE_STRING_LEAF(email);
	storageUserInfo.email = email;
	PARSE_STRING_LEAF(description);
	storageUserInfo.description = description;
	PARSE_INT64_LEAF(spaceQuota);
	storageUserInfo.spaceQuota = spaceQuota;
	PARSE_INT_LEAF(status);
	storageUserInfo.status = status;
	PARSE_INT_LEAF(regionId);
	storageUserInfo.regionId = regionId;
	PARSE_INT_LEAF(maxVersions);
	storageUserInfo.maxVersions = maxVersions;

	return RT_OK;
}

int32_t JsonParser::parseRegionInfo(DataBuffer &writeBuf, RegionIdInfoArray &regionIdInfoArray)
{
	int ret = RT_OK;

	RegionIdInfo tmpRegionId;
	std::string tmp_str;
	Json::Value jsonList(Json::arrayValue);
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_INT64_LEAF(id);
		tmpRegionId.id = id;
		PARSEJSON_STRING_LEAF(name);
		tmpRegionId.name = name;
		PARSEJSON_STRING_LEAF(description);
		tmpRegionId.description = description;
		regionIdInfoArray.push_back(tmpRegionId);
	}

	return RT_OK;
}
