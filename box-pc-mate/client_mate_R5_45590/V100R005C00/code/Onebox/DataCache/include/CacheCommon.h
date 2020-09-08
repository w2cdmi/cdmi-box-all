#ifndef _ONEBOX_CACHE_COMMON_H_
#define _ONEBOX_CACHE_COMMON_H_

#ifdef _DEBUG
#pragma comment(lib, "sqlite3_d.lib")
#else
#pragma comment(lib, "sqlite3.lib")
#endif

#define CATCH_SQLITE_EXCEPTION							\
	catch (CppSQLite3Exception& e)						\
	{													\
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		\
					"SQLite DML ErrorCode: %d ErrorMessage: %s ",	\
					e.errorCode(), e.errorMessage());	\
	}													\
	catch(...)											\
	{													\
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		\
					"the InsertSingle function occur unknown exception", NULL); \
	}

enum NotifyPageType
{
	Page_MyFile,
	Page_Share2Me,
	Page_MyShare,
	Page_TeamSpace,
	Page_Transfers,
	Page_Backup,
	Page_Other
};

#endif