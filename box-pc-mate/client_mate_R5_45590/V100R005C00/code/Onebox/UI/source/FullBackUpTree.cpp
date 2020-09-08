#include "stdafxOnebox.h"
#include "ListContainerElement.h"
#include "FullBackUpTree.h"
#include "DialogBuilderCallbackImpl.h"
#include "InILanguage.h"
#include "BackupAllMgr.h"
#include "UserInfoMgr.h"
#include "FullBackUpMgr.h"
#include "Utility.h"
#include <boost/thread.hpp>
#include "NoticeFrame.h"
#include "SysConfigureMgr.h"
#include "ProxyMgr.h"
#include "PathMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("FullBackUpTre")
#endif

namespace Onebox
{
	FullBackUpTreeDialog::FullBackUpTreeDialog(UserContext* context,HWND parent,DIALOGTYPE dType)
		:userContext_(context)
		,m_parentPaint(parent)
		,m_dType(dType)
		,m_pCloseBtn(NULL)
		,m_pCancelBtn(NULL)
	{
		m_pFinishBtn = NULL;
		m_pLocalTree = NULL;
		m_languageID = INVALID_LANG_ID;
		m_pRootNode = NULL;
		m_pIntro = NULL;
		m_pFirstPage = NULL;
		m_pSecondPage = NULL;
		m_pSelectArea = NULL;
		m_pRadioButton_Create = NULL;
		m_pRadioButton_Exist = NULL;
		m_pRemotePath = NULL;
		m_pRemotePathList = NULL;
		m_pPreviousBtn = NULL;
		m_pNextBtn = NULL;
		m_computerName = L"";
		m_curSel = -1;
	}

	FullBackUpTreeDialog::~FullBackUpTreeDialog()
	{
	}

	CDuiString FullBackUpTreeDialog::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString FullBackUpTreeDialog::GetSkinFile()
	{
		return L"FullBackUpTree.xml";
	}

	LPCTSTR FullBackUpTreeDialog::GetWindowClassName(void) const
	{
		return L"StartFullBackUpDialog";
	}

	CControlUI* FullBackUpTreeDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool FullBackUpTreeDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT FullBackUpTreeDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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
	
	LRESULT FullBackUpTreeDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		if (MSG_TREENODEEXPAND == uMsg)
		{
			BackupTreeNode *node = (BackupTreeNode*)wParam;
			if (NULL != node)
			{
				node->GetManager()->SendNotify(node, DUI_MSGTYPE_ITEMDBCLICK,1);
			}				
			return 0;
		}
		return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
	}

	void FullBackUpTreeDialog::Notify(TNotifyUI& msg)
	{
		if(msg.sType == DUI_MSGTYPE_CLICK) 
		{
			std::wstring name = msg.pSender->GetName().GetData();
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn)
			{
				Close();
				return; 
			}			
			else if (msg.pSender == m_pFinishBtn)
			{
				finishBtnClick();
			}			
			else if (msg.pSender == m_pPreviousBtn)
			{
				PreviousButtonClick(msg);
			}			
			else if (msg.pSender == m_pNextBtn)
			{
				NextButtonClick(msg);
			}			
			else if (msg.pSender == m_pRadioButton_Create)
			{
				CreateRadioButtonClick(msg);
			}			
			else if (msg.pSender == m_pRadioButton_Exist)
			{
				ExistRadioButtonClick(msg);
			}

			if(name == L"fullBackup_treeItem_expand") 
			{
				itemStates(msg);
			}			
			else if (name == L"fullBackup_treeItem_check")
			{
				CheckBoxClick(msg);
			}
		}
		else if(msg.sType == DUI_MSGTYPE_ITEMDBCLICK)
		{
			itemDBClick(msg);
		}
		else if(msg.sType == DUI_MSGTYPE_SELECTCHANGED)
		{
			if (msg.pSender->GetName() == L"fullBackup_ItemInfo")
			{
				ListItemClick(msg);
			}
		}
	}

	void FullBackUpTreeDialog::InitWindow()
	{  
		m_languageID = GetUserConfValue(CONF_SETTINGS_SECTION,CONF_LANGUAGE_KEY,(int32_t)UI_LANGUGE::DEFAULT);
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"fullBackUpTree_close_btn"));
		m_pCancelBtn = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindControl(L"fullBackUpTree_cancel"));
		m_pFinishBtn = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindControl(L"fullBackUpTree_finish"));
		m_pLocalTree = static_cast<CTreeViewUI*>(m_PaintManager.FindControl(L"fullBackUpTree_localTreeView"));
		m_pIntro = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"fullBackUpTree_intro"));
		m_pFirstPage = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(L"fullBackUpTree_firstPage"));
		m_pSecondPage = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(L"fullBackUpTree_secondPage"));
		m_pSelectArea = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"fullBackUpTree_selectArea"));
		m_pRadioButton_Create = static_cast<COptionUI*>(m_PaintManager.FindControl(L"fullBackUpTree_create"));
		m_pRadioButton_Exist = static_cast<COptionUI*>(m_PaintManager.FindControl(L"fullBackUpTree_exist"));
		m_pRemotePath = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"fullBackUpTree_remotePath"));
		m_pRemotePathList = static_cast<CListUI*>(m_PaintManager.FindControl(L"fullBackUpTree_remotePathList"));
		m_pPreviousBtn = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindControl(L"fullBackUpTree_previous"));
		m_pNextBtn = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindControl(L"fullBackUpTree_next"));

		if (NULL == m_pCloseBtn || NULL == m_pCancelBtn || NULL == m_pFinishBtn || NULL == m_pLocalTree || NULL == m_pIntro || NULL == m_pFirstPage
			|| NULL == m_pSecondPage || NULL == m_pSelectArea || NULL == m_pRadioButton_Create  || NULL == m_pRadioButton_Exist 
			|| NULL == m_pRemotePath || NULL == m_pRemotePathList || NULL == m_pPreviousBtn || NULL == m_pNextBtn) return;
	
		if (TYPE_CREATE == m_dType)
		{
			m_pNextBtn->SetVisible();
			m_pNextBtn->SetEnabled(false);
			m_pFinishBtn->SetVisible(false);
			m_pFinishBtn->SetText(iniLanguageHelper.GetCommonString(FULLBACKUP_DIALOG_SETTASK_FINISH).c_str());
		}
		else if (TYPE_SETTING == m_dType)
		{
			m_pNextBtn->SetVisible(false);
			m_pFinishBtn->SetVisible();
			m_pFinishBtn->SetText(iniLanguageHelper.GetCommonString(FULLBACKUP_DIALOG_SETTASK_OK).c_str());
		}

		//获取所有过滤配置
		BackupAllMgr::getInstance(userContext_) ->getPathInfo(m_selectPathList,m_filterPathList);
		//if (m_filterPathList.size() == 0) m_filterPathList.insert(L"C:\\Windows");

		std::list<std::wstring> loadPathList;
		std::wstring str_path = L"";
		loadPathList.push_back(str_path);//我的计算机
		for(std::set<std::wstring>::iterator it = m_filterPathList.begin(); it != m_filterPathList.end(); ++it)
		{
			if (!SD::Utility::FS::is_exist(*it))continue;
			if (!bEnableDir(*it)) continue;
			str_path = SD::Utility::FS::get_parent_path(*it);
			while (!str_path.empty())
			{
				if (!SD::Utility::FS::is_exist(str_path))
				{
					str_path = SD::Utility::FS::get_parent_path(str_path);
					continue;
				}
				loadPathList.push_back(str_path);
				str_path = SD::Utility::FS::get_parent_path(str_path);
			}
		}
		for(std::set<std::wstring>::iterator it = m_selectPathList.begin(); it != m_selectPathList.end(); ++it)
		{
			if (!SD::Utility::FS::is_exist(*it))continue;
			str_path = SD::Utility::FS::get_parent_path(*it);
			while (!str_path.empty())
			{
				if (!SD::Utility::FS::is_exist(str_path))
				{
					str_path = SD::Utility::FS::get_parent_path(str_path);
					continue;
				}
				loadPathList.push_back(str_path);
				str_path = SD::Utility::FS::get_parent_path(str_path);
			}
		}
		loadPathList.sort();
		loadPathList.unique();
		getLoadNode(L"",0);//添加首节点
		std::vector<std::wstring> pathLevelVec;
		for (std::list<std::wstring>::iterator it = loadPathList.begin(); it != loadPathList.end(); ++it)
		{
			pathLevelVec.clear();
			SD::Utility::String::split(*it,pathLevelVec,L"\\");
			getLoadNode(*it,pathLevelVec[0].empty()?1:pathLevelVec.size()+1);
		}	

		//加载Treeview
		loadLocalData(L"",NULL);
	}

	void FullBackUpTreeDialog::autoExpandNode(BackupTreeNode *node)
	{
		boost::this_thread::sleep(boost::posix_time::milliseconds(1));
		PostMessage(MSG_TREENODEEXPAND, (WPARAM)node, NULL);
	}

	bool FullBackUpTreeDialog::addItem(const BackupTreeNodeInfo& item, BackupTreeNode *parent, bool addToHeader, bool checkBoxIsCheck,bool isAuto)
	{
		CDialogBuilder builder;
		BackupTreeNode *node = static_cast<BackupTreeNode*>(builder.Create(L"FullBackupTreeItem.xml", L"", this, &m_PaintManager, NULL));
		if (NULL == node)
		{
			return false;
		}
		node->nodeData = item;
		bool bflag = bEnableDir(node->nodeData._fileData->path);
		COptionUI *pCheck = static_cast<COptionUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_treeItem_check"));
		if (NULL == pCheck)
		{
			return false;
		}
		if (isAuto)
		{	
			pCheck->Selected (bflag?(node->nodeData._nodeLevel==1?m_selectPathList.find(item._fileData->path)!=m_selectPathList.end():((checkBoxIsCheck?m_filterPathList.find(item._fileData->path)==m_filterPathList.end():m_selectPathList.find(item._fileData->path)!=m_selectPathList.end()))):bflag);
		}
		else
		{
			pCheck->Selected(bflag?(checkBoxIsCheck?m_filterPathList.find(item._fileData->path)==m_filterPathList.end():checkBoxIsCheck):bflag);
		}
		pCheck->SetEnabled(bflag);		
		pCheck->SetVisible(!addToHeader);

		CLabelUI *name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_treeItem_name"));
		if (NULL == name)
		{
			return false;
		}		
		name->SetTextColor(bflag?0xFF333333:0xFFBBBBBB);
		
		name->SetText(node->nodeData._fileData->name.c_str());
		name->SetToolTip(node->nodeData._fileData->name.c_str());
		if (NULL != name->GetParent())
		{
			CHorizontalLayoutUI * m_pHorLayout = static_cast<CHorizontalLayoutUI*>(name->GetParent());
			if (NULL != m_pHorLayout)
			{
				m_pHorLayout->SetToolTip(node->nodeData._fileData->name.c_str());
			}
		}

		CLabelUI *icon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_treeItem_icon"));
		if (NULL == icon)
		{
			return false;
		}
		std::wstring str_iconPath = L"..\\image\\icon_folder.png";
		if (!bflag)  str_iconPath = L"..\\image\\icon_folder_gray.png";
		if (0 == item._nodeLevel)
		{
			str_iconPath = L"..\\image\\ic_backup_pc.png";
		}
		else if (1 == item._nodeLevel)
		{
			str_iconPath = L"..\\image\\ic_backup_localdisk.png";
		}
		icon->SetBkImage(str_iconPath.c_str());

		if (NULL == parent)
		{
			if (!m_pLocalTree->Add(node)) return false;
		}
		else
		{
			if (!addToHeader)
			{
				if (!parent->AddChildNode(node)) return false;
			}
			else
			{
				if (!parent->AddChildNode(node)) return false;
			}
		}
		//parent->SetExpand();
		node->Select(true);
		boost::thread(boost::bind(&FullBackUpTreeDialog::autoExpandNode, this, node));
		return true;
	}

	void FullBackUpTreeDialog::itemStates(DuiLib::TNotifyUI& msg)
	{
		CCheckBoxUI* folderBtn = static_cast<CCheckBoxUI*>(msg.pSender);
		if (NULL == folderBtn)  return;
		if (NULL == folderBtn->GetClass())  return;
		if (_tcsicmp(L"CheckBoxUI",folderBtn->GetClass()) != 0) return;
		if (NULL == folderBtn->GetParent())  return;
		if (NULL == folderBtn->GetParent()->GetParent())  return;
		BackupTreeNode* node = static_cast<BackupTreeNode*>(folderBtn->GetParent()->GetParent());
		if (NULL == node) return;
		/*if (node->nodeData._nodeLevel > 1) 
		{
		NoticeFrameMgr* _noticeFrame = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
		_noticeFrame->Run(Confirm,Info,L"",MSG_FULLBACKUP_TREENODEXPAND_TIPS,Modal);
		delete _noticeFrame;
		_noticeFrame = NULL;
		return;
		}*/
		if (!bEnableDir(node->nodeData._fileData->path)) return;
		node->Select();
		if (node->IsExpanded()) return;
		if (!node->nodeData._bExpand || node->GetVisibleTag() == folderBtn->IsSelected())
		{
			if(!folderBtn->IsSelected())
			{
				node->SetExpand(false);
			}

			COptionUI *pCheck = static_cast<COptionUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_treeItem_check"));
			bool bCheck = false;
			if (NULL != pCheck)
			{
				if(pCheck->IsSelected())
				{
					bCheck = true;
				}
			}
			loadLocalData(node->nodeData._fileData->path,node,bCheck);
		}
	}

	void FullBackUpTreeDialog::itemDBClick(DuiLib::TNotifyUI& msg)
	{
		BackupTreeNode* node = static_cast<BackupTreeNode*>(msg.pSender);
		if (NULL == node) return;	
		/*if (1 != msg.wParam && node->nodeData._nodeLevel > 1) 
		{
			NoticeFrameMgr* _noticeFrame = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());
			_noticeFrame->Run(Confirm,Info,L"",MSG_FULLBACKUP_TREENODEXPAND_TIPS,Modal);
			delete _noticeFrame;
			_noticeFrame = NULL;
			return;
		}*/
		if (!bEnableDir(node->nodeData._fileData->path)) return;		
		CCheckBoxUI* folderBtn	= node->GetFolderButton();
		if (NULL == folderBtn) return;
		if (folderBtn->IsSelected())
		{
			if (!node->IsExpanded())
				node->SetExpand(false);		
			m_pLocalTree->SetItemExpand(false,node);			
			return;
		}
		else
		{
			node->SetExpand();
			if (node->GetCountChild() == 0)
			{
				m_pLocalTree->SetItemExpand(true,node);
				bool bCheck = false;
				if(m_pRootNode == node)
				{
					bCheck = true;
				}
				else
				{
					COptionUI *pCheck = static_cast<COptionUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_treeItem_check"));
					if (NULL != pCheck)
					{
						if(pCheck->IsSelected())
						{
							bCheck = true;
						}
					}
				}
				if (1 == msg.wParam)
				{
					loadExistData(node->nodeData._fileData->path,node,bCheck);
				}
				else
				{
					loadLocalData(node->nodeData._fileData->path,node,bCheck);
				}
			}
			return;
		}
	}

	void FullBackUpTreeDialog::loadLocalData(std::wstring str_path, BackupTreeNode* parent,bool checkBoxIsCheck)
	{
		std::vector<BackupTreeNodeInfo> folders;
		folders.clear();
		BackupTreeNodeInfo fileNode;
		if (NULL == parent)
		{
			fileNode._fileData->dataType = CFDDT_local;
			fileNode._fileData->name = userContext_->getUserInfoMgr()->getHostName();
			
			if(iniLanguageHelper.GetLanguage() ==UI_LANGUGE::CHINESE)
			{
				m_computerName += fileNode._fileData->name;
				m_computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
			}
			else
			{
				m_computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
				m_computerName += L" ";
				m_computerName += fileNode._fileData->name;
			}

			fileNode._fileData->path = L"";
			fileNode._fileData->data.reset(new LocalCommonFileDialogItemDataType);
			*(LocalCommonFileDialogItemDataType*)(fileNode._fileData->data.get()) = LCFDIDT_my_computer;
			fileNode._nodeLevel = 0;
			fileNode._fileData->type = CFDFT_DIR;
			fileNode._fileData->autoExpand = false;	
			fileNode._bExpand = true;	
			addItem(fileNode,NULL,true,true);
 			BackupTreeNode* pNode = static_cast<BackupTreeNode*>(m_pLocalTree->GetItemAt(0));
 			if(NULL==pNode) return;
 			m_pRootNode = pNode;
		}
		else
		{
			CommonFileDialogListResult result;
			m_pLogicalDriveList.clear();
			int32_t ret = listFolder(parent->nodeData._fileData, result);
			for (CommonFileDialogListResult::iterator it = result.begin(); it != result.end(); ++it)
			{
				fileNode._fileData = *it;
				fileNode._nodeLevel =  parent->nodeData._nodeLevel + 1;
				fileNode._parentPath = parent->nodeData._fileData->name;
				fileNode._bExpand = false;
				parent->nodeData._bExpand = true;
				folders.push_back(fileNode);
				if (fileNode._nodeLevel == 1)
				{
					m_pLogicalDriveList.push_back(fileNode._fileData->name);
				}
			}
		}
		if (folders.size() <= 0) return;
		for (size_t i = 0 ; i < folders.size() ;i++)
		{
			addItem(folders[i],parent,false,checkBoxIsCheck);
		}
	}

	void FullBackUpTreeDialog::loadExistData(std::wstring str_path, BackupTreeNode* parent,bool checkBoxIsCheck)
	{
		if (NULL == parent) return;		
		std::vector<BackupTreeNodeInfo> folders;
		folders.clear();
		BackupTreeNodeInfo fileNode;

		for (BackupTreeNodeInfoList::iterator it = m_treeNodes.begin(); it != m_treeNodes.end(); ++it)
		{
			if (parent->nodeData._nodeLevel+1 == (*it)._nodeLevel && parent->nodeData._fileData->path == (*it)._parentPath)
			{
				folders.push_back(*it);
			}
		}

		if (folders.size() <= 0) 
		{
			if (!bEnableDir(parent->nodeData._fileData->path)) return;		
			CCheckBoxUI* folderBtn	= parent->GetFolderButton();
			if (NULL == folderBtn) return;
			folderBtn->Selected(true);
			parent->Select(false);
			parent->SetExpand(false);
			BackupTreeNode* pNode = static_cast<BackupTreeNode*>(m_pLocalTree->GetItemAt(0));
			pNode->Select();
			return;
		}
		m_pLogicalDriveList.clear();
		for (size_t i = 0 ; i < folders.size() ;i++)
		{
			if (fileNode._nodeLevel == 1)
			{
				m_pLogicalDriveList.push_back(fileNode._fileData->name);
			}
			addItem(folders[i],parent,false,checkBoxIsCheck,true);
		}
	}

	void FullBackUpTreeDialog::finishBtnClick()
	{
		std::list<std::wstring> backupPathList;
		std::list<std::wstring> filterPathList;
		backupPathList.clear();
		filterPathList.clear();
		for (int i = 0; i < m_pLocalTree->GetCount(); i++)
		{
			BackupTreeNode* pNode = static_cast<BackupTreeNode*>(m_pLocalTree->GetItemAt(i));
			if (NULL == pNode)continue;
			COptionUI* pCheck = static_cast<COptionUI*>(pNode->FindSubControl(L"fullBackup_treeItem_check"));
			if (NULL == pCheck)continue;
			BackupTreeNode* parentNode = static_cast<BackupTreeNode*>(pNode->GetParentNode());
			if (NULL == parentNode)continue;
			COptionUI* parentCheck = static_cast<COptionUI*>(parentNode->FindSubControl(L"fullBackup_treeItem_check"));
			if (NULL == parentCheck)continue;
			if (parentNode->nodeData._fileData->path.empty() && pCheck->IsSelected()) backupPathList.push_back(pNode->nodeData._fileData->path);
			if (!parentCheck->IsSelected()) //父目录未选中情况
			{
				if (pCheck->IsSelected()) backupPathList.push_back(pNode->nodeData._fileData->path);
				continue;
			}
			if (!pCheck->IsSelected())//父目录选中，当前目录未选中情况
			{
				filterPathList.push_back(pNode->nodeData._fileData->path);
				continue;
			}
		}
		std::wstring str_name = L"" ;
		if (m_nameList.size() == 0)
		{
			str_name = m_computerName;
		}
		else
		{
			if (m_pRadioButton_Create->IsSelected())
			{
				str_name = m_pRemotePath->GetText();
			}
			else if (m_pRadioButton_Exist->IsSelected())
			{
				CListContainerElementUI *node = static_cast<CListContainerElementUI*> (m_pRemotePathList->GetItemAt(m_curSel));
				if (NULL == node) return;
				COptionUI *pCheck = static_cast<COptionUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_ItemInfo"));
				if (NULL == pCheck) return;
				str_name = pCheck->GetText();
			}
			str_name = SD::Utility::FS::get_file_name(str_name);
		}

		backupPathList.sort();
		backupPathList.unique();
		filterPathList.sort();
		filterPathList.unique();
		int32_t ret = BackupAllMgr::getInstance(userContext_) ->setBackupTask(backupPathList,filterPathList,BAT_REAL_TIME,L"_08:30",str_name);
		if (RT_OK == ret)
		{
			FullBackUpMgr* fullBackUpMgr__ = FullBackUpMgr::getInstance(userContext_,m_PaintManager);
			if (fullBackUpMgr__)
			{
				fullBackUpMgr__->initData();
			}
		}
		else
		{
			FullBackUpMgr* fullBackUpMgr__ = FullBackUpMgr::getInstance(userContext_,m_PaintManager);
			if (fullBackUpMgr__)
			{
				fullBackUpMgr__->showError(ret);
			}
		}
		Close();
	}

	void FullBackUpTreeDialog::CheckBoxClick(DuiLib::TNotifyUI& msg)
	{
		COptionUI* pCheck = static_cast<COptionUI*>(msg.pSender);
		if (NULL == pCheck)	return;
		if (NULL == pCheck->GetParent())return;
		if (NULL == pCheck->GetParent()->GetParent())return;
		if (NULL == pCheck->GetParent()->GetParent()->GetParent())return;
		BackupTreeNode* pNode = static_cast<BackupTreeNode*>(pCheck->GetParent()->GetParent()->GetParent());
		if (NULL == pNode) return;
		if (!bEnableDir(pNode->nodeData._fileData->path)) 
		{
			pCheck->Selected(true);
			return;
		}
		if (0 != pNode->GetCountChild()) 
		{
			if (pCheck->IsSelected()) pCheck->SetNormalImage(L"file='..\\image\\ic_popup_checkbox_unselect.png' source='0,0,16,16' dest='7,7,23,23'");
			ChildItemControl(pNode,!pCheck->IsSelected());
		}
		//if (NULL != pNode->GetParentNode()) ParentItemControl(pNode,!pCheck->IsSelected());
		if (TYPE_CREATE != m_dType) return;
		bool bEnabled = false;
		for (int i = 0; i < m_pLocalTree->GetCount(); i++)
		{
			BackupTreeNode* node = static_cast<BackupTreeNode*>(m_pLocalTree->GetItemAt(i));
			if (NULL == node)continue;
			if (node->nodeData._fileData->path.empty()) continue;
			COptionUI* pCheck = static_cast<COptionUI*>(node->FindSubControl(L"fullBackup_treeItem_check"));
			if (NULL == pCheck)continue;
			if (pNode == node )
			{
				if (!pCheck->IsSelected())
				{
					bEnabled = true;
					break;
				}
			}
			else
			{
				if (pCheck->IsSelected())
				{
					bEnabled = true;
					break;
				}
			}
		}
		m_pNextBtn->SetEnabled(bEnabled);
	}

	void FullBackUpTreeDialog::ChildItemControl(BackupTreeNode* parentNode, bool checkBoxIsCheck)
	{
		for (int i = 0; i < parentNode->GetCountChild(); i++)
		{
			BackupTreeNode* pNode = static_cast<BackupTreeNode*>(parentNode->GetChildNode(i));
			COptionUI* pCheck = static_cast<COptionUI*>(pNode->FindSubControl(L"fullBackup_treeItem_check"));
			if (NULL == pCheck) continue;
			pCheck->Selected(bEnableDir(pNode->nodeData._fileData->path)?checkBoxIsCheck:false);
			if (0 != pNode->GetCountChild()) ChildItemControl(pNode,checkBoxIsCheck);
		}		
	}

	void FullBackUpTreeDialog::ParentItemControl(BackupTreeNode* childNode, bool checkBoxIsCheck)
	{
		BackupTreeNode* parentNode = static_cast<BackupTreeNode*>(childNode->GetParentNode());
		if (NULL == parentNode) return;
		COptionUI* pCheck = static_cast<COptionUI*>(parentNode->FindSubControl(L"fullBackup_treeItem_check"));
		if (NULL == pCheck) return;
		if (checkBoxIsCheck)
		{
			bool bAllIsCheck = true;
			//判断同级节点是否全部选中
			for (int i = 0; i < parentNode->GetCountChild(); i++)
			{
				BackupTreeNode* pNode = static_cast<BackupTreeNode*>(parentNode->GetChildNode(i));
				if (NULL == pNode)continue;
				if (!bEnableDir(pNode->nodeData._fileData->path)) continue;
				COptionUI* pChildCheck = static_cast<COptionUI*>(pNode->FindSubControl(L"fullBackup_treeItem_check"));
				if (NULL == pChildCheck)
				{
					bAllIsCheck = false;
					break;
				}				
				if (childNode != pNode && !pChildCheck->IsSelected())
				{
					bAllIsCheck = false;
					break;
				}
			}	
			if (bAllIsCheck)
			{
				pCheck->Selected(true);
				ParentItemControl(parentNode,true);
			}
			else
			{
				pCheck->SetNormalImage(L"file='..\\image\\ic_popup_partial_select.png' source='0,0,16,16' dest='7,7,23,23'");
			}
		}
		else
		{
			//父节点逐级设置状态
			bool bHaveCheck = false;
			for (int i = 0; i < parentNode->GetCountChild(); i++)
			{
				//判断同级节点是否有选中
				BackupTreeNode* pNode = static_cast<BackupTreeNode*>(parentNode->GetChildNode(i));
				COptionUI* pChildCheck = static_cast<COptionUI*>(pNode->FindSubControl(L"fullBackup_treeItem_check"));
				if (NULL == pChildCheck) continue;
				if (childNode != pNode && pChildCheck->IsSelected())
				{
					bHaveCheck = true;
					break;
				}
			}	
			pCheck->Selected(false);
			if (bHaveCheck)
			{
				pCheck->SetNormalImage(L"file='..\\image\\ic_popup_partial_select.png' source='0,0,16,16' dest='7,7,23,23'");
			}
			else
			{
				pCheck->SetNormalImage(L"file='..\\image\\ic_popup_checkbox_unselect.png' source='0,0,16,16' dest='7,7,23,23'");
			}
			ParentItemControl(parentNode,false);
		}
	}

	bool FullBackUpTreeDialog::bEnableDir(std::wstring str_path)
	{
		//config
// 		std::wstring str_program = L"C:\\Program Files";
// 		std::wstring str_windows = L"C:\\Windows";
// 		if (str_program == str_path || str_windows == str_path) return false;
		if (m_selectPathList.find(str_path) != m_selectPathList.end()) return true;
		return !userContext_->getSysConfigureMgr()->isBackupDisable(str_path);
	}

	void FullBackUpTreeDialog::getLoadNode(std::wstring str_path,int32_t _level)
	{
		std::vector<BackupTreeNodeInfo> folders;
		folders.clear();
		BackupTreeNodeInfo fileNode;
		fileNode._fileData->dataType = CFDDT_local;
		fileNode._fileData->name = str_path.empty()?userContext_->getUserInfoMgr()->getHostName():(SD::Utility::FS::get_topdir_name(str_path).empty()?str_path:SD::Utility::FS::get_file_name(str_path));
		fileNode._fileData->path = str_path;
		fileNode._fileData->data.reset(new LocalCommonFileDialogItemDataType);
		*(LocalCommonFileDialogItemDataType*)(fileNode._fileData->data.get()) = str_path.empty()?LCFDIDT_my_computer:LCFDIDT_common_path;
		fileNode._nodeLevel = 0;
		fileNode._fileData->type = CFDFT_DIR;
		fileNode._fileData->autoExpand = false;	
		fileNode._bExpand = true;	
		if (str_path.empty() && 0 == _level)
		{
			m_treeNodes.push_back(fileNode);
		}
		else
		{
			CommonFileDialogListResult result;
			int32_t ret = listFolder(fileNode._fileData, result);
			for (CommonFileDialogListResult::iterator it = result.begin(); it != result.end(); ++it)
			{
				if (CFDFT_DIR != (*it)->type) continue;
				//if (m_filterPathList.find((*it)->path) != m_filterPathList.end() && -1 != (*it)->path.find(L"\\")) continue;
				
				fileNode._fileData = *it;
				fileNode._nodeLevel =  _level;
				fileNode._parentPath = str_path;
				fileNode._bExpand = false;
				m_treeNodes.push_back(fileNode);
				if (fileNode._nodeLevel == 1)
				{
					m_pLogicalDriveList.push_back(fileNode._fileData->name);
				}
			}
		}
	}

	int32_t FullBackUpTreeDialog::listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
	{
		//root
		if (NULL == parent.get())
		{
			return listRoot(result);
		}

		if (parent->dataType != CFDDT_local || parent->type != CFDFT_DIR)
		{
			return E_CFD_INVALID_PARAM;
		}
		LocalCommonFileDialogItemDataType type = *(LocalCommonFileDialogItemDataType*)parent->data.get();
		switch (type)
		{
		case Onebox::LCFDIDT_desktop:
			return listDesktop(result);
		case Onebox::LCFDIDT_my_documents:
		case Onebox::LCFDIDT_my_music:
		case Onebox::LCFDIDT_my_video:
		case Onebox::LCFDIDT_my_picture:
			return listVirtualPath(result, type);
		case Onebox::LCFDIDT_my_computer:
			return listMyComputer(result);
		case Onebox::LCFDIDT_common_path:
			return listPath(result, parent->path);
		default:
			break;
		}

		return E_CFD_INVALID_PARAM;
	}

	int32_t FullBackUpTreeDialog::listRoot(CommonFileDialogListResult& result)
	{
		CommonFileDialogItem item(new st_CommonFileDialogItem);
		item->name = IniLanguageHelper(m_languageID).GetCommonString(common_file_dialog_my_computer_name);
		item->type = CFDFT_DIR;
		item->dataType = CFDDT_local;
		item->data.reset(new LocalCommonFileDialogItemDataType);
		*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_my_computer;
		item->autoExpand = true;
		result.push_back(item);
		return E_CFD_SUCCESS;
	}

	int32_t FullBackUpTreeDialog::listDesktop(CommonFileDialogListResult& result)
	{
		// my computer
		{
			CommonFileDialogItem item(new st_CommonFileDialogItem);
			item->name = IniLanguageHelper(m_languageID).GetCommonString(common_file_dialog_my_computer_name);
			item->type = CFDFT_DIR;
			item->dataType = CFDDT_local;
			item->data.reset(new LocalCommonFileDialogItemDataType);
			*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_my_computer;
			result.push_back(item);
		}
		// desktop files
		WCHAR szPath[MAX_PATH] = {0};
		if (!SHGetSpecialFolderPath(NULL, szPath, CSIDL_DESKTOP, FALSE))
		{
			return E_CFD_ERROR;
		}
		return listPath(result, szPath);
	}

	int32_t FullBackUpTreeDialog::listMyComputer(CommonFileDialogListResult& result)
	{
		DWORD drivers = GetLogicalDrives();
		if (0 == drivers)
		{
			return E_CFD_ERROR;
		}

		WCHAR volumeName[MAX_PATH] = {0};
		WCHAR FileSystemName[MAX_PATH] = {0};

		TCHAR buf[100];
		(void)GetLogicalDriveStringsW(sizeof(buf)/sizeof(TCHAR),buf);
		int32_t driversFlag = 0;
		int32_t ret = SD::Utility::Registry::get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", L"NoDrives", driversFlag);
		if (RT_OK != ret)
		{
			ret = SD::Utility::Registry::get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Policies\\Explorer", L"NoDrives", driversFlag);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "get drives information from regist failed.");
			}		
		}
		for (TCHAR *str = buf; *str; str+=_tcslen(str)+1)
		{
			(void)memset_s(volumeName, sizeof(volumeName), 0, sizeof(volumeName));
			(void)memset_s(FileSystemName, sizeof(FileSystemName), 0, sizeof(FileSystemName));
			if (!GetVolumeInformation(str, volumeName, MAX_PATH, NULL, NULL,NULL /*文件系统标识*/, FileSystemName/*文件系统名称*/,MAX_PATH)) continue;
			if (0 != _wcsicmp(L"NTFS",FileSystemName)) continue;
			if (DRIVE_FIXED != GetDriveType(str)) continue;
			if (driversFlag & 1<<(SD::Utility::String::wstring_to_string(str).at(0) - 'A')) continue;
			CommonFileDialogItem item(new st_CommonFileDialogItem);
			item->name = volumeName;
			if (item->name.empty())
			{
				item->name = IniLanguageHelper(m_languageID).GetCommonString(common_file_dialog_default_disk_name);
			}
			item->name += L" ("  + std::wstring(str).substr(0, 2) + L")";
			item->path = std::wstring(str).substr(0, 2);
			item->type = CFDFT_DIR;
			item->dataType = CFDDT_local;
			item->data.reset(new LocalCommonFileDialogItemDataType);
			*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_common_path;
			result.push_back(item);
		}

		return E_CFD_SUCCESS;
	}

	int32_t FullBackUpTreeDialog::listVirtualPath(CommonFileDialogListResult& result, LocalCommonFileDialogItemDataType type)
	{
		WCHAR szPath[MAX_PATH] = {0};
		int csidl = 0;
		switch (type)
		{
		case Onebox::LCFDIDT_my_documents:
			csidl = CSIDL_MYDOCUMENTS;
			break;
		case Onebox::LCFDIDT_my_music:
			csidl = CSIDL_MYMUSIC;
			break;
		case Onebox::LCFDIDT_my_video:
			csidl = CSIDL_MYVIDEO;
			break;
		case Onebox::LCFDIDT_my_picture:
			csidl = CSIDL_MYPICTURES;
			break;
		default:
			return E_CFD_NOT_IMPLEMENT;
		}
		if (!SHGetSpecialFolderPath(NULL, szPath, csidl, FALSE))
		{
			return E_CFD_ERROR;
		}
		return listPath(result, szPath);
	}

	int32_t FullBackUpTreeDialog::listPath(CommonFileDialogListResult& result, const std::wstring path)
	{
		WIN32_FIND_DATA wfd;
		HANDLE hFind = FindFirstFile(std::wstring(path+L"\\*").c_str(), &wfd);
		if(hFind==INVALID_HANDLE_VALUE)
		{
			return E_CFD_ERROR;
		}
		std::wstring tempName = L"";
		CommonFileDialogListResult files, folders;
		while(FindNextFile(hFind, &wfd))
		{
			tempName = wfd.cFileName;
			if (L"." == tempName || L".." == tempName)
			{
				continue;
			}
			if (userContext_->getSysConfigureMgr()->isBackupDisableAttr(wfd.dwFileAttributes))
			{
				continue;
			}
			CommonFileDialogItem item(new st_CommonFileDialogItem);
			item->name = tempName;
			item->path = path+L"\\"+tempName;
			item->dataType = CFDDT_local;
			item->data.reset(new LocalCommonFileDialogItemDataType);
			*(LocalCommonFileDialogItemDataType*)(item->data.get()) = LCFDIDT_common_path;
			if (wfd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)
			{
				item->type = CFDFT_DIR;
				folders.push_back(item);
			}
			//else
			//{
				//item->type = CFDFT_FILE;
				//files.push_back(item);
			//}
		}
		FindClose(hFind);
		result.insert(result.end(), folders.begin(), folders.end());
		//result.insert(result.end(), files.begin(), files.end());
		return E_CFD_SUCCESS;
	}

	void FullBackUpTreeDialog::PreviousButtonClick(DuiLib::TNotifyUI& msg)
	{
		m_pFirstPage->SetVisible();
		m_pSecondPage->SetVisible(false);
		m_pPreviousBtn->SetVisible(false);
		m_pNextBtn->SetVisible();
		m_pFinishBtn->SetVisible(false);
		m_pIntro->SetText(iniLanguageHelper.GetCommonString(FULLBACKUP_DIALOG_SETTASK_SELECT_LOCAL).c_str());
	}

	void FullBackUpTreeDialog::NextButtonClick(DuiLib::TNotifyUI& msg)
	{
		m_nameList.clear();
		m_pFirstPage->SetVisible(false);
		m_pSecondPage->SetVisible();
		m_pPreviousBtn->SetVisible();
		m_pNextBtn->SetVisible(false);
		m_pFinishBtn->SetVisible();
		m_pIntro->SetText(iniLanguageHelper.GetCommonString(FULLBACKUP_DIALOG_SETTASK_SELECT_CLOUDY).c_str());
		Path listPath = userContext_->getPathMgr()->makePath();
		listPath.id(0);
		LIST_FOLDER_RESULT lfResult;
		ProxyMgr::getInstance(userContext_)->listAll(userContext_, listPath, lfResult);
		for(LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
		{
			if (it->extraType.empty()) continue;
			if (0 != SD::Utility::String::to_lower(it->extraType).compare(L"computer")) continue;
			m_nameList.insert(it->name);
		}
		std::wstring str_name = iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY) + L"\\";
		str_name += m_computerName;
		if (m_nameList.size() == 0)
		{
			m_pSelectArea->SetVisible(false);
			m_pRemotePathList->SetVisible(false);
			m_pRemotePath->SetVisible();
			m_pRemotePath->SetText(str_name.c_str());
			m_pRemotePath->SetToolTip(str_name.c_str());
			return;
		}

		m_pSelectArea->SetVisible();
		if (m_nameList.find(m_computerName) == m_nameList.end())
		{
			m_pRemotePath->SetVisible();
			m_pRemotePathList->SetVisible(false);
			m_pRadioButton_Create->Selected(true);
			m_pRemotePath->SetText(str_name.c_str());
			m_pRemotePath->SetToolTip(str_name.c_str());
		}
		else
		{
			m_pRemotePath->SetVisible(false);
			m_pRemotePathList->SetVisible();
			m_pRadioButton_Exist->Selected(true);
			m_pFinishBtn->SetEnabled(false);
			m_pRemotePathList->RemoveAll();
			for (std::set<std::wstring>::const_iterator it = m_nameList.begin(); it != m_nameList.end(); ++it)
			{
				addListItem(iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY) + L"\\" + *it,*it == m_computerName);
			}
		}
	}

	void FullBackUpTreeDialog::CreateRadioButtonClick(DuiLib::TNotifyUI& msg)
	{
		std::wstring newName = m_computerName;
		int32_t iCount = 1;
		m_pFinishBtn->SetEnabled();
		while (m_nameList.find(newName) != m_nameList.end())
		{
			newName = newName.substr(0, newName.find_last_of(L"("));
			newName += L"(" + SD::Utility::String::type_to_string<std::wstring>(iCount) + L")";
			++iCount;
		}
		std::wstring str_name = iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY) + L"\\" + newName;
		m_pRemotePath->SetVisible();
		m_pRemotePathList->SetVisible(false);
		m_pRadioButton_Create->Selected(true);
		m_pRemotePath->SetText(str_name.c_str());
		m_pRemotePath->SetToolTip(str_name.c_str());
	}

	void FullBackUpTreeDialog::ExistRadioButtonClick(DuiLib::TNotifyUI& msg)
	{
		m_pRemotePath->SetVisible(false);
		m_pRemotePathList->SetVisible();
		m_pRadioButton_Exist->Selected(true);
		m_pFinishBtn->SetEnabled(false);
		m_pRemotePathList->RemoveAll();
		for (std::set<std::wstring>::const_iterator it = m_nameList.begin(); it != m_nameList.end(); ++it)
		{
			addListItem(iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY) + L"\\" + *it,*it == m_computerName);
		}
	}

	void FullBackUpTreeDialog::addListItem(const std::wstring& itemName, bool IsRecommendation)
	{
		CDialogBuilder builder;
		CListContainerElementUI *node = static_cast<CListContainerElementUI*>(builder.Create(L"FullBackupListItem.xml", L"", this, &m_PaintManager, NULL));
		if (NULL == node) return;
		COptionUI *pCheck = static_cast<COptionUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_ItemInfo"));
		if (NULL == pCheck) return;
		pCheck->SetText(itemName.c_str());
		pCheck->Selected(IsRecommendation);
		CLabelUI *recommendation = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, L"fullBackup_itemType"));
		if (NULL == recommendation) return;
		recommendation->SetVisible(IsRecommendation);
		if (IsRecommendation) 
		{
			node->Select();
			m_pFinishBtn->SetEnabled();
			m_curSel = m_pRemotePathList->GetCount();
		}
		node->SetIndex(m_pRemotePathList->GetCount());
		m_pRemotePathList->AddAt(node,m_pRemotePathList->GetCount());
	}


	void FullBackUpTreeDialog::ListItemClick(DuiLib::TNotifyUI& msg)
	{
		if (NULL == msg.pSender->GetParent()) return;
		m_pFinishBtn->SetEnabled();
		CListContainerElementUI *node = static_cast<CListContainerElementUI*>(msg.pSender->GetParent());
		if (NULL == node) return;
		m_curSel = node->GetIndex();
	}
}
