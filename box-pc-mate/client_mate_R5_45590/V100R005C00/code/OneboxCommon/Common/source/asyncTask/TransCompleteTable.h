#ifndef _ONEBOX_TRANS_COMPLETE_TABLE_H_
#define _ONEBOX_TRANS_COMPLETE_TABLE_H_

#include "AsyncTaskCommon.h"

class TransCompleteTable
{
public:
	TransCompleteTable(UserContext* userContext, const std::wstring& path);

	int32_t create();

	int32_t getNode(const std::wstring& group, AsyncTransCompleteNode& node);

	int32_t getNodes(const AsyncTransType type, AsyncTransCompleteNodes& nodes, const Page& page);

	int32_t addNode(const AsyncTransCompleteNode& node);

	int32_t deleteNode(const std::wstring& group);

	int32_t addNodes(const AsyncTransCompleteNodes& nodes);

	int32_t deleteNodes(const AsyncTransType type);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif