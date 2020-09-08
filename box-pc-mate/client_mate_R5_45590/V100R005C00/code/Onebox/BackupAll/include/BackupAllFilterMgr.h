#ifndef _ONEBOX_BACKUP_ALL_FILTERMGR_H_
#define _ONEBOX_BACKUP_ALL_FILTERMGR_H_

#include "UserContext.h"
#include "BackupAllCommon.h"
#include <set>

class BackupAllFilterMgr
{
public:
	virtual ~BackupAllFilterMgr(){}

	static std::auto_ptr<BackupAllFilterMgr> create(UserContext* userContext);

	static std::auto_ptr<BackupAllFilterMgr> create(UserContext* userContext, 
		const std::list<std::wstring>&selectList, const std::list<std::wstring>&filterList);

	virtual void getSelectInfo(const std::wstring& volume, std::set<int64_t>& selectDirs) = 0;

	virtual void flushSelectInfo() = 0;

	//ɾ����ȫȡ��ѡ��Ĵ��̵Ĵ���������Ϣ
	virtual void doDeleteVolumeInfo() = 0;

	virtual void doFilterChange() = 0;
	
	//ͨ�õĹ���״̬��ѯ
	virtual bool isFilter(const std::wstring& path) = 0;

	//�ݹ鴦��ʱ������ʹ�ô˷���
	virtual bool isFilter(const std::wstring& path, bool parentIsFilter) = 0;

};

#endif