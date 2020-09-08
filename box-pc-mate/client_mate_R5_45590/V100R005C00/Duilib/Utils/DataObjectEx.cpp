#include "StdAfx.h"
#include "DataObjectEx.h"

namespace DuiLib 
{
	CDataObjectEx::CDataObjectEx(/*SdkDropSource *pDropSource*/)
	{
		//m_pDropSource = pDropSource;
		m_lRefCount = 1;
	}

	CDataObjectEx::~CDataObjectEx(void)
	{
		m_lRefCount = 0;

		int nSize = (int)m_dataStorageCL.size();
		for (int i = 0; i < nSize; ++i)
		{
			DATASTORAGE_t dataEntry = m_dataStorageCL.at(i);
			ReleaseStgMedium(dataEntry.m_stgMedium);
			//SAFE_DELETE(dataEntry.m_stgMedium);
			//SAFE_DELETE(dataEntry.m_formatEtc);
		}
	}

	STDMETHODIMP CDataObjectEx::QueryInterface(REFIID riid, void **ppv)
	{
		static const QITAB qit[] =
		{
			QITABENT(CDataObjectEx, IDataObject),
			{ 0 },
		};
		return QISearch(this, qit, riid, ppv);
	}

	STDMETHODIMP_(ULONG) CDataObjectEx::AddRef()
	{
		return InterlockedIncrement(&m_lRefCount);
	}

	STDMETHODIMP_(ULONG) CDataObjectEx::Release()
	{
		ULONG lRef = InterlockedDecrement(&m_lRefCount);
		if (0 == lRef)
		{
			delete this;
		}
		return m_lRefCount;
	}

	STDMETHODIMP CDataObjectEx::GetData(FORMATETC *pformatetcIn, STGMEDIUM *pmedium)
	{
		if ( (NULL == pformatetcIn) || (NULL == pmedium) )
		{
			return E_INVALIDARG;
		}

		pmedium->hGlobal = NULL;

		int nSize = (int)m_dataStorageCL.size();
		for (int i = 0; i < nSize; ++i)
		{
			DATASTORAGE_t dataEntry = m_dataStorageCL.at(i);
			if( (pformatetcIn->tymed & dataEntry.m_formatEtc->tymed) &&
				(pformatetcIn->dwAspect == dataEntry.m_formatEtc->dwAspect) &&
				(pformatetcIn->cfFormat == dataEntry.m_formatEtc->cfFormat) )
			{
				return CopyMedium(pmedium, dataEntry.m_stgMedium, dataEntry.m_formatEtc);
			}
		}

		return DV_E_FORMATETC;
	}

	STDMETHODIMP CDataObjectEx::SetData(FORMATETC *pformatetc, STGMEDIUM *pmedium, BOOL fRelease)
	{
		if ( (NULL == pformatetc) || (NULL == pmedium) )
		{
			return E_INVALIDARG;
		}

		if ( pformatetc->tymed != pmedium->tymed )
		{
			return E_FAIL;
		}

		FORMATETC* fetc = new FORMATETC;
		STGMEDIUM* pStgMed = new STGMEDIUM;
		ZeroMemory(fetc, sizeof(FORMATETC));
		ZeroMemory(pStgMed, sizeof(STGMEDIUM));

		*fetc = *pformatetc;

		if ( TRUE == fRelease )
		{
			*pStgMed = *pmedium;
		}
		else
		{
			CopyMedium(pStgMed, pmedium, pformatetc);
		}

		DATASTORAGE_t dataEntry = { fetc, pStgMed };
		m_dataStorageCL.push_back(dataEntry);

		return S_OK;
	}

	STDMETHODIMP CDataObjectEx::GetDataHere(FORMATETC *pformatetc , STGMEDIUM *pmedium)
	{
		UNREFERENCED_PARAMETER(pformatetc);
		UNREFERENCED_PARAMETER(pmedium);
		return E_NOTIMPL;
	}

	STDMETHODIMP CDataObjectEx::QueryGetData(FORMATETC *pformatetc)
	{
		if ( NULL == pformatetc )
		{
			return E_INVALIDARG;
		}
		if ( !(DVASPECT_CONTENT & pformatetc->dwAspect) )
		{
			return DV_E_DVASPECT;
		}
		HRESULT hr = DV_E_TYMED;
		int nSize = m_dataStorageCL.size();
		for (int i = 0; i < nSize; ++i)
		{
			DATASTORAGE_t dataEnrty = m_dataStorageCL.at(i);
			if ( dataEnrty.m_formatEtc->tymed & pformatetc->tymed )
			{
				if ( dataEnrty.m_formatEtc->cfFormat == pformatetc->cfFormat )
				{
					return S_OK;
				}
				else
				{
					hr = DV_E_CLIPFORMAT;
				}
			}
			else
			{
				hr = DV_E_TYMED;
			}
		}
		return hr;
	}

	STDMETHODIMP CDataObjectEx::EnumFormatEtc(DWORD dwDirection, IEnumFORMATETC **ppenumFormatEtc)
	{
		if ( NULL == ppenumFormatEtc )
		{
			return E_INVALIDARG;
		}
		*ppenumFormatEtc = NULL;
		HRESULT hr = E_NOTIMPL;
		if ( DATADIR_GET == dwDirection )
		{
			FORMATETC rgfmtetc[] =
			{
				{ CF_UNICODETEXT, NULL, DVASPECT_CONTENT, 0, TYMED_HGLOBAL },
			};
			hr = SHCreateStdEnumFmtEtc(ARRAYSIZE(rgfmtetc), rgfmtetc, ppenumFormatEtc);
		}
		return hr;
	}

	STDMETHODIMP CDataObjectEx::DAdvise(FORMATETC *pformatetc , DWORD advf , IAdviseSink *pAdvSnk , DWORD *pdwConnection)
	{
		UNREFERENCED_PARAMETER(pformatetc);
		UNREFERENCED_PARAMETER(advf);
		UNREFERENCED_PARAMETER(pAdvSnk);
		UNREFERENCED_PARAMETER(pdwConnection);
		return E_NOTIMPL;
	}

	STDMETHODIMP CDataObjectEx::DUnadvise(DWORD dwConnection)
	{
		UNREFERENCED_PARAMETER(dwConnection);
		return E_NOTIMPL;
	}

	STDMETHODIMP CDataObjectEx::EnumDAdvise(IEnumSTATDATA **ppenumAdvise)
	{
		UNREFERENCED_PARAMETER(ppenumAdvise);
		return E_NOTIMPL;
	}

	HRESULT CDataObjectEx::CopyMedium(STGMEDIUM* pMedDest, STGMEDIUM* pMedSrc, FORMATETC* pFmtSrc)
	{
		if ( (NULL == pMedDest) || (NULL ==pMedSrc) || (NULL == pFmtSrc) )
		{
			return E_INVALIDARG;
		}
		switch(pMedSrc->tymed)
		{
		case TYMED_HGLOBAL:
			pMedDest->hGlobal = (HGLOBAL)OleDuplicateData(pMedSrc->hGlobal, pFmtSrc->cfFormat, NULL);
			break;
		case TYMED_GDI:
			pMedDest->hBitmap = (HBITMAP)OleDuplicateData(pMedSrc->hBitmap, pFmtSrc->cfFormat, NULL);
			break;
		case TYMED_MFPICT:
			pMedDest->hMetaFilePict = (HMETAFILEPICT)OleDuplicateData(pMedSrc->hMetaFilePict, pFmtSrc->cfFormat, NULL);
			break;
		case TYMED_ENHMF:
			pMedDest->hEnhMetaFile = (HENHMETAFILE)OleDuplicateData(pMedSrc->hEnhMetaFile, pFmtSrc->cfFormat, NULL);
			break;
		case TYMED_FILE:
			pMedSrc->lpszFileName = (LPOLESTR)OleDuplicateData(pMedSrc->lpszFileName, pFmtSrc->cfFormat, NULL);
			break;
		case TYMED_ISTREAM:
			pMedDest->pstm = pMedSrc->pstm;
			pMedSrc->pstm->AddRef();
			break;
		case TYMED_ISTORAGE:
			pMedDest->pstg = pMedSrc->pstg;
			pMedSrc->pstg->AddRef();
			break;
		case TYMED_NULL:
		default:
			break;
		}
		pMedDest->tymed = pMedSrc->tymed;
		pMedDest->pUnkForRelease = NULL;
		if(pMedSrc->pUnkForRelease != NULL)
		{
			pMedDest->pUnkForRelease = pMedSrc->pUnkForRelease;
			pMedSrc->pUnkForRelease->AddRef();
		}
		return S_OK;
	}

	HRESULT CDataObjectEx::SetBlob(CLIPFORMAT cf, const void *pvBlob, UINT cbBlob)
	{
		void *pv = GlobalAlloc(GPTR, cbBlob);
		HRESULT hr = pv ? S_OK : E_OUTOFMEMORY;
		if ( SUCCEEDED(hr) )
		{
			CopyMemory(pv, pvBlob, cbBlob);
			FORMATETC fmte = {cf, NULL, DVASPECT_CONTENT, -1, TYMED_HGLOBAL};
			// The STGMEDIUM structure is used to define how to handle a global memory transfer.
			// This structure includes a flag, tymed, which indicates the medium
			// to be used, and a union comprising pointers and a handle for getting whichever
			// medium is specified in tymed.
			STGMEDIUM medium = {};
			medium.tymed = TYMED_HGLOBAL;
			medium.hGlobal = pv;
			hr = this->SetData(&fmte, &medium, TRUE);
			if (FAILED(hr))
			{
				GlobalFree(pv);
			}
		}
		return hr;
	}

	HGLOBAL CreateGlobalHandle(IN void* ptr, int size)
	{
		HGLOBAL hGlobal = NULL;
		hGlobal = GlobalAlloc(GMEM_FIXED, size);
		if (NULL != hGlobal)
		{
			LPVOID pdest = GlobalLock(hGlobal);
			if (NULL != pdest)
			{
				memcpy_s(pdest, size, ptr, size);
			}
			GlobalUnlock(hGlobal);
		}
		return hGlobal;
	}

} // namespace DuiLib
