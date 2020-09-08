#ifndef _ONEBOX_CONFIGURE_MGR_H_
#define _ONEBOX_CONFIGURE_MGR_H_

#include "UserContext.h"
#include "Configure.h"
#include "SyncRules.h"

class ConfigureMgr
{
public:
	virtual ~ConfigureMgr(){}

	static ConfigureMgr* create(UserContext* userContext, const std::wstring& confPath);

	virtual int32_t serialize() = 0;

	virtual int32_t unserialize() = 0;

	virtual Configure* getConfigure() = 0;

	virtual SyncRules* getSyncRules() = 0;
};

#endif
