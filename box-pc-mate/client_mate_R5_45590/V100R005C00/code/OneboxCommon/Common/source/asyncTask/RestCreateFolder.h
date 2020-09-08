#ifndef _ONEBOX_REST_CREATE_FOLDER_H_
#define _ONEBOX_REST_CREATE_FOLDER_H_

#include "TransTask.h"

class RestCreateFolder : public ITransmit
{
public:
	RestCreateFolder(UserContext* userContext, TransTask* transTask);

	virtual void transmit();

	virtual void finishTransmit();

	virtual void addNotify(ITransmitNotify* notify);

	virtual void setSerializer(TransSerializer* serializer);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
