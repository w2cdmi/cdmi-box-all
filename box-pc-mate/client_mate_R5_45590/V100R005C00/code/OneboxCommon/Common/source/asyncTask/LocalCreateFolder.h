#ifndef _ONEBOX_LOCAL_CREATE_FOLDER_H_
#define _ONEBOX_LOCAL_CREATE_FOLDER_H_

#include "TransTask.h"

class LocalCreateFolder : public ITransmit
{
public:
	LocalCreateFolder(UserContext* userContext, TransTask* transTask);

	virtual void transmit();

	virtual void finishTransmit();

	virtual void addNotify(ITransmitNotify* notify);

	virtual void setSerializer(TransSerializer* serializer);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
