#ifndef _CINI_HELPER_H_
#define _CINI_HELPER_H_

#include <xstring>
#include <stdint.h>
#include <boost/thread/mutex.hpp>

#ifdef __cplusplus
extern "C"
{
#endif

/* 华为特有安全函数 */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#ifndef MAX_VALUE_LEN
#define MAX_VALUE_LEN (1024)
#endif

template<typename T>
inline T str2cast(const std::wstring& value)
{
	T result;
    std::wistringstream sstream(value);
	sstream.imbue(std::locale("C"));
    sstream>>result;
    return result;
}

template<>
inline std::wstring str2cast(const std::wstring& value)
{
	std::wstring result = value;
	return result;
}

template<typename T>
inline std::wstring cast2str(const T& value)
{
	std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

template<>
inline std::wstring cast2str(const std::wstring& value)
{
	std::wstring result = value;
	return result;
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
		wchar_t *szValue = new (std::nothrow) wchar_t[MAX_VALUE_LEN];
		if (NULL == szValue)
		{
			return defaultValue;
		}
		(void)memset_s(szValue, MAX_VALUE_LEN*sizeof(wchar_t), 0, MAX_VALUE_LEN*sizeof(wchar_t));
		(void)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),cast2str<T>(defaultValue).c_str(),szValue,MAX_VALUE_LEN,m_strPath.c_str());
		T ret = str2cast<T>(std::wstring(szValue));
		delete []szValue;
		return ret;
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
