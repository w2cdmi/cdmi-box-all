#include "UserInfoMgrImpl.h"
#include <iphlpapi.h>
#include <Windows.h>
#include "nsca/info/device_info.h"

#pragma comment(lib,"Iphlpapi.lib")

#define BUF_LEN (1024)
#define DLL_NAME (L"NscaMiniLib.dll")

typedef bool (*fnsca_get_device_info)(const char* key,char* buffer,size_t bufferSize,size_t& requiredBufferSize);

int32_t UserInfoMgrImpl::getDeviceInfo()
{
	int32_t ret = RT_OK;
	fnsca_get_device_info deviceInfoFunction = NULL;
	std::wstring dllPath = userContext_->getConfigureMgr()->getConfigure()->appPath() + L"\\" + DLL_NAME;
	HMODULE  lib = LoadLibraryW(dllPath.c_str());

	if ( !lib )
	{
		return RT_WINDLL_LOAD_ERROR;
	}

	deviceInfoFunction = (fnsca_get_device_info)GetProcAddress(lib,"nsca_device_get_info");
	if (NULL == deviceInfoFunction)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "load dll  function failed.");

		FreeLibrary(lib);
		return ret;
	}

	std::shared_ptr<char> buffer(new char[BUF_LEN]);
	if (NULL == buffer.get())
	{
		FreeLibrary(lib);
		return RT_OPERATOR_NEW_ERROR;
	}
	size_t requiredSize = 0;

	(void)memset_s(buffer.get(), BUF_LEN, 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_DEVICE_ID,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_DEVICE_ID failed.");

		FreeLibrary(lib);
		return ret;
	}
	deviceId_ = Utility::String::utf8_to_wstring(buffer.get());

	(void)memset_s(buffer.get(), BUF_LEN, 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_HOST_NAME,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_HOST_NAME failed.");

		FreeLibrary(lib);
		return ret;
	}
	hostName_ = Utility::String::utf8_to_wstring(buffer.get());

	(void)memset_s(buffer.get(), BUF_LEN, 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_OS_VERSION,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_OS_VERSION failed.");

		FreeLibrary(lib);
		return ret;
	}
	osVersion_ = Utility::String::utf8_to_wstring(buffer.get());
	FreeLibrary(lib);

	return RT_OK;
}
