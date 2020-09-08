#include "RelationTable.h"
#include "SyncUtility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RelationTable")
#endif

class RelationTableImpl : public RelationTable
{
public:
	RelationTableImpl(const std::wstring& parent)
	{
		createRelationTable(parent);
	}

	virtual ~RelationTableImpl(void)
	{
	}

	virtual int32_t addRelation(const int64_t& remoteId, const int64_t& localId)
	{
		if((INVALID_ID == remoteId)||(INVALID_ID == localId))
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("REPLACE INTO %s(%s,%s) VALUES (%lld,%lld)", 
					RELATION_TABLE_NAME, 
					RELATION_ROW_REMOTEID, 
					RELATION_ROW_LOCALID,  
					remoteId, 
					localId);
			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addRelation(const RelationInfo& relationInfo)
	{
		if(relationInfo.empty())
		{
			return RT_OK;
		}

		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			for(RelationInfo::const_iterator it = relationInfo.begin(); it != relationInfo.end(); ++it)
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("REPLACE INTO %s(%s,%s) VALUES (%lld,%lld)", 
						RELATION_TABLE_NAME, 
						RELATION_ROW_REMOTEID, 
						RELATION_ROW_LOCALID,  
						it->first, 
						it->second);
				(void)db_.execDML(bufSQL);

				printObj->addField<int64_t>(it->first);
				printObj->lastField<int64_t>(it->second);
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "addRelation. relationInfo size:%d, [%s]", relationInfo.size(), printObj->getMsg().c_str());

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual bool isSync(int64_t localId, int64_t remoteId)
	{
		if((INVALID_ID==localId)|| (INVALID_ID==remoteId))
		{
			return false;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT 1 FROM %s WHERE %s = %lld AND %s = %lld LIMIT 0,1", 
								RELATION_TABLE_NAME,
								RELATION_ROW_LOCALID, localId,
								RELATION_ROW_REMOTEID, remoteId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return true;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getRemoteIdByLocalId(int64_t localId, int64_t& remoteId)
	{
		if(INVALID_ID == localId)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0,1", 
								RELATION_ROW_REMOTEID, 
								RELATION_TABLE_NAME,
								RELATION_ROW_LOCALID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			remoteId = qSet.getInt64Field(0);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getLocalIdByRemoteId(int64_t remoteId, int64_t& localId)
	{
		if(INVALID_ID == remoteId)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0,1", 
								RELATION_ROW_LOCALID, 
								RELATION_TABLE_NAME,
								RELATION_ROW_REMOTEID, remoteId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			localId = qSet.getInt64Field(0);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteByLocalId(const IdList& deleteList)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = bufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
				RELATION_TABLE_NAME, 
				RELATION_ROW_LOCALID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteByRemoteId(const IdList& deleteList)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = bufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
				RELATION_TABLE_NAME, 
				RELATION_ROW_REMOTEID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteByLocalId(const int64_t localId)
	{
		IdList idList;
		idList.push_back(localId);
		return deleteByLocalId(idList);
	}

	virtual int32_t deleteByRemoteId(const int64_t remoteId)
	{
		IdList idList;
		idList.push_back(remoteId);
		return deleteByRemoteId(idList);
	}

	virtual bool remoteIdIsExist(int64_t remoteId)
	{
		if(INVALID_ID == remoteId)
		{
			return false;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT 1 FROM %s WHERE %s = %lld LIMIT 0,1", 
								RELATION_TABLE_NAME,
								RELATION_ROW_REMOTEID, remoteId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return false;
			}
			return true;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;		
	}

	virtual bool localIdIsExist(int64_t localId)
	{
		if(INVALID_ID == localId)
		{
			return false;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT 1 FROM %s WHERE %s = %lld LIMIT 0,1", 
								RELATION_TABLE_NAME,
								RELATION_ROW_LOCALID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return false;
			}
			return true;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual bool isNoRelation()
	{
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT 1 FROM %s WHERE %s <> %d LIMIT 0,1", 
								RELATION_TABLE_NAME,
								RELATION_ROW_REMOTEID, ROOT_PARENTID);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return true;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getExistLocalByRemote(const IdList& remoteIdList, IdList& localExistIds)
	{
		if(remoteIdList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(remoteIdList);
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s IN(%s)", 
				RELATION_ROW_LOCALID,
				RELATION_TABLE_NAME, 
				RELATION_ROW_REMOTEID, 
				inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				localExistIds.push_back(qSet.getInt64Field(0));
				qSet.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getExistRemoteByLocal(const IdList& localIdList, IdList& remoteExistIds)
	{
		if(localIdList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(localIdList);
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s IN(%s)", 
				RELATION_ROW_REMOTEID,
				RELATION_TABLE_NAME, 
				RELATION_ROW_LOCALID, 
				inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				remoteExistIds.push_back(qSet.getInt64Field(0));
				qSet.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}
private:
	void createRelationTable(const std::wstring& parent)
	{
		try
		{
			std::wstring path = parent + SQLITE_RELATIONINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(RELATION_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL);", 
									RELATION_TABLE_NAME,
									RELATION_ROW_REMOTEID,
									RELATION_ROW_LOCALID);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"relationtable_idx1", RELATION_TABLE_NAME, RELATION_ROW_LOCALID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"relationtable_idx2", RELATION_TABLE_NAME, RELATION_ROW_REMOTEID);
				(void)db_.execDML(bufSQLIdx2);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

private:
	boost::mutex m_mutexDB;
	CppSQLite3DB db_;
};

std::auto_ptr<RelationTable>  RelationTable::create(const std::wstring& parent)
{
	return std::auto_ptr<RelationTable>(new RelationTableImpl(parent));
}