#ifndef ROUNDGIFUI_h__
#define ROUNDGIFUI_h__

#pragma once

#include "stdafxOnebox.h"

namespace Onebox
{
	#define EVENT_TIEM_ROUNDID	1000
	
	enum RUNSTATE
	{
		STOP = 0,
		START,
		PAUSE,	
		FINISH,
		ERRORSTATE,
		LOGOFF
	};

	class CRoundGifUI : public CControlUI
	{
	public:
		CRoundGifUI(void);
		~CRoundGifUI(void);
		void setModelName(LPCTSTR sName);
		LPCTSTR	GetClass() const;
		LPVOID	GetInterface(LPCTSTR pstrName);
		void	DoPaint(HDC hDC, const RECT& rcPaint) override;
		void	DoEvent(TEventUI& event) override;
		void	SetVisible(bool bVisible = true ) override;
		void	SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue) override;
		void	SetBkImage(LPCTSTR pStrImage);
		LPCTSTR GetBkImage();
		void	SetStretchForeImage( LPCTSTR pStretchForeImage);
		LPCTSTR GetStretchForeImage();
		void	SetRoundImage( LPCTSTR pRoundImage );
		LPCTSTR GetRoundImage();
		void	SetAngle(int nAngle);
		int		GetAngle();
		void	InitImage();
		void	SetRunState(RUNSTATE state);
		RUNSTATE	GetRunState();
		void SetEnabledRoundImage( LPCTSTR pEnableRoundImage);
		LPCTSTR GetEnableRoundImage();

	private:		
		void	DrawFrame( HDC hDC );		// ����ͼƬ
	private:
		CDuiString	m_sBkImage;				//����ͼƬ·��
		Image*		m_imgBk;				//����ͼƬ��Դ
		CDuiString	m_sStretchForeImage;	//ǰ��ͼƬ·���������ȣ�
		Image*		m_imgFore;				//ǰ��ͼƬ��Դ
		int32_t		m_nAngle;				//���ȣ��Ƕȣ�
		CDuiString	m_sRoundImage;			//�ڲ���̬��תͼƬ·��
		Image*		m_imgRound;				//�ڲ���̬��תͼƬ��Դ
		CDuiString	m_sEnableRoundImage;	//�ڲ���̬��תͼƬ���ɵ��״̬·��
		Image*		m_imgEnableRound;		//�ڲ���̬��תͼƬ���ɵ��״̬��Դ
		int32_t		m_trunAngle;			//��̬��תͼƬ��ת�Ƕ�
		RUNSTATE	m_nRunState;			//����״̬
		Gdiplus::Rect m_controlRect;		//��������
		CDuiString m_strTip;
		bool m_bIsGet;
	};
}

#endif // ROUNDGIFUI_h__
