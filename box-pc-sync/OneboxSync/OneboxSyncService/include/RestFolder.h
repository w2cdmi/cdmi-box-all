#ifndef _REST_FOLDER_H_
#define _REST_FOLDER_H_

#include "IFolder.h"
#include "FileItem.h"

class RestFolder : public IFolder
{
public:
	RestFolder(const Path& path, UserContext* userContext);

	virtual ~RestFolder();

	virtual int32_t create();

	virtual int32_t remove();

	virtual int32_t rename(const std::wstring& newName);

	virtual int32_t copy(const Path& newParent);

	virtual int32_t move(const Path& newParent);

	virtual bool isExist();

	virtual int32_t listFolder(LIST_FOLDER_RESULT& result);

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

public:
	static void convFileItemToFileDirInfo(FILE_DIR_INFO& fileDirInfo, const FileItem& fileItem);

private:
	void setPropertyByFileItem(const FileItem& fileItem);

private:
	UserContext* userContext_;
};

#endif
