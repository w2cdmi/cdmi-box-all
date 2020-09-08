#pragma once


#include "ScaleButton.h"
class CScaleIconButtonUI : public CScaleButtonUI
{
public:
	CScaleIconButtonUI(void);
	~CScaleIconButtonUI(void);

	SIZE EstimateSize(SIZE szAvailable);
	void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
	void PaintStatusImage(HDC hDC);
protected:
	CDuiString	m_strIconImage;
	CDuiString	m_strIconHotImage;
	CDuiString	m_strIconDisableImage;
};