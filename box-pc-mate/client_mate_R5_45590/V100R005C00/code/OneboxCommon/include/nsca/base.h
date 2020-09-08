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

//错误码。如果错误码未在此定义，则是系统错误，请参考MSDN winerror.h 中ERROR_*开头的错误码定义
//nsca相关函数调用失败时，可以通过调用GetLastError获取详细的错误码

#endif