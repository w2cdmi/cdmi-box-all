#ifndef _ONEBOX_IFOLDER_H_
#define _ONEBOX_IFOLDER_H_

#include "IFile.h"
#include <vector>
#include <boost/function.hpp>

typedef std::vector<FILE_DIR_INFO> LIST_FOLDER_RESULT;

class IFolder
{
public:
	IFolder(const Path& path)
		:error_(RT_ERROR)
	{
		property_.id = path.id();
		property_.parent = path.parent();
		property_.name = path.name();
		property_.type = FILE_TYPE_DIR;
		path_ = path.path();
		ownerId_ = path.ownerId();
	}

	virtual ~IFolder() {}

	FUNC_DEFAULT_SET_GET(int32_t, error);

	virtual FILE_DIR_INFO property(const PROPERTY_MASK& mask = PROPERTY_MASK_ALL)
	{
		FILE_DIR_INFO property;
		if (PROPERTY_ID&mask)
			property.id = property_.id;
		if (PROPERTY_PARENT&mask)
			property.parent = property_.parent;
		if (PROPERTY_NAME&mask)
			property.name = property_.name;
		if (PROPERTY_TYPE&mask)
			property.type = property_.type;
		if (PROPERTY_SIZE&mask)
			property.size = property_.size;
		if (PROPERTY_CREATE&mask)
			property.ctime = property_.ctime;
		if (PROPERTY_MODIFY&mask)
			property.mtime = property_.mtime;
		if (PROPERTY_VERSION&mask)
			property.version = property_.version;
		if (PROPERTY_SIGNATURE&mask)
			property.signature = property_.signature;
		if (PROPERTY_FLAGS&mask)
			property.flags = property_.flags;
		return property;
	}

	virtual void property(FILE_DIR_INFO& property, const PROPERTY_MASK& mask = PROPERTY_MASK_ALL)
	{
		if (PROPERTY_ID&mask)
			property_.id = property.id;
		if (PROPERTY_PARENT&mask)
			property_.parent = property.parent;
		if (PROPERTY_NAME&mask)
			property_.name = property.name;
		if (PROPERTY_TYPE&mask)
			property_.type = property.type;
		if (PROPERTY_SIZE&mask)
			property_.size = property.size;
		if (PROPERTY_CREATE&mask)
			property_.ctime = property.ctime;
		if (PROPERTY_MODIFY&mask)
			property_.mtime = property.mtime;
		if (PROPERTY_VERSION&mask)
			property_.version = property.version;
		if (PROPERTY_SIGNATURE&mask)
			property_.signature = property.signature;
		if (PROPERTY_FLAGS&mask)
			property_.flags = property.flags;
	}

	virtual int32_t create() {return RT_NOT_IMPLEMENT;}

	virtual int32_t remove() {return RT_NOT_IMPLEMENT;}

	virtual int32_t rename(const std::wstring& newName) {return RT_NOT_IMPLEMENT;}

	virtual int32_t copy(const Path& newParent) {return RT_NOT_IMPLEMENT;}

	virtual int32_t move(const Path& newParent) {return RT_NOT_IMPLEMENT;}

	virtual bool isExist() {return false;}

	virtual int32_t listFolder(LIST_FOLDER_RESULT& result) {return RT_NOT_IMPLEMENT;}

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask) {return RT_NOT_IMPLEMENT;}

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask) {return RT_NOT_IMPLEMENT;}

protected:
	FILE_DIR_INFO property_;
	std::wstring path_;
	int64_t ownerId_;

private:
	int32_t error_;
};

typedef std::shared_ptr<IFolder> IFolderPtr;

#endif