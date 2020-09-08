#ifndef _ONEBOX_BACKUP_ALL_INCSCAN_H_
#define _ONEBOX_BACKUP_ALL_INCSCAN_H_

#include "UserContext.h"
#include "BackupAllCommon.h"

class BackupAllIncScan
{
public:
	virtual ~BackupAllIncScan(){}

	static std::auto_ptr<BackupAllIncScan> create(UserContext* userContext);

	virtual int32_t incBackup(const std::wstring& disk, const USN& lastUsn) = 0;

};

#endif