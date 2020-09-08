#pragma warning(disable:4996)
#include "RestCreate.h"
#include "RestFolder.h"
#include "Utility.h"
#include "PathMgr.h"
#include "NetworkMgr.h"
#include <vector>
#include <boost/algorithm/string.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestCreate")
#endif

#define UNUSE_PART_ID (-1)

RestCreate::RestCreate(UserContext* userContext, TransTask* transTask)
	:userContext_(userContext)
	,transTask_(transTask)
	,remoteFolder_(NULL)
{

}

RestCreate::~RestCreate()
{

}

void RestCreate::transmit()
{
	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();
	if (ATT_Upload_Manual == transTaskNode.id.type)
	{
		transTask_->SetErrorCode(createDir(
			Utility::String::string_to_type<int64_t>(transTaskNode.parent), transTaskNode.name));
	}
	else if (ATT_Upload_Attachements == transTaskNode.id.type)
	{
		transTask_->SetErrorCode(createDir(transTaskNode.id.id));
	}
}

void RestCreate::finishTransmit()
{
	if (!transTask_->IsError() && !transTask_->IsCancel())
	{
		std::auto_ptr<FILE_DIR_INFO> fileDirInfo(new FILE_DIR_INFO);
		*fileDirInfo = remoteFolder_->property();
		transTask_->getCustomInfo().type = FolderInfo;
		transTask_->getCustomInfo().content = (void*)fileDirInfo.get();
		transTask_->notifyCustomInfo();
	}
}

int32_t RestCreate::createDir(const int64_t parent, const std::wstring& name)
{
	if (INVALID_ID == parent || name.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_ERROR;
	AsyncTransTaskNode& transTaskNode = transTask_->getTaskNode();
	Path remotePath = userContext_->getPathMgr()->makePath();
	remotePath.parent(parent);
	remotePath.name(name);
	remotePath.ownerId(Utility::String::string_to_type<int64_t>(transTaskNode.ownerId));
	// the remoteFolder_ may not be null, should reset it to release the old pointer
	remoteFolder_.reset(static_cast<IFolder*>(new RestFolder(remotePath, userContext_)));
	assert(NULL != remoteFolder_.get());

	ret = remoteFolder_->create();
	if (RT_FILE_EXIST_ERROR == ret)
	{
		std::list<FileItem*> fileItems;
		MAKE_CLIENT(client);
		ret = client().getFileInfoByParentAndName(remotePath.ownerId(), 
			remotePath.parent(), 
			Utility::String::wstring_to_utf8(remotePath.name()), 
			fileItems);
		if (RT_OK != ret || fileItems.empty())
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"failed to find the same name as %s in %I64d failed.", 
				Utility::String::wstring_to_string(remotePath.name()).c_str(), remotePath.parent());
			return ret;
		}

		FileItem* fileItem = NULL;
		for (std::list<FileItem*>::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
		{
			if (FILE_TYPE_FILE == (*it)->type())
			{
				HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR, 
					"create remote folder failed, the same name %s file is exist", 
					Utility::String::wstring_to_string(remotePath.name()).c_str());
				ret = RT_FILE_EXIST_ERROR;

				fileItem = NULL;
				break;
			}
			fileItem = *it;
		}

		if (NULL != fileItem)
		{
			// merge the exist folder to local folder
			FILE_DIR_INFO fileDirInfo;
			RestFolder::convFileItemToFileDirInfo(fileDirInfo, *fileItem);
			remoteFolder_->property(fileDirInfo);
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
	}
	else if (RT_OK != ret)
	{
		return ret;
	}

	return ret;
}

int32_t RestCreate::createDir(const std::wstring& path)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_ERROR;
	std::wstring tempPath = Utility::String::rtrim(path, L"\\");
	tempPath = Utility::String::ltrim(tempPath, L"\\");
	// if the remote directory path is "/", return RT_INVALID_PARAM
	if (tempPath.empty())
	{
		return RT_INVALID_PARAM;
	}

	std::vector<std::wstring> result;
	int64_t parent = ROOT_PARENTID;
	boost::split(result, tempPath, boost::is_any_of(L"\\"), boost::algorithm::token_compress_on);
	for (std::vector<std::wstring>::iterator it = result.begin(); it != result.end(); ++it)
	{
		ret = createDir(parent, *it);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, 
				"create folder of %s failed, folder name is %s.", 
				Utility::String::wstring_to_string(path).c_str(), 
				Utility::String::wstring_to_string(*it).c_str());
			return ret;
		}

		// set the first level of folder unsync
		if (ROOT_PARENTID == parent && (remoteFolder_->property().flags&OBJECT_FLAG_SYNC))
		{
			MAKE_CLIENT(client);
			ret = client().setFolderSync(
				Utility::String::string_to_type<int64_t>(transTask_->getTaskNode().ownerId), 
				remoteFolder_->property().id, 
				false);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, 
					"set folder of %s unsync failed.", 
					Utility::String::wstring_to_string(path).c_str());
				return ret;
			}
		}		

		parent = remoteFolder_->property().id;
		if (INVALID_ID == parent)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"invalid id, create folder of %s failed, folder name is %s.", 
				Utility::String::wstring_to_string(path).c_str(), 
				Utility::String::wstring_to_string(*it).c_str());
			return RT_INVALID_PARAM;
		}
	}

	return RT_OK;
}