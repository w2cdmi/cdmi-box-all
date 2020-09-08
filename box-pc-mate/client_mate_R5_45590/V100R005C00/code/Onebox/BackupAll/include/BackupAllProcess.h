#ifndef _ONEBOX_BACKUP_ALL_PROCESS_H_
#define _ONEBOX_BACKUP_ALL_PROCESS_H_

#include "UserContext.h"
#include "BackupAllCommon.h"

class BackupAllProcess
{
public:
	virtual ~BackupAllProcess(){}

	static std::auto_ptr<BackupAllProcess> create(UserContext* userContext);

	virtual int32_t stop(bool async = true) = 0;

	virtual int32_t start(bool hasError) = 0;
};

#endif