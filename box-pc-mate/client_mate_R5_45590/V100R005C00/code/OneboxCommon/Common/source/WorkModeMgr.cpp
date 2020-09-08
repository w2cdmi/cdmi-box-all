#include "WorkModeMgr.h"
#include "NotifyMgr.h"
#include "NetworkMgr.h"
#include "CredentialMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include <boost/thread.hpp>
#include <list>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("WorkModeMgr")
#endif

typedef std::list<IWorkModeChangeNotify*> WorkModeChangeNotifies;

class WorkModeMgrImpl : public WorkModeMgr
{
public:
	WorkModeMgrImpl(UserContext* userContext);

	virtual ~WorkModeMgrImpl();

	virtual int32_t changeWorkMode(const WorkMode& mode);

	virtual WorkMode getWorkMode();

	virtual void addWorkModeChangeNotify(IWorkModeChangeNotify* notify);

private:
	int32_t setOnlineMode();

	int32_t setOfflineMode();

	int32_t setErrorMode();

	int32_t setPauseMode();

	int32_t setUninitialMode();

private:
	int32_t netWorkDetect();

	int32_t isNetworkAvailable();

	int32_t isCloudServiceAvailable();

private:
	UserContext* userContext_;
	WorkMode workMode_;
	boost::mutex mutex_;
	boost::thread netWorkDetectThread_;
	WorkModeChangeNotifies notifies;
};

WorkModeMgr* WorkModeMgr::create(UserContext* userContext)
{
	return static_cast<WorkModeMgr*>(new WorkModeMgrImpl(userContext));
}

WorkModeMgrImpl::WorkModeMgrImpl(UserContext* userContext)
	:userContext_(userContext)
	,workMode_(WorkMode_Uninitial)
{

}

WorkModeMgrImpl::~WorkModeMgrImpl()
{
	try
	{
		netWorkDetectThread_.interrupt();
		netWorkDetectThread_.join();
	}
	catch(...){}
}

int32_t WorkModeMgrImpl::changeWorkMode(const WorkMode& mode)
{
	switch (mode)
	{
	case WorkMode_Online:
		boost::thread(boost::bind(&WorkModeMgrImpl::setOnlineMode, this));
		break;
	case WorkMode_Offline:
		boost::thread(boost::bind(&WorkModeMgrImpl::setOfflineMode, this));
		break;
	case WorkMode_Error:
		boost::thread(boost::bind(&WorkModeMgrImpl::setErrorMode, this));
		break;
	case WorkMode_Pause:
		boost::thread(boost::bind(&WorkModeMgrImpl::setPauseMode, this));
		break;
	default:
		return RT_INVALID_PARAM;
	}

	return RT_OK;
}

WorkMode WorkModeMgrImpl::getWorkMode()
{
	boost::mutex::scoped_lock lock(mutex_);
	return workMode_;
}

void WorkModeMgrImpl::addWorkModeChangeNotify(IWorkModeChangeNotify* notify)
{
	if (NULL == notify)
	{
		return;
	}
	notifies.push_back(notify);
}

int32_t WorkModeMgrImpl::setOnlineMode()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (WorkMode_Online != workMode_)
	{
		int32_t ret = RT_ERROR;
		for (WorkModeChangeNotifies::iterator it = notifies.begin(); it != notifies.end(); ++it)
		{
			ret = (*it)->changeWorkMode(WorkMode_Online);
			if (RT_OK != ret)
			{
				break;
			}
		}
		if (RT_OK == ret)
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "go to online mode.");
			// first change the work mode, start the network detect thread
			if (WorkMode_Uninitial == workMode_)
			{
				netWorkDetectThread_ = boost::thread(boost::bind(&WorkModeMgrImpl::netWorkDetect, this));
			}
			workMode_ = WorkMode_Online;
		}
	}
	
	userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_CHANGE_WORK_MODE, 
		Utility::String::format_string(L"%d", workMode_)));

	return RT_OK;
}

int32_t WorkModeMgrImpl::setOfflineMode()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (WorkMode_Online == workMode_ || 
		WorkMode_Pause == workMode_)
	{
		int32_t ret = RT_ERROR;
		for (WorkModeChangeNotifies::iterator it = notifies.begin(); it != notifies.end(); ++it)
		{
			ret = (*it)->changeWorkMode(WorkMode_Offline);
			if (RT_OK != ret)
			{
				break;
			}
		}
		if (RT_OK == ret)
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "go to offline mode.");
			workMode_ = WorkMode_Offline;
		}
	}

	userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_CHANGE_WORK_MODE, 
		Utility::String::format_string(L"%d", workMode_)));

	return RT_OK;
}

int32_t WorkModeMgrImpl::setErrorMode()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (WorkMode_Uninitial == workMode_)
	{
		HSLOG_EVENT(MODULE_NAME, RT_OK, "go to error mode.");
		workMode_ = WorkMode_Error;
	}

	userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_CHANGE_WORK_MODE, 
		Utility::String::format_string(L"%d", workMode_)));

	return RT_OK;
}

int32_t WorkModeMgrImpl::setPauseMode()
{
	boost::mutex::scoped_lock lock(mutex_);
	if (WorkMode_Online == workMode_)
	{
		int32_t ret = RT_ERROR;
		for (WorkModeChangeNotifies::iterator it = notifies.begin(); it != notifies.end(); ++it)
		{
			ret = (*it)->changeWorkMode(WorkMode_Pause);
			if (RT_OK != ret)
			{
				break;
			}
		}
		if (RT_OK == ret)
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "go to pause mode.");
			workMode_ = WorkMode_Pause;
		}
	}

	userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
		NOTIFY_MSG_CHANGE_WORK_MODE, 
		Utility::String::format_string(L"%d", workMode_)));

	return RT_OK;
}

int32_t WorkModeMgrImpl::isCloudServiceAvailable()
{
	int32_t ret = RT_OK;
	RETRY(5)
	{
		if (WorkMode_Offline == getWorkMode())
		{			
			ret = userContext_->getUserInfoMgr()->autoAuthen();
		}
		else
		{
			(void)userContext_->getCredentialMgr()->updateCredentialInfo();

			if (RT_OK == ret && NETWORK_STATUS_ERROR != userContext_->getNetworkMgr()->getNetworkStatus())
			{
				ret = RT_OK;
				break;
			}

			ret = userContext_->getUserInfoMgr()->autoAuthen();
		}

		SLEEP(boost::posix_time::milliseconds(1000));
	}
	return ret;
}

int32_t WorkModeMgrImpl::netWorkDetect()
{
	try
	{
		while (true)
		{
			SLEEP(boost::posix_time::seconds(60));

			// cloud service is not available, goto the offline mode
			int32_t ret = isCloudServiceAvailable();
			if (RT_OK != ret)
			{
				(void)changeWorkMode(WorkMode_Offline);
				continue;
			}
			// cloud service is available, go to online mode
			if (WorkMode_Online != getWorkMode())
			{
				(void)changeWorkMode(WorkMode_Online);
			}
		}
	}
	catch(boost::thread_interrupted& e)
	{
		UNUSED_ARG(e);
		HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "network detect thread interrupted.");
	}
	return RT_OK;
}
