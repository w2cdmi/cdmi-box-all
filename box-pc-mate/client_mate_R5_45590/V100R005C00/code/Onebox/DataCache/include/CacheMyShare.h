#ifndef _ONEBOX_CACHE_MYSHARE_H_
#define _ONEBOX_CACHE_MYSHARE_H_

#include "SyncFileSystemMgr.h"
#include "ShareResMgr.h"

#define SQLITE_CACHE_MYSHARE (L"cacheMyShare.db")
#define TABLE_MYSHARE ("tb_MyShare_v4")
#define MYSHARE_ROW_ID ("id")
#define MYSHARE_ROW_PARENT ("parent")
#define MYSHARE_ROW_NAME ("name")
#define MYSHARE_ROW_TYPE ("type")
#define MYSHARE_ROW_PATH ("path")
#define MYSHARE_ROW_SIZE ("size")
#define MYSHARE_ROW_EXTRATYPE ("extraType")

#define TABLE_MYLINK ("tb_MyLink_v4")
#define MYLINK_ROW_ID ("id")
#define MYLINK_ROW_PARENT ("parent")
#define MYLINK_ROW_NAME ("name")
#define MYLINK_ROW_TYPE ("type")
#define MYLINK_ROW_PATH ("path")
#define MYLINK_ROW_SIZE ("size")
#define MYLINK_ROW_LINKCOUNT ("linkCount")
#define MYLINK_ROW_EXTRATYPE ("extraType")

class CacheMyShare
{
public:
	virtual ~CacheMyShare(){}

	static CacheMyShare* create(UserContext* userContext, const std::wstring& parent);

	virtual int32_t listMyShare(const std::string& keyWord, MyShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;
	
	virtual int32_t flushMyShareCache(bool isFlush) = 0;

	virtual int32_t listMyLink(const std::string& keyWord, MyShareNodeList& result, const PageParam& pageParam, int64_t& count) = 0;
	
	virtual int32_t flushMyLinkCache(bool isFlush) = 0;

	virtual int32_t flushMyShare(const FILE_DIR_INFO& info) = 0;

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& idList) = 0;

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& idList) = 0;

	virtual bool isMyShareLinkCacheExist() = 0;
};

#endif