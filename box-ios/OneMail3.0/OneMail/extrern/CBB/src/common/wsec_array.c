/* ����pc lint�澯�ɺ��� */
/*lint -e506 -e522 -e533 -e534 -e632 -e633 -e634 -e636 -e638 -e639 -e665 -e830 -e960 */

#include "wsec_pri.h"
#include "wsec_array.h"

#ifdef __cplusplus
extern "C"{
#endif /* __cplusplus */

typedef struct tagWSEC_ARRAY_DATA
{
    WSEC_ADDR* pItemAddr;  /* ָ���Ԫ��ָ��ĵ�ַ(ע��, Ԫ�����ݴ洢�ռ���APP����) */
    WSEC_INT32   nCountUsed; /* ʵ����ʹ��Ԫ�ظ��� */
    WSEC_INT32   nCountMax;  /* ������󳤶�, ���Ԫ�ظ�����nCountUse����nGrowNum׷���ڴ���� */
    WSEC_INT32   nGrowNum;   /* �ڴ���������(�������˷�, ��С���׳�����Ƭ) */
    WSEC_FP_Compare       pfCompare;
    WSEC_FP_RemoveElement pfRemoveElement;
} WSEC_ARRAY_DATA_STRU;

#define WSEC_ARR_MEM_SIZE(Num) ((Num) * sizeof(WSEC_VOID*))
#define WSEC_ARR_ITEM_ADDR(pArr, Index) (pArr->pItemAddr + (Index))

WSEC_BOOL WSEC_ARR_CfmSpaceUseful(WSEC_ARRAY_DATA_STRU* pArr);

/*****************************************************************************
 �� �� ��  : WSEC_ARR_CfmSpaceUseful
 ��������  : ȷ������Ŀռ����
             ����Ҫ����������Ԫ��ʱ, ��鵱ǰԪ�ظ���(nCountUsed)�ͷ���
             ����(nCountMax)֮��Ĺ�ϵ�����ռ䲻������nGrowNumΪ��������
             �ڴ�.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : pArr: ����������
 �� �� ֵ  : WSEC_TRUE=�ɹ�, WSEC_FALSE=ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_ARR_CfmSpaceUseful(WSEC_ARRAY_DATA_STRU* pArr)
{
    WSEC_ADDR* pNew = (WSEC_ADDR*)WSEC_NULL_PTR;
    WSEC_VOID* ptr;
    WSEC_SIZE_T nSizeOld, nSizeNew;

    WSEC_ASSERT(pArr);
    if (pArr->nCountMax > pArr->nCountUsed) {return WSEC_TRUE;} /* �ռ��㹻 */

    if_oper(pArr->nGrowNum < 1, pArr->nGrowNum = WSEC_ARR_GROW_BY);

    nSizeOld = WSEC_ARR_MEM_SIZE(pArr->nCountMax);
    nSizeNew = WSEC_ARR_MEM_SIZE(pArr->nCountMax + pArr->nGrowNum);

    pNew = (WSEC_ADDR*)WSEC_MALLOC(nSizeNew);

    if (!pNew)
    {
        return WSEC_FALSE;
    }

    /* �����ڴ��Ǩ�����ڴ� */
    if (pArr->pItemAddr)
    {
        if (WSEC_MEMCPY(pNew, nSizeNew, pArr->pItemAddr, nSizeOld) != EOK) /* ��Ǩʧ��, ���ͷ��µ�, �����ɵ� */
        {
            ptr = (WSEC_VOID*)pNew;
            WSEC_FREE(ptr);
            return WSEC_FALSE;
        }

        /* ��Ǩ�ɹ�, �ͷžɵ� */
        ptr = (WSEC_VOID*)(pArr->pItemAddr);
        WSEC_FREE(ptr);
    }

    pArr->pItemAddr = pNew;
    pArr->nCountMax += pArr->nGrowNum;

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_Initialize
 ��������  : �����ʼ��.
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : nElementNum: ��ʼ����Ԫ�ظ���
             nGrowNum: ����Ҫ��չ�ռ�ʱ, �Զ����������ڴ�
             pfCmpElement: �Ƚ�����Ԫ�ش�С�Ļص�����
             pfRmvElement: ɾ��Ԫ��ǰ�ص�
 �� �� ֵ  : ָ�������������ݽṹ
 �ر�ע��  : �����������ڴ����ڶ�������й���, APP�������WSEC_ARR_Finalize
             �ͷ��ڴ�.

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ARRAY WSEC_ARR_Initialize(WSEC_INT32 nElementNum, WSEC_INT32 nGrowNum, WSEC_FP_Compare pfCmpElement, WSEC_FP_RemoveElement pfRmvElement)
{
    WSEC_ARRAY_DATA_STRU* pData = WSEC_NULL_PTR;

    pData = (WSEC_ARRAY_DATA_STRU*)WSEC_MALLOC(sizeof(WSEC_ARRAY_DATA_STRU));
    if (!pData) {return (WSEC_ARRAY)pData;}

    if (nElementNum < 0) {nElementNum = 0;}
    pData->nGrowNum        = nGrowNum; /* ���ﲻ�ü��, ��չ�ڴ�ʱ�ټ�� */
    pData->pfCompare       = pfCmpElement;
    pData->pfRemoveElement = pfRmvElement;

    if (0 == nElementNum) {return (WSEC_ARRAY)pData;}

    /* Misinformation: FORTIFY.Dead_Code */
    pData->pItemAddr = (WSEC_ADDR*)WSEC_MALLOC(WSEC_ARR_MEM_SIZE(nElementNum));
    if (pData->pItemAddr)
    {
        pData->nCountMax = nElementNum;
    }
    else
    {
        WSEC_FREE(pData);
    }

    return (WSEC_ARRAY)pData;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_Finalize
 ��������  : ��������.
 �� �� ��  : arr: �����ٵ�����
 �� �� ��  : ��
 ��γ���  : pArr: ����������
 �� �� ֵ  : NULLָ��(�����������������Ч)
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_ARRAY WSEC_ARR_Finalize(WSEC_ARRAY arr)
{
    if (arr)
    {
        WSEC_ARR_RemoveAll(arr);
        WSEC_FREE(arr);
    }
    
    return WSEC_NULL_PTR;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_GetAt
 ��������  : ��ȡָ��λ���ϵ�����
 �� �� ��  : arr: ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ����ڸ�λ��ָ��APP�������ݵ�ָ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID* WSEC_ARR_GetAt(const WSEC_ARRAY arr, WSEC_INT32 Index)
{
    const WSEC_ARRAY_DATA_STRU* pArr = (const WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_VOID* pVal = WSEC_NULL_PTR;

    WSEC_ASSERT(arr);
    if (Index < pArr->nCountUsed)
    {
        pVal = *(pArr->pItemAddr + Index);
    }

    return pVal;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_Add
 ��������  : ����Ԫ��
 �� �� ��  : pElement: APP���������ָ��.
 �� �� ��  : ��
 ��γ���  : arr: ����
 �� �� ֵ  : �������ɹ�, �򷵻��ڴ�ŵ�������±�.
             -1: ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 WSEC_ARR_Add(WSEC_ARRAY arr, const WSEC_VOID* pElement)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_INT32 nAt = -1;

    WSEC_ASSERT(arr && pElement);
    if (WSEC_ARR_CfmSpaceUseful(pArr))
    {
        nAt = pArr->nCountUsed;
        *(pArr->pItemAddr + nAt) = (WSEC_VOID*)pElement;
        pArr->nCountUsed++;
    }

    return nAt;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_AddOrderly
 ��������  : ��������Ԫ��
 �� �� ��  : pElement: APP���������ָ��.
 �� �� ��  : ��
 ��γ���  : arr: ����
 �� �� ֵ  : �������ɹ�, �򷵻��ڴ�ŵ�������±�.
             -1: ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 WSEC_ARR_AddOrderly(WSEC_ARRAY arr, const WSEC_VOID* pElement)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_INT32 i, nInsertAt = -1;

    WSEC_ASSERT(arr && pElement && pArr->pfCompare);
    if (!WSEC_ARR_CfmSpaceUseful(pArr)) {return -1;}

    for (i = 0; i < pArr->nCountUsed; i++)
    {
        if (pArr->pfCompare(&pElement, pArr->pItemAddr + i) < 0)
        {
            nInsertAt = i;
            break;
        }
    }

    if_oper(nInsertAt < 0, nInsertAt = pArr->nCountUsed);

    if (!WSEC_ARR_InsertAt(arr, nInsertAt, pElement)) {nInsertAt = -1;}

    return nInsertAt;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_InsertAt
 ��������  : ��ָ��λ���ϲ���Ԫ��
 �� �� ��  : Index: ָ��λ��(������±�)
             pElement: APP���������ָ��.
 �� �� ��  : ��
 ��γ���  : arr: ����
 �� �� ֵ  : WSEC_TRUE=�ɹ�, WSEC_FALSE=ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_ARR_InsertAt(WSEC_ARRAY arr, WSEC_INT32 Index, const WSEC_VOID* pElement)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_ADDR* pAddr = (WSEC_ADDR*)WSEC_NULL_PTR;
    WSEC_INT32 i;

    WSEC_ASSERT(pArr && pElement);
    if ((Index < 0) || (Index > pArr->nCountUsed)) {return WSEC_FALSE;}/* ���ֻ�ܽ���β������ */
    if (!WSEC_ARR_CfmSpaceUseful(pArr)) {return WSEC_FALSE;}

    pArr->nCountUsed++;

    /* [Index, nCountUsed-1]����Ų */
    pAddr = WSEC_ARR_ITEM_ADDR(pArr, pArr->nCountUsed - 1);
    for (i = pArr->nCountUsed - 1; i >= Index; i--, pAddr--)
    {
        *pAddr = *(pAddr - 1);
    }

    /* ��������д�� */
    *(pArr->pItemAddr + Index) = (WSEC_VOID*)pElement;

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_RemoveAt
 ��������  : ɾ��ָ��λ���ϵ�Ԫ��
 �� �� ��  : Index: ָ��λ��(������±�)
             pElement: APP���������ָ��.
 �� �� ��  : ��
 ��γ���  : arr: ����
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_ARR_RemoveAt(WSEC_ARRAY arr, WSEC_INT32 Index)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_INT32 i;

    WSEC_ASSERT(arr);
    if (Index >= pArr->nCountUsed) {return;}

    /* ɾ����Ԫ�� */
    if (pArr->pfRemoveElement) {pArr->pfRemoveElement(*(pArr->pItemAddr + Index));}

    /* [Index+1, nCountUsed-1] ����Ųһ��λ�� */
    for (i = Index + 1; i <= pArr->nCountUsed - 1; i++)
    {
        *(pArr->pItemAddr + i - 1) = *(pArr->pItemAddr + i);
    }

    pArr->nCountUsed--;
    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_RemoveAll
 ��������  : �������
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : arr: ����
 �� �� ֵ  : ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_ARR_RemoveAll(WSEC_ARRAY arr)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_ADDR* p;
    WSEC_VOID* pAddr;
    WSEC_INT32 i;

    if (!pArr) {return;}
    if (!pArr->pItemAddr) {return;}
    
    if (pArr->pfRemoveElement)
    {
        p = pArr->pItemAddr;
        for (i = 0; i < pArr->nCountUsed; i++, p++)
        {
            pArr->pfRemoveElement(*p);
        }
    }else{;}

    pArr->nCountUsed = 0;
    pArr->nCountMax  = 0;

    pAddr = (WSEC_VOID*)pArr->pItemAddr;
    WSEC_FREE(pAddr);
    pArr->pItemAddr = (WSEC_ADDR*)WSEC_NULL_PTR;

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_GetCount
 ��������  : ��ȡ��������ռ��Ԫ�ظ���
 �� �� ��  : arr: ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ������Ԫ�ظ���.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 WSEC_ARR_GetCount(const WSEC_ARRAY arr)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;

    return pArr ? pArr->nCountUsed : 0;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_IsEmpty
 ��������  : �ж������Ƿ�Ϊ��
 �� �� ��  : arr: ����
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : �����Ƿ�Ϊ��
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_ARR_IsEmpty(const WSEC_ARRAY arr)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;

    return pArr ? (pArr->nCountUsed < 1) : WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_QuickSort
 ��������  : �������������
 �� �� ��  : ��
 �� �� ��  : ��
 ��γ���  : arr: ����ǰ�������
 �� �� ֵ  : ������Ԫ�ظ���.
 �ر�ע��  : APP������WSEC_ARR_Initialize()�������ṩ�Ƚ�����Ԫ�ش�С�Ļص�����.
             �ûص������е���������, �ֱ�Ϊ����Ƚϵ���Ԫ�ص�ַ, �������е�Ԫ��
             �洢����APP�������ݵ�ַ, ��� *pA ����ָ��APP���ݵ�ָ��. ����:
            WSEC_INT32 ComparePersonId(const WSEC_VOID* p1, const WSEC_VOID* p2)
            {
                const PERSON_STRU* pManA;
                const PERSON_STRU* pManB;
                WSEC_INT32 nCmp = 0;
            
                pManA = (PERSON_STRU*)(*(const WSEC_VOID**)p1);
                pManB = (PERSON_STRU*)(*(const WSEC_VOID**)p2);
            
                if (pManA->nId > pManB->nId)
                {
                    nCmp = 1;
                }
                else if (pManA->nId < pManB->nId)
                {
                    nCmp = -1;
                }
            
                return nCmp;
            }

 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_ARR_QuickSort(WSEC_ARRAY arr)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;

    if (pArr && pArr->pItemAddr && pArr->pfCompare)
    {
        qsort((WSEC_VOID*)pArr->pItemAddr, pArr->nCountUsed, sizeof(WSEC_VOID*), pArr->pfCompare);
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_BinarySearch
 ��������  : ���ַ���������
 �� �� ��  : arr:  ����
             pvKey: ָ��ؼ��ֵ�ָ��(1��)
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : ��ؼ���ƥ����������ڵ�ַ.
 �ر�ע��  : (1) ȷ������arrΪ��������
             (2) ����������Ҫ���ñȽ�����Ԫ�ش�С�Ļص�����. ������Ļص�������ͬ.
                 Ӧ�ڵ���WSEC_ARR_Initialize()ʱ�ṩ.
             (3) ʾ��:
            ...
            PERSON_STRU* pMan;
            PERSON_STRU stKey = {0};

            stKey.nId = 99; // ����ID=99����
            pMan = (PERSON_STRU*)WSEC_ARR_BinarySearch(g_pArr, &stKey, ComparePersonId);
            ...
 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID* WSEC_ARR_BinarySearch(const WSEC_ARRAY arr, const WSEC_VOID* pvKey)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_ADDR* pFoundAddr;
    WSEC_VOID* pFoundData = WSEC_NULL_PTR;

    if (WSEC_ARR_GetCount(arr) > 0)
    {
        WSEC_ASSERT(pArr && pvKey && pArr->pfCompare);
        if (! pArr->pItemAddr) {return pFoundData;}

        /* Misinformation: FORTIFY.Out-of-Bounds_Read--Off-by-One */
        pFoundAddr = (WSEC_ADDR*)bsearch((const WSEC_VOID*)&pvKey, (WSEC_VOID*)pArr->pItemAddr, pArr->nCountUsed, sizeof(WSEC_VOID*), pArr->pfCompare);

        if (pFoundAddr) {pFoundData = *pFoundAddr;}
    }

    return pFoundData;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_BinarySearchAt
 ��������  : ���ַ���������
 �� �� ��  : arr:  ����
             pvKey: ָ��ؼ��ֵ�ָ��(1��)
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : �Ǹ���: ��ؼ���ƥ��������������е��±�;
                 -1: ����ʧ��.
 �ر�ע��  : (1) ȷ������pArrΪ��������
             (2) ����������Ҫ���ñȽ�����Ԫ�ش�С�Ļص�����. ������Ļص�������ͬ.
                 Ӧ�ڵ���WSEC_ARR_Initialize()ʱ�ṩ.
             (3) ʾ��:
            ...
            WSEC_INT32 nAt;
            PERSON_STRU* pMan;
            PERSON_STRU stKey = {0};

            stKey.nId = 99; // ����ID=99����
            nAt = WSEC_ARR_BinarySearchAt(g_pArr, &stKey, ComparePersonId);
            if (nAt < 0)
            {
                err_out("cannot find.");
            }
            else
            {
                pMan = (PERSON_STRU*)WSEC_ARR_GetAt(g_pArr, nAt);
            }
            ...
 �޸���ʷ
  1.��    ��   : 2014��10��27��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 WSEC_ARR_BinarySearchAt(const WSEC_ARRAY arr, const WSEC_VOID* pvKey)
{
    WSEC_ARRAY_DATA_STRU* pArr = (WSEC_ARRAY_DATA_STRU*)arr;
    WSEC_ADDR* pFoundAddr;
    WSEC_INT32 nAt = -1;

    if (WSEC_ARR_GetCount(arr) > 0)
    {
        WSEC_ASSERT(pArr && pArr->pItemAddr && pvKey && pArr->pfCompare);
        pFoundAddr = (WSEC_ADDR*)bsearch((const WSEC_VOID*)&pvKey, (WSEC_VOID*)pArr->pItemAddr, pArr->nCountUsed, sizeof(WSEC_VOID*), pArr->pfCompare);

        if (pFoundAddr) {nAt = (WSEC_INT32)(pFoundAddr - pArr->pItemAddr);}
    }

    return nAt;
}

/*****************************************************************************
 �� �� ��  : WSEC_ARR_StdRemoveElement
 ��������  : ��׼��ɾ������ڵ�
             ����Ľڵ��ŵ����û������ݵ�ַ, �����ַ����׼���÷��Ƕ�̬����
             ���ڴ�, ���ɾ����Ԫ��ʱӦ�ͷ��ڴ�.
 �� �� ��  : pElement: ����Ԫ�����ڵ�ַ
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : 
 �ر�ע��  : 

 �޸���ʷ
  1.��    ��   : 2015��3��24��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_ARR_StdRemoveElement(WSEC_VOID *pElement)
{
    WSEC_FREE(pElement);
    return;
}

#ifdef __cplusplus
}
#endif  /* __cplusplus */
