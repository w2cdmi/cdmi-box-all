#ifndef _ONEBOX_UPDATE_GENERAL_H_
#define _ONEBOX_UPDATE_GENERAL_H_
#include "OneboxExport.h"
#include <string>

class CUpdateGeneralImpl;
class UserContext;

class CUpdateGeneral
{
public:
	CUpdateGeneral(const std::wstring& oldversion, const std::wstring& newversion, UserContext * usercontext);
	virtual ~CUpdateGeneral();

public:
	bool UpdateDB();

private:
	CUpdateGeneralImpl* m_impl;
};

#endif