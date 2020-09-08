#include "stdafxOnebox.h"
#include "MainFrame.h"
#include "ControlNames.h"
#include "SkinConfMgr.h"
#include "Share2MeMgr.h"
#include "LeftRegionMgr.h"
#include "MyFileMgr.h"
#include "MyShareMgr.h"
#include "TeamSpaceMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "TransTaskMgr.h"
#include "LoginMgr.h"
#include "AsyncTaskCommon.h"
#include "Utility.h"
#include "NotifyMgr.h"
#include "NoticeFrame.h"
#include "UploadFrame.h"
#include "ThriftServiceImpl.h"
#include "SimpleNoticeFrame.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "UserInfoMgr.h"
#include "BackupAllMgr.h"
#include "SyncService.h"
#include "AsyncTaskMgr.h"
#include "SettingFrame.h"
#include "MsgFrame.h"
#include "MsgListener.h"
#include "PathMgr.h"
#include "RestFile.h"
#include "SyncFileSystemMgr.h"
#include "ProxyMgr.h"
#include "WorkModeMgr.h"
#include "RestTaskMgr.h"
#include "resource.h"
#include <boost/lexical_cast.hpp>
#include "UIScaleImgButton.h"
#include "UIUserPhoto.h"
#include "NotifyIconMgr.h"
#include "Configure.h"
#include "ConfigureMgr.h"
#include "FullBackUpMgr.h"
#include "CSystemInfoMgr.h"
#include <boost/thread.hpp>
#include "UpgradeMgr.h"
#include "UpdateDB.h"
#include "BackupGuideUpgrade.h"
#include "BackupGuideInstall.h"
#include "loginInfo.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("OneboxMainFrame")
#endif

#define REFRESH_NOTIFYICON_TIMERID 1000
#define NOTIFY_MSG_START_PLAY_TIMERID 1001

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(MainFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SETFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_HEADERCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MOUSEENTER,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MOUSELEAVE,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_RETURN,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_TEXTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDRAGMOVE,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDRAGCOPY,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDRAGFILE,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTITEMCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_DELETE,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTALL,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_NEXTPAGE,executeFunc)
		//DUI_ON_MSGTYPE(DUI_MSGTYPE_LASTPAGE,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_BACK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_LISTSELECTALL,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MENUITEM_CLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MENUADDED,executeFunc)	
		DUI_END_MESSAGE_MAP()

		MainFrame::MainFrame(const std::wstring& data, const std::wstring& token)
		:mainUserContext_(NULL)
		,share2MeMgr_(NULL)
		,leftRegionMgr_(NULL)
		,myFileMgr_(NULL)
		,myShareMgr_(NULL)
		,transTaskMgr_(NULL)
		,teamSpaceMgr_(NULL)
		,backUpMgr_(NULL)
		,msgFrame_(NULL)
		,data_(data)
		,token_(token)
		,isUpdating_(false)
		,m_hWndTip(NULL)
		,notifyIconMgr_(NULL)
		,m_State(0)
		,fullBackUpMgr_(NULL)
		,systemInfoMgr_(NULL)
	{
		m_uMsgTaskbarRestart = 0;

		m_InfoTip.cbSize = sizeof(TOOLINFO);
		m_InfoTip.uFlags = TTF_IDISHWND;
		m_InfoTip.hwnd = NULL;
		m_InfoTip.uId = 0;
		RECT rect = {0,0,0,0};
		m_InfoTip.rect = rect;
		m_InfoTip.hinst = NULL;
		m_InfoTip.lpszText = NULL;
		m_InfoTip.lParam = 0;
		m_InfoTip.lpReserved = NULL;
	}

	MainFrame::~MainFrame()
	{
		// release memory
		if (share2MeMgr_)
		{
			delete share2MeMgr_;
			share2MeMgr_ = NULL;
		}
		if (leftRegionMgr_)
		{
			delete leftRegionMgr_;
			leftRegionMgr_ = NULL;
		}
		if (myFileMgr_)
		{
			delete myFileMgr_;
			myFileMgr_ = NULL;
		}
		if (myShareMgr_)
		{
			delete myShareMgr_;
			myShareMgr_ = NULL;
		}
		if (transTaskMgr_)
		{
			delete transTaskMgr_;
			transTaskMgr_ = NULL;
		}
		if (teamSpaceMgr_)
		{
			delete teamSpaceMgr_;
			teamSpaceMgr_ = NULL;
		}
		if (notifyIconMgr_)
		{
			delete notifyIconMgr_;
			notifyIconMgr_ = NULL;
		}

		if (fullBackUpMgr_)
		{
			fullBackUpMgr_->stop();
		}

		if( systemInfoMgr_ )
		{
			delete systemInfoMgr_;
			systemInfoMgr_ = NULL;
		}
		// menu TODO...
		//TrayIconMgr::getInstance()->Dispose();
		// stop thrift service
		(void)stopService();

		if (NULL != mainUserContext_)
		{
			(void)MsgListener::getInstance(mainUserContext_)->stop();
			UserContextMgr::releaseInstance();
		}

		if (fullBackUpMgr_)
		{
			delete fullBackUpMgr_;
			fullBackUpMgr_ = NULL;
		}

		LogoutType logoutType = (LogoutType)GetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::NO);
		if (LogoutType::YES==logoutType || LogoutType::RESTART==logoutType || LogoutType::CHANGELANG==logoutType)
		{
			std::wstring ExecFilePath = L"\"" + GetInstallPath()+ ONEBOX_APP_NAME_EXT + L"\"";
			(void)ShellExecute(0,L"open",ExecFilePath.c_str() ,  L"restore", NULL, SW_SHOWNORMAL); 
		}
	}

	void MainFrame::InitWindow()
	{
		ShowWindow(FALSE);
		SetIcon(IDI_ICON1);

		mainUserContext_ = UserContextMgr::getInstance()->createUserContext((int64_t)this->GetHWND());
		m_uMsgTaskbarRestart = RegisterWindowMessage(_T("TaskbarCreated"));

		if (NULL == m_PaintManager.GetRoot()->GetNotifyIcon()) {
			LPCTSTR pDefaultAttributes = m_PaintManager.GetDefaultAttributeList(_T("NotifyIcon"));
			if( pDefaultAttributes ) {
				CNotifyIconUI* pNotifyIcon = new CNotifyIconUI;
				if (NULL != pNotifyIcon) {
					m_PaintManager.GetRoot()->SetNotifyIcon(pNotifyIcon);
					pNotifyIcon->ApplyAttributeList(pDefaultAttributes);
					pNotifyIcon->SetManager(&m_PaintManager, m_PaintManager.GetRoot(), true);
					notifyIconMgr_ = new NotifyIconMgr(pNotifyIcon, mainUserContext_, m_PaintManager, this);
					::SetTimer(*this, REFRESH_NOTIFYICON_TIMERID, 5000, NULL);
				}
			}
		}
		notifyIconMgr_->updateStatus(OFFLINE);

		bool bLogin = LoginMgr::getInstance(this->GetHWND())->Login(mainUserContext_,m_PaintManager);
		if(NULL==mainUserContext_) return;
		if (!bLogin)
		{
			::PostQuitMessage(0);
			return;
		}

		LoginInfo::getInstance(mainUserContext_)->Show();
		UpgradeMgr::getInstance(mainUserContext_, m_hWnd)->Run();
		if( UpdateDBMgr::getInstance(mainUserContext_)->isUpdate() )
		{
			isUpdating_ = true;
			updateThread_ = boost::thread(boost::bind(&MainFrame::update, this));
			int32_t timeCnt = 0;
			while(isUpdating_&&timeCnt<100)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(10));
				++timeCnt;
			}
			if(isUpdating_)
			{
				std::wstring strTitle = iniLanguageHelper.GetMsgTitle(MSG_SHARE_SYSTEM_UPDATE_TITLE);
				std::wstring strContent = iniLanguageHelper.GetMsgDesc(MSG_SYSTEM_UPDATING);
				notifyIconMgr_->updateStatus(UPDATING, strContent.c_str());
				notifyIconMgr_->showBalloon(SYSTEM, strTitle, SD::Utility::String::ltrim(strContent,strTitle));
				updateThread_.join();
			}
		}

		// start thrift service
		int32_t ret = startService();
		if (RT_OK != ret)
		{
			::PostQuitMessage(0);
			return;
		}

		// init async trans task
		ret = mainUserContext_->getAsyncTaskMgr()->init();
		if (RT_OK != ret)
		{
			::PostQuitMessage(0);
			return;
		}

		ret = MsgListener::getInstance(mainUserContext_)->start();
		if (RT_OK != ret)
		{
			::PostQuitMessage(0);
			return;
		}

		myFileMgr_ = MyFileMgr::getInstance(mainUserContext_, m_PaintManager);
		leftRegionMgr_ = LeftRegionMgr::getInstance(mainUserContext_, m_PaintManager);
		share2MeMgr_ = Share2MeMgr::getInstance(mainUserContext_, m_PaintManager);
		myShareMgr_= MyShareMgr::getInstance(mainUserContext_,m_PaintManager);
		teamSpaceMgr_= TeamSpaceMgr::getInstance(mainUserContext_,m_PaintManager);
		msgFrame_ = MsgFrame::getInstance(mainUserContext_,m_PaintManager);
		fullBackUpMgr_ = FullBackUpMgr::getInstance(mainUserContext_,m_PaintManager);
		transTaskMgr_ = TransTaskMgr::create(mainUserContext_, m_PaintManager);

		// change work mode
		ret = mainUserContext_->getWorkmodeMgr()->changeWorkMode(WorkMode_Online);
		if (RT_OK != ret)
		{
			::PostQuitMessage(0);
			return;
		}

		std::wstring str_name;		
		StorageUserInfo userInfo;
		if (RT_OK == mainUserContext_->getUserInfoMgr()->getCurUserInfo(userInfo))
			str_name = SD::Utility::String::utf8_to_wstring(userInfo.name);
		CScaleImgButtonUI* pUserName = static_cast<CScaleImgButtonUI*>(m_PaintManager.FindControl(L"btn_userName"));
		if (NULL != pUserName)
		{
			SERVICE_INFO("MainFrame", RT_OK, "userName %s", SD::Utility::String::wstring_to_string(str_name).c_str());
			pUserName->SetText(str_name.c_str());
			pUserName->SetToolTip(str_name.c_str());
		}

		leftRegionMgr_->initData();
		//myFileMgr_->initData();

		MsgTypeList msgTl;
		CButtonUI* pTip = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"msgTip"));
		if(NULL != pTip)
		{
			int64_t nCount = ProxyMgr::getInstance(mainUserContext_)->hasUnRead(msgTl);
			if (nCount <= 0)
			{
				pTip->SetVisible(false);
			}
			if (nCount > 0)
			{
				pTip->SetFixedHeight(16);
				CDuiString strCount;
				if (nCount > 99)
				{
					strCount = _T("99+");
					pTip->SetFixedWidth(30);
					pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,52,30,68'"));
				}
				else if (nCount > 9)
				{
					strCount.Format(_T("%d"), nCount);
					pTip->SetFixedWidth(23);
					pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,26,23,42'"));
				}
				else
				{
					strCount.Format(_T("%d"), nCount);
					pTip->SetFixedWidth(16);
					pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,0,16,16'"));
				}
				pTip->SetText(strCount);
				pTip->SetVisible(true);
			}
		}

		notifyIconMgr_->updateStatus(ONLINE);

		systemInfoMgr_ = CCSystemInfoMgr::Create();

		COptionUI* pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_backup"));
		if (NULL!= pSelect && !IsAdministratorUser())
		{
				pSelect->SetVisible(false);
		}

		CenterWindow();
		ShowWindow();

		bool bDefault = true;
		bool isFirstRun = GetUserConfValue(CONF_USERINFO_SECTION, CONF_IS_FIRST_RUN_KEY, bDefault); 

		if( isFirstRun && IsAdministratorUser() )
		{
			::SetTimer(*this, NOTIFY_MSG_START_PLAY_TIMERID, 300, NULL);
			SetUserConfValue(CONF_USERINFO_SECTION, CONF_IS_FIRST_RUN_KEY, false); 
		}

	}

	LPCTSTR  MainFrame::GetWindowClassName(void) const
	{
		return ControlNames::WND_CLS_NAME;
	}

	CDuiString MainFrame::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString MainFrame::GetSkinFile()
	{
		return ControlNames::SKIN_XML_MAIN_FILE;
	}

	CControlUI* MainFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool MainFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT MainFrame::MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled)
	{
		if (uMsg == WM_KEYDOWN)
		{
			switch (wParam)
			{
			case VK_F5:
				{
					TNotifyUI Msg;
					if (Page_MyFile == leftRegionMgr_->getCurPageType())
					{
						myFileMgr_->updateClick(Msg);
						msgFrame_->refreshClick();
					}
					else if (Page_Share2Me == leftRegionMgr_->getCurPageType())
					{
						share2MeMgr_->flushClick(Msg);
					}
					else if (Page_TeamSpace == leftRegionMgr_->getCurPageType())
					{
						teamSpaceMgr_->updateClick(Msg);
					}
					else if (Page_MyShare == leftRegionMgr_->getCurPageType())
					{
						myShareMgr_->flushClick(Msg);
					}
				}
				break;
			case VK_RETURN:
			case VK_ESCAPE:
				return ResponseDefaultKeyEvent(wParam);
			default:
				break;
			}
		}
		return FALSE;
	}

	LRESULT MainFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;
		if ( uMsg == m_uMsgTaskbarRestart )  
		{  
			// menu TODO...
			/*TrayIconMgr::getInstance()->Dispose();
			TrayIconMgr::getInstance()->Init();*/
		}  

		switch (uMsg)
		{
			/*case  USERBASEINFO_WM_USER_MSG_SETTING:
			{
			CSettingFrame* settingFram = new CSettingFrame(mainUserContext_);
			if (NULL != settingFram)
			{
			settingFram->Create(m_hWnd, _T("DUI_SETTING"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE, CDuiRect());
			settingFram->CenterWindow();
			settingFram->ShowModal();
			delete settingFram;
			settingFram=NULL;
			}
			}
			break;
			case  USERBASEINFO_WM_USER_MSG_LOGOUT:
			{
			SetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogOut::YES);
			PostQuitMessage(0);
			}
			break;*/
		case WM_SYSCOMMAND:
			lRes = OnSysCommand(uMsg, wParam, lParam, bHandled);
			break;
		case WM_NCHITTEST:
			lRes = OnNcHitTest(uMsg, wParam, lParam, bHandled);
			break;
		case WM_COPYDATA:
			lRes = OnHandleCopyData(uMsg, wParam, lParam, bHandled);
			break;
		case WM_TIMER:
			{
				if(UI_TIMERID::SIMPLENOTICE_TIMERID == wParam)
				{
					OnTimer(wParam);
					break;
				}
				else if (REFRESH_NOTIFYICON_TIMERID == wParam)
				{
					NotifyIconProcess();
					break;
				}
				else if ( NOTIFY_MSG_START_PLAY_TIMERID == wParam )
				{
					::KillTimer(this->GetHWND(), wParam);

					::ShowWindow(m_hWnd, SW_SHOW);
					SetForegroundWindow(m_hWnd);

					if( BackupAllMgr::getInstance(mainUserContext_)->isUpdate() )
					{
						CBackupGuideUpgrade backupgrade1(m_PaintManager);
						backupgrade1.Create(this->m_hWnd, _T("CBackupGuideUpgrade"), UI_CLASSSTYLE_DIALOG, WS_EX_WINDOWEDGE, CDuiRect());
						backupgrade1.CenterWindow();
						backupgrade1.ShowModal();
					}
					else
					{
						CBackupGuideInstall backinstall(m_PaintManager);
						backinstall.Create(this->m_hWnd, _T("CBackupGuideInstall"), UI_CLASSSTYLE_DIALOG, WS_EX_WINDOWEDGE, CDuiRect());
						backinstall.CenterWindow();

						if ( IDOK == backinstall.ShowModal() )
						{
							TNotifyUI msg;
							leftRegionMgr_->executeFunc(L"backup_selectchanged", msg);

							COptionUI* pControl = static_cast<COptionUI*>(m_PaintManager.FindControl(_T("leftRegion_backup")));
							if( pControl ) pControl->Selected(true);
						}
					}
				}
				bHandled = FALSE;
				break;
			}
		case WM_CUSTOM_TRANSTASK_AFTERADDTASK:
			//transTaskMgr_->afterAddTask(int32_t(wParam), int32_t(lParam));
			break;
		case WM_CUSTOM_TRANSTASK_LOADNEWTASK:
			//transTaskMgr_->loadNewTask((AsyncTaskId*)wParam);
			break;
		case WM_CUSTOM_TRANSTASK_ITEMERRORSTATECLICK:
			transTaskMgr_->itemErrorStateClick((CControlUI*)wParam);
			break;
		default:
			bHandled = FALSE;
			break;
		}
		if (bHandled)
		{
			return lRes;
		}

		// call the parent HandleMessage
		return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
	}

	LRESULT MainFrame::ResponseDefaultKeyEvent(WPARAM wParam)
	{
		if (wParam == VK_RETURN)
		{
			return FALSE;
		}
		else if (wParam == VK_ESCAPE)
		{
			ShowWindow(false);
			return TRUE;
		}

		return FALSE;
	}

	void MainFrame::changeWorkMode(int32_t workMode)
	{
		if (WorkMode_Online == workMode)
		{
			notifyIconMgr_->updateStatus(NotifyIconStatus::ONLINE);
		}
		else if (WorkMode_Offline == workMode)
		{
			if (!data_.empty() && !token_.empty())
			{
				LoginMgr::getInstance(this->GetHWND())->Logout();
			}
			else
			{
				notifyIconMgr_->updateStatus(NotifyIconStatus::OFFLINE);
			}
		}
	}

	void MainFrame::Notify(TNotifyUI& msg)
	{
		if (notifyIconMgr_ && notifyIconMgr_->proccessMessage(msg))
		{
			return;
		}
		if (msg.sType == DUI_MSGTYPE_RICHEDIT_CHARACTER_EXCCED)
		{
			std::wstring strText = L"";
			strText = iniLanguageHelper.GetCommonString(COMMOM_TOOLTIP_MAXCHAR_KEY).c_str();

			RECT rt={GET_X_LPARAM(msg.wParam),GET_X_LPARAM(msg.lParam),GET_Y_LPARAM(msg.wParam),GET_Y_LPARAM(msg.lParam)};

			return showTip(rt, strText);
		}
		if (msg.sType == DUI_MSGTYPE_RICHEDIT_INVALID_CHARACTER)
		{
			std::wstring strText = L"";
			strText = iniLanguageHelper.GetCommonString(COMMOM_TOOLTIP_CHARACTER_KEY).c_str();
			strText += L"(";
			CRenameRichEditUI* renameRichEdit = static_cast<CRenameRichEditUI*>(msg.pSender);
			if( NULL == renameRichEdit ) return;

			strText += renameRichEdit->GetLimitChar();
			strText += L")";

			RECT rt={GET_X_LPARAM(msg.wParam),GET_X_LPARAM(msg.lParam),GET_Y_LPARAM(msg.wParam),GET_Y_LPARAM(msg.lParam)};

			return showTip(rt, strText);
		}

		if (msg.sType == DUI_MSGTYPE_RICHEDIT_RESTOR_NORMAL)
		{
			return hideTip();
		}
		return WindowImplBase::Notify(msg);
	}

	LRESULT MainFrame::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( wParam == SC_CLOSE )
		{
			ShowWindow(false);
			return 0;
		}

		BOOL bZoomed = ::IsZoomed(*this);
		LRESULT lRes = CWindowWnd::HandleMessage(uMsg, wParam, lParam);
		if( ::IsZoomed(*this) != bZoomed ) 
		{
			if( !bZoomed ) 
			{
				CControlUI* pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(ControlNames::BTN_MAX));
				if( pControl ) pControl->SetVisible(false);
				pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(ControlNames::BTN_RESTOR));
				if( pControl ) pControl->SetVisible(true);
			}
			else 
			{
				CControlUI* pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(ControlNames::BTN_MAX));
				if( pControl ) pControl->SetVisible(true);
				pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(ControlNames::BTN_RESTOR));
				if( pControl ) pControl->SetVisible(false);
			}
		}
		return lRes;
	}

	LRESULT MainFrame::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient = {0,0,0,0};
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_PaintManager.GetCaptionRect();

		if (-1 == rcCaption.bottom)
		{
			rcCaption.bottom = rcClient.bottom;
		}

		if ( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) 
		{
			CControlUI* pControl = m_PaintManager.FindControl(pt);
			if (NULL != pControl)
			{
				CDuiString clsName;
				std::vector<CDuiString> clsNames;
				clsName = pControl->GetClass();
				clsName.MakeLower();
				clsNames.push_back(_T("controlui"));
				clsNames.push_back(_T("textui"));
				clsNames.push_back(_T("labelui"));
				clsNames.push_back(_T("containerui"));
				clsNames.push_back(_T("horizontallayoutui"));
				clsNames.push_back(_T("verticallayoutui"));
				clsNames.push_back(_T("tablayoutui"));
				clsNames.push_back(_T("childlayoutui"));
				clsNames.push_back(_T("dialoglayoutui"));
				clsNames.push_back(_T("cnameeditui"));

				std::vector<CDuiString>::iterator it = std::find(clsNames.begin(), clsNames.end(),clsName);
				if (clsNames.end() != it)
				{
					CControlUI* pParent = pControl->GetParent();
					while (pParent)
					{
						clsName = pParent->GetClass();
						clsName.MakeLower();
						it = std::find(clsNames.begin(),clsNames.end(),clsName);
						if (clsNames.end() == it)
						{
							return HTCLIENT;
						}
						pParent = pParent->GetParent();
						SLEEP(boost::posix_time::milliseconds(0));
					}
					return HTCAPTION;
				}				
			}
		}
		return HTCLIENT;
	}

	LRESULT MainFrame::OnHandleCopyData(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = TRUE;

		COPYDATASTRUCT* copyData = (COPYDATASTRUCT*)lParam;
		if (NULL == copyData || copyData->cbData < (6*FS_MAX_PATH_W*sizeof(wchar_t)))
		{
			return 0;
		}
		NOTIFY_PARAM param;
		param.type = copyData->dwData;
		std::shared_ptr<wchar_t> buf(new wchar_t[FS_MAX_PATH_W]);
		wcscpy_s(buf.get(), FS_MAX_PATH_W, (wchar_t*)copyData->lpData);
		param.msg1 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W);
		param.msg2 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*2);
		param.msg3 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*3);
		param.msg4 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*4);
		param.msg5 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*5);
		param.msg6 = buf.get();
		wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*6);
		param.msg7 = buf.get();

		switch (param.type)
		{
		case NOTIFY_MSG_TRANS_TASK_COMPLETE:
			{
				AsyncTransType type = AsyncTransType(SD::Utility::String::string_to_type<int32_t>(param.msg2));
				if ((type&ATT_Upload) != 0 || (type&ATT_Download) != 0)
				{
					transTaskMgr_->updateTaskStatus(param.msg1, ATS_Complete);
				}
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_ERROR:
			{
				AsyncTransType type = AsyncTransType(SD::Utility::String::string_to_type<int32_t>(param.msg2));
				if ((type&ATT_Upload) != 0 || (type&ATT_Download) != 0)
				{
					transTaskMgr_->updateTaskStatus(param.msg1, ATS_Error);
				}
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_CANCEL:
			{
				AsyncTransType type = AsyncTransType(SD::Utility::String::string_to_type<int32_t>(param.msg2));
				if ((type&ATT_Upload) != 0 || (type&ATT_Download) != 0)
				{
					transTaskMgr_->updateTaskStatus(param.msg1, ATS_Cancel);
				}
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_UPDATE:
			{
				AsyncTransType type = AsyncTransType(SD::Utility::String::string_to_type<int32_t>(param.msg2));
				if ((type&ATT_Upload) != 0 || (type&ATT_Download) != 0)
				{
					transTaskMgr_->updateTask(param.msg1, 
						SD::Utility::String::string_to_type<int64_t>(param.msg3), 
						SD::Utility::String::string_to_type<int64_t>(param.msg4));
				}
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_UPDATE_SIZE:
			{
				AsyncTransType type = AsyncTransType(SD::Utility::String::string_to_type<int32_t>(param.msg2));
				if ((type&ATT_Upload) != 0 || (type&ATT_Download) != 0)
				{
					transTaskMgr_->updateTaskSize(param.msg1, SD::Utility::String::string_to_type<int64_t>(param.msg3));
				}
			}
			break;
		case NOTIFY_MSG_TRANS_SPEED:
			{
				transTaskMgr_->updateTransSpeed(SD::Utility::String::string_to_type<int64_t>(param.msg1), 
					SD::Utility::String::string_to_type<int64_t>(param.msg2));
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_REFRESH_UI:
			{
				UserContextType userType = UserContextType(SD::Utility::String::string_to_type<int32_t>(param.msg1));
				int64_t ownerId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
				int64_t dirId = SD::Utility::String::string_to_type<int64_t>(param.msg3);
				leftRegionMgr_->addReloadInfo(userType, ownerId, dirId);
			}
			break;
		case NOTIFY_MSG_TRANS_TASK_UPDATE_NUMBER:
			{
				if (transTaskMgr_)
				{
					int32_t inewtasknumber = SD::Utility::String::string_to_type<int32_t>(param.msg1);
					transTaskMgr_->SetTransfersListTip(inewtasknumber);
				}
			}
			break;
		case NOTIFY_MSG_FS_DIR_CHANGE:
			{
				CurPageType pageType = (CurPageType)SD::Utility::String::string_to_type<int32_t>(param.msg1);
				int64_t ownerId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
				int64_t curId = SD::Utility::String::string_to_type<int64_t>(param.msg3);
				if(!leftRegionMgr_->isInHistory(pageType) || leftRegionMgr_->isSkipReload())
				{
					break;
				}

				switch(pageType)
				{
				case Page_MyFile:
					myFileMgr_->reloadCache(ownerId, curId);
					break;
				case Page_Share2Me:
					share2MeMgr_->reloadCache(ownerId, curId);
					break;
				case Page_TeamSpace:
					teamSpaceMgr_->reloadCache(ownerId, curId);
					break;
				case Page_MyShare:
					myShareMgr_->reloadCache();
					break;
				default:
					break;
				}
			}
			break;
		case NOTIFY_MSG_POWER_CHANGE:
			{
				std::wstring strContent = L"";
				std::wstring strTitle =  iniLanguageHelper.GetMsgTitle(MSG_POWER_TITLE);
				int32_t itype = SD::Utility::String::string_to_type<int32_t>(param.msg1);
				if( itype == POWER_TYPE_OFFLINE )
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_POWER_OFFLINE);
				}
				else
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_POWER_ONLINE);
				}

				notifyIconMgr_->showBalloon(RESOURCE, strTitle, SD::Utility::String::ltrim(strContent,strTitle));
			}
			break;
		case NOTIFY_MSG_NETWORK_WIRELESS_EXIST:
			{
				std::wstring strContent = L"";
				std::wstring strTitle =  iniLanguageHelper.GetMsgTitle(MSG_NETWORK_WIRELESS_TITLE);
				int32_t itype = SD::Utility::String::string_to_type<int32_t>(param.msg2);

				strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_NETWORK_WIRELESS_EXIST);

				notifyIconMgr_->showBalloon(RESOURCE, strTitle, SD::Utility::String::ltrim(strContent,strTitle));
			}
			break;
		case NOTIFY_MSG_NETWORK_WIRELESS:
			{
				std::wstring strContent = L"";
				std::wstring strTitle =  iniLanguageHelper.GetMsgTitle(MSG_NETWORK_WIRELESS_TITLE);
				int32_t itype = SD::Utility::String::string_to_type<int32_t>(param.msg2);
				if( itype == IF_OPER_STATUS_DISCONNECTED )
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_NETWORK_WIRELESS_OFFLINE);
				}
				else
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_NETWORK_WIRELESS_ONLINE);
				}

				notifyIconMgr_->showBalloon(RESOURCE, strTitle, SD::Utility::String::ltrim(strContent,strTitle));
			}
			break;
		case NOTIFY_MSG_NETWORK_CHANGE:
			{
				std::wstring strContent = L"";
				std::wstring strTitle =  iniLanguageHelper.GetMsgTitle(MSG_NETWORK_TITLE);
				int32_t itype = SD::Utility::String::string_to_type<int32_t>(param.msg1);
				if( itype == IF_OPER_STATUS_DISCONNECTED )
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_NETWORK_OFFLINE);
				}
				else
				{
					strContent = iniLanguageHelper.GetMsgDesc(SYSTEM_INFO_NETWORK_ONLINE);
				}

				notifyIconMgr_->showBalloon(RESOURCE, strTitle, SD::Utility::String::ltrim(strContent,strTitle));
			}
			break;
		case NOTIFY_MSG_MSG_CHANGE:
			//TODO UIË¢ÐÂÏûÏ¢
			{
				//msgFrame_->reloadCache();

				CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(m_PaintManager.FindControl(L"msgFrame"));
				if (NULL == pMsg ) return 0;
				//if (pMsg->IsVisible()) return 0;
				CButtonUI* pTip = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"msgTip"));
				if (NULL == pTip ) return 0;
				MsgTypeList msgTl;
				int64_t nCount = ProxyMgr::getInstance(mainUserContext_)->hasUnRead(msgTl);
				if (nCount <= 0)
				{
					pTip->SetVisible(false);
				}
				if (nCount > 0)
				{
					pTip->SetFixedHeight(16);
					CDuiString strCount;
					if (nCount > 99)
					{
						strCount = _T("99+");
						pTip->SetFixedWidth(30);
						pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,52,30,68'"));
					}
					else if (nCount > 9)
					{
						strCount.Format(_T("%d"), nCount);
						pTip->SetFixedWidth(23);
						pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,26,23,42'"));
					}
					else
					{
						strCount.Format(_T("%d"), nCount);
						pTip->SetFixedWidth(16);
						pTip->SetBkImage(_T("file='..\\Image\\ic_top_sys_number.png' source='0,0,16,16'"));
					}
					pTip->SetText(strCount);
					pTip->SetVisible(true);	
				}

				if (L"" == param.msg1) return 0;
				int msgType = SD::Utility::String::string_to_type<int>(param.msg1);
				std::wstring providerName = param.msg2;
				std::wstring nodeName = param.msg3;
				std::wstring title = param.msg5;
				std::wstring currentRole = param.msg6;
				std::wstring strContent = L"";
				std::wstring strTitle = L"";

				switch (msgType)
				{
				case MT_Share:
					{
						std::wstring msg_desc = SD::Utility::String::string_to_type<int32_t>(param.msg4) == FILE_TYPE_DIR ? USER_MSG_SHARE_FOLDER : USER_MSG_SHARE_FILE;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_SHARE_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(msg_desc, strTitle.c_str(), providerName.c_str(), nodeName.c_str());
					}
					break;
				case MT_Share_Delete:
					{
						std::wstring msg_desc = SD::Utility::String::string_to_type<int32_t>(param.msg4) == FILE_TYPE_DIR ? USER_MSG_CANCEL_SHARE_FOLDER : USER_MSG_CANCEL_SHARE_FILE;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_SHARE_KEY) ;
						strContent = iniLanguageHelper.GetMsgDesc(msg_desc, strTitle.c_str(), providerName.c_str(), nodeName.c_str());
					}
					break;
				case MT_TeamSpace_Upload:
					{
						std::wstring teamspace_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY);
						if((int32_t)UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage())
						{
							strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, strTitle.c_str(), providerName.c_str(), teamspace_name.c_str(), nodeName.c_str());
						}
						else
						{
							strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, strTitle.c_str(), providerName.c_str(), nodeName.c_str(), teamspace_name.c_str());

						}
					}
					break;
				case MT_TeamSpace_Add:
					{
						std::wstring teamspace_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_TEAMSPACE, (strTitle).c_str(), providerName.c_str(), teamspace_name.c_str());
					}
					break;
				case MT_TeamSpace_Delete:
					{
						std::wstring teamspace_name =param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_TEAMSPACE, strTitle.c_str(), providerName.c_str(), teamspace_name.c_str());

					}
					break;
				case MT_TeamSpace_Leave:
					{
						std::wstring teamspace_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_TEAMSPACE, strTitle.c_str(), providerName.c_str(), teamspace_name.c_str());
					}
					break;
				case MT_TeamSpace_RoleUpdate:
					{
						std::wstring str_role = L"";
						if (0== _tcscmp(currentRole.c_str(),L"admin"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"manager"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"editor"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"viewer"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"previewer"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_PREVIEWER_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"uploader"))
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADER_KEY);
						}
						else
						{
							str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADERANDVIEWER_KEY);
						}
						std::wstring teamspace_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_ROLE_UPDATE, strTitle.c_str(), providerName.c_str(), teamspace_name.c_str(), str_role.c_str());
					}
					break;
				case MT_Group_Add:
					{
						std::wstring group_name =param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_GROUP, strTitle.c_str(), providerName.c_str(), group_name.c_str());
					}
					break;
				case MT_Group_Delete:
					{
						std::wstring group_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_GROUP, strTitle.c_str(), providerName.c_str(), group_name.c_str());
					}
					break;
				case MT_Group_Leave:
					{
						std::wstring group_name = param.msg4;
						strTitle = iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_GROUP, strTitle.c_str(), providerName.c_str(), group_name.c_str());
					}
					break;
				case MT_Group_RoleUpdate:
					{
						std::wstring role = L"";
						if (0== _tcscmp(currentRole.c_str(),L"admin"))
						{
							role = iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY);
						}
						else if (0== _tcscmp(currentRole.c_str(),L"manager"))
						{
							role = iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY);
						}
						else
						{
							role = iniLanguageHelper.GetCommonString(COMMENT_COMMONUSER_KEY);
						}
						std::wstring group_name = param.msg4;
						strTitle =  iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_GROUP_ROLE_UPDATE, strTitle.c_str(), providerName.c_str(), group_name.c_str(), role.c_str());
					}
					break;
				case MT_System:
					{
						strTitle =  iniLanguageHelper.GetCommonString(MSGFRAME_SYSTEM_MSG_KEY);
						strContent = iniLanguageHelper.GetMsgDesc(USER_MSG_SYSTEM_ANNOUNCEMENT, strTitle.c_str(),  title.c_str());
					}
					break;
				default:
					break;
				}

				notifyIconMgr_->showBalloon(SYSTEM,strTitle,SD::Utility::String::ltrim(strContent,strTitle));
			}
			break;
		case NOTIFY_MSG_RESTTASK_START:
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
				simlpeNoticeFrame->Show(Right, MSG_RESTTASK_START_KEY, simlpeNoticeFrame->GetShowMsg(param.msg1).c_str());
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			break;
		case NOTIFY_MSG_RESTTASK_COMPLETE:
			{
				std::string type = SD::Utility::String::wstring_to_string(param.msg1);
				if(RESTTASK_DELETE==type || RESTTASK_MOVE==type)
				{
					int64_t srcOwnerId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
					int64_t srcId = SD::Utility::String::string_to_type<int64_t>(param.msg3);
					leftRegionMgr_->addReloadInfo(srcOwnerId, srcId);
				}
				if(RESTTASK_MOVE==type || RESTTASK_COPY==type || RESTTASK_SAVE==type)
				{
					int64_t destOwnerId = SD::Utility::String::string_to_type<int64_t>(param.msg4);
					int64_t destId = SD::Utility::String::string_to_type<int64_t>(param.msg5);
					leftRegionMgr_->addReloadInfo(destOwnerId, destId);
				}
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
				simlpeNoticeFrame->Show(Right, MSG_RESTTASK_COMPLETE_KEY, simlpeNoticeFrame->GetShowMsg(param.msg1).c_str());
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			break;
		case NOTIFY_MSG_RESTTASK_ERROR:
			{
				std::string type = SD::Utility::String::wstring_to_string(param.msg1);
				if(RESTTASK_DELETE==type || RESTTASK_MOVE==type)
				{
					int64_t srcOwnerId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
					int64_t srcId = SD::Utility::String::string_to_type<int64_t>(param.msg3);
					leftRegionMgr_->addReloadInfo(srcOwnerId, srcId);
				}
				if(RESTTASK_MOVE==type || RESTTASK_COPY==type || RESTTASK_SAVE==type)
				{
					int64_t destOwnerId = SD::Utility::String::string_to_type<int64_t>(param.msg4);
					int64_t destId = SD::Utility::String::string_to_type<int64_t>(param.msg5);
					leftRegionMgr_->addReloadInfo(destOwnerId, destId);
				}

				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
				if(L"SameParentConflict"==param.msg6)
				{
					simlpeNoticeFrame->Show(Right, MSG_RESTTASK_COMPLETE_KEY, simlpeNoticeFrame->GetShowMsg(param.msg1).c_str());
				}
				else
				{
					simlpeNoticeFrame->Show(Error, MSG_RESTTASK_ERROR_KEY, simlpeNoticeFrame->GetShowMsg(param.msg1).c_str(), 
						simlpeNoticeFrame->GetShowMsg(param.msg6).c_str());
				}
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			break;
		case NOTIFY_MSG_REFRESH_THUMB:
			{
				std::string thumbKey = SD::Utility::String::wstring_to_string(param.msg1);
				leftRegionMgr_->reloadThumb(thumbKey);
			}
			break;
		case NOTIFY_MSG_GOTO_MYFILE:
			{
				int64_t parentId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				if(parentId<0) break;
				int64_t selectId = -1;
				if(!param.msg2.empty())
				{
					selectId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
				}
				leftRegionMgr_->setCurPageType(Page_MyFile);
				myFileMgr_->showPage(parentId, selectId);
			}
			break;
		case NOTIFY_MSG_GOTO_MYFILE_FOR_NAME:
			{
				int64_t parentId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				if(parentId<0) break;
				leftRegionMgr_->setCurPageType(Page_MyFile);
				myFileMgr_->showPage(parentId, param.msg2);
			}
			break;
		case NOTIFY_MSG_GOTO_SHARE2ME:
			{
				int64_t ownerId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				int64_t fileId = SD::Utility::String::string_to_type<int64_t>(param.msg2);
				leftRegionMgr_->setCurPageType(Page_Share2Me);
				share2MeMgr_->showPage(ownerId, fileId);
			}
			break;
		case NOTIFY_MSG_GOTO_SHARE2ME_FOR_NAME:
			{
				int64_t ownerId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				leftRegionMgr_->setCurPageType(Page_Share2Me);
				share2MeMgr_->showPage(ownerId, param.msg2);
			}
			break;
		case NOTIFY_MSG_GOTO_TEAMSPACE:
			{
				int64_t selectTeamId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				std::wstring teamName = param.msg2;
				leftRegionMgr_->setCurPageType(Page_TeamSpace);
				teamSpaceMgr_->enterTeamspace(selectTeamId);
			}
			break;
		case NOTIFY_MSG_GOTO_TEAMSPACE_FOR_NAME:
			{
				int64_t selectTeamId = SD::Utility::String::string_to_type<int64_t>(param.msg1);
				std::wstring teamName = param.msg2;
				leftRegionMgr_->setCurPageType(Page_TeamSpace);
				UserContext* teamspaceusercontext = UserContextMgr::getInstance()->createUserContext(mainUserContext_
					, SD::Utility::String::string_to_type<int64_t>(param.msg3), UserContext_Teamspace, param.msg4);
				teamSpaceMgr_->showPage(teamspaceusercontext, selectTeamId, param.msg2);
			}
			break;
		case NOTIFY_MSG_CHANGE_WORK_MODE:
			changeWorkMode(SD::Utility::String::string_to_type<int32_t>(param.msg1));
			break;
		case NOTIFY_MSG_SETSHARE_FAILED:
			{
				NoticeFrameMgr *noticeFrame = new NoticeFrameMgr(m_hWnd);
				if (NULL != noticeFrame)
				{
					noticeFrame->Run(Confirm,Error,MSG_FAILURE_KEY,MSG_SHARE_SHAREFAILED_KEY);
				}
				delete noticeFrame;
				noticeFrame = NULL;
			}
			break;
		case NOTIFY_MSG_BACKUPALL_TASKINFO_CHANGE:
			{
				fullBackUpMgr_->updateTaskProcess();
			}
			break;
		case NOTIFY_MSG_ADDFULLBACKUP_SUCCESSED:
			{
				NoticeFrameMgr *noticeFrame = new NoticeFrameMgr(m_hWnd);
				if (NULL != noticeFrame)
				{
					::SetForegroundWindow(m_hWnd);
					noticeFrame->Run(Confirm, Right, MSG_SUCESS_KEY, MSG_SHARE_ADDFULLBACKUPSUCCESSED_KEY, Modal);
				}
				delete noticeFrame;
				noticeFrame = NULL;
			}
			break;
		case NOTIFY_MSG_ADDFULLBACKUP_FAILED:
			{
				NoticeFrameMgr *noticeFrame = new NoticeFrameMgr(m_hWnd);
				if (NULL != noticeFrame)
				{
					::SetForegroundWindow(m_hWnd);
					noticeFrame->Run(Confirm, Error, MSG_FAILURE_KEY, MSG_SHARE_ADDFULLBACKUPFAILED_KEY, Modal);
				}
				delete noticeFrame;
				noticeFrame = NULL;
			}
			break;
		default:
			break;
		}

		return 0;
	}

	LRESULT MainFrame::OnSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (NULL == m_PaintManager.GetRoot())
		{
			return WindowImplBase::OnSize(uMsg,wParam,lParam,bHandled);
		}
		if (::IsMinimized(this->GetHWND()))
		{		
			SetSate(1);
			return WindowImplBase::OnSize(uMsg,wParam,lParam,bHandled);
		}
		else
		{	
			SetSate(0);
		}
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
		simpleNotice->SetPos();
		delete simpleNotice;
		simpleNotice = NULL;

		return WindowImplBase::OnSize(uMsg,wParam,lParam,bHandled);
	}

	void MainFrame::OnClick(TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();
		if(name == ControlNames::BTN_CLOSE)
		{
			ShowWindow(false);
			m_State = 3;
			return;
		}
		else if(name == ControlNames::BTN_MIN)
		{ 
			SendMessage(WM_SYSCOMMAND, SC_MINIMIZE, 0); 
			m_State = 1;
			return; 
		}
		else if(name == ControlNames::BTN_MAX)
		{ 
			SendMessage(WM_SYSCOMMAND, SC_MAXIMIZE, 0); 
			return;
		}
		else if(name == ControlNames::BTN_RESTOR)
		{ 
			SendMessage(WM_SYSCOMMAND, SC_RESTORE, 0); 
			return; 
		}
		else if(name == ControlNames::BTN_CLOSENOTICE)
		{ 
			SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
			simpleNotice->RestoreNoticeArea();
			delete simpleNotice;
			simpleNotice=NULL;
			return;
		}
		else if(name == L"btn_msg" || name == L"msgTip")
		{
			msgShow();
		}
		else if(name == L"btn_userName")
		{
			msg.pSender->SetContextMenuUsed(true);
			TEventUI event;
			event.dwTimestamp = msg.dwTimestamp;
			event.pSender = msg.pSender;
			event.lParam = msg.lParam;
			event.wParam = msg.wParam;
			event.ptMouse = msg.ptMouse;
			event.Type = UIEVENT_CONTEXTMENU;
			msg.pSender->Event(event);
			msg.pSender->SetContextMenuUsed(false);
			return;
		}
		executeFunc(msg);
	}

	void MainFrame::msgShow()
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(m_PaintManager.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMain->SetVisible(false);
		pMsg->SetVisible();
		CurPageType curPage = leftRegionMgr_->getCurPageType();
		COptionUI* pSelect = NULL;
		switch (curPage)
		{
		case Onebox::Page_MyFile:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_myFile"));
			break;
		case Onebox::Page_Share2Me:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_share2Me"));
			break;
		case Onebox::Page_MyShare:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_myShare"));
			break;
		case Onebox::Page_TeamSpace:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_teamSpace"));
			break;
		case Onebox::Page_Transfers:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_transfersList"));
			break;
		case Onebox::Page_Backup:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_backup"));
			break;
		case Onebox::Page_Other:
			pSelect = static_cast<COptionUI*>(m_PaintManager.FindControl(L"leftRegion_other"));
			break;
		default:
			break;
		}
		if (pSelect)
			pSelect->Selected(false);
		CLabelUI* pTip = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"msgTip"));
		if (NULL != pTip)
			pTip->SetVisible(false);
		if(NULL == msgFrame_) return;
		msgFrame_->initData();
	}

	void MainFrame::OnTimer(UINT nIDEvent)
	{
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
		simpleNotice->RestoreNoticeArea();
		delete simpleNotice;
		simpleNotice=NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	void MainFrame::executeFunc(TNotifyUI& msg)
	{
		if (NULL== leftRegionMgr_ || NULL == myFileMgr_|| NULL == share2MeMgr_|| 
			NULL == transTaskMgr_|| NULL == myShareMgr_|| NULL == teamSpaceMgr_  || NULL == fullBackUpMgr_)
		{
			return;
		}

		CDuiString tempName = msg.pSender->GetName();
		if (tempName == L"btn_userName" && msg.sType == DUI_MSGTYPE_MENUITEM_CLICK)
		{
			menuItemClick(msg);
			return;
		}

		std::wstring funcName = tempName + L"_" + msg.sType;

		if(tempName.Find(L"leftRegion_trash")!=std::wstring::npos && msg.sType == L"click")
		{
			std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
			Configure* config =  configureMgr->getConfigure();				
			std::wstring  str_server = config->serverUrl();
			str_server = str_server.substr(0,str_server.length()-6);
			str_server += L"trash#1";
			(void)ShellExecute(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
			return;
		}

		if(tempName.Find(L"leftRegion_")!=std::wstring::npos)
		{
			leftRegionMgr_->executeFunc(funcName.substr(sizeof(L"leftRegion_")/sizeof(wchar_t)-1), msg);
			return;
		}

		if(tempName.Find(L"myFile_")!=std::wstring::npos)
		{
			myFileMgr_->executeFunc(funcName.substr(sizeof(L"myFile_")/sizeof(wchar_t)-1), msg);
			return;
		}

		if(tempName.Find(L"share2Me_")!=std::wstring::npos)
		{
			share2MeMgr_->executeFunc(funcName.substr(sizeof(L"share2Me_")/sizeof(wchar_t)-1), msg);
			return;
		}

		if (tempName.Find(L"transTask_") != std::wstring::npos)
		{
			transTaskMgr_->executeFunc(funcName.substr(sizeof(L"transTask_")/sizeof(wchar_t)-1), msg);
			return;
		}

		if (tempName.Find(L"myShare_") != std::wstring::npos)
		{
			myShareMgr_->executeFunc(funcName.substr(sizeof(L"myShare_")/sizeof(wchar_t)-1), msg);
			return;
		}
		if (tempName.Find(L"teamSpace_") != std::wstring::npos)
		{
			teamSpaceMgr_->executeFunc(funcName.substr(sizeof(L"teamSpace_")/sizeof(wchar_t)-1), msg);
			return;
		}
		if (tempName.Find(L"msgFrame_") != std::wstring::npos)
		{
			msgFrame_->executeFunc(funcName.substr(sizeof(L"msgFrame_")/sizeof(wchar_t)-1), msg);
			return;
		}
		if (tempName.Find(L"fullBackup_") != std::wstring::npos)
		{
			fullBackUpMgr_->executeFunc(funcName.substr(sizeof(L"fullBackup_")/sizeof(wchar_t)-1), msg);
			return;
		}
	}

	void MainFrame::showTip(const RECT rt, const std::wstring& strText)
	{
		(void)memset_s(&m_InfoTip, sizeof(m_InfoTip), 0, sizeof(m_InfoTip));
		m_InfoTip.cbSize = sizeof(m_InfoTip);
		m_InfoTip.hwnd = m_hWnd;
		m_InfoTip.uFlags = TTF_CENTERTIP;	
		m_InfoTip.rect = rt;

		m_InfoTip.lpszText = (LPWSTR)strText.c_str();

		if (NULL == m_hWndTip){
			m_hWndTip = ::CreateWindowEx(0, TOOLTIPS_CLASS, NULL, WS_POPUP | TTS_NOPREFIX | TTS_ALWAYSTIP,
				CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
				m_hWnd, NULL, CPaintManagerUI::GetInstance(), NULL);
			::SendMessage(m_hWndTip, TTM_ADDTOOL, 0, (LPARAM) &m_InfoTip);
		}

		::SendMessage(m_hWndTip, TTM_SETTOOLINFO, 0, (LPARAM) &m_InfoTip);
		::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, TRUE, (LPARAM) &m_InfoTip);
	}

	void MainFrame::hideTip()
	{
		if( m_hWndTip != NULL )
			::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, FALSE, (LPARAM) &m_InfoTip);
	}

	void MainFrame::SetTipText(CControlUI* pControl, int64_t num)
	{
		if (NULL == pControl)	return;

		CDuiString strText;
		pControl->SetVisible(false);
		if (num > 99){
			pControl->SetFixedHeight(16);
			pControl->SetFixedWidth(30);
			pControl->SetBkImage(ShowTipThree);
			pControl->SetText(L"99+");
		}else if (num <= 99 && num > 9){
			pControl->SetFixedHeight(16);
			pControl->SetFixedWidth(23);
			pControl->SetBkImage(ShowTipTwo);
			strText.Format(L"%lld", num);
			pControl->SetText(strText);
		}else if (num <= 9 && num >= 0){
			pControl->SetFixedHeight(16);
			pControl->SetFixedWidth(16);
			pControl->SetBkImage(ShowTipOne);
			strText.Format(L"%lld", num);
			pControl->SetText(strText);
		}
		if (num > 0)
		{
			pControl->SetVisible(true);
		}
	}

	void MainFrame::ChangeLanguage()
	{
		NoticeFrameMgr* pNoticeFrame_ = new NoticeFrameMgr(*this);
		if (pNoticeFrame_){
			pNoticeFrame_->Run(Choose, Warning, MSG_WARNING_KEY ,MSG_SETTING_CONFIRM_CONTEXT,Modal);
			if (pNoticeFrame_->IsClickOk()){	
				if (iniLanguageHelper.GetLanguage() == UI_LANGUGE::ENGLISH)
					SetUserConfValue(CONF_SETTINGS_SECTION, CONF_LANGUAGE_KEY, UI_LANGUGE::CHINESE);
				else
					SetUserConfValue(CONF_SETTINGS_SECTION, CONF_LANGUAGE_KEY, UI_LANGUGE::ENGLISH);

				notifyIconMgr_->Exit();
				SetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::CHANGELANG);
			}
			if (NULL != pNoticeFrame_){
				delete pNoticeFrame_;
				pNoticeFrame_ = NULL;
			}
		}
	}

	void MainFrame::menuItemClick(TNotifyUI& msg)
	{
		int menuId = msg.lParam;
		if (menuId < 0) return;
		switch (menuId)
		{
		case 0:
			{
				notifyIconMgr_->ShowSettings((int32_t)msg.wParam);
			}
			break;
		case 1:
			{
				ChangeLanguage();
			}
			break;
			// collect logs
		case 2:
			{
				LoginMgr::getInstance(this->GetHWND())->Logout();
			}
			break;
			// help
		case 3:
			{
				notifyIconMgr_->Exit();
			}
			break;
			// about
		case 4:
			break;
			// exit
		case 5:
			break;
		default:
			break;
		}
	}

	void MainFrame::NotifyIconProcess()
	{
		CNotifyIconUI* pNotifyIcon = m_PaintManager.GetRoot()->GetNotifyIcon();
		if (NULL == pNotifyIcon) return;
		if (!pNotifyIcon->ReInit()) return;
		WorkMode workMode = mainUserContext_->getWorkmodeMgr()->getWorkMode();
		NotifyIconStatus iconStatus = OFFLINE;
		switch (workMode)
		{
		case WorkMode_Online:
			iconStatus =  ONLINE;
			break;
		case WorkMode_Offline:
			iconStatus = OFFLINE;
			break;
		case WorkMode_Error:
			iconStatus = FAILED;
			break;
		case WorkMode_Pause:
			iconStatus = PAUSE;
			break;
		default:
			break;
		}

		notifyIconMgr_->updateStatus(iconStatus);
	}

	int MainFrame::GetState()
	{
		return m_State;
	}

	void MainFrame::SetSate( int istate )
	{
		m_State = istate;
	}

	void MainFrame::update()
	{
	
		if(!UpdateDBMgr::getInstance(mainUserContext_)->update())
		{
			Sleep(1000);
			SetUserConfValue(CONF_USERINFO_SECTION, CONF_LOGOUT_KEY, (int64_t)LogoutType::NO );
			PostQuitMessage(0);		
		}
		UpdateDBMgr::release();
		isUpdating_ = false;
	}
}
