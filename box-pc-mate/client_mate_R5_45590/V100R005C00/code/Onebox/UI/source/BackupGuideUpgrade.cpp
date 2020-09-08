#include "BackupGuideUpgrade.h"
#include "ControlNames.h"
#include "Utility.h"
#include "CommonDefine.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(CBackupGuideUpgrade, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MOUSEENTER,Notify)
	DUI_END_MESSAGE_MAP()

	CBackupGuideUpgrade::CBackupGuideUpgrade(CPaintManagerUI& parent):paintManager_(parent)
	{
		m_areaWidth = 720;
	}

	CBackupGuideUpgrade::~CBackupGuideUpgrade()
	{
	}


	void CBackupGuideUpgrade::InitWindow()
	{

	}

	CControlUI* CBackupGuideUpgrade::CreateControl( LPCTSTR pstrClass )
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}


	bool CBackupGuideUpgrade::InitLanguage( CControlUI* control )
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT CBackupGuideUpgrade::OnSysCommand( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		if (wParam == SC_CLOSE)
		{
			Close();
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	void CBackupGuideUpgrade::Notify( DuiLib::TNotifyUI& msg )
	{
		if ( msg.sType == DUI_MSGTYPE_CLICK )
		{
			Close();
		}
	}
}