#ifndef _ONEBOX_MSG_LISTENER_H_
#define _ONEBOX_MSG_LISTENER_H_

#include "UserContext.h"
#include "CacheMsgInfo.h"

class ONEBOX_DLL_EXPORT MsgListener
{
public:
	virtual ~MsgListener(){}

	static MsgListener* getInstance(UserContext* userContext);

	virtual int32_t start() = 0;

	virtual int32_t stop() = 0;

private:
	static std::auto_ptr<MsgListener> instance_;
};

#endif