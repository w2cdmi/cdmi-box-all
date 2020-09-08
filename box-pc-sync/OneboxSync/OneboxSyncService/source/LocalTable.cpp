#include "LocalTable.h"
#include "SyncUtility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalTable")
#endif

class LocalTableImpl : public LocalTable
{
public:
	LocalTableImpl(const std::wstring& parent)
	{
		createLocalTable(parent);
	}

	virtual int32_t getRoot(int64_t& rootId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s WHERE %s=%d LIMIT 0,1", 
				LOCAL_ROW_ID,
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_PARENT, 
				0);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			rootId = q.getInt64Field(0);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNode(const int64_t& id, LocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s,%s,%s,%s,%s,%s FROM %s WHERE %s=%lld LIMIT 0,1", 
				LOCAL_ROW_PARENT, 
				LOCAL_ROW_NAME, 
				LOCAL_ROW_TYPE, 
				LOCAL_ROW_STATUS, 
				LOCAL_ROW_CTIME, 
				LOCAL_ROW_MTIME,  
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, id);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			node->id = id;
			node->parent = q.getInt64Field(0);
			node->name = SD::Utility::String::utf8_to_wstring(q.getStringField(1));
			node->type = q.getIntField(2);
			node->status = (LocalStatus)q.getIntField(3);
			node->ctime = q.getInt64Field(4);
			node->mtime = q.getInt64Field(5);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNode(const int64_t& parent, const std::wstring& name, LocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s=%lld AND %s='%s' LIMIT 0,1", 
				LOCAL_ROW_ID, 
				LOCAL_ROW_PARENT, 
				LOCAL_ROW_NAME, 
				LOCAL_ROW_TYPE, 
				LOCAL_ROW_STATUS, 
				LOCAL_ROW_CTIME, 
				LOCAL_ROW_MTIME, 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_PARENT, parent, 
				LOCAL_ROW_NAME, CppSQLiteUtility::formatSqlStr(name).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			node->id = q.getInt64Field(0);
			node->parent = q.getInt64Field(1);
			node->name = SD::Utility::String::utf8_to_wstring(q.getStringField(2));
			node->type = q.getIntField(3);
			node->status = (LocalStatus)q.getIntField(4);
			node->ctime = q.getInt64Field(5);
			node->mtime = q.getInt64Field(6);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNoSyncList(const std::list<std::string>& noSyncNames, IdList& idList)
	{
		int64_t rootId = INVALID_ID;
		getRoot(rootId);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			for(std::list<std::string>::const_iterator it = noSyncNames.begin();
				it != noSyncNames.end(); ++it)
			{
				CppSQLite3Buffer sql;
				CppSQLite3Query q;
				sql.format("SELECT %s FROM %s WHERE %s=%lld AND %s='%s' LIMIT 0,1", 
					LOCAL_ROW_ID, 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_PARENT, rootId, 
					LOCAL_ROW_NAME, CppSQLiteUtility::formatSqlStr(*it).c_str());
				q = db_.execQuery(sql);
				if (!q.eof())
				{
					idList.push_back(q.getInt64Field(0));
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateNode(const LocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld, %s='%s', %s=%d, %s=%lld, %s=%lld WHERE %s=%lld", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_PARENT, node->parent, 
				LOCAL_ROW_NAME, CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
				LOCAL_ROW_TYPE, node->type, 
				//LOCAL_ROW_STATUS, node->status, 
				LOCAL_ROW_CTIME, node->ctime, 
				LOCAL_ROW_MTIME, node->mtime, 
				LOCAL_ROW_ID, node->id);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateNode(const int64_t& id, const LocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld, %s=%lld, %s='%s', %s=%d, %s=%lld, %s=%lld WHERE %s=%lld", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, node->id, 
				LOCAL_ROW_PARENT, node->parent, 
				LOCAL_ROW_NAME, CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
				LOCAL_ROW_TYPE, node->type, 
				//LOCAL_ROW_STATUS, node->status, 
				LOCAL_ROW_CTIME, node->ctime, 
				LOCAL_ROW_MTIME, node->mtime, 
				LOCAL_ROW_ID, id);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addNode(const LocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, 
				LOCAL_ROW_PARENT, 
				LOCAL_ROW_NAME, 
				LOCAL_ROW_TYPE, 
				LOCAL_ROW_STATUS, 
				LOCAL_ROW_CTIME, 
				LOCAL_ROW_MTIME, 
				node->id, 
				node->parent, 
				CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
				node->type, 
				node->status, 
				node->ctime, 
				node->mtime);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addNodes(const LocalNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			LocalNode node;
			db_.beginTransaction();
			for(LocalNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
					LOCAL_ROW_PARENT, 
					LOCAL_ROW_NAME, 
					LOCAL_ROW_TYPE, 
					LOCAL_ROW_STATUS, 
					LOCAL_ROW_CTIME, 
					LOCAL_ROW_MTIME, 
					node->id, 
					node->parent, 
					CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
					node->type, 
					node->status, 
					node->ctime, 
					node->mtime);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(node->id);
				printObj->addField<int64_t>(node->parent);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "addNodes. nodes size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addNodes(const LocalSyncNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			LocalSyncNode node;
			db_.beginTransaction();
			for(LocalSyncNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
					LOCAL_ROW_PARENT, 
					LOCAL_ROW_NAME, 
					LOCAL_ROW_TYPE, 
					LOCAL_ROW_STATUS, 
					LOCAL_ROW_CTIME, 
					LOCAL_ROW_MTIME,
					node->id, 
					node->parent, 
					CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
					(node->attributes&FILE_ATTRIBUTE_DIRECTORY)?FILE_TYPE_DIR:FILE_TYPE_FILE, 
					LS_Normal, 
					node->ctime, 
					node->mtime);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(node->id);
				printObj->addField<int64_t>(node->parent);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "addNodes. nodes size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}
	
	virtual int32_t replaceNodes(const LocalSyncNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			LocalSyncNode node;
			db_.beginTransaction();
			for(LocalSyncNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				(void)sql.format("REPLACE INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
					LOCAL_ROW_PARENT, 
					LOCAL_ROW_NAME, 
					LOCAL_ROW_TYPE, 
					LOCAL_ROW_STATUS, 
					LOCAL_ROW_CTIME, 
					LOCAL_ROW_MTIME,
					node->id, 
					node->parent, 
					CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
					(node->attributes&FILE_ATTRIBUTE_DIRECTORY)?FILE_TYPE_DIR:FILE_TYPE_FILE, 
					LS_Normal, 
					node->ctime, 
					node->mtime);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(node->id);
				printObj->addField<int64_t>(node->parent);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "replaceNodes. nodes size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t replaceNodes(const LocalNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			LocalNode node;
			db_.beginTransaction();
			for(LocalNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				(void)sql.format("REPLACE INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
					LOCAL_ROW_PARENT, 
					LOCAL_ROW_NAME, 
					LOCAL_ROW_TYPE, 
					LOCAL_ROW_STATUS, 
					LOCAL_ROW_CTIME, 
					LOCAL_ROW_MTIME, 
					node->id, 
					node->parent, 
					CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
					node->type, 
					node->status, 
					node->ctime, 
					node->mtime);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(node->id);
				printObj->addField<int64_t>(node->parent);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "replaceNodes. nodes size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t replaceNode(const LocalNode& node)
	{
		if (INVALID_ID == node->id)
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("REPLACE INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,'%s',%d,%d,%lld,%lld)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, 
				LOCAL_ROW_PARENT, 
				LOCAL_ROW_NAME, 
				LOCAL_ROW_TYPE, 
				LOCAL_ROW_STATUS, 
				LOCAL_ROW_CTIME, 
				LOCAL_ROW_MTIME, 
				node->id, 
				node->parent, 
				CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
				node->type, 
				node->status, 
				node->ctime, 
				node->mtime);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateNodes(const LocalNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			LocalNode node;
			db_.beginTransaction();
			for(LocalNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				(void)sql.format("UPDATE %s SET %s=%lld, %s='%s', %s=%d, %s=%lld, %s=%lld WHERE %s=%lld", 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_PARENT, node->parent, 
					LOCAL_ROW_NAME, CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
					LOCAL_ROW_TYPE, node->type, 
					//LOCAL_ROW_STATUS, node->status, 
					LOCAL_ROW_CTIME, node->ctime, 
					LOCAL_ROW_MTIME, node->mtime,
					LOCAL_ROW_ID, node->id);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(node->id);
				printObj->addField<int64_t>(node->parent);
				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "updateNodes. nodes size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return false;
	}

	virtual bool isExist(const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s WHERE %s=%lld LIMIT 0,1", 
				LOCAL_ROW_STATUS,
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, id);
			q = db_.execQuery(sql);
			if(!q.eof())
			{
				LocalStatus localStatus = (LocalStatus)q.getIntField(0);
				if(LS_Delete_Status!=(localStatus&LS_Delete_Status))
				{
					return true;
				}
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getExistStatus(const int64_t& localId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0,1", 
								LOCAL_ROW_STATUS, LOCAL_TABLE_NAME, LOCAL_ROW_ID, localId);
			CppSQLite3Query q = db_.execQuery(sql);

			if(!q.eof())
			{
				LocalStatus localStatus = (LocalStatus)q.getIntField(0);
				return (localStatus&LS_Delete_Status)?RT_PARENT_NOEXIST_ERROR:RT_OK;
			}
			return RT_SQLITE_NOEXIST;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual std::wstring getPath(const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::string tmpPath = "";
			int64_t parent = id;
			std::string name = "";
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			while (true)
			{
				sql.format("SELECT %s, %s FROM %s WHERE %s=%lld LIMIT 0,1", 
					LOCAL_ROW_NAME, 
					LOCAL_ROW_PARENT, 
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
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
			return (tmpPath.empty()?L"":SD::Utility::String::utf8_to_wstring(tmpPath.substr(1,tmpPath.length()-1)));
		}
		CATCH_SQLITE_EXCEPTION;	
		db_.rollbackTransaction();
		return L"";
	}

	virtual bool isSpecialStatus(const int64_t& id, LocalStatus& localStatus)
	{
		boost::mutex::scoped_lock lock(mutex_);
		bool isSpecialStatus = false;
		try
		{
			int64_t parent = id;
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			while (true)
			{
				sql.format("SELECT %s, %s FROM %s WHERE %s=%lld LIMIT 0,1",
					LOCAL_ROW_STATUS,
					LOCAL_ROW_PARENT,
					LOCAL_TABLE_NAME, 
					LOCAL_ROW_ID, 
					parent);
				CppSQLite3Query q = db_.execQuery(sql);
				if (q.eof())
				{
					break;
				}
				
				localStatus = LocalStatus(q.getIntField(0));
				if(LS_NoActionDelete_Status==(localStatus&LS_NoActionDelete_Status))
				{
					isSpecialStatus = true;
					break;
				}
				if(LS_Filter==(localStatus&LS_Filter))
				{
					isSpecialStatus = true;
					break;
				}
				parent = q.getInt64Field(1);
			}
			db_.commitTransaction();
			return isSpecialStatus;
		}
		CATCH_SQLITE_EXCEPTION;	
		db_.rollbackTransaction();
		return isSpecialStatus;
	}

	virtual int64_t getCount()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT COUNT(*) FROM %s", LOCAL_TABLE_NAME);
			q = db_.execQuery(sql);
			int64_t count = q.getInt64Field(0);
			return count;
		}
		CATCH_SQLITE_EXCEPTION;
		return 0;
	}

	virtual int32_t initMarkStatus(const MarkStatus& mark = MS_Missed)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_MARK,
				(int)mark);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateMarkStatus(const IdList& ids, const MarkStatus& mark)
	{
		try
		{
			LocalNode node;
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s IN(%s)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_MARK, (int)mark, 
				LOCAL_ROW_ID, Sync::getInStr(ids).c_str());
			//SERVICE_DEBUG(MODULE_NAME, RT_OK, "sqlStr:%s", sqlStr);
			db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNodesByMarkStatus(IdList& ids, const MarkStatus& mark)
	{
		try
		{
			LocalNode node;
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s WHERE %s=%d", 
				LOCAL_ROW_ID, 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_MARK, (int)mark);
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				ids.push_back(q.getInt64Field(0));
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getParentStatus(const int64_t& localId, int64_t& parentId, LocalStatus& status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			//select b.status from tb_localInfo a, tb_localInfo b where a.id = 12103423998912582 and a.parent = b.id
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT b.%s, b.%s FROM %s a, %s b WHERE a.%s = %lld AND a.%s = b.%s LIMIT 0,1", 
								LOCAL_ROW_ID, LOCAL_ROW_STATUS, 
								LOCAL_TABLE_NAME, LOCAL_TABLE_NAME,
								LOCAL_ROW_ID, localId,
								LOCAL_ROW_PARENT, LOCAL_ROW_ID);
			CppSQLite3Query q = db_.execQuery(sql);

			if(q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			parentId = q.getInt64Field(0);
			status = (LocalStatus)q.getIntField(1);
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
		return getChildList(idList, childList);
	}

	virtual int32_t getTopChildren(const int64_t& id, IdList& childList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		childList.clear();
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s FROM %s WHERE %s = %lld", 
								LOCAL_ROW_ID, LOCAL_TABLE_NAME,
								LOCAL_ROW_PARENT, id);
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				childList.push_back(q.getInt64Field(0));
				q.nextRow();
			}
			return RT_OK;
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
			CppSQLite3Buffer sql;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s IN(%s)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNode(const int64_t& id)
	{
		IdList idList;
		idList.push_back(id);
		return deleteNodes(idList);
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
			CppSQLite3Buffer sql;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s|%d) WHERE %s IN(%s)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_STATUS,
				LOCAL_ROW_STATUS,
				(int)LS_Delete_Status,
				LOCAL_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t noActionDelete(const IdList& deleteList)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			std::string inStr = Sync::getInStr(deleteList);
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s|%d) WHERE %s IN(%s)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_STATUS,
				LOCAL_ROW_STATUS,
				(int)LS_NoActionDelete_Status,
				LOCAL_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateStatus(const IdList& idList, LocalStatus localStatus)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			std::string inStr = Sync::getInStr(idList);
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d WHERE %s IN(%s)", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_STATUS,
				(int)localStatus,
				LOCAL_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(sql);
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
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s|%d) WHERE %s = %d", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_STATUS,
				LOCAL_ROW_STATUS,
				(int)LS_Delete_Status,
				LOCAL_ROW_MARK, 
				(int)MS_Missed);

			int32_t iModify = db_.execDML(sql);
			if(iModify > 0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateTimeInfo(int64_t& id, int64_t& ctime, int64_t& mtime)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld, %s=%lld, %s=%d WHERE %s=%lld", 
				LOCAL_TABLE_NAME, 
				LOCAL_ROW_CTIME, ctime, 
				LOCAL_ROW_MTIME, mtime, 
				LOCAL_ROW_STATUS, (int)LS_Normal,
				LOCAL_ROW_ID, id);

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t clearTable()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s", 
				LOCAL_TABLE_NAME);

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getRootNode(LocalNode& node)
	{
		int64_t rootId = INVALID_ID;
		int32_t ret = getRoot(rootId);
		if (RT_OK != ret)
		{
			return ret;
		}
		return getNode(rootId, node);
	}

private:
	void createLocalTable(const std::wstring& parent)
	{
		try
		{
			std::wstring path = parent + SQLITE_LOCALINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(LOCAL_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d);", 
									LOCAL_TABLE_NAME, 
									LOCAL_ROW_ID, 
									LOCAL_ROW_PARENT,
									LOCAL_ROW_NAME,
									LOCAL_ROW_TYPE, 
									LOCAL_ROW_STATUS, LS_Normal, 
									LOCAL_ROW_CTIME, 0L, 
									LOCAL_ROW_MTIME, 0L, 
									LOCAL_ROW_MARK, MS_Default);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"localtable_idx1", LOCAL_TABLE_NAME, LOCAL_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"localtable_idx2", LOCAL_TABLE_NAME, LOCAL_ROW_PARENT);
				(void)db_.execDML(bufSQLIdx2);
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
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s FROM %s WHERE %s IN (%s)", 
								LOCAL_ROW_ID, LOCAL_TABLE_NAME,
								LOCAL_ROW_PARENT, inStr.c_str());
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				int64_t id = q.getInt64Field(0);
				childList.push_back(id);
				newIdList.push_back(id);

				q.nextRow();
			}
			return getChildList(newIdList, childList);
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	boost::mutex mutex_;
	CppSQLite3DB db_;
};

std::auto_ptr<LocalTable> LocalTable::create(const std::wstring& parent)
{
	return std::auto_ptr<LocalTable>(new LocalTableImpl(parent));
}
