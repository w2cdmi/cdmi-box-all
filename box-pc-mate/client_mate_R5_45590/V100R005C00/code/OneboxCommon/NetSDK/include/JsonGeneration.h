#ifndef __ONEBOX__JSON_GENERATION__H__
#define __ONEBOX__JSON_GENERATION__H__

#include "OneboxExport.h"
#include <json.h>
#include "UserInfo.h"
#include "FileItem.h"
#include "UploadInfo.h"
#include "ShareNode.h"
#include "ShareLinkNode.h"
#include "ShareUserInfo.h"
#include "PageParam.h"
#include "EmailItem.h"
#include "MsgInfo.h"
#include "RestParam.h"

class ONEBOX_DLL_EXPORT JsonGeneration
{
public:
	static void genLoginInfo(const LoginReqInfo& loginReq, DataBuffer& loginReqBuf);

	static void genNewName(const std::string& newName, DataBuffer& readBuf);

	static int32_t genDestFolder(const int64_t& dest_parent_id, const int64_t& dest_owner_id,
		const bool autorename, DataBuffer& readBuf);

	static int32_t genNewFolder(const std::string& name, const int64_t& parent_id, 
		const int64_t& contentcreatedat, const int64_t& contentmodifiedat, const int32_t& extraType, const bool autoMerge, DataBuffer& readBuf);

	static int32_t genUploadReq(const FileItem& fileItem, const UploadType upload_type,  const std::string& encrypt_key, DataBuffer& readBuf);

	static void genUploadUrl(const std::string& uploadUrl, DataBuffer& readBuf);

	static int32_t genPartList(const PartList& partList, DataBuffer& readBuf);

	static int32_t genShareNodeList(const ShareNodeList& shareNodes, DataBuffer& readBuf);

	static int32_t genLoginName(const std::string& loginName, DataBuffer& readBuf);

	static int32_t genInviteShares(const ShareNodeEx& inviteNode, DataBuffer& readBuf);

	static int32_t genInviteSharesV2(const ShareNodeEx& inviteNode,DataBuffer& readBuf);

	static int32_t genShareLinkNode(const ShareLinkNode& shareLinkNode, DataBuffer& readBuf);

	static int32_t genShareLinkNodeV2(const ShareLinkNode& shareLinkNode, DataBuffer& readBuf);
	
	static int32_t genShareUserInfo(const ShareUserInfo& shareUserInfo, DataBuffer& readBuf);

	static int32_t genListShareParam(const std::string& keyword, const PageParam& pageparam, DataBuffer& readBuf);

	static int32_t genListDomainUserParam(const std::string& keyWord, int32_t limit, DataBuffer& readBuf);

	static int32_t genSendShareLinkByEmailParam(const std::string& linkUrl, EmailList& emails, DataBuffer& readBuf);

	static void genListFolder(const PageParam& pageparam, DataBuffer& readBuf);

	static int32_t genSearch(const PageParam& pageparam, const std::string& name, bool needPath, DataBuffer& readBuf);

	static int32_t genCreateShareLink(const std::string& access, const ShareLinkNodeEx& shareLinkNodeEx, DataBuffer& readBuf);

	static int32_t genEmailNode(const EmailNode& emailnode, DataBuffer& readBuf);

	static int32_t genTeamSpacesParam(const int64_t& userId, const PageParam& pageparam, DataBuffer& readBuf);

	static int32_t genTeamSpacesInfo(const std::string& name, const std::string& desc, const int64_t spaceQuota, const int8_t status, const int32_t maxVersions, DataBuffer& readBuf,int32_t type = 0);

	static int32_t genTeamSpacesMember(const std::string& member_type, const int64_t& member_id, const std::string& teamRole, const std::string& role, DataBuffer& readBuf);

	static int32_t genTeamSpacesMemberInfo(const std::string& teamRole,const std::string& role,DataBuffer& readBuf);

	static int32_t genTeamSpacesListMember(const std::string& keyword, const std::string& teamRole, const PageParam& pageParam, DataBuffer& readBuf);

	static int32_t genListFilesHadShareLink(const int64_t owner_id, const std::string& keyword, const PageParam& pageparam, DataBuffer& readBuf);

	static int32_t genMsgStartId(const int64_t startId, MsgStatus status, DataBuffer& readBuf);

	static int32_t genMsgOffset(const int64_t offset, MsgStatus status, DataBuffer& readBuf);

	static int32_t genSysMsgOffset(const int64_t offset, DataBuffer& readBuf);

	static int32_t genMsgStatus(MsgStatus status, DataBuffer& readBuf);

	static void genSetEmailInfo(const EmailInfoNode& emailInfoNode, DataBuffer& readBuf);

	static void genListGroups(const std::string& keyword, const std::string& type, const PageParam& pageparam, DataBuffer& readBuf);
	
	static int32_t genGroupParam(const PageParam& pageparam, DataBuffer& readBuf,const std::string& keyword,const std::string& type,const std::string& listRole);
	
	static int32_t genGroupInfo(const std::string& name, const std::string& desc,const std::string& status,const std::string& type,DataBuffer& readBuf);
	
	static int32_t genGroupMember(const std::string& member_type, const int64_t& member_id,const std::string& groupRole,DataBuffer& readBuf);
	
	static int32_t genGroupMemberInfo(const std::string& groupRole,DataBuffer& readBuf);

	static int32_t genGroupListMemberInfoParam(const PageParam& pageparam,DataBuffer& readBuf,const std::string& keyword,const std::string& groupRole = "all");

	static int32_t genAddAccessNode(const int64_t& ownerId,const int64_t& id,const int64_t& nodeId,const std::string& type,const std::string& role,DataBuffer& readBuf);

	static int32_t genUpdateAccessNode(const std::string& role,DataBuffer& readBuf);

	static int32_t genListAccessNode(const int64_t& nodeId,const int32_t& offset,const int32_t& limit,DataBuffer& readBuf);

	static void genTaskInfo(int64_t srcOwnerId, const std::list<int64_t>& srcNodeId,
		int64_t destOwnerId, int64_t destFolderId,
		const std::string& type, bool autoRename, DataBuffer& readBuf);

	static int32_t genBatchPreUploadReq(const BatchPreUploadRequest& request, DataBuffer& readBuf);
};

#endif
