#include "DiffProcessor.h"
#include "SyncCommon.h"
#include "SyncAction.h"
#include "DataBaseMgr.h"
#include "FilterMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "AsyncTaskMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include "NotifyMgr.h"
#include "SyncTimeCalc.h"
#include "GlobalVariable.h"
#include "DiffNode.h"
#include "SyncMeger.h"
#include "OverlayIconMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("DiffProcessor")
#endif

class DiffProcessorImpl : public DiffProcessor
{
public:
	DiffProcessorImpl(UserContext* userContext, 
		LocalDetector* localDetector, 
		RemoteDetector* remoteDetector)
		:userContext_(userContext)
		,localDetector_(localDetector)
		,remoteDetector_(remoteDetector)
		,lastNoData_(false)
		,lastDiffStatus_(Diff_Normal)
		,lastkey_(-1L)
		//,timeCntDiffId_(INVALID_ID)
		,runningDownload_(0)
		,runningUpload_(0)
		,taskCheckCnt_(0)
		,isTaskIdle_(true)
		,isStart_(true)
		,isBusy_(false)
		,hiddenCnt_(0)
	{
		nextOperNode_ = OperNode(new st_OperNode);
		syncAction_ = SyncAction::create(userContext_, SyncModel(userContext_->getConfigureMgr()->getConfigure()->syncModel()));

		userContext_->getAsyncTaskMgr()->addCallback(
			boost::bind(&DiffProcessorImpl::asyncTransTaskCallback, this, _1, _2), ATT_Upload);
		userContext_->getAsyncTaskMgr()->addCallback(
			boost::bind(&DiffProcessorImpl::asyncTransTaskCallback, this, _1, _2), ATT_Download);
		
		localRoot_ = userContext_->getConfigureMgr()->getConfigure()->monitorRootPath();
	}

	virtual int32_t processDiff()
	{
		int32_t ret = RT_OK;

		if(isStart_)
		{
			boost::mutex::scoped_lock remoteLock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());
			boost::mutex::scoped_lock localLock(userContext_->getGlobalVariable()->globalMutex()->localMutex());
			isStart_ = false;
			if(userContext_->getDataBaseMgr()->getRelationTable()->isNoRelation())
			{
				std::auto_ptr<SyncMeger> syncMeger = SyncMeger::create(userContext_);
				syncMeger->megerAll();
			}
			else if(Sync_Local==userContext_->getConfigureMgr()->getConfigure()->syncModel())
			{
				userContext_->getDataBaseMgr()->getDiffTable()->clearDiff(Key_RemoteID);
				AsyncTaskIds runningTaskIds;
				userContext_->getDataBaseMgr()->getTransTaskTable()->getRunningTaskEx(Key_RemoteID, runningTaskIds);
				userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
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
			/*
			if(INVALID_ID!=timeCntDiffId_)
			{
				timeCntDiffId_ = userContext_->getSyncTimeCalc()->stopCntTimer();
			}
			*/
			//refresh all the icons into the complete state when there is no diff
			if(0==userContext_->getDataBaseMgr()->getDiffTable()->getDiffCount())
			{
				userContext_->getDataBaseMgr()->getDiffTable()->delAllDiffPath();
				userContext_->getAsyncTaskMgr()->delAllTask();
				lastNoData_ = true;
			}
			else
			{
				checkTask();
			}

			return ret;
		}
		lastNoData_ = false;
		
		if(Diff_Failed==operNode->status ||lastkey_==operNode->key)
		{
			/*
			if(INVALID_ID!=timeCntDiffId_)
			{
				timeCntDiffId_ = userContext_->getSyncTimeCalc()->stopCntTimer();
			}
			*/
			if(DIFF_SLEEP_LIMEN < operNode->priority)
			{
				//sleep 30s
				for(int32_t i = 0; i < DIFF_FAILED_SLEEPTIMES; ++i)
				{
					if(userContext_->getDataBaseMgr()->getDiffTable()->isNeedRefresh())
					{
						break;
					}
					if((!isTaskIdle_)&&userContext_->getAsyncTaskMgr()->isIdle())
					{
						isTaskIdle_ = true;
						break;
					}
					boost::this_thread::sleep(boost::posix_time::milliseconds(500));
				}
			}
		}
		else if(Diff_Hidden==operNode->status)
		{
			if(hiddenCnt_>100)
			{
				userContext_->getDataBaseMgr()->getDiffTable()->lowerHiddenPriority();
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
				userContext_->getDataBaseMgr()->getDiffTable()->initPriority();
			}

			lastDiffStatus_ = operNode->status;
		}

		if(RT_OK != ret)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(500));
			return ret;
		}

		boost::mutex::scoped_lock remoteLock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());
		boost::mutex::scoped_lock localLock(userContext_->getGlobalVariable()->globalMutex()->localMutex());

		DiffNode diffNode = CDiffNode::create(userContext_);
		diffNode->init(operNode);
		lastkey_ = operNode->key;
		ret = syncAction_->executeAction(diffNode, nextOperNode_);

		return ret;
	}

	virtual void notifyDiff()
	{
		notifyLeft();
		if((!userContext_->getDataBaseMgr()->getDiffTable()->isNeedRefresh())||isStart_)
		{
			boost::this_thread::sleep(boost::posix_time::seconds(1));
			return;
		}

		DiffPathNodes diffInfos;
		refreshDiffPath(diffInfos);

		for(DiffPathNodes::const_iterator it = diffInfos.begin(); it != diffInfos.end(); ++it)
		{
			userContext_->getOverlayIconMgr()->refreshOverlayIcon(it->get()->localPath);
		}

		userContext_->getOverlayIconMgr()->notifyOverlayIcons();
	}

private:
	void checkTask()
	{
		//check running diff
		int64_t downloadSize = 0;
		int64_t uploadSize = 0;
		if(RT_OK==userContext_->getAsyncTaskMgr()->getTransSize(downloadSize, uploadSize))
		{
			taskCheckCnt_ = 0;
			if((downloadSize!=runningDownload_)||(uploadSize!=runningUpload_))
			{
				runningDownload_ = downloadSize;
				runningUpload_ = uploadSize;
			}
		}
		else
		{
			++taskCheckCnt_;
			if(taskCheckCnt_ > 600)
			{
				userContext_->getDataBaseMgr()->getDiffTable()->restoreRunningTask();
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
		userContext_->getDataBaseMgr()->getDiffTable()->getIncDiff(lastAutoInc, maxAutoInc, diffInfos);

		if(maxAutoInc <= lastAutoInc)
		{
			return;
		}

		//get path
		for(DiffPathNodes::iterator it = diffInfos.begin(); it != diffInfos.end(); ++it)
		{
			if(Key_LocalID==it->get()->keyType)
			{
				it->get()->localPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(it->get()->key);
				if(it->get()->localPath.empty())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "get local path failed. id: %I64d", it->get()->key);
					boost::this_thread::sleep(boost::posix_time::seconds(1));
					return;
				}
				/*
				if(0 == it->get()->size)
				{
					//get size
					it->get()->size = SD::Utility::FS::get_file_size(it->get()->localPath);
				}
				*/
			}
			else if(Key_RemoteID==it->get()->keyType)
			{
				it->get()->remotePath = userContext_->getDataBaseMgr()->getRemoteTable()->getPath(it->get()->key);
				if(it->get()->remotePath.empty())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "get remote path failed. id: %I64d", it->get()->key);
					boost::this_thread::sleep(boost::posix_time::seconds(1));
					return;
				}
				int64_t localId = INVALID_ID;
				if(RT_OK == userContext_->getDataBaseMgr()->getRelationTable()->getLocalIdByRemoteId(it->get()->key, localId))
				{
					it->get()->localPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(localId);
				}
				else
				{
					it->get()->localPath = localRoot_ + it->get()->remotePath;
				}
			}
		}

		//replace path info
		userContext_->getDataBaseMgr()->getDiffTable()->replaceIncPath(diffInfos);
		iniHelper.SetValue<int64_t>(L"", CONF_AUTOINC_ID, maxAutoInc);
	}

	void notifyLeft()
	{
		int32_t cnt = userContext_->getDataBaseMgr()->getDiffTable()->getNormalDiffCount() + 
			userContext_->getDataBaseMgr()->getTransTaskTable()->getAutoTransTaskCnt();

		isBusy_ = cnt>BUSY_DIFFCNT_LIMEN?true:false;

		int32_t failedCnt = 0;
		//when the diffcnt is 0 and task is running,is shown as 1.
		if(0==cnt)
		{
			if(userContext_->getDataBaseMgr()->getDiffTable()->getDiffCnt() > 0)
			{
				cnt = userContext_->getDataBaseMgr()->getTransTaskTable()->getCount(ATS_Running);
			}
			failedCnt = userContext_->getDataBaseMgr()->getDiffTable()->getErrorDiffCount();
		}

		//if (0==cnt)
		//{
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_DIFF_CNT, 
			SD::Utility::String::format_string(L"%d", cnt),
			SD::Utility::String::format_string(L"%d", failedCnt), 
			SD::Utility::String::format_string(L"%d", 0),
			SD::Utility::String::format_string(L"%d", 0)));
		//	return;
		//}
		/*
		int64_t runningDownload = 0;
		int64_t runningUpload = 0;
		if(RT_OK==userContext_->getAsyncTaskMgr()->getTransSize(runningDownload, runningUpload))
		{
			if((runningDownload!=runningDownload_)||(runningUpload!=runningUpload_))
			{
				runningDownload_ = runningDownload;
				runningUpload_ = runningUpload;
			}
		}

		int64_t uploadSize = 0;
		int64_t downloadSize = 0;
		userContext_->getDataBaseMgr()->getDiffTable()->getSizeInfo(uploadSize, downloadSize);

		if(uploadSize > runningUpload_)
		{
			uploadSize = uploadSize - runningUpload_;
		}
		if(downloadSize > runningDownload_)
		{
			downloadSize = downloadSize - runningDownload_;
		}

		int64_t timeLeft = 0;
		//int64_t timeLeft = cnt * userContext_->getSyncTimeCalc()->getDiffCntSpeed() 
		//	+ uploadSize/userContext_->getSyncTimeCalc()->getUploadSpeed()
		//	+ downloadSize/userContext_->getSyncTimeCalc()->getDownloadSpeed();

		//SERVICE_DEBUG(MODULE_NAME, RT_OK, "notifyTimeLeft: %I64d = %d * %d + %I64d / %d + %I64d / %d", 
		//	timeLeft, cnt, userContext_->getSyncTimeCalc()->getDiffCntSpeed(),
		//	uploadSize, userContext_->getSyncTimeCalc()->getUploadSpeed(), 
		//	downloadSize, userContext_->getSyncTimeCalc()->getDownloadSpeed());

		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_DIFF_CNT, 
				SD::Utility::String::format_string(L"%d", cnt), 
				SD::Utility::String::format_string(L"%I64d", timeLeft),
				SD::Utility::String::format_string(L"%I64d", int64_t(uploadSize + downloadSize))));
				*/
	}

	int32_t getNextOperNode(OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;
		if(INVALID_ID == nextOperNode_->key)
		{
			isTaskIdle_ = userContext_->getAsyncTaskMgr()->isIdle();
			ret = userContext_->getDataBaseMgr()->getDiffTable()->getTopOper(nextOperNode, isTaskIdle_);
		}
		else
		{
			nextOperNode->key = nextOperNode_->key;
			nextOperNode->keyType = nextOperNode_->keyType;
			OperNodes operList;
			userContext_->getDataBaseMgr()->getDiffTable()->geOperList(nextOperNode->key, nextOperNode->keyType, operList);
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

	int32_t asyncTransTaskCallback(CISSPTaskPtr ptrTask, AsyncTaskMgr::CallbackType type)
	{
		int32_t ret = RT_OK;
		TransTask* asyncTransTask = static_cast<TransTask*>(ptrTask.get());
		assert(NULL != asyncTransTask);
		AsyncTransTaskNode& transTaskNode = asyncTransTask->getTaskNode();

		//task complete
		if(AsyncTaskMgr::CT_UpdateNode == type)
		{
			FILE_DIR_INFO* fileDirInfo = static_cast<FILE_DIR_INFO*>(asyncTransTask->getCustomInfo().content);
			//upload success
			if(Key_LocalID == transTaskNode.id.type)
			{
				boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());

				RemoteNode remoteNode(new st_RemoteNode);
				remoteNode->id = fileDirInfo->id;
				if(RT_OK==userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
				{
					remoteNode->version = fileDirInfo->version;
				}
				else
				{
					remoteNode->parent = fileDirInfo->parent;
					remoteNode->name = fileDirInfo->name;
					remoteNode->type = fileDirInfo->type;
					remoteNode->status = RS_Sync_Status;
					remoteNode->version = fileDirInfo->version;
				}

				CHECK_RESULT(userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNode(remoteNode));
				// when complete upload file, update the LocalTable's modify time and create time
				// for local full detect
				int64_t localId = Utility::String::string_to_type<int64_t>(transTaskNode.id.id);
				(void)userContext_->getDataBaseMgr()->getLocalTable()->updateTimeInfo(localId, fileDirInfo->ctime, fileDirInfo->mtime);
				
				//add the special suffix to upload table 
				if(userContext_->getFilterMgr()->isUploadFilter(remoteNode->name))
				{
					userContext_->getDataBaseMgr()->getUploadTable()->replaceFilterInfo(localId);
				}				

				//change remote id
				int64_t oldRemoteId = INVALID_ID;
				if(RT_OK==userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localId, oldRemoteId))
				{
					if(oldRemoteId!=remoteNode->id)
					{
						userContext_->getDataBaseMgr()->getRelationTable()->deleteByRemoteId(oldRemoteId);
						if(Sync_All==userContext_->getConfigureMgr()->getConfigure()->syncModel())
						{
							userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_RemoteID, oldRemoteId);
							OperNode operNode(new st_OperNode);
							operNode->keyType = Key_RemoteID;
							operNode->key = oldRemoteId;
							operNode->oper = OT_Created;
							operNode->size = 0;
							userContext_->getDataBaseMgr()->getDiffTable()->addOper(operNode);
						}
					}
				}

				ret = userContext_->getDataBaseMgr()->getRelationTable()->addRelation(remoteNode->id, localId);
				return ret;
			}
			//download success
			else if(Key_RemoteID == transTaskNode.id.type)
			{
				boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->localMutex());

				int64_t parentId = Utility::String::string_to_type<int64_t>(transTaskNode.parent);

				LocalNode node(new st_LocalNode);
				node->id = fileDirInfo->id;
				node->parent = parentId;
				node->name = transTaskNode.name;
				node->type = fileDirInfo->type;
				node->ctime = fileDirInfo->ctime;
				node->mtime = fileDirInfo->mtime;
				node->status = LS_Normal;

				CHECK_RESULT(userContext_->getDataBaseMgr()->getLocalTable()->replaceNode(node));
				CHECK_RESULT(userContext_->getDataBaseMgr()->getRelationTable()->addRelation(
					Utility::String::string_to_type<int64_t>(transTaskNode.id.id), node->id));
			}
		}
		else
		{
			KeyType keyType = (KeyType)transTaskNode.id.type;
			int64_t key = Utility::String::string_to_type<int64_t>(transTaskNode.id.id);
			bool hasNewOper = false;
			IdList diffIdList;
			userContext_->getDataBaseMgr()->getDiffTable()->getRunningDiff(keyType, key, diffIdList, hasNewOper);

			if(asyncTransTask->IsCompletedWithSuccess())
			{
				if(!hasNewOper)
				{
					userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(keyType, key);
				}
				else
				{
					userContext_->getDataBaseMgr()->getDiffTable()->refreshDiff(diffIdList, Diff_Complete);
				}
			}
			else if(asyncTransTask->IsCompletedWithCancel())
			{
				userContext_->getDataBaseMgr()->getDiffTable()->refreshDiff(diffIdList, Diff_Normal);
			}
			else
			{
				userContext_->getDataBaseMgr()->getDiffTable()->refreshDiff(diffIdList, Diff_Failed);
				userContext_->getDataBaseMgr()->getDiffTable()->updateErrorCode(keyType, key, asyncTransTask->GetErrorCode());
			}
		}
		return RT_OK;
	}

private:
	UserContext* userContext_;
	LocalDetector* localDetector_;
	RemoteDetector* remoteDetector_;
	std::auto_ptr<SyncAction> syncAction_;
	//int64_t timeCntDiffId_;
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

std::auto_ptr<DiffProcessor> DiffProcessor::create(UserContext* userContext, 
												   LocalDetector* localDetector, 
												   RemoteDetector* remoteDetector)
{
	return std::auto_ptr<DiffProcessor>(
		static_cast<DiffProcessor*>(new DiffProcessorImpl(userContext, localDetector, remoteDetector)));
}
