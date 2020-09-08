#include "CredentialMgr.h"
#include "NetworkMgr.h"
#include "RestClient.h"
#include "ConfigureMgr.h"
#include "UserInfoMgr.h"
#include <boost/thread/mutex.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("CredentialMgr")
#endif

class CredentialMgrImpl : public CredentialMgr
{
public:
	CredentialMgrImpl(UserContext* userContext);

	virtual CredentialInfo getCredentialInfo();

	virtual CredentialInfo getRefreshCrentialInfo();

	virtual int32_t setCredentialInfo(const CredentialInfo& info);

	virtual int32_t setRefreshCredentialInfo(const CredentialInfo& info);

	virtual int32_t updateCredentialInfo();

private:
	UserContext* userContext_;
	CredentialInfo credentialInfo_;
	CredentialInfo refreshCredentialInfo_;
	boost::mutex mutex_;
};

CredentialMgr* CredentialMgr::create(UserContext* userContext)
{
	return static_cast<CredentialMgr*>(new CredentialMgrImpl(userContext));
}

CredentialMgrImpl::CredentialMgrImpl(UserContext* userContext)
	:userContext_(userContext)
{

}

CredentialInfo CredentialMgrImpl::getCredentialInfo()
{
	boost::mutex::scoped_lock lock(mutex_);
	return credentialInfo_;
}

CredentialInfo CredentialMgrImpl::getRefreshCrentialInfo()
{
	boost::mutex::scoped_lock lock(mutex_);
	return refreshCredentialInfo_;
}

int32_t CredentialMgrImpl::setCredentialInfo(const CredentialInfo& info)
{
	boost::mutex::scoped_lock lock(mutex_);
	credentialInfo_.token = info.token;
	//credentialInfo_.type = info.type;
	credentialInfo_.period = info.period;
	credentialInfo_.start = GetTickCount();

	return RT_OK;
}

int32_t CredentialMgrImpl::setRefreshCredentialInfo(const CredentialInfo& info)
{
	boost::mutex::scoped_lock lock(mutex_);
	refreshCredentialInfo_.token = info.token;
	//refreshCredentialInfo_.type = info.type;
	refreshCredentialInfo_.period = info.period;
	refreshCredentialInfo_.start = GetTickCount();

	return RT_OK;
}

int32_t CredentialMgrImpl::updateCredentialInfo()
{
	boost::mutex::scoped_lock lock(mutex_);

	uint32_t now = GetTickCount();
	if (now < credentialInfo_.start)
	{
		credentialInfo_.start = now;
	}

	// token is considered to be expired when 5 minutes before the real expired time
	if ((credentialInfo_.start+credentialInfo_.period*60*1000-300*1000) > now)
	{
		return RT_OK;
	}

	lock.unlock();
	RestClient client(refreshCredentialInfo_, *userContext_->getConfigureMgr()->getConfigure());
	LoginRespInfo loginRsp;
	int32_t ret = client.refreshToken(loginRsp);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "refresh token failed, try to relogin.");
		return userContext_->getUserInfoMgr()->autoAuthen();
	}

	lock.lock();
	// server interval is seconds and local interval is milliseconds
	credentialInfo_.period = loginRsp.expiredAt / 60;
	credentialInfo_.token = loginRsp.token;
	//credentialInfo_.type = loginRsp.token_type;
	credentialInfo_.start = GetTickCount();

	refreshCredentialInfo_.period = loginRsp.expiredAt / 60;
	refreshCredentialInfo_.token = loginRsp.refreshToken;
	//refreshCredentialInfo_.type = loginRsp.token_type;
	refreshCredentialInfo_.start = GetTickCount();

	return RT_OK;
}
