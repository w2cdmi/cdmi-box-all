#ifndef _TEAMSPACES_NODE_H_
#define _TEAMSPACES_NODE_H_

/******************************************************************************
Description  : 团队关系节点信息

*******************************************************************************/

#include "CommonValue.h"
#include <vector>

// /**
//  * 团队空间的元数据  
//  */
// struct TeamSpaceInfo
// {
// 	1: i64 id,					/*团队空间ID*/
// 	2: string name,				/*团队空间名称*/
// 	3: string description,		/*团队空间描述*/
// 	4: i32 curNumbers,			/*成员数量*/
// 	5: i64 createdAt,			/*创建时间*/
// 	6: i64 createdBy,			/*创建者的ID*/
// 	7: string createdByUserName,/*创建者的名称*/
// 	8: byte status,				/*状态*/
// 	9: i32 spaceQuota,			/*团队空间的容量，单位为MB*/
// 	10: i32 usedQuota,			/*团队空间已经使用的容量，单位为MB*/
// 	11: string ownerBy,			/*拥有者的ID*/
// 	12: string ownerByUserName,	/*拥有者的用户名*/
// 	13: i32 maxVersions,		/*最大版本数，默认值为-1，表示无限制*/
// 	14: i32 maxMembers,			/*最大成员数*/
// }
// 

// struct TeamSpaceMemberInfo
// {
// 	1: string id,				/*成员ID*/
// 	2: string loginName,		/*登录用户名*/
// 	3: string type,				/*user或者group*/
// 	4: string name,				/*用户或者群组名称*/
// 	5: string desc,				/*用户或者群组描述*/
// }
// 
// /**
//  * 用户团队空间的数据  
//  */
// struct UserTeamSpaceInfo
// {
// 	1: i32 id,
// 	2: i32 teamId,
// 	3: string teamRole,
// 	4: TeamSpaceInfo teamInfo,
// 	5: TeamSpaceMemberInfo member,
// }

class  TeamSpaceMemberNodeInfo
{
public:
	TeamSpaceMemberNodeInfo()
	{
		id_ = 0L;
		loginName_ = "";
		type_ = "";
		name_ = "";
		desc_ = "";
	}

	virtual ~TeamSpaceMemberNodeInfo()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, type)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, desc)

	TeamSpaceMemberNodeInfo& operator=(const TeamSpaceMemberNodeInfo &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();
			loginName_ = rhs.loginName();
			type_ = rhs.type();
			name_ = rhs.name();
			desc_ = rhs.desc();
		}
		return *this;
	}

private:
	int64_t id_;				/*成员ID*/
	std::string loginName_;			/*登录用户名*/
	std::string type_;				/*user或者group*/
	std::string name_;				/*用户或者群组名称*/
	std::string desc_;				/*用户或者群组描述*/
};

class TeamSpacesNode
{
public:
	TeamSpacesNode()
	{
		id_ = 0L;
		name_ = "";
		description_ = "";
		curNumbers_ = 0;
		createdAt_ = 0L;
		createdBy_ = 0L;
		createdByUserName_ = "";
		status_ = 0;	
		spaceQuota_ = -1;
		usedQuota_ = 0L;
		ownerBy_ = 0L;
		ownerByUserName_ = "";
		maxVersions_ = -1;
		maxMembers_ = -1;
	}

	virtual ~TeamSpacesNode()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int32_t, curNumbers)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(std::string, createdByUserName)
	FUNC_DEFAULT_SET_GET(int8_t, status)
	FUNC_DEFAULT_SET_GET(int64_t, spaceQuota)
	FUNC_DEFAULT_SET_GET(int64_t, usedQuota)
	FUNC_DEFAULT_SET_GET(int64_t, ownerBy)
	FUNC_DEFAULT_SET_GET(std::string, ownerByUserName)
	FUNC_DEFAULT_SET_GET(int32_t, maxVersions)
	FUNC_DEFAULT_SET_GET(int32_t, maxMembers)

	TeamSpacesNode& operator=(const TeamSpacesNode &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();			
			name_ = rhs.name();
			description_ = rhs.description();
			curNumbers_ = rhs.curNumbers();
			createdAt_ = rhs.createdAt();
			createdBy_ = rhs.createdBy();
			createdByUserName_ = rhs.createdByUserName();
			status_ = rhs.status();
			spaceQuota_ = rhs.spaceQuota();
			usedQuota_ = rhs.usedQuota();
			ownerBy_ = rhs.ownerBy();
			ownerByUserName_ = rhs.ownerByUserName();
			maxVersions_ = rhs.maxVersions();
			maxMembers_ = rhs.maxMembers();
		}
		return *this;
	}

private:
	int64_t id_;				/*团队空间ID*/
	std::string name_;				/*团队空间名称*/
	std::string description_;		/*团队空间描述*/
	int32_t curNumbers_;				/*成员数量*/
	int64_t createdAt_;			/*创建时间*/
	int64_t createdBy_;			/*创建者的ID*/
	std::string createdByUserName_;	/*创建者的名称*/
	int8_t status_;					/*状态*/
	int64_t spaceQuota_;				/*团队空间的容量，单位为KB*/
	int64_t usedQuota_;					/*团队空间已经使用的容量，单位为KB*/
	int64_t ownerBy_;			/*拥有者的ID*/
	std::string ownerByUserName_;	/*拥有者的用户名*/
	int32_t maxVersions_;				/*最大版本数，默认值为-1，表示无限制*/
	int32_t maxMembers_;				/*最大成员数*/
};


class  UserTeamSpaceNodeInfo
{
public:
	UserTeamSpaceNodeInfo()
	{
		id_ = 0L;
		teamId_ = 0L;
		teamRole_ = "";
		role_ = "";
	}

	virtual ~UserTeamSpaceNodeInfo()
	{
	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(int64_t, teamId)
	FUNC_DEFAULT_SET_GET(std::string, teamRole)
	FUNC_DEFAULT_SET_GET(std::string, role)

	UserTeamSpaceNodeInfo& operator=(const UserTeamSpaceNodeInfo &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();
			teamId_ = rhs.teamId();
			teamRole_ = rhs.teamRole();
			role_ = rhs.role();
			member_ = rhs.member_;
			teamInfo_ = rhs.teamInfo_;
		}
		return *this;
	}

private:
	int64_t id_;
	int64_t teamId_;
	std::string teamRole_;
	std::string role_;

public:
	TeamSpacesNode member_;
	TeamSpaceMemberNodeInfo teamInfo_;
};

typedef std::vector<UserTeamSpaceNodeInfo> UserTeamSpaceNodeInfoArray;

#endif
