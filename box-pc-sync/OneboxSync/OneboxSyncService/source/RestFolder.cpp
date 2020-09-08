#include "RestFolder.h"
#include "RestFile.h"
#include "Utility.h"
#include "NetworkMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestFolder")
#endif

RestFolder::RestFolder(const Path& path, UserContext* userContext)
	:IFolder(path)
	,userContext_(userContext)
{
}

RestFolder::~RestFolder()
{
}

int32_t RestFolder::create()
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.parent 
		|| property_.name.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = client().createFolder(ownerId_, property_.parent, 
		Utility::String::wstring_to_utf8(property_.name), 0, 0, fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"create remote folder of %I64d failed.", property_.id);
		return ret;
	}
	
	setPropertyByFileItem(fileItem);

	return RT_OK;
}

int32_t RestFolder::remove()
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = client().removeFile(ownerId_, 
		property_.id, 
		FILE_TYPE_DIR);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"remove remote folder of %I64d failed.", property_.id);
		return ret;
	}

	return RT_OK;
}

int32_t RestFolder::rename(const std::wstring& newName)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| newName.empty())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = client().renameFile(ownerId_, 
		property_.id, 
		Utility::String::wstring_to_utf8(newName), 
		FILE_TYPE_DIR, 
		fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"rename remote folder of %I64d to %s failed.", property_.id, 
			Utility::String::wstring_to_string(newName).c_str());
		return ret;
	}
	
	setPropertyByFileItem(fileItem);
	property_.name = newName;

	return RT_OK;
}

int32_t RestFolder::copy(const Path& newParent)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestFolder::move(const Path& newParent)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| INVALID_ID == newParent.id())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = client().moveFile(ownerId_, 
		property_.id, 
		newParent.id(), 
		//Utility::String::wstring_to_utf8(property_.name), 
		false,
		FILE_TYPE_DIR, 
		fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"move remote folder of %I64d to %I64d failed.", 
			property_.id, newParent.id());
		return ret;
	}
	
	setPropertyByFileItem(fileItem);
	property_.parent = newParent.id();

	return RT_OK;
}

bool RestFolder::isExist()
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return false;
	}

	MAKE_CLIENT(client);
	int32_t ret = client().checkFileExist(ownerId_, 
		property_.id, 
		FILE_TYPE_DIR);
	ret = (HTTP_NOT_FOUND==ret?RT_FILE_NOEXIST_ERROR:ret);
	if (RT_OK != ret && RT_FILE_NOEXIST_ERROR != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"check remote folder of %I64d exist failed.", property_.id);
		return false;
	}

	return (RT_OK == ret);
}

int32_t RestFolder::listFolder(LIST_FOLDER_RESULT& result)
{
	typedef std::list<FileItem*> FileItems;

	result.clear();

	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	std::list<FileItem*> fileItems;
	PageParam pageParam;
	int64_t nextOffset = 0;

	do
	{		
		pageParam.offset=nextOffset;
		ret = client().listFolder(ownerId_, 
			property_.id, 
			pageParam, nextOffset, fileItems);

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"list remote folder of %I64d failed.", property_.id);
			break;
		}
	} while(nextOffset);

	if (RT_OK == ret)
	{
		for (FileItems::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
		{
			FileItem* pFileItem = *it;
			if (NULL == pFileItem)
			{
				continue;
			}

			FILE_DIR_INFO fileDirInfo;
			convFileItemToFileDirInfo(fileDirInfo, *pFileItem);
			result.push_back(fileDirInfo);
		}
	}

	// release memory
	for (FileItems::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
	{
		FileItem* pFileItem = *it;
		if (NULL != pFileItem)
		{
			delete pFileItem;
			pFileItem = NULL;
		}
	}

	return ret;
}

int32_t RestFolder::getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().getFileInfo(ownerId_, 
		property_.id, 
		FILE_TYPE_DIR, 
		fileItem);
	if(RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get remote folder info of %I64d failed.", property_.id);
		return ret;
	}

	setPropertyByFileItem(fileItem);
	property = this->property(mask);

	return ret;
}

int32_t RestFolder::setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
	return RT_NOT_IMPLEMENT;
}

void RestFolder::setPropertyByFileItem(const FileItem& fileItem)
{
	convFileItemToFileDirInfo(property_, fileItem);
}

void RestFolder::convFileItemToFileDirInfo(FILE_DIR_INFO& fileDirInfo, const FileItem& fileItem)
{
	fileDirInfo.id = fileItem.id();
	fileDirInfo.parent = fileItem.parent();
	fileDirInfo.name = Utility::String::utf8_to_wstring(fileItem.name());
	fileDirInfo.type = fileItem.type();
	fileDirInfo.size = fileItem.size();
	fileDirInfo.mtime = fileItem.modifieTime();
	fileDirInfo.ctime = fileItem.createTime();
	fileDirInfo.version = Utility::String::utf8_to_wstring(fileItem.version());
	fileDirInfo.signature = fileItem.signature();
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_ENCRYPT, fileItem.isEncrypt());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARED, fileItem.isShare());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SYNC, fileItem.isSync());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARELINK, fileItem.isSharelink());
}
