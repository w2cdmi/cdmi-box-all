#ifndef _SYNC_TIME_CALC_H_
#define _SYNC_TIME_CALC_H_

#include "UserContext.h"
#include "SyncCommon.h"

class SyncTimeCalc
{
public:
	virtual ~SyncTimeCalc(void);

	virtual int64_t startCntTimer(void) = 0;

	virtual int64_t stopCntTimer(void) = 0;

	virtual int32_t getDiffCntSpeed(void) = 0;

	virtual void startUploadTimer(void) = 0;

	virtual void stopUploadTimer(int64_t& size) = 0;

	virtual int32_t getUploadSpeed() = 0;

	virtual void startDownloadTimer(void) = 0;

	virtual void stopDownloadTimer(int64_t& size) = 0;

	virtual int32_t getDownloadSpeed() = 0;

	static SyncTimeCalc* create(UserContext* userContext);
};

#endif