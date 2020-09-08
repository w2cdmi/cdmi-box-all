#ifndef _ONEBOX_TRANS_COMPLETE_TABLE_H_
#define _ONEBOX_TRANS_COMPLETE_TABLE_H_

#include "AsyncTaskCommon.h"

class TransCompleteTable
{
public:
	TransCompleteTable(const std::wstring& path);

	int32_t create();

	int32_t addNodes(const AsyncTransCompleteNodes& nodes);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif