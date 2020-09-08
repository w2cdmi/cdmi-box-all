#include "UserInfoMgrImpl.h"
#include <iphlpapi.h>
#include <Windows.h>
#include "nsca/info/device_info.h"
#include "FilterMgr.h"

#pragma comment(lib,"Iphlpapi.lib")

#define BUF_LEN (1024)
#define DLL_NAME (L"NscaMiniLib.dll")

typedef bool (*fnsca_get_device_info)(const char* key,char* buffer,size_t bufferSize,size_t& requiredBufferSize);

int32_t UserInfoMgrImpl::getDeviceInfo()
{
	int32_t ret = RT_OK;
	fnsca_get_device_info deviceInfoFunction = NULL;
	HMODULE  lib = LoadLibrary(DLL_NAME);
	deviceInfoFunction = (fnsca_get_device_info)GetProcAddress(lib,"nsca_device_get_info");
	if (NULL == deviceInfoFunction)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "load dll  function failed.");
		return ret;
	}

	std::shared_ptr<char> buffer(new char[BUF_LEN]);
	if (NULL == buffer.get())
	{
		return RT_OPERATOR_NEW_ERROR;
	}
	size_t requiredSize = 0;

	memset(buffer.get(), 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_DEVICE_ID,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_DEVICE_ID failed.");
		return ret;
	}
	deviceId_ = Utility::String::utf8_to_wstring(buffer.get());

	memset(buffer.get(), 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_HOST_NAME,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_HOST_NAME failed.");
		return ret;
	}
	hostName_ = Utility::String::utf8_to_wstring(buffer.get());

	memset(buffer.get(), 0, BUF_LEN);
	if (!deviceInfoFunction(NSCA_DEVICE_PROP_OS_VERSION,buffer.get(),BUF_LEN,requiredSize))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_OS_VERSION failed.");
		return ret;
	}
	osVersion_ = Utility::String::utf8_to_wstring(buffer.get());
/*****************2016/9/26 lidonghai
	if (FilterMgr::isHuaweiDevice())
	{
		memset(buffer.get(), 0, BUF_LEN);
		if (!deviceInfoFunction(NSCA_DEVICE_PROP_IS_SPES_INSTALLED,buffer.get(),BUF_LEN,requiredSize))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "get NSCA_DEVICE_PROP_IS_SPES_INSTALLED failed.");
			return ret;
		}
		if (std::string(buffer.get()) != "true")
		{
			return RT_INVALID_DEVICE;
		}
	}
	else
	{
		HSLOG_EVENT(MODULE_NAME, RT_OK, "This device is not a Huawei device");
	}
*******/
	return RT_OK;
}
