/******************************************************************************

��Ȩ���� (C), 2001-2011, ��Ϊ�������޹�˾

******************************************************************************
�� �� ��   : KMC_Pri.h
�� �� ��   : ����
��    ��   : x00102361
��������   : 2014��6��16��
����޸�   :
��������   : KMC_Func.c ���ڲ��ӿ�ͷ�ļ��������⿪��
�����б�   :
�޸���ʷ   :
1.��    ��   : 2014��6��16��
��    ��   : x00102361
�޸�����   : �����ļ�

******************************************************************************/
#ifndef __KMC_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__
#define __KMC_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__

#include "wsec_type.h"
#include "wsec_config.h"
#include "wsec_pri.h"
#include "wsec_array.h"

#ifdef __cplusplus
#if __cplusplus
extern "C"{
#endif
#endif /* __cplusplus */

/*=============================================================
                 �ļ��汾����ʷ
1. Keystore�ļ��汾��:
KMC_KSF_VER = 1     2014-12-31 �װ汾����

2. MK�ļ��汾��:
KMC_MKF_VER = 1     2014-12-31 �װ汾����
=============================================================*/
#define KMC_KSF_VER (1) /* Keystore�ļ��汾�� */
#define KMC_MKF_VER (1) /* MK�ļ��汾�� */
#define KMC_KCF_VER (1) /* KMC�����ļ��汾�� */

#define KMC_RMK_LEN      (32) /* RMK��Կ���� */
#define KMC_EK4MKF_LEN   (32) /* MKF(MK�ļ�)������Կ���� */
#define KMC_KEY4HMAC_LEN (32) /* ����HMAC����Կ���� */
#define KMC_HMAC_RST_LEN (32) /* HMAC���������� */

/* CBBר��Domain */
#define KMC_PRI_DOMAIN_ID_MIN (1024)
#define KMC_PRI_DOMAIN_ID_MAX (1056)

#define KMC_ITER_COUNT_MIN (1)
#define KMC_ITER_COUNT_MAX (100000)
#define KMC_IS_KEYITERATIONS_VALID(ulKeyIterations) ((KMC_ITER_COUNT_MIN <= (ulKeyIterations)) && ((ulKeyIterations) <= KMC_ITER_COUNT_MAX))

/*----------------------------------------------*
 * KMC�����ļ�TLV��ʽ֮Tag����
 *----------------------------------------------*/
typedef enum 
{
    KMC_CFT_HDR = 1,         /* �ļ�ͷ */
    KMC_CFT_RK_CFG,          /* RootKey���� */
    KMC_CFT_KEY_MAN,         /* ��Կ������ص����ò��� */
    KMC_CFT_DP_CFG,          /* ���ݱ������� */
    KMC_CFT_DOMAIN_CFG,      /* Domain���� */
    KMC_CFT_DOMAIN_KEY_TYPE  /* Domain��KeyType���� */
} KMC_CFG_FILE_TLV_ENUM;

/*----------------------------------------------*
 * KMC����������
 *----------------------------------------------*/
typedef enum
{
    KMC_LOCK_NONE     = 0, /* ���� */
    KMC_LOCK_CFG      = 1, /* ������ */
    KMC_LOCK_KEYSTORE = 2, /* ��Keystore */
    KMC_LOCK_BOTH     = 3  /* �����ü�Keystore */
} KMC_LOCK_TYPE_ENUM;

typedef enum
{
    KMC_NOT_LOCK, /* ������ */
    KMC_NEED_LOCK /* ����� */
} KMC_LOCK_OPER_ENUM;/* �Ӻ�������, Ҫ���Ӻ����Ƿ���ٽ���Դ���� */

/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                      Root Key(RK)���ݽṹ
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
#pragma pack(1)
typedef struct tagKMC_RK_PARA
{
    WSEC_BYTE abRkMeterial1[32]; /* ����Կ����1 */
    WSEC_BYTE abRkMeterial2[32]; /* ����Կ����2 */
    WSEC_BYTE abReserved[32]; /* Ԥ�� */
    WSEC_BYTE abRmkSalt[32];     /* ����RMK��ֵ */
} KMC_RK_PARA_STRU; /* Root Key������Կ�Ļ������� */
#pragma pack()

#pragma pack(1)
typedef struct tagKMC_KSF_RK
{
    KMC_RK_ATTR_STRU stRkAttr; /* RK�������� */
    KMC_RK_PARA_STRU stRkPara; /* RootKey��Կ������� */
    WSEC_UINT32     ulMkNum;         /* MasterKey���� */
    WSEC_BYTE       abReserved[36];  /* ���� */
    WSEC_BYTE       abAboveHash[32]; /* SHA256(stMemRk, ulMkNum, abReserved) */
} KMC_KSF_RK_STRU; /* Keystore��RootKey��Ϣ */
#pragma pack()
/*////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/


/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                      Master Key(MK)���ݽṹ
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
#pragma pack(1)
typedef struct tagKMC_MK_REAR
{
    WSEC_UINT32 ulPlainLen;        /* MK��Կ���ĳ��� */
    WSEC_BYTE   abKey[WSEC_MK_LEN_MAX]; /* ��Կ(1. פ���ļ�: RMK���ܺ������, 2. פ���ڴ�: RMK���ܺ�ļ�������) */
} KMC_MK_REAR_STRU; /* MKβ��Ϣ */
#pragma pack()

#pragma pack(1)
typedef struct tagKMC_KSF_MK
{
    KMC_MK_INFO_STRU stMkInfo;       /* MK������Ϣ */
    WSEC_BYTE        abReserved[44]; /* ���� */
    WSEC_BYTE        abIv[16];       /* MKʹ��RMK���ܴ洢IV */
    WSEC_UINT32      ulCipherLen;    /* MK��Կ���ĳ��� */
    KMC_MK_REAR_STRU stMkRear;       /* MKβ����Ϣ */
    WSEC_BYTE        abMkHash[32];   /* HMAC-SHA256(stMkInfo, abReserved, stMkRear) */
} KMC_KSF_MK_STRU; /* �洢��KSF�е�MK��Ϣ(��פ���ڴ�) */
#pragma pack()

#pragma pack(1)
typedef struct tagKMC_MEM_MK
{
    KMC_MK_INFO_STRU stMkInfo; /* MK������Ϣ */
    KMC_MK_REAR_STRU stMkRear; /* MKβ����Ϣ */
} KMC_MEM_MK_STRU; /* פ���ڴ��е�MK��Ϣ����ܺ�д��MKF */
#pragma pack()
/*////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

typedef struct tagKMC_KSF_MEM
{
    WSEC_CHAR*       pszFromFile; /* �����������ĸ�Keystore�ļ� */
    KMC_RK_ATTR_STRU stRkInfo; /* Root Key��Ϣ */
    WSEC_ARRAY       arrMk;    /* MK����, ��Ԫ�ش洢 KMC_MEM_MK_STRU �͵�ַ, ��ulDomainId+usType���ظ���������, ulDomainId+ulKeyIdΨһ */
} KMC_KSF_MEM_STRU; /* �ڴ��е�KSF�ļ� */

/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                      KMC�������ݽṹ���ڴ桿
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
typedef struct tagKMC_DOMAIN_CFG
{
    KMC_CFG_DOMAIN_INFO_STRU stDomainInfo;
    WSEC_ARRAY               arrKeyTypeCfg; /* KeyType����, ��Ԫ�ش洢 KMC_CFG_KEY_TYPE_STRU �͵�ַ, ��usKeyType�������� */
} KMC_DOMAIN_CFG_STRU;

typedef struct tagKMC_CFG
{
    KMC_CFG_ROOT_KEY_STRU     stRkCfg; /* RK���������Ϣ(ֻ���CBB�ڲ�����RKʱ��Ч) */
    KMC_CFG_KEY_MAN_STRU      stKmCfg; /* ��Կ������ص����ò��� */
    KMC_CFG_DATA_PROTECT_STRU astDataProtectCfg[3];
    WSEC_ARRAY                arrDomainCfg;    /* Domain����, ��Ԫ�ش洢 KMC_DOMAIN_CFG_STRU �͵�ַ, ��DomainId�������� */
} KMC_CFG_STRU;

typedef struct tagKMC_BACKUP_PARA
{
    KMC_FP_CALLBACK_STRU   stCallbackFun;
    WSEC_PROGRESS_RPT_STRU stProgressRpt;
} KMC_BACKUP_PARA_STRU;

typedef struct tagKMC_SYS
{
    KMC_RK_GEN_FROM eRootKeyFrom;                  /* RootKey��Դ */
    WSEC_CHAR*      apszKeystoreFile[2];           /* Keystore�ļ���(CBB�����ڴ����ļ���) */
    WSEC_CHAR*      apszKmcCfgFile[2];             /* KMC�����ļ��ļ���(CBB�����ڴ����ļ���) */
    WSEC_BYTE       abMkMaskCode[WSEC_MK_LEN_MAX]; /* ��MK���ļ��ڴ�������� */
    WSEC_UINT32     ulMkPlainLenMax;               /* MK������󳤶� */
    KMC_BACKUP_PARA_STRU stBackupPara;
    WSEC_RUN_STATE_ENUM  eState; /* KMC״̬ */
    WSEC_BYTE            bCorrectClockFlag; /* �Ƿ���У����ʱ�� */
} KMC_SYS_STRU;

/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                      KMC�������ݽṹ���ļ���
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
#pragma pack(1)
typedef struct tagKMC_KCF_HDR
{
    WSEC_BYTE   abFormatFlag[32]; /* ��ʽ��� */
    WSEC_UINT32 ulVer;            /* �汾�� */
    WSEC_UINT32 ulHashAlgId;      /* Hash�㷨 */
    WSEC_BYTE   abReserved[16];   /* Ԥ�� */
    WSEC_BYTE   abHash[32];       /* ���ļ�ͷ���������ݵ�Hashֵ */
} KMC_KCF_HDR_STRU; /* KMC�����ļ�ͷ */
#pragma pack()

typedef struct tagKMC_READ_KCF_CTX
{
    WSEC_CHAR* pszFile;      /* �����ļ��� */
    WSEC_FILE  pFile;        /* �򿪵��ļ� */
    WSEC_BUFF  stReadBuff;   /* ���ļ������� */
} KMC_READ_KCF_CTX_STRU; /* ��ȡKMC�����ļ�����������Ϣ */

typedef struct tagKMC_INI_CTX
{
    WSEC_BOOL              bCbbReadCfg;   /* �Ƿ���CBB��KMC���� */
    KMC_READ_KCF_CTX_STRU  stReadKcfCtx;  /* �������ļ��������� */
    WSEC_PROGRESS_RPT_STRU stProgressRpt; /* �ϱ����ȵĻص����������� */
} KMC_INI_CTX_STRU; /* KMC��ʼ���׶�ʹ�õ���������Ϣ */

/*==========================================================================================
                MK�ļ�(���MKF)���ݽṹ(����/����)
��ʽ:
    0. 32�ֽڸ�ʽ��
    1. KMC_MKF_HDR_WITH_HMAC_STRU
    2. n��MK��������(KMC_MKF_MK_STRU)
    3. ����MK���ĵ�HMAC
==========================================================================================*/
#pragma pack(1)
typedef struct tagKMC_MKF_HDR
{
    WSEC_UINT16 usVer;                 /* MK�ļ��汾�� */
    WSEC_UINT16 usKsfVer;              /* Keystore�ļ��汾�� */
    WSEC_UINT32 ulEncryptAlgId;        /* �����㷨ID */
    WSEC_UINT32 ulIteration4EncrypKey; /* ���ɼ�����Կʱ�������� */
    WSEC_BYTE   abSalt4EncrypKey[16];  /* ���ɼ�����Կʱʹ�õ���ֵ */
    WSEC_BYTE   abIv4EncrypMk[16];     /* ����MKʱʹ�õ�IV */
    WSEC_BYTE   Reserved1[16];         /* Ԥ�� */

    WSEC_UINT32 ulHmacAlgId;           /* HMAC�㷨ID */
    WSEC_UINT32 ulIteration4HmacKey;   /* ����HMAC��Կʱ�������� */
    WSEC_BYTE   abSalt4HmacKey[16];    /* ����HMAC��Կʱʹ�õ���ֵ */
    WSEC_UINT32 ulCipherLenPerMk;      /* ����MK���ĳ��� */
    WSEC_UINT32 ulMkNum;               /* MK���� */
    WSEC_BYTE   Reserved2[16];         /* Ԥ�� */
} KMC_MKF_HDR_STRU; /* MK�ļ�ͷ */
#pragma pack()

#pragma pack(1)
typedef struct tagKMC_MKF_HDR_WITH_HMAC
{
    KMC_MKF_HDR_STRU stHdr;
    WSEC_BYTE        abHmac[KMC_HMAC_RST_LEN]; /* �������ݵ�HMAC */
} KMC_MKF_HDR_WITH_HMAC_STRU;
#pragma pack()

#pragma pack(1)
typedef struct tagKMC_MKF_MK
{
    KMC_MK_INFO_STRU stMkInfo;   /* MK������Ϣ */
    WSEC_BYTE        abIv[16];   /* MKʹ��RMK���ܴ洢IV */
    WSEC_UINT32      ulPlainLen; /* ��Կ���ĳ���, ���ܳ��� WSEC_KEY_LEN */
    WSEC_BYTE        abPlainText[WSEC_MK_LEN_MAX]; /* ��Կ���� */
} KMC_MKF_MK_STRU; /* MK�ļ��е�MK��¼(���ܴ洢) */
#pragma pack()

/* KMC˽�к��� */
KMC_KSF_MEM_STRU* KMC_PRI_FreeKsfSnapshot(KMC_KSF_MEM_STRU* pData);
KMC_CFG_STRU* KMC_PRI_FreeKmcCfg(KMC_CFG_STRU* pKmcCfg);
WSEC_VOID KMC_PRI_FreeGlobalMem(KMC_CFG_STRU** ppKmcCfg, KMC_KSF_MEM_STRU** ppKeystore);

WSEC_ERR_T KMC_PRI_Finalize(KMC_LOCK_OPER_ENUM eLock);
WSEC_ERR_T KMC_PRI_UpdateRootKeyPri(const WSEC_BYTE* pbKeyEntropy, WSEC_SIZE_T ulSize, KMC_LOCK_OPER_ENUM eLock);
WSEC_ERR_T KMC_PRI_GetMaxMkId(KMC_LOCK_OPER_ENUM eLock, WSEC_UINT32 ulDomainId, WSEC_UINT32* pulMaxKeyId);
WSEC_ERR_T KMC_PRI_GetMkDetail(KMC_LOCK_OPER_ENUM eLock, WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen);

WSEC_VOID KMC_PRI_MakeDefaultCfg4RootKey(KMC_CFG_ROOT_KEY_STRU* pstRkCfg);
WSEC_VOID KMC_PRI_MakeDefaultCfg4KeyMan(KMC_CFG_KEY_MAN_STRU* pstKmCfg);
WSEC_VOID KMC_PRI_MakeDefaultCfg4DataProtect(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstCfg);
WSEC_BOOL KMC_PRI_MakeDefaultCfg4Domain(KMC_CFG_STRU* pKmcCfg);

/* KMC������������ʱ, ���к������APP��ɻص����ú����Ĺ��� */
WSEC_BOOL KMC_PRI_ReadRootKeyCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg); /* ��ȡRootKey���� */
WSEC_BOOL KMC_PRI_ReadKeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKmCfg); /* ��ȡKEY�������� */
WSEC_BOOL KMC_PRI_ReadCfgOfDomainCount(WSEC_UINT32* pulDomainCount); /* ��ȡKMC��������֮ Domain���� */
WSEC_BOOL KMC_PRI_ReadCfgOfDomainInfo(KMC_CFG_DOMAIN_INFO_STRU* pstAllDomainInfo, WSEC_UINT32 ulDomainCount); /* ��ȡ����Domain��������Ϣ */
WSEC_BOOL KMC_PRI_ReadCfgOfDomainKeyTypeCount(WSEC_UINT32 ulDomainId, WSEC_UINT32* pulKeyTypeCount); /* ��ȡָ��Domain�ж�����KeyType���� */
WSEC_BOOL KMC_PRI_ReadCfgOfDomainKeyType(WSEC_UINT32 ulDomainId, KMC_CFG_KEY_TYPE_STRU* pstDomainAllKeyType, WSEC_UINT32 ulKeyTypeCount); /* ��ȡָ��Domain������KeyType���� */
WSEC_BOOL KMC_PRI_ReadCfgOfDataProtection(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstPara); /* ��ȡָ�����͵��㷨���� */
WSEC_ERR_T KMC_PRI_OpenIniCtx(WSEC_BOOL bManCfgFile);
WSEC_VOID KMC_PRI_CloseIniCtx();

/* ������ò����Ϸ��� */
WSEC_ERR_T KMC_PRI_ChkProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, const KMC_CFG_DATA_PROTECT_STRU *pstPara);

/* дKMC�����ļ�(KCF: KMC Configure File) */
WSEC_ERR_T KMC_PRI_WriteKcfSafety(const KMC_CFG_STRU* pKmcCfg);
WSEC_ERR_T KMC_PRI_WriteKcf(const KMC_CFG_STRU* pKmcCfg, const WSEC_CHAR* pszKmcCfgFile, const WSEC_VOID* pvReserved);
WSEC_ERR_T KMC_PRI_ChkCfgFile(KMC_READ_KCF_CTX_STRU* pstCtx);

/* ��/дKeystore�ļ�(KSF: KeyStore File) */
WSEC_ERR_T KMC_PRI_ReadKsfSafety(KMC_KSF_MEM_STRU** ppKeystore);
WSEC_ERR_T KMC_PRI_ReadKsf(const WSEC_CHAR* pszKeystoreFile, KMC_KSF_MEM_STRU* pKeystoreData);
WSEC_ERR_T KMC_PRI_ReadRootKey(const WSEC_CHAR* pszFile, KMC_KSF_RK_STRU* pRk);
WSEC_ERR_T KMC_PRI_ReadKsfBasedVer(const WSEC_CHAR* pszKeystoreFile, WSEC_UINT16 usVer, KMC_KSF_MEM_STRU* pKeystoreData);

WSEC_ERR_T KMC_PRI_WriteKsfSafety(const KMC_KSF_MEM_STRU* pKeystoreData, const KMC_KSF_RK_STRU* pRk);
WSEC_ERR_T KMC_PRI_WriteKsf(const KMC_KSF_MEM_STRU* pKeystoreData, const WSEC_CHAR* pszFile, const KMC_KSF_RK_STRU* pRk);

WSEC_ERR_T KMC_PRI_CreateRootKey(const WSEC_BUFF* pstEntropy, KMC_KSF_RK_STRU* pRk);
WSEC_ERR_T KMC_PRI_CreateMkArr(KMC_KSF_MEM_STRU* pKeystore);
WSEC_ERR_T KMC_PRI_CreateMkItem(KMC_KSF_MEM_STRU* pKeystore, const KMC_CFG_DOMAIN_INFO_STRU* pstDomain, const KMC_CFG_KEY_TYPE_STRU* pstKeyType, const WSEC_BUFF* pKeyPlain, WSEC_UINT32 ulKeyId);
WSEC_ERR_T KMC_PRI_ReCreateMkItem(KMC_KSF_MEM_STRU* pKeystore, KMC_MK_INFO_STRU* pMk);
WSEC_ERR_T KMC_PRI_CreateKsf(KMC_KSF_MEM_STRU* pKeystoreData, KMC_KSF_RK_STRU* pRk);

WSEC_BOOL KMC_PRI_MakeRmk(const KMC_KSF_RK_STRU* pstRk, WSEC_BUFF* pstBuff);
WSEC_ERR_T KMC_PRI_MakeMk(const KMC_CFG_DOMAIN_INFO_STRU* pstDomain, const KMC_CFG_KEY_TYPE_STRU* pstKeyType, const WSEC_BUFF* pKeyPlain, WSEC_UINT32 ulKeyId, KMC_MEM_MK_STRU* pMk);
WSEC_VOID KMC_PRI_GetRkMeterial(WSEC_BYTE* pMeterial, WSEC_SIZE_T nSize);
WSEC_ERR_T KMC_PRI_CalcMkPlainLenMax(WSEC_UINT32* pulLenMax);

WSEC_BOOL KMC_PRI_IsTime4ChkKey(const WSEC_SYSTIME_T* pstLocalNow, const KMC_CFG_KEY_MAN_STRU* pstCfg, WSEC_EXEC_INFO_STRU* pExecInfo);
WSEC_ERR_T KMC_PRI_SetLifeTime(WSEC_UINT32 ulLifeDays, WSEC_SYSTIME_T* pstCreateUtc, WSEC_SYSTIME_T* pstExpireUtc);

WSEC_VOID KMC_PRI_CvtByteOrder4KsfRk(KMC_KSF_RK_STRU* pstRk, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4KsfMk(KMC_KSF_MK_STRU* pstMk, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4MkInfo(KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4MkfHdr(KMC_MKF_HDR_STRU* pstMkfHdr, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4KcfHdr(KMC_KCF_HDR_STRU* pstKcfHdr, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4RkCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4KeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKeyManCfg, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4DataProtectCfg(KMC_CFG_DATA_PROTECT_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4DomainInfo(KMC_CFG_DOMAIN_INFO_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID KMC_PRI_CvtByteOrder4KeyType(KMC_CFG_KEY_TYPE_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper);

WSEC_VOID KMC_PRI_NtfWriCfgFileFail(WSEC_ERR_T ulCause);
WSEC_VOID KMC_PRI_NtfMkChanged(const KMC_MK_INFO_STRU* pMk, KMC_KEY_CHANGE_TYPE_ENUM eType);
WSEC_VOID KMC_PRI_NtfRkExpire(const KMC_RK_ATTR_STRU* pRk, WSEC_INT32 nRemainLifeDays);
WSEC_ERR_T KMC_PRI_LoadData(KMC_CFG_STRU** ppKmcCfg, KMC_KSF_MEM_STRU** ppKeystore, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const KMC_FP_CFG_CALLBACK_STRU* pstCfgFun, WSEC_BOOL bCbbManCfg);
WSEC_ERR_T KMC_PRI_CfgDataInit(const KMC_FP_CFG_CALLBACK_STRU* pCallbackFun, KMC_CFG_STRU* pKmcCfg);
WSEC_ERR_T KMC_PRI_AddDomain2Array(KMC_CFG_STRU* pKmcCfg, const KMC_CFG_DOMAIN_INFO_STRU* pDomainInfo);
WSEC_ERR_T KMC_PRI_AddDomainKeyType2Array(KMC_CFG_STRU* pKmcCfg, WSEC_UINT32 ulDomainId,const KMC_CFG_KEY_TYPE_STRU* pKeyType);
WSEC_ERR_T KMC_PRI_AddMk2Array(KMC_KSF_MEM_STRU* pKeystore, KMC_MEM_MK_STRU* pMk);

WSEC_INT32 KMC_PRI_CompareDomain4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2);
WSEC_INT32 KMC_PRI_CompareDomainKeyType4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2);
WSEC_INT32 KMC_PRI_CompareMk4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2);

WSEC_BOOL KMC_PRI_SearchDomainKeyTypeCfg(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType, KMC_DOMAIN_CFG_STRU** ppDomain, KMC_CFG_KEY_TYPE_STRU** ppKeyType);
KMC_DOMAIN_CFG_STRU* KMC_PRI_SearchDomain(const KMC_CFG_STRU* pKmcCfg, WSEC_UINT32 ulDomainId);
WSEC_INT32 KMC_PRI_SearchMkByKeyId(const KMC_KSF_MEM_STRU* pKeystore, WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId);

WSEC_VOID KMC_PRI_OnRemoveDomainArr(WSEC_VOID *pElement);
WSEC_VOID KMC_PRI_OnRemoveMkArr(WSEC_VOID *pElement);

WSEC_VOID KMC_PRI_Lock(KMC_LOCK_TYPE_ENUM eType);
WSEC_VOID KMC_PRI_Unlock(KMC_LOCK_TYPE_ENUM eType);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif /* __cplusplus */


#endif /* __KMC_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__ */

