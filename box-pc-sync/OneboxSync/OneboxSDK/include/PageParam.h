#ifndef __ONEBOX__PAGE_PARAM__H__
#define __ONEBOX__PAGE_PARAM__H__

#include "CommonValue.h"
#include <list>

struct OrderParam
{
	std::string field;
	std::string direction;
	OrderParam():field("name"), direction("ASC")
	{
	}
};

struct TrumbParam
{
	int height;
	int width;
	TrumbParam():height(0), width(0)
	{
	}
};

typedef std::list<OrderParam> ParamOrderList;
typedef std::list<TrumbParam> ParamTrumbList;

struct PageParam
{
    int64_t offset;			//偏移量，从0开始
    uint32_t limit;		//每次最大反馈文件夹数目，默认值为100，最大值为1000
	ParamOrderList orderList;
	ParamTrumbList trumbList;
	bool is_used;

	PageParam():offset(0), limit(100), is_used(true)
	{
	}
};

#endif