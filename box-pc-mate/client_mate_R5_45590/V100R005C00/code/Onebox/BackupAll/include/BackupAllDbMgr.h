#ifndef _ONEBOX_BACKUPALL_DBMGR_H_
#define _ONEBOX_BACKUPALL_DBMGR_H_

#include "UserContext.h"
#include "BackupAllLocalDb.h"
#include "BackupAllTaskDb.h"

class BackupAllDbMgr
{
public:
	virtual ~BackupAllDbMgr(){}

	static BackupAllDbMgr* getInstance(UserContext* userContext);

	virtual BackupAllLocalDb* getBALocalDb(const std::wstring& path) = 0;

	virtual BackupAllTaskDb* getBATaskDb() = 0;

private:
	static std::auto_ptr<BackupAllDbMgr> instance_;
};

#endif
