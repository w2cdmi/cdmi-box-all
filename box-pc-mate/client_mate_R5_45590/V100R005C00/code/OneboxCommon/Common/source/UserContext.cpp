#include "UserContext.h"
#include "ConfigureMgr.h"
#include "SysConfigureMgr.h"
#include "CredentialMgr.h"
#include "NetworkMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "UserInfoMgr.h"
#include "ShareResMgr.h"
#include "TeamSpaceResMgr.h"
#include "GroupResMgr.h"
#include "NodeControlResMgr.h"
#include "NotifyMgr.h"
#include "MsgMgr.h"
#include "FilterMgr.h"
#include "AsyncTaskMgr.h"
#include "WorkModeMgr.h"
#include "RestTaskMgr.h"

UserContext::UserContext(int64_t uiHandle)
	:uiHandle_(uiHandle)
	,configureMgr_(NULL)
	,sysConfigureMgr_(NULL)
	,userInfoMgr_(NULL)
	,credentialMgr_(NULL)
	,networkMgr_(NULL)
	,pathMgr_(NULL)
	,syncFileSystemMgr_(NULL)
	,shareResMgr_(NULL)
	,notifyMgr_(NULL)
	,teamSpaceResMgr_(NULL)
	,groupResMgr_(NULL)
	,msgMgr_(NULL)
	,filterMgr_(NULL)
	,nodeControlResMgr_(NULL)
	,asyncTaskMgr_(NULL)
	,workModeMgr_(NULL)
	,restTaskMgr_(NULL)
{
}

UserContext::~UserContext()
{
	try
	{
		if (notifyMgr_ && UserContext_User == id.type)
		{
			notifyMgr_->interrupt();
		}
		if (workModeMgr_ && UserContext_User == id.type)
		{
			delete workModeMgr_;
			workModeMgr_ = NULL;
		}
		if (asyncTaskMgr_ && UserContext_User == id.type)
		{
			delete asyncTaskMgr_;
			asyncTaskMgr_ = NULL;
		}
		if(teamSpaceResMgr_)
		{
			delete teamSpaceResMgr_;
			teamSpaceResMgr_ = NULL;
		}
		if(groupResMgr_)
		{
			delete groupResMgr_;
			groupResMgr_ = NULL;
		}
		if(nodeControlResMgr_)
		{
			delete nodeControlResMgr_;
			nodeControlResMgr_ = NULL;
		}
		if (shareResMgr_)
		{
			delete shareResMgr_;
			shareResMgr_ = NULL;
		}
		if(msgMgr_)
		{
			delete msgMgr_;
			msgMgr_ = NULL;
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
		if (restTaskMgr_ && UserContext_User == id.type)
		{
			delete restTaskMgr_;
			restTaskMgr_ = NULL;
		}
		if (userInfoMgr_)
		{
			delete userInfoMgr_;
			userInfoMgr_ = NULL;
		}
		if (networkMgr_ && UserContext_User == id.type)
		{
			delete networkMgr_;
			networkMgr_ = NULL;
		}
		if (credentialMgr_ && UserContext_User == id.type)
		{
			delete credentialMgr_;
			credentialMgr_ = NULL;
		}
		if (configureMgr_)
		{
			delete configureMgr_;
			configureMgr_ = NULL;
		}
		if (sysConfigureMgr_)
		{
			delete sysConfigureMgr_;
			sysConfigureMgr_ = NULL;
		}
		if (notifyMgr_ && UserContext_User == id.type)
		{
			delete notifyMgr_;
			notifyMgr_ = NULL;
		}
		if (filterMgr_ && UserContext_User == id.type)
		{
			delete filterMgr_;
			filterMgr_ = NULL;
		}
	}
	catch(...){}
}

UserContext* UserContext::clone()
{
	UserContext* userContext = new UserContext(uiHandle_);
	if (NULL != userContext)
	{
		// use the parent's notify, credential, filter, etc...
		userContext->notifyMgr_ = this->notifyMgr_;
		userContext->credentialMgr_ = this->credentialMgr_;
		userContext->networkMgr_ = this->networkMgr_;
		userContext->filterMgr_ = this->filterMgr_;
		userContext->asyncTaskMgr_ = this->asyncTaskMgr_;
		userContext->workModeMgr_ = this->workModeMgr_;
		userContext->restTaskMgr_ = this->restTaskMgr_;

		// initial the configure
		(void)userContext->getConfigureMgr()->unserialize();
	}
	return userContext;
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
		configureMgr_ = ConfigureMgr::create(this);
	}
	return configureMgr_;
}

SysConfigureMgr* UserContext::getSysConfigureMgr()
{
	if (NULL == sysConfigureMgr_)
	{
		sysConfigureMgr_ = SysConfigureMgr::create(this);
	}
	return sysConfigureMgr_;
}

NetworkMgr* UserContext::getNetworkMgr()
{
	if (NULL == networkMgr_)
	{
		networkMgr_ = NetworkMgr::create(this);
	}
	return networkMgr_;
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

ShareResMgr* UserContext::getShareResMgr()
{
	if (NULL == shareResMgr_)
	{
		shareResMgr_ = ShareResMgr::create(this);
	}
	return shareResMgr_;
}

NotifyMgr* UserContext::getNotifyMgr()
{
	if (NULL == notifyMgr_)
	{
		notifyMgr_ = NotifyMgr::create(this, uiHandle_);
	}
	return notifyMgr_;
}

TeamSpaceResMgr* UserContext::getTeamSpaceMgr()
{
	if (NULL == teamSpaceResMgr_)
	{
		teamSpaceResMgr_ = TeamSpaceResMgr::create(this);
	}
	return teamSpaceResMgr_;
}

GroupResMgr* UserContext::getGroupMgr()
{
	if (NULL == groupResMgr_)
	{
		groupResMgr_ = GroupResMgr::create(this);
	}
	return groupResMgr_;
}

NodeControlResMgr* UserContext::getNodeControlMgr()
{
	if (NULL == nodeControlResMgr_)
	{
		nodeControlResMgr_ = NodeControlResMgr::create(this);
	}
	return nodeControlResMgr_;
}


MsgMgr* UserContext::getMsgMgr()
{
	if (NULL == msgMgr_)
	{
		msgMgr_ = MsgMgr::create(this);
	}
	return msgMgr_;
}

FilterMgr* UserContext::getFilterMgr()
{
	if (NULL == filterMgr_)
	{
		filterMgr_ = FilterMgr::create(this);
	}
	return filterMgr_;
}

AsyncTaskMgr* UserContext::getAsyncTaskMgr()
{
	if (NULL == asyncTaskMgr_)
	{
		asyncTaskMgr_ = AsyncTaskMgr::create(this);
	}
	return asyncTaskMgr_;
}

WorkModeMgr* UserContext::getWorkmodeMgr()
{
	if (NULL == workModeMgr_)
	{
		workModeMgr_ = WorkModeMgr::create(this);
	}
	return workModeMgr_;
}

RestTaskMgr* UserContext::getRestTaskMgr()
{
	if (NULL == restTaskMgr_)
	{
		restTaskMgr_ = RestTaskMgr::create(this);
	}
	return restTaskMgr_;
}