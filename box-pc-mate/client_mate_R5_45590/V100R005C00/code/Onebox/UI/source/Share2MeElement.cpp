#include "stdafxOnebox.h"
#include "Common.h"
#include "Share2MeElement.h"
#include "TileLayoutListUI.h"
#include "SkinConfMgr.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"
#include "InILanguage.h"

namespace Onebox
{
	void CShare2MeElementUI::fillData(const ShareNode& shareNode, UserContext* userContext, int64_t parentId)
	{
		m_uNodeData.basic.id = shareNode.id();
		m_uNodeData.basic.name = SD::Utility::String::utf8_to_wstring(shareNode.name());
		m_uNodeData.basic.type = shareNode.type();
		m_uNodeData.basic.size = shareNode.size();
		m_uNodeData.basic.parent = parentId;
		m_uNodeData.basic.extraType = SD::Utility::String::utf8_to_wstring(shareNode.extraType());
		ownerId = shareNode.ownerId();
		ownerName = SD::Utility::String::utf8_to_wstring(shareNode.ownerName());
		shareTime = shareNode.modifiedAt();
		if(0==parentId)
		{
			m_uNodeData.userContext = UserContextMgr::getInstance()->createUserContext(userContext,
				ownerId, UserContext_ShareUser, ownerName);
		}
		else
		{
			m_uNodeData.userContext = userContext;
		}
	}

	void CShare2MeElementUI::rename()
	{
		Select();
		CRichEditUI* creui = static_cast<CRichEditUI*>(FindSubControlsByClass(DUI_CTR_RICHEDITUI));
		if (NULL == creui) return;
		creui->SetMouseEnabled(true);
		SetMouseChildEnabled();
		creui->SetFocus();
	}

	bool Share2MeListContainerElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(SHARE2ME_FILEICON));
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

	void Share2MeListContainerElement::initUI(bool renameable)
	{
		isNoThumb = false;
		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(SHARE2ME_FILEICON));
		if (fileicon != NULL)
		{
			if(FILE_TYPE_FILE==m_uNodeData.basic.type && thumbEnabled(m_uNodeData.basic.name))
			{
				iconPath = ThumbMgr::getInstance()->getThumbPath(m_uNodeData.userContext->getUserInfoMgr()->getUserId(), m_uNodeData.basic.id, 0);
				isNoThumb = iconPath.empty();
			}
			if(iconPath.empty())
			{
				iconPath = SkinConfMgr::getInstance()->getIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}
			fileicon->SetNormalImage(iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CButtonUI* name = static_cast<CButtonUI*>(this->FindSubControl(SHARE2ME_FILENAME));
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

		CLabelUI* filetype = static_cast<CLabelUI*>(this->FindSubControl(SHARE2ME_SHAREUSER));
		if (filetype != NULL)
		{
			filetype->SetText(ownerName.c_str());
			filetype->SetToolTip(ownerName.c_str());
		}

		CLabelUI* filesize = static_cast<CLabelUI*>(this->FindSubControl(SHARE2ME_FILESIZE));
		if (filesize != NULL)
		{
			filesize->SetText((FILE_TYPE_FILE==m_uNodeData.basic.type) ? SD::Utility::String::getSizeStr(m_uNodeData.basic.size).c_str() : L"-");
		}

		CLabelUI* shareime = static_cast<CLabelUI*>(this->FindSubControl(SHARE2ME_SHARETIME));
		if (shareime != NULL)
		{
			shareime->SetText(SD::Utility::DateTime::getTime(shareTime, SD::Utility::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
		}
		this->setRenameFlag(renameable);
	}

	bool Share2MeTileLayoutListContainerElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(SHARE2ME_ICON));
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

	void Share2MeTileLayoutListContainerElement::initUI(bool renameable)
	{
		isNoThumb = false;
		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(SHARE2ME_ICON));
		if (fileicon != NULL)
		{
			if(FILE_TYPE_FILE==m_uNodeData.basic.type && thumbEnabled(m_uNodeData.basic.name))
			{
				iconPath = ThumbMgr::getInstance()->getThumbPath(m_uNodeData.userContext->getUserInfoMgr()->getUserId(), m_uNodeData.basic.id, 1);
				isNoThumb = iconPath.empty();
			}
			if(iconPath.empty())
			{
				//iconPath = SkinConfMgr::getInstance()->getBigIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name);
				if (-3 == m_uNodeData.basic.type){
					m_uNodeData.basic.type = FILE_TYPE_DIR;
					m_uNodeData.basic.extraType = FOLDER_ICON_COMPUTER;
				}
				iconPath = SkinConfMgr::getInstance()->getBigIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}
			fileicon->SetBkImage(iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CRichEditUI* filename = static_cast<CRichEditUI*>(this->FindSubControl(SHARE2ME_NAME));
		if (filename != NULL)
		{		
			filename->SetText(m_uNodeData.basic.name.c_str());
			filename->SetToolTip(m_uNodeData.basic.name.c_str());
		}
		this->SetToolTip(m_uNodeData.basic.name.c_str());
		this->setRenameFlag(renameable);
	}
}