// AutoStartOnebox.cpp : 定义控制台应用程序的入口点。
//
#include <iostream>
#include <Windows.h>
#include <Wininet.h>
#include <tchar.h>
#include <tinyxml.h>
#include <shlobj.h>
#include <memory>

#pragma comment(lib, "Shell32.lib")
#ifdef _DEBUG
	#pragma comment(lib, "tinyxml_d.lib")
#else
	#pragma comment(lib, "tinyxml.lib")
#endif
// 隐藏控制台窗口
#pragma comment(linker,"/subsystem:\"windows\" /entry:\"wmainCRTStartup\"")

#ifdef __cplusplus
extern "C"
{
#endif

/* 华为特有安全函数 */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

// 注册表中程序设置键值
#ifdef _WIN64
#ifndef Onebox_APP_REG
#define Onebox_APP_REG L"SOFTWARE\\Wow6432Node\\Huawei\\OneboxAPP\\Onebox\\Setting"
#endif
#else
#ifndef Onebox_APP_REG
#define Onebox_APP_REG L"Software\\Huawei\\OneboxAPP\\Onebox\\Setting"
#endif
#endif 

// 注册表中程序路径键
#ifndef Onebox_APP_REG_PATH
#define Onebox_APP_REG_PATH L"AppPath"
#endif

// 应用程序名
#ifndef Onebox_APP_NAME
#define Onebox_APP_NAME L"Onebox Mate.exe"
#endif


#ifndef SHARE_DRIVE_UI_STOP_EVENT
#define SHARE_DRIVE_UI_STOP_EVENT (L"D70AFFD6-67D3-457B-B027-9A9D66636830")
#endif

std::wstring wstrExplorerCmd = L"%SystemRoot%\\explorer.exe";
static BOOL execute_doscmd(LPCWSTR pszCmd, std::string &strAppData);
static int get(HKEY root, const std::wstring& key, const std::wstring& name, std::wstring& value);
static int set(HKEY root, const std::wstring& key, const std::wstring& name, const std::wstring& value);
//static int del(HKEY root, const std::wstring& key, const std::wstring& name);
static BOOL GetCurrentUserAndDomain(PTSTR szUser, PDWORD pcchUser, PTSTR szDomain, PDWORD pcchDomain);
static bool updataInfoToXml(std::wstring& sPath);
static int CreateTask();
static int cancelTask();
static bool getInstallPath(std::wstring& _return);

static BOOL execApplication(std::wstring wstrCommand, std::wstring wstrParam)
{    
	if (wstrCommand.empty())
	{
		return FALSE;
	}

	HANDLE hRead,hWrite;
	SECURITY_ATTRIBUTES sa;	
	sa.nLength = sizeof(SECURITY_ATTRIBUTES);
	sa.lpSecurityDescriptor = NULL;
	sa.bInheritHandle = TRUE;

	if(!::CreatePipe(&hRead,&hWrite,&sa,0)) 
		return FALSE;

	STARTUPINFOW si;
	si.cb = sizeof(STARTUPINFO);
	::GetStartupInfoW(&si); 
	si.hStdError = hWrite; 
	si.hStdOutput = hWrite;         
	si.wShowWindow = SW_SHOW;
	si.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
	PROCESS_INFORMATION pi;	
	wstrCommand.append(L"     ");
	wstrCommand.append(wstrParam);

	try
	{
		if(!::CreateProcess(NULL,(LPWSTR)wstrCommand.c_str(),NULL,NULL,TRUE,NULL,NULL,NULL,&si,&pi)) 
		{
			::CloseHandle(hWrite);
			::CloseHandle(hRead);

			return FALSE;
		}
	}
	catch (...)
	{
		WCHAR msg[100] = {0};
		::CloseHandle(hWrite);
		::CloseHandle(hRead);
		return FALSE;
	}

	::CloseHandle(hWrite);
	return TRUE;
}

static BOOL execute_doscmd(LPCWSTR pszCmd, std::string &strAppData)
{    
	HANDLE hRead,hWrite;

	SECURITY_ATTRIBUTES sa;	
	sa.nLength = sizeof(SECURITY_ATTRIBUTES);
	sa.lpSecurityDescriptor = NULL;
	sa.bInheritHandle = TRUE;

	if(!::CreatePipe(&hRead,&hWrite,&sa,0)) 
		return FALSE;

	std::wstring strCmd = L"Cmd.exe /C ";
	strCmd += pszCmd;

	STARTUPINFOW si;
	si.cb = sizeof(STARTUPINFO);
	::GetStartupInfoW(&si); 
	si.hStdError = hWrite; 
	si.hStdOutput = hWrite;         
	si.wShowWindow = SW_HIDE;
	si.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;

	PROCESS_INFORMATION pi;	
	std::wstring strwtemp = pszCmd;
	std::wstring strCurrentDir = L"";
	strwtemp = strwtemp.substr(strwtemp.length()-wstrExplorerCmd.length(),wstrExplorerCmd.length());
	BOOL bCreateProcess = TRUE;
	if (0 ==strwtemp.compare(wstrExplorerCmd) )
	{
		TCHAR szPath[MAX_PATH]; 
		ZeroMemory(szPath, MAX_PATH); 
		(void)SHGetSpecialFolderPath(NULL, szPath,  CSIDL_WINDOWS , FALSE);
		strCurrentDir = szPath;
		bCreateProcess = ::CreateProcess(NULL,(LPWSTR)strCmd.c_str(),NULL,NULL,TRUE,NULL,NULL,strCurrentDir.c_str(),&si,&pi); 
	}
	else
	{
		bCreateProcess =::CreateProcess(NULL,(LPWSTR)strCmd.c_str(),NULL,NULL,TRUE,NULL,NULL,NULL,&si,&pi);
	}
	if(!bCreateProcess) 
	{
		::CloseHandle(hWrite);
		::CloseHandle(hRead);

		return FALSE;
	}

	::CloseHandle(hWrite);

	CHAR buffer[4097] = {0};
	DWORD bytesRead; 

	if (0 !=strwtemp.compare(wstrExplorerCmd) )
	{
		while(true) 
		{
			if(!::ReadFile(hRead,buffer,4096,&bytesRead,NULL))
				break;
			buffer[4096] = '\0';
			strAppData += buffer;

			(void)memset_s(buffer,sizeof(buffer),0,sizeof(buffer));
		}

		::CloseHandle(hRead); 
	}

	return TRUE;
}

static int get(HKEY root, const std::wstring& key, const std::wstring& name, std::wstring& value)
{
	LONG ret;
	HKEY hkey = NULL;
	DWORD cb = MAX_PATH;
	std::auto_ptr<BYTE> buf(new BYTE[cb]);
	(void)memset_s(buf.get(), cb, 0, cb);

	ret = RegOpenKeyEx(root , 
		(LPCWSTR)(key.c_str()),
		NULL,  
		KEY_QUERY_VALUE,
		&hkey);
	if (ERROR_SUCCESS != ret)
	{
		return ret;
	}

	ret = RegQueryValueEx(hkey,  
		(LPCWSTR)(name.c_str()),  
		NULL, 
		NULL,  
		(LPBYTE)buf.get(), 
		&cb);
	if (ERROR_MORE_DATA == ret)
	{
		buf.reset(new BYTE[cb]);
		ret = RegQueryValueEx(hkey,  
			(LPCWSTR)(name.c_str()),  
			NULL, 
			NULL,  
			(LPBYTE)buf.get(), 
			&cb);
	}
	if (ERROR_SUCCESS != ret)
	{
		RegCloseKey(hkey);
		return ret;
	}
	RegCloseKey(hkey);

	value = (wchar_t*)buf.get();

	return 0;
}

static BOOL GetCurrentUserAndDomain(PTSTR szUser, PDWORD pcchUser, 
									PTSTR szDomain, PDWORD pcchDomain) {

										BOOL         fSuccess = FALSE;
										HANDLE       hToken   = NULL;
										HANDLE		 processHandle = NULL;
										PTOKEN_USER  ptiUser  = NULL;
										DWORD        cbti     = 0;
										SID_NAME_USE snu;

										__try {

											// Get the calling thread's access token.
											if (!OpenThreadToken(GetCurrentThread(), TOKEN_QUERY, TRUE,
												&hToken)) {

													if (GetLastError() != ERROR_NO_TOKEN)
														__leave;

													// Retry against process token if no thread token exists.
													if (!OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY, 
														&hToken))
														__leave;
											}

											// Obtain the size of the user information in the token.
											if (GetTokenInformation(hToken, TokenUser, NULL, 0, &cbti)) {

												// Call should have failed due to zero-length buffer.
												__leave;

											} else {

												// Call should have failed due to zero-length buffer.
												if (GetLastError() != ERROR_INSUFFICIENT_BUFFER)
													__leave;
											}

											processHandle = GetProcessHeap();
											if(processHandle)
											{
												// Allocate buffer for user information in the token.
												ptiUser = (PTOKEN_USER) HeapAlloc(processHandle, 0, cbti);
											}
											if (!ptiUser)
												__leave;

											// Retrieve the user information from the token.
											if (!GetTokenInformation(hToken, TokenUser, ptiUser, cbti, &cbti))
												__leave;

											// Retrieve user name and domain name based on user's SID.
											if (!LookupAccountSid(NULL, ptiUser->User.Sid, szUser, pcchUser, 
												szDomain, pcchDomain, &snu))
												__leave;

											fSuccess = TRUE;

										} __finally {

											// Free resources.
											if (hToken)
												CloseHandle(hToken);

											if (ptiUser)
											{
												HeapFree(processHandle, 0, ptiUser);
											}
										}

										return fSuccess;
}

static int set(HKEY root, const std::wstring& key, const std::wstring& name, const std::wstring& value)
{
	LONG ret;
	HKEY hkey = NULL;

	ret = RegCreateKeyEx(root , 
		(LPCWSTR)(key.c_str()),
		0, 
		NULL, 
		REG_OPTION_NON_VOLATILE, 
		KEY_SET_VALUE, 
		NULL, 
		&hkey, 
		NULL);
	if (ERROR_SUCCESS != ret)
	{
		return ret;
	}

	ret = RegSetValueEx(hkey,  
		(LPCWSTR)(name.c_str()),  
		0, 
		REG_SZ,  
		(LPBYTE)value.c_str(),
		(DWORD)(value.length()+1)*sizeof(wchar_t));

	RegCloseKey(hkey);

	return ret;
}

static int del(HKEY root, const std::wstring& key, const std::wstring& name)
{
	LONG ret;
	HKEY hkey = NULL;
	ret = RegOpenKeyEx(root,
		(LPCWSTR)key.c_str(),
		NULL,
		KEY_ALL_ACCESS,
		&hkey);
	if (ERROR_SUCCESS != ret)
	{
		return ret;
	}
	ret =RegDeleteValue(hkey,name.c_str());
	RegCloseKey(hkey);
	return ret;
}

static bool getInstallPath(std::wstring& _return)
{
	int ret = get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Huawei\\OneboxAPP\\Onebox\\Setting",Onebox_APP_REG_PATH, _return);
	if (0 != ret)
	{
		ret = get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Wow6432Node\\Huawei\\OneboxAPP\\Onebox\\Setting", Onebox_APP_REG_PATH, _return);
		if (0 != ret)
		{
			return false;
		}		
	}

	return true;
}
static bool updataInfoToXml(std::wstring& sPath)
{
	char path[_MAX_PATH] ="";
	char userName[_MAX_PATH] ="";
	char exe[MAX_PATH] = "";
	std::wstring wpath,wCurrentDomian,wCurrentUserName;

	if(!getInstallPath(wpath))
	{
		return false;
	}
	std::wstring tmp = wpath;
	wpath += L"\\setting.xml";
	tmp += L"\\OneboxStart.exe";
	WideCharToMultiByte(CP_ACP,0,wpath.c_str(),-1,path,_MAX_PATH,NULL,NULL);
	WideCharToMultiByte(CP_ACP,0,tmp.c_str(),-1,exe,_MAX_PATH,NULL,NULL);

	sPath = wpath;

	//获取用户名
	wchar_t str1[MAX_PATH] = L"";
	wchar_t str2[MAX_PATH] = L"";
	DWORD len = MAX_PATH;
	DWORD len2 = MAX_PATH;

	GetCurrentUserAndDomain(str1,&len,str2,&len2);

	wCurrentDomian = str2;
	wCurrentDomian += L"\\";
	wCurrentDomian += str1;
	WideCharToMultiByte(CP_ACP,0,wCurrentDomian.c_str(),-1,userName,_MAX_PATH,NULL,NULL);


	TiXmlDocument myDocument(path);

	if (true == myDocument.LoadFile())
	{
		TiXmlElement *rootElem = myDocument.RootElement();
		if (rootElem != NULL)
		{
			TiXmlElement *Principals = rootElem->FirstChildElement("Principals");
			TiXmlElement *Actions = rootElem->FirstChildElement("Actions");
			if (Principals != NULL)
			{
				TiXmlElement *Principal = Principals->FirstChildElement("Principal");
				if (Principal != NULL)
				{
					TiXmlElement *UserId = Principal->FirstChildElement("UserId");
					if (UserId != NULL)
					{
						TiXmlNode* node = UserId->FirstChild();
						if (node != NULL)
						{
							node->SetValue(userName);
						}
					}
				}
			}
			if (Actions != NULL)
			{
				TiXmlElement *Exec = Actions->FirstChildElement("Exec");
				if (Exec != NULL)
				{
					TiXmlElement *Command = Exec->FirstChildElement("Command");
					if (Command != NULL)
					{
						TiXmlNode* node = Command->FirstChild();
						if (node != NULL)
						{
							node->SetValue(exe);
						}
					}
				}
			}
		}
		myDocument.SaveFile(path);
		return true;
	}
	return false;
}

static int CreateTask()
{
	std::string strData = "";
	std::wstring path = L"";

	//std::wstring strCmd = L"schtasks /delete /TN onebox /F";
	std::wstring strCmd = L"schtasks /create /Tn OneboxCloud /xml \"";
	cancelTask();
	bool nbool = updataInfoToXml(path);
	if (nbool)
	{
		strCmd += path;
		strCmd += L"\"";
		(void)execute_doscmd(strCmd.c_str(),strData);

		int iWstrLen =  MultiByteToWideChar(CP_ACP,0,strData.c_str(),-1,NULL,0);
		LPWSTR lpwsBuf = new(std::nothrow) WCHAR[iWstrLen];
		if(NULL != lpwsBuf)
		{
			int nResult = MultiByteToWideChar(CP_ACP,0,strData.c_str(),(int)strData.size(),lpwsBuf,iWstrLen);
			std::wstring wbuf;
			if(nResult > 0)
			{
				wbuf = lpwsBuf;
			}
			delete []lpwsBuf;
			if (wbuf.find(L"ERROR") != std::wstring::npos || wbuf.find(L"错误") != std::wstring::npos)
			{
				//创建成功，加入RUN
				std::wstring tmp = L"\"";
				tmp += path;
				size_t index = tmp.rfind(L"\\");
				if (index != std::wstring::npos)
				{
					tmp.replace(index,tmp.size(),L"");
				}

				tmp += L"\\OneboxStart.exe\" schtask";
				set(HKEY_LOCAL_MACHINE,L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",L"OneboxCloud",tmp);
			}
		}
	}
	else
	{
		std::wstring InstallPath,ExePath = L"\"" ;
		(void)getInstallPath(InstallPath);
		InstallPath += L"\\OneboxStart.exe\" schtask";
		ExePath += InstallPath;
		set(HKEY_LOCAL_MACHINE,L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",L"OneboxCloud",ExePath);
	}
	return 0;
}

static int cancelTask()
{
	std::string strData;
	std::wstring path;
	std::wstring strCmd = L"schtasks /delete /TN OneboxCloud /F";

	execute_doscmd(strCmd.c_str(),strData);
	del(HKEY_LOCAL_MACHINE,L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",L"OneboxCloud");
	return 0;
}

static void runApplication(std::wstring wstrParam)
{
	std::wstring wstrOneboxInstallPath = L"";
   if(!getInstallPath(wstrOneboxInstallPath))
   {
	   return;
   }
	std::wstring wstrOneboxAppPath =  L"\"" ;
	wstrOneboxAppPath.append(wstrOneboxInstallPath);
	wstrOneboxAppPath.append(L"\\");
	wstrOneboxAppPath.append(Onebox_APP_NAME);
	wstrOneboxAppPath.append(L"\"");
	execApplication(wstrOneboxAppPath,wstrParam);
}

int _tmain(int argc, _TCHAR* argv[])
{
	std::wstring wstrParam = L"";
	if (argc > 1)
	{
		wstrParam = argv[1];
	}
	
	if (!wstrParam.find(_T("/createTask")))
	{
		CreateTask();
	}
	else if (!wstrParam.find(_T("/cancelTask")))
	{
		cancelTask();
	}
	else if (!wstrParam.find(_T("schtask")))
	{
		runApplication(L"autostart");
	}
	else
	{
		runApplication(wstrParam);
	}
	return 0;
}