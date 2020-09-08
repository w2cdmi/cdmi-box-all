#include "stdafxOnebox.h"
#include "ItemCheckBox.h"
#include "ListContainerElement.h"

namespace Onebox
{
	CItemCheckBoxUI::CItemCheckBoxUI():m_iIndex(-1),m_pItem(NULL),m_pOwner(NULL)
	{
	}

	void CItemCheckBoxUI::initData()
	{
		CControlUI* pParent = this->GetParent();
		if(pParent==NULL) return;
		m_pItem = static_cast<CListContainerElementUI*>(pParent->GetInterface(DUI_CTR_LISTCONTAINERELEMENT));
		if(m_pItem==NULL) return;
		m_iIndex = m_pItem->GetIndex();
		m_pOwner = m_pItem->GetOwner();
	}

	void CItemCheckBoxUI::DoEvent(TEventUI& event)
	{
		if( event.Type == UIEVENT_BUTTONUP )
		{
			if( (m_uButtonState & UISTATE_CAPTURED) != 0 ) {
				if( ::PtInRect(&m_rcItem, event.ptMouse) ) Activate();
				m_uButtonState &= ~(UISTATE_PUSHED | UISTATE_CAPTURED);
				Invalidate();
			}
			if(-1==m_iIndex) initData();
			if(m_pOwner==NULL) return;
			m_pOwner->SelectItem(m_iIndex, true);
			return;
		}
		if( event.Type == UIEVENT_MOUSEENTER )
		{
			if( IsEnabled() ) {
				m_uButtonState |= UISTATE_HOT;
				Invalidate();
			}
			if(Event_Default==event.wParam)
			{
				if(-1==m_iIndex) initData();
				if(m_pItem==NULL) return;
				event.wParam = Event_FromChild;
				m_pItem->DoEvent(event);
				event.wParam = Event_Default;
			}
			return;
		}
		if( event.Type == UIEVENT_MOUSELEAVE )
		{
			if( IsEnabled() ) {
				m_uButtonState &= ~UISTATE_HOT;
				Invalidate();
			}
			if(Event_Default==event.wParam)
			{
				if(-1==m_iIndex) initData();
				if(m_pItem==NULL) return;
				event.wParam = Event_FromChild;
				m_pItem->DoEvent(event);
				event.wParam = Event_Default;
			}
			return;
		}
		CButtonUI::DoEvent(event);
	}

	CSelectallCheckBoxUI::CSelectallCheckBoxUI():m_bInit(false),m_pOwner(NULL)
	{
		this->SetName(_T("selectall"));
	}

	void CSelectallCheckBoxUI::initData()
	{
		m_bInit = true;
		CControlUI* pHeaderItem = this->GetParent();
		if(pHeaderItem==NULL) return;
		CControlUI* pHeader = pHeaderItem->GetParent();
		if(pHeader==NULL) return;
		CControlUI* pOwner = pHeader->GetParent();
		m_pOwner = static_cast<CCustomListUI*>(pOwner);
	}

	void CSelectallCheckBoxUI::DoEvent(TEventUI& event)
	{
		if( event.Type == UIEVENT_BUTTONUP )
		{
			if( (m_uButtonState & UISTATE_CAPTURED) != 0 ) {
				if( ::PtInRect(&m_rcItem, event.ptMouse) ) Activate();
				m_uButtonState &= ~(UISTATE_PUSHED | UISTATE_CAPTURED);
				Invalidate();
			}
			if(!m_bInit) initData();
			if(m_pOwner==NULL) return;
			m_pOwner->SelectAllItem(m_bSelected);
			m_pManager->SendNotify(m_pOwner, DUI_MSGTYPE_SELECTALL, m_bSelected);
			return;
		}
		CButtonUI::DoEvent(event);
	}
}