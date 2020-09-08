#ifndef _ONEBOX_DATABASE_MGR_H_
#define _ONEBOX_DATABASE_MGR_H_

#include "UserContext.h"
#include "SyncDataTable.h"
#include "MetaDataTable.h"

class DataBaseMgr
{
public:
	virtual ~DataBaseMgr(){}

	static DataBaseMgr* create(UserContext* userContext);

	virtual VersionTable* getVersionTable() = 0;

	virtual LocalTable* getLocalTable() = 0;

	virtual RemoteTable* getRemoteTable() = 0;

	virtual RelationTable* getRelationTable() = 0;

	virtual DiffTable* getDiffTable() = 0;

	virtual TransTaskTable* getTransTaskTable() = 0;

	virtual UploadTable* getUploadTable() = 0;

	virtual SyncDataTable* getSyncDataTable() = 0;

	virtual MetaDataTable* getMetaDataTable() = 0;
};

#endif