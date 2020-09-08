#pragma once

#include "NoticeFrame.h"

namespace Onebox
{
	class TransTaskShowErrorDialog : public WindowImplBase
	{
	public:
		explicit TransTaskShowErrorDialog(UserContext* context,std::wstring& id,FILE_TYPE fileType);

		virtual ~TransTaskShowErrorDialog();  

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		virtual void Notify(TNotifyUI& msg);
		void InitWindow();

		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
	private:
		void InitData();
		void addItem(const AsyncTransDetailNode& node, const std::wstring& rootpath);
		void itemDeleteClick(TNotifyUI& msg);
		void ExportErrorList();

	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pOkBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		CListUI* list_;
		std::wstring m_id;
		FILE_TYPE m_fileType;
		CButtonUI *	m_pRetryBtn;
		CButtonUI * m_pExportBtn;
		CLabelUI* m_pCountLab;
		NoticeFrameMgr* m_noticeFrame_;
		int32_t m_errorCount;
	};
}
