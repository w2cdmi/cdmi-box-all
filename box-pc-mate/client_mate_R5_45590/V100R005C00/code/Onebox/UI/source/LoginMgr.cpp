#include "stdafxOnebox.h"
#include "LoginMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "ControlNames.h"
#include "UserContextMgr.h"
#include "UserInfo.h"
#include "UserInfoMgr.h"
#include "NoticeFrame.h"
#include "InILanguage.h"
#include "Utility.h"
#include "InIHelper.h"
#include "ConfigureMgr.h"
#include "Configure.h"
#include "UserConfigure.h"
#include "UICommonDefine.h"
#include "ErrorCode.h"
#include "boost/thread.hpp"
#include "DeclareFrame.h"
#include "UpdateDB.h"
#include "resource.h"

namespace Onebox
{
	class LoginFrame : public WindowImplBase,public LoginMgr
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		LoginFrame(HWND parent);

		virtual ~LoginFrame();

		virtual void OnFinalMessage( HWND hWnd );

		void InitWindow();

		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CDuiString GetSkinFolder();

		virtual CDuiString GetSkinFile();

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		virtual LRESULT MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled);

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual void OnSetFocus(DuiLib::TNotifyUI& msg);

		virtual void OnKillFocus(DuiLib::TNotifyUI& msg);

		virtual void OnClick(DuiLib::TNotifyUI& msg);

		void OnReturnDown(DuiLib::TNotifyUI& msg);

		void OnTextChanged(DuiLib::TNotifyUI& msg);

		void OnTimer(UINT nIDEvent);

	public:
		virtual bool Login(UserContext* userContext,CPaintManagerUI& mainFramePaintManager);

		static bool LoginSuccess();

		virtual void Exit();

		void Logout();

		virtual HWND GetHwnd();

	protected:
		LRESULT ResponseDefaultKeyEvent(WPARAM wParam);

	private:
		void Btn_Login_Clicked();

		void ShowModalFrame();

		void LoginRequest(std::wstring strUserName,std::wstring strPassWord);

		void ChangePassWordControl();

		void ResetButton();

		template<typename T>
		T GetValue(const std::wstring valueKey,const T& defaultValue);

		template<typename T>
		void SetValue(const std::wstring valueKey,const T& value);

	private:
		UserContext* m_pUserContext;
		CEditUI* m_editUsername;
		CEditUI* m_editpasswordnotice;
		CEditUI* m_editPassWord;
		CButtonUI* m_btnLogin;
		CButtonUI* m_btnCancel;
		COptionUI* m_optRemPsw;
		COptionUI* m_optAutoLogin;
		HWND  parent_;
		NoticeFrameMgr* noticeFrame_;
		static bool  m_bLoginSuccess;
		boost::thread LoginRequestThread;
		boost::thread ChangePassWordControlThread;

		std::wstring m_defaultUserName;
		std::wstring m_defaultPassWord;
		bool m_bLogout;
	};

	DUI_BEGIN_MESSAGE_MAP(LoginFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_RETURN,OnReturnDown)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SETFOCUS,OnSetFocus)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS,OnKillFocus)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_TEXTCHANGED,OnTextChanged)
		DUI_END_MESSAGE_MAP()

		bool  LoginFrame::m_bLoginSuccess = false;
	LoginFrame::LoginFrame(HWND parent)
		:m_editUsername(NULL)
		,m_editpasswordnotice(NULL)
		,m_editPassWord(NULL)
		,m_optRemPsw(NULL)
		,m_optAutoLogin(NULL)
		,m_btnLogin(NULL)
		,m_btnCancel(NULL)
		,parent_(parent)
		,m_defaultUserName(L"")
		,m_defaultPassWord(L"")
		,m_bLogout(false)
	{
		//m_pUserContext = UserContextMgr::getInstance()->createUserContext((int64_t)parent_);
		noticeFrame_ = NULL;
	}

	LoginFrame::~LoginFrame()
	{
		if (noticeFrame_ != NULL)
		{
			delete noticeFrame_;
			noticeFrame_=NULL;
		}
		LoginRequestThread.interrupt();
		ChangePassWordControlThread.interrupt();
		ChangePassWordControlThread.join();
	}

	void LoginFrame::OnFinalMessage( HWND hWnd )
	{
		WindowImplBase::OnFinalMessage(hWnd);
	}

	void LoginFrame::InitWindow()
	{
		m_editUsername = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_USERNAME));
		m_editpasswordnotice = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_PASSWORDNOTICE));
		m_editPassWord = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_PASSWORD));
		m_optRemPsw = static_cast<COptionUI*>(m_PaintManager.FindControl(ControlNames::OPT_REMPSW));
		m_optAutoLogin = static_cast<COptionUI*>(m_PaintManager.FindControl(ControlNames::OPT_AUTOLOGIN));
		m_btnLogin = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_LOGIN));
		m_btnCancel = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_CANCEL));
		SetIcon(IDI_ICON1);
		std::wstring emptyStr = L"";
		m_defaultUserName = m_editUsername->GetText().GetData();
		m_defaultPassWord = m_editPassWord->GetText().GetData();
		std::wstring  userName = GetValue(CONF_USERNAME_KEY,emptyStr);
		if (0 != _tcsicmp(userName.c_str(),emptyStr.c_str()))
		{
			m_editUsername->SetTextColor(0xFF000000);
			m_editUsername->SetText(userName.c_str());
		}
		else
		{
			m_editUsername->SetTextColor(0xFF999999);
			m_editUsername->SetText(m_defaultUserName.c_str());
		}
		
		if ((int64_t)CONF_SETTINGS::ON == GetValue(CONF_REMPASSWORD_KEY,(int64_t)0))
		{
			m_editPassWord->SetText(SD::Utility::String::decrypt_string(GetValue(CONF_PASSWORD_KEY,emptyStr)).c_str());
			m_optRemPsw->Selected(true);
		}
		if ((int64_t)CONF_SETTINGS::ON == GetValue(CONF_AUTOLOGIN_KEY,(int64_t)0))
		{
			m_editPassWord->SetText(SD::Utility::String::decrypt_string(GetValue(CONF_PASSWORD_KEY,emptyStr)).c_str());
			m_optAutoLogin->Selected(true);
		}
		if ( _tcsicmp(m_editPassWord->GetText().GetData(),emptyStr.c_str()) == 0)
		{
			m_editpasswordnotice->SetVisible(true);
			m_editPassWord->SetVisible(false);
		}
		else
		{
			m_editpasswordnotice->SetVisible(false);
			m_editPassWord->SetVisible(true);
		}

		noticeFrame_ = new NoticeFrameMgr(this->GetHWND(),m_pUserContext);
	}

	LPCTSTR LoginFrame::GetWindowClassName(void) const
	{
		return ControlNames::WND_LOGIN_CLS_NAME;
	}

	DuiLib::CDuiString LoginFrame::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	DuiLib::CDuiString LoginFrame::GetSkinFile()
	{
		return ControlNames::SKIN_XML_LOGIN_FILE;
	}

	CControlUI* LoginFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool LoginFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT LoginFrame::MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled)
	{
		if (uMsg == WM_KEYDOWN)
		{
			switch (wParam)
			{
			case VK_RETURN:
			case VK_ESCAPE:
				return ResponseDefaultKeyEvent(wParam);
			default:
				break;
			}
		}
		return FALSE;
	}

	LRESULT LoginFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;

		switch (uMsg)
		{
		case WM_TIMER:
			{
				if(UI_TIMERID::LOGIN_TIMERID !=wParam)
				{
					bHandled = FALSE;
					break;
				}
				OnTimer(wParam);
				break;
			}
		case WM_SHOWWINDOW:
			{
				SD::Utility::Registry::set(HKEY_CURRENT_USER,ONEBOX_REG_PATH,HWND_REG_NAME,(DWORD)this->GetHWND()); 
			}
			break;
		case WM_CUSTOM_LOGINREQ:
			{
				int32_t iRet = (int32_t)wParam;
				if(m_pUserContext->getUserInfoMgr()->getDeclarationInfo().needsigndeclare)
				{
					Declare* declare =  Declare::create(this->GetHWND(),m_pUserContext);
					declare->Run();
				}
				if (CHANGE_PASSWORD==iRet)
				{
					noticeFrame_->Run(Confirm,Warning,MSG_LOGIN_TITLE_KEY,MSG_LOGIN_CHANGEPWD_KEY,Modal,true);
					Logout();
				}
				else if ( iRet ==0 )
				{
					if (m_bLogout)
					{
						return iRet;
					}
					SD::Utility::Registry::set(HKEY_CURRENT_USER,ONEBOX_REG_PATH,ONEBOX_REG_USERID_NAME,(DWORD)m_pUserContext->getUserInfoMgr()->getUserId()); 
					if (m_optRemPsw->IsSelected())
					{
						SetValue(CONF_REMPASSWORD_KEY,(int64_t)CONF_SETTINGS::ON);
					}
					else
					{
						SetValue(CONF_REMPASSWORD_KEY,(int64_t)CONF_SETTINGS::OFF);
					}

					if (m_optAutoLogin->IsSelected())
					{
						SetValue(CONF_AUTOLOGIN_KEY,(int64_t)CONF_SETTINGS::ON);
					}
					else
					{
						SetValue(CONF_AUTOLOGIN_KEY,(int64_t)CONF_SETTINGS::OFF);
					}
					m_bLoginSuccess = true;
					Close();
				}
				else
				{
					if (HTTP_FORBIDDEN == iRet)
					{
						noticeFrame_->Run(Confirm,Error,MSG_FAILURE_KEY,MSG_LOGIN_LOCKED_KEY);
					}
					else if (CURL_ERROR_COULDNTRESOLVHOST ==  iRet || CURL_ERROR_COULDNTCONNECT == iRet)
					{
						noticeFrame_->Run(Confirm,Error,MSG_FAILURE_KEY,MSG_LOGIN_COULDNTCONNECT__KEY);
					}
					else
					{
						noticeFrame_->Run(Confirm,Error,MSG_FAILURE_KEY,MSG_LOGIN_FAILED_KEY);
						m_editPassWord ->SetText(L"");
					}
					ResetButton();
					return iRet;
				}
			}
			break;
		case WM_CUSTOM_CHANGE_PASSWORDMODE:
			{
				if (m_editPassWord->IsVisible() && _tcsicmp(m_editPassWord->GetText(),L"") ==0 )
				{
					m_editpasswordnotice->SetVisible(true);
					m_editPassWord->SetVisible(false);
				}
				else
				{
					m_editpasswordnotice->SetVisible(false);
					m_editPassWord->SetVisible(true);
					m_editPassWord->SetFocus();
				}
			}
			break;
		default:
			bHandled = FALSE;
			break;
		}
		if (bHandled)
		{
			return lRes;
		}

		// call the parent HandleMessage
		return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
	}

	LRESULT LoginFrame::ResponseDefaultKeyEvent(WPARAM wParam)
	{
		if (wParam == VK_RETURN)
		{
			return FALSE;
		}
		else if (wParam == VK_ESCAPE)
		{
			SendMessage(WM_SYSCOMMAND, SC_MINIMIZE, 0); 
			return TRUE;
		}

		return FALSE;
	}

	LRESULT LoginFrame::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (wParam == SC_CLOSE)
		{
			Exit();
			return 0;
		}
		else if (wParam ==  0xF032)
		{
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg, wParam, lParam, bHandled);
	}

	void LoginFrame::OnTimer(UINT nIDEvent)
	{
		if (UI_TIMERID::LOGIN_TIMERID == nIDEvent)
		{
			Btn_Login_Clicked();
			KillTimer(m_PaintManager.GetPaintWindow(),UI_TIMERID::LOGIN_TIMERID);
		}
	}

	void LoginFrame::OnClick(DuiLib::TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();
		if (name == ControlNames::BTN_CLOSE || name == ControlNames::BTN_CANCEL)
		{
			Exit();
			return;
		}
		if (name == ControlNames::BTN_MIN)
		{
			SendMessage(WM_SYSCOMMAND, SC_MINIMIZE, 0); 
			return;
		}
		else if (name == ControlNames::BTN_LOGIN)
		{
			Btn_Login_Clicked();
		}
		else if (name == ControlNames::OPT_AUTOLOGIN)
		{
			if (!m_optAutoLogin->IsSelected())
			{
				m_optRemPsw->Selected(TRUE);
			}
		}
		else if (name == ControlNames::OPT_REMPSW)
		{
			if (m_optRemPsw->IsSelected())
			{
				m_optAutoLogin->Selected(FALSE);
			}
		}
	}

	void LoginFrame::OnReturnDown(DuiLib::TNotifyUI& msg)
	{
		Btn_Login_Clicked();
	}

	void LoginFrame::OnSetFocus(DuiLib::TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();
		CDuiString text = msg.pSender->GetText();
		if (_tcsicmp(name.GetData(),ControlNames::EDIT_USERNAME)==0 && _tcsicmp(text.GetData(),m_defaultUserName.c_str()) == 0 )
		{
			m_editUsername->SetText(L"");
			m_editUsername->SetTextColor(0xFF000000);
		}
		else if(_tcsicmp(name.GetData(),ControlNames::EDIT_PASSWORDNOTICE)==0)
		{
			ChangePassWordControlThread = boost::thread(boost::bind(&LoginFrame::ChangePassWordControl,this));
		}
	}

	 void LoginFrame::OnKillFocus(DuiLib::TNotifyUI& msg)
	 {
		 CDuiString name = msg.pSender->GetName();
		 CDuiString text = msg.pSender->GetText();
		 if (_tcsicmp(name.GetData(),ControlNames::EDIT_USERNAME)==0 && _tcsicmp(text.GetData(),L"") == 0 )
		 {
			 m_editUsername->SetText(m_defaultUserName.c_str());
			 m_editUsername->SetTextColor(0xFF999999);
		 }
		 if(_tcsicmp(name.GetData(),ControlNames::EDIT_PASSWORD)==0 &&  _tcsicmp(text.GetData(),L"") ==0)
		 {
			 ChangePassWordControlThread = boost::thread(boost::bind(&LoginFrame::ChangePassWordControl,this));
		 }
	 }

	void LoginFrame::Btn_Login_Clicked()
	{
		CDuiString strUserName =  m_editUsername->GetText();
		CDuiString strPassWord  = m_editPassWord->GetText();
		m_btnCancel->SetVisible(TRUE);
		m_btnLogin->SetVisible(FALSE);
		m_editUsername->SetEnabled(FALSE);
		m_editPassWord ->SetEnabled(FALSE);
		m_optRemPsw -> SetEnabled(FALSE);
		m_optAutoLogin -> SetEnabled(FALSE);
		m_btnLogin -> SetEnabled(FALSE);
		if (_tcsicmp(strUserName.GetData(),L"")==0 || _tcsicmp(strUserName.GetData(),m_defaultUserName.c_str())==0 || _tcsicmp(strPassWord.GetData(),L"")==0)
		{
			ResetButton();
			noticeFrame_->Run(Confirm,Error,MSG_FAILURE_KEY,MSG_LOGIN_NOTICEINPUT_KEY);
			return;
		}
		std::wstring wstrUserName = strUserName.GetData();
		std::wstring wstrPassWord = strPassWord.GetData();
		LoginRequestThread = boost::thread(boost::bind(&LoginFrame::LoginRequest,this,wstrUserName,wstrPassWord));
	}

	void LoginFrame::ResetButton()
	{
		m_btnLogin->SetVisible(TRUE);
		m_btnCancel->SetVisible(FALSE);
		m_editUsername->SetEnabled(TRUE);
		m_editPassWord ->SetEnabled(TRUE);
		m_optRemPsw -> SetEnabled(TRUE);
		m_optAutoLogin -> SetEnabled(TRUE);
		m_btnLogin -> SetEnabled(TRUE);
	}

	bool LoginFrame::Login(UserContext* userContext,CPaintManagerUI& mainFramePaintManager)
	{
		m_pUserContext = userContext;
		LogoutType logoutType = (LogoutType)GetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::NO);
		int32_t iRet = -1;

		if (logoutType == LogoutType::CHANGELANG)
		{
			int32_t loginType = LoginTypeDomain;
			loginType = GetValue(CONF_LOGIN_TYPE_KEY, loginType);
			if (loginType == LoginTypeDomain)
			{
				iRet = m_pUserContext->getUserInfoMgr()->domainAuthen();
			}
			else
			{
				std::wstring userName = GetValue(CONF_USERNAME_KEY, std::wstring(L""));
				std::wstring password = SD::Utility::String::decrypt_string(GetValue(CONF_PASSWORD_KEY, std::wstring(L"")));
				iRet = m_pUserContext->getUserInfoMgr()->authen(userName, password);
			}
		}
		else if (logoutType == ((int64_t)LogoutType::NO)
			|| logoutType == ((int64_t)LogoutType::RESTART))
		{
			iRet = m_pUserContext->getUserInfoMgr()->domainAuthen();
		}

		if (iRet != 0)
		{
			ShowModalFrame();
		}
		else
		{
			SD::Utility::Registry::set(HKEY_CURRENT_USER,ONEBOX_REG_PATH,ONEBOX_REG_USERID_NAME,(DWORD)m_pUserContext->getUserInfoMgr()->getUserId()); 
			m_bLoginSuccess = true;
		}

		if (m_bLoginSuccess)
		{
			if (::IsWindow(m_hWnd))
			{
				Close();
			}	
			SetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::NO);
		}

		return m_bLoginSuccess;
	}

	bool LoginFrame::LoginSuccess()
	{
		return m_bLoginSuccess;
	}

	void LoginFrame::Exit()
	{
		m_bLoginSuccess = false;
		SetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::NO);
		PostQuitMessage(0);
	}

	void LoginFrame::Logout()
	{
		m_bLogout = false;
		m_bLoginSuccess = false;
		SetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::YES);
		PostQuitMessage(0);
	}

	void LoginFrame::OnTextChanged(DuiLib::TNotifyUI& msg)
	{
		std::wstring wstrDefaultValue = L"";
		if(msg.pSender== m_editUsername)
		{
			if(_tcsicmp(m_editUsername->GetText().GetData(), GetValue(CONF_USERNAME_KEY,wstrDefaultValue).c_str()) != 0)
			{
				m_editPassWord->SetText(L"");
			}
			else if (m_optRemPsw->IsSelected())
			{
				m_editPassWord->SetText(SD::Utility::String::decrypt_string(GetValue(CONF_PASSWORD_KEY,wstrDefaultValue)).c_str());
			}
		}
	}

	void LoginFrame::LoginRequest(std::wstring strUserName,std::wstring strPassWord)
	{
		HWND hWnd = this->GetHWND();
		int32_t iRet = m_pUserContext->getUserInfoMgr()->authen(strUserName,strPassWord);
		if (::IsWindow(hWnd))
		{
			::PostMessage(hWnd,WM_CUSTOM_LOGINREQ,iRet,0);
		}
	}

	void LoginFrame::ChangePassWordControl()
	{
		PostMessage(WM_CUSTOM_CHANGE_PASSWORDMODE);
	}

	void LoginFrame::ShowModalFrame()
	{
		LogoutType logoutType = (LogoutType)GetUserConfValue(CONF_USERINFO_SECTION,CONF_LOGOUT_KEY,(int64_t)LogoutType::NO);
		Create(NULL,iniLanguageHelper.GetWindowName(WINDOWNAME_LOGIN_KEY).c_str(), UI_WNDSTYLE_DIALOG, WS_EX_STATICEDGE | WS_EX_APPWINDOW, 0, 0, 0, 0);
		CenterWindow();
		if (m_optAutoLogin->IsSelected() && (logoutType == LogoutType::NO || logoutType == LogoutType::RESTART))
		{
			::SetTimer(m_PaintManager.GetPaintWindow(),UI_TIMERID::LOGIN_TIMERID,500,NULL); 
		}
		SetForegroundWindow(this->GetHwnd());
		ShowModal();
	}

	HWND LoginFrame::GetHwnd()
	{
		return m_hWnd;
	}

	template<typename T>
	T LoginFrame::GetValue(const std::wstring valueKey,const T& defaultValue)
	{
		T _return =  GetUserConfValue(CONF_USERINFO_SECTION,valueKey,defaultValue);
		return _return;
	}

	template<typename T>
	void LoginFrame::SetValue(const std::wstring valueKey, const T& value)
	{
		SetUserConfValue(CONF_USERINFO_SECTION,valueKey,value);
		return;
	}

	std::auto_ptr<LoginMgr> LoginMgr::instance_(NULL);

	LoginMgr* LoginMgr::getInstance(HWND parent)
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new LoginFrame(parent));
		}
		return instance_.get();
	}

	bool LoginMgr::LoginSuccess()
	{
		return LoginFrame::LoginSuccess();
	}
}