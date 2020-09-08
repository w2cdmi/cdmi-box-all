#ifndef _LOCAL_FOLDER_H_
#define _LOCAL_FOLDER_H_

#include "IFolder.h"

class LocalFolder : public IFolder
{
public:
	LocalFolder(const Path& path, UserContext* userContext);
	
	~LocalFolder();

	virtual int32_t create();

	virtual int32_t remove();

	virtual int32_t rename(const std::wstring& newName);

	virtual int32_t copy(const Path& newParent);

	virtual int32_t move(const Path& newParent);

	virtual bool isExist();

	virtual int32_t listFolder(LIST_FOLDER_RESULT& result);

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

private:
	UserContext* userContext_;
};

#endif
