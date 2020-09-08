#ifndef _ONEBOX_SYNCFILESYSTEM_MGR_H_
#define _ONEBOX_SYNCFILESYSTEM_MGR_H_

#include "UserContext.h"
#include "IFolder.h"
#include "PageParam.h"
#include "FileItem.h"

enum ADAPTER_FILE_TYPE
{
	ADAPTER_FILE_TYPE_LOCAL,
	ADAPTER_FILE_TYPE_REST,
	ADAPTER_FOLDER_TYPE_LOCAL,
	ADAPTER_FOLDER_TYPE_REST
};

class ONEBOX_DLL_EXPORT SyncFileSystemMgr
{
public:
	virtual ~SyncFileSystemMgr(){}

	static SyncFileSystemMgr* create(UserContext* userContext);

	virtual int32_t create(const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t create(const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type, FOLDER_EXTRA_TYPE extraType, const bool autoMerge) = 0;

	virtual int32_t remove(const Path& path, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t move(const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t copy(const Path& path, const Path& parent, bool autoRename, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t rename(const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t getProperty(const Path& path, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t isExist(const Path& path, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t listFolder(const Path& path, LIST_FOLDER_RESULT& result, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t listSubDir(const Path& path, LIST_FOLDER_RESULT& result) = 0;

	virtual int32_t listPage(const Path& path, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count) = 0;

	virtual int32_t listFileVersion(const Path& path, LIST_FILEVERSION_RESULT &result, ADAPTER_FILE_TYPE type) = 0;

	virtual int32_t getRemoteInfoByName(const Path& path, FILE_DIR_INFO& fileDirInfo) = 0;

	virtual int32_t getNewName(const Path& path, std::wstring& newName) = 0;

	virtual int32_t getFilePermissions(const Path& path, const int64_t& user_id, File_Permissions& filePermissions) = 0;
};

#endif