#ifndef _SYNC_MEGER_H_
#define _SYNC_MEGER_H_

#include "UserContext.h"
#include "SyncCommon.h"

class SyncMerger
{
public:
	virtual ~SyncMerger(){}

	virtual int32_t megerAll() = 0;

	static std::auto_ptr<SyncMerger> create(UserContext* userContext);
};

#endif