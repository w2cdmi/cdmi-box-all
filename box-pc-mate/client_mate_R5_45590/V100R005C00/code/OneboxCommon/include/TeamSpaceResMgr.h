#ifndef _TEAMSPACE_RES_MGR_H_
#define _TEAMSPACE_RES_MGR_H_

#include "UserContext.h"
#include "TeamSpacesNode.h"
#include "PageParam.h"
#include "ServerSysConfig.h"

class ONEBOX_DLL_EXPORT TeamSpaceResMgr
{
public:
	virtual ~TeamSpaceResMgr(){}
	static TeamSpaceResMgr* create(UserContext* userContext);
		//ме╤с©у╪Д
	virtual void getTeamSpaceListUser(UserTeamSpaceNodeInfoArray & _return, PageParam& _pageparam )=0;
	virtual void CreateTeamSpace(TeamSpacesNode& _return, const std::string& name, const std::string& desc, const int64_t spaceQuota, const int32_t status, const int32_t maxVersions)=0;
	virtual int32_t UpdateTeamSpace(const int64_t& teamId, const std::string& name, const std::string& desc, const int64_t spaceQuota, const int32_t status)=0;
	virtual void getTeamSpace(TeamSpacesNode& _return, const int64_t& teamId)=0;
	virtual int32_t deleteTeamSpace(const int64_t& teamId)=0;
	virtual void addTeamSpaceMember(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const std::string& member_type, const int64_t& member_id, const std::string& teamRole, const std::string& role)=0;
	virtual void getTeamSpaceMemberInfo(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const std::string& id)=0;
	virtual void UpdateTeamSpaceUserInfo(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const int64_t& id, const std::string& teamRole, const std::string& role)=0;
	virtual void getTeamSpaceListMemberInfo(const int64_t& teamId, 
		const std::string& keyword, 
		const std::string& teamRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserTeamSpaceNodeInfoArray& _return)=0;
	virtual int32_t DeleteTeamSpaceMember(const int64_t& teamId, const int64_t& id)=0;

};

#endif
