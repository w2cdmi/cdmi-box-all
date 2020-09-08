#ifndef _ONEBOX_DIFF_PROCESSOR_H_
#define _ONEBOX_DIFF_PROCESSOR_H_

#include "UserContext.h"

class DiffProcessor
{
public:
	virtual ~DiffProcessor(){}

	static std::auto_ptr<DiffProcessor> create(UserContext* userContext);

	virtual int32_t processDiff() = 0;

	virtual void notifyDiff() = 0;
};

#endif