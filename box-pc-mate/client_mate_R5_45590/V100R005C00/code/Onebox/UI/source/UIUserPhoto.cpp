#include "stdafxOnebox.h"
#include "UIUserPhoto.h"

namespace Onebox
{
	CUserPhotoUI::CUserPhotoUI()
	{
		m_gdiToken = 0;
	}

	CUserPhotoUI::~CUserPhotoUI()
	{
	}

	void CUserPhotoUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		if (0 == _tcsicmp(pstrName, _T("fixedimage")))
			m_strFixedImg = pstrValue;
		else
			CControlUI::SetAttribute(pstrName, pstrValue);
	}

	void CUserPhotoUI::PaintBkImage(HDC hDC)
	{
		if (m_strFixedImg.IsEmpty())	return;
		if (m_sBkImage.IsEmpty())		return;

		Graphics g(hDC);
		CDuiString strBkImg = CPaintManagerUI::GetResourcePath() + m_sBkImage;
		Image* pBkImage = Image::FromFile(strBkImg);
		if (NULL == pBkImage)
			return;
		CDuiString strFixedImg = CPaintManagerUI::GetResourcePath() + m_strFixedImg;
		Image* pFixedImage = Image::FromFile(strFixedImg);
		if (NULL == pFixedImage)
			return;

		int nWidth = m_rcItem.right-m_rcItem.left;
		int nHeight = m_rcItem.bottom-m_rcItem.top;

		g.DrawImage(pBkImage, Rect(m_rcItem.left, m_rcItem.top, nWidth, nHeight), 
			0, 0, pBkImage->GetWidth(), pBkImage->GetHeight(), UnitPixel);
		g.DrawImage(pFixedImage, Rect(m_rcItem.left, m_rcItem.top, nWidth, nHeight), 
			0, 0, pFixedImage->GetWidth(), pFixedImage->GetHeight(), UnitPixel);
		if (NULL != pBkImage)
		{
			delete pBkImage;
		}
		if (NULL != pFixedImage)
		{
			delete pFixedImage;
		}
	}
}
