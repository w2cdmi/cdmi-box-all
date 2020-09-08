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
#include "TeamspaceInfo.h"

class ONEBOX_DLL_EXPORT JsonGeneration
{
public:
	static int32_t genLoginInfo(const LoginReqInfo& loginReq, DataBuffer& loginReqBuf);

	static int32_t genNewName(const std::string& newName, DataBuffer& readBuf);

	static int32_t genDestFolder(const int64_t& dest_parent_id, const int64_t& dest_owner_id,
		const bool autorename, DataBuffer& readBuf);

	static int32_t genNewFolder(const std::string& name, const int64_t& parent_id, 
		const int64_t& contentcreatedat, const int64_t& contentmodifiedat, DataBuffer& readBuf);

	static int32_t genUploadReq(const FileItem& fileItem, const UploadType upload_type,  const std::string& encrypt_key, DataBuffer& readBuf);

	static int32_t genPartList(const PartList& partList, DataBuffer& readBuf);

	static int32_t genShareNodeList(const ShareNodeList& shareNodes, DataBuffer& readBuf);

	static int32_t genLoginName(const std::string& loginName, DataBuffer& readBuf);

	static int32_t genInviteShares(const ShareNodeEx& inviteNode, DataBuffer& readBuf);

	static int32_t genShareLinkNode(const ShareLinkNode& shareLinkNode, DataBuffer& readBuf);

	static int32_t genShareUserInfo(const ShareUserInfo& shareUserInfo, DataBuffer& readBuf);

	static int32_t genListShareParam(const std::string& keyword, const PageParam& pageparam, const OrderParam& orderparam, const TrumbParam& trumbtaram, DataBuffer& readBuf);

	static int32_t genListDomainUserParam(const std::string& keyWord, DataBuffer& readBuf);

	static int32_t genSendShareLinkByEmailParam(const std::string& linkUrl, EmailList& emails, DataBuffer& readBuf);

	static int32_t genListFolder(const PageParam& pageparam, DataBuffer& readBuf);

	static int32_t genCreateShareLink(const std::string& access, const ShareLinkNodeEx& shareLinkNodeEx, DataBuffer& readBuf);

	static int32_t genEmailNode(const EmailNode& emailnode, DataBuffer& readBuf);

	static int32_t genListTeamspacesParam(const std::string& keyword, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf);

	static int32_t genListTeamspaceByUserParam(const int64_t userId, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf);
	
	static int32_t genCreateOrUpdateTeamspaceParam(const std::string& name, const std::string& description, const int64_t spaceQuota, const TeamspaceStatus status, const int32_t maxVersion, const int32_t maxMembers, const int32_t regionId, DataBuffer& readBuf);

	static int32_t genAddTeamspaceMemberParam(const TeamspaceRoleType teamRole, const std::string& role, const int64_t memberId, const TeamspaceMemberType memberType, DataBuffer& readBuf);

	static int32_t genUpdateTeamspaceMemberParam(const TeamspaceRoleType teamRole, const std::string& role, DataBuffer& readBuf);

	static int32_t genListTeamspacesMembersParam(const std::string& keyword, const TeamspaceRoleType roleType, const int32_t limit, const int64_t offset, const std::string& orderField, const std::string& orderDirection, DataBuffer& readBuf);
};

#endif
