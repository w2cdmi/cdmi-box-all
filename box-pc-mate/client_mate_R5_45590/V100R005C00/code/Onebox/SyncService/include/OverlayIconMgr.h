#ifndef _ONEBOX_OVERLAYICON_MGR_H_
#define _ONEBOX_OVERLAYICON_MGR_H_

#include "UserContext.h"

enum OverlayIconStatus
{
	OS_None,
	OS_Synced,
	OS_Syncing,
	OS_NoActionDelete,
	OS_Invalid
};

class OverlayIconMgr
{
public:
	virtual ~OverlayIconMgr(){}

	static std::auto_ptr<OverlayIconMgr> create(UserContext* userContext);

	virtual int32_t refreshOverlayIcon(const std::wstring& path) = 0;

	virtual OverlayIconStatus getOverlayIconStatus(const std::wstring& path) = 0;

	virtual int32_t notifyOverlayIcons() = 0;
};

#endif