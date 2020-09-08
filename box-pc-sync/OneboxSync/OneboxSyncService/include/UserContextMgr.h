#ifndef _ONEBOX_USERCONTEXT_MGR_H_
#define _ONEBOX_USERCONTEXT_MGR_H_

#include "CommonDefine.h"
#include "UserContext.h"

class UserContextMgr
{
public:
	static UserContextMgr *getInstance();

	UserContext *getUserContext(const UserContextId& userContextId);

private:
	// forbidden constructor
	UserContextMgr();
	UserContextMgr(const UserContextMgr&);

	static UserContextMgr *instance_;

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
