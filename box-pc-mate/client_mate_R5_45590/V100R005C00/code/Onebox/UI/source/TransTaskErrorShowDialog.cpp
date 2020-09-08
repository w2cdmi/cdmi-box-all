#include "stdafxOnebox.h"
#include "SyncFileSystemMgr.h"
#include "NotifyMgr.h"
#include "TransTaskErrorShowDialog.h"
#include "CustomListUI.h"
#include "DialogBuilderCallbackImpl.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "TeamSpaceResMgr.h"
#include "PathMgr.h"
#include "ProxyMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "ListContainerElement.h"
#include "ErrorConfMgr.h"
#include "UserContext.h"
#include "AsyncTaskMgr.h"
#include "AsyncTaskCommon.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTaskErrorShowDilog")
#endif

namespace Onebox
{	
	TransTaskShowErrorDialog::TransTaskShowErrorDialog(UserContext* context,std::wstring& id,FILE_TYPE fileType)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,list_(NULL)
		,m_id(id)
		,m_fileType(fileType)
		,m_pRetryBtn(NULL)
		,m_pCountLab(NULL)
		,m_noticeFrame_(NULL)
	{
	}

	TransTaskShowErrorDialog::~TransTaskShowErrorDialog()
	{
		if(m_noticeFrame_)
		{
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;
		}
	}

	CDuiString TransTaskShowErrorDialog::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString TransTaskShowErrorDialog::GetSkinFile()
	{
		return L"TransTaskShowErrorDialog.xml";
	}

	LPCTSTR TransTaskShowErrorDialog::GetWindowClassName(void) const
	{
		return L"TransTaskShowErrorDialog";
	}

	CControlUI* TransTaskShowErrorDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool TransTaskShowErrorDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}
	
	void TransTaskShowErrorDialog::Notify(TNotifyUI& msg)
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
			else if (name == L"backupError_item_filePath")
			{
				if (NULL == msg.pSender->GetParent()) return;
				if (NULL == msg.pSender->GetParent()->GetParent()) return;
				TransErrorListContainerElement* pItem = static_cast<TransErrorListContainerElement*>(msg.pSender->GetParent()->GetParent());
				if (NULL == pItem) return;

				if (pItem->nodeData.root->type == ATT_Download)
				{
					Close();

					std::list<PathNode> pathnodes;
					int64_t parentid = 0;
					int64_t fileid = SD::Utility::String::string_to_type<int64_t>(pItem->nodeData.source);
					if( fileid >= 0 )
					{
						userContext_->getShareResMgr()->getFilePathNodes(fileid, pathnodes);
						std::list<PathNode>::iterator it = pathnodes.end();
						if ( pathnodes.size() > 1 )
						{
							it--;
							parentid = it->fileId;
						}
						else if ( 1 == pathnodes.size() )
						{
							parentid = it->fileId;
						}

						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE_FOR_NAME,
							SD::Utility::String::type_to_string<std::wstring>(parentid),
							pItem->nodeData.name));
					}
					return;
				}
				else
				{		
					std::wstring tip = pItem->nodeData.source
;
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
				
			}
			else if (name == L"backupError_item_delete")
			{	
				if (NULL == msg.pSender->GetParent()) return;
				if (NULL == msg.pSender->GetParent()->GetParent()) return;
				TransErrorListContainerElement* pItem = static_cast<TransErrorListContainerElement*>(msg.pSender->GetParent()->GetParent());
				if (NULL == pItem) return;

				NoticeFrameMgr* noticeFrame_ = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
				noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
				bool bIsClickOk =  noticeFrame_->IsClickOk();
				delete noticeFrame_;
				noticeFrame_ = NULL;
				if (!bIsClickOk)  return;
				std::wstring ErrtypeDes;
				
				{
					//int64_t tip = pItem->nodeData.parent;
					std::wstring filePath=pItem->nodeData.source;
					int32_t ret = userContext_->getAsyncTaskMgr()->deleteErrorTask(m_id, pItem->nodeData.source);
					if ( RT_OK != ret ) return;

					if ( pItem->nodeData.root->fileType == FILE_TYPE_FILE)
					{
						Close();
					}	
					else
					{
						list_->Remove(pItem);
						if (list_->GetCount() == 0 )
						{
							Close();
						}										
					}
					ErrtypeDes=iniLanguageHelper.GetCommonString(TRANSERROR_TEXT_KEY);		
				}

				if (NULL != m_pCountLab)
				{
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
			else if (msg.pSender == m_pRetryBtn)
			{		
				Close();

				userContext_->getAsyncTaskMgr()->resumeTask(m_id);
				return;
			}
			else if (name == L"BackUpError_export")
			{
				ExportErrorList();
			}
			else if(name == L"BackUpError_max_btn")
			{ 
				SendMessage(WM_SYSCOMMAND, SC_MAXIMIZE, 0); 
				return;
			}
			else if(name == L"BackUpError_restore_btn")
			{ 
				SendMessage(WM_SYSCOMMAND, SC_RESTORE, 0); 
				return; 
			}
		}
	}

	LRESULT TransTaskShowErrorDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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
				CControlUI* _maxBtn = static_cast<CControlUI*>(m_PaintManager.FindControl(L"BackUpError_max_btn"));
				CControlUI* _restoreBtn = static_cast<CControlUI*>(m_PaintManager.FindControl(L"BackUpError_restore_btn"));
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

	void TransTaskShowErrorDialog::InitWindow()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"BackUpError_close_btn"));
		m_pOkBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"BackUpError_ok"));
		m_pCancelBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"BackUpError_cancel"));
		list_ = static_cast<CListUI*>(m_PaintManager.FindControl(L"BackUpError_ListView"));
		m_pRetryBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"BackUpError_retry"));
		m_pCountLab = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"BackUpError_count"));
		m_pExportBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"BackUpError_export"));
		if (NULL == m_pCloseBtn ||NULL == m_pCancelBtn ||NULL == m_pRetryBtn||NULL == m_pCountLab ||NULL == m_pExportBtn) return;

		m_noticeFrame_ = new NoticeFrameMgr(m_hWnd);
		InitData();
	}

	void TransTaskShowErrorDialog::InitData()
	{
		if (NULL == list_)  return;
		AsyncTransDetailNodes errornodes;
		userContext_->getAsyncTaskMgr()->getErrorTasks( m_id, errornodes );
		AsyncTransDetailNodes::iterator ittemp = errornodes.begin();
		std::wstring rootpath = L"";
		std::wstring rootname = L"";
		if ( ittemp != errornodes.end() )
		{
			UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
			if (NULL == defaultUserContext)
			{
				return;
			}

			int ret = RT_OK;
			int64_t fileid = SD::Utility::String::string_to_type<int64_t>((*ittemp)->root->source);
			if( fileid >= 0)
			{
				ret = defaultUserContext->getShareResMgr()->getFilePath(fileid, rootpath);

				Path remotePath = defaultUserContext->getPathMgr()->makePath();
				remotePath.id(fileid);
				FILE_DIR_INFO fileNode;

				if( FILE_TYPE_DIR == m_fileType )
				{
					ret = defaultUserContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FOLDER_TYPE_REST);
				}
				else
				{
					ret = defaultUserContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FILE_TYPE_REST);
				}

				rootpath += fileNode.name;
			}
		}

		m_errorCount = userContext_->getAsyncTaskMgr()->getErrorTasksCount(m_id);
		int32_t errorList = 0;
		for (AsyncTransDetailNodes::iterator itor = errornodes.begin();itor != errornodes.end(); itor++)
		{
			if(99 < errorList)
			{
				break;
			}
			addItem(*itor, rootpath);
			errorList++;
		}
		
		if (NULL != m_pCountLab)
		{
			std::wstring ErrtypeDes;
			ErrtypeDes=iniLanguageHelper.GetCommonString(TRANSERROR_TEXT_KEY);								
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

	void TransTaskShowErrorDialog::addItem(const AsyncTransDetailNode& node, const std::wstring& rootpath)
	{
		bool isExit=true;
		if (NULL == list_)  return;	

		CDialogBuilder builder;
		TransErrorListContainerElement* item = static_cast<TransErrorListContainerElement*>(
			builder.Create(L"TransErrorListItem.xml", L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager));		
		if (NULL == item)  return;	

		item->nodeData.root = node->root;
		item->nodeData.source = node->source;
		item->nodeData.parent = node->parent;
		item->nodeData.name = node->name;
		item->nodeData.fileType = node->fileType;
		item->nodeData.size = node->size;
		item->nodeData.status = node->status;
		item->nodeData.statusEx = node->statusEx;

		CLabelUI* pIcon = static_cast<CLabelUI*>(item->FindSubControl(L"backupError_item_fileIcon"));
		if (pIcon != NULL)
		{
			std::wstring strIconPath = SkinConfMgr::getInstance()->getIconPath(node->fileType,node->name);
			pIcon->SetBkImage(strIconPath.c_str());
		}

		CLabelUI* localName = static_cast<CLabelUI*>(item->FindSubControl(L"backupError_item_fileName"));
		if (localName != NULL)
		{			
			localName->SetText(node->name.c_str());
			localName->SetToolTip(node->name.c_str()); 
		}
		
		CButtonUI* localPath = static_cast<CButtonUI*>(item->FindSubControl(L"backupError_item_filePath"));
		if (localPath != NULL)
		{
			std::wstring str_path = L"";
			std::wstring strIconPath = SD::Utility::FS::get_work_directory();
			if( ATT_Download == node->root->type )
			{
				std::wstring path = node->parent + PATH_DELIMITER_CON + node->name;
				std::wstring temppath = node->root->parent + PATH_DELIMITER + node->root->name;
				str_path = iniLanguageHelper.GetMsgDesc(TRANSTASK_MSG_OPEN_SOURCE);
				str_path += L" ";
				str_path += rootpath + path.substr(temppath.length(), path.length());
				strIconPath += PATH_DELIMITER;
				strIconPath += L"skin\\Image\\ic_transfer_open_cloudpath.png"; 
			}
			else if( ATT_Upload == node->root->type )
			{
				str_path = iniLanguageHelper.GetMsgDesc(TRANSTASK_MSG_OPEN_SOURCE);
				str_path += L" ";
				str_path += SD::Utility::FS::get_parent_path(node->source);
				strIconPath += PATH_DELIMITER;
				strIconPath += L"skin\\Image\\ic_transfer_open_locallist.png";
			}
			localPath->SetBkImage(strIconPath.c_str());
			localPath->SetToolTip(str_path.c_str());
		}

		CLabelUI* errorDesc = static_cast<CLabelUI*>(item->FindSubControl(L"backupError_item_desc"));
		if (errorDesc != NULL)
		{
			std::wstring desc;
			if (node->root->type == ATT_Upload)
			{
				desc = iniLanguageHelper.GetCommonString(TRANSERROR_UPLOAD_KEY).c_str();
			}
			else if (node->root->type == ATT_Download)
			{
				desc = iniLanguageHelper.GetCommonString(TRANSERROR_DOWNLOAD_KEY).c_str();
			}
			desc += L"  ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_VALUE_KEY).c_str();
			desc += SD::Utility::String::type_to_string<std::wstring,int32_t>(node->errorCode);
			desc += L"  ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_CAUSE_KEY).c_str();
			desc += ErrorConfMgr::getInstance()->getDescription(node->errorCode);
			if (ErrorConfMgr::getInstance()->getAdvice(node->errorCode) != L"")
			{
				desc += L"  ";
				desc += iniLanguageHelper.GetCommonString(BACKUPERROR_PROPOSE_KEY).c_str();
				desc += ErrorConfMgr::getInstance()->getAdvice(node->errorCode);
			}

			errorDesc->SetText(desc.c_str());
			errorDesc->SetToolTip(desc.c_str());
		}		

		list_->Add(item);
	}

	LRESULT TransTaskShowErrorDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void TransTaskShowErrorDialog::ExportErrorList()
	{
		m_pExportBtn->SetEnabled(false);
		//csv
		OPENFILENAME ofn;
		ZeroMemory(&ofn,sizeof(ofn));
		ofn.lStructSize = sizeof(ofn);
		WCHAR defaultPath[MAX_PATH];
		wcscpy_s(defaultPath,MAX_PATH,(SD::Utility::FS::get_work_directory() + L"\\TransTaskErrorList").c_str());
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

		AsyncTransDetailNodes errornodes;
		userContext_->getAsyncTaskMgr()->getErrorTasks(m_id, errornodes);

		std::FILE* fp;
		_wfopen_s(&fp,defaultPath,L"w,ccs=utf-8");
		if (NULL == fp) 
		{
			m_pExportBtn->SetEnabled();
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Export errorList failed.");
			return;
		}

		for (AsyncTransDetailNodes::iterator itor = errornodes.begin(); itor != errornodes.end(); itor++)
		{
			std::wstring desc;

			if ((*itor)->root->type == ATT_Upload)
			{
				desc = iniLanguageHelper.GetCommonString(TRANSERROR_UPLOAD_KEY).c_str();
			}
			else if ((*itor)->root->type == ATT_Download)
			{
				desc = iniLanguageHelper.GetCommonString(TRANSERROR_DOWNLOAD_KEY).c_str();
			}
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_VALUE_KEY).c_str();
			desc += SD::Utility::String::type_to_string<std::wstring,int32_t>((*itor)->errorCode);
			desc += L" ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_CAUSE_KEY).c_str();
			desc += ErrorConfMgr::getInstance()->getDescription((*itor)->errorCode);
			if (ErrorConfMgr::getInstance()->getAdvice((*itor)->errorCode) != L"")
			{
				desc += L" ";
				desc += iniLanguageHelper.GetCommonString(BACKUPERROR_PROPOSE_KEY).c_str();
				desc += ErrorConfMgr::getInstance()->getAdvice((*itor)->errorCode);
			}

			fputws((+ L"\""+(*itor)->name+ 
				L"\",\"" +(*itor)->source+L"\","+ 
				((*itor)->fileType?SD::Utility::String::getSizeStr((*itor)->size):L"--")+L",\""+desc+L"\"\n").c_str(),fp);
		}
		fclose(fp);
		m_pExportBtn->SetEnabled();
	}
}
