#include "StdAfx.h"

namespace DuiLib
{
	class CMenuWnd : public CWindowWnd
	{
	public:
		CMenuWnd(CControlUI* pOwner, LPCTSTR pstrXmlFile, IDialogBuilderCallback* pCallback = NULL)
			:m_pOwner(pOwner)
			,m_pRealOwner(NULL)
			,m_sXmlFile(pstrXmlFile)
			,m_pCallback(pCallback)
			,m_iSelectMenuId(-1)
		{
			Init();
		}

		virtual ~CMenuWnd()
		{

		}

		virtual UINT GetClassStyle() const
		{
			LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
			styleValue |= CS_DROPSHADOW;
			return styleValue;
		}

		virtual LPCTSTR GetWindowClassName(void) const
		{
			return _T("MenuWnd");
		}

		virtual void OnFinalMessage(HWND hWnd)
		{
			if (m_iSelectMenuId >= 0)
				m_pRealOwner->GetManager()->SendNotify(m_pRealOwner, DUI_MSGTYPE_MENUITEM_CLICK, NULL, m_iSelectMenuId, true);
			CWindowWnd::OnFinalMessage(hWnd);
			delete m_pOwner;
			delete this;
		}

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
		{
			if( uMsg == WM_CREATE ) {
				LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
				styleValue &= ~WS_CAPTION;
				::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

				m_paintManager.Init(m_hWnd);
				CDialogBuilder builder;
				CControlUI* pControl = builder.Create(m_sXmlFile.GetData(), NULL, m_pCallback, &m_paintManager);
				if (NULL == pControl) return 0;
				if (NULL == pControl->GetInterface(DUI_CTR_MENU)) return 0;
				CMenuUI* pMenu = static_cast<CMenuUI*>(pControl);
				if (NULL == pMenu) return 0;
				// set the owner of the menu
				pMenu->SetOwner(m_pOwner);
				m_paintManager.AttachDialog(pMenu);

				// send the notify for user to change the menu item
				m_pRealOwner->GetManager()->SendNotify(m_pRealOwner, DUI_MSGTYPE_MENUADDED, (WPARAM)pMenu);
				pMenu->GetHeader()->SetVisible(false);

				// initial the window height
				SIZE cxyBorderRound = pMenu->GetBorderRound();
				m_paintManager.SetRoundCorner(cxyBorderRound.cx, cxyBorderRound.cy);
				int cyHeight = 0, cxWidth = pMenu->GetFixedWidth();
				CMenuItemUI* pMenuItem = NULL;
				for (int i = 0; i < pMenu->GetCount(); ++i)
				{
					pMenuItem = static_cast<CMenuItemUI*>(pMenu->GetItemAt(i));
					if (pMenuItem && pMenuItem->IsVisible()) {
						cyHeight += pMenuItem->GetFixedHeight();
					}
				}
				m_paintManager.SetInitSize(cxWidth, cyHeight);

				if (NULL == m_pRealOwner) return 0;
				if (NULL == m_pRealOwner->GetManager()) return 0;

				POINT mousePos = m_pRealOwner->GetManager()->GetMousePos();
				if (pMenu->GetMenuPaddingEnabled())
				{
					const RECT realOwnerPos = m_pRealOwner->GetPos();
					if (pMenu->GetMenuPadding().right != 0)
					{
						mousePos.x = realOwnerPos.right + pMenu->GetMenuPadding().right;
					}
					else
					{
						mousePos.x = realOwnerPos.left + pMenu->GetMenuPadding().left;
					}
					mousePos.y = realOwnerPos.top + pMenu->GetMenuPadding().top;
				}

				// calculate the position of the context menu
				SIZE size = m_paintManager.GetInitSize();
				RECT rc = {0};
				rc.left = mousePos.x;
				rc.top = mousePos.y;
				if (pMenu->GetTriangleEnabled())
				{
					rc.top = m_pRealOwner->GetPos().top + pMenu->GetTrianglePadding().top;
				}
				rc.right = rc.left + size.cx;
				rc.bottom = rc.top + size.cy;

				if ( m_paintManager.GetBorderSize() != 0 ) {
					rc.right += m_paintManager.GetBorderSize();
					rc.bottom += m_paintManager.GetBorderSize();
				}

				::MapWindowRect(m_pRealOwner->GetManager()->GetPaintWindow(), HWND_DESKTOP, &rc);

				if (!pMenu->GetMenuPaddingEnabled())
				{
					MONITORINFO oMonitor = {0};
					oMonitor.cbSize = sizeof(oMonitor);
					::GetMonitorInfo(::MonitorFromWindow(*this, MONITOR_DEFAULTTOPRIMARY), &oMonitor);
					CDuiRect rcWork = oMonitor.rcWork;
					if (rc.right > rcWork.right)
					{
						rc.right = rc.left;
						rc.left = rc.right - size.cx;
					}
					if (rc.bottom > rcWork.bottom)
					{
						rc.bottom = rc.top;
						rc.top = rc.bottom - size.cy;
					}
				}
				::MoveWindow(*this, rc.left, rc.top, size.cx, size.cy, TRUE);

				// paint the corner and the triangle
				HRGN hRoudRgn = NULL, hTriangleRgn = NULL;
				SIZE szRoundCorner = m_paintManager.GetRoundCorner();
				LONG triangleHeight = pMenu->GetTriangleEnabled()?(pMenu->GetMenuPadding().top - pMenu->GetTrianglePadding().top):0;

				CDuiRect rcWnd(rc);
				rcWnd.Offset(-rcWnd.left, -rcWnd.top);

				if (szRoundCorner.cx != 0 || szRoundCorner.cy != 0) {
					hRoudRgn = ::CreateRoundRectRgn(rcWnd.left, rcWnd.top + triangleHeight, rcWnd.right, rcWnd.bottom, szRoundCorner.cx, szRoundCorner.cy);
				}
				else {
					hRoudRgn = ::CreateRectRgn(rcWnd.left, rcWnd.top + triangleHeight, rcWnd.right, rcWnd.bottom);
				}
				if (pMenu->GetTriangleEnabled()) {
					LONG triangleWidth = triangleHeight * 1000 / 1732;
					POINT trianglePos;
					trianglePos.x = pMenu->GetTrianglePadding().right;
					trianglePos.y = pMenu->GetTrianglePadding().top;
					trianglePos.x += m_pRealOwner->GetPos().right;
					trianglePos.y += m_pRealOwner->GetPos().top;
					::ClientToScreen(m_pRealOwner->GetManager()->GetPaintWindow(), &trianglePos);
					::ScreenToClient(*this, &trianglePos);

					POINT points[3] = {0};

					points[1].x = trianglePos.x;
					points[1].y = trianglePos.y;

					points[0].x = points[1].x - triangleWidth-3;
					points[0].y = rcWnd.top + triangleHeight;

					points[2].x = points[1].x + triangleWidth+3;
					points[2].y = rcWnd.top + triangleHeight;

					hTriangleRgn = ::CreatePolygonRgn(points, 3, ALTERNATE);
				}

				if (NULL != hRoudRgn && NULL != hTriangleRgn) {
					::CombineRgn(hRoudRgn, hRoudRgn, hTriangleRgn, RGN_OR);
					::SetWindowRgn(*this, hRoudRgn, TRUE);
					::DeleteObject(hTriangleRgn);
					::DeleteObject(hRoudRgn);
				}
				else if (NULL != hRoudRgn) {
					::SetWindowRgn(*this, hRoudRgn, TRUE);
					::DeleteObject(hRoudRgn);
				}
				else if (NULL != hTriangleRgn) {
					::SetWindowRgn(*this, hTriangleRgn, TRUE);
					::DeleteObject(hTriangleRgn);
				}

				::SetTimer(*this, PAINT_BORDER_TIMER, 50, NULL);

				return 0;
			}
			else if( uMsg == WM_KILLFOCUS ) {
				if( m_hWnd != (HWND) wParam ) PostMessage(WM_CLOSE, NULL, -1);
			}
			else if ( uMsg == WM_CLOSE ) {
				m_iSelectMenuId = (int)lParam;
			}
			else if ( uMsg == WM_TIMER && wParam == PAINT_BORDER_TIMER ) {
				DWORD dwBorderColor = m_paintManager.GetBorderColor();
				short H, S, L;
				CPaintManagerUI::GetHSL(&H, &S, &L);
				dwBorderColor = CRenderEngine::AdjustColor(dwBorderColor, H, S, L);		
				HBRUSH hBorderBrush = ::CreateSolidBrush(RGB(GetBValue(dwBorderColor), GetGValue(dwBorderColor), GetRValue(dwBorderColor)));
				HRGN hRgn = ::CreateRectRgn(0,0,0,0);
				::GetWindowRgn(*this, hRgn);
				::FrameRgn(m_paintManager.GetPaintDC(), hRgn, hBorderBrush, m_paintManager.GetBorderSize(), m_paintManager.GetBorderSize());
				::DeleteObject(hBorderBrush);
				::DeleteObject(hRgn);
			}
			LRESULT lRes = 0;
			if( m_paintManager.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
			return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
		}

		void Init()
		{
			if (NULL == m_pOwner) return;
			m_pRealOwner = m_pOwner->GetParent();
			// the real owner is null, should release the memory of the owner
			if (NULL == m_pRealOwner) return OnFinalMessage(NULL);

			HWND hOwnerWnd = m_pOwner->GetManager()->GetPaintWindow();
			Create(hOwnerWnd, NULL, WS_VISIBLE|WS_POPUP, WS_EX_TOOLWINDOW|WS_EX_TOPMOST);

			if (!::IsWindow(m_hWnd)) return;
			
			ShowWindow();

			::SetForegroundWindow(m_hWnd);
		}

	private:
		CControlUI* m_pOwner;
		CControlUI* m_pRealOwner;
		CDuiString m_sXmlFile;
		IDialogBuilderCallback* m_pCallback;
		CPaintManagerUI m_paintManager;
		int m_iSelectMenuId;
	};

	CMenuItemUI::CMenuItemUI()
		:m_id(-1)
	{
		m_strNormalImage = _T("");
		m_strHotImage = _T("");
	}

	CMenuItemUI::~CMenuItemUI(void)
	{
	}

	LPCTSTR CMenuItemUI::GetClass() const
	{
		return _T("MenuItemUI");
	}

	LPVOID CMenuItemUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, DUI_CTR_MENUITEM) == 0 ) return static_cast<CMenuItemUI*>(this);
		return CListContainerElementUI::GetInterface(pstrName);
	}

	void CMenuItemUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		if( _tcscmp(pstrName, _T("secondmenu")) == 0 ) 
			m_sSecondMenuXmlFile = pstrValue;
		if( _tcscmp(pstrName, _T("id")) == 0 ) {
			LPTSTR pstr = NULL;
			m_id = _tcstol(pstrValue, &pstr, 10);
		}
		else if (0 == _tcsicmp(pstrName, _T("normalimage")))
			m_strNormalImage = pstrValue;
		else if (0 == _tcsicmp(pstrName, _T("hotimage")))
			m_strHotImage = pstrValue;

		return CListContainerElementUI::SetAttribute(pstrName, pstrValue);
	}

	void CMenuItemUI::SetId(LPCTSTR pstrId)
	{
		LPTSTR pstr = NULL;
		m_id = _tcstol(pstrId, &pstr, 10);
	}

	int CMenuItemUI::GetId() const
	{
		return m_id;
	}

	void CMenuItemUI::DoPaint(HDC hDC, const RECT& rcPaint)
	{
		if( !::IntersectRect(&m_rcPaint, &rcPaint, &m_rcItem) ) return;
		DrawItemBk(hDC, m_rcItem);
		CContainerUI::DoPaint(hDC, rcPaint);	
	}

	void CMenuItemUI::DrawItemBk(HDC hDC, const RECT& rcItem)
	{
		ASSERT(m_pOwner);
		if( m_pOwner == NULL ) return;
		TListInfoUI* pInfo = m_pOwner->GetListInfo();
		DWORD iBackColor = 0;

		if( (m_uButtonState & UISTATE_HOT) != 0 ) {
			iBackColor = pInfo->dwHotBkColor;
		}
		if( IsSelected() ) {
			iBackColor = pInfo->dwSelectedBkColor;
		}
		if( !IsEnabled() ) {
			iBackColor = pInfo->dwDisabledBkColor;
		}
		if ( iBackColor != 0 ) {
			CRenderEngine::DrawColor(hDC, m_rcItem, GetAdjustColor(iBackColor));
		}

		if( !IsEnabled() ) {
			if( !pInfo->sDisabledImage.IsEmpty() ) {
				if( !DrawImage(hDC, (LPCTSTR)pInfo->sDisabledImage) ) pInfo->sDisabledImage.Empty();
				else return;
			}
		}
		if( IsSelected() ) {
			if( !pInfo->sSelectedImage.IsEmpty() ) {
				if( !DrawImage(hDC, (LPCTSTR)pInfo->sSelectedImage) ) pInfo->sSelectedImage.Empty();
				else return;
			}
		}
		if( (m_uButtonState & UISTATE_HOT) != 0 ) {
			if (!m_strHotImage.IsEmpty()){
				if( !DrawImage(hDC, (LPCTSTR)m_strHotImage)) 
					m_strHotImage.Empty();
			}else if( !pInfo->sHotImage.IsEmpty() ) {
				if( !DrawImage(hDC, (LPCTSTR)pInfo->sHotImage) ) pInfo->sHotImage.Empty();
				else return;
			}
		}
		else if ((m_uButtonState & UISTATE_SELECTED) == 0){
			if (!m_strNormalImage.IsEmpty()){
				if( !DrawImage(hDC, (LPCTSTR)m_strNormalImage)) 
					m_strNormalImage.Empty();
			}
		}

		if( !m_sBkImage.IsEmpty() ) {
			if( !DrawImage(hDC, (LPCTSTR)m_sBkImage) ) m_sBkImage.Empty();
		} else {

			if( !pInfo->sAlternateBkImage.IsEmpty() && m_iIndex % 2 == 1 ) {
				if( !DrawImage(hDC, (LPCTSTR)pInfo->sAlternateBkImage) ) pInfo->sAlternateBkImage.Empty();
			} else {
				if( !pInfo->sBkImage.IsEmpty() ) {
					if( !DrawImage(hDC, (LPCTSTR)pInfo->sBkImage) ) pInfo->sBkImage.Empty();
					else return;
				}
			}
		}

		if ( pInfo->dwLineColor != 0 ) {
			RECT rcLine = { m_rcItem.left, m_rcItem.bottom - 1, m_rcItem.right, m_rcItem.bottom - 1 };
			CRenderEngine::DrawLine(hDC, rcLine, 1, GetAdjustColor(pInfo->dwLineColor));
		}
	}

	void CMenuItemUI::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			else CContainerUI::DoEvent(event);
			return;
		}
		if( event.Type == UIEVENT_BUTTONUP ) 
		{
			if (IsEnabled()) {
				if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
				CMenuUI* pOwner = static_cast<CMenuUI*>(m_pOwner);
				if (NULL == pOwner) return;
				// the notify is on the control which has menu
				// and the param is the id of the selected menu
				::PostMessage(GetManager()->GetPaintWindow(), WM_CLOSE, NULL, GetId());
			}
			return;
		}
		if( event.Type == UIEVENT_RBUTTONUP ) 
		{
			if (IsEnabled()) {
				if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
				CMenuUI* pOwner = static_cast<CMenuUI*>(m_pOwner);
				if (NULL == pOwner) return;
				// the notify is on the control which has menu
				// and the param is the id of the selected menu
				::PostMessage(GetManager()->GetPaintWindow(), WM_CLOSE, NULL, GetId());
			}
			return;
		}
		return CListContainerElementUI::DoEvent(event);
	}

	CMenuUI::CMenuUI()
		:m_pOwner(NULL)
		,m_bMenuPaddingEnabled(false)
		,m_bTriangleEnabled(false)
	{
		
	}

	CMenuUI::~CMenuUI(void)
	{
	}

	LPCTSTR CMenuUI::GetClass() const
	{
		return _T("MenuUI");
	}

	LPVOID CMenuUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, DUI_CTR_MENU) == 0 ) return static_cast<CMenuUI*>(this);
		return CListUI::GetInterface(pstrName);
	}

	void CMenuUI::SetOwner(CControlUI* pOwner)
	{
		m_pOwner = pOwner;
	}

	CControlUI* CMenuUI::GetOwner() const
	{
		return m_pOwner;
	}

	void CMenuUI::SetMenuPaddingEnabled(bool bMenuPaddingEnabled)
	{
		m_bMenuPaddingEnabled = bMenuPaddingEnabled;
	}

	bool CMenuUI::GetMenuPaddingEnabled() const
	{
		return m_bMenuPaddingEnabled;
	}

	void CMenuUI::SetMenuPadding(RECT rcPadding)
	{
		m_rcMenuPadding = rcPadding;
	}

	RECT CMenuUI::GetMenuPadding() const
	{
		return m_rcMenuPadding;
	}

	void CMenuUI::SetTrianglePadding(const RECT rcPadding)
	{
		m_trianglePadding = rcPadding;
	}

	RECT CMenuUI::GetTrianglePadding() const
	{
		return m_trianglePadding;
	}

	void CMenuUI::SetTriangleEnabled(bool bTriangleUsed)
	{
		m_bTriangleEnabled = bTriangleUsed;
	}

	bool CMenuUI::GetTriangleEnabled() const
	{
		return (m_bTriangleEnabled&&m_bMenuPaddingEnabled);
	}

	void CMenuUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		if( _tcscmp(pstrName, _T("menupadding")) == 0 ) {
			RECT rcPadding = { 0 };
			LPTSTR pstr = NULL;
			rcPadding.left = _tcstol(pstrValue, &pstr, 10);  ASSERT(pstr);
			rcPadding.top = _tcstol(pstr + 1, &pstr, 10);    ASSERT(pstr);
			rcPadding.right = _tcstol(pstr + 1, &pstr, 10);  ASSERT(pstr);
			rcPadding.bottom = _tcstol(pstr + 1, &pstr, 10); ASSERT(pstr);
			SetMenuPadding(rcPadding);
			SetMenuPaddingEnabled(true);
		}
		else if( _tcscmp(pstrName, _T("trianglepadding")) == 0 ) {
			RECT rcPadding = { 0 };
			LPTSTR pstr = NULL;
			rcPadding.left = _tcstol(pstrValue, &pstr, 10);  ASSERT(pstr);
			rcPadding.top = _tcstol(pstr + 1, &pstr, 10);    ASSERT(pstr);
			rcPadding.right = _tcstol(pstr + 1, &pstr, 10);  ASSERT(pstr);
			rcPadding.bottom = _tcstol(pstr + 1, &pstr, 10); ASSERT(pstr);
			SetTrianglePadding(rcPadding);
			SetTriangleEnabled(true);
		}
		return CListUI::SetAttribute(pstrName, pstrValue);
	}

	void CMenuUI::SetVisible(bool bVisible)
	{
		if (NULL == GetManager()) return;
		if (m_bVisible == bVisible) return;
		SIZE szInitialSize = GetManager()->GetInitSize();
		if (!bVisible) 
			GetManager()->SetInitSize(0, 0);
		else 
			GetManager()->SetInitSize(szInitialSize.cx, szInitialSize.cy);
		CListUI::SetVisible(bVisible);
	}

	void CMenuUI::hiddenMenuItemById(int iMenuId)
	{
		CMenuItemUI* pMenuItem = GetMenuItemById(iMenuId);
		if(NULL != pMenuItem)
		{
			pMenuItem->SetVisible(false);
		}
	}

	CMenuItemUI* CMenuUI::GetMenuItemById(int iMenuId)
	{
		CMenuItemUI* pMenuItem = NULL;
		CControlUI* pControl = NULL;
		for (int i = 0; i < GetCount(); ++i)
		{
			pControl = GetItemAt(i);
			if (NULL == pControl) return NULL;
			pMenuItem = static_cast<CMenuItemUI*>(pControl->GetInterface(DUI_CTR_MENUITEM));
			if (NULL == pMenuItem) continue;
			else {
				if (pMenuItem->GetId() != iMenuId) continue;
				return pMenuItem;
			}
		}
		return NULL;
	}

	CContextMenuUI::CContextMenuUI()
		:m_pWindow(NULL)
		,m_pCallback(NULL)
	{
	}

	CContextMenuUI::~CContextMenuUI(void)
	{
	}

	void CContextMenuUI::Init()
	{
		if (m_sXmlFile.IsEmpty()) return;
		if (NULL != m_pWindow) delete m_pWindow;
		m_pWindow = new CMenuWnd(this, m_sXmlFile, m_pCallback);
	}

	LPCTSTR CContextMenuUI::GetClass() const
	{
		return _T("ContextMenuUI");
	}

	LPVOID CContextMenuUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, DUI_CTR_CONTEXTMENU) == 0 ) return static_cast<CContextMenuUI*>(this);
		return CControlUI::GetInterface(pstrName);
	}

	void CContextMenuUI::SetXmlFile(LPCTSTR pStrXmlFile)
	{
		m_sXmlFile = pStrXmlFile;
	}

	LPCTSTR CContextMenuUI::GetXmlFile()
	{
		return m_sXmlFile;
	}

	void CContextMenuUI::SetDialogBuilder(IDialogBuilderCallback* pCallback)
	{
		m_pCallback = pCallback;
	}

	IDialogBuilderCallback* CContextMenuUI::GetDialogBuilder() const
	{
		return m_pCallback;
	}
}