/*******************************************************************************
* Copyright @ Huawei Technologies Co., Ltd. 1998-2014. All rights reserved.  
* File name: WSEC_Array.h
* Decription:
  ����ӿ�
*********************************************************************************/
#ifndef WIRELESS_SEC_ARRAY_D1ASA42_DCFCVB4E74C27
#define WIRELESS_SEC_ARRAY_D1ASA42_DCFCVB4E74C27

#include "wsec_type.h"

#ifdef __cplusplus
extern "C" {
#endif

#define WSEC_ARR_GROW_BY (16) /* ���ڴ������, ���·����ڴ�ʱ������(Ԫ�ظ���) */

typedef WSEC_VOID* WSEC_ADDR;
typedef WSEC_VOID* WSEC_ARRAY;
typedef WSEC_VOID (* WSEC_FP_RemoveElement)(WSEC_VOID *pElement); /* pElementΪ�洢�ڴ�ɾ��Ԫ���ϵ�ֵ */
typedef WSEC_INT32 (* WSEC_FP_Compare)(const WSEC_VOID *pA, const WSEC_VOID *pB);

/* �����ʼ��: ָ����ʼ���鳤��, ������������ */
WSEC_ARRAY WSEC_ARR_Initialize(WSEC_INT32 nElementNum, WSEC_INT32 nGrowNum, WSEC_FP_Compare pfCmpElement, WSEC_FP_RemoveElement pfRmvElement);
WSEC_ARRAY WSEC_ARR_Finalize(WSEC_ARRAY arr); /* �����ս� */

WSEC_VOID* WSEC_ARR_GetAt(const WSEC_ARRAY arr, WSEC_INT32 Index); /* ��ȡָ��λ���ϵ����� */
WSEC_INT32 WSEC_ARR_Add(WSEC_ARRAY arr, const WSEC_VOID* pElement); /* ����Ԫ�� */
WSEC_INT32 WSEC_ARR_AddOrderly(WSEC_ARRAY arr, const WSEC_VOID* pElement); /* ��������Ԫ�� */
WSEC_BOOL WSEC_ARR_InsertAt(WSEC_ARRAY arr, WSEC_INT32 Index, const WSEC_VOID* pElement); /* ��ָ��λ���ϲ���Ԫ�� */
WSEC_VOID WSEC_ARR_RemoveAt(WSEC_ARRAY arr, WSEC_INT32 Index); /* ɾ��ָ��λ���ϵ�Ԫ�� */
WSEC_VOID WSEC_ARR_RemoveAll(WSEC_ARRAY arr); /* ������� */

WSEC_INT32 WSEC_ARR_GetCount(const WSEC_ARRAY arr); /* ��ȡ��������ռ��Ԫ�ظ��� */
WSEC_BOOL WSEC_ARR_IsEmpty(const WSEC_ARRAY arr); /* �ж������Ƿ�Ϊ�� */

WSEC_VOID  WSEC_ARR_QuickSort(WSEC_ARRAY arr);
WSEC_VOID* WSEC_ARR_BinarySearch(const WSEC_ARRAY arr, const WSEC_VOID* pvKey);
WSEC_INT32 WSEC_ARR_BinarySearchAt(const WSEC_ARRAY arr, const WSEC_VOID* pvKey);

WSEC_VOID WSEC_ARR_StdRemoveElement(WSEC_VOID *pElement);
#ifdef __cplusplus
}
#endif  /* __cplusplus */

#endif/* WIRELESS_SEC_ARRAY_D1ASA42_DCFCVB4E74C27 */
