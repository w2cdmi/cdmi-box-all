/******************************************************************************
��Ȩ����  : 2010-2020����Ϊ�������˿Ƽ����޹�˾
�� �� ��  : HttpCbFuns.cpp
��    ��  : ������
��    ��  : V1.0
��������  : 2011-12-24
��    ��  : CS http�ص�����Դ�ļ�
�����б�  :

��ʷ��¼   :
1.��    �� : 2011-12-24
��    ��   : ������
�޸�����   : ��ɳ���

*******************************************************************************/
#include "HttpCbFuns.h"
#include "CommonValue.h"
#include <assert.h>

int32_t HttpCbFuns::readBodyDataCallback(void *buffer, uint32_t bufferSize, void *callbackData)
{
    assert(buffer != NULL);
    assert(callbackData != NULL);

    if (NULL == buffer || NULL == callbackData)
    {
        return RT_INVALID_PARAM;
    }

    DataBuffer *data = (DataBuffer *)callbackData;

    if (NULL == data->pBuf)
    {
        return 0;
    }

    size_t byte2copy = bufferSize;

    if ((data->lBufLen < data->lOffset) ||
        ((data->lBufLen - data->lOffset) < bufferSize))
    {
        size_t newLen = (size_t)(data->lBufLen + bufferSize) * 2;

        data->pBuf = (unsigned char *)realloc(data->pBuf, newLen);

        if (NULL == data->pBuf)
        {
            return RT_MEMORY_MALLOC_ERROR;
        }
        data->lBufLen = newLen;
    }

    memcpy_s((data->pBuf + data->lOffset), byte2copy, buffer, byte2copy);
    data->lOffset += byte2copy;
    return (int32_t)byte2copy;
}

int32_t HttpCbFuns::putObjectDataCallback(void *buffer, uint32_t bufferSize, void *callbackData)
{
    assert(buffer != NULL);
    assert(callbackData != NULL);

    if (NULL == buffer || NULL == callbackData)
    {
        return RT_INVALID_PARAM;
    }

    DataBuffer *data = (DataBuffer *)callbackData;

    if ((NULL == data->pBuf)
        || (data->lBufLen <= data->lOffset))
    {
        return 0;
    }

    size_t byte2copy = (size_t)(((data->lBufLen - data->lOffset) < bufferSize) ? (data->lBufLen - data->lOffset) : bufferSize);
    memcpy_s(buffer, byte2copy, (data->pBuf + data->lOffset), byte2copy);
    data->lOffset += byte2copy;
    return (int32_t)byte2copy;
}

int32_t HttpCbFuns::getObjectDataCallback(void *buffer, uint32_t bufferSize, void *callbackData)
{
    assert(buffer != NULL);
    assert(callbackData != NULL);

    if (NULL == buffer || NULL == callbackData)
    {
        return RT_INVALID_PARAM;
    }

    DataBuffer *data = (DataBuffer *)callbackData;

    if ((NULL == data->pBuf)
        || (data->lBufLen <= data->lOffset))
    {
        return 0;
    }

    size_t byte2copy = (size_t)(((data->lBufLen - data->lOffset) < bufferSize) ? (data->lBufLen - data->lOffset) : bufferSize);

    memcpy_s((data->pBuf + data->lOffset), byte2copy, buffer, byte2copy);
    data->lOffset += byte2copy;
    return (int32_t)byte2copy;
}

int32_t HttpCbFuns::downloadFileCallback(void *buffer, uint32_t bufferSize, void *callbackData)
{
	assert(buffer != NULL);
	assert(callbackData != NULL);

	if (NULL == buffer || NULL == callbackData)
	{
		return RT_INVALID_PARAM;
	}

	FILE *file = (FILE *)((DataBuffer *)callbackData)->uDefParam;
	if (NULL == file)
	{
		return 0;
	}

	return fwrite(buffer, sizeof(char), bufferSize, file);
}

int32_t HttpCbFuns::uploadFileCallback(void *buffer, uint32_t bufferSize, void *callbackData)
{
	return RT_NOT_IMPLEMENT;
}
