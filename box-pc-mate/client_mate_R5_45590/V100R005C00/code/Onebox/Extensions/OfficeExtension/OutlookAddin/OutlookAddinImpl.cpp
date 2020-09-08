#include "stdafx.h"
#include "OutlookAddinImpl.h"
#include <windows.h>
#include <memory>
#include <atlsync.h>
#include "CommonDefine.h"
#include "OutlookAddinThriftClient.h"
#include "Utility.h"
#include "Global.h"
#include "ShellCommonFileDialog.h"

#pragma comment(lib, "Msimg32.lib")

using namespace SD;

static WCHAR* USER_DEFINE_NAME = L"TransTaskGroupId";

static const WCHAR* BTN_ADDATTACHES_ID = L"ButtonAddAttaches";
//static const WCHAR* BTN_SHOW_TASKS_ID = L"ButtonShowTasks";

_ATL_FUNC_INFO OneboxOutlookAddin::FormRegion::FormRegionEventsSink::CloseParam = {CC_STDCALL, VT_EMPTY, 0, 0};
_ATL_FUNC_INFO OneboxOutlookAddin::ItemEvents_10Sink::CloseParam = {CC_STDCALL, VT_EMPTY, 1, {VT_BOOL|VT_BYREF}};
_ATL_FUNC_INFO OneboxOutlookAddin::ItemEvents_10Sink::SendParam = {CC_STDCALL, VT_EMPTY, 1, {VT_BOOL|VT_BYREF}};
_ATL_FUNC_INFO OneboxOutlookAddin::InspectorEvents_10Sink::CloseParam = {CC_STDCALL, VT_EMPTY, 0, 0};
_ATL_FUNC_INFO OneboxOutlookAddin::InspectorsEventsSink::NewInspectorParam = {CC_STDCALL, VT_EMPTY, 1, {VT_DISPATCH}};

int32_t languageID_ = INVALID_LANG_ID;
OneboxOutlookAddin::InspectorWrappers inspectorWrappers_;

int32_t OneboxOutlookAddin::FormRegion::FormRegionEventsSink::init()
{
	if (init_)
	{
		return S_OK;
	}
	if (NULL == formRegion_)
	{
		return S_ERROR;
	}
	if (FAILED(FormRegionEventsSinkImpl::DispEventAdvise(formRegion_->spFormRegion_)))
	{
		return S_ERROR;
	}

	init_ = true;

	return S_OK;
}

void OneboxOutlookAddin::FormRegion::FormRegionEventsSink::release()
{
	if (NULL == formRegion_ || !init_)
	{
		return;
	}
	FormRegionEventsSinkImpl::DispEventUnadvise(formRegion_->spFormRegion_);
	init_ = false;
}

void __stdcall OneboxOutlookAddin::FormRegion::FormRegionEventsSink::OnClose()
{
	release();
	delete this;
}

HRESULT OneboxOutlookAddin::FormRegion::Init(MailItemInspectorWrapper* inspector, Outlook::_FormRegion* pFormRegion, const std::wstring& id)
{
	spFormRegion_ = pFormRegion;
	FormRegionEventsSink *sink = new FormRegionEventsSink(this);
	if (NULL == sink)
	{
		return S_ERROR;
	}
	if (S_OK != sink->init())
	{
		return S_ERROR;
	}

	HRESULT hr = S_ERROR;
	if (NULL == spFormRegion_)
	{
		return S_ERROR;
	}
	CComQIPtr<IDispatch> spIDispatch;
	hr = spFormRegion_->get_Form(&spIDispatch);
	if (FAILED(hr) || NULL == spIDispatch)
	{
		return hr;
	}
	CComQIPtr<Forms::_UserForm> spUserForm(spIDispatch);
	if (NULL == spUserForm)
	{
		return S_ERROR;
	}
	CComQIPtr<Forms::Controls> spControls;
	hr = spUserForm->get_Controls(&spControls);
	if (FAILED(hr) || NULL == spControls)
	{
		return hr;
	}

	CComBSTR bstrWBName(L"ProgressContainer");
	CComQIPtr<Forms::IControl> container;
	hr = spControls->_GetItemByName(bstrWBName.Detach(), &container);
	if (FAILED(hr) || NULL == container)
	{
		return hr;
	}

	HWND wnd = 0;
	hr = container->_GethWnd((int*)&wnd);
	if (FAILED(hr) || 0 == wnd)
	{
		return S_ERROR;
	}

	progressFrame_ = new OutlookaAddinFrame(id, boost::bind(&MailItemInspectorWrapper::updateMailItemBody, inspector, _1));
	if (NULL == progressFrame_)
	{
		return S_ERROR;
	}
	progressFrame_->Create(wnd, _T("OutlookaAddinFrame"), UI_WNDSTYLE_CHILD, 0, 0, 0, 0, 0);
	progressFrame_->ShowWindow();

	return S_OK;
}

int32_t OneboxOutlookAddin::ItemEvents_10Sink::init()
{
	if (init_)
	{
		return S_OK;
	}
	if (NULL == spMailItem_)
	{
		return S_ERROR;
	}

	// 1. set the id to the MailItem custom propterty
	// 2. update the MailItem body, <div id={id}></div>
	if (NULL == spMailItem_)
	{
		return S_ERROR;
	}
	VARIANT_BOOL sent = FALSE;
	HRESULT hr = spMailItem_->get_Sent(&sent);
	if (FAILED(hr))
	{
		return hr;
	}
	if (sent)
	{
		return S_OK;
	}
	CComQIPtr<Outlook::UserProperties> spUserProperties;
	hr = spMailItem_->get_UserProperties(&spUserProperties);
	if (FAILED(hr) || NULL == spUserProperties)
	{
		return hr;
	}
	CComQIPtr<Outlook::UserProperty> spUserProperty;
	BSTR userDefineName = SysAllocString(USER_DEFINE_NAME);
	if (!userDefineName)
	{
		return S_ERROR;
	}
	hr = spUserProperties->Find(userDefineName, CComVariant(TRUE), &spUserProperty);
	if (FAILED(hr) || NULL == spUserProperty)
	{
		hr = spUserProperties->Add(userDefineName, Outlook::olText, CComVariant(FALSE), CComVariant(1), &spUserProperty);
		if (FAILED(hr) || NULL == spUserProperty)
		{
			SysFreeString(userDefineName);
			return hr;
		}
		hr = spUserProperty->put_Value(CComVariant(id_.c_str()));
		if (FAILED(hr))
		{
			SysFreeString(userDefineName);
			return hr;
		}
	}

	if (FAILED(ItemEvents_10SinkImpl::DispEventAdvise(spMailItem_)))
	{
		SysFreeString(userDefineName);
		return S_ERROR;
	}

	init_ = true;
	SysFreeString(userDefineName);
	return S_OK;
}

void OneboxOutlookAddin::ItemEvents_10Sink::release()
{
	if (NULL == spMailItem_)
	{
		return;
	}
	if (!init_)
	{
		return;
	}
	ItemEvents_10SinkImpl::DispEventUnadvise(spMailItem_);
	init_ = false;
}

void __stdcall OneboxOutlookAddin::ItemEvents_10Sink::OnClose(BOOL* Cancel)
{
	if (COutlookAddinImpl::IsOneboxServiceNormal())
	{
		InspectorWrappers::iterator it = inspectorWrappers_.find(id_);
		if (it == inspectorWrappers_.end())
		{
			return;
		}
		MailItemInspectorWrapper* inspector = static_cast<MailItemInspectorWrapper*>(it->second);
		if (NULL == inspector)
		{
			return;
		}
		if (!inspector->formRegion()->progressFrame()->isComplete())
		{
			if (IDCANCEL == ::MessageBox(NULL, GetMessageFromIniFile(L"outlook_msg_close_notice").c_str(), 
				L"Onebox", MB_OKCANCEL))
			{
				*Cancel = TRUE;
				return;
			}
		}
	}	

	release();
	delete this;
}

void __stdcall OneboxOutlookAddin::ItemEvents_10Sink::OnSend(BOOL* Cancel)
{
	if (COutlookAddinImpl::IsOneboxServiceNormal())
	{
		InspectorWrappers::iterator it = inspectorWrappers_.find(id_);
		if (it == inspectorWrappers_.end())
		{
			return;
		}
		MailItemInspectorWrapper* inspector = static_cast<MailItemInspectorWrapper*>(it->second);
		if (NULL == inspector)
		{
			return;
		}
		if (!inspector->formRegion()->progressFrame()->isComplete())
		{
			if (IDCANCEL == ::MessageBox(NULL, GetMessageFromIniFile(L"outlook_msg_send_notice").c_str(), 
				L"Onebox", MB_OKCANCEL))
			{
				*Cancel = TRUE;
				return;
			}
		}
		inspector->formRegion()->progressFrame()->emailSendEvent();
	}
}
		
int32_t OneboxOutlookAddin::InspectorEvents_10Sink::init()
{
	if (init_)
	{
		return S_OK;
	}
	if (NULL == spInspector_)
	{
		return S_ERROR;
	}
	if (FAILED(InspectorEvents_10SinkImpl::DispEventAdvise(spInspector_)))
	{
		return S_ERROR;
	}

	init_ = true;

	return S_OK;
}

void OneboxOutlookAddin::InspectorEvents_10Sink::realse()
{
	if (NULL == spInspector_)
	{
		return;
	}
	if (!init_)
	{
		return;
	}
	// here to release the InsepectorWrapper memory and remove it from the global Inspector manager
	InspectorEvents_10SinkImpl::DispEventUnadvise(spInspector_);
	InspectorWrappers::iterator it = inspectorWrappers_.find(id_);
	if (it != inspectorWrappers_.end())
	{
		InspectorWrapper *inspectorWrapper = inspectorWrappers_[id_];
		if (NULL != inspectorWrapper)
		{
			MailItemInspectorWrapper* mailItemInspectorWrapper = static_cast<MailItemInspectorWrapper*>(inspectorWrapper);
			if (NULL != mailItemInspectorWrapper)
			{
				delete mailItemInspectorWrapper->formRegion();
				mailItemInspectorWrapper->formRegion(NULL);
			}
			delete inspectorWrapper;
			inspectorWrapper = NULL;
		}
		inspectorWrappers_.erase(it);
	}

	init_ = false;
}

void __stdcall OneboxOutlookAddin::InspectorEvents_10Sink::OnClose()
{
	CComQIPtr<IDispatch> spDispatch;
	HRESULT hr = spInspector_->get_CurrentItem(&spDispatch);
	if (SUCCEEDED(hr))
	{
		CComQIPtr<Outlook::_MailItem> spMailItem(spDispatch);
		if (NULL != spMailItem)
		{
			VARIANT_BOOL saved;
			hr = spMailItem->get_Saved(&saved);
			if (SUCCEEDED(hr) && !saved)
			{
				InspectorWrappers::iterator it = inspectorWrappers_.find(id_);
				if (it != inspectorWrappers_.end())
				{
					MailItemInspectorWrapper* inspector = static_cast<MailItemInspectorWrapper*>(it->second);
					if (NULL != inspector && COutlookAddinImpl::IsOneboxServiceNormal())
					{
						inspector->formRegion()->progressFrame()->emailDeleteEvent();
					}
				}
			}
		}
	}
	realse();
	delete this;
}

int32_t OneboxOutlookAddin::InspectorWrapper::init()
{
	if (init_)
	{
		return S_OK;
	}
	if (NULL == spInspector_)
	{
		return S_ERROR;
	}

	if (id_.empty())
	{
		id_ = Utility::String::gen_uuid();
		if (id_.empty())
		{
			return S_ERROR;
		}
	}	

	InspectorEvents_10Sink *sink = new InspectorEvents_10Sink(spInspector_, id_);
	if (NULL == sink)
	{
		return S_ERROR;
	}
	if (S_OK != sink->init())
	{
		delete sink;
		sink = NULL;
		return S_ERROR;
	}

	init_ = true;

	return S_OK;
}

void OneboxOutlookAddin::InspectorWrapper::realse()
{
	init_ = false;
}

int32_t OneboxOutlookAddin::MailItemInspectorWrapper::updateMailItemBody(const std::list<ShareLinkBodyItem>& shareLinkBodyItems)
{
	/*if (shareLinkBodyItems.empty())
	{
		return S_OK;
	}*/
	CComQIPtr<IDispatch> spDispatch;
	HRESULT hr = spInspector_->get_CurrentItem(&spDispatch);
	if (FAILED(hr) || NULL == spDispatch)
	{
		return hr;
	}
	CComQIPtr<Outlook::_MailItem> spMailItem(spDispatch);
	if (NULL == spMailItem)
	{
		return S_ERROR;
	}
	CComBSTR htmlBody;
	hr = spMailItem->get_HTMLBody(&htmlBody);
	if (FAILED(hr))
	{
		return hr;
	}

	std::wstring t = htmlBody;
	std::wstring::size_type pb = t.find(L"<body ");
	if (pb == std::wstring::npos)
	{
		pb = t.find(L"<BODY ");
		if (pb == std::wstring::npos)
		{
			pb = t.find(L"<body>");
			if (pb == std::wstring::npos)
			{
				pb = t.find(L"<BODY>");
				if (pb == std::wstring::npos)
				{
					return RT_ERROR;
				}
			}
		}
	}
	std::wstring::size_type pe = t.find(L">", pb);
	std::wstring s = t.substr(0, pe+1);
	std::wstring::size_type tmpPos = t.find(L"<div id=" + id_, pe);
	if (std::wstring::npos != tmpPos)
	{
		s = t.substr(0, pe+1);
		pe = t.find(L"</div>", tmpPos)+sizeof("</div>") - 2;
	}
	std::wstring body = L"<div id=" + id_ + L">";
	if (!shareLinkBodyItems.empty())
	{
		body += L"Onebox attachements links:<br>";
		for (std::list<ShareLinkBodyItem>::const_iterator it = shareLinkBodyItems.begin(); 
			it != shareLinkBodyItems.end(); ++it)
		{
			body += std::wstring(Utility::FS::get_file_name(it->path)+L": " + it->shareLink + L"<br>").c_str();
		}
	}	
	body += L"</div>";
	s += body;
	s += t.substr(pe+1);

#pragma warning(push)
#pragma warning(disable:4267)
	CComBSTR temp(s.length(), (LPCOLESTR)s.c_str());
#pragma warning(pop)
	hr = spMailItem->put_HTMLBody(temp);
	if (FAILED(hr))
	{
		return hr;
	}
	return S_OK;
}

int32_t OneboxOutlookAddin::MailItemInspectorWrapper::init()
{
	CComQIPtr<IDispatch> spIDispatch;
	HRESULT hr = spInspector_->get_CurrentItem(&spIDispatch);
	if (FAILED(hr) || NULL == spIDispatch)
	{
		return S_ERROR;
	}
	// the dispatch may be MailItem, ContactItem, PostItem or TaskItem
	// here, we only handle MailItem
	CComQIPtr<Outlook::_MailItem> spMailItem(spIDispatch);

	if (NULL == spMailItem)
	{
		return S_OK;
	}

	CComQIPtr<Outlook::UserProperties> spUserProperties;
	hr = spMailItem->get_UserProperties(&spUserProperties);
	if (FAILED(hr) || NULL == spUserProperties)
	{
		return hr;
	}
	CComQIPtr<Outlook::UserProperty> spUserProperty;
	BSTR useDefineName = SysAllocString(USER_DEFINE_NAME); 
	if (!useDefineName)
	{
		return S_ERROR;
	}
	hr = spUserProperties->Find(useDefineName, CComVariant(TRUE), &spUserProperty);
	SysFreeString(useDefineName);
	if (SUCCEEDED(hr) && NULL != spUserProperty)
	{
		CComVariant temp;
		hr = spUserProperty->get_Value(&temp);
		if (FAILED(hr))
		{
			return hr;
		}
		id_ = std::wstring((BSTR)temp.puiVal);
		if (id_.empty())
		{
			return S_ERROR;
		}
	}	

	if (S_OK != InspectorWrapper::init())
	{
		return S_ERROR;
	}

	ItemEvents_10Sink *sink = new ItemEvents_10Sink(spMailItem, id_);
	if (NULL == sink)
	{
		InspectorWrapper::realse();
		return S_ERROR;
	}
	if (S_OK != sink->init())
	{
		InspectorWrapper::realse();
		delete sink;
		sink = NULL;
		return S_ERROR;
	}

	return S_OK;
}

int32_t OneboxOutlookAddin::InspectorsEventsSink::init()
{
	if (NULL == spInspectors_)
	{
		return S_ERROR;
	}
	if (FAILED(InspectorsEventsSinkImpl::DispEventAdvise(spInspectors_)))
	{
		return S_ERROR;
	}

	init_ = true;

	return S_OK;
}

void OneboxOutlookAddin::InspectorsEventsSink::realse()
{
	if (NULL == spInspectors_)
	{
		return;
	}
	if (!init_)
	{
		return;
	}
	InspectorsEventsSinkImpl::DispEventUnadvise(spInspectors_);
	init_ = false;
}

void __stdcall OneboxOutlookAddin::InspectorsEventsSink::OnNewInspector(Outlook::_Inspector* inspector)
{
	InspectorWrapper *inspectorWrapper = new MailItemInspectorWrapper(inspector);
	if (NULL == inspectorWrapper)
	{
		return;
	}
	if (S_OK != inspectorWrapper->init())
	{
		delete inspectorWrapper;
		inspectorWrapper = NULL;
		return;
	}
	// add to global Inspector manager
	inspectorWrappers_.insert(std::make_pair(inspectorWrapper->id(), inspectorWrapper));
}

HRESULT COutlookAddinImpl::FinalConstruct()
{
	languageID_ = INVALID_LANG_ID;
	return S_OK;
}

void COutlookAddinImpl::FinalRelease()
{
	
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::Invoke(DISPID dispidMember, 
				  const IID &riid, 
				  LCID lcid, 
				  WORD wFlags, 
				  DISPPARAMS *pdispparams, 
				  VARIANT *pvarResult, 
				  EXCEPINFO *pexceptinfo, 
				  UINT *puArgErr)
{
	HRESULT hr=DISP_E_MEMBERNOTFOUND;
	if(dispidMember >= 42 && dispidMember <= 47)
	{
		hr  = CallbackImpl::Invoke(dispidMember, 
			riid, 
			lcid, 
			wFlags,       
			pdispparams, 
			pvarResult, 
			pexceptinfo, 
			puArgErr);
	}
	if (DISP_E_MEMBERNOTFOUND == hr)
		hr = IDTImpl::Invoke(dispidMember, 
		riid, 
		lcid, 
		wFlags, 
		pdispparams, 
		pvarResult, 
		pexceptinfo, 
		puArgErr);
	if (DISP_E_MEMBERNOTFOUND == hr)
		hr = FormImpl::Invoke(dispidMember, 
		riid, 
		lcid, 
		wFlags, 
		pdispparams, 
		pvarResult, 
		pexceptinfo, 
		puArgErr);
	return hr;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnConnection(LPDISPATCH Application, 
														  AddinDesign::ext_ConnectMode ConnectMode, 
														  LPDISPATCH AddInInst, 
														  SAFEARRAY * * custom)
{
	spApp_ = CComQIPtr<Outlook::_Application>(Application);
	if (NULL == spApp_)
	{
		return S_OK;
	}
	CComQIPtr<Outlook::_Inspectors> spInspectors;
	HRESULT hr = spApp_->get_Inspectors(&spInspectors);
	if (FAILED(hr))
	{
		return hr;
	}
	inspectorsEventsSink_.reset(new OneboxOutlookAddin::InspectorsEventsSink(spInspectors));

	// init language
	languageID_ = GetLanguageID();

	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnDisconnection(AddinDesign::ext_DisconnectMode RemoveMode, SAFEARRAY * * custom)
{
	inspectorsEventsSink_.reset(NULL);
	spApp_.Release();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnAddInsUpdate(SAFEARRAY * * custom)
{
	return E_NOTIMPL;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnStartupComplete(SAFEARRAY * * custom)
{
	return E_NOTIMPL;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnBeginShutdown(SAFEARRAY * * custom)
{
	return E_NOTIMPL;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::GetCustomUI(BSTR RibbonID, BSTR * RibbonXml)
{
	if(!RibbonXml)
		return E_POINTER;
	*RibbonXml = GetXMLResource(IDR_XML1);
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnGetEnabled(IDispatch* idispach, VARIANT_BOOL* pvarReturnedVal)
{
	if (NULL == pvarReturnedVal)
	{
		return S_OK;
	}
	*pvarReturnedVal = IsOneboxServiceNormal();
	return S_OK;
}

HWND COutlookAddinImpl::GetParentHwnd()
{
	HWND hwndPointNow = NULL;
	POINT pNow = {0,0};
	if (GetCursorPos(&pNow))
	{
		hwndPointNow = WindowFromPoint(pNow); 
	}

	return hwndPointNow;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnButtonClicked(IDispatch* idispach)
{
	if (NULL == spApp_)
	{
		return S_OK;
	}
	if (!IsOneboxServiceNormal())
	{
		MessageBox(NULL, GetMessageFromIniFile(L"outlook_msg_service_abnormal").c_str(), L"Onebox", MB_OK|MB_ICONWARNING);
		return S_OK;
	}

	CString id = L"";
	HRESULT hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}

	if (id == BTN_ADDATTACHES_ID)
	{
		// get the current custom property id
		CComPtr<Outlook::_Inspector> spInspector;
		HRESULT hr = spApp_->ActiveInspector(&spInspector);
		if (FAILED(hr) || NULL == spInspector)
		{
			return hr;
		}
		CComQIPtr<IDispatch> spDispatch;
		hr = spInspector->get_CurrentItem(&spDispatch);
		if (FAILED(hr) || NULL == spDispatch)
		{
			return hr;
		}
		CComQIPtr<Outlook::_MailItem> spMailItem(spDispatch);
		if (NULL == spMailItem)
		{
			return S_OK;
		}
		CComQIPtr<Outlook::UserProperties> spUserProperties;
		hr = spMailItem->get_UserProperties(&spUserProperties);
		if (FAILED(hr) || NULL == spUserProperties)
		{
			return hr;
		}
		CComQIPtr<Outlook::UserProperty> spUserProperty;
		BSTR userDefineName = SysAllocString(USER_DEFINE_NAME);
		if (!userDefineName)
		{
			return S_ERROR;
		}
		hr = spUserProperties->Find(userDefineName, CComVariant(TRUE), &spUserProperty);
		SysFreeString(userDefineName);
		if (FAILED(hr) || NULL == spUserProperty)
		{
			return hr;
		}
		CComVariant temp;
		hr = spUserProperty->get_Value(&temp);
		if (FAILED(hr))
		{
			return hr;
		}
		std::wstring id = std::wstring((BSTR)temp.puiVal);
		if (id.empty())
		{
			return S_ERROR;
		}

		OneboxOutlookAddin::InspectorWrappers::iterator it = inspectorWrappers_.find(id);
		assert(inspectorWrappers_.end() != it);

		ShellCommonFileDialogParam param(OpenFiles);
		param.parent = GetParentHwnd();
		ShellCommonFileDialog fileDialog(param);
		ShellCommonFileDialogResult results;
		if (fileDialog.getResults(results))
		{
			OutlookaAddinFrame* progressFrame = static_cast<OneboxOutlookAddin::MailItemInspectorWrapper*>(it->second)->formRegion()->progressFrame();
			assert(NULL != progressFrame);
			if (results.empty())
			{
				return S_ERROR;
			}
			std::list<std::wstring> paths;
			for(ShellCommonFileDialogResult::const_iterator it = results.begin(); it != results.end(); ++it)
			{
				paths.push_back(*it);
			}
			if (RT_OK != progressFrame->addTask(paths))
			{
				::MessageBox(NULL, GetMessageFromIniFile(L"outlook_msg_add_attachements_failed").c_str(), L"Onebox", MB_OK|MB_ICONERROR);
				return S_ERROR;
			}
		}
	}
	/*else if (id == BTN_SHOW_TASKS_ID)
	{
	::MessageBox(NULL, L"show trans task progress", L"OneboxTest", MB_OK);
	}*/
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnLoadImage(IDispatch* idispach, IPictureDisp** ppdispImage)
{
	if (NULL == ppdispImage)
	{
		return S_OK;
	}

	HRESULT hr = S_ERROR;
	PICTDESC pd;
	IPictureDisp* pic = NULL;
	HICON hIcon = NULL;

	pd.cbSizeofstruct  = sizeof(PICTDESC);
	pd.picType = PICTYPE_BITMAP;

	CString id = L"";
	hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (id == BTN_ADDATTACHES_ID)
	{
		hIcon =(HICON)::LoadImage(_AtlBaseModule.GetModuleInstance(), 
			MAKEINTRESOURCE(IDI_ICON1), 
			IMAGE_ICON, 32, 32, 0);
	}
	/*else if (id == BTN_SHOW_TASKS_ID)
	{
	hIcon =(HICON)::LoadImage(_AtlBaseModule.GetModuleInstance(), 
	MAKEINTRESOURCE(IDI_ICON2), 
	IMAGE_ICON, 32, 32, 0);
	}*/

	if (NULL == hIcon)
	{
		return HRESULT_FROM_WIN32(GetLastError());
	}
	ICONINFO iconinfo={TRUE,0,0,NULL,NULL};
	GetIconInfo(hIcon, &iconinfo);
	DeleteObject(hIcon);
	pd.bmp.hbitmap = iconinfo.hbmColor;
	hr = OleCreatePictureIndirect(&pd, IID_IPictureDisp, TRUE, (void**)&pic);
	if (FAILED(hr) || NULL == pic)
	{
		return hr;
	}
	*ppdispImage = pic;

	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnGetLable(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString id = L"", lable = L"";
	hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (id == BTN_ADDATTACHES_ID)
	{
		lable = GetMessageFromIniFile(L"outlook_add_attachements").c_str();
	}
	/*else if (id == BTN_SHOW_TASKS_ID)
	{
	lable = SHOWTASKS_NAME;
	}*/	
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnGetDiscription(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString id = L"", lable = L"";
	hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (id == BTN_ADDATTACHES_ID)
	{
		lable = GetMessageFromIniFile(L"outlook_add_attachements").c_str();
	}
	/*else if (id == BTN_SHOW_TASKS_ID)
	{
	lable = SHOWTASKS_NAME;
	}*/	
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnGetKeyTip(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString lable = L"Onebox addin";	
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::GetFormRegionStorage(BSTR FormRegionName, LPDISPATCH Item, long LCID, Outlook::OlFormRegionMode FormRegionMode, Outlook::OlFormRegionSize FormRegionSize, VARIANT* Storage)
{
	V_VT(Storage) = VT_ARRAY | VT_UI1;
	V_ARRAY(Storage) = GetOFSResource(IDR_OFS1);
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::BeforeFormRegionShow(Outlook::_FormRegion* FormRegion)
{
	// get the current custom property id
	CComPtr<Outlook::_Inspector> spInspector;
	HRESULT hr = FormRegion->get_Inspector(&spInspector);
	if (FAILED(hr) || NULL == spInspector)
	{
		return hr;
	}
	CComQIPtr<IDispatch> spDispatch;
	hr = spInspector->get_CurrentItem(&spDispatch);
	if (FAILED(hr) || NULL == spDispatch)
	{
		return hr;
	}
	CComQIPtr<Outlook::_MailItem> spMailItem(spDispatch);
	if (NULL == spMailItem)
	{
		return S_OK;
	}
	CComQIPtr<Outlook::UserProperties> spUserProperties;
	hr = spMailItem->get_UserProperties(&spUserProperties);
	if (FAILED(hr) || NULL == spUserProperties)
	{
		return hr;
	}
	CComQIPtr<Outlook::UserProperty> spUserProperty;
	BSTR useDefineName = SysAllocString(USER_DEFINE_NAME);
	if (!useDefineName)
	{
		return S_ERROR;
	}
	hr = spUserProperties->Find(useDefineName, CComVariant(TRUE), &spUserProperty);
	SysFreeString(useDefineName);
	if (FAILED(hr) || NULL == spUserProperty)
	{
		return hr;
	}
	CComVariant temp;
	hr = spUserProperty->get_Value(&temp);
	if (FAILED(hr))
	{
		return hr;
	}
	std::wstring id = std::wstring((BSTR)temp.puiVal);
	if (id.empty())
	{
		return S_ERROR;
	}

	OneboxOutlookAddin::InspectorWrappers::iterator it = inspectorWrappers_.find(id);
	assert(inspectorWrappers_.end() != it);
	OneboxOutlookAddin::MailItemInspectorWrapper* instpector = static_cast<OneboxOutlookAddin::MailItemInspectorWrapper*>(it->second);

	OneboxOutlookAddin::FormRegion *formRegion = new OneboxOutlookAddin::FormRegion;
	if (NULL == formRegion)
	{
		return S_ERROR;
	}
	if (S_OK != formRegion->Init(instpector, FormRegion, id))
	{
		return S_ERROR;
	}

	if (NULL == instpector->formRegion())
	{
		instpector->formRegion(formRegion);
	}

	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::GetFormRegionManifest(BSTR FormRegionName, long LCID, VARIANT* Manifest)
{
	V_VT(Manifest) = VT_BSTR;
	BSTR bstrManifest = GetXMLResource(IDR_XML2);
	V_BSTR(Manifest) = bstrManifest;
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::GetFormRegionIcon(BSTR FormRegionName, long LCID, Outlook::OlFormRegionIcon Icon, VARIANT* Result)
{
	return S_OK;
}

HRESULT COutlookAddinImpl::HrGetResource(int nId, 
										 LPCTSTR lpType, 
										 LPVOID* ppvResourceData,       
										 DWORD* pdwSizeInBytes)
{
	HMODULE hModule = _AtlBaseModule.GetModuleInstance();
	if (!hModule)
		return E_UNEXPECTED;
	HRSRC hRsrc = FindResource(hModule, MAKEINTRESOURCE(nId), lpType);
	if (!hRsrc)
		return HRESULT_FROM_WIN32(GetLastError());
	HGLOBAL hGlobal = LoadResource(hModule, hRsrc);
	if (!hGlobal)
		return HRESULT_FROM_WIN32(GetLastError());
	*pdwSizeInBytes = SizeofResource(hModule, hRsrc);
	*ppvResourceData = LockResource(hGlobal);
	return S_OK;
}

BSTR COutlookAddinImpl::GetXMLResource(int nId)
{
	LPVOID pResourceData = NULL;
	DWORD dwSizeInBytes = 0;
	HRESULT hr = HrGetResource(nId, TEXT("XML"), 
		&pResourceData, &dwSizeInBytes);
	if (FAILED(hr))
		return NULL;
	// Assumes that the data is not stored in Unicode.
	CComBSTR cbstr(dwSizeInBytes, reinterpret_cast<LPCSTR>(pResourceData));
	return cbstr.Detach();
}

SAFEARRAY* COutlookAddinImpl::GetOFSResource(int nId)
{
	LPVOID pResourceData = NULL;
	DWORD dwSizeInBytes = 0;

	if (FAILED(HrGetResource(nId, TEXT("OFS"), &pResourceData, &dwSizeInBytes)))
		return NULL;

	SAFEARRAY* psa;
	SAFEARRAYBOUND dim = {dwSizeInBytes, 0};

	psa = SafeArrayCreate(VT_UI1, 1, &dim);

	if (psa == NULL)
		return NULL;

	BYTE* pSafeArrayData;

	SafeArrayAccessData(psa, (void**)&pSafeArrayData);

	memcpy_s((void*)pSafeArrayData, dwSizeInBytes, pResourceData, dwSizeInBytes);

	SafeArrayUnaccessData(psa);

	return psa;
}

HRESULT COutlookAddinImpl::GetButtonId(IDispatch* idispach, CString& id)
{
	CComQIPtr < Office::IRibbonControl> ribbon(idispach);
	intptr_t idaddr = 0;
	HRESULT hr = ribbon->get_Id((BSTR*)(&idaddr));
	if (FAILED(hr))
	{
		return hr;
	}
	id = CString((BSTR)idaddr);
	return S_OK;
}

int32_t COutlookAddinImpl::GetLanguageID()
{
	if (NULL == spApp_)
	{
		return -1;
	}
	CComQIPtr<Office::LanguageSettings> languageSettings;
	HRESULT hr = spApp_->get_LanguageSettings(&languageSettings);
	if (FAILED(hr) || NULL == languageSettings)
	{
		return INVALID_LANG_ID;
	}
	int32_t languageID;
	hr = languageSettings->get_LanguageID(Office::MsoAppLanguageID::msoLanguageIDUI, &languageID);
	if (FAILED(hr))
	{
		return INVALID_LANG_ID;
	}
	return languageID;
}

BOOL COutlookAddinImpl::IsOneboxServiceNormal()
{
	ATL::CEvent oneboxEvent;
	if (!oneboxEvent.Open(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID))
	{
		return FALSE;
	}

	int32_t status = OutlookAddinThriftClient::create()->getServerStatus();
	if (Service_Status::Service_Status_Online == status)
	{
		return TRUE;
	}

	return FALSE;
}
