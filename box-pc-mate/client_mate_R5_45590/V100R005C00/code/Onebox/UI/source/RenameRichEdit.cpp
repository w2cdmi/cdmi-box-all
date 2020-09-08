#include "RenameRichEdit.h"
#include "Utility.h"

namespace Onebox
{
	HRESULT CRenameRichEditUI::TxSendMessage(UINT msg, WPARAM wparam, LPARAM lparam, LRESULT *plresult) const
	{
		if( m_pTwh ) {			
			if (msg == WM_CHAR)
			{
				wchar_t temp = static_cast<wchar_t>(wparam);
				CHorizontalLayoutUI * m_pHorLayout = static_cast<CHorizontalLayoutUI*>(this->GetParent());
				if (NULL != m_pHorLayout){
					m_pHorLayout->SetToolTip(_T(""));
					if( m_pManager->GetTooltipWindow() != NULL )
						::SendMessage(m_pManager->GetTooltipWindow(), TTM_TRACKACTIVATE, FALSE, NULL);
				}

				RECT rt = GetPos();
				long lLen = GetTextLength(GTL_NUMCHARS);
				if (lLen >= m_iLimitText)
				{
					wparam = MAKELPARAM(rt.left, rt.right);
					lparam = MAKELPARAM(rt.top, rt.bottom);
					if ( m_pManager )
						m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RICHEDIT_CHARACTER_EXCCED, wparam, lparam);
					return S_FALSE;
				}
				else if( (NULL != StrChr(m_pLimitChar, temp)) && (temp != L' ') )
				{
					wparam = MAKELPARAM(rt.left, rt.right);
					lparam = MAKELPARAM(rt.top, rt.bottom);
					if ( m_pManager )
						m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RICHEDIT_INVALID_CHARACTER, wparam, lparam);
					return S_FALSE;
				}
				else {
					if ( m_pManager )
						m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RICHEDIT_RESTOR_NORMAL);
				}
			}
			else if( msg == WM_KEYDOWN && TCHAR(wparam) == VK_RETURN ) {

				if (m_bWantReturn && GetKeyState(VK_CONTROL) < 0)
				{
					if( m_pManager != NULL ) 
						m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RETURN);
					return S_OK;
				}
				if (m_bWantCtrlReturn && GetKeyState(VK_CONTROL) >= 0)
				{
					TEventUI event = { 0 };
					event.Type = UIEVENT_KEYDOWN;
					event.chKey = (TCHAR)wparam;
					event.ptMouse.x = GET_X_LPARAM(lparam);
					event.ptMouse.y = GET_Y_LPARAM(lparam);
					event.wKeyState =  GetKeyState(VK_RETURN);
					if( m_pManager != NULL ) 
					{
						CControlUI* pControl =m_pParent->GetParent();
						if(NULL == pControl) return S_FALSE;
						while (pControl->GetName().IsEmpty())
						{
							pControl = pControl->GetParent();
							if(NULL == pControl) return S_FALSE;
						}
						if(NULL == pControl) return S_FALSE;
						pControl->DoEvent(event);
					}
					return S_OK;
				}
			}
			return CRichEditUI::TxSendMessage(msg, wparam, lparam, plresult);
		}
		return S_FALSE;
	}

	void CRenameRichEditUI::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pParent != NULL ) m_pParent->DoEvent(event);
			else CControlUI::DoEvent(event);
			return;
		}

		if( event.Type == UIEVENT_SETFOCUS ) {			
			m_bFocused = true;
			std::wstring str_text = this->GetText().GetData();
			if (-1 != str_text.find_last_of('.'))
			{
				this->SetSel(0,(long)str_text.find_last_of('.'));
			}
			else
			{
				this->SetSel(0,(long)str_text.length()+1);
			}
			this->SetFocusBorderColor(0xFF2E90E5);
			this->SetBkColor(0xFFFFFFFF);
			this->SetBorderSize(1);	
		}
		if( event.Type == UIEVENT_KILLFOCUS ) {
			m_bFocused = false;
			this->SetBorderSize(0);
			this->SetBkColor(0x00000000);
			this->SetMouseEnabled(false);
			this->SetSel(0,0);
			if ( m_pManager )
				m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RICHEDIT_RESTOR_NORMAL);

			CHorizontalLayoutUI * m_pHorLayout = static_cast<CHorizontalLayoutUI*>(this->GetParent());
			if (NULL != m_pHorLayout)
				m_pHorLayout->SetToolTip(this->GetText());
		}
		CRichEditUI::DoEvent(event);
	}

	void CRenameRichEditUI::DoPaint(HDC hDC, const RECT& rcPaint)
	{
		if(this->IsMouseEnabled())
		{
			if(!m_lastEnabled && this->GetText()!=this->GetToolTip())
			{
				this->SetText(this->GetToolTip());
				std::wstring realText = this->GetToolTip();
				if (-1 != realText.find_last_of('.'))
				{
					this->SetSel(0,(long)realText.find_last_of('.'));
				}
				else
				{
					this->SetSel(0,(long)realText.length()+1);
				}
			}
			m_lastEnabled = true;
			CRichEditUI::DoPaint(hDC, rcPaint);
		}
		else
		{
			CDuiString text = this->GetToolTip();
			//默认字体大小14
			int font = this->GetFont()==0?12:this->GetFont();
			//获取结尾省略的文本
			CDuiString endellipsisText = this->GetEndellipsisText(text, this->GetWidth(), font);
			if(this->GetText()!=endellipsisText)
			{
				this->SetText(endellipsisText);
			}
			m_lastEnabled = false;
			CRichEditUI::DoPaint(hDC, rcPaint);
		}
	}

	void CRenameRichEditUI::SetAttribute( LPCTSTR pstrName, LPCTSTR pstrValue )
	{
		if( _tcscmp(pstrName, _T("limitchars")) == 0 ) SetLimitChar(pstrValue);
		else CRichEditUI::SetAttribute(pstrName, pstrValue);
	}

	void CRenameRichEditUI::SetLimitChar( LPCTSTR pstrValue )
	{
		size_t ilen = _tcslen(pstrValue);
		if(ilen >= INT_MAX-1) return;

		m_pLimitChar = new wchar_t[ilen+1];
		memset_s((void*)m_pLimitChar, sizeof(wchar_t)*(ilen+1), 0, sizeof(wchar_t)*(ilen+1));
		memcpy_s((void*)m_pLimitChar, sizeof(wchar_t)*ilen, pstrValue, sizeof(wchar_t)*ilen);
	}

	LPCTSTR CRenameRichEditUI::GetLimitChar()
	{
		if( NULL != m_pLimitChar ) return m_pLimitChar;
		return L"";
	}

}
