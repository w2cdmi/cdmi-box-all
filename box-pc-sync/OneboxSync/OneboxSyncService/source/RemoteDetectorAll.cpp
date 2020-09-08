#include "RemoteDetectorAll.h"
#include "DataBaseMgr.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"
#include "InIHelper.h"
#include "NotifyMgr.h"
#include "SyncUtility.h"
#include "FilterMgr.h"
#include "AsyncTaskMgr.h"
#include "Utility.h"
#include <boost/thread.hpp>
#include "GlobalVariable.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RemoteDetectorAll")
#endif

using namespace SD;

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

class RemoteDetectorAllImpl : public RemoteDetectorAll
{
public:
	RemoteDetectorAllImpl(UserContext* userContext)
		:userContext_(userContext)
		,fullDetectVersion_(INVALID_VALUE)
		,lastFullNoDiff_(false)
	{
	}

	virtual ~RemoteDetectorAllImpl(void)
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

		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::fullDetect");

		if(fullDetectVersion_>0)
		{
			CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
			int64_t curVersion = iniHelper.GetValue<int64_t>(L"", CONF_SYNCVERSION_KEY, 0);
			if((fullDetectVersion_==curVersion)&&lastFullNoDiff_)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "remote no change, skip fullDetect.");
				return RT_OK;
			}
			if(userContext_->getDataBaseMgr()->getDiffTable()->getNormalDiffCount()>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "the diff is being processed, skip fullDetect.");
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
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::dirDetecte");

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

		SERVICE_DEBUG(MODULE_NAME, RT_OK, "RemoteDetectorAll::getRemoteDirDiff Entered...", NULL);
		OperNodes remoteDiff;
		RemoteNodes newRemoteNodes;
		getRemoteDirDiff(id, syncData, newRemoteNodes, remoteDiff);
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "RemoteDetectorAll::getRemoteDirDiff Exited...", NULL);

		userContext_->getDataBaseMgr()->getDiffTable()->addOperList(remoteDiff);
		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(newRemoteNodes);

		return ret;
	}
private:
	void getAllCreateDiff(RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::getAllCreateDiff");
		OperNodes remoteDiff;
		DiffPathNodes diffInfos;
		RemoteNodes newRemoteNodes;

		getCreateDiff(ROOT_PARENTID, L"", syncData, newRemoteNodes, remoteDiff, diffInfos);

		CInIHelper iniHelper(userContext_->getConfigureMgr()->getConfigure()->userDataPath() + SYNC_CONF_NAME);
		userContext_->getDataBaseMgr()->getDiffTable()->addOperList(remoteDiff);
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
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::getFullRemoteDiff");
		OperNodes remoteDiff;
		RemoteNodes newRemoteNodes;
		//get diff starting from the root
		IdList existList;
		existList.push_back(0);
		getRemoteDiff(ROOT_PARENTID, syncData, newRemoteNodes, remoteDiff, existList);
		getRemoteDelete(existList, remoteDiff);

		userContext_->getDataBaseMgr()->getDiffTable()->addOperList(remoteDiff);
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

	void getRemoteDiff(RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::getRemoteDiff");
		
		RemoteNodes newRemoteNodes;
		OperNodes operList;
		std::list<int64_t> deleteList;
		RemoteNode remoteNode(new st_RemoteNode);
		for(RemoteNodes::const_iterator it = syncData.begin();
			it != syncData.end(); ++it)
		{
			boost::this_thread::interruption_point();
			RemoteNode syncNode = *it;
			bool isExsit = false;
			bool isSync = true;

			remoteNode->id = syncNode->id;
			if(RT_OK==userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
			{
				if(!(RS_Delete_Status&remoteNode->status))
				{
					isExsit = true;
				}

				if((ROOT_PARENTID==remoteNode->parent)
					&&(!(RS_Sync_Status&remoteNode->status)))
				{
					isSync = false;
				}
			}

			//priority:delete>create>others
			if(SS_Delete_Status==syncNode->incStatus)
			{
				if(isExsit)
				{
					OperNode pOperNode(new st_OperNode());
					pOperNode->keyType = Key_RemoteID;
					pOperNode->key = syncNode->id;
					pOperNode->oper = OT_Deleted;
					pOperNode->priority = PRIORITY_LEVEL3;
					operList.push_back(pOperNode);
					deleteList.push_back(syncNode->id);
				}
				continue;
			}		

			newRemoteNodes.push_back(syncNode);

			//set noSync
			if((ROOT_PARENTID==syncNode->parent)&&(NO_SET_SYNC_STATUS ==syncNode->status))
			{
				if(isExsit&&isSync)
				{
					OperNode pOperNode(new st_OperNode());
					pOperNode->keyType = Key_RemoteID;
					pOperNode->key = syncNode->id;
					pOperNode->oper = OT_SetNoSync;
					pOperNode->priority = PRIORITY_LEVEL3;
					operList.push_back(pOperNode);
					deleteList.push_back(syncNode->id);
				}
				continue;
			}

			//set Sync
			if(isExsit&&(!isSync))
			{
				OperNode pOperNode(new st_OperNode());
				pOperNode->keyType = Key_RemoteID;
				pOperNode->key = syncNode->id;
				pOperNode->oper = OT_SetSync;
				pOperNode->size = syncNode->size;
				operList.push_back(pOperNode);

				//get children
				if(FILE_TYPE_DIR == syncNode->type)
				{
					dirDetecte(syncNode->id);
				}
				continue;
			}

			if(!isExsit)
			{
				if (!isSkipOper(syncNode))
				{
					OperNode pOperNode(new st_OperNode());
					pOperNode->keyType = Key_RemoteID;
					pOperNode->key = syncNode->id;
					pOperNode->oper = OT_Created;
					pOperNode->size = syncNode->size;
					operList.push_back(pOperNode);
				}				
				continue;
			}

			if(remoteNode->parent!=syncNode->parent)
			{
				OperNode pOperNode(new st_OperNode());
				pOperNode->keyType = Key_RemoteID;
				pOperNode->key = syncNode->id;
				pOperNode->oper = OT_Moved;
				operList.push_back(pOperNode);
			}
			if(remoteNode->name!=syncNode->name)
			{
				OperNode pOperNode(new st_OperNode());
				pOperNode->keyType = Key_RemoteID;
				pOperNode->key = syncNode->id;
				pOperNode->oper = OT_Renamed;
				operList.push_back(pOperNode);
			}
			if((FILE_TYPE_FILE==syncNode->type)&&(remoteNode->version!=syncNode->version))
			{
				if (!isSkipOper(syncNode))
				{
					OperNode pOperNode(new st_OperNode());
					pOperNode->keyType = Key_RemoteID;
					pOperNode->key = syncNode->id;
					pOperNode->oper = OT_Edited;
					pOperNode->size = syncNode->size;
					operList.push_back(pOperNode);
				}
			}
		}

		userContext_->getDataBaseMgr()->getDiffTable()->addOperList(operList);
		userContext_->getDataBaseMgr()->getRemoteTable()->getChildren(deleteList);
		userContext_->getDataBaseMgr()->getRemoteTable()->trashDeleteNodes(deleteList);
		userContext_->getDataBaseMgr()->getRemoteTable()->replaceRemoteNodes(newRemoteNodes);
	}

	int32_t getRemoteDiff(const int64_t& parentId, 
		RemoteNodes& syncData, 
		RemoteNodes& newRemoteList,
		OperNodes& remoteDiff,
		IdList& existList)
	{
		RemoteNodes dirSyncData;
		getSyncDataByParent(parentId, syncData, dirSyncData);

		for(RemoteNodes::iterator it = dirSyncData.begin();
			it != dirSyncData.end(); ++it)
		{
			boost::this_thread::interruption_point();
			RemoteNode syncNode = *(it);
			existList.push_back(syncNode->id);

			RemoteNode remoteNode(new st_RemoteNode);
			bool isExsit = false;
			bool isSync = true;

			remoteNode->id = syncNode->id;
			if(RT_OK==userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
			{
				isExsit = true;
				if((ROOT_PARENTID==remoteNode->parent)
					&&(NO_SET_SYNC_STATUS ==remoteNode->status))
				{
					isSync = false;
				}
			}

			//no sync status
			if((ROOT_PARENTID==syncNode->parent)&&(NO_SET_SYNC_STATUS ==syncNode->status))
			{
				//set no sync
				if(isExsit&&isSync)
				{
					//set no sync;
					OperNode operNode(new st_OperNode);
					operNode->keyType = Key_RemoteID;
					operNode->key = syncNode->id;
					operNode->oper = OT_SetNoSync;
					operNode->priority = PRIORITY_LEVEL3;
					remoteDiff.push_back(operNode);
					newRemoteList.push_back(syncNode);
				}
				else if(!isExsit)
				{
					newRemoteList.push_back(syncNode);
				}
				continue;
			}

			//set Sync
			if(isExsit&&(!isSync))
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = syncNode->id;
				operNode->oper = OT_SetSync;
				operNode->size = syncNode->size;
				remoteDiff.push_back(operNode);
				newRemoteList.push_back(syncNode);
				if(FILE_TYPE_DIR==syncNode->type)
				{
					getRemoteDiff(syncNode->id, syncData, newRemoteList, remoteDiff, existList);
				}
				continue;
			}

			if(!isExsit)
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = syncNode->id;
				operNode->oper = OT_Created;
				operNode->size = syncNode->size;
				remoteDiff.push_back(operNode);
				newRemoteList.push_back(syncNode);
				if(FILE_TYPE_DIR==syncNode->type)
				{
					getRemoteDiff(syncNode->id, syncData, newRemoteList, remoteDiff, existList);
				}
				continue;
			}

			bool hasChange = false;
			if(remoteNode->parent!=syncNode->parent)
			{
				hasChange = true;
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = syncNode->id;
				operNode->oper = OT_Moved;
				remoteDiff.push_back(operNode);
			}
			if(remoteNode->name!=syncNode->name)
			{
				hasChange = true;
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = syncNode->id;
				operNode->oper = OT_Renamed;
				remoteDiff.push_back(operNode);
			}
			if((FILE_TYPE_FILE==syncNode->type) && (remoteNode->version!=syncNode->version))
			{
				hasChange = true;
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = syncNode->id;
				operNode->oper = OT_Edited;
				operNode->size = syncNode->size;
				remoteDiff.push_back(operNode);
			}

			if(hasChange)
			{
				newRemoteList.push_back(syncNode);
			}

			if(FILE_TYPE_DIR==syncNode->type)
			{
				getRemoteDiff(syncNode->id, syncData, newRemoteList, remoteDiff, existList);
			}
		}
		return RT_OK;
	}

	int32_t getCreateDiff(const int64_t& parentId,
		const std::wstring& parentPath,
		RemoteNodes& syncData, 
		RemoteNodes& newRemoteList,
		OperNodes& remoteDiff,
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

			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_RemoteID;
			operNode->key = syncNode->id;
			operNode->oper = OT_Created;
			if(FILE_TYPE_FILE==syncNode->type)
			{
				operNode->size = syncNode->size;
			}
			operNode->priority = PRIORITY_LEVEL5;
			remoteDiff.push_back(operNode);

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
				getCreateDiff(syncNode->id, diffPathNode->remotePath, syncData, newRemoteList, remoteDiff, diffInfos);
			}
		}
		return RT_OK;
	}

	int32_t getRemoteDirDiff(const int64_t& parentId, 
		RemoteNodes& syncData, 
		RemoteNodes& newRemoteList,
		OperNodes& remoteDiff)
	{
		RemoteNodes dirSyncData;
		getSyncDataByParent(parentId, syncData, dirSyncData);

		for(RemoteNodes::iterator it = dirSyncData.begin();
			it != dirSyncData.end(); ++it)
		{
			boost::this_thread::interruption_point();
			RemoteNode syncNode = *(it);

			RemoteNode remoteNode(new st_RemoteNode);
			remoteNode->id = syncNode->id;
			if(RT_OK==userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
			{
				continue;
			}

			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_RemoteID;
			operNode->key = syncNode->id;
			operNode->oper = OT_Created;
			operNode->size = syncNode->size;
			remoteDiff.push_back(operNode);
			newRemoteList.push_back(syncNode);

			if(FILE_TYPE_DIR==syncNode->type)
			{
				getRemoteDirDiff(syncNode->id, syncData, newRemoteList, remoteDiff);
			}
		}
		return RT_OK;
	}

	int32_t getDirMetadata(const int64_t& parentId, RemoteNodes& syncData)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "RemoteDetectorAll::getDirMetadata");

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

	int32_t getRemoteDelete(IdList& existList, OperNodes& remoteDiff)
	{
		IdList idList;
		userContext_->getDataBaseMgr()->getRemoteTable()->getNodesByMarkStatus(existList, idList, MS_Missed);

		if(idList.empty())
		{
			return RT_OK;
		}

		userContext_->getDataBaseMgr()->getRemoteTable()->trashDeleteNodes(idList);
		SERVICE_INFO(MODULE_NAME, RT_OK, "delete remoteNode, id:%s", Sync::getInStr(idList).c_str())

		//When the parent is deleted, ignore the children's deleted
		userContext_->getDataBaseMgr()->getRemoteTable()->filterChildren(idList);
		SERVICE_INFO(MODULE_NAME, RT_OK, "delete remoteNode after filter, id:%s", Sync::getInStr(idList).c_str())

		for(IdList::const_iterator it = idList.begin();
			it != idList.end(); ++it)
		{
			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_RemoteID;
			operNode->key = *it;
			operNode->oper = OT_Deleted;
			operNode->priority = PRIORITY_LEVEL3;
			remoteDiff.push_back(operNode);
		}

		return RT_OK;
	}

	bool isSkipOper(const RemoteNode& remoteNode)
	{
		if (FILE_TYPE_DIR == remoteNode->type)
		{
			return false;
		}
		//when the file is uploading, skip the remote create and edit
		return userContext_->getDataBaseMgr()->getTransTaskTable()->isUploading(remoteNode->id, remoteNode->parent, remoteNode->name);
		/*
		LocalNode localNode(new st_LocalNode);
		int32_t ret = userContext_->getDataBaseMgr()->getRelationTable()->getLocalIdByRemoteId(remoteNode->id, localNode->id);
		if (RT_SQLITE_NOEXIST == ret)
		{
			ret = userContext_->getDataBaseMgr()->getRelationTable()->getLocalIdByRemoteId(remoteNode->parent, localNode->parent);
			if (RT_OK != ret)
			{
				return false;
			}
			ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(localNode->parent, remoteNode->name, localNode);
			if (RT_OK != ret)
			{
				return false;
			}
		}
		else if (RT_OK != ret)
		{
			return false;
		}

		AsyncTransTaskNode transTaskNode;
		ret = userContext_->getDataBaseMgr()->getTransTaskTable()->getNode(AsyncTaskId(
			AsyncTaskId(Utility::String::type_to_string<std::wstring>(localNode->id), L"", ATT_Upload)), transTaskNode);
		if (RT_SQLITE_NOEXIST == ret)
		{
			return false;
		}
		if (RT_OK == ret)
		{
			return (transTaskNode.status == ATS_Running);
		}
		return false;
		*/
	}

private:
	UserContext* userContext_;

	int64_t fullDetectVersion_;
	bool lastFullNoDiff_;
};

std::auto_ptr<RemoteDetector> RemoteDetectorAll::create(UserContext* userContext)
{
	return std::auto_ptr<RemoteDetector>(new RemoteDetectorAllImpl(userContext));
}