#ifndef _NODECONTROLRESMGR_H_
#define _NODECONTROLRESMGR_H_

#include "UserContext.h"
#include "AccesNode.h"
#include "PermissionRole.h"
#include "Userinfo.h"
#include "PageParam.h"
#include "ServerSysConfig.h"

class ONEBOX_DLL_EXPORT NodeControlResMgr
{
	public:
	virtual ~NodeControlResMgr(){}
	static NodeControlResMgr* create(UserContext* userContext);

	virtual void listSystemRole(SysRoleInfoExList& nodes)=0;

	virtual void  getSystemRoleList(PermissionRoleArray& _return)=0;

	virtual void  deleteNodeAccesControl(const int64_t& ownerId,const int64_t& aclId)=0;

	virtual void  addNodeAccesControl(const int64_t& ownerId,const int64_t& id,const int64_t& nodeId,const std::string& type,const std::string& role,AccesNode& _return)=0;

	virtual void  updateNodeAccesControl(const int64_t& ownerId,const int64_t& aclId,const std::string& role,AccesNode& _return)=0;

	virtual void  listNodeAccesControl(const int64_t& ownerId,const int64_t& nodeId,const int32_t& offset,const int32_t& limit,int64_t& total,AccesNodeArray& _return)=0;

	virtual void  getNodeAccesControl(const int64_t& ownerId,const int64_t& nodeId,const int64_t& userId,AccesNode& _return)=0;
};

#endif