#pragma once


class CLeftOptionUI : public COptionUI
{
public:
	CLeftOptionUI();
	~CLeftOptionUI();


	void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);

	void SetSelectedFont(int iFont);
	void SetHotTextColor(DWORD dwColor);

	void SetBkColor(DWORD dwColor);
	void SetHotBkColor(DWORD dwColor);
	void SetSelectedBkColor(DWORD dwColor);

	void SetBlockWidth(int iWidth);
	void SetNormalBlockColor(DWORD dwColor);
	void SetHotBlockColor(DWORD dwColor);
	void SetSelectedBlockColor(DWORD dwColor);

	void SetNormalIconImage(LPCTSTR strImage);
	void SetHotIconImage(LPCTSTR strImage);
	void SetSelectedIconImage(LPCTSTR strImage);

	void PaintBkColor(HDC hDC);
	void PaintBkImage(HDC hDC);
	void PaintStatusImage(HDC hDC);
	void PaintText(HDC hDC);
	void PaintBorder(HDC hDC);

protected:
	int				m_iBlockWidth;
	int				m_iSelectedFont;
	DWORD			m_dwHotTextColor;
	DWORD			m_dwBkColor;
	DWORD			m_dwHotBkColor;
	DWORD			m_dwSelectedBkColor;
	DWORD			m_dwNormalBlockColor;
	DWORD			m_dwHotBlockColor;
	DWORD			m_dwSelectedBlockColor;
	CDuiString		m_strNormalIconImage;
	CDuiString		m_strHotIconImage;
	CDuiString		m_strSelectedIconImage;
};
