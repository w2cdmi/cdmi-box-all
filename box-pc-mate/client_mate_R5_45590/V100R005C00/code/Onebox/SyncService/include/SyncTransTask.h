#ifndef _ONEBOX_SYNC_TRANSTASKC_H_
#define _ONEBOX_SYNC_TRANSTASKC_H_

#include "UserContext.h"
#include "Path.h"

class SyncTransTask
{
public:
	virtual ~SyncTransTask(){}

	static SyncTransTask* getInstance(UserContext* userContext);

	virtual void setAdapter() = 0;

	virtual int32_t upload(const Path& localPath, const Path& remoteParent) = 0;

	virtual int32_t download(const Path& remotePath, const Path& localParent) = 0;

private:
	static std::auto_ptr<SyncTransTask> instance_;

};

#endif