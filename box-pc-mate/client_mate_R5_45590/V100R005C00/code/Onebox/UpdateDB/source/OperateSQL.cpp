#include "OperateSQL.h"
#include "Utility.h"
#include "CommonDefine.h"
#include "UpdateDBCommon.h"
#include "Configure.h"
#include "ConfigureMgr.h"
#include "UserInfoMgr.h"
#include "UserContext.h"
#include "UserContextMgr.h"
#include "CppSQLite3.h"
#include "UpdateGeneralDefine.h"
#include <tchar.h>
#include <map>

#ifndef MODULE_NAME
#define MODULE_NAME ("OperateSQLMgr")
#endif

struct SQLInfo
{
	std::wstring PathType;
	std::wstring DBName;
	std::wstring SQL;
	SQLInfo():DBName(L""), SQL(L"")
	{

	}
};

#define SPLIT_SIGN (L"\" \"")
#define SPLIT_TAIL_HEAD (L"\"")

class OperateSQLImpl
{
public:
	OperateSQLImpl(UserContext* usercontext)
		: m_usercontext(usercontext)
	{

	}

public:
	bool runCommand(std::wstring userID, std::wstring param)
	{
		SQLInfo sqlInfo;
		parseParam(param, sqlInfo);
		initDBFilePath(userID, sqlInfo);
		if (RT_OK==runSQL(sqlInfo))
		{
			return true;
		}
		return false;
	}

private:
	void initDBFilePath(const std::wstring& userID, SQLInfo& sqlInfo)
	{
		std::wstring userdataPath = m_usercontext->getConfigureMgr()->getConfigure()->appUserDataPath();
		std::wstring workPath = SD::Utility::FS::get_work_directory();		
		std::wstring userPath = SD::Utility::String::replace_all(sqlInfo.DBName, USER_ID, userID);

		userPath = SD::Utility::String::replace_all(userPath, USERDATA_PATH, userdataPath);
		userPath = SD::Utility::String::replace_all(userPath, INSTALL_PATH, workPath);

		sqlInfo.DBName = userPath;
	}

	void parseParam(const std::wstring& param, SQLInfo& sqlInfo)
	{
		if ( param.empty() )
		{
			return;
		}
		std::vector<std::wstring> params;

		SD::Utility::String::split(param, params, SPLIT_SIGN);

		for ( uint32_t i=0; i<params.size(); i++)
		{
			if ( 0 == i )
			{
				sqlInfo.DBName = params[i];
				if ( 0 == sqlInfo.DBName.find_first_of(SPLIT_TAIL_HEAD))
				{
					sqlInfo.DBName = sqlInfo.DBName.substr(1);
				}
			}
			else
			{
				 sqlInfo.SQL = params[i];

				 if ( (sqlInfo.SQL.length() - 1) == sqlInfo.SQL.find_last_of(SPLIT_TAIL_HEAD) )
				 {
					 sqlInfo.SQL = sqlInfo.SQL.substr(0, sqlInfo.SQL.length() - 1);
				 }
			}
		}

		SERVICE_INFO(MODULE_NAME, RT_OK, "parseParam DBName:%s."
			, SD::Utility::String::wstring_to_string(sqlInfo.DBName).c_str());
		SERVICE_INFO(MODULE_NAME, RT_OK, "parseParam DBName:%s."
			, SD::Utility::String::wstring_to_string(sqlInfo.SQL).c_str());
	}

	int32_t runSQL(const SQLInfo& sqlInfo)
	{
		if ( sqlInfo.DBName.empty() || sqlInfo.SQL.empty() )
		{
			SERVICE_ERROR(MODULE_NAME,RT_INVALID_PARAM,"DBName or SQL is empty.");
			return RT_INVALID_PARAM;
		}

		try
		{
			if (!SD::Utility::FS::is_exist(sqlInfo.DBName))
			{
				return RT_OK;
			}
			CppSQLite3DB updateInfodb;
			CppSQLite3Buffer sql;
			CppSQLite3Query q;

			updateInfodb.open(SD::Utility::String::wstring_to_string(sqlInfo.DBName).c_str());
			(void)sql.format(SD::Utility::String::wstring_to_string(sqlInfo.SQL).c_str());
			(void)updateInfodb.execDML(sql);

			updateInfodb.close();
			return RT_OK;
		}
		catch (CppSQLite3Exception& e)
		{
			std::string errorMsg = e.errorMessage();
			if (std::string::npos != errorMsg.find("no such table"))
			{
				SERVICE_INFO(MODULE_NAME, RT_OK,
					"SQLite DML ErrorCode: %d ErrorMessage: %s ",
					e.errorCode(), e.errorMessage());
				// db file is not exist or the table is not exist
				// return OK directly
				return RT_OK;
			}
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"SQLite DML ErrorCode: %d ErrorMessage: %s ",
				e.errorCode(), e.errorMessage());
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,
				"the InsertSingle function occur unknown exception", NULL);
		}
		return RT_SQLITE_ERROR;
	}
private:
	UserContext* m_usercontext;
};

OperateSQLMgr::OperateSQLMgr( UserContext* usercontext )
{
	m_impl = new OperateSQLImpl(usercontext);
}

OperateSQLMgr::~OperateSQLMgr()
{
	if ( m_impl )
	{
		delete m_impl;
		m_impl = NULL;
	}
}

bool OperateSQLMgr::runCommand( std::wstring userID, std::wstring param )
{
	bool bret = false;

	if ( m_impl )
	{
		bret = m_impl->runCommand(userID, param);
	}

	return bret;
}
