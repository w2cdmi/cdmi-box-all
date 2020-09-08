#ifndef _ONEBOX_USERCONTEXT_H_
#define _ONEBOX_USERCONTEXT_H_

#include "CommonDefine.h"

class UserContextMgr;
class UserInfoMgr;
class CredentialMgr;
class ConfigureMgr;
class SysConfigureMgr;
class NetworkMgr;
class PathMgr;
class SyncFileSystemMgr;
class ShareResMgr;
class NotifyMgr;
class TeamSpaceResMgr;
class GroupResMgr;
class NodeControlResMgr;
class MsgMgr;
class FilterMgr;
class AsyncTaskMgr;
class WorkModeMgr;
class RestTaskMgr;

enum UserContextType
{
	UserContext_User = 0x01,
	UserContext_Teamspace = 0x02,
	UserContext_ShareUser = 0x04,
	UserContext_Group = 0x08,
	UserContext_Invalid = 0
};

struct UserContextId
{
	// the id is the owner id
	// the parent is the parent owner id, if no parent, keeps default value(0)
	// the name is empty or team space name if the userConetxt's type is team space
	int64_t id;
	int64_t parent;
	UserContextType type;
	std::wstring name;

	UserContextId()
		:id(0)
		,parent(0)
		,type(UserContext_Invalid)
		,name(L"") {}
};

class ONEBOX_DLL_EXPORT UserContext
{
	friend UserContextMgr;
public:
	virtual ~UserContext();

	UserInfoMgr* getUserInfoMgr();
	CredentialMgr* getCredentialMgr();
	ConfigureMgr* getConfigureMgr();
	SysConfigureMgr* getSysConfigureMgr();
	NetworkMgr* getNetworkMgr();
	PathMgr* getPathMgr();
	SyncFileSystemMgr* getSyncFileSystemMgr();
	ShareResMgr* getShareResMgr();
	NotifyMgr* getNotifyMgr();
	TeamSpaceResMgr* getTeamSpaceMgr();
	GroupResMgr* getGroupMgr();
	NodeControlResMgr* getNodeControlMgr();
	MsgMgr* getMsgMgr();
	FilterMgr* getFilterMgr();
	AsyncTaskMgr* getAsyncTaskMgr();
	WorkModeMgr* getWorkmodeMgr();
	RestTaskMgr* getRestTaskMgr();

private:
	UserContext();
	UserContext(const UserContext&);
	UserContext(int64_t uiHandle);
	UserContext* clone();

private:
	int64_t uiHandle_;
	UserInfoMgr* userInfoMgr_;
	CredentialMgr* credentialMgr_;
	ConfigureMgr* configureMgr_;
	SysConfigureMgr* sysConfigureMgr_;
	NetworkMgr* networkMgr_;
	PathMgr* pathMgr_;
	SyncFileSystemMgr* syncFileSystemMgr_;
	ShareResMgr* shareResMgr_;
	NotifyMgr* notifyMgr_;
	TeamSpaceResMgr* teamSpaceResMgr_;
	GroupResMgr* groupResMgr_;
	NodeControlResMgr* nodeControlResMgr_;
	MsgMgr* msgMgr_;
	FilterMgr* filterMgr_;
	AsyncTaskMgr* asyncTaskMgr_;
	WorkModeMgr* workModeMgr_;
	RestTaskMgr* restTaskMgr_;

public:
	UserContextId id;
};

#endif