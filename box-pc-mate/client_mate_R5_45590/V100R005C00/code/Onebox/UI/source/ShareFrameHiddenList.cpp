#include "stdafxOnebox.h"
#include "ShareFrameHiddenList.h"
#include "Utility.h"
#include "ControlNames.h"
#include "UserContextMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "InILanguage.h"

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(ShareFrameHiddenList, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK,OnItemDoubleClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMCLICK,OnItemClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS,OnKillFocus)
	DUI_END_MESSAGE_MAP()

	ShareFrameHiddenList::ShareFrameHiddenList(UserContext* context, ShareUserInfoList& shareUserInfos):userContext_(context), shareUserInfos_(shareUserInfos)
	{

	}

	ShareFrameHiddenList::~ShareFrameHiddenList()
	{
		
	}

	void ShareFrameHiddenList::Init(HWND hWndParent, POINT ptPos)
	{
		//Create(hWndParent, _T("ShareFrameHiddenList"), UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE | WS_EX_APPWINDOW);
		Create(hWndParent, _T("ShareFrameHiddenList"), UI_WNDSTYLE_FRAME, WS_EX_WINDOWEDGE);
		//Create(NULL,L"ShareFrame", UI_WNDSTYLE_FRAME, WS_EX_WINDOWEDGE, 0, 0, 0, 0);
		//HWND m_hwnd = Create(hWndParent, _T("ShareFrameHiddenList"), UI_WNDSTYLE_DIALOG, WS_EX_TOOLWINDOW);
		::ClientToScreen(hWndParent, &ptPos);
		::SetWindowPos(*this, NULL, ptPos.x, ptPos.y, 0, 0, SWP_NOZORDER | SWP_NOSIZE | SWP_NOACTIVATE);
		loadData();
	}

	LPCTSTR ShareFrameHiddenList::GetWindowClassName(void) const
	{
		return ControlNames::WND_HIDDEN_LIST_CLS_NAME;
	}

	DuiLib::CDuiString ShareFrameHiddenList::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	DuiLib::CDuiString ShareFrameHiddenList::GetSkinFile()
	{
		return ControlNames::SKIN_XML_HIDDEN_LIST_FILE;
	}

	void ShareFrameHiddenList::OnFinalMessage(HWND /*hWnd*/) 
	{ 
		//delete this;
	}

	LRESULT ShareFrameHiddenList::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandle = TRUE;

		switch (uMsg)
		{
		case WM_KILLFOCUS:    
			//OnKillFocus(uMsg, wParam, lParam, bHandle); 
			break;
			/*case WM_NCHITTEST: 
			OnNcHitTest(uMsg,wParam,lParam,bHandle); 
			break;*/
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

	void ShareFrameHiddenList::Notify(DuiLib::TNotifyUI& msg)
	{
		return WindowImplBase::Notify(msg);
	}

	LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return HTCLIENT;
	}

	void ShareFrameHiddenList::OnClick(DuiLib::TNotifyUI& msg)
	{

	}

	void ShareFrameHiddenList::OnItemDoubleClick(DuiLib::TNotifyUI& msg)
	{
		ShareFrameListContainerElement* pItem = static_cast<ShareFrameListContainerElement*>(msg.pSender);
		if(!pItem)
		{
			return;
		}
		
		shareUserInfo_ = pItem->nodeData.shareUserInfo;
		Close();
		return;
	}

	void ShareFrameHiddenList::OnItemClick(DuiLib::TNotifyUI& msg)
	{
		return;
	}

	void ShareFrameHiddenList::OnKillFocus(DuiLib::TNotifyUI& msg)
	{
		//Close();
		return;
	}

	void ShareFrameHiddenList::loadData()
	{
		CListUI* hiddenUserList = static_cast<CListUI*>(m_PaintManager.FindControl(L"shareFrame_hiddenUsersList"));

		hiddenUserList->SetVisible(true);
		hiddenUserList->RemoveAll();

		for(ShareUserInfoList::iterator it = shareUserInfos_.begin(); it != shareUserInfos_.end(); it++)
		{
			CDialogBuilder builder;
			ShareFrameListContainerElement* node = static_cast<ShareFrameListContainerElement*>(
				builder.Create(ControlNames::SKIN_XML_SHARE_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &m_PaintManager, NULL));
			if (NULL == node)
			{
				continue;
			}
			node->nodeData.shareUserInfo.department(it->department());
			node->nodeData.shareUserInfo.domain(it->domain());
			node->nodeData.shareUserInfo.email(it->email());
			node->nodeData.shareUserInfo.id(it->id());
			node->nodeData.shareUserInfo.label(it->label());
			node->nodeData.shareUserInfo.loginName(it->loginName());
			node->nodeData.shareUserInfo.name(it->name());
			node->nodeData.shareUserInfo.objectSid(it->objectSid());
			node->nodeData.shareUserInfo.recycleDays(it->recycleDays());
			node->nodeData.shareUserInfo.regionId(it->regionId());
			node->nodeData.shareUserInfo.spaceQuota(it->spaceQuota());
			node->nodeData.shareUserInfo.spaceUsed(it->spaceUsed());
			node->nodeData.shareUserInfo.status(it->status());
			node->nodeData.shareUserInfo.type(it->type());

			// initial UI
			CTextUI* name = static_cast<CTextUI*>(m_PaintManager.FindSubControlByName(node, L"shareFrame_userName"));
			if (NULL != name)
			{
				std::wstring tmpStr = SD::Utility::String::utf8_to_wstring(node->nodeData.shareUserInfo.name()) + L" " 
					+ SD::Utility::String::utf8_to_wstring(node->nodeData.shareUserInfo.department());
				//name->SetShowHtml(true);
				name->SetText(tmpStr.c_str());
			}

			if (hiddenUserList->Add(node))
			{
				// update the index information
			}
		}
	}

	ShareUserInfo ShareFrameHiddenList::getDBClickItem()
	{
		return shareUserInfo_;
	}
}
