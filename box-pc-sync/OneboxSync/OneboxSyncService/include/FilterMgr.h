#ifndef _ONEBOX_FILTER_MGR_H_
#define _ONEBOX_FILTER_MGR_H_

#include "CommonDefine.h"
#include "UserContext.h"

enum FILTER_TYPE
{
	FILTER_PREFIX, 
	FILTER_SUFFIX, 
	FILTER_KEYWORD, 
	FILTER_MATCH, 
	FILTER_FILENAME, 
	FILTER_FILENAME_START, 
	FILTER_FILENAME_END, 
	FILTER_FILENAME_EXT, 
	FILTER_FILENAME_CONTAIN
};

enum FILTER_STATUS
{
	FILTER_STATUS_NORMAL,
	FILTER_STATUS_USED,
	FILTER_STATUS_REMOVE,
	FILTER_STATUS_EXPIRE
};

typedef struct _FILTER_
{
	FILTER_TYPE type;
	FILTER_STATUS status;
}FILTER;

class FilterMgr
{
public:
	virtual ~FilterMgr(){}

	static FilterMgr* create(UserContext* userContext);

	virtual bool isMaxPath(const std::wstring& str) = 0;

	virtual bool isStaticFilter(const std::wstring& str) = 0;

	virtual int32_t addStaticFilter(const std::wstring& str, FILTER_TYPE iType) = 0;

	virtual bool isUploadFilter(const std::wstring& str) = 0;

	virtual int32_t addUploadFilter(const std::wstring& str) = 0;

	virtual bool isKiaFilter(const std::wstring& path) = 0;

	//static bool isHuaweiDevice();
};

#endif
