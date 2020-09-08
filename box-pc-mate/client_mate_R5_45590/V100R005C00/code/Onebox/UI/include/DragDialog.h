#pragma once
//#include "StdAfxOnebox.h"
#include <vector>
//#include "DragDialogItem.h"


namespace Onebox
{
	#define		FILE_ICON			22
	#define 	FILE_ICON_DISTANT	3
	#define		FILE_SHOW_NUM		8

	typedef struct ItemInfo
	{
		std::wstring fileIcon;
		std::wstring fileName;
	}ItemInfo;

	class DragDialog:  public CWindowWnd
	{
	public:
		explicit DragDialog();

	protected:
		virtual ~DragDialog();  

	public:
		LPCTSTR GetWindowClassName() const;
		UINT GetClassStyle() const;
		void OnFinalMessage(HWND /*hWnd*/);

		//void ResetWindow(LONG pty, MEDIA* pMedia);
		void Init(LPCTSTR pszXMLPath, HWND hWmdParent, POINT pt,std::vector<ItemInfo>info);
		//virtual void  Notify(TNotifyUI& msg);

		//virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnKillFocus(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnMouseLeave(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
    
		void SetDialogPos(POINT pt);
		void SetTransparent(int nOpacity);
		void SetWindowShow(bool isShow);
		void SetMovedName(std::wstring name);

		void AddItem(std::vector<ItemInfo> & value);
		void Add(std::vector<ItemInfo> & value);
	protected:
		CLabelUI *	pLabel;
		CLabelUI * pText;
		CDuiString		m_strXMLPath;
		CPaintManagerUI m_DragPaintm;
		CDialogBuilder m_dlgBuilder;
		HWND			m_hParentWnd;
		POINT			m_pt;
		HWND m_hwnd;
		std::vector<ItemInfo> m_info;
		CLabelUI* pName;
		int m_width;
		int m_heght;
	};


}