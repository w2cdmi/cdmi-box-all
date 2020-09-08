#include "GroupResMgr.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("GroupResMgr")
#endif

class GroupResMgrImpl : public GroupResMgr
{
public:
	GroupResMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

virtual int32_t GroupResMgrImpl::GetGroupListUser(UserGroupNodeInfoArray& _return, 
											   PageParam& _pageparam,
											   const std::string keyword,
											   const std::string& type,
											   const std::string& listRole)
{
	int iRet = RT_OK;
	int64_t totalCnt = 0;

	MAKE_CLIENT(client);

	do 
	{
		iRet = client().getGroupListUser(userContext_->id.id,_pageparam,keyword,type,listRole,totalCnt,_return);
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

	return iRet;
}

virtual int32_t GroupResMgrImpl:: CreateGroup(GroupNode& _return, 
		const std::string& name, 
		const std::string& desc,
		const std::string& type,
		const std::string& status)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().createGroup(name,desc,type,status,_return);

	return iRet;
}

virtual int32_t GroupResMgrImpl::UpdateGroup(const int64_t& groupId, 
		const std::string& name,
		const std::string& desc,
		const std::string& type,
		const std::string& status)
{	
	int iRet = RT_OK;
	GroupNode tmp;
	MAKE_CLIENT(client);
	iRet = client().updateGroup(groupId,name,desc,type,status,tmp);

	return iRet;
}

virtual int32_t GroupResMgrImpl::GetGroup(GroupNode& _return, const int64_t& groupId)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

 	iRet = client().getGroup(groupId,_return);

	return iRet;
}

virtual int32_t GroupResMgrImpl::DeleteGroup(const int64_t& groupId)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().deleteGroup(groupId);
	return iRet;
}

virtual int32_t GroupResMgrImpl::AddGroupMember(UserGroupNodeInfo& _return,
		const int64_t& groupId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& groupRole)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().addGroupMember(groupId,member_type,member_id,groupRole,_return);

	return iRet;
}

virtual int32_t GroupResMgrImpl::DeleteGroupMember(const int64_t& groupId, 
		const int64_t& id)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().deleteGroupMember(groupId,id);
	return iRet;
}

virtual int32_t GroupResMgrImpl::UpdateGroupUserInfo(UserGroupNodeInfo& _return, 
		const int64_t& groupId, 
		const int64_t& id,
		const std::string& groupRole)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);

	iRet = client().updateGroupUserInfo(groupId,id,groupRole,_return);

	return iRet;
}

virtual int32_t GroupResMgrImpl::GetGroupListMemberInfo(const int64_t& groupId, 
		const std::string& keyword, 
		const std::string& groupRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserGroupNodeInfoArray& _return)
{
	int iRet = RT_OK;
	MAKE_CLIENT(client);
	int64_t offset=0;
	do 
	{	
		iRet = client().getGroupListMemberInfo(groupId,keyword,groupRole,pageParam,total,_return);
		
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

	return iRet;
}

private:
	UserContext* userContext_;
};

GroupResMgr* GroupResMgr::create(UserContext* userContext)
{
	return static_cast<GroupResMgr*>(new GroupResMgrImpl(userContext));
}
