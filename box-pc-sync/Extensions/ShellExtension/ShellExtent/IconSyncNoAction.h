// IconSyncNoAction.h : CIconSyncNoAction ������

#pragma once
#include "resource.h"       // ������
#include <shlobj.h>
#include "ShellExtent_i.h"



#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Windows CE ƽ̨(�粻�ṩ��ȫ DCOM ֧�ֵ� Windows Mobile ƽ̨)���޷���ȷ֧�ֵ��߳� COM ���󡣶��� _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA ��ǿ�� ATL ֧�ִ������߳� COM ����ʵ�ֲ�����ʹ���䵥�߳� COM ����ʵ�֡�rgs �ļ��е��߳�ģ���ѱ�����Ϊ��Free����ԭ���Ǹ�ģ���Ƿ� DCOM Windows CE ƽ̨֧�ֵ�Ψһ�߳�ģ�͡�"
#endif

using namespace ATL;


// CIconSyncNoAction

class ATL_NO_VTABLE CIconSyncNoAction :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CIconSyncNoAction, &CLSID_IconSyncNoAction>,
	public IDispatchImpl<IIconSyncNoAction, &IID_IIconSyncNoAction, &LIBID_ShellExtentLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
	public IShellIconOverlayIdentifier
{
public:
	CIconSyncNoAction()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_ICONSYNCNOACTION)


BEGIN_COM_MAP(CIconSyncNoAction)
	COM_INTERFACE_ENTRY(IIconSyncNoAction)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(IShellIconOverlayIdentifier)
END_COM_MAP()

    DECLARE_PROTECT_FINAL_CONSTRUCT()

STDMETHOD(GetOverlayInfo)(LPWSTR pwszIconFile,int cchMax,int *pIndex,DWORD* pdwFlags);
STDMETHOD(GetPriority)(int* pPriority);
STDMETHOD(IsMemberOf)(LPCWSTR pwszPath,DWORD dwAttrib);

    HRESULT FinalConstruct()
    {
        return S_OK;
    }

	void FinalRelease()
	{
	}

public:



};

OBJECT_ENTRY_AUTO(__uuidof(IconSyncNoAction), CIconSyncNoAction)
