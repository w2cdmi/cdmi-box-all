#pragma once

//#import "libid:00020813-0000-0000-C000-000000000046" auto_rename auto_search raw_interfaces_only rename_namespace("Excel")
//#import "libid:00020905-0000-0000-C000-000000000046" auto_rename auto_search raw_interfaces_only rename_namespace("Word")
//#import "libid:91493440-5A91-11CF-8700-00AA0060263B" auto_rename auto_search raw_interfaces_only rename_namespace("PowerPoint")

#include "resource.h"
#include "OfficeAddin_i.h"
#include "MSADDNDR.tlh"
#include "EXCEL.tlh"
#include "MSWORD.tlh"
#include "MSPPT.tlh"
#include <stdint.h>

using namespace ATL;

class ExcelEventsSink : 
	public IDispEventSimpleImpl<2, ExcelEventsSink, &__uuidof(Excel::AppEvents)>
{
	typedef IDispEventSimpleImpl<2, ExcelEventsSink, &__uuidof(Excel::AppEvents)> ExcelEventsSinkImpl;
public:
	BEGIN_SINK_MAP(ExcelEventsSink)
		SINK_ENTRY_INFO(2, __uuidof(Excel::AppEvents), 0x00000622, OnWorkbookBeforeClose, &WorkbookBeforeCloseParam)
	END_SINK_MAP()

public:
	ExcelEventsSink(Excel::_Application* application)
		:spApplication_(application)
		,init_(false)
	{
	}

	int32_t init();

	void release();

private:
	void __stdcall OnWorkbookBeforeClose(Excel::_Workbook *wb, VARIANT_BOOL* Cancel);

private:
	static _ATL_FUNC_INFO WorkbookBeforeCloseParam;
	CComQIPtr<Excel::_Application> spApplication_;
	bool init_;
};

class WordEventsSink : 
	public IDispEventSimpleImpl<3, WordEventsSink, &__uuidof(Word::ApplicationEvents4)>
{
	typedef IDispEventSimpleImpl<3, WordEventsSink, &__uuidof(Word::ApplicationEvents4)> WordEventsSinkImpl;
public:
	BEGIN_SINK_MAP(WordEventsSink)
		SINK_ENTRY_INFO(3, __uuidof(Word::ApplicationEvents4), 0x00000006, OnDocumentBeforeClose, &DocumentBeforeCloseParam)
	END_SINK_MAP()

public:
	WordEventsSink(Word::_Application* application)
		:spApplication_(application)
		,init_(false)
	{
	}

	int32_t init();

	void release();

private:
	void __stdcall OnDocumentBeforeClose(Word::_Document *doc, VARIANT_BOOL* Cancel);

private:
	static _ATL_FUNC_INFO DocumentBeforeCloseParam;
	CComQIPtr<Word::_Application> spApplication_;
	bool init_;
};

class PowerPointEventsSink : 
	public IDispEventSimpleImpl<1, PowerPointEventsSink, &__uuidof(PowerPoint::EApplication)>
{
	typedef IDispEventSimpleImpl<1, PowerPointEventsSink, &__uuidof(PowerPoint::EApplication)> PowerPointEventsSinkImpl;
public:
	BEGIN_SINK_MAP(PowerPointEventsSink)
		SINK_ENTRY_INFO(1, __uuidof(PowerPoint::EApplication), 2004, OnPresentationClose, &PresentationCloseParam)
	END_SINK_MAP()

public:
	PowerPointEventsSink(PowerPoint::_Application* application)
		:spApplication_(application)
		,init_(false)
	{
	}

	int32_t init();

	void release();

private:
	void __stdcall OnPresentationClose(PowerPoint::_Presentation* presentation);

private:
	static _ATL_FUNC_INFO PresentationCloseParam;
	CComQIPtr<PowerPoint::_Application> spApplication_;
	bool init_;
};

typedef IDispatchImpl<AddinDesign::_IDTExtensibility2, &__uuidof(AddinDesign::_IDTExtensibility2), &__uuidof(AddinDesign::__AddInDesignerObjects), /* wMajor = */ 1> IDTImpl;
typedef IDispatchImpl<Office::IRibbonExtensibility, &__uuidof(Office::IRibbonExtensibility), &__uuidof(Office::__Office), /* wMajor = */ 2/*,  wMinor = 5*/> RibbonImpl;
typedef IDispatchImpl<IRibbonCallback, &__uuidof(IRibbonCallback)> CallbackImpl;

class ATL_NO_VTABLE COfficeAddinImpl :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<COfficeAddinImpl, &CLSID_OfficeAddinImpl>,
	public IDispatchImpl<IOfficeAddinImpl, &IID_IOfficeAddinImpl, &LIBID_OfficeAddinLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
	public IDTImpl,
	public RibbonImpl,
	public CallbackImpl
{
public:
	COfficeAddinImpl(){};

	DECLARE_REGISTRY_RESOURCEID(IDR_OFFICEADDINIMPL)

	BEGIN_COM_MAP(COfficeAddinImpl)
		COM_INTERFACE_ENTRY(IOfficeAddinImpl)
		COM_INTERFACE_ENTRY(AddinDesign::_IDTExtensibility2)
		COM_INTERFACE_ENTRY(Office::IRibbonExtensibility)
		COM_INTERFACE_ENTRY(IRibbonCallback)
		COM_INTERFACE_ENTRY2(IDispatch, IRibbonCallback)
	END_COM_MAP()

	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct();

	void FinalRelease();

private:
	STDMETHOD(Invoke)(DISPID dispidMember, const IID &riid, LCID lcid, WORD wFlags, DISPPARAMS *pdispparams, VARIANT *pvarResult, EXCEPINFO *pexceptinfo, UINT *puArgErr);

	// implememtion of _IDTExtensibility2 interface
	STDMETHOD(OnConnection)(LPDISPATCH Application, AddinDesign::ext_ConnectMode ConnectMode, LPDISPATCH AddInInst, SAFEARRAY * * custom);

	STDMETHOD(OnDisconnection)(AddinDesign::ext_DisconnectMode RemoveMode, SAFEARRAY * * custom);

	STDMETHOD(OnAddInsUpdate)(SAFEARRAY * * custom);

	STDMETHOD(OnStartupComplete)(SAFEARRAY * * custom);

	STDMETHOD(OnBeginShutdown)(SAFEARRAY * * custom);

	// implememtion of IRibbonExtensibility interface
	STDMETHOD(GetCustomUI)(BSTR RibbonID, BSTR * RibbonXml);

	// implememtion of IRibbonCallback interface
	STDMETHOD(OnGetEnabled)(IDispatch* idispach, VARIANT_BOOL* pvarReturnedVal);

	STDMETHOD(OnButtonClicked)(IDispatch* idispach);

	STDMETHOD(OnLoadImage)(IDispatch* idispach, IPictureDisp** ppdispImage);

	STDMETHOD(OnGetLable)(IDispatch* idispach, BSTR *pbstrReturnedVal);

	STDMETHOD(OnGetDiscription)(IDispatch* idispach, BSTR *pbstrReturnedVal);

	STDMETHOD(OnGetKeyTip)(IDispatch* idispach, BSTR *pbstrReturnedVal);

private:
	HRESULT HrGetResource(int nId, LPCTSTR lpType, LPVOID* ppvResourceData, DWORD* pdwSizeInBytes);

	BSTR GetXMLResource(int nId);

	HRESULT GetButtonId(IDispatch* idispach, CString& id);

	CString GetFilePath();

public:
	static CString GetFilePath(Word::_Document *doc);

	static CString GetFilePath(Excel::_Workbook *wb);

	static CString GetFilePath(PowerPoint::_Presentation *presentation);

	static BOOL IsOneboxServiceNormal();

	HWND	GetParentHwnd();
private:
	CComQIPtr<Word::_Application> spWordApplication_;
	CComQIPtr<Excel::_Application> spExcelApplication_;
	CComQIPtr<PowerPoint::_Application> spPPTApplication_;

	PowerPointEventsSink *powerPointEventsSink_;
	WordEventsSink *wordEventsSink_;
	ExcelEventsSink *excelEventsSink_;
};

OBJECT_ENTRY_AUTO(__uuidof(OfficeAddinImpl), COfficeAddinImpl)
