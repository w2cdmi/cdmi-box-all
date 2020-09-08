#ifndef _ONEBOX_TRANS_DETAIL_TABLE_H_
#define _ONEBOX_TRANS_DETAIL_TABLE_H_

#include "AsyncTaskCommon.h"

class TransDetailTable
{
public:
	TransDetailTable(const std::wstring& path);

	int32_t create(AsyncTransRootNode& rootNode);

	int32_t addNode(const AsyncTransDetailNode& node);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif