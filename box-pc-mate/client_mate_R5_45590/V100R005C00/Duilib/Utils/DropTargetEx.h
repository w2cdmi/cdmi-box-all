#pragma  once

#include "DataObjectEx.h"

namespace DuiLib 
{
	class IDuiDropTarget
	{
	public:  
		virtual HRESULT OnDragEnter(CDataObjectEx *pDataObj, DWORD grfKeyState, POINTL ptl,DWORD *pdwEffect) = 0;  
		virtual HRESULT OnDragOver(DWORD grfKeyState, POINTL pt,DWORD *pdwEffect) = 0;  
		virtual HRESULT OnDragLeave() = 0;  
		virtual HRESULT OnDrop(CDataObjectEx *pDataObj, DWORD grfKeyState, POINTL pt, DWORD *pdwEffect) = 0; 
	};

	class CDropTargetEx : public IDropTarget
	{  
	public:  
		CDropTargetEx(bool bDrop);  
		void setEnableWnd(bool bDrop);
		bool IsEnableWnd();
		bool DragDropRegister(IDuiDropTarget* pDuiDropTarget,HWND hWnd,DWORD AcceptKeyState=MK_LBUTTON);  
		bool DragDropRevoke(HWND hWnd);  
		HRESULT STDMETHODCALLTYPE QueryInterface(REFIID riid, __RPC__deref_out void **ppvObject);  
		ULONG STDMETHODCALLTYPE AddRef();  
		ULONG STDMETHODCALLTYPE Release();  
		//����   
		HRESULT STDMETHODCALLTYPE DragEnter(__RPC__in_opt IDataObject *pDataObj, DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect);  
		//�ƶ�   
		HRESULT STDMETHODCALLTYPE DragOver(DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect);  
		//�뿪   
		HRESULT STDMETHODCALLTYPE DragLeave();  
		//�ͷ�   
		HRESULT STDMETHODCALLTYPE Drop(__RPC__in_opt IDataObject *pDataObj, DWORD grfKeyState, POINTL pt, __RPC__inout DWORD *pdwEffect);  

	private:  
		~CDropTargetEx(void);  
		HWND m_hWnd;  
		IDropTargetHelper* m_piDropHelper;  
		bool m_bUseDnDHelper;  
		IDuiDropTarget* m_pDuiDropTarget; 
		DWORD m_dAcceptKeyState;  
		ULONG m_lRefCount;  
		bool m_enableWnd;  //�����Ƿ�֧����ק
	};

} // namespace DuiLib
