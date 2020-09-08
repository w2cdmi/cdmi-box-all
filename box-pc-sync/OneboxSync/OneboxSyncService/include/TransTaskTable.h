#ifndef _ONEBOX_TRANSTASK_TABLE_H_
#define _ONEBOX_TRANSTASK_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class TransTaskTable
{
public:
	virtual ~TransTaskTable(){}

	static std::auto_ptr<TransTaskTable> create(const std::wstring& parent);

	virtual int32_t addNode(const AsyncTransTaskNode& node) = 0;

	virtual int32_t updateNode(const AsyncTransTaskNode& node) = 0;

	virtual int32_t deleteNode(const AsyncTaskId& id) = 0;

	virtual int32_t addNodes(const AsyncTransTaskNodes& nodes) = 0;

	virtual int32_t updateNodes(const AsyncTransTaskNodes& nodes) = 0;

	virtual int32_t deleteNodes(const AsyncTaskIds& ids) = 0;

	virtual int32_t deleteNodes(KeyType keyType) = 0;

	virtual int32_t getNode(const AsyncTaskId& id, AsyncTransTaskNode& node) = 0;

	virtual int32_t getNodes(const AsyncTaskType type, AsyncTransTaskNodes& nodes) = 0;

	virtual int32_t getTopNode(AsyncTransTaskNode& node) = 0;

	virtual int32_t getRunningTaskEx(const IdList& idList, KeyType keyType, AsyncTaskIds& runningTaskIds) = 0;

	virtual int32_t getRunningTaskEx(KeyType keyType, AsyncTaskIds& runningTaskIds) = 0;

	virtual int32_t updateStatus(const AsyncTaskId& id, const AsyncTransTaskStatus status) = 0;

	virtual int32_t updateStatus(const AsyncTaskIds& ids, const AsyncTransTaskStatus status) = 0;

	virtual int32_t updateStatus(const AsyncTransTaskStatus oldStatus, const AsyncTransTaskStatus status) = 0;

	virtual int32_t updateStatus(const AsyncTransTaskStatus status) = 0;

	virtual bool isExist(const AsyncTaskId& id) = 0;

	virtual bool isUploading(const int64_t& id, const int64_t& parent, const std::wstring& name) = 0;

	virtual int32_t getAutoTransTaskCnt() = 0;

	virtual int32_t getCount(const AsyncTransTaskStatus status) = 0;

	virtual int32_t updatePriority(const AsyncTaskId& id, bool inc = false) = 0;

	virtual bool isExist(const std::wstring& groupId) = 0;

	virtual int32_t getNodes(const std::wstring & groupId, AsyncTransTaskNodes& nodes) = 0;

	virtual int32_t deleteNodes(const std::wstring & groupId) = 0;
};

#endif
