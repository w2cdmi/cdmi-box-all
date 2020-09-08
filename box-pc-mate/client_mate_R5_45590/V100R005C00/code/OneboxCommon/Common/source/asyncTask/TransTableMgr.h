#ifndef _ONEBOX_TRANS_TABLE_MGR_H_
#define _ONEBOX_TRANS_TABLE_MGR_H_

#include "UserContext.h"
#include "TransRootTable.h"
#include "TransDetailTable.h"
#include "TransDataTable.h"
#include "TransCompleteTable.h"

class TransTableMgr : public std::enable_shared_from_this<TransTableMgr>
{
public:
	TransTableMgr(UserContext* userContext, const std::wstring& path);

	TransRootTable* getRootTable(const AsyncTransType type);

	TransRootTable* getRootTable(const std::wstring& group);

	TransDetailTablePtr getDetailTable(const std::wstring& group);

	TransDataTable* getDataTable();

	TransCompleteTable* getCompleteTable();

	TransDetailTable* createDetailTable(const std::wstring& group);

	int32_t removeDetailTable(const std::wstring& group);

	int32_t removeDetailTable(const AsyncTransType type);

	int32_t removeZombieDetailTable();

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
