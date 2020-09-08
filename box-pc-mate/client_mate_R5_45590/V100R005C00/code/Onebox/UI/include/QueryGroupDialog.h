#pragma once
#include <vector>

namespace Onebox
{
	const TCHAR vGroupLookXml[]		=	_T("lookGroup.xml");
	const TCHAR vBtnCloseLook[]		=	_T("lookGroup_close_btn");
	const TCHAR vBtnOKLook[]		=	_T("lookGroup_ok");
	const TCHAR vBtnCancelLook[]	=	_T("lookGroup_cancel");

	class QueryGroupDialog:  public CWindowWnd,public INotifyUI,public IDialogBuilderCallback
	{
	public:
		explicit QueryGroupDialog(UserContext* context,UserGroupNodeInfo& data);

		~QueryGroupDialog();  

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
		CButtonUI*	m_pCloseBtn;
		CButtonUI*	m_pOkBtn;
		CButtonUI*	m_pCancelBtn;
		CEditUI*	m_pName;
		CRichEditUI* m_pDesc;
		UserContext* userContext_;
		CPaintManagerUI m_Paintm;
		CDialogBuilder	m_dlgBuilder;
		UserGroupNodeInfo m_groupData;
	};
}