#include "stdafxOnebox.h"
#include "UILeftOption.h"




CLeftOptionUI::CLeftOptionUI()
{	
	m_iBlockWidth = 0;
	m_iSelectedFont = -1;
	m_dwHotTextColor = 0;

	m_dwBkColor = 0;
	m_dwHotBkColor = 0;
	m_dwSelectedBkColor = 0;

	m_dwNormalBlockColor = 0;
	m_dwHotBlockColor = 0;
	m_dwSelectedBlockColor = 0;

	m_strNormalIconImage = _T("");
	m_strHotIconImage = _T("");
	m_strSelectedIconImage = _T("");
}


CLeftOptionUI::~CLeftOptionUI()
{

}


void CLeftOptionUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
{
	if( _tcscmp(pstrName, _T("blockwidth")) == 0 )
		SetBlockWidth(_ttoi(pstrValue));
	else if( _tcscmp(pstrName, _T("selectedfont")) == 0 )
		SetSelectedFont(_ttoi(pstrValue));
	else if( _tcscmp(pstrName, _T("hottextcolor")) == 0 ){
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetHotTextColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("bkcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetBkColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("hotbkcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetHotBkColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("selectedbkcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetSelectedBkColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("normalblockcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetNormalBlockColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("hotblockcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetHotBlockColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("selectedblockcolor")) == 0 ) {
		if( *pstrValue == _T('#')) pstrValue = ::CharNext(pstrValue);
		LPTSTR pstr = NULL;
		DWORD clrColor = _tcstoul(pstrValue, &pstr, 16);
		SetSelectedBlockColor(clrColor);
	}
	else if( _tcscmp(pstrName, _T("normaliconimage")) == 0 ) 
		SetNormalIconImage(pstrValue);
	else if( _tcscmp(pstrName, _T("hoticonimage")) == 0 ) 
		SetHotIconImage(pstrValue);
	else if( _tcscmp(pstrName, _T("selectediconimage")) == 0 ) 
		SetSelectedIconImage(pstrValue);
	else
		COptionUI::SetAttribute(pstrName, pstrValue);
}



void CLeftOptionUI::SetSelectedFont(int iFont)
{
	m_iSelectedFont = iFont;
}

void CLeftOptionUI::SetHotTextColor(DWORD dwColor)
{
	m_dwHotTextColor = dwColor;
}

void CLeftOptionUI::SetBkColor(DWORD dwColor)
{
	m_dwBkColor = dwColor;
}
void CLeftOptionUI::SetHotBkColor(DWORD dwColor)
{
	m_dwHotBkColor = dwColor;
}
void CLeftOptionUI::SetSelectedBkColor(DWORD dwColor)
{
	m_dwSelectedBkColor = dwColor;
}

void CLeftOptionUI::SetBlockWidth(int iWidth)
{
	m_iBlockWidth = iWidth;
}
void CLeftOptionUI::SetNormalBlockColor(DWORD dwColor)
{
	m_dwNormalBlockColor = dwColor;
}
void CLeftOptionUI::SetHotBlockColor(DWORD dwColor)
{
	m_dwHotBlockColor = dwColor;
}
void CLeftOptionUI::SetSelectedBlockColor(DWORD dwColor)
{
	m_dwSelectedBlockColor = dwColor;
}

void CLeftOptionUI::SetNormalIconImage(LPCTSTR strImage)
{
	m_strNormalIconImage = strImage;
}

void CLeftOptionUI::SetHotIconImage(LPCTSTR strImage)
{
	m_strHotIconImage = strImage;
}

void CLeftOptionUI::SetSelectedIconImage(LPCTSTR strImage)
{
	m_strSelectedIconImage = strImage;
}

void CLeftOptionUI::PaintBkColor(HDC hDC)
{
	m_uButtonState &= ~UISTATE_PUSHED;

	if( (m_uButtonState & UISTATE_SELECTED) != 0){
		if( m_dwSelectedBkColor != 0 )
			CRenderEngine::DrawColor(hDC, m_rcItem, GetAdjustColor(m_dwSelectedBkColor));
		if ((m_iBlockWidth > 0) && (m_dwSelectedBlockColor != 0)){
			RECT rc = m_rcItem;
			rc.left = rc.right-m_iBlockWidth;
			CRenderEngine::DrawColor(hDC, rc,GetAdjustColor(m_dwSelectedBlockColor));
		}
	}	
	else if( (m_uButtonState & UISTATE_HOT) != 0){
		if( m_dwHotBkColor != 0 )
			CRenderEngine::DrawColor(hDC, m_rcItem, GetAdjustColor(m_dwHotBkColor));
		if ((m_iBlockWidth > 0) && (m_dwHotBlockColor != 0)){
			RECT rc = m_rcItem;
			rc.left = rc.right-m_iBlockWidth;
			CRenderEngine::DrawColor(hDC, rc,GetAdjustColor(m_dwHotBlockColor));
		}
	} else{
		if( m_dwBkColor != 0 )
			CRenderEngine::DrawColor(hDC, m_rcItem, GetAdjustColor(m_dwBkColor));
		if ((m_iBlockWidth > 0) && (m_dwNormalBlockColor != 0)){
			RECT rc = m_rcItem;
			rc.left = rc.right-m_iBlockWidth;
			CRenderEngine::DrawColor(hDC, rc,GetAdjustColor(m_dwNormalBlockColor));
		}
	}
}

void CLeftOptionUI::PaintBkImage(HDC hDC)
{
	m_uButtonState &= ~UISTATE_PUSHED;

	if( (m_uButtonState & UISTATE_SELECTED) != 0){
		if (!m_strSelectedIconImage.IsEmpty()){
			if (!DrawImage(hDC, (LPCTSTR)m_strSelectedIconImage))
				m_strSelectedIconImage.Empty();
		}
	}else if( (m_uButtonState & UISTATE_HOT) != 0){
		if (!m_strHotIconImage.IsEmpty()){
			if (!DrawImage(hDC, (LPCTSTR)m_strHotIconImage))
				m_strHotIconImage.Empty();
		}
	}else{
		if (!m_strNormalIconImage.IsEmpty()){
			if (!DrawImage(hDC, (LPCTSTR)m_strNormalIconImage))
				m_strNormalIconImage.Empty();
		}
	}
}

void CLeftOptionUI::PaintStatusImage(HDC hDC)
{

}

void CLeftOptionUI::PaintText(HDC hDC)
{
	if( (m_uButtonState & UISTATE_SELECTED) != 0 )
	{
		DWORD oldTextColor = m_dwTextColor;
		if( m_dwSelectedTextColor != 0 ) m_dwTextColor = m_dwSelectedTextColor;

		if( m_dwTextColor == 0 ) m_dwTextColor = m_pManager->GetDefaultFontColor();
		if( m_dwDisabledTextColor == 0 ) m_dwDisabledTextColor = m_pManager->GetDefaultDisabledColor();

		if( m_sText.IsEmpty() ) return;
		int nLinks = 0;
		RECT rc = m_rcItem;
		rc.left += m_rcTextPadding.left;
		rc.right -= m_rcTextPadding.right;
		rc.top += m_rcTextPadding.top;
		rc.bottom -= m_rcTextPadding.bottom;

		if( m_bShowHtml )
			CRenderEngine::DrawHtmlText(hDC, m_pManager, rc, m_sText, IsEnabled()?m_dwTextColor:m_dwDisabledTextColor, \
			NULL, NULL, nLinks, m_uTextStyle);
		else
			CRenderEngine::DrawText(hDC, m_pManager, rc, m_sText, IsEnabled()?m_dwTextColor:m_dwDisabledTextColor, \
			m_iSelectedFont, m_uTextStyle);

		m_dwTextColor = oldTextColor;
	}else if( (m_uButtonState & UISTATE_HOT) != 0 )
	{
		DWORD oldTextColor = m_dwTextColor;
		if( m_dwHotTextColor != 0 ) m_dwTextColor = m_dwHotTextColor;

		if( m_dwTextColor == 0 ) m_dwTextColor = m_pManager->GetDefaultFontColor();
		if( m_dwDisabledTextColor == 0 ) m_dwDisabledTextColor = m_pManager->GetDefaultDisabledColor();

		if( m_sText.IsEmpty() ) return;
		int nLinks = 0;
		RECT rc = m_rcItem;
		rc.left += m_rcTextPadding.left;
		rc.right -= m_rcTextPadding.right;
		rc.top += m_rcTextPadding.top;
		rc.bottom -= m_rcTextPadding.bottom;

		if( m_bShowHtml )
			CRenderEngine::DrawHtmlText(hDC, m_pManager, rc, m_sText, IsEnabled()?m_dwTextColor:m_dwDisabledTextColor, \
			NULL, NULL, nLinks, m_uTextStyle);
		else
			CRenderEngine::DrawText(hDC, m_pManager, rc, m_sText, IsEnabled()?m_dwTextColor:m_dwDisabledTextColor, \
			m_iFont, m_uTextStyle);

		m_dwTextColor = oldTextColor;
	}
	else
		CButtonUI::PaintText(hDC);
}

void CLeftOptionUI::PaintBorder(HDC hDC)
{

}
