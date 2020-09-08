#include "stdafxOnebox.h"
#include "ShareLinkCountDialog.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"
#include "UserContextMgr.h"
#include "ShareResMgr.h"
#include "UIScaleIconButton.h"
#include "ConfigureMgr.h"
#include "ShareLinkContextDialog.h"
#include "ShareFrameV2.h"
#include "SendShareLink.h"
#include "ProxyMgr.h"
#include "PathMgr.h"

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(ShareLinkCountDialog, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
	DUI_END_MESSAGE_MAP()


	ShareLinkCountDialog::ShareLinkCountDialog(LPCTSTR strXML, UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager)
		: m_strXML(strXML)
		, m_pUserContext(context)
		, m_nodeData(nodeData)
		, m_paintMgr(paintManager)
	{
		linkCode_ = L"";
		m_pNoticeFrame = NULL;
		m_bInit = FALSE;
		m_bShowShare = true;
		m_strLink1 = L"";
		m_strLink2 = L"";
		m_strLink3 = L"";
	}


	ShareLinkCountDialog::~ShareLinkCountDialog(void)
	{
		Path filePath = m_pUserContext->getPathMgr()->makePath();
		filePath.id(m_nodeData.basic.id);
		filePath.type((m_nodeData.basic.type==0)?FILE_TYPE_DIR:FILE_TYPE_FILE);
		ProxyMgr::getInstance(m_pUserContext)->flushFileInfo(m_pUserContext, filePath);
		SAFE_DELETE_POINTER(m_pNoticeFrame);
	}

	CDuiString ShareLinkCountDialog::GetSkinFolder() 
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString ShareLinkCountDialog::GetSkinFile() 
	{
		return m_strXML;
	}

	LPCTSTR ShareLinkCountDialog::GetWindowClassName(void) const 
	{
		return _T("CreateLinkDialogClass");
	}

	CControlUI* ShareLinkCountDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	void ShareLinkCountDialog::OnFinalMessage( HWND hWnd )
	{
		__super::OnFinalMessage(hWnd);
	}

	LRESULT ShareLinkCountDialog::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (0xF032 == wParam)
			return 0;

		return WindowImplBase::OnSysCommand(uMsg, wParam, lParam, bHandled);
	}

	LRESULT ShareLinkCountDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient={0,0,0,0};
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_PaintManager.GetCaptionRect();
		if( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right \
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) {
				CControlUI* pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(pt));
				if( pControl && _tcsicmp(pControl->GetClass(), _T("ButtonUI")) != 0 && 
					_tcsicmp(pControl->GetClass(), _T("OptionUI")) != 0 &&
					_tcsicmp(pControl->GetClass(), _T("TextUI")) != 0 &&
					_tcsicmp(pControl->GetClass(), _T("ButtonUI")) != 0 ) 
					return HTCAPTION;
		}

		return HTCLIENT;
	}

	void ShareLinkCountDialog::InitWindow()
	{
		if (!m_bShowShare){
			CButtonUI* pBtnShare = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_BtnShare")));
			if (pBtnShare)
				pBtnShare->SetVisible(false);
		}
		if (NULL == m_pNoticeFrame)
			m_pNoticeFrame = new NoticeFrameMgr(m_hWnd);

		int32_t nRet = RT_ERROR;

		if (!m_bInit){
			CLabelUI* pTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"CreateLinkDialog_TitleName"));
			if(NULL == pTitle)			return;
			std::wstring defaultText = pTitle->GetText();
			std::wstring tmpFileName =  defaultText + L" “" + m_nodeData.basic.name + L"”";
			pTitle->SetText(tmpFileName.c_str());
			pTitle->SetToolTip(tmpFileName.c_str());

			m_bInit = TRUE;
		}
		

		CVerticalLayoutUI* pLayout = NULL;
		pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout1")));
		if (pLayout)
			pLayout->SetVisible(false);
		pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout2")));
		if (pLayout)
			pLayout->SetVisible(false);
		pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout3")));
		if (pLayout)
			pLayout->SetVisible(false);

		int64_t count = 0;
		ShareLinkNodeList shareLinkNodes;
		nRet = m_pUserContext->getShareResMgr()->listShareLinkByFile(m_nodeData.basic.id, count, shareLinkNodes);
		if(nRet != RT_OK || 0 == shareLinkNodes.size()){
			ShowWindow(false);
			int nRet = ShareLinkContextDialog::CreateDlg(m_paintMgr.GetPaintWindow(), m_pUserContext, m_nodeData, L"");
			if (10 == nRet){
				InitWindow();
				ShowWindow(true);
			}else{
				Close();
			}
			return;
		}

		CButtonUI* pBtnCreate = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_BtnCreate")));
		if (pBtnCreate){
			if (3 == shareLinkNodes.size()){
				pBtnCreate->SetVisible(false);
			}else
				pBtnCreate->SetVisible(true);
		}
		
		std::sort(shareLinkNodes.begin(), shareLinkNodes.end(), &Onebox::ShareLinkCountDialog::SortShareLinkNode);

		int nFlag = 0;
		for(ShareLinkNodeList::iterator it = shareLinkNodes.begin(); it != shareLinkNodes.end(); ++it)
		{
			SetLayout(it, nFlag);
			nFlag++;
		}
	}

	void ShareLinkCountDialog::OnClick(TNotifyUI& msg)
	{
		CDuiString strName = msg.pSender->GetName();

		if (0 == _tcsicmp(strName, _T("closebtn")))
			Close();
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnShare"))){
			ShowWindow(false);

			Onebox::ShareFrame* myShareFrame =  new Onebox::ShareFrame(m_pUserContext, m_nodeData, m_paintMgr);
			myShareFrame->ShowSharedFrame();
			delete myShareFrame;
			myShareFrame = NULL;

			Close();
			return;
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_BtnCreate")))
			OnCreateLinkClick();
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_BtnCancel")))
			OnCancelLinkClick();

		
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEdit1")))
			OnEditUrlClick(0);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEdit2")))
			OnEditUrlClick(1);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEdit3")))
			OnEditUrlClick(2);

		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnCopy1")))
			OnCopyUrlClick(0);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnCopy2")))
			OnCopyUrlClick(1);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnCopy3")))
			OnCopyUrlClick(2);

		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEmail1")))
			OnEmailUrlClick(0);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEmail2")))
			OnEmailUrlClick(1);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnEmail3")))
			OnEmailUrlClick(2);

		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnDelete1")))
			OnDeleteUrlClick(0);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnDelete2")))
			OnDeleteUrlClick(1);
		else if (0 == _tcsicmp(strName, _T("CreateLinkDialog_BtnDelete3")))
			OnDeleteUrlClick(2);
		
		return __super::OnClick(msg);
	}

	void ShareLinkCountDialog::SetLayout(ShareLinkNodeList::iterator iter, int nType)
	{

		std::string url = SD::Utility::String::wstring_to_utf8(m_pUserContext->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		iter->url(url.substr(0, pos) + "/p/" + iter->id());

		std::wstring linkUrl = SD::Utility::String::utf8_to_wstring(iter->url());
		std::wstring code = SD::Utility::String::utf8_to_wstring(iter->plainAccessCode());
		std::wstring datetimeFirst = SD::Utility::DateTime::getTime(iter->effectiveAt(), SD::Utility::UtcType::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage());
		std::wstring datetimeSecond = SD::Utility::DateTime::getTime(iter->expireAt(), SD::Utility::UtcType::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage());


		CVerticalLayoutUI* pLayout = NULL;
		CLabelUI* pStatus = NULL;
		CEditUI* pEditUrl = NULL;
		switch (nType)	{
		case 0:
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout1")));
			pStatus = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_TextStatus1")));
			pEditUrl = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl1")));
			if (code.empty()){
				m_strLink1 = linkUrl;
			}else{
				m_strLink1 = (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"链接: " : L"Link: ") + linkUrl + (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"     提取码: " : L"     Extraction Code: ") + code;
			}
			break;
		case 1:
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout2")));
			pStatus = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_TextStatus2")));
			pEditUrl = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl2")));
			if (code.empty()){
				m_strLink2 = linkUrl;
			}else{
				m_strLink2 = (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"链接: " : L"Link: ") + linkUrl + (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"     提取码: " : L"     Extraction Code: ") + code;
			}
			break;
		case 2:
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout3")));
			pStatus = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_TextStatus3")));
			pEditUrl = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl3")));
			if (code.empty()){
				m_strLink3 = linkUrl;
			}else{
				m_strLink3 = (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"链接: " : L"Link: ") + linkUrl + (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"     提取码: " : L"     Extraction Code: ") + code;
			}
			break;
		default:
			break;
		}		
		
		if (NULL == pLayout || NULL == pStatus)
			return;
		pLayout->SetVisible(true);


		std::wstring tmpStr = L"";
		std::wstring strTip = L"";
		if (iter->role() == "viewer"){
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}" + L" ";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}
		else if (iter->role() == "previewer"){
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}" + L" ";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}
		else if (iter->role() == "uploader"){
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L"{/c}{/f}" + L" ";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT);
		}
		else {
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L"{f 16}{c #000000}" 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}" + L" ";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}

		if (iter->plainAccessCode().empty()){
			if (iter->accesCodeMode() == "mail"){
				tmpStr = tmpStr + L"   " +iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT) + L" " 
				+ L"{f 16}{c #000000}" 
				+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDYMATICCODE_TEXT)
				+ L"{/c}{/f}" + L" ";

				strTip = strTip + L"   " +iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDYMATICCODE_TEXT);
			}
		}else{
			tmpStr = tmpStr + L"   " 
				+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT)
				+ L"{f 16}{c #000000} " 
				+ code
				+ L"{/c}{/f}" + L" ";

			strTip = strTip + L"   " 
				+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT)
				+ L"" + code;
		}

		
		if(iter->effectiveAt() != 0 || iter->expireAt() != 0)
		{
			if(iter->expireAt() == 0){
				tmpStr = tmpStr + L"      " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ L"{/c}{/f}" + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP2_TEXT) + L" ";

				strTip = strTip + L"      " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP2_TEXT);
			}
			else{
				tmpStr = tmpStr + L"      "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ L"{/c}{/f}" + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeSecond.substr(0, datetimeSecond.size()-3)
					+ L"{/c}{/f}" + L" ";

				strTip = strTip + L"      "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ datetimeSecond.substr(0, datetimeSecond.size()-3);
			}
		}

		pEditUrl->SetText(linkUrl.c_str());
		pStatus->SetText(tmpStr.c_str());
		pStatus->SetToolTip(strTip.c_str());

	}


	void ShareLinkCountDialog::OnEditUrlClick(int nType)
	{
		CEditUI* pEdit = NULL;
		switch (nType)
		{
		case 0:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl1")));
			break;
		case 1:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl2")));
			break;
		case 2:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl3")));
			break;
		}

		if (pEdit)
			EditUrl(pEdit);
	}
	void ShareLinkCountDialog::OnCopyUrlClick(int nType)
	{
		std::wstring strLink = L"";
		switch (nType)
		{
		case 0:
			strLink = m_strLink1;	break;
		case 1:
			strLink = m_strLink2;	break;
		case 2:
			strLink = m_strLink3;	break;
		default:
			break;
		}

		CopyUrl(strLink);
	}
	void ShareLinkCountDialog::OnEmailUrlClick(int nType)
	{

		CEditUI* pEdit = NULL;
		switch (nType)
		{
		case 0:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl1")));
			break;
		case 1:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl2")));
			break;
		case 2:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl3")));
			break;
		}

		if (pEdit)
			EmailUrl(pEdit);
	}
	void ShareLinkCountDialog::OnDeleteUrlClick(int nType)
	{
		m_pNoticeFrame->Run(Choose,Ask,L"",MSG_SHARE_CANCELASK_KEY, Modal);
		if (!m_pNoticeFrame->IsClickOk())	return;

		CEditUI* pEdit = NULL;
		CVerticalLayoutUI*	pLayout = NULL;
		switch (nType)
		{
		case 0:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl1")));
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout1")));
			break;
		case 1:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl2")));
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout2")));
			break;
		case 2:
			pEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_EditUrl3")));
			pLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("CreateLinkDialog_Layout3")));
			break;
		}

		if (pEdit && pLayout){
			DeleteUrl(pEdit, pLayout);
		}
	}

	void ShareLinkCountDialog::OnCreateLinkClick()
	{
		ShowWindow(false);
		ShareLinkContextDialog::CreateDlg(m_paintMgr.GetPaintWindow(), m_pUserContext, m_nodeData, L"");
		InitWindow();
		ShowWindow(true);
	}

	void ShareLinkCountDialog::OnCancelLinkClick()
	{
		Close();
	}

	void ShareLinkCountDialog::EditUrl(CControlUI* pControl)
	{
		if (NULL == pControl)	return;
		std::wstring strUrl = pControl->GetText();

		std::wstring linkCode = L"";
		std::string::size_type pos = SD::Utility::String::wstring_to_utf8(strUrl).find("/p/");
		linkCode = strUrl.substr(pos+3, strUrl.size()-pos -3);

		ShowWindow(false);
		ShareLinkContextDialog::CreateDlg(m_paintMgr.GetPaintWindow(), m_pUserContext, m_nodeData, linkCode);
		InitWindow();
		ShowWindow(true);
	}

	void ShareLinkCountDialog::CopyUrl(std::wstring copyUrl)
	{
		if(::OpenClipboard(NULL)&&::EmptyClipboard()) 
		{ 
			HGLOBAL clipbuffer; 
			clipbuffer = ::GlobalAlloc(GMEM_MOVEABLE, (copyUrl.size()+1)*sizeof(TCHAR));
			if(NULL == clipbuffer) 
			{
				::CloseClipboard();
				return;
			}
			LPTSTR lpStr =(LPTSTR)GlobalLock(clipbuffer);
			if(NULL != lpStr)
			{
				memcpy_s(lpStr, (copyUrl.size()+1)*sizeof(TCHAR), copyUrl.c_str(), copyUrl.size()*sizeof(TCHAR));
			}

			GlobalUnlock(clipbuffer); 
			SetClipboardData(CF_UNICODETEXT,clipbuffer); 
			::CloseClipboard();
			GlobalFree(clipbuffer);
			m_pNoticeFrame->Run(Confirm,Right,L"",MSG_SHARE_COPYSUCCESS_KEY);
		}

	}
	void ShareLinkCountDialog::EmailUrl(CControlUI* pControl)
	{
		if (NULL == pControl)	return;

		std::wstring linkCode = L"";
		std::wstring type = L"";
		std::wstring tmpUrl = pControl->GetText();
		std::string::size_type pos = SD::Utility::String::wstring_to_utf8(tmpUrl).find("/p/");
		linkCode = tmpUrl.substr(pos+3, tmpUrl.size()-pos -3);

		ShareLinkNode linkNode;
		int32_t ret = m_pUserContext->getShareResMgr()->getShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(linkCode), linkNode);
		if(ret != RT_OK)
		{
			return;
		}

		//待填充数据传入
		SendShareLinkDialog * sendP = new SendShareLinkDialog(m_pUserContext,linkNode,m_nodeData);
		sendP->Create(this->m_hWnd, _T("SendShareLinkDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		sendP->CenterWindow();
		sendP->ShowModal();
		delete sendP;
	}

	void ShareLinkCountDialog::DeleteUrl(CControlUI* pControl, CControlUI* pParent)
	{
		if (NULL == pControl)	return;
		if (NULL == pParent)	return;

		std::wstring linkCode = L"";
		std::wstring type = L"";
		std::wstring tmpUrl = pControl->GetText();
		std::string::size_type pos = SD::Utility::String::wstring_to_utf8(tmpUrl).find("/p/");
		linkCode = tmpUrl.substr(pos+3, tmpUrl.size()-pos -3);

		int32_t ret = m_pUserContext->getShareResMgr()->delShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(linkCode)
			, SD::Utility::String::wstring_to_utf8(type));
		if(ret != RT_OK)
		{
			return;
		}

		pParent->SetVisible(false);

		CButtonUI* pBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_BtnCreate")));
		if (pBtn)
			pBtn->SetVisible(true);
	}

	bool ShareLinkCountDialog::SortShareLinkNode(const ShareLinkNode& nodeA, const ShareLinkNode& nodeB)
	{
		int nAccessA = 0, nAccessB = 0;
		if (nodeA.role() == "previewer")	nAccessA = 1;
		else if (nodeA.role() == "viewer")	nAccessA = 2;
		else if (nodeA.role() == "uploader")	nAccessA = 3;
		else if (nodeA.role() == "uploadAndView")	nAccessA = 4;

		if (nodeB.role() == "previewer")	nAccessB = 1;
		else if (nodeB.role() == "viewer")	nAccessB = 2;
		else if (nodeB.role() == "uploader")	nAccessB = 3;
		else if (nodeB.role() == "uploadAndView")	nAccessB = 4;

		if (nAccessA != nAccessB)	
			return nAccessA < nAccessB;
		else
			return nodeA.createdAt() > nodeB.createdAt();
	}


}
