#include "JsonGeneration.h"
#include "Util.h"
#include "Utility.h"
#include <assert.h>

using namespace SD;

int32_t JsonGeneration::genLoginInfo(const LoginReqInfo& loginReq, DataBuffer& loginReqBuf)
{
    Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	//reqRoot["appId"] = "OneBox";
	reqRoot["loginName"] = loginReq.loginName;
	reqRoot["password"] = loginReq.password;
	if (!loginReq.domain.empty())
	{
		reqRoot["domain"] = loginReq.domain;
	}
	//reqRoot["deviceType"] = loginReq.clientType;
	//reqRoot["deviceSN"] = loginReq.clientSN;
	//reqRoot["deviceOS"] = loginReq.clientOS;
	//reqRoot["deviceName"] = loginReq.clientName;
	//reqRoot["deviceAgent"] = loginReq.clientVersion;

	std::string loginJsonStr = writer.write(reqRoot);
	string2Buff(loginJsonStr, loginReqBuf);

    return RT_OK;
}

int32_t JsonGeneration::genNewName(const std::string& newName, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["name"] = newName;

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
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
								   const int64_t& contentcreatedat, const int64_t& contentmodifiedat, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["name"] = name;
	reqRoot["parent"] = parent_id;
	reqRoot["contentCreatedAt"] = contentcreatedat;
	reqRoot["contentModifiedAt"] = contentmodifiedat;
		
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

    return RT_OK;
}

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
	if (FileSignature::SHA1 == fileItem.signature().algorithm)
	{
		reqRoot["sha1"] = fileItem.signature().signature;
	}
	else if (FileSignature::MD5 == fileItem.signature().algorithm)
	{
		reqRoot["md5"] = fileItem.signature().signature;
		reqRoot["blockMD5"] = fileItem.signature().blockSignature;
	}
	
	reqRoot["contentCreatedAt"] = Utility::DateTime(fileItem.contentCreatedAt()).getUnixFileTime();
	reqRoot["contentModifiedAt"] = Utility::DateTime(fileItem.contentModifiedAt()).getUnixFileTime();
	reqRoot["encryptKey"] = encrypt_key;
		
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
	if (NULL == readBuf.pBuf)
	{
		return RT_MEMORY_MALLOC_ERROR;
	}

    return RT_OK;
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
		node["modifiedAt"] = it->modifiedAt();
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
		reqRoot["effectiveAt"] = shareLinkNode.effectiveAt();
	}
	if(INVALID_TIME!=shareLinkNode.expireAt())
	{
		reqRoot["expireAt"] = shareLinkNode.expireAt();
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

	reqRoot.append(node);

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListShareParam(const std::string& keyword, const PageParam& pageparam, const OrderParam& orderparam, const TrumbParam& trumbtaram, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	Json::Value node_order(Json::objectValue);
	Json::Value orderby(Json::objectValue);

	reqRoot["limit"] = pageparam.limit;
	reqRoot["keyword"] = keyword;
	reqRoot["offset"] = pageparam.offset;

	reqRoot["order"] = node_order;
	orderby["filed"] = orderparam.field;
	orderby["direction"] = orderparam.direction;
	node_order.append(orderby);

	Json::Value node_thumb(Json::objectValue);
	Json::Value thumbby(Json::objectValue);

	reqRoot["thumbnail"] = node_thumb;
	thumbby["height"] = trumbtaram.height;
	thumbby["width"] = trumbtaram.width;
	node_thumb.append(thumbby);

	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListDomainUserParam(const std::string& keyWord, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);

	reqRoot["type"] = "auto";				//aduser、system、auto
	reqRoot["keyword"] = keyWord;
	reqRoot["offset"] = 0;					//可选，默认0
	reqRoot["limit"] = 20;					//可选，默认100，最大1000

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

int32_t JsonGeneration::genListFolder(const PageParam& pageparam, DataBuffer& readBuf)
{
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	Json::Value node_order(Json::arrayValue);
	Json::Value orderby(Json::objectValue);

	reqRoot["offset"] = pageparam.offset;
	reqRoot["limit"] = pageparam.limit;
	reqRoot["order"] = node_order;
	/*
	for(ParamOrderList::const_iterator it = pageparam.orderList.begin();
		it != pageparam.orderList.end(); ++it)
	{	
		orderby["filed"] = it->field;
		orderby["direction"] = it->direction;
	}
	*/
	node_order.append(orderby);

	Json::Value node_thumb(Json::arrayValue);
	Json::Value thumbby(Json::objectValue);
	reqRoot["thumbnail"] = node_thumb;
	for(ParamTrumbList::const_iterator it = pageparam.trumbList.begin();
		it != pageparam.trumbList.end(); ++it)
	{
		thumbby["height"] = it->height;
		thumbby["width"] = it->width;
	}
	node_thumb.append(thumbby);

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
	reqRoot["effectiveAt"] = shareLinkNodeEx.effectiveAt();
	reqRoot["expireAt"] = shareLinkNodeEx.expireAt();

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
	params["value"] = urlEncode(emailnode.email_param.message);
	node_params.append(params);

	params["name"] = "sender";
	params["value"] = emailnode.email_param.sender;
	node_params.append(params);

	params["name"] = "nodeName";
	params["value"] = urlEncode(emailnode.email_param.nodename);
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

int32_t JsonGeneration::genListTeamspacesParam(const std::string& keyword, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value order(Json::objectValue);
	order["field"] = orderField;
	order["direction"] = orderDirection;

	Json::Value orderRoot(Json::arrayValue);
	orderRoot.append(order);

	Json::Value root(Json::objectValue);
	root["limit"] = limit;
	root["offset"] = offset;
	root["order"] = orderRoot;
	if (!keyword.empty())
	{
		root["keyword"] = keyword;
	}

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListTeamspaceByUserParam(const int64_t userId, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value order(Json::objectValue);
	order["field"] = orderField;
	order["direction"] = orderDirection;

	Json::Value orderRoot(Json::arrayValue);
	orderRoot.append(order);

	Json::Value root(Json::objectValue);
	root["limit"] = limit;
	root["offset"] = offset;
	root["order"] = orderRoot;
	root["userId"] = userId;

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genCreateOrUpdateTeamspaceParam(const std::string& name, const std::string& description, const int64_t spaceQuota, const TeamspaceStatus status, const int32_t maxVersion, const int32_t maxMembers, const int32_t regionId, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value root(Json::objectValue);
	root["name"] = name;
	root["description"] = description;
	root["spaceQuota"] = spaceQuota;
	root["status"] = (int32_t)status;
	root["maxVersion"] = maxVersion;
	root["maxMembers"] = maxMembers;
	root["regionId"] = regionId;

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genAddTeamspaceMemberParam(const TeamspaceRoleType teamRole, const std::string& role, const int64_t memberId, const TeamspaceMemberType memberType, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value member(Json::objectValue);
	member["id"] = memberId;
	member["type"] = memberType;

	Json::Value root(Json::objectValue);
	root["member"] = member;
	root["teamRole"] = convertTeamspaceRoleType(teamRole);
	root["role"] = role;

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genUpdateTeamspaceMemberParam(const TeamspaceRoleType teamRole, const std::string& role, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value root(Json::objectValue);
	root["teamRole"] = convertTeamspaceRoleType(teamRole);
	root["role"] = role;

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}

int32_t JsonGeneration::genListTeamspacesMembersParam(const std::string& keyword, const TeamspaceRoleType roleType, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf)
{
	Json::StyledWriter writer;

	Json::Value order(Json::objectValue);
	order["field"] = orderField;
	order["direction"] = orderDirection;

	Json::Value orderRoot(Json::arrayValue);
	orderRoot.append(order);

	Json::Value root(Json::objectValue);
	root["limit"] = limit;
	root["offset"] = offset;
	root["order"] = orderRoot;
	root["teamRole"] = convertTeamspaceRoleType(roleType);
	if (!keyword.empty())
	{
		root["keyword"] = keyword;
	}

	std::string renameJsonStr = writer.write(root);
	string2Buff(renameJsonStr, readBuf);

	return RT_OK;
}
