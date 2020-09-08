#pragma once

#ifndef _CINI_HELPER_H_
#define _CINI_HELPER_H_

#include <xstring>

/*lint -save -e1712 */
class CInIHelper
{
public:
    CInIHelper(const std::wstring& strPath);

    virtual ~CInIHelper();

    void SetInIFilePath(const std::wstring& strPath);

    void SetInt32(const std::wstring& strSection,const std::wstring& strKey,int iValue);

    void SetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& strValue);

    void SetInt64(const std::wstring& strSection,const std::wstring& strKey, long long llValue);

    int GetInt32(const std::wstring& strSection,const std::wstring& strKey,int llDefaultValue);

    std::wstring GetString(const std::wstring& strSection,const std::wstring& strKey, const std::wstring& llDefaultValue);

    long long GetInt64(const std::wstring& strSection,const std::wstring& strKey, long long llDefaultValue);

private:
    std::wstring m_strPath;
};

#endif 
/*lint -restore */

