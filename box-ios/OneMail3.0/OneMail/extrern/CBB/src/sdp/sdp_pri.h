/******************************************************************************

                  ��Ȩ���� (C), 2001-2011, ��Ϊ�������޹�˾

 ******************************************************************************
  �� �� ��   : SDP_Pri.h
  �� �� ��   : ����
  ��    ��   : x00102361
  ��������   : 2014��6��16��
  ����޸�   :
  ��������   : SDP_Func.c ���ڲ��ӿ�ͷ�ļ��������⿪��
  �����б�   :
  �޸���ʷ   :
  1.��    ��   : 2014��6��16��
    ��    ��   : x00102361
    �޸�����   : �����ļ�

******************************************************************************/
#ifndef __SDP_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__
#define __SDP_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__

#include "wsec_type.h"
#include "wsec_pri.h"
#include "cac_pri.h"
#include "kmc_itf.h"
#include "sdp_itf.h"

#ifdef __cplusplus
#if __cplusplus
extern "C"{
#endif
#endif /* __cplusplus */

#define WSEC_HMAC_LEN_MAX (64)

/*----------------------------------------------*
 * �߽�ֵ����
 *----------------------------------------------*/
#define SDP_SALT_LEN            16u
#define SDP_IV_MAX_LEN          16u /* �Գ��㷨ʹ�õ�IV�ĳ��� */
#define SDP_PTMAC_MAX_LEN       64u /* �Գ��㷨����ͷ��������HMAC�ĳ��� */
#define SDP_KEY_MAX_LEN        128u
#define SDP_ALGNAME_MAX_LEN     64u /* �㷨���Ƶĳ��� */
#define SDP_SYM_MAX_BLOCK_SIZE  16u /* �ԳƼ������鳤 */
#define SDP_HMAC_MAX_SIZE       64u /* HMAC�㷨���� */

/*----------------------------------------------*
 * �����ļ�TLV��ʽ֮Tag����
 *----------------------------------------------*/
typedef enum 
{
    SDP_CFT_FILE_HDR = 1, /* �����ļ�ͷ */
    SDP_CFT_CIPHER_HDR,   /* ����ͷ */
    SDP_CFT_CIPHER_BODY,  /* ������ */
    SDP_CFT_HMAC_VAL      /* HMACֵ */
} SDP_CIPHER_FILE_TLV_ENUM;

/*----------------------------------------------*
 * ����ͷ����
 *----------------------------------------------*/
#pragma pack(1)
typedef struct
{
    WSEC_UINT32 ulVersion; /* ���ݱ���ģ��汾�� */
    WSEC_BOOL   bHmacFlag; /* �Ƿ��������HMAC,����������HMAC������HMAC��������ĵĺ���. ���ڴ����ݵļӽ�����ʱ��֧������HMAC*/
    WSEC_UINT32 ulDomain;  /* KEYID��Ӧ���� */
    WSEC_UINT32 ulAlgId;   /* �㷨ID*/
    WSEC_UINT32 ulKeyId;/* ����HMACʹ�õ�KEYID */
    WSEC_UINT32 ulIterCount; /* �����ִΣ�ʹ��Ӧ�����ø���Կ����ģ������������Կ���ִ� */
    WSEC_UINT8 aucSalt[SDP_SALT_LEN];/* ���ݱ���ģ�����ɵ���ֵ */
    WSEC_UINT8 aucIV[SDP_IV_MAX_LEN];/* ���ݱ���ģ�����ɵ���ֵ */    
    WSEC_UINT32 ulCDLen;/* ���ܺ���������ݳ��� */
}SDP_CIPHER_HEAD_STRU;/* �ԳƼ��ܵ�����ͷ */
#pragma pack()

/*----------------------------------------------*
 * HMACͷ����
 *----------------------------------------------*/
#pragma pack(1)
typedef struct
{
    WSEC_UINT32 ulVersion;  /* ���ݱ���ģ��汾�� */
    WSEC_UINT32 ulDomain;  /* KEYID��Ӧ���� */
    WSEC_UINT32 ulAlgId;   /* �㷨ID*/
    WSEC_UINT32 ulKeyId;/* ����HMACʹ�õ�KEYID */
    WSEC_UINT32 ulIterCount; /* �����ִΣ�ʹ��Ӧ�����ø���Կ����ģ������������Կ���ִ� */
    WSEC_UINT8 aucSalt[SDP_SALT_LEN];/* ���ݱ���ģ�����ɵ���ֵ */
}SDP_HMAC_HEAD_STRU;
#pragma pack()

/*----------------------------------------------*
 * �������ͷ����
 *----------------------------------------------*/
#pragma pack(1)
typedef struct
{
    WSEC_UINT32 ulVersion;  /* ���ݱ���ģ��汾�� */
    WSEC_UINT32 ulAlgId;    /* �㷨ID*/
    WSEC_UINT32 ulIterCount; /* �����ִΣ�ʹ��Ӧ�����ø���Կ����ģ������������Կ���ִ� */
    WSEC_UINT8 aucSalt[SDP_SALT_LEN];/* ���ݱ���ģ�����ɵ���ֵ */
    WSEC_UINT32 ulCDLen;     /* ���ܺ�Ŀ���� */
}SDP_PWD_HEAD_STRU;
#pragma pack()

/*----------------------------------------------*
 * �����ݼӽ�������������Ķ���
 *----------------------------------------------*/
typedef struct
{
    WSEC_CRYPT_CTX          stWsecCtx;    /* CAC ����������Ļ��� */
    SDP_CRYPT_CTX           stSdpCtxHmac; /* SDP ����������Ļ���������������У�� */
    SDP_CIPHER_HEAD_STRU    stCipherHead; /* �����ݼӽ��ܵ��Ѽ�¼����ͷ�� */
    SDP_HMAC_HEAD_STRU      stHmacHead;   /* �����ݼ���HMAC���Ѽ�¼HMACͷ�� */
    WSEC_ALGTYPE_E          eAlgType;     /* ��ǰִ�е��㷨���� */
} SDP_CRYPT_CTX_STRU;

/*----------------------------------------------*
 * ������������Ϣ
 *----------------------------------------------*/
typedef struct
{
    WSEC_UINT32          ulErrCount;         /* ������� */
    WSEC_UINT32          ulLastErrCode;      /* �ϴεĴ����� */
} SDP_ERROR_CTX_STRU;

/*----------------------------------------------*
 * �����ļ�ͷ�ṹ����
 *----------------------------------------------*/
#pragma pack(1)
typedef struct tagSDP_CIPHER_FILE_HDR
{
    WSEC_BYTE      abFormatFlag[32];    /* ��ʽ��Ƿ� */
    WSEC_UINT32    ulVer;               /* �����ļ��汾�� */
    WSEC_UINT32    ulPlainBlockLenMax;  /* ������Ķγ��� */
    WSEC_UINT32    ulCipherBlockLenMax; /* ������Ķγ��� */
    WSEC_SYSTIME_T tCreateFileTimeUtc;  /* �����ļ�����ʱ��(UTC) */
    WSEC_SYSTIME_T tSrcFileCreateTime;  /* Դ�ļ�����ʱ�� */
    WSEC_SYSTIME_T tSrcFileEditTime;    /* Դ�ļ�����޸�ʱ�� */
    WSEC_BYTE      abReserved[16];      /* Ԥ�� */
} SDP_CIPHER_FILE_HDR_STRU;
#pragma pack()

#define SDP_CIPHER_HEAD_STRU_LEN     sizeof(SDP_CIPHER_HEAD_STRU)
#define SDP_HMAC_HEAD_STRU_LEN       sizeof(SDP_HMAC_HEAD_STRU)
#define SDP_PWD_HEAD_STRU_LEN        sizeof(SDP_PWD_HEAD_STRU)
#define SDP_CRYPT_CTX_LEN            sizeof(SDP_CRYPT_CTX_STRU)

/*----------------------------------------------*
 * ��鳤���Ƿ񳬹�������������                 *
 *----------------------------------------------*/
typedef WSEC_BYTE AssertValidLengthCipherHead[SDP_CIPHER_HEAD_LEN - SDP_CIPHER_HEAD_STRU_LEN];
typedef WSEC_BYTE AssertValidLengthHmacHead[SDP_HMAC_HEAD_LEN - SDP_HMAC_HEAD_STRU_LEN];
typedef WSEC_BYTE AssertValidLengthPwdHead[SDP_PWD_HEAD_LEN - SDP_PWD_HEAD_STRU_LEN];

/*----------------------------------------------*
 * ˽�к���ԭ��˵��                             *
 *----------------------------------------------*/
WSEC_ERR_T SDP_GetAlgProperty(
    WSEC_UINT32 ulAlgID, WSEC_CHAR *pcAlgName, WSEC_UINT32 ulAlgNameLen,
    WSEC_ALGTYPE_E *peAlgType,
    WSEC_UINT32 *pulKeyLen,
    WSEC_UINT32 *pulIVLen,
    WSEC_UINT32 *pulBlockLen,
    WSEC_UINT32 *pulMACLen);

WSEC_VOID SDP_CvtByteOrder4CipherFileHdr(SDP_CIPHER_FILE_HDR_STRU* pstFileHdr, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID SDP_CvtByteOrder4CipherTextHeader(SDP_CIPHER_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID SDP_CvtByteOrder4HmacTextHeader(SDP_HMAC_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper);
WSEC_VOID SDP_CvtByteOrder4PwdCipherTextHeader(SDP_PWD_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper);

WSEC_ERR_T SDP_FillCipherTextHeader(
    KMC_SDP_ALG_TYPE_ENUM eIntfType, WSEC_UINT32 ulDomain,
    SDP_CIPHER_HEAD_STRU *pstCipherHead,
    WSEC_BYTE *pucKey, WSEC_UINT32 *pulKeyLen,
    WSEC_UINT32 *pulIVLen);
WSEC_ERR_T SDP_FillHmacTextHeader(KMC_SDP_ALG_TYPE_ENUM eIntfType, WSEC_UINT32 ulDomain,
                                  SDP_HMAC_HEAD_STRU *pstHmacHead,
                                  WSEC_BYTE *pucKey, WSEC_UINT32 *pulKeyLen);
WSEC_ERR_T SDP_FillPwdCipherTextHeader(KMC_SDP_ALG_TYPE_ENUM eIntfType, SDP_PWD_HEAD_STRU *pstCipherHead);

WSEC_ERR_T SDP_GetWorkKey(
    WSEC_UINT32 ulDomain,
    WSEC_UINT16 usKeyType,
    WSEC_UINT32 *pulKeyId,
    WSEC_UINT32 *pulIterCount,
    WSEC_BYTE *pucSalt, WSEC_UINT32 ulSaltLen,
    WSEC_BYTE *pucIV, WSEC_UINT32 ulIVLen,
    WSEC_BYTE *pucKey, WSEC_UINT32 ulKeyLen);
WSEC_ERR_T SDP_GetWorkKeyByID(
    WSEC_UINT32 ulDomain,
    WSEC_UINT32 ulKeyId,
    WSEC_UINT32 ulIterCount,
    const WSEC_BYTE *pucSalt, WSEC_UINT32 ulSaltLen,
    WSEC_BYTE *pucKey, WSEC_UINT32 ulKeyLen);
WSEC_VOID SDP_FreeCtx(SDP_CRYPT_CTX *pstSdpCtx);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif /* __cplusplus */


#endif /* __SDP_PRI_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__ */

