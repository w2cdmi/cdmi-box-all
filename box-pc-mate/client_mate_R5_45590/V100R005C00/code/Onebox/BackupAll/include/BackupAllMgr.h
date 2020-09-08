#ifndef _ONEBOX_BACKUP_ALL_MGR_H_
#define _ONEBOX_BACKUP_ALL_MGR_H_

#include "UserContext.h"
#include "BackupAllCommon.h"
#include "BackupAllTransTask.h"
#include <set>

class ONEBOX_DLL_EXPORT BackupAllMgr
{
public:
	static BackupAllMgr* getInstance(UserContext* userContext);

	static void releaseInstance();

	virtual ~BackupAllMgr(){}

	virtual int32_t stop(bool async = true) = 0;

	virtual int32_t setBackupTask(const std::list<std::wstring>& rootList, const std::list<std::wstring>& filterList,
		const int32_t backupType, const std::wstring& period, const std::wstring& rootName) = 0;

	virtual int32_t closeBackupTask() = 0;

	virtual int32_t pauseBackupTask() = 0;

	virtual int32_t resumeBackupTask() = 0;

	virtual int32_t restartBackupTask() = 0;

	virtual int32_t getFailedList(BATaskLocalNodeList& failedList, int32_t offset, int32_t limit) = 0;

	virtual int32_t ignoreFailedNode(const BATaskLocalNode& failedNode) = 0;

	virtual BATaskInfo* getTaskInfo() = 0;

	virtual int32_t getPathInfo(std::set<std::wstring>& selectPath, std::set<std::wstring>& filterPath) = 0;

	virtual void setDefault(const std::wstring& rootName) = 0;

	virtual bool isUpdate() = 0;

private:
	static BackupAllMgr* instance_;
};

#endif