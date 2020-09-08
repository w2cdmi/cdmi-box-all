#ifndef _ONEBOX_BACKUPALL_DB_H_
#define _ONEBOX_BACKUPALL_DB_H_

#include "BackupAllCommon.h"
#include "CppSQLite3.h"
#include <set>

class BackupAllTaskTable
{
public:
	virtual ~BackupAllTaskTable(){}

	static BackupAllTaskTable* create(const std::wstring& parent);

	virtual int32_t addNode(const BATaskNode& node) = 0;

	virtual int32_t updatePathInfo(const std::set<std::wstring>& selectPath, const std::set<std::wstring>& filterPath) = 0;

};

#endif
