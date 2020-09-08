#include "stdafx.h"
#include "IconSyncNoAction.h"
#include "Globals.h"
#include "Utility.h"

using namespace Onebox::ShellExt;
using namespace SD;

STDMETHODIMP CIconSyncNoAction::GetOverlayInfo(LPWSTR pwszIconFile,
                                         int cchMax,int* pIndex,
                                         DWORD* pdwFlags)
{
    if (NULL == pdwFlags || NULL == pwszIconFile)
    {
        return S_FALSE;
    }
    //You must set the ISIOI_ICONFILE flag in pdwFlags if you return a file name.
    *pdwFlags = ISIOI_ICONFILE;
    std::wstring iconFilePath = L"";
    int ret = Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, APP_PATH_REG_NAME, iconFilePath) ;
    if (0 != ret)
    {
        return S_FALSE;
    }

    iconFilePath.append(L"\\res\\SyncNoAction.ico");
    wcsncpy_s(pwszIconFile,iconFilePath.length()*(sizeof(wchar_t)),iconFilePath.c_str(),cchMax);

    return S_OK;
}

// Specifies the priority of an icon overlay.
// the priority 0 being the highest. 
STDMETHODIMP CIconSyncNoAction::GetPriority(int* pPriority)
{
    *pPriority= IconPrioritySyncNoAction;
    return S_OK;
}

// Returns S_OK to add Overlay icon.S_FALSE to keep icon intact
STDMETHODIMP CIconSyncNoAction::IsMemberOf(LPCWSTR pwszPath, DWORD dwAttrib)
{
    if (!Global::isSyncServiceNormal())
    {
        return S_FALSE;
    }
    if (NULL == pwszPath)
    {
        return S_FALSE;
    }
    if(!Global::isSyncFile(pwszPath))	
    {
        return S_FALSE;
    }
    if (IconNoAction == Global::getOverlayIconStatus(pwszPath))
    {
        return S_OK;
    }
    return S_FALSE;
}

