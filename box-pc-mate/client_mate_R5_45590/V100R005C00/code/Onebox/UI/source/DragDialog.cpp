#include "stdafxOnebox.h"
#include "DragDialog.h"
#include "InILanguage.h"


namespace Onebox
{

	DragDialog::DragDialog() :m_hParentWnd(NULL)
	{
		pLabel = NULL;
		pText = NULL;
		m_pt.x = 0;
		m_pt.y = 0;
		m_hwnd = NULL;
		pName = NULL;
		m_width = 0;
		m_heght = 0;
	}

	DragDialog::~DragDialog()
	{
	}

	LPCTSTR DragDialog::GetWindowClassName() const 
	{ 
		return _T("DragDialog");
	}

	UINT DragDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void DragDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
		 delete this;
	}

	// void DragDialog::Notify(TNotifyUI& msg)
	// {
	// 
	// }


	void DragDialog::Init( LPCTSTR pszXMLPath, HWND hWndParent, POINT pt,std::vector<ItemInfo> info)
	{  
		m_strXMLPath = pszXMLPath;
		m_hParentWnd = hWndParent;
		m_pt.x = pt.x;
		m_pt.y = pt.y;

		if (info.empty())
		{
			return;
		}

		for (unsigned int i=0;i<info.size();i++)
		{
			m_info.push_back(info[i]);
		}
		int nCount = info.size() > FILE_SHOW_NUM ? FILE_SHOW_NUM:info.size();

		m_hwnd = Create(hWndParent, _T("DragDialog"), UI_WNDSTYLE_DIALOG /*& ~WS_VISIBLE*/, WS_EX_TOOLWINDOW);
		/*::SetWindowPos(hwnd,HWND_TOP,0,0,300,400,SWP_SHOWWINDOW);*/
		RECT rc = {0,0,0,0};
		::GetWindowRect(m_hwnd,&rc);	

		pLabel = static_cast<CLabelUI*>(m_DragPaintm.FindControl(L"Draglogo"));
		pText = static_cast<CLabelUI*>(m_DragPaintm.FindControl(L"DragText"));
		pName = static_cast<CLabelUI*>(m_DragPaintm.FindControl(_T("NoveName")));

		m_width = rc.right - rc.left;
		m_heght = rc.bottom - rc.top + (FILE_ICON + FILE_ICON_DISTANT*2)*(nCount-1);

		::ClientToScreen(m_hParentWnd, &m_pt);
		::SetWindowPos(*this, NULL, m_pt.x, m_pt.y , m_width, m_heght, SWP_NOZORDER | SWP_NOSIZE | SWP_NOACTIVATE);
	}

	void DragDialog::SetDialogPos(POINT pt)
	{
		POINT tmp_pt = pt;
		ClientToScreen(m_hParentWnd,&tmp_pt);
		::MoveWindow(m_hwnd,tmp_pt.x,tmp_pt.y,m_width,m_heght,TRUE);
	}

	void DragDialog::SetTransparent(int nOpacity)
	{
		m_DragPaintm.SetTransparent(255 - nOpacity);
	}

	void DragDialog::SetWindowShow(bool isShow)
	{
		ShowWindow(isShow);
	}

	void DragDialog::SetMovedName(std::wstring name)
	{
		pName->SetShowHtml();
		pName->SetAttribute(L"endellipsis",L"true");
		pName->SetText(name.c_str());
	}


	// CControlUI* DragDialog::CreateControl(LPCTSTR pstrClass)
	// {
	// 	if (_tcscmp(pstrClass, _T("SelectFileList")) == 0)
	// 	{
	// 		return new DragDialogList(m_DragPaintm);
	// 	}
	// 	return NULL;
	// }

	void DragDialog::Add(std::vector<ItemInfo> & value)
	{
		if (value.empty())  return;
		CHorizontalLayoutUI* ph = static_cast<CHorizontalLayoutUI*>(m_DragPaintm.FindControl(L"DragDialog"));
		if(ph == NULL)  return;
		RECT rcIcon = pLabel->GetPos();
		RECT rcText = pText->GetPos();

		int nCount = (value.size() > FILE_SHOW_NUM) ? FILE_SHOW_NUM:value.size();		
		for (int i=0;i<nCount;i++)
		{
			if (i == 0)
			{
				pLabel->SetBkImage(value[i].fileIcon.c_str());
				pText->SetText(value[i].fileName.c_str());
				continue;
			}
		
			CLabelUI *label = new CLabelUI;
			ph->Add(label);
			RECT tmpIcon,tmpText;
			tmpIcon.left = rcIcon.left;
			tmpIcon.top = rcIcon.top + (FILE_ICON + FILE_ICON_DISTANT*2)*i;
			tmpIcon.right = rcIcon.right;
			tmpIcon.bottom = rcIcon.bottom + (FILE_ICON + FILE_ICON_DISTANT*2)*i;
			label->SetFloat(true);
			label->SetPos(tmpIcon);
			label->SetBkImage(value[i].fileIcon.c_str());
		
			CTextUI *text = new CTextUI;
			ph->Add(text);
			tmpText.left = rcText.left;
			tmpText.top = rcText.top + (FILE_ICON + FILE_ICON_DISTANT*2)*i;
			tmpText.right = rcText.right;
			tmpText.bottom = rcText.bottom + (FILE_ICON + FILE_ICON_DISTANT*2)*i;
			text->SetFloat(true);
			text->SetPos(tmpText);
			text->SetShowHtml();
			text->SetAttribute(L"endellipsis",L"true");
			text->SetText(value[i].fileName.c_str());
		}
	}

	void DragDialog::AddItem(std::vector<ItemInfo> & value)
	{
		if (value.size() == 0)
		{
			return;
		}
		
		CListUI* pList = static_cast<CListUI*>(m_DragPaintm.FindControl(_T("DragListt")));

		for (unsigned int i=0;i<value.size();i++)
		{
			//CListContainerElementUI* pListElement = new CListContainerElementUI;/*static_cast<CListContainerElementUI*>(m_DragPaintm.FindControl(L"DragList"))*/;
			CListContainerElementUI* pListElement = static_cast<CListContainerElementUI*>(m_DragPaintm.FindControl(L"DragList"));

			if (pListElement == NULL)
				return;
		
			//pListElement = static_cast<CListContainerElementUI*>(m_DragPaintm.FindControl(L"DragList"));
			pListElement->SetTag(i);
			(void)pList->Add(pListElement);

			CLabelUI* pDraglogo = static_cast<CLabelUI*>(m_DragPaintm.FindSubControlByName(pListElement, L"Draglogo"));
			if (pDraglogo != NULL)
			{
				pDraglogo->SetBkImage(value[i].fileIcon.c_str());
			}

			CTextUI* pDragText = static_cast<CTextUI*>(m_DragPaintm.FindSubControlByName(pListElement, L"DragText"));
			if (pDragText != NULL)
			{
				pDragText->SetText(value[i].fileName.c_str());
			}
		}
	}

	LRESULT DragDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
			styleValue &= ~WS_CAPTION;
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_DragPaintm.Init(m_hWnd);
		m_DragPaintm.SetResourcePath(std::wstring(GetInstallPath() + iniLanguageHelper.GetSkinFolderPath()).c_str());
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(m_strXMLPath.GetData(), (UINT)0, 0,&m_DragPaintm);
		ASSERT(pRoot && "Failed to parse XML");
		m_DragPaintm.AttachDialog(pRoot);
		m_DragPaintm.SetShadow();
		return 0;
	}

	LRESULT DragDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT DragDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT DragDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT DragDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT DragDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT DragDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return HTCLIENT;
	}


	LRESULT DragDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
	//	Close();
		return 0 ;
	}

	LRESULT DragDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
	//	Close();
		return 0 ;
	}


	LRESULT DragDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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

}