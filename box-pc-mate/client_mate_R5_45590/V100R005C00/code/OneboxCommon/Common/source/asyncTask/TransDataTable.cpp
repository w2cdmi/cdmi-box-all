#include "TransDataTable.h"
#include "TransTableDefine.h"
#include "CppSQLite3.h"
#include <boost/thread/mutex.hpp>
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransDataTable")
#endif

#define TABLE_ROW_ALL \
	TTRN_GROUP,\
	TTRN_SOURCE,\
	TTRN_BLOCKNUM,\
	TTRN_BLOCKS,\
	TTRN_MTIME,\
	TTRN_ALGORITHM,\
	TTRN_FINGERPRINT,\
	TTRN_BLOCKFINGERPRINT,\
	TTRN_USERDEFINE

typedef std::map<std::string, AsyncTransDataNode> CacheNodes;

class TransDataTable::Impl
{
public:
	Impl(UserContext* userContext, const std::wstring& path)
		:userContext_(userContext)
		,path_(path)
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
			if(!db_.tableExists(TTN_DATA))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
								 %s TEXT PRIMARY KEY NOT NULL,\
								 %s TEXT NOT NULL,\
								 %s TEXT NOT NULL,\
								 %s INTEGER DEFAULT 0 NOT NULL,\
								 %s BLOB NULL,\
								 %s INTEGER NOT NULL,\
								 %s INTEGER NOT NULL,\
								 %s TEXT,\
								 %s TEXT,\
								 %s TEXT)", 
								 TTN_DATA, 
								 TTRN_UNIQUEID, 
								 TTRN_GROUP, TTRN_SOURCE, TTRN_BLOCKNUM, TTRN_BLOCKS, 
								 TTRN_MTIME, TTRN_ALGORITHM, TTRN_FINGERPRINT, TTRN_BLOCKFINGERPRINT, 
								 TTRN_USERDEFINE);
				(void)db_.execDML(sql);
			}
			// load the cache
			else if (cacheMode_ == ATCM_CacheAll)
			{
				CppSQLite3Query q;
				CppSQLite3Buffer sql;
				(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s", 
					TABLE_ROW_ALL, 
					TTRN_UNIQUEID, 
					TTN_DATA);
				q = db_.execQuery(sql);
				while (!q.eof())
				{
					AsyncTransDataNode node(new (std::nothrow)st_AsyncTransDataNode);
					if (NULL == node.get())
					{
						return RT_MEMORY_MALLOC_ERROR;
					}

					node->group = Utility::String::utf8_to_wstring(q.getStringField(0));
					node->source = Utility::String::utf8_to_wstring(q.getStringField(1));
					node->blocks.blockNum = q.getIntField(2);
					if (node->blocks.blockNum > 0)
					{
						node->blocks.blocks = new (std::nothrow)AsyncTransBlock[node->blocks.blockNum];
						if (NULL == node->blocks.blocks)
						{
							return RT_MEMORY_MALLOC_ERROR;
						}
						CppSQLite3Binary blob;
						blob.setEncoded((unsigned char*)q.fieldValue(3));
						int len = sizeof(AsyncTransBlock) * node->blocks.blockNum;
						memcpy_s(node->blocks.blocks, len, blob.getBinary(), len);
					}
					node->mtime = q.getInt64Field(4);
					node->fingerprint.algorithm = q.getIntField(5);
					node->fingerprint.fingerprint = q.getStringField(6);
					node->fingerprint.blockFingerprint = q.getStringField(7);
					node->userDefine = Utility::String::utf8_to_wstring(q.getStringField(8));

					cacheNodes_[q.getStringField(9)] = node;
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
		if (cacheMode_ == ATCM_NoCache || cacheNodes_.empty())
		{
			return RT_OK;
		}

		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();

			if (cacheMode_ == ATCM_CacheAll)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("DELETE FROM %s", TTN_DATA);
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end(); ++it)
			{
				AsyncTransDataNode& node = it->second;
				CppSQLite3Binary blob;
				int len = node->blocks.blockNum * sizeof(AsyncTransBlock);
				unsigned char* buf = blob.allocBuffer(len);
				memcpy_s(buf, len, node->blocks.blocks, len);
				CppSQLite3Buffer sql;
				(void)sql.format("REPLACE INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s',%d,%Q,%lld,%d,'%s','%s','%s')", 
					TTN_DATA, 
					TTRN_UNIQUEID, 
					TABLE_ROW_ALL, 
					getUniqueId(node->group, node->source).c_str(), 
					Utility::String::wstring_to_utf8(node->group).c_str(), 
					CppSQLiteUtility::formatSqlStr(node->source).c_str(), 
					node->blocks.blockNum, 
					blob.getEncoded(), 
					node->mtime, 
					node->fingerprint.algorithm, 
					node->fingerprint.fingerprint.c_str(), 
					node->fingerprint.blockFingerprint.c_str(), 
					CppSQLiteUtility::formatSqlStr(node->userDefine).c_str());
				(void)db_.execDML(sql);
				printObj->addField<std::string>(Utility::String::wstring_to_string(node->group));
				printObj->lastField<std::string>(Utility::String::wstring_to_string(node->source));
			}
			db_.commitTransaction();

			HSLOG_TRACE(MODULE_NAME, RT_OK, "flush data nodes. size:%d, [%s]", cacheNodes_.size(), printObj->getMsg().c_str());

			// if the use count is 1, means the cache node is not been used
			if (cacheMode_ == ATCM_CachePart || cacheMode_ == ATCM_CacheAll)
			{
				for (CacheNodes::iterator it = cacheNodes_.begin(); it != cacheNodes_.end();)
				{
					if (it->second.use_count() == 1)
					{
						it = cacheNodes_.erase(it);
						continue;
					}
					++it;
				}
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t getNode(const std::wstring& group, const std::wstring& source, AsyncTransDataNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// search node from the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				node = it->second;
				return RT_OK;
			}
			node.reset(new (std::nothrow)st_AsyncTransDataNode);
			if (NULL == node.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}
			node->group = group;
			node->source = source;
			cacheNodes_[getUniqueId(group, source)] = node;
			return RT_OK;
			//return RT_SQLITE_NOEXIST;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// search node from the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				node = it->second;
				return RT_OK;
			}
		}

		try
		{
			CppSQLite3Query q;
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s' LIMIT 0,1", 
				TABLE_ROW_ALL, 
				TTRN_UNIQUEID, 
				TTN_DATA, 
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			q = db_.execQuery(sql);
			if (q.eof())
			{
				node.reset(new (std::nothrow)st_AsyncTransDataNode);
				if (NULL == node.get())
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				node->group = group;
				node->source = source;
				if (cacheMode_ == ATCM_CachePart)
				{
					cacheNodes_[getUniqueId(group, source)] = node;
				}
				return RT_OK;
				//return RT_SQLITE_NOEXIST;
			}
			node.reset(new (std::nothrow)st_AsyncTransDataNode);
			if (NULL == node.get())
			{
				return RT_MEMORY_MALLOC_ERROR;
			}

			node->group = group;
			node->source = source;
			node->blocks.blockNum = q.getIntField(2);
			if (node->blocks.blockNum > 0)
			{
				node->blocks.blocks = new (std::nothrow)AsyncTransBlock[node->blocks.blockNum];
				if (NULL == node->blocks.blocks)
				{
					return RT_MEMORY_MALLOC_ERROR;
				}
				CppSQLite3Binary blob;
				blob.setEncoded((unsigned char*)q.fieldValue(3));
				int len = sizeof(AsyncTransBlock) * node->blocks.blockNum;
				memcpy_s(node->blocks.blocks, len, blob.getBinary(), len);
			}
			node->mtime = q.getInt64Field(4);
			node->fingerprint.algorithm = q.getIntField(5);
			node->fingerprint.fingerprint = q.getStringField(6);
			node->fingerprint.blockFingerprint = q.getStringField(7);
			node->userDefine = Utility::String::utf8_to_wstring(q.getStringField(8));

			if (cacheMode_ == ATCM_CachePart)
			{
				cacheNodes_[q.getStringField(9)] = node;
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t replaceNode(const AsyncTransDataNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			cacheNodes_[getUniqueId(node->group, node->source)] = node;
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			cacheNodes_[getUniqueId(node->group, node->source)] = node;
			//return RT_OK;
		}

		try
		{
			CppSQLite3Binary blob;
			int len = node->blocks.blockNum * sizeof(AsyncTransBlock);
			unsigned char* buf = blob.allocBuffer(len);
			memcpy_s(buf, len, node->blocks.blocks, len);
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("REPLACE INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s',%d,%Q,%lld,%d,'%s','%s','%s')", 
				TTN_DATA, 
				TTRN_UNIQUEID, 
				TABLE_ROW_ALL, 
				getUniqueId(node->group, node->source).c_str(), 
				Utility::String::wstring_to_utf8(node->group).c_str(), 
				CppSQLiteUtility::formatSqlStr(node->source).c_str(), 
				node->blocks.blockNum, 
				blob.getEncoded(), 
				node->mtime, 
				node->fingerprint.algorithm, 
				node->fingerprint.fingerprint.c_str(), 
				node->fingerprint.blockFingerprint.c_str(), 
				CppSQLiteUtility::formatSqlStr(node->userDefine).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t deleteNode(const std::wstring& group, const std::wstring& source)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				cacheNodes_.erase(it);
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				cacheNodes_.erase(it);
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TTN_DATA, 
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateBlocks(const std::wstring& group, const std::wstring& source, const AsyncTransBlocks& blocks)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->blocks.blockNum = blocks.blockNum;
				for (uint32_t i = 0; i < it->second->blocks.blockNum; ++i)
				{
					it->second->blocks.blocks[i].offset = blocks.blocks[i].offset;
					it->second->blocks.blocks[i].blockOffset = blocks.blocks[i].blockOffset;
					it->second->blocks.blocks[i].blockSize = blocks.blocks[i].blockSize;
				}
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			// the block information may change frequently, do not update the database
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->blocks.blockNum = blocks.blockNum;
				for (uint32_t i = 0; i < it->second->blocks.blockNum; ++i)
				{
					it->second->blocks.blocks[i].offset = blocks.blocks[i].offset;
					it->second->blocks.blocks[i].blockOffset = blocks.blocks[i].blockOffset;
					it->second->blocks.blocks[i].blockSize = blocks.blocks[i].blockSize;
				}
				return RT_OK;
			}
		}

		try
		{
			CppSQLite3Binary blob;
			int len = blocks.blockNum * sizeof(AsyncTransBlock);
			unsigned char* buf = blob.allocBuffer(len);
			memcpy_s(buf, len, blocks.blocks, len);
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%d, %s=%Q WHERE %s='%s'", 
				TTN_DATA, 
				TTRN_BLOCKNUM, blocks.blockNum,
				TTRN_BLOCKS, blob.getEncoded(), 
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			(void)db_.execDML(sql);

			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			for (uint32_t i = 0; i < blocks.blockNum; ++i)
			{
				printObj->lastField<std::string>(Utility::String::format_string(
					"%d-%I64d-%I64d-%I64d", i, 
					blocks.blocks[i].blockSize, 
					blocks.blocks[i].offset, 
					blocks.blocks[i].blockOffset));
			}
			HSLOG_TRACE(MODULE_NAME, RT_OK, "update blocks information of %s, %s: %s.", 
				Utility::String::wstring_to_string(group).c_str(), 
				Utility::String::wstring_to_string(source).c_str(), 
				printObj->getMsg().c_str());

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updatemTime(const std::wstring& group, const std::wstring& source, const int64_t mtime)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->mtime = mtime;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->mtime = mtime;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s='%s'", 
				TTN_DATA, 
				TTRN_MTIME, mtime, 
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateFingerprint(const std::wstring& group, const std::wstring& source, const Fingerprint& fingerprint)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->fingerprint = fingerprint;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->fingerprint = fingerprint;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s='%s', %s='%s' WHERE %s='%s'", 
				TTN_DATA, 
				TTRN_ALGORITHM, fingerprint.algorithm,
				TTRN_FINGERPRINT, fingerprint.fingerprint.c_str(), 
				TTRN_BLOCKFINGERPRINT, fingerprint.blockFingerprint.c_str(),
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t updateUserDefine(const std::wstring& group, const std::wstring& source, const std::wstring& userDefine)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (cacheMode_ == ATCM_CacheAll)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->userDefine = userDefine;
				return RT_OK;
			}
			return RT_OK;
		}
		else if (cacheMode_ == ATCM_CachePart)
		{
			// update the cache
			CacheNodes::iterator it = cacheNodes_.find(getUniqueId(group, source));
			if (it != cacheNodes_.end())
			{
				it->second->userDefine = userDefine;
			}
		}

		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s' WHERE %s='%s'", 
				TTN_DATA, 
				TTRN_USERDEFINE, CppSQLiteUtility::formatSqlStr(userDefine).c_str(), 
				TTRN_UNIQUEID, getUniqueId(group, source).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}
	
	int32_t addNodes(const AsyncTransDataNodes& nodes)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			AsyncTransDataNode node;
			for (AsyncTransDataNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				node = *it;
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s',%d,%Q,%lld,%d,'%s','%s','%s')", 
					TTN_DATA, 
					TTRN_UNIQUEID, 
					TABLE_ROW_ALL, 
					getUniqueId(node->group, node->source).c_str(), 
					Utility::String::wstring_to_utf8(node->group).c_str(), 
					CppSQLiteUtility::formatSqlStr(node->source).c_str(), 
					node->blocks.blockNum, 
					node->blocks.blocks, 
					node->mtime, 
					node->fingerprint.algorithm, 
					node->fingerprint.fingerprint.c_str(), 
					node->fingerprint.blockFingerprint.c_str(), 
					CppSQLiteUtility::formatSqlStr(node->userDefine).c_str());
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			db_.commitTransaction();
			HSLOG_TRACE(MODULE_NAME, RT_OK, "Batch add data nodes. Group: %s, total: %d", Utility::String::wstring_to_string(node->group).c_str(), nodes.size());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

private:
	std::string getUniqueId(const std::wstring& group, const std::wstring& source)
	{
		return Utility::String::wstring_to_string(
			Utility::MD5::getMD5ByString(
			Utility::String::wstring_to_utf8(group + source).c_str()));
	}

private:
	UserContext* userContext_;
	std::wstring path_;
	CppSQLite3DB db_;
	boost::mutex mutex_;
	AsyncTransCacheMode cacheMode_;
	CacheNodes cacheNodes_;
};

TransDataTable::TransDataTable(UserContext* userContext, const std::wstring& path)
	:impl_(new Impl(userContext, path))
{

}

int32_t TransDataTable::create(const AsyncTransCacheMode cacheMode)
{
	return impl_->create(cacheMode);
}

int32_t TransDataTable::flushCache()
{
	return impl_->flushCache();
}

int32_t TransDataTable::getNode(const std::wstring& group, const std::wstring& source, AsyncTransDataNode& node)
{
	return impl_->getNode(group, source, node);
}

int32_t TransDataTable::replaceNode(const AsyncTransDataNode& node)
{
	return impl_->replaceNode(node);
}

int32_t TransDataTable::deleteNode(const std::wstring& group, const std::wstring& source)
{
	return impl_->deleteNode(group, source);
}

int32_t TransDataTable::updateBlocks(const std::wstring& group, const std::wstring& source, const AsyncTransBlocks& blocks)
{
	return impl_->updateBlocks(group, source, blocks);
}

int32_t TransDataTable::updatemTime(const std::wstring& group, const std::wstring& source, const int64_t mtime)
{
	return impl_->updatemTime(group, source, mtime);
}

int32_t TransDataTable::updateFingerprint(const std::wstring& group, const std::wstring& source, const Fingerprint& fingerprint)
{
	return impl_->updateFingerprint(group, source, fingerprint);
}

int32_t TransDataTable::updateUserDefine(const std::wstring& group, const std::wstring& source, const std::wstring& userDefine)
{
	return impl_->updateUserDefine(group, source, userDefine);
}

int32_t TransDataTable::addNodes(const AsyncTransDataNodes& nodes)
{
	return impl_->addNodes(nodes);
}
