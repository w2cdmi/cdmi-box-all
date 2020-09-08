// AutoStartOnebox.cpp : 定义控制台应用程序的入口点。
//
#include <iostream>
#include <Windows.h>
#include <Wininet.h>
#include <tchar.h>
// 隐藏控制台窗口

#pragma comment(linker,"/subsystem:\"windows\" /entry:\"wmainCRTStartup\"")

// 注册表中程序设置键值
#ifdef _WIN64
#ifndef Onebox_APP_REG
#define Onebox_APP_REG L"SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting"
#endif
#else
#ifndef Onebox_APP_REG
#define Onebox_APP_REG L"Software\\Chinasoft\\Onebox\\Setting"
#endif
#endif 

// 注册表中程序路径键
#ifndef Onebox_APP_REG_PATH
#define Onebox_APP_REG_PATH L"AppPath"
#endif

// 应用程序名
#ifndef Onebox_APP_NAME
#define Onebox_APP_NAME L"Onebox.exe"
#endif


#ifndef SHARE_DRIVE_UI_STOP_EVENT
#define SHARE_DRIVE_UI_STOP_EVENT (L"73830486-BA33-48EE-810B-069DB6868B49")
#endif

int  getRegValue(HKEY root, std::wstring key, std::wstring name, std::wstring& value )
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

static BOOL execApplication(std::wstring wstrCommand, std::wstring wstrParam)
{    
	if (L""== wstrCommand.c_str())
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
		if(!::CreateProcessW(NULL,(LPWSTR)wstrCommand.c_str(),NULL,NULL,TRUE,NULL,NULL,NULL,&si,&pi)) 
		{
			::CloseHandle(hWrite);
			::CloseHandle(hRead);

			return FALSE;
		}
	}
	catch (...)
	{
		WCHAR msg[100] = {0};
        //wsprintf(msg, L"Onebox start failed!\n.net%s may be disabled!", read_donet_ver().c_str());
        //MessageBox(NULL, msg, L"Onebox", MB_OK);
		::CloseHandle(hWrite);
		::CloseHandle(hRead);
		return FALSE;
	}

	::CloseHandle(hWrite);
	return TRUE;
}

int _tmain(int argc, _TCHAR* argv[])
{
	std::wstring wstrParam = L"";
	if (argc > 1)
	{
		wstrParam = argv[1];
	}
	
	std::wstring wstrOneboxInstallPath = L"";
	int iRet= getRegValue(HKEY_LOCAL_MACHINE,Onebox_APP_REG,Onebox_APP_REG_PATH,wstrOneboxInstallPath); 
	if (0 != iRet || wstrOneboxInstallPath.empty())
	{
		return -1;
	}

	std::wstring wstrOneboxAppPath =  L"\"" ;
	wstrOneboxAppPath.append(wstrOneboxInstallPath);
	wstrOneboxAppPath.append(L"\\");
    wstrOneboxAppPath.append(Onebox_APP_NAME);
	wstrOneboxAppPath.append(L"\"");
	execApplication(wstrOneboxAppPath,wstrParam);
	return 0;
}