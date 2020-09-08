#ifndef _ONEBOX_REST_DOWNLOAD_H_
#define _ONEBOX_REST_DOWNLOAD_H_

#include "TransTask.h"
#include "IFile.h"

class RestDownload : public ITransmit
{
public:
	RestDownload(UserContext* userContext, TransTask* transTask);

	virtual ~RestDownload();

	virtual void transmit();

	virtual void finishTransmit();

	virtual int64_t getTransLen();
	
private:
	int32_t prepareDownload();

	int32_t startDownload();

	int32_t completeDownload();

	void computeBlockSize();

	void threadDownloadBlock(uint32_t block);

	int32_t getThreadNum();

	uint32_t getDownloadBlockSize();

	float getProcess();

private:
	UserContext* userContext_;
	TransTask* transTask_;
	IFilePtr remoteFile_;
	IFilePtr localFile_;
	boost::thread_group* threadGroup_;
};

#endif