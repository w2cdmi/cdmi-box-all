#ifndef _COMMON_DEFINE_H_
#define _COMMON_DEFINE_H_

#include <stdint.h>
#include <xstring>
#include <string>
#include <tchar.h>
#include <memory>
#include "ErrorCode.h"
#ifndef DISABLE_LOG
#include <ISSPLog4cpp_md.h>
#endif

#ifndef FS_MAX_PATH
#define FS_MAX_PATH 248
#endif

#ifndef PATH_DELIMITER
#define PATH_DELIMITER L"\\"
#endif

#ifndef PATH_DELIMITER_STR
#define PATH_DELIMITER_STR "\\"
#endif

#ifndef CONF_PATH
#define CONF_PATH (L"./Config.ini")
#endif

#define INVALID_ID int64_t(-1)
#define INVALID_VALUE int64_t(-1)
#define INVALID_TIME int64_t(-1)

#ifndef UNUSED_ARG
#define UNUSED_ARG(arg) ((void)arg)
#endif

#ifndef SHARE_DRIVE_FILE_SYSTEM_MONITOR_STOP_EVENT
#define SHARE_DRIVE_FILE_SYSTEM_MONITOR_STOP_EVENT (L"DD2D3CB4-F71F-4FDC-9530-6A70CE9471EA")
#endif

#ifndef SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT
#define SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT (L"9A5FCF8C-6316-4101-AD83-35CF1DE9D526")
#endif

#ifndef SHARE_DRIVE_UI_STOP_EVENT
#define SHARE_DRIVE_UI_STOP_EVENT (L"73830486-BA33-48EE-810B-069DB6868B49")
#endif

#ifndef THRIFT_PORT_PATH
#ifdef _WIN64
#define THRIFT_PORT_PATH (L"Wow6432Node\\CLSID\\{2DB5975A-C37B-4BB3-88CE-1F91E82A09D5}")
#else
#define THRIFT_PORT_PATH (L"CLSID\\{2DB5975A-C37B-4BB3-88CE-1F91E82A09D5}")
#endif
#endif

#ifndef THRIFT_PORT_NAME
#define THRIFT_PORT_NAME (L"Port")
#endif

enum FILE_TYPE
{
	FILE_TYPE_DIR,
	FILE_TYPE_FILE
};

struct FileSignature
{
	enum Algorithm
	{
		SHA1,
		MD5,
		Invalid
	};

	int32_t algorithm;
	std::string signature;
	std::string blockSignature;

	FileSignature()
		:algorithm(Invalid)
		,signature("")
		,blockSignature(""){}

	FileSignature(const FileSignature& rhs)
	{
		algorithm = rhs.algorithm;
		signature = rhs.signature;
		blockSignature = rhs.blockSignature;
	}

	bool operator==(const FileSignature& rhs) const
	{
		return (algorithm == rhs.algorithm && signature == rhs.signature);
	}

	bool valid() const
	{
		return ((SHA1 == algorithm || MD5 == algorithm) && 
			!signature.empty());
	}
};

#ifndef DISABLE_LOG
class TraceObject
{
public:
	explicit TraceObject(const std::string& strMod, const std::string& strMsg)
		: m_strMod(strMod), m_strMsg(strMsg)
	{
		SERVICE_INFO(m_strMod, RT_OK, "%s Entered...", m_strMsg.c_str());
	}

	~TraceObject() 
	{
		try
		{
			SERVICE_INFO(m_strMod, RT_OK, "%s Exited...", m_strMsg.c_str());
		}
		catch(...)
		{
		}
	}

private:
	std::string m_strMod;
	std::string m_strMsg;
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

#endif
