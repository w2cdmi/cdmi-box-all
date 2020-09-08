#include "stdafxOnebox.h"
#include "ControlNames.h"
#include "InIHelper.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "CredentialMgr.h"
#include "ConfigureMgr.h"
#include "Utility.h"
#include "DialogBuilderCallbackImpl.h"
#include "InILanguage.h"
#include <ZLibWrapLib.h>
#include <fstream>
#include "UpgradeMgr.h"
#include "UICommonDefine.h"
#include "DialogBuilderCallbackImpl.h"
#include "SmartHandle.h"
#include "Version.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("UpgradeMgr")
#endif

namespace Onebox
{
	class UpgradeFrame : public WindowImplBase, public UpgradeMgr
	{
	public:
		UpgradeFrame(UserContext*  userContext, HWND parent);
		virtual ~UpgradeFrame(void);

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		virtual void Notify(TNotifyUI& msg);
		void InitWindow();

		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

	public:
	   virtual void Run();

	private:
		void Btn_Upgrage_Clicked();

		UpgradeType GetUpgradeType();

		bool DownloadPackage();

		bool UnzipFile();

		int32_t getSHA256Signature(const std::wstring& localPath, std::string& signature);

		bool checkClient(std::wstring clientType, std::wstring version);

		bool ExecUpgrade();

		bool RunUpgrade();

		void SaveConfig(std::string& _return);

		std::wstring GetVersion();

		void SetUpgradeMsg();

		BOOL IsUpgrade();

		void Show();

		std::vector<std::wstring> TraverseDirectory(std::wstring& wstrDir); 

	public:
		CIrregularWindow *m_pBackWnd;
		UserContext* userContext_;
		std::wstring m_newVersion;
		std::string m_downLoadUrl;
		std::wstring m_wstrIniFilePath;
		std::wstring m_wstrUpgradePath;
		std::wstring m_wstrZipFile;
		HWND parent_;
	};

	UpgradeFrame::UpgradeFrame(UserContext*  userContext, HWND parent)
		:userContext_(userContext)
		,parent_(parent)
		,m_pBackWnd(NULL)
	{
		SaveConfig(m_downLoadUrl);
		if (!IsUpgrade())
		{
			return;
		}
		CIrregularWindow::InitGDIplus();
		m_pBackWnd = new CIrregularWindow((GetInstallPath()+L"skin\\img\\upgradebg.png").c_str());
		assert(m_pBackWnd != NULL && _T("new CIrregularWindow() failed!"));
	}

	UpgradeFrame::~UpgradeFrame(void)
	{
		if( m_pBackWnd )
		{
			delete m_pBackWnd;
			m_pBackWnd = NULL;
		}
	}

	CDuiString UpgradeFrame::GetSkinFolder()
	{
		return iniLanguageHelper.GetSkinFolderPath().c_str();
	}

	CDuiString UpgradeFrame::GetSkinFile()
	{
		return ControlNames::SKIN_XML_UPGRADE_FILE;
	}

	LPCTSTR UpgradeFrame::GetWindowClassName(void) const
	{
		return ControlNames::WND_UPGRADE_NAME;
	}

	CControlUI* UpgradeFrame::CreateControl(LPCTSTR pstrClass)
	{
		return DialogBuilderCallbackImpl::getInstance()->CreateControl(pstrClass);
	}

	bool UpgradeFrame::InitLanguage(CControlUI* control)
	{
		return DialogBuilderCallbackImpl::getInstance()->InitLanguage(control);
	}

	void UpgradeFrame::Notify(TNotifyUI& msg)
	{
		if( msg.sType == _T("click") ) 
		{
			CDuiString name = msg.pSender->GetName();
			if (name == ControlNames::BTN_CLOSE || name == ControlNames::BTN_CANCEL)
			{
				if (GetUpgradeType() == Force)
				{
				 	PostQuitMessage(0L);
					return;
				}
				
				Close();
				return;
			}
			else if (name == ControlNames::BTN_UPGRADE)
			{
				if (::IsWindow(parent_))
				{
					::ShowWindow(parent_,SW_HIDE);
				}
				::ShowWindow(this->m_pBackWnd->GetHandle(),SW_HIDE);
				::ShowWindow(this->GetHWND(),SW_HIDE);
				Btn_Upgrage_Clicked();
			}
		}
	}

	void UpgradeFrame::InitWindow()
	{
		LONG styleValue = ::GetWindowLong(*this, GWL_STYLE);
		styleValue &= ~WS_CAPTION;
		styleValue &= ~WS_THICKFRAME; 
		::SetWindowLong(*this, GWL_STYLE, styleValue);

		if(m_pBackWnd)
		{
			m_pBackWnd->AttachWindow(m_hWnd);
		}
		SetUpgradeMsg();
	}

	LRESULT UpgradeFrame::HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		if (uMsg == WM_CLOSE)
		{
			return ::SendMessage(this->m_pBackWnd->GetHandle(),WM_CLOSE,0L,0L);
		}
		else if (uMsg == WM_DESTROY)
		{
			CIrregularWindow::UnInitGDIplus();
			delete m_pBackWnd;
			m_pBackWnd = NULL;
			return 0;
		}		
		return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
	}

	void UpgradeFrame::Show()
	{
		if (GetUpgradeType() == Force)
		{
			m_pBackWnd->CenterWindow();
			CenterWindow();
			ShowModal();
		}
		else
		{
			m_pBackWnd->CenterWindow();
			CenterWindow();
			ShowWindow();
		}
	}

	void UpgradeFrame::Btn_Upgrage_Clicked()
	{
		if(!RunUpgrade())
		{
			if (::IsWindow(m_hWnd))
			{
				Close();
			}
			if (::IsWindow(parent_))
			{
				::ShowWindow(parent_,SW_SHOW);
			}
		}
	}

	UpgradeType UpgradeFrame::GetUpgradeType()
	{
		CInIHelper iniHelper(m_wstrIniFilePath);
		std::wstring wstrUpdateType = iniHelper.GetString(CONF_VERSION_SECTION,CONF_UPDATE_TYPE_KEY,L"");
		return wstrUpdateType == L"0" ? Normal : Force;
	}

	bool  UpgradeFrame::DownloadPackage()
	{
		WCHAR userTempPath[MAX_PATH] = {0};
		GetTempPath(MAX_PATH,userTempPath);
		m_wstrUpgradePath = userTempPath;
		m_wstrUpgradePath += UPGRADEFOLDER;

		if(SD::Utility::FS::is_exist(m_wstrUpgradePath))
		{
			SD::Utility::FS::remove_all(m_wstrUpgradePath);
		}

		SD::Utility::FS::create_directory(m_wstrUpgradePath);
		m_wstrZipFile = m_wstrUpgradePath + L"\\" +  ZIPFILE;
		MAKE_CLIENT(client);
		int32_t ret = client().downloadClient(m_downLoadUrl,SD::Utility::String::wstring_to_string(m_wstrZipFile).c_str());
		if (0 == ret)
		{
			return true;
		}
		SERVICE_ERROR(MODULE_NAME, ret, "download the client package failed.");
		return false;
	}

	bool  UpgradeFrame::UnzipFile()
	{
		std::string strZipFile = SD::Utility::String::wstring_to_string(m_wstrZipFile);
		std::string strDecFolder = SD::Utility::String::wstring_to_string(m_wstrUpgradePath);
		if(!ZipExtract(strZipFile.c_str(),strDecFolder.c_str()))
		{
			return false;
		}

		return true;
	}

	std::vector<std::wstring> UpgradeFrame::TraverseDirectory(std::wstring& wstrDir)      
	{  
		std::vector<std::wstring> ret;
		WIN32_FIND_DATA FindFileData;  
		HANDLE hFind=INVALID_HANDLE_VALUE;  
		std::wstring wstrDirSpec = wstrDir;
		//	DWORD dwError;  
		wstrDirSpec +=TEXT("\\*");
		hFind=FindFirstFile(wstrDirSpec.c_str(),&FindFileData);       

		if(hFind==INVALID_HANDLE_VALUE)                           
		{  
			(void)FindClose(hFind);   
			return ret;    
		}  
		else   
		{  
			while(FindNextFile(hFind,&FindFileData)!=0)                          
			{  
				if((FindFileData.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)!=0&&wcscmp(FindFileData.cFileName,L".")==0||wcscmp(FindFileData.cFileName,L"..")==0)        
				{  
					continue;  
				}  
				if((FindFileData.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)!=0)
				{  
					std::wstring wstrDirAdd = wstrDir;
					wstrDirAdd +=TEXT("\\");
					wstrDirAdd +=FindFileData.cFileName;
					TraverseDirectory(wstrDirAdd);                               
				}  
				if((FindFileData.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)==0)    
				{  
					std::wstring wstrRet = wstrDir+L"\\"+FindFileData.cFileName;
					ret.push_back(wstrRet);
				}  
			}  
			(void)FindClose(hFind);  
			return ret;
		}  
	}  

	int32_t UpgradeFrame::getSHA256Signature(const std::wstring& localPath,	std::string& signature)
	{
		if (localPath.empty())
		{
			return RT_INVALID_PARAM;
		}

		int32_t ret = RT_OK;
		std::wstring wstrLocalPath=SD::Utility::String::replace_all(localPath,L"/",L"\\");
		const DWORD BUFSIZE = 4*1024*1024; // 4MB
		HCRYPTPROV hProv = 0;
		HCRYPTHASH hHash = 0;
		ALG_ID ai;
		SmartHandle hFile = NULL;
		DWORD cbRead = 0;
		DWORD cbHash = 0;
		CHAR rgbDigits[] = "0123456789abcdef";
		const DWORD SIGNATURELEN = 70;
		ai = CALG_SHA_256;
		std::auto_ptr<BYTE> rgbFile(new BYTE[BUFSIZE]);
		std::auto_ptr<BYTE> rgbHash(new BYTE[SIGNATURELEN]);

		hFile = CreateFile(std::wstring(L"\\\\?\\"+wstrLocalPath).c_str(),
			GENERIC_READ,
			FILE_SHARE_READ|FILE_SHARE_WRITE,
			NULL,
			OPEN_EXISTING,
			FILE_FLAG_SEQUENTIAL_SCAN,
			NULL);

		if (INVALID_HANDLE_VALUE == hFile)
		{
			ret = GetLastError();
			return ret;
		}

		// Get handle to the crypto provider
		if (!CryptAcquireContext(&hProv,
			NULL,
			NULL,
			PROV_RSA_AES,
			CRYPT_VERIFYCONTEXT))
		{
			ret = GetLastError();
			return ret;
		}

		if (!CryptCreateHash(hProv, ai, 0, 0, &hHash))
		{
			CryptReleaseContext(hProv, 0);
			ret = GetLastError();
			return ret;
		}

		while (true)
		{
			if (!ReadFile(hFile, rgbFile.get(), BUFSIZE, &cbRead, NULL))
			{
				CryptReleaseContext(hProv, 0);
				CryptDestroyHash(hHash);
				ret = GetLastError();
				return ret;
			}
			if (0 == cbRead)
			{
				break;
			}

			if (!CryptHashData(hHash, rgbFile.get(), cbRead, 0))
			{
				CryptReleaseContext(hProv, 0);
				CryptDestroyHash(hHash);
				ret = GetLastError();
				return ret;
			}
		}

		cbHash = SIGNATURELEN;
		(void)memset_s(rgbHash.get(), SIGNATURELEN, 0, SIGNATURELEN);
		if (CryptGetHashParam(hHash, HP_HASHVAL, rgbHash.get(), &cbHash, 0))
		{
			CHAR buf[SIGNATURELEN+1] = {0};
			for (DWORD i = 0; i < cbHash; i++)
			{
				buf[2*i] = rgbDigits[rgbHash.get()[i] >> 4];
				buf[2*i+1] = rgbDigits[rgbHash.get()[i] & 0xf];
			}
			signature = std::string(buf);
		}

		CryptDestroyHash(hHash);
		CryptReleaseContext(hProv, 0);

		return ret;
	}

	bool UpgradeFrame::checkClient(std::wstring clientType, std::wstring version)
	{
		MAKE_CLIENT(client);
		std::string featureCode = "";
		std::string signature = "";
		int32_t ret = client().getFeatureCode(SD::Utility::String::wstring_to_utf8(clientType),SD::Utility::String::wstring_to_utf8(version),featureCode);
		if (RT_OK !=ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "get  the server featureCode failed.");
			return false;
		}

		ret = getSHA256Signature(m_wstrZipFile,signature);
		if (0 ==ret)
		{
			if (_tcsicmp(SD::Utility::String::utf8_to_wstring(featureCode).c_str(),SD::Utility::String::utf8_to_wstring(signature).c_str())==0)
			{
				return true;
			}
			SERVICE_ERROR(MODULE_NAME,ret,"The SHA256 value is not equal,featureCode:%s,signature:%s",featureCode.c_str(),signature.c_str());
		}
		else
		{
			SERVICE_ERROR(MODULE_NAME,ret,"get the sha256 signature failed. ");
		}
		
		return false;
	}

	bool UpgradeFrame::ExecUpgrade()
	{
// 		if ( !checkClient(L"pccloud",m_newVersion) )
// 		{
// 			return false;
// 		}

		HANDLE hRead = NULL;
		HANDLE hWrite = NULL;
		SECURITY_ATTRIBUTES sa;	
		sa.nLength = sizeof(SECURITY_ATTRIBUTES);
		sa.lpSecurityDescriptor = NULL;
		sa.bInheritHandle = TRUE;
		std::wstring exeFile = L"\"";

		std::vector<std::wstring> vecFile = TraverseDirectory(m_wstrUpgradePath);
		for (size_t i=0;i<vecFile.size();i++)
		{
			if (vecFile[i].substr(vecFile[i].length()-4,4) == L".exe")
			{
				exeFile .append(vecFile[i]);
				exeFile.append(L"\"");
				break;
			}
		}

		if(!::CreatePipe(&hRead,&hWrite,&sa,0)) 
			return false;

		STARTUPINFOW si;
		si.cb = sizeof(STARTUPINFO);
		::GetStartupInfoW(&si); 
		si.hStdError = hWrite; 
		si.hStdOutput = hWrite;         
		si.wShowWindow = SW_SHOW;
		si.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
		PROCESS_INFORMATION pi;
		UpgradeType upgradeType = GetUpgradeType();
		switch (upgradeType)
		{
		case Normal:
			exeFile.append(L"     ");
			exeFile.append(L"/U");
			break;
		case Force:
			exeFile.append(L"     ");
			exeFile.append(L"/F");
			break;
		default:
			break;
		}

		try
		{
			if(!::CreateProcessW(NULL,(LPWSTR)exeFile.c_str(),NULL,NULL,TRUE,NULL,NULL,NULL,&si,&pi)) 
			{
				::CloseHandle(hWrite);
				::CloseHandle(hRead);

				return false;
			}
			::SendMessage(m_pBackWnd->GetHandle(),WM_CLOSE,0,0);
		}
		catch (...)
		{
			::CloseHandle(hWrite);
			::CloseHandle(hRead);
			return false;
		}

		::CloseHandle(hWrite);
		::CloseHandle(hRead);
		return true;
	}

	bool UpgradeFrame::RunUpgrade()
	{
		if(DownloadPackage()&&UnzipFile()&&ExecUpgrade())
		{
			return true;
		}
		return false;
	}

	void UpgradeFrame::SaveConfig(std::string& _return)
	{
		UpdateInfo updateInfo;
		MAKE_CLIENT(client);
		int32_t ret = client().getUpdateInfo(updateInfo);
		if (RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "save the config file failed. ");
		}
		_return = updateInfo.downloadUrl;

		WCHAR userTempPath[MAX_PATH] = {0};
		GetTempPath(MAX_PATH,userTempPath);

		m_wstrIniFilePath= userTempPath;
		m_wstrIniFilePath+= INIFILE;

		BOOL bRet = ::DeleteFile(m_wstrIniFilePath.c_str());
		if (!bRet && ERROR_FILE_NOT_FOUND != GetLastError()) return;
		std::wstring info = SD::Utility::String::utf8_to_wstring(updateInfo.versionInfo);

		FILE *fp;  
		errno_t err = _wfopen_s(&fp,m_wstrIniFilePath.c_str(),L"w+b,ccs=UNICODE");
		if (0 ==err )  
		{
			fputws(info.c_str(),fp);
		}
		fclose(fp);
	}

	void UpgradeFrame::SetUpgradeMsg()
	{
		CInIHelper iniHelper(m_wstrIniFilePath);
		std::wstring msg = L"";
		std::wstring upgradeMsg = L"";
		if(UI_LANGUGE::CHINESE ==iniLanguageHelper.GetLanguage())
		{
			msg = iniHelper.GetString(CONF_VERSION_SECTION,CONF_UPDATE_MSG_CN_KEY,L"");
		}
		else
		{
			msg = iniHelper.GetString(CONF_VERSION_SECTION,CONF_UPDATE_MSG_EN_KEY,L"");
		}

		if ( msg.empty() )
		{
			msg = iniLanguageHelper.GetMsgDesc(CONF_UPDATE_MSG_KEY);
		}

		while (true)
		{
			std::wstring::size_type pos = msg.find(MSGSEPARATE);
			if (std::wstring::npos != pos &&  (0 != pos))
			{
				std::wstring msgTemp = msg.substr(0,pos);
				msgTemp += L"\r\n";
				upgradeMsg +=msgTemp;
				msg = msg.substr(pos+1,msg.length()-pos-1);
			}
			else
			{
				upgradeMsg+= msg;
				break;
			}
		}
		CRichEditUI*  message = static_cast<CRichEditUI*>(m_PaintManager.FindControl(L"upgrade_msg"));
		message->SetText(upgradeMsg.c_str());
	}

	BOOL UpgradeFrame::IsUpgrade()
	{
		CInIHelper iniHelper(m_wstrIniFilePath);
		m_newVersion = iniHelper.GetString(CONF_VERSION_SECTION,CONF_VERSION_KEY,L"");
		std::wstring wstrOldVersion = L"";

		if(0 != SD::Utility::Registry::get(HKEY_LOCAL_MACHINE,ONEBOX_REG_PATH,VERSION_REG_NAME,wstrOldVersion))
		{
			if (0 != SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REGHKCU_PATH,VERSION_REG_NAME,wstrOldVersion))
			{
				return false;
			}
		}
		else if (!IsAdministratorUser())
		{
			return false;
		}

		// compare version number
		if (wstrOldVersion.empty() && m_newVersion.empty())
		{
			return false;
		}
		return Version(m_newVersion) > Version(wstrOldVersion);

	}

	void UpgradeFrame::Run()
	{
		if (!IsUpgrade())
		{
			return;
		}
		HRESULT Hr = ::CoInitialize(NULL);
		if( FAILED(Hr) ) 
			return;
		Create(m_pBackWnd->GetHandle(), _T("UpgradeFrame"), UI_WNDSTYLE_DIALOG, 0);
		CenterWindow();
		Show();
		(void)::SetActiveWindow (m_pBackWnd->GetHandle());
		::CoUninitialize();
	}

	std::auto_ptr<UpgradeMgr> UpgradeMgr::instance_;

	UpgradeMgr* UpgradeMgr::getInstance(UserContext*  userContext, HWND parent)
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new UpgradeFrame(userContext,parent));
		}
		return instance_.get();
	}
}
