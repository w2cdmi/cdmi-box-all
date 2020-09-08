#include "stdafxOnebox.h"
#include "Common.h"
#include "MyFileElement.h"
#include "TileLayoutListUI.h"
#include "SkinConfMgr.h"
#include "InILanguage.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"

using namespace SD::Utility;

namespace Onebox
{
	void CMyFileElementUI::fillData(const FILE_DIR_INFO& fileNode, UserContext* userContext)
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

	void CMyFileElementUI::rename()
	{
		Select();
		CRichEditUI* creui = static_cast<CRichEditUI*>(FindSubControlsByClass(DUI_CTR_RICHEDITUI));		
		if (NULL == creui) return;
		creui->SetMouseEnabled(true);
		SetMouseChildEnabled();
		creui->SetFocus();
	}

	bool MyFilesListContainerElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_LIST_ICON));
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

	void MyFilesListContainerElement::initUI()
	{
		isNoThumb = false;
		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_LIST_ICON));
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
				str_iconPath = SkinConfMgr::getInstance()->getIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}
			fileicon->SetNormalImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CButtonUI * m_pVersionBtn = static_cast<CButtonUI*>(this->FindSubControl(_T("myFile_version")));
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

		CLabelUI* name = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_LIST_NAME));
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

		CLabelUI* filesize = static_cast<CLabelUI*>(this->FindSubControl(MYFILE_LIST_SIZE));
		if (filesize != NULL)
		{
			if (FILE_TYPE_DIR == m_uNodeData.basic.type)
			{
				filesize->SetText(L"-");
				filesize->SetFont(12);
			}
			else
			{
				filesize->SetText(SD::Utility::String::getSizeStr(m_uNodeData.basic.size).c_str());
				filesize->SetUserData(String::type_to_string<std::wstring>(m_uNodeData.basic.size).c_str());
			}
		}

		CLabelUI* changetime = static_cast<CLabelUI*>(this->FindSubControl(MYFILE_LIST_CTIME));
		if (changetime != NULL)
		{
			changetime->SetText(SD::Utility::DateTime::getTime(m_uNodeData.basic.mtime, SD::Utility::Windows,(SD::Utility::LanguageType)iniLanguageHelper.GetLanguage()).c_str());
			changetime->SetUserData(String::type_to_string<std::wstring>(m_uNodeData.basic.mtime).c_str());
		}

		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("myFile_listOperation")));
		if ( m_pListHot != NULL )
		{	
			CButtonUI * _pIsShare = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE));
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK));
			CButtonUI * _pIsSync = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC));
			CButtonUI * _pIsShare_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE_CONTROL));
			CButtonUI * _pIsShareLink_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK_CONTROL));
			CButtonUI * _pIsSync_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC_CONTROL));
			if(NULL != _pIsShare && NULL != _pIsShareLink && NULL != _pIsSync && 
				NULL != _pIsShare_control && NULL != _pIsShareLink_control && NULL != _pIsSync_control)
			{
				int32_t width = 1;
				if (0 == m_uNodeData.basic.parent)
				{
					width += 21;
					if ((m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
						&& (FOLDER_ICON_COMPUTER != m_uNodeData.basic.extraType)
						&& (FOLDER_ICON_DISK != m_uNodeData.basic.extraType))
					{
						_pIsSync->SetEnabled(false);
						_pIsSync->SetVisible();
						_pIsSync_control->SetVisible(false);
					}
					else
					{
						_pIsSync->SetVisible(false);
					}
				}
				width += 21;
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARED)
				{
					_pIsShare->SetEnabled(false);
					_pIsShare->SetVisible();
					_pIsShare_control->SetVisible(false);
				}
				else
				{
					_pIsShare->SetVisible(false);
					_pIsShare_control->SetVisible();
				}
				width += 21;
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
				{
					_pIsShareLink->SetEnabled(false);
					_pIsShareLink->SetVisible();
					_pIsShareLink_control->SetVisible(false);
				}
				else
				{
					_pIsShareLink->SetVisible(false);
					_pIsShareLink_control->SetVisible();
				}
				m_pListHot->SetFixedWidth(width-1);
			}
		}
	}

	void MyFilesListContainerElement::initUI(const std::wstring& path)
	{
		isNoThumb = false;
		CButtonUI* fileicon = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_LIST_ICON));
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
				str_iconPath = SkinConfMgr::getInstance()->getIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}
			fileicon->SetNormalImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CLabelUI* name = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_LIST_NAME));
		if (NULL != name)
		{
			name->SetText(m_uNodeData.basic.name.c_str());
			name->SetToolTip(m_uNodeData.basic.name.c_str());				
		}

		CButtonUI* filePath = static_cast<CButtonUI*>(this->FindSubControl(L"myFile_path"));
		if (filePath != NULL)
		{
			filePath->SetText(path.c_str());
			filePath->SetFont(12);
			filePath->SetToolTip(path.c_str());		
			filePath->SetTag((UINT_PTR)this);
		}

		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("myFile_listOperation")));
		if ( m_pListHot != NULL )
		{	
			CButtonUI * _pIsShare = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE));
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK));
			CButtonUI * _pIsSync = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC));
			CButtonUI * _pIsShare_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE_CONTROL));
			CButtonUI * _pIsShareLink_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK_CONTROL));
			CButtonUI * _pIsSync_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC_CONTROL));
			if(NULL != _pIsShare && NULL != _pIsShareLink && NULL != _pIsSync && 
				NULL != _pIsShare_control && NULL != _pIsShareLink_control && NULL != _pIsSync_control)
			{
				int32_t width = 1;
				if (0 == m_uNodeData.basic.parent)
				{
					width += 21;
					if ((m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
						&& (FOLDER_ICON_COMPUTER != m_uNodeData.basic.extraType)
						&& (FOLDER_ICON_DISK != m_uNodeData.basic.extraType))
					{
						_pIsSync->SetEnabled(false);
						_pIsSync->SetVisible();
						_pIsSync_control->SetVisible(false);
					}
					else
					{
						_pIsSync->SetVisible(false);
					}
				}
				width += 21;
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARED)
				{
					_pIsShare->SetEnabled(false);
					_pIsShare->SetVisible();
					_pIsShare_control->SetVisible(false);
				}
				else
				{
					_pIsShare->SetVisible(false);
					_pIsShare_control->SetVisible();
				}
				width += 21;
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
				{
					_pIsShareLink->SetEnabled(false);
					_pIsShareLink->SetVisible();
					_pIsShareLink_control->SetVisible(false);
				}
				else
				{
					_pIsShareLink->SetVisible(false);
					_pIsShareLink_control->SetVisible();
				}
				m_pListHot->SetFixedWidth(width-1);
			}
		}
	}

	void MyFilesListContainerElement::mouseEnter()
	{
		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("myFile_listOperation")));
		this->SetMouseChildEnabled();
		if ( m_pListHot != NULL )
		{	
			CButtonUI * _pIsShare = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE));
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK));
			CButtonUI * _pIsSync = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC));
			CButtonUI * _pIsShare_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE_CONTROL));
			CButtonUI * _pIsShareLink_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK_CONTROL));
			CButtonUI * _pIsSync_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC_CONTROL));
			if(NULL == _pIsShare || NULL == _pIsShareLink || NULL == _pIsSync  || 
				NULL == _pIsShare_control || NULL == _pIsShareLink_control || NULL == _pIsSync_control) return;

			_pIsShare_control->SetVisible(false);
			_pIsShareLink_control->SetVisible(false);
			_pIsSync_control->SetVisible(false);

			if (0 == m_uNodeData.basic.parent)
			{
				_pIsShare->SetEnabled();
				_pIsShare->SetVisible();
				_pIsShareLink->SetEnabled();
				_pIsShareLink->SetVisible();
				if((FOLDER_ICON_COMPUTER != m_uNodeData.basic.extraType)
					&& (FOLDER_ICON_DISK != m_uNodeData.basic.extraType))
				{
					_pIsSync->SetEnabled();
					_pIsSync->SetVisible();
				}
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
				{
					_pIsSync->SetNormalImage(L"file='..\\Image\\ic_toolmenu_more_cancelsync.png' source='0,0,20,20'");
					_pIsSync->SetHotImage(L"file='..\\Image\\ic_toolmenu_more_cancelsync.png' source='0,0,20,20'");
					_pIsSync->SetPushedImage(L"file='..\\Image\\ic_toolmenu_more_cancelsync.png' source='0,30,20,50'");
					_pIsSync->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_CANCELSYN_KEY).c_str());
				}
				else
				{
					_pIsSync->SetNormalImage(L"file='..\\Image\\ic_toolmenu_sync.png' source='0,0,20,20'");
					_pIsSync->SetHotImage(L"file='..\\Image\\ic_toolmenu_sync.png' source='0,30,20,50'");
					_pIsSync->SetPushedImage(L"file='..\\Image\\ic_toolmenu_sync.png' source='0,60,20,80'");
					_pIsSync->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_SETSYN_KEY).c_str());
				}
				m_pListHot->SetFixedWidth(63);
			}
			else
			{
				_pIsShare->SetEnabled();
				_pIsShare->SetVisible();
				_pIsShareLink->SetEnabled();
				_pIsShareLink->SetVisible();
				_pIsSync->SetVisible(false);
				m_pListHot->SetFixedWidth(42);
			}
		}
		CButtonUI * m_pVersionBtn = static_cast<CButtonUI*>(this->FindSubControl(_T("myFile_version")));
		if(NULL == m_pVersionBtn) return;
		m_pVersionBtn->SetTextColor(0xFFFFFFFF);
		m_pVersionBtn->SetNormalImage(L"");
		m_pVersionBtn->SetPushedImage(L"");
		m_pVersionBtn->SetBkImage(L"file='..\\Image\\ic_popup_view_version - small.png' source='0,26,26,42'");
	}

	void MyFilesListContainerElement::mouseLeave()
	{
		CHorizontalLayoutUI * m_pListHot = static_cast<CHorizontalLayoutUI*>(this->FindSubControl(_T("myFile_listOperation")));

		if ( m_pListHot != NULL )
		{
			CButtonUI * _pIsShare = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE));
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK));
			CButtonUI * _pIsSync = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC));
			CButtonUI * _pIsShare_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARE_CONTROL));
			CButtonUI * _pIsShareLink_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSHARELINK_CONTROL));
			CButtonUI * _pIsSync_control = static_cast<CButtonUI*>(this->FindSubControl(MYFILE_ISSYNC_CONTROL));
			if(NULL == _pIsShare || NULL == _pIsShareLink || NULL == _pIsSync  || 
			   NULL == _pIsShare_control || NULL == _pIsShareLink_control || NULL == _pIsSync_control) return;
			int32_t width = 1;
			if (m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
			{
				_pIsSync->SetNormalImage(L"file='..\\Image\\ic_toolmenu_sync.png' source='0,0,20,20'");
			}
			else
			{
				_pIsSync->SetNormalImage(L"file='..\\Image\\ic_toolmenu_more_cancelsync.png' source='0,0,20,20'");
			}
			if (0 == m_uNodeData.basic.parent)
			{
				width += 21;
				if (m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
				{
					_pIsSync->SetEnabled(false);
					_pIsSync->SetVisible();
					_pIsSync_control->SetVisible();
				}
				else
				{
					_pIsSync->SetVisible(false);
				}
			}
			else
			{
				_pIsSync->SetVisible(false);
				_pIsSync_control->SetVisible(false);
			}
			width += 21;
			if ((m_uNodeData.basic.flags & OBJECT_FLAG_SHARED))				
			{
				if ((FOLDER_ICON_COMPUTER == m_uNodeData.basic.extraType)
					|| (FOLDER_ICON_DISK != m_uNodeData.basic.extraType))
				{
					_pIsShare->SetEnabled(false);
					_pIsShare->SetVisible();
					_pIsShare_control->SetVisible(false);
				}
			}
			else
			{
				_pIsShare->SetVisible(false);
				_pIsShare_control->SetVisible();
			}

			width += 21;
			if (m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
			{
				_pIsShareLink->SetEnabled(false);
				_pIsShareLink->SetVisible();
				_pIsShareLink_control->SetVisible(false);
			}
			else
			{
				_pIsShareLink->SetVisible(false);
				_pIsShareLink_control->SetVisible();
			}
			m_pListHot->SetFixedWidth(width-1);
		}
		this->SetMouseChildEnabled(false);
		CButtonUI * m_pVersionBtn = static_cast<CButtonUI*>(this->FindSubControl(_T("myFile_version")));
		if(NULL == m_pVersionBtn) return;
		m_pVersionBtn->SetTextColor(0xFFFFFFFF);
		m_pVersionBtn->SetNormalImage(L"");
		m_pVersionBtn->SetPushedImage(L"");
		m_pVersionBtn->SetBkImage(L"file='..\\Image\\ic_popup_view_version - small.png' source='0,26,26,42'");	
	}

	bool MyFilesTileLayoutListContainerElement::flushThumb(const std::string& thumbKey)
	{
		if(!isNoThumb) return false;

		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(MYFILE_LARGEICON_ICON));
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

	void MyFilesTileLayoutListContainerElement::initUI()
	{
		isNoThumb = false;
		CLabelUI* fileicon = static_cast<CLabelUI*>(this->FindSubControl(MYFILE_LARGEICON_ICON));
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
				str_iconPath = SkinConfMgr::getInstance()->getBigIconPath(m_uNodeData.basic.type, m_uNodeData.basic.name,m_uNodeData.basic.flags,m_uNodeData.basic.extraType);
			}
			fileicon->SetBkImage(str_iconPath.c_str());
			fileicon->SetTag((UINT_PTR)this);
		}

		CRichEditUI* filename = static_cast<CRichEditUI*>(this->FindSubControl(MYFILE_LARGEICON_NAME));
		if (filename != NULL)
		{		
			filename->SetText(m_uNodeData.basic.name.c_str());
			filename->SetToolTip(m_uNodeData.basic.name.c_str());
		}
		this->SetToolTip(String::replace_all(m_uNodeData.basic.name,L"\r",L" ").c_str());
	}
}