#ifndef _ONEBOX_PROXY_MGR_H_
#define _ONEBOX_PROXY_MGR_H_

#include "UserContext.h"
#include "ShareResMgr.h"
#include "SyncFileSystemMgr.h"
#include "CacheMetaData.h"
#include "CacheReceiveShare.h"
#include "CacheMsgInfo.h"
#include "UserInfo.h"

class ONEBOX_DLL_EXPORT ProxyMgr
{
public:
	virtual ~ProxyMgr(){}

	static ProxyMgr* getInstance(UserContext* userContext);

	virtual int32_t create(UserContext* userContext, const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t remove(UserContext* userContext, const Path& path, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t move(UserContext* userContext, const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t copy(UserContext* userContext, const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t rename(UserContext* userContext, const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t listPage(UserContext* userContext, const Path& path, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count, bool isFlush = false) = 0;

	virtual int32_t listAll(UserContext* userContext, const Path& path, LIST_FOLDER_RESULT& result) = 0;

	virtual int32_t flushFileInfo(UserContext* userContext, const Path& path) = 0;

	virtual int32_t getAllFileId(UserContext* userContext, const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual bool checkDirIsExist(std::list<std::wstring>& sourceList, UserContext* userContext, int64_t dirId) = 0;

	virtual int checkExistFileNumber(std::list<std::wstring>& sourceList, UserContext* userContext, int64_t dirId) = 0;

	virtual bool isMyShareCacheExist() = 0;

	virtual bool isMyShareLinkCacheExist() = 0;
	
	virtual int32_t listReceiveShareRes(UserContext* userContext, const ShareNodeParent& parent, ShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush = false) = 0;

	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual int32_t exitShare(int64_t& ownerId, int64_t& id) = 0;

	virtual int32_t listMyShareRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush = false) = 0;

	virtual int32_t listMyLinkRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush = false) = 0;

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& selectedItems) = 0;

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& selectedItems) = 0;

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, bool fromCache = false) = 0;

	virtual int32_t addUser(const ShareUserInfo& userInfo) = 0;

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo) = 0;

	virtual int32_t getMsg(const PageParam& pageParam, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All) = 0;

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed) = 0;

	virtual int32_t deleteMsg(const int64_t msgId) = 0;

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList) = 0;

private:
	static std::auto_ptr<ProxyMgr> instance_;
};

#endif