#ifndef _REMOTEDETECTOR_ALL_MGR_H_
#define _REMOTEDETECTOR_ALL_MGR_H_

#include "UserContext.h"
#include "SyncCommon.h"

class RemoteDetector
{
public:
	virtual ~RemoteDetector(){}

	static std::auto_ptr<RemoteDetector> create(UserContext* userContext);

	virtual int32_t fullDetect() = 0;

	virtual int32_t incDetect() = 0;

	//get children
	virtual int32_t dirDetecte(const int64_t& id) = 0;
};

#endif