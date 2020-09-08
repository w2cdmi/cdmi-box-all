#ifndef _SHARE_RES_MGR_H_
#define _SHARE_RES_MGR_H_

#include "UserContext.h"
#include "ShareNode.h"
#include "ShareLinkNode.h"
#include "ShareUserInfo.h"
#include "ServerSysConfig.h"
#include "EmailItem.h"
#include "Path.h"
#include "PageParam.h"
#include "IFolder.h"
#include "GroupNode.h"
#include <map>

class ONEBOX_DLL_EXPORT ShareResMgr
{
public:
	virtual ~ShareResMgr(){}

	static ShareResMgr* create(UserContext* userContext);

	virtual int32_t listReceiveShareRes(const std::string& keyword, ShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count) = 0;
	virtual int32_t setShare(int64_t id, ShareNodeExList& shareNodeExs, const std::string& path, const std::string& emailMsg) = 0;
	virtual int32_t listShareMember(int64_t id, ShareNodeList& shareNodes) = 0;
	virtual int32_t delShareMember(int64_t id, ShareNodeEx& shareNodeEx) = 0;
	virtual int32_t cancelShare(int64_t id) = 0;
	virtual int32_t exitShare(int64_t ownerId, int64_t id) = 0;
	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, int32_t limit) = 0;
	virtual int32_t getShareLink(int64_t id, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t getShareLink(int64_t id, std::string& linkCode, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t addShareLink(int64_t id, std::string& accessMode, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t listShareLinkByFile(int64_t id, int64_t count, ShareLinkNodeList& shareLinkNodes) = 0;
	virtual bool hasShareLink(int64_t id) = 0;
	virtual bool hasShareLinkV2(int64_t id) = 0;
	virtual int32_t modifyShareLink(int64_t id, ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t delShareLink(int64_t id) = 0;
	virtual int32_t modifyShareLink(int64_t id, std::string& linkCode, ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode) = 0;
	virtual int32_t delShareLink(int64_t id, std::string& linkCode, std::string& type) = 0;
	virtual int32_t getServerConfig(ServerSysConfig& serverSysConfig) = 0;
	virtual int32_t sendEmail(EmailNode& emailnode) = 0;
    virtual int32_t listMyShareRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count) = 0;
	virtual int32_t listMyLinkRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count) = 0;
	virtual int32_t setShare(int64_t id, ShareNodeExList& shareNodeExs, int32_t type, std::wstring fileName, const std::string& emailMsg) = 0;
	virtual int32_t setShareV2(int64_t id, ShareNodeExList& shareNodeExs, int32_t type, std::wstring fileName, const std::string& emailMsg) = 0;
	virtual int32_t setSync(const Path& path, bool isSync) = 0;
	virtual int32_t search(const std::string& keyword, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count,
		bool needPath, std::map<int64_t, std::wstring>& pathInfo) = 0;
	virtual int32_t getFilePath(const int64_t& file_id, std::wstring& path) = 0;
	virtual int32_t getFilePathNodes(const int64_t& file_id, std::list<PathNode>& pathNodes) = 0;
	virtual int32_t getMailInfo(const int64_t fileId, std::string source, EmailInfoNode& emailInfoNode) = 0;
	virtual int32_t setMailInfo(const int64_t fileId, EmailInfoNode& emailInfoNode) = 0;
	virtual int32_t listGroups(const std::string& strKey, const std::string& type, const PageParam& pageparam, int64_t& count, GroupNodeList& nodes) = 0;
};

#endif
