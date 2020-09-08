#include "stdafxOnebox.h"
#include "UIScaleIconButton.h"


CScaleIconButtonUI::CScaleIconButtonUI()
{
	m_strIconImage = _T("");
	m_strIconHotImage = _T("");
	m_strIconDisableImage = _T("");
}

CScaleIconButtonUI::~CScaleIconButtonUI()
{

}

SIZE CScaleIconButtonUI::EstimateSize(SIZE szAvailable)
{
	RECT rcText = { 0, 0, max(szAvailable.cx, m_cxyFixed.cx), 9999 };
	rcText.left += m_rcTextPadding.left;
	rcText.right -= m_rcTextPadding.right;
	if( m_bShowHtml ) {   
		int nLinks = 0;
		CRenderEngine::DrawHtmlText(m_pManager->GetPaintDC(), m_pManager, rcText, m_sText, m_dwTextColor, NULL, NULL, nLinks, DT_CALCRECT | m_uTextStyle);
	}
	else {
		CRenderEngine::DrawText(m_pManager->GetPaintDC(), m_pManager, rcText, m_sText, m_dwTextColor, m_iFont, DT_CALCRECT | m_uTextStyle);
	}
	SIZE cXY = {rcText.right - rcText.left + m_rcTextPadding.left + m_rcTextPadding.right,
		rcText.bottom - rcText.top + m_rcTextPadding.top + m_rcTextPadding.bottom};
	
	if( m_cxyFixed.cy != 0 ) cXY.cy = m_cxyFixed.cy;
	return cXY;
}

void CScaleIconButtonUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
{
	if (0 == _tcsicmp(pstrName, _T("iconimage")))
		m_strIconImage = pstrValue;
	else if (0 == _tcsicmp(pstrName, _T("iconhotimage")))
		m_strIconHotImage = pstrValue;
	else if (0 == _tcsicmp(pstrName, _T("disablediconimage")))
		m_strIconDisableImage = pstrValue;
	else
		CScaleButtonUI::SetAttribute(pstrName, pstrValue);
}

void CScaleIconButtonUI::PaintStatusImage(HDC hDC)
{
	CScaleButtonUI::PaintStatusImage(hDC);

	if( (m_uButtonState & UISTATE_DISABLED) != 0 ) {
		if( !m_strIconDisableImage.IsEmpty() )
		{
			if( !DrawImage(hDC, (LPCTSTR)m_strIconDisableImage) ) 
				m_strIconDisableImage.Empty();
		}
	}else if ((m_uButtonState & UISTATE_HOT) != 0){
		if (!m_strIconHotImage.IsEmpty()){
			if ( !DrawImage(hDC, (LPCTSTR)m_strIconHotImage))
				m_strIconHotImage.Empty();
		}
		else{
			if (!m_strIconImage.IsEmpty()){
				if( !DrawImage(hDC, (LPCTSTR)m_strIconImage) ) 
					m_strIconImage.Empty();
			}
		}
	}else
	{
		if (!m_strIconImage.IsEmpty()){
			if( !DrawImage(hDC, (LPCTSTR)m_strIconImage) ) 
				m_strIconImage.Empty();
		}
	}
	
}