#include "stdafxOnebox.h"
#include "Common.h"
#include "MsgElement.h"
#include "InILanguage.h"

namespace Onebox
{
	void MsgFrameListContainerElement::initUI()
	{
		CButtonUI* msgIcon = static_cast<CButtonUI*>(this->FindSubControl(L"msgFrame_icon"));
		if (msgIcon != NULL)
		{
			std::wstring iconPath = L"";
			if (MS_UnRead == nodeData.status)
			{
				if (MT_System == nodeData.type)
				{
					iconPath = L"file='..\\Image\\ic_top_sys_messege_notice.png' source='0,0,20,20'";
				}
				else
				{
					iconPath = L"file='..\\Image\\ic_top_sys_messege_new.png' source='0,0,20,20'";
				}
			}
			else if(MS_Readed == nodeData.status)
			{
				if (MT_System == nodeData.type)
				{
					iconPath = L"file='..\\Image\\ic_top_sys_messege_notice.png' source='0,30,20,50'";
				}
				else
				{
					iconPath = L"file='..\\Image\\ic_top_sys_messege_new.png' source='0,30,20,50'";
				}
			}	
			msgIcon->SetNormalImage(iconPath.c_str());
			msgIcon->SetTag((UINT_PTR)this);
		}

		CLabelUI* msgInfo = static_cast<CLabelUI*>(this->FindSubControl(L"msgFrame_info"));
		CHorizontalLayoutUI* msgTip = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(L"msgFrame_tooltip"));
		std::wstring str_text = L"";
		std::wstring str_tip = L"";
		getShowText(nodeData,str_text,str_tip);
		if (msgInfo != NULL)
		{
			msgInfo->SetShowHtml(true);
			msgInfo->SetText(str_text.c_str());				
		}
		if (msgTip != NULL)
		{
			if(msgInfo)
				msgTip->SetToolTip(str_tip.c_str());
		}

		CLabelUI* lTime = static_cast<CButtonUI*>(this->FindSubControl(L"msgFrame_time"));
		if (NULL != lTime)
		{
			lTime->SetText(SD::Utility::DateTime::getTime(nodeData.createdAt, SD::Utility::Crt,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
			lTime->SetToolTip(SD::Utility::DateTime::getTime(nodeData.createdAt, SD::Utility::Crt,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
		}

		CButtonUI* btn_view = static_cast<CButtonUI*>(this->FindSubControl(L"msgFrame_view"));
		CButtonUI* btn_others = static_cast<CButtonUI*>(this->FindSubControl(L"msgFrame_more"));
		
		if (MT_Share == nodeData.type || MT_TeamSpace_Upload ==  nodeData.type || MT_TeamSpace_Add ==  nodeData.type  || MT_System ==  nodeData.type )
		{
			if (NULL != btn_view)
			{
				btn_view->SetVisible();
			}
		}
		if (MT_Share == nodeData.type || MT_TeamSpace_Upload ==  nodeData.type)
		{
			if (NULL != btn_others)
			{
				btn_others->SetVisible();
			}
		}

		CButtonUI* btn_delete = static_cast<CButtonUI*>(this->FindSubControl(L"msgFrame_delete"));
		if(NULL != btn_delete && MT_System != nodeData.type)
		{
			btn_delete->SetVisible();
		}

	}

	void MsgFrameListContainerElement::getShowText(MsgNode& msgNode,std::wstring& str_text,std::wstring& str_tip)
	{
		str_text = L"";
		str_tip = L"";
		std::wstring provider = SD::Utility::String::utf8_to_wstring(msgNode.providerName);
		std::wstring msg_type = L"";

		switch (msgNode.type)
		{
		case MT_Share:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_SHARE_KEY) + L"] ";
				std::wstring node_name = SD::Utility::String::utf8_to_wstring(msgNode.params.nodeName);
				std::wstring msg_desc = msgNode.params.nodeType == FILE_TYPE_DIR ? USER_MSG_SHARE_FOLDER : USER_MSG_SHARE_FILE;
				str_text = iniLanguageHelper.GetMsgDesc(msg_desc, decorate(msg_type).c_str(), provider.c_str(), decorate(node_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(msg_desc, msg_type.c_str(), provider.c_str(), node_name.c_str());
			}
			break;
		case MT_Share_Delete:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_SHARE_KEY) + L"] ";
				std::wstring node_name = SD::Utility::String::utf8_to_wstring(msgNode.params.nodeName);
				std::wstring msg_desc = msgNode.params.nodeType == FILE_TYPE_DIR ? USER_MSG_CANCEL_SHARE_FOLDER : USER_MSG_CANCEL_SHARE_FILE;
				str_text = iniLanguageHelper.GetMsgDesc(msg_desc, decorate(msg_type).c_str(), provider.c_str(), decorate(node_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(msg_desc, msg_type.c_str(), provider.c_str(), node_name.c_str());
			}
			break;
		case MT_TeamSpace_Upload:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY) + L"] ";
				std::wstring teamspace_name = SD::Utility::String::utf8_to_wstring(msgNode.params.teamSpaceName);
				std::wstring node_name = SD::Utility::String::utf8_to_wstring(msgNode.params.nodeName);
				if((int32_t)UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage())
				{
					str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, decorate(msg_type).c_str(), provider.c_str(), decorate(teamspace_name).c_str(), decorate(node_name).c_str());
					str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, msg_type.c_str(), provider.c_str(), teamspace_name.c_str(), node_name.c_str());
				}
				else
				{
					str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, decorate(msg_type).c_str(), provider.c_str(), decorate(node_name).c_str(), decorate(teamspace_name).c_str());
					str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_UPLOAD, msg_type.c_str(), provider.c_str(), node_name.c_str(), teamspace_name.c_str());

				}
			}
			break;
		case MT_TeamSpace_Add:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY) + L"] ";
				std::wstring teamspace_name = SD::Utility::String::utf8_to_wstring(msgNode.params.teamSpaceName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_TEAMSPACE, decorate(msg_type).c_str(), provider.c_str(), decorate(teamspace_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_TEAMSPACE, msg_type.c_str(), provider.c_str(), teamspace_name.c_str());
			}
			break;
		case MT_TeamSpace_Delete:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY) + L"] ";
				std::wstring teamspace_name = SD::Utility::String::utf8_to_wstring(msgNode.params.teamSpaceName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_TEAMSPACE, decorate(msg_type).c_str(), provider.c_str(), decorate(teamspace_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_TEAMSPACE, msg_type.c_str(), provider.c_str(), teamspace_name.c_str());

			}
			break;
		case MT_TeamSpace_Leave:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY) + L"] ";
				std::wstring teamspace_name = SD::Utility::String::utf8_to_wstring(msgNode.params.teamSpaceName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_TEAMSPACE, decorate(msg_type).c_str(), provider.c_str(), decorate(teamspace_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_TEAMSPACE, msg_type.c_str(), provider.c_str(), teamspace_name.c_str());
			}
			break;
		case MT_TeamSpace_RoleUpdate:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_TEAM_KEY) + L"] ";
				std::wstring str_role = L"";
				if ("admin" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY);
				}
				else if ("manager" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY);
				}
				else if ("editor" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_EDITOR_KEY);
				}
				else if ("viewer" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_VIEWER_KEY);
				}
				else if ("previewer" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_PREVIEWER_KEY);
				}
				else if ("uploader" == msgNode.params.currentRole)
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADER_KEY);
				}
				else
				{
					str_role = iniLanguageHelper.GetCommonString(TEAMSPACE_UPLOADERANDVIEWER_KEY);
				}
				std::wstring teamspace_name = SD::Utility::String::utf8_to_wstring(msgNode.params.teamSpaceName);

				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_ROLE_UPDATE, decorate(msg_type).c_str(), provider.c_str(), decorate(teamspace_name).c_str(), decorate(str_role).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_TEAMSPACE_ROLE_UPDATE, msg_type.c_str(), provider.c_str(), teamspace_name.c_str(), str_role.c_str());

			}
			break;
		case MT_Group_Add:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY) + L"] ";
				std::wstring group_name = SD::Utility::String::utf8_to_wstring(msgNode.params.groupName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_GROUP, decorate(msg_type).c_str(), provider.c_str(), decorate(group_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_JOIN_GROUP, msg_type.c_str(), provider.c_str(), group_name.c_str());
			}
			break;
		case MT_Group_Delete:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY) + L"] ";
				std::wstring group_name = SD::Utility::String::utf8_to_wstring(msgNode.params.groupName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_GROUP, decorate(msg_type).c_str(), provider.c_str(), decorate(group_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_REMOVE_FROM_GROUP, msg_type.c_str(), provider.c_str(), group_name.c_str());
			}
			break;
		case MT_Group_Leave:
			{
				msg_type = L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY) + L"] ";
				std::wstring group_name = SD::Utility::String::utf8_to_wstring(msgNode.params.groupName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_GROUP, decorate(msg_type).c_str(), provider.c_str(), decorate(group_name).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_QUIT_GROUP, msg_type.c_str(), provider.c_str(), group_name.c_str());
			}
			break;
		case MT_Group_RoleUpdate:
			{
				std::wstring role = L"";
				if ("admin" == msgNode.params.currentRole)
				{
					role = iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY);
				}
				else if ("manager" == msgNode.params.currentRole)
				{
					role = iniLanguageHelper.GetCommonString(TEAMSPACE_MANAGER_KEY);
				}
				else
				{
					role = iniLanguageHelper.GetCommonString(COMMENT_COMMONUSER_KEY);
				}
				msg_type =  L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_GROUP_KEY) + L"] ";
				std::wstring group_name = SD::Utility::String::utf8_to_wstring(msgNode.params.groupName);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_GROUP_ROLE_UPDATE, decorate(msg_type).c_str(), provider.c_str(),
					decorate(group_name).c_str(), decorate(role).c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_GROUP_ROLE_UPDATE, msg_type.c_str(), provider.c_str(), group_name.c_str(), role.c_str());
			}
			break;
		case MT_System:
			{
				msg_type =  L"[" + iniLanguageHelper.GetCommonString(MSGFRAME_SYSTEM_MSG_KEY) + L"] ";
				std::wstring title = SD::Utility::String::utf8_to_wstring(msgNode.params.title);
				str_text = iniLanguageHelper.GetMsgDesc(USER_MSG_SYSTEM_ANNOUNCEMENT, decorate(msg_type).c_str(), title.c_str());
				str_tip = iniLanguageHelper.GetMsgDesc(USER_MSG_SYSTEM_ANNOUNCEMENT, msg_type.c_str(),  title.c_str());
			}
			break;
		default:
			break;
		}
		str_text = decorate(str_text, false);
	}

	std::wstring MsgFrameListContainerElement::decorate(std::wstring content, bool isBold, std::wstring color)
	{
		std::wstring font = isBold ? FONT_BOLD : FONT_NORMAL;
		content = L"{f "+ font + L"}{c " + color + L"}" + content + L"{/c}{/f}";
		return content;
	}

}