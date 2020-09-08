#include "SynchronizeMgr.h"
#include "ConfigureMgr.h"
#include "DiffProcessor.h"
#include "AsyncTaskMgr.h"
#include "TimeCounter.h"
#include "NetworkMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SynchronizeMgr")
#endif

class SynchronizeMgrImpl : public SynchronizeMgr
{
public:
	SynchronizeMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		localDetector_ = LocalDetector::create(userContext_);
		remoteDetector_ = RemoteDetector::create(userContext_, SyncModel(userContext_->getConfigureMgr()->getConfigure()->syncModel()));
		diffProcessor_ = DiffProcessor::create(userContext_, localDetector_.get(), remoteDetector_.get());
		timeCounter_ = std::auto_ptr<TimeCounter>(new TimeCounter(
			(int64_t)userContext_->getConfigureMgr()->getConfigure()->remoteDetectorPeriod()*1000*60*12,//2 hours 
			boost::bind(&SynchronizeMgrImpl::remoteFullDetect, this)));

		MAKE_CLIENT(client);
		int32_t ret = client().getIncSyncPeriod(sleepTime_);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "get inc sync period failed.");
			sleepTime_ = userContext_->getConfigureMgr()->getConfigure()->remoteDetectorPeriod();
		}
	}

	virtual ~SynchronizeMgrImpl()
	{
		try
		{
			stopSync(SYNC_TYPE_ALL);
		}
		catch(...){}
	}

	virtual int32_t startSync(const SYNC_TYPE type = SYNC_TYPE_ALL)
	{
		if (SYNC_TYPE_LOCAL_FULL&type)
		{
			// for now, we do not need local full detect
			//localFullDetectThread_ = boost::thread(boost::bind(&SynchronizeMgrImpl::localFullDetect, this));
		}
		if (SYNC_TYPE_LOCAL_INC&type)
		{
			if (RT_OK != localDetector_->start())
			{
				return RT_ERROR;
			}
			localIncDetectThread_ = boost::thread(boost::bind(&SynchronizeMgrImpl::localIncDetect, this));
		}
		if (SYNC_TYPE_REMOTE_FULL&type)
		{
			timeCounter_->start();
		}
		if (SYNC_TYPE_REMOTE_INC&type)
		{
			if(RT_OK!=remoteDetector_->fullDetect())
			{
				return RT_ERROR;
			}
			remoteIncDetectThread_ = boost::thread(boost::bind(&SynchronizeMgrImpl::remoteIncDetect, this));
		}
		if (SYNC_TYPE_PROCESS&type)
		{
			diffProcessorThread_ = boost::thread(boost::bind(&SynchronizeMgrImpl::diffProcessor, this));
			diffNotifyThread_ = boost::thread(boost::bind(&SynchronizeMgrImpl::diffNotify, this));
			userContext_->getAsyncTaskMgr()->start();
		}
		return RT_OK;
	}

	virtual int32_t stopSync(const SYNC_TYPE type = SYNC_TYPE_ALL)
	{
		if (SYNC_TYPE_PROCESS&type)
		{
			userContext_->getAsyncTaskMgr()->stop();
			//diffProcessor_->stopProcessor();
			diffProcessorThread_.interrupt();
			diffProcessorThread_.join();
			diffNotifyThread_.interrupt();
			diffNotifyThread_.join();
		}
		if (SYNC_TYPE_REMOTE_INC&type)
		{
			remoteIncDetectThread_.interrupt();
			remoteIncDetectThread_.join();
		}
		if (SYNC_TYPE_REMOTE_FULL&type)
		{
			timeCounter_->kill();
		}
		if (SYNC_TYPE_LOCAL_INC&type)
		{
			localIncDetectThread_.interrupt();
			localDetector_->stop();
			localIncDetectThread_.join();
		}
		if (SYNC_TYPE_LOCAL_FULL&type)
		{
			//localFullDetectThread_.interrupt();
			//localDetector_->stop();
			//localFullDetectThread_.join();
		}
		return RT_OK;
	}

private:
	void remoteIncDetect()
	{
		try
		{
			while (true)
			{
				boost::this_thread::sleep(boost::posix_time::seconds((long)sleepTime_));
				{
					(void)remoteDetector_->incDetect();
				}
				boost::this_thread::interruption_point();
			}
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "remote increase detect interrupt");
		}
	}

	void localIncDetect()
	{
		try
		{
			(void)localDetector_->incDetect();
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "local increase detect interrupt");
		}
	}

	void remoteFullDetect()
	{
		try
		{
			(void)remoteDetector_->fullDetect();
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "remote full detect interrupt");
		}
	}

	void localFullDetect()
	{
		try
		{
			(void)localDetector_->fullDetect();
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "local full detect interrupt");
		}
	}

	void diffProcessor()
	{
		try
		{
			while (true)
			{
				diffProcessor_->processDiff();
				boost::this_thread::interruption_point();
			}
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "diff processor interrupt");
		}
	}

	void diffNotify()
	{
		try
		{
			while (true)
			{
				diffProcessor_->notifyDiff();
				boost::this_thread::interruption_point();
			}
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "diff processor interrupt");
		}
	}
private:
	UserContext* userContext_;
	std::auto_ptr<LocalDetector> localDetector_;
	std::auto_ptr<RemoteDetector> remoteDetector_;
	std::auto_ptr<DiffProcessor> diffProcessor_;
	//boost::thread localFullDetectThread_;
	boost::thread localIncDetectThread_;
	boost::thread remoteFullDetectThread_;
	boost::thread remoteIncDetectThread_;
	boost::thread diffProcessorThread_;
	boost::thread diffNotifyThread_;
	std::auto_ptr<TimeCounter> timeCounter_;
	int64_t sleepTime_;
};

SynchronizeMgr* SynchronizeMgr::create(UserContext* userContext)
{
	return static_cast<SynchronizeMgr*>(new SynchronizeMgrImpl(userContext));
}