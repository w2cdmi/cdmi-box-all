#include "ShareResMgr.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ShareResMgr")
#endif

class ShareResMgrImpl : public ShareResMgr
{
public:
	ShareResMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual int32_t setShare(const int64_t& id, ShareNodeExList& shareNodeExs, const std::string& path, const std::string& emailMsg)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		std::wstring strPath = SD::Utility::String::utf8_to_wstring(path);
		FILE_TYPE type = SD::Utility::FS::is_directory(strPath)?FILE_TYPE_DIR:FILE_TYPE_FILE;
		std::wstring fileName = SD::Utility::FS::get_file_name(strPath);
		int32_t ret = RT_OK;
		
		for(ShareNodeExList::iterator it = shareNodeExs.begin(); it != shareNodeExs.end(); ++it)
		{
			if(INVALID_ID==it->sharedUserId())
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "invalid sharedUserId.");
				return RT_ERROR;
			}
			
			if(0==it->sharedUserId())
			{
				int64_t userId = INVALID_ID;
				MAKE_CLIENT(client);
				ret = client().createLdapUser(it->loginName(), userId);
				if(RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "createLdapUser loginName:%s error.", it->loginName().c_str());
					return ret;
				}
				it->sharedUserId(userId);
			}
			MAKE_CLIENT(client);
			ret = client().setShareRes(userContext_->getUserInfoMgr()->getUserId(), id, *it);
			if(RT_OK == ret)
			{
				EmailNode emailnode;
				emailnode.type = "share";
				emailnode.mailto = it->sharedEmail();
				emailnode.email_param.message = emailMsg;
				emailnode.email_param.nodename = SD::Utility::String::wstring_to_utf8(fileName);
				emailnode.email_param.sender = SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName());
				emailnode.email_param.type = type;
				emailnode.email_param.ownerid = userContext_->getUserInfoMgr()->getUserId();
				emailnode.email_param.nodeid = id;
				if(emailnode.mailto.empty())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
				}
				else
				{
					sendEmail(emailnode);
				}
			}
			else
			{
				return ret;
			}
		}

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "setShareRes of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t listShareMember(const int64_t& id, ShareNodeList& shareNodes)
	{
		int32_t ret = RT_OK;
		int32_t nextOffset = 0;
		int32_t offset = 0;
		int32_t limit = 100;

		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		do
		{		
			MAKE_CLIENT(client);
			ret = client().listShareRes(userContext_->getUserInfoMgr()->getUserId(), id, shareNodes, nextOffset, offset, limit);

			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "setShareRes of %I64d failed.", id);
				break;
			}
		} while(nextOffset);

		return ret;
	}

	virtual int32_t delShareMember(const int64_t& id, const ShareNodeEx& shareNodeEx)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().delShareResOwner(userContext_->getUserInfoMgr()->getUserId(), id, shareNodeEx);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "delShareResOwner of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t cancelShare(const int64_t& id)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		ShareNodeEx shareNodeEx;
		shareNodeEx.sharedUserId(INVALID_ID);
		int32_t ret = client().delShareResOwner(userContext_->getUserInfoMgr()->getUserId(), id, shareNodeEx);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "delShareResOwner of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos)
	{
		if (strKey.empty())
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().listDomainUsers(strKey, shareUserInfos);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listDomainUsers of failed.");
		}

		return ret;
	}

	virtual int32_t getShareLink(const int64_t& id, ShareLinkNode& shareLinkNode)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().getShareLink(id, userContext_->getUserInfoMgr()->getUserId(), shareLinkNode);
		if(HTTP_NOT_FOUND == ret)
		{
			ret = client().addShareLink(id, userContext_->getUserInfoMgr()->getUserId(), shareLinkNode);
		}

		std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getShareLink of %I64d failed.", id);
		}

		return ret;
	}

	virtual bool hasShareLink(const int64_t& id)
	{
		ShareLinkNode shareLinkNode;
		MAKE_CLIENT(client);
		int32_t ret = client().getShareLink(id, userContext_->getUserInfoMgr()->getUserId(), shareLinkNode);
		if(RT_OK == ret)
		{
			return true;
		}
		return false;
	}

	virtual int32_t modifyShareLink(const int64_t& id, const ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().modifyShareLink(id, userContext_->getUserInfoMgr()->getUserId(), shareLinkNodeEx, shareLinkNode);
		std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "modifyShareLink of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t delShareLink(const int64_t& id)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().delShareLink(id, userContext_->getUserInfoMgr()->getUserId());
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "delShareLink of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t getServerConfig(ServerSysConfig& serverSysConfig)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().getServerSysConfig(serverSysConfig);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "GetServerConfig");
		}

		return ret;
	}

	virtual int32_t sendEmail(const EmailNode& emailNode)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().sendEmail(emailNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "send email to %s failed", emailNode.mailto.c_str());
		}

		return ret;
	}

private:
	UserContext* userContext_;
};

ShareResMgr* ShareResMgr::create(UserContext* userContext)
{
	return static_cast<ShareResMgr*>(new ShareResMgrImpl(userContext));
}
