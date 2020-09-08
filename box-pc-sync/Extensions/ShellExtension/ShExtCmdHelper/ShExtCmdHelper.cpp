#include <tchar.h>
#include <xstring>
#include <windows.h>
#include <Shellapi.h>
#include <fstream>
#include <sstream>
#include <atlconv.h>
#include <shellapi.h>
#include "shlobj.h"
#include <io.h>

#ifndef VIRTYAL_FOLDER_ID
#define VIRTYAL_FOLDER_ID (L"{014D7A82-9589-42DC-8CF2-326F83755D4C}")
#endif

/*****************************************************************************/
/* 1: ShExtCmdHelper.exe [command] [param1] [param2] ...                              /
/* 2: command: install 
							 uninstall 
							 add-virtual-folder 
							 delete-virtual-folder          
							 update-virtual-folder
							 open-virtual-folder
    3: param: id
	                  name
					  path
					  icon
/*****************************************************************************/

static int install();
static int uninstall();
static int add_virtual_folder(const std::wstring& id, const std::wstring& name, const std::wstring& path, const std::wstring& icon);
static int delete_virtual_folder(const std::wstring& id);
static int update_virtual_folder(const std::wstring& id, const std::wstring& name, const std::wstring& path, const std::wstring& icon);
static int open_virtual_folder(const std::wstring& path);
static int changeVirtualFolderIcon(const std::wstring& path, const std::wstring& name, const std::wstring& icon);
static int removeVirtualFolderIcon(const std::wstring& id);

static void CreateShortCut(const std::wstring  showName, const std::wstring srcPath, const std::wstring desLinkPath,const std::wstring icon);
static std::string   wstring_to_string(const std::wstring &wstr);
static void AddToFavorite(const std::wstring showName,const std::wstring srcPath,const std::wstring icon);
static int removeFavoriteShotCut(const std::wstring& id);

int _tmain(int argc, _TCHAR* argv[])
{
	if (argc < 2)
	{
		printf("invalid param.\n");
		return 0;
	}

	std::wstring command = argv[1];
	std::wstring id = L"";
	std::wstring name = L"";
	std::wstring path = L"";
	std::wstring icon = L"";

	for (int i = 2; i < argc; ++i)
	{
		std::wstring param = argv[i];
		int len = sizeof(L"id");
		if (0 == wcscmp(L"id", param.substr(0, sizeof(L"id") / sizeof(wchar_t) - 1).c_str()))
		{
			id = param.substr(sizeof(L"id") / sizeof(wchar_t)).c_str();
		}
		else if (0 == wcscmp(L"name", param.substr(0, sizeof(L"name") / sizeof(wchar_t) - 1).c_str()))
		{
			name = param.substr(sizeof(L"name") / sizeof(wchar_t)).c_str();
		}
		else if (0 == wcscmp(L"path", param.substr(0, sizeof(L"path") / sizeof(wchar_t) - 1).c_str()))
		{
			path = param.substr(sizeof(L"path") / sizeof(wchar_t)).c_str();
		}
		else if (0 == wcscmp(L"icon", param.substr(0, sizeof(L"icon") / sizeof(wchar_t) - 1).c_str()))
		{
			icon = param.substr(sizeof(L"icon") / sizeof(wchar_t)).c_str();
		}
	}

	id = id.empty() ? VIRTYAL_FOLDER_ID : id;

	if (0 == wcscmp(L"install", command.c_str()))
	{
		return install();
	}
	else if (0 == wcscmp(L"uninstall", command.c_str()))
	{
		return uninstall();
	}
	else if(0 == wcscmp(L"add-virtual-folder", command.c_str()))
	{
		return add_virtual_folder(id, name, path, icon);
	}
	else if (0 == wcscmp(L"delete-virtual-folder", command.c_str()))
	{
		return delete_virtual_folder(id);
	}
	else if (0 == wcscmp(L"update-virtual-folder", command.c_str()))
	{
		return update_virtual_folder(id, name, path, icon);
	}
	else if (0 == wcscmp(L"open-virtual-folder", command.c_str()))
	{
		return open_virtual_folder(path);
	}

	return 0;
}

int install()
{
	return 0;
}

int uninstall()
{
	return 0;
}



//收藏夹下创建同步文件夹的快捷方式
void CreateShortCut(const std::wstring  showName, const std::wstring srcPath, const std::wstring desLinkPath,const std::wstring icon)
{
    // 参数 lpszExe: EXE 文件全路径名
    // 参数 lpszLnk: 快捷方式文件全路径名
    PCTSTR lpszExe = srcPath.c_str(); 
    LPCTSTR lpszLnk=desLinkPath.c_str();

    ::CoInitialize( NULL );
    IShellLink * psl = NULL;
    IPersistFile * ppf = NULL;

    HRESULT hr = ::CoCreateInstance(  // 启动组件
        CLSID_ShellLink,      // 快捷方式 CLSID
        NULL,                 // 聚合用(注4)
        CLSCTX_INPROC_SERVER, // 进程内(Shell32.dll)服务
        IID_IShellLink,       // IShellLink 的 IID
        (LPVOID *)&psl );     // 得到接口指针

    if ( SUCCEEDED(hr) )
    {
        //psl->SetPath( );  // 全路径程序名
        // psl->SetArguments();      // 命令行参数
        // psl->SetDescription();    // 备注
        // psl->SetHotkey();         // 快捷键
        // psl->SetIconLocation();   // 图标
        // psl->SetShowCmd();        // 窗口尺寸
        psl->SetPath( lpszExe );  // 全路径程序名
        psl->SetIconLocation(icon.c_str(),0);   // 图标

        // 根据 EXE 的文件名，得到目录名
        TCHAR szWorkPath[ MAX_PATH ];
        ::lstrcpy( szWorkPath, lpszExe );
        LPTSTR lp = szWorkPath;
        while( *lp )    lp++;
        while( '\\' != *lp )    lp--;
        *lp=0;

        // 设置 EXE 程序的默认工作目录
        psl->SetWorkingDirectory( szWorkPath );
        hr = psl->QueryInterface(  // 查找持续性文件接口指针
            IID_IPersistFile,      // 持续性接口 IID
            (LPVOID *)&ppf );      // 得到接口指针

        if ( SUCCEEDED(hr) )
        {
            USES_CONVERSION;       // 转换为 UNICODE 字符串
            ppf->Save( T2COLE( lpszLnk ), TRUE );  // 保存
        }
    }
    if ( ppf )    ppf->Release();
    if ( psl )    psl->Release();
    ::CoUninitialize();
}


std::string  wstring_to_string(const std::wstring& wstr)
{
    std::string  str;
    if(wstr.empty())
    {
        return str;
    }

    int istrLen = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,NULL,0,NULL,NULL);

    LPSTR lpsBuf = new(std::nothrow) CHAR[istrLen];
    //new出错判断
    if(NULL == lpsBuf)
    {
        return str;
    }

    memset(lpsBuf,0,istrLen * sizeof(CHAR));
    int nResult = WideCharToMultiByte(CP_ACP,0,wstr.c_str(),-1,lpsBuf,istrLen,NULL,NULL);	

    //转码出错处理
    if(0 == nResult)
    { 
        delete []lpsBuf;
        return str;
    }

    str = lpsBuf;
    delete []lpsBuf;
    return  str;
}


void AddToFavorite(const std::wstring showName,const std::wstring srcPath,const std::wstring icon)
{
    //获取当前登陆用户名，拼装收藏夹快捷方式地址
    wchar_t userName[MAX_PATH];
    std::wstring sUsername;
    DWORD size=MAX_PATH;
    ::GetUserName(userName,&size);
    sUsername =  std::wstring(userName);
    std::wstring linkDesPath = L"C:\\Users\\"+sUsername + L"\\Links\\"+showName+L".lnk";  //举例C:\\Users\\l00100468\\Links\\Huawei CloudDrive.lnk

    //检查快捷方式是否存在，不存在则创建
    std::string strLinkFilePath = wstring_to_string(linkDesPath);
    FILE* fp;
    fopen_s(&fp, strLinkFilePath.c_str(),"r");
    if(fp == NULL)
    {
        CreateShortCut(showName,srcPath,linkDesPath,icon);
		return;
    }
	fclose(fp);
}

//添加同步目录展示
int add_virtual_folder(const std::wstring& id, const std::wstring& name, const std::wstring& path, const std::wstring& icon)
{
	if (id.empty() || name.empty() || path.empty() || icon.empty())
	{
		return -1;
	}
	
	//先清空老的文件:避免老版本注册表未清除情况；
	delete_virtual_folder(id);

	//添加收藏夹显示
	AddToFavorite(name,path,icon); 

	std::wstring  wstrName = L"\""+ name +L"\"";
	std::wstring  wstrPath = L"\""+ path +L"\"";

	//添加资源管理器快捷方式显示：
	//更改注册表的写入结构
	std::wstring  HKey_root[] = {L"HKEY_CLASSES_ROOT",L"HKEY_CURRENT_USER\\Software\\Classes"};
	wchar_t  szCommand[512] = {0};
	for(int i=0;i<2;i++)
	{
		//HKEY_CLASSES_ROOT\CLSID\"下New Key: {CLSID}    默认值，InfoTip,LocalizedString
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s /ve /t REG_SZ /d \"%s\" /f", 
			HKey_root[i].c_str(),id.c_str(), wstrName.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		memset(szCommand, 0, 512*sizeof(wchar_t));
		//InfoTip
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s /v %s /t REG_SZ /d %s /f", 
		HKey_root[i].c_str(),id.c_str(), L"InfoTip", wstrName.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		memset(szCommand, 0, 512*sizeof(wchar_t));
		//LocalizedString
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s /v %s /t REG_SZ /d %s /f", 
		HKey_root[i].c_str(),id.c_str(), L"LocalizedString",wstrName.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		memset(szCommand, 0, 512*sizeof(wchar_t));
		
		//HKEY_CLASSES_ROOT\CLSID\{CLSID}"下New Key:DefaultIcon
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\DefaultIcon /ve /t REG_SZ /d \"%s\" /f", 
			HKey_root[i].c_str(),id.c_str(), icon.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//MessageBox(NULL, szCommand, L"ShareDrive_reg", MB_OK); 
		
		//HKEY_CLASSES_ROOT\CLSID\{CLSID}"下New Key:InprocServer32
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\InprocServer32 /ve /t REG_SZ /d \"%s\" /f", 
		HKey_root[i].c_str(),id.c_str(), L"shdocvw.dll");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//ThreadingModel
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\InprocServer32 /v %s /t REG_SZ /d %s /f", 
		HKey_root[i].c_str(),id.c_str(), L"ThreadingModel", L"Apartment");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);

		//HKEY_CLASSES_ROOT\CLSID\{CLSID}"下New Key: Instance, 默认值，子项：InitPropertyBag
		//默认值
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\Instance /ve /f", 
			HKey_root[i].c_str(),id.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//值：CLSID
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\Instance /v %s /t REG_SZ /d %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"CLSID", L"{0AFACED1-E828-11D1-9187-B532F1E9575D}");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		// 子项：InitPropertyBag
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\Instance\\InitPropertyBag /v %s /t REG_SZ /d %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"Target", wstrPath.c_str());
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//MessageBox(NULL, szCommand, L"ShareDrive_reg", MB_OK); 

		//HKEY_CLASSES_ROOT\CLSID\{CLSID}"下New Key:ShellFolder. 值：Attributes,PinToNameSapceTree,QueryForOverlay,wantsFORPARSING
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\ShellFolder /v %s /t REG_DWORD /d %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"Attributes",  L"4169142600");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//PinToNameSapceTree
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\ShellFolder /v %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"PinToNameSpaceTree");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//QueryForOverlay
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\ShellFolder /v %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"QueryForOverlay");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
		//wantsFORPARSING
		memset(szCommand, 0, 512*sizeof(wchar_t));
		wsprintf(szCommand, L"ADD %s\\CLSID\\%s\\ShellFolder /v %s /f", 
			HKey_root[i].c_str(),id.c_str(), L"wantsFORPARSING");
		ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);
	}

	//添加至计算机显示：HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\MyComputer\\NameSpace\\{ClSID}
	memset(szCommand, 0, 512*sizeof(wchar_t));
	wsprintf(szCommand, L"ADD HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\MyComputer\\NameSpace\\%s /ve /d \"%s\" /f", 
	id.c_str(), name.c_str());
	ShellExecute(NULL,  NULL, L"REG",szCommand, NULL, SW_HIDE);

	// 修改文件夹图标
	changeVirtualFolderIcon(path, name, icon);
	return 0;
}

int delete_virtual_folder(const std::wstring& id)
{
	if (id.empty())
	{
		return -1;
	}

	//删除文件夹图标
	removeVirtualFolderIcon(id);

	//删除收藏夹快捷方式
	removeFavoriteShotCut(id);

	//删除文件夹快捷图标：注册表项
	wchar_t  szCommand[512] = {0};
	wsprintf(szCommand, L"DELETE HKEY_CLASSES_ROOT\\CLSID\\%s /f",id.c_str());
	ShellExecute(NULL, NULL, L"REG",szCommand, NULL, SW_HIDE);

	memset(szCommand, 0, 512*sizeof(wchar_t));
	wsprintf(szCommand, L"DELETE HKEY_CURRENT_USER\\Software\\Classes\\CLSID\\%s /f", 
		id.c_str());
	ShellExecute(NULL, NULL, L"REG",szCommand, NULL, SW_HIDE);

	memset(szCommand, 0, 512*sizeof(wchar_t));
	wsprintf(szCommand, L"DELETE HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\MyComputer\\NameSpace\\%s /f", 
		id.c_str());
	ShellExecute(NULL,NULL, L"REG",szCommand, NULL, SW_HIDE);
	//MessageBox(NULL, szCommand, L"ShareDrive_reg", MB_OK); 

	return 0;
}

int update_virtual_folder(const std::wstring& id, const std::wstring& name, const std::wstring& path, const std::wstring& icon)
{
	if (id.empty() || name.empty() || path.empty() || icon.empty())
	{
		return -1;
	}
	return 0;
}

int open_virtual_folder(const std::wstring& path)
{
	if (path.empty())
	{
		return 0;
	}
	ShellExecute(NULL, 
		L"open", 
		path.c_str(), 
		NULL, 
		NULL, 
		SW_SHOWNORMAL);
	return 0;
}

//更改同步目录文件夹图标
int changeVirtualFolderIcon(const std::wstring& path, const std::wstring& name, const std::wstring& icon)
{
	std::wstring strDesktopIniPath = path;
	strDesktopIniPath += L"\\Desktop.ini";

	std::wfstream fs(strDesktopIniPath, std::ios_base::out);
	fs << L"[.ShellClassInfo]\r\nConfirmFileOp=0\r\nIconIndex=0\r\nInfoTip=" << name.c_str()
		<< L"\r\nIconFile=" << icon.c_str() 
		<< L"\r\nIconResource=" << icon.c_str()
		<<L","<<L"0"
		<< L"\r\n";
	fs.close();

	// 设置隐藏
	if (!SetFileAttributes(strDesktopIniPath.c_str(), FILE_ATTRIBUTE_HIDDEN))
	{
		return GetLastError();
	}

	//设置只读
	if (!SetFileAttributes(path.c_str(), FILE_ATTRIBUTE_READONLY))
	{
		return GetLastError();
	}
	return 0;
}


static int removeFavoriteShotCut(const std::wstring& id)
{
	std::wstring strKeyPath = L"CLSID\\" + id ;
	LONG len = MAX_PATH * 2;
	wchar_t* pBuffer = new wchar_t[len];
	if (NULL == pBuffer)
	{
		return -1;
	}
	memset(pBuffer, 0, len * 2);
	LONG ret = RegQueryValue(HKEY_CLASSES_ROOT, strKeyPath.c_str(), pBuffer, &len);
	if (ERROR_SUCCESS != ret)
	{
		delete pBuffer;
		return ret;
	}
	std::wstring wstrShowName = (std::wstring)pBuffer;
	delete pBuffer;

	 //获取当前登陆用户名，拼装收藏夹快捷方式地址
    wchar_t userName[MAX_PATH];
    std::wstring sUsername;
    DWORD size=MAX_PATH;
    ::GetUserName(userName,&size);
    std::wstring linkDesPath = L"C:\\Users\\"+std::wstring(userName) + L"\\Links\\"+wstrShowName+L".lnk";  //举例C:\\Users\\l00100468\\Links\\Huawei CloudDrive.lnk
	//删除快捷方式
	if(!DeleteFile(linkDesPath.c_str()))
	{
		return GetLastError();
	}
	return 0;
}

static int removeVirtualFolderIcon(const std::wstring& id)
{
	std::wstring strKeyPath = L"CLSID\\" + id + L"\\Instance\\InitPropertyBag";
	
	HKEY hkey;
	LONG iErrCode ; 
	DWORD dwPathSize = 512; 
	BYTE bPath[512]={0};
	iErrCode = RegOpenKeyEx(HKEY_CLASSES_ROOT,strKeyPath.c_str(),NULL,KEY_QUERY_VALUE,&hkey);
	if(ERROR_SUCCESS != iErrCode)
	{
		return iErrCode;
	}
	
	iErrCode =RegQueryValueEx(hkey,L"Target",NULL,NULL,(LPBYTE)bPath,&dwPathSize);
	if(ERROR_SUCCESS != iErrCode)
	{
		return iErrCode;
	}
	
	std::wstring wstrPath ;
	 wstrPath.append((wchar_t *) bPath);
	RegCloseKey(hkey);

	std::wstring strDesktopIniPath = wstrPath;
	strDesktopIniPath += L"\\Desktop.ini";

	std::wstring strCmd = L"/C attrib -s \"" + wstrPath + L"\"";
	ShellExecute(NULL, L"open", L"cmd.exe", strCmd.c_str(), NULL, SW_HIDE);
	if (!DeleteFile(strDesktopIniPath.c_str()))
	{
		return GetLastError();
	}
	return 0;
}
