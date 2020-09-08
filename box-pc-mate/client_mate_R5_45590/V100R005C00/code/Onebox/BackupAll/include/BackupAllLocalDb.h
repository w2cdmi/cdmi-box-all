#ifndef _ONEBOX_BACKUPALL_LOCAL_DB_H_
#define _ONEBOX_BACKUPALL_LOCAL_DB_H_

#include "BackupAllCommon.h"
#include "CppSQLite3.h"
#include <set>

class BackupAllLocalDb
{
public:
	virtual ~BackupAllLocalDb(){}

	static BackupAllLocalDb* create(const std::wstring& path, int32_t uploadFilterPeriod);

	virtual int32_t addRootNode(const BATaskLocalNode& node) = 0;

	virtual int32_t addNode(const BATaskBaseNode& node) = 0;

	virtual int32_t addNodes(BATaskBaseNodeList& nodes, const std::set<int64_t>& scanningDirs) = 0;

	virtual int32_t deleteNodes(IdList& deleteList, std::list<std::wstring>& uploadingPath) = 0;

	virtual int32_t setCheckNodes(const std::list<std::wstring>& checkList) = 0;

	virtual int32_t setAllCheck() = 0;

	virtual int32_t updateNode(const BATaskBaseNode& newNode) = 0;

	virtual int32_t updatePath() = 0;

	virtual int32_t updatePath(const std::wstring& oldPath, const std::wstring& newPath) = 0;

	virtual int32_t getBrokenPoint(std::set<int64_t>& scanningDirs, std::set<int64_t>& scannedDirs) = 0;

	virtual int32_t getNextNode(BATaskLocalNode& node) = 0;

	virtual int32_t getNodeById(const int64_t& localId, BATaskLocalNode& node) = 0;

	virtual int32_t getNodeByPath(const std::wstring& path, BATaskLocalNode& node) = 0;

	virtual int32_t getNodeByNode(const BATaskBaseNode& newNode, BATaskLocalNode& node) = 0;
	
	virtual int32_t getExistNodes(BATaskExistFileInfo& existFiles, BATaskExistDirInfo& existDirs) = 0;

	virtual std::wstring getPathById(const int64_t& localId) = 0;

	virtual int64_t getRemoteIdById(const int64_t& localId) = 0;

	virtual int32_t getSubNodes(const int64_t& parentId, BATaskBaseNodeList& nodes, bool& noDir) = 0;

	virtual int32_t getSubFiles(const int64_t& parentId, std::map<std::wstring, int64_t>& subFiles) = 0;

	virtual int32_t changeSubFilesOp(const int64_t& parentId) = 0;

	virtual int32_t getFailedNodes(BATaskLocalNodeList& failedList, int32_t offset, int32_t limit) = 0;

	virtual int32_t updateParentError(const int64_t& parentId, int32_t errorCode) = 0;

	virtual int32_t resumeError(bool hasErrorTasks, bool isFirstTime) = 0;

	virtual int32_t reUploadErrorPath() = 0;

	virtual int32_t updateStatus(const std::map<std::wstring, int32_t>& pathInfo) = 0;

	virtual int32_t getFilterUploading(std::list<std::wstring>& filterList) = 0;

	virtual int32_t getUploadingCnt() = 0;

	virtual int32_t updateSubFilesOp(const int64_t& parentId, const std::map<std::wstring, int64_t>& subFiles) = 0;

	virtual int32_t updateExInfo(const BATaskLocalNode& node) = 0;

	virtual int32_t resumeUpload() = 0;

	virtual int32_t markExist(bool isSetMark) = 0;
	
	virtual int32_t deleteMarkNodes() = 0;

	virtual int32_t ignoreError(const int64_t& localId) = 0;

	virtual int64_t getRowId(const int64_t& remoteParent, const std::wstring& filename, bool isSuccess, int64_t& localId) = 0;

	virtual int32_t updateUploadFailed(const std::map<int64_t, int32_t>& failedInfo, int64_t failedSize) = 0;

	virtual int32_t updateUploadInfo(const std::map<int64_t, int64_t>& successInfo, int64_t successSize) = 0;

	virtual int32_t getTotalInfo(BATaskInfo& taskInfo, bool flush = false) = 0;

	virtual bool isFilter(const int64_t& id) = 0;

	virtual int32_t updatePstTime(const int64_t& localId) = 0;

	virtual bool isNeedSkip(const int64_t& localId) = 0;

	virtual int32_t updateScanStatus(BAScanStatus status) = 0;

	virtual BAScanStatus getScanStatus() = 0;

	virtual int32_t loadIncInfoByTotal() = 0;

	virtual int32_t updateIncInfo() = 0;

	virtual int32_t initIncInfo() = 0;

	virtual int32_t addCheckPath(const std::list<std::wstring>& paths) = 0;

	virtual int32_t getCheckPath(std::list<std::wstring>& paths) = 0;

};

#endif
