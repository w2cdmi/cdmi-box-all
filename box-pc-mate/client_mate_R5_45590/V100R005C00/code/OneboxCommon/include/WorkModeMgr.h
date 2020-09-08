#ifndef _ONEBOX_WORKMODE_MGR_H_
#define _ONEBOX_WORKMODE_MGR_H_

#include "UserContext.h"

enum WorkMode
{
	WorkMode_Online, 
	WorkMode_Offline, 
	WorkMode_Error, 
	WorkMode_Pause, 
	WorkMode_Uninitial
};

class IWorkModeChangeNotify
{
public:
	virtual int32_t changeWorkMode(const WorkMode& mode) { return RT_OK; };
};

class ONEBOX_DLL_EXPORT WorkModeMgr
{
public:
	virtual ~WorkModeMgr(){}

	static WorkModeMgr* create(UserContext* userContext);

	virtual int32_t changeWorkMode(const WorkMode& mode) = 0;

	virtual WorkMode getWorkMode() = 0;

	virtual void addWorkModeChangeNotify(IWorkModeChangeNotify* notify) = 0;
};

#endif
