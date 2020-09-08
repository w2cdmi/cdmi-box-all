#include "DiffTable.h"
#include "SyncUtility.h"
#include <boost/thread.hpp>
#include "NotifyMgr.h"
#include "ConfigureMgr.h"
#include "OverlayIconMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("DiffTable")
#endif

class DiffTableImpl : public DiffTable
{
public:
	DiffTableImpl(UserContext* userContext, const std::wstring& parent)
		:userContext_(userContext)
	{
		createDiffTable(parent);
		needRefresh_ = true;
		rootLen_ = userContext_->getConfigureMgr()->getConfigure()->monitorRootPath().length();
		lastNormalCnt_ = getNormalDiffCount();
		lastErrorCnt_ = getErrorDiffCount();
	}

	virtual ~DiffTableImpl(void)
	{
	}

	virtual int32_t getDiffCnt(void)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			int32_t cnt = 0;

			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT COUNT(1) FROM %s", DIFFPATH_TABLE_NAME);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			if(!q1.eof())
			{
				cnt = q1.getIntField(0);
			}
			return cnt;
		}
		CATCH_SQLITE_EXCEPTION;
		return -1;
	}

	virtual int32_t getCntForTimer(int64_t& maxDiffId, int32_t& curdiffCnt)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			if(INVALID_ID == maxDiffId)
			{
				CppSQLite3Buffer sql3;
				(void)sql3.format("SELECT MAX(%s) FROM %s", 
									DIFF_ROW_DIFFID,
									DIFF_TABLE_NAME);
				CppSQLite3Query q3 = db_.execQuery(sql3);

				if(!q3.eof())
				{
					maxDiffId = q3.getInt64Field(0);
					if(0==maxDiffId)
					{
						maxDiffId = INVALID_ID;
						curdiffCnt = 0;
						return RT_OK;
					}
				}
			}

			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT COUNT(DISTINCT %s) FROM %s WHERE %s = %d AND %s <= %lld", 
				DIFF_ROW_KEY,
				DIFF_TABLE_NAME,
				DIFF_ROW_STATUS, (int)Diff_Normal,
				DIFF_ROW_DIFFID, maxDiffId);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			if(!q1.eof())
			{
				curdiffCnt = q1.getIntField(0);
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getSizeInfo(int64_t& uploadSize, int64_t& downloadSize)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s FROM %s WHERE %s > 0", 
				DIFFPATH_ROW_TYPE, DIFFPATH_ROW_SIZE,
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_SIZE);
			CppSQLite3Query q1 = db_.execQuery(sql1);
			while(!q1.eof())
			{
				if(Key_LocalID == q1.getIntField(0))
				{
					uploadSize += q1.getInt64Field(1);
				}
				else if(Key_RemoteID == q1.getIntField(0))
				{
					downloadSize += q1.getInt64Field(1);
				}
				q1.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual bool isInDiff(const std::wstring& path)
	{
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql2;
			(void)sql2.format("SELECT 1 FROM %s WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/' LIMIT 0,1", 
					DIFFPATH_TABLE_NAME, 
					DIFFPATH_ROW_PATH,
					CppSQLiteUtility::formaSqlLikeStr(path).c_str());
			CppSQLite3Query q2 = db_.execQuery(sql2);

			if(!q2.eof())
			{
				return true;
			}			
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return true;
	}

	virtual bool isInDiff(KeyType keyType, const int64_t& key)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT 1 FROM %s WHERE %s = %lld AND %s = %d LIMIT 0,1", 
								DIFF_TABLE_NAME,
								DIFF_ROW_KEY, key,
								DIFF_ROW_KEYTYPE, keyType);
			CppSQLite3Query q = db_.execQuery(sql);

			if(!q.eof())
			{				
				return true;
			}
			return false;
		}
		CATCH_SQLITE_EXCEPTION;
		return true;	
	}

	virtual int64_t getMaxInc()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql2;
			(void)sql2.format("SELECT MAX(%s) FROM %s", 
								DIFF_ROW_DIFFID,
								DIFF_TABLE_NAME);
			CppSQLite3Query q2 = db_.execQuery(sql2);

			if(!q2.eof())
			{
				return q2.getInt64Field(0);
			}
			return INVALID_VALUE;
		}
		CATCH_SQLITE_EXCEPTION;
		return INVALID_VALUE;	
	}

	virtual int32_t getRelationInfo(RelationInfo& fileRelationInfo, RelationInfo& dirRelationInfo)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT a.%s, b.%s FROM %s AS a, %s AS b WHERE a.%s = %d AND a.%s>=0 AND b.%s = %d AND b.%s>=0 AND a.%s = b.%s", 
				DIFFPATH_ROW_ID, DIFFPATH_ROW_ID,
				DIFFPATH_TABLE_NAME, DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_TYPE, (int)Key_RemoteID, DIFFPATH_ROW_SIZE,
				DIFFPATH_ROW_TYPE, (int)Key_LocalID, DIFFPATH_ROW_SIZE,
				DIFFPATH_ROW_PATH, DIFFPATH_ROW_PATH);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			while(!q1.eof())
			{
				fileRelationInfo.insert(std::make_pair(q1.getInt64Field(0), q1.getInt64Field(1)));
				q1.nextRow();
			}

			CppSQLite3Buffer sql2;
			(void)sql2.format("SELECT a.%s, b.%s FROM %s AS a, %s AS b WHERE a.%s = %d AND a.%s<0 AND b.%s = %d AND b.%s<0 AND a.%s = b.%s", 
				DIFFPATH_ROW_ID, DIFFPATH_ROW_ID,
				DIFFPATH_TABLE_NAME, DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_TYPE, (int)Key_RemoteID, DIFFPATH_ROW_SIZE,
				DIFFPATH_ROW_TYPE, (int)Key_LocalID, DIFFPATH_ROW_SIZE,
				DIFFPATH_ROW_PATH, DIFFPATH_ROW_PATH);
			CppSQLite3Query q2 = db_.execQuery(sql2);

			while(!q2.eof())
			{
				dirRelationInfo.insert(std::make_pair(q2.getInt64Field(0), q2.getInt64Field(1)));
				q2.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getErrorNodes(ErrorNodes& errorNodes, const int32_t offset, const int32_t limit)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "getErrorNodes");
		if((lastNormalCnt_>0)||(0==lastErrorCnt_))
		{
			return RT_OK;
		}
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s, %s FROM %s WHERE %s NOT IN (%d, %d) limit %d,%d",
				DIFFPATH_ROW_PATH, DIFFPATH_ROW_ERRORCODE, 
				DIFFPATH_ROW_REMOTEPATH, 
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_ERRORCODE, RT_OK, RT_PARENT_NOEXIST_ERROR,
				offset, offset+limit);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			while(!q1.eof())
			{
				ErrorNode errorNode(new st_ErrorNode);
				errorNode->path = q1.getStringField(2);
				if(errorNode->path.empty())
				{
					errorNode->path = q1.getStringField(0);
				}
				errorNode->errorCode = q1.getIntField(1);
				errorNodes.push_back(errorNode);
				q1.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t exportErrorNodes(ErrorNodes& errorNodes)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "exportErrorNodes");
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s, %s FROM %s WHERE %s <> %d",
				DIFFPATH_ROW_PATH, DIFFPATH_ROW_ERRORCODE, 
				DIFFPATH_ROW_REMOTEPATH, 
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_ERRORCODE, RT_OK);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			while(!q1.eof())
			{
				ErrorNode errorNode(new st_ErrorNode);
				errorNode->path = q1.getStringField(2);
				if(errorNode->path.empty())
				{
					errorNode->path = q1.getStringField(0);
				}
				errorNode->errorCode = q1.getIntField(1);
				errorNodes.push_back(errorNode);
				q1.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t addOper(const OperNode& operNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;

			const char* sqlStr = sql.format("INSERT INTO %s(%s, %s, %s, %s, %s) VALUES(%lld, %d, %d, %lld, %lld)",
				DIFF_TABLE_NAME, DIFF_ROW_KEY, DIFF_ROW_KEYTYPE, DIFF_ROW_OPER, DIFF_ROW_SIZE, DIFF_ROW_PRIORITY,
				operNode->key, operNode->keyType, operNode->oper, operNode->size, operNode->priority);
			int32_t iModify = db_.execDML(sql);
			needRefresh_ = true;
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addOperList(OperNodes& operList)
	{
		if (operList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			for(OperNodes::const_iterator it = operList.begin();
				it != operList.end(); ++it)
			{
				OperNode operNode = *it;
				(void)sql.format("INSERT INTO %s(%s, %s, %s, %s, %s) VALUES(%lld, %d, %d, %lld, %lld)", 
									DIFF_TABLE_NAME, DIFF_ROW_KEY, 
									DIFF_ROW_KEYTYPE, DIFF_ROW_OPER, 
									DIFF_ROW_SIZE, DIFF_ROW_PRIORITY,
									operNode->key, operNode->keyType, 
									operNode->oper, operNode->size,
									operNode->priority);
				(void)db_.execDML(sql);

				printObj->addField<int64_t>(operNode->key);
				printObj->lastField<int32_t>(operNode->oper);
			}
			db_.commitTransaction();
			needRefresh_ = true;
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "addOperList. operList size:%d, [%s]", operList.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getTopOper(OperNode& operNode, bool isTaskIdle)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream whereStr;
			whereStr << DIFF_ROW_STATUS << " IN (" << Diff_Normal << ", " << Diff_Failed << ", " << Diff_Hidden << " ) ";
			if(!isTaskIdle)
			{
				whereStr << "AND " << DIFFPATH_ROW_SIZE << " < 0";
			}

			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s, %s, %s FROM %s WHERE %s ORDER BY %s, %s LIMIT 0,1",
				DIFF_ROW_KEY, DIFF_ROW_KEYTYPE, 
				DIFF_ROW_PRIORITY, DIFF_ROW_STATUS,
				DIFF_TABLE_NAME,
				whereStr.str().c_str(), 
				DIFF_ROW_PRIORITY, DIFF_ROW_KEYTYPE);
			CppSQLite3Query q = db_.execQuery(sql);

			if(q.eof())
			{				
				return RT_SQLITE_NOEXIST;
			}

			operNode->key = q.getInt64Field(0);
			operNode->keyType = KeyType(q.getIntField(1));
			operNode->priority = q.getInt64Field(2);
			operNode->status = DiffStatus(q.getIntField(3));
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "getTopOper key:%I64d, keyType:%d, status:%d", 
				operNode->key, (int)operNode->keyType, (int)operNode->status);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t restoreRunningTask(void)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d WHERE %s = %d", 
								DIFF_TABLE_NAME, 
								DIFF_ROW_STATUS, (int)Diff_Normal, 
								DIFF_ROW_STATUS, (int)Diff_Running);

			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;	
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t geOperList(const int64_t& key, KeyType keyType, OperNodes& operList)
	{
		if(INVALID_ID == key)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %lld AND %s = %d ORDER BY %s", 
								DIFF_ROW_DIFFID, DIFF_ROW_OPER, 
								DIFF_ROW_PRIORITY, DIFF_ROW_STATUS,
								DIFF_TABLE_NAME,
								DIFF_ROW_KEY, key,
								DIFF_ROW_KEYTYPE, (int)keyType,
								DIFF_ROW_DIFFID);
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				OperNode pOperNode(new st_OperNode);
				pOperNode->id = q.getIntField(0);
				pOperNode->key = key;
				pOperNode->keyType = keyType;
				pOperNode->oper = OperType(q.getIntField(1));
				pOperNode->priority = q.getInt64Field(2);
				pOperNode->status = DiffStatus(q.getIntField(3));
				operList.push_back(pOperNode);
				q.nextRow();
			}			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t refreshDiff(const IdList& idList, const DiffStatus status)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);

		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			switch(status)
			{
			case Diff_Normal:
				(void)sql.format("UPDATE %s SET %s = %d WHERE %s IN(%s)", 
									DIFF_TABLE_NAME, DIFF_ROW_STATUS, (int)status, 
									DIFF_ROW_DIFFID, inStr.c_str());
				break;
			case Diff_Running:
				(void)sql.format("UPDATE %s SET %s = %d WHERE %s IN(%s) AND %s IN(%d,%d,%d)", 
									DIFF_TABLE_NAME, DIFF_ROW_STATUS, (int)status, 
									DIFF_ROW_DIFFID, inStr.c_str(),
									DIFF_ROW_OPER, (int)OT_Created, (int)OT_Edited, (int)OT_SetSync);
				break;
			case Diff_Failed:
				//the last 3 digits of DIFF_ROW_PRIORITY is manual priority
				(void)sql.format("UPDATE %s SET %s = %d, %s = %s + %d WHERE %s IN(%s)", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_STATUS, (int)status,
									DIFF_ROW_PRIORITY, DIFF_ROW_PRIORITY,
									PRIORITY_INCREMENT,
									DIFF_ROW_DIFFID, inStr.c_str());
				break;
			case Diff_Complete:
				(void)sql.format("DELETE FROM %s WHERE %s IN(%s)", 
									DIFF_TABLE_NAME, DIFF_ROW_DIFFID, inStr.c_str());
				needRefresh_ = true;
				break;
			default:
				SERVICE_DEBUG(MODULE_NAME, RT_ERROR, "unkonwn status:%d", (int)status);
				break;
			}

			int32_t iModify = db_.execDML(sql);

			SERVICE_DEBUG(MODULE_NAME, RT_OK, "set Diff-Node status. diff:%s, status:%d, iModify:%d", 
					inStr.c_str(), (int)status, iModify);

			/*
			if(Diff_Running==status)
			{
				CppSQLite3Buffer sql2;
				const char* sqlStr = sql2.format("UPDATE %s SET %s = %d WHERE %s IN (%d,%d) AND %s IN(%s)", 
					DIFF_TABLE_NAME, DIFF_ROW_OPER, (int)OT_Edited, 
					DIFF_ROW_OPER, (int)OT_Created, (int)OT_SetSync,
					DIFF_ROW_DIFFID, inStr.c_str());
				int32_t iModify2 = db_.execDML(sql2);
				if(iModify2 > 0)
				{
					SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify2, sqlStr);
				}
			}
			*/
			return RT_OK;	
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getRunningDiff(KeyType keyType, const int64_t& id, IdList& idList, bool& hasNewOper)
	{
		if(INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s FROM %s WHERE %s = %lld AND %s = %d", 
								DIFF_ROW_STATUS, DIFF_ROW_DIFFID,
								DIFF_TABLE_NAME,
								DIFF_ROW_KEY, id,
								DIFF_ROW_KEYTYPE, (int)keyType);
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				DiffStatus status = DiffStatus(q.getIntField(0));
				if(Diff_Running==status)
				{
					idList.push_back(q.getInt64Field(1));
				}
				else
				{
					hasNewOper = true;
				}
				q.nextRow();
			}			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateOper(KeyType keyType, const int64_t& id, OperType oldOper, OperType newOper)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d WHERE %s = %d AND %s = %lld AND %s = %d", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_OPER, (int)newOper,
									DIFF_ROW_KEYTYPE, (int)keyType,
									DIFF_ROW_KEY, id,
									DIFF_ROW_OPER, (int)oldOper);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t completeDiff(KeyType keyType, const int64_t& id)
	{
		IdList idList;
		idList.push_back(id);
		return completeDiff(keyType, idList);
	}

	virtual int32_t completeDiff(KeyType keyType, const IdList& idList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s IN(%s) AND %s = %d", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_KEY, inStr.c_str(),
									DIFF_ROW_KEYTYPE, (int)keyType);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			std::list<std::wstring> localPaths;
			bool isErrorChanged = false;
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s FROM %s WHERE %s = %d AND %s IN(%s)", 
								DIFFPATH_ROW_PATH,
								DIFFPATH_ROW_ERRORCODE,
								DIFFPATH_TABLE_NAME, 
								DIFFPATH_ROW_TYPE, (int)keyType,
								DIFFPATH_ROW_ID, inStr.c_str());
			CppSQLite3Query q1 = db_.execQuery(sql1);
			while(!q1.eof())
			{
				localPaths.push_back(SD::Utility::String::utf8_to_wstring(q1.getStringField(0)));
				if(0!=q1.getIntField(1))
				{
					isErrorChanged = true;
				}
				q1.nextRow();
			}

			CppSQLite3Buffer sql2;
			(void)sql2.format("DELETE FROM %s WHERE %s = %d AND %s IN(%s)", 
									DIFFPATH_TABLE_NAME,
									DIFFPATH_ROW_TYPE, (int)keyType, 
									DIFFPATH_ROW_ID, inStr.c_str());
			(void)db_.execDML(sql2);

			for(std::list<std::wstring>::const_iterator it = localPaths.begin(); it!= localPaths.end(); ++it)
			{
				userContext_->getOverlayIconMgr()->refreshOverlayIcon(*it);
			}
			if(isErrorChanged)
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Decrease)));
			}
			needRefresh_ = true;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getMove(KeyType keyType, const IdList& idList, IdList& moveList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sqlCheck;
			(void)sqlCheck.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d AND %s IN(%s)",
				DIFF_ROW_KEY, DIFF_TABLE_NAME, 
				DIFF_ROW_KEYTYPE, (int)keyType,
				DIFF_ROW_OPER, OT_Moved,
				DIFF_ROW_KEY, inStr.c_str());
			CppSQLite3Query qCheck = db_.execQuery(sqlCheck);
			while(!qCheck.eof())
			{
				moveList.push_back(qCheck.getInt64Field(0));
				qCheck.nextRow();
			}
			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getDelete(KeyType keyType, IdList& idList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sqlCheck;
			(void)sqlCheck.format("SELECT %s FROM %s WHERE %s = %d AND %s = %d",
				DIFF_ROW_KEY, DIFF_TABLE_NAME, 
				DIFF_ROW_KEYTYPE, (int)keyType,
				DIFF_ROW_OPER, OT_Deleted);
			CppSQLite3Query qCheck = db_.execQuery(sqlCheck);
			while(!qCheck.eof())
			{
				idList.push_back(qCheck.getInt64Field(0));
				qCheck.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t completeDiffExMove(KeyType keyType, const IdList& idList, IdList& moveList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		std::string notInStr = Sync::getInStr(moveList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s = %d AND %s IN(%s) AND %s NOT IN(%s)",
				DIFF_TABLE_NAME,
				DIFF_ROW_KEYTYPE, (int)keyType,
				DIFF_ROW_KEY, inStr.c_str(),
				DIFF_ROW_KEY, notInStr.c_str());
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			std::list<std::wstring> localPaths;
			bool isErrorChanged = false;
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s FROM %s WHERE %s = %d AND %s IN(%s) AND %s NOT IN(%s)", 
				DIFFPATH_ROW_PATH,
				DIFFPATH_ROW_ERRORCODE,
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_TYPE, (int)keyType,
				DIFFPATH_ROW_ID, inStr.c_str(),
				DIFFPATH_ROW_ID, notInStr.c_str());
			CppSQLite3Query q1 = db_.execQuery(sql1);
			while(!q1.eof())
			{
				localPaths.push_back(SD::Utility::String::utf8_to_wstring(q1.getStringField(0)));
				if(0!=q1.getIntField(1))
				{
					isErrorChanged = true;
				}
				q1.nextRow();
			}

			CppSQLite3Buffer sql2;
			(void)sql2.format("DELETE FROM %s WHERE %s = %d AND %s IN(%s) AND %s NOT IN(%s)",
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_TYPE, (int)keyType, 
				DIFFPATH_ROW_ID, inStr.c_str(),
				DIFFPATH_ROW_ID, notInStr.c_str());
			(void)db_.execDML(sql2);

			for(std::list<std::wstring>::const_iterator it = localPaths.begin(); it!= localPaths.end(); ++it)
			{
				userContext_->getOverlayIconMgr()->refreshOverlayIcon(*it);
			}
			if(isErrorChanged)
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Decrease)));
			}
			needRefresh_ = true;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t hiddenDiff(const IdList& idList)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d, %s = %d, %s = %d WHERE %s = %d AND %s = %d AND %s IN(%s)", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_STATUS, (int)Diff_Hidden,
									DIFF_ROW_PRIORITY, (int)PRIORITY_LEVEL7,
									DIFF_ROW_OPER, (int)OT_Edited,
									DIFF_ROW_KEYTYPE, (int)Key_LocalID,
									DIFF_ROW_OPER, (int)OT_Created,
									DIFF_ROW_KEY, inStr.c_str());
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			std::list<std::wstring> localPaths;
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s FROM %s WHERE %s = %d AND %s IN(%s)", 
								DIFFPATH_ROW_PATH,
								DIFFPATH_TABLE_NAME, 
								DIFFPATH_ROW_TYPE, (int)Key_LocalID,
								DIFFPATH_ROW_ID, inStr.c_str());
			CppSQLite3Query q1 = db_.execQuery(sql1);
			while(!q1.eof())
			{
				localPaths.push_back(SD::Utility::String::utf8_to_wstring(q1.getStringField(0)));
				q1.nextRow();
			}

			CppSQLite3Buffer sql2;
			(void)sql2.format("DELETE FROM %s WHERE %s = %d AND %s IN(%s)", 
									DIFFPATH_TABLE_NAME,
									DIFFPATH_ROW_TYPE, (int)Key_LocalID, 
									DIFFPATH_ROW_ID, inStr.c_str());
			(void)db_.execDML(sql2);

			for(std::list<std::wstring>::const_iterator it = localPaths.begin(); it!= localPaths.end(); ++it)
			{
				userContext_->getOverlayIconMgr()->refreshOverlayIcon(*it);
			}
			needRefresh_ = true;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t lowerHiddenPriority()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = %s + %d WHERE %s = %d", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_PRIORITY, DIFF_ROW_PRIORITY,
									PRIORITY_INCREMENT,
									DIFF_ROW_STATUS, (int)Diff_Hidden);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t initPriority()
	{
		//update tb_diffInfo set priority = (priority&7) + 8
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = (%s&%d) + %d WHERE %s > %d", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_PRIORITY, DIFF_ROW_PRIORITY, 
									PRIORITY_INCREMENT-1, PRIORITY_INCREMENT,
									DIFF_ROW_PRIORITY, DIFF_SLEEP_LIMEN);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual bool isNeedRefresh()
	{
		return needRefresh_;
	}

	virtual int32_t getIncDiff(int64_t lastAutoInc, int64_t& maxAutoInc, DiffPathNodes& diffInfos)
	{
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql2;
			(void)sql2.format("SELECT MAX(%s) FROM %s", 
								DIFF_ROW_DIFFID,
								DIFF_TABLE_NAME);
			CppSQLite3Query q2 = db_.execQuery(sql2);

			if(!q2.eof())
			{
				maxAutoInc = q2.getInt64Field(0);
			}
			
			needRefresh_ = false;
			if(maxAutoInc <= lastAutoInc)
			{
				return RT_OK;
			}

			if(maxAutoInc > lastAutoInc + 1000)
			{
				maxAutoInc = lastAutoInc + 1000;
				needRefresh_ = true;
			}

			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s, %s, %s, %s FROM %s WHERE %s>%lld AND %s<=%lld", 
								DIFF_ROW_KEY, DIFF_ROW_KEYTYPE, DIFF_ROW_SIZE, DIFF_ROW_OPER,
								DIFF_TABLE_NAME, 
								DIFF_ROW_DIFFID, lastAutoInc,
								DIFF_ROW_DIFFID, maxAutoInc);
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				KeyType keyType = (KeyType)q.getIntField(1);
				OperType operType = (OperType)q.getIntField(3);
				if((OT_Deleted == operType)&&(Key_LocalID == keyType))
				{
					q.nextRow();
					continue;
				}
				DiffPathNode pDiffPathNode(new st_DiffPathNode);
				pDiffPathNode->key = q.getInt64Field(0);
				pDiffPathNode->keyType = keyType;
				pDiffPathNode->size = q.getInt64Field(2);
				diffInfos.push_back(pDiffPathNode);

				q.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t replaceIncPath(DiffPathNodes& diffInfos)
	{
		if(diffInfos.empty())
		{
			return RT_OK;
		}
		SERVICE_FUNC_TRACE(MODULE_NAME, "replaceIncPath");
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			CppSQLite3Buffer sqlSel;
			//When a new operation that triggered the error list change notify UI. 
			bool isErrorDecrease = false;
			db_.beginTransaction();
			for(DiffPathNodes::const_iterator it = diffInfos.begin();
				it != diffInfos.end(); ++it)
			{
				(void)sqlSel.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %lld AND %s = %d LIMIT 0,1", 
								DIFFPATH_ROW_SIZE, DIFFPATH_ROW_PATH, DIFFPATH_ROW_REMOTEPATH, DIFFPATH_ROW_ERRORCODE,
								DIFFPATH_TABLE_NAME, 
								DIFFPATH_ROW_ID, it->get()->key,
								DIFFPATH_ROW_TYPE, it->get()->keyType);
				CppSQLite3Query q = db_.execQuery(sqlSel);
				if(!q.eof())
				{
					int64_t size = q.getInt64Field(0);
					std::wstring oldPath = SD::Utility::String::utf8_to_wstring(q.getStringField(1));
					std::wstring oldRemotePath = SD::Utility::String::utf8_to_wstring(q.getStringField(2));

					if((!isErrorDecrease)&&(RT_OK!=q.getIntField(3)))
					{
						isErrorDecrease = true;
					}

					if(size > it->get()->size)
					{
						it->get()->size = size;
					}
					if(it->get()->localPath.empty())
					{
						it->get()->localPath = oldPath;
					}
					if(it->get()->remotePath.empty())
					{
						it->get()->remotePath = oldRemotePath;
					}

					(void)sql.format("UPDATE %s SET %s = '%s', %s = '%s', %s = %lld, %s = %d WHERE %s = %lld AND %s = %d",
					DIFFPATH_TABLE_NAME, 
					DIFFPATH_ROW_PATH, CppSQLiteUtility::formatSqlStr(it->get()->localPath).c_str(),
					DIFFPATH_ROW_REMOTEPATH, CppSQLiteUtility::formatSqlStr(it->get()->remotePath).c_str(),
					DIFFPATH_ROW_SIZE, it->get()->size,
					DIFFPATH_ROW_ERRORCODE, RT_OK,	//new operation occurs, reset errorCode
					DIFFPATH_ROW_ID, it->get()->key,
					DIFFPATH_ROW_TYPE, it->get()->keyType);
					(void)db_.execDML(sql);

					printObj->lastField<int64_t>(it->get()->key);

					if(oldPath!=it->get()->localPath)
					{
						if(RT_OK!=replaceLocalPath(oldPath, it->get()->localPath))
						{
							return RT_SQLITE_ERROR;
						}
					}
					if(oldRemotePath!=it->get()->remotePath)
					{
						if(RT_OK!=replaceRemotePath(oldRemotePath, it->get()->remotePath))
						{
							return RT_SQLITE_ERROR;
						}
					}
				}
				else
				{
					CppSQLite3Buffer sqlCheck;
					(void)sqlCheck.format("SELECT 1 FROM %s WHERE %s = %lld AND %s = %d AND %s <> %d LIMIT 0,1", 
										DIFF_TABLE_NAME,
										DIFF_ROW_KEY, it->get()->key,
										DIFF_ROW_KEYTYPE, it->get()->keyType,
										DIFF_ROW_STATUS, (int)Diff_Hidden);
					CppSQLite3Query qCheck = db_.execQuery(sqlCheck);

					if(qCheck.eof())
					{				
						continue;
					}

					(void)sql.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s) VALUES(%lld, %d, '%s', '%s', %lld, %d)",
						DIFFPATH_TABLE_NAME,
						DIFFPATH_ROW_ID, DIFFPATH_ROW_TYPE,
						DIFFPATH_ROW_PATH, DIFFPATH_ROW_REMOTEPATH,
						DIFFPATH_ROW_SIZE, DIFFPATH_ROW_ERRORCODE,
						it->get()->key, 
						it->get()->keyType,
						CppSQLiteUtility::formatSqlStr(it->get()->localPath).c_str(),
						CppSQLiteUtility::formatSqlStr(it->get()->remotePath).c_str(),
						it->get()->size,
						RT_OK);
					(void)db_.execDML(sql);

					printObj->lastField<int64_t>(it->get()->key);
				}
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "replaceIncPath. diffInfos size:%d, [%s]", diffInfos.size(), printObj->getMsg().c_str());
			if(isErrorDecrease)
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Decrease)));
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t insertIncPath(DiffPathNodes& diffInfos)
	{
		if(diffInfos.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			CppSQLite3Buffer sql;
			db_.beginTransaction();
			for(DiffPathNodes::const_iterator it = diffInfos.begin();
				it != diffInfos.end(); ++it)
			{
				(void)sql.format("REPLACE INTO %s(%s, %s, %s, %s, %s, %s) VALUES(%lld, %d, '%s', '%s', %lld, %d)",
					DIFFPATH_TABLE_NAME,
					DIFFPATH_ROW_ID, DIFFPATH_ROW_TYPE,
					DIFFPATH_ROW_PATH, DIFFPATH_ROW_REMOTEPATH,
					DIFFPATH_ROW_SIZE, DIFFPATH_ROW_ERRORCODE,
					it->get()->key, 
					it->get()->keyType,
					CppSQLiteUtility::formatSqlStr(it->get()->localPath).c_str(),
					CppSQLiteUtility::formatSqlStr(it->get()->remotePath).c_str(),
					it->get()->size,
					RT_OK);
				(void)db_.execDML(sql);

				printObj->lastField<int64_t>(it->get()->key);
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "insertIncPath. diffInfos size:%d, [%s]", diffInfos.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t replaceSubPath(const std::wstring& oldPath, const std::wstring& newPath)
	{
		boost::mutex::scoped_lock lock(mutex_);

		if(RT_OK!=replaceLocalPath(oldPath, newPath))
		{
			return RT_SQLITE_ERROR;
		}

		std::wstring oldRemotePath = oldPath.substr(rootLen_, oldPath.length()-rootLen_);
		std::wstring newRemotePath = newPath.substr(rootLen_, newPath.length()-rootLen_);
		if(RT_OK!=replaceRemotePath(oldRemotePath, newRemotePath))
		{
			return RT_SQLITE_ERROR;
		}

		return RT_OK;
	}

	virtual int32_t getErrorCode(KeyType keyType, const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			sql.format("SELECT %s FROM %s WHERE %s = %lld AND %s = %d", 
				DIFFPATH_ROW_ERRORCODE,
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_ID, id,
				DIFFPATH_ROW_TYPE, keyType);
			CppSQLite3Query q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateErrorCode(KeyType keyType, const IdList& idList, int32_t errorCode)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStr(idList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d, %s = %s + %d WHERE %s IN(%s) AND %s = %d",
				DIFF_TABLE_NAME, 
				DIFF_ROW_STATUS, (errorCode==RT_OK)?Diff_Normal:Diff_Failed,
				DIFF_ROW_PRIORITY, (errorCode==RT_OK)?0:DIFF_ROW_PRIORITY,
				PRIORITY_INCREMENT,
				DIFF_ROW_KEY, inStr.c_str(),
				DIFF_ROW_KEYTYPE, (int)keyType);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			CppSQLite3Buffer sql2;
			const char* sqlStr2 = sql2.format("UPDATE %s SET %s = %d WHERE %s IN(%s) AND %s = %d",
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_ERRORCODE, errorCode,
				DIFFPATH_ROW_ID, inStr.c_str(),
				DIFFPATH_ROW_TYPE, keyType);
			int32_t iModify2 = db_.execDML(sql2);

			if((iModify2>0)&&(0!=errorCode))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify2, sqlStr2);
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Increase)));
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateErrorCode(KeyType keyType, const int64_t& id, int32_t errorCode = 0)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			
			const char* sqlStr = sql.format("UPDATE %s SET %s = %d WHERE %s = %lld AND %s = %d AND %s <> %d",
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_ERRORCODE, errorCode,
				DIFFPATH_ROW_ID, id,
				DIFFPATH_ROW_TYPE, keyType,
				DIFFPATH_ROW_ERRORCODE, errorCode);
			int32_t iModify = db_.execDML(sql);

			if((iModify>0)&&(0!=errorCode))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Increase)));
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteCompletePath(const int64_t& key, const KeyType keyType, std::wstring& localPath)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT %s, %s FROM %s WHERE %s = %d AND %s = %lld LIMIT 0,1", 
								DIFFPATH_ROW_PATH,
								DIFFPATH_ROW_ERRORCODE,
								DIFFPATH_TABLE_NAME, 
								DIFFPATH_ROW_TYPE, (int)keyType,
								DIFFPATH_ROW_ID, key);
			CppSQLite3Query q1 = db_.execQuery(sql1);
			if(q1.eof())
			{
				return RT_OK;
			}
			localPath = SD::Utility::String::utf8_to_wstring(q1.getStringField(0));
			
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s = %d AND %s = %lld", 
								DIFFPATH_TABLE_NAME, 
								DIFFPATH_ROW_TYPE, (int)keyType,
								DIFFPATH_ROW_ID, key);
			int32_t iModify = db_.execDML(sql);
			if(0!=q1.getIntField(1))
			{
				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
					SD::Utility::String::format_string(L"%d", Error_Decrease)));
			}
			//SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			needRefresh_ = true;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t delAllDiffPath()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s", DIFFPATH_TABLE_NAME);
			int32_t iModify = db_.execDML(sql);
			if(iModify > 0)
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

				userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ERROR_CHANGED, 
						SD::Utility::String::format_string(L"%d", Error_Decrease)));
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getDiffCount()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT COUNT(1) FROM %s", DIFF_TABLE_NAME);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return -1;
	}

	virtual int32_t getNormalDiffCount()
	{
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT COUNT(1) FROM %s WHERE %s = %d", 
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_ERRORCODE, 0);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			if(!q1.eof())
			{
				lastNormalCnt_ = q1.getIntField(0);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return lastNormalCnt_;
	}

	virtual int32_t getErrorDiffCount()
	{
		try
		{
			CppSQLite3Buffer sql1;
			(void)sql1.format("SELECT COUNT(1) FROM %s WHERE %s <> %d", 
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_ERRORCODE, 0);
			CppSQLite3Query q1 = db_.execQuery(sql1);

			if(!q1.eof())
			{
				lastErrorCnt_ = q1.getIntField(0);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return lastErrorCnt_;
	}

	virtual int32_t getLocalFileDiff(IdList& idList, IdList& fileIdList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::string inStr = Sync::getInStr(idList);
			CppSQLite3Buffer sql;
			(void)sql.format("SELECT %s FROM %s WHERE %s IN (%s) AND %s <> %d",
				DIFF_ROW_KEY,
				DIFF_TABLE_NAME,
				DIFF_ROW_KEY, inStr.c_str(),
				DIFF_ROW_SIZE, 0);
			CppSQLite3Query q = db_.execQuery(sql);

			while(!q.eof())
			{
				fileIdList.push_back(q.getInt64Field(0));
				q.nextRow();
			}

			idList.clear();
			CppSQLite3Buffer sql2;
			(void)sql2.format("SELECT %s FROM %s WHERE %s IN (%s) AND %s = %d",
				DIFF_ROW_KEY,
				DIFF_TABLE_NAME,
				DIFF_ROW_KEY, inStr.c_str(),
				DIFF_ROW_SIZE, 0);
			CppSQLite3Query q2 = db_.execQuery(sql2);

			while(!q2.eof())
			{
				idList.push_back(q2.getInt64Field(0));
				q2.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t clearDiff(KeyType keyType)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s = %d", 
				DIFF_TABLE_NAME, 
				DIFF_ROW_KEYTYPE, (int)keyType);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			CppSQLite3Buffer sql2;
			const char* sqlStr2 = sql2.format("DELETE FROM %s WHERE %s = %d", 
				DIFFPATH_TABLE_NAME,
				DIFFPATH_ROW_TYPE, (int)keyType);
			iModify = db_.execDML(sql2);
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr2);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	void createDiffTable(const std::wstring& parent)
	{
		try
		{
			std::wstring path = parent + SQLITE_DIFFINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(DIFF_TABLE_NAME))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d);", 
									DIFF_TABLE_NAME, 
									DIFF_ROW_DIFFID,
									DIFF_ROW_KEY, 
									DIFF_ROW_KEYTYPE, 
									DIFF_ROW_OPER, (int)SRK_NONE, 
									DIFF_ROW_STATUS, (int)Diff_Normal, 
									DIFF_ROW_PRIORITY, DEFAULT_PRIORITY,
									DIFF_ROW_SIZE, 0);
				(void)db_.execDML(sql);			
				CppSQLite3Buffer sqlIdx1;
				(void)sqlIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"difftable_idx1", DIFF_TABLE_NAME, DIFF_ROW_KEY);
				(void)db_.execDML(sqlIdx1);
			}

			if(!db_.tableExists(DIFFPATH_TABLE_NAME))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d);", 
									DIFFPATH_TABLE_NAME, 
									DIFFPATH_ROW_ID,
									DIFFPATH_ROW_TYPE,
									DIFFPATH_ROW_PATH,
									DIFFPATH_ROW_REMOTEPATH,
									DIFFPATH_ROW_SIZE, 0,
									DIFFPATH_ROW_ERRORCODE, 0);

				(void)db_.execDML(sql);			
				CppSQLite3Buffer sqlIdx1;
				(void)sqlIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"diffPathtable_idx1", DIFFPATH_TABLE_NAME, DIFFPATH_ROW_ID);
				(void)db_.execDML(sqlIdx1);
			}
			else
			{
				//TODO delete before TR6
				CppSQLite3Buffer sql1;
				(void)sql1.format("SELECT %s FROM %s limit 0,1", DIFFPATH_ROW_PATH, DIFFPATH_TABLE_NAME);
				CppSQLite3Query q1 = db_.execQuery(sql1);
								
				if(!q1.eof())
				{
					std::string path = q1.getStringField(0);
					if(std::string::npos != path.find("/"))
					{
						CppSQLite3Buffer bufUpdate;
						bufUpdate.format("UPDATE %s SET %s=REPLACE(%s, '/', '\\'), %s=REPLACE(%s, '/', '\\')", 
							DIFFPATH_TABLE_NAME, 
							DIFFPATH_ROW_PATH,
							DIFFPATH_ROW_PATH,
							DIFFPATH_ROW_REMOTEPATH,
							DIFFPATH_ROW_REMOTEPATH);
						int32_t iModify = db_.execDML(bufUpdate);
						SERVICE_INFO(MODULE_NAME, RT_OK, "update diffPath for /. iModify:%d.", iModify);
					}
				}
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	int32_t replaceLocalPath(const std::wstring& oldPath, const std::wstring& newPath)
	{
		try
		{
			CppSQLite3Buffer bufSQL;
			//update tb_localInfo set path = replace(path, "old" , "new")  where path||"/" like "old/%"
			bufSQL.format("UPDATE %s SET %s=REPLACE(%s, '%s', '%s') WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_PATH,
				DIFFPATH_ROW_PATH,
				CppSQLiteUtility::formatSqlStr(oldPath).c_str(), 
				CppSQLiteUtility::formatSqlStr(newPath).c_str(), 
				DIFFPATH_ROW_PATH, 
				CppSQLiteUtility::formaSqlLikeStr(oldPath).c_str());
			(void)db_.execDML(bufSQL);
		
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	int32_t replaceRemotePath(const std::wstring& oldPath, const std::wstring& newPath)
	{
		try
		{
			CppSQLite3Buffer bufSQL2;
			bufSQL2.format("UPDATE %s SET %s=REPLACE(%s, '%s', '%s') WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
				DIFFPATH_TABLE_NAME, 
				DIFFPATH_ROW_REMOTEPATH,
				DIFFPATH_ROW_REMOTEPATH,
				CppSQLiteUtility::formatSqlStr(oldPath).c_str(), 
				CppSQLiteUtility::formatSqlStr(newPath).c_str(), 
				DIFFPATH_ROW_REMOTEPATH, 
				CppSQLiteUtility::formaSqlLikeStr(oldPath).c_str());
			(void)db_.execDML(bufSQL2);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	UserContext* userContext_;

	boost::mutex mutex_;
	boost::mutex m_mutexData;
	CppSQLite3DB db_;

	bool needRefresh_;
	int32_t rootLen_;
	int32_t lastNormalCnt_;
	int32_t lastErrorCnt_;
};

std::auto_ptr<DiffTable>  DiffTable::create(UserContext* userContext, const std::wstring& parent)
{
	return std::auto_ptr<DiffTable>(new DiffTableImpl(userContext, parent));
}