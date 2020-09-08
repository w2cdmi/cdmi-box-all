#include "SyncUtility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncUtility")
#endif

std::string Sync::getInStr(const std::list<int64_t>& idList)
{
	if(idList.empty())
	{
		return "";
	}

	std::stringstream inStr;
	std::list<int64_t>::const_iterator it = idList.begin();

	inStr<<*it;
	++it;

	for(; it != idList.end(); ++it)
	{
		inStr<<",";
		inStr<<*it;
	}
	return inStr.str();
}

std::string Sync::getInStrEx(const std::list<int64_t>& idList)
{
	if(idList.empty())
	{
		return "''";
	}

	std::stringstream inStr;
	std::list<int64_t>::const_iterator it = idList.begin();

	inStr<<"'"<<*it<<"'";
	++it;

	for(; it != idList.end(); ++it)
	{
		inStr<<",";
		inStr<<"'"<<*it<<"'";
	}
	return inStr.str();
}