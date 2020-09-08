#ifndef _ONEBOX_BACKUPALLTASK_UTILITY_H_
#define _ONEBOX_BACKUPALLTASK_UTILITY_H_

#include "BackupAllCommon.h"
#include "UserContext.h"
#include <set>

namespace BackupAll
{
	int64_t li64toll(const LARGE_INTEGER& li64);

	void pasreNextRunTime(int32_t backupType, const std::wstring& period, int64_t& nextRunTime);

	void getNextRunTime(int32_t backupType, int64_t& nextRuntime, int32_t day);

	std::string getInStr(const std::list<int64_t>& idList);

	std::string getInStr(const std::set<int64_t>& idList);

	int64_t getIdByPath(UserContext* userContext, const std::wstring& path);

	std::wstring getFullPathByFileId(const std::wstring& disk, DWORDLONG FileReferenceNumber);

}

#endif