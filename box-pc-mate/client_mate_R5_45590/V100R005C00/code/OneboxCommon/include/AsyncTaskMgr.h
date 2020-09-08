#ifndef _ONEBOX_ASYNC_TASK_MGR_H_
#define _ONEBOX_ASYNC_TASK_MGR_H_

#include "Path.h"
#include "AsyncTaskCommon.h"
#include "Transmit.h"

struct st_AsyncUploadTaskParam
{
	std::wstring localPath;
	FILE_TYPE fileType;
	int64_t size;
	int64_t remoteParentId;
};
typedef std::shared_ptr<st_AsyncUploadTaskParam> AsyncUploadTaskParam;
typedef std::list<AsyncUploadTaskParam> AsyncUploadTaskParams;

struct st_AsyncDownloadTaskParam
{
	int64_t remoteId;
	std::wstring name;
	FILE_TYPE fileType;
	int64_t size;
	std::wstring localParentPath;
};
typedef std::shared_ptr<st_AsyncDownloadTaskParam> AsyncDownloadTaskParam;
typedef std::list<AsyncDownloadTaskParam> AsyncDownloadTaskParams;

struct st_AsyncUploadTaskParamEx : public st_AsyncUploadTaskParam
{
	std::wstring group;
};
typedef std::shared_ptr<st_AsyncUploadTaskParamEx> AsyncUploadTaskParamEx;
typedef std::list<AsyncUploadTaskParamEx> AsyncUploadTaskParamExs;

struct st_AsyncDownloadTaskParamEx : public st_AsyncDownloadTaskParam
{
	std::wstring group;
};
typedef std::shared_ptr<st_AsyncDownloadTaskParamEx> AsyncDownloadTaskParamEx;
typedef std::list<AsyncDownloadTaskParamEx> AsyncDownloadTaskParamExs;

class ONEBOX_DLL_EXPORT AsyncTaskMgr
{
public:
	static AsyncTaskMgr* create(UserContext* userContext);

	virtual ~AsyncTaskMgr() {}

	virtual int32_t init() = 0;

	virtual int32_t release() = 0;

	virtual int32_t setTransmitNotify(const AsyncTransType type, ITransmitNotify* notify) = 0;

	virtual ITransmitNotify* getTransmitNotify(const AsyncTransType type) = 0;

	virtual int32_t upload(
		const std::wstring& group, /* group id, GUID */
		const Path& localPath, /* path, type is required */
		const Path& remoteParent, /* id, ownerId is required */
		const AsyncTransType type = ATT_Upload
		) = 0;

	virtual int32_t download(
		const std::wstring& group, /* group id, GUID */
		const Path& remotePath, /* id, name, type, ownerId is required */
		const Path& localParent, /* path is required */
		const int64_t size = 0, /* if file, size is required*/
		const AsyncTransType type = ATT_Download
		) = 0;

	virtual int32_t uploads(const AsyncUploadTaskParamExs& tasks, const AsyncTransType type, const int64_t userId) = 0;

	virtual int32_t downloads(const AsyncDownloadTaskParamExs& tasks, const AsyncTransType type, const int64_t userId) = 0;

	// before call addAsync*Tasks should call beginAddAsyncTasks first and call endAddAsyncTasks at last
	virtual int32_t beginAddAsyncTasks(const std::wstring& group, const int64_t userId, const AsyncTransType type) = 0;

	virtual int32_t addAsyncUploadTasks(const std::wstring& group, const AsyncUploadTaskParams& tasks) = 0;

	virtual int32_t addAsyncDownloadTasks(const std::wstring& group, const AsyncDownloadTaskParams& tasks) = 0;

	virtual int32_t endAddAsyncTasks(const std::wstring& group, const AsyncTransType type) = 0;
	
	virtual int32_t pauseTask(const std::wstring& group) = 0;

	virtual int32_t pauseTask(const AsyncTransType type) = 0;

	virtual int32_t resumeTask(const std::wstring& group) = 0;

	virtual int32_t resumeTask(const AsyncTransType type) = 0;

	virtual int32_t deleteTask(const std::wstring& group) = 0;

	virtual int32_t deleteTask(const AsyncTransType type) = 0;

	virtual int32_t getTask(const std::wstring& group, AsyncTransRootNode& node) = 0;

	virtual int32_t getTasks(const AsyncTransType type, AsyncTransRootNodes& nodes, const Page& page = PAGE_OBJ) = 0;

	virtual int32_t getTasksCount(const AsyncTransType type) = 0;

	virtual int32_t getTasksCount(const std::wstring& group, AsyncTransStatus status) = 0;

	virtual int32_t getErrorTasks(const std::wstring& group, AsyncTransDetailNodes& nodes, const Page& page = PAGE_OBJ) = 0;

	virtual int32_t getErrorTasksCount(const std::wstring& group) = 0;

	virtual int32_t deleteErrorTask(const std::wstring& group, const std::wstring source) = 0;

	virtual int32_t deleteHistoricalTask(const std::wstring& group) = 0;

	virtual int32_t deleteHistoricalTask() = 0;

	virtual int32_t getHistoricalTask(const std::wstring& group, AsyncTransCompleteNode& node) = 0;

	virtual int32_t getHistoricalTasks(AsyncTransCompleteNodes& nodes, const Page& page = PAGE_OBJ) = 0;

	virtual int32_t addHistoricalTask(const AsyncTransCompleteNode& node) = 0;

	virtual int32_t deleteTasks(const std::wstring& group, const std::list<std::wstring>& sources) = 0;
};

#endif
