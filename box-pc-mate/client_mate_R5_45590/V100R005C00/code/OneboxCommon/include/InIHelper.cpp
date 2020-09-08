#include "InIHelper.h"
#include <sstream>
#include <Windows.h>

std::wstring Int32ToStr(int32_t value)
{
    std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

std::wstring Int64ToStr(int64_t value)
{
    std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

int64_t StrToInt64(const std::wstring& value)
{
    int64_t result = 0;
    std::wistringstream sstream(value);
    sstream>>result;
    return result;
}

CInIHelper::CInIHelper(const std::wstring& strPath)
{
    m_strPath = strPath;
}

CInIHelper::~CInIHelper()
{

}

void CInIHelper::SetInIFilePath(const std::wstring& strPath)
{
    boost::mutex::scoped_lock lock(m_mutex);
    m_strPath = strPath;
}

void CInIHelper::SetInt32(const std::wstring& strSection,const std::wstring& strKey,int32_t iValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int32ToStr(iValue).c_str(),m_strPath.c_str());
}

void CInIHelper::SetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& strValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),strValue.c_str(),m_strPath.c_str());
}

void CInIHelper::SetInt64(const std::wstring& strSection,const std::wstring& strKey, int64_t llValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int64ToStr(llValue).c_str(),m_strPath.c_str());
}

int32_t CInIHelper::GetInt32(const std::wstring& strSection,const std::wstring& strKey,int32_t llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    return (int32_t)GetPrivateProfileIntW(strSection.c_str(),strKey.c_str(),llDefaultValue,m_strPath.c_str());
}

std::wstring CInIHelper::GetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
	wchar_t *szValue = new (std::nothrow) wchar_t[MAX_VALUE_LEN];
	if (NULL == szValue)
	{
		return llDefaultValue;
	}
	(void)memset_s(szValue, MAX_VALUE_LEN*sizeof(wchar_t), 0, MAX_VALUE_LEN*sizeof(wchar_t));
    (void)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),llDefaultValue.c_str(),szValue,MAX_VALUE_LEN,m_strPath.c_str());
    std::wstring strOut = szValue;
	delete []szValue;
	return strOut;
}

int64_t CInIHelper::GetInt64(const std::wstring& strSection,const std::wstring& strKey, int64_t llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    wchar_t szValue[MAX_PATH] = {0};
    (void)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int64ToStr(llDefaultValue).c_str(),szValue,MAX_PATH,m_strPath.c_str());
    return StrToInt64(std::wstring(szValue));
}
