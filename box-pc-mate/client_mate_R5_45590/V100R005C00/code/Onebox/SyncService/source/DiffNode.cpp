#include <boost/thread.hpp>
#include "DiffNode.h"
#include "SyncUtility.h"
#include "SyncAction.h"
#include "SyncRules.h"
#include "DataBaseMgr.h"
#include "AsyncTaskMgr.h"
#include "ConfigureMgr.h"
#include "OverlayIconMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("DiffNode")
#endif

class DiffNodeImpl : public CDiffNode
{
public:
	DiffNodeImpl(UserContext* userContext)
		:userContext_(userContext)
		,localId_(INVALID_ID)
		,remoteId_(INVALID_ID)
		,syncRuleKey_(SRK_NONE)
	{
		dataBaseMgr_ = DataBaseMgr::getInstance(userContext);
		syncRules_ = SyncRules::create();
	}

	virtual ~DiffNodeImpl(void)
	{
	}

	virtual void init(const OperNode& operNode)
	{
		getTopDiff(operNode);

		mergeOper();

		checkOper();

		genSyncRuleKey();
	}

	virtual int64_t getLocalId()
	{
		return localId_;
	}
	
	virtual int64_t getRemoteId()
	{
		return remoteId_;
	}
	
	virtual OperNodes& getLocalList()
	{
		return localList_;
	}
	
	virtual OperNodes& getRemoteList()
	{
		return remoteList_;
	}

	virtual ExecuteActions& getActions()
	{
		return actions_;
	}

	virtual SyncRuleKey getSyncRuleKey()
	{
		return syncRuleKey_;
	}

	virtual void refreshDiff(const DiffStatus status, int errorCode = 0)
	{
		if(Diff_Complete==status)
		{
			completeDiff();
			return;
		}

		IdList idList;
		for(OperNodes::const_iterator itL = localList_.begin();
			itL != localList_.end(); ++itL)
		{
			idList.push_back(itL->get()->id);
		}

		for(OperNodes::const_iterator itR = remoteList_.begin();
			itR != remoteList_.end(); ++itR)
		{
			idList.push_back(itR->get()->id);
		}

		dataBaseMgr_->getDiffTable()->refreshDiff(completeList_, Diff_Complete);
		dataBaseMgr_->getDiffTable()->refreshDiff(idList, status);

		if(INVALID_ID != localId_)
		{
			if(localList_.empty())
			{
				std::wstring path = L"";
				dataBaseMgr_->getDiffTable()->deleteCompletePath(localId_, Key_LocalID, path);
			}
			else if(Diff_Running!=status)
			{
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_LocalID, localId_, errorCode);
			}
		}
		if(INVALID_ID != remoteId_)
		{
			if(remoteList_.empty())
			{
				std::wstring path = L"";
				dataBaseMgr_->getDiffTable()->deleteCompletePath(remoteId_, Key_RemoteID, path);
			}
			else if(Diff_Running!=status)
			{
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_RemoteID, remoteId_, errorCode);
			}
		}
	}

	virtual void stepComplete(OperType operType)
	{
		for(OperNodes::const_iterator itL = localList_.begin();
			itL != localList_.end();)
		{
			if(operType==itL->get()->oper)
			{
				completeList_.push_back(itL->get()->id);
				itL = localList_.erase(itL);
			}
			else
			{
				++itL;
			}
		}

		for(OperNodes::const_iterator itR = remoteList_.begin();
			itR != remoteList_.end();)
		{
			if(operType==itR->get()->oper)
			{
				completeList_.push_back(itR->get()->id);
				itR = remoteList_.erase(itR);
			}
			else
			{
				++itR;
			}
		}
	}

private:
	void getTopDiff(const OperNode& operNode)
	{
		if(Key_LocalID==operNode->keyType)
		{
			localId_ = operNode->key;
			(void)dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localId_, remoteId_);
		}
		else
		{
			remoteId_ = operNode->key;
			(void)dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteId_, localId_);
		}
		dataBaseMgr_->getDiffTable()->geOperList(localId_, Key_LocalID, localList_);
		dataBaseMgr_->getDiffTable()->geOperList(remoteId_, Key_RemoteID, remoteList_);
	}
	
	void mergeOper()
	{
		bool islocalEdited = false;

		//merge local oper
		mergeOperList(localList_, islocalEdited);

		//merge remote oper
		mergeOperList(remoteList_, islocalEdited);
	}

	void mergeOperList(OperNodes& operList, bool& localEdited)
	{
		if(operList.empty())
		{
			return;
		}
		OperNodes tempList;
		bool isRenamed = false;
		bool isMoved = false;
		bool isDone = false;
		bool isOtherEdit = false;

		for(OperNodes::reverse_iterator it = operList.rbegin();
			it != operList.rend(); ++it)
		{
			switch(it->get()->oper)
			{
			case OT_SetSync:
			case OT_Created:
				isOtherEdit = true;
				if(!isDone)
				{
					OperNode operNode = *it;
					OperNodes::reverse_iterator nextOper = ++it;
					if(nextOper==operList.rend())
					{
						for(OperNodes::const_iterator itTemp = tempList.begin();
							itTemp != tempList.end(); ++itTemp)
						{
							completeList_.push_back(itTemp->get()->id);
						}
						tempList.clear();
						tempList.push_back(operNode);
						//operList = tempList;
						//return;
						--it;
					}
					else
					{
						//create + delete = none
						completeList_.push_back(it->get()->id);
						if(OT_Deleted!=nextOper->get()->oper
							&&OT_SetNoSync!=nextOper->get()->oper)
						{
							SERVICE_ERROR(MODULE_NAME, RT_ERROR, "the oper after created is:%d", (int)nextOper->get()->oper);
							return;
						}
					}
				}
				else
				{
					completeList_.push_back(it->get()->id);
				}
				break;
			case OT_SetNoSync:
			case OT_Deleted:
				if(!isDone)
				{
					for(OperNodes::const_iterator itTemp = tempList.begin();
						itTemp != tempList.end(); ++itTemp)
					{
						completeList_.push_back(itTemp->get()->id);
					}
					tempList.clear();
					tempList.push_back(*it);
					isDone = true;
				}
				else
				{
					completeList_.push_back(it->get()->id);
				}
				break;
			case OT_Renamed:
				if(!isDone&&!isRenamed)
				{
					tempList.push_front(*it);
					isRenamed = true;
				}
				else
				{
					completeList_.push_back(it->get()->id);
				}
				break;
			case OT_Moved:
				if(!isDone&&!isMoved)
				{
					tempList.push_front(*it);
					isMoved = true;
				}
				else
				{
					completeList_.push_back(it->get()->id);
				}
				break;
			case OT_Edited:
				if(!isDone&&!localEdited)
				{
					tempList.push_front(*it);
					localEdited = true;
				}
				else
				{
					completeList_.push_back(it->get()->id);
					isOtherEdit = true;
				}
				break;
			default:
				break;
			}
		}

		if(localEdited&&isOtherEdit)
		{
			OperNode tempOperNode = *(operList.begin());
			//new editing occurs, cancel the task
			//AsyncTaskId taskId(SD::Utility::String::type_to_string<std::wstring>(tempOperNode->key),
			//	L"", AsyncTaskType(tempOperNode->keyType));
			//if(dataBaseMgr_->getTransTaskTable()->isExist(taskId))
			{
			//	userContext_->getAsyncTaskMgr()->delTask(taskId);
			}
		}
		operList = tempList;
	}

	void checkOper()
	{
		/*
		if((INVALID_ID != localId_)&&(!localList_.empty()))
		{
			if(dataBaseMgr_->getLocalTable()->getPath(localId_).empty())
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "get path error, local id:%I64d", localId_);
				for(OperNodes::iterator itL = localList_.begin(); itL != localList_.end(); )
				{
					if(OT_Deleted!=itL->get()->oper)
					{
						itL = localList_.erase(itL);
						completeList_.push_back(itL->get()->id);
					}
					else
					{
						++itL;
					}
				}
			}
		}
		*/
		if((INVALID_ID != remoteId_)&&(!remoteList_.empty()))
		{
			if(1==remoteList_.size())
			{
				if((OT_SetNoSync==remoteList_.begin()->get()->oper)
					||(OT_Deleted==remoteList_.begin()->get()->oper))
				{
					return;
				}
			}

			std::wstring remotePath = L"";
			if(RT_OK != dataBaseMgr_->getRemoteTable()->getPath(remoteId_, remotePath))
			{
				return;
			}

			if(remotePath.empty())
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "get path error, remote id:%I64d", remoteId_);
				deleteInvaildRemote();
			}
			else
			{
				if(dataBaseMgr_->getRemoteTable()->isNoSync(SD::Utility::FS::get_topdir_name(remotePath)))
				{
					deleteInvaildRemote();
				}
			}
		}
	}

	void genSyncRuleKey()
	{
		syncRuleKey_ = SRK_NONE;
		for(OperNodes::const_iterator it = localList_.begin(); it != localList_.end(); ++it)
		{
			switch(it->get()->oper)
			{
			case OT_Created:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Local_Created);
				break;
			case OT_Deleted:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Local_Deleted);
				break;
			case OT_Renamed:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Local_Renamed);
				break;
			case OT_Moved:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Local_Moved);
				break;
			case OT_Edited:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Local_Edited);
				break;
			default:
				break;
			}
		}
		for(OperNodes::const_iterator it = remoteList_.begin(); it != remoteList_.end(); ++it)
		{
			switch(it->get()->oper)
			{
			case OT_SetSync:
			case OT_Created:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Remote_Created);
				break;
			case OT_SetNoSync:
			case OT_Deleted:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Remote_Deleted);
				break;
			case OT_Renamed:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Remote_Renamed);
				break;
			case OT_Moved:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Remote_Moved);
				break;
			case OT_Edited:
				syncRuleKey_ = SyncRuleKey(syncRuleKey_|SRK_Remote_Edited);
				break;
			default:
				break;
			}
		}

		if(!syncRules_->getActions(syncRuleKey_, actions_))
		{
			refreshDiff(Diff_Failed, RT_DIFF_NOSYNCRULE);
		}
	}

	void completeDiff()
	{
		for(OperNodes::const_iterator itL = localList_.begin();
			itL != localList_.end(); ++itL)
		{
			completeList_.push_back(itL->get()->id);
		}

		for(OperNodes::const_iterator itR = remoteList_.begin();
			itR != remoteList_.end(); ++itR)
		{
			completeList_.push_back(itR->get()->id);
		}

		dataBaseMgr_->getDiffTable()->refreshDiff(completeList_, Diff_Complete);

		std::wstring localPath = L"";
		if(INVALID_ID != localId_)
		{
			dataBaseMgr_->getDiffTable()->deleteCompletePath(localId_, Key_LocalID, localPath);
		}

		std::wstring remotePath = L"";
		if(INVALID_ID != remoteId_)
		{
			dataBaseMgr_->getDiffTable()->deleteCompletePath(remoteId_, Key_RemoteID, remotePath);
		}

		//userContext_->getOverlayIconMgr()->refreshOverlayIcon(localPath.empty()?remotePath:localPath);
	}

	void deleteInvaildRemote()
	{
		if(INVALID_ID==localId_)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "delete invaild remote, remote id:%I64d", remoteId_);
			//delete invaild node
			IdList idlist;
			idlist.push_back(remoteId_);
			dataBaseMgr_->getRemoteTable()->getChildren(idlist);
			dataBaseMgr_->getRemoteTable()->deleteNodes(idlist);

			AsyncTaskIds runningTaskIds;
			//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(idlist, Key_RemoteID, runningTaskIds);
			userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);

			dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, idlist);
			remoteList_.clear();
		}
		else
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "change invaild remote to delete local, remote id:%I64d", remoteId_);
			dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteId_);
			remoteList_.clear();
			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_RemoteID;
			operNode->key = remoteId_;
			operNode->oper = OT_Deleted;
			dataBaseMgr_->getDiffTable()->addOper(operNode);
		}
	}
private:
	UserContext* userContext_;
	DataBaseMgr* dataBaseMgr_;
	std::auto_ptr<SyncRules> syncRules_;

	int64_t localId_;
	int64_t remoteId_;
	
	OperNodes localList_;
	OperNodes remoteList_;

	IdList completeList_;

	SyncRuleKey syncRuleKey_;
	ExecuteActions actions_;
};

DiffNode CDiffNode::create(UserContext* userContext)
{
	return DiffNode(new DiffNodeImpl(userContext));
}