//#include "stdafxOnebox.h"
#include "CommonDefine.h"
#include "ErrorConfMgr.h"
#include "Utility.h"
#include <fstream>
#include <map>
#include "InILanguage.h"

using namespace SD;

namespace Onebox
{
	class ErrorConfMgrImpl : public ErrorConfMgr
	{
	public:
		ErrorConfMgrImpl()
		{
			loadErrorConf();
		}

		virtual int32_t loadErrorConf()
		{
			std::wstring errorConfPath_ = iniLanguageHelper.GetErrorLanguageFilePath();
			if (!Utility::FS::is_exist(errorConfPath_))
			{
				//TODO …Ë÷√ƒ¨»œ÷µ
				return RT_ERROR;
			}

			std::ifstream infile(errorConfPath_, std::ios::in);
			std::string temp;

			while(getline(infile,temp))
			{
				if(-1 == temp.find("<ErrorMsg"))
				{
					continue;
				}

				int32_t errorCode = Utility::String::string_to_type<int32_t>(getValue(temp, "cd"));
				std::wstring des = Utility::String::string_to_wstring(getValue(temp, "des"));
				std::wstring advice = Utility::String::string_to_wstring(getValue(temp, "advice"));

				errorDes_.insert(std::make_pair(errorCode, des));
				errorAdvice_.insert(std::make_pair(errorCode, advice));
				if(-1==errorCode)
				{
					defaultDes_ = des;
					defaultAdvice_ = advice;
				}
			}
			return RT_OK;
		}

		virtual std::wstring getDescription(int32_t errorCode)
		{
			std::map<int32_t, std::wstring>::iterator it = errorDes_.find(errorCode);
			if(it!=errorDes_.end())
			{
				return  it->second;
			}
			return defaultDes_;
		}

		virtual std::wstring getAdvice(int32_t errorCode)
		{
			std::map<int32_t, std::wstring>::iterator it = errorAdvice_.find(errorCode);
			if(it!=errorAdvice_.end())
			{
				return it->second;
			}
			return defaultAdvice_;			
		}

	private:
		std::string getValue(const std::string& str, const std::string& attrKey)
		{
			std::string value = "";
			size_t pos = str.find(attrKey);
			if(-1!=pos)
			{
				size_t posStart = str.find("\"", pos) + 1;
				size_t posEnd = str.find("\"", posStart);
				if(posEnd>posStart)
				{
					value = str.substr(posStart, posEnd-posStart);
				}
			}
			return value;
		}

	private:
		std::wstring errorConfPath_;
		std::wstring defaultDes_;
		std::wstring defaultAdvice_;
		std::map<int32_t, std::wstring> errorDes_;
		std::map<int32_t, std::wstring> errorAdvice_;
	};

	ErrorConfMgr* ErrorConfMgr::instance_ = NULL;

	ErrorConfMgr* ErrorConfMgr::getInstance()
	{
		if (NULL == instance_)
		{
			instance_ = static_cast<ErrorConfMgr*>(new ErrorConfMgrImpl());
		}
		return instance_;
	}
}