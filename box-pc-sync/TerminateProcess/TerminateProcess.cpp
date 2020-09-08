// TerminateProcess.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <windows.h>
#include <string>
#include <Psapi.h>
#include "./tinyxml/tinyxml.h"
#include <shlobj.h>

#pragma comment(lib, "Shell32.lib")
#pragma comment(linker,"/subsystem:\"windows\" /entry:\"wmainCRTStartup\"")

static BOOL execute_doscmd(LPCWSTR pszCmd, std::string &strAppData);
static BOOL TerminateMyProcess(LPCWSTR pszCmd);
std::wstring wstrExplorerCmd = L"%SystemRoot%\\explorer.exe";

static int get(HKEY root, const std::wstring& key, const std::wstring& name, std::wstring& value);
static int set(HKEY root, const std::wstring& key, const std::wstring& name, const std::wstring& value);
static BOOL GetCurrentUserAndDomain(PTSTR szUser, PDWORD pcchUser, PTSTR szDomain, PDWORD pcchDomain);
static bool updataInfoToXml(std::wstring& sPath);
static int CreateTask();
static int cancelTask();
int _tmain(int argc, _TCHAR* argv[])
{
	std::wstring  wstrCmdLine1 = L"";
	std::wstring  wstrCmdLine2 = L"";
	if(argc > 1)
	{
		wstrCmdLine1 = argv[1];
	}

	if (argc>2)
	{
		wstrCmdLine2 = argv[2];
	}
		
	std::wstring strCmd = L"";
	strCmd =L"%SystemRoot%\\System32\\Taskkill  /F /FI " ;

	if (!wstrCmdLine1.find(_T("/name")))
	{
		strCmd +=L"\"imagename  eq ";
	}
	if (!wstrCmdLine1.find(_T("/title")))
	{
		strCmd +=L"\"WINDOWTITLE  eq ";
	}	
	if (!wstrCmdLine1.find(_T("/createTask")))
	{
		CreateTask();
	}
	if (!wstrCmdLine1.find(_T("/cancelTask")))
	{
		cancelTask();
	}

	strCmd +=wstrCmdLine2;
	strCmd +=L"\"";

	std::string strData;
	execute_doscmd(strCmd.c_str(),strData);
	std::wstring wstrexplorer = L"explorer.exe";
	if (0 == wstrCmdLine2.compare(wstrexplorer))
	{
		execute_doscmd(wstrExplorerCmd.c_str(),strData);
	}
	

	return 0;
}

static BOOL TerminateMyProcess(LPCWSTR pszCmd)
{
	DWORD aps[1024], cbNeeded, cbNeeded2, i;
	TCHAR buffer[256];
	HANDLE hProcess;
	HMODULE hModule;

	EnumProcesses(aps, sizeof(aps), &cbNeeded);
	for(i = 0; i < cbNeeded/sizeof(DWORD); ++i)
	{
		if(!(hProcess = OpenProcess(PROCESS_TERMINATE |
			PROCESS_QUERY_INFORMATION |
			PROCESS_VM_READ,FALSE, aps[i])))
			continue;
		if(!EnumProcessModules(hProcess, &hModule, 
			sizeof(hModule), &cbNeeded2))
			continue;
		GetModuleBaseName(hProcess, hModule, buffer, 256);
		std::wstring strProcessPath(buffer);
		if (strProcessPath==pszCmd)
		{
			TerminateProcess(hProcess, 0);
			CloseHandle(hProcess);
			break;
		}
		CloseHandle(hProcess);
	}

	return 0;
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
		SHGetSpecialFolderPath(NULL, szPath,  CSIDL_WINDOWS , FALSE);
		strCurrentDir = szPath;
		bCreateProcess = ::CreateProcessW(NULL,(LPWSTR)strCmd.c_str(),NULL,NULL,TRUE,NULL,NULL,strCurrentDir.c_str(),&si,&pi); 
	}
	else
	{
		bCreateProcess =::CreateProcessW(NULL,(LPWSTR)strCmd.c_str(),NULL,NULL,TRUE,NULL,NULL,NULL,&si,&pi);
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

			strAppData += buffer;

			::memset(buffer,0,sizeof(buffer));
		}

		::CloseHandle(hRead); 
	}

	return TRUE;
}

static int get(HKEY root, const std::wstring& key, const std::wstring& name, std::wstring& value)
{
	LONG ret;
	HKEY hkey;
	DWORD cb = MAX_PATH;
	BYTE buf[MAX_PATH];
	memset(buf, 0, cb);

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
		(LPBYTE)buf, 
		&cb);
	if (ERROR_MORE_DATA == ret)
	{
		//buf.reset(new BYTE[cb]);
		memset(buf, 0, cb);
		ret = RegQueryValueEx(hkey,  
			(LPCWSTR)(name.c_str()),  
			NULL, 
			NULL,  
			(LPBYTE)buf, 
			&cb);
	}
	if (ERROR_SUCCESS != ret)
	{
		RegCloseKey(hkey);
		return ret;
	}
	RegCloseKey(hkey);

	//value.append((wchar_t*)buf.get());
	value = (wchar_t*)buf;

	return 0;
}
static BOOL GetCurrentUserAndDomain(PTSTR szUser, PDWORD pcchUser, 
      PTSTR szDomain, PDWORD pcchDomain) {

   BOOL         fSuccess = FALSE;
   HANDLE       hToken   = NULL;
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

      // Allocate buffer for user information in the token.
      ptiUser = (PTOKEN_USER) HeapAlloc(GetProcessHeap(), 0, cbti);
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
         HeapFree(GetProcessHeap(), 0, ptiUser);
   }

   return fSuccess;
}

static int set(HKEY root, const std::wstring& key, const std::wstring& name, const std::wstring& value)
{
	LONG ret;
	HKEY hkey;

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
		(value.length()+1)*sizeof(wchar_t));

	RegCloseKey(hkey);

	return ret;
}

static bool updataInfoToXml(std::wstring& sPath)
{
	char path[_MAX_PATH] ="";
	char userName[_MAX_PATH] ="";
	char exe[MAX_PATH] = "";
	//TCHAR PathBuff[_MAX_PATH] = _T("");
	std::wstring wpath,wCurrentDomian,wCurrentUserName;

	//获取安装路劲
	int ret = get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Chinasoft\\Onebox\\Setting", L"AppPath", wpath);
	if (0 != ret)
	{
		ret = get(HKEY_LOCAL_MACHINE, L"SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting", L"AppPath", wpath);
		if (0 != ret)
		{
			return false;
		}		
	}
	std::wstring tmp = wpath;
 	wpath += L"\\setting.xml";
	tmp += L"\\OneboxStart.exe";
// 	wcscat(PathBuff,wpath.c_str());
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
//	memset(path,0,MAX_PATH);
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
	std::wstring strCmd = L"schtasks /create /Tn Onebox /xml \"";
	cancelTask();
	bool nbool = updataInfoToXml(path);
	if (nbool)
	{
		std::wstring wbuf = L"";
		WCHAR buf[MAX_PATH]=L"";
		strCmd += path;
		strCmd += L"\"";
		BOOL isTrue = execute_doscmd(strCmd.c_str(),strData);
		MultiByteToWideChar(CP_ACP,0,strData.c_str(),strData.size(),buf,MAX_PATH);
		wbuf = buf;
		if (wbuf.find(L"ERROR") == std::wstring::npos || wbuf.find(L"错误") == std::wstring::npos)
		{//创建失败，加入RUN
			std::wstring tmp = L"\"" + path;
			int index = tmp.rfind(L"\\");
			if (index != std::wstring::npos)
			{
				tmp.replace(index,tmp.size(),L"");
			}

			tmp += L"\\OneboxStart.exe\" autorun";
			set(HKEY_LOCAL_MACHINE,L"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",L"Onebox",tmp);
		}
	}
	return 0;
}

static int cancelTask()
{
	std::string strData;
	std::wstring path;
	std::wstring strCmd = L"schtasks /delete /TN onebox /F";
	
	execute_doscmd(strCmd.c_str(),strData);
	
	return 0;
}