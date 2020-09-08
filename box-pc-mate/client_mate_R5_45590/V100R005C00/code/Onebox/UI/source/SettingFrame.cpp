#include "stdafxOnebox.h"
#include "SettingFrame.h"
#include "Utility.h"
#include "DialogBuilderCallbackImpl.h"
#include "PathMgr.h"
#include "InILanguage.h"
#include "InIHelper.h"
#include "ConfigureMgr.h"
#include "UploadFrame.h"
#include "UserContext.h"
#include "UserInfo.h"
#include "UserInfoMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "NoticeFrame.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "ProxyMgr.h"

#include "ShellCommonFileDialog.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogRemoteNotify.h"

#define MIN_EDIT_NUMBER		(10)
#define PATH_LIMIT_CHARS    (L" : * | < > ? \" / ")

#define OPEN_NORMAL (0)
#define OPEN_ADVANCED (1)

using namespace SD::Utility;
namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(CSettingFrame, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED,OnSelectChanged)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MOUSEENTER,Notify)
	DUI_END_MESSAGE_MAP()


	CSettingFrame::CSettingFrame(UserContext* pUserContext, int32_t opentype)
	: m_pUserContext(pUserContext),m_hWndTip(NULL), m_iOpenType(opentype)
	{
		m_pTabLayout = NULL;

		//基本设置
		m_pLblUserHead = NULL;
		m_pUserInfoTitle = NULL;
		m_pUserInfoContext = NULL;

		//传输设置
		m_pComboAsyncTaskThreadNumber = NULL;
		m_pCheckDownloadLimitSpeed = NULL;
		m_pCheckDownloadLimitSpeed = NULL;
		m_pCheckUploadLimitSpeed = NULL;
		m_pCheckUploadLimitSpeed = NULL;
		m_pCheckAutoDir = NULL;
		m_pEditAutoDir = NULL;
		m_pBtnOpenFileDialog = NULL;

		//高级设置
		m_pCheckAutoRun = NULL;
		m_pCheckPopupNotification = NULL;
		m_pResourceNotification = NULL;
		m_pCheckRemPwd = NULL;
		m_pCheckAutoLogin = NULL;

		m_pEditDownloadLimitSpeed = NULL;
		m_pEditUploadLimitSpeed = NULL;
		m_pBtnSave = NULL;
		m_pBtnCancel = NULL;
		m_noticeFrame_ = NULL;

		m_strUserInfoURL = L"";
		downloadFlag_ = false;
	}

	CSettingFrame::~CSettingFrame(void)
	{
		if (NULL != m_noticeFrame_){
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;
		}
	}

	std::wstring CSettingFrame::SelectDir()
	{
		if (true == downloadFlag_) return L"";
		ShellCommonFileDialogParam param;
		param.type = OpenAFolder;
		std::auto_ptr<ShellCommonFileDialog> fileDialog(new ShellCommonFileDialog(param));
		ShellCommonFileDialogResult result;
		downloadFlag_ = true;
		(void)fileDialog->getResults(result);
		downloadFlag_ = false;
		if(1 != result.size()) return L"";

		return *(result.begin());
	}

	CControlUI* CSettingFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool CSettingFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void CSettingFrame::InitWindow()
	{
		m_noticeFrame_ = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
		m_pTabLayout = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(_T("Setting_tab")));
		if (NULL != m_pTabLayout)
			m_pTabLayout->SelectItem(0);
		CDuiString strUrl = m_pUserContext->getConfigureMgr()->getConfigure()->serverUrl().c_str();
		m_strUserInfoURL = strUrl.Left(strUrl.GetLength()-6) + DEFAULT_USER_SETTINGS_WEB;

		InitControl();
		InitData();

		if ( OPEN_ADVANCED == m_iOpenType )
		{
			COptionUI* pControl = static_cast<COptionUI*>(m_PaintManager.FindControl(_T("leftRegion_advanced_Setting")));
			if( pControl ) pControl->Selected(true);
		}

		m_isInitfanish = true;
	}

	void CSettingFrame::InitData()
	{
		InitBaseInfo();
		InitTransfer();
		InitAdvancedSetting();
	}

	LRESULT CSettingFrame::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (wParam == SC_CLOSE)
		{
			Close();
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	void CSettingFrame::showTip(const RECT rt, const std::wstring& strText, BOOL bMaxChar/* = TRUE*/)
	{
		(void)memset_s(&m_InfoTip, sizeof(m_InfoTip), 0, sizeof(m_InfoTip));
		m_InfoTip.cbSize = sizeof(m_InfoTip);
		m_InfoTip.hwnd = m_hWnd;
		m_InfoTip.uFlags = TTF_CENTERTIP;	
		m_InfoTip.rect = rt;

		m_InfoTip.lpszText = (LPWSTR)strText.c_str();

		if (NULL == m_hWndTip){
			m_hWndTip = ::CreateWindowEx(0, TOOLTIPS_CLASS, NULL, WS_POPUP | TTS_NOPREFIX | TTS_ALWAYSTIP,
				CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
				m_hWnd, NULL, CPaintManagerUI::GetInstance(), NULL);
			::SendMessage(m_hWndTip, TTM_ADDTOOL, 0, (LPARAM) &m_InfoTip);
		}

		::SendMessage(m_hWndTip, TTM_SETTOOLINFO, 0, (LPARAM) &m_InfoTip);
		::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, TRUE, (LPARAM) &m_InfoTip);
	}

	void CSettingFrame::hideTip()
	{
		if( m_hWndTip != NULL )
			::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, FALSE, (LPARAM) &m_InfoTip);
	}

	void CSettingFrame::Notify(DuiLib::TNotifyUI& msg)
	{
		if (msg.sType == DUI_MSGTYPE_KILLFOCUS){
			if (msg.pSender == m_pEditUploadLimitSpeed)
			{
				int32_t iUpSpeed = SD::Utility::String::string_to_type<int32_t>(m_pEditUploadLimitSpeed->GetText().GetData());
				if (iUpSpeed < MIN_EDIT_NUMBER){
					CDuiString strText;
					strText.Format(L"%d", MIN_EDIT_NUMBER);
					m_pEditUploadLimitSpeed->SetText(strText);
				}
			}
			else if (msg.pSender == m_pEditDownloadLimitSpeed){
				int32_t iDownSpeed = SD::Utility::String::string_to_type<int32_t>(m_pEditDownloadLimitSpeed->GetText().GetData());
				if (iDownSpeed < MIN_EDIT_NUMBER){
					CDuiString strText;
					strText.Format(L"%d", MIN_EDIT_NUMBER);
					m_pEditDownloadLimitSpeed->SetText(strText);
				}
			}
		}
		else if( msg.sType == DUI_MSGTYPE_EDIT_INVALID_CHARACTER )
		{
			RECT rt = {0, 0, 0, 0};
			CSearchTxtUI* edit = static_cast<CSearchTxtUI*>(msg.pSender);
			if( NULL == edit ) return;

			std::wstring strText = L"";
			strText = iniLanguageHelper.GetCommonString(COMMOM_TOOLTIP_CHARACTER_KEY).c_str();
			strText += L"(";
			strText += edit->GetLimitChar();
			strText += L")";

			rt = edit->GetPos();

			return showTip(rt, strText, FALSE);
		}
		else if( msg.sType == DUI_MSGTYPE_EDIT_RESTOR_NORMAL )
		{
			hideTip();
		}
		return WindowImplBase::Notify(msg);
	}

	void CSettingFrame::OnClick(TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();

		if ((_tcsicmp(name, _T("settingFrame_btnCancel")) == 0) ||
			(_tcsicmp(name, _T("settingFrame_btnClose")) == 0))
		{
			CloseAll();
		}
		else if ( (_tcsicmp(name, _T("settingFrame_btnOk")) == 0))
		{
			SaveAll();
		}
		else if ((name == _T("settingFrame_userInfo_btnAccountType")) || (name == _T("settingFrame_userInfo_btnDataCentre")))
			(void)ShellExecute(NULL,L"open",L"explorer.exe", m_strUserInfoURL, NULL,SW_SHOWNORMAL);	

		else if (_tcsicmp(name, _T("settingFrame_transfer_btnSelDir")) == 0)
		{
			std::wstring seldir = L"";
			if(m_pEditAutoDir)
			{
				seldir = m_pEditAutoDir->GetText();
			}
			seldir = SelectDir();
			if ( m_pEditAutoDir && !seldir.empty() )
			{
				m_pEditAutoDir->resetText(seldir.c_str());
			}
		}
	}
	
	void CSettingFrame::OnSelectChanged(TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();

		if (NULL == m_pTabLayout)
		{
			return;
		}

		if (_tcsicmp(name, _T("leftRegion_userInfo")) == 0)
		{
			m_pTabLayout->SelectItem(0);
			m_pBtnSave->SetVisible(false);
			m_pBtnCancel->SetVisible(false);
		}
		else if (_tcsicmp(name, _T("leftRegion_transfer_Setting")) == 0)
		{
			m_pTabLayout->SelectItem(1);
			m_pBtnSave->SetVisible(true);
			m_pBtnCancel->SetVisible(true);
		}
		else if (_tcsicmp(name, _T("leftRegion_advanced_Setting")) == 0)
		{
			m_pTabLayout->SelectItem(2);
			m_pBtnSave->SetVisible(true);
			m_pBtnCancel->SetVisible(true);
		}
		//使用固定保存地址
		else if (_tcsicmp(name, _T("settingFrame_transfer_usedownload")) == 0)
		{
			if( m_pCheckAutoDir )
			{
				bool bret = m_pCheckAutoDir->GetCheck()?true:false;
				if (m_pEditAutoDir)
				{
					m_pEditAutoDir->SetEnabled(bret);
				}
				if (m_pBtnOpenFileDialog)
				{
					m_pBtnOpenFileDialog->SetEnabled(bret);
				}
			}
		}
		//限制上传速度
		else if (_tcsicmp(name, _T("settingFrame_transfer_UploadPlace")) == 0)
		{		
			if ( m_pEditUploadLimitSpeed && m_pCheckUploadLimitSpeed )
			{
				bool bcheck = m_pCheckUploadLimitSpeed->GetCheck()?true:false;
				m_pEditUploadLimitSpeed->SetEnabled(bcheck);
			}
		}
		//限制下载速度
		else if (_tcsicmp(name, _T("settingFrame_transfer_DownPlace")) == 0)
		{		
			if ( m_pCheckDownloadLimitSpeed && m_pEditDownloadLimitSpeed )
			{
				bool bcheck = m_pCheckDownloadLimitSpeed->GetCheck()?true:false;
				m_pEditDownloadLimitSpeed->SetEnabled(bcheck);
			}
		}
		else if (_tcsicmp(name, _T("settingFrame_transfer_Option_Autodir")) == 0)
		{		
			if (m_pEditAutoDir && m_pCheckAutoDir )
			{
				bool bcheck = m_pCheckAutoDir->GetCheck()?true:false;
				m_pEditAutoDir->SetEnabled(bcheck);
				m_pBtnOpenFileDialog->SetEnabled(bcheck);

				if (bcheck)
				{
					m_pEditAutoDir->SetFocus();
				}
				else
				{
					CDuiString text = L"";
					m_pEditAutoDir->setDefaultTxt(text);
					m_pEditAutoDir->resetText(L"");
				}
			}
		}
		else if (_tcsicmp(name, _T("settingFrame_advanced_autologin")) == 0)
		{
			if (m_pCheckAutoLogin->IsSelected())
			{
				m_pCheckRemPwd->Selected(TRUE);
			}
		}
		else if (_tcsicmp(name, _T("settingFrame_advanced_rempsw")) == 0)
		{
			if (!m_pCheckRemPwd->IsSelected())
			{
				m_pCheckAutoLogin->Selected(FALSE);
			}
		}
	}

	void CSettingFrame::InitControl()
	{
		m_pTabLayout = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(_T("Setting_tab")));

		//基本设置
		m_pLblUserHead = static_cast<CUserPhotoUI*>(m_PaintManager.FindControl(_T("settingFrame_UserHead")));
		m_pUserInfoTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("settingFrame_userInfo_title")));
		m_pUserInfoContext = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("settingFrame_userInfo_context")));

		//传输设置
		m_pComboAsyncTaskThreadNumber = static_cast<CComboUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_combo")));
		m_pEditAutoDir = static_cast<CSearchTxtUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_edtAutodir")));
		m_pCheckAutoDir = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_Option_Autodir")));
		m_pBtnOpenFileDialog = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_btnSelDir")));
		
		m_pCheckDownloadLimitSpeed = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_DownPlace")));
		m_pEditDownloadLimitSpeed = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_edtDownSpeed")));
		m_pCheckUploadLimitSpeed = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_UploadPlace")));
		m_pEditUploadLimitSpeed = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("settingFrame_transfer_edtUploadSpeed")));
		//高级设置
		m_pCheckAutoRun = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_advanced_AutoRun")));
		m_pCheckPopupNotification = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_advanced_SystemNotification")));
		m_pResourceNotification = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_advanced_ResourceNotification")));
		m_pCheckRemPwd = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_advanced_rempsw")));
		m_pCheckAutoLogin = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("settingFrame_advanced_autologin")));

		m_pBtnSave = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("settingFrame_btnOk")));
		m_pBtnCancel = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("settingFrame_btnCancel")));
	}

	void CSettingFrame::InitBaseInfo()
	{
		if (NULL == m_pUserContext)
		{
			return;
		}

		StorageUserInfo storageUserInfo;
		ProxyMgr::getInstance(m_pUserContext)->getCurUserInfo(storageUserInfo);

		std::wstring strName = SD::Utility::String::utf8_to_wstring(storageUserInfo.name.c_str());	
		if (m_pUserInfoTitle)
		{
			m_pUserInfoTitle->SetText(strName.c_str());
		}

		std::wstring strDepart = SD::Utility::String::utf8_to_wstring(storageUserInfo.description.c_str());
		if (m_pUserInfoContext)
		{
			m_pUserInfoContext->SetText(strDepart.c_str());
			m_pUserInfoContext->SetToolTip(strDepart.c_str());
		}

		std::wstring str_photoPath = L"..\\Image\\userPhoto.png";

		if (m_pLblUserHead)
		{
			m_pLblUserHead->SetBkImage(str_photoPath.c_str());
		}
	}

	void CSettingFrame::InitTransfer()
	{
		int32_t asyntasknumber = GetUserConfValue(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, (int32_t)3);
		if (m_pComboAsyncTaskThreadNumber){
			if (asyntasknumber < 1 || asyntasknumber > 5)
				asyntasknumber = 3;

			m_pComboAsyncTaskThreadNumber->SetInternVisible(true);
			m_pComboAsyncTaskThreadNumber->SelectItem(asyntasknumber-2);
		}

		if( m_pCheckUploadLimitSpeed && m_pEditUploadLimitSpeed )
		{
			bool bIsLimitUploadSpeed = true;
			int64_t iUpSpeed = GetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, (int64_t)0)/1024;
			if (iUpSpeed < MIN_EDIT_NUMBER)
			{
				bIsLimitUploadSpeed = false;
				iUpSpeed = MIN_EDIT_NUMBER;
			}

			m_pCheckUploadLimitSpeed->SetCheck(bIsLimitUploadSpeed);

			m_pEditUploadLimitSpeed->SetNumberOnly(true);
			m_pEditUploadLimitSpeed->SetEnabled(bIsLimitUploadSpeed);

			m_pEditUploadLimitSpeed->SetText(SD::Utility::String::type_to_string<std::wstring>(iUpSpeed).c_str());
		}

		if( m_pCheckDownloadLimitSpeed && m_pEditDownloadLimitSpeed )
		{
			bool bIsLimitDownloadSpeed = true;
			int64_t iDownSpeed = GetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, (int64_t)0)/1024;
			if (iDownSpeed < MIN_EDIT_NUMBER)
			{
				bIsLimitDownloadSpeed = false;
				iDownSpeed = MIN_EDIT_NUMBER;
			}

			m_pCheckDownloadLimitSpeed->SetCheck(bIsLimitDownloadSpeed);

			m_pEditDownloadLimitSpeed->SetNumberOnly(true);
			m_pEditDownloadLimitSpeed->SetEnabled(bIsLimitDownloadSpeed);
			m_pEditDownloadLimitSpeed->SetText(SD::Utility::String::type_to_string<std::wstring>(iDownSpeed).c_str());
		}

		std::wstring defaultValue = L"";
		std::wstring strAutoDir = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_DOWNLOADPATH_KEY, defaultValue);
		bool bIsSetedDownloadAutoDir = true;
		if (strAutoDir.empty())
		{
			bIsSetedDownloadAutoDir = false;
			TCHAR szTemp[MAX_PATH];
			int nRet=GetTempPathW(MAX_PATH, szTemp);
			if (szTemp[nRet-1] != _T('\\'))
			{
				szTemp[nRet] = _T('\\');
				szTemp[nRet+1] = 0;
			}
		}

		if ( m_pEditAutoDir )
		{
			m_pCheckAutoDir->SetCheck(bIsSetedDownloadAutoDir);
			m_pEditAutoDir->SetEnabled(bIsSetedDownloadAutoDir);
			CDuiString defaultTxt = L"";
			m_pEditAutoDir->setDefaultTxt(defaultTxt);
			m_pEditAutoDir->enableInit(false);
			m_pEditAutoDir->resetText(strAutoDir.c_str());

			m_pBtnOpenFileDialog->SetEnabled(bIsSetedDownloadAutoDir);
		}
	}

	void CSettingFrame::InitAdvancedSetting()
	{
		if ( m_pCheckAutoRun )
		{
			bool bDefaultValue = false;
			bool bValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_AUTO_RUN_KEY, bDefaultValue);
			m_pCheckAutoRun->SetCheck(bValue);
		}
		if ( m_pCheckPopupNotification )
		{
			bool bDefaultValue = true;
			bool bValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, bDefaultValue);
			m_pCheckPopupNotification->SetCheck(bValue);
		}

		if ( m_pResourceNotification )
		{
			bool bDefaultValue = true;
			bool bValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, bDefaultValue);
			m_pResourceNotification->SetCheck(bValue);
		}
		if ( m_pCheckRemPwd && ((int64_t)CONF_SETTINGS::ON == GetValue(CONF_REMPASSWORD_KEY,(int64_t)0)))
		{
			m_pCheckRemPwd->SetCheck(true);
		}
		if ( m_pCheckAutoLogin && ((int64_t)CONF_SETTINGS::ON == GetValue(CONF_AUTOLOGIN_KEY,(int64_t)0)))
		{
			m_pCheckAutoLogin->SetCheck(true);
		}

	}

	void CSettingFrame::SaveAll()
	{
		if (!IsModify())
		{
			Close();
			return;
		}
		
		bool bPrompt = IsModify(true);

		if ( m_pCheckAutoDir && m_pCheckAutoDir->GetCheck() )
		{
			if( m_pEditAutoDir )
			{
				std::wstring autodir = m_pEditAutoDir->GetText().GetData();
				while ( (SD::Utility::String::rtrim(autodir,L" ").length() != autodir.length())
					|| (SD::Utility::String::rtrim(autodir,L"\\").length() != autodir.length()) )
				{
					autodir = SD::Utility::String::rtrim(autodir,L" ");
					autodir = SD::Utility::String::rtrim(autodir,L"\\");
				}

				if ( Utility::FS::is_invalidLocalPath(autodir) )
				{
					m_noticeFrame_->Run(Confirm, Warning, MSG_SETTING_SAVETITLE_KEY ,MSG_SETSYNCDIR_INVALIDPATH_KEY, Modal);
					m_pEditAutoDir->SetFocus();
					return;
				}

				if( Utility::FS::is_invalidCharPath(autodir, PATH_LIMIT_CHARS ) )
				{
					m_noticeFrame_->Run(Confirm, Warning, MSG_SETTING_SAVETITLE_KEY ,MSG_SETSYNCDIR_INVALIDCHAR_KEY, Modal);
					m_pEditAutoDir->SetFocus();
					return;
				}

				if ( !Utility::FS::is_exist(autodir) )
				{
					m_noticeFrame_->Run(Choose, Ask, MSG_SETTING_SAVETITLE_KEY ,MSG_NOTCDIR_IS_CREATE, Modal);
					if ( !m_noticeFrame_->IsClickOk() )
					{
						m_pEditAutoDir->SetFocus();
						return;
					}
					int32_t ret = Utility::FS::create_directories(autodir);
					if ( ERROR_SUCCESS != ret )
					{
						m_noticeFrame_->Run(Confirm, Warning, MSG_SETTING_SAVETITLE_KEY ,MSG_DOWLOADECDIR_CREATE_FAILED, Modal);
						m_pEditAutoDir->SetFocus();
						return;
					}
				}

				SetUserConfValue(CONF_SETTINGS_SECTION, CONF_DOWNLOADPATH_KEY, autodir.c_str() );
			}
		}
		else
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_DOWNLOADPATH_KEY, L"");
		}

		//传输设置
		if (m_pComboAsyncTaskThreadNumber)
		{
			SetUserConfValue(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_TASK_KEY
				, Utility::String::string_to_type<int32_t>(m_pComboAsyncTaskThreadNumber->GetText().GetData()) );
			SetUserConfValue(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY
				, Utility::String::string_to_type<int32_t>(m_pComboAsyncTaskThreadNumber->GetText().GetData()));
		}

		if ( m_pCheckDownloadLimitSpeed && m_pCheckUploadLimitSpeed 
			&& (m_pCheckUploadLimitSpeed->GetCheck() || m_pCheckDownloadLimitSpeed->GetCheck()) )
		{
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, 1);
		}
		else
		{
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_USE_SPEED_LIMIT_KEY, 0);
		}

		if (m_pCheckUploadLimitSpeed && m_pEditUploadLimitSpeed
			&& m_pCheckUploadLimitSpeed->GetCheck())
		{
			int64_t maxSpeedLimit = SD::Utility::String::string_to_type<int32_t>(m_pEditUploadLimitSpeed->GetText().GetData());
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, maxSpeedLimit*1024);
		}
		else
		{
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, 0);
		}

		if ( m_pCheckDownloadLimitSpeed && m_pEditDownloadLimitSpeed
			&& m_pCheckDownloadLimitSpeed->GetCheck())
		{
			int64_t maxSpeedLimit = SD::Utility::String::string_to_type<int32_t>(m_pEditDownloadLimitSpeed->GetText().GetData());
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, maxSpeedLimit*1024);
		}
		else
		{
			SetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, 0);
		}

		//高级设置
		if ( m_pCheckAutoRun && m_pCheckAutoRun->GetCheck() ){
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_AUTO_RUN_KEY, (int)UI_BALLOON::BALLOON_YES);
			(void)ShellExecute(NULL,L"open",L"OneboxStart.exe",L"/createTask",NULL,SW_SHOWNORMAL);
		}
		else
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_AUTO_RUN_KEY, (int)UI_BALLOON::BALLOON_NO);
			(void)ShellExecute(NULL,L"open",L"OneboxStart.exe",L"/cancelTask",NULL,SW_SHOWNORMAL);
		}

		if ( m_pCheckPopupNotification && m_pCheckPopupNotification->GetCheck())
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, 1);
		}
		else
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, 0);
		}

		if ( m_pResourceNotification && m_pResourceNotification->GetCheck())
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, 1);
		}
		else
		{
			SetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, 0);
		}

		if (m_pCheckRemPwd->IsSelected())
		{
			SetValue(CONF_REMPASSWORD_KEY,(int64_t)CONF_SETTINGS::ON);
		}
		else
		{
			SetValue(CONF_REMPASSWORD_KEY,(int64_t)CONF_SETTINGS::OFF);
		}

		if (m_pCheckAutoLogin->IsSelected())
		{
			SetValue(CONF_AUTOLOGIN_KEY,(int64_t)CONF_SETTINGS::ON);
		}
		else
		{
			SetValue(CONF_AUTOLOGIN_KEY,(int64_t)CONF_SETTINGS::OFF);
		}

		m_pUserContext->getConfigureMgr()->unserialize();

		if (bPrompt)
			m_noticeFrame_->Run(Confirm, Right, MSG_SETTING_SAVETITLE_KEY ,MSG_SETTING_SAVE_KEY,Modal);
		Close();
	}

	void CSettingFrame::CloseAll()
	{
		if (IsModify()){
			m_noticeFrame_->Run(Choose, Warning, MSG_WARNING_KEY ,MSG_SETTING_EXIT_KEY, Modal);
				if (!m_noticeFrame_->IsClickOk())
					return;
		}

		Close();
	}

	bool CSettingFrame::IsModify(bool bPrompt/*= false*/)
	{
		bool bRet = false;
		int32_t asyntasknumber = GetUserConfValue(CONF_TRANS_TASK_SECTION, CONF_MAX_ASYNC_TRANS_THREAD_KEY, (int32_t)1);
		if (0 != asyntasknumber && asyntasknumber !=SD::Utility::String::string_to_type<int32_t>(m_pComboAsyncTaskThreadNumber->GetText().GetData()))
		{
			bRet = true;
		}
		int64_t iUpSpeed = GetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_UPLOAD_SPEED_KEY, (int64_t)0)/1024;
		int64_t iDownSpeed = GetUserConfValue(CONF_NETWORK_SECTION, CONF_MAX_DWONLOAD_SPEED_KEY, (int64_t)0)/1024;
		bool bUpSpeed = iUpSpeed !=0 ? true:false;
		bool bDownSpeed = iDownSpeed !=0 ? true:false;
		if ((bUpSpeed !=  m_pCheckUploadLimitSpeed->GetCheck() || bDownSpeed !=  m_pCheckDownloadLimitSpeed->GetCheck()))
		{
			bRet = true;
		}
		if ((bUpSpeed && iUpSpeed != SD::Utility::String::string_to_type<int32_t>(m_pEditUploadLimitSpeed->GetText().GetData()))||
			(bDownSpeed && iDownSpeed != SD::Utility::String::string_to_type<int32_t>(m_pEditDownloadLimitSpeed->GetText().GetData())))
		{
			bRet = true;
		}

		if (bPrompt)
			return bRet;

		std::wstring defaultValue = L"";
		std::wstring strAutoDir = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_DOWNLOADPATH_KEY, defaultValue);
		bool bAutoDir = strAutoDir!=L""?true:false;
		if (bAutoDir != m_pCheckAutoDir->GetCheck() ||0 != _tcsicmp(m_pEditAutoDir->GetText().GetData(),strAutoDir.c_str()))
		{
			bRet = true;
		}

		bool bDefaultValue = false;
		bool bAutoRunValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_AUTO_RUN_KEY, bDefaultValue);
		if ( m_pCheckAutoRun->GetCheck() != bAutoRunValue )
		{
			bRet = true;
		}

		bDefaultValue = true;
		bool bBubblerValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_SYSTEM_BUBBLEREMIND_KEY, bDefaultValue);
		if ( m_pCheckPopupNotification->GetCheck() != bBubblerValue)
		{
			bRet = true;
		}

		bDefaultValue = true;
		bool bResourceValue = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_RESOURCE_BUBBLEREMIND_KEY, bDefaultValue);
		if ( m_pResourceNotification->GetCheck() != bResourceValue )
		{
			bRet = true;
		}

		bool bRemPwd = CONF_SETTINGS::OFF == GetValue(CONF_REMPASSWORD_KEY,(int64_t)0) ? false : true;
		if ( m_pCheckRemPwd->GetCheck() != bRemPwd )
		{
			bRet = true;
		}

		bool bAutoLogin = CONF_SETTINGS::OFF == GetValue(CONF_AUTOLOGIN_KEY,(int64_t)0) ? false : true;
		if ( m_pCheckAutoLogin->GetCheck() != bAutoLogin){
			bRet = true;
		}

		return bRet;
	}

	template<typename T>
	T CSettingFrame::GetValue(const std::wstring valueKey,const T& defaultValue)
	{
		T _return =  GetUserConfValue(CONF_USERINFO_SECTION,valueKey,defaultValue);
		return _return;
	}

	template<typename T>
	void CSettingFrame::SetValue(const std::wstring valueKey, const T& value)
	{
		SetUserConfValue(CONF_USERINFO_SECTION,valueKey,value);
		return;
	}
}