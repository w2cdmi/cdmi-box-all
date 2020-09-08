#pragma warning(disable:4996)
#include <boost/algorithm/string.hpp>
#include <vector>
#include <map>
#include <set>
#include <boost/thread.hpp>

#include "FilterMgr.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "NscaSdkMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("FilterMgr")
#endif

class FilterMgrImpl : public FilterMgr
{
public:
	FilterMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		init();
	}

	//false is valid£¬true is invalid
	bool isMaxPath(const std::wstring& str)
	{
		return false;
		//return (str.size()<FS_MAX_PATH)?false:true;
	}

	bool isStaticFilter(const std::wstring& str)
	{
		std::wstring strTmp = SD::Utility::String::to_lower(str);
		for (std::map<std::wstring, FILTER>::iterator it = m_staticFilters.begin(); it != m_staticFilters.end(); ++it)
		{
			std::wstring filter = it->first;

			FILTER_TYPE type = it->second.type;
			if (FILTER_PREFIX == type && (strTmp.substr(0, filter.size()) == filter))
			{
				return true;
			}
			else if (FILTER_SUFFIX == type && (strTmp.substr(strTmp.size() - filter.size(), filter.size()) == filter))
			{
				return true;
			}
			else if (FILTER_KEYWORD == type && std::wstring::npos != std::wstring(PATH_DELIMITER+strTmp+PATH_DELIMITER).find(filter))
			{
				return true;
			}
			else if (FILTER_FILENAME_CONTAIN == type && std::wstring::npos == strTmp.find(PATH_DELIMITER)
				&& std::wstring::npos != strTmp.find(filter))
			{
				return true;
			}
		}
		return false;
	}

	int32_t addStaticFilter(const std::wstring& str, FILTER_TYPE iType)
	{
		if (!str.empty())
		{
			FILTER filter;
			std::wstring strTmp = SD::Utility::String::to_lower(str);
			FILTER_TYPE iTmpType = iType;

			if (FILTER_FILENAME == iTmpType)
			{
				strTmp = PATH_DELIMITER + strTmp + PATH_DELIMITER;
				iTmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_START == iTmpType)
			{
				strTmp = PATH_DELIMITER + strTmp;
				iTmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_END == iTmpType)
			{
				strTmp = strTmp + PATH_DELIMITER;
				iTmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_EXT == iTmpType)
			{
				strTmp = L"." + strTmp + PATH_DELIMITER;
				iTmpType = FILTER_KEYWORD;
			}

			filter.type = iTmpType;
			filter.status = FILTER_STATUS_NORMAL;
			m_staticFilters[strTmp] = filter;
		}
		return RT_OK;
	}

	bool isUploadFilter(const std::wstring& str)
	{
/*************2016/9/26 lidonghai@chinasofti.com*******		
		if (!isHuaweiDevice())
		{
			return false;
		}
*****************************/
		std::wstring strTmp = SD::Utility::String::to_lower(str);
		for (std::map<std::wstring, FILTER>::iterator it = m_uploadFilters.begin(); it != m_uploadFilters.end(); ++it)
		{
			std::wstring filter = it->first;
			if(std::wstring::npos != std::wstring(PATH_DELIMITER+strTmp+PATH_DELIMITER).find(filter))
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "is upload filter: %s.", SD::Utility::String::wstring_to_string(str).c_str());
				return true;
			}
		}
		return false;
	}

	int32_t addUploadFilter(const std::wstring& str)
	{
		if (!str.empty())
		{
			std::wstring strTmp = L"." + SD::Utility::String::to_lower(str) + PATH_DELIMITER;

			FILTER filter;
			filter.type = FILTER_KEYWORD;
			filter.status = FILTER_STATUS_NORMAL;
			m_uploadFilters[strTmp] = filter;
		}
		return RT_OK;
	}

	bool isKiaFilter(const std::wstring& path)
	{
/*************2016/9/26 lidonghai@chinasofti.com*******		
		if (!isHuaweiDevice())
		{
			return false;
		}
*****************************/
		std::string tmp_path = SD::Utility::String::wstring_to_utf8(path);
		return NscaSdkMgr::instance()->isKia(tmp_path.c_str());
	}

private:
	void init()
	{
		(void)addStaticFilter(L"\"", FILTER_KEYWORD);
		(void)addStaticFilter(L"*", FILTER_KEYWORD);
		(void)addStaticFilter(L"?", FILTER_KEYWORD);
		(void)addStaticFilter(L"<", FILTER_KEYWORD);
		(void)addStaticFilter(L">", FILTER_KEYWORD);
		(void)addStaticFilter(L"|", FILTER_KEYWORD);
		(void)addStaticFilter(L":", FILTER_FILENAME_CONTAIN);

		FilterStr filterStr = userContext_->getConfigureMgr()->getConfigure()->filterStr();
		std::vector<std::wstring> nameFilters, nameStartFilters, nameEndFilters, nameExtFilters;
		boost::split(nameFilters, filterStr.nameFilter, boost::is_any_of(L";"), boost::token_compress_on);
		boost::split(nameStartFilters, filterStr.nameStartFilter, boost::is_any_of(L";"), boost::token_compress_on);
		boost::split(nameEndFilters, filterStr.nameEndFilter, boost::is_any_of(L";"), boost::token_compress_on);
		boost::split(nameExtFilters, filterStr.nameExtFilter, boost::is_any_of(L";"), boost::token_compress_on);
		for_each(nameFilters.begin(), nameFilters.end(), boost::bind(&FilterMgrImpl::addStaticFilter, this, _1, FILTER_FILENAME));
		for_each(nameStartFilters.begin(), nameStartFilters.end(), boost::bind(&FilterMgrImpl::addStaticFilter, this, _1, FILTER_FILENAME_START));
		for_each(nameEndFilters.begin(), nameEndFilters.end(), boost::bind(&FilterMgrImpl::addStaticFilter, this, _1, FILTER_FILENAME_END));
		for_each(nameExtFilters.begin(), nameExtFilters.end(), boost::bind(&FilterMgrImpl::addStaticFilter, this, _1, FILTER_FILENAME_EXT));

		std::vector<std::wstring> uploadExtFilters;

		std::wstring uploadFilterStr = userContext_->getConfigureMgr()->getConfigure()->uploadFilterStr();
		boost::split(uploadExtFilters, uploadFilterStr, boost::is_any_of(L";"), boost::token_compress_on);
		for_each(uploadExtFilters.begin(), uploadExtFilters.end(), boost::bind(&FilterMgrImpl::addUploadFilter, this, _1));
	}

private:
	UserContext* userContext_;

	std::map<std::wstring, FILTER> m_staticFilters;
	std::map<std::wstring, FILTER> m_uploadFilters;
	boost::mutex m_resMutex;

};

FilterMgr* FilterMgr::create(UserContext* userContext)
{
	return static_cast<FilterMgr*>(new FilterMgrImpl(userContext));
}
/******2016/9/26 lidonghai@chinasofti.com
bool FilterMgr::isHuaweiDevice()
{
	std::wstring path = L"";
	int32_t ret = SD::Utility::Registry::get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Huawei\\SPES5.0\\Composites\\spes", L"InstallPath", path);
	if (RT_OK != ret)
	{
		ret = SD::Utility::Registry::get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Wow6432Node\\Huawei\\SPES5.0\\Composites\\spes", L"InstallPath", path);
		if (RT_OK != ret)
		{
			//HSLOG_ERROR(MODULE_NAME, ret, "get device information from regist failed.");
			return false;
		}		
	}

	if (path.empty())
	{
		return false;
	}

	path += L"\\spes.exe";

	return SD::Utility::FS::is_exist(path);
}
******************/