#pragma once
//#include "WndShadow.h"
#include "ServerSysConfig.h"
#include "UserContext.h"
#include "ShareLinkNode.h"
#include "NoticeFrame.h"
#include "ShareLinkContextDialog.h"




namespace Onebox
{
	class ShareLinkCountDialog : public WindowImplBase
	{
	public:
		ShareLinkCountDialog(LPCTSTR strXML, UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager);
		~ShareLinkCountDialog();

		DUI_DECLARE_MESSAGE_MAP();
	protected:
		virtual CDuiString GetSkinFolder() ;
		virtual CDuiString GetSkinFile() ;
		virtual LPCTSTR GetWindowClassName(void) const ;
		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual void InitWindow();
		virtual void OnFinalMessage( HWND hWnd );
		virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void OnClick(TNotifyUI& msg);
		void OnEditUrlClick(int nType);
		void OnCopyUrlClick(int nType);
		void OnEmailUrlClick(int nType);
		void OnDeleteUrlClick(int nType);
		void OnCreateLinkClick();
		void OnCancelLinkClick();

		void SetLayout(ShareLinkNodeList::iterator iter, int nType);
		void EditUrl(CControlUI* pControl);
		void CopyUrl(std::wstring strUrl);
		void EmailUrl(CControlUI* pControl);
		void DeleteUrl(CControlUI* pControl, CControlUI* pParent);

		static bool SortShareLinkNode(const ShareLinkNode& nodeA, const ShareLinkNode& nodeB);
	protected:
		CDuiString	m_strXML;
		UserContext*	m_pUserContext;
		UIFileNode		m_nodeData;

		NoticeFrameMgr* m_pNoticeFrame;
		CPaintManagerUI& m_paintMgr;

		std::wstring linkCode_;
		BOOL			m_bInit;

		std::wstring	m_strLink1;
		std::wstring	m_strLink2;
		std::wstring	m_strLink3;

		bool			m_bShowShare;
	public:
		static void CreateDlg(CPaintManagerUI& paintManager, UserContext* context, UIFileNode& nodeData, bool bShowShare = true){
			ShareLinkCountDialog* pDlg = new ShareLinkCountDialog(_T("ShareLinkCountDialog.xml"), context, nodeData, paintManager);
			if (NULL == pDlg)	return ;
			pDlg->m_bShowShare = bShowShare;
			pDlg->Create(paintManager.GetPaintWindow(), _T("CreateLinkDialog"), UI_WNDSTYLE_DIALOG, UI_WNDSTYLE_EX_FRAME);
			pDlg->CenterWindow();
			pDlg->ShowModal();

			SAFE_DELETE_POINTER(pDlg);
		}
	};
}



