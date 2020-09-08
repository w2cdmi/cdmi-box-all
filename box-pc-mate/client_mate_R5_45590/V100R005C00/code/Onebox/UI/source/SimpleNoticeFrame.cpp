#include "SimpleNoticeFrame.h"
#include "ControlNames.h"
#include "Utility.h"
#include "CommonDefine.h"
#include "InIHelper.h"
#include "InILanguage.h"

namespace Onebox
{
	const wchar_t* TEXT_SIMPLENOTICEMSG = L"text_simpleNoticeMsg";
	const wchar_t* HORLAYOUT_MAINFRAME_NOTICEAREA = L"mianFrame_NoticeArea";

	SimpleNoticeFrame::SimpleNoticeFrame(CPaintManagerUI& parent):paintManager_(parent)
	{
		m_areaWidth = 720;
	}

	SimpleNoticeFrame::~SimpleNoticeFrame()
	{
	}

	void SimpleNoticeFrame::Show(NoticeType noticeType,std::wstring msgkey,...)
	{
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = GetShowMsg(msgkey);
		va_list args;
		va_start (args, msgkey);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		ShowMessage(buffer, noticeType);
	}

	int SimpleNoticeFrame::getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText)
	{
		HDC hDC = paintManager_.GetPaintDC();
		TEXTMETRIC* pTm = &paintManager_.GetDefaultFontInfo()->tm;
		TFontInfo* pFontInfo = paintManager_.GetFontInfo(12);	//×ÖÌå´óÐ¡
		::SelectObject(hDC, pFontInfo->hFont);
		pTm = &pFontInfo->tm;
		int cchChars = 0;
		int cchSize = 0;
		LPCTSTR pstrNext;
		SIZE szText = { 0 };
		LPCTSTR pstrText = str_fileName.c_str();
		LPCTSTR p = pstrText;
		int width = 0;
		std::wstringstream tempShowText;
		while( *p != _T('\0') && *p != _T('\n') ) {
			pstrNext = ::CharNext(p);
			cchChars++;
			cchSize += (int)(pstrNext - p);
			szText.cx = cchChars * pTm->tmMaxCharWidth;
			::GetTextExtentPoint32(hDC, pstrText, cchSize, &szText);
			if(szText.cx > m_areaWidth  - 52)
			{
				showText << L"...";
				return width + 12;
			}
			if(szText.cx<=  m_areaWidth - 52 )
			{
				showText << *p;
				width = szText.cx;
			}
			else
			{
				tempShowText << *p;
			}
			p = ::CharNext(p);
		}
		showText << tempShowText.str();
		return szText.cx  + 28;
	}

	void SimpleNoticeFrame::ShowMessage(const std::wstring& msg, NoticeType noticeType)
	{
		CTextUI* NoticeMsg = static_cast<CTextUI*> (paintManager_.FindControl(TEXT_SIMPLENOTICEMSG));
		CHorizontalLayoutUI * ShowArea = static_cast<CHorizontalLayoutUI*> (paintManager_.FindControl(HORLAYOUT_MAINFRAME_NOTICEAREA));
		
		if (NULL == NoticeMsg || NULL == ShowArea) return;

		if (Error == noticeType)
		{
			ShowArea->SetBkColor(0xFFFCDDD7);
			NoticeMsg->SetTextColor(0xFFCC4400);
		}
		else if (Warning == noticeType)
		{
			ShowArea->SetBkColor(0xfffed62f);
		}
		else
		{
			ShowArea->SetBkColor(0xFFDAF0CC);
			NoticeMsg->SetTextColor(0xFF276600);
		}
		std::wstringstream showText;
		showText << L"{f 12}";
		int32_t msgWidth  = getNameWidth(msg,showText);
		showText << L"{/f}";
		NoticeMsg->SetText(showText.str().c_str());
		NoticeMsg->SetToolTip(msg.c_str());
		ShowArea->SetVisible(true);
		NoticeMsg->SetShowHtml();
		NoticeMsg->SetFixedWidth(msgWidth);
		::SetTimer(paintManager_.GetPaintWindow(),UI_TIMERID::SIMPLENOTICE_TIMERID,5000,NULL);
	}

	void SimpleNoticeFrame::SetPos()
	{
		CTextUI* NoticeMsg = static_cast<CTextUI*> (paintManager_.FindControl(TEXT_SIMPLENOTICEMSG));
		CHorizontalLayoutUI * ShowArea = static_cast<CHorizontalLayoutUI*> (paintManager_.FindControl(HORLAYOUT_MAINFRAME_NOTICEAREA));
		CVerticalLayoutUI * LeftRegionArea = static_cast<CVerticalLayoutUI*> (paintManager_.FindControl(L"leftRegion_verticalLayout"));
		CHorizontalLayoutUI * Header = static_cast<CHorizontalLayoutUI*> (paintManager_.FindControl(L"mainframe_header"));

		PWINDOWINFO WindowInfo = new WINDOWINFO();
		if(NULL == WindowInfo) return;
		WindowInfo->cbSize = sizeof(WINDOWINFO);
		::GetWindowInfo(paintManager_.GetPaintWindow(),WindowInfo);
		LONG WindowWidth = WindowInfo->rcWindow.right- WindowInfo->rcWindow.left;
		LONG WindowHeight = WindowInfo->rcWindow.bottom- WindowInfo->rcWindow.top;
		ShowArea->SetFixedWidth(WindowWidth- LeftRegionArea->GetFixedWidth() -1);
		//LONG NoticeMsgWidth = WindowWidth- LeftRegionArea->GetFixedWidth()-25;
		NoticeMsg->SetFixedWidth(WindowWidth- LeftRegionArea->GetFixedWidth()-40);
		NoticeMsg->SetMinWidth(WindowWidth- LeftRegionArea->GetFixedWidth()-40);
		m_areaWidth = WindowWidth- LeftRegionArea->GetFixedWidth();
		std::wstring wstrShowAreaPos = L"0," + SD::Utility::String::type_to_string<std::wstring,LONG>(WindowHeight - Header->GetFixedHeight() - ShowArea->GetFixedHeight())  
			+  L"," + SD::Utility::String::type_to_string<std::wstring,LONG>(WindowWidth - LeftRegionArea->GetFixedWidth() -1) + L"," 
			+ SD::Utility::String::type_to_string<std::wstring,LONG>(WindowHeight - Header->GetFixedHeight());
		ShowArea->SetAttribute(L"pos",wstrShowAreaPos.c_str());
		
		delete WindowInfo;
		WindowInfo = NULL;
	}

	void SimpleNoticeFrame::RestoreNoticeArea()
	{
		CHorizontalLayoutUI * ShowArea = static_cast<CHorizontalLayoutUI*> (paintManager_.FindControl(HORLAYOUT_MAINFRAME_NOTICEAREA));
		ShowArea->SetVisible(false);
	}

	std::wstring SimpleNoticeFrame::GetShowMsg(std::wstring msgKey)
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path(iniLanguageHelper.GetMsgLanguageFilePath().c_str()));
		std::wstring _return =  InIHelper.GetString(MSG_DESC_SECTION,msgKey,L"");
		return _return;
	}

	std::wstring SimpleNoticeFrame::GetMsg(std::wstring msgKey,...)
	{
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		std::wstring strTmp = GetShowMsg(msgKey);
		va_list args;
		va_start (args, msgKey);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		return buffer;
	}
}