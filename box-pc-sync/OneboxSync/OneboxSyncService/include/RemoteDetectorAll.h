#ifndef _REMOTEDETECTOR_ALL_MGR_H_
#define _REMOTEDETECTOR_ALL_MGR_H_

#include "RemoteDetector.h"

class RemoteDetectorAll : public RemoteDetector
{
public:
	virtual ~RemoteDetectorAll(){}

	static std::auto_ptr<RemoteDetector> create(UserContext* userContext);

	virtual int32_t fullDetect() = 0;

	virtual int32_t incDetect() = 0;

	//get children
	virtual int32_t dirDetecte(const int64_t& id) = 0;
};

#endif