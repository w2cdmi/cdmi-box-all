#include "stdafxOnebox.h"
#include "QueryGroupDialog.h"
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

	QueryGroupDialog::QueryGroupDialog(UserContext* context,UserGroupNodeInfo& data)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_groupData(data)
		,m_pName(NULL)
	    ,m_pDesc(NULL)
	{
	}

	QueryGroupDialog::~QueryGroupDialog()
	{
	}

	LPCTSTR QueryGroupDialog::GetWindowClassName() const 
	{ 
		return _T("QueryGroupDialog");
	}

	UINT QueryGroupDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void QueryGroupDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
	}

	CControlUI* QueryGroupDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool QueryGroupDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void QueryGroupDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn) {
				Close();
				return; 
			}
			else if (msg.pSender == m_pOkBtn)
			{
				if (m_groupData.groupRole() == "admin")
				{
					std::wstring name,description;
					name = m_pName->GetText().GetData();
					description = m_pDesc->GetText().GetData();
					if (name.length() == 0) return;
					if (name.size() > 100)
					{//error
						SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
						simlpeNoticeFrame->Show(Warning,MSG_TEAMSPACE_NAME_SETTING_KEY);
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}
					if (name == SD::Utility::String::string_to_wstring(m_groupData.member_.name()) &&
						description == SD::Utility::String::string_to_wstring(m_groupData.member_.description())) 
					{
						return;
					}
					if (description.size() > 500)
					{//error
						SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
						simlpeNoticeFrame->Show(Warning,MSG_TEAMSPACE_DESCRIPTION_SETTING_KEY);
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}
							
					if (RT_OK == userContext_->getGroupMgr()->UpdateGroup(m_groupData.groupId(),SD::Utility::String::wstring_to_utf8(name),SD::Utility::String::wstring_to_utf8(description),"","")) 	
					{
						Close();
						return;
					}
				}				
				Close();
			}			
		}
	}

	void QueryGroupDialog::Init()
	{  
		m_pCloseBtn		= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseLook));
		m_pOkBtn		= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnOKLook));
		m_pCancelBtn	= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCancelLook));

		m_pName = static_cast<CEditUI*>(m_Paintm.FindControl(L"lookGroup_nameCreate"));
		if (m_pName != NULL)
		{
			m_pName->SetText(SD::Utility::String::utf8_to_wstring(m_groupData.member_.name()).c_str());
			if (!(m_groupData.groupRole() == "admin"))
			{
				m_pName->SetEnabled(false);
				m_pName->SetTextColor(0x999999);
			}
		}

		m_pDesc = static_cast<CRichEditUI*>(m_Paintm.FindControl(L"lookGroup_descriptionCreate"));
		if (m_pDesc != NULL)
		{
			m_pDesc->SetText(SD::Utility::String::utf8_to_wstring(m_groupData.member_.description()).c_str());
			if (!(m_groupData.groupRole() == "admin"))
			{
				m_pDesc->SetEnabled(false);
				m_pDesc->SetTextColor(0x999999);
			}
		}
	}

	void QueryGroupDialog::OnTimer(UINT nIDEvent)
	{
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_Paintm);
		simpleNotice->RestoreNoticeArea();
		delete simpleNotice;
		simpleNotice=NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}


	LRESULT QueryGroupDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_Paintm.Init(m_hWnd);
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(vGroupLookXml, (UINT)0, this,&m_Paintm);
		ASSERT(pRoot && "Failed to parse XML");
		m_Paintm.AttachDialog(pRoot);
		m_Paintm.AddNotifier(this);
		Init();

		return 0;
	}

	LRESULT QueryGroupDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT QueryGroupDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT QueryGroupDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT QueryGroupDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT QueryGroupDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT QueryGroupDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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


	LRESULT QueryGroupDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}

	LRESULT QueryGroupDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}


	LRESULT QueryGroupDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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
		case WM_TIMER:	
			{
				if ((UINT)UI_TIMERID::SIMPLENOTICE_TIMERID == wParam)
				{
					OnTimer(wParam);
				}
				else
				{
					bHandled = FALSE;
				}
				break;
			}
		default:
			bHandled = FALSE;
		}
		if( bHandled ) return lRes;
		if( m_Paintm.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
		return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
	}

}