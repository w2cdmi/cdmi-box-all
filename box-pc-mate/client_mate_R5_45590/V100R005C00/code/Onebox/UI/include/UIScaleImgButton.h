#pragma once

#include "ScaleButton.h"


class CImgButtonUI : public CButtonUI
{
public:
	CImgButtonUI(){m_pMenuBtn = NULL;}
	void DoEvent(TEventUI& event);
	void SetMenuButton(CButtonUI* pBtn){m_pMenuBtn = pBtn;}

protected:
	CButtonUI*	m_pMenuBtn;
};

class CScaleImgButtonUI : public CHorizontalLayoutUI
{
public:
	CScaleImgButtonUI(void);
	~CScaleImgButtonUI(void);

	void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);

	void SetText(LPCTSTR pstrText);
	void SetToolTip(LPCTSTR pstrText);

protected:
	CScaleButtonUI*			m_pTextButton;
	CImgButtonUI*			m_pImgButton;

	int				m_iAddWidth;
//	CDuiString		m_strBkImage;
};




