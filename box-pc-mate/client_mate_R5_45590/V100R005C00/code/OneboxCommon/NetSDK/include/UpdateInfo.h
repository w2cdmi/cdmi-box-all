/******************************************************************************
Description  : ”√ªß Ù–‘
*******************************************************************************/
#ifndef _UPDATEINFO_H_
#define _UPDATEINFO_H_

#include "CommonValue.h"

struct UpdateInfo
{
    std::string versionInfo;
	std::string downloadUrl;
	UpdateInfo():versionInfo(""), downloadUrl("")
	{
	}
};

#endif
