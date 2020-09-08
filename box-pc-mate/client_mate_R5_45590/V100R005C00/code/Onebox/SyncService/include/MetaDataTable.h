#ifndef _SYNC_METADATATABLE_H_
#define _SYNC_METADATATABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"
#include "LocalTable.h"
#include "RemoteTable.h"
#include "RelationTable.h"
#include "DiffTable.h"
#include "UploadTable.h"

class MetaDataTable
{
public:
	virtual ~MetaDataTable(){}

    static std::auto_ptr<MetaDataTable> create(UserContext* userContext, const std::wstring& parent, const long& offset);

	virtual LocalTable* getLocalTable() = 0;

	virtual RemoteTable* getRemoteTable() = 0;

	virtual RelationTable* getRelationTable() = 0;

	virtual DiffTable* getDiffTable() = 0;

	virtual UploadTable* getUploadTable() = 0;

	virtual int32_t checkUploadFilterInfo() = 0;
};

#endif