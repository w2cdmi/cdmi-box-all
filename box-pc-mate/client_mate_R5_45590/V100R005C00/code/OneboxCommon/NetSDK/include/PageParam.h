/******************************************************************************
Description  : ∑÷“≥ Ù–‘
Created By   : z00178165
*******************************************************************************/
#ifndef _PAGEPARAM_H_
#define _PAGEPARAM_H_

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
    int64_t offset;
    uint32_t limit;
	ParamOrderList orderList;
	ParamTrumbList trumbList;
	bool is_used;

	PageParam():offset(0), limit(100), is_used(true)
	{
	}
};

#endif