#include "stdafxOnebox.h"
#include "Common.h"
#include "ListContainerElement.h"
#include "TileLayoutListUI.h"
#include "InILanguage.h"
#include "NotifyMgr.h"
#include "CustomListUI.h"

namespace Onebox
{
	BaseContainerElement::BaseContainerElement()
	{
		m_pButtonDownPoint.x = -1;
		m_pButtonDownPoint.y = -1;

		m_bButtonDown = false;

		m_iRenameReady = 0;
		m_bReadOnly = true;
	}

	void BaseContainerElement::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			else CContainerUI::DoEvent(event);
			return;
		}

		if(event.Type == UIEVENT_BUTTONUP) 
		{
			MouseUp(event);
			return;
		}

		if( event.Type == UIEVENT_DBLCLICK )
		{
			MouseDBClick(event);
			return;
		}
		if( event.Type == UIEVENT_KEYDOWN && IsEnabled() )
		{
			if (event.chKey == 0x41)
			{
				BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
				if (bCtrl)
				{
					m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTALL);
				}
			}
			else if (event.chKey == VK_BACK)
			{
				m_pManager->SendNotify(this, DUI_MSGTYPE_BACK);
			}
			else 
			{
				KeyDown(event);
				return;
			}
		}
		if ( event.Type == UIEVENT_MOUSEENTER )
		{
			MouseEnter(event);
			//return;
		}
		if( event.Type == UIEVENT_BUTTONDOWN )
		{
			MouseDown(event);
			return;
		}
		if( event.Type == UIEVENT_RBUTTONDOWN )
		{
			MouseRDown(event);
			return;
		}
		if( event.Type == UIEVENT_RBUTTONUP ) 
		{
			MouseRUp(event);
			return;
		}
		if( event.Type == UIEVENT_MOUSEMOVE )
		{
			MouseMove(event);
			return;
		}
		if ( event.Type == UIEVENT_MOUSELEAVE )
		{
			MouseLeave(event);
			return;
		}
		CListContainerElementUI::DoEvent(event);
	}

	bool BaseContainerElement::Select(bool bSelect,bool bCallback)
	{
		BOOL bShift = (GetKeyState(VK_SHIFT) & 0x8000);
		BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000) && (!(GetKeyState(0x41) & 0x8000));
		if( !IsEnabled() ) return false;		

		m_bSelected = (bCtrl&&!bShift)?(!m_bSelected):bSelect;
		if (m_bSelected) {
			m_uButtonState |= UISTATE_HOT;
		}
		else {
			if( (m_uButtonState & UISTATE_HOT) != 0 ) m_uButtonState &= ~UISTATE_HOT;
		}
		if(bCallback && m_pOwner != NULL ) {
			if(bShift){
				m_pOwner->SelectRange(m_iIndex);
			}else{
				m_pOwner->SelectItem(m_iIndex);
			}
		}
		CControlUI* control = this->GetItemAt(0);
		if(NULL == control) return false;
		COptionUI* copui = (COptionUI*) (control->GetInterface(DUI_CTR_OPTION));
		if(NULL != copui) copui->Selected(m_bSelected);
		Invalidate();
		return true;
	}

	bool BaseContainerElement::SelectRange(int index, bool bCallBack)
	{
		if( !IsEnabled() ) return false;	

		if(m_pOwner != NULL ) {
			m_pOwner->SelectRange(index, bCallBack);
		}
		CControlUI* control = this->GetItemAt(0);
		if(NULL == control) return false;
		COptionUI* copui = (COptionUI*) (control->GetInterface(DUI_CTR_OPTION));
		if(NULL != copui) copui->Selected(m_bSelected);
		Invalidate();
		return true;
	}

	bool BaseContainerElement::IsExpanded() const
	{
		return false;
	}

	void BaseContainerElement::SetPos(RECT rc)
	{
		__super::SetPos(rc);
		if( m_pOwner == NULL ) return;
		TListInfoUI* pInfo = m_pOwner->GetListInfo();
		if(NULL == pInfo) return;
		int iChangeIndex=0;
		LONG cxLeft = 0;
		LONG cxRight = 0;
		for( int i = 0; i < pInfo->nColumns; i++ )
		{
			CControlUI* pControl = GetItemAt(i);
			if(!pControl) break;
			RECT rcOldItem = pControl->GetPos();
			if(pInfo->rcColumn[i].right-rcOldItem.right!=0){
				iChangeIndex =i;
				cxLeft=pInfo->rcColumn[i].left-rcOldItem.left;
				cxRight=pInfo->rcColumn[i].right-rcOldItem.right;
				break;
			}
		}
		RECT rcNew = {rc.left,rc.top,rc.right+cxRight,rc.bottom};
		if(cxRight!=0 &&  cxLeft==0){//列表整体在向左或右位移
			CControlUI::SetPos(rcNew);
		}else{
			CControlUI::SetPos(rc);
		}
		if( m_items.IsEmpty() ) return;
		rcNew.left += m_rcInset.left;
		rcNew.top += m_rcInset.top;
		rcNew.right -= m_rcInset.right;
		rcNew.bottom -= m_rcInset.bottom;

		for( int it = 0; it < m_items.GetSize(); it++ ) {
			CControlUI* pControl = static_cast<CControlUI*>(m_items[it]);
			if( !pControl->IsVisible() ) continue;
			if( pControl->IsFloat() ) {
				if(cxRight!=0 &&  cxLeft==0){//列表表头被拉伸
					if(it>=iChangeIndex){
						RECT rcItem = { pInfo->rcColumn[it].left, m_rcItem.top, pInfo->rcColumn[it].right, m_rcItem.bottom };
						pControl->SetPos(rcItem);
					}

				}else{
					SetFloatPos(it);
				}
			}
			else {
				pControl->SetPos(rcNew); // 所有非float子控件放大到整个客户区
			}
		}
	}

	void BaseContainerElement::MouseEnter(TEventUI& event)
	{
		if( IsEnabled() )
		{
			m_uButtonState |= UISTATE_HOT;
			m_pManager->SendNotify(this, DUI_MSGTYPE_MOUSEENTER);
		}

		if(Event_Default==event.wParam)
		{
			CControlUI* control = this->GetItemAt(0);
			if(NULL == control) return;
			CButtonUI* copui = (CButtonUI*) (control->GetInterface(DUI_CTR_BUTTON));
			event.wParam = Event_FromParent;
			if(NULL != copui) copui->DoEvent(event);
			event.wParam = Event_Default;
		}

		SetParentCurEnter(this->GetIndex());

		if ( MOUSE_MOVE_TYPE_SELECT == GetParentMouseMoveType() )
		{
			if ( (this->GetIndex() == m_pOwner->GetCurSel())
				|| (-1 == m_pOwner->GetCurSel()) )
			{
				Select();
			}
			else
			{
				SelectRange(this->GetIndex());
			}
			Invalidate();
		}
		else
		{
			PVOID owner = this->GetParent()->GetInterface(L"CTileLayoutListUI");
			if(!owner) return;
			CTileLayoutListUI* pOwner = static_cast<CTileLayoutListUI*>(m_pOwner);

			DragDialog* dragDialog = pOwner->getDragDialog();
			if(NULL==dragDialog) return;

			if(this->m_uNodeData.basic.type == FILE_TYPE_FILE||m_bSelected) return;
			BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
			if(bCtrl)
			{
				dragDialog->SetMovedName(iniLanguageHelper.GetCommonString(COMMENT_COPYTO_KEY).c_str() + this->m_uNodeData.basic.name);
			}
			else
			{
				dragDialog->SetMovedName(iniLanguageHelper.GetCommonString(COMMENT_MOVETO_KEY)+ this->m_uNodeData.basic.name);
			}
		}

		return;
	}

	void BaseContainerElement::MouseDown(TEventUI& event)
	{
		if( !IsEnabled() ) return;

		BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
		BOOL bShift = (GetKeyState(VK_SHIFT) & 0x8000);

		m_bButtonDown = !(bCtrl || bShift);
		m_pButtonDownPoint = event.ptMouse;					
		SetParentMouseDownPoint(m_pButtonDownPoint);

		int type = GetParentMouseMoveType();
		if ( MOUSE_MOVE_TYPE_INVALID == type || MOUSE_MOVE_TYPE_DRAG == type )
		{
			m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMCLICK);

			Invalidate();		
		}
	}
	void BaseContainerElement::MouseUp(TEventUI& event)
	{
		m_bButtonDown = false;
		if( !IsEnabled() ) return;
		
		if( MOUSE_MOVE_TYPE_INVALID == GetParentMouseMoveType())
		{
			if( (!m_bReadOnly) && (this->m_bSelected) 
				&& (this->GetIndex() == m_pOwner->GetCurSel())
				&& (1 == GetParentSelectSize()) )
			{
				renameItem(event.ptMouse);
			}

			Select();

			CControlUI* control = this->GetItemAt(0);
			if(NULL == control) return;
			CButtonUI* copui = (CButtonUI*) (control->GetInterface(DUI_CTR_BUTTON));
			if(NULL != copui)
			{
				RECT rc = copui->GetPos();
				if(PtInRect(&rc, event.ptMouse))
				{
					copui->DoEvent(event);
					return;
				}
			}
		}
		else if( MOUSE_MOVE_TYPE_DRAG == GetParentMouseMoveType() )
		{
			BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
			if(!bCtrl 
				&& m_bButtonDown 
				&& (m_pButtonDownPoint.x == event.ptMouse.x)
				&& (m_pButtonDownPoint.y == event.ptMouse.y) )
			{
				event.wParam = Event_ChildSend;
			}
			m_bButtonDown = false;
			m_pButtonDownPoint.x = -1;
			m_pButtonDownPoint.y = -1;
			SetParentMouseUpPoint(event.ptMouse);
			if(m_pOwner != NULL) m_pOwner->DoEvent(event);
			event.wParam = Event_Default;
			SetParentMouseMoveType(MOUSE_MOVE_TYPE_INVALID);
		}
		else if( MOUSE_MOVE_TYPE_SELECT == GetParentMouseMoveType() )
		{
			if(m_pOwner != NULL) m_pOwner->DoEvent(event);
			event.wParam = Event_Default;
			SetParentMouseMoveType(MOUSE_MOVE_TYPE_INVALID);
		}
	}
	void BaseContainerElement::MouseRDown(TEventUI& event)
	{
		if( IsEnabled() ){
			if(!m_bSelected) Select();
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			Invalidate();
		}
	}
	void BaseContainerElement::MouseRUp(TEventUI& event)
	{
		if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
	}
	void BaseContainerElement::MouseMove(TEventUI& event)
	{
		if(m_bButtonDown 
			&& event.ptMouse.x != -1 && event.ptMouse.y != -1
			&& ( abs(m_pButtonDownPoint.x - event.ptMouse.x) > 10 || abs(m_pButtonDownPoint.y - event.ptMouse.y) > 10) ) 
		{
			if ( this->m_bSelected && MOUSE_MOVE_TYPE_SELECT != GetParentMouseMoveType() )
			{
				SetParentMouseMoveType(MOUSE_MOVE_TYPE_DRAG);
				event.wParam = Event_ChildSend;

				if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
				event.wParam = Event_Default;
			}
			else
			{
				if ( !IsEncloseSelect() ) return ;
				if ( MOUSE_MOVE_TYPE_SELECT != GetParentMouseMoveType())
				{
					Select();
					SetParentMouseMoveType(MOUSE_MOVE_TYPE_SELECT);
				}
				else if (MOUSE_MOVE_TYPE_SELECT == GetParentMouseMoveType() )
				{
					ShowSelectWnd(event);
					SelectRange(GetParentMouseDownPoint(), event.ptMouse);
				}
			}
		}
	}
	void BaseContainerElement::MouseDBClick(TEventUI& event)
	{
		if( IsEnabled() ) {
			//Activate();
			Select();
			m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDBCLICK);
			Invalidate();
		}
	}
	void BaseContainerElement::MouseLeave(TEventUI& event)
	{
		m_uButtonState &= ~UISTATE_HOT;
		m_pManager->SendNotify(this, DUI_MSGTYPE_MOUSELEAVE);
		Invalidate();

		if(Event_Default==event.wParam)
		{
			CControlUI* control = this->GetItemAt(0);
			if(NULL == control) return;
			CButtonUI* copui = (CButtonUI*) (control->GetInterface(DUI_CTR_BUTTON));
			event.wParam = Event_FromParent;
			if(NULL != copui) copui->DoEvent(event);
			event.wParam = Event_Default;
		}

		if(this->m_uNodeData.basic.type == FILE_TYPE_FILE||m_bSelected) return;
		PVOID owner = this->GetParent()->GetInterface(L"CTileLayoutListUI");
		if(!owner) return;

		CTileLayoutListUI* pOwner = static_cast<CTileLayoutListUI*>(m_pOwner);
		if(NULL==pOwner) return;
		if ( MOUSE_MOVE_TYPE_DRAG == pOwner->GetMouseMoveType() )
		{
			pOwner->setCurEnter(-1);
			DragDialog* dragDialog = pOwner->getDragDialog();
			if(NULL==dragDialog) return;
			BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
			dragDialog->SetMovedName(bCtrl? iniLanguageHelper.GetCommonString(COMMENT_COPYTOCUR_KEY) : iniLanguageHelper.GetCommonString(COMMENT_MOVETO_KEY));
		}
		else if ( MOUSE_MOVE_TYPE_SELECT == pOwner->GetMouseMoveType() )
		{
			pOwner->SelectRange( GetParentMouseDownPoint(), event.ptMouse );
		}
	}

	void BaseContainerElement::KeyDown(TEventUI& event)
	{
		if( event.chKey == VK_RETURN ) {
			Activate();
			Invalidate();
			this->SetMouseChildEnabled(false);
			this->SetFocus();
			m_pManager->SendNotify(this, DUI_MSGTYPE_RETURN);
			return;
		}
		if( event.chKey == VK_SPACE ) {
			Invalidate();
			return;
		}

		if( event.chKey  == VK_DELETE)
		{
			m_pManager->SendNotify(this, DUI_MSGTYPE_DELETE);
			return;
		}

		if( event.chKey == VK_F2 )
		{
			if ( m_bReadOnly ) return;
			int selectSize = 0;
			int imode = m_pOwner->GetMode();
			if ( UILIST_MODE_LIST == imode )
			{
				CListUI* list = static_cast<CListUI*>(m_pOwner);
				selectSize = list->GetSelects()->GetSize();
			}
			else if( UILIST_MODE_ICON == imode )
			{
				CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
				selectSize = tilelayoutlist->GetSelects()->GetSize();
			}

			if(1==selectSize)
			{
				CRichEditUI* creui = static_cast<CRichEditUI*>(this->FindSubControlsByClass(DUI_CTR_RICHEDITUI));	
				if (NULL == creui) return; 
				creui->SetMouseEnabled(true);
				this->SetMouseChildEnabled();
				creui->SetFocus();
			}
		}
	}

	void BaseContainerElement::setRenameFlag(bool bFlag)
	{
		m_bReadOnly = (!bFlag);
	}

	void BaseContainerElement::renameItem(POINT& controlPoint)
	{
		BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
		BOOL bShift = (GetKeyState(VK_SHIFT) & 0x8000);
		if (bCtrl || bShift)
		{
			return;
		}

		if (this->GetIndex() != m_pOwner->GetCurSel())
		{
			return;
		}

		CRichEditUI* creui = static_cast<CRichEditUI*>(this->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
		if (NULL == creui) return;
		RECT rc = creui->GetPos();
		if (PtInRect(&rc,controlPoint))
		{
			if(!creui->IsFocused())	
			{
				creui->SetMouseEnabled(true);
				this->SetMouseChildEnabled();
				creui->SetFocus();
			}
		}
	}

	void BaseContainerElement::SetParentMouseMoveType( int type )
	{
		if (!m_pOwner) return;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			list->SetMouseMoveType(type);
		}
		else
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			tilelayoutlist->SetMouseMoveType(type);
		}
	}

	int BaseContainerElement::GetParentMouseMoveType( )
	{
		if (!m_pOwner) return MOUSE_MOVE_TYPE_INVALID;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->GetMouseMoveType();
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->GetMouseMoveType();
		}

		return MOUSE_MOVE_TYPE_INVALID;
	}

	void BaseContainerElement::SetParentMouseDownPoint( POINT pt )
	{
		if (!m_pOwner) return;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->SetButtonDownPoint(pt);
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->SetButtonDownPoint(pt);
		}
	}


	POINT BaseContainerElement::GetParentMouseDownPoint()
	{
		if (!m_pOwner) return m_pButtonDownPoint;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->GetButtonDownPoint();
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->GetButtonDownPoint();
		}

		return m_pButtonDownPoint;
	}


	void BaseContainerElement::SetParentMouseUpPoint( POINT pt )
	{
		if (!m_pOwner) return;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			list->SetButtonUpPoint(pt);
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			tilelayoutlist->SetButtonUpPoint(pt);
		}
	}

	void BaseContainerElement::SetParentCurEnter( int cursel )
	{
		if (!m_pOwner) return;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->setCurEnter(cursel);
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->setCurEnter(cursel);
		}
	}

	int BaseContainerElement::GetParentCurEnter()
	{
		int cursel = -1;
		if (!m_pOwner) return cursel;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			cursel = list->getCurEnter();
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			cursel = tilelayoutlist->getCurEnter();
		}
		return cursel;
	}

	bool BaseContainerElement::SelectRange(POINT posstart, POINT posend)
	{
		int cursel = -1;
		if (!m_pOwner) return false;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			POINT pt = m_pButtonDownPoint;
			pt.y = pt.y - ( list->GetScrollPos().cy - list->GetScrollDownPoint().cy );
			return list->SelectRange(pt, posend);

		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			POINT pt = m_pButtonDownPoint;
			pt.y = pt.y - ( tilelayoutlist->GetScrollPos().cy - tilelayoutlist->GetScrollDownPoint().cy );
			return tilelayoutlist->SelectRange(pt, posend);
		}
		return true;
	}

	void BaseContainerElement::ShowSelectWnd( TEventUI& event )
	{
		int cursel = -1;
		if (!m_pOwner) return;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			list->ShowSelectWnd(GetParentMouseDownPoint(), event.ptMouse, true);
			
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			tilelayoutlist->ShowSelectWnd(GetParentMouseDownPoint(), event.ptMouse, true);
		}
	}

	bool BaseContainerElement::IsEncloseSelect()
	{
		int cursel = -1;
		if (!m_pOwner) return false;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->IsEncloseSelect();
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->IsEncloseSelect();
		}
		return false;
	}

	int BaseContainerElement::GetParentSelectSize()
	{
		int cursel = -1;
		if (!m_pOwner) return 0;

		int imode = m_pOwner->GetMode();
		if ( UILIST_MODE_LIST == imode )
		{
			CListUI* list = static_cast<CListUI*>(m_pOwner);
			return list->GetSelects()->GetSize();
		}
		else if( UILIST_MODE_ICON == imode )
		{
			CTileLayoutListUI* tilelayoutlist = static_cast<CTileLayoutListUI*>(m_pOwner);
			return tilelayoutlist->GetSelects()->GetSize();
		}
		return false;
	}


	CShadeListContainerElement::CShadeListContainerElement()
	{
		m_shadecolor_ = 0;
		m_shadecolor2_ = 0;
		m_singularrowcolor_ = 0;
		m_dualrowcolor_ = 0;

		m_value_ = 0;

		m_bReadOnly = false;
		m_iRenameReady = 0;
	}

	CShadeListContainerElement::~CShadeListContainerElement()
	{

	}

	void CShadeListContainerElement::SetAttribute( LPCTSTR pstrName, LPCTSTR pstrValue )
	{
		if( _tcscmp(pstrName, _T("shadecolor")) == 0 ) 
		{
			while( *pstrValue > _T('\0') && *pstrValue <= _T(' ') ) pstrValue = ::CharNext(pstrValue);
			if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
			LPTSTR pstr = NULL;
			DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);

			SetStartColor(clrColor);
		}
		else if ( _tcscmp(pstrName, _T("shadecolor2")) == 0 ) 
		{
			while( *pstrValue > _T('\0') && *pstrValue <= _T(' ') ) pstrValue = ::CharNext(pstrValue);
			if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
			LPTSTR pstr = NULL;
			DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);

			SetEndColor(clrColor);
		}
		else if ( _tcscmp(pstrName, _T("singularrowcolor")) == 0 ) 
		{
			while( *pstrValue > _T('\0') && *pstrValue <= _T(' ') ) pstrValue = ::CharNext(pstrValue);
			if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
			LPTSTR pstr = NULL;
			DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);

			SetDualColor(clrColor);
		}
		else if ( _tcscmp(pstrName, _T("dualrowcolor")) == 0 ) 
		{
			while( *pstrValue > _T('\0') && *pstrValue <= _T(' ') ) pstrValue = ::CharNext(pstrValue);
			if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
			LPTSTR pstr = NULL;
			DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);

			SetSingularColor(clrColor);
		}
		else 
		{
			CListContainerElementUI::SetAttribute(pstrName, pstrValue);
		}
	}

	void CShadeListContainerElement::DoPaint( HDC hDC, const RECT& rcPaint )
	{
		if( !::IntersectRect(&m_rcPaint, &rcPaint, &m_rcItem) ) return;

		DrawItemBk(hDC, m_rcItem);
		CContainerUI::DoPaint(hDC, rcPaint);
	}

	void CShadeListContainerElement::DrawItemBk( HDC hDC, const RECT& rcItem )
	{
		if( m_pOwner == NULL ) return;
		TListInfoUI* pInfo = m_pOwner->GetListInfo();
		if(NULL == pInfo) return;
		DWORD iBackColor = 0;

		if ( (m_iIndex % 2) == 0)
		{
			iBackColor = m_dualrowcolor_;
		}
		else
		{
			iBackColor = m_singularrowcolor_;
		}

		if( ((m_uButtonState & UISTATE_HOT) != 0) && (0 != pInfo->dwHotBkColor) ) {
			iBackColor = pInfo->dwHotBkColor;
		}
		if( IsSelected() && (0 != pInfo->dwSelectedBkColor) ) {
			iBackColor = pInfo->dwSelectedBkColor;
		}
		if( !IsEnabled() ) {
			iBackColor = pInfo->dwDisabledBkColor;
		}

		if ( iBackColor != 0 ) {
			CRenderEngine::DrawColor(hDC, m_rcItem, GetAdjustColor(iBackColor));
		}

		if (m_value_ > 0 && m_value_ <= 100 )
		{
			RECT rcprogress;
			rcprogress = rcItem;
			rcprogress.right = rcprogress.left + (rcprogress.right - rcprogress.left) * m_value_ / 100;

			if ( m_shadecolor_ != 0 && m_shadecolor2_ != 0 )
			{
				CRenderEngine::DrawGradient(hDC, rcprogress, m_shadecolor_, m_shadecolor2_, false, 100);
			}
			else if ( m_shadecolor_ != 0 )
			{
				CRenderEngine::DrawColor(hDC, rcprogress, GetAdjustColor(m_shadecolor_));
			}
		}

		if ( pInfo->dwLineColor != 0 ) {
			RECT rcLine = { m_rcItem.left, m_rcItem.bottom - 1, m_rcItem.right, m_rcItem.bottom - 1 };
			CRenderEngine::DrawLine(hDC, rcLine, 1, GetAdjustColor(pInfo->dwLineColor));
		}
	}

	void CShadeListContainerElement::SetStartColor( DWORD shadecolor )
	{
		m_shadecolor_ = shadecolor;
	}

	void CShadeListContainerElement::SetEndColor( DWORD shadecolor2 )
	{
		m_shadecolor2_ = shadecolor2;
	}

	void CShadeListContainerElement::SetDualColor(DWORD evencolor)
	{
		m_dualrowcolor_ = evencolor;
	}
	void CShadeListContainerElement::SetSingularColor(DWORD oddnumbercolor)
	{
		m_singularrowcolor_ = oddnumbercolor;
	}

	void CShadeListContainerElement::SetValue( UINT8 value )
	{
		m_value_ = value;

		Invalidate();
	}

	UploadFrameTreeNode::UploadFrameTreeNode()
	{
		m_rename = false;
		m_bExpand = false;
		m_isEnableMltiSelect = true;
	}

	void UploadFrameTreeNode::SetExpanded(bool bFlag)
	{
		m_bExpand = bFlag;
	}

	bool UploadFrameTreeNode::IsExpanded() const
	{
		return m_bExpand;
	}

	void UploadFrameTreeNode::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			else CContainerUI::DoEvent(event);
			return;
		}

		if( event.Type == UIEVENT_DBLCLICK )
		{
			if( IsEnabled() ) {
				m_pManager->SendNotify(this, _T("itemdbclick"));
				if(!m_bSelected) Select();
				Invalidate();
			}
			return;
		}
		else if (event.Type == UIEVENT_BUTTONDOWN)
		{
			if (m_rename)
			{
				if (this->IsSelected())
				{
					BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
					BOOL bShift = (GetKeyState(VK_SHIFT) & 0x8000);
					if (bCtrl || bShift)
					{
						return;
					}
					CRenameRichEditUI* creui = static_cast<CRenameRichEditUI*>(this->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
					if (NULL == creui) return;
					RECT rc = creui->GetPos();
					if (PtInRect(&rc,event.ptMouse))
					{
						if(!creui->IsFocused())	
						{
							creui->SetMouseEnabled(true);
							creui->SetFocus();
						}
					}
					else
					{
						creui->SetMouseEnabled(false);
						this->m_bFocused = false;
					}
				}
				else
				{
					this->SetFocus();
				}
			}
		}
		else if (event.Type == UIEVENT_SETFOCUS)
		{
			m_bFocused = true;			
			return;
		}
		else if (event.Type == UIEVENT_KILLFOCUS)
		{ 
			m_bFocused = false;		
			return;			
		}
		if( event.Type == UIEVENT_KEYDOWN && IsEnabled() )
		{
			if( event.chKey == VK_RETURN ) {
				Activate();
 				Invalidate();
				this->SetMouseChildEnabled(false);
				this->SetFocus();
				m_pManager->SendNotify(this, DUI_MSGTYPE_RETURN);
				return;
			}
		}
		if (!m_isEnableMltiSelect)
		{
			BYTE lpKeyState[256] = {0};
			(void)::GetKeyboardState(lpKeyState);
			lpKeyState[VK_SHIFT] = 0;
			lpKeyState[VK_CONTROL] = 0;
			(void)::SetKeyboardState(lpKeyState);
		}
		CTreeNodeUI::DoEvent(event);
	}

	void UploadFrameTreeNode::setRenameFlag(bool bFlag)
	{
		m_rename = bFlag;
	}


	void UploadFrameTreeNode::RemoveAll()
	{
		CTreeNodeUI *node = GetChildNode(0);
		while (node)
		{
			RemoveAt(node);
			node = GetChildNode(0);
		}
	}

	bool UploadFrameTreeNode::IsEnableMultiSelect() const
	{
		return m_isEnableMltiSelect;
	}

	void UploadFrameTreeNode::SetEnableMultiSelect(bool bEnableMltiSelect)
	{
		m_isEnableMltiSelect = bEnableMltiSelect;
	}
}
