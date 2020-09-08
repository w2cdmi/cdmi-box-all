#ifndef _ONEBOX_CACHE_MGR_H_
#define _ONEBOX_CACHE_MGR_H_

#include "UserContext.h"
#include "CacheMetaData.h"
#include "CacheReceiveShare.h"
#include "CacheMyShare.h"
#include "CacheMsgInfo.h"
#include "UserInfo.h"

class CacheMgr
{
public:
	virtual ~CacheMgr(){}

	static CacheMgr* getInstance(UserContext* userContext);

	virtual int32_t listPage(UserContext* userContext, int64_t id, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count) = 0;
	
	virtual int32_t flushFileInfo(UserContext* userContext, const Path& path) = 0;

	virtual int32_t getAllFileId(UserContext* userContext, const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual int32_t flushCache(UserContext* userContext, const Path& path, bool isFlush) = 0;

	virtual int32_t saveName(UserContext* userContext, const Path& path, const std::wstring& name) = 0;

	virtual int32_t saveParent(UserContext* userContext, const Path& path, const Path& parent) = 0;

	virtual int32_t saveDelete(UserContext* userContext, const Path& path) = 0;

	virtual int32_t saveCreate(UserContext* userContext, const FILE_DIR_INFO& info) = 0;

	virtual bool isMyShareCacheExist() = 0;

	virtual bool isMyShareLinkCacheExist() = 0;

	virtual int32_t listReceiveShare(const ShareNodeParent& parent, ShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;

	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual int32_t flushShareCache(const ShareNodeParent& parent, bool isFlush) = 0;

	virtual int32_t listMyShare(const std::string& keyword, MyShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;

	virtual int32_t flushMyShareCache(bool isFlush) = 0;

	virtual int32_t listMyLink(const std::string& keyword, MyShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;

	virtual int32_t flushMyLinkCache(bool isFlush) = 0;

	virtual int32_t flushMyShare(const FILE_DIR_INFO& info) = 0;

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& idList) = 0;

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& idList) = 0;

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, int32_t limit) = 0;

	virtual int32_t addUser(const ShareUserInfo& userInfo) = 0;

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo) = 0;

	virtual std::string getUserName(int64_t teamId, int64_t userId) = 0;

	virtual int32_t getMsg(const PageParam& pageParam, const MsgTypeList& msgTypeList, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All) = 0;

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed) = 0;

	virtual int32_t deleteMsg(const int64_t msgId) = 0;

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList) = 0;

private:
	static std::auto_ptr<CacheMgr> instance_;
};

#endif