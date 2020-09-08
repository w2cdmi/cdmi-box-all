#include "stdafx.h"
#include "ContextMenuExt.h"
#include <shellapi.h>
#include <stdio.h>
#include <Strsafe.h>
#include "Global.h"
#include "InILanguage.h"
#include "ThriftClient.h"
#include "ControlNames.h"
#include "NotifyMsg.h"
#include "Upload.h"
#include "UICommonDefine.h"
#include "NotifyMsg.h"

using namespace SD;

HRESULT CContextMenuExt::Initialize( LPCITEMIDLIST pidlFolder, LPDATAOBJECT pDataObj, HKEY hProgID )
{
    if (pidlFolder != NULL)
    {
        return E_INVALIDARG;
    }

	HDROP hdrop;
    FORMATETC fmt = { CF_HDROP, NULL, DVASPECT_CONTENT, -1, TYMED_HGLOBAL };
    STGMEDIUM stg = { TYMED_HGLOBAL };
    bool bChangedDir = false;

    if (FAILED( pDataObj->GetData (&fmt, &stg)))
    {
        return E_INVALIDARG;
    }

    hdrop = (HDROP)GlobalLock ( stg.hGlobal );
    if (NULL == hdrop)
    {
        //ReleaseStgMedium ( &stg );
        return E_INVALIDARG;
    }

    UINT uNumFiles = DragQueryFile ( hdrop, 0xFFFFFFFF, NULL, 0 );
    if (0 == uNumFiles)
    {
        GlobalUnlock(stg.hGlobal);
        ReleaseStgMedium(&stg);
        return E_INVALIDARG;
    }

	for (UINT i = 0; i < uNumFiles; ++i)
	{
		TCHAR szFile [MAX_PATH] = {0};
		if ( 0 != DragQueryFile ( hdrop, i, szFile, MAX_PATH ))
		{
			std::wstring wcsPath = szFile;
			if (wcsPath.empty())
				continue;

			std::wstring wcsSuffix;
			size_t iDotIndex = wcsPath.find_last_of('.');
			wcsSuffix = wcsPath.substr(iDotIndex + 1, -1);
			if (wcsSuffix == L"lnk")
				continue;

			wcsSuffix = wcsPath.substr(0, 2);
			if (wcsSuffix == L"\\\\")
				continue;

			m_lsFiles.push_back ( std::wstring(szFile) );
		}
	}
    
    GlobalUnlock ( stg.hGlobal );
    ReleaseStgMedium ( &stg );

    return ( m_lsFiles.size() > 0 ) ? S_OK : E_INVALIDARG;
}

HBITMAP WhiteToTransparent(HBITMAP hbmp)
{
    HBITMAP newBitMap=NULL;
    DIBSECTION ds;
    GetObject(hbmp, sizeof(ds), &ds);
    int w = ds.dsBmih.biWidth;
    int h = ds.dsBmih.biHeight;

    HWND desktop = GetDesktopWindow();
    if (desktop == NULL)
    {
        DeleteObject(hbmp);
        return NULL;
    }
    HDC hdc = GetDC(desktop);
    if (hdc == NULL)
    {
        DeleteObject(hbmp);
        return NULL;
    }

    HDC newDC;
    HDC tempDC;
    newDC= CreateCompatibleDC(hdc);
    tempDC = CreateCompatibleDC(hdc);
    if(newDC == NULL || tempDC == NULL)
    {
        DeleteObject(hbmp);
        DeleteDC(newDC);
        DeleteDC(tempDC);
        ReleaseDC(desktop, hdc); 
        return NULL;
    }

	LONG width = w, hight = h;
    COLORREF nColor = GetSysColor(COLOR_BTNFACE );
    RECT rect;
	rect.right = width;
	rect.bottom = hight;
    //rect.right = GetSystemMetrics(SM_CXMENUCHECK);
    //rect.bottom = GetSystemMetrics(SM_CYMENUCHECK);
    rect.left = rect.top = 0;

	newBitMap=CreateCompatibleBitmap(hdc,width,hight);
    if(newBitMap == NULL)
    {
        DeleteObject(hbmp);
        DeleteDC(newDC);
        DeleteDC(tempDC);
        ReleaseDC(desktop,hdc);
        return NULL;
    }

    SelectObject(newDC,newBitMap);
    SelectObject(tempDC,hbmp); 

    SetBkColor(newDC,nColor);
    ExtTextOut(newDC, 0, 0, ETO_OPAQUE, &rect, NULL, 0, NULL);

	TransparentBlt(newDC,0,0,width,hight,tempDC,0,0,w,h,RGB(255,255,255));

    DeleteObject(hbmp);
	DeleteDC(tempDC);
	DeleteDC(newDC);
	ReleaseDC(desktop,hdc);

    return newBitMap;
}

STDMETHODIMP CContextMenuExt::QueryContextMenu( HMENU hmenu, UINT uMenuIndex, UINT uidFirstCmd, UINT uidLastCmd, UINT uFlags )
{
	if (!Onebox::IsOneboxRunning())
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}

	if (uFlags & CMF_DEFAULTONLY) 
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}

	if (0 == m_lsFiles.size())
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}
	string_list::iterator ibegin = m_lsFiles.begin();
	std::wstring wcsPath = *ibegin;

	//kick .lnk file, or there will be 2 "Upload to Onebox" for a .lnk file
	std::wstring wcsSuffix;
	size_t iDotIndex = wcsPath.find_last_of('.');
	wcsSuffix = wcsPath.substr(iDotIndex + 1, -1);
	if (wcsSuffix == L"lnk")
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}

	wcsSuffix = wcsPath.substr(0, 2);
	if (wcsSuffix == L"\\\\")
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}

	if (Onebox::isSyncDir(wcsPath))
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}
	
	// save the first cmd
	UINT uidFirstCmd_t = uidFirstCmd;

	// insert the upload menu
	if (m_lsFiles.size() == 1)
	{
		UINT uidCurCmd = uidFirstCmd++;
		UINT uCurMenuIndex = uMenuIndex++;
		InsertMenu(hmenu, uCurMenuIndex, MF_STRING|MF_BYPOSITION, uidCurCmd, Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_UPLOADTOONEBOX_KEY).c_str());
		if (NULL != m_hUploadFile)
		{
			HBITMAP hBitmap = WhiteToTransparent(m_hUploadFile);
			SetMenuItemBitmaps(hmenu, uCurMenuIndex, MF_BYPOSITION, hBitmap, NULL);
		}
		m_iUploadFileIndex = uidCurCmd-uidFirstCmd_t;
	}
	// insert the backup menu
// 	if (m_lsFiles.size() == 1)
// 	{
// 		if (SD::Utility::FS::is_directory(wcsPath))
// 		{
// 			UINT uidCurCmd = uidFirstCmd++;
// 			UINT uCurMenuIndex = uMenuIndex++;
// 			InsertMenu(hmenu, uCurMenuIndex, MF_STRING|MF_BYPOSITION, uidCurCmd, Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_CREATEBACKUP_KEY).c_str());
// 			if (NULL != m_hCreateBackupFile)
// 			{
// 				HBITMAP hBitmap = WhiteToTransparent(m_hCreateBackupFile);
// 				SetMenuItemBitmaps(hmenu, uCurMenuIndex, MF_BYPOSITION, hBitmap, NULL);
// 			}
// 			m_iCreateBackupFileIndex = uidCurCmd-uidFirstCmd_t;
// 		}
// 	}

	if (m_lsFiles.size() == 1 && IsAdministratorUser())
	{
		if (SD::Utility::FS::is_directory(wcsPath) && SyncServiceClientWrapper::getInstance()->needAddFullBackup(SD::Utility::String::wstring_to_utf8(wcsPath)))
		{
			UINT uidCurCmd = uidFirstCmd++;
			UINT uCurMenuIndex = uMenuIndex++;
			InsertMenu(hmenu, uCurMenuIndex, MF_STRING|MF_BYPOSITION, uidCurCmd, Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_CREATEBACKUP_KEY).c_str());
			if (NULL != m_hAddBackupFile)
			{
				HBITMAP hBitmap = WhiteToTransparent(m_hAddBackupFile);
				SetMenuItemBitmaps(hmenu, uCurMenuIndex, MF_BYPOSITION, hBitmap, NULL);
			}
			m_iAddBackupFileIndex = uidCurCmd-uidFirstCmd_t;
		}
	}

	return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, uidFirstCmd-uidFirstCmd_t);
}

#ifdef _WIN64
STDMETHODIMP CContextMenuExt::GetCommandString( UINT_PTR uCmdID, UINT uFlags,UINT* puReserved, LPSTR szName, UINT cchMax )
#else
STDMETHODIMP CContextMenuExt::GetCommandString( UINT uCmdID, UINT uFlags,UINT* puReserved, LPSTR szName, UINT cchMax )
#endif
{

//    USES_CONVERSION;

    if ( uCmdID >=2)
    {
        return E_INVALIDARG;
    }

    if ( uFlags & GCS_HELPTEXT )
    {
		//CInIHelper iniHelper(Global::getLangPath());
//      std::wstring szPrompt = L"";
		if ( uFlags & GCS_HELPTEXT )
		{
// 			if (uCmdID == m_iUploadFileIndex)
// 			{
// 				szPrompt = Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_UPLOADTOONEBOX_KEY);
// 			}
// 			else if (uCmdID == m_iCreateBackupFileIndex)
// 			{
// 				szPrompt = Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_CREATEBACKUP_KEY);
// 			}
// 			else if (uCmdID == m_iEncryptUploadIndex)
// 			{
// 				szPrompt = Onebox::GetMenuLanguageString(LANGUAGE_SHEELLEXTENT_ENCRYPTUPLOAD_KEY);
// 			}
// 			else
// 			{
// 				return E_INVALIDARG; 
// 			}
// 		}
// 
// 		if (!szPrompt.empty())
// 		{
// 			if ( uFlags & GCS_UNICODE )
// 			{
// 				(void)StringCchCopyW((LPWSTR)szName,cchMax,T2CW(szPrompt.c_str()));
// 			}
// 			else
// 			{
// 				LPCSTR temp = T2CA(szPrompt.c_str());
// 				if(NULL!=temp)
// 				{
// 					(void)StringCchCopyA(szName,cchMax,temp);
// 				}
// 			}
// 		}
			return S_OK;}
    }

    return E_INVALIDARG;
}

STDMETHODIMP CContextMenuExt::InvokeCommand( LPCMINVOKECOMMANDINFO pCmdInfo )
{
    if ( 0 != HIWORD( pCmdInfo->lpVerb ) )
    {
        return  E_INVALIDARG;
    }

    std::wstring wstrPath = m_lsFiles.front();

	if ( 0 != HIWORD( pCmdInfo->lpVerb ) )
	{
		return E_INVALIDARG; 
	}

	UINT uCmdID = LOWORD(pCmdInfo->lpVerb);

	if (uCmdID == m_iUploadFileIndex)
	{
		IniLanguageHelper iniLanguageHelper;
		HWND hWnd = ::FindWindow(RIGHT_UPLOADFRAME_CLSNAME,iniLanguageHelper.GetCommonString(COMMENT_SELECTCLOUD_KEY).c_str());
		if(NULL !=hWnd)
		{
			::SendMessage(hWnd,WM_CLOSE,0,0);
		}
		UploadImpl::getInstance()->UploadFile(wstrPath);
		return RT_OK;
	}
// 	else if (uCmdID == m_iCreateBackupFileIndex)
// 	{
// 		int32_t iRet = SyncServiceClientWrapper::getInstance()->sendMessage(NOTIFY_MSG_MENU_CREATEBACKUP,SD::Utility::String::wstring_to_utf8(wstrPath),"","","","");
// 		return iRet;
// 	}
	else if (uCmdID == m_iAddBackupFileIndex)
	{
		int32_t iRet = SyncServiceClientWrapper::getInstance()->addFullBackup(SD::Utility::String::wstring_to_utf8(wstrPath));
		if(iRet == RT_OK)
		{
			iRet = SyncServiceClientWrapper::getInstance()->sendMessage(NOTIFY_MSG_ADDFULLBACKUP_SUCCESSED, "", "", "", "", "");
		}
		else
		{
			iRet = SyncServiceClientWrapper::getInstance()->sendMessage(NOTIFY_MSG_ADDFULLBACKUP_FAILED, "", "", "", "", "");
		}
		return iRet;
	}
	else
	{
		return E_INVALIDARG; 
	}
}
