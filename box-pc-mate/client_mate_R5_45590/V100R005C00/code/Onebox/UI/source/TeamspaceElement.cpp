#include "stdafxOnebox.h"
#include "Common.h"
#include "TeamspaceElement.h"
#include "TileLayoutListUI.h"
#include "SkinConfMgr.h"
#include "InILanguage.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"
#include "ShareResMgr.h"

using namespace SD::Utility;

namespace Onebox
{
	void CTeamspaceFileElementUI::fillData(const FILE_DIR_INFO& fileNode, UserContext* userContext)
	{
		m_uNodeData.basic = fileNode;
		m_uNodeData.userContext = userContext;

		if (fileNode.type == FILE_TYPE_DIR)
		{
			str_type = L"folder";
		}
		else
		{
			str_type = FS::get_extension_name(fileNode.name);
			if (str_type.empty() || SkinConfMgr::getInstance()->isUnknownType(str_type))
				str_type = L"unknown";
		}
	}
	
	void CTeamspaceFileElementUI::rename()
	{
		Select();
		CRichEditUI* creui = static_cast<CRichEditUI*>(FindSubControlsByClass(DUI_CTR_RICHEDITUI));
		if (NULL == creui) return;

		creui->SetMouseEnabled(true);
		SetMouseChildEnabled();
		creui->SetFocus();
	}

	bool TeamspaceListFileElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_icon")));
		if (NULL==fileicon) return false;

		std::stringstream key;
		key << m_uNodeData.userContext->getUserInfoMgr()->getUserId() << "_" 
			<< m_uNodeData.basic.id << "_0";
		if(key.str()!=thumbKey) return false;
		std::string showPath = ThumbMgr::getInstance()->getShowPath(key.str());
		if(showPath.empty()) return false;

		fileicon->SetNormalImage(SD::Utility::String::utf8_to_wstring(showPath).c_str());
		fileicon->SetTag((UINT_PTR)this);
		isNoThumb = false;
		return true;
	}

	void TeamspaceListFileElement::initUI()
	{
		isNoThumb = false;
		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_icon")));
		if (fileicon != NULL)
		{
			std::wstring str_iconPath;
			if(FILE_TYPE_FILE==m_uNodeData.basic.type && thumbEnabled(m_uNodeData.basic.name))
			{
				str_iconPath = ThumbMgr::getInstance()->getThumbPath(m_uNodeData.userContext->getUserInfoMgr()->getUserId(), m_uNodeData.basic.id, 0);
				isNoThumb = str_iconPath.empty();
			}
			if(str_iconPath.empty())
			{
				str_iconPath = SkinConfMgr::getInstance()->getIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name);
			}
			fileicon->SetNormalImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CButtonUI * m_pVersionBtn = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_version")));
		if (NULL != m_pVersionBtn)
		{
			if (m_uNodeData.basic.version > 1)
			{
				std::wstring str_version = (m_uNodeData.basic.version < 10)?L"V":L"";
				str_version += String::string_to_wstring(String::type_to_string<std::string,int32_t>(m_uNodeData.basic.version));
				m_pVersionBtn->SetText(str_version.c_str());
				m_pVersionBtn->SetVisible();
			}
		}

		CLabelUI* name = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_listName")));
		if (NULL != name)
		{
			name->SetText(m_uNodeData.basic.name.c_str());
			name->SetToolTip(m_uNodeData.basic.name.c_str());	
			if (NULL != name->GetParent())
			{
				CHorizontalLayoutUI * m_pHorLayout = static_cast<CHorizontalLayoutUI*>(name->GetParent());
				if (NULL != m_pHorLayout)
				{
					m_pHorLayout->SetToolTip(m_uNodeData.basic.name.c_str());
				}
			}
		}

		CLabelUI* filesize = static_cast<CLabelUI*>(this->FindSubControl(_T("teamSpace_List_size")));
		if (filesize != NULL)
		{
			if (FILE_TYPE_DIR == m_uNodeData.basic.type)
			{
				filesize->SetText(L"-");
			}
			else
			{
				filesize->SetText(SD::Utility::String::getSizeStr(m_uNodeData.basic.size).c_str());
				filesize->SetUserData(String::type_to_string<std::wstring>(m_uNodeData.basic.size).c_str());
			}
		}

		CLabelUI* changetime = static_cast<CLabelUI*>(this->FindSubControl(_T("teamSpace_List_mtime")));
		if (changetime != NULL)
		{	
			changetime->SetText(SD::Utility::DateTime::getTime(m_uNodeData.basic.mtime, SD::Utility::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
		}

		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("teamSpace_List_listOperation")));
		if ( m_pListHot != NULL )
		{	
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_isShareLink")));
			if(NULL != _pIsShareLink)
			{
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
				{
					_pIsShareLink->SetEnabled(false);
					_pIsShareLink->SetVisible();
					m_pListHot->SetVisible();
				}
			}
		}
	}

	void TeamspaceListFileElement::initUI(const std::wstring& path)
	{
		isNoThumb = false;
		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_icon")));
		if (fileicon != NULL)
		{
			std::wstring str_iconPath;
			if(FILE_TYPE_FILE==m_uNodeData.basic.type && thumbEnabled(m_uNodeData.basic.name))
			{
				str_iconPath = ThumbMgr::getInstance()->getThumbPath(m_uNodeData.userContext->getUserInfoMgr()->getUserId(), m_uNodeData.basic.id, 0);
				isNoThumb = str_iconPath.empty();
			}
			if(str_iconPath.empty())
			{
				str_iconPath = SkinConfMgr::getInstance()->getIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name);
			}
			fileicon->SetNormalImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CButtonUI * m_pVersionBtn = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_version")));
		if (NULL != m_pVersionBtn)
		{
			if (m_uNodeData.basic.version > 1)
			{
				std::wstring str_version = (m_uNodeData.basic.version < 10)?L"V":L"";
				str_version += String::string_to_wstring(String::type_to_string<std::string,int32_t>(m_uNodeData.basic.version));
				m_pVersionBtn->SetText(str_version.c_str());
				m_pVersionBtn->SetVisible();
			}
		}

		CLabelUI* name = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_listName")));
		if (NULL != name)
		{
			name->SetText(m_uNodeData.basic.name.c_str());
			name->SetToolTip(m_uNodeData.basic.name.c_str());	
			if (NULL != name->GetParent())
			{
				CHorizontalLayoutUI * m_pHorLayout = static_cast<CHorizontalLayoutUI*>(name->GetParent());
				if (NULL != m_pHorLayout)
				{
					m_pHorLayout->SetToolTip(m_uNodeData.basic.name.c_str());
				}
			}
		}

		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("teamSpace_List_listOperation")));
		if ( m_pListHot != NULL )
		{	
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(_T("teamSpace_List_isShareLink")));
			if(NULL != _pIsShareLink)
			{
					_pIsShareLink->SetTag((UINT_PTR)this);

					if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
					{
						_pIsShareLink->SetEnabled(false);
						_pIsShareLink->SetVisible();
						m_pListHot->SetVisible();
					}
			}
		}

		CButtonUI* filePath = static_cast<CButtonUI*>(this->FindSubControl(L"teamSpace_List_path"));
		if (filePath != NULL)
		{
			filePath->SetText(path.c_str());
			filePath->SetToolTip(path.c_str());		
			filePath->SetTag((UINT_PTR)this);
		}
	}

	bool TeamspaceTileFileElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_File_nameIcon"));
		if (NULL==fileicon) return false;

		std::stringstream key;
		key << m_uNodeData.userContext->getUserInfoMgr()->getUserId() << "_" 
			<< m_uNodeData.basic.id << "_1";
		if(key.str()!=thumbKey) return false;
		std::string showPath = ThumbMgr::getInstance()->getShowPath(key.str());
		if(showPath.empty()) return false;

		fileicon->SetBkImage(SD::Utility::String::utf8_to_wstring(showPath).c_str());
		fileicon->SetTag((UINT_PTR)this);
		isNoThumb = false;
		return true;
	}

	void TeamspaceTileFileElement::initUI()
	{
		isNoThumb = false;
		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(L"teamSpace_File_nameIcon"));
		if (fileicon != NULL)
		{
			std::wstring str_iconPath;
			if(FILE_TYPE_FILE==m_uNodeData.basic.type && thumbEnabled(m_uNodeData.basic.name))
			{
				str_iconPath = ThumbMgr::getInstance()->getThumbPath(m_uNodeData.userContext->getUserInfoMgr()->getUserId(), m_uNodeData.basic.id, 1);
				isNoThumb = str_iconPath.empty();
			}
			if(str_iconPath.empty())
			{
				str_iconPath = SkinConfMgr::getInstance()->getBigIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name);
			}
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CRichEditUI* filename = static_cast<CRichEditUI*>(this->FindSubControl(L"teamSpace_File_name"));
		if (filename != NULL)
		{		
			filename->SetText(m_uNodeData.basic.name.c_str());
			filename->SetToolTip(m_uNodeData.basic.name.c_str());
		}
		this->SetToolTip(String::replace_all(m_uNodeData.basic.name,L"\r",L" ").c_str());
	}

	void TeamSpaceTileLayoutListContainerElement::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
			else CContainerUI::DoEvent(event);
			return;
		}
		if( event.Type == UIEVENT_RBUTTONDOWN)
		{
			if( IsEnabled() ){
				if(!m_bSelected) Select();
				//wParam is set to 1 ,itemclick is RButton
				m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMCLICK,1);
			}
			return;		
		}
		else if( event.Type == UIEVENT_BUTTONDOWN)
		{
			if( IsEnabled() ){
				if(!m_bSelected) Select();
				//wParam is set to 1 ,itemclick is RButton
				m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMCLICK,1);
			}
			return;		
		}
		CListContainerElementUI::DoEvent(event);
	}
}