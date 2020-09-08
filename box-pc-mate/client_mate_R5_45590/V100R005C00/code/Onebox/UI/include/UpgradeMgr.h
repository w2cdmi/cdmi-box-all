#pragma once
#include "StdAfx.h"
#include "resource.h"
#include "IrregularWindow.h"
#include "RestClient.h"
#include "NetworkMgr.h"
#include "CommonDefine.h"

#define  UPGRADEFOLDER (L"UpgradeOneboxBackUp")
#define  INIFILE (L"UpgradeOneboxBackUp.ini")
#define ZIPFILE (L"OneboxBackUp.zip")

#define  CONF_VERSION_SECTION (L"VERSION")
#define  CONF_VERSION_KEY (L"Version")
#define  CONF_UPDATE_TYPE_KEY (L"UpdateType")
#define  CONF_UPDATE_MSG_CN_KEY (L"UpdateMsg_CN")
#define  CONF_UPDATE_MSG_EN_KEY (L"UpdateMsg_EN")
#define  CONF_UPDATE_MSG_KEY (L"UpdateMsg")

#define  MSGSEPARATE (L"|")

#define VERSION_REG_NAME (L"MainVersion")

namespace Onebox
{
	enum UpgradeType
	{
		Normal,
		Force
	};

	class UpgradeMgr
	{
	public:
		virtual ~UpgradeMgr(){};

		static UpgradeMgr* getInstance(UserContext*  userContext, HWND parent);

		virtual void Run() = 0;

	private:
		static std::auto_ptr<UpgradeMgr> instance_;
	};
}











