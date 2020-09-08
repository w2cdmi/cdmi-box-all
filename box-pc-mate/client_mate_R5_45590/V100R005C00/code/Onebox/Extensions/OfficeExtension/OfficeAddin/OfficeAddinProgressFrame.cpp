#pragma warning(disable:4996)
#include "OfficeAddinThriftClient.h"
#include "OfficeAddinProgressFrame.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "NotifyMgr.h"
#include "Global.h"
#include "NoticeFrame.h"
#include "ErrorConfMgr.h"
#include "OfficeControlNames.h"
#include <boost/algorithm/string.hpp>

#include "ListContainerElement.h"

#ifdef _DEBUG
#pragma comment(lib, "DuiLib_d.lib")
#else
#pragma comment(lib, "DuiLib.lib")
#endif

#ifndef MODULE_NAME
#define MODULE_NAME ("OfficeAddinProgressFrame")
#endif

#define WM_LOAD_TASK (WM_USER+1)

#define TIMER_ID (1)

using namespace SD;
using namespace OfficeAddinControl;

DUI_BEGIN_MESSAGE_MAP(OfficeAddinProgressFrame,CNotifyPump)
	DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
DUI_END_MESSAGE_MAP()

OfficeAddinProgressFrame::OfficeAddinProgressFrame(const std::wstring& path, const std::wstring& remoteParentPath, int64_t parentId, const std::wstring& id,const int32_t commonFolderEXType)
:path_(path)
,remoteParentPath_(remoteParentPath)
,id_(id)
,commonFolderEXType_(commonFolderEXType)
,remoteParentId_(parentId)
,userId_(INVALID_ID)
,m_stop_(TRUE)
{
}

OfficeAddinProgressFrame::~OfficeAddinProgressFrame()
{
}

void OfficeAddinProgressFrame::InitWindow()
{
	CLabelUI *name = static_cast<CLabelUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_NAME));
	if (NULL == name)
	{
		return;
	}
	name->SetText(Utility::FS::get_file_name(path_).c_str());

	languageID_ = iniLanguageHelper.GetLanguage();

	CLabelUI* title = static_cast<CLabelUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_NAME));
	if (NULL != title)
	{
		int nPos = path_.find_last_of('\\') ;
		std::wstring strFileName = path_.substr(nPos+1,path_.length() - nPos);
		std::wstring strTiltle = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY)
			+ L" "+ strFileName +  L" "+ iniLanguageHelper.GetCommonString(COMMOM_UPLOADTOONEBOX_KEY);
		title->SetText(strTiltle.c_str());
		title->SetToolTip(strTiltle.c_str());

		CHorizontalLayoutUI* pParent =  static_cast<CHorizontalLayoutUI*>(title->GetParent());
		if(NULL == pParent) return;
		pParent->SetToolTip(strTiltle.c_str());
	}

	userId_ = OfficeAddinThriftClient::getInstance()->getUserId();

	int32_t ret = addTask();
	if( RT_OK == ret )
	{
		CListUI* officeTaskList = static_cast<CListUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_LIST_PROGRESS));
		if(NULL == officeTaskList) return;
		officeTaskList->RemoveAll();
		int32_t index = 0;

		CDialogBuilder builder;
		m_PaintManager.SetResourcePath((GetInstallPath() + IniLanguageHelper(languageID_).GetSkinFolderPath() + L"Office\\").c_str());
		CShadeListContainerElement* item = static_cast<CShadeListContainerElement*>(
			builder.Create(L"OfficeUploadListItem.xml", L"", this, &m_PaintManager, NULL));
		if (NULL == item) return;
		officeTaskList->AddAt(item, index++);

		m_thread_refresh_ = boost::thread(boost::bind(&OfficeAddinProgressFrame::refreshTaskList, this));
	}
	else
	{
		Close(ret);
	}

}

CControlUI* OfficeAddinProgressFrame::CreateControl(LPCTSTR pstrClass)
{
	if (_tcscmp(pstrClass, _T("OfficeAddinProgressFrameListContainerElement")) == 0)
	{
		return new OfficeAddinProgressFrameListContainerElement;
	}
	else if (_tcscmp(pstrClass, _T("ShadeListContainerElement")) == 0)
	{
		return new CShadeListContainerElement;
	}
	return NULL;
}
	
bool OfficeAddinProgressFrame::InitLanguage(CControlUI* control)	
{
	return true;
}

void OfficeAddinProgressFrame::OnFinalMessage(HWND hWnd)
{
	WindowImplBase::OnFinalMessage(hWnd);
	delete this;
}

LPCTSTR OfficeAddinProgressFrame::GetWindowClassName(void) const
{
	return L"OneboxOfficeAddinProgress";
}

CDuiString OfficeAddinProgressFrame::GetSkinFolder()
{
	return (GetInstallPath()+IniLanguageHelper(languageID_).GetSkinFolderPath()+L"Office\\").c_str();
}

CDuiString OfficeAddinProgressFrame::GetSkinFile()
{
	return L"OfficeAddinProgressFrame.xml";
}

LRESULT OfficeAddinProgressFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	LRESULT lRes = 0;
	BOOL bHandled = TRUE;

	switch (uMsg)
	{
    case WM_TIMER:
        if ((UINT)TIMER_ID ==wParam)
        {
            closeNoticeFrame(lParam);
            break;
        }        
        break;
	case NOTIFY_MSG_TRANS_TASK_COMPLETE:
		{
			updateTaskStatus(ATS_Complete);
		}
		break;
	case NOTIFY_MSG_TRANS_TASK_ERROR:
		{
			updateTaskStatus( ATS_Error, lParam );
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

	return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
}

int32_t OfficeAddinProgressFrame::updateTaskStatus(const AsyncTransStatus status, int32_t errorCode)
{
	CListUI *progressList = static_cast<CListUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_LIST_PROGRESS));
	CHorizontalLayoutUI *progresslayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_PROGRESS_LAYOUT));
	CHorizontalLayoutUI *progressStat = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_PROGRESS_STAT));
	CHorizontalLayoutUI *noticeArea = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_NOTICEAREA));
	CLabelUI *fail = static_cast<CLabelUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_FAIL));
	CRichEditUI *noticeInfo = static_cast<CRichEditUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_INFO));
    CButtonUI *close = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_BUTTON_CLOSE));
	CHorizontalLayoutUI *oper = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_OPERATIONS));	
	CButtonUI *cancel = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_BTN_CANCL));
	if (NULL == progressList
		||NULL == progresslayout
		|| NULL == progressStat
		|| NULL == noticeArea
		|| NULL == oper 
		|| NULL == close
		|| NULL == noticeInfo
		|| NULL == fail
		|| NULL == cancel)
	{
		return RT_OK;
	}
	
	cancel->SetVisible(false);
	m_errorcode_ = errorCode;

	if(status == ATS_Complete)
	{	
		CShadeListContainerElement *pProgress = static_cast<CShadeListContainerElement*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_PROGRESS));
		if(pProgress)
		{
			pProgress->SetValue(100);
		}

		noticeArea->SetVisible(false);
		oper->SetVisible();
		
		progressStat->SetText(GetMessageFromIniFile(TRANSTASK_STATUS_UPLOADED).c_str());
		::SetTimer(this->GetHWND(),(UINT)TIMER_ID,1000,NULL);
	}
	else if(status == ATS_Error)
	{
		Close(errorCode);
	}
	else
	{
		noticeArea->SetVisible(false);
		oper->SetVisible(false);
		progresslayout->SetVisible(true);
		progressStat->SetVisible(false);

		SIZE size = m_PaintManager.GetClientSize();
		if (size.cy==OFFICEADDIN_ITEM_FRAME_MAX_SIZE)
		{
			size.cy -= OFFICEADDIN_ITEM_CHANGE_DISTANCE;
			this->ResizeClient(size.cx, size.cy);
		}
	}
	return RT_OK;
}

void OfficeAddinProgressFrame::OnClick(TNotifyUI& msg)
{
	if (msg.pSender->GetName() == ControlNames::OFFICEADDINPROGRESSFRAME_BUTTON_CLOSE)
	{
		Close(m_errorcode_);
	}
    else if (msg.pSender->GetName() == ControlNames::OFFICEADDINPROGRESSFRAME_BTN_CANCL)
    {
		if (RT_OK != OfficeAddinThriftClient::getInstance()->delTask(id_))
		{
			// show error message, del task failed
		}
		if (1==commonFolderEXType_)
		{
			SetUserConfValue(CONF_SETTINGS_SECTION,CONF_OFFICEONEBOXPATH_KEY,L"\\Office\\PC");
		}
		Close(m_errorcode_);
    }
}

void OfficeAddinProgressFrame::closeNoticeFrame(UINT nIDEvent)
{
	::KillTimer(this->GetHWND(),nIDEvent);
	m_thread_refresh_.interrupt();
	m_thread_refresh_.join();
	Close(m_errorcode_);
}

int32_t OfficeAddinProgressFrame::addTask()
{
	if (L"" == remoteParentPath_)
	{
		remoteParentId_ = ROOT_PARENTID;
		remoteParentPath_ = PATH_DELIMITER;
	}
	
	// create remote folder
	if (remoteParentId_ < 0)
	{
		bool failed = false;
		int64_t parentId= ROOT_PARENTID;
		std::vector<std::wstring> results;
		boost::split(results, remoteParentPath_, boost::is_any_of(L"\\"), boost::token_compress_on);
		for (std::vector<std::wstring>::iterator it = results.begin(); it != results.end(); ++it)
		{
			if (!it->empty())
			{
				File_Node fileNode = OfficeAddinThriftClient::getInstance()->createFolder(userId_, 1, parentId, *it,commonFolderEXType_);
				if (fileNode.id < 0)
				{
					failed = true;
					break;
				}
				parentId = fileNode.id;
			}
		}
		if (!failed)
		{
			remoteParentId_ = parentId;
		}
	}
	SetUserConfValue(CONF_SETTINGS_SECTION, CONF_OFFICEONEBOXPATH_ID_KEY, remoteParentId_);

	int32_t ret = OfficeAddinThriftClient::getInstance()->upload(path_, remoteParentId_, id_);
	m_errorcode_ = ret;

	return ret;
}

void OfficeAddinProgressFrame::refreshTaskList( )
{
	int iRefreshNumber = 5;
	while ( m_stop_ && iRefreshNumber > 0 )
	{
		boost::this_thread::sleep( boost::posix_time::seconds(1) );
		TransTask_RootNode transTaskNode;
		int32_t	ret = OfficeAddinThriftClient::getInstance()->getTask(id_, transTaskNode);
		if (RT_OK !=  ret)
		{
			// check the task is exist or not
			ret = OfficeAddinThriftClient::getInstance()->isTaskExist(id_);
			if (RT_SQLITE_NOEXIST == ret)
			{
				::SendMessage(this->GetHWND(), NOTIFY_MSG_TRANS_TASK_COMPLETE, 0, 0);
				OfficeAddinThriftClient::getInstance()->delTask(Utility::String::string_to_wstring(transTaskNode.group));
				break;
			}
			iRefreshNumber--;
			if ( 0 == iRefreshNumber )
			{
				::SendMessage(this->GetHWND(), NOTIFY_MSG_TRANS_TASK_ERROR, 0, HTTP_INTERNAL_ERROR);
			}
			continue;
		}

		iRefreshNumber = 5;

		if ( ATS_Error == transTaskNode.status
			|| ATS_Cancel == transTaskNode.status )
		{
			::SendMessage(this->GetHWND(), NOTIFY_MSG_TRANS_TASK_ERROR, 0, transTaskNode.errorCode);

			OfficeAddinThriftClient::getInstance()->delTask(Utility::String::string_to_wstring(transTaskNode.group));
			break;
		}
		else if( ATS_Complete == transTaskNode.status )
		{
			::SendMessage(this->GetHWND(), NOTIFY_MSG_TRANS_TASK_COMPLETE, 0, 0);
			OfficeAddinThriftClient::getInstance()->delTask(Utility::String::string_to_wstring(transTaskNode.group));
			break;
		}
		else
		{
			CShadeListContainerElement *pProgress = static_cast<CShadeListContainerElement*>(m_PaintManager.FindControl(ControlNames::OFFICEADDINPROGRESSFRAME_ITEM_PROGRESS));			
			if ( !pProgress ) continue;

			int32_t progressValue = (transTaskNode.size==0)?0:(int32_t)(((float)(transTaskNode.transedSize))/transTaskNode.size*100);
			pProgress->SetValue(progressValue);
		}
	}
}

