#include "stdafx.h"
#include "UploadFrame.h"
#include "DialogBuilderCallbackImpl.h"
#include "Utility.h"
#include "ThriftClient.h"
#include "ControlNames.h"
#include "CommonDefine.h"
#include <boost/asio.hpp> 
#include "InIHelper.h"
#include "InILanguage.h"
#include "Global.h"

namespace Onebox
{

	const wchar_t* NodeIcon::MYFILE_ROOTNODE_ICON = L"onebox_small.png";
	const wchar_t* NodeIcon::TEAMSPACE_ROOTNODE_ICON = L"teamspace_small.png";
	const wchar_t* NodeIcon::NORMALNODE_ICON = L"icon_folder.png";

	void UploadFrameTreeNode::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			else CContainerUI::DoEvent(event);
			return;
		}

		if( event.Type == UIEVENT_DBLCLICK )
		{
			if( IsEnabled() ) {
				m_pManager->SendNotify(this, _T("itemdbclick"));
				if(!m_bSelected) Select();
				Invalidate();
			}
			return;
		}
		CTreeNodeUI::DoEvent(event);
	}

	DUI_BEGIN_MESSAGE_MAP(UploadFrame,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK,OnDBClick)
	DUI_END_MESSAGE_MAP()

	DestNodeInfo UploadFrame::m_selectedNode;

	UploadFrame::UploadFrame(std::wstring wstrPath):m_sourceFilePath(wstrPath)
	{
		m_userID = SyncServiceClientWrapper::getInstance()->getCurrentUserId();
	}

	UploadFrame::UploadFrame(std::wstring wstrPath, bool showTaskAddNotice):m_sourceFilePath(wstrPath),isShowTaskAddNotice(showTaskAddNotice)
	{
		m_userID = SyncServiceClientWrapper::getInstance()->getCurrentUserId();
	}

	UploadFrame::~UploadFrame()
	{
	}

	void UploadFrame::OnFinalMessage( HWND hWnd )
	{
		WindowImplBase::OnFinalMessage(hWnd);
		delete this;
	}

	void UploadFrame::InitWindow()
	{
		IniLanguageHelper iniLanguageHelper;
		CLabelUI* frameTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(ControlNames::LABEL_UPLOADFRAME_TITLE));
		frameTitle->SetText((getShortPath(m_sourceFilePath,35) + L" " + iniLanguageHelper.GetCommonString(LANGUAGE_COMMON_UPLOADTO_KEY)).c_str());
		frameTitle->SetToolTip(m_sourceFilePath.c_str());
		m_oneboxTree = static_cast<CTreeViewUI*>(m_PaintManager.FindControl(ControlNames::TREE_UPLOADFRAME_NODE));
		CDialogBuilder builderTeamSpace;
		pTeamSpaceNode_ = static_cast<Onebox::UploadFrameTreeNode*>(builderTeamSpace.Create(ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
		pTeamSpaceNode_->nodeData.id = -1;
		pTeamSpaceNode_->nodeData.flags = 0;
		pTeamSpaceNode_->nodeData.name = SD::Utility::String::wstring_to_utf8(iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY));
		pTeamSpaceNode_->nodeData.type = FILE_TYPE_DIR;
		pTeamSpaceNode_->isRootNode = true;

		std::wstring str_iconPath1 = NodeIcon::TEAMSPACE_ROOTNODE_ICON;

		CLabelUI* fileicon1 = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(pTeamSpaceNode_, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
		if (fileicon1 != NULL)
		{
			fileicon1->SetBkImage(str_iconPath1.c_str());
			fileicon1->SetTag((UINT_PTR)pTeamSpaceNode_);
		}

		CRichEditUI* filename1 = static_cast<CRichEditUI*>(m_PaintManager.FindSubControlByName(pTeamSpaceNode_, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
		if (filename1 != NULL)
		{		
			filename1->SetText(SD::Utility::String::utf8_to_wstring(pTeamSpaceNode_->nodeData.name).c_str());
		}
		pTeamSpaceNode_->SetToolTip(SD::Utility::String::utf8_to_wstring(pTeamSpaceNode_->nodeData.name).c_str());

		if (NULL == m_oneboxTree) return;

		if (!m_oneboxTree->Add(pTeamSpaceNode_))
		{
			return;
		}

		CDialogBuilder builder;
		pMyFileRootNode_ = static_cast<Onebox::UploadFrameTreeNode*>(builder.Create(ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
		pMyFileRootNode_->nodeData.id = 0;
		pMyFileRootNode_->nodeData.flags = 0;
		pMyFileRootNode_->nodeData.name = SD::Utility::String::wstring_to_utf8(iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY));
		pMyFileRootNode_->nodeData.type = FILE_TYPE_DIR;
		pMyFileRootNode_->isRootNode = true;

		std::wstring str_iconPath = NodeIcon::MYFILE_ROOTNODE_ICON ;

		CLabelUI* fileicon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(pMyFileRootNode_, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
		if (fileicon != NULL)
		{
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)pMyFileRootNode_);
		}

		CRichEditUI* filename = static_cast<CRichEditUI*>(m_PaintManager.FindSubControlByName(pMyFileRootNode_, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
		if (filename != NULL)
		{		
			filename->SetText(SD::Utility::String::utf8_to_wstring(pMyFileRootNode_->nodeData.name).c_str());
		}
		pMyFileRootNode_->SetToolTip(SD::Utility::String::utf8_to_wstring(pMyFileRootNode_->nodeData.name).c_str());

		if (NULL == m_oneboxTree) return;

		if (!m_oneboxTree->Add(pMyFileRootNode_))
		{
			return;
		}
		pMyFileRootNode_->Select(true);
		::SetTimer(this->GetHWND(),(UINT)AUTOEXPAND_TIMER,100,NULL); 
	}

	LPCTSTR UploadFrame::GetWindowClassName(void) const
	{
		return ControlNames::WND_UPLOADFRAME_CLS_NAME;
	}

	CDuiString  UploadFrame::GetSkinFolder()
	{
		std::wstring wstrInstallPath = GetInstallPath();
 		wstrInstallPath += std::wstring(L"Res\\");
 		return wstrInstallPath.c_str();
	}

	CDuiString  UploadFrame::GetSkinFile()
	{
		IniLanguageHelper iniLanguageHelper;
		if (UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage())
		{
			return ControlNames::SKIN_XML_UPLOADFRAMECN;
		}
		return ControlNames::SKIN_XML_UPLOADFRAMEEN;
	}

	LRESULT UploadFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;

		switch (uMsg)
		{
		case WM_TIMER:
			{
				if ((UINT)UPLOADFRAME_TIMER ==wParam)
				{
					OnUploadFrameTimer(wParam);
					break;
				}
				else if((UINT)NOTICEFRAME_TIMER == wParam)
				{
					OnNoticeMsgTimer(wParam);
					break;
				}
				else if((UINT)AUTOEXPAND_TIMER == wParam)
				{
					AutoExpandNode(wParam);
					break;
				}
				else
				{
					bHandled = FALSE;
					break;
				}
				
			}
		default:
			bHandled = FALSE;
			break;
		}

		if (bHandled)
		{
			return lRes;
		}

		return WindowImplBase::HandleMessage(uMsg,wParam,lParam);
	}

	 DuiLib::CControlUI* UploadFrame::CreateControl(LPCTSTR pstrClass)
	 {
		 return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	 }

	 bool UploadFrame::InitLanguage(CControlUI* control)
	 {
// 		 CInIHelper InIHelper(SD::Utility::String::format_string(iniLanguageHelper.GetLanguageFilePath().c_str()));
// 		 std::wstring TextKey = control->GetName().GetData();
// 		 TextKey += LANGUAGE_DEFAULT_TEXT_KEY;
// 		 std::wstring ToolTipKey = control->GetName().GetData();
// 		 ToolTipKey+=LANGUAGE_DEFAULT_TOOLTIP_KEY;
// 		 std::wstring controlText =  InIHelper.GetString(LANGUAGE_FRAMELANGUAGEINFO_SECTION,TextKey,L"");
// 		 std::wstring controlToolTip=  InIHelper.GetString(LANGUAGE_FRAMELANGUAGEINFO_SECTION,ToolTipKey,L"");
// 		 if (_tcsicmp(L"",controlText.c_str()) != 0)
// 		 {
// 			 control->SetText(controlText.c_str());
// 		 }
// 		 if (_tcsicmp(L"",controlToolTip.c_str()) != 0)
// 		 {
// 			 control->SetToolTip(controlToolTip.c_str());
// 		 }

		 return true;
	 }

	void UploadFrame::OnClick(TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();
		if (name == ControlNames::BTN_UPLOADFRAME_CLOSE || name == ControlNames::BTN_UPLOADFRAME_CANCEL)
		{
		    Close();
			return;
		}
		else if (name == ControlNames::BTN_UPLOADFRAME_UPLOAD)
		{
			Btn_Upload_Clicked();
		}
		else if(name == ControlNames::BTN_UPLOADFRAME_ITEMSTATE) 
		{
			CCheckBoxUI* folderBtn = static_cast<CCheckBoxUI*>(msg.pSender);
			if (NULL == folderBtn)  return;
			if (_tcsicmp(L"CheckBoxUI",folderBtn->GetClass()) != 0) return;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(folderBtn->GetParent()->GetParent());
			if (NULL == node) return;
			if (node->GetVisibleTag() == folderBtn->IsSelected())
			{
				std::string rootName = "";
				UploadFrameTreeNode* rootNode = static_cast<Onebox::UploadFrameTreeNode*>(GetSecondNode(node)->GetParentNode());
				if(NULL == rootNode)
				{
					rootName = node->nodeData.name;
				}
				else
				{
					rootName = rootNode->nodeData.name;
				}
				IniLanguageHelper iniLanguageHelper;
				if (iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY)==SD::Utility::String::utf8_to_wstring(rootName))
				{
					LoadRemoteDir(node);
				}
				else
				{
					if (node->isRootNode)
					{
						LoadTeamSpace(node);
					}
					else
					{
						LoadTeamSpaceDir(node);
					}
				}
			}
		}
		else if (name == ControlNames::BTN_CLOSENOTICE)
		{
			NoticeFrame* notice = new NoticeFrame(m_PaintManager);
			notice->Store();
			delete notice;
			notice =NULL;
		}
	}

	void UploadFrame::OnDBClick(TNotifyUI& msg)
	{
		UploadFrameTreeNode* node = static_cast<UploadFrameTreeNode*>(msg.pSender);
		if(NULL == node)return;
		std::string rootName = "";
		if(NULL == GetSecondNode(node)->GetParentNode())
		{
			rootName = node->nodeData.name;
		}
		else
		{
			rootName = (static_cast<Onebox::UploadFrameTreeNode*>(GetSecondNode(node)->GetParentNode()))->nodeData.name;
		}
		IniLanguageHelper iniLanguageHelper;
		if (iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY)==SD::Utility::String::utf8_to_wstring(rootName))
		{
			CCheckBoxUI* folderBtn	= node->GetFolderButton();
			if (NULL == folderBtn) return;
			if (folderBtn->IsSelected())
			{
				folderBtn->Selected(folderBtn->IsSelected());
				m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
				return;
			}
			else
			{
				folderBtn->Selected(folderBtn->IsSelected());
				if (node->GetCountChild() == 0)
				{
					LoadRemoteDir(node);
				}
				m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
				return;
			}
		}
		else 
		{
			CCheckBoxUI* folderBtn	= node->GetFolderButton();
			if (NULL == folderBtn) return;
			if (folderBtn->IsSelected())
			{
				folderBtn->Selected(folderBtn->IsSelected());
				m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
				return;
			}
			else
			{
				folderBtn->Selected(folderBtn->IsSelected());
				if (node->GetCountChild() == 0)
				{
					if (node->isRootNode)
					{
						LoadTeamSpace(node);
					}
					else
					{
						LoadTeamSpaceDir(node);
					}
				}
				m_oneboxTree->SetItemExpand(!folderBtn->IsSelected(),node);
				return;
			}
		}
	}

	void UploadFrame::LoadRemoteDir(UploadFrameTreeNode* parentNode,int index)
	{
		std::vector<File_Node>  lfResult;
		std::string rootName = "";
		UploadFrameTreeNode* rootNode = static_cast<UploadFrameTreeNode*>(GetSecondNode(parentNode)->GetParentNode());
		if (NULL == rootNode)
		{
			rootName = GetSecondNode(parentNode)->nodeData.name;
		}
		else
		{
			rootName = rootNode->nodeData.name;
		}
		IniLanguageHelper iniLanguageHelper;
		if (iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY)==SD::Utility::String::utf8_to_wstring(rootName))
		{
			SyncServiceClientWrapper::getInstance()->listRemoteDir(lfResult,parentNode->nodeData.id,m_userID,UserContext_User);
		}
		else
		{
			SyncServiceClientWrapper::getInstance()->listRemoteDir(lfResult,parentNode->nodeData.id,GetSecondNode(parentNode)->nodeData.id,UserContext_Teamspace);
		}

		
		for (size_t i = 0; i < lfResult.size(); ++i)
		{
			CDialogBuilder builder;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
			if (NULL == node)
			{
				return;
			}
			if (lfResult[i].type!= FILE_TYPE_DIR) 
			{
				continue;
			}
			node->nodeData.flags = lfResult[i].flags;
			node->nodeData.id = lfResult[i].id;
			node->nodeData.type = lfResult[i].type;
			node->nodeData.name = lfResult[i].name;

			CLabelUI* fileicon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
			if (fileicon != NULL)
			{
				fileicon->SetBkImage(NodeIcon::NORMALNODE_ICON);
				fileicon->SetTag((UINT_PTR)node);
			}

			CRichEditUI* filename = static_cast<CRichEditUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
			if (filename != NULL)
			{		
				filename->SetText(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());
			}
			node->SetToolTip(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());

			if (!parentNode->AddAt(node,index))
			{
				// show error message
				// ...
			}
		}
	}

	UploadFrameTreeNode* UploadFrame::GetSecondNode(UploadFrameTreeNode* Node)
	{
		UploadFrameTreeNode* _return = Node;
		while (true)
		{
			UploadFrameTreeNode* tmp = static_cast<UploadFrameTreeNode*>(_return->GetParentNode());
			if (tmp==NULL || tmp->isRootNode)
			{
				break;
			}
			else 
			{
				_return = tmp;
				continue;
			}
		}

		return _return;
	}

	void UploadFrame::LoadTeamSpace(UploadFrameTreeNode* parentNode,int index)
	{
		std::vector<TeamSpace_Node>  lfResult;
		SyncServiceClientWrapper::getInstance()->listTeamspace(lfResult);
		for (size_t i = 0; i < lfResult.size(); ++i)
		{
			CDialogBuilder builder;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
			if (NULL == node)
			{
				return;
			}

			node->nodeData.id = lfResult[i].id;
			node->nodeData.name = lfResult[i].name;

			CLabelUI* fileicon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
			if (fileicon != NULL)
			{
				fileicon->SetBkImage(NodeIcon::NORMALNODE_ICON);
				fileicon->SetTag((UINT_PTR)node);
			}

			CRichEditUI* filename = static_cast<CRichEditUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
			if (filename != NULL)
			{		
				filename->SetText(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());
			}
			node->SetToolTip(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());

			if (!parentNode->AddAt(node,index))
			{
				// show error message
				// ...
			}
		}
	}

	void UploadFrame::LoadTeamSpaceDir(UploadFrameTreeNode* parentNode,int index)
	{
		std::vector<File_Node>  lfResult;

		File_Node filenode;
		if (static_cast<UploadFrameTreeNode*>(parentNode->GetParentNode())->nodeData.id == -1)
		{
			filenode.id=0;
		}
		else
		{
			filenode.id=parentNode->nodeData.id;
		}

		filenode.name=parentNode->nodeData.name;
		SyncServiceClientWrapper::getInstance()->listRemoteDir(lfResult,filenode.id,GetSecondNode(parentNode)->nodeData.id,UserContext_Teamspace);
		for (size_t i = 0; i < lfResult.size(); ++i)
		{
			CDialogBuilder builder;
			Onebox::UploadFrameTreeNode* node = static_cast<Onebox::UploadFrameTreeNode*>(builder.Create(Onebox::ControlNames::SKIN_XML_UPLOADFRAME_TREENODE, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
			if (NULL == node)
			{
				return;
			}
			if (lfResult[i].type != FILE_TYPE_DIR)
			{
				return;
			}

			node->nodeData.id = lfResult[i].id;
			node->nodeData.name = lfResult[i].name;

			CLabelUI* fileicon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::LABEL_UPLOADFRAME_ITEMICON));
			if (fileicon != NULL)
			{
				fileicon->SetBkImage(NodeIcon::NORMALNODE_ICON);
				fileicon->SetTag((UINT_PTR)node);
			}

			CRichEditUI* filename = static_cast<CRichEditUI*>(m_PaintManager.FindSubControlByName(node, ControlNames::RICHEDIT_UPLOADFRAME_ITEMNAME));
			if (filename != NULL)
			{		
				filename->SetText(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());
			}
			node->SetToolTip(SD::Utility::String::utf8_to_wstring(lfResult[i].name).c_str());

			if (!parentNode->AddAt(node,index))
			{
				// show error message
				// ...
			}
		}
	}

	LRESULT UploadFrame::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient;
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

	int32_t UploadFrame::Btn_Upload_Clicked()
	{
		int32_t iRet = 0;
		CButtonUI* UploadBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_UPLOADFRAME_UPLOAD));
		UploadBtn->SetEnabled(false);
		NoticeFrame* noticeFrame =  new NoticeFrame(m_PaintManager);
		UploadFrameTreeNode* pNode = static_cast<UploadFrameTreeNode*>(m_oneboxTree->GetItemAt(m_oneboxTree->GetCurSel()));
		if (NULL == pNode || pNode->nodeData.id==-1)
		{
			noticeFrame->Show(MSG_UPLOAD_UPLOAD_SELECTDIR_FAILED,Error);
			UploadBtn->SetEnabled(true);
			return RT_ERROR;
		}
		std::string rootName = "";
		UploadFrameTreeNode* SecondNode = GetSecondNode(pNode);
		UploadFrameTreeNode* rootNode = static_cast<UploadFrameTreeNode*>(SecondNode->GetParentNode());
		if (NULL == rootNode)
		{
			rootName = SecondNode->nodeData.name;
		}
		else
		{
			rootName = rootNode->nodeData.name;
		}
		IniLanguageHelper iniLanguageHelper;
		if (iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY)==SD::Utility::String::utf8_to_wstring(rootName))
		{
			m_selectedNode.nodeID = pNode->nodeData.id;
			m_selectedNode.userID =  m_userID;
			m_selectedNode.userContextType = UserContext_User;
		}
		else
		{
			int64_t teamSpaceFileID = 0;
			if (SecondNode->nodeData.id != pNode->nodeData.id)
			{
				teamSpaceFileID = pNode->nodeData.id;
			}
			m_selectedNode.nodeID = teamSpaceFileID;
			m_selectedNode.userID =  SecondNode->nodeData.id;
			m_selectedNode.userContextType = UserContext_Teamspace;
		}
		
		iRet = SyncServiceClientWrapper::getInstance()->upload(m_sourceFilePath,Onebox::UploadFrame::GetSelectedNode().nodeID,Onebox::UploadFrame::GetSelectedNode().userID,Onebox::UploadFrame::GetSelectedNode().userContextType);
		if(isShowTaskAddNotice && 0==iRet)
		{
			noticeFrame->Show(MSG_UPLOAD_SUCCESSFUL_KEY,Info);
			::SetTimer(this->GetHWND(),(UINT)UPLOADFRAME_TIMER,3000,NULL); 
		}
		else
		{
			Close();
		}
	
		return iRet;
	}

	void UploadFrame::OnUploadFrameTimer(UINT nIDEvent)
	{
		Close();
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	void UploadFrame::OnNoticeMsgTimer(UINT nIDEvent)
	{
		NoticeFrame* notice = new NoticeFrame(m_PaintManager);
		notice->Store();
		delete notice;
		notice =NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	DestNodeInfo UploadFrame::GetSelectedNode()
	{
		return m_selectedNode;
	}

	void UploadFrame::AutoExpandNode(UINT nIDEvent)
	{
		TEventUI event;
		event.Type = UIEVENT_DBLCLICK;
		pMyFileRootNode_->DoEvent(event);
		::KillTimer(this->GetHWND(),nIDEvent);
	}
}