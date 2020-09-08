#pragma once

class CScaleButtonUI : public CButtonUI
{
public:
	CScaleButtonUI(void);
	~CScaleButtonUI(void);

	LPCTSTR GetClass() const;
	LPVOID GetInterface(LPCTSTR pstrName);

	SIZE EstimateSize(SIZE szAvailable);

	void SetLayout(CHorizontalLayoutUI* pLayout){
		m_pLayout = pLayout;
	}

	void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
	void SetAddWidth(int iWidth);
protected:
	CHorizontalLayoutUI*	m_pLayout;
	int						m_iAddWidth;
	BOOL					m_bInit;
};

