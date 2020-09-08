#include "ShareResMgr.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"
#include "NotifyMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("ShareResMgr")
#endif

class ShareResMgrImpl : public ShareResMgr
{
public:
	ShareResMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual int32_t listReceiveShareRes(const std::string& keyword, ShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		ret = client().listReceiveShareRes(keyword, pageParam, count, shareNodes);

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listReceiveShareRes of %s failed.", keyword.c_str());
		}

		return ret;
	}

    virtual int32_t listMyShareRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count)
    {
        int32_t ret = RT_OK;

        MAKE_CLIENT(client);
        ret = client().listDistributeShareRes(keyword, pageParam, count, shareNodes);
		if (RT_OK != ret)
        {
            HSLOG_ERROR(MODULE_NAME, ret, "listDistributeShareRes of %s failed.", keyword.c_str());
        }
        return ret;
    }

	virtual int32_t listMyLinkRes(const std::string& keyword, MyShareNodeList& shareNodes, const PageParam& pageParam, int64_t& count)
    {
        int32_t ret = RT_OK;

        MAKE_CLIENT(client);
		ret = client().listFilesHadShareLink(userContext_->getUserInfoMgr()->getUserId(), keyword, pageParam, count, shareNodes);
        if (RT_OK != ret)
        {
            HSLOG_ERROR(MODULE_NAME, ret, "listFilesHadShareLink of %s failed.", keyword.c_str());
        }
        return ret;
    }

	virtual int32_t setShare(int64_t id, ShareNodeExList& shareNodeExs, const std::string& path, const std::string& emailMsg)
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
			if(-1==it->sharedUserId())
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

			{
				MAKE_CLIENT(client);
				ret = client().setShareRes(userContext_->getUserInfoMgr()->getUserId(), id, *it);
			}
			if(RT_OK == ret)
			{
				EmailNode emailnode;
				emailnode.type = "share";
				emailnode.mailto = it->sharedEmail();
				emailnode.email_param.message = emailMsg;
				emailnode.email_param.nodename = SD::Utility::String::wstring_to_utf8(fileName);
				emailnode.email_param.sender = SD::Utility::String::wstring_to_string(userContext_->getUserInfoMgr()->getUserName());
				emailnode.email_param.type = type;
				emailnode.email_param.ownerid = userContext_->getUserInfoMgr()->getUserId();
				emailnode.email_param.nodeid = id;
				if(emailnode.mailto.empty())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
				}
				else
				{
					MAKE_CLIENT(client);
					client().sendEmail(emailnode);
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

	virtual int32_t listShareMember(int64_t id, ShareNodeList& shareNodes)
	{
		int32_t ret = RT_OK;
		int64_t nextOffset = 0;
		int64_t offset = 0;
		int32_t limit = 100;

		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		do
		{		
			MAKE_CLIENT(client);
			offset = nextOffset;
			ret = client().listShareRes(userContext_->getUserInfoMgr()->getUserId(), id, shareNodes, nextOffset, offset, limit);

			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "setShareRes of %I64d failed.", id);
				break;
			}
		} while(nextOffset);

		return ret;
	}

	virtual int32_t delShareMember(int64_t id, ShareNodeEx& shareNodeEx)
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

	virtual int32_t cancelShare(int64_t id)
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

	virtual int32_t exitShare(int64_t ownerId, int64_t id)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		ShareNodeEx shareNodeEx;
		shareNodeEx.sharedUserId(userContext_->getUserInfoMgr()->getUserId());
		int32_t ret = client().delShareResOwner(ownerId, id, shareNodeEx);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "exitShare of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t listDomainUsers(const std::string& strKey, ShareUserInfoList& shareUserInfos, int32_t limit)
	{
		if (strKey.empty())
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().listDomainUsers(strKey, shareUserInfos, limit);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listDomainUsers of failed.");
		}

		return ret;
	}

	virtual int32_t getShareLink(int64_t id, ShareLinkNode& shareLinkNode)
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

	virtual int32_t getShareLink(int64_t id, std::string& linkCode, ShareLinkNode& shareLinkNode)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().getShareLink(id, userContext_->getUserInfoMgr()->getUserId(), linkCode, shareLinkNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getShareLink of %I64d failed.", id);
			return ret;
		}
		std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());
		HSLOG_TRACE(MODULE_NAME, ret, "getShareLink time %I64d-%I64d", shareLinkNode.effectiveAt(), shareLinkNode.expireAt());
		return ret;
	}

	virtual int32_t addShareLink(int64_t id, std::string& accessMode, ShareLinkNode& shareLinkNode)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		MAKE_CLIENT(client);
		int32_t ret = client().addShareLink(id, userContext_->getUserInfoMgr()->getUserId(), accessMode, shareLinkNode);
		std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getShareLink of %I64d failed.", id);
		}
		return ret;
	}

	virtual int32_t listShareLinkByFile(int64_t id, int64_t count, ShareLinkNodeList& shareLinkNodes)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().listShareLinkByFile(id, userContext_->getUserInfoMgr()->getUserId(), count, shareLinkNodes);
		//if(HTTP_NOT_FOUND == ret)
		//{
		//	ret = client().addShareLink(id, userContext_->getUserInfoMgr()->getUserId(), shareLinkNode);
		//}

		//std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		//std::string::size_type pos = url.find("/api");
		//shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listShareLinkByFile of %I64d failed.", id);
		}

		return ret;
	}

	virtual bool hasShareLink(int64_t id)
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

	virtual int32_t modifyShareLink(int64_t id, ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
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

	virtual int32_t delShareLink(int64_t id)
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

	virtual bool hasShareLinkV2(int64_t id)
	{
		int64_t count = 0;
		ShareLinkNodeList shareLinkNodes;
		MAKE_CLIENT(client);
		int32_t ret = client().listShareLinkByFile(id, userContext_->getUserInfoMgr()->getUserId(), count, shareLinkNodes);
		if(0 != count)
		{
			return true;
		}
		return false;
	}

	virtual int32_t modifyShareLink(int64_t id, std::string& linkCode, ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().modifyShareLink(id, userContext_->getUserInfoMgr()->getUserId(), linkCode, shareLinkNodeEx, shareLinkNode);
		std::string url = SD::Utility::String::wstring_to_utf8(userContext_->getConfigureMgr()->getConfigure()->serverUrl());
		std::string::size_type pos = url.find("/api");
		shareLinkNode.url(url.substr(0, pos) + "/p/" + shareLinkNode.id());

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "modifyShareLink of %I64d failed.", id);
		}

		return ret;
	}

	virtual int32_t delShareLink(int64_t id, std::string& linkCode, std::string& type)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().delShareLink(id, userContext_->getUserInfoMgr()->getUserId(), linkCode, type);
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

	virtual int32_t sendEmail(EmailNode& emailNode)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().sendEmail(emailNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "send email to %s failed", emailNode.mailto.c_str());
		}

		return ret;
	}

	virtual int32_t setShare(int64_t id, ShareNodeExList& shareNodeExs, int32_t type, std::wstring fileName, const std::string& emailMsg)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		int32_t ret = RT_OK;

		for(ShareNodeExList::iterator it = shareNodeExs.begin(); it != shareNodeExs.end(); ++it)
		{
			if(-1==it->sharedUserId())
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
				emailnode.email_param.sender = SD::Utility::String::wstring_to_string(userContext_->getUserInfoMgr()->getUserName());
				emailnode.email_param.type = type;
				emailnode.email_param.ownerid = userContext_->getUserInfoMgr()->getUserId();
				emailnode.email_param.nodeid = id;
				if(emailnode.mailto.empty())
				{
					HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
				}
				else
				{
					MAKE_CLIENT(client);
					if(!client().sendEmail(emailnode))
					{
						EmailInfoNode emailInfoNode;
						emailInfoNode.sender = userContext_->getUserInfoMgr()->getUserId();
						emailInfoNode.source = "share";
						//emailInfoNode.ownedBy;
						emailInfoNode.subject = "";
						emailInfoNode.message = emailMsg;
						emailInfoNode.nodeId = id;
						setMailInfo(id, emailInfoNode);
					}
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

	int32_t setShareV2Wrapprt(int64_t id, ShareNodeExList& shareNodeExs, int32_t type, std::wstring fileName, const std::string& emailMsg)
	{
		if (INVALID_ID == id)
		{
			return RT_INVALID_PARAM;
		}
		int32_t ret = RT_OK;

		for(ShareNodeExList::iterator it = shareNodeExs.begin(); it != shareNodeExs.end(); ++it)
		{
			if(-1==it->sharedUserId())
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
			ret = client().setShareResV2(userContext_->getUserInfoMgr()->getUserId(), id, *it);
			if(RT_OK == ret)
			{
				EmailNode emailnode;
				emailnode.type = "share";
				emailnode.email_param.message = emailMsg;
				emailnode.email_param.nodename = SD::Utility::String::wstring_to_utf8(fileName);
				emailnode.email_param.sender = SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName());
				emailnode.email_param.type = type;
				emailnode.email_param.ownerid = userContext_->getUserInfoMgr()->getUserId();
				emailnode.email_param.nodeid = id;
				if(it->sharedUserType() == "user")
				{
					emailnode.mailto = it->sharedEmail();
					if(emailnode.mailto.empty())
					{
						HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
					}
					else
					{
						if(!sendEmail(emailnode))
						{
							EmailInfoNode emailInfoNode;
							emailInfoNode.sender = userContext_->getUserInfoMgr()->getUserId();
							emailInfoNode.source = "share";
							//emailInfoNode.ownedBy;
							emailInfoNode.subject = "";
							emailInfoNode.message = emailMsg;
							emailInfoNode.nodeId = id;
							setMailInfo(id, emailInfoNode);
						}
					}
				}
				else
				{
					int64_t count = 0;
					PageParam pageParam;
					UserGroupNodeInfoArray groupMembers;
					MAKE_CLIENT(client);
					if(client().getGroupListMemberInfo(it->sharedUserId(), "", "all", pageParam, count, groupMembers))
					{
						HSLOG_ERROR(MODULE_NAME, ret, "getGroupListMemberInfo of %I64d failed.", it->sharedUserId());
						return RT_OK;
					}
					for(UserGroupNodeInfoArray::iterator im = groupMembers.begin(); im != groupMembers.end(); ++im)
					{
						ShareUserInfoList shareUserInfos;
						std::string SearchKey = im->groupInfo_.loginName();
						if(listDomainUsers(SearchKey, shareUserInfos, 100))
						{
							continue;
						}
						if(shareUserInfos.size() == 0)
						{
							continue;
						}
						emailnode.mailto = shareUserInfos[0].email();
						if(emailnode.mailto.empty())
						{
							HSLOG_ERROR(MODULE_NAME, RT_ERROR, "mailto is empty.");
						}
						else
						{
							MAKE_CLIENT(client);
							if(!client().sendEmail(emailnode))
							{
								EmailInfoNode emailInfoNode;
								emailInfoNode.sender = userContext_->getUserInfoMgr()->getUserId();
								emailInfoNode.source = "share";
								//emailInfoNode.ownedBy;
								emailInfoNode.subject = "";
								emailInfoNode.message = emailMsg;
								emailInfoNode.nodeId = id;
								setMailInfo(id, emailInfoNode);
							}
						}
					}
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

	virtual int32_t setShareV2(int64_t id, ShareNodeExList& shareNodeExs, int32_t type, std::wstring fileName, const std::string& emailMsg)
	{
		int32_t ret = setShareV2Wrapprt(id, shareNodeExs, type, fileName, emailMsg);
		if (RT_OK != ret)
		{
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SETSHARE_FAILED));
			return ret;
		}
		return ret;
	}

	virtual int32_t setSync(const Path& path, bool isSync)
	{
		if (INVALID_ID == path.id()
			|| ROOT_PARENTID != path.parent())
		{
			return RT_INVALID_PARAM;
		}
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		
		ret = client().setSyncStatus(path.ownerId(), path.id(), path.type(), isSync);
		if(RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "setSync id:%I64d error.", path.id());
		}

		return ret;	
	}

	virtual int32_t search(const std::string& keyword, LIST_FOLDER_RESULT& result, const PageParam& pageParam, int64_t& count,
		bool needPath, std::map<int64_t, std::wstring>& pathInfo)
	{
		int32_t ret = RT_OK;

		MAKE_CLIENT(client);
		std::list<FileItem*> fileItems;
		ret = client().search(userContext_->getUserInfoMgr()->getUserId(), keyword, pageParam, count, fileItems, needPath, pathInfo);

		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "search of %s failed.", keyword.c_str());
			return ret;
		}

		if(RT_OK == ret)
		{
			for (std::list<FileItem*>::iterator it = fileItems.begin(); it != fileItems.end(); ++it)
			{
				FileItem* pFileItem = *it;
				if (NULL == pFileItem)
				{
					continue;
				}

				FILE_DIR_INFO fileDirInfo;

				fileDirInfo.id = pFileItem->id();
				fileDirInfo.parent = pFileItem->parent();
				fileDirInfo.name = SD::Utility::String::utf8_to_wstring(pFileItem->name());
				fileDirInfo.type = pFileItem->type();
				fileDirInfo.size = pFileItem->size();
				fileDirInfo.mtime = pFileItem->modifieTime();
				fileDirInfo.ctime = pFileItem->createTime();
				fileDirInfo.version = pFileItem->version();
				fileDirInfo.objectId = SD::Utility::String::utf8_to_wstring(pFileItem->objectId());
				fileDirInfo.fingerprint = pFileItem->fingerprint();
				fileDirInfo.extraType = SD::Utility::String::utf8_to_wstring(pFileItem->extraType());
				SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_ENCRYPT, pFileItem->isEncrypt());
				SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARED, pFileItem->isShare());
				SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SYNC, pFileItem->isSync());
				SET_BIT_VALUE_BY_BOOL(fileDirInfo.flags, OBJECT_FLAG_SHARELINK, pFileItem->isSharelink());

				result.push_back(fileDirInfo);
			}
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

		return ret;	
	}

	virtual int32_t getFilePath(const int64_t& file_id, std::wstring& path)
	{
		std::list<PathNode> pathNodes;
		int32_t ret = getFilePathNodes(file_id, pathNodes);
		if (RT_OK != ret)
		{
			return ret;	
		}

		for(std::list<PathNode>::iterator it = pathNodes.begin(); it != pathNodes.end(); ++it)
		{
			path += it->fileName + L"/" ;
		}
		return ret;
	}

	virtual int32_t getFilePathNodes(const int64_t& file_id, std::list<PathNode>& pathNodes)
	{
		MAKE_CLIENT(client);
		std::vector<int64_t> parentIds;
		std::vector<std::string> parentNames;
		int32_t ret = client().getFilePath(userContext_->getUserInfoMgr()->getUserId(), file_id, parentIds, parentNames);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "getFilePath failed");
			return ret;	
		}

		for(size_t i = 0; i < parentIds.size(); ++i)
		{
			PathNode node;
			node.fileId = parentIds[i];
			node.fileName = SD::Utility::String::utf8_to_wstring(parentNames[i]);
			pathNodes.push_front(node);
		}
		return ret;
	}

	virtual int32_t getMailInfo(const int64_t fileId, std::string source, EmailInfoNode& emailInfoNode)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().getMailInfo(userContext_->getUserInfoMgr()->getUserId(), fileId, source, emailInfoNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "get email info failed");
		}

		return ret;
	}

	virtual int32_t setMailInfo(const int64_t fileId, EmailInfoNode& emailInfoNode)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().setMailInfo(userContext_->getUserInfoMgr()->getUserId(), fileId, emailInfoNode);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "set email info failed");
		}

		return ret;
	}

	virtual int32_t listGroups(const std::string& strKey, const std::string& type, const PageParam& pageparam, int64_t& count, GroupNodeList& nodes)
	{
		if (strKey.empty())
		{
			return RT_INVALID_PARAM;
		}

		MAKE_CLIENT(client);
		int32_t ret = client().listGroups(strKey, type, pageparam, count, nodes);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "listGroups of failed.");
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
