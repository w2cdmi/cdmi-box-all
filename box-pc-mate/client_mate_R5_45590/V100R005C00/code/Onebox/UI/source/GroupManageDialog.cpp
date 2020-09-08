#include "stdafxOnebox.h"
#include "GroupManageDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "ListContainerElement.h"
#include "ShareResMgr.h"
#include "GroupResMgr.h"
#include "ProxyMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"

namespace Onebox{

	GroupManageDialog::GroupManageDialog(UserContext* context,UserGroupNodeInfo& data)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_pCloseOtherBtn(NULL)
		,m_pAddManage(NULL)
		,m_pChange(NULL)
		,m_pCombo(NULL)
		,m_list(NULL)
		,m_listViewer(NULL)
		,m_listUser(NULL)
		,m_tileLayoutList(NULL)
		,m_groupData(data)
		,m_lastText(L"")
	{
	}

	GroupManageDialog::~GroupManageDialog()
	{
	}


	LPCTSTR GroupManageDialog::GetWindowClassName() const 
	{ 
		return _T("GroupManageDialog");
	}

	UINT GroupManageDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void GroupManageDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
	}

	CControlUI* GroupManageDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool GroupManageDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void GroupManageDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			std::wstring name = msg.pSender->GetName().GetData();
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn || msg.pSender == m_pCloseOtherBtn ||
				msg.pSender == m_pCloseBtnViewer || msg.pSender == m_pCancelBtnViewer)
			{
				Close(0);
				if (m_pCombo == NULL) return;
				m_pCombo->RemoveAll();
				return; 
			}
			else if (msg.pSender == m_pOkBtn)
			{// create Group
				std::vector<int> sucess,fail;
				if (m_tileLayoutList->GetCount() == 0)
				{
					Close();
					return;
				}
				if (m_tileLayoutList->GetCount() > 0)
				{					
					UserContext* teamspaceContext = UserContextMgr::getInstance()->createUserContext(userContext_, 
						m_groupData.groupId(), UserContext_Group, 
						SD::Utility::String::string_to_wstring(m_groupData.member_.name()));
					for (int i=0;i<m_tileLayoutList->GetCount();i++)
					{
						UserGroupNodeInfo info;
						GroupManageTileLayoutListContainerElement* pItem = static_cast<GroupManageTileLayoutListContainerElement*>(m_tileLayoutList->GetItemAt(i));
						if (pItem == NULL)  continue;
						std::string type = pItem->nodeData.groupNode.id != NULL ? "group" : "user";	
						int64_t id = pItem->nodeData.groupNode.id != NULL ? pItem->nodeData.groupNode.id : pItem->nodeData.basic.id();
						userContext_->getGroupMgr()->AddGroupMember(info,m_groupData.groupId(),type,id,
																SD::Utility::String::wstring_to_utf8(pItem->nodeData.groupRole));
						if (info.id() != 0)
						{
							sucess.push_back(i);
						}
						else
						{
							fail.push_back(i);
						}
					}
				}
				if (fail.size() == m_tileLayoutList->GetCount())
				{// all fail
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Info,MSG_GROUP_ADD_MANAGER_FAIL_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
				}
				else if ((size_t)fail.size() < (size_t)m_tileLayoutList->GetCount() && fail.size() != 0)
				{// part fail
					for (size_t i = 0;i < sucess.size();i++)
					{
						m_tileLayoutList->RemoveAt(i);
					}
					loadlistuser();
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Info,MSG_GROUP_ADD_MANAGER_PART_FAIL_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
				}
				else if (fail.size() == 0)
				{
					Close();
				}
			}
			else if (name == L"GroupManage_TileLayoutListItem_delete_btn")
			{
				GroupManageTileLayoutListContainerElement* pContorl = static_cast<GroupManageTileLayoutListContainerElement*>(msg.pSender->GetParent()->GetParent());
				if (pContorl == NULL) return;
				m_tileLayoutList->Remove(pContorl);
			}
			else if (name == L"GroupManage_item_delete")
			{
				GroupManageListContainerElement* pContorl = static_cast<GroupManageListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (pContorl == NULL) return;
				if(RT_OK == userContext_->getGroupMgr()->DeleteGroupMember(m_groupData.groupId(),pContorl->nodeData.basic.id()))
				{
					loadlistuser();
				}
			}
		}
		else if ( msg.sType == _T("setfocus") )
		{
			if ( msg.pSender == m_pAddManage )
			{
				std::wstring keyWord = m_pAddManage->GetText().GetData();
				if (keyWord == iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_TEAMSPACE_KEY))
				{
					m_pAddManage->SetText(L"");
					m_listUser->SetVisible(false);
				}
				if (m_pChange == NULL || m_pCombo == NULL) return;
				if (m_pChange->IsVisible()) return;
				m_pChange->SetVisible();
				m_pCloseOtherBtn->SetVisible(false);
				m_pOkBtn->SetVisible();
				m_pCancelBtn->SetVisible();
				m_pCombo->SelectItem(1,true);
				RECT rc;
				::GetWindowRect(GetHWND(),&rc);	

				int width = rc.right - rc.left;
				int height = rc.bottom - rc.top + GROUP_MANAGE_DIALOG_CHANGE_DISTANCE;
				rc.bottom += GROUP_MANAGE_DIALOG_CHANGE_DISTANCE;
				::MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
				::InvalidateRect(*this,&rc,true);
				m_pAddManage->SetEnabled();				 
			}
		}
		else if (msg.sType == _T("killfocus"))
		{
			if (msg.pSender != m_pAddManage) return;
			if (m_listUser != NULL && m_listUser->GetCount() > 0)
			{
				RECT rc = m_listUser->GetPos();
				if(!PtInRect(&rc,msg.ptMouse))
				{
					m_pAddManage->SetText(iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_TEAMSPACE_KEY).c_str());
					m_lastText = L"";
					m_listUser->RemoveAll();
					m_listUser->SetVisible(false);
				}
			}
			std::wstring text = m_pAddManage->GetText().GetData();
			if (text.empty())
			{
				m_pAddManage->SetText(iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_TEAMSPACE_KEY).c_str());
			}
		}
		else if (msg.sType == _T("return"))
		{
			m_listUser->RemoveAll();
			int sCount = SetGroupList();
			int uCount = SetUserList();
			if ((sCount + uCount) == 0)
			{
				SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
				simlpeNoticeFrame->Show(Warning,MSG_GROUP_MANAGER_SETTING_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
		}
		else if (msg.sType == _T("itemclick"))
		{
			std::wstring name = msg.pSender->GetName().GetData();
			if (name == L"GroupManage_listUserItem")
			{
				GroupManageListUserContainerElement* item = static_cast<GroupManageListUserContainerElement*>(msg.pSender);
				if (item == NULL) return;
				if (m_pCombo != NULL)
				{
					std::wstring name = m_pCombo->GetText().GetData();
					if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY))
					{
						item->nodeData.groupRole = L"manager";
					}
					else if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY))
					{
						item->nodeData.groupRole = L"member";
					}
					else if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY))
					{
						item->nodeData.groupRole = L"member";
					}
				}
				AddTileLayoutItem(item->nodeData);
				m_listUser->RemoveAll();
				m_listUser->SetVisible(false);
				m_pAddManage->SetText(iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_GROUP_KEY).c_str());
				m_lastText = L"";
			}
		}
		else if (msg.sType == L"textchanged")
		{
		}
		else if (msg.sType == _T("itemselect"))
		{
			std::wstring name = msg.pSender->GetName().GetData();
			if (name == L"GroupManage_item_Combo")
			{
				UserGroupNodeInfo info;
				std::string groupRole;
				std::wstring text = msg.pSender->GetText();				
				GroupManageListContainerElement* pContorl = static_cast<GroupManageListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (pContorl == NULL) return;
				if (text == iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY))
				{
					groupRole = "manager";
				}
				else if (text == iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY))
				{
					groupRole = "member";
				}
				else if (text == iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY))
				{
					groupRole = "member";
				}
				userContext_->getGroupMgr()->UpdateGroupUserInfo(info,m_groupData.groupId(),pContorl->nodeData.basic.id(),groupRole);
				if (!info.groupRole().empty())
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_TEAMSPACE_MANAGER_SUCCESS_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
				}
				else
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_TEAMSPACE_MANAGER_FAIL_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
				}
			}
		}
	}

	int GroupManageDialog::SetUserList()
	{
		if (NULL == m_pAddManage || NULL == userContext_) return 0;
		ShareUserInfoList shareUserInfos;
		std::wstring SearchKey = m_pAddManage->GetText().GetData();
		m_lastText = SearchKey;
		ProxyMgr::getInstance(userContext_)->listDomainUsers(SD::Utility::String::wstring_to_utf8(SearchKey), shareUserInfos);
		if (0 == shareUserInfos.size())
		{			
			return 0;
		}
		if (1 == shareUserInfos.size())
		{
			UIGroupManageUserNode node;
			node.basic = shareUserInfos[0];
			node.userContext = userContext_;
			if (m_pCombo != NULL)
			{
				std::wstring name = m_pCombo->GetText().GetData();
				if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY))
				{
					node.groupRole = L"manager";
				}
				else if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY))
				{
					node.groupRole = L"member";
				}
				else if (name == iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY))
				{
					node.groupRole = L"member";
				}
			}
			if (m_pAddManage != NULL)
			{
				m_pAddManage->SetText(iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_GROUP_KEY).c_str());
			}
			AddTileLayoutItem(node);
		}
		else
		{
			for (size_t i=0;i<shareUserInfos.size();i++)
			{
				AddUserItem(shareUserInfos[i]);
			}			
			m_listUser->SetVisible(true);
		}
		return shareUserInfos.size();
	}

	int GroupManageDialog::SetGroupList(bool fromCache)
	{
		if (NULL == m_pAddManage || NULL == userContext_) return 0;
		std::wstring SearchKey = m_pAddManage->GetText().GetData();
		m_lastText = SearchKey;
		GroupNodeList groupNodes;
		PageParam pageParam;
		OrderParam orderParam;
		pageParam.orderList.push_back(orderParam);
		int64_t count = 0;

		userContext_->getShareResMgr()->listGroups(SD::Utility::String::wstring_to_utf8(SearchKey), "private", pageParam, count, groupNodes);

		if (groupNodes.size() == 0)
		{			
			return 0;
		}

		for (size_t i=0;i<groupNodes.size();i++)
		{
			AddGroupItem(groupNodes[i]);
		}
		m_listUser->SetVisible(true);
		return groupNodes.size();
	}

	void GroupManageDialog::loadlistuser()
	{
		UserGroupNodeInfoArray nodes;
		GetNodes(nodes);
		if (nodes.size() == 0)  return;
		m_Users = nodes;
		if (m_groupData.groupInfo_.groupRole() == "admin" || m_groupData.groupInfo_.groupRole() == "manager")
		{			
			if (m_list == NULL) return;
			m_list->RemoveAll();			
			CLabelUI* number = static_cast<CLabelUI*>(m_Paintm.FindControl(L"GroupManage_count"));
			if (number == NULL) return;
			std::wstring text = iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_START_KEY);
			text += SD::Utility::String::type_to_string<std::wstring,int32_t>(nodes.size());
			text += iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_END_KEY);
			number->SetText(text.c_str());

			for (size_t i=0; i < nodes.size();i++)
			{
				AddItem(nodes[i]);
			}
		}		
		else if (m_groupData.groupInfo_.groupRole() == "member" || m_groupData.groupInfo_.groupRole() == "member")
		{
			if (m_listViewer == NULL) return;
			m_listViewer->RemoveAll();			
			// number
			CLabelUI* number = static_cast<CLabelUI*>(m_Paintm.FindControl(L"GroupViewer_count"));
			if (number == NULL) return;
			std::wstring text = iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_START_KEY);
			text += SD::Utility::String::type_to_string<std::wstring,int32_t>(nodes.size());
			text += iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_END_KEY);
			number->SetText(text.c_str());

			for (size_t i=0; i < nodes.size();i++)
			{
				AddItemViewer(nodes[i]);
			}
		}
		
	}

	void GroupManageDialog::Init()
	{  
		m_pCloseBtn			= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseManage));
		m_pCloseOtherBtn	= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseOther));
		m_pOkBtn			= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnOkManage));
		m_pCancelBtn		= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCancelManage));
		m_pAddManage		= static_cast<CEditUI*>(m_Paintm.FindControl(vRichEditManage));
		m_pChange			= static_cast<CVerticalLayoutUI*>(m_Paintm.FindControl(vVerticalLayoutManage));
		m_pCombo			= static_cast<CComboUI*>(m_Paintm.FindControl(vComboManage));
		m_list				= static_cast<CListUI*>(m_Paintm.FindControl(vListManage));
		m_listUser			= static_cast<CListUI*>(m_Paintm.FindControl(vListUserManage));
		m_tileLayoutList	= static_cast<CTileLayoutListUI*>(m_Paintm.FindControl(vTileLayoutListManage));
		m_listViewer		= static_cast<CListUI*>(m_Paintm.FindControl(L"GroupViewer_listView"));
		m_pCloseBtnViewer	= static_cast<CButtonUI*>(m_Paintm.FindControl(L"GroupViewer_close_btn"));
		m_pCancelBtnViewer	= static_cast<CButtonUI*>(m_Paintm.FindControl(L"GroupViewer_close"));

		if (m_pCombo != NULL)
		{
			CListLabelElementUI* pElement = new CListLabelElementUI;
			pElement->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY).c_str());
			pElement->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_POWER_AUTHER_KEY).c_str());
			if (m_groupData.groupInfo_.groupRole() == "admin")
			{
				m_pCombo->Add(pElement);
			}

			CListLabelElementUI* pElement1 = new CListLabelElementUI;
			pElement1->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY).c_str());
			pElement1->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_POWER_EDITOR_KEY).c_str());
			m_pCombo->Add(pElement1);

			CListLabelElementUI* pElement2 = new CListLabelElementUI;
			pElement2->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY).c_str());
			pElement2->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_POWER_VIEWER_KEY).c_str());
			m_pCombo->Add(pElement2);
		}
		loadlistuser();
	}

	void GroupManageDialog::GetNodes(UserGroupNodeInfoArray& nodes)
	{
		if (userContext_ == NULL) return;
		PageParam page;
		int64_t total = 0;
		userContext_->getGroupMgr()->GetGroupListMemberInfo(m_groupData.groupId(),"","all",page,total,nodes);
	}

	void GroupManageDialog::AddItem(UserGroupNodeInfo& node)
	{
		if (NULL == m_list) return;

		CDialogBuilder builder;
		GroupManageListContainerElement* item = static_cast<GroupManageListContainerElement*>(
			builder.Create(vFileGroupManageListItem, 
			L"", 
			this, 
			&m_Paintm, 
			NULL));
		if (item == NULL) return;
		//....
		item->nodeData.basic = node;
		item->nodeData.userContext = userContext_;

		// icon
		CLabelUI* icon = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_icon"));
		if (icon == NULL) return;

		// name
		CLabelUI* name = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_name"));
		if (name == NULL) return;
		name->SetText(SD::Utility::String::utf8_to_wstring(node.groupInfo_.username()).c_str());
		name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.groupInfo_.username()).c_str());

		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_dept"));
		if (dept == NULL) return;
		dept->SetText(SD::Utility::String::utf8_to_wstring(node.member_.description()).c_str());
		dept->SetToolTip(SD::Utility::String::utf8_to_wstring(node.member_.description()).c_str());

		// Combo
		CComboUI* combo = static_cast<CComboUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_Combo"));
		if (combo == NULL) return;
		if (node.groupInfo_.groupRole() == "admin")
		{
			CListLabelElementUI* pElement = new CListLabelElementUI;
			pElement->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY).c_str());
			combo->Add(pElement);
			combo->SelectItem(0);
			combo->SetEnabled(false);
			CButtonUI* bDelete = static_cast<CButtonUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_delete"));
			if (bDelete == NULL) return;
			bDelete->SetEnabled(false);
		}
		else
		{
			CListLabelElementUI* pElement = new CListLabelElementUI;
			pElement->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY).c_str());
			combo->Add(pElement);
			CListLabelElementUI* pElement1 = new CListLabelElementUI;
			pElement1->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY).c_str());
			combo->Add(pElement1);
			CListLabelElementUI* pElement2 = new CListLabelElementUI;
			pElement2->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY).c_str());
			combo->Add(pElement2);
			if (node.groupInfo_.groupRole() == "member")
			{
				combo->SelectItem(2);
			}
			else if (node.groupInfo_.groupRole() == "member")
			{
				combo->SelectItem(1);
			}
			else if (node.groupInfo_.groupRole() == "manager")
			{
				combo->SelectItem(0);
			}	

			if (m_groupData.groupInfo_.groupRole() == "manager")
			{
				int type = combo->GetCurSel();
				switch (type)
				{
				case 0:
					{
						combo->SetEnabled(false);
						CButtonUI* bDelete = static_cast<CButtonUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_item_delete"));
						if (bDelete != NULL)
						{
							bDelete->SetEnabled(false);
						}	
					}
					break;
				case 1:
				case 2:
					{
						combo->RemoveAt(0);
					}
					break;
				default:
					break;
				}
			}
		}
		
		m_list->Add(item);
	}

	void GroupManageDialog::AddItemViewer(UserGroupNodeInfo& node)
	{
		if (NULL == m_listViewer) return;

		CDialogBuilder builder;
		GroupManageListContainerElement* item = static_cast<GroupManageListContainerElement*>(
			builder.Create(vFileGroupViewerListItem, 
			L"", 
			this, 
			&m_Paintm, 
			NULL));
		if (item == NULL) return;
		//....
		item->nodeData.basic = node;
		item->nodeData.userContext = userContext_;

		// name
		CLabelUI* name = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupViewer_item_name"));
		if (name == NULL) return;
		name->SetText(SD::Utility::String::utf8_to_wstring(node.groupInfo_.username()).c_str());
		name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.groupInfo_.username()).c_str());

		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupViewer_item_dept"));
		if (dept == NULL) return;
		dept->SetText(SD::Utility::String::utf8_to_wstring(node.member_.description()).c_str());
		dept->SetToolTip(SD::Utility::String::utf8_to_wstring(node.member_.description()).c_str());

		// Combo
		CLabelUI* role = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupViewer_item_role"));
		if (role == NULL) return;
		if (node.groupInfo_.groupRole() == "admin")
		{			
			role->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY).c_str());
		}
		else if (node.groupInfo_.groupRole() == "manager")
		{
			role->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY).c_str());
		}
		else if (node.groupInfo_.groupRole() == "member")
		{
			role->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY).c_str());
		}
		else if (node.groupInfo_.groupRole() == "member")
		{
			role->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY).c_str());
		}
		m_listViewer->Add(item);
	}

	void GroupManageDialog::AddUserItem(ShareUserInfo& node)
	{
		if (NULL == m_listUser) return;
		CDialogBuilder builder;
		GroupManageListUserContainerElement* item = static_cast<GroupManageListUserContainerElement*>(
			builder.Create(vFileGroupManageListUserItem, 
			L"",
			this, 
			&m_Paintm, 
			NULL));
		if (item == NULL) return;

		item->nodeData.basic = node;
		item->nodeData.userContext = userContext_;		

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"GroupManage_itemUser_user"));
		if (name == NULL) return;
		name->SetText(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
		name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.name()).c_str());

		// email
		CLabelUI* email = static_cast<CLabelUI*>(item->FindSubControl(L"GroupManage_itemUser_email"));
		if (email == NULL) return;		
		email->SetText(SD::Utility::String::utf8_to_wstring(node.email()).c_str());
		email->SetToolTip(SD::Utility::String::utf8_to_wstring(node.email()).c_str());

		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_Paintm.FindSubControlByName(item, L"GroupManage_itemUser_dept"));
		if (dept == NULL) return;
		dept->SetText(SD::Utility::String::utf8_to_wstring(node.department()).c_str());
		dept->SetToolTip(SD::Utility::String::utf8_to_wstring(node.department()).c_str());

		m_listUser->Add(item);
	}

	void GroupManageDialog::AddGroupItem(GroupNode& node)
	{
		if (NULL == m_listUser) return;
		CDialogBuilder builder;
		GroupManageListUserContainerElement* item = static_cast<GroupManageListUserContainerElement*>(
			builder.Create(vFileGroupManageListUserItem, 
			L"", 
			this, 
			&m_Paintm, 
			NULL));
		if (item == NULL) return;

		item->nodeData.groupNode.id = node.id();
		item->nodeData.groupNode.name = SD::Utility::String::utf8_to_wstring(node.name());
		item->nodeData.groupNode.description = SD::Utility::String::utf8_to_wstring(node.description());
		item->nodeData.groupNode.accountId = node.accountId();
		item->nodeData.groupNode.maxMembers = node.maxMembers();
		item->nodeData.groupNode.createdAt = node.createdAt();
		item->nodeData.groupNode.modifiedAt = node.modifiedAt();
		item->nodeData.groupNode.createdBy = node.createdBy();
		item->nodeData.groupNode.ownedBy = node.ownedBy();
		item->nodeData.groupNode.status = SD::Utility::String::utf8_to_wstring(node.status());
		item->nodeData.groupNode.appId = SD::Utility::String::utf8_to_wstring(node.appId());
		item->nodeData.groupNode.type = SD::Utility::String::utf8_to_wstring(node.type());

		item->nodeData.userContext = userContext_;		

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"GroupManage_itemUser_user"));
		if (name == NULL) return;
		name->SetText(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
		name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.name()).c_str());

		// email
		CLabelUI* email = static_cast<CLabelUI*>(item->FindSubControl(L"GroupManage_itemUser_email"));
		if (email == NULL) return;		
		email->SetText(SD::Utility::String::utf8_to_wstring(node.description()).c_str());
		email->SetToolTip(SD::Utility::String::utf8_to_wstring(node.description()).c_str());

		m_listUser->Add(item);
	}

	void GroupManageDialog::AddTileLayoutItem(UIGroupManageUserNode& node)
	{
		if (NULL == m_tileLayoutList) return;
		//ШЅжи
		for (std::vector<UserGroupNodeInfo>::iterator itor=m_Users.begin();itor!=m_Users.end();itor++)
		{
			if (node.basic.id() == (*itor).groupInfo_.id())
			{
				SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
				simlpeNoticeFrame->Show(Warning,MSG_GROUP_MANAGER_ADDUSER_SETTING_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
				return;
			}
		}
		for (int i=0;i<m_tileLayoutList->GetCount();i++)
		{
			GroupManageTileLayoutListContainerElement* item = static_cast<GroupManageTileLayoutListContainerElement*>(m_tileLayoutList->GetItemAt(i));
			if (item == NULL)  continue;
			if (node.basic.id() == item->nodeData.basic.id())
			{
				SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
				simlpeNoticeFrame->Show(Warning,MSG_GROUP_MANAGER_ADDUSER_SETTING_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
				return;
			}
		}

		CDialogBuilder builder;
		GroupManageTileLayoutListContainerElement* item = static_cast<GroupManageTileLayoutListContainerElement*>(
			builder.Create(vFileGroupManageTileLayoutListItem, 
			L"", 
			this, 
			&m_Paintm, 
			NULL));
		if (item == NULL) return;
		//....

		item->nodeData.basic = node.basic;
		item->nodeData.userContext = userContext_;
		item->nodeData.groupRole = node.groupRole;	
		item->nodeData.groupNode = node.groupNode;

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"GroupManage_TileLayoutListItem_name"));
		if (name == NULL) return;
		if (item->nodeData.basic.id() != NULL)
		{
			name->SetText(SD::Utility::String::utf8_to_wstring(node.basic.name()).c_str());
		}
		if (item->nodeData.groupNode.id != NULL)
		{
			name->SetText(node.groupNode.name.c_str());
		}

		m_tileLayoutList->Add(item);
	}

	void GroupManageDialog::OnTimer(UINT nIDEvent)
	{
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_Paintm);
		simpleNotice->RestoreNoticeArea();
		delete simpleNotice;
		simpleNotice=NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	LRESULT GroupManageDialog::OnNcBtn(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		if (m_listUser != NULL && m_listUser->GetCount() > 0)
		{
			POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
			//::ScreenToClient(GetHWND(), &pt);
			RECT rc = m_listUser->GetPos();
			if (!PtInRect(&rc,pt))
			{
				m_pAddManage->SetText(iniLanguageHelper.GetCommonString(COMMENT_ADDMEMBER_GROUP_KEY).c_str());
				m_lastText = L"";
				m_listUser->RemoveAll();
				m_listUser->SetVisible(false);
			}
		}
		return 0;
	}

	void GroupManageDialog::SetTransparent(int nOpacity)
	{
		m_Paintm.SetTransparent(255 - nOpacity);
	}

	LRESULT GroupManageDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_Paintm.Init(m_hWnd);
		CDialogBuilder builder;

		CControlUI *pRoot = NULL;
		if (m_groupData.groupInfo_.groupRole() == "admin" || m_groupData.groupInfo_.groupRole() == "manager")
		{
			pRoot = builder.Create(vGroupManageXml, (UINT)0, this,&m_Paintm);
		}
		else if (m_groupData.groupInfo_.groupRole() == "member" || m_groupData.groupInfo_.groupRole() == "member")
		{
			pRoot = builder.Create(vGroupViewerXml, (UINT)0, this,&m_Paintm);
		}
		ASSERT(pRoot && "Failed to parse XML");
		m_Paintm.AttachDialog(pRoot);
		m_Paintm.AddNotifier(this);
		Init();

		return 0;
	}

	LRESULT GroupManageDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT GroupManageDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT GroupManageDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT GroupManageDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT GroupManageDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT GroupManageDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient;
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_Paintm.GetCaptionRect();

		if (-1 == rcCaption.bottom)
		{
			rcCaption.bottom = rcClient.bottom;
		}

		if ( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) 
		{
			CControlUI* pControl = m_Paintm.FindControl(pt);
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


	LRESULT GroupManageDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}

	LRESULT GroupManageDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}


	LRESULT GroupManageDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;
		switch( uMsg ) {
		case WM_CREATE:        lRes = OnCreate(uMsg, wParam, lParam, bHandled); break;
		case WM_CLOSE:         lRes = OnClose(uMsg, wParam, lParam, bHandled); break;
		case WM_DESTROY:       lRes = OnDestroy(uMsg, wParam, lParam, bHandled); break;
		case WM_NCACTIVATE:    lRes = OnNcActivate(uMsg, wParam, lParam, bHandled); break;
		case WM_NCCALCSIZE:    lRes = OnNcCalcSize(uMsg, wParam, lParam, bHandled); break;
		case WM_NCPAINT:       lRes = OnNcPaint(uMsg, wParam, lParam, bHandled); break;
		case WM_NCHITTEST:     lRes = OnNcHitTest(uMsg, wParam, lParam, bHandled); break;
		case WM_KILLFOCUS:     lRes = OnKillFocus(uMsg, wParam, lParam, bHandled); break; 
		case WM_MOUSELEAVE:	   lRes = OnMouseLeave(uMsg, wParam, lParam, bHandled); break; 
		case WM_TIMER:
			{
				if ((UINT)UI_TIMERID::SIMPLENOTICE_TIMERID == wParam)
				{
					OnTimer(wParam);
				}
				else
				{
					bHandled = FALSE;
				}
				break;
			}
		case WM_LBUTTONDOWN:		
		case WM_NCLBUTTONDOWN:	lRes = OnNcBtn(uMsg, wParam, lParam, bHandled); break;
		default:
			bHandled = FALSE;
		}
		if( bHandled ) return lRes;
		if( m_Paintm.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
		return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
	}

}