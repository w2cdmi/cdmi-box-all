#include "BackupAllLocalDb.h"
#include "Utility.h"
#include "BackupAllUtility.h"
#include <boost/thread.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllLocalDb")
#endif

#define BT_LOCAL_TABLE_NAME ("tb_LocalInfo")
#define BT_LOCAL_ROW_LOCAL_ID ("local_id")
#define BT_LOCAL_ROW_LOCAL_PARENT ("local_parent")
#define BT_LOCAL_ROW_TYPE ("type")
#define BT_LOCAL_ROW_MTIME ("mtime")
#define BT_LOCAL_ROW_SIZE ("size")
#define BT_LOCAL_ROW_PATH ("path")		//文件夹时为路径，文件时为文件名
#define BT_LOCAL_ROW_REMOTE_ID ("remote_id")
#define BT_LOCAL_ROW_ERROR_CODE ("error_code")
#define BT_LOCAL_ROW_OP_TYPE ("op_type")

#define BT_SCANNING_TABLE_NAME ("tb_ScanningInfo")
#define BT_SCANNING_ROW_LOCAL_ID ("local_id")

#define BT_PSTINFO_TABLE_NAME ("tb_PstUploadInfo")
#define BT_PSTINFO_ROW_LOCAL_ID ("local_id")
#define BT_PSTINFO_ROW_UPLOAD_TIME ("upload_time")

#define BT_INCINFO_TABLE_NAME ("tb_IncInfo")
#define BT_INCINFO_ROW_CNT ("cnt")
#define BT_INCINFO_ROW_SIZE ("size")
#define BT_INCINFO_ROW_STATUS ("status")

#define BT_CHECKINFO_TABLE_NAME ("tb_CheckExistInfo")
#define BT_CHECKINFO_ROW_PATH ("path")

#define INVALID_CNT -1000000		//足够大的负值，避免在短时间内被加为正数

class BackupAllLocalDbImpl : public BackupAllLocalDb
{
public:
	BackupAllLocalDbImpl(const std::wstring& path, int32_t uploadFilterPeriod)
	{
		uploadFilterPeriod_ = 60*uploadFilterPeriod;

		createTable(path);

		uploadingCnt_ = INVALID_CNT;
		totalSize_ = INVALID_CNT;
		totalCnt_ = INVALID_CNT;
		leftSize_ = INVALID_CNT;
		leftCnt_ = INVALID_CNT;
		failedCnt_ = INVALID_CNT;

		incCnt_ = 0;
		incSize_ = 0;
		loadIncInfo();

		scanStatus_ = getScanStatus();
	}

	virtual ~BackupAllLocalDbImpl()
	{
		updateIncInfo();
	}

	virtual int32_t addRootNode(const BATaskLocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			CppSQLite3Buffer sql;
			(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,%d,%lld,%lld,'%s',%d,%lld,%d)", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_ERROR_CODE,
				node.baseInfo.localId,
				node.baseInfo.localParent,
				node.baseInfo.type,
				node.baseInfo.mtime,
				node.baseInfo.size,
				CppSQLiteUtility::formatSqlStr(node.baseInfo.path).c_str(),
				node.baseInfo.opType,
				node.remoteId,
				node.errorCode);
			(void) db_.execDML(sql);

			if(-1!=node.baseInfo.localId)
			{
				CppSQLite3Buffer sql2;
				(void)sql2.format("INSERT INTO %s(%s) VALUES(%lld)", 
					BT_SCANNING_TABLE_NAME,
					BT_SCANNING_ROW_LOCAL_ID,
					node.baseInfo.localId);
				(void) db_.execDML(sql2);
			}
			db_.commitTransaction();

			if(0==(node.baseInfo.opType&BAO_Filter))
			{
				++totalCnt_;
				++incCnt_;
				++leftCnt_;
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t addNode(const BATaskBaseNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,%d,%lld,%lld,'%s',%d)", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_OP_TYPE,
				node.localId,
				node.localParent,
				node.type,
				node.mtime,
				node.size,
				CppSQLiteUtility::formatSqlStr(node.path).c_str(),
				node.opType);
			(void) db_.execDML(sql);
			if(0==(node.opType&BAO_Filter))
			{
				++totalCnt_;
				++incCnt_;
				++leftCnt_;
				if(node.size>0)
				{
					totalSize_ += node.size;
					incSize_ += node.size;
					leftSize_ += node.size;
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t addNodes(BATaskBaseNodeList& nodes, const std::set<int64_t>& scanningDirs)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(BATaskBaseNodeList::iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				CppSQLite3Buffer bufSQL1;
				(void)bufSQL1.format("SELECT %s, %s, %s, %s, %s, %s, %s, rowid FROM %s WHERE %s=%lld AND (%s&%d)=%d LIMIT 0,1",
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_ROW_LOCAL_PARENT,
					BT_LOCAL_ROW_TYPE,
					BT_LOCAL_ROW_MTIME,
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_ROW_REMOTE_ID,
					BT_LOCAL_ROW_OP_TYPE,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_LOCAL_ID, it->localId,
					BT_LOCAL_ROW_OP_TYPE, BAO_Delete, BAO_Delete);
				CppSQLite3Query qSet1 = db_.execQuery(bufSQL1);
				if(qSet1.eof())
				{
					it->opType = (FILE_TYPE_DIR==it->type)?BAO_Create:BAO_Upload;
					//新增节点直接插入
					CppSQLite3Buffer sql;
					(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,%d,%lld,%lld,'%s',%d)", 
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_LOCAL_ID,
						BT_LOCAL_ROW_LOCAL_PARENT,
						BT_LOCAL_ROW_TYPE,
						BT_LOCAL_ROW_MTIME,
						BT_LOCAL_ROW_SIZE,
						BT_LOCAL_ROW_PATH,
						BT_LOCAL_ROW_OP_TYPE,
						it->localId,
						it->localParent,
						it->type,
						it->mtime,
						it->size,
						CppSQLiteUtility::formatSqlStr(it->path).c_str(),
						it->opType);
					(void)db_.execDML(sql);
					++totalCnt_;
					++incCnt_;
					++leftCnt_;
					if(it->size>0)
					{
						totalSize_ += it->size;
						incSize_ += it->size;
						leftSize_ += it->size;
					}
				}
				else
				{
					BATaskLocalNode oldNode;
					oldNode.baseInfo.localId = qSet1.getInt64Field(0);
					oldNode.baseInfo.localParent = qSet1.getInt64Field(1);
					oldNode.baseInfo.type = qSet1.getIntField(2);
					oldNode.baseInfo.mtime = qSet1.getInt64Field(3);
					oldNode.baseInfo.path = SD::Utility::String::utf8_to_wstring(qSet1.getStringField(4));
					oldNode.remoteId = qSet1.getInt64Field(5);
					oldNode.baseInfo.opType = qSet1.getIntField(6);
					//已存在的节点判断是否变更，并设置opType
					if(setOpType(oldNode, *it))
					{
						//已变更节点插入变更后的信息
						CppSQLite3Buffer sql;
						(void)sql.format("INSERT INTO %s(%s,%s,%s,%s,%s,%s,%s,%s) VALUES(%lld,%lld,%d,%lld,%lld,'%s',%d,%lld)", 
							BT_LOCAL_TABLE_NAME,
							BT_LOCAL_ROW_LOCAL_ID,
							BT_LOCAL_ROW_LOCAL_PARENT,
							BT_LOCAL_ROW_TYPE,
							BT_LOCAL_ROW_MTIME,
							BT_LOCAL_ROW_SIZE,
							BT_LOCAL_ROW_PATH,
							BT_LOCAL_ROW_OP_TYPE,
							BT_LOCAL_ROW_REMOTE_ID,
							it->localId,
							it->localParent,
							it->type,
							it->mtime,
							it->size,
							CppSQLiteUtility::formatSqlStr(it->path).c_str(),
							it->opType,
							oldNode.remoteId);
						(void)db_.execDML(sql);
						//删除原节点
						CppSQLite3Buffer delSql;
						(void)delSql.format("DELETE FROM %s WHERE rowid=%lld",  
							BT_LOCAL_TABLE_NAME,
							qSet1.getInt64Field(7));
						(void)db_.execDML(delSql);
						++totalCnt_;
						++incCnt_;
						++leftCnt_;
						if(it->size>0)
						{
							totalSize_ += it->size;
							incSize_ += it->size;
							leftSize_ += it->size;
						}
					}
					else
					{
						//未变更节点标记为命中
						CppSQLite3Buffer sql;
						(void)sql.format("UPDATE %s SET %s=%s&%d WHERE rowid=%lld",  
							BT_LOCAL_TABLE_NAME,
							BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, ~BAO_Delete,
							qSet1.getInt64Field(7));
						(void)db_.execDML(sql);
						if(BASS_FullScanning==scanStatus_ || BAO_CheckRemote&oldNode.baseInfo.opType)
						{
							++totalCnt_;
							++incCnt_;
							totalSize_ += it->size;
							incSize_ += it->size;
							if(it->opType >= BAO_Uploading)
							{
								++leftCnt_;
								leftSize_ += it->size;
							}
						}
					}
				}
			}
			SERVICE_DEBUG(diskModuleName_, RT_OK, "replaceNodes. size:%d", nodes.size());

			CppSQLite3Buffer delSql;
			(void)delSql.format("DELETE FROM %s", BT_SCANNING_TABLE_NAME);
			(void)db_.execDML(delSql);

			for(std::set<int64_t>::const_iterator it = scanningDirs.begin();
				it != scanningDirs.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s) VALUES(%lld)", 
					BT_SCANNING_TABLE_NAME,
					BT_SCANNING_ROW_LOCAL_ID,
					*it);
				(void) db_.execDML(sql);
			}

			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t deleteNodes(IdList& deleteList, std::list<std::wstring>& uploadingPath)
	{
		if(deleteList.empty())
		{
			return RT_OK;
		}

		//删除子对象
		std::string idStr = BackupAll::getInStr(deleteList);
		while(!deleteList.empty())
		{
			getChildren(deleteList, idStr);
		}

		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sqlSel;
			CppSQLite3Query q;
			sqlSel.format("SELECT %s FROM %s WHERE %s IN(%s) AND (%s&%d)=%d",
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, idStr.c_str(),
				BT_LOCAL_ROW_OP_TYPE, BAO_Uploading, BAO_Uploading);
			q = db_.execQuery(sqlSel);
			while(!q.eof())
			{
				uploadingPath.push_back(SD::Utility::String::utf8_to_wstring(q.getStringField(0)));
				q.nextRow();
			}

			CppSQLite3Buffer sql;
			(void)sql.format("DELETE FROM %s WHERE %s IN(%s)", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, idStr.c_str());
			int32_t iModify = db_.execDML(sql);
			totalCnt_ = INVALID_CNT;//触发重新统计
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, delete size:%d", iModify, deleteList.size());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;			
	}

	virtual int32_t setCheckNodes(const std::list<std::wstring>& checkList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			//更新自身及所有子文件夹
			for(std::list<std::wstring>::const_iterator it = checkList.begin();
				it != checkList.end(); ++it)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d WHERE %s=%d AND %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_CheckRemote,
					BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
					BT_LOCAL_ROW_PATH, CppSQLiteUtility::formaSqlLikeStr(*it).c_str());
				int32_t iModify = db_.execDML(sql);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}

			//将父节点状态为扫描的文件更新为扫描状态
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%d AND (%s&%d)=%d",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
				BT_LOCAL_ROW_OP_TYPE, BAO_CheckRemote, BAO_CheckRemote);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			std::string inStr;
			while(!qSet.eof())
			{
				inStr = inStr + qSet.getStringField(0) + ",";
				qSet.nextRow();
			}
			if(!inStr.empty())
			{
				inStr = inStr.substr(0, inStr.length()-1);
				CppSQLite3Buffer sql2;
				const char* sqlStr2= sql2.format("UPDATE %s SET %s=%s|%d WHERE %s=%d AND %s IN(%s)", 
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_CheckRemote,
						BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
						BT_LOCAL_ROW_LOCAL_PARENT, inStr.c_str());
				int32_t iModify2 = db_.execDML(sql2);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify2, sqlStr2);
			}
			db_.commitTransaction();
			totalCnt_ = INVALID_CNT;//触发重新统计
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t setAllCheck()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%s|%d", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_CheckRemote);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d", iModify);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateNode(const BATaskBaseNode& newNode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%lld, %s=%lld, %s=%lld, %s='%s', %s=%d WHERE %s=%lld", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_PARENT, newNode.localParent,
				BT_LOCAL_ROW_MTIME, newNode.mtime,
				BT_LOCAL_ROW_SIZE, newNode.size,
				BT_LOCAL_ROW_PATH, CppSQLiteUtility::formatSqlStr(newNode.path).c_str(),
				BT_LOCAL_ROW_OP_TYPE, newNode.opType,
				BT_LOCAL_ROW_LOCAL_ID, newNode.localId);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				totalCnt_ = INVALID_CNT;//触发重新统计
				++incCnt_;
				incSize_ += newNode.size;
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updatePath()
	{
		SERVICE_FUNC_TRACE(diskModuleName_, "updatePath");
		try
		{
			while(true)
			{
				//select a.path, b.path from tb_localInfo a, tb_localInfo b where a.type = 0 and b.type = 0 and a.local_parent = b.local_id and a.path||'\' not like b.path||'\%' limit 0,1
				CppSQLite3Buffer sql;
				(void)sql.format("SELECT a.%s, b.%s FROM %s a, %s b WHERE a.%s=0 AND b.%s=0 AND a.%s = b.%s AND a.%s||'\\' NOT LIKE b.%s||'\\%%' LIMIT 0,1",
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_TYPE,
					BT_LOCAL_ROW_TYPE,
					BT_LOCAL_ROW_LOCAL_PARENT,
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_ROW_PATH);
				CppSQLite3Query qSet = db_.execQuery(sql);
				if(qSet.eof())
				{
					return RT_SQLITE_NOEXIST;
				}
				std::wstring oldPath = SD::Utility::FS::get_parent_path(SD::Utility::String::utf8_to_wstring(qSet.getStringField(0)));
				std::wstring newPath = SD::Utility::String::utf8_to_wstring(qSet.getStringField(1));

				//刷新子对象路径
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer bufSQL;
				//update tb_localInfo set path = replace(path, "old" , "new")  where path||"/" like "old/%"
				const char* sqlStr = bufSQL.format("UPDATE %s SET %s=REPLACE(%s, '%s', '%s') WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
					BT_LOCAL_TABLE_NAME, 
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_ROW_PATH,
					CppSQLiteUtility::formatSqlStr(oldPath).c_str(), 
					CppSQLiteUtility::formatSqlStr(newPath).c_str(), 
					BT_LOCAL_ROW_PATH, 
					CppSQLiteUtility::formaSqlLikeStr(oldPath).c_str());
				int32_t iModify = db_.execDML(bufSQL);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updatePath(const std::wstring& oldPath, const std::wstring& newPath)
	{
		try
		{
			//刷新子对象路径
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufSQL;
			//update tb_localInfo set path = replace(path, "old" , "new")  where path||"/" like "old/%"
			const char* sqlStr = bufSQL.format("UPDATE %s SET %s=REPLACE(%s, '%s', '%s') WHERE %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
				BT_LOCAL_TABLE_NAME, 
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_PATH,
				CppSQLiteUtility::formatSqlStr(oldPath).c_str(), 
				CppSQLiteUtility::formatSqlStr(newPath).c_str(), 
				BT_LOCAL_ROW_PATH, 
				CppSQLiteUtility::formaSqlLikeStr(oldPath).c_str());
			int32_t iModify = db_.execDML(bufSQL);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getBrokenPoint(std::set<int64_t>& scanningDirs, std::set<int64_t>& scannedDirs)
	{
		scanningDirs.clear();
		scannedDirs.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s",
				BT_SCANNING_ROW_LOCAL_ID,
				BT_SCANNING_TABLE_NAME);
			q = db_.execQuery(sql);
			std::stringstream scanningStream;
			while(!q.eof())
			{
				scanningDirs.insert(q.getInt64Field(0));
				scanningStream << q.getInt64Field(0) << ",";
				q.nextRow();
			}
			std::string scanningStr = scanningStream.str();
			if(scanningStr.empty())
			{
				//全部扫描完成时，返回
				return RT_OK;
			}

			//获取已经是扫描完成状态的目录（父为扫描中，自身为扫描完成），父为扫描完成的，无需关注
			scanningStr = scanningStr.substr(0, scanningStr.length()-1);
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%d AND %s IN (%s) AND %s NOT IN (%s)",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
				BT_LOCAL_ROW_LOCAL_PARENT, scanningStr.c_str(),
				BT_LOCAL_ROW_LOCAL_ID, scanningStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			while(!qSet.eof())
			{
				scannedDirs.insert(qSet.getInt64Field(0));
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNextNode(BATaskLocalNode& node)
	{
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=0 AND %s>=%d AND (%s&%d)=0 AND (%s&%d)=0 LIMIT 0,1",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Upload,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Delete|BAO_Move,
				BT_LOCAL_ROW_OP_TYPE, BAO_Uploading);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=0 AND %s>=%d AND (%s&%d)=0 AND (%s&%d)=0 LIMIT 0,1",
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_ROW_LOCAL_PARENT,
					BT_LOCAL_ROW_TYPE,
					BT_LOCAL_ROW_MTIME,
					BT_LOCAL_ROW_SIZE,
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_ROW_REMOTE_ID,
					BT_LOCAL_ROW_OP_TYPE,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_ERROR_CODE,
					BT_LOCAL_ROW_OP_TYPE, BAO_Upload,
					BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Delete,
					BT_LOCAL_ROW_OP_TYPE, BAO_Uploading);
				qSet = db_.execQuery(bufSQL);
				if(qSet.eof())
				{
					return RT_SQLITE_NOEXIST;
				}
			}

			node.baseInfo.localId = qSet.getInt64Field(0);
			node.baseInfo.localParent = qSet.getInt64Field(1);
			node.baseInfo.type = qSet.getIntField(2);
			node.baseInfo.mtime = qSet.getInt64Field(3);
			node.baseInfo.size = qSet.getInt64Field(4);
			node.baseInfo.path = SD::Utility::String::utf8_to_wstring(qSet.getStringField(5));
			node.remoteId = qSet.getInt64Field(6);
			node.baseInfo.opType = qSet.getIntField(7);
			node.errorCode = RT_OK;

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getNodeById(const int64_t& localId, BATaskLocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=%lld LIMIT 0,1",
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.errorCode = qSet.getIntField(0);
			node.baseInfo.localId = localId;
			node.baseInfo.localParent = qSet.getInt64Field(1);
			node.baseInfo.type = qSet.getIntField(2);
			node.baseInfo.mtime = qSet.getInt64Field(3);
			node.baseInfo.size = qSet.getInt64Field(4);
			node.baseInfo.path = SD::Utility::String::utf8_to_wstring(qSet.getStringField(5));
			node.remoteId = qSet.getInt64Field(6);
			node.baseInfo.opType = qSet.getIntField(7);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getNodeByPath(const std::wstring& path, BATaskLocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s='%s' LIMIT 0,1",
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_PATH, CppSQLiteUtility::formatSqlStr(path).c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}
			node.errorCode = qSet.getIntField(0);
			node.baseInfo.localId = qSet.getInt64Field(5);
			node.baseInfo.localParent = qSet.getInt64Field(1);
			node.baseInfo.type = qSet.getIntField(2);
			node.baseInfo.mtime = qSet.getInt64Field(3);
			node.baseInfo.size = qSet.getInt64Field(4);
			node.baseInfo.path = path;
			node.remoteId = qSet.getInt64Field(6);
			node.baseInfo.opType = qSet.getIntField(7);

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getNodeByNode(const BATaskBaseNode& newNode, BATaskLocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=%lld",
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, newNode.localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			if(qSet.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			BATaskLocalNodeList nodes;
			while(!qSet.eof())
			{
				BATaskLocalNode tempNode;
				tempNode.errorCode = qSet.getIntField(0);
				tempNode.baseInfo.localId = newNode.localId;
				tempNode.baseInfo.localParent = qSet.getInt64Field(1);
				tempNode.baseInfo.type = qSet.getIntField(2);
				tempNode.baseInfo.mtime = qSet.getInt64Field(3);
				tempNode.baseInfo.size = qSet.getInt64Field(4);
				tempNode.baseInfo.path = SD::Utility::String::utf8_to_wstring(qSet.getStringField(5));
				tempNode.remoteId = qSet.getInt64Field(6);
				tempNode.baseInfo.opType = qSet.getIntField(7);
				//处理文件硬链接场景，优先匹配路径完全一致的文件
				if(tempNode.baseInfo.localParent==newNode.localParent && tempNode.baseInfo.path==newNode.path)
				{
					node = tempNode;
					return RT_OK;
				}
				nodes.push_back(tempNode);
				qSet.nextRow();
			}

			for(BATaskLocalNodeList::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
			{
				//处理文件硬链接场景，次优先匹配父路径一致的文件
				if(it->baseInfo.localParent==newNode.localParent)
				{
					node = *it;
					return RT_OK;
				}
			}

			node = nodes.front();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getExistNodes(BATaskExistFileInfo& existFiles, BATaskExistDirInfo& existDirs)
	{
		SERVICE_FUNC_TRACE(diskModuleName_, "getExistNodes");
		existFiles.clear();
		existDirs.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL1;
			(void)bufSQL1.format("SELECT %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %d AND (%s&%d)=%d",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_MTIME,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Delete, BAO_Delete);
			CppSQLite3Query qSet1 = db_.execQuery(bufSQL1);
			while(!qSet1.eof())
			{
				boost::this_thread::interruption_point();
				BATaskExistFile existNode;
				if(-1==qSet1.getInt64Field(1))
				{
					existNode.opType = BAO_Create;
				}
				else
				{
					existNode.opType = qSet1.getIntField(2);
				}
				existNode.localParent = qSet1.getInt64Field(3);
				existNode.mtime = qSet1.getInt64Field(4);
				existNode.name = SD::Utility::String::utf8_to_wstring(qSet1.getStringField(5));
				existFiles.insert(std::make_pair(qSet1.getInt64Field(0), existNode));
				qSet1.nextRow();
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = %d AND (%s&%d)=%d",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
				BT_LOCAL_ROW_OP_TYPE, BAO_Delete, BAO_Delete);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				boost::this_thread::interruption_point();
				BATaskExistDir existNode;
				existNode.opType = qSet.getIntField(1);
				existNode.localParent = qSet.getInt64Field(2);
				existNode.name = SD::Utility::FS::get_file_name(SD::Utility::String::utf8_to_wstring(qSet.getStringField(3)));
				existDirs.insert(std::make_pair(qSet.getInt64Field(0), existNode));
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual std::wstring getPathById(const int64_t& localId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%lld LIMIT 0,1",
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return SD::Utility::String::utf8_to_wstring(qSet.getStringField(0));
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return L"";
	}

	virtual int64_t getRemoteIdById(const int64_t& localId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%lld LIMIT 0,1",
				BT_LOCAL_ROW_REMOTE_ID,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return qSet.getInt64Field(0);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return -1;
	}

	virtual int32_t getSubNodes(const int64_t& parentId, BATaskBaseNodeList& nodes, bool& noDir)
	{
		noDir = true;
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s FROM %s WHERE %s=%lld ORDER BY %s DESC, %s collate TREE ASC",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_PARENT, parentId,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_PATH);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				BATaskBaseNode node;
				node.localId = qSet.getInt64Field(0);
				node.path = SD::Utility::String::utf8_to_wstring(qSet.getStringField(1));
				node.type = qSet.getIntField(2);
				if(FILE_TYPE_DIR == node.type)
				{
					noDir = false;
				}
				nodes.push_back(node);
				qSet.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getSubFiles(const int64_t& parentId, std::map<std::wstring, int64_t>& subFiles)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s FROM %s WHERE %s=0 AND %s=%lld AND %s=%d",
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_LOCAL_PARENT, parentId,
				BT_LOCAL_ROW_OP_TYPE, BAO_Upload);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				subFiles.insert(std::make_pair(SD::Utility::String::utf8_to_wstring(qSet.getStringField(0)), qSet.getInt64Field(1)));
				qSet.nextRow();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t changeSubFilesOp(const int64_t& parentId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%d WHERE %s=%lld AND %s=%d AND (%s&%d)=%d", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BAO_Upload,
				BT_LOCAL_ROW_LOCAL_PARENT, parentId,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
				BT_LOCAL_ROW_OP_TYPE, BAO_CheckRemote, BAO_CheckRemote);
			int32_t iModify = db_.execDML(sql);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t getFailedNodes(BATaskLocalNodeList& failedList, int32_t offset, int32_t limit)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::stringstream limitStr;
			if(-1!=limit)
			{
				limitStr << "LIMIT " << offset << ", " << limit;
			}
			std::set<int64_t> parents;
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s NOT IN(%d,%d,%d) AND (%s&%d)=0 %s",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_ROW_LOCAL_PARENT,
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE, RT_OK, RT_DIFF_FILTER, RT_PARENT_NOEXIST_ERROR,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter,
				limitStr.str().c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				BATaskLocalNode node;
				node.baseInfo.localId = qSet.getInt64Field(0);
				node.baseInfo.path = SD::Utility::String::utf8_to_wstring(qSet.getStringField(1));
				node.baseInfo.type = qSet.getIntField(2);
				node.baseInfo.size = qSet.getInt64Field(3);
				node.baseInfo.localParent = qSet.getInt64Field(4);
				node.baseInfo.opType = qSet.getIntField(5);
				node.errorCode = qSet.getIntField(6);
				failedList.push_back(node);
				if(FILE_TYPE_FILE == node.baseInfo.type)
				{
					parents.insert(node.baseInfo.localParent);
				}
				qSet.nextRow();
			}

			if(!parents.empty())
			{
				std::map<int64_t, std::wstring> pathInfo;
				std::stringstream parentStr;
				std::set<int64_t>::const_iterator it = parents.begin();
				parentStr<<*it;
				++it;
				for(; it != parents.end(); ++it)
				{
					parentStr<<",";
					parentStr<<*it;
				}
				
				CppSQLite3Buffer bufParent;
				(void)bufParent.format("SELECT %s, %s FROM %s WHERE %s IN(%s)",
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_ROW_PATH,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_LOCAL_ID, parentStr.str().c_str());
				CppSQLite3Query qParentSet = db_.execQuery(bufParent);

				while(!qParentSet.eof())
				{
					pathInfo.insert(std::make_pair(qParentSet.getInt64Field(0), SD::Utility::String::utf8_to_wstring(qParentSet.getStringField(1))));
					qParentSet.nextRow();
				}

				for(BATaskLocalNodeList::iterator it = failedList.begin(); it != failedList.end(); ++it)
				{
					if(FILE_TYPE_FILE == it->baseInfo.type)
					{
						std::map<int64_t, std::wstring>::const_iterator itP = pathInfo.find(it->baseInfo.localParent);
						if(pathInfo.end()!=itP)
						{
							it->baseInfo.path = itP->second + PATH_DELIMITER + it->baseInfo.path;
						}
					}
				}
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateParentError(const int64_t& parentId, int32_t errorCode)
	{
		boost::mutex::scoped_lock lock(mutex_);
		IdList ids;
		ids.push_back(parentId);
		std::string idStr = SD::Utility::String::type_to_string<std::string>(parentId);
		size_t parentLen = idStr.length()+1;
		while(!ids.empty())
		{
			getChildren(ids, idStr);
		}
		if(idStr.length()<=parentLen)
		{
			return RT_OK;
		}
		idStr = idStr.substr(parentLen, idStr.length()-parentLen);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%d WHERE %s IN(%s)", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE, errorCode,
				BT_LOCAL_ROW_LOCAL_ID, idStr.c_str());
			(void)db_.execDML(sql);
			totalCnt_ = INVALID_CNT;//触发重新统计
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t resumeError(bool hasErrorTasks, bool isFirstTime)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			if(hasErrorTasks)
			{
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d&(~%d), %s=0 WHERE %s NOT IN(%d, %d) AND (%s&%d)=%d",  
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Uploading, BAO_Upload,
					BT_LOCAL_ROW_ERROR_CODE,
					BT_LOCAL_ROW_ERROR_CODE, RT_OK, RT_PARENT_NOEXIST_ERROR,
					BT_LOCAL_ROW_OP_TYPE, BAO_Upload, BAO_Upload);
				int32_t iModify = db_.execDML(sql);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s, uploadingCnt:%d", iModify, sqlStr, uploadingCnt_);
				uploadingCnt_ = -1;
			}

			if(isFirstTime)
			{
				CppSQLite3Buffer sql1;
				const char* sqlStr1 = sql1.format("UPDATE %s SET %s=0 WHERE %s<>0 AND %s>=%d", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_ERROR_CODE,
					BT_LOCAL_ROW_ERROR_CODE,
					BT_LOCAL_ROW_OP_TYPE, BAO_Upload);
				int32_t iModify1 = db_.execDML(sql1);
				totalCnt_ = INVALID_CNT;//触发重新统计
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify1, sqlStr1);
			}
			else
			{
				CppSQLite3Buffer sql2;
				const char* sqlStr2 = sql2.format("UPDATE %s SET %s=0 WHERE %s NOT IN(%d,%d) AND %s>=%d", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_ERROR_CODE,
					BT_LOCAL_ROW_ERROR_CODE, RT_OK, RT_DIFF_FILTER,
					BT_LOCAL_ROW_OP_TYPE, BAO_Upload);
				int32_t iModify2 = db_.execDML(sql2);
				totalCnt_ = INVALID_CNT;//触发重新统计
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify2, sqlStr2);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t reUploadErrorPath()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=0 WHERE %s IN(2,3)", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_ERROR_CODE);
			int32_t iModify = db_.execDML(sql);
			if(iModify>0)
			{
				totalCnt_ = INVALID_CNT;//触发重新统计
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t updateStatus(const std::map<std::wstring, int32_t>& pathInfo)
	{
		//SERVICE_FUNC_TRACE(diskModuleName_, "updateStatus");
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			//从父到子更新文件夹
			for(std::map<std::wstring, int32_t>::const_iterator it = pathInfo.begin();
				it != pathInfo.end(); ++it)
			{
				CppSQLite3Buffer sql;
				if(BAP_Filter==it->second)
				{
					const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d WHERE %s=%d AND %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAP_Filter,
						BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
						BT_LOCAL_ROW_PATH, CppSQLiteUtility::formaSqlLikeStr(it->first).c_str());
					int32_t iModify = db_.execDML(sql);
					SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				}
				else
				{
					const char* sqlStr = sql.format("UPDATE %s SET %s=%s&%d WHERE %s=%d AND %s||'\\' LIKE '%s\\%%' ESCAPE '/'", 
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, ~BAP_Filter,
						BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
						BT_LOCAL_ROW_PATH, CppSQLiteUtility::formaSqlLikeStr(it->first).c_str());
					int32_t iModify = db_.execDML(sql);
					SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				}
			}
			
			//更新所有文件节点为过滤状态
			CppSQLite3Buffer sql1;
			const char* sqlStr1 = sql1.format("UPDATE %s SET %s=%s|%d WHERE %s=%d", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAP_Filter,
					BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE);
			int32_t iModify1 = db_.execDML(sql1);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify1, sqlStr1);

			//将父节点状态为非过滤的文件更新为非过滤状态
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%d AND (%s&%d)=0",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_DIR,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			std::string inStr;
			while(!qSet.eof())
			{
				inStr = inStr + qSet.getStringField(0) + ",";
				qSet.nextRow();
			}
			if(!inStr.empty())
			{
				inStr = inStr.substr(0, inStr.length()-1);
				CppSQLite3Buffer sql2;
				const char* sqlStr2= sql2.format("UPDATE %s SET %s=%s&%d WHERE %s=%d AND %s IN(%s)", 
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, ~BAP_Filter,
						BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
						BT_LOCAL_ROW_LOCAL_PARENT, inStr.c_str());
				int32_t iModify2 = db_.execDML(sql2);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify2, sqlStr2);
			}
			db_.commitTransaction();
			totalCnt_ = INVALID_CNT;//触发重新统计
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getFilterUploading(std::list<std::wstring>& filterList)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			//查询需要取消上传的文件
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT a.%s, b.%s FROM %s a, %s b WHERE a.%s=%d AND (a.%s&%d)=%d AND a.%s=b.%s",
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Uploading, BAO_Filter|BAO_Uploading,
				BT_LOCAL_ROW_LOCAL_PARENT, BT_LOCAL_ROW_LOCAL_ID);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);
			while(!qSet.eof())
			{
				filterList.push_back(Utility::String::utf8_to_wstring(qSet.getStringField(1)) 
					+ PATH_DELIMITER + Utility::String::utf8_to_wstring(qSet.getStringField(0)));
				qSet.nextRow();
			}
			
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d&(~%d) WHERE (%s&%d)=%d",  
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Upload, BAO_Uploading,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Uploading, BAO_Filter|BAO_Uploading);
			int32_t iModify = db_.execDML(sql);
			uploadingCnt_ = uploadingCnt_ - iModify;
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s, uploadingCnt:%d", iModify, sqlStr, uploadingCnt_);

			//查询需要取消上传的失败传输任务
			CppSQLite3Buffer bufFailedSQL;
			(void)bufFailedSQL.format("SELECT a.%s, b.%s FROM %s a, %s b WHERE a.%s=%d AND a.%s<>0 AND (a.%s&%d)=%d AND a.%s=b.%s",
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_ROW_PATH,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_TYPE, FILE_TYPE_FILE,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Upload, BAO_Filter|BAO_Upload,
				BT_LOCAL_ROW_LOCAL_PARENT, BT_LOCAL_ROW_LOCAL_ID);
			CppSQLite3Query qFailedSet = db_.execQuery(bufFailedSQL);
			while(!qFailedSet.eof())
			{
				filterList.push_back(Utility::String::utf8_to_wstring(qFailedSet.getStringField(1)) 
					+ PATH_DELIMITER + Utility::String::utf8_to_wstring(qFailedSet.getStringField(0)));
				qFailedSet.nextRow();
			}
			/*
			CppSQLite3Buffer sqlFailed;
			const char* sqlFailedStr = sqlFailed.format("UPDATE %s SET %s=0 WHERE %s<>0 AND (%s&%d)=%d",  
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Upload, BAO_Filter|BAO_Upload);
			int32_t iFailedModify = db_.execDML(sqlFailed);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iFailedModify, sqlFailedStr);
			*/
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t getUploadingCnt()
	{
		if(uploadingCnt_<0)
		{
			initUploadingCnt();
		}
		return uploadingCnt_;
	}

	virtual int32_t updateSubFilesOp(const int64_t& parentId, const std::map<std::wstring, int64_t>& subFiles)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(std::map<std::wstring, int64_t>::const_iterator it = subFiles.begin();
				it != subFiles.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("UPDATE %s SET %s=%d WHERE %s=%lld AND %s='%s'", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BAO_Uploading,
					BT_LOCAL_ROW_LOCAL_PARENT, parentId,
					BT_LOCAL_ROW_PATH, CppSQLiteUtility::formatSqlStr(it->first).c_str());
				(void) db_.execDML(sql);
			}
			db_.commitTransaction();
			uploadingCnt_ += subFiles.size();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t updateExInfo(const BATaskLocalNode& node)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			(void)sql.format("UPDATE %s SET %s=%lld, %s=%d, %s=%d WHERE %s=%lld", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_REMOTE_ID, node.remoteId,
				BT_LOCAL_ROW_ERROR_CODE, node.errorCode,
				BT_LOCAL_ROW_OP_TYPE, node.baseInfo.opType,
				BT_LOCAL_ROW_LOCAL_ID, node.baseInfo.localId);
			int32_t iModify = db_.execDML(sql);
			if(0==iModify) return RT_OK;
			if(node.baseInfo.opType&BAO_Uploading)
			{
				++uploadingCnt_;
			}

			if(0==node.baseInfo.opType||RT_OK!=node.errorCode)
			{
				--leftCnt_;
				if(node.baseInfo.size>0)
				{
					leftSize_ = leftSize_ - node.baseInfo.size;
				}
			}
			if(!(RT_OK==node.errorCode||RT_DIFF_FILTER==node.errorCode||RT_PARENT_NOEXIST_ERROR==node.errorCode))
			{
				++failedCnt_;
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t resumeUpload()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d&(~%d) WHERE (%s&%d)=%d",  
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Upload, BAO_Uploading,
				BT_LOCAL_ROW_OP_TYPE, BAO_Uploading, BAO_Uploading);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s, uploadingCnt:%d", iModify, sqlStr, uploadingCnt_);
			uploadingCnt_ = 0;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t markExist(bool isSetMark)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			if(isSetMark)
			{
				const char* sqlStr = sql.format("UPDATE %s SET %s=%s|%d WHERE %s<>0",  
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Delete,
					BT_LOCAL_ROW_LOCAL_PARENT);
				int32_t iModify = db_.execDML(sql);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			else
			{
				const char* sqlStr = sql.format("UPDATE %s SET %s=%s&%d WHERE (%s&%d)=%d",  
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, ~BAO_Delete,
					BT_LOCAL_ROW_OP_TYPE, BAO_Delete, BAO_Delete);
				int32_t iModify = db_.execDML(sql);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			}
			totalCnt_ = INVALID_CNT;//触发重新统计
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;		
	}

	virtual int32_t deleteMarkNodes()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("DELETE FROM %s WHERE (%s&%d)=%d AND (%s&%d)=0", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BAO_Delete, BAO_Delete,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter);
			int32_t iModify = db_.execDML(sql);
			totalCnt_ = INVALID_CNT;//触发重新统计
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t ignoreError(const int64_t& localId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s=%s|%d WHERE %s=%lld", 
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE, RT_OK,
				BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Filter,
				BT_LOCAL_ROW_LOCAL_ID, localId);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			failedCnt_ = failedCnt_ - iModify;
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual int64_t getRowId(const int64_t& remoteParent, const std::wstring& filename, bool isSuccess, int64_t& localId)
	{
		int64_t rowId = -1;
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			int64_t localParent;
			CppSQLite3Buffer sqlParent;
			(void)sqlParent.format("SELECT %s FROM %s WHERE %s=%lld LIMIT 0,1", 
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_REMOTE_ID, remoteParent);
			CppSQLite3Query qSetParent = db_.execQuery(sqlParent);
			if(qSetParent.eof())
			{
				return rowId;
			}
			localParent = qSetParent.getInt64Field(0);

			CppSQLite3Buffer sql;
			if(isSuccess)
			{
				(void)sql.format("SELECT ROWID, %s FROM %s WHERE %s=%lld AND %s='%s'", 
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_LOCAL_PARENT, localParent,
					BT_LOCAL_ROW_PATH, CppSQLiteUtility::formatSqlStr(filename).c_str());
			}
			else
			{
				//失败任务不刷新过滤节点
				(void)sql.format("SELECT ROWID, %s FROM %s WHERE %s=%lld AND (%s&%d)=0 AND %s='%s'", 
					BT_LOCAL_ROW_LOCAL_ID,
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_LOCAL_PARENT, localParent,
					BT_LOCAL_ROW_OP_TYPE, BAO_Filter,
					BT_LOCAL_ROW_PATH, CppSQLiteUtility::formatSqlStr(filename).c_str());			
			}
			CppSQLite3Query qSet = db_.execQuery(sql);
			while(!qSet.eof())
			{
				if(rowId > 0)
				{
					//删除同路径多余的记录
					CppSQLite3Buffer delSql;
					const char* sqlStr = delSql.format("DELETE FROM %s WHERE ROWID=%lld", 
						BT_LOCAL_TABLE_NAME, rowId);
					int32_t iModify = db_.execDML(delSql);
					SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				}
				rowId = qSet.getInt64Field(0);
				localId = qSet.getInt64Field(1);
				qSet.nextRow();
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return rowId;
	}

	virtual int32_t updateUploadFailed(const std::map<int64_t, int32_t>& failedInfo, int64_t failedSize)
	{
		if(failedInfo.empty())
		{
			return RT_OK;
		}
		for(std::map<int64_t, int32_t>::const_iterator itF = failedInfo.begin(); itF != failedInfo.end(); ++itF)
		{
			if(!(RT_OK==itF->second||RT_DIFF_FILTER==itF->second||RT_PARENT_NOEXIST_ERROR==itF->second))
			{
				++failedCnt_;
			}
		}
		leftCnt_ = leftCnt_ - failedInfo.size();
		leftSize_ = leftSize_ - failedSize;
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(std::map<int64_t, int32_t>::const_iterator it = failedInfo.begin();
				it != failedInfo.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("UPDATE %s SET %s=%d, %s=%s|%d&(~%d) WHERE ROWID=%lld",  
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_ERROR_CODE, it->second,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Upload, BAO_Uploading,
					it->first);
				(void)db_.execDML(sql);
				--uploadingCnt_;
			}
			db_.commitTransaction();
			//SERVICE_DEBUG(diskModuleName_, RT_OK, "failedInfo size:%d, uploadingCnt_:%d", failedInfo.size(), uploadingCnt_);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}

	virtual int32_t updateUploadInfo(const std::map<int64_t, int64_t>& successInfo, int64_t successSize)
	{
		if(successInfo.empty())
		{
			return RT_OK;
		}
		leftCnt_ = leftCnt_ - successInfo.size();
		leftSize_ = leftSize_ - successSize;
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			for(std::map<int64_t, int64_t>::const_iterator it = successInfo.begin();
				it != successInfo.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("UPDATE %s SET %s=%lld, %s=%d, %s=%s&(~%d) WHERE ROWID=%lld", 
					BT_LOCAL_TABLE_NAME,
					BT_LOCAL_ROW_REMOTE_ID, it->second,
					BT_LOCAL_ROW_ERROR_CODE, RT_OK,
					BT_LOCAL_ROW_OP_TYPE, BT_LOCAL_ROW_OP_TYPE, BAO_Uploading,
					it->first);
				(void)db_.execDML(sql);
				--uploadingCnt_;
			}
			db_.commitTransaction();
			//SERVICE_DEBUG(diskModuleName_, RT_OK, "successInfo size:%d, uploadingCnt_:%d", successInfo.size(), uploadingCnt_);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getTotalInfo(BATaskInfo& taskInfo, bool flush = false)
	{
		if (flush||totalCnt_<=0||totalSize_<0||leftCnt_<=0||leftSize_<0||failedCnt_<0
			||totalCnt_<leftCnt_||totalCnt_<failedCnt_||totalSize_<leftSize_)
		{
			initTotalInfo();
		}
		taskInfo.totalSize = totalSize_;
		taskInfo.totalCnt = totalCnt_;
		taskInfo.leftSize = leftSize_;
		taskInfo.leftCnt = leftCnt_;
		taskInfo.failedCnt = failedCnt_;
		if(leftCnt_>incCnt_)
		{
			incCnt_ = leftCnt_;
			incSize_ = leftSize_;
		}
		taskInfo.curCnt = incCnt_;
		taskInfo.curSize = incSize_;
		if(totalCnt_>=0 && totalCnt_<taskInfo.curCnt)
		{
			taskInfo.curCnt = totalCnt_;
		}
		if(totalSize_<taskInfo.curSize)
		{
			taskInfo.curSize = totalSize_;
		}
		return RT_OK;
	}

	virtual bool isFilter(const int64_t& id)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%lld",
				BT_LOCAL_ROW_OP_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_ID, id);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return qSet.getIntField(0)&BAO_Filter;
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return true;
	}

	virtual int32_t updatePstTime(const int64_t& localId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("REPLACE INTO %s(%s,%s) VALUES(%lld,%lld)", 
				BT_PSTINFO_TABLE_NAME,
				BT_PSTINFO_ROW_LOCAL_ID,
				BT_PSTINFO_ROW_UPLOAD_TIME,
				localId,
				time(NULL));
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual bool isNeedSkip(const int64_t& localId)
	{
		//boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s WHERE %s=%lld",
				BT_PSTINFO_ROW_UPLOAD_TIME,
				BT_PSTINFO_TABLE_NAME,
				BT_PSTINFO_ROW_LOCAL_ID, localId);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				//7*24*60=604800
				return qSet.getInt64Field(0) + uploadFilterPeriod_ >time(NULL);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return false;	
	}

	virtual int32_t updateScanStatus(BAScanStatus status)
	{
		try
		{
			{
				boost::mutex::scoped_lock lock(mutex_);
				CppSQLite3Buffer sql;
				const char* sqlStr = sql.format("UPDATE %s SET %s=%d", 
					BT_INCINFO_TABLE_NAME,
					BT_INCINFO_ROW_STATUS, status);
				int32_t iModify = db_.execDML(sql);
				SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
				scanStatus_ = status;
				if(BASS_Scanning==status)
				{
					//增量扫描时继承上次未完成任务
					CppSQLite3Buffer bufLeftSQL;
					(void)bufLeftSQL.format("SELECT COUNT(1), SUM(%s) FROM %s WHERE %s>=%d AND (%s&%d)=0 AND %s<>%d",
						BT_LOCAL_ROW_SIZE,
						BT_LOCAL_TABLE_NAME,
						BT_LOCAL_ROW_OP_TYPE, BAO_Uploading,
						BT_LOCAL_ROW_OP_TYPE, BAO_Filter,
						BT_LOCAL_ROW_ERROR_CODE, RT_DIFF_FILTER);
					CppSQLite3Query qLeftSet = db_.execQuery(bufLeftSQL);
					if(!qLeftSet.eof())
					{
						incCnt_ = qLeftSet.getIntField(0);
						incSize_ = qLeftSet.getInt64Field(1);
					}
				}
			}
			if(BASS_Init!=status)
			{
				updateIncInfo();
			}

			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	virtual BAScanStatus getScanStatus()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s FROM %s", BT_INCINFO_ROW_STATUS, BT_INCINFO_TABLE_NAME);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				return BAScanStatus(qSet.getIntField(0));
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return BASS_Init;		
	}

	virtual int32_t loadIncInfoByTotal()
	{
		initTotalInfo();
		incCnt_ = totalCnt_;
		incSize_ = totalSize_;
		return RT_OK;
	}

	virtual int32_t updateIncInfo()
	{
		if(0==incCnt_)
		{
			loadIncInfo();
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=%d, %s=%lld", 
				BT_INCINFO_TABLE_NAME,
				BT_INCINFO_ROW_CNT, incCnt_,
				BT_INCINFO_ROW_SIZE, incSize_);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;			
	}

	virtual int32_t initIncInfo()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			incCnt_ = 0;
			incSize_ = 0;
			CppSQLite3Buffer sql;
			const char* sqlStr = sql.format("UPDATE %s SET %s=0, %s=0", 
				BT_INCINFO_TABLE_NAME,
				BT_INCINFO_ROW_CNT,
				BT_INCINFO_ROW_SIZE);
			int32_t iModify = db_.execDML(sql);
			SERVICE_DEBUG(diskModuleName_, RT_OK, "iModify:%d, sqlStr:%s", iModify, sqlStr);
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual int32_t addCheckPath(const std::list<std::wstring>& paths)
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			db_.beginTransaction();
			CppSQLite3Buffer delSql;
			(void)delSql.format("DELETE FROM %s", BT_CHECKINFO_TABLE_NAME);
			(void)db_.execDML(delSql);

			for(std::list<std::wstring>::const_iterator it = paths.begin();
				it != paths.end(); ++it)
			{
				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s) VALUES('%s')", 
					BT_CHECKINFO_TABLE_NAME,
					BT_CHECKINFO_ROW_PATH,
					CppSQLiteUtility::formatSqlStr(*it).c_str());
				(void) db_.execDML(sql);
			}
			db_.commitTransaction();
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;	
	}

	virtual int32_t getCheckPath(std::list<std::wstring>& paths)
	{
		paths.clear();
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer sql;
			CppSQLite3Query q;
			sql.format("SELECT %s FROM %s",
				BT_CHECKINFO_ROW_PATH,
				BT_CHECKINFO_TABLE_NAME);
			q = db_.execQuery(sql);
			while(!q.eof())
			{
				paths.push_back(SD::Utility::String::utf8_to_wstring(q.getStringField(0)));
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	void createTable(const std::wstring& path)
	{
		try
		{
			//parentPath_ + L"local_" + disk + L".db";
			diskModuleName_ = MODULE_NAME;
			if(path.length() > 5)
			{
				diskModuleName_ += SD::Utility::String::wstring_to_string(path.substr(path.length() - 5, 2));
			}
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(BT_LOCAL_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER DEFAULT %d,\
									%s INTEGER DEFAULT %d);", 
									BT_LOCAL_TABLE_NAME,
									BT_LOCAL_ROW_LOCAL_ID,
									BT_LOCAL_ROW_LOCAL_PARENT,
									BT_LOCAL_ROW_SIZE,
									BT_LOCAL_ROW_TYPE,
									BT_LOCAL_ROW_PATH,
									BT_LOCAL_ROW_MTIME,
									BT_LOCAL_ROW_OP_TYPE,
									BT_LOCAL_ROW_REMOTE_ID, -1,
									BT_LOCAL_ROW_ERROR_CODE, 0);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_local_idx1", BT_LOCAL_TABLE_NAME, BT_LOCAL_ROW_LOCAL_ID);
				(void)db_.execDML(bufSQLIdx1);

				CppSQLite3Buffer bufSQLIdx2;
				(void)bufSQLIdx2.format("CREATE INDEX %s ON %s(%s);", 
					"tb_local_idx2", BT_LOCAL_TABLE_NAME, BT_LOCAL_ROW_LOCAL_PARENT);
				(void)db_.execDML(bufSQLIdx2);

				CppSQLite3Buffer bufSQLIdx3;
				(void)bufSQLIdx3.format("CREATE INDEX %s ON %s(%s);", 
					"tb_local_idx3", BT_LOCAL_TABLE_NAME, BT_LOCAL_ROW_REMOTE_ID);
				(void)db_.execDML(bufSQLIdx3);

				CppSQLite3Buffer bufSQLIdx4;
				(void)bufSQLIdx4.format("CREATE INDEX %s ON %s(%s);", 
					"tb_local_idx4", BT_LOCAL_TABLE_NAME, BT_LOCAL_ROW_PATH);
				(void)db_.execDML(bufSQLIdx4);
			}

			if(!db_.tableExists(BT_SCANNING_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (%s INTEGER NOT NULL);", 
									BT_SCANNING_TABLE_NAME,
									BT_SCANNING_ROW_LOCAL_ID);
				(void)db_.execDML(bufSQL);
			}

			if(!db_.tableExists(BT_PSTINFO_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL);", 
									BT_PSTINFO_TABLE_NAME,
									BT_PSTINFO_ROW_LOCAL_ID,
									BT_PSTINFO_ROW_UPLOAD_TIME);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_upload_idx1", BT_PSTINFO_TABLE_NAME, BT_PSTINFO_ROW_LOCAL_ID);
				(void)db_.execDML(bufSQLIdx1);
			}

			if(!db_.tableExists(BT_INCINFO_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL,\
									%s INTEGER NOT NULL);", 
									BT_INCINFO_TABLE_NAME,
									BT_INCINFO_ROW_CNT,
									BT_INCINFO_ROW_SIZE,
									BT_INCINFO_ROW_STATUS);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer sql;
				(void)sql.format("INSERT INTO %s(%s,%s,%s) VALUES(0,0,%d)", 
					BT_INCINFO_TABLE_NAME,
					BT_INCINFO_ROW_CNT,
					BT_INCINFO_ROW_SIZE,
					BT_INCINFO_ROW_STATUS,
					BASS_Init);
				(void)db_.execDML(sql);
			}

			if(!db_.tableExists(BT_CHECKINFO_TABLE_NAME))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (%s VARCHAR NOT NULL);", 
									BT_CHECKINFO_TABLE_NAME,
									BT_CHECKINFO_ROW_PATH);
				(void)db_.execDML(bufSQL);
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	int32_t initUploadingCnt()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT COUNT(1) FROM %s WHERE (%s&%d)=%d",
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BAO_Uploading&BAO_Filter, BAO_Uploading);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				uploadingCnt_ = qSet.getIntField(0);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	int32_t initTotalInfo()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufLeftSQL;
			(void)bufLeftSQL.format("SELECT COUNT(1), SUM(%s) FROM %s WHERE %s=0 AND %s>=%d AND (%s&%d)=0",
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE,
				BT_LOCAL_ROW_OP_TYPE, BAO_Uploading,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Delete);
			CppSQLite3Query qLeftSet = db_.execQuery(bufLeftSQL);

			if(!qLeftSet.eof())
			{
				leftCnt_ = qLeftSet.getIntField(0);
				leftSize_ = qLeftSet.getInt64Field(1);
			}

			CppSQLite3Buffer failedSQL;
			(void)failedSQL.format("SELECT COUNT(1) FROM %s WHERE %s NOT IN(%d,%d,%d) AND (%s&%d)=0",
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_ERROR_CODE, RT_OK, RT_DIFF_FILTER, RT_PARENT_NOEXIST_ERROR,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Delete);
			CppSQLite3Query qFailedSet = db_.execQuery(failedSQL);

			if(!qFailedSet.eof())
			{
				failedCnt_ = qFailedSet.getIntField(0);
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT COUNT(1), SUM(%s) FROM %s WHERE (%s&%d)=0 AND %s<>%d",
				BT_LOCAL_ROW_SIZE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_OP_TYPE, BAO_Filter|BAO_Delete,
				BT_LOCAL_ROW_ERROR_CODE, RT_DIFF_FILTER);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				totalCnt_ = qSet.getIntField(0);
				totalSize_ = qSet.getInt64Field(1);
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;	
	}

	void getChildren(IdList& ids, std::string& idStr)
	{
		if(ids.empty())
		{
			return;
		}
		std::string inStr = BackupAll::getInStr(ids);
		ids.clear();
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s FROM %s WHERE %s IN(%s)",
				BT_LOCAL_ROW_LOCAL_ID,
				BT_LOCAL_ROW_TYPE,
				BT_LOCAL_TABLE_NAME,
				BT_LOCAL_ROW_LOCAL_PARENT, inStr.c_str());
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				idStr = idStr + "," + qSet.getStringField(0);
				if(FILE_TYPE_DIR==qSet.getIntField(1))
				{
					ids.push_back(qSet.getInt64Field(0));
				}
				qSet.nextRow();
			}
		}
		CATCH_SQLITE_EXCEPTION;	
	}

	int32_t loadIncInfo()
	{
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s FROM %s", 
				BT_INCINFO_ROW_CNT, 
				BT_INCINFO_ROW_SIZE, 
				BT_INCINFO_TABLE_NAME);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			if(!qSet.eof())
			{
				incCnt_ = qSet.getIntField(0);
				incSize_ = qSet.getInt64Field(1);
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	bool setOpType(const BATaskLocalNode& oldNode, BATaskBaseNode& existNode)
	{
		if(existNode.localParent==oldNode.baseInfo.localParent
			&&existNode.path==oldNode.baseInfo.path
			&&existNode.mtime==oldNode.baseInfo.mtime)
		{
			existNode.opType = oldNode.baseInfo.opType&(~BAO_Filter)&(~BAO_Delete);
			return false;
		}

		if(-1==oldNode.remoteId)
		{
			existNode.opType = (FILE_TYPE_DIR==existNode.type)?BAO_Create:BAO_Upload;
			return true;
		}
		existNode.opType = oldNode.baseInfo.opType&(~BAO_Filter)&(~BAO_Delete);
		if(oldNode.baseInfo.localParent!=existNode.localParent)
		{
			existNode.opType = existNode.opType|BAO_Move;
		}
		
		if(FILE_TYPE_DIR==existNode.type)
		{
			if(SD::Utility::FS::get_file_name(oldNode.baseInfo.path)!=SD::Utility::FS::get_file_name(existNode.path))
			{
				existNode.opType = existNode.opType|BAO_Rename;
			}
		}
		else
		{
			if(oldNode.baseInfo.path!=existNode.path)
			{
				existNode.opType = existNode.opType|BAO_Rename;
			}

			if(oldNode.baseInfo.mtime!=existNode.mtime)
			{
				std::wstring extName = SD::Utility::FS::get_extension_name(existNode.path);
				if((L"pst"==extName||L"ost"==extName||L"nsf"==extName)&&isNeedSkip(existNode.localId))
				{
					HSLOG_ERROR(MODULE_NAME, RT_OK, "%s skip upload.", SD::Utility::String::wstring_to_string(existNode.path).c_str());
					//需要跳过时，不改变文件节点的mtime
					if(oldNode.baseInfo.localParent==existNode.localParent&&oldNode.baseInfo.path==existNode.path)
					{
						return false;
					}
					else
					{
						existNode.mtime = oldNode.baseInfo.mtime;
					}
				}
				else
				{
					existNode.opType = existNode.opType|BAO_Upload;
				}
			}
		}
		return true;	
	}

private:
	int32_t uploadFilterPeriod_;
	boost::mutex mutex_;
	CppSQLite3DB db_;
	std::string diskModuleName_;
	int32_t uploadingCnt_;
	int64_t totalSize_;
	int32_t totalCnt_;
	int64_t leftSize_;
	int32_t leftCnt_;
	int32_t failedCnt_;
	int64_t incSize_;
	int32_t incCnt_;
	BAScanStatus scanStatus_;
};

BackupAllLocalDb* BackupAllLocalDb::create(const std::wstring& path, int32_t uploadFilterPeriod)
{
	return static_cast<BackupAllLocalDb*>(new BackupAllLocalDbImpl(path, uploadFilterPeriod));
}