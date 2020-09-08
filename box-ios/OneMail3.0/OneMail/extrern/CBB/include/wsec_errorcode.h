/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_ErrorCode.h
* Decription: ������
*********************************************************************************/
#ifndef __WIRELESS_ERROR_CODE_D413A42DCRF_3F4E_427
#define __WIRELESS_ERROR_CODE_D413A42DCRF_3F4E_427

#include "wsec_config.h"

#ifdef __cplusplus
extern "C" {
#endif

/* ��CBB������: ��IDE Project����ú�, ��Ҫ��'WSEC_Config.h'����, �Դ�ʹAPP������ȷ�Ķ��� */
#ifndef WSEC_ERR_CODE_BASE 
    #error Please define the 'WSEC_ERR_CODE_BASE' in 'WSEC_Config.h'.
#endif

#define WSEC_ERROR_CODE(seq) ((WSEC_ERR_T)(WSEC_ERR_CODE_BASE + seq))

#define WSEC_SUCCESS                                               (WSEC_ERR_T)0    /* �ɹ� */
#define WSEC_FAILURE                                           WSEC_ERROR_CODE(1)   /* ͨ�ô��� */

/* �ļ��������� */
#define WSEC_ERR_OPEN_FILE_FAIL                                WSEC_ERROR_CODE(11)   /* ���ļ�ʧ�� */
#define WSEC_ERR_READ_FILE_FAIL                                WSEC_ERROR_CODE(12)   /* ���ļ�ʧ�� */
#define WSEC_ERR_WRI_FILE_FAIL                                 WSEC_ERROR_CODE(13)   /* д�ļ�ʧ�� */
#define WSEC_ERR_GET_FILE_LEN_FAIL                             WSEC_ERROR_CODE(14)   /* ��ȡ�ļ�����ʧ�� */
#define WSEC_ERR_FILE_FORMAT                                   WSEC_ERROR_CODE(15)   /* �ļ���ʽ���� */

/* �ڴ�������� */
#define WSEC_ERR_MALLOC_FAIL                                   WSEC_ERROR_CODE(51)  /* �ڴ����ʧ�� */
#define WSEC_ERR_MEMCPY_FAIL                                   WSEC_ERROR_CODE(52)  /* �ڴ濽��ʧ�� */
#define WSEC_ERR_MEMCLONE_FAIL                                 WSEC_ERROR_CODE(53)  /* �ڴ��¡ʧ�� */
#define WSEC_ERR_STRCPY_FAIL                                   WSEC_ERROR_CODE(54)  /* �ַ�������ʧ�� */
#define WSEC_ERR_OPER_ARRAY_FAIL                               WSEC_ERROR_CODE(55)  /* �������ʧ�� */

/* ��ȫ����������� */
#define WSEC_ERR_CRPTO_LIB_FAIL                                WSEC_ERROR_CODE(101) /* ��ȫ������(iPSI)����ʧ�� */
#define WSEC_ERR_GEN_HASH_CODE_FAIL                            WSEC_ERROR_CODE(102) /* ����Hashֵʧ�� */
#define WSEC_ERR_HASH_NOT_MATCH                                WSEC_ERROR_CODE(103) /* Hashֵ��ƥ�� */
#define WSEC_ERR_INTEGRITY_FAIL                                WSEC_ERROR_CODE(104) /* �����Ա��ƻ� */
#define WSEC_ERR_HMAC_FAIL                                     WSEC_ERROR_CODE(105) /* HMACʧ�� */
#define WSEC_ERR_HMAC_AUTH_FAIL                                WSEC_ERROR_CODE(106) /* HMAC��֤ʧ�� */
#define WSEC_ERR_GET_RAND_FAIL                                 WSEC_ERROR_CODE(107) /* ��ȡ�����ʧ�� */
#define WSEC_ERR_PBKDF2_FAIL                                   WSEC_ERROR_CODE(108) /* ������Կʧ�� */
#define WSEC_ERR_ENCRPT_FAIL                                   WSEC_ERROR_CODE(109) /* ���ݼ���ʧ�� */
#define WSEC_ERR_DECRPT_FAIL                                   WSEC_ERROR_CODE(110) /* ���ݽ���ʧ�� */
#define WSEC_ERR_GET_ALG_NAME_FAIL                             WSEC_ERROR_CODE(111) /* ��ȡ��ȫ�㷨��ʧ�� */

/* ������������� */
#define WSEC_ERR_INVALID_ARG                                   WSEC_ERROR_CODE(151) /* �Ƿ����� */
#define WSEC_ERR_OUTPUT_BUFF_NOT_ENOUGH                        WSEC_ERROR_CODE(152) /* ������������� */
#define WSEC_ERR_INPUT_BUFF_NOT_ENOUGH                         WSEC_ERROR_CODE(153) /* ���뻺�������� */
#define WSEC_ERR_CANCEL_BY_APP                                 WSEC_ERROR_CODE(154) /* APPȡ������ */
#define WSEC_ERR_INVALID_CALL_SEQ                              WSEC_ERROR_CODE(155) /* APP����˳����� */

/* ϵͳ�������� */
#define WSEC_ERR_GET_CURRENT_TIME_FAIL                         WSEC_ERROR_CODE(201) /* ��ȡ��ǰʱ��ʧ�� */

/* KMC���� */
#define WSEC_ERR_KMC_CALLBACK_KMCCFG_FAIL                      WSEC_ERROR_CODE(251) /* �ص���ȡKMC��������ʧ�� */
#define WSEC_ERR_KMC_KMCCFG_INVALID                            WSEC_ERROR_CODE(252) /* KMC�������ݷǷ� */
#define WSEC_ERR_KMC_KSF_DATA_INVALID                          WSEC_ERROR_CODE(253) /* Keystore���ڷǷ��������� */
#define WSEC_ERR_KMC_INI_MUL_CALL                              WSEC_ERROR_CODE(254) /* ��ε��ó�ʼ�� */
#define WSEC_ERR_KMC_NOT_KSF_FORMAT                            WSEC_ERROR_CODE(255) /* ����Keystore�ļ���ʽ */
#define WSEC_ERR_KMC_READ_DIFF_VER_KSF_FAIL                    WSEC_ERROR_CODE(256) /* ��ȡ�����汾��Keystore�ļ�ʧ�� */
#define WSEC_ERR_KMC_READ_MK_FAIL                              WSEC_ERROR_CODE(257) /* ��ȡMKʧ�� */
#define WSEC_ERR_KMC_MK_LEN_TOO_LONG                           WSEC_ERROR_CODE(258) /* MK��Կ���� */
#define WSEC_ERR_KMC_REG_REPEAT_MK                             WSEC_ERROR_CODE(259) /* ��ͼע���ظ���MK */
#define WSEC_ERR_KMC_ADD_REPEAT_DOMAIN                         WSEC_ERROR_CODE(260) /* ��ͼ�����ظ���Domain(ID�ظ�) */
#define WSEC_ERR_KMC_ADD_REPEAT_KEY_TYPE                       WSEC_ERROR_CODE(261) /* ��ͼ�����ظ���KeyType(ͬһDomain��KeyType�ظ�) */
#define WSEC_ERR_KMC_ADD_REPEAT_MK                             WSEC_ERROR_CODE(262) /* ��ͼ�����ظ���MK(ͬһDomain��KeyId�ظ�) */
#define WSEC_ERR_KMC_DOMAIN_MISS                               WSEC_ERROR_CODE(263) /* DOMAIN������ */
#define WSEC_ERR_KMC_DOMAIN_KEYTYPE_MISS                       WSEC_ERROR_CODE(264) /* DOMAIN KeyType������ */
#define WSEC_ERR_KMC_DOMAIN_NUM_OVERFLOW                       WSEC_ERROR_CODE(265) /* DOMAIN������������ */
#define WSEC_ERR_KMC_KEYTYPE_NUM_OVERFLOW                      WSEC_ERROR_CODE(266) /* KeyType������������ */
#define WSEC_ERR_KMC_MK_NUM_OVERFLOW                           WSEC_ERROR_CODE(267) /* MK�������� */
#define WSEC_ERR_KMC_MK_MISS                                   WSEC_ERROR_CODE(268) /* MK������ */
#define WSEC_ERR_KMC_RECREATE_MK                               WSEC_ERROR_CODE(269) /* ���´���MKʧ�� */
#define WSEC_ERR_KMC_CBB_NOT_INIT                              WSEC_ERROR_CODE(270) /* CBB��δ��ʼ�� */
#define WSEC_ERR_KMC_CANNOT_REG_AUTO_KEY                       WSEC_ERROR_CODE(271) /* ����ע��ϵͳ�Զ����ɵ���Կ */
#define WSEC_ERR_KMC_CANNOT_RMV_ACTIVE_MK                      WSEC_ERROR_CODE(272) /* ����ɾ�����ڻ״̬��MK */
#define WSEC_ERR_KMC_CANNOT_SET_EXPIRETIME_FOR_INACTIVE_MK     WSEC_ERROR_CODE(273) /* ���ܶ�inactive��MK���ù���ʱ�� */
#define WSEC_ERR_KMC_RK_GENTYPE_REJECT_THE_OPER                WSEC_ERROR_CODE(274) /* RK�����ɷ�ʽ��֧�ָò��� */
#define WSEC_ERR_KMC_MK_GENTYPE_REJECT_THE_OPER                WSEC_ERROR_CODE(275) /* MK�����ɷ�ʽ��֧�ָò��� */
#define WSEC_ERR_KMC_ADD_DOMAIN_DISCREPANCY_MK                 WSEC_ERROR_CODE(276) /* ����DOMAIN�������MKì�� */
#define WSEC_ERR_KMC_IMPORT_MK_CONFLICT_DOMAIN                 WSEC_ERROR_CODE(277) /* �����MK��Domain���ó�ͻ */
#define WSEC_ERR_KMC_CANNOT_ACCESS_PRI_DOMAIN                  WSEC_ERROR_CODE(278) /* ���ܷ���CBB˽��Domain */

/* SDP���� */
#define WSEC_ERR_SDP_PWD_VERIFY_FAIL                           WSEC_ERROR_CODE(351) /* ��������У��ʧ�� */
#define WSEC_ERR_SDP_CONFIG_INCONSISTENT_WITH_USE              WSEC_ERROR_CODE(352) /* ����������ʹ�ò�һ�� */
#define WSEC_ERR_SDP_INVALID_CIPHER_TEXT                       WSEC_ERROR_CODE(353) /* ���ĸ�ʽ�������� */
#define WSEC_ERR_SDP_VERSION_INCOMPATIBLE                      WSEC_ERROR_CODE(354) /* ���İ汾�뵱ǰ�汾������ */
#define WSEC_ERR_SDP_ALG_NOT_SUPPORTED                         WSEC_ERROR_CODE(355) /* �㷨�����ڻ�֧�� */
#define WSEC_ERR_SDP_DOMAIN_UNEXPECTED			               WSEC_ERROR_CODE(356) /* �������Է�Ԥ��Domain */

#define WSEC_ERR_MAX                                           WSEC_ERROR_CODE(5000) /* CBB�������� */

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* __WIRELESS_ERROR_CODE_D413A42DCRF_3F4E_427 */
