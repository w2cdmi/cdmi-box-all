#pragma once

#include "resource.h"
#include "OutlookAddin_i.h"
#include <atlstr.h>
#include <map>
#include "CommonDefine.h"

#include "MSADDNDR.tlh"
#include "MSO.tlh"
#include "MSOUTL.tlh"
#include "FM20.tlh"
//#import "libid:AC0714F2-3D04-11D1-AE7D-00A0C90F26F4" auto_rename auto_search raw_interfaces_only rename_namespace("AddinDesign")
//#import "libid:2DF8D04C-5BFA-101B-BDE5-00AA0044DE52" auto_rename auto_search raw_interfaces_only rename_namespace("Office")
//#import "libid:00062FFF-0000-0000-C000-000000000046" auto_rename auto_search raw_interfaces_only rename_namespace("Outlook")

#include "OutlookAddinFrame.h"

using namespace ATL;

namespace OneboxOutlookAddin
{
	class MailItemInspectorWrapper;

	class FormRegion
	{
	public:
		FormRegion():progressFrame_(NULL){}

		virtual ~FormRegion() {}

		HRESULT Init(MailItemInspectorWrapper* inspector, Outlook::_FormRegion* pFormRegion, const std::wstring& id);

		class FormRegionEventsSink : 
			public IDispEventSimpleImpl<4, FormRegionEventsSink, &__uuidof(Outlook::FormRegionEvents)>
		{
			typedef IDispEventSimpleImpl<4, FormRegionEventsSink, &__uuidof(Outlook::FormRegionEvents)> FormRegionEventsSinkImpl;
		public:
			BEGIN_SINK_MAP(FormRegionEventsSink)
				SINK_ENTRY_INFO(4, __uuidof(Outlook::FormRegionEvents), 0x0000f004, OnClose, &CloseParam)
			END_SINK_MAP()

		public:
			FormRegionEventsSink(FormRegion* formRegion)
				:init_(false)
				,formRegion_(formRegion) {}

			virtual ~FormRegionEventsSink() {}

			int32_t init();

			void release();

		private:
			void __stdcall OnClose();

		private:
			bool init_;
			static _ATL_FUNC_INFO CloseParam;
			FormRegion* formRegion_;
		};

		FUNC_DEFAULT_SET_GET(OutlookaAddinFrame*, progressFrame);

	private:
		CComQIPtr<Outlook::_FormRegion> spFormRegion_;
		OutlookaAddinFrame* progressFrame_;
	};

	class ItemEvents_10Sink : 
		public IDispEventSimpleImpl<3, ItemEvents_10Sink, &__uuidof(Outlook::ItemEvents_10)>
	{
		typedef IDispEventSimpleImpl<3, ItemEvents_10Sink, &__uuidof(Outlook::ItemEvents_10)> ItemEvents_10SinkImpl;
	public:
		BEGIN_SINK_MAP(ItemEvents_10Sink)
			SINK_ENTRY_INFO(3, __uuidof(Outlook::ItemEvents_10), 0x0000f004, OnClose, &CloseParam)
			SINK_ENTRY_INFO(3, __uuidof(Outlook::ItemEvents_10), 0x0000f005, OnSend, &SendParam)
		END_SINK_MAP()

	public:
		ItemEvents_10Sink(Outlook::_MailItem* mailItem, const std::wstring& id)
			:init_(false)
			,spMailItem_(mailItem)
			,id_(id) {}

		virtual ~ItemEvents_10Sink() {}

		int32_t init();

		void release();

	private:
		void __stdcall OnClose(BOOL* Cancel);

		void __stdcall OnSend(BOOL* Cancel);

	private:
		bool init_;
		std::wstring id_;
		static _ATL_FUNC_INFO CloseParam;
		static _ATL_FUNC_INFO SendParam;
		CComQIPtr<Outlook::_MailItem> spMailItem_;
	};

	class InspectorEvents_10Sink : 
		public IDispEventSimpleImpl<2, InspectorEvents_10Sink, &__uuidof(Outlook::InspectorEvents_10)>
	{
		typedef IDispEventSimpleImpl<2, InspectorEvents_10Sink, &__uuidof(Outlook::InspectorEvents_10)> InspectorEvents_10SinkImpl;
	public:
		BEGIN_SINK_MAP(InspectorEvents_10Sink)
			SINK_ENTRY_INFO(2, __uuidof(Outlook::InspectorEvents_10), 0x0000f008, OnClose, &CloseParam)
		END_SINK_MAP()

	public:
		InspectorEvents_10Sink(Outlook::_Inspector* inspector, const std::wstring& id)
			:init_(false)
			,spInspector_(inspector)
			,id_(id) {}

		virtual ~InspectorEvents_10Sink() {}

	public:
		int32_t init();

		void realse();

	private:
		void __stdcall OnClose();

	private:
		bool init_;
		std::wstring id_;
		static _ATL_FUNC_INFO CloseParam;
		CComQIPtr<Outlook::_Inspector> spInspector_;
	};

	class InspectorWrapper
	{
	public:
		InspectorWrapper(Outlook::_Inspector* inspector)
			:init_(false)
			,spInspector_(inspector)
			,id_(L"") {}

		virtual ~InspectorWrapper()
		{
			realse();
		}

		std::wstring id() const
		{
			return id_;
		}

		Outlook::_Inspector* Inspector()
		{
			return spInspector_;
		}

		virtual int32_t init();

		virtual void realse();

	protected:
		bool init_;
		CComQIPtr<Outlook::_Inspector> spInspector_;
		std::wstring id_;
	};

	typedef std::map<std::wstring, InspectorWrapper*> InspectorWrappers;

	class MailItemInspectorWrapper : public InspectorWrapper
	{
	public:
		MailItemInspectorWrapper(Outlook::_Inspector* inspector)
			:InspectorWrapper(inspector)
			,formRegion_(NULL) {}

		virtual ~MailItemInspectorWrapper() {}

		FUNC_DEFAULT_SET_GET(FormRegion*, formRegion);

	public:
		int32_t updateMailItemBody(const std::list<ShareLinkBodyItem>& shareLinkBodyItems);

	private:
		virtual int32_t init();

	private:
		FormRegion* formRegion_;
	};

	class InspectorsEventsSink :
		public IDispEventSimpleImpl<1, InspectorsEventsSink, &__uuidof(Outlook::InspectorsEvents)>
	{
		typedef IDispEventSimpleImpl<1, InspectorsEventsSink, &__uuidof(Outlook::InspectorsEvents)> InspectorsEventsSinkImpl;
	public:
		BEGIN_SINK_MAP(InspectorsEventsSink)
			SINK_ENTRY_INFO(1, __uuidof(Outlook::InspectorsEvents), 0x0000f001, OnNewInspector, &NewInspectorParam)
		END_SINK_MAP()

	public:
		InspectorsEventsSink(Outlook::_Inspectors* inspectors)
			:init_(false)
			,spInspectors_(inspectors)
		{
			init();
		}

		virtual ~InspectorsEventsSink()
		{
			realse();
		}

		int32_t init();

		void realse();

	private:
		void __stdcall OnNewInspector(Outlook::_Inspector* inspector);

	private:
		bool init_;
		static _ATL_FUNC_INFO NewInspectorParam;
		CComQIPtr<Outlook::_Inspectors> spInspectors_;
	};
}

typedef IDispatchImpl<AddinDesign::_IDTExtensibility2, &__uuidof(AddinDesign::_IDTExtensibility2), &__uuidof(AddinDesign::__AddInDesignerObjects), /* wMajor = */ 1> IDTImpl;
typedef IDispatchImpl<Office::IRibbonExtensibility, &__uuidof(Office::IRibbonExtensibility), &__uuidof(Office::__Office), /* wMajor = */ 2, /* wMinor = */ 5> RibbonImpl;
typedef IDispatchImpl<IRibbonCallback, &__uuidof(IRibbonCallback)> CallbackImpl;
typedef IDispatchImpl<Outlook::_FormRegionStartup, &__uuidof(Outlook::_FormRegionStartup), &__uuidof(Outlook::__Outlook), /* wMajor = */ 9> FormImpl;

class ATL_NO_VTABLE COutlookAddinImpl :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<COutlookAddinImpl, &CLSID_OutlookAddinImpl>,
	public IDispatchImpl<IOutlookAddinImpl, &IID_IOutlookAddinImpl, &LIBID_OutlookAddinLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
	public IDTImpl,
	public RibbonImpl,
	public CallbackImpl, 
	public FormImpl
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
		COM_INTERFACE_ENTRY(Outlook::_FormRegionStartup)
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

	// implemention of _FormRegionStartup interface
	STDMETHOD(GetFormRegionStorage)(BSTR FormRegionName, LPDISPATCH Item, long LCID, Outlook::OlFormRegionMode FormRegionMode, Outlook::OlFormRegionSize FormRegionSize, VARIANT* Storage);

	STDMETHOD(BeforeFormRegionShow)(Outlook::_FormRegion* FormRegion);

	STDMETHOD(GetFormRegionManifest)(BSTR FormRegionName, long LCID, VARIANT* Manifest);

	STDMETHOD(GetFormRegionIcon)(BSTR FormRegionName, long LCID, Outlook::OlFormRegionIcon Icon, VARIANT* Result);

private:
	HRESULT HrGetResource(int nId, LPCTSTR lpType, LPVOID* ppvResourceData, DWORD* pdwSizeInBytes);

	BSTR GetXMLResource(int nId);

	SAFEARRAY* GetOFSResource(int nId);

	HRESULT GetButtonId(IDispatch* idispach, CString& id);

	int32_t GetLanguageID();

public:
	static BOOL IsOneboxServiceNormal();

private:
	HWND GetParentHwnd();

private:
	CComQIPtr<Outlook::_Application> spApp_;
	std::auto_ptr<OneboxOutlookAddin::InspectorsEventsSink> inspectorsEventsSink_;
};

OBJECT_ENTRY_AUTO(__uuidof(OutlookAddinImpl), COutlookAddinImpl)
