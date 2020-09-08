#pragma once
#include "stdafxOnebox.h"
#include "InILanguage.h"

namespace Onebox
{
class CAboutFrame : public WindowImplBase
{
public:
	CAboutFrame(UserContext* userContext);
	~CAboutFrame(void);
	virtual LPCTSTR GetWindowClassName() const { return _T("DUI_ABOUT_CLS_NAME"); }
	virtual CDuiString GetSkinFolder() { return iniLanguageHelper.GetSkinFolderPath().c_str();}
	virtual CDuiString GetSkinFile() { return _T("about.xml"); }
	virtual void OnFinalMessage( HWND hWnd );
	virtual void InitWindow();

	virtual	bool InitLanguage(CControlUI* control);
public:
	static BOOL isOpenAboutFrm();
	static void delOpenAboutFrm();
	//只需要提供动态设置版本号
	void SetVersionNum(LPCTSTR pstrVerNum);
public:
	virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		LRESULT lRes = 0;

		return  __super::HandleMessage(uMsg, wParam, lParam);
	}
public:
	static CAboutFrame* m_pOwnerAboutFrm;
	CDuiString m_strVersNum;
	UserContext* userContext_;
};
}
