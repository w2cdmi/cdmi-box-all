#include "stdafxOnebox.h"
#include "DeclareFrame.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"
#include "RestClient.h"
#include "NetworkMgr.h"
#include "UserContext.h"
#include "LoginMgr.h"
#include "UserInfoMgr.h"

namespace Onebox
{
	const wchar_t* WND_DECLARE_CLS_NAME = L"DeclareFrame";
	const wchar_t* SKIN_DECLARE_FOLDER = L"skin\\Default\\";
	const wchar_t* SKIN_XML_DECLARE_XML_FILE = L"declareFrame.xml";
	const wchar_t* RICHEDIT_DECLAREINFO= L"text_declareinfo";
	const wchar_t* BTN_DECLARE_OK = L"btn_ok";
	const wchar_t* BTN_DECLARE_CLOSE = L"btn_close";
	const wchar_t* BTN_DECLARE_CANCEL = L"btn_cancel";

	const char* CLIENTTYPE = "PC";

	class DeclareFrame : public WindowImplBase, public Declare
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		DeclareFrame(HWND parent,UserContext* userContext):parent_(parent),userContext_(userContext),declarationID_("")
		{

		}
		~DeclareFrame()
		{
			
		}
	protected:
		virtual LPCTSTR GetWindowClassName(void) const
		{
			return WND_DECLARE_CLS_NAME;
		}

		virtual CDuiString GetSkinFolder()
		{
			return iniLanguageHelper.GetSkinFolderPath().c_str(); 
		}

		virtual CDuiString GetSkinFile()
		{
			return SKIN_XML_DECLARE_XML_FILE;
		}


		virtual CControlUI* CreateControl(LPCTSTR pstrClass)
		{
			return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
		}

		virtual bool InitLanguage(CControlUI* control)
		{
			return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
		}

		virtual void OnFinalMessage( HWND hWnd )
		{
			WindowImplBase::OnFinalMessage(hWnd);
			delete this;
		}

	private:
		void Show()
		{
			Create(parent_, L"OneboxDeclareFrame", UI_WNDSTYLE_FRAME, WS_EX_TOOLWINDOW, 0, 0, 0, 0);
			if (NULL == m_hWnd)
			{
				return;
			}
			DeclarationInfo declarationInfo;
			declarationInfo = userContext_->getUserInfoMgr()->getDeclarationInfo();
			declarationID_ = declarationInfo.declarationID;
			CRichEditUI*  declaretionText = static_cast<CRichEditUI*>(m_PaintManager.FindControl(RICHEDIT_DECLAREINFO) );
			if (NULL == declaretionText)
			{
				return;
			}
			declaretionText->SetText(SD::Utility::String::utf8_to_wstring(declarationInfo.declarationText).c_str());
			CenterWindow();
			ShowModal();
		}

		LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
		{
			if (wParam == SC_CLOSE)
			{
				LoginMgr::getInstance(parent_)->Logout();
				return 0;
			}
			else if (wParam ==  0xF032)
			{
				return 0;
			}

			return WindowImplBase::OnSysCommand(uMsg, wParam, lParam, bHandled);
		}


		virtual void OnClick(DuiLib::TNotifyUI& msg)
		{
			if ( m_PaintManager.FindControl(BTN_DECLARE_CLOSE)== msg.pSender
				|| m_PaintManager.FindControl(BTN_DECLARE_CANCEL) == msg.pSender)
			{
				LoginMgr::getInstance(parent_)->Logout();
			}
			else if (m_PaintManager.FindControl(BTN_DECLARE_OK) == msg.pSender)
			{
				std::string  isSign;
				MAKE_CLIENT(client);
				if(0 !=client().signDeclaration(declarationID_,isSign) || isSign == "false")
				{
					LoginMgr::getInstance(parent_)->Logout();
				}
				Close();
			}
		}

	public:
		virtual void Run()
		{
			Show();
		}

	private:
		HWND parent_;
		UserContext*  userContext_;
		std::string declarationID_;
	};

	DUI_BEGIN_MESSAGE_MAP(DeclareFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
	DUI_END_MESSAGE_MAP()

	Declare::Declare()
	{

	}

	Declare::~Declare()
	{
	}

	Declare* Declare::create(HWND parent,UserContext* userContext)
	{
		 return    static_cast<Declare*> (new DeclareFrame(parent,userContext));
	}
}