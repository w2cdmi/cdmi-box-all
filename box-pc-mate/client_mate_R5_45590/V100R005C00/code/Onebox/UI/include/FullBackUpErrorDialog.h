#pragma once

#include "NoticeFrame.h"
#include "BackupAllMgr.h"

namespace Onebox
{
	class FullBackUpErrorDialog : public WindowImplBase
	{
	public:
		explicit FullBackUpErrorDialog(UserContext* context);

		virtual ~FullBackUpErrorDialog();  

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
		void addItem(BATaskLocalNode node);
		void ExportErrorList();
		void DeleteBtnClick(DuiLib::TNotifyUI& msg);
	protected:
		UserContext* userContext_;
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pExportBtn;
		CButtonUI *	m_pRetryBtn;
		CButtonUI *	m_pCancelBtn;
		CListUI* list_;
		CLabelUI* m_pCountLab;
		NoticeFrameMgr* m_noticeFrame_;
		int32_t m_errorCount;
	};
}
