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
    int64_t offset;			//ƫ��������0��ʼ
    uint32_t limit;		//ÿ��������ļ�����Ŀ��Ĭ��ֵΪ100�����ֵΪ1000
	ParamOrderList orderList;
	ParamTrumbList trumbList;
	bool is_used;

	PageParam():offset(0), limit(100), is_used(true)
	{
	}
};

#endif