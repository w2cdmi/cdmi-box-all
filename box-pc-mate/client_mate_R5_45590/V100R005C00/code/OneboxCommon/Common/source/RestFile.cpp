#include "RestFile.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include <algorithm>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestFile")
#endif

RestFile::RestFile(const Path& path, UserContext* userContext)
	:IFile(path)
	,userContext_(userContext)
	,uploadURL_(L"")
	,contentCreate_(INVALID_TIME)
	,contentModify_(INVALID_TIME)
{
}

RestFile::~RestFile()
{
}

int32_t RestFile::remove()
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	MAKE_CLIENT(client);
	ret = client().removeFile(ownerId_, property_.id, FILE_TYPE_FILE);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"remove remote file of %I64d failed.", property_.id);
		return ret;
	}

	return RT_OK;
}

int32_t RestFile::rename(const std::wstring& newName)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| newName.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().renameFile(ownerId_, property_.id, 
		Utility::String::wstring_to_utf8(newName),
		FILE_TYPE_FILE, fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if(RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"rename remote file: %I64d to %s failed.", 
			property_.id, Utility::String::wstring_to_string(newName).c_str());
		return ret;
	}

	property_.name = newName;

	return RT_OK;
}

int32_t RestFile::copy(const Path& newParent, bool autoRename)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| INVALID_ID == newParent.id() || INVALID_ID == newParent.ownerId())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().copyFile(ownerId_,
		property_.id, 
		newParent.ownerId(),
		newParent.id(),
		autoRename,
		FILE_TYPE_FILE, 
		fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if(RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"copy remote file %I64d to %I64d failed.", 
			property_.id, newParent.id());
		return ret;
	}

	property_.parent = newParent.id();

	return RT_OK;
}

int32_t RestFile::move(const Path& newParent, bool autoRename)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| INVALID_ID == newParent.id())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().moveFile(ownerId_, property_.id, newParent.id(),
		//Utility::String::wstring_to_utf8(property_.name), 
		autoRename,
		FILE_TYPE_FILE, 
		fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if(RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"move remote file %I64d to %I64d failed.", 
			property_.id, newParent.id());
		return ret;
	}

	property_.parent = newParent.id();

	return RT_OK;
}

bool RestFile::isExist()
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return false;
	}

	int32_t ret = RT_OK;
	MAKE_CLIENT(client);
	ret = client().checkFileExist(ownerId_, property_.id, FILE_TYPE_FILE);
	ret = (HTTP_NOT_FOUND==ret?RT_FILE_NOEXIST_ERROR:ret);
	error(ret);
	if (RT_OK != ret && RT_FILE_NOEXIST_ERROR != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"check exist remote file of %I64d failed.", property_.id);
		return false;
	}

	return (RT_OK == ret);
}

int32_t RestFile::getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().getFileInfo(ownerId_, property_.id, FILE_TYPE_FILE, fileItem);
	ret = (HTTP_NOT_FOUND==ret?RT_FILE_NOEXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get information of remote file of %I64d failed.", property_.id);
		return ret;
	}

	setPropertyByFileItem(fileItem);
	property = this->property(mask);

	return RT_OK;
}

int32_t RestFile::setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestFile::read(const int64_t& offset, const uint32_t size, unsigned char* buffer)
{
	if (NULL == buffer || 0 == size)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_DOWNLOAD_CLIENT(client);
	int64_t readLen = size;
	int32_t ret = RT_OK;
	std::string download_url = "";
	ret = client().getDownloadUrl(ownerId_, property_.id, download_url);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get download url of remote file of %I64d failed.", property_.id);
		return ret;
	}
	ret = client().downloadFile(download_url, buffer, readLen, offset);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"read remote file of %I64d failed.", property_.id);
		return ret;
	}

	return (int32_t)readLen;
}

int32_t RestFile::preUpload(const UploadType type)
{
	if(INVALID_ID == property_.parent || INVALID_ID == ownerId_ 
		|| property_.name.empty() || !property_.fingerprint.valid())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	FileItem fileItem;
	fileItem.ownerId(ownerId_);
	fileItem.id(property_.id);
	fileItem.parent(property_.parent);
	fileItem.name(Utility::String::wstring_to_utf8(property_.name));
	fileItem.type(FILE_TYPE_FILE);
	fileItem.size(property_.size);
	fileItem.fingerprint(property_.fingerprint);
	fileItem.contentCreatedAt(contentCreate_);
	fileItem.contentModifiedAt(contentModify_);

	FileItem existFileItem;
	UploadInfo uploadInfo;

	//MAKE_CLIENT(client);
	MAKE_UPLOAD_CLIENT(client);
	int32_t ret = client().preUpload(fileItem, type, existFileItem, uploadInfo);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if (FILE_CREATED == ret)
	{
		setPropertyByFileItem(existFileItem);
		error(FILE_CREATED);
		return RT_OK;
	}
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"preUpload remote file of %I64d failed.", property_.id);
		return ret;
	}

	uploadURL_ = Utility::String::utf8_to_wstring(uploadInfo.upload_url);
	property_.id = uploadInfo.file_id;

	return RT_OK;
}

int32_t RestFile::totalUpload(UploadCallback callback, void* callbackData, const int64_t len, ProgressCallback progressCallback, void* progressCallbackData)
{
	if (uploadURL().empty() || NULL == callbackData)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_UPLOAD_CLIENT(client);	
	ret = client().totalUpload(Utility::String::wstring_to_utf8(uploadURL()), len, callback, callbackData, progressCallback, progressCallbackData);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"total upload remote file of %I64d failed.", property_.id);
		return ret;
	}

	return RT_OK;
}

int32_t RestFile::uploadPart(UploadCallback callback, void* callbackData, uint32_t part, const int64_t len, ProgressCallback progressCallback, void* progressCallbackData)
{
	if (uploadURL().empty() || NULL == callbackData || len <= 0)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_UPLOAD_CLIENT(client);
	ret = client().partUpload(Utility::String::wstring_to_utf8(uploadURL()),  
		part+1, len, callback, callbackData, progressCallback, progressCallbackData);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"part upload remote file of %I64d failed.", property_.id);
		return ret;
	}	

	return RT_OK;
}

int32_t RestFile::download(DownloadCallback callback, void* callbackData, const int64_t offset, const int64_t size, ProgressCallback progressCallback, void* progressCallbackData)
{
	if (size <= 0 || NULL == callbackData)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_DOWNLOAD_CLIENT(client);
	int32_t ret = RT_OK;
	std::string download_url = "";
	ret = client().getDownloadUrl(ownerId_, property_.id, download_url);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get download url of remote file of %I64d failed.", property_.id);
		return ret;
	}
	ret = client().downloadFile(download_url, offset, size, callback, callbackData, progressCallback, progressCallbackData);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"read remote file of %I64d failed.", property_.id);
		return ret;
	}

	return ret;
}

int32_t RestFile::completeUploadPart(const PartList& partList)
{
	if(uploadURL().empty() || partList.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = RT_OK;
	ret = client().partUploadComplete(Utility::String::wstring_to_utf8(uploadURL()), partList, fileItem);
	if (HTTP_FORBIDDEN == ret)
	{
		ret = refreshUploadURL();
		if (RT_OK != ret)
		{
			error(ret);
			HSLOG_ERROR(MODULE_NAME, ret, 
				"refresh upload url of remote file %I64d failed.", property_.id);
			return ret;
		}
		ret = client().partUploadComplete(Utility::String::wstring_to_utf8(uploadURL()), partList, fileItem);
	}
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"complete upload remote file of %I64d failed.", property_.id);
		return ret;
	}

	return RT_OK;
}

int32_t RestFile::getUploadParts(PartInfoList& parts)
{
	if(uploadURL().empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	PartInfoList partInfoList;
	partInfoList.clear();
	ret = client().getUploadPart(Utility::String::wstring_to_utf8(uploadURL()), partInfoList);
	if (HTTP_FORBIDDEN == ret)
	{
		ret = refreshUploadURL();
		if (RT_OK != ret)
		{
			error(ret);
			HSLOG_ERROR(MODULE_NAME, ret, 
				"refresh upload url of remote file %I64d failed.", property_.id);
			return ret;
		}
		partInfoList.clear();
		ret = client().getUploadPart(Utility::String::wstring_to_utf8(uploadURL()), partInfoList);
	}
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get remote file of %I64d upload partial id failed.", property_.id);
		return ret;
	}

	partInfoList.swap(parts);

	return RT_OK;
}

int32_t RestFile::cancelUploadPart()
{
	if(INVALID_ID == property_.id || INVALID_ID == ownerId_)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	int32_t ret = client().partUploadCancel(SD::Utility::String::wstring_to_string(uploadURL()));
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"cancel part upload remote file of %I64d failed.", property_.id);
		return ret;
	}
	return RT_OK;
}

int32_t RestFile::getContentProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().getFileInfo(ownerId_, property_.id, FILE_TYPE_FILE, fileItem);
	ret = (HTTP_NOT_FOUND==ret?RT_FILE_NOEXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get information of remote file of %I64d failed.", property_.id);
		return ret;
	}

	setPropertyByFileItem(fileItem);
	property = this->property(mask);

	property.ctime = fileItem.contentCreatedAt();
	property.mtime = fileItem.contentModifiedAt();

	return RT_OK;
}

void RestFile::setPropertyByFileItem(const FileItem& fileItem)
{
	property_.id = fileItem.id();
	property_.parent = fileItem.parent();
	property_.name = Utility::String::utf8_to_wstring(fileItem.name());
	property_.type = fileItem.type();
	property_.size = fileItem.size();
	property_.mtime = fileItem.modifieTime();
	property_.ctime = fileItem.createTime();
	property_.version = fileItem.version();
	property_.objectId = Utility::String::utf8_to_wstring(fileItem.objectId());
	property_.fingerprint = fileItem.fingerprint();
	property_.modifiedId = fileItem.modifiedBy();
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_ENCRYPT, fileItem.isEncrypt());
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_SHARELINK, fileItem.isSharelink());
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_SHARED, fileItem.isShare());
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_SYNC, fileItem.isSync());
}

int32_t RestFile::refreshUploadURL()
{
	boost::mutex::scoped_lock lock(mutex_);

	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id || uploadURL().empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	std::string url = "";
	MAKE_CLIENT(client);
	ret = client().refreshUploadURL(ownerId_, property_.id, 
		Utility::String::wstring_to_utf8(uploadURL()), url);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"refresh upload url of %I64d failed.", property_.id);
		return ret;
	}

	uploadURL(Utility::String::utf8_to_wstring(url));

	return RT_OK;
}

int32_t RestFile::listFileVersion(LIST_FILEVERSION_RESULT& result)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	int64_t nextOffset = 0;
	FileVersionList fileVersionNodes;

	PageParam pageparam;
	do
	{
		pageparam.offset = nextOffset;
		MAKE_CLIENT(client);
		ret = client().listFileVersion(ownerId_, property_.id, pageparam, nextOffset, fileVersionNodes);

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listFileVersion of %64d failed.", property_.id);
			break;
		}
	} while(nextOffset);

	if(RT_OK == ret)
	{
		for (FileVersionList::iterator it = fileVersionNodes.begin(); it != fileVersionNodes.end(); ++it)
		{
			FILE_VERSION_INFO fileVersionInfo;
			fileVersionInfo.id = it->id();
			fileVersionInfo.parent = it->parent();
			fileVersionInfo.name = Utility::String::utf8_to_wstring(it->name());
			fileVersionInfo.type = it->type();
			fileVersionInfo.ownedBy = it->ownedBy();
			fileVersionInfo.size = it->size();
			fileVersionInfo.status = it->status();
			fileVersionInfo.mtime = it->modifieTime();
			fileVersionInfo.ctime = it->createTime();
			fileVersionInfo.createdBy = it->createdBy();
			fileVersionInfo.modifiedBy = it->modifiedBy();
			fileVersionInfo.objectId = Utility::String::utf8_to_wstring(it->objectId());
			fileVersionInfo.fingerprint = it->fingerprint();
			fileVersionInfo.contentCreatedAt = it->contentCreatedAt();
			fileVersionInfo.contentModifiedAt = it->contentModifiedAt();
			result.push_back(fileVersionInfo);
		}
	}

	return ret;
}

