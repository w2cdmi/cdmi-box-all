#ifndef _ONEBOX_CREDENTIAL_MGR_H_
#define _ONEBOX_CREDENTIAL_MGR_H_

#include "UserContext.h"
#include "Token.h"

typedef TOKEN CredentialInfo;

class CredentialMgr
{
public:
	virtual ~CredentialMgr(){}

	static CredentialMgr* create(UserContext* userContext);

	virtual CredentialInfo getCredentialInfo() = 0;

	virtual CredentialInfo getRefreshCrentialInfo() = 0;

	virtual int32_t setCredentialInfo(const CredentialInfo& info) = 0;

	virtual int32_t setRefreshCredentialInfo(const CredentialInfo& info) = 0;

	virtual int32_t updateCredentialInfo() = 0;
};

#endif