#ifndef _LOCAL_MAPPING_FILE_H__
#define _LOCAL_MAPPING_FILE_H__

#include "LocalFile.h"
#include <windows.h>

class LocalMappingFile : public LocalFile
{
public:
	LocalMappingFile(const Path& path, UserContext* userContext);

	virtual ~LocalMappingFile();

	virtual int32_t open(const OpenMode mode = OpenRead);

	virtual int32_t close();

	virtual int32_t read(const int64_t& offset, const uint32_t size, unsigned char* buffer);

	virtual int32_t write(const unsigned char* buffer, const uint32_t size, const int64_t& offset);

private:
	boost::mutex mutex_;
	SmartHandle fileHandle_;
	SmartHandle mappingHandle_;
	SYSTEM_INFO si_;
};

#endif
