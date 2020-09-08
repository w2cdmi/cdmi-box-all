#ifndef _ONEBOX_REST_DOWNLOAD_H_
#define _ONEBOX_REST_DOWNLOAD_H_

#include "TransTask.h"

class RestDownload : public ITransmit
{
public:
	RestDownload(UserContext* userContext, TransTask* transTask);

	virtual void transmit();

	virtual void finishTransmit();

	virtual void addNotify(ITransmitNotify* notify);

	virtual void setSerializer(TransSerializer* serializer);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif