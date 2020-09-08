#include "NotifyMgr.h"
#include <list>
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("NotifyMgr")
#endif

#define SEND_MSG_TIME_OUT (1000)

class COPY_DATA_MSG
{
public:
	COPY_DATA_MSG(const NOTIFY_PARAM& param)
		:param_(param)
		,content_Len_(7*FS_MAX_PATH_W*sizeof(wchar_t))
	{
	}

	std::shared_ptr<unsigned char> getContent()
	{
		std::shared_ptr<unsigned char> buf(new unsigned char[content_Len_]);
		wcscpy_s((wchar_t*)buf.get(), FS_MAX_PATH_W, param_.msg1.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W, FS_MAX_PATH_W, param_.msg2.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W*2, FS_MAX_PATH_W, param_.msg3.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W*3, FS_MAX_PATH_W, param_.msg4.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W*4, FS_MAX_PATH_W, param_.msg5.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W*5, FS_MAX_PATH_W, param_.msg6.c_str());
		wcscpy_s(((wchar_t*)buf.get())+FS_MAX_PATH_W*6, FS_MAX_PATH_W, param_.msg7.c_str());
		return buf;
	}

	size_t getContentLen()
	{
		return content_Len_;
	}

private:
	COPY_DATA_MSG();

private:
	const NOTIFY_PARAM& param_;
	size_t content_Len_;
};

class NotifyMgrImpl : public NotifyMgr
{
public:
	NotifyMgrImpl(UserContext* userContext, int64_t uiHandle);

	virtual ~NotifyMgrImpl();

	virtual int32_t notify(const NOTIFY_PARAM& param);

	virtual void addNotify(int64_t uiHandle);

	virtual void removeNotify(int64_t uiHandle);

	virtual void interrupt();

private:
	int32_t sendMessage2UI(const NOTIFY_PARAM& param, int64_t uiHandle);

	int32_t NotifyMgrImpl::sendMessage2UITrustiness(const NOTIFY_PARAM& param, int64_t uiHandle);

	void notifyThread();

private:
	UserContext* userContext_;
	boost::mutex mutex_;
	std::list<int64_t> uiHandles_;
	boost::mutex notifyMutex_;
	std::list<NOTIFY_PARAM> notifyParams_;
	boost::thread notifyThread_;
};

NotifyMgr* NotifyMgr::create(UserContext* userContext, int64_t uiHandle)
{
	return static_cast<NotifyMgr*>(new NotifyMgrImpl(userContext, uiHandle));
}

NotifyMgrImpl::NotifyMgrImpl(UserContext* userContext, int64_t uiHandle)
	:userContext_(userContext)
{
	try
	{
		uiHandles_.push_back(uiHandle);
		notifyThread_ = boost::thread(boost::bind(&NotifyMgrImpl::notifyThread, this));
	}
	catch(...) { }
}

NotifyMgrImpl::~NotifyMgrImpl()
{
	
}

int32_t NotifyMgrImpl::notify(const NOTIFY_PARAM& param)
{
	boost::mutex::scoped_lock lock(notifyMutex_);
	notifyParams_.push_back(param);
	return RT_OK;
}

void NotifyMgrImpl::addNotify(int64_t uiHandle)
{
	boost::mutex::scoped_lock lock(mutex_);
	uiHandles_.push_back(uiHandle);
}

void NotifyMgrImpl::removeNotify(int64_t uiHandle)
{
	boost::mutex::scoped_lock lock(mutex_);
	uiHandles_.remove(uiHandle);
}

void NotifyMgrImpl::interrupt()
{
	try
	{
		notifyThread_.interrupt();
		notifyThread_.join();
	}
	catch(...) { }
}

int32_t NotifyMgrImpl::sendMessage2UI(const NOTIFY_PARAM& param, int64_t uiHandle)
{
	if (0 == uiHandle)
	{
		return RT_INVALID_PARAM;
	}
	if (FS_MAX_PATH_W <= param.msg1.length() || 
		FS_MAX_PATH_W <= param.msg2.length() || 
		FS_MAX_PATH_W <= param.msg3.length() || 
		FS_MAX_PATH_W <= param.msg4.length() || 
		FS_MAX_PATH_W <= param.msg5.length() || 
		FS_MAX_PATH_W <= param.msg6.length() || 
		FS_MAX_PATH_W <= param.msg7.length())
	{
		return RT_INVALID_PARAM;
	}

	COPY_DATA_MSG copyDataMsg(param);
	std::shared_ptr<unsigned char> content = copyDataMsg.getContent();
	COPYDATASTRUCT copyData =
	{
		param.type, copyDataMsg.getContentLen(), (PVOID)content.get()
	};

	DWORD dwResult = RT_OK;
	LRESULT ret = SendMessageTimeout((HWND)uiHandle, 
		WM_COPYDATA, 
		0, 
		(LPARAM)&copyData, 
		SMTO_BLOCK, SEND_MSG_TIME_OUT, 
		&dwResult);

	if (RT_OK == ret)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "SendMessageTimeout failed, msg: %d.", param.type);
		return ret;
	}
	if (RT_OK != dwResult)
	{
		HSLOG_ERROR(MODULE_NAME, dwResult, "SendMessageTimeout success, but the result is error.");
		return dwResult;
	}

	return RT_OK;
}

int32_t NotifyMgrImpl::sendMessage2UITrustiness(const NOTIFY_PARAM& param, int64_t uiHandle)
{
	if (0 == uiHandle)
	{
		return RT_INVALID_PARAM;
	}
	if (FS_MAX_PATH_W <= param.msg1.length() || 
		FS_MAX_PATH_W <= param.msg2.length() || 
		FS_MAX_PATH_W <= param.msg3.length() || 
		FS_MAX_PATH_W <= param.msg4.length() || 
		FS_MAX_PATH_W <= param.msg5.length() || 
		FS_MAX_PATH_W <= param.msg6.length() || 
		FS_MAX_PATH_W <= param.msg7.length())
	{
		return RT_INVALID_PARAM;
	}

	COPY_DATA_MSG copyDataMsg(param);
	std::shared_ptr<unsigned char> content = copyDataMsg.getContent();
	COPYDATASTRUCT copyData =
	{
		param.type, copyDataMsg.getContentLen(), (PVOID)content.get()
	};

	LRESULT ret = SendMessage((HWND)uiHandle, 
		WM_COPYDATA, 
		0, 
		(LPARAM)&copyData );

	if (RT_OK != ret)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "SendMessage failed, msg: %d.", param.type);
		return ret;
	}

	return RT_OK;
}

void NotifyMgrImpl::notifyThread()
{
	try
	{
		while (true)
		{
			boost::this_thread::interruption_point();
			NOTIFY_PARAM param;
			{
				boost::mutex::scoped_lock lock(notifyMutex_);
				if (notifyParams_.empty())
				{
					lock.unlock();
					SLEEP(boost::posix_time::milliseconds(10));
					continue;
				}
				param = notifyParams_.front();
				notifyParams_.pop_front();
			}
			boost::mutex::scoped_lock lock(mutex_);
			for (std::list<int64_t>::iterator it = uiHandles_.begin(); it != uiHandles_.end(); )
			{
				boost::this_thread::interruption_point();
				if (!::IsWindow(HWND(*it)))
				{
					it = uiHandles_.erase(it);
					continue;
				}
				(void)sendMessage2UI(param, *it);
				++it;
			}
		}
	}
	catch(...)
	{
		HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "notify thread interrupted.");
	}
}
