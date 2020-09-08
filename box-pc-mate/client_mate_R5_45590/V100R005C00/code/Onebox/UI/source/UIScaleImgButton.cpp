#include "stdafxOnebox.h"
#include "UIScaleImgButton.h"


CScaleImgButtonUI::CScaleImgButtonUI(void)
{
	m_pTextButton = new CScaleButtonUI();
	m_pImgButton = new CImgButtonUI();		

	if ( NULL != m_pTextButton && NULL != m_pImgButton){
		this->Add(m_pTextButton);
		this->Add(m_pImgButton);
		m_pTextButton->SetLayout(this);
		SetFixedWidth(100);
		m_pImgButton->SetMenuButton(m_pTextButton);
	}
	
	m_iAddWidth = 0;
}

CScaleImgButtonUI::~CScaleImgButtonUI(void)
{
		this->Remove(m_pTextButton);
		this->Remove(m_pImgButton);
}

void CScaleImgButtonUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
{
	if( _tcscmp(pstrName, _T("name")) == 0 ) {
		if (m_pImgButton)
			m_pImgButton->SetName(pstrValue);
		if (m_pTextButton)
			m_pTextButton->SetName(pstrValue);
		SetName(pstrValue);
	}
	else if( _tcscmp(pstrName, _T("btnnormalimage")) == 0 ) {
		if (m_pImgButton)
			m_pImgButton->SetNormalImage(pstrValue);
	}
	else if( _tcscmp(pstrName, _T("btnhotimage")) == 0 ) {
		if (m_pImgButton)
			m_pImgButton->SetHotImage(pstrValue);
	}
	else if( _tcscmp(pstrName, _T("addwidth")) == 0 ) {
		m_iAddWidth = _ttoi(pstrValue);
		if (m_pTextButton)
			m_pTextButton->SetAddWidth(m_iAddWidth);
		if (m_pImgButton)
			m_pImgButton->SetFixedWidth(m_iAddWidth);
	}
	else {
		if (m_pTextButton)
			m_pTextButton->SetAttribute(pstrName, pstrValue);
//  		if (m_pImgButton)
//  			m_pImgButton->SetAttribute(pstrName, pstrValue);
	}

}

void CScaleImgButtonUI::SetText(LPCTSTR pstrText)
{
	if (m_pTextButton)
		m_pTextButton->SetText(pstrText);
}

void CScaleImgButtonUI::SetToolTip(LPCTSTR pstrText)
{
	if (m_pTextButton){
		m_pTextButton->SetToolTip(pstrText);
	}
	if (m_pImgButton)
		m_pImgButton->SetToolTip(pstrText);
}

void CImgButtonUI::DoEvent(TEventUI& event)
{
	if( event.Type == UIEVENT_BUTTONDOWN )
	{
 		if( ::PtInRect(&m_rcItem, event.ptMouse) && IsEnabled() ) {
 			m_uButtonState |= UISTATE_PUSHED | UISTATE_CAPTURED;
 			Invalidate();
			if (m_pMenuBtn)
			{
				event.pSender = m_pMenuBtn;
				event.Type = UIEVENT_CONTEXTMENU;
				event.ptMouse.x -= m_rcItem.right-m_rcItem.left;
				event.dwTimestamp = ::GetTickCount();
				m_pMenuBtn->SetContextMenuUsed(true);
				m_pMenuBtn->Event(event);
			}
			return;
 		}
		
	}
	else if (event.Type == UIEVENT_CONTEXTMENU)
		return;

	CButtonUI::DoEvent(event);
}



