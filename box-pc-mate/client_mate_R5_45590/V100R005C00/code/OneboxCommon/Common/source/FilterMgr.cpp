#pragma warning(disable:4996)
#include <boost/algorithm/string.hpp>
#include <boost/thread.hpp>
#include "FilterMgr.h"
#include "Utility.h"
#include "ConfigureMgr.h"
#include "SysConfigureMgr.h"
#include "NscaSdkMgr.h"
#include "UserInfo.h"
#include "UserInfoMgr.h"
#include <map>
#include <set>

#ifndef MODULE_NAME
#define MODULE_NAME ("FilterMgr")
#endif

#ifndef MAX_FILE_NAME_LENGTH
#define MAX_FILE_NAME_LENGTH 246
#endif

class FilterMgrImpl : public FilterMgr
{
public:
	FilterMgrImpl(UserContext* userContext)
		:userContext_(userContext)
	{
		init();
	}

	virtual ~FilterMgrImpl()
	{

	}

	virtual bool isFilter(const std::wstring& str)
	{
		boost::mutex::scoped_lock lock(mutex_);
		std::wstring strTmp = SD::Utility::String::to_lower(str);
		for (std::map<std::wstring, FILTER_TYPE>::iterator it = filters_.begin(); it != filters_.end(); ++it)
		{
			std::wstring filter = it->first;

			FILTER_TYPE type = it->second;
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

	virtual int32_t addFilter(const std::wstring& str, FILTER_TYPE type)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (!str.empty())
		{
			std::wstring tmpStr = SD::Utility::String::to_lower(str);
			FILTER_TYPE tmpType = type;
			if (FILTER_FILENAME == type)
			{
				tmpStr = PATH_DELIMITER + tmpStr + PATH_DELIMITER;
				tmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_START == type)
			{
				tmpStr = PATH_DELIMITER + tmpStr;
				tmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_END == type)
			{
				tmpStr = tmpStr + PATH_DELIMITER;
				tmpType = FILTER_KEYWORD;
			}
			else if (FILTER_FILENAME_EXT == type)
			{
				tmpStr = L"." + tmpStr + PATH_DELIMITER;
				tmpType = FILTER_KEYWORD;
			}
			filters_[tmpStr] = tmpType;
		}
		return RT_OK;
	}

	virtual bool isKIA(const std::wstring& path)
	{
		int32_t networkType = userContext_->getUserInfoMgr()->getNetworkType();
		if(NT_UYELLOW != networkType && NT_RDVM != networkType)
		{
			return false;
		}
		if (!isHuaweiDevice())
		{
			return false;
		}

		std::string tmp_path = SD::Utility::String::wstring_to_utf8(path);
		return NscaSdkMgr::instance()->isKia(tmp_path.c_str());
	}

	virtual bool isValid(const std::wstring& path)
	{
		if(path.empty())
		{
			return false;
		}
		std::wstring name = SD::Utility::FS::get_file_name(path);
		if(name.empty())
		{
			return false;
		}
		if(name.length() > MAX_FILE_NAME_LENGTH)
		{
			return false;
		}
		if(name.substr(name.length() - 1) == L".")
		{
			return false;
		}
		return true;
	}

private:
	void init()
	{
		(void)addFilter(L"\"", FILTER_KEYWORD);
		(void)addFilter(L"*", FILTER_KEYWORD);
		(void)addFilter(L"?", FILTER_KEYWORD);
		(void)addFilter(L"<", FILTER_KEYWORD);
		(void)addFilter(L">", FILTER_KEYWORD);
		(void)addFilter(L"|", FILTER_KEYWORD);
		(void)addFilter(L":", FILTER_FILENAME_CONTAIN);

		std::wstring defaultFilterStart = L"CON.;PRN.;AUX.;NUL.;COM1.;COM2.;COM3.;COM4.;COM5.;COM6.;COM7.;COM8.;COM9.;LPT1.;LPT2.;LPT3.;LPT4.;LPT5.;LPT6.;LPT7.;LPT8.;LPT9.";
		std::wstring defaultFilterName = L"CON;PRN;AUX;NUL;COM1;COM2;COM3;COM4;COM5;COM6;COM7;COM8;COM9;LPT1;LPT2;LPT3;LPT4;LPT5;LPT6;LPT7;LPT8;LPT9";
		std::vector<std::wstring> defaultNameStartFilters, defaultNameFilters;
		boost::split(defaultNameStartFilters, defaultFilterStart, boost::is_any_of(L";"), boost::token_compress_on);
		boost::split(defaultNameFilters, defaultFilterName, boost::is_any_of(L";"), boost::token_compress_on);
		for_each(defaultNameFilters.begin(), defaultNameFilters.end(), boost::bind(&FilterMgrImpl::addFilter, this, _1, FILTER_FILENAME));
		for_each(defaultNameStartFilters.begin(), defaultNameStartFilters.end(), boost::bind(&FilterMgrImpl::addFilter, this, _1, FILTER_FILENAME_START));

		SysFilter sysFilter = userContext_->getSysConfigureMgr()->getSysFilter();
		std::vector<std::wstring> nameFilters, nameStartFilters, nameEndFilters;
		boost::split(nameFilters, sysFilter.nameFilter, boost::is_any_of(L"|"), boost::token_compress_on);
		boost::split(nameStartFilters, sysFilter.nameStartFilter, boost::is_any_of(L"|"), boost::token_compress_on);
		boost::split(nameEndFilters, sysFilter.nameEndFilter, boost::is_any_of(L"|"), boost::token_compress_on);
		for_each(nameFilters.begin(), nameFilters.end(), boost::bind(&FilterMgrImpl::addFilter, this, _1, FILTER_FILENAME));
		for_each(nameStartFilters.begin(), nameStartFilters.end(), boost::bind(&FilterMgrImpl::addFilter, this, _1, FILTER_FILENAME_START));
		for_each(nameEndFilters.begin(), nameEndFilters.end(), boost::bind(&FilterMgrImpl::addFilter, this, _1, FILTER_FILENAME_END));
	}

private:
	UserContext* userContext_;
	std::map<std::wstring, FILTER_TYPE> filters_;
	boost::mutex mutex_;
};

FilterMgr* FilterMgr::create(UserContext* userContext)
{
	return static_cast<FilterMgr*>(new FilterMgrImpl(userContext));
}

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
