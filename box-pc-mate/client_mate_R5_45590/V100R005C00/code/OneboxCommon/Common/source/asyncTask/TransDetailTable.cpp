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
	Impl(UserContext* userContext, const std::wstring& path)
		:userContext_(userContext)
		,path_(path)
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
									 %s TEXT NOT NULL UNIQUE,\
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

	int32_t remove()
	{
		if (path_.empty())
		{
			return RT_INVALID_PARAM;
		}
		if (!Utility::FS::is_exist(path_))
		{
			return RT_OK;
		}

		// close database first, then remove the database file
		db_.close();

		return Utility::FS::remove(path_);
	}

	int32_t getTopNodes(AsyncTransDetailNodes& nodes, const int64_t fileSize, const uint32_t count)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			if (fileSize > 0)
			{
				(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s=%d AND (%s=%d OR %s=%d) AND %s<%lld LIMIT 0,%d", 
					TABLE_ROW_ALL, 
					TTN_DETAIL, 
					TTRN_STATUS, ATS_Waiting,
					TTRN_STATUSEX, 0,
					TTRN_STATUSEX, ATSEX_VirtualParent,
					TTRN_SIZE, fileSize, 
					count);
			}
			else
			{
				(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s=%d AND (%s=%d OR %s=%d) LIMIT 0,%d", 
					TABLE_ROW_ALL, 
					TTN_DETAIL, 
					TTRN_STATUS, ATS_Waiting, 
					TTRN_STATUSEX, 0,
					TTRN_STATUSEX, ATSEX_VirtualParent,
					count);
			}
			
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransDetailNode node(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}

				node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->fileType = FILE_TYPE(q.getIntField(3));
				node->status = AsyncTransStatus(q.getIntField(4));
				node->statusEx = AsyncTransStatusEx(q.getIntField(5));
				node->size = q.getInt64Field(6);
				node->errorCode = q.getIntField(7);

				node->root = rootNode_;
				nodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getTopScanNode(AsyncTransDetailNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0 AND (%s&%d)=0 LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TTN_DETAIL, 
				TTRN_STATUSEX, ATSEX_Scanning,
				TTRN_STATUS, ATS_Error);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.reset(new (std::nothrow)st_AsyncTransDetailNode);
			if (NULL == node.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}
			node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
			node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
			node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
			node->fileType = FILE_TYPE(q.getIntField(3));
			node->status = AsyncTransStatus(q.getIntField(4));
			node->statusEx = AsyncTransStatusEx(q.getIntField(5));
			node->size = q.getInt64Field(6);
			node->errorCode = q.getIntField(7);
			node->root = rootNode_;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNode(const std::wstring& source, AsyncTransDetailNode& node)
	{
		if (source.empty())
		{
			return RT_SQLITE_NOEXIST;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s' LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TTN_DETAIL, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.reset(new (std::nothrow)st_AsyncTransDetailNode);
			if (NULL == node.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}
			node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
			node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
			node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
			node->fileType = FILE_TYPE(q.getIntField(3));
			node->status = AsyncTransStatus(q.getIntField(4));
			node->statusEx = AsyncTransStatusEx(q.getIntField(5));
			node->size = q.getInt64Field(6);
			node->errorCode = q.getIntField(7);
			node->root = rootNode_;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodes(const AsyncTransStatus status, AsyncTransDetailNodes& nodes, const Page& page)
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
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0%s", 
				TABLE_ROW_ALL, 
				TTN_DETAIL, 
				TTRN_STATUS, status, 
				limitFormatStr.c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransDetailNode node(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->fileType = FILE_TYPE(q.getIntField(3));
				node->status = AsyncTransStatus(q.getIntField(4));
				node->statusEx = AsyncTransStatusEx(q.getIntField(5));
				node->size = q.getInt64Field(6);
				node->errorCode = q.getIntField(7);
				node->root = rootNode_;
				nodes.push_back(node);
				q.nextRow();
			}
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

	int32_t addNodes(const AsyncTransDetailNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			if (NULL != rootNode_.get())
			{
				printObj->lastField<std::string>(Utility::String::wstring_to_string(rootNode_->group));
			}
			
			db_.beginTransaction();
			AsyncTransDetailNode node;
			for (AsyncTransDetailNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
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
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->source));
				printObj->lastField<std::string>(Utility::String::wstring_to_string(node->parent));
				//printObj->lastField<std::string>(Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "add detail nodes. size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNode(const std::wstring& source)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatus(const std::wstring& source, const AsyncTransStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUS, status, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatus(const AsyncTransStatus status, const AsyncTransStatus value)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE (%s&%d)<>0", 
				TTN_DETAIL, 
				TTRN_STATUS, value, 
				TTRN_STATUS, status);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addStatusEx(const std::wstring& source, const AsyncTransStatusEx status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s|%d) WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, TTRN_STATUSEX, status, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t removeStatusEx(const std::wstring& source, const AsyncTransStatusEx status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s&(~%d)) WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, TTRN_STATUSEX, status, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateParentAndStatus(const std::wstring& oldParent, const std::wstring& newParent)
	{
		if (oldParent.empty() || newParent.empty())
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s', %s=(%s&(~%d)) WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(newParent).c_str(), 
				TTRN_STATUSEX, TTRN_STATUSEX, ATSEX_Uninitial, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(oldParent).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatusAndErrorCode(const std::wstring& source, const AsyncTransStatus status, const int32_t errorCode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s=%d WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUS, status,
				TTRN_ERRORCODE, errorCode,
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getScanNodesCount()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT COUNT(0) FROM %s WHERE (%s&%d)<>0", 
				TTN_DETAIL, 
				TTRN_STATUSEX, ATSEX_Scanning);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodesCount(const AsyncTransStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT COUNT(0) FROM %s WHERE (%s&%d)<>0", 
				TTN_DETAIL, 
				TTRN_STATUS, status);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t listFilesByStatusEx(const std::wstring& parent, AsyncTransStatusEx statusEx, const Page& page, AsyncTransDetailNodes& nodes)
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
 			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s = '%s' and %s = %d %s", 
				TABLE_ROW_ALL, 
				TTN_DETAIL, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(parent).c_str(), 
				TTRN_STATUSEX, statusEx,
				limitFormatStr.c_str());
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				AsyncTransDetailNode node(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->fileType = FILE_TYPE(q.getIntField(3));
				node->status = AsyncTransStatus(q.getIntField(4));
				node->statusEx = AsyncTransStatusEx(q.getIntField(5));
				node->size = q.getInt64Field(6);
				node->errorCode = q.getIntField(7);
				node->root = rootNode_;
				nodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateParent(const std::wstring& oldParent, const std::wstring& newParent)
	{
		if (oldParent.empty() || newParent.empty())
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s' WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(newParent).c_str(), 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(oldParent).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatusForBatchUpload(const std::wstring& parent)
	{
		if (parent.empty())
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s=(%s&(~%d)) WHERE %s='%s' AND (%s = %d OR (%s&%d)<>0 OR %s >= %lld)", 
				TTN_DETAIL, 
				TTRN_STATUS, ATS_Waiting,
				TTRN_STATUSEX, TTRN_STATUSEX, ATSEX_Uninitial, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(parent).c_str(),
				TTRN_FILETYPE, FILE_TYPE_DIR,
				TTRN_STATUSEX, ATSEX_VirtualParent,
				TTRN_SIZE, BATCH_PREUPLOAD_FILE_SIZE);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNodes(const std::list<std::wstring>& sources)
	{
		if (sources.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for (std::list<std::wstring>::const_iterator it = sources.begin(); it != sources.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("DELETE FROM %s WHERE %s='%s'", 
					TTN_DETAIL, 
					TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void)db_.execDML(sql);
				printObj->lastField<std::string>(Utility::String::wstring_to_string(*it));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "delete detail nodes, size %d, [%s]", sources.size(), printObj->getMsg().c_str());

			return RT_OK;			
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t batchUpdateParentAndErrorCode(const std::list<std::wstring>& sources, const std::wstring& parent, const int32_t errorCode)
	{
		if (sources.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for (std::list<std::wstring>::const_iterator it = sources.begin(); it != sources.end(); ++it)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s='%s', %s=%d, %s=(%s&(~%d)), %s=%d WHERE %s='%s'", 
					TTN_DETAIL, 
					TTRN_PARENT, CppSQLiteUtility::formatSqlStr(parent).c_str(),
					TTRN_STATUS, ATS_Error,
					TTRN_STATUSEX, TTRN_STATUSEX, ATSEX_Uninitial,
					TTRN_ERRORCODE, errorCode,
					TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(*it).c_str());
				int32_t iModify = db_.execDML(sql);
				printObj->lastField<std::string>(Utility::String::wstring_to_string(*it));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "Update detail node status to error, size %d, [%s]", sources.size(), printObj->getMsg().c_str());

			return RT_OK;			
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t removeStatusExByParent(const std::wstring& parent, const AsyncTransStatusEx status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s&(~%d)) WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, TTRN_STATUSEX, status, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(parent).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t batchUpdateStatus(const std::list<std::wstring>& sources)
	{
		if (sources.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for (std::list<std::wstring>::const_iterator it = sources.begin(); it != sources.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("UPDATE %s SET %s=%d, %s=(%s&(~%d)) WHERE %s='%s'", 
					TTN_DETAIL, 
					TTRN_STATUS, ATS_Waiting,
					TTRN_STATUSEX, TTRN_STATUSEX, ATSEX_Uninitial, 
					TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(*it).c_str());
				int32_t iModify = db_.execDML(sql);
				printObj->lastField<std::string>(Utility::String::wstring_to_string(*it));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "Batch update files status to waiting, size %d, [%s]", sources.size(), printObj->getMsg().c_str());

			return RT_OK;			
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t setNextVirtualParent(const AsyncTransDetailNode& detailNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s from %s where %s = '%s' and %s = %d and %s = %d and size < %lld LIMIT 0,1", 
				TABLE_ROW_ALL,
				TTN_DETAIL, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(detailNode->parent).c_str(),
				TTRN_STATUS, ATS_Waiting,
				TTRN_STATUSEX, ATSEX_Uninitial,
				BATCH_PREUPLOAD_FILE_SIZE);
			q = db_.execQuery(sql);

			AsyncTransDetailNode node;
			if (!q.eof())
			{
				node.reset(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->fileType = FILE_TYPE(q.getIntField(3));
				node->status = AsyncTransStatus(q.getIntField(4));
				node->statusEx = AsyncTransStatusEx(q.getIntField(5));
				node->size = q.getInt64Field(6);
				node->errorCode = q.getIntField(7);
				node->root = rootNode_;
			}

			db_.beginTransaction();

			// Remove old virtual parent status;
			(void)sql.format("UPDATE %s SET %s=(%s&(~%d)) WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, TTRN_STATUSEX, ATSEX_VirtualParent, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(detailNode->source).c_str());
			db_.execDML(sql);

			if(NULL == node.get())
			{
				db_.commitTransaction();
				return RT_OK;
			}
			
			// Set new virtual parent
			(void)sql.format("UPDATE %s SET %s=%d WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, ATSEX_VirtualParent,
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(node->source).c_str());
			int32_t iModify = db_.execDML(sql);
			if(iModify == 0)
			{
				db_.rollbackTransaction();
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Set new virtual parent failed. Source: %s, modified: %d", 
					Utility::String::wstring_to_string(node->source).c_str(), iModify);
				return RT_ERROR;
			}
			db_.commitTransaction();
			SERVICE_INFO(MODULE_NAME, RT_OK, "Remove virtual parent node: %s, and set the next virtual parent node: %s.", 
				Utility::String::wstring_to_string(detailNode->source).c_str(), Utility::String::wstring_to_string(node->source).c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	bool isError()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT DISTINCT %s FROM %s WHERE (%s&%d)=0",  
				TTRN_STATUS, 
				TTN_DETAIL,
				TTRN_STATUSEX, ATSEX_Uninitial);
			q = db_.execQuery(sql);
			AsyncTransStatus status = ATS_Invalid;
			while (!q.eof())
			{
				status = AsyncTransStatus(status|q.getIntField(0));
				q.nextRow();
			}
			// only error or error|cancel
			if ((status&ATS_Error) != 0 && (status&(~(ATS_Error|ATS_Cancel))) == 0)
			{
				return true;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	int32_t updateSize(const std::wstring& source, const int64_t sizeIncement)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s+%lld WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_SIZE, TTRN_SIZE, sizeIncement, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t checkAndAddNodes(const AsyncTransDetailNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			if (NULL != rootNode_.get())
			{
				printObj->lastField<std::string>(Utility::String::wstring_to_string(rootNode_->group));
			}

			db_.beginTransaction();
			
			int32_t statusExMask = 0;
			{
				CppSQLite3Query q;
				CppSQLite3Buffer sql;
				(void)sql.format("SELECT %s FROM %s WHERE %s = '%s' and (%s&%d) <> 0 LIMIT 0,1", 
					TTRN_SOURCE,
					TTN_DETAIL, 
					TTRN_PARENT,
					CppSQLiteUtility::formatSqlStr(nodes.front()->parent).c_str(),
					TTRN_STATUSEX, ATSEX_VirtualParent);
				q = db_.execQuery(sql);
				
				if (!q.eof())
				{
					statusExMask = ATSEX_VirtualParent;
					SERVICE_INFO(MODULE_NAME, RT_OK, "Virtual parent node is already exist. %s", 
						Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(q.getStringField(0))).c_str());
				}
			}

			AsyncTransDetailNode node;
			for (AsyncTransDetailNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s',%d,%d,%d,%lld,%d)", 
					TTN_DETAIL, 
					TABLE_ROW_ALL,
					CppSQLiteUtility::formatSqlStr(node->source).c_str(),
					CppSQLiteUtility::formatSqlStr(node->parent).c_str(),
					CppSQLiteUtility::formatSqlStr(node->name).c_str(),
					node->fileType,
					node->status,
					node->statusEx&(~statusExMask),
					node->size,
					node->errorCode);
				(void)db_.execDML(sql);
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->source));
				printObj->lastField<std::string>(Utility::String::wstring_to_string(node->parent));
				//printObj->lastField<std::string>(Utility::String::wstring_to_string(node->name));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "add detail nodes. size:%d, [%s]", nodes.size(), printObj->getMsg().c_str());

			return RT_OK;
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR, 
				"SQLite DML ErrorCode: %d ErrorMessage: %s ", 
				e.errorCode(), e.errorMessage());
			// convert the error code, the caller may use it
			if (SQLITE_CONSTRAINT == e.errorCode())
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

	int32_t getRepeatedNodes(AsyncTransDetailNodes& repeatedNodes, const AsyncTransDetailNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_SQLITE_NOEXIST;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::string inStr = "";
			AsyncTransDetailNodes::const_iterator it = nodes.begin();
			inStr += "'" + CppSQLiteUtility::formatSqlStr((*it)->source) + "'";
			for (++it; it != nodes.end(); ++it)
			{
				inStr += ",'" + CppSQLiteUtility::formatSqlStr((*it)->source) + "'";
			}
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s IN (%s)", 
				TABLE_ROW_ALL, 
				TTN_DETAIL, 
				TTRN_SOURCE, inStr.c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransDetailNode node(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->fileType = FILE_TYPE(q.getIntField(3));
				node->status = AsyncTransStatus(q.getIntField(4));
				node->statusEx = AsyncTransStatusEx(q.getIntField(5));
				node->size = q.getInt64Field(6);
				node->errorCode = q.getIntField(7);
				node->root = rootNode_;
				repeatedNodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t checkAndDeleteNode(const AsyncTransDetailNode& detailNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			// Check for missing tasks
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s from %s where %s = '%s' and %s = %d and %s = %d and size < %lld LIMIT 0,1", 
				TABLE_ROW_ALL,
				TTN_DETAIL, 
				TTRN_PARENT, CppSQLiteUtility::formatSqlStr(detailNode->parent).c_str(),
				TTRN_STATUS, ATS_Waiting,
				TTRN_STATUSEX, ATSEX_Uninitial,
				BATCH_PREUPLOAD_FILE_SIZE);
			q = db_.execQuery(sql);

			AsyncTransDetailNode missingNode;
			if (!q.eof())
			{
				missingNode.reset(new (std::nothrow)st_AsyncTransDetailNode);
				if (NULL == missingNode.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				missingNode->source = Utility::String::utf8_to_wstring(q.getStringField(0));
				missingNode->parent = Utility::String::utf8_to_wstring(q.getStringField(1));
				missingNode->name = Utility::String::utf8_to_wstring(q.getStringField(2));
				missingNode->fileType = FILE_TYPE(q.getIntField(3));
				missingNode->status = AsyncTransStatus(q.getIntField(4));
				missingNode->statusEx = AsyncTransStatusEx(q.getIntField(5));
				missingNode->size = q.getInt64Field(6);
				missingNode->errorCode = q.getIntField(7);
				missingNode->root = rootNode_;
			}

			db_.beginTransaction();
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(detailNode->source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			if(NULL == missingNode.get())
			{
				db_.commitTransaction();
				return RT_OK;
			}

			// Update the missing node status to virtual parent
			(void)sql.format("UPDATE %s SET %s=%d WHERE %s='%s'", 
				TTN_DETAIL, 
				TTRN_STATUSEX, ATSEX_VirtualParent,
				TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(missingNode->source).c_str());
			iModify = db_.execDML(sql);
			if(iModify == 0)
			{
				db_.rollbackTransaction();
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Set new virtual parent failed. Source: %s, modified: %d", 
					Utility::String::wstring_to_string(missingNode->source).c_str(), iModify);
				return RT_ERROR;
			}
			db_.commitTransaction();
			SERVICE_INFO(MODULE_NAME, RT_OK, "Delete detail node: %s, and set the next virtual parent node: %s.", 
				Utility::String::wstring_to_string(detailNode->source).c_str(), Utility::String::wstring_to_string(missingNode->source).c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t restoreVirtualParent()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			// get all parents
			std::list<std::string> parents;
			{
				CppSQLite3Buffer sql;
				CppSQLite3Query q;
				sql.format("SELECT DISTINCT %s FROM %s", TTRN_PARENT, TTN_DETAIL);
				q = db_.execQuery(sql);
				while (!q.eof())
				{
					parents.push_back(q.getStringField(0));
					q.nextRow();
				}
			}
			// check the virtual parent is exist or not
			for (std::list<std::string>::iterator it = parents.begin(); it != parents.end();)
			{
				CppSQLite3Buffer sql;
				CppSQLite3Query q;
				sql.format("SELECT COUNT(0) FROM %s WHERE %s=%d AND %s='%s'", 
					TTN_DETAIL, 
					TTRN_STATUSEX, ATSEX_VirtualParent, 
					TTRN_PARENT, CppSQLiteUtility::formatSqlStr(*it).c_str());
				q = db_.execQuery(sql);
				if (q.getIntField(0) > 0)
				{
					it = parents.erase(it);
					continue;
				}
				++it;
			}
			// restore the virtual parent
			for (std::list<std::string>::iterator it = parents.begin(); 
				it != parents.end(); ++it)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE ROWID=(SELECT ROWID FROM %s WHERE %s='%s' AND %s=%d AND %s=%d AND %s<%lld LIMIT 0,1)", 
					TTN_DETAIL, 
					TTRN_STATUSEX, ATSEX_VirtualParent, 
					TTN_DETAIL, 
					TTRN_PARENT, CppSQLiteUtility::formatSqlStr(*it).c_str(), 
					TTRN_STATUS, ATS_Waiting, 
					TTRN_STATUSEX, ATSEX_Uninitial, 
					TTRN_SIZE, BATCH_PREUPLOAD_FILE_SIZE);
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	UserContext* userContext_;
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
	AsyncTransRootNode rootNode_;
};

TransDetailTable::TransDetailTable(UserContext* userContext, const std::wstring& path)
	:impl_(new Impl(userContext, path))
{

}

int32_t TransDetailTable::create(AsyncTransRootNode& rootNode)
{
	return impl_->create(rootNode);
}

int32_t TransDetailTable::remove()
{
	return impl_->remove();
}

int32_t TransDetailTable::getTopNodes(AsyncTransDetailNodes& nodes, const int64_t fileSize, const uint32_t count)
{
	return impl_->getTopNodes(nodes, fileSize, count);
}

int32_t TransDetailTable::getTopScanNode(AsyncTransDetailNode& node)
{
	return impl_->getTopScanNode(node);
}

int32_t TransDetailTable::getNode(const std::wstring& source, AsyncTransDetailNode& node)
{
	return impl_->getNode(source, node);
}

int32_t TransDetailTable::getNodes(const AsyncTransStatus status, AsyncTransDetailNodes& nodes, const Page& page)
{
	return impl_->getNodes(status, nodes, page);
}

int32_t TransDetailTable::addNode(const AsyncTransDetailNode& node)
{
	return impl_->addNode(node);
}

int32_t TransDetailTable::addNodes(const AsyncTransDetailNodes& nodes)
{
	return impl_->addNodes(nodes);
}

int32_t TransDetailTable::deleteNode(const std::wstring& source)
{
	return impl_->deleteNode(source);
}

int32_t TransDetailTable::updateStatus(const std::wstring& source, const AsyncTransStatus status)
{
	return impl_->updateStatus(source, status);
}

int32_t TransDetailTable::updateStatus(const AsyncTransStatus status, const AsyncTransStatus value)
{
	return impl_->updateStatus(status, value);
}

int32_t TransDetailTable::addStatusEx(const std::wstring& source, const AsyncTransStatusEx status)
{
	return impl_->addStatusEx(source, status);
}

int32_t TransDetailTable::removeStatusEx(const std::wstring& source, const AsyncTransStatusEx status)
{
	return impl_->removeStatusEx(source, status);
}

int32_t TransDetailTable::updateParentAndStatus(const std::wstring& oldParent, const std::wstring& newParent)
{
	return impl_->updateParentAndStatus(oldParent, newParent);
}

int32_t TransDetailTable::updateStatusAndErrorCode(const std::wstring& source, const AsyncTransStatus status, const int32_t errorCode)
{
	return impl_->updateStatusAndErrorCode(source, status, errorCode);
}

int32_t TransDetailTable::getScanNodesCount()
{
	return impl_->getScanNodesCount();
}

int32_t TransDetailTable::getNodesCount(const AsyncTransStatus status)
{
	return impl_->getNodesCount(status);
}

int32_t TransDetailTable::deleteNodes(const std::list<std::wstring>& sources)
{
	return impl_->deleteNodes(sources);
}

int32_t TransDetailTable::listFilesByStatusEx(const std::wstring& parent, const AsyncTransStatusEx statusEx, const Page& page, AsyncTransDetailNodes& nodes)
{
	return impl_->listFilesByStatusEx(parent, statusEx, page, nodes);
}

int32_t TransDetailTable::batchUpdateParentAndErrorCode(const std::list<std::wstring>& sources, const std::wstring& parent, const int32_t errorCode)
{
	return impl_->batchUpdateParentAndErrorCode(sources, parent, errorCode);
}

int32_t TransDetailTable::updateParent(const std::wstring& oldParent, const std::wstring& newParent)
{
	return impl_->updateParent(oldParent, newParent);
}

int32_t TransDetailTable::updateStatusForBatchUpload(const std::wstring& parent)
{
	return impl_->updateStatusForBatchUpload(parent);
}

int32_t TransDetailTable::removeStatusExByParent(const std::wstring& parent, const AsyncTransStatusEx status)
{
	return impl_->removeStatusExByParent(parent, status);
}

int32_t TransDetailTable::batchUpdateStatus(const std::list<std::wstring>& sources)
{
	return impl_->batchUpdateStatus(sources);
}

int32_t TransDetailTable::setNextVirtualParent(const AsyncTransDetailNode& transDetailNode)
{
	return impl_->setNextVirtualParent(transDetailNode);
}

bool TransDetailTable::isError()
{
	return impl_->isError();
}

int32_t TransDetailTable::updateSize(const std::wstring& source, const int64_t sizeIncement)
{
	return impl_->updateSize(source, sizeIncement);
}

int32_t TransDetailTable::checkAndAddNodes(const AsyncTransDetailNodes& nodes)
{
	return impl_->checkAndAddNodes(nodes);
}

int32_t TransDetailTable::getRepeatedNodes(AsyncTransDetailNodes& repeatedNodes, const AsyncTransDetailNodes& nodes)
{
	return impl_->getRepeatedNodes(repeatedNodes, nodes);
}

int32_t TransDetailTable::checkAndDeleteNode(const AsyncTransDetailNode& node)
{
	return impl_->checkAndDeleteNode(node);
}

int32_t TransDetailTable::restoreVirtualParent()
{
	return impl_->restoreVirtualParent();
}
