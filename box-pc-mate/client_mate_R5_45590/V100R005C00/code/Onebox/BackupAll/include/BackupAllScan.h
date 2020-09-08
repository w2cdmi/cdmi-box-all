#ifndef _ONEBOX_BACKUP_ALL_SCAN_H_
#define _ONEBOX_BACKUP_ALL_SCAN_H_

#include "UserContext.h"
#include "BackupAllCommon.h"

class BackupAllScan
{
public:
	virtual ~BackupAllScan(){}

	static std::auto_ptr<BackupAllScan> create(UserContext* userContext);

	virtual int32_t start() = 0;

	virtual int32_t stop(bool async = true) = 0;

	virtual bool isScanning() = 0;

	virtual void flushSelectInfo() = 0;
};

#endif