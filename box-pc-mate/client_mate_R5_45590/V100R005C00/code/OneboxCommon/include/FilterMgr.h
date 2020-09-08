#ifndef _FILTER_MGR_H_
#define _FILTER_MGR_H_

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

class FilterMgr
{
public:
	virtual ~FilterMgr(){}

	static FilterMgr* create(UserContext* userContext);

	virtual bool isFilter(const std::wstring& str) = 0;

	virtual int32_t addFilter(const std::wstring& str, FILTER_TYPE type) = 0;

	virtual bool isKIA(const std::wstring& path) = 0;

	virtual bool isValid(const std::wstring& path) = 0;

	static bool isHuaweiDevice();
};

#endif
