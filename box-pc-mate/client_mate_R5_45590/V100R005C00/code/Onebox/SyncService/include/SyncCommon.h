#ifndef _ONEBOX_SYNC_COMMON_H_
#define _ONEBOX_SYNC_COMMON_H_

#include "UserContext.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "TableDefine.h"
#include <list>
#include <set>
#include <map>
#include <memory>
#include <sstream>
#include <boost/thread/mutex.hpp>

#ifdef _DEBUG
#pragma comment(lib, "sqlite3_d.lib")
#else
#pragma comment(lib, "sqlite3.lib")
#endif

#ifndef SYNC_CONF_NAME
#define SYNC_CONF_NAME (L"\\SyncConfig.ini")
#endif

#define CONF_SYNCVERSION_KEY (L"SyncVersion")
#define CONF_AUTOINC_ID (L"AutoInc")

#define DIFF_SLEEP_LIMEN 16		//2*8

#ifndef SYNC_RULES_PATH
#define SYNC_RULES_PATH (L"./SyncRules.xml")
#endif

#define SYNCRULE_RULE_TAG "<SyncRule"
#define SYNCRULE_RULE_ENDTAG "</SyncRule>"
#define SYNCRULE_CHANGE_TAG "<SDChangeStatus"
#define SYNCRULE_ITEM_MAPTYPE "ItemMapType"
#define SYNCRULE_ITEM_TYPE "ItemType"
#define SYNCRULE_ITEM_MAPTYPE_LOCAL "Local"
#define SYNCRULE_ITEM_MAPTYPE_REMOTE "Remote"
#define SYNCRULE_ITEM_FILE "File"
#define SYNCRULE_ITEM_FOLDER "Folder"
#define SYNCRULE_EXEC_TAG "<SDExecuteAction"
#define SYNCRULE_ACTION_TYPE "ActionType"
#define SYNCRULE_ACTION_COMMAND "ActionCommand"

#define CATCH_SQLITE_EXCEPTION							\
	catch (CppSQLite3Exception& e)						\
	{													\
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		\
					"SQLite DML ErrorCode: %d ErrorMessage: %s ",	\
					e.errorCode(), e.errorMessage());	\
	}													\
	catch(...)											\
	{													\
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		\
					"the InsertSingle function occur unknown exception", NULL); \
	}

enum SyncModel
{
	Sync_All,
	Sync_Local
};

enum ErrorChangedType
{
	Error_Increase,
	Error_Decrease
};

enum MarkStatus
{
	MS_Default,
	MS_Missed,
	MS_Marked
};

enum ActionType
{
	ActionType_Local,
	ActionType_Remote
};

enum ActionCommand
{
	CMD_NoAction,
	CMD_Create,
	CMD_Delete,
	CMD_Rename,
	CMD_Move
};
typedef std::map<std::string, ActionCommand> ActionCommandMap;

struct st_ExecuteAction
{
	ActionType actionType;
	ActionCommand actionCommand;

	st_ExecuteAction()
		:actionType(ActionType_Local)
		,actionCommand(CMD_NoAction){}

	std::string toString()
	{
		std::ostringstream ostr;
		ostr << ((ActionType_Local==actionType)?"Local":"Remote") << "-";
		switch (actionCommand)
		{
		case CMD_NoAction:
			ostr << "NoAction";
			break;
		case CMD_Create:
			ostr << "Create";
			break;
		case CMD_Delete:
			ostr << "Delete";
			break;
		case CMD_Rename:
			ostr << "Rename";
			break;
		case CMD_Move:
			ostr << "Move";
			break;
		default:
			break;
		}
		return ostr.str();
	}
};
typedef std::shared_ptr<st_ExecuteAction> ExecuteAction;

//ItemType_File Folder/File="0/1"
//Local Created="0/1" Deleted="0/1" Moved="0/1" Renamed="0/1" Edited="0/1
//Remote Created="0/1" Deleted="0/1" Moved="0/1" Renamed="0/1" Edited="0/1
enum SyncRuleKey
{
	SRK_NONE = 0,
	SRK_Remote_Edited = 0x0001,
	SRK_Remote_Renamed = 0x0002,
	SRK_Remote_Moved = 0x0004,
	SRK_Remote_Deleted = 0x0008,
	SRK_Remote_Created = 0x0010,
	SRK_Local_Edited = 0x00020,
	SRK_Local_Renamed = 0x00040,
	SRK_Local_Moved = 0x00080,
	SRK_Local_Deleted = 0x0100,
	SRK_Local_Created = 0x0200
};
typedef std::map<std::string, SyncRuleKey> SyncRuleKeyMap;
typedef std::list<ExecuteAction> ExecuteActions;
typedef std::map<SyncRuleKey, ExecuteActions> SyncRulesInfo;

enum OperType
{
	OT_SetNoSync,
	OT_Deleted,
	OT_SetSync,
	OT_Created,
	OT_Edited,
	OT_Renamed,
	OT_Moved,
	OT_Invalid
};

enum KeyType
{
	Key_LocalID,
	Key_RemoteID,
	Key_Invalid
};

enum DiffStatus
{
	Diff_Normal,
	Diff_Failed,
	Diff_Running,
	Diff_Complete,
	Diff_Hidden
};

struct st_ErrorNode
{
	std::string path;
	int32_t errorCode;
};
typedef std::shared_ptr<st_ErrorNode> ErrorNode;
typedef std::list<ErrorNode> ErrorNodes;

struct st_OperNode
{
	int64_t id;
	int64_t key;
	KeyType keyType;
	OperType oper;
	DiffStatus status;
	int64_t priority;
	int64_t size;

	st_OperNode()
		:id(INVALID_ID)
		,key(INVALID_ID)
		,keyType(Key_Invalid)
		,oper(OT_Invalid)
		,status(Diff_Normal)
		,priority(DEFAULT_PRIORITY)
		,size(INVALID_VALUE){}
};
typedef std::shared_ptr<st_OperNode> OperNode;
typedef std::list<OperNode> OperNodes;

struct st_DiffPathNode
{
	int64_t key;
	KeyType keyType;
	std::wstring localPath;
	std::wstring remotePath;
	int64_t size;

	st_DiffPathNode()
		:key(INVALID_ID)
		,keyType(Key_Invalid)
		,localPath(L"")
		,remotePath(L"")
		,size(INVALID_VALUE){}
};
typedef std::shared_ptr<st_DiffPathNode> DiffPathNode;
typedef std::list<DiffPathNode> DiffPathNodes;

typedef std::list<int64_t> IdList;

typedef std::map<int64_t, int64_t> RelationInfo;

/*
0-Normal
1-Creating
2-Delete
3-Parent-Deleted
4-Complate-Deleted
*/
enum IncStatus
{
	SS_Normal_Status,
	SS_Create_Status,
	SS_Delete_Status,
	SS_TrashDelete_Status,
	SS_Clear_Status
};

enum RemoteStatus
{
	RS_None = 0,		//no sync
	RS_Sync_Status = 0x0001,
	RS_Delete_Status = 0x0002
};

enum SyncStatus
{
	NO_SET_SYNC_STATUS,
	SYNC_STATUS
};

struct st_RemoteNode
{
	int64_t id;
	int64_t parent;
	std::wstring name;
	int32_t type;
	RemoteStatus status;
	IncStatus incStatus;
	std::wstring version;
	int64_t contentCreate;
	int64_t contentModify;
	int64_t size;
	
	st_RemoteNode()
		:id(INVALID_ID)
		,parent(INVALID_ID)
		,name(L"")
		,type(FILE_TYPE_DIR)
		,status(RS_Sync_Status)
		,incStatus(SS_Normal_Status)
		,version(L"")
		,contentCreate(INVALID_TIME)
		,contentModify(INVALID_TIME)
		,size(0){}
};
typedef std::shared_ptr<st_RemoteNode> RemoteNode;
typedef std::list<RemoteNode> RemoteNodes;

enum LocalStatus
{
	LS_Normal = 0,
	LS_Delete_Status = 0x0001,
	LS_NoActionDelete_Status = 0x0002,
	LS_ShowNormal = 0x0004,
	LS_Filter = 0x0008
};

struct st_LocalNode
{
	int64_t id;
	int64_t parent;
	std::wstring name;
	int32_t type;
	LocalStatus status;
	int64_t ctime;
	int64_t mtime;

	st_LocalNode()
		:id(INVALID_ID)
		,parent(INVALID_ID)
		,name(L"")
		,type(FILE_TYPE_DIR)
		,status(LS_Normal)
		,ctime(0L)
		,mtime(0L){}

	st_LocalNode(int64_t _id, 
		int64_t _parent, 
		const std::wstring& _name, 
		int32_t _type)
		:id(_id)
		,parent(_parent)
		,name(_name)
		,type(_type)
		,status(LS_Normal)
		,ctime(0L)
		,mtime(0L){}
};
typedef std::shared_ptr<st_LocalNode> LocalNode;
typedef std::list<LocalNode> LocalNodes;

struct st_LocalSyncNode
{
	int64_t id;
	int64_t parent;
	std::wstring name;
	uint32_t attributes;
	uint32_t oper;
	int64_t ctime;
	int64_t mtime;
	std::wstring path;
	int64_t usn;

	st_LocalSyncNode()
		:id(INVALID_ID)
		,parent(INVALID_ID)
		,name(L"")
		,attributes(0)
		,oper(0)
		,ctime(0L)
		,mtime(0L)
		,path(L"")
		,usn(0){}

	st_LocalSyncNode(int64_t parent)
		:id(INVALID_ID)
		,parent(parent)
		,name(L"")
		,attributes(0)
		,oper(0)
		,ctime(0L)
		,mtime(0L)
		,path(L"")
		,usn(0){}
};
typedef std::shared_ptr<st_LocalSyncNode> LocalSyncNode;
typedef std::list<LocalSyncNode> LocalSyncNodes;

inline std::string oper2String(const SyncRuleKey& op)
{
	std::ostringstream ostr;
	ostr << "(" << op << ")"
		<< ((op&SRK_Local_Created)?"1":"0")
		<< ((op&SRK_Local_Deleted)?"1":"0") 
		<< ((op&SRK_Local_Moved)?"1":"0") 
		<< ((op&SRK_Local_Renamed)?"1":"0")
		<< ((op&SRK_Local_Edited)?"1":"0")
		<< "-"
		<< ((op&SRK_Remote_Created)?"1":"0")
		<< ((op&SRK_Remote_Deleted)?"1":"0")
		<< ((op&SRK_Remote_Moved)?"1":"0")
		<< ((op&SRK_Remote_Renamed)?"1":"0")
		<< ((op&SRK_Remote_Edited)?"1":"0");
	return ostr.str();
}

#define CHECK_RESULT(FUNCNAME)					\
	ret = FUNCNAME;									\
	if(RT_OK != ret)									\
    {													\
		return ret;									\
	}

class GlobalMutex
{
public:
	static boost::mutex& localMutex()
	{
		return localMutex_;
	}

	static boost::mutex& remoteMutex()
	{
		return remoteMutex_;
	}

private:
	static boost::mutex localMutex_;
	static boost::mutex remoteMutex_;
};

#endif