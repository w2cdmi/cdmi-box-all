#include "OpertingSystem.h"

CopertingSystem::CopertingSystem()
{
}

CopertingSystem::~CopertingSystem()
{
}


void GetSystemName(std::wstring& osname)
{
	SYSTEM_INFO info; //用SYSTEM_INFO结构判断64位AMD处理器 
	GetSystemInfo(&info); //调用GetSystemInfo函数填充结构 
	OSVERSIONINFOEX os; 
	os.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX); 

	osname = _T("unknown OperatingSystem.");

	if(GetVersionEx((OSVERSIONINFO *)&os))
	{ 
		//下面根据版本信息判断操作系统名称 
		switch(os.dwMajorVersion)//判断主版本号
		{
		case 4:
			switch(os.dwMinorVersion)//判断次版本号 
			{ 
			case 0:
				if(os.dwPlatformId==VER_PLATFORM_WIN32_NT)
					osname =_T("Microsoft Windows NT 4.0"); //1996年7月发布 
				else if(os.dwPlatformId==VER_PLATFORM_WIN32_WINDOWS)
					osname =_T("Microsoft Windows 95");
				break;
			case 10:
				osname =_T("Microsoft Windows 98");
				break;
			case 90:
				osname =_T("Microsoft Windows Me");
				break;
			}
			break;

		case 5:
			switch(os.dwMinorVersion) //再比较dwMinorVersion的值
			{ 
			case 0:
				osname =_T("Microsoft Windows 2000");//1999年12月发布
				break;

			case 1:
				osname =_T("Microsoft Windows XP");//2001年8月发布
				break;

			case 2:
				if(os.wProductType==VER_NT_WORKSTATION 
					&& info.wProcessorArchitecture==PROCESSOR_ARCHITECTURE_AMD64)
				{
					osname =_T("Microsoft Windows XP Professional x64 Edition");
				}
				else if(GetSystemMetrics(SM_SERVERR2)==0)
					osname =_T("Microsoft Windows Server 2003");//2003年3月发布 
				else if(GetSystemMetrics(SM_SERVERR2)!=0)
					osname =_T("Microsoft Windows Server 2003 R2");
				break;
			}
			break;

		case 6:
			switch(os.dwMinorVersion)
			{
			case 0:
				if(os.wProductType == VER_NT_WORKSTATION)
					osname =_T("Microsoft Windows Vista");
				else
					osname =_T("Microsoft Windows Server 2008");//服务器版本 
				break;
			case 1:
				if(os.wProductType == VER_NT_WORKSTATION)
					osname =_T("Microsoft Windows 7");
				else
					osname =_T("Microsoft Windows Server 2008 R2");
				break;
			}
			break;
		}
	}//if(GetVersionEx((OSVERSIONINFO *)&os))

} 

//
void GetVersionMark(std::wstring& vmark)
{
	OSVERSIONINFOEX os; 
	os.dwOSVersionInfoSize=sizeof(OSVERSIONINFOEX); 
	vmark = _T("");

	if(GetVersionEx((OSVERSIONINFO*)&os))
	{ 
		switch(os.dwMajorVersion)
		{ //
		case 5: 
			switch(os.dwMinorVersion)
			{ 
			case 0: //Windows 2000 
				if(os.wSuiteMask==VER_SUITE_ENTERPRISE) 
					vmark=_T("Advanced Server"); 
				break; 
			case 1: //Windows XP 
				if(os.wSuiteMask==VER_SUITE_EMBEDDEDNT) 
					vmark=_T("Embedded"); 
				else if(os.wSuiteMask==VER_SUITE_PERSONAL) 
					vmark=_T("Home Edition"); 
				else 
					vmark=_T("Professional"); 
				break; 
			case 2: 
				if(GetSystemMetrics(SM_SERVERR2)==0 
					&&os.wSuiteMask==VER_SUITE_BLADE) //Windows Server 2003 
					vmark=_T("Web Edition");
				else if(GetSystemMetrics(SM_SERVERR2)==0 
					&& os.wSuiteMask==VER_SUITE_COMPUTE_SERVER)
					vmark=_T("Compute Cluster Edition");
				else if(GetSystemMetrics(SM_SERVERR2)==0
					&& os.wSuiteMask==VER_SUITE_STORAGE_SERVER)
					vmark=_T("Storage Server");
				else if(GetSystemMetrics(SM_SERVERR2)==0 
					&& os.wSuiteMask==VER_SUITE_DATACENTER)
					vmark=_T("Datacenter Edition");
				else if(GetSystemMetrics(SM_SERVERR2)==0 
					&& os.wSuiteMask==VER_SUITE_ENTERPRISE)
					vmark=_T("Enterprise Edition");
				else if(GetSystemMetrics(SM_SERVERR2)!=0
					&& os.wSuiteMask==VER_SUITE_STORAGE_SERVER)
					vmark=_T("Storage Server");
				break;
			}
			break;

		case 6:
			switch(os.dwMinorVersion)
			{
			case 0:
				if(os.wProductType!=VER_NT_WORKSTATION 
					&& os.wSuiteMask==VER_SUITE_DATACENTER)
					vmark=_T("Datacenter Server");
				else if(os.wProductType!=VER_NT_WORKSTATION 
					&& os.wSuiteMask==VER_SUITE_ENTERPRISE)
					vmark=_T("Enterprise");
				else if(os.wProductType==VER_NT_WORKSTATION 
					&& os.wSuiteMask==VER_SUITE_PERSONAL) //Windows Vista
					vmark =_T("Home");
				break;
			}
			break;
		}
	}
} 

int32_t CopertingSystem::GetOperatingSystemInfo( STR_OPERATINGSYSTEM_INFO& strSystemInfo )
{
	SYSTEM_INFO info; 
	GetSystemInfo( &info );

	BOOL bIsWow64 = false;
	IsWow64Process(GetCurrentProcess(), &bIsWow64);
	if (bIsWow64)
	{
		strSystemInfo.system_bits = OPERATING_SYSTEM_64;
	}
	else
	{
		strSystemInfo.system_bits = OPERATING_SYSTEM_32;
	}

	GetSystemName(strSystemInfo.system_name);
	GetVersionMark(strSystemInfo.system_version);
	return 0;
}

