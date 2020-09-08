#include "ShellCommonFileDialog.h"
#include "Utility.h"
#include <shlobj.h>
#include <shlwapi.h>

#pragma comment(lib, "Shlwapi.lib")

class CMultiFileDialogEx : public CMultiFileDialogExImpl<CMultiFileDialogEx>
{
public:
	static WNDPROC m_lpfnOkButton;
	static int m_iRet;

	CMultiFileDialogEx(
		HWND hWndParent = NULL, 
		LPCWSTR lpszOkButtonName = NULL,
		LPCWSTR lpszTitle = NULL,
		LPCWSTR lpszFilter = NULL)
		: CMultiFileDialogExImpl<CMultiFileDialogEx>(hWndParent, lpszOkButtonName, lpszTitle, lpszFilter)
	{ }

	BEGIN_MSG_MAP(CMultiFileDialogEx)
		CHAIN_MSG_MAP(CMultiFileDialogExImpl<CMultiFileDialogEx>)
	END_MSG_MAP()

	static HRESULT OkButtonProc (HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		if (NULL != m_lpfnOkButton)
		{
			if (WM_LBUTTONUP == uMsg)
			{
				::SendMessage(::GetParent(hWnd), WM_COMMAND, MAKEWPARAM(IDCANCEL, 0), NULL);
				m_iRet = IDOK;
				return FALSE;
			}
			return m_lpfnOkButton(hWnd, uMsg, wParam, lParam);
		}
		return FALSE;
	}
};

WNDPROC CMultiFileDialogEx::m_lpfnOkButton = NULL;
int CMultiFileDialogEx::m_iRet = IDCANCEL;

class ShellCommonFileDialog::Impl
{
public:
	Impl(const ShellCommonFileDialogParam& param)
		:param_(param)
		,filterTypes_(NULL)
	{
		if (!param_.filters.empty())
		{
			filterTypes_ = new (std::nothrow) COMDLG_FILTERSPEC[param_.filters.size()];
			if (NULL != filterTypes_)
			{
				int32_t index = 0;
				for (ShellCommonFileDialogFilter::const_iterator it = param_.filters.begin(); 
					it != param_.filters.end(); ++it)
				{
					filterTypes_[index].pszName = it->first.c_str();
					filterTypes_[index++].pszSpec = it->second.c_str();
				}
			}			
		}
	}

	~Impl()
	{
		if (NULL != filterTypes_)
		{
			delete filterTypes_;
			filterTypes_ = NULL;
		}
	}

	bool getResults(ShellCommonFileDialogResult& results)
	{
		bool ret = true;

		std::wstring workDirectory = SD::Utility::FS::get_work_directory();
		switch (param_.type)
		{
		case OpenAFile:
			ret = openFile(results);
			break;
		case OpenFiles:
			ret = openFiles(results);
			break;
		case OpenAFolder:
			ret = openFolder(results);
			break;
		case OpenFilesAndFolders:
			ret = openFilesAndFolders(results);
			break;
		default:
			ret = false;
			break;
		}
		::SetCurrentDirectory(workDirectory.c_str());
		return ret;
	}

private:
	bool openFile(ShellCommonFileDialogResult& results)
	{
		bool ret = false;
		HRESULT hr = S_OK;

		// Create a new common open file dialog.
		IFileDialog *pfd = NULL;
		hr = CoCreateInstance(CLSID_FileOpenDialog, NULL, CLSCTX_INPROC_SERVER, 
			IID_PPV_ARGS(&pfd));
		if (SUCCEEDED(hr))
		{
			// Set the title of the dialog.
			if (SUCCEEDED(hr))
			{
				if (!param_.title.empty())
				{
					hr = pfd->SetTitle(param_.title.c_str());
				}
				else
				{
					hr = pfd->SetTitle(L"Select a File");
				}
			}

			// Specify file types for the file dialog.
			if (SUCCEEDED(hr) && NULL != filterTypes_)
			{
				hr = pfd->SetFileTypes(param_.filters.size(), filterTypes_);
				if (SUCCEEDED(hr))
				{
					// Set the selected file type index of 0.
					hr = pfd->SetFileTypeIndex(0);
				}
			}

			// Show the open file dialog.
			if (SUCCEEDED(hr))
			{
				hr = pfd->Show(param_.parent);
				if (SUCCEEDED(hr))
				{
					// Get the result of the open file dialog.
					IShellItem *psiResult = NULL;
					hr = pfd->GetResult(&psiResult);
					if (SUCCEEDED(hr))
					{
						PWSTR pszPath = NULL;
						hr = psiResult->GetDisplayName(SIGDN_FILESYSPATH, &pszPath);
						if (SUCCEEDED(hr))
						{
							results.push_back(pszPath);
							CoTaskMemFree(pszPath);
						}
						psiResult->Release();
					}
					ret = true;
				}
			}

			pfd->Release();
			
			return ret;
		}

		return ret;
	}

	bool openFiles(ShellCommonFileDialogResult& results)
	{
		bool ret = false;
		HRESULT hr = S_OK;

		// Create a new common open file dialog.
		IFileOpenDialog *pfd = NULL;
		hr = CoCreateInstance(CLSID_FileOpenDialog, NULL, CLSCTX_INPROC_SERVER, 
			IID_PPV_ARGS(&pfd));
		if (SUCCEEDED(hr))
		{
			// Allow multi-selection in the common file dialog.
			DWORD dwOptions;
			hr = pfd->GetOptions(&dwOptions);
			if (SUCCEEDED(hr))
			{
				hr = pfd->SetOptions(dwOptions | FOS_ALLOWMULTISELECT);
			}

			// Set the title of the dialog.
			if (SUCCEEDED(hr))
			{
				if (!param_.title.empty())
				{
					hr = pfd->SetTitle(param_.title.c_str());
				}
				else
				{
					hr = pfd->SetTitle(L"Select Files");
				}
			}

			// Show the open file dialog.
			if (SUCCEEDED(hr))
			{
				hr = pfd->Show(param_.parent);
				if (SUCCEEDED(hr))
				{
					// Obtain the results of the user interaction.
					IShellItemArray *psiaResults = NULL;
					hr = pfd->GetResults(&psiaResults);
					if (SUCCEEDED(hr))
					{
						// Get the number of files being selected.
						DWORD dwFolderCount;
						hr = psiaResults->GetCount(&dwFolderCount);
						if (SUCCEEDED(hr))
						{
							// Iterate through all selected files.
							for (DWORD i = 0; i < dwFolderCount; i++)
							{
								IShellItem *psi = NULL;
								if (SUCCEEDED(psiaResults->GetItemAt(i, &psi)))
								{
									// Retrieve the file path.
									PWSTR pszPath = NULL;
									if (SUCCEEDED(psi->GetDisplayName(SIGDN_FILESYSPATH, 
										&pszPath)))
									{
										results.push_back(pszPath);
										CoTaskMemFree(pszPath);
									}
									psi->Release();
								}
							}

							ret = true;
						}
						psiaResults->Release();
					}
				}
			}
			pfd->Release();
		}
		return ret;
	}

	bool openFolder(ShellCommonFileDialogResult& results)
	{
		bool ret = false;
		HRESULT hr = S_OK; 
		// Create a new common open file dialog.
		IFileOpenDialog *pfd = NULL;
		hr = CoCreateInstance(CLSID_FileOpenDialog, NULL, CLSCTX_INPROC_SERVER, 
			IID_PPV_ARGS(&pfd));
		if (SUCCEEDED(hr))
		{
			// Set the dialog as a folder picker.
			DWORD dwOptions;
			hr = pfd->GetOptions(&dwOptions);
			if (SUCCEEDED(hr))
			{
				hr = pfd->SetOptions(dwOptions | FOS_PICKFOLDERS);
			}

			// Set the title of the dialog.
			if (SUCCEEDED(hr))
			{
				if (!param_.title.empty())
				{
					hr = pfd->SetTitle(param_.title.c_str());
				}
				else
				{
					hr = pfd->SetTitle(L"Select a Folder");
				}
			}

			// Show the open file dialog.
			if (SUCCEEDED(hr))
			{
				hr = pfd->Show(param_.parent);
				if (SUCCEEDED(hr))
				{
					// Get the selection from the user.
					IShellItem *psiResult = NULL;
					hr = pfd->GetResult(&psiResult);
					if (SUCCEEDED(hr))
					{
						PWSTR pszPath = NULL;
						hr = psiResult->GetDisplayName(SIGDN_FILESYSPATH, &pszPath);
						if (SUCCEEDED(hr))
						{
							results.push_back(pszPath);
							CoTaskMemFree(pszPath);
						}
						psiResult->Release();
					}
					ret = true;
				}
			}

			pfd->Release();
		}

		return ret;
	}

	bool openFilesAndFolders(ShellCommonFileDialogResult& results)
	{
		/*std::wstring filter = L"";
		for (ShellCommonFileDialogFilter::const_iterator it = param_.filters.begin(); it != param_.filters.end(); ++it)
		{
		filter += it->second + L";";
		}*/
		CMultiFileDialogEx fileDialog(param_.parent, param_.okButtonName.c_str(), param_.title.c_str()/*, filter.c_str()*/);
		fileDialog.DoModal();
		if (IDCANCEL == fileDialog.GetResults(results))
		{
			return false;
		}
		return true;
	}

private:
	const ShellCommonFileDialogParam& param_;
	COMDLG_FILTERSPEC *filterTypes_;
};

ShellCommonFileDialog::ShellCommonFileDialog(const ShellCommonFileDialogParam& param)
	:impl_(new Impl(param))
{

}

ShellCommonFileDialog::~ShellCommonFileDialog()
{

}

bool ShellCommonFileDialog::getResults(ShellCommonFileDialogResult& results)
{
	if (NULL == impl_.get())
	{
		return false;
	}
	return impl_->getResults(results);
}
