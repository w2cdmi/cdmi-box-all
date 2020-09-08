#include "CacheMsgInfo.h"
#include "CppSQLite3.h"
#include "Utility.h"
#include "ErrorCode.h"
#include "CommonDefine.h"
#include "CacheCommon.h"
#include "MsgMgr.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("CacheMsg")
#endif

class CacheMsgInfoImpl : public CacheMsgInfo
{
public:
	CacheMsgInfoImpl(UserContext* userContext, const std::wstring& parent):userContext_(userContext)
	{
		createMsgTable(parent);
	}

	virtual ~CacheMsgInfoImpl()
	{
	}

	virtual int32_t getMsg(const PageParam& pageParam, const MsgTypeList& msgTypeList, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All)
	{
		try
		{
			std::string inStr = getTypeInStr(msgTypeList, status);

			std::stringstream orderStr;
			OrderParam orderParam;
			if(pageParam.orderList.empty())
			{
				orderParam.field = MSG_ROW_CREATEDAT;
				orderParam.direction = "DESC";
			}
			else
			{
				orderParam = pageParam.orderList.back();
			}
			orderStr << "ORDER BY " << orderParam.field << " " << orderParam.direction;

			boost::mutex::scoped_lock lock(mutex_);
			std::stringstream limitStr;
			if(pageParam.limit > 0)
			{
				limitStr << "LIMIT " << pageParam.offset << ", " << pageParam.limit;
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s \
								FROM %s %s %s %s", 
								MSG_ROW_ID, MSG_ROW_PROVIDERID,
								MSG_ROW_PUSERNAME, MSG_ROW_PNAME,
								MSG_ROW_RECEIVERID, MSG_ROW_APPID,
								MSG_ROW_TYPE, MSG_ROW_STATUS,
								MSG_ROW_CREATEDAT, MSG_ROW_EXPIREDAT,
								MSG_ROW_NODEID, MSG_ROW_NODENAME,
								MSG_ROW_NODETYPE, MSG_ROW_TEAMSPACEID,
								MSG_ROW_TEAMSPACENAME, MSG_ROW_GROUPID,
								MSG_ROW_GROUPNAME, MSG_ROW_ORIROLE,
								MSG_ROW_CURROLE, MSG_ROW_TITLE,
								MSG_ROW_CONTENT, MSG_ROW_ANNOUNCEMENTID,
								TABLE_MSG,
								inStr.c_str(),
								orderStr.str().c_str(),
								limitStr.str().c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				MsgNode msgNode;
				msgNode.id = qSet.getInt64Field(0);
				msgNode.providerId = qSet.getInt64Field(1);
				msgNode.providerUsername = qSet.getStringField(2);
				msgNode.providerName = qSet.getStringField(3);
				msgNode.receiverId = qSet.getInt64Field(4);
				msgNode.appId = qSet.getStringField(5);
				msgNode.type = (MsgType)qSet.getIntField(6);
				msgNode.status = (MsgStatus)qSet.getIntField(7);
				msgNode.createdAt = qSet.getInt64Field(8);
				msgNode.expiredAt = qSet.getInt64Field(9);

				MsgParams params;
				params.nodeId = qSet.getInt64Field(10);
				params.nodeName = qSet.getStringField(11);
				params.nodeType = qSet.getIntField(12);
				params.teamSpaceId = qSet.getInt64Field(13);
				params.teamSpaceName = qSet.getStringField(14);
				params.groupId = qSet.getInt64Field(15);
				params.groupName = qSet.getStringField(16);
				params.originalRole = qSet.getStringField(17);
				params.currentRole = qSet.getStringField(18);
				params.title = qSet.getStringField(19);
				params.content = qSet.getStringField(20);
				params.announcementId = qSet.getInt64Field(21);

				msgNode.params = params;
				msgNodes.push_back(msgNode);

				qSet.nextRow();
			}

			if(msgNodes.size()>=pageParam.limit)
			{
				CppSQLite3Buffer bufCnt;
				(void)bufCnt.format("SELECT COUNT(1) FROM %s %s", 
									TABLE_MSG,
									inStr.c_str());
				CppSQLite3Query qCnt = db_.execQuery(bufCnt);
				if(!qCnt.eof())
				{
					count = qCnt.getInt64Field(0);
				}
			}
			else
			{
				count = msgNodes.size();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getAllMsgKeyInfo(MsgKeyInfo& msgKeyInfo)
	{
		msgKeyInfo.clear();
		try
		{
			boost::mutex::scoped_lock lock(mutex_);

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s FROM %s", 
								MSG_ROW_ID, MSG_ROW_STATUS,
								TABLE_MSG);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				msgKeyInfo.insert(std::make_pair(qSet.getInt64Field(0), (MsgStatus)qSet.getIntField(1)));
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s=%lld", 
				TABLE_MSG, 
				MSG_ROW_STATUS, status, 
				MSG_ROW_ID, msgId);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteMsg(const int64_t msgId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s=%lld", 
				TABLE_MSG, 
				MSG_ROW_ID, msgId);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteMsg(const MsgKeyInfo& msgKeyInfo)
	{
		if(msgKeyInfo.empty())
		{
			return RT_OK;
		}
		std::stringstream inStr;
		MsgKeyInfo::const_iterator it = msgKeyInfo.begin();
		inStr << it->first;
		++it;
		for(; it != msgKeyInfo.end(); ++it)
		{
			inStr<<",";
			inStr<<it->first;
		}

		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s IN(%s)", 
				TABLE_MSG, 
				MSG_ROW_ID, inStr.str().c_str());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteAll()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s", TABLE_MSG);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t replaceMsg(const MsgList &msgNodes)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for(MsgList::const_iterator it = msgNodes.begin(); it != msgNodes.end(); ++it)
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) \
					VALUES (%lld,%lld,%lld,'%s',%d,%d,%lld,%lld,'%s','%s',%lld,'%s',%d,%lld,'%s',%lld,'%s','%s','%s','%s','%s',%lld)", 
					TABLE_MSG, 
					MSG_ROW_ID, MSG_ROW_PROVIDERID, 
					MSG_ROW_RECEIVERID, MSG_ROW_APPID,
					MSG_ROW_TYPE, MSG_ROW_STATUS,
					MSG_ROW_CREATEDAT, MSG_ROW_EXPIREDAT,
					MSG_ROW_PUSERNAME, MSG_ROW_PNAME,
					MSG_ROW_NODEID, MSG_ROW_NODENAME,
					MSG_ROW_NODETYPE, MSG_ROW_TEAMSPACEID,
					MSG_ROW_TEAMSPACENAME, MSG_ROW_GROUPID,
					MSG_ROW_GROUPNAME, MSG_ROW_ORIROLE,
					MSG_ROW_CURROLE, MSG_ROW_TITLE,
					MSG_ROW_CONTENT, MSG_ROW_ANNOUNCEMENTID,
					it->id, it->providerId,
					it->receiverId, 
					CppSQLiteUtility::formatSqlStr(it->appId).c_str(),
					it->type, it->status,
					it->createdAt, it->expiredAt,
					CppSQLiteUtility::formatSqlStr(it->providerUsername).c_str(), 
					CppSQLiteUtility::formatSqlStr(it->providerName).c_str(),
					it->params.nodeId, 
					CppSQLiteUtility::formatSqlStr(it->params.nodeName).c_str(),
					it->params.nodeType, it->params.teamSpaceId,
					CppSQLiteUtility::formatSqlStr(it->params.teamSpaceName).c_str(), 
					it->params.groupId,
					CppSQLiteUtility::formatSqlStr(it->params.groupName).c_str(), 
					CppSQLiteUtility::formatSqlStr(it->params.originalRole).c_str(),
					CppSQLiteUtility::formatSqlStr(it->params.currentRole).c_str(),
					CppSQLiteUtility::formatSqlStr(it->params.title).c_str(),
					CppSQLiteUtility::formatSqlStr(it->params.content).c_str(),
					it->params.announcementId);
				(void)db_.execDML(bufSQL);

				printObj->addField<int64_t>(it->id);
				printObj->addField<int64_t>(it->type);
				printObj->lastField<int64_t>(it->status);
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", msgNodes.size(), printObj->getMsg().c_str());
			
			std::stringstream stream;
			stream << "delete from " << TABLE_MSG << " WHERE " << MSG_ROW_TYPE << " = " << MT_System << " AND id not in (select id from " ;
			stream << TABLE_MSG << " WHERE " << MSG_ROW_TYPE << " = " << MT_System << " order by " << MSG_ROW_CREATEDAT << " desc limit 0,3)";

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("%s", stream.str().c_str());
			(void)db_.execDML(bufSQL);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList)
	{
		try
		{
			std::string inStr = getTypeInStr(msgTypeList, MS_UnRead);

			boost::mutex::scoped_lock lock(mutex_);

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT COUNT(1) FROM %s %s",
								TABLE_MSG,
								inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return qSet.getInt64Field(0);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return -1;
	}

private:
	void createMsgTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_MSG;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_MSG))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s INTEGER NOT NULL);",
									TABLE_MSG,
									MSG_ROW_ID,
									MSG_ROW_PROVIDERID,
									MSG_ROW_RECEIVERID,
									MSG_ROW_APPID,
									MSG_ROW_TYPE,
									MSG_ROW_STATUS,
									MSG_ROW_CREATEDAT,
									MSG_ROW_EXPIREDAT,
									MSG_ROW_PUSERNAME,
									MSG_ROW_PNAME,
									MSG_ROW_NODEID,
									MSG_ROW_NODENAME,
									MSG_ROW_NODETYPE,
									MSG_ROW_TEAMSPACEID,
									MSG_ROW_TEAMSPACENAME,
									MSG_ROW_GROUPID,
									MSG_ROW_GROUPNAME,
									MSG_ROW_ORIROLE,
									MSG_ROW_CURROLE,
									MSG_ROW_TITLE,
									MSG_ROW_CONTENT,
									MSG_ROW_ANNOUNCEMENTID);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_msg_idx1", TABLE_MSG, MSG_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);
			}	
		}
		CATCH_SQLITE_EXCEPTION;
	}

	std::string getTypeInStr(const MsgTypeList& msgTypeList, MsgStatus status)
	{
		if(msgTypeList.empty() && MS_All==status)
		{
			return "";
		}

		std::stringstream inStr;
		inStr << "WHERE ";

		if(!msgTypeList.empty())
		{
			MsgTypeList::const_iterator it = msgTypeList.begin();

			inStr << MSG_ROW_TYPE << " IN(" << *it;
			++it;

			for(; it != msgTypeList.end(); ++it)
			{
				inStr<<",";
				inStr<<*it;
			}
			inStr << ")";
			if(MS_All!=status)
			{
				inStr << " AND ";
			}
		}
		if(MS_All!=status)
		{
			inStr << MSG_ROW_STATUS << " = " << status;
		}

		return inStr.str();
	}
private:
	UserContext* userContext_;

	boost::mutex mutex_;
	CppSQLite3DB db_;
};

CacheMsgInfo* CacheMsgInfo::create(UserContext* userContext, const std::wstring& parent)
{
	return static_cast<CacheMsgInfo*>(new CacheMsgInfoImpl(userContext, parent));
}