#ifndef _ONEBOX_ASYNCTASK_COMMON_H_
#define _ONEBOX_ASYNCTASK_COMMON_H_

#include "CommonDefine.h"
#include <list>
#include "UserContext.h"

enum AsyncTransType
{
	ATT_Upload = 0x01,
	ATT_Download = 0x02,
	ATT_Upload_Outlook = 0x10,
	ATT_Upload_Office = 0x20,
	ATT_Backup = 0x40,
	ATT_Invalid = 0
};

enum AsyncTransStatus
{
	ATS_Waiting = 0x0001,
	ATS_Running = 0x0002,
	ATS_Complete = 0x0004,
	ATS_Cancel = 0x0008,
	ATS_Error = 0x0010,
	ATS_Invalid = 0
};

enum AsyncTransStatusEx
{
	ATSEX_Uninitial = 0x0001,
	ATSEX_Scanning = 0x0002,
	ATSEX_AddingTasks = 0x0004,
	ATSEX_VirtualParent = 0x0008,
	ATSEX_Invalid = 0
};

struct AsyncTransBlock
{
	int64_t blockSize;
	int64_t offset;
	int64_t blockOffset;

	AsyncTransBlock() 
		:blockSize(0)
		,offset(0)
		,blockOffset(0) {}
};

struct AsyncTransBlocks
{
	uint32_t blockNum;
	AsyncTransBlock* blocks;

	AsyncTransBlocks() 
		:blockNum(0)
		,blocks(NULL) {}

	~AsyncTransBlocks()
	{
		if (NULL != blocks)
		{
			delete[] blocks;
			blocks = NULL;
			blockNum = 0;
		}
	}
};

struct st_AsyncTransRootNodeBase
{
	std::wstring group;
	std::wstring source;
	std::wstring parent;
	std::wstring name;
	AsyncTransType type;
	FILE_TYPE fileType;
	int64_t userId;
	UserContextType userType;
	std::wstring userName;
	int64_t size;

	st_AsyncTransRootNodeBase()
		:group(L""), source(L""), parent(L""), name(L"")
		,type(ATT_Invalid), fileType(FILE_TYPE_DIR)
		,userId(INVALID_ID), userType(UserContext_Invalid)
		,userName(L""), size(0) {}
};

struct st_AsyncTransRootNode : public st_AsyncTransRootNodeBase
{
	AsyncTransStatus status;
	AsyncTransStatusEx statusEx;
	int32_t priority;
	int64_t transedSize;
	int32_t errorCode;

	st_AsyncTransRootNode()
		:status(ATS_Invalid), statusEx(ATSEX_Invalid)
		,priority(DEFAULT_PRIORITY), transedSize(0)
		,errorCode(RT_OK) {}
};
typedef std::shared_ptr<st_AsyncTransRootNode> AsyncTransRootNode;
typedef std::list<AsyncTransRootNode> AsyncTransRootNodes;

struct st_AsyncTransDataNode
{
	std::wstring group;
	std::wstring source;
	AsyncTransBlocks blocks;
	int64_t mtime;
	Fingerprint fingerprint;
	std::wstring userDefine;

	st_AsyncTransDataNode()
		:group(L""), source(L""), mtime(0)
		,userDefine(L"") {}
};
typedef std::shared_ptr<st_AsyncTransDataNode> AsyncTransDataNode;
typedef std::list<AsyncTransDataNode> AsyncTransDataNodes;

struct st_AsyncTransDetailNode
{
	std::wstring source;
	std::wstring parent;
	std::wstring name;
	FILE_TYPE fileType;
	AsyncTransStatus status;
	AsyncTransStatusEx statusEx;
	int64_t size;
	int32_t errorCode;
	AsyncTransDataNode data;

	AsyncTransRootNode root;

	st_AsyncTransDetailNode()
		:source(L""), parent(L""), name(L"")
		,fileType(FILE_TYPE_DIR)
		,status(ATS_Invalid), statusEx(ATSEX_Invalid)
		,size(0), errorCode(RT_OK) {}
};
typedef std::shared_ptr<st_AsyncTransDetailNode> AsyncTransDetailNode;
typedef std::list<AsyncTransDetailNode> AsyncTransDetailNodes;

struct st_AsyncTransCompleteNode : public st_AsyncTransRootNodeBase
{
	int64_t completeTime;

	st_AsyncTransCompleteNode() : completeTime(0) {}
};
typedef std::shared_ptr<st_AsyncTransCompleteNode> AsyncTransCompleteNode;
typedef std::list<AsyncTransCompleteNode> AsyncTransCompleteNodes;

enum TASK_THREAD_NUM
{
	LowTaskThreadNum = 1, 
	MiddleTaskThreadNum = 2, 
	HightTaskThreadNum = 4
};

enum TASK_FILE_SIZE_LEVEL
{
	LowTaskFileSizeLevel = (20*1024*1024), //20MB
	HightTaskFileSizeLevel = (100*1024*1024) //100MB
};

enum AsyncTransCacheMode
{
	ATCM_NoCache,
	ATCM_CacheAll,
	ATCM_CachePart
};

enum AsyncTransCacheChangeType
{
	ATCCT_STRONG, 
	ATCCT_WEAK, 
	ATCCT_NONE
};

#endif