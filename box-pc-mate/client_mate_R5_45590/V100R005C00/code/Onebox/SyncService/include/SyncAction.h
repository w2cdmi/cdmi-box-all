#ifndef _SYNC_ACTION_ALL_H_
#define _SYNC_ACTION_ALL_H_

#include "UserContext.h"
#include "SyncCommon.h"
#include "DiffNode.h"

class SyncAction
{
public:
	virtual ~SyncAction(){}

	virtual int32_t executeAction(DiffNode& diffNode, OperNode& nextOperNode) = 0;

	static std::auto_ptr<SyncAction> create(UserContext* userContext);
};

#endif