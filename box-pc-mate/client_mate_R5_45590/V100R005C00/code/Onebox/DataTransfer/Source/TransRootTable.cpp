#include "TransRootTable.h"
#include "TransTableDefine.h"
#include "CppSQLite3.h"
#include <boost/thread/mutex.hpp>
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransRootTable")
#endif

#define MAX_CACHE_COUNT (10)
#define TTRN_INCREMENT ("id")

#define TABLE_ROW_ALL \
	TTRN_GROUP,\
	TTRN_SOURCE,\
	TTRN_PARENT,\
	TTRN_NAME,\
	TTRN_TYPE,\
	TTRN_FILETYPE,\
	TTRN_STATUS,\
	TTRN_STATUSEX,\
	TTRN_USERID,\
	TTRN_USERTYPE,\
	TTRN_USERNAME,\
	TTRN_PRIORITY,\
	TTRN_SIZE,\
	TTRN_TRANSEDSIZE,\
	TTRN_ERRORCODE

class TransRootTable::Impl
{
public:
	Impl(const std::wstring& path)
		:path_(path)
	{
	}

	~Impl()
	{
	}

	int32_t create()
	{
		if (path_.empty())
		{
			return RT_INVALID_PARAM;
		}

		try
		{
			db_.open(Utility::String::wstring_to_utf8(path_).c_str());
			if(!db_.tableExists(TTN_ROOT))
			{
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE TABLE %s (\
									 %s INTEGER PRIMARY KEY AUTOINCREMENT,\
									 %s TEXT UNIQUE NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s TEXT,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER DEFAULT 0 NOT NULL,\
									 %s INTEGER DEFAULT 0 NOT NULL,\
									 %s INTEGER)", 
									 TTN_ROOT, 
									 TTRN_INCREMENT, 
									 TABLE_ROW_ALL);
					(void)db_.execDML(sql);
				}
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE INDEX idx_group_status_type ON %s(%s,%s,%s)", 
						TTN_ROOT, TTRN_GROUP, TTRN_STATUS, TTRN_TYPE);
					(void)db_.execDML(sql);
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addNodes(const AsyncTransRootNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			AsyncTransRootNode node;
			for (AsyncTransRootNodes::const_iterator itor = nodes.begin(); itor != nodes.end(); ++itor)
			{
				node = *itor;
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES\
												('%s','%s','%s','%s',%d,%d,%d,%d,%lld,%d,'%s',%d,%lld,%lld,%d)", 
												TTN_ROOT, 
												TABLE_ROW_ALL,
												Utility::String::wstring_to_utf8(node->group).c_str(),
												CppSQLiteUtility::formatSqlStr(node->source).c_str(),
												CppSQLiteUtility::formatSqlStr(node->parent).c_str(),
												CppSQLiteUtility::formatSqlStr(node->name).c_str(),
												node->type,
												node->fileType,
												node->status,
												node->statusEx,
												node->userId,
												node->userType,
												CppSQLiteUtility::formatSqlStr(node->userName).c_str(),
												node->priority,
												node->size,
												node->transedSize,
												node->errorCode);
				(void)db_.execDML(sql);
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->group));
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->source));
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->parent));
				printObj->lastField<std::string>(Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "add root nodes. size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());

			return RT_OK;
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"SQLite DML ErrorCode: %d ErrorMessage: %s ",
				e.errorCode(), e.errorMessage());
			if (e.errorCode() == SQLITE_CONSTRAINT)
			{
				db_.rollbackTransaction();
				return RT_SQLITE_EXIST;
			}
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"the InsertSingle function occur unknown exception", NULL);
		}
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

private:
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
};

TransRootTable::TransRootTable(const std::wstring& path)
	:impl_(new Impl(path))
{

}

int32_t TransRootTable::create()
{
	return impl_->create();
}

int32_t TransRootTable::addNodes(const AsyncTransRootNodes& nodes)
{
	return impl_->addNodes(nodes);
}
