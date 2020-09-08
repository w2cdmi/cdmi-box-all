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

	//删除完全取消选择的磁盘的传输任务信息
	virtual void doDeleteVolumeInfo() = 0;

	virtual void doFilterChange() = 0;
	
	//通用的过滤状态查询
	virtual bool isFilter(const std::wstring& path) = 0;

	//递归处理时，优先使用此方法
	virtual bool isFilter(const std::wstring& path, bool parentIsFilter) = 0;

};

#endif