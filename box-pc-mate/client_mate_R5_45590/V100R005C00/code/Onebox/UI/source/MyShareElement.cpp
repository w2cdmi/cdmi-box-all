#include "stdafxOnebox.h"
#include "Common.h"
#include "MyShareElement.h"
#include "TileLayoutListUI.h"
#include "SkinConfMgr.h"
#include "InILanguage.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"

namespace Onebox
{
    void CMyShareElementUI::fillData(const MyShareNode& shareNode)
    {
		m_bReadOnly = true;
		m_iRenameReady = 0;

		shareInfo = shareNode;
		nodeData.basic.id = shareNode.id;
		nodeData.basic.name = SD::Utility::String::utf8_to_wstring(shareNode.name);
		nodeData.basic.type = shareNode.type;

		nodeData.basic.size = shareNode.size;
		nodeData.basic.parent = shareNode.parent;
		nodeData.basic.extraType = SD::Utility::String::utf8_to_wstring(shareNode.extraType);
    }

	bool CMyShareElementUI::flushThumb(const std::string& thumbKey)
    {
		if(!isNoThumb) return false;

        CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(L"myShare_fileicon"));
        if (NULL==fileicon) return false;
		if (NULL==UserContextMgr::getInstance()->getDefaultUserContext()) return false;
		std::stringstream key;
		key << UserContextMgr::getInstance()->getDefaultUserContext()->getUserInfoMgr()->getUserId()
			<< "_" << nodeData.basic.id << "_0";
		if(key.str()!=thumbKey) return false;
		std::string showPath = ThumbMgr::getInstance()->getShowPath(key.str());
		if(showPath.empty()) return false;

        fileicon->SetNormalImage(SD::Utility::String::utf8_to_wstring(showPath).c_str());
		isNoThumb = false;
		return true;
	}

    void CMyShareElementUI::initUI()
    {
		isNoThumb = false;
        CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(L"myShare_fileicon"));
        if (fileicon != NULL)
        {
			std::wstring iconPath;
			UserContext* userContext = UserContextMgr::getInstance()->getDefaultUserContext();
			if(NULL==userContext) return;
			if(FILE_TYPE_FILE==nodeData.basic.type && thumbEnabled(nodeData.basic.name))
			{
				iconPath = ThumbMgr::getInstance()->getThumbPath(userContext->getUserInfoMgr()->getUserId(), nodeData.basic.id, 0);
				isNoThumb = iconPath.empty();
			}
			if(iconPath.empty())
			{				
				iconPath = SkinConfMgr::getInstance()->getIconPath(nodeData.basic.type, nodeData.basic.name, 
					0, nodeData.basic.extraType);
			}

            fileicon->SetNormalImage(iconPath.c_str());
        }

        CRenameRichEditUI* name = static_cast<CRenameRichEditUI*>(this->FindSubControl( L"myShare_filename"));
        if (NULL != name)
        {
            name->SetText(nodeData.basic.name.c_str());
			name->SetToolTip(nodeData.basic.name.c_str());
 			CHorizontalLayoutUI* pLayout = static_cast<CHorizontalLayoutUI*>(name->GetParent());
 			if (pLayout)
 				pLayout->SetToolTip(nodeData.basic.name.c_str());
        }
		CButtonUI* pBtnPath = static_cast<CButtonUI*>(this->FindSubControl(L"myShare_sharePath"));
		if (pBtnPath)
		{
			std::wstring path = SD::Utility::String::utf8_to_wstring(shareInfo.path);
			pBtnPath->SetText(path.c_str());
			pBtnPath->SetToolTip(path.c_str());		
			pBtnPath->SetTag((UINT_PTR)this);
		}

		CLabelUI* pSize = static_cast<CLabelUI*>(this->FindSubControl(L"myShare_fileSize"));
		if (pSize){
			pSize->SetText((FILE_TYPE_DIR == nodeData.basic.type) ? L"-" : SD::Utility::String::getSizeStr(nodeData.basic.size).c_str());	
		}

		CButtonUI* pLinkCount = static_cast<CButtonUI*>(this->FindSubControl(L"myShare_shareLink"));
		if (pLinkCount){
			CDuiString strText;
			CDuiString strTemp = iniLanguageHelper.GetFrameName(SHAREFRAME_LINKS).c_str();
			strText.Format(L" %d ", shareInfo.linkCount);
			pLinkCount->SetText(strText+strTemp);
			pLinkCount->SetTag((UINT_PTR)this);
		}
    }

	void MyShareTileLayoutListContainerElement::initUI(bool renameable /* = false */)
	{
		isNoThumb = false;
		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(L"myShare_icon"));
		if (fileicon != NULL)
		{
			std::wstring iconPath;
			UserContext* userContext = UserContextMgr::getInstance()->getDefaultUserContext();
			if(NULL==userContext) return;
			if(FILE_TYPE_FILE==nodeData.basic.type && thumbEnabled(nodeData.basic.name))
			{
				iconPath = ThumbMgr::getInstance()->getThumbPath(userContext->getUserInfoMgr()->getUserId(), nodeData.basic.id, 1);
				isNoThumb = iconPath.empty();
			}
			if(iconPath.empty())
			{
				iconPath = SkinConfMgr::getInstance()->getBigIconPath(FILE_TYPE_DIR, 
					nodeData.basic.name, 0, nodeData.basic.extraType);
			}

			fileicon->SetBkImage(iconPath.c_str());
		}

		CLabelUI* filename = static_cast<CLabelUI*>(this->FindSubControl(L"myShare_name"));
		if (filename != NULL)
		{		
			filename->SetText(nodeData.basic.name.c_str());
			filename->SetToolTip(nodeData.basic.name.c_str());
		}
		this->SetToolTip(nodeData.basic.name.c_str());
	}

	void MyShareTileLayoutListContainerElement::flushThumb()
	{
		isNoThumb = false;
		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(L"myShare_icon"));
		if (fileicon != NULL)
		{
			std::wstring iconPath;
			UserContext* userContext = UserContextMgr::getInstance()->getDefaultUserContext();
			if(NULL==userContext) return;
			if(FILE_TYPE_FILE==nodeData.basic.type && thumbEnabled(nodeData.basic.name))
			{
				iconPath = ThumbMgr::getInstance()->getThumbPath(userContext->getUserInfoMgr()->getUserId(), nodeData.basic.id, 0);
				isNoThumb = iconPath.empty();
			}
			if(iconPath.empty())
			{
				//iconPath = SkinConfMgr::getInstance()->getIconPath(shareInfo.type, SD::Utility::String::utf8_to_wstring(shareInfo.name));
				if (-3 == m_uNodeData.basic.type){
					m_uNodeData.basic.type = FILE_TYPE_DIR;
					m_uNodeData.basic.extraType = FOLDER_ICON_COMPUTER;
				}
				iconPath = SkinConfMgr::getInstance()->getBigIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}

			fileicon->SetBkImage(iconPath.c_str());
		}
	}
}