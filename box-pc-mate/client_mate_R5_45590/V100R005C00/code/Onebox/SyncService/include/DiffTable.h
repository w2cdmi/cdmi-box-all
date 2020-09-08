#ifndef _ONEBOX_DIFF_TABLE_H_
#define _ONEBOX_DIFF_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class DiffTable
{
public:
	virtual ~DiffTable(){}

	virtual int32_t getDiffCnt(void) = 0;
	
	virtual int32_t getCntForTimer(int64_t& maxDiffId, int32_t& curdiffCnt) = 0;

	virtual int32_t getSizeInfo(int64_t& uploadSize, int64_t& downloadSize) = 0;

	virtual bool isInDiff(const std::wstring& path) = 0;

	virtual bool isInDiff(KeyType keyType, const int64_t& key) = 0;

	virtual int64_t getMaxInc() = 0;

	virtual int32_t getRelationInfo(RelationInfo& fileRelationInfo, RelationInfo& dirRelationInfo) = 0;

	virtual int32_t getErrorNodes(ErrorNodes& errorNodes, const int32_t offset, const int32_t limit) = 0;

	virtual int32_t exportErrorNodes(ErrorNodes& errorNodes) = 0;

	virtual int32_t addOper(const OperNode& operNode) = 0;

	virtual int32_t addOperList(OperNodes& operList) = 0;

	virtual int32_t getTopOper(OperNode& operNode) = 0;

	virtual int32_t restoreRunningTask(void) = 0;

	virtual int32_t geOperList(const int64_t& key, KeyType keyType, OperNodes& operList) = 0;

	//refreshDiff by operIds
	virtual int32_t refreshDiff(const IdList& idList, const DiffStatus status) = 0;

	virtual int32_t getRunningDiff(KeyType keyType, const int64_t& id, IdList& idList, bool& hasNewOper) = 0;

	virtual int32_t updateOper(KeyType keyType, const int64_t& id, OperType oldOper, OperType newOper) = 0;

	virtual int32_t completeDiff(KeyType keyType, const int64_t& id) = 0;
	
	virtual int32_t completeDiff(KeyType keyType, const IdList& idList) = 0;

	virtual int32_t getMove(KeyType keyType, const IdList& idList, IdList& moveList) = 0;

	virtual int32_t getDelete(KeyType keyType, IdList& idList) = 0;

	virtual int32_t completeDiffExMove(KeyType keyType, const IdList& idList, IdList& moveList) = 0;

	virtual int32_t hiddenDiff(const IdList& idList) = 0;

	virtual int32_t lowerHiddenPriority() = 0;

	virtual int32_t initPriority() = 0;

	virtual bool isNeedRefresh() = 0;

	virtual int32_t getIncDiff(int64_t lastAutoInc, int64_t& maxAutoInc, DiffPathNodes& diffInfos) = 0;

	virtual int32_t replaceIncPath(DiffPathNodes& diffInfos) = 0;

	virtual int32_t insertIncPath(DiffPathNodes& diffInfos) = 0;

	virtual int32_t replaceSubPath(const std::wstring& oldPath, const std::wstring& newPath) = 0;

	virtual int32_t getErrorCode(KeyType keyType, const int64_t& id) = 0;

	virtual int32_t updateErrorCode(KeyType keyType, const IdList& idList, int32_t errorCode) = 0;

	virtual int32_t updateErrorCode(KeyType keyType, const int64_t& id, int32_t errorCode = 0) = 0;

	virtual int32_t deleteCompletePath(const int64_t& key, const KeyType keyType, std::wstring& localPath) = 0;

	//virtual int32_t deleteCompletePath(const int64_t& key, const KeyType keyType) = 0;

	virtual int32_t delAllDiffPath() = 0;

	virtual int64_t getDiffCount() = 0;

	virtual int64_t getLeftDiffCount() = 0;

	virtual int64_t getErrorDiffCount() = 0;

	virtual int32_t getLocalFileDiff(IdList& idList, IdList& fileIdList) = 0;

	virtual int32_t clearDiff(KeyType keyType) = 0;

	static std::auto_ptr<DiffTable> create(UserContext* userContext, const std::wstring& parent);
};

#endif
