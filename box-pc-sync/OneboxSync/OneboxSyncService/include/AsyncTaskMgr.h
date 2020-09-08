#ifndef _ONEBOX_ASYNC_TASK_MGR_H_
#define _ONEBOX_ASYNC_TASK_MGR_H_

#include "TransTask.h"
#include "Path.h"
#include <boost/function.hpp>

class AsyncTaskMgr
{
public:
	enum CallbackType
	{
		CT_TaskComplete,
		CT_UpdateNode
	};

	typedef boost::function<int32_t(CISSPTaskPtr, CallbackType)> callback_type;

	static AsyncTaskMgr* create(UserContext* userContext);

	virtual ~AsyncTaskMgr() {}

	virtual int32_t addCallback(callback_type callback, AsyncTaskType type) = 0;

	virtual int32_t start() = 0;

	virtual int32_t stop() = 0;

	virtual bool isIdle() = 0;

	virtual int32_t upload(const Path& localPath, const Path& remoteParent) = 0;

	virtual int32_t upload(const std::wstring& localPath, const int64_t& remoteParent, const int64_t& owner_id, const std::wstring& groupId) = 0;

	virtual int32_t upload(const std::wstring& localPath, const std::wstring& remoteParent, const std::wstring& groupId) = 0;

	virtual int32_t download(const Path& remotePath, const Path& localParent) = 0;

	virtual int32_t cancelTask(const AsyncTaskId& taskId) = 0;

	virtual int32_t cancelTask(const AsyncTaskIds& taskIds) = 0;

	virtual int32_t delTask(const AsyncTaskId& taskId) = 0;

	virtual int32_t delTask(const AsyncTaskIds& taskIds) = 0;

	virtual int32_t resumeTask(const AsyncTaskId& taskId) = 0;

	virtual int32_t resumeTask(const AsyncTaskIds& taskIds) = 0;

	virtual int32_t resumeAllTask() = 0;

	virtual int32_t delAllTask() = 0;

	virtual int32_t getTransSize(int64_t& downloadSize, int64_t& uploadSize) = 0;
};

#endif
