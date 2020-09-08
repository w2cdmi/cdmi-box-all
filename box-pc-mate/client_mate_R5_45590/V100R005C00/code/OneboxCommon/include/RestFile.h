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

	virtual int32_t copy(const Path& newParent, bool autoRename);

	virtual int32_t move(const Path& newParent, bool autoRename);

	virtual bool isExist();

	virtual int32_t getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	virtual int32_t setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask);
	
	virtual int32_t read(const int64_t& offset, const uint32_t size, unsigned char* buffer);

	virtual int32_t listFileVersion(LIST_FILEVERSION_RESULT& fileVersionNodes);

	int32_t preUpload(const UploadType type);

	int32_t totalUpload(UploadCallback callback, void* callbackData, const int64_t len, ProgressCallback progressCallback, void* progressCallbackData);

	int32_t uploadPart(UploadCallback callback, void* callbackData, uint32_t part, const int64_t len, ProgressCallback progressCallback, void* progressCallbackData);

    int32_t completeUploadPart(const PartList& partList);
	
	int32_t download(DownloadCallback callback, void* callbackData, const int64_t offset, const int64_t size, ProgressCallback progressCallback, void* progressCallbackData);
	
	int32_t getUploadParts(PartInfoList& parts);

	int32_t cancelUploadPart();

	int32_t refreshUploadURL();

	int32_t getContentProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask);

	FUNC_DEFAULT_SET_GET(std::wstring, uploadURL);
	FUNC_DEFAULT_SET_GET(int64_t, contentCreate);
	FUNC_DEFAULT_SET_GET(int64_t, contentModify);

private:
	void setPropertyByFileItem(const FileItem& fileItem);

private:
	UserContext* userContext_;
	std::wstring uploadURL_;
	int64_t contentCreate_;
	int64_t contentModify_;
	boost::mutex mutex_;
};

#endif
