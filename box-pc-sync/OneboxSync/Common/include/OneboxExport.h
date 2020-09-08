#ifndef _ONEBOX_EXPORT_H_
#define _ONEBOX_EXPORT_H_

//Windows平台导出宏定义
#if defined(_WIN32) && defined(ONEBOX_EXPORTS)
#pragma warning(push)
#pragma warning(disable:4251)   //屏蔽C4251警告，MS官方文档说明此警告可以忽略
#define ONEBOX_DLL_EXPORT __declspec(dllexport)
#else
#define ONEBOX_DLL_EXPORT
#endif

#endif
