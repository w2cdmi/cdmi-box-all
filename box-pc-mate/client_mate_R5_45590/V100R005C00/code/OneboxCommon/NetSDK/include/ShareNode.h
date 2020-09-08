#ifndef _SHARE_NODE_H_
#define _SHARE_NODE_H_

/******************************************************************************
Description  : �����ϵ�ڵ���Ϣ
Created By   : dailinye
*******************************************************************************/

#include "CommonValue.h"
#include <vector>
#include <list>

#define ORDER_TYPE_BY_NAME "name"
#define ORDER_TYPE_BY_MODIFY_TIME "modifiedAt"
#define ORDER_TYPE_BY_OWNER_NAME "ownerName"

class ShareNode
{
public:
	ShareNode()
		:id_(0L)
		,type_(0)
		,ownerId_(0L)
		,ownerName_("")
		,sharedUserId_(0L)
		,sharedUserType_("")
		,sharedUserName_("")
		,sharedUserLoginName_("")
		,sharedUserDescription_("")
		,inodeId_(0L)
		,name_("")
		,modifiedAt_(0L)
		,modifiedBy_(0L)
		,roleName_("")
		,status_(0)
		,size_(0L)
		,extraType_("")
	{
	}

	virtual ~ShareNode()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(int32_t, type)
	FUNC_DEFAULT_SET_GET(int64_t, ownerId)
	FUNC_DEFAULT_SET_GET(std::string, ownerName)
	FUNC_DEFAULT_SET_GET(int64_t, sharedUserId)
	FUNC_DEFAULT_SET_GET(std::string, sharedUserType)
	FUNC_DEFAULT_SET_GET(std::string, sharedUserName)
	FUNC_DEFAULT_SET_GET(std::string, sharedUserLoginName)
	FUNC_DEFAULT_SET_GET(std::string, sharedUserDescription)
	FUNC_DEFAULT_SET_GET(int64_t, inodeId)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)
	FUNC_DEFAULT_SET_GET(std::string, roleName)
	FUNC_DEFAULT_SET_GET(int32_t, status)
	FUNC_DEFAULT_SET_GET(int64_t, size)
	FUNC_DEFAULT_SET_GET(std::string, extraType)

private:
	int64_t id_; // �����ϵΨһ��ʶ
	int32_t type_; // �����ϵ���� 0��ʾ�ļ��� 1��ʾ�ļ�
	int64_t ownerId_; // ������Դӵ����ID
	std::string ownerName_; // ������Դӵ����ȫ��
	int64_t sharedUserId_; // ��������ID
	std::string sharedUserType_; // ���������û����ͣ�0��ʾ��ͨ�û�
	std::string sharedUserName_; // ���������û�ȫ��
	std::string sharedUserLoginName_; // ���������û���½��
	std::string sharedUserDescription_; // ������������
	int64_t inodeId_; // ������ԴID
	std::string name_; // ������Դ����
	int64_t modifiedAt_; // �����ϵ������ʱ��
	int64_t modifiedBy_; // �����ϵ��������ID
	std::string roleName_; // �����߽�ɫ��Ŀǰֻ֧��"shared"����ʾֻ��������
	int32_t status_; // ������Դ״̬��0��ʾ������1��ʾ����λ�ڻ���վ��ʵ�ʲ��᷵��״̬Ϊ��0������
	int64_t size_; // �ļ���С���ļ��д�СΪ0����ʵ������
	std::string extraType_;
};

typedef std::vector<ShareNode> ShareNodeList;
#define EmptyShareNodes std::vector<ShareNode>(0)

class ShareNodeEx
{
public:
	ShareNodeEx()
		:sharedUserId_(0L)
		,loginName_("")
		,sharedUserType_("user")
		,sharedEmail_("")
		,roleName_("")
	{
	}

	virtual ~ShareNodeEx()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, sharedUserId)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, sharedUserType)
	FUNC_DEFAULT_SET_GET(std::string, sharedEmail)
	FUNC_DEFAULT_SET_GET(std::string, roleName)
private:
	int64_t sharedUserId_;	// ��������ID
	std::string loginName_;	// ���������û���½��
	std::string sharedUserType_; 
	std::string sharedEmail_;
	std::string roleName_;
};

typedef std::vector<ShareNodeEx> ShareNodeExList;

static ShareNode convertShareNodeExToShareNode(ShareNodeEx& shareNodeEx)
{
	ShareNode shareNode;
	shareNode.sharedUserId(shareNodeEx.sharedUserId());
	shareNode.sharedUserType(shareNodeEx.sharedUserType());
	shareNode.roleName(shareNodeEx.roleName());

	return shareNode;
}

enum ShareType
{
	ShareType_Share,
	ShareType_Link
};

struct MyShareNode
{
	int64_t id;
	int64_t parent;
	int64_t size;
	int64_t linkCount;
	int32_t type;
	std::string name;
	std::string path;
	std::string extraType;

	MyShareNode():id(-1), parent(-1), type(-1), name(""), path(""), size(0), linkCount(0), extraType("")
	{}
};
typedef std::list<MyShareNode> MyShareNodeList;

#endif
