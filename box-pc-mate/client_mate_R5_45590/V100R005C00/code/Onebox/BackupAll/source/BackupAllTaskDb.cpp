#include "BackupAllTaskDb.h"
#include "Utility.h"
#include "BackupAllUtility.h"
#include <boost/thread/mutex.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllTaskDb")
#endif

#define SQLITE_BATASKINFO_TABLE (L"backupAllTaskInfo.db")

#define BA_TASK_TABLE_NAME ("tb_backupAllTaskInfo")
#define BA_TASK_ROW_ID ("remote_id")
#define BA_TASK_ROW_TYPE ("type")
#define BA_TASK_ROW_USER_DEFINE ("user_define")
#define BA_TASK_ROW_NEXT_START_TIME ("next_start_time")
#define BA_TASK_ROW_STATUS ("status")
#define BA_TASK_ROW_FIRST_START_TIME ("first_start_time")	//首次启动时间
#define BA_TASK_ROW_CUR_START_TIME ("cur_start_time")		//本次启动时间
#define BA_TASK_ROW_CUR_RUN_TIME ("cur_run_time")			//本次运行时间，单位：秒
#define BA_TASK_ROW_ISFILTER_CHANGE ("is_filter_change")	

#define BA_VOLUME_TABLE_NAME ("tb_backupAllVolume")
#define BA_VOLUME_ROW_PATH ("volume")
#define BA_VOLUME_ROW_USN ("usn")
#define BA_VOLUME_ROW_STATUS ("status")

#define BA_PATH_TABLE_NAME ("tb_backupAllPath")
#define BA_PATH_ROW_LOCAL_ID ("local_id")
#define BA_PATH_ROW_PATH ("local_path")
#define BA_PATH_ROW_TYPE ("type")

class BackupAllTaskDbImpl : public BackupAllTaskDb
{
public:
	BackupAllTaskDbImpl(const std::wstring& parent)
	{
		createBackupTaskTable(parent);
	}

	int32_t addNode(const BATaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%d,'%s',%lld,%d,%lld,%lld,%lld,%d)", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_ID,
				BA_TASK_ROW_TYPE, 
				BA_TASK_ROW_USER_DEFINE, 
				BA_TASK_ROW_NEXT_START_TIME, 
				BA_TASK_ROW_STATUS,
				BA_TASK_ROW_FIRST_START_TIME, 
				BA_TASK_ROW_CUR_START_TIME, 
				BA_TASK_ROW_CUR_RUN_TIME,
				BA_TASK_ROW_ISFILTER_CHANGE,
				node.remoteId,
				node.type,
				CppSQLiteUtility::formatSqlStr(node.userDefine).c_str(),
				node.nextStartTime,
				node.status,
				node.firstStartTime,
				node.curStartTime,
				node.curRunTime,
				0);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t setStartInfo(const BATaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%d, %s=%lld, %s=%lld, %s=%lld WHERE %s=%lld", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_STATUS, node.status,
				BA_TASK_ROW_FIRST_START_TIME, node.firstStartTime,
				BA_TASK_ROW_CUR_START_TIME, node.curStartTime,
				BA_TASK_ROW_CUR_RUN_TIME, node.curRunTime,
				BA_TASK_ROW_ID, node.remoteId);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}
	
	virtual int32_t setPeriodInfo(const BATaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%d, %s='%s', %s=%lld WHERE %s=%lld", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_TYPE, node.type,
				BA_TASK_ROW_USER_DEFINE, CppSQLiteUtility::formatSqlStr(node.userDefine).c_str(),
				BA_TASK_ROW_NEXT_START_TIME, node.nextStartTime,
				BA_TASK_ROW_ID, node.remoteId);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateRemoteId(int64_t remoteId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_ID, remoteId);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateStatus(const BATaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_STATUS, status);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t addRunTime(int64_t offset)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%s + %lld", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_CUR_RUN_TIME, BA_TASK_ROW_CUR_RUN_TIME, offset);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateFilterChange(bool isChange)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d", 
				BA_TASK_TABLE_NAME,
				BA_TASK_ROW_ISFILTER_CHANGE, isChange);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	int32_t getNode(BATaskNode& node)
	{
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s LIMIT 0,1",
				BA_TASK_ROW_ID,
				BA_TASK_ROW_TYPE,
				BA_TASK_ROW_USER_DEFINE,
				BA_TASK_ROW_NEXT_START_TIME,
				BA_TASK_ROW_STATUS,
				BA_TASK_ROW_FIRST_START_TIME,
				BA_TASK_ROW_CUR_START_TIME,
				BA_TASK_ROW_CUR_RUN_TIME,
				BA_TASK_ROW_ISFILTER_CHANGE,
				BA_TASK_TABLE_NAME);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.remoteId = qSet.getInt64Field(0);
			node.type = (BATaskType)qSet.getIntField(1);
			node.userDefine = SD::Utility::String::utf8_to_wstring(qSet.getStringField(2));
			node.nextStartTime = qSet.getInt64Field(3);
			node.status = (BATaskStatus)qSet.getIntField(4);
			node.firstStartTime = qSet.getInt64Field(5);
			node.curStartTime = qSet.getInt64Field(6);
			node.curRunTime = qSet.getInt64Field(7);
			node.isFilterChange = (1==qSet.getIntField(8));
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	int32_t getVolumeInfo(std::map<std::wstring, int64_t>& volumeInfo)
	{
		volumeInfo.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s,%s FROM %s WHERE %s<>%d", 
				BA_VOLUME_ROW_PATH, 
				BA_VOLUME_ROW_USN,
				BA_VOLUME_TABLE_NAME,
				BA_VOLUME_ROW_STATUS, BAVS_Delete);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				volumeInfo.insert(std::make_pair(Utility::String::utf8_to_wstring(q.getStringField(0)), q.getInt64Field(1)));
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	int32_t getVolumeInfo(std::map<std::wstring, int64_t>& volumeInfo, BAVolumeStatus status)
	{
		volumeInfo.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s,%s FROM %s WHERE %s=%d", 
				BA_VOLUME_ROW_PATH, 
				BA_VOLUME_ROW_USN,
				BA_VOLUME_TABLE_NAME,
				BA_VOLUME_ROW_STATUS, status);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				volumeInfo.insert(std::make_pair(Utility::String::utf8_to_wstring(q.getStringField(0)), q.getInt64Field(1)));
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateVolumeUsn(const std::wstring& volumePath, int64_t usn)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s='%s'", 
				BA_VOLUME_TABLE_NAME, 
				BA_VOLUME_ROW_USN, usn,
				BA_VOLUME_ROW_PATH, CppSQLiteUtility::formatSqlStr(volumePath).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t updateVolumeUsn(const std::wstring& volumePath)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=-%s, %s=%d WHERE %s='%s' AND %s<0", 
				BA_VOLUME_TABLE_NAME, 
				BA_VOLUME_ROW_USN, BA_VOLUME_ROW_USN,
				BA_VOLUME_ROW_STATUS, BAVS_Normal,
				BA_VOLUME_ROW_PATH, CppSQLiteUtility::formatSqlStr(volumePath).c_str(),
				BA_VOLUME_ROW_USN);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getPathInfo(std::set<std::wstring>& selectPath, std::set<std::wstring>& filterPath)
	{
		selectPath.clear();
		filterPath.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s FROM %s", 
				BA_PATH_ROW_PATH,
				BA_PATH_ROW_TYPE,
				BA_PATH_TABLE_NAME);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				if(BAP_Select==q.getIntField(1))
				{
					selectPath.insert(Utility::String::utf8_to_wstring(q.getStringField(0)));
				}
				else
				{
					filterPath.insert(Utility::String::utf8_to_wstring(q.getStringField(0)));
				}
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getPathInfo(std::map<std::wstring, int64_t>& selectPath, std::map<std::wstring, int64_t>& filterPath)
	{
		selectPath.clear();
		filterPath.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s, %s FROM %s ORDER BY %s", 
				BA_PATH_ROW_PATH,
				BA_PATH_ROW_TYPE,
				BA_PATH_ROW_LOCAL_ID,
				BA_PATH_TABLE_NAME,
				BA_PATH_ROW_PATH);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				if(BAP_Select==q.getIntField(1))
				{
					selectPath.insert(std::make_pair(Utility::String::utf8_to_wstring(q.getStringField(0)),q.getInt64Field(2)));
				}
				else
				{
					filterPath.insert(std::make_pair(Utility::String::utf8_to_wstring(q.getStringField(0)),q.getInt64Field(2)));
				}
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateIdByPath(const std::wstring& path, const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s='%s'", 
				BA_PATH_TABLE_NAME, 
				BA_PATH_ROW_LOCAL_ID, id,
				BA_PATH_ROW_PATH, CppSQLiteUtility::formatSqlStr(path).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updatePathById(const int64_t& id, const std::wstring& path)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s' WHERE %s=%lld", 
				BA_PATH_TABLE_NAME, 
				BA_PATH_ROW_PATH, CppSQLiteUtility::formatSqlStr(path).c_str(),
				BA_PATH_ROW_LOCAL_ID, id);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getPathInfo(std::map<std::wstring, int32_t>& pathInfo, uint32_t& maxPathLen)
	{
		boost::mutex::scoped_lock lock(mutex_);
		maxPathLen = 0;
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s FROM %s", 
				BA_PATH_ROW_PATH,
				BA_PATH_ROW_TYPE,
				BA_PATH_TABLE_NAME);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				std::wstring path = Utility::String::utf8_to_wstring(q.getStringField(0));
				pathInfo.insert(std::make_pair(path,q.getIntField(1)));
				if(path.length()>maxPathLen)
				{
					maxPathLen = path.length();
				}
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getPathInfo(const std::wstring& volumePath, std::map<std::wstring, int32_t>& pathInfo)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s FROM %s WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
				BA_PATH_ROW_PATH,
				BA_PATH_ROW_TYPE,
				BA_PATH_TABLE_NAME,
				BA_PATH_ROW_PATH, 
				CppSQLiteUtility::formaSqlLikeStr(volumePath).c_str());
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				pathInfo.insert(std::make_pair(Utility::String::utf8_to_wstring(q.getStringField(0)),q.getIntField(1)));
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNextSelectPath(const std::set<int64_t>& selectDirs, BATaskBaseNode& nextNode)
	{
		std::string inStr = BackupAll::getInStr(selectDirs);

		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s FROM %s WHERE %s=%d AND %s IN(%s) ORDER BY %s LIMIT 0,1", 
				BA_PATH_ROW_PATH,
				BA_PATH_ROW_LOCAL_ID,
				BA_PATH_TABLE_NAME,
				BA_PATH_ROW_TYPE, BAP_Select,
				BA_PATH_ROW_LOCAL_ID, inStr.c_str(),
				BA_PATH_ROW_PATH);
			q = db_.execQuery(sql);
			if(!q.eof())
			{
				nextNode.path = Utility::String::utf8_to_wstring(q.getStringField(0));
				nextNode.localId = q.getInt64Field(1);
				return RT_OK;
			}
			return RT_SQLITE_NOEXIST;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updatePathInfo(const std::list<std::wstring>& selectPath, const std::list<std::wstring>& filterPath,
		const std::set<std::wstring>& deleteSelectPath, const std::set<std::wstring>& deleteFilterPath)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(std::set<std::wstring>::const_iterator it = deleteSelectPath.begin();
				it != deleteSelectPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("DELETE FROM %s WHERE %s='%s'", 
					BA_PATH_TABLE_NAME,
					BA_PATH_ROW_PATH, CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void) db_.execDML(sql);
			}
			for(std::set<std::wstring>::const_iterator it = deleteFilterPath.begin();
				it != deleteFilterPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("DELETE FROM %s WHERE %s='%s'", 
					BA_PATH_TABLE_NAME,
					BA_PATH_ROW_PATH, CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void) db_.execDML(sql);
			}
			for(std::list<std::wstring>::const_iterator it = selectPath.begin();
				it != selectPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s) VALUES('%s',%d)", 
					BA_PATH_TABLE_NAME,
					BA_PATH_ROW_PATH,
					BA_PATH_ROW_TYPE,
					CppSQLiteUtility::formatSqlStr(*it).c_str(),
					BAP_Select);
				(void) db_.execDML(sql);
			}
			for(std::list<std::wstring>::const_iterator it = filterPath.begin();
				it != filterPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s) VALUES('%s',%d)", 
					BA_PATH_TABLE_NAME,
					BA_PATH_ROW_PATH,
					BA_PATH_ROW_TYPE,
					CppSQLiteUtility::formatSqlStr(*it).c_str(),
					BAP_Filter);
				(void) db_.execDML(sql);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteAllPathInfo()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("DELETE FROM %s", BA_PATH_TABLE_NAME);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t updateVolumeInfo(const std::set<std::wstring>& selectPath, const std::map<std::wstring, int64_t>& oldVolumeInfo)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			CppSQLite3Buffer delSql;
			(void)delSql.format("DELETE FROM %s", BA_VOLUME_TABLE_NAME);
			(void)db_.execDML(delSql);

			for(std::set<std::wstring>::const_iterator it = selectPath.begin();
				it != selectPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s) VALUES('%s')", 
					BA_VOLUME_TABLE_NAME,
					BA_VOLUME_ROW_PATH,
					CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void) db_.execDML(sql);
			}

			for(std::map<std::wstring, int64_t>::const_iterator itO = oldVolumeInfo.begin();
				itO != oldVolumeInfo.end(); ++itO)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s) VALUES('%s',%d)", 
					BA_VOLUME_TABLE_NAME,
					BA_VOLUME_ROW_PATH,
					BA_VOLUME_ROW_STATUS,
					CppSQLiteUtility::formatSqlStr(itO->first).c_str(),
					BAVS_Delete);
				(void) db_.execDML(sql);
			}

			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteVolumeInfo()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer delSql;
			(void)delSql.format("DELETE FROM %s WHERE %s=%d", 
				BA_VOLUME_TABLE_NAME,
				BA_VOLUME_ROW_STATUS, BAVS_Delete);
			(void)db_.execDML(delSql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	void createBackupTaskTable(const std::wstring& parent)
	{
		try
		{
			if (!Utility::FS::is_directory(parent))
			{
				Utility::FS::create_directories(parent);
			}
			std::wstring path = parent+PATH_DELIMITER+SQLITE_BATASKINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(BA_TASK_TABLE_NAME))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
					%s INTEGER PRIMARY KEY NOT NULL,\
					%s INTEGER,\
					%s VARCHAR,\
					%s INTEGER NOT NULL,\
					%s INTEGER NOT NULL,\
					%s INTEGER NOT NULL,\
					%s INTEGER NOT NULL,\
					%s INTEGER NOT NULL,\
					%s INTEGER NOT NULL);",
					BA_TASK_TABLE_NAME,
					BA_TASK_ROW_ID,
					BA_TASK_ROW_TYPE,
					BA_TASK_ROW_USER_DEFINE,
					BA_TASK_ROW_NEXT_START_TIME,
					BA_TASK_ROW_STATUS,
					BA_TASK_ROW_FIRST_START_TIME,
					BA_TASK_ROW_CUR_START_TIME,
					BA_TASK_ROW_CUR_RUN_TIME,
					BA_TASK_ROW_ISFILTER_CHANGE);
				(void)db_.execDML(sql);
			}

			if(!db_.tableExists(BA_VOLUME_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s VARCHAR PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d);", 
									BA_VOLUME_TABLE_NAME,
									BA_VOLUME_ROW_PATH,
									BA_VOLUME_ROW_USN, 0,
									BA_VOLUME_ROW_STATUS, BAVS_Init);
				(void)db_.execDML(bufSQL);
			}

			if(!db_.tableExists(BA_PATH_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (%s VARCHAR PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL);", 
									BA_PATH_TABLE_NAME,
									BA_PATH_ROW_PATH,
									BA_PATH_ROW_LOCAL_ID, -1,
									BA_PATH_ROW_TYPE);
				(void)db_.execDML(bufSQL);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

private:
	boost::mutex mutex_;
	CppSQLite3DB db_;
};


BackupAllTaskDb* BackupAllTaskDb::create(const std::wstring& parent)
{
	return static_cast<BackupAllTaskDb*>(new BackupAllTaskDbImpl(parent));
}