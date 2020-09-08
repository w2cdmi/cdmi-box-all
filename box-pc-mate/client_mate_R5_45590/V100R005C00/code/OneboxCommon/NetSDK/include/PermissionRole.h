#ifndef _PERMISSIONROLE_H_
#define _PERMISSIONROLE_H_

#include "CommonValue.h"
#include <vector>

class Permission
{
public:
	Permission()
	{
			browse_			= 0;
			preview_		= 0;
			download_		= 0;
			upload_			= 0;
			edit_			= 0;
			Delete_			= 0;
			publishLink_	= 0;
			authorize_		= 0;
	}
	~Permission(){}

	Permission& operator=(const Permission& rhs)
	{
		if(&rhs != this)
		{
			browse_			= rhs.browse();
			preview_		= rhs.preview();
			download_		= rhs.download();
			upload_			= rhs.upload();
			edit_			= rhs.edit();
			Delete_			= rhs.Delete();
			publishLink_	= rhs.publishLink();
			authorize_		= rhs.authorize();
		}
		return *this;
	}

	FUNC_DEFAULT_SET_GET(int32_t, browse)
	FUNC_DEFAULT_SET_GET(int32_t, preview)
	FUNC_DEFAULT_SET_GET(int32_t, download)
	FUNC_DEFAULT_SET_GET(int32_t, upload)
	FUNC_DEFAULT_SET_GET(int32_t, edit)
	FUNC_DEFAULT_SET_GET(int32_t, Delete)
	FUNC_DEFAULT_SET_GET(int32_t, publishLink)
	FUNC_DEFAULT_SET_GET(int32_t, authorize)
private:
	int32_t browse_;
	int32_t preview_;
	int32_t download_;
	int32_t upload_;
	int32_t edit_;
	int32_t Delete_;
	int32_t publishLink_;
	int32_t authorize_;
};

class PermissionRole
{
public:
	PermissionRole()
	{
			name_			= "";
			description_	= "";
			status_			= 0;
	}
	~PermissionRole(){}

	PermissionRole& operator=(const PermissionRole& rhs)
	{
		if(&rhs != this)
		{
			name_			= rhs.name();
			description_	= rhs.description();
			status_			= rhs.status();
			permission_		= rhs.permission_;
		}
	}

	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int32_t, status)
public:
	Permission permission_;
private:
	std::string name_;
	std::string description_;
	int32_t		status_;
};

typedef  std::vector<PermissionRole> PermissionRoleArray;

#endif