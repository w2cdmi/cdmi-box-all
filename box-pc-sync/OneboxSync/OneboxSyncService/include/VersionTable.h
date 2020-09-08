#ifndef _ONEBOX_VERSION_TABLE_H_
#define _ONEBOX_VERSION_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class VersionTable
{
public:
	virtual ~VersionTable(){}

	static std::auto_ptr<VersionTable> create(const std::wstring& parent);
};

#endif
