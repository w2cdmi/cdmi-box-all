#include "BackupAllFilterMgr.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "SysConfigureMgr.h"
#include "BackupAllDbMgr.h"
#include "BackupAllLocalFile.h"
#include "SmartHandle.h"
#include "AsyncTaskMgr.h"
#include "BackupAllUtility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllFilterMgr")
#endif

using namespace SD::Utility;

class BackupAllFilterMgrImpl : public BackupAllFilterMgr
{
public:
	BackupAllFilterMgrImpl(UserContext* userContext):userContext_(userContext)
	{
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(pathInfo_, maxPathLen_);
	}

	BackupAllFilterMgrImpl(UserContext* userContext, const std::list<std::wstring>&selectList,
		const std::list<std::wstring>&filterList):userContext_(userContext)
	{
		maxPathLen_ = 0;
		for(std::list<std::wstring>::const_iterator itS = selectList.begin(); itS != selectList.end(); ++itS)
		{
			pathInfo_.insert(std::make_pair(*itS, BAP_Select));
			if(itS->length()>maxPathLen_)
			{
				maxPathLen_ = itS->length();
			}
		}
		for(std::list<std::wstring>::const_iterator itF = filterList.begin(); itF != filterList.end(); ++itF)
		{
			pathInfo_.insert(std::make_pair(*itF, BAP_Filter));
			if(itF->length()>maxPathLen_)
			{
				maxPathLen_ = itF->length();
			}
		}
	}

	virtual ~BackupAllFilterMgrImpl()
	{
	}

	virtual void getSelectInfo(const std::wstring& volume, std::set<int64_t>& selectDirs)
	{
		std::map<std::wstring, int64_t> selectPath;
		std::map<std::wstring, int64_t> filterPath;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(selectPath, filterPath);
		for(std::map<std::wstring, int64_t>::iterator itS = selectPath.begin(); itS != selectPath.end(); ++itS)
		{
			if(-1!=itS->second 
				&& FS::get_parent_path(itS->first).length()>3
				&& itS->first.substr(0,1)==volume.substr(0,1))
			{
				selectDirs.insert(itS->second);
			}
		}
	}

	virtual void flushSelectInfo()
	{
		//更新目录选择信息
		std::map<std::wstring, int64_t> selectPath;
		std::map<std::wstring, int64_t> filterPath;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(selectPath, filterPath);
		for(std::map<std::wstring, int64_t>::iterator itS = selectPath.begin(); itS != selectPath.end(); ++itS)
		{
			flushPath(itS->first, itS->second, true);
		}
		for(std::map<std::wstring, int64_t>::iterator itF = filterPath.begin(); itF != filterPath.end(); ++itF)
		{
			flushPath(itF->first, itF->second, false);
		}
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(pathInfo_, maxPathLen_);
	}

	virtual void doDeleteVolumeInfo()
	{
		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo, BAVS_Delete);
		for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
		{
			std::map<std::wstring, int32_t> pathInfo;
			pathInfo.insert(std::make_pair(it->first, BAP_Filter));
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->updateStatus(pathInfo);
			std::list<std::wstring> filterList;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->getFilterUploading(filterList);
			userContext_->getAsyncTaskMgr()->deleteTasks(BACKUPALL_GROUPID, filterList);
		}
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->deleteVolumeInfo();
	}

	virtual void doFilterChange() 
	{
		std::map<std::wstring, int64_t> volumeInfo;
		BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getVolumeInfo(volumeInfo);
		//重置目录选择信息
		for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
		{
			checkPath(it->first);

			std::map<std::wstring, int32_t> pathInfo;
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->getPathInfo(it->first, pathInfo);
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->updateStatus(pathInfo);
			std::list<std::wstring> filterList;
			BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first)->getFilterUploading(filterList);
			userContext_->getAsyncTaskMgr()->deleteTasks(BACKUPALL_GROUPID, filterList);
		}
	}

	virtual bool isFilter(const std::wstring& path)
	{
		std::map<std::wstring, int32_t>::iterator it = pathInfo_.find(path);
		if(pathInfo_.end()!=it)
		{
			return BAP_Filter==it->second;
		}
		//在过滤设置中查找最近的父目录
		std::wstring tempPath = FS::get_parent_path(path.substr(0, maxPathLen_+1));
		while(!tempPath.empty())
		{
			it = pathInfo_.find(tempPath);
			if(pathInfo_.end()!=it)
			{
				return BAP_Filter==it->second;
			}
			tempPath = FS::get_parent_path(tempPath);
		}
		return true;
	}

	virtual bool isFilter(const std::wstring& path, bool parentIsFilter)
	{
		std::map<std::wstring, int32_t>::iterator it = pathInfo_.find(path);
		if(pathInfo_.end()!=it)
		{
			return BAP_Filter==it->second;
		}
		//未设置时，返回父的过滤设置
		return parentIsFilter;
	}

private:
	void checkPath(const std::wstring& volumePath)
	{
		std::list<std::wstring> checkPaths;
		BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(volumePath)->getCheckPath(checkPaths);

		for(std::list<std::wstring>::const_iterator it = checkPaths.begin(); it!= checkPaths.end(); ++it)
		{
			if(it->length()<4)
			{
				BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(volumePath)->setAllCheck();
				return;
			}
		}

		BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(volumePath)->setCheckNodes(checkPaths);
	}

	void flushPath(const std::wstring& path, const int64_t& id, bool isSelect)
	{
		if(VOLUME_ROOTID==id)
		{
			return;
		}
		if(-1==id)
		{
			if(isSelect)
			{
				std::auto_ptr<BackupAllLocalFile> pLocalFile = BackupAllLocalFile::create(userContext_);
				int64_t localParent;
				if(RT_OK!=pLocalFile->buildPathFromRoot(path, localParent))
				{
					return;
				}
			}
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateIdByPath(path, BackupAll::getIdByPath(userContext_, path));
			return;
		}

		std::wstring curPath = BackupAll::getFullPathByFileId(path, id);
		if(curPath.empty())
		{
			//刷新id信息
			if(SD::Utility::FS::is_directory(path))
			{
				BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updateIdByPath(path, BackupAll::getIdByPath(userContext_, path));
			}
			return;
		}

		if(path != curPath)
		{
			if(isSelect)
			{
				std::auto_ptr<BackupAllLocalFile> pLocalFile = BackupAllLocalFile::create(userContext_);
				int64_t localParent;
				if(RT_OK!=pLocalFile->buildPathFromRoot(curPath, localParent))
				{
					return;
				}
			}
			//刷新路径信息
			BackupAllDbMgr::getInstance(userContext_)->getBATaskDb()->updatePathById(id, curPath);
		}
	}

private:
	UserContext* userContext_;
	std::map<std::wstring, int32_t> pathInfo_;
	uint32_t maxPathLen_;
};

std::auto_ptr<BackupAllFilterMgr> BackupAllFilterMgr::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllFilterMgr>(new BackupAllFilterMgrImpl(userContext));
}

std::auto_ptr<BackupAllFilterMgr> BackupAllFilterMgr::create(UserContext* userContext,
		const std::list<std::wstring>&selectList, const std::list<std::wstring>&filterList)
{
	return std::auto_ptr<BackupAllFilterMgr>(new BackupAllFilterMgrImpl(userContext, selectList, filterList));
}