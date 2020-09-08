#ifndef _SHARE_LINK_NODE_H_
#define _SHARE_LINK_NODE_H_

/******************************************************************************
Description  : �����ڵ���Ϣ
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
	std::string id_; // �����ϵΨһ��ʶ
	int64_t iNodeId_; // ������ԴID
	int64_t ownedBy_; // ��Դӵ����ID
	std::string url_; // ������ַ
	std::string plainAccessCode_; // ������ȡ��
	std::string accesCodeMode_; // ������ȡ��ģʽ
	int64_t effectiveAt_; // ������Чʱ��
	int64_t expireAt_; // ����ʧЧʱ��
	int64_t createdAt_; // ��������ʱ��
	int64_t modifiedAt_; // ����������ʱ��
	int64_t createdBy_; // ����������ID
	int64_t modifiedBy_; // ��������޸���ID
	std::string creator_;	//�������������������
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
	std::string plainAccessCode_; // ������ȡ��
	int64_t effectiveAt_; // ������Чʱ��
	int64_t expireAt_; // ����ʧЧʱ��
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
