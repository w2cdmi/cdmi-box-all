#ifndef _ONEBOX_EXPORT_H_
#define _ONEBOX_EXPORT_H_

//Windowsƽ̨�����궨��
#if defined(_WIN32) && defined(ONEBOX_EXPORTS)
#pragma warning(push)
#pragma warning(disable:4251)   //����C4251���棬MS�ٷ��ĵ�˵���˾�����Ժ���
#define ONEBOX_DLL_EXPORT __declspec(dllexport)
#else
#define ONEBOX_DLL_EXPORT
#endif

#endif
