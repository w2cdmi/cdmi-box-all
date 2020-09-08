#include "SyncMeger.h"
#include "NotifyMgr.h"
#include "DataBaseMgr.h"
#include "ConfigureMgr.h"
#include <boost/thread.hpp>
#include "InIHelper.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncMeger")
#endif

class NotifyObject
{
public:
	explicit NotifyObject(UserContext* userContext)
		:userContext_(userContext)
	{
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"begin"));
	}

	~NotifyObject() 
	{
		try
		{
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"end"));
		}
		catch(...)
		{
		}
	}
private:
	UserContext* userContext_;
}; 

class SyncMegerImpl : public SyncMeger
{
public:
	SyncMegerImpl(UserContext* userContext)
		:userContext_(userContext)
	{
	}

	virtual ~SyncMegerImpl(void)
	{
	}

	virtual int32_t megerAll()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "SyncMeger::megerAll");
		NotifyObject notifyObject(userContext_);

		//get remote no sync
		std::list<std::string> noSyncNames;
		userContext_->getDataBaseMgr()->getRemoteTable()->getNoSyncList(noSyncNames);
		if(!noSyncNames.empty())
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "noSyncNames size:%d", noSyncNames.size());
			IdList localNoSyncIds;
			userContext_->getDataBaseMgr()->getLocalTable()->getNoSyncList(noSyncNames, localNoSyncIds);
			userContext_->getDataBaseMgr()->getLocalTable()->getChildren(localNoSyncIds);
			userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, localNoSyncIds);
			userContext_->getDataBaseMgr()->getLocalTable()->noActionDelete(localNoSyncIds);
		}

		//get merger file&dir
		RelationInfo fileRelationInfo;
		RelationInfo dirRelationInfo;
		userContext_->getDataBaseMgr()->getDiffTable()->getRelationInfo(fileRelationInfo, dirRelationInfo);

		userContext_->getDataBaseMgr()->getRelationTable()->addRelation(fileRelationInfo);
		userContext_->getDataBaseMgr()->getRelationTable()->addRelation(dirRelationInfo);

		IdList idList;
		if(Sync_All==userContext_->getConfigureMgr()->getConfigure()->syncModel())
		{
			for(RelationInfo::const_iterator it = fileRelationInfo.begin();
				it != fileRelationInfo.end(); ++it)
			{
				idList.push_back(it->first);
			}
			for(RelationInfo::const_iterator it = dirRelationInfo.begin();
				it != dirRelationInfo.end(); ++it)
			{
				idList.push_back(it->first);
			}
			userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_RemoteID, idList);
		}
		else
		{
			userContext_->getDataBaseMgr()->getDiffTable()->clearDiff(Key_RemoteID);
		}

		idList.clear();
		for(RelationInfo::const_iterator it = dirRelationInfo.begin();
			it != dirRelationInfo.end(); ++it)
		{
			idList.push_back(it->second);
		}
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, idList);

		idList.clear();
		for(RelationInfo::const_iterator it = fileRelationInfo.begin();
			it != fileRelationInfo.end(); ++it)
		{
			idList.push_back(it->second);
		}
		//update local to LS_ShowNormal
		userContext_->getDataBaseMgr()->getDiffTable()->hiddenDiff(idList);
		userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(idList, LS_ShowNormal);

		return RT_OK;
	}

private:
	UserContext* userContext_;
};

std::auto_ptr<SyncMeger> SyncMeger::create(UserContext* userContext)
{
	return std::auto_ptr<SyncMeger>(new SyncMegerImpl(userContext));
}