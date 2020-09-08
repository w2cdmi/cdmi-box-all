#ifndef _SHARE_USER_INFO_
#define _SHARE_USER_INFO_

/******************************************************************************
Description  : 共享用户信息
Created By   : dailinye
*******************************************************************************/

#include "CommonValue.h"
#include <vector>

class ShareUserInfo
{
public:
	ShareUserInfo()
		:department_("")
		,domain_("")
		,email_("")
		,id_(0L)
		,label_("")
		,loginName_("")
		,name_("")
		,objectSid_("")
		,recycleDays_(0)
		,regionId_(0)
		,spaceQuota_(0L)
		,spaceUsed_(0L)
		,status_("")
		,type_(0)
		,accountId_()
		,roleName_("viewer")
	{
	}

	virtual ~ShareUserInfo()
	{

	}

	FUNC_DEFAULT_SET_GET(std::string, department)
	FUNC_DEFAULT_SET_GET(std::string, domain)
	FUNC_DEFAULT_SET_GET(std::string, email)
	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, label)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, objectSid)
	FUNC_DEFAULT_SET_GET(int32_t, recycleDays)
	FUNC_DEFAULT_SET_GET(int32_t, regionId)
	FUNC_DEFAULT_SET_GET(int64_t, spaceQuota)
	FUNC_DEFAULT_SET_GET(int64_t, spaceUsed)
	FUNC_DEFAULT_SET_GET(std::string, status)
	FUNC_DEFAULT_SET_GET(int32_t, type)
	FUNC_DEFAULT_SET_GET(int64_t, accountId)
	FUNC_DEFAULT_SET_GET(std::string, roleName)

	ShareUserInfo& operator=(const ShareUserInfo &rhs)
	{
		if (&rhs != this)
		{
			department_ = rhs.department();			
			domain_ = rhs.domain();
			email_ = rhs.email();
			id_ = rhs.id();
			label_ = rhs.label();
			loginName_ = rhs.loginName();
			name_ = rhs.name();
			objectSid_ = rhs.objectSid();
			recycleDays_ = rhs.recycleDays();
			regionId_ = rhs.regionId();
			spaceQuota_ = rhs.spaceQuota();
			spaceUsed_ = rhs.spaceUsed();
			status_ = rhs.status();
			type_ = rhs.type();
			accountId_ = rhs.accountId();
			roleName_ = rhs.roleName();
		}
		return *this;
	}
private:
	std::string department_; // 部门
	std::string domain_; // 用户所在区域
	std::string email_; // 用户邮箱
	int64_t id_; // 用户id，为开通返回0，非实际id
	std::string label_;	//	用户全名
	std::string loginName_; // 用户登录名
	std::string name_; // 用户全名
	std::string objectSid_; // 用户sid信息
	int32_t recycleDays_; // 用户密码过期时间
	int32_t regionId_; // 用户区域ID
	int64_t spaceQuota_; // 用户配额
	int64_t spaceUsed_; // 用户已使用空间
	std::string status_; // 用户状态："enable"表示启用状态
	int32_t type_; // 0表示已开户 1表示未开户
	int64_t accountId_; // 企业应用账户
	std::string roleName_;	//用户权限
};

typedef std::vector<ShareUserInfo> ShareUserInfoList;

#endif
