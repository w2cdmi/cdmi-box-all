#include "stdafxOnebox.h"
#include "NotifyIconMgr.h"
#include "SettingFrame.h"
#include "UserContext.h"
#include "AboutFrame.h"
#include "ConfigureMgr.h"
#include <Shellapi.h>
#include "AsyncTaskMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include <ZLibWrapLib.h>
#include "TransTaskMgr.h"
#include "WorkModeMgr.h"
#include "LoginMgr.h"
#include "MainFrame.h"
#include "SimpleNoticeFrame.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("NotifyIcon")
#endif

using namespace SD;

namespace Onebox
{
	const  wchar_t* TRAYICON_OFFLINE = L".\\skin\\Image\\tsk_offine.ico";
	const  wchar_t* TRAYICON_ONLINE = L".\\skin\\Image\\tsk_online.ico";
	const  wchar_t* TRAYICON_SYNC_ING = L".\\skin\\Image\\tsk_online.ico";
	const  wchar_t* TRAYICON_SYNC_PAUSE = L".\\skin\\Image\\tsk_online.ico";
	const  wchar_t* TRAYICON_SYNC_FAILED = L".\\skin\\Image\\tsk_online.ico";
	const  wchar_t* TRAYICON_SYSTEM_UPDATING = L".\\skin\\Image\\ic_upgrade.ico";

	static int32_t CopyFiles(const wchar_t *strSourcePath, const wchar_t *strDestPath)
	{
		if (NULL == strSourcePath || NULL == strDestPath)
		{
			return RT_INVALID_PARAM;
		}

		wchar_t src[MAX_PATH] = {0};
		wchar_t des[MAX_PATH] = {0};
		wcscpy_s(src, strSourcePath);
		wcscpy_s(des, strDestPath);

		SHFILEOPSTRUCT lpfile;
		ZeroMemory(&lpfile , sizeof(SHFILEOPSTRUCT));
		lpfile.hwnd = NULL;   
		lpfile.wFunc = FO_COPY;   
		lpfile.fFlags = FOF_NOCONFIRMATION|FOF_SILENT|FOF_NOERRORUI|FOF_NOCONFIRMMKDIR;   
		lpfile.pFrom = src;
		lpfile.pTo = des;   

		return SHFileOperation(&lpfile);
	}

	NotifyIconMgr::NotifyIconMgr(CNotifyIconUI* notifyIcon, UserContext* userContext, CPaintManagerUI& paintManager, MainFrame* mainframe)
		:userContext_(userContext)
		,notifyIcon_(notifyIcon)
		,paintManager_(paintManager)
		,m_mainFrame(mainframe)
	{
		showFlag_ = false;
	}

	NotifyIconMgr::~NotifyIconMgr(void)
	{

	}

	void NotifyIconMgr::updateStatus(const NotifyIconStatus status)
	{
		if (NULL == notifyIcon_)
		{
			return;
		}
		if (ONLINE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_ONLINE);
		}
		else if (OFFLINE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_OFFLINE);
		}
		else if (SYNCING == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_ING);
		}
		else if (PAUSE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_PAUSE);
		}
		else if (FAILED == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_FAILED);
		}
		else if( UPDATING == status )
		{
			 notifyIcon_->SetNotifyIcon(TRAYICON_SYSTEM_UPDATING);
		}
	}

	void NotifyIconMgr::updateStatus(const NotifyIconStatus status, LPCTSTR title)
	{
		if (NULL == notifyIcon_)
		{
			return;
		}
		if (ONLINE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_ONLINE, title);
		}
		else if (OFFLINE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_OFFLINE, title);
		}
		else if (SYNCING == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_ING, title);
		}
		else if (PAUSE == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_PAUSE, title);
		}
		else if (FAILED == status)
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYNC_FAILED, title);
		}
		else if( UPDATING == status )
		{
			notifyIcon_->SetNotifyIcon(TRAYICON_SYSTEM_UPDATING, title);
		}
	}

	void NotifyIconMgr::showBalloon(const MessageType type, const std::wstring& pstrTitle, const std::wstring& pstrContent)
	{
		if ( (SYSTEM == type) || (LOGIN == type) )
		{
			if ( GetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, true) )
			{
				paintManager_.GetRoot()->GetNotifyIcon()->ShowBalloon(type, pstrTitle.c_str(), pstrContent.c_str());
			}
		}
		else if ( RESOURCE == type )
		{
			if ( GetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, true) )
			{
				paintManager_.GetRoot()->GetNotifyIcon()->ShowBalloon(type, pstrTitle.c_str(), pstrContent.c_str());
			}
		}
	}

	void NotifyIconMgr::ShowMainFrame()
	{
		HWND hWndTemp = NULL;
		if ( !LoginMgr::LoginSuccess() )
		{
			if(::IsWindow(LoginMgr::getInstance(m_mainFrame->GetHWND())->GetHwnd()))
			{
				hWndTemp = LoginMgr::getInstance(m_mainFrame->GetHWND())->GetHwnd();
			}
		}
		else
		{
			hWndTemp = m_mainFrame->GetHWND();
		}
		if (::IsMinimized(hWndTemp))
		{
			::PostMessage(hWndTemp,WM_SYSCOMMAND, SC_RESTORE, 0); 
		}
		else
		{
			::ShowWindow(hWndTemp,SW_SHOW);
		}

		SetForegroundWindow(hWndTemp);
		m_mainFrame->SetSate(0);
	}

	bool NotifyIconMgr::proccessMessage(TNotifyUI& msg)
	{
		if (NULL == msg.pSender || msg.pSender->GetName() != L"notify_icon")
		{
			return false;
		}
		if (msg.sType == DUI_MSGTYPE_MENUADDED)
		{
			WorkMode wm = userContext_->getWorkmodeMgr()->getWorkMode();
			if ( WorkMode_Online != wm )
			{
				CMenuUI* pMenu = (CMenuUI*)msg.wParam;
				if (NULL == pMenu) return false;

				CMenuItemUI* menuitem = pMenu->GetMenuItemById(0);
				if (menuitem)
				{
					menuitem->SetEnabled(false);
					CLabelUI* showbox = static_cast<CLabelUI*>(menuitem->FindSubControl(L"notifyicon_showonebox"));
					if (showbox)
					{
						showbox->SetTextColor(0xff666666);
					}
				}
				menuitem = pMenu->GetMenuItemById(1);
				if (menuitem)
				{
					menuitem->SetEnabled(false);
					CLabelUI* showbox = static_cast<CLabelUI*>(menuitem->FindSubControl(L"notifyicon_settings"));
					if (showbox)
					{
						showbox->SetTextColor(0xff666666);
					}
				}
			}
			return true;
		}
		else if( msg.sType == DUI_MSGTYPE_NOTIFYICON_EVENT)
		{
			if ( 2 == msg.lParam)
			{
				ShowMainFrame();
			}
			else if ( NOTIFYICON_EDIT_SYSTEM_CONF == msg.lParam )
			{
				SetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, msg.wParam);
			}
			else if ( NOTIFYICON_EDIT_RESOURCE_CONF == msg.lParam )
			{
				SetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, msg.wParam);
			}
		}
		else if (msg.sType == DUI_MSGTYPE_MENUITEM_CLICK)
		{
			if ((int)msg.lParam == 0)
			{
				ShowMainFrame();
			}
			else if ( (int)msg.lParam == 1 )
			{
				ShowSettings(msg.wParam);
			}
			else if ( (int)msg.lParam == 2 )
			{
				BrowseOnebox();
			}
			else if ( (int)msg.lParam == 3 )
			{
				boost::thread collectLogsThread = boost::thread(boost::bind(&NotifyIconMgr::CollectLogs, this));

				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				if(NULL == simlpeNoticeFrame)
				{
					return false;
				}
				simlpeNoticeFrame->Show(Info, MSG_COLLECTLOGS_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			else if ( (int)msg.lParam == 4 )
			{
				Help();
			}
			else if ( (int)msg.lParam == 5 )
			{
				About();
			}
			else if ( (int)msg.lParam == 6 )
			{
				Exit();
			}
			return true;
		}
		return false;
	}

	void NotifyIconMgr::ShowSettings(int32_t opentype)
	{
		if (!m_mainFrame) return;

		if(showFlag_)
		{
			return;
		}

		CSettingFrame* settingFram = new CSettingFrame(userContext_, opentype);
		if (settingFram)
		{
			settingFram->Create(m_mainFrame->GetHWND(), _T("DUI_SETTING"), UI_CLASSSTYLE_DIALOG, WS_EX_WINDOWEDGE, CDuiRect());
			showFlag_ = true;

			if ( 0 == m_mainFrame->GetState())
			{
				settingFram->CenterWindow();
				settingFram->ShowModal();
			}
			else
			{
				RECT rcDlg = { 0 };
				::GetWindowRect(settingFram->GetHWND(), &rcDlg);

				MONITORINFO oMonitor = {};
				oMonitor.cbSize = sizeof(oMonitor);
				::GetMonitorInfo(::MonitorFromWindow(settingFram->GetHWND(), MONITOR_DEFAULTTONEAREST), &oMonitor);
				RECT rcArea = oMonitor.rcWork;

				int DlgWidth = rcDlg.right - rcDlg.left;
				int DlgHeight = rcDlg.bottom - rcDlg.top;

				// Find dialog's upper left based on rcCenter
				int xLeft = (rcArea.left + rcArea.right) / 2 - DlgWidth / 2;
				int yTop = (rcArea.top + rcArea.bottom) / 2 - DlgHeight / 2;

				::SetWindowPos( settingFram->GetHWND(), NULL, xLeft, yTop, -1, -1, true);
				settingFram->ShowModal();
			}

			delete settingFram;
			settingFram=NULL;
			showFlag_ = false;
		}
	}

	void NotifyIconMgr::Exit()
	{
		LoginMgr::getInstance(m_mainFrame->GetHWND())->Exit();
	}

	int32_t NotifyIconMgr::CollectLogsWrapprt()
	{
		// 1. create a temp directory to hold all the user data and logs
		// 2. compress the temp directory as a zip file
		// 3. delete the temp directory
		// 4. explorer the zip file
		std::wstring appPath = Utility::FS::get_system_user_app_path() + PATH_DELIMITER + ONEBOX_APP_DIR;
		std::wstring tmpName = ONEBOX_APP_DIR + std::wstring(L"_Log");
		std::wstring tmpPath = appPath + PATH_DELIMITER + tmpName;
		if (Utility::FS::is_directory(tmpPath))
		{
			(void)Utility::FS::remove_all(tmpPath);
		}
		// sleep for a while, or the next create maybe fail
		Sleep(500);

		tmpPath += L"\\logs";
		(void)Utility::FS::create_directories(tmpPath);

		// re-check if the temp path is already been created
		if (!Utility::FS::is_directory(tmpPath))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "failed to create the temp path.");
			return RT_ERROR;
		}

		// the install log and cbb log is not in user data path, should copy them manually
		int32_t ret = CopyFiles(L".\\install.log", (tmpPath + L"\\install.log").c_str());
		if (RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "failed to copy install log.");
		}
		ret = CopyFiles(L".\\cbb.log", (tmpPath + L"\\cbb.log").c_str());
		if (RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "failed to copy cbb log.");
		}

		// collect user data and the other user logs
		WIN32_FIND_DATA wfd = {0};
		HANDLE hFind = FindFirstFile(std::wstring(appPath + L"\\*").c_str(),&wfd);
		if(hFind==INVALID_HANDLE_VALUE)
		{
			ret = GetLastError();
			SERVICE_ERROR(MODULE_NAME, ret, "enum log directory failed.");
			return ret;
		}
		std::wstring tmp = L"";
		while (FindNextFile(hFind, &wfd))
		{
			tmp = wfd.cFileName;
			if (L"." == tmp || L".." == tmp || tmpName == tmp)
			{
				continue;
			}
			ret = CopyFiles(std::wstring(appPath + PATH_DELIMITER + tmp).c_str(), 
				std::wstring(tmpPath + PATH_DELIMITER + tmp).c_str());
			if (RT_OK != ret)
			{
				SERVICE_ERROR(MODULE_NAME, ret, "copy log file of %s failed.", 
					Utility::String::wstring_to_string(appPath + PATH_DELIMITER + tmp).c_str());
			}
		}
		(void)FindClose(hFind);

		// eg: "\\Onebox_Log\\Onebox_Log20150101000000.zip"
		SYSTEMTIME localTime = {0};
		GetLocalTime(&localTime);
		std::wstring zipFilePath = appPath + PATH_DELIMITER + tmpName + PATH_DELIMITER + tmpName + 
			Utility::String::format_string(L"%d%02d%02d%02d%02d%02d.zip", 
			localTime.wYear, localTime.wMonth, localTime.wDay, 
			localTime.wHour, localTime.wMinute, localTime.wSecond);
		if (!ZipCompress(Utility::String::wstring_to_string(tmpPath).c_str(), 
			Utility::String::wstring_to_string(zipFilePath).c_str()))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "failed to compress the log files.");
			(void)Utility::FS::remove_all(tmpPath);
			return RT_ERROR;
		}
		(void)Utility::FS::remove_all(tmpPath);
		(void)ShellExecute(NULL, L"open", L"explorer.exe", 
			std::wstring(L" /select," + zipFilePath).c_str(), 
			NULL, SW_SHOWNORMAL);
		return RT_OK;
	}

	void  NotifyIconMgr::CollectLogs()
	{
		int32_t ret = RT_OK;

		ret = CollectLogsWrapprt();
		if(RT_OK == ret)
		{

		}
		else
		{
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
			if(NULL == simlpeNoticeFrame)
			{
				return;
			}
			simlpeNoticeFrame->Show(Info, MSG_COLLECTLOGS_FAILED_KEY);
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}
	}

	void NotifyIconMgr::BrowseOnebox()
	{
		if ( m_strUserUrl.IsEmpty() )
		{
			CDuiString suserUrl = userContext_->getConfigureMgr()->getConfigure()->serverUrl().c_str();
			m_strUserUrl = suserUrl.Left(suserUrl.GetLength()-6);
		}
		(void)ShellExecute(NULL,L"open",L"explorer.exe", m_strUserUrl, NULL,SW_SHOWNORMAL);
	}

	void  NotifyIconMgr::Help()
	{
		if ( m_strUserUrl.IsEmpty() )
		{
			CDuiString suserUrl = userContext_->getConfigureMgr()->getConfigure()->serverUrl().c_str();
			m_strUserUrl = suserUrl.Left(suserUrl.GetLength()-6);
		}
		if( (int32_t)UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage() )
		{
			(void)ShellExecute(NULL,L"open",L"explorer.exe", m_strUserUrl + DEFAULT_USER_HELP_WEB_CH, NULL,SW_SHOWNORMAL);
		}
		else
		{
			(void)ShellExecute(NULL,L"open",L"explorer.exe", m_strUserUrl + DEFAULT_USER_HELP_WEB_EN, NULL,SW_SHOWNORMAL);
		}
	}

	void  NotifyIconMgr::About()
	{
		if ( CAboutFrame::isOpenAboutFrm() )
		{
			CAboutFrame::delOpenAboutFrm();
		}
		std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
		Configure* config =  configureMgr->getConfigure();	
		CAboutFrame* aboutFrm = new CAboutFrame(userContext_);
		aboutFrm->SetVersionNum(config->version().c_str());
		aboutFrm->Create(NULL, _T("DUI_ABOUT"), UI_CLASSSTYLE_CHILD, WS_EX_TOPMOST, CDuiRect());
		aboutFrm->CenterWindow();
		aboutFrm->ShowWindow(true);
	}
}
