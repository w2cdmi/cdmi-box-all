#pragma once
#include "stdafxOnebox.h"
#include "InILanguage.h"
#include "UIUserPhoto.h"
#include "SearchTxt.h"

namespace Onebox
{
	#define WM_SETDIALG WM_USER + 101
	class NoticeFrameMgr;

	class CSettingFrame : public WindowImplBase
	{
	public:
		CSettingFrame(UserContext* pUserContext, int32_t opentype);
		~CSettingFrame(void);

		enum CONF_SETTINGS
		{
			OFF,
			ON
		};

		DUI_DECLARE_MESSAGE_MAP();

		void InitWindow();

		CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		virtual LPCTSTR GetWindowClassName() const { return _T("DUI_SETTINGFRAME_CLS_NAME"); }

		virtual CDuiString GetSkinFolder() { return iniLanguageHelper.GetSkinFolderPath().c_str(); }

		virtual CDuiString GetSkinFile() { return _T("settingFrame.xml"); }

		LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void Notify(DuiLib::TNotifyUI& msg);

		virtual void OnClick(TNotifyUI& msg);
		void OnSelectChanged(TNotifyUI& msg);

		void InitControl();
		void InitData();
		void InitBaseInfo();
		void InitTransfer();
		void InitAdvancedSetting();
		void SaveAll();
		void CloseAll();
		bool IsModify(bool bPrompt = false);

		std::wstring SelectDir();
	private:
			void showTip(const RECT rt, const std::wstring& strText, BOOL bMaxChar = TRUE);
			void hideTip();

			template<typename T>
			T GetValue(const std::wstring valueKey,const T& defaultValue);

			template<typename T>
			void SetValue(const std::wstring valueKey,const T& value);
	private:
		CTabLayoutUI	*m_pTabLayout;

		//基本设置
		CUserPhotoUI	*m_pLblUserHead;
		CLabelUI		*m_pUserInfoTitle;
		CLabelUI		*m_pUserInfoContext;

		//传输设置
		CComboUI		*m_pComboAsyncTaskThreadNumber;
		CCheckBoxUI		*m_pCheckDownloadLimitSpeed;
		CEditUI			*m_pEditDownloadLimitSpeed;
		CCheckBoxUI		*m_pCheckUploadLimitSpeed;
		CEditUI			*m_pEditUploadLimitSpeed;
		CCheckBoxUI		*m_pCheckAutoDir;
		CSearchTxtUI			*m_pEditAutoDir;
		CLabelUI		*m_pBtnOpenFileDialog;

		//高级设置
		CCheckBoxUI		*m_pCheckAutoRun;
		CCheckBoxUI		*m_pCheckPopupNotification;
		CCheckBoxUI     *m_pResourceNotification;

		CCheckBoxUI		*m_pCheckRemPwd;
		CCheckBoxUI		*m_pCheckAutoLogin;

		CButtonUI		*m_pBtnSave;
		CButtonUI		*m_pBtnCancel;

		CDuiString		m_strUserInfoURL;

		UserContext		*m_pUserContext;
		NoticeFrameMgr*	m_noticeFrame_;

		HWND			m_hWndTip;
		TOOLINFO		m_InfoTip;
		bool			downloadFlag_;

		int32_t			m_iOpenType;

		bool			m_isInitfanish;
		int32_t			m_iCurrentPage;
	};
}

