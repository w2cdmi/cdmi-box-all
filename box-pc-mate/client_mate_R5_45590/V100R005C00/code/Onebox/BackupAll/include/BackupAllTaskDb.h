#ifndef _ONEBOX_BACKUPALL_DB_H_
#define _ONEBOX_BACKUPALL_DB_H_

#include "BackupAllCommon.h"
#include "CppSQLite3.h"
#include <set>

class BackupAllTaskDb
{
public:
	virtual ~BackupAllTaskDb(){}

	static BackupAllTaskDb* create(const std::wstring& parent);

	virtual int32_t addNode(const BATaskNode& node) = 0;

	virtual int32_t setStartInfo(const BATaskNode& node) = 0;

	virtual int32_t setPeriodInfo(const BATaskNode& node) = 0;

	virtual int32_t updateRemoteId(int64_t remoteId) = 0;

	virtual int32_t updateStatus(const BATaskStatus status) = 0;

	virtual int32_t addRunTime(int64_t offset) = 0;

	virtual int32_t updateFilterChange(bool isChange) = 0;

	virtual int32_t getNode(BATaskNode& node) = 0;

	virtual int32_t getVolumeInfo(std::map<std::wstring, int64_t>& volumeInfo) = 0;

	virtual int32_t getVolumeInfo(std::map<std::wstring, int64_t>& volumeInfo, BAVolumeStatus status) = 0;

	virtual int32_t updateVolumeUsn(const std::wstring& volumePath, int64_t usn) = 0;

	virtual int32_t updateVolumeUsn(const std::wstring& volumePath) = 0;

	virtual int32_t getPathInfo(std::set<std::wstring>& selectPath, std::set<std::wstring>& filterPath) = 0;

	virtual int32_t getPathInfo(std::map<std::wstring, int64_t>& selectPath, std::map<std::wstring, int64_t>& filterPath) = 0;

	virtual int32_t updateIdByPath(const std::wstring& path, const int64_t& id) = 0;

	virtual int32_t updatePathById(const int64_t& id, const std::wstring& path) = 0;

	virtual int32_t getPathInfo(std::map<std::wstring, int32_t>& pathInfo, uint32_t& maxPathLen) = 0;

	virtual int32_t getPathInfo(const std::wstring& volumePath, std::map<std::wstring, int32_t>& pathInfo) = 0;

	virtual int32_t getNextSelectPath(const std::set<int64_t>& selectDirs, BATaskBaseNode& nextNode) = 0;

	virtual int32_t updatePathInfo(const std::list<std::wstring>& selectPath, const std::list<std::wstring>& filterPath,
		const std::set<std::wstring>& deleteSelectPath, const std::set<std::wstring>& deleteFilterPath) = 0;

	virtual int32_t deleteAllPathInfo() = 0;

	virtual int32_t updateVolumeInfo(const std::set<std::wstring>& selectPath, const std::map<std::wstring, int64_t>& oldVolumeInfo) = 0;

	virtual int32_t deleteVolumeInfo() = 0;
};

#endif
