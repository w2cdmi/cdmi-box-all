#ifndef _ONEBOX_RELATION_TABLE_H_
#define _ONEBOX_RELATION_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class RelationTable
{
public:
	virtual ~RelationTable(){}

    static std::auto_ptr<RelationTable> create(const std::wstring& parent);

	virtual int32_t addRelation(const int64_t& remoteId, const int64_t& localId) = 0;

	virtual int32_t addRelation(const RelationInfo& relationInfo) = 0;

	virtual bool isSync(int64_t localId, int64_t remoteId) = 0;

	virtual int32_t getRemoteIdByLocalId(int64_t localId, int64_t& remoteId) = 0;

	virtual int32_t getLocalIdByRemoteId(int64_t remoteId, int64_t& localId) = 0;

	virtual int32_t deleteByLocalId(const IdList& deleteList) = 0;

	virtual int32_t deleteByRemoteId(const IdList& deleteList) = 0;

	virtual int32_t deleteByLocalId(const int64_t localId) = 0;

	virtual int32_t deleteByRemoteId(const int64_t remoteId) = 0;

	virtual bool remoteIdIsExist(int64_t remoteId) = 0;

	virtual bool localIdIsExist(int64_t localId) = 0;

	virtual bool isNoRelation() = 0;

	virtual int32_t getExistLocalByRemote(const IdList& remoteIdList, IdList& localExistIds) = 0;

	virtual int32_t getExistRemoteByLocal(const IdList& localIdList, IdList& remoteExistIds) = 0;
};

#endif
