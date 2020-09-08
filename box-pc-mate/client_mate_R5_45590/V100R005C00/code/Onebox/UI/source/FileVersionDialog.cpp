#include "stdafxOnebox.h"
#include "DialogBuilderCallbackImpl.h"
#include "FileVersionDialog.h"
#include "ListContainerElement.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "UploadFrame.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"
#include "ShellCommonFileDialog.h"
#include "NoticeFrame.h"
#include "ProxyMgr.h"

namespace Onebox
{
	FileVersionDialog::FileVersionDialog(UserContext* context, FILE_DIR_INFO& node)
		:userContext_(context)
		,m_node(node)
		,m_pCloseBtn(NULL)
		,m_pCloseBtnIcon(NULL)
		,m_pList(NULL)
		,m_iVersionSize(0)
	{
		noDelete_ = true;
		if(NULL!=UserContextMgr::getInstance()->getDefaultUserContext())
		{
			noDelete_ = (UserContextMgr::getInstance()->getDefaultUserContext()->id.id != context->id.id);
		}
	}

	FileVersionDialog::FileVersionDialog(UserContext* context,FILE_DIR_INFO& node,int64_t userId)
		:userContext_(context)
		,m_node(node)
		,m_pCloseBtn(NULL)
		,m_pCloseBtnIcon(NULL)
		,m_pList(NULL)
		,m_iVersionSize(0)
		,m_userId(userId)
	{
		noDelete_ = true;
		downloadFlag_ = false;
		filePermissions_ = FP_INVALID;
		if(NULL!=UserContextMgr::getInstance()->getDefaultUserContext())
		{
			noDelete_ = (UserContextMgr::getInstance()->getDefaultUserContext()->id.id != context->id.id);
		}
	}

	FileVersionDialog::~FileVersionDialog()
	{
		try
		{
			Path filePath = userContext_->getPathMgr()->makePath();
			filePath.id(m_node.id);
			filePath.type(FILE_TYPE_FILE);
			ProxyMgr::getInstance(userContext_)->flushFileInfo(userContext_, filePath);
		}
		catch(...)
		{
		}
	}

	CDuiString FileVersionDialog::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString FileVersionDialog::GetSkinFile()
	{
		return vFileXml;
	}

	LPCTSTR FileVersionDialog::GetWindowClassName(void) const
	{
		return L"FileVersionDialog";
	}

	CControlUI* FileVersionDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool FileVersionDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void FileVersionDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn  || msg.pSender == m_pCloseBtnIcon) {
				Close();
			}
			else if (msg.pSender->GetName() == vBtnDwon)
			{
				// down file
				if(NULL==msg.pSender->GetParent()) return;
				if(NULL==msg.pSender->GetParent()->GetParent()) return;
				FileVersionListContainerElement* pControl = static_cast<FileVersionListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (pControl == NULL) return;
				if (true == downloadFlag_) return;

				std::list<FileBaseInfo> desList;
				std::list<UIFileNode> itemList;
				UIFileNode node;
				node.userContext = pControl->nodeData.userContext;
				node.basic.id = pControl->nodeData.basic.id;
				node.basic.name = pControl->nodeData.basic.name;
				node.basic.parent = pControl->nodeData.basic.parent;
				node.basic.type = FILE_TYPE_FILE;
				node.basic.size = pControl->nodeData.basic.size;
				node.userContext = pControl->nodeData.userContext;
				itemList.push_back(node);

				ShellCommonFileDialogParam param;
				param.type = OpenAFolder;
				param.parent = *this;
 				std::auto_ptr<ShellCommonFileDialog> fileDialog(new ShellCommonFileDialog(param));
 				ShellCommonFileDialogResult result;
 				downloadFlag_ = true;
 				(void)fileDialog->getResults(result);
 				downloadFlag_ = false;
 				if(1 != result.size()) return;
 				(void)TransTaskMgr::download(itemList, *(result.begin()));
			}
			else if (msg.pSender->GetName() == vBtnDelete)
			{
				// delete file version
				if(NULL==msg.pSender->GetParent()) return;
				if(NULL==msg.pSender->GetParent()->GetParent()) return;
				FileVersionListContainerElement* pControl = static_cast<FileVersionListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (pControl == NULL) return;

				NoticeFrameMgr* noticeFrame_ = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
				noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_VERSIONFILE_DELCONFIRM_NOTICE_KEY,Modal);
				bool bIsClickOk =  noticeFrame_->IsClickOk();
				delete noticeFrame_;
				noticeFrame_ = NULL;
				if (!bIsClickOk)  return;

				if(m_pList->Remove(pControl))
				{
					Path path = userContext_->getPathMgr()->makePath();
					path.id(pControl->nodeData.basic.id);
					(void)userContext_->getSyncFileSystemMgr()->remove(path,ADAPTER_FILE_TYPE_REST);
				}
			}
			else if(msg.pSender->GetName() == vBtnRestore)
			{

			}
		}
		return WindowImplBase::Notify(msg);
	}

	void FileVersionDialog::InitWindow()
	{  
		m_pList = static_cast<CListUI*>(m_PaintManager.FindControl(vListView));
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(vBtnClose));
		m_pCloseBtnIcon = static_cast<CButtonUI*>(m_PaintManager.FindControl(vBtnIconClose));

		LIST_FILEVERSION_RESULT nodes;
		GetNodes(nodes);
		if (nodes.size() == 0) return;

		CLabelUI* pTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(vLabFileTitle));
		
		if (pTitle == NULL) return;		

		std::wstringstream  strTitel;
		strTitel<<iniLanguageHelper.GetCommonString(MSG_MYFILE_VERSION_START).c_str();
		strTitel<< L"¡°" << nodes[0].name.c_str()<< L"¡±";
		strTitel<<iniLanguageHelper.GetCommonString(MSG_MYFILE_VERSION_END).c_str();

		pTitle->SetText(strTitel.str().c_str());
		userContext_->getSyncFileSystemMgr()->getFilePermissions(m_path, m_userId, filePermissions_);
		m_iVersionSize = nodes.size();
		for (size_t i=0;i<nodes.size();i++)
		{
			AddItem(nodes[i],nodes.size() - i);
		}	
	}

	void FileVersionDialog::SetTransparent(int nOpacity)
	{
		m_PaintManager.SetTransparent(255 - nOpacity);
	}

	void FileVersionDialog::GetNodes(LIST_FILEVERSION_RESULT& nodes)
	{
		if (userContext_ == NULL) return;
		m_path = userContext_->getPathMgr()->makePath();
		m_path.id(m_node.id);
		userContext_->getSyncFileSystemMgr()->listFileVersion(m_path,nodes,ADAPTER_FILE_TYPE_REST);
	}

	void FileVersionDialog::AddItem(FILE_VERSION_INFO& node,int versionNum)
	{		
		if (m_pList == NULL) return;

		CDialogBuilder builder;
		FileVersionListContainerElement* item = static_cast<FileVersionListContainerElement*>(
				builder.Create(vFileListItem, 
				L"", 
				this, 
				&m_PaintManager, 
				NULL));
		if (item == NULL) return;

		item->nodeData.basic = node;
		item->nodeData.userContext = userContext_;

		// set file version
		CLabelUI* fVersion = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vLabFileVersion));
		if (fVersion == NULL) return;
		WCHAR buf[26] = {0};
		swprintf_s(buf,L"V%d",versionNum);
		fVersion->SetText(buf);

		CLabelUI* fDelete	= static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vBtnDelete));
		CLabelUI* fRestore	= static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vBtnRestore));
		CLabelUI* fDownload = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vBtnDwon));
		if(NULL == fDelete || NULL == fRestore || NULL == fDownload) return;
		if(m_iVersionSize != versionNum)
		{
			fVersion->SetBkImage(L"file='..\\Image\\ic_popup_view_version.png' source='0,30,40,50'");
			fDelete->SetVisible(false);
		}
		else
		{
			
			fDelete->SetVisible(false);
			fRestore->SetVisible(false);
		}

		if(filePermissions_ & FP_DOWNLOAD)
		{
			fDownload->SetVisible(true);
		}
		else
		{
			fDownload->SetVisible(false);
		}

		if(filePermissions_ & FP_DELETE && m_iVersionSize != versionNum)
		{
			fDelete->SetVisible(true);
		}
		else
		{
			fDelete->SetVisible(false);
		}

		// set file size
		CLabelUI* fSize = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vLabFileSize));
		if (fSize == NULL) return;
		fSize->SetText(SD::Utility::String::getSizeStr(node.size).c_str());
		
		// set file time
		CLabelUI* fTime = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, vLabFileTime));
		if (fTime == NULL) return;
		fTime->SetText(SD::Utility::DateTime::getTime(node.mtime, SD::Utility::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
				
		m_pList->Add(item);
	}

	LRESULT FileVersionDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

		if ( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) 
		{
			CControlUI* pControl = m_PaintManager.FindControl(pt);
			if (NULL != pControl)
			{
				CDuiString clsName;
				std::vector<CDuiString> clsNames;
				clsName = pControl->GetClass();
				clsName.MakeLower();
				clsNames.push_back(_T("controlui"));
				clsNames.push_back(_T("textui"));
				clsNames.push_back(_T("labelui"));
				clsNames.push_back(_T("containerui"));
				clsNames.push_back(_T("horizontallayoutui"));
				clsNames.push_back(_T("verticallayoutui"));
				clsNames.push_back(_T("tablayoutui"));
				clsNames.push_back(_T("childlayoutui"));
				clsNames.push_back(_T("dialoglayoutui"));

				std::vector<CDuiString>::iterator it = std::find(clsNames.begin(), clsNames.end(),clsName);
				if (clsNames.end() != it)
				{
					CControlUI* pParent = pControl->GetParent();
					while (pParent)
					{
						clsName = pParent->GetClass();
						clsName.MakeLower();
						it = std::find(clsNames.begin(),clsNames.end(),clsName);
						if (clsNames.end() == it)
						{
							return HTCLIENT;
						}
						pParent = pParent->GetParent();
					}
					return HTCAPTION;
				}				
			}
		}
		return HTCLIENT;
	}
}
