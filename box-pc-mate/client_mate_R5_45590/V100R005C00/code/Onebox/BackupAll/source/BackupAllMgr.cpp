#include "BackupAllMgr.h"
#include "BackupAllCommon.h"
#include "BackupAllDbMgr.h"
#include "Utility.h"
#include "BackupAllProcess.h"
#include "BackupAllScan.h"
#include "NotifyMgr.h"
#include "NotifyMsg.h"
#include "ConfigureMgr.h"
#include "BackupAllTransTask.h"
#include "ShareResMgr.h"
#include "BackupAllLocalFile.h"
#include "AsyncTaskMgr.h"
#include "WorkModeMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "BackupAllUtility.h"
#include "BackupAllFilterMgr.h"
#include <boost/thread.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllMgr")
#endif

class BackupAllMgrImpl : public BackupAllMgr
{
public:
	BackupAllMgrImpl(UserContext* userContext):userContext_(userContext)
	{
		std::wstring logFile = Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"BackupAll.log";
		ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"), TP_FILE, Utility::String::wstring_to_string(logFile));
		BackupAllTransTask::getInstance(userContext_)->setTransmitNotify();
		pBackupAllProcess_ = BackupAllProcess::create(userContext_);
		pBackupAllScan_ = BackupAllScan::create(userContext_);
		isFlush_ = false;
		isNeedRestart_ = false;
		isFirstTime_ = false;
		startTime_ = -1;
		lastOffset_ = 0;

		start();
	}

	virtual ~BackupAllMgrImpl()
	{
		try
		{
			stop(false);
			ISSP_LogExit();
		}
		catch(...) {}	
	}

	virtual int32_t stop(bool async = true)
	{
		if(BATS_Running==taskInfo_.status && -1!=startTime_)
		{
			addRunTime(true);
		}
		backupAllThread_.interrupt();
		backupAllThread_.join();
		pBackupAllScan_->stop(async);
		pBackupAllProcess_->stop(async);
		return RT_OK;	
	}

	virtual int32_t setBackupTask(const std::list<std::wstring>& rootList, const std::list<std::wstring>& filterList,
		const int32_t backupType, const std::wstring& period, const std::wstring& rootName)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "setBackupTask");
		int32_t ret = RT_OK;

		//1.check the local path
		if(rootList.empty())
		{
			closeBackupTask();
			return RT_OK;
		}

		BATaskNode baTaskNode;
		bool isNeedStart = false;
		if(RT_SQLITE_NOEXIST==BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getNode(baTaskNode))
		{
			BATaskLocalNode localNode;
			if(rootName.empty())
			{
				HSLOG_ERROR(MODULE_NAME, ret, "rootName is empty.");
				return RT_ERROR;
			}
			else
			{
				localNode.baseInfo.path = rootName;
			}
			std::auto_ptr<BackupAllLocalFile> pLocalFile = BackupAllLocalFile::create(userContext_);
			ret = pLocalFile->create(0, localNode);
			if(RT_OK!=ret)
			{
				return ret;
			}
			baTaskNode.remoteId = localNode.remoteId;
			ret = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->addNode(baTaskNode);
			if(ret != RT_OK)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "add task info failed.");
				return ret;
			}
		}

		if(BATS_Stop==baTaskNode.status)
		{
			baTaskNode.status = BATS_Running;
			baTaskNode.firstStartTime = time(NULL);
			baTaskNode.curStartTime = time(NULL);
			baTaskNode.curRunTime = 0;
			ret = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setStartInfo(baTaskNode);
			taskInfo_.status = baTaskNode.status;
			if(ret != RT_OK)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "setStartInfo failed.");
				return ret;
			}
			isNeedStart = true;
		}

		baTaskNode.type = (BATaskType)backupType;
		baTaskNode.userDefine = period;
		BackupAll::pasreNextRunTime(backupType, period, baTaskNode.nextStartTime);
		ret = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setPeriodInfo(baTaskNode);
		if(ret != RT_OK)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "setPeriodInfo failed.");
			return ret;
		}

		ret = flushPathInfo(rootList, filterList);
		if(ret != RT_OK)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "flushPathInfo failed.");
			return ret;
		}

		if(isNeedStart)
		{
			start();
		}
		return RT_OK;
	}

	virtual int32_t closeBackupTask()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "closeBackupTask");
		stop();
		taskInfo_.status = BATS_Stop;
		userContext_->getAsyncTaskMgr()->pauseTask(BACKUPALL_GROUPID);
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->deleteAllPathInfo();
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Stop);
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		return RT_OK;
	}

	virtual int32_t pauseBackupTask()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "pauseBackupTask");
		stop();
		taskInfo_.status = BATS_Pausing;
		userContext_->getAsyncTaskMgr()->pauseTask(BACKUPALL_GROUPID);
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Pausing);
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		return RT_OK;
	}

	virtual int32_t resumeBackupTask()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "resumeBackupTask");
		taskInfo_.status = BATS_Running;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Running);
		start();
		userContext_->getAsyncTaskMgr()->resumeTask(BACKUPALL_GROUPID);
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		return RT_OK;
	}

	virtual int32_t restartBackupTask()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "restartBackupTask");
		taskInfo_.status = BATS_Running;
		taskInfo_.curCnt = 0;
		taskInfo_.curSize = 0;
		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
		for(std::map<std::wstring, int64_t>::iterator itV = volumeInfo.begin(); itV != volumeInfo.end(); ++itV)
		{
			BATaskInfo volumeTaskInfo;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(itV->first)->initIncInfo();
		}

		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Running);
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		return RT_OK;
	}

	virtual int32_t getFailedList(BATaskLocalNodeList& failedList, int32_t offset, int32_t limit)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("getFailedList offset:%d, limit:%d", offset, limit));
		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
		int32_t leftLimit = limit;
		for(std::map<std::wstring, int64_t>::iterator itV = volumeInfo.begin(); itV != volumeInfo.end(); ++itV)
		{
			BATaskInfo volumeTaskInfo;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(itV->first)->getFailedNodes(failedList, offset, leftLimit);
			
			if(-1!=limit)
			{
				if(int32_t(failedList.size())<limit)
				{
					leftLimit = limit - failedList.size();
				}
				else
				{
					break;
				}
			}
		}
		if(0==failedList.size()&&BATS_Failed==taskInfo_.status)
		{
			taskInfo_.status = BATS_Complete;
			taskInfo_.failedCnt = 0;
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Complete);
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		}
		if(-1==limit && taskInfo_.failedCnt != failedList.size())
		{
			taskInfo_.failedCnt = failedList.size();
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		}
		return RT_OK;
	}

	virtual int32_t ignoreFailedNode(const BATaskLocalNode& failedNode) 
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("ignoreFailedNode path:%s", 
			SD::Utility::String::wstring_to_string(failedNode.baseInfo.path).c_str()));
		BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(failedNode.baseInfo.path)->ignoreError(failedNode.baseInfo.localId);
		userContext_->getAsyncTaskMgr()->deleteErrorTask(BACKUPALL_GROUPID, failedNode.baseInfo.path);
		if(taskInfo_.failedCnt > 1)
		{
			--taskInfo_.failedCnt;
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
			return RT_OK;
		}

		if(1==taskInfo_.failedCnt)
		{
			--taskInfo_.failedCnt;
			taskInfo_.status = BATS_Complete;
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(BATS_Complete);
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
		}
		return RT_OK;
	}

	virtual BATaskInfo* getTaskInfo()
	{
		return &taskInfo_;
	}

	virtual int32_t getPathInfo(std::set<std::wstring>& selectPath, std::set<std::wstring>& filterPath)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "getPathInfo");
		pBackupAllScan_->flushSelectInfo();
		return BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(selectPath, filterPath);
	}

	virtual void setDefault(const std::wstring& rootName)
	{
		rootName_ = rootName;
	}

	virtual bool isUpdate()
	{
		return isFirstTime_;
	}

private:
	int32_t start()
	{
		// init thread pool
		backupAllThread_ = boost::thread(boost::bind(&BackupAllMgrImpl::backupProcess, this));
		return RT_OK;
	}

	int32_t flushPathInfo(const std::list<std::wstring>& rootList, const std::list<std::wstring>& filterList)
	{
		std::list<std::wstring> newRootList = rootList;
		std::list<std::wstring> newFilterList = filterList;
		std::set<std::wstring> oldRootList;
		std::set<std::wstring> oldFilterList;
		std::set<std::wstring> newVolumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(oldRootList, oldFilterList);
		std::auto_ptr<BackupAllFilterMgr> pFilterMgr = BackupAllFilterMgr::create(userContext_, newRootList, newFilterList);
		for(std::list<std::wstring>::iterator itR = newRootList.begin(); itR != newRootList.end();)
		{
			if(itR->length()>3)
			{
				if(!pFilterMgr->isFilter(SD::Utility::FS::get_parent_path(*itR)))
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s is redundance.", SD::Utility::String::wstring_to_string(*itR).c_str());
					itR = newRootList.erase(itR);
					continue;
				}
			}
			newVolumeInfo.insert((*itR).substr(0,2));
			if(oldRootList.end()!=oldRootList.find(*itR))
			{
				oldRootList.erase(*itR);
				itR = newRootList.erase(itR);
			}
			else
			{
				++itR;
			}
		}

		for(std::list<std::wstring>::iterator itF = newFilterList.begin(); itF != newFilterList.end();)
		{
			if(itF->length()>3)
			{
				if(pFilterMgr->isFilter(SD::Utility::FS::get_parent_path(*itF)))
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s is redundance.", SD::Utility::String::wstring_to_string(*itF).c_str());
					itF = newFilterList.erase(itF);
					continue;
				}
			}
			if(oldFilterList.end()!=oldFilterList.find(*itF))
			{
				oldFilterList.erase(*itF);
				itF = newFilterList.erase(itF);
			}
			else
			{
				++itF;
			}
		}
		
		if(newRootList.empty()&&newFilterList.empty()&&oldRootList.empty()&&oldFilterList.empty())
		{
			HSLOG_TRACE(MODULE_NAME, RT_OK, "select path no change.");
			return RT_OK;
		}
		BAScanStatus scanStatus = BASS_FullScanning;
		if(BATS_Complete==taskInfo_.status||BATS_Failed==taskInfo_.status)
		{
			scanStatus = BASS_Scanning;
		}
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updatePathInfo(newRootList, newFilterList, oldRootList, oldFilterList);
		std::map<std::wstring, int64_t> oldVolumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(oldVolumeInfo);
		for(std::set<std::wstring>::iterator it = newVolumeInfo.begin(); it != newVolumeInfo.end(); ++it)
		{
			std::map<std::wstring, int64_t>::iterator itO = oldVolumeInfo.find(*it);
			if(itO!=oldVolumeInfo.end())
			{
				oldVolumeInfo.erase(itO);
			}
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(*it)->updateScanStatus(scanStatus);

			std::list<std::wstring> checkPaths;
			for(std::list<std::wstring>::iterator itNR = newRootList.begin(); itNR != newRootList.end();++itNR)
			{
				if(std::wstring::npos!=itNR->find(*it))
				{
					checkPaths.push_back(*itNR);
				}
			}
			for(std::set<std::wstring>::iterator itOF = oldFilterList.begin(); itOF != oldFilterList.end();++itOF)
			{
				if(std::wstring::npos!=itOF->find(*it))
				{
					checkPaths.push_back(*itOF);
				}
			}
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(*it)->addCheckPath(checkPaths);
		}
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateVolumeInfo(newVolumeInfo, oldVolumeInfo);

		//停掉后由任务线程重新拉起
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateFilterChange(true);
		pBackupAllScan_->stop();
		pBackupAllProcess_->stop();
		isNeedRestart_ = true;
		if(BATS_Pausing!=taskInfo_.status)
		{
			taskInfo_.status = BATS_Running;
		}
		return RT_OK;
	}

	int32_t startFirstTime(BATaskNode& baTaskNode)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "startFirstTime");
		isFirstTime_ = true;
		if(rootName_.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "rootName is empty.");
			return RT_ERROR;
		}
		BATaskLocalNode localNode;
		localNode.baseInfo.path = rootName_;

		std::auto_ptr<BackupAllLocalFile> pLocalFile = BackupAllLocalFile::create(userContext_);
		int32_t ret = pLocalFile->create(0, localNode);
		if(RT_OK!=ret)
		{
			return ret;
		}
		baTaskNode.remoteId = localNode.remoteId;
		ret = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateRemoteId(baTaskNode.remoteId);
		if(ret != RT_OK)
		{
			return ret;
		}

		baTaskNode.status = BATS_Running;
		baTaskNode.firstStartTime = time(NULL);
		baTaskNode.curStartTime = time(NULL);
		baTaskNode.curRunTime = 0;
		ret = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setStartInfo(baTaskNode);

		initTaskInfo();
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));

		return ret;
	}

	void backupProcess()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "backupProcess");
		
		try
		{
			initTaskInfo();

			BATaskNode baTaskNode;
			if(RT_OK!=BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getNode(baTaskNode)
				||BATS_Stop==baTaskNode.status)
			{
				return;
			}

			if(-1==baTaskNode.remoteId)
			{
				if(RT_OK!=startFirstTime(baTaskNode))
				{
					return;
				}
			}

			while(true)
			{
				if(BATS_Running!=taskInfo_.status)
				{
					while((BATS_Running!=taskInfo_.status) && (!isNeedRestart_) && (baTaskNode.nextStartTime > time(NULL)))
					{
						boost::this_thread::interruption_point();
						boost::this_thread::sleep(boost::posix_time::milliseconds(100));
					}
					
					//增量为0时，不启动处理
					if((BATS_Running!=taskInfo_.status) 
						&& (!isNeedRestart_) 
						&& BATS_Complete==baTaskNode.status
						&& baTaskNode.nextStartTime <= time(NULL))
					{
						pBackupAllScan_->start();
						while(pBackupAllScan_->isScanning())
						{
							boost::this_thread::interruption_point();
							boost::this_thread::sleep(boost::posix_time::milliseconds(100));
							if(BATS_Running==taskInfo_.status || isNeedRestart_)
							{
								break;
							}
						}
						initTaskInfo();
						if(!pBackupAllScan_->isScanning() && 0==taskInfo_.leftCnt)
						{
							BackupAll::pasreNextRunTime(baTaskNode.type, baTaskNode.userDefine, baTaskNode.nextStartTime);
							BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setPeriodInfo(baTaskNode);
							std::map<std::wstring, int64_t> volumeInfo;
							BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
							for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
							{
								BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->updateScanStatus(BASS_Init);
							}
							continue;
						}
					}

					baTaskNode.status = BATS_Running;
					baTaskNode.curStartTime = time(NULL);
					baTaskNode.curRunTime = 0;
					BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setStartInfo(baTaskNode);
					taskInfo_.curStartTime = baTaskNode.curStartTime;
					taskInfo_.status = BATS_Running;
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
				}
				taskInfo_.curRunTime = baTaskNode.curRunTime;
				startTime_ = time(NULL);
				lastOffset_ = 0;
				pBackupAllProcess_->start(taskInfo_.failedCnt>0);
				pBackupAllScan_->start();
				isNeedRestart_ = false;
				WorkMode lastWorkMode = WorkMode_Online;	//默认在线
				int64_t lastOffLineTime = 0;
				do
				{
					WorkMode curWorkMode = userContext_->getWorkmodeMgr()->getWorkMode();
					if(WorkMode_Offline==curWorkMode)
					{
						if(lastWorkMode!=curWorkMode)
						{
							taskInfo_.status = BATS_Offline;
							userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
							pBackupAllProcess_->stop();
							lastWorkMode = curWorkMode;
							HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "goto offline.");
							lastOffLineTime = time(NULL);
						}
						boost::this_thread::interruption_point();
						boost::this_thread::sleep(boost::posix_time::seconds(1));
						continue;
					}
					else if(WorkMode_Online==curWorkMode && lastWorkMode!=curWorkMode)
					{
						taskInfo_.status = BATS_Running;
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
						pBackupAllProcess_->start(false);
						lastWorkMode = curWorkMode;
						HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "goto online.");
						startTime_ = startTime_ + time(NULL) - lastOffLineTime;	//离线时间不算入运行时间内
					}

					boost::this_thread::interruption_point();
					boost::this_thread::sleep(boost::posix_time::seconds(3));
					if(isNeedRestart_)
					{
						pBackupAllScan_->start();
						pBackupAllProcess_->start(false);
						isNeedRestart_ = false;
					}
					flushTaskInfo();
				}
				while(pBackupAllScan_->isScanning()||taskInfo_.leftCnt>0||BATS_Offline==taskInfo_.status);
				
				addRunTime(true);
				startTime_ = -1;

				BackupAll::pasreNextRunTime(baTaskNode.type, baTaskNode.userDefine, baTaskNode.nextStartTime);
				BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->setPeriodInfo(baTaskNode);
				std::map<std::wstring, int64_t> volumeInfo;
				BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
				for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
				{
					BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->updateScanStatus(BASS_Init);
				}

				if(0==taskInfo_.totalCnt)
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "totalCnt is error.");
					continue;
				}
				baTaskNode.status = (taskInfo_.failedCnt>0)?BATS_Failed:BATS_Complete;
				BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateStatus(baTaskNode.status);

				initTaskInfo();
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
			}
		}
		catch(boost::thread_interrupted)
		{
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "backup all process thread interrupted.");
		}
	}

	void initTaskInfo()
	{
		BATaskNode baTaskNode;
		(void)BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getNode(baTaskNode);
		taskInfo_.status = baTaskNode.status;
		taskInfo_.remoteId = baTaskNode.remoteId;

		FILE_DIR_INFO info;
		Path path = userContext_->getPathMgr()->makePath();
		path.id(baTaskNode.remoteId);
		userContext_->getSyncFileSystemMgr()->getProperty(path, info, ADAPTER_FOLDER_TYPE_REST);
		taskInfo_.remotePath = info.name;
		taskInfo_.curRunTime = baTaskNode.curRunTime;
		taskInfo_.curStartTime = baTaskNode.curStartTime;
		taskInfo_.totalDay = int32_t((time(NULL) - baTaskNode.firstStartTime)/86400) + 1;
		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
		BATaskInfo tempTaskInfo;
		for(std::map<std::wstring, int64_t>::iterator itV = volumeInfo.begin(); itV != volumeInfo.end(); ++itV)
		{
			BATaskInfo volumeTaskInfo;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(itV->first)->getTotalInfo(volumeTaskInfo, true);
			tempTaskInfo.totalSize += volumeTaskInfo.totalSize;
			tempTaskInfo.totalCnt += volumeTaskInfo.totalCnt;
			tempTaskInfo.leftSize += volumeTaskInfo.leftSize;
			tempTaskInfo.leftCnt += volumeTaskInfo.leftCnt;
			tempTaskInfo.failedCnt += volumeTaskInfo.failedCnt;
			tempTaskInfo.curCnt += volumeTaskInfo.curCnt;
			tempTaskInfo.curSize += volumeTaskInfo.curSize;
		}
		taskInfo_.totalSize = tempTaskInfo.totalSize;
		taskInfo_.totalCnt = tempTaskInfo.totalCnt;
		taskInfo_.leftSize = tempTaskInfo.leftSize;
		taskInfo_.leftCnt = tempTaskInfo.leftCnt;
		taskInfo_.failedCnt = tempTaskInfo.failedCnt;
		taskInfo_.curCnt = tempTaskInfo.curCnt;
		taskInfo_.curSize = tempTaskInfo.curSize;

		taskInfo_.leftTime = -1;
		taskInfo_.transSize = 0;
		taskInfo_.curUpload = L"";
		timeInfos_.clear();
	}

	void flushTaskInfo()
	{
		addRunTime();

		if(taskInfo_.remotePath.empty())
		{
			BATaskNode baTaskNode;
			(void)BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getNode(baTaskNode);
			taskInfo_.remoteId = baTaskNode.remoteId;
			FILE_DIR_INFO info;
			Path path = userContext_->getPathMgr()->makePath();
			path.id(baTaskNode.remoteId);
			userContext_->getSyncFileSystemMgr()->getProperty(path, info, ADAPTER_FOLDER_TYPE_REST);
			taskInfo_.remotePath = info.name;
		}

		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
		BATaskInfo tempTaskInfo;
		for(std::map<std::wstring, int64_t>::iterator itV = volumeInfo.begin(); itV != volumeInfo.end(); ++itV)
		{
			BATaskInfo volumeTaskInfo;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(itV->first)->getTotalInfo(volumeTaskInfo, isFlush_);
			tempTaskInfo.totalSize += volumeTaskInfo.totalSize;
			tempTaskInfo.totalCnt += volumeTaskInfo.totalCnt;
			tempTaskInfo.leftSize += volumeTaskInfo.leftSize;
			tempTaskInfo.leftCnt += volumeTaskInfo.leftCnt;
			tempTaskInfo.failedCnt += volumeTaskInfo.failedCnt;
			tempTaskInfo.curCnt += volumeTaskInfo.curCnt;
			tempTaskInfo.curSize += volumeTaskInfo.curSize;
		}
		taskInfo_.totalSize = tempTaskInfo.totalSize;
		taskInfo_.totalCnt = tempTaskInfo.totalCnt;
		taskInfo_.leftSize = tempTaskInfo.leftSize;
		taskInfo_.leftCnt = tempTaskInfo.leftCnt;
		taskInfo_.failedCnt = tempTaskInfo.failedCnt;
		taskInfo_.curCnt = tempTaskInfo.curCnt;
		taskInfo_.curSize = tempTaskInfo.curSize;

		//补正传输部分数据
		taskInfo_.leftSize = taskInfo_.leftSize - taskInfo_.transSize;
		if(taskInfo_.leftSize < 0) taskInfo_.leftSize = 0;

		std::stringstream printInfo;
		setLeftTime(volumeInfo, printInfo);
		printInfo << "leftTime:" << taskInfo_.leftTime;
		HSLOG_TRACE(MODULE_NAME, RT_OK, "%s", printInfo.str().c_str());

		//通知界面
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE));
	}

	void addRunTime(bool writeDb = false)
	{
		int64_t offset = time(NULL) - startTime_;
		taskInfo_.curRunTime += (offset-lastOffset_);
		lastOffset_ = offset;
		if(offset>60||writeDb)
		{
			startTime_ = time(NULL);
			lastOffset_ = 0;
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->addRunTime(offset);
		}
	}

	void setLeftTime(const std::map<std::wstring, int64_t>& volumeInfo, std::stringstream& printInfo)
	{
		TimeInfo endTime;
		endTime.transCnt = taskInfo_.totalCnt - taskInfo_.leftCnt;
		endTime.transSize = taskInfo_.totalSize - taskInfo_.leftSize;
		endTime.curRunTime = taskInfo_.curRunTime;
		printInfo << "curRunTime:" << endTime.curRunTime << ","
			<< "transCnt:" << endTime.transCnt << ","
			<< "transSize:" << endTime.transSize << ",";

		//计算无传输进度的时间长度
		int64_t noChangeTime = 0;
		for(TimeInfoList::const_reverse_iterator it = timeInfos_.rbegin(); it!= timeInfos_.rend(); ++it)
		{
			if(it->transCnt==endTime.transCnt && it->transSize==endTime.transSize)
			{
				noChangeTime = endTime.curRunTime - it->curRunTime;
			}
			else
			{
				break;
			}
		}
		
		//上个传输任务完成后3秒内没有新的传输任务，则清除正在上传的路径信息
		if(noChangeTime>3)
		{
			printInfo << "noChangeTime:" << noChangeTime << ",";
			if(!taskInfo_.curUpload.empty()&&0==taskInfo_.transSize)
			{
				taskInfo_.curUpload = L"";
			}
		}
		//6秒内无数据变化则刷新统计信息
		isFlush_ = (noChangeTime>6);

		//60秒内无数据变化则重启传输任务
		if(noChangeTime>60)
		{
			if(userContext_->getAsyncTaskMgr()->getTasksCount(BACKUPALL_GROUPID, ATS_Waiting)<=0)
			{
				//恢复所有uploading的数据，重新传输
				for(std::map<std::wstring, int64_t>::const_iterator itV = volumeInfo.begin(); itV != volumeInfo.end(); ++itV)
				{
					BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(itV->first)->resumeUpload();
				}
				timeInfos_.clear();
				return;
			}
		}

		timeInfos_.push_back(endTime);
		if(timeInfos_.size()>60)	//取样时间保持60*3秒=3分钟左右
		{
			timeInfos_.pop_front();
		}

		if(taskInfo_.curRunTime < 5 || taskInfo_.totalSize <= taskInfo_.leftSize)
		{
			taskInfo_.leftTime = -1;
			return;
		}
		printInfo << "timeInfos size:" << timeInfos_.size() << ",";
		if(timeInfos_.size()>1)
		{
			TimeInfo startTime = timeInfos_.front();
			int64_t offSet = endTime.curRunTime-startTime.curRunTime;
			//printInfo << "offSetTime:" << offSet << ",";
			if(0==offSet) return;
			int64_t offSetSize = endTime.transSize-startTime.transSize;
			//printInfo << "offSetSize:" << offSetSize << ",";
			if(offSetSize<0)
			{
				timeInfos_.clear();
				return;
			}
			if(0==offSetSize)
			{
				taskInfo_.leftTime = taskInfo_.curRunTime * taskInfo_.leftSize / (taskInfo_.totalSize - taskInfo_.leftSize);
				return;
			}

			int32_t offSetCnt = endTime.transCnt - startTime.transCnt;
			//printInfo << "offSetCnt:" << offSetCnt << ",";
			if(offSetSize>10485760*offSet)
			{
				offSetSize = 10485760*offSet;	//传输速度大于10M/s时，推测存在秒传，将速度转为10M/s，避免预估时间过小。
			}
			if(offSetCnt>0 && taskInfo_.leftCnt>0) 
			{
				int64_t offAverage = offSetSize/offSetCnt;
				int64_t leftAverage = taskInfo_.leftSize/taskInfo_.leftCnt;
				if(0==offAverage) offAverage = 1;
				if(0==leftAverage) leftAverage = 1;
				if(taskInfo_.leftSize>=offSetSize)
				{
					taskInfo_.leftTime = (taskInfo_.leftSize/offSetSize)*offSet*(offAverage<1048576?offAverage:1048576)/(leftAverage<1048576?leftAverage:1048576);
				}
				else
				{
					//1048576=1MB
					taskInfo_.leftTime = (offAverage<1048576?offAverage:1048576)*taskInfo_.leftSize*offSet/(offSetSize*(leftAverage<1048576?leftAverage:1048576));
				}
				return;
			}
			taskInfo_.leftTime = taskInfo_.leftSize*offSet/offSetSize;
			return;
		}
		taskInfo_.leftTime = taskInfo_.curRunTime * taskInfo_.leftSize / (taskInfo_.totalSize - taskInfo_.leftSize);
	}

private:
	UserContext* userContext_;
	std::auto_ptr<BackupAllScan> pBackupAllScan_;
	std::auto_ptr<BackupAllProcess> pBackupAllProcess_;
	boost::thread backupAllThread_;
	int64_t startTime_;
	int64_t lastOffset_;
	TimeInfoList timeInfos_;
	bool isFlush_;
	bool isNeedRestart_;
	std::wstring rootName_;
	bool isFirstTime_;

	BATaskInfo taskInfo_;
};

BackupAllMgr* BackupAllMgr::instance_ = NULL;

BackupAllMgr* BackupAllMgr::getInstance(UserContext* userContext)
{
	if (NULL == instance_)
	{
		instance_ = static_cast<BackupAllMgr*>(new BackupAllMgrImpl(userContext));
	}
	return instance_;
}

void BackupAllMgr::releaseInstance()
{
	if (NULL != instance_)
	{
		delete instance_;
		instance_ = NULL;
	}
}