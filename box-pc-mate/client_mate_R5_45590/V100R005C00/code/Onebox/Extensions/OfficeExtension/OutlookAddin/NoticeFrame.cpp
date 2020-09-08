#include "NoticeFrame.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "Utility.h"
#include "Global.h"

namespace Onebox
{
	const wchar_t* WND_NOTICEMSG_NAME = L"OneboxNoticeMsgFrame";
	const wchar_t* SKIN_NOTICEMSG_FOLDER = L"skin\\Default\\";
	const wchar_t* SKIN_XML_NOTICEMSG_FILE = L"NoticeMsgDailog.xml";
	const wchar_t* CTRL_BLANKSPACE = L"ctrl_blankspace";
	const wchar_t* TEXT_NOTICEMSG = L"text_noticemsg";
	const wchar_t* LABLE_CAPTION = L"label_caption";
	const wchar_t* BTN_NOTICETYPE = L"btn_noticetype";
	const wchar_t* BTN_OK = L"btn_ok";
	const wchar_t* BTN_CLOSE = L"btn_close";
	const wchar_t* BTN_RETURN = L"btn_return";	
	const wchar_t* BTN_CANCEL = L"btn_cancel";
	const wchar_t* BTN_CHANGEPWD = L"btn_changepwd";
	const wchar_t* LAYOUT_MSG = L"layout_msg";

	class NoticeFrame : public WindowImplBase,public NoticeImpl
	{
		DUI_DECLARE_MESSAGE_MAP() 

	public:
		NoticeFrame (HWND parent);
		~NoticeFrame();

		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CDuiString GetSkinFolder();

		virtual CDuiString GetSkinFile();

		virtual void OnFinalMessage( HWND hWnd );

		virtual bool InitLanguage(CControlUI* control);

		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		virtual void OnClick(DuiLib::TNotifyUI& msg);

	public:
		void showWindow(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...);

		void showModal(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...);

		static bool IsClickOk();

	private:
		std::wstring GetTitle(std::wstring MsgCode);

		std::wstring GetMsg(std::wstring MsgCode, ...);

		void InitData(FrameType frameType, NoticeData noticeData,bool bShowLink=false);

		virtual void RedrawWindow(int nNumber,CVerticalLayoutUI* LayoutMsg);

		virtual void SetMessageLayout(int32_t ilen,CVerticalLayoutUI* LayoutMs,CRichEditUI* NoticeMsg);
	private:
		static bool m_bIsClickOk;
		HWND m_parentHwnd;
	};

	DUI_BEGIN_MESSAGE_MAP(NoticeFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_END_MESSAGE_MAP()

		bool NoticeFrame::m_bIsClickOk = false;

	NoticeFrame::NoticeFrame(HWND parent)
		:m_parentHwnd(parent)
	{
	}

	NoticeFrame::~NoticeFrame()
	{
	}

	LPCTSTR NoticeFrame::GetWindowClassName(void) const
	{
		return WND_NOTICEMSG_NAME;
	}

	CDuiString NoticeFrame::GetSkinFolder()
	{
		return  (GetInstallPath()+IniLanguageHelper(languageID_).GetSkinFolderPath()).c_str();
	}

	CDuiString NoticeFrame::GetSkinFile()
	{
		return SKIN_XML_NOTICEMSG_FILE;
	}

	void NoticeFrame::OnFinalMessage( HWND hWnd )
	{
		WindowImplBase::OnFinalMessage(hWnd);
		//		delete this;
	}

	bool NoticeFrame::InitLanguage(CControlUI* control)
	{
		return true;
	}

	LRESULT NoticeFrame::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient={0,0,0,0};
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_PaintManager.GetCaptionRect();

		if (-1 == rcCaption.bottom)
		{
			rcCaption.bottom = rcClient.bottom;
		}

		if ( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) 
		{
			CControlUI* pControl = m_PaintManager.FindControl(pt);
			if (NULL != pControl)
			{
				CDuiString clsName;
				std::vector<CDuiString> clsNames;
				clsName = pControl->GetClass();
				clsName.MakeLower();
				clsNames.push_back(_T("controlui"));
				clsNames.push_back(_T("textui"));
				clsNames.push_back(_T("labelui"));
				clsNames.push_back(_T("containerui"));
				clsNames.push_back(_T("horizontallayoutui"));
				clsNames.push_back(_T("verticallayoutui"));
				clsNames.push_back(_T("tablayoutui"));
				clsNames.push_back(_T("childlayoutui"));
				clsNames.push_back(_T("dialoglayoutui"));

				std::vector<CDuiString>::iterator it = std::find(clsNames.begin(), clsNames.end(),clsName);
				if (clsNames.end() != it)
				{
					CControlUI* pParent = pControl->GetParent();
					while (pParent)
					{
						clsName = pParent->GetClass();
						clsName.MakeLower();
						it = std::find(clsNames.begin(),clsNames.end(),clsName);
						if (clsNames.end() == it)
						{
							return HTCLIENT;
						}
						pParent = pParent->GetParent();
					}
					return HTCAPTION;
				}				
			}
		}
		return HTCLIENT;
	}

	LRESULT NoticeFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		if (uMsg == WM_NCLBUTTONDBLCLK)
			return 0;

		return __super::HandleMessage(uMsg, wParam, lParam);
	}

	LRESULT NoticeFrame::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (wParam == SC_CLOSE)
		{
			m_bIsClickOk = false;
			Close();
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	void NoticeFrame::OnClick(DuiLib::TNotifyUI& msg)
	{
		CDuiString sender = msg.pSender->GetName();
		if (sender == BTN_CLOSE || sender ==  BTN_CANCEL)
		{
			m_bIsClickOk = false;
			Close();
		}
		else if (sender == BTN_OK || sender == BTN_RETURN)
		{
			m_bIsClickOk = true;
			Close();
		}
	}

	std::wstring NoticeFrame::GetTitle(std::wstring msgCode)
	{
		CInIHelper IniHelper(SD::Utility::String::format_string(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_TITLE_SECTION,msgCode,L"");
		if (0==_tcsicmp(strTmp.c_str(),L""))
		{
			return msgCode;
		}
		return strTmp;
	}

	std::wstring NoticeFrame::GetMsg(std::wstring msgCode, ...)
	{
		CInIHelper IniHelper(SD::Utility::String::format_string(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_DESC_SECTION,msgCode,L"");
		if (L"" == strTmp)
		{
			return msgCode;
		}
		va_list args;
		va_start (args, msgCode);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		return buffer;
	}

	void NoticeFrame::InitData(FrameType frameType, NoticeData noticeData,bool bShowLink)
	{
		m_frameType		= frameType;
		m_noticeData	= noticeData;
		CLabelUI* NoticeCaption = static_cast<CLabelUI*>(m_PaintManager.FindControl(LABLE_CAPTION));
		if (L"" != noticeData.noticeTitleCode)
		{
			if (NULL ==NoticeCaption )
			{
				return;
			}
			NoticeCaption->SetText(GetTitle(noticeData.noticeTitleCode).c_str());
		}

		CRichEditUI* NoticeMsg = static_cast<CRichEditUI*>(m_PaintManager.FindControl(TEXT_NOTICEMSG));
		CVerticalLayoutUI* LayoutMsg = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(LAYOUT_MSG));
		if (NULL==NoticeMsg || NULL == LayoutMsg)
		{
			return;
		}
		if (bShowLink)
		{
			CButtonUI* BtnChangePwd = static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_CHANGEPWD));
			if (NULL == BtnChangePwd)
			{
				return;
			}
			LayoutMsg->SetFixedHeight(80);
			BtnChangePwd->SetVisible(true);
		}
		std::wstring txtMsg = GetMsg(noticeData.noticeCode);
		int32_t ilen = NoticeMsg->GetTextWidth(txtMsg.c_str());
		SetMessageLayout(ilen,LayoutMsg,NoticeMsg);
	
		NoticeMsg->SetText(GetMsg(noticeData.noticeCode).c_str());

		if (Confirm == frameType)
		{
			CControlUI*  blackSpace =  static_cast<CControlUI*>(m_PaintManager.FindControl(CTRL_BLANKSPACE));
			CButtonUI* btnCancel =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_CANCEL));
			if (NULL == blackSpace|| NULL == btnCancel)
			{
				return;
			}
			btnCancel->SetVisible(false);
			blackSpace->SetFixedWidth(296);
		}	

		CButtonUI* noticeType =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_NOTICETYPE));
		if (NULL == noticeType)
		{
			return;
		}
		switch(noticeData.noticeType)
		{
		case  Right:
			{
				NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_SUCESS_KEY).c_str());
				noticeType->SetBkImage(RIGHTICO);
			}
			break;
		case Info:
			{
				if (noticeData.noticeTitleCode.empty())
				{
					NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_MESSAGE_KEY).c_str());
				}
				else
				{
					NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(noticeData.noticeTitleCode).c_str());
				}
				noticeType->SetBkImage(INFOICO);
			}
			break;
		case Ask:
			{
				NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_SETTING_CONFIRM_KEY).c_str());
				noticeType->SetBkImage(ASKICO);
			}
			break;
		case Error:
			{
				NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_FAILURE_KEY).c_str());
				noticeType->SetBkImage(ERRORICO);
			}
			break;
		case Warning:
			{
				NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_WARNING_KEY).c_str());
				noticeType->SetBkImage(WARNINGICO);
			}
			break;
		}
	}

	void NoticeFrame::SetMessageLayout(int32_t ilen,CVerticalLayoutUI* LayoutMsg,CRichEditUI* NoticeMsg)
	{
		RECT padding;
		padding.left = 0;
		padding.top = 0;
		padding.right = 0;
		padding.bottom = 0;

		int nNumber = 0;

		if ( ilen <= 400 )
		{
			padding.top = (LayoutMsg->GetFixedHeight()-70)/2;
		}
		else if ( ilen <= 800)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-75)/2;
		}
		else if ( ilen <= 1100)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-90)/2;
			nNumber = 2;
		}
		else if ( ilen <= 1300)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-95)/2;
			nNumber = 3;
		}
		else
		{
			padding.top = (LayoutMsg->GetFixedHeight()-90)/2;
			nNumber = 4;
		}

		NoticeMsg->SetInset(padding);
		RedrawWindow(nNumber,LayoutMsg);
	}


	void NoticeFrame::RedrawWindow(int nNumber,CVerticalLayoutUI* LayoutMsg)
	{
		RECT rc;
		::GetWindowRect(GetHWND(),&rc);	
		int height = rc.bottom - rc.top;

		if (height>=NOTICE_FRAME_WINDOW_MAX_HIGHT)
			height += (NOTICE_FRAME_CHANGE_DISTANCE * nNumber);

		LayoutMsg->SetFixedHeight(100 + 10*nNumber);

		int width = rc.right - rc.left;
		rc.bottom += (NOTICE_FRAME_CHANGE_DISTANCE * nNumber);
		::MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
		::InvalidateRect(*this,&rc,true);
	}


	void NoticeFrame::showWindow(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...)
	{
		std::wstring windowTile = GetTitle(noticeTitle);
		if(_tcsicmp(windowTile.c_str(),L"") == 0)
		{
			windowTile = iniLanguageHelper.GetWindowName(NOTICEMSGNAME_LOGIN_KEY);
		}		
		Create(m_parentHwnd,windowTile.c_str(), UI_WNDSTYLE_FRAME, WS_EX_TOOLWINDOW, 0, 0, 0, 0);
		CenterWindow();
		NoticeData noticeData;
		noticeData.noticeType=noticeType;
		noticeData.noticeTitleCode=noticeTitle;
		noticeData.noticeCode=noticeCode;
		InitData(frameType,noticeData,0);
		if (L"" != noticeTitle)
		{
			CLabelUI* NoticeCaption = static_cast<CLabelUI*>(m_PaintManager.FindControl(LABLE_CAPTION));
			NoticeCaption->SetText(GetTitle(noticeTitle).c_str());
		}

		CTextUI* NoticeMsg = static_cast<CTextUI*>(m_PaintManager.FindControl(TEXT_NOTICEMSG));

		CInIHelper IniHelper(SD::Utility::String::format_string(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_DESC_SECTION,noticeCode,L"");
		if (L"" == strTmp)
		{
			NoticeMsg->SetText(noticeCode.c_str());
		}
		else
		{
			va_list args;
			va_start (args, noticeCode);
			(void)_vstprintf_s(buffer, strTmp.c_str(), args);
			va_end (args);
			NoticeMsg->SetText(buffer);
		}

		if (Confirm == frameType)
		{
			CControlUI*  blackSpace =  static_cast<CControlUI*>(m_PaintManager.FindControl(CTRL_BLANKSPACE));
			CButtonUI* btnCancel =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_CANCEL));
			btnCancel->SetVisible(false);
			blackSpace->SetFixedWidth(296);
		}

		CButtonUI* btnNoticeType =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_NOTICETYPE));
		switch(noticeType)
		{
		case  Right:
			btnNoticeType->SetBkImage(RIGHTICO);
			break;
		case Info:
			btnNoticeType->SetBkImage(INFOICO);
			break;
		case Ask:
			btnNoticeType->SetBkImage(ASKICO);
			break;
		case Error:
			btnNoticeType->SetBkImage(ERRORICO);
			break;
		case Warning:
			btnNoticeType->SetBkImage(WARNINGICO);
			break;
		}
		ShowWindow();
	}

	void NoticeFrame::showModal(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...)
	{
		std::wstring windowTile = GetTitle(noticeTitle);
		if(_tcsicmp(windowTile.c_str(),L"") == 0)
		{
			windowTile = iniLanguageHelper.GetWindowName(NOTICEMSGNAME_LOGIN_KEY);
		}		
		Create(m_parentHwnd,windowTile.c_str(), UI_WNDSTYLE_FRAME, WS_EX_TOOLWINDOW, 0, 0, 0, 0);
		CenterWindow();
		NoticeData noticeData;
		noticeData.noticeType=noticeType;
		noticeData.noticeTitleCode=noticeTitle;
		noticeData.noticeCode=noticeCode;
		InitData(frameType,noticeData,0);
		if (L"" != noticeTitle)
		{
			CLabelUI* NoticeCaption = static_cast<CLabelUI*>(m_PaintManager.FindControl(LABLE_CAPTION));
			NoticeCaption->SetText(GetTitle(noticeTitle).c_str());
		}

		CTextUI* NoticeMsg = static_cast<CTextUI*>(m_PaintManager.FindControl(TEXT_NOTICEMSG));

		CInIHelper IniHelper(SD::Utility::String::format_string(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_DESC_SECTION,noticeCode,L"");
		if (L"" == strTmp)
		{
			NoticeMsg->SetText(noticeCode.c_str());
		}
		else
		{
			va_list args;
			va_start (args, noticeCode);
			(void)_vstprintf_s(buffer, strTmp.c_str(), args);
			va_end (args);
			NoticeMsg->SetText(buffer);
		}

		if (Confirm == frameType)
		{
			CControlUI*  blackSpace =  static_cast<CControlUI*>(m_PaintManager.FindControl(CTRL_BLANKSPACE));
			CButtonUI* btnCancel =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_CANCEL));
			btnCancel->SetVisible(false);
			blackSpace->SetFixedWidth(296);
		}

		CButtonUI* btnNoticeType =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_NOTICETYPE));
		switch(noticeType)
		{
		case  Right:
			btnNoticeType->SetBkImage(RIGHTICO);
			break;
		case Info:
			btnNoticeType->SetBkImage(INFOICO);
			break;
		case Ask:
			btnNoticeType->SetBkImage(ASKICO);
			break;
		case Error:
			btnNoticeType->SetBkImage(ERRORICO);
			break;
		case Warning:
			btnNoticeType->SetBkImage(WARNINGICO);
			break;
		}
		ShowModal();
	}

	bool NoticeFrame::IsClickOk()
	{
		return m_bIsClickOk;
	}

	std::auto_ptr<NoticeImpl> NoticeImpl::instance_;

	NoticeImpl* NoticeImpl::getInstance(HWND parent)
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new NoticeFrame(parent));
		}
		return instance_.get();
	}

	bool NoticeImplIsClickOk()
	{
		return NoticeFrame::IsClickOk();
	}
}
