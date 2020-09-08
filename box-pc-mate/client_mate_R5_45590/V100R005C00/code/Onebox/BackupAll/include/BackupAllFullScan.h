#ifndef _ONEBOX_BACKUP_ALL_FULLSCAN_H_
#define _ONEBOX_BACKUP_ALL_FULLSCAN_H_

#include "UserContext.h"
#include "BackupAllCommon.h"

class BackupAllFullScan
{
public:
	virtual ~BackupAllFullScan(){}

	static std::auto_ptr<BackupAllFullScan> create(UserContext* userContext);

	virtual void backupDisk(const std::wstring& disk, const USN& lastUsn, const int64_t& rootRemoteId) = 0;

};

#endif