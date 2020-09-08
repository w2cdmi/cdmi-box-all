#include "CacheMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "CacheUserInfo.h"
#include <map>

#ifndef MODULE_NAME
#define MODULE_NAME ("CacheMgr")
#endif

typedef std::map<int64_t, CacheMetaData*> CMDMap;

class CacheMgrImpl : public CacheMgr
{
public:
	CacheMgrImpl(UserContext* userContext):userContext_(userContext), pS2MTable_(NULL), pMyShareTable_(NULL), pUserTable_(NULL), pMsgTable_(NULL)
	{
	}

	virtual ~CacheMgrImpl()
	{
		try
		{
			for (CMDMap::iterator it = cacheMetaInfos_.begin(); it != cacheMetaInfos_.end(); ++it)
			{
				CacheMetaData* pItem = it->second;
				if (NULL != pItem)
				{
					delete pItem;
					pItem = NULL;
				}
			}
			if (pS2MTable_)
			{
				delete pS2MTable_;
				pS2MTable_ = NULL;
			}
			if (pMyShareTable_)
			{
				delete pMyShareTable_;
				pMyShareTable_ = NULL;
			}
			if (pUserTable_)
			{
				delete pUserTable_;
				pUserTable_ = NULL;
			}
			if (pMsgTable_)
			{
				delete pMsgTable_;
				pMsgTable_ = NULL;
			}
		}
		catch(...){}
	}

	virtual int32_t listPage(UserContext* userContext, int64_t id, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->listPage(id, result, pageParam, count);
	}
	
	virtual int32_t flushFileInfo(UserContext* userContext, const Path& path)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushFileInfo(path);	
	}
	
	virtual int32_t getAllFileId(UserContext* userContext, const int64_t& dirId, std::set<int64_t>& result)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->getAllFileId(dirId, result);
	}

	virtual int32_t flushCache(UserContext* userContext, const Path& path, bool isFlush)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushCache(path, isFlush);
	}

	virtual int32_t saveName(UserContext* userContext, const Path& path, const std::wstring& name)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->saveName(path, name);
	}

	virtual int32_t saveParent(UserContext* userContext, const Path& path, const Path& parent)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->saveParent(path, parent);
	}

	virtual int32_t saveDelete(UserContext* userContext, const Path& path)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->saveDelete(path);
	}

	virtual int32_t saveCreate(UserContext* userContext, const FILE_DIR_INFO& info)
	{
		CacheMetaData* pCache = getCMDTable(userContext);
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->saveCreate(userContext->id.id, info);
	}

	virtual bool isMyShareCacheExist()
	{
		std::wstring path = userContext_->getConfigureMgr()->getConfigure()->userDataPath() + PATH_DELIMITER + SQLITE_CACHE_MYSHARE;
		return SD::Utility::FS::is_exist(path);
	}

	virtual bool isMyShareLinkCacheExist()
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "CacheMyShare is null", NULL);
			return false;
		}
		return pCache->isMyShareLinkCacheExist();
	}

	virtual int32_t listReceiveShare(const ShareNodeParent& parent, ShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		CacheReceiveShare* pCache = getS2MTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->listPage(parent, result, pageParam, count);
	}

	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result)
	{
		CacheReceiveShare* pCache = getS2MTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->getShareFileId(ownerId, dirId, result);	
	}

	virtual int32_t flushShareCache(const ShareNodeParent& parent, bool isFlush)
	{
		CacheReceiveShare* pCache = getS2MTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushShareCache(parent, isFlush);
	}

	virtual int32_t listMyShare(const std::string& keyword, MyShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->listMyShare(keyword, result, pageParam, count);	
	}

	virtual int32_t flushMyShareCache(bool isFlush)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushMyShareCache(isFlush);
	}

	virtual int32_t listMyLink(const std::string& keyword, MyShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->listMyLink(keyword, result, pageParam, count);	
	}

	virtual int32_t flushMyLinkCache(bool isFlush)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushMyLinkCache(isFlush);
	}

	virtual int32_t flushMyShare(const FILE_DIR_INFO& info)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->flushMyShare(info);	
	}

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& idList)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->deleteMyShareRes(idList);	
	}

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& idList)
	{
		CacheMyShare* pCache = getMyShareTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->deleteMyLinkRes(idList);	
	}

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, int32_t limit)
	{
		CacheUserInfo* pCache = getUserTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->getUser(strKey, shareUserInfos, limit);	
	}

	virtual int32_t addUser(const ShareUserInfo& userInfo)
	{
		CacheUserInfo* pCache = getUserTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->addUser(userInfo);
	}

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo)
	{
		CacheUserInfo* pCache = getUserTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->getCurUserInfo(storageUserInfo);	
	}

	virtual std::string getUserName(int64_t teamId, int64_t userId)
	{
		CacheUserInfo* pCache = getUserTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return "";
		}
		return pCache->getUserName(teamId, userId);
	}

	virtual int32_t getMsg(const PageParam& pageParam, const MsgTypeList& msgTypeList, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All)
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->getMsg(pageParam, msgTypeList, msgNodes, count, status);	
	}

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed)
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->updateMsg(msgId, status);	
	}

	virtual int32_t deleteMsg(const int64_t msgId)
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return RT_ERROR;
		}
		return pCache->deleteMsg(msgId);	
	}

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList)
	{
		CacheMsgInfo* pCache = getMsgTable();
		if(NULL==pCache) 
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "pCache is null", NULL);
			return false;
		}
		return pCache->hasUnRead(msgTypeList);	
	}

private:
	virtual CacheMetaData* getCMDTable(UserContext* userContext)
	{
		CMDMap::iterator it = cacheMetaInfos_.find(userContext->id.id);
		if(it!=cacheMetaInfos_.end())
		{
			return it->second;
		}

		std::wstring parentPath = userContext->getConfigureMgr()->getConfigure()->userDataPath() + PATH_DELIMITER;
		bool isMyFile = (userContext->id.id==userContext_->id.id);
		CacheMetaData* pCacheMetaData = CacheMetaData::create(userContext_, parentPath, isMyFile);
		cacheMetaInfos_.insert(std::make_pair(userContext->id.id, pCacheMetaData));
		return pCacheMetaData;
	}

	virtual CacheReceiveShare* getS2MTable()
	{
		if(NULL == pS2MTable_)
		{
			std::wstring parentPath = userContext_->getConfigureMgr()->getConfigure()->userDataPath();
			pS2MTable_ = CacheReceiveShare::create(userContext_, parentPath);
		}
		return pS2MTable_;
	}

	virtual CacheMyShare* getMyShareTable()
	{
		if(NULL == pMyShareTable_)
		{
			std::wstring parentPath = userContext_->getConfigureMgr()->getConfigure()->userDataPath();
			pMyShareTable_ = CacheMyShare::create(userContext_, parentPath);
		}
		return pMyShareTable_;
	}

	virtual CacheUserInfo* getUserTable()
	{
		if(NULL == pUserTable_)
		{
			std::wstring parentPath = userContext_->getConfigureMgr()->getConfigure()->userDataPath();
			pUserTable_ = CacheUserInfo::create(userContext_, parentPath);
		}
		return pUserTable_;
	}

	virtual CacheMsgInfo* getMsgTable()
	{
		if(NULL == pMsgTable_)
		{
			std::wstring parentPath = userContext_->getConfigureMgr()->getConfigure()->userDataPath();
			pMsgTable_ = CacheMsgInfo::create(userContext_, parentPath);
		}
		return pMsgTable_;
	}

private:
	UserContext* userContext_;
	CMDMap cacheMetaInfos_;
	CacheReceiveShare* pS2MTable_;
	CacheMyShare* pMyShareTable_;
	CacheUserInfo* pUserTable_;
	CacheMsgInfo* pMsgTable_;
};

std::auto_ptr<CacheMgr> CacheMgr::instance_(NULL);

CacheMgr* CacheMgr::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new CacheMgrImpl(userContext));
	}
	return instance_.get();
}