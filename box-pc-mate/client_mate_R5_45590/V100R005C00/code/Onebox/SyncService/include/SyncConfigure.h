#ifndef _SYNC_CONFIGURE_H_
#define _SYNC_CONFIGURE_H_

#include "UserContext.h"

class SyncConfigure
{
public:
	virtual ~SyncConfigure(){}

	static SyncConfigure* getInstance(UserContext* userContext);

	FUNC_DEFAULT_SET_GET(int32_t, remoteDetectorPeriod);
	FUNC_DEFAULT_SET_GET(int32_t, localDetectorPeriod);
	FUNC_DEFAULT_SET_GET(int32_t, uploadFilterPeriod);
	FUNC_DEFAULT_SET_GET(std::wstring, uploadFilterStr);
	FUNC_DEFAULT_SET_GET(std::wstring, virtualFolderName);
	FUNC_DEFAULT_SET_GET(std::wstring, monitorRootPath);
	FUNC_DEFAULT_SET_GET(std::wstring, cachePath);
	FUNC_DEFAULT_SET_GET(std::wstring, cacheTrashPath);
	FUNC_DEFAULT_SET_GET(std::wstring, cacheDataPath);
	FUNC_DEFAULT_SET_GET(int32_t, syncMode);

private:
	SyncConfigure(UserContext* userContext);

private:
	static SyncConfigure* instance_;

	UserContext* userContext_;
	int32_t remoteDetectorPeriod_;
	int32_t localDetectorPeriod_;
	int32_t uploadFilterPeriod_;
	std::wstring uploadFilterStr_;
	std::wstring virtualFolderName_;
	std::wstring monitorRootPath_;
	std::wstring cachePath_;
	std::wstring cacheTrashPath_;
	std::wstring cacheDataPath_;
	int32_t syncMode_;
};

#endif