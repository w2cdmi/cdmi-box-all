#include "stdafxOnebox.h"
#include "TrayIcon.h"
#include <Shellapi.h>
#include <string.h>
#include <strsafe.h>
#include "AsyncTaskMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include <ZLibWrapLib.h>
#include "TransTaskMgr.h"
#include <shellapi.h>

#ifndef MODULE_NAME
#define MODULE_NAME ("TrayIcon")
#endif

#define TRAYTIP L"Onebox_Mate"

using namespace SD;

namespace Onebox
{
	const  wchar_t* TrayIconStauts::TRAYICON_OFFLINE = L".\\Res\\tsk_offine.ico";
	const  wchar_t* TrayIconStauts::TRAYICON_ONLINE = L".\\Res\\tsk_online.ico";
	const  wchar_t* TrayIconStauts::TRAYICON_SYNC_ING = L".\\Res\\tsk_sync_ing.ico";
	const  wchar_t* TrayIconStauts::TRAYICON_SYNC_PAUSE = L".\\Res\\tsk_sync_pause.ico";
	const  wchar_t* TrayIconStauts::TRAYICON_SYNC_FAILED = L".\\Res\\tsk_sync_failed.ico";

	class CTrayIcon:public TrayIconMgr
	{
	public:
		CTrayIcon();
		~CTrayIcon();
	public://操作
		void DoEvent(TRAY_ICON_USER_MESSAGE nEvent);
		void SetTrayState(TrayStauts trayStauts );
		void Dispose();
		static void SetHWND(HWND hWnd);
		BOOL Init();
		BOOL AddMenu();
	    BOOL ShowBalloon(DWORD dwStype, std::wstring strTitle, std::wstring strContent);
	private:
		void SetTrayIcon(const wchar_t* trayState);
		void  SaveLog();
		BOOL Modify(HICON hIcon);
		static int32_t CopyFiles(const wchar_t * strSourcePath,const wchar_t * strDestPath);
	private:
		static HWND m_hWnd;
		HICON m_hIcon;
		int m_taskState;
		bool m_isUseOpenLog;
		bool m_isEnablePartMenu;
	};

	HWND CTrayIcon::m_hWnd =NULL;

	CTrayIcon::CTrayIcon()
	{
		m_taskState = TaskStauts::PAUSE;
		m_hIcon =  (HICON)LoadImage(NULL, TrayIconStauts::TRAYICON_OFFLINE, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);  
		m_isUseOpenLog = false;
		m_isEnablePartMenu = false;

		Init();
	}

	CTrayIcon::~CTrayIcon()
	{
		DeleteObject(m_hIcon); 
	}

	 void CTrayIcon::SetHWND(HWND hWnd)
	{
		m_hWnd = hWnd;
	}

	BOOL  CTrayIcon::Init()
	{
		BOOL  bRet = FALSE;
		NOTIFYICONDATA nID = {};
		nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon =m_hIcon;
		wcsncpy_s(nID.szTip, TRAYTIP, sizeof(TRAYTIP));          
		nID.hWnd = m_hWnd;                                                                               
		nID.uID = 1;                                                                                     
		nID.uFlags = NIF_GUID | NIF_ICON | NIF_MESSAGE | NIF_TIP;    
		nID.uCallbackMessage=WM_NOTIFYICON;
		bRet = Shell_NotifyIcon(NIM_ADD, &nID); 
		if (!bRet)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR,"Init Icon failed.");
			//TODO 打印Error GetLastError()
		}
		
		return bRet;
	}

	int32_t CTrayIcon::CopyFiles(const wchar_t * strSourcePath,const wchar_t * strDestPath)
	{
		TCHAR szBuffSrc[MAX_PATH] = {0};
		TCHAR szBuffDes[MAX_PATH] = {0};
		StringCchCopy(szBuffSrc,MAX_PATH,strSourcePath);
		StringCchCopy(szBuffDes,MAX_PATH,strDestPath);
		SHFILEOPSTRUCT lpfile;
		ZeroMemory(&lpfile , sizeof(SHFILEOPSTRUCT));
		lpfile.hwnd = NULL;   
		lpfile.wFunc = FO_COPY;   
		lpfile.fFlags = FOF_NOCONFIRMATION|FOF_SILENT|FOF_NOERRORUI|FOF_NOCONFIRMMKDIR;   
		lpfile.pFrom = szBuffSrc;
		lpfile.pTo = szBuffDes;   
		lpfile.fAnyOperationsAborted = TRUE;

		if(ERROR_SUCCESS != SHFileOperation(&lpfile))
		{
			return GetLastError();
		}
		return ERROR_SUCCESS;	
	}


	BOOL CTrayIcon::AddMenu()
	{
		BOOL bRet = FALSE;
		POINT pt; 
		GetCursorPos(&pt); 

		CMenuUI* pMenu = new CMenuUI;	//menu
		pMenu->InitWnd(pt, m_hWnd, NULL, 30);
		pMenu->SetItemWidth(180);
		if ((int)TaskStauts::PAUSE == m_taskState)
		{			
			pMenu->Add(MENU_SUSPEND_KEY, TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_OPERATOR,L"file='..\\img\\tary_menu_stopSync.png' dest='4,2,20,18'",m_isEnablePartMenu);
		} 
		else if((int)TaskStauts::RESUME == m_taskState)
		{			
			pMenu->Add(MENU_RECOVER_KEY, TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_OPERATOR,L"file='..\\img\\tary_menu_startSync.png' dest='4,2,20,18'",m_isEnablePartMenu);
		}
		
		pMenu->Add(MENU_OPENCLIENT_KEY, TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_OPTION,L"file='..\\img\\tary_menu_setting.png' dest='4,2,20,18'",m_isEnablePartMenu);
		pMenu->AddLine();
		pMenu->Add(MENU_OPENWEB_KEY,TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_OPEN_WEB,L"file='..\\img\\tary_menu_openIE.png' dest='4,2,20,18'");
		std::wstring str_pngPath = L"";
		if (m_isUseOpenLog)
		{
			str_pngPath = L"";//灰色不可点击
		}
		else
		{
			str_pngPath = L"";//亮色可点击
		}		
		pMenu->Add(MENU_COLLENTLOG_KEY,TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_COLLECT_LOG,str_pngPath.c_str(),!m_isUseOpenLog);

		pMenu->AddLine();
		pMenu->Add(MENU_HELP_KEY,TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_HELP);
		pMenu->Add(MENU_ABOUT_KEY,TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_ABOUT);
		pMenu->Add(MENU_EXIT_KEY,TRAY_ICON_USER_MESSAGE::TRAY_ICON_WM_USER_MSG_QUIT,L"file='..\\img\\tary_menu_exit.png' dest='4,4,20,16'");
		pMenu->UpdateWnd(true);
		pMenu->ShowWindow(true);
		return bRet;
	}

	BOOL  CTrayIcon::Modify(HICON hIcon)
	{
		BOOL  bRet = FALSE;
		NOTIFYICONDATA nID = {};
		nID.cbSize =  (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon = hIcon;
		wcsncpy_s(nID.szTip, TRAYTIP, sizeof(TRAYTIP));          
		nID.hWnd = m_hWnd;                                                                               
		nID.uID = 1;                                                                                     
		nID.uFlags = NIF_GUID | NIF_ICON | NIF_MESSAGE | NIF_TIP|NIF_INFO;    
		nID.uCallbackMessage=WM_NOTIFYICON;
		bRet = Shell_NotifyIcon(NIM_MODIFY, &nID); 
		if (!bRet)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR,"Modify Icon failed.");
			//TODO 打印Error GetLastError()
		}

		return bRet;
	}

	BOOL CTrayIcon::ShowBalloon(DWORD dwStype,std::wstring strTitle, std::wstring strContent)
	{
		if ((int)UI_BALLOON::BALLOON_NO == GetUserConfValue(CONF_SETTINGS_SECTION, CONF_BUBBLEREMIND_KEY, (int)UI_BALLOON::BALLOON_NO))
		{
			return true;
		}
		BOOL  bRet = FALSE;
		NOTIFYICONDATA nID = {};
		nID.cbSize =  (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon =m_hIcon;
		wcsncpy_s(nID.szTip, TRAYTIP, sizeof(TRAYTIP));          
		nID.hWnd = m_hWnd;                                                                               
		nID.uID = 1;                                                                                     
		nID.uFlags = NIF_ICON | NIF_MESSAGE | NIF_TIP|NIF_INFO;    
		nID.uCallbackMessage=WM_NOTIFYICON;
		wcsncpy_s(nID.szInfoTitle,strTitle.c_str(),strTitle.length());
		wcsncpy_s(nID.szInfo,strContent.c_str(),strContent.length());
		nID.dwInfoFlags  = dwStype;
		nID.uTimeout=5000;  
		bRet = Shell_NotifyIcon(NIM_MODIFY, &nID); 
		if (!bRet)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR,"Init Icon failed.");
			//TODO 打印Error GetLastError()
		}

		return bRet;
	}

	void CTrayIcon::Dispose()
	{
		BOOL  bRet = FALSE;
		NOTIFYICONDATA nID = {};
		nID.hIcon = m_hIcon;
		wcsncpy_s(nID.szTip, TRAYTIP, sizeof(TRAYTIP));          
		nID.hWnd = m_hWnd;                                                                               
		nID.uID = 1;                                                                                     
		nID.uFlags = NIF_GUID | NIF_ICON | NIF_MESSAGE | NIF_TIP;    
		nID.uCallbackMessage=WM_NOTIFYICON;
		bRet = Shell_NotifyIcon(NIM_DELETE, &nID); 
	}
	
	void CTrayIcon::SetTrayIcon(const wchar_t* trayState)
	{
		m_hIcon =  (HICON)LoadImage(NULL, trayState, IMAGE_ICON, 0, 0, LR_LOADFROMFILE); 
		Modify(m_hIcon);
	}

	void CTrayIcon::SetTrayState(TrayStauts trayStauts)
	{
		if (TRAY_ONLINE == trayStauts)
		{
			m_isEnablePartMenu = true;
			SetTrayIcon(TrayIconStauts::TRAYICON_ONLINE);
		}
		else if (TRAY_OFFLINE == trayStauts)
		{
			m_isEnablePartMenu = false;
			SetTrayIcon(TrayIconStauts::TRAYICON_OFFLINE);
		}
		else if (TRAY_SYNCING == trayStauts)
		{
			m_isEnablePartMenu = true;
			SetTrayIcon(TrayIconStauts::TRAYICON_SYNC_ING);
		}
		else if (TRAY_PAUSE == trayStauts)
		{
			m_isEnablePartMenu = true;
			SetTrayIcon(TrayIconStauts::TRAYICON_SYNC_PAUSE);
		}
		else if (TRAY_FAILED == trayStauts)
		{
			m_isEnablePartMenu = false;
			SetTrayIcon(TrayIconStauts::TRAYICON_SYNC_FAILED);
		}
	}

	void  CTrayIcon::SaveLog()
	{
		m_isUseOpenLog = true;
		std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
		Configure* config =  configureMgr->getConfigure();				
		std::wstring  str_appPath = config->appPath();
		std::wstring str_logPath = str_appPath + L"\\OneboxLog\\Log";
		Utility::FS::remove_all(str_appPath + L"\\OneboxLog");
		Utility::FS::create_directories(str_logPath);
		_finddata_t fileInfo;
		std::string str_file = Utility::String::wstring_to_string(str_appPath) + "\\*";
		long handle = _findfirst(str_file.c_str(),&fileInfo);
		if (-1 == handle)
		{
			return;
		}
		do 
		{
			std::string str_name = fileInfo.name;
			//if (str_name.length() < 4) continue;
			if ((0 == str_name.compare("UserData") || 0 == str_name.compare("Log"))  && fileInfo.attrib == _A_SUBDIR)
			{
				std::wstring str_oldPath = str_appPath + L"\\"+ Utility::String::string_to_wstring(str_name);
				std::wstring str_newPath = str_logPath + L"\\"+ Utility::String::string_to_wstring(str_name);
				if (RT_OK !=CopyFiles(str_oldPath.c_str(),str_newPath.c_str()))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "copy folder failed, foldername: %s", 
						(Utility::String::wstring_to_string(str_oldPath)).c_str());
				}				
			}
			if (str_name.length() >=4 && 0 == Utility::String::to_lower(str_name.substr(str_name.length()-4,4)).compare(".dmp") || -1 != str_name.find(".log"))
			{
				std::wstring str_oldPath = str_appPath + L"\\"+ Utility::String::string_to_wstring(str_name);
				std::wstring str_newPath = str_logPath + L"\\"+ Utility::String::string_to_wstring(str_name);
				int32_t ret = -1;
				if (fileInfo.attrib == _A_SUBDIR)
				{
					ret = CopyFiles(str_oldPath.c_str(),str_newPath.c_str());
				}
				else
				{
					ret = Utility::FS::copy_file(str_oldPath,str_newPath);
				}
				if (RT_OK != ret)
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "copy file failed, filename: %s", 
						(Utility::String::wstring_to_string(str_oldPath)).c_str());
				}
			}
		} while (0 == _findnext(handle,&fileInfo));
		std::wstring str_zipPath = str_logPath.substr(0 , str_logPath.find_last_of(L"\\"));
		SYSTEMTIME sysTime; 
		FILETIME ft = {0,0};
		ULARGE_INTEGER ularge;
		::GetSystemTime(&sysTime);
		::SystemTimeToFileTime(&sysTime,&ft);
		ularge.LowPart = ft.dwLowDateTime;
		ularge.HighPart = ft.dwHighDateTime;
		int64_t currentTime = ularge.QuadPart;
		std::wstring str_currentTime = SD::Utility::DateTime::getTime(currentTime).c_str();
		str_currentTime = Utility::String::replace_all(str_currentTime,L"/",L"");
		str_currentTime = Utility::String::replace_all(str_currentTime,L" ",L"");
		str_currentTime = Utility::String::replace_all(str_currentTime,L":",L"");
		std::wstring str_zipName = str_zipPath+L"\\OneboxLog_"+str_currentTime+L".zip";
		std::string strZipFile = SD::Utility::String::wstring_to_string(str_logPath);
		std::string strDecFolder = SD::Utility::String::wstring_to_string(str_zipName);
		if (ZipCompress(strZipFile.c_str(),strDecFolder.c_str()))
		{
			Utility::FS::remove_all(str_logPath);
			std::wstring openParam = L" /select," + str_zipName;
			(void)ShellExecuteW(NULL,L"open",L"explorer.exe",openParam.c_str(),NULL,SW_SHOWNORMAL);
			m_isUseOpenLog = false;
			SERVICE_INFO(MODULE_NAME, RT_OK, "save log end");
		}
	}

	void CTrayIcon::DoEvent(TRAY_ICON_USER_MESSAGE nEvent)
	{
		switch (nEvent)
		{
		case TRAY_ICON_WM_USER_MSG:
			break;
		case TRAY_ICON_WM_USER_MSG_ABOUT:
			{
				if ( CAboutFrame::isOpenAboutFrm() )
				{
					CAboutFrame::delOpenAboutFrm();
				}
				std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
				Configure* config =  configureMgr->getConfigure();	
				CAboutFrame* aboutFrm = new CAboutFrame;
				aboutFrm->SetVersionNum(config->version().c_str());
				aboutFrm->Create(NULL, _T("DUI_ABOUT"), UI_CLASSSTYLE_CHILD, WS_EX_TOPMOST, CDuiRect());
				aboutFrm->CenterWindow();
				aboutFrm->ShowWindow(true);
			}
			break;
		case TRAY_ICON_WM_USER_MSG_QUIT:
			PostQuitMessage(0);
			break;
		case TRAY_ICON_WM_USER_MSG_HELP:
			{
				std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
				Configure* config =  configureMgr->getConfigure();				
				std::wstring  str_server = config->serverUrl();
				str_server = str_server.substr(0,str_server.length()-6);
				str_server += L"static/help/zh/helpcenter.html";
				(void)ShellExecuteW(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
			}
			break;
		case TRAY_ICON_WM_USER_MSG_COLLECT_LOG:
			{
				if (!m_isUseOpenLog)
				{
					SERVICE_INFO(MODULE_NAME, RT_OK, "save log begin");
					boost::thread(boost::bind(&CTrayIcon::SaveLog, this));
				}
			}
			break;
		case TRAY_ICON_WM_USER_MSG_OPEN_WEB:
			{
				std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
				Configure* config =  configureMgr->getConfigure();				
 				std::wstring  str_server = config->serverUrl();
				str_server = str_server.substr(0,str_server.length()-6);
				if (str_server.empty()) return;
				(void)ShellExecuteW(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
			}
			break;
		case TRAY_ICON_WM_USER_MSG_OPTION:
			{
				if (m_isEnablePartMenu)
				{
					if (::IsMinimized(m_hWnd))
					{
						::PostMessage(m_hWnd,WM_SYSCOMMAND, SC_RESTORE, 0); 
					}
					else
					{
						::ShowWindow(m_hWnd,SW_SHOW);
						SetForegroundWindow(m_hWnd);
					}
				}
			}
			break;
		case TRAY_ICON_WM_USER_MSG_OPERATOR:
			{
				if (m_isEnablePartMenu)
				{
					if (TaskStauts::PAUSE  == m_taskState)
					{
						int32_t ret = TransTaskMgr::pauseAllTask();
						if (RT_OK == ret)
						{						
							m_taskState = TaskStauts::RESUME ;
							SetTrayIcon(TrayIconStauts::TRAYICON_SYNC_PAUSE);
						}
						else
						{
							SERVICE_ERROR(MODULE_NAME, RT_ERROR, "cancelAllTask failed.");
							// show error message
							// ...
							return;
						}
					}
					else if(TaskStauts::RESUME == m_taskState)
					{
						int32_t ret = TransTaskMgr::resumeAllTask();
						if (RT_OK == ret)
						{			
							m_taskState = TaskStauts::PAUSE;
							SetTrayIcon(TrayIconStauts::TRAYICON_ONLINE);
						}
						else
						{		
							SERVICE_ERROR(MODULE_NAME, RT_ERROR, "resumeAllTask failed.");	
							// show error message
							// ...
							return;
						}
					}
				}
			}			
			break;
		default:
			break;
		}
	}

	std::auto_ptr<TrayIconMgr> TrayIconMgr::instance_(NULL);
	TrayIconMgr* TrayIconMgr::getInstance()
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new CTrayIcon());
		}
		return instance_.get();
	}

	void TrayIconMgr::SetHWND(HWND hWnd)
	{
		CTrayIcon::SetHWND(hWnd);
	}
}