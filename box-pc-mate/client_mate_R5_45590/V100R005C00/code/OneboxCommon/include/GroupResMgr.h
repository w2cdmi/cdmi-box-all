#ifndef _GROUP_RES_MGR_H_
#define _GROUP_RES_MGR_H_

#include "UserContext.h"
#include "GroupNode.h"
#include "PageParam.h"
#include "ServerSysConfig.h"

class ONEBOX_DLL_EXPORT GroupResMgr
{
public:
	virtual ~GroupResMgr(){}
	static GroupResMgr* create(UserContext* userContext);

	virtual int32_t GetGroupListUser(UserGroupNodeInfoArray& _return,
		PageParam& _pageparam,
		const std::string keyword,
		const std::string& type		= "all",
		const std::string& listRole = "false")=0;

	virtual int32_t CreateGroup(GroupNode& _return, 
		const std::string& name, 
		const std::string& desc,
		const std::string& type		= "private",
		const std::string& status	= "enable")=0;

	virtual int32_t UpdateGroup(const int64_t& groupId, 
		const std::string& name,
		const std::string& desc,
		const std::string& type,
		const std::string& status)=0;

	virtual int32_t GetGroup(GroupNode& _return, 
		const int64_t& groupId)=0;

	virtual int32_t DeleteGroup(const int64_t& groupId)=0;

	virtual int32_t AddGroupMember(UserGroupNodeInfo& _return,
		const int64_t& groupId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& groupRole)=0;

	virtual int32_t DeleteGroupMember(const int64_t& groupId, 
		const int64_t& id)=0;

	virtual int32_t UpdateGroupUserInfo(UserGroupNodeInfo& _return, 
		const int64_t& groupId, 
		const int64_t& id,
		const std::string& groupRole)=0;

	virtual int32_t GetGroupListMemberInfo(const int64_t& groupId, 
		const std::string& keyword, 
		const std::string& groupRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserGroupNodeInfoArray& _return)=0;
};

#endif 