#pragma once

#include "resource.h"
#include "OutlookAddin_i.h"
#include <atlstr.h>
#include <list>
#include <map>

#include "MSADDNDR.tlh"
#include "MSO.tlh"
#include "MSOUTL.tlh"
//#import "libid:AC0714F2-3D04-11D1-AE7D-00A0C90F26F4" auto_rename auto_search raw_interfaces_only rename_namespace("AddinDesign")
//#import "libid:2DF8D04C-5BFA-101B-BDE5-00AA0044DE52" auto_rename auto_search raw_interfaces_only rename_namespace("Office")
//#import "libid:00062FFF-0000-0000-C000-000000000046" auto_rename auto_search raw_interfaces_only rename_namespace("Outlook")

using namespace ATL;

class MailItemEventImpl;
class COutlookAddinImpl;

typedef IDispatchImpl<AddinDesign::_IDTExtensibility2, &__uuidof(AddinDesign::_IDTExtensibility2), &__uuidof(AddinDesign::__AddInDesignerObjects), /* wMajor = */ 1> IDTImpl;
typedef IDispatchImpl<Office::IRibbonExtensibility, &__uuidof(Office::IRibbonExtensibility), &__uuidof(Office::__Office), /* wMajor = */ 2, /* wMinor = */ 5> RibbonImpl;
typedef IDispatchImpl<IRibbonCallback, &__uuidof(IRibbonCallback)> CallbackImpl;

//typedef IDispEventSimpleImpl<1, MailItemEventImpl, &__uuidof(Outlook::ApplicationEvents)> ApplicationStartEventSink;
//typedef IDispEventSimpleImpl<2, MailItemEventImpl, &__uuidof(Outlook::ApplicationEvents)> ApplicationQuitEventSink;
//typedef IDispEventSimpleImpl<3, MailItemEventImpl, &__uuidof(Outlook::ApplicationEvents)> ApplicationItemLoadEventSink;
typedef IDispEventSimpleImpl<4, MailItemEventImpl, &__uuidof(Outlook::ItemEvents)> MailItemSendEventSink;
typedef IDispEventSimpleImpl<5, MailItemEventImpl, &__uuidof(Outlook::ItemEvents)> MailItemCloseEventSink;

class MailItemEventImpl : 
	public MailItemSendEventSink, 
	public MailItemCloseEventSink
{
public:
	BEGIN_SINK_MAP(MailItemEventImpl)
		SINK_ENTRY_INFO(4, __uuidof(Outlook::ItemEvents), 0xf005, OnItemSend, &ItemSendCloseFuncInfo)
		SINK_ENTRY_INFO(5, __uuidof(Outlook::ItemEvents), 0xf004, OnItemClose, &ItemSendCloseFuncInfo)
	END_SINK_MAP()

	MailItemEventImpl(COutlookAddinImpl* parent, Outlook::_Application* application, Outlook::_MailItem* mailItem, const std::wstring& customMailItemId);

	HRESULT Init();

	int AddMailItemAttachements(std::list<std::wstring>& attachements);

private:
	void __stdcall OnItemSend(BOOL* bCancel);

	void __stdcall OnItemClose(BOOL* bCancel);

private:
	std::wstring GetRemoteParentPath();

	HRESULT UpdateMailItemAttachementLinks(const std::map<std::wstring, std::wstring>& attachementsLinks);

private:
	static _ATL_FUNC_INFO ItemSendCloseFuncInfo;

	COutlookAddinImpl* parent_;
	CComQIPtr<Outlook::_Application> spApplication_;
	CComQIPtr<Outlook::_MailItem> spMailItem_;
	std::wstring customMailItemId_;
	std::list<std::wstring> attachements_;
	std::wstring remoteParentPath_;
};

class ATL_NO_VTABLE COutlookAddinImpl :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<COutlookAddinImpl, &CLSID_OutlookAddinImpl>,
	public IDispatchImpl<IOutlookAddinImpl, &IID_IOutlookAddinImpl, &LIBID_OutlookAddinLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
	public IDTImpl,
	public RibbonImpl,
	public CallbackImpl
{
public:
	COutlookAddinImpl()
	{
	}

	DECLARE_REGISTRY_RESOURCEID(IDR_OUTLOOKADDINIMPL)

	BEGIN_COM_MAP(COutlookAddinImpl)
		COM_INTERFACE_ENTRY(IOutlookAddinImpl)
		COM_INTERFACE_ENTRY2(IDispatch, IRibbonCallback)
		COM_INTERFACE_ENTRY(AddinDesign::_IDTExtensibility2)
		COM_INTERFACE_ENTRY(Office::IRibbonExtensibility)
		COM_INTERFACE_ENTRY(IRibbonCallback)
	END_COM_MAP()

	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

	void RemoveMailItemEvent(const std::wstring customMailItemId)
	{
		std::map<std::wstring, MailItemEventImpl*>::iterator it = mailItemEvents_.find(customMailItemId);
		if (it != mailItemEvents_.end())
		{
			/*if (NULL != it->second)
			{
			delete it->second;
			it->second = NULL;
			}*/
			mailItemEvents_.erase(it);
		}
	}

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

public:
	static BOOL IsOneboxServiceNormal();

private:
	CComQIPtr<Outlook::_Application> spApp_;
	std::map<std::wstring, MailItemEventImpl*> mailItemEvents_;
};

OBJECT_ENTRY_AUTO(__uuidof(OutlookAddinImpl), COutlookAddinImpl)
