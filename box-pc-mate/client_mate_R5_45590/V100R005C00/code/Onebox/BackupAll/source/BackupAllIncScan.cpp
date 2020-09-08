#include "BackupAllIncScan.h"
#include "Utility.h"
#include "AsyncTaskMgr.h"
#include "SysConfigureMgr.h"
#include "BackupAllDbMgr.h"
#include "BackupAllLocalFile.h"
#include "MFTReader.h"
#include "SmartHandle.h"
#include "BackupAllUtility.h"
#include "BackupAllFilterMgr.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllIncScan")
#endif

using namespace SD::Utility;

class BackupAllIncScanImpl : public BackupAllIncScan
{
public:
	BackupAllIncScanImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual ~BackupAllIncScanImpl()
	{
	}

	virtual int32_t incBackup(const std::wstring& disk, const USN& lastUsn)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, String::format_string("incBackup %s", String::wstring_to_string(disk).c_str()));

		pLocalDb_ = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(disk);

		//获取当前USN
		USN nextUsn;
		std::auto_ptr<MFTReader> pMFTReader = MFTReader::create(userContext_, disk);
		int32_t ret = pMFTReader->getNextUsn(nextUsn);
		if(RT_OK!=ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getNextUsn failed.");
			return ret;
		}
		if(nextUsn < lastUsn)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "getNextUsn failed.lastUsn: %I64d, nextUsn: %I64d", lastUsn, nextUsn);
			return RT_ERROR;
		}

		ret = pMFTReader->readUsnJournal(boost::bind(&BackupAllIncScanImpl::addIncNodes, this, _1, _2), lastUsn);
		if(RT_OK==ret)
		{
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateVolumeUsn(disk, nextUsn);
		}
		return ret;
	}

	int32_t addIncNodes(BATaskBaseNodeList& nodes, std::set<int64_t>& createDirs)
	{
		int32_t ret = RT_OK;
		if(nodes.empty())
		{
			return RT_OK;
		}

		//统计所有节点信息，并倒序删除重复项
		std::set<int64_t> idset;
		for(BATaskBaseNodeList::reverse_iterator rit = nodes.rbegin(); rit != nodes.rend();)
		{
			if(idset.end()==idset.find(rit->localId))
			{
				idset.insert(rit->localId);
				++rit;
			}
			else
			{
				rit = BATaskBaseNodeList::reverse_iterator(nodes.erase((++rit).base()));
			}
		}
		HSLOG_TRACE(MODULE_NAME, RT_OK, "addIncNodes size: %d", nodes.size());

		//按父子关系整理节点顺序
		BATaskBaseNodeList sortNodes;
		std::stringstream sortNodesStr;
		while(!nodes.empty())
		{
			for(BATaskBaseNodeList::iterator it = nodes.begin(); it != nodes.end();)
			{
				if(idset.end()==idset.find(it->localParent))
				{
					sortNodesStr << it->localId << "-" << SD::Utility::String::wstring_to_string(it->path) << ",";
					sortNodes.push_back(*it);
					idset.erase(it->localId);
					it = nodes.erase(it);
				}
				else
				{
					++it;
				}
			}
		}
		HSLOG_TRACE(MODULE_NAME, RT_OK, "addIncNodes [%s]", sortNodesStr.str().c_str());

		IdList deleteList;
		std::map<int64_t, std::wstring> pathInfo;
		std::auto_ptr<BackupAllFilterMgr> pFilterMgr = BackupAllFilterMgr::create(userContext_);
		for(BATaskBaseNodeList::iterator it = sortNodes.begin(); it != sortNodes.end();)
		{
			//优先在本次节点信息中找父节点信息
			std::map<int64_t, std::wstring>::iterator itP = pathInfo.find(it->localParent);
			if(pathInfo.end()==itP)
			{
				std::wstring parentPath = pLocalDb_->getPathById(it->localParent);
				if(!parentPath.empty())
				{
					it->path = pLocalDb_->getPathById(it->localParent) + PATH_DELIMITER + it->path;
				}
				else
				{
					deleteList.push_back(it->localId);	//无法获取路径的节点待删除
					it = sortNodes.erase(it);
					continue;
				}
			}
			else
			{
				it->path = itP->second + PATH_DELIMITER + it->path;
			}

			if(!it->path.empty() && pFilterMgr->isFilter(it->path))
			{
				//过滤节点跳过
				HSLOG_TRACE(MODULE_NAME, RT_OK, "%s is filter", SD::Utility::String::wstring_to_string(it->path).c_str());
				it = sortNodes.erase(it);
				continue;
			}

			if(FILE_TYPE_DIR==it->type)
			{
				pathInfo.insert(std::make_pair(it->localId, it->path));
			}
			//文件节点获取size、mtime
			else if(FILE_TYPE_FILE==it->type)
			{
				SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+it->path).c_str(), 
					GENERIC_READ, 
					FILE_SHARE_READ|FILE_SHARE_WRITE, 
					NULL, 
					OPEN_EXISTING, 
					FILE_ATTRIBUTE_NORMAL, 
					NULL);
				if (INVALID_HANDLE_VALUE == hFile)
				{
					if(2==GetLastError()||3==GetLastError())
					{
						deleteList.push_back(it->localId);	//无法获取路径的节点待删除
						it = sortNodes.erase(it);
						continue;
					}
					it->path = FS::get_file_name(it->path);
					HSLOG_ERROR(MODULE_NAME, GetLastError(), "%s CreateFile failed.", String::wstring_to_string(it->path).c_str());
					++it;
					continue;
				}

				BY_HANDLE_FILE_INFORMATION bhfi;
				(void)memset_s(&bhfi, sizeof(BY_HANDLE_FILE_INFORMATION), 0, sizeof(BY_HANDLE_FILE_INFORMATION));
				if (!GetFileInformationByHandle(hFile, &bhfi))
				{
					HSLOG_ERROR(MODULE_NAME, GetLastError(), "%s GetFileInformationByHandle failed.", String::wstring_to_string(it->path).c_str());
					++it;
					continue;
				}
				int64_t id = bhfi.nFileIndexHigh;
				id = (id<<32)+bhfi.nFileIndexLow;
				if(id!=it->localId)
				{
					deleteList.push_back(it->localId);	//当前路径节点id与usn内id不一致，待删除
					it = sortNodes.erase(it);
					continue;
				}
				if(userContext_->getSysConfigureMgr()->isBackupDisableAttr(bhfi.dwFileAttributes))
				{
					deleteList.push_back(it->localId);	//系统文件、隐藏文件等，待删除
					it = sortNodes.erase(it);
					continue;
				}
				int64_t size = bhfi.nFileSizeHigh;
				size = (size<<32)+bhfi.nFileSizeLow;
				it->size = size;
				int64_t mtime = bhfi.ftLastWriteTime.dwHighDateTime;
				mtime = (mtime<<32)+bhfi.ftLastWriteTime.dwLowDateTime;
				it->mtime = mtime;
				//文件只存储文件名
				it->path = FS::get_file_name(it->path);
			}
			++it;
		}

		HSLOG_TRACE(MODULE_NAME, RT_OK, "addIncNodes sortNodes size: %d", sortNodes.size());
		if(!sortNodes.empty())
		{
			pLocalDb_->initIncInfo();
		}
		//更新节点
		for(BATaskBaseNodeList::iterator it = sortNodes.begin(); it != sortNodes.end(); ++it)
		{
			boost::this_thread::interruption_point();
			ret = updateLocal(*it, createDirs);
			if(RT_OK!=ret)
			{
				return ret;
			}
		}

		//删除待删除节点
		std::list<std::wstring> uploadingPath;
		pLocalDb_->deleteNodes(deleteList, uploadingPath);
		userContext_->getAsyncTaskMgr()->deleteTasks(BACKUPALL_GROUPID, uploadingPath);

		return RT_OK;
	}

	int32_t updateLocal(BATaskBaseNode& node, std::set<int64_t>& createDirs)
	{
		BATaskLocalNode localNode;
		int32_t ret = RT_OK;
		if(FILE_TYPE_DIR==node.type)
		{
			ret = pLocalDb_->getNodeById(node.localId, localNode);
		}
		else
		{
			//文件考虑硬链接场景
			ret = pLocalDb_->getNodeByNode(node, localNode);
		}
		if(RT_SQLITE_NOEXIST==ret)
		{
			//添加节点
			if(FILE_TYPE_DIR==node.type)
			{
				if(createDirs.end()==createDirs.find(node.localId))
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s is move to create", String::wstring_to_string(node.path).c_str());
					return RT_ERROR;
				}
				node.opType = BAO_Create;
			}
			else
			{
				node.opType = BAO_Upload;
			}
			return pLocalDb_->addNode(node);
		}

		//重置过滤状态
		localNode.baseInfo.opType = localNode.baseInfo.opType&(~BAO_Filter);

		if(-1!=localNode.remoteId)
		{
			if(localNode.baseInfo.localParent!=node.localParent)
			{
				localNode.baseInfo.opType = localNode.baseInfo.opType|BAO_Move;
			}

			if(FILE_TYPE_DIR==node.type)
			{
				if(FS::get_file_name(localNode.baseInfo.path)!=FS::get_file_name(node.path))
				{
					localNode.baseInfo.opType = localNode.baseInfo.opType|BAO_Rename;
				}
			}
			else
			{
				if(localNode.baseInfo.path!=node.path)
				{
					localNode.baseInfo.opType = localNode.baseInfo.opType|BAO_Rename;
				}
				if(localNode.baseInfo.mtime!=node.mtime)
				{
					std::wstring extName = FS::get_extension_name(node.path);
					if((L"pst"==extName||L"ost"==extName||L"nsf"==extName)&&pLocalDb_->isNeedSkip(node.localId))
					{
						//需要跳过时，不改变文件节点的mtime
						node.mtime = localNode.baseInfo.mtime;
						HSLOG_ERROR(MODULE_NAME, RT_OK, "%s skip upload.", String::wstring_to_string(localNode.baseInfo.path).c_str());
					}
					else
					{
						localNode.baseInfo.opType = localNode.baseInfo.opType|BAO_Upload;
					}
				}
			}
		}
		if(0!=localNode.baseInfo.opType)
		{
			node.opType = localNode.baseInfo.opType;
			return pLocalDb_->updateNode(node);
		}
		else
		{
			HSLOG_ERROR(MODULE_NAME, RT_OK, "%s noChange", String::wstring_to_string(node.path).c_str());
		}
		return RT_OK;
	}

private:
	UserContext* userContext_;
	BackupAllLocalDb* pLocalDb_;
};

std::auto_ptr<BackupAllIncScan> BackupAllIncScan::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllIncScan>(new BackupAllIncScanImpl(userContext));
}