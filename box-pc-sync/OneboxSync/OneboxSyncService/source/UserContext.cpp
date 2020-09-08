#include "UserContext.h"
#include "ConfigureMgr.h"
#include "FilterMgr.h"
#include "CredentialMgr.h"
#include "DataBaseMgr.h"
#include "NetworkMgr.h"
#include "NotifyMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "UserInfoMgr.h"
#include "WorkModeMgr.h"
#include "SynchronizeMgr.h"
#include "AsyncTaskMgr.h"
#include "SyncTimeCalc.h"
#include "ShareResMgr.h"
#include "TeamspaceMgr.h"
#include "GlobalVariable.h"
#include "OverlayIconMgr.h"

UserContext::UserContext(int64_t uiHandle, const std::wstring& confPath)
	:uiHandle_(uiHandle)
	,confPath_(confPath)
	,configureMgr_(NULL)
	,filterMgr_(NULL)
	,userInfoMgr_(NULL)
	,credentialMgr_(NULL)
	,networkMgr_(NULL)
	,dataBaseMgr_(NULL)
	,pathMgr_(NULL)
	,syncFileSystemMgr_(NULL)
	,notifyMgr_(NULL)
	,synchronizeMgr_(NULL)
	,workModeMgr_(NULL)
	,asyncTaskMgr_(NULL)
	,shareResMgr_(NULL)
	,teamspaceMgr_(NULL)
	,globalVariable_(NULL)
	,overlayIconMgr_(NULL)
{
	try
	{
		if (NULL == userInfoMgr_)
		{
			userInfoMgr_ = UserInfoMgr::create(this);
		}
		if (NULL == credentialMgr_)
		{
			credentialMgr_ = CredentialMgr::create(this);
		}
		if (NULL == configureMgr_)
		{
			configureMgr_ = ConfigureMgr::create(this, confPath_);
		}
		if (NULL == filterMgr_)
		{
			filterMgr_ = FilterMgr::create(this);
		}
		if (NULL == notifyMgr_)
		{
			notifyMgr_ = NotifyMgr::create(this, uiHandle_);
		}
		if (NULL == overlayIconMgr_)
		{
			overlayIconMgr_ = OverlayIconMgr::create(this);
		}
		if (NULL == networkMgr_)
		{
			networkMgr_ = NetworkMgr::create(this);
		}
		if (NULL == dataBaseMgr_)
		{
			dataBaseMgr_ = DataBaseMgr::create(this);
		}
		if (NULL == pathMgr_)
		{
			pathMgr_ = PathMgr::create(this);
		}
		if (NULL == syncFileSystemMgr_)
		{
			syncFileSystemMgr_ = SyncFileSystemMgr::create(this);
		}
		if (NULL == shareResMgr_)
		{
			shareResMgr_ = ShareResMgr::create(this);
		}
		if (NULL == teamspaceMgr_)
		{
			teamspaceMgr_ = TeamspaceMgr::create(this);
		}
		if (NULL == globalVariable_)
		{
			globalVariable_ = GlobalVariable::create(this);
		}
	}
	catch(...){}
}

UserContext::~UserContext()
{
	try
	{
		if (workModeMgr_)
		{
			delete workModeMgr_;
			workModeMgr_ = NULL;
		}
		if (synchronizeMgr_)
		{
			delete synchronizeMgr_;
			synchronizeMgr_ = NULL;
		}
		if (asyncTaskMgr_)
		{
			delete asyncTaskMgr_;
			asyncTaskMgr_ = NULL;
		}
		if (globalVariable_)
		{
			delete globalVariable_;
			globalVariable_ = NULL;
		}
		if (notifyMgr_)
		{
			delete notifyMgr_;
			notifyMgr_ = NULL;
		}
		if (overlayIconMgr_)
		{
			delete overlayIconMgr_;
			overlayIconMgr_ = NULL;
		}
		if (syncFileSystemMgr_)
		{
			delete syncFileSystemMgr_;
			syncFileSystemMgr_ = NULL;
		}
		if (pathMgr_)
		{
			delete pathMgr_;
			pathMgr_ = NULL;
		}	
		if (dataBaseMgr_)
		{
			delete dataBaseMgr_;
			dataBaseMgr_ = NULL;
		}
		if (userInfoMgr_)
		{
			delete userInfoMgr_;
			userInfoMgr_ = NULL;
		}
		if (networkMgr_)
		{
			delete networkMgr_;
			networkMgr_ = NULL;
		}
		if (credentialMgr_)
		{
			delete credentialMgr_;
			credentialMgr_ = NULL;
		}
		if (filterMgr_)
		{
			delete filterMgr_;
			filterMgr_ = NULL;
		}
		if (configureMgr_)
		{
			delete configureMgr_;
			configureMgr_ = NULL;
		}
		if (shareResMgr_)
		{
			delete shareResMgr_;
			shareResMgr_ = NULL;
		}
		if (teamspaceMgr_)
		{
			delete teamspaceMgr_;
			teamspaceMgr_ = NULL;
		}
	}
	catch(...){}
}

UserInfoMgr* UserContext::getUserInfoMgr()
{
	if (NULL == userInfoMgr_)
	{
		userInfoMgr_ = UserInfoMgr::create(this);
	}
	return userInfoMgr_;
}

CredentialMgr* UserContext::getCredentialMgr()
{
	if (NULL == credentialMgr_)
	{
		credentialMgr_ = CredentialMgr::create(this);
	}
	return credentialMgr_;
}

ConfigureMgr* UserContext::getConfigureMgr()
{
	if (NULL == configureMgr_)
	{
		configureMgr_ = ConfigureMgr::create(this, confPath_);
		configureMgr_->unserialize();
	}
	return configureMgr_;
}

FilterMgr* UserContext::getFilterMgr()
{
	if (NULL == filterMgr_)
	{
		filterMgr_ = FilterMgr::create(this);
	}
	return filterMgr_;
}

SynchronizeMgr* UserContext::getSynchronizeMgr()
{
	if (NULL == synchronizeMgr_)
	{
		synchronizeMgr_ = SynchronizeMgr::create(this);
	}
	return synchronizeMgr_;
}

WorkModeMgr* UserContext::getWorkModeMgr()
{
	if (NULL == workModeMgr_)
	{
		workModeMgr_ = WorkModeMgr::create(this);
	}
	return workModeMgr_;
}

NotifyMgr* UserContext::getNotifyMgr()
{
	if (NULL == notifyMgr_)
	{
		notifyMgr_ = NotifyMgr::create(this, uiHandle_);
	}
	return notifyMgr_;
}

NetworkMgr* UserContext::getNetworkMgr()
{
	if (NULL == networkMgr_)
	{
		networkMgr_ = NetworkMgr::create(this);
	}
	return networkMgr_;
}

DataBaseMgr* UserContext::getDataBaseMgr()
{
	if (NULL == dataBaseMgr_)
	{
		dataBaseMgr_ = DataBaseMgr::create(this);
	}
	return dataBaseMgr_;
}

PathMgr* UserContext::getPathMgr()
{
	if (NULL == pathMgr_)
	{
		pathMgr_ = PathMgr::create(this);
	}
	return pathMgr_;
}

SyncFileSystemMgr* UserContext::getSyncFileSystemMgr()
{
	if (NULL == syncFileSystemMgr_)
	{
		syncFileSystemMgr_ = SyncFileSystemMgr::create(this);
	}
	return syncFileSystemMgr_;
}

AsyncTaskMgr* UserContext::getAsyncTaskMgr()
{
	if (NULL == asyncTaskMgr_)
	{
		asyncTaskMgr_ = AsyncTaskMgr::create(this);
	}
	return asyncTaskMgr_;
}

ShareResMgr* UserContext::getShareResMgr()
{
	if (NULL == shareResMgr_)
	{
		shareResMgr_ = ShareResMgr::create(this);
	}
	return shareResMgr_;
}

TeamspaceMgr* UserContext::getTeamspaceMgr()
{
	if (NULL == teamspaceMgr_)
	{
		teamspaceMgr_ = TeamspaceMgr::create(this);
	}
	return teamspaceMgr_;
}

GlobalVariable* UserContext::getGlobalVariable()
{
	if (NULL == globalVariable_)
	{
		globalVariable_ = GlobalVariable::create(this);
	}
	return globalVariable_;
}

OverlayIconMgr* UserContext::getOverlayIconMgr()
{
	if (NULL == overlayIconMgr_)
	{
		overlayIconMgr_ = OverlayIconMgr::create(this);
	}
	return overlayIconMgr_;
}

UserContextId UserContext::getUserContextId() const
{
	return id;
}

void UserContext::setUserContextId(const UserContextId& id)
{
	this->id = id;
}
