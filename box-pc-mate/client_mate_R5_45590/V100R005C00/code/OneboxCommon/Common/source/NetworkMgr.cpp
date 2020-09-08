#include "NetworkMgr.h"
#include "CredentialMgr.h"
#include "ConfigureMgr.h"
#include <map>
#include <boost/thread/mutex.hpp>

typedef std::shared_ptr<RestClient> RestClientPtr;

static const uint32_t REQUEST_BUST_TIME = 50*1000; // 50s

enum CONNECT_STATUS
{
	CONNECT_STATUS_NORMAL,
	CONNECT_STATUS_CLOSE
};

class ConnectProperty
{
public:
	CONNECT_TYPE type;
	CONNECT_STATUS status;
	uint32_t tick;

	ConnectProperty(CONNECT_TYPE type_)
		:type(type_)
		,status(CONNECT_STATUS_NORMAL)
	{
		tick = GetTickCount();
	}
};

class NetworkMgrImpl : public NetworkMgr
{
public:
	NetworkMgrImpl(UserContext* userContext);

	virtual RestClient* getClient(const CONNECT_TYPE& type);

	virtual void removeClient(RestClient* client);

	virtual int64_t getUploadSpeed();

	virtual int64_t getDownloadSpeed();

	virtual NETWORK_STATUS getNetworkStatus();

private:
	void setUploadSpeed();

	void setDownloadSpeed();

	void setErrorCode(int32_t errorCode);

	void setRequestTime(uint32_t requestTime);

private:
	UserContext* userContext_;
	std::map<RestClientPtr, ConnectProperty> clients_;
	uint32_t uploadClientSize_;
	uint32_t downloadClientSize_;
	int32_t errorCode_;
	uint32_t requestTime_;
	boost::mutex mutex_;
};

NetworkMgr* NetworkMgr::create(UserContext* userContext)
{
	return static_cast<NetworkMgr*>(new NetworkMgrImpl(userContext));
}

NetworkMgrImpl::NetworkMgrImpl(UserContext* userContext)
	:userContext_(userContext)
	,uploadClientSize_(0)
	,downloadClientSize_(0)
	,errorCode_(RT_OK)
	,requestTime_(0)
{

}

RestClient* NetworkMgrImpl::getClient(const CONNECT_TYPE& type)
{
	boost::mutex::scoped_lock lock(mutex_);
	
	RestClientPtr client(new RestClient(userContext_->getCredentialMgr()->getCredentialInfo(), 
		*(userContext_->getConfigureMgr()->getConfigure())));
	ConnectProperty property(type);

	clients_.insert(std::make_pair(client, property));

	if (CONNECT_TYPE_UPLOAD == type)
	{
		++uploadClientSize_;
		setUploadSpeed();
	}
	else if (CONNECT_TYPE_DOWNLOAD == type)
	{
		++downloadClientSize_;
		setDownloadSpeed();
	}
	
	return client.get();
}

void NetworkMgrImpl::removeClient(RestClient* client)
{
	setErrorCode(client->getErrorCode());
	setRequestTime(client->getRequstTime());

	boost::mutex::scoped_lock lock(mutex_);

	for (std::map<RestClientPtr, ConnectProperty>::iterator it = clients_.begin(); 
		it != clients_.end(); ++it)
	{
		if (client == it->first.get())
		{
			if (CONNECT_TYPE_NORMAL == it->second.type)
			{
				clients_.erase(it);
				break;
			}
			else if (CONNECT_TYPE_UPLOAD == it->second.type)
			{
				it->second.status = CONNECT_STATUS_CLOSE;
				--uploadClientSize_;
				setUploadSpeed();
				break;
			}
			else if (CONNECT_TYPE_DOWNLOAD == it->second.type)
			{
				it->second.status = CONNECT_STATUS_CLOSE;
				--downloadClientSize_;
				setDownloadSpeed();
				break;
			}
		}
	}
}

int64_t NetworkMgrImpl::getUploadSpeed()
{
	boost::mutex::scoped_lock lock(mutex_);

	if (clients_.empty())
	{
		return 0L;
	}
	int64_t distance = 0L;
	uint32_t now = GetTickCount(), time = now;
	for (std::map<RestClientPtr, ConnectProperty>::iterator it = clients_.begin(); 
		it != clients_.end(); )
	{
		if (CONNECT_TYPE_UPLOAD != it->second.type)
		{
			++it;
			continue;
		}
		if (now > it->second.tick)
		{
			time = (time<it->second.tick?time:it->second.tick);
			distance += (now - it->second.tick)*it->first->getUploadSpeed();
		}
		if (CONNECT_STATUS_CLOSE == it->second.status)
		{
			it = clients_.erase(it);
			continue;
		}
		++it;
	}
	time = now - time;
	if (0 == time)
	{
		return 0L;
	}
	return (distance/time);
}

int64_t NetworkMgrImpl::getDownloadSpeed()
{
	boost::mutex::scoped_lock lock(mutex_);

	if (clients_.empty())
	{
		return 0L;
	}
	int64_t distance = 0L;
	uint32_t now = GetTickCount(), time = now;
	for (std::map<RestClientPtr, ConnectProperty>::iterator it = clients_.begin(); 
		it != clients_.end(); )
	{
		if (CONNECT_TYPE_DOWNLOAD != it->second.type)
		{
			++it;
			continue;
		}
		if (now > it->second.tick)
		{
			time = (time<it->second.tick?time:it->second.tick);
			distance += (now - it->second.tick)*it->first->getDownloadSpeed();
		}
		if (CONNECT_STATUS_CLOSE == it->second.status)
		{
			it = clients_.erase(it);
			continue;
		}
		++it;
	}
	time = now - time;
	if (0 == time)
	{
		return 0L;
	}
	return (distance/time);
}
 
NETWORK_STATUS NetworkMgrImpl::getNetworkStatus()
{
	boost::mutex::scoped_lock lock(mutex_);

	NETWORK_STATUS status = NETWORK_STATUS_NORMAL;
	if (ACCOUNT_DISABLE == errorCode_ || 
		//SECURITY_MATRIX_FORBIDDEN == errorCode_ || 
		CURL_RESOLVE_PROXY_FAILED == errorCode_ || 
		CURL_RESOLVE_HOST_FAILED == errorCode_ || 
		CURL_CONNECT_FAILED == errorCode_ || 
		CURL_TIMEDOUT == errorCode_ || 
		HTTP_PROXY_ERROR == errorCode_)
	{
		status = NETWORK_STATUS_ERROR;
	}
	else if (REQUEST_BUST_TIME < requestTime_)
	{
		status = NETWORK_STATUS_BUSY;
	}
	else
	{
		status = NETWORK_STATUS_NORMAL;
	}

	// clear error code
	errorCode_ = RT_OK;

	return status;
}

void NetworkMgrImpl::setUploadSpeed()
{
	SpeedLimitConf speedLimitConf = userContext_->getConfigureMgr()->getConfigure()->speedLimitConf();	
	if (!speedLimitConf.useSpeedLimit || 0 == uploadClientSize_)
	{
		return;
	}
	int64_t avrSpeed = speedLimitConf.maxUploadSpeed/uploadClientSize_;
	for (std::map<RestClientPtr, ConnectProperty>::iterator it = clients_.begin(); 
		it != clients_.end(); ++it)
	{
		if (CONNECT_TYPE_UPLOAD != it->second.type)
		{
			continue;
		}
		if (CONNECT_STATUS_NORMAL == it->second.status)
		{
			(it->first)->setUploadSpeedLimit(avrSpeed);
		}
	}
}

void NetworkMgrImpl::setDownloadSpeed()
{
	SpeedLimitConf speedLimitConf = userContext_->getConfigureMgr()->getConfigure()->speedLimitConf();	
	if (!speedLimitConf.useSpeedLimit || 0 == downloadClientSize_)
	{
		return;
	}
	int64_t avrSpeed = speedLimitConf.maxDownloadSpeed/downloadClientSize_;
	for (std::map<RestClientPtr, ConnectProperty>::iterator it = clients_.begin(); 
		it != clients_.end(); ++it)
	{
		if (CONNECT_TYPE_DOWNLOAD != it->second.type)
		{
			continue;
		}
		if (CONNECT_STATUS_NORMAL == it->second.status)
		{
			(it->first)->setDownloadSpeedLimit(avrSpeed);
		}
	}
}

void NetworkMgrImpl::setErrorCode(int32_t errorCode)
{
	boost::mutex::scoped_lock lock(mutex_);
	errorCode_ = errorCode;
}

void NetworkMgrImpl::setRequestTime(uint32_t requestTime)
{
	boost::mutex::scoped_lock lock(mutex_);
	requestTime_ = requestTime;
}
