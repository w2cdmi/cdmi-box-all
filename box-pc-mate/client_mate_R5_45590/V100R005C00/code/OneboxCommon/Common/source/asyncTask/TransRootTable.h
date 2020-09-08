#ifndef _ONEBOX_TRANS_ROOT_TABLE_H_
#define _ONEBOX_TRANS_ROOT_TABLE_H_

#include "AsyncTaskCommon.h"

class TransRootTable
{
public:
	TransRootTable(UserContext* userContext, const std::wstring& path);

	int32_t create(const AsyncTransCacheMode cacheMode = ATCM_NoCache);

	int32_t flushCache();

	int32_t getTopNodes(AsyncTransRootNodes& nodes, const uint32_t count);

	int32_t getTopScanNode(AsyncTransRootNode& node);

	int32_t getNode(const std::wstring& group, AsyncTransRootNode& node);

	int32_t getNodes(const AsyncTransType type, AsyncTransRootNodes& nodes, const Page& page);

	int32_t getNodes(const AsyncTransStatus status, AsyncTransRootNodes& nodes, const Page& page);

	int32_t addNode(const AsyncTransRootNode& node);

	int32_t addNodes(const AsyncTransRootNodes& nodes);

	int32_t updateNode(const AsyncTransRootNode& node);

	int32_t deleteNode(const std::wstring& group);

	int32_t deleteNodes(const AsyncTransType type);

	int32_t updateStatus(const std::wstring& group, const AsyncTransStatus status);

	int32_t updateStatus(const AsyncTransType type, const AsyncTransStatus status);

	int32_t addStatusEx(const std::wstring& group, const AsyncTransStatusEx status);

	int32_t removeStatusEx(const std::wstring& group, const AsyncTransStatusEx status);

	int32_t updateStatus(const AsyncTransStatus status, const AsyncTransStatus value);

	int32_t updateSize(const std::wstring& group, const int64_t size);

	int32_t updateTransedSizeAndSize(const std::wstring& group, const int64_t transIncrement, const int64_t sizeIncrement);

	int32_t updatePriority(const std::wstring& group, const bool inc = false);

	int32_t updateStatusAndErrorCode(const std::wstring& group, const AsyncTransStatus status, const int32_t errorCode);

	int32_t getNodesCount(const AsyncTransType type);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif