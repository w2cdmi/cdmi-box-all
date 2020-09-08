#ifndef _CSJSON_PARSER_INL_
#define _CSJSON_PARSER_INL_

#include "ErrorCode.h"

template<typename DataUnit>
JsonParser::Parser<DataUnit>::Parser(DataBuffer &dataBuf)
{
	std::string jsonStr;
	Json::Reader reader;
	jsonRoot_ = Json::nullValue;

	if ((NULL != dataBuf.pBuf) && (0 < dataBuf.lOffset))
	{
		//jsonStr.assign((char *)dataBuf.pBuf, (int32_t)dataBuf.lOffset);
		jsonStr.assign((char *)dataBuf.pBuf);
		reader.parse(jsonStr, jsonRoot_);
	}
}

template<typename DataUnit>
JsonParser::Parser<DataUnit>::~Parser()
{
}

template<typename DataUnit>
inline int32_t JsonParser::Parser<DataUnit>::parseList( const std::string& strKey, UnitParserPtr parser, std::list<DataUnit*>& dataUnits )
{
	if (jsonRoot_.isNull() || (NULL == parser))
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	Json::Value jsonList;
	jsonList = jsonRoot_[strKey];

	for(uint32_t i=0; i<jsonList.size(); ++i)
	{
		Json::Value jsonNode = jsonList[i];
		try
		{
			DataUnit *pObjInfo = new DataUnit;
			ret = parser(jsonNode, *pObjInfo);
			if (RT_OK != ret)
			{
				delete pObjInfo;
				continue;
			}
			dataUnits.push_back(pObjInfo);
		}
		catch(std::bad_alloc& e)
		{
			(void)e;
			return RT_MEMORY_MALLOC_ERROR;
		}
	}

	return ret;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseList(const std::string& strKey, Json::Value& jsonList)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strKey].isArray())
	{
		jsonList = Json::nullValue;
		return RT_OK;
	}

	jsonList = jsonRoot_[strKey];

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeafSafe(const std::string& strLeafKey, std::string& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isString()||jsonRoot_[strLeafKey].isNull())
	{
		strLeafValue = "";
		return FAILED_TO_PARSEJSON;
	}

	strLeafValue = jsonRoot_[strLeafKey].asString();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, std::string& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isString())
	{
		strLeafValue = "";
		return RT_OK;
	}

	strLeafValue = jsonRoot_[strLeafKey].asString();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, int32_t& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isInt())
	{
		strLeafValue = 0;
		return RT_OK;
	}

	strLeafValue = jsonRoot_[strLeafKey].asInt();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, int64_t& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isUInt64())
	{
		strLeafValue = 0;
		return RT_OK;
	}

	strLeafValue = (int64_t)jsonRoot_[strLeafKey].asUInt64();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, uint64_t& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isInt64())
	{
		strLeafValue = 0;
		return RT_OK;
	}

	strLeafValue = (uint64_t)jsonRoot_[strLeafKey].asInt64();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, bool& strLeafValue)
{
	if(jsonRoot_.isNull()||!jsonRoot_[strLeafKey].isBool())
	{
		strLeafValue = false;
		return RT_OK;
	}

	strLeafValue = jsonRoot_[strLeafKey].asBool();

	return RT_OK;
}

template<typename DataUnit>
int32_t JsonParser::Parser<DataUnit>::parseLeaf(const std::string& strLeafKey, Json::Value& jsonValue)
{
	if(jsonRoot_.isNull())
	{
		jsonValue = Json::nullValue;
		return RT_OK;
	}

	jsonValue = jsonRoot_[strLeafKey];

	return RT_OK;
}

#endif//_CSJSON_PARSER_INL_
