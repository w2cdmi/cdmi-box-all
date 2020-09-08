/******************************************************************************

                  ��Ȩ���� (C), 2001-2011, ��Ϊ�������޹�˾

 ******************************************************************************
  �� �� ��   : SDP_Itf.h
  �� �� ��   : ����
  ��    ��   : x00102361
  ��������   : 2014��6��16��
  ����޸�   :
  ��������   : SDP_Func.c �Ķ���ӿ�ͷ�ļ�
  �����б�   :
  �޸���ʷ   :
  1.��    ��   : 2014��6��16��
    ��    ��   : x00102361
    �޸�����   : �����ļ�

******************************************************************************/
#ifndef __SDP_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__
#define __SDP_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__

#include "wsec_itf.h"
#include "kmc_itf.h"

/* Keep unchanged for each version, include length of reserved bytes */
#define SDP_CIPHER_HEAD_LEN     (68)
#define SDP_HMAC_HEAD_LEN       (44)
#define SDP_PWD_HEAD_LEN        (40)

#ifdef __cplusplus
#if __cplusplus
extern "C"{
#endif
#endif /* __cplusplus */

typedef WSEC_VOID* SDP_CRYPT_CTX; /* �����ݼӽ��������� */

/*----------------------------------------------*
 * ����ͷ����
 *----------------------------------------------*/
#pragma pack(1)
typedef struct
{
    WSEC_BYTE abyBuffer[SDP_CIPHER_HEAD_LEN];
}SDP_CIPHER_HEAD;
#pragma pack()

#pragma pack(1)
typedef struct
{
    WSEC_BYTE abyBuffer[SDP_HMAC_HEAD_LEN];
}SDP_HMAC_ALG_ATTR;
#pragma pack()

#pragma pack(1)
typedef struct
{
    WSEC_BYTE abyBuffer[SDP_PWD_HEAD_LEN];
}SDP_PWD_HEAD;
#pragma pack()

#pragma pack(1)
typedef struct
{
    SDP_CIPHER_HEAD   stCipherHeader;
    SDP_HMAC_ALG_ATTR stHmacAlgAttr;
}SDP_BOD_CIPHER_HEAD;
#pragma pack()

#define SDP_BOD_CIPHER_HEAD_LEN     sizeof(SDP_BOD_CIPHER_HEAD)

/*----------------------------------------------*
 *          һ���ӽ���
 *----------------------------------------------*/
/* �������ĳ��ȼ������ĳ��� */
WSEC_ERR_T SDP_GetCipherDataLen(WSEC_UINT32 ulPlainTextLen, WSEC_UINT32* pulCipherLen);

/* С���ݼӽ��� */
WSEC_ERR_T SDP_Encrypt(WSEC_UINT32 ulDomain,
                       const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                       WSEC_BYTE *pucCipherText, WSEC_UINT32 *pulCTLen);
WSEC_ERR_T SDP_Decrypt(WSEC_UINT32 ulDomain,
                       const WSEC_BYTE *pucCipherText, WSEC_UINT32 ulCTLen,
                       WSEC_BYTE *pucPlainText, WSEC_UINT32 *pulPTLen);

/* �����ݼӽ��� */
WSEC_ERR_T SDP_EncryptInit(WSEC_UINT32 ulDomain, SDP_CRYPT_CTX *pstSdpCtx, SDP_BOD_CIPHER_HEAD *pstBodCipherHeader);
WSEC_ERR_T SDP_EncryptUpdate(const SDP_CRYPT_CTX *pstSdpCtx, 
                             const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                             WSEC_BYTE *pucCipherText, INOUT WSEC_UINT32 *pulCTLen);
WSEC_ERR_T SDP_EncryptFinal(const SDP_CRYPT_CTX *pstSdpCtx, 
                            WSEC_BYTE *pucCipherText, INOUT WSEC_UINT32 *pulCTLen,
                            WSEC_BYTE *pucHmacText, INOUT WSEC_UINT32 *pulHTLen);
WSEC_VOID SDP_EncryptCancel(SDP_CRYPT_CTX *pstSdpCtx);

WSEC_ERR_T SDP_DecryptInit(WSEC_UINT32 ulDomain, SDP_CRYPT_CTX *pstSdpCtx, const SDP_BOD_CIPHER_HEAD* pstBodCipherHeader);
WSEC_ERR_T SDP_DecryptUpdate(const SDP_CRYPT_CTX *pstSdpCtx,
                             const WSEC_BYTE *pucCipherData, WSEC_UINT32 ulCDLen,
                             WSEC_BYTE *pucPlainText, WSEC_UINT32 *pulPTLen);
WSEC_ERR_T SDP_DecryptFinal(const SDP_CRYPT_CTX *pstSdpCtx,
                            const WSEC_BYTE *pucHmacText, WSEC_UINT32 ulHTLen,
                            WSEC_BYTE *pucPlainText, WSEC_UINT32* pulPTLen);
WSEC_VOID SDP_DecrypCancel(SDP_CRYPT_CTX *pstSdpCtx);

/* Encrpt/Decrpt file. */
WSEC_ERR_T SDP_FileEncrypt(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszPlainFile, const WSEC_CHAR *pszCipherFile, WSEC_FP_GetFileDateTime pfGetFileDateTime, const WSEC_PROGRESS_RPT_STRU* pstRptProgress);
WSEC_ERR_T SDP_FileDecrypt(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszCipherFile, const WSEC_CHAR *pszPlainFile, WSEC_FP_SetFileDateTime pfSetFileDateTime, const WSEC_PROGRESS_RPT_STRU* pstRptProgress);

/*----------------------------------------------*
 *          ����HMAC
 *----------------------------------------------*/
WSEC_ERR_T SDP_GetHmacLen(WSEC_UINT32* pulHmacLen);
WSEC_ERR_T SDP_Hmac(WSEC_UINT32 ulDomain,
                    const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                    WSEC_BYTE *pucHmacText, WSEC_UINT32 *pulHTLen);
/* HMAC Verify functions */
WSEC_ERR_T SDP_VerifyHmac(WSEC_UINT32 ulDomain,
                         const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                         const WSEC_BYTE *pucHmacText, WSEC_UINT32 ulHTLen);
    
WSEC_ERR_T SDP_GetHmacAlgAttr(WSEC_UINT32 ulDomain, SDP_HMAC_ALG_ATTR *pstHmacAlgAttr);
WSEC_ERR_T SDP_HmacInit(WSEC_UINT32 ulDomain, const SDP_HMAC_ALG_ATTR *pstHmacAlgAttr, SDP_CRYPT_CTX *pstSdpCtx);
WSEC_ERR_T SDP_HmacUpdate(const SDP_CRYPT_CTX *pstSdpCtx, const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen);
WSEC_ERR_T SDP_HmacFinal(const SDP_CRYPT_CTX *pstSdpCtx, WSEC_BYTE *pucHmacData, WSEC_UINT32 *pulHDLen);
WSEC_VOID SDP_HmacCancel(SDP_CRYPT_CTX *pstSdpCtx);

WSEC_ERR_T SDP_FileHmac(WSEC_UINT32 ulDomain, const WSEC_CHAR* pszFile, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const SDP_HMAC_ALG_ATTR* pstHmacAlgAttr, WSEC_VOID* pvHmacData, WSEC_UINT32* pulHDLen);
WSEC_ERR_T SDP_VerifyFileHmac(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszFile, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const SDP_HMAC_ALG_ATTR* pstHmacAlgAttr, const WSEC_VOID *pvHmacData, WSEC_UINT32 ulHDLen);

/*----------------------------------------------*
 *          ���������
 *----------------------------------------------*/
WSEC_SIZE_T SDP_GetPwdCipherLen(WSEC_SIZE_T ulPwdLen);
WSEC_ERR_T SDP_ProtectPwd(const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                          WSEC_BYTE *pucCipherText, WSEC_UINT32 ulCTLen);
WSEC_ERR_T SDP_VerifyPwd(const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                         const WSEC_BYTE *pucCipherText, WSEC_UINT32 ulCLen);

#ifdef __cplusplus
#if __cplusplus
}
#endif
#endif /* __cplusplus */


#endif /* __SDP_ITF_H_D13DA0FG2_DCRFKLAPSD32SF_4EHLPOC27__ */

