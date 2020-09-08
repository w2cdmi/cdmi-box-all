#ifndef __ONEBOX__UPDATEINFO_H_
#define __ONEBOX__UPDATEINFO_H_

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
