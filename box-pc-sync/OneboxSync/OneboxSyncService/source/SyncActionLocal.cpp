#include <fstream>
#include <boost/thread.hpp>
#include "SyncActionLocal.h"
#include "DataBaseMgr.h"
#include "FilterMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ConfigureMgr.h"
#include "AsyncTaskMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncActionLocal")
#endif

class SyncActionLocalImpl : public SyncActionLocal
{
public:
	SyncActionLocalImpl(UserContext* userContext)
		:userContext_(userContext)
	{
	}

	virtual ~SyncActionLocalImpl(void)
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
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "skip remote action. diffNode:[%I64d]<->[%I64d], command:%s",
					diffNode->getLocalId(), diffNode->getRemoteId(), it->get()->toString().c_str());
				ret = RT_OK;
				continue;
			}
			
			if(ActionType_Remote == (*it)->actionType)
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
	int32_t remoteCreate(DiffNode& diffNode, OperNode& nextOperNode)
	{
		int32_t ret = RT_OK;

		if(INVALID_ID == diffNode->getLocalId())
		{
			return RT_ERROR;
		}

		LocalNode localNode(new st_LocalNode);
		ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
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
			int64_t remoteId = INVALID_ID;
			if(RT_OK==userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(diffNode->getLocalId(), remoteId))
			{
				if(userContext_->getDataBaseMgr()->getRemoteTable()->isExist(remoteId))
				{
					//remote node has been created
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "remote node has been created. localId:%I64d", diffNode->getLocalId());
					return RT_OK;
				}
				else
				{
					userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(diffNode->getLocalId());
				}
			}
		}

		//check filter char
		if(userContext_->getFilterMgr()->isStaticFilter(localNode->name))
		{
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
			//return RT_DIFF_FILTER;
		}

		//check path length
		Path localPath = userContext_->getPathMgr()->makePath();
		localPath.id(localNode->id);
		localPath.parent(localNode->parent);
		localPath.name(localNode->name);
		std::wstring parentPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(localNode->parent);
		localPath.path(parentPath + PATH_DELIMITER + localNode->name);
		if(userContext_->getFilterMgr()->isMaxPath(localPath.path()))
		{
			return RT_DIFF_MAXPATH;
		}

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
			//return RT_DIFF_HIDDEN;
		}

		//check is KIA files 
		if(userContext_->getFilterMgr()->isKiaFilter(localPath.path()))
		{
			return RT_DIFF_KIA;
		}
		
		//check upaload files 
		if(userContext_->getFilterMgr()->isUploadFilter(localPath.path()))
		{
			if(userContext_->getDataBaseMgr()->getRelationTable()->localIdIsExist(diffNode->getLocalId())
				&&userContext_->getDataBaseMgr()->getUploadTable()->isFilter(diffNode->getLocalId()))
			{
				return RT_OK;
			}
		}

		//check parent
		int64_t remoteParentId = INVALID_ID;
		if(RT_OK != userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localNode->parent, remoteParentId))
		{
			LocalStatus localStatus;
			//check no action and filter
			if(userContext_->getDataBaseMgr()->getLocalTable()->isSpecialStatus(localNode->parent, localStatus))
			{
				IdList deleteList;
				deleteList.push_back(localNode->id);
				if(FILE_TYPE_DIR==localNode->type)
				{
					userContext_->getDataBaseMgr()->getLocalTable()->getChildren(deleteList);
				}
				userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(deleteList, localStatus);
				return RT_OK;
			}

			if(!userContext_->getDataBaseMgr()->getDiffTable()->isInDiff(Key_LocalID, localNode->parent))
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_LocalID;
				operNode->key = localNode->parent;
				operNode->oper = OT_Created;
				operNode->size = 0;
				userContext_->getDataBaseMgr()->getDiffTable()->addOper(operNode);

				//delete children task and diff
				IdList localIdList;
				localIdList.push_back(localNode->parent);
				userContext_->getDataBaseMgr()->getLocalTable()->getChildren(localIdList);
				AsyncTaskIds runningTaskIds;
				userContext_->getDataBaseMgr()->getTransTaskTable()->getRunningTaskEx(localIdList, Key_LocalID, runningTaskIds);		
				if(!runningTaskIds.empty())
				{
					userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
				}
			}
			nextOperNode->key = localNode->parent;
			nextOperNode->keyType = Key_LocalID;
			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "parent:%I64d not exist", localNode->parent);
			//if parent is not RT_OK, update children to RT_PARENT_NOEXIST_ERROR
			if(RT_OK!=userContext_->getDataBaseMgr()->getDiffTable()->getErrorCode(Key_LocalID, localNode->parent))
			{
				IdList idList;
				idList.push_back(localNode->parent);
				userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);
				idList.pop_front();
				userContext_->getDataBaseMgr()->getDiffTable()->updateErrorCode(Key_LocalID, idList, RT_PARENT_NOEXIST_ERROR);
			}
			return RT_CANCEL;
		}

		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(remoteParentId);
		parent.path(parentPath);

		//check remote path
		RemoteNodes remoteNodes;
		CHECK_RESULT(userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNodes(remoteParentId, localNode->name, remoteNodes));
		if(!remoteNodes.empty())
		{
			for(RemoteNodes::const_iterator it = remoteNodes.begin();
				it != remoteNodes.end(); ++it)
			{
				RemoteNode remoteNode = *it;
				int64_t localId = INVALID_ID;
				if(RT_SQLITE_NOEXIST == userContext_->getDataBaseMgr()->getRelationTable()->getLocalIdByRemoteId(remoteNode->id, localId))
				{
					if((ROOT_PARENTID==remoteNode->parent)&&(NO_SET_SYNC_STATUS ==remoteNode->status))
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
						megerFile(remoteNode->id, localNode->id);
						//add upload task
						CHECK_RESULT(userContext_->getAsyncTaskMgr()->upload(localPath, parent));
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
					if(!userContext_->getDataBaseMgr()->getLocalTable()->isExist(localId))
					{
						//清除删除场景下异步传输任务可能添加的脏数据
						userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(localId);
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
			CHECK_RESULT(userContext_->getAsyncTaskMgr()->upload(localPath, parent));
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

			CHECK_RESULT(userContext_->getDataBaseMgr()->getRemoteTable()->addRemoteNode(remoteNode));
			CHECK_RESULT(userContext_->getDataBaseMgr()->getRelationTable()->addRelation(remoteNode->id, localNode->id));

			if(RT_OK!=userContext_->getDataBaseMgr()->getDiffTable()->getErrorCode(Key_LocalID, localNode->id))
			{
				IdList idList;
				idList.push_back(localNode->id);
				userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);
				idList.pop_front();
				userContext_->getDataBaseMgr()->getDiffTable()->updateErrorCode(Key_LocalID, idList, RT_OK);
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
		ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
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

		std::wstring parentPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(localNode->parent);
		//check filter char
		if(userContext_->getFilterMgr()->isStaticFilter(localNode->name))
		{
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
		}

		//check path length
		std::wstring lengthPath = parentPath + PATH_DELIMITER + localNode->name;
		if(userContext_->getFilterMgr()->isMaxPath(lengthPath))
		{
			return RT_DIFF_MAXPATH;
		}
		
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
			//return RT_DIFF_HIDDEN;
		}

		if(LS_Filter==(localNode->status&LS_Filter))
		{
			//restore the status to normal
			restoreFilter(localNode->id, (FILE_TYPE)localNode->type);
			return RT_OK;
		}
		
		//check remote node
		int64_t remoteId = INVALID_ID;
		if(RT_OK != userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localNode->id, remoteId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. relation not exist, localId:%I64d", localNode->id);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}
		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = remoteId;
		ret = userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode);
		if(RT_OK==ret)
		{
			if(localNode->name==remoteNode->name)
			{
				return RT_OK;
			}
		}
		else if(RT_SQLITE_NOEXIST==ret)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteRename. remote node:%I64d not exist", remoteId);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}

		//check remote path
		RemoteNodes remoteNodes;
		userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNodes(remoteNode->parent, localNode->name, remoteNodes);
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

					if((!userContext_->getDataBaseMgr()->getRelationTable()->remoteIdIsExist(newRemoteId))
						&&(!userContext_->getDataBaseMgr()->getDiffTable()->isInDiff(Key_RemoteID, newRemoteId)))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteRename. remote node:%I64d change remoteId to %I64d", 
							remoteId, newRemoteId);
						userContext_->getDataBaseMgr()->getRelationTable()->deleteByRemoteId(remoteId);
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
			std::wstring oldPath = userContext_->getConfigureMgr()->getConfigure()->monitorRootPath()
				+ userContext_->getDataBaseMgr()->getRemoteTable()->getPath(remoteNode->id);
			if(lengthPath!=oldPath)
			{
				userContext_->getDataBaseMgr()->getDiffTable()->replaceSubPath(oldPath, lengthPath);
			}
		}

		remoteNode->name = localNode->name;
		CHECK_RESULT(userContext_->getDataBaseMgr()->getRemoteTable()->updateRemoteNode(remoteNode));

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
		ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
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

		//check path length
		std::wstring lengthPath = userContext_->getDataBaseMgr()->getLocalTable()->getPath(localNode->id);
		if(userContext_->getFilterMgr()->isMaxPath(lengthPath))
		{
			return RT_DIFF_MAXPATH;
		}
		
		//check hidden
		DWORD dwAttribute = GetFileAttributes(std::wstring(L"\\\\?\\"+lengthPath).c_str());
		if((INVALID_FILE_ATTRIBUTES!=dwAttribute)&&(FILE_ATTRIBUTE_HIDDEN&dwAttribute))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "hidden path:%s", SD::Utility::String::wstring_to_string(lengthPath).c_str());
			localFilter(localNode->id, localNode->type);
			return RT_CANCEL;
			//return RT_DIFF_HIDDEN;
		}
		
		//check remote node
		int64_t remoteId = INVALID_ID;
		if(RT_OK != userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localNode->id, remoteId))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. relation not exist, localId:%I64d", localNode->id);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}
		RemoteNode remoteNode(new st_RemoteNode);
		remoteNode->id = remoteId;
		if(RT_OK!=userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNode(remoteNode))
		{
			SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. remote node:%I64d not exist", remoteId);
			changeOpToCreate(Key_LocalID, localNode->id);
			return RT_SQLITE_NOEXIST;
		}

		//check parent
		int64_t remoteParentId = INVALID_ID;
		if(RT_OK != userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(localNode->parent, remoteParentId))
		{
			LocalStatus localStatus;
			//check no action and filter
			if(userContext_->getDataBaseMgr()->getLocalTable()->isSpecialStatus(localNode->parent, localStatus))
			{
				IdList deleteList;
				deleteList.push_back(localNode->id);
				if(FILE_TYPE_DIR==localNode->type)
				{
					userContext_->getDataBaseMgr()->getLocalTable()->getChildren(deleteList);
				}
				userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(deleteList, localStatus);
				return RT_OK;
			}

			if(!userContext_->getDataBaseMgr()->getDiffTable()->isInDiff(Key_LocalID, localNode->parent))
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_LocalID;
				operNode->key = localNode->parent;
				operNode->oper = OT_Created;
				operNode->size = 0;
				userContext_->getDataBaseMgr()->getDiffTable()->addOper(operNode);
			}
			nextOperNode->key = localNode->parent;
			nextOperNode->keyType = Key_LocalID;
			SERVICE_DEBUG(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "parent:%I64d not exist", localNode->parent);
			diffNode->refreshDiff(Diff_Failed, RT_PARENT_NOEXIST_ERROR);
			return RT_CANCEL;
		}

		if(remoteParentId==remoteNode->parent)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "remoteMove cancel.");
			return RT_OK;
		}

		Path parent = userContext_->getPathMgr()->makePath();
		parent.id(remoteParentId);

		//check remote path
		RemoteNodes remoteNodes;
		userContext_->getDataBaseMgr()->getRemoteTable()->getRemoteNodes(remoteParentId, remoteNode->name, remoteNodes);
		if(!remoteNodes.empty())
		{
			/*
			if(1==remoteNodes.size())
			{
				if((FILE_TYPE_FILE == remoteNode->type)&&(FILE_TYPE_FILE == remoteNodes.begin()->get()->type))
				{
					int64_t newRemoteId = remoteNodes.begin()->get()->id;
					if(!userContext_->getDataBaseMgr()->getRelationTable()->remoteIdIsExist(newRemoteId))
					{
						SERVICE_DEBUG(MODULE_NAME, RT_SQLITE_NOEXIST, "remoteMove. remote node:%I64d change remoteId to %I64d", 
							remoteId, newRemoteId);
						userContext_->getDataBaseMgr()->getRelationTable()->deleteByRemoteId(remoteId);
						changeOpToCreate(Key_LocalID, localNode->id);
					}
				}
			}
			*/
			return RT_DIFF_CONFILCTPATH;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(remoteNode->id);
		path.parent(remoteNode->parent);
		path.name(remoteNode->name);

		CHECK_RESULT(userContext_->getSyncFileSystemMgr()->move(path, parent, 
			(FILE_TYPE_DIR == remoteNode->type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST));

		if(FILE_TYPE_DIR == remoteNode->type)
		{
			std::wstring oldPath = userContext_->getConfigureMgr()->getConfigure()->monitorRootPath()
				+ userContext_->getDataBaseMgr()->getRemoteTable()->getPath(remoteNode->id);
			if(lengthPath!=oldPath)
			{
				userContext_->getDataBaseMgr()->getDiffTable()->replaceSubPath(oldPath, lengthPath);
			}
		}

		remoteNode->parent = remoteParentId;
		CHECK_RESULT(userContext_->getDataBaseMgr()->getRemoteTable()->updateRemoteNode(remoteNode));

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
		ret = userContext_->getDataBaseMgr()->getLocalTable()->getNode(diffNode->getLocalId(), localNode);
		if (RT_SQLITE_NOEXIST != ret && RT_OK != ret)
		{
			return ret;
		}
		//delete local trashdelete
		IdList idList;
		userContext_->getDataBaseMgr()->getDiffTable()->getDelete(Key_LocalID, idList);
		if(idList.size()>100)
		{
			return remoteBatDelete(idList);
		}
		else
		{
			idList.clear();
			idList.push_back(diffNode->getLocalId());
		}

		if(FILE_TYPE_DIR==localNode->type)
		{
			CHECK_RESULT(userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList));
		}
		AsyncTaskIds runningTaskIds;
		userContext_->getDataBaseMgr()->getTransTaskTable()->getRunningTaskEx(idList, Key_LocalID, runningTaskIds);
		userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
		
		//child diff not contain self
		idList.pop_front();
		IdList moveList;
		userContext_->getDataBaseMgr()->getDiffTable()->getMove(Key_LocalID, idList, moveList);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiffExMove(Key_LocalID, idList, moveList);
		//check local child move in
		if(!moveList.empty())
		{
			//when move exist, return ready to wait the move
			SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
			return RT_READY;
		}
		//check local child move out
		int64_t remoteId = INVALID_ID;
		if(RT_OK == userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(diffNode->getLocalId(), remoteId))
		{
			IdList remoteIdList;
			remoteIdList.push_back(remoteId);
			userContext_->getDataBaseMgr()->getRemoteTable()->getChildren(remoteIdList);
			//child diff not contain self
			remoteIdList.pop_front();
			IdList oldLocalIdList;
			IdList localMoveOutList;
			userContext_->getDataBaseMgr()->getRelationTable()->getExistLocalByRemote(remoteIdList, oldLocalIdList);
			userContext_->getDataBaseMgr()->getDiffTable()->getMove(Key_LocalID, oldLocalIdList, localMoveOutList);
			if(!localMoveOutList.empty())
			{
				//when move exist, return ready to wait the move
				SERVICE_DEBUG(MODULE_NAME, RT_READY, "parent delete + child move");
				return RT_READY;
			}
		}

		idList.push_front(diffNode->getLocalId());
		CHECK_RESULT(userContext_->getDataBaseMgr()->getLocalTable()->deleteNodes(idList));
		CHECK_RESULT(userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(idList));

		return ret;
	}

	int32_t remoteBatDelete(IdList& idList)
	{
		int32_t ret = RT_OK;

		//delete local trashdelete
		CHECK_RESULT(userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList));
		AsyncTaskIds runningTaskIds;
		userContext_->getDataBaseMgr()->getTransTaskTable()->getRunningTaskEx(idList, Key_LocalID, runningTaskIds);
		userContext_->getAsyncTaskMgr()->delTask(runningTaskIds);
		CHECK_RESULT(userContext_->getDataBaseMgr()->getLocalTable()->deleteNodes(idList));
		CHECK_RESULT(userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(idList));

		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, idList);
		return RT_CANCEL;
	}

	void megerDir(const int64_t& remoteId, const int64_t& localId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "megerDir, remoteId:%I64d, localId:%I64d", remoteId, localId);
		userContext_->getDataBaseMgr()->getRelationTable()->addRelation(remoteId, localId);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_RemoteID, remoteId);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, localId);
	}

	void megerFile(const int64_t& remoteId, const int64_t& localId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "megerFile, remoteId:%I64d, localId:%I64d", remoteId, localId);
		//Retain local created operation
		userContext_->getDataBaseMgr()->getRelationTable()->addRelation(remoteId, localId);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_RemoteID, remoteId);
		userContext_->getDataBaseMgr()->getDiffTable()->updateOper(Key_LocalID, localId, OT_Created, OT_Edited);
	}

	void changeOpToCreate(KeyType keyType, int64_t nodeId)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "changeOpToCreate, key:%I64d, keyType:%d", nodeId, keyType);
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(keyType, nodeId);
		OperNode operNode(new st_OperNode);
		operNode->keyType = keyType;
		operNode->key = nodeId;
		operNode->oper = OT_Created;
		operNode->size = 0;
		userContext_->getDataBaseMgr()->getDiffTable()->addOper(operNode);
	}

	void localFilter(const int64_t& id, const int32_t& type)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "localFilter, id:%I64d, type:%d", id, type);
		IdList idList;
		idList.push_back(id);
		if(FILE_TYPE_DIR == type)
		{
			userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);
		}
		userContext_->getDataBaseMgr()->getLocalTable()->updateStatus(idList, LS_Filter);
		userContext_->getDataBaseMgr()->getRelationTable()->deleteByLocalId(idList);
		//delete oper
		userContext_->getDataBaseMgr()->getDiffTable()->completeDiff(Key_LocalID, idList);
	}

	void restoreFilter(const int64_t& id, FILE_TYPE type)
	{
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "restoreFilter, id:%I64d, type:%d", id, type);
		IdList idList;
		idList.push_back(id);
		if(FILE_TYPE_DIR == type)
		{
			userContext_->getDataBaseMgr()->getLocalTable()->getChildren(idList);
		}
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
private:
	UserContext* userContext_;
};

std::auto_ptr<SyncAction> SyncActionLocal::create(UserContext* userContext)
{
	return std::auto_ptr<SyncAction>(new SyncActionLocalImpl(userContext));
}