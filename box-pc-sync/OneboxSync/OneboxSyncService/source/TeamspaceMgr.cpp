#include "TeamspaceMgr.h"
#include "NetworkMgr.h"
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TeamspaceMgr")
#endif

class TeamspaceMgrImpl : public TeamspaceMgr
{
public:
	TeamspaceMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual ~TeamspaceMgrImpl()
	{
		userContext_ = NULL;
	}

	virtual int32_t listAllTeamspaces(TeamspaceNodes& teamspaceNodes, 
		int64_t& totalCount, 
		const std::string& keyword, 
		const int64_t offset, 
		const int32_t limit, 
		const std::string& orderField, 
		const std::string& orderDirection)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().listAllTeamspaces(teamspaceNodes, totalCount, keyword, offset, limit, orderField, orderDirection);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to list all teamspaces.");
			return ret;
		}
		return ret;
	}

	virtual int32_t listTeamspacesByUser(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t userId, 
		const int64_t offset, 
		const int32_t limit, 
		const std::string& orderField, 
		const std::string& orderDirection)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().listTeamspacesByUser(teamspaceMemberships, totalCount, userId, offset, limit, orderField, orderDirection);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to list teamspace by user.");
			return ret;
		}
		return ret;
	}

	virtual int32_t createTeamspace(TeamspaceNode& teamspaceNode, 
		const std::string& name, 
		const std::string& description, 
		const int64_t spaceQuota, 
		const TeamspaceStatus status, 
		const int32_t maxVersion, 
		const int32_t maxMembers, 
		const int32_t regionId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().createTeamspace(teamspaceNode, name, description, spaceQuota, status, maxVersion, maxMembers, regionId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to create teamspace.");
			return ret;
		}
		return ret;
	}

	virtual int32_t updateTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId, 
		const std::string& name, 
		const std::string& description, 
		const int64_t spaceQuota, 
		const TeamspaceStatus status, 
		const int32_t maxVersion, 
		const int32_t maxMembers, 
		const int32_t regionId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().updateTeamspace(teamspaceNode, teamspaceId, name, description, spaceQuota, status, maxVersion, maxMembers, regionId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to update teamspace.");
			return ret;
		}
		return ret;
	}

	virtual int32_t getTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().getTeamspace(teamspaceNode, teamspaceId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to get teamspace.");
			return ret;
		}
		return ret;
	}

	virtual int32_t deleteTeamspace(const int64_t teamspaceId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().deleteTeamspace(teamspaceId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to delete teamspace.");
			return ret;
		}
		return ret;
	}

	virtual int32_t addTeamspaceMember(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role, 
		const int64_t memberId, 
		const TeamspaceMemberType memberType)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().addTeamspaceMember(teamspaceMembership, teamspaceId, teamRole, role, memberId, memberType);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to add teamspace member.");
			return ret;
		}
		return ret;
	}

	virtual int32_t getTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().getTeamspaceMembership(teamspaceMembership, teamspaceId, teamspaceMemebershiId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to get teamspace membership.");
			return ret;
		}
		return ret;
	}

	virtual int32_t updateTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().updateTeamspaceMembership(teamspaceMembership, teamspaceId, teamspaceMemebershiId, teamRole, role);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to update teamspace membership.");
			return ret;
		}
		return ret;
	}

	virtual int32_t listTeamspaceMembers(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t teamspaceId, 
		const std::string& keyword, 
		const TeamspaceRoleType roleType, 
		const int64_t offset, 
		const int32_t limit, 
		const std::string& orderField, 
		const std::string& orderDirection)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().listTeamspaceMembers(teamspaceMemberships, totalCount, teamspaceId, keyword, roleType, offset, limit, orderField, orderDirection);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to list teamspace members.");
			return ret;
		}
		return ret;
	}

	virtual int32_t deleteTeamspaceMember(const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId)
	{
		MAKE_CLIENT(client);
		int32_t ret = client().deleteTeamspaceMember(teamspaceId, teamspaceMemebershiId);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to delete teamspace member.");
			return ret;
		}
		return ret;
	}

private:
	UserContext* userContext_;
};

TeamspaceMgr* TeamspaceMgr::create(UserContext* userContext)
{
	return static_cast<TeamspaceMgr*>(new TeamspaceMgrImpl(userContext));
}
