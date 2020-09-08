#include "OfficeAddinThriftClient.h"
#include "OfficeAddinFrame.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "NotifyMgr.h"
#include "Global.h"

#ifdef _DEBUG
#pragma comment(lib, "DuiLibLib_d.lib")
#else
#pragma comment(lib, "DuiLibLib.lib")
#endif

#ifndef MODULE_NAME
#define MODULE_NAME ("OfficeAddinFrame")
#endif

#define WM_LOAD_TASK (WM_USER+1)

#define TIMER_ID (1)

using namespace SD;

DUI_BEGIN_MESSAGE_MAP(OfficeAddinFrame,CNotifyPump)
	DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
DUI_END_MESSAGE_MAP()

OfficeAddinFrame::OfficeAddinFrame(const std::wstring& path)
	:path_(path)
	,id_(L"")
{
}

OfficeAddinFrame::~OfficeAddinFrame()
{
}

void OfficeAddinFrame::InitWindow()
{
	(void)OfficeAddinThriftClient::getInstance()->addNotify((int64_t)GetHWND());
	
	// initial UI
	CLabelUI *name = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_name"));
	if (NULL == name)
	{
		return;
	}
	name->SetText(Utility::FS::get_file_name(path_).c_str());
	
	boost::thread(boost::bind(&OfficeAddinFrame::addTask, this));
}

CControlUI* OfficeAddinFrame::CreateControl(LPCTSTR pstrClass)
{
	if (_tcscmp(pstrClass, _T("OfficeAddinFrameListContainerElement")) == 0)
	{
		return new OfficeAddinFrameListContainerElement;
	}
	return NULL;
}
	
bool OfficeAddinFrame::InitLanguage(CControlUI* control)	
{
	return true;
}

void OfficeAddinFrame::OnFinalMessage(HWND hWnd)
{
    (void)OfficeAddinThriftClient::getInstance()->removeNotify((int64_t)GetHWND());
	WindowImplBase::OnFinalMessage(hWnd);
	delete this;
}

LPCTSTR OfficeAddinFrame::GetWindowClassName(void) const
{
	return L"OneboxOfficeAddinProgress";
}

CDuiString OfficeAddinFrame::GetSkinFolder()
{
	return (GetInstallPath()+IniLanguageHelper(languageID_).GetSkinFolderPath()+L"Office\\").c_str();
}

CDuiString OfficeAddinFrame::GetSkinFile()
{
	return L"OfficeAddin.xml";
}

LRESULT OfficeAddinFrame::OnHandleCopyData(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	bHandled = TRUE;

	COPYDATASTRUCT* copyData = (COPYDATASTRUCT*)lParam;
	if (NULL == copyData || copyData->cbData < (6*FS_MAX_PATH_W*sizeof(wchar_t)))
	{
		return 0;
	}
	NOTIFY_PARAM param;
	param.type = copyData->dwData;
	std::shared_ptr<wchar_t> buf(new wchar_t[FS_MAX_PATH_W]);
	wcscpy_s(buf.get(), FS_MAX_PATH_W, (wchar_t*)copyData->lpData);
	param.msg1 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W);
	param.msg2 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*2);
	param.msg3 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*3);
	param.msg4 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*4);
	param.msg5 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*5);
	param.msg6 = buf.get();
	wcscpy_s(buf.get(), FS_MAX_PATH_W, ((wchar_t*)copyData->lpData)+FS_MAX_PATH_W*6);
	param.msg7 = buf.get();

	switch (param.type)
	{
	case NOTIFY_MSG_TRANS_TASK_COMPLETE:
		updateTaskStatus(ATS_Complete);
		break;
	case NOTIFY_MSG_TRANS_TASK_UPDATE:
		updateTask(Utility::String::string_to_type<int64_t>(param.msg5), 
			Utility::String::string_to_type<int64_t>(param.msg6), 
			Utility::String::string_to_type<int64_t>(param.msg7));
		break;
    case NOTIFY_MSG_TRANS_TASK_ERROR:
        updateTaskStatus(ATS_Error);
		break;
	default:
		break;
	}
	return 0;
}

LRESULT OfficeAddinFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	LRESULT lRes = 0;
	BOOL bHandled = TRUE;

	switch (uMsg)
	{
	case WM_LOAD_TASK:
		loadTask();
		break;
	case WM_COPYDATA:
		lRes = OnHandleCopyData(uMsg, wParam, lParam, bHandled);
		break;
    case WM_TIMER:
        if ((UINT)TIMER_ID ==wParam)
        {
            closeNoticeFrame(wParam);
            break;
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

int32_t OfficeAddinFrame::updateTaskStatus(const AsyncTransTaskStatus status)
{
	CProgressUI *progress = static_cast<CProgressUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_progress"));
	CLabelUI *success = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_success"));
	CLabelUI *fail = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_fail"));
	CHorizontalLayoutUI *oper = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_operations"));
	if (NULL == progress || NULL == success || NULL == fail || NULL == oper)
	{
		return RT_OK;
	}

	if(status == ATS_Complete)
	{
		progress->SetVisible(false);
		fail->SetVisible(false);
		success->SetVisible(true);
		success->SetText(GetMessageFromIniFile(OFFICEADDIN_ITEM_SUCCESS_TXT).c_str());
		::SetTimer(this->GetHWND(),(UINT)TIMER_ID,5000,NULL);
	}
	else if(status == ATS_Error)
	{
		oper->SetVisible();
		progress->SetVisible(false);
		success->SetVisible(false);
		fail->SetVisible(true);
		fail->SetText(GetMessageFromIniFile(OFFICEADDIN_ITEM_FILE_TXT).c_str());

		SIZE size = m_PaintManager.GetClientSize();
		size.cy += 50;
		this->ResizeClient(size.cx, size.cy);
	}
	else
	{
		success->SetVisible(false);
		fail->SetVisible(false);
		oper->SetVisible(false);
		progress->SetVisible(true);

		SIZE size = m_PaintManager.GetClientSize();
		size.cy -= 50;
		this->ResizeClient(size.cx, size.cy);
	}
	return RT_OK;
}

int32_t OfficeAddinFrame::updateTask(const int64_t transedSize, const int64_t size, const int64_t speed)
{
	CProgressUI *progress = static_cast<CProgressUI*>(m_PaintManager.FindControl(L"OfficeAddin_item_progress"));
	if (NULL == progress)
	{
		return RT_OK;
	}
	int32_t progressValue = (size==0)?100:(int32_t)(((float)(transedSize))/size*100);
	progress->SetValue(progressValue);
	if (100 == progressValue)
	{
		updateTaskStatus(ATS_Complete);
	}
    
	return RT_OK;
}

void OfficeAddinFrame::addTask()
{
	std::list<std::wstring> paths;
	paths.push_back(path_);

	std::wstring tmpTime = Utility::DateTime(time(NULL), Utility::Crt).getTime();
	std::wstring::size_type pos = tmpTime.find(L" ");
	std::wstring parentPath = L"\\Office\\PC\\"+Utility::String::replace_all(tmpTime.substr(0, pos), L"/", L"");
	id_ =  Utility::String::gen_uuid();
	int32_t ret = OfficeAddinThriftClient::getInstance()->upload(paths, parentPath, OfficeAddinThriftClient::getInstance()->getUserId(), id_);
	if(ret != RT_OK)
	{
		// show error message
		// ...
		return;
	}
	if (::IsWindow(m_hWnd))
	{
		::PostMessage(m_hWnd, WM_LOAD_TASK, NULL, NULL);
	}
}

void OfficeAddinFrame::loadTask()
{
	std::vector<TransTask_Node> nodes;
	if (RT_OK != OfficeAddinThriftClient::getInstance()->getTasks(id_, nodes) || 
		nodes.empty())
	{
		// show error message, add task failed
		// ...
		return;
	}

	TransTask_Node node = nodes.front();
    taskNode_.id.id = SD::Utility::String::utf8_to_wstring(node.id.id);
    taskNode_.id.group=SD::Utility::String::utf8_to_wstring(node.id.group);
    taskNode_.id.type=node.id.type;
    taskNode_.id.userId=node.id.userId;
    taskNode_.id.rowId = node.id.rowId;
    taskNode_.type=FILE_TYPE_FILE;
    taskNode_.name=SD::Utility::String::utf8_to_wstring(node.name);
    taskNode_.path=SD::Utility::String::utf8_to_wstring(node.id.path);
    taskNode_.userDefine=SD::Utility::String::utf8_to_wstring(node.userDefine);
	taskNode_.info.transedSize = node.transedSize;
	taskNode_.info.totalSize = node.size;
}

void OfficeAddinFrame::OnClick(TNotifyUI& msg)
{
    if (msg.pSender->GetName() == L"OfficeAddin_btn_cancl")
    {
		if (RT_OK != OfficeAddinThriftClient::getInstance()->delTask(id_))
		{
			// show error message, del task failed
		}
		Close();
    }
    else if (msg.pSender->GetName() == L"OfficeAddin_btn_retry")
    {
		TransTask_Id transTaskId;
		transTaskId.id=SD::Utility::String::wstring_to_utf8(taskNode_.id.id);
		transTaskId.group=SD::Utility::String::wstring_to_utf8(taskNode_.id.group);
		transTaskId.userId=taskNode_.id.userId;
		transTaskId.rowId=taskNode_.id.rowId;
		transTaskId.path=SD::Utility::String::wstring_to_utf8(taskNode_.path);

		if (RT_OK != OfficeAddinThriftClient::getInstance()->resumeTask(transTaskId))
		{
			// show error message, del task failed
		}
		updateTaskStatus(ATS_Waiting);
    }
}

void OfficeAddinFrame::closeNoticeFrame(UINT nIDEvent)
{
    Close();
    ::KillTimer(this->GetHWND(),nIDEvent);
}
