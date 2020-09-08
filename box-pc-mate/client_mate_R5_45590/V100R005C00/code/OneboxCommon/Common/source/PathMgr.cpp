#include "PathMgr.h"
#include "UserInfoMgr.h"

class PathMgrImpl : public PathMgr
{
public:
	PathMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{

	}

	virtual Path makePath()
	{
		Path tempPath;
		tempPath.id(INVALID_ID);
		tempPath.parent(INVALID_ID);
		tempPath.name(L"");
		tempPath.path(L"");
		tempPath.type(FILE_TYPE_DIR);
		tempPath.ownerId(userContext_->id.id);
		return tempPath;
	}

private:
	UserContext* userContext_;
};

PathMgr* PathMgr::create(UserContext* userContext)
{
	return static_cast<PathMgr*>(new PathMgrImpl(userContext));
}
