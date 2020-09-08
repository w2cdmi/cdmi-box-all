#ifndef __ONEBOX__EMAILNODE__H__
#define __ONEBOX__EMAILNODE__H__

#include "CommonValue.h"

struct EmailParam
{
	std::string message;	//�ʼ��Զ�����Ϣ����
	std::string nodename;	//�ļ����ļ�������
	std::string sender;		//������ȫ��
	int32_t	type;			//�����ļ�����
	int64_t ownerid;		//������ID
	int64_t nodeid;			//file/folder ID
	std::string plainaccesscode;	//������ȡ��
	int64_t start;		//������Чʱ��
	int64_t end;		//����ʧЧʱ��
	std::string linkurl;	//��������url

	EmailParam():message(""),nodename(""),sender(""),plainaccesscode(""),start(0L),end(0L),linkurl("")
	{
	}
};

struct EmailNode
{
	std::string type;	//�ʼ�����
	std::string mailto;	//�ռ��˵�������
	std::string copyto;	//�����˵�������
	EmailParam email_param;	//�����б�
};

#endif // end of defined __ONEBOX__EMAILNODE__H__
