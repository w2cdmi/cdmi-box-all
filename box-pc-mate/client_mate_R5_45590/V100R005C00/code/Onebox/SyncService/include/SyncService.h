#ifndef _ONEBOX_SYNC_MGR_H_
#define _ONEBOX_SYNC_MGR_H_

#include "UserContext.h"

class ONEBOX_DLL_EXPORT SyncService
{
public:
	virtual ~SyncService(){}

	static SyncService* getInstance(UserContext* userContext);

	virtual int32_t start() = 0;

	virtual int32_t stop() = 0;

private:
	static std::auto_ptr<SyncService> instance_;
};

#endif