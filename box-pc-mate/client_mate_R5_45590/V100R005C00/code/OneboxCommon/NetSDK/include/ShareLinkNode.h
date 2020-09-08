#ifndef _SHARE_LINK_NODE_H_
#define _SHARE_LINK_NODE_H_

/******************************************************************************
Description  : 外链节点信息
Created By   : dailinye
*******************************************************************************/

#include "CommonValue.h"
#include <vector>

typedef std::vector<std::string> EmailList;
typedef std::vector<std::string> IdentityList;

class ShareLinkNode
{
public:
	ShareLinkNode()
		:id_("")
		,iNodeId_(0L)
		,ownedBy_(0)
		,url_("")
		,plainAccessCode_("")
		,accesCodeMode_("")
		,effectiveAt_(0L)
		,expireAt_(0L)
		,createdAt_(0L)
		,modifiedAt_(0L)
		,createdBy_(0L)
		,modifiedBy_(0L)
		,creator_("")
		,role_("")
	{

	}	

	virtual ~ShareLinkNode()
	{

	}

	FUNC_DEFAULT_SET_GET(std::string, id)
	FUNC_DEFAULT_SET_GET(int64_t, iNodeId)
	FUNC_DEFAULT_SET_GET(int64_t, ownedBy)
	FUNC_DEFAULT_SET_GET(std::string, url)
	FUNC_DEFAULT_SET_GET(std::string, plainAccessCode)
	FUNC_DEFAULT_SET_GET(std::string, accesCodeMode)
	FUNC_DEFAULT_SET_GET(int64_t, effectiveAt)
	FUNC_DEFAULT_SET_GET(int64_t, expireAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)
	FUNC_DEFAULT_SET_GET(std::string, creator)
	FUNC_DEFAULT_SET_GET(std::string, role)
	//FUNC_DEFAULT_SET_GET(IdentityList, identity)
public:
	IdentityList identity_;
private:
	std::string id_; // 共享关系唯一标识
	int64_t iNodeId_; // 共享资源ID
	int64_t ownedBy_; // 资源拥有者ID
	std::string url_; // 外链地址
	std::string plainAccessCode_; // 外链提取码
	std::string accesCodeMode_; // 外链提取码模式
	int64_t effectiveAt_; // 外链生效时间
	int64_t expireAt_; // 外链失效时间
	int64_t createdAt_; // 外链创建时间
	int64_t modifiedAt_; // 外链最后更新时间
	int64_t createdBy_; // 外链创建者ID
	int64_t modifiedBy_; // 外链最后修改者ID
	std::string creator_;	//外链创建或更新者名称
	std::string role_;
};

class ShareLinkNodeEx
{
public:
	ShareLinkNodeEx()
		:plainAccessCode_("")
		,effectiveAt_(0)
		,expireAt_(0)
		,role_("")
		,accessCodeMode_("")
	{
	}

	virtual ~ShareLinkNodeEx()
	{

	}

	FUNC_DEFAULT_SET_GET(std::string, plainAccessCode)
	FUNC_DEFAULT_SET_GET(int64_t, effectiveAt)
	FUNC_DEFAULT_SET_GET(int64_t, expireAt)
	FUNC_DEFAULT_SET_GET(std::string, role)
	FUNC_DEFAULT_SET_GET(std::string, accessCodeMode)
	//FUNC_DEFAULT_SET_GET(IdentityList, identity)

public:
	IdentityList identity_;
private:
	std::string plainAccessCode_; // 外链提取码
	int64_t effectiveAt_; // 外链生效时间
	int64_t expireAt_; // 外链失效时间
	std::string role_;
	std::string accessCodeMode_;
	
};

static ShareLinkNode convertShareLinkNodeExToShareLinkNode(const ShareLinkNodeEx& shareLinkNodeEx)
{
	ShareLinkNode shareLinkNode;
	shareLinkNode.plainAccessCode(shareLinkNodeEx.plainAccessCode());
	shareLinkNode.effectiveAt(shareLinkNodeEx.effectiveAt());
	shareLinkNode.expireAt(shareLinkNodeEx.expireAt());
	shareLinkNode.role(shareLinkNodeEx.role());
	shareLinkNode.accesCodeMode(shareLinkNodeEx.accessCodeMode());

	for(IdentityList::const_iterator it = shareLinkNodeEx.identity_.begin(); it != shareLinkNodeEx.identity_.end(); ++it)
	{
		shareLinkNode.identity_.push_back(*it);
	}

	return shareLinkNode;
}

typedef std::vector<ShareLinkNode> ShareLinkNodeList;

#endif
