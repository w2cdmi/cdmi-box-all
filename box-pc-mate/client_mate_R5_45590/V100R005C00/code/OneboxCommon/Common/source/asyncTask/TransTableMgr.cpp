#include "TransTableMgr.h"
#include <map>
#include <Utility.h>
#include "TransTableDefine.h"
#include <boost/thread.hpp>
#include "SyncFileSystemMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTableMgr")
#endif

#define DEFAULT_FLUSH_CACHE_INTERVAL (60)

typedef std::map<std::wstring, std::shared_ptr<TransDetailTable>> TransDetailTables;

class TransTableMgr::Impl
{
public:
	Impl(UserContext* userContext, const std::wstring& path)
		:userContext_(userContext)
		,path_(path)
	{
		try
		{
			(void)init();
			flushCacheThread_ = boost::thread(boost::bind(&Impl::flushCache, this));
		}
		catch(...) {}
	}

	~Impl()
	{
		try
		{
			flushCacheThread_.interrupt();
			flushCacheThread_.join();
		}
		catch(...) { }
	}

	TransRootTable* getRootTable(const AsyncTransType type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (ATT_Backup == type)
		{
			return backupRootTable_.get();
		}
		return rootTable_.get();
	}

	TransRootTable* getRootTable(const std::wstring& group)
	{
		if (group.empty())
		{
			return NULL;
		}
		boost::mutex::scoped_lock lock(mutex_);
		AsyncTransRootNode node;
		int32_t ret = backupRootTable_->getNode(group, node);
		if (RT_OK == ret)
		{
			return backupRootTable_.get();
		}
		else if (RT_SQLITE_NOEXIST == ret)
		{
			return rootTable_.get();
		}
		return NULL;
	}

	TransDetailTablePtr getDetailTable(const std::wstring& group)
	{
		if (group.empty())
		{
			return TransDetailTablePtr(NULL);
		}
		boost::mutex::scoped_lock lock(mutex_);
		TransDetailTables::iterator it = detailTables_.find(group);
		if (detailTables_.end() != it)
		{
			return it->second;
		}

		AsyncTransRootNode rootNode;
		int32_t ret = backupRootTable_->getNode(group, rootNode);
		if (RT_SQLITE_NOEXIST == ret)
		{
			TransRootTable* rootTable = rootTable_.get();
			ret = rootTable->getNode(group, rootNode);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "get root node of %s failed.", 
					Utility::String::wstring_to_string(group).c_str());
				return TransDetailTablePtr(NULL);
			}
		}
		else if (RT_OK != ret)
		{
			return TransDetailTablePtr(NULL);
		}
		
		TransDetailTablePtr detailTable(new (std::nothrow)TransDetailTable(userContext_, path_ + TTDN_DETAIL + group + L".db"));
		if (NULL == detailTable.get())
		{
			return TransDetailTablePtr(NULL);
		}
		ret = detailTable->create(rootNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create detail table of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			return TransDetailTablePtr(NULL);
		}

		// cache the detail table
		detailTables_[group] = detailTable;

		return detailTable;
	}

	TransDataTable* getDataTable()
	{
		boost::mutex::scoped_lock lock(mutex_);
		return dataTable_.get();
	}

	TransCompleteTable* getCompleteTable()
	{
		boost::mutex::scoped_lock lock(mutex_);
		return completeTable_.get();
	}

	TransDetailTable* createDetailTable(const std::wstring& group)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// if the detail table is in cache, return the cache
		TransDetailTables::iterator it = detailTables_.find(group);
		if (detailTables_.end() != it)
		{
			return (it->second).get();
		}

		TransDetailTable* detailTable = new TransDetailTable(userContext_, path_ + TTDN_DETAIL + group + L".db");
		if (NULL == detailTable)
		{
			return NULL;
		}
		AsyncTransRootNode rootNode;
		int32_t ret = detailTable->create(rootNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create detail table of %s failed.", 
				Utility::String::wstring_to_string(group).c_str());
			delete detailTable;
			return NULL;
		}
		return detailTable;
	}

	int32_t removeDetailTable(const std::wstring& group)
	{
		if (group.empty())
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		return removeDetailTableNoLock(group);
	}

	int32_t removeDetailTable(const AsyncTransType type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// 1. get all root nodes which type is match
		// 2. remove the detail table
		TransRootTable* rootTable = rootTable_.get();
		if (ATT_Backup == type)
		{
			rootTable = backupRootTable_.get();
		}
		AsyncTransRootNodes rootNodes;
		int32_t ret = rootTable->getNodes(type, rootNodes, PAGE_OBJ/*get all nodes*/);
		if (RT_SQLITE_NOEXIST == ret)
		{
			return RT_OK;
		}
		else if (RT_OK != ret)
		{
			return ret;
		}
		for (AsyncTransRootNodes::iterator it = rootNodes.begin(); it != rootNodes.end(); ++it)
		{
			// we may not want to miss the error code
			if (RT_OK != ret)
			{
				removeDetailTableNoLock((*it)->group);
			}
			else
			{
				ret = removeDetailTableNoLock((*it)->group);
			}
		}
		return ret;
	}

	int32_t removeZombieDetailTable()
	{
		Path detailFolderPath;
		detailFolderPath.path(path_ + TTDN_DETAIL);
		LIST_FOLDER_RESULT result;
		(void)userContext_->getSyncFileSystemMgr()->listFolder(detailFolderPath, result, ADAPTER_FOLDER_TYPE_LOCAL);
		std::wstring group = L"";
		for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
		{
			group = it->name.substr(0, it->name.rfind(L'.'));
			AsyncTransRootNode node;
			if (RT_SQLITE_NOEXIST == rootTable_->getNode(group, node) && 
				RT_SQLITE_NOEXIST == backupRootTable_->getNode(group, node))
			{
				(void)Utility::FS::remove(detailFolderPath.path() + it->name);
			}
		}
		return RT_OK;
	}

private:
	int32_t init()
	{
		if (path_.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "invalid param, path is empty.");
			return RT_INVALID_PARAM;
		}
		if (!Utility::FS::is_directory(path_))
		{
			Utility::FS::create_directories(path_);
		}
		
		rootTable_.reset(new TransRootTable(userContext_, path_ + PATH_DELIMITER + TTFN_ROOT));
		int32_t ret = rootTable_->create(ATCM_CachePart);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create root table failed.");
			rootTable_.reset(NULL);
		}

		backupRootTable_.reset(new TransRootTable(userContext_, path_ + PATH_DELIMITER + TTFN_BACKUP_ROOT));
		ret = backupRootTable_->create(ATCM_CacheAll);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create backup root table failed.");
			backupRootTable_.reset(NULL);
		}

		dataTable_.reset(new TransDataTable(userContext_, path_ + PATH_DELIMITER + TTFN_DATA));
		ret = dataTable_->create(ATCM_CachePart);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create data table failed.");
			dataTable_.reset(NULL);
		}

		completeTable_.reset(new TransCompleteTable(userContext_, path_ + PATH_DELIMITER + TTFN_COMPLETE));
		ret = completeTable_->create();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create complete table failed.");
			completeTable_.reset(NULL);
		}

		return RT_OK;
	}

	int32_t removeDetailTableNoLock(const std::wstring& group)
	{
		if (group.empty())
		{
			HSLOG_ERROR(MODULE_NAME ,RT_INVALID_PARAM, 
				"invalid param, failed to remove detail table.");
			return RT_INVALID_PARAM;
		}
		int32_t ret = RT_OK;
		// if the detail table is in cache, should remove it from the cache
		TransDetailTables::iterator it = detailTables_.find(group);
		if (detailTables_.end() != it)
		{
			// check if the detail table is used by someone else
			if (it->second.use_count() == 1)
			{
				ret = it->second->remove();
			}
			detailTables_.erase(it);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME ,ret, 
					"failed to remove detail table of %s.", 
					Utility::String::wstring_to_string(group).c_str());
			}
			// here we ignore the return value, the db file can not be delete
			return RT_OK;
		}
		// if not in cache, means the detail table is not been open, just delete the detail file
		std::wstring detailFilePath = path_ + TTDN_DETAIL + group + L".db";
		if (!Utility::FS::is_exist(detailFilePath))
		{
			return RT_OK;
		}
		ret = Utility::FS::remove(detailFilePath);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME ,ret, 
				"failed to remove detail table of %s.", 
				Utility::String::wstring_to_string(group).c_str());
		}
		// here we ignore the return value, the db file can not be delete
		return RT_OK;
	}

	void flushCache()
	{
		try
		{
			while (true)
			{
				SLEEP(boost::posix_time::seconds(DEFAULT_FLUSH_CACHE_INTERVAL));
				boost::mutex::scoped_lock lock(mutex_);
				(void)rootTable_->flushCache();
				(void)backupRootTable_->flushCache();
				(void)dataTable_->flushCache();
			}
		}
		catch(...)
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "flush cache thread interrupted.");
		}
	}

private:
	UserContext* userContext_;
	std::wstring path_;
	std::auto_ptr<TransRootTable> rootTable_;
	std::auto_ptr<TransRootTable> backupRootTable_;
	TransDetailTables detailTables_;
	std::auto_ptr<TransDataTable> dataTable_;
	std::auto_ptr<TransCompleteTable> completeTable_;
	boost::thread flushCacheThread_;
	boost::mutex mutex_;
};

TransTableMgr::TransTableMgr(UserContext* userContext, const std::wstring& path)
	:impl_(new Impl(userContext, path))
{

}

TransRootTable* TransTableMgr::getRootTable(const AsyncTransType type)
{
	return impl_->getRootTable(type);
}

TransRootTable* TransTableMgr::getRootTable(const std::wstring& group)
{
	return impl_->getRootTable(group);
}

TransDetailTablePtr TransTableMgr::getDetailTable(const std::wstring& group)
{
	return impl_->getDetailTable(group);
}

TransDataTable* TransTableMgr::getDataTable()
{
	return impl_->getDataTable();
}

TransCompleteTable* TransTableMgr::getCompleteTable()
{
	return impl_->getCompleteTable();
}

TransDetailTable* TransTableMgr::createDetailTable(const std::wstring& group)
{
	return impl_->createDetailTable(group);
}

int32_t TransTableMgr::removeDetailTable(const std::wstring& group)
{
	return impl_->removeDetailTable(group);
}

int32_t TransTableMgr::removeDetailTable(const AsyncTransType type)
{
	return impl_->removeDetailTable(type);
}

int32_t TransTableMgr::removeZombieDetailTable()
{
	return impl_->removeZombieDetailTable();
}
