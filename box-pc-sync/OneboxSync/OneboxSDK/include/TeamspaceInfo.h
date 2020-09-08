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
	int64_t id_; // �Ŷӿռ�ID
	std::string name_; // �Ŷӿռ�����
	std::string description_; // �Ŷӿռ�����
	int32_t curNumbers_; // ��Ա����
	int64_t createdAt_; // ����ʱ��ĺ���ֵ
	int64_t createdBy_; // �����ߵ�ID
	std::string createdByUserName_; // �����ߵ�����
	int64_t ownedBy_; // ӵ���ߵ�ID
	std::string ownedByUserName_; // ӵ���ߵ�����
	TeamspaceStatus status_; // �Ŷӿռ�״̬
	int64_t spaceQuota_; // �Ŷӿռ����������λΪ�ֽ�
	int64_t spaceUsed_; // �Ŷӿռ��Ѿ�ʹ�õ���������λΪ�ֽ�
	int32_t maxVersions_; // ���汾����Ĭ��ֵΪ-1����ʾ������
	int32_t maxMembers_; // �Ŷӿռ�����Ա�����ƣ�Ĭ��ֵΪ-1����ʾ������
	int32_t regionId_; // �洢����ID
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
	int64_t id_; // User����Ⱥ���ID
	TeamspaceMemberType type_; // ���ͣ�User����Ⱥ��
	std::string name_; // �û�����Ⱥ������
	std::string loginName_; // �û�����
	std::string description_; // �û�����Ⱥ����������
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
	int64_t id_; // �Ŷӿռ��Ա��ϵID
	TeamspaceNode teamspace_; // �Ŷӿռ����
	TeamspaceMemberNode teamspaceMember_; // User����Ⱥ�����
	TeamspaceRoleType teamRole_; // �Ŷӿռ��Ա��ɫ
	std::string role_; // Ȩ�޽�ɫ���ƣ��ɱ䣬�ɹ���Ա�Զ��壩
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
