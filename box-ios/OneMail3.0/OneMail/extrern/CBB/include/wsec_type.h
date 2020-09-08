/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name:  WSEC_Type.h
* Decription: 
     ���Ͷ���
*********************************************************************************/
#ifndef __WIRELESS_TYPE_D13A02_DCRFASD3F_4E7HLPO4C27
#define __WIRELESS_TYPE_D13A02_DCRFASD3F_4E7HLPO4C27

#include <stdio.h>
#include <time.h>

#ifdef __cplusplus
extern "C"
{
#endif

/************************************************************************
 *  macro defines
************************************************************************/

#define INOUT

#define WSEC_NULL_PTR       ((void *)0)

#define WSEC_FALSE          (0)
#define WSEC_TRUE           (1)
#define WSEC_FALSE_FOREVER  (!__LINE__) /* ���, ���������жϱ������澯 */
#define WSEC_TRUE_FOREVER   (__LINE__)  /* ����, ���������жϱ������澯 */

/************************************************************************
 *  type defines
************************************************************************/

/* basic type redefines */
typedef void            WSEC_VOID;
typedef unsigned char   WSEC_UINT8;
typedef unsigned char   WSEC_BYTE;
typedef unsigned short  WSEC_UINT16;
typedef unsigned int    WSEC_UINT32;

typedef int             WSEC_INT32;
typedef int             WSEC_BOOL;
typedef char            WSEC_CHAR;

typedef void *          WSEC_HANDLE;
typedef FILE*           WSEC_FILE;
typedef int             WSEC_POSITION;
typedef unsigned long   WSEC_FILE_LEN;

typedef unsigned int    WSEC_SIZE_T;
typedef clock_t         WSEC_CLOCK_T;
typedef unsigned long   WSEC_ERR_T;

/* ֪ͨAPP��ͨ����(�Դ�ʶ��������ͨ��) */
typedef enum
{
    WSEC_KMC_NTF_RK_EXPIRE          = 1, /* Root Key(RK)��Կ����(����)���� */
    WSEC_KMC_NTF_MK_EXPIRE          = 2, /* Master Key(MK)(����)���� */
    WSEC_KMC_NTF_MK_CHANGED         = 3, /* MK��� */
    WSEC_KMC_NTF_USING_EXPIRED_MK   = 4, /* ʹ�ù���MK */
    WSEC_KMC_NTF_KEY_STORE_CORRUPT  = 5, /* Keystore���ƻ� */
    WSEC_KMC_NTF_CFG_FILE_CORRUPT   = 6, /* KMC�����ļ����ƻ� */
    WSEC_KMC_NTF_WRI_KEY_STORE_FAIL = 7, /* ����Keystoreʧ�� */
    WSEC_KMC_NTF_WRI_CFG_FILE_FAIL  = 8, /* ����KMC�����ļ�ʧ�� */
    WSEC_KMC_NTF_MK_NUM_OVERFLOW    = 9  /* MK������������� */
} WSEC_NTF_CODE_ENUM;

/* CBB��APPͨ�� */
typedef WSEC_VOID (*WSEC_FP_Notify)(WSEC_NTF_CODE_ENUM eNtfCode, const WSEC_VOID* pData, WSEC_SIZE_T nDataSize);

/* �����ϱ� */
typedef WSEC_VOID (*WSEC_FP_RptProgress)(WSEC_UINT32 ulTag, WSEC_UINT32 ulScale, WSEC_UINT32 ulCurrent, WSEC_BOOL* pbCancel);

/* struct defines */
#pragma pack(1)
typedef struct wsectagSysTime
{
    WSEC_UINT16 uwYear;     /* ��� */
    WSEC_UINT8  ucMonth;    /* �·�(1~12) */
    WSEC_UINT8  ucDate;     /* ����(1~31, ���������·�ȷ��) */
    WSEC_UINT8  ucHour;     /* ʱ(0~23) */
    WSEC_UINT8  ucMinute;   /* ��(0~59) */
    WSEC_UINT8  ucSecond;   /* ��(0~59) */
    WSEC_UINT8  ucWeek;     /* ����(1~7�ֱ��ʾ��һ������) */
} WSEC_SYSTIME_T;
#pragma pack()

#pragma pack(1)
typedef struct tagWSEC_SCHEDULE_TIME
{
    WSEC_UINT8  ucHour;   /* ʱ(0~23) */
    WSEC_UINT8  ucMinute; /* ��(0~59) */
    WSEC_UINT8  ucWeek;   /* ÿ�ܼ�, ȡֵΪ1~7, ��ʾ��1~����; 1~7֮�⣬���ʾÿ�� */
    WSEC_BYTE   abReserved[4]; /* Ԥ�� */
} WSEC_SCHEDULE_TIME_STRU;
#pragma pack()

typedef struct tagWSEC_PROGRESS_RPT
{
    WSEC_UINT32         ulTag;         /* APPʶ�������ı�ǩ */
    WSEC_FP_RptProgress pfRptProgress; /* �����ϱ��ص����� */
} WSEC_PROGRESS_RPT_STRU; /* CBB���������ʱ��APP�ϱ����� */

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* __WIRELESS_TYPE_D13A02_DCRFASD3F_4E7HLPO4C27 */
