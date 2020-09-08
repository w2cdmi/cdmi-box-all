#include "BackupAllProcess.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "BackupAllDbMgr.h"
#include "BackupAllLocalFile.h"
#include <boost/thread.hpp>
#include "MFTReader.h"
#include "SmartHandle.h"
#include "AsyncTaskMgr.h"
#include "BackupAllMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllProcess")
#endif

#define UR_BUF_LEN 1024

using namespace SD::Utility;

class BackupAllProcessImpl : public BackupAllProcess
{
public:
	BackupAllProcessImpl(UserContext* userContext):userContext_(userContext)
	{
		pLocalFile_ = BackupAllLocalFile::create(userContext);
		pTaskDb_ = BackupAllDbMgr::getInstance(userContext_)->getBATaskDb();
		isProcess_ = false;
		isProcessStoping_ = false;
		isFirstTime_ = true;
	}

	virtual ~BackupAllProcessImpl()
	{
		try
		{
			stop(false);
		}
		catch(...) {}	
	}

	virtual int32_t stop(bool async = true)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "stop");
		if(isProcess_&&!isProcessStoping_)
		{
			processThread_.interrupt();
			if (!async)
			{
				processThread_.join();
			}
			isProcessStoping_ = true;
		}
		return RT_OK;
	}

	virtual int32_t start(bool hasError)
	{
		if(isProcessStoping_)
		{
			processThread_.join();
			isProcessStoping_ = false;
			isProcess_ = false;
		}
		if(!isProcess_)
		{
			isProcess_ = true;
			processThread_ = boost::thread(boost::bind(&BackupAllProcessImpl::processDiff, this, hasError));
		}
		return RT_OK;	
	}

private:
	void processDiff(bool hasError)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, String::format_string("processDiff %d", hasError));
		try
		{
			int32_t ret = RT_OK;
			//更新备份选择信息期间，等待更新完成
			BATaskNode baTaskNode;
			do
			{
				if(RT_OK!=pTaskDb_->getNode(baTaskNode))
				{
					return;
				}
				boost::this_thread::sleep(boost::posix_time::milliseconds(500));
			}
			while(baTaskNode.isFilterChange);

			bool hasErrorTasks = (userContext_->getAsyncTaskMgr()->getTasksCount(BACKUPALL_GROUPID, ATS_Error)>0);
			if(isFirstTime_)
			{
				userContext_->getAsyncTaskMgr()->beginAddAsyncTasks(BACKUPALL_GROUPID, userContext_->id.id, ATT_Backup);
				hasError = true;	//首次启动重试所有任务(包括过滤对象)
			}
			if(hasError)
			{
				userContext_->getAsyncTaskMgr()->resumeTask(BACKUPALL_GROUPID);
			}
			
			std::map<std::wstring, int64_t> volumeInfo;
			while(true)
			{
				pTaskDb_->getVolumeInfo(volumeInfo);
				for(std::map<std::wstring, int64_t>::iterator it = volumeInfo.begin(); it != volumeInfo.end(); ++it)
				{
					BackupAllLocalDb* pLocalDb = BackupAllDbMgr::getInstance(userContext_)->getBALocalDb(it->first);
					if(hasError)
					{
						//重置错误码
						pLocalDb->resumeError(hasErrorTasks, isFirstTime_);
					}

					while(true)
					{
						boost::this_thread::interruption_point();
						BATaskLocalNode node;
						ret = pLocalDb->getNextNode(node);
						if(RT_OK!=ret)
						{
							break;
						}
						pLocalFile_->backup(pLocalDb, node);
					}
				}
				hasError = false;
				if(BATS_Running==BackupAllMgr::getInstance(userContext_)->getTaskInfo()->status)
				{
					boost::this_thread::sleep(boost::posix_time::milliseconds(100));
				}
				else
				{
					break;
				}
			}
			//userContext_->getAsyncTaskMgr()->endAddAsyncTasks(BACKUPALL_GROUPID, ATT_Backup);
		}
		catch(boost::thread_interrupted)
		{
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "backup all proccess diff thread interrupted.");
		}
		isProcess_ = false;
		isFirstTime_ = false;
	}

private:
	UserContext* userContext_;
	std::auto_ptr<BackupAllLocalFile> pLocalFile_;
	BackupAllTaskDb* pTaskDb_;

	boost::thread processThread_;
	bool isProcess_;
	bool isProcessStoping_;
	bool isFirstTime_;
};

std::auto_ptr<BackupAllProcess> BackupAllProcess::create(UserContext* userContext)
{
	return std::auto_ptr<BackupAllProcess>(new BackupAllProcessImpl(userContext));
}