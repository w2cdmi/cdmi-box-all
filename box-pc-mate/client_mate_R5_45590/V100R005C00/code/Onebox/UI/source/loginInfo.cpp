#include "stdafxOnebox.h"
#include "loginInfo.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"
#include "UserInfo.h"
#include "UserInfoMgr.h"

#define WND_LOGININFO_CLS_NAME (L"OneboxLoginInfoFrame")
#define WND_LOGININFO_XML_NAME (L"LoginInfoFrame.xml")
#define BTN_LOGININFOFRAME_CLOSE (L"LoginInfoFrame_Close")
namespace Onebox
{
	class LoginInfoImpl : public LoginInfo, public WindowImplBase
	{
	public:
		LoginInfoImpl(UserContext* userContext):
			m_pUserContext(userContext)
		{
		}
		virtual ~LoginInfoImpl()
		{

		}
	public:
		virtual LPCTSTR GetWindowClassName(void) const
		{
			return WND_LOGININFO_CLS_NAME;
		}

		virtual CDuiString GetSkinFolder()
		{
			return iniLanguageHelper.GetSkinFolderPath().c_str(); 
		}

		virtual CDuiString GetSkinFile()
		{
			return WND_LOGININFO_XML_NAME;
		}

		virtual CControlUI* CreateControl(LPCTSTR pstrClass)
		{
			return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
		}

		virtual bool InitLanguage(CControlUI* control)
		{
			return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
		}

		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
		{
			LRESULT lRes = 0;
			BOOL bHandled = TRUE;
			switch (uMsg)
			{
			case WM_TIMER:
				{
					if(UI_TIMERID::LOGININGO_TIMER !=wParam)
					{
						bHandled = FALSE;
						break;
					}
					OnTimer(wParam);
					break;
				}
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

		void Show()
		{
			Create(NULL, L"OneboxLoginInfoFrame", UI_WNDSTYLE_DIALOG, 0);
			if (NULL == m_hWnd)
			{
				return;
			}
			int cx = GetSystemMetrics( SM_CXSCREEN ); 
			int cy = GetSystemMetrics( SM_CYSCREEN ); 
			APPBARDATA appbardata;
			(void)memset_s(&appbardata,sizeof(appbardata),0,sizeof(appbardata)); 
			appbardata.cbSize = sizeof(appbardata); 
			appbardata.hWnd = ::FindWindow(L"Shell_TrayWnd", NULL);
			::SHAppBarMessage(ABM_GETSTATE ,&appbardata);
			bool isTaskbarHide = (appbardata.lParam == ABS_AUTOHIDE)?TRUE:FALSE;
			if (!isTaskbarHide)
			{
				::SHAppBarMessage(ABM_GETTASKBARPOS ,&appbardata);
				switch (appbardata.uEdge)
				{
				case ABE_RIGHT:
					SetWindowPos(m_hWnd,HWND_TOPMOST,cx-m_PaintManager.GetInitSize().cx-(appbardata.rc.right-appbardata.rc.left),cy-m_PaintManager.GetInitSize().cy,0,0,SWP_NOSIZE);
					break;
				case ABE_BOTTOM:
					SetWindowPos(m_hWnd,HWND_TOPMOST,cx-m_PaintManager.GetInitSize().cx,cy-m_PaintManager.GetInitSize().cy-(appbardata.rc.bottom-appbardata.rc.top),0,0,SWP_NOSIZE);
					break;
				default:
					SetWindowPos(m_hWnd,HWND_TOPMOST,cx-m_PaintManager.GetInitSize().cx,cy-m_PaintManager.GetInitSize().cy,0,0,SWP_NOSIZE);
					break;
				}
			}
			else
			{
				SetWindowPos(m_hWnd,HWND_TOPMOST,cx-m_PaintManager.GetInitSize().cx,cy-m_PaintManager.GetInitSize().cy,0,0,SWP_NOSIZE);
			}

			ShowWindow(InitControlUI());
			::SetTimer(m_PaintManager.GetPaintWindow(),UI_TIMERID::LOGININGO_TIMER,5000,NULL);
		}

		virtual void OnClick(DuiLib::TNotifyUI& msg)
		{
			if ( m_PaintManager.FindControl(BTN_LOGININFOFRAME_CLOSE)== msg.pSender)
			{
				Close();
			}
		}

		void OnTimer(UINT nIDEvent)
		{
			Close();
		}

	private:
		bool InitControlUI()
		{
			CLabelUI* time = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"LoginInfoFrame_timeInfo"));
			CLabelUI* ip = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"LoginInfoFrame_ipInfo"));
			CLabelUI* clientType = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"LoginInfoFrame_clientTypeInfo"));
			if (NULL == time || NULL == ip || NULL == clientType)
			{
				return false;
			}

			LastAccessTerminal lastAccessTerminal;
			lastAccessTerminal = m_pUserContext->getUserInfoMgr()->getLastAccessTerminal();
			std::wstring wstrClientType = SD::Utility::String::utf8_to_wstring(lastAccessTerminal.deviceType);
			if (0==lastAccessTerminal.lastAccessAt)
			{
				return false;
			}
			if("pc" == lastAccessTerminal.deviceType)
			{
				wstrClientType = SD::Utility::String::to_upper(wstrClientType);
			}
			time->SetText(SD::Utility::DateTime::getTime(lastAccessTerminal.lastAccessAt,SD::Utility::Unix,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
			ip->SetText(SD::Utility::String::utf8_to_wstring(lastAccessTerminal.lastAccessIP).c_str());
			clientType->SetText(wstrClientType.c_str());
			return true;
		}
	private:
		UserContext* m_pUserContext;
	};

	std::auto_ptr<LoginInfo> LoginInfo::instance_;
	LoginInfo* LoginInfo::getInstance(UserContext* userContext)
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new LoginInfoImpl(userContext));
		}
		return instance_.get();
	}

	LoginInfo::~LoginInfo()
	{

	}
}