#ifndef _LOCALDETECTOR_MGR_H_
#define _LOCALDETECTOR_MGR_H_

#include "UserContext.h"
#include "SyncCommon.h"

class LocalDetector
{
public:
	virtual ~LocalDetector(){}

	static std::auto_ptr<LocalDetector> create(UserContext* userContext);

	virtual int32_t fullDetect() = 0;

	virtual int32_t incDetect() = 0;

	virtual int32_t start() = 0;

	virtual int32_t stop() = 0;
};

#endif