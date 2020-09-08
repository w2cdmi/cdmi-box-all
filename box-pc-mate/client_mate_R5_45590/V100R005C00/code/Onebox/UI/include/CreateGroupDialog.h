#pragma once
#include <vector>
#include "SearchTxt.h"

namespace Onebox
{
	const TCHAR vGroupCreateXml[]		=	_T("createGroup.xml");
	const TCHAR vBtnCloseCreate[]		=	_T("createGroup_close_btn");
	const TCHAR vRichEditName[]			=	_T("createGroup_nameCreate");
	const TCHAR vRichEditDescription[]	=	_T("createGroup_descriptionCreate");
	const TCHAR vBtnOK[]				=	_T("createGroup_ok");
	const TCHAR vBtnCancel[]			=	_T("createGroup_cancel");

	class CreateGroupDialog:  public CWindowWnd,public INotifyUI,public IDialogBuilderCallback
	{
	public:
		explicit CreateGroupDialog(UserContext* context);

		~CreateGroupDialog();  

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
    
		//void SetTransparent(int nOpacity);

	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pOkBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		CPaintManagerUI m_Paintm;
		CDialogBuilder m_dlgBuilder;
		CEditUI* m_name;
		CSearchTxtUI* m_description;
	};
}