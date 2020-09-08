/******************************************************************************

��Ȩ���� (C), 2001-2011, ��Ϊ�������޹�˾

******************************************************************************
�� �� ��   : KMC_Itf.h
�� �� ��   : ����
��    ��   : x00102361
��������   : 2014��6��16��
����޸�   :
��������   : KMC_Func.c �Ķ���ӿ�ͷ�ļ�
�����б�   :
�޸���ʷ   :
1.��    ��   : 2014��6��16��
��    ��   : x00102361
�޸�����   : �����ļ�

******************************************************************************/
#ifndef __KMC_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__
#define __KMC_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__

#include "wsec_type.h"
#include "wsec_config.h"

#ifdef __cplusplus
#if __cplusplus
extern "C"
{
#endif
#endif /* __cplusplus */

/*==============================================
                �궨��
==============================================*/
#define WSEC_MK_LEN_MAX        (128)  /* MK��Կ��(��)����󳤶�(�ֽ���) */
#define KMC_KEY_DFT_ITERATIONS (4000) /* ȱʡ��Կ�������� */

/*==============================================
                ö������
==============================================*/
/* ����;����Կ���࣬��bit���� */
typedef enum
{
    KMC_KEY_TYPE_ENCRPT    = 1,       /* �ԳƼ��� */
    KMC_KEY_TYPE_INTEGRITY = 2,       /* �����Ա��� */
    KMC_KEY_TYPE_ENCRPT_INTEGRITY = 3 /* ���ܼ������� */
} KMC_KEY_TYPE_ENUM;

/* ���ݱ���ģ���ṩ�����ݱ����㷨���� */
typedef enum 
{
    SDP_ALG_ENCRPT,     /* ���� */
    SDP_ALG_INTEGRITY,  /* �����Ա��� */
    SDP_ALG_PWD_PROTECT /* ����� */
} KMC_SDP_ALG_TYPE_ENUM;

/* Root Key���ϲ�����ʽ */
typedef enum
{
    KMC_RK_GEN_BY_INNER, /* ϵͳ�Զ����� */
    KMC_RK_GEN_BY_IMPORT /* �ⲿ���� */
} KMC_RK_GEN_FROM;

/* Master Key������ʽ */
typedef enum
{
    KMC_MK_GEN_BY_INNER, /* ϵͳ�Զ����� */
    KMC_MK_GEN_BY_IMPORT /* �ⲿ���� */
} KMC_MK_GEN_FROM;

/* ��Կ״̬ */
typedef enum
{
    KMC_KEY_STATUS_INACTIVE = 0, /* �ǻ״̬����Կ�������ڻ������ݼ��ܣ�����������������ʷ���� */
    KMC_KEY_STATUS_ACTIVE        /* ����ʹ���� */
} KMC_KEY_STATUS_ENUM;

/* ��Կ������� */
typedef enum 
{
    KMC_KEY_ACTIVATED = 0, /* ��Կ���� */
    KMC_KEY_INACTIVATED,   /* ��Կȥ����(����) */
    KMC_KEY_REMOVED        /* ��Կ��ɾ�� */
} KMC_KEY_CHANGE_TYPE_ENUM;

/*==============================================
                �ṹ��
==============================================*/
/*----------------------------------------------------------
1. Root Key(RK)��Ϣ
----------------------------------------------------------*/
typedef struct tagKMC_RK_ATTR
{
    WSEC_UINT16    usVer;              /* �汾�� */
    WSEC_UINT16    usRkMeterialFrom;   /* ����Կ������Դ, �� KMC_RK_GEN_FROM */
    WSEC_SYSTIME_T stRkCreateTimeUtc;  /* ����Կ����ʱ��(UTC) */
    WSEC_SYSTIME_T stRkExpiredTimeUtc; /* ����Կ����ʱ��(UTC) */
    WSEC_UINT32    ulRmkIterations;    /* ����RMK�������� */
} KMC_RK_ATTR_STRU;

/*----------------------------------------------------------
2. Master Key(MK)��Ϣ
----------------------------------------------------------*/
#pragma pack(1)
/*``````````````````````````````````````````````````````````````````````````````````````````````
MK������ؼ���:
(1) ulDomainId + ulKeyIdΪΨһ�ؼ���, ����ʶ��MK
(2) ulDomainId + usType + ucStatus Ϊ���ظ��ؼ���, ����APP��usType��ȡ��ǰ״̬Ϊ'����'��MK
``````````````````````````````````````````````````````````````````````````````````````````````*/
typedef struct tagKMC_MK_INFO
{
    WSEC_UINT32    ulDomainId;      /* ��Կ������ */
    WSEC_UINT32    ulKeyId;         /* ��ԿID����ͬһDomainΨһ */
    WSEC_UINT16    usType;          /* ����;����Կ����, �� KMC_KEY_TYPE_ENUM */
    WSEC_UINT8     ucStatus;        /* ��Կ״̬, �� KMC_KEY_STATUS_ENUM */
    WSEC_UINT8     ucGenStyle;      /* ��Կ������ʽ, �� KMC_MK_GEN_FROM */
    WSEC_SYSTIME_T stMkCreateTimeUtc;  /* MK����ʱ��(UTC) */
    WSEC_SYSTIME_T stMkExpiredTimeUtc; /* MK����ʱ��(UTC) */
} KMC_MK_INFO_STRU; /* MKͷ��Ϣ */
#pragma pack()

/*----------------------------------------------------------
3. ��Կ������ص�����
----------------------------------------------------------*/
/* 1) ȫ���Ե���Կ������Ϣ */
#pragma pack(1)
typedef struct tagKMC_CFG_ROOT_KEY
{
    WSEC_UINT32    ulRootKeyLifeDays;          /* Rootkey ��Чʱ�䣨�죩*/
    WSEC_UINT32    ulRootMasterKeyIterations;  /* Rootkey �������� */
    WSEC_BYTE      abReserved[8];              /* Ԥ�� */
} KMC_CFG_ROOT_KEY_STRU; /* RK������� */
#pragma pack()
#pragma pack(1)
typedef struct tagKMC_CFG_KEY_MAN
{
    WSEC_UINT32    ulWarningBeforeKeyExpiredDays; /* ��Կ������ǰԤ������ */
    WSEC_UINT32    ulGraceDaysForUseExpiredKey;   /* ʹ�ù�����Կ�Ŀ�������, ������������֪ͨAPP */
    WSEC_BOOL      bKeyAutoUpdate;                /* ��Կ�����Ƿ��Զ�����, ��Ը���Կ����KeyFrom=0��MK��Ч */
    WSEC_SCHEDULE_TIME_STRU stAutoUpdateKeyTime;  /* ��Կ�Զ�����ʱ�� */
    WSEC_BYTE      abReserved[8];                 /* Ԥ�� */
} KMC_CFG_KEY_MAN_STRU; /* ��Կ�������ò��� */
#pragma pack()

/* 2) ���ݱ��� */
#pragma pack(1)
typedef struct tagKMC_CFG_DATA_PROTECT
{
    WSEC_UINT32 ulAlgId;         /* �㷨ID */
    WSEC_UINT16 usKeyType;       /* ����;����Կ����, �� KMC_KEY_TYPE_ENUM */
    WSEC_BOOL   bAppendMac;      /* �Ƿ�׷��������У��ֵ */
    WSEC_UINT32 ulKeyIterations; /* ��Կ�������� */
    WSEC_BYTE   abReserved[8];   /* Ԥ�� */
} KMC_CFG_DATA_PROTECT_STRU;
#pragma pack()

/* 3) DOMAIN Key Type���� */
#pragma pack(1)
typedef struct tagKMC_CFG_KEY_TYPE
{
    WSEC_UINT16 usKeyType;     /* ����;����Կ����, �� KMC_KEY_TYPE_ENUM */
    WSEC_UINT32 ulKeyLen;      /* ��Կ���� */
    WSEC_UINT32 ulKeyLifeDays; /* ��Ч��(����) */
    WSEC_BYTE   abReserved[8]; /* Ԥ�� */
} KMC_CFG_KEY_TYPE_STRU;
#pragma pack()

/* 4) DOMAIN���� */
#pragma pack(1)
typedef struct tagKMC_CFG_DOMAIN_INFO
{
    WSEC_UINT32  ulId;        /* ��Կ������ */
    WSEC_UINT8   ucKeyFrom;   /* ��Կ������Դ, �� KMC_MK_GEN_FROM */
    WSEC_CHAR    szDesc[128]; /* ��Կ���� */
    WSEC_BYTE    abReserved[8]; /* Ԥ�� */
} KMC_CFG_DOMAIN_INFO_STRU;
#pragma pack()

/*----------------------------------------------------------
4. KMC������ļ���
----------------------------------------------------------*/
typedef struct tagKMC_FILE_NAME
{
    WSEC_CHAR* pszKeyStoreFile[2]; /* Keystore�ļ���(�ɿ��Կ���, ���ļ���Ϊ����) */
    WSEC_CHAR* pszKmcCfgFile[2];   /* KMC�����ļ���(���APP�Լ�������������, �����ṩ) */
} KMC_FILE_NAME_STRU;

/*----------------------------------------------------------
5. ֪ͨAPP�����ݽṹ
----------------------------------------------------------*/
/* 1) RK��������ͨ�� */
typedef struct tagKMC_RK_EXPIRE_NTF
{
    KMC_RK_ATTR_STRU stRkInfo;    /* �������ڵĸ���Կ��Ϣ */
    WSEC_INT32       nRemainDays; /* ������ջ��ж����� */
} KMC_RK_EXPIRE_NTF_STRU;

/* 2) MK��������ͨ�� */
typedef struct tagKMC_MK_EXPIRE_NTF
{
    KMC_MK_INFO_STRU stMkInfo;    /* �������ڵ�MK��Ϣ */
    WSEC_INT32       nRemainDays; /* ������ջ��ж����� */
} KMC_MK_EXPIRE_NTF_STRU;

/* 3) MK���ͨ�� */
typedef struct tagKMC_MK_CHANGE_NTF
{
    KMC_MK_INFO_STRU         stMkInfo; /* �����MK��Ϣ */
    KMC_KEY_CHANGE_TYPE_ENUM eType;    /* ������� */
} KMC_MK_CHANGE_NTF_STRU;

/* 4) ����MK��������ʹ��ͨ�� */
typedef struct tagKMC_USE_EXPIRED_MK_NTF
{
    KMC_MK_INFO_STRU stExpiredMkInfo; /* ����MK��Ϣ */
    WSEC_INT32       nExpiredDays;    /* �������� */
} KMC_USE_EXPIRED_MK_NTF_STRU;

/* 5) дKeystore�ļ�ʧ��ͨ�� */
typedef struct tagKMC_WRI_KSF_FAIL_NTF
{
    WSEC_ERR_T ulCause; /* ʧ��ԭ�� */
} KMC_WRI_KSF_FAIL_NTF_STRU;

/* 6) дKMC�����ļ�ʧ��ͨ�� */
typedef struct tagKMC_WRI_KCF_FAIL_NTF
{
    WSEC_ERR_T ulCause; /* ʧ��ԭ�� */
} KMC_WRI_KCF_FAIL_NTF_STRU;

/* 7) MK���������ͨ�� */
typedef struct tagKMC_MK_NUM_OVERFLOW
{
    WSEC_UINT32 ulNum;    /* ��ǰMK���� */
    WSEC_UINT32 ulMaxNum; /* ����MK������ */
} KMC_MK_NUM_OVERFLOW_STRU;

/*----------------------------------------------
    �����ûص�����ָ�붨��
----------------------------------------------*/
typedef WSEC_BOOL (*WSEC_FP_ReadRootKeyCfg)(KMC_CFG_ROOT_KEY_STRU* pstRkCfg); /* ��ȡRootKey���� */
typedef WSEC_BOOL (*WSEC_FP_ReadKeyManCfg)(KMC_CFG_KEY_MAN_STRU* pstKmCfg); /* ��ȡKEY�������� */

/* ��ȡ����Domain���� */
typedef WSEC_BOOL (*WSEC_FP_ReadCfgOfDomainCount)(WSEC_UINT32* pulDomainCount); /* ��ȡKMC��������֮ Domain���� */

/* ��ȡ����Domain������Ϣ */
typedef WSEC_BOOL (*WSEC_FP_ReadCfgOfDomainInfo)(KMC_CFG_DOMAIN_INFO_STRU* pstAllDomainInfo, /* �����������Domain������Ϣ�ĵĻ����� */
                                                 WSEC_UINT32 ulDomainCount); /* pstAllDomainInfo��ָ�򻺳�������KMC_CFG_DOMAIN_INFO_STRU���ݽṹ�ĸ��� */

/* ��ȡָ��Domain�ж�����KeyType���� */
typedef WSEC_BOOL (*WSEC_FP_ReadCfgOfDomainKeyTypeCount)(WSEC_UINT32 ulDomainId, /* ָ����Domain */
                                                       WSEC_UINT32* pulKeyTypeCount); /* �����Domain��KeyType��¼�� */

/* ��ȡָ��Domain������KeyType���� */
typedef WSEC_BOOL (*WSEC_FP_ReadCfgOfDomainKeyType)(WSEC_UINT32 ulDomainId, /* ����Doamin */
                                                    KMC_CFG_KEY_TYPE_STRU* pstDomainAllKeyType, /* �����߸��������������Domain����KeyType���ü�¼ */
                                                    WSEC_UINT32 ulKeyTypeCount); /* pstDomainAllKeyType��ָ�򻺳�������KMC_KEY_TYPE_STRU���ݽṹ�ĸ��� */

/* ��ȡָ�����͵��㷨���� */
typedef WSEC_BOOL (*WSEC_FP_ReadCfgOfDataProtection)(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstPara); 

/* KMC���ûص������� */
typedef struct tagKMC_FP_CFG_CALLBACK
{
    WSEC_FP_ReadRootKeyCfg              pfReadRootKeyCfg;              /* ��ȡRootKey������ز��� */
    WSEC_FP_ReadKeyManCfg               pfReadKeyManCfg;               /* ��ȡ��Կ�������ڹ�����ز��� */
    WSEC_FP_ReadCfgOfDomainCount        pfReadCfgOfDomainCount;        /* ��ȡKMC��������֮ Domain���� */
    WSEC_FP_ReadCfgOfDomainInfo         pfReadCfgOfDomainInfo;         /* ��ȡ����Domain������Ϣ */
    WSEC_FP_ReadCfgOfDomainKeyTypeCount pfReadCfgOfDomainKeyTypeCount; /* ��ȡָ��Domain�ж�����KeyType���� */
    WSEC_FP_ReadCfgOfDomainKeyType      pfReadCfgOfDomainKeyType;      /* ��ȡָ��Domain������KeyType���� */
    WSEC_FP_ReadCfgOfDataProtection     pfReadCfgOfDataProtection;     /* ��ȡָ�����͵��㷨���� */
} KMC_FP_CFG_CALLBACK_STRU;

typedef struct tagKMC_FP_CALLBACK
{
    KMC_FP_CFG_CALLBACK_STRU          stReadCfg;     /* KMC���ûص������� */
} KMC_FP_CALLBACK_STRU; /* KMC�Ļص����� */

/*----------------------------------------------
    ����ԭ��˵��
----------------------------------------------*/
/* (1) Keystore���� */
WSEC_ERR_T KMC_RmvMk(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId);
WSEC_ERR_T KMC_ExportMkFile(const WSEC_CHAR* pszToFile, const WSEC_BYTE* pbPwd, WSEC_UINT32 ulPwdLen, WSEC_UINT32 ulKeyIterations, const WSEC_PROGRESS_RPT_STRU* pstRptProgress);
WSEC_ERR_T KMC_ImportMkFile(const WSEC_CHAR* pszFromFile, const WSEC_BYTE* pbPwd, WSEC_UINT32 ulPwdLen, const WSEC_PROGRESS_RPT_STRU* pstRptProgress);
WSEC_ERR_T KMC_UpdateRootKey(const WSEC_BYTE* pbKeyEntropy, WSEC_SIZE_T ulSize);
WSEC_ERR_T KMC_GetRootKeyInfo(KMC_RK_ATTR_STRU* pstRkInfo);
WSEC_INT32 KMC_GetMkCount();
WSEC_ERR_T KMC_GetMk(WSEC_INT32 Index, KMC_MK_INFO_STRU* pstMk);
WSEC_ERR_T KMC_GetMaxMkId(WSEC_UINT32 ulDomainId, WSEC_UINT32* pulMaxKeyId);
WSEC_ERR_T KMC_SetMkExpireTime(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, const WSEC_SYSTIME_T* psExpireTime);
WSEC_ERR_T KMC_SetMkStatus(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, WSEC_UINT8 ucStatus);
WSEC_ERR_T KMC_RegisterMk(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, WSEC_UINT16 usKeyType, const WSEC_BYTE* pPlainTextKey, WSEC_UINT32 ulKeyLen);
WSEC_ERR_T KMC_CreateMk(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType);
WSEC_ERR_T KMC_GetMkDetail(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen);
WSEC_ERR_T KMC_SecureEraseKeystore();

/* (2) ���� */
/* 2.1 ��Կ�������� */
WSEC_ERR_T KMC_SetRootKeyCfg(const KMC_CFG_ROOT_KEY_STRU* pstRkCfg);
WSEC_ERR_T KMC_SetKeyManCfg(const KMC_CFG_KEY_MAN_STRU* pstKmCfg);
WSEC_ERR_T KMC_GetRootKeyCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg);
WSEC_ERR_T KMC_GetKeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKmCfg);

/* 2.2 Domain���� */
WSEC_ERR_T KMC_AddDomain(const KMC_CFG_DOMAIN_INFO_STRU* pstDomain);
WSEC_ERR_T KMC_RmvDomain(WSEC_UINT32 ulDomainId);

WSEC_ERR_T KMC_AddDomainKeyType(WSEC_UINT32 ulDomainId, const KMC_CFG_KEY_TYPE_STRU* pstKeyType);
WSEC_ERR_T KMC_RmvDomainKeyType(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType);

WSEC_INT32 KMC_GetDomainCount(); /* ��ȡ���õ�Domain���� */
WSEC_ERR_T KMC_GetDomain(WSEC_INT32 Index, KMC_CFG_DOMAIN_INFO_STRU* pstDomainInfo); /* ��ȡָ��λ���ϵ�Domain */

WSEC_INT32 KMC_GetDomainKeyTypeCount(WSEC_UINT32 ulDomainId); /* ��ȡDomain��KeyType���� */
WSEC_ERR_T KMC_GetDomainKeyType(WSEC_UINT32 ulDomainId, WSEC_INT32 Index, KMC_CFG_KEY_TYPE_STRU* pstKeyType); /* ��ȡָ��Domainָ��λ���ϵ�KeyType */

WSEC_VOID KMC_GetExpiredMkStartPos(WSEC_POSITION* pPos); /* ��ȡ����MK����λ�� */
WSEC_BOOL KMC_GetExpiredMkByPos(WSEC_POSITION* pPosNow, KMC_MK_INFO_STRU* pstExpiredMk); /* ��ȡָ��λ���ϵĹ���MK��������һ������MK��λ�� */

WSEC_ERR_T KMC_SetDataProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, const KMC_CFG_DATA_PROTECT_STRU* pstPara);
WSEC_ERR_T KMC_GetDataProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstPara);

/* �г�CBB֧�ֵİ�ȫ�㷨 */
typedef WSEC_VOID (*KMC_FP_ProcAlg)(WSEC_UINT32 ulAlgId, const WSEC_CHAR* pszAlgName, WSEC_VOID* pReserved);
WSEC_ERR_T KMC_GetAlgList(KMC_FP_ProcAlg pfProcAlg, INOUT WSEC_VOID *pReserved);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif /* __cplusplus */

#endif /* __KMC_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__ */

