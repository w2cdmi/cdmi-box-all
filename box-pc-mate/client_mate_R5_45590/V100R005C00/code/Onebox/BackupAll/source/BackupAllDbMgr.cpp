#include "BackupAllDbMgr.h"
#include "ConfigureMgr.h"
#include <map>

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllDbMgr")
#endif

typedef std::map<std::wstring, BackupAllLocalDb*> LocalDBMap;

class BackupAllDbMgrImpl : public BackupAllDbMgr
{
public:
	BackupAllDbMgrImpl(UserContext* userContext):userContext_(userContext)
	{
		taskDb_ = NULL;
		parentPath_ = userContext_->getConfigureMgr()->getConfigure()->userDataPath() + PATH_DELIMITER;
		uploadFilterPeriod_ = userContext_->getConfigureMgr()->getConfigure()->uploadFilterPeriod();
	}

	virtual ~BackupAllDbMgrImpl()
	{
		// release memory
		for (LocalDBMap::iterator it = localInfos_.begin(); it != localInfos_.end(); ++it)
		{
			BackupAllLocalDb* pItem = it->second;
			if (NULL != pItem)
			{
				delete pItem;
				pItem = NULL;
			}
		}
		if (NULL != taskDb_)
		{
			delete taskDb_;
			taskDb_ = NULL;
		}
	}

	virtual BackupAllLocalDb* getBALocalDb(const std::wstring& path)
	{
		std::wstring disk = path.substr(0,1);
		LocalDBMap::iterator it = localInfos_.find(disk);
		if(it != localInfos_.end())
		{
			return it->second;
		}
		std::wstring dbPath = parentPath_ + L"local_" + disk + L".db";
		BackupAllLocalDb* pLocal = BackupAllLocalDb::create(dbPath, uploadFilterPeriod_);
		localInfos_.insert(std::make_pair(disk, pLocal));
		return pLocal;
	}

	virtual BackupAllTaskDb* getBATaskDb()
	{
		if(NULL==taskDb_)
		{
			taskDb_ = BackupAllTaskDb::create(parentPath_);
		}
		return taskDb_;
	}

private:
	UserContext* userContext_;
	LocalDBMap localInfos_;
	BackupAllTaskDb* taskDb_;
	std::wstring parentPath_;
	int32_t uploadFilterPeriod_;
};

std::auto_ptr<BackupAllDbMgr> BackupAllDbMgr::instance_(NULL);

BackupAllDbMgr* BackupAllDbMgr::getInstance(UserContext* userContext)
{
	if (NULL == instance_.get())
	{
		instance_.reset(new BackupAllDbMgrImpl(userContext));
	}
	return instance_.get();
}


