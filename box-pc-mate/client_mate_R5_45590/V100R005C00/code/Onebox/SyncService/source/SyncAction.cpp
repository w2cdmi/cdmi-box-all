#include <fstream>
#include <boost/thread.hpp>
#include "SyncAction.h"
#include "DataBaseMgr.h"
#include "FilterMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ConfigureMgr.h"
#include "AsyncTaskMgr.h"
#include "LocalFile.h"
#include "SyncUtility.h"
#include "TransTaskTable.h"
#include "SyncTransTask.h"
#include "SyncConfigure.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncAction")
#endif

class SyncActionImpl : public SyncAction
{
public:
	SyncActionImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		dataBaseMgr_ = DataBaseMgr::getInstance(userContext);
		transTaskTable_ = TransTaskTable::create(userContext_->getConfigureMgr()->getConfigure()->appUserDataPath()+
			PATH_DELIMITER+SD::Utility::String::type_to_string<std::wstring>(userContext_->id.id));
	}

	virtual ~SyncActionImpl(void)
	{
	}

	virtual int32_t executeAction(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		for(ExecuteActions::const_iterator it = diffNode->getActions().begin(); it!= diffNode->getActions().end(); ++it)
		{
			if(CMD_NoAction == (*it)->actionCommand)
			{
				continue;
			}

			if(ActionType_Local == (*it)->actionType)
			{
				switch ((*it)->actionCommand)
				{
				case CMD_Create:
					ret = localCreate(diffNode, nextOperNode);
					break;
				case CMD_Delete:
					ret = localDelete(diffNode);
					break;
				case CMD_Rename:
					ret = localRename(diffNode);
					if(RT_OK != ret)
					{
						if(SRK_Local_Renamed==(diffNode->getSyncRuleKey()&SRK_Local_Renamed))
						{
							SERVICE_INFO(MODULE_NAME, ret, "localRename failed. rename remote");
							ret = remoteRename(diffNode);
						}
					}
					if(RT_OK==ret)
					{
						diffNode->stepComplete(OT_Renamed);
					}
					break;
				case CMD_Move:
					ret = localMove(diffNode, nextOperNode);
					if(RT_OK != ret)
					{
						if(SRK_Local_Moved==(diffNode->getSyncRuleKey()&SRK_Local_Moved))
						{
							SERVICE_INFO(MODULE_NAME, ret, "localMove failed. move remote");
							ret = remoteMove(diffNode, nextOperNode);
						}
					}
					if(RT_OK==ret)
					{
						diffNode->stepComplete(OT_Moved);
					}
					break;
				default:
					break;
				}
			}
			else if(ActionType_Remote == (*it)->actionType)
			{
				switch ((*it)->actionCommand)
				{
				case CMD_Create:
					ret = remoteCreate(diffNode, nextOperNode);
					break;
				case CMD_Delete:
					ret = remoteDelete(diffNode);
					break;
				case CMD_Rename:
					ret = remoteRename(diffNode);
					if(RT_OK==ret)
					{
						diffNode->stepComplete(OT_Renamed);
					}
					break;
				case CMD_Move:
					ret = remoteMove(diffNode, nextOperNode);
					if(RT_OK==ret)
					{
						diffNode->stepComplete(OT_Moved);
					}
					break;		
				default:
					break;
				}
			}
			SERVICE_INFO(MODULE_NAME, ret, "execute action. diffNode:[%I64d]<->[%I64d], command:%s", 
				diffNode->getLocalId(), diffNode->getRemoteId(), it->get()->toString().c_str());
			if(RT_OK!=ret)
			{
				break;
			}
		}

		if(RT_CANCEL==ret)
		{
			return RT_OK;
		}
		else if(RT_RUNNING==ret)
		{
			diffNode->refreshDiff(Diff_Running);
		}
		else
		{
			diffNode->refreshDiff((RT_OK==ret)?Diff_Complete:Diff_Failed, ret);
		}
		return ret;
	}

private:
	int32_t localCreate(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getRemoteId())
		{
			return RT_ERROR;
		}

		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = diffNode->getRemoteId();
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			if(dataBaseMgr_->getRelationTable()->remoteIdIsExist(diffNode->getRemoteId()))
			{
				//local node has been created
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "local node has been created. remoteId:%I64d", diffNode->getRemoteId());
				return RT_OK;
			}
		}

		//TODO check filter char, delete children
		//if(userContext_->getFilterMgr()->isStaticFilter(remoteNode->name))
		//{
		//	return RT_DIFF_FILTER;
		//}

		//check parent
		int64_t localParentId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->parent, localParentId))
		{
			nextOperNode->key = remoteNode->parent;
			nextOperNode->keyType = Key_RemoteID;
			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "localCreate. parent:%I64d not exist", remoteNode->parent);
			//if parent is not RT_OK, update children to RT_PARENT_NOEXIST_ERROR
			if(RT_OK!=dataBaseMgr_->getDiffTable()->getErrorCode(Key_RemoteID, remoteNode->parent))
			{
				IdList idList;
				idList.push_back(remoteNode->parent);
				dataBaseMgr_->getRemoteTable()->getChildren(idList);
				idList.pop_front();
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_RemoteID, idList, RT_PARENT_NOEXIST_ERROR);
			}
			return RT_CANCEL;
		}

		//check local path
		LocalNode conflictNode(new st_LocalNode);
		if(RT_OK == dataBaseMgr_->getLocalTable()->getNode(localParentId, remoteNode->name, conflictNode))
		{
			int64_t remoteId = INVALID_ID;
			if(RT_SQLITE_NOEXIST == dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(conflictNode->id, remoteId))
			{
				//merger local node and remote node
				if((remoteNode->type == FILE_TYPE_DIR)&&(conflictNode->type == FILE_TYPE_DIR))
				{
					megerDir(remoteNode->id, conflictNode->id);
					if(LS_NoActionDelete_Status==(conflictNode->status&LS_NoActionDelete_Status))
					{
						restoreNoActionDelete(conflictNode->id, FILE_TYPE_DIR);
					}
					return RT_CANCEL;
				}
				else if((remoteNode->type == FILE_TYPE_FILE)&&(conflictNode->type == FILE_TYPE_FILE))
				{
					//check task
					AsyncTaskId taskId(SD::Utility::String::type_to_string<std::wstring>(conflictNode->id),
						L"", ATT_Upload, userContext_->id);
					if(transTaskTable_->isExist(taskId))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_OK, 
							"localCreate. diff remoteId:%I64d, remoteType:%d, localId:%I64d is uploading", 
							diffNode->getRemoteId(), remoteNode->type, conflictNode->id);
						userContext_->getAsyncTaskMgr()->delTask(taskId);
						bool hasNewOper = false;
						IdList diffIdList;
						//refresh diffstatus from Diff_Running to Diff_Normal
						dataBaseMgr_->getDiffTable()->getRunningDiff(Key_LocalID, conflictNode->id, diffIdList, hasNewOper);
						dataBaseMgr_->getDiffTable()->refreshDiff(diffIdList, Diff_Normal);
					}
					megerFile(remoteNode->id, conflictNode->id);
					if(LS_NoActionDelete_Status==(conflictNode->status&LS_NoActionDelete_Status))
					{
						restoreNoActionDelete(conflictNode->id, FILE_TYPE_FILE);
					}
					return RT_CANCEL;
				}
				else
				{
					SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "localCreate. diff remoteId:%I64d, remoteType:%d, conflictType:%d", 
						diffNode->getRemoteId(), remoteNode->type, conflictNode->type);
					return RT_DIFF_CONFILCTPATH;
				}
			}

			//conflict
			if((INVALID_ID!=remoteId)&&(remoteId!=diffNode->getRemoteId()))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "localCreate. diff remoteId:%I64d, remoteId:%I64d", diffNode->getRemoteId(), remoteId);
				return RT_DIFF_CONFILCTPATH;
			}
		}

		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(localParentId);
		parent.path(dataBaseMgr_->getLocalTable()->getPath(localParentId));
		if(FILE_TYPE_FILE == remoteNode->type)
		{
			//add download task
			Path remotePath = userContext_->getPathMgr()->makePath();
			remotePath.id(remoteNode->id);
			remotePath.parent(remoteNode->parent);
			remotePath.name(remoteNode->name);

			//remove local file
			std::wstring localPath = parent.path() + PATH_DELIMITER + remoteNode->name;
			if (SD::Utility::FS::is_exist(localPath))
			{
				FILE_DIR_INFO fileInfo;
				Path filePath = userContext_->getPathMgr()->makePath();
				filePath.path(localPath);
				(void)userContext_->getSyncFileSystemMgr()->getProperty(filePath, fileInfo, ADAPTER_FILE_TYPE_LOCAL);
				if(INVALID_ID == fileInfo.id)
				{
					SERVICE_ERROR(MODULE_NAME, RT_INVALID_PARAM, "get property failed. path:%s.", 
						SD::Utility::String::wstring_to_string(localPath).c_str());
					diffNode->refreshDiff(Diff_Failed, RT_INVALID_PARAM);
					return RT_CANCEL;
				}

				if(fileInfo.id != diffNode->getLocalId())
				{
					SERVICE_ERROR(MODULE_NAME, RT_DIFF_CONFILCTPATH, "local file exist. id:%I64d.", fileInfo.id);
					diffNode->refreshDiff(Diff_Failed, RT_DIFF_CONFILCTPATH);
					return RT_CANCEL;
				}
				//check modify time
				if(conflictNode->mtime!=fileInfo.mtime)
				{
					SERVICE_ERROR(MODULE_NAME, RT_FILE_WRITE_ERROR, "local file edited. path:%s, db mtime:%I64d, local mtime:%I64d.", 
						SD::Utility::String::wstring_to_string(localPath).c_str(), conflictNode->mtime, fileInfo.mtime);
					diffNode->refreshDiff(Diff_Failed, RT_FILE_WRITE_ERROR);
					return RT_CANCEL;
				}
					
				CHECK_RESULT(SD::Utility::FS::remove(localPath));
				SERVICE_INFO(MODULE_NAME, RT_OK, 
						"download file %s, delete local exsit file.",
						SD::Utility::String::wstring_to_string(localPath).c_str());
				(void)dataBaseMgr_->getLocalTable()->deleteNode(conflictNode->id);
			}

			CHECK_RESULT(SyncTransTask::getInstance(userContext_)->download(remotePath , parent));
			return RT_RUNNING;
		}
		else if(FILE_TYPE_DIR == remoteNode->type)
		{
			FILE_DIR_INFO info;
			CHECK_RESULT(userContext_->getSyncFileSystemMgr()->create(parent, remoteNode->name, info, ADAPTER_FOLDER_TYPE_LOCAL));
			LocalNode localNode(new st_LocalNode);
			localNode->id = info.id;
			localNode->parent = info.parent;
			localNode->name = info.name;
			localNode->type = info.type;
			localNode->status = LS_Normal;
			localNode->ctime = info.ctime;
			localNode->mtime = info.mtime;

			CHECK_RESULT(dataBaseMgr_->getLocalTable()->addNode(localNode));
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->addRelation(remoteNode->id, localNode->id));

			if(RT_OK!=dataBaseMgr_->getDiffTable()->getErrorCode(Key_RemoteID, remoteNode->id))
			{
				IdList idList;
				idList.push_back(remoteNode->id);
				dataBaseMgr_->getRemoteTable()->getChildren(idList);
				idList.pop_front();
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_RemoteID, idList, RT_OK);
			}
		}
		else
		{
			return RT_ERROR;
		}

		return ret;
	}

	int32_t localRename(DiffNode& diffNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getRemoteId())
		{
			return RT_ERROR;
		}

		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = diffNode->getRemoteId();
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode));

		//TODO check filter char
		//if(userContext_->getFilterMgr()->isStaticFilter(remoteNode->name))
		//{
		//	return RT_DIFF_FILTER;
		//}

		//check local node
		int64_t localId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->id, localId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "localRename. relation not exist, remoteId:%I64d", remoteNode->id);
			changeOpToCreate(Key_RemoteID, remoteNode->id);
			return RT_SQLITE_NOEXIST;
		}
		LocalNode localNode(new st_LocalNode);
		if(RT_OK == dataBaseMgr_->getLocalTable()->getNode(localId, localNode))
		{
			if(localNode->name==remoteNode->name)
			{
				return RT_OK;
			}
		}
		else
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "localRename. local node:%I64d not exist", localId);
			return RT_SQLITE_NOEXIST;
		}

		std::wstring parentPath = dataBaseMgr_->getLocalTable()->getPath(localNode->parent);
		//check local path
		LocalNode conflictNode(new st_LocalNode);
		if(SD::Utility::FS::is_exist(parentPath + PATH_DELIMITER + remoteNode->name))
		{
			return RT_DIFF_CONFILCTPATH;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(localNode->id);
		path.parent(localNode->parent);
		path.name(localNode->name);
		path.path(parentPath + PATH_DELIMITER + localNode->name);

		localNode->name = remoteNode->name;

		CHECK_RESULT(userContext_->getSyncFileSystemMgr()->rename(path, remoteNode->name, 
			(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_LOCAL:ADAPTER_FILE_TYPE_LOCAL));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			std::wstring oldPath = parentPath + PATH_DELIMITER + localNode->name;
			std::wstring newPath = parentPath + PATH_DELIMITER + remoteNode->name;
			if(oldPath!=newPath)
			{
				dataBaseMgr_->getDiffTable()->replaceSubPath(oldPath, newPath);
			}
		}

		CHECK_RESULT(dataBaseMgr_->getLocalTable()->updateNode(localNode));

		return ret;
	}

	int32_t localMove(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getRemoteId())
		{
			return RT_ERROR;
		}

		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = diffNode->getRemoteId();
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode));

		//check local node
		int64_t localId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->id, localId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "localMove. relation not exist, remoteId:%I64d", remoteNode->id);
			changeOpToCreate(Key_RemoteID, remoteNode->id);
			return RT_SQLITE_NOEXIST;
		}

		//check parent
		int64_t localParentId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->parent, localParentId))
		{
			if(!dataBaseMgr_->getRemoteTable()->isExist(remoteNode->parent))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "localMove. parent:%I64d is not exist", remoteNode->parent);
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->key = remoteNode->id;
				operNode->oper = OT_Deleted;
				operNode->priority = PRIORITY_LEVEL3;
				dataBaseMgr_->getDiffTable()->addOper(operNode);
				return RT_OK;
			}

			nextOperNode->key = remoteNode->parent;
			nextOperNode->keyType = Key_RemoteID;
			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "localMove. parent:%I64d not exist", remoteNode->parent);
			return RT_PARENT_NOEXIST_ERROR;
		}
		LocalNode localNode(new st_LocalNode);
		if(RT_OK == dataBaseMgr_->getLocalTable()->getNode(localId, localNode))
		{
			if(localNode->parent==localParentId)
			{
				return RT_OK;
			}
		}
		else
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "localMove. local node:%I64d not exist", localId);
			return RT_SQLITE_NOEXIST;
		}

		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(localParentId);
		parent.path(dataBaseMgr_->getLocalTable()->getPath(localParentId));

		std::wstring localPath = parent.path() + PATH_DELIMITER + localNode->name;
		//check local path
		LocalNode conflictNode(new st_LocalNode);
		if(SD::Utility::FS::is_exist(localPath))
		{
			return RT_DIFF_CONFILCTPATH;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(localNode->id);
		path.parent(localNode->parent);
		path.name(localNode->name);
		path.path(dataBaseMgr_->getLocalTable()->getPath(localNode->id));

		localNode->parent = localParentId;

		CHECK_RESULT(userContext_->getSyncFileSystemMgr()->move(path, parent, false,
			(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_LOCAL:ADAPTER_FILE_TYPE_LOCAL));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			if(localPath!=path.path())
			{
				dataBaseMgr_->getDiffTable()->replaceSubPath(path.path(), localPath);
			}
		}

		CHECK_RESULT(dataBaseMgr_->getLocalTable()->updateNode(localNode));

		return ret;
	}

	int32_t localDelete(DiffNode& diffNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getRemoteId())
		{
			return RT_ERROR;
		}

		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = diffNode->getRemoteId();
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode));
		int64_t localId = INVALID_ID;
		ret = dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(diffNode->getRemoteId(), localId);

		IdList localIdList;
		if(INVALID_ID!=localId)
		{
			localIdList.push_back(localId);
		}
		IdList remoteIdList;
		remoteIdList.push_back(diffNode->getRemoteId());
		if(FILE_TYPE_DIR==remoteNode->type)
		{
			dataBaseMgr_->getLocalTable()->getChildren(localIdList);
			dataBaseMgr_->getRemoteTable()->getChildren(remoteIdList);
			
			if(!localIdList.empty())
			{
				localIdList.pop_front();
			}
			remoteIdList.pop_front();

			//delete children task and diff
			AsyncTaskIds runningTaskIds;
			IdList remoteMoveList;
			if(!localIdList.empty())
			{
				transTaskTable_->getSyncTask(localIdList, ATT_Upload, runningTaskIds);
				dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localIdList);
			}
			if(!remoteIdList.empty())
			{
				transTaskTable_->getSyncTask(remoteIdList, ATT_Download, runningTaskIds);
				dataBaseMgr_->getDiffTable()->getMove(Key_RemoteID, remoteIdList, remoteMoveList);
				dataBaseMgr_->getDiffTable()->completeDiffExMove(Key_RemoteID, remoteIdList, remoteMoveList);
			}
			if(!runningTaskIds.empty())
			{
				userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
			}
			//check remote child move in
			if(!remoteMoveList.empty())
			{
				//when move exist, return ready to wait the move
				SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
				return RT_READY;
			}

			//check remote child move out
			IdList oldRemoteIdList;
			IdList remoteMoveOutList;
			dataBaseMgr_->getRelationTable()->getExistRemoteByLocal(localIdList, oldRemoteIdList);
			dataBaseMgr_->getDiffTable()->getMove(Key_RemoteID, oldRemoteIdList, remoteMoveOutList);
			if(!remoteMoveOutList.empty())
			{
				//when move exist, return ready to wait the move
				SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
				return RT_READY;
			}

			if(INVALID_ID!=localId)
			{
				localIdList.push_back(localId);
			}
			remoteIdList.push_front(diffNode->getRemoteId());
		}
		else
		{
			IdList idList;
			idList.push_back(diffNode->getRemoteId());
			AsyncTaskIds runningTaskIds;
			transTaskTable_->getSyncTask(idList, ATT_Download, runningTaskIds);
			userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
		}
		//check local node
		if(RT_SQLITE_NOEXIST == ret)
		{
			//keep no sync parent
			if((ROOT_PARENTID==remoteNode->parent)&&(!(RS_Sync_Status&remoteNode->status)))
			{
				remoteIdList.pop_front();
			}
			dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "localDelete. relation not exist, remoteId:%I64d", diffNode->getRemoteId());
			return RT_OK;
		}
		LocalNode localNode(new st_LocalNode);
		if(RT_SQLITE_NOEXIST == dataBaseMgr_->getLocalTable()->getNode(localId, localNode))
		{
			//keep no sync parent
			if((ROOT_PARENTID==remoteNode->parent)&&(!(RS_Sync_Status&remoteNode->status)))
			{
				remoteIdList.pop_front();
			}
			dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList);
			dataBaseMgr_->getRelationTable()->deleteByRemoteId(remoteIdList);
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "localDelete. local node:%I64d not exist", localId);
			return RT_OK;
		}

		if(localIdList.size()==remoteIdList.size())
		{
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList));
		}
		else
		{
			checkReCreate(localIdList, remoteIdList);
		}
		//update db to NoActionDelete
		CHECK_RESULT(dataBaseMgr_->getLocalTable()->noActionDelete(localIdList));		

		//keep no sync parent
		if((ROOT_PARENTID==remoteNode->parent)&&(!(RS_Sync_Status&remoteNode->status)))
		{
			remoteIdList.pop_front();
		}
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList));

		return ret;
	}

	int32_t remoteCreate(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getLocalId())
		{
			return RT_ERROR;
		}

		LocalNode localNode(new st_LocalNode);
		ret = dataBaseMgr_->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
		if(RT_SQLITE_NOEXIST==ret)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteCreate. local node not exist, localId:%I64d", localNode->id);
			return RT_OK;
		}
		if(RT_OK!=ret)
		{
			return ret;
		}
		
		if(LS_NoActionDelete_Status==(localNode->status&LS_NoActionDelete_Status))
		{
			return RT_OK;
		}

		if(FILE_TYPE_DIR == localNode->type)
		{
			if(dataBaseMgr_->getRelationTable()->localIdIsExist(diffNode->getLocalId()))
			{
				//remote node has been created
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "remote node has been created. localId:%I64d", diffNode->getLocalId());
				return RT_OK;
			}
		}

		//TODO check filter char
		//if(userContext_->getFilterMgr()->isStaticFilter(localNode->name))
		//{
		//	localFilter(localNode->id, localNode->type);
		//	return RT_CANCEL;
		//}

		//check path length
		Path localPath = userContext_->getPathMgr()->makePath();
		localPath.id(localNode->id);
		localPath.parent(localNode->parent);
		localPath.name(localNode->name);
		std::wstring parentPath = dataBaseMgr_->getLocalTable()->getPath(localNode->parent);
		localPath.path(parentPath + PATH_DELIMITER+ localNode->name);

		//check hidden
		DWORD dwAttribute = GetFileAttributes(std::wstring(L"\\\\?\\"+localPath.path()).c_str());
		if (INVALID_FILE_ATTRIBUTES == dwAttribute)
		{
			return RT_FILE_NOEXIST_ERROR;
		}
		if (FILE_ATTRIBUTE_HIDDEN&dwAttribute)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "hidden path:%s", SD::Utility::String::wstring_to_string(localPath.path()).c_str());
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
		}

		//check upload files
		if(isUploadFilter(localPath.path()))
		{
			UploadTable* uploadTable = dataBaseMgr_->getUploadTable();
			if(NULL!=uploadTable)
			{
				if(dataBaseMgr_->getRelationTable()->localIdIsExist(diffNode->getLocalId())
					&&uploadTable->isFilter(diffNode->getLocalId()))
				{
					return RT_OK;
				}
			}
		}

		//check parent
		int64_t remoteParentId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localNode->parent, remoteParentId))
		{
			LocalStatus localStatus;
			//check no action and filter
			if(dataBaseMgr_->getLocalTable()->isSpecialStatus(localNode->parent, localStatus))
			{
				IdList deleteList;
				deleteList.push_back(localNode->id);
				if(FILE_TYPE_DIR==localNode->type)
				{
					dataBaseMgr_->getLocalTable()->getChildren(deleteList);
				}
				dataBaseMgr_->getLocalTable()->updateStatus(deleteList, localStatus);
				return RT_OK;
			}

			nextOperNode->key = localNode->parent;
			nextOperNode->keyType = Key_LocalID;
			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "remoteCreate. parent:%I64d not exist", localNode->parent);
			//if parent is not RT_OK, update children to RT_PARENT_NOEXIST_ERROR
			if(RT_OK!=dataBaseMgr_->getDiffTable()->getErrorCode(Key_LocalID, localNode->parent))
			{
				IdList idList;
				idList.push_back(localNode->parent);
				dataBaseMgr_->getLocalTable()->getChildren(idList);
				idList.pop_front();
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_LocalID, idList, RT_PARENT_NOEXIST_ERROR);
			}
			return RT_CANCEL;
		}
		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(remoteParentId);
		parent.path(parentPath);

		//check remote path
		RemoteNodes remoteNodes;
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->getRemoteNodes(remoteParentId, localNode->name, remoteNodes));
		if(!remoteNodes.empty())
		{
			for(RemoteNodes::const_iterator it = remoteNodes.begin();
				it != remoteNodes.end(); ++it)
			{
				RemoteNode remoteNode = *it;
				int64_t localId = INVALID_ID;
				if(RT_SQLITE_NOEXIST == dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->id, localId))
				{
					if((ROOT_PARENTID==remoteNode->parent)&&(RS_None==remoteNode->status))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "remoteCreate. diff localId:%I64d, remote nosync:%I64d", diffNode->getLocalId(), remoteNode->id);
						return RT_DIFF_CONFILCTPATH;
					}

					//merger local node and remote node
					if((remoteNode->type == FILE_TYPE_DIR)
						&&(localNode->type == FILE_TYPE_DIR))
					{
						megerDir(remoteNode->id, localNode->id);
						return RT_CANCEL;
					}
					else if((remoteNode->type == FILE_TYPE_FILE)
						&&(localNode->type == FILE_TYPE_FILE))
					{
						AsyncTaskId taskId(SD::Utility::String::type_to_string<std::wstring>(remoteNode->id),
							L"", ATT_Download, userContext_->id);
						if(transTaskTable_->isExist(taskId))
						{
							SERVICE_DEBUG(MODULE_NAME, RT_OK, 
								"localCreate. remoteId:%I64d is downloading", remoteNode->id);
							userContext_->getAsyncTaskMgr()->delTask(taskId);
						}

						megerFile(remoteNode->id, localNode->id);
						//add upload task
						CHECK_RESULT(SyncTransTask::getInstance(userContext_)->upload(localPath, parent));
						return RT_RUNNING;
					}
					else
					{
						SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "remoteCreate. diff localId:%I64d, localType:%d, conflictType:%d", 
							diffNode->getLocalId(), localNode->type, remoteNode->type);
						return RT_DIFF_CONFILCTPATH;
					}
				}

				if((INVALID_ID!=localId)&&(localId!=diffNode->getLocalId()))
				{
					if(!dataBaseMgr_->getLocalTable()->isExist(localId))
					{
						//清除删除场景下异步传输任务可能添加的脏数据
						dataBaseMgr_->getRelationTable()->deleteByLocalId(localId);
					}
					else
					{
						SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "remoteCreate. diff localId:%I64d, localId:%I64d", diffNode->getLocalId(), localId);
						return RT_DIFF_CONFILCTPATH;
					}
				}
			}
		}

		if(FILE_TYPE_FILE == localNode->type)
		{
			//add upload task
			CHECK_RESULT(SyncTransTask::getInstance(userContext_)->upload(localPath, parent));
			return RT_RUNNING;
		}
		else if(FILE_TYPE_DIR == localNode->type)
		{
			FILE_DIR_INFO info;
			CHECK_RESULT(userContext_->getSyncFileSystemMgr()->create(parent, localNode->name, info, ADAPTER_FOLDER_TYPE_REST));

			RemoteNode remoteNode(new st_RemoteNode);
			remoteNode->id = info.id;
			remoteNode->parent = info.parent;
			remoteNode->name = info.name;
			remoteNode->type = info.type;
			remoteNode->version = info.version;
			remoteNode->status = RS_Sync_Status;

			CHECK_RESULT(dataBaseMgr_->getRemoteTable()->addRemoteNode(remoteNode));
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->addRelation(remoteNode->id, localNode->id));

			if(RT_OK!=dataBaseMgr_->getDiffTable()->getErrorCode(Key_LocalID, localNode->id))
			{
				IdList idList;
				idList.push_back(localNode->id);
				dataBaseMgr_->getLocalTable()->getChildren(idList);
				idList.pop_front();
				dataBaseMgr_->getDiffTable()->updateErrorCode(Key_LocalID, idList, RT_OK);
			}
		}
		else
		{
			return RT_ERROR;
		}

		return ret;
	}

	int32_t remoteRename(DiffNode& diffNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getLocalId())
		{
			return RT_ERROR;
		}

		LocalNode localNode(new st_LocalNode);
		ret = dataBaseMgr_->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
		if(RT_SQLITE_NOEXIST==ret)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. local node not exist, localId:%I64d", localNode->id);
			return RT_OK;
		}
		if(RT_OK!=ret)
		{
			return ret;
		}

		if(LS_NoActionDelete_Status==(localNode->status&LS_NoActionDelete_Status))
		{
			return RT_OK;
		}

		std::wstring parentPath = dataBaseMgr_->getLocalTable()->getPath(localNode->parent);
		//TODO check filter char
		//if(userContext_->getFilterMgr()->isStaticFilter(localNode->name))
		//{
		//	localFilter(localNode->id, localNode->type);
		//	return RT_CANCEL;
		//}

		//check path length
		std::wstring lengthPath = parentPath + PATH_DELIMITER + localNode->name;

		//check hidden
		DWORD dwAttribute = GetFileAttributes(std::wstring(L"\\\\?\\"+lengthPath).c_str());
		if (INVALID_FILE_ATTRIBUTES == dwAttribute)
		{
			return RT_FILE_NOEXIST_ERROR;
		}
		if (FILE_ATTRIBUTE_HIDDEN&dwAttribute)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "hidden path:%s", SD::Utility::String::wstring_to_string(lengthPath).c_str());
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
		}

		if(LS_Filter==(localNode->status&LS_Filter))
		{
			//restore the status to normal
			restoreFilter(localNode->id, (FILE_TYPE)localNode->type);
			return RT_OK;
		}

		//check remote node
		int64_t remoteId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localNode->id, remoteId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. relation not exist, localId:%I64d", localNode->id);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}
		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = remoteId;
		if(RT_OK==dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode))
		{
			if(localNode->name==remoteNode->name)
			{
				return RT_OK;
			}
		}
		else
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. remote node:%I64d not exist", remoteId);
			return RT_SQLITE_NOEXIST;
		}

		//check remote path
		RemoteNodes remoteNodes;
		dataBaseMgr_->getRemoteTable()->getRemoteNodes(remoteNode->parent, localNode->name, remoteNodes);
		if(!remoteNodes.empty())
		{
			if(1==remoteNodes.size())
			{
				if((FILE_TYPE_FILE == remoteNode->type)&&(FILE_TYPE_FILE == remoteNodes.begin()->get()->type))
				{
					int64_t newRemoteId = remoteNodes.begin()->get()->id;
					//noSync node
					if((ROOT_PARENTID==remoteNodes.begin()->get()->parent)&&(0==remoteNodes.begin()->get()->status))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_DIFF_CONFILCTPATH, "remoteRename. remote is noSync node. remoteId:%I64d", newRemoteId);
						return RT_DIFF_CONFILCTPATH;
					}

					if((!dataBaseMgr_->getRelationTable()->remoteIdIsExist(newRemoteId))
						&&(!dataBaseMgr_->getDiffTable()->isInDiff(Key_RemoteID, newRemoteId)))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. remote node:%I64d change remoteId to %I64d", 
							remoteId, newRemoteId);
						dataBaseMgr_->getRelationTable()->deleteByRemoteId(remoteId);
						changeOpToCreate(Key_LocalID, localNode->id);
					}
				}
			}
			return RT_DIFF_CONFILCTPATH;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(remoteNode->id);
		path.parent(remoteNode->parent);
		path.name(remoteNode->name);

		CHECK_RESULT(userContext_->getSyncFileSystemMgr()->rename(path, localNode->name, 
			(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			std::wstring oldPath = SyncConfigure::getInstance(userContext_)->monitorRootPath()
				+ dataBaseMgr_->getRemoteTable()->getPath(remoteNode->id);
			if(lengthPath!=oldPath)
			{
				dataBaseMgr_->getDiffTable()->replaceSubPath(oldPath, lengthPath);
			}
		}

		remoteNode->name = localNode->name;
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->updateRemoteNode(remoteNode));

		return ret;
	}

	int32_t remoteMove(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getLocalId())
		{
			return RT_ERROR;
		}

		LocalNode localNode(new st_LocalNode);
		ret = dataBaseMgr_->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
		if(RT_SQLITE_NOEXIST==ret)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. local node not exist, localId:%I64d", localNode->id);
			return RT_OK;
		}
		if(RT_OK!=ret)
		{
			return ret;
		}

		if(LS_NoActionDelete_Status==(localNode->status&LS_NoActionDelete_Status))
		{
			return RT_OK;
		}

		std::wstring lengthPath = dataBaseMgr_->getLocalTable()->getPath(localNode->id);
		//check hidden
		DWORD dwAttribute = GetFileAttributes(std::wstring(L"\\\\?\\"+lengthPath).c_str());
		if ((INVALID_FILE_ATTRIBUTES!=dwAttribute)&&(FILE_ATTRIBUTE_HIDDEN&dwAttribute))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "hidden path:%s", SD::Utility::String::wstring_to_string(lengthPath).c_str());
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
		}

		//check remote node
		int64_t remoteId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localNode->id, remoteId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. relation not exist, localId:%I64d", localNode->id);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}
		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = remoteId;
		if(RT_OK!=dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. remote node:%I64d not exist", remoteId);
			return RT_SQLITE_NOEXIST;
		}

		//check parent
		int64_t remoteParentId = INVALID_ID;
		if(RT_OK != dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(localNode->parent, remoteParentId))
		{
			LocalStatus localStatus;
			//check no action and filter
			if(dataBaseMgr_->getLocalTable()->isSpecialStatus(localNode->parent, localStatus))
			{
				IdList localIdList;
				AsyncTaskIds runningTaskIds;
				localIdList.push_back(localNode->id);
				if(FILE_TYPE_DIR==localNode->type)
				{
					dataBaseMgr_->getLocalTable()->getChildren(localIdList);
				}
				dataBaseMgr_->getLocalTable()->updateStatus(localIdList, localStatus);
				transTaskTable_->getSyncTask(localIdList, ATT_Upload, runningTaskIds);
				dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localIdList);

				if(INVALID_ID != diffNode->getRemoteId())
				{
					IdList remoteIdList;
					remoteIdList.push_back(diffNode->getRemoteId());
					if(FILE_TYPE_DIR==localNode->type)
					{
						dataBaseMgr_->getRemoteTable()->getChildren(remoteIdList);
					}
					dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList);
					(void)dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList);
					transTaskTable_->getSyncTask(remoteIdList, ATT_Download, runningTaskIds);
					dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteIdList);
					
					Path path = userContext_->getPathMgr()->makePath();
					path.id(remoteNode->id);
					path.parent(remoteNode->parent);
					path.name(remoteNode->name);

					ret = userContext_->getSyncFileSystemMgr()->remove(path,
						(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST);
				}

				if(!runningTaskIds.empty())
				{
					userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
				}

				return RT_CANCEL;
			}

			nextOperNode->key = localNode->parent;
			nextOperNode->keyType = Key_LocalID;

			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "remoteMove. parent:%I64d not exist", localNode->parent);
			return RT_PARENT_NOEXIST_ERROR;
		}
		else
		{
			if(remoteParentId==remoteNode->parent)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteMove cancel.");
				return RT_OK;
			}
		}
		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(remoteParentId);

		//check remote path
		RemoteNodes remoteNodes;
		dataBaseMgr_->getRemoteTable()->getRemoteNodes(remoteParentId, remoteNode->name, remoteNodes);
		if(!remoteNodes.empty())
		{
			return RT_DIFF_CONFILCTPATH;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(remoteNode->id);
		path.parent(remoteNode->parent);
		path.name(remoteNode->name);

		//CHECK_RESULT(userContext_->getSyncFileSystemMgr()->move(path, parent, 
		//	(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			std::wstring oldPath = SyncConfigure::getInstance(userContext_)->monitorRootPath()
				+ dataBaseMgr_->getRemoteTable()->getPath(remoteNode->id);
			if(lengthPath!=oldPath)
			{
				dataBaseMgr_->getDiffTable()->replaceSubPath(oldPath, lengthPath);
			}
		}

		remoteNode->parent = remoteParentId;
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->updateRemoteNode(remoteNode));

		return ret;
	}

	int32_t remoteDelete(DiffNode& diffNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getLocalId())
		{
			return RT_ERROR;
		}

		LocalNode localNode(new st_LocalNode);
		localNode->type = FILE_TYPE_FILE;
		ret = dataBaseMgr_->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
		if (RT_SQLITE_NOEXIST != ret && RT_OK != ret)
		{
			return ret;
		}

		IdList localIdList;
		dataBaseMgr_->getDiffTable()->getDelete(Key_LocalID, localIdList);
		if(localIdList.size()>100)
		{
			return remoteBatDelete(localIdList);
		}
		else
		{
			localIdList.clear();
			localIdList.push_back(diffNode->getLocalId());
		}
		int64_t remoteId = INVALID_ID;
		ret = dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(diffNode->getLocalId(), remoteId);
		IdList remoteIdList;
		if(INVALID_ID!=remoteId)
		{
			remoteIdList.push_back(remoteId);
		}
		if(FILE_TYPE_DIR==localNode->type)
		{
			dataBaseMgr_->getLocalTable()->getChildren(localIdList);
			dataBaseMgr_->getRemoteTable()->getChildren(remoteIdList);
			
			localIdList.pop_front();
			if(!remoteIdList.empty())
			{
				remoteIdList.pop_front();
			}

			//delete children task and diff
			AsyncTaskIds runningTaskIds;
			IdList localMoveList;
			if(!localIdList.empty())
			{
				//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(localIdList, Key_LocalID, runningTaskIds);
				dataBaseMgr_->getDiffTable()->getMove(Key_LocalID, localIdList, localMoveList);
				dataBaseMgr_->getDiffTable()->completeDiffExMove(Key_LocalID, localIdList, localMoveList);
			}
			if(!remoteIdList.empty())
			{
				//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(remoteIdList, Key_RemoteID, runningTaskIds);
				dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteIdList);
			}
			if(!runningTaskIds.empty())
			{
				userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
			}
			//check local child move in
			if(!localMoveList.empty())
			{
				//when move exist, return ready to wait the move
				SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
				return RT_READY;
			}

			//check local child move out
			IdList oldLocalIdList;
			IdList localMoveOutList;
			dataBaseMgr_->getRelationTable()->getExistLocalByRemote(remoteIdList, oldLocalIdList);
			dataBaseMgr_->getDiffTable()->getMove(Key_LocalID, oldLocalIdList, localMoveOutList);
			if(!localMoveOutList.empty())
			{
				//when move exist, return ready to wait the move
				SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
				return RT_READY;
			}

			localIdList.push_front(diffNode->getLocalId());
			if(INVALID_ID!=remoteId)
			{
				remoteIdList.push_front(remoteId);
			}
		}
		else
		{
			IdList idList;
			idList.push_back(diffNode->getLocalId());
			AsyncTaskIds runningTaskIds;
			//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(idList, Key_LocalID, runningTaskIds);
			userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
		}

		//check remote node
		if(RT_OK != ret)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. relation not exist, localId:%I64d", diffNode->getLocalId());
			CHECK_RESULT(dataBaseMgr_->getLocalTable()->deleteNodes(localIdList));
			return RT_OK;
		}
		// ignore remote root, delete should not delete remote root id
		if (ROOT_PARENTID == remoteId)
		{
			return RT_OK;
		}

		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = remoteId;
		if(RT_SQLITE_NOEXIST == dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. remote node:%I64d not exist", remoteId);
			CHECK_RESULT(dataBaseMgr_->getLocalTable()->deleteNodes(localIdList));
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList));
			return RT_OK;
		}

		if(FILE_TYPE_FILE==localNode->type)
		{
			IdList idList;
			idList.push_back(diffNode->getLocalId());
			//if user delete the local file, and then create a same name file, we should not delete the remote file
			//especially for office and etc...
			int64_t localParentId;
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->parent, localParentId));
			std::wstring localPath = dataBaseMgr_->getLocalTable()->getPath(localParentId)+PATH_DELIMITER+remoteNode->name;
			if(SD::Utility::FS::is_exist(localPath))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. local file changed. path:%s",
					SD::Utility::String::wstring_to_string(localPath).c_str());

				// convert path to id
				FILE_DIR_INFO fileDirInfo;
				Path tempPath;
				tempPath.path(localPath);
				int32_t ret = userContext_->getSyncFileSystemMgr()->getProperty(tempPath, fileDirInfo, ADAPTER_FILE_TYPE_LOCAL);
				if(fileDirInfo.id == diffNode->getLocalId())
				{
					SERVICE_ERROR(MODULE_NAME, RT_OK, "remoteDelete. local id no changed. path:%s", 
						SD::Utility::String::wstring_to_string(localPath).c_str());
					return RT_OK;
				}

				// if the remote id related to the local id is exist, should delete the remote node
				// this case is for "move and cover the dest file"
				int64_t remoteCoverId = INVALID_ID;
				ret = dataBaseMgr_->getRelationTable()->getRemoteIdByLocalId(fileDirInfo.id, remoteCoverId);
				if (RT_SQLITE_NOEXIST == ret)
				{
					//delete local trashdelete
					CHECK_RESULT(dataBaseMgr_->getLocalTable()->deleteNodes(idList));
					CHECK_RESULT(dataBaseMgr_->getRelationTable()->deleteByLocalId(idList));

					return RT_OK;
				}
				else if (ret != RT_OK)
				{
					return ret;
				}			
			}
		}
		else
		{
			int64_t localParentId;
			dataBaseMgr_->getRelationTable()->getLocalIdByRemoteId(remoteNode->parent, localParentId);
			std::wstring localPath = dataBaseMgr_->getLocalTable()->getPath(localParentId)+PATH_DELIMITER+remoteNode->name;
			if(SD::Utility::FS::is_exist(localPath))
			{
				FILE_DIR_INFO fileInfo;// = LocalFile::getPropertyByPath(localPath);
				if(fileInfo.id == diffNode->getLocalId())
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. local folder exist. path:%s",
						SD::Utility::String::wstring_to_string(localPath).c_str());
					return RT_OK;
				}
			}		
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(remoteNode->id);
		path.parent(remoteNode->parent);
		path.name(remoteNode->name);

		ret = userContext_->getSyncFileSystemMgr()->remove(path,
			(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST);
		if(HTTP_NOT_FOUND==ret)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. remote node:%I64d not exist", remoteId);
			ret = RT_OK;
		}
		if(RT_OK!=ret)
		{
			return ret;
		}

		if(localIdList.size()==remoteIdList.size())
		{
			CHECK_RESULT(dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList));
		}
		else
		{
			checkReCreate(localIdList, remoteIdList);
		}

		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList));
		CHECK_RESULT(dataBaseMgr_->getLocalTable()->deleteNodes(localIdList));

		return ret;
	}

	int32_t remoteBatDelete(IdList& localIdList)
	{
		int32_t ret = RT_OK;
		IdList remoteIdList;
		dataBaseMgr_->getRelationTable()->getExistRemoteByLocal(localIdList, remoteIdList);
		dataBaseMgr_->getLocalTable()->getChildren(localIdList);
		dataBaseMgr_->getRemoteTable()->getChildren(remoteIdList);

		//delete children task and diff
		AsyncTaskIds runningTaskIds;
		//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(localIdList, Key_LocalID, runningTaskIds);
		//dataBaseMgr_->getTransTaskTable()->getRunningTaskEx(remoteIdList, Key_RemoteID, runningTaskIds);
		if(!runningTaskIds.empty())
		{
			userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
		}

		IdList deleteIdList;
		deleteIdList = remoteIdList;
		SERVICE_INFO(MODULE_NAME, RT_OK, "delete remoteNode, id:%s", Sync::getInStr(deleteIdList).c_str());
		//When the parent is deleted, ignore the children's deleted
		dataBaseMgr_->getRemoteTable()->filterChildren(deleteIdList);
		SERVICE_INFO(MODULE_NAME, RT_OK, "delete remoteNode after filter, id:%s", Sync::getInStr(deleteIdList).c_str());
		for(IdList::const_iterator it = deleteIdList.begin(); it!=deleteIdList.end(); ++it)
		{
			RemoteNode remoteNode(new st_RemoteNode);
			remoteNode->id = *it;
			if(RT_SQLITE_NOEXIST == dataBaseMgr_->getRemoteTable()->getRemoteNode(remoteNode))
			{
				continue;
			}
			Path path = userContext_->getPathMgr()->makePath();
			path.id(remoteNode->id);
			path.parent(remoteNode->parent);
			path.name(remoteNode->name);
			ret = userContext_->getSyncFileSystemMgr()->remove(path,
				(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST);
			if(HTTP_NOT_FOUND==ret)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteDelete. remote node:%I64d not exist", *it);
				ret = RT_OK;
			}
			if(RT_OK!=ret)
			{
				return ret;
			}
		}

		CHECK_RESULT(dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList));
		CHECK_RESULT(dataBaseMgr_->getRemoteTable()->deleteNodes(remoteIdList));
		CHECK_RESULT(dataBaseMgr_->getLocalTable()->deleteNodes(localIdList));
		dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localIdList);
		dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteIdList);

		return RT_CANCEL;;
	}

	void megerDir(const int64_t& remoteId, const int64_t& localId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "megerDir, remoteId:%I64d, localId:%I64d", remoteId, localId);
		(void)dataBaseMgr_->getRelationTable()->addRelation(remoteId, localId);
		dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteId);
		dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localId);
	}

	void megerFile(const int64_t& remoteId, const int64_t& localId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "megerFile, remoteId:%I64d, localId:%I64d", remoteId, localId);
		//Retain local created operation
		(void)dataBaseMgr_->getRelationTable()->addRelation(remoteId, localId);
		dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteId);
		dataBaseMgr_->getDiffTable()->updateOper(Key_LocalID, localId, OT_Created, OT_Edited);
	}

	void changeOpToCreate(KeyType keyType, int64_t nodeId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "changeOpToCreate, key:%I64d, keyType:%d", nodeId, keyType);
		dataBaseMgr_->getDiffTable()->completeDiff(keyType, nodeId);
		OperNode operNode(new st_OperNode);
		operNode->keyType = keyType;
		operNode->key = nodeId;
		operNode->oper = OT_Created;
		operNode->size = 0;
		dataBaseMgr_->getDiffTable()->addOper(operNode);
	}

	void restoreNoActionDelete(const int64_t& id, FILE_TYPE type)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "restoreNoActionDelete, id:%I64d, type:%d", id, type);
		IdList idList;
		idList.push_back(id);
		if(FILE_TYPE_DIR == type)
		{
			dataBaseMgr_->getLocalTable()->getChildren(idList);
		}
		dataBaseMgr_->getLocalTable()->updateStatus(idList, LS_Normal);
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
		dataBaseMgr_->getDiffTable()->addOperList(operNodes);
	}

	void restoreFilter(const int64_t& id, FILE_TYPE type)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "restoreFilter, id:%I64d, type:%d", id, type);
		IdList idList;
		idList.push_back(id);
		if(FILE_TYPE_DIR == type)
		{
			dataBaseMgr_->getLocalTable()->getChildren(idList);
		}
		dataBaseMgr_->getLocalTable()->updateStatus(idList, LS_Normal);
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
		dataBaseMgr_->getDiffTable()->addOperList(operNodes);
	}

	void localFilter(const int64_t& id, const int32_t& type)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "localFilter, id:%I64d, type:%d", id, type);
		IdList idList;
		idList.push_back(id);
		if(FILE_TYPE_DIR == type)
		{
			dataBaseMgr_->getLocalTable()->getChildren(idList);
		}
		dataBaseMgr_->getLocalTable()->updateStatus(idList, LS_Filter);
		(void)dataBaseMgr_->getRelationTable()->deleteByLocalId(idList);
		//delete oper
		dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, idList);
	}

	void checkReCreate(const IdList& localIdList, const IdList& remoteIdList)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "checkReCreate, local size:%d, remote size:%d", localIdList.size(), remoteIdList.size());
		if(localIdList.size() < remoteIdList.size())
		{
			(void)dataBaseMgr_->getRelationTable()->deleteByLocalId(localIdList);
			//get local reCreate
			IdList localExistIds;
			dataBaseMgr_->getRelationTable()->getExistLocalByRemote(remoteIdList, localExistIds);
			(void)dataBaseMgr_->getRelationTable()->deleteByLocalId(localExistIds);
			dataBaseMgr_->getDiffTable()->completeDiff(Key_LocalID, localExistIds);
			OperNodes operNodes;
			for(IdList::const_iterator it = localExistIds.begin(); it != localExistIds.end(); ++it)
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_LocalID;
				operNode->oper = OT_Created;
				operNode->key = *it;
				operNode->size = 0;
				operNodes.push_back(operNode);
			}
			dataBaseMgr_->getDiffTable()->addOperList(operNodes);
		}
		else
		{
			dataBaseMgr_->getRelationTable()->deleteByRemoteId(remoteIdList);
			//get remote reCreate
			IdList remoteExistIds;
			dataBaseMgr_->getRelationTable()->getExistRemoteByLocal(localIdList, remoteExistIds);
			dataBaseMgr_->getRelationTable()->deleteByRemoteId(remoteExistIds);
			dataBaseMgr_->getDiffTable()->completeDiff(Key_RemoteID, remoteExistIds);
			OperNodes operNodes;
			for(IdList::const_iterator it = remoteExistIds.begin(); it != remoteExistIds.end(); ++it)
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_RemoteID;
				operNode->oper = OT_Created;
				operNode->key = *it;
				operNode->size = 0;
				operNodes.push_back(operNode);			
			}
			dataBaseMgr_->getDiffTable()->addOperList(operNodes);
		}
	}

	bool isUploadFilter(const std::wstring& path)
	{
		if (path.empty())
		{
			return false;
		}
		std::wstring uploadFilterExts = SyncConfigure::getInstance(userContext_)->uploadFilterStr();
		if (uploadFilterExts.empty())
		{
			return false;
		}
		std::wstring extName = SD::Utility::FS::get_file_name(path);
		extName = SD::Utility::String::to_lower(extName);
		uploadFilterExts = SD::Utility::String::to_lower(uploadFilterExts);
		return (std::wstring::npos != uploadFilterExts.find(extName));
	}

private:
	UserContext* userContext_;
	DataBaseMgr* dataBaseMgr_;
	std::auto_ptr<TransTaskTable> transTaskTable_;
};

std::auto_ptr<SyncAction> SyncAction::create(UserContext* userContext)
{
	return std::auto_ptr<SyncAction>(new SyncActionImpl(userContext));
}