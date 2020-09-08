#include "SyncTransTask.h"
#include "Utility.h"
#include "AsyncTaskMgr.h"
#include "TransTask.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ConfigureMgr.h"
#include "SyncCommon.h"
#include "DataBaseMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncTransTask")
#endif

using namespace SD::Utility;

class SyncTaskAdapter : public IAsyncTaskAdapter
{
public:
	SyncTaskAdapter(UserContext* userContext):userContext_(userContext)
	{
		dataBaseMgr_ = DataBaseMgr::getInstance(userContext);
	}

	virtual bool isSurpport(const AsyncTransTaskNode& node)
	{
		if (ATT_Upload == node->id.type || ATT_Download == node->id.type)
		{
			return true;
		}
		return false;
	}

	virtual int32_t updateNode(CISSPTask* ptrTask, const FILE_DIR_INFO& fileNode)
	{
		int32_t ret = RT_OK;
		TransTask* transTask = static_cast<TransTask*>(ptrTask);
		if(NULL == transTask)
		{
			return RT_INVALID_PARAM;
		}
		AsyncTaskId taskId = transTask->getTaskNode().get()->id;

		if(ATT_Upload == taskId.type)
		{
			//boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->remoteMutex());
			RemoteNode remoteNode(new st_RemoteNode);
			remoteNode->id = fileNode.id;
			remoteNode->parent = fileNode.parent;
			remoteNode->name = fileNode.name;
			remoteNode->type = fileNode.type;
			remoteNode->status = RS_Sync_Status;
			remoteNode->version = fileNode.version;

			CHECK_RESULT(dataBaseMgr_->getRemoteTable()->replaceRemoteNode(remoteNode));
			// when complete upload file, update the LocalTable's modify time and create time
			// for local full detect
			int64_t localId = SD::Utility::String::string_to_type<int64_t>(taskId.id);
			(void)dataBaseMgr_->getLocalTable()->updateTimeInfo(localId, fileNode.ctime, fileNode.mtime);
				
			//TODO add the special suffix to upload table 
			/*
			if(userContext_->getFilterMgr()->isUploadFilter(remoteNode->name))
			{
				dataBaseMgr_->getUploadTable()->replaceFilterInfo(localId);
			}				
			*/

			//change remote id
			int64_t oldRemoteId = INVALID_ID;
			if(RT_OK==dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localId, oldRemoteId))
			{
				if(oldRemoteId!=remoteNode->id)
				{
					dataBaseMgr_->getRelationTable()->deleteByRemoteId(oldRemoteId);
					dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, oldRemoteId);
					OperNode operNode(new st_OperNode);
					operNode->keyType = Key_RemoteID;
					operNode->key = oldRemoteId;
					operNode->oper = OT_Created;
					operNode->size = 0;
					dataBaseMgr_->getDiffTable()->addOper(operNode);
				}
			}

			return dataBaseMgr_->getRelationTable()->addRelation(remoteNode->id, localId);
		}
		else if(ATT_Download == taskId.type)
		{
			//boost::mutex::scoped_lock lock(userContext_->getGlobalVariable()->globalMutex()->localMutex());
			LocalNode node(new st_LocalNode);
			node->id = fileNode.id;
			node->parent = fileNode.parent;
			node->name = fileNode.name;
			node->type = fileNode.type;
			node->ctime = fileNode.ctime;
			node->mtime = fileNode.mtime;
			node->status = LS_Normal;

			CHECK_RESULT(dataBaseMgr_->getLocalTable()->replaceNode(node));
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->addRelation(SD::Utility::String::string_to_type<int64_t>(taskId.id), node->id));
			return ret;
		}
		return RT_INVALID_PARAM;
	}

	virtual int32_t completeNode(CISSPTask* ptrTask)
	{
		int32_t ret = RT_OK;
		TransTask* transTask = static_cast<TransTask*>(ptrTask);
		if(NULL == transTask)
		{
			return RT_INVALID_PARAM;
		}
		AsyncTaskId taskId = transTask->getTaskNode().get()->id;
		KeyType keyType = Key_RemoteID;
		if(ATT_Upload==taskId.type)
		{
			keyType = Key_LocalID;
		}
		int64_t key = SD::Utility::String::string_to_type<int64_t>(taskId.id);

		bool hasNewOper = false;
		IdList diffIdList;
		dataBaseMgr_->getDiffTable()->getRunningDiff(keyType, key, diffIdList, hasNewOper);
		if(transTask->IsError())
		{
			dataBaseMgr_->getDiffTable()->refreshDiff(diffIdList, Diff_Failed);
			dataBaseMgr_->getDiffTable()->updateErrorCode(keyType, key, transTask->GetErrorCode());
		}
		else if(transTask->IsCancel())
		{
			dataBaseMgr_->getDiffTable()->refreshDiff(diffIdList, Diff_Normal);
		}
		//IsCompletedWithSuccess
		else
		{
			if(!hasNewOper)
			{
				dataBaseMgr_->getDiffTable()->completeDiff(keyType, key);
			}
			else
			{
				dataBaseMgr_->getDiffTable()->refreshDiff(diffIdList, Diff_Complete);
			}		
		}

		return RT_OK;
	}

	virtual int32_t convertLocalId2RemoteId(CISSPTask* ptrTask, const int64_t localId, int64_t& remoteId)
	{
		if (NULL == ptrTask || localId == INVALID_ID)
		{
			return RT_INVALID_PARAM;
		}
		return dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localId, remoteId);
	}

private:
	UserContext* userContext_;
	DataBaseMgr* dataBaseMgr_;
};

class SyncTransTaskImpl : public SyncTransTask
{
public:
	SyncTransTaskImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual void setAdapter()
	{
		userContext_->getAsyncTaskMgr()->setAdapter(new SyncTaskAdapter(userContext_));
	}

	virtual int32_t upload(const Path& localPath, const Path& remoteParent)
	{
		AsyncTaskId taskId;
		int32_t ret = userContext_->getAsyncTaskMgr()->upload(userContext_->id, 
			localPath, 
			remoteParent,
			taskId);
		return ret;
	}

	virtual int32_t download(const Path& remotePath, const Path& localParent)
	{
		AsyncTaskId taskId;
		int32_t ret = userContext_->getAsyncTaskMgr()->download(userContext_->id, 
			remotePath,
			localParent,
			taskId);
		return ret;	
	}

private:
	UserContext* userContext_;
};

std::auto_ptr<SyncTransTask> SyncTransTask::instance_(NULL);

SyncTransTask* SyncTransTask::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new SyncTransTaskImpl(userContext));
	}
	return instance_.get();
}