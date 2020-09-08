#include "CacheReceiveShare.h"
#include "CppSQLite3.h"
#include "Utility.h"
#include "ErrorCode.h"
#include "CommonDefine.h"
#include "CacheCommon.h"
#include "NotifyMgr.h"
#include <boost/thread.hpp>
#include "UserContextMgr.h"
#include "PathMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("CacheReceiveShare")
#endif

class CacheReceiveShareImpl : public CacheReceiveShare
{
public:
	CacheReceiveShareImpl(UserContext* userContext, const std::wstring& parent):userContext_(userContext)
	{
		createS2MTable(parent);
		lastParent_.id = -1;
		nextParent_.id = -1;
		isFlush_ = false;
	}

	virtual ~CacheReceiveShareImpl()
	{
		saveThread_.interrupt();
		saveThread_.join();
	}

	virtual int32_t listPage(const ShareNodeParent& parent, ShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("listPage id:%I64d, ownerId:%I64d", parent.id, parent.ownerId));
		if(INVALID_ID == parent.id || INVALID_ID == parent.ownerId)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream whereStr;
			if(parent.id!=0)
			{
				whereStr << "AND " << S2M_ROW_OWNERID << "=" << parent.ownerId << " ";
			}
			CppSQLite3Buffer bufCount;
			(void)bufCount.format("SELECT %s FROM %s WHERE %s = %lld %s LIMIT 0, 1", 
								S2M_ROW_COUNT,
								TABLE_S2M,
								S2M_ROW_ID, parent.id,
								whereStr.str().c_str());
			CppSQLite3Query qCount = db_.execQuery(bufCount);
			if(!qCount.eof())
			{
				count = qCount.getInt64Field(0);
			}

			if(0==parent.id && !parent.keyWord.empty())
			{
				std::string strKey = CppSQLiteUtility::formaSqlLikeStr(parent.keyWord);
				whereStr << "AND (" << S2M_ROW_NAME << " LIKE '%" << strKey << "%' OR "
					<< S2M_ROW_OWNERNAME << " LIKE '%" <<strKey << "%') ";
			}

			std::stringstream orderStr;
			OrderParam orderParam;
			if(pageParam.orderList.empty())
			{
				orderParam.field = S2M_ROW_STIME;
				orderParam.direction = "DESC";
			}
			else
			{
				orderParam = pageParam.orderList.back();
			}
			orderStr << "ORDER BY ";
			orderStr << S2M_ROW_TYPE << " ASC" << ", ";
			if(S2M_ROW_NAME==orderParam.field)
			{
				orderStr << S2M_ROW_NAME << " collate PINYIN " << orderParam.direction;
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
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %lld %s %s %s", 
								S2M_ROW_ID, 
								S2M_ROW_TYPE, S2M_ROW_NAME,
								S2M_ROW_SIZE, S2M_ROW_STIME,
								S2M_ROW_OWNERID, S2M_ROW_OWNERNAME, S2M_ROW_EXTRATYPE, 
								TABLE_S2M,
								S2M_ROW_PARENT, 
								parent.id,
								whereStr.str().c_str(),
								orderStr.str().c_str(),
								limitStr.str().c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				ShareNode shareInfo;
				shareInfo.id(qSet.getInt64Field(0));
				shareInfo.type(qSet.getIntField(1));
				shareInfo.name(CppSQLiteUtility::unformaSqlLikeStr(qSet.getStringField(2)));
				shareInfo.size(qSet.getInt64Field(3));
				shareInfo.modifiedAt(qSet.getInt64Field(4));
				shareInfo.ownerId(qSet.getInt64Field(5));
				shareInfo.ownerName(CppSQLiteUtility::unformaSqlLikeStr(qSet.getStringField(6)));
				shareInfo.extraType(CppSQLiteUtility::unformaSqlLikeStr(qSet.getStringField(7)));
				result.push_back(shareInfo);
				qSet.nextRow();
			}

			if(!result.empty()&&count<result.size())
			{
				CppSQLite3Buffer bufCnt;
				std::stringstream tempStr;
				if(parent.id!=0)
				{
					tempStr << "AND " << S2M_ROW_OWNERID << "=" << parent.ownerId << " ";
				}
				(void)bufCnt.format("SELECT COUNT(1) FROM %s WHERE %s = %lld %s LIMIT 0, 1", 
									TABLE_S2M,
									S2M_ROW_PARENT, parent.id,
									tempStr.str().c_str());
				CppSQLite3Query qCnt = db_.execQuery(bufCnt);
				if(!qCnt.eof())
				{
					count = qCnt.getInt64Field(0);
				}
				updateCount(parent.ownerId, parent.id, count);
				SERVICE_ERROR(MODULE_NAME, RT_OK, "listPage count error. id:%I64d, cnt:%I64d", parent.id, count);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getShareFileId(int64_t& ownerId, const int64_t& dirId, std::set<int64_t>& result)
	{
		if(INVALID_ID == dirId)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream tempStr;
			if(dirId!=0)
			{
				tempStr << "AND " << S2M_ROW_OWNERID << "=" << ownerId << " ";
			}
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s = %lld %s", 
								S2M_ROW_ID,
								TABLE_S2M,
								S2M_ROW_PARENT, dirId,
								tempStr.str().c_str());
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

	virtual int32_t flushShareCache(const ShareNodeParent& parent, bool isFlush)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCache id:%I64d", parent.id));
		isFlush_ = isFlush;
		if(isFlush)
		{
			saveCache(parent);
			return RT_OK;
		}

		if(lastParent_.id==parent.id)
		{
			return RT_OK;
		}

		if(lastParent_.id!=-1&&lastParent_.id!=parent.id)
		{
			nextParent_ = parent;
			SERVICE_INFO(MODULE_NAME, RT_OK, "wait last saveCache. last parentId:%I64d", lastParent_.id);
			return RT_OK;
		}
		lastParent_ = parent;
		saveThread_ = boost::thread(boost::bind(&CacheReceiveShareImpl::saveCacheAsync, this));

		return RT_OK;
	}

private:
	void createS2MTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_SHARE2ME;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_S2M))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d,\
									%s VARCHAR, \
									%s VARCHAR);", 
									TABLE_S2M,
									S2M_ROW_SHAREID,
									S2M_ROW_ID, 
									S2M_ROW_PARENT,
									S2M_ROW_TYPE,
									S2M_ROW_NAME,
									S2M_ROW_SIZE, 0L,
									S2M_ROW_STIME, 0L,
									S2M_ROW_COUNT, -1L,
									S2M_ROW_OWNERID, -1L,
									S2M_ROW_OWNERNAME,
									S2M_ROW_EXTRATYPE);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_S2M_idx1", TABLE_S2M, S2M_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"tb_S2M_idx2", TABLE_S2M, S2M_ROW_PARENT);
				(void)db_.execDML(bufSQLIdx2);

				CppSQLite3Buffer bufSQLIdx3;
				(void)bufSQLIdx3.format("CREATE INDEX %s ON %s(%s);", 
					"tb_S2M_idx3", TABLE_S2M, S2M_ROW_OWNERID);
				(void)db_.execDML(bufSQLIdx3);

				createRoot();
			}	
		}
		CATCH_SQLITE_EXCEPTION;
	}

	void saveCacheAsync()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCacheAsync id:%I64d", lastParent_.id));

		saveCache(lastParent_);

		if(-1!=nextParent_.id)
		{
			lastParent_ = nextParent_;
			nextParent_.id = -1;
			saveCacheAsync();
		}
		lastParent_.id = -1;
	}

	void saveCache(const ShareNodeParent& parent)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveCacheAsync id:%I64d", parent.id));

		ShareNodeList result;
		int64_t count = 0;
		PageParam pageParam;
		pageParam.offset = 0;
		pageParam.limit = 1000;
		if(0!=parent.id)
		{
			LIST_FOLDER_RESULT tempResult;
			UserContext* shareContext = UserContextMgr::getInstance()->createUserContext(userContext_, 
				parent.ownerId, UserContext_ShareUser, SD::Utility::String::utf8_to_wstring(parent.ownerName));
			if(NULL==shareContext) return;
			Path path = shareContext->getPathMgr()->makePath();
			path.id(parent.id);
			do
			{
				if(RT_OK!=userContext_->getSyncFileSystemMgr()->listPage(path, tempResult, pageParam, count))
				{
					break;
				}
				pageParam.offset += pageParam.limit;
			}while(tempResult.size()<count);
			convert(tempResult, parent, result);
		}
		else
		{
			do
			{
				userContext_->getShareResMgr()->listReceiveShareRes("", result, pageParam, count);
				pageParam.offset += pageParam.limit;
			}while(result.size()<count);
		}
		
		saveResult(parent, result);
	}

	void convert(const LIST_FOLDER_RESULT& lfResult, const ShareNodeParent& parent, ShareNodeList& shareNodes)
	{
		for(LIST_FOLDER_RESULT::const_iterator it = lfResult.begin();
			it != lfResult.end(); ++it)
		{
			ShareNode shareNode;
			shareNode.id(it->id);
			shareNode.type(it->type);
			shareNode.name(SD::Utility::String::wstring_to_utf8(it->name));
			shareNode.size(it->size);
			shareNode.modifiedAt(parent.shareTime);
			shareNode.ownerId(parent.ownerId);
			shareNode.ownerName(parent.ownerName);
			shareNode.extraType(SD::Utility::String::wstring_to_utf8(it->extraType));
			shareNodes.push_back(shareNode);
		}
	}

	void createRoot()
	{
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s) VALUES (%d,%d,%d)", 
				TABLE_S2M, 
				S2M_ROW_ID, 
				S2M_ROW_PARENT, 
				S2M_ROW_TYPE,
				0,
				-1,
				0);
			(void)db_.execDML(bufSQL);
			return;
		}
		CATCH_SQLITE_EXCEPTION;
	}

	int32_t saveResult(const ShareNodeParent& parent, ShareNodeList& result)
	{
		try
		{
			int64_t count = result.size();
			CppSQLite3Buffer queryBufSQL;
			(void)queryBufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %lld AND (0 = %lld OR %s = %lld)", 
								S2M_ROW_ID, S2M_ROW_NAME,
								S2M_ROW_SIZE, S2M_ROW_STIME,
								S2M_ROW_OWNERID, S2M_ROW_SHAREID, S2M_ROW_EXTRATYPE,
								TABLE_S2M,
								S2M_ROW_PARENT, parent.id,
								parent.id,
								S2M_ROW_OWNERID, parent.ownerId);
			CppSQLite3Query querySet = db_.execQuery(queryBufSQL);
			ShareNodeList oldResult;
			while(!querySet.eof())
			{
				ShareNode shareInfo;
				shareInfo.id(querySet.getInt64Field(0));
				shareInfo.name(CppSQLiteUtility::unformaSqlLikeStr(querySet.getStringField(1)));
				shareInfo.size(querySet.getInt64Field(2));
				shareInfo.modifiedAt(querySet.getInt64Field(3));
				shareInfo.ownerId(querySet.getInt64Field(4));
				shareInfo.inodeId(querySet.getInt64Field(5));
				shareInfo.extraType(CppSQLiteUtility::unformaSqlLikeStr(querySet.getStringField(6)));
				oldResult.push_back(shareInfo);
				querySet.nextRow();
			}

			bool hasChange = false;
			if(result.size()!=oldResult.size())
			{
				hasChange = true;
			}
			else
			{
				for(ShareNodeList::const_iterator itO = oldResult.begin();
					itO != oldResult.end(); ++itO)
				{
					bool isFound = false;
					for(ShareNodeList::const_iterator it = result.begin();
						it != result.end(); ++it)
					{
						if(itO->id() == it->id() && itO->ownerId() == it->ownerId())
						{
							isFound = true;
							if(itO->name() == it->name()
								&& itO->size() == it->size())
							{
								break;
							}
							else
							{
								hasChange = true;
								break;
							}
						}
					}
					if(!isFound)
					{
						hasChange = true;
					}
					if(hasChange)
					{
						break;
					}
				}
			}

			if(hasChange)
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer deleteBufSQL;
				const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s = %lld AND (0 = %lld OR %s = %lld)",
					TABLE_S2M, 
					S2M_ROW_PARENT, parent.id,
					parent.id,
					S2M_ROW_OWNERID, parent.ownerId);
				int32_t iModify = db_.execDML(deleteBufSQL);
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

				std::auto_ptr<PrintObj> printObj = PrintObj::create();
				db_.beginTransaction();
				for(ShareNodeList::const_iterator it = result.begin(); it != result.end(); ++it)
				{
					CppSQLite3Buffer bufSQL;
					(void)bufSQL.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,%d,'%s',%lld,%lld,%lld,'%s','%s')", 
						TABLE_S2M, 
						S2M_ROW_ID, 
						S2M_ROW_PARENT, 
						S2M_ROW_TYPE, 
						S2M_ROW_NAME,
						S2M_ROW_SIZE,
						S2M_ROW_STIME,
						S2M_ROW_OWNERID,
						S2M_ROW_OWNERNAME,
						S2M_ROW_EXTRATYPE,
						it->id(),
						parent.id,
						it->type(),
						CppSQLiteUtility::formaSqlLikeStr(it->name()).c_str(),
						it->size(), 
						it->modifiedAt(), 
						it->ownerId(), 
						CppSQLiteUtility::formatSqlStr(it->ownerName()).c_str(),
						CppSQLiteUtility::formatSqlStr(it->extraType()).c_str());
					(void)db_.execDML(bufSQL);

					printObj->addField<int64_t>(it->id());
					printObj->addField<int64_t>(it->ownerId());
					printObj->lastField<std::string>(it->name());
				}
				db_.commitTransaction();
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", result.size(), printObj->getMsg().c_str());

				updateCount(parent.ownerId, parent.id, count);

				if(!isFlush_)
				{
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
						SD::Utility::String::type_to_string<std::wstring>(Page_Share2Me),
						SD::Utility::String::type_to_string<std::wstring>(parent.ownerId),
						SD::Utility::String::type_to_string<std::wstring>(parent.id)));
				}
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t updateCount(int64_t ownerId, int64_t id, int64_t count)
	{
		try
		{
			std::stringstream tempStr;
			if(id!=0)
			{
				tempStr << "AND " << S2M_ROW_OWNERID << "=" << ownerId << " ";
			}
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld WHERE %s=%lld %s", 
				TABLE_S2M, 
				S2M_ROW_COUNT, count, 
				S2M_ROW_ID, id,
				tempStr.str().c_str());
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
	ShareNodeParent lastParent_;
	ShareNodeParent nextParent_;
	bool isFlush_;
};

CacheReceiveShare* CacheReceiveShare::create(UserContext* userContext, const std::wstring& parent)
{
	return static_cast<CacheReceiveShare*>(new CacheReceiveShareImpl(userContext, parent));
}