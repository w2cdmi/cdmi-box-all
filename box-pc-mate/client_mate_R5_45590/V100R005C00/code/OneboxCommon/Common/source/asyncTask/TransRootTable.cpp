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

typedef std::list<AsyncTransRootNode> CacheNodes;

//////////////////////////////////////////////////////////////////////////
// 1. status, transedSize are cached all the time because of frequent change
// 2. delete and others are not cached
// 3. in cache mode, data may lose in some extreme condition (like power lose ...)
//////////////////////////////////////////////////////////////////////////
class TransRootTable::Impl
{
public:
	Impl(UserContext* userContext, const std::wstring& path)
		:userContext_(userContext)
		,path_(path)
		,cacheMode_(ATCM_NoCache)
		,cacheChangeType_(ATCCT_NONE)
	{
		cacheNodes_.clear();
	}

	~Impl()
	{
		(void)flushCache();
	}

	int32_t create(const AsyncTransCacheMode cacheMode)
	{
		if (path_.empty())
		{
			return RT_INVALID_PARAM;
		}

		cacheMode_ = cacheMode;

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
			// load the cache
			else if (cacheMode_ == ATCM_CacheAll)
			{
				CppSQLite3Query q;
				CppSQLite3Buffer sql;
				(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s", 
					TABLE_ROW_ALL, 
					TTN_ROOT);
				q = db_.execQuery(sql);
				while (!q.eof())
				{
					AsyncTransRootNode node(new (std::nothrow)st_AsyncTransRootNode);
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
					node->status = AsyncTransStatus(q.getIntField(6));
					node->statusEx = AsyncTransStatusEx(q.getIntField(7));
					node->userId = q.getInt64Field(8);
					node->userType = UserContextType(q.getIntField(9));
					node->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
					node->priority = q.getIntField(11);
					node->size = q.getInt64Field(12);
					node->transedSize = q.getInt64Field(13);
					node->errorCode = q.getIntField(14);

					cacheNodes_.push_back(node);
					q.nextRow();
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t flushCache()
	{
		boost::mutex::scoped_lock lock(mutex_);
		int32_t ret = flushCacheNoLock();
		if (RT_OK != ret)
		{
			return ret;
		}
		// if the use count is 1, means the cache node is not been used
		if (cacheMode_ == ATCM_CachePart)
		{
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end();)
			{
				if (it->use_count() == 1)
				{
					it = cacheNodes_.erase(it);
					continue;
				}
				++it;
			}
		}
		return RT_OK;
	}

	int32_t getTopNodes(AsyncTransRootNodes& nodes, const uint32_t count)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// search node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if ((*it)->status == ATS_Running && (*it)->fileType != FILE_TYPE_FILE)
				{
					nodes.push_back(*it);
					if (nodes.size() >= count)
					{
						return RT_OK;
					}
				}
			}
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if ((*it)->status == ATS_Waiting)
				{
					nodes.push_back(*it);
					if (nodes.size() >= count)
					{
						return RT_OK;
					}
				}
			}
			if (!nodes.empty())
			{
				return RT_OK;
			}
			return RT_SQLITE_NOEXIST;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// search node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if ((*it)->status == ATS_Running && (*it)->fileType != FILE_TYPE_FILE)
				{
					nodes.push_back(*it);
					if (nodes.size() >= count)
					{
						return RT_OK;
					}
				}
			}
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if ((*it)->status == ATS_Waiting)
				{
					nodes.push_back(*it);
					if (nodes.size() >= count)
					{
						return RT_OK;
					}
				}
			}

			// if the change type is strong, should flush cache into database
			// then load data from database to cache
			if (cacheChangeType_ == ATCCT_STRONG)
			{
				int32_t ret = flushCacheNoLock();
				if (RT_OK !=  ret)
				{
					return ret;
				}
				// clear the cache
				for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end();)
				{
					if (it->use_count() == 1)
					{
						it = cacheNodes_.erase(it);
						continue;
					}
					++it;
				}
			}
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			std::string limitFormatStr = Utility::String::format_string(" LIMIT 0,%d", MAX_CACHE_COUNT);
			if (ATCM_NoCache)
			{
				limitFormatStr = Utility::String::format_string(" LIMIT 0,%d", count);
			}
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE ((%s&%d)<>0 AND %s<>%d) OR ((%s&%d)<>0 AND %s=%d) ORDER BY %s DESC,%s%s", 
				TABLE_ROW_ALL, 
				TTN_ROOT, 
				TTRN_STATUS, ATS_Waiting|ATS_Running, 
				TTRN_FILETYPE, FILE_TYPE_FILE, 
				TTRN_STATUS, ATS_Waiting, 
				TTRN_FILETYPE, FILE_TYPE_FILE, 
				TTRN_STATUS, 
				TTRN_INCREMENT, 
				limitFormatStr.c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				if (cacheMode_ == ATCM_CachePart && !nodes.empty())
				{
					return RT_OK;
				}
				return RT_SQLITE_NOEXIST;
			}
			CacheNodes tmpCacheNodes;
			while (!q.eof())
			{
				AsyncTransRootNode tmpNode(new (std::nothrow)st_AsyncTransRootNode);
				if (NULL == tmpNode.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}

				tmpNode->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				tmpNode->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				tmpNode->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				tmpNode->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				tmpNode->type = AsyncTransType(q.getIntField(4));
				tmpNode->fileType = FILE_TYPE(q.getIntField(5));
				tmpNode->status = AsyncTransStatus(q.getIntField(6));
				tmpNode->statusEx = AsyncTransStatusEx(q.getIntField(7));
				tmpNode->userId = q.getInt64Field(8);
				tmpNode->userType = UserContextType(q.getIntField(9));
				tmpNode->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
				tmpNode->priority = q.getIntField(11);
				tmpNode->size = q.getInt64Field(12);
				tmpNode->transedSize = q.getInt64Field(13);
				tmpNode->errorCode = q.getIntField(14);

				if (cacheMode_ == ATCM_CachePart)
				{
					// filter repeated nodes
					bool found = false;
					for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
					{
						if ((*it)->group == tmpNode->group)
						{
							found = true;
							break;
						}
					}
					if (!found)
					{
						cacheNodes_.push_back(tmpNode);
						
					}
					if (!found && nodes.size() < count)
					{
						nodes.push_back(tmpNode);
					}
				}
				else if (cacheMode_ == ATCM_NoCache)
				{
					nodes.push_back(tmpNode);
				}

				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getTopScanNode(AsyncTransRootNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// search node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->statusEx&ATSEX_Scanning) != 0 && 
					((*it)->status == ATS_Waiting || (*it)->status == ATS_Running))
				{
					node = *it;
					return RT_OK;
				}
			}
			return RT_SQLITE_NOEXIST;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// search node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->statusEx&ATSEX_Scanning) != 0 && 
					((*it)->status == ATS_Waiting || (*it)->status == ATS_Running))
				{
					node = *it;
					return RT_OK;
				}
			}
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0 AND %s IN (%d,%d) ORDER BY %s LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TTN_ROOT, 
				TTRN_STATUSEX, ATSEX_Scanning, 
				TTRN_STATUS, ATS_Waiting, ATS_Running, 
				TTRN_INCREMENT);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			node.reset(new (std::nothrow)st_AsyncTransRootNode);
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
			node->status = AsyncTransStatus(q.getIntField(6));
			node->statusEx = AsyncTransStatusEx(q.getIntField(7));
			node->userId = q.getInt64Field(8);
			node->userType = UserContextType(q.getIntField(9));
			node->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
			node->priority = q.getIntField(11);
			node->size = q.getInt64Field(12);
			node->transedSize = q.getInt64Field(13);
			node->errorCode = q.getIntField(14);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNode(const std::wstring& group, AsyncTransRootNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// search node from the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				node = *it;
				return RT_OK;
			}
			return RT_SQLITE_NOEXIST;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// search node from the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				node = *it;
				return RT_OK;
			}
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s' LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TTN_ROOT, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.reset(new (std::nothrow)st_AsyncTransRootNode);
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
			node->status = AsyncTransStatus(q.getIntField(6));
			node->statusEx = AsyncTransStatusEx(q.getIntField(7));
			node->userId = q.getInt64Field(8);
			node->userType = UserContextType(q.getIntField(9));
			node->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
			node->priority = q.getIntField(11);
			node->size = q.getInt64Field(12);
			node->transedSize = q.getInt64Field(13);
			node->errorCode = q.getIntField(14);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodes(const AsyncTransType type, AsyncTransRootNodes& nodes, const Page& page)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// search node from the cache
		if (cacheMode_ == ATCM_CacheAll)
		{
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->type&type) != 0)
				{
					nodes.push_back(*it);
				}
			}
			return (nodes.empty() ? RT_SQLITE_NOEXIST : RT_OK);
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			std::string limitFormatStr = "";
			if (page.offset > 0)
			{
				limitFormatStr = Utility::String::format_string(" LIMIT %d,%d", page.start, page.offset);
			}
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0 ORDER BY %s %s", 
				TABLE_ROW_ALL, 
				TTN_ROOT, 
				TTRN_TYPE, type, 
				TTRN_INCREMENT,
				limitFormatStr.c_str()
				);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransRootNode node(new (std::nothrow)st_AsyncTransRootNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				if (cacheMode_ == ATCM_CachePart)
				{
					// search node from the cache
					CacheNodes::iterator it = find(node->group);
					if (it != cacheNodes_.end())
					{
						node = *it;
						nodes.push_back(node);
						q.nextRow();
						continue;
					}
				}				
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = AsyncTransType(q.getIntField(4));
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->status = AsyncTransStatus(q.getIntField(6));
				node->statusEx = AsyncTransStatusEx(q.getIntField(7));
				node->userId = q.getInt64Field(8);
				node->userType = UserContextType(q.getIntField(9));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
				node->priority = q.getIntField(11);
				node->size = q.getInt64Field(12);
				node->transedSize = q.getInt64Field(13);
				node->errorCode = q.getIntField(14);
				
				nodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodes(const AsyncTransStatus status, AsyncTransRootNodes& nodes, const Page& page)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// search node from the cache
		if (cacheMode_ == ATCM_CacheAll)
		{
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->status&status) != 0)
				{
					nodes.push_back(*it);
				}
			}
			return (nodes.empty() ? RT_SQLITE_NOEXIST : RT_OK);
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			std::string limitFormatStr = "";
			if (page.offset > 0)
			{
				limitFormatStr = Utility::String::format_string(" LIMIT %d,%d", page.start, page.offset);
			}
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE (%s&%d)<>0%s", 
				TABLE_ROW_ALL, 
				TTN_ROOT, 
				TTRN_STATUS, status, 
				limitFormatStr.c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			while (!q.eof())
			{
				AsyncTransRootNode node(new (std::nothrow)st_AsyncTransRootNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
				if (cacheMode_ == ATCM_CachePart)
				{
					// search node from the cache
					CacheNodes::iterator it = find(node->group);
					if (it != cacheNodes_.end())
					{
						node = *it;
						nodes.push_back(node);
						q.nextRow();
						continue;
					}
				}				
				node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
				node->parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node->name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node->type = AsyncTransType(q.getIntField(4));
				node->fileType = FILE_TYPE(q.getIntField(5));
				node->status = AsyncTransStatus(q.getIntField(6));
				node->statusEx = AsyncTransStatusEx(q.getIntField(7));
				node->userId = q.getInt64Field(8);
				node->userType = UserContextType(q.getIntField(9));
				node->userName = Utility::String::utf8_to_wstring(q.getStringField(10));
				node->priority = q.getIntField(11);
				node->size = q.getInt64Field(12);
				node->transedSize = q.getInt64Field(13);
				node->errorCode = q.getIntField(14);
				
				nodes.push_back(node);
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;		
		return RT_SQLITE_ERROR;
	}

	int32_t addNode(const AsyncTransRootNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// update the cache
		if (cacheMode_ == ATCM_CacheAll)
		{
			CacheNodes::iterator it = find(node->group);
			if (it != cacheNodes_.end())
			{
				return RT_SQLITE_EXIST;
			}
			cacheNodes_.push_back(node);
			cacheChangeType_ = ATCCT_STRONG;
			//return RT_OK;
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES\
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
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		catch (CppSQLite3Exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"SQLite DML ErrorCode: %d ErrorMessage: %s ",
				e.errorCode(), e.errorMessage());
			if (e.errorCode() == SQLITE_CONSTRAINT)
			{
				return RT_SQLITE_EXIST;
			}
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"the InsertSingle function occur unknown exception", NULL);
		}
		return RT_SQLITE_ERROR;
	}

	int32_t addNodes(const AsyncTransRootNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		// update the cache
		if (cacheMode_ == ATCM_CacheAll)
		{
			AsyncTransRootNode node;
			for (AsyncTransRootNodes::const_iterator itor = nodes.begin(); itor != nodes.end(); ++itor)
			{
				node = *itor;
				CacheNodes::iterator it = find(node->group);
				if (it != cacheNodes_.end())
				{
					return RT_SQLITE_EXIST;
				}
				cacheNodes_.push_back(node);
				cacheChangeType_ = ATCCT_STRONG;
			}			
			//return RT_OK;
		}

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

	int32_t updateNode(const AsyncTransRootNode& node)
	{
		return RT_NOT_IMPLEMENT;
	}

	int32_t deleteNode(const std::wstring& group)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// delete node from the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				HSLOG_TRACE(MODULE_NAME, RT_OK, "delete root node of %s.", 
					Utility::String::wstring_to_string(group).c_str());
				cacheNodes_.erase(it);
				cacheChangeType_ = ATCCT_STRONG;
				//return RT_OK;
			}
			//return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// delete node from the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				cacheNodes_.erase(it);
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNodes(const AsyncTransType type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// delete node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end();)
			{
				if (((*it)->type&type) != 0)
				{
					HSLOG_TRACE(MODULE_NAME, RT_OK, "delete root node of %s.", 
						Utility::String::wstring_to_string((*it)->group).c_str());
					it = cacheNodes_.erase(it);
					cacheChangeType_ = ATCCT_STRONG;
					continue;
				}
				++it;
			}
			//return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// delete node from the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end();)
			{
				if (((*it)->type&type) != 0)
				{
					it = cacheNodes_.erase(it);
					cacheChangeType_ = ATCCT_STRONG;
					continue;
				}
				++it;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE (%s&%d)<>0", 
				TTN_ROOT, 
				TTRN_TYPE, type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatus(const std::wstring& group, const AsyncTransStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->status = status;
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			// status change frequently, do not update database
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->status = status;
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_STATUS, status, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatus(const AsyncTransType type, const AsyncTransStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->type&type) != 0)
				{
					(*it)->status = status;
					cacheChangeType_ = ATCCT_STRONG;
				}
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->type&type) != 0)
				{
					(*it)->status = status;
					cacheChangeType_ = ATCCT_STRONG;
				}
			}
		}		

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE (%s&%d)<>0", 
				TTN_ROOT, 
				TTRN_STATUS, status, 
				TTRN_TYPE, type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addStatusEx(const std::wstring& group, const AsyncTransStatusEx status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->statusEx = AsyncTransStatusEx((*it)->statusEx|status);
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->statusEx = AsyncTransStatusEx((*it)->statusEx|status);
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s|%d) WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_STATUSEX, TTRN_STATUSEX, status, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t removeStatusEx(const std::wstring& group, const AsyncTransStatusEx status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->statusEx = AsyncTransStatusEx((*it)->statusEx&(~status));
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->statusEx = AsyncTransStatusEx((*it)->statusEx&(~status));
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=(%s&(~%d)) WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_STATUSEX, TTRN_STATUSEX, status, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
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
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->status&status) != 0)
				{
					(*it)->status = value;
					cacheChangeType_ = ATCCT_STRONG;
				}
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				if (((*it)->status&status) != 0)
				{
					(*it)->status = value;
					cacheChangeType_ = ATCCT_STRONG;
				}
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE (%s&%d)<>0", 
				TTN_ROOT, 
				TTRN_STATUS, value, 
				TTRN_STATUS, status);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateSize(const std::wstring& group, const int64_t size)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->size = size;
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->size = size;
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_SIZE, size, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateTransedSizeAndSize(const std::wstring& group, const int64_t transedIncrement, const int64_t sizeIncement)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->size += sizeIncement;
				(*it)->transedSize += transedIncrement;
				if ((*it)->size < (*it)->transedSize)
				{
					(*it)->transedSize = (*it)->size;
				}
				if (cacheChangeType_ != ATCCT_STRONG)
				{
					cacheChangeType_ = ATCCT_WEAK;
				}
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			// transedSize change frequently, do not update database
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->size += sizeIncement;
				(*it)->transedSize += transedIncrement;
				if ((*it)->size < (*it)->transedSize)
				{
					(*it)->transedSize = (*it)->size;
				}
				if (cacheChangeType_ != ATCCT_STRONG)
				{
					cacheChangeType_ = ATCCT_WEAK;
				}
				return RT_OK;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s+%lld, %s=%s+%lld WHERE %s='%s' AND (%s+%lld)<(%s+%lld)", 
				TTN_ROOT, 
				TTRN_TRANSEDSIZE, TTRN_TRANSEDSIZE, transedIncrement, 
				TTRN_SIZE, TTRN_SIZE, sizeIncement, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str(), 
				TTRN_TRANSEDSIZE, transedIncrement, TTRN_SIZE, sizeIncement);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updatePriority(const std::wstring& group, const bool inc)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->priority += inc?(-PRIORITY_INCREMENT):PRIORITY_INCREMENT;
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->priority += inc?(-PRIORITY_INCREMENT):PRIORITY_INCREMENT;
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s+(%d) WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_PRIORITY, TTRN_PRIORITY, inc?(-PRIORITY_INCREMENT):PRIORITY_INCREMENT, 
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateStatusAndErrorCode(const std::wstring& group, const AsyncTransStatus status, const int32_t errorCode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->status = status;
				(*it)->errorCode = errorCode;
				cacheChangeType_ = ATCCT_STRONG;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update node in the cache
			CacheNodes::iterator it = find(group);
			if (it != cacheNodes_.end())
			{
				(*it)->status = status;
				(*it)->errorCode = errorCode;
				cacheChangeType_ = ATCCT_STRONG;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s=%d WHERE %s='%s'", 
				TTN_ROOT, 
				TTRN_STATUS, status,
				TTRN_ERRORCODE, errorCode,
				TTRN_GROUP, CppSQLiteUtility::formatSqlStr(group).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t getNodesCount(const AsyncTransType type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			return cacheNodes_.size();
		}

		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			(void)sql.format("SELECT COUNT(0) FROM %s WHERE (%s&%d)<>0", 
				TTN_ROOT, 
				TTRN_TYPE, type);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	int32_t flushCacheNoLock()
	{
		if (cacheMode_ == ATCM_NoCache || cacheNodes_.empty() || cacheChangeType_ == ATCCT_NONE)
		{
			return RT_OK;
		}

		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			AsyncTransRootNode node;
			db_.beginTransaction();

			if (cacheMode_ == ATCM_CacheAll)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("DELETE FROM %s", TTN_ROOT);
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

				for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
				{
					node = *it;
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
					printObj->addField<int32_t>(node->status);
					printObj->addField<int32_t>(node->statusEx);
					printObj->addField<int32_t>(node->priority);
					printObj->lastField<int64_t>(node->transedSize);
				}
			}
			else if (cacheMode_ == ATCM_CachePart)
			{
				for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
				{
					node = *it;
					CppSQLite3Buffer sql;
					(void)sql.format("UPDATE %s SET %s='%s',%s='%s',%s='%s',\
									 %s=%d,%s=%d,%s=%d,%s=%d,%s=%lld,%s=%d,%s='%s',%s=%d,\
									 %s=%lld,%s=%lld,%s=%d WHERE %s='%s'", 
									 TTN_ROOT, 
									 TTRN_SOURCE, CppSQLiteUtility::formatSqlStr(node->source).c_str(), 
									 TTRN_PARENT, CppSQLiteUtility::formatSqlStr(node->parent).c_str(), 
									 TTRN_NAME, CppSQLiteUtility::formatSqlStr(node->name).c_str(), 
									 TTRN_TYPE, node->type,
									 TTRN_FILETYPE, node->fileType,
									 TTRN_STATUS, node->status,
									 TTRN_STATUSEX, node->statusEx,
									 TTRN_USERID, node->userId,
									 TTRN_USERTYPE, node->userType,
									 TTRN_USERNAME, CppSQLiteUtility::formatSqlStr(node->userName).c_str(),
									 TTRN_PRIORITY, node->priority,
									 TTRN_SIZE, node->size,
									 TTRN_TRANSEDSIZE, node->transedSize,
									 TTRN_ERRORCODE, node->errorCode, 
									 TTRN_GROUP, Utility::String::wstring_to_utf8(node->group).c_str());
					(void)db_.execDML(sql);
					printObj->addField<std::string>(Utility::String::wstring_to_string(node->group));
					printObj->addField<std::string>(Utility::String::wstring_to_string(node->source));
					printObj->addField<std::string>(Utility::String::wstring_to_string(node->parent));
					printObj->addField<std::string>(Utility::String::wstring_to_string(node->name));
					printObj->addField<int32_t>(node->type);
					printObj->addField<int32_t>(node->fileType);
					printObj->addField<int64_t>(node->userId);					
					printObj->addField<int32_t>(node->status);
					printObj->addField<int32_t>(node->statusEx);
					printObj->addField<int32_t>(node->priority);
					printObj->lastField<int64_t>(node->transedSize);
				}
			}
			
			db_.commitTransaction();
			
			HSLOG_TRACE(MODULE_NAME, RT_OK, "flush root nodes. size:%d, [%s]", cacheNodes_.size(), printObj->getMsg().c_str());

			cacheChangeType_ = ATCCT_NONE;

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	CacheNodes::iterator find(const std::wstring& group)
	{
		CacheNodes::iterator it = cacheNodes_.begin();
		for (; it != cacheNodes_.end(); ++it)
		{
			if ((*it)->group == group)
			{
				break;
			}
		}
		return it;
	}

private:
	UserContext* userContext_;
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
	AsyncTransCacheMode cacheMode_;
	AsyncTransCacheChangeType cacheChangeType_;
	CacheNodes cacheNodes_;
};

TransRootTable::TransRootTable(UserContext* userContext, const std::wstring& path)
	:impl_(new Impl(userContext, path))
{

}

int32_t TransRootTable::create(const AsyncTransCacheMode cacheMode)
{
	return impl_->create(cacheMode);
}

int32_t TransRootTable::flushCache()
{
	return impl_->flushCache();
}

int32_t TransRootTable::getTopNodes(AsyncTransRootNodes& nodes, const uint32_t count)
{
	return impl_->getTopNodes(nodes, count);
}

int32_t TransRootTable::getTopScanNode(AsyncTransRootNode& node)
{
	return impl_->getTopScanNode(node);
}

int32_t TransRootTable::getNode(const std::wstring& group, AsyncTransRootNode& node)
{
	return impl_->getNode(group, node);
}

int32_t TransRootTable::getNodes(const AsyncTransType type, AsyncTransRootNodes& nodes, const Page& page)
{
	return impl_->getNodes(type, nodes, page);
}

int32_t TransRootTable::getNodes(const AsyncTransStatus status, AsyncTransRootNodes& nodes, const Page& page)
{
	return impl_->getNodes(status, nodes, page);
}

int32_t TransRootTable::addNode(const AsyncTransRootNode& node)
{
	return impl_->addNode(node);
}

int32_t TransRootTable::addNodes(const AsyncTransRootNodes& nodes)
{
	return impl_->addNodes(nodes);
}

int32_t TransRootTable::updateNode(const AsyncTransRootNode& node)
{
	return impl_->updateNode(node);
}

int32_t TransRootTable::deleteNode(const std::wstring& group)
{
	return impl_->deleteNode(group);
}

int32_t TransRootTable::deleteNodes(const AsyncTransType type)
{
	return impl_->deleteNodes(type);
}

int32_t TransRootTable::updateStatus(const std::wstring& group, const AsyncTransStatus status)
{
	return impl_->updateStatus(group, status);
}

int32_t TransRootTable::updateStatus(const AsyncTransType type, const AsyncTransStatus status)
{
	return impl_->updateStatus(type, status);
}

int32_t TransRootTable::addStatusEx(const std::wstring& group, const AsyncTransStatusEx status)
{
	return impl_->addStatusEx(group, status);
}

int32_t TransRootTable::removeStatusEx(const std::wstring& group, const AsyncTransStatusEx status)
{
	return impl_->removeStatusEx(group, status);
}

int32_t TransRootTable::updateStatus(const AsyncTransStatus status, const AsyncTransStatus value)
{
	return impl_->updateStatus(status, value);
}

int32_t TransRootTable::updateSize(const std::wstring& group, const int64_t size)
{
	return impl_->updateSize(group, size);
}

int32_t TransRootTable::updateTransedSizeAndSize(const std::wstring& group, const int64_t transIncrement, const int64_t sizeIncrement)
{
	return impl_->updateTransedSizeAndSize(group, transIncrement, sizeIncrement);
}

int32_t TransRootTable::updatePriority(const std::wstring& group, const bool inc)
{
	return impl_->updatePriority(group, inc);
}

int32_t TransRootTable::updateStatusAndErrorCode(const std::wstring& group, const AsyncTransStatus status, const int32_t errorCode)
{
	return impl_->updateStatusAndErrorCode(group, status, errorCode);
}

int32_t TransRootTable::getNodesCount(const AsyncTransType type)
{
	return impl_->getNodesCount(type);
}
