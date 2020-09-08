#ifndef _ONEBOX_CACHE_METADATA_H_
#define _ONEBOX_CACHE_METADATA_H_

#include "SyncFileSystemMgr.h"
#include <set>

#define SQLITE_CACHE_METADATA (L"cacheMetaData.db")
#define TABLE_CMD ("tb_CMD_v3")
#define CMD_ROW_ID ("id")
#define CMD_ROW_PARENT ("parent")
#define CMD_ROW_TYPE ("type")
#define CMD_ROW_NAME ("name")
#define CMD_ROW_SIZE ("size")
#define CMD_ROW_VERSION ("version")
#define CMD_ROW_COUNT ("count")
#define CMD_ROW_STATUS ("status")
#define CMD_ROW_TYPESTR ("typeStr")
#define CMD_ROW_MID ("mid")
#define CMD_ROW_MNAME ("mname")
#define CMD_ROW_MTIME ("mtime")
#define CMD_ROW_EXTRATYPE ("extraType")

class CacheMetaData
{
public:
	virtual ~CacheMetaData(){}

	static CacheMetaData* create(UserContext* userContext, const std::wstring& parent, bool isMyFile);

	virtual int32_t listPage(int64_t id, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count) = 0;
	
	virtual int32_t getAllFileId(const int64_t& dirId, std::set<int64_t>& result) = 0;

	virtual int32_t flushFileInfo(const Path& path) = 0;

	virtual int32_t flushCache(const Path& path, bool isFlush) = 0;

	virtual int32_t saveName(const Path& path, const std::wstring& name) = 0;

	virtual int32_t saveParent(const Path& path, const Path& parent) = 0;

	virtual int32_t saveDelete(const Path& path) = 0;

	virtual int32_t saveCreate(const int64_t& teamId, const FILE_DIR_INFO& info) = 0;
};

#endif