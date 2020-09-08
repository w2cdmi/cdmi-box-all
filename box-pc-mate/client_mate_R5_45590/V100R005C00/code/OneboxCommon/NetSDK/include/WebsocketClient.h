#ifndef __CLOUD__STORE__WEBSOCKET_CLIENT__H__
#define __CLOUD__STORE__WEBSOCKET_CLIENT__H__

#include "CommonDefine.h"
#include "Token.h"
#include "Configure.h"
#include "MsgInfo.h"

class ONEBOX_DLL_EXPORT WebsocketClient
{
public:
    WebsocketClient(const TOKEN& token, const Configure& configure);

	int32_t getMsg(MsgInfoChangeHandler handler);

private:
	class Impl;
	std::shared_ptr<Impl> impl;
};

#endif
