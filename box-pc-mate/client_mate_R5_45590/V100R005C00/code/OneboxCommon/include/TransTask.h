#ifndef _ONEBOX_TRANS_TASK_H_
#define _ONEBOX_TRANS_TASK_H_

#include "AsyncTaskCommon.h"
#include "Transmit.h"
#include "UserContext.h"
#include <ISSPThreads_md.h>

class TransTaskErrorCode;

class ONEBOX_DLL_EXPORT TransTask : public CISSPTask
{
public:
	friend TransTaskErrorCode;

	virtual ~TransTask() {}

	static TransTask* create(UserContext* userContext, 
		AsyncTransDetailNode& transDetailNode, 
		TransSerializer* searializer, 
		ITransmitNotifies& transmitNotifies, 
		CISSPNotifyPtr notify);

	virtual AsyncTransDetailNode& getTransDetailNode() = 0;

	virtual ITransmitNotifies& getTransmitNotifies() = 0;
};

class TransTaskErrorCode
{
public:
	TransTaskErrorCode(TransTask* transTask) : transTask_(transTask) { }
	void SetErrorCode(const int32_t error) 
	{
		if (RT_OK == transTask_->GetErrorCode())
		{
			return transTask_->SetErrorCode(error);
		}
	}
private:
	TransTask* transTask_;
};

inline void freeWrapper(unsigned char* buf)
{
	if(buf) free(buf);
}

extern int32_t getFingerprint(Fingerprint& fingerprint, const std::wstring& path, CISSPTask* task);

#endif
