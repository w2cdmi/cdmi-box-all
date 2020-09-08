#ifndef _ONEBOX_SHARE_RES_MGR_H_
#define _ONEBOX_SHARE_RES_MGR_H_

#include "UserContext.h"
#include "ShareNode.h"
#include "ShareLinkNode.h"
#include "ShareUserInfo.h"
#include "ServerSysConfig.h"
#include "EmailItem.h"

class ShareResMgr
{
public:
	virtual ~ShareResMgr(){}

	static ShareResMgr* create(UserContext* userContext);

	virtual int32_t setShare(const int64_t& id, ShareNodeExList& shareNodeExs, const std::string& path, const std::string& emailMsg) = 0;
	virtual int32_t listShareMember(const int64_t& id, ShareNodeList& shareNodes) = 0;
	virtual int32_t delShareMember(const int64_t& id, const ShareNodeEx& shareNodeEx) = 0;
	virtual int32_t cancelShare(const int64_t& id) = 0;
	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos) = 0;
	virtual int32_t getShareLink(const int64_t& id, ShareLinkNode& shareLinkNode) = 0;
	virtual bool hasShareLink(const int64_t& id) = 0;
	virtual int32_t modifyShareLink(const int64_t& id, const ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t delShareLink(const int64_t& id) = 0;
	virtual int32_t getServerConfig(ServerSysConfig& serverSysConfig) = 0;
	virtual int32_t sendEmail(const EmailNode& emailnode) = 0;
};

#endif
