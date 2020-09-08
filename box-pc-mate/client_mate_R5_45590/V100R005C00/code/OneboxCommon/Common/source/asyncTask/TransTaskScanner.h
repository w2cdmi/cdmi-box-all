#ifndef _ONEBOX_TRANS_TASK_SCANNER_H_
#define _ONEBOX_TRANS_TASK_SCANNER_H_

#include "AsyncTaskCommon.h"
#include "TransTableMgr.h"

class TransTaskScanner
{
public:
	TransTaskScanner(UserContext* userContext, TransTableMgr* transTableMgr);

	int32_t run();

	int32_t interrupt();

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
