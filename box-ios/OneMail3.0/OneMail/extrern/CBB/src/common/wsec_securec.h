/*lint -e526 -e553*/

#ifndef __WIRELESS_PRI_SECURE_C_H_D13A04GCRKLAPS32SF_4EHLPOC27
#define __WIRELESS_PRI_SECURE_C_H_D13A04GCRKLAPS32SF_4EHLPOC27

#if (!__STDC_WANT_SECURE_LIB__)
#include <stdio.h>
#include<stdarg.h>
#else
#include <memory.h>
#endif

#ifdef __cplusplus
extern "C"
{
#endif

#ifndef errno_t
typedef int errno_t;
#endif

#ifndef EOK
#define EOK (0)
#endif

/* ����C��ȫ�����⣬�����VS���뻷����������CBB�û��ṩ��ʵ��. */
#if (!__STDC_WANT_SECURE_LIB__)
/* 1 �ڴ���� */
extern errno_t memcpy_s(void* dest, size_t destMax, const void* src, size_t count);
extern errno_t memmove_s(void* dest, size_t destMax, const void* src, size_t count);

/* 2 �ַ����� */
extern errno_t strcpy_s(char* strDest, size_t destMax, const char* strSrc);
extern errno_t strncpy_s(char* strDest, size_t destMax, const char* strSrc, size_t count);
extern errno_t strcat_s(char* strDest, size_t destMax, const char* strSrc);
extern errno_t strncat_s(char* strDest, size_t destMax, const char* strSrc, size_t count);
extern char* strtok_s(char* strToken, const char* strDelimit, char** context);


/* 3 ��ʽ����� */
extern int sprintf_s(char* strDest, size_t destMax, const char* format, ...);
extern int vsprintf_s(char* strDest, size_t destMax, const char* format, va_list argptr);
extern int vsnprintf_s(char* strDest, size_t destMax, size_t count, const char* format, va_list arglist);
extern int snprintf_s(char* strDest, size_t destMax, size_t count, const char* format, ...);

/* 4 ��ʽ������ */
extern int scanf_s(const char* format, ...);
extern int vscanf_s(const char* format, va_list arglist);
extern int fscanf_s(FILE* stream, const char* format, ...);
extern int vfscanf_s(FILE* stream, const char* format, va_list arglist);
extern int sscanf_s(const char* buffer, const char* format, ...);
extern int vsscanf_s(const char* buffer, const char* format, va_list argptr);

/* 5 ��׼���������� */
extern char* gets_s(char* buffer, size_t destMax);
#endif /* (!__STDC_WANT_SECURE_LIB__) */

/* ��Ϊ���а�ȫ���� */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */
#endif/* __WIRELESS_PRI_SECURE_C_H_D13A04GCRKLAPS32SF_4EHLPOC27 */
