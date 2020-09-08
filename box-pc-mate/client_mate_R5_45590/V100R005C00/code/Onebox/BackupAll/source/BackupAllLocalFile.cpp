#include "BackupAllLocalFile.h"
#include "Utility.h"
#include "SyncFileSystemMgr.h"
#include "BackupAllDbMgr.h"
#include "BackupAllTransTask.h"
#include "PathMgr.h"
#include "NotifyMgr.h"
#include "BackupAllUtility.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllLocalFile")
#endif

using namespace SD::Utility;

class BackupAllLocalFileImpl : public BackupAllLocalFile
{
public:
	BackupAllLocalFileImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual ~BackupAllLocalFileImpl()
	{
	}

	virtual void backup(BackupAllLocalDb* pLocalDb, BATaskLocalNode& node)
	{
		if((FILE_TYPE_DIR==node.baseInfo.type)
			&&((node.baseInfo.opType&(BAO_Rename|BAO_Move))>0))
		{
			pLocalDb->updatePath();
		}
		while(pLocalDb->getUploadingCnt()>100)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(100));
		}
		if(execute(pLocalDb, node))
		{
			if (RT_FILE_EXIST_ERROR == node.errorCode)
			{
				//尝试删除rename、move操作，转为创建或上传合并
				if((node.baseInfo.opType&(BAO_Rename|BAO_Move))>0)
				{
					node.baseInfo.opType = FILE_TYPE_DIR==node.baseInfo.type?BAO_Create:BAO_Upload;
					HSLOG_TRACE(MODULE_NAME, node.errorCode, "change op to %d", node.baseInfo.opType);
					execute(pLocalDb, node);
				}
			}
			pLocalDb->updateExInfo(node);
		}
	}

	virtual int32_t create(const int64_t& parentId, BATaskLocalNode& node)
	{
		Path parentPath = userContext_->getPathMgr()->makePath();
		parentPath.id(parentId);
		std::wstring fileName = FS::get_file_name(node.baseInfo.path);
		FOLDER_EXTRA_TYPE extType = FOLDER_EXTRA_TYPE_NONE;
		if(fileName.empty())
		{
			//备份根节点路径即文件名
			fileName = node.baseInfo.path;
			if(0 == parentId)
			{
				extType = FOLDER_EXTRA_TYPE_COMPUTER;
			}
			else
			{
				extType = FOLDER_EXTRA_TYPE_DISK;
				//备份盘符只要盘符号
				std::wstring::size_type nPos = fileName.find(L":");
				if(std::wstring::npos==nPos)
				{
					return RT_INVALID_PARAM;
				}
				fileName = node.baseInfo.path.substr(0, nPos);
			}			
		}
		FILE_DIR_INFO remoteInfo;

		int32_t ret = userContext_->getSyncFileSystemMgr()->create(parentPath, fileName, remoteInfo, ADAPTER_FOLDER_TYPE_REST, extType, true);
		
		if(HTTP_NOT_FOUND == ret)
		{
			ret = RT_PARENT_NOEXIST_ERROR;
		}

		node.errorCode = ret;
		node.remoteId = remoteInfo.id;
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_REFRESH_UI, 
			SD::Utility::String::type_to_string<std::wstring>(userContext_->id.type), 
			SD::Utility::String::type_to_string<std::wstring>(userContext_->id.id), 
			SD::Utility::String::type_to_string<std::wstring>(parentId)));
		return ret;	
	}

	virtual int32_t buildPathFromRoot(const std::wstring& path, int64_t& localParent)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("buildPathFromRoot curPath:%s", 
			SD::Utility::String::wstring_to_string(path).c_str()));
		int32_t ret = RT_OK;
		BackupAllLocalDb* pLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(path);
		//磁盘节点作为根节点，依次处理
		localParent = VOLUME_ROOTID;
		std::wstring parentPath = path.substr(0,2);
		int64_t remoteParent = pLocalDb->getRemoteIdById(localParent);
		if(-1==remoteParent)
		{
			BATaskLocalNode node;
			node.baseInfo.path = parentPath;
			node.baseInfo.localId = VOLUME_ROOTID;
			node.baseInfo.localParent = 0;
			node.baseInfo.type = FILE_TYPE_DIR;
			//当前路径非磁盘路径，则磁盘节点为过滤节点
			if(path.length()>3)
			{
				node.baseInfo.opType = node.baseInfo.opType|BAO_Filter;
			}
			BATaskNode baTaskNode;
			(void)BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getNode(baTaskNode);
			if(RT_OK!=create(baTaskNode.remoteId, node))
			{
				return RT_ERROR;
			}
			pLocalDb->addRootNode(node);
			remoteParent = node.remoteId;
		}

		BATaskLocalNode node;
		while(true)
		{
			std::wstring::size_type nPos = path.find(L"\\", parentPath.length() + 1);
			if (std::wstring::npos == nPos)
			{
				return RT_OK;
			}
			parentPath = path.substr(0, nPos);
			int64_t localId = BackupAll::getIdByPath(userContext_, parentPath);
			if(-1==localId)
			{
				return RT_ERROR;
			}

			ret = pLocalDb->getNodeById(localId, node);
			if(RT_SQLITE_NOEXIST == ret)
			{
				//添加节点
				node.baseInfo.path = parentPath;
				node.baseInfo.localId = localId;
				node.baseInfo.localParent = localParent;
				node.baseInfo.type = FILE_TYPE_DIR;
				node.baseInfo.opType = BAO_Filter;
				pLocalDb->addNode(node.baseInfo);
				
				//创建
				(void)create(remoteParent, node);
				pLocalDb->updateExInfo(node);
				remoteParent = node.remoteId;
				if(-1==remoteParent)
				{
					return RT_ERROR;
				}
			}
			else if(RT_OK == ret)
			{
				bool isOpChange = false;
				//移动或重命名
				if(localParent!=node.baseInfo.localParent)
				{
					node.baseInfo.localParent = localParent;
					if(-1!=node.remoteId)
					{
						node.baseInfo.opType = node.baseInfo.opType|BAO_Move;
						isOpChange = true;
					}
				}
				if(SD::Utility::FS::get_file_name(parentPath)!=SD::Utility::FS::get_file_name(node.baseInfo.path))
				{
					node.baseInfo.path = parentPath;
					if(-1!=node.remoteId)
					{
						node.baseInfo.opType = node.baseInfo.opType|BAO_Rename;
						isOpChange = true;
					}
				}
				if(isOpChange)
				{
					pLocalDb->updateNode(node.baseInfo);
				}
				backup(pLocalDb, node);
				remoteParent = node.remoteId;
				if(-1==remoteParent)
				{
					return RT_ERROR;
				}
			}
			else
			{
				return ret;
			}
			localParent = localId;
		}
	}

private:
	bool execute(BackupAllLocalDb* pLocalDb, BATaskLocalNode& node)
	{
		bool isNeedUpdate = false;

		if(node.baseInfo.opType&BAO_CheckRemote)
		{
			if(checkRemote(node))
			{
				node.baseInfo.opType = node.baseInfo.opType&(~BAO_CheckRemote);
			}
			else
			{
				HSLOG_TRACE(MODULE_NAME, node.errorCode, "checkRemote failed, new op:%d", node.baseInfo.opType);
				if(node.baseInfo.opType&BAO_Upload)
				{
					pLocalDb->changeSubFilesOp(node.baseInfo.localParent);
					return isNeedUpdate;
				}
			}
			isNeedUpdate = true;
		}

		if(node.baseInfo.opType&BAO_Rename)
		{
			if(-1==node.remoteId)
			{
				node.baseInfo.opType = FILE_TYPE_DIR==node.baseInfo.type?BAO_Create:BAO_Upload;
			}
			//重命名
			else if(RT_OK==rename(node))
			{
				node.baseInfo.opType = node.baseInfo.opType&(~BAO_Rename);
			}
			isNeedUpdate = true;
			HSLOG_TRACE(MODULE_NAME, node.errorCode, "rename:%s", String::wstring_to_string(node.baseInfo.path).c_str());
		}

		if(0==(node.baseInfo.opType&(BAO_Create|BAO_Move|BAO_Upload)))
		{
			return isNeedUpdate;
		}
		if(-1==node.baseInfo.localParent)
		{
			node.errorCode = buildPathFromRoot(node.baseInfo.path, node.baseInfo.localParent);
			if(RT_OK==node.errorCode)
			{
				pLocalDb->updateNode(node.baseInfo);
			}
			else
			{
				node.baseInfo.localParent = -1;
				isNeedUpdate = true;
				HSLOG_TRACE(MODULE_NAME, node.errorCode, "create:%s", String::wstring_to_string(node.baseInfo.path).c_str());
				return isNeedUpdate;
			}
		}

		BATaskLocalNode parentNode;		
		if(RT_SQLITE_NOEXIST==pLocalDb->getNodeById(node.baseInfo.localParent, parentNode))
		{
			isNeedUpdate = true;
			node.errorCode = RT_ERROR;
			HSLOG_TRACE(MODULE_NAME, node.errorCode, "parent not exist:%s", String::wstring_to_string(node.baseInfo.path).c_str());
			return isNeedUpdate;
		}
		if(-1==parentNode.remoteId)
		{
			node.errorCode = parentNode.errorCode==RT_DIFF_FILTER?RT_DIFF_FILTER:RT_PARENT_NOEXIST_ERROR;
			pLocalDb->updateParentError(node.baseInfo.localParent, node.errorCode);
			return isNeedUpdate;
		}
		if(node.baseInfo.opType&BAO_Create)
		{
			//创建文件夹不可能与其他操作叠加
			if(RT_OK==create(parentNode.remoteId, node))
			{
				node.baseInfo.opType = node.baseInfo.opType&(~BAO_Create);
			}
			isNeedUpdate = true;
			HSLOG_TRACE(MODULE_NAME, node.errorCode, "create:%s", String::wstring_to_string(node.baseInfo.path).c_str());
			return isNeedUpdate;
		}

		if(node.baseInfo.opType&BAO_Move)
		{
			if(-1==node.remoteId)
			{
				node.baseInfo.opType = FILE_TYPE_DIR==node.baseInfo.type?BAO_Create:BAO_Upload;
			}
			//移动
			else if(RT_OK==move(parentNode.remoteId, node))
			{
				node.baseInfo.opType = node.baseInfo.opType&(~BAO_Move);
			}
			isNeedUpdate = true;
			HSLOG_TRACE(MODULE_NAME, node.errorCode, "move:%s", String::wstring_to_string(node.baseInfo.path).c_str());
		}
		if(node.baseInfo.opType&BAO_Upload)
		{
			//批量添加子文件的上传
			std::map<std::wstring, int64_t> subFiles;
			pLocalDb->getSubFiles(parentNode.baseInfo.localId, subFiles);
			if(subFiles.empty())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "upload failed, path:%s", String::wstring_to_string(node.baseInfo.path).c_str());
				return isNeedUpdate;
			}
			//上传
			int32_t ret = BackupAllTransTask::getInstance(userContext_)->uploadSubFiles(parentNode, subFiles);
			if(RT_OK == ret)
			{
				pLocalDb->updateSubFilesOp(parentNode.baseInfo.localId, subFiles);
			}
			HSLOG_TRACE(MODULE_NAME, ret, "upload subFiles:%s", String::wstring_to_string(parentNode.baseInfo.path).c_str());
		}
		return isNeedUpdate;
	}

	int32_t rename(BATaskLocalNode& node)
	{
		Path path = userContext_->getPathMgr()->makePath();
		path.id(node.remoteId);
		int32_t ret = RT_OK;
		if(FILE_TYPE_DIR==node.baseInfo.type)
		{
			ret = userContext_->getSyncFileSystemMgr()->rename(path, FS::get_file_name(node.baseInfo.path),ADAPTER_FOLDER_TYPE_REST);
		}
		else
		{
			ret = userContext_->getSyncFileSystemMgr()->rename(path, node.baseInfo.path, ADAPTER_FILE_TYPE_REST);
		}
		node.errorCode = ret;
		return ret;
	}

	int32_t move(const int64_t& parentId, BATaskLocalNode& node)
	{
		Path parentPath = userContext_->getPathMgr()->makePath();
		parentPath.id(parentId);
		Path path = userContext_->getPathMgr()->makePath();
		path.id(node.remoteId);
		int32_t ret = userContext_->getSyncFileSystemMgr()->move(path, parentPath, false, 
			(FILE_TYPE_DIR==node.baseInfo.type)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST);
		
		node.errorCode = ret;
		return ret;
	}

	bool checkRemote(BATaskLocalNode& node)
	{
		if(0!=(node.baseInfo.opType&(BAO_Create|BAO_Uploading|BAO_Upload)))
		{
			return true;
		}

		if(-1== node.remoteId)
		{
			return true;
		}

		Path path = userContext_->getPathMgr()->makePath();
		path.id(node.remoteId);	
		if(RT_FILE_NOEXIST_ERROR == userContext_->getSyncFileSystemMgr()->isExist(path, FILE_TYPE_DIR==node.baseInfo.type?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST))
		{	
			node.remoteId = -1;
			node.baseInfo.opType = FILE_TYPE_DIR==node.baseInfo.type?BAO_Create:BAO_Upload;
			return false;
		}

		return true;
	}

private:
	UserContext* userContext_;
};

std::auto_ptr<BackupAllLocalFile> BackupAllLocalFile::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllLocalFile>(new BackupAllLocalFileImpl(userContext));
}