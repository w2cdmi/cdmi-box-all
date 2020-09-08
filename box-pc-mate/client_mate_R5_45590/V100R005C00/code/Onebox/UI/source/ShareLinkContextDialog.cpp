#include "stdafxOnebox.h"
#include "ShareLinkContextDialog.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"

#include "UserInfoMgr.h"
#include "UserContextMgr.h"
#include "ShareResMgr.h"
#include "SendShareLink.h"
#include <boost/algorithm/string.hpp>

namespace Onebox
{
#define OFFSET_DAY		(864000000000)		//int64_t的一天时间量

	DUI_BEGIN_MESSAGE_MAP(ShareLinkContextDialog, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED, OnSelectChanged)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMSELECT, OnItemSelect)
	DUI_END_MESSAGE_MAP()


	ShareLinkContextDialog::ShareLinkContextDialog(LPCTSTR strXML, UserContext* context, UIFileNode& nodeData, HWND hParentWnd)
		: m_strXML(strXML)
		, m_pUserContext(context)
		, m_nodeData(nodeData)
		, m_hParentWnd(hParentWnd)
	{
		m_pLabelTitle = NULL;
		m_pEditUrl = NULL;
		m_pEditCode = NULL;
		m_pEditEmail = NULL;

		m_pCheckDownload = NULL;
		m_pCheckPreview = NULL;
		m_pCheckUpload = NULL;
		m_pCheckUseCode = NULL;
		m_pCheckDynamicCode = NULL;

		m_pLayoutSwitch = NULL;
		m_pLayoutCode = NULL;
		m_pLayoutEmail = NULL;
		m_pLayoutTime = NULL;

		m_pComboTime = NULL;
		m_pDateStart = NULL;
		m_pDateStop = NULL;

		m_pBtnCopyLink = NULL;
		m_pBtnSendLink = NULL;
		m_pBtnFinish = NULL;
		m_pBtnCancel = NULL;
		m_pCtlRefresh = NULL;
		m_pBtnRefresh = NULL;

		m_nResult = 0;
		m_strLinkUrl = L"";
		m_pNoticeFrame = NULL;

		m_bViewer = FALSE;
	}

	ShareLinkContextDialog::~ShareLinkContextDialog()
	{
		SAFE_DELETE_POINTER(m_pNoticeFrame);
	}

	CDuiString ShareLinkContextDialog::GetSkinFolder() 
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString ShareLinkContextDialog::GetSkinFile() 
	{
		return m_strXML;
	}

	LPCTSTR ShareLinkContextDialog::GetWindowClassName(void) const 
	{
		return _T("ShareLinkDialog");
	}

	CControlUI* ShareLinkContextDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	LRESULT ShareLinkContextDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		WindowImplBase::OnCreate(uMsg, wParam, lParam, bHandled);

		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		styleValue &= ~WS_CAPTION;
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		return 0;
	}

	void ShareLinkContextDialog::OnFinalMessage( HWND hWnd )
	{
		__super::OnFinalMessage(hWnd);
	}

	LRESULT ShareLinkContextDialog::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (0xF032 == wParam)
			return 0;

		return WindowImplBase::OnSysCommand(uMsg, wParam, lParam, bHandled);
	}

	LRESULT ShareLinkContextDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void ShareLinkContextDialog::InitWindow()
	{		
		if (!InitControl())
			return;


		int32_t ret = RT_ERROR;
		if (NULL != m_pUserContext)
			ret = m_pUserContext->getShareResMgr()->getServerConfig(m_serverSysConfig);
		if(ret != RT_OK)
		{
			SERVICE_ERROR("ShareLinkDialog", ret, "getServerConfig failed.");
		}


		// 1.判断是否需要创建新的链接
//		ShareLinkNode shareLinkNode;
		std::wstring accessMode = L"static";
		if(m_strLinkUrl.empty())
		{
			m_bViewer = FALSE;
			ret = m_pUserContext->getShareResMgr()->addShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(accessMode), m_shareLinkNode);
		}
		else
		{
			m_bViewer = TRUE;
			ret = m_pUserContext->getShareResMgr()->getShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(m_strLinkUrl), m_shareLinkNode);
			if(m_shareLinkNode.accesCodeMode().empty())	//没有权限则新建链接
			{
				ret = m_pUserContext->getShareResMgr()->addShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(accessMode), m_shareLinkNode);
			}
		}

		if(ret == EXCEED_MAX_LINK_NUM)
		{
			m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_SHARETOOMUCH_KEY);
			Close();
			return;
		}
		else if(ret != RT_OK)
		{
			//MessageBox(NULL, _T("失败"), _T("deleteShareUserList"),MB_OK);
			return;
		}
		//end 1

		SetControl(m_shareLinkNode);
		
	}

	void ShareLinkContextDialog::SetControl(ShareLinkNode& shareLinkNode)
	{

		std::wstring linkUrl = SD::Utility::String::utf8_to_wstring(shareLinkNode.url());
		m_pEditUrl->SetText(linkUrl.c_str());



		if (m_serverSysConfig.complexCode()){
			m_pEditCode->SetReadOnly(true);
			m_pEditCode->SetMouseEnabled(true);
			m_pCtlRefresh->SetVisible(true);
			m_pBtnRefresh->SetVisible(true);

			CLabelUI* pLabel = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("label_input_code")));
			if (pLabel)	pLabel->SetVisible(false);
			RefreshCode();
		}else{
			m_pEditCode->SetReadOnly(false);
			m_pEditCode->SetMouseEnabled(true);
			m_pCtlRefresh->SetVisible(false);
			m_pBtnRefresh->SetVisible(false);
		}

		//提取密码非空
		if(!shareLinkNode.plainAccessCode().empty())
		{
			m_pCheckUseCode->SetCheck(true);
			m_pLayoutCode->SetVisible(true);
			//获取动态密码
			std::wstring accessCode = SD::Utility::String::utf8_to_wstring(shareLinkNode.plainAccessCode());
			m_pEditCode->resetText(accessCode.c_str());			
		}
		if (m_pEditCode->GetText().IsEmpty() && !m_bViewer && m_serverSysConfig.complexCode())
			RefreshCode();


		if(shareLinkNode.accesCodeMode() == "mail")
		{
			m_pCheckUseCode->SetCheck(true);
			m_pCheckDynamicCode->SetCheck(true);
			m_pLayoutEmail->SetVisible(true);
			std::wstring accessCode = L"";
			for(IdentityList::iterator it = shareLinkNode.identity_.begin(); it != shareLinkNode.identity_.end(); ++it)
			{
				accessCode = accessCode + SD::Utility::String::utf8_to_wstring(*it);
				m_pEditEmail->resetText(accessCode.c_str());
			}
		}

		if(0 != shareLinkNode.effectiveAt() && 0 != shareLinkNode.expireAt())
		{
			int64_t startTime = shareLinkNode.effectiveAt();
			int64_t stopTime = shareLinkNode.expireAt();


			if (stopTime == startTime+OFFSET_DAY){
				m_pComboTime->SelectItem(1);
			}else if (stopTime == startTime+OFFSET_DAY*3){
				m_pComboTime->SelectItem(2);
			}else if (stopTime == startTime+OFFSET_DAY*7){
				m_pComboTime->SelectItem(3);
			}else {
				SYSTEMTIME st = int64TimeToSysTime(m_shareLinkNode.createdAt());
				st.wMilliseconds = 4;
				time_t t = systime_to_time_t(st);

				struct tm curTm ;
				localtime_s(&curTm, &t);

				int nMon = ++curTm.tm_mon;
				time_t temp = mktime(&curTm);
				//如果溢出，月份值会比保存的值大
				if (curTm.tm_mon != nMon){
					curTm.tm_mday = 0;	//设为0，自动变成上个月最后一天
					temp = mktime(&curTm);
				}

				st=time_t_to_systime(temp);
				int64_t timeExpire = sysTimetoInt64Time(st);
				if (stopTime == timeExpire){
					m_pComboTime->SelectItem(4);
				}
				else
				{
					m_pComboTime->SelectItem(5);

					SYSTEMTIME stime;
					stime = int64TimeToSysTime(shareLinkNode.effectiveAt());
					m_pDateStart->SetTime(&stime);

					if(!shareLinkNode.expireAt())
					{
						m_pDateStop->SetText(L"");
					}
					else
					{
						stime = int64TimeToSysTime(shareLinkNode.expireAt());
						m_pDateStop->SetTime(&stime);
					}
				}
				
			}		
		}

		if(shareLinkNode.role() ==  "viewer")
		{
			m_pCheckUpload->SetCheck(false);
			m_pCheckDownload->SetCheck(true);
			m_pCheckPreview->SetCheck(true);		
		}
		else if(shareLinkNode.role() ==  "previewer")
		{
			m_pCheckUpload->SetCheck(false);
			m_pCheckDownload->SetCheck(false);
			m_pCheckPreview->SetCheck(true);
		}
		else if(shareLinkNode.role() ==  "uploader")
		{
			m_pCheckUpload->SetCheck(true);
			m_pCheckDownload->SetCheck(false);
			m_pCheckPreview->SetCheck(false);
		}
		else if(shareLinkNode.role() ==  "uploadAndView")
		{
			m_pCheckUpload->SetCheck(true);
			m_pCheckPreview->SetCheck(true);
			m_pCheckDownload->SetCheck(true);
		}

		if(FILE_TYPE_FILE == m_nodeData.basic.type)
		{
			m_pCheckUpload->SetVisible(false);
		}
		else
		{
			m_pCheckUpload->SetVisible(true);
		}


	}

	SYSTEMTIME ShareLinkContextDialog::int64TimeToSysTime(int64_t time)
	{
		SYSTEMTIME stime;
		TIME_ZONE_INFORMATION timeZone;
		(void)memset_s(&stime, sizeof(SYSTEMTIME), 0, sizeof(SYSTEMTIME));
		if (TIME_ZONE_ID_INVALID == GetTimeZoneInformation(&timeZone))
		{
			m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_GETTIMEZONEFAILED_KEY);
			return stime;
		}
		time -= ((int64_t)timeZone.Bias)*600000000;
		FILETIME ftime;
		ftime.dwLowDateTime = (DWORD)time;
		ftime.dwHighDateTime = (DWORD)(time>>32);	
		if (!FileTimeToSystemTime(&ftime, &stime))
		{
			return stime;
		}
		return stime;
	}

	BOOL ShareLinkContextDialog::InitControl()
	{
		m_pNoticeFrame = new NoticeFrameMgr(m_hWnd);
		if (NULL == m_pNoticeFrame)	return FALSE;


		m_pLabelTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LabelTitle")));
		m_pEditUrl = static_cast<CEditUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_EditUrl")));
		m_pEditCode = static_cast<CSearchTxtUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_EditCode")));
		m_pEditEmail = static_cast<CSearchTxtUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_EditEmail")));

		m_pCheckDownload = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_CheckDownload")));
		m_pCheckPreview = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_CheckPreview")));
		m_pCheckUpload = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_CheckUpload")));
		m_pCheckUseCode = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_CheckUseCode")));
		m_pCheckDynamicCode = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_CheckDynamicCode")));

		m_pLayoutSwitch = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LayoutSwitch")));
		m_pLayoutCode = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LayoutCode")));
		m_pLayoutEmail = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LayoutEmail")));
		m_pLayoutTime = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LayoutTime")));

		m_pComboTime = static_cast<CComboUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_ComboTime")));
		m_pDateStart = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_StartDateTime")));
		m_pDateStop = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_StopDateTime")));

		m_pBtnCopyLink = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LabelTitle")));
		m_pBtnSendLink = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LabelTitle")));
		m_pBtnFinish = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LabelTitle")));
		m_pBtnCancel = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_LabelTitle")));
		m_pBtnRefresh = static_cast<CButtonUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_BtnRefresh")));
		m_pCtlRefresh = static_cast<CControlUI*>(m_PaintManager.FindControl(_T("ShareLinkDialog_ctlRefresh")));


		if (NULL == m_pLabelTitle || 	NULL == m_pEditUrl || 	NULL == m_pEditCode || 	NULL == m_pEditEmail || 
			NULL == m_pCheckDownload || NULL == m_pCheckPreview || NULL == m_pCheckUpload || NULL == m_pCheckUseCode || NULL == m_pCheckDynamicCode || 
			NULL == m_pLayoutSwitch || 	NULL == m_pLayoutCode || NULL == m_pLayoutEmail || 	NULL == m_pLayoutTime || 
			NULL == m_pComboTime || NULL == m_pDateStart || NULL == m_pDateStop || 	
			NULL == m_pBtnCopyLink || 	NULL == m_pBtnSendLink || NULL == m_pBtnFinish || NULL == m_pBtnCancel || NULL == m_pBtnRefresh || NULL == m_pCtlRefresh)
		{
			Close();
			return FALSE;
		}

		CDuiString strDefault = L"";
		m_pEditCode->setDefaultTxt(strDefault);
		m_pEditEmail->setDefaultTxt(strDefault);

		m_pLayoutCode->SetVisible(false);
		m_pLayoutEmail->SetVisible(false);
		if (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()){
			m_pDateStart->SetLanguageType(true);
			m_pDateStop->SetLanguageType(true);
		}
		else{
			m_pDateStart->SetLanguageType(false);
			m_pDateStop->SetLanguageType(false);
		}
		m_pComboTime->SelectItem(0);

		return TRUE;
	}

	void ShareLinkContextDialog::OnClick(TNotifyUI& msg)
	{
		CDuiString strName = msg.pSender->GetName();
		if (0 == _tcsicmp(strName, _T("closebtn"))){
			m_nResult = 0;
			OnCloseClick();
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_cancel"))){
			m_nResult = 1;
			OnCancelClick();
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_finish"))){
			
			if (RT_OK == OnFinishClick()){
				m_nResult = 10;
				Close();
			}
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_copylink"))){
			if (RT_OK == OnFinishClick(false)){
				CopyUrl();
 				m_nResult = 10;
 //				Close();
				m_bViewer = TRUE;
			}
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_sendlink"))){
			if (RT_OK == OnFinishClick(false)){
				m_nResult = 10;
				SendEmail();
				Close();
			}
		}
		else if (0 == _tcsicmp(strName, _T("ShareLinkDialog_BtnRefresh"))){
			RefreshCode();
		}
	}

	void ShareLinkContextDialog::OnSelectChanged(TNotifyUI& msg)
	{
		if (msg.pSender == m_pCheckUseCode)
			OnCheckUseCode();
		else if (msg.pSender == m_pCheckDynamicCode)
			OnCheckDynamicCode();
		else if (msg.pSender == m_pCheckPreview)
			OnCheckPreview();
		else if (msg.pSender == m_pCheckDownload)
			OnCheckDownload();
		else if (msg.pSender == m_pCheckUpload)
			OnCheckUpload();
	}

	void ShareLinkContextDialog::OnItemSelect(TNotifyUI& msg)
	{
		if (msg.pSender == m_pComboTime)
			OnSelectTime();
	}

	void ShareLinkContextDialog::OnCheckPreview()
	{
		//	上传选中，预览跟下载绑定
		if (m_pCheckUpload->IsSelected())
		{
			if (m_pCheckPreview->IsSelected())
				m_pCheckDownload->Selected(true);
			else
				m_pCheckDownload->Selected(false);
		}else{
			if (!m_pCheckPreview->IsSelected())
				m_pCheckDownload->SetCheck(false);
		}
		
	}

	void ShareLinkContextDialog::OnCheckDownload()
	{
		//	上传选中，预览跟下载绑定
		if (m_pCheckUpload->IsSelected())
		{
			if (m_pCheckDownload->IsSelected())
				m_pCheckPreview->Selected(true);
			else
				m_pCheckPreview->Selected(false);
		}else{
			if (m_pCheckDownload->IsSelected())
				m_pCheckPreview->SetCheck(true);
		}
	}
	void ShareLinkContextDialog::OnCheckUpload()
	{
		if (m_pCheckUpload->IsSelected()){
			if (m_pCheckPreview->IsSelected())
				m_pCheckDownload->Selected(true);
		}
	}
	void ShareLinkContextDialog::OnCheckUseCode()
	{
		if (m_pCheckUseCode->IsSelected()){
			ShowCode();
		}else{
			ShowCode(false);
		}
	}
	void ShareLinkContextDialog::OnCheckDynamicCode()
	{
		if (m_pCheckDynamicCode->IsSelected()){
			ShowEmail();
		}else{
			ShowEmail(false);
		}
	}

	void ShareLinkContextDialog::ShowCode(bool bShow/* =true */)
	{
		if (bShow)
		{
			m_pLayoutEmail->SetVisible(false);
			m_pLayoutCode->SetVisible(true);
			m_pCheckDynamicCode->SetEnabled(true);
			m_pLayoutSwitch->SetVisible(true);
		}else{
			m_pCheckDynamicCode->SetCheck(false);
			m_pLayoutSwitch->SetVisible(false);
			m_pLayoutCode->SetVisible(false);
			m_pLayoutEmail->SetVisible(false);
			m_pCheckDynamicCode->SetEnabled(false);
		}
	}

	void ShareLinkContextDialog::ShowEmail(bool bShow/* =true */)
	{
		if (bShow){
			m_pLayoutCode->SetVisible(false);
			m_pLayoutEmail->SetVisible(true);
		}else{
			m_pLayoutEmail->SetVisible(false);
			m_pLayoutCode->SetVisible(true);
		}
	}
	
	void ShareLinkContextDialog::OnSelectTime()
	{

		bool bShowTime=false;
		
		int64_t timeEffctive = -1;
		int64_t timeExpire = -1;
		SYSTEMTIME curTime;
		GetLocalTime(&curTime);
		int64_t nCurTime = sysTimetoInt64Time(curTime);

		int nIdx = m_pComboTime->GetCurSel();
		switch (nIdx)
		{
		case 0:break;
		case 1:
			{
				SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
				time.wMilliseconds = 1;
				timeEffctive = sysTimetoInt64Time(time);
				timeExpire = timeEffctive + OFFSET_DAY;

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					m_pComboTime->SelectItem(5);
					return;
				}
			}			
			break;
		case 2:
			{
				SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
				time.wMilliseconds = 1;
				timeEffctive = sysTimetoInt64Time(time);
				timeExpire = timeEffctive + OFFSET_DAY*3;

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					m_pComboTime->SelectItem(5);
					return;
				}
			}
			break;
		case 3:
			{
				SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
				time.wMilliseconds = 1;
				timeEffctive = sysTimetoInt64Time(time);
				timeExpire = timeEffctive + OFFSET_DAY*7;

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					m_pComboTime->SelectItem(5);
					return;
				}
			}
			break;
		case 4:
			{
				SYSTEMTIME st = int64TimeToSysTime(m_shareLinkNode.createdAt());
				st.wMilliseconds = 4;
				time_t t = systime_to_time_t(st);

				struct tm curTm ;
				localtime_s(&curTm, &t);
			
				int nMon = ++curTm.tm_mon;
				time_t temp = mktime(&curTm);
				//如果溢出，月份值会比保存的值大
				if (curTm.tm_mon != nMon){
					curTm.tm_mday = 0;	//设为0，自动变成上个月最后一天
					temp = mktime(&curTm);
				}

				st=time_t_to_systime(temp);
				timeExpire = sysTimetoInt64Time(st);

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					m_pComboTime->SelectItem(5);
					return;
				}
			}
			break;
		case 5:		bShowTime=true;	break;
		default:
			break;
		}

		ShowTime(bShowTime);
	}

	void ShareLinkContextDialog::ShowTime(bool bShow/* =true */)
	{
		m_pLayoutTime->SetVisible(bShow);
	}

	void ShareLinkContextDialog::OnCancelClick()
	{
		if (!m_bViewer)
			CancelUrl();

		Close();
	}

	void ShareLinkContextDialog::OnCloseClick()
	{
		if (!m_bViewer)
			CancelUrl();

		Close();
	}

	time_t ShareLinkContextDialog::systime_to_time_t(const SYSTEMTIME& st)
	{
		struct tm gm = {
			st.wSecond,
			st.wMinute,
			st.wHour,
			st.wDay,
			st.wMonth-1,
			st.wYear-1900,
			st.wDayOfWeek,
			st.wMilliseconds
		};
		return mktime(&gm);
	}

	SYSTEMTIME ShareLinkContextDialog::time_t_to_systime(time_t t)
	{
		tm temptm;
		localtime_s(&temptm, &t);
		SYSTEMTIME st = {
			1900 + temptm.tm_year,
			1 + temptm.tm_mon,
			temptm.tm_wday,
			temptm.tm_mday,
			temptm.tm_hour,
			temptm.tm_min,
			temptm.tm_sec,
			temptm.tm_isdst
		};
		return st;
	}

	int32_t ShareLinkContextDialog::OnFinishClick(bool bSend)
	{
		//get role
		std::wstring role = L"";

		if(!m_pCheckPreview->GetCheck() && !m_pCheckDownload->GetCheck() && !m_pCheckUpload->GetCheck())
		{
			m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_SELECTAUTHORITYSHARELINK_KEY);
			return RT_ERROR;
		}

		if(m_pCheckPreview->GetCheck())
		{
			role = L"previewer";
		}
		if(m_pCheckDownload->GetCheck())
		{
			role = L"viewer";
		}
		if(m_pCheckUpload->GetCheck())
		{
			if(m_pCheckPreview->GetCheck() || m_pCheckDownload->GetCheck())
			{
				role = L"uploadAndView";
			}
			else
			{
				role = L"uploader";
			}
		}

		//get share link code
		std::wstring linkCode = L"";
		std::wstring tmpUrl = m_pEditUrl->GetText();
		if(tmpUrl.empty())
		{
			return RT_ERROR;
		}
		std::string::size_type pos = SD::Utility::String::wstring_to_utf8(tmpUrl).find("/p/");
		linkCode = tmpUrl.substr(pos+3, tmpUrl.size()-pos -3);


		//get code
		std::wstring tmpCode = m_pEditCode->GetText();
		if(!m_pCheckUseCode->GetCheck())
		{
			m_pCheckDynamicCode->SetCheck(false);
			tmpCode = L"";
		}
		ShareLinkNode shareLinkNode;
		ShareLinkNodeEx shareLinkNodeEx;
		if((tmpCode.empty() || tmpCode == iniLanguageHelper.GetCommonString(COMMENT_INPUTCODE_KEY)) && m_pCheckUseCode->GetCheck() && !m_pCheckDynamicCode->GetCheck())
		{
			m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_CODENOTNULL_KEY);
			return RT_ERROR;
		}
		
		if(!m_serverSysConfig.complexCode() && !m_pCheckDynamicCode->GetCheck())
		{
			std::string tmpString = SD::Utility::String::wstring_to_utf8(tmpCode);
			for(size_t i = 0; i < tmpString.size(); ++i)
			{
				if((tmpString[i] >= '0'&& tmpString[i] <= '9') 
					|| (tmpString[i] >= 'a' && tmpString[i] <= 'z')
					|| (tmpString[i] >= 'A' && tmpString[i] <= 'Z'))
				{
					continue;
				}
				m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_SETERRORTEXT_KEY);
				return RT_ERROR;
			}
		}

		if(m_pCheckDynamicCode->GetCheck())
		{
			tmpCode = m_pEditEmail->GetText();
			tmpCode = boost::algorithm::trim_copy(tmpCode);
			size_t index = tmpCode.find(L";");
			if(std::wstring::npos == index || tmpCode.length() == index + 1 )
			{
				if(!IsEmailAddressValid(tmpCode))
				{
					m_pNoticeFrame->Run(Confirm,Error,L"",MSG_SHARE_MAILISINVALID_KEY);
					return RT_ERROR;
				}
			}
			else
			{
				m_pNoticeFrame->Run(Confirm,Error,L"",MSG_SHARE_MAILONLYONEEMAILADDRESS_KEY);
				return RT_ERROR;
			}
		}

		//get time
		std::wstring tmpFirstStr = L"";
		std::wstring tmpSecondStr = L"";
		int64_t timeEffctive = -1;
		int64_t timeExpire = -1;

		int nIdx = m_pComboTime->GetCurSel();
		SYSTEMTIME curTime;
		GetLocalTime(&curTime);
		int64_t nCurTime = sysTimetoInt64Time(curTime);
		switch (nIdx)
		{
		case 0:	
			break;
		case 1:	
				{
					SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
					timeEffctive = sysTimetoInt64Time(time);
					timeExpire = timeEffctive + OFFSET_DAY;

					if (nCurTime > timeExpire){
						m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
						return RT_ERROR;
					}
				}break;
		case 2:	
			{
				SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
				timeEffctive = sysTimetoInt64Time(time);
				timeExpire = timeEffctive + OFFSET_DAY*3;

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					return RT_ERROR;
				}
			}break;
		case 3:	
			{
				SYSTEMTIME time = int64TimeToSysTime(m_shareLinkNode.createdAt());
				timeEffctive = sysTimetoInt64Time(time);
				timeExpire = timeEffctive + OFFSET_DAY*7;

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					return RT_ERROR;
				}
			}break;
		case 4:	
			{	
// 				const time_t t = time(NULL);
// 				struct tm curTm ;
// 				localtime_s(&curTm, &t);
// 
// 				SYSTEMTIME st=time_t_to_systime(t);
// 				st.wMilliseconds = 4;
// 				timeEffctive = sysTimetoInt64Time(st);
// 				
// 				int nMon = ++curTm.tm_mon;
// 				time_t temp = mktime(&curTm);
// 				//如果溢出，月份值会比保存的值大
// 				if (curTm.tm_mon != nMon){
// 					curTm.tm_mday = 0;	//设为0，自动变成上个月最后一天
// 					temp = mktime(&curTm);
// 				}
// 
// 				st=time_t_to_systime(temp);
// 				timeExpire = sysTimetoInt64Time(st);

				SYSTEMTIME st = int64TimeToSysTime(m_shareLinkNode.createdAt());
				st.wMilliseconds = 4;
				timeEffctive = sysTimetoInt64Time(st);

				time_t t = systime_to_time_t(st);
				struct tm curTm ;
				localtime_s(&curTm, &t);

				int nMon = ++curTm.tm_mon;
				time_t temp = mktime(&curTm);
				//如果溢出，月份值会比保存的值大
				if (curTm.tm_mon != nMon){
					curTm.tm_mday = 0;	//设为0，自动变成上个月最后一天
					temp = mktime(&curTm);
				}

				st=time_t_to_systime(temp);
				timeExpire = sysTimetoInt64Time(st);

				if (nCurTime > timeExpire){
					m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_DATEISINVALID_KEY);
					return RT_ERROR;
				}
			}break;
		case 5:	
			{
				tmpFirstStr = m_pDateStart->GetText();
				tmpSecondStr = m_pDateStop->GetText();
				if(!tmpFirstStr.empty())
				{
					SYSTEMTIME st = m_pDateStart->GetTime();
					st.wMilliseconds = 5;
					timeEffctive = sysTimetoInt64Time(st);
				}
				if(!tmpSecondStr.empty())
				{
					SYSTEMTIME st = m_pDateStop->GetTime();
					st.wMilliseconds = 5;
					timeExpire = sysTimetoInt64Time(st);
				}
				if(tmpFirstStr.empty() )
				{
					m_pNoticeFrame->Run(Confirm,Error,L"",MSG_SHARE_SETTIMEERROR_C_KEY);
					return RT_ERROR;
				}
				if((timeEffctive > timeExpire) && !tmpSecondStr.empty())
				{
					m_pNoticeFrame->Run(Confirm,Error,L"",MSG_SHARE_SETTIMEERROR_B_KEY);
					return RT_ERROR;
				}
			}
			break;
		default:
			break;
		}







		//modify
		shareLinkNodeEx.effectiveAt(timeEffctive);
		shareLinkNodeEx.expireAt(timeExpire);
		if(m_pCheckDynamicCode->GetCheck())
		{
			shareLinkNodeEx.accessCodeMode("mail");
			shareLinkNodeEx.identity_.push_back(SD::Utility::String::wstring_to_utf8(tmpCode));
		}
		else
		{
			shareLinkNodeEx.accessCodeMode("static");
			shareLinkNodeEx.plainAccessCode(SD::Utility::String::wstring_to_utf8(tmpCode));
		}
		shareLinkNodeEx.role(SD::Utility::String::wstring_to_utf8(role));

		int32_t ret = m_pUserContext->getShareResMgr()->modifyShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_string(linkCode), shareLinkNodeEx, m_shareLinkNode);
		if(ret != RT_OK)
		{
			return RT_ERROR;
		}

		//send email
		if(m_pCheckDynamicCode->GetCheck() && bSend)
		{
			EmailNode emailNode;

			emailNode.type = "link";
			emailNode.email_param.message = "";
			emailNode.email_param.nodename = SD::Utility::String::wstring_to_utf8(m_nodeData.basic.name);
			emailNode.email_param.sender = SD::Utility::String::wstring_to_utf8(m_pUserContext->getUserInfoMgr()->getUserName());
			emailNode.email_param.plainaccesscode = shareLinkNodeEx.plainAccessCode();
			emailNode.email_param.start = shareLinkNode.effectiveAt();
			emailNode.email_param.end = shareLinkNode.expireAt();
			emailNode.email_param.linkurl = SD::Utility::String::wstring_to_string(tmpUrl);
			for(IdentityList::iterator it = shareLinkNodeEx.identity_.begin(); it != shareLinkNodeEx.identity_.end(); ++it)
			{
				emailNode.mailto = *it;
				(void)m_pUserContext->getShareResMgr()->sendEmail(emailNode);
			}
		}

		return RT_OK;
		
	}



	int64_t ShareLinkContextDialog::sysTimetoInt64Time(SYSTEMTIME& sysTime)
	{
		FILETIME lpFileTime;
		lpFileTime.dwLowDateTime = 0;
		lpFileTime.dwHighDateTime = 0;
		ULARGE_INTEGER ularge;
		TIME_ZONE_INFORMATION timeZone;
		if (TIME_ZONE_ID_INVALID == GetTimeZoneInformation(&timeZone))
		{
			m_pNoticeFrame->Run(Confirm,Warning,L"",MSG_SHARE_GETTIMEZONEFAILED_KEY);
			return 0;
		}
		SystemTimeToFileTime(&sysTime, &lpFileTime);
		ularge.LowPart = lpFileTime.dwLowDateTime;
		ularge.HighPart = lpFileTime.dwHighDateTime;
		return ularge.QuadPart + ((int64_t)timeZone.Bias)*600000000;
	}


	void ShareLinkContextDialog::CancelUrl()
	{
		std::wstring linkCode = L"";
		std::wstring type = L"";

		std::wstring tmpUrl = m_pEditUrl->GetText();
		if(tmpUrl.empty())
			return;

		std::string::size_type pos = SD::Utility::String::wstring_to_utf8(tmpUrl).find("/p/");
		linkCode = tmpUrl.substr(pos+3, tmpUrl.size()-pos -3);

		(void)m_pUserContext->getShareResMgr()->delShareLink(m_nodeData.basic.id, SD::Utility::String::wstring_to_utf8(linkCode)
			,SD::Utility::String::wstring_to_utf8(type));
	}

	void ShareLinkContextDialog::CopyUrl()
	{
		std::wstring source = m_pEditUrl->GetText();
		std::wstring strCode = m_pEditCode->GetText();
		std::wstring copyUrl;
		if (m_pCheckUseCode->GetCheck() && !strCode.empty())
			copyUrl = (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"链接: " : L"Link: ") + source +
			(UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"     提取码: " : L"     Extraction Code: ") + strCode;
		else
			copyUrl = (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage()? L"链接: " : L"Link: ") + source;

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
			m_pNoticeFrame->Run(Confirm,Ask,L"",MSG_SHARE_COPYSUCCESS_KEY, Modal);
			if (!m_pNoticeFrame->IsClickOk())	return;
		}
	}

	void ShareLinkContextDialog::SendEmail()
	{
		ShowWindow(false);
		SendShareLinkDialog * sendP = new SendShareLinkDialog(m_pUserContext,m_shareLinkNode,m_nodeData);
		sendP->Create(this->m_hWnd, _T("SendShareLinkDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		sendP->CenterWindow();
		sendP->ShowModal();
		delete sendP;
		sendP=NULL;
		Close();
	}

	void ShareLinkContextDialog::RefreshCode()
	{
		std::wstring complexCode = SD::Utility::String::create_random_string();
		if (m_pEditCode)
			m_pEditCode->resetText(complexCode.c_str());
	}







}