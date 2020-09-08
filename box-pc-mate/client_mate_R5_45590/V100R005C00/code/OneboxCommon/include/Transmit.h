#ifndef _ONEBOX_TRANSMIT_H_
#define _ONEBOX_TRANSMIT_H_

#include "CommonDefine.h"
#include "IFile.h"
#include "RestParam.h"
#include "AsyncTaskCommon.h"

class TransTask;
class ITransmit;
class TransDataTable;
typedef std::shared_ptr<ITransmit> ITransmitPtr;
typedef std::shared_ptr<TransTask> TransTaskPtr;
typedef TransDataTable TransSerializer;

enum TRANSMIT_STATUS
{
	TRANSMIT_INIT_START,
	TRANSMIT_INIT_END,
	TRANSMIT_START,
	TRANSMIT_END,
	TRANSMIT_INVALID
};

class ONEBOX_DLL_EXPORT ITransmitNotify
{
public:
	virtual ~ITransmitNotify() {}

	// if you do not care about the notify message, return 0
	virtual int32_t notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote) { return RT_OK; };

	virtual int32_t batchPreuploadNotify(const AsyncTransDetailNode& transDetailNode, const UploadUrlList& uploadUrlList, const FileList& flashUploadList, const FailedList& failedList) { return RT_OK;};

	virtual void notifyProgress(ITransmit* transmit, const int64_t transedSize, const int64_t transIncrement, const int64_t sizeIncrement = 0) { };
};
typedef std::list<ITransmitNotify*> ITransmitNotifies;

class ONEBOX_DLL_EXPORT ITransmit
{
public:
	ITransmit(TransTask* transTask) : transTask_(transTask), status_(TRANSMIT_INVALID) {}

	virtual ~ITransmit() {}

	virtual void transmit() = 0;

	virtual void finishTransmit() = 0;

	virtual void addNotify(ITransmitNotify* notify) { notifies_.push_back(notify); }

	virtual ITransmitNotifies& getNotifies() { return notifies_; }

	virtual void setSerializer(TransSerializer* serializer) { serializer_ = serializer; }

	void setStatus(const TRANSMIT_STATUS status) { status_ = status; }

	TransTask* getTransTask() { return transTask_; }

	TRANSMIT_STATUS getStatus() { return status_; }

protected:
	TransTask* transTask_;
	ITransmitNotifies notifies_;
	TransSerializer* serializer_;
	TRANSMIT_STATUS status_;
};

#endif
