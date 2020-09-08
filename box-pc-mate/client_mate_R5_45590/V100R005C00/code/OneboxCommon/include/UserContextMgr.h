#ifndef _ONEBOX_USERCONTEXT_MGR_H_
#define _ONEBOX_USERCONTEXT_MGR_H_

#include "UserContext.h"
#include <list>

typedef std::list<UserContext*> UserContexts;

class ONEBOX_DLL_EXPORT UserContextMgr
{
public:
	static UserContextMgr* getInstance();

	static void releaseInstance();

	UserContextMgr();

	virtual ~UserContextMgr();

	virtual UserContext* getUserContext(const UserContextId& userContextId);

	virtual UserContext* getUserContext(const int64_t userId);

	virtual UserContext* createUserContext(int64_t uiHandle);

	virtual UserContext* createUserContext(UserContext* parent, const int64_t ownerId, const UserContextType type, const std::wstring& name = L"");

	virtual int32_t removeUserContext(const UserContextId& userContextId);

	virtual UserContext* getDefaultUserContext() = 0;

private:
	static UserContextMgr* instance_;
};

#endif
