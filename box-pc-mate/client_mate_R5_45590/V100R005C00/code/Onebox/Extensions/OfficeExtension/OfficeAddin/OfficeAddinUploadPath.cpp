#include "OfficeAddinThriftClient.h"
#include "OfficeAddinUploadPath.h"
#include "OfficeAddinProgressFrame.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "NotifyMgr.h"
#include "Global.h"
#include "UserConfigure.h"
#include "ErrorConfMgr.h"
#include "CommonFileDialogThriftNotify.h"

#ifdef _DEBUG
#pragma comment(lib, "DuiLib_d.lib")
#else
#pragma comment(lib, "DuiLib.lib")
#endif
#define  OFFICE_UPLOADFRAME_CLSNAME L"OFFICE_UPLOADFRAME_CLSNAME"

DUI_BEGIN_MESSAGE_MAP(OfficeAddinUploadPath,CNotifyPump)
	DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
DUI_END_MESSAGE_MAP()

OfficeAddinUploadPath::OfficeAddinUploadPath(const std::wstring& path, HWND hwnd, POFFICEUPLOADINFO officeuploadinfo)
:path_(path)
,id_(L"")
,parentPath_(L"")
,commonFolderEXType_(0)
,parentId_(-1)
,m_state_(false)
,m_officeuploadinfo_(officeuploadinfo)
{
	Create(hwnd,  IniLanguageHelper(languageID_).GetCommonString(COMMENT_UPLOAD_KEY).c_str(), UI_WNDSTYLE_FRAME, WS_EX_TOPMOST, 0, 0, 0, 0);
}

void OfficeAddinUploadPath::InitWindow()
{
	WindowImplBase::InitWindow();
	int64_t defaultValue = -1;
	parentId_ = GetUserConfValue(CONF_SETTINGS_SECTION,CONF_OFFICEONEBOXPATH_ID_KEY,defaultValue); 
	if ( parentId_ < 0)
	{
		parentPath_ = L"\\Office\\PC";
	}
	else
	{
		(void)OfficeAddinThriftClient::getInstance()->getPathByFileId(parentId_, parentPath_);
	}
	languageID_ = iniLanguageHelper.GetLanguage();


}

bool OfficeAddinUploadPath::InitLanguage(CControlUI* control)
{
	return true;
}

void OfficeAddinUploadPath::OnFinalMessage(HWND hWnd)
{
	WindowImplBase::OnFinalMessage(hWnd);
	delete this;
}

LPCTSTR OfficeAddinUploadPath::GetWindowClassName(void) const
{
	return L"OfficeAddinUploadPath";
}

CDuiString OfficeAddinUploadPath::GetSkinFolder()
{
	return  (GetInstallPath()+IniLanguageHelper(languageID_).GetSkinFolderPath()+L"Office\\").c_str();
}

CDuiString OfficeAddinUploadPath::GetSkinFile()
{
	return L"OfficeAddinUploadPath.xml";
}

LRESULT OfficeAddinUploadPath::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	return WindowImplBase::HandleMessage(uMsg,wParam,lParam);
}

void OfficeAddinUploadPath::resultHandler(const Onebox::CommonFileDialogListResult& result)
{
	if (result.empty())
	{  
		return;
	}
	parentPath_ = result.front()->path;
	commonFolderEXType_=result.front()->commonFolderEXType;
	Onebox::RemoteCommonFileDialogThriftData* filethriftdata = NULL;
	filethriftdata = static_cast<Onebox::RemoteCommonFileDialogThriftData*>(result.front()->data.get());
	if (filethriftdata)
	{
		parentId_ = filethriftdata->id;
	}
}

void OfficeAddinUploadPath::OnClick(TNotifyUI& msg)
{
    CControlUI*  control = msg.pSender;
	if (_tcsicmp(control->GetName(),L"OfficeShowUploadPath_button_close")==0)
	{
		Close(IDCANCEL);
	}
	else if (_tcsicmp(control->GetName(),L"OfficeShowUploadPath_button_updatePath")==0)
	{
		ShowWindow(false);
		std::wstring strTiltle = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY); 
			//+ L" "+ getShortPath(path_,25) +  L" "+ iniLanguageHelper.GetCommonString(COMMOM_UPLOADTOONEBOX_KEY);
		Onebox::CommonFileDialogPtr fileDailog = Onebox::CommonFileDialog::createInstance(
			GetInstallPath() + IniLanguageHelper(languageID_).GetSkinFolderPath(),
			this->GetHWND(),
			NULL,iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY)
			,OFFICE_UPLOADFRAME_CLSNAME,
			iniLanguageHelper.GetCommonString(COMMENT_SELECTCLOUD_KEY),
			strTiltle.c_str(),path_);
		fileDailog->setNotify(new Onebox::MyFileCommonFileDialogThriftNotify(OfficeAddinThriftClient::getInstance()->getUserId(), languageID_));
		fileDailog->setOption((Onebox::CommonFileDialogOption)(Onebox::CFDO_only_show_folder
			//&Onebox::CFDO_ok_button_visible
			));
		Onebox::CommonFileDialogError commonFileDialogError = fileDailog->showModal(std::bind(&OfficeAddinUploadPath::resultHandler, this, std::placeholders::_1));
		if (commonFileDialogError == Onebox::E_CFD_CANCEL)
		{
			ShowWindow(true);
			return;
		}

		if( (NULL != m_officeuploadinfo_) && (NULL != m_officeuploadinfo_.get()) )
		{
			m_officeuploadinfo_->remotePath = parentPath_;
			m_officeuploadinfo_->extype = commonFolderEXType_;
			m_officeuploadinfo_->id = parentId_;
		}
		Close(IDOK);
	}
	else if ( _tcsicmp(control->GetName(),L"OfficeShowUploadPath_button_upload") == 0 )
	{
		if( (NULL != m_officeuploadinfo_) && (NULL != m_officeuploadinfo_.get()) )
		{
			m_officeuploadinfo_->remotePath = parentPath_;
			m_officeuploadinfo_->extype = commonFolderEXType_;
			m_officeuploadinfo_->id = parentId_;
		}
		Close(IDOK);
	}
}

void OfficeAddinUploadPath::setControlAttribute()
{
	CRichEditUI* richEdit = static_cast<CRichEditUI*>(m_PaintManager.FindControl(L"OfficeShowUploadPath_showPath"));
	if (NULL != richEdit)
	{
		richEdit->SetText(parentPath_.c_str());
	}

	CLabelUI* title = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OfficeShowUploadPath_label_name"));
	if (NULL != title)
	{
		std::wstring strTiltle = IniLanguageHelper(languageID_).GetCommonString(COMMENT_UPLOAD_KEY); 
			//+ L" "+ getShortPath(path_,25) +  L" "+ IniLanguageHelper(languageID_).GetCommonString(COMMOM_UPLOADTOONEBOX_KEY);
		title->SetText(strTiltle.c_str());
		title->SetToolTip(path_.c_str());
	}
}

void OfficeAddinUploadPath::show()
{
	m_state_ = false;
	Create(NULL, IniLanguageHelper(languageID_).GetCommonString(COMMENT_UPLOAD_KEY).c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE, 0, 0, 0, 0);
	setControlAttribute();
	CenterWindow();	
	ShowWindow();
}

UINT OfficeAddinUploadPath::showModal()
{
	m_state_ = false;

	setControlAttribute();
	CenterWindow();
	return ShowModal();
}

std::wstring& OfficeAddinUploadPath::GetPath()
{
	return path_;
}
std::wstring& OfficeAddinUploadPath::GetId()
{
	return id_;
}
std::wstring& OfficeAddinUploadPath::GetParentPath()
{
	return parentPath_;
}
int32_t		  OfficeAddinUploadPath::GetCommonFolderEXType()
{
	return commonFolderEXType_;
}

bool OfficeAddinUploadPath::IsOk()
{
	return m_state_;
}

POFFICEUPLOADINFO OfficeAddinUploadPath::GetUploadInfo()
{
	return m_officeuploadinfo_;
}
