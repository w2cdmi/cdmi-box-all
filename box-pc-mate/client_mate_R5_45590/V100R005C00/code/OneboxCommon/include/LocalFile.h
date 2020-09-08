#ifndef _LOCAL_FILE_H__
#define _LOCAL_FILE_H__

#include "IFile.h"
#include <boost/thread/mutex.hpp>
#include "SmartHandle.h"

class LocalFile : public IFile
{
public:
	LocalFile(const Path& path, UserContext* userContext);

	virtual ~LocalFile();

	virtual int32_t remove();

	virtual int32_t rename(const std::wstring& newName);

	virtual int32_t copy(const Path& newParent, bool autoRename);

	virtual int32_t move(const Path& newParent, bool autoRename);

	virtual bool isExist();

	virtual int32_t open(const OpenMode mode = OpenRead);

	virtual int32_t close();

	virtual int32_t read(const int64_t& offset, const uint32_t size, unsigned char* buffer);

	virtual int32_t write(const unsigned char* buffer, const uint32_t size, const int64_t& offset);

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

public:
	static int32_t create(const std::wstring& path, const int64_t& size);

	static FILE_DIR_INFO getPropertyByPath(const std::wstring& path);

	static bool isOpen(const std::wstring& path);

	static int64_t li64toll(const LARGE_INTEGER& li64);

	static int64_t filetimetoll(const FILETIME& filetime);
private:
	UserContext* userContext_;
	SmartHandle file_;
	int fd_;
	boost::mutex mutex_;
};

#endif
