#pragma once
#include "SystemInfoExport.h"
#include "SystemInfoDefine.h"

using namespace SystemInfo;

class SYSTEMINFO_DLL_EXPORT CCSystemInfoMgr
{
public:
	CCSystemInfoMgr(){}
	virtual ~CCSystemInfoMgr( );

	static CCSystemInfoMgr*  Create();
	
	virtual void Release() = 0;
	virtual STR_OPERATINGSYSTEM_INFO& GetOperatingSystemInfo() = 0;
	virtual STR_POWER_INFO& GetPowerInfo() = 0;
	virtual STR_PROCESSOR_INFO& GetProcessorInfo() = 0;
	virtual STR_HD_INFOS& GetHDInfo() = 0;
	virtual SHARE_STR_NETWORK_INFO_VECTOR& GetNetworkInfo() = 0;
	virtual float GetNetworkUseRate(int ComboIndex) = 0;
	virtual BYTE GetMachineType() = 0;

protected:
	static CCSystemInfoMgr* m_instance_;
};

