#include "stdafx.h"
#include "OfficeAddinThriftClient.h"
#include "OfficeAddinImpl.h"
#include "OfficeAddinUploadPath.h"
#include "OfficeAddinProgressFrame.h"
#include "Global.h"
#include "Utility.h"
#include "NoticeFrame.h"
#include "NoticeFrame.h"
#include "ErrorConfMgr.h"

static const WCHAR* BTN_SAVETOCLOUD_ID = L"ButtonSaveToCloud";
//static const WCHAR* BTN_SETTINGS_ID = L"ButtonSettings";

_ATL_FUNC_INFO ExcelEventsSink::WorkbookBeforeCloseParam = {CC_STDCALL, VT_EMPTY, 2, {VT_DISPATCH, VT_BOOL|VT_BYREF}};
_ATL_FUNC_INFO WordEventsSink::DocumentBeforeCloseParam = {CC_STDCALL, VT_EMPTY, 2, {VT_DISPATCH, VT_BOOL|VT_BYREF}};
_ATL_FUNC_INFO PowerPointEventsSink::PresentationCloseParam = {CC_STDCALL, VT_EMPTY, 1, {VT_DISPATCH}};

int32_t languageID_ = INVALID_LANG_ID;

int32_t ExcelEventsSink::init()
{
	if (init_)
	{
		return S_OK;
	}

	if (NULL == spApplication_)
	{
		return S_ERROR;
	}
	if (FAILED(ExcelEventsSinkImpl::DispEventAdvise(spApplication_)))
	{
		return S_ERROR;
	}

	init_ = true;

	languageID_ = iniLanguageHelper.GetLanguage();
	return S_OK;
}

void ExcelEventsSink::release()
{
	if (!init_)
	{
		return;
	}
	ExcelEventsSinkImpl::DispEventUnadvise(spApplication_);
	init_ = false;
}

void __stdcall ExcelEventsSink::OnWorkbookBeforeClose(Excel::_Workbook *wb, VARIANT_BOOL* Cancel)
{
	//COfficeAddinImpl::UploadFile(COfficeAddinImpl::GetFilePath(wb));
}

int32_t WordEventsSink::init()
{
	if (init_)
	{
		return S_OK;
	}

	if (NULL == spApplication_)
	{
		return S_ERROR;
	}
	if (FAILED(WordEventsSinkImpl::DispEventAdvise(spApplication_)))
	{
		return S_ERROR;
	}

	init_ = true;

	languageID_ = iniLanguageHelper.GetLanguage();
	return S_OK;
}

void WordEventsSink::release()
{
	if (!init_)
	{
		return;
	}
	WordEventsSinkImpl::DispEventUnadvise(spApplication_);
	init_ = false;
}

void __stdcall WordEventsSink::OnDocumentBeforeClose(Word::_Document *doc, VARIANT_BOOL* Cancel)
{
	//COfficeAddinImpl::UploadFile(COfficeAddinImpl::GetFilePath(doc));
}

int32_t PowerPointEventsSink::init()
{
	if (init_)
	{
		return S_OK;
	}

	if (NULL == spApplication_)
	{
		return S_ERROR;
	}
	if (FAILED(PowerPointEventsSinkImpl::DispEventAdvise(spApplication_)))
	{
		return S_ERROR;
	}

	init_ = true;

	languageID_ = iniLanguageHelper.GetLanguage();
	return S_OK;
}

void PowerPointEventsSink::release()
{
	if (!init_)
	{
		return;
	}
	PowerPointEventsSinkImpl::DispEventUnadvise(spApplication_);
	init_ = false;
}

void __stdcall PowerPointEventsSink::OnPresentationClose(PowerPoint::_Presentation* presentation)
{
	//COfficeAddinImpl::UploadFile(COfficeAddinImpl::GetFilePath(presentation));
}

HRESULT COfficeAddinImpl::FinalConstruct()
{
	powerPointEventsSink_ = NULL;
	wordEventsSink_ = NULL;
	excelEventsSink_ = NULL;
	return S_OK;
}

void COfficeAddinImpl::FinalRelease()
{

}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::Invoke(DISPID dispidMember, 
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

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnConnection(LPDISPATCH Application, AddinDesign::ext_ConnectMode ConnectMode, LPDISPATCH AddInInst, SAFEARRAY * * custom)
{
	HRESULT hr;
	CComQIPtr<Office::LanguageSettings> languageSettings;

	spWordApplication_ = Application;
	if (NULL != spWordApplication_)
	{
		hr = spWordApplication_->get_LanguageSettings(&languageSettings);
		if (FAILED(hr))
		{
			return hr;
		}
		hr = languageSettings->get_LanguageID(Office::MsoAppLanguageID::msoLanguageIDUI, &languageID_);
		if (FAILED(hr))
		{
			return hr;
		}
		wordEventsSink_ = new WordEventsSink(spWordApplication_);
		if (S_OK != wordEventsSink_->init())
		{
			return S_ERROR;
		}
		return S_OK;
	}
	spExcelApplication_ = Application;
	if (NULL != spExcelApplication_)
	{
		hr = spExcelApplication_->get_LanguageSettings(&languageSettings);
		if (FAILED(hr))
		{
			return hr;
		}
		hr = languageSettings->get_LanguageID(Office::MsoAppLanguageID::msoLanguageIDUI, &languageID_);
		if (FAILED(hr))
		{
			return hr;
		}
		excelEventsSink_ = new ExcelEventsSink(spExcelApplication_);
		if (S_OK != excelEventsSink_->init())
		{
			return S_ERROR;
		}
		return S_OK;
	}
	spPPTApplication_ = Application;
	if (NULL != spPPTApplication_)
	{
		hr = spPPTApplication_->get_LanguageSettings(&languageSettings);
		if (FAILED(hr))
		{
			return hr;
		}
		hr = languageSettings->get_LanguageID(Office::MsoAppLanguageID::msoLanguageIDUI, &languageID_);
		if (FAILED(hr))
		{
			return hr;
		}
		powerPointEventsSink_ = new PowerPointEventsSink(spPPTApplication_);
		if (S_OK != powerPointEventsSink_->init())
		{
			return S_ERROR;
		}
		return S_OK;
	}
	return E_NOTIMPL;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnDisconnection(AddinDesign::ext_DisconnectMode RemoveMode, SAFEARRAY * * custom)
{
	if (NULL != spWordApplication_)
	{
		wordEventsSink_->release();
		delete wordEventsSink_;
		wordEventsSink_ = NULL;
		return S_OK;
	}
	if (NULL != spExcelApplication_)
	{
		excelEventsSink_->release();
		delete excelEventsSink_;
		excelEventsSink_ = NULL;
		return S_OK;
	}
	if (NULL != spPPTApplication_)
	{
		powerPointEventsSink_->release();
		delete powerPointEventsSink_;
		powerPointEventsSink_ = NULL;
		return S_OK;
	}
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnAddInsUpdate(SAFEARRAY * * custom)
{
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnStartupComplete(SAFEARRAY * * custom)
{
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnBeginShutdown(SAFEARRAY * * custom)
{
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::GetCustomUI(BSTR RibbonID, BSTR * RibbonXml)
{
	if(!RibbonXml)
		return E_POINTER;
	*RibbonXml = GetXMLResource(IDR_XML1);
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnGetEnabled(IDispatch* idispach, VARIANT_BOOL* pvarReturnedVal)
{
	if (NULL == pvarReturnedVal)
	{
		return S_OK;
	}
	*pvarReturnedVal = IsOneboxServiceNormal();
	return S_OK;
}

HWND this_hwnd_ = 0;
std::wstring this_name_ = L"";
BOOL CALLBACK lpEnumFunc(HWND hwnd, LPARAM lParam)
{
	LPWSTR wintitle = new WCHAR[FILENAME_MAX];
	memset_s(wintitle, sizeof(WCHAR)*FILENAME_MAX, 0, sizeof(WCHAR)*FILENAME_MAX);

	::GetWindowText(hwnd, wintitle, FILENAME_MAX);

	std::wstring wintitles = wintitle;
	delete[] wintitle;
	if ( std::wstring::npos != wintitles.find(this_name_) )
	{
		this_hwnd_ = hwnd;
		return 0;
	}
	return 1;
}

HWND COfficeAddinImpl::GetParentHwnd()
{
	HWND hwndPointNow = NULL;
	POINT pNow = {0,0};
	if (GetCursorPos(&pNow))
	{
		hwndPointNow = WindowFromPoint(pNow); 
	}

	return hwndPointNow;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnButtonClicked(IDispatch* idispach)
{
	CString id = L"";
	HRESULT hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (BTN_SAVETOCLOUD_ID == id)
	{
		std::wstring path = GetFilePath().GetString();
		if ( path.empty() )
		{			
			Onebox::NoticeImpl::getInstance(NULL)->showModal(Onebox::Confirm,Onebox::Info,L"Onebox",MSG_OFFICE_SAVEFILE_KEY);
			return S_ERROR;
		}
		
		HWND hwnd = GetParentHwnd();

		uint32_t iret = 0;
		std::shared_ptr<OfficeUploadInfo> uploadinfo(new OfficeUploadInfo);
		if( (NULL == uploadinfo) || (NULL == uploadinfo.get()) ) return S_OK;
		OfficeAddinUploadPath* framepath = new OfficeAddinUploadPath(path, hwnd, uploadinfo);
		if (NULL != framepath)
		{
			iret = framepath->showModal();
		}

		if( IDOK == iret )
		{
			if( IsOneboxServiceNormal() )
			{			
				std::wstring taskId = SD::Utility::String::gen_uuid();
				OfficeAddinProgressFrame* frameprogress = new OfficeAddinProgressFrame(path, uploadinfo->remotePath, uploadinfo->id, taskId, uploadinfo->extype);

				if (NULL == frameprogress) return S_OK;
				else frameprogress->Create(hwnd,  IniLanguageHelper(languageID_).GetCommonString(COMMENT_UPLOAD_KEY).c_str()
					, UI_WNDSTYLE_FRAME, WS_EX_TOPMOST, 0, 0, 0, 0);

				int32_t ierrorcode = -1;
				frameprogress->CenterWindow();
				ierrorcode = frameprogress->ShowModal();
				if( RT_OK != ierrorcode )
				{
					std::wstring sadvice = Onebox::ErrorConfMgr::getInstance()->getAdvice(ierrorcode);
					if( !sadvice.empty() )
					{
						Onebox::NoticeImpl::getInstance(hwnd)->showModal(Onebox::Confirm,Onebox::Warning,L"Onebox"
							,MSG_ERROR_KEY
							,ierrorcode
							,Onebox::ErrorConfMgr::getInstance()->getDescription(ierrorcode).c_str()
							,Onebox::ErrorConfMgr::getInstance()->getAdvice(ierrorcode).c_str());
					}
					else
					{
						Onebox::NoticeImpl::getInstance(hwnd)->showModal(Onebox::Confirm,Onebox::Warning,L"Onebox"
							,MSG_ERROR_NO_ADVICE_KEY
							,ierrorcode
							,Onebox::ErrorConfMgr::getInstance()->getDescription(ierrorcode).c_str());
					}
				}
			}
			else
			{
				Onebox::NoticeImpl::getInstance(hwnd)->showModal(Onebox::Confirm,Onebox::Warning,L"Onebox"
					,MSG_ERROR_KEY
					,RT_NOLOGINUSER
					,Onebox::ErrorConfMgr::getInstance()->getDescription(RT_NOLOGINUSER).c_str()
					,Onebox::ErrorConfMgr::getInstance()->getAdvice(RT_NOLOGINUSER).c_str());
			}
		}
	}
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnLoadImage(IDispatch* idispach, IPictureDisp** ppdispImage)
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
	if (BTN_SAVETOCLOUD_ID == id)
	{
		hIcon =(HICON)::LoadImage(_AtlBaseModule.GetModuleInstance(), 
			MAKEINTRESOURCE(IDI_ICON1), 
			IMAGE_ICON, 32, 32, 0);
	}
	/*else if (id == BTN_SETTINGS_ID)
	{
	hIcon =(HICON)::LoadImage(_AtlBaseModule.GetModuleInstance(), 
	MAKEINTRESOURCE(IDI_ICON1), 
	IMAGE_ICON, 32, 32, 0);
	}*/

	if (NULL == hIcon)
	{
		return HRESULT_FROM_WIN32(GetLastError());
	}
	ICONINFO iconinfo = {};
	GetIconInfo(hIcon, &iconinfo);
	pd.bmp.hbitmap = iconinfo.hbmColor;
	DeleteObject(hIcon);
	hr = OleCreatePictureIndirect(&pd, IID_IPictureDisp, TRUE, (void**)&pic);
	if (FAILED(hr) || NULL == pic)
	{
		return hr;
	}
	*ppdispImage = pic;

	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnGetLable(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString id = L"", lable = L"";
	hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (id == BTN_SAVETOCLOUD_ID)
	{
		lable = GetMessageFromIniFile(L"comment_office_uploadtoonebox").c_str();
	}
	/*else if (id == BTN_SETTINGS_ID)
	{
		lable = GetMessageFromIniFile(L"comment_office_settings").c_str();
	}*/
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnGetDiscription(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString id = L"", lable = L"";
	hr = GetButtonId(idispach, id);
	if (FAILED(hr))
	{
		return hr;
	}
	if (id == BTN_SAVETOCLOUD_ID)
	{
		lable = GetMessageFromIniFile(L"comment_office_uploadtoonebox").c_str();
	}
	/*else if (id == BTN_SETTINGS_ID)
	{
		lable = GetMessageFromIniFile(L"comment_office_settings").c_str();
	}*/
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT STDMETHODCALLTYPE COfficeAddinImpl::OnGetKeyTip(IDispatch* idispach, BSTR *pbstrReturnedVal)
{
	HRESULT hr = S_ERROR;
	CString lable = L"Onebox addin";	
	CComBSTR cbstr(lable.GetLength(), (LPCOLESTR)lable);
	*pbstrReturnedVal = cbstr.Detach();
	return S_OK;
}

HRESULT COfficeAddinImpl::HrGetResource(int nId, 
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

BSTR COfficeAddinImpl::GetXMLResource(int nId)
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

HRESULT COfficeAddinImpl::GetButtonId(IDispatch* idispach, CString& id)
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

CString COfficeAddinImpl::GetFilePath()
{
	if (NULL != spWordApplication_)
	{
		CComQIPtr<Word::_Document> spDocument;
		HRESULT hr = spWordApplication_->get_ActiveDocument(&spDocument);
		if (FAILED(hr) || NULL == spDocument)
		{
			return L"";
		}
		return GetFilePath(spDocument);
	}
	if (NULL != spExcelApplication_)
	{
		CComQIPtr<Excel::_Workbook> spWorkBook;
		HRESULT hr = spExcelApplication_->get_ActiveWorkbook(&spWorkBook);
		if (FAILED(hr) || NULL == spWorkBook)
		{
			return L"";
		}
		return GetFilePath(spWorkBook);
	}
	if (NULL != spPPTApplication_)
	{
		CComQIPtr<PowerPoint::_Presentation> spPresentation;
		HRESULT hr = spPPTApplication_->get_ActivePresentation(&spPresentation);
		if (FAILED(hr) || NULL == spPresentation)
		{
			return L"";
		}
		return GetFilePath(spPresentation);
	}

	return L"";
}

CString COfficeAddinImpl::GetFilePath(Word::_Document *doc)
{
	CComQIPtr<Word::_Document> spDocument(doc);
	if (NULL == spDocument)
	{
		return L"";
	}
	BSTR temp;
	HRESULT hr = spDocument->get_Path(&temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}
	CString filePath = temp;
	SysFreeString(temp);
	if (filePath.IsEmpty())
	{
		return L"";
	}
	hr = spDocument->get_Name(&temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}

	filePath += L"\\";
	filePath += temp;
	SysFreeString(temp);
	return filePath;
}

CString COfficeAddinImpl::GetFilePath(Excel::_Workbook *wb)
{
	CComQIPtr<Excel::_Workbook> spWorkBook(wb);
	if (NULL == spWorkBook)
	{
		return L"";
	}
	BSTR temp;
	HRESULT hr = spWorkBook->get_Path(0, &temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}
	CString filePath = temp;
	SysFreeString(temp);
	if (filePath.IsEmpty())
	{
		return L"";
	}
	hr = spWorkBook->get_Name(&temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}

	filePath += L"\\";
	filePath += temp;
	SysFreeString(temp);
	return filePath;
}

CString COfficeAddinImpl::GetFilePath(PowerPoint::_Presentation *presentation)
{
	CComQIPtr<PowerPoint::_Presentation> spPresentation(presentation);
	if (NULL == spPresentation)
	{
		return L"";
	}
	BSTR temp;
	HRESULT hr = spPresentation->get_Path(&temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}
	CString filePath = temp;
	SysFreeString(temp);
	hr = spPresentation->get_Name(&temp);
	if (FAILED(hr) || !temp)
	{
		return L"";
	}

	filePath += L"\\";
	filePath += temp;
	SysFreeString(temp);
	return filePath;
}

BOOL COfficeAddinImpl::IsOneboxServiceNormal()
{
	HANDLE hEvent = ::OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
	if (NULL == hEvent)
	{
		return FALSE;
	}
	::CloseHandle(hEvent);

	int32_t status = OfficeAddinThriftClient::getInstance()->getServerStatus();
	if (Service_Status::Service_Status_Online == status)
	{
		return TRUE;
	}

	return FALSE;
}
