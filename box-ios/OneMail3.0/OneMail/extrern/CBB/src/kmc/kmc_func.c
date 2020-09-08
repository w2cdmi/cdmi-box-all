/* ����pc lint�澯�ɺ��� */
/*lint -e506 -e522 -e533 -e534 -e539 -e545 -e550 -e573 -e574 -e602 -e603 -e632 -e633 -e634 -e636 -e638 -e639 -e641 -e655 -e665 -e701 -e702 -e750 -e785 -e794 -e830 -e960 */

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

                        KMC - Key Manangement Component(��Կ�������)
% KMC_PRI_ ǰ׺��, ��KMC��˽�к���, ������ǹ�������
% ȫ�ֱ��� g_pKeystore, g_pKmcCfg ����������ͻ���ʱ���, ����������: ���������ӽ���, ˽�к����������ӽ�����

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

#include "wsec_config.h"
#include "wsec_type.h"
#include "kmc_itf.h"
#include "cac_pri.h"
#include "kmc_pri.h"
#include "wsec_pri.h"
#include "wsec_share.h"

#ifdef __cplusplus
extern "C"{
#endif /* __cplusplus */

#define return_err_if_kmc_not_work do{if (g_KmcSys.eState != WSEC_RUNNING){WSEC_LOG_E("KMC not running.");return WSEC_ERR_KMC_CBB_NOT_INIT;}} do_end
#define return_err_if_domain_privacy(ulDomainId) return_oper_if(KMC_IS_PRI_DOMAIN(ulDomainId), KMC_LOG_DOMAIN_PRIVACY(ulDomainId), WSEC_ERR_KMC_CANNOT_ACCESS_PRI_DOMAIN)

#define KMC_LOG_DOMAIN_PRIVACY(ulDomainId) WSEC_LOG_E3("DomainId(%u) is privacy(%d ~ %d)", (ulDomainId), KMC_PRI_DOMAIN_ID_MIN, KMC_PRI_DOMAIN_ID_MAX)

/* MK��/ȥ�� */
#define KMC_MASK_MK(pMk)   WSEC_Xor(pMk->stMkRear.abKey, pMk->stMkRear.ulPlainLen, g_KmcSys.abMkMaskCode, sizeof(g_KmcSys.abMkMaskCode), pMk->stMkRear.abKey, pMk->stMkRear.ulPlainLen);
#define KMC_UNMASK_MK(pMk) KMC_MASK_MK(pMk) /* ���ں��ټ��ڼ�Ϊ����, �ɶ��Կ��� */

/* ��MKȥ�ڵ�... */
#define KMC_UNMASK_MK_TO(pMk, Dst, DstLen) WSEC_Xor(pMk->stMkRear.abKey, pMk->stMkRear.ulPlainLen, g_KmcSys.abMkMaskCode, sizeof(g_KmcSys.abMkMaskCode), Dst, DstLen);

#define KMC_IS_VALID_KEY_TYPE(usKeyType) (WSEC_IS3(usKeyType, KMC_KEY_TYPE_ENCRPT, KMC_KEY_TYPE_INTEGRITY, KMC_KEY_TYPE_ENCRPT_INTEGRITY))
#define KMC_IS_VALID_KEY_TYPE_LEN(ulLen) WSEC_IN_SCOPE(ulLen, 1, g_KmcSys.ulMkPlainLenMax)
#define KMC_IS_VALID_RK_FROM(ucKeyFrom) WSEC_IS2(ucKeyFrom, KMC_RK_GEN_BY_INNER, KMC_RK_GEN_BY_IMPORT)
#define KMC_IS_VALID_MK_FROM(ucKeyFrom) WSEC_IS2(ucKeyFrom, KMC_MK_GEN_BY_INNER, KMC_MK_GEN_BY_IMPORT)
#define KMC_IS_VALID_KEY_STATUS(ucStatus) WSEC_IS2(ucStatus, KMC_KEY_STATUS_INACTIVE, KMC_KEY_STATUS_ACTIVE)
#define KMC_IS_INNER_CREATE_RK (KMC_RK_GEN_BY_INNER == g_KmcSys.eRootKeyFrom)
#define KMC_IS_PRI_DOMAIN(ulDomainId) WSEC_IN_SCOPE(ulDomainId, KMC_PRI_DOMAIN_ID_MIN, KMC_PRI_DOMAIN_ID_MAX)

#define KMC_CFG_IS_ROOT_KEY_VALID(pstCfgRootKey) (((pstCfgRootKey)->ulRootKeyLifeDays > 0) && ((pstCfgRootKey)->ulRootMasterKeyIterations > 0))
#define KMC_CFG_IS_KEY_MAN_VALID(pstCfgKeyMan)  ((pstCfgKeyMan->ulWarningBeforeKeyExpiredDays > 0) && (pstCfgKeyMan->ulGraceDaysForUseExpiredKey) && \
                                                ((pstCfgKeyMan)->stAutoUpdateKeyTime.ucHour < 24) && ((pstCfgKeyMan)->stAutoUpdateKeyTime.ucMinute < 60))

#define KMC_ENCRYPT_MK_ALGID  WSEC_ALGID_AES256_CBC  /* MK�����㷨ID */
#define KMC_HMAC_MK_ALGID     WSEC_ALGID_HMAC_SHA256 /* HMAC����MK���ݵ��㷨ID */
#define KMC_IS_MAN_CFG_FILE ((g_KmcSys.apszKmcCfgFile[0] != WSEC_NULL_PTR) && (g_KmcSys.apszKmcCfgFile[1] != WSEC_NULL_PTR))

#define KMC_KSF_NUM WSEC_NUM_OF(g_KmcSys.apszKeystoreFile)

const WSEC_BYTE g_KsfFlag[32] = {0x5F, 0x64, 0x97, 0x8D, 0x19, 0x4F, 0x89, 0xCF, 0xA8, 0x3F, 0x8E, 0xE1, 0xDB, 0x01, 0x3C, 0x0C,
                                 0x88, 0x42, 0x4A, 0x1C, 0xB7, 0xFC, 0xAD, 0x70, 0x4E, 0x45, 0x13, 0xA5, 0x14, 0x46, 0x71, 0x6C};
const WSEC_BYTE g_MkfFlag[32] = {0xF5, 0x06, 0xEF, 0xD8, 0x56, 0x3F, 0xD3, 0x07, 0x3F, 0xDE, 0x29, 0xD7, 0x89, 0xFE, 0xD3, 0xEC,
                                 0x8D, 0x48, 0xA4, 0x17, 0xC7, 0xDD, 0x3D, 0xD0, 0xF3, 0xED, 0x92, 0x7D, 0x46, 0x3A, 0xAA, 0x0D};
const WSEC_BYTE g_KcfFlag[32] = {0xB7, 0x0D, 0xB7, 0xC5, 0xBD, 0xF7, 0xE8, 0x4B, 0x47, 0xBB, 0xE4, 0x75, 0xAE, 0x52, 0x85, 0x4B,
                                 0x79, 0xF0, 0x27, 0x6E, 0x5F, 0xEB, 0xE9, 0x6C, 0x3F, 0x3D, 0x21, 0xD9, 0x99, 0xD0, 0xC7, 0xB2};

KMC_KSF_MEM_STRU* g_pKeystore = WSEC_NULL_PTR;
/*lint -e64*/
KMC_CFG_STRU*     g_pKmcCfg   = WSEC_NULL_PTR;
/*line -restore*/
KMC_SYS_STRU      g_KmcSys    = {0};
KMC_INI_CTX_STRU* g_pKmcIniCtx = WSEC_NULL_PTR; /* KMC��ʼ��ʹ�õ�������, ��ʼ����������Ч */

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                          һ�� �ڲ�˽�к���
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

/*****************************************************************************
 �� �� ��  : KMC_PRI_FreeKsfSnapshot
 ��������  : �ͷ�KMC_KSF_SNAPSHOT_STRU���͵Ķ�̬�ڴ�
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : p
 �� �� ֵ  : WSEC_NULL_PTR, ����Callerʹ��
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��10��23��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
KMC_KSF_MEM_STRU* KMC_PRI_FreeKsfSnapshot(KMC_KSF_MEM_STRU* pData)
{
    if (pData)
    {
        pData->arrMk = WSEC_ARR_Finalize(pData->arrMk);
        WSEC_FREE(pData);
    }

    return pData;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_FreeKmcCfg
 ��������  : �ͷ�KMC_KEY_CFG_STRU���͵Ķ�̬�ڴ�
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pKmcCfg
 �� �� ֵ  : WSEC_NULL_PTR, ����Callerʹ��
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��10��23��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
KMC_CFG_STRU* KMC_PRI_FreeKmcCfg(KMC_CFG_STRU* pKmcCfg)
{
    if (pKmcCfg)
    {
        pKmcCfg->arrDomainCfg = WSEC_ARR_Finalize(pKmcCfg->arrDomainCfg);
        WSEC_FREE(pKmcCfg);
    }

    return pKmcCfg;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_FreeGlobalMem
 ��������  : �ͷ�ȫ����Դ
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ppKmcCfg: ָ����������ָ���ָ��
             ppKeystore: ָ��Keystoreָ���ָ��
 �� �� ֵ  : ��
 �ر�ע��  : ȫ����Դ�����ͷ�

 �޸���ʷ
  1.��    ��   : 2014��10��23��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_FreeGlobalMem(KMC_CFG_STRU** ppKmcCfg, KMC_KSF_MEM_STRU** ppKeystore)
{
    WSEC_SIZE_T i;

    *ppKeystore = KMC_PRI_FreeKsfSnapshot(*ppKeystore);
    *ppKmcCfg = KMC_PRI_FreeKmcCfg(*ppKmcCfg);

    for (i = 0; i < KMC_KSF_NUM; i++)
    {
        WSEC_FREE(g_KmcSys.apszKeystoreFile[i]);
        WSEC_FREE(g_KmcSys.apszKmcCfgFile[i]);
    }

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_Finalize
 ��������  : KMCȥ��ʼ��
 �� �� ��  : eLock: �Ƿ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��03��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_Finalize(KMC_LOCK_OPER_ENUM eLock)
{
    WSEC_LOG_E("KMC_PRI_Finalize()");

    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Lock(KMC_LOCK_BOTH);}
    KMC_PRI_FreeGlobalMem(&g_pKmcCfg, &g_pKeystore);
    g_KmcSys.eState = WSEC_WAIT_INIT;
    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Unlock(KMC_LOCK_BOTH);}

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_UpdateRootKeyPri
 ��������  : ���¸���Կ
             ԭ��: �������ɸ���Կ, ��MK���ֲ���.
 �� �� ��  : pbKeyEntropy: ��Կ������;[�ɿ�];
             ulSize:       ��Կ��������.
             eLock:        �Ƿ���Ҫ���ٽ���Դ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��07��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_UpdateRootKeyPri(const WSEC_BYTE* pbKeyEntropy, WSEC_SIZE_T ulSize, KMC_LOCK_OPER_ENUM eLock)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BUFF stEntropy = {0};
    WSEC_BUFF* pEntropy = WSEC_NULL_PTR;
    KMC_KSF_RK_STRU* pRk = WSEC_NULL_PTR;

    return_err_if_kmc_not_work;
    return_oper_if(!WSEC_GetUtcDateTime(&g_pKeystore->stRkInfo.stRkExpiredTimeUtc), WSEC_LOG_E("Get current UTC fail."), WSEC_ERR_GET_CURRENT_TIME_FAIL);

    pRk = (KMC_KSF_RK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_RK_STRU));
    return_oper_if(!pRk, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_RK_STRU)), WSEC_ERR_MALLOC_FAIL);
    
    KMC_PRI_NtfRkExpire(&g_pKeystore->stRkInfo, 0); /* ���¸���Կ����ǰ�����澯 */

    if (pbKeyEntropy)
    {
        pEntropy = &stEntropy;
        pEntropy->pBuff = (WSEC_BYTE*)pbKeyEntropy;
        pEntropy->nLen  = ulSize;
    }

    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Lock(KMC_LOCK_KEYSTORE);}
    /* ���¸���Կ */
    do
    {
        nErrCode = KMC_PRI_CreateRootKey(pEntropy, pRk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E("KMC_PRI_CreateRootKey() fail."), oper_null); /* ʧ����Ҳû�й�ϵ */

        nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, pRk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E("KMC_PRI_WriteKsfSafety() fail."), oper_null);
    } do_end;
    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);}

    WSEC_FREE(pRk);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_GetMaxMkId
 ��������  : ��ȡָ��Domain�µ�ǰ���MK ID
 �� �� ��  : eLock: �Ƿ���Ҫ����
             ulDomainId:  �������ʶ
 �� �� ��  : pulMaxKeyId: ���Master Key ID
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��23��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_GetMaxMkId(KMC_LOCK_OPER_ENUM eLock, WSEC_UINT32 ulDomainId, WSEC_UINT32* pulMaxKeyId)
{
    WSEC_INT32 i;
    WSEC_UINT32 ulMaxId = 0;
    WSEC_BOOL bDomainFound = WSEC_FALSE;
    const KMC_MEM_MK_STRU* pItem;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("KMC_PRI_GetMaxMkId", pulMaxKeyId);

    if_oper(KMC_NEED_LOCK == eLock, KMC_PRI_Lock(KMC_LOCK_KEYSTORE));
    for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
    {
        pItem = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
        break_oper_if(!pItem, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        if (pItem->stMkInfo.ulDomainId == ulDomainId)
        {
            bDomainFound = WSEC_TRUE;
            if (ulMaxId < pItem->stMkInfo.ulKeyId) {ulMaxId = pItem->stMkInfo.ulKeyId;}
        }
        else if (bDomainFound) /* MK����Domain���е�, ɨ�赽Domain���������Domain, ����Ҫ��ѭ���� */
        {
            break;
        }else{;}
    }
    if_oper(KMC_NEED_LOCK == eLock, KMC_PRI_Unlock(KMC_LOCK_KEYSTORE));

    if (bDomainFound)
    {
        *pulMaxKeyId = ulMaxId;
    }
    else if (WSEC_SUCCESS == nErrCode)
    {
        nErrCode = WSEC_ERR_KMC_DOMAIN_MISS;
    }else{;}

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeDefaultCfg4RootKey
 ��������  : ����RootKeyȱʡ����
 �� �� ��  : ��
 �� �� ��  : pstRkCfg: ���ȱʡ����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_MakeDefaultCfg4RootKey(KMC_CFG_ROOT_KEY_STRU* pstRkCfg)
{
    WSEC_ASSERT(pstRkCfg);
    
    pstRkCfg->ulRootKeyLifeDays         = 3650;
    pstRkCfg->ulRootMasterKeyIterations = 5000;
    
    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeDefaultCfg4KeyMan
 ��������  : ������Կ�������ڹ���ȱʡ����
 �� �� ��  : ��
 �� �� ��  : pstRkCfg: ���ȱʡ����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_MakeDefaultCfg4KeyMan(KMC_CFG_KEY_MAN_STRU* pstKmCfg)
{
    WSEC_ASSERT(pstKmCfg);

    pstKmCfg->ulWarningBeforeKeyExpiredDays = 30;
    pstKmCfg->ulGraceDaysForUseExpiredKey   = 60;
    pstKmCfg->bKeyAutoUpdate                = WSEC_TRUE;

    pstKmCfg->stAutoUpdateKeyTime.ucHour   = 1;
    pstKmCfg->stAutoUpdateKeyTime.ucMinute = 10;
    pstKmCfg->stAutoUpdateKeyTime.ucWeek   = 7;
    
    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeDefaultCfg4DataProtect
 ��������  : �������ݱ���ȱʡ����
 �� �� ��  : eType: ���ݱ�������
 �� �� ��  : pstCfg: ���ȱʡ����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_MakeDefaultCfg4DataProtect(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstCfg)
{
    WSEC_ASSERT(pstCfg);

    if (SDP_ALG_ENCRPT == eType)
    {
        pstCfg->ulAlgId         = WSEC_ALGID_AES128_CBC;
        pstCfg->usKeyType       = KMC_KEY_TYPE_ENCRPT_INTEGRITY;
        pstCfg->bAppendMac      = WSEC_TRUE;
        pstCfg->ulKeyIterations = 0;
    }
    else if (SDP_ALG_INTEGRITY == eType)
    {
        pstCfg->ulAlgId         = WSEC_ALGID_HMAC_SHA256;
        pstCfg->usKeyType       = KMC_KEY_TYPE_ENCRPT_INTEGRITY;
        pstCfg->bAppendMac      = WSEC_FALSE;
        pstCfg->ulKeyIterations = 0;
    }
    else if (SDP_ALG_PWD_PROTECT == eType)
    {
        pstCfg->ulAlgId         = WSEC_ALGID_PBKDF2_HMAC_SHA256;
        pstCfg->usKeyType       = 0;
        pstCfg->bAppendMac      = 0;
        pstCfg->ulKeyIterations = 2000;
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeDefaultCfg4Domain
 ��������  : ����ȱʡ��Domain�Լ���KeyType����
 �� �� ��  : ��
 �� �� ��  : pKmcCfg: ���ȱʡ��Domain����KeyType����
 ��γ���  : ��
 �� �� ֵ  : �ɹ���ʧ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_MakeDefaultCfg4Domain(KMC_CFG_STRU* pKmcCfg)
{
    KMC_CFG_DOMAIN_INFO_STRU astDomainCfg[] = {
            {0, KMC_MK_GEN_BY_INNER,  "Generate MK by KMC"},
            {1, KMC_MK_GEN_BY_IMPORT, "Register MK by APP"}};
    KMC_CFG_KEY_TYPE_STRU stKeyType = {KMC_KEY_TYPE_ENCRPT_INTEGRITY, 32, 180};
    WSEC_SIZE_T i;
    WSEC_ERR_T nRet = WSEC_SUCCESS;

    /* 1. ȱʡDomain������Ϣ */
    for (i = 0; i < WSEC_NUM_OF(astDomainCfg); i++)
    {
        nRet = KMC_PRI_AddDomain2Array(pKmcCfg, &astDomainCfg[i]);
        if (nRet != WSEC_SUCCESS)
        {
            WSEC_LOG_E1("KMC_PRI_AddDomain2Array() = %u", nRet);
            WSEC_ARR_RemoveAll(pKmcCfg->arrDomainCfg);
            return WSEC_FALSE;
        }
    }

    /* 2. Domain��ȱʡKeyType���� */
    return_oper_if(KMC_PRI_AddDomainKeyType2Array(pKmcCfg, astDomainCfg[0].ulId, &stKeyType) != WSEC_SUCCESS, oper_null, WSEC_FALSE);
    return_oper_if(KMC_PRI_AddDomainKeyType2Array(pKmcCfg, astDomainCfg[1].ulId, &stKeyType) != WSEC_SUCCESS, oper_null, WSEC_FALSE);

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadRootKeyCfg
 ��������  : ��ȡRK���������Ϣ
 �� �� ��  : ��
 �� �� ��  : pKmcCfg: ���RK���������Ϣ
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadRootKeyCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg)
{
    WSEC_TLV_STRU stTlv = {0};
    KMC_CFG_ROOT_KEY_STRU* pstReadRkCfg = WSEC_NULL_PTR;
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;

    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pstRkCfg) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_RK_CFG);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_ROOT_KEY_STRU), WSEC_LOG_E1("The RK-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);

        pstReadRkCfg = (KMC_CFG_ROOT_KEY_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4RkCfg(pstReadRkCfg, wbcNetwork2Host);

        return WSEC_MEMCPY(pstRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU), pstReadRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU)) == EOK ? WSEC_TRUE : WSEC_FALSE;
    }
    
    return WSEC_FALSE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadKeyManCfg
 ��������  : ��ȡKey�������������Ϣ
 �� �� ��  : ��
 �� �� ��  : pstKmCfg: ���Key�������������Ϣ
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadKeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKmCfg)
{
    WSEC_TLV_STRU stTlv = {0};
    KMC_CFG_KEY_MAN_STRU* pstReadCfg = WSEC_NULL_PTR;
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;

    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pstKmCfg) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_KEY_MAN);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_KEY_MAN_STRU), WSEC_LOG_E1("The RK-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);

        pstReadCfg = (KMC_CFG_KEY_MAN_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4KeyManCfg(pstReadCfg, wbcNetwork2Host);

        return WSEC_MEMCPY(pstKmCfg, sizeof(KMC_CFG_KEY_MAN_STRU), pstReadCfg, sizeof(KMC_CFG_KEY_MAN_STRU)) == EOK ? WSEC_TRUE : WSEC_FALSE;
    }
    
    return WSEC_FALSE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadCfgOfDomainCount
 ��������  : ��ȡDomain���ø���
 �� �� ��  : ��
 �� �� ��  : pulDomainCount: ���Domain���ø���
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadCfgOfDomainCount(WSEC_UINT32* pulDomainCount)
{
    WSEC_TLV_STRU stTlv = {0};
    WSEC_UINT32 ulDomainCount = 0;
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    
    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pulDomainCount) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_DOMAIN_CFG);
        ulDomainCount++;
    }

    *pulDomainCount = ulDomainCount;
    
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadCfgOfDomainInfo
 ��������  : ��ȡulDomainCount��Domain����
 �� �� ��  : ulDomainCount: Domain����
 �� �� ��  : pstAllDomainInfo: ���ulDomainCount��Domain����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadCfgOfDomainInfo(KMC_CFG_DOMAIN_INFO_STRU* pstAllDomainInfo, WSEC_UINT32 ulDomainCount)
{
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    WSEC_TLV_STRU stTlv = {0};
    KMC_CFG_DOMAIN_INFO_STRU *pRead, *pWri;
    
    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pstAllDomainInfo) {return WSEC_FALSE;}
    if (ulDomainCount < 1) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    pWri = pstAllDomainInfo;
    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_DOMAIN_CFG);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_DOMAIN_INFO_STRU), WSEC_LOG_E1("The Domain-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);
        if (ulDomainCount < 1) {break;}

        pRead = (KMC_CFG_DOMAIN_INFO_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4DomainInfo(pRead, wbcNetwork2Host);

        return_oper_if(WSEC_MEMCPY(pWri, sizeof(KMC_CFG_DOMAIN_INFO_STRU), pRead, sizeof(KMC_CFG_DOMAIN_INFO_STRU)) != EOK, 
                       WSEC_LOG_E("Memory copy fail."), WSEC_ERR_MEMCPY_FAIL);
        pWri++;
        ulDomainCount--;
    }

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadCfgOfDomainKeyTypeCount
 ��������  : ��ȡָ��Domain��KeyType���ø���
 �� �� ��  : ulDomainId: �������ʶ
 �� �� ��  : pulKeyTypeCount: ���ָ��Domain��KeyType���ø���
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadCfgOfDomainKeyTypeCount(WSEC_UINT32 ulDomainId, WSEC_UINT32* pulKeyTypeCount)
{
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    WSEC_TLV_STRU stTlv = {0};
    KMC_CFG_DOMAIN_INFO_STRU* pDomain = WSEC_NULL_PTR;
    WSEC_BOOL bDomainFound = WSEC_FALSE;
    WSEC_UINT32 ulKeyTypeNum = 0;
    
    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pulKeyTypeCount) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    /* 1. �ҵ�Domain���� */
    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_DOMAIN_CFG);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_DOMAIN_INFO_STRU), WSEC_LOG_E1("The Domain-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);

        pDomain = (KMC_CFG_DOMAIN_INFO_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4DomainInfo(pDomain, wbcNetwork2Host);
        continue_if(pDomain->ulId != ulDomainId);

        bDomainFound = WSEC_TRUE;
        break;
    }
    if (!bDomainFound) {return WSEC_FALSE;}

    /* 2. ��ȡ��Domain�����µ�KeyType�������� */
    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        if (stTlv.ulTag != KMC_CFT_DOMAIN_KEY_TYPE) {break;}
        ulKeyTypeNum++;
    }

    *pulKeyTypeCount = ulKeyTypeNum;

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadCfgOfDomainKeyType
 ��������  : ��ȡָ��Domain������KeyType����
 �� �� ��  : ulDomainId: �������ʶ
             ulKeyTypeCount: ָ��Domain�µ�KeyType���ø���
 �� �� ��  : pstDomainAllKeyType: ���ָ��Domain������KeyType����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadCfgOfDomainKeyType(WSEC_UINT32 ulDomainId, KMC_CFG_KEY_TYPE_STRU* pstDomainAllKeyType, WSEC_UINT32 ulKeyTypeCount)
{
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    WSEC_TLV_STRU stTlv = {0};
    KMC_CFG_DOMAIN_INFO_STRU* pDomain = WSEC_NULL_PTR;
    WSEC_BOOL bDomainFound = WSEC_FALSE;
    KMC_CFG_KEY_TYPE_STRU *pRead, *pWri;
    
    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pstDomainAllKeyType) {return WSEC_FALSE;}
    if (ulKeyTypeCount < 1) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    /* 1. �ҵ�Domain���� */
    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_DOMAIN_CFG);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_DOMAIN_INFO_STRU), WSEC_LOG_E1("The RK-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);
        
        pDomain = (KMC_CFG_DOMAIN_INFO_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4DomainInfo(pDomain, wbcNetwork2Host);
        continue_if(pDomain->ulId != ulDomainId);

        bDomainFound = WSEC_TRUE;
        break;
    }
    if (!bDomainFound) {return WSEC_FALSE;}

    /* 2. ��ȡ��Domain�����µ�����KeyType���� */
    pWri = pstDomainAllKeyType;
    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        if (stTlv.ulTag != KMC_CFT_DOMAIN_KEY_TYPE) {break;}
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_KEY_TYPE_STRU), WSEC_LOG_E1("The KeyType-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);
        if (ulKeyTypeCount < 1) {break;}

        pRead = (KMC_CFG_KEY_TYPE_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4KeyType(pRead, wbcNetwork2Host);
        return_oper_if(WSEC_MEMCPY(pWri, sizeof(KMC_CFG_KEY_TYPE_STRU), pRead, sizeof(KMC_CFG_KEY_TYPE_STRU)) != EOK, WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);

        ulKeyTypeCount--;
        pWri++;
    }

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadCfgOfDataProtection
 ��������  : ��ȡָ�����������ݱ����㷨����
 �� �� ��  : eType: ���ݱ����㷨������
 �� �� ��  : pstPara: ���ָ�����������ݱ����㷨����
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_ReadCfgOfDataProtection(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstPara)
{
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    WSEC_TLV_STRU stTlv = {0};
    WSEC_INT32 nIndex = 0;
    KMC_CFG_DATA_PROTECT_STRU* pRead = WSEC_NULL_PTR;
    
    if (!g_pKmcIniCtx) {return WSEC_FALSE;}
    if (!g_pKmcIniCtx->bCbbReadCfg) {return WSEC_FALSE;}
    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;
    if (!pstPara) {return WSEC_FALSE;}

    return_oper_if(WSEC_FSEEK(pReadKcfCtx->pFile, 0, SEEK_SET), WSEC_LOG_E("Cannot move file-ptr to start-pos."), WSEC_ERR_READ_FILE_FAIL);

    while (WSEC_ReadTlv(pReadKcfCtx->pFile, pReadKcfCtx->stReadBuff.pBuff, pReadKcfCtx->stReadBuff.nLen, &stTlv, WSEC_NULL_PTR))
    {
        continue_if(stTlv.ulTag != KMC_CFT_DP_CFG);
        return_oper_if(stTlv.ulLen != sizeof(KMC_CFG_DATA_PROTECT_STRU), WSEC_LOG_E1("The DataProtect-Cfg in '%s' is incorrect.", pReadKcfCtx->pszFile), WSEC_FALSE);

        if (nIndex == eType)
        {
            pRead = (KMC_CFG_DATA_PROTECT_STRU*)stTlv.pVal;
            KMC_PRI_CvtByteOrder4DataProtectCfg(pRead, wbcNetwork2Host);
            return WSEC_MEMCPY(pstPara, sizeof(KMC_CFG_DATA_PROTECT_STRU), pRead, sizeof(KMC_CFG_DATA_PROTECT_STRU)) == EOK ? WSEC_TRUE : WSEC_FALSE;
        }
        nIndex++;
    }

    return WSEC_FALSE;
}


/*****************************************************************************
 �� �� ��  : KMC_PRI_OpenIniCtx
 ��������  : ����KMC��ʼ���׶�ʹ�õ�������.
 �� �� ��  : bManCfgFile: �Ƿ���KMC���������ļ�
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_OpenIniCtx(WSEC_BOOL bManCfgFile)
{
    WSEC_SIZE_T i;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    g_pKmcIniCtx = (KMC_INI_CTX_STRU*)WSEC_MALLOC(sizeof(KMC_INI_CTX_STRU));
    return_oper_if(!g_pKmcIniCtx, WSEC_LOG_E4MALLOC(sizeof(KMC_INI_CTX_STRU)), WSEC_ERR_MALLOC_FAIL);
    g_pKmcIniCtx->bCbbReadCfg = bManCfgFile;
    if (!bManCfgFile) {return WSEC_SUCCESS;}

    do
    {
        WSEC_BUFF_ALLOC(g_pKmcIniCtx->stReadKcfCtx.stReadBuff, WSEC_FILE_IO_SIZE_MAX);
        break_oper_if(!g_pKmcIniCtx->stReadKcfCtx.stReadBuff.pBuff, WSEC_LOG_E4MALLOC(g_pKmcIniCtx->stReadKcfCtx.stReadBuff.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);

        for (i = 0; i < WSEC_NUM_OF(g_KmcSys.apszKmcCfgFile); i++)
        {
            g_pKmcIniCtx->stReadKcfCtx.pszFile = g_KmcSys.apszKmcCfgFile[i];
            nErrCode = KMC_PRI_ChkCfgFile(&g_pKmcIniCtx->stReadKcfCtx);
            if (WSEC_SUCCESS == nErrCode)
            {
                WSEC_LOG_I1("'%s' chose as master-cfg-file.", g_pKmcIniCtx->stReadKcfCtx.pszFile);
                break; /* ���ҵ���ȷ�������ļ� */
            }
            else
            {
                continue; /* ����ȷ�����������ļ��Ƿ���� */
            }
        }
    }do_end;

    if (nErrCode != WSEC_SUCCESS) {KMC_PRI_CloseIniCtx();}

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CloseIniCtx
 ��������  : �ر�KMC��ʼ�����̵�������, �ͷŸù����е���Դ.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CloseIniCtx()
{
    KMC_READ_KCF_CTX_STRU* pReadKcfCtx = WSEC_NULL_PTR;
    
    if (!g_pKmcIniCtx) {return;}

    pReadKcfCtx = &g_pKmcIniCtx->stReadKcfCtx;

    if (pReadKcfCtx->pFile) {WSEC_FCLOSE(pReadKcfCtx->pFile);}
    WSEC_BUFF_FREE(pReadKcfCtx->stReadBuff);
    WSEC_FREE(g_pKmcIniCtx);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ChkProtectCfg
 ��������  : ������ݱ����㷨���úϷ���
 �� �� ��  : eType: ���ݱ�������
             pstPara: ���ò���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��03��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ChkProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, const KMC_CFG_DATA_PROTECT_STRU *pstPara)
{
    WSEC_BOOL bAlgIdOk = WSEC_FALSE, bKeyTypeOk = WSEC_FALSE, bIterationOk = WSEC_TRUE;

    return_err_if_para_invalid("KMC_PRI_ChkProtectCfg", pstPara && WSEC_IS3(eType, SDP_ALG_ENCRPT, SDP_ALG_INTEGRITY, SDP_ALG_PWD_PROTECT));

    /* ����㷨ID, KeyType */
    if (SDP_ALG_ENCRPT == eType)
    {
        bAlgIdOk = WSEC_IS_ENCRYPT_ALGID(pstPara->ulAlgId);
        bKeyTypeOk = (pstPara->usKeyType & KMC_KEY_TYPE_ENCRPT);
    }
    else if (SDP_ALG_INTEGRITY == eType)
    {
        bAlgIdOk = WSEC_IS_HMAC_ALGID(pstPara->ulAlgId);
        bKeyTypeOk = (pstPara->usKeyType & KMC_KEY_TYPE_INTEGRITY);
    }
    else if (SDP_ALG_PWD_PROTECT == eType)
    {
        bAlgIdOk = WSEC_IS_PBKDF_ALGID(pstPara->ulAlgId);
        bKeyTypeOk = WSEC_TRUE;
        bIterationOk = KMC_IS_KEYITERATIONS_VALID(pstPara->ulKeyIterations);
    }
    else
    {
        return WSEC_ERR_INVALID_ARG;
    }
    return_oper_if(!bAlgIdOk, WSEC_LOG_E1("Invalid alg-id(%d)", pstPara->ulAlgId), WSEC_ERR_INVALID_ARG);
    return_oper_if(!bKeyTypeOk, WSEC_LOG_E1("Invalid KeyType(%d)", pstPara->usKeyType), WSEC_ERR_INVALID_ARG);
    return_oper_if(!bIterationOk, WSEC_LOG_E1("Invalid KeyIterationS(%d)", pstPara->ulKeyIterations), WSEC_ERR_INVALID_ARG);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_WriteKcfSafety
 ��������  : �ɿ��ؽ�����д��KMC�����ļ�
 �� �� ��  : pKmcCfg: ����д�������ļ�������
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_WriteKcfSafety(const KMC_CFG_STRU* pKmcCfg)
{
    WSEC_ASSERT(KMC_IS_MAN_CFG_FILE);
    return WSEC_WriteFileS(pKmcCfg, g_KmcSys.apszKmcCfgFile, WSEC_NUM_OF(g_KmcSys.apszKmcCfgFile), (WSEC_WriteFile)KMC_PRI_WriteKcf, WSEC_NULL_PTR);
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_WriteKcf
 ��������  : ����KMC��������
 �� �� ��  : pKmcCfg: KMC��������
             pszKmcCfgFile: KMC�����ļ���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_WriteKcf(const KMC_CFG_STRU* pKmcCfg, const WSEC_CHAR* pszKmcCfgFile, const WSEC_VOID* pvReserved)
{
    WSEC_FILE pFile = WSEC_NULL_PTR;
    KMC_KCF_HDR_STRU* pstHdr = WSEC_NULL_PTR;
    const KMC_CFG_DATA_PROTECT_STRU* pstDataProtectCfg = WSEC_NULL_PTR;
    const KMC_DOMAIN_CFG_STRU* pstDomainCfg = WSEC_NULL_PTR;
    const KMC_CFG_KEY_TYPE_STRU* pstKeyType = WSEC_NULL_PTR;
    WSEC_ERR_T ulErrCode = WSEC_SUCCESS;
    WSEC_BUFF stBuff = {0};
    WSEC_BUFF stHash = {0};
    WSEC_INT32 i, j, nPos;

    WSEC_ASSERT(pKmcCfg && pszKmcCfgFile);
    WSEC_UNREFER(pvReserved);

    /*lint -e668*/
    pFile = WSEC_FOPEN(pszKmcCfgFile, "w+b");
    return_oper_if(!pFile, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), WSEC_ERR_WRI_FILE_FAIL);

    WSEC_BUFF_ALLOC(stBuff, WSEC_FILE_IO_SIZE_MAX); /* ������󳤶ȷ���TLV������ */
    pstHdr = (KMC_KCF_HDR_STRU*)WSEC_MALLOC(sizeof(KMC_KCF_HDR_STRU));
    
    do
    {
        break_oper_if(!stBuff.pBuff, WSEC_LOG_E4MALLOC(stBuff.nLen), ulErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pstHdr, WSEC_LOG_E4MALLOC(sizeof(KMC_KCF_HDR_STRU)), ulErrCode = WSEC_ERR_MALLOC_FAIL);

        WSEC_ASSERT(sizeof(g_KcfFlag) == sizeof(pstHdr->abFormatFlag));

        /* 1. ��ʱд�ļ�ͷռλ(������Ҫ��д) */
        ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_HDR, sizeof(KMC_KCF_HDR_STRU), pstHdr);
        break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);
        nPos = (WSEC_INT32)WSEC_FTELL(pFile); /* ��ס�˿̵��ļ�λ��, �����Ӵ˿�ʼ�����ļ���Hash */
        break_oper_if(nPos < 0, WSEC_LOG_E("ftell() fail."), ulErrCode = WSEC_ERR_WRI_FILE_FAIL);

        /* 2. дRK������Ϣ */
        WSEC_ASSERT(stBuff.nLen >= sizeof(pKmcCfg->stRkCfg));
        break_oper_if(WSEC_MEMCPY(stBuff.pBuff, stBuff.nLen, &pKmcCfg->stRkCfg, sizeof(pKmcCfg->stRkCfg)) != EOK,
                      WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
        KMC_PRI_CvtByteOrder4RkCfg((KMC_CFG_ROOT_KEY_STRU*)stBuff.pBuff, wbcHost2Network);
        ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_RK_CFG, sizeof(KMC_CFG_ROOT_KEY_STRU), stBuff.pBuff);
        break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);

        /* 3. дKey�������� */
        WSEC_ASSERT(stBuff.nLen >= sizeof(pKmcCfg->stKmCfg));
        break_oper_if(WSEC_MEMCPY(stBuff.pBuff, stBuff.nLen, &pKmcCfg->stKmCfg, sizeof(pKmcCfg->stKmCfg)) != EOK,
                      WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
        KMC_PRI_CvtByteOrder4KeyManCfg((KMC_CFG_KEY_MAN_STRU*)stBuff.pBuff, wbcHost2Network);
        ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_KEY_MAN, sizeof(KMC_CFG_KEY_MAN_STRU), stBuff.pBuff);
        break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);

        /* 4. д���ݱ������������� */
        WSEC_ASSERT(stBuff.nLen >= sizeof(pKmcCfg->astDataProtectCfg[0]));
        for (i = 0, pstDataProtectCfg = pKmcCfg->astDataProtectCfg; i < (WSEC_INT32)WSEC_NUM_OF(pKmcCfg->astDataProtectCfg); i++, pstDataProtectCfg++)
        {
            break_oper_if(WSEC_MEMCPY(stBuff.pBuff, stBuff.nLen, pstDataProtectCfg, sizeof(KMC_CFG_DATA_PROTECT_STRU)) != EOK,
                          WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
                          
            KMC_PRI_CvtByteOrder4DataProtectCfg((KMC_CFG_DATA_PROTECT_STRU*)stBuff.pBuff, wbcHost2Network);
            ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_DP_CFG, sizeof(KMC_CFG_DATA_PROTECT_STRU), stBuff.pBuff);
            break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);
            WSEC_FFLUSH(pFile);
        }
        if (ulErrCode != WSEC_SUCCESS) {break;}

        /* 5. дDomain����KeyType���� */
        WSEC_ASSERT(stBuff.nLen >= sizeof(KMC_CFG_DOMAIN_INFO_STRU));
        WSEC_ASSERT(stBuff.nLen >= sizeof(KMC_CFG_KEY_TYPE_STRU));
        for (i = 0; i < WSEC_ARR_GetCount(pKmcCfg->arrDomainCfg); i++)
        {
            pstDomainCfg = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_GetAt(pKmcCfg->arrDomainCfg, i);
            break_oper_if(!pstDomainCfg, WSEC_LOG_E("memory access fail."), ulErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            break_oper_if(WSEC_MEMCPY(stBuff.pBuff, stBuff.nLen, &pstDomainCfg->stDomainInfo, sizeof(pstDomainCfg->stDomainInfo)) != EOK,
                          WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
            KMC_PRI_CvtByteOrder4DomainInfo((KMC_CFG_DOMAIN_INFO_STRU*)stBuff.pBuff, wbcHost2Network);
            ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_DOMAIN_CFG, sizeof(KMC_CFG_DOMAIN_INFO_STRU), stBuff.pBuff);
            break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);
            
            for (j = 0; j < WSEC_ARR_GetCount(pstDomainCfg->arrKeyTypeCfg); j++)
            {
                pstKeyType = (KMC_CFG_KEY_TYPE_STRU*)WSEC_ARR_GetAt(pstDomainCfg->arrKeyTypeCfg, j);
                break_oper_if(!pstKeyType, WSEC_LOG_E("memory access fail."), ulErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

                break_oper_if(WSEC_MEMCPY(stBuff.pBuff, stBuff.nLen, pstKeyType, sizeof(KMC_CFG_KEY_TYPE_STRU)) != EOK,
                              WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
                KMC_PRI_CvtByteOrder4KeyType((KMC_CFG_KEY_TYPE_STRU*)stBuff.pBuff, wbcHost2Network);
                ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_DOMAIN_KEY_TYPE, sizeof(KMC_CFG_KEY_TYPE_STRU), stBuff.pBuff);
                break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Cannot write '%s'", pszKmcCfgFile), oper_null);

                WSEC_FFLUSH(pFile);
            }
            if (ulErrCode != WSEC_SUCCESS) {break;}
        }
        if (ulErrCode != WSEC_SUCCESS) {break;}
        WSEC_FFLUSH(pFile);

        /* 6. �����ļ���Hashֵ, �Է��۸� */
        pstHdr->ulHashAlgId = WSEC_ALGID_SHA256;
        break_oper_if(WSEC_FSEEK(pFile, nPos, SEEK_SET) != 0, WSEC_LOG_E("fseek fail."), ulErrCode = WSEC_ERR_WRI_FILE_FAIL);
        WSEC_BUFF_ASSIGN(stHash, pstHdr->abHash, sizeof(pstHdr->abHash));
        ulErrCode = WSEC_HashFile(pstHdr->ulHashAlgId, pFile, 0, &stHash);
        break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_HashFile()=%u", ulErrCode), oper_null);

        /* 7. ��д�ļ�ͷ */
        break_oper_if(WSEC_MEMCPY(pstHdr->abFormatFlag, sizeof(pstHdr->abFormatFlag), g_KcfFlag, sizeof(g_KcfFlag)) != EOK,
                      WSEC_LOG_E4MEMCPY, ulErrCode = WSEC_ERR_MEMCPY_FAIL);
        pstHdr->ulVer = KMC_KCF_VER;
        KMC_PRI_CvtByteOrder4KcfHdr(pstHdr, wbcHost2Network);

        break_oper_if(WSEC_FSEEK(pFile, 0, SEEK_SET) != 0, WSEC_LOG_E("fseek fail."), ulErrCode = WSEC_ERR_WRI_FILE_FAIL);
        ulErrCode = WSEC_WriteTlv(pFile, KMC_CFT_HDR, sizeof(KMC_KCF_HDR_STRU), pstHdr);
        break_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_WriteTlv()=%u", ulErrCode), oper_null);

        WSEC_FFLUSH(pFile);
    }do_end;
    
    WSEC_FCLOSE(pFile);
    WSEC_FREE(pstHdr);
    WSEC_BUFF_FREE(stBuff);

    /* Misinformation: FORTIFY.Race_Condition--File_System_Access */
    if (ulErrCode != WSEC_SUCCESS) {WSEC_UNCARE(WSEC_FREMOVE(pszKmcCfgFile));}
    return ulErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ChkCfgFile
 ��������  : ���KMC�����ļ��Ϸ���
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pstCtx: ��ȡKMC�����ļ�����������Ϣ.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ChkCfgFile(KMC_READ_KCF_CTX_STRU* pstCtx)
{
    WSEC_CHAR* pszCfgFile = pstCtx->pszFile;
    WSEC_FILE pFile = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BUFF stRead = {0};
    WSEC_BUFF stHash = {0};
    WSEC_TLV_STRU stTlv = {0};
    KMC_KCF_HDR_STRU* pstHdr = WSEC_NULL_PTR;

    pFile = WSEC_FOPEN(pszCfgFile, "rb");
    return_oper_if(!pFile, WSEC_LOG_E1("Cannot open '%s'", pszCfgFile), WSEC_ERR_OPEN_FILE_FAIL);

    WSEC_BUFF_ASSIGN(stRead, pstCtx->stReadBuff.pBuff, pstCtx->stReadBuff.nLen);

    do
    {
        /* ���ļ�ͷ */
        if (!WSEC_ReadTlv(pFile, stRead.pBuff, stRead.nLen, &stTlv, &nErrCode)) {break;}
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E2("Read TLV from '%s' = %u", pszCfgFile, nErrCode), nErrCode = WSEC_ERR_FILE_FORMAT);
        break_oper_if(stTlv.ulTag != KMC_CFT_HDR, WSEC_LOG_E1("'%s' is not the KMC-CFG file.", pszCfgFile), nErrCode = WSEC_ERR_FILE_FORMAT);
        break_oper_if(stTlv.ulLen != sizeof(KMC_KCF_HDR_STRU), WSEC_LOG_E1("'%s' is not the KMC-CFG file.", pszCfgFile), nErrCode = WSEC_ERR_FILE_FORMAT);
        
        pstHdr = (KMC_KCF_HDR_STRU*)stTlv.pVal;
        KMC_PRI_CvtByteOrder4KcfHdr(pstHdr, wbcNetwork2Host);

        /* ����ʽ��� */
        break_oper_if(WSEC_MEMCMP(pstHdr->abFormatFlag, g_KcfFlag, sizeof(g_KcfFlag)) != 0, WSEC_LOG_E1("'%s' is not the KMC-CFG file.", pszCfgFile), nErrCode = WSEC_ERR_FILE_FORMAT);

        /* ���汾�� */
        if (pstHdr->ulVer != KMC_KCF_VER)
        {
            WSEC_LOG_E1("The KMC-CFG version of '%s' is not correct.", pszCfgFile); /* ���������߽���汾����������. ���δ�����д */
            nErrCode = WSEC_FAILURE;
            break;
        }

        /* ��������� */
        WSEC_BUFF_ASSIGN(stHash, pstHdr->abHash, sizeof(pstHdr->abHash));
        nErrCode = WSEC_ChkFileIntegrity(pstHdr->ulHashAlgId, pFile, 0, &stHash);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E2("WSEC_ChkFileIntegrity('%s') = %u", pszCfgFile, nErrCode), oper_null);
    }do_end;

    if (WSEC_SUCCESS == nErrCode)
    {
        pstCtx->pFile = pFile;
    }
    else
    {
        WSEC_FCLOSE(pFile);
    }
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadKsfSafety
 ��������  : ��ȫ�ض�ȡKeystore����.
             ϵͳ֧��˫Keystore�ļ�, �����ȡ���ݹ��̳��ִ���, ���ȡ�����ļ�.
             1. ��ѡ����ȷ�����ļ�������;
             2. ���˫�ļ�����ȡ�쳣, ��ȡ��ȡ��MK������������.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ppKeystore: ָ��Keystore���ݽṹָ���ָ��.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ���������ɹ���ȡ����Keystore����д��ȫ�ֱ���g_pReystore

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ReadKsfSafety(KMC_KSF_MEM_STRU** ppKeystore)
{
    KMC_KSF_MEM_STRU* pRead[KMC_KSF_NUM]  = {WSEC_NULL_PTR};
    WSEC_INT32        aMkNum[KMC_KSF_NUM] = {-1};
    WSEC_SIZE_T i, nAimAt = 0;
    WSEC_BOOL bReadKsfOk = WSEC_FALSE, bReWriKeystore = WSEC_FALSE;
    KMC_KSF_RK_STRU *pRk = WSEC_NULL_PTR;
    WSEC_ERR_T nRet = WSEC_SUCCESS;

    WSEC_ASSERT(ppKeystore);

    /* 1. ������Դ */
    for (i = 0; i < KMC_KSF_NUM; i++)
    {
        pRead[i] = (KMC_KSF_MEM_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_MEM_STRU));
        break_oper_if(!pRead[i], WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_MEM_STRU)), nRet = WSEC_ERR_MALLOC_FAIL);
    }
    if (nRet != WSEC_SUCCESS) /* ��Դûȫ����ɹ�, ���ͷ� */
    {
        for (i = 0; i < KMC_KSF_NUM; i++)
        {
            WSEC_FREE(pRead[i]);
        }

        return nRet;
    }
    
    /* 2. ��ȡA, B�ļ� */
    for (i = 0; i < KMC_KSF_NUM; i++)
    {
        pRead[i]->pszFromFile = g_KmcSys.apszKeystoreFile[i];
        nRet = KMC_PRI_ReadKsf(g_KmcSys.apszKeystoreFile[i], pRead[i]);
        if (WSEC_SUCCESS == nRet)
        {
            aMkNum[i] = WSEC_MK_NUM_MAX; /* ��ȡ�ɹ�, �������Դ��ļ�����Ϊ׼ */
            bReadKsfOk = WSEC_TRUE;
            break;
        }

        if (WSEC_ERR_CANCEL_BY_APP == nRet) {goto ExitPort;}

        if (WSEC_ERR_KMC_READ_MK_FAIL == nRet)
        {
            aMkNum[i] = WSEC_ARR_GetCount(pRead[i]->arrMk); /* ��ȡMKʧ��, �Ѷ�ȡ���ֿ���. */
        }
    }

    /* 3. ȷ��ʹ���ķ�����: MK��������ʤ�� */
    nAimAt = 0;
    for (i = 1; i < KMC_KSF_NUM; i++)
    {
        if (aMkNum[nAimAt] < aMkNum[i])
        {
            nAimAt = i;
        }
    }

    /* 4. �����ȡ������ */
    do
    {
        if (aMkNum[nAimAt] > 0) /* Ŀ������ѡ��, �Դ�Ϊ׼. */
        {
            nRet = WSEC_SUCCESS;
            WSEC_LOG_E1("Used %s", g_KmcSys.apszKeystoreFile[nAimAt]);
            bReWriKeystore = !bReadKsfOk; /* ˵��Keystore�ļ�����, ��Ҫ��дKeystore */
        }
        else /* ��Ҫ����Keystore */
        {
            pRk = (KMC_KSF_RK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_RK_STRU));
            break_oper_if(!pRk, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_RK_STRU)), nRet = WSEC_ERR_MALLOC_FAIL);
            
            nAimAt = 0; /* ���ô���Դ����Keystore */
            WSEC_ASSERT(WSEC_ARR_IsEmpty(pRead[nAimAt]->arrMk)); /* ��֤����������BUG */
            nRet = KMC_PRI_CreateKsf(pRead[nAimAt], pRk);
            WSEC_LOG_E1("KMC_PRI_CreateKsf() = %u", nRet);
        }

        if (WSEC_SUCCESS == nRet)
        {
            KMC_PRI_FreeKsfSnapshot(*ppKeystore);
            *ppKeystore = pRead[nAimAt];
        }
        else
        {
            nAimAt = KMC_KSF_NUM + 1; /* �����ͷ���Դʱ���������� */
        }
        
        if (!bReadKsfOk) {WSEC_NOTIFY(WSEC_KMC_NTF_KEY_STORE_CORRUPT, WSEC_NULL_PTR, 0);}
        if (bReWriKeystore) {KMC_PRI_WriteKsfSafety(*ppKeystore, pRk);}
    }do_end;
    
ExitPort:

    /* 5. ��Դ�ͷ� */
    for (i = 0; i < KMC_KSF_NUM; i++)
    {
        if (i != nAimAt)
        {
            pRead[i] = KMC_PRI_FreeKsfSnapshot(pRead[i]);
        }
    }
   
    if (pRk) { WSEC_FREE(pRk); }

    return nRet;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadKsf
 ��������  : ��ȡָ��Keystore�ļ�(���KSF)�е�����
 �� �� ��  : pszKeystoreFile: Keystoreȫ�ļ���
 �� �� ��  : ���Keystore�е�����.
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS:          ��ȫ�ɹ�
             WSEC_ERR_KMC_READ_MK_FAIL: ��ȡMK���̲��ֳ���, pKeystoreData�����������,
                                    �Ѿ���ȡ��MK��Ȼ��ȷ�������pKeystoreData->pMkList, 
             ����:                  ��KSFʧ�ܵľ������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ReadKsf(const WSEC_CHAR* pszKeystoreFile, KMC_KSF_MEM_STRU* pKeystoreData)
{
    WSEC_FILE fKeystore = WSEC_NULL_PTR;
    KMC_KSF_RK_STRU* pRk = WSEC_NULL_PTR;
    KMC_KSF_MK_STRU stMkRead = {0};
    KMC_MEM_MK_STRU* pstMkRec = WSEC_NULL_PTR;
    WSEC_UINT32 ulRet = WSEC_SUCCESS;
    WSEC_UINT32 ulReadMkNum = 0;
    WSEC_BUFF aBuffs[4] = {{0},{0},{0},{0}}, stHmacRstOld = {0}, stHmacKey = {0};
    WSEC_BUFF stRmk = {0};
    WSEC_BUFF stCipherBuff = {0};
    WSEC_UINT32 i = 0;
    WSEC_PROGRESS_RPT_STRU* pstRptProgress = WSEC_NULL_PTR;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE;

    if (g_pKmcIniCtx) {pstRptProgress = &g_pKmcIniCtx->stProgressRpt;}

    WSEC_ASSERT(pszKeystoreFile && pKeystoreData);
    WSEC_ASSERT(WSEC_ARR_IsEmpty(pKeystoreData->arrMk));

    pRk = (KMC_KSF_RK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_RK_STRU));
    return_oper_if(!pRk, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_RK_STRU)), WSEC_ERR_MALLOC_FAIL);

    /* 1 ��Rootkey��Ϣ*/
    do
    {
        ulRet = (WSEC_UINT32)KMC_PRI_ReadRootKey(pszKeystoreFile, pRk);
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_ReadRootKey()=%u", ulRet), oper_null);

        if (pRk->stRkAttr.usVer != KMC_KSF_VER) /* �汾�Ų�ƥ��, ���ܰ��ձ������߼�����ȡ */
        {
            ulRet = (WSEC_UINT32)KMC_PRI_ReadKsfBasedVer(pszKeystoreFile, pRk->stRkAttr.usVer, pKeystoreData);
        }
    }do_end;

    if (ulRet != WSEC_SUCCESS)
    {
        WSEC_FREE(pRk);
        return ulRet;
    }

    WSEC_BUFF_ALLOC(stRmk, KMC_RMK_LEN * 2); /* ǰ�벿������EK, ��벿������IK */
    WSEC_BUFF_ALLOC(stCipherBuff, sizeof(stMkRead.stMkRear.abKey));

    do
    {
        break_oper_if(!stRmk.pBuff, WSEC_LOG_E4MALLOC(stRmk.nLen), ulRet = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stCipherBuff.pBuff, WSEC_LOG_E4MALLOC(stCipherBuff.nLen), ulRet = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(WSEC_MEMCPY(&(pKeystoreData->stRkInfo), sizeof(pKeystoreData->stRkInfo), &pRk->stRkAttr, sizeof(pRk->stRkAttr)) != EOK,
                      WSEC_LOG_E4MEMCPY, ulRet = WSEC_ERR_MEMCPY_FAIL);

        /* ����RMK */
        break_oper_if(!KMC_PRI_MakeRmk(pRk, &stRmk), WSEC_LOG_E("KMC_PRI_MakeRmk() fail."), ulRet = WSEC_ERR_PBKDF2_FAIL);

        /*lint -e668*/
        fKeystore = WSEC_FOPEN(pszKeystoreFile, "rb");
        break_oper_if(!fKeystore, WSEC_LOG_E1("Open '%s' fail.", pszKeystoreFile), ulRet = WSEC_ERR_OPEN_FILE_FAIL);

        break_oper_if(WSEC_FSEEK(fKeystore, sizeof(g_KsfFlag) + sizeof(KMC_KSF_RK_STRU), SEEK_SET) != 0, WSEC_LOG_E("fseek fail."), ulRet = WSEC_ERR_WRI_FILE_FAIL);
    }do_end;

    if (ulRet != WSEC_SUCCESS)
    {
        WSEC_FCLOSE(fKeystore);
		WSEC_BUFF_FREE_S(stRmk);
        WSEC_BUFF_FREE(stCipherBuff);
        WSEC_FREE(pRk);
        return ulRet;
    }
    
    /*---------------------------------------------------------------------------------------------------
    2 ��MK
    ˵��: ��ȡMK������, ���ڿ����Կ���, ����MK��¼��ȡʧ��, ��Ȼ������ȡ������MK.
    ---------------------------------------------------------------------------------------------------*/
    for (i = 0; i < pRk->ulMkNum; i++)
    {
        /* 2.1 ���������MK����Ľڵ� */
        if (!pstMkRec) {pstMkRec = (KMC_MEM_MK_STRU*)WSEC_MALLOC(sizeof(KMC_MEM_MK_STRU));}
        if (!pstMkRec)
        {
            WSEC_LOG_E4MALLOC(sizeof(KMC_MEM_MK_STRU));
            continue;
        }

        /* 2.2 �����ض���1��MK��¼ */
        /*lint -e802*/
        if (!WSEC_FREAD_MUST(&stMkRead, sizeof(stMkRead), fKeystore))
        {
            WSEC_LOG_E("WSEC_FREAD_MUST fali.");
            continue;
        }

        /* 2.3 �ֽ���ת�� */
        KMC_PRI_CvtByteOrder4KsfMk(&stMkRead, wbcNetwork2Host);

        /* 2.4 MK������У�� */
        /* 2.4.1 ����MK��Կ */
        if (WSEC_MEMCPY(stCipherBuff.pBuff, stCipherBuff.nLen, stMkRead.stMkRear.abKey, stMkRead.ulCipherLen) != EOK)
        {
            WSEC_LOG_E4MEMCPY;
            continue;
        }
		pstMkRec->stMkRear.ulPlainLen = WSEC_MK_LEN_MAX;
        if(CAC_Decrypt(KMC_ENCRYPT_MK_ALGID,
                       stRmk.pBuff, KMC_RMK_LEN,
                       stMkRead.abIv, sizeof(stMkRead.abIv),
                       stCipherBuff.pBuff, stMkRead.ulCipherLen,
                       pstMkRec->stMkRear.abKey, &pstMkRec->stMkRear.ulPlainLen) != WSEC_SUCCESS)
        {
            WSEC_LOG_E("CAC_Decrypt() fali.");
            continue;
        }

        /* 2.4.2 MK������У�� */
        WSEC_BUFF_ASSIGN(aBuffs[0], &stMkRead.stMkInfo, sizeof(stMkRead.stMkInfo));
        WSEC_BUFF_ASSIGN(aBuffs[1], stMkRead.abReserved, sizeof(stMkRead.abReserved));
        WSEC_BUFF_ASSIGN(aBuffs[2], &pstMkRec->stMkRear.ulPlainLen, sizeof(pstMkRec->stMkRear.ulPlainLen));
        WSEC_BUFF_ASSIGN(aBuffs[3], pstMkRec->stMkRear.abKey, pstMkRec->stMkRear.ulPlainLen);
        WSEC_BUFF_ASSIGN(stHmacKey, (WSEC_BYTE*)stRmk.pBuff + KMC_RMK_LEN, KMC_RMK_LEN); /* IK */
        WSEC_BUFF_ASSIGN(stHmacRstOld, stMkRead.abMkHash, sizeof(stMkRead.abMkHash)); /* HMAC code */
        if(WSEC_ChkHmacCode(KMC_HMAC_MK_ALGID, aBuffs, 4, &stHmacKey, &stHmacRstOld) != WSEC_SUCCESS)
        {
            WSEC_LOG_E("Above function return fail.");
            continue;
        }

        if(WSEC_MEMCPY(&(pstMkRec->stMkInfo), sizeof(pstMkRec->stMkInfo), &(stMkRead.stMkInfo), sizeof(stMkRead.stMkInfo)) != EOK)
        {
            WSEC_LOG_E4MEMCPY;
            continue;
        }

        /* 2.5 ��MK�������� */
        if(KMC_PRI_AddMk2Array(pKeystoreData, pstMkRec) == WSEC_SUCCESS)
        {
            pstMkRec = WSEC_NULL_PTR; /* Misinformation: FORTIFY.Memory_Leak(This branch means that 'pstMkRec' added ARRAY.) */
            ulReadMkNum++;
        }
        else
        {
            WSEC_LOG_E("KMC_PRI_AddMk2Array() fail.");
            continue;
        }

        WSEC_RptProgress(pstRptProgress, &stTimer, pRk->ulMkNum, ulReadMkNum, &bCancel);
        break_oper_if(bCancel, WSEC_LOG_E("App Canceled."), ulRet = WSEC_ERR_CANCEL_BY_APP);
    }
    if (ulReadMkNum > 0) {WSEC_ASSERT((WSEC_INT32)ulReadMkNum == WSEC_ARR_GetCount(pKeystoreData->arrMk));}
    if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, pRk->ulMkNum, pRk->ulMkNum, WSEC_NULL_PTR);} /* ȷ������100%�ϱ� */
    
    WSEC_FCLOSE(fKeystore);
    WSEC_FREE(pstMkRec);
    WSEC_BUFF_FREE(stCipherBuff);
    WSEC_BUFF_FREE_S(stRmk);

    if (pRk->ulMkNum != ulReadMkNum)
    {
        WSEC_LOG_E3("Read MK from key-store-file(%s) fail, The header define MKs = %d, but success read MKs = %d.", 
                    pszKeystoreFile, pRk->ulMkNum, ulReadMkNum);
        ulRet = WSEC_ERR_KMC_READ_MK_FAIL;
    }

    WSEC_FREE(pRk);

    return ulRet;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadRootKey
 ��������  : ��ȡKeystore�ļ��е�RootKey��Ϣ
 �� �� ��  : pszFile: Keystoreȫ�ļ���
 �� �� ��  : pRk: ���Keystore�е�RootKey
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         ʧ�ܵľ������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��05��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ReadRootKey(const WSEC_CHAR* pszFile, KMC_KSF_RK_STRU* pRk)
{
    WSEC_BYTE   abKsfFmtFlg[sizeof(g_KsfFlag)] = {0};
    WSEC_FILE fKeystore = WSEC_NULL_PTR;
    WSEC_BUFF aBuffs[1] = {{0}};
    WSEC_UINT32 ulRet = WSEC_SUCCESS;

    WSEC_ASSERT(pszFile && pRk);

    /*lint -e668*/
    fKeystore = WSEC_FOPEN(pszFile, "rb");
    
    do
    {
        /* 1) �����Դ�Ƿ���� */
        break_oper_if(!fKeystore, WSEC_LOG_E1("Open '%s' fail.", pszFile), ulRet = WSEC_ERR_OPEN_FILE_FAIL);

        /* 2) ��ȡ������ʽ�� */
        break_oper_if(!WSEC_FREAD_MUST(abKsfFmtFlg, sizeof(abKsfFmtFlg), fKeystore), 
                      WSEC_LOG_E1("Read File(%s) fail.", pszFile),
                      ulRet = WSEC_ERR_KMC_NOT_KSF_FORMAT);
        break_oper_if(WSEC_MEMCMP(abKsfFmtFlg, g_KsfFlag, sizeof(g_KsfFlag)) != 0,
                      WSEC_LOG_E1("%s is not KSF format.", pszFile),
                      ulRet = WSEC_ERR_KMC_NOT_KSF_FORMAT);

        /* 3) ��ȡRootKey��Ϣ */
        break_oper_if(!WSEC_FREAD_MUST(pRk, sizeof(KMC_KSF_RK_STRU), fKeystore), 
                      WSEC_LOG_E1("Read File(%s) fail.", pszFile),
                      ulRet = WSEC_ERR_KMC_NOT_KSF_FORMAT);

        /* 4) �ֽ���ת�� */
        KMC_PRI_CvtByteOrder4KsfRk(pRk, wbcNetwork2Host);

        /* 5) �ļ��汾�ż�� */
        if (pRk->stRkAttr.usVer != KMC_KSF_VER) {break;}

        /* 6) ���MK�����Ƿ��� */
        break_oper_if(pRk->ulMkNum > WSEC_MK_NUM_MAX, \
                      WSEC_LOG_E2("ulMkNum(%u) invalid.(MAX = %u)", pRk->ulMkNum, WSEC_MK_NUM_MAX), \
                      ulRet = WSEC_ERR_KMC_KSF_DATA_INVALID);

        /* 7) ���Hashֵ */
        WSEC_BUFF_ASSIGN(aBuffs[0], pRk, sizeof(KMC_KSF_RK_STRU) - sizeof(pRk->abAboveHash));

        ulRet = (WSEC_UINT32)WSEC_ChkIntegrity(WSEC_ALGID_SHA256, aBuffs, 1, pRk->abAboveHash, sizeof(pRk->abAboveHash));
        break_oper_if(ulRet != WSEC_SUCCESS, WSEC_LOG_E("The KSF's header integrity fail."), oper_null);
    }do_end;        
    WSEC_FCLOSE(fKeystore);
    
    return ulRet;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReadKsfBasedVer
 ��������  : ����ָ���汾�Ŷ�ȡָ��Keystore�ļ�(���KSF)�е�����
 �� �� ��  : pszKeystoreFile: Keystoreȫ�ļ���
             usVer:           ���ļ��İ汾��
 �� �� ��  : ���Keystore�е�����.
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS:          ��ȫ�ɹ�
             WSEC_ERR_KMC_READ_MK_FAIL: ��ȡMK���̲��ֳ���, pKeystoreData�����������,
                                    �Ѿ���ȡ��MK��Ȼ��ȷ�������pKeystoreData->pMkList, 
             ����:                  ��KSFʧ�ܵľ������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ReadKsfBasedVer(const WSEC_CHAR* pszKeystoreFile, WSEC_UINT16 usVer, KMC_KSF_MEM_STRU* pKeystoreData)
{
    WSEC_UNREFER(pszKeystoreFile);
    WSEC_UNREFER(usVer);
    WSEC_UNREFER(pKeystoreData);

    /* ��Keystore�ļ��汾�ű��ʱ, ��Ҫ����Աʵ�ָú��� */
    return WSEC_ERR_KMC_READ_DIFF_VER_KSF_FAIL;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_WriteKsfSafety
 ��������  : �ɿ��ؽ�����д��Keystore�ļ�
 �� �� ��  : pKeystoreData: ����д��Keystore�ļ�������
             pRk: ����Կ�����Ϣ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��02��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_WriteKsfSafety(const KMC_KSF_MEM_STRU* pKeystoreData, const KMC_KSF_RK_STRU* pRk)
{
    WSEC_ERR_T nErrCode;
    KMC_WRI_KSF_FAIL_NTF_STRU stRpt = {0};
    
    nErrCode = WSEC_WriteFileS(pKeystoreData, g_KmcSys.apszKeystoreFile, WSEC_NUM_OF(g_KmcSys.apszKeystoreFile), (WSEC_WriteFile)KMC_PRI_WriteKsf, pRk);
    if (nErrCode != WSEC_SUCCESS)
    {
        stRpt.ulCause = nErrCode;
        WSEC_NOTIFY(WSEC_KMC_NTF_WRI_KEY_STORE_FAIL, &stRpt, sizeof(stRpt));
    }
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_WriteKsf
 ��������  : дKeystore�ļ�
 �� �� ��  : pKeystoreData: ����д��Keystore�ļ�������
             pszFile:       Keystore�ļ���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��01��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_WriteKsf(const KMC_KSF_MEM_STRU* pKeystoreData, const WSEC_CHAR* pszFile, const KMC_KSF_RK_STRU* pRk)
{
    WSEC_FILE fKeystore = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    KMC_MEM_MK_STRU* pMemMk = WSEC_NULL_PTR;
    KMC_KSF_RK_STRU* pstRkWri = WSEC_NULL_PTR;
    KMC_KSF_MK_STRU* pstMkWri = WSEC_NULL_PTR;
    WSEC_BUFF stRmk = {0};
    WSEC_BUFF aBuffs[4] = {{0},{0},{0},{0}}, stHmacKey = {0}, stHmacRst = {0};
    WSEC_BUFF stPlainBuff = {0};
    WSEC_PROGRESS_RPT_STRU* pstRptProgress = WSEC_NULL_PTR;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_INT32 nMkNum, i;

    if (g_pKmcIniCtx) {pstRptProgress = &g_pKmcIniCtx->stProgressRpt;}

    /* 1. ȷ��RK��Ϣ���� */
    do
    {
        pstRkWri = (KMC_KSF_RK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_RK_STRU));
        break_oper_if(!pstRkWri, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_RK_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);

        if (pRk)
        {
            break_oper_if(WSEC_MEMCPY(pstRkWri, sizeof(KMC_KSF_RK_STRU), pRk, sizeof(KMC_KSF_RK_STRU)) != EOK, WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        }
        else
        {
            nErrCode = KMC_PRI_ReadRootKey(pKeystoreData->pszFromFile, pstRkWri);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_ReadRootKey() = %u", nErrCode), oper_null);
            break_oper_if(WSEC_MEMCPY(&pstRkWri->stRkAttr, sizeof(pstRkWri->stRkAttr), &pKeystoreData->stRkInfo, sizeof(pKeystoreData->stRkInfo)) != EOK, 
                          WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        }
    }do_end;
    if (nErrCode != WSEC_SUCCESS)
    {
        WSEC_FREE(pstRkWri);
        return nErrCode;
    }

    /* 2. дRootKey��MK */
    do
    {
        /*lint -e668*/
        fKeystore = WSEC_FOPEN(pszFile, "wb");
        break_oper_if(!fKeystore, WSEC_LOG_E1("Open file(%s) fail", pszFile), nErrCode = WSEC_ERR_OPEN_FILE_FAIL);

        WSEC_BUFF_ALLOC(stRmk, KMC_RMK_LEN * 2); /* ǰ�벿������EK, ��벿������IK */
        WSEC_BUFF_ALLOC(stPlainBuff, sizeof(pstMkWri->stMkRear.abKey));
        pstMkWri = (KMC_KSF_MK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_MK_STRU));

        break_oper_if(!stRmk.pBuff, WSEC_LOG_E4MALLOC(stRmk.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stPlainBuff.pBuff, WSEC_LOG_E4MALLOC(stPlainBuff.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pstMkWri, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_MK_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);

        pstRkWri->ulMkNum = (WSEC_UINT32)WSEC_ARR_GetCount(pKeystoreData->arrMk);

        /* RootKey��Ϣ��Hash */
        WSEC_BUFF_ASSIGN(aBuffs[0], pstRkWri, sizeof(KMC_KSF_RK_STRU) - sizeof(pstRkWri->abAboveHash));
        WSEC_BUFF_ASSIGN(aBuffs[1], pstRkWri->abAboveHash, sizeof(pstRkWri->abAboveHash));
        break_oper_if(!WSEC_CreateHashCode(WSEC_ALGID_SHA256, aBuffs, 1, &(aBuffs[1])),
                      WSEC_LOG_E("WSEC_CreateHashCode() fail"),
                      nErrCode = WSEC_ERR_GEN_HASH_CODE_FAIL);

        KMC_PRI_CvtByteOrder4KsfRk(pstRkWri, wbcHost2Network);
        
        /* ��RKд���ļ� */
        break_oper_if(!WSEC_FWRITE_MUST(g_KsfFlag, sizeof(g_KsfFlag), fKeystore),
                      WSEC_LOG_E1("Write file(%s) fail", pszFile),
                      nErrCode = WSEC_ERR_WRI_FILE_FAIL);
        break_oper_if(!WSEC_FWRITE_MUST(pstRkWri, sizeof(KMC_KSF_RK_STRU), fKeystore),
                      WSEC_LOG_E1("Write file(%s) fail", pszFile),
                      nErrCode = WSEC_ERR_WRI_FILE_FAIL);

        KMC_PRI_CvtByteOrder4KsfRk(pstRkWri, wbcNetwork2Host); /* �ֽ���ԭ, ��������Ҫʹ�� */

        /* ����RMK */
        break_oper_if(!KMC_PRI_MakeRmk(pstRkWri, &stRmk), WSEC_LOG_E("KMC_PRI_MakeRmk() fail."), nErrCode = WSEC_ERR_PBKDF2_FAIL);

        /* ����MKд���ļ� */
        nMkNum = WSEC_ARR_GetCount(pKeystoreData->arrMk);
        for (i = 0; i < nMkNum; i++)
        {
            pMemMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(pKeystoreData->arrMk, i);
            break_oper_if(!pMemMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            break_oper_if(WSEC_MEMCPY(&pstMkWri->stMkInfo, sizeof(pstMkWri->stMkInfo), &pMemMk->stMkInfo, sizeof(pMemMk->stMkInfo)) != EOK,
                          WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
            break_oper_if(WSEC_MEMCPY(&pstMkWri->stMkRear, sizeof(pstMkWri->stMkRear), &pMemMk->stMkRear, sizeof(pMemMk->stMkRear)) != EOK,
                          WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
            WSEC_UNCARE(CAC_Random(pstMkWri->abIv, sizeof(pstMkWri->abIv))); /* ����ʹ���������������IV */

            /* �洢�������е���Կ���Ĳ�������������, �Ǽ��ڴ����, ������Ҫ���� */
            KMC_UNMASK_MK_TO(pMemMk, pstMkWri->stMkRear.abKey, pstMkWri->stMkRear.ulPlainLen);

            /* ����MK��Hash */
            WSEC_BUFF_ASSIGN(aBuffs[0], &pstMkWri->stMkInfo, sizeof(pstMkWri->stMkInfo));
            WSEC_BUFF_ASSIGN(aBuffs[1], &pstMkWri->abReserved, sizeof(pstMkWri->abReserved));
            WSEC_BUFF_ASSIGN(aBuffs[2], &pstMkWri->stMkRear.ulPlainLen, sizeof(pstMkWri->stMkRear.ulPlainLen));
            WSEC_BUFF_ASSIGN(aBuffs[3], pstMkWri->stMkRear.abKey, pstMkWri->stMkRear.ulPlainLen);
            WSEC_BUFF_ASSIGN(stHmacRst, pstMkWri->abMkHash, sizeof(pstMkWri->abMkHash));
            WSEC_BUFF_ASSIGN(stHmacKey, (WSEC_BYTE*)stRmk.pBuff + KMC_RMK_LEN, KMC_RMK_LEN);
			
            /* ����HMAC */
            nErrCode = WSEC_CreateHmacCode(KMC_HMAC_MK_ALGID, aBuffs, 4, &stHmacKey, &stHmacRst);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E("Create HMAC fail."), oper_null);

            /* ��MK������RMK���� */
			pstMkWri->ulCipherLen = WSEC_MK_LEN_MAX;
            break_oper_if(WSEC_MEMCPY(stPlainBuff.pBuff, stPlainBuff.nLen, pstMkWri->stMkRear.abKey, pstMkWri->stMkRear.ulPlainLen) != EOK, 
                          WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
            break_oper_if(CAC_Encrypt(KMC_ENCRYPT_MK_ALGID,
                                       stRmk.pBuff, KMC_RMK_LEN,
                                       pstMkWri->abIv, sizeof(pstMkWri->abIv),
                                       stPlainBuff.pBuff, pstMkWri->stMkRear.ulPlainLen,
                                       pstMkWri->stMkRear.abKey, &pstMkWri->ulCipherLen) != WSEC_SUCCESS,
                           WSEC_LOG_E("CAC_Encrypt() fail."), nErrCode = WSEC_ERR_ENCRPT_FAIL);

            KMC_PRI_CvtByteOrder4KsfMk(pstMkWri, wbcHost2Network); /* д�ļ�ǰ�ֽ���ת������ */

            break_oper_if(!WSEC_FWRITE_MUST(pstMkWri, sizeof(KMC_KSF_MK_STRU), fKeystore),
                          WSEC_LOG_E1("Write file(%s) fail", pszFile),
                          nErrCode = WSEC_ERR_WRI_FILE_FAIL);
            WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)nMkNum, (WSEC_UINT32)(i + 1), WSEC_NULL_PTR);
        }
    } do_end;

    WSEC_FFLUSH(fKeystore);
    WSEC_FCLOSE(fKeystore);
    WSEC_BUFF_FREE_S(stRmk);
    WSEC_BUFF_FREE_S(stPlainBuff);
    WSEC_FREE(pstRkWri);
    WSEC_FREE(pstMkWri);
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CreateRootKey
 ��������  : ����Root Key�ļ�
 �� �� ��  : pstEntropy: ����Կ��
 �� �� ��  : pRkInfo: ����½�Root Key�����в���������
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CreateRootKey(const WSEC_BUFF* pstEntropy, KMC_KSF_RK_STRU* pRk)
{
    const WSEC_INT32 nRndNum = 2;
    KMC_RK_ATTR_STRU* pKeyAttr = WSEC_NULL_PTR;
    KMC_RK_PARA_STRU* pKeyPara = WSEC_NULL_PTR;
    WSEC_SIZE_T nOneMeteriaLen = 0;
    WSEC_BYTE* pbAllMeterial = WSEC_NULL_PTR;
    WSEC_BYTE abBase[2] = {0x01, 0x02};
    WSEC_BUFF aBuff[4] = {{0},{0},{0},{0}};
    WSEC_BUFF aWriHash[2] = {{0},{0}};
    WSEC_UINT32 ulBuffNum = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_INT32 i;
    
    WSEC_ASSERT(pRk);
    
    pKeyAttr = &pRk->stRkAttr;
    pKeyPara = &pRk->stRkPara;
    nOneMeteriaLen = sizeof(pKeyPara->abRkMeterial1);
    WSEC_ASSERT(sizeof(pKeyPara->abRkMeterial2) == nOneMeteriaLen);

    /* 1. ������Ϣ */
    pKeyAttr->usVer            = KMC_KSF_VER;
    pKeyAttr->usRkMeterialFrom = KMC_RK_GEN_BY_INNER;
    
    nErrCode = KMC_PRI_SetLifeTime(g_pKmcCfg->stRkCfg.ulRootKeyLifeDays, &pKeyAttr->stRkCreateTimeUtc, &pKeyAttr->stRkExpiredTimeUtc);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_SetLifeTime()=%u", nErrCode), nErrCode);
    return_oper_if(CAC_Random(pKeyPara->abRmkSalt, sizeof(pKeyPara->abRmkSalt)) != WSEC_SUCCESS, WSEC_LOG_E("CAC_Random() fail."), WSEC_ERR_GET_RAND_FAIL);
    pKeyAttr->ulRmkIterations = g_pKmcCfg->stRkCfg.ulRootMasterKeyIterations;

    /* 2. Keystore���2������Կ����
        ��2�������R1, R2���ⲿ��E, ��2������Կ����M1~M2�������㷨����:
        M1 = SHA256(0x1 + R1 + R2 + E)
        M2 = SHA256(0x2 + R1 + R2 + E)         */
    pbAllMeterial = (WSEC_BYTE*)WSEC_MALLOC(nRndNum * nOneMeteriaLen); /* �������� */
    return_oper_if(!pbAllMeterial, WSEC_LOG_E4MALLOC(nRndNum * nOneMeteriaLen), WSEC_ERR_MALLOC_FAIL);

    if (CAC_Random(pbAllMeterial, nRndNum * nOneMeteriaLen) != WSEC_SUCCESS)
    {
        WSEC_FREE(pbAllMeterial);
        WSEC_LOG_E("CAC_Random() fail.");
        return WSEC_ERR_GET_RAND_FAIL;
    }

    WSEC_BUFF_ASSIGN(aBuff[1], pbAllMeterial, nOneMeteriaLen);
    WSEC_BUFF_ASSIGN(aBuff[2], pbAllMeterial + nOneMeteriaLen, nOneMeteriaLen);
    if (pstEntropy) {WSEC_BUFF_ASSIGN(aBuff[3], pstEntropy->pBuff, pstEntropy->nLen);}
    ulBuffNum = 1 + nRndNum + (pstEntropy ? 1 : 0);

    WSEC_BUFF_ASSIGN(aWriHash[0], pKeyPara->abRkMeterial1, nOneMeteriaLen);
    WSEC_BUFF_ASSIGN(aWriHash[1], pKeyPara->abRkMeterial2, nOneMeteriaLen);

    for (i = 0; i < nRndNum; i++) /* �������ڸ���Կ�� nRndNum ������ */
    {
        WSEC_BUFF_ASSIGN(aBuff[0], &abBase[i], sizeof(abBase[i]));
        if (!WSEC_CreateHashCode(WSEC_ALGID_SHA256, aBuff, ulBuffNum, &aWriHash[i]))
        {
            WSEC_LOG_E("WSEC_CreateHashCode() fail.");
            nErrCode = WSEC_ERR_GEN_HASH_CODE_FAIL;
            break;
        }
    }

    WSEC_FREE(pbAllMeterial);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CreateMkArr
 ��������  : ����Master Key����
 �� �� ��  : ��
 �� �� ��  : pKeystore: �洢MK�����Keystore���ݽṹ
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CreateMkArr(KMC_KSF_MEM_STRU* pKeystore)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;
    KMC_DOMAIN_CFG_STRU* pDomainCfg = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU*   pKeyType   = WSEC_NULL_PTR;
    WSEC_UINT32 ulKeyId = 0;
    WSEC_INT32 i, j, nDomainNum;
    WSEC_PROGRESS_RPT_STRU* pstRptProgress = WSEC_NULL_PTR;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE;

    WSEC_ASSERT(pKeystore);

    if (g_pKmcIniCtx) {pstRptProgress = &g_pKmcIniCtx->stProgressRpt;}

    /* ����KMC����, ����MK */
    nDomainNum = WSEC_ARR_GetCount(g_pKmcCfg->arrDomainCfg);
    for (i = 0; i < nDomainNum; i++)
    {
        pDomainCfg = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_GetAt(g_pKmcCfg->arrDomainCfg, i);
        break_oper_if(!pDomainCfg, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
        continue_if(pDomainCfg->stDomainInfo.ucKeyFrom != KMC_MK_GEN_BY_INNER);

        ulKeyId = 0; /* KeyId��ÿ��Domain��Ψһ���� */
        for (j = 0; j < WSEC_ARR_GetCount(pDomainCfg->arrKeyTypeCfg); j++)
        {
            ulKeyId++;
            pKeyType = (KMC_CFG_KEY_TYPE_STRU*)WSEC_ARR_GetAt(pDomainCfg->arrKeyTypeCfg, j);
            break_oper_if(!pKeyType, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            nTemp = KMC_PRI_CreateMkItem(pKeystore, &pDomainCfg->stDomainInfo, pKeyType, WSEC_NULL_PTR, ulKeyId);
            continue_if(WSEC_SUCCESS == nTemp);

            nErrCode = nTemp;
            WSEC_LOG_E1("KMC_PRI_CreateMkItem()=%u", nTemp);
        }

        WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)nDomainNum, i + 1, &bCancel);
        break_oper_if(bCancel, WSEC_LOG_E("App Canceled."), nErrCode = WSEC_ERR_CANCEL_BY_APP);
    }
    if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, (WSEC_UINT32)nDomainNum, (WSEC_UINT32)nDomainNum, WSEC_NULL_PTR);} /* ȷ������100%�ϱ� */

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CreateMkItem
 ��������  : ��Keystore����MK
 �� �� ��  : pKeystore:  �洢MK��Keystore;
             pstDomain:  Domain������Ϣ;
             pstKeyType: KeyType����
             pKeyPlain:  ��Կ����(NULL����Ҫ�Զ�����)
             ulKeyId:    Key ID
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CreateMkItem(KMC_KSF_MEM_STRU* pKeystore, const KMC_CFG_DOMAIN_INFO_STRU* pstDomain, const KMC_CFG_KEY_TYPE_STRU* pstKeyType, const WSEC_BUFF* pKeyPlain, WSEC_UINT32 ulKeyId)
{
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    pMk = (KMC_MEM_MK_STRU*)WSEC_MALLOC(sizeof(KMC_MEM_MK_STRU));
    return_oper_if(!pMk, WSEC_LOG_E4MALLOC(sizeof(KMC_MEM_MK_STRU)), WSEC_ERR_MALLOC_FAIL);

    do
    {
        nErrCode = KMC_PRI_MakeMk(pstDomain, pstKeyType, pKeyPlain, ulKeyId, pMk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_MakeMk() = %u", nErrCode), oper_null);

        nErrCode = KMC_PRI_AddMk2Array(pKeystore, pMk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_AddMk2Array() = %u", nErrCode), oper_null);
    }do_end;

    if (WSEC_SUCCESS == nErrCode)
    {
        KMC_PRI_NtfMkChanged(&pMk->stMkInfo, KMC_KEY_ACTIVATED);
    }
    else
    {
        WSEC_FREE(pMk); /* ��������ɹ�, ���ڴ���Դ���������, ������Ҫ�ͷ� */
    }

    /* Misinformation: FORTIFY.Memory_Leak ('pMk' added to 'pKeystore' when success) */

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_ReCreateMkItem
 ��������  : ���´���MK
             1. ��������MK��ͬ���͵�MK;
             2. ����MK��״̬��Ϊ'������'״̬.
 �� �� ��  : pKeystore: Keystore���ݽṹ;
 �� �� ��  : ��
 ��γ���  :
             pMk:       ���ڵ�MK.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��17��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_ReCreateMkItem(KMC_KSF_MEM_STRU* pKeystore, KMC_MK_INFO_STRU* pMk)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    KMC_DOMAIN_CFG_STRU* pDomain = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU* pKeyType = WSEC_NULL_PTR;
    WSEC_UINT32 ulMaxKeyId = 0;

    WSEC_ASSERT(pKeystore && pMk);

    /* ֻ�ܶ��ڲ�MK���´��� */
    return_oper_if(pMk->ucGenStyle != KMC_MK_GEN_BY_INNER, WSEC_LOG_E2("Cannot Recreate external MK(DomainId=%d, KeyId=%d)", pMk->ulDomainId, pMk->ulKeyId), WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER);

    /* ������MK�������Ϣ */
    return_oper_if(!WSEC_GetUtcDateTime(&pMk->stMkExpiredTimeUtc), WSEC_LOG_E("WSEC_GetUtcDateTime() fail."), WSEC_ERR_GET_CURRENT_TIME_FAIL);
    pMk->ucStatus = KMC_KEY_STATUS_INACTIVE;
    WSEC_LOG_W3("The MK(DomainId=%u, KeyId=%u, KeyType=%u) expired, it's status become in-active.", pMk->ulDomainId, pMk->ulKeyId, pMk->usType);

    /* ����Կ״̬���, ֪ͨAPP, ��¼��־ */
    KMC_PRI_NtfMkChanged(pMk, KMC_KEY_INACTIVATED);

    return_oper_if(!KMC_PRI_SearchDomainKeyTypeCfg(pMk->ulDomainId, pMk->usType, &pDomain, &pKeyType), 
                   WSEC_LOG_E2("No KeyType found for the MK(DomainId=%u, KeyType=%u)", pMk->ulDomainId, pMk->usType),
                   WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS);

    /* ��ȡ���KeyId */
    nErrCode = KMC_PRI_GetMaxMkId(KMC_NOT_LOCK, pMk->ulDomainId, &ulMaxKeyId);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_GetMaxMkId()=%u", nErrCode), nErrCode);

    /* ���´���MK */
    return KMC_PRI_CreateMkItem(pKeystore, &pDomain->stDomainInfo, pKeyType, WSEC_NULL_PTR, ulMaxKeyId + 1);
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CreateKsf
 ��������  : ����Keystore�ļ�
 �� �� ��  : ��
 �� �� ��  : pKeystoreData, pRk: ����½�Keystore�����в���������
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CreateKsf(KMC_KSF_MEM_STRU* pKeystoreData, KMC_KSF_RK_STRU* pRk)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    WSEC_ASSERT(pKeystoreData && pRk);

    /* ����Root Key��MK���� */
    do
    {
        nErrCode = KMC_PRI_CreateRootKey((WSEC_BUFF*)WSEC_NULL_PTR, pRk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_CreateRootKey()=%u", nErrCode), oper_null);
        break_oper_if(WSEC_MEMCPY(&pKeystoreData->stRkInfo, sizeof(pKeystoreData->stRkInfo), &pRk->stRkAttr, sizeof(pRk->stRkAttr)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);

        nErrCode = KMC_PRI_CreateMkArr(pKeystoreData);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_CreateMasterKey()=%u", nErrCode), oper_null);
    } do_end;

    if (nErrCode != WSEC_SUCCESS) {return nErrCode;}

    /* ������д���ļ� */
    return KMC_PRI_WriteKsfSafety(pKeystoreData, pRk);
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeRmk
 ��������  : �������Կ������Կ, ���ڶ�MK���б���.
 �� �� ��  : pstRkInfo:  ����Կ��Ϣ
 �� �� ��  : pstRk:  ����Կ��Ϣ
 �� �� ��  : ��
 ��γ���  : pstBuff: ����ʱָ�����RMK���ڴ������䳤��(��), ���ʱ����RMK������ʵ����
 �� �� ֵ  : �ɹ���ʧ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_MakeRmk(const KMC_KSF_RK_STRU* pstRk, WSEC_BUFF* pstBuff)
{
    WSEC_BYTE abRkMeterial[sizeof(pstRk->stRkPara.abRkMeterial1)];
    WSEC_BYTE abMeterial3[sizeof(pstRk->stRkPara.abRkMeterial1)] = {0};
    const WSEC_BYTE *pMeterial1 = WSEC_NULL_PTR;
    const WSEC_BYTE *pMeterial2 = WSEC_NULL_PTR;
    const WSEC_BYTE *pMeterial3 = WSEC_NULL_PTR;
    WSEC_UINT32 i;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    WSEC_ASSERT(pstRk && pstBuff);
    WSEC_ASSERT((pstBuff->pBuff) && (pstBuff->nLen > 0));

    pMeterial1 = pstRk->stRkPara.abRkMeterial1;
    pMeterial2 = pstRk->stRkPara.abRkMeterial2;
    pMeterial3 = abMeterial3;
    KMC_PRI_GetRkMeterial(abMeterial3, sizeof(abMeterial3));
    
    for (i = 0; i < sizeof(abRkMeterial); i++, pMeterial1++, pMeterial2++, pMeterial3++)
    {
        abRkMeterial[i] = (WSEC_BYTE)((*pMeterial1) ^ (*pMeterial2) ^ (*pMeterial3));
    }

    nErrCode = CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256,
                          abRkMeterial, sizeof(abRkMeterial),
                          pstRk->stRkPara.abRmkSalt, sizeof(pstRk->stRkPara.abRmkSalt),
                          pstRk->stRkAttr.ulRmkIterations,
                          pstBuff->nLen, pstBuff->pBuff);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("CAC_Pbkdf2() = %u", nErrCode), WSEC_FALSE);
    
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_MakeMk
 ��������  : ����KMC���õ�Domain��KeyType��Ϣ����MK
 �� �� ��  : pstDomain:  Domain������Ϣ;
             pstKeyType: KeyType����
             pKeyPlain:  ��Կ����(NULL����Ҫ�Զ�����)
             ulKeyId:    Key ID
 �� �� ��  : pMk:        �����MK����
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_MakeMk(const KMC_CFG_DOMAIN_INFO_STRU* pstDomain,
                      const KMC_CFG_KEY_TYPE_STRU* pstKeyType, 
                      const WSEC_BUFF* pKeyPlain, 
                      WSEC_UINT32 ulKeyId, 
                      KMC_MEM_MK_STRU* pMk)
{
    KMC_MK_INFO_STRU* pMkMainInfo = WSEC_NULL_PTR;
    KMC_MK_REAR_STRU* pMkRear = WSEC_NULL_PTR;
    WSEC_ERR_T ulErrCode = WSEC_SUCCESS;

    WSEC_ASSERT(pstDomain && pstKeyType && pMk);

    pMkMainInfo = &pMk->stMkInfo;
    pMkRear = &pMk->stMkRear;
    
    /* 1) ������Կ����&����ʱ�� */
    ulErrCode = KMC_PRI_SetLifeTime(pstKeyType->ulKeyLifeDays, &pMkMainInfo->stMkCreateTimeUtc, &pMkMainInfo->stMkExpiredTimeUtc);
    return_oper_if(ulErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_SetLifeTime()=%u", ulErrCode), ulErrCode);
    
    /* 2) ��Կ */
    if (pKeyPlain)
    {
        return_oper_if(WSEC_MEMCPY(pMk->stMkRear.abKey, sizeof(pMk->stMkRear.abKey), pKeyPlain->pBuff, pKeyPlain->nLen) != EOK,
                       WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);
        pMkRear->ulPlainLen = pKeyPlain->nLen;
    }
    else
    {
        return_oper_if(CAC_Random(pMk->stMkRear.abKey, pstKeyType->ulKeyLen) != WSEC_SUCCESS, /* �����������Ϊ��Կ */
                       WSEC_LOG_E("CAC_Random() fail."), WSEC_ERR_GET_RAND_FAIL);
        pMkRear->ulPlainLen = pstKeyType->ulKeyLen;
    }

    /* 3) ������Ϣ */
    pMkMainInfo->ulDomainId   = pstDomain->ulId;
    pMkMainInfo->ulKeyId      = ulKeyId;
    pMkMainInfo->usType       = pstKeyType->usKeyType;
    pMkMainInfo->ucStatus     = KMC_KEY_STATUS_ACTIVE;
    pMkMainInfo->ucGenStyle   = pstDomain->ucKeyFrom;

    WSEC_LOG_W3("Create master Key(DomainId=%u, KeyId= %u, KeyType=%u) OK.", pstDomain->ulId, ulKeyId, pstKeyType->usKeyType);

    return ulErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_GetRkMeterial
 ��������  : ��ȡӲ�������Կ����
             Ϊ�˱�֤����Կ���ϵİ�ȫ��, �ܼ�3������֮�е�2���洢��Keystore,
             ��1������Ӳ������.
 �� �� ��  : nSize: pMeterialָ�򻺳�������
 �� �� ��  : pMeterial: �������Կ����.
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : �ú���ʵ�ֲ��ܸĶ�, ���򽫵��������ɵ�Keystore������!

 �޸���ʷ
  1.��    ��   : 2015��02��03��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���

  2.��    ��   : 2015��07��27��
    ��    ��   : z00118096
    �޸�����   : ����z��������, ��ARM����, �޸ķ�ʽΪȷ��zֵ���㲻�����.
                 �Ҽ����ϰ汾. ���Ӳ����жϣ���ֹx,y���--by l00171031
*****************************************************************************/
WSEC_VOID KMC_PRI_GetRkMeterial(WSEC_BYTE* pMeterial, WSEC_SIZE_T nSize)
{
    WSEC_SIZE_T i = 0;
    WSEC_INT32 x = 0, y = 0, z = 0, nType = 0;
    WSEC_BYTE aOffset[32] = {59, 5, 91, 0, 68, 5, 52, 64, 75, 73, 11, 0, 20, 69, 68, 32, 91, 69, 91, 73, 32, 88, 51, 37, 64, 24, 51, 69, 68, 32, 32, 32};
    WSEC_BYTE aType[32] = {0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};

    if( !pMeterial || nSize > WSEC_NUM_OF(g_KsfFlag) )
    {
    	WSEC_LOG_E1("The Rk meterial input is wrong, input length is %d .",  nSize);
		return;
    }
		
    for (i = 0; i < nSize; i++)
    {
        x = (i + 11) * (i + 29);
        y = (x + 17) * (x + 31);
        z = ((y % 29) + 23) * ((y % 29) + 37) + aOffset[i];

        nType = z % 3;
        if (aType[i] > 0) { nType = nType - 3; }

        x %= WSEC_NUM_OF(g_KsfFlag);
        y %= WSEC_NUM_OF(g_MkfFlag);
        z %= WSEC_NUM_OF(g_KcfFlag);

        if (0 == nType)
        {
            *(pMeterial + i)= (WSEC_BYTE)(g_KsfFlag[x] & (g_MkfFlag[y] + g_KcfFlag[z]));
        }
        else if (1 == nType)
        {
            *(pMeterial + i)= (WSEC_BYTE)(g_KsfFlag[x] * g_MkfFlag[y] - g_KcfFlag[z]);
        }
        else
        {
            *(pMeterial + i)= (WSEC_BYTE)(g_KsfFlag[x] + g_MkfFlag[y] * g_KcfFlag[z]);
        }
    }

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CalcMkPlainLenMax
 ��������  : ����MK���ĵ���󳤶�
             ����MK���ı����ܳ����WSEC_MK_LEN_MAX���ȵ����Ĵ洢��Keystore,
             Ϊ�˷�ֹ���ĳ���, ��Ҫ�����ĳ�������.
 �� �� ��  : ��
 �� �� ��  : pulLenMax: MK������󳤶�(�ֽ���)
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
  2.��    ��   : 2015��8��7��
    ��    ��   : z00118096
    �޸�����   : ������ĳ��ȹ̻�, ��DEBUG�汾���Թ̻�ֵ�Ƿ񳬴�.
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CalcMkPlainLenMax(WSEC_UINT32* pulLenMax)
{
    WSEC_UINT32 ulPlainLenMax = WSEC_MK_LEN_MAX - 1;
    *pulLenMax = ulPlainLenMax;
    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_GetMkDetail
 ��������  : ����DomainId, KeyId��ȡMK
 �� �� ��  : eLock: �Ƿ����
             ulDomainId, ulKeyId: MKΨһ���ID
 �� �� ��  : pstMkInfo: MK��������[�ɿ�]
             pbKeyPlainText: MK����
 ��γ���  : pKeyLen: [in]�����pbKeyPlainText�ĳ���;
                      [out]���pbKeyPlainText��ʵ�ʳ���.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : �ú���ֻ���Ÿ�CBB�ڲ�����

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_GetMkDetail(KMC_LOCK_OPER_ENUM eLock, WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen)
{
    WSEC_INT32 nIndex = -1;
    WSEC_INT32 nExpiredDays = 0;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    KMC_USE_EXPIRED_MK_NTF_STRU* pstMkNtf = WSEC_NULL_PTR;
    WSEC_SYSTIME_T stNowUtc = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("KMC_PRI_GetMkDetail", pbKeyPlainText && pKeyLen);

    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Lock(KMC_LOCK_BOTH);}
    do
    {
        nIndex = KMC_PRI_SearchMkByKeyId(g_pKeystore, ulDomainId, ulKeyId);
        break_oper_if(nIndex < 0, oper_null, nErrCode = WSEC_ERR_KMC_MK_MISS);

        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, nIndex);
        break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        if (pstMkInfo)
        {
            break_oper_if(WSEC_MEMCPY(pstMkInfo, sizeof(KMC_MK_INFO_STRU), &pMk->stMkInfo, sizeof(pMk->stMkInfo)) != EOK, 
                           WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        }
        break_oper_if(*pKeyLen < pMk->stMkRear.ulPlainLen,
                       WSEC_LOG_E2("*pKeyLen must at least given %d, but %d, so input-buff insufficient.", pMk->stMkRear.ulPlainLen, *pKeyLen),
                       nErrCode = WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);

        KMC_UNMASK_MK_TO(pMk, pbKeyPlainText, pMk->stMkRear.ulPlainLen);
        *pKeyLen = pMk->stMkRear.ulPlainLen;

        /* �����Կ�����ѳ�������, ��ͨ��(������Ϊ, ����Ӱ�캯����ȷ����) */
        break_oper_if(!WSEC_GetUtcDateTime(&stNowUtc), WSEC_LOG_E("Get UTC fail."), oper_null);
        nExpiredDays = WSEC_DateTimeDiff(dtpDay, &pMk->stMkInfo.stMkExpiredTimeUtc, &stNowUtc);
        break_oper_if(nExpiredDays < (WSEC_INT32)g_pKmcCfg->stKmCfg.ulGraceDaysForUseExpiredKey, oper_null, oper_null);
        pstMkNtf = WSEC_MALLOC(sizeof(KMC_USE_EXPIRED_MK_NTF_STRU));
        break_oper_if(!pstMkNtf, WSEC_LOG_E4MALLOC(sizeof(KMC_USE_EXPIRED_MK_NTF_STRU)), oper_null);
        pstMkNtf->nExpiredDays = nExpiredDays;
        if (WSEC_MEMCPY(&pstMkNtf->stExpiredMkInfo, sizeof(pstMkNtf->stExpiredMkInfo), &pMk->stMkInfo, sizeof(pMk->stMkInfo)) != EOK)
        {
            WSEC_FREE(pstMkNtf);
        }
    }do_end;
    if (KMC_NEED_LOCK == eLock) {KMC_PRI_Unlock(KMC_LOCK_BOTH);}

    if (pstMkNtf)
    {
        WSEC_NOTIFY(WSEC_KMC_NTF_USING_EXPIRED_MK, pstMkNtf, sizeof(KMC_USE_EXPIRED_MK_NTF_STRU));
        WSEC_FREE(pstMkNtf);
    }

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_IsTime4ChkKey
 ��������  : �жϵ�ǰʱ���Ƿ�ɶ���Կ��Ч�ڼ��
 �� �� ��  : pstCfg:    ���м����Ҫ��������Ϣ;
             pExecInfo: ���м������Ҫ��ʱ����Ϣ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��: �ɼ��, ��: ���ʱ��δ�����⿪�عر�.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��14��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_IsTime4ChkKey(const WSEC_SYSTIME_T* pstLocalNow, const KMC_CFG_KEY_MAN_STRU* pstCfg, WSEC_EXEC_INFO_STRU* pExecInfo)
{
    const WSEC_SCHEDULE_TIME_STRU* pstScheduleTime;
    WSEC_BOOL bChk = WSEC_FALSE;
    
    if (g_KmcSys.eState != WSEC_RUNNING) {return WSEC_FALSE;}
    if (pExecInfo->bUnconditionalExec) {return WSEC_TRUE;} /* ������������� */

    if (!pstCfg->bKeyAutoUpdate) {return WSEC_FALSE;}

    if (!WSEC_IsDateTime(&pExecInfo->stPreExecTime)) /* ��û���ϴμ���ʱ�� */
    {
        WSEC_DateTimeCopy(&pExecInfo->stPreExecTime, pstLocalNow); /* ϵͳ�������첻��� */
    }

    pstScheduleTime = &pstCfg->stAutoUpdateKeyTime;
    if (WSEC_IN_SCOPE(pstScheduleTime->ucWeek, 1, 7)) /* ÿ��x��h:m�ɼ�� */
    {
        if (pstScheduleTime->ucWeek != pstLocalNow->ucWeek) {return WSEC_FALSE;} /* δ����x, ����� */
    }

    /* ��������������, ����ʱ,�� */
    if (pstLocalNow->ucHour < pstScheduleTime->ucHour) {return WSEC_FALSE;} /* 'ʱ'δ��, ����� */
    if ((pstLocalNow->ucHour == pstScheduleTime->ucHour) && (pstLocalNow->ucMinute < pstScheduleTime->ucMinute)) {return WSEC_FALSE;} /* '��'δ��, ����� */

    bChk = (WSEC_DateTimeDiff(dtpSecond, &pExecInfo->stPreExecTime, pstLocalNow) >= 24*3600); /* ���ϴμ��ʱ���������1����, �ż�� */
    if (bChk) /* ��¼��������ʱ�� */
    {
        WSEC_DateTimeCopy(&pExecInfo->stPreExecTime, pstLocalNow);
    }

    pExecInfo->stPreExecTime.ucHour   = pstScheduleTime->ucHour;
    pExecInfo->stPreExecTime.ucMinute = pstScheduleTime->ucMinute; /* У��'ʱ', '��'��Ŀ���Ƿ�ֹ���Ⱦ��Ȳ���, ������������'ִ��'��ʱ��Խ��Խ�� */
    
    return bChk;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_SetLifeTime
 ��������  : ��������������ص�����&ʱ����Ϣ
 �� �� ��  : ulLifeDays: ��������(��)
 �� �� ��  : pstCreateUtc: ����ʱ��
             pstExpireUtc: ����ʱ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��5��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_SetLifeTime(WSEC_UINT32 ulLifeDays, WSEC_SYSTIME_T* pstCreateUtc, WSEC_SYSTIME_T* pstExpireUtc)
{
    return_oper_if((((WSEC_INT32)ulLifeDays) < 1), WSEC_LOG_E1("ulLifeDays(%u) too big.", ulLifeDays), WSEC_ERR_INVALID_ARG);

    if (!WSEC_GetUtcDateTime(pstCreateUtc)) {return WSEC_ERR_GET_CURRENT_TIME_FAIL;}
    WSEC_DateTimeAdd(pstCreateUtc, (WSEC_INT32)ulLifeDays, dtpDay, pstExpireUtc);
    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4KsfRk
 ��������  : KSF��RK��Ϣ�ֽ���ת��.
             ����KSF���ܻ�������ϵͳ, ��˴洢�ڽ����ϵ����ݾ�Ϊ������, ���뵽
             �ڴ棬��Ӧת��Ϊ������
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstRk: [in]�ֽ����ת������; [out]�ֽ���ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4KsfRk(KMC_KSF_RK_STRU* pstRk, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    KMC_RK_ATTR_STRU* pData = WSEC_NULL_PTR;
    
    WSEC_ASSERT(pstRk);
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));
    
    pData = &(pstRk->stRkAttr);

    pData->usVer            = WSEC_BYTE_ORDER_CVT_S(eOper, pData->usVer);
    pData->usRkMeterialFrom = WSEC_BYTE_ORDER_CVT_S(eOper, pData->usRkMeterialFrom);
    pData->ulRmkIterations  = WSEC_BYTE_ORDER_CVT_L(eOper, pData->ulRmkIterations);
    pstRk->ulMkNum          = WSEC_BYTE_ORDER_CVT_L(eOper, pstRk->ulMkNum);

    WSEC_CvtByteOrder4DateTime(&(pData->stRkCreateTimeUtc), eOper);
    WSEC_CvtByteOrder4DateTime(&(pData->stRkExpiredTimeUtc), eOper);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4KsfMk
 ��������  : KSF��MK��Ϣ�ֽ���ת��.
             ����KSF���ܻ�������ϵͳ, ��˴洢�ڽ����ϵ����ݾ�Ϊ������, ���뵽
             �ڴ棬��Ӧת��Ϊ������
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstMk: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4KsfMk(KMC_KSF_MK_STRU* pstMk, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(pstMk);
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstMk->stMkRear.ulPlainLen  = WSEC_BYTE_ORDER_CVT_L(eOper, pstMk->stMkRear.ulPlainLen);
    KMC_PRI_CvtByteOrder4MkInfo(&pstMk->stMkInfo, eOper);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4MkInfo
 ��������  : MK��Ϣ��������ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstMkInfo: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4MkInfo(KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstMkInfo->ulDomainId = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkInfo->ulDomainId);
    pstMkInfo->ulKeyId    = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkInfo->ulKeyId);
    pstMkInfo->usType     = WSEC_BYTE_ORDER_CVT_S(eOper, pstMkInfo->usType);

    WSEC_CvtByteOrder4DateTime(&(pstMkInfo->stMkCreateTimeUtc), eOper);
    WSEC_CvtByteOrder4DateTime(&(pstMkInfo->stMkExpiredTimeUtc), eOper);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4MkfHdr
 ��������  : MKF��ͷ��Ϣ�ֽ���ת��.
             ����MKF(MK����/���ļ�)���ܻ�������ϵͳ, ��˴洢�ڽ����ϵ����ݾ�Ϊ������, ���뵽
             �ڴ棬��Ӧת��Ϊ������
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstMkfHdr: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4MkfHdr(KMC_MKF_HDR_STRU* pstMkfHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstMkfHdr->usVer                 = WSEC_BYTE_ORDER_CVT_S(eOper, pstMkfHdr->usVer);
    pstMkfHdr->usKsfVer              = WSEC_BYTE_ORDER_CVT_S(eOper, pstMkfHdr->usKsfVer);
    pstMkfHdr->ulEncryptAlgId        = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulEncryptAlgId);
    pstMkfHdr->ulIteration4EncrypKey = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulIteration4EncrypKey);
    pstMkfHdr->ulHmacAlgId           = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulHmacAlgId);
    pstMkfHdr->ulIteration4HmacKey   = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulIteration4HmacKey);
    pstMkfHdr->ulCipherLenPerMk      = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulCipherLenPerMk);
    pstMkfHdr->ulMkNum               = WSEC_BYTE_ORDER_CVT_L(eOper, pstMkfHdr->ulMkNum);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4KcfHdr
 ��������  : KMC�����ļ�ͷ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstKcfHdr: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4KcfHdr(KMC_KCF_HDR_STRU* pstKcfHdr, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstKcfHdr->ulVer       = WSEC_BYTE_ORDER_CVT_L(eOper, pstKcfHdr->ulVer);
    pstKcfHdr->ulHashAlgId = WSEC_BYTE_ORDER_CVT_L(eOper, pstKcfHdr->ulHashAlgId);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4RkCfg
 ��������  : RootKey������Ϣ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstRkCfg: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4RkCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstRkCfg->ulRootKeyLifeDays         = WSEC_BYTE_ORDER_CVT_L(eOper, pstRkCfg->ulRootKeyLifeDays);
    pstRkCfg->ulRootMasterKeyIterations = WSEC_BYTE_ORDER_CVT_L(eOper, pstRkCfg->ulRootMasterKeyIterations);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4KeyManCfg
 ��������  : KeyMan������Ϣ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstKeyManCfg: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4KeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKeyManCfg, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstKeyManCfg->ulWarningBeforeKeyExpiredDays = WSEC_BYTE_ORDER_CVT_L(eOper, pstKeyManCfg->ulWarningBeforeKeyExpiredDays);
    pstKeyManCfg->ulGraceDaysForUseExpiredKey   = WSEC_BYTE_ORDER_CVT_L(eOper, pstKeyManCfg->ulGraceDaysForUseExpiredKey);
    pstKeyManCfg->bKeyAutoUpdate                = WSEC_BYTE_ORDER_CVT_L(eOper, (WSEC_UINT32)pstKeyManCfg->bKeyAutoUpdate);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4DataProtectCfg
 ��������  : ���ݱ���������Ϣ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstCfg: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4DataProtectCfg(KMC_CFG_DATA_PROTECT_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstCfg->ulAlgId         = WSEC_BYTE_ORDER_CVT_L(eOper, pstCfg->ulAlgId);
    pstCfg->usKeyType       = WSEC_BYTE_ORDER_CVT_S(eOper, pstCfg->usKeyType);
    pstCfg->bAppendMac      = WSEC_BYTE_ORDER_CVT_L(eOper, (WSEC_UINT32)pstCfg->bAppendMac);
    pstCfg->ulKeyIterations = WSEC_BYTE_ORDER_CVT_L(eOper, pstCfg->ulKeyIterations);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4DomainInfo
 ��������  : Domain������Ϣ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstCfg: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4DomainInfo(KMC_CFG_DOMAIN_INFO_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstCfg->ulId = WSEC_BYTE_ORDER_CVT_L(eOper, pstCfg->ulId);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CvtByteOrder4KeyType
 ��������  : KeyType������Ϣ�ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstCfg: [in]�ֽ����ת������; [out]�ֽ���ת���������.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��30��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_CvtByteOrder4KeyType(KMC_CFG_KEY_TYPE_STRU* pstCfg, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstCfg->usKeyType     = WSEC_BYTE_ORDER_CVT_S(eOper, pstCfg->usKeyType);
    pstCfg->ulKeyLen      = WSEC_BYTE_ORDER_CVT_L(eOper, pstCfg->ulKeyLen);
    pstCfg->ulKeyLifeDays = WSEC_BYTE_ORDER_CVT_L(eOper, pstCfg->ulKeyLifeDays);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_NtfWriCfgFileFail
 ��������  : ͨ��APP: д�����ļ�ʧ��
 �� �� ��  : ulCause: ʧ��ԭ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_NtfWriCfgFileFail(WSEC_ERR_T ulCause)
{
    KMC_WRI_KCF_FAIL_NTF_STRU stRpt = {0};
    
    stRpt.ulCause = ulCause;
    WSEC_NOTIFY(WSEC_KMC_NTF_WRI_CFG_FILE_FAIL, &stRpt, sizeof(stRpt));

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_NtfMkChanged
 ��������  : ͨ��APP: MK�����˱仯.
 �� �� ��  : pMk:   �仯�˵�MK��Ϣ
             eType: ����仯����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��5��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_NtfMkChanged(const KMC_MK_INFO_STRU* pMk, KMC_KEY_CHANGE_TYPE_ENUM eType)
{
    KMC_MK_CHANGE_NTF_STRU stNtfData = {0};

    if (WSEC_MEMCPY(&stNtfData.stMkInfo, sizeof(stNtfData.stMkInfo), pMk, sizeof(KMC_MK_INFO_STRU)) != EOK)
    {
        WSEC_LOG_E4MEMCPY;
        return;
    }
    
    stNtfData.eType = eType;
    WSEC_NOTIFY(WSEC_KMC_NTF_MK_CHANGED, &stNtfData, sizeof(stNtfData));

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_NtfRkExpire
 ��������  : ͨ��APP: RK��������.
 �� �� ��  : pRk:             RK������Ϣ
             nRemainLifeDays: ��ʣ���������
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��5��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_NtfRkExpire(const KMC_RK_ATTR_STRU* pRk, WSEC_INT32 nRemainLifeDays)
{
    KMC_RK_EXPIRE_NTF_STRU stNtfData = {0};

    if (WSEC_MEMCPY(&stNtfData.stRkInfo, sizeof(stNtfData.stRkInfo), pRk, sizeof(KMC_RK_ATTR_STRU)) != EOK)
    {
        WSEC_LOG_E4MEMCPY;
        return;
    }

    stNtfData.nRemainDays = nRemainLifeDays;
    WSEC_NOTIFY(WSEC_KMC_NTF_RK_EXPIRE, &stNtfData, sizeof(stNtfData));

    return;
}

/*****************************************************************************
 �� �� ��  : ����KMC��������
 ��������  : ������Դ����ȡKMC���á�Keystore����.
 �� �� ��  : ��
 �� �� ��  : ppKmcCfg: פ�����ڴ��е�KMC����
             ppKeystore: פ�����ڴ��е�Keystore
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��07��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_LoadData(KMC_CFG_STRU** ppKmcCfg, KMC_KSF_MEM_STRU** ppKeystore, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, const KMC_FP_CFG_CALLBACK_STRU* pstCfgFun, WSEC_BOOL bCbbManCfg)
{
    WSEC_ERR_T nRet = WSEC_SUCCESS, nTemp;

    /* 1 ��ȡKMC���� */
    *ppKmcCfg = (KMC_CFG_STRU*)WSEC_MALLOC(sizeof(KMC_CFG_STRU));
    do
    {
        break_oper_if(!g_KmcSys.apszKeystoreFile[0], WSEC_LOG_E("Allocate memory fail."), nRet = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!g_KmcSys.apszKeystoreFile[1], WSEC_LOG_E("Allocate memory fail."), nRet = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!(*ppKmcCfg), WSEC_LOG_E("Allocate memory fail."), nRet = WSEC_ERR_MALLOC_FAIL);

        nRet = CAC_Random(g_KmcSys.abMkMaskCode, sizeof(g_KmcSys.abMkMaskCode));
        break_oper_if(nRet != WSEC_SUCCESS, WSEC_LOG_E1("CAC_Random()=%u", nRet), nRet = WSEC_ERR_GET_RAND_FAIL);

        if (bCbbManCfg)
        {
            break_oper_if(!g_KmcSys.apszKmcCfgFile[0], WSEC_LOG_E("Allocate memory fail."), nRet = WSEC_ERR_MALLOC_FAIL);
            break_oper_if(!g_KmcSys.apszKmcCfgFile[1], WSEC_LOG_E("Allocate memory fail."), nRet = WSEC_ERR_MALLOC_FAIL);
        }
        
        nRet = KMC_PRI_OpenIniCtx(bCbbManCfg);
        if (nRet != WSEC_SUCCESS) /* ��������Ϣ����ʧ��, �����Լ�������. */
        {
            WSEC_LOG_E1("KMC_PRI_OpenIniCtx() = %u", nRet);
            WSEC_NOTIFY(WSEC_KMC_NTF_CFG_FILE_CORRUPT, WSEC_NULL_PTR, 0);
        }

        if (g_pKmcIniCtx && pstRptProgress)
        {
            g_pKmcIniCtx->stProgressRpt.pfRptProgress = pstRptProgress->pfRptProgress;
            g_pKmcIniCtx->stProgressRpt.ulTag         = pstRptProgress->ulTag;
        }

        /* ��ʼ��KMC���� */
        nTemp = KMC_PRI_CfgDataInit(pstCfgFun, *ppKmcCfg);
        nRet = ((nRet != WSEC_SUCCESS) && (nTemp == WSEC_SUCCESS)) ? KMC_PRI_WriteKcfSafety(*ppKmcCfg) : nTemp; /* ����ȡʧ�ܶ���ȡȱʡ����, ���д�ļ� */
    } do_end;
    KMC_PRI_CloseIniCtx();

    /* 2 ��Keystore */
    do
    {
        if_oper(nRet != WSEC_SUCCESS, break);

        /* ��Keystore */
        nRet = KMC_PRI_ReadKsfSafety(ppKeystore);

        break_oper_if(nRet != WSEC_SUCCESS, WSEC_LOG_E1("[KMC] KMC_PRI_ReadKsfSafety() = %u.", nRet), oper_null);
    } do_end;

    return nRet;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CfgDataInit
 ��������  : ��KMC�������ݳ�ʼ��: ͨ���ص�������APP��ȡKMC����, ���ĳ�����û�
             ȡʧ��, ��ʹ��ȱʡ����.
 �� �� ��  : pCallbackFun: ���ֻص�����
 �� �� ��  : pKmcCfg:      ��ȡ��������
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_CfgDataInit(const KMC_FP_CFG_CALLBACK_STRU* pCallbackFun, KMC_CFG_STRU* pKmcCfg)
{
    WSEC_BUFF stBuff = {0};
    KMC_CFG_DOMAIN_INFO_STRU* pstDomainInfo = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU* pstKeyType4Read = WSEC_NULL_PTR;
    KMC_DOMAIN_CFG_STRU* pstDomainCfg = WSEC_NULL_PTR;
    KMC_CFG_ROOT_KEY_STRU* pstRkCfg = WSEC_NULL_PTR;
    KMC_CFG_KEY_MAN_STRU* pstKmCfg = WSEC_NULL_PTR;
    KMC_CFG_DATA_PROTECT_STRU* pDataProtectCfg = WSEC_NULL_PTR;
    KMC_SDP_ALG_TYPE_ENUM eAlgType[] = {SDP_ALG_ENCRPT, SDP_ALG_INTEGRITY, SDP_ALG_PWD_PROTECT};
    WSEC_SPEND_TIME_STRU stTimer = {0}, *pstTimer = WSEC_NULL_PTR;
    WSEC_UINT32 ulNum = 0, ulDomainNum = 0;
    WSEC_UINT32 ulDomainId;
    WSEC_UINT32 i, j;
    WSEC_PROGRESS_RPT_STRU* pstRptProgress = WSEC_NULL_PTR;
    WSEC_ERR_T nRet = WSEC_SUCCESS;
    WSEC_BOOL bReadOk = WSEC_FALSE;
    WSEC_BOOL bCancel = WSEC_FALSE;

    WSEC_TRACE("KMC_PRI_CfgDataInit() begin:");
    WSEC_ASSERT(pCallbackFun && pKmcCfg);
    WSEC_ASSERT(WSEC_ARR_IsEmpty(pKmcCfg->arrDomainCfg)); /* ȷ��Domain����Ϊ�� */
    WSEC_ASSERT(WSEC_NUM_OF(eAlgType) == WSEC_NUM_OF(pKmcCfg->astDataProtectCfg));

    if (g_pKmcIniCtx) {pstRptProgress = &g_pKmcIniCtx->stProgressRpt;}

    /* 1. RootKey���� */
    if (KMC_IS_INNER_CREATE_RK)
    {
        pstRkCfg = &pKmcCfg->stRkCfg;
        bReadOk = pCallbackFun->pfReadRootKeyCfg(pstRkCfg);
        if (bReadOk) {bReadOk = KMC_CFG_IS_ROOT_KEY_VALID(pstRkCfg);} /* �������ݺϷ��� */
        if (!bReadOk)
        {
            WSEC_LOG_E("Callback to get RootKey CFG fail. so default CFG used.");
            KMC_PRI_MakeDefaultCfg4RootKey(pstRkCfg);
        }
    }

    /* 2. ��Կ�������ڹ������ */
    pstKmCfg = &pKmcCfg->stKmCfg;
    bReadOk = pCallbackFun->pfReadKeyManCfg(pstKmCfg);
    if (bReadOk) {bReadOk = KMC_CFG_IS_KEY_MAN_VALID(pstKmCfg);}/* �������ݺϷ��� */
    if (!bReadOk)
    {
        WSEC_LOG_E("Callback to get Key-Management CFG fail. so default CFG used.");
        KMC_PRI_MakeDefaultCfg4KeyMan(pstKmCfg);
    }
    
    /* 3. DataProtection���� */
    pDataProtectCfg = pKmcCfg->astDataProtectCfg;
    for (i = 0; i < WSEC_NUM_OF(eAlgType); i++, pDataProtectCfg++)
    {
        bReadOk = pCallbackFun->pfReadCfgOfDataProtection(eAlgType[i], pDataProtectCfg);
        if (bReadOk) /* ������ݺϷ��� */
        {
            bReadOk = (KMC_PRI_ChkProtectCfg(eAlgType[i], pDataProtectCfg) == WSEC_SUCCESS);
        }
        if_oper(!bReadOk, KMC_PRI_MakeDefaultCfg4DataProtect(eAlgType[i], pDataProtectCfg));
    }

    /* 4. Domain���� */
    if (!pCallbackFun->pfReadCfgOfDomainCount(&ulNum)) {ulNum = 0;}
    if (0 == ulNum) /* ʹ��ȱʡDomain����KeyType���� */
    {
        WSEC_LOG_E("Callback to get DomainNum fail. so default config used.");
        return_oper_if(!KMC_PRI_MakeDefaultCfg4Domain(pKmcCfg), WSEC_LOG_E("Make DefaultCfg4Domain fail."), WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL);
        return WSEC_SUCCESS;
    }

    return_oper_if(ulNum > WSEC_DOMAIN_NUM_MAX, 
                   WSEC_LOG_E2("Callbacked invalid DomainNum(%u), it cannot over %d.", ulNum, WSEC_DOMAIN_NUM_MAX), 
                   WSEC_ERR_KMC_DOMAIN_NUM_OVERFLOW);

    WSEC_BUFF_ALLOC(stBuff, (WSEC_SIZE_T)(sizeof(KMC_CFG_DOMAIN_INFO_STRU) * ulNum));
    return_oper_if(!stBuff.pBuff, WSEC_LOG_E4MALLOC(stBuff.nLen), WSEC_ERR_MALLOC_FAIL);

    do
    {
        pstDomainInfo = (KMC_CFG_DOMAIN_INFO_STRU*)stBuff.pBuff;
        break_oper_if(!pCallbackFun->pfReadCfgOfDomainInfo(pstDomainInfo, ulNum), WSEC_LOG_E("Callback to get all DomainInfo fail."), nRet = WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL);

        /* ��Domain�������� */
        for (i = 0; i < ulNum; i++, pstDomainInfo++)
        {
            /* ����������ݺϷ��� */
            break_oper_if(!KMC_IS_VALID_MK_FROM(pstDomainInfo->ucKeyFrom),
                          WSEC_LOG_E1("Callback to got invalid domain-cfg.keyfrom(%d).", pstDomainInfo->ucKeyFrom),
                          nRet = WSEC_ERR_KMC_KMCCFG_INVALID);
            nRet = KMC_PRI_AddDomain2Array(pKmcCfg, pstDomainInfo);
            break_oper_if(nRet != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_AddDomain2Array()=%u", nRet), oper_null);

            pstTimer = (i != (ulNum - 1)) ? &stTimer : WSEC_NULL_PTR;
            WSEC_RptProgress(pstRptProgress, pstTimer, ulNum, i + 1, &bCancel);
            break_oper_if(bCancel, WSEC_LOG_E("App Canceled."), nRet = WSEC_ERR_CANCEL_BY_APP);
        }
        break_oper_if(nRet != WSEC_SUCCESS, oper_null, oper_null);

        WSEC_ASSERT(WSEC_ARR_GetCount(pKmcCfg->arrDomainCfg) == (WSEC_INT32)ulNum); /* ��֤���������BUG */
    }do_end;
    WSEC_BUFF_FREE(stBuff); /* �ͷ����ڻ�ȡDomain���õĻ����� */

    /* ���������ȡDomain����ʧ��, ���ͷ�������Դ */
    if (nRet != WSEC_SUCCESS)
    {
        WSEC_ARR_RemoveAll(pKmcCfg->arrDomainCfg);
        return nRet;
    }

    /* 5. Domain KeyType */
    ulDomainNum = WSEC_ARR_GetCount(pKmcCfg->arrDomainCfg);
    for (i = 0; (WSEC_INT32)i < ulDomainNum; i++)
    {
        pstDomainCfg = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_GetAt(pKmcCfg->arrDomainCfg, i);
        break_oper_if(!pstDomainCfg, WSEC_LOG_E("memory access fail."), nRet = WSEC_ERR_OPER_ARRAY_FAIL);
        ulDomainId = pstDomainCfg->stDomainInfo.ulId;
        
        /* 1) ��ȡ��Domain��KeyType���� */
        break_oper_if(!pCallbackFun->pfReadCfgOfDomainKeyTypeCount(ulDomainId, &ulNum), \
                       WSEC_LOG_E1("Callback to get DomainKeyTypeCount(DomainId = %u) fail.", ulDomainId), \
                       nRet = WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL);
        break_oper_if(ulNum > WSEC_DOMAIN_KEY_TYPE_NUM_MAX, \
                      WSEC_LOG_E3("Callback Domain key-type-num(%u) invalid(max-num = %d) for DomainId = %u", ulNum, WSEC_DOMAIN_KEY_TYPE_NUM_MAX, ulDomainId), \
                      nRet = WSEC_ERR_KMC_KEYTYPE_NUM_OVERFLOW);
        continue_if(0 == ulNum);

        /* 2) �������ڻ�ȡ��Domain������KeyType�Ļ����� */
        WSEC_BUFF_ALLOC(stBuff, sizeof(KMC_CFG_KEY_TYPE_STRU) * ulNum);
        break_oper_if(!stBuff.pBuff, WSEC_LOG_E4MALLOC(stBuff.nLen), nRet = WSEC_ERR_MALLOC_FAIL);

        /* 3) ��ȡ��Domain������KeyType */
        pstKeyType4Read = (KMC_CFG_KEY_TYPE_STRU*)stBuff.pBuff;
        break_oper_if(!pCallbackFun->pfReadCfgOfDomainKeyType(ulDomainId, pstKeyType4Read, ulNum), \
                      WSEC_LOG_E1("Callback to read DomainKeyType(for domain-id = %u) fail.", ulDomainId), \
                      nRet = WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL);

        /* 4) ��KeyType�������� */
        for (j = 0; j < ulNum; j++, pstKeyType4Read++)
        {
            break_oper_if(!KMC_IS_VALID_KEY_TYPE(pstKeyType4Read->usKeyType),
                          WSEC_LOG_E1("Callback to read invalid KeyType(usKeyType=%u)", pstKeyType4Read->usKeyType), 
                          nRet = WSEC_ERR_KMC_KMCCFG_INVALID);
            break_oper_if(!KMC_IS_VALID_KEY_TYPE_LEN(pstKeyType4Read->ulKeyLen), 
                          WSEC_LOG_E2("Callback to read invalid KeyType Len(%u), it must between[1, %u]", pstKeyType4Read->ulKeyLen, g_KmcSys.ulMkPlainLenMax),
                          nRet = WSEC_ERR_KMC_KMCCFG_INVALID);
            break_oper_if(pstKeyType4Read->ulKeyLifeDays < 1, 
                          WSEC_LOG_E("Callback to read invalid KeyType(ulKeyLifeDays must bigger than 0)"),
                          nRet = WSEC_ERR_KMC_KMCCFG_INVALID);

            nRet = KMC_PRI_AddDomainKeyType2Array(pKmcCfg, ulDomainId, pstKeyType4Read);
            break_oper_if(nRet != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_AddDomainKeyType2Array()=%u", nRet), oper_null);
        }
        break_oper_if(nRet != WSEC_SUCCESS, oper_null, oper_null);
        WSEC_BUFF_FREE(stBuff);

        WSEC_ASSERT((WSEC_INT32)ulNum == WSEC_ARR_GetCount(pstDomainCfg->arrKeyTypeCfg)); /* ��֤���������BUG */

        pstTimer = (i != (ulDomainNum - 1)) ? &stTimer : WSEC_NULL_PTR;
        WSEC_RptProgress(pstRptProgress, pstTimer, ulDomainNum, i + 1, &bCancel);
        break_oper_if(bCancel, WSEC_LOG_E("APP Canceled."), nRet = WSEC_ERR_CANCEL_BY_APP);
    }
    WSEC_BUFF_FREE(stBuff);

    if (WSEC_SUCCESS == nRet) 
    {
        if (WSEC_ARR_IsEmpty(pKmcCfg->arrDomainCfg)) /* û��Domain����, �����ȱʡֵ */
        {
            nRet = KMC_PRI_MakeDefaultCfg4Domain(pKmcCfg) ? WSEC_SUCCESS : WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL;
        }
    }
    else /* ��������ʧ��, ����Ҫ�ͷ�����������Դ */
    {
        WSEC_ARR_RemoveAll(pKmcCfg->arrDomainCfg);
    }

    WSEC_TRACE("KMC_PRI_CfgDataInit() End.");
    return nRet;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_AddDomain2Array
 ��������  : ��KMC������Ϣ�����Domain��Ϣ
 �� �� ��  : pDomainInfo: Domain������Ϣ
 �� �� ��  : ��
 ��γ���  : pKmcCfg: KMC��������
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ����DomainԪ��ʱ, ȷ����DomainId��������

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_AddDomain2Array(KMC_CFG_STRU* pKmcCfg, const KMC_CFG_DOMAIN_INFO_STRU* pDomainInfo)
{
    KMC_DOMAIN_CFG_STRU* pElement = WSEC_NULL_PTR;
    WSEC_INT32 nNum;
    WSEC_ERR_T nRetCode = WSEC_SUCCESS;

    WSEC_ASSERT(pKmcCfg && pDomainInfo);

    if (pKmcCfg->arrDomainCfg)
    {
        nNum = WSEC_ARR_GetCount(pKmcCfg->arrDomainCfg);
        return_oper_if(WSEC_DOMAIN_NUM_MAX == nNum,
                       WSEC_LOG_E2("DomainNum(%d) cannot over %u", nNum, WSEC_ERR_KMC_DOMAIN_NUM_OVERFLOW), 
                       WSEC_ERR_KMC_DOMAIN_NUM_OVERFLOW);
    }
    else
    {
        pKmcCfg->arrDomainCfg = WSEC_ARR_Initialize(0, 0, KMC_PRI_CompareDomain4Arr, KMC_PRI_OnRemoveDomainArr);
        return_oper_if(!pKmcCfg->arrDomainCfg, WSEC_LOG_E("WSEC_ARR_Initialize() fail."), WSEC_ERR_OPER_ARRAY_FAIL);
    }

    /* ��������������Ԫ�� */
    pElement = (KMC_DOMAIN_CFG_STRU*)WSEC_MALLOC(sizeof(KMC_DOMAIN_CFG_STRU));
    return_oper_if(!pElement, WSEC_LOG_E4MALLOC(sizeof(KMC_DOMAIN_CFG_STRU)), WSEC_ERR_MALLOC_FAIL);

    do
    {
        pElement->arrKeyTypeCfg = WSEC_ARR_Initialize(0, 0, KMC_PRI_CompareDomainKeyType4Arr, WSEC_ARR_StdRemoveElement);
        break_oper_if(!pElement->arrKeyTypeCfg, WSEC_LOG_E("WSEC_ARR_Initialize() fail."), nRetCode = WSEC_ERR_OPER_ARRAY_FAIL);
        break_oper_if(WSEC_MEMCPY(&pElement->stDomainInfo, sizeof(pElement->stDomainInfo), pDomainInfo, sizeof(KMC_CFG_DOMAIN_INFO_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nRetCode = WSEC_ERR_MEMCPY_FAIL);

        /* ���Ψһ�� */
        break_oper_if(WSEC_ARR_BinarySearch(pKmcCfg->arrDomainCfg, pElement), 
                      WSEC_LOG_E1("The Domain(ID=%u) already existed.", pDomainInfo->ulId), nRetCode = WSEC_ERR_KMC_ADD_REPEAT_DOMAIN);

        /* �������� */
        break_oper_if(WSEC_ARR_AddOrderly(pKmcCfg->arrDomainCfg, pElement) < 0, 
                      WSEC_LOG_E("WSEC_ARR_AddOrderly() fail."), nRetCode = WSEC_ERR_OPER_ARRAY_FAIL);
    }do_end;

    if (nRetCode != WSEC_SUCCESS)
    {
        WSEC_ARR_Finalize(pElement->arrKeyTypeCfg);
        WSEC_FREE(pElement);
    }

    /* Misinformation: FORTIFY.Memory_Leak ('pElement' added to 'pKmcCfg->arrDomainCfg' when success) */

    return nRetCode;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_AddDomainKeyType2Array
 ��������  : ��Domain���������KeyType����
 �� �� ��  : ulDomainId: KeyType������Domain;
             pKeyType:   KeyType����
 �� �� ��  : ��
 ��γ���  : pKmcCfg: KMC��������
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_AddDomainKeyType2Array(KMC_CFG_STRU* pKmcCfg, WSEC_UINT32 ulDomainId, const KMC_CFG_KEY_TYPE_STRU* pKeyType)
{
    KMC_DOMAIN_CFG_STRU* pDomainCfg = WSEC_NULL_PTR;
    WSEC_ARRAY arrKeyType = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU* pElement = WSEC_NULL_PTR;
    WSEC_INT32 iNum;

    /* 1. ����Domain�Ƿ���� */
    pDomainCfg = KMC_PRI_SearchDomain(pKmcCfg, ulDomainId);
    return_oper_if(!pDomainCfg, WSEC_LOG_E1("The Domain(ID=%u) not exist", ulDomainId), WSEC_ERR_KMC_DOMAIN_MISS);

    arrKeyType = pDomainCfg->arrKeyTypeCfg;
    WSEC_ASSERT(arrKeyType);

    /* 2. ÿ��Domain��KeyType�������ܳ��� */
    iNum = WSEC_ARR_GetCount(arrKeyType);
    return_oper_if(WSEC_DOMAIN_KEY_TYPE_NUM_MAX == iNum,
                   WSEC_LOG_E2("Each Domain's KeyType num(%d) cannot over %u", iNum, WSEC_DOMAIN_KEY_TYPE_NUM_MAX), 
                   WSEC_ERR_KMC_KEYTYPE_NUM_OVERFLOW);

    /* 3. ȷ����Domain��KeyTypeΨһ�� */
    return_oper_if(WSEC_ARR_BinarySearch(arrKeyType, pKeyType), 
                   WSEC_LOG_E2("The KeyType(DomainId=%u, KeyType=%u) already existed.", ulDomainId, pKeyType->usKeyType), 
                   WSEC_ERR_KMC_ADD_REPEAT_KEY_TYPE);
    
    /* 4. ���뵽���� */
    pElement = (KMC_CFG_KEY_TYPE_STRU*)WSEC_CLONE_BUFF(pKeyType, sizeof(KMC_CFG_KEY_TYPE_STRU));
    return_oper_if(!pElement, WSEC_LOG_E("WSEC_CLONE_BUFF() fail."), WSEC_ERR_MEMCLONE_FAIL);

    if (WSEC_ARR_AddOrderly(arrKeyType, pElement) < 0) /* ����ʧ��, ���ͷſ�¡�������� */
    {
        WSEC_FREE(pElement);
        return WSEC_ERR_OPER_ARRAY_FAIL;
    }

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_AddMk2Array
 ��������  : ����MK������
 �� �� ��  : pKeystore: �洢MK�����Keystore���ݽṹ
             pMk:       MK����;
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_PRI_AddMk2Array(KMC_KSF_MEM_STRU* pKeystore, KMC_MEM_MK_STRU* pMk)
{
    WSEC_INT32 iNum;
    WSEC_ASSERT(pKeystore && pMk);

    if (pKeystore->arrMk)
    {
        iNum = WSEC_ARR_GetCount(pKeystore->arrMk);
        return_oper_if(WSEC_MK_NUM_MAX == iNum,
                       WSEC_LOG_E2("MkNum(%d) cannot over %u", iNum, WSEC_MK_NUM_MAX), 
                       WSEC_ERR_KMC_MK_NUM_OVERFLOW);
    }
    else
    {
        pKeystore->arrMk = WSEC_ARR_Initialize(0, 0, KMC_PRI_CompareMk4Arr, KMC_PRI_OnRemoveMkArr);
        return_oper_if(!pKeystore->arrMk, WSEC_LOG_E("WSEC_ARR_Initialize() fail"), WSEC_ERR_OPER_ARRAY_FAIL);
    }

    return_oper_if(KMC_PRI_SearchMkByKeyId(pKeystore, pMk->stMkInfo.ulDomainId, pMk->stMkInfo.ulKeyId) >= 0, 
                   WSEC_LOG_E2("The MK(Domain=%u, KeyId=%u) exist.", pMk->stMkInfo.ulDomainId, pMk->stMkInfo.ulKeyId), 
                   WSEC_ERR_KMC_ADD_REPEAT_MK);

    /* ������ǰ, �����ļ��� */
    KMC_MASK_MK(pMk);
    return_oper_if(WSEC_ARR_AddOrderly(pKeystore->arrMk, pMk) < 0,
                   WSEC_LOG_E("WSEC_ARR_AddOrderly() fail."), WSEC_ERR_OPER_ARRAY_FAIL);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CompareDomain4Arr
 ��������  : ���������������/����ʱ�Ƚ�����Ԫ�ش�С�Ļص�����.
 �� �� ��  : p1: ���Domain1���ݵ�ַ��ָ��;
             p2: ���Domain2���ݵ�ַ��ָ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 1:  ����1 ���� ����2
             0:  ����1 ���� ����2
             -1: ����1 С�� ����2
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��10��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_PRI_CompareDomain4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2)
{
    const KMC_DOMAIN_CFG_STRU *pDomain1, *pDomain2;
    
    WSEC_ASSERT(p1 && p2);

    pDomain1 = (const KMC_DOMAIN_CFG_STRU*)(*(const WSEC_VOID**)p1);
    pDomain2 = (const KMC_DOMAIN_CFG_STRU*)(*(const WSEC_VOID**)p2);

    if (pDomain1->stDomainInfo.ulId > pDomain2->stDomainInfo.ulId)
    {
        return WSEC_CMP_RST_BIG_THAN;
    }

    if (pDomain1->stDomainInfo.ulId < pDomain2->stDomainInfo.ulId)
    {
        return WSEC_CMP_RST_SMALL_THAN;
    }

    return WSEC_CMP_RST_EQUAL;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CompareDomainKeyType4Arr
 ��������  : ���������������/����ʱ�Ƚ�����Ԫ�ش�С�Ļص�����.
 �� �� ��  : p1: ���KeyType1���ݵ�ַ��ָ��;
             p2: ���KeyType2���ݵ�ַ��ָ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 1:  ����1 ���� ����2
             0:  ����1 ���� ����2
             -1: ����1 С�� ����2
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��11��01��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_PRI_CompareDomainKeyType4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2)
{
    const KMC_CFG_KEY_TYPE_STRU *pKeyType1, *pKeyType2;

    WSEC_ASSERT(p1 && p2);

    pKeyType1 = (const KMC_CFG_KEY_TYPE_STRU*)(*(const WSEC_VOID**)p1);
    pKeyType2 = (const KMC_CFG_KEY_TYPE_STRU*)(*(const WSEC_VOID**)p2);

    if (pKeyType1->usKeyType > pKeyType2->usKeyType)
    {
        return WSEC_CMP_RST_BIG_THAN;
    }
    
    if (pKeyType1->usKeyType < pKeyType2->usKeyType)
    {
        return WSEC_CMP_RST_SMALL_THAN;
    }

    return WSEC_CMP_RST_EQUAL;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_CompareMk4Arr
 ��������  : ���������������/����ʱ�Ƚ�����Ԫ�ش�С�Ļص�����.
 �� �� ��  : p1: ���MK1���ݵ�ַ��ָ��;
             p2: ���MK1���ݵ�ַ��ָ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 1:  ����1 ���� ����2
             0:  ����1 ���� ����2
             -1: ����1 С�� ����2
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��11��01��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_PRI_CompareMk4Arr(const WSEC_VOID* p1, const WSEC_VOID* p2)
{
    const KMC_MEM_MK_STRU* pElement1;
    const KMC_MEM_MK_STRU* pElement2;
    const KMC_MK_INFO_STRU* pMk1;
    const KMC_MK_INFO_STRU* pMk2;
    
    WSEC_ASSERT(p1 && p2);
    pElement1 = (const KMC_MEM_MK_STRU*)(*(const WSEC_VOID**)p1);
    pElement2 = (const KMC_MEM_MK_STRU*)(*(const WSEC_VOID**)p2);

    pMk1 = &pElement1->stMkInfo;
    pMk2 = &pElement2->stMkInfo;
    
    if (pMk1->ulDomainId > pMk2->ulDomainId) {return WSEC_CMP_RST_BIG_THAN;}
    if (pMk1->ulDomainId < pMk2->ulDomainId) {return WSEC_CMP_RST_SMALL_THAN;}

    /* ulDomainId��ͬ, ���usType */
    if (pMk1->usType > pMk2->usType) {return WSEC_CMP_RST_BIG_THAN;}
    if (pMk1->usType < pMk2->usType) {return WSEC_CMP_RST_SMALL_THAN;}

    /* ulDomainId + usType����ͬ, ��Ҫ�ټ��ucStatus */
    if (pMk1->ucStatus > pMk2->ucStatus) {return WSEC_CMP_RST_BIG_THAN;}
    if (pMk1->ucStatus < pMk2->ucStatus) {return WSEC_CMP_RST_SMALL_THAN;}

    /* ulDomainId + usType + ucStatus����ͬ */
    return WSEC_CMP_RST_EQUAL;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_SearchDomainKeyTypeCfg
 ��������  : ����DomainId,[usKeyType]����Domain, [KeyType]����
 �� �� ��  : ulDomainId: �������
             usKeyType:  ѡ�����
 �� �� ��  : ppDomain:   �����ҵ���Domain[��NULL];
             ppKeyType:  �����ҵ���KeyType[��NULL];
 ��γ���  : ��
 �� �� ֵ  : WSEC_TRUE:  ��Ҫ����ҳɹ�.
             WSEC_FALSE: ����ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��04��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_PRI_SearchDomainKeyTypeCfg(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType, KMC_DOMAIN_CFG_STRU** ppDomain, KMC_CFG_KEY_TYPE_STRU** ppKeyType)
{
    KMC_DOMAIN_CFG_STRU stFindDomain = {0};
    KMC_CFG_KEY_TYPE_STRU   stFindKeyType = {0};
    KMC_DOMAIN_CFG_STRU* pstFoundDomain = WSEC_NULL_PTR;

    stFindDomain.stDomainInfo.ulId = ulDomainId;
    pstFoundDomain = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_BinarySearch(g_pKmcCfg->arrDomainCfg, &stFindDomain);

    if (!pstFoundDomain) {return WSEC_FALSE;}
    if (ppDomain) {*ppDomain = pstFoundDomain;}
    if (!ppKeyType) {return WSEC_TRUE;}

    /* ����Ҫ���Domain�µ�KeyType */
    stFindKeyType.usKeyType = usKeyType;
    *ppKeyType = (KMC_CFG_KEY_TYPE_STRU*)WSEC_ARR_BinarySearch(pstFoundDomain->arrKeyTypeCfg, &stFindKeyType);

    return (*ppKeyType != WSEC_NULL_PTR);
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_SearchDomain
 ��������  : ��KMC������Ϣ������Domain
 �� �� ��  : pKmcCfg:    KMC��������
             ulDomainId: ��������DomainId
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ָ��Domain���õ�ָ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
KMC_DOMAIN_CFG_STRU* KMC_PRI_SearchDomain(const KMC_CFG_STRU* pKmcCfg, WSEC_UINT32 ulDomainId)
{
    KMC_DOMAIN_CFG_STRU stDomain;

    stDomain.stDomainInfo.ulId = ulDomainId;
    return (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_BinarySearch(pKmcCfg->arrDomainCfg, &stDomain);
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_SearchMkByKeyId
 ��������  : ����MK
 �� �� ��  : pKeystore: �洢Keystore�����ݽṹ
             ulDomainId, ulKeyId: Ψһ��ʶMK
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : �Ǹ���: �����ҵ���MK�������е��±�
             -1:     ����ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��04��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_PRI_SearchMkByKeyId(const KMC_KSF_MEM_STRU* pKeystore, WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId)
{
    WSEC_INT32 i;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    
    for (i = 0; i < WSEC_ARR_GetCount(pKeystore->arrMk); i++)
    {
        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(pKeystore->arrMk, i);
        continue_if(!pMk);
        if ((pMk->stMkInfo.ulDomainId == ulDomainId) && (pMk->stMkInfo.ulKeyId == ulKeyId))
        {
            return i;
        }
    }

    return -1;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_OnRemoveDomainArr
 ��������  : ɾ��Domain����Ԫ��ʱ, ����洢�ڸ�Ԫ���ϵ�����.
             ����Ԫ�ش��Domain���ݵĵ�ַ, ��Ҫ����Domain�ͷ�.
             �ͷ�Domainǰ, ��Ҫ�ͷ���KeyType����.
 �� �� ��  : pElement: ����Ԫ�����ڵ�ַ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��11��01��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_OnRemoveDomainArr(WSEC_VOID *pElement)
{
    KMC_DOMAIN_CFG_STRU* pData;

    pData = (KMC_DOMAIN_CFG_STRU*)pElement;
    if (pData)
    {
        WSEC_ARR_Finalize(pData->arrKeyTypeCfg);
        WSEC_FREE(pData);
    }

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_OnRemoveMkArr
 ��������  : ɾ��MK����Ԫ��ʱ, ����洢�ڸ�Ԫ���ϵ�����.
             ����Ԫ�ش��MK���ݵĵ�ַ, ��Ҫ����MK�ͷ�.
 �� �� ��  : pElement: ����Ԫ�����ڵ�ַ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2014��11��01��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_OnRemoveMkArr(WSEC_VOID *pElement)
{
    WSEC_FREE_S(pElement, sizeof(KMC_MEM_MK_STRU)); /* ��MK����, �谲ȫ���ͷ�(�ͷ�ǰ�����ڴ�) */

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_Lock
 ��������  : ����KMC���ٽ���Դ
 �� �� ��  : eType: �ٽ���Դ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2015��3��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_Lock(KMC_LOCK_TYPE_ENUM eType)
{
    if (eType & KMC_LOCK_KEYSTORE) {WSEC_Lock(LOCK4KEYSTORE);}
    if (eType & KMC_LOCK_CFG) {WSEC_Lock(LOCK4KMC_CFG);}

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_PRI_Unlock
 ��������  : �������KMC���ٽ���Դ
 �� �� ��  : eType: �ٽ���Դ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2015��3��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_PRI_Unlock(KMC_LOCK_TYPE_ENUM eType)
{
    if (eType & KMC_LOCK_KEYSTORE) {WSEC_Unlock(LOCK4KEYSTORE);}
    if (eType & KMC_LOCK_CFG) {WSEC_Unlock(LOCK4KMC_CFG);}

    return;
}


/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                          ���� ����ӿں���
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

/*****************************************************************************
 �� �� ��  : KMC_Initialize
 ��������  : KMCģ���ʼ��
 �� �� ��  : pstFileName: KMC�����ļ���.
             pKmcCallbackFun:  CBB�ص�����, �����ע��, ��ϵͳʹ��ȱʡֵ.
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(0Ϊ�ɹ�, ����Ϊ������)
 �ر�ע��  : 1. APP����KMC���κκ���ǰ��������Ҫ���ô˺������KMC��ʼ��.
             2. App���ô˺���ǰ, Ӧȷ���ļ�ϵͳ�ɹ���.

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_Initialize(const KMC_FILE_NAME_STRU* pstFileName, const KMC_FP_CALLBACK_STRU* pKmcCallbackFun, const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    WSEC_ERR_T nRet = WSEC_SUCCESS;
    KMC_FP_CFG_CALLBACK_STRU stCallbackFun = {0};
    const KMC_FP_CFG_CALLBACK_STRU* pstCfgFun = WSEC_NULL_PTR;
    const KMC_FP_CFG_CALLBACK_STRU* pReadCfgCallback = WSEC_NULL_PTR;
    WSEC_BOOL bCbbManCfg = WSEC_FALSE;

    return_oper_if(WSEC_RUNNING == g_KmcSys.eState, WSEC_LOG_E("KMC is running, cannot be Initialize before finalize."), WSEC_ERR_KMC_INI_MUL_CALL);
    return_err_if_para_invalid("KMC_Initialize", pstFileName);

    WSEC_LOG_E("KMC Initialize begin.");

    if (pKmcCallbackFun)
    {
        pReadCfgCallback = &pKmcCallbackFun->stReadCfg;
    }

    g_KmcSys.eState       = WSEC_INIT_FAIL; /* ��ʼ������û�����������, �����ڳ�ʼ��ʧ�� */
    g_KmcSys.eRootKeyFrom = KMC_RK_GEN_BY_INNER; /* ��ʱ��֧��CBB�ڲ�����RK */

    WSEC_ASSERT(KMC_KSF_NUM >= 2); /* ����֧��2����Ϊ������Keystore�ļ�. */

    /* 1 ��μ�� */
    return_oper_if((!pstFileName->pszKeyStoreFile[0]) || (!pstFileName->pszKeyStoreFile[1]),
                    WSEC_LOG_E1("The %d KSF name must be all-provided.", WSEC_NUM_OF(pstFileName->pszKeyStoreFile)), WSEC_ERR_INVALID_ARG);
    return_oper_if(WSEC_PTR_XOR(pstFileName->pszKmcCfgFile[0], pstFileName->pszKmcCfgFile[1]), /* �����ļ���Ҫôȫ���ṩ, Ҫôȫ���ṩ */
                   WSEC_LOG_E1("The %d KMC-CFG-File must be all-provided or all-missed.", WSEC_NUM_OF(pstFileName->pszKmcCfgFile)),
                   WSEC_ERR_INVALID_ARG);

    if (pstFileName->pszKmcCfgFile[0] && pstFileName->pszKmcCfgFile[1]) /* ��CBB������������, ��APP��Ӧ��ע���ȡ���õĻص����� */
    {
        if (pReadCfgCallback)
        {
            return_oper_if(WSEC_GetZeroItemCount(pReadCfgCallback, sizeof(KMC_FP_CFG_CALLBACK_STRU), sizeof(pReadCfgCallback->pfReadKeyManCfg))
                           != (sizeof(KMC_FP_CFG_CALLBACK_STRU) / sizeof(pReadCfgCallback->pfReadKeyManCfg)),
                           WSEC_LOG_E("App should not provid para of 'KMC_FP_CALLBACK_STRU' because of KMC-CFG-File provided."), WSEC_ERR_INVALID_ARG);
        }

        stCallbackFun.pfReadRootKeyCfg              = KMC_PRI_ReadRootKeyCfg;
        stCallbackFun.pfReadKeyManCfg               = KMC_PRI_ReadKeyManCfg;
        stCallbackFun.pfReadCfgOfDomainCount        = KMC_PRI_ReadCfgOfDomainCount;
        stCallbackFun.pfReadCfgOfDomainInfo         = KMC_PRI_ReadCfgOfDomainInfo;
        stCallbackFun.pfReadCfgOfDomainKeyTypeCount = KMC_PRI_ReadCfgOfDomainKeyTypeCount;
        stCallbackFun.pfReadCfgOfDomainKeyType      = KMC_PRI_ReadCfgOfDomainKeyType;
        stCallbackFun.pfReadCfgOfDataProtection     = KMC_PRI_ReadCfgOfDataProtection;
        pstCfgFun = &stCallbackFun;

        g_KmcSys.apszKmcCfgFile[0] = WSEC_CLONE_STR(pstFileName->pszKmcCfgFile[0]);
        g_KmcSys.apszKmcCfgFile[1] = WSEC_CLONE_STR(pstFileName->pszKmcCfgFile[1]);
        bCbbManCfg = WSEC_TRUE;
    }
    else /* ��ҪAPP�������ṩ�ص�������ȡ�������� */
    {
        return_oper_if(!pReadCfgCallback, WSEC_LOG_E("App must provid para of 'KMC_FP_CALLBACK_STRU'."), WSEC_ERR_INVALID_ARG);
        return_oper_if(WSEC_GetZeroItemCount(pReadCfgCallback, sizeof(KMC_FP_CFG_CALLBACK_STRU), sizeof(pReadCfgCallback->pfReadKeyManCfg)) != 0, 
                       WSEC_LOG_E("App must provid all callback function of 'KMC_FP_CALLBACK_STRU'."), WSEC_ERR_INVALID_ARG);
        pstCfgFun = pReadCfgCallback;
    }

    g_KmcSys.apszKeystoreFile[0] = WSEC_CLONE_STR(pstFileName->pszKeyStoreFile[0]);
    g_KmcSys.apszKeystoreFile[1] = WSEC_CLONE_STR(pstFileName->pszKeyStoreFile[1]);
    
    WSEC_UNCARE(KMC_PRI_CalcMkPlainLenMax(&g_KmcSys.ulMkPlainLenMax));

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    nRet = KMC_PRI_LoadData(&g_pKmcCfg, &g_pKeystore, pstRptProgress, pstCfgFun, bCbbManCfg);
    if (nRet != WSEC_SUCCESS) /* �����������ɹ�, ���ͷ���Դ */
    {
        KMC_PRI_FreeGlobalMem(&g_pKmcCfg, &g_pKeystore);
        WSEC_LOG_E1("KMC_Initialize() fail. ErrCode = %u", nRet);
    }
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    if (nRet != WSEC_SUCCESS) {return nRet;}

    WSEC_ASSERT(g_pKeystore->pszFromFile);

    /* �����������Ա�Reset�� */
    if (pReadCfgCallback)
    {
        if (WSEC_MEMCPY(&g_KmcSys.stBackupPara.stCallbackFun, sizeof(g_KmcSys.stBackupPara.stCallbackFun), pKmcCallbackFun, sizeof(KMC_FP_CALLBACK_STRU)) != EOK)
        {
            if (WSEC_MEMSET(&g_KmcSys.stBackupPara.stCallbackFun, sizeof(g_KmcSys.stBackupPara.stCallbackFun), 0, sizeof(KMC_FP_CALLBACK_STRU)) != EOK)
            {
                WSEC_LOG_E("memory oper fail.");
            }
        }
    }

    if (pstRptProgress)
    {
        if (WSEC_MEMCPY(&g_KmcSys.stBackupPara.stProgressRpt, sizeof(g_KmcSys.stBackupPara.stProgressRpt), pstRptProgress, sizeof(WSEC_PROGRESS_RPT_STRU)) != EOK)
        {
            if (WSEC_MEMSET(&g_KmcSys.stBackupPara.stProgressRpt, sizeof(g_KmcSys.stBackupPara.stProgressRpt), 0, sizeof(WSEC_PROGRESS_RPT_STRU)) != EOK)
            {
                WSEC_LOG_E("memory oper fail.");
            }
        }
    }

    g_KmcSys.eState = WSEC_RUNNING;
    WSEC_LOG_E("KMC Initialized successful.");
    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_Finalize
 ��������  : KMCȥ��ʼ��
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��03��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_Finalize()
{
    WSEC_ERR_T nErrCode;

    nErrCode = KMC_PRI_Finalize(KMC_NEED_LOCK);
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_Reset
 ��������  : KMCģ�鸴λ
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��03��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_Reset()
{
    KMC_FILE_NAME_STRU stFileName = {0};
    KMC_FP_CALLBACK_STRU stKmcCallbackFun = {0};
    WSEC_PROGRESS_RPT_STRU stRptProgress = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;

    do
    {
        /* 1. �����ʼ������ */
        stFileName.pszKeyStoreFile[0] = WSEC_CLONE_STR(g_KmcSys.apszKeystoreFile[0]);
        stFileName.pszKeyStoreFile[1] = WSEC_CLONE_STR(g_KmcSys.apszKeystoreFile[1]);
        stFileName.pszKmcCfgFile[0] = WSEC_CLONE_STR(g_KmcSys.apszKmcCfgFile[0]);
        stFileName.pszKmcCfgFile[1] = WSEC_CLONE_STR(g_KmcSys.apszKmcCfgFile[1]);
        break_oper_if(WSEC_MEMCPY(&stKmcCallbackFun, sizeof(stKmcCallbackFun), &g_KmcSys.stBackupPara.stCallbackFun, sizeof(g_KmcSys.stBackupPara.stCallbackFun)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        break_oper_if(WSEC_MEMCPY(&stRptProgress, sizeof(stRptProgress), &g_KmcSys.stBackupPara.stProgressRpt, sizeof(g_KmcSys.stBackupPara.stProgressRpt)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);

        /* 2. ȥ��ʼ�� */
        nErrCode = KMC_PRI_Finalize(KMC_NEED_LOCK);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC Finalize ret %u", nErrCode), oper_null);

        /* 3. ��ʼ�� */
        nErrCode = KMC_Initialize(&stFileName, &stKmcCallbackFun, &stRptProgress);
    }do_end;

    WSEC_FREE(stFileName.pszKeyStoreFile[0]);
    WSEC_FREE(stFileName.pszKeyStoreFile[1]);
    WSEC_FREE(stFileName.pszKmcCfgFile[0]);
    WSEC_FREE(stFileName.pszKmcCfgFile[1]);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_RmvMk
 ��������  : ɾ��MasterKey
 �� �� ��  : ulDomainId, ulKeyId: MK��Ψһ���.
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��04��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_RmvMk(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId)
{
    WSEC_INT32 nAt = -1;
    KMC_DOMAIN_CFG_STRU* psDomain = WSEC_NULL_PTR;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    KMC_MK_INFO_STRU stRmvMk = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        /* 1. ������MK */
        nAt = KMC_PRI_SearchMkByKeyId(g_pKeystore, ulDomainId, ulKeyId);
        break_oper_if(nAt < 0, WSEC_LOG_E2("MK(DomainId=%u, KeyId=%u) not found", ulDomainId, ulKeyId), nErrCode = WSEC_ERR_KMC_MK_MISS);
        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, nAt);
        break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        /* 2. ������Ӧ��Domain */
        psDomain = KMC_PRI_SearchDomain(g_pKmcCfg, ulDomainId);
        if (psDomain) /* Domain�����д�ʱ��ֹɾ�����Կ */
        {
            break_oper_if(KMC_KEY_STATUS_ACTIVE == pMk->stMkInfo.ucStatus, 
                           WSEC_LOG_E2("Cannot remove active MK(DomainId=%u, KeyId=%u)", ulDomainId, ulKeyId), nErrCode = WSEC_ERR_KMC_CANNOT_RMV_ACTIVE_MK);
        }

        break_oper_if(WSEC_MEMCPY(&stRmvMk, sizeof(stRmvMk), &pMk->stMkInfo, sizeof(pMk->stMkInfo)) != EOK,
                       WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);

        WSEC_ARR_RemoveAt(g_pKeystore->arrMk, nAt);
        nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);
        WSEC_LOG_W1("KMC_PRI_WriteKsfSafety()=%u", nErrCode);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    if (nErrCode != WSEC_SUCCESS) {return nErrCode;}

    /* ֪ͨAPP, д��־ */
    KMC_PRI_NtfMkChanged(&stRmvMk, KMC_KEY_REMOVED);
    WSEC_LOG_W3("Del MasterKey(DomainId=%u, KeyId=%u, KeyType=%u)", stRmvMk.ulDomainId, stRmvMk.ulKeyId, stRmvMk.usType);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_ExportMkFile
 ��������  : ��Keystore�ļ�������ָ���ļ�.
 �� �� ��  : pszToFile: �����ļ���
             pbPwd:     ����
             ulPwdLen:  �����
             ulKeyIterations: ��Կ��������, ���Ϊ0, ��ʹ�� KMC_KEY_DFT_ITERATIONS
             pstRptProgress:  �ϱ�����ָʾ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��05��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_ExportMkFile(const WSEC_CHAR* pszToFile, const WSEC_BYTE* pbPwd, WSEC_UINT32 ulPwdLen, WSEC_UINT32 ulKeyIterations, const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    KMC_MKF_HDR_WITH_HMAC_STRU* pstHdrWithHmac = WSEC_NULL_PTR;
    KMC_MKF_HDR_STRU*    pstHdr = WSEC_NULL_PTR;
    KMC_MEM_MK_STRU*     pMemMk = WSEC_NULL_PTR;
    WSEC_BUFF            stEncrptKey     = {0}; /* MK������Կ */
    WSEC_BUFF            stHmacKey       = {0}; /* HMAC��Կ */
    WSEC_BUFF            stMkCiphertext  = {0}; /* ����MK���� */
    WSEC_BUFF            stHmac4MkCipher = {0}; /* MK���ĵ�HMAC */
    KMC_MKF_MK_STRU*     pstMkWri = WSEC_NULL_PTR;
    WSEC_FILE            fWri = WSEC_NULL_PTR;
    WSEC_CRYPT_CTX       ctx  = WSEC_NULL_PTR;
    WSEC_SIZE_T nMkCipherLenMax = 0;
    WSEC_SIZE_T nHmacRstLen = 0;
    WSEC_INT32 i, nMkNum;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bCancel = WSEC_FALSE;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_ExportMkFile", pszToFile && pbPwd && (ulPwdLen > 0));

    if (ulKeyIterations < 1) {ulKeyIterations = KMC_KEY_DFT_ITERATIONS;}
    
    pstHdrWithHmac = (KMC_MKF_HDR_WITH_HMAC_STRU*)WSEC_MALLOC(sizeof(KMC_MKF_HDR_WITH_HMAC_STRU)); /* �����ݱȽϴ�, ʹ�ö�Ϊ�� */
    return_oper_if(!pstHdrWithHmac, WSEC_LOG_E4MALLOC(sizeof(KMC_MKF_HDR_WITH_HMAC_STRU)), WSEC_ERR_MALLOC_FAIL);

    pstHdr = &pstHdrWithHmac->stHdr;
    /* 1. ���ܡ�HMAC������ز������� */
    do
    {
        pstHdr->usVer = KMC_MKF_VER;
        pstHdr->usKsfVer = KMC_KSF_VER;
        pstHdr->ulEncryptAlgId = KMC_ENCRYPT_MK_ALGID;
        pstHdr->ulIteration4EncrypKey = ulKeyIterations;
        break_oper_if(CAC_Random(pstHdr->abSalt4EncrypKey, sizeof(pstHdr->abSalt4EncrypKey)) != WSEC_SUCCESS, WSEC_LOG_E("CAC_Random() fail."), nErrCode = WSEC_ERR_GET_RAND_FAIL);
        break_oper_if(CAC_Random(pstHdr->abIv4EncrypMk, sizeof(pstHdr->abIv4EncrypMk)) != WSEC_SUCCESS, WSEC_LOG_E("CAC_Random() fail."), nErrCode = WSEC_ERR_GET_RAND_FAIL);

        pstHdr->ulHmacAlgId = KMC_HMAC_MK_ALGID;
        pstHdr->ulIteration4HmacKey = ulKeyIterations;
        break_oper_if(CAC_Random(pstHdr->abSalt4HmacKey, sizeof(pstHdr->abSalt4HmacKey)) != WSEC_SUCCESS, WSEC_LOG_E("CAC_Random() fail."), nErrCode = WSEC_ERR_GET_RAND_FAIL);
    } do_end;
    if (nErrCode != WSEC_SUCCESS)
    {
        WSEC_FREE(pstHdrWithHmac);
        return nErrCode;
    }

    /* 2. ׼����Դ: �ڴ���䡢���ļ� */
    WSEC_BUFF_ALLOC(stEncrptKey, KMC_EK4MKF_LEN);
    WSEC_BUFF_ALLOC(stHmacKey, KMC_KEY4HMAC_LEN);
    nMkCipherLenMax = sizeof(KMC_MKF_MK_STRU) * 2; /* ��ֹ�������, ���ؿ��� */
    WSEC_BUFF_ALLOC(stMkCiphertext, nMkCipherLenMax);
    WSEC_BUFF_ALLOC(stHmac4MkCipher, KMC_HMAC_RST_LEN);
    pstMkWri = (KMC_MKF_MK_STRU*)WSEC_MALLOC(sizeof(KMC_MKF_MK_STRU));
    fWri = WSEC_FOPEN(pszToFile, "wb");

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do /* 3. ��������, ��д�ļ� */
    {
        break_oper_if(!stEncrptKey.pBuff, WSEC_LOG_E4MALLOC(stEncrptKey.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmacKey.pBuff, WSEC_LOG_E4MALLOC(stHmacKey.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stMkCiphertext.pBuff, WSEC_LOG_E4MALLOC(stMkCiphertext.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmac4MkCipher.pBuff, WSEC_LOG_E4MALLOC(stHmac4MkCipher.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pstMkWri, WSEC_LOG_E4MALLOC(sizeof(KMC_MKF_MK_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!fWri, WSEC_LOG_E1("Open file(%s) for write fail.", pszToFile), nErrCode = WSEC_ERR_OPEN_FILE_FAIL);
        
        /* 1) ����MK������Կ */
        break_oper_if(CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, 
                                  pbPwd, ulPwdLen, 
                                  pstHdr->abSalt4EncrypKey, sizeof(pstHdr->abSalt4EncrypKey),
                                  pstHdr->ulIteration4EncrypKey,
                                  stEncrptKey.nLen, stEncrptKey.pBuff) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_pbkdf2() fail."), nErrCode = WSEC_ERR_PBKDF2_FAIL);

        /* 2) ����HMAC��Կ */
        break_oper_if(CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, 
                                  pbPwd, ulPwdLen, 
                                  pstHdr->abSalt4HmacKey, sizeof(pstHdr->abSalt4HmacKey),
                                  pstHdr->ulIteration4HmacKey,
                                  stHmacKey.nLen, stHmacKey.pBuff) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_Pbkdf2() fail."), nErrCode = WSEC_ERR_PBKDF2_FAIL);

        /* 3) ����MK����, ���ܲ�����HMAC */
        break_oper_if(CAC_HmacInit(&ctx, pstHdr->ulHmacAlgId, stHmacKey.pBuff, stHmacKey.nLen) != WSEC_SUCCESS, WSEC_LOG_E("CAC_HmacInit() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);
        nMkNum = WSEC_ARR_GetCount(g_pKeystore->arrMk);
        break_oper_if(nMkNum < 1, WSEC_LOG_E("No MK exist."), nErrCode = WSEC_ERR_KMC_MK_MISS);
        
        pstHdr->ulMkNum = nMkNum;
        for (i = 0; i < nMkNum; i++)
        {
            pMemMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            break_oper_if(!pMemMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            break_oper_if(WSEC_MEMCPY(&pstMkWri->stMkInfo, sizeof(pstMkWri->stMkInfo), &pMemMk->stMkInfo, sizeof(pMemMk->stMkInfo)) != EOK,
                          WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
            pstMkWri->ulPlainLen = pMemMk->stMkRear.ulPlainLen;

            /* a. ��MK���ڵ���������, ʹ֮��Ϊ������ */
            KMC_UNMASK_MK_TO(pMemMk, pstMkWri->abPlainText, sizeof(pstMkWri->abPlainText));

            /* b. ����MK���� */
            stMkCiphertext.nLen = nMkCipherLenMax;
            WSEC_UNCARE(CAC_Random(pstMkWri->abIv, sizeof(pstMkWri->abIv)));   /* �������������IV */
            WSEC_UNCARE(CAC_Random(stMkCiphertext.pBuff, stMkCiphertext.nLen)); /* ���ĸ���ռ価������������ */
            break_oper_if(CAC_Encrypt(pstHdr->ulEncryptAlgId,
                                      stEncrptKey.pBuff, stEncrptKey.nLen,
                                      pstHdr->abIv4EncrypMk, sizeof(pstHdr->abIv4EncrypMk),
                                      pstMkWri, sizeof(KMC_MKF_MK_STRU),
                                      stMkCiphertext.pBuff, &stMkCiphertext.nLen) != WSEC_SUCCESS,
                          WSEC_LOG_E("CAC_Encrypt() fail."), nErrCode = WSEC_ERR_ENCRPT_FAIL);

            if (0 == i) /* ���׸�MK����, �����˵���MK���ĵĳ���, ����дMKF�ļ�ͷ�����ļ�ͷ��HMAC */
            {
                pstHdr->ulCipherLenPerMk = stMkCiphertext.nLen;

                break_oper_if(!WSEC_FWRITE_MUST(g_MkfFlag, sizeof(g_MkfFlag), fWri),
                              WSEC_LOG_E1("Write file(%s) fail.", pszToFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
                nHmacRstLen = sizeof(pstHdrWithHmac->abHmac);
                break_oper_if(CAC_Hmac(pstHdr->ulHmacAlgId,
                                       stHmacKey.pBuff, stHmacKey.nLen, 
                                       &pstHdrWithHmac->stHdr, sizeof(pstHdrWithHmac->stHdr),
                                       pstHdrWithHmac->abHmac, &nHmacRstLen),
                              WSEC_LOG_E("CAC_Hmac() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);
                WSEC_ASSERT(nHmacRstLen <= sizeof(pstHdrWithHmac->abHmac)); /* ������, ����Ҫ����KMC_MKF_HDR_STRU.abHmac[x]֮x */
                KMC_PRI_CvtByteOrder4MkfHdr(&pstHdrWithHmac->stHdr, wbcHost2Network);
                break_oper_if(!WSEC_FWRITE_MUST(pstHdrWithHmac, sizeof(KMC_MKF_HDR_WITH_HMAC_STRU), fWri),
                              WSEC_LOG_E1("Write file(%s) fail.", pszToFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
                KMC_PRI_CvtByteOrder4MkfHdr(&pstHdrWithHmac->stHdr, wbcNetwork2Host); /* ���ڼ���MKʱ��Ҫʹ��ͷ��Ϣ�е����ݣ������軹ԭ */
            }
            else
            {
                WSEC_ASSERT(stMkCiphertext.nLen == pstHdr->ulCipherLenPerMk); /* MK���ĵȳ�, ����ҲӦ�ȳ�, ������BUG */
            }

            /* c. дMK���� */
            break_oper_if(!WSEC_FWRITE_MUST(stMkCiphertext.pBuff, stMkCiphertext.nLen, fWri),
                          WSEC_LOG_E1("Write file(%s) fail.", pszToFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);

            /* d. ����HMAC */
            break_oper_if(CAC_HmacUpdate(ctx, stMkCiphertext.pBuff, stMkCiphertext.nLen) != WSEC_SUCCESS,
                          WSEC_LOG_E("CAC_HmacUpdate() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);

            WSEC_RptProgress(pstRptProgress, &stTimer, nMkNum, i + 1, &bCancel);
            break_oper_if(bCancel, WSEC_LOG_E("App Canceled"), nErrCode = WSEC_ERR_CANCEL_BY_APP);
        }
        if (nErrCode != WSEC_SUCCESS) {break;}
        if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, nMkNum, nMkNum, WSEC_NULL_PTR);} /* ȷ������100%�ϱ� */

        nHmacRstLen = stHmac4MkCipher.nLen;
        break_oper_if(CAC_HmacFinal(&ctx, stHmac4MkCipher.pBuff, &nHmacRstLen) != WSEC_SUCCESS, 
                      WSEC_LOG_E("CAC_HmacFinal() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);
        WSEC_ASSERT(nHmacRstLen <= stHmac4MkCipher.nLen);

        /* 5) дHMAC�����Ϣ */
        break_oper_if(!WSEC_FWRITE_MUST(stHmac4MkCipher.pBuff, nHmacRstLen, fWri), WSEC_LOG_E1("Write file(%s) fail.", pszToFile), nErrCode = WSEC_ERR_WRI_FILE_FAIL);
    } do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    WSEC_BUFF_FREE(stEncrptKey);
    WSEC_BUFF_FREE(stHmacKey);
    WSEC_BUFF_FREE(stMkCiphertext);
    WSEC_BUFF_FREE(stHmac4MkCipher);
    WSEC_FREE(pstHdrWithHmac);
    WSEC_FREE(pstMkWri);
    if (fWri) {WSEC_FCLOSE(fWri);}

    WSEC_LOG_W2("Export MK to '%s' finished, return code = %u.", pszToFile, nErrCode);
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_ImportMkFile
 ��������  : ��ָ��Keystore�ļ�����MK����.
 �� �� ��  : pszFromFile: Keystore�ļ���
             pbPwd:       ����
             ulPwdLen:    �����
             pstRptProgress: �ϱ�����ָʾ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��05��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_ImportMkFile(const WSEC_CHAR* pszFromFile, const WSEC_BYTE* pbPwd, WSEC_UINT32 ulPwdLen, const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    KMC_KSF_MEM_STRU* pKeystore = WSEC_NULL_PTR;
    WSEC_BYTE   abFormatFlag[32];      /* MK�ļ���ʽ��ʶ */
    KMC_MKF_HDR_WITH_HMAC_STRU* pstHdrWithHmac = WSEC_NULL_PTR;
    KMC_MKF_HDR_STRU* pstHdr   = WSEC_NULL_PTR;
    KMC_MKF_MK_STRU*  pMkPlain = WSEC_NULL_PTR;
    WSEC_CRYPT_CTX    ctx      = WSEC_NULL_PTR;
    WSEC_FILE fRead            = WSEC_NULL_PTR;
    WSEC_BUFF stDecrptKey = {0};
    WSEC_BUFF stHmacKey   = {0};
    WSEC_BUFF stCipher    = {0};
    WSEC_BUFF stHmacRst   = {0};
    WSEC_BUFF stPlainKey = {0};
    WSEC_BUFF stHmac4MkInFile = {0};
    WSEC_SIZE_T nPlainLen = 0;
    KMC_KSF_RK_STRU* pstRk = {0};
    KMC_CFG_DOMAIN_INFO_STRU stDomain = {0};
    KMC_DOMAIN_CFG_STRU* pstDomainCfg = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU        stKeyType = {0};
    KMC_MEM_MK_STRU*         pstMemMk = WSEC_NULL_PTR;
    KMC_MEM_MK_STRU*         pstMk4Add = WSEC_NULL_PTR;
    WSEC_SPEND_TIME_STRU tSpend = {0};
    WSEC_BOOL bCancel = WSEC_FALSE;
    WSEC_INT32 i;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    WSEC_ASSERT(sizeof(abFormatFlag) == sizeof(g_MkfFlag));

    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_ImportMk", pszFromFile && pbPwd && (ulPwdLen > 0));

    do
    {
        pstRk = (KMC_KSF_RK_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_RK_STRU));
        break_oper_if(!pstRk, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_RK_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);

        nErrCode = KMC_PRI_ReadRootKey(g_pKeystore->pszFromFile, pstRk);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_ReadRootKey()=%u", nErrCode), oper_null);

        /* ���ļ� */
        fRead = WSEC_FOPEN(pszFromFile, "rb");
        break_oper_if(!fRead, WSEC_LOG_E1("Open file(%s) for read fail.", pszFromFile), nErrCode = WSEC_ERR_OPEN_FILE_FAIL);
    }do_end;

    if (nErrCode != WSEC_SUCCESS)
    {
        WSEC_FREE(pstRk);
        WSEC_FCLOSE(fRead);
        return nErrCode;
    }

    WSEC_BUFF_ALLOC(stDecrptKey, KMC_EK4MKF_LEN);
    WSEC_BUFF_ALLOC(stHmacKey, KMC_KEY4HMAC_LEN);
    WSEC_BUFF_ALLOC(stHmacRst, KMC_HMAC_RST_LEN);
    WSEC_BUFF_ALLOC(stHmac4MkInFile, KMC_HMAC_RST_LEN);
    pstHdrWithHmac = (KMC_MKF_HDR_WITH_HMAC_STRU*)WSEC_MALLOC(sizeof(KMC_MKF_HDR_WITH_HMAC_STRU));
    pstHdr = &pstHdrWithHmac->stHdr;
    pstMemMk = (KMC_MEM_MK_STRU*)WSEC_MALLOC(sizeof(KMC_MEM_MK_STRU));

    /* ��ȡ�ļ�ͷ, ���ж��ļ���ʽ */
    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        break_oper_if(!stDecrptKey.pBuff, WSEC_LOG_E4MALLOC(stDecrptKey.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmacKey.pBuff, WSEC_LOG_E4MALLOC(stHmacKey.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmacRst.pBuff, WSEC_LOG_E4MALLOC(stHmacRst.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!stHmac4MkInFile.pBuff, WSEC_LOG_E4MALLOC(stHmac4MkInFile.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pstHdrWithHmac, WSEC_LOG_E4MALLOC(sizeof(KMC_MKF_HDR_WITH_HMAC_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pstMemMk, WSEC_LOG_E4MALLOC(sizeof(KMC_MEM_MK_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);

        break_oper_if(!WSEC_FREAD_MUST(abFormatFlag, sizeof(abFormatFlag), fRead),
                      WSEC_LOG_E1("Read file(%s) fail.", pszFromFile), nErrCode = WSEC_ERR_READ_FILE_FAIL);
        break_oper_if(WSEC_MEMCMP(abFormatFlag, g_MkfFlag, sizeof(g_MkfFlag)) != 0,
                      WSEC_LOG_E1("%s is not MK file.", pszFromFile), nErrCode = WSEC_ERR_FILE_FORMAT);
        break_oper_if(!WSEC_FREAD_MUST(pstHdrWithHmac, sizeof(KMC_MKF_HDR_WITH_HMAC_STRU), fRead),
                      WSEC_LOG_E1("%s is not MK file.", pszFromFile), nErrCode = WSEC_ERR_FILE_FORMAT);

        KMC_PRI_CvtByteOrder4MkfHdr(&pstHdrWithHmac->stHdr, wbcNetwork2Host);

        /* ��������HMAC����Կ */
        break_oper_if(CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, 
                                  pbPwd, ulPwdLen, 
                                  pstHdr->abSalt4HmacKey, sizeof(pstHdr->abSalt4HmacKey),
                                  pstHdr->ulIteration4HmacKey,
                                  stHmacKey.nLen, stHmacKey.pBuff) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_Pbkdf2() fail."), nErrCode = WSEC_ERR_PBKDF2_FAIL);

        /* ���ļ�ͷ����HMAC */
        break_oper_if(CAC_Hmac(pstHdr->ulHmacAlgId,
                               stHmacKey.pBuff, stHmacKey.nLen, 
                               pstHdr, sizeof(KMC_MKF_HDR_STRU),
                               stHmacRst.pBuff, &stHmacRst.nLen),
                      WSEC_LOG_E("CAC_Hmac() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);

        /* �ж�HMACֵ����֤�ļ�ͷ�Ƿ��ƻ� */
        break_oper_if(WSEC_MEMCMP(stHmacRst.pBuff, pstHdrWithHmac->abHmac, sizeof(pstHdrWithHmac->abHmac)) != 0,
                      WSEC_LOG_E1("The file(%s) tampered.", pszFromFile), nErrCode = WSEC_ERR_HMAC_FAIL);

        /* �������ڽ���MK���ĵ���Կ */
        break_oper_if(CAC_Pbkdf2(WSEC_ALGID_PBKDF2_HMAC_SHA256, 
                                  pbPwd, ulPwdLen, 
                                  pstHdr->abSalt4EncrypKey, sizeof(pstHdr->abSalt4EncrypKey),
                                  pstHdr->ulIteration4EncrypKey,
                                  stDecrptKey.nLen, stDecrptKey.pBuff) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_Pbkdf2() fail."), nErrCode = WSEC_ERR_PBKDF2_FAIL);

        /* �����ڴ�, Ϊ��ȡMK������׼�� */
        WSEC_BUFF_ALLOC(stCipher, pstHdr->ulCipherLenPerMk);
        pKeystore = (KMC_KSF_MEM_STRU*)WSEC_MALLOC(sizeof(KMC_KSF_MEM_STRU)); /* �����ݽϴ�, ʹ�ö��ڴ�Ϊ�� */
        nPlainLen = pstHdr->ulCipherLenPerMk;
        pMkPlain  = (KMC_MKF_MK_STRU*)WSEC_MALLOC(nPlainLen); /* ���ĳ��Ȳ����ܴ������ĳ��� */

        /* �����Դ�Ƿ�λ */
        break_oper_if(!stCipher.pBuff, WSEC_LOG_E4MALLOC(stCipher.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pKeystore, WSEC_LOG_E4MALLOC(sizeof(KMC_KSF_MEM_STRU)), nErrCode = WSEC_ERR_MALLOC_FAIL);
        break_oper_if(!pMkPlain, WSEC_LOG_E4MALLOC(pstHdr->ulCipherLenPerMk), nErrCode = WSEC_ERR_MALLOC_FAIL);

        if (WSEC_MEMCPY(&pKeystore->stRkInfo, sizeof(pKeystore->stRkInfo), &g_pKeystore->stRkInfo, sizeof(g_pKeystore->stRkInfo)) != EOK) {nErrCode = WSEC_ERR_MEMCPY_FAIL;}
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E4MEMCPY, oper_null);

        break_oper_if(CAC_HmacInit(&ctx, pstHdr->ulHmacAlgId, stHmacKey.pBuff, stHmacKey.nLen) != WSEC_SUCCESS, WSEC_LOG_E("CAC_HmacInit() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);
        
        /* �����ȡMK���� */
        for (i = 0; i < (WSEC_INT32)pstHdr->ulMkNum; i++)
        {
            break_oper_if(!WSEC_FREAD_MUST(stCipher.pBuff, stCipher.nLen, fRead),
                          WSEC_LOG_E1("Read data from(%s) fail.", pszFromFile), nErrCode = WSEC_ERR_READ_FILE_FAIL);

            /* �Ա����ļ���HMAC */
            break_oper_if(CAC_HmacUpdate(ctx, stCipher.pBuff, stCipher.nLen) != WSEC_SUCCESS,
                          WSEC_LOG_E("CAC_HmacUpdate() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);

            /* ����MK */
            nPlainLen = pstHdr->ulCipherLenPerMk;
            break_oper_if(CAC_Decrypt(pstHdr->ulEncryptAlgId,
                                      stDecrptKey.pBuff, stDecrptKey.nLen,
                                      pstHdr->abIv4EncrypMk, sizeof(pstHdr->abIv4EncrypMk),
                                      stCipher.pBuff, stCipher.nLen,
                                      pMkPlain, &nPlainLen) != WSEC_SUCCESS,
                          WSEC_LOG_E("CAC_Decrypt() fail."), nErrCode = WSEC_ERR_DECRPT_FAIL);
            break_oper_if(nPlainLen != sizeof(KMC_MKF_MK_STRU), WSEC_LOG_E1("Parse data in file(%s) fail.", pszFromFile), nErrCode = WSEC_ERR_FILE_FORMAT);

            /* �����ܳ���MKת��Ϊ���������е�MK */
            pstDomainCfg = KMC_PRI_SearchDomain(g_pKmcCfg, pMkPlain->stMkInfo.ulDomainId);
            break_oper_if(pstDomainCfg && (pstDomainCfg->stDomainInfo.ucKeyFrom != pMkPlain->stMkInfo.ucGenStyle), 
                          WSEC_LOG_E4("Import MK(DomainId=%d, KeyId=%d) fail for KeyFrom(%d) conflict with Domain-CFG(%d).",
                                      pMkPlain->stMkInfo.ulDomainId, pMkPlain->stMkInfo.ulKeyId, pMkPlain->stMkInfo.ucGenStyle, pstDomainCfg->stDomainInfo.ucKeyFrom), 
                          nErrCode = WSEC_ERR_KMC_IMPORT_MK_CONFLICT_DOMAIN);

            stDomain.ulId      = pMkPlain->stMkInfo.ulDomainId;
            stDomain.ucKeyFrom = pMkPlain->stMkInfo.ucGenStyle;

            stKeyType.ulKeyLen  = pMkPlain->ulPlainLen;
            stKeyType.usKeyType = pMkPlain->stMkInfo.usType;
            stKeyType.ulKeyLifeDays = 180; /* ȷ�����ݽṹ�Ϸ�, û��ʵ������. */

            WSEC_BUFF_ASSIGN(stPlainKey, pMkPlain->abPlainText, pMkPlain->ulPlainLen);
            nErrCode = KMC_PRI_MakeMk(&stDomain, &stKeyType, &stPlainKey, pMkPlain->stMkInfo.ulKeyId, pstMemMk);
            if (nErrCode != WSEC_SUCCESS) {break;}

            /* ������Ϣ���Ե����ļ�����Ϊ׼ */
            pstMemMk->stMkInfo.ucStatus = pMkPlain->stMkInfo.ucStatus;
            break_oper_if(!WSEC_DateTimeCopy(&pstMemMk->stMkInfo.stMkCreateTimeUtc, &pMkPlain->stMkInfo.stMkCreateTimeUtc),
                          WSEC_LOG_E("WSEC_DateTimeCopy() fail."), nErrCode = WSEC_ERR_MEMCPY_FAIL);
            break_oper_if(!WSEC_DateTimeCopy(&pstMemMk->stMkInfo.stMkExpiredTimeUtc, &pMkPlain->stMkInfo.stMkExpiredTimeUtc),
                          WSEC_LOG_E("WSEC_DateTimeCopy() fail."), nErrCode = WSEC_ERR_MEMCPY_FAIL);

            /* �������� */
            pstMk4Add = (KMC_MEM_MK_STRU*)WSEC_CLONE_BUFF(pstMemMk, sizeof(KMC_MEM_MK_STRU));
            break_oper_if(!pstMk4Add, WSEC_LOG_E("WSEC_CLONE_BUFF() fail."), nErrCode = WSEC_ERR_MEMCLONE_FAIL);
            nErrCode = KMC_PRI_AddMk2Array(pKeystore, pstMk4Add);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_AddMk2Array() = %u", nErrCode), WSEC_FREE(pstMk4Add));

            WSEC_RptProgress(pstRptProgress, &tSpend, pstHdr->ulMkNum, i + 1, &bCancel);
            break_oper_if(bCancel, WSEC_LOG_E("App Canceled"), nErrCode = WSEC_ERR_CANCEL_BY_APP);
        }

        if (nErrCode != WSEC_SUCCESS) {break;}
        if (!bCancel) {WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, pstHdr->ulMkNum, pstHdr->ulMkNum, WSEC_NULL_PTR);} /* ȷ������100%�ϱ� */

        /* ��ȡ����MK���ĵ�HMAC����֤������ */
        break_oper_if(CAC_HmacFinal(&ctx, stHmacRst.pBuff, &stHmacRst.nLen) != WSEC_SUCCESS, 
                      WSEC_LOG_E("CAC_HmacFinal() fail."), nErrCode = WSEC_ERR_HMAC_FAIL);

        /* ��ȡ�ļ��ж�����MK���ĵ�HMAC */
        break_oper_if(!WSEC_FREAD_MUST(stHmac4MkInFile.pBuff, stHmac4MkInFile.nLen, fRead),
                      WSEC_LOG_E1("Read data from(%s) fail.", pszFromFile), nErrCode = WSEC_ERR_READ_FILE_FAIL);

        break_oper_if(WSEC_MEMCMP(stHmacRst.pBuff, stHmac4MkInFile.pBuff, stHmacRst.nLen) != 0,
                      WSEC_LOG_E1("The file(%s)'s HMAC authenticated fail.", pszFromFile), nErrCode = WSEC_ERR_HMAC_AUTH_FAIL);

        /* ����Keystoreд�ļ�, �ڴ��е�Keystore�����л� */
        nErrCode = KMC_PRI_WriteKsfSafety(pKeystore, pstRk);
        if (nErrCode != WSEC_SUCCESS) {WSEC_LOG_E1("KMC_PRI_WriteKsfSafety() = %u.", nErrCode);}
    } do_end;
    
    if (WSEC_SUCCESS == nErrCode) /* �ڴ��е�Keystore�����л� */
    {
        pKeystore->pszFromFile = g_pKeystore->pszFromFile;
        KMC_PRI_FreeKsfSnapshot(g_pKeystore);
        g_pKeystore = pKeystore;
    }
    else
    {
        KMC_PRI_FreeKsfSnapshot(pKeystore);
    }
    
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    /* ��Դ�ͷ� */
    WSEC_FCLOSE(fRead);
    WSEC_BUFF_FREE(stDecrptKey);
    WSEC_BUFF_FREE(stHmacKey);
    WSEC_BUFF_FREE(stHmacRst);
    WSEC_BUFF_FREE(stCipher);
    WSEC_BUFF_FREE(stHmac4MkInFile);
    WSEC_FREE(pMkPlain);
    WSEC_FREE(pstHdrWithHmac);
    WSEC_FREE(pstRk);
    WSEC_FREE(pstMemMk);

    WSEC_LOG_W2("Import MK from '%s' finished, return code = %u.", pszFromFile, nErrCode);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_UpdateRootKey
 ��������  : ���¸���Կ
             ԭ��: �������ɸ���Կ, ��MK���ֲ���.
 �� �� ��  : pbKeyEntropy: ��Կ������;[�ɿ�];
             ulSize:       ��Կ��������.
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��07��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_UpdateRootKey(const WSEC_BYTE* pbKeyEntropy, WSEC_SIZE_T ulSize)
{
    return_err_if_kmc_not_work;
    return KMC_PRI_UpdateRootKeyPri(pbKeyEntropy, ulSize, KMC_NEED_LOCK);
}

/*****************************************************************************
 �� �� ��  : KMC_GetRootKeyInfo
 ��������  : ��ȡRoot Key��������Ϣ
 �� �� ��  : ��
 �� �� ��  : pstRkInfo: Root Key�ǰ�ȫ������Ϣ
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetRootKeyInfo(KMC_RK_ATTR_STRU* pstRkInfo)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetRootKeyInfo", pstRkInfo);

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    if (WSEC_MEMCPY(pstRkInfo, sizeof(KMC_RK_ATTR_STRU), &g_pKeystore->stRkInfo, sizeof(g_pKeystore->stRkInfo)) != EOK) {nErrCode = WSEC_ERR_MEMCPY_FAIL;}
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetMkCount
 ��������  : ��ȡMK����
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : -1:     ��CBB��δ����ʼ��;
             �Ǹ���: ��ǰMK����.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_GetMkCount()
{
    WSEC_INT32 nCount = -1;

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    if (WSEC_RUNNING == g_KmcSys.eState)
    {
        nCount = WSEC_ARR_GetCount(g_pKeystore->arrMk);
    }
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    return nCount;
}

/*****************************************************************************
 �� �� ��  : KMC_GetMk
 ��������  : ��ȡָ��λ���ϵ�MK
 �� �� ��  : Index: ָ��λ��(0��ʼ������)
 �� �� ��  : pstMk: ���MK
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetMk(WSEC_INT32 Index, KMC_MK_INFO_STRU* pstMk)
{
    KMC_MK_INFO_STRU* pItem = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetMk", (Index >= 0) && pstMk);

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        break_oper_if(Index >= WSEC_ARR_GetCount(g_pKeystore->arrMk), WSEC_LOG_E1("Index(%d) overflow the array.", Index), nErrCode = WSEC_ERR_INVALID_ARG);
        
        pItem = (KMC_MK_INFO_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, Index);
        break_oper_if(!pItem, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        break_oper_if(WSEC_MEMCPY(pstMk, sizeof(KMC_MK_INFO_STRU), pItem, sizeof(KMC_MK_INFO_STRU)) != EOK, WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetMaxMkId
 ��������  : ��ȡָ��Domain�µ�ǰ���MK ID
 �� �� ��  : ulDomainId:  �������ʶ
 �� �� ��  : pulMaxKeyId: ���Master Key ID
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��23��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetMaxMkId(WSEC_UINT32 ulDomainId, WSEC_UINT32* pulMaxKeyId)
{
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_GetMaxMkId", pulMaxKeyId);
    
    return KMC_PRI_GetMaxMkId(KMC_NEED_LOCK, ulDomainId, pulMaxKeyId);
}

/*****************************************************************************
 �� �� ��  : KMC_SetMkExpireTime
 ��������  : ����ָ��MK�Ĺ���ʱ��
 �� �� ��  : ulDomainId, ulKeyId: MKΨһ���ID
             psExpireTime: ����ʱ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SetMkExpireTime(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, const WSEC_SYSTIME_T* psExpireTimeUtc)
{
    WSEC_INT32 nIndex = -1, nRemainLifeDays = 0;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    KMC_MK_EXPIRE_NTF_STRU stWarn = {0};
    WSEC_SYSTIME_T stUtcNow = {0};
    WSEC_BOOL bWarn = WSEC_FALSE;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_SetMkExpireTime", psExpireTimeUtc && WSEC_IsDateTime(psExpireTimeUtc));

    return_oper_if(!WSEC_GetUtcDateTime(&stUtcNow), WSEC_LOG_E("WSEC_GetUtcDateTime() fail"), WSEC_ERR_GET_CURRENT_TIME_FAIL);

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        nIndex = KMC_PRI_SearchMkByKeyId(g_pKeystore, ulDomainId, ulKeyId);
        break_oper_if(nIndex < 0, WSEC_LOG_E2("Cannot find MK(DomainId=%d, KeyId=%d)", ulDomainId, ulKeyId), nErrCode = WSEC_ERR_KMC_MK_MISS);

        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, nIndex);
        break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        break_oper_if(KMC_KEY_STATUS_INACTIVE == pMk->stMkInfo.ucStatus,
                       WSEC_LOG_E2("Cannot set expiretime for inactive MK(ulDomainId=%u, ulKeyId=%u)", ulDomainId, ulKeyId),
                       nErrCode = WSEC_ERR_KMC_CANNOT_SET_EXPIRETIME_FOR_INACTIVE_MK);
        break_oper_if(pMk->stMkInfo.ucGenStyle != KMC_MK_GEN_BY_IMPORT,
                       WSEC_LOG_E2("The MK(ulDomainId=%u, ulKeyId=%u) is not imported, cannot suppoert this oper.", ulDomainId, ulKeyId),
                       nErrCode = WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER);

        break_oper_if(!WSEC_DateTimeCopy(&pMk->stMkInfo.stMkExpiredTimeUtc, psExpireTimeUtc), WSEC_LOG_E("Date time copy fail."), nErrCode = WSEC_ERR_MEMCPY_FAIL);

        /* ����Ƿ��� */
        nRemainLifeDays = WSEC_DateTimeDiff(dtpDay, &stUtcNow, &pMk->stMkInfo.stMkExpiredTimeUtc);
        if (nRemainLifeDays <= 0) /* ������ */
        {
            pMk->stMkInfo.ucStatus = KMC_KEY_STATUS_INACTIVE;
            WSEC_DateTimeCopy(&pMk->stMkInfo.stMkExpiredTimeUtc, &stUtcNow);
        }
        else if (((WSEC_UINT32)nRemainLifeDays) < g_pKmcCfg->stKmCfg.ulWarningBeforeKeyExpiredDays) /* Ԥ�� */
        {
            if (WSEC_MEMCPY(&stWarn.stMkInfo, sizeof(stWarn.stMkInfo), &pMk->stMkInfo, sizeof(pMk->stMkInfo)) == EOK)
            {
                stWarn.nRemainDays = nRemainLifeDays;
                bWarn = WSEC_TRUE;
            }else{;}
        }else{;}

        nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    if_oper(bWarn, WSEC_NOTIFY(WSEC_KMC_NTF_MK_EXPIRE, &stWarn, sizeof(stWarn)));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_SetMkStatus
 ��������  : ����MK״̬
 �� �� ��  : ulDomainId, ulKeyId: MKΨһ���ID
             ucStatus: ��Կ״̬(�� KMC_KEY_STATUS_ENUM)
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SetMkStatus(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, WSEC_UINT8 ucStatus)
{
    WSEC_INT32 nIndex = -1;
    WSEC_BOOL bNtf = WSEC_FALSE;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_SetMkStatus", KMC_IS_VALID_KEY_STATUS(ucStatus));

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        nIndex = KMC_PRI_SearchMkByKeyId(g_pKeystore, ulDomainId, ulKeyId);
        break_oper_if(nIndex < 0, WSEC_LOG_E2("Cannot find MK(DomainId=%d, KeyId=%d)", ulDomainId, ulKeyId), nErrCode = WSEC_ERR_KMC_MK_MISS);

        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, nIndex);
        break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        break_oper_if(pMk->stMkInfo.ucGenStyle != KMC_MK_GEN_BY_IMPORT, WSEC_LOG_E("Only imported MK can support this oper."), nErrCode = WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER);

        if (pMk->stMkInfo.ucStatus != ucStatus) /* ״̬��ͬ����Ҫ�޸� */
        {
            pMk->stMkInfo.ucStatus = ucStatus;
            if (KMC_KEY_STATUS_INACTIVE == ucStatus)
            {
                break_oper_if(!WSEC_GetUtcDateTime(&pMk->stMkInfo.stMkExpiredTimeUtc), WSEC_LOG_E("Get UTC fail."), nErrCode = WSEC_ERR_GET_CURRENT_TIME_FAIL);
            }

            WSEC_ARR_QuickSort(g_pKeystore->arrMk); /* ȷ��MK��������, �Ա�'�԰����' */
            nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);

            bNtf = WSEC_TRUE;
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    if (bNtf && (pMk!=WSEC_NULL_PTR))
    {
        KMC_PRI_NtfMkChanged(&pMk->stMkInfo, KMC_KEY_STATUS_INACTIVE == ucStatus ? KMC_KEY_INACTIVATED : KMC_KEY_ACTIVATED);
        WSEC_LOG_W3("The MK(DomainId=%u, KeyId=%u)'s status change to %u", ulDomainId, ulKeyId, ucStatus);
    }

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_RegisterMk
 ��������  : MK��Կע��
 �� �� ��  : ulDomainId: ����Ӧ����
             ulKeyId:    ��ԿID
             usKeyType:  ��Կ��;
             pPlainTextKey: ��Կ����
             ulKeyLen:      ��Կ���ĳ���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS: �ɹ�
             ����:         �������ԭ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��04��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_RegisterMk(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, WSEC_UINT16 usKeyType, const WSEC_BYTE* pPlainTextKey, WSEC_UINT32 ulKeyLen)
{
    KMC_DOMAIN_CFG_STRU* pstFoundDomain = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU*   pstFoundKeyType = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU    stKeyType = {0};
    KMC_CFG_DOMAIN_INFO_STRU* pstDomainInfo = WSEC_NULL_PTR;
    WSEC_BUFF stPlainTextKey = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_RegisterMk", KMC_IS_VALID_KEY_TYPE(usKeyType) && (pPlainTextKey) && (ulKeyLen > 0));

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        /* 1. �����Ϲ��� */
        /* 1) Domainδ���û���Կ��ԴΪϵͳ�Զ�������Ƿ� */
        break_oper_if(!KMC_PRI_SearchDomainKeyTypeCfg(ulDomainId, usKeyType, &pstFoundDomain, &pstFoundKeyType),
                       WSEC_LOG_E2("Domain KeyType(DomainId=%d, KeyType=%d) miss.", ulDomainId, usKeyType), nErrCode = WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS);
        WSEC_ASSERT(pstFoundDomain && pstFoundKeyType);

        pstDomainInfo = &pstFoundDomain->stDomainInfo;
        break_oper_if(pstDomainInfo->ucKeyFrom != KMC_MK_GEN_BY_IMPORT,
                       WSEC_LOG_E2("The keys of domain(%s, id=%d) defined as inner-generated, cannot register.", pstDomainInfo->szDesc, pstDomainInfo->ulId),
                       nErrCode = WSEC_ERR_KMC_CANNOT_REG_AUTO_KEY);
        
        /* 2) ��MK�Ѵ���, ���ֹע�� */
        break_oper_if(KMC_PRI_SearchMkByKeyId(g_pKeystore, ulDomainId, ulKeyId) >= 0,
                       WSEC_LOG_E2("The MasterKey(DomainId=%u, KeyId=%u) already exist.", ulDomainId, ulKeyId),
                       nErrCode = WSEC_ERR_KMC_REG_REPEAT_MK);

        /* 3) ��Կ���ĳ������Բ��������ֹע�� */
        break_oper_if(!KMC_IS_VALID_KEY_TYPE_LEN(ulKeyLen), WSEC_LOG_E2("MK len(%u) is too long, it must not over %u.", ulKeyLen, g_KmcSys.ulMkPlainLenMax), 
                       nErrCode = WSEC_ERR_KMC_MK_LEN_TOO_LONG);

        stKeyType.usKeyType     = usKeyType;
        stKeyType.ulKeyLen      = ulKeyLen;
        stKeyType.ulKeyLifeDays = pstFoundKeyType->ulKeyLifeDays;

        /* 2. ����MK */
        WSEC_BUFF_ASSIGN(stPlainTextKey, (WSEC_BYTE*)pPlainTextKey, ulKeyLen);

        nErrCode = KMC_PRI_CreateMkItem(g_pKeystore, pstDomainInfo, &stKeyType, &stPlainTextKey, ulKeyId);
        if (WSEC_SUCCESS == nErrCode) {nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);} /* ��Կע��ɹ�, ��дKeystore�ļ� */
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_CreateMk
 ��������  : ����Master Key
             ������ھ���Կ, ����״̬��Ϊ����.
 �� �� ��  : ulDomainId: MK������
             usKeyType:  ��Կ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��18��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
  2.��    ��   : 2015��8��7��
    ��    ��   : z00118096
    �޸�����   : �����´���MKǰ������MK�����Ƿ���, �������ϰ汾�Ĳ�����
                 �߼�: ������Կ״̬Ϊ����, �ٿ�¡MKʧ��.
*****************************************************************************/
WSEC_ERR_T KMC_CreateMk(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType)
{
    KMC_DOMAIN_CFG_STRU* pstDomain = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU*   pstKeyType = WSEC_NULL_PTR;
    WSEC_INT32 i;
    KMC_MEM_MK_STRU* pMemMk = WSEC_NULL_PTR;
    KMC_MK_INFO_STRU* pMk = WSEC_NULL_PTR;
    WSEC_UINT32 ulMkIdMax = 0;
    WSEC_ARRAY arrSelMk = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);

    /* ��ѯ��MK */
    arrSelMk = WSEC_ARR_Initialize(0, 0, WSEC_NULL_PTR, WSEC_NULL_PTR);
    return_oper_if(!arrSelMk, WSEC_LOG_E("Array Initialize fail."), WSEC_ERR_MALLOC_FAIL);

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        break_oper_if(!KMC_PRI_SearchDomainKeyTypeCfg(ulDomainId, usKeyType, &pstDomain, &pstKeyType),
                       WSEC_LOG_E2("The DomainKey(DomainId=%u, KeyType=%u) not found.", ulDomainId, usKeyType), nErrCode = WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS);
        break_oper_if(pstDomain->stDomainInfo.ucKeyFrom != KMC_MK_GEN_BY_INNER,
                      WSEC_LOG_E("Only inner Key can support this oper."), nErrCode = WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER);

        for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
        {
            pMemMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            break_oper_if(!pMemMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            continue_if((pMemMk->stMkInfo.ulDomainId != ulDomainId) || (pMemMk->stMkInfo.usType != usKeyType) || (pMemMk->stMkInfo.ucStatus != KMC_KEY_STATUS_ACTIVE));

            WSEC_ARR_Add(arrSelMk, &pMemMk->stMkInfo);
        }

        if (nErrCode != WSEC_SUCCESS) {break;}
        break_oper_if((WSEC_ARR_GetCount(arrSelMk) + WSEC_ARR_GetCount(g_pKeystore->arrMk)) > WSEC_MK_NUM_MAX, WSEC_LOG_E("Cannot CreateMK for the num of MK will overflow."), nErrCode = WSEC_ERR_KMC_MK_NUM_OVERFLOW);

        if (WSEC_ARR_GetCount(arrSelMk) > 0) /* �����ھ���Կ, ���ھ���Կ�������ٴ�������Կ */
        {
            for (i = 0; i < WSEC_ARR_GetCount(arrSelMk); i++)
            {
                pMk = (KMC_MK_INFO_STRU*)WSEC_ARR_GetAt(arrSelMk, i);
                break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
                nTemp = KMC_PRI_ReCreateMkItem(g_pKeystore, pMk);
                if (nTemp != WSEC_SUCCESS) {nErrCode = nTemp;}
            }
        }
        else /* �����ھ�MK, ���½�֮ */
        {
            KMC_PRI_GetMaxMkId(KMC_NOT_LOCK, ulDomainId, &ulMkIdMax);
            nErrCode = KMC_PRI_CreateMkItem(g_pKeystore, &pstDomain->stDomainInfo, pstKeyType, WSEC_NULL_PTR, ulMkIdMax + 1);
        }
        
        if (WSEC_SUCCESS == nErrCode) {nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);}
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    WSEC_ARR_Finalize(arrSelMk);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetMkDetailInner
 ��������  : ����DomainId, KeyId��ȡMK
 �� �� ��  : ulDomainId, ulKeyId: MKΨһ���ID
 �� �� ��  : pstMkInfo: MK��������[�ɿ�]
             pbKeyPlainText: MK����
 ��γ���  : pKeyLen: [in]�����pbKeyPlainText�ĳ���;
                      [out]���pbKeyPlainText��ʵ�ʳ���.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : �ú���ֻ���Ÿ�CBB�ڲ�����

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetMkDetailInner(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen)
{
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return KMC_PRI_GetMkDetail(KMC_NEED_LOCK, ulDomainId, ulKeyId, pstMkInfo, pbKeyPlainText, pKeyLen);
}

/*****************************************************************************
 �� �� ��  : KMC_GetMkDetail
 ��������  : ����DomainId, KeyId��ȡMK
 �� �� ��  : ulDomainId, ulKeyId: MKΨһ���ID
 �� �� ��  : pstMkInfo: MK��������[�ɿ�]
             pbKeyPlainText: MK����
 ��γ���  : pKeyLen: [in]�����pbKeyPlainText�ĳ���;
                      [out]���pbKeyPlainText��ʵ�ʳ���.
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : �ú�����ΪAPP���õĽӿ�, ֻ�ܲ�ѯ'�ⲿ����'����Կ.

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetMkDetail(WSEC_UINT32 ulDomainId, WSEC_UINT32 ulKeyId, KMC_MK_INFO_STRU* pstMkInfo, WSEC_BYTE* pbKeyPlainText, WSEC_UINT32* pKeyLen)
{
    KMC_MK_INFO_STRU stMk = {0};
    WSEC_BYTE        abKeyPlain[WSEC_MK_LEN_MAX] = {0};
    WSEC_UINT32      ulKeyLen;
    WSEC_ERR_T       nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_GetMkDetail", pbKeyPlainText && pKeyLen);

    do
    {
        ulKeyLen = sizeof(abKeyPlain);
        nErrCode = KMC_PRI_GetMkDetail(KMC_NEED_LOCK, ulDomainId, ulKeyId, &stMk, abKeyPlain, &ulKeyLen);
        if (nErrCode != WSEC_SUCCESS) {break;}
        
        /* ֻ��'�ⲿ������Կ'��������ѯ */
        break_oper_if(stMk.ucGenStyle != KMC_MK_GEN_BY_IMPORT,
                       WSEC_LOG_E2("The MK(DomainId=%u, KeyId=%u)'s KeyFrom cannot support this oper.", ulDomainId, ulKeyId), 
                       nErrCode = WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER);

        /* �����Կ���� */
        break_oper_if(*pKeyLen < ulKeyLen, 
                       WSEC_LOG_E2("*pKeyLen must at least given %d, but %d, so input-buff insufficient.", ulKeyLen, *pKeyLen),
                       nErrCode = WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);
        break_oper_if(WSEC_MEMCPY(pbKeyPlainText, *pKeyLen, abKeyPlain, ulKeyLen) != EOK, 
                       WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        *pKeyLen = ulKeyLen;

        /* ���MK������Ϣ */
        if (pstMkInfo)
        {
            break_oper_if(WSEC_MEMCPY(pstMkInfo, sizeof(KMC_MK_INFO_STRU), &stMk, sizeof(stMk)) != EOK, WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        }
    }do_end;

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_SecureEraseKeystore
 ��������  : ���׵�����Keystore�ļ�
             ԭ����: ɾ���ļ�ǰ, ���ļ�����ȫ����0��д.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��08��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SecureEraseKeystore()
{
    WSEC_CHAR* pszFile = WSEC_NULL_PTR;
    WSEC_SIZE_T i;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    for (i = 0; i < KMC_KSF_NUM; i++)
    {
        pszFile = g_KmcSys.apszKeystoreFile[i];
        continue_if(!pszFile);

        if (!WSEC_DeleteFileS(pszFile))
        {
            nErrCode = WSEC_FAILURE;
            WSEC_LOG_E1("WSEC_DeleteFileS(%s) fail.", pszFile);
        }
    }
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    if (WSEC_SUCCESS == nErrCode) {nErrCode = KMC_PRI_Finalize(KMC_NEED_LOCK);}

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_SetRootKeyCfg
 ��������  : ����RootKey����
 �� �� ��  : pstRkCfg: RK��������[��CBB�ڲ�����RK]
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SetRootKeyCfg(const KMC_CFG_ROOT_KEY_STRU* pstRkCfg)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_INT32 nTemp = 0;
    WSEC_BOOL bNtf = WSEC_FALSE;
    
    return_err_if_kmc_not_work;
    if (!KMC_IS_INNER_CREATE_RK) {return WSEC_ERR_KMC_RK_GENTYPE_REJECT_THE_OPER;}
    return_err_if_para_invalid("KMC_SetRootKeyCfg", pstRkCfg);

    /* ���������ݺϷ��� */
    return_oper_if(!KMC_CFG_IS_ROOT_KEY_VALID(pstRkCfg), WSEC_LOG_E("'ulRootKeyLifeDays' or 'ulRootMasterKeyIterations' invalid"), WSEC_ERR_INVALID_ARG);

    nTemp = (WSEC_INT32)pstRkCfg->ulRootKeyLifeDays;
    return_oper_if(nTemp < 1, WSEC_LOG_E1("'ulRootKeyLifeDays'(%d) must great than 0.", nTemp), WSEC_ERR_INVALID_ARG);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        nErrCode = (WSEC_MEMCPY(&g_pKmcCfg->stRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU), pstRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU)) == EOK) ? WSEC_SUCCESS : WSEC_ERR_MEMCPY_FAIL;
        if ((WSEC_SUCCESS == nErrCode) && (KMC_IS_MAN_CFG_FILE))
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_SetKeyManCfg
 ��������  : ����Key�����������
 �� �� ��  : pstKmCfg: Key����������������
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SetKeyManCfg(const KMC_CFG_KEY_MAN_STRU* pstKmCfg)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;

    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_SetKeyManCfg", pstKmCfg);
    return_err_if_para_invalid("KMC_SetKeyManCfg", KMC_CFG_IS_KEY_MAN_VALID(pstKmCfg));

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        nErrCode = (WSEC_MEMCPY(&g_pKmcCfg->stKmCfg, sizeof(KMC_CFG_KEY_MAN_STRU), pstKmCfg, sizeof(KMC_CFG_KEY_MAN_STRU)) == EOK) ? WSEC_SUCCESS : WSEC_ERR_MEMCPY_FAIL;
        if ((WSEC_SUCCESS == nErrCode) && (KMC_IS_MAN_CFG_FILE))
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetRootKeyCfg
 ��������  : ��ȡRootKey������Ϣ
 �� �� ��  : ��
 �� �� ��  : pstRkCfg: RK��������[��CBB�ڲ�����RK]
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetRootKeyCfg(KMC_CFG_ROOT_KEY_STRU* pstRkCfg)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    if (!KMC_IS_INNER_CREATE_RK) {return WSEC_ERR_KMC_RK_GENTYPE_REJECT_THE_OPER;}
    return_err_if_para_invalid("KMC_GetRootKeyCfg", pstRkCfg);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        break_oper_if(WSEC_MEMCPY(pstRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU), &g_pKmcCfg->stRkCfg, sizeof(KMC_CFG_ROOT_KEY_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetKeyManCfg
 ��������  : ��ȡ��Կ�������ڹ���������Ϣ
 �� �� ��  : ��
 �� �� ��  : pstRkCfg: RK��������[��CBB�ڲ�����RK]
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetKeyManCfg(KMC_CFG_KEY_MAN_STRU* pstKmCfg)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetKeyManCfg", pstKmCfg);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        break_oper_if(WSEC_MEMCPY(pstKmCfg, sizeof(KMC_CFG_KEY_MAN_STRU), &g_pKmcCfg->stKmCfg, sizeof(KMC_CFG_KEY_MAN_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_AddDomain
 ��������  : ����Domain����
 �� �� ��  : pstDomain: Domain��������
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_AddDomain(const KMC_CFG_DOMAIN_INFO_STRU* pstDomain)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;
    WSEC_INT32 i;
    KMC_MEM_MK_STRU* pMemMk = WSEC_NULL_PTR;
    
    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_AddDomain", pstDomain);
    return_err_if_domain_privacy(pstDomain->ulId);
    return_oper_if(!KMC_IS_VALID_MK_FROM(pstDomain->ucKeyFrom), WSEC_LOG_E("'ucKeyFrom' invalid."), WSEC_ERR_INVALID_ARG);

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        /* ����Domain���Ƿ����MK */
        for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
        {
            pMemMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            break_oper_if(!pMemMk, WSEC_LOG_E("MK array corrupted"), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);
            break_oper_if(pMemMk->stMkInfo.ulDomainId > pstDomain->ulId, oper_null, oper_null); /* ��ΪMK���鰴DomainId�������� */
            continue_if(pMemMk->stMkInfo.ulDomainId != pstDomain->ulId);
            break_oper_if(pMemMk->stMkInfo.ucGenStyle != pstDomain->ucKeyFrom,
                          WSEC_LOG_E2("MK(DomainId=%d, KeyId=%d) remained, but It's Keyfrom inconsistent with the Domain.", pMemMk->stMkInfo.ulDomainId, pMemMk->stMkInfo.ulKeyId),
                          nErrCode = WSEC_ERR_KMC_ADD_DOMAIN_DISCREPANCY_MK);
        }
        break_oper_if(nErrCode != WSEC_SUCCESS, oper_null, oper_null);

        nErrCode = KMC_PRI_AddDomain2Array(g_pKmcCfg, pstDomain);
        if ((WSEC_SUCCESS == nErrCode) && (KMC_IS_MAN_CFG_FILE))
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_RmvDomain
 ��������  : ɾ��Domain
 �� �� ��  : ulDomainId: DomainΨһ��ʶ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��10��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_RmvDomain(WSEC_UINT32 ulDomainId)
{
    WSEC_INT32 nAt = -1;
    KMC_DOMAIN_CFG_STRU stDomain = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;

    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        stDomain.stDomainInfo.ulId = ulDomainId;
        nAt = WSEC_ARR_BinarySearchAt(g_pKmcCfg->arrDomainCfg, &stDomain);
        break_oper_if(nAt < 0, WSEC_LOG_W1("The domain(Id=%u) not existed", ulDomainId), nErrCode = WSEC_ERR_KMC_DOMAIN_MISS);

        WSEC_ARR_RemoveAt(g_pKmcCfg->arrDomainCfg, nAt);
        WSEC_LOG_W1("Removed the domain(Id=%u).", ulDomainId);

        if (KMC_IS_MAN_CFG_FILE)
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_AddDomainKeyType
 ��������  : ��ָ��Domain������KeyType
 �� �� ��  : ulDomainId: Domain��ʾ
             pstKeyType: KeyType����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_AddDomainKeyType(WSEC_UINT32 ulDomainId, const KMC_CFG_KEY_TYPE_STRU* pstKeyType)
{
    KMC_DOMAIN_CFG_STRU* pDomain = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU* pTempKeyType = WSEC_NULL_PTR;
    WSEC_INT32 nTemp = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;
    
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_AddDomainKeyType", pstKeyType);

    return_oper_if(!KMC_IS_VALID_KEY_TYPE(pstKeyType->usKeyType), WSEC_LOG_E1("Input pstKeyType->usKeyType(%d) invalid.", pstKeyType->usKeyType), WSEC_ERR_INVALID_ARG);
    return_oper_if(!KMC_IS_VALID_KEY_TYPE_LEN(pstKeyType->ulKeyLen), 
                   WSEC_LOG_E2("Input pstKeyType->ulKeyLen(%d) invalid. it must not over %u", pstKeyType->ulKeyLen, g_KmcSys.ulMkPlainLenMax), 
                   WSEC_ERR_INVALID_ARG);
    nTemp = (WSEC_INT32)pstKeyType->ulKeyLifeDays;
    return_oper_if(nTemp < 1, WSEC_LOG_E1("'KeyLifeDays'(%d) must great than 0", nTemp), WSEC_ERR_INVALID_ARG);

    return_oper_if(KMC_PRI_SearchDomainKeyTypeCfg(ulDomainId, pstKeyType->usKeyType, &pDomain, &pTempKeyType),
                   WSEC_LOG_E2("The KeyType(DomainId=%u, KeyType=%u) already exist.", ulDomainId, pstKeyType->usKeyType), WSEC_ERR_KMC_ADD_REPEAT_KEY_TYPE);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        nErrCode = KMC_PRI_AddDomainKeyType2Array(g_pKmcCfg, ulDomainId, pstKeyType);
        if ((WSEC_SUCCESS == nErrCode) && (KMC_IS_MAN_CFG_FILE))
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_RmvDomainKeyType
 ��������  : ɾ��ָ��Domain�µ�KeyType
 �� �� ��  : ulDomainId, usKeyType: KeyType��Ψһ��ʶ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_RmvDomainKeyType(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType)
{
    KMC_DOMAIN_CFG_STRU stFindDomain = {0};
    KMC_CFG_KEY_TYPE_STRU   stFindKeyType = {0};
    KMC_DOMAIN_CFG_STRU* pDomainCfg = WSEC_NULL_PTR;
    WSEC_INT32 nIndex = -1;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;
    
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        /* ��Domain */
        stFindDomain.stDomainInfo.ulId = ulDomainId;
        nIndex = WSEC_ARR_BinarySearchAt(g_pKmcCfg->arrDomainCfg, &stFindDomain);
        break_oper_if(nIndex < 0, WSEC_LOG_E1("The Domain(Id=%u) not exist.", ulDomainId), nErrCode = WSEC_ERR_KMC_DOMAIN_MISS);

        pDomainCfg = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_GetAt(g_pKmcCfg->arrDomainCfg, nIndex);
        break_oper_if(!pDomainCfg, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        /* ��Domain�µ�KeyTypeλ�� */
        stFindKeyType.usKeyType = usKeyType;
        nIndex = WSEC_ARR_BinarySearchAt(pDomainCfg->arrKeyTypeCfg, &stFindKeyType);
        break_oper_if(nIndex < 0, WSEC_LOG_E2("The KeyType(DomainId=%u, KeyType=%u) not exist.", ulDomainId, usKeyType), nErrCode = WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS);

        /* ɾ�� */
        WSEC_ARR_RemoveAt(pDomainCfg->arrKeyTypeCfg, nIndex);

        WSEC_LOG_E2("Remove KeyType(DomainId=%u, KeyType=%u) succ.", ulDomainId, usKeyType);

        if (KMC_IS_MAN_CFG_FILE)
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetDomainCount
 ��������  : ��ȡ���õ�Domain����
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 0������: ���õ�Domain����;
                ����: ����
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_GetDomainCount()
{
    WSEC_INT32 nCount = 0;
    
    if (g_KmcSys.eState != WSEC_RUNNING) {return -1;}

    KMC_PRI_Lock(KMC_LOCK_CFG);
    nCount = WSEC_ARR_GetCount(g_pKmcCfg->arrDomainCfg);
    KMC_PRI_Unlock(KMC_LOCK_CFG);
    
    return nCount;
}

/*****************************************************************************
 �� �� ��  : KMC_GetDomain
 ��������  : ��ȡָ��������Domain
 �� �� ��  : Index: Domain����λ��(0��ʼ������)
 �� �� ��  : pstDomainInfo: ���Domain������Ϣ
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : �ú���һ���KMC_GetDomainCount���ʹ��, ��:
             ...
             KMC_CFG_DOMAIN_INFO_STRU stDomainInfo;
             int i;
             
             for (i = 0; i < KMC_GetDomainCount(); i++)
             {
                if (KMC_GetDomain(i, &stDomainInfo) == WSEC_SUCCESS)
                {
                    // ʹ��stDomainInfo
                }
             }
             ...
 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetDomain(WSEC_INT32 Index, KMC_CFG_DOMAIN_INFO_STRU* pstDomainInfo)
{
    KMC_DOMAIN_CFG_STRU* pDomainCfg = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
 
    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetDomain", (Index >= 0) && pstDomainInfo);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        break_oper_if(Index >= WSEC_ARR_GetCount(g_pKmcCfg->arrDomainCfg), oper_null, nErrCode = WSEC_ERR_INVALID_ARG);
        
        pDomainCfg = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_GetAt(g_pKmcCfg->arrDomainCfg, Index);
        break_oper_if(!pDomainCfg, WSEC_LOG_E("memory access fail."), nErrCode = WSEC_ERR_OPER_ARRAY_FAIL);

        break_oper_if(WSEC_MEMCPY(pstDomainInfo, sizeof(KMC_CFG_DOMAIN_INFO_STRU), &pDomainCfg->stDomainInfo, sizeof(KMC_CFG_DOMAIN_INFO_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL); 
     }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetDomainKeyTypeCount
 ��������  : ��ȡDomain��KeyType����
 �� �� ��  : ulDomainId: DomainΨһ��ʶ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 0������: ���õ�Domain KeyType����;
                ����: ����
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 KMC_GetDomainKeyTypeCount(WSEC_UINT32 ulDomainId)
{
    KMC_DOMAIN_CFG_STRU stFindDomain = {0};
    KMC_DOMAIN_CFG_STRU* pDomain = WSEC_NULL_PTR;
    WSEC_INT32 nReturn = -1;
    
    if (g_KmcSys.eState != WSEC_RUNNING) {return nReturn;}

    stFindDomain.stDomainInfo.ulId = ulDomainId;

    KMC_PRI_Lock(KMC_LOCK_CFG);
    pDomain = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_BinarySearch(g_pKmcCfg->arrDomainCfg, &stFindDomain);
    if (pDomain) {nReturn = WSEC_ARR_GetCount(pDomain->arrKeyTypeCfg);}
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    return nReturn;
}

/*****************************************************************************
 �� �� ��  : KMC_GetDomainKeyType
 ��������  : ��ȡָ��Domainָ��������KeyType
 �� �� ��  : ulDomainId: DomainΨһ��ʶ
             Index: ����ѯKeyType����λ��(0��ʼ������)
 �� �� ��  : pstDomainKeyType: ���KeyType
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetDomainKeyType(WSEC_UINT32 ulDomainId, WSEC_INT32 Index, KMC_CFG_KEY_TYPE_STRU* pstKeyType)
{
    KMC_DOMAIN_CFG_STRU stFindDomain = {0};
    KMC_DOMAIN_CFG_STRU* pDomain = WSEC_NULL_PTR;
    KMC_CFG_KEY_TYPE_STRU* pstExistKeyType = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetDomainKeyType", pstKeyType);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        stFindDomain.stDomainInfo.ulId = ulDomainId;
        pDomain = (KMC_DOMAIN_CFG_STRU*)WSEC_ARR_BinarySearch(g_pKmcCfg->arrDomainCfg, &stFindDomain);
        break_oper_if(!pDomain, WSEC_LOG_E1("Domain(Id=%u) not exist.", ulDomainId), nErrCode = WSEC_ERR_KMC_DOMAIN_MISS);

        pstExistKeyType = (KMC_CFG_KEY_TYPE_STRU*)WSEC_ARR_GetAt(pDomain->arrKeyTypeCfg, Index);
        break_oper_if(!pstExistKeyType, WSEC_LOG_E1("Get KeyType at %d fail.", Index), nErrCode = WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS);

        break_oper_if(WSEC_MEMCPY(pstKeyType, sizeof(KMC_CFG_KEY_TYPE_STRU), pstExistKeyType, sizeof(KMC_CFG_KEY_TYPE_STRU)) != EOK,
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetExpiredMkStartPos
 ��������  : ��ȡ����MK����λ��
 �� �� ��  : ��
 �� �� ��  : pPos: ���MK�׸�������Կ��λ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_GetExpiredMkStartPos(WSEC_POSITION* pPos)
{
    WSEC_INT32 i;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;

    *pPos = -1;
    if (g_KmcSys.eState != WSEC_RUNNING) {return;}
    WSEC_CallPeriodicFunc(KMC_ChkMkStatus); /* ��ȡ������Կ֮ǰ, ��Ҫ�����Կ״̬ */

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
    {
        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
        continue_if(!pMk);
        if (KMC_KEY_STATUS_INACTIVE == pMk->stMkInfo.ucStatus)
        {
            *pPos = i;
            break;
        }
    }
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_GetExpiredMkByPos
 ��������  : ��ȡָ��λ���ϵĹ���MK�������һ������MK��λ��
 �� �� ��  : ��
 �� �� ��  : pstExpiredMk: �������MK
 ��γ���  : pPosNow: [in]��ǰλ�õĹ�����Կ
                      [out]��һ��������Կ��λ��
 �� �� ֵ  : ��ȡ��ǰλ���ϵ���Կ�Ƿ�ɹ�
 �ر�ע��  : �ú�����KMC_GetExpiredMkStartPos���ʹ��, ����:
              ......
              WSEC_POSITION pos;
              KMC_MK_INFO_STRU stExpiredMk;

              KMC_GetExpiredMkStartPos(&pos);
              while (KMC_GetExpiredMkByPos(&pos, &stExpiredMk))
              {
                  <ʹ��stExpiredMk������>
              }
              ......

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL KMC_GetExpiredMkByPos(WSEC_POSITION* pPosNow, KMC_MK_INFO_STRU* pstExpiredMk)
{
    WSEC_INT32 i = 0, nCount = 0;
    KMC_MEM_MK_STRU* pMk = WSEC_NULL_PTR;
    WSEC_BOOL bOk = WSEC_TRUE;
    
    if (g_KmcSys.eState != WSEC_RUNNING) {return WSEC_FALSE;}
    if ((!pPosNow) || (!pstExpiredMk)) {return WSEC_FALSE;}

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        nCount = WSEC_ARR_GetCount(g_pKeystore->arrMk);

        i = *pPosNow;
        break_oper_if((i < 0) || (i >= nCount), oper_null, bOk = WSEC_FALSE);

        pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
        break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), bOk = WSEC_FALSE);

        break_oper_if(pMk->stMkInfo.ucStatus != KMC_KEY_STATUS_INACTIVE, oper_null, bOk = WSEC_FALSE); /* ˵�������߶�pPosNow���ݽ����˸�д, ���ڱ�̴��� */

        break_oper_if(WSEC_MEMCPY(pstExpiredMk, sizeof(KMC_MK_INFO_STRU), &pMk->stMkInfo, sizeof(pMk->stMkInfo)) != EOK, WSEC_LOG_E4MEMCPY, bOk = WSEC_FALSE);

        /* Ѱ����һ���ݵ�λ�� */
        *pPosNow = -1; /* ��ʾû��'��һ��'�� */
        for (i++; i < nCount; i++)
        {
            pMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            break_oper_if(!pMk, WSEC_LOG_E("memory access fail."), bOk = WSEC_FALSE);
            if (KMC_KEY_STATUS_INACTIVE == pMk->stMkInfo.ucStatus)
            {
                *pPosNow = i;
                break;
            }
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    return bOk;
}

/*****************************************************************************
 �� �� ��  : KMC_GetMkByType
 ��������  : ulDomainId: MK����Domain;
             usKeyType:  KeyType
 �� �� ��  : ��
 �� �� ��  : pKeyBuf: ���MK��Կ����;
             pKeyId:  ���MK ID;
 ��γ���  : pKeyLen: [in]������pKeyBuf�ĳ���;
                      [out]��Կ����ʵ�ʳ���
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetMkByType(WSEC_UINT32 ulDomainId, WSEC_UINT16 usKeyType, WSEC_BYTE* pKeyBuf, WSEC_UINT32 *pKeyLen, WSEC_UINT32 *pKeyId)
{
    KMC_MEM_MK_STRU stMkFind = {0};
    KMC_MEM_MK_STRU* pstMkFound = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    return_err_if_kmc_not_work;
    return_err_if_domain_privacy(ulDomainId);
    return_err_if_para_invalid("KMC_GetMkByType", pKeyBuf && pKeyLen && pKeyId);

    stMkFind.stMkInfo.ulDomainId = ulDomainId;
    stMkFind.stMkInfo.usType     = usKeyType;
    stMkFind.stMkInfo.ucStatus   = KMC_KEY_STATUS_ACTIVE; /* ������Щ���õ�MK */

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        pstMkFound = WSEC_ARR_BinarySearch(g_pKeystore->arrMk, &stMkFind);
        break_oper_if(!pstMkFound, WSEC_LOG_E2("Cannot find Active-MK(DomainId=%u, KeyType=%u)", ulDomainId, usKeyType), nErrCode = WSEC_ERR_KMC_MK_MISS);
        break_oper_if(*pKeyLen < pstMkFound->stMkRear.ulPlainLen, 
                       WSEC_LOG_E2("The buffer-len of pKeyBuf is too small(%d < %d).", *pKeyLen, pstMkFound->stMkRear.ulPlainLen), 
                       nErrCode = WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH);

        *pKeyId = pstMkFound->stMkInfo.ulKeyId;
        *pKeyLen = pstMkFound->stMkRear.ulPlainLen;
        KMC_UNMASK_MK_TO(pstMkFound, pKeyBuf, *pKeyLen); /* MK�����������뷽ʽ�������ڴ棬��ȥ�� */
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);
    
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_SetDataProtectCfg
 ��������  : �����������ݱ������㷨����
 �� �� ��  : eType: �㷨����;
             pstPara: ��Ӧ���������ݱ����㷨����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_SetDataProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, const KMC_CFG_DATA_PROTECT_STRU* pstPara)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_BOOL bNtf = WSEC_FALSE;
    
    return_err_if_kmc_not_work;

    nErrCode = KMC_PRI_ChkProtectCfg(eType, pstPara);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E("KMC_PRI_ChkProtectCfg() return fail."), nErrCode);

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        break_oper_if(WSEC_MEMCPY(&g_pKmcCfg->astDataProtectCfg[eType], sizeof(KMC_CFG_DATA_PROTECT_STRU), pstPara, sizeof(KMC_CFG_DATA_PROTECT_STRU)) != EOK,
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
        if (KMC_IS_MAN_CFG_FILE)
        {
            nErrCode = KMC_PRI_WriteKcfSafety(g_pKmcCfg);
            bNtf = (nErrCode != WSEC_SUCCESS);
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);

    if_oper(bNtf, KMC_PRI_NtfWriCfgFileFail(nErrCode));

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : KMC_GetDataProtectCfg
 ��������  : ��ȡָ���㷨���͵�����
 �� �� ��  : eType: �㷨����;
 �� �� ��  : pstPara: ����㷨����
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T KMC_GetDataProtectCfg(KMC_SDP_ALG_TYPE_ENUM eType, KMC_CFG_DATA_PROTECT_STRU *pstPara)
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_kmc_not_work;
    return_err_if_para_invalid("KMC_GetDataProtectCfg", pstPara && WSEC_IS3(eType, SDP_ALG_ENCRPT, SDP_ALG_INTEGRITY, SDP_ALG_PWD_PROTECT));

    KMC_PRI_Lock(KMC_LOCK_CFG);
    do
    {
        break_oper_if(WSEC_MEMCPY(pstPara, sizeof(KMC_CFG_DATA_PROTECT_STRU), &g_pKmcCfg->astDataProtectCfg[eType], sizeof(KMC_CFG_DATA_PROTECT_STRU)) != EOK, 
                      WSEC_LOG_E4MEMCPY, nErrCode = WSEC_ERR_MEMCPY_FAIL);
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_CFG);
    
    return nErrCode;
}

/*****************************************************************************
> �� �� ��  : KMC_GetAlgList
> ��������  : ��ѯSDP���֧�ֵ��㷨ȫ�����Իص�������ʽʵ�֡�
> �������  : pfProcAlg ��ʾ�������㷨�����Ϣ�Ļص��������ûص��������ڱ���������ÿ���㷨ʱ����
>             pReserved ��ʾ�����������������ݻص����������������Ϣ
> �������  : pReserved ��ʾ�����������������ݻص����������������Ϣ
> �� �� ֵ  : �����룬WSEC_SUCCESS��ʾ�����������ء�

> �޸���ʷ      :
>  1.��    ��   : 2014��7��1��
>    ��    ��   : j00265291
>    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T KMC_GetAlgList(
    KMC_FP_ProcAlg pfProcAlg,
    INOUT WSEC_VOID* pReserved
    )
{
    WSEC_UINT32 ulRet = WSEC_SUCCESS;

    /* check input */
    return_err_if_para_invalid("KMC_GetAlgList", pfProcAlg);

    ulRet = (WSEC_UINT32)CAC_GetAlgList((CAC_FP_ProcAlg)pfProcAlg, pReserved);
    return_oper_if((WSEC_SUCCESS != ulRet), WSEC_LOG_E("CAC_GetAlgList() fail."), ulRet);

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : KMC_RefreshMkMask
 ��������  : �������ڶ�MK���ļ��ڵ������, ����MK��Կ���¼���
 �� �� ��  : 3��������δʹ��(����ע�ắ��������Ҫ)
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_RefreshMkMask(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo)
{
    KMC_MEM_MK_STRU* pstMk = WSEC_NULL_PTR;
    WSEC_INT32 i;

    WSEC_UNREFER(pstLocalNow);
    WSEC_UNREFER(pstUtcNow);
    WSEC_UNREFER(pstRptProgress);
    WSEC_UNREFER(pExecInfo);

    if (g_KmcSys.eState != WSEC_RUNNING) {return;}

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        /* 1. �þ����뻹ԭ */
        for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
        {
            pstMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            continue_if(!pstMk);
            KMC_UNMASK_MK(pstMk);
        }

        /* 2. ˢ������ */
        WSEC_UNCARE(CAC_Random(g_KmcSys.abMkMaskCode, sizeof(g_KmcSys.abMkMaskCode))); /* �����ע��������ɳɹ���� */

        /* 3. ����������� */
        for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
        {
            pstMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            continue_if(!pstMk);
            KMC_MASK_MK(pstMk);
        }
    } do_end;
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_ChkRkStatus
 ��������  : ���Root Key�Ƿ����
 �� �� ��  : pstLocalNow: ��ǰ����ʱ��;
             pstUtcNow:   ��ǰUTCʱ��;
 �� �� ��  : ��
 ��γ���  : pExecInfo:   ��¼ִ����Ϣ
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_ChkRkStatus(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo)
{
    const KMC_RK_ATTR_STRU* pstRkInfo = WSEC_NULL_PTR;
    const KMC_CFG_KEY_MAN_STRU* pstCfg = WSEC_NULL_PTR;
    KMC_RK_ATTR_STRU* pstRkNtf = WSEC_NULL_PTR;
    WSEC_INT32 nRemainLifeDays = 0; /* ��ʱ�̾���Կ���ڵ����� */
    WSEC_CHAR szTime[30] = {0};

	WSEC_UNREFER(pstRptProgress);

    if_oper(g_KmcSys.eState != WSEC_RUNNING, return);
    WSEC_ASSERT(pstLocalNow && pstUtcNow && pExecInfo);

    KMC_PRI_Lock(KMC_LOCK_BOTH);
    do
    {
        if (!KMC_PRI_IsTime4ChkKey(pstLocalNow, &g_pKmcCfg->stKmCfg, pExecInfo)) {break;} /* ʱ��δ�� */

        WSEC_DateTimeCopy(&pExecInfo->stPreExecTime, pstLocalNow); /* ��¼���ʱ�� */

        pstRkInfo = &g_pKeystore->stRkInfo;
        pstCfg    = &g_pKmcCfg->stKmCfg;

        /* ����Ƿ�Ԥ�� */
        nRemainLifeDays = WSEC_DateTimeDiff(dtpDay, pstUtcNow, &pstRkInfo->stRkExpiredTimeUtc);

        if (nRemainLifeDays > (WSEC_INT32)pstCfg->ulWarningBeforeKeyExpiredDays) {break;}

        WSEC_LOG_E1("WARNING: Root key will expire at %s(UTC)", WSEC_DateTime2String(&pstRkInfo->stRkExpiredTimeUtc, szTime, sizeof(szTime)));

        if (nRemainLifeDays <= 0) /* ��Կ�Ѿ�����,  */
        {
            WSEC_LOG_W1("Root key expired, KMC_PRI_UpdateRootKeyPri()=%u", KMC_PRI_UpdateRootKeyPri(WSEC_NULL_PTR, 0, KMC_NOT_LOCK));
        }
        else
        {
            pstRkNtf = WSEC_CLONE_BUFF(pstRkInfo, sizeof(KMC_RK_ATTR_STRU));
        }
    }do_end;
    KMC_PRI_Unlock(KMC_LOCK_BOTH);

    if (pstRkNtf)
    {
        KMC_PRI_NtfRkExpire(pstRkNtf, nRemainLifeDays);
        WSEC_FREE(pstRkNtf);
    }

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_ChkMkStatus
 ��������  : �������Master Key�Ƿ����
 �� �� ��  : pstLocalNow: ��ǰ����ʱ��;
             pstUtcNow:   ��ǰUTCʱ��;
 �� �� ��  : ��
 ��γ���  : pExecInfo:   ��¼ִ����Ϣ
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_ChkMkStatus(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo)
{
    WSEC_INT32 nMkNum = 0, i = 0, nRemainLifeDays = 0, nRemainMkNum;
    KMC_MEM_MK_STRU* pMemMk = WSEC_NULL_PTR;
    KMC_MK_INFO_STRU* pMk = WSEC_NULL_PTR;
    KMC_MK_EXPIRE_NTF_STRU* pExpireNtf = WSEC_NULL_PTR;
    KMC_MK_NUM_OVERFLOW_STRU stMkOverflowNtf = {0};
    WSEC_ARRAY arrExpiredMk = WSEC_NULL_PTR, arrWarnMk = WSEC_NULL_PTR;
    WSEC_SPEND_TIME_STRU stTimer = {0};
    WSEC_BOOL bIsTime4ChkKey = WSEC_FALSE;
    const WSEC_UINT32 ulRemainMkNumPercent = 10; /* ʣ��MK����ָ��ռ�ܹ��İٷֱ� */
    WSEC_ERR_T ulErrCode = WSEC_SUCCESS;

    if_oper(g_KmcSys.eState != WSEC_RUNNING, return);
    WSEC_ASSERT(pstLocalNow && pstUtcNow && pExecInfo);

    /* 1. ���ʱ���Ƿ�? */
    KMC_PRI_Lock(KMC_LOCK_CFG);
    bIsTime4ChkKey = KMC_PRI_IsTime4ChkKey(pstLocalNow, &g_pKmcCfg->stKmCfg, pExecInfo);
    KMC_PRI_Unlock(KMC_LOCK_CFG);
    if (!bIsTime4ChkKey) {return;} /* ʱ��δ�� */

    arrExpiredMk = WSEC_ARR_Initialize(0, 0, WSEC_NULL_PTR, WSEC_NULL_PTR);
    arrWarnMk = WSEC_ARR_Initialize(0, 0, WSEC_NULL_PTR, WSEC_ARR_StdRemoveElement);
    if ((!arrExpiredMk) || (!arrWarnMk))
    {
        WSEC_LOG_E("WSEC_ARR_Initialize() fail");
        if_oper(arrExpiredMk, WSEC_ARR_Finalize(arrExpiredMk));
        if_oper(arrWarnMk, WSEC_ARR_Finalize(arrWarnMk));
        return;
    }else{;}
    
    /* 2. ��ȡ����ЩMK���Ϊ������ */
    KMC_PRI_Lock(KMC_LOCK_BOTH);
    nMkNum = WSEC_ARR_GetCount(g_pKeystore->arrMk);
    for (i = 0; i < nMkNum; i++)
    {
        pMemMk = WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
        continue_if(!pMemMk);

        pMk = &pMemMk->stMkInfo;
        continue_if(pMk->ucStatus != KMC_KEY_STATUS_ACTIVE);

        nRemainLifeDays = WSEC_DateTimeDiff(dtpDay, pstUtcNow, &pMk->stMkExpiredTimeUtc);
        continue_if((nRemainLifeDays > 0) && ((WSEC_UINT32)nRemainLifeDays > g_pKmcCfg->stKmCfg.ulWarningBeforeKeyExpiredDays));
        
        if ((nRemainLifeDays <= 0) && (KMC_MK_GEN_BY_INNER == pMk->ucGenStyle)) /* �ڲ���Կ���� */
        {
            WSEC_ARR_Add(arrExpiredMk, pMk);
        }
        else /* �ⲿ��Կ, ����ֻ��Ԥ�����ڲ���Կ */
        {
            pExpireNtf = (KMC_MK_EXPIRE_NTF_STRU*)WSEC_MALLOC(sizeof(KMC_MK_EXPIRE_NTF_STRU));
            break_oper_if(!pExpireNtf, WSEC_LOG_E4MALLOC(sizeof(KMC_MK_EXPIRE_NTF_STRU)), oper_null);
            break_oper_if(WSEC_MEMCPY(&pExpireNtf->stMkInfo, sizeof(pExpireNtf->stMkInfo), pMk, sizeof(KMC_MK_INFO_STRU)) != EOK, WSEC_LOG_E4MEMCPY, oper_null);
            pExpireNtf->nRemainDays = nRemainLifeDays;

            WSEC_UNCARE(WSEC_ARR_Add(arrWarnMk, pExpireNtf));
        }

        WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)nMkNum, (WSEC_UINT32)(i + 1), WSEC_NULL_PTR); /* ����Ӧȡ�� */
    }

    /* 3. �������MK */
    nMkNum = WSEC_ARR_GetCount(arrExpiredMk);
    if (nMkNum > 0)
    {
        for (i = 0; i < nMkNum; i++)
        {
            pMk = (KMC_MK_INFO_STRU*)WSEC_ARR_GetAt(arrExpiredMk, i);
            continue_if(!pMk);

            if (KMC_MK_GEN_BY_INNER == pMk->ucGenStyle) /* �ڲ�MK, ��Ҫ���´���, �����ⲿMK�������. */
            {
                ulErrCode = KMC_PRI_ReCreateMkItem(g_pKeystore, pMk);
                WSEC_LOG_E3("OnTiner: The MK(DomainId=%u, KeyId=%u) expired, ReCreateMk()=%u", pMk->ulDomainId, pMk->ulKeyId, ulErrCode);
            }
            WSEC_RptProgress(pstRptProgress, &stTimer, (WSEC_UINT32)nMkNum, (WSEC_UINT32)(i + 1), WSEC_NULL_PTR); /* ����Ӧȡ�� */
        }
        WSEC_ARR_QuickSort(g_pKeystore->arrMk);
        ulErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR);

        if (ulErrCode != WSEC_SUCCESS) {WSEC_LOG_E1("KMC_PRI_WriteKsfSafety()=%u", ulErrCode);}
    }
    stMkOverflowNtf.ulNum = WSEC_ARR_GetCount(g_pKeystore->arrMk);
    KMC_PRI_Unlock(KMC_LOCK_BOTH);
    WSEC_RptProgress(pstRptProgress, WSEC_NULL_PTR, (WSEC_UINT32)nMkNum, (WSEC_UINT32)nMkNum, WSEC_NULL_PTR); /* ȷ������100%�ϱ� */

    /* 4. ��Ԥ�� */
    for (i = 0; i < WSEC_ARR_GetCount(arrWarnMk); i++)
    {
        pExpireNtf = (KMC_MK_EXPIRE_NTF_STRU*)WSEC_ARR_GetAt(arrWarnMk, i);
        WSEC_ASSERT(pExpireNtf);
        WSEC_NOTIFY(WSEC_KMC_NTF_MK_EXPIRE, pExpireNtf, sizeof(KMC_MK_EXPIRE_NTF_STRU));
    }

    /* 5. MK�����澯 */
    nRemainMkNum = WSEC_MK_NUM_MAX - stMkOverflowNtf.ulNum;
    WSEC_ASSERT(nRemainMkNum >= 0); /* ���Ϊ����, ��KMC_PRI_ReCreateMkItem()�����߼����� */
    if ((WSEC_INT32)(nRemainMkNum * 100) < (WSEC_INT32)(WSEC_MK_NUM_MAX * ulRemainMkNumPercent))
    {
        stMkOverflowNtf.ulMaxNum = WSEC_MK_NUM_MAX;
        WSEC_LOG_E2("MkNum(%d) will overflow(%d)", stMkOverflowNtf.ulNum, stMkOverflowNtf.ulMaxNum);
        WSEC_NOTIFY(WSEC_KMC_NTF_MK_NUM_OVERFLOW, &stMkOverflowNtf, sizeof(stMkOverflowNtf));
    }
    
    WSEC_ARR_Finalize(arrExpiredMk);
    WSEC_ARR_Finalize(arrWarnMk);

    /* Misinformation: FORTIFY.Memory_Leak (because 'pExpireNtf' freed when 'WSEC_ARR_Finalize(arrWarnMk)') */

    return;
}

/*****************************************************************************
 �� �� ��  : KMC_Correct4Clock
 ��������  : У����ʱ����Ϣ����ȷ������
             CBB��ʼ��ʱ��Ҫ����ʱ��, �����ʱϵͳʱ����δ���������ȡ��ʱ����Ϣ
             ����ȷ, ��APP��⵽ʱ��ϵͳ���������֪CBB, ��ʱ, CBB��У��ʱ������.
 �������  : ��
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2015��5��25��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID KMC_Correct4Clock()
{
    const WSEC_BYTE bCorrected = 1;
    WSEC_BOOL bDirty = WSEC_FALSE;
    WSEC_SYSTIME_T tNowUtc = {0};
    KMC_MEM_MK_STRU* pMemMk = WSEC_NULL_PTR;
    KMC_MK_INFO_STRU* pMk = WSEC_NULL_PTR;
    WSEC_INT32 i = 0;
    WSEC_INT32 nDays = 0;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    if_oper(g_KmcSys.eState != WSEC_RUNNING, return);
    if_oper(bCorrected == g_KmcSys.bCorrectClockFlag, return);
    if (!WSEC_GetUtcDateTime(&tNowUtc))
    {
        WSEC_LOG_E("WSEC_GetUtcDateTime() fail");
        return;
    }
    if_oper(!WSEC_IS_NORMAL_YEAR(tNowUtc.uwYear), return); /* ʱ��ϵͳ��δ���� */

    KMC_PRI_Lock(KMC_LOCK_KEYSTORE);
    do
    {
        if (!WSEC_IS_NORMAL_YEAR(g_pKeystore->stRkInfo.stRkCreateTimeUtc.uwYear)) /* ��Ҫ����ʱ�� */
        {
            nDays = WSEC_DateTimeDiff(dtpDay, &g_pKeystore->stRkInfo.stRkCreateTimeUtc, &g_pKeystore->stRkInfo.stRkExpiredTimeUtc);
            nErrCode = KMC_PRI_SetLifeTime(nDays, &g_pKeystore->stRkInfo.stRkCreateTimeUtc, &g_pKeystore->stRkInfo.stRkExpiredTimeUtc);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_SetLifeTime()=%u", nErrCode), oper_null);
            bDirty = WSEC_TRUE;
        }

        for (i = 0; i < WSEC_ARR_GetCount(g_pKeystore->arrMk); i++)
        {
            pMemMk = (KMC_MEM_MK_STRU*)WSEC_ARR_GetAt(g_pKeystore->arrMk, i);
            continue_if(!pMemMk); /* �ݴ�: �������� */

            pMk = &pMemMk->stMkInfo;
            continue_if(WSEC_IS_NORMAL_YEAR(pMk->stMkCreateTimeUtc.uwYear));

            nDays = WSEC_DateTimeDiff(dtpDay, &pMk->stMkCreateTimeUtc, &pMk->stMkExpiredTimeUtc);
            nErrCode = KMC_PRI_SetLifeTime(nDays, &pMk->stMkCreateTimeUtc, &pMk->stMkExpiredTimeUtc);
            break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("KMC_PRI_SetLifeTime()=%u", nErrCode), oper_null);
            bDirty = WSEC_TRUE;
        }
    }do_end;
    if_oper((WSEC_SUCCESS == nErrCode) && bDirty, nErrCode = KMC_PRI_WriteKsfSafety(g_pKeystore, WSEC_NULL_PTR)); /* �����ļ� */
    KMC_PRI_Unlock(KMC_LOCK_KEYSTORE);

    if_oper(WSEC_SUCCESS == nErrCode, g_KmcSys.bCorrectClockFlag = bCorrected);
    return;
}

/*****************************************************************************
 �� �� ��  : KMC_ShowStructSize
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
WSEC_VOID KMC_ShowStructSize(WSEC_FP_ShowStructSize pfShow)
{
    pfShow("KMC_MK_INFO_STRU", sizeof(KMC_MK_INFO_STRU));
    pfShow("KMC_CFG_ROOT_KEY_STRU", sizeof(KMC_CFG_ROOT_KEY_STRU));
    pfShow("KMC_CFG_KEY_MAN_STRU", sizeof(KMC_CFG_KEY_MAN_STRU));
    pfShow("KMC_CFG_DATA_PROTECT_STRU", sizeof(KMC_CFG_DATA_PROTECT_STRU));
    pfShow("KMC_CFG_KEY_TYPE_STRU", sizeof(KMC_CFG_KEY_TYPE_STRU));
    pfShow("KMC_CFG_DOMAIN_INFO_STRU", sizeof(KMC_CFG_DOMAIN_INFO_STRU));
    pfShow("KMC_RK_PARA_STRU", sizeof(KMC_RK_PARA_STRU));
    pfShow("KMC_KSF_RK_STRU", sizeof(KMC_KSF_RK_STRU));
    pfShow("KMC_MK_REAR_STRU", sizeof(KMC_MK_REAR_STRU));
    pfShow("KMC_KSF_MK_STRU", sizeof(KMC_KSF_MK_STRU));
    pfShow("KMC_MEM_MK_STRU", sizeof(KMC_MEM_MK_STRU));
    pfShow("KMC_MKF_HDR_STRU", sizeof(KMC_MKF_HDR_STRU));
    pfShow("KMC_MKF_HDR_WITH_HMAC_STRU", sizeof(KMC_MKF_HDR_WITH_HMAC_STRU));
    pfShow("KMC_MKF_MK_STRU", sizeof(KMC_MKF_MK_STRU));

    return;
}
#endif

#ifdef __cplusplus
}
#endif  /* __cplusplus */

