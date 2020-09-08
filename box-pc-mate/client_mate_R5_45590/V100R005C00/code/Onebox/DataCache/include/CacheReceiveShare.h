#ifndef _ONEBOX_CACHE_RECEIVESHARE_H_
#define _ONEBOX_CACHE_RECEIVESHARE_H_

#include "SyncFileSystemMgr.h"
#include "ShareResMgr.h"
#include <set>

#define SQLITE_CACHE_SHARE2ME (L"cacheShare2Me.db")
#define TABLE_S2M ("tb_S2M_v4")
#define S2M_ROW_SHAREID ("share_id")
#define S2M_ROW_ID ("id")
#define S2M_ROW_PARENT ("parent")
#define S2M_ROW_TYPE ("type")
#define S2M_ROW_NAME ("name")
#define S2M_ROW_SIZE ("size")
#define S2M_ROW_COUNT ("count")
#define S2M_ROW_STIME ("stime")
#define S2M_ROW_OWNERID ("owner_id")
#define S2M_ROW_OWNERNAME ("owner_name")
#define S2M_ROW_EXTRATYPE ("extraType")

struct ShareNodeParent
{
	int64_t id;
	int64_t ownerId;
	std::string ownerName;
	int64_t shareTime;
	std::string keyWord;

	ShareNodeParent():id(-1), ownerId(-1), ownerName(""), shareTime(0), keyWord("")
	{}
};

class CacheReceiveShare
{
public:
	virtual ~CacheReceiveShare(){}

	static CacheReceiveShare* create(UserContext* userContext, const std::wstring& parent);

	virtual int32_t listPage(const ShareNodeParent& parent, ShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;
	
	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual int32_t flushShareCache(const ShareNodeParent& parent, bool isFlush) = 0;
};

#endif