#ifndef _ONEBOX_TRANS_TASK_H_
#define _ONEBOX_TRANS_TASK_H_

#include "SyncCommon.h"
#include <ISSPThreads_md.h>
#include "UserContext.h"

enum TASK_THREAD_NUM
{
	LowTaskThreadNum = 1, 
	MiddleTaskThreadNum = 2, 
	HightTaskThreadNum = 4
};

enum TASK_FILE_SIZE_LEVEL
{
	LowTaskFileSizeLevel = (20*1024*1024), //20MB
	HightTaskFileSizeLevel = (100*1024*1024) //100MB
};

enum CustomNotifyType
{
	Process,
	FileInfo,
	FolderInfo,
	AddTask,
	Invalid
};

struct CustomNotifyInfo
{
	int32_t type;
	void* content;

	CustomNotifyInfo()
	{
		type = Invalid;
		content = NULL;
	}
};

class ITransmit
{
public:
	virtual ~ITransmit() {}

	virtual void transmit() = 0;

	virtual void finishTransmit() = 0;

	virtual int64_t getTransLen() {return 0;};
};

class TransTaskNotify;
class RestUpload;
class RestDownload;
class RestCreate;

class TransTask : public CISSPTask
{
	friend TransTaskNotify;
	friend RestUpload;
	friend RestDownload;
	friend RestCreate;
public:
	virtual ~TransTask() {}

	static TransTask* create(UserContext* userContext, std::shared_ptr<AsyncTransTaskNode> transTaskNode, CISSPNotifyPtr& notify);

	virtual AsyncTransTaskNode& getTaskNode() = 0;

	virtual CustomNotifyInfo& getCustomInfo() = 0;

	virtual int64_t getTransLen() = 0;

	virtual void notifyCustomInfo() = 0;

	virtual FileSignature getSignature(const std::wstring& localPath) = 0;
};

inline void freeWrapper(unsigned char* buf)
{
	if(buf) free(buf);
}

#endif
