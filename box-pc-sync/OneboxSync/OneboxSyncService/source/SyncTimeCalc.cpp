#include <fstream>
#include "SyncTimeCalc.h"
#include "InIHelper.h"
#include "DataBaseMgr.h"
#include "ConfigureMgr.h"
#include "NetworkMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncTimeCalc")
#endif

#define DEFAULT_DIFFCNT_SPEED 100		//ms/per
#define DEFAULT_UPLOAD_SPEED 2000		//B/ms
#define DEFAULT_DOWNLOAD_SPEED 2000		//B/ms
#define DEFAULT_CALC_SIZE 5*1024		//B

class SyncTimeCalcImpl : public SyncTimeCalc
{
public:
	SyncTimeCalcImpl(UserContext* userContext):userContext_(userContext)
	{
		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		diffTotalCnt_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_TOTALCNT, 0);
		diffTotalCntTime_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_TOTALCNT_TIME, 0);
		uploadTotalSize_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_UPLOAD_SIZE, 0);
		uploadTotalSizeTime_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_UPLOAD_TIME, 0);
		downloadTotalSize_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_DOWNLOAD_SIZE, 0);
		downloadTotalSizeTime_ = iniHelper.GetValue<int64_t>(L"", CONF_DIFF_DOWNLOAD_TIME, 0);
	}

	virtual ~SyncTimeCalcImpl(void)
	{
	}

	//未启动计时的情况下，遇normal diff时启动
	virtual int64_t startCntTimer(void)
	{
		boost::mutex::scoped_lock lock(m_mutexCnt);
		startCntDiffId_ = INVALID_ID;
		startCntTime_ = GetTickCount();
		userContext_->getDataBaseMgr()->getDiffTable()->getCntForTimer(startCntDiffId_, startCnt_);
		return startCntDiffId_;
	}

	//停止同步、遇非normal diff、或当前DiffId大于启动定时器时DiffId的情况下，停止计时
	virtual int64_t stopCntTimer(void)
	{
		boost::mutex::scoped_lock lock(m_mutexCnt);
		unsigned long now = GetTickCount();
		if(now < startCntTime_)
		{
			return INVALID_ID;
		}
		int32_t curCnt;
		userContext_->getDataBaseMgr()->getDiffTable()->getCntForTimer(startCntDiffId_, curCnt);
		diffTotalCnt_ = diffTotalCnt_ + startCnt_ - curCnt;
		diffTotalCntTime_ = diffTotalCntTime_ + now - startCntTime_;

		if(0==curCnt)
		{
			startCntDiffId_ = INVALID_ID;
		}

		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_TOTALCNT, diffTotalCnt_);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_TOTALCNT_TIME, diffTotalCntTime_);

		return startCntDiffId_;
	}

	//ms/per
	virtual int32_t getDiffCntSpeed()
	{
		boost::mutex::scoped_lock lock(m_mutexCnt);
		int32_t diffCntSpeed = DEFAULT_DIFFCNT_SPEED;
		if((diffTotalCnt_>0)&&(diffTotalCntTime_>0))
		{
			diffCntSpeed = static_cast<int32_t>(diffTotalCntTime_/diffTotalCnt_);
		}
		return diffCntSpeed;
	}

	virtual void startUploadTimer(void)
	{
		boost::mutex::scoped_lock lock(m_mutexUp);
		uploadSizeTime_ = GetTickCount();
	}

	virtual void stopUploadTimer(int64_t& size)
	{
		boost::mutex::scoped_lock lock(m_mutexUp);
		if(size <= 0)
		{
			return;
		}
		unsigned long now = GetTickCount();
		if(now < uploadSizeTime_)
		{
			return;
		}
		if(size < DEFAULT_CALC_SIZE)
		{
			return;
		}
		uploadTotalSize_ = uploadTotalSize_ + size;
		uploadTotalSizeTime_ = uploadTotalSizeTime_ + now - uploadSizeTime_;

		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_UPLOAD_SIZE, uploadTotalSize_);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_UPLOAD_TIME, uploadTotalSizeTime_);
	}

	//B/ms
	virtual int32_t getUploadSpeed()
	{
		boost::mutex::scoped_lock lock(m_mutexUp);
		int32_t uploadSpeed = DEFAULT_UPLOAD_SPEED;
		if((uploadTotalSize_>0)&&(uploadTotalSizeTime_>0))
		{
			uploadSpeed = static_cast<int32_t>(uploadTotalSize_/uploadTotalSizeTime_);
		}
		else
		{
			//B/s
			int32_t tempSpeed = static_cast<int32_t>(userContext_->getNetworkMgr()->getUploadSpeed());
			if(tempSpeed>0)
			{
				uploadSpeed = tempSpeed/1000;
			}
		}
		return (uploadSpeed>0?uploadSpeed:DEFAULT_DOWNLOAD_SPEED);;
	}

	virtual void startDownloadTimer(void)
	{
		boost::mutex::scoped_lock lock(m_mutexDowm);
		downloadSizeTime_ = GetTickCount();
	}

	virtual void stopDownloadTimer(int64_t& size)
	{
		boost::mutex::scoped_lock lock(m_mutexDowm);
		if(size <= 0)
		{
			return;
		}
		unsigned long now = GetTickCount();
		if(now < downloadSizeTime_)
		{
			return;
		}
		if(size < DEFAULT_CALC_SIZE)
		{
			return;
		}
		downloadTotalSize_ = downloadTotalSize_ + size;
		downloadTotalSizeTime_ = downloadTotalSizeTime_ + now - downloadSizeTime_;

		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_DOWNLOAD_SIZE, downloadTotalSize_);
		iniHelper.SetValue<int64_t>(L"", CONF_DIFF_DOWNLOAD_TIME, downloadTotalSizeTime_);
	}

	//B/ms
	virtual int32_t getDownloadSpeed()
	{
		boost::mutex::scoped_lock lock(m_mutexDowm);
		int32_t downloadSpeed = DEFAULT_DOWNLOAD_SPEED;
		if((downloadTotalSize_>0)&&(downloadTotalSizeTime_>0))
		{
			downloadSpeed = static_cast<int32_t>(downloadTotalSize_/downloadTotalSizeTime_);
		}
		else
		{
			//B/s
			int32_t tempSpeed = static_cast<int32_t>(userContext_->getNetworkMgr()->getDownloadSpeed());
			if(tempSpeed>0)
			{
				downloadSpeed = tempSpeed/1000;
			}
		}
		return (downloadSpeed>0?downloadSpeed:DEFAULT_DOWNLOAD_SPEED);
	}
private:
	UserContext* userContext_;

	boost::mutex m_mutexCnt;
	int64_t diffTotalCnt_;
	int64_t diffTotalCntTime_;
	int64_t startCntDiffId_;
	int32_t startCnt_;
	unsigned long startCntTime_;

	boost::mutex m_mutexUp;
	int64_t uploadTotalSize_;
	int64_t uploadTotalSizeTime_;
	unsigned long uploadSizeTime_;

	boost::mutex m_mutexDowm;
	int64_t downloadTotalSize_;
	int64_t downloadTotalSizeTime_;
	unsigned long downloadSizeTime_;
};

SyncTimeCalc::~SyncTimeCalc(void)
{
}

SyncTimeCalc* SyncTimeCalc::create(UserContext* userContext)
{
	return static_cast<SyncTimeCalc*>(new SyncTimeCalcImpl(userContext));
}