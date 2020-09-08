/******************************************************************************
Description  : ��ȡ����������
Created By   : l00100468
*******************************************************************************/
#ifndef __ONEBOX__SYSCONFIG__H__
#define __ONEBOX__SYSCONFIG__H__

#include "CommonValue.h"

class ServerSysConfig
{
public:
	ServerSysConfig()
	{
		loginFailNotify_ = false;
		loginFailNum_ = 0;
		notRemberMe_ = false;
		complexCode_ = false;
		backupAllowedRule_ = "";
		backupForbiddenRule_ = "";
	};

	virtual ~ServerSysConfig()
	{
	};

		FUNC_DEFAULT_SET_GET(bool, loginFailNotify)
		FUNC_DEFAULT_SET_GET(bool, notRemberMe)
		FUNC_DEFAULT_SET_GET(bool, complexCode)
		FUNC_DEFAULT_SET_GET(int32_t, loginFailNum)
		FUNC_DEFAULT_SET_GET(std::string, backupAllowedRule)
		FUNC_DEFAULT_SET_GET(std::string, backupForbiddenRule)

private:
	bool loginFailNotify_;	//��½ʧ��֪ͨ����Ա��
	bool notRemberMe_;		//�Ƿ��ס�û���½��Ϣ��
	bool complexCode_;			//������ȡ���Ƿ�Ϊǿ���Ӷȣ�
	int32_t loginFailNum_;				//����ʧ�ܴ�����
	std::string backupAllowedRule_;		//ȫ�̱��ݰ�����
	std::string backupForbiddenRule_;	//ȫ�̱��ݺ�����
};

#endif // end of defined __ONEBOX__SYSCONFIG__H__
