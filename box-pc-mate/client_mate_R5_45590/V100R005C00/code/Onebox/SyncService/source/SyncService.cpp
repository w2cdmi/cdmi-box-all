#include "SyncService.h"
#include "UserContextMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "RemoteDetector.h"
#include "LocalDetector.h"
#include "DiffProcessor.h"
#include "SyncConfigure.h"
#include "TimeCounter.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncService")
#endif

using namespace SD::Utility;

boost::mutex GlobalMutex::localMutex_;
boost::mutex GlobalMutex::remoteMutex_;

class SyncServiceImpl : public SyncService
{
public:
	SyncServiceImpl(UserContext* userContext):userContext_(userContext)
	{
		localDetector_ = LocalDetector::create(userContext_);
		remoteDetector_ = RemoteDetector::create(userContext_);
		diffProcessor_ = DiffProcessor::create(userContext_);
		isStart_ = true;
		//timeCounter_ = std::auto_ptr<TimeCounter>(new TimeCounter(
		//	(int64_t)userContext_->getConfigureMgr()->getConfigure()->remoteDetectorPeriod()*1000*60*12,//2 hours 
		//	boost::bind(&SynchronizeMgrImpl::remoteFullDetect, this)));
	}

	~SyncServiceImpl()
	{
	}

	virtual int32_t start()
	{
		boost::thread(boost::bind(&SyncServiceImpl::startSync, this));
		return RT_OK;
	}

	virtual int32_t stop()
	{
		return stopSync();
	}

private:
	int32_t startSync()
	{
		if (RT_OK != localDetector_->start())
		{
			return RT_ERROR;
		}
		if(RT_OK!=remoteDetector_->fullDetect())
		{
			return RT_ERROR;
		}
		isStart_ = true;

		localIncDetectThread_ = boost::thread(boost::bind(&SyncServiceImpl::localIncDetect, this));
		remoteIncDetectThread_ = boost::thread(boost::bind(&SyncServiceImpl::remoteIncDetect, this));
		diffProcessorThread_ = boost::thread(boost::bind(&SyncServiceImpl::diffProcessor, this));
		//diffNotifyThread_ = boost::thread(boost::bind(&SyncServiceImpl::diffNotify, this));

		return RT_OK;
	}

	int32_t stopSync()
	{
		// cancel transtask of sychronize
		// ...
		//diffProcessorThread_.interrupt();
		//diffProcessorThread_.join();
		//diffNotifyThread_.interrupt();
		//diffNotifyThread_.join();
		isStart_ = false;

		remoteIncDetectThread_.interrupt();
		remoteIncDetectThread_.join();

		localIncDetectThread_.interrupt();
		localDetector_->stop();
		localIncDetectThread_.join();

		return RT_OK;
	}

	void remoteIncDetect()
	{
		try
		{
			while (isStart_)
			{
				int64_t sleepTime = SyncConfigure::getInstance(userContext_)->remoteDetectorPeriod();
				boost::this_thread::sleep(boost::posix_time::milliseconds(sleepTime*1000));
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
			boost::this_thread::sleep(boost::posix_time::milliseconds(1000));
			while (isStart_)
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
			while (isStart_)
			{
				diffProcessor_->notifyDiff();
				boost::this_thread::sleep(boost::posix_time::milliseconds(500));
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

	boost::thread localIncDetectThread_;
	boost::thread remoteFullDetectThread_;
	boost::thread remoteIncDetectThread_;
	boost::thread diffProcessorThread_;
	boost::thread diffNotifyThread_;
	bool isStart_;
	//std::auto_ptr<TimeCounter> timeCounter_;
};

std::auto_ptr<SyncService> SyncService::instance_(NULL);

SyncService* SyncService::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new SyncServiceImpl(userContext));
	}
	return instance_.get();
}


