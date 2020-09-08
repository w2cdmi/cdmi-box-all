#ifndef _ONEBOX_NETWORK_MGR_H_
#define _ONEBOX_NETWORK_MGR_H_

#include "UserContext.h"
#include "RestClient.h"

enum CONNECT_TYPE
{
	CONNECT_TYPE_NORMAL,
	CONNECT_TYPE_UPLOAD,
	CONNECT_TYPE_DOWNLOAD
};

enum NETWORK_STATUS
{
	NETWORK_STATUS_NORMAL,
	NETWORK_STATUS_BUSY,
	NETWORK_STATUS_ERROR
};

class ONEBOX_DLL_EXPORT NetworkMgr
{
public:
	virtual ~NetworkMgr(){}

	static NetworkMgr* create(UserContext* userContext);

	virtual RestClient* getClient(const CONNECT_TYPE& type) = 0;

	virtual void removeClient(RestClient* client) = 0;

	virtual int64_t getUploadSpeed() = 0;

	virtual int64_t getDownloadSpeed() = 0;

	virtual NETWORK_STATUS getNetworkStatus() = 0;
};

class ConnectionGuarder
{
public:
	ConnectionGuarder(UserContext* userContext, RestClient* client)
		:userContext_(userContext)
		,client_(client)
	{

	}

	virtual ~ConnectionGuarder()
	{
		userContext_->getNetworkMgr()->removeClient(client_);
	}

	RestClient& operator()() const
	{
		return *client_;
	}

private:
	UserContext* userContext_;
	RestClient* client_;
};

#define MAKE_CLIENT(client) ConnectionGuarder client(userContext_, \
	userContext_->getNetworkMgr()->getClient(CONNECT_TYPE_NORMAL))
#define MAKE_UPLOAD_CLIENT(client) ConnectionGuarder client(userContext_, \
	userContext_->getNetworkMgr()->getClient(CONNECT_TYPE_UPLOAD))
#define MAKE_DOWNLOAD_CLIENT(client) ConnectionGuarder client(userContext_, \
	userContext_->getNetworkMgr()->getClient(CONNECT_TYPE_DOWNLOAD))

#endif