#include "CacheMetaData.h"
#include "CppSQLite3.h"
#include "Utility.h"
#include "ErrorCode.h"
#include "CommonDefine.h"
#include "CacheCommon.h"
#include "NotifyMgr.h"
#include <boost/thread.hpp>
#include "CacheMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("CacheMetaData")
#endif

class CacheMetaDataImpl : public CacheMetaData
{
public:
	CacheMetaDataImpl(UserContext* userContext, const std::wstring& parent, bool isMyFile):userContext_(userContext)
	{
		createCMDTable(parent);
		lastParent_.id(-1);
		nextParent_.id(-1);
		pageType_ = isMyFile?Page_MyFile:Page_TeamSpace;
		isFlush_ = false;
	}

	virtual ~CacheMetaDataImpl()
	{
		saveThread_.interrupt();
		saveThread_.join();
	}

	virtual int32_t listPage(int64_t id, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("listPage id:%I64d", id));
		if(INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufCount;
			(void)bufCount.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0, 1", 
								CMD_ROW_COUNT,
								TABLE_CMD,
								CMD_ROW_ID, id);
			CppSQLite3Query qCount = db_.execQuery(bufCount);
			if(!qCount.eof())
			{
				count = qCount.getInt64Field(0);
			}

			std::stringstream orderStr;
			OrderParam orderParam;
			if(pageParam.orderList.empty())
			{
				orderParam.field = CMD_ROW_NAME;
				orderParam.direction = " asc";
			}
			else
			{
				orderParam = pageParam.orderList.back();
			}
			orderStr << "ORDER BY " << CMD_ROW_EXTRATYPE << " DESC, ";
			orderStr << CMD_ROW_TYPE << " ASC, ";
			
			if(CMD_ROW_NAME==orderParam.field)
			{
				orderStr << CMD_ROW_NAME << " collate PINYIN " << orderParam.direction;
			}
			else
			{
				orderStr << orderParam.field << " " << orderParam.direction;
			}

			std::stringstream limitStr;
			if(pageParam.limit > 0)
			{
				limitStr << "LIMIT " << pageParam.offset << ", " << pageParam.limit;
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %lld %s %s", 
								CMD_ROW_ID, CMD_ROW_PARENT, 
								CMD_ROW_TYPE, CMD_ROW_NAME,
								CMD_ROW_SIZE, CMD_ROW_MTIME,
								CMD_ROW_VERSION, CMD_ROW_STATUS,
								CMD_ROW_MNAME,
								CMD_ROW_EXTRATYPE,
								TABLE_CMD,
								CMD_ROW_PARENT,								
								id,
								orderStr.str().c_str(),
								limitStr.str().c_str());

			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				FILE_DIR_INFO fileInfo;
				fileInfo.id = qSet.getInt64Field(0);
				fileInfo.parent = qSet.getInt64Field(1);
				fileInfo.type = qSet.getIntField(2);
				fileInfo.name = SD::Utility::String::utf8_to_wstring(qSet.getStringField(3));
				fileInfo.size = qSet.getInt64Field(4);
				fileInfo.mtime = qSet.getInt64Field(5);
				fileInfo.version = qSet.getIntField(6);
				fileInfo.flags = qSet.getIntField(7);
				fileInfo.modifiedName = SD::Utility::String::utf8_to_wstring(qSet.getStringField(8));
				fileInfo.extraType = SD::Utility::String::utf8_to_wstring(qSet.getStringField(9));
				result.push_back(fileInfo);
				qSet.nextRow();
			}

			if(!result.empty()&&count<result.size())
			{
				CppSQLite3Buffer bufCnt;
				(void)bufCnt.format("SELECT COUNT(1) FROM %s WHERE %s = %lld", 
									TABLE_CMD,
									CMD_ROW_PARENT, id);
				CppSQLite3Query qCnt = db_.execQuery(bufCnt);
				if(!qCnt.eof())
				{
					count = qCnt.getInt64Field(0);
				}
				updateCount(id, count);
				SERVICE_ERROR(MODULE_NAME, RT_OK, "listPage count error. id:%I64d, cnt:%I64d", id, count);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getAllFileId(const int64_t& dirId, std::set<int64_t>& result)
	{
		if(INVALID_ID == dirId)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %lld", 
								CMD_ROW_ID,
								TABLE_CMD,
								CMD_ROW_PARENT, dirId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			while(!qSet.eof())
			{
				result.insert(qSet.getInt64Field(0));
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t flushCache(const Path& path, bool isFlush)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCache id:%I64d", path.id()));
		isFlush_ = isFlush;
		if(isFlush)
		{
			saveCache(path);
			return RT_OK;
		}

		if(lastParent_.id()==path.id())
		{
			return RT_OK;
		}
		
		if(lastParent_.id()!=-1&&lastParent_.id()!=path.id())
		{
			nextParent_ = path;
			SERVICE_INFO(MODULE_NAME, RT_OK, "wait last saveCache. last parentId:%I64d", lastParent_.id());
			return RT_OK;
		}
		lastParent_ = path;
		saveThread_ = boost::thread(boost::bind(&CacheMetaDataImpl::saveCacheAsync, this));

		return RT_OK;
	}

	virtual int32_t flushFileInfo(const Path& path)
	{
		FILE_DIR_INFO info;
		ADAPTER_FILE_TYPE type = (path.type()==FILE_TYPE_DIR)?ADAPTER_FOLDER_TYPE_REST:ADAPTER_FILE_TYPE_REST;
		if(RT_OK==userContext_->getSyncFileSystemMgr()->getProperty(path, info, type))
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,%d,'%s',%lld,%lld,%d,%d,'%s',%lld,'%s','%s')", 
				TABLE_CMD, 
				CMD_ROW_ID, 
				CMD_ROW_PARENT, 
				CMD_ROW_TYPE, 
				CMD_ROW_NAME,
				CMD_ROW_SIZE,
				CMD_ROW_MTIME,
				CMD_ROW_VERSION,
				CMD_ROW_STATUS,
				CMD_ROW_TYPESTR,
				CMD_ROW_MID,
				CMD_ROW_MNAME,
				CMD_ROW_EXTRATYPE,
				info.id,
				info.parent,
				info.type,
				CppSQLiteUtility::formatSqlStr(info.name).c_str(),
				info.size, 
				info.mtime, 
				info.version, 
				info.flags,
				CppSQLiteUtility::formatSqlStr(SD::Utility::FS::get_extension_name(info.name)).c_str(),
				info.modifiedId,
				CppSQLiteUtility::formatSqlStr(CacheMgr::getInstance(userContext_)->getUserName(path.ownerId(), info.modifiedId)).c_str(),
				CppSQLiteUtility::formatSqlStr(info.extraType).c_str());
			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			skipIdList_.push_back(info.id);
			if(Page_MyFile==pageType_)
			{
				CacheMgr::getInstance(userContext_)->flushMyShare(info);
			}
		}
		return RT_OK;
	}

	virtual int32_t saveName(const Path& path, const std::wstring& name)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s='%s' WHERE %s=%lld", 
				TABLE_CMD, 
				CMD_ROW_NAME, CppSQLiteUtility::formatSqlStr(name).c_str(),
				CMD_ROW_ID, path.id());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t saveParent(const Path& path, const Path& parent)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s=%lld", 
				TABLE_CMD, 
				CMD_ROW_PARENT, parent.id(), 
				CMD_ROW_ID, path.id());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			addCount(path.parent(), -1);
			addCount(parent.id(), 1);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t saveDelete(const Path& path)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s=%lld", 
				TABLE_CMD, 
				CMD_ROW_ID, path.id());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			addCount(path.parent(), -1);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;			
	}

	virtual int32_t saveCreate(const int64_t& teamId, const FILE_DIR_INFO& info)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::string mName;
			if(Page_TeamSpace==pageType_)
			{
				mName = CacheMgr::getInstance(userContext_)->getUserName(teamId, info.modifiedId);
			}
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,%d,'%s',%lld,%lld,%d,%d,'%s',%lld,'%s','%s')",
				TABLE_CMD, 
				CMD_ROW_ID, 
				CMD_ROW_PARENT, 
				CMD_ROW_TYPE, 
				CMD_ROW_NAME,
				CMD_ROW_SIZE,
				CMD_ROW_MTIME,
				CMD_ROW_VERSION,
				CMD_ROW_STATUS,
				CMD_ROW_TYPESTR,
				CMD_ROW_MID,
				CMD_ROW_MNAME,
				CMD_ROW_EXTRATYPE,
				info.id,
				info.parent,
				info.type,
				CppSQLiteUtility::formatSqlStr(info.name).c_str(),
				info.size, 
				info.mtime, 
				info.version, 
				info.flags,
				CppSQLiteUtility::formatSqlStr(SD::Utility::FS::get_extension_name(info.name)).c_str(),
				info.modifiedId,
				CppSQLiteUtility::formatSqlStr(mName).c_str(),
				CppSQLiteUtility::formatSqlStr(info.extraType).c_str());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			addCount(info.parent, 1);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

private:
	void createCMDTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_METADATA;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_CMD))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s VARCHAR,\
									%s INTEGER DEFAULT %d,\
									%s VARCHAR,\
									%s INTEGER DEFAULT %d,\
									%s VARCHAR);", 
									TABLE_CMD, 
									CMD_ROW_ID, 
									CMD_ROW_PARENT,
									CMD_ROW_TYPE,
									CMD_ROW_NAME,
									CMD_ROW_SIZE, 0L,
									CMD_ROW_VERSION, 1L,
									CMD_ROW_COUNT, -1L,
									CMD_ROW_STATUS, 0L,
									CMD_ROW_TYPESTR,
									CMD_ROW_MID, 0L,
									CMD_ROW_MNAME,
									CMD_ROW_MTIME, 0L,
									CMD_ROW_EXTRATYPE);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_CMD_idx1", TABLE_CMD, CMD_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"tb_CMD_idx2", TABLE_CMD, CMD_ROW_PARENT);
				(void)db_.execDML(bufSQLIdx2);

				createRoot();
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	void saveCacheAsync()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCacheAsync id:%I64d", lastParent_.id()));

		try
		{
			boost::this_thread::interruption_point();

			saveCache(lastParent_);

			if(-1!=nextParent_.id())
			{
				lastParent_ = nextParent_;
				nextParent_.id(-1);
				saveCacheAsync();
			}
			lastParent_.id(-1);
		}
		catch(boost::thread_interrupted&)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_CANCEL, "save cache async thread interrupted.");
		}
	}

	void saveCache(const Path& path)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCache id:%I64d", path.id()));

		LIST_FOLDER_RESULT result;

		int64_t count = 0;
		PageParam pageParam;
		pageParam.offset = 0;
		pageParam.limit = 1000;
		skipIdList_.clear();
		do
		{
			boost::this_thread::interruption_point();
			if(RT_OK!=userContext_->getSyncFileSystemMgr()->listPage(path, result, pageParam, count))
			{
				break;
			}
			pageParam.offset += 1000;
		}while(result.size()<count);
		
		saveResult(path, result);
	}

	void createRoot()
	{
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s) VALUES (%d,%d,%d)", 
				TABLE_CMD, 
				CMD_ROW_ID, 
				CMD_ROW_PARENT, 
				CMD_ROW_TYPE,
				0,
				-1,
				0);
			(void)db_.execDML(bufSQL);
			return;
		}
		CATCH_SQLITE_EXCEPTION;
	}

	bool isExist(LIST_FOLDER_RESULT& result, const FILE_DIR_INFO& old)
	{
		for(LIST_FOLDER_RESULT::const_iterator it = result.begin();
			it != result.end(); ++it)
		{
			if(old.id == it->id)
			{
				if(old.parent == it->parent
					&&old.name == it->name
					&&old.mtime == it->mtime
					&&old.size == it->size
					&&old.flags == it->flags
					&&old.version == it->version
					&&old.modifiedId == it->modifiedId)
				{
					result.erase(it);
				}
				return true;
			}
		}
		return false;
	}

	std::string getInStr(const std::list<int64_t>& idList)
	{
		if(idList.empty())
		{
			return "";
		}

		std::stringstream inStr;
		std::list<int64_t>::const_iterator it = idList.begin();

		inStr<<*it;
		++it;

		for(; it != idList.end(); ++it)
		{
			inStr<<",";
			inStr<<*it;
		}
		return inStr.str();
	}

	int32_t saveResult(const Path& path, LIST_FOLDER_RESULT& result)
	{
		try
		{
			int64_t count = result.size();
			LIST_FOLDER_RESULT oldResult;
			std::list<int64_t> lastDelete;
			if(!isFlush_)
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer queryBufSQL;
				(void)queryBufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %lld", 
									CMD_ROW_ID, CMD_ROW_PARENT, 
									CMD_ROW_TYPE, CMD_ROW_NAME,
									CMD_ROW_SIZE, CMD_ROW_MTIME,
									CMD_ROW_VERSION, CMD_ROW_STATUS,
									CMD_ROW_MID,
									CMD_ROW_EXTRATYPE,
									TABLE_CMD,
									CMD_ROW_PARENT, 								
									path.id());
				CppSQLite3Query querySet = db_.execQuery(queryBufSQL);
			
				while(!querySet.eof())
				{
					FILE_DIR_INFO fileInfo;
					fileInfo.id = querySet.getInt64Field(0);
					fileInfo.parent = querySet.getInt64Field(1);
					fileInfo.type = querySet.getIntField(2);
					fileInfo.name = SD::Utility::String::utf8_to_wstring(querySet.getStringField(3));
					fileInfo.size = querySet.getInt64Field(4);
					fileInfo.mtime = querySet.getInt64Field(5);
					fileInfo.version = querySet.getIntField(6);
					fileInfo.flags = querySet.getIntField(7);
					fileInfo.modifiedId = querySet.getInt64Field(8);
					fileInfo.extraType = SD::Utility::String::utf8_to_wstring(querySet.getStringField(9));
					oldResult.push_back(fileInfo);
					querySet.nextRow();
				}
				for(LIST_FOLDER_RESULT::const_iterator itO = oldResult.begin();
					itO != oldResult.end(); ++itO)
				{
					if(!isExist(result, *itO))
					{
						lastDelete.push_back(itO->id);
					}
				}
				
				for(std::list<int64_t>::iterator itS = skipIdList_.begin(); itS!= skipIdList_.end(); ++itS)
				{
					for(LIST_FOLDER_RESULT::const_iterator it = result.begin();
						it != result.end(); ++it)
					{
						if(*itS == it->id)
						{
							result.erase(it);
							break;
						}
					}
				}
				skipIdList_.clear();

				if(!lastDelete.empty())
				{
					CppSQLite3Buffer deleteBufSQL;
					std::string inStr = getInStr(lastDelete);
					const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
						TABLE_CMD, CMD_ROW_ID, inStr.c_str());
					int32_t iModify = db_.execDML(deleteBufSQL);
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				}
			}
			else
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer deleteBufSQL;
				const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s = %lld", 
					TABLE_CMD, CMD_ROW_PARENT, path.id());
				int32_t iModify = db_.execDML(deleteBufSQL);
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			if(!result.empty())
			{
				boost::mutex::scoped_lock lock(mutex_);
				std::auto_ptr<PrintObj> printObj = PrintObj::create();
				db_.beginTransaction();
				for(LIST_FOLDER_RESULT::const_iterator it = result.begin();
					it != result.end(); ++it)
				{
					CppSQLite3Buffer bufSQL;
					(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,%d,'%s',%lld,%lld,%d,%d,'%s',%lld,'%s','%s')", 
						TABLE_CMD, 
						CMD_ROW_ID, 
						CMD_ROW_PARENT, 
						CMD_ROW_TYPE, 
						CMD_ROW_NAME,
						CMD_ROW_SIZE,
						CMD_ROW_MTIME,
						CMD_ROW_VERSION,
						CMD_ROW_STATUS,
						CMD_ROW_TYPESTR,
						CMD_ROW_MID,
						CMD_ROW_MNAME,
						CMD_ROW_EXTRATYPE,
						it->id,
						it->parent,
						it->type,
						CppSQLiteUtility::formatSqlStr(it->name).c_str(),
						it->size, 
						it->mtime, 
						it->version, 
						it->flags,
						CppSQLiteUtility::formatSqlStr(SD::Utility::FS::get_extension_name(it->name)).c_str(),
						it->modifiedId,
						CppSQLiteUtility::formatSqlStr(CacheMgr::getInstance(userContext_)->getUserName(path.ownerId(), it->modifiedId)).c_str(),
						CppSQLiteUtility::formatSqlStr(it->extraType).c_str());
					(void)db_.execDML(bufSQL);

					printObj->addField<int64_t>(it->id);
					printObj->addField<int64_t>(it->parent);
					printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(it->name));
				}
				db_.commitTransaction();
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", result.size(), printObj->getMsg().c_str());
			}

			//notify UI
			if((!lastDelete.empty())||(!result.empty()))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. delete size:%d, replace size:%d", lastDelete.size(), result.size());
				{
					boost::mutex::scoped_lock lock(mutex_);
					updateCount(path.id(), count);
				}

				if(!isFlush_)
				{
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
						SD::Utility::String::type_to_string<std::wstring>(pageType_),
						SD::Utility::String::type_to_string<std::wstring>(path.ownerId()),
						SD::Utility::String::type_to_string<std::wstring>(path.id())));
				}
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t updateCount(int64_t id, int64_t count)
	{
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s=%lld", 
				TABLE_CMD, 
				CMD_ROW_COUNT, count, 
				CMD_ROW_ID, id);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t addCount(int64_t id, int32_t offset)
	{
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s+%d WHERE %s=%lld", 
				TABLE_CMD, 
				CMD_ROW_COUNT, CMD_ROW_COUNT, offset, 
				CMD_ROW_ID, id);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}
private:
	UserContext* userContext_;

	boost::mutex mutex_;
	CppSQLite3DB db_;
	boost::thread saveThread_;
	Path lastParent_;
	Path nextParent_;
	NotifyPageType pageType_;
	bool isFlush_;
	std::list<int64_t> skipIdList_;
};

CacheMetaData* CacheMetaData::create(UserContext* userContext, const std::wstring& parent, bool isMyFile)
{
	return static_cast<CacheMetaData*>(new CacheMetaDataImpl(userContext, parent, isMyFile));
}