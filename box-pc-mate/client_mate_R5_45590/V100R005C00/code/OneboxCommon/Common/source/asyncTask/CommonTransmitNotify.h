#ifndef _ONEBOX_COMMON_TRANSMIT_NOTIFY_H_
#define _ONEBOX_COMMON_TRANSMIT_NOTIFY_H_

#include "Transmit.h"
#include "TransTableMgr.h"

class CommonTransmitNotify : public ITransmitNotify
{
public:
	CommonTransmitNotify(UserContext* userContext, TransTableMgr* transTableMgr);

	// if you do not care about the notify message, return 0
	virtual int32_t notify(ITransmit* transmit, const FILE_DIR_INFO& local, const FILE_DIR_INFO& remote);

	virtual void notifyProgress(ITransmit* transmit, const int64_t transedSize, const int64_t transIncrement, const int64_t sizeIncrement);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif