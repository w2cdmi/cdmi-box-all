#ifndef _NSCA_BASE_H_
#define _NSCA_BASE_H_

#ifndef NSCA_COMPONENT_API
#if defined(_WIN32) || defined(_WIN64)
	#ifdef NSCA_COMPONENT_IMPL
	#define NSCA_COMPONENT_API __declspec(dllexport)
	#else
	#define NSCA_COMPONENT_API
	#endif
#else
   #define NSCA_COMPONENT_API
#endif
#endif

//�����롣���������δ�ڴ˶��壬����ϵͳ������ο�MSDN winerror.h ��ERROR_*��ͷ�Ĵ����붨��
//nsca��غ�������ʧ��ʱ������ͨ������GetLastError��ȡ��ϸ�Ĵ�����

#endif