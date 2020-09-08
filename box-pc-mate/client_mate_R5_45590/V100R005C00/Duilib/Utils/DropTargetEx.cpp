#include "StdAfx.h"
#include "DropTargetEx.h"

#pragma comment(lib, "Shlwapi.lib")

namespace DuiLib 
{
	CDropTargetEx::CDropTargetEx(bool bDrop):m_enableWnd(bDrop),m_piDropHelper(NULL),m_pDuiDropTarget(NULL)
	{  
		m_lRefCount = 1;  

		// init COM
		// 	if (S_OK != CoInitialize(NULL))  
		// 	{
		// 		m_piDropHelper = NULL;
		// 		m_bUseDnDHelper = false;
		// 		return;
		// 	}
		if (S_OK != OleInitialize(NULL))
		{
			return ;
		}

		// Create an instance of the shell DnD helper object.   
		if ( SUCCEEDED( CoCreateInstance ( CLSID_DragDropHelper, NULL,   
			CLSCTX_INPROC_SERVER,  
			IID_IDropTargetHelper,   
			(void**) &m_piDropHelper ) ))  
		{  
			m_bUseDnDHelper = true;  
			//m_piDropHelper->InitializeFromBitmap()
			//IDragSourceHelper::InitializeFromBitmap();
		}  
		else
		{
			m_bUseDnDHelper = false;
		}
	}  

	CDropTargetEx::~CDropTargetEx(void)  
	{  
		if (m_piDropHelper)  
		{  
			m_piDropHelper->Release();  
		}  
		m_bUseDnDHelper = false;  
		m_lRefCount = 0;  
		//CoUninitialize();  //卸载组件
		OleUninitialize();
	}  

	void CDropTargetEx::setEnableWnd(bool bDrop)
	{
		m_enableWnd = bDrop;
	}

	bool CDropTargetEx::IsEnableWnd()
	{
		return m_enableWnd;
	}

	bool CDropTargetEx::DragDropRegister(IDuiDropTarget* pDuiDropTarget,HWND hWnd,DWORD AcceptKeyState)  
	{  	
		if(!IsWindow(hWnd)) return false;  
		m_pDuiDropTarget = pDuiDropTarget;  
		HRESULT s = ::RegisterDragDrop (hWnd,this);  
		m_hWnd = hWnd;  
		if(SUCCEEDED(s))  
		{  
			m_dAcceptKeyState = AcceptKeyState;  
			return true; 
		}  
		else 
		{
			return false; 
		}    
	}  

	bool CDropTargetEx::DragDropRevoke(HWND hWnd)  
	{  
		if(!IsWindow(hWnd))return false;  

		HRESULT s = ::RevokeDragDrop(hWnd);  

		return SUCCEEDED(s);  
	}  

	HRESULT STDMETHODCALLTYPE CDropTargetEx::QueryInterface(REFIID riid, __RPC__deref_out void **ppvObject)  
	{  
		static const QITAB qit[] =  
		{  
			QITABENT(CDropTargetEx, IDropTarget),  
			{ 0 }  
		};  
		return QISearch(this, qit, riid, ppvObject);  
	}  

	ULONG STDMETHODCALLTYPE CDropTargetEx::AddRef()  
	{  
		return InterlockedIncrement(&m_lRefCount);  
	}  

	ULONG STDMETHODCALLTYPE CDropTargetEx::Release()  
	{  
		ULONG lRef = InterlockedDecrement(&m_lRefCount);  
		if (0 == lRef)  
		{  
			delete this;
			return 0;
		}  
		return m_lRefCount;  
	}  

	//进入   
	HRESULT STDMETHODCALLTYPE CDropTargetEx::DragEnter(__RPC__in_opt IDataObject *pDataObj, DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect)  
	{  
		if (pDataObj == NULL)
		{
			return S_FALSE;
		}
		if ( m_bUseDnDHelper )  
		{  
			if (IsEnableWnd())
			{
				m_piDropHelper->DragEnter (m_hWnd, pDataObj, (LPPOINT)&pt, *pdwEffect );
				if (m_pDuiDropTarget != NULL)
				{
					m_pDuiDropTarget->OnDragEnter((CDataObjectEx*)pDataObj,grfKeyState,pt,pdwEffect);
				}
			}
		}  
		return S_OK;  
	}  

	//移动   
	HRESULT STDMETHODCALLTYPE CDropTargetEx::DragOver(DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect)  
	{  
		if ( m_bUseDnDHelper )  
		{  
			if (IsEnableWnd())
			{
				m_piDropHelper->DragOver((LPPOINT)&pt, *pdwEffect); 

				if(m_pDuiDropTarget != NULL)
				{
					m_pDuiDropTarget->OnDragOver(grfKeyState,pt,pdwEffect);
				}
			}
		}  
		return S_OK;  
	}  

	//离开   
	HRESULT STDMETHODCALLTYPE CDropTargetEx::DragLeave()  
	{  
		if ( m_bUseDnDHelper )  
		{  
			if (IsEnableWnd())
			{
				m_piDropHelper->DragLeave(); 
				if(m_pDuiDropTarget != NULL)
				{
					m_pDuiDropTarget->OnDragLeave();
				}
			}
		}  
		return S_OK;  
	}  

	//释放   
	HRESULT STDMETHODCALLTYPE CDropTargetEx::Drop(__RPC__in_opt IDataObject *pDataObj, DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect)  
	{  
		if (m_bUseDnDHelper)
		{
			if (IsEnableWnd())
			{
				m_piDropHelper->Drop ( pDataObj,  (LPPOINT)&pt, *pdwEffect );  
				if(m_pDuiDropTarget != NULL)
				{
					m_pDuiDropTarget->OnDrop((CDataObjectEx*)pDataObj,grfKeyState,pt,pdwEffect);
				}
			}
		}

		return S_OK;  
	}

} // namespace DuiLib
