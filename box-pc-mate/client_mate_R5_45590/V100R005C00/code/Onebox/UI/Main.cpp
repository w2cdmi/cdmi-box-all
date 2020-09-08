#include "stdafxOnebox.h"
#include "MainFrame.h"
#include "Utility.h"
#include "InIHelper.h"
#include "CommonDefine.h"
#include "CrashHandler.h"
#include "UICommonDefine.h"
#include "SmartHandle.h"

static int createMainFrame(HINSTANCE hInstance, bool bShow = true, const std::wstring& data = L"", const std::wstring& token = L"")
{
	ULONG_PTR gdiplusToken;
	Gdiplus::GdiplusStartupInput gdiplusStartup;
	GdiplusStartup(&gdiplusToken,&gdiplusStartup,NULL);

	CCrashHandler objCrashHandler;
	objCrashHandler.SetProcessExceptionHandlers();
	objCrashHandler.SetThreadExceptionHandlers();
	std::wstring wstrInstallPath = L"";
	if (0 == SD::Utility::Registry::get(HKEY_LOCAL_MACHINE,ONEBOX_REG_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath) ||
		0 == SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REGHKCU_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath))
	{
		::SetCurrentDirectory(wstrInstallPath.c_str());
	}
	std::wstring logFile = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR;
	if (!SD::Utility::FS::is_directory(logFile))
	{
		SD::Utility::FS::create_directories(logFile);
	}
	if (!SD::Utility::FS::is_directory(logFile))
	{
		return 0;
	}
	SD::Utility::FS::copy_file(GetInstallPath() + L"log4cpp.conf",SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf");
	logFile += std::wstring(PATH_DELIMITER)+L"OneboxUI.log";
	ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"), 
		TP_FILE, 
		SD::Utility::String::wstring_to_string(logFile));

	CPaintManagerUI::SetInstance(hInstance);
	CPaintManagerUI::SetResourcePath(CPaintManagerUI::GetInstancePath());

	Onebox::MainFrame* mainFrame = new Onebox::MainFrame(data, token);
	if (NULL == mainFrame)
	{
		return 0;
	}
	mainFrame->Create(NULL, ONEBOX_APP_NAME, UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE | WS_EX_APPWINDOW, 0, 0, 0, 0);
	SD::Utility::Registry::set(HKEY_CURRENT_USER,ONEBOX_REG_PATH,HWND_REG_NAME,(DWORD)mainFrame->GetHWND()); 
	mainFrame->CenterWindow();
	if (!bShow)
		mainFrame->ShowWindow(false);
	CPaintManagerUI::MessageLoop();
	CPaintManagerUI::Term();

	if (NULL != mainFrame)
	{
		delete mainFrame;
		mainFrame = NULL;
	}

	GdiplusShutdown(gdiplusToken);

	ISSP_LogExit();

	return 0;
}

class CmdArgsMemHelper
{
public:
	CmdArgsMemHelper(HLOCAL hMem)
		:hMem_(hMem)
	{

	}

	~CmdArgsMemHelper()
	{
		if (NULL != hMem_)
		{
			LocalFree(hMem_);
		}
	}
private:
	HLOCAL hMem_;
};

int APIENTRY _tWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow)
{
	SetErrorMode(SEM_NOGPFAULTERRORBOX|SEM_FAILCRITICALERRORS);

	int argc = 0;
	LPWSTR *argv = CommandLineToArgvW(GetCommandLineW(), &argc);
	if (NULL == argv || argc == 0)
	{
		return RT_INVALID_PARAM;
	}

	CmdArgsMemHelper cmdArgsMemHelper((HLOCAL)argv);
	std::wstring command = (argc > 1) ? argv[1] : L"start";
	SmartHandle hEvent = NULL;
	DWORD hWnd;
	SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REG_PATH,HWND_REG_NAME,hWnd);

	try
	{
		if (command == L"restore")
		{
			hEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			int32_t i = 0;
			while (NULL != hEvent && i < 10)
			{
				Sleep(1000);
				hEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
				++i;
			}

			hEvent = CreateEvent(NULL, TRUE, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL == hEvent)
			{
				return GetLastError();
			}
		}
		else if (command == L"start")
		{
			hEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL != hEvent)
			{
				if (::IsMinimized((HWND)hWnd)) 
					::SendMessage((HWND)hWnd, WM_SYSCOMMAND, SC_RESTORE, 0); 
				else 
					::ShowWindow((HWND)hWnd, SW_SHOW);

				return GetLastError();
			}
			hEvent = CreateEvent(NULL, TRUE, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL == hEvent)
			{
				return GetLastError();
			}
		}
		else if (command == L"stop")
		{
			hEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL == hEvent)
			{
				return GetLastError();
			}
	  
		   ::PostMessage((HWND)hWnd,WM_QUIT,hWnd,0);
		   return RT_OK;
		}
		else if (command == L"autostart")
		{
			hEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL != hEvent)
			{
				return GetLastError();
			}
			hEvent = CreateEvent(NULL, TRUE, FALSE, ONEBOX_INSTANCE_EVENT_ID);
			if (NULL == hEvent)
			{
				return GetLastError();
			}        
			return createMainFrame(hInstance, false);
		}
		else
		{
			return RT_INVALID_PARAM;
		}

		return createMainFrame(hInstance);
	}
	catch(...)
	{
		return RT_ERROR;
	}
}
