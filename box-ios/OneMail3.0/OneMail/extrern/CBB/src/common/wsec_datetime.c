/* ����pc lint�澯�ɺ��� */
/*lint -e506 -e522 -e533 -e534 -e632 -e633 -e634 -e638 -e639 -e641 -e665 -e830 -e960 */

#include "wsec_type.h"
#include "wsec_pri.h"
#include "time.h"

#ifdef __cplusplus
extern "C"{
#endif /* __cplusplus */

#ifdef CBB_INNER_TEST
extern void APP_DateTimeSpeedup(WSEC_SYSTIME_T* pTime);
#endif

#define WSEC_IS_LEAP_YEAR(y) (((!((y) % 4)) && ((y) % 100)) || (!((y) % 400)))
#define WSEC_MONTH_DAYS(y, m) (((4==(m))||(6==(m))||(9==(m))||(11==(m))) ? 30 : ((2==(m)) ? (WSEC_IS_LEAP_YEAR(y) ? 29 : 28) : 31))
#define WSEC_WEEK_DAYS (7)

/* ��ģ��˽�к����������⿪�� */
WSEC_VOID WSEC_Tm2WsecSystemTime(const struct tm* pTm, WSEC_SYSTIME_T* pWsecTime);
WSEC_BOOL WSEC_LocalTimeMinusUtcSec(WSEC_INT32* pDiffSec);
WSEC_BOOL WSEC_OsLocalTime(struct tm * _Tm, const time_t * _Time);
WSEC_BOOL WSEC_OsGmTime(struct tm * _Tm, const time_t * _Time);

/*****************************************************************************
 �� �� ��  : WSEC_DateTimeAdd
 ��������  : ��ָ��������&ʱ��(pstBase)��nAdd��ʱ�䵥λ(ePart)��
             ��������pstNew��
 �������  : pstBase: ����ӷ������ڼ�ʱ��
             nAdd:    ������, �������ʾ������
             ePart:   ������&ʱ����ĸ��������ӷ�
 �������  : pstNew:  ������
 �� �� ֵ  : ��

 �޸���ʷ      :
  1.��    ��   : 2014��7��11��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_DateTimeAdd(const WSEC_SYSTIME_T* pstBase, WSEC_INT32 nAdd, WSEC_DATETIME_PART_ENUM ePart, WSEC_SYSTIME_T* pstNew)
{
    WSEC_INT32 i, nOffset, y, m, d, nDayOffset;
    WSEC_INT32 aItem[]            = {dtpSecond, dtpMinutes, dtpHour, dtpDay, dtpMonth, dtpYear}; /* ��ʱ��ʼ�������"non-constant aggregate initializer"�澯 */
    const WSEC_INT32 aTop[]       = {59,            59,        23,     -1,      12}; /* ���� */
    const WSEC_INT32 aRound[]     = {60,            60,        24,     0,       12}; /* �ֻ�ֵ */
    WSEC_UINT8 bHandleDay = 0;

    WSEC_ASSERT(pstBase && WSEC_IsDateTime(pstBase) && pstNew);
    if (0 == nAdd)
    {
        WSEC_DateTimeCopy(pstNew, pstBase);
        return;
    }

    aItem[0] = pstBase->ucSecond;
    aItem[1] = pstBase->ucMinute;
    aItem[2] = pstBase->ucHour;
    aItem[3] = pstBase->ucDate;
    aItem[4] = pstBase->ucMonth;
    aItem[5] = pstBase->uwYear;

    nDayOffset = 0;
    aItem[ePart] += nAdd;

    for (i = ePart; i < dtpYear; i++)
    {
        if ((aItem[i] > aTop[i]) || (aItem[i] <= 0)) /* ��λ or ��λ */
        {
            if (aRound[i]) /* �ǡ��ա��������ʹ��ͳһ��ʽ���� */
            {
                nOffset = aItem[i] / aRound[i];
                if_oper((aItem[i] % aRound[i]) && (aItem[i] < 0), nOffset--);

                aItem[i + 1] += nOffset;
                aItem[i] -= aRound[i] * nOffset;

                if (dtpMonth == ePart) /* �·ݲ�ͬ��ʱ����, ��ʼ��1 */
                {
                    /* Misinformation: FORTIFY.Dead_Code */
                    if (0 == aItem[dtpMonth])
                    {
                        aItem[dtpMonth] = aRound[dtpMonth];
                        aItem[dtpYear]--;
                    }else{;}
                }else{;}
            }
            else /* ���ա��Ĵ��� */
            {
                bHandleDay = 1;
                break;
            }
        }else{;}
    }

    if (bHandleDay) /* �ԡ��ա��Ĵ��� */
    {
        y = aItem[dtpYear];
        m = aItem[dtpMonth];
        d = aItem[dtpDay];

        nDayOffset = d - pstBase->ucDate; /* ���ա��ı仯�� */

        while (!WSEC_IN_SCOPE(d, 1, WSEC_MONTH_DAYS(y, m)))
        {
            if (d > 0)
            {
                d -= WSEC_MONTH_DAYS(y, m);

                m++;
                if (m > aTop[dtpMonth]) /* ��׷�ת */
                {
                    y++;
                    m = 1;
                }else{;}
            }
            else
            {
                m--;
                if (0 == m) /* �����ת */
                {
                    m = aRound[dtpMonth];
                    y--;
                }else{;}

                d += WSEC_MONTH_DAYS(y, m);
            }
        }

        aItem[dtpYear]  = y;
        aItem[dtpMonth] = m;
        aItem[dtpDay]   = d;
    }

    if ((dtpYear == ePart) || (dtpMonth == ePart)) /* ����/��Ϊ��λ�ļӷ����赥��ͳ�ơ��ա��ı仯�� */
    {
        /* Misinformation: FORTIFY.Dead_Code */
        y = (WSEC_INT32)pstBase->uwYear;
        m = (WSEC_INT32)pstBase->ucMonth;

        while ((y != aItem[dtpYear]) || (m != aItem[dtpMonth]))
        {
            if (nAdd > 0)
            {
                nDayOffset += WSEC_MONTH_DAYS(y, m);
                m++;
                if (m > aTop[dtpMonth]) /* ��׷�ת */
                {
                    y++;
                    m = 1;
                }else{;}
            }
            else
            {
                m--;
                if (0 == m) /* �����ת */
                {
                    m = aRound[dtpMonth];
                    y--;
                }else{;}
                nDayOffset += WSEC_MONTH_DAYS(y, m);
            }
        }

        if_oper(nAdd < 0, nDayOffset *= (-1));
    }

    if ((2 == aItem[dtpMonth]) && (29 == aItem[dtpDay]) && (!WSEC_IS_LEAP_YEAR(aItem[dtpYear]))) /* ֻ���������2��29��������Ҫ���� */
    {
        if (nAdd > 0) /* �ӷ������������ */
        {
            aItem[dtpMonth] = 3;
            aItem[dtpDay]   = 1;
        }
        else /* ������������� */
        {
            aItem[dtpMonth] = 2;
            aItem[dtpDay]   = 28;
        }
    }else{;}

    pstNew->ucSecond = (WSEC_UINT8)aItem[dtpSecond];
    pstNew->ucMinute = (WSEC_UINT8)aItem[dtpMinutes];
    pstNew->ucHour   = (WSEC_UINT8)aItem[dtpHour];
    pstNew->ucDate   = (WSEC_UINT8)aItem[dtpDay];
    pstNew->ucMonth  = (WSEC_UINT8)aItem[dtpMonth];
    pstNew->uwYear   = (WSEC_UINT16)aItem[dtpYear];

    nDayOffset %= WSEC_WEEK_DAYS;
    if_oper(nDayOffset < 0, nDayOffset += WSEC_WEEK_DAYS);
    pstNew->ucWeek = (((pstBase->ucWeek - 1) + nDayOffset) % WSEC_WEEK_DAYS) + 1;

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_DateTimeCopy
 ��������  : ����DateTime�ṹ
 �� �� ��  : pstSrc: Դ����
 �� �� ��  : pstDst: Ŀ������
 ��γ���  : ��
 �� �� ֵ  : �ɹ���ʧ��.
 �ر�ע��  : ��

 �޸���ʷ
  1.��    ��   : 2014��10��20��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_DateTimeCopy(WSEC_SYSTIME_T* pstDst, const WSEC_SYSTIME_T *pstSrc)
{
    WSEC_ASSERT(pstDst && pstSrc && WSEC_IsDateTime(pstSrc));
    return (WSEC_MEMCPY(pstDst, sizeof(WSEC_SYSTIME_T), pstSrc, sizeof(WSEC_SYSTIME_T)) == EOK);
}

/*****************************************************************************
 �� �� ��  : WSEC_IsDateTime
 ��������  : �ж�ָ���Ĳ����Ƿ��ǺϷ�������&ʱ��
             ֻ�ж��ꡢ�¡��ա�ʱ���֡���ȵ�ȡֵ��Ϸ�;
             �ж�week�Ƿ��ںϷ���Χ�����޷��ж�week��ֵ��������ƥ��.
 �������  : pstVal: ���ж�ֵ
 �������  : ��
 �� �� ֵ  : ��=�Ϸ�������&ʱ��; ��=�Ƿ�

 �޸���ʷ      :
  1.��    ��   : 2014��7��11��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_IsDateTime(const WSEC_SYSTIME_T* pstVal)
{
    WSEC_BOOL bOk = (0 < pstVal->uwYear) 
           && WSEC_IN_SCOPE(pstVal->ucMonth, 1, 12) /* ��: 1~12 */
           && (pstVal->ucHour < 24)    /* ʱ: 0~23 */
           && (pstVal->ucMinute < 60)  /* ��: 0~59 */
           && (pstVal->ucSecond <= 60); /* ��: 0~60(����) */

    if (bOk)
    {
        bOk = WSEC_IN_SCOPE(pstVal->ucDate, 1, WSEC_MONTH_DAYS(pstVal->uwYear, pstVal->ucMonth));
    }

    return bOk;
}

/*****************************************************************************
 �� �� ��  : WSEC_DateTimeCompare
 ��������  : �Ƚ�pstBig��pstSmall����������&ʱ��Ĵ�С
 �� �� ��  : pstBig: ����Ƚϵ�����;
             pstSmall: ����Ƚϵ�����;
 �� �� ��  : ��
 ��γ���  : ��
 �� �� ֵ  : WSEC_CMP_RST_EQUAL:      A == B
             WSEC_CMP_RST_BIG_THAN:   A  > B
             WSEC_CMP_RST_SMALL_THAN: A  < B
 �ر�ע��  : ��������ʹ�ú���:
             ......
             WSEC_CMP_RST_ENUM eCmp;

             eCmp = WSEC_DateTimeCompare(&stA, &stB);
             if (eCmp > 0)
             {
                <stA����stB>
             }
             else if(eCmp < 0)
             {
                <stAС��stB>
             }
             else
             {
                <stA����stB>
             }
             ......

 �޸���ʷ
  1.��    ��   : 2014��7��11��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_CMP_RST_ENUM WSEC_DateTimeCompare(const WSEC_SYSTIME_T* pstBig, const WSEC_SYSTIME_T* pstSmall)
{
    WSEC_ASSERT(pstBig && WSEC_IsDateTime(pstBig) && pstSmall && WSEC_IsDateTime(pstSmall));
    
    /* �� */
    if (pstBig->uwYear > pstSmall->uwYear) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->uwYear < pstSmall->uwYear) {return WSEC_CMP_RST_SMALL_THAN;}

    /* �� */
    if (pstBig->ucMonth > pstSmall->ucMonth) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->ucMonth < pstSmall->ucMonth) {return WSEC_CMP_RST_SMALL_THAN;}

    /* �� */
    if (pstBig->ucDate > pstSmall->ucDate) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->ucDate < pstSmall->ucDate) {return WSEC_CMP_RST_SMALL_THAN;}

    /* ʱ */
    if (pstBig->ucHour > pstSmall->ucHour) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->ucHour < pstSmall->ucHour) {return WSEC_CMP_RST_SMALL_THAN;}

    /* �� */
    if (pstBig->ucMinute > pstSmall->ucMinute) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->ucMinute < pstSmall->ucMinute) {return WSEC_CMP_RST_SMALL_THAN;}

    /* �� */
    if (pstBig->ucSecond > pstSmall->ucSecond) {return WSEC_CMP_RST_BIG_THAN;}
    if (pstBig->ucSecond < pstSmall->ucSecond) {return WSEC_CMP_RST_SMALL_THAN;}

    return WSEC_CMP_RST_EQUAL;
}

/*****************************************************************************
 �� �� ��  : WSEC_DateTimeDiff
 ��������  : ����stTo�����stFrom������ٸ�ʱ�䵥λeUnit��
 �������  : eUnit:   �����ֵ��ʱ�䵥λ����/��/��/ʱ/��/��
             pstFrom: ������
             pstTo:   ����
 �������  : ��
 �� �� ֵ  : ʱ���ֵ

 �޸���ʷ      :
  1.��    ��   : 2014��7��18��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_INT32 WSEC_DateTimeDiff(WSEC_DATETIME_PART_ENUM eUnit, const WSEC_SYSTIME_T* pstFrom, const WSEC_SYSTIME_T* pstTo)
{
    const WSEC_INT32 aRound[] = {60, 60, 24, 0, 12}; /* �ֻ�ֵ */
    WSEC_INT32 nDays = 0;
    WSEC_INT32 nDiff = 0;
    WSEC_UINT16 y, y1;
    WSEC_UINT8 m, m1;
    WSEC_INT32 nCmp;

    WSEC_ASSERT(pstFrom && WSEC_IsDateTime(pstFrom) && pstTo && WSEC_IsDateTime(pstTo));

    /* ����/��Ϊ��λ�Ĳ�ֵ������ֱ�Ӽ��� */
    if (dtpYear == eUnit)
    {
        return (WSEC_INT32)(pstTo->uwYear - pstFrom->uwYear);
    }
    else if (dtpMonth == eUnit)
    {
        return ((WSEC_INT32)(pstTo->uwYear - pstFrom->uwYear)) * aRound[dtpMonth]
              + (WSEC_INT32)(pstTo->ucMonth - pstFrom->ucMonth);
    }else{;}
    
    /* ������������ */
    nCmp = WSEC_DateTimeCompare(pstTo, pstFrom);
    if (nCmp > 0) /* stTo > stFrom */
    {
        y = pstFrom->uwYear;
        m = pstFrom->ucMonth;

        y1 = pstTo->uwYear;
        m1 = pstTo->ucMonth;

        nDays =(WSEC_INT32)(pstTo->ucDate - pstFrom->ucDate);
    }
    else /* stTo <= stFrom */
    {
        y = pstTo->uwYear;
        m = pstTo->ucMonth;

        y1 = pstFrom->uwYear;
        m1 = pstFrom->ucMonth;

        nDays =(WSEC_INT32)(pstFrom->ucDate - pstTo->ucDate);
    }

    /* �ԡ��¡�Ϊ������ͳ���������� */
    while ((y != y1) || (m != m1))
    {
        nDays += WSEC_MONTH_DAYS(y, m);
        m++;

        if (m > aRound[dtpMonth]) /* ��ݷ�ת */
        {
            y++;
            m = 1;
        }else{;}
    }

    if_oper(nCmp < 0, nDays *= (-1));

    if (dtpDay == eUnit)
    {
        return nDays;
    }

    nDiff = nDays * aRound[dtpHour] + (WSEC_INT32)pstTo->ucHour - (WSEC_INT32)pstFrom->ucHour; /* ������Сʱ */

    if (dtpMinutes == eUnit)
    {
        /* Misinformation: FORTIFY.Dead_Code */
        nDiff = nDiff * aRound[dtpMinutes] + (pstTo->ucMinute - pstFrom->ucMinute);
    }
    else if (dtpSecond == eUnit)
    {
        nDiff = nDiff * aRound[dtpMinutes] + (pstTo->ucMinute - pstFrom->ucMinute);
        nDiff = nDiff * aRound[dtpSecond] + (pstTo->ucSecond - pstFrom->ucSecond);
    }else{;}

    return nDiff;
}

/*****************************************************************************
 �� �� ��  : WSEC_GetLocalDateTime
 ��������  : ��ȡ��ǰ��������&ʱ��
 �������  : ��
 �������  : pstNow �ش���ǰ��������&ʱ��
 �� �� ֵ  : �Ƿ��ȡ�ɹ�����ϵͳʱ����1970ǰ����ʧ��.

 �޸���ʷ      :
  1.��    ��   : 2014��7��22��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_GetLocalDateTime(WSEC_SYSTIME_T* pstNow)
{
    time_t tNow;
    struct tm tTime = {0};

#ifdef CBB_INNER_TEST
    WSEC_SYSTIME_T tUtc;
    WSEC_GetUtcDateTime(&tUtc);
    WSEC_UtcTime2Local(&tUtc, pstNow);
    return WSEC_TRUE;
#endif    

    WSEC_ASSERT(pstNow);

    time(&tNow);
    return_oper_if(!WSEC_OsLocalTime(&tTime, &tNow), oper_null, WSEC_FALSE);

    pstNow->uwYear   = (WSEC_UINT16)(tTime.tm_year + 1900);
    pstNow->ucMonth  = (WSEC_UINT8)(tTime.tm_mon + 1);
    pstNow->ucDate   = (WSEC_UINT8)tTime.tm_mday;
    pstNow->ucHour   = (WSEC_UINT8)tTime.tm_hour;
    pstNow->ucMinute = (WSEC_UINT8)tTime.tm_min;
    pstNow->ucSecond = (WSEC_UINT8)tTime.tm_sec;
    pstNow->ucWeek   = (WSEC_UINT8)tTime.tm_wday;

    if (0 == pstNow->ucWeek)
    {
        pstNow->ucWeek = 7;
    }

    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_GetUtcDateTime
 ��������  : ��ȡ��ǰUTC(Universal Time Coordinated)����&ʱ��
 �������  : ��
 �������  : pstNow �ش���ǰUTC����&ʱ��
 �� �� ֵ  : �Ƿ��ȡ�ɹ�.

 �޸���ʷ      :
  1.��    ��   : 2014��7��22��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_GetUtcDateTime(WSEC_SYSTIME_T* pstNow)
{
    time_t tNow;
    struct tm tTime;

    WSEC_ASSERT(pstNow);
    
    time(&tNow);

    return_oper_if(!WSEC_OsGmTime(&tTime, &tNow), oper_null, WSEC_FALSE);

    WSEC_Tm2WsecSystemTime(&tTime, pstNow);

#ifdef CBB_INNER_TEST
    APP_DateTimeSpeedup(pstNow);
#endif
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_Tm2WsecSystemTime
 ��������  : ��C�����е�tm���ݽṹ(pTm)ת��ΪWSEC_SYSTIME_T
 �������  : ��
 �������  : ��
 �� �� ֵ  : ��
 ע    ��  : �������Ǹ�ģ���ڲ���˽�к���.

 �޸���ʷ      :
  1.��    ��   : 2014��7��25��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_VOID WSEC_Tm2WsecSystemTime(const struct tm* pTm, WSEC_SYSTIME_T* pWsecTime)
{
    WSEC_ASSERT(pTm && pWsecTime);
    
    pWsecTime->uwYear   = (WSEC_UINT16)(pTm->tm_year + 1900);
    pWsecTime->ucMonth  = (WSEC_UINT8)(pTm->tm_mon + 1);
    pWsecTime->ucDate   = (WSEC_UINT8)pTm->tm_mday;
    pWsecTime->ucHour   = (WSEC_UINT8)pTm->tm_hour;
    pWsecTime->ucMinute = (WSEC_UINT8)pTm->tm_min;
    pWsecTime->ucSecond = (WSEC_UINT8)pTm->tm_sec;
    pWsecTime->ucWeek   = (WSEC_UINT8)pTm->tm_wday;

    if (0 == pWsecTime->ucWeek)
    {
        pWsecTime->ucWeek = 7;
    }

    return;
}

/*****************************************************************************
 �� �� ��  : WSEC_LocalTimeMinusUtcSec
 ��������  : ���㱾��ʱ�䳬��UTCʱ�������
 �������  : ��
 �������  : pDiffSec: �ش���ֵ
 �� �� ֵ  : �ɹ���񣬵�ϵͳʱ����1970ǰ����ʧ��.
 ע    ��  : �������Ǹ�ģ���ڲ���˽�к���.

 �޸���ʷ      :
  1.��    ��   : 2014��7��25��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_LocalTimeMinusUtcSec(WSEC_INT32* pDiffSec)
{
    time_t tSysNow, tLocal;
    struct tm tmLocal;
    struct tm tmUtc;
    static WSEC_INT32 nDiffSec = -1;

    if (nDiffSec != -1) /* ֱ��ʹ���ϴμ����� */
    {
        *pDiffSec = nDiffSec;
        return WSEC_TRUE;
    }

    time(&tSysNow);
    return_oper_if(!WSEC_OsLocalTime(&tmLocal, &tSysNow), oper_null, WSEC_FALSE);
    tLocal = mktime(&tmLocal);

    return_oper_if(!WSEC_OsGmTime(&tmUtc, &tSysNow), oper_null, WSEC_FALSE);

    nDiffSec  = (WSEC_INT32)(tLocal - mktime(&tmUtc));
    *pDiffSec = nDiffSec;
    
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_UtcTime2Local
 ��������  : ��UTCʱ��(pUtc)ת��Ϊ����ʱ��(pLocal)
 �������  : ��
 �������  : ��
 �� �� ֵ  : �ɹ���񣬵�ϵͳʱ����1970ǰ����ʧ��.

 �޸���ʷ      :
  1.��    ��   : 2014��7��25��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_UtcTime2Local(const WSEC_SYSTIME_T* pUtc, WSEC_SYSTIME_T* pLocal)
{
    WSEC_INT32 nLocalMinusUtcSec;

    return_oper_if(!WSEC_LocalTimeMinusUtcSec(&nLocalMinusUtcSec), oper_null, WSEC_FALSE);
    WSEC_DateTimeAdd(pUtc, nLocalMinusUtcSec, dtpSecond, pLocal);
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_LocalTime2Utc
 ��������  : ������ʱ��(pLocal)ת��ΪUTCʱ��(pUtc)
 �������  : ��
 �������  : ��
 �� �� ֵ  : �ɹ���񣬵�ϵͳʱ����1970ǰ����ʧ��.

 �޸���ʷ      :
  1.��    ��   : 2014��7��25��
    ��    ��   : z118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_BOOL WSEC_LocalTime2Utc(const WSEC_SYSTIME_T* pLocal, WSEC_SYSTIME_T* pUtc)
{
    WSEC_INT32 nLocalMinusUtcSec;

    WSEC_ASSERT(pLocal && WSEC_IsDateTime(pLocal) && pUtc);

    return_oper_if(!WSEC_LocalTimeMinusUtcSec(&nLocalMinusUtcSec), oper_null, WSEC_FALSE);
    WSEC_DateTimeAdd(pLocal, (-1)*nLocalMinusUtcSec, dtpSecond, pUtc);
    return WSEC_TRUE;
}

/*****************************************************************************
 �� �� ��  : WSEC_DateTime2String
 ��������  : ��ʱ������������ַ���
 �� �� ��  : pTime: ����&ʱ��
             nBuffSize: �������������
 �� �� ��  : pszBuff: ���������;
 ��γ���  : ��
 �� �� ֵ  : ָ�������������ָ��(��������)
 �ر�ע��  : ������ʹ�øú���
             ......
             WSEC_SYSTIME_T stNow;
             WSEC_CHAR szTime[30];
             
             WSEC_GetLocalDateTime(&stNow);
             printf("Now: %s", WSEC_DateTime2String(&stNow, szTime, sizeof(szTime)));
             ......

 �޸���ʷ
  1.��    ��   : 2014��11��13��
    ��    ��   : z00118096
    �޸�����   : �����ɺ���
*****************************************************************************/
WSEC_CHAR* WSEC_DateTime2String(const WSEC_SYSTIME_T* pTime, WSEC_VOID* pBuff, WSEC_SIZE_T nBuffSize)
{
    /*lint -e506 -e668 */
    WSEC_UNCARE(sprintf_s((char*)pBuff, nBuffSize, "%04d-%02d-%02d %02d:%02d:%02d(Week-%d)", pTime->uwYear, pTime->ucMonth, pTime->ucDate, pTime->ucHour, pTime->ucMinute, pTime->ucSecond, pTime->ucWeek));
    return (WSEC_CHAR*)pBuff;
}

WSEC_BOOL WSEC_OsLocalTime(struct tm * _Tm, const time_t * _Time)
{
//    WSEC_DISABLE_WARNING_BEGIN(4996)
    struct tm* outTm = localtime(_Time);
    return_oper_if(!outTm, oper_null, WSEC_FALSE);

    *_Tm = *outTm;
    return WSEC_MEMCPY(_Tm, sizeof(struct tm), outTm, sizeof(struct tm)) == EOK ? WSEC_TRUE : WSEC_FALSE;

//    WSEC_DISABLE_WARNING_END
}

WSEC_BOOL WSEC_OsGmTime(struct tm * _Tm, const time_t * _Time)
{
//    WSEC_DISABLE_WARNING_BEGIN(4996)

    struct tm* outTm = gmtime(_Time);

    return_oper_if(!outTm, oper_null, WSEC_FALSE);
    
    *_Tm = *outTm;
    return WSEC_MEMCPY(_Tm, sizeof(struct tm), outTm, sizeof(struct tm)) == EOK ? WSEC_TRUE : WSEC_FALSE;

//    WSEC_DISABLE_WARNING_END
}

#ifdef __cplusplus
}
#endif  /* __cplusplus */
