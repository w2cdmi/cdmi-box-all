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
	int32_t extraType = FOLDER_EXTRA_TYPE_NONE;
	bool autoMerge = false;
	int32_t ret = client().createFolder(ownerId_, property_.parent, 
		Utility::String::wstring_to_utf8(property_.name), 0, 0, extraType, autoMerge, fileItem);
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

int32_t RestFolder::create(FOLDER_EXTRA_TYPE extraType, const bool autoMerge)
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
		Utility::String::wstring_to_utf8(property_.name), 0, 0, extraType, autoMerge, fileItem);
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

int32_t RestFolder::copy(const Path& newParent, bool autoRename)
{
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id 
		|| INVALID_ID == newParent.id() || INVALID_ID == newParent.ownerId())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	MAKE_CLIENT(client);
	FileItem fileItem;
	int32_t ret = client().copyFile(ownerId_, 
		property_.id,
		newParent.ownerId(),
		newParent.id(), 
		autoRename,
		FILE_TYPE_DIR, 
		fileItem);
	ret = (HTTP_CONFLICT==ret?RT_FILE_EXIST_ERROR:ret);
	if (RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"copy remote folder of %I64d to %I64d failed.", 
			property_.id, newParent.id());
		return ret;
	}
	
	setPropertyByFileItem(fileItem);
	property_.parent = newParent.id();

	return RT_OK;
}

int32_t RestFolder::move(const Path& newParent, bool autoRename)
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
		autoRename,
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
	// set the error code first
	error(ret);
	if (RT_OK != ret && RT_FILE_NOEXIST_ERROR != ret)
	{
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
	
	pageParam.limit = 1000;
	do
	{	
		pageParam.offset = nextOffset;
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

	if(RT_OK == ret)
	{
		LIST_FOLDER_RESULT files;
		for (FileItems::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
		{
			FileItem* pFileItem = *it;
			if (NULL == pFileItem)
			{
				continue;
			}

			FILE_DIR_INFO fileDirInfo;
			convFileItemToFileDirInfo(fileDirInfo, *pFileItem);
			if (fileDirInfo.type == FILE_TYPE_FILE)
			{
				files.push_back(fileDirInfo);
			}
			else
			{
				result.push_back(fileDirInfo);
			}
		}
		result.splice(result.end(), files);
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

int32_t RestFolder::listSubDir(LIST_FOLDER_RESULT& result)
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
	PageParam pageParam;
	int64_t nextOffset = 0;
	pageParam.limit = 1000;
	do
	{
		std::list<FileItem*> fileItems;
		pageParam.offset = nextOffset;
		ret = client().listFolder(ownerId_, 
			property_.id, 
			pageParam, nextOffset, fileItems);

		if(RT_OK == ret)
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
				if (fileDirInfo.type == FILE_TYPE_FILE)
				{
					nextOffset = 0;
					break;
				}
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
	} while(nextOffset);

	return ret;
}

int32_t RestFolder::listPage(LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count)
{
	typedef std::list<FileItem*> FileItems;
	if(INVALID_ID == ownerId_ || INVALID_ID == property_.id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}
	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	std::list<FileItem*> fileItems;
	ret = client().listPage(ownerId_, 
		property_.id, 
		pageParam, count, fileItems);

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"list page of %I64d failed.", property_.id);
	}

	if(RT_OK == ret)
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

int32_t RestFolder::getRemoteInfoByName(const Path& path, FILE_DIR_INFO& fileDirInfo)
{
	if (path.parent() == INVALID_ID || path.name().empty())
	{
		return RT_INVALID_PARAM;
	}
	std::list<FileItem*> fileItems;
	MAKE_CLIENT(client);
	int32_t ret = client().getFileInfoByParentAndName(path.ownerId(), 
		path.parent(), 
		Utility::String::wstring_to_utf8(path.name()), 
		fileItems);
	if (RT_OK != ret || fileItems.empty())
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"failed to find the same name as %s in %I64d failed.", 
			Utility::String::wstring_to_string(path.name()).c_str(), path.parent());
		return ret;
	}

	FileItem* fileItem = NULL;
	ret = fileItems.empty()?RT_ERROR:RT_FILE_EXIST_ERROR;
	for (std::list<FileItem*>::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
	{
		if (path.type() == (*it)->type())
		{
			fileItem = *it;
			break;
		}
	}

	if (NULL != fileItem)
	{
		// merge the exist folder to local folder
		RestFolder::convFileItemToFileDirInfo(fileDirInfo, *fileItem);
		ret = RT_OK;
	}

	// release memory
	for (std::list<FileItem*>::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
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

int32_t RestFolder::getNewName(const Path& path, std::wstring& newName)
{
	if (INVALID_ID == path.parent())
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}
	MAKE_CLIENT(client);
	int32_t ret = RT_OK;
	int32_t iCount = 2;
	while(RT_OK==ret)
	{
		ret = client().checkExistByParentAndName(path.ownerId(), 
			path.parent(), 
			Utility::String::wstring_to_utf8(newName));
		if (HTTP_NOT_FOUND == ret)
		{
			return RT_OK;
		}
		newName = newName.substr(0, newName.find_last_of(L"("));
		newName += L"(" + SD::Utility::String::type_to_string<std::wstring>(iCount) + L")";
		++iCount;
	}
	return ret;
}

int32_t RestFolder::getFilePermissions(const Path& path, const int64_t& user_id, File_Permissions& filePermissions)
{
	if(INVALID_ID == path.ownerId() || INVALID_ID == path.id() || INVALID_ID == user_id)
	{
		error(RT_INVALID_PARAM);
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	MAKE_CLIENT(client);
	FileItem fileItem;
	ret = client().getFilePermissions(path.ownerId(), path.id(), user_id, filePermissions);
	if(RT_OK != ret)
	{
		error(ret);
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get remote folder info of %I64d failed.", property_.id);
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
	fileDirInfo.version = fileItem.version();
	fileDirInfo.objectId = Utility::String::utf8_to_wstring(fileItem.objectId());
	fileDirInfo.fingerprint = fileItem.fingerprint();
	fileDirInfo.modifiedId = fileItem.modifiedBy();
	fileDirInfo.extraType = Utility::String::utf8_to_wstring(fileItem.extraType());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_ENCRYPT, fileItem.isEncrypt());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARED, fileItem.isShare());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SYNC, fileItem.isSync());
	SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARELINK, fileItem.isSharelink());
}
