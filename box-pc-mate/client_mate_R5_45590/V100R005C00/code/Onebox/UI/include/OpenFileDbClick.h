#pragma once

namespace Onebox
{
	class OpenFileDbClickDialog : public WindowImplBase
	{
	public:
		explicit OpenFileDbClickDialog(UserContext* context,UIFileNode& fileNode,bool isOpen);

		virtual ~OpenFileDbClickDialog();  

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);
		
		virtual void Notify(TNotifyUI& msg);
		void InitWindow();
		
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pOpenBtn;
		CButtonUI * m_pSaveBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		UIFileNode m_fileNode;
		bool m_isOpen;
	};
}
