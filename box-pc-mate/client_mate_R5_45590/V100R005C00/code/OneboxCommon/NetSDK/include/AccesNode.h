#ifndef ACCESNODE_H_
#define ACCESNODE_H_

#include "CommonValue.h"
#include "PermissionRole.h"
#include <vector>

class AccesNodeResource
{

public:
	AccesNodeResource()
	{
		ownerId_	= 0L;
		nodeId_		= 0L;
	}
	~AccesNodeResource()
	{
	}

	AccesNodeResource& operator=(const AccesNodeResource& rhs)
	{
		if(&rhs != this)
		{
			ownerId_	= rhs.ownerId();
			nodeId_		= rhs.nodeId();
			permission_ = rhs.permission_;
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int64_t,ownerId);
	FUNC_DEFAULT_SET_GET(int64_t,nodeId);
public:
	Permission permission_;
private:
	int64_t ownerId_;
	int64_t nodeId_;
};

class AccesNodeUser
{
public:
	AccesNodeUser()
	{
		id_			= 0L;
		type_		= "";
		name_		= "";
		loginName_	= "";
		desciption_ = "";
	}

	~AccesNodeUser()
	{
	}

	AccesNodeUser& operator=(const AccesNodeUser& rhs)
	{
		if(&rhs != this)
		{
			id_			= rhs.id();
			type_		= rhs.type();
			name_		= rhs.name();
			loginName_	= rhs.loginName();
			desciption_ = rhs.desciption();
		}
		return *this;
	}
	
	FUNC_DEFAULT_SET_GET(int64_t,id);
	FUNC_DEFAULT_SET_GET(std::string,name);
	FUNC_DEFAULT_SET_GET(std::string,type);
	FUNC_DEFAULT_SET_GET(std::string,loginName);
	FUNC_DEFAULT_SET_GET(std::string,desciption);
private:
	int64_t id_;
	std::string type_;
	std::string name_;
	std::string loginName_;
	std::string desciption_;
};

class AccesNode
{
public:
	AccesNode()
	{
		id_		= 0L;
		role_	= "";
	}
	~AccesNode()
	{
	}

	AccesNode& operator=(const AccesNode& rhs)
	{
		if(&rhs != this)
		{
			id_			= rhs.id();
			role_		= rhs.role();
			resource_	= rhs.resource_;
			accesUser_	= rhs.accesUser_;
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int64_t,id);
	FUNC_DEFAULT_SET_GET(std::string,role);
public:
	AccesNodeResource	resource_;
	AccesNodeUser		accesUser_;
private:
	int64_t		id_;
	std::string role_;
};

typedef std::vector<AccesNode> AccesNodeArray;

#endif