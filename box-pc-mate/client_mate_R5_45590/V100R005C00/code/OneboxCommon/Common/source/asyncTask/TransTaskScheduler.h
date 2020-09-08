#ifndef _ONEBOX_TRANS_TASK_SCHEDULER_H_
#define _ONEBOX_TRANS_TASK_SCHEDULER_H_

#include "AsyncTaskCommon.h"
#include "TransTableMgr.h"
#include <ISSPThreads_md.h>

class TransTaskScheduler
{
public:
	TransTaskScheduler(UserContext* userContext, TransTableMgr* transTableMgr);

	int32_t run();

	int32_t interrupt();

	int32_t interruptRunningTask(const std::wstring& group, bool async = false);

	int32_t interruptRunningTask(const AsyncTransType type, bool async = false);

	int32_t interruptRunningTask(const std::wstring& group, const std::wstring& source);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
