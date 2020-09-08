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
/*****************************************************************************************
Function Name : parseListFolderResult
Description   : 解析列举目录的返回结果
Input         : jsonStr		服务器返回的json字符串
Output        : pList		文件列表
				total_count 返回文件（包括目录）总数
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
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

/*****************************************************************************************
Function Name : getFolderUnitFromJson
Description   : 解析列举目录的返回结果
Input         : jsonObj		文件(夹)json对象
					"id":{id}, 
					"type":1, 
					"name":"{name}", 
					"parent":{parent},
					"ownedBy":{ownedBy}, 				
					"size":{size}, 
					"createdAt":{createdAt}, 
					"modifiedAt":{modifiedAt}, 
					"objectId":"{objectId}", 
					"isShare":{isShare}, 
					"isSync":{isSync}, 
					"isSharelink":{isSharelink}, 
					"isEncrypt":{isEncrypt}
					"status":{status}, 
					"sha1":"{sha1}", 				
					"contentCreatedAt":{contentCreatedAt},
					"contentModifiedAt":{contentModifiedAt},
					"createdBy":{createdBy}, 
					"modifiedBy":{modifiedBy}, 
					"description":"{description}", 
					
Output        : objectUnit	文件对象
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
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
	objectUnit.objectId(objectId);

	PARSEJSON_INT_LEAF(versions);
	objectUnit.version(versions);

	PARSEJSON_BOOL_LEAF(isShare);
	objectUnit.isShare(isShare);

	PARSEJSON_BOOL_LEAF(isSync);
	objectUnit.isSync(isSync);

	PARSEJSON_BOOL_LEAF(isEncrypt);
	objectUnit.isEncrypt(isEncrypt);

	PARSEJSON_BOOL_LEAF(isSharelink);
	objectUnit.isSharelink(isSharelink);

	PARSEJSON_INT64_LEAF(modifiedBy);
	objectUnit.modifiedBy(modifiedBy);

	Fingerprint fingerprint;
	if(!jsonObj["sha1"].isNull())
	{
		fingerprint.algorithm = Fingerprint::SHA1;
		fingerprint.fingerprint = jsonObj["sha1"].asString();
	}
	else if (!jsonObj["md5"].isNull())
	{
		fingerprint.algorithm = Fingerprint::MD5;
		fingerprint.fingerprint = jsonObj["md5"].asString();
	}
	objectUnit.fingerprint(fingerprint);

	PARSEJSON_STRING_LEAF(extraType);
	if(!extraType.empty())
	{
		objectUnit.extraType(extraType);
	}

	return ret;
}

/* 
"user_id":{user_id}, 
"token_type":{token_type},
"token":{token}, 
"period":{period} 
*/
int32_t JsonParser::parseLoginRespInfo(DataBuffer &dataBuf, LoginRespInfo& loginResp)
{
	Parser<LoginRespInfo> parser(dataBuf);
	int32_t ret = RT_OK;

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

	PARSE_BOOL_LEAF(needChangePassword);
	loginResp.needChangePassword = needChangePassword;

	PARSE_BOOL_LEAF(needDeclaration);
	loginResp.declarationInfo.needsigndeclare = needDeclaration;

	Json::Value jsonObj = Json::nullValue;
	ret = parser.parseLeaf("lastAccessTerminal", jsonObj);
	if (jsonObj.isNull())
	{
		return ret;
	}
	PARSEJSON_STRING_LEAF(deviceType);
	loginResp.lastAccessTerminal.deviceType = deviceType;

	PARSEJSON_STRING_LEAF(deviceName);
	loginResp.lastAccessTerminal.deviceName = deviceName;

	PARSEJSON_STRING_LEAF(deviceOS);
	loginResp.lastAccessTerminal.deviceOS = deviceOS;

	PARSEJSON_STRING_LEAF(deviceAgent);
	loginResp.lastAccessTerminal.deviceAgent = deviceAgent;

	PARSEJSON_STRING_LEAF(lastAccessIP);
	loginResp.lastAccessTerminal.lastAccessIP = lastAccessIP;

	PARSEJSON_INT64_LEAF(lastAccessAt);
	loginResp.lastAccessTerminal.lastAccessAt = lastAccessAt;

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

	PARSE_BOOL_LEAF(needChangePassword);
	loginResp.needChangePassword = needChangePassword;

	PARSE_BOOL_LEAF(needsigndeclare);
	loginResp.declarationInfo.needsigndeclare = needsigndeclare;

	Json::Value jsonObj = Json::nullValue;
	ret = parser.parseLeaf("lastAccessTerminal", jsonObj);
	if (jsonObj.isNull())
	{
		return ret;
	}
	PARSEJSON_STRING_LEAF(deviceType);
	loginResp.lastAccessTerminal.deviceType = deviceType;

	PARSEJSON_STRING_LEAF(deviceName);
	loginResp.lastAccessTerminal.deviceName = deviceName;

	PARSEJSON_STRING_LEAF(deviceOS);
	loginResp.lastAccessTerminal.deviceOS = deviceOS;

	PARSEJSON_STRING_LEAF(deviceAgent);
	loginResp.lastAccessTerminal.deviceAgent = deviceAgent;

	PARSEJSON_STRING_LEAF(lastAccessIP);
	loginResp.lastAccessTerminal.lastAccessIP = lastAccessIP;

	PARSEJSON_INT64_LEAF(lastAccessAt);
	loginResp.lastAccessTerminal.lastAccessAt = lastAccessAt;

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
	fileItem.objectId(objectId);

	PARSE_INT_LEAF(versions);
	fileItem.version(versions);

	PARSE_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSE_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSE_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSE_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	Fingerprint fingerprint;
	if (RT_OK == parser.parseLeafSafe("sha1", fingerprint.fingerprint))
	{
		fingerprint.algorithm = Fingerprint::SHA1;
	}
	else if (RT_OK == parser.parseLeafSafe("md5", fingerprint.fingerprint))
	{
		fingerprint.algorithm = Fingerprint::MD5;
	}
	fileItem.fingerprint(fingerprint);

	return ret;
}

int32_t JsonParser::parseFilePermissions(DataBuffer &dataBuf, File_Permissions& filePermissions)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	filePermissions = FP_INVALID;
	//"permissions":{"browse":1,"preview":1,"download":0,"upload":0,"edit":0,"delete":0,"publishLink":0,"authorize":0}}
	int32_t tempPerm = 0;
	jsonObj = jsonObj["permissions"];
	if(jsonObj["browse"].isInt())			
	{
		if(1==jsonObj["browse"].asInt()) tempPerm = tempPerm|FP_BROWSE;
	}
	if(jsonObj["preview"].isInt())			
	{
		if(1==jsonObj["preview"].asInt()) tempPerm = tempPerm|FP_PREVIEW;
	}
	if(jsonObj["download"].isInt())			
	{
		if(1==jsonObj["download"].asInt()) tempPerm = tempPerm|FP_DOWNLOAD;
	}
	if(jsonObj["upload"].isInt())			
	{
		if(1==jsonObj["upload"].asInt()) tempPerm = tempPerm|FP_UPLOAD;
	}
	if(jsonObj["edit"].isInt())			
	{
		if(1==jsonObj["edit"].asInt()) tempPerm = tempPerm|FP_EDIT;
	}
	if(jsonObj["delete"].isInt())			
	{
		if(1==jsonObj["delete"].asInt()) tempPerm = tempPerm|FP_DELETE;
	}
	if(jsonObj["publishLink"].isInt())			
	{
		if(1==jsonObj["publishLink"].asInt()) tempPerm = tempPerm|FP_PUBLISHLINK;
	}
	if(jsonObj["authorize"].isInt())			
	{
		if(1==jsonObj["authorize"].asInt()) tempPerm = tempPerm|FP_AUTHORIZE;
	}
	filePermissions = (File_Permissions)tempPerm;
	return RT_OK;
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
	fileItem.objectId(objectId);

	PARSEJSON_INT_LEAF(versions);
	fileItem.version(versions);

	PARSEJSON_BOOL_LEAF(isShare);
	fileItem.isShare(isShare);

	PARSEJSON_BOOL_LEAF(isSync);
	fileItem.isSync(isSync);

	PARSEJSON_BOOL_LEAF(isEncrypt);
	fileItem.isEncrypt(isEncrypt);

	PARSEJSON_BOOL_LEAF(isSharelink);
	fileItem.isSharelink(isSharelink);

	Fingerprint fingerprint;
	if (!jsonObj["sha1"].isNull())
	{
		fingerprint.algorithm = Fingerprint::SHA1;
		fingerprint.fingerprint = jsonObj["sha1"].asString();
	}
	else if (!jsonObj["md5"].isNull())
	{
		fingerprint.algorithm = Fingerprint::MD5;
		fingerprint.fingerprint = jsonObj["md5"].asString();
	}
	fileItem.fingerprint(fingerprint);

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
	fileItem.createdBy(createdBy);

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

	PARSE_STRING_LEAF(extraType);
	if(!extraType.empty())
	{
		fileItem.extraType(extraType);
	}

	return ret;
}

/*
{ 
file_id	string	文件ID
upload_ip	string	上传地址
upload_url	string	上传URL，样例如/gd164asdf121545asdf1
upload_id	string	分片上传id，分片上传必须返回
} 
*/
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


/*****************************************************************************************
Function Name : getErrorMessageFromJson
Description   : 从JSON中解析错误信息
Input         : dataBuf							
Output        : errorMessege错误信息
*******************************************************************************************/
void JsonParser::getErrorMessageFromJson(DataBuffer &dataBuf, std::string &errorMessege)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["code"].isString())				
	{											
		errorMessege = jsonObj["code"].asString();			
	}
}


void JsonParser::parseUploadUrl(DataBuffer &dataBuf, std::string& uploadUrl)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["uploadUrl"].isString())				
	{											
		uploadUrl = jsonObj["uploadUrl"].asString();			
	}
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

int32_t JsonParser::parseListenerUrl(DataBuffer &dataBuf, std::string& url)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["url"].isString())				
	{											
		url = jsonObj["url"].asString();			
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

int32_t JsonParser::parseShareNodeList(DataBuffer &dataBuf, ShareNodeList& shareNodes, int64_t& total_cnt)
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

		PARSEJSON_INT64_LEAF(nodeId);
		shareNode.id(nodeId);
		PARSEJSON_INT_LEAF(type);
		shareNode.type(type);
		PARSEJSON_INT64_LEAF(ownerId);
		shareNode.ownerId(ownerId);
		PARSEJSON_STRING_LEAF(ownerName);
		shareNode.ownerName(ownerName);
		PARSEJSON_INT64_LEAF(sharedUserId);
		shareNode.sharedUserId(sharedUserId);
		PARSEJSON_STRING_LEAF(sharedUserType);
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
		shareNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(modifiedBy);
		shareNode.modifiedBy(modifiedBy);
		PARSEJSON_STRING_LEAF(roleName);
		shareNode.roleName(roleName);
		PARSEJSON_INT_LEAF(status);
		shareNode.status(status);
		PARSEJSON_INT64_LEAF(size);
		shareNode.size(size);
		PARSEJSON_STRING_LEAF(extraType);
		if(!extraType.empty())
		{
			shareNode.extraType(extraType);
		}

		shareNodes.push_back(shareNode);
	}
	return RT_OK;
}

int32_t JsonParser::parseMyShareNodeList(DataBuffer &dataBuf, MyShareNodeList& shareNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<MyShareNode> parser(dataBuf);
	Json::Value jsonList;
	
	PARSE_INT_LEAF(totalCount);
	total_cnt = (int32_t)totalCount;

	parser.parseList("contents", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		MyShareNode shareNode;
		PARSEJSON_INT64_LEAF(nodeId);
		shareNode.id = nodeId;
		PARSEJSON_INT_LEAF(type);
		shareNode.type = type;
		PARSEJSON_STRING_LEAF(name);
		shareNode.name = name;
		PARSEJSON_INT64_LEAF(size);
		shareNode.size = size;
		PARSEJSON_STRING_LEAF(extraType);
		if(!extraType.empty())
		{
			shareNode.extraType = extraType;
		}
	
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
		PARSEJSON_INT64_LEAF(accountId);
		shareUserInfo.accountId(accountId);
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
	if(effectiveAt != 0)
	{
		shareLinkNode.effectiveAt(Utility::DateTime(effectiveAt, Utility::UtcType::Unix).getWindowsFileTime());
	}
	else
	{
		shareLinkNode.effectiveAt(effectiveAt);
	}

	PARSE_INT64_LEAF(expireAt);
	if(expireAt != 0)
	{
		shareLinkNode.expireAt(Utility::DateTime(expireAt, Utility::UtcType::Unix).getWindowsFileTime());
	}
	else
	{
		shareLinkNode.expireAt(expireAt);
	}

	PARSE_INT64_LEAF(createdAt);
	shareLinkNode.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedAt);
	shareLinkNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(createdBy);
	shareLinkNode.createdBy(createdBy);

	PARSE_INT64_LEAF(modifiedBy);
	shareLinkNode.createdBy(modifiedBy);

	PARSE_STRING_LEAF(creator);
	shareLinkNode.creator(creator);

	return ret;
}

int32_t JsonParser::parseShareLinkNodeV2(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode)
{
	Parser<ShareLinkNode> parser(dataBuf);
	int32_t ret = RT_OK;

	PARSE_STRING_LEAF(id);
	shareLinkNode.id(id);

	PARSE_INT64_LEAF(nodeId);
	shareLinkNode.iNodeId(nodeId);

	PARSE_INT64_LEAF(ownedBy);
	shareLinkNode.ownedBy(ownedBy);

	PARSE_STRING_LEAF(url);
	shareLinkNode.url(url);

	PARSE_STRING_LEAF(plainAccessCode);
	shareLinkNode.plainAccessCode(plainAccessCode);

	PARSE_STRING_LEAF(accessCodeMode);
	shareLinkNode.accesCodeMode(accessCodeMode);


	PARSE_INT64_LEAF(effectiveAt);
	if(effectiveAt != 0)
	{
		shareLinkNode.effectiveAt(Utility::DateTime(effectiveAt, Utility::UtcType::Unix).getWindowsFileTime());
	}
	else
	{
		shareLinkNode.effectiveAt(effectiveAt);
	}
	
	PARSE_INT64_LEAF(expireAt);
	if(expireAt != 0)
	{
		shareLinkNode.expireAt(Utility::DateTime(expireAt, Utility::UtcType::Unix).getWindowsFileTime());
	}
	else
	{
		shareLinkNode.expireAt(expireAt);
	}

	PARSE_INT64_LEAF(createdAt);
	shareLinkNode.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedAt);
	shareLinkNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(createdBy);
	shareLinkNode.createdBy(createdBy);

	PARSE_INT64_LEAF(modifiedBy);
	shareLinkNode.modifiedBy(modifiedBy);

	PARSE_STRING_LEAF(creator);
	shareLinkNode.creator(creator);

	PARSE_STRING_LEAF(role);
	shareLinkNode.role(role);

	Json::Value jsonList;
	parser.parseList("identities", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_STRING_LEAF(identity);
		shareLinkNode.identity_.push_back(identity);
	}
	return ret;
}

int32_t JsonParser::parseShareLinkNodeList(DataBuffer &dataBuf, ShareLinkNodeList& shareLinkNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<ShareLinkNode> parser(dataBuf);
	Json::Value jsonList(Json::arrayValue);

	PARSE_INT_LEAF(totalCount);
	total_cnt = totalCount;

	parser.parseList("links", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		ShareLinkNode shareLinkNode;

		PARSEJSON_STRING_LEAF(id);
		shareLinkNode.id(id);
		PARSEJSON_INT64_LEAF(iNodeId);
		shareLinkNode.iNodeId(iNodeId);
		PARSEJSON_INT64_LEAF(ownedBy);
		shareLinkNode.ownedBy(ownedBy);
		PARSEJSON_STRING_LEAF(url);
		shareLinkNode.url(url);
		PARSEJSON_STRING_LEAF(plainAccessCode);
		shareLinkNode.plainAccessCode(plainAccessCode);
		PARSEJSON_STRING_LEAF(accessCodeMode);
		shareLinkNode.accesCodeMode(accessCodeMode);
		PARSEJSON_INT64_LEAF(effectiveAt);
		if(effectiveAt != 0)
		{
			shareLinkNode.effectiveAt(Utility::DateTime(effectiveAt, Utility::UtcType::Unix).getWindowsFileTime());
		}
		else
		{
			shareLinkNode.effectiveAt(effectiveAt);
		}

		PARSEJSON_INT64_LEAF(expireAt);
		if(expireAt != 0)
		{
			shareLinkNode.expireAt(Utility::DateTime(expireAt, Utility::UtcType::Unix).getWindowsFileTime());
		}
		else
		{
			shareLinkNode.expireAt(expireAt);
		}
		PARSEJSON_INT64_LEAF(createdAt);
		shareLinkNode.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(modifiedAt);
		shareLinkNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(createdBy);
		shareLinkNode.createdBy(createdBy);
		PARSEJSON_INT64_LEAF(modifiedBy);
		shareLinkNode.modifiedBy(modifiedBy);
		PARSEJSON_STRING_LEAF(creator);
		shareLinkNode.creator(creator);
		PARSEJSON_STRING_LEAF(role);
		shareLinkNode.role(role);

		Json::Value tmpJsonList(Json::arrayValue);
		tmpJsonList = jsonObj["identities"];

		for(uint32_t i=0; i<tmpJsonList.size(); ++i)
		{
			Json::Value jsonObj = tmpJsonList[i];

			PARSEJSON_STRING_LEAF(identity);
			shareLinkNode.identity_.push_back(identity);
		}

		shareLinkNodes.push_back(shareLinkNode);
	}
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
		else if("backupAllowedRule" == option)
		{
			serverSysConfig.backupAllowedRule(value);
		}
		else if("backupForbiddenRule" == option)
		{
			serverSysConfig.backupForbiddenRule(value);
		}
	}

	return RT_OK;
}

int32_t JsonParser::parseUpdateInfo(DataBuffer &dataBuf, UpdateInfo& updateInfo)
{
	Parser<UpdateInfo> parser(dataBuf);
	int32_t ret = RT_OK;

	PARSE_STRING_LEAF(versionInfo);
	updateInfo.versionInfo = versionInfo;

	PARSE_STRING_LEAF(downloadUrl);
	updateInfo.downloadUrl = downloadUrl;

	return ret;
}

int32_t JsonParser::parseFeatureCode(DataBuffer &dataBuf, std::string& featureCode)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["featurecode"].isString())				
	{											
		featureCode = jsonObj["featurecode"].asString();			
	}		

	return RT_OK;
}

int32_t JsonParser::parseFileInfoByShareLink(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode, FileItem& fileItem)
{
	Parser<ShareLinkNode> parser(dataBuf);
	int ret = RT_OK;

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
	shareLinkNode.effectiveAt(Utility::DateTime(effectiveAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(expireAt);
	shareLinkNode.expireAt(Utility::DateTime(expireAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(createdAt);
	shareLinkNode.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedAt);
	shareLinkNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());

	PARSE_INT64_LEAF(modifiedBy);
	shareLinkNode.createdBy(modifiedBy);

	PARSE_STRING_LEAF(creator);
	shareLinkNode.creator(creator);

	tmp_str.assign((char *)dataBuf.pBuf, sizeof(ShareLinkNode), sizeof(FileItem));
	reader.parse(tmp_str, jsonfileitem);

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

int32_t JsonParser::parseTeamSpacesUserInfoList(DataBuffer &dataBuf, UserTeamSpaceNodeInfoArray& _list, int64_t &total)
{
	Parser<UserTeamSpaceNodeInfo> parser(dataBuf);
	int ret = RT_OK;
	Json::Value jsonList;

	PARSE_UINT64_LEAF(totalCount);
	total = totalCount;
	if(totalCount == 0)
	{
		return RT_NOT_IMPLEMENT;
	}

	(void)parser.parseList("memberships",jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		UserTeamSpaceNodeInfo _tmp;
		DataBuffer _buf;
		Json::Value jsonObj = jsonList[i];

		//"id":1,"teamId":943,"teamRole":"admin","role":"auther"

		PARSEJSON_INT64_LEAF(id);
		_tmp.id(id);
		PARSEJSON_INT64_LEAF(teamId);
		_tmp.teamId(teamId);
		PARSEJSON_STRING_LEAF(teamRole);
		_tmp.teamRole(teamRole);
		PARSEJSON_STRING_LEAF(role);
		_tmp.role(role);

		Json::Value tmp_m = jsonObj["member"];
		JsonToTeamSpaceMemberNodeInfo(tmp_m,_tmp.teamInfo_);

		Json::Value tmp_t = jsonObj["teamspace"];
		JsonToTeamSpacesNode(tmp_t,_tmp.member_);

		_list.push_back(_tmp);
	}
	return RT_OK;
}

int32_t JsonParser::JsonToTeamSpaceMemberNodeInfo(Json::Value _value,TeamSpaceMemberNodeInfo& _tmp)
{
	Json::Value jsonObj = _value;

// 	PARSEJSON_INT64_LEAF(id);
// 	_tmp.id(id);
	PARSEJSON_STRING_LEAF(id);
	_tmp.id(SD::Utility::String::string_to_type<int64_t>(id));
	PARSEJSON_STRING_LEAF(loginName);
	_tmp.loginName(loginName);
	PARSEJSON_STRING_LEAF(type);
	_tmp.type(type);
	PARSEJSON_STRING_LEAF(name);
	_tmp.name(name);
	PARSEJSON_STRING_LEAF(description);
	_tmp.desc(description);

	return RT_OK;
}

int32_t JsonParser::JsonToTeamSpacesNode(Json::Value _value,TeamSpacesNode& _tmp)
{
	Json::Value jsonObj = _value;

	PARSEJSON_INT64_LEAF(id);
	_tmp.id(id);
	PARSEJSON_STRING_LEAF(name);
	_tmp.name(name);
	PARSEJSON_STRING_LEAF(description);
	_tmp.description(description);
	PARSEJSON_INT_LEAF(status);
	_tmp.status(status);
	PARSEJSON_INT_LEAF(curNumbers);
	_tmp.curNumbers(curNumbers);
	PARSEJSON_INT64_LEAF(ownedBy);
	_tmp.ownerBy(ownedBy);
	PARSEJSON_INT64_LEAF(createdBy);
	_tmp.createdBy(createdBy);
	PARSEJSON_STRING_LEAF(ownedByUserName);
	_tmp.ownerByUserName(ownedByUserName);
	PARSEJSON_STRING_LEAF(createdByUserName);
	_tmp.createdByUserName(createdByUserName);
	PARSEJSON_INT64_LEAF(createdAt);
	_tmp.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSEJSON_INT64_LEAF(spaceQuota);
	_tmp.spaceQuota(spaceQuota);
	PARSEJSON_UINT64_LEAF(spaceUsed);
	_tmp.usedQuota(spaceUsed);
	PARSEJSON_INT_LEAF(maxMembers);
	_tmp.maxMembers(maxMembers);
	PARSEJSON_INT_LEAF(maxVersions);
	_tmp.maxVersions(maxVersions);

	return RT_OK;
}

int32_t JsonParser::parseTeamSpaceNode(DataBuffer &dataBuf,TeamSpacesNode& _info)
{
	Parser<TeamSpacesNode> parser(dataBuf);
	int ret = RT_OK;

	PARSE_INT64_LEAF(id);
	_info.id(id);
	PARSE_STRING_LEAF(name);
	_info.name(name);
	PARSE_STRING_LEAF(description);
	_info.description(description);
	PARSE_INT_LEAF(status);
	_info.status(status);
	PARSE_INT_LEAF(curNumbers);
	_info.curNumbers(curNumbers);
	PARSE_INT64_LEAF(ownerBy);
	_info.ownerBy(ownerBy);
	PARSE_INT64_LEAF(createdBy);
	_info.createdBy(createdBy);
	PARSE_STRING_LEAF(ownerByUserName);
	_info.ownerByUserName(ownerByUserName);
	PARSE_STRING_LEAF(createdByUserName);
	_info.createdByUserName(createdByUserName);
	PARSE_INT64_LEAF(createdAt);
	_info.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSE_INT64_LEAF(spaceQuota);
	_info.spaceQuota(spaceQuota);
	PARSE_INT_LEAF(maxMembers);
	_info.maxMembers(maxMembers);
	PARSE_INT_LEAF(maxVersions);
	_info.maxVersions(maxVersions);
	PARSE_UINT64_LEAF(spaceUsed);
	_info.usedQuota(spaceUsed);

	return ret;
}

int32_t JsonParser::parseTeamSpacesMemberInfo(DataBuffer &dataBuf,UserTeamSpaceNodeInfo& _info)
{
	Parser<UserTeamSpaceNodeInfo> parser(dataBuf);
	int ret = RT_OK;

	PARSE_INT_LEAF(id);
	_info.id(id);
	PARSE_INT_LEAF(teamId);
	_info.teamId(teamId);
	PARSE_STRING_LEAF(teamRole);
	_info.teamRole(teamRole);
	PARSE_STRING_LEAF(role);
	_info.role(role);

	Json::Value tmp_m;
	(void)parser.parseList("member",tmp_m);
	JsonToTeamSpaceMemberNodeInfo(tmp_m, _info.teamInfo_);

	Json::Value tmp_t;
	(void)parser.parseList("teamspace",tmp_t);
	JsonToTeamSpacesNode(tmp_t, _info.member_);

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

int32_t JsonParser::parseFileVersionInfo(DataBuffer &writeBuf, FileVersionList &fileVersionNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<FileVersionItem> parser(writeBuf);
	Json::Value jsonList;
	FileVersionItem fileVersionInfo;

	PARSE_UINT64_LEAF(totalCount);
	total_cnt = totalCount;
	if(totalCount == 0)
	{
		return RT_NOT_IMPLEMENT;
	}

	(void)parser.parseList("versions",jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_INT64_LEAF(id);
		fileVersionInfo.id(id);
		PARSEJSON_STRING_LEAF(name);
		fileVersionInfo.name(name);
		PARSEJSON_INT_LEAF(type);
		fileVersionInfo.type((FILE_TYPE)type);
		PARSEJSON_INT64_LEAF(parent);
		fileVersionInfo.parent(parent);
		PARSEJSON_INT64_LEAF(ownedBy);
		fileVersionInfo.ownedBy(ownedBy);
		PARSEJSON_INT64_LEAF(size);
		fileVersionInfo.size(size);
		PARSEJSON_INT64_LEAF(createdAt);
		fileVersionInfo.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(modifiedAt);
		fileVersionInfo.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_STRING_LEAF(objectId);
		fileVersionInfo.objectId(objectId);
		PARSEJSON_BOOL_LEAF(isEncrypt);
		fileVersionInfo.isEncrypt(isEncrypt);
		PARSEJSON_INT_LEAF(status);
		fileVersionInfo.status(status);
		PARSEJSON_INT64_LEAF(contentCreatedAt);
		fileVersionInfo.contentCreatedAt(Utility::DateTime(contentCreatedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(contentModifiedAt);
		fileVersionInfo.contentModifiedAt(Utility::DateTime(contentModifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(createdBy);
		fileVersionInfo.createdBy(createdBy);
		PARSEJSON_INT64_LEAF(modifiedBy);
		fileVersionInfo.modifiedBy(modifiedBy);
		Fingerprint fingerprint;
		if(jsonObj["sha1"].isString())
		{
			fingerprint.fingerprint = jsonObj["sha1"].asString();
			fingerprint.algorithm = Fingerprint::SHA1;
		}
		else if (jsonObj["md5"].isString())
		{
			fingerprint.fingerprint = jsonObj["md5"].asString();
			fingerprint.algorithm = Fingerprint::MD5;
		}
		fileVersionInfo.fingerprint(fingerprint);

		fileVersionNodes.push_back(fileVersionInfo);
	}

	return RT_OK;
}

int32_t JsonParser::parseFilesHadShareLink(DataBuffer &writeBuf, MyShareNodeList& nodes, int64_t& count)
{
	int ret = RT_OK;
	
	Parser<FileVersionItem> parser(writeBuf);
	Json::Value jsonList;
	FileVersionItem fileVersionInfo;

	PARSE_UINT64_LEAF(totalCount);
	count = totalCount;
	if(totalCount == 0)
	{
		return RT_NOT_IMPLEMENT;
	}

	(void)parser.parseList("folders",jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		MyShareNode node;
		PARSEJSON_INT64_LEAF(id);
		node.id = id;
		PARSEJSON_INT_LEAF(type);
		node.type = type;
		PARSEJSON_STRING_LEAF(name);
		node.name = name;
		PARSEJSON_INT64_LEAF(size);
		node.size = size;
		PARSEJSON_INT64_LEAF(linkCount);
		node.linkCount = linkCount;
		PARSEJSON_STRING_LEAF(extraType);
		if(!extraType.empty())
		{
			node.extraType = extraType;
		}
		nodes.push_back(node);
	}

	(void)parser.parseList("files",jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		MyShareNode node;
		PARSEJSON_INT64_LEAF(id);
		node.id = id;
		PARSEJSON_INT_LEAF(type);
		node.type = type;
		PARSEJSON_STRING_LEAF(name);
		node.name = name;
		PARSEJSON_INT64_LEAF(size);
		node.size = size;
		PARSEJSON_INT64_LEAF(linkCount);
		node.linkCount = linkCount;

		nodes.push_back(node);
	}		
	return RT_OK;	
}

int32_t JsonParser::parseMsgInfo(DataBuffer &writeBuf, MsgList &msgNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<MsgNode> parser(writeBuf);

	PARSE_UINT64_LEAF(totalCount);
	total_cnt = totalCount;

	Json::Value jsonList;
	(void)parser.parseList("messages", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		MsgNode msgNode;
		PARSEJSON_INT64_LEAF(id);
		msgNode.id = id;
		PARSEJSON_INT64_LEAF(providerId);
		msgNode.providerId = providerId;
		PARSEJSON_STRING_LEAF(providerUsername);
		msgNode.providerUsername = providerUsername;
		PARSEJSON_STRING_LEAF(providerName);
		msgNode.providerName = providerName;
		PARSEJSON_INT64_LEAF(receiverId);
		msgNode.receiverId = receiverId;
		PARSEJSON_STRING_LEAF(appId);
		msgNode.appId = appId;
		PARSEJSON_STRING_LEAF(type);
		msgNode.type = convertMsgType(type);
		PARSEJSON_STRING_LEAF(status);
		msgNode.status = ("read"==status)?MS_Readed:MS_UnRead;
		PARSEJSON_INT64_LEAF(createdAt);
		msgNode.createdAt = createdAt/1000;
		PARSEJSON_INT64_LEAF(expiredAt);
		msgNode.expiredAt = expiredAt/1000;

		jsonObj = jsonObj["params"];
		MsgParams params;
		PARSEJSON_INT64_LEAF(nodeId);
		params.nodeId = nodeId;
		PARSEJSON_STRING_LEAF(nodeName);
		params.nodeName = nodeName;
		PARSEJSON_STRING_LEAF(nodeType);
		params.nodeType = ("file"==nodeType)?1:0;
		PARSEJSON_INT64_LEAF(teamSpaceId);
		params.teamSpaceId = teamSpaceId;
		PARSEJSON_STRING_LEAF(teamSpaceName);
		params.teamSpaceName = teamSpaceName;
		PARSEJSON_INT64_LEAF(groupId);
		params.groupId = groupId;
		PARSEJSON_STRING_LEAF(groupName);
		params.groupName = groupName;
		PARSEJSON_STRING_LEAF(originalRole);
		params.originalRole = originalRole;
		PARSEJSON_STRING_LEAF(currentRole);
		params.currentRole = currentRole;
		PARSEJSON_STRING_LEAF(title);
		params.title = title;
		PARSEJSON_STRING_LEAF(content);
		params.content = content;
		PARSEJSON_INT64_LEAF(announcementId);
		params.announcementId = announcementId;
		if(MT_System==msgNode.type)
		{
			msgNode.id = -msgNode.id;
		}

		msgNode.params = params;
		msgNodes.push_back(msgNode);
	}

	return RT_OK;
}

int32_t JsonParser::parseSysMsgInfo(DataBuffer &writeBuf, MsgList &msgNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<MsgNode> parser(writeBuf);

	PARSE_UINT64_LEAF(totalCount);
	total_cnt = totalCount;

	Json::Value jsonList;
	(void)parser.parseList("announcements", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		MsgNode msgNode;
		msgNode.type = MT_System;
		msgNode.status = MS_Readed;
		PARSEJSON_INT64_LEAF(id);
		msgNode.id = -id;
		PARSEJSON_INT64_LEAF(createdAt);
		msgNode.createdAt = createdAt/1000;

		MsgParams params;
		PARSEJSON_STRING_LEAF(title);
		params.title = title;
		PARSEJSON_STRING_LEAF(content);
		params.content = content;

		msgNode.params = params;
		msgNodes.push_back(msgNode);
	}

	return RT_OK;
}

int32_t JsonParser::parseMsgInfo(DataBuffer &writeBuf, MsgNode &msgNode)
{
	Parser<MsgNode> parser(writeBuf);
	int32_t ret = RT_OK;

	PARSE_INT64_LEAF(id);
	msgNode.id = id;
	PARSE_INT64_LEAF(providerId);
	msgNode.providerId = providerId;
	PARSE_STRING_LEAF(providerUsername);
	msgNode.providerUsername = providerUsername;
	PARSE_STRING_LEAF(providerName);
	msgNode.providerName = providerName;
	PARSE_INT64_LEAF(receiverId);
	msgNode.receiverId = receiverId;
	PARSE_STRING_LEAF(appId);
	msgNode.appId = appId;
	PARSE_STRING_LEAF(type);
	msgNode.type = convertMsgType(type);
	PARSE_STRING_LEAF(status);
	msgNode.status = ("read"==status)?MS_Readed:MS_UnRead;
	PARSE_INT64_LEAF(createdAt);
	msgNode.createdAt = createdAt/1000;
	PARSE_INT64_LEAF(expiredAt);
	msgNode.expiredAt = expiredAt/1000;

	Json::Value jsonObj = Json::nullValue;
	Json::Reader reader;
	std::string jsonStr;
	jsonStr.assign((char *)writeBuf.pBuf);
	if (!reader.parse(jsonStr, jsonObj) || jsonObj.isNull())
	{
		return FAILED_TO_PARSEJSON;
	}
	jsonObj = jsonObj["params"];
	if (jsonObj.isNull())
	{
		return FAILED_TO_PARSEJSON;
	}
	MsgParams params;
	PARSEJSON_INT64_LEAF(nodeId);
	params.nodeId = nodeId;
	PARSEJSON_STRING_LEAF(nodeName);
	params.nodeName = nodeName;
	PARSEJSON_STRING_LEAF(nodeType);
	params.nodeType = ("folder"==nodeType)?0:1;
	PARSEJSON_STRING_LEAF(teamSpaceId);
	params.teamSpaceId = SD::Utility::String::string_to_type<int64_t>(teamSpaceId);
	PARSEJSON_STRING_LEAF(teamSpaceName);
	params.teamSpaceName = teamSpaceName;
	PARSEJSON_INT64_LEAF(groupId);
	params.groupId = groupId;
	PARSEJSON_STRING_LEAF(groupName);
	params.groupName = groupName;
	PARSEJSON_STRING_LEAF(originalRole);
	params.originalRole = originalRole;
	PARSEJSON_STRING_LEAF(currentRole);
	params.currentRole = currentRole;
	PARSEJSON_STRING_LEAF(title);
	params.title = title;
	PARSEJSON_STRING_LEAF(content);
	params.content = content;
	PARSEJSON_STRING_LEAF(announcementId);
	if(MT_System==msgNode.type)
	{
		msgNode.id = -msgNode.id;
	}

	msgNode.params = params;

	return ret;
}

int32_t JsonParser::parseEmailInfoNode(DataBuffer &writeBuf, EmailInfoNode& emailInfoNode)
{
	Parser<EmailInfoNode> parser(writeBuf);
	int ret = RT_OK;

	PARSE_INT64_LEAF(sender);
	emailInfoNode.sender = sender;
	PARSE_STRING_LEAF(source);
	emailInfoNode.source = source;
	PARSE_INT64_LEAF(ownedBy);
	emailInfoNode.ownedBy = ownedBy;
	PARSE_INT64_LEAF(nodeId);
	emailInfoNode.nodeId = nodeId;
	PARSE_STRING_LEAF(subject);
	emailInfoNode.subject = subject;
	PARSE_STRING_LEAF(message);
	emailInfoNode.message = message;

	return RT_OK;
}

int32_t JsonParser::parseGroupNode(DataBuffer &writeBuf, GroupNodeList &groupNodes, int64_t& total_cnt)
{
	int ret = RT_OK;

	Parser<GroupNode> parser(writeBuf);
	Json::Value jsonList;

	PARSE_INT_LEAF(totalCount);
	total_cnt = totalCount;

	parser.parseList("groups", jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];
		GroupNode groupNode;

		PARSEJSON_INT64_LEAF(id);
		groupNode.id(id);
		PARSEJSON_STRING_LEAF(name);
		groupNode.name(name);
		PARSEJSON_STRING_LEAF(description);
		groupNode.description(description);
		PARSEJSON_INT64_LEAF(url);
		groupNode.accountId(url);
		PARSEJSON_INT_LEAF(maxMembers);
		groupNode.maxMembers(maxMembers);
		PARSEJSON_INT64_LEAF(ownedBy);
		groupNode.ownedBy(ownedBy);
		PARSEJSON_INT64_LEAF(createdAt);
		groupNode.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(modifiedAt);
		groupNode.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(createdBy);
		groupNode.createdBy(createdBy);
		PARSEJSON_STRING_LEAF(status);
		groupNode.status(status);
		PARSEJSON_STRING_LEAF(appId);
		groupNode.appId(appId);
		PARSEJSON_STRING_LEAF(type);
		groupNode.type(type);

		groupNodes.push_back(groupNode);
	}
	return RT_OK;
}

int32_t JsonParser::parseGroupUserInfoList(DataBuffer &dataBuf, UserGroupNodeInfoArray& _list, int64_t& total)
{
	Parser<UserGroupNodeInfo> parser(dataBuf);
	int ret = RT_OK;
	Json::Value jsonList;

	PARSE_UINT64_LEAF(totalCount);
	total = totalCount;
	if(totalCount == 0)
	{
		return RT_NOT_IMPLEMENT;
	}

	(void)parser.parseList("memberships",jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		UserGroupNodeInfo _tmp;
		DataBuffer _buf;
		Json::Value jsonObj = jsonList[i];

		//"id":1,"teamId":943,"teamRole":"admin","role":"auther"

		PARSEJSON_INT64_LEAF(id);
		_tmp.id(id);
		PARSEJSON_STRING_LEAF(groupRole);
		_tmp.groupRole(groupRole);

		Json::Value tmp_m = jsonObj["member"];
		JsonToGroupMemberNodeInfo(tmp_m,_tmp.groupInfo_);

		Json::Value tmp_t = jsonObj["group"];
		JsonToGroupNode(tmp_t,_tmp.member_);

		if(_tmp.groupRole().empty())
		{
			_tmp.groupRole(_tmp.groupInfo_.groupRole());
		}

		if(0 == _tmp.groupId())
		{
			_tmp.groupId(_tmp.groupInfo_.groupId());
		}
		
		_list.push_back(_tmp);
	}
	return RT_OK;
}

int32_t JsonParser::JsonToGroupMemberNodeInfo(Json::Value _value,GroupMemberNodeInfo& _tmp)
{
	Json::Value jsonObj = _value;

	PARSEJSON_INT64_LEAF(id);
	_tmp.id(id);
	PARSEJSON_STRING_LEAF(loginName);
	_tmp.loginName(loginName);
	PARSEJSON_STRING_LEAF(userType);
	_tmp.userType(userType);
	PARSEJSON_STRING_LEAF(name);
	_tmp.name(name);
	PARSEJSON_STRING_LEAF(username);
	_tmp.username(username);
	PARSEJSON_STRING_LEAF(groupRole);
	_tmp.groupRole(groupRole);
	PARSEJSON_INT64_LEAF(groupId);
	_tmp.groupId(groupId);
	PARSEJSON_INT64_LEAF(userId);
	_tmp.userId(userId);

	return RT_OK;
}

int32_t JsonParser::JsonToGroupNode(Json::Value _value,GroupNode& _tmp)
{
	Json::Value jsonObj = _value;

	PARSEJSON_INT64_LEAF(id);
	_tmp.id(id);
	PARSEJSON_STRING_LEAF(name);
	_tmp.name(name);
	PARSEJSON_STRING_LEAF(description);
	_tmp.description(description);
	PARSEJSON_STRING_LEAF(status);
	_tmp.status(status);
	PARSEJSON_INT64_LEAF(ownedBy);
	_tmp.ownedBy(ownedBy);
	PARSEJSON_INT64_LEAF(createdBy);
	_tmp.createdBy(createdBy);
	PARSEJSON_INT64_LEAF(createdAt);
	_tmp.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSEJSON_STRING_LEAF(type);
	_tmp.type(type);
	PARSEJSON_STRING_LEAF(appId);
	_tmp.appId(appId);
	PARSEJSON_UINT64_LEAF(accountId);
	_tmp.accountId(accountId);
	PARSEJSON_UINT64_LEAF(modifiedAt);
	_tmp.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSEJSON_INT_LEAF(maxMembers);
	_tmp.maxMembers(maxMembers);

	return RT_OK;
}

int32_t JsonParser::parseGroupNode(DataBuffer &dataBuf,GroupNode& _info)
{
	Parser<GroupNode> parser(dataBuf);
	int ret = RT_OK;

	PARSE_INT64_LEAF(id);
	_info.id(id);
	PARSE_STRING_LEAF(name);
	_info.name(name);
	PARSE_STRING_LEAF(description);
	_info.description(description);
	PARSE_STRING_LEAF(status);
	_info.status(status);
	PARSE_INT64_LEAF(createdBy);
	_info.createdBy(createdBy);
	PARSE_INT64_LEAF(ownedBy);
	_info.ownedBy(ownedBy);
	PARSE_INT64_LEAF(accountId);
	_info.accountId(accountId);
	PARSE_INT64_LEAF(createdAt);
	_info.createdAt(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSE_INT64_LEAF(modifiedAt);
	_info.modifiedAt(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
	PARSE_STRING_LEAF(type);
	_info.type(type);
	PARSE_INT_LEAF(maxMembers);
	_info.maxMembers(maxMembers);

	return ret;
}

int32_t JsonParser::parseGroupMemberInfo(DataBuffer &dataBuf,UserGroupNodeInfo& _info)
{
	Parser<UserGroupNodeInfo> parser(dataBuf);
	int ret = RT_OK;

	PARSE_INT_LEAF(id);
	_info.id(id);
	PARSE_INT_LEAF(groupId);
	_info.groupId(groupId);
	PARSE_STRING_LEAF(groupRole);
	_info.groupRole(groupRole);

	Json::Value tmp_m;
	(void)parser.parseList("member",tmp_m);

	JsonToGroupMemberNodeInfo(tmp_m, _info.groupInfo_);

	Json::Value tmp_t;
	(void)parser.parseList("group",tmp_t);
	JsonToGroupNode(tmp_t, _info.member_);

	return RT_OK;
}

int32_t JsonParser::parseSystemRoleList(DataBuffer &dataBuf,PermissionRoleArray& _info)
{
	int ret = RT_OK;

	PermissionRole tmpPermissionRoleInfoEx;
	std::string tmp_str;
	Json::Value jsonList(Json::arrayValue);
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_STRING_LEAF(name);
		tmpPermissionRoleInfoEx.name(name);
		PARSEJSON_STRING_LEAF(description);
		tmpPermissionRoleInfoEx.description(description);
		PARSEJSON_INT_LEAF(status);
		tmpPermissionRoleInfoEx.status(status);

		JsonToPermissionsInfo(jsonObj["permissions"], tmpPermissionRoleInfoEx.permission_);

		_info.push_back(tmpPermissionRoleInfoEx);
	}

	return RT_OK;
}

int32_t JsonParser::JsonToPermissionsInfo(Json::Value _value, Permission& _tmp)
{
	Json::Value jsonObj = _value;

	PARSEJSON_INT_LEAF(browse);
	_tmp.browse(browse);
	PARSEJSON_INT_LEAF(preview);
	_tmp.preview(preview);
	PARSEJSON_INT_LEAF(download);
	_tmp.download(download);
	PARSEJSON_INT_LEAF(upload);
	_tmp.upload(upload);
	PARSEJSON_INT_LEAF(edit);
	_tmp.edit(edit);
	PARSEJSON_INT_LEAF(Delete);
	_tmp.Delete(Delete);
	PARSEJSON_INT_LEAF(publishLink);
	_tmp.publishLink(publishLink);
	PARSEJSON_INT_LEAF(authorize);
	_tmp.authorize(authorize);

	return RT_OK;
}

int32_t JsonParser::parseAccessNode(DataBuffer &dataBuf, AccesNode& _list)
{
	Parser<AccesNode> parser(dataBuf);
	int ret = RT_OK;

	PARSE_INT64_LEAF(id);
	_list.id(id);
	PARSE_STRING_LEAF(role);
	_list.role(role);

	Json::Value tmp_m;
	(void)parser.parseList("resource",tmp_m);
	JsonToResource(tmp_m, _list.resource_);
	
	Json::Value tmp_u;
	(void)parser.parseList("user",tmp_u);
	JsonToUser(tmp_u, _list.accesUser_);	
	return RT_OK;
}

int32_t JsonParser::JsonToResource(Json::Value _value, AccesNodeResource& _tmp)
{
	Json::Value jsonObj = _value;
	int ret = RT_OK;

	PARSEJSON_INT64_LEAF(ownerId);
	_tmp.ownerId(ownerId);
	PARSEJSON_INT64_LEAF(nodeId);
	_tmp.nodeId(nodeId);
	
	return ret;
}

int32_t JsonParser::JsonToUser(Json::Value _value, AccesNodeUser& _tmp)
{
	Json::Value jsonObj = _value;
	int ret = RT_OK;

	PARSEJSON_INT64_LEAF(id);
	_tmp.id(id);
	PARSEJSON_STRING_LEAF(name);
	_tmp.name(name);
	PARSEJSON_STRING_LEAF(desciption);
	_tmp.desciption(desciption);
	PARSEJSON_STRING_LEAF(type);
	_tmp.type(type);
	PARSEJSON_STRING_LEAF(loginName);
	_tmp.loginName(loginName);

	return ret;
}

int32_t JsonParser::parseAccessNodeList(DataBuffer &dataBuf,int64_t& total, AccesNodeArray& _list)
{
	Parser<AccesNode> parser(dataBuf);
	int ret = RT_OK;
	Json::Value jsonList;

	PARSE_UINT64_LEAF(totalCount);
	total = totalCount;
	if(totalCount == 0)
	{
		return RT_NOT_IMPLEMENT;
	}

	(void)parser.parseList("acls",jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		AccesNode _tmp;
		DataBuffer _buf;
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_INT64_LEAF(id);
		_tmp.id(id);
		PARSEJSON_STRING_LEAF(role);
		_tmp.role(role);

		Json::Value tmp_m = jsonObj["resource"];

		JsonToResource(tmp_m, _tmp.resource_);
	
		Json::Value tmp_t = jsonObj["user"];

		JsonToUser(tmp_t, _tmp.accesUser_);

		_list.push_back(_tmp);
	}
	return RT_OK;
}

int32_t JsonParser::parseGetAccessNode(DataBuffer &dataBuf, AccesNode& _list)
{
	Parser<AccesNode> parser(dataBuf);
	int ret = RT_OK;
	Json::Value jsonList;

	PARSE_UINT64_LEAF(ownerId);
	_list.resource_.ownerId(ownerId);
	PARSE_UINT64_LEAF(nodeId);
	_list.resource_.nodeId(nodeId);

	Json::Value tmp_m;
	(void)parser.parseList("permissions",tmp_m);
	JsonToPermissionsInfo(tmp_m, _list.resource_.permission_);

	return RT_OK;
}

int32_t JsonParser::parseTaskInfo(DataBuffer &writeBuf, std::string &taskId)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["id"].isString())				
	{											
		taskId = jsonObj["id"].asString();			
	}		

	return RT_OK;
}

int32_t JsonParser::parseTaskStatus(DataBuffer &writeBuf, std::string &taskStatus)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["status"].isString())				
	{											
		taskStatus = jsonObj["status"].asString();			
	}		

	return RT_OK;
}

int32_t JsonParser::parseSysRoleInfo(DataBuffer &writeBuf, SysRoleInfoExList &nodes)
{
	int ret = RT_OK;

	SysRoleInfoEx tmpSystemRoleInfoEx;
	std::string tmp_str;
	Json::Value jsonList(Json::arrayValue);
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonObj = jsonList[i];

		PARSEJSON_STRING_LEAF(name);
		tmpSystemRoleInfoEx.name = name;
		PARSEJSON_STRING_LEAF(description);
		tmpSystemRoleInfoEx.description = description;
		PARSEJSON_INT_LEAF(status);
		tmpSystemRoleInfoEx.status = status;

		nodes.push_back(tmpSystemRoleInfoEx);
	}
	return RT_OK;
}

int32_t JsonParser::parseFilePath(DataBuffer &writeBuf, std::vector<int64_t>& parentIds, std::vector<std::string>& parentNames)
{
	std::string tmp_str;
	Json::Value jsonList;
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonList);

	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		parentIds.push_back(jsonList[i]["id"].asInt64());
		parentNames.push_back(jsonList[i]["name"].asString());
	}
	parentIds.push_back(0);
	parentNames.push_back("");

	return RT_OK;
}

int32_t JsonParser::parsePathInfo(DataBuffer &writeBuf, std::map<int64_t, std::wstring>& pathInfo)
{
	std::string tmp_str;
	Json::Value jsonRoot;
	Json::Reader reader; 
	tmp_str.assign((char *)writeBuf.pBuf);
	reader.parse(tmp_str, jsonRoot);

	Json::Value jsonList;
	jsonList = jsonRoot["folders"];
	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		int64_t fileId = jsonList[i]["id"].asInt64();
		int64_t parent = jsonList[i]["parent"].asInt64();
		if(0==parent)
		{
			pathInfo.insert(std::make_pair(fileId, L"/"));
			continue;
		}
		Json::Value jsonPath;
		jsonPath = jsonList[i]["path"];
		if(!jsonPath.isArray())
		{
			return RT_NOT_IMPLEMENT;
		}
		std::string path;
		for(unsigned int j=0; j<jsonPath.size(); ++j)
		{
			path = jsonPath[j]["name"].asString() + "/" + path;
		}
		path = "/" + path;
		pathInfo.insert(std::make_pair(fileId, SD::Utility::String::utf8_to_wstring(path)));
	}

	jsonList = jsonRoot["files"];
	for(unsigned int i=0; i<jsonList.size(); ++i)
	{
		int64_t fileId = jsonList[i]["id"].asInt64();
		int64_t parent = jsonList[i]["parent"].asInt64();
		if(0==parent)
		{
			pathInfo.insert(std::make_pair(fileId, L"/"));
			continue;
		}
		Json::Value jsonPath;
		jsonPath = jsonList[i]["path"];
		if(!jsonPath.isArray())
		{
			return RT_NOT_IMPLEMENT;
		}
		std::string path;
		for(unsigned int j=0; j<jsonPath.size(); ++j)
		{
			path = jsonPath[j]["name"].asString() + "/" + path;
		}
		path = "/" + path;
		pathInfo.insert(std::make_pair(fileId, SD::Utility::String::utf8_to_wstring(path)));
	}

	return RT_OK;
}

int32_t JsonParser::parseDeclarationInfo(DataBuffer& dataBuf, DeclarationInfo& declarationInfo)
{
	Parser<DeclarationInfo> parser(dataBuf);
	int32_t ret;

	PARSE_STRING_LEAF(id);
	declarationInfo.declarationID = id;

	PARSE_STRING_LEAF(appId);
	declarationInfo.appId = appId;

	PARSE_STRING_LEAF(clientType);
	declarationInfo.clientType = clientType;

	PARSE_INT64_LEAF(createAt);
	declarationInfo.createAt = createAt;

	PARSE_STRING_LEAF(declaration);
	declarationInfo.declarationText = declaration;

	return ret;
}

int32_t JsonParser::parseSignDeclaration(DataBuffer& dataBuf, std::string& isSign)
{
	std::string tmp_str;
	Json::Value jsonObj;
	Json::Reader reader; 
	tmp_str.assign((char *)dataBuf.pBuf);
	reader.parse(tmp_str, jsonObj);

	if(jsonObj["issign"].isString())				
	{											
		isSign = jsonObj["issign"].asString();			
	}		

	return RT_OK;
}

int32_t JsonParser::parseBatchPreUploadInfo(DataBuffer &dataBuf, BatchPreUploadResponse& response)
{
	int ret = RT_OK;

	Parser<BatchPreUploadResponse> parser(dataBuf);
	Json::Value uploadUrlList;
	Json::Value uploadedList;
	Json::Value failedList;

	parser.parseList("uploadUrlList", uploadUrlList);
	UploadUrlList urlList;
	for(uint32_t i = 0; i< uploadUrlList.size(); ++i)
	{
		Json::Value jsonObj = uploadUrlList[i];
		UploadUrl uploadUrlObj;

		PARSEJSON_STRING_LEAF(name);
		uploadUrlObj.name = name;
		PARSEJSON_INT64_LEAF(fileId);
		uploadUrlObj.fileId = fileId;
		PARSEJSON_STRING_LEAF(uploadUrl);
		uploadUrlObj.uploadUrl = uploadUrl;
		urlList.push_back(uploadUrlObj);
	}
	response.uploadUrlList = urlList;

	parser.parseList("uploadedList", uploadedList);
	FileList fileList;
	for(uint32_t i = 0; i< uploadedList.size(); ++i)
	{
		Json::Value jsonObj = uploadedList[i];
		FileItem fileItem;

		PARSEJSON_INT64_LEAF(id);
		fileItem.id(id);
		PARSEJSON_INT_LEAF(type);
		fileItem.type((FILE_TYPE)type);
		PARSEJSON_STRING_LEAF(name);
		fileItem.name(name);
		PARSEJSON_INT64_LEAF(size);
		fileItem.size(size);
		PARSEJSON_STRING_LEAF(objectId);
		fileItem.objectId(objectId);
		PARSEJSON_INT_LEAF(versions);
		fileItem.version(versions);
		PARSEJSON_BOOL_LEAF(status);
		fileItem.status(status);
		PARSEJSON_INT64_LEAF(parent);
		fileItem.parent(parent);
		PARSEJSON_INT64_LEAF(ownedBy);
		fileItem.ownerId(ownedBy);
		PARSEJSON_INT64_LEAF(createdBy);
		fileItem.createdBy(createdBy);
		PARSEJSON_INT64_LEAF(createdAt);
		fileItem.createTime(Utility::DateTime(createdAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(modifiedAt);
		fileItem.modifieTime(Utility::DateTime(modifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(contentCreatedAt);
		fileItem.contentCreatedAt(Utility::DateTime(contentCreatedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_INT64_LEAF(contentModifiedAt);
		fileItem.contentModifiedAt(Utility::DateTime(contentModifiedAt, Utility::UtcType::Unix).getWindowsFileTime());
		PARSEJSON_BOOL_LEAF(isShare);
		fileItem.isShare(isShare);
		PARSEJSON_BOOL_LEAF(isSync);
		fileItem.isSync(isSync);
		PARSEJSON_BOOL_LEAF(isEncrypt);
		fileItem.isEncrypt(isEncrypt);
		PARSEJSON_BOOL_LEAF(isSharelink);
		fileItem.isSharelink(isSharelink);

		fileList.push_back(fileItem);
	}
	response.uploadedList = fileList;

	parser.parseList("failedList", failedList);
	FailedList errorList;
	for(uint32_t i = 0; i< failedList.size(); ++i)
	{
		Json::Value jsonObj = failedList[i];
		FailedInfo failedInfo;

		PARSEJSON_STRING_LEAF(name);
		failedInfo.name = name;

		ErrorInfo errorInfo;
		parser.parseLeaf("exception", jsonObj);
		PARSEJSON_STRING_LEAF(type);
		errorInfo.type = type;
		PARSEJSON_STRING_LEAF(code);
		errorInfo.code = code;
		PARSEJSON_STRING_LEAF(message);
		errorInfo.message = message;
		PARSEJSON_STRING_LEAF(requestId);
		errorInfo.requestId = requestId;
		failedInfo.errorInfo = errorInfo;

		errorList.push_back(failedInfo);
	}
	response.failedList = errorList;

	return RT_OK;

}