#ifndef _SYNC_RULES_H_
#define _SYNC_RULES_H_

#include "SyncCommon.h"

class SyncRules
{
public:
	virtual ~SyncRules(void);

	virtual bool getActions(const SyncRuleKey& op, ExecuteActions& actions) = 0;

	static std::auto_ptr<SyncRules> create();
};

#endif