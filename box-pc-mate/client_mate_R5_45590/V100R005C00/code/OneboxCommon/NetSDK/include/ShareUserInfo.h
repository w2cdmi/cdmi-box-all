#ifndef _SHARE_USER_INFO_
#define _SHARE_USER_INFO_

/******************************************************************************
Description  : �����û���Ϣ
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
	std::string department_; // ����
	std::string domain_; // �û���������
	std::string email_; // �û�����
	int64_t id_; // �û�id��Ϊ��ͨ����0����ʵ��id
	std::string label_;	//	�û�ȫ��
	std::string loginName_; // �û���¼��
	std::string name_; // �û�ȫ��
	std::string objectSid_; // �û�sid��Ϣ
	int32_t recycleDays_; // �û��������ʱ��
	int32_t regionId_; // �û�����ID
	int64_t spaceQuota_; // �û����
	int64_t spaceUsed_; // �û���ʹ�ÿռ�
	std::string status_; // �û�״̬��"enable"��ʾ����״̬
	int32_t type_; // 0��ʾ�ѿ��� 1��ʾδ����
	int64_t accountId_; // ��ҵӦ���˻�
	std::string roleName_;	//�û�Ȩ��
};

typedef std::vector<ShareUserInfo> ShareUserInfoList;

#endif
