#pragma once

#include <Ole2.h>
#include <shlobj.h>
#include <Shlwapi.h>
#include <vector>

namespace DuiLib 
{
	typedef struct _DATASTORAGE
	{
		FORMATETC *m_formatEtc;
		STGMEDIUM *m_stgMedium;

	} DATASTORAGE_t, *LPDATASTORAGE_t;

	class CDataObjectEx : public IDataObject
	{
	public:
		CDataObjectEx(/*SdkDropSource *pDropSource = NULL*/);
		BOOL IsDataAvailable(CLIPFORMAT cfFormat);
		BOOL GetGlobalData(CLIPFORMAT cfFormat, void **ppData);
		BOOL GetGlobalDataArray(CLIPFORMAT cfFormat, HGLOBAL *pDataArray, DWORD dwCount);
		BOOL SetGlobalData(CLIPFORMAT cfFormat, void *pData, BOOL fRelease = TRUE);
		BOOL SetGlobalDataArray(CLIPFORMAT cfFormat, HGLOBAL *pDataArray, DWORD dwCount, BOOL fRelease = TRUE);
		//BOOL SetDropTip(DROPIMAGETYPE type, PCWSTR pszMsg, PCWSTR pszInsert);

		// The com interface.
		IFACEMETHODIMP QueryInterface(REFIID riid, void **ppv);
		IFACEMETHODIMP_(ULONG) AddRef();
		IFACEMETHODIMP_(ULONG) Release();
		IFACEMETHODIMP GetData(FORMATETC *pformatetcIn, STGMEDIUM *pmedium);
		IFACEMETHODIMP SetData(FORMATETC *pformatetc, STGMEDIUM *pmedium, BOOL fRelease);
		IFACEMETHODIMP GetDataHere(FORMATETC *pformatetc , STGMEDIUM *pmedium );
		IFACEMETHODIMP QueryGetData(FORMATETC *pformatetc);
		//IFACEMETHODIMP GetCanonicalFormatEtc(FORMATETC *pformatetcIn, FORMATETC *pformatetcOut);
		IFACEMETHODIMP EnumFormatEtc(DWORD dwDirection, IEnumFORMATETC **ppenumFormatEtc);
		IFACEMETHODIMP DAdvise(FORMATETC *pformatetc , DWORD advf , IAdviseSink *pAdvSnk , DWORD *pdwConnection);
		IFACEMETHODIMP DUnadvise(DWORD dwConnection);
		IFACEMETHODIMP EnumDAdvise(IEnumSTATDATA **ppenumAdvise);

	private:
		~CDataObjectEx(void);
		CDataObjectEx(const CDataObjectEx&);
		CDataObjectEx& operator = (const CDataObjectEx&);
		HRESULT CopyMedium(STGMEDIUM* pMedDest, STGMEDIUM* pMedSrc, FORMATETC* pFmtSrc);
		HRESULT SetBlob(CLIPFORMAT cf, const void *pvBlob, UINT cbBlob);

	private:
		volatile LONG m_lRefCount;
		std::vector<DATASTORAGE_t> m_dataStorageCL;
	};
} // namespace DuiLib
