#include "stdafxOnebox.h"
#include "ShareFrameV2.h"
#include "Utility.h"
#include "ControlNames.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "ShareUserInfo.h"
#include "ShareResMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "CustomListUI.h"
#include "ShareFrameHiddenList.h"
#include "TileLayoutListUI.h"
#include "ProxyMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "ConfigureMgr.h"
#include <boost/algorithm/string.hpp>
#include "CustomComboUI.h"
#include "GroupResMgr.h"
#include <Winuser.h >
#include "CommonLoadingFrame.h"
#include "PathMgr.h"
#include "NotifyMgr.h"
#include "ShareLinkCountDialog.h"
#include "NodeControlResMgr.h"
#include "UserListControl.h"
#include "UIScaleIconButton.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ShareFrame")
#endif


#define TIMERID_COMBOXITEMSELECT		10010

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(ShareFrameV2,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_TEXTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_RETURN,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SETFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMSELECT,executeFunc)
	DUI_END_MESSAGE_MAP()

	ShareFrameV2::ShareFrameV2(UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager)
	:userContext_(context)
	, nodeData_(nodeData)
	, paintManager_(paintManager)
	, skipChange_(false)
	, skipInitComboItem_(false)
	{
		int32_t ret = RT_OK;
		privateLinkFlag_=0;
		m_noticeFrame_ = NULL;
	    funcMaps_.insert(std::make_pair(L"shareFrame_users_setfocus", boost::bind(&ShareFrameV2::usersSetfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_textchanged", boost::bind(&ShareFrameV2::usersTextchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_killfocus", boost::bind(&ShareFrameV2::usersKillfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_return", boost::bind(&ShareFrameV2::usersReturndown, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_ShareLink_click", boost::bind(&ShareFrameV2::changed2ShareLink, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_add_click", boost::bind(&ShareFrameV2::addClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_setfocus", boost::bind(&ShareFrameV2::messageSetfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_killfocus", boost::bind(&ShareFrameV2::messageKillfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_return", boost::bind(&ShareFrameV2::messageReturnDown, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_invite_click", boost::bind(&ShareFrameV2::inviteClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_deleteall_click", boost::bind(&ShareFrameV2::deleteallClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_copyall_click", boost::bind(&ShareFrameV2::copyallClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_listItem_itemclick", boost::bind(&ShareFrameV2::hiddenListItemClick, this, _1)));		
		funcMaps_.insert(std::make_pair(L"shareFrame_compelete_click", boost::bind(&ShareFrameV2::compeleteClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_listUserItem_itemclick", boost::bind(&ShareFrameV2::itemDBClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_item_combo_itemselect", boost::bind(&ShareFrameV2::comboItemSelect, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_menu_itemselect", boost::bind(&ShareFrameV2::menuItemSelect, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_item_delete_click", boost::bind(&ShareFrameV2::deleteShareUserList, this, _1)));	
		funcMaps_.insert(std::make_pair(L"shareFrame_item_label_setfocus", boost::bind(&ShareFrameV2::itemLabelSetfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_scale_button_click", boost::bind(&ShareFrameV2::itemLabelSetfocus, this, _1)));

		ret = userContext_->getShareResMgr()->getServerConfig(m_serverSysConfig);
		if(ret != RT_OK)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "getServerConfig failed.");
		}
		linkCode_ = L"";
	}

	ShareFrameV2::~ShareFrameV2()
	{
		try
		{
			Path filePath = userContext_->getPathMgr()->makePath();
			filePath.id(nodeData_.basic.id);
			filePath.type((nodeData_.basic.type==0)?FILE_TYPE_DIR:FILE_TYPE_FILE);
			ProxyMgr::getInstance(userContext_)->flushFileInfo(userContext_, filePath);
		}
		catch(...)
		{
		}
		roleName_.clear();
		m_sysRoleInfoExs.clear();
		if(m_noticeFrame_)
		{
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;
		}
	}

	void ShareFrameV2::init()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "ShareFrameV2::init");
		m_editSharedUsers = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_SHARED_USERS));
		m_btnSharedClose = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_SHARED_CLOSE));
		sharedUserText = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"shareFrame_text"));
		sharedUserList = static_cast<CListUI*>(m_PaintManager.FindControl(SHAREFRAME_SHAREUSERSLIST));
		copyallBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_COPYALL));
		deleteallBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_DELETEALL));
		m_shareFrame_user_layout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(L"shareFrame_user_Layout"));
		m_sHareFrame_menulayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"shareFrame_menulayout"));
		m_shareFrame_messagelayout = static_cast<CHorizontalLayoutUI*>(m_PaintManager.FindControl(L"shareFrame_messagelayout"));
		inviteBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_INVITE));
		cancelBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_CANCEL));
		closeBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_CLOSE));
		m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(SHAREFRAME_LISTUSERVIEW));   
		shareMenu = static_cast<CComboUI*>(m_PaintManager.FindControl(SHAREFRAME_MENU));
		m_editSharedMessage = static_cast<CRichEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_SHARED_MESSAGE));
		titleName = static_cast<CEditUI*>(m_PaintManager.FindControl(L"shareFrame_title"));
		
		if(m_editSharedUsers == NULL || m_btnSharedClose == NULL || sharedUserText == NULL ||sharedUserList == NULL || copyallBTN == NULL||
			deleteallBTN == NULL || m_shareFrame_user_layout == NULL || m_sHareFrame_menulayout == NULL ||m_shareFrame_messagelayout == NULL || inviteBTN  == NULL||
			cancelBTN == NULL || closeBTN == NULL || m_listUser == NULL ||shareMenu == NULL || m_editSharedMessage  == NULL ||titleName == NULL )
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "init ControlUI failed.");
			return;
		}
		m_editSharedUsers->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str());

		std::wstring defaultText = titleName->GetText();
		std::wstring tmpFileName =  defaultText + L"\"" + nodeData_.basic.name + L"\"";
		titleName->SetText(tmpFileName.c_str());	
		m_noticeFrame_ = new NoticeFrameMgr(m_hWnd);
		//get the system role
		userContext_->getNodeControlMgr()->listSystemRole(m_sysRoleInfoExs);
		if (m_sysRoleInfoExs.size()==0)
		{
			std::list<std::string> strName;
			strName.push_back("viewer");
			SysRoleInfoEx roleName;
			for (std::list<std::string>::iterator it=strName.begin();it!=strName.end();++it)
			{
				roleName.name=it->c_str();
				roleName.description=it->c_str();
				m_sysRoleInfoExs.push_back(roleName);
			}
			
		}
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_EDITOR), iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY)));
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_PREVIEWER), iniLanguageHelper.GetCommonString(TEAMSPACE_PREVIEWER_KEY)));
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_UPLOADERANDVIEWER), iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADERANDVIEWER_KEY)));
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_UPLOADER), iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADER_KEY)));
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_VIEWER), iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY)));
		roleMaps_.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(SYSROLE_LISTER), iniLanguageHelper.GetCommonString(TEAMSPACE_LISTER_KEY)));

		if(nodeData_.basic.type)
		{
			//teamSpace_viewer=查看者
			//teamSpace_previewer=预览者
			for(SysRoleInfoExList::iterator it = m_sysRoleInfoExs.begin(); it != m_sysRoleInfoExs.end(); ++it)
			{
				if(it->name == SYSROLE_VIEWER || it->name == SYSROLE_PREVIEWER || it->name == SYSROLE_LISTER)
				{
					std::string tmpStr= it->name;
					roleName_.push_back(tmpStr);
				}
			}
			if (roleName_.size()==0)
			{
				roleName_.push_back(SYSROLE_VIEWER);
				roleName_.push_back(SYSROLE_PREVIEWER);
				roleName_.push_back(SYSROLE_LISTER);
				m_sysRoleInfoExs.clear();
				SysRoleInfoEx roleName;
				for (std::vector<std::string>::iterator it=roleName_.begin();it!=roleName_.end();++it)
				{
					roleName.name=it->c_str();
					roleName.description=it->c_str();
					m_sysRoleInfoExs.push_back(roleName);
				}
			}
		}
		else
		{
			for(SysRoleInfoExList::iterator it = m_sysRoleInfoExs.begin(); it != m_sysRoleInfoExs.end(); ++it)
			{
				std::string tmpStr= it->name;
				roleName_.push_back(tmpStr);
			}
		}
		loadSharedUsersList();
		showButton();

	}


	void ShareFrameV2::changeShareNum(int32_t nCount)
	{
		ShareNodeList shareNodes;
		std::wstring keyWord = sharedUserText->GetText();
		int pos=keyWord.find(L'(');
		keyWord=keyWord.substr(0,pos);
		keyWord+=L"(";
		char buf[26] = {0};
		sprintf_s(buf,"%d", nCount);
		std::string tmp = buf;
		keyWord+= SD::Utility::String::string_to_wstring(tmp);
		keyWord+= L")";
		sharedUserText->SetText(keyWord.c_str());
		if (sharedUserList->GetCount()==0)
		{
			deleteallBTN->SetVisible(false);
			copyallBTN->SetVisible(false);
		}else
		{
			copyallBTN->SetVisible();
			deleteallBTN->SetVisible();
		}

	}


	void ShareFrameV2::loadSharedUsersList()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "ShareFrameV2::loadSharedUsersList");
		int32_t ret = RT_OK;
		ShareNodeList shareNodes;
		m_shareFrame_user_layout->SetVisible();
		//init my files list and get the selected item of the list
		ret = userContext_->getShareResMgr()->listShareMember(nodeData_.basic.id, shareNodes);
		if(ret != RT_OK)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "listShareMember failed.");
		}
		//init the shared users list		
		sharedUserList->RemoveAll();

		for(ShareNodeList::iterator it = shareNodes.begin(); it != shareNodes.end(); it++)
		{
			CDialogBuilder builder;
			ShareFrameListContainerElement* node = static_cast<ShareFrameListContainerElement*>(
				builder.Create(ControlNames::SKIN_XML_SHAREEX_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
			if (NULL == node)
			{
				continue;
			}
			node->nodeData.shareNode.id(it->id());
			node->nodeData.shareNode.type(it->type());
			node->nodeData.shareNode.ownerId(it->ownerId());
			node->nodeData.shareNode.ownerName(it->ownerName());
			node->nodeData.shareNode.sharedUserId(it->sharedUserId());
			node->nodeData.shareNode.sharedUserType(it->sharedUserType());
			node->nodeData.shareNode.sharedUserName(it->sharedUserName());
			node->nodeData.shareNode.sharedUserLoginName(it->sharedUserLoginName());
			node->nodeData.shareNode.sharedUserDescription(it->sharedUserDescription());
			node->nodeData.shareNode.inodeId(it->inodeId());
			node->nodeData.shareNode.name(it->name());
			node->nodeData.shareNode.modifiedAt(it->modifiedAt());
			node->nodeData.shareNode.modifiedBy(it->modifiedBy());
			node->nodeData.shareNode.roleName(it->roleName());
			node->nodeData.shareNode.status(it->status());
			node->nodeData.shareNode.size(it->size());

			// initial UI
			CLabelUI* icon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_ITEMICON));
			if (NULL != icon)
			{
				std::wstring str_photoPath;
				if(node->nodeData.shareNode.sharedUserType() == "user")
				{
					str_photoPath = GetInstallPath() + L"skin\\Image\\" + L"ic_popup_share_person.png";
				}
				else if(node->nodeData.shareNode.sharedUserType() == "group")
				{
					str_photoPath += GetInstallPath() + L"skin\\IMage\\" + L"ic_popup_share_team.png";
				}
				icon->SetBkImage(str_photoPath.c_str());
			}

			CLabelUI* name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_USERNAME));
			if(NULL != name)
			{
				std::wstring tmpStr = SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.sharedUserName());
				name->SetToolTip(tmpStr.c_str());
				name->SetText(tmpStr.c_str());
			}

			CScaleIconButtonUI* pButton = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_SCALE_BUTTON));
			if(NULL==pButton)
			{
				return;
			}
			
			CCustomComboUI* combo = static_cast<CCustomComboUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_ITEMCOMBO));
			if(NULL==combo) return;
			int32_t viewCount = 0;
			initComboList(combo, m_sysRoleInfoExs, viewCount);

			std::map<std::wstring, std::wstring>::iterator iter = roleMaps_.find(SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.roleName()));
			if(iter == roleMaps_.end())
			{
				return;
			}
			pButton->SetText(iter->second.c_str());
			pButton->SetToolTip(iter->second.c_str());

			

			if(!sharedUserList->Add(node))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
			}
		}
		changeShareNum(shareNodes.size());
    }

	LPCTSTR ShareFrameV2::GetWindowClassName(void) const
	{
		return ControlNames::WND_SHARE_CLS_NAME;
	}

	DuiLib::CDuiString ShareFrameV2::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	DuiLib::CDuiString ShareFrameV2::GetSkinFile()
	{
		return ControlNames::SKIN_XML_SHAREEX_FILE;
	}

	CControlUI* ShareFrameV2::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool ShareFrameV2::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT ShareFrameV2::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandle = TRUE;

		switch (uMsg)
		{
		case WM_CREATE:
			OnCreate(uMsg,wParam,lParam,bHandle);
			break;
		case WM_NCHITTEST: 
			lRes = OnNcHitTest(uMsg,wParam,lParam,bHandle); 
			break;
		case WM_SYSCOMMAND:
			OnSysCommand(uMsg,wParam,lParam,bHandle);
			break;
		case WM_TIMER:
			if (TIMERID_COMBOXITEMSELECT == wParam)
			{
				KillTimer(*this, TIMERID_COMBOXITEMSELECT);
				CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),m_hWnd, boost::bind(
					&ShareFrameV2::timerFunc, 
					this, timerMsg));
				break;
			}
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

	LRESULT ShareFrameV2::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		WindowImplBase::OnCreate(uMsg, wParam, lParam, bHandled);
		init();
		return 0;
	}

	LRESULT ShareFrameV2::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if (wParam == SC_CLOSE)
		{
			Close(0);
			return 0;
		}
		else if(wParam == 0xF032)//forbidden double click zoom out
		{
			return 0;
		}
		return WindowImplBase::OnSysCommand(uMsg, wParam, lParam, bHandled);
	}

	void ShareFrameV2::Notify(DuiLib::TNotifyUI& msg)
	{
		return WindowImplBase::Notify(msg);
	}

	LRESULT ShareFrameV2::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void ShareFrameV2::OnClick(DuiLib::TNotifyUI& msg)
	{
		CDuiString uiName = msg.pSender->GetName();	
		std::wstring name = msg.pSender->GetName().GetData();
		if (uiName == ControlNames::BTN_CLOSE || uiName == ControlNames::BTN_SHARED_CLOSE)//CANCEL)
		{
			Close();
			return;
		}
		else if(uiName==ControlNames::BTN_SHARED_CANCEL)
		{
			RECT rc;
			::GetWindowRect(GetHWND(),&rc);	
			int height = rc.bottom - rc.top;

			if (height>=SHARE_FRAME_MAX_SIZE)
				height-=SHARE_FRAME_CHANGE_DISTANCE;
			m_listUser->RemoveAll();
			m_listUser->SetVisible(false);
		
			m_sHareFrame_menulayout->SetVisible(false);
			m_shareFrame_messagelayout->SetVisible(false);
			inviteBTN->SetVisible(false);
			cancelBTN->SetVisible(false);
			closeBTN->SetVisible();
			if (sharedUserList->GetCount()==0)
			{
				deleteallBTN->SetVisible(false);
				copyallBTN->SetVisible(false);
			}
			else
			{
				copyallBTN->SetVisible();
				deleteallBTN->SetVisible();
			}
			if (!m_addNodes.empty())
			{
				m_editSharedUsers->SetText(L"");
			}

			int width = rc.right - rc.left;
			rc.bottom -= SHARE_FRAME_CHANGE_DISTANCE;
			::MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
			::InvalidateRect(*this,&rc,true);	
		}

		if (name.substr(0,11) == L"userDelete_")
		{
			int32_t delCount = SD::Utility::String::string_to_type<int32_t>(name.substr(11,name.length()-11));
			int32_t count = 0;
			for (std::list<ShareNode>::iterator it = m_addNodes.begin(); it != m_addNodes.end(); ++it)
			{
				count++;
				if (count == delCount)
				{
					bool isGroup=1;
					for ( ShareUserInfoList::iterator iter= m_inviteUsersList.begin(); iter != m_inviteUsersList.end(); ++iter)
					{
						if (it->sharedUserName()==iter->name())
						{
							m_inviteUsersList.erase(iter);
							isGroup=0;
							break;
						}
					}
					if (isGroup)
					{
						for ( UIGroupNodeList::iterator iter= m_inviteGroupList.begin(); iter != m_inviteGroupList.end(); ++iter)
						{
							if (SD::Utility::String::utf8_to_wstring(it->sharedUserName())==iter->name)
							{
								m_inviteGroupList.erase(iter);
								break;
							}
						}
					}

					m_addNodes.erase(it);
					break;
				}
			}		
			ShareUserInfoList users;
			UIGroupNodeList groups;
			addSharedTileUserList(users,groups,true);

			m_editSharedUsers->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str());
			m_editSharedUsers->SetTextColor(0x99999999);
		}
		executeFunc(msg);
	}

	void ShareFrameV2::executeFunc(TNotifyUI& msg)
	{

		CDuiString tempName = msg.pSender->GetName();
		std::wstring funcName = tempName + L"_" + msg.sType;

		std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
		if(it!=funcMaps_.end())
		{
			it->second(msg);
		}
	}

	void ShareFrameV2::shareSelectchanged()
	{
		//showButton();
		if(skipInitComboItem_ == FALSE)
		{
			int32_t count = 0;
			int32_t viewCount = 0;
			std::wstring tmpRoleName = L"";
		
			
			initComboList(shareMenu, m_sysRoleInfoExs, viewCount);
			shareMenu->SelectItem(viewCount);
			std::wstring tmpRole = shareMenu->GetText();
			shareMenu->SetToolTip(tmpRole.c_str());
			skipInitComboItem_ = TRUE;
			
		}
		
		EmailInfoNode emailInfoNode;
		userContext_->getShareResMgr()->getMailInfo(nodeData_.basic.id, "share", emailInfoNode);
		if(!emailInfoNode.message.empty())
		{
			m_editSharedMessage->SetText(SD::Utility::String::utf8_to_wstring(emailInfoNode.message).c_str());
		}
		else
		{
			m_editSharedMessage->SetText(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY).c_str());
			m_editSharedMessage->SetTextColor(0x99999999);
			
		}

		return;
	}

	void ShareFrameV2::changed2ShareLink(TNotifyUI& msg)
	{
		ShowWindow(false);
		ShareLinkCountDialog::CreateDlg(paintManager_, userContext_, nodeData_);
		Close();
	}

	void ShareFrameV2::usersSetfocus(TNotifyUI& msg)
	{
		m_editSharedUsers->SetEnabled();
	
		if (m_editSharedUsers->GetText().IsEmpty() || (0 == _tcsicmp(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str(), m_editSharedUsers->GetText().GetData())))
		{
			m_editSharedUsers->SetText(L"");
		}

		RECT rc;
		::GetWindowRect(GetHWND(),&rc);	
		int height = rc.bottom - rc.top;

		if (height<SHARE_FRAME_MAX_SIZE)
			height+=SHARE_FRAME_CHANGE_DISTANCE;
		m_sHareFrame_menulayout->SetVisible();	
		if (!inviteBTN->IsVisible())
		{
			shareMenu->SelectItem(0);
			shareMenu->SetVisible();
		}

		m_shareFrame_messagelayout->SetVisible();
		inviteBTN->SetVisible();

		if (m_addNodes.size() == 0)
		{
			inviteBTN->SetEnabled(false);
		}

		cancelBTN->SetVisible();
		closeBTN->SetVisible(false);
		m_editSharedMessage->SetEnabled();
		copyallBTN->SetVisible(false);
		deleteallBTN->SetVisible(false);
		int width = rc.right - rc.left;
		rc.bottom += SHARE_FRAME_CHANGE_DISTANCE;
		::MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
		::InvalidateRect(*this,&rc,true);
	}

	void ShareFrameV2::usersTextchanged(TNotifyUI& msg)
	{
		if(skipChange_)
		{
			skipChange_ = false;
			return;
		}
		std::wstring keyWord = msg.pSender->GetText();		
		m_listUser->RemoveAll();
		if (keyWord==L""||std::wstring::npos != keyWord.find(L",") || std::wstring::npos != keyWord.find(L";"))
		{
			m_listUser->SetVisible(false);
			return;
		}
		listDomainUsers(keyWord, ShareType_Share, true);
	}

	void ShareFrameV2::usersKillfocus(TNotifyUI& msg)
	{
		int32_t horHeight = m_shareFrame_user_layout->GetFixedHeight();
		horHeight=horHeight-31;
		int64_t tmpX = msg.ptMouse.x ;
		int64_t tmpY = msg.ptMouse.y ;

		if(m_editSharedUsers->GetText().IsEmpty())
		{
			m_editSharedUsers->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str());
			m_editSharedUsers->SetTextColor(0x99999999);
		}

		if((tmpX<20 || tmpX>584) || (tmpY<(295+horHeight) || tmpY>(413+horHeight)))
		{				
			m_listUser->SetVisible(false);
		}
// 		RECT rt = m_listUser->GetPos();
// 		if (!PtInRect(&rt,msg.ptMouse))
// 		{
// 			m_listUser->SetVisible(false);
// 		}
	}

	void ShareFrameV2::findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey, int curlSel)
	{
		if((size_t)curlSel > roleName_.size())
		{
			return;
		}

		vecRemainUser.clear();

		std::vector<std::wstring> vecRturn = getBatchUsers(searchKey);
		
		for (std::vector<std::wstring>::const_iterator itr = vecRturn.begin(); itr != vecRturn.end(); ++itr)
		{
			GroupNodeList groupNodes;
			PageParam pageParam;
			OrderParam orderParam;
			pageParam.orderList.push_back(orderParam);
			int64_t count = 0;
			
			// find group
			userContext_->getShareResMgr()->listGroups(SD::Utility::String::wstring_to_utf8(*itr), "private", pageParam, count, groupNodes);
			if (groupNodes.size() > 1)
			{
				vecRemainUser.push_back(*itr);
				continue;
			}

			// find user
			ShareUserInfoList shareUserInfos;
			ProxyMgr::getInstance(userContext_)->listDomainUsers(SD::Utility::String::wstring_to_utf8(*itr), shareUserInfos);
			if (shareUserInfos.size() > 1)
			{
				vecRemainUser.push_back(*itr);
				continue;
			}

			if ((groupNodes.size() + shareUserInfos.size()) != 1)
			{
				vecRemainUser.push_back(*itr);
				continue;
			}

			if (groupNodes.size() != 0)
			{
				bool flag=0;
				if (!groups.empty())
				{
					for(GroupNodeList::iterator iter=groups.begin();iter!=groups.end();++iter)
					{
						if (iter->name()==groupNodes[0].name())
						{
							flag=1;
							break;
						}
					}
				}
				if (!flag)   groups.push_back(groupNodes[0]);
			}
			if (shareUserInfos.size() != 0)
			{
				// filter user self
				if(userContext_->getUserInfoMgr()->getUserName() == SD::Utility::String::utf8_to_wstring(shareUserInfos[0].loginName()))
				{
					vecRemainUser.push_back(*itr);
					continue;
				}

				shareUserInfos[0].roleName(roleName_[curlSel]);
				
				users.push_back(shareUserInfos[0]);
			}
		}
	}

	void ShareFrameV2::findGroupsAndUser(const std::wstring& searchKey)
	{
		listGroups(searchKey, ShareType_Share);
		listDomainUsers(searchKey, ShareType_Share);
	}


	void ShareFrameV2::usersReturndown(TNotifyUI& msg)
	{
		std::wstring searchKey = m_editSharedUsers->GetText();
		if(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY) == searchKey || L"" == searchKey )
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_NOTSETVALIDUSER_KEY);
			return;
		}
		while (SD::Utility::String::rtrim(searchKey,L" ").length() != searchKey.length())
		{
			searchKey = SD::Utility::String::rtrim(searchKey,L" ");
		}
		if (std::wstring::npos != searchKey.find(L",") || std::wstring::npos != searchKey.find(L";")
			|| std::wstring::npos != searchKey.find(L"，") || std::wstring::npos != searchKey.find(L"；"))
		{
			std::vector<std::wstring> vecRemainUser;
			ShareUserInfoList users;
			GroupNodeList groups;

			CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),m_hWnd, boost::bind(
				&ShareFrameV2::findGroupsAndUser, 
				this, 
				boost::ref(vecRemainUser), 
				boost::ref(users), 
				boost::ref(groups), 
				boost::cref(searchKey), 
				shareMenu->GetCurSel()));

			UIGroupNodeList uiGroupList;
			for (GroupNodeList::iterator it = groups.begin(); it != groups.end(); ++it)
			{
				ShareUserInfo shareUserInfo;
				shareUserInfo.id(it->id());
				shareUserInfo.name(it->name());
				shareUserInfo.loginName();
				shareUserInfo.department(it->description());
				shareUserInfo.roleName(roleName_[shareMenu->GetCurSel()]);

				UIGroupNode uiGroupNode;
				uiGroupNode.id = it->id();
				uiGroupNode.name = SD::Utility::String::utf8_to_wstring(it->name());
				uiGroupNode.description = SD::Utility::String::utf8_to_wstring(it->description());
				uiGroupNode.accountId = it->accountId();
				uiGroupNode.maxMembers = it->maxMembers();
				uiGroupNode.createdAt = it->createdAt();
				uiGroupNode.modifiedAt = it->modifiedAt();
				uiGroupNode.createdBy = it->createdBy();
				uiGroupNode.ownedBy = it->ownedBy();
				uiGroupNode.status = SD::Utility::String::utf8_to_wstring(it->status());
				uiGroupNode.appId = SD::Utility::String::utf8_to_wstring(it->appId());
				uiGroupNode.type = SD::Utility::String::utf8_to_wstring(it->type());
				uiGroupList.push_back(uiGroupNode);					
			}
			(void)addSharedTileUserList(users,uiGroupList);
			m_listUser->RemoveAll();
			m_listUser->SetVisible(false);
			std::wstring remainderUser = L"";
			if (vecRemainUser.size() != 0)
			{
				std::vector<std::wstring>::iterator itr = vecRemainUser.begin();
				while (itr != vecRemainUser.end())
				{
					remainderUser += *itr;
					if(vecRemainUser[vecRemainUser.size()-1] != *itr)
					{
						 remainderUser+= L";";
					}
					itr++;
				}
				m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_BATCH_ADD_KEY);
			}
			m_editSharedUsers->SetText(remainderUser.c_str());
			return;
		}
		else
		{
			m_listUser->RemoveAll();
			//listGroups(searchKey, ShareType_Share);
			//listDomainUsers(searchKey, ShareType_Share);
			CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),m_hWnd, boost::bind(
				&ShareFrameV2::findGroupsAndUser, 
				this, 
				boost::cref(searchKey)));


			if(m_listUser->GetCount() == 0)
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_NOTFOUND_KEY);
				m_listUser->SetVisible(false);
				return;
			}
			else if(m_listUser->GetCount() == 1)
			{
				ShareFrameListContainerElement* pFileListItem = static_cast<ShareFrameListContainerElement*>(m_listUser->GetItemAt(0));
				if(pFileListItem == NULL)
				{
					return;
				}
				msg.pSender = pFileListItem;
				itemDBClick(msg);
				return;
			}
			else
			{
				setUserListVisible();
			}
		}
	}

	void ShareFrameV2::listGroups(const std::wstring& SearchKey, int32_t type, bool fromCache)
	{
		GroupNodeList groupNodes;
		PageParam pageParam;
		OrderParam orderParam;
		pageParam.orderList.push_back(orderParam);
		int64_t count = 0;
		userContext_->getShareResMgr()->listGroups(SD::Utility::String::wstring_to_utf8(SearchKey), "private", pageParam, count, groupNodes);

		for (size_t i=0;i<groupNodes.size();i++)
		{
			AddGroupItem(groupNodes[i], type);
		}
		setUserListVisible();
	}

	void ShareFrameV2::listDomainUsers(const std::wstring& SearchKey, int32_t type, bool fromCache)
	{
		ShareUserInfoList shareUserInfos;
		
		//list domain users
		ProxyMgr::getInstance(userContext_)->listDomainUsers(SD::Utility::String::wstring_to_utf8(SearchKey), shareUserInfos, fromCache);
		if(shareUserInfos.size()>0)
		{
			setUserListVisible();
		}
		for (size_t i=0;i<shareUserInfos.size();i++)
		{
			AddUserItem(shareUserInfos[i], type);
		}
		return;
	}

	void ShareFrameV2::AddGroupItem(GroupNode& node, int32_t type)
	{
		CDialogBuilder builder;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(
			builder.Create(_T("shareFrameTeamListUserItem.xml"), 
			L"", 
			DialogBuilderCallbackImpl::getInstance(), 
			&m_PaintManager, 
			NULL));
		if (item == NULL) return;

		item->nodeData.groupNode.id = node.id();
		item->nodeData.groupNode.name = SD::Utility::String::utf8_to_wstring(node.name());
		item->nodeData.groupNode.description =SD::Utility::String::utf8_to_wstring(node.description());
		item->nodeData.groupNode.accountId = node.accountId();
		item->nodeData.groupNode.maxMembers = node.maxMembers();
		item->nodeData.groupNode.createdAt = node.createdAt();
		item->nodeData.groupNode.modifiedAt = node.modifiedAt();
		item->nodeData.groupNode.createdBy = node.createdBy();
		item->nodeData.groupNode.ownedBy = node.ownedBy();
		item->nodeData.groupNode.status = SD::Utility::String::utf8_to_wstring(node.status());
		item->nodeData.groupNode.appId = SD::Utility::String::utf8_to_wstring(node.appId());
		item->nodeData.groupNode.type = SD::Utility::String::utf8_to_wstring(node.type());	

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"shareFrame_Team_user"));
		if (name != NULL)
		{
			std::wstring nametext=L"{b}{f 13}"+SD::Utility::String::utf8_to_wstring(node.name())+L"{/f}{/b}";
			nametext+=L"{f 12}("+SD::Utility::String::utf8_to_wstring(node.name())+L")"+L"{/f}";
			name->SetText(nametext.c_str());
			name->SetShowHtml();
		}
		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"shareFrame_Team_itemUser_dept"));
		if (dept != NULL)
		{
			if(!node.description().empty())
			{
				dept->SetText(SD::Utility::String::utf8_to_wstring(node.description()).c_str());
				dept->SetToolTip(SD::Utility::String::utf8_to_wstring(node.description()).c_str());
			}
			else
			{
				dept->SetText(L"-");
				dept->SetToolTip(L"-");
			}
		}
		m_listUser->Add(item);
	}

	void ShareFrameV2::AddUserItem(ShareUserInfo& node, int32_t type)
	{
		CDialogBuilder builder;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(
			builder.Create(_T("shareFrameTeamListUserItem.xml"), 
			L"", 
			DialogBuilderCallbackImpl::getInstance(), 
			&m_PaintManager, 
			NULL));
		if (item == NULL) return;

		item->nodeData.shareUserInfo = node;	

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"shareFrame_Team_user"));
		if (name != NULL)
		{			 
			std::wstring nametext=L"{b}{f 13}"+SD::Utility::String::utf8_to_wstring(node.name())+L"{/f}{/b}";
            nametext+=L"{f 12}("+SD::Utility::String::utf8_to_wstring(node.loginName())+L")"+L"{/f}";
			name->SetText(nametext.c_str());
			name->SetShowHtml();
		
		}		
		
		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"shareFrame_Team_itemUser_dept"));
		if (dept != NULL)
		{
			std::wstring depttext = SD::Utility::String::utf8_to_wstring(node.department());
			std::vector<std::wstring> vecInfo;
			SD::Utility::String::split(depttext, vecInfo, L",");
			if(vecInfo.size()>=3)
			{
				std::wstring::size_type pos = vecInfo[0].length() + vecInfo[1].length() + 2;
				if(pos < depttext.size())
				{
					depttext = depttext.substr(pos);
				}
			}

			dept->SetText(depttext.c_str());
			dept->SetToolTip(depttext.c_str());
		}
		m_listUser->Add(item);
	}

	void ShareFrameV2::addClick(TNotifyUI& msg)
	{
		usersReturndown(msg);
		return;
	}

	void ShareFrameV2::menuClick(TNotifyUI& msg)
	{
		return;
	}

	void ShareFrameV2::messageSetfocus(TNotifyUI& msg)
	{		
			std::wstring keyWord = m_editSharedMessage->GetText();
			if(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY) == keyWord)
			{
				m_editSharedMessage->SetText(L"");
				m_editSharedMessage->SetTextColor(0x0);
				m_editSharedMessage->SetAttribute(L"maxchar", L"2000");
			}
	}

	void ShareFrameV2::messageKillfocus(TNotifyUI& msg)
	{		
			if(m_editSharedMessage->GetText().IsEmpty())
			{
				m_editSharedMessage->SetText(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY).c_str());
				m_editSharedMessage->SetTextColor(0x99999999);
				m_isInvite = false;
			}
			else
			{
				m_isInvite = true;
			}
	}

	void ShareFrameV2::messageReturnDown(TNotifyUI& msg)
	{
			CHARRANGE cr;
			m_editSharedMessage->GetSel(cr);
			LONG curLength= cr.cpMax;
			std::wstring textstring = m_editSharedMessage->GetText().GetData();
			std::wstring frontpartstring = textstring.substr(0,curLength);
			std::wstring middlepartstring = L"";
			std::wstring backpartstring =L"";
			bool isAddReturn = false;
			if (textstring.length() != 0)
			{
				if (textstring.length() != curLength)
				{
					backpartstring = textstring.substr(curLength,textstring.length()-curLength);
				}
				std::wstring tmp =L"";
				while (frontpartstring != L"" &&  frontpartstring[frontpartstring.length()-1] ==  L'\r')
				{
					tmp += L"\r\n";
					frontpartstring = frontpartstring.substr(0,frontpartstring.length()-1);
				}
				frontpartstring += tmp;
				while (backpartstring != L"" && backpartstring[backpartstring.length()-1] == L'\r')
				{
					backpartstring = backpartstring.substr(0,backpartstring.length()-1);
				}
			}
			std::wstring inputstring = frontpartstring;
			if (textstring.length() == curLength || textstring[curLength] == L'\r' )
			{
				if (inputstring[inputstring.length()-1] != L'\n')
				{
					inputstring+=L"\r\n";
				}
				inputstring+=L"\r\n";
			}
			else if(curLength<1 || textstring[curLength-1] != '\r')
			{
				inputstring+=L"\r\n";
			}
			if (curLength >=1 && textstring[curLength-1] == L'\r')
			{
				inputstring+=L"\r\n";
			}
			inputstring += backpartstring;
			m_editSharedMessage->SetText(inputstring.c_str());
			m_editSharedMessage->SetSel(cr.cpMin+1,cr.cpMax+1);
		
	}

	void ShareFrameV2::deleteallClick(TNotifyUI& msg)
	{
		m_noticeFrame_->Run(Choose,Ask,MSG_SHARE_DELETE_ALLSHARER_TITLE_KEY,MSG_SHARE_DELETE_ALLSHARER_KEY, Modal);
		if (!m_noticeFrame_->IsClickOk())	return;

		sharedUserList->RemoveAll();
		ShareNodeList shareNodes;
		ShareNodeEx shareNodeEx;
		userContext_->getShareResMgr()->listShareMember(nodeData_.basic.id, shareNodes);
		bool bRet = true;
		for (ShareNodeList::iterator it=shareNodes.begin();it!=shareNodes.end();++it)
		{
			shareNodeEx.sharedUserId(it->sharedUserId());
			shareNodeEx.sharedUserType(it->sharedUserType());
			shareNodeEx.roleName(it->roleName());

			if (RT_OK != userContext_->getShareResMgr()->delShareMember(nodeData_.basic.id, shareNodeEx))
				bRet = false;
		}
		if (!bRet)
			loadSharedUsersList();		
	}

	void ShareFrameV2::copyallClick(TNotifyUI& msg)
	{
		ShareNodeList shareNodes;
		userContext_->getShareResMgr()->listShareMember(nodeData_.basic.id, shareNodes);
		if(shareNodes.empty()) return;
		ShareNodeList::iterator it = shareNodes.begin();
		std::wstring source =  SD::Utility::String::utf8_to_wstring(it->sharedUserName());
		++it;
		for(; it != shareNodes.end(); ++it)
		{
			source += L";";
			source += SD::Utility::String::utf8_to_wstring(it->sharedUserName());
		}
		if(OpenClipboard(NULL)) 
		{
			EmptyClipboard();
			HGLOBAL clipbuffer;  
			clipbuffer = GlobalAlloc(GMEM_MOVEABLE, (source.size()+1)*sizeof(TCHAR));
			if(NULL == clipbuffer) 
			{
				CloseClipboard();
				return;
			}
			
			LPTSTR lpStr =(LPTSTR)GlobalLock(clipbuffer);
			if(NULL != lpStr)
			{
				memcpy_s(lpStr, (source.size()+1)*sizeof(TCHAR), source.c_str(), source.size()*sizeof(TCHAR));
				lpStr[source.size()]=(TCHAR)0;
			}
			GlobalUnlock(clipbuffer); 
			SetClipboardData(CF_UNICODETEXT,clipbuffer); 
			CloseClipboard();
			GlobalFree(clipbuffer);			
		}
		m_noticeFrame_->Run(Confirm,Right,L"",MSG_SHARE_COPYSUCCESS_KEY);

		if (OpenClipboard(NULL))
		{
			HANDLE hData = GetClipboardData(CF_UNICODETEXT);
			TCHAR * buffer = (TCHAR *)GlobalLock(hData);
			if(NULL != buffer)
			{
				std::wstring destStr = buffer;
				if(destStr==source)
				{
					m_noticeFrame_->Run(Confirm,Right,L"",MSG_SHARE_COPYSUCCESS_KEY);
				}
				else
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "copy to clipboard error");
				}
			}
			GlobalUnlock(hData);
			CloseClipboard();
		}
	}

	void ShareFrameV2::inviteClick(TNotifyUI& msg)
	{
		int32_t iRet = RT_OK;

		//TODO 处理鼠标状态为等待
		m_editSharedUsers->SetEnabled(FALSE);
		m_editSharedMessage ->SetEnabled(FALSE);
		std::string rolename=roleName_[shareMenu->GetCurSel()];
		if(m_inviteUsersList.empty() && m_inviteGroupList.empty())
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_NOTSETVALIDUSER_KEY);
			m_editSharedUsers->SetEnabled(true);
			m_editSharedMessage ->SetEnabled(true);
			m_btnSharedCancel -> SetEnabled(true);
			return;
		}
		ShareNodeExList shareNodeExs;
		for (ShareUserInfoList::iterator it = m_inviteUsersList.begin(); it != m_inviteUsersList.end(); ++it)
		{
			ShareNodeEx shareNodeEx;
			shareNodeEx.sharedUserId(it->id());
			shareNodeEx.sharedUserType("user");
			shareNodeEx.roleName(rolename);
			shareNodeEx.sharedEmail(it->email());
			shareNodeEx.loginName(it->loginName());
			shareNodeExs.push_back(shareNodeEx);
		}

		for (UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
		{
			ShareNodeEx shareNodeEx;
			shareNodeEx.sharedUserId(it->id);
			shareNodeEx.sharedUserType("group");
			shareNodeEx.roleName(rolename);
			shareNodeExs.push_back(shareNodeEx);
		}

		std::wstring emailMsg = L"";
		if(m_isInvite)
		{
			emailMsg = m_editSharedMessage->GetText();
			if(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY) ==  emailMsg)
			{
				emailMsg = L"";
			}
		}

		CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),m_hWnd, boost::bind(
			&ShareResMgr::setShareV2, 
			userContext_->getShareResMgr(), 
			nodeData_.basic.id, shareNodeExs, 
			nodeData_.basic.type, nodeData_.basic.name, SD::Utility::String::wstring_to_utf8(emailMsg)));

		m_inviteGroupList.clear();
		m_inviteUsersList.clear();
		CUserListControlUI* pContainer = static_cast<CUserListControlUI*>(m_PaintManager.FindControl(L"shareFrame_tileLayout_listView"));
		pContainer->RemoveAll();

		pContainer->SetVisible(false);
		m_shareFrame_user_layout->SetFixedHeight(31);
		loadSharedUsersList();
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"begin"));
		{
			RECT rc;
			::GetWindowRect(GetHWND(),&rc);	
			int height=SHARE_FRAME_MAX_SIZE-SHARE_FRAME_CHANGE_DISTANCE;
			m_editSharedUsers->SetEnabled();
			inviteBTN->SetVisible(false);
			cancelBTN->SetVisible(false);
			closeBTN->SetVisible();
			copyallBTN->SetVisible();
			deleteallBTN->SetVisible();
			m_addNodes.clear();
			m_editSharedMessage->SetText(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY).c_str());
			m_editSharedMessage->SetTextColor(0x99999999);
			m_sHareFrame_menulayout->SetVisible(false);
			m_shareFrame_messagelayout->SetVisible(false);

			int width = rc.right - rc.left;
			rc.bottom =rc.top+ SHARE_FRAME_MAX_SIZE;
			::MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
			::InvalidateRect(*this,&rc,true);

		}
		return;
	}

	void ShareFrameV2::hiddenListItemClick(TNotifyUI& msg)
	{
		return;
	}


	void ShareFrameV2::itemDBClick(TNotifyUI& msg)
	{
		int32_t ret = RT_OK;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(msg.pSender);
		if (item == NULL) return;
		UIGroupNode groupNode = item->nodeData.groupNode;
		ShareUserInfo shareUserInfo = item->nodeData.shareUserInfo;
		if(!shareUserInfo.loginName().empty())
		{
			//add the node to the invite users list
			ShareUserInfoList users;
			UIGroupNodeList groups;
			users.push_back(shareUserInfo);
			addSharedTileUserList(users,groups);
		}
		else if(groupNode.id != NULL)
		{
			//add the node to the invite group list
			ShareUserInfoList users;
			UIGroupNodeList groups;
			groups.push_back(groupNode);
			addSharedTileUserList(users,groups);	
		}

		m_editSharedUsers->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str());
        m_editSharedUsers->SetTextColor(0x99999999);

		m_listUser->RemoveAll();
		m_listUser->SetVisible(false);
	}

	void ShareFrameV2::ShowSharedFrame()
	{
		TNotifyUI msg;
		Create(paintManager_.GetPaintWindow(), iniLanguageHelper.GetWindowName(WINDOWNAME_SHARE_KEY).c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		shareSelectchanged();
		showButton();	
		CenterWindow();
		ShowModal();
	}

	bool ShareFrameV2::isExistsharedUsers(CListUI* sharedUsers, ShareUserInfo& shareUserInfo, std::string type)
	{
		int32_t count = sharedUsers->GetCount();
		for(int32_t i = 0; i < count; i++)
		{
			ShareFrameListContainerElement* pFileListItem = static_cast<ShareFrameListContainerElement*>(sharedUsers->GetItemAt(i));
			if(pFileListItem == NULL)
			{
				continue;
			}
			int64_t tmpId = pFileListItem->nodeData.shareNode.sharedUserId();
			std::string tmprolename = pFileListItem->nodeData.shareNode.roleName();
			std::string tmpLoginName = pFileListItem->nodeData.shareNode.sharedUserLoginName();
			std::string tmpType = pFileListItem->nodeData.shareNode.sharedUserType();
			if(shareUserInfo.id() == 0)
			{
				if(tmpLoginName == shareUserInfo.loginName() && type == tmpType)
				{
					return TRUE;
				}
			}
			else if(tmpId == shareUserInfo.id() && type == tmpType) 
			{
				return TRUE;
			}
		}
		return FALSE;
	}

	bool ShareFrameV2::isExistsharedTileUsers(ShareNode& node)
	{
		for (std::list<ShareNode>::iterator it = m_addNodes.begin(); it != m_addNodes.end(); ++it)
		{
			int64_t tmpId = it->sharedUserId();
			std::string tmpLoginName = it->sharedUserLoginName();
			std::string tmpType = it->sharedUserType();

			if(node.sharedUserId() == 0)
			{
				if(tmpLoginName == node.sharedUserLoginName())
				{
					return TRUE;
				}
			}
			else if(tmpId == node.sharedUserId())
			{
				return TRUE;
			}
		}
		return FALSE;
	}

	int32_t ShareFrameV2::addSharedUserList(ShareUserInfo& shareUserInfo, std::string type, int32_t shareType)
	{
		if(type == "user")
		{
			ProxyMgr::getInstance(userContext_)->addUser(shareUserInfo);
		}
		
		CDialogBuilder builder;
		ShareFrameListContainerElement* node = static_cast<ShareFrameListContainerElement*>
			(builder.Create(shareType?ControlNames::SKIN_XML_SHARE_ITEM:ControlNames::SKIN_XML_SHAREEX_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
		if(NULL == node)
		{
			return RT_INVALID_PARAM;
		}

		if(isExistsharedUsers(sharedUserList, shareUserInfo, type))
		{
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_HASSHARED_KEY,Modal);
			skipChange_ = true;
			return RT_ERROR;
		}

		node->nodeData.shareNode.id(nodeData_.basic.id);
		node->nodeData.shareNode.type(nodeData_.basic.type);
		node->nodeData.shareNode.ownerId(userContext_->getUserInfoMgr()->getUserId());
		node->nodeData.shareNode.ownerName(SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName()));
		node->nodeData.shareNode.sharedUserId(shareUserInfo.id());
		node->nodeData.shareNode.sharedUserType(type);
		node->nodeData.shareNode.sharedUserName(shareUserInfo.name());
		node->nodeData.shareNode.sharedUserLoginName(shareUserInfo.loginName());
		node->nodeData.shareNode.sharedUserDescription(shareUserInfo.department());
		node->nodeData.shareNode.name(SD::Utility::String::wstring_to_utf8(nodeData_.basic.name));
		node->nodeData.shareNode.modifiedAt(nodeData_.basic.mtime);
		node->nodeData.shareNode.size(nodeData_.basic.size);
		node->nodeData.shareNode.roleName(shareUserInfo.roleName());

		SERVICE_INFO(MODULE_NAME, RT_OK, "node->nodeData.shareNode.sharedUserId %I64d", shareUserInfo.id());
		SERVICE_INFO(MODULE_NAME, RT_OK, "shareUserInfo.roleName() %s", SD::Utility::String::utf8_to_wstring(shareUserInfo.roleName()).c_str());

		 //initial UI
		CLabelUI* icon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_ITEMICON));
		if (NULL != icon)
		{
			std::wstring str_photoPath;
			if(node->nodeData.shareNode.sharedUserType() == "user")
			{
				str_photoPath = GetInstallPath() + L"skin\\Image\\" + L"ic_popup_share_person.png";
			}
			else if(node->nodeData.shareNode.sharedUserType() == "group")
			{
				str_photoPath += GetInstallPath() + L"skin\\IMage\\" + L"ic_popup_share_team.png";
			}
			icon->SetBkImage(str_photoPath.c_str());
		}

		std::map<std::wstring, std::wstring>::iterator it = roleMaps_.find(SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.roleName()));
		if(it == roleMaps_.end())
		{
			return RT_ERROR;
		}

		CLabelUI* name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_USERNAME));
		if (NULL != name)
		{
			std::wstring tmpStr = SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.sharedUserName());
			name->SetToolTip(tmpStr.c_str());
			name->SetText(tmpStr.c_str());
		}

		CScaleIconButtonUI* pButton = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_SCALE_BUTTON));
		if(NULL==pButton) return RT_ERROR;
		pButton->SetText(it->second.c_str());
		pButton->SetToolTip(it->second.c_str());

		CCustomComboUI* combo = static_cast<CCustomComboUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_ITEMCOMBO));
		if(NULL==combo) return RT_ERROR;
		int32_t viewCount = 0;
		initComboList(combo, m_sysRoleInfoExs, viewCount);

		if (!sharedUserList->AddAt(node, 0))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
			return RT_ERROR;
		}
				
		skipChange_ = true;
		return RT_OK;
	}


	void ShareFrameV2::fillShareNode(int64_t& shareUserId,std::string& shareUserName, std::string& shareUserLoginName,std::string& shareUserDepartment, std::string& roleName,ShareNode& rNode)
	{
		rNode.id(nodeData_.basic.id);
		rNode.type(nodeData_.basic.type);
		rNode.ownerId(userContext_->getUserInfoMgr()->getUserId());
		rNode.ownerName(SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName()));
		rNode.sharedUserId(shareUserId);
		rNode.sharedUserType("");
		rNode.sharedUserName(shareUserName);
		rNode.sharedUserLoginName(shareUserLoginName);
		rNode.sharedUserDescription(shareUserDepartment);
		rNode.name(SD::Utility::String::wstring_to_utf8(nodeData_.basic.name));
		rNode.modifiedAt(nodeData_.basic.mtime);
		rNode.size(nodeData_.basic.size);
		rNode.roleName(roleName);
	}

	void ShareFrameV2::setUserListVisible()
	{
		RECT rc = 	m_shareFrame_user_layout->GetPos();
		rc.top = rc.bottom;
		rc.bottom = rc.top + 115;
		m_listUser->SetPos(rc);
		m_listUser->SetVisible();
	}

	

	int32_t ShareFrameV2::addSharedTileUserList(ShareUserInfoList& shareUserInfos,UIGroupNodeList& groupList,bool bDelete)
	{	 
		CUserListControlUI* pContainer = static_cast<CUserListControlUI*>(m_PaintManager.FindControl(L"shareFrame_tileLayout_listView"));
		//获取之前存储的
		std::list<ShareNode> nameNodes;
		for (std::list<ShareNode>::iterator it = m_addNodes.begin(); it != m_addNodes.end(); ++it)
		{
			nameNodes.push_back(*it);
		}

		ShareNode _node;
		for (ShareUserInfoList::iterator it = shareUserInfos.begin(); it != shareUserInfos.end(); ++it)
		{
			fillShareNode((int64_t)(it->id()),(std::string)(it->name()),(std::string)(it->loginName()),(std::string)(it->department()),(std::string)(it->roleName()),_node);		
			if (isExistsharedTileUsers(_node))
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_HASSHARED_KEY,Modal);
				skipChange_ = true;
				return RT_ERROR;
			}
			
			if (_node.sharedUserLoginName()==_node.ownerName())
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_NOTSHARETOMYSELF_KEY,Modal);
				skipChange_ = true;
				return RT_ERROR;
			}
			nameNodes.push_back(_node);
			m_addNodes.push_back(_node);
			
			m_inviteUsersList.push_back(*it);
		}

		for (UIGroupNodeList::iterator it = groupList.begin(); it != groupList.end(); ++it)
		{
			fillShareNode(it->id,SD::Utility::String::wstring_to_utf8(it->name),SD::Utility::String::wstring_to_string(it->name),SD::Utility::String::wstring_to_string(it->description),SD::Utility::String::wstring_to_string(it->roleName),_node);
			
			if (isExistsharedTileUsers(_node))
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_HASSHARED_KEY,Modal);
				skipChange_ = true;
				return RT_ERROR;
			}
			nameNodes.push_back(_node);
			m_addNodes.push_back(_node);
			m_inviteGroupList.push_back(*it);
		}

		int32_t _height = 0;
		pContainer->showList(nameNodes,_height);

		bool bIsShow = false;
		if (0 != _height)
		{
			bIsShow = true;
		}
		int32_t horHeight = m_shareFrame_user_layout->GetFixedHeight();
		if (horHeight == _height + 31) return RT_OK;
		m_shareFrame_user_layout->SetFixedHeight(_height + 31);
		pContainer->SetVisible(bIsShow);
		RECT rc;
		::GetWindowRect(GetHWND(),&rc);	
		int width = rc.right - rc.left;
		int height = rc.bottom - rc.top + (bDelete == true ? -25 : _height);
		MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
		::InvalidateRect(*this,&rc,true);

			inviteBTN->SetEnabled(bIsShow);
		return RT_OK;
	}

	void ShareFrameV2::showButton()
	{
		if (sharedUserList->GetCount()==0)
		{
			deleteallBTN->SetVisible(false);
			copyallBTN->SetVisible(false);
		}
		closeBTN->SetVisible(true);
	}


	void ShareFrameV2::compeleteClick(TNotifyUI& msg)
	{
		Close();
		return;
	}

	void ShareFrameV2::comboItemSelect(TNotifyUI& msg)
	{  
		timerMsg = msg;
		SetTimer(*this, TIMERID_COMBOXITEMSELECT, 10, NULL);
	}

	void ShareFrameV2::timerFunc(TNotifyUI& msg)
	{
		if(msg.wParam > roleName_.size()) return;
		if(NULL==msg.pSender->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
		CCustomComboUI* combo = static_cast<CCustomComboUI*>(m_PaintManager.FindSubControlByName(msg.pSender->GetParent(), SHAREFRAME_ITEMCOMBO));
		CScaleIconButtonUI* pButton = static_cast<CScaleIconButtonUI*>(m_PaintManager.FindSubControlByName(msg.pSender->GetParent(), SHAREFRAME_SCALE_BUTTON));
		CLabelUI* pLable = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(msg.pSender->GetParent(),L"shareFrame_item_label"));
		if(NULL==pButton||NULL==combo||NULL==pLable||item == NULL) return;

		//bool kk=combo->IsVisible();
		ShareNode shareInfo = item->nodeData.shareNode;
		ShareNodeEx shareNodeEx;
		ShareNodeExList shareNodeExs;
		shareNodeEx.sharedUserId(shareInfo.sharedUserId());
		shareNodeEx.sharedUserType(shareInfo.sharedUserType());
		shareNodeEx.loginName(shareInfo.sharedUserLoginName());
		shareNodeEx.sharedEmail();
		shareNodeEx.roleName(roleName_[msg.wParam]);
		shareNodeExs.push_back(shareNodeEx);

		int32_t ret = userContext_->getShareResMgr()->setShareV2(nodeData_.basic.id, shareNodeExs, 
			nodeData_.basic.type, nodeData_.basic.name, "");
		if(ret == RT_OK)
		{			
			m_noticeFrame_->Run(Confirm,Right,L"",MSG_TEAMSPACE_MANAGER_SUCCESS_SETTING_KEY, NotModal);
		}
		else
		{
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_TEAMSPACE_MANAGER_FAIL_SETTING_KEY, NotModal);
		}


		std::map<std::wstring, std::wstring>::iterator it = roleMaps_.find(SD::Utility::String::utf8_to_wstring(roleName_[msg.wParam]));
		if(it == roleMaps_.end())
		{
			return;
		}

		pButton->SetText(it->second.c_str());
		pButton->SetToolTip(it->second.c_str());
		loadSharedUsersList();
	}

	void ShareFrameV2::menuItemSelect(TNotifyUI& msg)
	{
		if(msg.wParam > roleName_.size())
		{
			return;
		}


		std::map<std::wstring, std::wstring>::iterator it = roleMaps_.find(SD::Utility::String::utf8_to_wstring(roleName_[msg.wParam]));
		if(it == roleMaps_.end())
		{
			return;
		}
		
		shareMenu->SetToolTip(it->second.c_str());
		return;
	}
	
	void ShareFrameV2::deleteShareUserList(TNotifyUI& msg)
	{
		if(NULL==msg.pSender->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
		if (item == NULL) return;
		ShareNode shareInfo = item->nodeData.shareNode;
		UIGroupNode groupNode = item->nodeData.groupNode;

		ShareNodeEx shareNodeEx;

		shareNodeEx.sharedUserId(shareInfo.sharedUserId());
		shareNodeEx.sharedUserType(shareInfo.sharedUserType());
		shareNodeEx.roleName(shareInfo.roleName());

		SERVICE_INFO(MODULE_NAME, RT_OK, "sharedUserId:%I64d, sharedUserType:%s, roleName:%s", shareInfo.sharedUserId(),
			SD::Utility::String::utf8_to_wstring(shareNodeEx.sharedUserType()).c_str(),
			SD::Utility::String::utf8_to_wstring(shareNodeEx.roleName()).c_str());		
		
		int32_t ret = userContext_->getShareResMgr()->delShareMember(nodeData_.basic.id, shareNodeEx);

		if(ret == HTTP_NOT_FOUND)
		{
			if(shareInfo.id() != NULL)
			{
				for(ShareUserInfoList::iterator it = m_inviteUsersList.begin(); it != m_inviteUsersList.end(); ++it)
				{
					if(it->loginName() != shareInfo.sharedUserLoginName())
					{
						continue;
					}
					it = m_inviteUsersList.erase(it);
					break;
				}
				for(UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
				{
					if(it->id != shareInfo.sharedUserId())
					{
						continue;
					}
					it = m_inviteGroupList.erase(it);
					break;
				}
			}
		}
		else if (ret != RT_OK)
		{
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_DELETEFAILED_KEY);
			return;
		}
		//delete the last one person should be cancel the share
		int32_t count = sharedUserList->GetCount();
		if(1 == count)
		{
			userContext_->getShareResMgr()->cancelShare(nodeData_.basic.id);
		}
		loadSharedUsersList();
		for(ShareUserInfoList::iterator it = m_inviteUsersList.begin(); it != m_inviteUsersList.end(); ++it)
		{
			(void)addSharedUserList(*it, "user", ShareType_Share);
		}
		for(UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
		{
			ShareUserInfo shareUserInfo;
			shareUserInfo.id(it->id);
			shareUserInfo.name(SD::Utility::String::wstring_to_utf8(it->name));
			shareUserInfo.loginName();
			shareUserInfo.department(SD::Utility::String::wstring_to_utf8(it->description));
			shareUserInfo.roleName(SD::Utility::String::wstring_to_utf8(it->roleName));
			(void)addSharedUserList(shareUserInfo, "group", ShareType_Share);
		}
		return;
	}

	std::vector<std::wstring> ShareFrameV2::getBatchUsers(std::wstring searchKey)
	{
		searchKey = SD::Utility::String::replace_all(searchKey,L",",L";");
		searchKey = SD::Utility::String::replace_all(searchKey,L"，",L";");
		searchKey = SD::Utility::String::replace_all(searchKey,L"；",L";");
		std::vector<std::wstring> vecRturn;
		vecRturn.clear();
		size_t i = 0;
		while (std::wstring::npos != i)
		{
			i = searchKey.find_first_of(L";");
			if (i != std::wstring::npos)
			{
				std::wstring findstr = searchKey.substr(0,i);
				searchKey = searchKey.substr(i+1,searchKey.length()-i-1);
				vecRturn.push_back(findstr);
			}
		}
		if (L"" != searchKey)
		{
			vecRturn.push_back(searchKey);
		}
		return vecRturn;
	}

	void ShareFrameV2::initComboList(CComboUI *combo, SysRoleInfoExList& sysRoleInfoExs, int32_t& viewCount)
	{
		int32_t count = 0;
		if(nodeData_.basic.type)
		{
			//std::sort(sysRoleInfoExs.begin(), sysRoleInfoExs.end(), &Onebox::ShareFrameV2::SortSysRoleInfoEx);

			for(SysRoleInfoExList::iterator it = sysRoleInfoExs.begin(); it != sysRoleInfoExs.end(); ++it)
			{
				if(it->name == SYSROLE_PREVIEWER)
				{
					CListLabelElementUI* pElement = new CListLabelElementUI;
					pElement->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_PREVIEWER_KEY).c_str());
					combo->Add(pElement);
					count++;
				}
				else if(it->name == SYSROLE_VIEWER)
				{
					CListLabelElementUI* pElement1 = new CListLabelElementUI;
					pElement1->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY).c_str());
					combo->Add(pElement1);
					viewCount = count;
					count++;
				}
				else if(it->name == SYSROLE_LISTER)
				{
					CListLabelElementUI* pElement2 = new CListLabelElementUI;
					pElement2->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_LISTER_KEY).c_str());
					combo->Add(pElement2);
					count++;
				}
			}
		}
		else
		{
			for(SysRoleInfoExList::iterator it = sysRoleInfoExs.begin(); it != sysRoleInfoExs.end(); ++it)
			{
				if(it->name == SYSROLE_EDITOR)
				{
					CListLabelElementUI* pElement = new CListLabelElementUI;
					pElement->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY).c_str());
					combo->Add(pElement);
				}
				else if(it->name == SYSROLE_PREVIEWER)
				{
					CListLabelElementUI* pElement1 = new CListLabelElementUI;
					pElement1->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_PREVIEWER_KEY).c_str());
					combo->Add(pElement1);
				}
				else if(it->name == SYSROLE_UPLOADERANDVIEWER)
				{
					CListLabelElementUI* pElement2 = new CListLabelElementUI;
					pElement2->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADERANDVIEWER_KEY).c_str());
					combo->Add(pElement2);
				}
				else if(it->name == SYSROLE_UPLOADER)
				{
					CListLabelElementUI* pElement3 = new CListLabelElementUI;
					pElement3->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADER_KEY).c_str());
					combo->Add(pElement3);
				}
				else if(it->name == SYSROLE_VIEWER)
				{
					CListLabelElementUI* pElement4 = new CListLabelElementUI;
					pElement4->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY).c_str());
					combo->Add(pElement4);
					viewCount = count;
				}
				else if(it->name == SYSROLE_LISTER)
				{
					CListLabelElementUI* pElement5 = new CListLabelElementUI;
					pElement5->SetText(iniLanguageHelper.GetCommonString(TEAMSPACE_LISTER_KEY).c_str());
					combo->Add(pElement5);
				}
				count++;
			}
		}
		
		return;
	}

	void ShareFrameV2::itemLabelSetfocus(TNotifyUI& msg)
	{
		CCustomComboUI* combo = static_cast<CCustomComboUI*>(m_PaintManager.FindSubControlByName(msg.pSender->GetParent(), SHAREFRAME_ITEMCOMBO));
		if(NULL==combo) return;	
		combo->Activate();								
	}

	bool ShareFrameV2::SortSysRoleInfoEx(const SysRoleInfoEx& nodeA, const SysRoleInfoEx& nodeB)
	{
		int nAccessA = 0, nAccessB = 0;
		if (nodeA.name == "previewer")	nAccessA = 2;
		else if (nodeA.name == "viewer")	nAccessA = 1;
		else if (nodeA.name == "editor")	nAccessA = 3;
		else if (nodeA.name == "uploader")	nAccessA = 4;
		else if (nodeA.name == "uploadAndView")	nAccessA = 5;

		if (nodeB.name == "previewer")	nAccessB = 2;
		else if (nodeB.name == "viewer")	nAccessB = 1;
		else if (nodeA.name == "editor")	nAccessA = 3;
		else if (nodeB.name == "uploader")	nAccessB = 4;
		else if (nodeB.name == "uploadAndView")	nAccessB = 5;
				
		return nAccessA < nAccessB;
	}
}