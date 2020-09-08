#include "../stdafx.h"
#include <sstream>
#include <Windows.h>
#include <boost/thread/mutex.hpp>
#include "IniHelper.h"

static boost::mutex m_mutex;

std::wstring Int32ToStr(int value)
{
    std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

std::wstring Int64ToStr(long long value)
{
    std::wostringstream sstream;
    sstream.imbue(std::locale("C"));
    sstream<<value;
    return sstream.str();
}

long long StrToInt64(const std::wstring& value)
{
    long long result = 0;
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

void CInIHelper::SetInt32(const std::wstring& strSection,const std::wstring& strKey,int iValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int32ToStr(iValue).c_str(),m_strPath.c_str());
}

void CInIHelper::SetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& strValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),strValue.c_str(),m_strPath.c_str());
}

void CInIHelper::SetInt64(const std::wstring& strSection,const std::wstring& strKey, long long llValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    WritePrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int64ToStr(llValue).c_str(),m_strPath.c_str());
}

int CInIHelper::GetInt32(const std::wstring& strSection,const std::wstring& strKey,int llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    return (int)GetPrivateProfileIntW(strSection.c_str(),strKey.c_str(),llDefaultValue,m_strPath.c_str());
}

std::wstring CInIHelper::GetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    wchar_t szValue[MAX_PATH] = {0};
    int iRet = (int)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),llDefaultValue.c_str(),szValue,MAX_PATH,m_strPath.c_str());
    (void)iRet;
    return std::wstring(szValue);
}

long long CInIHelper::GetInt64(const std::wstring& strSection,const std::wstring& strKey, long long llDefaultValue)
{
    boost::mutex::scoped_lock lock(m_mutex);
    wchar_t szValue[MAX_PATH] = {0};
    int iRet = (int)GetPrivateProfileStringW(strSection.c_str(),strKey.c_str(),Int64ToStr(llDefaultValue).c_str(),szValue,MAX_PATH,m_strPath.c_str());
    (void)iRet;
    return StrToInt64(std::wstring(szValue));
}
