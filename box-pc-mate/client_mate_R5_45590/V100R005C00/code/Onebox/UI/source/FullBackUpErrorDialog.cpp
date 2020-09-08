#include "stdafxOnebox.h"
#include "FullBackUpErrorDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "ListContainerElement.h"
#include "InILanguage.h"
#include "NotifyMgr.h"
#include "ErrorConfMgr.h"
#include "UserContext.h"
#include "SkinConfMgr.h"
#include <boost/thread.hpp>
#include "FullBackUpMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("FullBackupErrorDilog")
#endif

namespace Onebox
{	
	FullBackUpErrorDialog::FullBackUpErrorDialog(UserContext* context)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pExportBtn(NULL)
		,m_pRetryBtn(NULL)
		,m_pCancelBtn(NULL)
		,list_(NULL)
		,m_pCountLab(NULL)
		,m_noticeFrame_(NULL)
	{
		m_errorCount = 0;
	}

	FullBackUpErrorDialog::~FullBackUpErrorDialog()
	{
		if(m_noticeFrame_)
		{
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;
		}
	}

	CDuiString FullBackUpErrorDialog::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString FullBackUpErrorDialog::GetSkinFile()
	{
		return L"FullBackUpErrorDialog.xml";
	}

	LPCTSTR FullBackUpErrorDialog::GetWindowClassName(void) const
	{
		return L"FullBackUpErrorDialog";
	}

	CControlUI* FullBackUpErrorDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool FullBackUpErrorDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}
	
	void FullBackUpErrorDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			std::wstring name = msg.pSender->GetName().GetData();
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn)
			{
				if (list_ != NULL) list_->RemoveAll();
				Close();
				return; 
			}
			else if (msg.pSender == m_pExportBtn)
			{
				boost::thread(boost::bind(&FullBackUpErrorDialog::ExportErrorList, this));
			}
			else if (msg.pSender == m_pRetryBtn)
			{
				Close();
				BackupAllMgr::getInstance(userContext_)->restartBackupTask();
				return;
			}
			if (name == L"fullBackupError_item_filePath")
			{
				if (NULL == msg.pSender->GetParent()) return;
				if (NULL == msg.pSender->GetParent()->GetParent()) return;
				FullBackUpErrorListContainerElement* pItem = static_cast<FullBackUpErrorListContainerElement*>(msg.pSender->GetParent()->GetParent());
				if (NULL == pItem) return;
				std::wstring tip = pItem->nodeData.baseInfo.path;
				if (tip.size() != 0)
				{
					if (!PathFileExists(tip.c_str()))
					{
						m_noticeFrame_->Run(Confirm,Info,L"",MSG_TRANSERROR_NOEXIT_KEY,Modal);
						return;
					}
					std::wstring pathDir = L" /select,";
					pathDir += tip;										
					(void)ShellExecute(NULL,L"open",L"explorer.exe",pathDir.c_str(),NULL,SW_SHOWNORMAL);
				}
			}
			else if (name == L"fullBackupError_item_delete")
			{
				DeleteBtnClick(msg);
			}
			else if(name == L"FullBackUpError_max_btn")
			{ 
				SendMessage(WM_SYSCOMMAND, SC_MAXIMIZE, 0); 
				return;
			}
			else if(name == L"FullBackUpError_restore_btn")
			{ 
				SendMessage(WM_SYSCOMMAND, SC_RESTORE, 0); 
				return; 
			}
		}
	}

	LRESULT FullBackUpErrorDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;
		if (uMsg == WM_SYSCOMMAND)
		{
			if( wParam == SC_CLOSE )
			{
				Close();
				return 0;
			}

			BOOL bZoomed = ::IsZoomed(*this);
			LRESULT lRes = CWindowWnd::HandleMessage(uMsg, wParam, lParam);
			if( ::IsZoomed(*this) != bZoomed ) 
			{
				CControlUI* _maxBtn = static_cast<CControlUI*>(m_PaintManager.FindControl(L"FullBackUpError_max_btn"));
				CControlUI* _restoreBtn = static_cast<CControlUI*>(m_PaintManager.FindControl(L"FullBackUpError_restore_btn"));
				if (NULL == _maxBtn || NULL == _restoreBtn) return lRes;
				_maxBtn->SetVisible(TRUE == bZoomed);
				_restoreBtn->SetVisible(FALSE == bZoomed);
			}
		}
		else
		{
			bHandled = FALSE;
		}
		if (bHandled) return lRes;
		return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
	}

	void FullBackUpErrorDialog::InitWindow()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"FullBackUpError_close_btn"));
		m_pExportBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"FullBackUpError_export"));
		m_pCancelBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"FullBackUpError_cancel"));
		list_ = static_cast<CListUI*>(m_PaintManager.FindControl(L"FullBackUpError_ListView"));
		m_pRetryBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"FullBackUpError_retry"));
		m_pCountLab = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"FullBackUpError_count"));
		m_noticeFrame_ = new NoticeFrameMgr(m_hWnd);
		InitData();
	}

	void FullBackUpErrorDialog::InitData()
	{
		if (NULL == list_)  return;
		BATaskLocalNodeList failedList;
		BackupAllMgr::getInstance(userContext_)->getFailedList(failedList, 0, 100);
		for (BATaskLocalNodeList::iterator itor = failedList.begin();itor != failedList.end(); itor++)
		{
			addItem(*itor);
		}		
		BATaskInfo* taskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(taskInfo)
		{
			m_errorCount = taskInfo->failedCnt;
		}
		if (NULL != m_pCountLab)
		{
			std::wstring ErrtypeDes = iniLanguageHelper.GetCommonString(BACKUPERROR_TEXT_KEY);
			std::wstringstream str_text;
			str_text << L"{f 13}{c #000000}";
			if (m_errorCount > 99)
			{
				str_text << L"99+";
			} 
			else
			{
				str_text << list_->GetCount();
			}
			str_text << L"{/c}{/f} {c #666666}"	<< ErrtypeDes << L"{/c}";
			m_pCountLab->SetShowHtml();
			m_pCountLab->SetText(str_text.str().c_str());
		}
	}

	void FullBackUpErrorDialog::addItem(BATaskLocalNode node)
	{
		bool isExit=true;
		if (NULL == list_)  return;	
		CDialogBuilder builder;
		FullBackUpErrorListContainerElement* item = static_cast<FullBackUpErrorListContainerElement*>(
			builder.Create(L"FullBackupErrorListItem.xml", L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager));		
		if (NULL == item)  return;
		item->nodeData = node;		

		CLabelUI* pIcon = static_cast<CLabelUI*>(item->FindSubControl(L"fullBackupError_item_fileIcon"));
		if (pIcon != NULL)
		{
			std::wstring strIconPath = SkinConfMgr::getInstance()->getIconPath(node.baseInfo.type, SD::Utility::FS::get_file_name(node.baseInfo.path));
			pIcon->SetBkImage(strIconPath.c_str());
		}

		CLabelUI* localName = static_cast<CLabelUI*>(item->FindSubControl(L"fullBackupError_item_fileName"));
		if (localName != NULL)
		{			
			localName->SetText(SD::Utility::FS::is_local_root(node.baseInfo.path) ? node.baseInfo.path.substr(0,1).c_str():SD::Utility::FS::get_file_name(node.baseInfo.path).c_str());
			localName->SetToolTip(SD::Utility::FS::is_local_root(node.baseInfo.path) ? node.baseInfo.path.substr(0,1).c_str():SD::Utility::FS::get_file_name(node.baseInfo.path).c_str()); 
		}
		
		CButtonUI* localPath = static_cast<CButtonUI*>(item->FindSubControl(L"fullBackupError_item_filePath"));
		if (localPath != NULL)
		{
			std::wstring str_path = iniLanguageHelper.GetMsgDesc(TRANSTASK_MSG_OPEN_SOURCE);
			str_path += L" ";
			str_path += SD::Utility::FS::get_parent_path(node.baseInfo.path).c_str();
			str_path +=L"\\";
			localPath->SetToolTip(str_path.c_str());
		}

		CLabelUI* errorDesc = static_cast<CLabelUI*>(item->FindSubControl(L"fullBackupError_item_desc"));
		if (errorDesc != NULL)
		{
			std::wstring desc;
			if (node.baseInfo.opType&BAO_Create)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_CREATE_KEY).c_str();
			}
			else if (node.baseInfo.opType&BAO_Rename)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_RENAME_KEY).c_str();
			}
			else if (node.baseInfo.opType&BAO_Move)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_MOVE_KEY).c_str();
			}
			else if (node.baseInfo.opType&BAO_Upload)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_UPLOAD_KEY).c_str();
			}
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_VALUE_KEY).c_str();
			desc += SD::Utility::String::type_to_string<std::wstring,int32_t>(node.errorCode);
			desc += L" ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_CAUSE_KEY).c_str();
			desc += ErrorConfMgr::getInstance()->getDescription(node.errorCode);
			if (ErrorConfMgr::getInstance()->getAdvice(node.errorCode) != L"")
			{
				desc += L" ";
				desc += iniLanguageHelper.GetCommonString(BACKUPERROR_PROPOSE_KEY).c_str();
				desc += ErrorConfMgr::getInstance()->getAdvice(node.errorCode);
			}
			errorDesc->SetText(desc.c_str());
			errorDesc->SetToolTip(desc.c_str());
		}
		list_->Add(item);
	}

	LRESULT FullBackUpErrorDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient = {0,0,0,0};
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_PaintManager.GetCaptionRect();

		if (-1 == rcCaption.bottom)
		{
			rcCaption.bottom = rcClient.bottom;
		}

		if( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right \
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom )
		{
				CControlUI* pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(pt));
				if( pControl )
				{
					if (0 == _tcsicmp(pControl->GetClass(), _T("LabelUI")) ||
						0 == _tcsicmp(pControl->GetClass(), _T("ControlUI")) ) 
						return HTCAPTION;
				}
		}
		return HTCLIENT;
	}

	void FullBackUpErrorDialog::ExportErrorList()
	{
		m_pExportBtn->SetEnabled(false);
		//csv
		OPENFILENAME ofn;
		ZeroMemory(&ofn,sizeof(ofn));
		ofn.lStructSize = sizeof(ofn);
		WCHAR defaultPath[MAX_PATH];
		wcscpy_s(defaultPath,MAX_PATH,(SD::Utility::FS::get_work_directory() + L"\\fullBackupErrorList").c_str());
		ofn.lpstrFile = defaultPath;
		ofn.nMaxFile = MAX_PATH;
		ofn.lpstrFilter = TEXT("csv files\0*.csv\0");
		ofn.nFilterIndex = 0;
		ofn.lpstrFileTitle = NULL;
		ofn.nMaxFileTitle = 0;
		ofn.Flags = OFN_EXPLORER | OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST;
		if (!GetSaveFileName(&ofn)) 
		{
			m_pExportBtn->SetEnabled();
			return;
		}
		std::wstring strRealPath = defaultPath;
		if (0 != SD::Utility::String::to_lower(strRealPath.substr(strRealPath.length() -4,4)).compare(L".csv"))
		{
			wcscat_s(defaultPath,MAX_PATH,L".csv");
		}

		BATaskLocalNodeList failedList;
		BackupAllMgr::getInstance(userContext_)->getFailedList(failedList, 0, -1);
		std::FILE* fp;
		_wfopen_s(&fp,defaultPath,L"w,ccs=utf-8");
		if (NULL == fp) 
		{
			m_pExportBtn->SetEnabled();
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Export errorList failed.");
			return;
		}

		for (BATaskLocalNodeList::iterator itor = failedList.begin();itor != failedList.end(); itor++)
		{
			std::wstring desc;
			if (itor->baseInfo.opType&BAO_Create)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_CREATE_KEY).c_str();
			}
			else if (itor->baseInfo.opType&BAO_Rename)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_RENAME_KEY).c_str();
			}
			else if (itor->baseInfo.opType&BAO_Move)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_MOVE_KEY).c_str();
			}
			else if (itor->baseInfo.opType&BAO_Upload)
			{
				desc = iniLanguageHelper.GetCommonString(BACKUPERROR_UPLOAD_KEY).c_str();
			}
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_VALUE_KEY).c_str();
			desc += SD::Utility::String::type_to_string<std::wstring,int32_t>(itor->errorCode);
			desc += L" ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_CAUSE_KEY).c_str();
			desc += ErrorConfMgr::getInstance()->getDescription(itor->errorCode);
			if (ErrorConfMgr::getInstance()->getAdvice(itor->errorCode) != L"")
			{
				desc += L" ";
				desc += iniLanguageHelper.GetCommonString(BACKUPERROR_PROPOSE_KEY).c_str();
				desc += ErrorConfMgr::getInstance()->getAdvice(itor->errorCode);
			}
			fputws((+ L"\""+SD::Utility::FS::get_file_name(itor->baseInfo.path)+ L"\",\"" +SD::Utility::FS::get_parent_path(itor->baseInfo.path)+L"\","+ (itor->baseInfo.type?SD::Utility::String::getSizeStr(SD::Utility::FS::get_file_size(itor->baseInfo.path)):L"--")+L",\""+desc+L"\"\n").c_str(),fp);
		}
		fclose(fp);
		m_pExportBtn->SetEnabled();
	}

	void FullBackUpErrorDialog::DeleteBtnClick(DuiLib::TNotifyUI& msg)
	{
		if (NULL == msg.pSender->GetParent())return;
		if (NULL == msg.pSender->GetParent()->GetParent())return;
		FullBackUpErrorListContainerElement* pItem = static_cast<FullBackUpErrorListContainerElement*>(msg.pSender->GetParent()->GetParent());
		if (NULL == pItem) return;

		NoticeFrameMgr* noticeFrame_ = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
		noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_BACKUP_DELCONFIRM_NOTICE_KEY,Modal);
		bool bIsClickOk =  noticeFrame_->IsClickOk();
		delete noticeFrame_;
		noticeFrame_ = NULL;
		if (!bIsClickOk)  return;

		if (RT_OK == BackupAllMgr::getInstance(userContext_)->ignoreFailedNode(pItem->nodeData))
		{
			m_errorCount--;
			list_->Remove(pItem);
			if (NULL != m_pCountLab)
			{
				std::wstring ErrtypeDes = iniLanguageHelper.GetCommonString(BACKUPERROR_TEXT_KEY);
				std::wstringstream str_text;
				str_text << L"{f 13}{c #000000}";
				if (m_errorCount > 99)
				{
					str_text << L"99+";
				} 
				else
				{
					str_text << list_->GetCount();
				}
				str_text << L"{/c}{/f} {c #666666}"	<< ErrtypeDes << L"{/c}";
				m_pCountLab->SetShowHtml();
				m_pCountLab->SetText(str_text.str().c_str());
			}
			FullBackUpMgr* fullBackUpMgr_ = FullBackUpMgr::getInstance(userContext_,m_PaintManager);
			if (NULL == fullBackUpMgr_)return;
			if (0 == m_errorCount)
			{
				Close();
				fullBackUpMgr_->initData();
			}
			else
			{
				fullBackUpMgr_->updateErrorCount(m_errorCount);
			}
		}
	}
}
