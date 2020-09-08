#include "UserInfoMgrImpl.h"
#include "Utility.h"
#include "UserInfo.h"

using namespace SD;

UserInfoMgr* UserInfoMgr::create(UserContext* userContext)
{
	return static_cast<UserInfoMgr*>(new UserInfoMgrImpl(userContext));
}

UserInfoMgrImpl::UserInfoMgrImpl(UserContext* userContext)
	:userContext_(userContext)
{
	userId_ = 0;
}

UserInfoMgrImpl::~UserInfoMgrImpl()
{
	if (userContext_->id.type == UserContext_User)
	{
		(void)logout();
	}
}

int32_t UserInfoMgrImpl::authen(const std::wstring& account, const std::wstring& password)
{
	boost::mutex::scoped_lock lock(mutex_);
	if (account.empty() || password.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = getDeviceInfo();
	if (RT_OK != ret)
	{
		return ret;
	}

	std::wstring domain;
	std::wstring username;
	std::vector<std::wstring> accountInfo;  
	std::wstring temp = Utility::String::replace_all(account, L"/",  L"\\");

	Utility::String::split(temp, accountInfo, L"\\");
	if(accountInfo.size() == 2)
	{
		domain = accountInfo[0];
		username = accountInfo[1];
	}
	else
	{
		username = account;
	}

	MAKE_CLIENT(client);
	LoginRespInfo info;
	ret = client().login(Utility::String::wstring_to_utf8(domain), 
		Utility::String::wstring_to_utf8(username), 
		Utility::String::wstring_to_utf8(password), info, 
		Utility::String::wstring_to_utf8(deviceId_), 
		Utility::String::wstring_to_utf8(osVersion_), 
		Utility::String::wstring_to_utf8(hostName_), 
		Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->version()));
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "login failed.");
		return ret;
	}

	if (!info.isValid())
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "authen response information is invalid.");
		return RT_INVALID_PARAM;
	}

	account_ = account;

	ret = parseAuthenRsp(info);
	if (RT_OK != ret)
	{
		return ret;
	}

	userContext_->getConfigureMgr()->getConfigure()->password(Utility::String::encrypt_string(password));
	userContext_->getConfigureMgr()->getConfigure()->loginType(LoginTypeManual);
	//userName_ = account_;
	// save the user configure
	return userContext_->getConfigureMgr()->serialize();
}

int32_t UserInfoMgrImpl::autoAuthen()
{
	if (LoginTypeDomain == userContext_->getConfigureMgr()->getConfigure()->loginType())
	{
		return domainAuthen();
	}
	return authen(getAccount(), Utility::String::decrypt_string(userContext_->getConfigureMgr()->getConfigure()->password()));
}

int32_t UserInfoMgrImpl::logout()
{
	boost::mutex::scoped_lock lock(mutex_);
	MAKE_CLIENT(client);
	int32_t ret = client().logout();
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "login out failed.");
	}
	return ret;
}

std::wstring UserInfoMgrImpl::getAccount()
{
	boost::mutex::scoped_lock lock(mutex_);
	return account_;
}

int64_t UserInfoMgrImpl::getUserId()
{
	boost::mutex::scoped_lock lock(mutex_);
	return userId_;
}

std::wstring UserInfoMgrImpl::getUserName()
{
	boost::mutex::scoped_lock lock(mutex_);
	return userName_;
}

void UserInfoMgrImpl::setUserId(const int64_t userId)
{
	boost::mutex::scoped_lock lock(mutex_);
	userId_ = userId;
}

void UserInfoMgrImpl::setUserName(const std::wstring& userName)
{
	boost::mutex::scoped_lock lock(mutex_);
	userName_ = userName;
}

int32_t UserInfoMgrImpl::parseAuthenRsp(LoginRespInfo& authenRsp)
{
	if (authenRsp.token.empty() 
		|| authenRsp.refreshToken.empty())
	{
		HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
			"authen failed, invalid userId or token.");
		return RT_INVALID_PARAM;
	}

	// set token
	CredentialInfo credInfo;
	credInfo.token = authenRsp.token;
	//credInfo.type = authenRsp.token_type;
	// server interval is seconds and local interval is milliseconds
	credInfo.period = authenRsp.expiredAt / 60;
	userContext_->getCredentialMgr()->setCredentialInfo(credInfo);

	CredentialInfo refreshCredInfo;
	refreshCredInfo.token = authenRsp.refreshToken;
	//refreshCredInfo.type = authenRsp.token_type;
	refreshCredInfo.period = authenRsp.expiredAt / 60;
	userContext_->getCredentialMgr()->setRefreshCredentialInfo(refreshCredInfo);

	// set user information
	userId_ = authenRsp.cloud_userid;
	userName_ = Utility::String::utf8_to_wstring(authenRsp.login_name);

	// initial user context id information
	userContext_->id.id = userId_;
	userContext_->id.parent = 0;
	userContext_->id.type = UserContext_User;
	userContext_->id.name = L"";

	// set the user configure path
	ConfigureMgr* configureMgr = userContext_->getConfigureMgr();
	configureMgr->updateUserConfigurePath(userId_, userId_);
	configureMgr->unserialize();

	// update the configure
	configureMgr->getConfigure()->userName(account_);
	configureMgr->getConfigure()->deviceName(hostName_);

	SpeedLimitConf remoteSpeedLimitConf, localSpeedLimitConf, speedLimitConf;
	localSpeedLimitConf = configureMgr->getConfigure()->localSpeedLimitConf();
	remoteSpeedLimitConf.maxUploadSpeed = authenRsp.maxUploadSpeed * 1024;
	remoteSpeedLimitConf.maxDownloadSpeed = authenRsp.maxDownloadSpeed *1024;
	remoteSpeedLimitConf.useSpeedLimit = (remoteSpeedLimitConf.maxUploadSpeed>0 || remoteSpeedLimitConf.maxDownloadSpeed>0);	
	
	speedLimitConf = localSpeedLimitConf;
	// use the minimum as the speed limit value
	speedLimitConf.useSpeedLimit = (localSpeedLimitConf.useSpeedLimit || remoteSpeedLimitConf.useSpeedLimit);
	if (remoteSpeedLimitConf.maxUploadSpeed > 0)
	{
		speedLimitConf.maxUploadSpeed = remoteSpeedLimitConf.maxUploadSpeed;
		if (localSpeedLimitConf.maxUploadSpeed > 0 && 
			localSpeedLimitConf.maxUploadSpeed < remoteSpeedLimitConf.maxUploadSpeed)
		{
			speedLimitConf.maxUploadSpeed = localSpeedLimitConf.maxUploadSpeed;
		}
	}
	if (remoteSpeedLimitConf.maxDownloadSpeed > 0)
	{
		speedLimitConf.maxDownloadSpeed = remoteSpeedLimitConf.maxDownloadSpeed;
		if (localSpeedLimitConf.maxDownloadSpeed > 0 && 
			localSpeedLimitConf.maxDownloadSpeed < remoteSpeedLimitConf.maxDownloadSpeed)
		{
			speedLimitConf.maxDownloadSpeed = localSpeedLimitConf.maxDownloadSpeed;
		}
	}
	configureMgr->getConfigure()->remoteSpeedLimitConf(remoteSpeedLimitConf);
	configureMgr->getConfigure()->speedLimitConf(speedLimitConf);

	lastAccessTerminal_ = authenRsp.lastAccessTerminal;

	configureMgr->serialize();

	if (authenRsp.declarationInfo.needsigndeclare)
	{
		MAKE_CLIENT(client);
		client().getDeclaration("pc",declarationInfo_);
		declarationInfo_.needsigndeclare = authenRsp.declarationInfo.needsigndeclare;
	}

	if (authenRsp.needChangePassword)
	{
		HSLOG_EVENT(MODULE_NAME, CHANGE_PASSWORD, "user should change the password.");
		return CHANGE_PASSWORD;
	}
	networkType_ = authenRsp.networkType;
	return RT_OK;
}

int32_t UserInfoMgrImpl::getCurUserInfo(StorageUserInfo& storageUserInfo)
{
	MAKE_CLIENT(client);
	int32_t ret = client().getCurUserInfo(storageUserInfo);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "getCurUserInfo failed.");
	}
	return ret;
}


DeclarationInfo UserInfoMgrImpl::getDeclarationInfo()
{
	boost::mutex::scoped_lock lock(mutex_);
	return declarationInfo_;
}

LastAccessTerminal UserInfoMgrImpl::getLastAccessTerminal()
{
	boost::mutex::scoped_lock lock(mutex_);
	return lastAccessTerminal_;
 }

std::wstring UserInfoMgrImpl::getHostName()
{
	boost::mutex::scoped_lock lock(mutex_);
	return hostName_;
}

int32_t UserInfoMgrImpl::getNetworkType()
{
	boost::mutex::scoped_lock lock(mutex_);
	return networkType_;
}

