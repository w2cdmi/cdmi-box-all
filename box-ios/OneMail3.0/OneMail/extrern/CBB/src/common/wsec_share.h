/*lint -e526 -e628 */

/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_Share.h
* Decription: 
    ��CBB�乫���ӿڣ��������⿪��.
*********************************************************************************/
#ifndef __WIRELESS_SHARE_D13A230G2DC_RFKLAP_SD32SSDA4EH_LPOC27
#define __WIRELESS_SHARE_D13A230G2DC_RFKLAP_SD32SSDA4EH_LPOC27

#include "kmc_itf.h"

#ifdef __cplusplus
extern "C"
{
#endif

typedef WSEC_ERR_T (*WSEC_SubCbbFinalize)();
typedef WSEC_ERR_T (*WSEC_SubCbbReset)();

typedef struct tagWSEC_SUBCBB
{
    WSEC_CHAR           szSubCbbName[16];
    WSEC_SubCbbFinalize pfFinalize;
    WSEC_SubCbbReset    pfReset;
} WSEC_SUBCBB_STRU;

/*=========================================================================
                     1. ����CBB��ʼ����ȥ��ʼ������λ
=========================================================================*/
/* (1) ��CBB��ʼ����ȥ��ʼ�� */
WSEC_ERR_T KMC_Initialize(const KMC_FILE_NAME_STRU *pstFileName, const KMC_FP_CALLBACK_STRU *pKmcCallbackFun, const WSEC_PROGRESS_RPT_STRU *pstRptProgress);
WSEC_ERR_T KMC_Finalize();
WSEC_ERR_T KMC_Reset();

WSEC_ERR_T SDP_Initialize();

/*=========================================================================
                     2. ����CBB���м��
=========================================================================*/
/* (1) KMC */
WSEC_VOID KMC_RefreshMkMask(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo);
WSEC_VOID KMC_ChkMkStatus(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo);
WSEC_VOID KMC_ChkRkStatus(const WSEC_SYSTIME_T* pstLocalNow, const WSEC_SYSTIME_T* pstUtcNow, const WSEC_PROGRESS_RPT_STRU* pstRptProgress, WSEC_EXEC_INFO_STRU* pExecInfo);

/*=========================================================================
                     3. ��������
=========================================================================*/
WSEC_VOID KMC_Correct4Clock();

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* __WIRELESS_SHARE_D13A230G2DC_RFKLAP_SD32SSDA4EH_LPOC27 */
