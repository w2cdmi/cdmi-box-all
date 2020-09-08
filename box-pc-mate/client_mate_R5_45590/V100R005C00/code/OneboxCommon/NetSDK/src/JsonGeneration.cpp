#include "JsonGeneration.h"
#include "Util.h"
#include "Utility.h"
#include <assert.h>
#include <boost/algorithm/string.hpp>

using namespace SD;

/*****************************************************************************************
Function Name : genLoginInfo
Description   : 构造登录信息json对象
Input         : loginReq		登陆信息
Output        : loginJsonStr	登陆信息json字符串
				"login_name":{login_name}, 
				"password":{password}, 
				"client_type":{client_type} 
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
void JsonGeneration::genLoginInfo(const LoginReqInfo& loginReq, DataBuffer& loginReqBuf)
{
    Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["loginName"] = loginReq.loginName;
	reqRoot["password"] = loginReq.password;
	if(!loginReq.domain.empty())
	{
		reqRoot["domain"] = loginReq.domain;
	}

	std::string loginJsonStr = writer.write(reqRoot);
	string2Buff(loginJsonStr, loginReqBuf);
}

void JsonGeneration::genNewName(const std::string& newName, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["name"] = newName;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
}

int32_t JsonGeneration::genDestFolder(const int64_t& dest_parent_id, const int64_t& dest_owner_id,
									  const bool autorename, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	
	reqRoot["destParent"] = dest_parent_id;
	reqRoot["destOwnerId"] = dest_owner_id;
	reqRoot["autoRename"] = autorename;
			
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

int32_t JsonGeneration::genNewFolder(const std::string& name, const int64_t& parent_id, 
									 const int64_t& contentcreatedat, const int64_t& contentmodifiedat, const int32_t& extraType, const bool autoMerge, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["name"] = name;
	reqRoot["parent"] = parent_id;
	reqRoot["contentCreatedAt"] = Utility::DateTime(contentcreatedat, Utility::UtcType::Windows).getUnixFileTime();
	reqRoot["contentModifiedAt"] = Utility::DateTime(contentmodifiedat, Utility::UtcType::Windows).getUnixFileTime();
	if(0 != extraType)
	{
		switch(extraType)
		{
		case 1:
			reqRoot["extraType"] = "computer";
			break;
		case 2:
			reqRoot["extraType"] = "disk";
			break;
		default:
			break;
		}
	}
	reqRoot["autoMerge"] = autoMerge;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

/*
upload_type	string	文件上传类型，包含
"sigle":一次性上传整个文件内容，
"multi-part":文件按多个分片上传文件	必选
parent	string	父文件夹ID或者文件ID
当为文件ID时是文件新增版本。	必选
name	string	文件名称	必选
size	int32_t	文件空间大小	必选
sha1	string	文件SHA1	必选
content_created_at	timestamp	客户端文件夹创建时间	可选
content_modified_at	timestamp	客户端文件夹更新时间	可选
is_encrypt	boolean	文件是否被加密	可选
encrypt_key	string	密钥被加密后的字符串，需要和is_encrypt配合使用	可选
*/
int32_t JsonGeneration::genUploadReq(const FileItem& fileItem, 
								   const UploadType upload_type,  
								   const std::string& encrypt_key, 
								   DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	//reqRoot["upload_type"] = (upload_type==Upload_Sigle)?"sigle":"multi-part";
	reqRoot["parent"] = fileItem.parent();
	reqRoot["name"] = fileItem.name();
	reqRoot["size"] = fileItem.size();
	if (Fingerprint::SHA1 == fileItem.fingerprint().algorithm)
	{
		reqRoot["sha1"] = fileItem.fingerprint().fingerprint;
	}
	else if (Fingerprint::MD5 == fileItem.fingerprint().algorithm)
	{
		reqRoot["md5"] = fileItem.fingerprint().fingerprint;
		reqRoot["blockMD5"] = fileItem.fingerprint().blockFingerprint;
	}
	
	reqRoot["contentCreatedAt"] = Utility::DateTime(fileItem.contentCreatedAt(), Utility::UtcType::Windows).getUnixFileTime();
	reqRoot["contentModifiedAt"] = Utility::DateTime(fileItem.contentModifiedAt(), Utility::UtcType::Windows).getUnixFileTime();
	reqRoot["encryptKey"] = encrypt_key;
		
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
	if (NULL == readBuf.pBuf)
	{
		return RT_MEMORY_MALLOC_ERROR;
	}

    return RT_OK;
}

void JsonGeneration::genUploadUrl(const std::string& uploadUrl, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
    Json::Value reqRoot(Json::objectValue);

	reqRoot["uploadUrl"] = uploadUrl;

    std::string renameJsonStr = writer.write(reqRoot);
    string2Buff(renameJsonStr, readBuf);
}

int32_t JsonGeneration::genPartList(const PartList& partList, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	Json::Value parts(Json::arrayValue);
	for(PartList::const_iterator it = partList.begin();
		it != partList.end(); ++it)
	{
		Json::Value node(Json::objectValue);
		node["partId"] = *it;
		parts.append(node);
	}
	reqRoot["parts"] = parts;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

int32_t JsonGeneration::genShareNodeList(const ShareNodeList& shareNodes, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::arrayValue);
	for(ShareNodeList::const_iterator it = shareNodes.begin();
		it != shareNodes.end(); ++it)
	{
		Json::Value node(Json::objectValue);
		node["id"] = it->id();
		node["type"] = it->type();
		node["ownerId"] = it->ownerId();
		node["ownerName"] = it->ownerName();
		node["sharedUserId"] = it->sharedUserId();
		node["sharedUserType"] = it->sharedUserType();
		node["sharedUserName"] = it->sharedUserName();
		node["sharedUserLoginName"] = it->sharedUserLoginName();
		node["sharedUserDescription"] = it->sharedUserDescription();
		node["inodeId"] = it->inodeId();
		node["name"] = it->name();
		node["modifiedAt"] = Utility::DateTime(it->modifiedAt(), Utility::UtcType::Windows).getUnixFileTime();
		node["modifiedBy"] = it->modifiedBy();
		node["roleName"] = it->roleName();
		node["status"] = it->status();
		node["size"] = it->size();
		reqRoot.append(node);
	}

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genLoginName(const std::string& loginName, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
    Json::Value reqRoot(Json::objectValue);

	reqRoot["loginName"] = loginName;

    std::string renameJsonStr = writer.write(reqRoot);
    string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

int32_t JsonGeneration::genInviteShares(const ShareNodeEx& inviteNode,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	Json::Value node(Json::objectValue);
	node["id"] = inviteNode.sharedUserId();
	node["type"] = inviteNode.sharedUserType();
	reqRoot["sharedUser"] = node;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genInviteSharesV2(const ShareNodeEx& inviteNode,DataBuffer& readBuf)
{
    Json::StyledWriter writer;
    Json::Value reqRoot(Json::objectValue);

    Json::Value node(Json::objectValue);
    node["id"] = inviteNode.sharedUserId();
	node["type"] = inviteNode.sharedUserType();
	reqRoot["sharedUser"] = node;
	reqRoot["roleName"] = inviteNode.roleName();

    std::string renameJsonStr = writer.write(reqRoot);
    string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

int32_t JsonGeneration::genShareLinkNode(const ShareLinkNode& shareLinkNode, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot;

	//node["id"] = shareLinkNode.id();
	//node["iNodeId"] = shareLinkNode.iNodeId();
	//node["ownedBy"] = shareLinkNode.ownedBy();
	//node["url"] = shareLinkNode.url();
	reqRoot["plainAccessCode"] = shareLinkNode.plainAccessCode();
	if(INVALID_TIME!=shareLinkNode.effectiveAt())
	{
		reqRoot["effectiveAt"] = Utility::DateTime(shareLinkNode.effectiveAt(), Utility::UtcType::Windows).getUnixFileTime();
	}
	if(INVALID_TIME!=shareLinkNode.expireAt())
	{
		reqRoot["expireAt"] = Utility::DateTime(shareLinkNode.expireAt(), Utility::UtcType::Windows).getUnixFileTime();
	}
	
	//reqRoot["effectiveAt"] = shareLinkNode.effectiveAt();
	//reqRoot["expireAt"] = shareLinkNode.expireAt();
	//node["createdAt"] = shareLinkNode.createdAt();
	//node["modifiedAt"] = shareLinkNode.modifiedAt();
	//node["createdBy"] = shareLinkNode.createdBy();
	//node["modifiedBy"] = shareLinkNode.modifiedBy();

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genShareLinkNodeV2(const ShareLinkNode& shareLinkNode, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot;

	Json::Value nodeIdentity(Json::arrayValue);
	Json::Value identity(Json::objectValue);

	//node["id"] = shareLinkNode.id();
	//node["iNodeId"] = shareLinkNode.iNodeId();
	//node["ownedBy"] = shareLinkNode.ownedBy();
	//node["url"] = shareLinkNode.url();
	reqRoot["plainAccessCode"] = shareLinkNode.plainAccessCode();
	if(INVALID_TIME!=shareLinkNode.effectiveAt())
	{
		reqRoot["effectiveAt"] = Utility::DateTime(shareLinkNode.effectiveAt(), Utility::UtcType::Windows).getUnixFileTime();
	}
	else
	{
		reqRoot["effectiveAt"] = 0;
	}
	if(INVALID_TIME!=shareLinkNode.expireAt())
	{
		reqRoot["expireAt"] = Utility::DateTime(shareLinkNode.expireAt(), Utility::UtcType::Windows).getUnixFileTime();
	}
	else
	{
		reqRoot["expireAt"] = 0;
	}
	
	reqRoot["role"] = shareLinkNode.role();
	reqRoot["accessCodeMode"] = shareLinkNode.accesCodeMode();
	for(IdentityList::const_iterator it = shareLinkNode.identity_.begin();
		it != shareLinkNode.identity_.end(); ++it)
	{
		identity["identity"] = *it;
		nodeIdentity.append(identity);
	}
	reqRoot["identities"] = nodeIdentity;
	//reqRoot["effectiveAt"] = shareLinkNode.effectiveAt();
	//reqRoot["expireAt"] = shareLinkNode.expireAt();
	//node["createdAt"] = shareLinkNode.createdAt();
	//node["modifiedAt"] = shareLinkNode.modifiedAt();
	//node["createdBy"] = shareLinkNode.createdBy();
	//node["modifiedBy"] = shareLinkNode.modifiedBy();

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genShareUserInfo(const ShareUserInfo& shareUserInfo, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::arrayValue);
	Json::Value node(Json::objectValue);

	node["department"] = shareUserInfo.department();
	node["domain"] = shareUserInfo.domain();
	node["email"] = shareUserInfo.email();
	node["id"] = shareUserInfo.id();
	node["label"] = shareUserInfo.label();
	node["loginName"] = shareUserInfo.loginName();
	node["name"] = shareUserInfo.name();
	node["objectSid"] = shareUserInfo.objectSid();
	node["recycleDays"] = shareUserInfo.recycleDays();
	node["regionId"] = shareUserInfo.regionId();
	node["spaceQuota"] = shareUserInfo.spaceQuota();
	node["spaceUsed"] = shareUserInfo.spaceUsed();
	node["status"] = shareUserInfo.status();
	node["type"] = shareUserInfo.type();
	node["accountId"] = shareUserInfo.accountId();

	reqRoot.append(node);

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListShareParam(const std::string& keyword, const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	
	reqRoot["limit"] = pageparam.limit;
	reqRoot["keyword"] = boost::algorithm::trim_copy(keyword);
	reqRoot["offset"] = pageparam.offset;

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	/*
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby); 
	}
	*/
	reqRoot["order"] = node_order;

	Json::Value node_thumb(Json::arrayValue);
	Json::Value thumbby(Json::objectValue);
	for(ParamTrumbList::const_iterator it = pageparam.trumbList.begin();
		it != pageparam.trumbList.end(); ++it)
	{
		thumbby["height"] = it->height;
		thumbby["width"] = it->width;
		node_thumb.append(thumbby);
	}
	reqRoot["thumbnail"] = node_thumb;
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListDomainUserParam(const std::string& keyWord, int32_t limit, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["type"] = "auto";				//aduser、system、auto
	reqRoot["keyword"] = boost::algorithm::trim_copy(keyWord);
	reqRoot["offset"] = 0;					//可选，默认0
	reqRoot["limit"] = limit;				//可选，默认100，最大1000

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genSendShareLinkByEmailParam(const std::string& linkUrl, EmailList& emails, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	std::string strTmpEmails = "";
	for (EmailList::iterator it = emails.begin(); it != emails.end(); ++it)
	{
		strTmpEmails += *it + ";";
	}

	reqRoot["linkUrl"] = linkUrl;
	reqRoot["emails"] = strTmpEmails;


	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

void JsonGeneration::genListFolder(const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);

	reqRoot["offset"] = pageparam.offset;
	reqRoot["limit"] = pageparam.limit;
	reqRoot["withExtraType"] = "true";

	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby);
	}	
	if(!node_order.empty())
	{
		reqRoot["order"] = node_order;
	}

	Json::Value node_thumb(Json::arrayValue);
	Json::Value thumbby(Json::objectValue);
	reqRoot["thumbnail"] = node_thumb;
	for(ParamTrumbList::const_iterator it = pageparam.trumbList.begin();
		it != pageparam.trumbList.end(); ++it)
	{
		thumbby["height"] = it->height;
		thumbby["width"] = it->width;
		node_thumb.append(thumbby);
	}
	if(!node_thumb.empty())
	{
		reqRoot["thumbnail"] = node_thumb;
	}

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
}

int32_t JsonGeneration::genSearch(const PageParam& pageparam, const std::string& name, bool needPath, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);

	reqRoot["offset"] = pageparam.offset;
	reqRoot["limit"] = pageparam.limit;
	reqRoot["name"] = boost::algorithm::trim_copy(name);
	if(needPath)
	{
		reqRoot["withPath"] = needPath;
	}
	
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby);
	}	

	if(!node_order.empty())
	{
		reqRoot["order"] = node_order;
	}

	Json::Value node_thumb(Json::arrayValue);
	Json::Value thumbby(Json::objectValue);

	for(ParamTrumbList::const_iterator it = pageparam.trumbList.begin();
		it != pageparam.trumbList.end(); ++it)
	{
		thumbby["height"] = it->height;
		thumbby["width"] = it->width;
		node_thumb.append(thumbby);
	}

	if(!node_thumb.empty())
	{
		reqRoot["thumbnail"] = node_thumb;
	}
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genCreateShareLink(const std::string& access, const ShareLinkNodeEx& shareLinkNodeEx, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["access"] = access;
	reqRoot["plainAccessCode"] = shareLinkNodeEx.plainAccessCode();
	reqRoot["effectiveAt"] = Utility::DateTime(shareLinkNodeEx.effectiveAt(), Utility::UtcType::Windows).getUnixFileTime();
	reqRoot["expireAt"] = Utility::DateTime(shareLinkNodeEx.expireAt(), Utility::UtcType::Windows).getUnixFileTime();

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genEmailNode(const EmailNode& emailnode, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	Json::Value node_mailto(Json::arrayValue);
	Json::Value mailto(Json::objectValue);
	//Json::Value node_copyto(Json::arrayValue);
	//Json::Value copyto(Json::objectValue);
	Json::Value node_params(Json::arrayValue);
	Json::Value params(Json::objectValue);

	reqRoot["type"] = emailnode.type;

	mailto["email"] = emailnode.mailto;
	node_mailto.append(mailto);
	reqRoot["mailTo"] = node_mailto;
	
	//copyto["email"] = emailnode.copyto;
	//node_copyto.append(copyto);
	//reqRoot["copyTo"] = node_copyto;
	
	params["name"] = "message";
	params["value"] = emailnode.email_param.message;
	node_params.append(params);

	params["name"] = "sender";
	params["value"] = emailnode.email_param.sender;
	node_params.append(params);

	params["name"] = "nodeName";
	params["value"] = emailnode.email_param.nodename;
	node_params.append(params);

	if(emailnode.type == "link")
	{
		if(!emailnode.email_param.plainaccesscode.empty())
		{
			params["name"] = "plainAccessCode";
			params["value"] = emailnode.email_param.plainaccesscode;
			node_params.append(params);
		}
		if(!emailnode.email_param.linkurl.empty())
		{
			params["name"] = "linkUrl";
			params["value"] = emailnode.email_param.linkurl;
			node_params.append(params);
		}
		if(0!=emailnode.email_param.start)
		{
			params["name"] = "start";
			params["value"] = emailnode.email_param.start;
			node_params.append(params);
		}
		if(0!=emailnode.email_param.end)
		{
			params["name"] = "end";
			params["value"] = emailnode.email_param.end;
			node_params.append(params);
		}
	}
	else if(emailnode.type == "share")
	{
		params["name"] = "type";
		params["value"] = emailnode.email_param.type;
		node_params.append(params);

		params["name"] = "ownerId";
		params["value"] = emailnode.email_param.ownerid;
		node_params.append(params);

		params["name"] = "nodeId";
		params["value"] = emailnode.email_param.nodeid;
		node_params.append(params);

		params["name"] = "inodeId";
		params["value"] = emailnode.email_param.nodeid;
		node_params.append(params);
	}

	reqRoot["params"] = node_params;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;

}

int32_t JsonGeneration::genTeamSpacesParam(const int64_t& userId, const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["userId"] = userId;
	reqRoot["limit"] = pageparam.limit;
	reqRoot["offset"] = pageparam.offset;

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	/*
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby); 
	}
	*/
	reqRoot["order"] = node_order;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genTeamSpacesInfo(const std::string& name, const std::string& desc, const int64_t spaceQuota, const int8_t status, const int32_t maxVersions, DataBuffer& readBuf,int32_t type)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);
	//Json::Value root(Json::arrayValue);

	node["name"] = name;
	node["description"] = desc;
	if (spaceQuota == -1)
	{
		node["spaceQuota"] = spaceQuota;
	}
	node["status"] = status;

	if(type == 0)
	{
		node["maxVersions"] = maxVersions;
	}

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genTeamSpacesMember(const std::string& member_type, const int64_t& member_id, const std::string& teamRole, const std::string& role, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);
	Json::Value member(Json::objectValue);

	member["id"] = member_id;
	member["type"] = member_type;
	node["teamRole"] = teamRole;
	node["role"] = role;
	node["member"] = member;

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genTeamSpacesMemberInfo(const std::string& teamRole,const std::string& role,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);

	node["teamRole"] = teamRole;
	node["role"] = role;

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genTeamSpacesListMember(const std::string& keyword, const std::string& teamRole, const PageParam& pageParam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);
	Json::Value order(Json::objectValue);
	Json::Value tmp(Json::arrayValue);
	/*
	for(ParamOrderList::const_iterator it = pageParam.orderList.begin();
		it != pageParam.orderList.end(); ++it)
	{
		order["field"] = it->field;
		order["direction"] = it->direction;
		tmp.append(order);
	}
	*/
	node["teamRole"] = teamRole;

	node["keyword"] = boost::algorithm::trim_copy(keyword);
	node["limit"] = pageParam.limit;
	node["offset"] = pageParam.offset;
	node["order"] = tmp;

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListFilesHadShareLink(const int64_t owner_id, const std::string& keyword, const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["ownedBy"] = owner_id;
	reqRoot["limit"] = pageparam.limit;
	reqRoot["offset"] = pageparam.offset;
	//reqRoot["keyword"] = boost::algorithm::trim_copy(keyword);	//TODO 等待服务器添加该字段

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	/*
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby); 
	}
	*/
	reqRoot["order"] = node_order;

	Json::Value node_thumb(Json::arrayValue);
	Json::Value thumbby(Json::objectValue);
	for(ParamTrumbList::const_iterator it = pageparam.trumbList.begin();
		it != pageparam.trumbList.end(); ++it)
	{
		thumbby["height"] = it->height;
		thumbby["width"] = it->width;
		node_thumb.append(thumbby);
	}
	reqRoot["thumbnail"] = node_thumb;
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
	return RT_OK;
}

int32_t JsonGeneration::genMsgStartId(const int64_t startId, MsgStatus status, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	std::string statusStr;
	if(MS_All==status)
	{
		statusStr = "all";
	}
	else if(MS_UnRead==status)
	{
		statusStr = "unread";
	}
	else if(MS_Readed==status)
	{
		statusStr = "read";
	}

	reqRoot["status"] = statusStr;
	reqRoot["startId"] = startId;
	reqRoot["limit"] = MSG_PAGE_LIMIT;

	std::string jsonStr = writer.write(reqRoot);
	string2Buff(jsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genMsgOffset(const int64_t offset, MsgStatus status, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	std::string statusStr;
	if(MS_All==status)
	{
		statusStr = "all";
	}
	else if(MS_UnRead==status)
	{
		statusStr = "unread";
	}
	else if(MS_Readed==status)
	{
		statusStr = "read";
	}

	reqRoot["status"] = statusStr;
	reqRoot["offset"] = offset;
	reqRoot["limit"] = MSG_PAGE_LIMIT;

	std::string jsonStr = writer.write(reqRoot);
	string2Buff(jsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genSysMsgOffset(const int64_t offset, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["offset"] = offset;
	reqRoot["limit"] = MSG_PAGE_LIMIT;

	std::string jsonStr = writer.write(reqRoot);
	string2Buff(jsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genMsgStatus(MsgStatus status, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	std::string statusStr;
	if(MS_UnRead==status)
	{
		statusStr = "unread";
	}
	else if(MS_Readed==status)
	{
		statusStr = "read";
	}
	reqRoot["status"] = statusStr;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

void JsonGeneration::genSetEmailInfo(const EmailInfoNode& emailInfoNode, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["source"] = emailInfoNode.source;
	reqRoot["subject"] = emailInfoNode.subject;
	reqRoot["message"] = emailInfoNode.message;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
}

void JsonGeneration::genListGroups(const std::string& keyword, const std::string& type, const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["limit"] = pageparam.limit;
	reqRoot["offset"] = pageparam.offset;
	reqRoot["keyword"] = boost::algorithm::trim_copy(keyword);
	reqRoot["type"] = type;

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["field"] = it->field;
		orderby["direction"] = it->direction;
		node_order.append(orderby); 
	}
	
	reqRoot["order"] = node_order;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
}

int32_t JsonGeneration::genGroupParam(const PageParam& pageparam,
									  DataBuffer& readBuf,
									  const std::string& keyword,
									  const std::string& type,
									  const std::string& listRole)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["limit"]	= pageparam.limit;
	reqRoot["offset"]	= pageparam.offset;
	reqRoot["type"]		= type;
	reqRoot["listRole"]	= listRole;
	reqRoot["keyword"]	= keyword;

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["filed"]		= it->field;
		orderby["direction"]	= it->direction;
		node_order.append(orderby); 
	}
	reqRoot["order"] = node_order;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genGroupListMemberInfoParam(const PageParam& pageparam,
									  DataBuffer& readBuf,
									  const std::string& keyword,
									  const std::string& groupRole)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["limit"]		= pageparam.limit;
	reqRoot["offset"]		= pageparam.offset;
	reqRoot["groupRole"]	= groupRole;
	reqRoot["keyword"]		= keyword;

	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["filed"]		= it->field;
		orderby["direction"]	= it->direction;
		node_order.append(orderby); 
	}
	reqRoot["order"] = node_order;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genGroupInfo(const std::string& name, 
									 const std::string& desc, 
									 const std::string& status,
									 const std::string& type,
									 DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);
	//Json::Value root(Json::arrayValue);

	node["name"]		= name;
	node["description"] = desc;
	
	if(!status.empty())
	{
		node["status"]		= status;
	}
	
	if(!type.empty())
	{
		node["type"]		= type;
	}

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genGroupMember(const std::string& member_type, 
									   const int64_t& member_id,
									   const std::string& groupRole,
									   DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);
	Json::Value member(Json::objectValue);

	member["userId"]		= member_id;
	member["userType"]		= member_type;
	node["groupRole"]	= groupRole;
	node["member"]		= member;

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genGroupMemberInfo(const std::string& groupRole,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value node(Json::objectValue);

	node["groupRole"] = groupRole;

	std::string renameJsonStr = writer.write(node);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genAddAccessNode(const int64_t& ownerId,const int64_t& id,const int64_t& nodeId,const std::string& type,const std::string& role,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["role"]			= role;

	Json::Value node_resource(Json::arrayValue);
	Json::Value resource(Json::objectValue);

	resource["ownerId"]	= ownerId;
	resource["nodeId"]	= nodeId;
	node_resource.append(resource); 

	reqRoot["resource"] = node_resource;

	Json::Value node_user(Json::arrayValue);
	Json::Value user(Json::objectValue);

	user["id"]		= id;
	user["type"]	= type;
	node_user.append(user); 

	reqRoot["user"] = node_user;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genUpdateAccessNode(const std::string& role,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["role"]			= role;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListAccessNode(const int64_t& nodeId,const int32_t& offset,const int32_t& limit,DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["limit"]		= limit;
	reqRoot["offset"]		= offset;
	reqRoot["nodeId"]		= nodeId;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}


void JsonGeneration::genTaskInfo(int64_t srcOwnerId, const std::list<int64_t>& srcNodeId,
									int64_t destOwnerId, int64_t destFolderId,
									const std::string& type, bool autoRename, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["type"] = type;
	reqRoot["destFolderId"] = destFolderId;
	reqRoot["destOwnerId"] = destOwnerId;
	reqRoot["srcOwnerId"] = srcOwnerId;

	Json::Value srcNode(Json::arrayValue);
	Json::Value srcId(Json::objectValue);
	
	for(std::list<int64_t>::const_iterator it = srcNodeId.begin();
		it != srcNodeId.end(); ++it)
	{	
		srcId["srcNodeId"] = *it;
		srcNode.append(srcId); 
	}
	
	reqRoot["srcNodeList"] = srcNode;
	reqRoot["autoRename"] = autoRename;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
}

int32_t JsonGeneration::genBatchPreUploadReq(const BatchPreUploadRequest& request, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["tokenTimeout"] = request.tokenTimeout;
	reqRoot["parent"] = request.parent;

	Json::Value fileList(Json::arrayValue);
	for(FilePreUploadInfoList::const_iterator it = request.fileList.begin(); it != request.fileList.end(); ++it)
	{
		Json::Value file(Json::objectValue);

		file["name"] = it->name;
		file["size"] = it->size;
		if(!(it->md5.empty()))
		{
			file["md5"] = it->md5;
		}
		if(!(it->blockMD5.empty()))
		{
			file["blockMD5"] = it->blockMD5;
		}
		if(it->contentCreatedAt > 0)
		{
			file["contentCreatedAt"] = it->contentCreatedAt;
		}
		if(it->contentModifiedAt > 0)
		{
			file["contentModifiedAt"] = it->contentModifiedAt;
		}
		if(!(it->encryptKey.empty()))
		{
			file["encryptKey"] = it->encryptKey;
		}
		fileList.append(file);
	}
	reqRoot["files"] = fileList;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
	if (NULL == readBuf.pBuf)
	{
		return RT_MEMORY_MALLOC_ERROR;
	}

	return RT_OK;

}

