#include "RemoteTable.h"
#include "SyncUtility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RemoteTable")
#endif

class RemoteTableImpl : public RemoteTable
{
public:
	RemoteTableImpl(const std::wstring& parent)
	{
		createRemoteTable(parent);
	}

	virtual ~RemoteTableImpl(void)
	{
	}

	virtual int32_t addRemoteNode(const RemoteNode& remoteNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s) VALUES(%lld,'%s',%d,'%s',%lld,%d)", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_ID, 
				REMOTE_ROW_NAME, 
				REMOTE_ROW_TYPE, 
				REMOTE_ROW_VERSION, 
				REMOTE_ROW_PARENT,
				REMOTE_ROW_STATUS,
				remoteNode->id, 
				CppSQLiteUtility::formatSqlStr(remoteNode->name).c_str(), 
				remoteNode->type, 
				CppSQLiteUtility::formatSqlStr(remoteNode->version).c_str(), 
				remoteNode->parent, 
				(int)remoteNode->status);
			int32_t iModify = db_.execDML(bufSQL);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateRemoteNode(const RemoteNode& remoteNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("UPDATE %s SET %s='%s', %s=%d, %s='%s', %s=%lld, %s=%d WHERE %s=%lld", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_NAME,
				CppSQLiteUtility::formatSqlStr(remoteNode->name).c_str(),
				REMOTE_ROW_TYPE,
				remoteNode->type, 
				REMOTE_ROW_VERSION,
				CppSQLiteUtility::formatSqlStr(remoteNode->version).c_str(),
				REMOTE_ROW_PARENT,
				remoteNode->parent,
				REMOTE_ROW_STATUS,
				(int)RS_Sync_Status,
				REMOTE_ROW_ID,
				remoteNode->id);
			int32_t iModify = db_.execDML(bufSQL);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getRemoteNode(RemoteNode& remoteNode)
	{
		if(INVALID_ID==remoteNode->id)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = %lld LIMIT 0,1", 
								REMOTE_ROW_NAME, REMOTE_ROW_VERSION, 
								REMOTE_ROW_PARENT, REMOTE_ROW_TYPE,
								REMOTE_ROW_STATUS, REMOTE_TABLE_NAME,
								REMOTE_ROW_ID, remoteNode->id);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{				
				return RT_SQLITE_NOEXIST;
			}
			remoteNode->name = SD::Utility::String::utf8_to_wstring(qSet.getStringField(0));
			remoteNode->version = SD::Utility::String::utf8_to_wstring(qSet.getStringField(1));
			remoteNode->parent = qSet.getInt64Field(2);
			remoteNode->type = qSet.getIntField(3);
			remoteNode->status = (RemoteStatus)qSet.getIntField(4);			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getRemoteNodes(const int64_t& parent, const std::wstring& name, RemoteNodes& remoteNodes)
	{
		if((INVALID_ID==parent)||name.empty())
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %lld AND %s = '%s'", 
								REMOTE_ROW_ID, REMOTE_ROW_VERSION, 
								REMOTE_ROW_TYPE, REMOTE_ROW_STATUS, 
								REMOTE_TABLE_NAME,
								REMOTE_ROW_PARENT, parent,
								REMOTE_ROW_NAME, CppSQLiteUtility::formatSqlStr(name).c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				RemoteNode remoteNode(new st_RemoteNode);
				remoteNode->id = qSet.getInt64Field(0);;
				remoteNode->name = name;
				remoteNode->version = SD::Utility::String::utf8_to_wstring(qSet.getStringField(1));
				remoteNode->parent = parent;
				remoteNode->type = qSet.getIntField(2);
				remoteNode->status = (RemoteStatus)qSet.getIntField(3);

				remoteNodes.push_back(remoteNode);
				qSet.nextRow();
			}
						
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNoSyncList(std::list<std::string>& noSyncNames)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d", 
								REMOTE_ROW_NAME, 
								REMOTE_TABLE_NAME,
								REMOTE_ROW_PARENT, 0,
								REMOTE_ROW_STATUS, 0);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				noSyncNames.push_back(qSet.getStringField(0));
				qSet.nextRow();
			}
						
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t replaceRemoteNode(const RemoteNode& newRemote)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s) VALUES (%lld,'%s',%d,'%s',%lld,%d)", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_ID, 
				REMOTE_ROW_NAME, 
				REMOTE_ROW_TYPE, 
				REMOTE_ROW_VERSION, 
				REMOTE_ROW_PARENT,
				REMOTE_ROW_STATUS,
				newRemote->id, 
				CppSQLiteUtility::formatSqlStr(newRemote->name).c_str(), 
				newRemote->type, 
				CppSQLiteUtility::formatSqlStr(newRemote->version).c_str(), 
				newRemote->parent, 
				(int)newRemote->status);
			int32_t iModify = db_.execDML(bufSQL);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t replaceRemoteNodes(const RemoteNodes& newRemotes)
	{
		if(newRemotes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for(RemoteNodes::const_iterator it = newRemotes.begin();
				it != newRemotes.end(); ++it)
			{
				RemoteNode syncNode = *(it);
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s) VALUES (%lld,'%s',%d,'%s',%lld,%d)", 
					REMOTE_TABLE_NAME, 
					REMOTE_ROW_ID, 
					REMOTE_ROW_NAME, 
					REMOTE_ROW_TYPE, 
					REMOTE_ROW_VERSION, 
					REMOTE_ROW_PARENT,
					REMOTE_ROW_STATUS,
					syncNode->id, 
					CppSQLiteUtility::formatSqlStr(syncNode->name).c_str(), 
					syncNode->type, 
					CppSQLiteUtility::formatSqlStr(syncNode->version).c_str(), 
					syncNode->parent, 
					(int)syncNode->status);
				(void)db_.execDML(bufSQL);

				printObj->addField<int64_t>(syncNode->id);
				printObj->addField<int64_t>(syncNode->parent);
				printObj->addField<std::string>(SD::Utility::String::wstring_to_string(syncNode->version));
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(syncNode->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "replaceRemoteList. newRemotes size:%d, [%s]", newRemotes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual bool isExist(int64_t remoteId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0,1", 
								REMOTE_ROW_STATUS, REMOTE_TABLE_NAME, REMOTE_ROW_ID, remoteId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				RemoteStatus status = (RemoteStatus)qSet.getIntField(0);				
				return (RS_Sync_Status==status);
			}			
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getParentStatus(const int64_t& remoteId, int64_t& parentId, RemoteStatus& status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			//select b.status from tb_localInfo a, tb_localInfo b where a.id = 12103423998912582 and a.parent = b.id
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT b.%s, b.%s FROM %s a, %s b WHERE a.%s = %lld AND a.%s = b.%s LIMIT 0,1", 
								REMOTE_ROW_ID, REMOTE_ROW_STATUS, 
								REMOTE_TABLE_NAME, REMOTE_TABLE_NAME,
								REMOTE_ROW_ID, remoteId,
								REMOTE_ROW_PARENT, REMOTE_ROW_ID);
			CppSQLite3Query qSet = db_.execQuery(sql);

			if(qSet.eof())
			{				
				return RT_SQLITE_NOEXIST;
			}
			parentId = qSet.getInt64Field(0);
			status = (RemoteStatus)qSet.getIntField(1);			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t initMarkStatus()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("UPDATE %s SET %s=%d WHERE %s<>0", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_MARK,
				(int)MS_Missed,
				REMOTE_ROW_ID);
			int32_t iModify = db_.execDML(bufSQL);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				return RT_OK;
			}
			else
			{
				return RT_CANCEL;
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getNodesByMarkStatus(const IdList& existList, IdList& idList, const MarkStatus& mark)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::string inStr = Sync::getInStr(existList);
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s NOT IN(%s) AND %s = %d", 
				REMOTE_ROW_ID, REMOTE_TABLE_NAME,
				REMOTE_ROW_ID, inStr.c_str(),
				REMOTE_ROW_MARK, (int)mark);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				idList.push_back(qSet.getInt64Field(0));
				qSet.nextRow();
			}			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getChildren(IdList& childList)
	{
		if(childList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		IdList idList = childList;
		getChildList(idList, childList);
		return RT_OK;
	}

	virtual int32_t filterChildren(IdList& idList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}

		boost::mutex::scoped_lock lock(mutex_);
		std::string inStr = Sync::getInStr(idList);
		idList.clear();
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s IN (%s) AND %s NOT IN (%s)", 
								REMOTE_ROW_ID, REMOTE_TABLE_NAME,
								REMOTE_ROW_ID, inStr.c_str(),
								REMOTE_ROW_PARENT, inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				idList.push_back(qSet.getInt64Field(0));
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getTopChildren(const int64_t& id, IdList& childList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		childList.clear();
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s FROM %s WHERE %s = %lld", 
				REMOTE_ROW_ID, REMOTE_TABLE_NAME,
				REMOTE_ROW_PARENT, id);
			CppSQLite3Query qSet = db_.execQuery(sql);

			while(!qSet.eof())
			{
				childList.push_back(qSet.getInt64Field(0));
				qSet.nextRow();
			}			
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNodes(const IdList& deleteList)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = bufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_ID, inStr.c_str());

			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t trashDeleteNodes(const IdList& deleteList)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = bufSQL.format("UPDATE %s SET %s=(%s|%d) WHERE %s IN(%s)", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_STATUS,
				REMOTE_ROW_STATUS,
				(int)RS_Delete_Status,
				REMOTE_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t trashDeleteNodesByMark()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("UPDATE %s SET %s=(%s|%d) WHERE %s = %d", 
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_STATUS,
				REMOTE_ROW_STATUS,
				(int)RS_Delete_Status,
				REMOTE_ROW_MARK, 
				(int)MS_Missed);

			int32_t iModify = db_.execDML(bufSQL);
			if(iModify > 0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual std::wstring getPath(const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		std::string tmpPath = "";
		try
		{
			int64_t parent = id;
			std::string name = "";
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			while (true)
			{
				sql.format("SELECT %s, %s FROM %s WHERE %s=%lld LIMIT 0,1", 
					REMOTE_ROW_NAME, 
					REMOTE_ROW_PARENT, 
					REMOTE_TABLE_NAME, 
					REMOTE_ROW_ID, 
					parent);
				CppSQLite3Query q = db_.execQuery(sql);
				if (q.eof())
				{
					SERVICE_ERROR(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "parent:%I64d not exist", parent);
					tmpPath = "";
					break;
				}
				name = q.getStringField(0);
				parent = q.getInt64Field(1);
				tmpPath = PATH_DELIMITER_STR + name + tmpPath;
				if(0==parent)
				{
					break;
				}
			}
			db_.commitTransaction();
			return SD::Utility::String::utf8_to_wstring(tmpPath);
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return L"";
	}

	virtual int32_t getPath(const int64_t& id, std::wstring& path)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			path = L"";
			int64_t parent = id;
			std::wstring name = L"";
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			while (true)
			{
				sql.format("SELECT %s, %s FROM %s WHERE %s=%lld LIMIT 0,1", 
					REMOTE_ROW_NAME, 
					REMOTE_ROW_PARENT, 
					REMOTE_TABLE_NAME, 
					REMOTE_ROW_ID, 
					parent);
				CppSQLite3Query q = db_.execQuery(sql);
				if (q.eof())
				{
					SERVICE_ERROR(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "parent:%I64d not exist", parent);
					path = L"";
					break;
				}
				name = SD::Utility::String::utf8_to_wstring(q.getStringField(0));
				parent = q.getInt64Field(1);
				path = PATH_DELIMITER + name + path;
				if(0==parent)
				{
					break;
				}
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual bool isNoSync(const std::wstring& topDirName)
	{
		boost::mutex::scoped_lock lock(mutex_);
		bool isNoSync = false;
		try
		{
			CppSQLite3Buffer sql;
			sql.format("SELECT %s FROM %s WHERE %s=%d AND %s='%s' LIMIT 0,1", 
				REMOTE_ROW_STATUS,
				REMOTE_TABLE_NAME, 
				REMOTE_ROW_PARENT, 0,
				REMOTE_ROW_NAME, CppSQLiteUtility::formatSqlStr(topDirName).c_str());
			CppSQLite3Query q = db_.execQuery(sql);
			if (!q.eof())
			{
				RemoteStatus status = (RemoteStatus)q.getIntField(0);
				if(RS_Sync_Status!=(status&RS_Sync_Status))
				{
					isNoSync = true;
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "topDirName:%s is noSync", 
						SD::Utility::String::wstring_to_string(topDirName).c_str());
				}
			}
			else
			{
				SERVICE_ERROR(MODULE_NAME, RT_PARENT_NOEXIST_ERROR, "topDirName:%s not exist", 
					SD::Utility::String::wstring_to_string(topDirName).c_str());
			}
			return isNoSync;
		}
		CATCH_SQLITE_EXCEPTION;
		return isNoSync;	
	}
private:
	void createRemoteTable(const std::wstring& parent)
	{
		try
		{
			std::wstring path = parent + SQLITE_REMOTEINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			createRemoteTable();
		}
		CATCH_SQLITE_EXCEPTION;
	}

	void createRemoteTable()
	{
		try
		{
			if(!db_.tableExists(REMOTE_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER,\
									%s VARCHAR NOT NULL,\
									%s INTEGER DEFAULT %d);", 
									REMOTE_TABLE_NAME, 
									REMOTE_ROW_ID, 
									REMOTE_ROW_PARENT,
									REMOTE_ROW_NAME, 
									REMOTE_ROW_TYPE,
									REMOTE_ROW_STATUS,
									REMOTE_ROW_VERSION,
									REMOTE_ROW_MARK, MS_Default);

				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"remotetable_idx1", REMOTE_TABLE_NAME, REMOTE_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"remotetable_idx2", REMOTE_TABLE_NAME, REMOTE_ROW_PARENT);
				(void)db_.execDML(bufSQLIdx2);

				CppSQLite3Buffer bufSQL2;
				const char* sqlStr = bufSQL2.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s) VALUES(%d,'%s',%d,'%s',%d,%d)", 
					REMOTE_TABLE_NAME, 
					REMOTE_ROW_ID, 
					REMOTE_ROW_NAME, 
					REMOTE_ROW_TYPE, 
					REMOTE_ROW_VERSION, 
					REMOTE_ROW_PARENT,
					REMOTE_ROW_STATUS,
					0, 
					"", 
					(int)FILE_TYPE_DIR, 
					"", 
					-1, 
					(int)RS_Sync_Status);
				int32_t iModify = db_.execDML(bufSQL2);
			}
		}
		CATCH_SQLITE_EXCEPTION;	
	}

	virtual int32_t getChildList(const IdList& idList, IdList& childList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		std::list<int64_t> newIdList;
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s IN (%s)", 
								REMOTE_ROW_ID, REMOTE_TABLE_NAME,
								REMOTE_ROW_PARENT, inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				int64_t id = qSet.getInt64Field(0);
				childList.push_back(id);
				newIdList.push_back(id);

				qSet.nextRow();
			}			
			getChildList(newIdList, childList);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	boost::mutex mutex_;
	CppSQLite3DB db_;
};

std::auto_ptr<RemoteTable>  RemoteTable::create(const std::wstring& parent)
{
	return std::auto_ptr<RemoteTable>(new RemoteTableImpl(parent));
}