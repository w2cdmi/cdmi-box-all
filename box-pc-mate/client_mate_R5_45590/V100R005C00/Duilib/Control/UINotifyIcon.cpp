#include "StdAfx.h"
#include "time.h"

namespace DuiLib
{
	static void copy_notifyicon_str(wchar_t* dest, size_t cnt1, const wchar_t* source, size_t cnt2)
	{
		if (cnt1 <= cnt2) 
		{
			if (cnt1 > 3)
			{
				dest[cnt1-1] = 0;
				dest[cnt1-2] = L'.';
				dest[cnt1-3] = L'.';
				dest[cnt1-4] = L'.';
				memcpy_s(dest, cnt1*sizeof(wchar_t), source, (cnt1-4)*sizeof(wchar_t));
			} 
		}
		else 
		{
			wcsncpy_s(dest, cnt1, source, cnt2);
		}
	}

	#define TIMER_DISPLAY_ID (1)
	#define TIMER_CLOSE_ID (2)
	#define MOVE_HEIGHT (5)
	#define MOVE_INTERVAL (10)
	#define DISPLAY_MAX_CHARS (196)
	#define OPEN_SETTINGS_WPARAM (1)
	#define SIMULATION_MENUID (1)
	#define MOUSEMOVE_COUNT (5)

	class CShowBalloonDialog : public WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP();
	public:
		CShowBalloonDialog(CDuiString skinfolder
			, CDuiString skinfile
			, CPaintManagerUI* pManager
			, CControlUI* pParent
			, MessageType type
			, DWORD timeout)
		{
			m_sSkinFile = skinfile;
			m_sSkinFolder = skinfolder;

			m_sContent = L"";
			m_sTitle = L"";

			m_msgType = type;

			m_pManager = pManager;
			m_pControl = pParent;

			m_tTimeout = timeout;

			m_y = 0;

			m_CheckboxTitleResource = L"";
			m_CheckboxTitleSystem = L"";

			m_mouseEnter = false;

			m_mouserMoveCount = 0;

			Create(NULL, L"CShowBalloonDialog", UI_CLASSSTYLE_FRAME, WS_EX_TOPMOST|WS_EX_TOOLWINDOW, 0, 0, 0, 0);
		}

		~CShowBalloonDialog(){};


		void InitWindow()
		{
			::GetWindowRect(this->GetHWND(), &m_windowRect);
		}

		virtual CDuiString GetSkinFolder()
		{
			return m_sSkinFolder;
		}
		virtual CDuiString GetSkinFile()
		{
			return m_sSkinFile;
		}
		virtual LPCTSTR GetWindowClassName(void) const
		{
			return _T("CSHOWBALLOONDIALOG");
		}

		void OnClick(TNotifyUI& msg)
		{
			CDuiString name = msg.pSender->GetName();

			if ((_tcsicmp(name, _T("notifcation_btnClose")) == 0) )
			{
				m_tStartDisplay -= m_tTimeout;
				this->ShowWindow(false);
			}
			else if (_tcsicmp(name, _T("notifcation_btnSetting")) == 0)
			{
				this->ShowWindow(false);
				m_pManager->SendNotify(m_pControl, DUI_MSGTYPE_MENUITEM_CLICK, OPEN_SETTINGS_WPARAM, SIMULATION_MENUID);
			}
		}

		void OnSelectChanged(TNotifyUI& msg)
		{
			CDuiString name = msg.pSender->GetName();

			if (_tcsicmp(name, _T("notifcation_btnCheck")) == 0)
			{
				CCheckBoxUI* checkbox = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(L"notifcation_btnCheck"));
				NotifyIconEvent eventype;
				if ( SYSTEM == m_msgType )
				{
					eventype = NOTIFYICON_EDIT_SYSTEM_CONF;
				}
				else if ( RESOURCE == m_msgType )
				{
					eventype = NOTIFYICON_EDIT_RESOURCE_CONF;
				}

				if ( checkbox )
				{
					m_pManager->SendNotify(m_pControl, DUI_MSGTYPE_NOTIFYICON_EVENT, !checkbox->GetCheck(), eventype);
				}
			}
		}

		void SetDisplay(const LPCTSTR stitle, const LPCTSTR scontent)
		{
			m_sTitle = stitle;
			m_sContent = scontent;

			if ( !m_sTitle.IsEmpty() )
			{

			}

			if ( !m_sContent.IsEmpty() )
			{
				CDuiString scontent = L"";
				scontent = m_sContent;
				
				CRichEditUI* edit = static_cast<CRichEditUI*>(m_PaintManager.FindControl(L"notifcation_userInfo_dataContext"));
				if ( edit )
				{
					CDuiString stemp = edit->GetEndellipsisText(scontent, (m_windowRect.right-m_windowRect.left)*3+160, 12);
					edit->SetText(stemp);

					if ( stemp != scontent )
					{
						edit->SetToolTip(m_sContent);
					}
					else
					{
						edit->SetToolTip(L"");
					}
				}
			}
		}

		void SetMsgType(MessageType type)
		{
			m_msgType = type;
		}

		void ShowWindow(bool bShow  = true, bool bTakeFocus = true )
		{
			if (bShow )
			{
				HDC screenDC;
				screenDC = CreateDC(L"DISPLAY",NULL, NULL, NULL);

				int hRes = SystemParametersInfo(SPI_GETWORKAREA, 0, &m_workAreaRect, 0);
				m_xRes = GetDeviceCaps(screenDC, HORZRES);
				m_yRes = GetDeviceCaps(screenDC, VERTRES);
				DeleteDC(screenDC);
				CCheckBoxUI* checkbox = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(L"notifcation_btnCheck"));
				std::wstring checktitle = L"";
				if ( SYSTEM == m_msgType )
				{
					checktitle = m_CheckboxTitleSystem;
				}
				else if ( RESOURCE == m_msgType )
				{
					checktitle = m_CheckboxTitleResource;
				}

				if ( checkbox )
				{
					checkbox->SetCheck(false);
					checkbox->SetText(checktitle.c_str());
				}

				m_tStartDisplay = time(NULL);
				::SetTimer(this->GetHWND(), TIMER_DISPLAY_ID, MOVE_INTERVAL, NULL);

				m_mouserMoveCount = 0;
			}
			else
			{
				WindowImplBase::ShowWindow(bShow, bTakeFocus);
			}
		}

		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
		{
			LRESULT lRes = 0;
			switch (uMsg)
			{
			case WM_TIMER:
				if ((UINT)TIMER_DISPLAY_ID ==wParam)
				{
					DisplayWindow(MOVE_HEIGHT);
					return lRes;
				}   
				else if((UINT)TIMER_CLOSE_ID == wParam)
				{
					CloseWindow(MOVE_HEIGHT);
					return lRes;
				}
				break;
			case WM_MOUSELEAVE:
				{
					m_mouseEnter = false;
					m_mouserMoveCount = 0;
				}
				break;
			case WM_MOUSEMOVE:
				{
					if ( m_mouserMoveCount > MOUSEMOVE_COUNT )
					{
						m_mouseEnter = true;
					}
					m_mouserMoveCount++;
				}
				break;
			}

			return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
		}

		void DisplayWindow(int y)
		{
			UINT width = m_windowRect.right - m_windowRect.left;

			POINT ptwindowpos;
			ptwindowpos.x = m_xRes - width;
			ptwindowpos.y = m_workAreaRect.bottom - m_y;//(winrect.bottom - winrect.top);

			if ( m_y < m_windowRect.bottom - m_windowRect.top )
			{
				m_y += y;
			}
			
			if ( m_y > m_windowRect.bottom - m_windowRect.top )
			{
				m_y = m_windowRect.bottom - m_windowRect.top;
			}

			if ( m_y < 0 )
			{
				this->ShowWindow(false);
				return;
			}

			if ( m_tTimeout < (time(NULL) - m_tStartDisplay)*1000 )
			{
				::KillTimer(this->GetHWND(), TIMER_DISPLAY_ID);
				::SetTimer(this->GetHWND(), TIMER_CLOSE_ID, MOVE_INTERVAL, NULL);
			}

			::MoveWindow(this->GetHWND(), ptwindowpos.x, ptwindowpos.y, width, m_y, TRUE);
			ClientToScreen(NULL, &ptwindowpos);
			WindowImplBase::ShowWindow();
		}

		void CloseWindow(int y)
		{
			UINT width = m_windowRect.right - m_windowRect.left;

			if ( m_mouseEnter )
			{
				m_y = m_windowRect.bottom - m_windowRect.top;
				y = 0;
			}

			POINT ptwindowpos;
			ptwindowpos.x = m_xRes - width;
			ptwindowpos.y = m_workAreaRect.bottom - (m_y - y);//(winrect.bottom - winrect.top);

			if ( m_y > 0 )
			{
				m_y -= y;
			}

			if ( m_y <= 0 )
			{
				this->ShowWindow(false);
				::KillTimer(this->GetHWND(), TIMER_CLOSE_ID);
				return;
			}

			ClientToScreen(NULL, &ptwindowpos);
			::MoveWindow(this->GetHWND(), ptwindowpos.x, ptwindowpos.y, width, m_y, TRUE);
		}

		void SetCheckboxTitleSystem(const CDuiString title)
		{
			m_CheckboxTitleSystem = title;
		}
		CDuiString SetCheckboxTitleSystem()
		{
			return m_CheckboxTitleSystem;
		}

		void SetCheckboxTitleResource(const CDuiString title)
		{
			m_CheckboxTitleResource = title;
		}

		CDuiString GetCheckboxTitleResource()
		{
			return m_CheckboxTitleResource;
		}
	private:
		CDuiString m_sSkinFile;
		CDuiString m_sSkinFolder;

		CDuiString m_sTitle;
		CDuiString m_sContent;

		CControlUI* m_pControl;
		CPaintManagerUI* m_pManager;

		MessageType m_msgType;

		DWORD m_tTimeout;
		int  m_y;

		RECT m_windowRect;
		RECT m_workAreaRect;
		int m_xRes;
		int m_yRes;
		time_t m_tStartDisplay;

		CDuiString m_CheckboxTitleSystem;
		CDuiString m_CheckboxTitleResource;

		bool m_mouseEnter;
		int m_mouserMoveCount;
	};

	DUI_BEGIN_MESSAGE_MAP(CShowBalloonDialog, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED,OnSelectChanged)
	DUI_END_MESSAGE_MAP()


	CShowBalloonDialog* m_Balloondlg = NULL;

	CNotifyIconUI::CNotifyIconUI(void)
		:m_hIcon(NULL)
		,m_id(0)
		,m_bBalloonEnabled(true)
		,m_uiBalloonTimeout(5000)
		,m_balloonElapsed(0)
	{
		m_Balloondlg = NULL;
	}

	CNotifyIconUI::~CNotifyIconUI(void)
	{
		if (NULL != m_hIcon) {
			NOTIFYICONDATA nID = {0};
			nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
			nID.hWnd = GetManager()->GetPaintWindow();
			nID.uID = m_id;
			(void)::Shell_NotifyIcon(NIM_DELETE, &nID);
			::DestroyIcon(m_hIcon);
		}

		if ( m_Balloondlg )
		{
			m_Balloondlg->Close();
			delete m_Balloondlg;
			m_Balloondlg = NULL;
		}
	}

	int CNotifyIconUI::GetId() const
	{
		return m_id;
	}

	void CNotifyIconUI::SetNotifyIcon(LPCTSTR pstrIcon)
	{
#ifdef _UNICODE
		m_sNotifyIcon = pstrIcon;
		if (NULL != m_hIcon) ::DestroyIcon(m_hIcon);
		m_hIcon = (HICON)::LoadImage(NULL, m_sNotifyIcon, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);
		if (NULL == m_hIcon) return;
		NOTIFYICONDATA nID = {0};
		nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon = m_hIcon;
		copy_notifyicon_str(nID.szTip, sizeof(nID.szTip)/sizeof(WCHAR), m_sToolTip, m_sToolTip.GetLength());
		nID.hWnd = GetManager()->GetPaintWindow();
		nID.uID = m_id;
		nID.uFlags = NIF_GUID|NIF_ICON|NIF_MESSAGE|NIF_TIP|NIF_INFO;
		nID.uCallbackMessage = WM_NOTIFYICON;
		(void)::Shell_NotifyIcon(NIM_MODIFY, &nID);		
#endif
	}

	void CNotifyIconUI::SetNotifyIcon(LPCTSTR pstrIcon, LPCTSTR title)
	{
#ifdef _UNICODE
		DuiLib::CDuiString dtitle = title;
		m_sNotifyIcon = pstrIcon;
		if (NULL != m_hIcon) ::DestroyIcon(m_hIcon);
		m_hIcon = (HICON)::LoadImage(NULL, m_sNotifyIcon, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);
		if (NULL == m_hIcon) return;
		NOTIFYICONDATA nID = {0};
		nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon = m_hIcon;
		copy_notifyicon_str(nID.szTip, sizeof(nID.szTip)/sizeof(WCHAR), dtitle, dtitle.GetLength());
		m_sCurrToolTip = dtitle;
		nID.hWnd = GetManager()->GetPaintWindow();
		nID.uID = m_id;
		nID.uFlags = NIF_GUID|NIF_ICON|NIF_MESSAGE|NIF_TIP|NIF_INFO;
		nID.uCallbackMessage = WM_NOTIFYICON;
		(void)::Shell_NotifyIcon(NIM_MODIFY, &nID);		
#endif
	}

	LPCTSTR CNotifyIconUI::GetNotifyIcon() const
	{
		return m_sNotifyIcon;
	}

	void CNotifyIconUI::SetBalloonEnabled(bool bEnabled)
	{
		m_bBalloonEnabled = bEnabled;
	}

	bool CNotifyIconUI::GetBalloonEnabled() const
	{
		return m_bBalloonEnabled;
	}

	void CNotifyIconUI::SetBalloonTimeout(const UINT uiTimeout)
	{
		m_uiBalloonTimeout = uiTimeout;
	}

	void CNotifyIconUI::SetBalloonSkinFile(const LPCTSTR filename)
	{
		m_sBalloonSkinFile = filename;
	}

	CDuiString CNotifyIconUI::GetBalloonSkinFile()
	{
		return m_sBalloonSkinFile;
	}

	void CNotifyIconUI::SetBalloonSkinFolder(const LPCTSTR folder)
	{
		m_sBalloonSkinFolder = folder;
	}

	CDuiString CNotifyIconUI::GetBalloonSkinFolder()
	{
		return m_sBalloonSkinFolder;
	}

	UINT CNotifyIconUI::GetBalloonTimeout() const
	{
		return m_uiBalloonTimeout;
	}

	void CNotifyIconUI::SetCheckboxTitleSystem(const LPCTSTR title)
	{
		m_CheckboxTitleSystem = title;
	}

	CDuiString CNotifyIconUI::SetCheckboxTitleSystem()
	{
		return m_CheckboxTitleSystem;
	}

	void CNotifyIconUI::SetCheckboxTitleResource(const LPCTSTR title)
	{
		m_CheckboxTitleResource = title;
	}

	CDuiString CNotifyIconUI::GetCheckboxTitleResource()
	{
		return m_CheckboxTitleResource;
	}

	LPCTSTR CNotifyIconUI::GetClass() const
	{
		return _T("NotifyIconUI");
	}

	LPVOID CNotifyIconUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, DUI_CTR_NOTIFYICON) == 0 ) return static_cast<CNotifyIconUI*>(this);
		return CControlUI::GetInterface(pstrName);
	}

	void CNotifyIconUI::Init()
	{
#ifdef _UNICODE
		if (NULL != m_hIcon) ::DestroyIcon(m_hIcon);
		m_hIcon = (HICON)::LoadImage(NULL, m_sNotifyIcon, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);
		if (NULL == m_hIcon) return;
		NOTIFYICONDATA nID = {0};
		nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon = m_hIcon;
		copy_notifyicon_str(nID.szTip, sizeof(nID.szTip)/sizeof(WCHAR), m_sToolTip, m_sToolTip.GetLength());
		m_sCurrToolTip = m_sToolTip;
		nID.hWnd = GetManager()->GetPaintWindow();
		nID.uID = m_id;
		nID.uFlags = NIF_GUID|NIF_ICON|NIF_MESSAGE|NIF_TIP;
		nID.uCallbackMessage = WM_NOTIFYICON;
		(void)::Shell_NotifyIcon(NIM_ADD, &nID);
#endif
	}

	void CNotifyIconUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		LPTSTR pstr = NULL;
		if( _tcscmp(pstrName, _T("notifyicon")) == 0 ) 
		{
			m_sNotifyIcon = pstrValue;
			return;
		}
		if( _tcscmp(pstrName, _T("id")) == 0 ) {
			m_id = _tcstol(pstrValue, &pstr, 10);
			return;
		}
		if( _tcscmp(pstrName, _T("balloonenabled")) == 0 ) 
		{
			m_bBalloonEnabled = (_tcscmp(pstrValue, _T("true")) == 0);
			return;
		}
		if( _tcscmp(pstrName, _T("balloontimeout")) == 0 ) 
		{
			m_uiBalloonTimeout = _tcstol(pstrValue, &pstr, 10);
			return;
		}
		if( _tcscmp(pstrName, _T("balloonskinfile")) == 0 ) 
		{
			SetBalloonSkinFile(pstrValue);
			return;
		}

		if( _tcscmp(pstrName, _T("balloonskinfolder")) == 0 ) 
		{
			SetBalloonSkinFolder(pstrValue);
			return;
		}

		if( _tcscmp(pstrName, _T("checkboxtitlesystem")) == 0 ) 
		{
			SetCheckboxTitleSystem(pstrValue);
			return;
		}

		if( _tcscmp(pstrName, _T("checkboxtitleresource")) == 0 ) 
		{
			SetCheckboxTitleResource(pstrValue);
			return;
		}

		return CControlUI::SetAttribute(pstrName, pstrValue);
	}

	void CNotifyIconUI::DoEvent(TEventUI& event)
	{
		if (UIEVENT_NOTIFYICON != event.Type) return;
		if (event.lParam == WM_RBUTTONUP && IsContextMenuUsed()) {
			event.Type = UIEVENT_CONTEXTMENU;
		}
		switch (event.lParam)
		{
		case WM_RBUTTONUP:
			GetManager()->SendNotify(this, DUI_MSGTYPE_NOTIFYICON_EVENT, event.wParam, NOTIFYICON_RCLICK);
			break;
		case WM_LBUTTONUP:
			GetManager()->SendNotify(this, DUI_MSGTYPE_NOTIFYICON_EVENT, event.wParam, NOTIFYICON_CLICK);
			break;
		case WM_LBUTTONDBLCLK:
			GetManager()->SendNotify(this, DUI_MSGTYPE_NOTIFYICON_EVENT, event.wParam, NOTIFYICON_DBCLICK);
			break;
		case WM_RBUTTONDBLCLK:
			GetManager()->SendNotify(this, DUI_MSGTYPE_NOTIFYICON_EVENT, event.wParam, NOTIFYICON_RDBCLICK);
			break;
		default:
			break;
		}
		return CControlUI::DoEvent(event);
	}

	void CNotifyIconUI::ShowBalloon(const MessageType type, LPCTSTR pstrTitle, LPCTSTR pstrContent)
	{
#ifdef _UNICODE

		if ( !m_Balloondlg )
		{
			m_Balloondlg = new CShowBalloonDialog(m_sBalloonSkinFolder, m_sBalloonSkinFile
				, this->m_pManager, this, type, m_uiBalloonTimeout);	
		}

		if ( m_Balloondlg )
		{
			m_Balloondlg->SetMsgType(type);
			m_Balloondlg->SetCheckboxTitleResource(m_CheckboxTitleResource);
			m_Balloondlg->SetCheckboxTitleSystem(m_CheckboxTitleSystem);


			m_Balloondlg->ShowWindow();
			m_Balloondlg->SetDisplay(L"", pstrContent);
		}
#endif
	}

	bool CNotifyIconUI::ReInit()
	{
#ifdef _UNICODE
		if (((time(NULL) - m_balloonElapsed) * 1000) < (time_t)m_uiBalloonTimeout)
		{
			return false;
		}

		if (NULL != m_hIcon) ::DestroyIcon(m_hIcon);
		m_hIcon = (HICON)::LoadImage(NULL, m_sNotifyIcon, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);
		if (NULL == m_hIcon) return false;
		NOTIFYICONDATA nID = {0};
		nID.cbSize = (DWORD)sizeof(NOTIFYICONDATA);
		nID.hIcon = m_hIcon;
		copy_notifyicon_str(nID.szTip, sizeof(nID.szTip)/sizeof(WCHAR), m_sCurrToolTip, m_sCurrToolTip.GetLength());
		nID.hWnd = GetManager()->GetPaintWindow();
		nID.uID = m_id;
		nID.uFlags = NIF_GUID|NIF_ICON|NIF_MESSAGE|NIF_TIP;
		nID.uCallbackMessage = WM_NOTIFYICON;
		(void)::Shell_NotifyIcon(NIM_ADD, &nID);

		return true;
#endif
	}
}