#include "BackupGuideInstall.h"
#include "ControlNames.h"
#include "Utility.h"
#include "CommonDefine.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "DialogBuilderCallbackImpl.h"

namespace Onebox
{
	DUI_BEGIN_MESSAGE_MAP(CBackupGuideInstall, CNotifyPump)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_CLICK,OnClick)
		DUI_ON_MSGTYPE(DUI_MSGTYPE_MOUSEENTER,Notify)
	DUI_END_MESSAGE_MAP()

	CBackupGuideInstall::CBackupGuideInstall(CPaintManagerUI& parent):paintManager_(parent)
	{
		m_areaWidth = 720;
	}

	CBackupGuideInstall::~CBackupGuideInstall()
	{
	}

	void CBackupGuideInstall::InitWindow()
	{

	}

	CControlUI* CBackupGuideInstall::CreateControl( LPCTSTR pstrClass )
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool CBackupGuideInstall::InitLanguage( CControlUI* control )
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	LRESULT CBackupGuideInstall::OnSysCommand( UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled )
	{
		if (wParam == SC_CLOSE)
		{
			Close();
			return 0;
		}

		return WindowImplBase::OnSysCommand(uMsg,wParam,lParam,bHandled);
	}

	void CBackupGuideInstall::Notify( DuiLib::TNotifyUI& msg )
	{
		if ( msg.sType == DUI_MSGTYPE_CLICK )
		{
			if( msg.pSender && msg.pSender->GetName() == ControlNames::BACKUPGUIDE_CLOSE ) Close(IDCLOSE);
			if( msg.pSender && msg.pSender->GetName() == ControlNames::BACKUPGUIDE_OK ) Close(IDOK);
		}
	}
}