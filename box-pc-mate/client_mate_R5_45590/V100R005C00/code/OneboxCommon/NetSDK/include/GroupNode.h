#ifndef _GROUP_NODE_H_
#define _GROUP_NODE_H_

#include "CommonValue.h"
#include <vector>

class GroupMemberNodeInfo
{
public:
	GroupMemberNodeInfo()
	{
		id_			= 0L;
		userId_		= 0L;
		groupId_	= 0L;
		name_		= "";
		username_	= "";
		userType_	= "";
		loginName_	= "";
		groupRole_	= "";
	}

	~GroupMemberNodeInfo()
	{
	}

	GroupMemberNodeInfo& operator=(const GroupMemberNodeInfo& rhs)
	{
		if(&rhs != this)
		{
			id_			= rhs.id();
			userId_		= rhs.userId();
			groupId_	= rhs.groupId();
			name_		= rhs.name();
			username_	= rhs.username();
			userType_	= rhs.userType();
			loginName_	= rhs.loginName();
			groupRole_	= rhs.groupRole();
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(int64_t, userId)
	FUNC_DEFAULT_SET_GET(int64_t, groupId)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, username)
	FUNC_DEFAULT_SET_GET(std::string, userType)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, groupRole)
private:
	int64_t id_; 
	int64_t userId_; 
	int64_t groupId_; 
    std::string name_; 
    std::string username_;
	std::string userType_;
	std::string loginName_;
	std::string groupRole_;
};

class GroupNode
{
public:
	GroupNode()
		:id_(0L)
		,name_("")
		,description_("")
		,accountId_(0L)
		,maxMembers_(0)
		,createdAt_(0L)
		,modifiedAt_(0L)
		,createdBy_(0L)
		,ownedBy_(0L)
		,status_("")
		,appId_("")
		,type_("")
	{
	}	

	virtual ~GroupNode()
	{

	}
	
	GroupNode& operator=(const GroupNode& rhs) 
	{
		if(&rhs	!=	this)
		{
			id_				= rhs.id();
			name_			= rhs.name();
			description_	= rhs.description();
			createdAt_		= rhs.createdAt();
			createdBy_		= rhs.createdBy();
			status_			= rhs.status();
			ownedBy_		= rhs.ownedBy();
			maxMembers_		= rhs.maxMembers();
			modifiedAt_		= rhs.modifiedAt();
			accountId_		= rhs.accountId();
			appId_			= rhs.appId();
			type_			= rhs.type();
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int64_t, accountId)
	FUNC_DEFAULT_SET_GET(int32_t, maxMembers)
	FUNC_DEFAULT_SET_GET(int64_t, ownedBy)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(std::string, status)
	FUNC_DEFAULT_SET_GET(std::string, appId)
	FUNC_DEFAULT_SET_GET(std::string, type)

private:
	int64_t id_; //
	std::string name_; //
	std::string description_; //
	int64_t accountId_; //
	int32_t maxMembers_; //
	int64_t ownedBy_; // 
	int64_t createdAt_; // 外链创建时间
	int64_t modifiedAt_; // 外链最后更新时间
	int64_t createdBy_; // 外链创建者ID
	std::string status_;	//
	std::string appId_;
	std::string type_;
};

class UserGroupNodeInfo
{
public:
	UserGroupNodeInfo()
	{
		id_			= 0L;
		groupId_	= 0L;
		groupRole_	= "";
	}

	~UserGroupNodeInfo()
	{
	}

	UserGroupNodeInfo& operator=(const UserGroupNodeInfo& rhs)
	{
		if(&rhs != this)
		{
			id_			= rhs.id();
			groupId_	= rhs.groupId();
			groupRole_	= rhs.groupRole();
			member_		= rhs.member_;
			groupInfo_	= rhs.groupInfo_;
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(int64_t, groupId)
	FUNC_DEFAULT_SET_GET(std::string, groupRole)

private:
	int64_t id_;
	int64_t groupId_;
	std::string groupRole_;

public:
	GroupNode			member_;
	GroupMemberNodeInfo groupInfo_;
};

typedef std::vector<GroupNode> GroupNodeList;

typedef std::vector<UserGroupNodeInfo>	UserGroupNodeInfoArray;

#endif