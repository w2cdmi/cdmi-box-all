#pragma once
#include "resource.h"
#include "ShellExtent_i.h"
#include "shlobj.h"
#include "comdef.h"
#include "UploadFrame.h"

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Windows CE 平台(如不提供完全 DCOM 支持的 Windows Mobile 平台)上无法正确支持单线程 COM 对象。定义 _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA 可强制 ATL 支持创建单线程 COM 对象实现并允许使用其单线程 COM 对象实现。rgs 文件中的线程模型已被设置为“Free”，原因是该模型是非 DCOM Windows CE 平台支持的唯一线程模型。"
#endif

using namespace ATL;

class ATL_NO_VTABLE CContextMenuExt :
    public CComObjectRootEx<CComSingleThreadModel>,
    public CComCoClass<CContextMenuExt, &CLSID_ContextMenuExt>,
    public IDispatchImpl<IContextMenuExt, &IID_IContextMenuExt, &LIBID_ShellExtentLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
    public IShellExtInit,
    public IContextMenu
{
public:
    CContextMenuExt()
    {
		m_hUploadFile = (HBITMAP)LoadImage(_AtlBaseModule.GetModuleInstance(), MAKEINTRESOURCE(IDB_BITMAP_CLOUDLOGO), 
			IMAGE_BITMAP, 0, 0, LR_CREATEDIBSECTION );
		//m_hCreateBackupFile = (HBITMAP)LoadImage(_AtlBaseModule.GetModuleInstance(), MAKEINTRESOURCE(IDB_BITMAP_CREATEBACKUP), 
			//IMAGE_BITMAP, 0, 0, LR_CREATEDIBSECTION );
		m_hAddBackupFile =  (HBITMAP)LoadImage(_AtlBaseModule.GetModuleInstance(), MAKEINTRESOURCE(IDB_BITMAP_CREATEBACKUP), 
			IMAGE_BITMAP, 0, 0, LR_CREATEDIBSECTION );

		m_iUploadFileIndex = -1;
		//m_iCreateBackupFileIndex = -1;
		m_iAddBackupFileIndex = -1;
    }

DECLARE_REGISTRY_RESOURCEID(IDR_CONTEXTMENUEXT)

BEGIN_COM_MAP(CContextMenuExt)
    COM_INTERFACE_ENTRY(IContextMenuExt)
    COM_INTERFACE_ENTRY(IDispatch)
    COM_INTERFACE_ENTRY(IShellExtInit)
    COM_INTERFACE_ENTRY(IContextMenu)
END_COM_MAP()

    DECLARE_PROTECT_FINAL_CONSTRUCT()

    HRESULT FinalConstruct()
    {
        return S_OK;
    }

    void FinalRelease()
    {
    }

    ~CContextMenuExt()
    {
		if (NULL != m_hUploadFile)
		{
			DeleteObject(m_hUploadFile);
		}
// 		if (NULL != m_hCreateBackupFile)
// 		{
// 			DeleteObject(m_hCreateBackupFile);
// 		}
		if (NULL != m_hAddBackupFile)
		{
			DeleteObject(m_hAddBackupFile);
		}
    }

protected:
	HBITMAP m_hUploadFile;
	//HBITMAP m_hCreateBackupFile;
	HBITMAP m_hAddBackupFile;
    string_list m_lsFiles;
    TCHAR m_szDir [MAX_PATH];

protected:
	int m_iUploadFileIndex;
	//int m_iCreateBackupFileIndex;
	int m_iAddBackupFileIndex;

public:
    // IShellExtInit 
    STDMETHOD(Initialize)(LPCITEMIDLIST, LPDATAOBJECT, HKEY);

    //IContextMenu
#ifdef _WIN64
    STDMETHODIMP GetCommandString(UINT_PTR, UINT, UINT*, LPSTR, UINT);
#else
    STDMETHODIMP GetCommandString(UINT, UINT, UINT*, LPSTR, UINT);
#endif

    STDMETHODIMP InvokeCommand(LPCMINVOKECOMMANDINFO);
    STDMETHODIMP QueryContextMenu(HMENU, UINT, UINT, UINT, UINT);
};

OBJECT_ENTRY_AUTO(__uuidof(ContextMenuExt), CContextMenuExt)
