#include "TeamSpaceResMgr.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("TeamSpaceMgr")
#endif

class TeamSpaceResMgrImpl : public TeamSpaceResMgr
{
public:
	TeamSpaceResMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

virtual void TeamSpaceResMgrImpl::getTeamSpaceListUser(UserTeamSpaceNodeInfoArray & _return, PageParam& _pageparam )
{
	int iRet = RT_OK;
	int64_t totalCnt = 0;

	MAKE_CLIENT(client);

	do 
	{
		iRet = client().getTeamSpaceListUser(userContext_->id.id,_pageparam,totalCnt,_return);
		if (iRet != RT_OK)
		{
			break;
		}
		_pageparam.offset += _pageparam.limit;
		if(totalCnt <= _pageparam.offset)
		{
			_pageparam.offset = 0;
		}
	} while (_pageparam.offset != 0);
}

virtual void TeamSpaceResMgrImpl::CreateTeamSpace(TeamSpacesNode& _return, const std::string& name, const std::string& desc, const int64_t spaceQuota, const int32_t status, const int32_t maxVersions)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().createTeamSpace(name,desc,spaceQuota,status,maxVersions,_return);

}

virtual int32_t TeamSpaceResMgrImpl::UpdateTeamSpace(const int64_t& teamId, const std::string& name, const std::string& desc, const int64_t spaceQuota, const int32_t status)
{
	
	int iRet = RT_OK;
	TeamSpacesNode tmp;
	MAKE_CLIENT(client);
	iRet = client().updateTeamSpace(teamId,name,desc,spaceQuota,status,tmp);
	if (iRet ==RT_OK)
	{
		//do something
		//userContext_->getDataBaseMgr()->getTeamSpacesTable()->updataTeamSpaces(tmp,teamId);
	}

	return iRet;
}

virtual void TeamSpaceResMgrImpl::getTeamSpace(TeamSpacesNode& _return, const int64_t& teamId)
{

	int iRet = RT_OK;
	MAKE_CLIENT(client);

 	iRet = client().getTeamSpace(teamId,_return);

}

virtual int32_t TeamSpaceResMgrImpl::deleteTeamSpace(const int64_t& teamId)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().deleteTeamSpace(teamId);
	return iRet;
}

virtual void TeamSpaceResMgrImpl::addTeamSpaceMember(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const std::string& member_type, const int64_t& member_id, const std::string& teamRole, const std::string& role)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().addTeamSpaceMember(teamId,member_type,member_id,teamRole,role,_return);

}

virtual void TeamSpaceResMgrImpl::getTeamSpaceMemberInfo(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const std::string& id)
{

	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().getTeamSpaceMemberInfo(teamId,id,_return);
}

virtual void TeamSpaceResMgrImpl::UpdateTeamSpaceUserInfo(UserTeamSpaceNodeInfo& _return, const int64_t& teamId, const int64_t& id, const std::string& teamRole, const std::string& role)
{

	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().updateTeamSpaceUserInfo(teamId,id,teamRole,role,_return);
}

virtual void TeamSpaceResMgrImpl::getTeamSpaceListMemberInfo(const int64_t& teamId, 
		const std::string& keyword, 
		const std::string& teamRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserTeamSpaceNodeInfoArray& _return)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);
	int64_t offset=0;
	do 
	{	
		iRet = client().getTeamSpaceListMemberInfo(teamId,keyword,teamRole,pageParam,total,_return);
		
		if (iRet != RT_OK)
		{
			break;
		}

		offset += pageParam.limit;
		if( total <= offset)
		{
			offset = 0;
		}
	} while (offset != 0);
}

virtual int32_t TeamSpaceResMgrImpl::DeleteTeamSpaceMember(const int64_t& teamId, const int64_t& id)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().deleteTeamSpaceMember(teamId,id);
	return iRet;
}
private:
	UserContext* userContext_;
};

TeamSpaceResMgr* TeamSpaceResMgr::create(UserContext* userContext)
{
	return static_cast<TeamSpaceResMgr*>(new TeamSpaceResMgrImpl(userContext));
}
