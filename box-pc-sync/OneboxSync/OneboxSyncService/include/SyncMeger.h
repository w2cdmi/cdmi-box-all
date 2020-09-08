#ifndef _SYNC_MEGER_H_
#define _SYNC_MEGER_H_

#include "UserContext.h"
#include "SyncCommon.h"

class SyncMeger
{
public:
	virtual int32_t megerAll() = 0;

	static std::auto_ptr<SyncMeger> create(UserContext* userContext);
};

#endif