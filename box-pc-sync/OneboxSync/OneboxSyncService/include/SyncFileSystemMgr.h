#ifndef _ONEBOX_SYNCFILESYSTEM_MGR_H_
#define _ONEBOX_SYNCFILESYSTEM_MGR_H_

#include "UserContext.h"
#include "IFolder.h"

enum ADAPTER_FILE_TYPE
{
	ADAPTER_FILE_TYPE_LOCAL,
	ADAPTER_FILE_TYPE_REST,
	ADAPTER_FOLDER_TYPE_LOCAL,
	ADAPTER_FOLDER_TYPE_REST
};

class SyncFileSystemMgr
{
public:
	virtual ~SyncFileSystemMgr(){}

	static SyncFileSystemMgr* create(UserContext* userContext);

	virtual int32_t create(const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t remove(const Path& path, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t move(const Path& path, const Path& parent, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t rename(const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t getProperty(const Path& path, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t isExist(const Path& path, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t listFolder(const Path& path, LIST_FOLDER_RESULT& result, ADAPTER_FILE_TYPE type) = 0;

};

#endif