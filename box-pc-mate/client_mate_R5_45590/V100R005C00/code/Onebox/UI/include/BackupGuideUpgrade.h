#pragma  once
#include <UIlib.h>
#include <stdint.h>
#include "InILanguage.h"

using namespace DuiLib;

namespace Onebox
{
	class CBackupGuideUpgrade : public WindowImplBase
	{
	public:
		CBackupGuideUpgrade(CPaintManagerUI& parent);

		virtual ~CBackupGuideUpgrade();

		DUI_DECLARE_MESSAGE_MAP();

		void InitWindow();

		CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		virtual LPCTSTR GetWindowClassName() const { return _T("DUI_CBACKLEAD_1_CLS_NAME"); }

		virtual CDuiString GetSkinFolder() { return iniLanguageHelper.GetSkinFolderPath().c_str(); }

		virtual CDuiString GetSkinFile() { return _T("BackupGuide_1.xml"); }

		LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void Notify(DuiLib::TNotifyUI& msg);

	private:
		CPaintManagerUI& paintManager_;
		int32_t m_areaWidth;
	};
}