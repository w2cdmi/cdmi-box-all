#include "TransCompleteTable.h"
#include "TransTableDefine.h"
#include "CppSQLite3.h"
#include <boost/thread/mutex.hpp>
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransCompleteTable")
#endif

#define TABLE_ROW_ALL \
	TTRN_GROUP,\
	TTRN_SOURCE,\
	TTRN_PARENT,\
	TTRN_NAME,\
	TTRN_TYPE,\
	TTRN_FILETYPE,\
	TTRN_USERID,\
	TTRN_USERTYPE,\
	TTRN_USERNAME,\
	TTRN_SIZE,\
	TTRN_COMPLETETIME

class TransCompleteTable::Impl
{
public:
	Impl(const std::wstring& path)
		:path_(path)
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
			if(!db_.tableExists(TTN_COMPLETE))
			{
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE TABLE %s (\
									 %s TEXT PRIMARY KEY NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s TEXT,\
									 %s INTEGER DEFAULT 0 NOT NULL,\
									 %s INTEGER)", 
									 TTN_COMPLETE, 
									 TABLE_ROW_ALL);
					(void)db_.execDML(sql);
				}
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE INDEX idx_time ON %s(%s)", TTN_COMPLETE, TTRN_COMPLETETIME);
					(void)db_.execDML(sql);
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addNodes(const AsyncTransCompleteNodes& nodes)
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
			AsyncTransCompleteNode node;
			for (AsyncTransCompleteNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES\
								 ('%s','%s','%s','%s',%d,%d,%lld,%d,'%s',%lld,%lld)", 
								 TTN_COMPLETE, 
								 TABLE_ROW_ALL,
								 Utility::String::wstring_to_utf8(node->group).c_str(),
								 CppSQLiteUtility::formatSqlStr(node->source).c_str(),
								 CppSQLiteUtility::formatSqlStr(node->parent).c_str(),
								 CppSQLiteUtility::formatSqlStr(node->name).c_str(),
								 node->type,
								 node->fileType,
								 node->userId,
								 node->userType,
								 CppSQLiteUtility::formatSqlStr(node->userName).c_str(),
								 node->size, 
								 node->completeTime);
				(void)db_.execDML(sql);
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->group));
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->source));
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->parent));
				printObj->lastField<std::string>(Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "add detail nodes. size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

private:
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
};

TransCompleteTable::TransCompleteTable(const std::wstring& path)
	:impl_(new Impl(path))
{

}

int32_t TransCompleteTable::create()
{
	return impl_->create();
}

int32_t TransCompleteTable::addNodes(const AsyncTransCompleteNodes& nodes)
{
	return impl_->addNodes(nodes);
}
