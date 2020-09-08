#ifndef __ONEBOX__SHARE_LINK_NODE_H_
#define __ONEBOX__SHARE_LINK_NODE_H_

#include "CommonValue.h"
#include <vector>

typedef std::vector<std::string> EmailList;

class ShareLinkNode
{
public:
	ShareLinkNode()
		:id_("")
		,iNodeId_(0L)
		,ownedBy_(0)
		,url_("")
		,plainAccessCode_("")
		,effectiveAt_(0L)
		,expireAt_(0L)
		,createdAt_(0L)
		,modifiedAt_(0L)
		,createdBy_(0L)
		,modifiedBy_(0L)
		,creator_(0L)
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
	FUNC_DEFAULT_SET_GET(int64_t, effectiveAt)
	FUNC_DEFAULT_SET_GET(int64_t, expireAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)
	FUNC_DEFAULT_SET_GET(int64_t, creator)

private:
	std::string id_; // 共享关系唯一标识
	int64_t iNodeId_; // 共享资源ID
	int64_t ownedBy_; // 资源拥有者ID
	std::string url_; // 外链地址
	std::string plainAccessCode_; // 外链提取码
	int64_t effectiveAt_; // 外链生效时间
	int64_t expireAt_; // 外链失效时间
	int64_t createdAt_; // 外链创建时间
	int64_t modifiedAt_; // 外链最后更新时间
	int64_t createdBy_; // 外链创建者ID
	int64_t modifiedBy_; // 外链最后修改者ID
	int64_t creator_;	//外链创建或更新者名称
};

class ShareLinkNodeEx
{
public:
	ShareLinkNodeEx()
		:plainAccessCode_("")
		,effectiveAt_(0)
		,expireAt_(0)
	{
	}

	virtual ~ShareLinkNodeEx()
	{

	}

	FUNC_DEFAULT_SET_GET(std::string, plainAccessCode)
	FUNC_DEFAULT_SET_GET(int64_t, effectiveAt)
	FUNC_DEFAULT_SET_GET(int64_t, expireAt)

private:
	std::string plainAccessCode_; // 外链提取码
	int64_t effectiveAt_; // 外链生效时间
	int64_t expireAt_; // 外链失效时间
};

static ShareLinkNode convertShareLinkNodeExToShareLinkNode(const ShareLinkNodeEx& shareLinkNodeEx)
{
	ShareLinkNode shareLinkNode;
	shareLinkNode.plainAccessCode(shareLinkNodeEx.plainAccessCode());
	shareLinkNode.effectiveAt(shareLinkNodeEx.effectiveAt());
	shareLinkNode.expireAt(shareLinkNodeEx.expireAt());

	return shareLinkNode;
}

#endif
