#include "stdafx.h"
#include "IconSyncFailed.h"
#include "Common/Utility.h"
#include "Globals.h"

//Provides the location of the icon overlay's bitmap,初始化时调用
//本工程返回图标的全路径
STDMETHODIMP CIconSyncFailed::GetOverlayInfo(LPWSTR pwszIconFile,
                                         int cchMax,int* pIndex,
                                         DWORD* pdwFlags)
{
    //判断返回条件
    if (NULL == pdwFlags || NULL == pwszIconFile)
    {
        return S_FALSE;
    }
    //You must set the ISIOI_ICONFILE flag in pdwFlags if you return a file name.
    *pdwFlags = ISIOI_ICONFILE;
    std::wstring iconFilePath = L"";
    int iRet = GetRegString(CSE_APP_REG_PATH,  CSE_APP_REG_PRO_PATH, iconFilePath) ;
    if (0 != iRet)
    {
        return S_FALSE;
    }
    iconFilePath.append(L"res\\SyncFailed.ico");
    //将icon路径写入pwszIconFile
    wcsncpy_s(pwszIconFile,iconFilePath.length()*(sizeof(wchar_t)),iconFilePath.c_str(),cchMax);

    return S_OK;
}

// Specifies the priority of an icon overlay.初始化时调用；
// the priority 0 being the highest. 
STDMETHODIMP CIconSyncFailed::GetPriority(int* pPriority)
{
    *pPriority= IconPriorityFailed;
    return S_OK;
}

//处理具体对像的小图标覆盖
// Returns S_OK to add Overlay icon.S_FALSE to keep icon intact
STDMETHODIMP CIconSyncFailed::IsMemberOf(LPCWSTR pwszPath, DWORD dwAttrib)
{
    //判断服务端
    if (!IsDriveOk())
    {
        return S_FALSE;
    }
    //路径判断：是否为空
    if (NULL == pwszPath)
    {
        return S_FALSE;
    }
    //路径判断：是否在同步目录下
    if(!IsInSyncFolder(pwszPath))	
    {
        return S_FALSE;
    }
    
    return S_FALSE;
}
