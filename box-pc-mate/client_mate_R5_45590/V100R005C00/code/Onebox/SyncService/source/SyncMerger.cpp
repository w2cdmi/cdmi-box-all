#include "SyncMerger.h"
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

class SyncMergerImpl : public SyncMerger
{
public:
	SyncMergerImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		dataBaseMgr_ = DataBaseMgr::getInstance(userContext);
	}

	virtual ~SyncMergerImpl(void)
	{
	}

	virtual int32_t megerAll()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "SyncMeger::megerAll");
		NotifyObject notifyObject(userContext_);

		//get remote no sync
		std::list<std::string> noSyncNames;
		dataBaseMgr_->getRemoteTable()->getNoSyncList(noSyncNames);
		if(!noSyncNames.empty())
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "noSyncNames size:%d", noSyncNames.size());
			IdList localNoSyncIds;
			dataBaseMgr_->getLocalTable()->getNoSyncList(noSyncNames, localNoSyncIds);
			dataBaseMgr_->getLocalTable()->getChildren(localNoSyncIds);
			dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localNoSyncIds);
			dataBaseMgr_->getLocalTable()->noActionDelete(localNoSyncIds);
		}

		//get merger file&dir
		RelationInfo fileRelationInfo;
		RelationInfo dirRelationInfo;
		dataBaseMgr_->getDiffTable()->getRelationInfo(fileRelationInfo, dirRelationInfo);

		dataBaseMgr_->getRelationTable()->addRelation(fileRelationInfo);
		dataBaseMgr_->getRelationTable()->addRelation(dirRelationInfo);

		IdList idList;
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
		dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, idList);

		idList.clear();
		for(RelationInfo::const_iterator it = dirRelationInfo.begin();
			it != dirRelationInfo.end(); ++it)
		{
			idList.push_back(it->second);
		}
		dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, idList);

		idList.clear();
		for(RelationInfo::const_iterator it = fileRelationInfo.begin();
			it != fileRelationInfo.end(); ++it)
		{
			idList.push_back(it->second);
		}
		//update local to LS_ShowNormal
		dataBaseMgr_->getDiffTable()->hiddenDiff(idList);
		dataBaseMgr_->getLocalTable()->updateStatus(idList, LS_ShowNormal);

		return RT_OK;
	}

private:
	UserContext* userContext_;
	DataBaseMgr* dataBaseMgr_;
};

std::auto_ptr<SyncMerger> SyncMerger::create(UserContext* userContext)
{
	return std::auto_ptr<SyncMerger>(new SyncMergerImpl(userContext));
}