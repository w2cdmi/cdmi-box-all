#include "MsgListener.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "CacheMsgInfo.h"
#include "MsgMgr.h"
#include "NotifyMgr.h"
#include <boost/thread.hpp>
#include "WebsocketClient.h"
#include "CredentialMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("MsgListener")
#endif

class MsgListenerImpl : public MsgListener
{
public:
	MsgListenerImpl(UserContext* userContext):userContext_(userContext), pMsgTable_(NULL), stop_(true)
	{
	}

	virtual ~MsgListenerImpl()
	{
		try
		{
			if (pMsgTable_)
			{
				delete pMsgTable_;
				pMsgTable_ = NULL;
			}
		}
		catch(...){}
	}

	virtual int32_t start()
	{
		if (stop_)
		{
			msgListenerThread_ = boost::thread(boost::bind(&MsgListenerImpl::listen, this));
			stop_ = false;
		}		
		return RT_OK;
	}
	
	virtual int32_t stop()
	{
		if (!stop_)
		{
			stop_ = true;
			msgListenerThread_.interrupt();
			// WEBSOCKET use asio, can not be interrupt, 
			// so just interrupt the thread to avoid memory leak
			//msgListenerThread_.join();
		}		
		return RT_OK;
	}

private:
	void listen()
	{
		try
		{
			initMsg();

			while (!stop_)
			{
				WebsocketClient client(userContext_->getCredentialMgr()->getCredentialInfo(), 
					*(userContext_->getConfigureMgr()->getConfigure()));
				int32_t ret = client.getMsg(std::bind(&MsgListenerImpl::msgInfoChangeHandler, this, std::placeholders::_1));
				if (RT_OK != ret)
				{
					SERVICE_ERROR(MODULE_NAME, ret, "getMsgListener failed.");
				}
				SLEEP(boost::posix_time::seconds(10));
			}
		}
		catch(boost::thread_interrupted&)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_CANCEL, "message listen thread interrupted.");
		}
	}

	void initMsg()
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return;
		}
		MsgList msgNodes;
		int32_t ret = userContext_->getMsgMgr()->getAllMsg(msgNodes);
		if(RT_OK != ret)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "initMsg. List msg failed. Ret: %d", ret);
			return;
		}
		
		pCache->deleteAll();
		pCache->replaceMsg(msgNodes);
		//pCache->deleteMsg(msgKeyInfo);
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "initMsg. msg size:%d", msgNodes.size());

		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_MSG_CHANGE));
	}

	int32_t msgInfoChangeHandler(const MsgNode& msg)
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		MsgList msgNodes;
		msgNodes.push_back(msg);

		int32_t ret = pCache->replaceMsg(msgNodes);
		if(RT_OK!=ret)
		{
			return ret;
		}

		NOTIFY_PARAM parm;
		parm.type = NOTIFY_MSG_MSG_CHANGE;
		parm.msg1 = SD::Utility::String::type_to_string<std::wstring>((int)msg.type);
		parm.msg2 = SD::Utility::String::utf8_to_wstring(msg.providerName);
		parm.msg3 = SD::Utility::String::utf8_to_wstring(msg.params.nodeName);
		parm.msg5 = SD::Utility::String::utf8_to_wstring(msg.params.title);
		parm.msg6 = SD::Utility::String::utf8_to_wstring(msg.params.currentRole);
		if (MT_Share == msg.type || MT_Share_Delete == msg.type)
		{
			parm.msg4 = SD::Utility::String::type_to_string<std::wstring>((int32_t)msg.params.nodeType);
		}
		else if (MT_TeamSpace_Add == msg.type  
			|| MT_TeamSpace_Delete == msg.type 
			|| MT_TeamSpace_Leave == msg.type
			|| MT_TeamSpace_Upload == msg.type
			|| MT_TeamSpace_RoleUpdate == msg.type)
		{
			parm.msg4 = SD::Utility::String::utf8_to_wstring(msg.params.teamSpaceName);
		}
		else if (MT_Group_Add == msg.type  
			|| MT_Group_Delete == msg.type 
			|| MT_Group_Leave == msg.type
			|| MT_Group_RoleUpdate == msg.type)
		{
			parm.msg4 = SD::Utility::String::utf8_to_wstring(msg.params.groupName);
		}
		userContext_->getNotifyMgr()->notify(parm);
		return RT_OK;
	}

	virtual CacheMsgInfo* getMsgTable()
	{
		if(NULL == pMsgTable_)
		{
			std::wstring parentPath = userContext_->getConfigureMgr()->getConfigure()->userDataPath() + PATH_DELIMITER;
			pMsgTable_ = CacheMsgInfo::create(userContext_, parentPath);
		}
		return pMsgTable_;
	}

private:
	UserContext* userContext_;
	CacheMsgInfo* pMsgTable_;
	bool stop_;
	boost::thread msgListenerThread_;
};

std::auto_ptr<MsgListener> MsgListener::instance_(NULL);

MsgListener* MsgListener::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new MsgListenerImpl(userContext));
	}
	return instance_.get();
}