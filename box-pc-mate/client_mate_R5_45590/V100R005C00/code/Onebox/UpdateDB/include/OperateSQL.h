#ifndef  _ONEBOX_OPERATESQL_H_
#define  _ONEBOX_OPERATESQL_H_

#include <string>

class OperateSQLImpl;
class  UserContext;

class OperateSQLMgr
{
public:
	OperateSQLMgr(UserContext* usercontext);
	virtual ~OperateSQLMgr();
public:
	bool runCommand(std::wstring userID, std::wstring param);

private:
	OperateSQLImpl* m_impl;
};


#endif

