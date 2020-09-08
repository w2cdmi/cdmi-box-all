#ifndef _ONEBOX_BACKUPALLTASK_COMMON_H_
#define _ONEBOX_BACKUPALLTASK_COMMON_H_

#include "CommonDefine.h"
#include <list>
#include <map>
#include "UserContext.h"

#define BACKUPALL_GROUPID L"backup_all_group"
#define VOLUME_ROOTID 0x5000000000005

enum BATaskType
{
	BAT_REAL_TIME,	//通过默认间隔10分钟的增量检测实现
	BAT_PER_DAY,
	BAT_PER_WEEK,
	BAT_PER_MONTH
};

enum BAOperType
{
	BAO_Filter = 0x01,
	BAO_Delete = 0x02,
	BAO_Uploading = 0x04,
	BAO_Upload = 0x08,
	BAO_Create = 0x10,
	BAO_Rename = 0x20,
	BAO_Move = 0x40,
	BAO_CheckRemote = 0x80
};

enum BAVolumeStatus
{
	BAVS_Init,		//初始状态，需要执行全量
	BAVS_Normal,	//正常状态，需要执行增量
	BAVS_Delete		//删除状态，需要取消相关传输任务
};

enum BAPathType
{
	BAP_Select,
	BAP_Filter
};

enum BAScanStatus
{
	BASS_Init,			//初始状态、处理完成
	BASS_Scanning,		//正在扫描
	BASS_Complete,		//扫描完成
	BASS_FullScanning	//正在全量扫描
};

enum BATaskStatus
{
	BATS_Stop,			//停止状态
	BATS_Running,		//正在备份
	BATS_Pausing,		//暂停
	BATS_Complete,		//完成
	BATS_Failed,		//失败
	BATS_Offline		//离线
};

struct BATaskNode
{
	int64_t remoteId;
	BATaskType type;
	std::wstring userDefine; // datetime
	int64_t nextStartTime;
	BATaskStatus status;
	int64_t firstStartTime;	//首次启动时间
	int64_t curStartTime;	//本次启动时间
	int64_t curRunTime;		//本次运行时间，单位：秒
	bool isFilterChange;

	BATaskNode()
		:remoteId(-1L)
		,type(BAT_REAL_TIME)
		,userDefine(L"")
		,nextStartTime(0L)
		,status(BATS_Stop)
		,firstStartTime(0L)
		,curStartTime(0L)
		,curRunTime(0L)
		,isFilterChange(false)
	{
	}
};

struct BATaskBaseNode
{
	int64_t localId;
	int64_t localParent;
	int32_t type;
	std::wstring path;
	int64_t mtime;
	int64_t size;
	int32_t opType;		//BAOperType

	BATaskBaseNode()
		:localId(-1L)
		,localParent(-1L)
		,type(0)
		,path(L"")
		,mtime(0L)
		,size(0L)
		,opType(0)
	{
	}
};
typedef std::list<BATaskBaseNode> BATaskBaseNodeList;

struct BATaskExistFile
{
	int64_t localParent;
	std::wstring name;
	int64_t mtime;
	int32_t opType;
};
struct BATaskExistDir
{
	int64_t localParent;
	std::wstring name;
	int32_t opType;
};
typedef std::map<int64_t, BATaskExistFile> BATaskExistFileInfo;
typedef std::map<int64_t, BATaskExistDir> BATaskExistDirInfo;

struct BATaskLocalNode
{
	BATaskBaseNode baseInfo;
	int64_t remoteId;
	int32_t errorCode;

	BATaskLocalNode()
		:remoteId(-1L)
		,errorCode(0)
	{
	}
};
typedef std::list<BATaskLocalNode> BATaskLocalNodeList;

typedef std::list<int64_t> IdList;

struct BATaskInfo
{
	BATaskStatus status;
	std::wstring remotePath;
	std::wstring curUpload;
	int64_t remoteId;
	int32_t totalCnt;
	int32_t curCnt;			//本次增量个数
	int32_t leftCnt;		//剩余个数不包含失败个数
	int32_t failedCnt;
	int64_t	totalSize;
	int64_t	curSize;		//本次增量大小
	int64_t leftSize;		//剩余大小不包含失败大小
	int64_t transSize;
	int64_t curRunTime;		//本次备份耗时 单位：秒
	int64_t leftTime;		//预估剩余时间 单位：秒
	int64_t curStartTime;	//本次备份启动时间
	int32_t totalDay;		//备份开启总天数

	BATaskInfo()
		:status(BATS_Stop)
		,remotePath(L"")
		,curUpload(L"")
		,remoteId(-1)
		,totalCnt(0)
		,curCnt(0)
		,leftCnt(0)
		,failedCnt(0)
		,totalSize(0L)
		,curSize(0L)
		,leftSize(0L)
		,transSize(0L)
		,curRunTime(0L)
		,leftTime(0L)
		,curStartTime(0L)
		,totalDay(0)
	{
	}
};

struct TimeInfo
{
	int32_t transCnt;
	int64_t transSize;
	int64_t curRunTime;

	TimeInfo():transCnt(0),transSize(0L),curRunTime(0L)
	{
	}
};
typedef std::list<TimeInfo> TimeInfoList;

#endif