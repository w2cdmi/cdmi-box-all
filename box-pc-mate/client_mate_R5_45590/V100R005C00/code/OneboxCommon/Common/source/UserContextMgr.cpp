#include "UserContextMgr.h"
#include <boost/thread/mutex.hpp>
#include "RestClient.h"
#include "CredentialMgr.h"
#include "ConfigureMgr.h"
#include "UserInfoMgr.h"
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("UserContextMgr")
#endif

UserContextMgr* UserContextMgr::instance_ = NULL;

class UserContextMgrImpl : public UserContextMgr
{
private:
	UserContexts userContexts_;
	boost::mutex mutex_;

public:
	virtual ~UserContextMgrImpl()
	{
		for (UserContexts::iterator it = userContexts_.begin(); it != userContexts_.end(); ++it)
		{
			if (NULL != *it)
			{
				delete *it;
				*it = NULL;
			}
		}
		userContexts_.clear();
	}

	virtual UserContext* getUserContext(const UserContextId& userContextId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (userContexts_.empty())
		{
			return NULL;
		}
		for (UserContexts::iterator it = userContexts_.begin(); it != userContexts_.end(); ++it)
		{
			if ((*it)->id.id == userContextId.id)
			{
				return *it;
			}
		}
		// the user maybe deleted
		if (UserContext_User == userContextId.type)
		{
			return NULL;
		}
		return getChildUserContext(userContexts_.front(), userContextId);
	}

	virtual UserContext* getUserContext(const int64_t userId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (userContexts_.empty())
		{
			return NULL;
		}
		for (UserContexts::iterator it = userContexts_.begin(); it != userContexts_.end(); ++it)
		{
			if ((*it)->id.id == userId)
			{
				return *it;
			}
		}
		return NULL;
	}

	virtual UserContext* createUserContext(int64_t uiHandle)
	{
		boost::mutex::scoped_lock lock(mutex_);
		UserContext* userContext = UserContextMgr::createUserContext(uiHandle);
		if (NULL != userContext)
		{
			userContexts_.push_back(userContext);
		}
		return userContext;
	}

	virtual UserContext* createUserContext(UserContext* parent, const int64_t ownerId, const UserContextType type, const std::wstring& name = L"")
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (!userContexts_.empty())
		{
			for (UserContexts::iterator it = userContexts_.begin(); it != userContexts_.end(); ++it)
			{
				if ((*it)->id.id == ownerId)
				{
					return *it;
				}
			}
		}
		return createUserContextNoLock(parent, ownerId, type, name);
	}

	virtual int32_t removeUserContext(const UserContextId& userContextId)
	{
		boost::mutex::scoped_lock lock(mutex_);
		for (UserContexts::iterator it = userContexts_.begin(); it != userContexts_.end(); ++it)
		{
			if ((*it)->id.id == userContextId.id)
			{
				delete *it;
				*it = NULL;
				userContexts_.erase(it);
				return RT_OK;
			}
		}
		return RT_ERROR;
	}

	virtual UserContext* getDefaultUserContext()
	{
		boost::mutex::scoped_lock lock(mutex_);
		if (userContexts_.empty())
		{
			return NULL;
		}
		return userContexts_.front();
	}

private:
	UserContext* createUserContextNoLock(UserContext* parent, const int64_t ownerId, const UserContextType type, const std::wstring& name)
	{
		UserContext* userContext = UserContextMgr::createUserContext(parent, ownerId, type, name);
		if (NULL != userContext)
		{
			userContexts_.push_back(userContext);
		}
		return userContext;
	}

	UserContext* getShareUserChildUserContext(UserContext* parent, const UserContextId& userContextId)
	{
		UserContext* userContext = createUserContextNoLock(parent, userContextId.id, userContextId.type, userContextId.name);
		if (NULL == userContext)
		{
			return NULL;
		}
		return userContext;
	}

	UserContext* getTeamspaceChildUserContext(UserContext* parent, const UserContextId& userContextId)
	{
		UserTeamSpaceNodeInfoArray result;
		RestClient client(parent->getCredentialMgr()->getCredentialInfo(), 
			*(parent->getConfigureMgr()->getConfigure()));
		int64_t count = 0;
		PageParam pageparam;

		pageparam.offset = 0;
		pageparam.limit = 1000;

		do 
		{
			int32_t ret = client.getTeamSpaceListUser(parent->id.id, 
				pageparam, count, result);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "get user teamspace list failed.");
			}
			pageparam.offset += pageparam.limit;
		} while (pageparam.offset < count);

		for (UserTeamSpaceNodeInfoArray::iterator it = result.begin(); it != result.end(); ++it)
		{
			int64_t teamspaceId = it->teamId();
			if (teamspaceId == userContextId.id)
			{
				UserContext* userContext = createUserContextNoLock(parent, userContextId.id, userContextId.type, userContextId.name);
				if (NULL == userContext)
				{
					return NULL;
				}
				userContext->id.id = teamspaceId;
				userContext->id.parent = parent->id.id;
				userContext->id.type = UserContext_Teamspace;
				userContext->id.name = Utility::String::utf8_to_wstring(it->member_.name());
				return userContext;
			}
		}
		return NULL;
	}

	UserContext* getChildUserContext(UserContext* parent, const UserContextId& userContextId)
	{
		if (UserContext_Teamspace == userContextId.type)
		{
			return getTeamspaceChildUserContext(parent, userContextId);
		}
		else if (UserContext_ShareUser == userContextId.type)
		{
			return getShareUserChildUserContext(parent, userContextId);
		}
		return NULL;
	}
};

UserContextMgr::UserContextMgr()
{
	std::wstring logFile = Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"OneboxCommon.log";
	ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"),
		TP_FILE,
		Utility::String::wstring_to_string(logFile));
	RestClient::initialize();
}

UserContextMgr::~UserContextMgr()
{
	RestClient::deinitialize();
	ISSP_LogExit();
}

UserContext* UserContextMgr::getUserContext(const UserContextId& userContextId)
{
	return NULL;
}

UserContext* UserContextMgr::getUserContext(const int64_t userId)
{
	return NULL;
}

UserContext* UserContextMgr::createUserContext(int64_t uiHandle)
{
	return new UserContext(uiHandle);
}

UserContext* UserContextMgr::createUserContext(UserContext* parent, const int64_t ownerId, const UserContextType type, const std::wstring& name)
{
	UserContext* userContext = parent->clone();
	if (NULL == userContext)
	{
		return NULL;
	}
	userContext->id.id = ownerId;
	userContext->id.name = name;
	userContext->id.type = type;
	userContext->id.parent = parent->id.id;

	// set the user configure path
	userContext->getConfigureMgr()->updateUserConfigurePath(userContext->id.id, userContext->id.parent);
	// initial the user configure
	userContext->getConfigureMgr()->unserialize();

	// set the user information
	userContext->getUserInfoMgr()->setUserId(ownerId);
	userContext->getUserInfoMgr()->setUserName(name);

	return userContext;
}

int32_t UserContextMgr::removeUserContext(const UserContextId& userContextId)
{
	return RT_NOT_IMPLEMENT;
}

UserContext* UserContextMgr::getDefaultUserContext()
{
	return NULL;
}

UserContextMgr* UserContextMgr::getInstance()
{
	if (NULL == instance_)
	{
		instance_ = static_cast<UserContextMgr*>(new UserContextMgrImpl);
	}
	return instance_;
}

void UserContextMgr::releaseInstance()
{
	if (NULL != instance_)
	{
		delete instance_;
		instance_ = NULL;
	}
}
