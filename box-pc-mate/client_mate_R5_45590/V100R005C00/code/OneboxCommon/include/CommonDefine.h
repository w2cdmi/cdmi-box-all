#ifndef _COMMON_DEFINE_H_
#define _COMMON_DEFINE_H_

#include <stdint.h>
#include <xstring>
#include <string>
#include <sstream>
#include <tchar.h>
#include <memory>
#include "OneboxExport.h"
#include "ErrorCode.h"
#ifndef DISABLE_LOG
#include <ISSPLog4cpp_md.h>
#endif

#ifdef __cplusplus
extern "C"
{
#endif

/* 华为特有安全函数 */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#ifndef FS_MAX_PATH
#define FS_MAX_PATH 248
#endif

#ifndef PATH_DELIMITER
#define PATH_DELIMITER L"\\"
#endif

#ifndef PATH_DELIMITER_STR
#define PATH_DELIMITER_STR "\\"
#endif

#define SYNCDATA_DIR (L"SyncData")
#define SYNCDATA_TABLE (L"syncdata.db")

#ifndef CONF_PATH
#define CONF_PATH (L"./Config.ini")
#endif

#define DEFAULT_CONFIG_NAME (L"Config.ini")

#define INVALID_ID int64_t(-1)
#define INVALID_VALUE int64_t(-1)
#define INVALID_TIME int64_t(-1)

#ifndef UNUSED_ARG
#define UNUSED_ARG(arg) ((void)arg)
#endif

#ifndef ONEBOX_INSTANCE_EVENT_ID
#define ONEBOX_INSTANCE_EVENT_ID (L"D70AFFD6-67D3-457B-B027-9A9D66636830")
#endif

#ifndef THRIFT_PORT_PATH
#define THRIFT_PORT_PATH (L"CLSID\\{C14D663B-61B2-4FF2-B6B1-097F2C7CD45D}")
#endif

#ifndef THRIFT_PORT_NAME
#define THRIFT_PORT_NAME (L"Port")
#endif

#ifndef ONEBOX_REG_PATH
#ifdef _WIN64
#define ONEBOX_REG_PATH (L"SOFTWARE\\Wow6432Node\\Huawei\\OneboxApp\\Onebox\\Setting")
#else
#define ONEBOX_REG_PATH (L"SOFTWARE\\Huawei\\OneboxApp\\Onebox\\Setting")
#endif
#endif

#define ONEBOX_REGHKCU_PATH (L"SOFTWARE\\Huawei\\OneboxApp\\Onebox\\Setting")

#ifndef ONEBOXSYNC_REG_PATH
#ifdef _WIN64
#define ONEBOXSYNC_REG_PATH (L"SOFTWARE\\Wow6432Node\\Huawei\\Onebox\\Setting")
#else
#define ONEBOXSYNC_REG_PATH (L"SOFTWARE\\Huawei\\Onebox\\Setting")
#endif
#endif

#ifndef ONEBOX_REG_PATH_KEY
#define ONEBOX_REG_PATH_KEY (L"AppPath")
#endif

#ifndef ONEBOX_APP_DIR
#define ONEBOX_APP_DIR (L"Onebox_Mate")
#define ONEBOX_APP_NAME (L"Onebox Mate")
#define ONEBOX_APP_NAME_EXT (L"Onebox Mate.exe")
#endif

#ifndef BATCH_PREUPLOAD_FILE_SIZE
#define BATCH_PREUPLOAD_FILE_SIZE int64_t(1024 * 1024 * 100)
#endif

enum FILE_TYPE
{
	FILE_TYPE_DIR,
	FILE_TYPE_FILE
};

enum FILE_EXTRA_TYPE
{
	FOLDER = 0,
	COMPUTER = 1,
	DISK = 2
};

struct Fingerprint
{
	enum Algorithm
	{
		SHA1,
		MD5,
		Invalid
	};

	int32_t algorithm;
	std::string fingerprint;
	std::string blockFingerprint;

	Fingerprint()
		:algorithm(Invalid)
		,fingerprint("")
		,blockFingerprint(""){}

	Fingerprint(const Fingerprint& rhs)
	{
		algorithm = rhs.algorithm;
		fingerprint = rhs.fingerprint;
		blockFingerprint = rhs.blockFingerprint;
	}

	bool operator==(const Fingerprint& rhs) const
	{
		return (algorithm == rhs.algorithm && fingerprint == rhs.fingerprint);
	}

	bool valid() const
	{
		return ((SHA1 == algorithm || MD5 == algorithm) && 
			!fingerprint.empty());
	}
};

#ifndef DISABLE_LOG
class TraceObject
{
public:
	explicit TraceObject(const std::string& strMod, const std::string& strMsg)
		: m_strMod(strMod), m_strMsg(strMsg)
	{
		startTime = GetTickCount();
		SERVICE_INFO(m_strMod, RT_OK, "%s Entered", m_strMsg.c_str());
	}

	~TraceObject() 
	{
		try
		{
			SERVICE_INFO(m_strMod, RT_OK, "%s Exited | %d ms", m_strMsg.c_str(), GetTickCount()-startTime);
		}
		catch(...)
		{
		}
	}

private:
	std::string m_strMod;
	std::string m_strMsg;
	uint32_t startTime;
}; 

#define SERVICE_FUNC_TRACE(strMod, strMsg) TraceObject traceObj_(strMod, strMsg);
#endif

#define RETRY(times) for(int32_t i = 0; i < times; ++i)

#define FUNC_DEFAULT_SET_GET(TYPENAME, PARANAME)		\
	TYPENAME const & PARANAME(void) const				\
	{                                                   \
		return PARANAME##_;                             \
	}                                                   \
	void PARANAME(TYPENAME _##PARANAME)					\
	{                                                   \
		PARANAME##_ = _##PARANAME;                      \
	}

#define CATCH_SQLITE_EXCEPTION							            \
	catch (CppSQLite3Exception& e)						            \
	{													            \
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		            \
		"SQLite DML ErrorCode: %d ErrorMessage: %s ",	            \
		e.errorCode(), e.errorMessage());	                        \
	}													            \
	catch(...)											            \
	{													            \
		SERVICE_ERROR(MODULE_NAME, RT_SQLITE_ERROR,		            \
		"the InsertSingle function occur unknown exception", NULL); \
	}

#define CHECK_RESULT(FUNCNAME)					\
	ret = FUNCNAME;								\
	if(RT_OK != ret)							\
	{											\
		return ret;								\
	}

class PrintObj
{
public:
	PrintObj()
	{
		len_ = 0;
	}

	virtual ~PrintObj(void)
	{
	}

	template<typename T>
	void addField(const T& value)
	{
		tempStr_ << value << "-";
	}

	template<typename T>
	void lastField(const T& value)
	{
		tempStr_ << value << ",";
		if((len_ + tempStr_.str().length()) > 200)
		{
			debugStr_<<"\n";
			len_ = tempStr_.str().length();
			debugStr_<<tempStr_.str();
		}
		else
		{
			len_ += tempStr_.str().length();
			debugStr_<<tempStr_.str();
		}
		tempStr_.str("");
	}

	std::string getMsg()
	{
		return debugStr_.str();
	}

	static std::auto_ptr<PrintObj> create()
	{
		return std::auto_ptr<PrintObj>(new PrintObj());
	}

private:
	std::stringstream debugStr_;
	std::stringstream tempStr_;
	int32_t len_;
};

#define UNLIMIT_PAGE_SIZE (-1)
struct Page
{
	int32_t start;
	int32_t offset;

	Page() : start(0), offset(UNLIMIT_PAGE_SIZE) {}
};
#define PAGE_OBJ (Page())

#define INIT_REF_VAR(var) var=rhs.var

enum PriorityType
{
	PRIORITY_LEVEL0 = 0x0000,
	PRIORITY_LEVEL1 = 0x0001,	//right upload
	PRIORITY_LEVEL2 = 0x0002,
	PRIORITY_LEVEL3 = 0x0003,	//setNoSync + delete
	DEFAULT_PRIORITY = 0x0004,	//oper
	PRIORITY_LEVEL5 = 0x0005,	//scan
	PRIORITY_LEVEL6 = 0x0006,	//backup
	PRIORITY_LEVEL7 = 0x0007,	//hidden
	PRIORITY_INCREMENT = 0x0008
};

#define ROOT_PARENTID (0)
#define ROOT_PARENTID_STR "0"
#define FS_MAX_PATH_W (32768)

#ifndef SLEEP
#define SLEEP(x) boost::this_thread::sleep(x)
#endif

#endif
