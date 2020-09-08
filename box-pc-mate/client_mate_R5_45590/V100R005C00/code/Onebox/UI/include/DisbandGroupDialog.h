#pragma once
#include <vector>

namespace Onebox
{
	const TCHAR vGroupDisbandXml[]	=	_T("distandGroup.xml");
	const TCHAR vBtnCloseDisband[]	=	_T("distandGroup_close_btn");
	const TCHAR vBtnOKDisband[]		=	_T("distandGroup_ok");
	const TCHAR vBtnCancelDisband[] =	_T("distandGroup_cancel");

	class DisbandGroupDialog:  public CWindowWnd,public INotifyUI,public IDialogBuilderCallback
	{
	public:
		explicit DisbandGroupDialog(UserContext* context,UserGroupNodeInfo& data);

		~DisbandGroupDialog();  

	public:
		LPCTSTR GetWindowClassName() const;
		UINT GetClassStyle() const;
		void OnFinalMessage(HWND /*hWnd*/);

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		void Init();
		void OnTimer(UINT nIDEvent);
		virtual void  Notify(TNotifyUI& msg);

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
    
	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pOkBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		CPaintManagerUI m_Paintm;
		CDialogBuilder m_dlgBuilder;
		UserGroupNodeInfo m_groupData;
	};
}