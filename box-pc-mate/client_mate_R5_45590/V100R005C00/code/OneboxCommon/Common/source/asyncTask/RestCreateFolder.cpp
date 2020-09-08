#include "RestCreateFolder.h"
#include "RestFolder.h"
#include "Utility.h"
#include "PathMgr.h"
#include "NetworkMgr.h"
#include "TransTableMgr.h"
#include "FilterMgr.h"
#include <vector>
#include <boost/algorithm/string.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("RestCreateFolder")
#endif

class RestCreateFolder::Impl : public ITransmit
{
public:
	Impl(UserContext* userContext, TransTask* transTask)
		:ITransmit(transTask)
		,errorCode_(transTask)
		,userContext_(userContext)
		,remoteFolder_(NULL)
	{

	}

	virtual ~Impl()
	{

	}

	virtual void transmit()
	{
		setStatus(TRANSMIT_START);

		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();

		if (userContext_->getFilterMgr()->isFilter(transDetailNode->name))
		{
			errorCode_.SetErrorCode(RT_DIFF_FILTER);
			return;
		}
		int32_t ret = createDir(Utility::String::string_to_type<int64_t>(transDetailNode->parent), transDetailNode->name);
		if (RT_OK != ret)
		{
			errorCode_.SetErrorCode(ret);
			return;
		}
	}

	virtual void finishTransmit()
	{
		// update the transmit status
		setStatus(TRANSMIT_END);

		FILE_DIR_INFO lFileDirInfo, rFileDirInfo;
		if (NULL != remoteFolder_.get())
		{
			rFileDirInfo = remoteFolder_->property();
		}

		int32_t ret = RT_OK;
		for (ITransmitNotifies::iterator it = notifies_.begin(); it != notifies_.end(); ++it)
		{
			ret = (*it)->notify(this, lFileDirInfo, rFileDirInfo);
			if (RT_OK != ret)
			{
				errorCode_.SetErrorCode(ret);
				continue;
			}
		}

		if (transTask_->IsError())
		{
			return;
		}
		
		errorCode_.SetErrorCode(ret);
	}

private:
	int32_t createDir(const int64_t parent, const std::wstring& name)
	{
		if (INVALID_ID == parent || name.empty())
		{
			return RT_INVALID_PARAM;
		}
		if (userContext_->getFilterMgr()->isFilter(name))
		{
			return RT_DIFF_FILTER;
		}

		int32_t ret = RT_ERROR;
		AsyncTransDetailNode& transDetailNode = transTask_->getTransDetailNode();
		Path remotePath = userContext_->getPathMgr()->makePath();
		remotePath.parent(parent);
		remotePath.name(name);
		remotePath.ownerId(transDetailNode->root->userId);
		remoteFolder_.reset(static_cast<IFolder*>(new RestFolder(remotePath, userContext_)));
		if (NULL == remoteFolder_.get())
		{
			return RT_INVALID_PARAM;
		}

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
			HSLOG_ERROR(MODULE_NAME, ret, "failed to create remote directory of %s.", 
				Utility::String::wstring_to_string(name).c_str());
			return ret;
		}

		return ret;
	}

private:
	TransTaskErrorCode errorCode_;
	UserContext* userContext_;
	IFolderPtr remoteFolder_;
};

RestCreateFolder::RestCreateFolder(UserContext* userContext, TransTask* transTask)
	:ITransmit(transTask)
	,impl_(new Impl(userContext, transTask))
{
}

void RestCreateFolder::transmit()
{
	return impl_->transmit();
}

void RestCreateFolder::finishTransmit()
{
	return impl_->finishTransmit();
}

void RestCreateFolder::addNotify(ITransmitNotify* notify)
{
	return impl_->addNotify(notify);
}

void RestCreateFolder::setSerializer(TransSerializer* serializer)
{
	return impl_->setSerializer(serializer);
}
