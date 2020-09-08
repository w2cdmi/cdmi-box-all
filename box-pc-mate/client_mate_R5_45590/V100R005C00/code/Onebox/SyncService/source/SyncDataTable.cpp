#include "SyncDataTable.h"
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncDataTable")
#endif

class SyncDataTableImpl : public SyncDataTable
{
public:
	SyncDataTableImpl(const std::wstring& path):path_(path)
	{
		(void)SD::Utility::FS::create_directories(SD::Utility::FS::get_parent_path(path_));
	}

	virtual ~SyncDataTableImpl(void)
	{
	}

	int32_t getSyncData(RemoteNodes& syncData)
	{
		syncData.clear();
		if (!SD::Utility::FS::is_exist(path_) || 0 == SD::Utility::FS::get_file_size(path_))
		{
			return RT_OK;
		}
		CppSQLite3DB db;
		try
		{
			db.open(SD::Utility::String::wstring_to_utf8(path_).c_str());
			CppSQLite3Buffer sql;
		
			(void)sql.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s IN(%d,%d) AND %s IN(%d,%d)", 
								SYNC_ROW_ID, SYNC_ROW_NAME, SYNC_ROW_OBJID, 
								SYNC_ROW_PARENTID, SYNC_ROW_STATUS, SYNC_ROW_TYPE, 
								SYNC_ROW_SYNCSTATUS, SYNC_ROW_CONTENT_CREATE, 
								SYNC_ROW_CONTENT_MODIFY, SYNC_ROW_SIZE,
								SYNC_TABLE_NAME, 
								SYNC_ROW_STATUS, (int)SS_Normal_Status, (int)SS_Delete_Status,
								SYNC_ROW_TYPE, (int)FILE_TYPE_DIR, (int)FILE_TYPE_FILE);
			CppSQLite3Query q = db.execQuery(sql);

			while(!q.eof())
			{
				RemoteNode pSyncNode(new st_RemoteNode);
				pSyncNode->id = q.getInt64Field(0);
				pSyncNode->name = SD::Utility::String::utf8_to_wstring(q.getStringField(1));
				pSyncNode->version = SD::Utility::String::utf8_to_wstring(q.getStringField(2));
				pSyncNode->parent = q.getInt64Field(3);
				pSyncNode->incStatus = (IncStatus)q.getIntField(4);
				pSyncNode->type = q.getIntField(5);
				pSyncNode->status = (RemoteStatus)q.getIntField(6);
				pSyncNode->contentCreate = Utility::DateTime(q.getInt64Field(7), Utility::UtcType::Unix).getWindowsFileTime();
				pSyncNode->contentModify = Utility::DateTime(q.getInt64Field(8), Utility::UtcType::Unix).getWindowsFileTime();
				pSyncNode->size = q.getInt64Field(9);
				syncData.push_back(pSyncNode);

				q.nextRow();
			}
			q.finalize();
			db.close();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db.close();
		return RT_ERROR;
	}

	int32_t getLoginSyncData(RemoteNodes& syncData)
	{
		if (!SD::Utility::FS::is_exist(path_) || 0 == SD::Utility::FS::get_file_size(path_))
		{
			return RT_OK;
		}
		CppSQLite3DB db;
		try
		{
			db.open(SD::Utility::String::wstring_to_utf8(path_).c_str());
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %d AND %s IN(%d,%d)", 
								SYNC_ROW_ID, SYNC_ROW_NAME, 
								SYNC_ROW_OBJID, SYNC_ROW_PARENTID, 
								SYNC_ROW_TYPE, SYNC_ROW_SYNCSTATUS,
								SYNC_ROW_CONTENT_CREATE, 
								SYNC_ROW_CONTENT_MODIFY, SYNC_ROW_SIZE,
								SYNC_TABLE_NAME, 
								SYNC_ROW_STATUS, (int)SS_Normal_Status,
								SYNC_ROW_TYPE, (int)FILE_TYPE_DIR, (int)FILE_TYPE_FILE);
			CppSQLite3Query q = db.execQuery(sql);

			while(!q.eof())
			{
				RemoteNode pSyncNode(new st_RemoteNode);
				pSyncNode->id = q.getInt64Field(0);
				pSyncNode->name = SD::Utility::String::utf8_to_wstring(q.getStringField(1));
				pSyncNode->version = SD::Utility::String::utf8_to_wstring(q.getStringField(2));
				pSyncNode->parent = q.getInt64Field(3);
				pSyncNode->type = q.getIntField(4);
				pSyncNode->status = (RemoteStatus)q.getIntField(5);
				pSyncNode->contentCreate = Utility::DateTime(q.getInt64Field(6), Utility::UtcType::Unix).getWindowsFileTime();
				pSyncNode->contentModify = Utility::DateTime(q.getInt64Field(7), Utility::UtcType::Unix).getWindowsFileTime();
				pSyncNode->size = q.getInt64Field(8);
				syncData.push_back(pSyncNode);

				q.nextRow();
			}
			q.finalize();
			db.close();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db.close();
		return RT_ERROR;
	}

	int64_t getMaxVersion()
	{
		int64_t syncVersion = 0;
		if (!SD::Utility::FS::is_exist(path_))
		{
			return syncVersion;
		}
		CppSQLite3DB db;
		try
		{
			db.open(SD::Utility::String::wstring_to_utf8(path_).c_str());
			CppSQLite3Buffer sql;
		
			(void)sql.format("SELECT MAX(%s) FROM %s", 
								SYNC_ROW_SYNCVERSION, SYNC_TABLE_NAME);
			CppSQLite3Query q = db.execQuery(sql);

			if(!q.eof())
			{
				syncVersion = q.getInt64Field(0);
			}
			q.finalize();
		}
		CATCH_SQLITE_EXCEPTION;
		db.close();
		return syncVersion;
	}

private:
	std::wstring path_;
};

std::auto_ptr<SyncDataTable>  SyncDataTable::create(const std::wstring& path)
{
	return std::auto_ptr<SyncDataTable>(new SyncDataTableImpl(path));
}