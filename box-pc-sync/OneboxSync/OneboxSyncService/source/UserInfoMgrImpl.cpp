#include "UserInfoMgrImpl.h"

UserInfoMgr* UserInfoMgr::create(UserContext* userContext)
{
	return static_cast<UserInfoMgr*>(new UserInfoMgrImpl(userContext));
}

UserInfoMgrImpl::UserInfoMgrImpl(UserContext* userContext)
	:userContext_(userContext)
{

}

UserInfoMgrImpl::~UserInfoMgrImpl()
{
	(void)logout();
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

	std::wstring userName = account, domain = L"";
	std::wstring::size_type pos = account.find(L'\\');
	if (pos != std::wstring::npos)
	{
		domain = account.substr(0, pos);
		userName = account.substr(pos + 1);
	}

	MAKE_CLIENT(client);
	LoginRespInfo info;
	ret = client().login(Utility::String::wstring_to_utf8(userName), 
		Utility::String::wstring_to_utf8(password), 
		Utility::String::wstring_to_utf8(domain), 
		info, 
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
	userContext_->getConfigureMgr()->getConfigure()->password(password);
	//userName_ = account_;

	return parseAuthenRsp(info);
}

int32_t UserInfoMgrImpl::autoAuthen()
{
	if (LoginTypeDomain == userContext_->getConfigureMgr()->getConfigure()->loginType())
	{
		return domainAuthen();
	}
	return authen(getAccount(), userContext_->getConfigureMgr()->getConfigure()->password());
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

int32_t UserInfoMgrImpl::listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray)
{
	MAKE_CLIENT(client);
	int32_t ret = client().listRegionIdInfo(regionIdInfoArray);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "getCurUserInfo failed.");
	}
	return ret;
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

	// update the configure file
	userContext_->getConfigureMgr()->getConfigure()->userName(account_);
	userContext_->getConfigureMgr()->getConfigure()->deviceName(hostName_);
	SpeedLimitConf speedLimitConf;
	speedLimitConf.useSpeedLimit = (authenRsp.maxUploadSpeed>0 || authenRsp.maxDownloadSpeed>0);
	speedLimitConf.maxUploadSpeed = authenRsp.maxUploadSpeed*1024;
	speedLimitConf.maxDownloadSpeed = authenRsp.maxDownloadSpeed*1024;
	userContext_->getConfigureMgr()->getConfigure()->speedLimitConf(speedLimitConf);

	userContext_->getConfigureMgr()->serialize();

	return RT_OK;
}
