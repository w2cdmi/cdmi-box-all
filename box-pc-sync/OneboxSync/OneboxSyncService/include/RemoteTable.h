#ifndef _ONEBOX_REMOTE_TABLE_H_
#define _ONEBOX_REMOTE_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class RemoteTable
{
public:
	virtual ~RemoteTable(){}

	virtual int32_t addRemoteNode(const RemoteNode& remoteNode) = 0;

	virtual int32_t updateRemoteNode(const RemoteNode& remoteNode) = 0;

	virtual int32_t getRemoteNode(RemoteNode& remoteNode) = 0;

	virtual int32_t getRemoteNodes(const int64_t& parent, const std::wstring& name, RemoteNodes& remoteNodes) = 0;

	virtual int32_t getNoSyncList(std::list<std::string>& noSyncNames) = 0;

	virtual int32_t replaceRemoteNode(const RemoteNode& newRemote) = 0;

	virtual int32_t replaceRemoteNodes(const RemoteNodes& newRemotes) = 0;

	virtual bool isExist(int64_t remoteId) = 0;

	virtual int32_t getParentStatus(const int64_t& remoteId, int64_t& parentId, RemoteStatus& status) = 0;

	virtual int32_t initMarkStatus() = 0;

	virtual int32_t getNodesByMarkStatus(const IdList& existList, IdList& idList, const MarkStatus& mark) = 0;

	virtual int32_t getChildren(IdList& childList) = 0;

	virtual int32_t filterChildren(IdList& idList) = 0;

	virtual int32_t getTopChildren(const int64_t& id, IdList& childList) = 0;

	virtual int32_t deleteNodes(const IdList& deleteList) = 0;

	virtual int32_t trashDeleteNodes(const IdList& deleteList) = 0;

	virtual int32_t trashDeleteNodesByMark() = 0;

	virtual std::wstring getPath(const int64_t& id) = 0;

	virtual int32_t getPath(const int64_t& id, std::wstring& path) = 0;
	
	virtual bool isNoSync(const std::wstring& topDirName) = 0;
	
	static std::auto_ptr<RemoteTable> create(const std::wstring& parent);
};

#endif
