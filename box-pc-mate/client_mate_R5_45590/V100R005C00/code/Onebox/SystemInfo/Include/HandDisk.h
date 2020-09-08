#pragma once
#include "SystemInfoDefine.h"

using namespace SystemInfo;

class CHandDisk
{
public:
	CHandDisk();
	~CHandDisk();

	int32_t GetHDInfo( STR_HD_INFOS& strHDInfo );
private:
	bool m_isInitializeSecurity;
};
