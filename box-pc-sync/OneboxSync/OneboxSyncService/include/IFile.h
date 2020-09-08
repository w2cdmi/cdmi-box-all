#ifndef _ONEBOX_IFILE_H_
#define _ONEBOX_IFILE_H_

#include "CommonDefine.h"
#include "Path.h"
#include "UserContext.h"

#define TRANSMIT_PART_SIZE (5*1024*1024)

enum OpenMode
{
	OpenRead,
	OpenWrite
};

struct FILE_DIR_INFO
{
	int64_t id;
	int64_t parent;
	std::wstring name;
	int32_t type;
	int64_t size;
	int64_t mtime;
	int64_t ctime;
	std::wstring version;	
	FileSignature signature;
	int32_t flags;

	FILE_DIR_INFO()
		:id(INVALID_ID)
		,parent(INVALID_ID)
		,name(L"")
		,type(FILE_TYPE_FILE)
		,size(0L)
		,mtime(0L)
		,ctime(0L)
		,version(L"")
		,flags(0){}
};

enum PROPERTY_MASK
{
	PROPERTY_ID = 0x0001,
	PROPERTY_PARENT = 0x0002,
	PROPERTY_NAME = 0x0004,
	PROPERTY_TYPE = 0x0008,
	PROPERTY_SIZE = 0x0010,
	PROPERTY_CREATE = 0x0020,
	PROPERTY_MODIFY = 0x0040,
	PROPERTY_VERSION = 0x0080,
	PROPERTY_SIGNATURE = 0x00100,
	PROPERTY_FLAGS = 0x00200
};

#define PROPERTY_MASK_ALL (PROPERTY_MASK)(~0)

enum OBJECT_FLAG
{
	OBJECT_FLAG_ENCRYPT = 0x00000001,
	OBJECT_FLAG_SHARELINK = 0x00000002,
	OBJECT_FLAG_SHARED = 0x00000004,
	OBJECT_FLAG_SYNC = 0x00000008
};

#define SET_BIT_VALUE(target, mask, value) \
	target = (((~mask)|(value&mask))&(target|mask))

#define SET_BIT_VALUE_TYPE(target, mask, value, type) \
	target = (type)(((~mask)|(value&mask))&(target|mask))

#define GET_BIT_VALUE(target, mask) \
	(target&mask)

#define SET_BIT_VALUE_BY_BOOL(target, mask, value)	\
	target = value?(target|mask):(target&(~mask))

class IFile
{
public:
	IFile(const Path& path)
		:error_(RT_OK)
	{
		property_.id = path.id();
		property_.parent = path.parent();
		property_.name = path.name();
		property_.type = FILE_TYPE_FILE;
		path_ = path.path();
		ownerId_ = path.ownerId();
	}

	virtual ~IFile() {}

	FUNC_DEFAULT_SET_GET(int32_t, error);
	FUNC_DEFAULT_SET_GET(std::wstring, path);

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

	virtual void property(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask = PROPERTY_MASK_ALL)
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

	virtual int32_t remove() {return RT_NOT_IMPLEMENT;}

	virtual int32_t rename(const std::wstring& newName) {return RT_NOT_IMPLEMENT;}

	virtual int32_t copy(const Path& newParent) {return RT_NOT_IMPLEMENT;}

	virtual int32_t move(const Path& newParent) {return RT_NOT_IMPLEMENT;}

	virtual bool isExist() {return false;}

	virtual int32_t open(const OpenMode mode = OpenRead) {return RT_NOT_IMPLEMENT;}

	virtual int32_t close() {return RT_NOT_IMPLEMENT;}

	virtual int32_t read(unsigned char* buffer, const uint32_t size) {return RT_NOT_IMPLEMENT;}

	virtual int32_t read(const int64_t& offset, const uint32_t size, unsigned char* buffer) {return RT_NOT_IMPLEMENT;}

	virtual int32_t write(const unsigned char* buffer, const uint32_t size) {return RT_NOT_IMPLEMENT;}

	virtual int32_t write(const unsigned char* buffer, const uint32_t size, const int64_t& offset) {return RT_NOT_IMPLEMENT;}

	virtual int32_t seek(const int64_t& offset, const int32_t oringin) {return RT_NOT_IMPLEMENT;}

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask) {return RT_NOT_IMPLEMENT;}

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask) {return RT_NOT_IMPLEMENT;}

protected:
	FILE_DIR_INFO property_;
	std::wstring path_;
	int64_t ownerId_;

private:
	int32_t error_;
};

typedef std::shared_ptr<IFile> IFilePtr;

#endif