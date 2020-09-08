#include "NotifyMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("NotifyMgr")
#endif

#define SEND_MSG_TIME_OUT (3*1000)
#define FS_MAX_PATH_W (32768)

class COPY_DATA_MSG
{
public:
	COPY_DATA_MSG(const NOTIFY_PARAM& param)
		:param_(param)
		,content_Len_(6*FS_MAX_PATH_W*sizeof(wchar_t))
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
	NotifyMgrImpl(UserContext* userContext, HANDLE uiHandle);

	virtual int32_t notify(const NOTIFY_PARAM& param);

private:
	int32_t sendMessage2UI(const NOTIFY_PARAM& param);

private:
	UserContext* userContext_;
	HANDLE uiHandle_;
};

NotifyMgr* NotifyMgr::create(UserContext* userContext, int64_t uiHandle)
{
	return static_cast<NotifyMgr*>(new NotifyMgrImpl(userContext, (HANDLE)uiHandle));
}

NotifyMgrImpl::NotifyMgrImpl(UserContext* userContext, void* uiHandle)
	:userContext_(userContext)
	,uiHandle_(uiHandle)
{

}

int32_t NotifyMgrImpl::notify(const NOTIFY_PARAM& param)
{
	return sendMessage2UI(param);
}

int32_t NotifyMgrImpl::sendMessage2UI(const NOTIFY_PARAM& param)
{
	if (0 == uiHandle_)
	{
		return RT_INVALID_PARAM;
	}
	if (FS_MAX_PATH_W <= param.msg1.length() || 
		FS_MAX_PATH_W <= param.msg2.length() || 
		FS_MAX_PATH_W <= param.msg3.length() || 
		FS_MAX_PATH_W <= param.msg4.length() || 
		FS_MAX_PATH_W <= param.msg5.length() || 
		FS_MAX_PATH_W <= param.msg6.length())
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
	LRESULT ret = SendMessageTimeout((HWND)uiHandle_, 
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
		//HSLOG_ERROR(MODULE_NAME, dwResult, "SendMessageTimeout sucess, but the result is error.");
		return dwResult;
	}

	return RT_OK;
}
