#include "DataBaseMgr.h"
#include "ConfigureMgr.h"
#include "SyncConfigure.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("DataBaseMgr")
#endif

class DataBaseMgrImpl : public DataBaseMgr
{
public:
	DataBaseMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		createTables();
	}

	virtual ~DataBaseMgrImpl()
	{
		try
		{
			db_.close();
		}
		catch(...){}
	}

	virtual LocalTable* getLocalTable()
	{
		return metaDataTable_->getLocalTable();
	}

	virtual RemoteTable* getRemoteTable()
	{
		return metaDataTable_->getRemoteTable();
	}

	virtual RelationTable* getRelationTable()
	{
		return metaDataTable_->getRelationTable();
	}

	virtual DiffTable* getDiffTable()
	{
		return metaDataTable_->getDiffTable();
	}

	virtual UploadTable* getUploadTable()
	{
		return metaDataTable_->getUploadTable();
	}

	virtual MetaDataTable* getMetaDataTable()
	{
		return metaDataTable_.get();
	}

	virtual SyncDataTable* getSyncDataTable()
	{
		return syncDataTable_.get();
	}

private:
	void createTables()
	{
		std::wstring parent = userContext_->getConfigureMgr()->getConfigure()->userDataPath() + PATH_DELIMITER + SYNCDATA_DIR;
		int32_t ret = checkDb(parent);
		while(RT_OK!=ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "rebulid db failed.");
			ret = checkDb(parent);
			boost::this_thread::sleep(boost::posix_time::seconds(1));
		}
		long offset = SyncConfigure::getInstance(userContext_)->uploadFilterPeriod()*60;

		metaDataTable_ = MetaDataTable::create(userContext_, parent+PATH_DELIMITER, offset);
		syncDataTable_ = SyncDataTable::create(parent+PATH_DELIMITER+SYNCDATA_TABLE);
	}

	int32_t checkDb(const std::wstring& parent)
	{
		int32_t ret = RT_OK;
		//check db file
		if (!SD::Utility::FS::is_exist(parent))
		{
			(void)SD::Utility::FS::create_directories(parent);
			return ret;
		}

		CppSQLite3Buffer bufSQL;
		//check local & remote & relation
		try
		{
			db_.open(SD::Utility::String::wstring_to_utf8(parent+PATH_DELIMITER+SQLITE_LOCALINFO_TABLE).c_str());
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s LIMIT 0,1", 
							LOCAL_ROW_ID, 
							LOCAL_ROW_PARENT,
							LOCAL_ROW_NAME,
							LOCAL_ROW_TYPE, 
							LOCAL_ROW_STATUS, 
							LOCAL_ROW_CTIME, 
							LOCAL_ROW_MTIME, 
							LOCAL_ROW_MARK,
							LOCAL_TABLE_NAME);
			(void)db_.execQuery(bufSQL);
			db_.close();

			db_.open(SD::Utility::String::wstring_to_utf8(parent+PATH_DELIMITER+SQLITE_REMOTEINFO_TABLE).c_str());
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s LIMIT 0,1", 
							REMOTE_ROW_ID, 
							REMOTE_ROW_PARENT,
							REMOTE_ROW_NAME, 
							REMOTE_ROW_TYPE,
							REMOTE_ROW_STATUS,
							REMOTE_ROW_VERSION,
							REMOTE_ROW_MARK,
							REMOTE_TABLE_NAME);
			(void)db_.execQuery(bufSQL);
			db_.close();

			db_.open(SD::Utility::String::wstring_to_utf8(parent+PATH_DELIMITER+SQLITE_RELATIONINFO_TABLE).c_str());
			(void)bufSQL.format("SELECT %s, %s FROM %s LIMIT 0,1", 
							RELATION_ROW_REMOTEID,
							RELATION_ROW_LOCALID,
							RELATION_TABLE_NAME);
			(void)db_.execQuery(bufSQL);
			db_.close();
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"SQLite DML ErrorCode: %d ErrorMessage: %s ",
						e.errorCode(), e.errorMessage());
			db_.close();
			//when the localInfo or remoteInfo or relationInfo is lost, need a full scan
			return rebuildAll(parent);
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"the InsertSingle function occur unknown exception", NULL);
			db_.close();
			return rebuildAll(parent);
		}

		//check diff
		bool hasDiff = true;
		try
		{
			if(SD::Utility::FS::is_exist(parent+PATH_DELIMITER+SQLITE_DIFFINFO_TABLE))
			{
				db_.open(SD::Utility::String::wstring_to_utf8(parent+PATH_DELIMITER+SQLITE_DIFFINFO_TABLE).c_str());
				(void)bufSQL.format("SELECT COUNT(1) FROM %s", DIFF_TABLE_NAME);
				CppSQLite3Query q = db_.execQuery(bufSQL);
				if(0==q.getIntField(0))
				{
					hasDiff = false;
				}
				q.finalize();

				(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s LIMIT 0,1", 
								DIFF_ROW_DIFFID,
								DIFF_ROW_KEY, 
								DIFF_ROW_KEYTYPE, 
								DIFF_ROW_OPER,
								DIFF_ROW_STATUS,
								DIFF_ROW_PRIORITY,
								DIFF_ROW_SIZE,
								DIFF_TABLE_NAME);
				(void)db_.execQuery(bufSQL);
				(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s FROM %s LIMIT 0,1", 
								DIFFPATH_ROW_ID,
								DIFFPATH_ROW_TYPE,
								DIFFPATH_ROW_PATH,
								DIFFPATH_ROW_REMOTEPATH,
								DIFFPATH_ROW_SIZE,
								DIFFPATH_ROW_ERRORCODE,
								DIFFPATH_TABLE_NAME);
				(void)db_.execQuery(bufSQL);
				db_.close();
			}
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"SQLite DML ErrorCode: %d ErrorMessage: %s ",
						e.errorCode(), e.errorMessage());
			db_.close();
			if(hasDiff)
			{
				//when the diff is lost, need a full scan
				return rebuildAll(parent);
			}
			else
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "rebuild diffInfo.");
				CHECK_RESULT(SD::Utility::FS::remove(parent+PATH_DELIMITER+SQLITE_DIFFINFO_TABLE));
			}
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"the InsertSingle function occur unknown exception", NULL);
			db_.close();
			if(hasDiff)
			{
				return rebuildAll(parent);
			}
			else
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "rebuild diffInfo.");
				CHECK_RESULT(SD::Utility::FS::remove(parent+PATH_DELIMITER+SQLITE_DIFFINFO_TABLE));
			}
		}

		try
		{
			if(SD::Utility::FS::is_exist(parent+PATH_DELIMITER+SQLITE_UPLOAD_TABLE))
			{
				db_.open(SD::Utility::String::wstring_to_utf8(parent+PATH_DELIMITER+SQLITE_UPLOAD_TABLE).c_str());
				(void)bufSQL.format("SELECT %s, %s FROM %s LIMIT 0,1", 
								UPLOAD_ROW_ID, 
								UPLOAD_ROW_LASTTIME,
								UPLOAD_TABLE_NAME);
				(void)db_.execQuery(bufSQL);
				db_.close();
			}
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"SQLite DML ErrorCode: %d ErrorMessage: %s ",
						e.errorCode(), e.errorMessage());
			db_.close();
			SERVICE_INFO(MODULE_NAME, RT_OK, "rebuild uploadInfo.");
			CHECK_RESULT(SD::Utility::FS::remove(parent+PATH_DELIMITER+SQLITE_UPLOAD_TABLE));
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
						"the InsertSingle function occur unknown exception", NULL);
			db_.close();
			SERVICE_INFO(MODULE_NAME, RT_OK, "rebuild uploadInfo.");
			CHECK_RESULT(SD::Utility::FS::remove(parent+PATH_DELIMITER+SQLITE_UPLOAD_TABLE));
		}

		return RT_OK;
	}

	int32_t rebuildAll(const std::wstring& parent)
	{
		SERVICE_INFO(MODULE_NAME, RT_OK, "rebuild db.");
		int32_t ret = RT_OK;
		ret = SD::Utility::FS::remove_all(parent);
		if(RT_OK!=ret)
		{
			return ret;
		}
		return SD::Utility::FS::create_directories(parent);
	}
private:
	UserContext* userContext_;
	CppSQLite3DB db_;
	std::auto_ptr<SyncDataTable> syncDataTable_;
	std::auto_ptr<MetaDataTable> metaDataTable_;
};

DataBaseMgr* DataBaseMgr::instance_ = NULL;

DataBaseMgr* DataBaseMgr::getInstance(UserContext* userContext)
{
	if (NULL == instance_)
	{
		instance_ = new DataBaseMgrImpl(userContext);
	}
	return instance_;
}