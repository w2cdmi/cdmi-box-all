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
		void	DrawFrame( HDC hDC );		// 绘制图片
	private:
		CDuiString	m_sBkImage;				//背景图片路径
		Image*		m_imgBk;				//背景图片资源
		CDuiString	m_sStretchForeImage;	//前景图片路径（即进度）
		Image*		m_imgFore;				//前景图片资源
		int32_t		m_nAngle;				//进度（角度）
		CDuiString	m_sRoundImage;			//内部动态旋转图片路径
		Image*		m_imgRound;				//内部动态旋转图片资源
		CDuiString	m_sEnableRoundImage;	//内部动态旋转图片不可点击状态路径
		Image*		m_imgEnableRound;		//内部动态旋转图片不可点击状态资源
		int32_t		m_trunAngle;			//动态旋转图片旋转角度
		RUNSTATE	m_nRunState;			//运行状态
		Gdiplus::Rect m_controlRect;		//操作区域
		CDuiString m_strTip;
		bool m_bIsGet;
	};
}

#endif // ROUNDGIFUI_h__
