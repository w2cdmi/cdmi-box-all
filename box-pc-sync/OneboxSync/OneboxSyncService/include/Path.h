#ifndef _ONEBOX_PATH_H_
#define _ONEBOX_PATH_H_

#include "CommonDefine.h"

class Path
{
public:
	FUNC_DEFAULT_SET_GET(int64_t, id);
	FUNC_DEFAULT_SET_GET(int64_t, parent);
	FUNC_DEFAULT_SET_GET(std::wstring, name);
	FUNC_DEFAULT_SET_GET(std::wstring, path);
	FUNC_DEFAULT_SET_GET(int64_t, ownerId);
private:
	int64_t id_;
	int64_t parent_;
	std::wstring name_;
	std::wstring path_;
	int64_t ownerId_;
};

#endif