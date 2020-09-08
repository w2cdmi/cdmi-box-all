#include "stdafxOnebox.h"
#include "RoundGif.h"
#include <gdiplusinit.h>
#include "Utility.h"
namespace Onebox
{
	CRoundGifUI::CRoundGifUI(void)
	{	
		m_sStretchForeImage = L"";
		m_sBkImage = L"";
		m_sRoundImage = L"";
		m_sEnableRoundImage = L"";
		m_nAngle = 0;
		m_imgBk = NULL;
		m_imgFore = NULL;
		m_imgRound = NULL;
		m_imgEnableRound = NULL;
		m_trunAngle = 0;
		m_nRunState = START;
		m_controlRect = Rect(0,0,0,0);
		m_bIsGet = false;
		m_strTip = L"";
	}

	CRoundGifUI::~CRoundGifUI(void)
	{
		m_pManager->KillTimer( this, EVENT_TIEM_ROUNDID);
		delete m_imgBk;
		m_imgBk = NULL;
		delete m_imgFore;
		m_imgFore = NULL;
		delete m_imgRound;
		m_imgRound = NULL;
		delete m_imgEnableRound;
		m_imgEnableRound = NULL;
	}

	LPCTSTR CRoundGifUI::GetClass() const
	{
		return _T("CRoundGifUI");
	}

	LPVOID CRoundGifUI::GetInterface( LPCTSTR pstrName )
	{
		if( _tcscmp(pstrName, DUI_CTR_ROUNDGIF) == 0 ) return static_cast<CRoundGifUI*>(this);
		return CControlUI::GetInterface(pstrName);
	}

	void  CRoundGifUI::InitImage()
	{
		CDuiString strFile = CPaintManagerUI::GetResourcePath();
		if (m_sBkImage.IsEmpty()) m_sBkImage = L"..\\image\\ic_backup_circle_gray.png";
		m_imgBk = Image::FromFile(strFile + m_sBkImage);
		if (m_sStretchForeImage.IsEmpty()) m_sStretchForeImage = L"..\\image\\ic_backup_circle_gradient.png";
		m_imgFore = Image::FromFile(strFile + m_sStretchForeImage);
		if (m_sRoundImage.IsEmpty()) m_sRoundImage = L"..\\image\\ic_backup_circle_refresh.png";
		m_imgRound = Image::FromFile(strFile + m_sRoundImage);
		if (m_sEnableRoundImage.IsEmpty()) m_sEnableRoundImage = L"..\\image\\ic_backup_circle_refresh__gray.png";
		m_imgEnableRound = Image::FromFile(strFile + m_sEnableRoundImage);
	}

	void CRoundGifUI::DoPaint( HDC hDC, const RECT& rcPaint )
	{
		if( !::IntersectRect( &m_rcPaint, &rcPaint, &m_rcItem ) ) return;

		if ( NULL == m_imgBk || NULL == m_imgFore || NULL == m_imgRound || NULL == m_imgEnableRound)
		{		
			InitImage();
		}
		DrawFrame( hDC );
	}

	void CRoundGifUI::DoEvent( TEventUI& event )
	{
		if (event.Type ==  UIEVENT_TIMER)
		{
			if (event.wParam == EVENT_TIEM_ROUNDID)
			{
				m_pManager->KillTimer( this, EVENT_TIEM_ROUNDID );
				Invalidate();
			}
		}
		else if (event.Type ==  UIEVENT_BUTTONDOWN)
		{
			RECT rc;
			rc.left = m_controlRect.GetLeft() + m_rcItem.left;
			rc.top = m_controlRect.GetTop()+ m_rcItem.top;
			rc.right = m_controlRect.GetRight()+ m_rcItem.left;
			rc.bottom = m_controlRect.GetBottom()+ m_rcItem.top;
			if (!PtInRect(&rc, event.ptMouse))
				return;
			m_pManager->SendNotify(this, DUI_MSGTYPE_CLICK, event.wParam, event.lParam);
			if (RUNSTATE::START == m_nRunState)
			{
				SetRunState(RUNSTATE::PAUSE );
			}
			else if (RUNSTATE::PAUSE == m_nRunState)
			{
				SetRunState(RUNSTATE::START );
			}
			else if (RUNSTATE::STOP == m_nRunState)
			{
				SetRunState(RUNSTATE::STOP);
			}
			else if (RUNSTATE::FINISH == m_nRunState)
			{
				SetRunState(RUNSTATE::FINISH );
			}
			else if (RUNSTATE::ERRORSTATE == m_nRunState)
			{
				SetRunState(RUNSTATE::ERRORSTATE );
			}
			else if (RUNSTATE::LOGOFF == m_nRunState)
			{
				SetRunState(RUNSTATE::LOGOFF );
			}
		}
		else if (event.Type ==  UIEVENT_MOUSEMOVE)
		{
			if (m_bIsGet)
			{
				if (0 != this->GetToolTip().GetLength())
					m_strTip = this->GetToolTip();
				
			}
			else
			{
				m_bIsGet = true;
				m_strTip = this->GetToolTip();
			}
			
			RECT rc;
			rc.left = m_controlRect.GetLeft() + m_rcItem.left;
			rc.top = m_controlRect.GetTop()+ m_rcItem.top;
			rc.right = m_controlRect.GetRight()+ m_rcItem.left;
			rc.bottom = m_controlRect.GetBottom()+ m_rcItem.top;
			if (PtInRect(&rc, event.ptMouse))
			{
				this->SetToolTip(m_strTip);					
				::SetCursor(::LoadCursor(NULL, MAKEINTRESOURCE(IDC_HAND)));
			}
			else
			{
				this->SetToolTip(L"");					
				::SetCursor(::LoadCursor(NULL, MAKEINTRESOURCE(IDC_ARROW)));
			}
		}
		CControlUI::DoEvent(event);
	}

	void CRoundGifUI::SetVisible(bool bVisible /* = true */)
	{
		CControlUI::SetVisible(bVisible);
	}

	void CRoundGifUI::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		if( _tcscmp(pstrName, _T("bkimage")) == 0 ) SetBkImage(pstrValue);
		else if( _tcscmp(pstrName, _T("foreimage")) == 0 ) SetStretchForeImage(pstrValue);
		else if( _tcscmp(pstrName, _T("roundimage")) == 0 ) SetRoundImage(pstrValue);
		else if( _tcscmp(pstrName, _T("roundimageenable")) == 0 ) SetEnabledRoundImage(pstrValue);
		else
			CControlUI::SetAttribute(pstrName, pstrValue);
	}

	void CRoundGifUI::SetBkImage(LPCTSTR pStrImage)
	{
		if(NULL == pStrImage || m_sBkImage == pStrImage) return;
		m_sBkImage = pStrImage;
		Invalidate();
	}

	LPCTSTR CRoundGifUI::GetBkImage()
	{
		return m_sBkImage.GetData();
	}

	void CRoundGifUI::SetStretchForeImage( LPCTSTR pStretchForeImage /*= true*/ )
	{
		if (NULL == pStretchForeImage || m_sStretchForeImage == pStretchForeImage)		return;
		m_sStretchForeImage = pStretchForeImage;
		Invalidate();
	}

	LPCTSTR CRoundGifUI::GetStretchForeImage()
	{
		return m_sStretchForeImage;
	}

	void CRoundGifUI::SetRoundImage( LPCTSTR pRoundImage /*= true*/ )
	{
		if (NULL == pRoundImage || m_sRoundImage == pRoundImage)		return;
		m_sRoundImage = pRoundImage;
		Invalidate();
	}

	LPCTSTR CRoundGifUI::GetRoundImage()
	{
		return m_sRoundImage;
	}

	void CRoundGifUI::SetEnabledRoundImage( LPCTSTR pEnableRoundImage /*= true*/ )
	{
		if (NULL == pEnableRoundImage || m_sEnableRoundImage == pEnableRoundImage)		return;
		m_sEnableRoundImage = pEnableRoundImage;
		Invalidate();
	}

	LPCTSTR CRoundGifUI::GetEnableRoundImage()
	{
		return m_sEnableRoundImage;
	}
	
	void CRoundGifUI::SetAngle(int nAngle)
	{
		if (m_nAngle == nAngle  || 0 > nAngle)		return;
		m_nAngle = nAngle;
		Invalidate();
	}

	int CRoundGifUI::GetAngle()
	{
		return m_nAngle;
	}

	void CRoundGifUI::SetRunState(RUNSTATE state)
	{
		m_nRunState = state;
		if (RUNSTATE::PAUSE == m_nRunState)
		{
			m_pManager->SetTimer(this,EVENT_TIEM_ROUNDID,100);
		}
		else
		{
			m_pManager->KillTimer( this, EVENT_TIEM_ROUNDID );
			Invalidate();
		}
	}

	RUNSTATE CRoundGifUI::GetRunState()
	{
		return m_nRunState;
	}

	void CRoundGifUI::DrawFrame( HDC hDC)
	{
		HDC pHdc = CreateCompatibleDC(hDC);
		HBITMAP bitmap = CreateCompatibleBitmap(hDC,m_rcItem.right - m_rcItem.left,m_rcItem.bottom - m_rcItem.top);
		SelectObject(pHdc, bitmap);
		long rcX = (m_rcItem.right - m_rcItem.left)/2;
		long rcY = (m_rcItem.bottom - m_rcItem.top)/2;
		if ( NULL == hDC ||  NULL == pHdc) return;	

		Gdiplus::Graphics graphics( pHdc );	
		graphics.SetSmoothingMode(SmoothingModeAntiAlias);
		//ÃÓ≥‰ª∫¥ÊDC±≥æ∞
		Gdiplus::SolidBrush bru(Color(242,242,242));
		graphics.FillRectangle(&bru, -1,-1,m_rcItem.right - m_rcItem.left+1,m_rcItem.bottom - m_rcItem.top+1);		

		//ª≠±≥æ∞
		int bkImgX = m_imgBk->GetWidth();
		int bkImgY = m_imgBk->GetHeight();
		int bkImgDestX = rcX - bkImgX/2;
		int bkImgDestY = rcY - bkImgY/2;
		graphics.DrawImage( m_imgBk, bkImgDestX, bkImgDestY,0,0, bkImgX, bkImgY, UnitPixel);
		
		//ª≠Ω¯∂»
		int foreImgX = m_imgFore->GetWidth();
		int foreImgY = m_imgFore->GetHeight();
		Gdiplus::TextureBrush picBrush(m_imgFore);
		int foreImgDestX = rcX - foreImgX/2;
		int foreImgDestY = rcY - foreImgY/2;
		picBrush.TranslateTransform(REAL(foreImgDestX-foreImgX),REAL(foreImgDestY-foreImgY));
		if (m_nRunState == RUNSTATE::LOGOFF) m_nAngle = 0;
		graphics.FillPie(&picBrush,REAL(foreImgDestX), REAL(foreImgDestY),REAL(foreImgX),REAL(foreImgY),-90,REAL(m_nAngle));

		//ª≠∂ØÃ¨–˝◊™Õº∆¨
		CDuiString strFile = CPaintManagerUI::GetResourcePath();
		 if (m_nRunState == RUNSTATE::LOGOFF)
		{
			int stopImgX = m_imgEnableRound->GetWidth();
			int stopImgY = m_imgEnableRound->GetHeight();
			int stopImgDestX = rcX - stopImgX/2;
			int stopImgDestY = rcY - stopImgY/2;
			Gdiplus::Rect rcDest = Rect(stopImgDestX, stopImgDestY, stopImgX, stopImgY);
			graphics.DrawImage(m_imgEnableRound, rcDest.GetLeft(), rcDest.GetTop(), stopImgX, stopImgY);
			m_controlRect = rcDest;
		}
		else
		{
			int roundImgX = m_imgRound->GetWidth();
			int roundImgY = m_imgRound->GetHeight();
			int roundImgDestX = rcX - roundImgX/2;
			int roundImgDestY = rcY - roundImgY/2;
			int nCenterImgX = roundImgX/2;
			int nCenterImgY = roundImgY/2;
			Gdiplus::Rect rcDest = Rect(roundImgDestX, roundImgDestY,
				roundImgX, roundImgY);
			if (m_nRunState == RUNSTATE::PAUSE)
			{
				m_trunAngle += 5;
				if (m_trunAngle >= 360) m_trunAngle = 0;		
				Gdiplus::Matrix matrix;
				matrix.RotateAt(REAL(m_trunAngle), PointF(REAL(nCenterImgX+rcDest.GetLeft()),REAL(nCenterImgY+rcDest.GetTop())));
				graphics.SetTransform(&matrix);
				m_pManager->SetTimer(this,EVENT_TIEM_ROUNDID,100);
			}
			graphics.DrawImage(m_imgRound, rcDest.GetLeft(), rcDest.GetTop(), roundImgX, roundImgY);
			m_controlRect = rcDest;
			graphics.ResetTransform();
		}

		//ª≠◊¥Ã¨	
		Image* _imgState = NULL;
		if (m_nRunState == RUNSTATE::PAUSE)
		{			
			_imgState = Image::FromFile(strFile += L"..\\image\\ic_backup_circle_state_pause.png");
		}
		else if (m_nRunState == RUNSTATE::START)
		{
			_imgState = Image::FromFile(strFile + L"..\\image\\ic_backup_circle_state_play.png");
		}
		else if (m_nRunState == RUNSTATE::FINISH)
		{
			_imgState = Image::FromFile(strFile + L"..\\image\\ic_backup_circle_state_finish.png");
		}
		else if (m_nRunState == RUNSTATE::ERRORSTATE)
		{
			_imgState = Image::FromFile(strFile + L"..\\image\\ic_backup_circle_state_finish_erro.png");
		}
		else if (m_nRunState == RUNSTATE::LOGOFF)
		{
			_imgState = Image::FromFile(strFile + L"..\\image\\ic_backup_circle_state_play_gray.png");
		}
		else if (m_nRunState == RUNSTATE::STOP)
		{
			_imgState = Image::FromFile(strFile + L"..\\image\\ic_backup_circle_state_play.png");
		}
		if(NULL==_imgState) return;
		int _imgStateX = _imgState->GetWidth();
		int _imgStateY = _imgState->GetHeight();
		int _imgStateDestX = rcX - _imgStateX/2;
		int _imgStateDestY = rcY - _imgStateY/2;
		graphics.DrawImage( _imgState, Rect(_imgStateDestX, _imgStateDestY,_imgStateX, _imgStateY),0,0, _imgStateX, _imgStateY, UnitPixel);
		delete _imgState;
		_imgState = NULL;

		BitBlt(hDC, m_rcItem.left, m_rcItem.top, m_rcItem.right-m_rcItem.left, m_rcItem.bottom-m_rcItem.top,
			pHdc, 0, 0, SRCCOPY);
		::DeleteObject(bitmap);
		::DeleteDC(pHdc);  //“™”√delete¿¥ Õ∑≈ª∫¥ÊDC
	}
}