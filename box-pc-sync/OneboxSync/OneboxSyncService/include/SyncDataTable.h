#ifndef _ONEBOX_SYNCDATA_TABLE_H_
#define _ONEBOX_SYNCDATA_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class SyncDataTable
{
public:
	virtual ~SyncDataTable(){}

	virtual int32_t getSyncData(RemoteNodes& syncData) = 0;

	virtual int32_t getLoginSyncData(RemoteNodes& syncData) = 0;

	virtual int64_t getMaxVersion() = 0;

	static std::auto_ptr<SyncDataTable> create(const std::wstring& path);
};

#endif
