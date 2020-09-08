#include "stdafxOnebox.h"
#include "AboutFrame.h"
#include "UserContext.h"
#include "UserInfoMgr.h"
#include "UserInfo.h"
#include "NetworkMgr.h"
#include "DialogBuilderCallbackImpl.h"

namespace Onebox
{
CAboutFrame * CAboutFrame::m_pOwnerAboutFrm =  NULL;
CAboutFrame::CAboutFrame(UserContext* userContext):userContext_(userContext)
{
	CAboutFrame::m_pOwnerAboutFrm = this;
	m_strVersNum = _T("0.0.0.1");
}


CAboutFrame::~CAboutFrame(void)
{
	m_pOwnerAboutFrm = NULL;
}


BOOL CAboutFrame::isOpenAboutFrm()
{
	return m_pOwnerAboutFrm != NULL;
}

void CAboutFrame::delOpenAboutFrm()
{
	if (m_pOwnerAboutFrm != NULL)
	{
		m_pOwnerAboutFrm->Close(1);
		
	}
}

void CAboutFrame::SetVersionNum(LPCTSTR pstrVerNum)
{
	m_strVersNum = pstrVerNum;
}

 void CAboutFrame::OnFinalMessage( HWND hWnd )
{ 
	DUI__Trace(_T("[%d] - OnFinalMessage"), this);
	__super::OnFinalMessage(hWnd);
	delete this;
}

void CAboutFrame::InitWindow()
{
	CLabelUI* pVersionNum = static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("label_ver_num")));
	if (NULL != pVersionNum)
	{
		pVersionNum->SetText(m_strVersNum);
	}
	
	CRichEditUI* pPrivacyStatement	= static_cast<CRichEditUI*>(m_PaintManager.FindControl(_T("label_privacy_statement")));
	CLabelUI* pPowerBy				= static_cast<CLabelUI*>(m_PaintManager.FindControl(_T("label_powered_by")));
	if (NULL != pPowerBy)
	{
		pPowerBy->SetText(iniLanguageHelper.GetFrameName(ABOUT_POWERED_BY).c_str());
	}
	if (NULL != pPrivacyStatement)
	{
		DeclarationInfo declareInfo;
		MAKE_CLIENT(client);
		client().getDeclaration("pc",declareInfo);
		std::string		showText	= declareInfo.declarationText;
		std::wstringstream	text;
		text<<iniLanguageHelper.GetFrameName(ABOUT_PRIVACY_STATMENT).c_str();
		text<<SD::Utility::String::utf8_to_wstring(showText);		
		pPrivacyStatement->SetText(text.str().c_str());
	}

}

bool CAboutFrame::InitLanguage(CControlUI* control)
{
	return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
}
}