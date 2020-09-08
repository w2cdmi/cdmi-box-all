#ifndef __ONEBOX__SHARE_USER_INFO_H_
#define __ONEBOX__SHARE_USER_INFO_H_

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
};

typedef std::vector<ShareUserInfo> ShareUserInfoList;

#endif
