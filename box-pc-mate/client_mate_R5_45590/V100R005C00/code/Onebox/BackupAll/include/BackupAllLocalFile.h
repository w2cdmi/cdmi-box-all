#ifndef _ONEBOX_BACKUP_ALL_LOCALFILE_H_
#define _ONEBOX_BACKUP_ALL_LOCALFILE_H_

#include "UserContext.h"
#include "BackupAllLocalDb.h"
#include "BackupAllCommon.h"

class BackupAllLocalFile
{
public:
	virtual ~BackupAllLocalFile(){}

	static std::auto_ptr<BackupAllLocalFile> create(UserContext* userContext);

	virtual void backup(BackupAllLocalDb* pLocalDb, BATaskLocalNode& node) = 0;

	virtual int32_t create(const int64_t& parentId, BATaskLocalNode& node) = 0;

	virtual int32_t buildPathFromRoot(const std::wstring& path, int64_t& localParent) = 0;

};

#endif