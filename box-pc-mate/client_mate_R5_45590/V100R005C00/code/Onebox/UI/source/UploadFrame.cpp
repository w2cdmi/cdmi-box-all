#include "stdafxOnebox.h"
#include "UploadFrame.h"
#include "Utility.h"
#include "ControlNames.h"
#include "UserInfoMgr.h"
#include "UserContextMgr.h"
#include "SyncFileSystemMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "PathMgr.h"
#include "TransTaskMgr.h"
#include "TeamSpaceResMgr.h"
#include "InILanguage.h"
#include "InIHelper.h"
#include "SimpleNoticeFrame.h"
#include "ProxyMgr.h"
#include <io.h>
#include "ErrorConfMgr.h"
#include "SysConfigureMgr.h"

using namespace SD::Utility;
namespace Onebox
{
	// 比较函数，升序
	bool CompareWithUFNDesc(FILE_DIR_INFO& item1,FILE_DIR_INFO& item2)
	{
		//wstring 比较
		if(_tccmp(item1.name.c_str(),item2.name.c_str()) > 0) 
			return true;
		else 
			return false;
	}

	UploadFrame::UploadFrame(UserContext* context, CPaintManagerUI& paintManager):userContext_(context), paintManager_(paintManager)
	{
		//不指定根节点时可根据用户上下文确认默认根节点
		rootNode_.nodeType = FileNode_FileRoot;
		rootNode_.fileData.userContext = context;
		rootNode_.fileData.basic.id = 0;

		m_iAction = -1;
		m_pCloseBtn = NULL;
		m_pCancelBtn = NULL;
		m_pUploadBtn = NULL;
		m_pCopyBtn = NULL;
		m_pMoveBtn = NULL;
		m_oneboxTree = NULL;
		m_upLoadArea = NULL;
		m_downLoadArea = NULL;
		m_copyMoveArea = NULL;
		m_title = NULL;
		m_controlIndex = 0;
		m_pRootNode=NULL;
		m_selectCloudDir = NULL;
		m_warning = NULL;
		m_pCreateBtn = NULL;
	}

	UploadFrame::UploadFrame(UserContext* context, const FileBaseInfo& rootNode, CPaintManagerUI& paintManager):userContext_(context), paintManager_(paintManager)
	{
		rootNode_ = rootNode;
		m_iAction = -1;
		m_pCloseBtn = NULL;
		m_pCancelBtn = NULL;
		m_pUploadBtn = NULL;
		m_pCopyBtn = NULL;
		m_pMoveBtn = NULL;
		m_oneboxTree = NULL;
		m_upLoadArea = NULL;
		m_downLoadArea = NULL;
		m_copyMoveArea = NULL;
		m_title = NULL;
		m_controlIndex = 0;
		m_pRootNode=NULL;
		m_selectCloudDir = NULL;
		m_warning = NULL;
		m_pCreateBtn = NULL;
	}

	UploadFrame::~UploadFrame()
	{
		if (NULL != m_oneboxTree)
		{
			m_oneboxTree->RemoveAll();
			m_oneboxTree = NULL;
		}
	}

	void UploadFrame::Init()
	{
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_CLOSE));
		m_pCancelBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_CANCEL));
		m_pUploadBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_UPLOAD));
		//m_pDownloadBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_DOWNLOAD));
		m_pCopyBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_COPY));
		m_pMoveBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_MOVE));
		m_oneboxTree = static_cast<CTreeViewUI*>(m_PaintManager.FindControl(ControlNames::TREE_UPLOADFRAME_MYFILE));
		m_upLoadArea = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::HORLAYOUT_UPLOADFRAME_UPLOADAREA));
		m_downLoadArea = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::HORLAYOUT_UPLOADFRAME_DOWNLOADAREA));
		m_copyMoveArea = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(ControlNames::HORLAYOUT_UPLOADFRAME_COPYMOVEAREA));
		m_title = static_cast<CLabelUI*>(m_PaintManager.FindControl(ControlNames::LABEL_UPLOADFRAME_TILE));
		m_selectCloudDir = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"uploadFrame_selectCloudDir"));
		m_warning = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"uploadFrame_warning"));
		m_pCreateBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"uploadFrame_create"));
	}

	LPCTSTR UploadFrame::GetWindowClassName(void) const
	{
		return ControlNames::WND_UPLOADFRAME_CLS_NAME;
	}

	DuiLib::CDuiString UploadFrame::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	DuiLib::CDuiString UploadFrame::GetSkinFile()
	{
		return ControlNames::SKIN_XML_UPLOADFRAME_FILE;
	}

	CControlUI* UploadFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool UploadFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT UploadFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandle = TRUE;
		if (uMsg == WM_TIMER)
		{	
			if ((UINT)UI_TIMERID::SIMPLENOTICE_TIMERID == wParam || (UINT)UI_TIMERID::AUTOEXPAND_TIMER == wParam)
			{
				OnTimer(wParam);
				return lRes;
			}
		}

		switch (uMsg)
		{
		case WM_CREATE:
			OnCreate(uMsg,wParam,lParam,bHandle);
			break;
		case WM_SYSCOMMAND:
			OnSysCommand(uMsg,wParam,lParam,bHandle);
			break;
		default:
			bHandle = FALSE;
			break;
		}
		if (bHandle)
		{
			return lRes;
		}

		return WindowImplBase::HandleMessage(uMsg,wParam,lParam);
	}

	void UploadFrame::OnTimer(UINT nIDEvent)
	{
		if (UI_TIMERID::SIMPLENOTICE_TIMERID == nIDEvent)
		{
			SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
			simpleNotice->RestoreNoticeArea();
			delete simpleNotice;
			simpleNotice=NULL;
		}
		else if (UI_TIMERID::AUTOEXPAND_TIMER == nIDEvent)
		{
			if(NULL!=m_pRootNode)
			{
				TEventUI event;
				event.Type = UIEVENT_DBLCLICK;
				m_pRootNode->DoEvent(event);
			}
		}

		::KillTimer(this->GetHWND(),nIDEvent);
	}

	LRESULT UploadFrame::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		WindowImplBase::OnCreate(uMsg, wParam, lParam, bHandled);
		Init();
		::SetTimer(this->GetHWND(),UI_TIMERID::AUTOEXPAND_TIMER,100,NULL);
		return 0;
	}

	LRESULT UploadFrame::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (wParam == SC_CLOSE)
		{
			Close();
			return 0;
		}
		else if(wParam == 0xF032)//forbidden double click zoom out
		{
			return 0;
		}
		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	LRESULT UploadFrame::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void UploadFrame::uploadClick()
	{
		m_itemList.clear();
		if (m_iAction == UPLOADFRAME_UPLOAD)
		{	
			//上传
			for (int i = 0; i < m_oneboxTree->GetCount(); i++)
			{
				UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(i));
				if(NULL==pNode) continue;
				if(pNode->IsSelected())
				{
					m_itemList.push_back(pNode->nodeData);
				}
			}
		}
		else if (m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
		{
			//下载
			//保存到我的云盘
			//保存到团队空间
			UploadFrameTreeNode* itemTmp = NULL;
			m_pUploadBtn->SetEnabled(false);

			for (int i = 0; i < m_oneboxTree->GetCount(); i++)
			{
				UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(i));
				if(NULL==pNode) continue;
				if (pNode->IsSelected())
				{
					m_itemList.push_back(pNode->nodeData);
					itemTmp = pNode;
				}
			}
			
			if (m_itemList.empty()) return;

			if (m_itemList.size() > 1)
			{
				SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_PaintManager);
				simlpeNoticeFrame->Show(Warning,MSG_SELECTMULTI_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
				m_pUploadBtn->SetEnabled(true);
				return;
			}
			if(m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
			{
				FileBaseInfo info = *m_itemList.begin();
				m_itemList.clear();
				std::wstring path;
				std::vector<std::wstring> textlist;
				if (itemTmp == NULL) return;
				CTreeNodeUI* pNode = static_cast<CTreeNodeUI*>(itemTmp);
				UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(0));
				UploadFrameTreeNode* ttnode = static_cast<UploadFrameTreeNode*>(pNode);
				if (node == ttnode)
				{						
					info.path = node->nodeData.fileData.basic.name;
				}
				else
				{
					for (int i=0;;i++)
					{								
						textlist.push_back(itemTmp->nodeData.fileData.basic.name);
						pNode = static_cast<CTreeNodeUI*>(pNode->GetParentNode());
						if (pNode == NULL) break;
						itemTmp = static_cast<UploadFrameTreeNode*>(pNode);
					}
					for (INT counts = textlist.size();counts > 0;counts--)
					{
						path += textlist[counts - 1];
						if (0 == counts - 1) break;
						path += L"\\";
					}
					info.path = path;
				}
				m_itemList.push_back(info);
			}
		}
		m_controlIndex = 0;
	}

	void UploadFrame::createClick()
	{
		m_itemList.clear();
		for (int i = 0; i < m_oneboxTree->GetCount(); i++)
		{
			UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(i));
			if(NULL==pNode) continue;
			if (pNode->IsSelected())
			{
				m_itemList.push_back(pNode->nodeData);
			}
		}
		if (m_itemList.size() > 1)
		{
			SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_PaintManager);
			simlpeNoticeFrame->Show(Warning,MSG_SELECTMULTI_KEY);
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
			m_itemList.clear();
		}
		else
		{	
			UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(m_oneboxTree->GetCurSel()));	
			if (NULL == pNode) return;
			bool bNeedAdd = false;
			if (m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
			{
				bNeedAdd = true;
			}
			else if (m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE)
			{
				if (pNode->GetParentNode())
				{
					bNeedAdd = true;
				}
			} 
			if (bNeedAdd)
			{
				if (!pNode->IsExpanded())
				{
					if (m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE)
					{
						loadOneboxDate(pNode);
					}
					else if (m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL)
					{
						loadLocallDate(pNode);
					}
					pNode->SetExpanded();
				}

				CDialogBuilder builder;
				Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) return;
				node->nodeData.fileData.basic.name = iniLanguageHelper.GetCommonString(COMMENT_CREATENEWDIR_KEY);
				node->nodeData.fileData.basic.type = FILE_TYPE_DIR;
				node->nodeData.fileData.userContext = pNode->nodeData.fileData.userContext;
				std::wstring str_iconPath = SkinConfMgr::getInstance()->getIconPath(node->nodeData.fileData.basic.type, node->nodeData.fileData.basic.name);
				UserContext* context = NULL;
				if (m_iAction == UPLOADFRAME_DOWNLOAD)
				{
					std::wstring newName = node->nodeData.fileData.basic.name;
					int sufix = 2;
					while (SD::Utility::FS::is_exist(pNode->nodeData.path + PATH_DELIMITER + newName))
					{
						newName = node->nodeData.fileData.basic.name + SD::Utility::String::format_string(L" (%d)", sufix++);
					}
					//node->nodeData.path = pNode->nodeData.path + PATH_DELIMITER + newName;
					node->nodeData.fileData.basic.name = newName;
				}
				else if (m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
				{
					context = pNode->nodeData.fileData.userContext;
				}

				if (context)
				{
					Path path = context->getPathMgr()->makePath();
					path.parent(pNode->nodeData.fileData.basic.id);
					userContext_->getSyncFileSystemMgr()->getNewName(path, node->nodeData.fileData.basic.name);
				}

				CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
				if (fileicon != NULL)
				{
					fileicon->SetBkImage(str_iconPath.c_str());
					fileicon->SetTag((UINT_PTR)node);
				}

				CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
				if (filename != NULL)
				{		
					filename->SetText(node->nodeData.fileData.basic.name.c_str());
				}
				node->SetToolTip(node->nodeData.fileData.basic.name .c_str());
				if (pNode->AddAt(node, -1))
				{
					CCheckBoxUI* folderBtn	= static_cast<CCheckBoxUI*>(pNode->GetFolderButton());
					if (NULL == folderBtn) return;
					folderBtn->Selected(false);
					node->Select();
					CRenameRichEditUI* creui = static_cast<CRenameRichEditUI*>(node->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
					if (NULL == creui) return;
					creui->SetMouseEnabled(true);
					node->SetMouseChildEnabled();
					creui->SetFocus();
				}
				else
				{
					delete node;
				}				
			}
			m_itemList.clear();
		}
	}

	void UploadFrame::itemStates(DuiLib::TNotifyUI& msg)
	{
		CCheckBoxUI* folderBtn = static_cast<CCheckBoxUI*>(msg.pSender);
		if (NULL == folderBtn)  return;
		if (NULL == folderBtn->GetClass())  return;
		//if (_tcsicmp(L"CheckBoxUI",folderBtn->GetClass()) != 0) return;
		//if (folderBtn->GetParent())  return;
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(folderBtn->GetParent()->GetParent());
		if (NULL == node) return;
		node->Select();
		if (node->IsExpanded()) return;
		if (node->GetVisibleTag() == folderBtn->IsSelected())
		{
			if(!folderBtn->IsSelected())
			{
				node->SetExpanded(false);
			}
			if (m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
			{
				loadOneboxDate(node);
			}
			else if (m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_UPLOAD || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL)
			{
				loadLocallDate(node);
			}
			else if (m_iAction == UPLOADFRAME_SAVETOTEAMSPACE)
			{
				loadTeamspaceDate(node);
			}
		}
	}

	void UploadFrame::itemDBClick(DuiLib::TNotifyUI& msg)
	{
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(msg.pSender);
		if (NULL == node) return;			
		CCheckBoxUI* folderBtn	= node->GetFolderButton();
		if (NULL == folderBtn) return;
		if (folderBtn->IsSelected())
		{
			if (!node->IsExpanded())
				node->SetExpanded(false);
			m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
			return;
		}
		else
		{
			node->SetExpanded();
			if (node->GetCountChild() == 0)
			{
				if (m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
				{
					loadOneboxDate(node);
				}
				else if (m_iAction == UPLOADFRAME_DOWNLOAD|| m_iAction == UPLOADFRAME_UPLOAD || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL)
				{
					loadLocallDate(node);
				}
				else if (m_iAction == UPLOADFRAME_SAVETOTEAMSPACE)
				{
					loadTeamspaceDate(node);
				}
			}
			m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
			CHorizontalLayoutUI* phorLayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"uploadFrame_horLayout"));
			if (m_iAction == UPLOADFRAME_COPYMOVE||m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
			{
				m_pCreateBtn->SetVisible();	
				if (NULL != phorLayout)
					phorLayout->SetVisible(false);	
			}
			return;
		}
	}

	void UploadFrame::itemClick(DuiLib::TNotifyUI& msg)
	{
		UploadFrameTreeNode* pControl = static_cast<UploadFrameTreeNode*>(msg.pSender);
		if (pControl == NULL) return;

		BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
		if (bCtrl)
		{
			if (pControl->IsSelected())
			{
				m_select.insert(pControl->GetIndex());
			}
			else if (m_select.find(pControl->GetIndex()) != m_select.end())
			{
				m_select.erase(pControl->GetIndex());
			}
		}
		else
		{
			m_select.clear();
			m_select.insert(pControl->GetIndex());
		}
		CHorizontalLayoutUI* phorLayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"uploadFrame_horLayout"));

		if (NULL == m_pCreateBtn) return;
		if (NULL == phorLayout) return;
		m_pCreateBtn->SetVisible(false);
		phorLayout->SetVisible();
		if(m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE || m_iAction == UPLOADFRAME_COPYMOVE
			||m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
		{
			if( 0 == *(m_select.begin()))
			{
				m_pCreateBtn->SetVisible(false);	
			}
			else
			{
				m_pCreateBtn->SetVisible(true);
			}
			phorLayout->SetVisible(false);
		}

		if (m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL ||
			m_iAction == UPLOADFRAME_DOWNLOAD ||
			m_iAction == UPLOADFRAME_COPYMOVE ||
			m_iAction == UPLOADFRAME_SAVETOMYFILE ||
			m_iAction == UPLOADFRAME_SAVETOTEAMSPACE ||
			m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
		{
			if (m_select.size() > 1 || m_select.size() == 0)
			{
				m_pUploadBtn->SetEnabled(false);
				m_pCopyBtn->SetEnabled(false);
				m_pMoveBtn->SetEnabled(false);
			}
			else
			{
				if (m_iAction ==  UPLOADFRAME_COPYMOVE || m_iAction ==  UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX)
				{
					m_pUploadBtn->SetEnabled(true);
					m_pCopyBtn->SetEnabled(true);
					m_pMoveBtn->SetEnabled(true);
				}
				else
				{
					bool bEnabled = false;
					bool bVisible = false;
					for(std::set<int32_t>::iterator it = m_select.begin(); it != m_select.end(); ++it)
					{
						if (0 == *it)
						{
							bEnabled = true;
							bVisible = false;
							break;
						}

						bVisible = true;
						if (m_iAction !=  UPLOADFRAME_BACKUPTASK_LOCALL)continue;
						UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(*it));
						if(NULL==pNode) continue;						
						if (0 != pNode->nodeData.fileData.basic.parent)continue;
						bEnabled = true;
						bVisible = false;
						break;
					}
					if (m_iAction ==  UPLOADFRAME_BACKUPTASK_LOCALL)
					{
						m_pUploadBtn->SetEnabled(true);							
					}
					else
					{
						m_pUploadBtn->SetEnabled(!bEnabled);
						m_pUploadBtn->SetVisible(bVisible);
					}
					m_pCopyBtn->SetEnabled(!bEnabled);
					m_pMoveBtn->SetEnabled(!bEnabled);
				}
			}
		}
		else if (m_iAction == UPLOADFRAME_UPLOAD)
		{
			if (m_select.size() == 0)
			{
				m_pUploadBtn->SetEnabled(false);
			}
			else
			{
				bool bEnabled = false;
				bool bVisible = false;
				for(std::set<int32_t>::iterator it = m_select.begin(); it != m_select.end(); ++it)
				{
					if (0 == *it)
					{
						bEnabled = true;
						bVisible = false;
						break;
					}					
					UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(*it));
					if(NULL==pNode) continue;
					bVisible = true;
					if (0 != pNode->nodeData.fileData.basic.parent) continue;
					bEnabled = true;
					bVisible = false;
					m_pUploadBtn->SetVisible(false);
					break;
				}
				m_pUploadBtn->SetEnabled(!bEnabled);
				m_pUploadBtn->SetVisible(bVisible);
			}
		}
	}

	void UploadFrame::killFocus(DuiLib::TNotifyUI& msg)
	{
		if (NULL == msg.pSender->GetParent()) return;
		if (NULL == msg.pSender->GetParent()->GetParent()) return;
		UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(msg.pSender->GetParent()->GetParent()->GetParent());
		if (NULL == pNode) return;
		std::wstring str_name = pNode->nodeData.fileData.basic.name;
		std::wstring str_newName = msg.pSender->GetText().GetData();
		str_newName = SD::Utility::String::replace_all(str_newName,L"\r",L" ");
		UploadFrameTreeNode* parentNode = static_cast<UploadFrameTreeNode*>(pNode->GetParentNode());
		if (NULL == parentNode) return;
		int32_t result = -1;
		int tipType = -1;
		std::wstring str_des = L"";
		if (m_iAction == UPLOADFRAME_UPLOAD || m_iAction == UPLOADFRAME_DOWNLOAD || m_iAction == UPLOADFRAME_BACKUPTASK_LOCALL)
		{
			std::wstring str_newPath = parentNode->nodeData.path + L"\\" + str_newName;
			bool bCreateDir = false;
			if (pNode->nodeData.path.empty())//新建
			{
				result = SD::Utility::FS::create_directory(str_newPath);
				if (ERROR_SUCCESS == result)
				{
					if (parentNode->nodeData.path == SD::Utility::FS::get_parent_path(str_newPath))
					{
						pNode->nodeData.path = str_newPath;
						pNode->nodeData.fileData.basic.name = str_newName;
					}
					else
					{
						parentNode->RemoveAt(pNode);
					}
				}
				else
				{
					parentNode->RemoveAt(pNode);//本地创建失败删除节点
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetEnabled(false);
					if (NULL != m_pCreateBtn)
						m_pCreateBtn->SetVisible(false);
					CHorizontalLayoutUI* phorLayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"uploadFrame_horLayout"));
					if (NULL != phorLayout)
						phorLayout->SetVisible();
					tipType = Error;
					str_des = MSG_CREATEDIR_FAILED_KEY;
				}
			}
			else
			{
				if (str_name == str_newName) return;
				result = SD::Utility::FS::rename(pNode->nodeData.path,str_newPath);
				if (ERROR_SUCCESS == result)
				{
					pNode->nodeData = parentNode->nodeData;
					pNode->nodeData.path = str_newPath;
					pNode->nodeData.fileData.basic.name = str_newName;
				}
				else
				{
					msg.pSender->SetText(str_name.c_str());
					tipType = Error;
					str_des = MSG_RENAME_FAILED_KEY;
				}
			}	
		}
		else if (m_iAction == UPLOADFRAME_SAVETOMYFILE || m_iAction == UPLOADFRAME_COPYMOVE || m_iAction == UPLOADFRAME_BACKUPTASK_ONEBOX || m_iAction == UPLOADFRAME_SAVETOTEAMSPACE)
		{
			if (-1 == pNode->nodeData.fileData.basic.id)//新建
			{
				pNode->nodeData = parentNode->nodeData;
				pNode->nodeData.fileData.basic.name = str_newName;
				Path path = pNode->nodeData.fileData.userContext->getPathMgr()->makePath();
				path.id(parentNode->nodeData.fileData.basic.id);
				FILE_DIR_INFO fdi = pNode->nodeData.fileData.basic;
				result = pNode->nodeData.fileData.userContext->getSyncFileSystemMgr()->create(path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
				if (RT_OK == result)
				{
					pNode->nodeData.fileData.basic.id = fdi.id;
				}
				else
				{
					parentNode->RemoveAt(pNode);//创建失败删除节点
					if (NULL != m_pCopyBtn)
						m_pCopyBtn->SetEnabled(false);
					if (NULL != m_pMoveBtn)
						m_pMoveBtn->SetEnabled(false);
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetEnabled(false);
					if (NULL != m_pCreateBtn)
						m_pCreateBtn->SetVisible(false);
					CHorizontalLayoutUI* phorLayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"uploadFrame_horLayout"));
					if (NULL != phorLayout)
						phorLayout->SetVisible();

					if (RT_DIFF_FILTER == result || RT_FILE_EXIST_ERROR == result)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
						simlpeNoticeFrame->Show(Error, MSG_CREATEDIR_FAILED_EX_KEY,  
							ErrorConfMgr::getInstance()->getDescription(result).c_str());
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}
					else
					{
						tipType = Error;
						str_des = MSG_CREATEDIR_FAILED_KEY;
					}
				}
			}
			else
			{
				if (str_name == str_newName) return;
				Path path = pNode->nodeData.fileData.userContext->getPathMgr()->makePath();
				path.id(pNode->nodeData.fileData.basic.id);
				ADAPTER_FILE_TYPE type = (pNode->nodeData.fileData.basic.type == FILE_TYPE_DIR)?ADAPTER_FOLDER_TYPE_REST : ADAPTER_FILE_TYPE_REST;
				result = ProxyMgr::getInstance(userContext_)->rename(pNode->nodeData.fileData.userContext, path, str_newName, type);
				if (RT_OK != result)
				{
					msg.pSender->SetText(str_name.c_str());

					if (RT_DIFF_FILTER == result || RT_FILE_EXIST_ERROR == result)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
						simlpeNoticeFrame->Show(Error, MSG_RENAME_FAILED_EX_KEY,  
							ErrorConfMgr::getInstance()->getDescription(result).c_str());
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}
					else
					{
						tipType = Error;
						str_des = MSG_RENAME_FAILED_KEY;
					}
				}
			}	
		}

		if (-1 != tipType && !str_des.empty())
		{
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
			simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}
	}

	void UploadFrame::Notify(DuiLib::TNotifyUI& msg)
	{
		if( msg.sType == DUI_MSGTYPE_CLICK )
		{
			CDuiString name = msg.pSender->GetName();
			if( (NULL != m_pCloseBtn && msg.pSender == m_pCloseBtn )|| (NULL != m_pCancelBtn && msg.pSender == m_pCancelBtn)) 
			{
				Close();
				return; 
			}
			else if(NULL != m_pUploadBtn && msg.pSender == m_pUploadBtn) 
			{
				uploadClick();
				Close();
				return; 
			}
			else if(NULL != m_pCopyBtn && msg.pSender == m_pCopyBtn) 
			{
				m_itemList.clear();
				for (int i = 0; i < m_oneboxTree->GetCount(); i++)
				{
					UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(i));
					if(NULL==pNode) continue;
					if (pNode->IsSelected())
					{
						m_itemList.push_back(pNode->nodeData);
					}
				}

				if (m_itemList.size() > 1)
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_PaintManager);
					simlpeNoticeFrame->Show(Warning,MSG_SELECTMULTI_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				else
				{
					//复制到我的云盘/团队空间
					m_controlIndex = 0;
					Close();
					return; 
				}
			}
			else if(NULL != m_pMoveBtn && msg.pSender == m_pMoveBtn) 
			{
				m_itemList.clear();
				for (int i = 0; i < m_oneboxTree->GetCount(); i++)
				{
					UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(i));
					if(NULL==pNode) continue;
					if (pNode->IsSelected())
					{
						m_itemList.push_back(pNode->nodeData);
					}
				}
				if (m_itemList.size() > 1)
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_PaintManager);
					simlpeNoticeFrame->Show(Warning,MSG_SELECTMULTI_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				else
				{
					//移动到我的云盘/团队空间
					m_controlIndex = 1;
					Close();
					return; 
				}
			}
			else if(name == ControlNames::BTN_UPLOADFRAME_ITEMSTATE) 
			{
				itemStates(msg);
			}
			else if(name == ControlNames::BTN_CLOSENOTICE)
			{ 
				SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
				simpleNotice->RestoreNoticeArea();
				delete simpleNotice;
				simpleNotice=NULL;
				return; 
			}
			else if (name == L"uploadFrame_create")
			{
				createClick();
			}
		}
		else if( msg.sType == DUI_MSGTYPE_ITEMDBCLICK )
		{
			itemDBClick(msg);
		}
		else if (msg.sType == DUI_MSGTYPE_ITEMCLICK)
		{
			itemClick(msg);
		}
		else if( msg.sType == DUI_MSGTYPE_KILLFOCUS)
		{
			if (msg.pSender->GetName() != L"uploadFrame_itemName") return;	
			killFocus(msg);
		}
		return WindowImplBase::Notify(msg);
	}

	void UploadFrame::ShowFrame(std::list<FileBaseInfo>& destList,int& controlIndex, int iAction, bool isSetParent)
	{		
		std::wstring windowTile = L"";
		m_iAction = iAction;
		switch (m_iAction)
		{
		case UPLOADFRAME_UPLOAD:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY);
			break;
		case UPLOADFRAME_DOWNLOAD:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_DOWNLOAD_KEY);
			break;
		case UPLOADFRAME_COPYMOVE:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_COPYMOVE_KEY);
			break;
		case UPLOADFRAME_SAVETOMYFILE:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_SAVETOONEBOX_KEY);
			break;
		case UPLOADFRAME_SAVETOTEAMSPACE:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_SAVETOTEAMSPACE_KEY);
			break;
		case UPLOADFRAME_BACKUPTASK_LOCALL:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_SELECTCURDIR_KEY);
			break;
		case UPLOADFRAME_BACKUPTASK_ONEBOX:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_SELECTCLOUD_KEY);
			break;
		case UPLOADFRAME_RIGHTUPLOAD:
			windowTile = iniLanguageHelper.GetCommonString(COMMENT_UPLOADTOONEBOX_KEY);
			break;
		default:
			break;
		}

		if (isSetParent)
		{
			Create(paintManager_.GetPaintWindow(),windowTile.c_str(), UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE | WS_EX_APPWINDOW,0 , 0, 0, 0);
		}
		else
		{
			Create(NULL,windowTile.c_str(), UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE | WS_EX_APPWINDOW,0 , 0, 0, 0);
		}

		CHorizontalLayoutUI* phorLayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"uploadFrame_horLayout"));
		if (NULL != m_pCreateBtn)
			m_pCreateBtn->SetVisible(false);
		if (NULL != phorLayout)
			phorLayout->SetVisible();
		if (m_iAction == UPLOADFRAME_UPLOAD)
		{	
			if (NULL != m_title)
				m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY).c_str());
			if (NULL != m_upLoadArea)
				m_upLoadArea->SetVisible();
			if (NULL != m_pUploadBtn)
			{
				m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY).c_str());
				m_pUploadBtn->SetVisible(false);
			}
			if (NULL != m_copyMoveArea)
				m_copyMoveArea->SetVisible(false);
			loadLocallFirstNode();
		}
		else
		{
			switch (m_iAction)
			{
			case UPLOADFRAME_DOWNLOAD:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_DOWNLOAD_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
					{
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_DOWNLOAD_KEY).c_str());
						m_pUploadBtn->SetVisible(false);
					}
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);
					loadLocallFirstNode();
				}
				break;
			case UPLOADFRAME_COPYMOVE:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_COPYMOVE_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible(false);
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible();
					loadOneboxFirstNode();
				}
				break;
			case UPLOADFRAME_SAVETOMYFILE:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_SAVETOONEBOX_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_SAVE_KEY).c_str());
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);		
					loadOneboxFirstNode();
				}
				break;			
			case UPLOADFRAME_SAVETOTEAMSPACE:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_SAVETOTEAMSPACE_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_SAVE_KEY).c_str());
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);
					loadTeamspaceFirstNode();
				}
				break;
			case UPLOADFRAME_BACKUPTASK_LOCALL:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_SELECTCURDIR_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_OK_KEY).c_str());
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);
					loadLocallFirstNode();
				}
				break;
			case UPLOADFRAME_BACKUPTASK_ONEBOX:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_SELECTCLOUD_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_OK_KEY).c_str());
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);
					loadOneboxFirstNode();
				}
				break;
			case UPLOADFRAME_RIGHTUPLOAD:
				{
					if (NULL != m_title)
						m_title->SetText(iniLanguageHelper.GetCommonString(COMMENT_UPLOADTOONEBOX_KEY).c_str());
					if (NULL != m_upLoadArea)
						m_upLoadArea->SetVisible();
					if (NULL != m_pUploadBtn)
						m_pUploadBtn->SetText(iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY).c_str());
					if (NULL != m_copyMoveArea)
						m_copyMoveArea->SetVisible(false);
					loadOneboxFirstNode();
				}
				break;
			default:
				{
					return;
				}
				break;
			}
		}
		CenterWindow();
		ShowModal();
		destList = m_itemList;
		controlIndex = m_controlIndex;
	}

	void UploadFrame::loadLocallFirstNode()
	{
		CDialogBuilder builder;
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(
			builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
		if (NULL == node) return;

		node->nodeData.fileData.basic.name = iniLanguageHelper.GetCommonString(COMMENT_MYCOMPUTER_KEY);
		node->nodeData.fileData.basic.type= FILE_TYPE_DIR;
		node->nodeData.path= L"";
		std::wstring str_iconPath = SkinConfMgr::getInstance()->getIconPath(FILE_TYPE_DIR, L"");
		CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
		if (fileicon != NULL)
		{
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)node);
		}
		CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
		if (filename != NULL)
		{		
			filename->SetText(node->nodeData.fileData.basic.name.c_str());
			filename->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
			if (NULL != horLayout)
			{
				horLayout->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			}
		}
		node->SetToolTip(node->nodeData.fileData.basic.name.c_str());

		if (NULL == m_oneboxTree) return;
		if (m_oneboxTree->GetCount() > 0 )
		{
			m_oneboxTree->RemoveAll();
		}

		if (!m_oneboxTree->Add(node))
		{
			delete node;
			node=NULL;
			return;
		}

		UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(0));
		m_pRootNode = pNode;
	}

	void UploadFrame::loadLocallDate(UploadFrameTreeNode* parentNode,int index)
	{
		std::vector<FileBaseInfo> folders;
		std::vector<FileBaseInfo> files;
		folders.clear();
		files.clear();
		FileBaseInfo fileNode;
		bool bRenameFlag = false;
		if (0 == parentNode->nodeData.path.compare(L""))
		{
			TCHAR path[MAX_PATH];
			if(SHGetSpecialFolderPath(0,path,CSIDL_DESKTOPDIRECTORY,0))
			{
				fileNode.fileData.basic.name = iniLanguageHelper.GetCommonString(COMMENT_DESKTOP_KEY);
				fileNode.fileData.basic.type = FILE_TYPE_DIR;
				fileNode.path = path;
				folders.push_back(fileNode);
			}
			TCHAR buf[100];
			(void)GetLogicalDriveStringsW(sizeof(buf)/sizeof(TCHAR),buf);
			for (TCHAR *str = buf; *str; str+=_tcslen(str)+1)
			{
				if (DRIVE_FIXED == GetDriveType(str))
				{
					std::wstring sDrivePath = str;
					sDrivePath = sDrivePath.substr(0,2);
					fileNode.fileData.basic.parent = 0;
					fileNode.fileData.basic.name = sDrivePath;
					fileNode.fileData.basic.type = FILE_TYPE_DIR;
					fileNode.path = sDrivePath;
					folders.push_back(fileNode);
				}
			}
		}
		else
		{
			if (m_iAction == UPLOADFRAME_UPLOAD)
			{
				bRenameFlag = true;
			}
			WIN32_FIND_DATA fd;
			fd.dwFileAttributes = 0;
			fd.cFileName[0] = _T('');
			HANDLE hFind;
			std::wstring str_allPath = parentNode->nodeData.path;
			str_allPath += L"\\*.*";
			hFind = ::FindFirstFile(str_allPath.c_str(), &fd);
			bool bflag = false;
			bool isDirectoy = false;
			if(hFind != INVALID_HANDLE_VALUE)
			{
				TVITEM tvi;
				(void)memset_s(&tvi, sizeof(tvi), 0, sizeof(tvi));
				HTREEITEM hInsertAfter= 0;
				size_t nFindCount= 0;
				tvi.mask = TVIF_HANDLE| TVIF_CHILDREN|TVIF_PARAM;
				do 
				{
					if (userContext_->getSysConfigureMgr()->isBackupDisableAttr(fd.dwFileAttributes))
					{
						continue;
					}
					if(_tcscmp(fd.cFileName,_T(".")) == 0 || _tcscmp(fd.cFileName,_T("..")) == 0)
					{
						continue;
					}
					fileNode.fileData.basic.name = fd.cFileName;
					fileNode.path = parentNode->nodeData.path  + L"\\"+ fileNode.fileData.basic.name;
					if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
					{
						fileNode.fileData.basic.type = FILE_TYPE_DIR;
						folders.push_back(fileNode);
					} 
					else
					{
						fileNode.fileData.basic.type = FILE_TYPE_FILE;
						files.push_back(fileNode);
					}							
				} while (::FindNextFile(hFind, &fd));
			}
			(void)::FindClose(hFind);
		}
		if (folders.size() <= 0 && files.size() <= 0 )
		{
			//提示获取本地目录错误 
			return;
		}

		std::vector<FileBaseInfo> fileNodes;
		fileNodes.clear();

		for (size_t i =0 ; i < folders.size(); i++)
		{
			fileNodes.push_back(folders[i]);
		}

		for (size_t i =0 ; i < files.size(); i++)
		{
			fileNodes.push_back(files[i]);
		}

		for (int32_t i = fileNodes.size()-1; i >= 0 ;i--)
		{
			CDialogBuilder builder;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(
				builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node) continue;
			if (UPLOADFRAME_DOWNLOAD == m_iAction || UPLOADFRAME_BACKUPTASK_LOCALL == m_iAction)
			{
				if (fileNodes[i].fileData.basic.type != FILE_TYPE_DIR) continue;
			}
			node->nodeData.fileData.basic.name = fileNodes[i].fileData.basic.name;;
			node->nodeData.fileData.basic.type = fileNodes[i].fileData.basic.type;
			node->nodeData.fileData.basic.parent = fileNodes[i].fileData.basic.parent; 
			node->nodeData.path = fileNodes[i].path;

			std::wstring str_iconPath = SkinConfMgr::getInstance()->getIconPath(fileNodes[i].fileData.basic.type, fileNodes[i].fileData.basic.name);

			CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
			if (fileicon != NULL)
			{
				fileicon->SetBkImage(str_iconPath.c_str());
				fileicon->SetTag((UINT_PTR)node);
			}

			CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
			if (filename != NULL)
			{		
				filename->SetText(fileNodes[i].fileData.basic.name.c_str());
				filename->SetToolTip(fileNodes[i].fileData.basic.name.c_str());
				CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
				if (NULL != horLayout)
				{
					horLayout->SetToolTip(fileNodes[i].fileData.basic.name.c_str());
				}
			}
			node->SetToolTip(fileNodes[i].fileData.basic.name.c_str());
			node->setRenameFlag(bRenameFlag);
			if (!parentNode->AddAt(node,index))
			{
				delete node;
				node=NULL;
				// show error message
				// ...
			}
		}
	}

	void UploadFrame::loadOneboxFirstNode()
	{
		CDialogBuilder builder;
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(
			builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
		if (NULL == node) return;

		node->nodeData.fileData.basic.id = rootNode_.fileData.basic.id;
		node->nodeData.fileData.basic.parent = -1;
		node->nodeData.fileData.basic.type = FILE_TYPE_DIR;
		node->nodeData.fileData.userContext = userContext_;
		node->nodeData.nodeType = rootNode_.nodeType;

		std::wstring str_iconPath;
		if(FileNode_FileRoot == rootNode_.nodeType)
		{
			if(UserContext_User==rootNode_.fileData.userContext->id.type)
			{
				str_iconPath = L"..\\img\\onebox_small.png";
				node->nodeData.fileData.basic.name = iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY);
			}
			else if(UserContext_Teamspace==rootNode_.fileData.userContext->id.type)
			{
				str_iconPath = L"..\\img\\teamspace_small.png";
				node->nodeData.fileData.basic.name = iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY) 
					+ L"(" + rootNode_.fileData.userContext->id.name +L")";
			}
		}
		else if(FileNode_Rest == rootNode_.nodeType)
		{
			//共享编辑者权限，移动时，根节点为文件夹
			str_iconPath = SkinConfMgr::getInstance()->getIconPath(rootNode_.fileData.basic.type, rootNode_.fileData.basic.name);
			node->nodeData.fileData.basic.name = rootNode_.fileData.basic.name
				+ L"(" + rootNode_.fileData.userContext->id.name +L")";
		}

		CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
		if (fileicon != NULL)
		{
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)node);
		}

		CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
		if (filename != NULL)
		{		
			filename->SetText(node->nodeData.fileData.basic.name.c_str());
			filename->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
			if (NULL != horLayout)
			{
				horLayout->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			}
		}
		node->SetToolTip(node->nodeData.fileData.basic.name.c_str());

		if (NULL == m_oneboxTree) return;

		if (!m_oneboxTree->Add(node))
		{
			delete node;
			node=NULL;
			return;
		}
		UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(0));
		m_pRootNode = pNode;
	}

	void UploadFrame::loadOneboxDate(UploadFrameTreeNode* parentNode,int index)
	{		
		Path listPath = parentNode->nodeData.fileData.userContext->getPathMgr()->makePath();
		listPath.id(parentNode->nodeData.fileData.basic.id);
		LIST_FOLDER_RESULT lfResult;
		if (0 != parentNode->nodeData.fileData.userContext->getSyncFileSystemMgr()->listFolder(listPath,lfResult,ADAPTER_FOLDER_TYPE_REST))
		{
			//提示获取listfolder错误 
			return;
		}
		lfResult.sort(CompareWithUFNDesc);
		for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
		{
			CDialogBuilder builder;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(
				builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node) continue;;
			if (it->type != FILE_TYPE_DIR) continue;
			// fill in file node information
			node->nodeData.fileData.basic.id = it->id;
			node->nodeData.fileData.basic.name = it->name;
			node->nodeData.fileData.basic.parent = it->parent;
			node->nodeData.fileData.basic.type = it->type;
			node->nodeData.fileData.userContext = parentNode->nodeData.fileData.userContext;

			std::wstring str_iconPath = SkinConfMgr::getInstance()->getIconPath(it->type, it->name);

			CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
			if (fileicon != NULL)
			{
				fileicon->SetBkImage(str_iconPath.c_str());
				fileicon->SetTag((UINT_PTR)node);
			}

			CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
			if (filename != NULL)
			{		
				filename->SetText(it->name.c_str());
				filename->SetToolTip(it->name.c_str());
				CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
				if (NULL != horLayout)
				{
					horLayout->SetToolTip(it->name.c_str());
				}
			}
			node->SetToolTip(it->name.c_str());

			if (!parentNode->AddAt(node,index))
			{
				delete node;
				node=NULL;
				// show error message
				// ...
			}
		}

		if (NULL != m_pUploadBtn)
			m_pUploadBtn->SetEnabled();		
		if (NULL != m_pCopyBtn)
			m_pCopyBtn->SetEnabled();
		if (NULL != m_pMoveBtn)
			m_pMoveBtn->SetEnabled();
	}

	void UploadFrame::loadTeamspaceFirstNode()
	{
		CDialogBuilder builder;
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(
			builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
		if (NULL == node) return;

		node->nodeData.fileData.basic.id = -1;
		node->nodeData.fileData.basic.parent = -1;
		node->nodeData.fileData.basic.name = iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY);
		node->nodeData.fileData.basic.type = FILE_TYPE_DIR;
		node->nodeData.fileData.userContext = userContext_;
		node->nodeData.nodeType = FileNode_TeamSpaceRoot;
		std::wstring str_iconPath = L"..\\img\\teamspace_small.png";
		CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
		if (fileicon != NULL)
		{
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)node);
		}

		CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
		if (filename != NULL)
		{		
			filename->SetText(node->nodeData.fileData.basic.name.c_str());
			filename->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
			if (NULL != horLayout)
			{
				horLayout->SetToolTip(node->nodeData.fileData.basic.name.c_str());
			}
		}
		node->SetToolTip(node->nodeData.fileData.basic.name.c_str());

		if (NULL == m_oneboxTree) return;

		if (!m_oneboxTree->Add(node))
		{
			return;
		}
		UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(0));
		m_pRootNode = pNode;
	}

	void UploadFrame::loadTeamspaceDate(UploadFrameTreeNode* parentNode,int index)
	{
		if (FileNode_TeamSpaceRoot == parentNode->nodeData.nodeType)
		{
			std::vector<FileBaseInfo> fileNodes;
			fileNodes.clear();
			FileBaseInfo fileNode;
			UserTeamSpaceNodeInfoArray  teamspaceListArray_;
			PageParam pageparam_;
			userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray_,pageparam_);
			for (size_t i = 0; i < teamspaceListArray_.size(); ++i)
			{
				if ("viewer" == teamspaceListArray_[i].role()) continue;
				CDialogBuilder builder;
				Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;;
				node->nodeData.fileData.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					teamspaceListArray_[i].teamId(), UserContext_Teamspace, String::utf8_to_wstring(teamspaceListArray_[i].member_.name()));
				node->nodeData.nodeType = FileNode_FileRoot;
				node->nodeData.fileData.basic.id = 0;
				std::wstring str_iconPath = L"..\\img\\teamspace_small.png";

				CLabelUI* fileicon = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
				if (fileicon != NULL)
				{
					fileicon->SetBkImage(str_iconPath.c_str());
					fileicon->SetTag((UINT_PTR)node);
				}

				CRenameRichEditUI* filename = static_cast<CRenameRichEditUI*>(paintManager_.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
				std::wstring str_name = String::utf8_to_wstring(teamspaceListArray_[i].member_.name());
				if (filename != NULL)
				{		
					filename->SetText(str_name.c_str());

					filename->SetToolTip(str_name.c_str());
					CHorizontalLayoutUI* horLayout = static_cast<CHorizontalLayoutUI*>(filename->GetParent());
					if (NULL != horLayout)
					{
						horLayout->SetToolTip(str_name.c_str());
					}
				}
				node->SetToolTip(str_name.c_str());
				if (!parentNode->AddAt(node,index))
				{
					delete node;
					node=NULL;
					// show error message
					// ...
				}
			}
		}
		else
		{
			loadOneboxDate(parentNode, index);
		}
	}
}