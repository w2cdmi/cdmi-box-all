/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_Config.h
* Decription: ���ļ���ҪAPP����Ա�Ա���ѡ����м򵥵�����
*********************************************************************************/
#ifndef __WIRELESS_SEC_CONFIG_D4513A042DC_RF3F_4E427
#define __WIRELESS_SEC_CONFIG_D4513A042DC_RF3F_4E427

#ifdef __cplusplus
extern "C" {
#endif

/*================================================
       1. ���뿪��
================================================*/
//#define WSEC_WIN32
#define WSEC_LINUX
#define WSEC_DEBUG
//#define WSEC_TRACE_MEMORY        /* CBB����������, APP����� */
#define WSEC_COMPILE_SDP         /* ����SDP(�������ݱ���)��CBB */
//#define WSEC_COMPILE_CAC_IPSI    /* ���û���iPSI��CAC(�����㷨������)��CBB */
#define WSEC_COMPILE_CAC_OPENSSL /* ���û���OpenSSL��CAC(�����㷨������)��CBB */

/*================================================
       2. CPUѰַģʽ
================================================*/
#define WSEC_CPU_ENDIAL_AUTO_CHK (0) /* �����Զ���� */
#define WSEC_CPU_ENDIAL_BIG      (1) /* ��˶��� */
#define WSEC_CPU_ENDIAL_LITTLE   (2) /* С�˶��� */
#define WSEC_CPU_ENDIAN_MODE     WSEC_CPU_ENDIAL_AUTO_CHK /* ���������ú꣬���ɳ����Զ���� */

/*================================================
        3. ��̬����
================================================*/
#define WSEC_DOMAIN_NUM_MAX          (1024) /* Domain������ */
#define WSEC_DOMAIN_KEY_TYPE_NUM_MAX (16)   /* ÿ��Domainӵ��KeyType�������� */
#define WSEC_MK_NUM_MAX              (4096) /* Keystore�ļ�������洢MK������� */
#define WSEC_ENABLE_BLOCK_MILSEC     (10)   /* ��ʱ����, ��������ռ��CPU��ʱ��(��λ: ����) */
#define WSEC_ERR_CODE_BASE (0) /* Ԥ����CBB����ʼ������, APP������ʽ���� */

/*================================================
        4. ����
================================================*/
//#define WSEC_WRI_LOG_AUTO_END_WITH_CRLF /* �����Ʒ��¼��־ʱ�Զ���β������˻س���������ú꣬����ע��֮ */

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* __WIRELESS_SEC_CONFIG_D4513A042DC_RF3F_4E427 */
