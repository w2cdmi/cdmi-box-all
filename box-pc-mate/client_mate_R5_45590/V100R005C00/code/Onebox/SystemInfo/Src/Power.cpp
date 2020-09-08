#include "Power.h"

#include "UserContextMgr.h"
#include "UserContext.h"
#include "NotifyMgr.h"
#include "Utility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("CCSystemInfoMgr")
#endif

CPower::CPower()
{
}

CPower::~CPower()
{
}

int32_t CPower::GetPowerInfo( STR_POWER_INFO& strPowerInfo)
{
	SYSTEM_POWER_STATUS sps;	
	GetSystemPowerStatus(&sps);

	SERVICE_INFO(MODULE_NAME, 0, "power status: old status: %d; current status: %d."
		, strPowerInfo.PowerType, sps.ACLineStatus);

	if (strPowerInfo.PowerType != sps.ACLineStatus
		&& POWER_TYPE_OFFLINE == sps.ACLineStatus )
	{
		NOTIFY_PARAM param;
		param.type = NOTIFY_MSG_POWER_CHANGE;
		param.msg1 = SD::Utility::String::type_to_string<std::wstring>(sps.ACLineStatus);
		param.msg2 = SD::Utility::String::type_to_string<std::wstring>(strPowerInfo.PowerType);
		UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL==defaultUserContext) return RT_ERROR;
		defaultUserContext->getNotifyMgr()->notify(param);
	}
	strPowerInfo.PowerType = sps.ACLineStatus;
	strPowerInfo.PowerManageType = sps.BatteryFlag;
	strPowerInfo.PowerLifePercent = sps.BatteryLifePercent;
	strPowerInfo.PowerLifeTime = sps.BatteryLifeTime;
	return 0;
}
