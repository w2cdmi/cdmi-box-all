#ifndef _MSG_MGR_H_
#define _MSG_MGR_H_

#include "UserContext.h"
#include "MsgInfo.h"

class ONEBOX_DLL_EXPORT MsgMgr
{
public:
	virtual ~MsgMgr(){}

	static MsgMgr* create(UserContext* userContext);

	virtual int32_t getAllMsg(MsgList &msgNodes, MsgStatus status = MS_All) = 0;

	virtual int32_t getMsg(const int64_t startId, MsgList &msgNodes, MsgStatus status = MS_All) = 0;

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed) = 0;

	virtual int32_t deleteMsg(const int64_t msgId) = 0;
};

#endif
