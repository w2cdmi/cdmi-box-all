/* ����pc lint�澯�ɺ��� */
/*lint -e506 -e522 -e526 -e533 -e534 -e545 -e550 -e573 -e574 -e602 -e603 -e632 -e633 -e634 -e638 -e639 -e655 -e665 -e668 -e701 -e702 -e750 -e785 -e794 -e830 -e960 */

/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_Util.c
* Decription:
  CBB���ú���ʵ��
*********************************************************************************/
#include "wsec_type.h"
#include "wsec_itf.h"
#include "wsec_pri.h"
#include "wsec_share.h"
#include <string.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C"
{
#endif

/* �Ƿ�֧��ȱʡ��Lock���� */
#ifdef WSEC_WIN32
    #define WSEC_SUPPORT_LOCK_FUN
#endif
#ifdef WSEC_LINUX
    #define WSEC_SUPPORT_LOCK_FUN
#endif

WSEC_HANDLE g_hLock[WSEC_LOCK_NUM] = {0};

#ifdef WSEC_DEBUG
    WSEC_BOOL g_bCpuEndianModeChked = WSEC_FALSE;
#endif
WSEC_BOOL g_bIsBigEndianMode = WSEC_FALSE;

/* ע�ắ�� */
WSEC_CALLBACK_FUN_STRU g_RegFun = {
    /* WSEC_MEMORY_CALLBACK_STRU */
    {malloc, free, memcmp}, 

    /* WSEC_FILE_CALLBACK_STRU */
    /*{WSEC_NULL_PTR},*/
    {fopen, fclose, fread, fwrite, fflush, remove, fgetc, fgets, ftell, fseek, feof, ferror}, 

    /* WSEC_LOCK_CALLBACK_STRU */
#ifdef WSEC_SUPPORT_LOCK_FUN
    {WSEC_DeftCreateLock, WSEC_DeftDestroyLock, WSEC_DeftLock, WSEC_DeftUnlock},
#else
    {WSEC_NULL_PTR},
#endif

    /* WSEC_BASE_RELY_APP_CALLBACK_STRU */
    {WSEC_NULL_PTR}, 
};

/* �����Ե��õĺ��� */
/*lint -e651*/
WSEC_PERIODIC_CALL_STRU g_PeriodCall[] = {
    {KMC_RefreshMkMask, {0}, {0}, 3600}, /* ÿ1Сʱˢ������ */
    {KMC_ChkMkStatus,   {0}, {0}, 3600}, /* ÿ1Сʱ���Լ��һ��MK��Կ״̬�Ƿ���� */
    {KMC_ChkRkStatus,   {0}, {0}, 3600}, /* ÿ1Сʱ���Լ��һ��RK��Կ״̬�Ƿ���� */
};
WSEC_INT32 g_nChkPeriodCallIndex = 0;
WSEC_SYS_STRU g_CbbSys = {WSEC_WAIT_INIT};

WSEC_SUBCBB_STRU g_SubCbbFunc[] = {
    {"KMC", KMC_Finalize,  KMC_Reset},
};

WSEC_VOID WSEC_ChkCpuEndianMode();
WSEC_ERR_T WSEC_RegFunc(const WSEC_FP_CALLBACK_STRU* pstCallBack);
WSEC_VOID WSEC_LogEnvironment();

#ifdef WSEC_DEBUG
extern WSEC_VOID WSEC_ShowStructSize(WSEC_FP_ShowStructSize pfShow);
extern WSEC_VOID KMC_ShowStructSize(WSEC_FP_ShowStructSize pfShow);
extern WSEC_VOID SDP_ShowStructSize(WSEC_FP_ShowStructSize pfShow);
#endif

/*****************************************************************************
 �� �� ��  : WSEC_ChkCpuEndianMode
 ��������  : ���CPU�ֽ������ģʽ
             ����������浽ȫ�ֱ��� g_bIsBigEndianMode
 �������  : ��
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2014-8-29
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_ChkCpuEndianMode()
{
    WSEC_BYTE bytes[] = {0x12, 0x34};
    WSEC_UINT16* pShort = (WSEC_UINT16*)bytes;

    g_bIsBigEndianMode = (0x1234 == *pShort) ? WSEC_TRUE : WSEC_FALSE;
#ifdef WSEC_DEBUG
    g_bCpuEndianModeChked = WSEC_TRUE;
#endif

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_IsBigEndianMode
 ��������  : �ж�CPU�ֽ�������Ƿ�Ϊ��˶���
 �������  : ��
 �������  : ��
 �� �� ֵ  : T=��˶���/F=С�˶���

 �޸���ʷ      :
  1.��    ��   : 2014-8-29
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_IsBigEndianMode()
{
#ifdef WSEC_DEBUG
    WSEC_ASSERT(g_bCpuEndianModeChked);
#endif
    return g_bIsBigEndianMode;
}

/*****************************************************************************
 �� �� ��  : WSEC_RegFunc
 ��������  : �Ǽ�APP��ע��Ļص�����
 �������  : pstCallBack: ����ص�����
 �������  : ��
 �� �� ֵ  : WSEC_ERR_T

 �޸���ʷ      :
  1.��    ��   : 2014-8-26
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_RegFunc(const WSEC_FP_CALLBACK_STRU* pstCallBack)
{
    const WSEC_LOCK_CALLBACK_STRU* pstLock = WSEC_NULL_PTR;
    const WSEC_BASE_RELY_APP_CALLBACK_STRU* pstRelyApp = WSEC_NULL_PTR;
    WSEC_SIZE_T nNullFunNum;

    if (!pstCallBack) {return WSEC_ERR_INVALID_ARG;}

    pstRelyApp = &pstCallBack->stRelyApp;
    pstLock    = &pstCallBack->stLock;

    /* 1. �ڴ���(��ѡ) */
    nNullFunNum = WSEC_GetZeroItemCount(&pstCallBack->stMemory, sizeof(pstCallBack->stMemory), sizeof(pstCallBack->stMemory.pfMemAlloc));
    if (0 == nNullFunNum) /* �ṩ�����лص������� */
    {
        return_oper_if(WSEC_MEMCPY(&g_RegFun.stMemory, sizeof(g_RegFun.stMemory), &pstCallBack->stMemory, sizeof(pstCallBack->stMemory)) != EOK,
                       WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);
    }
    else if (nNullFunNum != (sizeof(pstCallBack->stMemory) / sizeof(pstCallBack->stMemory.pfMemAlloc)))
    {
        WSEC_LOG_E("Memory-oper callback function must be all provided");
        return WSEC_ERR_INVALID_ARG;
    }else{;}

    /* 2. �ļ�������(��ѡ) */
    nNullFunNum = WSEC_GetZeroItemCount(&pstCallBack->stFile, sizeof(pstCallBack->stFile), sizeof(pstCallBack->stFile.pfFclose));
    if (0 == nNullFunNum) /* �ṩ�����лص������� */
    {
        return_oper_if(WSEC_MEMCPY(&g_RegFun.stFile, sizeof(g_RegFun.stFile), &pstCallBack->stFile, sizeof(pstCallBack->stFile)) != EOK,
                       WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);
    }
    else if (nNullFunNum != (sizeof(pstCallBack->stFile) / sizeof(pstCallBack->stFile.pfFclose)))
    {
        WSEC_LOG_E("File-oper callback function must be all provided");
        return WSEC_ERR_INVALID_ARG;
    }else{;}

    /* 3. ǿ����APP�ұ����ṩ�ĺ��� */
    return_oper_if(!pstRelyApp->pfWriLog, WSEC_LOG_E("'pfWriLog' must be provided"), WSEC_ERR_INVALID_ARG);
    g_RegFun.stRelyApp.pfWriLog = pstRelyApp->pfWriLog;
    
    return_oper_if(!pstRelyApp->pfNotify, WSEC_LOG_E("'pfNotify' must be provided"), WSEC_ERR_INVALID_ARG);
    g_RegFun.stRelyApp.pfNotify = pstRelyApp->pfNotify;

    return_oper_if(!pstRelyApp->pfDoEvents, WSEC_LOG_E("'pfDoEvents' must be provided"), WSEC_ERR_INVALID_ARG);
    g_RegFun.stRelyApp.pfDoEvents = pstRelyApp->pfDoEvents;

    /* 4. ��������(��ǿ����) */
    nNullFunNum = WSEC_GetZeroItemCount(pstLock, sizeof(WSEC_LOCK_CALLBACK_STRU), sizeof(pstLock->pfCreateLock));
    if (0 == nNullFunNum) /* �����ظ��������������� */
    {
        return_oper_if(WSEC_MEMCPY(&g_RegFun.stLock, sizeof(g_RegFun.stLock), pstLock, sizeof(WSEC_LOCK_CALLBACK_STRU)) != EOK, 
                       WSEC_LOG_E4MEMCPY, WSEC_ERR_MEMCPY_FAIL);
    }
    else if (nNullFunNum != (sizeof(WSEC_LOCK_CALLBACK_STRU) / sizeof(pstLock->pfCreateLock))) /* ���������������� */
    {
        WSEC_LOG_E("All callback functions of LOCK must provid.");
        return WSEC_ERR_INVALID_ARG;
    }
    else /* �������ຯ��ȫδ����, ����OS���뿪���Ƿ������ȱʡ���� */
    {
#ifndef WSEC_SUPPORT_LOCK_FUN
        WSEC_LOG_E("All callback functions of LOCK must provid.");
        return WSEC_ERR_INVALID_ARG;
#endif
    }

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : WSEC_LogEnvironment
 ��������  : �����л�����Ϣ��¼����־.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��7��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_LogEnvironment()
{
    WSEC_BOOL bEndialBig = WSEC_FALSE;
    const WSEC_CHAR* pszTemp = WSEC_NULL_PTR;
    const WSEC_CHAR *pszKmc = "", *pszSdp = "";

    WSEC_LOG_I("\r\n\r\n============= Environment begin =============");
#ifdef WSEC_DEBUG
    WSEC_ShowStructSize(WSEC_LogStructSize);

    KMC_ShowStructSize(WSEC_LogStructSize);
#ifdef WSEC_COMPILE_SDP
    SDP_ShowStructSize(WSEC_LogStructSize);
#endif
#endif

    /* ����ѧ�㷨�� */
#ifdef WSEC_COMPILE_CAC_OPENSSL
    WSEC_LOG_I("CBB based on OpenSSL");
#else
#ifdef WSEC_COMPILE_CAC_IPSI
    WSEC_LOG_I("CBB based on iPSI");
#else
    WSEC_LOG_I("CBB based on nothing");
#endif
#endif

    /* OSƽ̨ */
#ifdef WSEC_WIN32
    WSEC_LOG_I("OS is Windows.");
#else
#ifdef WSEC_LINUX
    WSEC_LOG_I("OS is LINUX.");
#else
    WSEC_LOG_I("OS is neither Windows nor LINUX."); 
#endif
#endif

    /* ��������ЩCBB��ģ�� */
    pszKmc = "KMC";
#ifdef WSEC_COMPILE_SDP
    pszSdp = "SDP";
#endif

    WSEC_LOG_I2("Use subcbb: %s %s", pszKmc, pszSdp);

    /* CPU�ֽڶ���ģʽ */
#if WSEC_CPU_ENDIAN_MODE == WSEC_CPU_ENDIAL_BIG /* ָ��Ϊ��˶���ģʽ */
    bEndialBig = WSEC_TRUE;
#elif WSEC_CPU_ENDIAN_MODE == WSEC_CPU_ENDIAL_LITTLE /* ָ��ΪС�˶���ģʽ */
    bEndialBig = WSEC_FALSE;
#else /* �����Զ���� */
    bEndialBig = g_bIsBigEndianMode;
#endif
    pszTemp = bEndialBig ? "Big" : "Small";
    WSEC_LOG_I1("CPU ENDIAL is %s", pszTemp);

    WSEC_LOG_I("\r\n============= Environment end =============\r\n\r\n");

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_Initialize
 ��������  : CBB��ʼ��.
             CBB���к�������ƽ̨����ƽ̨��صĺ�������APPע��, CBB�ص���ʽ���.
             ���, APP��ʹ��CBB���������ܺ���ǰ(��SDP, KMC�ĺ���), ��Ҫ�ȵ���
             �˺���.
 �� �� ��  : pstFileName: ��Keystore�ļ�����KMC�����ļ�.
                          ��ʹ��SDP��KMC������ṩ�������NULL.
                          ���ṩ�ļ���, ������ṩ2��Keystore��KMC�����ļ���. ��Ҫ2���ļ�����
                          Ŀ���ǳ��ڿɿ��Կ���, CBB�Զ�ʵ��2���ļ�������ͬ��, ���ĳ�ļ�������
                          ��, ���Զ��л�ʹ����һ���ļ�������.
             pstCallbackFun: APP�ṩ��CBB�Ļص�����.
             pstRptProgress: ��ʼ�������������ʱ�ϳ����ϱ�����[�ɿ�]
             pvReserved:       Ԥ������, ������չ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��20��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_Initialize(const KMC_FILE_NAME_STRU* pstFileName,
                           const WSEC_FP_CALLBACK_STRU* pstCallbackFun,
                           const WSEC_PROGRESS_RPT_STRU* pstRptProgress,
                           const WSEC_VOID* pvReserved)
{
    WSEC_ERR_T nRet = WSEC_SUCCESS;

    return_oper_if(WSEC_RUNNING == g_CbbSys.eState, WSEC_LOG_E("WSEC CBB is running, cannot be Initialize before finalize."), WSEC_ERR_INVALID_CALL_SEQ);
    g_CbbSys.eState = WSEC_INIT_FAIL;

    /* 1. �ص�����ע�� */
    nRet = WSEC_RegFunc(pstCallbackFun);
    if (nRet != WSEC_SUCCESS) {return nRet;}

    /* 2. ȷ��CPU�ֽ��� */
    WSEC_ChkCpuEndianMode();

    WSEC_LogEnvironment();

    /* 3. ������ʼ�� */
    nRet = WSEC_InitializeLock();
    return_oper_if((nRet != WSEC_SUCCESS), WSEC_LOG_E1("WSEC_InitializeLock() = %u.", nRet), nRet);

    /* 4. ���������SDP/KMC���KMC��ʼ�� */
    nRet = KMC_Initialize(pstFileName, &pstCallbackFun->stKmcCallbackFun, pstRptProgress);
    if (WSEC_SUCCESS == nRet)
    {
#ifdef WSEC_COMPILE_SDP
        nRet = SDP_Initialize();
#endif
    }

    if (WSEC_SUCCESS == nRet)
    {
        g_CbbSys.eState = WSEC_RUNNING;
        WSEC_LOG_E("WSEC CBB Initialized successful.");
    }
    else
    {
        WSEC_Finalize();
        WSEC_LOG_E("WSEC CBB Initialized fail.");
    }

    return nRet;
}

/*****************************************************************************
 �� �� ��  : WSEC_OnTimer
 ��������  : APP��CBB����������
             CBB��Ҫ����ִ��һЩ����, ������Կ����, ����ˢ�µ�, ��CBB����'����',
             �Ǿ�̬�ĺ�������, ������ҪAPP����������.
 �� �� ��  : pstRptProgress: �����ϱ�ָʾ��صĻص�������Ϣ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��������: ����5����.

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_OnTimer(const WSEC_PROGRESS_RPT_STRU* pstRptProgress)
{
    WSEC_PERIODIC_CALL_STRU* pCall;
    WSEC_SYSTIME_T stLocalNow = {0}, stUtcNow = {0};
    WSEC_INT32 nSec;
    WSEC_BOOL bCallNow = WSEC_FALSE;
#ifdef WSEC_DEBUG
    const WSEC_CHAR* pszFunName = WSEC_NULL_PTR;
#endif

    if_oper((!WSEC_GetLocalDateTime(&stLocalNow)) || (!WSEC_GetUtcDateTime(&stUtcNow)), return);
    if_oper(!WSEC_IS_NORMAL_YEAR(stLocalNow.uwYear), return); /* ʱ����δ����, �޷����� */

    KMC_Correct4Clock();

    if_oper(g_nChkPeriodCallIndex >= WSEC_NUM_OF(g_PeriodCall), g_nChkPeriodCallIndex = 0);
    pCall = g_PeriodCall + g_nChkPeriodCallIndex;
    for (; g_nChkPeriodCallIndex < WSEC_NUM_OF(g_PeriodCall); g_nChkPeriodCallIndex++, pCall++)
    {
        continue_if(!pCall->pfPeriodicCall);

        if (!WSEC_IsDateTime(&pCall->stPreCallTimeUtc)) {WSEC_DateTimeCopy(&pCall->stPreCallTimeUtc, &stUtcNow);}
        nSec = WSEC_DateTimeDiff(dtpSecond, &pCall->stPreCallTimeUtc, &stUtcNow);
        bCallNow = ((WSEC_UINT32)nSec >= pCall->ulPeriodSec);
        
        continue_if(!bCallNow);
        if (nSec <= 0) /* ϵͳʱ��ص�, ���޷��жϱ����Ƿ����м��, ��¼��ǰʱ��, �Ա�����ж�  */
        {
            WSEC_DateTimeCopy(&pCall->stPreCallTimeUtc, &stUtcNow);
            continue;
        }
        
#ifdef WSEC_DEBUG
        if (pCall->pfPeriodicCall == KMC_RefreshMkMask) pszFunName = "KMC_RefreshMkMask";
        if (pCall->pfPeriodicCall == KMC_ChkMkStatus) pszFunName = "KMC_ChkMkStatus";
        if (pCall->pfPeriodicCall == KMC_ChkRkStatus) pszFunName = "KMC_ChkRkStatus";
        WSEC_LOG_I2("OnTimer() call '%s', PeriodSec = %d", pszFunName, pCall->ulPeriodSec);
#endif
        pCall->pfPeriodicCall(&stLocalNow, &stUtcNow, pstRptProgress, &pCall->stExecInfo);
        WSEC_DateTimeCopy(&pCall->stPreCallTimeUtc, &stUtcNow);

        g_nChkPeriodCallIndex++;
        break; /* ÿ��ִֻ��һ������, �������ռ��CPU */
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_OnAppReady
 ��������  : APP����֪ͨ
             ��APP��ĳЩ��Դ������֪ͨCBB.
 �������  : eType: ��ʶ������Դ����
             pData: ֪ͨ����������(��NULL)
             nDataSize: ֪ͨ���ݵĳ���(��0)
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2015��5��25��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_OnAppReady(WSEC_APP_READY_ENUM eType, const WSEC_VOID* pData, WSEC_SIZE_T nDataSize)
{
    switch (eType)
    {
    case WSEC_APP_CLOCK_READY:
        KMC_Correct4Clock();
        break;

    default:
        WSEC_ASSERT_FALSE;
    }
}

/*****************************************************************************
 �� �� ��  : WSEC_Finalize
 ��������  : CBB���Ž���
 �������  : ��
 �������  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)

 �޸���ʷ      :
  1.��    ��   : 2014��8��29��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_Finalize()
{
    WSEC_ERR_T nRet = WSEC_SUCCESS, nTemp = WSEC_SUCCESS;
    WSEC_SUBCBB_STRU* pstSubCbb;
    WSEC_SIZE_T i;

    for (i = 0, pstSubCbb = g_SubCbbFunc; i < WSEC_NUM_OF(g_SubCbbFunc); i++, pstSubCbb++)
    {
        continue_if(!pstSubCbb->pfFinalize);
        
        nTemp = pstSubCbb->pfFinalize();
        if (nTemp != WSEC_SUCCESS)
        {
            WSEC_LOG_E2("'%s' Finalize fail, return %u.", pstSubCbb->szSubCbbName, nTemp);
            nRet = nTemp;
        }
    }

    WSEC_FinalizeLock();
    
    g_CbbSys.eState = WSEC_WAIT_INIT;
    WSEC_LOG_W("WSEC Finalized.");
    
    return nRet;
}

/*****************************************************************************
 �� �� ��  : WSEC_Reset
 ��������  : CBB��λ
 �������  : ��
 �������  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)

 �޸���ʷ      :
  1.��    ��   : 2015��1��22��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_Reset()
{
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;
    WSEC_SUBCBB_STRU* pstSubCbb;
    WSEC_SIZE_T i;

    return_oper_if(g_CbbSys.eState != WSEC_RUNNING, WSEC_LOG_E("CBB is not running."), WSEC_ERR_INVALID_CALL_SEQ);

    for (i = 0, pstSubCbb = g_SubCbbFunc; i < WSEC_NUM_OF(g_SubCbbFunc); i++, pstSubCbb++)
    {
        continue_if(!pstSubCbb->pfReset);
        
        nTemp = pstSubCbb->pfReset();
        if (nTemp != WSEC_SUCCESS)
        {
            WSEC_LOG_E2("Reset '%s' fail, return %u.", pstSubCbb->szSubCbbName, nTemp);
            nErrCode = nTemp;
        }
    }

    g_CbbSys.eState = WSEC_SUCCESS == nErrCode ? WSEC_RUNNING : WSEC_INIT_FAIL;
    WSEC_LOG_W("WSEC Reset.");

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : WSEC_GetVersion
 ��������  : ��ѯCBB�汾��
 �������  : ��
 �������  : ��
 �� �� ֵ  : CBB�汾��

��� �汾��       ����ʱ��    ��Ҫ����
--------------------------------------------------------------------------
1    V100R001C00  2015-05-30  ��ʼ�汾.
2    ?
3    ?
*****************************************************************************/
const WSEC_CHAR* WSEC_GetVersion()
{
    const WSEC_CHAR* pszVer = "KMC V100R001C00SPC001";
    return pszVer;
}

/*****************************************************************************
 �� �� ��  : WSEC_CreateLock
 ��������  : ����һ����
 �������  : OUT WSEC_HANDLE *phMutex
 �������  : ��
 �� �� ֵ  : WSEC_ERR_T

 �޸���ʷ      :
  1.��    ��   : 2013 ��6 - 13
    ��    ��   : l00171031
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_ERR_T WSEC_CreateLock(WSEC_HANDLE *phMutex)
{
    WSEC_ASSERT(phMutex);
    WSEC_ASSERT(g_RegFun.stLock.pfCreateLock);

    return g_RegFun.stLock.pfCreateLock(phMutex) ? WSEC_SUCCESS : WSEC_FAILURE;
}

/*****************************************************************************
 �� �� ��  : WSEC_DestroyLock
 ��������  : ����һ����
 �������  : INOUT WSEC_HANDLE *phMutex
 �������  : ��
 �� �� ֵ  : WSEC_VOID

 �޸���ʷ      :
  1.��    ��   : 2013��6 - 13
    ��    ��   : l00171031
    �޸�����   : �����ɺ���

*****************************************************************************/
WSEC_VOID WSEC_DestroyLock(WSEC_HANDLE *phMutex)
{
    if (phMutex && (*phMutex))
    {
        g_RegFun.stLock.pfDestroyLock(*phMutex);
        *phMutex = WSEC_NULL_PTR;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_Lock
 ��������  : ����
 �������  : eLockId: ��Դ��ʶ
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2014-12-27
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_Lock(WSEC_LOCK_FOR_ENUM eLockId)
{
    WSEC_HANDLE hMutex = g_hLock[eLockId];

    /* Misinformation: FORTIFY.Unreleased_Resource--Synchronization */
    if (hMutex) {g_RegFun.stLock.pfLock(hMutex);}

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_Unlock
 ��������  : ����
 �������  : eLockId: ��Դ��ʶ
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2014-12-27
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_Unlock(WSEC_LOCK_FOR_ENUM eLockId)
{
    WSEC_HANDLE hMutex = g_hLock[eLockId];

    if (hMutex)
    {
        WSEC_ASSERT(g_RegFun.stLock.pfUnlock);
        g_RegFun.stLock.pfUnlock(hMutex);
    }else{;}

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_StringClone
 ��������  : ��¡�ַ���
 �� �� ��  : pszCloneFrom: ����¡���ַ���
             pszCallerFile, nCallerLine: ���ô˺�������������ļ����к�[DEBUG����]
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��¡�����ַ���, WSEC_NULL_PTR����ζ��¡ʧ��.
 �ر�ע��  : Caller��Ҫ������ָ�벢�����ͷ�.

 �޸���ʷ
  1.��    ��   : 2014��10��22��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_CHAR* WSEC_StringClone(const WSEC_CHAR* pszCloneFrom, const WSEC_CHAR* pszCallerFile, WSEC_INT32 nCallerLine)
{
    WSEC_CHAR* pszNew = WSEC_NULL_PTR;
    WSEC_SIZE_T nLen = 0;

    return_oper_if(!pszCloneFrom, oper_null, pszNew);

    nLen = (WSEC_SIZE_T)WSEC_STRLEN(pszCloneFrom);
    return_oper_if(nLen < 1, oper_null, pszNew);

    nLen += 2; /* �洢�ַ����迼��'\0'��unicode���� */

    pszNew = (WSEC_CHAR*)WSEC_MALLOC(nLen);
    if (pszNew)
    {
        if (strcpy_s(pszNew, nLen, pszCloneFrom) != EOK) /* ����ʧ�����ͷ��ڴ� */
        {
            WSEC_FREE(pszNew);
        }
    }
#ifdef WSEC_TRACE_MEMORY
    if (pszNew) {WSEC_LOG_E3("WSEC-StringClone() = 0x%p, at %s, Line-%d", pszNew, pszCallerFile, nCallerLine);}
#endif

    return pszNew;
}

/*****************************************************************************
 �� �� ��  : WSEC_BuffClone
 ��������  : ��������¡:
             1. ����һ��ȴ�Ļ�����
             2. ��Դ���ݸ���
 �� �� ��  : pCloneFrom: ��¡Դ����;
             nSize:      ��¡Դ����.
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��¡�ɹ�����µ�ַ
 �ر�ע��  : Caller��Ҫ������ָ�벢�����ͷ�.

 �޸���ʷ
  1.��    ��   : 2014��10��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID* WSEC_BuffClone(const WSEC_VOID* pCloneFrom, WSEC_SIZE_T nSize, const WSEC_CHAR* pszCallerFile, WSEC_INT32 nCallerLine)
{
    WSEC_VOID* pNew = WSEC_NULL_PTR;

    WSEC_ASSERT(pCloneFrom && (nSize > 0));
    pNew = WSEC_MALLOC(nSize);

    return_oper_if(!pNew, oper_null, pNew);

    if (WSEC_MEMCPY(pNew, nSize, pCloneFrom, nSize) != EOK) /* ����ʧ�����ͷ��ڴ� */
    {
        WSEC_FREE(pNew);
    }

#ifdef WSEC_TRACE_MEMORY
    if (pNew) {WSEC_LOG_E3("WSEC-BuffClone() = 0x%p, at %s, Line-%d", pNew, pszCallerFile, nCallerLine);}
#endif

    return pNew;
}

/*****************************************************************************
 �� �� ��  : WSEC_Xor
 ��������  : �������ڴ���������ֽ����
 �� �� ��  : pOperand1, nOperand1Len: ��������������1���䳤��
             pOperand2, nOperand2Len: ��������������2���䳤��
             nResultLen: ����������Ļ���������
 �� �� ��  : pResult: ���������.
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_Xor(const WSEC_BYTE* pOperand1, WSEC_SIZE_T nOperand1Len,
                   const WSEC_BYTE* pOperand2, WSEC_SIZE_T nOperand2Len,
                   WSEC_BYTE* pResult, WSEC_SIZE_T nResultLen)
{
    WSEC_SIZE_T nXorLen = (nOperand1Len < nOperand2Len) ? nOperand1Len : nOperand2Len;
    WSEC_SIZE_T i;

    if (nXorLen > nResultLen) {nXorLen = nResultLen;} /* ȡ3����̻�������������� */
    for (i = 0; i < nXorLen; i++, pOperand1++, pOperand2++, pResult++)
    {
        *pResult = (*pOperand1) ^ (*pOperand2);
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_GetZeroItemCount
 ��������  : ͳ�ƻ���������0��Ԫ�ظ���
 �� �� ��  : pvData: �������׵�ַ
             nSize:  �������ܳ���
             nItemSize: �������и�Ԫ�صĳ���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 0Ԫ�ظ���
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��3��3��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_SIZE_T WSEC_GetZeroItemCount(const WSEC_VOID* pvData, WSEC_SIZE_T nSize, WSEC_SIZE_T nItemSize)
{
    WSEC_SIZE_T nCount = 0, i;
    WSEC_BYTE aZero[16] = {0};
    WSEC_BYTE* pbChk;
    
    WSEC_ASSERT((nSize % nItemSize) == 0);
    WSEC_ASSERT(nItemSize <= sizeof(aZero));

    for (pbChk = (WSEC_BYTE*)pvData, i = 0; i < nSize; i += nItemSize, pbChk += nItemSize)
    {
        if (WSEC_MEMCMP(pbChk, aZero, nItemSize) == 0)
        {
            nCount++;
        }
    }

    return nCount;
}

/*****************************************************************************
 �� �� ��  : WSEC_CopyFile
 ��������  : �ļ�����
 �� �� ��  : pszFrom: Դ�ļ�;
             pszTo:   Ŀ���ļ�.
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : �ɹ���ʧ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��03��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_CopyFile(const WSEC_CHAR* pszFrom, const WSEC_CHAR* pszTo)
{
    WSEC_FILE fRead = WSEC_NULL_PTR;
    WSEC_FILE fWri  = WSEC_NULL_PTR;
    WSEC_BYTE* pRead = WSEC_NULL_PTR;
    const WSEC_SIZE_T nBuffLen = WSEC_FILE_IO_SIZE_MAX;
    WSEC_SIZE_T nRead, nWrite;
    WSEC_BOOL bReturn = WSEC_TRUE;

    return_oper_if((!pszFrom) || (!pszTo), oper_null, WSEC_FALSE);

    fRead = WSEC_FOPEN(pszFrom, "rb");
    fWri  = WSEC_FOPEN(pszTo, "wb");
    pRead = (WSEC_BYTE*)WSEC_MALLOC(nBuffLen);

    do
    {
        break_oper_if(!fRead, WSEC_LOG_E1("Open file(%s) fail.", pszFrom), bReturn = WSEC_FALSE);
        break_oper_if(!fWri, WSEC_LOG_E1("Open file(%s) fail.", pszTo), bReturn = WSEC_FALSE);
        break_oper_if(!pRead, WSEC_LOG_E4MALLOC(nBuffLen), bReturn = WSEC_FALSE);

        while (!WSEC_FEOF(fRead))
        {
            nRead = (WSEC_SIZE_T)WSEC_FREAD(pRead, 1, nBuffLen, fRead);
            WSEC_UNCARE(nRead);
            break_oper_if(WSEC_FERROR(fRead), WSEC_LOG_E1("Read file(%s) fail.", pszFrom), bReturn = WSEC_FALSE);

            nWrite = (WSEC_SIZE_T)WSEC_FWRITE(pRead, 1, nRead, fWri);
            break_oper_if(nWrite != nRead, WSEC_LOG_E1("Write file(%s) fail.", pszTo), bReturn = WSEC_FALSE);
        }

        if (bReturn) {WSEC_FFLUSH(fWri);}
    } do_end;
    
    if (fRead) {WSEC_FCLOSE(fRead);}
    if (fWri) {WSEC_FCLOSE(fWri);}
    if (pRead) {WSEC_FREE(pRead);}

    return bReturn;
}

/*****************************************************************************
 �� �� ��  : WSEC_GetFileLen
 ��������  : ��ȡ�ļ�ռ�ó���
 �� �� ��  : pszFileName: �ļ���;
 �� �� ��  : pulLen: �ļ�����
 ��γ���  : ��
 �� �� ֵ  : �ɹ���ʧ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_GetFileLen(const WSEC_CHAR* pszFileName, WSEC_FILE_LEN* pulLen)
{
    WSEC_FILE fRead;
    long nLen = -1;

    return_oper_if((!pszFileName) || (!pulLen), oper_null, WSEC_FALSE);
    fRead = WSEC_FOPEN(pszFileName, "rb");
    if (!fRead) {return WSEC_FALSE;}

    do
    {
        if (WSEC_FSEEK(fRead, 0, SEEK_END) != 0) {break;}
        nLen = WSEC_FTELL(fRead);
    }do_end;
    WSEC_FCLOSE((fRead));

    if (nLen >= 0) {*pulLen = (WSEC_FILE_LEN)nLen;}

    return (nLen >= 0);
}

/*****************************************************************************
 �� �� ��  : WSEC_DeleteFileS
 ��������  : ��ȫ��ɾ���ļ�
 �� �� ��  : pszFileName: �ļ���;
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : �ɹ���ʧ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_DeleteFileS(const WSEC_CHAR* pszFileName)
{
    WSEC_FILE fWri = WSEC_NULL_PTR;
    WSEC_FILE_LEN ulFileLen = 0;
    WSEC_UINT32 ulWriLen = 0;
    WSEC_BUFF stWri = {0};
    WSEC_BOOL bReturn = WSEC_TRUE;

    WSEC_ASSERT(pszFileName);
    
    return_oper_if(!WSEC_GetFileLen(pszFileName, &ulFileLen), WSEC_LOG_E1("WSEC_GetFileLen(%s) fail.", pszFileName), WSEC_FALSE);
    
    WSEC_BUFF_ALLOC(stWri, 2048); /* �����2KΪ��λ���Ǵ�ɾ���ļ� */
    return_oper_if(!stWri.pBuff, WSEC_LOG_E4MALLOC(stWri.nLen), WSEC_FALSE);

    do
    {
        fWri = WSEC_FOPEN(pszFileName, "wb");
        break_oper_if(!fWri, WSEC_LOG_E1("Open file(%s) fail.", pszFileName), bReturn = WSEC_FALSE);

        while (ulFileLen > 0)
        {
            ulWriLen = ((WSEC_UINT32)ulFileLen > stWri.nLen) ? stWri.nLen : (WSEC_UINT32)ulFileLen;
            WSEC_UNCARE(WSEC_FWRITE_MUST(stWri.pBuff, ulWriLen, fWri));
            ulFileLen -= ulWriLen;
        }
    } do_end;
    WSEC_BUFF_FREE(stWri);
    if (fWri) {WSEC_FCLOSE(fWri);}

    /* Misinformation: FORTIFY.Race_Condition--File_System_Access */
    if (bReturn) {bReturn = (WSEC_FREMOVE(pszFileName) == 0);}

    return bReturn;
}

/*****************************************************************************
 �� �� ��  : WSEC_WriteFileS
 ��������  : ��ȫ����һ���ļ���������
 �� �� ��  : pvData: ��д����;
             ppszFile: һ���ļ���
             nFileNum: �ļ�����
             pfWriFile: д�ļ���ִ�к���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_WriteFileS(const WSEC_VOID* pvData, WSEC_CHAR** ppszFile, WSEC_SIZE_T nFileNum, WSEC_WriteFile pfWriFile, const WSEC_VOID* pvReserved)
{
    WSEC_SIZE_T i;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    WSEC_CHAR** ppszFileFrom = WSEC_NULL_PTR;
    WSEC_CHAR* pszCopyFromFile = WSEC_NULL_PTR;

    for (i = 0, ppszFileFrom = ppszFile; i < nFileNum; i++, ppszFileFrom++)
    {
        nErrCode = pfWriFile(pvData, *ppszFileFrom, pvReserved);
        break_oper_if(WSEC_SUCCESS == nErrCode, pszCopyFromFile = *ppszFileFrom, oper_null); /* �����ļ�д�ɹ�, �������ļ����ݿ����������ļ� */
    }
    return_oper_if(!pszCopyFromFile, oper_null, nErrCode); /* �������ļ�д���ݾ�ʧ�� */

    /* ��д�ɹ����ļ����Ƹ������ļ� */
    for (i = 0, ppszFileFrom = ppszFile; i < nFileNum; i++, ppszFileFrom++)
    {
        continue_if(pszCopyFromFile == *ppszFileFrom);
        if (!WSEC_CopyFile(pszCopyFromFile, *ppszFileFrom)) /* ����ʧ��, ������־���� */
        {
            WSEC_LOG_E2("copy file from '%s' to '%s' fail.", pszCopyFromFile, *ppszFileFrom);
        }
    }

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : WSEC_MemAlloc
 ��������  : �����ڴ�, ���ҳ�ʼ��Ϊȫ0
 �������  : ulSize: �������ڴ泤��
             pszFile, nLine: ���ô˺�������������ļ����к�[DEBUG����]
 �������  : ��
 �� �� ֵ  : �����ڴ����ʼ��ַ.

 �޸���ʷ      :
  1.��    ��   : 2014��10��20��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID* WSEC_MemAlloc(WSEC_UINT32 ulSize, const WSEC_CHAR* pszFile, WSEC_INT32 nLine)
{
    WSEC_VOID * ptr = WSEC_NULL_PTR;

    if (0 == ulSize)
    {
        WSEC_ASSERT(WSEC_FALSE);
        return ptr;
    }

    if (!g_RegFun.stMemory.pfMemAlloc) {g_RegFun.stMemory.pfMemAlloc = malloc;}

    /* Misinformation: FORTIFY.Integer_Overflow */
    ptr = g_RegFun.stMemory.pfMemAlloc(ulSize);
    if(ptr != WSEC_NULL_PTR)
    {
        WSEC_UNCARE(WSEC_MEMSET(ptr, ulSize, 0, ulSize));
    }

    /* DEBUG�汾�¼�¼�ڴ����� */
#ifdef WSEC_TRACE_MEMORY
    WSEC_LOG_E4("WSEC-MemAlloc(%d) = 0x%p, at: %s, Line-%d", ulSize, ptr, pszFile, nLine);
#endif

    return ptr;
}

/*****************************************************************************
 �� �� ��  : WSEC_MemFree
 ��������  : �ͷ��ڴ�.
 �������  : ptr: ���ͷ��ڴ�
             pszFile, nLine: ���ô˺�������������ļ����к�[DEBUG����]
 �������  : ��
 �� �� ֵ  : ��ָ��, �Է���Caller�ͷ��ڴ��ͬʱ��ָ����Ϊ��Ч, ��: 
             pBuff = WSEC_MemFree(pBuff, __FILE__, __LINE__);

 �޸���ʷ      :
  1.��    ��   : 2014��10��20��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID* WSEC_MemFree(WSEC_VOID* ptr, const WSEC_CHAR* pszFile, WSEC_INT32 nLine)
{
    if (!ptr) {return ptr;} /* �����ظ��ͷ� */

    /* DEBUG�汾�¼�¼�ڴ��ͷ� */
#ifdef WSEC_TRACE_MEMORY
    WSEC_LOG_E3("WSEC-MemFree(0x%p) at: %s, Line-%d", ptr, pszFile, nLine);
#endif

    if (!g_RegFun.stMemory.pfMemFree) {g_RegFun.stMemory.pfMemFree = free;}
    g_RegFun.stMemory.pfMemFree(ptr);
    ptr = WSEC_NULL_PTR;

    return ptr;
}

/*****************************************************************************
 �� �� ��  : WSEC_WriLog
 ��������  : CBB�ڲ�д��־�������ú�������־��Ϣͨ���ص�APP����־ע�ắ���׳�.
 �������  : pszFileName: �����������ļ���
             nLineNo:     �����������к�
             eLevel:      ��Ϣ��Ҫ�̶�
             format:      ��ʽ���ַ���
             ...          ���������Ϳɱ�Ĳ�����
 �������  : ��
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2014��7��11��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
/*lint -e960*/
WSEC_VOID WSEC_WriLog(const WSEC_CHAR* pszFileName, WSEC_INT32 nLineNo, WSEC_LOG_LEVEL_ENUM eLevel, const WSEC_CHAR* format, ...)
{
    va_list marker;
    WSEC_CHAR szLog[WSEC_LOG_BUFF_SIZE] = {0};
    const WSEC_CHAR* pPureFileName;
    WSEC_INT32 nPos = 0;

    if (!g_RegFun.stRelyApp.pfWriLog) {return;}

    /*1. ͨ����ʽ����������־�� */
    va_start(marker, format);
    WSEC_UNCARE(vsprintf_s(szLog, WSEC_LOG_BUFF_SIZE, format, marker)); /* Misinformation: FORTIFY.Format_String */
    va_end(marker);

    /*2. ���ݲ�Ʒ�����־���ص㣬�Զ������ȥ����־�еĻ��з� */
    /*2.1 ɾ����־��β���Ļ��з� */
    nPos = (WSEC_INT32)WSEC_STRLEN(szLog) - 1;
    if (nPos >= 0)
    {
        if ('\n' == szLog[nPos])
        {
            szLog[nPos] = '\0';
            nPos--;
        }
    }
    if (nPos >= 0)
    {
        if ('\r' == szLog[nPos])
        {
            szLog[nPos] = '\0';
            nPos--;
        }
    }

    /* 2.2 ������Ҫ, �Զ���ӻ��з� */
#ifndef WSEC_WRI_LOG_AUTO_END_WITH_CRLF /* ��Ʒд��־ʱδ�Զ����У���׷���Ա�֤��־�Ŀɶ��� */
    nPos++;
    if ((0 <= nPos) && ((nPos + 2) < WSEC_LOG_BUFF_SIZE))
    {
        szLog[nPos] = '\r';
        nPos++;
        szLog[nPos] = '\n';
        nPos++;
        szLog[nPos] = '\0';
    }
#endif

    /* 3. ��ȡ���ļ���(����·��) */
    pPureFileName = strrchr(pszFileName, '\\');
    if_oper(!pPureFileName, pPureFileName = strrchr(pszFileName, '/'));
    if (pPureFileName)
    {
        pPureFileName++;
    }
    else
    {
        pPureFileName = pszFileName;
    }

    /* 4. д��־ */
    g_RegFun.stRelyApp.pfWriLog((int)eLevel, "WSEC_CBB", pPureFileName, nLineNo, szLog);

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_CallPeriodicFunc
 ��������  : �������������Ե��ȳ���
 �� �� ��  : pFunc: ��ִ�к���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_CallPeriodicFunc(WSEC_FP_PeriodicCall pFunc)
{
    WSEC_SIZE_T i;
    WSEC_PERIODIC_CALL_STRU* pCall;
    WSEC_SYSTIME_T stLocalNow = {0}, stUtcNow = {0};

    if (!WSEC_GetLocalDateTime(&stLocalNow)) {return;}
    if (!WSEC_GetUtcDateTime(&stUtcNow)) {return;}

    pCall = g_PeriodCall;
    for (i = 0; i < WSEC_NUM_OF(g_PeriodCall); i++, pCall++)
    {
        continue_if(pCall->pfPeriodicCall != pFunc);

        pCall->stExecInfo.bUnconditionalExec = WSEC_TRUE;
        pFunc(&stLocalNow, &stUtcNow, WSEC_NULL_PTR, &pCall->stExecInfo);
        WSEC_DateTimeCopy(&pCall->stPreCallTimeUtc, &stUtcNow);
        pCall->stExecInfo.bUnconditionalExec = WSEC_FALSE;

        break;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_InitializeLock
 ��������  : �������г�ʼ��
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��20��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_InitializeLock()
{
    WSEC_INT32 i = 0;
    WSEC_INT32 j = 0;

    for(i = 0;  i < WSEC_NUM_OF(g_hLock);  i++)
    {
        WSEC_DestroyLock(&(g_hLock[i])); /* ȷ����δ������ */

        if (WSEC_CreateLock(&g_hLock[i]) != WSEC_SUCCESS) /* ʧ��, �������Ѿ��������� */
        {
            for(j = 0;  j < i;  j++)
            {
                WSEC_DestroyLock(&(g_hLock[j]));
            }

            return WSEC_FAILURE;
        }
    }

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : WSEC_FinalizeLock
 ��������  : ������
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��20��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_FinalizeLock()
{
    WSEC_INT32 i = 0;

    for(i = 0;  i < WSEC_NUM_OF(g_hLock);  i++)
    {
        WSEC_DestroyLock(&(g_hLock[i]));
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_ReadTlv
 ��������  : ���ļ����а���TLV��ʽ��ȡһ��TLV��Ԫ
 �� �� ��  : stream:    �ļ���
             nBuffSize: ��д����������
 �� �� ��  : pTlv:      ��ȡ����TLV����
 ��γ���  : pBuff: [in]��д������
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_ReadTlv(WSEC_FILE stream, WSEC_VOID* pBuff, WSEC_SIZE_T nBuffSize, WSEC_TLV_STRU* pTlv, WSEC_ERR_T* pnErrCode)
{
    WSEC_SIZE_T nReadLen;
    WSEC_ERR_T nErrCode;
    WSEC_ERR_T* pErr;
    
    WSEC_ASSERT(stream && pBuff && pTlv && (nBuffSize > 0));

    pErr = pnErrCode ? pnErrCode : &nErrCode;
    *pErr = WSEC_SUCCESS;

    if (WSEC_FEOF(stream)) {return WSEC_FALSE;}

    nReadLen = (WSEC_SIZE_T)WSEC_FREAD(pTlv, 1, sizeof(WSEC_TLV_STRU) - sizeof(pTlv->pVal), stream);
    if (0 == nReadLen) {return WSEC_FALSE;} /* �ļ��������� */

    return_oper_if(nReadLen != sizeof(WSEC_TLV_STRU) - sizeof(pTlv->pVal), *pErr = WSEC_ERR_FILE_FORMAT, WSEC_FALSE);
    
    WSEC_CvtByteOrder4Tlv(pTlv, wbcNetwork2Host);

    if (nBuffSize < pTlv->ulLen)
    {
        WSEC_LOG_E2("Cannot write %u bytes to buffer(%u bytes).", pTlv->ulLen, nBuffSize);
        *pErr = WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH;
        return WSEC_FALSE;
    }

    return_oper_if(!WSEC_FREAD_MUST(pBuff, pTlv->ulLen, stream), *pErr = WSEC_ERR_READ_FILE_FAIL, WSEC_FALSE);

    pTlv->pVal = pBuff;
#ifdef WSEC_TRACE_TLV
    printf("ReadTlv: T=%u, L=%u\r\n", pTlv->ulTag, pTlv->ulLen);
#endif
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_WriteTlv
 ��������  : ���ļ�����д��һ��TLV��Ԫ
 �� �� ��  : stream: �ļ���
             ulTag, ulLen, pVal: ��ӦTLV
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������(WSEC_SUCCESSΪ�ɹ�, ����Ϊ������)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��19��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_WriteTlv(WSEC_FILE stream, WSEC_UINT32 ulTag, WSEC_SIZE_T ulLen, const WSEC_VOID* pVal)
{
    WSEC_TLV_STRU stTlvWri;

#ifdef WSEC_TRACE_TLV
    printf("WriteTlv: T=%u, L=%u\r\n", ulTag, ulLen);
#endif
    WSEC_ASSERT(stream && pVal);
    WSEC_TLV_ASSIGN(stTlvWri, ulTag, ulLen, (WSEC_VOID*)pVal);
    WSEC_CvtByteOrder4Tlv(&stTlvWri, wbcHost2Network);
    
    if (!WSEC_FWRITE_MUST(&stTlvWri, sizeof(stTlvWri) - sizeof(stTlvWri.pVal), stream)) {return WSEC_ERR_READ_FILE_FAIL;}
    if (!WSEC_FWRITE_MUST(pVal, ulLen, stream)) {return WSEC_ERR_READ_FILE_FAIL;}

    return WSEC_SUCCESS;
}

/*****************************************************************************
 �� �� ��  : WSEC_CvtByteOrder4Tlv
 ��������  : ��TLV�ṹ�����������ֽ���ת��.
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
WSEC_VOID WSEC_CvtByteOrder4Tlv(WSEC_TLV_STRU* pTlv, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    if (wbcHost2Network == eOper)
    {
        pTlv->ulTag = WSEC_H2N_L(pTlv->ulTag);
        pTlv->ulLen = WSEC_H2N_L(pTlv->ulLen);
    }
    else if (wbcNetwork2Host == eOper)
    {
        pTlv->ulTag = WSEC_N2H_L(pTlv->ulTag);
        pTlv->ulLen = WSEC_N2H_L(pTlv->ulLen);
    }
    else
    {
        WSEC_ASSERT_FALSE;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_IsTimerout
 ��������  : �жϼ�ʱ�Ƿ�ʱ
 �� �� ��  : ulWaitTick: ��ʱ�ʱ��, ��λ�����ں���. 0����WSEC_ENABLE_BLOCK_MILSECȡ��
 �� �� ��  : ��
 ��γ���  : pTimer: [in]��ʱ�������ݽṹ; [out]�����ּ�ʱ��ת��ʱ, ���д֮.
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_IsTimerout(WSEC_SPEND_TIME_STRU* pTimer, WSEC_UINT32 ulWaitMilSec)
{
    WSEC_CLOCK_T tNow = 0;
    WSEC_UINT32 nWaitClocks, nDiff;
    WSEC_BOOL bTimerout;

    tNow = clock();
    if (0 == ulWaitMilSec) {ulWaitMilSec = WSEC_ENABLE_BLOCK_MILSEC;}
    nWaitClocks = ulWaitMilSec * CLOCKS_PER_SEC / 1000;

    nDiff = (WSEC_UINT32)(tNow - pTimer->tPre);

    bTimerout = (nDiff > nWaitClocks);
    if (bTimerout) {pTimer->tPre = clock();}

    return bTimerout;
}

/*****************************************************************************
 �� �� ��  : WSEC_RptProgress
 ��������  : �ϱ�����ָʾ
 �� �� ��  : pstRptProgressFun: �����ϱ��ص����������Ϣ;
             pstTimer:          ��¼�ϴ��ϱ���ʱ����Ϣ;
             ulScale:           �����ģ;
             ulCurrent:         ��ǰ��չ;
 �� �� ��  : pbCancel:          App�Ƿ�ȡ����������.
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��1��4��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_RptProgress(const WSEC_PROGRESS_RPT_STRU* pstRptProgressFun, WSEC_SPEND_TIME_STRU* pstTimer, WSEC_UINT32 ulScale, WSEC_UINT32 ulCurrent, WSEC_BOOL* pbCancel)
{
    if (pstTimer)
    {
        if (!WSEC_IsTimerout(pstTimer, WSEC_ENABLE_BLOCK_MILSEC)) {return;}
    }
    WSEC_DO_EVENTS;

    if (!pstRptProgressFun) {return;}
    if (!pstRptProgressFun->pfRptProgress) {return;}
    pstRptProgressFun->pfRptProgress(pstRptProgressFun->ulTag, ulScale, ulCurrent, pbCancel);

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_LogStructSize
 ��������  : ���ṹ�峤����Ϣ��¼����־
 �� �� ��  : pszStructName: �ṹ������
             nSize: �ṹ�峤��
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��6��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_LogStructSize(const WSEC_CHAR* pszStructName, WSEC_SIZE_T nSize)
{
    WSEC_LOG_I2("sizeof(%s) = %d", pszStructName, nSize);

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_CvtByteOrder4DateTime
 ��������  : ������&ʱ�������������ֽ���ת��.
 �� �� ��  : eOper: �ֽ���ת������
 �� �� ��  : ��
 ��γ���  : pstDateTime: [in]�ֽ����ת������; [out]ת���������
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_CvtByteOrder4DateTime(WSEC_SYSTIME_T* pstDateTime, WSEC_BYTEORDER_CVT_ENUM eOper)
{
    WSEC_ASSERT(WSEC_IS2(eOper, wbcHost2Network, wbcNetwork2Host));

    pstDateTime->uwYear = WSEC_BYTE_ORDER_CVT_S(eOper, pstDateTime->uwYear);

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_ShowStructSize
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
WSEC_VOID WSEC_ShowStructSize(WSEC_FP_ShowStructSize pfShow)
{
    pfShow("WSEC_UINT8", sizeof(WSEC_UINT8));
    pfShow("WSEC_BYTE", sizeof(WSEC_BYTE));
    pfShow("WSEC_UINT16", sizeof(WSEC_UINT16));
    pfShow("WSEC_UINT32", sizeof(WSEC_UINT32));
    pfShow("WSEC_INT32", sizeof(WSEC_INT32));
    pfShow("WSEC_BOOL", sizeof(WSEC_BOOL));
    pfShow("WSEC_CHAR", sizeof(WSEC_CHAR));
    pfShow("WSEC_HANDLE", sizeof(WSEC_HANDLE));
    pfShow("WSEC_FILE", sizeof(WSEC_FILE));
    pfShow("WSEC_POSITION", sizeof(WSEC_POSITION));
    pfShow("WSEC_SIZE_T", sizeof(WSEC_SIZE_T));
    pfShow("WSEC_CLOCK_T", sizeof(WSEC_CLOCK_T));
    pfShow("WSEC_ERR_T", sizeof(WSEC_ERR_T));
    pfShow("WSEC_VOID*", sizeof(WSEC_VOID*));

    pfShow("WSEC_SYSTIME_T", sizeof(WSEC_SYSTIME_T));
    pfShow("WSEC_SCHEDULE_TIME_STRU", sizeof(WSEC_SCHEDULE_TIME_STRU));
    pfShow("WSEC_TLV_STRU", sizeof(WSEC_TLV_STRU));

    return;
}
#endif

/*****************************************************************************
 �� �� ��  : WSEC_CreateHashCode
 ��������  : ��ָ�������ݿ������Hashֵ
 �������  : ulHashAlg: Hash�㷨
             pBuff:     ��Hash�����ݿ�
             ulBuffNum: ��Hash�����ݿ����
             pHashCode: ���ڴ洢Hashֵ�Ļ�����(������)
 �������  : pHashCode: ���Hashֵ�����С
 �� �� ֵ  : �ɹ�/ʧ��

 �޸���ʷ      :
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_CreateHashCode(WSEC_UINT32 ulHashAlg, const WSEC_BUFF* pBuff, WSEC_UINT32 ulBuffNum, INOUT WSEC_BUFF* pHashCode)
{
    WSEC_UINT32 i;
    WSEC_CRYPT_CTX ctx = WSEC_NULL_PTR;
    const WSEC_BUFF* pReadBuff = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;
    
    if (1 == ulBuffNum)
    {
        return (CAC_Digest(ulHashAlg, pBuff->pBuff, pBuff->nLen, pHashCode->pBuff, (WSEC_UINT32 *)&(pHashCode->nLen)) == WSEC_SUCCESS);
    }
    else if (ulBuffNum > 1)
    {
        nErrCode = CAC_DigestInit(&ctx, ulHashAlg);
        return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("CAC_DigestInit() = %u", nErrCode), WSEC_FALSE);

        for (i = 0, pReadBuff = pBuff; i < ulBuffNum; i++, pReadBuff++)
        {
            if(WSEC_SUCCESS != CAC_DigestUpdate(ctx, pReadBuff->pBuff, pReadBuff->nLen))
            {
                CAC_DigestFree(&ctx);
                return WSEC_FALSE;
            }
        }

        return (CAC_DigestFinal(&ctx, pHashCode->pBuff, &(pHashCode->nLen)) == WSEC_SUCCESS);
    }
    else
    {
        return WSEC_FALSE;
    }
}

/*****************************************************************************
 �� �� ��  : WSEC_ChkIntegrity
 ��������  : ��ָ֤���������������������Ƿ����֪��Hashֵƥ��
 �� �� ��  : ulHashAlg: Hash�㷨
             pChkBuff:  ����֤�����ݿ�
             ulBuffNum: ����֤�����ݿ����
             pCmpHashCode: ��֪��Hashֵ
             ulHashCodeLen: ��֪��Hashֵ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS=У��ɹ�������Ϊ������
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_ChkIntegrity(WSEC_UINT32 ulHashAlg,
                              const WSEC_BUFF* pChkBuff, WSEC_UINT32 ulBuffNum,
                              const WSEC_VOID* pCmpHashCode, WSEC_UINT32 ulHashCodeLen)
{
    WSEC_BUFF stHashNew = {0};
    WSEC_ERR_T ulReturnCode = WSEC_SUCCESS;
    
    WSEC_ASSERT(pChkBuff && (ulBuffNum > 0) && pCmpHashCode && (ulHashCodeLen > 0));

    WSEC_BUFF_ALLOC(stHashNew, ulHashCodeLen);

    return_oper_if(!stHashNew.pBuff, WSEC_LOG_E1("Allocate memory(len=%u) fail.", stHashNew.nLen), WSEC_ERR_MALLOC_FAIL);

    do
    {
        break_oper_if(!WSEC_CreateHashCode(ulHashAlg, pChkBuff, ulBuffNum, &stHashNew), WSEC_LOG_E("Generate hash fail."), ulReturnCode = WSEC_ERR_GEN_HASH_CODE_FAIL);

        ulReturnCode = WSEC_ERR_HASH_NOT_MATCH;
        if (stHashNew.nLen == ulHashCodeLen)
        {
            if (WSEC_MEMCMP(stHashNew.pBuff, pCmpHashCode, ulHashCodeLen) == 0){ulReturnCode = WSEC_SUCCESS;}
        }
    } do_end;

    WSEC_BUFF_FREE(stHashNew);
    return ulReturnCode;
}

/*****************************************************************************
 �� �� ��  : WSEC_CreateHmacCode
 ��������  : ����HMAC
 �� �� ��  : eHmacAlg:         HMAC�㷨
             pBuff, ulBuffNum: ����HMAC��������ݶ�
             pKey:             HMAC��Կ
 �� �� ��  : ��
 ��γ���  : pHmacCode: [in]���HMAC�Ļ�����, [out]�ش�HMAC���
 �� �� ֵ  : WSEC_SUCCESS=�ɹ�������Ϊ������
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��3��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_CreateHmacCode(WSEC_ALGID_E eHmacAlg, const WSEC_BUFF* pBuff, WSEC_UINT32 ulBuffNum, const WSEC_BUFF* pKey, INOUT WSEC_BUFF* pHmacCode)
{
    WSEC_CRYPT_CTX stCtx = {0};
    WSEC_UINT32 i;
    const WSEC_BUFF* pDataBuff = WSEC_NULL_PTR;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS, nTemp;

    return_err_if_para_invalid("WSEC_CreateHmacCode", pBuff && ulBuffNum && pKey && pHmacCode);

    /* 1. Initialize */
    nErrCode = CAC_HmacInit(&stCtx, (WSEC_UINT32)eHmacAlg, pKey->pBuff, pKey->nLen);
    return_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("The function return %u", nErrCode), nErrCode);

    /* 2. Update */
    for (i = 0, pDataBuff = pBuff; i < ulBuffNum; i++, pDataBuff++)
    {
        nErrCode = CAC_HmacUpdate(stCtx, pDataBuff->pBuff, pDataBuff->nLen);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("The function return %u", nErrCode), oper_null);
    }

    /* 3. Finalize */
    nTemp = CAC_HmacFinal(&stCtx, pHmacCode->pBuff, &pHmacCode->nLen);
    if (nTemp != WSEC_SUCCESS) {nErrCode = nTemp;}

    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : WSEC_ChkHmacCode
 ��������  : ��֤HMAC
 �� �� ��  : eHmacAlg:         HMAC�㷨
             pBuff, ulBuffNum: ����HMAC��������ݶ�
             pKey:             HMAC��Կ
             pHmacCode:        ��HMAC���
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS=�ɹ�������Ϊ������
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2015��3��3��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_ChkHmacCode(WSEC_ALGID_E eHmacAlg, const WSEC_BUFF* pBuff, WSEC_UINT32 ulBuffNum, const WSEC_BUFF* pKey, const WSEC_BUFF* pHmacCode)
{
    WSEC_BUFF stHmacNew = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    return_err_if_para_invalid("WSEC_ChkHmacCode", pBuff && ulBuffNum && pKey && pHmacCode);
    return_err_if_para_invalid("WSEC_ChkHmacCode", pHmacCode->pBuff && (pHmacCode->nLen > 0));

    WSEC_BUFF_ALLOC(stHmacNew, pHmacCode->nLen);

    do
    {
        break_oper_if(!stHmacNew.pBuff, WSEC_LOG_E4MALLOC(stHmacNew.nLen), nErrCode = WSEC_ERR_MALLOC_FAIL);
        
        nErrCode = WSEC_CreateHmacCode(eHmacAlg, pBuff, ulBuffNum, pKey, &stHmacNew);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("Above function return %u", nErrCode), oper_null);

        break_oper_if(WSEC_MEMCMP(pHmacCode->pBuff, stHmacNew.pBuff, stHmacNew.nLen) != 0, WSEC_LOG_E("Authenticate HMAC fail."), nErrCode = WSEC_ERR_HMAC_AUTH_FAIL);
    }do_end;

    WSEC_BUFF_FREE(stHmacNew);
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : WSEC_HashFile
 ��������  : �����ļ���Hashֵ
 �� �� ��  : ulHashAlg:     Hash�㷨
             pFile:         ���򿪵��ļ����
             ulDataSize:    �ӵ�ǰλ�ÿ�ʼ, ����Hash������ļ����ݳ���.
                            ���Ϊ0, ��ӵ�ǰλ�ÿ�ʼ���ļ��������������ݾ�����Hash.
 �� �� ��  : pHash: ���Hash���.
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS=У��ɹ�������Ϊ������
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_HashFile(WSEC_UINT32 ulHashAlg, WSEC_FILE pFile, WSEC_UINT32 ulDataSize, WSEC_BUFF* pHash)
{
    const WSEC_SIZE_T nMaxReadLen = WSEC_FILE_IO_SIZE_MAX;
    WSEC_CRYPT_CTX ctx = WSEC_NULL_PTR;
    WSEC_BUFF stReadBuff = {0};
    WSEC_SIZE_T nReadSize = 0;
    WSEC_SIZE_T nRemainSize = 0;
    WSEC_BOOL bReadToEnd;
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    WSEC_ASSERT(pFile && pHash && pHash->pBuff && (pHash->nLen > 0));
    WSEC_BUFF_ALLOC(stReadBuff, nMaxReadLen);
    return_oper_if(!stReadBuff.pBuff, WSEC_LOG_E4MALLOC(stReadBuff.nLen), WSEC_ERR_MALLOC_FAIL);

    do
    {
        break_oper_if(CAC_DigestInit(&ctx, ulHashAlg) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_DigestInit() fail."), nErrCode = WSEC_ERR_GEN_HASH_CODE_FAIL);

        bReadToEnd = (0 == ulDataSize);
        nRemainSize = ulDataSize;
        while (!WSEC_FEOF(pFile))
        {
            /* ȷ�����ζ�ȡ���ݵĳ��� */
            stReadBuff.nLen = bReadToEnd ? nMaxReadLen : nRemainSize;
            if (stReadBuff.nLen > nMaxReadLen) {stReadBuff.nLen = nMaxReadLen;}

            nReadSize = (WSEC_SIZE_T)WSEC_FREAD(stReadBuff.pBuff, 1, stReadBuff.nLen, pFile);
            continue_if(0 == nReadSize);

            if (!bReadToEnd) {nRemainSize -= nReadSize;}
            break_oper_if(CAC_DigestUpdate(ctx, stReadBuff.pBuff, nReadSize) != WSEC_SUCCESS,
                          WSEC_LOG_E("CAC_DigestUpdate() fail."), nErrCode = WSEC_ERR_GEN_HASH_CODE_FAIL);
            if ((!bReadToEnd) && (0 == nRemainSize)) {break;}
        }
        break_oper_if(nErrCode != WSEC_SUCCESS, CAC_DigestFree(&ctx), oper_null);

        /* ��ȡHashֵ */
        break_oper_if(CAC_DigestFinal(&ctx, pHash->pBuff, &pHash->nLen) != WSEC_SUCCESS,
                      WSEC_LOG_E("CAC_DigestFinal() fail."), nErrCode = WSEC_ERR_GEN_HASH_CODE_FAIL)
    }do_end;

    WSEC_BUFF_FREE(stReadBuff);
    return nErrCode;
}

/*****************************************************************************
 �� �� ��  : WSEC_ChkFileIntegrity
 ��������  : ����ļ���������.
 �� �� ��  : ulHashAlg:  Hash�㷨
             pFile:      ���򿪵��ļ����
             ulDataSize: �ӵ�ǰλ�ÿ�ʼ, ����Hash������ļ����ݳ���.
                         ���Ϊ0, ��ӵ�ǰλ�ÿ�ʼ���ļ��������������ݾ�����Hash.
             pHash:      ���ļ���ԭHashֵ.
 �� �� ��  : 
 ��γ���  : ��
 �� �� ֵ  : WSEC_SUCCESS=У��ɹ�������Ϊ������
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��12��31��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ERR_T WSEC_ChkFileIntegrity(WSEC_UINT32 ulHashAlg, WSEC_FILE pFile, WSEC_UINT32 ulDataSize, const WSEC_BUFF* pHash)
{
    WSEC_BUFF stHashNew = {0};
    WSEC_ERR_T nErrCode = WSEC_SUCCESS;

    WSEC_ASSERT(pFile && pHash && pHash->pBuff && (pHash->nLen > 0));
    WSEC_BUFF_ALLOC(stHashNew, pHash->nLen);
    return_oper_if(!stHashNew.pBuff, WSEC_LOG_E4MALLOC(stHashNew.nLen), WSEC_ERR_MALLOC_FAIL);

    do
    {
        nErrCode = WSEC_HashFile(ulHashAlg, pFile, ulDataSize, &stHashNew);
        break_oper_if(nErrCode != WSEC_SUCCESS, WSEC_LOG_E1("WSEC_HashFile()=%u", nErrCode), oper_null);

        break_oper_if(pHash->nLen != stHashNew.nLen, oper_null, nErrCode = WSEC_ERR_HASH_NOT_MATCH);
        nErrCode = (WSEC_MEMCMP(pHash->pBuff, stHashNew.pBuff, stHashNew.nLen) == 0) ? WSEC_SUCCESS : WSEC_ERR_HASH_NOT_MATCH;
    }do_end;

    WSEC_BUFF_FREE(stHashNew);
    return nErrCode;
}

#ifdef __cplusplus
}
#endif  /* __cplusplus */


