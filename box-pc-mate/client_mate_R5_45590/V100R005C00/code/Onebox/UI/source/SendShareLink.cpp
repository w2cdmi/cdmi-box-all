#include "stdafxOnebox.h"
#include "SendShareLink.h"
#include "DialogBuilderCallbackImpl.h"
#include "Utility.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "ErrorConfMgr.h"
#include "ProxyMgr.h"
#include "UserContextMgr.h"
#include "CommonLoadingFrame.h"
#include "UserContext.h"
#include "ShareResMgr.h"
#include "GroupResMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SendShareLink")
#endif

namespace Onebox
{
	SendShareLinkDialog::SendShareLinkDialog(UserContext* context,ShareLinkNode& _linkNode,UIFileNode& _fileNode)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pSendBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_linkNode(_linkNode)
		,m_fileNode(_fileNode)
	{
		m_noticeFrame_ = NULL;
		skipChange_ = false;
		m_pUserEdit = NULL;
		m_pListUser = NULL;
		m_pMessage = NULL;
	}

	SendShareLinkDialog::~SendShareLinkDialog()
	{
		delete m_noticeFrame_;
		m_noticeFrame_ = NULL;
	}

	CDuiString SendShareLinkDialog::GetSkinFolder()
	{ 
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString SendShareLinkDialog::GetSkinFile()
	{ 
		return L"SendShareLinkDialog.xml";
	}

	LPCTSTR SendShareLinkDialog::GetWindowClassName(void) const
	{ 
		return L"SendShareLinkDialog";
	}

	CControlUI* SendShareLinkDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool SendShareLinkDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT SendShareLinkDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient={0,0,0,0};
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_PaintManager.GetCaptionRect();
		if( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right \
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) {
				CControlUI* pControl = static_cast<CControlUI*>(m_PaintManager.FindControl(pt));
				if( pControl && _tcsicmp(pControl->GetClass(), _T("ButtonUI")) != 0 && 
					_tcsicmp(pControl->GetClass(), _T("OptionUI")) != 0 &&
					_tcsicmp(pControl->GetClass(), _T("TextUI")) != 0 &&
					_tcsicmp(pControl->GetClass(), _T("ButtonUI")) != 0 ) 
					return HTCAPTION;
		}

		return HTCLIENT;
	}


	void SendShareLinkDialog::Notify(TNotifyUI& msg)
	{
		std::wstring name = msg.pSender->GetName().GetData();
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn)
			{
				Close();
				return; 
			}
			else if (msg.pSender == m_pSendBtn)
			{
				sendMail(msg);
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
				(void)addSharedTileUserList(users,groups,true);
			}
		}
		else if(msg.sType == _T("setfocus"))
		{
			if (msg.pSender == m_pUserEdit)
			{
				usersSetfocus(msg);
			}
			else if (msg.pSender == m_pMessage)
			{
				messageSetfocus(msg);
			}
		}
		else if(msg.sType == _T("killfocus"))
		{
			if (msg.pSender == m_pUserEdit)
			{
				usersKillfocus(msg);
			}
			else if (msg.pSender == m_pMessage)
			{
				messageKillfocus(msg);
			}
		}
		else if(msg.sType == _T("return"))
		{
			if (msg.pSender == m_pUserEdit)
			{
				usersReturndown(msg);
			}
			else if (msg.pSender == m_pMessage)
			{
				messageReturnDown(msg);
			}
		}
		else if(msg.sType == _T("textchanged"))
		{
			if (msg.pSender == m_pUserEdit)
			{
				usersTextchanged(msg);
			}
		}
		else  if(msg.sType == _T("itemclick"))
		{
			if (name == L"sendShareLink_listItem")
			{
				itemClick(msg);
			}
		}
	}
	void SendShareLinkDialog::InitWindow()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"SendShareLink_close_btn"));
		m_pSendBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"SendShareLink_send"));
		m_pCancelBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"SendShareLink_cancel"));
		CEditUI* pUrlEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(L"SendShareLink_url"));
		CLabelUI* pUrlDetail = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"SendShareLink_urlDetail"));
		m_pUserEdit = static_cast<CEditUI*>(m_PaintManager.FindControl(L"SendShareLink_users"));
		m_pListUser = static_cast<CListUI*>(m_PaintManager.FindControl(L"SendShareLink_listUserView"));
		m_pMessage = static_cast<CRichEditUI*>(m_PaintManager.FindControl(L"SendShareLink_messsage"));
		if (NULL == pUrlEdit || NULL == pUrlDetail || NULL == m_pUserEdit || NULL == m_pListUser || NULL == m_pMessage || NULL == m_pSendBtn) return;
		pUrlEdit->SetText(SD::Utility::String::string_to_wstring(m_linkNode.url()).c_str());

		std::wstring tmpStr = L"";
		std::wstring strTip = L"";
		formatDes(tmpStr,strTip);
		if (tmpStr.empty() || strTip.empty())return;
		pUrlDetail->SetText(tmpStr.c_str());
		pUrlDetail->SetToolTip(strTip.c_str());
		m_noticeFrame_ = new NoticeFrameMgr(m_PaintManager.GetPaintWindow());

		EmailInfoNode emailInfoNode;
		userContext_->getShareResMgr()->getMailInfo(m_fileNode.basic.id, "link", emailInfoNode);
		if(!emailInfoNode.message.empty())
		{
			m_pMessage->SetText(SD::Utility::String::utf8_to_wstring(emailInfoNode.message).c_str());
		}
		else
		{
			m_pMessage->SetText(iniLanguageHelper.GetCommonString(SENDLINK_ADD_MESSAGE).c_str());
			m_pMessage->SetTextColor(0x99999999);
		}
	}

	void SendShareLinkDialog::formatDes(std::wstring& tmpStr,std::wstring& strTip)
	{
		std::wstring code = SD::Utility::String::utf8_to_wstring(m_linkNode.plainAccessCode());
		std::wstring datetimeFirst = SD::Utility::DateTime::getTime(m_linkNode.effectiveAt(), SD::Utility::UtcType::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage());
		std::wstring datetimeSecond = SD::Utility::DateTime::getTime(m_linkNode.expireAt(), SD::Utility::UtcType::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage());

		if (m_linkNode.role() == "viewer")
		{
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}
		else if (m_linkNode.role() == "previewer")
		{
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}
		else if (m_linkNode.role() == "uploader")
		{
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L"{f 16}{c #000000}"
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L"{/c}{/f}";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT) 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT);
		}
		else
		{
			tmpStr = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L"{f 16}{c #000000}" 
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT)
				+ L"{/c}{/f}";

			strTip = iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTONE_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT)
				+ L" " + iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT);
		}

		if (m_linkNode.plainAccessCode().empty())
		{
			if (m_linkNode.accesCodeMode() == "mail")
			{
				tmpStr = tmpStr + L"   " +iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDYMATICCODE_TEXT)
					+ L"{/c}{/f}";

				strTip = strTip + L"   " +iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDYMATICCODE_TEXT);
			}
		}
		else
		{
			tmpStr = tmpStr + L"   " 
				+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT)
				+ L" {f 16}{c #000000}" 
				+ code
				+ L"{/c}{/f}";

			strTip = strTip + L"   " 
				+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTWO_TEXT)
				+ code;
		}

		if(m_linkNode.effectiveAt() != 0 || m_linkNode.expireAt() != 0)
		{
			if(m_linkNode.expireAt() == 0)
			{
				tmpStr = tmpStr + L"      " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ L"{/c}{/f}" + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP2_TEXT);

				strTip = strTip + L"      " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP2_TEXT);
			}
			else
			{
				tmpStr = tmpStr + L"      "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) 
					+ L"{/c}{/f}" + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ L"{f 16}{c #000000}" 
					+ datetimeSecond.substr(0, datetimeSecond.size()-3)
					+ L"{/c}{/f}";

				strTip = strTip + L"      "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTTHREE_TEXT) + L" " 
					+ datetimeFirst.substr(0, datetimeFirst.size()-3) + L" "
					+ iniLanguageHelper.GetCommonString(SHAREFRAME_SHARELINKTEXTDATETIP_TEXT) + L" " 
					+ datetimeSecond.substr(0, datetimeSecond.size()-3);
			}
		}
	}

	void SendShareLinkDialog::usersSetfocus(TNotifyUI& msgCListUI)
	{	  
		m_pUserEdit->SetEnabled();
		if (m_pUserEdit->GetText().IsEmpty() || iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_TEAMSPACE_KEY) == m_pUserEdit->GetText().GetData())
		{
			m_pUserEdit->SetText(L"");
		}
		if (m_addNodes.size() == 0)
		{
			m_pSendBtn->SetEnabled(false);
		}
	}

	void SendShareLinkDialog::usersTextchanged(TNotifyUI& msg)
	{
		if(skipChange_)
		{
			skipChange_ = false;
			return;
		}

		CEditUI *userEdit = static_cast<CEditUI*>(msg.pSender);
		if (NULL == userEdit) return;
		std::wstring keyWord = userEdit->GetText();	
		m_pListUser->SetVisible(false);
		m_pListUser->RemoveAll();
		if (std::wstring::npos != keyWord.find(L",") || std::wstring::npos != keyWord.find(L";"))
		{
			m_pListUser->SetVisible(false);
			return;
		}

		listDomainUsers(keyWord, ShareType_Link, true);
	}

	void SendShareLinkDialog::usersKillfocus(TNotifyUI& msg)
	{
		if(m_pUserEdit->GetText().IsEmpty())
		{
			m_pUserEdit->SetText(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_TEAMSPACE_KEY).c_str());
			m_pUserEdit->SetTextColor(0x99999999);
		}

		RECT rt = m_pListUser->GetPos();
		if (!PtInRect(&rt,msg.ptMouse))
		{
			m_pListUser->SetVisible(false);
		}
	}

	std::vector<std::wstring> SendShareLinkDialog::getBatchUsers(std::wstring searchKey)
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

	void SendShareLinkDialog::findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey, int curlSel)
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

				shareUserInfos[0].roleName(m_linkNode.role());

				users.push_back(shareUserInfos[0]);
			}
		}
	}

	void SendShareLinkDialog::findGroupsAndUser(const std::wstring& searchKey)
	{
		listGroups(searchKey, ShareType_Link);
		listDomainUsers(searchKey, ShareType_Link);
	}

	void SendShareLinkDialog::usersReturndown(TNotifyUI& msg)
	{
		std::wstring searchKey = m_pUserEdit->GetText();
		if(iniLanguageHelper.GetCommonString(COMMENT_INPUTUSERID_TEAMSPACE_KEY).c_str() == searchKey || L"" == searchKey )
		{
			m_noticeFrame_->Run(Confirm,Warning,L"",MSG_SHARE_NOTSETVALIDUSER_KEY,Modal);
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
				&SendShareLinkDialog::findGroupsAndUser, 
				this, 
				boost::ref(vecRemainUser), 
				boost::ref(users), 
				boost::ref(groups), 
				boost::cref(searchKey),
				0));
			UIGroupNodeList uiGroupList;
			for (GroupNodeList::iterator it = groups.begin(); it != groups.end(); ++it)
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
				uiGroupList.push_back(uiGroupNode);
			}
			(void)addSharedTileUserList(users,uiGroupList);

			m_pListUser->RemoveAll();
			m_pListUser->SetVisible(false);
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
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_BATCH_ADD_KEY,Modal);
			}
			m_pUserEdit->SetText(remainderUser.c_str());
			return;
		}
		else
		{
			m_pListUser->RemoveAll();
			//listGroups(searchKey, ShareType_Link);
			//listDomainUsers(searchKey, ShareType_Link);

			CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),m_hWnd, boost::bind(
				&SendShareLinkDialog::findGroupsAndUser, 
				this, 
				boost::cref(searchKey)));

			if(m_pListUser->GetCount() == 0)
			{
				m_noticeFrame_->Run(Confirm,Error,L"",MSG_SHARE_NOTFOUND_KEY,Modal);
				m_pListUser->SetVisible(false);
				return;
			}
			else if(m_pListUser->GetCount() == 1)
			{
				ShareFrameListContainerElement* pFileListItem = static_cast<ShareFrameListContainerElement*>(m_pListUser->GetItemAt(0));
				if(pFileListItem == NULL)
				{
					return;
				}
				msg.pSender = pFileListItem;
				itemClick(msg);
				return;
			}
			else
			{
				setUserListVisible();
			}
		}
	}

	void SendShareLinkDialog::listGroups(const std::wstring& SearchKey, int32_t type, bool fromCache)
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
	//	setUserListVisible();
	}

	void SendShareLinkDialog::AddGroupItem(GroupNode& node, int32_t type)
	{
		CDialogBuilder builder;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(
			builder.Create(_T("sendShareLinkListItem.xml"), 
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

		//item->nodeData = userContext_;		

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"sendShareLink_listItem_user"));
		if (name != NULL)
		{			 
			std::wstring nametext=L"{b}{f 13}"+SD::Utility::String::utf8_to_wstring(node.name())+L"{/f}{/b}";
			nametext+=L"{f 12}("+SD::Utility::String::utf8_to_wstring(node.name())+L")"+L"{/f}";
			name->SetText(nametext.c_str());
			name->SetShowHtml();
		}

		// email
		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"sendShareLink_listItem_dept"));
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
		m_pListUser->Add(item);
	}

	void SendShareLinkDialog::listDomainUsers(const std::wstring& SearchKey, int32_t type, bool fromCache)
	{
		ShareUserInfoList shareUserInfos;		
		//list domain users
		ProxyMgr::getInstance(userContext_)->listDomainUsers(SD::Utility::String::wstring_to_utf8(SearchKey), shareUserInfos, fromCache);

		for (size_t i=0;i<shareUserInfos.size();i++)
		{
			AddUserItem(shareUserInfos[i], type);
		}
		return;
	}

	void SendShareLinkDialog::AddUserItem(ShareUserInfo& node, int32_t type)
	{
		CDialogBuilder builder;
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(
			builder.Create(_T("sendShareLinkListItem.xml"), 
			L"", 
			DialogBuilderCallbackImpl::getInstance(), 
			&m_PaintManager, 
			NULL));
		if (item == NULL) return;

		item->nodeData.shareUserInfo = node;

		// name
		CLabelUI* name = static_cast<CLabelUI*>(item->FindSubControl(L"sendShareLink_listItem_user"));
		if (name != NULL)
		{			 
			std::wstring nametext=L"{b}{f 13}"+SD::Utility::String::utf8_to_wstring(node.name())+L"{/f}{/b}";
			nametext+=L"{f 12}("+SD::Utility::String::utf8_to_wstring(node.loginName())+L")"+L"{/f}";
			name->SetText(nametext.c_str());
			name->SetShowHtml();
		}

		// dept
		CLabelUI* dept = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(item, L"sendShareLink_listItem_dept"));
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
			if(depttext.empty()) depttext = L"-";
			dept->SetText(depttext.c_str());
			dept->SetToolTip(depttext.c_str());
		}
		m_pListUser->Add(item);
	}

	bool SendShareLinkDialog::isExistsharedTileUsers(ShareNode& node)
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

	void SendShareLinkDialog::fillShareNode(int64_t& shareUserId,std::string& shareUserName, std::string& shareUserLoginName,std::string& shareUserDepartment, std::string& roleName,ShareNode& rNode)
	{
		rNode.id(m_fileNode.basic.id);
		rNode.type(m_fileNode.basic.type);
		rNode.ownerId(userContext_->getUserInfoMgr()->getUserId());
		rNode.ownerName(SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName()));
		rNode.sharedUserId(shareUserId);
		rNode.sharedUserType("");
		rNode.sharedUserName(shareUserName);
		rNode.sharedUserLoginName(shareUserLoginName);
		rNode.sharedUserDescription(shareUserDepartment);
		rNode.name(SD::Utility::String::wstring_to_utf8(m_fileNode.basic.name));
		rNode.modifiedAt(m_fileNode.basic.mtime);
		rNode.size(m_fileNode.basic.size);
		rNode.roleName(roleName);
	}

	void SendShareLinkDialog::setUserListVisible()
	{
		CVerticalLayoutUI* pHorLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(L"SendShareLink_userArea"));
		if (NULL == pHorLayout) return;
		RECT rc = pHorLayout->GetPos();
		rc.top = rc.bottom;
		rc.bottom = rc.top + 115;
		m_pListUser->SetPos(rc);
		m_pListUser->SetVisible();
		ShareFrameListContainerElement* item = static_cast<ShareFrameListContainerElement*>(m_pListUser->GetItemAt(0));
		if (item == NULL) return;
		item->Select();
	}

	int32_t SendShareLinkDialog::addSharedTileUserList(ShareUserInfoList& shareUserInfos,UIGroupNodeList& groupList,bool bDelete)
	{	 
		CUserListControlUI* pContainer = static_cast<CUserListControlUI*>(m_PaintManager.FindControl(L"SendShareLink_list"));
		if(NULL == pContainer) return RT_ERROR;
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
			nameNodes.push_back(_node);
			m_addNodes.push_back(_node);
			m_inviteUsersList.push_back(*it);
		}

		for (UIGroupNodeList::iterator it = groupList.begin(); it != groupList.end(); ++it)
		{
			fillShareNode(it->id,SD::Utility::String::wstring_to_utf8(it->name),SD::Utility::String::wstring_to_utf8(it->name),SD::Utility::String::wstring_to_utf8(it->description),SD::Utility::String::wstring_to_utf8(it->roleName),_node);
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

		CVerticalLayoutUI* pHorLayout = static_cast<CVerticalLayoutUI*>(m_PaintManager.FindControl(L"SendShareLink_userArea"));
		if (NULL == pHorLayout) return RT_ERROR;
		bool bIsShow = false;
		if (0 != _height)
		{
			bIsShow = true;
		}
		int32_t horHeight = pHorLayout->GetFixedHeight();
		if (horHeight == _height + 31) return RT_OK;
		pHorLayout->SetFixedHeight(_height + 31);
		pContainer->SetVisible(bIsShow);
		RECT rc;
		::GetWindowRect(GetHWND(),&rc);	
		int width = rc.right - rc.left;
		int height = rc.bottom - rc.top + (bDelete == true ? -25 : _height);
		MoveWindow(*this,rc.left,rc.top,width,height,TRUE);
		::InvalidateRect(*this,&rc,true);
		m_pSendBtn->SetEnabled(bIsShow);
		return RT_OK;
	}

	void SendShareLinkDialog::itemClick(TNotifyUI& msg)
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
			ret = addSharedTileUserList(users,groups);
		}
		else if(groupNode.id != NULL)
		{
			//add the node to the invite group list
			ShareUserInfoList users;
			UIGroupNodeList groups;
			groups.push_back(groupNode);
			ret = addSharedTileUserList(users,groups);	
		}
		if (RT_OK == ret)
		{
			m_pUserEdit->SetText(L"");
		}
		m_pListUser->RemoveAll();
		m_pListUser->SetVisible(false);
	}

	void SendShareLinkDialog::messageSetfocus(TNotifyUI& msg)
	{
		std::wstring keyWord = m_pMessage->GetText();
		if(iniLanguageHelper.GetCommonString(SENDLINK_ADD_MESSAGE).c_str() != keyWord) return;
		m_pMessage->SetText(L"");
		m_pMessage->SetTextColor(0x0);
		m_pMessage->SetAttribute(L"maxchar", L"2000");
	}

	void SendShareLinkDialog::messageKillfocus(TNotifyUI& msg)
	{
		if(m_pMessage->GetText().IsEmpty())
		{
			m_pMessage->SetText(iniLanguageHelper.GetCommonString(SENDLINK_ADD_MESSAGE).c_str());
			m_pMessage->SetTextColor(0x99999999);
		}
	}

	void SendShareLinkDialog::messageReturnDown(TNotifyUI& msg)
	{
		CHARRANGE cr;
		m_pMessage->GetSel(cr);
		LONG curLength= cr.cpMax;
		std::wstring textstring = m_pMessage->GetText().GetData();
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
		m_pMessage->SetText(inputstring.c_str());
		m_pMessage->SetSel(cr.cpMin+1,cr.cpMax+1);
	}

	void SendShareLinkDialog::sendMail(TNotifyUI& msg)
	{
		int32_t ret = RT_OK;
		ShareLinkNode shareLinkNode;
		std::wstring accessMode = L"static";
		std::string url = m_linkNode.id();
		ret = userContext_->getShareResMgr()->getShareLink(m_fileNode.basic.id,url, shareLinkNode);
		if(ret != RT_OK) return;
		std::wstring tmpMessage = m_pMessage->GetText();
		if(tmpMessage == iniLanguageHelper.GetCommonString(SENDLINK_ADD_MESSAGE).c_str())
		{
			tmpMessage = L"";
		}
		EmailNode emailNode;
		emailNode.type = "link";
		emailNode.email_param.linkurl = shareLinkNode.url();
		emailNode.email_param.message = SD::Utility::String::wstring_to_utf8(tmpMessage);
		emailNode.email_param.sender = SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName());
		emailNode.email_param.plainaccesscode = shareLinkNode.plainAccessCode();
		emailNode.email_param.start = shareLinkNode.effectiveAt();
		emailNode.email_param.end = shareLinkNode.expireAt();
		emailNode.email_param.nodename = SD::Utility::String::wstring_to_utf8(m_fileNode.basic.name);
		for(ShareUserInfoList::iterator it = m_inviteUsersList.begin(); it != m_inviteUsersList.end(); ++it)
		{
			emailNode.mailto = it->email();
			ret = userContext_->getShareResMgr()->sendEmail(emailNode);
			if(ret == RT_OK)
			{
				EmailInfoNode emailInfoNode;
				emailInfoNode.sender = userContext_->getUserInfoMgr()->getUserId();
				emailInfoNode.source = "link";
				emailInfoNode.subject = "";
				emailInfoNode.message = SD::Utility::String::wstring_to_utf8(tmpMessage);
				emailInfoNode.nodeId = m_fileNode.basic.id;
				userContext_->getShareResMgr()->setMailInfo(m_fileNode.basic.id, emailInfoNode);
			}
		}
		for(UIGroupNodeList::iterator it = m_inviteGroupList.begin(); it != m_inviteGroupList.end(); ++it)
		{
			int64_t count = 0;
			PageParam pageParam;
			UserGroupNodeInfoArray groupMembers;
			if(userContext_->getGroupMgr()->GetGroupListMemberInfo(it->id, "", "all", pageParam, count, groupMembers))
			{
				continue;
			}
			for(UserGroupNodeInfoArray::iterator im = groupMembers.begin(); im != groupMembers.end(); ++im)
			{
				ShareUserInfoList shareUserInfos;
				std::string SearchKey = im->groupInfo_.loginName();
				if(userContext_->getShareResMgr()->listDomainUsers(SearchKey, shareUserInfos, 100))
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "listDomainUsers is failed.");
					continue;
				}
				if(shareUserInfos.size() == 0)
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "shareUserInfos is NULL.")
						continue;
				}
				emailNode.mailto = shareUserInfos[0].email();
				if(emailNode.mailto.empty())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
				}
				else
				{
					if(!userContext_->getShareResMgr()->sendEmail(emailNode))
					{
						EmailInfoNode emailInfoNode;
						emailInfoNode.sender = userContext_->getUserInfoMgr()->getUserId();
						emailInfoNode.source = "link";
						emailInfoNode.subject = "";
						emailInfoNode.message = SD::Utility::String::wstring_to_utf8(tmpMessage);
						emailInfoNode.nodeId = m_fileNode.basic.id;
						userContext_->getShareResMgr()->setMailInfo(m_fileNode.basic.id, emailInfoNode);
					}
				}
			}
		}
		Close();
	}
}