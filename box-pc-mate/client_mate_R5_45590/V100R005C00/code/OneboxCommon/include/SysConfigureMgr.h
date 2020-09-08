#ifndef _ONEBOX_SYSCONFIGURE_MGR_H_
#define _ONEBOX_SYSCONFIGURE_MGR_H_

#include "UserContext.h"
#include <list>

struct SysFilter 
{
	std::wstring nameFilter;
	std::wstring nameStartFilter;
	std::wstring nameEndFilter;

	SysFilter():nameFilter(L"")
		,nameStartFilter(L"")
		,nameEndFilter(L"")
	{
	}
};

class ONEBOX_DLL_EXPORT SysConfigureMgr
{
public:
	virtual ~SysConfigureMgr(){}

	static SysConfigureMgr* create(UserContext* userContext);

	virtual int32_t getBackupDisableList(std::list<std::wstring>& backupDisableList) = 0;

	//�ж��Ƿ��ֹ����Ŀ¼�������ִ�Сд
	virtual bool isBackupDisable(const std::wstring& path) = 0;

	virtual bool isBackupDisableAttr(int32_t attr) = 0;

	virtual SysFilter getSysFilter() = 0;
};

#endif
