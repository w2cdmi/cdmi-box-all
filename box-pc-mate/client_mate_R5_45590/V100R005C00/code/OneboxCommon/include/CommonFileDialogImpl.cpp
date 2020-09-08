#include "UIlib.h"
#include "CommonFileDialog.h"
#include "RenameRichEdit.h"
#include "InILanguage.h"
#include "ErrorConfMgr.h"
#include <boost/thread.hpp>
#include "CommonLoadingFrame.h"
#include "SimpleNoticeFrame.h"


namespace Onebox
{
	using namespace DuiLib;

	class CNameEditUI : public CRichEditUI
	{
	public:
		HRESULT TxSendMessage(UINT msg, WPARAM wparam, LPARAM lparam, LRESULT *plresult) const
		{
			if( m_pTwh ) {
				if( msg == WM_KEYDOWN && TCHAR(wparam) == VK_RETURN ) {
					if ( 0 == GetKeyState(VK_CONTROL) ) {
						if( m_pManager != NULL ) 
							m_pManager->SendNotify((CControlUI*)this, DUI_MSGTYPE_RETURN);
						return S_OK;
					}
				}
				return CRichEditUI::TxSendMessage(msg, wparam, lparam, plresult);
			}
			return S_FALSE;
		}

		void DoEvent(TEventUI& event)
		{
			if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
				if( m_pParent != NULL ) m_pParent->DoEvent(event);
				else CControlUI::DoEvent(event);
				return;
			}

			if( event.Type == UIEVENT_CHAR ) {
				int i = 0;
			}
			if( event.Type == UIEVENT_SETFOCUS ) {			
				m_bFocused = true;
				this->SetFocusBorderColor(0xFF000000);
				this->SetBorderSize(1);
			}
			if( event.Type == UIEVENT_KILLFOCUS )  {
				m_bFocused = false;
				this->SetBorderSize(0);
				this->SetMouseEnabled(false);
			}
			CRichEditUI::DoEvent(event);
		}
	};

	struct CommonFileDialogTreeNode : public CTreeNodeUI
	{
		CommonFileDialogItem data;

		CommonFileDialogTreeNode()
			:isExpanded_(false)
			,isEnableMltiSelect_(true)
			, isRename(false)
			, isNewNode(false)
		{

		}

		void RemoveAll()
		{
			CTreeNodeUI *node = GetChildNode(0);
			while (node)
			{
				RemoveAt(node);
				node = GetChildNode(0);
			}
		}

		bool IsExpanded() const
		{
			return isExpanded_;
		}

		void SetExpand(bool bExpand = true)
		{
			isExpanded_ = bExpand;
		}

		bool IsEnableMultiSelect() const
		{
			return isEnableMltiSelect_;
		}

		void SetEnableMultiSelect(bool bEnableMltiSelect = true)
		{
			isEnableMltiSelect_ = bEnableMltiSelect;
		}

		void SetRename(bool bRename){
			isRename = bRename;
		}

		void SetNewNode(bool bNew){
			isNewNode = bNew;
		}

		bool isNewCreateNode() const
		{
			return isNewNode;
		}

		void DoEvent(TEventUI& event)
		{
			if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
				if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
				else CContainerUI::DoEvent(event);
				return;
			}

			if( event.Type == UIEVENT_DBLCLICK )
			{
				if( IsEnabled() ) {
					m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDBCLICK);
					if (!m_bSelected)
						Select();
					Invalidate();
				}
				return;
			}
			else if (event.Type == UIEVENT_BUTTONDOWN){
				if (isRename){
					if (this->IsSelected()){
						BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
						BOOL bShift = (GetKeyState(VK_SHIFT) & 0x8000);
						if (bCtrl || bShift)
							return;

						CRenameRichEditUI* pEdit = static_cast<CRenameRichEditUI*>(this->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
						if (NULL == pEdit)return;

						RECT rc = pEdit->GetPos();
						if (PtInRect(&rc, event.ptMouse)){
							if (!pEdit->IsFocused()){
								pEdit->SetMouseEnabled(true);
								pEdit->SetFocus();
							}
						}else{
							pEdit->SetMouseEnabled(false);
							this->m_bFocused = false;
						}
					}
					else{
						CTreeNodeUI* pNode = static_cast<CTreeNodeUI*>(this->GetTreeView()->GetItemAt(this->GetTreeView()->GetCurSel()));
						if (pNode)
							pNode->Select(false);
						this->Select();
					}
				}
			}
			else if (event.Type == UIEVENT_SETFOCUS){
				m_bFocused = true;
				return;
			}
			else if (event.Type == UIEVENT_KILLFOCUS){
				m_bFocused = false;
				return;
			}
			
			if (event.Type == UIEVENT_KEYDOWN && IsEnabled()){
				if (event.chKey == VK_RETURN){
					Activate();
					Invalidate();
					this->SetMouseChildEnabled(false);
					this->SetFocus();
					m_pManager->SendNotify(this, DUI_MSGTYPE_RETURN);
					return;
				}
			}

			if (!isEnableMltiSelect_)
			{
				BYTE lpKeyState[256] = {0};
				::GetKeyboardState(lpKeyState);
				lpKeyState[VK_SHIFT] = 0;
				lpKeyState[VK_CONTROL] = 0;
				::SetKeyboardState(lpKeyState);
			}

			CTreeNodeUI::DoEvent(event);
		}

	private:
		bool isExpanded_;
		bool isEnableMltiSelect_;
		bool isRename;
		bool isNewNode;
	};

	static UINT MSG_AUTOEXPAND = WM_USER+10;

	class CommonFileDialog::Impl : public WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		Impl(const std::wstring& xmlFolder, 
			HWND parent, 
			CommonFileDialogItem root, 
			const std::wstring& okButtonName, 
			const std::wstring& wndClsName, 
			const std::wstring& wndName, 
			const std::wstring& title, 
			const std::wstring& titleTooltip)
			:xmlFolder_(xmlFolder)
			,option_(CommonFileDialogOption(CFDO_enable_multi_select|CFDO_ok_button_visible|CFDO_cancel_button_visible))
			,notify_(NULL)
			,treeView_(NULL)
			,isAsync_(true)
			,error_(E_CFD_CANCEL)
			,parent_(parent)
			,root_(root)
			,wndTile_(title)
			,wndTileToolTip_(titleTooltip)
			,okButtonName_(okButtonName)
			,wndClsName_(wndClsName)
			,wndName_(wndName)
			,iControlIndex_(-1)
			,m_hWndTip(NULL)
		{

		}

		virtual ~Impl()
		{
			if (NULL != notify_)
			{
				delete notify_;
				notify_ = NULL;
			}
		}

		void setOption(const CommonFileDialogOption option)
		{
			option_ = option;
		}

		void setNotify(ICommonFileDialogNotify* notify)
		{
			if (NULL != notify_)
			{
				delete notify_;
				notify_ = NULL;
			}
			if (NULL != notify)
			{
				notify_ = notify;
			}
		}

		void setControlAttribute()
		{
			if (L"" != wndTile_)
			{
				CLabelUI *windowTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"CommonFileDialog_label_title"));
				if (NULL != windowTitle)
				{
					windowTitle->SetText(wndTile_.c_str());
				}
			}
			if (L"" != wndTileToolTip_)
			{
				CLabelUI *windowTitle = static_cast<CLabelUI*>(m_PaintManager.FindControl(L"CommonFileDialog_label_title"));
				if (NULL != windowTitle)
				{
					windowTitle->SetToolTip(wndTileToolTip_.c_str());
				}
			}
			if (L"" != okButtonName_)
			{
				CButtonUI *okButton = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_ok"));
				if (NULL != okButton)
				{
					okButton->SetText(okButtonName_.c_str());
				}
			}
			CButtonUI *cancelButton = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_cancel"));
			if (NULL != cancelButton)
			{
				cancelButton->SetVisible((option_&CFDO_cancel_button_visible) > 0);
			}
		}

		void show(ResultHandler handler)
		{
			Create(parent_, wndName_.c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE, 0, 0, 0, 0);
			if (NULL == m_hWnd)
			{
				return;
			}
			setControlAttribute();
			resultHandler_ = handler;
			CenterWindow();
			ShowWindow();
		}

		CommonFileDialogError showModal(ResultHandler handler,int iAction = 0)
		{
			Create(parent_, wndName_.c_str(), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE, 0, 0, 0, 0);
			if (NULL == m_hWnd)
			{
				return E_CFD_INVALID_PARAM;
			}
			setControlAttribute();
			setButtonViseble(iAction);
			resultHandler_ = handler;
			isAsync_ = false;
			CenterWindow();
			(void)ShowModal();
			return error_;
		}

		void setButtonViseble(int iAction)
		{
			CButtonUI  *cancelButton	= static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_cancel"));
			CButtonUI  *okButton		= static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_ok"));
			CControlUI *copyButton		= static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_copy"));
			CControlUI *moveButton		= static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_move"));
			switch (iAction)
			{
				case COMMFILEDIALOG_COPYMOVE:
					{
						cancelButton->SetVisible(true);
						moveButton->SetVisible(true);
						copyButton->SetVisible(true);
						okButton->SetVisible(false);
					}
					break;
				case COMMFILEDIALOG_SAVETOMYFILE:
					{
						cancelButton->SetVisible(true);
						moveButton->SetVisible(false);
						copyButton->SetVisible(false);
						okButton->SetVisible(true);
					}
					break;
				case COMMFILEDIALOG_SAVETOTEAMSPACE:
					{
						cancelButton->SetVisible(true);
						moveButton->SetVisible(false);
						copyButton->SetVisible(false);
						okButton->SetVisible(false);
					}
					break;
				default:
					break;
			}
		}

	public:
		virtual void OnFinalMessage(HWND hWnd)
		{
			WindowImplBase::OnFinalMessage(hWnd);
			if (isAsync_)
			{
				delete this;
			}
		}

		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
		{
			POINT pt; pt.x = GET_X_LPARAM(lParam); pt.y = GET_Y_LPARAM(lParam);
			::ScreenToClient(*this, &pt);

			RECT rcClient = {0,0,0,0};
			::GetClientRect(*this, &rcClient);

			RECT rcCaption = m_PaintManager.GetCaptionRect();

			if (0 < rcCaption.bottom)
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
					clsNames.push_back(_T("cnameeditui"));

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

		virtual void InitWindow()
		{
			treeView_ = static_cast<CTreeViewUI*>(m_PaintManager.FindControl(L"CommonFileDialog_treeView"));
			if (NULL == treeView_ || NULL == notify_)
			{
				return;
			}

			// load root
			CommonFileDialogListResult result;
			if (NULL != root_.get())
			{
				(void)addItem(root_);
			}
			else
			{
				int32_t ret = notify_->listFolder(root_, result);
				if (E_CFD_SUCCESS != ret || result.empty())
				{
					return;
				}

				for (CommonFileDialogListResult::iterator it = result.begin(); it != result.end(); ++it)
				{
					if (!addItem(*it))
					{
						// show error message
						// ...
						continue;
					}
				}
			}			
		}

		virtual CControlUI* CreateControl(LPCTSTR pstrClass)
		{
			if (std::wstring(pstrClass) == L"CommonFileDialogTreeNode")
			{
				return new CommonFileDialogTreeNode;
			}	

			if (std::wstring(pstrClass) == L"NameEdit")
			{
				return new CNameEditUI;
			}

			if (std::wstring(pstrClass) == L"MyFilesRichEditUI")
			{
				return new CRenameRichEditUI;
			}
			return NULL;
		}

		virtual bool InitLanguage(CControlUI* control)
		{
			return true;
		}

		virtual LPCTSTR GetWindowClassName(void) const
		{
			return wndClsName_.c_str();
		}

		virtual CDuiString GetSkinFolder()
		{
			return xmlFolder_.c_str();
		}

		virtual CDuiString GetSkinFile()
		{
			return L"CommonFileDialogFrame.xml";
		}

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
		{
			if (MSG_AUTOEXPAND == uMsg)
			{
				CommonFileDialogTreeNode *node = (CommonFileDialogTreeNode*)wParam;
				if (NULL != node)
				{
					node->GetManager()->SendNotify(node, DUI_MSGTYPE_ITEMDBCLICK);
				}				
				return 0;
			}
			else if (uMsg == WM_TIMER){
				if ((UINT)UI_TIMERID::SIMPLENOTICE_TIMERID == wParam ){
					OnTimer((UINT)wParam);
					return 0;
				}

			}
			return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
		}

		void OnTimer(UINT nIDEvent){
			::KillTimer(this->GetHWND(),nIDEvent);

			if (UI_TIMERID::SIMPLENOTICE_TIMERID == nIDEvent)
			{
				SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
				simpleNotice->RestoreNoticeArea();
				delete simpleNotice;
				simpleNotice=NULL;
			}
		}
		void Notify(TNotifyUI& msg)
		{
			CDuiString name = msg.pSender->GetName();
			if (msg.sType == DUI_MSGTYPE_RICHEDIT_CHARACTER_EXCCED)
			{
				std::wstring strText = L"";
				strText = iniLanguageHelper.GetCommonString(COMMOM_TOOLTIP_MAXCHAR_KEY).c_str();

				RECT rt={GET_X_LPARAM(msg.wParam),GET_X_LPARAM(msg.lParam),GET_Y_LPARAM(msg.wParam),GET_Y_LPARAM(msg.lParam)};

				return showTip(rt, strText);
			}
			if (msg.sType == DUI_MSGTYPE_RICHEDIT_INVALID_CHARACTER)
			{
				std::wstring strText = L"";
				strText = iniLanguageHelper.GetCommonString(COMMOM_TOOLTIP_CHARACTER_KEY).c_str();
				strText += L"(";
				CRenameRichEditUI* renameRichEdit = static_cast<CRenameRichEditUI*>(msg.pSender);
				if( NULL == renameRichEdit ) return;

				strText += renameRichEdit->GetLimitChar();
				strText += L")";

				RECT rt={GET_X_LPARAM(msg.wParam),GET_X_LPARAM(msg.lParam),GET_Y_LPARAM(msg.wParam),GET_Y_LPARAM(msg.lParam)};

				return showTip(rt, strText);
			}
			if(name == L"btn_closeNotice")
			{ 
				SimpleNoticeFrame* simpleNotice = new SimpleNoticeFrame(m_PaintManager);
				simpleNotice->RestoreNoticeArea();
				delete simpleNotice;
				simpleNotice=NULL;
				return; 
			}

			if (msg.sType == DUI_MSGTYPE_RICHEDIT_RESTOR_NORMAL)
			{
				return hideTip();
			}
			return WindowImplBase::Notify(msg);
		}

		void showTip(const RECT rt, const std::wstring& strText)
		{
			(void)memset_s(&m_InfoTip, sizeof(m_InfoTip), 0, sizeof(m_InfoTip));
			m_InfoTip.cbSize = sizeof(m_InfoTip);
			m_InfoTip.hwnd = m_hWnd;
			m_InfoTip.uFlags = TTF_CENTERTIP;	
			m_InfoTip.rect = rt;

			m_InfoTip.lpszText = (LPWSTR)strText.c_str();

			if (NULL == m_hWndTip){
				m_hWndTip = ::CreateWindowEx(0, TOOLTIPS_CLASS, NULL, WS_POPUP | TTS_NOPREFIX | TTS_ALWAYSTIP,
					CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
					m_hWnd, NULL, CPaintManagerUI::GetInstance(), NULL);
				::SendMessage(m_hWndTip, TTM_ADDTOOL, 0, (LPARAM) &m_InfoTip);
			}

			::SendMessage(m_hWndTip, TTM_SETTOOLINFO, 0, (LPARAM) &m_InfoTip);
			::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, TRUE, (LPARAM) &m_InfoTip);
		}

		void hideTip()
		{
			if( m_hWndTip != NULL )
				::SendMessage(m_hWndTip, TTM_TRACKACTIVATE, FALSE, (LPARAM) &m_InfoTip);
		}

		int GetControlIndex()
		{
			return iControlIndex_;
		}

		void OnClick(TNotifyUI& msg)
		{
			if ( NULL == msg.pSender ) return;
			if ( NULL == msg.pSender->GetParent() ) return;

			CDuiString name = msg.pSender->GetName();
			if (name == L"CommonFileDialog_button_close")
			{
				Close();
				error_ = E_CFD_CANCEL;
			}
			else if (name == L"CommonFileDialog_button_ok")
			{
				ResultDealWith();
				Close();
				error_ = E_CFD_SUCCESS;
			}
			else if (name == L"CommonFileDialog_button_cancel")
			{
				Close();
				error_ = E_CFD_CANCEL;
			}
			else if (name == L"CommonFileDialogTreeViewItem_expand")
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(msg.pSender->GetParent()->GetParent());
				if (NULL == node)
				{
					return;
				}
				if (node->IsExpanded())
				{
					node->RemoveAll();
					node->SetExpand(false);
					return;
				}

				loadChildren(node);
				node->SetExpand(true);
			}
			else if(name == L"CommonFileDialog_button_copy")
			{
				ResultDealWith();
				iControlIndex_ = 0;
				Close();
				error_ = E_CFD_SUCCESS;
			}
			else if(name == L"CommonFileDialog_button_move")
			{		
				ResultDealWith();
				iControlIndex_ = 1;
				Close();
				error_ = E_CFD_SUCCESS;
			}
			else if (name == L"CommonFileDialog_button_create")
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(treeView_->GetItemAt(treeView_->GetCurSel()));
				if (NULL == node)
				{
					return;
				}
				if (node->data && node->data->notify)
				{
					if (!node->IsExpanded())
					{
						node->GetManager()->SendNotify(node, DUI_MSGTYPE_ITEMDBCLICK);
					}
					CommonFileDialogItem item = node->data->notify->createItem(node->data);
					if (NULL == item.get())
					{
						return;
					}
					if (addItem(item, node, true))
					{
						int index = node->GetTreeIndex();
						treeView_->SelectItem(index+1, true);
						CommonFileDialogTreeNode *newNode = static_cast<CommonFileDialogTreeNode*>(treeView_->GetItemAt(index+1));
						if (NULL != newNode)
						{
							newNode->SetNewNode(true);
							newNode->Select(true);
							newNode->Invalidate();
							CRenameRichEditUI *name = static_cast<CRenameRichEditUI*>(newNode->FindSubControl(L"CommonFileDialogTreeViewItem_name"));
							if (NULL != name)
							{
								name->SetMouseEnabled(true);
								name->SetFocus();
								name->SetSelAll();
							}
						}						
					}					
				}
			}
		}

		void ResultDealWith()
		{
			if(0 != result_.size())
			{
				result_.clear();
			}
			for (int i = 0; i < treeView_->GetCount(); ++i)
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(treeView_->GetItemAt(i));
				if (NULL == node)
				{
					return;
				}
				if (node->IsSelected())
				{
					CRenameRichEditUI *name = static_cast<CRenameRichEditUI*>(node->FindSubControl(L"CommonFileDialogTreeViewItem_name"));
					if (NULL == name)
					{
						return;
					}
					if (_tcsicmp(node->data->name.c_str(),name->GetText().GetData())!=0 && node->data->notify)
					{   
						CommonFileDialogTreeNode *parent = static_cast<CommonFileDialogTreeNode*>(node->GetParentNode());
						if (NULL == parent)
						{
							return;
						}
						node->data->notify->renameItem(node->data,  name->GetText().GetData());
						node->data->name = name->GetText().GetData();
						node->data->path =  parent->data->path+ PATH_DELIMITER +node->data->name;
					}
					result_.push_back(node->data);
				}
			}
			resultHandler_(result_);
		}

		void OnItemDbClick(TNotifyUI& msg)
		{
			std::wstring className = msg.pSender->GetClass();
			if (className == L"TreeNodeUI")
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(msg.pSender);
				if (NULL == node)
				{
					return;
				}
				if (node->IsExpanded())
				{
					node->RemoveAll();
					node->SetExpand(false);
					return;
				}
				CommonLoadingFrame::create(xmlFolder_,m_hWnd, boost::bind(
				&CommonFileDialog::Impl::loadChildren, 
				this, 
				boost::ref(node)));
				node->SetExpand(true);

				CommonFileDialogTreeNode *curNode = static_cast<CommonFileDialogTreeNode*>(msg.pSender);
				if (NULL == curNode)
				{
					return;
				}
				CControlUI *createButton = m_PaintManager.FindControl(L"CommonFileDialog_button_create");
				if (NULL == createButton)
				{
					return;
				}
				if ((option_&CFDO_enable_multi_select) > 0)
				{
					for (int i = 0; i < treeView_->GetCount(); ++i)
					{
						CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(treeView_->GetItemAt(i));
						if (NULL == node)
						{
							return;
						}
						if (node->IsSelected() && node != curNode)
						{
							createButton->SetVisible(false);
							return;
						}
					}
				}
				if (curNode->IsSelected() && curNode->data && curNode->data->notify)
				{
					createButton->SetVisible(curNode->data->notify->customButtonVisible(curNode->data, CFDCB_CREATE));
				}
			}
		}

		void OnItemClick(TNotifyUI& msg)
		{
			std::wstring className = msg.pSender->GetClass();
			if (className == L"TreeNodeUI")
			{
				CommonFileDialogTreeNode *curNode = static_cast<CommonFileDialogTreeNode*>(msg.pSender);
				if (NULL == curNode)
				{
					return;
				}

				CControlUI *createButton = m_PaintManager.FindControl(L"CommonFileDialog_button_create");
				if (NULL == createButton)
				{
					return;
				}
				if ((option_&CFDO_enable_multi_select) > 0)
				{
					for (int i = 0; i < treeView_->GetCount(); ++i)
					{
						CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(treeView_->GetItemAt(i));
						if (NULL == node)
						{
							return;
						}
						if (node->IsSelected() && node != curNode)
						{
							createButton->SetVisible(false);
							return;
						}
					}
				}
				if (curNode->IsSelected() && curNode->data && curNode->data->notify)
				{
					createButton->SetVisible(curNode->data->notify->customButtonVisible(curNode->data, CFDCB_CREATE));
				}

				CControlUI* copyBtn = m_PaintManager.FindControl(L"CommonFileDialog_button_copy");
				if (NULL != copyBtn && curNode->IsSelected() && curNode->data && curNode->data->notify)
				{
					copyBtn->SetVisible(curNode->data->notify->customButtonVisible(curNode->data, CFDCB_COPY));
				}

				CControlUI* moveBtn = m_PaintManager.FindControl(L"CommonFileDialog_button_move");
				if (NULL != moveBtn && curNode->IsSelected() && curNode->data && curNode->data->notify)
				{
					moveBtn->SetVisible(curNode->data->notify->customButtonVisible(curNode->data, CFDCB_MOVE));
				}

				CButtonUI *okButton = static_cast<CButtonUI*>(m_PaintManager.FindControl(L"CommonFileDialog_button_ok"));
				if (NULL != okButton && curNode->data && curNode->data->notify)
				{
					okButton->SetVisible(curNode->data->notify->customButtonVisible(curNode->data, CFDCB_OK));
				}
			}
		}

		void OnKillFocus(TNotifyUI& msg)
		{
			if( NULL == msg.pSender ) return;
			if( NULL == msg.pSender->GetParent() ) return;
			if( NULL == msg.pSender->GetParent()->GetParent() ) return;

			if (std::wstring(msg.pSender->GetName()) == L"CommonFileDialogTreeViewItem_name")
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (NULL == node)
				{
					return;
				}
				// rename
				CRenameRichEditUI *name = static_cast<CRenameRichEditUI*>(node->FindSubControl(L"CommonFileDialogTreeViewItem_name"));
				if (NULL == name)
				{
					return;
				}
				if (_tcsicmp(node->data->name.c_str(),name->GetText().GetData())!=0 && node->data->notify)
				{
					int32_t result = node->data->notify->renameItem(node->data, msg.pSender->GetText().GetData());
					if(E_CFD_SUCCESS == result)
					{
						name->SetText(msg.pSender->GetText().GetData());
						name->SetToolTip(msg.pSender->GetText().GetData());
					}
					else
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(m_PaintManager);
						if(NULL==simlpeNoticeFrame) return;
						if(node->isNewCreateNode())
						{
							CommonFileDialogTreeNode *parentNode = static_cast<CommonFileDialogTreeNode*>(node->GetParentNode());
							if (NULL == parentNode)
							{
								delete simlpeNoticeFrame;
								simlpeNoticeFrame = NULL;
								return;
							}

							if(E_CFD_SUCCESS == node->data->notify->removeItem(node->data))
							{
								parentNode->RemoveAt(node);
							}
							simlpeNoticeFrame->Show(Error, MSG_CREATEDIR_FAILED_KEY);
						}
						else
						{
							simlpeNoticeFrame->Show(Error, MSG_RENAME_FAILED_KEY);
						}
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}
				}
			}
		}

		void OnReturn(TNotifyUI& msg)
		{
			if( NULL == msg.pSender ) return;
			if( NULL == msg.pSender->GetParent() ) return;
			if( NULL == msg.pSender->GetParent()->GetParent() ) return;

			if (std::wstring(msg.pSender->GetName()) == L"CommonFileDialogTreeViewItem_name")
			{
				CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(msg.pSender->GetParent()->GetParent()->GetParent());
				if (NULL == node)
				{
					return;
				}
				node->SetFocus();
			}
		}
		
		void autoExpandNode(CommonFileDialogTreeNode *node)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(1000));
	        PostMessage(MSG_AUTOEXPAND, (WPARAM)node, NULL);
		}

	private:
		bool addItem(const CommonFileDialogItem& item, CommonFileDialogTreeNode *parent = NULL, bool addToHeader = false)
		{
			if ((option_&CFDO_only_show_folder) && (item->type == CFDFT_FILE))
			{
				return true;
			}
			CDialogBuilder builder;
			CommonFileDialogTreeNode *node = static_cast<CommonFileDialogTreeNode*>(builder.Create(L"CommonFileDialogTreeViewItem.xml", L"", this, &m_PaintManager, NULL));
			if (NULL == node)
			{
				return false;
			}
			node->data = item;

			// set name
			CLabelUI *name = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, L"CommonFileDialogTreeViewItem_name"));
			if (NULL == name)
			{
				return false;
			}
			name->SetText(node->data->name.c_str());
			name->SetToolTip(node->data->name.c_str());
			name->SetAttribute(L"endellipsis",L"true");

			if (item->notify)
			{
				node->SetRename(item->notify->isRename(item));
			}

			CHorizontalLayoutUI *pHLayout = static_cast<CHorizontalLayoutUI*>(name->GetParent());
			if (NULL == pHLayout)
			{
				return false;
			}
			pHLayout->SetToolTip(node->data->name.c_str());

			// set icon
			CLabelUI *icon = static_cast<CLabelUI*>(m_PaintManager.FindSubControlByName(node, L"CommonFileDialogTreeViewItem_icon"));
			if (NULL == icon)
			{
				return false;
			}
			if (item->notify)
			{
				icon->SetBkImage(item->notify->loadIcon(item).c_str());
			}

			// set multi select
			node->SetEnableMultiSelect((option_&CFDO_enable_multi_select)>0);

			if (NULL == parent)
			{
				if (!treeView_->Add(node))
				{
					return false;
				}
			}
			else
			{
				if (!addToHeader)
				{
					if (!parent->Add(node))
					{
						return false;
					}
				}
				else
				{
					if (!parent->AddAt(node, -1))
					{
						return false;
					}
				}
			}

			// set folder icon
			if (CFDFT_FILE == node->data->type)
			{
				node->SetVisibleFolderBtn(false);
				RECT padding = node->GetPadding();
				padding.left += node->GetFolderButton()->GetFixedWidth();
				node->SetPadding(padding);
			}

			if (node->data->autoExpand)
			{
				node->Select(true);
				boost::thread(boost::bind(&Impl::autoExpandNode, this, node));
			}

			return true;
		}

		void loadChildren(CommonFileDialogTreeNode *parent)
		{
			if (NULL == parent || NULL == parent->data || NULL == parent->data->notify)
			{
				return;
			}
			CommonFileDialogListResult result;
			int32_t ret = parent->data->notify->listFolder(parent->data, result);
			if (E_CFD_SUCCESS != ret || result.empty())
			{
				return;
			}

			for (CommonFileDialogListResult::iterator it = result.begin(); it != result.end(); ++it)
			{
				if (!addItem(*it, parent))
				{
					// show error message
					// ...
					continue;
				}
			}
		}

	private:
		std::wstring xmlFolder_;
		CommonFileDialogOption option_;
		ICommonFileDialogNotify* notify_;
		CTreeViewUI *treeView_;
		CommonFileDialogListResult result_;
		bool isAsync_;
		ResultHandler resultHandler_;
		CommonFileDialogError error_;
		HWND parent_;
		CommonFileDialogItem root_;
		std::wstring wndTile_;
		std::wstring wndTileToolTip_;
		std::wstring okButtonName_;
		std::wstring wndClsName_;
		std::wstring wndName_;
		int iControlIndex_;
		HWND		m_hWndTip;
		TOOLINFO	m_InfoTip;
		//CWndShadow m_WndShadow;
	};

	DUI_BEGIN_MESSAGE_MAP(CommonFileDialog::Impl,CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK, OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMDBCLICK, OnItemDbClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_ITEMCLICK, OnItemClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_KILLFOCUS, OnKillFocus)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_RETURN, OnReturn)
	DUI_END_MESSAGE_MAP()

	CommonFileDialogPtr CommonFileDialog::createInstance(const std::wstring& xmlFolder, 
		HWND parent, 
		CommonFileDialogItem root, 
		const std::wstring& okButtonName, 
		const std::wstring& wndClsName, 
		const std::wstring& wndName, 
		const std::wstring& title, 
		const std::wstring& titleTooltip)
	{
		return CommonFileDialogPtr(new CommonFileDialog(xmlFolder, parent, root, okButtonName, wndClsName, wndName, title, titleTooltip));
	}

	CommonFileDialog::CommonFileDialog(const std::wstring& xmlFolder, 
		HWND parent, 
		CommonFileDialogItem root, 
		const std::wstring& okButtonName, 
		const std::wstring& wndClsName, 
		const std::wstring& wndName, 
		const std::wstring& title, 
		const std::wstring& titleTooltip)
	{
		try
		{
			impl_.reset(new Impl(xmlFolder, parent, root, okButtonName, wndClsName, wndName, title, titleTooltip));
		}
		catch(...) {}
	}

	CommonFileDialog::~CommonFileDialog()
	{

	}

	void CommonFileDialog::setOption(const CommonFileDialogOption option)
	{
		return impl_->setOption(option);
	}

	void CommonFileDialog::setNotify(ICommonFileDialogNotify* notify)
	{
		return impl_->setNotify(notify);
	}

	void CommonFileDialog::show(ResultHandler handler)
	{
		return impl_->show(handler);
	}

	CommonFileDialogError CommonFileDialog::showModal(ResultHandler handler,int iAction)
	{
		return impl_->showModal(handler,iAction);
	}

	int CommonFileDialog::GetControlIndex()
	{
		return impl_->GetControlIndex();
	}
}
