#ifndef _SYNC_UTILITY_H_
#define _SYNC_UTILITY_H_

#include  "SyncCommon.h"

namespace Sync
{
	std::string getInStr(const std::list<int64_t>& idList);

	std::string getInStrEx(const std::list<int64_t>& idList);
}

#endif
