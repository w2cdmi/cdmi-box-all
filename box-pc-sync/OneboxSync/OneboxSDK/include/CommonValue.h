#ifndef __ONEBOX__COMMONVALUE__H__
#define __ONEBOX__COMMONVALUE__H__

#include "CommonDefine.h"

#ifndef HEAD_CONTENT_TYPE
#define HEAD_CONTENT_TYPE "Content-Type"
#endif

#ifndef HEAD_CONTENT_LENGTH
#define HEAD_CONTENT_LENGTH "Content-Length"
#endif

#ifndef HEAD_DATE
#define HEAD_DATE "Date"
#endif

#ifndef HEAD_VARY
#define HEAD_VARY "Vary"
#endif

#ifndef HEAD_TRANSFER_ENCODING
#define HEAD_TRANSFER_ENCODING "Transfer-Encoding"
#endif

#ifndef HEAD_AUTHORIZATION
#define HEAD_AUTHORIZATION "Authorization"
#endif

#ifndef HEAD_RANGE
#define HEAD_RANGE "Range"
#endif

#ifndef CONNECTION
#define CONNECTION "Connection"
#endif

#ifndef HEAD_DEVICE_SN
#define HEAD_DEVICE_SN "x-device-sn"
#endif

#ifndef HEAD_DEVICE_OS
#define HEAD_DEVICE_OS "x-device-os"
#endif

#ifndef HEAD_DEVICE_NAME
#define HEAD_DEVICE_NAME "x-device-name"
#endif

#ifndef HEAD_DEVICE_TYPE
#define HEAD_DEVICE_TYPE "x-device-type"
#endif

#ifndef HEAD_DEVICE_VERSION
#define HEAD_DEVICE_VERSION "x-client-version"
#endif

#ifndef BODY_BUF_SIZE
#define BODY_BUF_SIZE    (64 * 1024)
#endif

typedef int32_t (ReadDataCallback)(void *buffer, size_t bufferSize, 
                               void *callbackData);

typedef int32_t (WriteDataCallback)(void *buffer, size_t bufferSize,
                                void *callbackData);

typedef struct DATA_BUFFER_STRU
{
    unsigned char *pBuf;
    int64_t lBufLen;
    int64_t lOffset;
    void * uDefParam;

    typedef void (*Buffer_Free)(void *);
    Buffer_Free pFreeFunc;

    DATA_BUFFER_STRU()
        : pBuf(NULL), lBufLen(0), lOffset(0),
          uDefParam(NULL), pFreeFunc(NULL)
    {}

    /*lint -e(1740) */
    virtual ~DATA_BUFFER_STRU()
    {
        if ((pFreeFunc) && (pBuf))
        {
            pFreeFunc(pBuf);
			pBuf = NULL;
			lBufLen = 0;
			lOffset = 0;
        }
    }
}DataBuffer;

struct Malloc_Buffer : public DATA_BUFFER_STRU
{
    /*lint -e(578) */
    Malloc_Buffer(size_t lBufLen)
    {
        this->pBuf = (unsigned char *)::malloc(lBufLen);
        if (this->pBuf)
        {
            this->lBufLen = lBufLen;
            this->pFreeFunc = &::free;
            ::memset(this->pBuf, 0, lBufLen);
        }
        else
        {
            this->lBufLen = 0;
            this->pFreeFunc = NULL;
        }
    }
};

typedef struct HEADER_BUFFER_STRU
{
    std::string strHeader;
    bool done;
}HeaderBuffer;

struct RequestParam
{
    DataBuffer   *writeData;
    DataBuffer   *readData;
    HeaderBuffer *responseHeader;
    ReadDataCallback  *readDataCallback;
    WriteDataCallback *writeDataCallback;
	int32_t httpMethod;

    RequestParam()
        : writeData(NULL), readData(NULL),
          responseHeader(NULL), readDataCallback(NULL),
          writeDataCallback(NULL), httpMethod(-1)
    {}
};

typedef enum _HttpMethod
{
	HttpRequestTypeGET,
	HttpRequestTypeHEAD,
	HttpRequestTypePUT,
	HttpRequestTypePOST,
	HttpRequestTypeCOPY,
	HttpRequestTypeDELETE,
	HttpRequestTypeInvalid,
}HttpMethod;

typedef enum _SERVICE_TYPE
{
	SERVICE_CLOUDAPP = 0,
	SERVICE_UAM = 1,
	SERVICE_UFM = 2,
	SERVICE_END
}SERVICE_TYPE;

typedef enum _OPTION_TYPE
{
	OPTION_LINK_ACCESS_KEY_RULE = 0,
	OPTION_SYSTEM_MAX_VERSIONS = 1,
	OPTION_ALL = 2,
	OPTION_END
}OPTION_TYPE;

#endif // end of defined  __ONEBOX__COMMONVALUE__H__
