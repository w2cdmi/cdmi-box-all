#include "OutlookAddinFrame.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "Global.h"
#include "ErrorConfMgr.h"
#include "NoticeFrame.h"
#include "OutlookControlNames.h"

#ifdef _DEBUG
#pragma comment(lib, "DuiLib_d.lib")
#else
#pragma comment(lib, "DuiLib.lib")
#endif

#ifndef MODULE_NAME
#define MODULE_NAME ("OutlookaAddinFrame")
#endif

#define OUTLOOK_REMOTE_ROOT_NAME (L"Outlook Attachements")
#define WM_REFRESH_UI (WM_USER+1)

#define IC_OPER_TRANSFER_UPLOAD_PATH L"..\\Image\\ic_oper_transfer_upload.png"
#define IC_OPER_TRANSFER_DOWNLOAD_PATH L"..\\Image\\ic_oper_transfer_download.png"
#define IC_OPER_TRANSFER_UPLOAD_LINK L"..\\Image\\ic_transfer_open_cloudpath.png"
#define IC_OPER_TRANSFER_DOWNLOAD_LINK L"..\\Image\\ic_transfer_open_locallist.png"

#define STATUS_DESCRIBE_RUNNING_FORMAT L"{c #000000}{f 12}%s{/f}{/c}{c #008be8}{f 12} %d%%{/f}{/c}"
#define STATUS_DESCRIBE_ERROR_FILE_FORMAT L"{c #666666}{f 12}%s - {/f}{/c}{c #FF6B21}{f 12}%s{/f}{/c}"
#define STATUS_DESCRIBE_ERROR_DIR_FORMAT L"{c #666666}{f 12}%s - {/f}{/c}{c #FF6B21}{f 12}%lld%s{/f}{/c}"
#define STATUS_DESCRIBE_COMPLETE_FORMAT L"{c #666666}{f 12}%s{/f}{/c}{c #666666}{f 12}%d%%{/f}{/c}"
#define STATUS_DESCRIBE_INFO_FORMART L"{c #666666}{f 12}%s{/f}{/c}"

using namespace SD;
using namespace OutlookAddinControl;

static std::wstring getSizeStr(int64_t size)
{
	std::wstringstream stream;

	if(size<1024)
	{
		stream << size << L" B";
	}
	else if(size<1048576)
	{
		char pSize[10];
		float tempSize = float(size)/1024;
		sprintf_s(pSize, "%0.2f", tempSize);
		stream << pSize << L" KB";
	}
	else if(size<1073741824)
	{
		char pSize[10];
		float tempSize = float(size)/1048576;
		sprintf_s(pSize, "%0.2f", tempSize);
		stream << pSize << L" MB";
	}
	else if(size<1099511627776)
	{
		char pSize[10];
		float tempSize = float(size)/1073741824;
		sprintf_s(pSize, "%0.2f", tempSize);
		stream << pSize << L" GB";
	}
	else
	{
		char pSize[10];
		float tempSize = float(size)/1099511627776;
		sprintf_s(pSize, "%0.2f", tempSize);
		stream << pSize << L" TB";
	}
	return stream.str();
}

OutlookaAddinFrame::OutlookaAddinFrame(const std::wstring& emailId, updateShareLinkBodyCallback callback)
	:emailId_(emailId)
	,updateShareLinkBody_(callback)
	,outlookTable_(NULL)
	,isComplete_(false)
{
}

OutlookaAddinFrame::~OutlookaAddinFrame()
{
}

void OutlookaAddinFrame::InitWindow()
{
	HWND hParent = ::GetParent(*this);
	RECT rcParent, rc;
	if (NULL != hParent)
	{
		::GetWindowRect(hParent, &rcParent);
		::GetWindowRect(*this, &rc);
		::MoveWindow(*this, 1, 1, rc.right - rc.left, rc.bottom - rc.top, TRUE);
	}

	int64_t userId = OutlookAddinThriftClient::create()->getUserId();
	if (userId <= 0)
	{
		return;
	}
	std::wstring strUserId = Utility::String::type_to_string<std::wstring>(userId);
	std::wstring outlookTablePath = Utility::FS::get_system_user_app_path()
		+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+DEFAULT_USER_DATA_NAME+PATH_DELIMITER
		+strUserId+PATH_DELIMITER+strUserId+PATH_DELIMITER+TFN_OUTLOOK;
	outlookTable_.reset(new OutlookTable(outlookTablePath));
	if (NULL == outlookTable_.get())
	{
		return;
	}
	processThread_ = boost::thread(boost::bind(&OutlookaAddinFrame::processTasks, this));
}

CControlUI* OutlookaAddinFrame::CreateControl(LPCTSTR pstrClass)
{
	if (_tcscmp(pstrClass, _T("TransTaskListContainerElement")) == 0)
	{
		return new UITransTaskElement;
	}
	return NULL;
}

bool OutlookaAddinFrame::InitLanguage(CControlUI* control)	
{
	return true;
}

void OutlookaAddinFrame::OnFinalMessage(HWND hWnd)
{
	processThread_.interrupt();
	processThread_.join();
	WindowImplBase::OnFinalMessage(hWnd);
	delete this;
}

LPCTSTR OutlookaAddinFrame::GetWindowClassName(void) const
{
	return L"OutlookaAddinFrame";
}

CDuiString OutlookaAddinFrame::GetSkinFolder()
{
	return (GetInstallPath() + IniLanguageHelper(languageID_).GetSkinFolderPath()+L"Outlook\\").c_str();
}

CDuiString OutlookaAddinFrame::GetSkinFile()
{
	return L"OutlookAddin.xml";
}

LRESULT OutlookaAddinFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	if (uMsg == WM_REFRESH_UI)
	{
		refreshUI();
		return 0;
	}
	return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
}

LRESULT OutlookaAddinFrame::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
	return HTCLIENT;
}

void OutlookaAddinFrame::Notify(TNotifyUI& msg)
{
	if (msg.sType == DUI_MSGTYPE_CLICK)
	{
		std::wstring name = msg.pSender->GetName();
		if (name == L"transTask_item_pause")
		{
			return itemPauseClick(msg);
		}
		else if (name == L"transTask_item_resume")
		{
			return itemResumeClick(msg);
		}
		else if (name == L"transTask_item_delete")
		{
			return itemDeleteClick(msg);
		}
		else if (name == L"transTask_item_status_describe")
		{
			return itemErrorClick(msg);
		}
	}
}

int32_t OutlookaAddinFrame::addTask(std::list<std::wstring>& paths)
{
	boost::mutex::scoped_lock lock(mutex_);
	uploadPaths_.splice(uploadPaths_.end(), paths);
	isComplete_ = false;
	return RT_OK;
}

void OutlookaAddinFrame::processTasks()
{
	try
	{
		while (true)
		{
			boost::this_thread::sleep(boost::posix_time::seconds(2));

			boost::mutex::scoped_lock lock(mutex_);
			if (isComplete_)
			{
				continue;
			}

			// 1. if the uploadPaths_ is not empty, upload the files
			
			// create the remote parent folder
			std::shared_ptr<OutlookAddinThriftClient> client = OutlookAddinThriftClient::create();
			int64_t remoteParentId = INVALID_ID;
			std::wstring localPath = L"";
			std::wstring group = L"";			
			while (!uploadPaths_.empty())
			{
				isComplete_ = false;

				if (remoteParentId < 0)
				{
					remoteParentId = client->createRemoteFolder(ROOT_PARENTID, OUTLOOK_REMOTE_ROOT_NAME);
					if (remoteParentId < 0)
					{
						// post error message to UI
						// ...
						uploadPaths_.clear();
						break;
					}
				}
				
				group = Utility::String::gen_uuid();
				localPath = uploadPaths_.front();
				uploadPaths_.pop_front();

				// add data to outlook table
				OutlookNode outlookNode(new (std::nothrow)st_OutlookNode);
				if (NULL == outlookNode.get())
				{
					// post error message to UI
					// ...
					continue;
				}
				outlookNode->emailId = emailId_;
				outlookNode->group = group;
				outlookNode->localPath = localPath;
				outlookNode->shareLink = L"";
				if (RT_OK != outlookTable_->addNode(outlookNode))
				{
					// post error message to UI
					// ...
					continue;
				}

				// upload
				if (RT_OK != client->upload(localPath, remoteParentId, group))
				{
					// post error message to UI
					// ...
					continue;
				}
			}
			// 2. load the trans tasks
			OutlookNodes outlookUnShareLinkedNodes;
			(void)outlookTable_->getUnShareLinkNodes(emailId_, outlookUnShareLinkedNodes);
			std::list<std::wstring> groups;
			for (OutlookNodes::iterator it = outlookUnShareLinkedNodes.begin(); it != outlookUnShareLinkedNodes.end(); ++it)
			{
				groups.push_back((*it)->group);
			}
			transRootNodes_.clear();
			OutlookAddinThriftClient::create()->getTasks(groups, transRootNodes_);
			// 3. update the share link to Outlook UI
			OutlookNodes outlookSharedLinkedNodes;
			(void)outlookTable_->getShareLinkedNodes(emailId_, outlookSharedLinkedNodes);
			for (OutlookNodes::iterator it = outlookSharedLinkedNodes.begin(); it != outlookSharedLinkedNodes.end(); ++it)
			{
				ShareLinkBodyItem item;
				item.path = (*it)->localPath;
				item.shareLink = (*it)->shareLink;
				shareLinkBodyItems_.push_back(item);
			}

			if (outlookUnShareLinkedNodes.empty())
			{
				isComplete_ = true;
			}

			if (::IsWindow(m_hWnd))
			{
				PostMessage(WM_REFRESH_UI);
			}
		}
	}
	catch(boost::thread_interrupted) {}
}

void OutlookaAddinFrame::refreshUI()
{
	boost::mutex::scoped_lock lock(mutex_);
	TransTaskRootNodes transRootNodes;
	ShareLinkBodyItems shareLinkBodyItems;
	transRootNodes.splice(transRootNodes.end(), transRootNodes_);
	shareLinkBodyItems.splice(shareLinkBodyItems.end(), shareLinkBodyItems_);
	lock.unlock();

	CListUI* outlookTaskList = static_cast<CListUI*>(m_PaintManager.FindControl(ControlNames::TRANSTASK_LISTVIEW));
	if(NULL == outlookTaskList) return;
	outlookTaskList->RemoveAll();
	int32_t index = 0;

	for (TransTaskRootNodes::iterator it = transRootNodes.begin(); it != transRootNodes.end(); ++it)
	{
		CDialogBuilder builder;
		m_PaintManager.SetResourcePath((GetInstallPath() + IniLanguageHelper(languageID_).GetSkinFolderPath() + L"Outlook\\").c_str());
		UITransTaskElement* item = static_cast<UITransTaskElement*>(
			builder.Create(L"OutlookAddinListItem.xml", L"", this, &m_PaintManager, NULL));
		if (NULL == item) return;

		item->group = Utility::String::utf8_to_wstring(it->group);
		item->name = Utility::String::utf8_to_wstring(it->name);
		item->fileType = FILE_TYPE(it->fileType);
		item->status = AsyncTransStatus(it->status);
		item->size = it->size;
		item->transedSize = it->transedSize;
		item->errorCode = it->errorCode;

		// set icon
		CControlUI* icon = static_cast<CButtonUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_ICON));
		if (icon == NULL) return;
		std::wstring strIconPath = Onebox::SkinConfMgr::getInstance()->getIconPath(item->fileType, item->name);
		icon->SetBkImage(strIconPath.c_str());

		// set name
		CLabelUI* name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_NAME));
		if (name == NULL) return;
		name->SetText(item->name.c_str());

		// set size
		CLabelUI* size = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_SIZE));
		if (size == NULL) return;
		size->SetText(SD::Utility::String::format_string(L"%s / %s", 
			getSizeStr(item->transedSize).c_str(), 
			getSizeStr(item->size).c_str()).c_str());

		// set button status
		setDescribeForStatus(item);

		setItemResumeAndPauseButtonStatus(item);

		outlookTaskList->AddAt(item, index++);
	}

	// update email body
	updateShareLinkBody_(shareLinkBodyItems);
}

void OutlookaAddinFrame::setDescribeForStatus(UITransTaskElement* item)
{
	if (NULL == item) return;

	// set progress
	int32_t iProgress = (item->size==0) ? 0:(int32_t)(((float)item->transedSize)/item->size*100);
	if (iProgress >= 0 && iProgress <= 100)
	{
		item->SetValue(iProgress);
	}

	CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
	if (NULL == clbstatus_describe) return;
	clbstatus_describe->SetShowHtml(true);	

	std::wstring status_describe = L"";
	CButtonUI* btntranstype = static_cast<CButtonUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_TYPE));
	btntranstype->SetBkImage(IC_OPER_TRANSFER_UPLOAD_PATH);
	switch ( item->status )
	{
	case ATS_Running:
		{
			status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADING);
			clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_RUNNING_FORMAT, 
				status_describe.c_str(), iProgress).c_str());
			break;
		}
	case ATS_Complete:
		{
			status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADED);
			clbstatus_describe->SetText(status_describe.c_str());
			break;
		}
	case ATS_Error:
		{
			setErrorInfo(item);
			break;
		}
	case ATS_Cancel:
		{
			status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOAD_PAUSED);
			clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
				status_describe.c_str()).c_str());
			break;
		}
	case ATS_Waiting:
		{
			status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOAD_WAITING);
			clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
				status_describe.c_str()).c_str());
			break;
		}
	default:
		break;
	}
}

void OutlookaAddinFrame::setErrorInfo(UITransTaskElement* item)
{
	if (NULL == item) return;
	std::wstring status_describe = L"";
	CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
	if (!clbstatus_describe) return;

	clbstatus_describe->SetEnabled();
	clbstatus_describe->SetShowHtml(true);

	status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADED);
	clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_ERROR_FILE_FORMAT, 
		status_describe.c_str(), iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_FAILED).c_str()).c_str());
	clbstatus_describe->SetToolTip(Onebox::ErrorConfMgr::getInstance()->getDescription(item->errorCode).c_str());
}

void OutlookaAddinFrame::itemPauseClick(TNotifyUI& msg)
{
	if (NULL==msg.pSender->GetParent()) return;
	else if (NULL == msg.pSender->GetParent()->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;
	UITransTaskElement* pItem = static_cast<UITransTaskElement*>(
		msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
	if(pItem==NULL) return;

	//1.pause the task
	int32_t ret = OutlookAddinThriftClient::create()->pauseTask(pItem->group);
	if(ret != RT_OK)
	{
		// show error message
		// ...
		return;
	}
	//2.set the button status
	pItem->status = ATS_Cancel;
	setDescribeForStatus(pItem);
	setItemResumeAndPauseButtonStatus(pItem);
}

void OutlookaAddinFrame::itemResumeClick(TNotifyUI& msg)
{
	if (NULL == msg.pSender->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()->GetParent()) return;
	else if(NULL ==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;
	UITransTaskElement* pItem = static_cast<UITransTaskElement*>(
		msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
	if(pItem==NULL) return;

	//1.resume the task
	int32_t ret = OutlookAddinThriftClient::create()->resumeTask(pItem->group);
	if(ret != RT_OK)
	{
		// show error message
		// ...
		return;
	}
	//2.set the button status
	pItem->status = ATS_Waiting;
	setDescribeForStatus(pItem);
	setItemResumeAndPauseButtonStatus(pItem);
}

void OutlookaAddinFrame::itemDeleteClick(TNotifyUI& msg)
{
	if (NULL == msg.pSender->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()->GetParent()) return;
	else if (NULL ==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

	UITransTaskElement* pItem = static_cast<UITransTaskElement*>(
		msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
	if(pItem==NULL) return;

	//1.delete the task
	int32_t ret = OutlookAddinThriftClient::create()->delTask(pItem->group);
	if(ret != RT_OK)
	{
		// show error message
		// ...
		//return;
	}
	//2.delete outlook table
	ret = outlookTable_->deleteNode(pItem->group);
	if(ret != RT_OK)
	{
		// show error message
		// ...
		//return;
	}
	//3. remove item from UI
	CListUI* outlookTaskList = static_cast<CListUI*>(m_PaintManager.FindControl(ControlNames::TRANSTASK_LISTVIEW));
	if(NULL == outlookTaskList) return;
	outlookTaskList->RemoveAt(pItem->GetIndex());
}

void OutlookaAddinFrame::itemErrorClick(TNotifyUI& msg)
{
	boost::mutex::scoped_lock lock(mutex_);
	if (NULL == msg.pSender->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()) return;
	else if(NULL == msg.pSender->GetParent()->GetParent()->GetParent()) return;

	UITransTaskElement* pItem = static_cast<UITransTaskElement*>(
		msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
	if(pItem==NULL) return;

	if( RT_OK == pItem->errorCode ) return;

	std::wstring sadvice = Onebox::ErrorConfMgr::getInstance()->getAdvice(pItem->errorCode);
	if( !sadvice.empty() )
	{
		Onebox::NoticeImpl::getInstance(NULL)->showModal(Onebox::Confirm,Onebox::Warning,L"Onebox"
			,MSG_ERROR_KEY
			,pItem->errorCode
			,Onebox::ErrorConfMgr::getInstance()->getDescription(pItem->errorCode).c_str()
			,sadvice.c_str());
	}
	else
	{
		Onebox::NoticeImpl::getInstance(NULL)->showModal(Onebox::Confirm,Onebox::Warning,L"Onebox"
			,MSG_ERROR_NO_ADVICE_KEY
			,pItem->errorCode
			,Onebox::ErrorConfMgr::getInstance()->getDescription(pItem->errorCode).c_str());
	}
}

bool OutlookaAddinFrame::isComplete()
{
	boost::mutex::scoped_lock lock(mutex_);
	return isComplete_;
}

void OutlookaAddinFrame::emailSendEvent()
{
	return emailDeleteEvent();
}

void OutlookaAddinFrame::emailDeleteEvent()
{
	// when delete the email should delete all the tasks and nodes in outlook table
	OutlookNodes outlookNodes;
	(void)outlookTable_->getUnShareLinkNodes(emailId_, outlookNodes);
	std::shared_ptr<OutlookAddinThriftClient> client = OutlookAddinThriftClient::create();
	for (OutlookNodes::iterator it = outlookNodes.begin(); it != outlookNodes.end(); ++it)
	{
		(void)client->delTask((*it)->group);
	}
	(void)outlookTable_->deleteNodes(emailId_);

	// delete the UI
	CListUI* outlookTaskList = static_cast<CListUI*>(m_PaintManager.FindControl(L"transTask_listView"));
	if (NULL == outlookTaskList) return;
	outlookTaskList->RemoveAll();
}

void OutlookaAddinFrame::setItemResumeAndPauseButtonStatus(UITransTaskElement* item)
{
	if (NULL == item) return;
	CButtonUI* btnItemResume = static_cast<CButtonUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_RESUME));
	if (NULL == btnItemResume) return;
	CButtonUI* btnItemPause = static_cast<CButtonUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_PAUSE));
	if (NULL == btnItemPause) return;
	CButtonUI* btnItemDescribe = static_cast<CButtonUI*>(m_PaintManager.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
	if (btnItemDescribe == NULL) return;

	if (item->status&ATS_Error)
	{				
		btnItemDescribe->SetMouseEnabled(true);
		btnItemPause->SetVisible(false);
		btnItemResume->SetVisible(true);
		btnItemPause->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_PAUSEUPLOAD_KEY).c_str());
	}
	else if (ATS_Cancel == item->status 
		|| ATS_Complete == item->status 
		|| ATS_Waiting == item->status)
	{
		btnItemDescribe->SetMouseEnabled(false);
		btnItemResume->SetVisible(true);
		btnItemPause->SetVisible(false);
		btnItemResume->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_STARTUPLOAD_KEY).c_str());
	}
}
