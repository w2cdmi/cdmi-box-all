#pragma once

#include "SystemInfoDefine.h"

using namespace SystemInfo;

class CopertingSystem
{
public:
	CopertingSystem();
	~CopertingSystem();

	int32_t GetOperatingSystemInfo( STR_OPERATINGSYSTEM_INFO& strSystemInfo );

private:

};