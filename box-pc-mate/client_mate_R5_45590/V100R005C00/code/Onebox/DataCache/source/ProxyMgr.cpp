#include "ProxyMgr.h"
#include "CacheMgr.h"
#include "UserContextMgr.h"
#include "ConfigureMgr.h"
#include "PathMgr.h"
#include "Utility.h"
#include "UserInfoMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ProxyMgr")
#endif

#ifndef MAX_USER_COUNT
#define MAX_USER_COUNT (20)
#endif

class ProxyMgrImpl : public ProxyMgr
{
public:
	ProxyMgrImpl(UserContext* userContext):userContext_(userContext)
	{
		std::wstring logFile = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"DataCache.log";
		ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"),
			TP_FILE, 
			SD::Utility::String::wstring_to_string(logFile));
	}

	~ProxyMgrImpl()
	{
		try
		{
			ISSP_LogExit();
		}
		catch(...)
		{
		}
	}
	virtual int32_t create(UserContext* userContext, const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type)
	{
		int32_t ret = RT_OK;

		ret = userContext->getSyncFileSystemMgr()->create(parent, name, info, type);
		if(RT_OK != ret)
		{
			return ret;
		}

		CacheMgr::getInstance(userContext_)->saveCreate(userContext, info);
		return ret;
	}

	virtual int32_t remove(UserContext* userContext, const Path& path, ADAPTER_FILE_TYPE type)
	{
		int32_t ret = RT_OK;

		ret = userContext->getSyncFileSystemMgr()->remove(path, type);
		if(RT_OK != ret)
		{
			return ret;
		}

		CacheMgr::getInstance(userContext_)->saveDelete(userContext, path);
		return ret;
	}

	virtual int32_t move(UserContext* userContext, const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type)
	{
		int32_t ret = RT_OK;

		ret = userContext->getSyncFileSystemMgr()->move(path, parent, autoRename, type);
		if(RT_OK != ret)
		{
			return ret;
		}

		CacheMgr::getInstance(userContext_)->saveParent(userContext, path, parent);
		return ret;
	}

	virtual int32_t copy(UserContext* userContext, const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type)
	{
		int32_t ret = RT_OK;

		ret = userContext->getSyncFileSystemMgr()->copy(path, parent, autoRename, type);

		return ret;
	}

	virtual int32_t rename(UserContext* userContext, const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type)
	{
		int32_t ret = RT_OK;

		ret = userContext->getSyncFileSystemMgr()->rename(path, name, type);
		if(RT_OK != ret)
		{
			return ret;
		}

		CacheMgr::getInstance(userContext_)->saveName(userContext, path, name);
		return ret;
	}

	virtual int32_t listPage(UserContext* userContext, const Path& path, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count, bool isFlush = false)
	{
		int32_t ret = RT_OK;
		if(0!=pageParam.offset)
		{
			return CacheMgr::getInstance(userContext_)->listPage(userContext, path.id(), result, pageParam, count);
		}

		if(isFlush)
		{
			CacheMgr::getInstance(userContext_)->flushCache(userContext, path, true);
			ret = CacheMgr::getInstance(userContext_)->listPage(userContext, path.id(), result, pageParam, count);
		}
		else
		{
			ret = CacheMgr::getInstance(userContext_)->listPage(userContext, path.id(), result, pageParam, count);
			if(-1==count)
			{
				//如果无缓存则同步查询SDK接口后，重新通过缓存获取数据
				CacheMgr::getInstance(userContext_)->flushCache(userContext, path, true);
				ret = CacheMgr::getInstance(userContext_)->listPage(userContext, path.id(), result, pageParam, count);
				return ret;
			}
			CacheMgr::getInstance(userContext_)->flushCache(userContext, path, false);
		}

		return ret;	
	}

	virtual int32_t listAll(UserContext* userContext, const Path& path, LIST_FOLDER_RESULT& result)
	{
		PageParam pageParam;
		int64_t count = 0;
		pageParam.offset = 0;
		pageParam.limit = 0;
		return CacheMgr::getInstance(userContext_)->listPage(userContext, path.id(), result, pageParam, count);
	}

	virtual int32_t flushFileInfo(UserContext* userContext, const Path& path)
	{
		return CacheMgr::getInstance(userContext_)->flushFileInfo(userContext, path);
	}

	virtual int32_t getAllFileId(UserContext* userContext, const int64_t& dirId, std::set<int64_t>& result)
	{
		return CacheMgr::getInstance(userContext_)->getAllFileId(userContext, dirId, result);
	}

	virtual bool checkDirIsExist(std::list<std::wstring>& sourceList, UserContext* userContext, int64_t dirId)
	{
		Path listPath = userContext->getPathMgr()->makePath();
		listPath.id(dirId);
		LIST_FOLDER_RESULT lfResult;
		listAll(userContext, listPath, lfResult);
		for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
		{
			for (std::list<std::wstring>::iterator itor = sourceList.begin();itor != sourceList.end();itor++)
			{
				if (SD::Utility::FS::get_file_name(*itor) != it->name) continue;
				if(it->type == FILE_TYPE_DIR && SD::Utility::FS::is_directory(*itor))
				{
					return true;
				}
			}
		}
		return false;
	}

	virtual int checkExistFileNumber(std::list<std::wstring>& sourceList, UserContext* userContext, int64_t dirId)
	{
		int nCount = 0;
		Path listPath = userContext->getPathMgr()->makePath();
		listPath.id(dirId);
		LIST_FOLDER_RESULT lfResult;
		listAll(userContext, listPath, lfResult);
		for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
		{
			for (std::list<std::wstring>::iterator itor = sourceList.begin();itor != sourceList.end();itor++)
			{
				if (SD::Utility::FS::get_file_name(*itor) == it->name)
				{
					nCount++;
				}
			}
		}
		return nCount;
	}

	virtual bool isMyShareCacheExist()
	{
		return CacheMgr::getInstance(userContext_)->isMyShareCacheExist();
	}

	virtual bool isMyShareLinkCacheExist()
	{
		return CacheMgr::getInstance(userContext_)->isMyShareLinkCacheExist();
	}

	virtual int32_t listReceiveShareRes(UserContext* userContext, const ShareNodeParent& parent, ShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush)
	{
		int32_t ret = RT_OK;
		if(shareNodes.empty())
		{
			if(!isFlush)
			{
				ret = CacheMgr::getInstance(userContext_)->listReceiveShare(parent, shareNodes, pageParam, count);
				if(-1==count)
				{
					CacheMgr::getInstance(userContext_)->flushShareCache(parent, true);
					ret = CacheMgr::getInstance(userContext_)->listReceiveShare(parent, shareNodes, pageParam, count);
					return ret;
				}
				CacheMgr::getInstance(userContext_)->flushShareCache(parent, false);
			}
			else
			{
				CacheMgr::getInstance(userContext_)->flushShareCache(parent, true);
				ret = CacheMgr::getInstance(userContext_)->listReceiveShare(parent, shareNodes, pageParam, count);
			}
		}
		else
		{
			ret = CacheMgr::getInstance(userContext_)->listReceiveShare(parent, shareNodes, pageParam, count);
		}

		return ret;
	}

	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result)
	{
		return CacheMgr::getInstance(userContext_)->getShareFileId(ownerId, dirId, result);
	}

	virtual int32_t exitShare(int64_t& ownerId, int64_t& id)
	{
		int32_t ret = RT_OK;

		ret = userContext_->getShareResMgr()->exitShare(ownerId, id);

		return ret;
	}

	virtual int32_t listMyShareRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush = false)
	{
		int32_t ret = RT_OK;

		if(0==pageParam.offset)
		{
			CacheMgr::getInstance(userContext_)->flushMyShareCache(isFlush);
		}

		ret = CacheMgr::getInstance(userContext_)->listMyShare(keyword, shareNodes, pageParam, count);

		return ret;
	}

	virtual int32_t listMyLinkRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count, bool isFlush = false)
	{
		int32_t ret = RT_OK;

		if(0==pageParam.offset)
		{
			CacheMgr::getInstance(userContext_)->flushMyLinkCache(isFlush);
		}

		ret = CacheMgr::getInstance(userContext_)->listMyLink(keyword, shareNodes, pageParam, count);

		return ret;
	}

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& selectedItems)
	{
		if(!selectedItems.empty())
		{
			CacheMgr::getInstance(userContext_)->deleteMyShareRes(selectedItems);
		}

		return RT_OK;
	}

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& selectedItems)
	{
		if(!selectedItems.empty())
		{
			CacheMgr::getInstance(userContext_)->deleteMyLinkRes(selectedItems);
		}

		return RT_OK;
	}

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, bool fromCache = false)
	{
		int32_t ret = RT_OK;

		if(fromCache)
		{
			ret = CacheMgr::getInstance(userContext_)->listDomainUsers(strKey, shareUserInfos, MAX_USER_COUNT);
		}
		else
		{
			ret = userContext_->getShareResMgr()->listDomainUsers(strKey, shareUserInfos, MAX_USER_COUNT);
		}

		return ret;
	}

	virtual int32_t addUser(const ShareUserInfo& userInfo)
	{
		int32_t ret = RT_OK;

		ret = CacheMgr::getInstance(userContext_)->addUser(userInfo);

		return ret;
	}

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo)
	{
		int32_t ret = RT_OK;

		if(RT_OK != CacheMgr::getInstance(userContext_)->getCurUserInfo(storageUserInfo))
		{
			ret = userContext_->getUserInfoMgr()->getCurUserInfo(storageUserInfo);
			if(RT_OK == ret)
			{
				ShareUserInfo userInfo;
				userInfo.department(storageUserInfo.description);
				userInfo.email(storageUserInfo.email);
				userInfo.id(storageUserInfo.user_id);
				userInfo.loginName(storageUserInfo.login_name);
				userInfo.name(storageUserInfo.name);
				CacheMgr::getInstance(userContext_)->addUser(userInfo);
			}
		}

		return ret;	
	}

	virtual int32_t getMsg(const PageParam& pageParam, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All)
	{
		int32_t ret = RT_OK;

		MsgTypeList msgTypeList;
		if(0 == pageParam.offset)
		{
			PageParam tempParam = pageParam;
			tempParam.limit = 3;
			msgTypeList.push_back(MT_System);
			OrderParam orderParam;
			orderParam.field = MSG_ROW_CREATEDAT;
			orderParam.direction = "desc";
			tempParam.orderList.push_back(orderParam);
			ret = CacheMgr::getInstance(userContext_)->getMsg(tempParam, msgTypeList, msgNodes, count, status);
			
			msgTypeList.clear();
			for(int i=1; i<MT_System; ++i)
			{
				msgTypeList.push_back(MsgType(i));
			}
			tempParam.limit = pageParam.limit - 3;
			tempParam.orderList = pageParam.orderList;
			ret = CacheMgr::getInstance(userContext_)->getMsg(tempParam, msgTypeList, msgNodes, count, status);
		}
		else
		{
			msgTypeList.clear();
			for(int i=1; i<MT_System; ++i)
			{
				msgTypeList.push_back(MsgType(i));
			}
			ret = CacheMgr::getInstance(userContext_)->getMsg(pageParam, msgTypeList, msgNodes, count, status);
		}

		return ret;	
	}

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed)
	{
		int32_t ret = userContext_->getMsgMgr()->updateMsg(msgId, status);
		if(RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "updateMsg failed", NULL);
			return RT_ERROR;
		}

		ret = CacheMgr::getInstance(userContext_)->updateMsg(msgId, status);

		return ret;	
	}

	virtual int32_t deleteMsg(const int64_t msgId)
	{
		int ret = userContext_->getMsgMgr()->deleteMsg(msgId);
		if(RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "deleteMsg failed", NULL);
			return RT_ERROR;
		}

		ret = CacheMgr::getInstance(userContext_)->deleteMsg(msgId);

		return ret;	
	}

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList)
	{
		return CacheMgr::getInstance(userContext_)->hasUnRead(msgTypeList);	
	}

private:
	UserContext* userContext_;
};

std::auto_ptr<ProxyMgr> ProxyMgr::instance_(NULL);

ProxyMgr* ProxyMgr::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new ProxyMgrImpl(userContext));
	}
	return instance_.get();
}


