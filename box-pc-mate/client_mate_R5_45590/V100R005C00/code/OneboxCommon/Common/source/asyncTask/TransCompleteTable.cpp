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
	Impl(UserContext* userContext, const std::wstring& path)
		:userContext_(userContext)
		,path_(path)
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

	int32_t getNode(const std::wstring& group, AsyncTransCompleteNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;

			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s'", 
				TABLE_ROW_ALL, 
				TTN_COMPLETE, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				node.reset(new (std::nothrow)st_AsyncTransCompleteNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = AsyncTransType(q.getIntField(4));
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->userId = q.getInt64Field(6);
				node->userType = UserContextType(q.getIntField(7));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(8));
				node->size = q.getInt64Field(9);
				node->completeTime = q.getInt64Field(10);

				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodes(const AsyncTransType type, AsyncTransCompleteNodes& nodes, const Page& page)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			std::string limitFormatStr = "";
			if (page.offset > 0)
			{
				limitFormatStr = Utility::String::format_string(" LIMIT %d,%d", page.start, page.offset);
			}
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0 ORDER BY %s DESC%s", 
				TABLE_ROW_ALL, 
				TTN_COMPLETE, 
				TTRN_TYPE, type, 
				TTRN_COMPLETETIME, 
				limitFormatStr.c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransCompleteNode node(new (std::nothrow)st_AsyncTransCompleteNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = AsyncTransType(q.getIntField(4));
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->userId = q.getInt64Field(6);
				node->userType = UserContextType(q.getIntField(7));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(8));
				node->size = q.getInt64Field(9);
				node->completeTime = q.getInt64Field(10);

				nodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addNode(const AsyncTransCompleteNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES\
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
											time(NULL));
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNode(const std::wstring& group)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TTN_COMPLETE, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
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

	int32_t deleteNodes(const AsyncTransType type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE (%s&%d)<>0", 
				TTN_COMPLETE, 
				TTRN_TYPE, type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	UserContext* userContext_;
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
};

TransCompleteTable::TransCompleteTable(UserContext* userContext, const std::wstring& path)
	:impl_(new Impl(userContext, path))
{

}

int32_t TransCompleteTable::create()
{
	return impl_->create();
}

int32_t TransCompleteTable::getNode(const std::wstring& group, AsyncTransCompleteNode& node)
{
	return impl_->getNode(group, node);
}
int32_t TransCompleteTable::getNodes(const AsyncTransType type, AsyncTransCompleteNodes& nodes, const Page& page)
{
	return impl_->getNodes(type, nodes, page);
}

int32_t TransCompleteTable::addNode(const AsyncTransCompleteNode& node)
{
	return impl_->addNode(node);
}

int32_t TransCompleteTable::deleteNode(const std::wstring& group)
{
	return impl_->deleteNode(group);
}

int32_t TransCompleteTable::addNodes(const AsyncTransCompleteNodes& nodes)
{
	return impl_->addNodes(nodes);
}

int32_t TransCompleteTable::deleteNodes(const AsyncTransType type)
{
	return impl_->deleteNodes(type);
}
