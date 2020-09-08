#include "RemoteDetector.h"
#include "RemoteDetectorAll.h"
#include "RemoteDetectorLocal.h"

std::auto_ptr<RemoteDetector> RemoteDetector::create(UserContext* userContext, SyncModel syncModel)
{
	if(Sync_All==syncModel)
	{
		return RemoteDetectorAll::create(userContext);
	}
	return RemoteDetectorLocal::create(userContext);
}