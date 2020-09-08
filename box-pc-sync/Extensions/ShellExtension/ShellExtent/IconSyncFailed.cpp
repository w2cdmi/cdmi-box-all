#include "stdafx.h"
#include "IconSyncFailed.h"
#include "Common/Utility.h"
#include "Globals.h"

//Provides the location of the icon overlay's bitmap,��ʼ��ʱ����
//�����̷���ͼ���ȫ·��
STDMETHODIMP CIconSyncFailed::GetOverlayInfo(LPWSTR pwszIconFile,
                                         int cchMax,int* pIndex,
                                         DWORD* pdwFlags)
{
    //�жϷ�������
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
    //��icon·��д��pwszIconFile
    wcsncpy_s(pwszIconFile,iconFilePath.length()*(sizeof(wchar_t)),iconFilePath.c_str(),cchMax);

    return S_OK;
}

// Specifies the priority of an icon overlay.��ʼ��ʱ���ã�
// the priority 0 being the highest. 
STDMETHODIMP CIconSyncFailed::GetPriority(int* pPriority)
{
    *pPriority= IconPriorityFailed;
    return S_OK;
}

//�����������Сͼ�긲��
// Returns S_OK to add Overlay icon.S_FALSE to keep icon intact
STDMETHODIMP CIconSyncFailed::IsMemberOf(LPCWSTR pwszPath, DWORD dwAttrib)
{
    //�жϷ����
    if (!IsDriveOk())
    {
        return S_FALSE;
    }
    //·���жϣ��Ƿ�Ϊ��
    if (NULL == pwszPath)
    {
        return S_FALSE;
    }
    //·���жϣ��Ƿ���ͬ��Ŀ¼��
    if(!IsInSyncFolder(pwszPath))	
    {
        return S_FALSE;
    }
    
    return S_FALSE;
}
