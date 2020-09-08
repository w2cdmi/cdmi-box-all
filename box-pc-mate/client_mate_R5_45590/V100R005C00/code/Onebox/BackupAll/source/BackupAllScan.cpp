#include "BackupAllScan.h"
#include "BackupAllIncScan.h"
#include "BackupAllFullScan.h"
#include "BackupAllDbMgr.h"
#include "AsyncTaskMgr.h"
#include "BackupAllFilterMgr.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllScan")
#endif

class BackupAllScanImpl : public BackupAllScan
{
public:
	BackupAllScanImpl(UserContext* userContext):userContext_(userContext)
	{
		pTaskDb_ = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb();
		pFilterMgr_ = BackupAllFilterMgr::create(userContext_);
		isScanning_ = false;
		isStopScanning_ = false;
	}

	virtual ~BackupAllScanImpl()
	{
		try
		{
			stop(false);
		}
		catch(...) {}	
	}

	virtual int32_t stop(bool async = true)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "stop");
		if(isScanning_&&!isStopScanning_)
		{
			scanThread_.interrupt();
			if (async)
			{
				isStopScanning_ = true;
			}
			else
			{
				scanThread_.join();
				isStopScanning_ = false;
			}
		}
		return RT_OK;
	}

	virtual int32_t start()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "start");
		if(isStopScanning_)
		{
			scanThread_.join();
			isStopScanning_ = false;
			isScanning_ = false;
		}

		int32_t ret = RT_OK;
		BATaskNode baTaskNode;
		ret = pTaskDb_->getNode(baTaskNode);
		if(RT_OK!=ret) return ret;

		if(-1==baTaskNode.remoteId)
		{
			return RT_ERROR;
		}
		if(!isScanning_)
		{
			isScanning_ = true;
			scanThread_ = boost::thread(boost::bind(&BackupAllScanImpl::startAsync, this, baTaskNode.remoteId, baTaskNode.isFilterChange));
		}

		return RT_OK;
	}

	virtual bool isScanning()
	{
		return isScanning_;
	}

	virtual void flushSelectInfo()
	{
		pFilterMgr_->flushSelectInfo();
	}

private:
	void startAsync(int64_t rootRemoteId, bool isFilterChange)
	{
		try
		{
			pFilterMgr_->flushSelectInfo();

			if(isFilterChange)
			{
				userContext_->getAsyncTaskMgr()->pauseTask(BACKUPALL_GROUPID);
				pFilterMgr_->doDeleteVolumeInfo();
				pFilterMgr_->doFilterChange();
				userContext_->getAsyncTaskMgr()->resumeTask(BACKUPALL_GROUPID);
				BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateFilterChange(false);
			}

			std::map<std::wstring, int64_t> fullBackVolume;
			pTaskDb_->getVolumeInfo(fullBackVolume, BAVS_Init);

			//扫描选择对象
			for(std::map<std::wstring, int64_t>::iterator it = fullBackVolume.begin(); it != fullBackVolume.end(); ++it)
			{
				BackupAllLocalDb* pLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first);
				BAScanStatus status = pLocalDb->getScanStatus();
				if(BASS_Complete==status)
				{
					//跳过扫描完成的
					continue;
				}
				if(BASS_Init==status)
				{
					pLocalDb->updateScanStatus(BASS_FullScanning);
				}

				//全量备份时，保持inc数与总数一致
				pLocalDb->loadIncInfoByTotal();

				//全量备份
				std::auto_ptr<BackupAllFullScan> pFullScan = BackupAllFullScan::create(userContext_);
				pFullScan->backupDisk(it->first, it->second, rootRemoteId);
				pLocalDb->reUploadErrorPath();
				pLocalDb->updateScanStatus(BASS_Complete);
			}

			if(!fullBackVolume.empty())
			{
				isScanning_ = false;
				return;
			}

			std::map<std::wstring, int64_t> volumeInfo;
			pTaskDb_->getVolumeInfo(volumeInfo, BAVS_Normal);
			for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
			{
				BackupAllLocalDb* pLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first);

				BAScanStatus status = pLocalDb->getScanStatus();
				if(BASS_Complete==status)
				{
					//跳过扫描完成的
					continue;
				}
				if(BASS_Init==status)
				{
					pLocalDb->updateScanStatus(BASS_Scanning);
				}

				//增量备份
				std::auto_ptr<BackupAllIncScan> pIncScan = BackupAllIncScan::create(userContext_);
				if(RT_OK!=pIncScan->incBackup(it->first, it->second))
				{
					std::auto_ptr<BackupAllFullScan> pFullScan = BackupAllFullScan::create(userContext_);
					pFullScan->backupDisk(it->first, 0, rootRemoteId);
				}
				pLocalDb->reUploadErrorPath();
				pLocalDb->updateScanStatus(BASS_Complete);
			}
		}
		catch(boost::thread_interrupted)
		{
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "backup all scan async thread interrupted.");
		}
		isScanning_ = false;
	}

private:
	UserContext* userContext_;
	BackupAllTaskDb* pTaskDb_;
	std::auto_ptr<BackupAllFilterMgr> pFilterMgr_;

	boost::thread scanThread_;
	bool isScanning_;
	bool isStopScanning_;
};

std::auto_ptr<BackupAllScan> BackupAllScan::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllScan>(new BackupAllScanImpl(userContext));
}