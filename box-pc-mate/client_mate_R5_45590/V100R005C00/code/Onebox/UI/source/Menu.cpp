#include "Menu.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "Utility.h"

MenuElementUI::MenuElementUI(UINT ID /* = 0 */,bool isEnable,int64_t ownerID,int64_t fileID,bool isShowToolTip)
	: uID(ID),
	uOwenrID(ownerID),
	uFileID(fileID),
	m_pWnd(NULL),
	m_Layout(NULL),
	m_Text(NULL),
	m_Icon(NULL),
	m_bDrawLine(false),
	m_bextend(false),
	m_lineColor(0xFFFFFFFF),
	m_iconWidth(0)
{
	m_isEnable = isEnable;
	m_isShowToolTip = isShowToolTip;
	m_Layout = new CHorizontalLayoutUI;
	RECT rc = {0,0,0,0};
	m_Layout->SetInset(rc);
	m_Layout->SetMouseChildEnabled(m_isEnable);
	m_Layout->SetMouseEnabled(m_isEnable);
	m_Layout->SetEnabled(m_isEnable);
	Add(m_Layout);
}

void MenuElementUI::SetText(LPCTSTR pstrText, DWORD textColor, CMenuUI::menuLayout textalign /* = CMenuUI::TEXT_ALIGN_LEFT_DEFAULT */)
{
	if (m_Text == NULL) m_Text = new CLabelUI;
	m_Text->SetShowHtml(true);
	if (textalign == CMenuUI::TEXT_ALIGN_CENTER)
		m_Text->SetAttribute(_T("align"), _T("left"));
	else if (textalign == CMenuUI::TEXT_ALIGN_RIGHT)
		m_Text->SetAttribute(_T("align"), _T("left"));
	m_Text->SetTextColor(textColor);
	m_Text->SetDisabledTextColor(0x00000000);
	m_Text->SetFont(0);

	std::wstring str_name = pstrText;
	if (m_isShowToolTip)
	{
		if (str_name.length() > 10)
		{
			str_name = str_name.substr(0,7);
			str_name += L"...";
		}
		m_Text->SetToolTip(pstrText);
	}
	m_Text->SetText(str_name.c_str());
	m_Text->SetEnabled(m_isEnable);
	RECT rc = {8,8,15,9};
	m_Text->SetTextPadding(rc);
	m_Layout->Add(m_Text);
}

void MenuElementUI::SetExplandIcon(LPCTSTR iconName /* = ExplandIcon */)
{
	if (EXPLAND_ICON_WIDTH <= 0)
		return;

	CDuiString str;
	if (iconName != _T(""))
	{
		str.Format(_T("{i %s }"), iconName);
	}

	CLabelUI* icon = new CLabelUI;
	icon->SetShowHtml(true);
	icon->SetText(str);
	icon->SetFixedWidth(EXPLAND_ICON_WIDTH);
	m_Layout->Add(icon);
}

void MenuElementUI::SetIcon(int width, LPCTSTR iconName /* = _T */)
{
	if (m_Icon == NULL) m_Icon = new CLabelUI;
	m_Icon->SetFixedWidth(width);
	m_Icon->SetShowHtml(true);
	m_Icon->SetBkImage(iconName);
	m_Layout->AddAt(m_Icon, 0);
}

bool MenuElementUI::AddCtrl(CControlUI* pControl)
{
	return m_Layout->Add(pControl);
}

void MenuElementUI::SetLine(bool isline, DWORD col, int width)
{
	m_bDrawLine = isline;
	m_lineColor = col;
	m_iconWidth = width;
}

void MenuElementUI::DrawItemBk(HDC hDC, const RECT& rcItem)
{
	if (m_bDrawLine)
	{
		RECT rcLine = {m_rcItem.left + m_iconWidth, m_rcItem.bottom - 3, m_rcItem.right - m_rcItem.left, m_rcItem.bottom - 3};
		CRenderEngine::DrawLine(hDC, rcLine, 1, m_lineColor);
	}
	else
	{
		return CListContainerElementUI::DrawItemBk(hDC, rcItem);
	}
}

LPVOID MenuElementUI::GetInterface(LPCTSTR pstrName)
{
	if (_tcsicmp(pstrName, _T("MenuElement")) == 0)
		return static_cast<MenuElementUI*>(this);
	return CListContainerElementUI::GetInterface(pstrName);
}

void MenuElementUI::DoPaint(HDC hDC, const RECT& rcPaint)
{
	if (!::IntersectRect(&m_rcPaint, &rcPaint, &m_rcItem)) return ;
	DrawItemBk(hDC, m_rcItem);
	CContainerUI::DoPaint(hDC, rcPaint);
}

CMenuUI* MenuElementUI::CreateWnd(POINT& pt, HWND hParent, int nLeftPos)
{
	m_pWnd = new CMenuUI;
	ASSERT(m_pWnd != NULL);
	SetExplandIcon();
	m_pWnd->InitWnd(pt, hParent, this, nLeftPos);
	return m_pWnd;
}

void MenuElementUI::DoEvent(TEventUI& event)
{
	if (event.Type == UIEVENT_MOUSEENTER)
	{
		if (GetIndex() == m_pOwner->GetCurSel())
			return ;

		this->Select();

		ContextMenuParam param;
		param.hWnd = m_pManager->GetPaintWindow();
		param.wParam = 2;
		WndsVector::BroadCast(param);
		if (m_pWnd != NULL)
		{
			if (m_bextend)
			{
				m_pWnd->ShowWindow(true);
			}
			else
			{
				m_bextend = !m_bextend;
				m_pWnd->SetPos();
				m_pWnd->UpdateWnd();
			}
		}

		return;
	}
	else if (event.Type == UIEVENT_BUTTONDOWN)
	{
		if (m_pWnd == NULL)
		{
			PostMessage(WndsVector::rootHwnd, uID, (WPARAM)uOwenrID, (LPARAM)uFileID);
			ContextMenuParam param;
			param.hWnd = NULL;
			param.wParam = 1;
			WndsVector::BroadCast(param);

			return;
		}
	}

	return CListContainerElementUI::DoEvent(event);
}

///////////////////////////////////////////////////
CMenuUI::CMenuUI(menuLayout textalign /* = TEXT_ALIGN_LEFT_DEFAULT */)
	: m_itemHeight(ITEM_DEFAULT_HEIGHT),
	m_itemWidth(ITEM_DEFAULT_WIDTH),
	m_hParent(NULL),
	m_pOwner(NULL),
	m_wndHeight(0),
	m_leftWidth(0),
	m_textAlign(textalign)
{
	m_list = NULL;
	m_basePoint.x = 0;
	m_basePoint.y = 0;
}

CMenuUI::~CMenuUI(void)
{

}

LPVOID CMenuUI::GetInterface(LPCTSTR pstrName)
{
	if (_tcsicmp(pstrName, _T("Menu")) == 0) 
		return static_cast<CMenuUI*>(this);
	return NULL;
}

LRESULT CMenuUI::OnCreate(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle)
{
	LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
	styleValue &= ~WS_CAPTION;
	::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

	m_pm.Init(m_hWnd);
	m_pm.SetResourcePath(std::wstring(GetInstallPath() + iniLanguageHelper.GetSkinFolderPath()).c_str());
	m_list = new CListUI;
	m_list->SetManager(&m_pm, NULL);
	m_list->ApplyAttributeList(MenuDefaultList);
	m_list->SetBkImage(MenuBkImage);

	m_pm.AttachDialog(m_list);
	m_pm.SetBackgroundTransparent(TRUE);

	m_wndHeight = 44;
	if (m_pOwner == NULL) //首次创建
	{
		WndsVector::RemoveAll();
		WndsVector::rootHwnd = m_hParent;
	}
	
	WndsVector::AddWnd(this);
	return 0;
}

LRESULT CMenuUI::OnKillFocus(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle)
{
	HWND hFocusWnd = (HWND)wParam;
	if (hFocusWnd == m_hWnd) return 0;	
	if (hFocusWnd != NULL && WndsVector::FindWndClass(hFocusWnd) != NULL)
	{
		Close();
		return 0;
	}

	ContextMenuParam param;
	param.hWnd = GetHWND();
	param.wParam = 1;
	WndsVector::BroadCast(param);
	return 0;
}

LRESULT CMenuUI::OnDestroy(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle)
{
	bHandle = FALSE;
	if (m_hParent == WndsVector::rootHwnd)
		WndsVector::RemoveAll();

	return 0;
}

void CMenuUI::ReceiveCloseMsg(ContextMenuParam& param)
{
	switch (param.wParam)
	{
	case 1:
		if (m_hParent = WndsVector::rootHwnd)
		{
			Close();
		}
		break;
	case 2:
		{
			HWND hParent = GetParent(m_hWnd);
			while (hParent != NULL)
			{
				if (hParent == param.hWnd)
				{
					ShowWindow(false);
					break;
				}

				hParent = GetParent(hParent);
			}
		}
		break;
	default:
		break;
	}
}

CMenuUI* CMenuUI::Add(LPCTSTR contentKey, UINT itemID,LPCTSTR iconImageName,bool isEnable,DWORD textcol, int height, bool extend,std::wstring fileInfo)
{
	if (height <= 0)
		height = m_itemHeight;

	std::wstring str_name = contentKey;
	int64_t ownerId = 0;
	int64_t fileId = 0;
	bool bIsShowTootip = false;
	if (0 == fileInfo.length())
	{
		str_name = GetMenuContent(contentKey);
	}
	else
	{
		std::vector<std::wstring> vecInfo;  
		SD::Utility::String::split(fileInfo, vecInfo, L"_");
		ownerId = SD::Utility::String::string_to_type<int64_t>(vecInfo[0]);
		fileId = SD::Utility::String::string_to_type<int64_t>(vecInfo[1]);
		bIsShowTootip = true;
	}
	MenuElementUI* new_node = new MenuElementUI(itemID,isEnable,ownerId,fileId,bIsShowTootip);
	new_node->SetText(str_name.c_str(), textcol, m_textAlign);
	new_node->SetFixedHeight(height);
	m_list->Add(new_node);
	m_wndHeight += height;
	//文本居中时不支持扩展和左边图标
	if (m_textAlign == TEXT_ALIGN_CENTER)
	{
		return NULL;
	}

	if (m_leftWidth > 4)
	{
		new_node->SetIcon(m_leftWidth+5,iconImageName);
	}

	//文本右对齐时不支持扩展
	if (m_textAlign == TEXT_ALIGN_RIGHT)
	{
		return NULL;
	}

	if (extend)
	{
		POINT pt =  {0, 0};
		CMenuUI* wndPtr = new_node->CreateWnd(pt, m_hWnd, m_leftWidth);
		if(NULL==wndPtr) return NULL;
		wndPtr->SetItemWidth(m_itemWidth);
		wndPtr->SetItemHeight(m_itemHeight);
		return wndPtr;
	}
	else
	{
		return NULL;
	}
}


CMenuUI* CMenuUI::Add(MenuElementUI* pctrl, int height /* = 0 */, bool extend /* = false */)
{
	if (height <= 0)
		height = m_itemHeight;
	pctrl->SetFixedHeight(height);
	m_list->Add(pctrl);
	m_wndHeight += height;
	if (extend)
	{
		POINT pt = {0, 0};
		CMenuUI* wndPtr = pctrl->CreateWnd(pt, m_hWnd, m_leftWidth);
		if(NULL==wndPtr) return NULL;
		wndPtr->SetItemWidth(m_itemWidth);
		wndPtr->SetItemHeight(m_itemHeight);
		return wndPtr;
	}
	else
	{
		return NULL;
	}
}

void CMenuUI::AddLine(DWORD color /* = DEFAULT_LINE_COLOR */)
{
	MenuElementUI * new_node = new MenuElementUI();
	new_node->SetLine(true, color, m_leftWidth);
	new_node->SetFixedHeight(DEFAULT_LINE_HEIGHT);
	m_list->Add(new_node);
	m_wndHeight += DEFAULT_LINE_HEIGHT;
}

void CMenuUI::SetItemHeight(int height /* = ITEM_DEFAULT_HEIGHT */)
{
	m_itemHeight = height;
}

void CMenuUI::SetItemWidth(int width /* = ITEM_DEFAULT_WIDTH */)
{
	m_itemWidth = width;
}

void CMenuUI::InitWnd(POINT& pt, HWND hParent /* = NULL */, MenuElementUI* pOwner /* = NULL */, int nLeftPos /* = ITEM_DEFAULT_WIDTH */)
{
	m_basePoint = pt;
	m_hParent = hParent;
	m_pOwner = pOwner;
	if (m_textAlign == TEXT_ALIGN_CENTER)
		m_leftWidth = 4;
	else
		m_leftWidth = nLeftPos;

	Create((m_pOwner == NULL) ? NULL : m_pOwner->GetManager()->GetPaintWindow(), NULL, WS_POPUP, WS_EX_TOOLWINDOW | WS_EX_TOPMOST, CDuiRect());

}

POINT CMenuUI::GetPos()
{
	return m_basePoint;
}
 void CMenuUI::SetPos()
 {
	 if (m_pOwner != NULL)
	 {
		 CMenuUI* parent = WndsVector::FindWndClass(m_hParent);
		 ASSERT(parent != NULL);
		 POINT pt = parent->GetPos();
		 CDuiRect rc = m_pOwner->GetPos();
		 m_basePoint.x = pt.x + m_itemWidth;
		 m_basePoint.y = pt.y + rc.top;
	 }
 }

 void CMenuUI::AddMenuHeader(int height /* = HEADER_DEFAULT_HEIGHT */, LPCTSTR text /* = _T */, LPCTSTR bkimagename/* =""*/)
 {
	 m_list->SetAttribute(_T("header"), _T("true"));
	 CListHeaderItemUI* tt = new CListHeaderItemUI;
	 tt->SetFixedHeight(height);
	 tt->SetText(text);
	 tt->SetBkImage(bkimagename);
	 m_list->Add(tt);
	 m_wndHeight += height;
 }

 void CMenuUI::UpdateWnd(bool isTrayIcon)
 {
	 ASSERT(m_leftWidth < m_itemWidth && _T("the m_leftWidth is larger than"));
	 int num = m_list->GetCount();
	 if (num == 0)
	 {
		 Close();
		 return ;
	 }
	 if (!isTrayIcon)
	 {		
		 //非托盘菜单需检测窗体是否超出窗口
		 RECT parentRc;
		 GetWindowRect(m_hParent,&parentRc);
		 if (m_basePoint.x + m_itemWidth > parentRc.right) //看右边是否越界
			 m_basePoint.x - m_itemWidth > parentRc.left ? m_basePoint.x -= m_itemWidth : parentRc.left + 4;
		 if (m_basePoint.y + m_wndHeight >parentRc.bottom) //看下边是否越界
			 m_basePoint.y - m_wndHeight > parentRc.top ? m_basePoint.y -= m_wndHeight : parentRc.top + 4;
	 }
	 //检测窗体是否超出桌面
	 MONITORINFO oMonitor = {};
	 oMonitor.cbSize = sizeof(oMonitor);
	 ::GetMonitorInfo(::MonitorFromWindow(*this, MONITOR_DEFAULTTOPRIMARY), &oMonitor);
	 CDuiRect rcWork = oMonitor.rcWork;
	 if (m_basePoint.x - m_itemWidth < rcWork.left) //看左边是否越界
		 m_basePoint.x + m_itemWidth <m_itemWidth ? m_basePoint.x+= m_itemWidth: m_itemWidth + 4;
	 if (m_basePoint.x + m_itemWidth > rcWork.right) //看右边是否越界
		 m_basePoint.x - m_itemWidth > rcWork.left ? m_basePoint.x -= m_itemWidth : rcWork.left + 4;
	 if (m_basePoint.y + m_wndHeight > rcWork.bottom) //看下边是否越界
		 m_basePoint.y - m_wndHeight > rcWork.top ? m_basePoint.y -= m_wndHeight : rcWork.top + 4;

	 SetWindowPos(m_hWnd, HWND_TOPMOST, m_basePoint.x, m_basePoint.y, m_itemWidth, m_wndHeight, SWP_SHOWWINDOW);

	 SetForegroundWindow(m_hWnd);
 }

void CMenuUI::OnFinalMessage(HWND hWnd)
{
	delete this;
}

LRESULT CMenuUI::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	LRESULT lRes = 0;
	BOOL bHandled = TRUE;

	switch (uMsg)
	{
	case WM_CREATE:
		lRes = OnCreate(uMsg, wParam, lParam, bHandled);
		break;
	case WM_KILLFOCUS:
		lRes = OnKillFocus(uMsg, wParam, lParam, bHandled);
		break;
	case WM_DESTROY:
		lRes = OnDestroy(uMsg, wParam, lParam, bHandled);
		break;
	default:
		bHandled = FALSE;
		break;
	}

	if (bHandled)
		return lRes;

	if (m_pm.MessageHandler(uMsg, wParam, lParam, lRes))
	{
		return lRes;
	}

	return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
}

std::wstring CMenuUI::GetMenuContent(std::wstring key)
{
	CInIHelper InIHelper(SD::Utility::String::format_string(iniLanguageHelper.GetLanguageFilePath().c_str()));
	std::wstring _return =  InIHelper.GetString(LANGUAGE_RIGHTMENU_SECTION,key,L"");
	return _return;
}


WndsVector::ReceiversVector WndsVector::receiverWnd;
HWND WndsVector::rootHwnd = NULL;

