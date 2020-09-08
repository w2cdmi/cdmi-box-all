#include "RestTaskMgr.h"
#include "NetworkMgr.h"
#include "NotifyMgr.h"
#include <boost/thread.hpp>
#include "Utility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RestTaskMgr")
#endif

class RestTaskMgrImpl : public RestTaskMgr
{
public:
	RestTaskMgrImpl(UserContext* userContext):userContext_(userContext), taskId_(""), type_("")
	{
		srcOwnerId_ = 0;
		srcParentId_ = 0;
		destOwnerId_ = 0;
		destFolderId_ = 0;
	}

	~RestTaskMgrImpl()
	{
		try
		{
			queryThread_.interrupt();
			queryThread_.join();
		}
		catch(...) {}
	}

	virtual int32_t addRestTask(int64_t srcOwnerId, int64_t srcParentId, const std::list<int64_t>& srcNodeId,
											   int64_t destOwnerId, int64_t destFolderId,
											   const std::string& type, bool autoRename = false)
	{
		int32_t ret = RT_OK;
		if(!taskId_.empty())
		{
			return RT_RESTTASK_DOING;
		}

		MAKE_CLIENT(client);
		std::string tempType = (RESTTASK_SAVE==type)?RESTTASK_COPY:type;
		ret = client().addRestTask(srcOwnerId, srcNodeId, destOwnerId, destFolderId, tempType, autoRename, taskId_);

		if (RT_OK != ret)
		{
			taskId_ = "";
			HSLOG_ERROR(MODULE_NAME, ret, "addTask %s failed.", type.c_str());
			return ret;
		}
		else
		{
			type_ = type;
			srcOwnerId_ = srcOwnerId;
			srcParentId_ = srcParentId;
			destOwnerId_ = destOwnerId;
			destFolderId_ = destFolderId;
		}

		queryThread_ = boost::thread(boost::bind(&RestTaskMgrImpl::queryTaskStatus, this));

		return ret;
	}

	virtual std::string getLastType()
	{
		return type_;
	}

private:
	void queryTaskStatus()
	{
		try
		{
			int32_t ret = RT_OK;
			std::string taskStatus;
			bool taskStart = true;
			int sleepTime = 0;
			MAKE_CLIENT(client);
			while(true)
			{
				boost::this_thread::interruption_point();
				if(sleepTime < 5) ++sleepTime;
				SLEEP(boost::posix_time::seconds(sleepTime));

				ret = client().queryTaskStatus(taskId_, taskStatus);
				if(RT_OK == ret)
				{
					if("Doing"!=taskStatus)
					{
						HSLOG_ERROR(MODULE_NAME, ret, "taskId:%s, taskStatus:%s", taskId_.c_str(), taskStatus.c_str());
						taskId_ = "";
						if("NotFound"==taskStatus)
						{
							userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_RESTTASK_COMPLETE,
								SD::Utility::String::string_to_wstring(type_), 
								SD::Utility::String::type_to_string<std::wstring>(srcOwnerId_),
								SD::Utility::String::type_to_string<std::wstring>(srcParentId_),
								SD::Utility::String::type_to_string<std::wstring>(destOwnerId_),
								SD::Utility::String::type_to_string<std::wstring>(destFolderId_)));
						}
						else
						{
							userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_RESTTASK_ERROR,
								SD::Utility::String::string_to_wstring(type_),
								SD::Utility::String::type_to_string<std::wstring>(srcOwnerId_),
								SD::Utility::String::type_to_string<std::wstring>(srcParentId_),
								SD::Utility::String::type_to_string<std::wstring>(destOwnerId_),
								SD::Utility::String::type_to_string<std::wstring>(destFolderId_),
								SD::Utility::String::string_to_wstring(taskStatus)));
						}
						return;
					}

					if(taskStart)
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_RESTTASK_START, SD::Utility::String::string_to_wstring(type_)));
						taskStart = false;
					}
				}
			}
		}
		catch(boost::thread_interrupted&)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_CANCEL, "query rest task status thread interrupted.");
		}
	}

private:
	UserContext* userContext_;
	std::string taskId_;
	std::string type_;
	int64_t srcOwnerId_;
	int64_t srcParentId_;
	int64_t destOwnerId_;
	int64_t destFolderId_;
	boost::thread queryThread_;
};

RestTaskMgr* RestTaskMgr::create(UserContext* userContext)
{
	return static_cast<RestTaskMgr*>(new RestTaskMgrImpl(userContext));
}
