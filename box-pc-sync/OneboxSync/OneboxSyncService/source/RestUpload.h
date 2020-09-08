#ifndef _ONEBOX_REST_UPLOAD_H_
#define _ONEBOX_REST_UPLOAD_H_

#include "TransTask.h"
#include "IFile.h"

class RestUpload : public ITransmit
{
public:
	RestUpload(UserContext* userContext, TransTask* transTask);

	virtual ~RestUpload();

	virtual void transmit();

	virtual void finishTransmit();

	virtual int64_t getTransLen();

private:
	int32_t prepareUpload();

	int32_t totalUpload();

	int32_t startPartialUpload();

	int32_t getThreadNum();

	void partialUpload(bool loop);

	int32_t completePartialUpload();

	int32_t getPartialId();

	int32_t initPartialIds(const std::vector<int32_t>& completePartialIds = std::vector<int32_t>(0));

	float getProcess();

private:
	UserContext* userContext_;
	TransTask* transTask_;
	IFilePtr remoteFile_;
	IFilePtr localFile_;
	int32_t curPartialId_;
	std::vector<int32_t> partialIds_;
	boost::thread_group* threadGroup_;
	boost::mutex mutex_;
};

#endif
