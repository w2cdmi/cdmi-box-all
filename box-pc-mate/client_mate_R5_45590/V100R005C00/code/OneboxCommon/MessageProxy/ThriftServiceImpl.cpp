#include "ThriftServiceImpl.h"
#include "thrift/ThriftService.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/transport/TServerSocket.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/PlatformThreadFactory.h"
#include "thrift/concurrency/thread.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "UserContextMgr.h"
#include "SyncFileSystemMgr.h"
#include "PathMgr.h"
#include "AsyncTaskMgr.h"
#include "NotifyMgr.h"
#include "ShareResMgr.h"
#include "TeamSpaceResMgr.h"
#include "WorkModeMgr.h"
#include "BackupAllCommon.h"
#include "BackupAllMgr.h"
#include "SysConfigureMgr.h"
#include "NetworkMgr.h"
#include "UserContext.h"
#include "..\..\Onebox\UI\include\InILanguage.h"
#include "..\Common\source\UserInfoMgrImpl.h"
#include "SmartHandle.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("MessageProxy")
#endif

#define THRIFT_THREAD_NUM 2

#define SORT_FILED_NAME ("name")
#define SORT_FILED_TYPE ("type")
#define SORT_DIRECTION_ASC ("asc")
#define SORT_DIRECTION_DESC ("desc")

using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;
using namespace ::apache::thrift::concurrency;
using namespace OneboxThriftService;

class ThriftServiceImpl : public ThriftServiceIf
{
public:
	ThriftServiceImpl();
	virtual ~ThriftServiceImpl();

private:
	int32_t uploadImpl(const std::string& localPath, const int64_t remoteParentId, UserContext* userContext, AsyncTransType type, const std::string& group);

private:
	virtual int32_t getServiceStatus();
	virtual int64_t getCurrentUserId();
	virtual void listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType);
	virtual void listLocalDir(std::vector<File_Node> & _return, const std::string& path);
	virtual void GetPathName(std::string& _return, const int64_t fileId);
	virtual int32_t upload(const std::string& localPath, const int64_t remoteParentId, const int64_t userId, const int32_t userType, const std::string& group);
	virtual void getTask(TransTask_RootNode& _return, const std::string& group);
	virtual int32_t pauseTask(const std::string& group);
	virtual int32_t delTask(const std::string& group);
	virtual int32_t resumeTask(const std::string& group);
	virtual int32_t isTaskExist(const std::string& group);
	virtual void createShareLink(std::string& _return, const int64_t fileId);
	virtual int32_t addNotify(const int64_t handle);
	virtual int32_t removeNotify(const int64_t handle);
	virtual void listTeamspace(std::vector<TeamSpace_Node> & _return);
	virtual int32_t sendMessage(const int32_t type, const std::string& msg1, const std::string& msg2, const std::string& msg3, const std::string& msg4, const std::string& msg5);
	virtual void getNewName(std::string& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& defaultName);
	virtual void createFolder(File_Node& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& name);
	virtual void createFolderNoSync(File_Node& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& name,const int32_t extraType);
	virtual int32_t renameFolder(const int64_t userId, const int32_t userType, const int64_t fileId, const std::string& name);
	virtual int32_t uploadOutlook(const std::string& localPath, const int64_t remoteParentId, const std::string& group);
	virtual int32_t uploadOffice(const std::string& localPath, const int64_t remoteParentId, const std::string& group);
	virtual bool needAddFullBackup(const std::string& strPath);
	virtual int32_t addFullBackup(const std::string& strPath);
};

int32_t ThriftServiceImpl::uploadImpl(const std::string& localPath, const int64_t remoteParentId, UserContext* userContext, AsyncTransType type, const std::string& group)
{
	if (localPath.empty() || remoteParentId < 0 || NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "invalid param.");
		return RT_INVALID_PARAM;
	}

	Path local = userContext->getPathMgr()->makePath();
	local.path(Utility::String::utf8_to_wstring(localPath));
	local.type(Utility::FS::is_directory(local.path())?FILE_TYPE_DIR:FILE_TYPE_FILE);

	Path remoteParent = userContext->getPathMgr()->makePath();
	remoteParent.id(remoteParentId);
	remoteParent.ownerId(userContext->id.id);

	int32_t ret = userContext->getAsyncTaskMgr()->upload(Utility::String::utf8_to_wstring(group), 
		local, remoteParent, type);

	return ret;
}

ThriftServiceImpl::ThriftServiceImpl()
{
}

ThriftServiceImpl::~ThriftServiceImpl()
{
}

int32_t ThriftServiceImpl::getServiceStatus()
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return WorkMode_Uninitial;
	}
	return defaultUserContext->getWorkmodeMgr()->getWorkMode();
}

int64_t ThriftServiceImpl::getCurrentUserId()
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return INVALID_ID;
	}
	return defaultUserContext->id.id;
}

void ThriftServiceImpl::listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType)
{
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		return;
	}

	Path remotePath = userContext->getPathMgr()->makePath();
	remotePath.id(fileId);
	LIST_FOLDER_RESULT result;

	PageParam pageParam;
	pageParam.limit = 1000;
	OrderParam order;
	order.field	= SORT_FILED_TYPE;
	order.direction = SORT_DIRECTION_ASC;
	pageParam.orderList.push_back(order);
	order.field	= SORT_FILED_NAME;
	order.direction = SORT_DIRECTION_ASC;
	pageParam.orderList.push_back(order);

	int64_t count = 0 ;
	int32_t ret = userContext->getSyncFileSystemMgr()->listPage(remotePath, result, pageParam, count);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "list remote folder of %I64d failed.", fileId);
		return;
	}
	for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
	{
		File_Node fileNode;

		fileNode.id = it->id;
		fileNode.parent = it->parent;
		fileNode.name = Utility::String::wstring_to_utf8(it->name);
		fileNode.type = it->type;
		fileNode.size = it->size;
		fileNode.mtime = it->mtime;
		fileNode.ctime = it->ctime;
		fileNode.flags = it->flags;
		if (L"" == it->extraType)
		{
			fileNode.extraType = FOLDER;
		}
		else if (L"computer" == it->extraType)	
		{
            fileNode.extraType = COMPUTER;
		}
		else if (L"disk" == it->extraType)
		{
			fileNode.extraType = DISK;
		}

		_return.push_back(fileNode);
	}
}

void ThriftServiceImpl::listLocalDir(std::vector<File_Node> & _return, const std::string& path)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return;
	}

	Path localPath = defaultUserContext->getPathMgr()->makePath();
	localPath.path(Utility::String::utf8_to_wstring(path));
	LIST_FOLDER_RESULT result;

	int32_t ret = defaultUserContext->getSyncFileSystemMgr()->listFolder(localPath, result, ADAPTER_FOLDER_TYPE_LOCAL);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "list local folder of %s failed.", 
			Utility::String::wstring_to_string(localPath.path()).c_str());
		return;
	}
	for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
	{
		File_Node fileNode;

		fileNode.id = it->id;
		fileNode.parent = it->parent;
		fileNode.name = Utility::String::wstring_to_utf8(it->name);
		fileNode.type = it->type;
		fileNode.size = it->size;
		fileNode.mtime = it->mtime;
		fileNode.ctime = it->ctime;
		fileNode.flags = it->flags;

		_return.push_back(fileNode);
	}
}

void ThriftServiceImpl::GetPathName(std::string& _return, const int64_t fileId)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return;
	}
	std::wstring path = L"";
	defaultUserContext->getShareResMgr()->getFilePath(fileId, path);

	Path remotePath = defaultUserContext->getPathMgr()->makePath();
	remotePath.id(fileId);
	FILE_DIR_INFO fileNode;

	(void)defaultUserContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FOLDER_TYPE_REST);

	path += fileNode.name;

	_return = SD::Utility::String::wstring_to_utf8(path);
}

int32_t ThriftServiceImpl::upload(const std::string& localPath, const int64_t remoteParentId, const int64_t userId, const int32_t userType, const std::string& group)
{
	if (localPath.empty() || remoteParentId < 0 || userId <= 0)
	{
		return RT_INVALID_PARAM;
	}
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		return RT_INVALID_PARAM;
	}

	int32_t ret = uploadImpl(localPath, remoteParentId, userContext, ATT_Upload, group);

	if( RT_OK == ret )
	{
		NOTIFY_PARAM param;
		param.type = NOTIFY_MSG_TRANS_TASK_UPDATE_NUMBER;
		param.msg1 = L"1";
		userContext->getNotifyMgr()->notify(param);
	}

	return ret;
}

void ThriftServiceImpl::getTask(TransTask_RootNode& _return, const std::string& group)
{
	if (group.empty())
	{
		_return.group = "";
		return;
	}
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		_return.group = "";
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return;
	}

	AsyncTransRootNode rootNode;
	int32_t ret = defaultUserContext->getAsyncTaskMgr()->getTask(Utility::String::utf8_to_wstring(group), rootNode);
	if (RT_OK != ret)
	{
		_return.group = "";
		HSLOG_ERROR(MODULE_NAME, ret, "failed to get task.");
		return;
	}

	_return.group = group;	
	_return.source = Utility::String::wstring_to_utf8(rootNode->source);
	_return.parent = Utility::String::wstring_to_utf8(rootNode->parent);
	_return.name = Utility::String::wstring_to_utf8(rootNode->name);
	_return.type = rootNode->type;
	_return.fileType = rootNode->fileType;
	_return.status = rootNode->status;
	_return.statusEx = rootNode->statusEx;
	_return.userId = rootNode->userId;
	_return.userType = rootNode->userType;
	_return.userName = Utility::String::wstring_to_utf8(rootNode->userName);
	_return.priority = rootNode->priority;
	_return.size = rootNode->size;
	_return.transedSize = rootNode->transedSize;
	_return.errorCode = rootNode->errorCode;
}

int32_t ThriftServiceImpl::pauseTask(const std::string& group)
{
	if (group.empty())
	{
		return RT_INVALID_PARAM;
	}
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	return defaultUserContext->getAsyncTaskMgr()->pauseTask(Utility::String::utf8_to_wstring(group));
}

int32_t ThriftServiceImpl::delTask(const std::string& group)
{
	if (group.empty())
	{
		return RT_INVALID_PARAM;
	}
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	return defaultUserContext->getAsyncTaskMgr()->deleteTask(Utility::String::utf8_to_wstring(group));
}

int32_t ThriftServiceImpl::resumeTask(const std::string& group)
{
	if (group.empty())
	{
		return RT_INVALID_PARAM;
	}
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	return defaultUserContext->getAsyncTaskMgr()->resumeTask(Utility::String::utf8_to_wstring(group));
}

int32_t ThriftServiceImpl::isTaskExist(const std::string& group)
{
	if (group.empty())
	{
		return RT_INVALID_PARAM;
	}
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	AsyncTransRootNode rootNode;
	return defaultUserContext->getAsyncTaskMgr()->getTask(Utility::String::utf8_to_wstring(group), rootNode);
}

void ThriftServiceImpl::createShareLink(std::string& _return, const int64_t fileId)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		_return = "";
		return;
	}

	ShareLinkNode node;
	int32_t ret = defaultUserContext->getShareResMgr()->getShareLink(fileId, node);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "create share link of %I64d failed.", fileId);
		_return = "";
		return;
	}
	_return = node.url();
}

int32_t ThriftServiceImpl::addNotify(const int64_t handle)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	
	defaultUserContext->getNotifyMgr()->addNotify(handle);

	return RT_OK;
}

int32_t ThriftServiceImpl::removeNotify(const int64_t handle)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}

	defaultUserContext->getNotifyMgr()->removeNotify(handle);

	return RT_OK;
}

void ThriftServiceImpl::listTeamspace(std::vector<TeamSpace_Node> & _return)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return;
	}

	UserTeamSpaceNodeInfoArray  teamspaceListArray_;
	PageParam pageparam_;
	defaultUserContext->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray_,pageparam_);

	for (size_t i=0; i<teamspaceListArray_.size(); i++)
	{
		if("viewer" == teamspaceListArray_[i].role())
		{
			continue;
		}
		TeamSpace_Node teamspace_node;
		teamspace_node.id = teamspaceListArray_[i].teamId();
		teamspace_node.name = teamspaceListArray_[i].member_.name();
		_return.push_back(teamspace_node);
	}
}

int32_t ThriftServiceImpl::sendMessage(const int32_t type, const std::string& msg1, const std::string& msg2, const std::string& msg3, const std::string& msg4, const std::string& msg5)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	return defaultUserContext->getNotifyMgr()->notify(NOTIFY_PARAM(type, 
		Utility::String::utf8_to_wstring(msg1), 
		Utility::String::utf8_to_wstring(msg2), 
		Utility::String::utf8_to_wstring(msg3), 
		Utility::String::utf8_to_wstring(msg4), 
		Utility::String::utf8_to_wstring(msg5)));
}

void ThriftServiceImpl::getNewName(std::string& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& defaultName)
{
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		_return = "";
		return;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		_return = "";
		return;
	}

	Path remotePath = userContext->getPathMgr()->makePath();
	remotePath.parent(parentId);
	remotePath.name(Utility::String::utf8_to_wstring(defaultName));
	std::wstring newName = remotePath.name();
	int32_t ret = userContext->getSyncFileSystemMgr()->getNewName(remotePath, newName);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get new name of %I64d failed.", parentId);
		_return = "";
		return;
	}
	_return = Utility::String::wstring_to_utf8(newName);
}

void ThriftServiceImpl::createFolder(File_Node& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& name)
{
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		_return.id = INVALID_ID;
		return;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		_return.id = INVALID_ID;
		return;
	}

	Path remotePath = userContext->getPathMgr()->makePath();
	remotePath.id(parentId);
	remotePath.name(Utility::String::utf8_to_wstring(name));
	FILE_DIR_INFO fileInfo;

	int32_t ret = userContext->getSyncFileSystemMgr()->create(remotePath, remotePath.name(), fileInfo, ADAPTER_FOLDER_TYPE_REST, FOLDER_EXTRA_TYPE_NONE, true);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "create folder of %s failed.", 
			Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str());
		_return.id = INVALID_ID;
		return;
	}

	_return.id = fileInfo.id;
	_return.parent = fileInfo.parent;
	_return.name = Utility::String::wstring_to_utf8(fileInfo.name);
	_return.type = fileInfo.type;
	_return.size = fileInfo.size;
	_return.mtime = fileInfo.mtime;
	_return.ctime = fileInfo.ctime;
	_return.flags = fileInfo.flags;
	if (L""==fileInfo.extraType)
	{
		_return.extraType=0;
	}
	else if(L"computer"==fileInfo.extraType)
	{
      _return.extraType=1;
	}
}

void ThriftServiceImpl::createFolderNoSync(File_Node& _return, const int64_t userId, const int32_t userType, const int64_t parentId, const std::string& name, const int32_t extraType)
{
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		_return.id = INVALID_ID;
		return;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		_return.id = INVALID_ID;
		return;
	}

	Path remotePath = userContext->getPathMgr()->makePath();
	remotePath.id(parentId);
	remotePath.name(Utility::String::utf8_to_wstring(name));
	FILE_DIR_INFO fileInfo;
	int32_t ret=RT_OK;
	if (1==extraType)
	{
       ret = userContext->getSyncFileSystemMgr()->create(remotePath, remotePath.name(), fileInfo, ADAPTER_FOLDER_TYPE_REST, FOLDER_EXTRA_TYPE_COMPUTER, true);
	}
	else if(0==extraType)
	{
		ret = userContext->getSyncFileSystemMgr()->create(remotePath, remotePath.name(), fileInfo, ADAPTER_FOLDER_TYPE_REST, FOLDER_EXTRA_TYPE_NONE, true);
	}
	
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "create folder of %s failed.", 
			Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str());
		_return.id = INVALID_ID;
		return;
	}

	
	UserContext* userContext_ = defaultUserContext;
	if ( 0 == parentId )
	{
		MAKE_CLIENT(client);
		ret = client().setSyncStatus(userId, fileInfo.id, FILE_TYPE_DIR, false);
	}
	_return.id = fileInfo.id;
	_return.parent = fileInfo.parent;
	_return.name = Utility::String::wstring_to_utf8(fileInfo.name);
	_return.type = fileInfo.type;
	_return.size = fileInfo.size;
	_return.mtime = fileInfo.mtime;
	_return.ctime = fileInfo.ctime;
	_return.flags = fileInfo.flags;
	if (L""==fileInfo.extraType)
	{
		_return.extraType=0;
	}
	else if(L"computer"==fileInfo.extraType)
	{
		_return.extraType=1;
	}

	// ...
}

int32_t ThriftServiceImpl::renameFolder(const int64_t userId, const int32_t userType, const int64_t fileId, const std::string& name)
{
	UserContextId userContextId;
	userContextId.id = userId;
	userContextId.type = (UserContextType)userType;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if (NULL == defaultUserContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find default usercontext.");
		return RT_INVALID_PARAM;
	}
	userContextId.parent = defaultUserContext->id.id;
	UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
	if (NULL == userContext)
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "can not find usercontext of %I64d.", userId);
		return RT_INVALID_PARAM;
	}

	Path remotePath = userContext->getPathMgr()->makePath();
	remotePath.id(fileId);
	int32_t ret = userContext->getSyncFileSystemMgr()->rename(remotePath, Utility::String::utf8_to_wstring(name), ADAPTER_FOLDER_TYPE_REST);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "rename folder of %s failed.", 
			Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str());
		return ret;
	}
	return RT_OK;
}

int32_t ThriftServiceImpl::uploadOutlook(const std::string& localPath, const int64_t remoteParentId, const std::string& group)
{
	return uploadImpl(localPath, remoteParentId, 
		UserContextMgr::getInstance()->getDefaultUserContext(), ATT_Upload_Outlook, group);
}

int32_t ThriftServiceImpl::uploadOffice(const std::string& localPath, const int64_t remoteParentId, const std::string& group)
{
	return uploadImpl(localPath, remoteParentId, 
		UserContextMgr::getInstance()->getDefaultUserContext(), ATT_Upload_Office, group);
}

bool ThriftServiceImpl::needAddFullBackup(const std::string& strPath)
{
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	std::wstring str_path = SD::Utility::String::utf8_to_wstring(strPath);
	std::wstring parentPath = str_path.substr(0,2);
	while(true)
	{
		if(parentPath==str_path)
		{
			return true;
		}
		std::wstring::size_type nPos = str_path.find(L"\\", parentPath.length() + 1);
		if (std::wstring::npos == nPos)
		{
			parentPath = str_path;
		}
		else
		{
			parentPath = str_path.substr(0, nPos);
		}
		if (defaultUserContext->getSysConfigureMgr()->isBackupDisable(parentPath))
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "isBackupDisable path:%s", SD::Utility::String::wstring_to_string(parentPath).c_str());
			return false;
		}
		SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+parentPath).c_str(), 
			GENERIC_READ, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL, 
			OPEN_EXISTING, 
			FILE_ATTRIBUTE_NORMAL|FILE_FLAG_BACKUP_SEMANTICS, 
			NULL);
		if (INVALID_HANDLE_VALUE == hFile)
		{
			HSLOG_ERROR(MODULE_NAME, GetLastError(), "get id failed, open handle failed. path:%s", SD::Utility::String::wstring_to_string(parentPath).c_str());
			return false;
		}
		BY_HANDLE_FILE_INFORMATION bhfi;
		(void)memset_s(&bhfi, sizeof(BY_HANDLE_FILE_INFORMATION), 0, sizeof(BY_HANDLE_FILE_INFORMATION));
		if(GetFileInformationByHandle(hFile, &bhfi))
		{
			if(defaultUserContext->getSysConfigureMgr()->isBackupDisableAttr(bhfi.dwFileAttributes))
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "isBackupDisableAttr. path:%s", SD::Utility::String::wstring_to_string(parentPath).c_str());
				return false;
			}
		}
	}
	return true;
}

int32_t ThriftServiceImpl::addFullBackup(const std::string& strPath)
{
	std::wstring str_path = SD::Utility::String::utf8_to_wstring(strPath);
	std::set<std::wstring> _filterPathList;	
	std::set<std::wstring> _selectPathList;
	UserContext *defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	BackupAllMgr::getInstance(defaultUserContext)->getPathInfo(_selectPathList,_filterPathList);
	std::list<std::wstring> backupPathList;
	std::list<std::wstring> filterPathList;
	for(std::set<std::wstring>::iterator it = _selectPathList.begin(); it != _selectPathList.end(); ++it)
	{
		backupPathList.push_back(*it);
	}
	backupPathList.push_back(str_path);
	for(std::set<std::wstring>::iterator it = _filterPathList.begin(); it != _filterPathList.end(); ++it)
	{
		if(str_path==*it)
		{
			continue;
		}
		filterPathList.push_back(*it);
	}
	BATaskInfo* taskInfo = BackupAllMgr::getInstance(defaultUserContext)->getTaskInfo();

	std::wstring computerName;
	if (taskInfo->remotePath.empty())
	{
		if(iniLanguageHelper.GetLanguage() ==UI_LANGUGE::CHINESE)
		{
			computerName += defaultUserContext->getUserInfoMgr()->getHostName();
			computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
		}
		else
		{
			computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
			computerName += L" ";
			computerName += defaultUserContext->getUserInfoMgr()->getHostName();
		}
	}
	else
	{
		computerName = taskInfo->remotePath;
	}
	
	return BackupAllMgr::getInstance(defaultUserContext)->setBackupTask(backupPathList,filterPathList,BAT_REAL_TIME,L"",computerName);
}

class ThriftServiceInstance
{
public:
	int32_t startService()
	{
		std::wstring logFile = Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"OneboxMessageProxy.log";
		ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"), 
			TP_FILE, 
			Utility::String::wstring_to_string(logFile));

		workThread_ = boost::thread(boost::bind(&ThriftServiceInstance::startServiceImpl, this));

		return RT_OK;
	}

	int32_t stopService()
	{
		int32_t ret = RT_OK;
		try
		{
			if (NULL != server_)
			{
				HSLOG_EVENT(MODULE_NAME, RT_OK, "stop thrift server in.");
				server_->stop();
				HSLOG_EVENT(MODULE_NAME, RT_OK, "stop thrift server exit.");
			}
		}
		catch(TException& e)
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s", e.what());
			ret = RT_ERROR;
		}

		workThread_.join();

		ISSP_LogExit();

		return ret;
	}

private:
	int32_t startServiceImpl()
	{
		int32_t ret = RT_OK;
		RETRY(5)
		{
			uint16_t port = Utility::String::get_random_num(9999, 65535);
			ret = Utility::Registry::set(HKEY_CURRENT_USER, THRIFT_PORT_PATH, THRIFT_PORT_NAME, (int32_t)port);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "set port %d to regist failed.", port);
				SLEEP(boost::posix_time::seconds(1));
				continue;
			}

			try
			{
				boost::shared_ptr<ThriftServiceImpl> handler(new ThriftServiceImpl);
				boost::shared_ptr<TProcessor> processor(new ThriftServiceProcessor(handler));
				//boost::shared_ptr<TServerTransport> serverTransport(new TServerSocket(port));
				boost::shared_ptr<TServerTransport> serverTransport(new TServerSocket(Utility::String::format_string("127.0.0.1:%d", port).c_str()));
				boost::shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
				boost::shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());
				//TSimpleServer server(processor, serverTransport, transportFactory, protocolFactory);
				boost::shared_ptr<ThreadManager> threadManager = ThreadManager::newSimpleThreadManager(THRIFT_THREAD_NUM);
				boost::shared_ptr<ThreadFactory> threadFactory(new PlatformThreadFactory(false));
				threadManager->threadFactory(threadFactory);
				threadManager->start();
				server_.reset(new TThreadPoolServer(processor, serverTransport, transportFactory, protocolFactory, threadManager));
				server_->serve();
			}
			catch(TException& e)
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s", e.what());
				ret = RT_ERROR;
			}

			if (RT_OK == ret)
			{
				break;
			}
		}
		return ret;
	}

private:
	boost::shared_ptr<TServer> server_;
	boost::thread workThread_;
};

boost::shared_ptr<ThriftServiceInstance> instance(new ThriftServiceInstance);

int32_t startService()
{
	return instance->startService();
}

int32_t stopService()
{
	return instance->stopService();
}
