#ifndef _REST_FOLDER_H_
#define _REST_FOLDER_H_

#include "IFolder.h"
#include "FileItem.h"
#include "PageParam.h"

class RestFolder : public IFolder
{
public:
	RestFolder(const Path& path, UserContext* userContext);

	virtual ~RestFolder();

	virtual int32_t create();

	virtual int32_t create(FOLDER_EXTRA_TYPE extraType = FOLDER_EXTRA_TYPE_NONE, const bool autoMerge = false);

	virtual int32_t remove();

	virtual int32_t rename(const std::wstring& newName);

	virtual int32_t copy(const Path& newParent, bool autoRename);

	virtual int32_t move(const Path& newParent, bool autoRename);

	virtual bool isExist();

	virtual int32_t listFolder(LIST_FOLDER_RESULT& result);

	virtual int32_t listSubDir(LIST_FOLDER_RESULT& result);

	virtual int32_t listPage(LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count);

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t getRemoteInfoByName(const Path& path, FILE_DIR_INFO& fileDirInfo);

	virtual int32_t getNewName(const Path& path, std::wstring& newName);

	virtual int32_t getFilePermissions(const Path& path, const int64_t& user_id, File_Permissions& filePermissions);

public:
	static void convFileItemToFileDirInfo(FILE_DIR_INFO& fileDirInfo, const FileItem& fileItem);

private:
	void setPropertyByFileItem(const FileItem& fileItem);

private:
	UserContext* userContext_;
};

#endif
