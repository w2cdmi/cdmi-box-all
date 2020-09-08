#ifndef _ONEBOX_LOCAL_TABLE_H_
#define _ONEBOX_LOCAL_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class LocalTable
{
public:
	virtual ~LocalTable(){}

	static std::auto_ptr<LocalTable> create(const std::wstring& parent);

	virtual int32_t getRoot(int64_t& rootId) = 0;

	virtual int32_t getNode(const int64_t& id, LocalNode& node) = 0;

	virtual int32_t getNode(const int64_t& parent, const std::wstring& name, LocalNode& node) = 0;

	virtual int32_t getNoSyncList(const std::list<std::string>& noSyncNames, IdList& idList) = 0;

	virtual int32_t updateNode(const LocalNode& node) = 0;

	virtual int32_t updateNode(const int64_t& id, const LocalNode& node) = 0;

	virtual int32_t addNode(const LocalNode& node) = 0;

	virtual int32_t addNodes(const LocalNodes& nodes) = 0;

	virtual int32_t addNodes(const LocalSyncNodes& nodes) = 0;

	virtual int32_t replaceNode(const LocalNode& node) = 0;

	virtual int32_t replaceNodes(const LocalSyncNodes& nodes) = 0;

	virtual int32_t replaceNodes(const LocalNodes& nodes) = 0;

	virtual int32_t updateNodes(const LocalNodes& nodes) = 0;

	virtual bool isExist(const int64_t& id) = 0;

	virtual int32_t getExistStatus(const int64_t& localId) = 0;

	virtual std::wstring getPath(const int64_t& id) = 0;

	virtual bool isSpecialStatus(const int64_t& id, LocalStatus& localStatus) = 0;

	virtual int64_t getCount() = 0;

	virtual int32_t initMarkStatus(const MarkStatus& mark = MS_Missed) = 0;

	virtual int32_t updateMarkStatus(const IdList& ids, const MarkStatus& mark) = 0;

	virtual int32_t getNodesByMarkStatus(IdList& ids, const MarkStatus& mark) = 0;

	virtual int32_t getParentStatus(const int64_t& localId, int64_t& parentId, LocalStatus& status) = 0;

	virtual int32_t getChildren(IdList& childList) = 0;

	virtual int32_t getTopChildren(const int64_t& id, IdList& childList) = 0;

	virtual int32_t deleteNodes(const IdList& deleteList) = 0;

	virtual int32_t deleteNode(const int64_t& id) = 0;

	virtual int32_t trashDeleteNodes(const IdList& deleteList) = 0;

	virtual int32_t noActionDelete(const IdList& deleteList) = 0;

	virtual int32_t updateStatus(const IdList& idList, LocalStatus localStatus) = 0;

	virtual int32_t trashDeleteNodesByMark() = 0;

	virtual int32_t updateTimeInfo(int64_t& id, int64_t& ctime, int64_t& mtime) = 0;

	virtual int32_t clearTable() = 0;

	virtual int32_t getRootNode(LocalNode& node) = 0;
};

#endif
