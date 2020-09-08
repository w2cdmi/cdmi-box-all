/* ����pc lint�澯�ɺ��� */
/*lint -e413 -e506 -e522 -e526 -e533 -e534 -e545 -e550 -e573 -e602 -e603 -e628 -e632 -e633 -e638 -e639 -e634 -e641 -e655 -e665 -e668 -e701 -e702 -e750 -e775 -e785 -e794 -e830 -e960 */

#include "stdlib.h"
#include "wsec_securec.h"
#include "kmc_itf.h"
#include "kmc_pri.h"
#include "sdp_itf.h"
#include "sdp_pri.h"

#ifdef __cplusplus
extern "C"{
#endif /* __cplusplus */

#ifndef WSEC_COMPILE_SDP
#error Please defined 'WSEC_COMPILE_SDP' to compile this-file.
#endif

/***** ���Ľṹ�汾 */
#define SDP_CIPHER_TEXT_VER (1)
#define SDP_HMAC_VER        (1)
#define SDP_PWD_CIPHER_VER  (1)
#define SDP_CIPHER_FILE_VER (1) /* �����ļ��汾��, ����ʽ�仯����Ҫ�����ҽ���ʱ��Ҫ�����ݴ��� */

#define SDP_MAX_ERR_FIELD_OF_CIPHER (1)
#define SDP_HMAC_FLAG_MAX           (1)

#define init_error_ctx(stErrCtx) do{ stErrCtx.ulErrCount = 0; stErrCtx.ulLastErrCode = WSEC_SUCCESS; }do_end
#define update_error_ctx(stErrCtx, condition, oper, err_code) do{if(condition) {stErrCtx.ulErrCount++; stErrCtx.ulLastErrCode = err_code; oper;}}do_end

WSEC_BYTE g_CipherFileFlag[] = {0x7F, 0x11, 0x3A, 0xBE, 0x84, 0x18, 0x20, 0x0D, 0xE0, 0x45, 0x2F, 0x61, 0x3D, 0x34, 0xD5, 0x16,
                                0x31, 0x99, 0x77, 0x74, 0x6F, 0xEE, 0x6D, 0x76, 0xC8, 0x92, 0xC4, 0x73, 0xF0, 0x17, 0x96, 0x87};

extern WSEC_ERR_T KMC_GetMkByType(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType, WSEC_BYTE* pKeyBuf, WSEC_UINT32 *pKeyLen, WSEC_UINT32 *pKeyId);
extern WSEC_ERR_T KMC_GetMkDetailInner(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen);

WSEC_VOID SDP_CalcAllAlgPara(WSEC_UINT32 ulAlgId, const WSEC_CHAR* pszAlgName, WSEC_VOID* pReserved);

/*****************************************************************************
 �� �� ��  : SDP_CalcAllAlgPara
 ��������  : ����CAC��֧�ֵ��㷨��, ���SDP�����Ƿ�������ܡ�HMAC�Բ�����Ҫ��.
 �������  : ulAlgId��pszAlgName: �㷨ID������
 �������  : pReserved: ������ʧ�������WSEC_FALSE��������������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��18��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
  2.��    ��   : 2015��7��31��
    ��    ��   : z00118096
    �޸�����   : �����ԳƷ���������鳤�Ⱥꡢ���HMACֵ���Ⱥ궨����Ƿ��С.
*****************************************************************************/
WSEC_VOID SDP_CalcAllAlgPara(WSEC_UINT32 ulAlgId, const WSEC_CHAR* pszAlgName, WSEC_VOID* pReserved)
{
    WSEC_ALGTYPE_E eType;
    WSEC_UINT32 ulLen;
    WSEC_BOOL* pbOk = (WSEC_BOOL*)pReserved;

    eType = CAC_AlgId2Type(ulAlgId);

    do
    {
        if (WSEC_ALGTYPE_SYM == eType) /* �����㷨 */
        {
            /* ��� SDP_KEY_MAX_LEN */
            ulLen = CAC_SYM_KeyLen(ulAlgId);
            break_oper_if(SDP_KEY_MAX_LEN < ulLen, WSEC_LOG_E2("'SDP_KEY_MAX_LEN' defined too small, it should not small than %d when used '%s' algorithmic.", ulLen, pszAlgName), *pbOk = WSEC_FALSE);

            /* ��� SDP_IV_MAX_LEN */
            ulLen = CAC_SYM_IvLen(ulAlgId);
            break_oper_if(SDP_IV_MAX_LEN < ulLen, WSEC_LOG_E2("'SDP_IV_MAX_LEN' defined too small, it should not small than %d when used '%s' algorithmic.", ulLen, pszAlgName), *pbOk = WSEC_FALSE);

            ulLen = CAC_SYM_BlockSize(ulAlgId);
            break_oper_if(0 == ulLen, WSEC_LOG_E1("'%s' cannot support BlockSize.", pszAlgName), *pbOk = WSEC_FALSE);
            break_oper_if(ulLen > SDP_SYM_MAX_BLOCK_SIZE, WSEC_LOG_E1("'SDP_SYM_MAX_BLOCK_SIZE' defined too small, at least is %d.", ulLen), *pbOk = WSEC_FALSE);
        }
        else if (WSEC_ALGTYPE_HMAC == eType) /* HMAC�㷨 */
        {
            ulLen = CAC_HMAC_Size(ulAlgId);
            break_oper_if(SDP_PTMAC_MAX_LEN < ulLen, WSEC_LOG_E2("'SDP_PTMAC_MAX_LEN' defined too small, it should not small than %d when used '%s' algorithmic.", ulLen, pszAlgName), *pbOk = WSEC_FALSE);
            break_oper_if(SDP_HMAC_MAX_SIZE < ulLen, WSEC_LOG_E2("'SDP_HMAC_MAX_SIZE' defined too small, it should not small than %d when used '%s' algorithmic.", ulLen, pszAlgName), *pbOk = WSEC_FALSE);
        }else{;}
    }do_end;

    return;
}

/*****************************************************************************
 �� �� ��  : SDP_Initialize
 ��������  : SDP����ǰ, ��鳣�������Ƿ�Ϲ�, ��Щ������:
             SDP_KEY_MAX_LEN
             SDP_IV_MAX_LEN
             SDP_PTMAC_MAX_LEN
 �������  :
 �������  :
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_Initialize()
{
#ifdef WSEC_DEBUG
    WSEC_BOOL bOk = WSEC_TRUE;
#endif
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

#ifdef WSEC_DEBUG
    nErrCode = CAC_GetAlgList(SDP_CalcAllAlgPara, &bOk);
    if ((WSEC_SUCCESS == nErrCode) && (!bOk)) {nErrCode = WSEC_FAILURE;}
#endif

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : SDP_GetAlgProperty
 ��������  : ��ѯ�����㷨���䣬��������IPSI��OpenSSL
 �������  :
 �������  :
 �� �� ֵ  : ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��1��
    ��    ��   : j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_GetAlgProperty(
    WSEC_UINT32 ulAlgID,
    WSEC_CHAR *pcAlgName,
    WSEC_UINT32 ulAlgNameLen,
    WSEC_ALGTYPE_E *peAlgType,
    WSEC_UINT32 *pulKeyLen,
    WSEC_UINT32 *pulIVLen,
    WSEC_UINT32 *pulBlockLen,
    WSEC_UINT32 *pulMACLen
    )
{
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_UINT32 ulTempKeyLen   = 0;
    WSEC_UINT32 ulTempIVLen    = 0;
    WSEC_UINT32 ulTempBlockLen = 0;
    WSEC_UINT32 ulTempMACLen   = 0;
    const WSEC_CHAR* pszTempAlgName = WSEC_NULL_PTR;

    eAlgType = CAC_AlgId2Type(ulAlgID);
    return_oper_if((WSEC_ALGTYPE_UNKNOWN == eAlgType), WSEC_LOG_E("[SDP] CAC Get algorithm types failed."), WSEC_ERR_SDP_ALG_NOT_SUPPORTED);

    if (pcAlgName && (ulAlgNameLen > 0))
    {
        /* Misinformation: FORTIFY.Dead_Code */
        pszTempAlgName = CAC_AlgId2Name(ulAlgID);
        return_oper_if(!pszTempAlgName, WSEC_LOG_E("[SDP] CAC Get algorithm names failed."), WSEC_ERR_GET_ALG_NAME_FAIL);
        return_oper_if(strcpy_s(pcAlgName, ulAlgNameLen, pszTempAlgName) != EOK, WSEC_LOG_E("strcpy_s() fail"), WSEC_ERR_STRCPY_FAIL);
    }

    if (WSEC_NULL_PTR != pulKeyLen)
    {
        ulTempKeyLen = (WSEC_ALGTYPE_SYM == eAlgType) ? CAC_SYM_KeyLen(ulAlgID) : SDP_KEY_MAX_LEN;
        if (0 == ulTempKeyLen) {ulTempKeyLen = SDP_KEY_MAX_LEN;}
        return_oper_if((ulTempKeyLen > SDP_KEY_MAX_LEN), 
                       WSEC_LOG_E2("[SDP] Length of key exceeds the limit %d. Actually %d.", SDP_KEY_MAX_LEN, ulTempKeyLen), 
                       WSEC_ERR_INVALID_ARG);
    }

    if (WSEC_NULL_PTR != pulIVLen)
    {
        ulTempIVLen = CAC_SYM_IvLen(ulAlgID);
        return_oper_if((ulTempIVLen > SDP_IV_MAX_LEN), WSEC_LOG_E2("[SDP] Length of IV exceeds the limit %d. Actually %d.", SDP_IV_MAX_LEN, ulTempIVLen), WSEC_ERR_INVALID_ARG);
    }

    if (WSEC_NULL_PTR != pulBlockLen)
    {
        ulTempBlockLen = CAC_SYM_BlockSize(ulAlgID);
    }

    if (WSEC_NULL_PTR != pulMACLen)
    {
        ulTempMACLen = CAC_HMAC_Size(ulAlgID);
        return_oper_if(ulTempMACLen < 1, WSEC_LOG_E1("The alg(%d) connot support HMAC.", ulAlgID), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);
        return_oper_if((ulTempMACLen > SDP_PTMAC_MAX_LEN), 
                       WSEC_LOG_E2("[SDP] Length of MAC exceeds the limit %d. Actually %d.", SDP_PTMAC_MAX_LEN, ulTempMACLen), 
                       WSEC_ERR_INVALID_ARG);
    }

    /* output param */
    WSEC_SAFE_ASSIGN(peAlgType, eAlgType);
    WSEC_SAFE_ASSIGN(pulKeyLen, ulTempKeyLen);
    WSEC_SAFE_ASSIGN(pulIVLen, ulTempIVLen);
    WSEC_SAFE_ASSIGN(pulBlockLen, ulTempBlockLen);
    WSEC_SAFE_ASSIGN(pulMACLen, ulTempMACLen);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_CvtByteOrder4CipherFileHdr
 ��������  : �������ļ�ͷ���ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pTlv: [in]�ֽ����ת������; [out]ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_CvtByteOrder4CipherFileHdr(SDP_CIPHER_FILE_HDR_STRU* pstFileHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    if (wbcHost2Network == eOper)
    {
        pstFileHdr->ulVer               = WSEC_H2N_L(pstFileHdr->ulVer);
        pstFileHdr->ulPlainBlockLenMax  = WSEC_H2N_L(pstFileHdr->ulPlainBlockLenMax);
        pstFileHdr->ulCipherBlockLenMax = WSEC_H2N_L(pstFileHdr->ulCipherBlockLenMax);
    }
    else if (wbcNetwork2Host == eOper)
    {
        pstFileHdr->ulVer               = WSEC_N2H_L(pstFileHdr->ulVer);
        pstFileHdr->ulPlainBlockLenMax  = WSEC_N2H_L(pstFileHdr->ulPlainBlockLenMax);
        pstFileHdr->ulCipherBlockLenMax = WSEC_N2H_L(pstFileHdr->ulCipherBlockLenMax);
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    WSEC_CvtByteOrder4DateTime(&pstFileHdr->tCreateFileTimeUtc, eOper);
    WSEC_CvtByteOrder4DateTime(&pstFileHdr->tSrcFileCreateTime, eOper);
    WSEC_CvtByteOrder4DateTime(&pstFileHdr->tSrcFileEditTime, eOper);

    return;
}

/*****************************************************************************
 �� �� ��  : SDP_CvtByteOrder4CipherTextHeader
 ��������  : ������ͷ���ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstHdr: [in]�ֽ����ת������; [out]ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_CvtByteOrder4CipherTextHeader(SDP_CIPHER_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(pstHdr);

    if (wbcHost2Network == eOper)
    {
        pstHdr->ulVersion   = WSEC_H2N_L(pstHdr->ulVersion);
        pstHdr->bHmacFlag   = WSEC_H2N_L((WSEC_UINT32)pstHdr->bHmacFlag);
        pstHdr->ulDomain    = WSEC_H2N_L(pstHdr->ulDomain);
        pstHdr->ulAlgId     = WSEC_H2N_L(pstHdr->ulAlgId);
        pstHdr->ulKeyId     = WSEC_H2N_L(pstHdr->ulKeyId);
        pstHdr->ulIterCount = WSEC_H2N_L(pstHdr->ulIterCount);
        pstHdr->ulCDLen     = WSEC_H2N_L(pstHdr->ulCDLen);
    }
    else if (wbcNetwork2Host == eOper)
    {
        pstHdr->ulVersion   = WSEC_N2H_L(pstHdr->ulVersion);
        pstHdr->bHmacFlag   = WSEC_N2H_L((WSEC_UINT32)pstHdr->bHmacFlag);
        pstHdr->ulDomain    = WSEC_N2H_L(pstHdr->ulDomain);
        pstHdr->ulAlgId     = WSEC_N2H_L(pstHdr->ulAlgId);
        pstHdr->ulKeyId     = WSEC_N2H_L(pstHdr->ulKeyId);
        pstHdr->ulIterCount = WSEC_N2H_L(pstHdr->ulIterCount);
        pstHdr->ulCDLen     = WSEC_N2H_L(pstHdr->ulCDLen);
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : SDP_CvtByteOrder4HmacTextHeader
 ��������  : ��HMACͷ���ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstHdr: [in]�ֽ����ת������; [out]ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_CvtByteOrder4HmacTextHeader(SDP_HMAC_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(pstHdr);

    if (wbcHost2Network == eOper)
    {
        pstHdr->ulVersion   = WSEC_H2N_L(pstHdr->ulVersion);
        pstHdr->ulDomain    = WSEC_H2N_L(pstHdr->ulDomain);
        pstHdr->ulAlgId     = WSEC_H2N_L(pstHdr->ulAlgId);
        pstHdr->ulKeyId     = WSEC_H2N_L(pstHdr->ulKeyId);
        pstHdr->ulIterCount = WSEC_H2N_L(pstHdr->ulIterCount);
    }
    else if (wbcNetwork2Host == eOper)
    {
        pstHdr->ulVersion   = WSEC_N2H_L(pstHdr->ulVersion);
        pstHdr->ulDomain    = WSEC_N2H_L(pstHdr->ulDomain);
        pstHdr->ulAlgId     = WSEC_N2H_L(pstHdr->ulAlgId);
        pstHdr->ulKeyId     = WSEC_N2H_L(pstHdr->ulKeyId);
        pstHdr->ulIterCount = WSEC_N2H_L(pstHdr->ulIterCount);
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : SDP_CvtByteOrder4PwdCipherTextHeader
 ��������  : �������ͷ���ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstHdr: [in]�ֽ����ת������; [out]ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_CvtByteOrder4PwdCipherTextHeader(SDP_PWD_HEAD_STRU* pstHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(pstHdr);

    if (wbcHost2Network == eOper)
    {
        pstHdr->ulVersion   = WSEC_H2N_L(pstHdr->ulVersion);
        pstHdr->ulAlgId     = WSEC_H2N_L(pstHdr->ulAlgId);
        pstHdr->ulIterCount = WSEC_H2N_L(pstHdr->ulIterCount);
        pstHdr->ulCDLen     = WSEC_H2N_L(pstHdr->ulCDLen);
    }
    else if (wbcNetwork2Host == eOper)
    {
        pstHdr->ulVersion   = WSEC_N2H_L(pstHdr->ulVersion);
        pstHdr->ulAlgId     = WSEC_N2H_L(pstHdr->ulAlgId);
        pstHdr->ulIterCount = WSEC_N2H_L(pstHdr->ulIterCount);
        pstHdr->ulCDLen     = WSEC_N2H_L(pstHdr->ulCDLen);
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    return;
}


/*****************************************************************************
 �� �� ��  : SDP_FillCipherTextHeader
 ��������  : ���Ĭ�ϼ���ͷ��Ϣ�ͼ�����Կ��Ϣ
 �������  : KMC_SDP_ALG_TYPE_ENUM eIntfType      �ӿ�����
             WSEC_UINT32 ulDomain                 ������
 �������  : SDP_CIPHER_HEAD_STRU *pstCipherHead  ����ͷ���ṹ
             WSEC_BYTE *pucKey                    ������Կ
             WSEC_UINT32 *pulKeyLen               ������Կ����
             WSEC_UINT32 *pulIVLen                ������IV�ĳ���
 �� �� ֵ  : WSEC_UINT32   ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��2��
    ��    ��   : x00102361, j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_FillCipherTextHeader(
    KMC_SDP_ALG_TYPE_ENUM eIntfType,
    WSEC_UINT32 ulDomain,
    SDP_CIPHER_HEAD_STRU *pstCipherHead,
    WSEC_BYTE *pucKey,
    WSEC_UINT32 *pulKeyLen,
    WSEC_UINT32 *pulIVLen
    )
{
    KMC_CFG_DATA_PROTECT_STRU stDP;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_ERR_T  ulRet       = WSEC_SUCCESS;
    WSEC_UINT32 ulIVLen     = 0;
    WSEC_UINT32 ulKeyLen    = 0;
    WSEC_UINT32 ulKeyId     = 0;
    WSEC_UINT32 ulIterCount = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("SDP_FillCipherTextHeader", pstCipherHead && pulKeyLen && pucKey && pulIVLen);

    /* Get interface */
    nErrCode = KMC_GetDataProtectCfg(eIntfType, &stDP);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_GetDataProtectCfg() = %u.", nErrCode), nErrCode);
    return_oper_if((0 == (stDP.usKeyType & KMC_KEY_TYPE_ENCRPT)), WSEC_LOG_E("[SDP] Key type is incompatible with application. EK expected."), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);

    /* ����㷨ID�Ƿ�Ϸ� */
    if (SDP_ALG_ENCRPT == eIntfType) {return_oper_if(CAC_AlgId2Type(stDP.ulAlgId) != WSEC_ALGTYPE_SYM, WSEC_LOG_E("KMC's Data-Protection CFG error."), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);}
    if (SDP_ALG_INTEGRITY == eIntfType) {return_oper_if(CAC_AlgId2Type(stDP.ulAlgId) != WSEC_ALGTYPE_HMAC, WSEC_LOG_E("KMC's Data-Protection CFG error."), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);}
    if (SDP_ALG_PWD_PROTECT == eIntfType) {return_oper_if(CAC_AlgId2Type(stDP.ulAlgId) != WSEC_ALGTYPE_PBKDF, WSEC_LOG_E("KMC's Data-Protection CFG error."), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);}

    /* Get key length, IV length */
    ulRet = SDP_GetAlgProperty(stDP.ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, &ulKeyLen, &ulIVLen, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_SYM != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);

    /* Get WK (work key) */
    ulRet = SDP_GetWorkKey(ulDomain, stDP.usKeyType, &ulKeyId, &ulIterCount,
                           pstCipherHead->aucSalt, SDP_SALT_LEN,
                           pstCipherHead->aucIV, ulIVLen, pucKey, ulKeyLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get WK failed."), ulRet);

    /* fill header except pstCipherHead->ulCDLen */
    pstCipherHead->ulVersion    = SDP_CIPHER_TEXT_VER;
    pstCipherHead->bHmacFlag    = stDP.bAppendMac;
    pstCipherHead->ulDomain     = ulDomain;
    pstCipherHead->ulAlgId      = stDP.ulAlgId;
    pstCipherHead->ulIterCount  = ulIterCount;
    pstCipherHead->ulKeyId      = ulKeyId;
    
    *pulKeyLen = ulKeyLen;
    *pulIVLen  = ulIVLen;

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : SDP_FillHmacTextHeader
 ��������  : ���Ĭ�ϼ���HMAC��Ϣͷ����Կ
 �������  : KMC_SDP_ALG_TYPE_ENUM eIntfType    �ӿ�����
             WSEC_UINT32 ulDomain               ������
 �������  : SDP_HMAC_HEAD_STRU *pstHmacHead    ������HMAC��ͷ����ַ
             WSEC_BYTE *pucKey                  �����ɵĹ�����Կ[�ɿ�]
             WSEC_UINT32 *pulKeyLen             �����ɵĹ�����Կ����[�ɿ�]
 �� �� ֵ  : WSEC_UINT32 ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��2��
    ��    ��   : x00102361, j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_FillHmacTextHeader(
    KMC_SDP_ALG_TYPE_ENUM eIntfType,
    WSEC_UINT32 ulDomain,
    SDP_HMAC_HEAD_STRU *pstHmacHead,
    WSEC_BYTE *pucKey,
    WSEC_UINT32 *pulKeyLen
    )
{
    KMC_CFG_DATA_PROTECT_STRU stDP;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_ERR_T  ulRet       = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen    = 0;
    WSEC_UINT32 ulKeyId     = 0;
    WSEC_UINT32 ulIterCount = 0;
    WSEC_ERR_T ulErrCode = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("SDP_FillHmacTextHeader", pstHmacHead && pulKeyLen && pucKey);

    /* Get interface */
    ulErrCode = KMC_GetDataProtectCfg(eIntfType, &stDP);
    return_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_GetDataProtectCfg()=%u", ulErrCode), ulErrCode);
    return_oper_if((0 == (stDP.usKeyType & KMC_KEY_TYPE_INTEGRITY)), WSEC_LOG_E("[SDP] Key type is incompatible with application. IK expected."), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);

    /* Get key length */
    ulRet = SDP_GetAlgProperty(stDP.ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, &ulKeyLen, WSEC_NULL_PTR, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_HMAC != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);

    /* Get WK (work key) */
    ulRet = SDP_GetWorkKey(ulDomain, stDP.usKeyType, &ulKeyId, &ulIterCount,
                           pstHmacHead->aucSalt, SDP_SALT_LEN, WSEC_NULL_PTR, 0, pucKey, ulKeyLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get WK failed."), ulRet);

    /* fill header */
    pstHmacHead->ulVersion      = SDP_HMAC_VER;
    pstHmacHead->ulDomain       = ulDomain;
    pstHmacHead->ulAlgId        = stDP.ulAlgId;
    pstHmacHead->ulIterCount    = ulIterCount;
    pstHmacHead->ulKeyId        = ulKeyId;

    /* output param */
    *pulKeyLen                  = ulKeyLen;

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_FillPwdCipherTextHeader
 ��������  : ���Ĭ�Ͽ�������ͷ��Ϣ�ͼ�����Կ��Ϣ
 �������  : KMC_INTERFACE_TYPE_E eIntfType       �ӿ�����
             WSEC_UINT32 ulDomain                 ������
 �������  : SDP_PWD_HEAD_STRU *pstPwdHead        ��������ͷ���ṹ
 �� �� ֵ  : WSEC_UINT32   ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��2��
    ��    ��   : x00102361, j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_FillPwdCipherTextHeader(
    KMC_SDP_ALG_TYPE_ENUM eIntfType,
    SDP_PWD_HEAD_STRU *pstPwdHead
    )
{
    KMC_CFG_DATA_PROTECT_STRU stDP;
    WSEC_ERR_T ulRet  = WSEC_SUCCESS;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_ERR_T ulErrCode = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("SDP_FillPwdCipherTextHeader", pstPwdHead);

    /* Get interface */
    ulErrCode = KMC_GetDataProtectCfg(eIntfType, &stDP);
    return_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_GetDataProtectCfg()=%u", ulErrCode), ulErrCode);
    return_oper_if(!KMC_IS_KEYITERATIONS_VALID(stDP.ulKeyIterations), WSEC_LOG_E1("KeyIterations(%u) invalid.", stDP.ulKeyIterations), WSEC_ERR_KMC_KMCCFG_INVALID);

    /* Get Algorithm Property */
    ulRet = SDP_GetAlgProperty(stDP.ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, WSEC_NULL_PTR, WSEC_NULL_PTR, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_PBKDF != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE);
    
    /* generate salt */
    ulRet = CAC_Random(pstPwdHead->aucSalt, SDP_SALT_LEN);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC calculate random failed."), WSEC_ERR_GET_RAND_FAIL);

    /* fill header except pstPwdHead->ulCDLen */
    pstPwdHead->ulVersion      = SDP_PWD_CIPHER_VER;
    pstPwdHead->ulAlgId        = stDP.ulAlgId;
    pstPwdHead->ulIterCount    = stDP.ulKeyIterations;

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_GetWorkKey
 ��������  : ��ȡWorkKey
 �������  :
 �������  :
 �� �� ֵ  : ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��1��
    ��    ��   : j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_GetWorkKey(
    WSEC_UINT32 ulDomain,
    WSEC_UINT16 usKeyType,
    WSEC_UINT32 *pulKeyId,
    WSEC_UINT32 *pulIterCount,
    WSEC_BYTE *pucSalt,
    WSEC_UINT32 ulSaltLen,
    WSEC_BYTE *pucIV,
    WSEC_UINT32 ulIVLen,
    WSEC_BYTE *pucKey,
    WSEC_UINT32 ulKeyLen
    )
{
    WSEC_BYTE szMasterKey[WSEC_MK_LEN_MAX];
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulMkLen         = 0;
    WSEC_UINT32 ulTempKeyId     = 0;
    WSEC_UINT32 ulTempIterCount = 1;

    /* check input */
    return_err_if_para_invalid("SDP_GetWorkKey", pucSalt && pucKey && pulKeyId && (ulSaltLen > 0) && (ulKeyLen > 0));

    /* generate salt */
    ulRet = CAC_Random(pucSalt, ulSaltLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC calculate random failed."), WSEC_ERR_GET_RAND_FAIL);

    /* get Master Key */
    ulMkLen = sizeof(szMasterKey);
    ulRet = KMC_GetMkByType(ulDomain, usKeyType, szMasterKey, &ulMkLen, &ulTempKeyId);
    if ((WSEC_SUCCESS != ulRet) && (KMC_KEY_TYPE_ENCRPT_INTEGRITY != usKeyType))
    {
        ulRet = KMC_GetMkByType(ulDomain, KMC_KEY_TYPE_ENCRPT_INTEGRITY, szMasterKey, &ulMkLen, &ulTempKeyId);
    }
    return_oper_if((WSEC_SUCCESS != ulRet), WSEC_LOG_E("[SDP] KMC get MK by Key-Type failed."), ulRet);

    /* optionally generate IV */
    if ((WSEC_NULL_PTR != pucIV) && (ulIVLen > 0))
    {
        ulRet = CAC_Random(pucIV, ulIVLen);
        return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC calculate random failed."), WSEC_ERR_GET_RAND_FAIL);
    }

    /* derive work key */
    ulRet = CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, szMasterKey, ulMkLen,
                       pucSalt, ulSaltLen, ulTempIterCount, ulKeyLen, pucKey);
    return_oper_if((WSEC_SUCCESS != ulRet), WSEC_LOG_E("[SDP] CAC pbkdf2 derive WK failed."), WSEC_ERR_PBKDF2_FAIL);

    /* output param */
    *pulKeyId = ulTempKeyId;
    WSEC_SAFE_ASSIGN(pulIterCount, ulTempIterCount);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_GetWorkKeyByID
 ��������  : ��ȡWorkKey
 �������  :
 �������  :
 �� �� ֵ  : ������

 �޸���ʷ      :
  1.��    ��   : 2014��7��1��
    ��    ��   : j00265291
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_GetWorkKeyByID(
    WSEC_UINT32 ulDomain,
    WSEC_UINT32 ulKeyId,
    WSEC_UINT32 ulIterCount,
    const WSEC_BYTE *pucSalt,
    WSEC_UINT32 ulSaltLen,
    WSEC_BYTE *pucKey,
    WSEC_UINT32 ulKeyLen
    )
{
    WSEC_BYTE szMasterKey[WSEC_MK_LEN_MAX];
    WSEC_ERR_T ulRet    = WSEC_SUCCESS;
    WSEC_UINT32 ulMkLen = 0;

    /* check input */
    return_err_if_para_invalid("SDP_GetWorkKeyByID", pucSalt && pucKey && (ulSaltLen > 0) && (ulKeyLen > 0) && (ulIterCount > 0));

    /* get Master Key */
    ulMkLen = sizeof(szMasterKey);
    ulRet = KMC_GetMkDetailInner(ulDomain, ulKeyId, WSEC_NULL_PTR, szMasterKey, &ulMkLen);
    return_oper_if((WSEC_SUCCESS != ulRet), WSEC_LOG_E("[SDP] KMC get MK by Key-Id failed."), ulRet);

    /* derive work key */
    ulRet = CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, szMasterKey, ulMkLen,
                       pucSalt, ulSaltLen, ulIterCount, ulKeyLen, pucKey);
    return_oper_if((WSEC_SUCCESS != ulRet), WSEC_LOG_E("[SDP] CAC pbkdf2 derive WK failed."), WSEC_ERR_PBKDF2_FAIL);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_FreeCtx
 ��������  : �ͷŷֶΰ�ȫ�㷨(�ӽ��ܡ�HMAC)������ʹ�õ�������.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pstSdpCtx: [in]ָ��������Ϣ��ָ��, [out]ָ������ΪNULL
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��5��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_FreeCtx(SDP_CRYPT_CTX *pstSdpCtx)
{
    SDP_CRYPT_CTX_STRU *pstSdpCtxObj = WSEC_NULL_PTR;

    if (!pstSdpCtx) {return;}

    pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);
    if (!pstSdpCtxObj) {return;}

    do
    {
        break_oper_if(!pstSdpCtxObj->stWsecCtx, oper_null, oper_null);
        break_oper_if(WSEC_ALGTYPE_SYM == pstSdpCtxObj->eAlgType, CAC_CipherFree(&pstSdpCtxObj->stWsecCtx), oper_null);
        break_oper_if(WSEC_ALGTYPE_HMAC == pstSdpCtxObj->eAlgType, CAC_HmacFree(&pstSdpCtxObj->stWsecCtx), oper_null);
        break_oper_if(WSEC_ALGTYPE_DIGEST == pstSdpCtxObj->eAlgType, CAC_DigestFree(&pstSdpCtxObj->stWsecCtx), oper_null);
        WSEC_ASSERT(0);
    }do_end;

    WSEC_FREE(pstSdpCtxObj);
    *pstSdpCtx = WSEC_NULL_PTR;
}

/*****************************************************************************
 �� �� ��  : SDP_GetCipherDataLen
 ��������  : Ԥ����ռ�ʱʹ�ã��������ĵĳ��Ȼ�ȡ�������ݿ�( cipher-data )�ĳ���
 �� �� ��  : ulPlainTextLen ��ʾ�����ܵ����ĳ��ȡ�
 �� �� ��  : pulCipherLen: ��Ӧ�����ĳ���, 
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : �ú���������SDP_Encrypt��SDP_EncryptInit��SDP_EncryptUpdate��SDP_EncryptFinal�ȼ��ܺ�������ǰ�������ĳ��ȼ���������ڴ���䡣
             ͬ��������AES������SDP_DecryptInit��SDP_DecryptUpdate��SDP_DecryptFinal�Ƚ��ܺ�������ǰ���������ڴ���䣬
             ȷ�����پ���һ�����ܿ�ĳ��ȡ�

 �޸���ʷ
  1.��    ��   : 2014��7��2��
    ��    ��   : x00102361, j00265291
    �޸�����   : �����ɺ���
  2.��    ��   : 2014��11��20��
    ��    ��   : z00118096
    �޸�����   : �ӿڱ��, �ɳ��δ�������, ����ֵ��������Ϣ.
  3.��    ��   : 2015��07��31��
    ��    ��   : z00118096
    �޸�����   : �ϰ汾�ĸýӿ��޷������������: 
                 1) �߳�P1���ô˺�����ȡ���ĳ���;
                 2) �߳�P2����KMC_SetDataProtectCfg�������㷨����;
                 3) P1����SDP_Encrypt����, ���2)���㷨���ĵ���ʵ����Ҫ��
                    ���ĳ��Ƚ�1)����, ������ڴ�Խ��д����, �������޷���֪.

                 �޸�����: ʹ�����ļ��ܿ鳤���������ĳ���, ���㷨�޹�.
*****************************************************************************/
WSEC_ERR_T SDP_GetCipherDataLen(WSEC_UINT32 ulPlainTextLen, WSEC_UINT32* pulCipherLen)
{
    const WSEC_UINT32 ulBlkLen  = SDP_SYM_MAX_BLOCK_SIZE;
    WSEC_UINT32 ulHmacLen = 0;
    WSEC_ERR_T  ulRet = WSEC_SUCCESS;

    return_err_if_para_invalid("SDP_GetCipherDataLen", pulCipherLen);

    ulRet = SDP_GetHmacLen(&ulHmacLen);
    return_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E1("SDP_GetHmacLen()=%u", ulRet), ulRet);
    
    *pulCipherLen = SDP_CIPHER_HEAD_LEN + (((ulPlainTextLen / ulBlkLen) + 1) * ulBlkLen) + ulHmacLen;

    return WSEC_SUCCESS;
}

/*****************************************************************************
> �� �� ��  : SDP_Encrypt
> ��������  : �������ݼ��ܽӿڣ������ڻ������ݶԳƼ��ܵĳ�����
> ���Ľṹ  : cipher-head | cipher-data
>             cipher-head | cipher-data | hmac-head | hmac-data
> �������  : ulDomain ��ʾ������ID
>             pucPlainText ��ʾ��Ҫ���ܵ������ڴ��
>             ulPTLen ��ʾ��Ҫ���ܵ������ڴ��ĳ���
>             pulCTLen ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ��ķ��䳤��
> �������  : pucCipherText ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ�飬��������ͷ��
>             pulCTLen ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ���ʵ�ʳ��ȣ���������ͷ������
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ������Ѿ������
> ��������  : ��
> ��������  : ������WSEC_SUCCESSʱ��*pulCTLen > 0
> ע������  : pucCipherTextָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��pulCTLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_Encrypt(WSEC_UINT32 ulDomain,
                       const WSEC_BYTE *pucPlainText, WSEC_UINT32 ulPTLen,
                       WSEC_BYTE *pucCipherText, WSEC_UINT32 *pulCTLen)
{
    SDP_CIPHER_HEAD_STRU *pstCipherHead = WSEC_NULL_PTR;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN];
    WSEC_ERR_T ulRet            = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen        = 0;
    WSEC_UINT32 ulIVLen         = 0;
    WSEC_UINT32 ulMACLen        = 0;
    WSEC_UINT32 ulTempCTLen     = 0;
    WSEC_UINT32 ulCTMaxLen      = 0;


    /* check input */
    return_err_if_para_invalid("SDP_Encrypt", pucPlainText && pulCTLen && pucCipherText && (*pulCTLen >= ulPTLen) && (pucPlainText != pucCipherText));

    ulCTMaxLen = *pulCTLen;
    ulTempCTLen = ulCTMaxLen;

    /* construct header, get MK-derived work key */
    return_oper_if((ulCTMaxLen <= SDP_CIPHER_HEAD_LEN), WSEC_LOG_E("[SDP] Buffer for cipher text is not enough."), WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);
    WSEC_MEMSET(pucCipherText, ulCTMaxLen, 0, SDP_CIPHER_HEAD_LEN);
    pstCipherHead = (SDP_CIPHER_HEAD_STRU *)pucCipherText;
    ulRet = SDP_FillCipherTextHeader(SDP_ALG_ENCRPT, ulDomain, pstCipherHead, aucKey, &ulKeyLen, &ulIVLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Fill cipher text header failed."), ulRet);
    ulCTMaxLen -= SDP_CIPHER_HEAD_LEN;

    /* data encryption */
    ulRet = CAC_Encrypt(pstCipherHead->ulAlgId, aucKey, ulKeyLen, pstCipherHead->aucIV, ulIVLen,
                        pucPlainText, ulPTLen, (pucCipherText + SDP_CIPHER_HEAD_LEN),
                        /* suggest INOUT */ &ulTempCTLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC encrypt failed."), WSEC_ERR_ENCRPT_FAIL);
    WSEC_ASSERT(ulTempCTLen <= ulCTMaxLen); /* assert: cipher space not overflow */

    /* destroy key */
    WSEC_DESTROY_KEY(aucKey, ulKeyLen);
    
    ulCTMaxLen -= ulTempCTLen;

    /* update header with real cipher length */
    pstCipherHead->ulCDLen = ulTempCTLen;

    /* compute cipher text length */
    ulTempCTLen += SDP_CIPHER_HEAD_LEN;

    /* compute data HMAC */
    if (pstCipherHead->bHmacFlag)
    {
        ulMACLen = ulCTMaxLen;
        ulRet = SDP_Hmac(ulDomain, pucPlainText, ulPTLen, pucCipherText + ulTempCTLen, &ulMACLen);
        return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Compute hmac failed."), ulRet);

        ulTempCTLen += ulMACLen;
    }

    /* byte order process */
    SDP_CvtByteOrder4CipherTextHeader(pstCipherHead, wbcHost2Network);

    /* output param */
    *pulCTLen = ulTempCTLen;

    return WSEC_SUCCESS;
}

/*****************************************************************************
> �� �� ��  : SDP_Decrypt
> ��������  : �������ݽ��ܽӿڣ��ṩ���ܻ����������ĵĹ��ܡ�ͨ������ͷ����ȡ����������㷨��IV��Salt����ԿID�������ݽ��н��ܣ������ķ��ظ�Ӧ�á�
> ���Ľṹ  : cipher-head | cipher-data
>             cipher-head | cipher-data | hmac-head | hmac-data
> �������  : pucCipherText ��ʾ��Ҫ���ܵ����ģ��������ݱ���������ͷ����׷��HMAC��
>             ulCTLen ��ʾ��Ҫ���ܵ����ĵĳ��ȣ��������ݱ���������ͷ��׷��HMAC�ĳ���
> �������  : pucPlainText ��ʾ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ��
>             pulPTLen ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ��ĳ���
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ������Ѿ������
> ��������  : ��
> ��������  : ������WSEC_SUCCESSʱ��* pulPTLen > 0
> ע������  : pucPlainTextָ����ڴ���ɵ����߷�����ͷţ���AES�����£����ĳ��Ȳ��������ĳ��ȣ�
>             �ڴ��ķ����С����ʹ��ulCTLen��pulPTLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_Decrypt(
      WSEC_UINT32   ulDomain,
      const WSEC_BYTE *pucCipherText,
      WSEC_UINT32 ulCTLen,
      WSEC_BYTE *pucPlainText,
      WSEC_UINT32 *pulPTLen
    )
{
    SDP_CIPHER_HEAD_STRU    *pstCipherHead = WSEC_NULL_PTR;
    SDP_ERROR_CTX_STRU      stErrCtx;
    WSEC_ALGTYPE_E          eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN] = {0};
    WSEC_ERR_T ulRet            = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen        = 0;
    WSEC_UINT32 ulIVLen         = 0;
    WSEC_UINT32 ulTempPTLen     = 0;
    WSEC_UINT32 ulPTMaxLen      = 0;

    /* check input */
    return_err_if_para_invalid("SDP_Decrypt", pucCipherText && pucPlainText && pulPTLen && (ulCTLen > 0) && (pucPlainText != pucCipherText));

    ulPTMaxLen = *pulPTLen;
    ulTempPTLen = ulPTMaxLen;

    /* parse header */
    return_oper_if((ulCTLen < SDP_CIPHER_HEAD_LEN), WSEC_LOG_E("[SDP] Buffer for cipher text is not enough."), WSEC_ERR_INPUT_BUFF_NOT_ENOUGH);
    pstCipherHead = (SDP_CIPHER_HEAD_STRU *)pucCipherText;

    /* byte order process */
    SDP_CvtByteOrder4CipherTextHeader(pstCipherHead, wbcNetwork2Host);
    return_oper_if(pstCipherHead->ulCDLen < 1, *pulPTLen = 0, WSEC_SUCCESS); /* ���ĳ���Ϊ0, ������� */

    /* check format */
    init_error_ctx(stErrCtx);
    update_error_ctx(stErrCtx, (SDP_CIPHER_TEXT_VER != pstCipherHead->ulVersion),
                   WSEC_LOG_E2("[SDP] Cipher text version is incompatible, %d expected, %d actually.", SDP_CIPHER_TEXT_VER, pstCipherHead->ulVersion), 
                   WSEC_ERR_SDP_VERSION_INCOMPATIBLE);    
    update_error_ctx(stErrCtx, (ulDomain != pstCipherHead->ulDomain), 
                   WSEC_LOG_E1("[SDP] Cipher text are marked with an unexpected domain %d.", pstCipherHead->ulDomain), 
                   WSEC_ERR_SDP_DOMAIN_UNEXPECTED);
    update_error_ctx(stErrCtx, (0 == pstCipherHead->ulCDLen), 
                   WSEC_LOG_E("[SDP] Cipher data length cannot be 0."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    update_error_ctx(stErrCtx, (pstCipherHead->bHmacFlag < 0 || pstCipherHead->bHmacFlag > SDP_HMAC_FLAG_MAX), 
                   WSEC_LOG_E("[SDP] Hmac flag is out of bounds."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    update_error_ctx(stErrCtx, (!KMC_IS_KEYITERATIONS_VALID(pstCipherHead->ulIterCount)), 
                   WSEC_LOG_E("[SDP] Iterator count is out of bounds."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    update_error_ctx(stErrCtx, (WSEC_ALGTYPE_UNKNOWN == CAC_AlgId2Type(pstCipherHead->ulAlgId)), 
                   WSEC_LOG_E("[SDP] CAC  algoGetrithm types failed."),
                   WSEC_ERR_SDP_ALG_NOT_SUPPORTED);
    return_oper_if((stErrCtx.ulErrCount > SDP_MAX_ERR_FIELD_OF_CIPHER), WSEC_LOG_E("[SDP] Cipher text format is invalid."), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    return_oper_if((stErrCtx.ulErrCount > 0), oper_null, stErrCtx.ulLastErrCode);

    return_oper_if((ulCTLen < (SDP_CIPHER_HEAD_LEN + pstCipherHead->ulCDLen)), WSEC_LOG_E("[SDP] Buffer for cipher text is not enough."), WSEC_ERR_INPUT_BUFF_NOT_ENOUGH);

    /* Get key length, IV length, MAC length */
    ulRet = SDP_GetAlgProperty(pstCipherHead->ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, &ulKeyLen, &ulIVLen, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_SYM != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);

    /* Get WK (work key) By KeyID */
    ulRet = SDP_GetWorkKeyByID(pstCipherHead->ulDomain, pstCipherHead->ulKeyId, pstCipherHead->ulIterCount,
                               pstCipherHead->aucSalt, SDP_SALT_LEN, aucKey, ulKeyLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get WK by key id failed."), ulRet);

    /* data decryption */
    return_oper_if(ulTempPTLen < pstCipherHead->ulCDLen, WSEC_LOG_E2("Plain-Buff size(%d) at least is %d", ulTempPTLen, pstCipherHead->ulCDLen), WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);
    ulRet = CAC_Decrypt(pstCipherHead->ulAlgId, aucKey, ulKeyLen, pstCipherHead->aucIV, ulIVLen,
                        (pucCipherText + SDP_CIPHER_HEAD_LEN), pstCipherHead->ulCDLen,
                        pucPlainText, /*suggested INOUT */ &ulTempPTLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC decrypt failed."), WSEC_ERR_DECRPT_FAIL);

    /* destroy key */
    WSEC_DESTROY_KEY(aucKey, ulKeyLen);

    /* verify HMAC */
    if (pstCipherHead->bHmacFlag )
    {
        ulRet = SDP_VerifyHmac(ulDomain, pucPlainText, ulTempPTLen,
                               (pucCipherText + SDP_CIPHER_HEAD_LEN + pstCipherHead->ulCDLen),
                               (ulCTLen - SDP_CIPHER_HEAD_LEN - pstCipherHead->ulCDLen) );
        return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Cipher text hmac verify failed."), ulRet);
    }

    /* output param */
    *pulPTLen = ulTempPTLen;

    return WSEC_SUCCESS;
}
/*****************************************************************************
> �� �� ��  : SDP_EncryptInit
> ��������  : ���������ݼ��ܳ�ʼ���ӿڡ��ṩ�ԳƼ����㷨�����ĳ�ʼ�����ܣ���ʼ�������㷨��Ҫ����Կ��IV�ȣ�
>             �����ڴ�����������Ҫ�������ܵĳ�����Ϊȷ��������ȷ���ܣ�Ҫ��Ӧ�ñ�������ͷ����
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : ulDomain ��ʾ������ID
> �������  : pstSdpCtx ��ʾ�������ݼ��ܹ��̽��������ݱ���������
>             pstBodCipherHeader ��ʾ����������ͷ�����ڴ�飬���׷��HMAC�������HMACͷ��
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ�����ͷ���Ѿ������
> ��������  : ��
> ��������  : ������WSEC_SUCCESSʱ��(*pulCHLen > 0) && (WSEC_NULL_PTR != *pstSdpCtx)
> ע������  : pucCipherHeaderָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��
>             pstSdpCtx��pulCHLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_EncryptInit(
    WSEC_UINT32 ulDomain,
    SDP_CRYPT_CTX *pstSdpCtx,
    SDP_BOD_CIPHER_HEAD *pstBodCipherHeader
    )
{
    SDP_CRYPT_CTX_STRU *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU *pstCtxCipherHead = WSEC_NULL_PTR;
    WSEC_BYTE  aucKey[SDP_KEY_MAX_LEN] = {0};
    WSEC_ERR_T ulRet            = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen        = 0;
    WSEC_UINT32 ulIVLen         = 0;

    /* check input */
    return_err_if_para_invalid("SDP_EncryptInit", pstSdpCtx && pstBodCipherHeader);

    /* construct context */
    pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)WSEC_MALLOC(SDP_CRYPT_CTX_LEN);
    return_oper_if((WSEC_NULL_PTR == pstSdpCtxObj), WSEC_LOG_E("[SDP] Memory for context allocate failed."), WSEC_ERR_MALLOC_FAIL);
    pstSdpCtxObj->eAlgType = WSEC_ALGTYPE_SYM;
    *pstSdpCtx = pstSdpCtxObj;
    pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);

    do
    {
        /* construct header, get MK-derived work key */
        /* header version is always the newest */
        WSEC_MEMSET(pstBodCipherHeader, sizeof(SDP_BOD_CIPHER_HEAD), 0, sizeof(SDP_BOD_CIPHER_HEAD));
        ulRet = SDP_FillCipherTextHeader(SDP_ALG_ENCRPT, ulDomain, pstCtxCipherHead,
                                         aucKey, &ulKeyLen, &ulIVLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Fill cipher text header failed."), oper_null);

        /* data encryption init */
        ulRet = CAC_EncryptInit(&(pstSdpCtxObj->stWsecCtx), pstCtxCipherHead->ulAlgId,
                                aucKey, ulKeyLen, pstCtxCipherHead->aucIV, ulIVLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC encrypt init failed."), ulRet = WSEC_ERR_ENCRPT_FAIL);

        /* destroy key */
        WSEC_DESTROY_KEY(aucKey, ulKeyLen);

        /* update header with real cipher length */
        pstCtxCipherHead->ulCDLen = 0u;

        /* output cipher header */
        break_oper_if(WSEC_MEMCPY(&pstBodCipherHeader->stCipherHeader, sizeof(pstBodCipherHeader->stCipherHeader), pstCtxCipherHead, sizeof(SDP_CIPHER_HEAD_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, ulRet = WSEC_ERR_MEMCPY_FAIL);

        /* compute data HMAC */
        if (pstCtxCipherHead->bHmacFlag )
        {
            ulRet = SDP_GetHmacAlgAttr(ulDomain, &pstBodCipherHeader->stHmacAlgAttr);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E1("SDP_GetHmacAlgAttr() = %u.", ulRet), oper_null);

            ulRet = SDP_HmacInit(ulDomain, &pstBodCipherHeader->stHmacAlgAttr, &pstSdpCtxObj->stSdpCtxHmac);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] SDP HMAC calculation init failed."), oper_null);
        }

        /* convert bytes-order */
        SDP_CvtByteOrder4CipherTextHeader((SDP_CIPHER_HEAD_STRU *)(&(pstBodCipherHeader->stCipherHeader)), wbcHost2Network);

        ulRet = WSEC_SUCCESS;
    } do_end;

    if (ulRet != WSEC_SUCCESS) { SDP_FreeCtx(pstSdpCtx); }

    return ulRet;
}

/*****************************************************************************
> �� �� ��  : SDP_EncryptUpdate
> ��������  : ���������ݼ��ܸ��½ӿڡ������ѽ��������ݱ��������ĶԴ�����ݽ��зֶμ��ܣ�
>             ���ִ�иú�������ɸ������ݶεļ��ܹ��̡����������ݵķֶ��ɵ�������ɣ�
>             �ú���ֻ�����Ѿ����ֺõ����ݶ���Ϊ���������������Ӧ�ɵ����߽������ӣ������������ġ�
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_EncryptInit���
>             pucPlainText ��ʾ��Ҫ���ܵķֶ������ڴ��
>             ulPTLen ��ʾ��Ҫ���ܵķֶ������ڴ��ĳ���
>             pulCTLen ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ��ķ��䳤��
> �������  : pucCipherText ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ��
>             pulCTLen ��ʾ��Ҫ���ɼ��ܺ����ĵ��ڴ���ʵ�ʳ���
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ��ֶ������Ѿ����
> ��������  : (WSEC_NULL_PTR != pucPlainText) && (0 != ulPTLen)
>             && (WSEC_NULL_PTR != pucCipherText) && (WSEC_NULL_PTR != pulCTLen)
>             && (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
> ��������  : ������WSEC_SUCCESSʱ��*pulCTLen > 0
> ע������  : pucCipherTextָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��
>             pulCTLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���
>  2.��    ��   : 2014��11��6��
>    ��    ��   : j00265291
>    �޸�����   : �޸ĺ��������������ʧ��ʱû�е���Final��ɵ��ڴ�й¶

*****************************************************************************/
WSEC_ERR_T SDP_EncryptUpdate (
      const SDP_CRYPT_CTX *pstSdpCtx,
      const WSEC_BYTE *pucPlainText,
      WSEC_UINT32 ulPTLen,
      WSEC_BYTE *pucCipherText,
      INOUT WSEC_UINT32 *pulCTLen
    )
{
    SDP_CRYPT_CTX_STRU      *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU    *pstCtxCipherHead = WSEC_NULL_PTR;
    WSEC_ERR_T  ulRet       = WSEC_SUCCESS;
    WSEC_UINT32 ulTempCTLen = 0;
    WSEC_UINT32 ulCTMaxLen  = 0;

    /* check input */
    return_err_if_para_invalid("SDP_EncryptUpdate", pucPlainText && pucCipherText && pstSdpCtx && pulCTLen && *pstSdpCtx && (ulPTLen > 0) && (*pulCTLen >= ulPTLen) && (pucPlainText != pucCipherText));

    ulCTMaxLen = *pulCTLen;
    ulTempCTLen = ulCTMaxLen;

    do 
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);
        pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);

        /* data encryption update */
        ulRet = CAC_EncryptUpdate(pstSdpCtxObj->stWsecCtx, pucPlainText, ulPTLen,
                                  pucCipherText, /* suggest INOUT */ &ulTempCTLen);
        if (ulRet!= WSEC_SUCCESS)
        {
            ulRet = WSEC_ERR_ENCRPT_FAIL;
            WSEC_LOG_E("[SDP] CAC encrypt update failed.");
            break;
        }

        /* update header with real cipher length, but not output to interface, avoiding rewrite */
        pstCtxCipherHead->ulCDLen += ulTempCTLen;

        /* compute data HMAC */
        if (pstCtxCipherHead->bHmacFlag )
        {
            ulRet = SDP_HmacUpdate(&(pstSdpCtxObj->stSdpCtxHmac), pucPlainText, ulPTLen);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] SDP HMAC calculation update failed."), oper_null);
        }

        /* output param */
        *pulCTLen = ulTempCTLen;

        ulRet = WSEC_SUCCESS;
    } do_end;

    if (ulRet != WSEC_SUCCESS) { SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx); }

    return ulRet;
}

/*****************************************************************************
> �� �� ��  : SDP_EncryptFinal
> ��������  : ���������ݼ��ܽ����ӿڣ������ѽ��������ݱ��������ģ��������һ���������ݶΡ�
>             ���������µ�����ͷ������������ѡ���Ƿ�����HMAC���ġ�
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_EncryptInit�����
>             pulCHLen��ʾ��Ҫ���ɼ��ܽ���������ͷ�����ڴ��ķ��䳤�ȣ�
>             pulCTLen��ʾ��Ҫ���ɼ��ܽ��������ĵ��ڴ��ķ��䳤�ȣ�
>             pulHTLen��ʾ��Ҫ���ɼ��ܽ�����HMAC���ڴ��ķ��䳤�ȣ�
> �������  : pucCipherHeader ��ʾ��Ҫ���ɼ��ܽ���������ͷ�����ڴ�飻
>             pulCHLen ��ʾ��Ҫ���ɼ��ܽ���������ͷ�����ڴ���ʵ�ʳ��ȣ�
>             pucCipherText ��ʾ��Ҫ���ɼ��ܽ��������ĵ��ڴ�飻
>             pulCTLen ��ʾ��Ҫ���ɼ��ܽ��������ĵ��ڴ���ʵ�ʳ��ȣ�
>             pucHmacText��ʾ��Ҫ���ɼ��ܽ�����HMAC���ڴ�飻
>             pulHTLen��ʾ��Ҫ���ɼ��ܽ�����HMAC���ڴ���ʵ�ʳ��ȣ�
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ������Ѿ������
> ��������  : (WSEC_NULL_PTR != pucCipherText) && (WSEC_NULL_PTR != pulCTLen)
>             && (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
> ��������  : ������WSEC_SUCCESSʱ��(*pulCHLen> 0) && (*pulCTLen > 0)
> ע������  : pucHmacText��pucCipherHeader��pucCipherTextָ����ڴ���ɵ����߷�����ͷţ�
>             pucCipherHeader��pucCipherText�ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��
>             pucHmacText�ڴ��Ĵ�С����ʹ��SDP_GetHmacLen��ȡ��pulCHLen��pulCTLen��pulHTLen�ɵ����߷����ڴ档
>             ������Ҫ��׷��HMACʱ����������(WSEC_NULL_PTR != pucHmacText) && (WSEC_NULL_PTR != pulHTLen)�����HMAC�����򷵻ش���
>             �����ò�Ҫ��׷��HMACʱ�����Բ���pucHmacText��pulHTLen��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_EncryptFinal(
      const SDP_CRYPT_CTX *pstSdpCtx,
      WSEC_BYTE *pucCipherText,
      INOUT WSEC_UINT32 *pulCTLen,
      WSEC_BYTE *pucHmacText,
      INOUT WSEC_UINT32 *pulHTLen
    )
{
    SDP_CRYPT_CTX_STRU          *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU        *pstCtxCipherHead = WSEC_NULL_PTR;
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulTempMacLen    = 0;
    WSEC_UINT32 ulTempCTLen     = 0;
    WSEC_UINT32 ulCTMaxLen      = 0;
    WSEC_UINT32 ulHTMaxLen      = 0;

    /* check input */
    return_err_if_para_invalid("SDP_EncryptFinal", pucCipherText && pstSdpCtx && (*pstSdpCtx) && (pulCTLen));

    ulCTMaxLen = *pulCTLen;
    ulTempCTLen = ulCTMaxLen;
    ulHTMaxLen = *pulHTLen;

    do
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);
        pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);
        break_oper_if((pstCtxCipherHead->bHmacFlag) && (WSEC_NULL_PTR == pucHmacText || WSEC_NULL_PTR == pulHTLen), 
                      WSEC_LOG_E("[SDP] Invalid parameter. Non-Null expected."), 
                      ulRet = WSEC_ERR_INVALID_ARG);

        /* data encryption final */
        ulRet = CAC_EncryptFinal(&(pstSdpCtxObj->stWsecCtx), pucCipherText, /* suggest INOUT */ &ulTempCTLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC encrypt update failed."), ulRet = WSEC_ERR_ENCRPT_FAIL);

        /* update header with real cipher length, but not output to interface, avoiding rewrite */
        pstCtxCipherHead->ulCDLen += ulTempCTLen;

        /* compute data HMAC */
        if (pstCtxCipherHead->bHmacFlag )
        {
            ulTempMacLen = ulHTMaxLen;
            ulRet = SDP_HmacFinal(&(pstSdpCtxObj->stSdpCtxHmac), pucHmacText, /* suggest INOUT */ &ulTempMacLen);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] SDP HMAC calculation final failed."), oper_null);
        }

        /* output param */
        *pulCTLen = ulTempCTLen;
        *pulHTLen = ulTempMacLen;

        ulRet = WSEC_SUCCESS;
    } do_end;

    SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx);
    return ulRet;
}

/*****************************************************************************
 �� �� ��  : SDP_EncryptCancel
 ��������  : ȡ���ֶμ��ܴ���ĺ�������.
             �����ķֶμ��ܴ��������: Init ---> Update ---> Final. ������м�����
                                              ��________|
                                                   
             ����ȡ�������Ĵ���, ����Ҫ���ô˺���.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pstSdpCtx: [in]ָ��������Ϣ��ָ��, [out]ָ������ΪNULL
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��5��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_EncryptCancel(SDP_CRYPT_CTX *pstSdpCtx)
{
    SDP_FreeCtx(pstSdpCtx);
}

/*****************************************************************************
> �� �� ��  : SDP_DecryptInit
> ��������  : ���������ݽ��ܳ�ʼ���ӿڡ��ṩ��ʼ�����ݱ��������Ĺ��ܡ�
>             ʹ����������ͷ���������ݱ��������ģ��������õ����ݱ��������ķ��ظ�Ӧ�á�
>             ��������ͷ��ѡ���Ƿ�У��HMAC���ġ�
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : pucCipherHeader ��ʾ��Ҫ����������ͷ����
>             pulCHLen ��ʾ��Ҫ����������ͷ���ķ��䳤�ȣ��ó��ȿ��ܴ�������ͷ����ʵ�ʳ��ȣ�
>             pucHmacText ��ʾ��ҪУ���HMAC���ģ��������ݱ�����HMACͷ����
>             ulHTLen��ʾ��ҪУ���HMAC���ĵĳ��ȣ�
> �������  : pstSdpCtx ��ʾ�������ݽ��ܹ��̽��������ݱ��������ģ�
>             pulCHLen ��ʾ��Ҫ����������ͷ����ʵ�ʳ��ȣ�
>             pulCDLen ��ʾ��Ҫ�������������ݵ�ʵ�ʳ��ȣ�
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������ء�
> ��������  : ��
> ��������  : ������WSEC_SUCCESSʱ��(*pulCHLen > 0) && (*pulCDLen > 0) && (WSEC_NULL_PTR != *pstSdpCtx)
> ע������  : pucCipherHeaderָ����ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��
>             pucHmacTextָ����ڴ��Ĵ�С����ʹ��SDP_GetHmacLen��ȡ��pstSdpCtx��pulCHLen��pulCDLen�ɵ����߷����ڴ档
>             �����İ���׷��HMACʱ����������(WSEC_NULL_PTR != pucHmacText) && (0 != ulHTLen)��У��HMAC�����򷵻ش���
>             �����Ĳ�����׷��HMACʱ�����Բ���pucHmacText��ulHTLen��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_DecryptInit(
      WSEC_UINT32   ulDomain,
      SDP_CRYPT_CTX *pstSdpCtx,
      const SDP_BOD_CIPHER_HEAD* pstBodCipherHeader
    )
{
    SDP_CRYPT_CTX_STRU          *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU        *pstCtxCipherHead = WSEC_NULL_PTR;
    SDP_ERROR_CTX_STRU          stErrCtx;
    WSEC_ALGTYPE_E eAlgType     = WSEC_ALGTYPE_UNKNOWN;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN];
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen        = 0;
    WSEC_UINT32 ulIVLen         = 0;

    /* check input */
    return_err_if_para_invalid("SDP_DecryptInit", pstSdpCtx);

    /* construct context */
    pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)WSEC_MALLOC(SDP_CRYPT_CTX_LEN);
    return_oper_if(!pstSdpCtxObj, WSEC_LOG_E("[SDP] Memory for context allocate failed."), WSEC_ERR_MALLOC_FAIL);
    pstSdpCtxObj->eAlgType = WSEC_ALGTYPE_SYM;
    *pstSdpCtx = pstSdpCtxObj;
    pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);

    do
    {
        WSEC_ASSERT(sizeof(SDP_CIPHER_HEAD_STRU) <= sizeof(pstBodCipherHeader->stCipherHeader));
        break_oper_if(WSEC_MEMCPY(pstCtxCipherHead, sizeof(SDP_CIPHER_HEAD_STRU), &pstBodCipherHeader->stCipherHeader, sizeof(SDP_CIPHER_HEAD_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, ulRet = WSEC_ERR_MEMCPY_FAIL);
        
        /* parse header, its version is referred to cipher text */
        /* case is version 1 */
        SDP_CvtByteOrder4CipherTextHeader(pstCtxCipherHead, wbcNetwork2Host);

        /* check format */
        init_error_ctx(stErrCtx);
        update_error_ctx(stErrCtx, (SDP_CIPHER_TEXT_VER != pstCtxCipherHead->ulVersion),
                      WSEC_LOG_E2("[SDP] Cipher text version is incompatible, %d expected, %d actually.", SDP_CIPHER_TEXT_VER, pstCtxCipherHead->ulVersion), 
                      ulRet = WSEC_ERR_SDP_VERSION_INCOMPATIBLE);  
        update_error_ctx(stErrCtx, (ulDomain != pstCtxCipherHead->ulDomain), 
                      WSEC_LOG_E1("[SDP] Cipher text are marked with an unexpected domain %d.", pstCtxCipherHead->ulDomain), 
                      ulRet = WSEC_ERR_SDP_DOMAIN_UNEXPECTED);
        update_error_ctx(stErrCtx, (0 != pstCtxCipherHead->ulCDLen), 
                      WSEC_LOG_E("[SDP] Cipher data length must be 0."),
                      WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
        update_error_ctx(stErrCtx, (pstCtxCipherHead->bHmacFlag < 0 || pstCtxCipherHead->bHmacFlag > SDP_HMAC_FLAG_MAX), 
                      WSEC_LOG_E("[SDP] Hmac flag is out of bounds."),
                      WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
        update_error_ctx(stErrCtx, !KMC_IS_KEYITERATIONS_VALID(pstCtxCipherHead->ulIterCount), 
                      WSEC_LOG_E("[SDP] Iterator count is out of bounds."),
                      WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
        update_error_ctx(stErrCtx, (WSEC_ALGTYPE_UNKNOWN == CAC_AlgId2Type(pstCtxCipherHead->ulAlgId)), 
                      WSEC_LOG_E("[SDP] CAC Get algorithm types failed."), 
                      WSEC_ERR_SDP_ALG_NOT_SUPPORTED);
        break_oper_if((stErrCtx.ulErrCount > SDP_MAX_ERR_FIELD_OF_CIPHER), WSEC_LOG_E("[SDP] Cipher text format is invalid."), ulRet = WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
        break_oper_if((stErrCtx.ulErrCount > 0), oper_null, ulRet = stErrCtx.ulLastErrCode);

        /* Get key length, IV length, MAC length */
        ulRet = SDP_GetAlgProperty(pstCtxCipherHead->ulAlgId, WSEC_NULL_PTR, 0, 
                                   &eAlgType, &ulKeyLen, &ulIVLen, WSEC_NULL_PTR, WSEC_NULL_PTR);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Get algorithm property failed."), oper_null);
        break_oper_if(WSEC_ALGTYPE_SYM != eAlgType, 
                      WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), 
                      ulRet = WSEC_ERR_SDP_INVALID_CIPHER_TEXT);

        /* Get WK (work key) By KeyID */
        ulRet = SDP_GetWorkKeyByID(pstCtxCipherHead->ulDomain, pstCtxCipherHead->ulKeyId,
                                   pstCtxCipherHead->ulIterCount, pstCtxCipherHead->aucSalt,
                                   SDP_SALT_LEN, aucKey, ulKeyLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Get WK by KeyID failed."), oper_null);

        /* data decryption init */
        ulRet = CAC_DecryptInit(&(pstSdpCtxObj->stWsecCtx), pstCtxCipherHead->ulAlgId,
                                aucKey, ulKeyLen, pstCtxCipherHead->aucIV, ulIVLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC decrypt init failed."), ulRet = WSEC_ERR_DECRPT_FAIL);

        /* destroy key */
        WSEC_DESTROY_KEY(aucKey, ulKeyLen);

        /* verify HMAC init */
        if (pstCtxCipherHead->bHmacFlag )
        {
            ulRet = SDP_HmacInit(ulDomain, &pstBodCipherHeader->stHmacAlgAttr, &pstSdpCtxObj->stSdpCtxHmac);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Cipher text hmac verify init failed."), oper_null);
        }

        ulRet = WSEC_SUCCESS;
    } do_end;

    if (ulRet != WSEC_SUCCESS) { SDP_FreeCtx(pstSdpCtx); }

    return ulRet;
}

/*****************************************************************************
> �� �� ��  : SDP_DecryptUpdate
> ��������  : ���������ݽ��ܸ��½ӿڣ��ṩ���ܹ��ܣ��������ݱ��������ĶԴ�����Ľ��зֶν��ܡ�
>             ͨ�����ݱ��������Ļ�ȡ����������㷨��IV��Salt����ԿID�������ݽ��н��ܣ������ķ��ظ�Ӧ�á�
>             ���ִ�иú�������ɸ������ĶεĽ��ܹ��̡�������ĵķֶ��ɵ�������ɣ�
>             �ú���ֻ�����Ѿ����ֺõ����Ķ���Ϊ���������������Ӧ�ɵ����߽������ӣ������������ġ�
>             ��������ͷ��ѡ���Ƿ�У��HMAC���ġ�
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_DecryptInit�����
>             pucCipherText ��ʾ��Ҫ���ܵķֶ������ڴ�飻
>             ulCTLen ��ʾ��Ҫ���ܵķֶ������ڴ��ĳ��ȣ�
>             pulPTLen ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ��ķ��䳤�ȣ�
> �������  : pucPlainText ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ�飻
>             pulPTLen ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ���ʵ�ʳ��ȣ�
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ��ֶ������Ѿ������
> ��������  : (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
>             && (WSEC_NULL_PTR != pucCipherText) && (0 != ulCTLen)
>             && (WSEC_NULL_PTR != pucPlainText) && (WSEC_NULL_PTR != pulPTLen)
> ��������  : ������WSEC_SUCCESSʱ��*pulPTLen> 0
> ע������  : pucPlainTextָ����ڴ���ɵ����߷�����ͷţ���AES�����£����ĳ������ٰ���һ���������Ŀ�Ĵ�С��
>             �ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��pulPTLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���
>  2.��    ��   : 2014��11��6��
>    ��    ��   : j00265291
>    �޸�����   : �޸ĺ��������������ʧ��ʱû�е���Final��ɵ��ڴ�й¶

*****************************************************************************/
WSEC_ERR_T SDP_DecryptUpdate(
      const SDP_CRYPT_CTX *pstSdpCtx,
      const WSEC_BYTE *pucCipherText,
      WSEC_UINT32 ulCTLen,
      WSEC_BYTE *pucPlainText,
      INOUT WSEC_UINT32 *pulPTLen
    )
{
    SDP_CRYPT_CTX_STRU          *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU        *pstCtxCipherHead = WSEC_NULL_PTR;
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulTempPTLen     = 0;
    WSEC_UINT32 ulPTMaxLen      = 0;

    /* check input */
    return_err_if_para_invalid("SDP_DecryptUpdate", pstSdpCtx && *pstSdpCtx && pucCipherText && pucPlainText && pulPTLen && (ulCTLen > 0) && (pucPlainText != pucCipherText));

    ulPTMaxLen = *pulPTLen;
    ulTempPTLen = ulPTMaxLen;

    do 
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);
        pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);

        /* data decryption update */
        ulRet = CAC_DecryptUpdate(pstSdpCtxObj->stWsecCtx,
                                  pucCipherText, ulCTLen, pucPlainText, /* suggest INOUT */ &ulTempPTLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC decrypt update failed."), ulRet = WSEC_ERR_DECRPT_FAIL);

        /* verify HMAC update */
        if ((pstCtxCipherHead->bHmacFlag) && (ulTempPTLen > 0))
        {
            ulRet = SDP_HmacUpdate(&pstSdpCtxObj->stSdpCtxHmac, pucPlainText, ulTempPTLen);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Cipher text hmac verify update failed."), oper_null);
        }

        /* output param */
        *pulPTLen = ulTempPTLen;

        ulRet = WSEC_SUCCESS;
    } do_end;

    if (ulRet != WSEC_SUCCESS) { SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx); }

    return ulRet;
}

/*****************************************************************************
> �� �� ��  : SDP_DecryptFinal
> ��������  : ���������ݽ��ܽ����ӿڡ��ṩ���ܹ��ܣ��������ݱ��������Ķ����һ�����Ķν��н��ܡ���������ͷ��ѡ���Ƿ�У��HMAC���ġ�
> ���Ľṹ  : cipher-head | hmac-head | cipher-data & hmac-data
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_DecryptInit�����
>             pucHmacText ��ʾ��ҪУ���HMAC���ģ��������ݱ�����HMACͷ����
>             ulHTLen ��ʾ��ҪУ���HMAC���ĵĳ��ȣ�
>             pulPTLen ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ��ķ��䳤�ȣ�
> �������  : pucPlainText ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ�飻
>             pulPTLen ��ʾ��Ҫ���ɽ��ܺ����ĵ��ڴ���ʵ�ʳ��ȣ�
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ������Ѿ������
> ��������  : (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
>             && (WSEC_NULL_PTR != pucPlainText) && (WSEC_NULL_PTR != pulPTLen)
> ��������  : ������WSEC_SUCCESSʱ��*pulPTLen > 0
> ע������  : pucPlainTextָ����ڴ���ɵ����߷�����ͷţ���AES�����£����ĳ������ٰ���һ���������Ŀ�Ĵ�С��
>             �ڴ��Ĵ�С����ʹ��SDP_GetCipherDataLen��ȡ��pulPTLen�ɵ����߷����ڴ档
>             �����İ���׷��HMACʱ����������(WSEC_NULL_PTR != pucHmacText) && (0 != ulHTLen)��У��HMAC�����򷵻ش���
>             �����Ĳ�����׷��HMACʱ�����Բ���pucHmacText��ulHTLen��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_DecryptFinal(
      const SDP_CRYPT_CTX *pstSdpCtx,
      const WSEC_BYTE    *pucHmacText,
      WSEC_UINT32         ulHTLen,
      WSEC_BYTE          *pucPlainText,
      INOUT WSEC_UINT32   *pulPTLen
    )
{
    SDP_CRYPT_CTX_STRU          *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_CIPHER_HEAD_STRU        *pstCtxCipherHead = WSEC_NULL_PTR;
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulTempPTLen     = 0;
    WSEC_UINT32 ulPTMaxLen      = 0;
    WSEC_BUFF stHmacNew = {0};

    /* check input */
    return_err_if_para_invalid("SDP_DecryptFinal", pstSdpCtx && *pstSdpCtx && pucPlainText && pulPTLen);

    ulPTMaxLen = *pulPTLen;
    ulTempPTLen = ulPTMaxLen;

    do
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);
        pstCtxCipherHead = &(pstSdpCtxObj->stCipherHead);
        break_oper_if((pstCtxCipherHead->bHmacFlag) && ((WSEC_NULL_PTR == pucHmacText) || (0 == ulHTLen)), 
                      WSEC_LOG_E("[SDP] Invalid parameter. Non-Null HMAC pointer expected."), 
                      ulRet = WSEC_ERR_INVALID_ARG);

        /* data decryption final */
        ulRet = CAC_DecryptFinal(&(pstSdpCtxObj->stWsecCtx), pucPlainText, /* suggest INOUT */ &ulTempPTLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC decrypt final failed."), ulRet = WSEC_ERR_DECRPT_FAIL);

        /* verify HMAC final */
        if (pstCtxCipherHead->bHmacFlag)
        {
            if (ulTempPTLen > 0)
            {
                ulRet = SDP_HmacUpdate(&pstSdpCtxObj->stSdpCtxHmac, pucPlainText, ulTempPTLen);
                break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Cipher text hmac verify update failed."), oper_null);
            }

            WSEC_BUFF_ALLOC(stHmacNew, WSEC_HMAC_LEN_MAX);
            break_oper_if(!stHmacNew.pBuff, WSEC_LOG_E4MALLOC(stHmacNew.nLen), ulRet = WSEC_ERR_MALLOC_FAIL);

            ulRet = SDP_HmacFinal(&pstSdpCtxObj->stSdpCtxHmac, (WSEC_BYTE*)stHmacNew.pBuff, &stHmacNew.nLen);
            break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] Cipher text hmac verify final failed."), oper_null);

            break_oper_if(stHmacNew.nLen != ulHTLen, WSEC_LOG_E("Verify hmac failed for HMAC-LEN is not same."), ulRet = WSEC_ERR_HMAC_AUTH_FAIL);
            break_oper_if(WSEC_MEMCMP(stHmacNew.pBuff, pucHmacText, ulHTLen) != 0, WSEC_LOG_E("Verify hmac failed for HMAC is not same."), ulRet = WSEC_ERR_HMAC_AUTH_FAIL);
        }

        /* output param */
        *pulPTLen = ulTempPTLen;
    } do_end;

    SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx);
    return ulRet;
}

/*****************************************************************************
 �� �� ��  : SDP_DecrypCancel
 ��������  : ȡ���ֶν��ܴ���ĺ�������.
             �����ķֶν��ܴ��������: Init ---> Update ---> Final. ������м�����
                                              ��________|
                                                   
             ����ȡ�������Ĵ���, ����Ҫ���ô˺���.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pstSdpCtx: [in]ָ��������Ϣ��ָ��, [out]ָ������ΪNULL
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��5��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_DecrypCancel(SDP_CRYPT_CTX *pstSdpCtx)
{
    SDP_FreeCtx(pstSdpCtx);
}

/*****************************************************************************
 �� �� ��  : SDP_FileEncrypt
 ��������  : ���ļ�����
 �� �� ��  : ulDomain: �������ʶ
             pszPlainFile:  �����ļ�
             pszCipherFile: �����ļ�
             pfGetFileDateTime: ��ȡ�ļ�ʱ����Ϣ����, ���Caller�ṩ, ��ԭ�ļ���
                                ʱ����Ϣ��¼, ����ʱ���Ի�ԭ��ʱ����Ϣ.[��NULL]
             pstRptProgress:    �����ϱ�����.[��NULL]
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_FileEncrypt(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszPlainFile, const WSEC_CHAR *pszCipherFile, WSEC_FP_GetFileDateTime pfGetFileDateTime, const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    WSEC_FILE fRead = WSEC_NULL_PTR;
    WSEC_FILE fWri  = WSEC_NULL_PTR;
    WSEC_BUFF stPlain  = {0};
    WSEC_BUFF stCipher = {0};
    WSEC_BUFF stHmac   = {0};
    SDP_CIPHER_FILE_HDR_STRU stFileHdr = {0};
    WSEC_SIZE_T nPlainLen, nCipherLen, nHmacLen;
    WSEC_FILE_LEN ulSrcFileLen = 0, nReadSize = 0;
    SDP_CRYPT_CTX stSdpCtx = {0};
    SDP_BOD_CIPHER_HEAD stBodCipherHeader = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE; /* �����������Ƿ�APPȡ�� */
    WSEC_BOOL bRmvWhenFail = WSEC_FALSE; /* ����ʧ�ܺ��Ƿ�ɾ�������ļ�. */

    WSEC_ASSERT(sizeof(stFileHdr.abFormatFlag) == sizeof(g_CipherFileFlag));
    return_err_if_para_invalid("SDP_FileEncrypt", pszPlainFile && pszCipherFile);
    return_err_if_para_invalid("SDP_FileEncrypt", WSEC_NOT_IS_EMPTY_STRING(pszPlainFile) && WSEC_NOT_IS_EMPTY_STRING(pszCipherFile));

    return_oper_if(!WSEC_GetFileLen(pszPlainFile, &ulSrcFileLen), WSEC_LOG_E1("Cannot acess '%s'", pszPlainFile), WSEC_ERR_OPEN_FILE_FAIL);

    /* 1. �����ļ�ͷ׼�� */
    stFileHdr.ulPlainBlockLenMax = WSEC_FILE_IO_SIZE_MAX;
    return_oper_if(!WSEC_GetUtcDateTime(&stFileHdr.tCreateFileTimeUtc), WSEC_LOG_E("WSEC_GetUtcDateTime() fail."), WSEC_ERR_GET_CURRENT_TIME_FAIL);
    nErrCode = SDP_GetCipherDataLen(stFileHdr.ulPlainBlockLenMax, &stFileHdr.ulCipherBlockLenMax);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_GetCipherDataLen()=%u", nErrCode), nErrCode);
    return_oper_if(WSEC_MEMCPY(stFileHdr.abFormatFlag, sizeof(stFileHdr.abFormatFlag), g_CipherFileFlag, sizeof(g_CipherFileFlag)) != EOK, WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);
    stFileHdr.ulVer = SDP_CIPHER_FILE_VER;

    if (pfGetFileDateTime)
    {
        if (!pfGetFileDateTime(pszPlainFile, &stFileHdr.tSrcFileCreateTime, &stFileHdr.tSrcFileEditTime)) /* ȷ��ʱ��������Ч, ����APPŪ������ */
        {
            stFileHdr.tSrcFileCreateTime.uwYear = 0;
            stFileHdr.tSrcFileEditTime.uwYear = 0;
        }
    }

    /* 2. �򿪶�&д�ļ� */
    fRead = WSEC_FOPEN(pszPlainFile, "rb");
    return_oper_if(!fRead, WSEC_LOG_E1("Open '%s' fail.", pszPlainFile), WSEC_ERR_OPEN_FILE_FAIL);
    fWri = WSEC_FOPEN(pszCipherFile, "wb");

    /* 3. �߶������ļ�, ���������Ĳ�д���� */
    do
    {
        break_oper_if(!fWri, WSEC_LOG_E1("Cannot write '%s'.", pszCipherFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
        WSEC_BUFF_ALLOC(stPlain, stFileHdr.ulPlainBlockLenMax);
        WSEC_BUFF_ALLOC(stCipher, stFileHdr.ulCipherBlockLenMax);
        WSEC_BUFF_ALLOC(stHmac, SDP_HMAC_MAX_SIZE); /* �ռ䲻����ȷ����, �����ռ���� */
        break_oper_if(!stPlain.pBuff, WSEC_LOG_E4MALLOC(stPlain.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stCipher.pBuff, WSEC_LOG_E4MALLOC(stCipher.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmac.pBuff, WSEC_LOG_E4MALLOC(stHmac.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);

        /* 3.1 д�����ļ�ͷ */
        bRmvWhenFail = WSEC_TRUE;
        SDP_CvtByteOrder4CipherFileHdr(&stFileHdr, wbcHost2Network);
        nErrCode = WSEC_WriteTlv(fWri, SDP_CFT_FILE_HDR, sizeof(stFileHdr), &stFileHdr);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", nErrCode), oper_null);

        /* 3.2 �����ݼ��ܳ�ʼ�� */
        nErrCode = SDP_EncryptInit(ulDomain, &stSdpCtx, &stBodCipherHeader);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_EncryptInit()=%u", nErrCode), oper_null);

        /* 3.2.1 д����ͷ */
        nErrCode = WSEC_WriteTlv(fWri, SDP_CFT_CIPHER_HDR, sizeof(stBodCipherHeader), &stBodCipherHeader);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", nErrCode), oper_null);

        /* 3.3 �������ļ�, ����, д�����ļ� */
        while (!WSEC_FEOF(fRead))
        {
            nPlainLen = (WSEC_SIZE_T)WSEC_FREAD(stPlain.pBuff, 1, stPlain.nLen, fRead);
            break_oper_if(WSEC_FERROR(fRead), WSEC_LOG_E1("Read file(%s) fail.", pszPlainFile), nErrCode = WSEC_ERR_READ_FILE_FAIL);
            if (0 == nPlainLen) {break;}
            nReadSize += nPlainLen; /* �Ѿ��������ĳ��� */
            nCipherLen = stCipher.nLen;
            nErrCode = SDP_EncryptUpdate(&stSdpCtx, (const WSEC_BYTE*)stPlain.pBuff, nPlainLen, (WSEC_BYTE*)stCipher.pBuff, &nCipherLen);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_EncryptUpdate()=%u", nErrCode), oper_null);

            /* д������ */
            if (nCipherLen > 0)
            {
                nErrCode = WSEC_WriteTlv(fWri, SDP_CFT_CIPHER_BODY, nCipherLen, stCipher.pBuff);
                WSEC_FFLUSH(fWri);
                break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", nErrCode), oper_null);
            }

            /* �����ϱ� */
            WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)ulSrcFileLen, (WSEC_UINT32)nReadSize, &bCancel);
            if (bCancel) {break;}
        }
        if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, (WSEC_UINT32)ulSrcFileLen, (WSEC_UINT32)ulSrcFileLen, WSEC_NULL_PTR);} /* ȷ�����Ȱ�100%�ϱ� */

        /* 3.4 ���ܽ��� */
        nCipherLen = stCipher.nLen;
        nHmacLen = stHmac.nLen;
        nTemp = SDP_EncryptFinal(&stSdpCtx, (WSEC_BYTE*)stCipher.pBuff, &nCipherLen, (WSEC_BYTE*)stHmac.pBuff, &nHmacLen);
        break_oper_if(bCancel, WSEC_LOG_E("Encrypt progress canceled."), nErrCode = WSEC_ERR_CANCEL_BY_APP);
        break_oper_if(nTemp != WSEC_SUCCESS, WSEC_LOG_E1("SDP_EncryptFinal()=%u", nErrCode), nErrCode = nTemp);
        
        if (nErrCode != WSEC_SUCCESS) {break;}

        if (nCipherLen > 0)
        {
            nErrCode = WSEC_WriteTlv(fWri, SDP_CFT_CIPHER_BODY, nCipherLen, stCipher.pBuff);
            WSEC_FFLUSH(fWri);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", nErrCode), oper_null);
        }

        /* дHMAC */
        if (nHmacLen > 0)
        {
            nErrCode = WSEC_WriteTlv(fWri, SDP_CFT_HMAC_VAL, nHmacLen, stHmac.pBuff);
            WSEC_FFLUSH(fWri);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", nErrCode), oper_null);
        }
    }do_end;    

    /* 4. �ͷ���Դ */
    WSEC_FCLOSE(fRead);
    WSEC_FFLUSH(fWri);
    WSEC_FCLOSE(fWri);
    WSEC_BUFF_FREE(stPlain);
    WSEC_BUFF_FREE(stCipher);
    WSEC_BUFF_FREE(stHmac);
    SDP_FreeCtx(&stSdpCtx);

    /* Misinformation: FORTIFY.Race_Condition--File_System_Access */
    if ((nErrCode != WSEC_SUCCESS) && bRmvWhenFail) {WSEC_UNCARE(WSEC_FREMOVE(pszCipherFile));}/* ����ʧ��, ����������ļ� */

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : SDP_FileDecrypt
 ��������  : ���ļ�����
 �� �� ��  : pszCipherFile: �����ļ�
             pszPlainFile: �����ļ�
             pfSetFileDateTime: �����ļ�ʱ����Ϣ����, ���Caller�ṩ, ��ԭ�ļ���
                                ʱ����Ϣ��¼��ԭ[��NULL]
             pstRptProgress:    �����ϱ�����.[��NULL]
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_FileDecrypt(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszCipherFile, const WSEC_CHAR *pszPlainFile, WSEC_FP_SetFileDateTime pfSetFileDateTime, const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    WSEC_FILE fRead = WSEC_NULL_PTR;
    WSEC_FILE fWri  = WSEC_NULL_PTR;
    WSEC_BUFF stPlain  = {0};
    WSEC_BUFF stCipher = {0};
    WSEC_BUFF stHmac   = {0};
    SDP_CIPHER_FILE_HDR_STRU stFileHdr = {0};
    SDP_BOD_CIPHER_HEAD stBodCipherHeader = {0};
    SDP_CRYPT_CTX stSdpCtx = {0};
    WSEC_TLV_STRU stTlv = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;
    WSEC_FILE_LEN ulSrcFileLen = 0, nReadSize = 0;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE; /* �����������Ƿ�APPȡ�� */

    return_err_if_para_invalid("SDP_FileEncrypt", pszPlainFile && pszCipherFile);
    return_err_if_para_invalid("SDP_FileEncrypt", WSEC_NOT_IS_EMPTY_STRING(pszPlainFile) && WSEC_NOT_IS_EMPTY_STRING(pszCipherFile));

    return_oper_if(!WSEC_GetFileLen(pszCipherFile, &ulSrcFileLen), WSEC_LOG_E1("Cannot acess '%s'", pszCipherFile), WSEC_ERR_OPEN_FILE_FAIL);

    do
    {
        fRead = WSEC_FOPEN(pszCipherFile, "rb");
        fWri = WSEC_FOPEN(pszPlainFile, "wb");
        break_oper_if(!fRead, WSEC_LOG_E1("Cannot open '%s'", pszCipherFile), nErrCode = WSEC_ERR_OPEN_FILE_FAIL);
        break_oper_if(!fWri, WSEC_LOG_E1("Cannot write '%s'", pszPlainFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);

        /* 1. �����ļ���� */
        /* 1.1 �������ļ�ͷ */
        if (!WSEC_ReadTlv(fRead, &stFileHdr, sizeof(stFileHdr), &stTlv, &nErrCode)) {break;}
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Read '%s' fail.", pszCipherFile), oper_null);
        break_oper_if(stTlv.ulTag != SDP_CFT_FILE_HDR, WSEC_LOG_E1("'%s' is not expected encrpt file for TLV's tag is not SDP_CFT_FILE_HDR.", pszCipherFile), nErrCode = WSEC_ERR_FILE_FORMAT);
        SDP_CvtByteOrder4CipherFileHdr(&stFileHdr, wbcNetwork2Host);
        nReadSize += sizeof(stFileHdr);

        stFileHdr.ulPlainBlockLenMax += 100; /* OpenSSLҪ�����Ļ��������ȴ���ʵ�ʳ���һ��Block, Ϊ��ȫ����, �����ĳ��ȷŴ��Ա�����㹻�Ļ�������OpenSSLʹ��. */

        /* 1) ����ʽ */
        break_oper_if(WSEC_MEMCMP(g_CipherFileFlag, stFileHdr.abFormatFlag, sizeof(g_CipherFileFlag)) != 0,
                      WSEC_LOG_E1("The format of '%s' is incorrect.", pszCipherFile), nErrCode = WSEC_ERR_FILE_FORMAT);

        /* 2) ���汾 */
        if (stFileHdr.ulVer != SDP_CIPHER_FILE_VER) /* �汾����ȷ, ��Ҫ�Զ�����, �������������. */
        {
            WSEC_LOG_E1("The version of '%s' is incorrect.", pszCipherFile);
            nErrCode = WSEC_ERR_SDP_VERSION_INCOMPATIBLE;
            break;
        }

        /* 1.2 ������ͷ */
        if (!WSEC_ReadTlv(fRead, &stBodCipherHeader, sizeof(stBodCipherHeader), &stTlv, &nErrCode)) {break;}
        break_oper_if(stTlv.ulTag != SDP_CFT_CIPHER_HDR, WSEC_LOG_E1("'%s' is not expected encrpt file for TLV's tag not is SDP_CFT_CIPHER_HDR.", pszCipherFile), nErrCode = WSEC_ERR_FILE_FORMAT);

        nReadSize += sizeof(stBodCipherHeader);

        /* 2 ���������ļ����Ķ� */
        WSEC_BUFF_ALLOC(stCipher, stFileHdr.ulCipherBlockLenMax);
        WSEC_BUFF_ALLOC(stPlain, stFileHdr.ulPlainBlockLenMax);

        break_oper_if(!stCipher.pBuff, WSEC_LOG_E4MALLOC(stCipher.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stPlain.pBuff, WSEC_LOG_E4MALLOC(stPlain.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);

        nErrCode = SDP_DecryptInit(ulDomain, &stSdpCtx, &stBodCipherHeader);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_DecryptInit()=%u", nErrCode), oper_null);

        while (WSEC_ReadTlv(fRead, stCipher.pBuff, stCipher.nLen, &stTlv, &nErrCode))
        {
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_ReadTlv()=%u", nErrCode), oper_null);
            nReadSize += stTlv.ulLen; /* �Ѿ��������ĳ��� */

            if (SDP_CFT_HMAC_VAL == stTlv.ulTag)
            {
                WSEC_BUFF_ASSIGN(stHmac, stTlv.pVal, stTlv.ulLen);
                break;
            }
            break_oper_if(stTlv.ulTag != SDP_CFT_CIPHER_BODY, WSEC_LOG_E2("Unexpected TLV's Tag(%u) in %s", stTlv.ulTag, pszCipherFile), nErrCode = WSEC_ERR_FILE_FORMAT);

            /* �������Ķ� */
            stPlain.nLen = stFileHdr.ulPlainBlockLenMax;
            nErrCode = SDP_DecryptUpdate(&stSdpCtx, stTlv.pVal, stTlv.ulLen, stPlain.pBuff, &stPlain.nLen);

            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_DecryptUpdate()=%u", nErrCode), oper_null);
            break_oper_if(!WSEC_FWRITE_MUST(stPlain.pBuff, stPlain.nLen, fWri), WSEC_LOG_E1("Write '%s' fail.", pszPlainFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
            WSEC_FFLUSH(fWri);

            /* �����ϱ� */
            WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)ulSrcFileLen, (WSEC_UINT32)nReadSize, &bCancel);
            if (bCancel) {break;}
        }
        if ((!bCancel)) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, (WSEC_UINT32)ulSrcFileLen, (WSEC_UINT32)ulSrcFileLen, WSEC_NULL_PTR);} /* ȷ��������100% */
        
        stPlain.nLen = stFileHdr.ulPlainBlockLenMax;
        nTemp = SDP_DecryptFinal(&stSdpCtx, stHmac.pBuff, stHmac.nLen, stPlain.pBuff, &stPlain.nLen);
        break_oper_if(bCancel, WSEC_LOG_E("Decrypt progress canceled."), nErrCode = WSEC_ERR_CANCEL_BY_APP);
        break_oper_if(nTemp != WSEC_SUCCESS, WSEC_LOG_E1("SDP_DecryptFinal()=%u", nTemp), nErrCode = nTemp);

        if (nErrCode != WSEC_SUCCESS) {break;}
        if (0 == stPlain.nLen) {break;}

        break_oper_if(!WSEC_FWRITE_MUST(stPlain.pBuff, stPlain.nLen, fWri), WSEC_LOG_E1("Write '%s' fail.", pszPlainFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
    }do_end;

    WSEC_FCLOSE(fRead);
    WSEC_FCLOSE(fWri);
    WSEC_BUFF_FREE(stPlain);
    WSEC_BUFF_FREE(stCipher);
    SDP_FreeCtx(&stSdpCtx);

    if (WSEC_SUCCESS == nErrCode) /* ���黹ԭ�����ļ���������Ϣ */
    {
        if ((pfSetFileDateTime) && WSEC_IsDateTime(&stFileHdr.tSrcFileCreateTime) && WSEC_IsDateTime(&stFileHdr.tSrcFileEditTime))
        {
            pfSetFileDateTime(pszPlainFile, &stFileHdr.tSrcFileCreateTime, &stFileHdr.tSrcFileEditTime);
        }
    }
    else
    {
        /* Misinformation: FORTIFY.Race_Condition--File_System_Access */
        WSEC_UNCARE(WSEC_FREMOVE(pszPlainFile));
    }
    
    return nErrCode;
}


/*****************************************************************************
> �� �� ��  : SDP_GetHmacLen
> ��������  : ����HMAC���ģ�hmac-data�����ȡ�
> �������  : ��
> �������  : ��
> �� �� ֵ  : ���ܺ�HMAC���ĵĳ��ȣ�����HMACͷ���ĳ��ȡ�
> ע������  : ��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���
>  2.��    ��   : 2015��7��31��
>    ��    ��   : z00118096
>    �޸�����   : �ϰ汾�ĸýӿ��޷������������: 
         1) �߳�P1���ô˺�����ȡHMAC����(������ʱ���㷨����ΪHMAC256);
         2) �߳�P2����KMC_SetDataProtectCfg�������㷨����ΪHMAC512;
         3) P1����SDP_Hmac, ����APP����1)ʱ�ṩ��HMAC�ռ��С, �����
            �ڴ�Խ��дHMAC, �������޷���֪.

         �޸�����: ʹ������HMAC�����������ĳ���, ���㷨�޹�.
*****************************************************************************/
WSEC_ERR_T SDP_GetHmacLen(WSEC_UINT32* pulHmacLen)
{
    return_err_if_para_invalid("SDP_GetHmacLen", pulHmacLen);

    *pulHmacLen = SDP_HMAC_HEAD_LEN + SDP_HMAC_MAX_SIZE;

    return WSEC_SUCCESS;
}

/*****************************************************************************
> �� �� ��  : SDP_Hmac
> ��������  : �������ݼ���Hmac�ӿڡ�
> ���Ľṹ  : hmac-head | hmac-data
> �������  : ulDomain ��ʾ������ID
>             pucPlainText ��ʾ��Ҫ����HMAC�������ڴ��
>             ulPTLen ��ʾ��Ҫ����HMAC�������ڴ��ĳ���
>             pulHTLen ��ʾ��Ҫ����HMAC���ڴ��ķ��䳤��
> �������  : pucHmacText ��ʾ��Ҫ����HMAC���ڴ�飬����HMACͷ��
>             pulHTLen ��ʾ��Ҫ����HMAC���ڴ���ʵ�ʳ��ȣ�����HMACͷ���ĳ���
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ�HMAC�Ѿ����
> ��������  : ��
> ��������  : ������WSEC_SUCCESSʱ��*pulHTLen> 0
> ע������  : pucHmacTextָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetHmacLen��ȡ��
>             pulHTLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_Hmac(
      WSEC_UINT32   ulDomain,
      const WSEC_BYTE *pucPlainText,
      WSEC_UINT32   ulPTLen,
      WSEC_BYTE    *pucHmacText,
      INOUT WSEC_UINT32   *pulHTLen
    )
{
    SDP_HMAC_HEAD_STRU          *pstHmacHead = WSEC_NULL_PTR;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN];
    WSEC_ERR_T  ulRet       = WSEC_SUCCESS;
    WSEC_UINT32 ulKeyLen    = 0;
    WSEC_UINT32 ulTempHTLen = 0;
    WSEC_UINT32 ulHTMaxLen  = 0;

    /* check input */
    return_err_if_para_invalid("SDP_Hmac", pucPlainText && pucHmacText && pulHTLen && (pucPlainText != pucHmacText));

    ulHTMaxLen = *pulHTLen;
    ulTempHTLen = ulHTMaxLen;

    /* construct header, get MK-derived work key */
    return_oper_if((ulHTMaxLen <= SDP_HMAC_HEAD_LEN), WSEC_LOG_E("[SDP] Buffer for hmac text is not enough."), WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);
    WSEC_MEMSET(pucHmacText, ulHTMaxLen, 0, SDP_HMAC_HEAD_LEN);
    pstHmacHead = (SDP_HMAC_HEAD_STRU *)pucHmacText;
    ulRet = SDP_FillHmacTextHeader(SDP_ALG_INTEGRITY, ulDomain, pstHmacHead, aucKey, &ulKeyLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Fill HMAC text header failed."), ulRet);

    /* HMAC calculation */
    ulRet = CAC_Hmac(pstHmacHead->ulAlgId, aucKey, ulKeyLen, pucPlainText, ulPTLen,
                     pucHmacText + SDP_HMAC_HEAD_LEN, /* suggest INOUT */ &ulTempHTLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC calculate hmac failed."), WSEC_ERR_HMAC_FAIL);

    /* destroy key */
    WSEC_DESTROY_KEY(aucKey, ulKeyLen);

    /* header not filled with real HMAC length, allocate fixed max space for HMAC */
    /* compute HMAC length, including header length */
    ulTempHTLen += SDP_HMAC_HEAD_LEN;

    /* byte order process */
    SDP_CvtByteOrder4HmacTextHeader(pstHmacHead, wbcHost2Network);

    /* output param */
    *pulHTLen = ulTempHTLen;

    return WSEC_SUCCESS;
}

/*****************************************************************************
> �� �� ��  : SDP_VerifyHmac
> ��������  : HMACУ��ӿڡ��ṩУ���������HMAC�Ĺ��ܡ�
>             ͨ��HMACͷ����ȡУ��������㷨��Salt����ԿID��У��HMAC�Ƿ���ȷ��
> ���Ľṹ  : hmac-head | hmac-data
> �������  : pucPlainText ��ʾ��ҪУ��HMAC���Ķ�Ӧ�������ڴ��
>             ulPTLen ��ʾ��ҪУ��HMAC���Ķ�Ӧ�������ڴ��ĳ���
>             pucHmacText ��ʾ��ҪУ���HMAC���ģ��������ݱ�����HMACͷ��
>             ulHTLen ��ʾ��ҪУ���HMAC���ĵĳ���
> �������  : ��
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ�HMAC����У������ȷ��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_VerifyHmac(
      WSEC_UINT32       ulDomain,
      const WSEC_BYTE  *pucPlainText,
      WSEC_UINT32       ulPTLen,
      const WSEC_BYTE  *pucHmacText,
      WSEC_UINT32       ulHTLen
    )
{
    SDP_HMAC_HEAD_STRU          *pstHmacHead = WSEC_NULL_PTR;
    SDP_ERROR_CTX_STRU          stErrCtx;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN];
    WSEC_BYTE aucTempPTMac[SDP_PTMAC_MAX_LEN];
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_UINT32 ulTempPTMacLen  = 0;
    WSEC_UINT32 ulKeyLen        = 0;

    /* check input */
    return_err_if_para_invalid("SDP_VerifyHmac", pucHmacText && pucPlainText && (ulHTLen > 0) && (pucPlainText != pucHmacText));

    return_oper_if((ulHTLen <= SDP_HMAC_HEAD_LEN), WSEC_LOG_E("[SDP] Invalid parameter. Buffer for hmac text is not enough."), WSEC_ERR_INPUT_BUFF_NOT_ENOUGH);
    return_oper_if((ulHTLen > SDP_HMAC_HEAD_LEN + SDP_PTMAC_MAX_LEN), WSEC_LOG_E("[SDP] Invalid parameter. Buffer for hmac text is out of bounds."), WSEC_ERR_INVALID_ARG);

    /* parse header */
    pstHmacHead = (SDP_HMAC_HEAD_STRU *)pucHmacText;

    /* byte order process */
    SDP_CvtByteOrder4HmacTextHeader(pstHmacHead, wbcNetwork2Host);

    /* check format */
    init_error_ctx(stErrCtx);
    update_error_ctx(stErrCtx, (SDP_HMAC_VER != pstHmacHead->ulVersion),
                   WSEC_LOG_E2("[SDP] Cipher text version is incompatible, %d expected, %d actually.", SDP_HMAC_VER, pstHmacHead->ulVersion), 
                   WSEC_ERR_SDP_VERSION_INCOMPATIBLE);    
    update_error_ctx(stErrCtx, (ulDomain != pstHmacHead->ulDomain), 
                   WSEC_LOG_E1("[SDP] Cipher text are marked with an unexpected domain %d.", pstHmacHead->ulDomain), 
                   WSEC_ERR_SDP_DOMAIN_UNEXPECTED);
    update_error_ctx(stErrCtx, (!KMC_IS_KEYITERATIONS_VALID(pstHmacHead->ulIterCount)), 
                   WSEC_LOG_E("[SDP] Iterator count is out of bounds."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    update_error_ctx(stErrCtx, (WSEC_ALGTYPE_UNKNOWN == CAC_AlgId2Type(pstHmacHead->ulAlgId)), 
                   WSEC_LOG_E("[SDP] CAC Get algorithm types failed."), 
                   WSEC_ERR_SDP_ALG_NOT_SUPPORTED);
    return_oper_if((stErrCtx.ulErrCount > SDP_MAX_ERR_FIELD_OF_CIPHER), WSEC_LOG_E("[SDP] Cipher text format is invalid."), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    return_oper_if((stErrCtx.ulErrCount > 0), oper_null, stErrCtx.ulLastErrCode);

    /* Get key length, MAC length */
    ulRet = SDP_GetAlgProperty(pstHmacHead->ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, &ulKeyLen, WSEC_NULL_PTR, WSEC_NULL_PTR, &ulTempPTMacLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_HMAC != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    return_oper_if((ulHTLen < SDP_HMAC_HEAD_LEN + ulTempPTMacLen), WSEC_LOG_E("[SDP] Invalid parameter. Buffer for hmac text is not enough."), WSEC_ERR_INPUT_BUFF_NOT_ENOUGH);

    /* Get WK (work key) By KeyID */
    ulRet = SDP_GetWorkKeyByID(pstHmacHead->ulDomain, pstHmacHead->ulKeyId, pstHmacHead->ulIterCount,
                               pstHmacHead->aucSalt, SDP_SALT_LEN, aucKey, ulKeyLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get WK by KeyID failed."), ulRet);

    /* HMAC calculation */
    ulRet = CAC_Hmac(pstHmacHead->ulAlgId, aucKey, ulKeyLen, pucPlainText, ulPTLen,
                     aucTempPTMac, &ulTempPTMacLen);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC calculate hmac failed."), WSEC_ERR_HMAC_FAIL);

    /* destroy key */
    WSEC_DESTROY_KEY(aucKey, ulKeyLen);

    /* verify HMAC */
    return_oper_if(WSEC_MEMCMP(aucTempPTMac, pucHmacText + SDP_HMAC_HEAD_LEN, ulTempPTMacLen) != 0, 
                   WSEC_LOG_E("[SDP] HMAC failed to pass the verification."), WSEC_ERR_HMAC_AUTH_FAIL);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : SDP_GetHmacAlgAttr
 ��������  : ��ȡHMAC�㷨��Ϣ
 �� �� ��  : ulDomain: ��HMAC������������������
 �� �� ��  : pstHmacAlgAttr: �ش�HMAC�㷨������Ϣ
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_GetHmacAlgAttr(WSEC_UINT32 ulDomain, SDP_HMAC_ALG_ATTR *pstHmacAlgAttr)
{
    SDP_HMAC_HEAD_STRU* pHdr = WSEC_NULL_PTR;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN] = {0};
    WSEC_UINT32 ulKeyLen = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("SDP_GetHmacAlgAttr", pstHmacAlgAttr);
    pHdr = (SDP_HMAC_HEAD_STRU*)pstHmacAlgAttr->abyBuffer;

    nErrCode = SDP_FillHmacTextHeader(SDP_ALG_INTEGRITY, ulDomain, pHdr, aucKey, &ulKeyLen);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_FillHmacTextHeader() = %u", nErrCode), nErrCode);
    
    SDP_CvtByteOrder4HmacTextHeader(pHdr, wbcHost2Network);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : SDP_HmacInit
 ��������  : �Դ�����, ���߷ֶ����ݽ���HMAC�ĳ�ʼ���ڡ�
 �� �� ��  : ulDomain: ��HMAC������������������
             pstHmacAlgAttr: HMAC�㷨������Ϣ
 �� �� ��  : pstSdpCtx: HMAC�ֶμ���ʹ�õ�������.
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_HmacInit(WSEC_UINT32 ulDomain, const SDP_HMAC_ALG_ATTR *pstHmacAlgAttr, SDP_CRYPT_CTX *pstSdpCtx)
{
    SDP_HMAC_HEAD_STRU* pstHdr = WSEC_NULL_PTR;
    SDP_CRYPT_CTX_STRU *pstSdpCtxObj = WSEC_NULL_PTR;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;
    WSEC_BYTE aucKey[SDP_KEY_MAX_LEN];
    WSEC_UINT32 ulTempPTMacLen  = 0;
    WSEC_UINT32 ulKeyLen        = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("SDP_HmacInit", pstHmacAlgAttr && pstSdpCtx);

    pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)WSEC_MALLOC(SDP_CRYPT_CTX_LEN);
    return_oper_if(!pstSdpCtxObj, WSEC_LOG_E4MALLOC(SDP_CRYPT_CTX_LEN), WSEC_ERR_MALLOC_FAIL);

    *pstSdpCtx = pstSdpCtxObj;
    pstSdpCtxObj->eAlgType = WSEC_ALGTYPE_HMAC;
    pstHdr = &pstSdpCtxObj->stHmacHead;

    do
    {
        break_oper_if(WSEC_MEMCPY(pstHdr, sizeof(SDP_HMAC_HEAD_STRU), pstHmacAlgAttr, sizeof(SDP_HMAC_HEAD_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);

        SDP_CvtByteOrder4HmacTextHeader(pstHdr, wbcNetwork2Host);
        
        /* ������� */
        break_oper_if(pstHdr->ulVersion != SDP_HMAC_VER, WSEC_LOG_E2("HMAC version is incompatible, %d expected, %d actually.", SDP_HMAC_VER, pstHdr->ulVersion), nErrCode = WSEC_ERR_SDP_VERSION_INCOMPATIBLE);
        break_oper_if((ulDomain != pstHdr->ulDomain), WSEC_LOG_E1("Cipher text are marked with an unexpected domain %d.", pstHdr->ulDomain), nErrCode = WSEC_ERR_SDP_DOMAIN_UNEXPECTED);
        break_oper_if(!KMC_IS_KEYITERATIONS_VALID(pstHdr->ulIterCount), WSEC_LOG_E("Iterator count is out of bounds."), nErrCode = WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
        break_oper_if(CAC_AlgId2Type(pstHdr->ulAlgId) == WSEC_ALGTYPE_UNKNOWN, WSEC_LOG_E("CAC Get algorithm types failed."), nErrCode = WSEC_ERR_SDP_ALG_NOT_SUPPORTED);

        /* ����HMAC��Կ */
        nErrCode = SDP_GetAlgProperty(pstHdr->ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, &ulKeyLen, WSEC_NULL_PTR, WSEC_NULL_PTR, &ulTempPTMacLen);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_GetAlgProperty() = %u.", nErrCode), oper_null);
        break_oper_if(eAlgType != WSEC_ALGTYPE_HMAC, WSEC_LOG_E1("AlgType(%d) cannot support HMAC.", eAlgType), nErrCode = WSEC_ERR_SDP_INVALID_CIPHER_TEXT);

        /* Get WK (work key) By KeyID */
        nErrCode = SDP_GetWorkKeyByID(pstHdr->ulDomain, pstHdr->ulKeyId, pstHdr->ulIterCount, pstHdr->aucSalt, SDP_SALT_LEN, aucKey, ulKeyLen);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_GetWorkKeyByID() = %u", nErrCode), oper_null);

        /* HMAC calculation init */
        nErrCode = CAC_HmacInit(&(pstSdpCtxObj->stWsecCtx), pstHdr->ulAlgId, aucKey, ulKeyLen);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("CAC_HmacInit() = %u", nErrCode), oper_null);
    }do_end;

    if (nErrCode != WSEC_SUCCESS) {SDP_FreeCtx(pstSdpCtx);}

    /* destroy key */
    WSEC_DESTROY_KEY(aucKey, ulKeyLen);

    /* Misinformation: FORTIFY.Memory_Leak ('pstSdpCtxObj' output by 'pstSdpCtx' when succ.) */

    return nErrCode;
}


/*****************************************************************************
> �� �� ��  : SDP_HmacUpdate
> ��������  : ���������ݼ���HMAC���½ӿڡ������ѽ��������ݱ��������ĶԴ�����ݽ��зֶθ���HMAC��
>             ���ִ�иú�������ɸ������ݶε�HMAC���¹��̡����������ݵķֶ��ɵ�������ɣ�
>             �ú���ֻ�����Ѿ����ֺõ����ݶ���Ϊ������
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_HmacInit�����
>             pucPlainText ��ʾ��Ҫ����HMAC�ķֶ������ڴ�飻
>             ulPTLen ��ʾ��Ҫ����HMAC�ķֶ������ڴ��ĳ��ȣ�
> �������  : ��
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������ء�
> ��������  : (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
>             && (WSEC_NULL_PTR != pucPlainText) && (0 != ulPTLen)
> ��������  : ��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���
>  2.��    ��   : 2014��11��6��
>    ��    ��   : j00265291
>    �޸�����   : �޸ĺ��������������ʧ��ʱû�е���Final��ɵ��ڴ�й¶

*****************************************************************************/
WSEC_ERR_T SDP_HmacUpdate(
      const SDP_CRYPT_CTX *pstSdpCtx,
      const WSEC_BYTE *pucPlainText,
      WSEC_UINT32 ulPTLen
    )
{
    SDP_CRYPT_CTX_STRU          *pstSdpCtxObj = WSEC_NULL_PTR;
    WSEC_ERR_T ulRet = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("SDP_HmacUpdate", pstSdpCtx && *pstSdpCtx && pucPlainText && (ulPTLen > 0));

    do 
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);

        /* HMAC calculation update */
        ulRet = CAC_HmacUpdate(pstSdpCtxObj->stWsecCtx, pucPlainText, ulPTLen);
        if (ulRet!= WSEC_SUCCESS)
        {
            ulRet = WSEC_ERR_HMAC_FAIL;
            WSEC_LOG_E("[SDP] CAC calculate hmac update failed.");
            break;
        }

        ulRet = WSEC_SUCCESS;
    } do_end;

    if (ulRet != WSEC_SUCCESS) { SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx); }

    return ulRet;
}

/*****************************************************************************
> �� �� ��  : SDP_HmacFinal
> ��������  : ���������ݼ���HMAC�����ӿڡ������ѽ��������ݱ��������ģ�������һ�����ݶε�HMAC���£�
>             ����������������ݵ�HMAC��
> �������  : pstSdpCtx ��ʾ�ѽ��������ݱ��������ģ�������SDP_HmacInit�����
>             pulHDLen ��ʾ��Ҫ���ɼ���õ�HMAC���ڴ��ķ��䳤�ȣ�
> �������  : pucHmacData ��ʾ��Ҫ���ɼ���õ�HMAC���ڴ�飻
>             pulHDLen ��ʾ��Ҫ���ɼ���õ�HMAC���ڴ���ʵ�ʳ��ȣ�
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ�HMAC�Ѿ������
> ��������  : (WSEC_NULL_PTR != pstSdpCtx) && (WSEC_NULL_PTR != *pstSdpCtx)
>             && (WSEC_NULL_PTR != pucHmacData) && (WSEC_NULL_PTR != pulHDLen)
> ��������  : ������WSEC_SUCCESSʱ��*pulHDLen> 0
> ע������  : pucHmacDataָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetHmacLen��ȡ��
>             pulHDLen�ɵ����߷����ڴ档

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

>  2.��    ��   : 2015��3��20��
>    ��    ��   : z00118096
>    �޸�����   : ���Ӷ�HMAC����������ļ��
*****************************************************************************/
WSEC_ERR_T SDP_HmacFinal(
      const SDP_CRYPT_CTX *pstSdpCtx,
      WSEC_BYTE    *pucHmacData,
      INOUT WSEC_UINT32   *pulHDLen
    )
{
    SDP_CRYPT_CTX_STRU *pstSdpCtxObj = WSEC_NULL_PTR;
    SDP_HMAC_HEAD_STRU* pstHdr = WSEC_NULL_PTR;
    WSEC_ERR_T ulRet = WSEC_SUCCESS;
    WSEC_UINT32 ulTempHDLen    = 0;
    WSEC_UINT32 ulHDMaxLen     = 0;
    WSEC_UINT32 ulNeedHmacSize = 0;

    /* check input */
    return_err_if_para_invalid("SDP_HmacFinal", pstSdpCtx && (*pstSdpCtx) && pucHmacData && pulHDLen);

    ulHDMaxLen = *pulHDLen;
    ulTempHDLen = ulHDMaxLen;

    do
    {
        /* construct context */
        pstSdpCtxObj = (SDP_CRYPT_CTX_STRU *)(*pstSdpCtx);

        /* ���HMAC����������Ƿ��㹻 */
        pstHdr = &pstSdpCtxObj->stHmacHead;
        ulNeedHmacSize = CAC_HMAC_Size(pstHdr->ulAlgId);

        break_oper_if(ulHDMaxLen < ulNeedHmacSize, WSEC_LOG_E2("HMAC buffer too small(%d), need %d", ulHDMaxLen, ulNeedHmacSize), ulRet = WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);
        ulRet = CAC_HmacFinal(&(pstSdpCtxObj->stWsecCtx), pucHmacData, &ulTempHDLen);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("[SDP] CAC calculate hmac final failed."), ulRet = WSEC_ERR_HMAC_FAIL);

        /* output param */
        *pulHDLen = ulTempHDLen;

        ulRet = WSEC_SUCCESS;
    } do_end;

    SDP_FreeCtx((SDP_CRYPT_CTX*)pstSdpCtx);

    return ulRet;
}

/*****************************************************************************
 �� �� ��  : SDP_HmacCancel
 ��������  : ȡ���ֶ�HMAC����ĺ�������.
             �����ķֶ�HMAC���������: Init ---> Update ---> Final. ������м�����
                                              ��________|
                                                   
             ����ȡ�������Ĵ���, ����Ҫ���ô˺���.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pstSdpCtx: [in]ָ��������Ϣ��ָ��, [out]ָ������ΪNULL
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��5��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID SDP_HmacCancel(SDP_CRYPT_CTX *pstSdpCtx)
{
    SDP_FreeCtx(pstSdpCtx);
}

/*****************************************************************************
 �� �� ��  : SDP_FileHmac
 ��������  : ���ļ�����HMAC����.
 �� �� ��  : ulDomain: ��HMAC�����ļ���������������
             pszFile:  �������ļ�
             pstRptProgress: �����ϱ��ص�����[�ɿ�]
             pstHmacAlgAttr: HMAC�㷨������Ϣ
 �� �� ��  : pvHmacData: ���HMAC(�������ṩ�ռ�)
 ��γ���  : pulHDLen: [in]pvHmacData�������������; [out]HMACʵ�ʳ���
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_FileHmac(WSEC_UINT32 ulDomain, const WSEC_CHAR* pszFile, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const SDP_HMAC_ALG_ATTR* pstHmacAlgAttr, WSEC_VOID* pvHmacData, WSEC_UINT32* pulHDLen)
{
    SDP_CRYPT_CTX stSdpCtx = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_FILE_LEN nFileLen = 0, nReadLen = 0, nReadLenStat = 0;
    WSEC_VOID *pvReadBuff = WSEC_NULL_PTR;
    WSEC_FILE  fRead;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE, bNeedHmacFinal = WSEC_FALSE;
    
    return_err_if_para_invalid("SDP_FileHmac", pszFile && pstHmacAlgAttr && pvHmacData && pulHDLen);
    return_err_if_para_invalid("SDP_FileHmac", WSEC_NOT_IS_EMPTY_STRING(pszFile));
    return_oper_if(!WSEC_GetFileLen(pszFile, &nFileLen), WSEC_LOG_E1("Cannot access '%s'", pszFile), WSEC_ERR_OPEN_FILE_FAIL);

    fRead = WSEC_FOPEN(pszFile, "rb");
    return_oper_if(!fRead, WSEC_LOG_E1("Cannot open '%s'", pszFile), WSEC_ERR_OPEN_FILE_FAIL);

    pvReadBuff = WSEC_MALLOC(WSEC_FILE_IO_SIZE_MAX);

    do
    {
        break_oper_if(!pvReadBuff, WSEC_LOG_E4MALLOC(WSEC_FILE_IO_SIZE_MAX), nErrCode = WSEC_ERR_MALLOC_FAIL);
        
        nErrCode = SDP_HmacInit(ulDomain, pstHmacAlgAttr, &stSdpCtx);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_HmacInit() = %u", nErrCode), oper_null);

        bNeedHmacFinal = WSEC_TRUE;
        while (!WSEC_FEOF(fRead))
        {
            nReadLen = WSEC_FREAD(pvReadBuff, 1, WSEC_FILE_IO_SIZE_MAX, fRead);
            continue_if(nReadLen <= 0);

            nReadLenStat += nReadLen;

            nErrCode = SDP_HmacUpdate(&stSdpCtx, pvReadBuff, (WSEC_UINT32)nReadLen);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_HmacUpdate() = %u", nErrCode), bNeedHmacFinal = WSEC_FALSE);

            WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)nFileLen, (WSEC_UINT32)nReadLenStat, &bCancel);
            break_oper_if(bCancel, WSEC_LOG_E("App canceled."), nErrCode = WSEC_ERR_CANCEL_BY_APP);
        }

        if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, (WSEC_UINT32)nFileLen, (WSEC_UINT32)nReadLenStat, WSEC_NULL_PTR);}
        if (bNeedHmacFinal) {nErrCode = SDP_HmacFinal(&stSdpCtx, (WSEC_BYTE*)pvHmacData, pulHDLen);}
    }do_end;

    SDP_FreeCtx(&stSdpCtx);

    WSEC_FCLOSE(fRead);
    WSEC_FREE(pvReadBuff);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : SDP_VerifyFileHmac
 ��������  : ���ļ�����HMAC��֤.
 �� �� ��  : ulDomain: ��HMAC�����ļ���������������
             pszFile:  �������ļ�
             pstRptProgress: �����ϱ��ص�����[�ɿ�]
             pstHmacAlgAttr: HMAC�㷨������Ϣ
             pvHmacData: ���ļ�ԭHMACֵ
             ulHDLen:    ���ļ�ԭHMAC����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��2��15��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T SDP_VerifyFileHmac(WSEC_UINT32 ulDomain, const WSEC_CHAR *pszFile, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const SDP_HMAC_ALG_ATTR* pstHmacAlgAttr, const WSEC_VOID *pvHmacData, WSEC_UINT32 ulHDLen)
{
    WSEC_BUFF stHmacNew = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("SDP_VerifyFileHmac", pszFile && pstHmacAlgAttr && pvHmacData && (ulHDLen > 0));
    WSEC_BUFF_ALLOC(stHmacNew, WSEC_HMAC_LEN_MAX);
    return_oper_if(!stHmacNew.pBuff, WSEC_LOG_E4MALLOC(stHmacNew.nLen), WSEC_ERR_MALLOC_FAIL);

    do
    {
        nErrCode = SDP_FileHmac(ulDomain, pszFile, pstRptProgress, pstHmacAlgAttr, stHmacNew.pBuff, &stHmacNew.nLen);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("SDP_FileHmac() = %u", nErrCode), oper_null);

        break_oper_if(ulHDLen != stHmacNew.nLen, oper_null, nErrCode = WSEC_ERR_HMAC_AUTH_FAIL);
        break_oper_if(WSEC_MEMCMP(pvHmacData, stHmacNew.pBuff, ulHDLen) != 0, oper_null, nErrCode = WSEC_ERR_HMAC_AUTH_FAIL);
    }do_end;

    WSEC_BUFF_FREE(stHmacNew);
    return nErrCode;
}

/*****************************************************************************
> �� �� ��  : SDP_GetPwdCipherLen
> ��������  : ����ָ���ļ��ܺ����ݳ��ȣ������������ȫ�����ȡ�
> �������  : ulPwdLen Password����
> �������  : ��
> �� �� ֵ  : �������ĵĳ��ȣ�������������ͷ����
> ע������  : �ú���������SDP_ProtectPwd��������ǰ���п������ĳ��ȼ���Ϳ��������ڴ���䡣

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_SIZE_T SDP_GetPwdCipherLen(WSEC_SIZE_T ulPwdLen)
{
    /* sum of length */
    return (WSEC_SIZE_T)(SDP_PWD_HEAD_LEN + ulPwdLen);
}


/*****************************************************************************
> �� �� ��  : SDP_ProtectPwd
> ��������  : �������ܽӿڡ��ṩ�������ܹ��ܣ�
> ���Ľṹ  : pwd-cipher-head | pwd-cipher-data
> �������  : pucPlainText ��ʾ��Ҫ������ܵ����Ŀ���
>             ulPTLen ��ʾ��Ҫ������ܵ����Ŀ���ĳ���
>             ulCTLen ��ʾ���ɵĿ������ĵ���󳤶ȣ�������������ͷ�����ȡ�
> �������  : pucCipherText ��ʾ��Ҫ���ɵ�����ܺ�������ĵ��ڴ�飬������������ͷ��
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ����������Ѿ������
> ע������  : pucCipherTextָ����ڴ���ɵ����߷�����ͷţ��ڴ��Ĵ�С����ʹ��SDP_GetPwdCipherLen��ȡ��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_ProtectPwd(
      const WSEC_BYTE *pucPlainText,
      WSEC_UINT32 ulPTLen,
      WSEC_BYTE *pucCipherText,
      WSEC_UINT32 ulCTLen
    )
{
    SDP_PWD_HEAD_STRU *pstPwdHead = WSEC_NULL_PTR;
    WSEC_ERR_T  ulRet = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("SDP_ProtectPwd", pucPlainText && pucCipherText && (ulCTLen >= SDP_PWD_HEAD_LEN) && (pucPlainText != pucCipherText));

    /* construct header, get MK-derived work key */
    WSEC_MEMSET(pucCipherText, ulCTLen, 0, SDP_PWD_HEAD_LEN);
    pstPwdHead = (SDP_PWD_HEAD_STRU *)pucCipherText;
    ulRet = SDP_FillPwdCipherTextHeader(SDP_ALG_PWD_PROTECT, pstPwdHead);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Fill password cipher text header failed."), ulRet);

    /* update header with real cipher length */
    pstPwdHead->ulCDLen = ulCTLen - SDP_PWD_HEAD_LEN;

    /* password encryption */
    ulRet = CAC_Pbkdf2(pstPwdHead->ulAlgId, (WSEC_BYTE *)pucPlainText, ulPTLen,
                       pstPwdHead->aucSalt, SDP_SALT_LEN,
                       pstPwdHead->ulIterCount, pstPwdHead->ulCDLen, pucCipherText + SDP_PWD_HEAD_LEN);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] CAC pbkdf2 encrypt password failed."), WSEC_ERR_PBKDF2_FAIL);

    /* byte order process */
    SDP_CvtByteOrder4PwdCipherTextHeader(pstPwdHead, wbcHost2Network);

    return WSEC_SUCCESS;
}

/*****************************************************************************
> �� �� ��  : SDP_VerifyPwd
> ��������  : ��������У��ӿڡ��ṩУ��������ĵĹ���
> �������  : pucPlainText ��ʾ��ҪУ��������Ķ�Ӧ�����Ŀ���
>             ulPTLen ��ʾ��ҪУ��������Ķ�Ӧ�����Ŀ���ĳ���
>             pucCipherText ��ʾ��ҪУ��Ŀ������ģ��������ݱ����Ŀ�������ͷ��
>             ulCTLen ��ʾ��ҪУ��Ŀ������ĵĳ���
> �������  : ��
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������أ���������У������ȷ��

> �޸���ʷ      :
>  1.��    ��   : 2014��7��2��
>    ��    ��   : x00102361, j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T SDP_VerifyPwd(
      const WSEC_BYTE *pucPlainText,
      WSEC_UINT32 ulPTLen,
      const WSEC_BYTE *pucCipherText,
      WSEC_UINT32 ulCTLen
    )
{
    SDP_PWD_HEAD_STRU           *pstPwdHead = WSEC_NULL_PTR;
    SDP_ERROR_CTX_STRU          stErrCtx;
    WSEC_ERR_T  ulRet           = WSEC_SUCCESS;
    WSEC_BYTE *pTempCipherData = WSEC_NULL_PTR;
    WSEC_ALGTYPE_E eAlgType = WSEC_ALGTYPE_UNKNOWN;

    /* check input */
    return_err_if_para_invalid("SDP_VerifyPwd", pucPlainText && pucCipherText && (ulCTLen >= SDP_PWD_HEAD_LEN) && (pucPlainText != pucCipherText));

    /* check header */
    pstPwdHead = (SDP_PWD_HEAD_STRU *)pucCipherText;

    /* byte order process */
    SDP_CvtByteOrder4PwdCipherTextHeader(pstPwdHead, wbcNetwork2Host);

    /* check format */
    init_error_ctx(stErrCtx);
    update_error_ctx(stErrCtx, (SDP_PWD_CIPHER_VER != pstPwdHead->ulVersion),
                   WSEC_LOG_E2("[SDP] Cipher text version is incompatible, %d expected, %d actually.", SDP_PWD_CIPHER_VER, pstPwdHead->ulVersion), 
                   WSEC_ERR_SDP_VERSION_INCOMPATIBLE);    
/*    update_error_ctx(stErrCtx, (0 == pstPwdHead->ulCDLen), 
                   WSEC_LOG_E("[SDP] Cipher data length cannot be 0."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT); */
    update_error_ctx(stErrCtx, (!KMC_IS_KEYITERATIONS_VALID(pstPwdHead->ulIterCount)), 
                   WSEC_LOG_E("[SDP] Iterator count is out of bounds."),
                   WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    update_error_ctx(stErrCtx, (WSEC_ALGTYPE_UNKNOWN == CAC_AlgId2Type(pstPwdHead->ulAlgId)), 
                   WSEC_LOG_E("[SDP] CAC Get algorithm types failed."), 
                   WSEC_ERR_SDP_ALG_NOT_SUPPORTED);
    return_oper_if((stErrCtx.ulErrCount > SDP_MAX_ERR_FIELD_OF_CIPHER), WSEC_LOG_E("[SDP] Cipher text format is invalid."), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);
    return_oper_if((stErrCtx.ulErrCount > 0), oper_null, stErrCtx.ulLastErrCode);

    return_oper_if((ulCTLen < SDP_PWD_HEAD_LEN + pstPwdHead->ulCDLen), WSEC_LOG_E("[SDP] Invalid parameter. Buffer for cipher text is not enough."), WSEC_ERR_INPUT_BUFF_NOT_ENOUGH);

    /* Get algorithm property */
    ulRet = SDP_GetAlgProperty(pstPwdHead->ulAlgId, WSEC_NULL_PTR, 0, &eAlgType, WSEC_NULL_PTR, WSEC_NULL_PTR, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if((ulRet!= WSEC_SUCCESS), WSEC_LOG_E("[SDP] Get algorithm property failed."), ulRet);
    return_oper_if((WSEC_ALGTYPE_PBKDF != eAlgType), WSEC_LOG_E1("[SDP] AlgType(%d) is out of bounds.", eAlgType), WSEC_ERR_SDP_INVALID_CIPHER_TEXT);

    pTempCipherData = (WSEC_BYTE*)WSEC_MALLOC(pstPwdHead->ulCDLen > 0 ? pstPwdHead->ulCDLen : 32); /* ���0������ */
    return_oper_if((WSEC_NULL_PTR == pTempCipherData), WSEC_LOG_E("[SDP] Memory for cipher data allocate failed."), WSEC_ERR_MALLOC_FAIL);

    do
    {
        /* password encryption */
        ulRet = CAC_Pbkdf2(pstPwdHead->ulAlgId, (WSEC_BYTE *)pucPlainText, ulPTLen,
                           pstPwdHead->aucSalt, SDP_SALT_LEN, pstPwdHead->ulIterCount, pstPwdHead->ulCDLen, pTempCipherData);
        if (ulRet!= WSEC_SUCCESS)
        {
            ulRet = WSEC_ERR_PBKDF2_FAIL;
            WSEC_LOG_E("[SDP] CAC pbkdf2 encrypt password failed.");
            break;
        }

        /* verify cipher text */
        if (0 != WSEC_MEMCMP(pTempCipherData, pucCipherText + SDP_PWD_HEAD_LEN, pstPwdHead->ulCDLen) )
        {
            ulRet = WSEC_ERR_SDP_PWD_VERIFY_FAIL;
            WSEC_LOG_E("[SDP] Password cipher text failed to pass the verification.");
            break;
        }

        ulRet = WSEC_SUCCESS;
    } do_end;

    WSEC_FREE(pTempCipherData);
    return ulRet;
}

/*****************************************************************************
 �� �� ��  : SDP_ShowStructSize
 ��������  : ��ʾ�ṹ����, ���ڲ�ͬ�����µĵ���
 �������  : pfShow: ������ʵ�ֵĻ��Ժ���
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2015��3��6��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
#ifdef WSEC_DEBUG
WSEC_VOID SDP_ShowStructSize(WSEC_FP_ShowStructSize pfShow)
{
    pfShow("SDP_CIPHER_HEAD", sizeof(SDP_CIPHER_HEAD));
    pfShow("SDP_HMAC_ALG_ATTR", sizeof(SDP_HMAC_ALG_ATTR));
    pfShow("SDP_PWD_HEAD", sizeof(SDP_PWD_HEAD));
    pfShow("SDP_BOD_CIPHER_HEAD", sizeof(SDP_BOD_CIPHER_HEAD));
    pfShow("SDP_CIPHER_HEAD_STRU", sizeof(SDP_CIPHER_HEAD_STRU));
    pfShow("SDP_HMAC_HEAD_STRU", sizeof(SDP_HMAC_HEAD_STRU));
    pfShow("SDP_PWD_HEAD_STRU", sizeof(SDP_PWD_HEAD_STRU));
    pfShow("SDP_CIPHER_FILE_HDR_STRU", sizeof(SDP_CIPHER_FILE_HDR_STRU));
}
#endif

#ifdef __cplusplus
}
#endif  /* __cplusplus */

