#pragma once
#include <vector>
#include "SearchTxt.h"

namespace Onebox
{
	const TCHAR vGroupQuitXml[] =	_T("quitGroup.xml");
	const TCHAR vBtnCloseQuit[] =		_T("quitGroup_close_btn");
	const TCHAR vBtnOKQuit[] =			_T("quitGroup_ok");
	const TCHAR vBtnCancelQuit[] =		_T("quitGroup_cancel");

	class QuitGroupDialog:  public CWindowWnd,public INotifyUI,public IDialogBuilderCallback
	{
	public:
		explicit QuitGroupDialog(UserContext* context,UserGroupNodeInfo& data);

		~QuitGroupDialog();  

	public:
		LPCTSTR GetWindowClassName() const;
		UINT GetClassStyle() const;
		void OnFinalMessage(HWND /*hWnd*/);

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		void Init();
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