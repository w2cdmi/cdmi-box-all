#include "TransTaskTable.h"
#include "Utility.h"
#include "SyncUtility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTaskTable")
#endif

class TransTaskTableImpl : public TransTaskTable
{
public:
	TransTaskTableImpl(const std::wstring& parent)
	{
		createTransTaskTable(parent);
	}

	virtual int32_t addNode(const AsyncTransTaskNode& node)
	{
		if (isExist(node.id))
		{
			return RT_SQLITE_EXIST;
		}

		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Binary blob;
			int len = node.blocks.blockNum*sizeof(AsyncTransTaskBlock) + sizeof(uint32_t);
			unsigned char* buf = blob.allocBuffer(len);
			memcpy(buf, &node.blocks.blockNum, sizeof(uint32_t));
			memcpy(buf+sizeof(uint32_t), (unsigned char*)node.blocks.blocks, len-sizeof(uint32_t));

			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s','%s','%s',%d,%d,%d,%d,'%s','%s',%Q,'%s')", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_ID, 
				TRANS_TASK_ROW_GROUP, 
				TRANS_TASK_ROW_PARENT, 
				TRANS_TASK_ROW_OWNERID, 
				TRANS_TASK_ROW_NAME, 
				TRANS_TASK_ROW_TYPE, 
				TRANS_TASK_ROW_STATUS, 
				TRANS_TASK_ROW_PRIORITY, 
				TRANS_TASK_ROW_ALGORITHM,
				TRANS_TASK_ROW_SIGNATURE,
				TRANS_TASK_ROW_BLOCK_SIGNATURE,
				TRANS_TASK_ROW_BLOCKS, 
				TRANS_TASK_ROW_USER_DEFINE, 
				CppSQLiteUtility::formatSqlStr(node.id.id).c_str(), 
				CppSQLiteUtility::formatSqlStr(node.id.group).c_str(), 
				CppSQLiteUtility::formatSqlStr(node.parent).c_str(), 
				CppSQLiteUtility::formatSqlStr(node.ownerId).c_str(),
				CppSQLiteUtility::formatSqlStr(node.name).c_str(), 
				node.id.type, 
				node.status, 
				node.priority, 
				node.signature.algorithm, 
				node.signature.signature.c_str(), 
				node.signature.blockSignature.c_str(), 
				blob.getEncoded(), 
				CppSQLiteUtility::formatSqlStr(node.userDefine).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateNode(const AsyncTransTaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Binary blob;
			int len = node.blocks.blockNum*sizeof(AsyncTransTaskBlock) + sizeof(uint32_t);
			unsigned char* buf = blob.allocBuffer(len);
			memcpy(buf, &node.blocks.blockNum, sizeof(uint32_t));
			memcpy(buf+sizeof(uint32_t), (unsigned char*)node.blocks.blocks, len-sizeof(uint32_t));

			CppSQLite3Buffer sql;
			//sql.format("UPDATE %s SET %s='%s', %s='%s', %s=%d, %s=%d, %s='%s', %s=%Q, %s='%s' WHERE %s='%s' AND %s='%s' AND %s=%d", 
			sql.format("UPDATE %s SET %s='%s', %s='%s', %s='%s', %s=%d, %s='%s', %s='%s', %s=%Q, %s='%s' WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_PARENT, CppSQLiteUtility::formatSqlStr(node.parent).c_str(), 
				TRANS_TASK_ROW_OWNERID, CppSQLiteUtility::formatSqlStr(node.ownerId).c_str(), 
				TRANS_TASK_ROW_NAME, CppSQLiteUtility::formatSqlStr(node.name).c_str(), 
				/*TRANS_TASK_ROW_STATUS, node.status, 
				TRANS_TASK_ROW_PRIORITY, node.priority, */
				TRANS_TASK_ROW_ALGORITHM, node.signature.algorithm, 
				TRANS_TASK_ROW_SIGNATURE, node.signature.signature.c_str(), 
				TRANS_TASK_ROW_BLOCK_SIGNATURE, node.signature.blockSignature.c_str(), 
				TRANS_TASK_ROW_BLOCKS, blob.getEncoded(), 
				TRANS_TASK_ROW_USER_DEFINE, CppSQLiteUtility::formatSqlStr(node.userDefine).c_str(), 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(node.id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(node.id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, node.id.type);
			(void)db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNode(const AsyncTaskId& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, id.type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addNodes(const AsyncTransTaskNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for (AsyncTransTaskNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				AsyncTransTaskNode node = *it;
				
				CppSQLite3Binary blob;
				int len = node.blocks.blockNum*sizeof(AsyncTransTaskBlock) + sizeof(uint32_t);
				unsigned char* buf = blob.allocBuffer(len);
				memcpy(buf, &node.blocks.blockNum, sizeof(uint32_t));
				memcpy(buf+sizeof(uint32_t), (unsigned char*)node.blocks.blocks, len-sizeof(uint32_t));

				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES('%s','%s','%s','%s','%s',%d,%d,%d,%d,'%s','%s',%Q,'%s')", 
					TRANS_TASK_TABLE_NAME, 
					TRANS_TASK_ROW_ID, 
					TRANS_TASK_ROW_GROUP, 
					TRANS_TASK_ROW_PARENT, 
					TRANS_TASK_ROW_OWNERID, 
					TRANS_TASK_ROW_NAME, 
					TRANS_TASK_ROW_TYPE, 
					TRANS_TASK_ROW_STATUS, 
					TRANS_TASK_ROW_PRIORITY, 
					TRANS_TASK_ROW_ALGORITHM,
					TRANS_TASK_ROW_SIGNATURE,
					TRANS_TASK_ROW_BLOCK_SIGNATURE,
					TRANS_TASK_ROW_BLOCKS, 
					TRANS_TASK_ROW_USER_DEFINE, 
					CppSQLiteUtility::formatSqlStr(node.id.id).c_str(), 
					CppSQLiteUtility::formatSqlStr(node.id.group).c_str(), 
					CppSQLiteUtility::formatSqlStr(node.parent).c_str(), 
					CppSQLiteUtility::formatSqlStr(node.name).c_str(), 
					node.id.type, 
					node.status, 
					node.priority, 
					node.signature.algorithm, 
					node.signature.signature.c_str(), 
					node.signature.blockSignature.c_str(), 
					blob.getEncoded(), 
					CppSQLiteUtility::formatSqlStr(node.userDefine).c_str());
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateNodes(const AsyncTransTaskNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for (AsyncTransTaskNodes::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				AsyncTransTaskNode node = *it;
				
				CppSQLite3Binary blob;
				int len = node.blocks.blockNum*sizeof(AsyncTransTaskBlock) + sizeof(uint32_t);
				unsigned char* buf = blob.allocBuffer(len);
				memcpy(buf, &node.blocks.blockNum, sizeof(uint32_t));
				memcpy(buf+sizeof(uint32_t), (unsigned char*)node.blocks.blocks, len-sizeof(uint32_t));

				CppSQLite3Buffer sql;
				//sql.format("UPDATE %s SET %s='%s', %s='%s', %s=%d, %s=%d, %s='%s', %s=%Q, %s='%s' WHERE %s='%s' AND %s='%s' AND %s=%d", 
				sql.format("UPDATE %s SET %s='%s', %s='%s', %s='%s', %s=%d, %s='%s', %s='%s', %s=%Q, %s='%s' WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_PARENT, CppSQLiteUtility::formatSqlStr(node.parent).c_str(), 
				TRANS_TASK_ROW_OWNERID, CppSQLiteUtility::formatSqlStr(node.ownerId).c_str(), 
				TRANS_TASK_ROW_NAME, CppSQLiteUtility::formatSqlStr(node.name).c_str(), 
				/*TRANS_TASK_ROW_STATUS, node.status, 
				TRANS_TASK_ROW_PRIORITY, node.priority, */
				TRANS_TASK_ROW_ALGORITHM, node.signature.algorithm, 
				TRANS_TASK_ROW_SIGNATURE, node.signature.signature.c_str(), 
				TRANS_TASK_ROW_BLOCK_SIGNATURE, node.signature.blockSignature.c_str(), 
				TRANS_TASK_ROW_BLOCKS, blob.getEncoded(), 
				TRANS_TASK_ROW_USER_DEFINE, CppSQLiteUtility::formatSqlStr(node.userDefine).c_str(), 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(node.id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(node.id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, node.id.type);
				(void)db_.execDML(sql);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNodes(const AsyncTaskIds& ids)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for (AsyncTaskIds::const_iterator it = ids.begin(); it != ids.end(); ++it)
			{
				AsyncTaskId id = *it;
				CppSQLite3Buffer sql;
				(void)sql.format("DELETE FROM %s WHERE %s='%s' AND %s='%s' AND %s=%d", 
					TRANS_TASK_TABLE_NAME, 
					TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
					TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
					TRANS_TASK_ROW_TYPE, id.type);
				(void)db_.execDML(sql);

				printObj->lastField<std::string>(SD::Utility::String::wstring_to_string(id.id));
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "deleteNodes. ids size:%d, [%s]", ids.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNodes(KeyType keyType)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_TYPE, (int)keyType);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNode(const AsyncTaskId& id, AsyncTransTaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			CppSQLite3Binary blob;
			const char* sqlStr = sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_ROW_PARENT, 
				TRANS_TASK_ROW_OWNERID, 
				TRANS_TASK_ROW_NAME, 
				TRANS_TASK_ROW_STATUS, 
				TRANS_TASK_ROW_PRIORITY, 
				TRANS_TASK_ROW_ALGORITHM,
				TRANS_TASK_ROW_SIGNATURE,
				TRANS_TASK_ROW_BLOCK_SIGNATURE,
				TRANS_TASK_ROW_BLOCKS, 
				TRANS_TASK_ROW_USER_DEFINE, 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, id.type);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.id = id;
			node.parent = Utility::String::utf8_to_wstring(q.getStringField(0));
			node.ownerId = Utility::String::utf8_to_wstring(q.getStringField(1));
			node.name = Utility::String::utf8_to_wstring(q.getStringField(2));
			node.status = (AsyncTransTaskStatus)q.getIntField(3);
			node.priority = q.getIntField(4);
			node.signature.algorithm = q.getIntField(5);
			node.signature.signature = q.getStringField(6);
			node.signature.blockSignature = q.getStringField(7);
			blob.setEncoded((unsigned char*)q.fieldValue(8));
			node.blocks.blockNum = *(uint32_t*)blob.getBinary();
			if (0 != node.blocks.blockNum)
			{
				node.blocks.blocks = new AsyncTransTaskBlock[node.blocks.blockNum];
				memcpy(node.blocks.blocks, (unsigned char*)blob.getBinary()+sizeof(uint32_t), node.blocks.blockNum*sizeof(AsyncTransTaskBlock));
			}			
			node.userDefine = Utility::String::utf8_to_wstring(q.getStringField(9));
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNodes(const AsyncTaskType type, AsyncTransTaskNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			CppSQLite3Binary blob;
			sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s=%d", 
				TRANS_TASK_ROW_ID, 
				TRANS_TASK_ROW_GROUP, 
				TRANS_TASK_ROW_PARENT, 
				TRANS_TASK_ROW_OWNERID, 
				TRANS_TASK_ROW_NAME, 
				TRANS_TASK_ROW_STATUS, 
				TRANS_TASK_ROW_PRIORITY, 
				TRANS_TASK_ROW_ALGORITHM,
				TRANS_TASK_ROW_SIGNATURE,
				TRANS_TASK_ROW_BLOCK_SIGNATURE,
				TRANS_TASK_ROW_BLOCKS, 
				TRANS_TASK_ROW_USER_DEFINE, 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_TYPE, type);
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				AsyncTransTaskNode node;
				node.id.id = Utility::String::utf8_to_wstring(q.getStringField(0));
				node.id.type = type;
				node.id.group = Utility::String::utf8_to_wstring(q.getStringField(1));
				node.parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node.ownerId = Utility::String::utf8_to_wstring(q.getStringField(3));
				node.name = Utility::String::utf8_to_wstring(q.getStringField(4));
				node.status = AsyncTransTaskStatus(q.getIntField(5));
				node.priority = q.getIntField(6);
				node.signature.algorithm = q.getIntField(7);
				node.signature.signature = q.getStringField(8);
				node.signature.blockSignature = q.getStringField(9);
				blob.setEncoded((unsigned char*)q.fieldValue(10));
				node.blocks.blockNum = *(uint32_t*)blob.getBinary();
				/*if (0 != node.blocks.blockNum)
				{
				node.blocks.blocks = new AsyncTransTaskBlock[node.blocks.blockNum];
				memcpy(node.blocks.blocks, (unsigned char*)blob.getBinary()+sizeof(uint32_t), node.blocks.blockNum*sizeof(AsyncTransTaskBlock));
				}*/
				node.userDefine = Utility::String::utf8_to_wstring(q.getStringField(11));
				
				nodes.push_back(node);

				q.nextRow();
			}
			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getTopNode(AsyncTransTaskNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			CppSQLite3Binary blob;
			sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s=%d OR (%s IN (%d,%d) AND %s NOT IN(%d,%d)) ORDER BY %s LIMIT 0, 1", 
				TRANS_TASK_ROW_ID, 
				TRANS_TASK_ROW_GROUP, 
				TRANS_TASK_ROW_PARENT, 
				TRANS_TASK_ROW_OWNERID, 
				TRANS_TASK_ROW_NAME, 
				TRANS_TASK_ROW_TYPE, 
				TRANS_TASK_ROW_STATUS, 
				TRANS_TASK_ROW_PRIORITY, 
				TRANS_TASK_ROW_ALGORITHM,
				TRANS_TASK_ROW_SIGNATURE,
				TRANS_TASK_ROW_BLOCK_SIGNATURE,
				TRANS_TASK_ROW_BLOCKS, 
				TRANS_TASK_ROW_USER_DEFINE, 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_STATUS, ATS_Waiting, 
				TRANS_TASK_ROW_STATUS, ATS_Cancel, ATS_Error, 
				TRANS_TASK_ROW_TYPE, ATT_Upload, ATT_Download, 
				TRANS_TASK_ROW_PRIORITY);
			q = db_.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.id.id = Utility::String::utf8_to_wstring(q.getStringField(0));
			node.id.group = Utility::String::utf8_to_wstring(q.getStringField(1));
			node.parent = Utility::String::utf8_to_wstring(q.getStringField(2));
			node.ownerId = Utility::String::utf8_to_wstring(q.getStringField(3));
			node.name = Utility::String::utf8_to_wstring(q.getStringField(4));
			node.id.type = (AsyncTaskType)q.getIntField(5);
			node.status = (AsyncTransTaskStatus)q.getIntField(6);
			node.priority = q.getIntField(7);
			node.signature.algorithm = q.getIntField(8);
			node.signature.signature = q.getStringField(9);
			node.signature.blockSignature = q.getStringField(10);
			blob.setEncoded((unsigned char*)q.fieldValue(11));
			node.blocks.blockNum = *(uint32_t*)blob.getBinary();
			if (0 != node.blocks.blockNum)
			{
				node.blocks.blocks = new AsyncTransTaskBlock[node.blocks.blockNum];
				memcpy(node.blocks.blocks, (unsigned char*)blob.getBinary()+sizeof(uint32_t), node.blocks.blockNum*sizeof(AsyncTransTaskBlock));
			}
			node.userDefine = Utility::String::utf8_to_wstring(q.getStringField(12));

			// update the priority
			/*sql.format("UPDATE %s SET %s=%s+%d WHERE %s='%s' AND %s='%s' AND %s=%d", 
			TRANS_TASK_TABLE_NAME, 
			TRANS_TASK_ROW_PRIORITY, TRANS_TASK_ROW_PRIORITY, DEFAULT_PRIORITY, 
			TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(node.id.id).c_str(), 
			TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(node.id.group).c_str(), 
			TRANS_TASK_ROW_TYPE, node.id.type);
			db_.execDML(sql);

			node.priority += DEFAULT_PRIORITY;*/

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getRunningTaskEx(const IdList& idList, KeyType keyType, AsyncTaskIds& runningTaskIds)
	{
		if(idList.empty())
		{
			return RT_OK;
		}
		std::string inStr = Sync::getInStrEx(idList);
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s WHERE %s=%d AND %s IN(%s)", 
				TRANS_TASK_ROW_ID,
				TRANS_TASK_TABLE_NAME,  
				TRANS_TASK_ROW_TYPE, keyType,
				TRANS_TASK_ROW_ID, inStr.c_str());
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				AsyncTaskId taskId(SD::Utility::String::utf8_to_wstring(q.getStringField(0)), 
					L"", AsyncTaskType(keyType));
				runningTaskIds.push_back(taskId);

				q.nextRow();
			}
			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getRunningTaskEx(KeyType keyType, AsyncTaskIds& runningTaskIds)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s WHERE %s=%d", 
				TRANS_TASK_ROW_ID,
				TRANS_TASK_TABLE_NAME,  
				TRANS_TASK_ROW_TYPE, keyType);
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				AsyncTaskId taskId(SD::Utility::String::utf8_to_wstring(q.getStringField(0)), 
					L"", AsyncTaskType(keyType));
				runningTaskIds.push_back(taskId);

				q.nextRow();
			}
			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateStatus(const AsyncTaskId& id, const AsyncTransTaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_STATUS, status, 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, id.type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			if(0==iModify)
			{
				return RT_SQLITE_NOEXIST;
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateStatus(const AsyncTaskIds& ids, const AsyncTransTaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for (AsyncTaskIds::const_iterator it = ids.begin(); it != ids.end(); ++it)
			{
				AsyncTaskId id = *it;
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s='%s' AND %s='%s' AND %s=%d", 
					TRANS_TASK_TABLE_NAME, 
					TRANS_TASK_ROW_STATUS, status, 
					TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
					TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
					TRANS_TASK_ROW_TYPE, id.type);
				int32_t iModify = db_.execDML(sql);
				HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateStatus(const AsyncTransTaskStatus oldStatus, const AsyncTransTaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d WHERE %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_STATUS, status, 
				TRANS_TASK_ROW_STATUS, oldStatus);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateStatus(const AsyncTransTaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_STATUS, status);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual bool isExist(const AsyncTaskId& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT 1 FROM %s WHERE %s='%s' AND %s='%s' AND %s=%d LIMIT 0,1", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, id.type);
			q = db_.execQuery(sql);
			return (!q.eof());
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual bool isUploading(const int64_t& id, const int64_t& parent, const std::wstring& name)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s, %s, %s FROM %s WHERE %s=%d AND %s=%d", 
				TRANS_TASK_ROW_USER_DEFINE,
				TRANS_TASK_ROW_PARENT,
				TRANS_TASK_ROW_NAME,
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_TYPE, ATT_Upload,
				TRANS_TASK_ROW_STATUS, ATS_Running);
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				std::wstring userDefine = Utility::String::utf8_to_wstring(q.getStringField(0));
				if(userDefine.empty())
				{
					std::wstring tempName = Utility::String::utf8_to_wstring(q.getStringField(2));
					if((parent==q.getInt64Field(1))&&(name==tempName))
					{
						SERVICE_INFO(MODULE_NAME, RT_OK, "isUploading, name is:%s", SD::Utility::String::wstring_to_string(tempName).c_str())
						return true;
					}
				}
				else
				{
					std::wstring::size_type pos = userDefine.find_last_of(L'/');
					if(id==Utility::String::string_to_type<int64_t>(userDefine.substr(pos+1)))
					{
						SERVICE_INFO(MODULE_NAME, RT_OK, "isUploading, userDefine is:%s", SD::Utility::String::wstring_to_string(userDefine).c_str())
						return true;
					}
				}
				q.nextRow();
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getAutoTransTaskCnt()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT COUNT(1) FROM %s WHERE %s IN(%d,%d,%d) AND %s IN(%d,%d)", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_TYPE, ATT_Upload_Manual, ATT_Download_Manual, ATT_Upload_Attachements,  
				TRANS_TASK_ROW_STATUS, ATS_Waiting, ATS_Running);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return 0;
	}

	virtual int32_t getCount(const AsyncTransTaskStatus status)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT COUNT(1) FROM %s WHERE %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_STATUS, status);
			q = db_.execQuery(sql);
			return q.getIntField(0);
		}
		CATCH_SQLITE_EXCEPTION;
		return 0;
	}

	virtual int32_t updatePriority(const AsyncTaskId& id, bool inc = false)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s%s%d WHERE %s='%s' AND %s='%s' AND %s=%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_PRIORITY, TRANS_TASK_ROW_PRIORITY, 
				(inc?"-":"+"), PRIORITY_INCREMENT, 
				TRANS_TASK_ROW_ID, CppSQLiteUtility::formatSqlStr(id.id).c_str(), 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(id.group).c_str(), 
				TRANS_TASK_ROW_TYPE, id.type);
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual bool isExist(const std::wstring& groupId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT 1 FROM %s WHERE %s='%s' AND %s<>%d", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(groupId).c_str(), 
				TRANS_TASK_ROW_STATUS, ATS_Complete);
			q = db_.execQuery(sql);
			return (!q.eof());
		}
		CATCH_SQLITE_EXCEPTION;
		return false;
	}

	virtual int32_t getNodes(const std::wstring & groupId, AsyncTransTaskNodes& nodes)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			CppSQLite3Binary blob;
			sql.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s WHERE %s='%s'", 
				TRANS_TASK_ROW_ID, 
				TRANS_TASK_ROW_TYPE, 
				TRANS_TASK_ROW_PARENT, 
				TRANS_TASK_ROW_NAME, 
				TRANS_TASK_ROW_STATUS, 
				TRANS_TASK_ROW_PRIORITY, 
				TRANS_TASK_ROW_ALGORITHM,
				TRANS_TASK_ROW_SIGNATURE,
				TRANS_TASK_ROW_BLOCK_SIGNATURE,
				TRANS_TASK_ROW_BLOCKS, 
				TRANS_TASK_ROW_USER_DEFINE, 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(groupId).c_str());
			q = db_.execQuery(sql);
			while (!q.eof())
			{
				AsyncTransTaskNode node;
				node.id.id = Utility::String::utf8_to_wstring(q.getStringField(0));
				node.id.type = (AsyncTaskType)q.getIntField(1);
				node.id.group = groupId;
				node.parent = Utility::String::utf8_to_wstring(q.getStringField(2));
				node.name = Utility::String::utf8_to_wstring(q.getStringField(3));
				node.status = AsyncTransTaskStatus(q.getIntField(4));
				node.priority = q.getIntField(5);
				node.signature.algorithm = q.getIntField(6);
				node.signature.signature = q.getStringField(7);
				node.signature.blockSignature = q.getStringField(8);
				blob.setEncoded((unsigned char*)q.fieldValue(9));
				node.blocks.blockNum = *(uint32_t*)blob.getBinary();
				/*if (0 != node.blocks.blockNum)
				{
				node.blocks.blocks = new AsyncTransTaskBlock[node.blocks.blockNum];
				memcpy(node.blocks.blocks, (unsigned char*)blob.getBinary()+sizeof(uint32_t), node.blocks.blockNum*sizeof(AsyncTransTaskBlock));
				}*/
				node.userDefine = Utility::String::utf8_to_wstring(q.getStringField(10));
				
				nodes.push_back(node);

				q.nextRow();
			}
			
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t deleteNodes(const std::wstring & groupId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE %s='%s'", 
				TRANS_TASK_TABLE_NAME, 
				TRANS_TASK_ROW_GROUP, CppSQLiteUtility::formatSqlStr(groupId).c_str());
			int32_t iModify = db_.execDML(sql);
			HSLOG_TRACE(MODULE_NAME, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	void createTransTaskTable(const std::wstring& parent)
	{
		try
		{
			std::wstring path = parent + SQLITE_TASKINFO_TABLE;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TRANS_TASK_TABLE_NAME))
			{
				CppSQLite3Buffer sql;
				(void)sql.format("CREATE TABLE %s (\
									%s VARCHAR NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER NOT NULL DEFAULT %d,\
									%s INTEGER,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s BLOB NULL,\
									%s VARCHAR);", 
									TRANS_TASK_TABLE_NAME, 
									TRANS_TASK_ROW_ID, 
									TRANS_TASK_ROW_GROUP, 
									TRANS_TASK_ROW_PARENT,
									TRANS_TASK_ROW_OWNERID,
									TRANS_TASK_ROW_NAME, 
									TRANS_TASK_ROW_TYPE, Key_Invalid, 
									TRANS_TASK_ROW_STATUS, ATS_Waiting, 
									TRANS_TASK_ROW_PRIORITY, DEFAULT_PRIORITY, 
									TRANS_TASK_ROW_ALGORITHM,
									TRANS_TASK_ROW_SIGNATURE,
									TRANS_TASK_ROW_BLOCK_SIGNATURE,
									TRANS_TASK_ROW_BLOCKS, 
									TRANS_TASK_ROW_USER_DEFINE);
				(void)db_.execDML(sql);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"transtasktable_idx1", TRANS_TASK_TABLE_NAME, TRANS_TASK_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

private:
	boost::mutex mutex_;
	CppSQLite3DB db_;
};

std::auto_ptr<TransTaskTable> TransTaskTable::create(const std::wstring& parent)
{
	return std::auto_ptr<TransTaskTable>(
		static_cast<TransTaskTable*>(new TransTaskTableImpl(parent)));
}