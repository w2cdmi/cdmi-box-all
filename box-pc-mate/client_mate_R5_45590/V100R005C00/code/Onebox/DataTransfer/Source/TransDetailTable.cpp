#include "TransDetailTable.h"
#include "TransTableDefine.h"
#include "CppSQLite3.h"
#include <boost/thread/mutex.hpp>
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransDetailTable")
#endif

#define TABLE_ROW_ALL \
	TTRN_SOURCE,\
	TTRN_PARENT,\
	TTRN_NAME,\
	TTRN_FILETYPE,\
	TTRN_STATUS,\
	TTRN_STATUSEX,\
	TTRN_SIZE,\
	TTRN_ERRORCODE

class TransDetailTable::Impl
{
public:
	Impl(const std::wstring& path)
		:path_(path)
	{

	}

	int32_t create(AsyncTransRootNode& rootNode)
	{
		if (path_.empty())
		{
			return RT_INVALID_PARAM;
		}

		try
		{
			std::wstring dir = Utility::FS::get_parent_path(path_);
			if (!Utility::FS::is_directory(dir))
			{
				Utility::FS::create_directories(dir);
			}

			db_.open(Utility::String::wstring_to_utf8(path_).c_str());
			if(!db_.tableExists(TTN_DETAIL))
			{
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE TABLE %s (\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s TEXT NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER NOT NULL,\
									 %s INTEGER DEFAULT 0 NOT NULL,\
									 %s INTEGER)", 
									 TTN_DETAIL, 
									 TTRN_SOURCE, TTRN_PARENT, TTRN_NAME, 
									 TTRN_FILETYPE, TTRN_STATUS, TTRN_STATUSEX, 
									 TTRN_SIZE, TTRN_ERRORCODE);
					(void)db_.execDML(sql);
				}
				{
					CppSQLite3Buffer sql;
					(void)sql.format("CREATE INDEX idx_source_status_parent_type_statusEx ON %s(%s,%s,%s,%s)", 
						TTN_DETAIL, TTRN_SOURCE, TTRN_STATUS, TTRN_PARENT, TTRN_STATUSEX);
					(void)db_.execDML(sql);
				}
			}

			rootNode_ = rootNode;

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addNode(const AsyncTransDetailNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s',%d,%d,%d,%lld,%d)", 
				TTN_DETAIL, 
				TABLE_ROW_ALL,
				CppSQLiteUtility::formatSqlStr(node->source).c_str(),
				CppSQLiteUtility::formatSqlStr(node->parent).c_str(),
				CppSQLiteUtility::formatSqlStr(node->name).c_str(),
				node->fileType,
				node->status,
				node->statusEx,
				node->size,
				node->errorCode);
			(void)db_.execDML(sql);

			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			if (NULL != rootNode_.get())
			{
				printObj->addField<std::string>(Utility::String::wstring_to_string(rootNode_->group));
			}
			printObj->lastField<std::string>(Utility::String::wstring_to_string(node->source));
			HSLOG_TRACE(MODULE_NAME, RT_OK, "add detail node. [%s]", printObj->getMsg().c_str());

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
	AsyncTransRootNode rootNode_;
};

TransDetailTable::TransDetailTable(const std::wstring& path)
	:impl_(new Impl(path))
{

}

int32_t TransDetailTable::create(AsyncTransRootNode& rootNode)
{
	return impl_->create(rootNode);
}

int32_t TransDetailTable::addNode(const AsyncTransDetailNode& node)
{
	return impl_->addNode(node);
}
