#ifndef _ONEBOX_PATH_MGR_H_
#define _ONEBOX_PATH_MGR_H_

#include "UserContext.h"
#include "Path.h"

class ONEBOX_DLL_EXPORT PathMgr
{
public:
	virtual ~PathMgr(){}

	static PathMgr* create(UserContext* userContext);

	virtual Path makePath() = 0;
};

#endif