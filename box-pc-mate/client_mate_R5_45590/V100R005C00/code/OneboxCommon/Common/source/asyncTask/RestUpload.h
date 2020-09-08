#ifndef _ONEBOX_REST_UPLOAD_H_
#define _ONEBOX_REST_UPLOAD_H_

#include "TransTask.h"

class RestUpload : public ITransmit
{
public:
	RestUpload(UserContext* userContext, TransTask* transTask);

	virtual void transmit();

	virtual void finishTransmit();

	virtual void addNotify(ITransmitNotify* notify);

	virtual void setSerializer(TransSerializer* serializer);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
