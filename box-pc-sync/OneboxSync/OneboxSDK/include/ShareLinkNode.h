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
	std::string id_; // �����ϵΨһ��ʶ
	int64_t iNodeId_; // ������ԴID
	int64_t ownedBy_; // ��Դӵ����ID
	std::string url_; // ������ַ
	std::string plainAccessCode_; // ������ȡ��
	int64_t effectiveAt_; // ������Чʱ��
	int64_t expireAt_; // ����ʧЧʱ��
	int64_t createdAt_; // ��������ʱ��
	int64_t modifiedAt_; // ����������ʱ��
	int64_t createdBy_; // ����������ID
	int64_t modifiedBy_; // ��������޸���ID
	int64_t creator_;	//�������������������
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
	std::string plainAccessCode_; // ������ȡ��
	int64_t effectiveAt_; // ������Чʱ��
	int64_t expireAt_; // ����ʧЧʱ��
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
