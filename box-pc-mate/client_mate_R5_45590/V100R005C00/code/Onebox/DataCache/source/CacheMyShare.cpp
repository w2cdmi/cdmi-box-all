#include "CacheMyShare.h"
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
#define MODULE_NAME ("CacheMyShare")
#endif

#define MAX_PAGE_SIZE (1000)

class CacheMyShareImpl : public CacheMyShare
{
public:
	CacheMyShareImpl(UserContext* userContext, const std::wstring& parent):userContext_(userContext)
	{
		createMyShareTable(parent);
		isSaveing_ = false;
		isFlush_ = false;
	}

	virtual ~CacheMyShareImpl()
	{
		saveThread_.interrupt();
		saveThread_.join();
	}

	virtual int32_t listMyShare(const std::string& keyWord, MyShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("listPage keyWord:%s", keyWord.c_str()));
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream whereStr;
			if(!keyWord.empty())
			{
				whereStr << "WHERE " << MYSHARE_ROW_NAME << " LIKE '%" << keyWord << "%' ";
			}

			std::stringstream orderStr;
			orderStr << "ORDER BY " << MYSHARE_ROW_TYPE  << " ASC, ";

			for(ParamOrderList::const_iterator it = pageParam.orderList.begin();
				it != pageParam.orderList.end(); )
			{
				OrderParam orderParam = *it;
				++it;
				if(MYLINK_ROW_LINKCOUNT==orderParam.field) continue;
				if(MYLINK_ROW_NAME==orderParam.field || MYLINK_ROW_PATH==orderParam.field)
				{
					orderStr << orderParam.field << " collate PINYIN " << orderParam.direction;
				}
				else
				{
					orderStr << orderParam.field << " " << orderParam.direction;
				}
				if(it != pageParam.orderList.end())
				{
					orderStr << ", ";
				}
			}

			std::stringstream limitStr;
			if(pageParam.limit > 0)
			{
				limitStr << "LIMIT " << pageParam.offset << ", " << pageParam.limit;
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s %s %s %s",
								MYSHARE_ROW_ID,
								MYSHARE_ROW_PARENT,
								MYSHARE_ROW_NAME,
								MYSHARE_ROW_PATH,
								MYSHARE_ROW_TYPE,
								MYSHARE_ROW_SIZE,
								MYSHARE_ROW_EXTRATYPE,
								TABLE_MYSHARE,
								whereStr.str().c_str(),
								orderStr.str().c_str(),
								limitStr.str().c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				MyShareNode myShareInfo;
				myShareInfo.id = qSet.getInt64Field(0);
				myShareInfo.parent = qSet.getInt64Field(1);
				myShareInfo.name = qSet.getStringField(2);
				myShareInfo.path = qSet.getStringField(3);
				myShareInfo.type = qSet.getIntField(4);
				myShareInfo.size = qSet.getInt64Field(5);
				myShareInfo.extraType = qSet.getStringField(6);
				if (!myShareInfo.path.empty())
					result.push_back(myShareInfo);
				qSet.nextRow();
			}

			if(result.size()>=pageParam.limit)
			{
				CppSQLite3Buffer bufCnt;
				(void)bufCnt.format("SELECT COUNT(1) FROM %s %s", 
									TABLE_MYSHARE,
									whereStr.str().c_str());
				CppSQLite3Query qCnt = db_.execQuery(bufCnt);
				if(!qCnt.eof())
				{
					count = qCnt.getInt64Field(0);
				}
			}
			else
			{
				count = result.size();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t flushMyShareCache(bool isFlush)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("flushMyShareCache"));
		if(isFlush)
		{
			isFlush_ = true;
			saveShareAsync();
			return RT_OK;
		}

		if(isSaveing_)
		{
			return RT_OK;
		}
		isFlush_ = false;
		saveThread_ = boost::thread(boost::bind(&CacheMyShareImpl::saveShareAsync, this));

		return RT_OK;
	}

	virtual int32_t listMyLink(const std::string& keyWord, MyShareNodeList& result, const PageParam& pageParam, int64_t& count)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("listPage keyWord:%s", keyWord.c_str()));
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream whereStr;
			if(!keyWord.empty())
			{
				whereStr << "WHERE " << MYLINK_ROW_NAME << " LIKE '%" << keyWord << "%' ";
			}

			std::stringstream orderStr;
			orderStr << "ORDER BY " << MYLINK_ROW_TYPE  << " ASC, ";

			for(ParamOrderList::const_iterator it = pageParam.orderList.begin();
				it != pageParam.orderList.end(); )
			{
                OrderParam orderParam = *it;
				++it;
				if(MYLINK_ROW_NAME==orderParam.field || MYLINK_ROW_PATH==orderParam.field)
				{
					orderStr << orderParam.field << " collate PINYIN " << orderParam.direction;
				}
				else
				{
					orderStr << orderParam.field << " " << orderParam.direction;
				}
				if(it != pageParam.orderList.end())
				{
					orderStr << ", ";
				}
			}

			std::stringstream limitStr;
			if(pageParam.limit > 0)
			{
				limitStr << "LIMIT " << pageParam.offset << ", " << pageParam.limit;
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s %s %s %s", 
								MYLINK_ROW_ID,
								MYLINK_ROW_PARENT,
								MYLINK_ROW_NAME,
								MYLINK_ROW_PATH,
								MYLINK_ROW_TYPE,
								MYLINK_ROW_SIZE,
								MYLINK_ROW_LINKCOUNT,
								MYLINK_ROW_EXTRATYPE,
								TABLE_MYLINK,
								whereStr.str().c_str(),
								orderStr.str().c_str(),
								limitStr.str().c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				MyShareNode myShareInfo;
				myShareInfo.id = qSet.getInt64Field(0);
				myShareInfo.parent = qSet.getInt64Field(1);
				myShareInfo.name = qSet.getStringField(2);
				myShareInfo.path = qSet.getStringField(3);
				myShareInfo.type = qSet.getIntField(4);
				myShareInfo.size = qSet.getInt64Field(5);
				myShareInfo.linkCount = qSet.getInt64Field(6);
				myShareInfo.extraType = qSet.getStringField(7);

				result.push_back(myShareInfo);
				qSet.nextRow();
			}

			if(result.size()>=pageParam.limit)
			{
				CppSQLite3Buffer bufCnt;
				(void)bufCnt.format("SELECT COUNT(1) FROM %s %s", 
									TABLE_MYLINK,
									whereStr.str().c_str());
				CppSQLite3Query qCnt = db_.execQuery(bufCnt);
				if(!qCnt.eof())
				{
					count = qCnt.getInt64Field(0);
				}
			}
			else
			{
				count = result.size();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t flushMyLinkCache(bool isFlush)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("flushMyLinkCache"));
		if(isFlush)
		{
			isFlush_ = true;
			saveLinkAsync();
			return RT_OK;
		}

		if(isSaveing_)
		{
			return RT_OK;
		}
		isFlush_ = false;
		saveThread_ = boost::thread(boost::bind(&CacheMyShareImpl::saveLinkAsync, this));

		return RT_OK;
	}

	virtual int32_t flushMyShare(const FILE_DIR_INFO& info)
	{
		try
		{
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer deleteBufSQL;
				const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s = %lld", 
									TABLE_MYSHARE,
									MYSHARE_ROW_ID,
									info.id);
				int32_t iModify = db_.execDML(deleteBufSQL);
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			if((info.flags & OBJECT_FLAG_SHARED)||(info.flags & OBJECT_FLAG_SHARELINK))
			{
				MyShareNode myShareNode;
				myShareNode.id = info.id;
				myShareNode.type = info.type;
				myShareNode.size = info.size;
				myShareNode.name = SD::Utility::String::wstring_to_utf8(info.name);
				myShareNode.extraType = SD::Utility::String::wstring_to_string(info.extraType);
				std::list<PathNode> pathNodes;
				std::wstring path;
				userContext_->getShareResMgr()->getFilePathNodes(myShareNode.id, pathNodes);
				if(!pathNodes.empty())
				{
					for(std::list<PathNode>::iterator itPath = pathNodes.begin(); itPath != pathNodes.end(); ++itPath)
					{
						path += itPath->fileName + L"/" ;
					}
					myShareNode.path = SD::Utility::String::wstring_to_utf8(path);
					myShareNode.parent = pathNodes.rbegin()->fileId;
				}

				if(info.flags & OBJECT_FLAG_SHARED)
				{
					saveMyShare(myShareNode);
				}
				if(info.flags & OBJECT_FLAG_SHARELINK)
				{
					//此处无法获取外链个数
					//saveMyLink(myShareNode);
					flushMyLinkCache(false);
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteMyShareRes(const std::list<int64_t>& idList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer deleteBufSQL;
			std::string inStr = getInStr(idList);
			const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
				TABLE_MYSHARE, 
				MYSHARE_ROW_ID, inStr.c_str());
			int32_t iModify = db_.execDML(deleteBufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t deleteMyLinkRes(const std::list<int64_t>& idList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer deleteBufSQL;
			std::string inStr = getInStr(idList);
			const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
				TABLE_MYLINK, 
				MYLINK_ROW_ID, inStr.c_str());
			int32_t iModify = db_.execDML(deleteBufSQL);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual bool isMyShareLinkCacheExist()
	{
		if(!db_.tableExists(TABLE_MYLINK))
		{
			return false;
		}
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("SELECT COUNT(*) FROM %s", TABLE_MYLINK);
			CppSQLite3Query query = db_.execQuery(sqlStr);
			if(!query.eof())
			{
				int64_t count = query.getInt64Field(0);
				return count != 0;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

private:
	void createMyShareTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_MYSHARE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_MYSHARE))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL PRIMARY KEY,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR);", 
									TABLE_MYSHARE,
									MYSHARE_ROW_ID,
									MYSHARE_ROW_PARENT,
									MYSHARE_ROW_NAME,
									MYSHARE_ROW_PATH,
									MYSHARE_ROW_TYPE,
									MYSHARE_ROW_SIZE,
									MYSHARE_ROW_EXTRATYPE);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_MyShare_idx1", TABLE_MYSHARE, MYSHARE_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);
			}

			if(!db_.tableExists(TABLE_MYLINK))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL PRIMARY KEY,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR);", 
									TABLE_MYLINK,
									MYLINK_ROW_ID,
									MYLINK_ROW_PARENT,
									MYLINK_ROW_NAME,
									MYLINK_ROW_PATH,
									MYLINK_ROW_TYPE,
									MYLINK_ROW_SIZE,
									MYLINK_ROW_LINKCOUNT,
									MYLINK_ROW_EXTRATYPE);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_MyLink_idx1", TABLE_MYLINK, MYLINK_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	void saveShareAsync()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveShareAsync"));
		isSaveing_ = true;

		MyShareNodeList result;
		int64_t count = 0;
		PageParam pageParam;
		pageParam.offset = 0;
		pageParam.limit = MAX_PAGE_SIZE;

		do
		{
			userContext_->getShareResMgr()->listMyShareRes("", result, pageParam, count);
			pageParam.offset += MAX_PAGE_SIZE;
		}while(result.size()<count);

		for(MyShareNodeList::iterator it = result.begin(); it != result.end(); ++it)
		{
			std::list<PathNode> pathNodes;
			std::wstring path;
			userContext_->getShareResMgr()->getFilePathNodes(it->id, pathNodes);
			if(pathNodes.empty())
			{
				continue;
			}
			for(std::list<PathNode>::iterator itPath = pathNodes.begin(); itPath != pathNodes.end(); ++itPath)
			{
				path += itPath->fileName + L"/" ;
			}
			it->path = SD::Utility::String::wstring_to_utf8(path);
			it->parent = pathNodes.rbegin()->fileId;
		}
		saveShareResult(result);

		isSaveing_ = false;
	}

	void saveLinkAsync()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("saveLinkAsync"));
		isSaveing_ = true;

		MyShareNodeList result;
		MyShareNodeList temp;
		int64_t count = 0;
		PageParam pageParam;
		pageParam.offset = 0;
		pageParam.limit = MAX_PAGE_SIZE;
		int tempSize = 0;

		do
		{
			userContext_->getShareResMgr()->listMyLinkRes("", temp, pageParam, count);
			pageParam.offset += MAX_PAGE_SIZE;

			// 规避由于服务端无法列举计算机目录和磁盘目录外链, result.size始终小于count而导致的死循环问题
			tempSize = temp.size();
			if(!temp.empty())
			{
				result.splice(result.end(), temp);
			}
		}while(tempSize >= MAX_PAGE_SIZE);

		for(MyShareNodeList::iterator it = result.begin(); it != result.end(); ++it)
		{
			std::list<PathNode> pathNodes;
			std::wstring path;
			userContext_->getShareResMgr()->getFilePathNodes(it->id, pathNodes);
			if(pathNodes.empty())
			{
				continue;
			}
			for(std::list<PathNode>::iterator itPath = pathNodes.begin(); itPath != pathNodes.end(); ++itPath)
			{
				path += itPath->fileName + L"/" ;
			}
			it->path = SD::Utility::String::wstring_to_utf8(path);
			it->parent = pathNodes.rbegin()->fileId;
		}
		saveLinkResult(result);

		isSaveing_ = false;
	}

	std::string getInStr(const MyShareNodeList& nodes)
	{
		if(nodes.empty())
		{
			return "";
		}

		std::stringstream inStr;
		MyShareNodeList::const_iterator it = nodes.begin();

		inStr<<it->id;
		++it;

		for(; it != nodes.end(); ++it)
		{
			inStr<<",";
			inStr<<it->id;
		}
		return inStr.str();
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

	int32_t saveShareResult(MyShareNodeList& result)
	{
		try
		{
			CppSQLite3Buffer queryBufSQL;
			(void)queryBufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s", 
								MYSHARE_ROW_ID,
								MYSHARE_ROW_PARENT,
								MYSHARE_ROW_NAME,
								MYSHARE_ROW_PATH,
								MYSHARE_ROW_TYPE,
								MYSHARE_ROW_SIZE,
								MYSHARE_ROW_EXTRATYPE,
								TABLE_MYSHARE);
			CppSQLite3Query querySet = db_.execQuery(queryBufSQL);
			MyShareNodeList oldResult;
			while(!querySet.eof())
			{
				MyShareNode myShareInfo;
				myShareInfo.id = querySet.getInt64Field(0);
				myShareInfo.parent = querySet.getInt64Field(1);
				myShareInfo.name = querySet.getStringField(2);
				myShareInfo.path = querySet.getStringField(3);
				myShareInfo.type = querySet.getIntField(4);
				myShareInfo.size = querySet.getInt64Field(5);
				myShareInfo.extraType = querySet.getStringField(6);
				oldResult.push_back(myShareInfo);
				querySet.nextRow();
			}

			for(MyShareNodeList::iterator it = result.begin(); it != result.end();)
			{
				bool isErase = false;
				for(MyShareNodeList::iterator itO = oldResult.begin(); itO != oldResult.end(); ++itO)
				{
					if(it->id == itO->id && it->type == itO->type)
					{
						if(it->name == itO->name && it->parent == itO->parent && it->path == itO->path && it->size == itO->size)
						{
							it = result.erase(it);
							isErase = true;
							oldResult.erase(itO);
						}
						break;
					}
				}
				if(!isErase)
				{
					++it;
				}
			}

			if(!oldResult.empty())
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer deleteBufSQL;
				std::string inStr = getInStr(oldResult);
				const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
					TABLE_MYSHARE, MYSHARE_ROW_ID, inStr.c_str());
				int32_t iModify = db_.execDML(deleteBufSQL);
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			if(!result.empty())
			{
				boost::mutex::scoped_lock lock(mutex_);
				std::auto_ptr<PrintObj> printObj = PrintObj::create();
				db_.beginTransaction();
				for(MyShareNodeList::const_iterator it = result.begin();
					it != result.end(); ++it)
				{
					CppSQLite3Buffer bufSQL;
					(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,'%s','%s',%d,%lld,'%s')", 
						TABLE_MYSHARE, 
						MYSHARE_ROW_ID,
						MYSHARE_ROW_PARENT,
						MYSHARE_ROW_NAME,
						MYSHARE_ROW_PATH,
						MYSHARE_ROW_TYPE,
						MYSHARE_ROW_SIZE,
						MYSHARE_ROW_EXTRATYPE,
						it->id,
						it->parent,
						CppSQLiteUtility::formatSqlStr(it->name).c_str(),
						CppSQLiteUtility::formatSqlStr(it->path).c_str(),
						it->type,
						it->size,
						CppSQLiteUtility::formatSqlStr(it->extraType).c_str());
					(void)db_.execDML(bufSQL);

					printObj->addField<int64_t>(it->id);
					printObj->lastField<std::string>(it->name);
				}
				db_.commitTransaction();
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", result.size(), printObj->getMsg().c_str());
			}

			//notify UI
			if(!oldResult.empty()||!result.empty())
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. delete size:%d, replace size:%d", oldResult.size(), result.size());
				if(!isFlush_)
				{
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
						SD::Utility::String::type_to_string<std::wstring>(Page_MyShare)));
				}
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t saveLinkResult(MyShareNodeList& result)
	{
		try
		{
			CppSQLite3Buffer queryBufSQL;
			(void)queryBufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s", 
								MYLINK_ROW_ID,
								MYLINK_ROW_PARENT,
								MYLINK_ROW_NAME,
								MYLINK_ROW_PATH,
								MYLINK_ROW_TYPE,
								MYLINK_ROW_SIZE,
								MYLINK_ROW_LINKCOUNT,
								MYLINK_ROW_EXTRATYPE,
								TABLE_MYLINK);
			CppSQLite3Query querySet = db_.execQuery(queryBufSQL);
			MyShareNodeList oldResult;
			while(!querySet.eof())
			{
				MyShareNode myShareInfo;
				myShareInfo.id = querySet.getInt64Field(0);
				myShareInfo.parent = querySet.getInt64Field(1);
				myShareInfo.name = querySet.getStringField(2);
				myShareInfo.path = querySet.getStringField(3);
				myShareInfo.type = querySet.getIntField(4);
				myShareInfo.size = querySet.getInt64Field(5);
				myShareInfo.linkCount = querySet.getInt64Field(6);
				myShareInfo.extraType = querySet.getStringField(7);

				oldResult.push_back(myShareInfo);
				querySet.nextRow();
			}

			for(MyShareNodeList::iterator it = result.begin(); it != result.end();)
			{
				bool isErase = false;
				for(MyShareNodeList::iterator itO = oldResult.begin(); itO != oldResult.end(); ++itO)
				{
					if(it->id == itO->id && it->type == itO->type)
					{
						if(it->name == itO->name && it->parent == itO->parent 
							&& it->path == itO->path && it->linkCount == itO->linkCount)
						{
							it = result.erase(it);
							isErase = true;
							oldResult.erase(itO);
						}
						break;
					}
				}
				if(!isErase)
				{
					++it;
				}
			}

			if(!oldResult.empty())
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer deleteBufSQL;
				std::string inStr = getInStr(oldResult);
				const char* sqlStr = deleteBufSQL.format("DELETE FROM %s WHERE %s IN(%s)", 
					TABLE_MYLINK, MYLINK_ROW_ID, inStr.c_str());
				int32_t iModify = db_.execDML(deleteBufSQL);
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			if(!result.empty())
			{
				boost::mutex::scoped_lock lock(mutex_);
				std::auto_ptr<PrintObj> printObj = PrintObj::create();
				db_.beginTransaction();
				for(MyShareNodeList::const_iterator it = result.begin();
					it != result.end(); ++it)
				{
					CppSQLite3Buffer bufSQL;
					(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,'%s','%s',%d,%lld,%lld,'%s')", 
						TABLE_MYLINK, 
						MYLINK_ROW_ID,
						MYLINK_ROW_PARENT,
						MYLINK_ROW_NAME,
						MYLINK_ROW_PATH,
						MYLINK_ROW_TYPE,
						MYLINK_ROW_SIZE,
						MYLINK_ROW_LINKCOUNT,
						MYLINK_ROW_EXTRATYPE,
						it->id,
						it->parent,
						CppSQLiteUtility::formatSqlStr(it->name).c_str(),
						CppSQLiteUtility::formatSqlStr(it->path).c_str(),
						it->type, 
						it->size,
						it->linkCount,
						CppSQLiteUtility::formatSqlStr(it->extraType).c_str());
					(void)db_.execDML(bufSQL);

					printObj->addField<int64_t>(it->id);
					printObj->lastField<std::string>(it->name);
				}
				db_.commitTransaction();
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", result.size(), printObj->getMsg().c_str());
			}

			//notify UI
			if(!oldResult.empty()||!result.empty())
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. delete size:%d, replace size:%d", oldResult.size(), result.size());
				if(!isFlush_)
				{
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
						SD::Utility::String::type_to_string<std::wstring>(Page_MyShare)));
				}
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	int32_t saveMyShare(MyShareNode& node)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,'%s','%s',%d,%lld,'%s')", 
				TABLE_MYSHARE, 
				MYSHARE_ROW_ID,
				MYSHARE_ROW_PARENT,
				MYSHARE_ROW_NAME,
				MYSHARE_ROW_PATH,
				MYSHARE_ROW_TYPE,
				MYSHARE_ROW_SIZE,
				MYSHARE_ROW_EXTRATYPE,
				node.id,
				node.parent,
				CppSQLiteUtility::formatSqlStr(node.name).c_str(),
				CppSQLiteUtility::formatSqlStr(node.path).c_str(),
				node.type, 
				node.size,
				CppSQLiteUtility::formatSqlStr(node.extraType).c_str());
			int32_t iModify = db_.execDML(bufSQL);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t saveMyLink(MyShareNode& node)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufSQL;
			const char* sqlStr = bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s) VALUES (%lld,%lld,'%s','%s',%d,%lld,%lld,'%s')", 
				TABLE_MYLINK, 
				MYLINK_ROW_ID,
				MYLINK_ROW_PARENT,
				MYLINK_ROW_NAME,
				MYLINK_ROW_PATH,
				MYLINK_ROW_TYPE,
				MYLINK_ROW_SIZE,
				MYLINK_ROW_LINKCOUNT,
				MYLINK_ROW_EXTRATYPE,
				node.id,
				node.parent,
				CppSQLiteUtility::formatSqlStr(node.name).c_str(),
				CppSQLiteUtility::formatSqlStr(node.path).c_str(),
				node.type,
				node.size,
				node.linkCount,
				node.extraType);
			int32_t iModify = db_.execDML(bufSQL);

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
	bool isSaveing_;
	bool isFlush_;
};

CacheMyShare* CacheMyShare::create(UserContext* userContext, const std::wstring& parent)
{
	return static_cast<CacheMyShare*>(new CacheMyShareImpl(userContext, parent));
}