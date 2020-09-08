#ifndef __ONEBOX__TEAMSPACE_INFO__H__
#define __ONEBOX__TEAMSPACE_INFO__H__

#include "CommonValue.h"
#include <list>

#ifndef DEFAULT_REGION_ID
#define DEFAULT_REGION_ID (-1)
#endif

#ifndef TEAMSPACE_UNLIMIT_VALUE
#define TEAMSPACE_UNLIMIT_VALUE (-1)
#endif

#ifndef TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE
#define TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE ("teamRole")
#endif

#ifndef TEAMSPACE_LIST_ORDER_FIELD_CREATEDAT
#define TEAMSPACE_LIST_ORDER_FIELD_CREATEDAT ("createdAt")
#endif

#ifndef TEAMSPACE_LIST_ORDER_FIELD_USERNAME
#define TEAMSPACE_LIST_ORDER_FIELD_USERNAME ("userName")
#endif

#ifndef TEAMSPACE_LIST_ORDER_DIRECTION_ASC
#define TEAMSPACE_LIST_ORDER_DIRECTION_ASC ("ASC")
#endif

#ifndef TEAMSPACE_LIST_ORDER_DIRECTION_DESC
#define TEAMSPACE_LIST_ORDER_DIRECTION_DESC ("DESC")
#endif

#ifndef DEFAULT_TEAMSPACE_LIST_LIMIT
#define DEFAULT_TEAMSPACE_LIST_LIMIT (100)
#endif

enum TeamspaceRoleType
{
	TeamspaceRoleType_Admin,
	TeamspaceRoleType_Manager,
	TeamspaceRoleType_Member,
	TeamspaceRoleType_All,
	TeamspaceRoleType_Invalid
};

enum TeamspaceStatus
{
	TeamspaceStatus_Normal,
	TeamspaceStatus_Disable,
	TeamspaceStatus_Invalid
};

enum TeamspaceMemberType
{
	TeamspaceMemberType_User,
	TeamspaceMemberType_Group,
	TeamspaceMemberType_Invalid
};

class TeamspaceNode
{
public:
	TeamspaceNode()
		:id_(0)
		,name_("")
		,description_("")
		,curNumbers_(0)
		,createdAt_(0)
		,createdBy_(0)
		,createdByUserName_("")
		,ownedBy_(0)
		,ownedByUserName_("")
		,status_(TeamspaceStatus_Invalid)
		,spaceQuota_(0)
		,spaceUsed_(0)
		,maxVersions_(-1)
		,maxMembers_(-1)
		,regionId_(DEFAULT_REGION_ID) {}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int32_t, curNumbers)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(std::string, createdByUserName)
	FUNC_DEFAULT_SET_GET(int64_t, ownedBy)
	FUNC_DEFAULT_SET_GET(std::string, ownedByUserName)
	FUNC_DEFAULT_SET_GET(TeamspaceStatus, status)
	FUNC_DEFAULT_SET_GET(int64_t, spaceQuota)
	FUNC_DEFAULT_SET_GET(int64_t, spaceUsed)
	FUNC_DEFAULT_SET_GET(int32_t, maxVersions)
	FUNC_DEFAULT_SET_GET(int32_t, maxMembers)
	FUNC_DEFAULT_SET_GET(int32_t, regionId)

private:
	int64_t id_; // 团队空间ID
	std::string name_; // 团队空间名称
	std::string description_; // 团队空间描述
	int32_t curNumbers_; // 成员数量
	int64_t createdAt_; // 创建时间的毫秒值
	int64_t createdBy_; // 创建者的ID
	std::string createdByUserName_; // 创建者的名称
	int64_t ownedBy_; // 拥有者的ID
	std::string ownedByUserName_; // 拥有者的名称
	TeamspaceStatus status_; // 团队空间状态
	int64_t spaceQuota_; // 团队空间的容量，单位为字节
	int64_t spaceUsed_; // 团队空间已经使用的容量，单位为字节
	int32_t maxVersions_; // 最大版本数，默认值为-1，表示无限制
	int32_t maxMembers_; // 团队空间最大成员数限制，默认值为-1，表示无限制
	int32_t regionId_; // 存储区域ID
};

class TeamspaceMemberNode
{
public:
	TeamspaceMemberNode()
		:id_(0)
		,type_(TeamspaceMemberType_Invalid)
		,name_("")
		,loginName_("")
		,description_("") {}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(TeamspaceMemberType, type)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, description)

private:
	int64_t id_; // User或者群组的ID
	TeamspaceMemberType type_; // 类型，User或者群组
	std::string name_; // 用户或者群组名称
	std::string loginName_; // 用户名称
	std::string description_; // 用户或者群组名称描述
};

class TeamspaceMembership
{
public:
	TeamspaceMembership()
		:id_(0)
		,teamRole_(TeamspaceRoleType_Invalid)
		,role_("") {}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(TeamspaceNode, teamspace)
	FUNC_DEFAULT_SET_GET(TeamspaceMemberNode, teamspaceMember)
	FUNC_DEFAULT_SET_GET(TeamspaceRoleType, teamRole)
	FUNC_DEFAULT_SET_GET(std::string, role)

private:
	int64_t id_; // 团队空间成员关系ID
	TeamspaceNode teamspace_; // 团队空间对象
	TeamspaceMemberNode teamspaceMember_; // User或者群组对象
	TeamspaceRoleType teamRole_; // 团队空间成员角色
	std::string role_; // 权限角色名称（可变，由管理员自定义）
};

typedef std::list<TeamspaceNode> TeamspaceNodes;
typedef std::list<TeamspaceMemberNode> TeamspaceMemberNodes;
typedef std::list<TeamspaceMembership> TeamspaceMemberships;

static inline std::string convertTeamspaceRoleType(const TeamspaceRoleType type)
{
	if (type == TeamspaceRoleType_Admin)
	{
		return "admin";
	}
	else if (type == TeamspaceRoleType_Manager)
	{
		return "manager";
	}
	else if (type == TeamspaceRoleType_Member)
	{
		return "member";
	}
	else if (type == TeamspaceRoleType_All)
	{
		return "all";
	}
	else
	{
		return "";
	}
}

static inline TeamspaceRoleType convertTeamspaceRoleType(const std::string& type)
{
	if (type == "admin")
	{
		return TeamspaceRoleType_Admin;
	}
	else if (type == "manager")
	{
		return TeamspaceRoleType_Manager;
	}
	else if (type == "member")
	{
		return TeamspaceRoleType_Member;
	}
	else if (type == "all")
	{
		return TeamspaceRoleType_All;
	}
	else
	{
		return TeamspaceRoleType_Invalid;
	}
}

static inline std::string convertTeamspaceMemberType(const TeamspaceMemberType type)
{
	if (type == TeamspaceMemberType_User)
	{
		return "user";
	}
	else if (type == TeamspaceMemberType_Group)
	{
		return "group";
	}
	else
	{
		return "";
	}
}

static inline TeamspaceMemberType convertTeamspaceMemberType(const std::string& type)
{
	if (type == "user")
	{
		return TeamspaceMemberType_User;
	}
	else if (type == "group")
	{
		return TeamspaceMemberType_Group;
	}
	else
	{
		return TeamspaceMemberType_Invalid;
	}
}

#endif
