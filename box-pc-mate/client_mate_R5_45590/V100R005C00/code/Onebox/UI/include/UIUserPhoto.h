#pragma once

namespace Onebox
{
	class CUserPhotoUI : public CControlUI
	{
	public:
		CUserPhotoUI();
		~CUserPhotoUI();

		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void PaintBkImage(HDC hDC);

	protected:
		ULONG_PTR	m_gdiToken;
		CDuiString	m_strFixedImg;
	};
}

