#include "DiffProcessor.h"
#include "SyncCommon.h"
#include "SyncAction.h"
#include "DataBaseMgr.h"
#include "FilterMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include "NotifyMgr.h"
#include "DiffNode.h"
#include "SyncMerger.h"
#include "OverlayIconMgr.h"
#include <boost/thread.hpp>
#include "AsyncTaskMgr.h"
#include "SyncConfigure.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("DiffProcessor")
#endif

class DiffProcessorImpl : public DiffProcessor
{
public:
	DiffProcessorImpl(UserContext* userContext)
		:userContext_(userContext)
		,lastNoData_(false)
		,lastDiffStatus_(Diff_Normal)
		,lastkey_(-1L)
		,runningDownload_(0)
		,runningUpload_(0)
		,taskCheckCnt_(0)
		,isTaskIdle_(true)
		,isStart_(true)
		,isBusy_(false)
		,hiddenCnt_(0)
	{
		nextOperNode_ = OperNode(new st_OperNode);
		dataBaseMgr_ = DataBaseMgr::getInstance(userContext);
		overlayIconMgr_ = OverlayIconMgr::create(userContext);
		syncAction_ = SyncAction::create(userContext);

		localRoot_ = SyncConfigure::getInstance(userContext_)->monitorRootPath();
	}

	virtual int32_t processDiff()
	{
		int32_t ret = RT_OK;

		if(isStart_)
		{
			boost::mutex::scoped_lock remoteLock(GlobalMutex::remoteMutex());
			boost::mutex::scoped_lock localLock(GlobalMutex::localMutex());
			isStart_ = false;
			if(dataBaseMgr_->getRelationTable()->isNoRelation())
			{
				std::auto_ptr<SyncMerger> syncMeger = SyncMerger::create(userContext_);
				syncMeger->megerAll();
			}
			return RT_OK;
		}

		OperNode operNode(new st_OperNode);
		ret = getNextOperNode(operNode);
		if(RT_OK!=ret)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(500));
			if(lastNoData_)
			{
				return ret;
			}
			//refresh all the icons into the complete state when there is no diff
			if(0==dataBaseMgr_->getDiffTable()->getDiffCount())
			{
				dataBaseMgr_->getDiffTable()->delAllDiffPath();
				//userContext_->getAsyncTaskMgr()->delAllTask();
				lastNoData_ = true;
			}
			else
			{
				//checkTask();
			}

			return ret;
		}
		lastNoData_ = false;
		
		if(Diff_Hidden==operNode->status)
		{
			if(hiddenCnt_>100)
			{
				dataBaseMgr_->getDiffTable()->lowerHiddenPriority();
				hiddenCnt_ = 0;
			}
			else
			{
				++hiddenCnt_;
			}
		}

		if(lastDiffStatus_!=operNode->status)
		{
			if(Diff_Normal==lastDiffStatus_)
			{
				//Diff_Normal -> other, show error list.
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
							SD::Utility::String::format_string(L"%d", Error_Increase)));
			}
			else
			{
				//other -> Diff_Normal, init priority to DEFAULT_PRIORITY~DIFF_SLEEP_LIMEN
				dataBaseMgr_->getDiffTable()->initPriority();
			}

			lastDiffStatus_ = operNode->status;
		}

		boost::mutex::scoped_lock remoteLock(GlobalMutex::remoteMutex());
		boost::mutex::scoped_lock localLock(GlobalMutex::localMutex());

		DiffNode diffNode = CDiffNode::create(userContext_);
		diffNode->init(operNode);
		lastkey_ = operNode->key;
		ret = syncAction_->executeAction(diffNode, nextOperNode_);

		return ret;
	}

	virtual void notifyDiff()
	{
		notifyLeft();
		if((!dataBaseMgr_->getDiffTable()->isNeedRefresh())||isStart_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(500));
			return;
		}

		DiffPathNodes diffInfos;
		refreshDiffPath(diffInfos);

		for(DiffPathNodes::const_iterator it = diffInfos.begin(); it != diffInfos.end(); ++it)
		{
			overlayIconMgr_->refreshOverlayIcon(it->get()->localPath);
		}

		overlayIconMgr_->notifyOverlayIcons();
	}

private:
	void checkTask()
	{
		//check running diff
		int64_t downloadSize = 0;
		int64_t uploadSize = 0;
		//if(RT_OK==userContext_->getAsyncTaskMgr()->getTransSize(downloadSize, uploadSize))
		{
			taskCheckCnt_ = 0;
			if((downloadSize!=runningDownload_)||(uploadSize!=runningUpload_))
			{
				runningDownload_ = downloadSize;
				runningUpload_ = uploadSize;
			}
		}
		//else
		{
			++taskCheckCnt_;
			if(taskCheckCnt_ > 600)
			{
				dataBaseMgr_->getDiffTable()->restoreRunningTask();
				taskCheckCnt_ = 0;
			}
		}
	}

	void refreshDiffPath(DiffPathNodes& diffInfos)
	{
		//get inc diff info
		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		int64_t lastAutoInc = iniHelper.GetValue<int64_t>(L"", CONF_AUTOINC_ID, 0);
		int64_t maxAutoInc = 0;
		dataBaseMgr_->getDiffTable()->getIncDiff(lastAutoInc, maxAutoInc, diffInfos);

		if(maxAutoInc <= lastAutoInc)
		{
			return;
		}

		//get path
		for(DiffPathNodes::iterator it = diffInfos.begin(); it != diffInfos.end(); ++it)
		{
			if(Key_LocalID==it->get()->keyType)
			{
				it->get()->localPath = dataBaseMgr_->getLocalTable()->getPath(it->get()->key);
				if(it->get()->localPath.empty())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "get local path failed. id: %I64d", it->get()->key);
					boost::this_thread::sleep(boost::posix_time::seconds(1));
					return;
				}
			}
			else if(Key_RemoteID==it->get()->keyType)
			{
				it->get()->remotePath = dataBaseMgr_->getRemoteTable()->getPath(it->get()->key);
				if(it->get()->remotePath.empty())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "get remote path failed. id: %I64d", it->get()->key);
					boost::this_thread::sleep(boost::posix_time::seconds(1));
					return;
				}
				int64_t localId = INVALID_ID;
				if(RT_OK == dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(it->get()->key, localId))
				{
					it->get()->localPath = dataBaseMgr_->getLocalTable()->getPath(localId);
				}
				else
				{
					it->get()->localPath = localRoot_ + it->get()->remotePath;
				}
			}
		}

		//replace path info
		dataBaseMgr_->getDiffTable()->replaceIncPath(diffInfos);
		iniHelper.SetValue<int64_t>(L"", CONF_AUTOINC_ID, maxAutoInc);
	}

	void notifyLeft()
	{
		int64_t totalCnt = dataBaseMgr_->getLocalTable()->getCount();
		int64_t leftCnt = dataBaseMgr_->getDiffTable()->getLeftDiffCount();

		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_DIFF_CNT, 
			SD::Utility::String::format_string(L"%I64d", totalCnt),
			SD::Utility::String::format_string(L"%I64d", totalCnt-leftCnt)));
	}

	int32_t getNextOperNode(OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;
		if(INVALID_ID == nextOperNode_->key)
		{
			if(!userContext_->getAsyncTaskMgr()->isIdle())
			{
				return RT_ERROR;
			}
			ret = dataBaseMgr_->getDiffTable()->getTopOper(nextOperNode);
		}
		else
		{
			nextOperNode->key = nextOperNode_->key;
			nextOperNode->keyType = nextOperNode_->keyType;
			OperNodes operList;
			dataBaseMgr_->getDiffTable()->geOperList(nextOperNode->key, nextOperNode->keyType, operList);
			if(operList.empty())
			{
				SERVICE_ERROR(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "get nextOperNode failed. id: %I64d", nextOperNode->key);
			}
			else
			{
				nextOperNode->status = operList.rbegin()->get()->status;
			}
			
			nextOperNode_->key = INVALID_ID;
		}

		return ret;
	}

private:
	UserContext* userContext_;
	DataBaseMgr* dataBaseMgr_;
	std::auto_ptr<OverlayIconMgr> overlayIconMgr_;
	std::auto_ptr<SyncAction> syncAction_;
	int64_t runningDownload_;
	int64_t runningUpload_;
	int32_t taskCheckCnt_;

	std::wstring localRoot_;
	bool lastNoData_;
	DiffStatus lastDiffStatus_;
	int64_t lastkey_;
	OperNode nextOperNode_;

	bool isTaskIdle_;
	bool isStart_;
	bool isBusy_;
	int32_t hiddenCnt_;
};

std::auto_ptr<DiffProcessor> DiffProcessor::create(UserContext* userContext)
{
	return std::auto_ptr<DiffProcessor>(static_cast<DiffProcessor*>(new DiffProcessorImpl(userContext)));
}
