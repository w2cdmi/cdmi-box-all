#ifndef _CINI_HELPER_H_
#define _CINI_HELPER_H_

#include <xstring>
#include <stdint.h>
#include <boost/thread/mutex.hpp>

template<typename T>
T str2cast(const std::wstring& value)
{
	T result = 0;
    std::wistringstream sstream(value);
    sstream>>result;
    return result;
}

template<typename T>
std::wstring cast2str(T value)
{
	std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

/*lint -save -e1712 */
class CInIHelper
{
public:
    CInIHelper(const std::wstring& strPath);

    virtual ~CInIHelper();

    void SetInIFilePath(const std::wstring& strPath);

    void SetInt32(const std::wstring& strSection,const std::wstring& strKey,int32_t iValue);

    void SetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& strValue);

    void SetInt64(const std::wstring& strSection,const std::wstring& strKey, int64_t llValue);

    int32_t GetInt32(const std::wstring& strSection,const std::wstring& strKey,int32_t llDefaultValue);

    std::wstring GetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& llDefaultValue);

    int64_t GetInt64(const std::wstring& strSection,const std::wstring& strKey, int64_t llDefaultValue);

	template<typename T>
	T GetValue(const std::wstring& strSection,const std::wstring& strKey, T const & defaultValue)
	{
		boost::mutex::scoped_lock lock(m_mutex);
		wchar_t szValue[MAX_PATH] = {0};
		int32_t ret = (int32_t)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),cast2str<T>(defaultValue).c_str(),szValue,MAX_PATH,m_strPath.c_str());
		(void)ret;
		return str2cast<T>(std::wstring(szValue));
	}

	template<typename T>
	void SetValue(const std::wstring& strSection,const std::wstring& strKey, T const & value)
	{
		boost::mutex::scoped_lock lock(m_mutex);
		WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),cast2str<T>(value).c_str(),m_strPath.c_str());
	}
private:
    std::wstring m_strPath;
	boost::mutex m_mutex;
};

#endif 
/*lint -restore */
