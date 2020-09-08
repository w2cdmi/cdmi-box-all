#include "RemoteDetectorLocal.h"
#include "DataBaseMgr.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"
#include "InIHelper.h"
#include "NotifyMgr.h"
#include "SyncUtility.h"
#include "FilterMgr.h"
#include "AsyncTaskMgr.h"
#include <boost/thread.hpp>
#include "GlobalVariable.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RemoteDetectorLocal")
#endif

class NotifyObject
{
public:
	explicit NotifyObject(UserContext* userContext)
		:userContext_(userContext)
	{
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"begin"));
	}

	~NotifyObject() 
	{
		try
		{
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"end"));
		}
		catch(...)
		{
		}
	}
private:
	UserContext* userContext_;
}; 

class RemoteDetectorLocalImpl : public RemoteDetectorLocal
{
public:
	RemoteDetectorLocalImpl(UserContext* userContext)
		:userContext_(userContext)
		,rootId_(INVALID_ID)
		,fullDetectVersion_(INVALID_VALUE)
		,lastFullNoDiff_(false)
	{
	}

	virtual ~RemoteDetectorLocalImpl(void)
	{
	}

	virtual int32_t incDetect()
	{
		boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());

		//read syncVersion
		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		int64_t curVersion = iniHelper.GetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, 0);

		RemoteNodes syncData;
		int32_t ret = getChangeMetadata(curVersion, syncData);
		if (RT_OK != ret)
		{
			return ret;
		}
		if(syncData.empty())
		{
			return RT_OK;
		}

		int64_t maxVersion = userContext_->getDataBaseMgr()->getSyncDataTable()->getMaxVersion();
		if(maxVersion==0)
		{
			return RT_OK;
		}

		//check syncVersion
		if(maxVersion<curVersion)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "getAllMetadata failed, curVersion:%I64d, maxVersion:%I64d", curVersion, maxVersion);
			return RT_ERROR;
		}

		getRemoteDiff(syncData);

		//update syncVersion
		iniHelper.SetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, maxVersion);
		return RT_OK;
	}

	virtual int32_t fullDetect()
	{
		NotifyObject notifyObject(userContext_);

		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::fullDetect");

		if(fullDetectVersion_>0)
		{
			CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
			int64_t curVersion = iniHelper.GetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, 0);
			if((fullDetectVersion_==curVersion)&&lastFullNoDiff_)
			{
				return RT_OK;
			}
			if(userContext_->getDataBaseMgr()->getDiffTable()->getNormalDiffCount()>0)
			{
				return RT_OK;
			}
		}

		boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());

		bool isAllCreate = false;
		//update remote tree exist flag 
		if(RT_CANCEL==userContext_->getDataBaseMgr()->getRemoteTable()->initMarkStatus())
		{
			isAllCreate = true;
		}

		RemoteNodes syncData;
		int32_t ret = RT_ERROR;
		RETRY(5)
		{
			ret = getDirMetadata(ROOT_PARENTID, syncData);
			if (RT_OK == ret)
			{
				break;
			}
			boost::this_thread::sleep(boost::posix_time::milliseconds(5000));
		}
		if (RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "get all metadata failed.");
			return ret;
		}

		if(INVALID_ID==rootId_)
		{
			userContext_->getDataBaseMgr()->getLocalTable()->getRoot(rootId_);
		}

		if(isAllCreate)
		{
			getAllCreateDiff(syncData);
		}
		else
		{
			getFullRemoteDiff(syncData);
		}

		return RT_OK;
	}

	virtual int32_t dirDetecte(const int64_t& id)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::dirDetecte");

		if(ROOT_PARENTID==id)
		{
			return RT_OK;
		}
		RemoteNodes syncData;
		int32_t ret = getDirMetadata(id, syncData);
		if (RT_OK != ret)
		{
			return ret;
		}
		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(syncData);

		return ret;
	}
private:
	void getAllCreateDiff(RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::getAllCreateDiff");
		DiffPathNodes diffInfos;
		RemoteNodes newRemoteNodes;

		getCreateDiff(ROOT_PARENTID, L"", syncData, newRemoteNodes, diffInfos);

		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		userContext_->getDataBaseMgr()->getDiffTable()->insertIncPath(diffInfos);
		iniHelper.SetValue<int64_t>(L"", CONF_AUTOINC_ID, userContext_->getDataBaseMgr()->getDiffTable()->getMaxInc());

		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(newRemoteNodes);

		//update syncVersion
		int64_t maxVersion = userContext_->getDataBaseMgr()->getSyncDataTable()->getMaxVersion();
		iniHelper.SetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, maxVersion);

		if(newRemoteNodes.empty())
		{
			lastFullNoDiff_ = true;
		}
		else
		{
			lastFullNoDiff_ = false;
		}
		fullDetectVersion_ = maxVersion;
	}

	void getFullRemoteDiff(RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::getFullRemoteDiff");

		RemoteNodes newRemoteNodes;
		//get diff starting from the root
		IdList existList;
		existList.push_back(0);
		getRemoteDiff(ROOT_PARENTID, syncData, newRemoteNodes, existList);
		getRemoteDelete(existList);
		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(newRemoteNodes);

		//update syncVersion
		int64_t maxVersion = userContext_->getDataBaseMgr()->getSyncDataTable()->getMaxVersion();
		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		iniHelper.SetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, maxVersion);

		if(newRemoteNodes.empty())
		{
			lastFullNoDiff_ = true;
		}
		else
		{
			lastFullNoDiff_ = false;
		}

		fullDetectVersion_ = maxVersion;
	}

	int32_t getRemoteDiff(const int64_t& parentId, 
		RemoteNodes& syncData, 
		RemoteNodes& newRemoteList,
		IdList& existList)
	{
		RemoteNodes dirSyncData;
		getSyncDataByParent(parentId, syncData, dirSyncData);

		if((ROOT_PARENTID==parentId)&&(INVALID_ID!=rootId_))
		{
			for(RemoteNodes::iterator it = dirSyncData.begin();
				it != dirSyncData.end(); ++it)
			{
				RemoteNode syncNode = *(it);
				LocalNode localNode(new st_LocalNode);
				//no sync -> sync, these is no relation, check by parent + name.
				if(RT_OK == userContext_->getDataBaseMgr()->getLocalTable()->getNode(rootId_, syncNode->name, localNode))
				{
					if(LS_NoActionDelete_Status==(localNode->status&LS_NoActionDelete_Status))
					{
						//set sync
						if(SYNC_STATUS == syncNode->status)
						{
							restoreNoActionDelete(localNode->id);
						}
					}
					else if(NO_SET_SYNC_STATUS == syncNode->status)
					{
						//set no sync
						if(userContext_->getDataBaseMgr()->getRelationTable()->isSync(localNode->id, syncNode->id))
						{
							setNoActionDelete(localNode->id, syncNode->id);
						}
					}
				}
			}
		}

		for(RemoteNodes::iterator it = dirSyncData.begin();
			it != dirSyncData.end(); ++it)
		{
			RemoteNode syncNode = *(it);
			existList.push_back(syncNode->id);

			RemoteNode remoteNode(new st_RemoteNode);
			remoteNode->id = syncNode->id;
			if(RT_OK==userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
			{
				if((remoteNode->parent!=syncNode->parent)
					||(remoteNode->name!=syncNode->name)
					||(remoteNode->version!=syncNode->version)
					||((ROOT_PARENTID==parentId)&&(remoteNode->status!=syncNode->status)))
				{
					newRemoteList.push_back(syncNode);
				}
			}
			else
			{
				newRemoteList.push_back(syncNode);
			}

			if(FILE_TYPE_DIR==syncNode->type)
			{
				if((ROOT_PARENTID!=parentId)||(SYNC_STATUS == syncNode->status))
				{
					getRemoteDiff(syncNode->id, syncData, newRemoteList, existList);
				}
			}
		}

		return RT_OK;
	}

	int32_t getCreateDiff(const int64_t& parentId,
		const std::wstring& parentPath,
		RemoteNodes& syncData, 
		RemoteNodes& newRemoteList,
		DiffPathNodes& diffInfos)
	{
		RemoteNodes dirSyncData;
		getSyncDataByParent(parentId, syncData, dirSyncData);

		for(RemoteNodes::iterator it = dirSyncData.begin();
			it != dirSyncData.end(); ++it)
		{
			boost::this_thread::interruption_point();
			RemoteNode syncNode = *(it);
			newRemoteList.push_back(syncNode);

			//no sync status
			if((ROOT_PARENTID==syncNode->parent)&&(NO_SET_SYNC_STATUS ==syncNode->status))
			{
				continue;
			}

			DiffPathNode diffPathNode(new st_DiffPathNode);
			diffPathNode->key = syncNode->id;
			diffPathNode->keyType = Key_RemoteID;
			if(FILE_TYPE_FILE == syncNode->type)
			{
				diffPathNode->size = syncNode->size;
			}

			diffPathNode->remotePath = parentPath + PATH_DELIMITER + syncNode->name;
			diffPathNode->localPath = userContext_->getConfigureMgr()->getConfigure()->monitorRootPath() + diffPathNode->remotePath;
			diffInfos.push_back(diffPathNode);

			if(FILE_TYPE_DIR==syncNode->type)
			{
				getCreateDiff(syncNode->id, diffPathNode->remotePath, syncData, newRemoteList, diffInfos);
			}
		}
		return RT_OK;
	}

	int32_t getSyncDataByParent(const int64_t& parentId, RemoteNodes& syncData, RemoteNodes& dirSyncData)
	{
		for(RemoteNodes::iterator it = syncData.begin();
			it != syncData.end();)
		{
			if(it->get()->parent == parentId)
			{
				dirSyncData.push_back(*it);
				it = syncData.erase(it);
			}
			else
			{
				++it;
			}
		}
		return RT_OK;
	}

	int32_t getRemoteDelete(IdList& existList)
	{
		IdList idList;
		userContext_->getDataBaseMgr()->getRemoteTable()->getNodesByMarkStatus(existList, idList, MS_Missed);

		if(idList.empty())
		{
			return RT_OK;
		}

		userContext_->getDataBaseMgr()->getRemoteTable()->deleteNodes(idList);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_RemoteID, idList);

		IdList localIdList;
		userContext_->getDataBaseMgr()->getRelationTable()->getExistLocalByRemote(idList, localIdList);
		userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(localIdList, LS_ShowNormal);

		userContext_->getDataBaseMgr()->getRelationTable()->deleteByRemoteId(idList);

		return RT_OK;
	}

	void getRemoteDiff(RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::getRemoteDiff");
		
		if(INVALID_ID==rootId_)
		{
			userContext_->getDataBaseMgr()->getLocalTable()->getRoot(rootId_);
		}

		RemoteNodes newRemoteNodes;
		OperNodes operList;
		std::list<int64_t> deleteList;
		for(RemoteNodes::const_iterator it = syncData.begin();
			it != syncData.end(); ++it)
		{
			boost::this_thread::interruption_point();
			RemoteNode syncNode = *it;
			if(SS_Delete_Status==syncNode->incStatus)
			{
				deleteList.push_back(syncNode->id);
			}
			else
			{
				newRemoteNodes.push_back(syncNode);

				if((ROOT_PARENTID==syncNode->parent)&&(INVALID_ID!=rootId_))
				{
					LocalNode localNode(new st_LocalNode);
					//no sync -> sync, these is no relation, check by parent + name.
					if(RT_OK == userContext_->getDataBaseMgr()->getLocalTable()->getNode(rootId_, syncNode->name, localNode))
					{
						if(LS_NoActionDelete_Status==(localNode->status&LS_NoActionDelete_Status))
						{
							//set sync
							if(SYNC_STATUS == syncNode->status)
							{
								dirDetecte(syncNode->id);
								restoreNoActionDelete(localNode->id);
							}
						}
						else if(NO_SET_SYNC_STATUS == syncNode->status)
						{
							//set no sync
							if(userContext_->getDataBaseMgr()->getRelationTable()->isSync(localNode->id, syncNode->id))
							{
								setNoActionDelete(localNode->id, syncNode->id);
							}
						}
					}
				}
			}
		}

		userContext_->getDataBaseMgr()->getRemoteTable()->getChildren(deleteList);
		userContext_->getDataBaseMgr()->getRemoteTable()->deleteNodes(deleteList);

		IdList localIdList;
		userContext_->getDataBaseMgr()->getRelationTable()->getExistLocalByRemote(deleteList, localIdList);
		userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(localIdList, LS_ShowNormal);

		userContext_->getDataBaseMgr()->getRelationTable()->deleteByRemoteId(deleteList);
		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(newRemoteNodes);
	}

	int32_t getDirMetadata(const int64_t& parentId, RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorLocal::getDirMetadata");

		std::string limitCnt;
		std::string curCnt;
		MAKE_CLIENT(client);
		int64_t ownerId = userContext_->getUserInfoMgr()->getUserId();
		int32_t ret = client().getAllMetadata(ownerId, parentId, limitCnt, curCnt);
		if(RT_OK!=ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "getDirMetadata failed.");
			if(HTTP_PRECONDITION_FAILED==ret)
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_CNT_LIMIT, 
					SD::Utility::String::string_to_wstring(limitCnt), 
					SD::Utility::String::string_to_wstring(curCnt)));
				boost::this_thread::sleep(boost::posix_time::seconds(60));
			}
			return ret;
		}

		syncData.clear();
		ret = userContext_->getDataBaseMgr()->getSyncDataTable()->getLoginSyncData(syncData);
		SERVICE_INFO(MODULE_NAME, RT_OK, "dir id:%I64d, metadata size:%d", parentId, syncData.size());
		return ret;
	}

	int32_t getChangeMetadata(int64_t curVersion, RemoteNodes& syncData)
	{
		std::string limitCnt;
		std::string curCnt;
		MAKE_CLIENT(client);
		int64_t ownerId = userContext_->getUserInfoMgr()->getUserId();
		int32_t ret = client().getSyncMetadata(ownerId, curVersion, limitCnt, curCnt);

		if(RT_OK!=ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "getChangeMetadata failed.");
			if(HTTP_PRECONDITION_FAILED==ret)
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_CNT_LIMIT, 
					SD::Utility::String::string_to_wstring(limitCnt), 
					SD::Utility::String::string_to_wstring(curCnt)));
				boost::this_thread::sleep(boost::posix_time::seconds(60));
			}
			return ret;
		}

		syncData.clear();
		CHECK_RESULT(userContext_->getDataBaseMgr()->getSyncDataTable()->getSyncData(syncData));
		
		if(!syncData.empty())
		{
			SERVICE_INFO(MODULE_NAME, RT_OK, "sync data size:%d", syncData.size());
		}
		return ret;
	}

	void restoreNoActionDelete(const int64_t& id)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "restoreNoActionDelete, id:%I64d", id);
		IdList idList;
		idList.push_back(id);
		userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);
		userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(idList, LS_Normal);
		//add oper
		OperNodes operNodes;
		for(IdList::const_iterator it = idList.begin(); it != idList.end(); ++it)
		{
			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_LocalID;
			operNode->oper = OT_Created;
			operNode->key = *it;
			operNode->size = 0;
			operNodes.push_back(operNode);
		}
		userContext_->getDataBaseMgr()->getDiffTable()->addOperList(operNodes);
	}

	void setNoActionDelete(const int64_t& id, const int64_t& remoteId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "setNoActionDelete, id:%I64d", id);
		IdList idList;
		idList.push_back(id);
		userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);

		AsyncTaskIds runningTaskIds;
		userContext_->getDataBaseMgr()->getTransTaskTable()->getRunningTaskEx(idList, Key_LocalID, runningTaskIds);
		userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);

		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, idList);
		userContext_->getDataBaseMgr()->getLocalTable()->noActionDelete(idList);
		userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(idList);

		IdList remoteIdList;
		remoteIdList.push_back(remoteId);
		userContext_->getDataBaseMgr()->getRemoteTable()->getChildren(remoteIdList);
		//skip top dir
		remoteIdList.pop_front();
		userContext_->getDataBaseMgr()->getRemoteTable()->deleteNodes(remoteIdList);
	}
private:
	UserContext* userContext_;
	int64_t rootId_;

	int64_t fullDetectVersion_;
	bool lastFullNoDiff_;
};

std::auto_ptr<RemoteDetector> RemoteDetectorLocal::create(UserContext* userContext)
{
	return std::auto_ptr<RemoteDetector>(new RemoteDetectorLocalImpl(userContext));
}