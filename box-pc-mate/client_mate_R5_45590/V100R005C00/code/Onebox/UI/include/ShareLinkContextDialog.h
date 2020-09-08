#pragma once
//#include "WndShadow.h"
#include "ServerSysConfig.h"
#include "UserContext.h"
#include "NoticeFrame.h"

#include "ShareResMgr.h"
#include "SearchTxt.h"


#define SAFE_DELETE_POINTER(pointer)		{try{delete pointer;}catch(...){}pointer=NULL;}

namespace Onebox
{
	
	class ShareLinkContextDialog : public WindowImplBase
	{
	public:
		ShareLinkContextDialog(LPCTSTR xmlPath, UserContext* context, UIFileNode& nodeData, HWND hParentWnd);
		~ShareLinkContextDialog();

		DUI_DECLARE_MESSAGE_MAP();
	protected:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;
		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual void InitWindow();
		virtual void OnFinalMessage( HWND hWnd );
		LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void OnClick(TNotifyUI& msg);
		void OnSelectChanged(TNotifyUI& msg);
		void OnItemSelect(TNotifyUI& msg);


		CDuiString	m_strXML;
		CLabelUI*						m_pLabelTitle;
		CEditUI*						m_pEditUrl;
		CSearchTxtUI*					m_pEditCode;
		CSearchTxtUI*					m_pEditEmail;

		CCheckBoxUI*					m_pCheckDownload;
		CCheckBoxUI*					m_pCheckPreview;
		CCheckBoxUI*					m_pCheckUpload;
		CCheckBoxUI*					m_pCheckUseCode;
		CCheckBoxUI*					m_pCheckDynamicCode;
		CHorizontalLayoutUI*			m_pLayoutSwitch;
		CVerticalLayoutUI*				m_pLayoutCode;
		CVerticalLayoutUI*				m_pLayoutEmail;
		CVerticalLayoutUI*				m_pLayoutTime;

		CComboUI*						m_pComboTime;
		CDateTimeUI*					m_pDateStart;
		CDateTimeUI*					m_pDateStop;

		CButtonUI*						m_pBtnCopyLink;
		CButtonUI*						m_pBtnSendLink;
		CButtonUI*						m_pBtnFinish;
		CButtonUI*						m_pBtnCancel;
		CButtonUI*						m_pBtnRefresh;
		CControlUI*						m_pCtlRefresh;
		

		UserContext*	m_pUserContext;
		UIFileNode		m_nodeData;
		HWND			m_hParentWnd;
		ServerSysConfig m_serverSysConfig;

		int				m_nResult;
		std::wstring	m_strLinkUrl;
		NoticeFrameMgr* m_pNoticeFrame;

		BOOL			m_bViewer;
		ShareLinkNode	m_shareLinkNode;
	private:
		BOOL InitControl();
		void SetControl(ShareLinkNode& sharedLinkNode);

		void OnCheckPreview();
		void OnCheckDownload();
		void OnCheckUpload();
		void OnCheckUseCode();
		void OnCheckDynamicCode();
		void OnSelectTime();
		void OnCancelClick();
		int32_t OnFinishClick(bool bSend = true);
		void OnCloseClick();

		void ShowCode(bool bShow=true);
		void ShowEmail(bool bShow=true);
		void ShowTime(bool bShow=true);
		void CancelUrl();
		void CopyUrl();
		void SendEmail();
		void RefreshCode();

		inline int getResult(){return m_nResult;}
		inline void setLinkUrl(std::wstring strLinkUrl){m_strLinkUrl = strLinkUrl;}
		SYSTEMTIME int64TimeToSysTime(int64_t time);
		int64_t sysTimetoInt64Time(SYSTEMTIME& sysTime);
		//COMBO类型不好判断，使用SYSTEMTIME中wMilliseconds来标记
		time_t systime_to_time_t(const SYSTEMTIME& st);
		SYSTEMTIME time_t_to_systime(time_t t);


	public:
		static int CreateDlg( HWND hParentWnd, UserContext* context, UIFileNode& nodeData, std::wstring strLinkUrl){

			ShareLinkContextDialog* pDlg = new ShareLinkContextDialog(_T("ShareLinkContextDialog.xml"), context, nodeData, hParentWnd);
			if (NULL == pDlg) return 0;
			pDlg->setLinkUrl(strLinkUrl);
			pDlg->Create(hParentWnd, _T("ShareLinkDialog"), UI_WNDSTYLE_DIALOG, UI_WNDSTYLE_EX_FRAME);
			pDlg->CenterWindow();
			pDlg->ShowModal();

			int nRet = pDlg->getResult();
			SAFE_DELETE_POINTER(pDlg);

			return nRet;
		}
	};
}


