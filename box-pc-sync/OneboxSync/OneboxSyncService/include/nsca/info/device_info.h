#ifndef _NSCA_DEVICE_INFO_H_
#define _NSCA_DEVICE_INFO_H_
#include "../base.h"

//���е����Ծ�ʹ��UTF8����
/*!
	�豸���ƣ���windows��linux��������hostname��
*/
#define NSCA_DEVICE_PROP_HOST_NAME "nsca_device_name"

/*!
	�豸ΨһID��һ�㲻�ظ�����ȫ�ַ�Χ�ڲ��ų��豸id�ظ������
*/
#define NSCA_DEVICE_PROP_DEVICE_ID "nsca_device_id"

/*!
	�豸��ʷID����ȡ�豸ΨһID���㷨�п��ܷ����ı䡣
	���㷨�����ı��ͨ����key���Ի�ȡ֮ǰ���2���汾�㷨��Ӧ����ʷ�豸ID��
	��ͬID֮��ʹ��;�ָ�
*/
#define NSCA_DEVICE_PROP_HISTORY_DEVICE_ID "nsca_history_device_id"

/*!
	�����豸OS���ͣ���Ϊ��WINDOWS,IOS��ANDROID,LINUX��
*/
#define NSCA_DEVICE_PROP_OS	"nsca_device_os"

/*!
	�����豸OS�汾����ʽΪ��OS����.���汾.�ΰ汾.CPU���͡�
	OS���ͣ�windows,ios��android,linux��
	���汾������
	�ΰ汾����sp0��sp1��sp2��
	CPU���ͣ�x86,x64,arm��
*/
#define NSCA_DEVICE_PROP_OS_VERSION	"nsca_device_os_version"

/*!
	�ж��Ƿ�װ��spes�����
*/
#define NSCA_DEVICE_PROP_IS_SPES_INSTALLED	"nsca_device_is_spes_installed"
extern "C"
{

	/*!
		��ȡ�豸��ص���Ϣ
		@param key ������
		@param buffer [out] ����ֵ���ܻ��档����ֵΪ��\0��β���ַ�����
		@param bufferSize [in] �����С��Ӧ����\0�Ŀռ䡣			
		@param requiredBufferSize ��Ҫ�Ļ��ɴ�С����������\0�ַ�������Ҫ�Ļ����С��
		@return true �ɹ���false ʧ�ܡ��������false������requiredBufferSize С�ڵ��� bufferSize��˵��������Ϊ���泤�Ȳ���������������
	*/
	bool NSCA_COMPONENT_API nsca_get_device_info(const char* key,char* buffer,size_t bufferSize,size_t& requiredBufferSize);
};

#endif