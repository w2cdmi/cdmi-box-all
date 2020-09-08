#ifndef _ONEBOX_OUTLOOK_TRANSMIT_NOTIFY_H_
#define _ONEBOX_OUTLOOK_TRANSMIT_NOTIFY_H_

#include "Transmit.h"
#include "TransTask.h"
#include "OutlookTable.h"
#include "ShareResMgr.h"
#include "Utility.h"

class OutlookTransmitNotify : public ITransmitNotify
{
public:
	OutlookTransmitNotify(UserContext* userContext, TransTableMgr* transTableMgr, OutlookTable* outlookTable)
		:userContext_(userContext)
		,transTableMgr_(transTableMgr)
		,outlookTable_(outlookTable)
	{

	}

	// if you do not care about the notify message, return 0
	virtual int32_t notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote)
	{
		// only care about the finished transmit status
		if (transmit->getStatus() != TRANSMIT_END)
		{
			return RT_OK;
		}

		TransTask* transTask = transmit->getTransTask();
		if (transTask->IsError() || transTask->IsCancel())
		{
			return RT_OK;
		}
		
		if (outlookTable_ == NULL || remote.id == INVALID_ID)
		{
			return RT_INVALID_PARAM;
		}

		AsyncTransDetailNode& transDetailNode = transTask->getTransDetailNode();
		// create shareLink
		ShareLinkNode shareLinkNode;
		int32_t ret = userContext_->getShareResMgr()->getShareLink(remote.id, shareLinkNode);
		if (RT_OK != ret)
		{
			return ret;
		}

		// update shareLink to database, if failed, the trans task will retry
		return outlookTable_->updateShareLink(transDetailNode->root->group, 
			SD::Utility::String::utf8_to_wstring(shareLinkNode.url()));
	}

private:
	UserContext* userContext_;
	TransTableMgr* transTableMgr_;
	OutlookTable* outlookTable_;
};

#endif