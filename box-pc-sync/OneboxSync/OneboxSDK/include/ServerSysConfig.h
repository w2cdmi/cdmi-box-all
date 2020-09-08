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
	};

	virtual ~ServerSysConfig()
	{
	};

		FUNC_DEFAULT_SET_GET(bool, loginFailNotify)
		FUNC_DEFAULT_SET_GET(bool, notRemberMe)
		FUNC_DEFAULT_SET_GET(bool, complexCode)
		FUNC_DEFAULT_SET_GET(int32_t, loginFailNum)

private:
	bool loginFailNotify_;	//��½ʧ��֪ͨ����Ա��
	bool notRemberMe_;		//�Ƿ��ס�û���½��Ϣ��
	bool complexCode_;			//������ȡ���Ƿ�Ϊǿ���Ӷȣ�
	int32_t loginFailNum_;				//����ʧ�ܴ�����
};

#endif // end of defined __ONEBOX__SYSCONFIG__H__
