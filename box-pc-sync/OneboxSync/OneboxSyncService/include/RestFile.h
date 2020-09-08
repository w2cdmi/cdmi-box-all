#ifndef _REST_FILE_H_
#define _REST_FILE_H_

#include "IFile.h"
#include "FileItem.h"
#include "UploadInfo.h"
#include <boost/thread/mutex.hpp>

class CRestFolder;

class RestFile : public IFile
{
public:
	RestFile(const Path& path, UserContext* userContext);

	virtual ~RestFile();

	virtual int32_t remove();

	virtual int32_t rename(const std::wstring& newName);

	virtual int32_t copy(const Path& newParent);

	virtual int32_t move(const Path& newParent);

	virtual bool isExist();

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);
	
	virtual int32_t read(const int64_t& offset, const uint32_t size, unsigned char* buffer);

	int32_t preUpload();

	int32_t totalUpload(unsigned char* buffer, const int64_t& len);

	int32_t uploadPart(unsigned char* buffer, const int64_t& len, uint32_t part);

    int32_t completeUploadPart();
	
	int32_t getUploadParts(PartList& parts);

	int32_t cancelUploadPart();

	FUNC_DEFAULT_SET_GET(std::wstring, uploadURL);
	FUNC_DEFAULT_SET_GET(int64_t, contentCreate);
	FUNC_DEFAULT_SET_GET(int64_t, contentModify);

	int32_t getContentProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

private:
	void setPropertyByFileItem(const FileItem& fileItem);

	int32_t createUploadParts(PartList& parts);

	int32_t refreshUploadURL();

private:
	UserContext* userContext_;
	std::wstring uploadURL_;
	int64_t contentCreate_;
	int64_t contentModify_;
	boost::mutex mutex_;
};

#endif
