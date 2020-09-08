#ifndef _ONEBOX_TRANS_DETAIL_TABLE_H_
#define _ONEBOX_TRANS_DETAIL_TABLE_H_

#include "AsyncTaskCommon.h"

class TransDetailTable
{
public:
	TransDetailTable(UserContext* userContext, const std::wstring& path);

	int32_t create(AsyncTransRootNode& rootNode);

	int32_t remove();

	int32_t getTopNodes(AsyncTransDetailNodes& nodes, const int64_t fileSize = 0, const uint32_t count = 1);

	int32_t getTopScanNode(AsyncTransDetailNode& node);

	int32_t getNode(const std::wstring& source, AsyncTransDetailNode& node);

	int32_t getNodes(const AsyncTransStatus status, AsyncTransDetailNodes& nodes, const Page& page);

	int32_t addNode(const AsyncTransDetailNode& node);

	int32_t addNodes(const AsyncTransDetailNodes& nodes);

	int32_t deleteNode(const std::wstring& source);

	int32_t updateStatus(const std::wstring& source, const AsyncTransStatus status);

	int32_t updateStatus(const AsyncTransStatus status, const AsyncTransStatus value);

	int32_t addStatusEx(const std::wstring& source, const AsyncTransStatusEx status);

	int32_t removeStatusEx(const std::wstring& source, const AsyncTransStatusEx status);

	int32_t removeStatusExByParent(const std::wstring& parent, const AsyncTransStatusEx status);

	int32_t updateParentAndStatus(const std::wstring& oldParent, const std::wstring& newParent);

	int32_t updateStatusAndErrorCode(const std::wstring& source, const AsyncTransStatus status, const int32_t errorCode);

	int32_t getScanNodesCount();

	int32_t getNodesCount(const AsyncTransStatus status);

	int32_t listFilesByStatusEx(const std::wstring& parent, const AsyncTransStatusEx statusEx, const Page& page, AsyncTransDetailNodes& nodes);

	int32_t deleteNodes(const std::list<std::wstring>& sources);

	int32_t batchUpdateParentAndErrorCode(const std::list<std::wstring>& sources, const std::wstring& parent, const int32_t errorCode);

	int32_t updateParent(const std::wstring& oldParent, const std::wstring& newParent);

	int32_t updateStatusForBatchUpload(const std::wstring& parent);

	int32_t batchUpdateStatus(const std::list<std::wstring>& sources);

	int32_t setNextVirtualParent(const AsyncTransDetailNode& transDetailNode);

	bool isError();

	int32_t updateSize(const std::wstring& source, const int64_t sizeIncement);

	int32_t checkAndAddNodes(const AsyncTransDetailNodes& nodes);

	int32_t getRepeatedNodes(AsyncTransDetailNodes& repeatedNodes, const AsyncTransDetailNodes& nodes);

	int32_t checkAndDeleteNode(const AsyncTransDetailNode& node);

	int32_t restoreVirtualParent();

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};
typedef std::shared_ptr<TransDetailTable> TransDetailTablePtr;

#endif