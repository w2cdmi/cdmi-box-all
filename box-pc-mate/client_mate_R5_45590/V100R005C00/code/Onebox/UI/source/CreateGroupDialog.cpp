#include "stdafxOnebox.h"
#include "CreateGroupDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "GroupResMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"
#include "NodeControlResMgr.h"
#include "RestClient.h"

namespace Onebox{

	CreateGroupDialog::CreateGroupDialog(UserContext* context)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_name(NULL)
		,m_description(NULL)
	{
	}

	CreateGroupDialog::~CreateGroupDialog()
	{
	}


	LPCTSTR CreateGroupDialog::GetWindowClassName() const 
	{ 
		return _T("CreateGroupDialog");
	}

	UINT CreateGroupDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void CreateGroupDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
	}

	CControlUI* CreateGroupDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool CreateGroupDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void CreateGroupDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn) {
				Close(0);
				return; 
			}
			else if (msg.pSender == m_pOkBtn)
			{
				if (m_name == NULL || m_description == NULL)  return;
				std::wstring name,description;
				name = m_name->GetText().GetData();
				if (name.size() == 0 || name == iniLanguageHelper.GetCommonString(NAMEDESCRIPTION_KEY).c_str())
				{
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_GROUP_EMPTY_NAME_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				if (name.size() > 255)
				{//error
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_GROUP_NAME_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				description = m_description->GetText().GetData();
				if (description.size() > 255)
				{//error
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Warning,MSG_GROUP_DESCRIPTION_SETTING_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				if (name.length() == 0) return;

				//PermissionRole perRole;

				//userContext_->getNodeControlMgr()->getSystemRoleList(perRole);

 				GroupNode node;
				userContext_->getGroupMgr()->CreateGroup(node,SD::Utility::String::wstring_to_utf8(name),
														SD::Utility::String::wstring_to_utf8(description));

				if (node.id() != 0)				
				{
					Close(1);
					return;
				}
				else
				{// error
					SimpleNoticeFrame* simlpeNoticeFrame =  new SimpleNoticeFrame(m_Paintm);					
					simlpeNoticeFrame->Show(Info,MSG_GROUP_NAME_FAIL_KEY);
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
			}			
		}
		if( msg.sType == _T("setfocus") )
		{
			if (msg.pSender == m_name)
			{
				std::wstring text = m_name->GetText().GetData();
				if (text == iniLanguageHelper.GetCommonString(NAMEDESCRIPTION_KEY).c_str())
				{
					m_name->SetText(L"");
				}
			}
		}
		if( msg.sType == _T("killfocus") )
		{
			if (msg.pSender == m_name)
			{
				std::wstring text = m_name->GetText();
				if (text == L"")
				{
					m_name->SetText(iniLanguageHelper.GetCommonString(NAMEDESCRIPTION_KEY).c_str());
				}
			}
		}
	}

	void CreateGroupDialog::Init()
	{  
		m_pCloseBtn		= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseCreate));
		m_pOkBtn		= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnOK));
		m_pCancelBtn	= static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCancel));;
		m_name			= static_cast<CEditUI*>(m_Paintm.FindControl(vRichEditName));

		m_description	= static_cast<CSearchTxtUI*>(m_Paintm.FindControl(vRichEditDescription));

		if (m_name != NULL && m_description != NULL)
		{
			CDuiString nameDescription = iniLanguageHelper.GetCommonString(NAMEDESCRIPTION_KEY).c_str();
			CDuiString descriptionDescription = iniLanguageHelper.GetCommonString(DESCRIPTIONDESCRIPTIONA_KEY).c_str();
			m_name->SetText(nameDescription);
			m_description->setDefaultTxt(descriptionDescription);
		}
	}

	void CreateGroupDialog::OnTimer(UINT nIDEvent)
	{
		SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_Paintm);
		simpleNotice->RestoreNoticeArea();
		delete simpleNotice;
		simpleNotice=NULL;
		::KillTimer(this->GetHWND(),nIDEvent);
	}

	LRESULT CreateGroupDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_Paintm.Init(m_hWnd);
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(vGroupCreateXml, (UINT)0, this,&m_Paintm);
		ASSERT(pRoot && "Failed to parse XML");
		m_Paintm.AttachDialog(pRoot);
		m_Paintm.AddNotifier(this);
		Init();

		return 0;
	}

	LRESULT CreateGroupDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT CreateGroupDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT CreateGroupDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT CreateGroupDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT CreateGroupDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT CreateGroupDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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


	LRESULT CreateGroupDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}

	LRESULT CreateGroupDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}


	LRESULT CreateGroupDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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