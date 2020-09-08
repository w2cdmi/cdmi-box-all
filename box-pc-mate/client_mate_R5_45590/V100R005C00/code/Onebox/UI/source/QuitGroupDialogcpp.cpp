#include "stdafxOnebox.h"
#include "QuitGroupDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "NetworkMgr.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "TransTaskMgr.h"
#include "GroupResMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"

namespace Onebox{

	QuitGroupDialog::QuitGroupDialog(UserContext* context,UserGroupNodeInfo& data)
		:userContext_(context)
		,m_pCloseBtn(NULL)
		,m_pOkBtn(NULL)
		,m_pCancelBtn(NULL)
		,m_groupData(data)
	{
	}

	QuitGroupDialog::~QuitGroupDialog()
	{
	}

	LPCTSTR QuitGroupDialog::GetWindowClassName() const 
	{ 
		return _T("CreateGroupDialog");
	}

	UINT QuitGroupDialog::GetClassStyle() const 
	{ 
		return CS_DBLCLKS;
	}

	void QuitGroupDialog::OnFinalMessage(HWND /*hWnd*/) 
	{ 
	}

	void QuitGroupDialog::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			if( msg.pSender == m_pCloseBtn || msg.pSender == m_pCancelBtn) {
				Close(0);
				return; 
			}
			else if (msg.pSender == m_pOkBtn)
			{// quit Group 				
				if(RT_OK == userContext_->getGroupMgr()->DeleteGroupMember(m_groupData.groupId(),m_groupData.id()))
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
	}

	void QuitGroupDialog::Init()
	{  
		m_pCloseBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCloseQuit));
		m_pOkBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnOKQuit));
		m_pCancelBtn = static_cast<CButtonUI*>(m_Paintm.FindControl(vBtnCancelQuit));
	}

	CControlUI* QuitGroupDialog::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool QuitGroupDialog::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT QuitGroupDialog::OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		
		::SetWindowLong(*this, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);

		m_Paintm.Init(m_hWnd);
		CDialogBuilder builder;

		CControlUI *pRoot = builder.Create(vGroupQuitXml, (UINT)0, this,&m_Paintm);
		ASSERT(pRoot && "Failed to parse XML");
		m_Paintm.AttachDialog(pRoot);
		m_Paintm.AddNotifier(this);
		Init();

		return 0;
	}

	LRESULT QuitGroupDialog::OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT QuitGroupDialog::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		bHandled = FALSE;
		return 0;
	}

	LRESULT QuitGroupDialog::OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		if( ::IsIconic(*this) ) bHandled = FALSE;
		return (wParam == 0) ? TRUE : FALSE;
	}

	LRESULT QuitGroupDialog::OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT QuitGroupDialog::OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
	{
		return 0;
	}

	LRESULT QuitGroupDialog::OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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


	LRESULT QuitGroupDialog::OnKillFocus( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}

	LRESULT QuitGroupDialog::OnMouseLeave( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		return 0 ;
	}


	LRESULT QuitGroupDialog::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
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
		default:
			bHandled = FALSE;
		}
		if( bHandled ) return lRes;
		if( m_Paintm.MessageHandler(uMsg, wParam, lParam, lRes) ) return lRes;
		return CWindowWnd::HandleMessage(uMsg, wParam, lParam);
	}

}