#include "SyncFileSystemMgr.h"
#include "RestFile.h"
#include "RestFolder.h"
#include "LocalFile.h"
#include "LocalFolder.h"
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncFileSystemMgr")
#endif

class SyncFileSystemMgrImpl : public SyncFileSystemMgr
{
public:
	SyncFileSystemMgrImpl(UserContext* userContext);

	virtual int32_t create(const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type);

	virtual int32_t remove(const Path& path, ADAPTER_FILE_TYPE type);

	virtual int32_t move(const Path& path, const Path& parent, ADAPTER_FILE_TYPE type);

	virtual int32_t rename(const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type);

	virtual int32_t getProperty(const Path& path, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type);

	virtual int32_t isExist(const Path& path, ADAPTER_FILE_TYPE type);

	virtual int32_t listFolder(const Path& path, LIST_FOLDER_RESULT& result, ADAPTER_FILE_TYPE type);

private:
	UserContext* userContext_;
};

SyncFileSystemMgr* SyncFileSystemMgr::create(UserContext* userContext)
{
	return static_cast<SyncFileSystemMgr*>(new SyncFileSystemMgrImpl(userContext));
}

SyncFileSystemMgrImpl::SyncFileSystemMgrImpl(UserContext* userContext)
	:userContext_(userContext)
{

}

int32_t SyncFileSystemMgrImpl::create(const Path& parent, const std::wstring& name, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == parent.id() || name.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	IFolderPtr folder;

	switch (type)
	{
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			Path path;
			path.parent(parent.id());
			path.name(name);
			path.path(parent.path()+PATH_DELIMITER+name);
			path.ownerId(parent.ownerId());
			folder = IFolderPtr(new LocalFolder(path, userContext_));
			ret = folder->create();
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			Path path;
			path.parent(parent.id());
			path.name(name);
			path.ownerId(parent.ownerId());
			folder = IFolderPtr(new RestFolder(path, userContext_));
			ret = folder->create();
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "create file/folder %I64d:%s failed.", 
			parent.id(), Utility::String::wstring_to_string(name).c_str());
		return ret;
	}

	info = folder->property();

	return RT_OK;
}

int32_t SyncFileSystemMgrImpl::remove(const Path& path, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	switch (type)
	{
	case ADAPTER_FILE_TYPE_LOCAL:
		{
			IFilePtr file(new LocalFile(path, userContext_));
			ret = file->remove();
		}
		break;
	case ADAPTER_FILE_TYPE_REST:
		{
			IFilePtr file(new RestFile(path, userContext_));
			ret = file->remove();
		}
		break;
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			IFolderPtr folder(new LocalFolder(path, userContext_));
			ret = folder->remove();
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			IFolderPtr folder(new RestFolder(path, userContext_));
			ret = folder->remove();
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "remove file/folder %I64d:%s failed.", 
			path.id(), Utility::String::wstring_to_string(path.name()).c_str());
		return ret;
	}

	return RT_OK;
}

int32_t SyncFileSystemMgrImpl::move(const Path& path, const Path& parent, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id() || INVALID_ID == parent.id())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	switch (type)
	{
	case ADAPTER_FILE_TYPE_LOCAL:
		{
			IFilePtr file(new LocalFile(path, userContext_));
			ret = file->move(parent);
		}
		break;
	case ADAPTER_FILE_TYPE_REST:
		{
			IFilePtr file(new RestFile(path, userContext_));
			ret = file->move(parent);
		}
		break;
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			IFolderPtr folder(new LocalFolder(path, userContext_));
			ret = folder->move(parent);
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			IFolderPtr folder(new RestFolder(path, userContext_));
			ret = folder->move(parent);
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "move file/folder %I64d:%s failed.", 
			path.id(), Utility::String::wstring_to_string(parent.name()).c_str());
		return ret;
	}

	return RT_OK;
}

int32_t SyncFileSystemMgrImpl::rename(const Path& path, const std::wstring& name, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id() || name.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	switch (type)
	{
	case ADAPTER_FILE_TYPE_LOCAL:
		{
			IFilePtr file(new LocalFile(path, userContext_));
			ret = file->rename(name);
		}
		break;
	case ADAPTER_FILE_TYPE_REST:
		{
			IFilePtr file(new RestFile(path, userContext_));
			ret = file->rename(name);
		}
		break;
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			IFolderPtr folder(new LocalFolder(path, userContext_));
			ret = folder->rename(name);
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			IFolderPtr folder(new RestFolder(path, userContext_));
			ret = folder->rename(name);
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "rename file/folder %I64d:%s failed.", 
			path.id(), Utility::String::wstring_to_string(name).c_str());
		return ret;
	}

	return RT_OK;
}

int32_t SyncFileSystemMgrImpl::getProperty(const Path& path, FILE_DIR_INFO& info, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	switch (type)
	{
	case ADAPTER_FILE_TYPE_LOCAL:
		{
			IFilePtr file(new LocalFile(path, userContext_));
			ret = file->getProperty(info, PROPERTY_MASK_ALL);
		}
		break;
	case ADAPTER_FILE_TYPE_REST:
		{
			IFilePtr file(new RestFile(path, userContext_));
			ret = file->getProperty(info, PROPERTY_MASK_ALL);
		}
		break;
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			IFolderPtr folder(new LocalFolder(path, userContext_));
			ret = folder->getProperty(info, PROPERTY_MASK_ALL);
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			IFolderPtr folder(new RestFolder(path, userContext_));
			ret = folder->getProperty(info, PROPERTY_MASK_ALL);
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get property of file/folder %I64d:%s failed.", 
			path.id(), Utility::String::wstring_to_string(path.name()).c_str());
		return ret;
	}

	return RT_OK;
}

int32_t SyncFileSystemMgrImpl::isExist(const Path& path, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	switch (type)
	{
	case ADAPTER_FILE_TYPE_LOCAL:
		{
			IFilePtr file(new LocalFile(path, userContext_));
			if (!file->isExist())
			{
				ret = RT_FILE_NOEXIST_ERROR;
			}
		}
		break;
	case ADAPTER_FILE_TYPE_REST:
		{
			IFilePtr file(new RestFile(path, userContext_));
			if (!file->isExist())
			{
				ret = file->error();
			}
		}
		break;
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			IFolderPtr folder(new LocalFolder(path, userContext_));
			if (!folder->isExist())
			{
				ret = RT_FILE_NOEXIST_ERROR;
			}
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			IFolderPtr folder(new RestFolder(path, userContext_));
			if (!folder->isExist())
			{
				ret = folder->error();
			}
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	return ret;
}

int32_t SyncFileSystemMgrImpl::listFolder(const Path& path, LIST_FOLDER_RESULT& result, ADAPTER_FILE_TYPE type)
{
	if (INVALID_ID == path.id())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	IFolderPtr folder;

	switch (type)
	{
	case ADAPTER_FOLDER_TYPE_LOCAL:
		{
			folder = IFolderPtr(new LocalFolder(path, userContext_));
			ret = folder->listFolder(result);
		}
		break;
	case ADAPTER_FOLDER_TYPE_REST:
		{
			folder = IFolderPtr(new RestFolder(path, userContext_));
			ret = folder->listFolder(result);
		}
		break;
	default:
		ret = RT_INVALID_PARAM;
		break;
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "list folder %I64d:%s failed.", 
			path.id(), Utility::String::wstring_to_string(path.name()).c_str());
		return ret;
	}

	return RT_OK;
}

