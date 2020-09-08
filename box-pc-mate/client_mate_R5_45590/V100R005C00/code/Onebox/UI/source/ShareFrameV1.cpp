#include "stdafxOnebox.h"
#include "ShareFrameV1.h"
#include "Utility.h"
#include "ControlNames.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "ShareUserInfo.h"
#include "ShareResMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "CustomListUI.h"
#include "ShareFrameHiddenList.h"
#include "ProxyMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "CommonLoadingFrame.h"
#include "PathMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ShareFrame")
#endif

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(ShareFrameV1,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SELECTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_TEXTCHANGED,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_RETURN,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_SETFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK,executeFunc)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMCLICK,executeFunc)
	DUI_END_MESSAGE_MAP()

	ShareFrameV1::ShareFrameV1(UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager)
	:userContext_(context)
	,nodeData_(nodeData)
	,paintManager_(paintManager)
	,skipChange_(false)
	{
		int32_t ret = RT_OK;
		funcMaps_.insert(std::make_pair(L"shareFrame_share_selectchanged", boost::bind(&ShareFrameV1::shareSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_link_selectchanged", boost::bind(&ShareFrameV1::linkSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_setfocus", boost::bind(&ShareFrameV1::usersSetfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_textchanged", boost::bind(&ShareFrameV1::usersTextchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_killfocus", boost::bind(&ShareFrameV1::usersKillfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_users_return", boost::bind(&ShareFrameV1::usersReturndown, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_add_click", boost::bind(&ShareFrameV1::addClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_setfocus", boost::bind(&ShareFrameV1::messageSetfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_killfocus", boost::bind(&ShareFrameV1::messageKillfocus, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_message_return", boost::bind(&ShareFrameV1::messageReturnDown, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_invite_click", boost::bind(&ShareFrameV1::inviteClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_ListItem_itemclick", boost::bind(&ShareFrameV1::hiddenListItemClick, this, _1)));
		//funcMaps_.insert(std::make_pair(L"shareFrame_ListItem_itemdbclick", boost::bind(&ShareFrameV1::hiddenListItemDBClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_publicLink_selectchanged", boost::bind(&ShareFrameV1::publicLinkSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_privateLink_selectchanged", boost::bind(&ShareFrameV1::privateLinkSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_codeSelect_selectchanged", boost::bind(&ShareFrameV1::codeSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_dateSelect_selectchanged", boost::bind(&ShareFrameV1::datatimeSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_compelete_click", boost::bind(&ShareFrameV1::compeleteClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_linkCancel_click", boost::bind(&ShareFrameV1::linkCancelClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_publicCopyUrl_click", boost::bind(&ShareFrameV1::publicCopyUrlClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_privateCopyUrl_click", boost::bind(&ShareFrameV1::praviteCopyUrlClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TeamSpaceManage_listUserItem_itemclick", boost::bind(&ShareFrameV1::itemDBClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_item_delete_click", boost::bind(&ShareFrameV1::deleteShareUserList, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_privateCodeRefresh_click", boost::bind(&ShareFrameV1::refreshClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_save_click", boost::bind(&ShareFrameV1::saveClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareFrame_back_click", boost::bind(&ShareFrameV1::backClick, this, _1)));

		ret = userContext_->getShareResMgr()->getServerConfig(m_serverSysConfig);
		if(ret != RT_OK)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "getServerConfig failed.");
		}
		m_noticeFrame_ = new NoticeFrameMgr(m_hWnd);
	}

	ShareFrameV1::~ShareFrameV1()
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
		delete m_noticeFrame_;
		m_noticeFrame_=NULL;
	}

	void ShareFrameV1::init()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "ShareFrameV1::init");
		m_editSharedUsers = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_SHARED_USERS));
		m_editSharedMessage = static_cast<CEditUI*>(m_PaintManager.FindControl(ControlNames::EDIT_SHARED_MESSAGE));
		m_btnSharedCancel = static_cast<CButtonUI*>(m_PaintManager.FindControl(ControlNames::BTN_SHARED_CANCEL));
		if(m_editSharedUsers == NULL || m_editSharedMessage == NULL || m_btnSharedCancel == NULL)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "init ControlUI failed.");
			return;
		}
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODE));
		CDateTimeUI* datatimeFirst = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKFIRSTDATETIME));
		CTextUI* datatimeTip = static_cast<CTextUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKTEXTDATETIP));
		CDateTimeUI* datatimeSecond = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKSECONDDATETIME));
		if(codeEdit == NULL || datatimeFirst == NULL || datatimeTip == NULL || datatimeSecond == NULL)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "init ControlUI failed.");
			return;
		}
		codeEdit->SetVisible(false);
		datatimeFirst->SetVisible(false);
		datatimeTip->SetVisible(false);
		datatimeSecond->SetVisible(false);

		CEditUI* titleName = static_cast<CEditUI*>(m_PaintManager.FindControl(L"shareFrame_title"));
		if(titleName == NULL)
		{
			return;
		}
		std::wstring defaultText = titleName->GetText();
		std::wstring tmpFileName =  defaultText + L"‘" + nodeData_.basic.name + L"’";
		titleName->SetText(tmpFileName.c_str());

		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if(userstext == NULL)
		{
			return;
		}
		EmailInfoNode emailInfoNode;
		userContext_->getShareResMgr()->getMailInfo(nodeData_.basic.id, "share", emailInfoNode);
		if(!emailInfoNode.message.empty())
		{
			userstext->SetText(SD::Utility::String::utf8_to_wstring(emailInfoNode.message).c_str());
		}

		showButton(0);
		loadSharedUsersList();

	}

	void ShareFrameV1::loadSharedUsersList()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "ShareFrameV1::loadSharedUsersList");
		int32_t ret = RT_OK;
		ShareNodeList shareNodes;

		//init my files list and get the selected item of the list
		ret = userContext_->getShareResMgr()->listShareMember(nodeData_.basic.id, shareNodes);
		if(ret != RT_OK)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "listShareMember failed.");
		}
		//init the shared users list
		CListUI* sharedUserList = static_cast<CListUI*>(m_PaintManager.FindControl(SHAREFRAME_SHAREUSERSLIST));
		if(NULL == sharedUserList)
		{
			return;
		}
		sharedUserList->RemoveAll();

		for(ShareNodeList::iterator it = shareNodes.begin(); it != shareNodes.end(); it++)
		{
			CDialogBuilder builder;
			ShareFrameListContainerElement* node = static_cast<ShareFrameListContainerElement*>(
				builder.Create(ControlNames::SKIN_XML_SHARE_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
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
					str_photoPath = GetInstallPath() + L"skin\\img\\" + L"teamSpaceManage.png";
				}
				else if(node->nodeData.shareNode.sharedUserType() == "group")
				{
					str_photoPath += GetInstallPath() + L"skin\\img\\" + L"icons-orange.png";
				}
				icon->SetBkImage(str_photoPath.c_str());
			}

			CLabelUI* name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_USERNAME));
			if(NULL != name)
			{
				std::wstring tmpStr = SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.sharedUserName());
				name->SetToolTip(tmpStr.c_str());
				name->SetShowHtml(true);
				name->SetText(tmpStr.c_str());
			}

			if(!sharedUserList->Add(node))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
			}
		}
	}

	LPCTSTR ShareFrameV1::GetWindowClassName(void) const
	{
		return ControlNames::WND_SHARE_CLS_NAME;
	}

	DuiLib::CDuiString ShareFrameV1::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	DuiLib::CDuiString ShareFrameV1::GetSkinFile()
	{
		return ControlNames::SKIN_XML_SHARE_FILE;
	}

	CControlUI* ShareFrameV1::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool ShareFrameV1::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT ShareFrameV1::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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

	LRESULT ShareFrameV1::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		WindowImplBase::OnCreate(uMsg, wParam, lParam, bHandled);
		init();
		return 0;
	}

	LRESULT ShareFrameV1::OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void ShareFrameV1::Notify(DuiLib::TNotifyUI& msg)
	{
		return WindowImplBase::Notify(msg);
	}

	LRESULT ShareFrameV1::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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

	void ShareFrameV1::OnClick(DuiLib::TNotifyUI& msg)
	{
		CDuiString name = msg.pSender->GetName();
		if (name == ControlNames::BTN_CLOSE || name == ControlNames::BTN_SHARED_CANCEL)
		{
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKLISTVIEW));
			if (NULL == pControl)
			{
				return;
			}
			if(pControl->GetCurSel() == 2)
			{
				std::wstring linkCode = L"";
				std::wstring type = L"";
				CEditUI* linkUrl = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATELINKURL));
				if (NULL == linkUrl)
				{
					return;
				}
				std::wstring tmpUrl = linkUrl->GetText();
				if(tmpUrl.empty())
				{
					return;
				}
				std::string::size_type pos = SD::Utility::String::wstring_to_utf8(tmpUrl).find("/p/");
				linkCode = tmpUrl.substr(pos+3, tmpUrl.size()-pos -3);
				(void)userContext_->getShareResMgr()->delShareLink(nodeData_.basic.id, SD::Utility::String::wstring_to_utf8(linkCode)
					,SD::Utility::String::wstring_to_utf8(type));
			}
			Close();
			return;
		}
		executeFunc(msg);
	}

	void ShareFrameV1::executeFunc(TNotifyUI& msg)
	{
		CDuiString tempName = msg.pSender->GetName();
		std::wstring funcName = tempName + L"_" + msg.sType;

		std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
		if(it!=funcMaps_.end())
		{
			it->second(msg);
		}
	}

	void ShareFrameV1::shareSelectchanged(TNotifyUI& msg)
	{
		CTabLayoutUI* pShareTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARE));
		if (NULL == pShareTab)
		{
			return;
		}
		pShareTab->SetAttribute(L"selected", L"true");
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		pControl->SelectItem(0);
		showButton(0);
		//get message at the last time
		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if(userstext == NULL)
		{
			return;
		}
		EmailInfoNode emailInfoNode;
		userContext_->getShareResMgr()->getMailInfo(nodeData_.basic.id, "share", emailInfoNode);
		if(!emailInfoNode.message.empty())
		{
			userstext->SetText(SD::Utility::String::utf8_to_wstring(emailInfoNode.message).c_str());
		}
		else
		{
			userstext->SetText(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY).c_str());
			userstext->SetTextColor(0x99999999);
		}

		return;
	}

	void ShareFrameV1::linkSelectchanged(TNotifyUI& msg)
	{
		CTabLayoutUI* pLinkTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINK));
		if (NULL == pLinkTab)
		{
			return;
		}
		pLinkTab->SetAttribute(L"enabled", L"true");
		pLinkTab->SetAttribute(L"selected", L"true");
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		pControl->SelectItem(1);
		showButton(1);
		if(userContext_->getShareResMgr()->hasShareLink(nodeData_.basic.id))
		{
			privateLinkSelectchanged(msg);
		}
		return;
	}

	void ShareFrameV1::usersSetfocus(TNotifyUI& msg)
	{
		CEditUI* userstext = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_USERS));
		if (userstext == NULL) return;

		std::wstring keyWord = userstext->GetText();
		if(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY) == keyWord)
		{
			keyWord = L"";
			userstext->SetText(L"");
			userstext->SetTextColor(0x0);
		}
	}

	void ShareFrameV1::usersTextchanged(TNotifyUI& msg)
	{
		if(skipChange_)
		{
			skipChange_ = false;
			return;
		}
		std::wstring keyWord = msg.pSender->GetText();
		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
		CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
		m_listUser->RemoveAll();
		if (std::wstring::npos != keyWord.find(L",") || std::wstring::npos != keyWord.find(L";"))
		{
			m_listUser->SetVisible(false);
			m_tip->SetVisible(false);
			return;
		}
		listDomainUsers(keyWord, true);
	}

	void ShareFrameV1::usersKillfocus(TNotifyUI& msg)
	{
		CEditUI* userstext = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_USERS));
		if (userstext != NULL)
		{
			if(userstext->GetText().IsEmpty())
			{
				int64_t tmpX = msg.ptMouse.x ;
				int64_t tmpY = msg.ptMouse.y ;

				if((tmpX<100 || tmpX>456) || (tmpY<84 || tmpY>230))
				{
					userstext->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY).c_str());
					userstext->SetTextColor(0x99999999);
					CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
					CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
					if(m_listUser == NULL || m_tip == NULL)
					{
						return;
					}
					m_listUser->SetVisible(false);
					m_tip->SetVisible(false);
				}
				else
				{
				}
			}
		}
	}

	void ShareFrameV1::usersReturndown(TNotifyUI& msg)
	{
		//deal with list domain user
		CEditUI* userstext = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_USERS));
		if(userstext == NULL)
		{
			return;
		}
		std::wstring searchKey = userstext->GetText();
		if(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_KEY) == searchKey || L"" == searchKey)
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
				&ShareFrameV1::findGroupsAndUser, 
				this, 
				boost::ref(vecRemainUser), 
				boost::ref(users), 
				boost::ref(groups), 
				boost::cref(searchKey)));

			for (ShareUserInfoList::iterator it = users.begin(); it != users.end(); ++it)
			{
				int32_t ret = addSharedUserList(*it, "user");
				if(ret == RT_OK)
				{
					m_inviteUsersList.push_back(*it);
				}
			}
			for (GroupNodeList::iterator it = groups.begin(); it != groups.end(); ++it)
			{
				ShareUserInfo shareUserInfo;
				shareUserInfo.id(it->id());
				shareUserInfo.name(it->name());
				shareUserInfo.loginName();
				shareUserInfo.department(it->description());

				int32_t ret = addSharedUserList(shareUserInfo, "group");
				if(ret == RT_OK)
				{
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
					m_inviteGroupList.push_back(uiGroupNode);
				}
			}

			CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
			if(m_listUser == NULL) return;
			CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
			if(m_tip == NULL) return;
			m_listUser->RemoveAll();
			m_listUser->SetVisible(false);
			m_tip->SetVisible(false);
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
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_BATCH_ADD_KEY);
			}
			userstext->SetText(remainderUser.c_str());
			return;
		}
		else
		{
			CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
			if(m_listUser == NULL) return;
			m_listUser->RemoveAll();
			listGroups(searchKey);
			listDomainUsers(searchKey);

			if(m_listUser->GetCount() == NULL)
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_NOTFOUND_KEY);
				m_listUser->SetVisible(false);
				CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
				if(m_tip == NULL) return;
				m_tip->SetVisible(false);
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
		}
	}

	void ShareFrameV1::findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey)
	{
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
				groups.push_back(groupNodes[0]);
			}
			if (shareUserInfos.size() != 0)
			{
				// filter user self
				if(userContext_->getUserInfoMgr()->getUserName() == SD::Utility::String::utf8_to_wstring(shareUserInfos[0].loginName()))
				{
					vecRemainUser.push_back(*itr);
					continue;
				}
				users.push_back(shareUserInfos[0]);
			}
		}
	}

	void ShareFrameV1::listGroups(const std::wstring& SearchKey, bool fromCache)
	{
		GroupNodeList groupNodes;
		PageParam pageParam;
		OrderParam orderParam;
		pageParam.orderList.push_back(orderParam);
		int64_t count = 0;

		userContext_->getShareResMgr()->listGroups(SD::Utility::String::wstring_to_utf8(SearchKey), "private", pageParam, count, groupNodes);

		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
		if(m_listUser == NULL) return;
		CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
		if(m_tip == NULL) return;
		if(fromCache&&(groupNodes.size()>0))
		{
			m_tip->SetVisible(true);
		}
		else
		{
			m_tip->SetVisible(false);
		}
		for (size_t i=0;i<groupNodes.size();i++)
		{
			AddGroupItem(groupNodes[i]);
		}
		m_listUser->SetVisible(true);
	}

	void ShareFrameV1::listDomainUsers(const std::wstring& SearchKey, bool fromCache)
	{
		ShareUserInfoList shareUserInfos;
		
		//list domain users
		ProxyMgr::getInstance(userContext_)->listDomainUsers(SD::Utility::String::wstring_to_utf8(SearchKey), shareUserInfos, fromCache);

		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
		if(m_listUser == NULL) return;
		CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
		if(m_tip == NULL) return;
		if(fromCache&&(shareUserInfos.size()>0))
		{
			m_tip->SetVisible(true);
		}
		else
		{
			m_tip->SetVisible(false);
		}

		for (size_t i=0;i<shareUserInfos.size();i++)
		{
			AddUserItem(shareUserInfos[i]);
		}
		m_listUser->SetVisible(true);
	}

	void ShareFrameV1::AddGroupItem(GroupNode& node)
	{
		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
		if (NULL == m_listUser) return;
		CDialogBuilder builder;
		TeamSpaceManageListUserContainerElement* item = static_cast<TeamSpaceManageListUserContainerElement*>(
			builder.Create(_T("teamSpaceManageListUserItem.xml"), 
			L"", 
			DialogBuilderCallbackImpl::getInstance(), 
			&m_PaintManager, 
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
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"TeamSpaceManage_itemUser_user"));
		if (name != NULL)
		{
			name->SetText(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
			name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
		}
		// email

		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"TeamSpaceManage_itemUser_dept"));
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

	void ShareFrameV1::AddUserItem(ShareUserInfo& node)
	{
		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));

		if (NULL == m_listUser) return;
		CDialogBuilder builder;
		TeamSpaceManageListUserContainerElement* item = static_cast<TeamSpaceManageListUserContainerElement*>(
			builder.Create(_T("teamSpaceManageListUserItem.xml"), 
			L"", 
			DialogBuilderCallbackImpl::getInstance(), 
			&m_PaintManager, 
			NULL));
		if (item == NULL) return;

		item->nodeData.basic = node;
		item->nodeData.userContext = userContext_;		

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"TeamSpaceManage_itemUser_user"));
		if (name != NULL)
		{
			name->SetText(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
			name->SetToolTip(SD::Utility::String::utf8_to_wstring(node.name()).c_str());
		}
		// email
		CLabelUI* email = static_cast<CLabelUI*>(item->FindSubControl(L"TeamSpaceManage_itemUser_email"));
		if (email != NULL)
		{
			email->SetText(SD::Utility::String::utf8_to_wstring(node.email()).c_str());
			email->SetToolTip(SD::Utility::String::utf8_to_wstring(node.email()).c_str());
		}
		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"TeamSpaceManage_itemUser_dept"));
		if (dept != NULL)
		{
			dept->SetText(SD::Utility::String::utf8_to_wstring(node.department()).c_str());
			dept->SetToolTip(SD::Utility::String::utf8_to_wstring(node.department()).c_str());
		}

		m_listUser->Add(item);
	}

	void ShareFrameV1::addClick(TNotifyUI& msg)
	{
		usersReturndown(msg);
		return;
	}

	void ShareFrameV1::menuClick(TNotifyUI& msg)
	{
		return;
	}
	void ShareFrameV1::messageSetfocus(TNotifyUI& msg)
	{
		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if (userstext != NULL)
		{
			std::wstring keyWord = userstext->GetText();
			if(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY) == keyWord)
			{
				userstext->SetText(L"");
				userstext->SetTextColor(0x0);
				userstext->SetAttribute(L"maxchar", L"500");
			}
		}
	}

	void ShareFrameV1::messageKillfocus(TNotifyUI& msg)
	{
		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if (userstext != NULL)
		{
			if(userstext->GetText().IsEmpty())
			{
				userstext->SetText(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY).c_str());
				userstext->SetTextColor(0x99999999);
				m_isInvite = false;
			}
			else
			{
				m_isInvite = true;
			}
		}
	}

	void ShareFrameV1::messageReturnDown(TNotifyUI& msg)
	{
		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if (userstext != NULL)
		{
			CHARRANGE cr;
			userstext->GetSel(cr);
			LONG curLength= cr.cpMax;
			std::wstring textstring = userstext->GetText().GetData();
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
			userstext->SetText(inputstring.c_str());
			userstext->SetSel(cr.cpMin+1,cr.cpMax+1);
		}
	}

	int32_t ShareFrameV1::shareLinkControl()
	{
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKLISTVIEW));
		if (NULL == pControl)
		{
			return RT_INVALID_PARAM;
		}
		if(pControl->GetCurSel() == 2)
		{
			CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODE));
			if (NULL == codeEdit)
			{
				return RT_INVALID_PARAM;
			}
			
			//get datetime
			CCheckBoxUI* pDateSelect = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_DATESELECT));
			CDateTimeUI* dateTimeFirst = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKFIRSTDATETIME));
			CDateTimeUI* dateTimesSecond = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKSECONDDATETIME));
			if (NULL == dateTimeFirst || NULL == dateTimesSecond || NULL == pDateSelect)
			{
				return RT_INVALID_PARAM;
			}
			std::wstring tmpFirstStr = dateTimeFirst->GetText();
			std::wstring tmpSecondStr = dateTimesSecond->GetText();

			int64_t timeEffctive = -1;
			int64_t timeExpire = -1;
			if(!tmpFirstStr.empty())
			{
				timeEffctive = sysTimetoInt64Time(dateTimeFirst->GetTime());
			}
			if(!tmpSecondStr.empty())
			{
				timeExpire = sysTimetoInt64Time(dateTimesSecond->GetTime());
			}
		
			if(tmpFirstStr.empty() && pDateSelect->IsSelected())
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_SETTIMEERROR_C_KEY);
				return RT_ERROR;
			}
			if((timeEffctive > timeExpire) && !tmpSecondStr.empty() && pDateSelect->IsSelected())
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_SETTIMEERROR_B_KEY);
				return RT_ERROR;
			}
			
			if(!pDateSelect->IsSelected())
			{
				timeEffctive = timeExpire = -1;
			}

			//get code
			CCheckBoxUI* codeSelect = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_CODESELECT));
			if (NULL == codeSelect)
			{
				return RT_INVALID_PARAM;
			}
			std::wstring tmpCode = codeEdit->GetText();
			if(!codeSelect->GetCheck())
			{
				tmpCode = L"";
			}

			if((tmpCode.empty() || tmpCode == iniLanguageHelper.GetCommonString(COMMENT_INPUTCODE_KEY)) && codeSelect->GetCheck())
			{
				m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_CODENOTNULL_KEY);
				return RT_ERROR;
			}

			if(!m_serverSysConfig.complexCode())
			{
				std::string tmpString = SD::Utility::String::wstring_to_utf8(tmpCode);
				for(size_t i = 0; i < tmpString.size(); ++i)
				{
					if((tmpString[i] >= '0'&& tmpString[i] <= '9') 
						|| (tmpString[i] >= 'a' && tmpString[i] <= 'z')
						|| (tmpString[i] >= 'A' && tmpString[i] <= 'Z'))
					{
						continue;
					}
					m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_SETERRORTEXT_KEY);
					return RT_ERROR;
				}
			}

			ShareLinkNode shareLinkNode;
			ShareLinkNodeEx shareLinkNodeEx;
			shareLinkNodeEx.plainAccessCode(SD::Utility::String::wstring_to_utf8(tmpCode));
			shareLinkNodeEx.effectiveAt(timeEffctive);
			shareLinkNodeEx.expireAt(timeExpire);

			userContext_->getShareResMgr()->modifyShareLink(nodeData_.basic.id, shareLinkNodeEx, shareLinkNode);
		}
		return RT_OK;
	}
	void ShareFrameV1::inviteClick(TNotifyUI& msg)
	{
		int32_t iRet = RT_OK;

		//TODO 处理鼠标状态为等待
		m_editSharedUsers->SetEnabled(FALSE);
		m_editSharedMessage ->SetEnabled(FALSE);
		//m_btnSharedInvite->SetEnabled(FALSE);
		m_btnSharedCancel -> SetEnabled(FALSE);
		
		if(m_inviteUsersList.empty() && m_inviteGroupList.empty())
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_NOTSETVALIDUSER_KEY);
			m_editSharedUsers->SetEnabled(true);
			m_editSharedMessage ->SetEnabled(true);
			//m_btnSharedInvite->SetEnabled(FALSE);
			m_btnSharedCancel -> SetEnabled(true);
			return;
		}
		ShareNodeExList shareNodeExs;
		for (ShareUserInfoList::iterator it = m_inviteUsersList.begin(); it != m_inviteUsersList.end(); ++it)
		{
			ShareNodeEx shareNodeEx;
			shareNodeEx.sharedUserId(it->id());
			shareNodeEx.sharedUserType("user");
			shareNodeEx.roleName(it->roleName());
			shareNodeEx.sharedEmail(it->email());
			shareNodeEx.loginName(it->loginName());
			shareNodeExs.push_back(shareNodeEx);
		}

		for (UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
		{
			ShareNodeEx shareNodeEx;
			shareNodeEx.sharedUserId(it->id);
			shareNodeEx.sharedUserType("group");
			shareNodeEx.roleName(SD::Utility::String::wstring_to_utf8(it->roleName));
			//shareNodeEx.sharedEmail(it->email());
			//shareNodeEx.loginName();
			shareNodeExs.push_back(shareNodeEx);
		}

		CRichEditUI* userstext = static_cast<CRichEditUI*>(m_PaintManager.FindControl(SHAREFRAME_MESSAGE));
		if(userstext == NULL)
		{
			return;
		}
		std::wstring emailMsg = L"";
		if(m_isInvite)
		{
			emailMsg = userstext->GetText();
			if(iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONB_KEY) ==  emailMsg)
			{
				emailMsg = L"";
			}
		}
		iRet = userContext_->getShareResMgr()->setShare(nodeData_.basic.id, shareNodeExs, 
			nodeData_.basic.type, nodeData_.basic.name, SD::Utility::String::wstring_to_utf8(emailMsg));
		if (iRet == 0)
		{
			Close();
		}
		else
		{
			m_editSharedUsers->SetEnabled(TRUE);
			m_editSharedMessage ->SetEnabled(TRUE);
			//m_btnSharedInvite->SetEnabled(TRUE);
			m_btnSharedCancel -> SetEnabled(TRUE);
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_SHAREFAILED_KEY);
		}
		m_inviteGroupList.clear();
		m_inviteUsersList.clear();
		return;
	}

	void ShareFrameV1::hiddenListItemClick(TNotifyUI& msg)
	{
		return;
	}

	void ShareFrameV1::itemDBClick(TNotifyUI& msg)
	{
		int32_t ret = RT_OK;
		TeamSpaceManageListUserContainerElement* item = static_cast<TeamSpaceManageListUserContainerElement*>(msg.pSender);
		if (item == NULL) return;

		UIGroupNode groupNode = item->nodeData.groupNode;
		ShareUserInfo shareUserInfo = item->nodeData.basic;

		if(!shareUserInfo.loginName().empty())
		{
			if(userContext_->getUserInfoMgr()->getUserName() == SD::Utility::String::utf8_to_wstring(shareUserInfo.loginName()))
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_NOTSHARETOMYSELF_KEY);
				return;
			}
			ret = addSharedUserList(shareUserInfo, "user");
			if(ret == RT_OK)
			{
				//add the node to the invite users list
				m_inviteUsersList.push_back(shareUserInfo);
			}
			CEditUI* userstext = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_USERS));
			if(userstext == NULL) return;
			userstext->SetText(_T(""));
		}
		else if(groupNode.id != NULL)
		{
			shareUserInfo.id(groupNode.id);
			shareUserInfo.name(SD::Utility::String::wstring_to_utf8(groupNode.name));
			shareUserInfo.loginName();
			shareUserInfo.department(SD::Utility::String::wstring_to_utf8(groupNode.description));
			shareUserInfo.roleName(SD::Utility::String::wstring_to_utf8(groupNode.roleName));

			ret = addSharedUserList(shareUserInfo, "group");
			if(ret == RT_OK)
			{
				//add the node to the invite users list
				m_inviteGroupList.push_back(groupNode);
			}
			CEditUI* userstext = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_USERS));
			if(userstext == NULL) return;
			userstext->SetText(_T(""));
		}

		CListUI* m_listUser = static_cast<CListUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserView")));
		if(m_listUser == NULL) return;
		CLabelUI* m_tip = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("shareFrame_listUserTip")));
		if(m_tip == NULL) return;
		m_listUser->RemoveAll();
		m_listUser->SetVisible(false);
		m_tip->SetVisible(false);
	}

	void ShareFrameV1::ShowSharedFrame(int32_t type)
	{
		TNotifyUI msg;
		Create(paintManager_.GetPaintWindow(), iniLanguageHelper.GetWindowName(WINDOWNAME_SHARE_KEY).c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		CTabLayoutUI* pLinkTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINK));
		CTabLayoutUI* pShareTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARE));
		if (NULL == pLinkTab)
		{
			return;
		}
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		if(type == 1)
		{
			pLinkTab->SetAttribute(L"enabled", L"true");
			pLinkTab->SetAttribute(L"selected", L"true");
			pControl->SelectItem(1);
			showButton(1);
		}
		else if(type == 0)
		{
			pShareTab->SetAttribute(L"enabled", L"true");
			pShareTab->SetAttribute(L"selected", L"true");
			pControl->SelectItem(0);
			showButton(0);
		}
		else if(type == 2)
		{
			pLinkTab->SetAttribute(L"enabled", L"true");
			pLinkTab->SetAttribute(L"selected", L"true");
			pControl->SelectItem(1);
			showButton(1);
			CTabLayoutUI* pShareTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARE));
			if (NULL == pShareTab)
			{
				return;
			}
			pShareTab->SetAttribute(L"enabled", L"false");
		}
						
		CenterWindow();
		ShowModal();
	}

	bool ShareFrameV1::isExistsharedUsers(CListUI* sharedUsers, ShareUserInfo& shareUserInfo, std::string type)
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

	void ShareFrameV1::addInviteUsersList(ShareUserInfo& shareUserInfo)
	{

	}

	int32_t ShareFrameV1::addSharedUserList(ShareUserInfo& shareUserInfo, std::string type)
	{
		if(type == "user")
		{
			ProxyMgr::getInstance(userContext_)->addUser(shareUserInfo);
		}
		
		CDialogBuilder builder;
		ShareFrameListContainerElement* node = static_cast<ShareFrameListContainerElement*>
			(builder.Create(ControlNames::SKIN_XML_SHARE_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
		if(NULL == node)
		{
			return RT_INVALID_PARAM;
		}

		CListUI* sharedUserList = static_cast<CListUI*>(m_PaintManager.FindControl(SHAREFRAME_SHAREUSERSLIST));
		if(NULL == sharedUserList)
		{
			return RT_INVALID_PARAM;
		}

		if(isExistsharedUsers(sharedUserList, shareUserInfo, type))
		{
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_HASSHARED_KEY);
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
		//node->nodeData.shareNode.inodeId(it->inodeId());
		node->nodeData.shareNode.name(SD::Utility::String::wstring_to_utf8(nodeData_.basic.name));
		node->nodeData.shareNode.modifiedAt(nodeData_.basic.mtime);
		//node->nodeData.shareNode.modifiedBy(pFileListItem->nodeData.basic.);
		//node->nodeData.shareNode.roleName(it->roleName());
		//node->nodeData.shareNode.status(pFileListItem->nodeData.basic.size);
		node->nodeData.shareNode.size(nodeData_.basic.size);
		node->nodeData.shareNode.roleName(shareUserInfo.roleName());

		// initial UI
		CLabelUI* icon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_ITEMICON));
		if (NULL != icon)
		{
			std::wstring str_photoPath;
			if(node->nodeData.shareNode.sharedUserType() == "user")
			{
				str_photoPath = GetInstallPath() + L"skin\\img\\" + L"teamSpaceManage.png";
			}
			else if(node->nodeData.shareNode.sharedUserType() == "group")
			{
				str_photoPath += GetInstallPath() + L"skin\\img\\" + L"icons-orange.png";
			}
			icon->SetBkImage(str_photoPath.c_str());
		}

		CLabelUI* name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, SHAREFRAME_USERNAME));
		if (NULL != name)
		{
			std::wstring tmpStr = SD::Utility::String::utf8_to_wstring(node->nodeData.shareNode.sharedUserName());
			name->SetToolTip(tmpStr.c_str());
			name->SetShowHtml(true);
			name->SetText(tmpStr.c_str());
		}

		if (!sharedUserList->AddAt(node, 0))
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
			return RT_ERROR;
		}
				
		skipChange_ = true;
		return RT_OK;
	}

	void ShareFrameV1::publicLinkSelectchanged(TNotifyUI& msg)
	{
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKLISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		
		pControl->SelectItem(1);
		showButton(1);
	}

	void ShareFrameV1::privateLinkSelectchanged(TNotifyUI& msg)
	{
		CTabLayoutUI* pLinkTab = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINK));
		if (NULL == pLinkTab)
		{
			return;
		}
		pLinkTab->SetAttribute(L"enabled", L"true");
		pLinkTab->SetAttribute(L"selected", L"true");

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKLISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		pControl->SelectItem(2);
		showButton(2);
		
		//add the link url
		CEditUI* linkUrlEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATELINKURL));
		if (NULL == linkUrlEdit)
		{
			return;
		}
		linkUrlEdit->SetEnabled(false);

		ShareLinkNode shareLinkNode;
		userContext_->getShareResMgr()->getShareLink(nodeData_.basic.id, shareLinkNode);
		std::wstring linkUrl = SD::Utility::String::utf8_to_wstring(shareLinkNode.url());
		linkUrlEdit->SetText(linkUrl.c_str());

		//code check
		CCheckBoxUI* pCodeSelect = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_CODESELECT));
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODE));
		CButtonUI* codeRefreshBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODEREFRESH));
		if(NULL == pCodeSelect || NULL == codeEdit || NULL == codeRefreshBtn)
		{
			return;
		}
		if(!shareLinkNode.plainAccessCode().empty())
		{
			pCodeSelect->SetCheck(true);
			std::wstring accessCode = SD::Utility::String::utf8_to_wstring(shareLinkNode.plainAccessCode());
			codeEdit->SetText(accessCode.c_str());
			if(m_serverSysConfig.complexCode())
			{
				codeRefreshBtn->SetVisible(true);
				codeEdit->SetReadOnly(true);
			}
			else
			{
				codeRefreshBtn->SetVisible(false);
				codeEdit->SetReadOnly(false);
			}

		}

		//datetime check
		if(shareLinkNode.effectiveAt() || shareLinkNode.expireAt())
		{
			CCheckBoxUI* pDateSelect = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_DATESELECT));
			CDateTimeUI* datetimeFirst = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKFIRSTDATETIME));
			CDateTimeUI* datetimeSecond = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKSECONDDATETIME));
			CTextUI* datetimeText = static_cast<CTextUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKTEXTDATETIP));
			if(NULL == pDateSelect || NULL == datetimeFirst || NULL == datetimeSecond || NULL == datetimeText)
			{
				return;
			}
			pDateSelect->SetCheck(true);
			datetimeFirst->SetVisible(true);
			datetimeText->SetVisible(true);
			datetimeSecond->SetVisible(true);

			SYSTEMTIME stime;
			stime = int64TimeToSysTime(shareLinkNode.effectiveAt());
			datetimeFirst->SetTime(&stime);

			if(!shareLinkNode.expireAt())
			{
				datetimeSecond->SetText(L"");
			}
			else
			{
				stime = int64TimeToSysTime(shareLinkNode.expireAt());
				datetimeSecond->SetTime(&stime);
			}
		}

		return;
	}

	void ShareFrameV1::showButton(int32_t type)
	{
		CButtonUI* inviteBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_INVITE));
		CButtonUI* cancelBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_CANCEL));
		CButtonUI* compeleteBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_COMPELETE));
		CButtonUI* linkCancelBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKCANCEL));	
		//CButtonUI* saveBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_SAVE));
		//CButtonUI* backBTN = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_BACK));
		if(inviteBTN == NULL || cancelBTN == NULL || compeleteBTN == NULL || linkCancelBTN == NULL)
		{
			return;
		}		
		//if(saveBTN == NULL || backBTN == NULL)
		//{
		//	return;
		//}
		if(type == 0)
		{
			inviteBTN->SetVisible(true);
			cancelBTN->SetVisible(true);
			compeleteBTN->SetVisible(false);
			linkCancelBTN->SetVisible(false);
		}
		else if(type == 1)
		{
			inviteBTN->SetVisible(false);
			cancelBTN->SetVisible(false);
			compeleteBTN->SetVisible(true);
			linkCancelBTN->SetVisible(true);
		}
		else if(type == 2)
		{
			inviteBTN->SetVisible(false);
			cancelBTN->SetVisible(false);
			compeleteBTN->SetVisible(true);
			linkCancelBTN->SetVisible(true);
		}
		return;
	}

	void ShareFrameV1::codeSelectchanged(TNotifyUI& msg)
	{
		CCheckBoxUI* pControl = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_CODESELECT));
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODE));
		CButtonUI* codeRefreshBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODEREFRESH));
		if(NULL == pControl || NULL == codeEdit || NULL == codeRefreshBtn)
		{
			return;
		}
		if(pControl->IsSelected())
		{
			ShareLinkNode shareLinkNode;
			userContext_->getShareResMgr()->getShareLink(nodeData_.basic.id, shareLinkNode);
			codeEdit->SetVisible(true);
			codeEdit->SetText(SD::Utility::String::utf8_to_wstring(shareLinkNode.plainAccessCode()).c_str());
			if(m_serverSysConfig.complexCode())
			{
				codeEdit->SetReadOnly(true);
				codeRefreshBtn->SetVisible(true);
				refreshClick(msg);
			}
			else
			{
				codeEdit->SetReadOnly(false);
				codeRefreshBtn->SetVisible(false);
			}
		}
		else
		{
			codeEdit->SetVisible(false);
			codeEdit->SetText(L"");
			codeRefreshBtn->SetVisible(false);
		}		
	}

	void ShareFrameV1::datatimeSelectchanged(TNotifyUI& msg)
	{
		CCheckBoxUI* pControl = static_cast<CCheckBoxUI*>(m_PaintManager.FindControl(SHAREFRAME_DATESELECT));
		CDateTimeUI* datetimeFirst = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKFIRSTDATETIME));
		CDateTimeUI* datetimeSecond = static_cast<CDateTimeUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKSECONDDATETIME));
		CTextUI* datetimeText = static_cast<CTextUI*>(m_PaintManager.FindControl(SHAREFRAME_SHARELINKTEXTDATETIP));
		if(NULL == pControl || NULL == datetimeFirst || NULL == datetimeSecond || NULL == datetimeText)
		{
			return;
		}
		if(pControl->IsSelected())
		{
			datetimeFirst->SetVisible(true);
			datetimeFirst->SetText(L"");
			datetimeText->SetVisible(true);
			datetimeSecond->SetVisible(true);
			datetimeSecond->SetText(L"");
		}
		else
		{
			datetimeFirst->SetVisible(false);
			datetimeText->SetVisible(false);
			datetimeSecond->SetVisible(false);
		}		
	}

	void ShareFrameV1::compeleteClick(TNotifyUI& msg)
	{
		int ret = RT_OK;
		ret = shareLinkControl();
		if(ret != RT_OK)
		{
			return;
		}
		Close();
		return;
	}

	void ShareFrameV1::linkCancelClick(TNotifyUI& msg)
	{
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(m_PaintManager.FindControl(SHAREFRAME_LINKLISTVIEW));
		if (NULL == pControl)
		{
			return;
		}
		if(pControl->GetCurSel() == 0)
		{
			Close();
		}
		else
		{
			m_noticeFrame_->Run(Choose,Ask,L"",MSG_SHARE_ALLCANCELASK_KEY, Modal);
			if(m_noticeFrame_->IsClickOk())
			{
				int32_t ret = userContext_->getShareResMgr()->delShareLink(nodeData_.basic.id);
				if(ret != RT_OK)
				{
					return;
				}
				Close();
			}
		}
		return;
	}

	void ShareFrameV1::publicCopyUrlClick(TNotifyUI& msg)
	{
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PUBLICLINKURL));
		if(codeEdit == NULL)
		{
			return;
		}
		std::wstring source = codeEdit->GetText();
		std::string copyUrl = SD::Utility::String::wstring_to_string(source);

		if(OpenClipboard(NULL)) 
		{ 
			HGLOBAL clipbuffer; 
			EmptyClipboard(); 
			clipbuffer = GlobalAlloc(GMEM_DDESHARE, copyUrl.size()+1);
			if(NULL==clipbuffer) return;
			memcpy_s(clipbuffer, copyUrl.size()+1, copyUrl.c_str(), copyUrl.size());
			GlobalUnlock(clipbuffer); 
			SetClipboardData(CF_TEXT,clipbuffer); 
			CloseClipboard();
			GlobalFree(clipbuffer);
		}
		m_noticeFrame_->Run(Confirm,Right,L"",MSG_SHARE_COPYSUCCESS_KEY);
		return;
	}

	void ShareFrameV1::praviteCopyUrlClick(TNotifyUI& msg)
	{
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATELINKURL));
		if(codeEdit == NULL)
		{
			return;
		}
		std::wstring source = codeEdit->GetText();
		if(source.empty())
		{
			return;
		}
		std::string copyUrl = SD::Utility::String::wstring_to_string(source);

		if(OpenClipboard(NULL)) 
		{ 
			HGLOBAL clipbuffer;
			EmptyClipboard(); 
			clipbuffer = GlobalAlloc(GMEM_DDESHARE, copyUrl.size()+1);
			if(NULL==clipbuffer) return;
			memcpy_s(clipbuffer, copyUrl.size()+1, copyUrl.c_str(), copyUrl.size());
			GlobalUnlock(clipbuffer); 
			SetClipboardData(CF_TEXT,clipbuffer); 
			CloseClipboard();
			GlobalFree(clipbuffer);
		}
		m_noticeFrame_->Run(Confirm,Right,L"",MSG_SHARE_COPYSUCCESS_KEY);
		return;
	}

	void ShareFrameV1::deleteShareUserList(TNotifyUI& msg)
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
					if(it == m_inviteUsersList.end())
					{
						break;
					}
				}
				for(UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
				{
					if(it->id != shareInfo.sharedUserId())
					{
						continue;
					}
					it = m_inviteGroupList.erase(it);
					if(it == m_inviteGroupList.end())
					{
						break;
					}
				}
			}
		}
		else if (ret != RT_OK)
		{
			m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_DELETEFAILED_KEY);
			return;
		}
		CListUI* sharedUserList = static_cast<CListUI*>(m_PaintManager.FindControl(SHAREFRAME_SHAREUSERSLIST));
		if(NULL == sharedUserList)
		{
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
			(void)addSharedUserList(*it, "user");
		}
		for(UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
		{
			ShareUserInfo shareUserInfo;
			shareUserInfo.id(it->id);
			shareUserInfo.name(SD::Utility::String::wstring_to_utf8(it->name));
			shareUserInfo.loginName();
			shareUserInfo.department(SD::Utility::String::wstring_to_utf8(it->description));
			(void)addSharedUserList(shareUserInfo, "group");
		}
	}

	void ShareFrameV1::refreshClick(TNotifyUI& msg)
	{
		CEditUI* codeEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODE));
		CButtonUI* codeRefreshBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(SHAREFRAME_PRIVATECODEREFRESH));
		if(NULL == codeEdit || NULL == codeRefreshBtn)
		{
			return;
		}
		std::wstring complexCode = SD::Utility::String::create_random_string();
		codeEdit->SetText(complexCode.c_str());
		return;
	}

	void ShareFrameV1::saveClick(TNotifyUI& msg)
	{
		shareLinkControl();
		publicLinkSelectchanged(msg);
	}

	void ShareFrameV1::backClick(TNotifyUI& msg)
	{
		m_noticeFrame_->Run(Choose,Ask,L"",MSG_SHARE_CANCELASK_KEY, Modal);
		if(m_noticeFrame_->IsClickOk())
		{
			userContext_->getShareResMgr()->delShareLink(nodeData_.basic.id);
			publicLinkSelectchanged(msg);
		}
	}

	std::vector<std::wstring> ShareFrameV1::getBatchUsers(std::wstring searchKey)
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
	int64_t ShareFrameV1::sysTimetoInt64Time(SYSTEMTIME& sysTime)
	{
		FILETIME lpFileTime;
		lpFileTime.dwLowDateTime = 0;
		lpFileTime.dwHighDateTime = 0;
		ULARGE_INTEGER ularge;
		TIME_ZONE_INFORMATION timeZone;
		if (TIME_ZONE_ID_INVALID == GetTimeZoneInformation(&timeZone))
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_GETTIMEZONEFAILED_KEY);
			return 0;
		}
		SystemTimeToFileTime(&sysTime, &lpFileTime);
		ularge.LowPart = lpFileTime.dwLowDateTime;
		ularge.HighPart = lpFileTime.dwHighDateTime;
		return ularge.QuadPart + ((int64_t)timeZone.Bias)*600000000;
	}

	SYSTEMTIME ShareFrameV1::int64TimeToSysTime(int64_t time)
	{
		SYSTEMTIME stime;
		TIME_ZONE_INFORMATION timeZone;
		(void)memset_s(&stime, sizeof(SYSTEMTIME), 0, sizeof(SYSTEMTIME));
		if (TIME_ZONE_ID_INVALID == GetTimeZoneInformation(&timeZone))
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_GETTIMEZONEFAILED_KEY);
			return stime;
		}
		time -= ((int64_t)timeZone.Bias)*600000000;
		FILETIME ftime;
		ftime.dwLowDateTime = (DWORD)time;
		ftime.dwHighDateTime = (DWORD)(time>>32);	
		if (!FileTimeToSystemTime(&ftime, &stime))
		{
			return stime;
		}
		return stime;
	}
}