#include "stdafx.h"
#include "SyncServiceImpl.h"
#include "CrashHandler.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("main")
#endif

int _tmain(int argc, _TCHAR* argv[])
{
	SetErrorMode(SEM_NOGPFAULTERRORBOX|SEM_FAILCRITICALERRORS);

	if (argc < 2)
	{
		return RT_OK;
	}
	int ret = RT_OK;
	HANDLE hStopEvent = NULL;
	std::wstring strCmd =std::wstring(argv[1]);
	if (strCmd == L"start")
	{
		hStopEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT);
		if (NULL != hStopEvent)
		{
			ret = GetLastError();
			printf("start failed, the event has been opened, ret: %d.\n", ret);
			return ret;
		}
		hStopEvent = CreateEvent(NULL, FALSE, FALSE, SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT);
		if (NULL == hStopEvent)
		{
			ret = GetLastError();
			printf("start failed, can not create the event, ret: %d.\n", ret);
			return ret;
		}
	}
	else if(strCmd == L"stop")
	{
		hStopEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT);
		if (NULL == hStopEvent)
		{
			ret = GetLastError();
			printf("stop failed, the event has not been created, ret: %d.\n", ret);
			return ret;
		}

		SetEvent(hStopEvent);

		return RT_OK;
	}
	else
	{
		printf("invalid parameter.\n");
		return RT_INVALID_PARAM;
	}

	if (0 != ISSP_LogInit("./log4cpp.conf", TP_FILE, "./OneboxSyncService.log"))
	{
		printf("init log failed.\n");
		return RT_ERROR;
	}

	CCrashHandler objCrashHandler;
	objCrashHandler.SetProcessExceptionHandlers();
	objCrashHandler.SetThreadExceptionHandlers();

	boost::thread mainThread(boost::bind(&SyncServiceImpl::startService));

	if (WAIT_OBJECT_0 != WaitForSingleObject(hStopEvent, INFINITE))
	{
		HSLOG_ERROR(MODULE_NAME, GetLastError(), "wait for exit event failed.");
	}

	SyncServiceImpl::stopService();

	if (WAIT_OBJECT_0 != WaitForSingleObject(mainThread.native_handle(), INFINITE))
	{
		HSLOG_ERROR(MODULE_NAME, GetLastError(), "wait for main thread exit failed.");
	}

	ISSP_LogExit();

	return RT_OK;
}

