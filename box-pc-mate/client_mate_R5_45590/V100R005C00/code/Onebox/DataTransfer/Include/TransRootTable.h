#ifndef _ONEBOX_TRANS_ROOT_TABLE_H_
#define _ONEBOX_TRANS_ROOT_TABLE_H_

#include "AsyncTaskCommon.h"

class TransRootTable
{
public:
	TransRootTable(const std::wstring& path);

	int32_t create();

	int32_t addNodes(const AsyncTransRootNodes& nodes);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif