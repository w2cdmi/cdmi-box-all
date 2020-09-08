#include "LocalCreateFolder.h"
#include "RestFolder.h"
#include "LocalFolder.h"
#include "Utility.h"
#include "PathMgr.h"
#include "NetworkMgr.h"
#include "TransTableMgr.h"
#include "FilterMgr.h"
#include <vector>
#include <boost/algorithm/string.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalCreateFolder")
#endif

class LocalCreateFolder::Impl : public ITransmit
{
public:
	Impl(UserContext* userContext, TransTask* transTask)
		:ITransmit(transTask)
		,errorCode_(transTask)
		,userContext_(userContext)
		,localFolder_(NULL)
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

		Path localPath = userContext_->getPathMgr()->makePath();
		localPath.path(transDetailNode->parent + PATH_DELIMITER + transDetailNode->name);

		localFolder_.reset(static_cast<IFolder*>(new LocalFolder(localPath, userContext_)));
		if (!localFolder_->isExist())
		{
			int32_t ret = localFolder_->create();
			if (RT_OK != ret)
			{
				errorCode_.SetErrorCode(ret);
				return;
			}
		}
	}

	virtual void finishTransmit()
	{
		// update the transmit status
		setStatus(TRANSMIT_END);

		FILE_DIR_INFO lFileDirInfo, rFileDirInfo;
		if (NULL != localFolder_.get())
		{
			lFileDirInfo = localFolder_->property();
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
	TransTaskErrorCode errorCode_;
	UserContext* userContext_;
	IFolderPtr localFolder_;
};

LocalCreateFolder::LocalCreateFolder(UserContext* userContext, TransTask* transTask)
	:ITransmit(transTask)
	,impl_(new Impl(userContext, transTask))
{
}

void LocalCreateFolder::transmit()
{
	return impl_->transmit();
}

void LocalCreateFolder::finishTransmit()
{
	return impl_->finishTransmit();
}

void LocalCreateFolder::addNotify(ITransmitNotify* notify)
{
	return impl_->addNotify(notify);
}

void LocalCreateFolder::setSerializer(TransSerializer* serializer)
{
	return impl_->setSerializer(serializer);
}
