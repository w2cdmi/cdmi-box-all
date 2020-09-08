#include "SyncAction.h"
#include "SyncActionAll.h"
#include "SyncActionLocal.h"

std::auto_ptr<SyncAction> SyncAction::create(UserContext* userContext, SyncModel syncModel)
{
	if(Sync_All==syncModel)
	{
		return SyncActionAll::create(userContext);
	}
	return SyncActionLocal::create(userContext);
}