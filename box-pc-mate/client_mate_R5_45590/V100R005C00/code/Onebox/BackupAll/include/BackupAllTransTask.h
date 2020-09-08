#ifndef _ONEBOX_BACKUPALL_TRANSTASKC_H_
#define _ONEBOX_BACKUPALL_TRANSTASKC_H_

#include "UserContext.h"
#include "Path.h"
#include "BackupAllCommon.h"

class BackupAllTransTask
{
public:
	virtual ~BackupAllTransTask(){}

	static BackupAllTransTask* getInstance(UserContext* userContext);

	virtual void setTransmitNotify() = 0;

	virtual int32_t upload(const std::wstring& localPath, const int64_t& remoteParent, const int64_t& size) = 0;

	virtual int32_t uploadSubFiles(const BATaskLocalNode& parentNode, const std::map<std::wstring, int64_t>& subFiles) = 0;

private:
	static BackupAllTransTask* instance_;

};

#endif