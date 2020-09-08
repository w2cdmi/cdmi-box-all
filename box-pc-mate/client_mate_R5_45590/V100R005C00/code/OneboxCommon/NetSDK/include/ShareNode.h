#ifndef _SHARE_NODE_H_
#define _SHARE_NODE_H_

/******************************************************************************
Description  : 共享关系节点信息
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
	int64_t id_; // 共享关系唯一标识
	int32_t type_; // 共享关系类型 0表示文件夹 1表示文件
	int64_t ownerId_; // 共享资源拥有者ID
	std::string ownerName_; // 共享资源拥有者全名
	int64_t sharedUserId_; // 被共享者ID
	std::string sharedUserType_; // 被共享者用户类型，0表示普通用户
	std::string sharedUserName_; // 被共享者用户全名
	std::string sharedUserLoginName_; // 被共享者用户登陆名
	std::string sharedUserDescription_; // 被共享者描述
	int64_t inodeId_; // 共享资源ID
	std::string name_; // 共享资源名称
	int64_t modifiedAt_; // 共享关系最后更新时间
	int64_t modifiedBy_; // 共享关系最后更新者ID
	std::string roleName_; // 共享者角色，目前只支持"shared"，表示只读共享者
	int32_t status_; // 共享资源状态，0表示正常，1表示数据位于回收站。实际不会返回状态为非0的数据
	int64_t size_; // 文件大小，文件夹大小为0，无实际意义
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
	int64_t sharedUserId_;	// 被共享者ID
	std::string loginName_;	// 被共享者用户登陆名
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
