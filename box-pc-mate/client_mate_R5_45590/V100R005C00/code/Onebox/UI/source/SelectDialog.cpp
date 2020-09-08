#include "stdafxOnebox.h"
#include "SelectDialog.h"
#include "InILanguage.h"

namespace Onebox
{
	SelectDialog::SelectDialog() : m_hParentWnd(NULL)
	{
		m_pt.x = 0;
		m_pt.y = 0;
		m_hSelectWnd = NULL;
		m_width = 0;
		m_heght = 0;
	}

	SelectDialog::~SelectDialog()
	{

	}

	LPCTSTR SelectDialog::GetWindowClassName() const
	{
		return L"SelectDialog";
	}
	UINT SelectDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void SelectDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
		delete this;
	}

	void SelectDialog::Init( LPCTSTR pszXMLPath, HWND hWndParent, POINT pt )
	{  
		m_strXMLPath = pszXMLPath;
		m_hParentWnd = hWndParent;

		m_hSelectWnd = Create(m_hParentWnd, _T("SelectDialog"), UI_WNDSTYLE_DIALOG, WS_EX_TOOLWINDOW);

		::SetWindowPos(m_hSelectWnd,HWND_TOP,pt.x,pt.y,300,400,SWP_SHOWWINDOW);

		RECT rc = {0,0,0,0};
		::GetWindowRect(m_hSelectWnd, &rc);	

		::ClientToScreen(hWndParent, &pt);
		::SetWindowPos(*this, NULL, pt.x, pt.y , 100, 100, SWP_NOZORDER | SWP_NOSIZE | SWP_NOACTIVATE);
	}

	void SelectDialog::SetDialogPos(POINT pt)
	{
		POINT tmp_pt = pt;
		ClientToScreen(m_hParentWnd,&tmp_pt);
		::MoveWindow(m_hSelectWnd,tmp_pt.x,tmp_pt.y,m_width,m_heght,TRUE);
	}

	void SelectDialog::SetTransparent(int nOpacity)
	{
		m_DragPaintm.SetTransparent(255 - nOpacity);
	}

	void SelectDialog::SetWindowShow(bool isShow)
	{
		ShowWindow(isShow);
	}

	LRESULT SelectDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		styleValue &= ~WS_CAPTION;
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_DragPaintm.Init(m_hWnd);
		m_DragPaintm.SetResourcePath(std::wstring(GetInstallPath() + iniLanguageHelper.GetSkinFolderPath()).c_str());
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(m_strXMLPath.GetData(), (UINT)0, 0,&m_DragPaintm);
		if ( pRoot )
		{
			m_DragPaintm.AttachDialog(pRoot);
			m_DragPaintm.SetShadow();
		}
		return 0;
	}

	LRESULT SelectDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT SelectDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT SelectDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT SelectDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT SelectDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT SelectDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return HTCLIENT;
	}


	LRESULT SelectDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		//	Close();
		return 0 ;
	}

	LRESULT SelectDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		//	Close();
		return 0 ;
	}


	LRESULT SelectDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;
		switch( uMsg ) {
		case WM_CREATE:        lRes = OnCreate(uMsg, wParam, lParam, bHandled); break;
		case WM_CLOSE:         lRes = OnClose(uMsg, wParam, lParam, bHandled); break;
		case WM_DESTROY:       lRes = OnDestroy(uMsg, wParam, lParam, bHandled); break;
		case WM_NCACTIVATE:    lRes = OnNcActivate(uMsg, wParam, lParam, bHandled); break;
		case WM_NCCALCSIZE:    lRes = OnNcCalcSize(uMsg, wParam, lParam, bHandled); break;
		case WM_NCPAINT:       lRes = OnNcPaint(uMsg, wParam, lParam, bHandled); break;
		case WM_NCHITTEST:     lRes = OnNcHitTest(uMsg, wParam, lParam, bHandled); break;
		case WM_KILLFOCUS:     lRes = OnKillFocus(uMsg, wParam, lParam, bHandled); break; 
		case WM_MOUSELEAVE:	   lRes = OnMouseLeave(uMsg, wParam, lParam, bHandled); break; 
		default:
			bHandled = FALSE;
		}
		if( bHandled ) return lRes;
		if( m_DragPaintm.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
		return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
	}

};



