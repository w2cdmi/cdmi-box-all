/******************************************************************************
Description  : Email����
Created By   : l00295403
*******************************************************************************/
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

	EmailParam():message(""),nodename(""),sender(""),
		type(0),ownerid(-1L),nodeid(-1L),
		plainaccesscode(""),start(0L),end(0L),linkurl("")
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

struct EmailInfoNode
{
	int64_t sender;		//��Ϣ������ID
	std::string source;	//�ʼ���Ϣ��Դ share or link
	int64_t ownedBy;	//��Դӵ����
	int64_t nodeId;		//�ļ��л��ļ�ID
	std::string subject;	//�ʼ�����
	std::string message;	//�ʼ�����

	EmailInfoNode():
		sender(0L),
		source(""),
		ownedBy(0L),
		nodeId(0L),
		subject(""),
		message("")
	{
	}
};

#endif // end of defined __ONEBOX__EMAILNODE__H__