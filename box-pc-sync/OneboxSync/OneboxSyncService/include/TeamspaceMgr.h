#ifndef _ONEBOX_TEAMSPACE_MGR_H_
#define _ONEBOX_TEAMSPACE_MGR_H_

#include "UserContext.h"
#include "TeamspaceInfo.h"

class TeamspaceMgr
{
public:
	virtual ~TeamspaceMgr() {}
	
	static TeamspaceMgr* create(UserContext* userContext);

	virtual int32_t listAllTeamspaces(TeamspaceNodes& teamspaceNodes, 
		int64_t& totalCount, 
		const std::string& keyword = "", 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC) = 0;

	virtual int32_t listTeamspacesByUser(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t userId, 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC) = 0;

	virtual int32_t createTeamspace(TeamspaceNode& teamspaceNode, 
		const std::string& name, 
		const std::string& description = "", 
		const int64_t spaceQuota = TEAMSPACE_UNLIMIT_VALUE, 
		const TeamspaceStatus status = TeamspaceStatus_Normal, 
		const int32_t maxVersion = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t maxMembers = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t regionId = DEFAULT_REGION_ID) = 0;

	virtual int32_t updateTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId, 
		const std::string& name, 
		const std::string& description = "", 
		const int64_t spaceQuota = TEAMSPACE_UNLIMIT_VALUE, 
		const TeamspaceStatus status = TeamspaceStatus_Normal, 
		const int32_t maxVersion = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t maxMembers = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t regionId = DEFAULT_REGION_ID) = 0;

	virtual int32_t getTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId) = 0;

	virtual int32_t deleteTeamspace(const int64_t teamspaceId) = 0;

	virtual int32_t addTeamspaceMember(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role, 
		const int64_t memberId, 
		const TeamspaceMemberType memberType) = 0;

	virtual int32_t getTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId) = 0;

	virtual int32_t updateTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role) = 0;

	virtual int32_t listTeamspaceMembers(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t teamspaceId, 
		const std::string& keyword = "", 
		const TeamspaceRoleType roleType = TeamspaceRoleType_All, 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC) = 0;

	virtual int32_t deleteTeamspaceMember(const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId) = 0;

};

#endif
