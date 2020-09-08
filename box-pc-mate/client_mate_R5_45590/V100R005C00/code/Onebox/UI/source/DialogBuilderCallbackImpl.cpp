#include "stdafxOnebox.h"
#include "DialogBuilderCallbackImpl.h"
#include "ChildLayout.h"
#include "ListContainerElement.h"
#include "Share2MeElement.h"
#include "MyShareElement.h"
#include "TileLayoutListUI.h"
#include "CustomListUI.h"
#include "SearchTxt.h"
#include "ItemCheckBox.h"
#include "MyShareElement.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "MyFileElement.h"
#include "TeamspaceElement.h"
#include "MsgElement.h"
#include "CustomComboUI.h"
#include "ScaleButton.h"
#include "UILeftOption.h"
#include "UIScaleImgButton.h"
#include "UIUserPhoto.h"
#include "UIScaleIconButton.h"
#include "RoundGif.h"
#include "GroupButton.h"
#include "UserListControl.h"
#include "FullBackUpTree.h"

namespace Onebox
{
	std::auto_ptr<DialogBuilderCallbackImpl> DialogBuilderCallbackImpl::instance_;

	DialogBuilderCallbackImpl* DialogBuilderCallbackImpl::getInstance()
	{
		if (NULL == instance_.get())
		{
			instance_ = std::auto_ptr<DialogBuilderCallbackImpl>(new DialogBuilderCallbackImpl);
		}
		return instance_.get();
	}

	CControlUI* DialogBuilderCallbackImpl::CreateControl(LPCTSTR pstrClass)
	{
		if (_tcscmp(pstrClass, _T("ChildLayoutEx")) == 0)
		{
			return new ChildLayoutEx(this);
		}
		else if (_tcscmp(pstrClass, _T("MyFilesListContainerElement")) == 0)
		{
			return new MyFilesListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("MyFilesTileLayoutListContainerElement")) ==0)
		{
			return new MyFilesTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("MyShareListContainerElement")) == 0)
		{
			return new CMyShareElementUI;
		}
		else if (_tcscmp(pstrClass, _T("Share2MeListContainerElement")) == 0)
		{
			return new Share2MeListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("Share2MeTileLayoutListContainerElement")) ==0)
		{
			return new Share2MeTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("MyShareTileLayoutListContainerElement")) ==0)
		{
			return new MyShareTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceTileLayoutListContainerElement")) ==0)
		{
			return new TeamSpaceTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TransTaskListContainerElement")) == 0)
		{
			return new TransTaskListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TransTaskCompleteListContainerElement")) == 0)
		{
			return new TransTaskListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TileLayoutList")) == 0)
		{
			return new CTileLayoutListUI;
		}
		else if (_tcscmp(pstrClass, _T("CustomList")) == 0)
		{
			return new CCustomListUI;
		}
		else if (_tcscmp(pstrClass, _T("SearchTxt")) == 0)
		{
			return new CSearchTxtUI;
		}
		else if (_tcscmp(pstrClass, _T("UploadFrameTreeNode")) == 0)
		{
			return new UploadFrameTreeNode;
		}
		else if (_tcscmp(pstrClass, _T("ShareFrameTileLayoutListContainerElement")) == 0)
		{
			return new ShareFrameTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("ShareFrameListContainerElement")) == 0)
		{
			return new ShareFrameListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("MyFilesRichEditUI")) == 0)
		{
			return new CRenameRichEditUI;
		}
		else if (_tcscmp(pstrClass, _T("ItemCheckBox")) == 0)
		{
			return new CItemCheckBoxUI;
		}
		else if (_tcscmp(pstrClass, _T("SelectallCheckBox")) == 0)
		{
			return new CSelectallCheckBoxUI;
		}
		else if (_tcscmp(pstrClass, _T("MyShareElement")) == 0)
        {
            return new CMyShareElementUI;
        }
		else if (_tcscmp(pstrClass, _T("FileVersionListContainerElement")) == 0)
		{
			return new FileVersionListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamspaceListFileContainerElement")) == 0)
		{
			return new TeamspaceListFileElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceFilesTileLayoutListContainerElement")) == 0)
		{
			return new TeamspaceTileFileElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceTileLayoutListContainerElement")) == 0)
		{
			return new TeamSpaceTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceManageListContainerElement")) == 0)
		{
			return new TeamSpaceManageListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceManageTileLayoutListContainerElement")) == 0)
		{
			return new TeamSpaceManageTileLayoutListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("TeamSpaceManageListUserContainerElement")) == 0)
		{
			return new TeamSpaceManageListUserContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("MsgFrameListContainerElement")) == 0)
		{
			return new MsgFrameListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("CustomCombo")) == 0)
		{
			return new CCustomComboUI;
		}
		else if (_tcscmp(pstrClass, _T("ScaleButton")) == 0)
		{
			return new CScaleButtonUI;
		}
		else if (_tcscmp(pstrClass, _T("LeftOption")) == 0)
		{
			return new CLeftOptionUI;
		}
		else if (_tcscmp(pstrClass, _T("ScaleImgButton")) == 0)
		{
			return new CScaleImgButtonUI;
		}
		else if (_tcscmp(pstrClass, _T("UserPhoto")) == 0)
		{
			return new CUserPhotoUI;
		}
		else if (_tcscmp(pstrClass, _T("ScaleIconButton")) == 0)
		{
			return new CScaleIconButtonUI;
		}
		else if (_tcscmp(pstrClass, _T("RoundGif")) == 0)
		{
			return new CRoundGifUI;
		}
		else if (_tcscmp(pstrClass, _T("GroupButton")) == 0)
		{
			return new CGroupButtonUI;
		}
		else if (_tcscmp(pstrClass, _T("UserListControl")) == 0)
		{
			return new CUserListControlUI;
		}
		else if (_tcscmp(pstrClass, _T("BackupTreeNode")) == 0)
		{
			return new BackupTreeNode;
		}
		else if (_tcscmp(pstrClass, _T("TransErrorListContainerElement")) == 0)
		{
			return new TransErrorListContainerElement;
		}
		else if (_tcscmp(pstrClass, _T("FullBackUpErrorListContainerElement")) == 0)
		{
			return new FullBackUpErrorListContainerElement;
		}
		return NULL;
	}

	bool DialogBuilderCallbackImpl::InitLanguage(CControlUI* control)
	{
// 		if (NULL == control)
// 		{
// 			return false;
// 		}
// 		CInIHelper InIHelper(SD::Utility::FS::format_path(GetLanguageFilePath().c_str()));
// 		std::wstring TextKey = control->GetName().GetData();
// 		TextKey += LANGUAGE_DEFAULT_TEXT_KEY;
// 		std::wstring ToolTipKey = control->GetName().GetData();
// 		ToolTipKey+=LANGUAGE_DEFAULT_TOOLTIP_KEY;
// 		std::wstring controlText =  InIHelper.GetString(LANGUAGE_FRAMELANGUAGEINFO_SECTION,TextKey,L"");
// 		std::wstring controlToolTip=  InIHelper.GetString(LANGUAGE_FRAMELANGUAGEINFO_SECTION,ToolTipKey,L"");
// 		if (_tcsicmp(L"",controlText.c_str()) != 0)
// 		{
// 			control->SetText(controlText.c_str());
// 		}
// 		if (_tcsicmp(L"",controlToolTip.c_str()) != 0)
// 		{
// 			control->SetToolTip(controlToolTip.c_str());
// 		}

		return true;
	}
}