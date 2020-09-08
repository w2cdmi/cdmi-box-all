#include "stdafxOnebox.h"
#include "ScaleButton.h"


CScaleButtonUI::CScaleButtonUI(void)
{
	m_pLayout = NULL;
	m_iAddWidth = 0;
	m_bInit = FALSE;
}


CScaleButtonUI::~CScaleButtonUI(void)
{
}

LPCTSTR CScaleButtonUI::GetClass() const
{
	return _T("ScaleButtonUI");
}

LPVOID CScaleButtonUI::GetInterface(LPCTSTR pstrName)
{
	if( _tcscmp(pstrName, L"ScaleButtonUI") == 0 ) return static_cast<CScaleButtonUI*>(this);
	return CButtonUI::GetInterface(pstrName);
}

void CScaleButtonUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
{
	if( _tcscmp(pstrName, _T("addwidth")) == 0 ) SetAddWidth(_ttoi(pstrValue));
	else
		return CButtonUI::SetAttribute(pstrName, pstrValue);
}

void CScaleButtonUI::SetAddWidth(int iWidth)
{
	m_iAddWidth = iWidth;
}

SIZE CScaleButtonUI::EstimateSize(SIZE szAvailable)
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

	if (m_pLayout){
		if (m_iAddWidth > 0){
			int nWidth = cXY.cx+m_iAddWidth;
			if (nWidth > GetMaxWidth() )
				nWidth = GetMaxWidth() + m_iAddWidth ;
			if (nWidth != m_pLayout->GetFixedWidth())
				m_pLayout->SetFixedWidth(nWidth);
		}
		else{
			int nWidth = cXY.cx+16;
			if (nWidth > 88)
				nWidth = 88;
			m_pLayout->SetMaxWidth(nWidth);
		}
	}
	
	if( m_cxyFixed.cy != 0 ) cXY.cy = m_cxyFixed.cy;
	return cXY;
}