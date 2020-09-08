#include "SyncServiceImpl.h"
#include "SynchronizeMgr.h"
#include "DataBaseMgr.h"
#include "SyncFileSystemMgr.h"
#include "WorkModeMgr.h"
#include "UserInfoMgr.h"
#include "NetworkMgr.h"
#include "ConfigureMgr.h"
#include "AsyncTaskMgr.h"
#include "PathMgr.h"
#include "FilterMgr.h"
#include "NotifyMgr.h"
#include "Utility.h"
#include "ShareResMgr.h"
#include "TeamspaceMgr.h"
#include "OverlayIconMgr.h"

using namespace SD;

//#define CONSOLE_DEBUG

#ifndef MODULE_NAME
#define MODULE_NAME ("SyncService")
#endif

#define THRIFT_THREAD_NUM 2

bool SyncServiceImpl::isInit_ = false;
TThreadPoolServer* SyncServiceImpl::server_ = NULL;

int32_t SyncServiceImpl::startServiceImpl(uint16_t port)
{
	if (isInit_)
	{
		return RT_OK;
	}

	int32_t ret = RT_OK;
	isInit_ = true;
	try
	{
		boost::shared_ptr<SyncServiceImpl> handler(new SyncServiceImpl);
		boost::shared_ptr<TProcessor> processor(new SyncServiceProcessor(handler));
		boost::shared_ptr<TServerTransport> serverTransport(new TServerSocket(port));
		//boost::shared_ptr<TServerTransport> serverTransport(new TServerSocket(Utility::String::format_string("127.0.0.1:%d", port).c_str()));
		boost::shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
		boost::shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());
		//TSimpleServer server(processor, serverTransport, transportFactory, protocolFactory);
		boost::shared_ptr<ThreadManager> threadManager = ThreadManager::newSimpleThreadManager(THRIFT_THREAD_NUM);
		boost::shared_ptr<ThreadFactory> threadFactory(new BoostThreadFactory());
		threadManager->threadFactory(threadFactory);
		threadManager->start();
		server_ = new TThreadPoolServer(processor, serverTransport, transportFactory, protocolFactory, threadManager);
		server_->serve();
	}
	catch(TException& e)
	{
		HSLOG_ERROR(MODULE_NAME, RT_ERROR, "%s", e.what());
		ret = RT_ERROR;
	}
	isInit_ = false;

	if (NULL != server_)
	{
		delete server_;
		server_ = NULL;
	}	

	return ret;
}

int32_t SyncServiceImpl::startService()
{
	int32_t ret = RT_OK;	
	RETRY(5)
	{
		uint16_t port = Utility::String::get_random_num(9999, 65535);
		ret = Utility::Registry::set(HKEY_CLASSES_ROOT, THRIFT_PORT_PATH, THRIFT_PORT_NAME, (int32_t)port);
		if (RT_OK != ret)
		{
			return ret;
		}
		ret = startServiceImpl(port);
		if (RT_OK == ret)
		{
			break;
		}
		boost::this_thread::sleep(boost::posix_time::seconds(1));
	}
	return ret;
}

int32_t SyncServiceImpl::stopService()
{
	if (!isInit_)
	{
		return RT_OK;
	}
	try
	{
		server_->stop();
	}
	catch(...)
	{
		return RT_ERROR;
	}
	return RT_OK;
}

SyncServiceImpl::SyncServiceImpl()
	:userContext_(NULL)
{
#ifdef CONSOLE_DEBUG
	try
	{
		(void)initUserContext(0, Utility::String::wstring_to_string(Utility::FS::get_work_directory()+L"\\Config.ini"));
		assert(NULL != userContext_);

		if (RT_OK == userContext_->getUserInfoMgr()->domainAuthen())
		{
			userContext_->getWorkModeMgr()->changeWorkMode(WorkMode_Online);
		}
	}
	catch(...){}
#endif
}

SyncServiceImpl::~SyncServiceImpl()
{
	(void)releaseUserContext();
}

int32_t SyncServiceImpl::initUserContext(const int64_t uiHandle, const std::string& confPath)
{
	if (confPath.empty())
	{
		return RT_INVALID_PARAM;
	}
	if (RT_OK != RestClient::initialize())
	{
		HSLOG_ERROR(MODULE_NAME, RT_ERROR, "initial Rest SDK failed.");
		return RT_ERROR;
	}

	userContext_ = new UserContext((int64_t)uiHandle, Utility::String::utf8_to_wstring(confPath));

	return RT_OK;
}

int32_t SyncServiceImpl::releaseUserContext()
{
	if (userContext_)
	{
		delete userContext_;
		userContext_ = NULL;
	}

	RestClient::deinitialize();

	return RT_OK;
}

int32_t SyncServiceImpl::getServiceStatus()
{
	return userContext_->getWorkModeMgr()->getWorkMode();
}

int32_t SyncServiceImpl::sendMessage(const int32_t type, 
									 const std::string& msg1, 
									 const std::string& msg2, 
									 const std::string& msg3, 
									 const std::string& msg4, 
									 const std::string& msg5)
{
	return userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(type, 
		Utility::String::utf8_to_wstring(msg1), 
		Utility::String::utf8_to_wstring(msg2), 
		Utility::String::utf8_to_wstring(msg3), 
		Utility::String::utf8_to_wstring(msg4), 
		Utility::String::utf8_to_wstring(msg5)));
}

int32_t SyncServiceImpl::changeServiceWorkMode(const Service_Status::type status)
{
	return userContext_->getWorkModeMgr()->changeWorkMode((WorkMode)status);
}

int32_t SyncServiceImpl::uploadFile(const std::string& source, const std::string& target, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::downloadFile(const std::string& source, const std::string& target, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::deleteFile(const std::string& filePath, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::renameFile(const std::string& source, const std::string& target, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::moveFile(const std::string& source, const std::string& target, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::copyFile(const std::string& source, const std::string& target, const File_Type::type fileType)
{
	return RT_NOT_IMPLEMENT;
}

int32_t SyncServiceImpl::createDir(const std::string& dirPath)
{
	return RT_NOT_IMPLEMENT;
}

void SyncServiceImpl::listRemoteDir(std::vector<List_Info> & _return, const int64_t parent, const int64_t owner_id)
{
	_return.clear();

	if (0 > parent)
	{
		return;
	}

	Path remotePath = userContext_->getPathMgr()->makePath();
	remotePath.id(parent);
	remotePath.ownerId(owner_id);
	LIST_FOLDER_RESULT result;
	int32_t ret = userContext_->getSyncFileSystemMgr()->listFolder(remotePath, result, ADAPTER_FOLDER_TYPE_REST);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "list remote folder of %I64d failed.", parent);
		return;
	}

	for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
	{
		if (userContext_->getFilterMgr()->isStaticFilter(it->name))
		{
			continue;
		}
		if (FILE_TYPE_FILE == it->type)
		{
			continue;
		}
		List_Info listInfo;
		listInfo.id = it->id;
		listInfo.name = Utility::String::wstring_to_utf8(it->name);
		listInfo.type = it->type;
		listInfo.flags = it->flags;
		_return.push_back(listInfo);
	}
}

int64_t SyncServiceImpl::upload(const std::string& path, const int64_t parent, const int64_t owner_id)
{
	if (path.empty() || 0 > parent)
	{
		return RT_INVALID_PARAM;
	}
	std::wstring temp = Utility::String::utf8_to_wstring(path);
	temp = Utility::String::rtrim(temp, PATH_DELIMITER);
	if (temp.empty())
	{
		return RT_INVALID_PARAM;
	}
	return userContext_->getAsyncTaskMgr()->upload(temp, parent, owner_id, Utility::String::gen_uuid());
}

int32_t SyncServiceImpl::login(const int32_t type, const std::string& username, const std::string& password, const std::string& domain)
{
	int32_t ret = RT_OK;
	if (LoginTypeDomain == type)
	{
		ret = userContext_->getUserInfoMgr()->domainAuthen();
	}
	else if (LoginTypeManual == type)
	{
		ret = userContext_->getUserInfoMgr()->authen(Utility::String::utf8_to_wstring(username), 
			Utility::String::utf8_to_wstring(password));
	}

	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "user login failed.");
		return ret;
	}

	// update login type for auto login
	userContext_->getConfigureMgr()->getConfigure()->loginType(type);

	return RT_OK;
}

int32_t SyncServiceImpl::logout()
{
	return userContext_->getUserInfoMgr()->logout();
}

void SyncServiceImpl::encyptString(std::string& _return, const std::string& src)
{
	std::wstring temp = Utility::String::utf8_to_wstring(src);
	temp = Utility::String::encrypt_string(temp);
	_return = Utility::String::wstring_to_utf8(temp);
}

void SyncServiceImpl::decyptString(std::string& _return, const std::string& src)
{
	std::wstring temp = Utility::String::utf8_to_wstring(src);
	temp = Utility::String::decrypt_string(temp);
	_return = Utility::String::wstring_to_utf8(temp);
}

int32_t SyncServiceImpl::updateConfigure()
{
	return userContext_->getConfigureMgr()->unserialize();
}

void SyncServiceImpl::getTransSpeed(Trans_Speed_Info& _return)
{
	_return.upload = userContext_->getNetworkMgr()->getUploadSpeed();
	_return.download = userContext_->getNetworkMgr()->getDownloadSpeed();
}

int64_t SyncServiceImpl::getUserId()
{
	return userContext_->getUserInfoMgr()->getUserId();
}

void SyncServiceImpl::getUpdateInfo(Update_Info& _return)
{
	UpdateInfo updateInfo;
	MAKE_CLIENT(client);
	int32_t ret = client().getUpdateInfo(updateInfo);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get update info failed.");
	}
	_return.versionInfo = updateInfo.versionInfo;
	_return.downloadUrl = updateInfo.downloadUrl;
}

int32_t SyncServiceImpl::downloadClient(const std::string& downloadUrl, const std::string& location)
{
	if (downloadUrl.empty() || location.empty())
	{
		return RT_INVALID_PARAM;
	}
	
	std::wstring downloadUrl_, location_;
	downloadUrl_ = Utility::String::utf8_to_wstring(downloadUrl);
	location_ = Utility::String::utf8_to_wstring(location);

	MAKE_CLIENT(client);
	int32_t ret = client().downloadClient(Utility::String::wstring_to_utf8(downloadUrl_), Utility::String::wstring_to_utf8(location_));
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "download update client failed.");
	}
	return ret;
}

void SyncServiceImpl::getNodeInfo(Node_Info& _return, const std::string& path)
{
	// initial return value
	_return.remoteId = getRemoteId(path);
	_return.hasShareLink = false;
	if(NULL == userContext_)
	{
		return;	
	}
	if(INVALID_ID!=_return.remoteId)
	{
		_return.hasShareLink = userContext_->getShareResMgr()->hasShareLink(_return.remoteId);
	}
}

int32_t SyncServiceImpl::getOverlayIconStatus(const std::string& path)
{
	if(NULL == userContext_ || path.empty())
	{
		return OverlayIcon_Status::OverlayIcon_Status_Invalid;	
	}
	return userContext_->getOverlayIconMgr()->getOverlayIconStatus(Utility::String::utf8_to_wstring(path));
}

int64_t SyncServiceImpl::getRemoteId(const std::string& path)
{
	if(NULL == userContext_)
	{
		return INVALID_ID;	
	}
	std::wstring tempPath = Utility::String::utf8_to_wstring(path);
	// initial return value
	int64_t id = INVALID_ID;

	// convert path to id
	FILE_DIR_INFO fileDirInfo;
	Path localPath;
	localPath.path(tempPath);
	int32_t ret = userContext_->getSyncFileSystemMgr()->getProperty(localPath, fileDirInfo, ADAPTER_FILE_TYPE_LOCAL);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get local id of %s failed.", 
			Utility::String::wstring_to_string(localPath.path()).c_str());
		return id;
	}

	ret = userContext_->getDataBaseMgr()->getRelationTable()->getRemoteIdByLocalId(fileDirInfo.id, id);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get local node of %s failed.", 
			Utility::String::wstring_to_string(localPath.path()).c_str());
		return id;
	}

	return id;
}

void SyncServiceImpl::listError(std::vector<Error_Info> & _return, const int32_t offset, const int32_t limit)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "listError");
	_return.clear();
	ErrorNodes errorNodes;
	if(INVALID_VALUE==limit)
	{
		userContext_->getDataBaseMgr()->getDiffTable()->exportErrorNodes(errorNodes);
	}
	else
	{
		userContext_->getDataBaseMgr()->getDiffTable()->getErrorNodes(errorNodes, offset, limit);
	}

	for (ErrorNodes::iterator itL = errorNodes.begin(); itL != errorNodes.end(); ++itL)
	{
		Error_Info errorInfo;
		errorInfo.path = itL->get()->path;
		errorInfo.errorCode = itL->get()->errorCode;
		_return.push_back(errorInfo);
	}
}

void SyncServiceImpl::listDomainUsers(std::vector<Share_User_Info> & _return, const std::string& keyword)
{
	if (keyword.empty())
	{
		return;
	}

	ShareUserInfoList shareUserInfos;
	if (RT_OK != userContext_->getShareResMgr()->listDomainUsers(keyword, shareUserInfos))
	{
		return;
	}

	for (ShareUserInfoList::iterator it = shareUserInfos.begin(); it != shareUserInfos.end(); ++it)
	{
		Share_User_Info share_user_info;
		share_user_info.id = it->id();
		share_user_info.userName = it->name();
		share_user_info.loginName = it->loginName();
		share_user_info.email = it->email();
		share_user_info.department = it->department();
		share_user_info.right = Share_Right::Share_Right_R;

		_return.push_back(share_user_info);
	}
}

void SyncServiceImpl::listShareUsers(std::vector<Share_User_Info> & _return, const std::string& path)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return;
	}

	ShareNodeList shareNodes;
	int64_t id = getRemoteId(path);
	if (RT_OK != userContext_->getShareResMgr()->listShareMember(id, shareNodes))
	{
		return;
	}

	for (ShareNodeList::iterator it = shareNodes.begin(); it != shareNodes.end(); ++it)
	{
		Share_User_Info share_user_info;
		share_user_info.id = it->sharedUserId();
		share_user_info.userName = it->sharedUserName();
		share_user_info.loginName = it->sharedUserLoginName();
		share_user_info.department = it->sharedUserDescription();
		share_user_info.right = Share_Right::Share_Right_R;

		_return.push_back(share_user_info);
	}
}

int32_t SyncServiceImpl::setShareMember(const std::string& path, const std::vector<Share_User_Info> & shareUserInfos, const std::string& emailMsg)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}

	ShareNodeExList shareNodeExs;
	for (std::vector<Share_User_Info>::const_iterator it = shareUserInfos.begin(); it != shareUserInfos.end(); ++it)
	{
		ShareNodeEx shareNodeEx;
		shareNodeEx.sharedUserId(it->id);
		shareNodeEx.sharedEmail(it->email);
		shareNodeEx.loginName(it->loginName);
		shareNodeExs.push_back(shareNodeEx);
	}

	int64_t id = getRemoteId(path);
	return userContext_->getShareResMgr()->setShare(id, shareNodeExs, path, emailMsg);
}

int32_t SyncServiceImpl::delShareMember(const std::string& path, const Share_User_Info& shareUserInfo)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}

	ShareNodeEx shareNodeEx;
	shareNodeEx.sharedUserId(shareUserInfo.id);

	int64_t id = getRemoteId(path);
	return userContext_->getShareResMgr()->delShareMember(id, shareNodeEx);
}

int32_t SyncServiceImpl::cancelShare(const std::string& path)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}
	int64_t id = getRemoteId(path);
	return userContext_->getShareResMgr()->cancelShare(id);
}

void SyncServiceImpl::getShareLink(Share_Link_Info& _return, const std::string& path)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return;
	}

	int64_t id = getRemoteId(path);
	ShareLinkNode shareLinkNode;
	if (RT_OK != userContext_->getShareResMgr()->getShareLink(id, shareLinkNode))
	{
		return;
	}

	_return.url = shareLinkNode.url();
	_return.accessCode = shareLinkNode.plainAccessCode();
	_return.effectAt = shareLinkNode.effectiveAt();
	_return.expireAt = shareLinkNode.expireAt();
}

void SyncServiceImpl::modifyShareLink(Share_Link_Info& _return, const std::string& path, const Share_Link_Info& shareLinkInfo)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return;
	}

	ShareLinkNodeEx shareLinkNodeEx;
	shareLinkNodeEx.plainAccessCode(shareLinkInfo.accessCode);
	shareLinkNodeEx.effectiveAt(shareLinkInfo.effectAt);
	shareLinkNodeEx.expireAt(shareLinkInfo.expireAt);
	
	ShareLinkNode shareLinkNode;
	int64_t id = getRemoteId(path);
	if (RT_OK != userContext_->getShareResMgr()->modifyShareLink(id, shareLinkNodeEx, shareLinkNode))
	{
		return;
	}

	_return.url = shareLinkNode.url();
	_return.accessCode = shareLinkNode.plainAccessCode();
	_return.effectAt = shareLinkNode.effectiveAt();
	_return.expireAt = shareLinkNode.expireAt();
}

int32_t SyncServiceImpl::delShareLink(const std::string& path)
{
	std::wstring strPath = Utility::String::utf8_to_wstring(path);
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}
	int64_t id = getRemoteId(path);
	return userContext_->getShareResMgr()->delShareLink(id);
}

void SyncServiceImpl::getRandomString(std::string& _return)
{
	ServerSysConfig  sysConfig;
	if (RT_OK == userContext_->getShareResMgr()->getServerConfig(sysConfig) 
		&& sysConfig.complexCode())
	{
		_return = Utility::String::wstring_to_utf8(Utility::String::create_random_string());
		return ;
	}

	_return = "";
}

int32_t SyncServiceImpl::sendEmail(const std::string& type, const std::string& path, const Share_Link_Info& shareLinkInfo, const std::string& emailMsg, const std::vector<std::string>& mailto)
{
	if (path.empty())
	{
		return RT_INVALID_PARAM;
	}
	std::wstring fileName = Utility::FS::get_file_name(Utility::String::utf8_to_wstring(path));
	std::vector<std::string> it = mailto;
	ShareLinkNode shareLinkNodeEx;
	shareLinkNodeEx.url(shareLinkInfo.url);
	shareLinkNodeEx.plainAccessCode(shareLinkInfo.accessCode);
	shareLinkNodeEx.effectiveAt(shareLinkInfo.effectAt);
	shareLinkNodeEx.expireAt(shareLinkInfo.expireAt);

	EmailNode emailNode;
	emailNode.type = "link";
	emailNode.copyto = "";
	emailNode.email_param.message = emailMsg;
	emailNode.email_param.nodename = Utility::String::wstring_to_utf8(fileName);
	emailNode.email_param.sender = Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName());
	emailNode.email_param.plainaccesscode = shareLinkNodeEx.plainAccessCode();
	emailNode.email_param.start = shareLinkNodeEx.effectiveAt();
	emailNode.email_param.end = shareLinkNodeEx.expireAt();
	emailNode.email_param.linkurl = shareLinkNodeEx.url();
	
	bool failed = false;
	int32_t ret = RT_ERROR;
	for(std::vector<std::string>::const_iterator it = mailto.begin(); it != mailto.end(); ++it)
	{
		emailNode.mailto = *it;
		if(emailNode.mailto.empty())
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
			continue;
		}
		ret = userContext_->getShareResMgr()->sendEmail(emailNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to send email of %s.", 
				Utility::String::wstring_to_string(fileName).c_str());
			failed = true;
		}
	}

	return (failed ? RT_PART_FAILED : RT_OK);
}

void SyncServiceImpl::listBatchDomainUsers(std::map<std::string, Share_User_Info> & _return, const std::vector<std::string> & keyword)
{
	for (std::vector<std::string>::const_iterator it = keyword.begin(); it != keyword.end(); ++it)
	{
		if (it->empty())
		{
			continue;
		}

		//find the domain users
		ShareUserInfoList shareUserInfos;
		if (RT_OK != userContext_->getShareResMgr()->listDomainUsers(*it, shareUserInfos))
		{
			continue;
		}

		//check domain user is just only one
		if(1 == shareUserInfos.size())
		{
			ShareUserInfoList::iterator im = shareUserInfos.begin();

			Share_User_Info share_user_info;
			share_user_info.id = im->id();
			share_user_info.userName = im->name();
			share_user_info.loginName = im->loginName();
			share_user_info.email = im->email();
			share_user_info.department = im->department();
			share_user_info.right = Share_Right::Share_Right_R;

			_return.insert(std::pair<std::string,Share_User_Info>(*it, share_user_info));
		}
	}
}

int32_t SyncServiceImpl::uploadAttachements(const std::vector<std::string> & attachements, const std::string& parent, const std::string& taskGroupId)
{
	if (NULL == userContext_)
	{
		return RT_ERROR;
	}
	if (attachements.empty())
	{
		return RT_OK;
	}
	if (parent.empty() || taskGroupId.empty())
	{
		return RT_INVALID_PARAM;
	}
	int32_t ret = RT_ERROR;
	std::wstring localPath = L"";
	for (std::vector<std::string>::const_iterator it = attachements.begin(); it != attachements.end(); ++it)
	{
		localPath = Utility::String::utf8_to_wstring(*it);
		ret = userContext_->getAsyncTaskMgr()->upload(localPath, Utility::String::utf8_to_wstring(parent), Utility::String::utf8_to_wstring(taskGroupId));
		if (RT_OK != ret)
		{
			return ret;
		}
	}
	return RT_OK;
}

bool SyncServiceImpl::isAttachementsTransComplete(const std::string& taskGroupId)
{
	return (!userContext_->getDataBaseMgr()->getTransTaskTable()->isExist(Utility::String::utf8_to_wstring(taskGroupId)));
}

void SyncServiceImpl::getAttachementsLinks(std::map<std::string, std::string> & _return, const std::string& transGroupId)
{
	if (transGroupId.empty())
	{
		return;
	}
	AsyncTransTaskNodes nodes;
	int32_t ret = userContext_->getDataBaseMgr()->getTransTaskTable()->getNodes(Utility::String::utf8_to_wstring(transGroupId), nodes);
	if (RT_OK != ret)
	{
		return;
	}
	int64_t id = INVALID_ID;
	for (AsyncTransTaskNodes::iterator it = nodes.begin(); it != nodes.end(); ++it)
	{
		id = Utility::String::string_to_type<int64_t>(it->userDefine.substr(it->userDefine.find_last_of(L'/')+1));
		if (INVALID_ID == id || 0 >= id)
		{
			_return.clear();
			return;
		}
		// create share link
		ShareLinkNode node;
		ret = userContext_->getShareResMgr()->getShareLink(id, node);
		if (RT_OK != ret)
		{
			_return.clear();
			return;
		}
		_return[Utility::String::wstring_to_utf8(it->id.id)] = node.url();
	}
}

int32_t SyncServiceImpl::deleteTransTasksByGroupId(const std::string& transGroupId)
{
	return userContext_->getDataBaseMgr()->getTransTaskTable()->deleteNodes(Utility::String::utf8_to_wstring(transGroupId));
}

int32_t SyncServiceImpl::showTransTask(const std::string& transGroupId)
{
	return userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SHOW_TRANSTASKS));
}

void SyncServiceImpl::listTeamspacesByUser(std::vector<Teamspace_Membership> & _return, const int64_t userId)
{
	int64_t totalCount = 0, offset = 0, curCount = 0;
	int32_t ret = RT_ERROR;
	do 
	{
		TeamspaceMemberships teamspaceMemberships;
		ret = userContext_->getTeamspaceMgr()->listTeamspacesByUser(teamspaceMemberships, totalCount, userId, offset);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "faile to list teamspace by user.");
			return;
		}
		curCount += teamspaceMemberships.size();
		for (TeamspaceMemberships::const_iterator it = teamspaceMemberships.begin(); it != teamspaceMemberships.end(); ++it)
		{
			Teamspace_Membership membership;
			membership.id = it->id();
			membership.teamRole = it->teamRole();
			membership.role = it->role();
			
			membership.teamspace.id = it->teamspace().id();
			membership.teamspace.name = it->teamspace().name();
			membership.teamspace.description = it->teamspace().description();
			membership.teamspace.curNumbers = it->teamspace().curNumbers();
			membership.teamspace.createdAt = it->teamspace().createdAt();
			membership.teamspace.createdBy = it->teamspace().createdBy();
			membership.teamspace.createdByUserName = it->teamspace().createdByUserName();
			membership.teamspace.ownedBy = it->teamspace().ownedBy();
			membership.teamspace.ownedByUserName = it->teamspace().ownedByUserName();
			membership.teamspace.status = it->teamspace().status();
			membership.teamspace.spaceQuota = it->teamspace().spaceQuota();
			membership.teamspace.spaceUsed = it->teamspace().spaceUsed();
			membership.teamspace.maxVersions = it->teamspace().maxVersions();
			membership.teamspace.maxMembers = it->teamspace().maxMembers();
			membership.teamspace.regionId = it->teamspace().regionId();

			membership.member.id = it->teamspaceMember().id();
			membership.member.type = it->teamspaceMember().type();
			membership.member.name = it->teamspaceMember().name();
			membership.member.loginName = it->teamspaceMember().loginName();
			membership.member.description = it->teamspaceMember().description();

			_return.push_back(membership);
		}
	} while (totalCount == curCount);
}
