#ifndef _ONEBOX_SYNCHRONIZE_MGR_H_
#define _ONEBOX_SYNCHRONIZE_MGR_H_

#include "UserContext.h"

enum SYNC_TYPE
{
	SYNC_TYPE_LOCAL_FULL = 0x01,
	SYNC_TYPE_LOCAL_INC = 0x02,
	SYNC_TYPE_REMOTE_FULL = 0x04,
	SYNC_TYPE_REMOTE_INC = 0x08,
	SYNC_TYPE_PROCESS = 0x10
};

#define SYNC_TYPE_ALL (SYNC_TYPE)(~0)

class SynchronizeMgr
{
public:
	virtual ~SynchronizeMgr(){}

	static SynchronizeMgr* create(UserContext* userContext);

	virtual int32_t startSync(const SYNC_TYPE type = SYNC_TYPE_ALL) = 0;

	virtual int32_t stopSync(const SYNC_TYPE type = SYNC_TYPE_ALL) = 0;
};

#endif