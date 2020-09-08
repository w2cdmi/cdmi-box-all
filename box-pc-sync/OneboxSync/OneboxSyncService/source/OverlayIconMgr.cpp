#include "OverlayIconMgr.h"
#include "ConfigureMgr.h"
#include "SyncFileSystemMgr.h"
#include "DataBaseMgr.h"
#include "Utility.h"
#include <Shlobj.h>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("OverlayIconMgr")
#endif

#define OVERLAY_ICON_EXPIRE_TIME (5*1000) // 5s
#define MAX_OVERLAY_ICON_PATH_SKIP (1000)

struct st_OverlayIconInfo
{
	std::wstring path;
	OverlayIconStatus status;
	uint32_t interval;

	st_OverlayIconInfo()
		:path(L"")
		,status(OS_Invalid)
		,interval(0){}
};
typedef std::shared_ptr<st_OverlayIconInfo> OverlayIconInfo;
typedef std::list<OverlayIconInfo> OverlayIconInfos;

class OverlayIconMgrImpl : public OverlayIconMgr
{
public:
	OverlayIconMgrImpl(UserContext* userContext);

	virtual int32_t refreshOverlayIcon(const std::wstring& path);

	virtual OverlayIconStatus getOverlayIconStatus(const std::wstring& path);

	virtual int32_t notifyOverlayIcons();

private:
	OverlayIconStatus getOverlayIconStatusFromCache(const std::wstring& path);

	int32_t addOverlayIconInfoToCache(const std::wstring& path, OverlayIconStatus status);

	OverlayIconStatus getStatusFromLocalStatus(LocalStatus localStatus);

private:
	UserContext* userContext_;
	boost::mutex mutex_;
	boost::mutex infoMutex_;
	std::set<std::wstring> overlayIconPathCache_;
	OverlayIconInfos overlayIconInfoCache_;
	bool notifyAll_; 
};

OverlayIconMgr* OverlayIconMgr::create(UserContext* userContext)
{
	return static_cast<OverlayIconMgr*>(new OverlayIconMgrImpl(userContext));
}

OverlayIconMgrImpl::OverlayIconMgrImpl(UserContext* userContext)
	:userContext_(userContext)
	,notifyAll_(true)
{

}

int32_t OverlayIconMgrImpl::refreshOverlayIcon(const std::wstring& path)
{
	if (path.length() <= userContext_->getConfigureMgr()->getConfigure()->monitorRootPath().length()) 
	{
		return RT_OK;
	}
	{
		boost::mutex::scoped_lock lock(mutex_);
		overlayIconPathCache_.insert(path);
	}

	return refreshOverlayIcon(Utility::FS::get_parent_name(path));
}

OverlayIconStatus OverlayIconMgrImpl::getOverlayIconStatus(const std::wstring& path)
{
	if (path.empty())
	{
		return OS_Invalid;
	}

	OverlayIconStatus status = getOverlayIconStatusFromCache(path);
	if (OS_Invalid != status)
	{
		return status;
	}

	// no data in cache or the cache data is expired, get the icon status from database
	// if the node is in diff, set status to OS_Syncing 
	if(userContext_->getDataBaseMgr()->getDiffTable()->isInDiff(path))
	{
		status = OS_Syncing;
		addOverlayIconInfoToCache(path, status);
		return status;
	}

	FILE_DIR_INFO fileDirInfo;
	Path localPath;
	localPath.path(path);
	int32_t ret = userContext_->getSyncFileSystemMgr()->getProperty(localPath, fileDirInfo, ADAPTER_FILE_TYPE_LOCAL);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get local id of %s failed.", 
			Utility::String::wstring_to_string(localPath.path()).c_str());
		return status;
	}

	LocalNode localNode(new st_LocalNode);
	ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(fileDirInfo.id, localNode);
	if (RT_OK != ret)
	{
		return status;
	}

	if(LS_Normal==localNode->status)
	{
		// the node is in diff, set status to OS_Syncing
		if(userContext_->getDataBaseMgr()->getDiffTable()->isInDiff(Key_LocalID, fileDirInfo.id))
		{
			DiffPathNodes diffPathNodes;	
			DiffPathNode diffPathNode(new st_DiffPathNode);
			diffPathNode->key = fileDirInfo.id;
			diffPathNode->keyType = Key_LocalID;
			diffPathNode->localPath = localPath.path();
			diffPathNodes.push_back(diffPathNode);
			userContext_->getDataBaseMgr()->getDiffTable()->replaceIncPath(diffPathNodes);

			status = OS_Syncing;
			addOverlayIconInfoToCache(localPath.path(), status);
			return status;
		}

		int64_t remoteId = INVALID_ID;
		ret = userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(fileDirInfo.id, remoteId);
		if(RT_OK == ret)
		{
			status = OS_Synced;
			addOverlayIconInfoToCache(localPath.path(), status);
			return status;
		}
		else if(RT_SQLITE_NOEXIST == ret)
		{
			status = OS_Syncing;
			addOverlayIconInfoToCache(localPath.path(), status);
			return status;
		}
	}
	else
	{
		status = getStatusFromLocalStatus(localNode->status);
		addOverlayIconInfoToCache(localPath.path(), status);
		return status;
	}

	return status;
}

int32_t OverlayIconMgrImpl::notifyOverlayIcons()
{
	std::set<std::wstring> overlayIconPaths;
	{
		boost::mutex::scoped_lock lock(mutex_);
		overlayIconPaths.swap(overlayIconPathCache_);
	}

	if(overlayIconPaths.empty())
	{
		return RT_OK;
	}

	if(overlayIconInfoCache_.empty()&&notifyAll_)
	{
		SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, NULL, NULL);
		notifyAll_ = false;
		HSLOG_TRACE(MODULE_NAME, RT_OK, "notify all icon");
		return RT_OK;
	}

	if(overlayIconPaths.size() > MAX_OVERLAY_ICON_PATH_SKIP)
	{
		HSLOG_TRACE(MODULE_NAME, RT_OK, "too many notifys, skip notifyIcon.");
		return RT_OK;
	}

	for(std::set<std::wstring>::iterator it = overlayIconPaths.begin();
		it != overlayIconPaths.end(); ++it)
	{
		std::wstring localPath = *it;
		{
			boost::mutex::scoped_lock lock(infoMutex_);
			for(OverlayIconInfos::iterator itI = overlayIconInfoCache_.begin();
				itI != overlayIconInfoCache_.end(); ++itI)
			{
				if(localPath==itI->get()->path)
				{
					overlayIconInfoCache_.erase(itI);
					break;
				}
			}
		}
		if(Utility::FS::is_directory(localPath))
		{
			localPath += PATH_DELIMITER;
		}
		SHChangeNotify(SHCNE_UPDATEITEM, SHCNF_PATHW, localPath.c_str(), NULL);
	}

	return RT_OK;
}

OverlayIconStatus OverlayIconMgrImpl::getOverlayIconStatusFromCache(const std::wstring& path)
{
	boost::mutex::scoped_lock lock(infoMutex_);
	for(OverlayIconInfos::iterator it = overlayIconInfoCache_.begin();
		it != overlayIconInfoCache_.end(); ++it)
	{
		if(path==it->get()->path)
		{
			// the cache status is expired after 5 seconds
			if((GetTickCount()-it->get()->interval) > OVERLAY_ICON_EXPIRE_TIME)
			{
				overlayIconInfoCache_.erase(it);
				return OS_Invalid;
			}
			else
			{
				return it->get()->status;
			}
		}
	}
	return OS_Invalid;
}

int32_t OverlayIconMgrImpl::addOverlayIconInfoToCache(const std::wstring& path, OverlayIconStatus status)
{
	boost::mutex::scoped_lock lock(infoMutex_);
	for(OverlayIconInfos::iterator it = overlayIconInfoCache_.begin();
		it != overlayIconInfoCache_.end(); ++it)
	{
		if(path==it->get()->path)
		{
			it->get()->status = status;
			it->get()->interval = GetTickCount();

			return RT_OK;
		}
	}

	OverlayIconInfo OverlayIconInfo(new st_OverlayIconInfo);
	OverlayIconInfo->path = path;
	OverlayIconInfo->status = status;
	OverlayIconInfo->interval = GetTickCount();
	overlayIconInfoCache_.push_back(OverlayIconInfo);

	return RT_OK;
}

OverlayIconStatus OverlayIconMgrImpl::getStatusFromLocalStatus(LocalStatus localStatus)
{
	OverlayIconStatus overlayIconStatus = OS_Invalid;
	switch (localStatus)
	{
	case LS_Normal:
		overlayIconStatus = OS_Synced;
		break;
	case LS_Delete_Status:
		overlayIconStatus = OS_NoActionDelete;
		break;
	case LS_NoActionDelete_Status:
		overlayIconStatus = OS_NoActionDelete;
		break;
	case LS_ShowNormal:
		overlayIconStatus = OS_Synced;
		break;
	case LS_Filter:
		overlayIconStatus = OS_None;
		break;
	default:
		break;
	}
	return overlayIconStatus;
}
