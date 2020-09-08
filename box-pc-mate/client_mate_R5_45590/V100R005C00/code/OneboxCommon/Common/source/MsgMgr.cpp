#include "MsgMgr.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("MsgMgr")
#endif

class MsgMgrImpl : public MsgMgr
{
public:
	MsgMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual int32_t getAllMsg(MsgList &msgNodes, MsgStatus status = MS_All)
	{
		int32_t ret = RT_OK;
		
		msgNodes.clear();
		int64_t totalCnt = 1;
		int64_t offset = 0;
		while(offset < totalCnt)
		{
			boost::this_thread::interruption_point();
			MAKE_CLIENT(client);
			ret = client().getMsg(offset, msgNodes, totalCnt, false, status);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "getAllMsg failed.");
				return ret;
			}
			offset += MSG_PAGE_LIMIT;
		}

		totalCnt = 1;
		offset = 0;
		while(offset < totalCnt)
		{
			boost::this_thread::interruption_point();
			MAKE_CLIENT(client);
			//ret = client().getSysMsg(offset, msgNodes, totalCnt);
			ret = client().getMsg(offset, msgNodes, totalCnt, true);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "getAllMsg failed.");
				return ret;
			}
			offset += MSG_PAGE_LIMIT;
		}

		return ret;
	}

	virtual int32_t getMsg(const int64_t startId, MsgList &msgNodes, MsgStatus status = MS_All)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		ret = client().getMsg(startId, msgNodes, status);

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getMsg from %I64d failed.", startId);
		}

		return ret;
	}

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		if(msgId<0)
		{
			ret = client().updateMsg(-msgId, true, status);
		}
		else
		{
			ret = client().updateMsg(msgId, false, status);
		}

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "updateMsg %I64d failed.", msgId);
		}

		return ret;	
	}

	virtual int32_t deleteMsg(const int64_t msgId)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		ret = client().deleteMsg(msgId);

		if(HTTP_NOT_FOUND == ret)
		{
			ret = RT_OK;
		}

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "deleteMsg %I64d failed.", msgId);
		}

		return ret;		
	}

private:
	UserContext* userContext_;
};

MsgMgr* MsgMgr::create(UserContext* userContext)
{
	return static_cast<MsgMgr*>(new MsgMgrImpl(userContext));
}
