#include "stdafx.h"
#include "OutlookAddinImpl.h"
#include <atlapp.h>
#include <atldlgs.h>
#include <windows.h>
#include <memory>
#include <atlsync.h>
#include "CommonDefine.h"
#include "OutlookAddinThriftClient.h"
#include "Utility.h"
#include "OutlookAddinThriftClient.h"

#pragma comment(lib, "Msimg32.lib")

using namespace SD;

#ifdef _WIN64
#define APP_PATH_GEG_PATH (L"SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting")
#else
#define APP_PATH_GEG_PATH (L"SOFTWARE\\Chinasoft\\Onebox\\Setting")
#endif

#ifndef S_ERROR
#define S_ERROR (-1)
#endif

#define SERVER_STATUS_REG_NAME (L"LoginState")

static WCHAR* USER_DEFINE_NAME = L"TransTaskGroupId";

static const WCHAR* BTN_ADDATTACHES_ID = L"ButtonAddAttaches";
static const WCHAR* BTN_SHOW_TASKS_ID = L"ButtonShowTasks";

static const WCHAR* ADDATTACHES_NAME = L"添加附件";
static const WCHAR* SHOWTASKS_NAME = L"查看进度";

//_ATL_FUNC_INFO MailItemEventImpl::VoidFuncInfo = {CC_STDCALL, VT_EMPTY, 0, 0};
//_ATL_FUNC_INFO MailItemEventImpl::AppLoadItemFuncInfo = {CC_STDCALL, VT_EMPTY, 1, {VT_DISPATCH}};
_ATL_FUNC_INFO MailItemEventImpl::ItemSendCloseFuncInfo = {CC_STDCALL, VT_EMPTY, 1, {VT_BOOL|VT_BYREF}};

MailItemEventImpl::MailItemEventImpl(COutlookAddinImpl* parent, Outlook::_Application* application, Outlook::_MailItem* mailItem, const std::wstring& customMailItemId)
	:parent_(parent) 
	,spApplication_(application)
	,spMailItem_(mailItem)
	,customMailItemId_(customMailItemId)
	,remoteParentPath_(L"")
{
	attachements_.clear();
}

HRESULT MailItemEventImpl::Init()
{
	HRESULT hr = S_OK;
	hr = MailItemSendEventSink::DispEventAdvise(spMailItem_);
	if (FAILED(hr))
	{
		return hr;
	}
	hr = MailItemCloseEventSink::DispEventAdvise(spMailItem_);
	if (FAILED(hr))
	{
		MailItemSendEventSink::DispEventUnadvise(spMailItem_);
		return hr;
	}
	return S_OK;
}

int MailItemEventImpl::AddMailItemAttachements(std::list<std::wstring>& attachements)
{
	int ret = OutlookAddinThriftClient::getInstance()->uploadAttachements(attachements, GetRemoteParentPath(), customMailItemId_);
	if (RT_OK != ret)
	{
		return ret;
	}
	attachements_.splice(attachements_.end(), attachements);
	return RT_OK;
}

std::wstring MailItemEventImpl::GetRemoteParentPath()
{
	if (!remoteParentPath_.empty())
	{
		return remoteParentPath_;
	}
	SYSTEMTIME st;
	CString date;
	GetLocalTime(&st);
	date.Format(L"\\OutlookAttachements\\%4d-%02d-%02d", st.wYear, st.wMonth, st.wDay);
	return std::wstring(date);
}

HRESULT MailItemEventImpl::UpdateMailItemAttachementLinks(const std::map<std::wstring, std::wstring>& attachementsLinks)
{
	CComQIPtr<Outlook::_Inspector> spInspector;
	HRESULT hr = spApplication_->ActiveInspector(&spInspector);
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
	std::wstring::size_type pe = t.find(L">", pb);
	std::wstring s = t.substr(0, pe+1);
	std::wstring attachementsBody = L"Onebox attachements links:<br>";
	for (std::map<std::wstring, std::wstring>::const_iterator it = attachementsLinks.begin(); it != attachementsLinks.end(); ++it)
	{
		attachementsBody += std::wstring(Utility::FS::get_file_name(it->first)+L": "+it->second+L"<br>").c_str();
	}
	s += attachementsBody;
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

void __stdcall MailItemEventImpl::OnItemSend(BOOL* bCancel)
{
	if (!COutlookAddinImpl::IsOneboxServiceNormal() || 
		!OutlookAddinThriftClient::getInstance()->isAttachementsTransComplete(customMailItemId_))
	{
		if (IDCANCEL == MessageBox(NULL, L"The attachments you have added have not been transmited over, Do you still want to send the mail?", L"Onebox", MB_OKCANCEL|MB_ICONWARNING))
		{
			*bCancel = TRUE;
			return;
		}
	}
	std::map<std::wstring, std::wstring> attachementsLinks;
	if (RT_OK != OutlookAddinThriftClient::getInstance()->getAttachementsLinks(attachementsLinks, customMailItemId_) 
		|| attachementsLinks.empty())
	{
		if (IDCANCEL == MessageBox(NULL, L"Create attachements share links failed, Do you still want to send the mail?", L"Onebox", MB_OKCANCEL|MB_ICONERROR))
		{
			*bCancel = TRUE;
			return;
		}
		return;
	}
	if (FAILED(UpdateMailItemAttachementLinks(attachementsLinks)))
	{
		if (IDCANCEL == MessageBox(NULL, L"Create attachements share links failed, Do you still want to send the mail?", L"Onebox", MB_OKCANCEL|MB_ICONERROR))
		{
			*bCancel = TRUE;
			return;
		}
		return;
	}

	MailItemSendEventSink::DispEventUnadvise(spMailItem_);
	MailItemCloseEventSink::DispEventUnadvise(spMailItem_);

	// delete the object in parent
	parent_->RemoveMailItemEvent(customMailItemId_);

	// delete the tasks and ignore error
	(void)OutlookAddinThriftClient::getInstance()->deleteTransTasks(customMailItemId_);

	delete this;
}

void __stdcall MailItemEventImpl::OnItemClose(BOOL* bCancel)
{
	if (!COutlookAddinImpl::IsOneboxServiceNormal())
	{
		return;
	}
	if (!OutlookAddinThriftClient::getInstance()->isAttachementsTransComplete(customMailItemId_))
	{
		if (IDCANCEL == MessageBox(NULL, L"The attachments you have added have not been transmited over, Do you still want to close the mail?", L"Onebox", MB_OKCANCEL|MB_ICONWARNING))
		{
			*bCancel = TRUE;
			return;
		}
	}

	MailItemSendEventSink::DispEventUnadvise(spMailItem_);
	MailItemCloseEventSink::DispEventUnadvise(spMailItem_);

	// delete the object in parent
	parent_->RemoveMailItemEvent(customMailItemId_);

	// delete the tasks and ignore error
	(void)OutlookAddinThriftClient::getInstance()->deleteTransTasks(customMailItemId_);

	delete this;
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
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnDisconnection(AddinDesign::ext_DisconnectMode RemoveMode, SAFEARRAY * * custom)
{
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

HRESULT STDMETHODCALLTYPE COutlookAddinImpl::OnButtonClicked(IDispatch* idispach)
{
	if (NULL == spApp_)
	{
		return S_OK;
	}
	if (!IsOneboxServiceNormal())
	{
		MessageBox(NULL, L"You have not install the Onebox pc client or the Onebox pc client is not running!", L"Onebox", MB_OK|MB_ICONWARNING);
		return S_OK;
	}
	CString id = L"";
	HRESULT hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}

	CComPtr<Outlook::_Inspector> spInspector;
	hr = spApp_->ActiveInspector(&spInspector);
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
	std::wstring customMailItemId = L"";
	hr = spUserProperties->Find(USER_DEFINE_NAME, CComVariant(TRUE), &spUserProperty);
	// if the custom mail item id is not exist, add the custom mail item id
	// if the custom mail item id is exist, query the custom mail item id
	if (FAILED(hr) || NULL == spUserProperty)
	{
		customMailItemId = Utility::String::gen_uuid();
		if (customMailItemId.empty())
		{
			return S_ERROR;
		}
		hr = spUserProperties->Add(USER_DEFINE_NAME, Outlook::olText, CComVariant(FALSE), CComVariant(1), &spUserProperty);
		if (FAILED(hr) || NULL == spUserProperty)
		{
			return hr;
		}
		hr = spUserProperty->put_Value(CComVariant(customMailItemId.c_str()));
		if (FAILED(hr))
		{
			return hr;
		}
	}
	else
	{
		CComVariant temp;
		HRESULT hr = spUserProperty->get_Value(&temp);
		if (FAILED(hr))
		{
			return hr;
		}
		customMailItemId = std::wstring((BSTR)temp.puiVal);
		if (customMailItemId.empty())
		{
			return S_ERROR;
		}
	}

	// find if the mail item has already sinked
	std::map<std::wstring, MailItemEventImpl*>::iterator it = mailItemEvents_.find(customMailItemId);
	bool isMailItemSinked = (it != mailItemEvents_.end());

	if (id == BTN_ADDATTACHES_ID)
	{
		WTL::CFileDialog fileDialog(TRUE);
		fileDialog.m_ofn.Flags |= OFN_EXPLORER|OFN_ALLOWMULTISELECT;
		if (IDOK == fileDialog.DoModal())
		{
			MailItemEventImpl* mailItemEvent = NULL;
			// if not sinked, sink the mail item, and insert the MailItemEvent object
			if (!isMailItemSinked)
			{
				mailItemEvent = new MailItemEventImpl(this, spApp_, spMailItem, customMailItemId);
				hr = mailItemEvent->Init();
				if (FAILED(hr))
				{
					return hr;
				}
				mailItemEvents_[customMailItemId] = mailItemEvent;
			}
			else
			{
				mailItemEvent = it->second;
			}

			// get the file list
			std::list<std::wstring> attachements;
			std::wstring parent = std::wstring(fileDialog.m_ofn.lpstrFile).substr(0, fileDialog.m_ofn.nFileOffset);
			parent = Utility::String::rtrim(parent, L"\\");
			LPWSTR pos = fileDialog.m_ofn.lpstrFile+fileDialog.m_ofn.nFileOffset;
			std::wstring name = L"";
			while (NULL != (*pos))
			{
				name = pos;
				attachements.push_back(parent+L"\\"+name);
				pos += name.length()+1;
			}

			if (RT_OK != mailItemEvent->AddMailItemAttachements(attachements))
			{
				MessageBox(NULL, L"Add attachements failed, please try again after a while.", L"Onebox", MB_ICONERROR|MB_OK);
				return S_OK;
			}
		}
	}
	else if (id == BTN_SHOW_TASKS_ID)
	{
		if (!isMailItemSinked)
		{
			return S_OK;
		}
		(void)OutlookAddinThriftClient::getInstance()->showTransTask(customMailItemId);
	}
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
	else if (id == BTN_SHOW_TASKS_ID)
	{
		hIcon =(HICON)::LoadImage(_AtlBaseModule.GetModuleInstance(), 
			MAKEINTRESOURCE(IDI_ICON2), 
			IMAGE_ICON, 32, 32, 0);
	}

	if (NULL == hIcon)
	{
		return HRESULT_FROM_WIN32(GetLastError());
	}
	ICONINFO iconinfo;
	GetIconInfo(hIcon, &iconinfo);
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
		lable = ADDATTACHES_NAME;
	}
	else if (id == BTN_SHOW_TASKS_ID)
	{
		lable = SHOWTASKS_NAME;
	}		
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
		lable = ADDATTACHES_NAME;
	}
	else if (id == BTN_SHOW_TASKS_ID)
	{
		lable = SHOWTASKS_NAME;
	}		
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

BOOL COutlookAddinImpl::IsOneboxServiceNormal()
{
	ATL::CEvent oneboxEvent;
	if (!oneboxEvent.Open(EVENT_ALL_ACCESS, FALSE, SHARE_DRIVE_STORAGE_SERVICE_STOP_EVENT))
	{
		return FALSE;
	}

	int32_t status = Service_Status::Service_Status_Uninitial;
	int32_t ret = Utility::Registry::get(HKEY_LOCAL_MACHINE, APP_PATH_GEG_PATH, SERVER_STATUS_REG_NAME, status);
	if (RT_OK != ret)
	{
		return FALSE;
	}

	if(status == Service_Status::Service_Status_Online/* || 
		status == Service_Status::Service_Status_Pause*/)
	{
		return TRUE;
	}

	return FALSE;
}
