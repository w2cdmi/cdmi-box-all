#ifndef _ONEBOX_USERCONTEXT_H_
#define _ONEBOX_USERCONTEXT_H_

#include "CommonDefine.h"

class UserInfoMgr;
class CredentialMgr;
class ConfigureMgr;
class FilterMgr;
class SynchronizeMgr;
class WorkModeMgr;
class NotifyMgr;
class NetworkMgr;
class DataBaseMgr;
class PathMgr;
class SyncFileSystemMgr;
class AsyncTaskMgr;
class ShareResMgr;
class TeamspaceMgr;
class GlobalVariable;
class OverlayIconMgr;

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

	UserContextId(const UserContextId &rhs)
	{
		id = rhs.id;
		parent = rhs.parent;
		type = rhs.type;
		name = rhs.name;
	}

	UserContextId& operator= (const UserContextId &rhs)
	{
		if (&rhs != this)
		{
			id = rhs.id;
			parent = rhs.parent;
			type = rhs.type;
			name = rhs.name;
		}
		return *this;
	}
};

class UserContext
{
public:
	UserContext(int64_t uiHandle, const std::wstring& confPath);

	virtual ~UserContext();

	UserInfoMgr* getUserInfoMgr();
	CredentialMgr* getCredentialMgr();
	ConfigureMgr* getConfigureMgr();
	FilterMgr* getFilterMgr();
	SynchronizeMgr* getSynchronizeMgr();
	WorkModeMgr* getWorkModeMgr();
	NotifyMgr* getNotifyMgr();
	NetworkMgr* getNetworkMgr();
	DataBaseMgr* getDataBaseMgr();
	PathMgr* getPathMgr();
	SyncFileSystemMgr* getSyncFileSystemMgr();
	AsyncTaskMgr* getAsyncTaskMgr();
	ShareResMgr* getShareResMgr();
	TeamspaceMgr* getTeamspaceMgr();
	GlobalVariable* getGlobalVariable();
	OverlayIconMgr* getOverlayIconMgr();

	UserContextId getUserContextId() const;
	void setUserContextId(const UserContextId& id);

private:
	UserInfoMgr* userInfoMgr_;
	CredentialMgr* credentialMgr_;
	ConfigureMgr* configureMgr_;
	FilterMgr* filterMgr_;
	SynchronizeMgr* synchronizeMgr_;
	WorkModeMgr* workModeMgr_;
	NotifyMgr* notifyMgr_;
	NetworkMgr* networkMgr_;
	DataBaseMgr* dataBaseMgr_;
	PathMgr* pathMgr_;
	SyncFileSystemMgr* syncFileSystemMgr_;
	AsyncTaskMgr* asyncTaskMgr_;
	ShareResMgr* shareResMgr_;
	TeamspaceMgr* teamspaceMgr_;
	GlobalVariable* globalVariable_;
	OverlayIconMgr* overlayIconMgr_;

private:
	int64_t uiHandle_;
	const std::wstring confPath_;
	UserContextId id;
};

#endif