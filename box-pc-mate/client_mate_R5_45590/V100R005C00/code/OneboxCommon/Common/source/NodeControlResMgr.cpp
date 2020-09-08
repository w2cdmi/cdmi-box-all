#include "NodeControlResMgr.h"
#include "CommonDefine.h"
#include "Utility.h"
#include "NetworkMgr.h"
#include "UserInfoMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("NodeControlResMgr")
#endif

class NodeControlResMgrImpl : public NodeControlResMgr
{
public:
	NodeControlResMgrImpl(UserContext* userContext):userContext_(userContext)
	{
	}

	virtual void listSystemRole(SysRoleInfoExList& nodes)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().listSystemRole(nodes);
	}

	virtual void  getSystemRoleList(PermissionRoleArray& _return)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().getSystemRoleList(_return);
	}

	virtual void  deleteNodeAccesControl(const int64_t& ownerId,
		const int64_t& aclId)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().deleteNodeAccesControl(ownerId,aclId);
	}

	virtual void  addNodeAccesControl(const int64_t& ownerId,
		const int64_t& id,
		const int64_t& nodeId,
		const std::string& type,
		const std::string& role,
		AccesNode& _return)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().addNodeAccesControl(ownerId,id,nodeId,type,role,_return);
	}

	virtual void  updateNodeAccesControl(const int64_t& ownerId,
		const int64_t& aclId,
		const std::string& role,
		AccesNode& _return)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().updateNodeAccesControl(ownerId,aclId,role,_return);
	}

	virtual void  listNodeAccesControl(const int64_t& ownerId,
		const int64_t& nodeId,
		const int32_t& offset,
		const int32_t& limit,
		int64_t& total,
		AccesNodeArray& _return)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().listNodeAccesControl(ownerId,nodeId,offset,limit,total,_return);
	}

	virtual void  getNodeAccesControl(const int64_t& ownerId,
		const int64_t& nodeId,
		const int64_t& userId,
		AccesNode& _return)
	{
		int iRet = RT_OK;
		MAKE_CLIENT(client);

		iRet = client().getNodeAccesControl(ownerId,nodeId,userId,_return);
	}

private:
	UserContext* userContext_;
};

NodeControlResMgr* NodeControlResMgr::create(UserContext* userContext)
{
	return static_cast<NodeControlResMgr*>(new NodeControlResMgrImpl(userContext));
}