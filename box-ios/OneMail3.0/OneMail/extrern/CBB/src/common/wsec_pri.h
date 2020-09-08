/*lint -u -e526 -e960 */
/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_Pri.h
* Decription: 
    ��CBB�乫���ӿڣ��������⿪��.
*********************************************************************************/
#ifndef __WIRELESS_PRI_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27
#define __WIRELESS_PRI_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27

#include "wsec_config.h"
#include "wsec_itf.h"
#include "wsec_securec.h"
#include "cac_pri.h"
#include <stdarg.h>
#include <memory.h>

#include <stdlib.h>
#include <stdio.h>

#ifdef WSEC_DEBUG
    #include <assert.h>
#endif
#include <time.h>

#ifdef __cplusplus
extern "C"
{
#endif

/*lint -e666 */
/*=======================================================================
    0. ���뿪���໥��Ĺ����Ϸ��Լ��
=======================================================================*/
/* 1) WSEC_TRACE_MEMORY ֻ���� WSEC_DEBUG ������ */
#ifndef WSEC_DEBUG
    #ifdef WSEC_TRACE_MEMORY
        #undef WSEC_TRACE_MEMORY
    #endif
#endif

/* 2) WIN32��Linux����ͬʱ�� */
#ifdef WSEC_WIN32
#ifdef WSEC_LINUX
    #error Cannot defined both 'WSEC_LINUX' and 'WSEC_WIN32'.
#endif
#endif

/* 3) iPSI��OpenSSL����ֻ��ָ����һ */
#ifdef WSEC_COMPILE_CAC_IPSI
#ifdef WSEC_COMPILE_CAC_OPENSSL
#error Cannot defined both 'WSEC_COMPILE_CAC_IPSI' and 'WSEC_COMPILE_CAC_OPENSSL'
#endif
#endif

#ifndef WSEC_COMPILE_CAC_IPSI
#ifndef WSEC_COMPILE_CAC_OPENSSL
#error 'WSEC_COMPILE_CAC_IPSI' or 'WSEC_COMPILE_CAC_OPENSSL' must be defined.
#endif
#endif


/*=======================================================================
    1. �궨��
=======================================================================*/
/* 1.1 ������ */
#define WSEC_LOG_BUFF_SIZE    (512)  /* һ����־����󳤶� */
#define WSEC_FILE_IO_SIZE_MAX (4096) /* һ���ļ�IO����󳤶� */

/* 1.2 ���ʽ�� */
#define WSEC_IN_SCOPE(x, min, max) (((min) <= (x)) && ((x) <= (max)))
#define WSEC_OUT_OF_SCOPE(x, min, max) (((x) < (min)) || ((max) < (x)))
#define WSEC_IS2(x, is1, is2) ((x) == (is1) || (x) == (is2))
#define WSEC_IS3(x, is1, is2, is3) ((x) == (is1) || (x) == (is2) || (x) == (is3))

/* �ж�����Ƿ����� */
#define WSEC_IS_NORMAL_YEAR(y) ((y) > 2000)

/* ��Ч���ַ����Ƿ�Ϊ�յ��жϷ�������Ϊ�������ʽʹ�� ---- j00265291 2014-8-5 */
#define WSEC_IS_EMPTY_STRING(str)       ('\0' == str[0])
#define WSEC_NOT_IS_EMPTY_STRING(str)   ('\0' != str[0])

/* ָ����򣺾�Ϊ�ջ���ǿ���ȡ0, ����ȡ1, ���������ʽʹ�� */
#define WSEC_PTR_XOR(p1, p2) (((p1) ? 1 : 0) ^ ((p2) ? 1 : 0))

/* ��������Ԫ�ظ�������Ϊ���ͱ��ʽʹ�� ---- j00265291 2014-8-5 */
#define WSEC_NUM_OF(arr)        (sizeof(arr) / sizeof(arr[0]))

/* 1.3 ��־������ */
#define WSEC_LOG(eLevel, string) WSEC_WriLog(__FILE__, __LINE__, eLevel, "%s", string)
#define WSEC_LOG1(eLevel, format, p1) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1)
#define WSEC_LOG2(eLevel, format, p1, p2) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2)
#define WSEC_LOG3(eLevel, format, p1, p2, p3) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3)
#define WSEC_LOG4(eLevel, format, p1, p2, p3, p4) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4)
#define WSEC_LOG5(eLevel, format, p1, p2, p3, p4, p5) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4, p5)
#define WSEC_LOG6(eLevel, format, p1, p2, p3, p4, p5, p6) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4, p5, p6)
#define WSEC_LOG7(eLevel, format, p1, p2, p3, p4, p5, p6, p7) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4, p5, p6, p7)
#define WSEC_LOG8(eLevel, format, p1, p2, p3, p4, p5, p6, p7, p8) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4, p5, p6, p7, p8)
#define WSEC_LOG9(eLevel, format, p1, p2, p3, p4, p5, p6, p7, p8, p9) WSEC_WriLog(__FILE__, __LINE__, eLevel, format, p1, p2, p3, p4, p5, p6, p7, p8, p9)

/* 1) ��������־ */
#define WSEC_LOG_E(string) WSEC_LOG(WSEC_LOG_ERR, string)
#define WSEC_LOG_E1(Format, p1) WSEC_LOG1(WSEC_LOG_ERR, Format, p1)
#define WSEC_LOG_E2(Format, p1, p2) WSEC_LOG2(WSEC_LOG_ERR, Format, p1, p2)
#define WSEC_LOG_E3(Format, p1, p2, p3) WSEC_LOG3(WSEC_LOG_ERR, Format, p1, p2, p3)
#define WSEC_LOG_E4(Format, p1, p2, p3, p4) WSEC_LOG4(WSEC_LOG_ERR, Format, p1, p2, p3, p4)
#define WSEC_LOG_E5(Format, p1, p2, p3, p4, p5) WSEC_LOG5(WSEC_LOG_ERR, Format, p1, p2, p3, p4, p5)
#define WSEC_LOG_E6(Format, p1, p2, p3, p4, p5, p6) WSEC_LOG6(WSEC_LOG_ERR, Format, p1, p2, p3, p4, p5, p6)
#define WSEC_LOG_E7(Format, p1, p2, p3, p4, p5, p6, p7) WSEC_LOG7(WSEC_LOG_ERR, Format, p1, p2, p3, p4, p5, p6, p7)
#define WSEC_LOG_E8(Format, p1, p2, p3, p4, p5, p6, p7, p8) WSEC_LOG8(WSEC_LOG_ERR, Format, p1, p2, p3, p4, p5, p6, p7, p8)
#define WSEC_LOG_E9(Format, p1, p2, p3, p4, p5, p6, p7, p8, p9) WSEC_LOG9(WSEC_LOG_ERR, Format, p1, p2, p3, p4, p5, p6, p7, p8, p9)

/* 2) ��������־ */
#define WSEC_LOG_W(string) WSEC_LOG(WSEC_LOG_WARN, string)
#define WSEC_LOG_W1(Format, p1) WSEC_LOG1(WSEC_LOG_WARN, Format, p1)
#define WSEC_LOG_W2(Format, p1, p2) WSEC_LOG2(WSEC_LOG_WARN, Format, p1, p2)
#define WSEC_LOG_W3(Format, p1, p2, p3) WSEC_LOG3(WSEC_LOG_WARN, Format, p1, p2, p3)
#define WSEC_LOG_W4(Format, p1, p2, p3, p4) WSEC_LOG4(WSEC_LOG_WARN, Format, p1, p2, p3, p4)
#define WSEC_LOG_W5(Format, p1, p2, p3, p4, p5) WSEC_LOG5(WSEC_LOG_WARN, Format, p1, p2, p3, p4, p5)
#define WSEC_LOG_W6(Format, p1, p2, p3, p4, p5, p6) WSEC_LOG6(WSEC_LOG_WARN, Format, p1, p2, p3, p4, p5, p6)
#define WSEC_LOG_W7(Format, p1, p2, p3, p4, p5, p6, p7) WSEC_LOG7(WSEC_LOG_WARN, Format, p1, p2, p3, p4, p5, p6, p7)
#define WSEC_LOG_W8(Format, p1, p2, p3, p4, p5, p6, p7, p8) WSEC_LOG8(WSEC_LOG_WARN, Format, p1, p2, p3, p4, p5, p6, p7, p8)
#define WSEC_LOG_W9(Format, p1, p2, p3, p4, p5, p6, p7, p8, p9) WSEC_LOG9(WSEC_LOG_WARN, Format, p1, p2, p3, p4, p5, p6, p7, p8, p9)

/* 3) ��ʾ����־ */
#define WSEC_LOG_I(string) WSEC_LOG(WSEC_LOG_INFO, string)
#define WSEC_LOG_I1(Format, p1) WSEC_LOG1(WSEC_LOG_INFO, Format, p1)
#define WSEC_LOG_I2(Format, p1, p2) WSEC_LOG2(WSEC_LOG_INFO, Format, p1, p2)
#define WSEC_LOG_I3(Format, p1, p2, p3) WSEC_LOG3(WSEC_LOG_INFO, Format, p1, p2, p3)
#define WSEC_LOG_I4(Format, p1, p2, p3, p4) WSEC_LOG4(WSEC_LOG_INFO, Format, p1, p2, p3, p4)
#define WSEC_LOG_I5(Format, p1, p2, p3, p4, p5) WSEC_LOG5(WSEC_LOG_INFO, Format, p1, p2, p3, p4, p5)
#define WSEC_LOG_I6(Format, p1, p2, p3, p4, p5, p6) WSEC_LOG6(WSEC_LOG_INFO, Format, p1, p2, p3, p4, p5, p6)
#define WSEC_LOG_I7(Format, p1, p2, p3, p4, p5, p6, p7) WSEC_LOG7(WSEC_LOG_INFO, Format, p1, p2, p3, p4, p5, p6, p7)
#define WSEC_LOG_I8(Format, p1, p2, p3, p4, p5, p6, p7, p8) WSEC_LOG8(WSEC_LOG_INFO, Format, p1, p2, p3, p4, p5, p6, p7, p8)
#define WSEC_LOG_I9(Format, p1, p2, p3, p4, p5, p6, p7, p8, p9) WSEC_LOG9(WSEC_LOG_INFO, Format, p1, p2, p3, p4, p5, p6, p7, p8, p9)

/* 4) ���ֲ���ʧ�ܵ���־ */
#define WSEC_LOG_E4MALLOC(nSize) WSEC_LOG_E1("Allocate Memory(size=%u) fail.", nSize) /* �ڴ����ʧ�� */
#define WSEC_LOG_E4MEMCPY        WSEC_LOG_E("copy memory fail.") /* �ڴ濽��ʧ�� */

/* 5) DEBUG��TRACE */
#ifdef WSEC_DEBUG
    #define WSEC_TRACE(string) WSEC_LOG_I(string)
    #define WSEC_TRACE1(format, p1) WSEC_LOG_I1(format, p1)
    #define WSEC_TRACE2(format, p1, p2) WSEC_LOG_I2(format, p1, p2)
    #define WSEC_TRACE3(format, p1, p2, p3) WSEC_LOG_I3(format, p1, p2, p3)
    #define WSEC_TRACE4(format, p1, p2, p3, p4) WSEC_LOG_I4(format, p1, p2, p3, p4)
    #define WSEC_TRACE5(format, p1, p2, p3, p4, p5) WSEC_LOG_I5(format, p1, p2, p3, p4, p5)
#else
    #define WSEC_TRACE(strInfo) 
    #define WSEC_TRACE1(format, p1) 
    #define WSEC_TRACE2(format, p1, p2) 
    #define WSEC_TRACE3(format, p1, p2, p3) 
    #define WSEC_TRACE4(format, p1, p2, p3, p4) 
    #define WSEC_TRACE5(format, p1, p2, p3, p4, p5) 
#endif

/* disable warnings under different compiler  ---- j00265291 2014-9-15 */
#ifdef _MSC_VER
# define WSEC_DISABLE_WARNING_BEGIN(WarnId) \
    __pragma(warning(push)) \
    __pragma(warning(disable:WarnId)) 
# define WSEC_DISABLE_WARNING_END   __pragma(warning(pop))
#else
# define WSEC_DISABLE_WARNING_BEGIN(WarnId) 
# define WSEC_DISABLE_WARNING_END
#endif

#define oper_null 0
#define do_end while(WSEC_FALSE_FOREVER) /* �˱��ʽ��Ŀ����������"conditional expression is constant" ����澯 */
#define break_oper_if(condition, oper1, oper2) if(condition){oper1; oper2; break;}else{;}
#define continue_if(condition) if (condition){continue;}else{;}
#define return_oper_if(condition, oper, ret_code) do{if(condition) {oper; return ret_code;}else{;}}do_end
#define if_oper(condition, oper) do{if(condition){oper;}else{;}}do_end

#ifdef WSEC_DEBUG
#define return_err_if_para_invalid(func_name, valid_condition) do{if(!(valid_condition)){WSEC_LOG_E1("The para for %s invalid.", func_name);return WSEC_ERR_INVALID_ARG;}else{;}}do_end
#else
#define return_err_if_para_invalid(func_name, valid_condition) do{if(!(valid_condition)){WSEC_LOG_E("The function's para invalid.");return WSEC_ERR_INVALID_ARG;}else{;}}do_end
#endif

#define WSEC_UNCARE(condition) do{if(condition){;}else{;}}do_end

/* assign a value to a pointer safely ---- j00265291 2014-8-5 */
#define WSEC_SAFE_ASSIGN(ptr, val)      do {if (WSEC_NULL_PTR != (ptr)){*(ptr) = (val);}else{;}} do_end

#define WSEC_UNREFER(v) (v)=(v) /* ��ʱ���õı���/������ʹ�ô˺���"not referenced"����澯 */

#ifdef WSEC_DEBUG
    #define WSEC_ASSERT(b) do{if (!(b)){WSEC_LOG_E2("Assert fail at %s, Line %d", __FILE__, __LINE__); assert(b);}}do_end
#else
    #define WSEC_ASSERT(b)
#endif
#define WSEC_ASSERT_FALSE WSEC_ASSERT(WSEC_FALSE_FOREVER)
#ifdef WSEC_DEBUG
#define WSEC_VERIFY(b) assert(b)
#else
#define WSEC_VERIFY(b) (b)
#endif

#define WSEC_MALLOC(size) WSEC_MemAlloc(size, __FILE__, __LINE__)
#define WSEC_FREE(pAddr)  do{pAddr = WSEC_MemFree(pAddr, __FILE__, __LINE__); WSEC_UNREFER(pAddr);}do_end
#define WSEC_FREE_S(pAddr, nSize) do{if (pAddr) {WSEC_MEMSET(pAddr, nSize, 0, nSize); WSEC_MemFree(pAddr, __FILE__, __LINE__); pAddr = WSEC_NULL_PTR;}}do_end

#define WSEC_CLONE_BUFF(pCloneFrom, nSize) WSEC_BuffClone(pCloneFrom, nSize, __FILE__, __LINE__)
#define WSEC_CLONE_STR(pszCloneFrom) WSEC_StringClone(pszCloneFrom, __FILE__, __LINE__)

#define WSEC_MEMCMP(Buf1, Buf2, uiCount) g_RegFun.stMemory.pfMemCmp(Buf1, Buf2, uiCount)
#define WSEC_MEMCPY(dest, destMax, src, count) memcpy_s(dest, destMax, src, count)
#define WSEC_MEMSET(dest, destMax, c, count) memset_s(dest, destMax, c, count)

/*
#define WSEC_FOPEN(filename, mode)               (g_RegFun.stFile.pfFopen ? g_RegFun.stFile.pfFopen(filename, mode) : fopen(filename, mode))
#define WSEC_FCLOSE(stream)                      ((stream) ? (g_RegFun.stFile.pfFclose ? g_RegFun.stFile.pfFclose(stream) : fclose(stream)) : 0)
#define WSEC_FREAD(buffer, size, count, stream)  (g_RegFun.stFile.pfFread ? g_RegFun.stFile.pfFread(buffer, size, count, stream) : fread(buffer, size, count, stream))
#define WSEC_FWRITE(buffer, size, count, stream) (g_RegFun.stFile.pfFwrite ? g_RegFun.stFile.pfFwrite(buffer, size, count, stream) : fwrite(buffer, size, count, stream))
#define WSEC_FREMOVE(path)                 (g_RegFun.stFile.pfFremove ? g_RegFun.stFile.pfFremove(path) : remove(path))
#define WSEC_FFLUSH(stream)                (stream ? (g_RegFun.stFile.pfFflush ? g_RegFun.stFile.pfFflush(stream) : fflush(stream)) : 0)
#define WSEC_FGETC(stream)                 (g_RegFun.stFile.pfFgetc ? g_RegFun.stFile.pfFgetc(stream) : fgetc(stream))
#define WSEC_FGETS(string, n, stream)      (g_RegFun.stFile.pfFgets ? g_RegFun.stFile.pfFgets(string, n, stream) : fgets(string, n, stream))
#define WSEC_FTELL(stream)                 (g_RegFun.stFile.pfFtell ? g_RegFun.stFile.pfFtell(stream) : ftell(stream))
#define WSEC_FSEEK(stream, offset, origin) (g_RegFun.stFile.pfFseek ? g_RegFun.stFile.pfFseek(stream, offset, origin) : fseek(stream, offset, origin))
#define WSEC_FEOF(stream)                  (g_RegFun.stFile.pfFeof ? g_RegFun.stFile.pfFeof(stream) : feof(stream))
#define WSEC_FERROR(stream)                (g_RegFun.stFile.pfFerror ? g_RegFun.stFile.pfFerror(stream) : ferror(stream))
*/
#define WSEC_FOPEN(filename, mode)               (g_RegFun.stFile.pfFopen(filename, mode))
#define WSEC_FCLOSE(stream)                      ((stream) ? g_RegFun.stFile.pfFclose(stream) : 0)
#define WSEC_FREAD(buffer, size, count, stream)  (g_RegFun.stFile.pfFread(buffer, size, count, stream))
#define WSEC_FWRITE(buffer, size, count, stream) (g_RegFun.stFile.pfFwrite(buffer, size, count, stream))
#define WSEC_FREMOVE(path)                 (g_RegFun.stFile.pfFremove(path))
#define WSEC_FFLUSH(stream)                (stream ? g_RegFun.stFile.pfFflush(stream) : 0)
#define WSEC_FGETC(stream)                 (g_RegFun.stFile.pfFgetc(stream))
#define WSEC_FGETS(string, n, stream)      (g_RegFun.stFile.pfFgets(string, n, stream))
#define WSEC_FTELL(stream)                 (g_RegFun.stFile.pfFtell(stream))
#define WSEC_FSEEK(stream, offset, origin) (g_RegFun.stFile.pfFseek(stream, offset, origin))
#define WSEC_FEOF(stream)                  (g_RegFun.stFile.pfFeof(stream))
#define WSEC_FERROR(stream)                (g_RegFun.stFile.pfFerror(stream))

#define WSEC_DESTROY_KEY(pKey, nKeySize) do{if(nKeySize) {WSEC_MEMSET(pKey, nKeySize, 0, nKeySize);}}do_end;

/* ��APPͨ��: WSEC_NTF_CODE_ENUM eNtfCode, void* pData, size_t nSize */
#define WSEC_NOTIFY(eNtfCode, pData, nSize) do{if (g_RegFun.stRelyApp.pfNotify){g_RegFun.stRelyApp.pfNotify(eNtfCode, pData, nSize);}}do_end

/* ���ƽ�CPUִ��Ȩ */
#define WSEC_DO_EVENTS do{if (g_RegFun.stRelyApp.pfDoEvents){g_RegFun.stRelyApp.pfDoEvents();}}do_end;

#define WSEC_STRLEN       strlen

#define WSEC_FREAD_MUST(Buff, BuffSize, FileStream) ((WSEC_FREAD(Buff, 1, BuffSize, FileStream) == (WSEC_SIZE_T)(BuffSize)))
#define WSEC_FWRITE_MUST(Buff, BuffSize, FileStream) ((WSEC_FWRITE(Buff, 1, BuffSize, FileStream) == (WSEC_SIZE_T)(BuffSize)))

/* byte order: swap order ---- j00265291 2014-8-5 */
#define WSEC_SWAP_SHORT(L) ((((L) & 0x00FF) << 8) | (((L) & 0xFF00) >> 8))
#define WSEC_SWAP_LONG(L) ((WSEC_SWAP_SHORT((L) & 0xFFFF) << 16) | WSEC_SWAP_SHORT(((L) >> 16) & 0xFFFF))

#ifndef WSEC_CPU_ENDIAN_MODE
    #define WSEC_CPU_ENDIAN_MODE WSEC_CPU_ENDIAL_AUTO_CHK
#endif

/* �ֽ���ת�� */
#if WSEC_CPU_ENDIAN_MODE == WSEC_CPU_ENDIAL_BIG /* ָ��Ϊ��˶���ģʽ */
    #define WSEC_H2N_L(v) (v)
    #define WSEC_N2H_L(v) (v)
    #define WSEC_H2N_S(v) (v)
    #define WSEC_N2H_S(v) (v)
#elif WSEC_CPU_ENDIAN_MODE == WSEC_CPU_ENDIAL_LITTLE /* ָ��ΪС�˶���ģʽ */
    #define WSEC_H2N_L(v) WSEC_SWAP_LONG(v)
    #define WSEC_N2H_L(v) WSEC_SWAP_LONG(v)
    #define WSEC_H2N_S(v) WSEC_SWAP_SHORT(v)
    #define WSEC_N2H_S(v) WSEC_SWAP_SHORT(v)
#else /* �����Զ���� */
    #ifdef WSEC_DEBUG
        #define WSEC_H2N_L(v) (WSEC_IsBigEndianMode() ? (v) : WSEC_SWAP_LONG(v))
        #define WSEC_N2H_L(v) (WSEC_IsBigEndianMode() ? (v) : WSEC_SWAP_LONG(v))
        #define WSEC_H2N_S(v) (WSEC_IsBigEndianMode() ? (v) : WSEC_SWAP_SHORT(v))
        #define WSEC_N2H_S(v) (WSEC_IsBigEndianMode() ? (v) : WSEC_SWAP_SHORT(v))
    #else
        #define WSEC_H2N_L(v) (g_bIsBigEndianMode ? (v) : WSEC_SWAP_LONG(v))
        #define WSEC_N2H_L(v) (g_bIsBigEndianMode ? (v) : WSEC_SWAP_LONG(v))
        #define WSEC_H2N_S(v) (g_bIsBigEndianMode ? (v) : WSEC_SWAP_SHORT(v))
        #define WSEC_N2H_S(v) (g_bIsBigEndianMode ? (v) : WSEC_SWAP_SHORT(v))
    #endif
#endif
/* eCvtType:WSEC_BYTEORDER_CVT_ENUM, v:2��4�ֽ����� */
#define WSEC_BYTE_ORDER_CVT_L(eCvtType, v) ((wbcHost2Network == eCvtType) ? WSEC_H2N_L(v) : WSEC_N2H_L(v))
#define WSEC_BYTE_ORDER_CVT_S(eCvtType, v) ((wbcHost2Network == eCvtType) ? WSEC_H2N_S(v) : WSEC_N2H_S(v))

/*=======================================================================
    2. ö��
=======================================================================*/
/* ��־��Ϣ���� */
typedef enum 
{
    WSEC_LOG_INFO,
    WSEC_LOG_WARN,
    WSEC_LOG_ERR
} WSEC_LOG_LEVEL_ENUM;

typedef enum 
{
    wbcHost2Network, /* ������ת������ */
    wbcNetwork2Host  /* ������ת������ */
} WSEC_BYTEORDER_CVT_ENUM; /* �ֽ���ת������ */

typedef enum
{
    LOCK4KEYSTORE, /* �ڴ��е�Keystore���ݼ���Ӧ���ļ� */
    LOCK4KMC_CFG,  /* KMC�ڴ����� */
    WSEC_LOCK_NUM
} WSEC_LOCK_FOR_ENUM;

/* ������ȽϽ�� */
typedef enum
{
    WSEC_CMP_RST_SMALL_THAN  = -1,
    WSEC_CMP_RST_EQUAL       = 0,
    WSEC_CMP_RST_BIG_THAN    = 1
} WSEC_CMP_RST_ENUM;

/* ����&ʱ��ĵ�λ */
typedef enum
{
    dtpSecond = 0,
    dtpMinutes,
    dtpHour,
    dtpDay,
    dtpMonth,
    dtpYear
} WSEC_DATETIME_PART_ENUM;

typedef enum
{
    WSEC_WAIT_INIT = 0, /* ��δ��ʼ�� */
    WSEC_INIT_FAIL,     /* ��ʼ��ʧ�� */
    WSEC_RUNNING        /* �������� */
} WSEC_RUN_STATE_ENUM; /* CBB��ǰ���ں���״̬ */


/*=======================================================================
    3. ������������, �ṹ
=======================================================================*/
typedef struct tagWSEC_BUFF
{
    WSEC_VOID*  pBuff;
    WSEC_SIZE_T nLen;
} WSEC_BUFF;

#pragma pack(1)
typedef struct tagWSEC_TLV
{
    WSEC_UINT32 ulTag;
    WSEC_SIZE_T ulLen;
    WSEC_VOID*  pVal;
} WSEC_TLV_STRU;
#pragma pack()

typedef struct tagWSEC_CALLBACK_FUN_STRU
{
    WSEC_MEMORY_CALLBACK_STRU        stMemory;
    WSEC_FILE_CALLBACK_STRU          stFile;
    
    WSEC_LOCK_CALLBACK_STRU          stLock;
    WSEC_BASE_RELY_APP_CALLBACK_STRU stRelyApp;
} WSEC_CALLBACK_FUN_STRU;

typedef struct tagWSEC_SYS
{
    WSEC_RUN_STATE_ENUM eState;
} WSEC_SYS_STRU;

typedef struct tagWSEC_EXEC_INFO
{
    WSEC_SYSTIME_T stPreExecTime;      /* �ϴ�ִ�����������ʱ�� */
    WSEC_BOOL      bUnconditionalExec; /* �Ƿ�������ִ��, ����Ҫ���ִ��ʱ�� */
} WSEC_EXEC_INFO_STRU;

typedef WSEC_VOID (* WSEC_FP_PeriodicCall)(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo);
typedef WSEC_ERR_T (*WSEC_WriteFile)(const WSEC_VOID* pData, const WSEC_CHAR* pszFile, const WSEC_VOID* pReserved);

typedef struct tagWSEC_PERIODIC_CALL
{
    WSEC_FP_PeriodicCall pfPeriodicCall;   /* �����Ե��õĺ��� */
    WSEC_EXEC_INFO_STRU  stExecInfo;       /* ����ִ�������Ϣ */
    WSEC_SYSTIME_T       stPreCallTimeUtc; /* �ϴε���ʱ�ĵ�UTCʱ�� */
    WSEC_UINT32          ulPeriodSec;      /* ��������(��λ: ��) */
} WSEC_PERIODIC_CALL_STRU;

typedef struct tagWSEC_SPEND_TIME
{
    WSEC_CLOCK_T tPre;
} WSEC_SPEND_TIME_STRU; /* ��¼ʱ�� */

#define WSEC_BUFF_ALLOC(buff, size) do{buff.pBuff = WSEC_MALLOC(size); buff.nLen = size;}do_end
#define WSEC_BUFF_ASSIGN(buff, ptr, ptr_size) do{buff.pBuff = ptr; buff.nLen = ptr_size;}do_end
#define WSEC_BUFF_FREE(buff) WSEC_FREE(buff.pBuff)
#define WSEC_BUFF_FREE_S(buff) WSEC_FREE_S(buff.pBuff, buff.nLen)

#define WSEC_TLV_ASSIGN(TLV_STRU, T, L, V) do{TLV_STRU.ulTag = T; TLV_STRU.ulLen = L; TLV_STRU.pVal = V;}do_end

/*=======================================================================
    4. CBB�ڲ���������
=======================================================================*/
WSEC_VOID* WSEC_MemAlloc(WSEC_UINT32 ulSize, const WSEC_CHAR* pszFile, WSEC_INT32 nLine);
WSEC_VOID* WSEC_MemFree(WSEC_VOID* ptr, const WSEC_CHAR* pszFile, WSEC_INT32 nLine);

/* ��ӡ��־ */
/*lint -e960*/
WSEC_VOID WSEC_WriLog(const WSEC_CHAR* pszFileName, WSEC_INT32 nLineNo, WSEC_LOG_LEVEL_ENUM eLevel, const WSEC_CHAR* format, ...);

/* �ж�CPU����ģʽ */
extern WSEC_BOOL WSEC_IsBigEndianMode();

/* ����&ʱ�䴦���� */
WSEC_VOID WSEC_DateTimeAdd(const WSEC_SYSTIME_T* pstBase, WSEC_INT32 nAdd, WSEC_DATETIME_PART_ENUM ePart, WSEC_SYSTIME_T* pstNew);
WSEC_BOOL WSEC_IsDateTime(const WSEC_SYSTIME_T* pstVal); /* �ж�stVal�ĸ������ڳ��⣩�Ƿ�Ϊ����&ʱ�� */
WSEC_CMP_RST_ENUM WSEC_DateTimeCompare(const WSEC_SYSTIME_T* pstBig, const WSEC_SYSTIME_T* pstSmall); /* �Ƚ�����ʱ��Ĵ�С������0��ʾ��ȣ�1��ʾǰ�ߴ�-1��ʾǰ��С */
WSEC_INT32 WSEC_DateTimeDiff(WSEC_DATETIME_PART_ENUM eUnit, const WSEC_SYSTIME_T* pstFrom, const WSEC_SYSTIME_T* pstTo);
WSEC_BOOL WSEC_DateTimeCopy(WSEC_SYSTIME_T* pstDst, const WSEC_SYSTIME_T *pstSrc);
WSEC_BOOL WSEC_GetLocalDateTime(WSEC_SYSTIME_T* pstNow);
WSEC_BOOL WSEC_GetUtcDateTime(WSEC_SYSTIME_T* pstNow);
WSEC_BOOL WSEC_UtcTime2Local(const WSEC_SYSTIME_T* pUtc, WSEC_SYSTIME_T* pLocal);
WSEC_BOOL WSEC_LocalTime2Utc(const WSEC_SYSTIME_T* pLocal, WSEC_SYSTIME_T* pUtc);
WSEC_CHAR* WSEC_DateTime2String(const WSEC_SYSTIME_T* pTime, WSEC_VOID* pBuff, WSEC_SIZE_T nBuffSize);

/* ����������ԭ�� */
WSEC_ERR_T WSEC_CreateLock(WSEC_HANDLE *phMutex);
WSEC_VOID WSEC_Lock(WSEC_LOCK_FOR_ENUM eLockId);
WSEC_VOID WSEC_Unlock(WSEC_LOCK_FOR_ENUM eLockId);
WSEC_VOID WSEC_DestroyLock(WSEC_HANDLE *phMutex);

WSEC_CHAR* WSEC_StringClone(const WSEC_CHAR* pszCloneFrom, const WSEC_CHAR* pszCallerFile, WSEC_INT32 nCallerLine); /* ��¡�ַ��� */
WSEC_VOID* WSEC_BuffClone(const WSEC_VOID* pCloneFrom, WSEC_SIZE_T nSize, const WSEC_CHAR* pszCallerFile, WSEC_INT32 nCallerLine); /* ��¡������ */
    
WSEC_VOID WSEC_Xor(const WSEC_BYTE* pOperand1, WSEC_SIZE_T nOperand1Len, /* ��������������1���䳤�� */
                   const WSEC_BYTE* pOperand2, WSEC_SIZE_T nOperand2Len, /* ��������������2���䳤�� */
                   WSEC_BYTE* pResult, WSEC_SIZE_T nResultLen); /* ����������Ļ���������Ԥ������ */

WSEC_SIZE_T WSEC_GetZeroItemCount(const WSEC_VOID* pvData, WSEC_SIZE_T nSize, WSEC_SIZE_T nItemSize);

WSEC_BOOL WSEC_CopyFile(const WSEC_CHAR* pszFrom, const WSEC_CHAR* pszTo);
WSEC_BOOL WSEC_GetFileLen(const WSEC_CHAR* pszFileName, WSEC_FILE_LEN* pulLen);
WSEC_BOOL WSEC_DeleteFileS(const WSEC_CHAR* pszFileName); /* ��ȫ��ɾ���ļ� */
WSEC_ERR_T WSEC_WriteFileS(const WSEC_VOID* pvData, WSEC_CHAR** ppszFile, WSEC_SIZE_T nFileNum, WSEC_WriteFile pfWriFile, const WSEC_VOID* pvReserved); /* ��ȫ����һ���ļ��������� */

/* ������Ĭ�Ϻ��� */
WSEC_BOOL WSEC_DeftCreateLock(WSEC_HANDLE *phMutex);
WSEC_VOID WSEC_DeftDestroyLock(WSEC_HANDLE hMutex);
WSEC_VOID WSEC_DeftLock(WSEC_HANDLE hMutex);
WSEC_VOID WSEC_DeftUnlock(WSEC_HANDLE hMutex);

/* Hash */
WSEC_BOOL WSEC_CreateHashCode(WSEC_UINT32 ulHashAlg, const WSEC_BUFF* pChkBuff, WSEC_UINT32 ulBuffNum, INOUT WSEC_BUFF* pHashCode);
WSEC_ERR_T WSEC_ChkIntegrity(WSEC_UINT32 ulHashAlg, const WSEC_BUFF* pChkBuff, WSEC_UINT32 ulBuffNum, const WSEC_VOID* pCmpHashCode, WSEC_UINT32 ulHashCodeLen);
WSEC_ERR_T WSEC_CreateHmacCode(WSEC_ALGID_E eHmacAlg, const WSEC_BUFF* pBuff, WSEC_UINT32 ulBuffNum, const WSEC_BUFF* pKey, INOUT WSEC_BUFF* pHmacCode);
WSEC_ERR_T WSEC_ChkHmacCode(WSEC_ALGID_E eHmacAlg, const WSEC_BUFF* pBuff, WSEC_UINT32 ulBuffNum, const WSEC_BUFF* pKey, const WSEC_BUFF* pHmacCode);

WSEC_ERR_T WSEC_HashFile(WSEC_UINT32 ulHashAlg, WSEC_FILE pFile, WSEC_UINT32 ulDataSize, WSEC_BUFF* pHash);
WSEC_ERR_T WSEC_ChkFileIntegrity(WSEC_UINT32 ulHashAlg, WSEC_FILE pFile, WSEC_UINT32 ulDataSize, const WSEC_BUFF* pHash);

WSEC_VOID WSEC_CvtByteOrder4DateTime(WSEC_SYSTIME_T* pstDateTime, WSEC_BYTEORDER_CVT_ENUM eOper);

WSEC_VOID WSEC_CallPeriodicFunc(WSEC_FP_PeriodicCall pFunc); /* �������������Ե��ȳ��� */

WSEC_ERR_T WSEC_InitializeLock();
WSEC_VOID WSEC_FinalizeLock();

/* TLV */
WSEC_BOOL WSEC_ReadTlv(WSEC_FILE stream, WSEC_VOID* pBuff, WSEC_SIZE_T nBuffSize, WSEC_TLV_STRU* pTlv, WSEC_ERR_T* pnErrCode);
WSEC_ERR_T WSEC_WriteTlv(WSEC_FILE stream, WSEC_UINT32 ulTag, WSEC_SIZE_T ulLen, const WSEC_VOID* pVal);
WSEC_VOID WSEC_CvtByteOrder4Tlv(WSEC_TLV_STRU* pTlv, WSEC_BYTEORDER_CVT_ENUM eOper);

/* ��ʱ�� */
WSEC_BOOL WSEC_IsTimerout(WSEC_SPEND_TIME_STRU* pTimer, WSEC_UINT32 ulWaitMilSec); /* �жϼ�ʱ�Ƿ�ʱ */
WSEC_VOID WSEC_RptProgress(const WSEC_PROGRESS_RPT_STRU* pstRptProgressFun, WSEC_SPEND_TIME_STRU* pstTimer, WSEC_UINT32 ulScale, WSEC_UINT32 ulCurrent, WSEC_BOOL* pbCancel);

WSEC_VOID WSEC_LogStructSize(const WSEC_CHAR* pszStructName, WSEC_SIZE_T nSize);

extern WSEC_CALLBACK_FUN_STRU g_RegFun;
extern WSEC_BOOL g_bIsBigEndianMode;

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* __WIRELESS_PRI_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27 */
