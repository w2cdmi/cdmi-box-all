#include "stdafxOnebox.h"
#include "OpenFileDbClick.h"
#include "DialogBuilderCallbackImpl.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "TeamSpaceResMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"
#include "FilterMgr.h"
#include "ThumbMgr.h"
#include "UserInfoMgr.h"

namespace Onebox{

	OpenFileDbClickDialog::OpenFileDbClickDialog(UserContext* context,UIFileNode& fileNode,bool isOpen)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOpenBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_pSaveBtn(NULL)
		,m_fileNode(fileNode)
		,m_isOpen(isOpen)
	{
	}

	OpenFileDbClickDialog::~OpenFileDbClickDialog()
	{
	}

	CDuiString OpenFileDbClickDialog::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString OpenFileDbClickDialog::GetSkinFile()
	{
		return L"OpenFileDbClick.xml";
	}

	LPCTSTR OpenFileDbClickDialog::GetWindowClassName(void) const
	{
		return L"OpenFileDbClickDialog";
	}

	CControlUI* OpenFileDbClickDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool OpenFileDbClickDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void OpenFileDbClickDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn) {
				Close(0);				
				return; 
			}
			else if (msg.pSender == m_pOpenBtn)
			{
				Close(1);
			}		
			else if (msg.pSender == m_pSaveBtn)
			{
				Close(2);
			}	
		}
	}

	void OpenFileDbClickDialog::InitWindow()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"OpenFile_Click_close_btn"));
		m_pCancelBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"OpenFile_Click_cancel"));
		m_pOpenBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"OpenFile_Click_open"));
		m_pSaveBtn = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"OpenFile_Click_save"));

		CLabelUI* pName = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OpenFile_Click_name"));	
		if (pName != NULL)
		{
			CHorizontalLayoutUI *pParentHorizontal = static_cast<CHorizontalLayoutUI*>(pName->GetParent());
			pName->SetText(m_fileNode.basic.name.c_str());
			pName->SetToolTip(m_fileNode.basic.name.c_str());
			if(NULL != pParentHorizontal)
			{
				pParentHorizontal->SetToolTip(m_fileNode.basic.name.c_str());
			}
		}

		CLabelUI* pIcon = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OpenFile_Click_icon"));
		if (pIcon != NULL)
		{
			std::wstring str_iconPath;
			if(FILE_TYPE_FILE==m_fileNode.basic.type && thumbEnabled(m_fileNode.basic.name))
			{
				str_iconPath = ThumbMgr::getInstance()->getThumbPath(m_fileNode.userContext->getUserInfoMgr()->getUserId(), m_fileNode.basic.id, 0);
			}
			if(str_iconPath.empty())
			{
				str_iconPath = SkinConfMgr::getInstance()->getIconPath(FILE_TYPE_FILE, m_fileNode.basic.name);
			}
			pIcon->SetBkImage(str_iconPath.c_str());
		}

		CLabelUI* tipLabel = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OpenFile_Click_tip"));
		CLabelUI* downloadTipLabel = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"OpenFile_Click_download_tip"));

		if (m_pOpenBtn && tipLabel && downloadTipLabel)
		{
			m_pOpenBtn->SetVisible(m_isOpen);	
			tipLabel->SetVisible(m_isOpen);
			downloadTipLabel->SetVisible(!m_isOpen);
		}
	}

	LRESULT OpenFileDbClickDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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
}
