#ifndef _ONEBOX_CONFIGURE_MGR_H_
#define _ONEBOX_CONFIGURE_MGR_H_

#include "UserContext.h"
#include "Configure.h"

class ONEBOX_DLL_EXPORT ConfigureMgr
{
public:
	virtual ~ConfigureMgr(){}

	static ConfigureMgr* create(UserContext* userContext, const std::wstring& confPath=L"");

	virtual int32_t serialize() = 0;

	virtual int32_t unserialize() = 0;

	virtual Configure* getConfigure() = 0;

	virtual int32_t updateUserConfigurePath(const int64_t userId, const int64_t parentId) = 0;
};

#endif
