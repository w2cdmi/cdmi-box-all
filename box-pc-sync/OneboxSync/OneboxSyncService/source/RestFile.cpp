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

int32_t RestFile::copy(const Path& newParent)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestFile::move(const Path& newParent)
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
		false,
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
	if (RT_OK != ret && RT_FILE_NOEXIST_ERROR != ret)
	{
		error(ret);
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
	RETRY(3)
	{
		std::string download_url = "";
		ret = client().getDownloadUrl(ownerId_, property_.id, download_url);
		if (RT_OK != ret)
		{
			continue;
		}
		ret = client().downloadFile(download_url, buffer, readLen, offset);
		if (RT_OK == ret)
		{
			break;
		}
	}

	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"read remote file of %I64d failed.", property_.id);
		return ret;
	}

	return (int32_t)readLen;
}

int32_t RestFile::preUpload()
{
	if(INVALID_ID == property_.parent || INVALID_ID == ownerId_ 
		|| property_.name.empty() || !property_.signature.valid())
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
	fileItem.signature(property_.signature);
	fileItem.contentCreatedAt(contentCreate_);
	fileItem.contentModifiedAt(contentModify_);

	UploadType uploadType = (property_.size > TRANSMIT_PART_SIZE ? Upload_MultiPart : Upload_Sigle);
	FileItem existFileItem;
	UploadInfo uploadInfo;

	MAKE_CLIENT(client);
	int32_t ret = client().preUpload(fileItem, uploadType, existFileItem, uploadInfo);
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

int32_t RestFile::totalUpload(unsigned char* buffer, const int64_t& len)
{
	if (uploadURL().empty() || NULL == buffer || 0 > len)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_UPLOAD_CLIENT(client);	
	RETRY(3)
	{
		ret = client().totalUpload(Utility::String::wstring_to_utf8(uploadURL()), buffer, (uint32_t)len);
		if (RT_OK == ret)
		{
			break;
		}
		if (HTTP_FORBIDDEN == ret)
		{
			if (RT_OK != refreshUploadURL())
			{
				ret = error();
				break;
			}
		}
	}

	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"total upload remote file of %I64d failed.", property_.id);
		return ret;
	}
	return RT_OK;
}

int32_t RestFile::uploadPart(unsigned char* buffer, const int64_t& len, uint32_t part)
{
	boost::mutex::scoped_lock lock(mutex_);

	if (uploadURL().empty() || NULL == buffer || 0 >= len)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_UPLOAD_CLIENT(client);
	RETRY(3)
	{
		ret = client().partUpload(Utility::String::wstring_to_utf8(uploadURL()),  
			part+1, buffer, (uint32_t)len);
		if (RT_OK == ret)
		{
			break;
		}
		if (HTTP_FORBIDDEN == ret)
		{
			if (RT_OK != refreshUploadURL())
			{
				ret = error();
				break;
			}
		}
	}

	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"part upload remote file of %I64d failed.", property_.id);
		return ret;
	}

	return RT_OK;
}

int32_t RestFile::completeUploadPart()
{
	if(uploadURL().empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	PartList partList;
	FileItem fileItem;
	if (RT_OK != createUploadParts(partList))
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}	

	int32_t ret = RT_OK;
	RETRY(3)
	{
		ret = client().partUploadComplete(Utility::String::wstring_to_utf8(uploadURL()), partList, fileItem);
		if (RT_OK == ret)
		{
			break;
		}
		if (HTTP_UNAUTHORIZED == ret)
		{
			if (RT_OK != refreshUploadURL())
			{
				ret = error();
				break;
			}
		}
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

int32_t RestFile::getUploadParts(PartList& parts)
{
	if(uploadURL().empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	PartInfoList partInfoList;
	RETRY(3)
	{
		partInfoList.clear();
		ret = client().getUploadPart(Utility::String::wstring_to_utf8(uploadURL()), partInfoList);
		if (RT_OK == ret)
		{
			break;
		}
		if (HTTP_FORBIDDEN == ret)
		{
			if (RT_OK != refreshUploadURL())
			{
				ret = error();
				break;
			}
		}
	}
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get remote file of %I64d upload partial id failed.", property_.id);
		return ret;
	}

	bool isComplete = false;
	for(PartInfoList::const_iterator it = partInfoList.begin(); it != partInfoList.end(); ++it)
	{
		int64_t tmpSize = property_.size - (int64_t)(it->partId - 1) * TRANSMIT_PART_SIZE;
		if(tmpSize >= TRANSMIT_PART_SIZE)
		{
			isComplete = (it->size == TRANSMIT_PART_SIZE);
		}
		else
		{
			isComplete = (it->size == tmpSize);
		}

		if(isComplete)
		{
			parts.push_back(it->partId - 1);
		}
	}

	std::sort(parts.begin(),parts.end());

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
	property_.version = Utility::String::utf8_to_wstring(fileItem.version());
	property_.signature = fileItem.signature();
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_ENCRYPT, fileItem.isEncrypt());
	SET_BIT_VALUE_BY_BOOL(property_.flags, OBJECT_FLAG_SHARED, fileItem.isSharelink());
}

int32_t RestFile::createUploadParts(PartList& parts)
{
	if (TRANSMIT_PART_SIZE >= property_.size)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t maxPart = (int32_t)(property_.size / TRANSMIT_PART_SIZE);
	if (((int64_t)maxPart*TRANSMIT_PART_SIZE) < (int64_t)property_.size)
	{
		++maxPart;
	}

	for (int32_t i = 1; i <= maxPart; ++i)
	{
		parts.push_back(i);
	}

	return RT_OK;
}

int32_t RestFile::refreshUploadURL()
{
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
