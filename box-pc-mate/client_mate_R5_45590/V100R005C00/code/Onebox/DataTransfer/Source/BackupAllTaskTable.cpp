#include "BackupAllTaskTable.h"
#include "Utility.h"
#include "BackupAllUtility.h"
#include <boost/thread/mutex.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllTaskTable")
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

class BackupAllTaskTableImpl : public BackupAllTaskTable
{
public:
	BackupAllTaskTableImpl(const std::wstring& parent)
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

	virtual int32_t updatePathInfo(const std::set<std::wstring>& selectPath, const std::set<std::wstring>& filterPath)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(std::set<std::wstring>::const_iterator it = selectPath.begin();
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

			for(std::set<std::wstring>::const_iterator it = filterPath.begin();
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

			for(std::set<std::wstring>::const_iterator it = filterPath.begin();
				it != filterPath.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s) VALUES('%s')", 
					BA_VOLUME_TABLE_NAME,
					BA_VOLUME_ROW_PATH,
					CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void) db_.execDML(sql);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
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


BackupAllTaskTable* BackupAllTaskTable::create(const std::wstring& parent)
{
	return static_cast<BackupAllTaskTable*>(new BackupAllTaskTableImpl(parent));
}