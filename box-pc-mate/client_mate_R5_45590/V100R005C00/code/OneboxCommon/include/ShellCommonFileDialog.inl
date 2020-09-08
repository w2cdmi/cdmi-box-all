#include <atlbase.h>
#include <atlapp.h>
#include <atldlgs.h>

template <class T>
class ATL_NO_VTABLE CMultiFileDialogExImpl : public CFileDialogImpl< T >
{
public:
	CMultiFileDialogExImpl(
		HWND hWndParent = NULL, 
		LPCWSTR lpszOkButtonName = NULL,
		LPCWSTR lpszTitle = NULL,
		LPCWSTR lpszFilter = NULL)
		: CFileDialogImpl<T>(TRUE, NULL, NULL, OFN_HIDEREADONLY, lpszFilter, hWndParent)
		,m_strFolder(L"")
		,m_okButtonName(lpszOkButtonName)
		,m_Title(lpszTitle)
	{
		m_ofn.Flags |= OFN_ALLOWMULTISELECT;
		m_ofn.lpstrTitle = m_Title.c_str();

		// set the default root to Desktop
		WCHAR szDesktopPath[MAX_PATH] = {0};
		(void)SHGetSpecialFolderPath(NULL, szDesktopPath, CSIDL_DESKTOP, FALSE);
		m_strFolder = szDesktopPath;
		m_ofn.lpstrInitialDir = m_strFolder.c_str();

		// no reason, the GetOpenFileName may change the current work directory
		// so we should restore the work directory at last
		WCHAR szWorkDir[MAX_PATH] = {0};
		(void)GetCurrentDirectory(MAX_PATH, szWorkDir);
		m_strWorkDir = szWorkDir;

		T::m_iRet = IDCANCEL;
	}

	~CMultiFileDialogExImpl()
	{
		if (!m_strWorkDir.empty())
		{
			(void)SetCurrentDirectory(m_strWorkDir.c_str());
		}
	}

	HWND GetListView()
	{
		CWindow dlgWindow = GetFileDialogWindow();
		HWND hListView = ::GetDlgItem(dlgWindow.m_hWnd, DEFVIEW_ID);
		if (NULL == hListView)
		{
			return NULL;
		}
		return ::GetDlgItem(hListView, LISTVIEW_ID);
	}

	HWND GetComboBox()
	{
		CWindow dlgWindow = GetFileDialogWindow();
		return ::GetDlgItem(dlgWindow.m_hWnd, COMBOBOX_ID);
	}

	HWND GetOkButton()
	{
		CWindow dlgWindow = GetFileDialogWindow();
		return ::GetDlgItem(dlgWindow.m_hWnd, IDOK);
	}

	void OnInitDone(LPOFNOTIFY lpon)
	{
		HWND hOkButton = GetOkButton();
		if (NULL == hOkButton)
		{
			return;
		}
		::SendMessage(hOkButton, WM_SETTEXT, NULL, (LPARAM)m_okButtonName.c_str());
		::EnableWindow(hOkButton, std::wstring(m_ofn.lpstrFile).empty() ? FALSE : TRUE);
		T::m_lpfnOkButton = (WNDPROC)::GetWindowLongPtr(hOkButton, GWLP_WNDPROC);
		if (NULL == T::m_lpfnOkButton)
		{
			return;
		}
		::SetWindowLongPtr(hOkButton, GWLP_WNDPROC, (LONG_PTR)T::OkButtonProc);
	}

	void OnFolderChange(LPOFNOTIFY lpon)
	{
		WCHAR folderPath[MAX_PATH] = {0};
		CTempBuffer<ITEMIDLIST> idlist;
		int nBytes = GetFolderIDList(NULL, 0);
		if (nBytes > 0)
		{
			idlist.AllocateBytes(nBytes);
			if (GetFolderIDList(idlist, nBytes) > 0)
			{
				SHGetPathFromIDList(idlist, folderPath);
			}
		}
		m_strFolder = folderPath;
		if (!m_strFolder.empty() && *m_strFolder.rbegin() == L'\\')
		{
			m_strFolder = m_strFolder.substr(0, m_strFolder.size() - 1);
		}

		m_selectItems.clear();

		SetDlgControlsState(L"");
	}

	void OnSelChange(LPOFNOTIFY lpon)
	{
		// clear the last selects
		m_selectItems.clear();

		// get the selected file item names text
		std::list<std::wstring> selectedFileItemNames;
		std::wstring selectedFileItemNameTexts = RetrieveSelectedFileItemNames(selectedFileItemNames);

		// 1. get all the selected items (without extension name)
		std::list<std::wstring> selectedItemNames;
		RetrieveSelectedItemNames(selectedItemNames);
		if (selectedItemNames.empty())
		{
			SetDlgControlsState();
			return;
		}

		std::wstring selectedItemNameTexts = L"";
		// 2. exclude the file (has the same name) from the selected items
		std::wstring fileName = L"", fileNameWithoutExt = L"";
		bool fakeFound = false;
		while (!selectedFileItemNames.empty())
		{
			fileName = selectedFileItemNames.front();
			// remove the itemName
			selectedFileItemNames.pop_front();
			// trim the extension name
			fileNameWithoutExt = TrimExtensionName(fileName);
			std::list<std::wstring>::iterator itor;
			for (std::list<std::wstring>::iterator it = selectedItemNames.begin(); it != selectedItemNames.end(); ++it)
			{
				if (*it == fileName)
				{
					selectedItemNameTexts += L"\"" + fileName + L"\" ";
					m_selectItems.push_back(m_strFolder + L"\\" + fileName);
					selectedItemNames.erase(it);
					fakeFound = false;
					break;
				}
				else if (*it == fileNameWithoutExt)
				{
					fakeFound = true;
					itor = it;
				}
			}
			if (fakeFound)
			{
				// in some case, like Library/My Pictures, the picture file name is not the show name
				selectedItemNameTexts += L"\"" + fileName + L"\" ";
				m_selectItems.push_back(m_strFolder + L"\\" + fileName);
				selectedItemNames.erase(itor);
			}
		}

		// 3. the left are all folders, filter the left folders (like My Computer, Network and so on...)
		// Get the ID-list of the current folder.
		CTempBuffer<ITEMIDLIST> idlist;
		int nBytes = GetFolderIDList(NULL, 0);
		if (nBytes <= 0)
		{
			SetDlgControlsState();
			return;
		}
		idlist.AllocateBytes(nBytes);
		if (GetFolderIDList(idlist, nBytes) <= 0)
		{
			SetDlgControlsState();
			return;
		}
		// First bind to the desktop folder, then to the current folder.		
		ATL::CComPtr<IShellFolder> pDesktop, pFolder;
		if (FAILED(::SHGetDesktopFolder(&pDesktop)))
		{
			SetDlgControlsState();
			return;
		}

		// can not select root driver
		LPITEMIDLIST pMyComputer = NULL;
		if (SUCCEEDED(SHGetFolderLocation(NULL, CSIDL_DRIVES, NULL, NULL, &pMyComputer)))
		{
			// user select the local driver
			if (0 == (short)HRESULT_CODE(pDesktop->CompareIDs(SHCIDS_ALLFIELDS, pMyComputer, idlist)))
			{
				ILFree(pMyComputer);
				SetDlgControlsState();
				return;
			}
			ILFree(pMyComputer);
		}

		if (idlist->mkid.cb != CSIDL_DESKTOP)
		{
			if (FAILED(pDesktop->BindToObject(idlist, NULL, IID_IShellFolder, (void**)&pFolder)))
			{
				SetDlgControlsState();
				return;
			}
		}
		else
		{
			pFolder = pDesktop;
		}

		// enum the current folder
		ATL::CComPtr<IEnumIDList> ppenum;
		LPITEMIDLIST pidlItems = NULL;
		STRRET strDispName;
		ULONG uAttr = 0;
		WCHAR pszDisplayName[MAX_PATH] = {0};
		HRESULT hr = pFolder->EnumObjects(NULL, SHCONTF_FOLDERS|SHCONTF_NONFOLDERS, &ppenum);
		if (FAILED(hr))
		{
			SetDlgControlsState();
			return;
		}
		while((hr = ppenum->Next(1, &pidlItems, NULL) == S_OK) && !selectedItemNames.empty())
		{
			uAttr = SFGAO_FILESYSTEM;
			hr = pFolder->GetAttributesOf(1, (LPCITEMIDLIST*)&pidlItems, &uAttr);
			if (SUCCEEDED(hr) && (uAttr&SFGAO_FILESYSTEM))
			{
				if (SUCCEEDED(pFolder->GetDisplayNameOf(pidlItems, SHGDN_INFOLDER, &strDispName)))
				{
					StrRetToBuf(&strDispName, pidlItems, pszDisplayName, MAX_PATH);
					for (std::list<std::wstring>::iterator it = selectedItemNames.begin(); it != selectedItemNames.end(); ++it)
					{
						if (*it == pszDisplayName)
						{
							selectedItemNameTexts += L"\"" + (*it) + L"\" ";
							// convert relative path to absolute path
							ATL::CComPtr<IShellItem> pShellItem = NULL;
							hr = SHCreateItemWithParent(idlist, pFolder, pidlItems, IID_IShellItem, (void**)&pShellItem);
							if (SUCCEEDED(hr))
							{
								LPITEMIDLIST pidAbsoluteItems = NULL;
								hr = SHGetIDListFromObject(pShellItem, &pidAbsoluteItems);
								if (SUCCEEDED(hr))
								{
									WCHAR buf[MAX_PATH] = {0};
									if (SHGetPathFromIDList(pidAbsoluteItems, buf))
									{
										m_selectItems.push_back(buf);
									}
									selectedItemNames.erase(it);
								}
							}
							break;
						}
					}
				}
			}
			CoTaskMemFree(pidlItems);
		}

		SetDlgControlsState(selectedItemNameTexts);
	}

	BOOL OnFileOK(LPOFNOTIFY /*lpon*/)
	{
		WCHAR path[MAX_PATH] = {0};
		int nLength = GetFilePath(path, MAX_PATH);
		if (nLength > 1)
		{
			m_selectItems.clear();
			m_selectItems.push_back(path);
			T::m_iRet = IDOK;
		}
		return TRUE;
	}

	std::wstring GetFolder() const
	{
		return m_strFolder;
	}

	int GetResults(std::list<std::wstring>& results)
	{
		if (T::m_iRet == IDCANCEL)
		{
			return IDCANCEL;
		}
		results.swap(m_selectItems);
		return IDOK;
	}

private:
	void SetDlgControlsState(const std::wstring& itemsText = L"")
	{
		HWND hOkButton = GetOkButton(), hComboBox = GetComboBox();
		::EnableWindow(hOkButton, itemsText.empty() ? FALSE : TRUE);
		::SendMessage(hComboBox, WM_SETTEXT, NULL, (LPARAM)itemsText.c_str());
		if (itemsText.empty()) m_selectItems.clear();
	}

	std::wstring RetrieveSelectedFileItemNames(std::list<std::wstring>& itemNames)
	{
		// get the selected item names length
		int length = GetSpec(NULL, 0);
		if (length < 1)
		{
			SetDlgControlsState();
			return L"";
		}

		WCHAR *szFileName = new (std::nothrow) WCHAR[length];
		if (NULL == szFileName)
		{
			SetDlgControlsState();
			return L"";
		}
		// get the selected item names text
		if (GetSpec(szFileName, length) <= 0)
		{
			delete []szFileName;
			SetDlgControlsState();
			return L"";
		}

		std::wstring name = szFileName;
		delete []szFileName;

		if (!name.empty())
		{
			std::wstring::size_type sp = name.find(L'\"'), ep = 0;
			if (sp == std::wstring::npos)
			{
				itemNames.push_back(name);
			}
			else
			{
				std::wstring tmpName = L"";
				while (std::wstring::npos != sp)
				{
					ep = name.find(L'\"', sp + 1);
					if (ep != std::wstring::npos)
					{
						tmpName = name.substr(sp + 1, ep - sp - 1);
						if (tmpName != L" ")
						{
							itemNames.push_back(tmpName);
						}
					}
					sp = ep;
				}
			}
		}

		return name;
	}

	void RetrieveSelectedItemNames(std::list<std::wstring>& itemNames)
	{
		HWND hListView = GetListView();
		if (NULL != hListView)
		{
			int index = -1;
			while ((index = ListView_GetNextItem(hListView, index, LVNI_SELECTED)) != -1)
			{
			LVITEM item = {0};
			item.mask = LVIF_STATE|LVIF_TEXT;
			item.stateMask = LVIS_SELECTED;
				WCHAR buff[MAX_PATH] = {0};
				item.iItem = index;
				item.pszText = buff;
				item.cchTextMax = MAX_PATH;
				ListView_GetItem(hListView, &item);
				itemNames.push_back(item.pszText);
			}
		}
	}

	std::wstring TrimExtensionName(const std::wstring& name)
	{
		std::wstring::size_type pos = name.find_last_of(L'.');
		if (pos != std::wstring::npos)
		{
			return name.substr(0, pos);
		}
		return name;
	}

private:
	static const DWORD LISTVIEW_ID = 0x01;
	static const DWORD DEFVIEW_ID = 0x461;
	static const DWORD COMBOBOX_ID = 0x47C;

	std::wstring m_okButtonName;
	std::wstring m_Title;

	std::wstring m_strFolder;
	std::list<std::wstring> m_selectItems;

	std::wstring m_strWorkDir;
};
