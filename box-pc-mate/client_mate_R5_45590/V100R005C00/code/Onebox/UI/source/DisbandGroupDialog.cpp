#include "stdafxOnebox.h"
#include "DisbandGroupDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "GroupResMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"


namespace Onebox{

	DisbandGroupDialog::DisbandGroupDialog(UserContext* context,UserGroupNodeInfo& data)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_groupData(data)
	{
	}

	DisbandGroupDialog::~DisbandGroupDialog()
	{
	}

	LPCTSTR DisbandGroupDialog::GetWindowClassName() const 
	{ 
		return _T("DisbandGroupDialog");
	}

	UINT DisbandGroupDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void DisbandGroupDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
	}

	CControlUI* DisbandGroupDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool DisbandGroupDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void DisbandGroupDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn) {
				Close(0);
				return; 
			}
			else if (msg.pSender == m_pOkBtn)
			{// create Group 	
				CEditUI* pContorl = static_cast<CEditUI*>(m_Paintm.FindControl(L"distandGroup_text"));
				if (pContorl == NULL) return;
				std::wstring str = pContorl->GetText().GetData();
				if (str.empty() || str != L"yes")
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_TEAMSPACE_DISBAND_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}

				if (RT_OK == userContext_->getGroupMgr()->DeleteGroup(m_groupData.groupId()))
				{
					Close();
					return;
				}
				else
				{// error
					//...
					Close();
					return;
				}
			}			
		}
		if( msg.sType == _T("return") ) 
		{
			CEditUI* pContorl = static_cast<CEditUI*>(m_Paintm.FindControl(L"distandGroup_text"));
			if (pContorl == NULL) return;
			std::wstring str = pContorl->GetText().GetData();
			if (str != L"yes")  return;

			if (RT_OK == userContext_->getGroupMgr()->DeleteGroup(m_groupData.groupId()))
			{
				Close();
				return;
			}
			else
			{// error
				//...
			}
		}
	}

	void DisbandGroupDialog::Init()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseDisband));
		m_pOkBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnOKDisband));
		m_pCancelBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCancelDisband));
		CLabelUI* pContorlA = static_cast<CLabelUI*>(m_Paintm.FindControl(L"distandGroup_desc_a"));
		CLabelUI* pContorlB = static_cast<CLabelUI*>(m_Paintm.FindControl(L"distandGroup_desc_b"));
		CLabelUI* pContorlC = static_cast<CLabelUI*>(m_Paintm.FindControl(L"distandGroup_desc_c"));
		if (pContorlA != NULL && pContorlB != NULL && pContorlC != NULL)
		{
			pContorlB->SetToolTip(SD::Utility::String::utf8_to_wstring(m_groupData.member_.name()).c_str());	
			pContorlA->SetText(iniLanguageHelper.GetCommonString(DISTANDTEAMSPACE_DESC_START_KEY).c_str());
			pContorlB->SetText(SD::Utility::String::utf8_to_wstring(m_groupData.member_.name()).c_str());
			pContorlC->SetText(iniLanguageHelper.GetCommonString(DESTANDTEAMSPACE_DESC_END_KEY).c_str());		
		}
	}

	void DisbandGroupDialog::OnTimer(UINT nIDEvent)
	{
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_Paintm);
		simpleNotice->RestoreNoticeArea();
		delete simpleNotice;
		simpleNotice=NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	LRESULT DisbandGroupDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_Paintm.Init(m_hWnd);
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(vGroupDisbandXml, (UINT)0, this,&m_Paintm);
		ASSERT(pRoot && "Failed to parse XML");
		m_Paintm.AttachDialog(pRoot);
		m_Paintm.AddNotifier(this);
		Init();

		return 0;
	}

	LRESULT DisbandGroupDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT DisbandGroupDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT DisbandGroupDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT DisbandGroupDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT DisbandGroupDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT DisbandGroupDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
		::ScreenToClient(*this, &pt);

		RECT rcClient;
		::GetClientRect(*this, &rcClient);

		RECT rcCaption = m_Paintm.GetCaptionRect();

		if (-1 == rcCaption.bottom)
		{
			rcCaption.bottom = rcClient.bottom;
		}

		if ( pt.x >= rcClient.left + rcCaption.left && pt.x < rcClient.right - rcCaption.right
			&& pt.y >= rcCaption.top && pt.y < rcCaption.bottom ) 
		{
			CControlUI* pControl = m_Paintm.FindControl(pt);
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


	LRESULT DisbandGroupDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}

	LRESULT DisbandGroupDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}


	LRESULT DisbandGroupDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;
		BOOL bHandled = TRUE;
		switch( uMsg ) {
		case WM_CREATE:        lRes = OnCreate(uMsg, wParam, lParam, bHandled); break;
		case WM_CLOSE:         lRes = OnClose(uMsg, wParam, lParam, bHandled); break;
		case WM_DESTROY:       lRes = OnDestroy(uMsg, wParam, lParam, bHandled); break;
		case WM_NCACTIVATE:    lRes = OnNcActivate(uMsg, wParam, lParam, bHandled); break;
		case WM_NCCALCSIZE:    lRes = OnNcCalcSize(uMsg, wParam, lParam, bHandled); break;
		case WM_NCPAINT:       lRes = OnNcPaint(uMsg, wParam, lParam, bHandled); break;
		case WM_NCHITTEST:     lRes = OnNcHitTest(uMsg, wParam, lParam, bHandled); break;
		case WM_KILLFOCUS:     lRes = OnKillFocus(uMsg, wParam, lParam, bHandled); break; 
		case WM_MOUSELEAVE:	   lRes = OnMouseLeave(uMsg, wParam, lParam, bHandled); break; 
		case WM_TIMER:			OnTimer(wParam);return lRes;
		default:
			bHandled = FALSE;
		}
		if( bHandled ) return lRes;
		if( m_Paintm.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
		return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
	}

}