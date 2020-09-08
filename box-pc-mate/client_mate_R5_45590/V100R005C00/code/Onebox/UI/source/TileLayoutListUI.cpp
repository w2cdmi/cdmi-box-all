#include "stdafxOnebox.h"
#include "TileLayoutListUI.h"
#include "ListContainerElement.h"
#include "SkinConfMgr.h"
#include "Utility.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ItemCheckBox.h"
#include "UICommonDefine.h"

using namespace SD::Utility;

namespace Onebox
{
	double CTileLayoutListUI::CalculateDelay(double state)
	{
		return pow(state, 2);
	}

	CTileLayoutListUI::CTileLayoutListUI()
		:m_bScrollSelect(false) 
		, m_iCurSel(-1)
		, m_nColumns(1)
		, m_lastSelectIndex(1)
		, m_iDelayDeltaY(0)
		, m_iDelayNumber(0)
		, m_iDelayLeft(0)
		, m_pDragDialog(NULL)
		, m_iCurEnter(-1)
		, isDragable_(true)
		,m_selects(sizeof(int))
	{

		m_szItem.cx = m_szItem.cy = 0;

		m_ListInfo.nColumns = 0;
		m_ListInfo.nFont = -1;
		m_ListInfo.uTextStyle = DT_VCENTER; // m_uTextStyle(DT_VCENTER | DT_END_ELLIPSIS)
		m_ListInfo.dwTextColor = 0xFF000000;
		m_ListInfo.dwBkColor = 0;
		m_ListInfo.dwSelectedTextColor = 0xFF000000;
		m_ListInfo.dwSelectedBkColor = 0xFFC1E3FF;
		m_ListInfo.dwHotTextColor = 0xFF000000;
		m_ListInfo.dwHotBkColor = 0xFFE9F5FF;
		m_ListInfo.dwDisabledTextColor = 0xFFCCCCCC;
		m_ListInfo.dwDisabledBkColor = 0xFFFFFFFF;
		m_ListInfo.dwLineColor = 0;
		m_ListInfo.bShowHtml = false;
		m_ListInfo.bMultiExpandable = false;
		::ZeroMemory(&m_ListInfo.rcTextPadding, sizeof(m_ListInfo.rcTextPadding));
		::ZeroMemory(&m_ListInfo.rcColumn, sizeof(m_ListInfo.rcColumn));
		clearDropFile();

		m_wSelectWnd = NULL;
		m_hParentHwnd = 0;
		m_pPaintManager = NULL;

		m_iMouseMoveType = 0;
		m_iSelStartIndex = -1;
		m_iSelEndIndex = -1;

		m_bEncloseSelect = false;
		m_pButtonDownPoint.x = -1;
		m_pButtonDownPoint.y = -1;
		m_pButtonUpPoint.x = -1;
		m_pButtonUpPoint.y = -1;
		m_pButtonMovePoint.x = -1;
		m_pButtonMovePoint.y = -1;
		m_pScrollDownPoint.cx = -1;
		m_pScrollDownPoint.cy = -1;
	}

	TListInfoUI* CTileLayoutListUI::GetListInfo()
	{
		return &m_ListInfo;
	}

	int CTileLayoutListUI::GetCurSel() const
	{
		return m_iCurSel;
	}
	
	LPVOID CTileLayoutListUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, _T("TileLayoutList")) == 0 ) return this;
		if( _tcscmp(pstrName, _T("IListOwner")) == 0 ) return static_cast<IListOwnerUI*>(this);
		return CTileLayoutUI::GetInterface(pstrName);
	}

	bool CTileLayoutListUI::Add(CControlUI* pControl)
	{
		// The list items should know about us
		IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
		if( pListItem != NULL ) {
			pListItem->SetOwner(this);
			pListItem->SetIndex(GetCount());
		}
		return CContainerUI::Add(pControl);
	}

	bool CTileLayoutListUI::AddAt(CControlUI* pControl, int iIndex)
	{
		if (!CContainerUI::AddAt(pControl, iIndex)) return false;

		// The list items should know about us
		IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
		if( pListItem != NULL ) {
			pListItem->SetOwner(this);
			pListItem->SetIndex(iIndex);
		}

		for(int i = iIndex + 1; i < CContainerUI::GetCount(); ++i) {
			CControlUI* p = CContainerUI::GetItemAt(i);
			if(NULL == p) continue;
			pListItem = static_cast<IListItemUI*>(p->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem != NULL ) {
				pListItem->SetIndex(i);
			}
		}
		if( m_iCurSel >= iIndex ) 
			m_iCurSel += 1;
		return true;
	}

	bool CTileLayoutListUI::Remove(CControlUI* pControl)
	{
		int iIndex = CContainerUI::GetItemIndex(pControl);
		if (iIndex == -1) return false;

		if (!CContainerUI::RemoveAt(iIndex)) return false;

		for(int i = iIndex; i < CContainerUI::GetCount(); ++i) {
			CControlUI* p = CContainerUI::GetItemAt(i);
			if(NULL == p) continue;
			IListItemUI* pListItem = static_cast<IListItemUI*>(p->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem != NULL ) {
				pListItem->SetIndex(i);
			}
		}

		if( iIndex == m_iCurSel && m_iCurSel >= 0 ) {
			int iSel = m_iCurSel;
			m_iCurSel = -1;
			SelectItem(FindSelectable(iSel, false));
		}
		else if( iIndex < m_iCurSel ) m_iCurSel -= 1;
		return true;
	}

	bool CTileLayoutListUI::RemoveAt(int iIndex)
	{
		if (!CContainerUI::RemoveAt(iIndex)) return false;

		for(int i = iIndex; i < CContainerUI::GetCount(); ++i) {
			CControlUI* p = CContainerUI::GetItemAt(i);
			if(NULL==p) continue;
			IListItemUI* pListItem = static_cast<IListItemUI*>(p->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem != NULL ) pListItem->SetIndex(i);
		}

		if( iIndex == m_iCurSel && m_iCurSel >= 0 ) {
			int iSel = m_iCurSel;
			m_iCurSel = -1;
			SelectItem(FindSelectable(iSel, false));
		}
		else if( iIndex < m_iCurSel ) m_iCurSel -= 1;
		return true;
	}

	void CTileLayoutListUI::RemoveAll()
	{
		m_iCurSel = -1;
		m_selects.Empty();
		CContainerUI::RemoveAll();
		selectChanged();
	}

	SIZE CTileLayoutListUI::GetItemSize() const
	{
		return m_szItem;
	}

	void CTileLayoutListUI::SetItemSize(SIZE szItem)
	{
		if( m_szItem.cx != szItem.cx || m_szItem.cy != szItem.cy ) {
			m_szItem = szItem;
			NeedUpdate();
		}
	}

	int CTileLayoutListUI::GetColumns() const
	{
		return m_nColumns;
	}

	void CTileLayoutListUI::SetColumns(int nCols)
	{
		if( nCols <= 0 ) return;
		m_nColumns = nCols;
		NeedUpdate();
	}

	void CTileLayoutListUI::EnsureVisible(int iIndex)
	{
		if( m_iCurSel < 0 ) return;
		if(NULL == CContainerUI::GetItemAt(iIndex)) return;
		RECT rcItem = CContainerUI::GetItemAt(iIndex)->GetPos();
		RECT rcList = CContainerUI::GetPos();
		RECT rcListInset = CContainerUI::GetInset();

		rcList.left += rcListInset.left;
		rcList.top += rcListInset.top;
		rcList.right -= rcListInset.right;
		rcList.bottom -= rcListInset.bottom;

		CScrollBarUI* pHorizontalScrollBar = CContainerUI::GetHorizontalScrollBar();
		if( pHorizontalScrollBar && pHorizontalScrollBar->IsVisible() ) rcList.bottom -= pHorizontalScrollBar->GetFixedHeight();

		if( rcItem.top >= rcList.top && rcItem.bottom < rcList.bottom ) return;
		int dx = 0;
		if( rcItem.top < rcList.top ) dx = rcItem.top - rcList.top;
		if( rcItem.bottom > rcList.bottom ) dx = rcItem.bottom - rcList.bottom;
		Scroll(0, dx);
	}

	void CTileLayoutListUI::Scroll(int dx, int dy)
	{
		if( dx == 0 && dy == 0 ) return;
		SIZE sz = CContainerUI::GetScrollPos();
		CContainerUI::SetScrollPos(CSize(sz.cx + dx, sz.cy + dy));
	}
	/*
	void CTileLayoutListUI::resumeLastRename()
	{
		if(-1==m_iCurSel)
		{
			return;
		}
		CControlUI* pControl = GetItemAt(m_iCurSel);
		if( pControl == NULL) return;
		CContainerUI* pListItem = static_cast<CContainerUI*>(pControl->GetInterface(DUI_CTR_CONTAINER));
		if( pListItem == NULL ) return;
		CRichEditUI* creui = static_cast<CRichEditUI*>(pListItem->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
		if (NULL == creui) return;
		creui->SetBorderSize(0);
		creui->SetMouseEnabled(false);
		pListItem->SetMouseChildEnabled(false);
		m_pManager->SendNotify(pControl,DUI_MSGTYPE_RETURN);
	}*/

	bool CTileLayoutListUI::SelectItem(int iIndex, bool bMultiSelect)
	{
		BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
		/*CControlUI* pControl = GetItemAt(iIndex);
		if( pControl == NULL) return false;
		IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
		if( pListItem == NULL ) return false;*/

		m_iCurSel = iIndex;

		bool isSelected = false;
		for (int i = 0; i < m_selects.GetSize(); ++i)
		{
			if (*(int*)m_selects[i] == iIndex)
			{
				isSelected = true;
				break;
			}
		}

		if(bMultiSelect)
		{
			if( !isSelected )
			{
				m_selects.Add(&iIndex);
			}
		}
		else if(bCtrl)
		{
			//ctrl + click blank:do nothing
			if( iIndex < 0 ) return false;
			if( !isSelected )
			{
				m_selects.Add(&iIndex);
			}
		}
		// when the curItem not selected, We should unselect the currently selected item 
		else
		{
			for (int i = 0; i < m_selects.GetSize(); ++i)
			{
				CControlUI* pControl = GetItemAt(*(int*)m_selects[i]);
				if( pControl == NULL) continue;
				IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
				if( pListItem == NULL ) continue;
				if (pListItem->GetIndex() != iIndex )
				{
					pListItem->Select(false,false);
				}
			}
			m_selects.Empty();
			if( iIndex >= 0 )
			{
				m_selects.Add(&iIndex);
			}
		}
		if( m_pManager != NULL ) m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTCHANGED, iIndex, m_iCurSel);
		return true;
	}

	bool CTileLayoutListUI::SelectRange(int iIndex, bool bTakeFocus)
	{
		if ( MOUSE_MOVE_TYPE_SELECT == m_iMouseMoveType)
		{
			POINT pt = m_pButtonDownPoint;
			pt.y = m_pButtonDownPoint.y - (this->GetScrollPos().cy - this->m_pScrollDownPoint.cy);
			return SelectRange( pt, m_pManager->GetMousePos());
		}

		int i = 0;
		int iFirst = m_iCurSel > iIndex ? iIndex : m_iCurSel;
		int iLast = m_iCurSel > iIndex ? m_iCurSel : iIndex;
		iLast++;
		int iCount = GetCount();
		m_selects.Empty();

		if(iFirst == iLast) return true;
		CControlUI* pControl = GetItemAt(iIndex);
		if( pControl == NULL ) return false;
		if( !pControl->IsVisible() ) return false;
		if( !pControl->IsEnabled() ) return false;

		IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(_T("ListItem")));
		if( pListItem == NULL ) return false;
		if( !pListItem->Select(true,false) ) {
			m_iCurSel = -1;
			return false;
		}
		EnsureVisible(iIndex);
		if( bTakeFocus ) pControl->SetFocus();
		if( m_pManager != NULL ) {
			m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMSELECT, iIndex, m_iCurSel);
		}
		//locate (and select) either first or last
		// (so order is arbitrary)
		while(i<iFirst){
			CControlUI* pControl = GetItemAt(i);
			if( pControl != NULL) {
				IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(_T("ListItem")));
				if( pListItem != NULL ) pListItem->Select(false,false);
			}
			i++;
		}

		// select rest of range
		while(i<iLast){
			CControlUI* pControl = GetItemAt(i);
			if( pControl != NULL) {
				IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(_T("ListItem")));
				if( pListItem != NULL ) 
				{
					m_selects.Add(&i);
					pListItem->Select(true,false);
				}
			}
			i++;
		}

		// unselect rest of range
		while(i<iCount){
			CControlUI* pControl = GetItemAt(i);
			if( pControl != NULL) {
				IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(_T("ListItem")));
				if( pListItem != NULL ) pListItem->Select(false,false);
			}
			i++;
		}
		if( m_pManager != NULL ) m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTCHANGED, iIndex, m_iCurSel);
		return true;
	}

	bool CTileLayoutListUI::SelectRange(POINT ptstart, POINT ptend)
	{
		int iCount = GetCount();
		RECT rctemp;
		m_selects.Empty();

		rctemp.left = ptstart.x < ptend.x ? ptstart.x : ptend.x;
		rctemp.top = ptstart.y < ptend.y ? ptstart.y : ptend.y;
		rctemp.right = ptstart.x < ptend.x ? ptend.x : ptstart.x;
		rctemp.bottom = ptstart.y < ptend.y ? ptend.y : ptstart.y;

		for (int i=0; i<iCount; i++)
		{
			CControlUI* pControl = GetItemAt(i);
			if( pControl != NULL) {
				IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
				if( pListItem == NULL ) continue;

				RECT rccontrol = pControl->GetPos();
				if ( rctemp.left < rccontrol.left && rctemp.top < rccontrol.top && rctemp.right > rccontrol.right && rctemp.bottom > rccontrol.bottom
					|| rctemp.left < rccontrol.left && rctemp.right > rccontrol.right && rctemp.top > rccontrol.top && rctemp.top < rccontrol.bottom
					|| rctemp.top > rccontrol.top && rctemp.top < rccontrol.bottom && rctemp.right > rccontrol.left && rctemp.right < rccontrol.right
					|| rctemp.right > rccontrol.left && rctemp.right < rccontrol.right && rctemp.top < rccontrol.top && rctemp.bottom > rccontrol.bottom
					|| rctemp.bottom > rccontrol.top && rctemp.bottom < rccontrol.bottom && rctemp.right > rccontrol.left && rctemp.right < rccontrol.right
					|| rctemp.bottom > rccontrol.top && rctemp.bottom < rccontrol.bottom && rctemp.left < rccontrol.left && rctemp.right > rccontrol.right
					|| rctemp.left > rccontrol.left && rctemp.left < rccontrol.right && rctemp.bottom > rccontrol.top && rctemp.bottom < rccontrol.bottom
					|| rctemp.left > rccontrol.left && rctemp.left < rccontrol.right && rctemp.top < rccontrol.top && rctemp.bottom > rccontrol.bottom
					|| rctemp.top > rccontrol.top && rctemp.top < rccontrol.bottom && rctemp.left > rccontrol.left && rctemp.left < rccontrol.right
					)
				{
					if( !pListItem->IsSelected() ) pListItem->Select(true, false);
					
					int index = pListItem->GetIndex();
					m_selects.Add(&index);
				}
				else
				{
					if( pListItem->IsSelected() ) pListItem->Select(false,false);
				}
			}
		}
		if( m_pManager != NULL ) m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTCHANGED, m_iCurSel, m_iCurSel);
		return true;
	}

	void CTileLayoutListUI::selectChanged()
	{
		m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTITEMCHANGED);
	}
	
	void CTileLayoutListUI::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) 
		{
			if( m_pParent != NULL ) 
				m_pParent->DoEvent(event);
			else 
				CTileLayoutUI::DoEvent(event);
			return;
		}
		if( event.Type == UIEVENT_SETFOCUS ) 
		{
			m_bFocused = true;
			return;
		}
		if( event.Type == UIEVENT_KILLFOCUS ) 
		{
			m_bFocused = false;
			return;
		}
		if( event.Type == UIEVENT_BUTTONDOWN )
		{
			if ( MOUSE_MOVE_TYPE_INVALID == GetMouseMoveType() && IsEncloseSelect() )
			{
				SelectItem(-1);
				SetMouseMoveType(MOUSE_MOVE_TYPE_SELECT);
				m_pButtonDownPoint = event.ptMouse;
				m_pScrollDownPoint = this->GetScrollPos();
				ShowSelectWnd(event.ptMouse, event.ptMouse, true);
			}
			return;
		}
		if ( event.Type == UIEVENT_MOUSEMOVE || UIEVENT_MOUSEHOVER == event.Type )
		{
			if ( MOUSE_MOVE_TYPE_DRAG == m_iMouseMoveType )
			{
				if(!IsDropEnabled()) return;
				if(!isDragable_) return;
				std::wstring listItemName = GetName().GetData();
				if(event.wParam == Event_ChildSend && m_pDragDialog == NULL)
				{
					std::vector<ItemInfo> info;
					for (int i = 0; i < m_selects.GetSize(); ++i)
					{
						CShadeListContainerElement* pm = static_cast<CShadeListContainerElement*>(GetItemAt(*(int*)m_selects[i]));
						if (pm == NULL) continue;
						ItemInfo tmp;
						tmp.fileName = pm->m_uNodeData.basic.name;
						tmp.fileIcon = SkinConfMgr::getInstance()->getIconPath(pm->m_uNodeData.basic.type, pm->m_uNodeData.basic.name);
						info.push_back(tmp);
					}
					m_pDragDialog = new DragDialog;
					m_pDragDialog->Init(L"DragDialog.xml",m_pManager->GetPaintWindow(),event.ptMouse,info);
					m_pDragDialog->SetTransparent(50);
					m_pDragDialog->SetWindowShow(false);
					m_pDragDialog->Add(info);
				}

				if(m_pDragDialog != NULL)
				{
					bool isSelect = false;
					POINT pt=event.ptMouse;
					RECT re = this->GetPos();
					if(pt.x < re.left)    pt.x = re.left;
					if(pt.x > re.right)   pt.x = re.right;
					if(pt.y < re.top)     pt.y = re.top;
					if(pt.y > re.bottom)  pt.y = re.bottom;
					CStdValArray* selects = GetSelects();
					int iSelectIndex = -1;
					CControlUI* cSelectCtl = NULL;
					for (int i = 0; i < selects->GetSize(); ++i)
					{
						if(NULL==selects->GetAt(i)) continue;
						iSelectIndex = *(int*)selects->GetAt(i);
						cSelectCtl = GetItemAt(iSelectIndex);
						if (NULL == cSelectCtl) continue;
						CShadeListContainerElement* pm = static_cast<CShadeListContainerElement*>(cSelectCtl);
						RECT rc = pm->GetPos();
						if(PtInRect(&rc, event.ptMouse))
						{
							isSelect = true;
							break;
						}
					}
					m_pDragDialog->SetDialogPos(pt);
					if (selects->GetSize() != 0) 
					{
						if (!isSelect)
						{
							m_pDragDialog->SetWindowShow(true);
						}
						else
						{
							m_pDragDialog->SetWindowShow(false);
						}
					}				
				}
			}
			else if( MOUSE_MOVE_TYPE_SELECT == m_iMouseMoveType )
			{
				if ( IsEncloseSelect() )
				{
					ShowSelectWnd(m_pButtonDownPoint, event.ptMouse, true);

					POINT pt = m_pButtonDownPoint;
					pt.y = m_pButtonDownPoint.y - (this->GetScrollPos().cy - m_pScrollDownPoint.cy);

					SelectRange( pt, event.ptMouse );
				}
			}
		}
		if ( event.Type == UIEVENT_MOUSEENTER )
		{
			if( MOUSE_MOVE_TYPE_SELECT == m_iMouseMoveType && IsEncloseSelect()  )
			{
				ShowSelectWnd(m_pButtonDownPoint, event.ptMouse, true);
				POINT pt = m_pButtonDownPoint;
				pt.y = m_pButtonDownPoint.y - (this->GetScrollPos().cy - m_pScrollDownPoint.cy);
				SelectRange( pt, event.ptMouse );
			}
		}
		if( event.Type == UIEVENT_BUTTONUP ) 
		{

			if(event.wParam == Event_ChildSend)
			{
				clearOtherSelected();
				return;
			}
			if ( MOUSE_MOVE_TYPE_DRAG == m_iMouseMoveType )
			{
				if(NULL!=m_pDragDialog)
				{
					m_pDragDialog->SetWindowShow(false);
					m_pDragDialog->Close();
					m_pDragDialog = NULL;
					BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
					if(bCtrl)
					{
						m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGCOPY);
					}
					else if(-1!=m_iCurEnter)
					{					
						m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGMOVE);
					}				
				}
			}
			else if ( MOUSE_MOVE_TYPE_SELECT == m_iMouseMoveType)
			{
				if (m_wSelectWnd && IsEncloseSelect() )
				{
					ShowSelectWnd(m_pButtonDownPoint, event.ptMouse, false);
					SetMouseMoveType(MOUSE_MOVE_TYPE_INVALID);
				}
			}
			return;
		}

		switch( event.Type ) {
		case UIEVENT_KEYDOWN:
			switch( event.chKey ) {
			case VK_UP:
				SelectItem(FindSelectable(m_iCurSel - 1, false), true);
				return;
			case VK_DOWN:
				SelectItem(FindSelectable(m_iCurSel + 1, true), true);
				return;
			case VK_PRIOR:
				PageUp();
				return;
			case VK_NEXT:
				PageDown();
				return;
			case VK_HOME:
				SelectItem(FindSelectable(0, false), true);
				return;
			case VK_END:
				SelectItem(FindSelectable(GetCount() - 1, true), true);
				return;
			case VK_RETURN:
				if(-1 == m_iCurSel) return;
				if(NULL == GetItemAt(m_iCurSel)) return;
				(void)GetItemAt(m_iCurSel)->Activate();
				return;
			case VK_DELETE:
				m_pManager->SendNotify(this, DUI_MSGTYPE_DELETE);
				break;
			case 0x41:
				{
					BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
					if (bCtrl)
					{
						m_pManager->SendNotify(this, DUI_MSGTYPE_LISTSELECTALL);
					}
				}
				break;
			case VK_BACK:
				m_pManager->SendNotify(this, DUI_MSGTYPE_BACK);
				break;
			}
			break;
		case  UIEVENT_TIMER: 
			{
				if (event.wParam == 0)
				{
					if (m_iDelayLeft > 0)
					{
						--m_iDelayLeft;
						SIZE sz = GetScrollPos();
						LONG lDeltaY =  (LONG)(CalculateDelay((double)m_iDelayLeft / m_iDelayNumber) * m_iDelayDeltaY);
						if ((lDeltaY > 0 && sz.cy != 0)  || (lDeltaY < 0 && sz.cy != GetScrollRange().cy ))
						{
							sz.cy -= lDeltaY;
							SetScrollPos(sz);
							return;
						}
					}
					m_iDelayDeltaY = 0;
					m_iDelayNumber = 0;
					m_iDelayLeft = 0;
					m_pManager->KillTimer(this, UI_TIMERID::SCROLL_TIMERID);
					return;
				}
			}			
			break;
		case  UIEVENT_SCROLLWHEEL:
			{
				LONG lDeltaY = 0;
				if (m_iDelayNumber > 0)
					lDeltaY =  (LONG)(CalculateDelay((double)m_iDelayLeft / m_iDelayNumber) * m_iDelayDeltaY);
				switch (LOWORD(event.wParam))
				{
				case SB_LINEUP:
					if (m_iDelayDeltaY >= 0)
						m_iDelayDeltaY = lDeltaY + 8;
					else
						m_iDelayDeltaY = lDeltaY + 12;
					break;
				case SB_LINEDOWN:
					if (m_iDelayDeltaY <= 0)
						m_iDelayDeltaY = lDeltaY - 8;
					else
						m_iDelayDeltaY = lDeltaY - 12;
					break;
				}
				if (m_iDelayDeltaY > 100) 
					m_iDelayDeltaY = 100;
				else if
					(m_iDelayDeltaY < -100) m_iDelayDeltaY = -100;

				m_iDelayNumber = (DWORD)sqrt((double)abs(m_iDelayDeltaY)) * 5;
				m_iDelayLeft = m_iDelayNumber;
				m_pManager->SetTimer(this, UI_TIMERID::SCROLL_TIMERID, 50U);
				return;
			}
			break;
		}

		CTileLayoutUI::DoEvent(event);
	}

	void CTileLayoutListUI::SelectAllItem(bool bCheck)
	{
		//if (bCheck) clearOtherSelected();
		m_selects.Empty();
		for (int i = 0; i < this->GetCount(); i++)
		{
			if (bCheck) m_selects.Add(&i);
			CControlUI* pControl = GetItemAt(i);
			if( NULL == pControl)return;
			IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem != NULL ) pListItem->Select(bCheck,false);
		}
		m_iCurSel = (m_selects.GetSize()==1)?(*(int*)m_selects[0]):-1;
		selectChanged();
	}

	CStdValArray* CTileLayoutListUI::GetSelects()
	{
		return &m_selects;
	}

	void CTileLayoutListUI::clearOtherSelected()
	{
		int index = -1;
		for (int i = 0; i < m_selects.GetSize(); ++i)
		{
			index = *(int*)m_selects[i];
			if(index==m_iCurSel)
			{
				continue;
			}
			CControlUI* pControl = GetItemAt(index);
			if( pControl == NULL) continue;
			IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem == NULL ) continue;
			pListItem->Select(false,false);
		}
		m_selects.Empty();
		m_selects.Add(&m_iCurSel);
		m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTALL, false);
		selectChanged();
	}

	void  CTileLayoutListUI::OnDragEnter( IDataObject *pDataObj, DWORD grfKeyState, POINT pt,  DWORD *pdwEffect)
	{
		if (m_pDragDialog == NULL)
		{
			m_dropFiles.clear();
			HGLOBAL hgloba = NULL;
			HDROP hDrop = NULL;
			//WORD wNumFilesDropped =0;  
			WORD wPathnameSize = 0;  
			WCHAR *pFilePathName = NULL;  
			IEnumFORMATETC *etc;
			FORMATETC etc2;
			STGMEDIUM stg;
			pDataObj->EnumFormatEtc(DATADIR_GET,&etc);
			ULONG count;
			int res = etc->Reset();
			while (res == S_OK)
			{
				res = etc->Next(1,&etc2,&count);
				if(res == S_OK)
				{
					if (etc2.cfFormat == CF_HDROP)
					{
						pDataObj->GetData(&etc2,&stg);
						hgloba = stg.hGlobal;
					}
				}
			}	

			if (hgloba != NULL)
			{
				hDrop = (HDROP)hgloba;
				(void)DragQueryFile(hDrop, -1, NULL, 0);  

				/*for (WORD i=0;i<wNumFilesDropped;i++)
				{  
					wPathnameSize = DragQueryFile(hDrop, i, NULL, 0);  
					wPathnameSize++;  
					pFilePathName = new WCHAR[wPathnameSize];  
					if (NULL == pFilePathName)  
					{  
						_ASSERT(0);  
						DragFinish(hDrop);  
						return ;  
					}  

					::ZeroMemory(pFilePathName, wPathnameSize);  
					DragQueryFile(hDrop, i, pFilePathName, wPathnameSize);
					m_dropFiles.push_back(pFilePathName);
					delete[] pFilePathName;
					pFilePathName=NULL;
				}  		*/
				DragFinish(hDrop);
			}
			//if (m_dropFiles.empty())   return;

			std::vector<ItemInfo> info;
			ItemInfo tmp;
			info.push_back(tmp);
			/*for(std::list<std::wstring>::iterator it = m_dropFiles.begin(); it != m_dropFiles.end(); ++it)
			{							
			ItemInfo tmp;
			tmp.fileName = *it;
			tmp.fileIcon = SkinConfMgr::getInstance()->getIconPath(FILE_TYPE_DIR, *it);
			info.push_back(tmp);
			break;
			}*/

			m_pDragDialog = new DragDialog;
			m_pDragDialog->Init(L"DragDialog.xml",m_pManager->GetPaintWindow(),pt,info);
			m_pDragDialog->SetTransparent(50);
			m_pDragDialog->Add(info);
			m_pDragDialog->SetWindowShow(false);
		}

		::SendMessage(m_pManager->GetPaintWindow(),WM_MOUSEMOVE,(WPARAM)-1,(LPARAM)MAKELPARAM(pt.x,pt.y));
		CControlUI::OnDragEnter(pDataObj,grfKeyState,pt,pdwEffect);
	}

	void CTileLayoutListUI::OnDragOver(DWORD grfKeyState, POINT pt,DWORD *pdwEffect)
	{
		::SendMessage(m_pManager->GetPaintWindow(),WM_MOUSEMOVE,(WPARAM)-1,(LPARAM)MAKELPARAM(pt.x,pt.y));
		CControlUI::OnDragOver(grfKeyState,pt,pdwEffect);
	}

	void CTileLayoutListUI::OnDragLeave()
	{
		if (m_pDragDialog != NULL)
		{
			m_pDragDialog->Close();
			m_pDragDialog = NULL;
		}
		CControlUI::OnDragLeave();
	}

	void CTileLayoutListUI::OnDrop(IDataObject *pDataObj, DWORD grfKeyState, POINT pt, DWORD *pdwEffect)
	{
		if (m_pDragDialog != NULL)
		{
			m_pDragDialog->Close();
			m_pDragDialog = NULL;
		}

		m_dropFiles.clear();
		HGLOBAL hgloba = NULL;
		HDROP hDrop = NULL;
		WORD wNumFilesDropped =0;  
		WORD wPathnameSize = 0;  
		WCHAR *pFilePathName = NULL;  
		IEnumFORMATETC *etc;
		FORMATETC etc2;
		STGMEDIUM stg;
		pDataObj->EnumFormatEtc(DATADIR_GET,&etc);
		ULONG count;
		int res = etc->Reset();
		while (res == S_OK)
		{
			res = etc->Next(1,&etc2,&count);
			if(res == S_OK)
			{
				if (etc2.cfFormat == CF_HDROP)
				{
					pDataObj->GetData(&etc2,&stg);
					hgloba = stg.hGlobal;
				}
			}
		}	

		if (hgloba != NULL)
		{
			hDrop = (HDROP)hgloba;
			wNumFilesDropped = DragQueryFile(hDrop, -1, NULL, 0);  

			for (WORD i=0;i<wNumFilesDropped;i++)
			{  
				wPathnameSize = DragQueryFile(hDrop, i, NULL, 0);  
				wPathnameSize++;  
				pFilePathName = new WCHAR[wPathnameSize];  
				if (NULL == pFilePathName)  
				{  
					_ASSERT(0);  
					DragFinish(hDrop);  
					return ;  
				}  

				::ZeroMemory(pFilePathName, wPathnameSize);  
				DragQueryFile(hDrop, i, pFilePathName, wPathnameSize);
				m_dropFiles.push_back(pFilePathName);
				delete[] pFilePathName;
				pFilePathName=NULL;
			}  		
			DragFinish(hDrop);
		}

		if (m_dropFiles.empty())   return;
		
		m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGFILE);
	}

	std::list<std::wstring> CTileLayoutListUI::getDropFile()
	{
		return m_dropFiles;
	}

	void CTileLayoutListUI::clearDropFile()
	{
		m_dropFiles.clear();
	}

	DragDialog* CTileLayoutListUI::getDragDialog()
	{
		return m_pDragDialog;
	}

	void CTileLayoutListUI::clearDialog()
	{
		if (m_pDragDialog == NULL)  return;
		m_pDragDialog->Close();
		m_pDragDialog = NULL;
	}
	
	void CTileLayoutListUI::setDragable(bool dragable)
	{
		isDragable_ = dragable;
	}

	void CTileLayoutListUI::SetMouseMoveType(int type)
	{
		m_iMouseMoveType = type;
	}

	int CTileLayoutListUI::GetMouseMoveType( )
	{
		return m_iMouseMoveType;
	}

	void CTileLayoutListUI::SetButtonDownPoint( POINT pt )
	{
		m_pButtonDownPoint = pt;
		m_pScrollDownPoint = this->GetScrollPos();
	}

	void CTileLayoutListUI::SetButtonUpPoint( POINT pt )
	{
		m_pButtonUpPoint = pt;
	}

	POINT CTileLayoutListUI::GetButtonDownPoint()
	{
		return m_pButtonDownPoint;
	}

	POINT CTileLayoutListUI::GetButtonUpPoint()
	{
		return m_pButtonUpPoint;
	}

	void CTileLayoutListUI::ShowSelectWnd( POINT posstart, POINT posend, bool isShow)
	{
		m_pButtonMovePoint = posend;
		POINT posendtemp = posend;
		if (m_wSelectWnd)
		{
			if (isShow)
			{
				int iVScrollWidth = 0;
				int iHScrollHeight = 0;

				if ( this->GetVerticalScrollBar() )
				{
					iVScrollWidth = this->GetVerticalScrollBar()->GetWidth();
				}

				if (this->GetHorizontalScrollBar())
				{
					iHScrollHeight = this->GetHorizontalScrollBar()->GetHeight();
				}

				posstart.y = posstart.y - (GetScrollPos().cy - m_pScrollDownPoint.cy);
				if (posstart.y > this->GetPos().bottom - this->GetInset().bottom )
				{
					posstart.y = this->GetPos().bottom - this->GetInset().bottom;
				}
				else if( posstart.y < this->GetPos().top + this->GetInset().top )
				{
					posstart.y = this->GetPos().top + this->GetInset().top;
				}

				if ( posend.x > this->GetPos().right - this->GetInset().right - iVScrollWidth )
				{
					posend.x = this->GetPos().right - this->GetInset().right - iVScrollWidth;
				}
				else if( posend.x < this->GetPos().left + this->GetInset().left )
				{
					posend.x = this->GetPos().left + this->GetInset().left;
				}

				if (posend.y > this->GetPos().bottom - this->GetInset().bottom - iHScrollHeight )
				{
					posend.y = this->GetPos().bottom - iHScrollHeight;
				}
				else if( posend.y < this->GetPos().top + this->GetInset().top )
				{
					posend.y = this->GetPos().top + this->GetInset().top;
				}

				int height = 0;
				int width = 0;
				POINT ptwindowpos;
				ptwindowpos.x = -1;
				ptwindowpos.y = -1;

				if ( posend.x > posstart.x )
				{
					if ( posend.y > posstart.y )
					{
						ptwindowpos = posstart;
					}
					else
					{
						ptwindowpos.x = posstart.x;
						ptwindowpos.y = posend.y;
					}
				}
				else
				{
					if (posend.y > posstart.y)
					{
						ptwindowpos.x = posend.x;
						ptwindowpos.y = posstart.y;
					}
					else
					{
						ptwindowpos.x = posend.x;
						ptwindowpos.y = posend.y;
					}
				}

				width = abs(posend.x - posstart.x);
				height = abs(posend.y - posstart.y);


				ClientToScreen(this->GetManager()->GetPaintWindow(), &ptwindowpos);

				::MoveWindow(m_wSelectWnd->GetHWND(),ptwindowpos.x,ptwindowpos.y,width,height,TRUE);
				m_pPaintManager->SetTransparent(50);
				m_wSelectWnd->ShowWindow(isShow);
				m_pPaintManager->SetBackgroundTransparent(true);

				if (posendtemp.y > this->GetPos().bottom )
				{
					if (GetScrollPos().cy != GetScrollRange().cy )
					{
						this->LineDown();
					}
				}
				else if( posendtemp.y < this->GetPos().top)
				{
					if (GetScrollPos().cy != 0 )
					{
						this->LineUp();
					}
				}
			}
			else
			{
				m_wSelectWnd->ShowWindow(isShow);
			}
		}
	}

	void CTileLayoutListUI::SetSelectWnd( CWindowWnd* window, HWND parenthwnd, CPaintManagerUI* paintmanager )
	{
		m_wSelectWnd = window;
		m_hParentHwnd = parenthwnd;
		m_pPaintManager = paintmanager;
	}

	void CTileLayoutListUI::setCurEnter( int curEnter )
	{
		m_iCurEnter = curEnter;
	}

	int CTileLayoutListUI::getCurEnter()
	{
		return m_iCurEnter;
	}

	bool CTileLayoutListUI::IsEncloseSelect()
	{
		return m_bEncloseSelect;
	}

	void CTileLayoutListUI::SetEncloseSelect( bool bEncloseSelect )
	{
		m_bEncloseSelect = bEncloseSelect;
	}

	void CTileLayoutListUI::SetAttribute( LPCTSTR pstrName, LPCTSTR pstrValue )
	{
		if ( _tcscmp(pstrName, _T("encloseselect")) == 0 )SetEncloseSelect(_tcscmp(pstrValue, _T("true")) == 0);
		else CTileLayoutUI::SetAttribute(pstrName, pstrValue);
	}

	SIZE CTileLayoutListUI::GetScrollDownPoint()
	{
		return m_pScrollDownPoint;
	}

}