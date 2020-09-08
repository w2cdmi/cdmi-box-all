#include "stdafxOnebox.h"
#include "NoticeFrame.h"
#include "ControlNames.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "Utility.h"
#include "DialogBuilderCallbackImpl.h"
#include "ConfigureMgr.h"
#include <atlconv.h>

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
	const wchar_t* BTN_CANCEL = L"btn_cancel";
	const wchar_t* BTN_CHANGEPWD = L"btn_changepwd";
	const wchar_t* LAYOUT_MSG = L"layout_msg";

	DUI_BEGIN_MESSAGE_MAP(NoticeFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_END_MESSAGE_MAP()

		bool NoticeFrame::m_bIsClickOk = false;

	NoticeFrame::NoticeFrame(HWND parent,UserContext* userContext):m_parentHwnd(parent),userContext_(userContext)
	{
		m_frameType = Confirm;
		m_showType = Modal;
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
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString NoticeFrame::GetSkinFile()
	{
		return SKIN_XML_NOTICEMSG_FILE;
	}

	void NoticeFrame::OnFinalMessage( HWND hWnd )
	{
		WindowImplBase::OnFinalMessage(hWnd);
	}

	CControlUI* NoticeFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool NoticeFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
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
		else if (wParam ==  0xF032)
		{
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	LRESULT NoticeFrame::MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled)
	{
		if (uMsg == WM_KEYDOWN)
		{
			switch (wParam)
			{
			case VK_RETURN:
				{
					CButtonUI* btnok =  static_cast<CButtonUI*>(m_PaintManager.FindControl(BTN_OK));
					if (NULL ==btnok )
					{
						return FALSE;
					}
					btnok->Activate();
					btnok->SetFocus();
					return TRUE;
				}
			case VK_ESCAPE:
				{
					m_bIsClickOk = false;
					Close();
					return TRUE;
				}
			default:
				break;
			}
		}
		return FALSE;
	}

	void NoticeFrame::OnClick(DuiLib::TNotifyUI& msg)
	{
		CDuiString sender = msg.pSender->GetName();
		if (sender == BTN_CLOSE || sender ==  BTN_CANCEL)
		{
			m_bIsClickOk = false;
			Close();
		}
		else if (sender == BTN_OK)
		{
			m_bIsClickOk = true;
			Close();
		}
		else if (sender == BTN_CHANGEPWD)
		{
			if (NULL != userContext_ )
			{
				CDuiString suserUrl = userContext_->getConfigureMgr()->getConfigure()->serverUrl().c_str();
				std::wstring m_strUserUrl = suserUrl.Left(suserUrl.GetLength()-6);
				(void)ShellExecute(NULL,L"open",L"explorer.exe", m_strUserUrl.c_str(), NULL,SW_SHOWNORMAL);
			}
		}
	}

	std::wstring NoticeFrame::GetTitle(std::wstring msgCode, ...)
	{
		CInIHelper IniHelper(SD::Utility::FS::format_path(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_TITLE_SECTION,msgCode,L"");
		va_list args;
		va_start (args, msgCode);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		return buffer;
	}

	std::wstring NoticeFrame::GetMsg(std::wstring msgCode, ...)
	{
		CInIHelper IniHelper(SD::Utility::FS::format_path(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = IniHelper.GetString(MSG_DESC_SECTION,msgCode,L"");
		va_list args;
		va_start (args, msgCode);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		return buffer;
	}

	void NoticeFrame::ShowNoticeWindow(FrameType frameType,NoticeData& noticeData,ShowType showType,bool bShowLink)
	{		
		std::wstring windowTile = GetTitle(noticeData.noticeTitleCode);		
		Create(m_parentHwnd,windowTile.c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE, 0, 0, 0, 0);
		CenterWindow();
		InitData(frameType,noticeData,bShowLink);
		m_showType = showType;
		switch (showType)
		{
		case NotModal:
			ShowWindow();
			break;
		case Modal:
			ShowModal();
			break;
		default:
			break;
		}
	}

	void NoticeFrame::InitData(FrameType frameType, NoticeData noticeData,bool bShowLink)
	{
		m_frameType = frameType;
		m_noticeData = noticeData;
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
				if (noticeData.noticeTitleCode.empty())
					NoticeCaption->SetText(iniLanguageHelper.GetMsgTitle(MSG_FAILURE_KEY).c_str());
				else
					NoticeCaption->SetText(GetTitle(noticeData.noticeTitleCode).c_str());
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
			padding.top = (LayoutMsg->GetFixedHeight()-50)/2;
		}
		else if ( ilen <= 800)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-70)/2;
		}
		else if ( ilen <= 1100)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-80)/2;
			nNumber = 2;
		}
		else if ( ilen <= 1300)
		{
			padding.top = (LayoutMsg->GetFixedHeight()-95)/2;
			nNumber = 3;
		}
		else
		{
			padding.top = (LayoutMsg->GetFixedHeight()-80)/2;
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

	bool NoticeFrame::IsClickOk()
	{
		return m_bIsClickOk;
	}

	NoticeFrameMgr::NoticeFrameMgr(HWND parent,UserContext* userContext):m_parent(parent),userContext_(userContext)
	{
		m_vecFrame.clear();
	}

	NoticeFrameMgr::~NoticeFrameMgr()
	{
		for(std::vector<NoticeFrame*>::iterator it = m_vecFrame.begin(); it != m_vecFrame.end();)
		{
			delete *it;
			*it = NULL;
			it = m_vecFrame.erase(it);
		}
	}

	NoticeFrame* NoticeFrameMgr::Find(FrameType frameType,NoticeData& noticeData,ShowType showType)
	{
		NoticeFrame* _return = NULL;
		std::vector<NoticeFrame*> ::iterator itr = m_vecFrame.begin();
		while (itr !=m_vecFrame.end())
		{
			if ((*itr)->m_frameType == frameType && (*itr)->m_noticeData==noticeData &&
				(*itr)->m_showType == showType)
			{
				_return = (*itr);
				m_vecFrame.erase(itr);
				break;
			}
			itr++;
		}

		return _return;
	}

	void NoticeFrameMgr::Run(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,ShowType showType,bool bShowLink)
	{
		NoticeData noticeData;
		noticeData.noticeType=noticeType;
		noticeData.noticeTitleCode=noticeTitle;
		noticeData.noticeCode=noticeCode;
		NoticeFrame* noticeFrame = Find(frameType, noticeData, showType);
		if (NULL != noticeFrame && 	::IsWindow(noticeFrame->GetHWND()))
		{
			noticeFrame->Close();
		}
		noticeFrame = new NoticeFrame(m_parent,userContext_);
		m_vecFrame.push_back(noticeFrame);
		noticeFrame->ShowNoticeWindow( frameType, noticeData, showType,bShowLink);
	}

	bool NoticeFrameMgr::IsClickOk()
	{
		return NoticeFrame::IsClickOk();
	}
}