#include "stdafx.h"
#include "Global.h"
#include "ErrorCode.h"
#include "Utility.h"
#include "NotifyMsg.h"
#include "UploadFrame.h"
#include <stdio.h>
#include "ThriftClient.h"
#include "CommonDefine.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include <boost/algorithm/string.hpp>
using namespace boost;

#ifdef _WIN64
#ifdef _DEBUG
#pragma comment (lib, "HuaweiSecureC_d_x64.lib")
#else
#pragma comment (lib, "HuaweiSecureC_x64.lib")
#endif
#else
#ifdef _DEBUG
#pragma comment (lib, "HuaweiSecureC_d.lib")
#else
#pragma comment (lib, "HuaweiSecureC.lib")
#endif
#endif

#ifndef ONEBOX_SYNC_REG_PATH
#ifdef _WIN64
//#define ONEBOX_SYNC_REG_PATH (L"SOFTWARE\\Wow6432Node\\Huawei\\Onebox\\Setting")
#define ONEBOX_SYNC_REG_PATH (L"SOFTWARE\\Wow6432Node\\Huawei\\OneboxApp\\Onebox\\Setting")
#else
//#define ONEBOX_SYNC_REG_PATH (L"SOFTWARE\\Huawei\\Onebox\\Setting")
#define ONEBOX_SYNC_REG_PATH (L"SOFTWARE\\Huawei\\OneboxApp\\Onebox\\Setting")
#endif
#endif

namespace Onebox
{
	bool IsOneboxRunning()
	{
		HANDLE hEvent = ::OpenEvent(EVENT_ALL_ACCESS, FALSE, ONEBOX_INSTANCE_EVENT_ID);
		if (NULL == hEvent)
		{
			return false;
		}
		::CloseHandle(hEvent);

		int32_t status = SyncServiceClientWrapper::getInstance()->getServerStatus();
		if (Service_Status::Service_Status_Online == status)
		{
			return true;
		}

		return false;
	}

	std::wstring GetMenuLanguageString(std::wstring key)
	{
		return CInIHelper(IniLanguageHelper().GetLanguageFilePath()).GetString(LANGUAGE_SHEELLEXTENT_SECTION,key,L"");
	}

	std::wstring getShortPath(std::wstring fullPath,int32_t length)
	{
		std::wstring _return = L"";
		int32_t fullPathWidth = 0;
		for (size_t i=0; i< fullPath.length(); i++)
		{
			if((31<fullPath[i] && fullPath[i]<65) || 90<fullPath[i] && fullPath[i]<127)
			{
				fullPathWidth += 1;
			}
			else
			{
				fullPathWidth += 2;
			}

			if (fullPathWidth > length)
			{ 
				_return = fullPath.substr(0,length);
				break;
			}
		}

		return _return == L""? fullPath:_return+L"......";
	}

	bool isSyncDir(const std::wstring& path)
	{
		std::wstring oneboxSyncInstallPath = L"";
		if(ERROR_SUCCESS != SD::Utility::Registry::get<std::wstring>(HKEY_LOCAL_MACHINE,ONEBOX_SYNC_REG_PATH,L"AppPath",oneboxSyncInstallPath))
		{
			return false;
		}
		CInIHelper InIHelper((oneboxSyncInstallPath+L"\\Config.ini").c_str());
		std::wstring syncDir=  InIHelper.GetString(L"CONFIGURE",L"MonitorRootPath",L"");
		if (syncDir.empty())
		{
			return false;
		}
		syncDir = SD::Utility::String::to_upper(syncDir);
		std::wstring strPath = SD::Utility::String::to_upper(path);
		if (std::wstring::npos == strPath.find(syncDir))
		{
			return false;
		}
		return true;
	}
}