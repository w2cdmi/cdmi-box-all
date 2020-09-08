#include "UploadTable.h"
#include "Utility.h"
#include "SyncUtility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("UploadTable")
#endif

class UploadTableImpl : public UploadTable
{
public:
	UploadTableImpl(const std::wstring& parent, const long& offset)
	{
		offset_ = offset;
		std::wstring path = parent + SQLITE_UPLOAD_TABLE; 
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			db_.open(SD::Utility::String::wstring_to_string(path).c_str());
			if(!db_.tableExists(UPLOAD_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL);", 
									UPLOAD_TABLE_NAME, UPLOAD_ROW_ID, UPLOAD_ROW_LASTTIME);
				(void)db_.execDML(bufSQL);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	virtual ~UploadTableImpl(void)
	{
	}

	virtual int32_t replaceFilterInfo(const int64_t& local_id)
	{
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			db_.beginTransaction();
			const char* sqlStr = bufSQL.format("REPLACE INTO %s (%s,%s) VALUES(%lld,%d)", 
				UPLOAD_TABLE_NAME, 
				UPLOAD_ROW_ID,
				UPLOAD_ROW_LASTTIME, 
				local_id, 
				time(NULL));
			int32_t iModify = db_.execDML(bufSQL);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual bool isFilter(const int64_t& local_id)
	{
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%lld LIMIT 0,1", 
								UPLOAD_ROW_LASTTIME,
								UPLOAD_TABLE_NAME,
								UPLOAD_ROW_ID, local_id);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return false;
			}
			
			long lastTime = qSet.getIntField(0);
			if((lastTime+offset_)>(long)time(NULL))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "is upload filter. local id:%lld", local_id);
				return true;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;	
	}

	virtual int32_t getAllUploadInfo(IdList& idList)
	{
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s", 
								UPLOAD_ROW_ID,
								UPLOAD_TABLE_NAME);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			
			while(!qSet.eof())
			{
				idList.push_back(qSet.getInt64Field(0));
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteUplaodInfo(const IdList & idList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(m_mutexDB);
		try
		{
			CppSQLite3Buffer sql;
			std::string inStr = Sync::getInStr(idList);
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s IN(%s)", 
				UPLOAD_TABLE_NAME, 
				UPLOAD_ROW_ID, 
				inStr.c_str());

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	boost::mutex m_mutexDB;
	CppSQLite3DB db_;
	long offset_;
};

std::auto_ptr<UploadTable> UploadTable::create(const std::wstring& parent, const long& offset)
{
	return std::auto_ptr<UploadTable>(new UploadTableImpl(parent, offset));
}