#include "stdafx.h"
#include "ContextMenuExt.h"
#include "Globals.h"
#include "InIHelper.h"
#include <shellapi.h>
#include <stdio.h>

using namespace Onebox::ShellExt;

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
    if (0 == uNumFiles || 1 < uNumFiles)
    {
        GlobalUnlock(stg.hGlobal);
        ReleaseStgMedium(&stg);
        return E_INVALIDARG;
    }

    TCHAR szFile [MAX_PATH] = {0};
    if ( 0 != DragQueryFile ( hdrop, 0, szFile, MAX_PATH ))
    {
        m_lsFiles.push_back ( std::wstring(szFile) );
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
    if (!Global::isSyncServiceNormal())
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
	int iDotIndex = wcsPath.find_last_of('.');
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

	if (Global::isMonitorRootPath(wcsPath))
	{
		return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
	}

	 CInIHelper iniHelper(Global::getLangPath());
     HMENU cloudDriveMenu = CreateMenu();
	 UINT uidstartCmdID = uidFirstCmd; 
	 if (Global::isSyncFile(wcsPath) )
	 {
		 if (!Global::isSynced(wcsPath))
		 {
			 return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, 0);
		 }
		if (cloudDriveMenu)
		{
			std::wstring shareStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_SHARE", L"Share");
			UINT iUnShareCmd = uidFirstCmd ++;
			m_iShareIndex = iUnShareCmd - uidstartCmdID;
			InsertMenu (cloudDriveMenu,CtxMenu_Operation_Share,MF_STRING | MF_BYPOSITION, iUnShareCmd, shareStr.c_str());
			if (NULL != m_hShareBmp)
			{
				HBITMAP newShareMap = WhiteToTransparent(m_hShareBmp);
				SetMenuItemBitmaps(cloudDriveMenu, CtxMenu_Operation_Share, MF_BYPOSITION, newShareMap, NULL);
			}
			
			std::wstring linkStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_OUTCHAIN", L"ShareLink");
			UINT iUnOutChainCmd = uidFirstCmd ++;
			m_iOutChainIndex = iUnOutChainCmd - uidstartCmdID;
			InsertMenu (cloudDriveMenu,CtxMenu_Operation_OutChain,MF_STRING | MF_BYPOSITION, iUnOutChainCmd, linkStr.c_str());
			if (NULL != m_hOutChainBmp)
			{
				HBITMAP newLinkBmp = WhiteToTransparent(m_hOutChainBmp);
				SetMenuItemBitmaps(cloudDriveMenu, CtxMenu_Operation_OutChain, MF_BYPOSITION, newLinkBmp, NULL);
			}
		}

		std::wstring cloudDriveStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_CLOUD_DRIVE", L"Onebox");
		UINT iUnCloudDriveCmd = uidFirstCmd ++;
		m_iCloudDriveIndex = iUnCloudDriveCmd - uidstartCmdID;
		UINT iUnCloudDriveIndex = uMenuIndex++;
		InsertMenu(hmenu,iUnCloudDriveIndex,MF_STRING | MF_BYPOSITION |MF_POPUP, (UINT_PTR)cloudDriveMenu, cloudDriveStr.c_str());
		if (NULL != m_hCloudDrive)
		{
			HBITMAP newLogoBmp = WhiteToTransparent(m_hCloudDrive);
			SetMenuItemBitmaps(hmenu, iUnCloudDriveIndex, MF_BYPOSITION, newLogoBmp, NULL);
		}
	 }
	 else
	 {
		 std::wstring wstrUpLoadFile = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_UPLOAD_FILE", L"Upload to Onebox");
		 UINT iUnUploadFileCmd = uidFirstCmd ++;
		 m_iUploadFileIndex = iUnUploadFileCmd - uidstartCmdID;
		 UINT iUnUploadFileIndex = uMenuIndex++;
		 InsertMenu(hmenu,iUnUploadFileIndex,MF_STRING | MF_BYPOSITION,  iUnUploadFileCmd, wstrUpLoadFile.c_str());
		 if (NULL != m_hUploadFile)
		 {
			 HBITMAP  newUploadFile = WhiteToTransparent(m_hUploadFile);
			 SetMenuItemBitmaps(hmenu, iUnUploadFileIndex, MF_BYPOSITION, newUploadFile, NULL);
		 }
	 }

	 return MAKE_HRESULT(SEVERITY_SUCCESS, FACILITY_NULL, uidFirstCmd-uidstartCmdID);
}

#ifdef _WIN64
STDMETHODIMP CContextMenuExt::GetCommandString( UINT_PTR uCmdID, UINT uFlags,UINT* puReserved, LPSTR szName, UINT cchMax )
#else
STDMETHODIMP CContextMenuExt::GetCommandString( UINT uCmdID, UINT uFlags,UINT* puReserved, LPSTR szName, UINT cchMax )
#endif
{
    if (!Global::isSyncServiceNormal())
    {
        return S_OK;
    }

    USES_CONVERSION;

    if ( uCmdID >=2)
    {
        return E_INVALIDARG;
    }

    if ( uFlags & GCS_HELPTEXT )
    {
		CInIHelper iniHelper(Global::getLangPath());
        LPCTSTR szPrompt=_T("");
		if ( uFlags & GCS_HELPTEXT )
		{
			LPCTSTR szPrompt=_T("");
			if (uCmdID == m_iShareIndex)
			{
				std::wstring shareStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_SHARE", L"Share");
				szPrompt = shareStr.c_str();
			}
			else if (uCmdID == m_iOutChainIndex)
			{
				std::wstring  chainStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_OUTCHAIN", L"ShareLink");
				szPrompt = chainStr.c_str(); 
			}
			else if (uCmdID ==m_iUploadFileIndex)
			{
				std::wstring  UploadStr = iniHelper.GetString(LANG_MSG_SECTION, L"ID_SHELL_UPLOAD_FILE", L"Upload to Onebox");
				szPrompt = UploadStr.c_str(); 
			}
			else
			{
				return E_INVALIDARG; 
			}
		}
        if ( uFlags & GCS_UNICODE )
        {
            lstrcpynW ( (LPWSTR) szName, T2CW(szPrompt), cchMax );
        }
        else
        {
            lstrcpynA (szName, T2CA(szPrompt), cchMax );
        }
        return S_OK;
    }

    return E_INVALIDARG;
}

STDMETHODIMP CContextMenuExt::InvokeCommand( LPCMINVOKECOMMANDINFO pCmdInfo )
{
    if ( 0 != HIWORD( pCmdInfo->lpVerb ) )
    {
        return  E_INVALIDARG;
    }

    string_list::iterator ibegin = m_lsFiles.begin();
    std::wstring wstrPath = *ibegin;

	if ( 0 != HIWORD( pCmdInfo->lpVerb ) )
	{
		return E_INVALIDARG; 
	}

	UINT uCmdID = LOWORD(pCmdInfo->lpVerb);

	if (uCmdID == m_iShareIndex)
	{
		Global::setShare(wstrPath);
		return S_OK;
	}
	else if (uCmdID == m_iOutChainIndex)
	{
		Global::setShareLink(wstrPath);
		return S_OK;
	}
	else if (uCmdID == m_iUploadFileIndex)
	{
		Global::uploadFileOperation(wstrPath);
		return S_OK;
	}
	else
	{
		return E_INVALIDARG; 
	}
}
