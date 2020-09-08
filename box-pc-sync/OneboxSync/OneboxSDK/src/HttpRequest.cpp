#pragma warning (disable:4996)

#include "HttpRequest.h"
#include "ErrorCode.h"
#include "Utility.h"
#include "Util.h"
#include <assert.h>
#include <vector>
#include <boost/algorithm/string.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("HttpRequest")
#endif

using namespace SD;

#define curl_easy_setopt_safe(opt, val)                            \
    if (curl_easy_setopt(curl_, opt, val) != CURLE_OK)           \
    {                                                              \
        return FAILED_TO_INITIALIZE_REQUEST;                       \
    }

#ifdef __cplusplus
extern "C" {
#endif
static size_t curlWriteFunc(void *ptr, size_t size, size_t nmemb,
                     void *data);

static size_t curlReadFunc(void *ptr, size_t size, size_t nmemb,
                    void *data);

static size_t curlHeaderFunc(void *ptr, size_t size, size_t nmemb,
                      void *data);

static void curlHeaderDone(void *data);

static int32_t curlSeekFunc(void *instream, curl_off_t offset, int32_t origin);
#ifdef __cplusplus
}
#endif


static std::map<std::string, std::string> initUnEncodeHeader();

static std::map<int32_t, int32_t> initHttpStatusAsErrorCodes();

static int32_t convertHttpRspCode(CURLcode curlCode, int32_t httpCode);

static bool isUnEncodeHeader(const std::string &strHeader);

static int32_t getHeadFromVector(const std::vector<std::string> &meta, std::string &strMetaKey, std::string &strMetaValue);

static const std::map<std::string, std::string> unEncodeHeaders = initUnEncodeHeader();

static const std::map<int32_t, int32_t> httpStatusAsErrorCodes = initHttpStatusAsErrorCodes();


HttpRequest::HttpRequest() 
	: httpMethod_(HttpMethod(HttpRequestTypeInvalid))
	,httpStatus_(0)
	,requestHeaders_(NULL)
	,curl_(NULL)
{
	curl_ = curl_easy_init();
}

HttpRequest::HttpRequest(HttpMethod method) 
    : httpMethod_(method)
	,httpStatus_(0)
	,requestHeaders_(NULL)
	,curl_(NULL)
{
	curl_ = curl_easy_init();
}

HttpRequest::~HttpRequest()
{
    if (requestHeaders_ != NULL)
    {
        curl_slist_free_all(requestHeaders_);
        requestHeaders_ = NULL;
    }

    if (curl_ != NULL)
    {
        curl_easy_cleanup(curl_);
        curl_ = NULL;
    }
}

HttpRequest::HttpRequest(const HttpRequest &rhs)
{
	httpMethod_ = rhs.httpMethod_;
	httpStatus_ = rhs.httpStatus_;
	requestHeaders_ = rhs.requestHeaders_;
	curl_ = rhs.curl_;
}

HttpRequest& HttpRequest::operator=(const HttpRequest &rhs)
{
	if (&rhs != this)
	{
		httpMethod_ = rhs.httpMethod_;
		httpStatus_ = rhs.httpStatus_;
		requestHeaders_ = rhs.requestHeaders_;
		curl_ = rhs.curl_;
	}
	return (*this);
}

int32_t HttpRequest::setUri(const std::string &strEndpoint, const std::string  &strUri)
{
	if(0==strUri.find("http://")||0==strUri.find("https://"))
	{
		uri_ = strUri;
	}
	else
	{
		uri_ = strEndpoint + strUri;
	}

	return RT_OK;
}

void HttpRequest::setHttpMethod(const HttpMethod& method)
{
	httpMethod_ = method;
}

std::map<std::string, std::string>& HttpRequest::getResponseHeaders() 
{
	return responseHeaders_;
}

int32_t HttpRequest::requestPerform(RequestParam &param, Configure &config, 
							  const std::map<std::string, std::string> &mapHeaders)
{
    int32_t ret = 0;
    if (NULL == curl_)
    {
        return FAILED_TO_INITIALIZE_REQUEST;
    }

    ret = setupCurl(param, config);

    if (ret != RT_OK)
    {
        //curl_easy_cleanup(curl_);
        //curl_ = NULL;
        return ret;
    }

    ret = setHeaders(mapHeaders);

    if (ret != RT_OK)
    {
        curl_slist_free_all(requestHeaders_);
        requestHeaders_ = NULL;
        //curl_easy_cleanup(curl_);
        //curl_ = NULL;
        return ret;
    }

	CURLcode code;
	code = curl_easy_perform(curl_);

    curlHeaderDone(&param);

    int32_t iHttpRspCode = 0;
    curl_easy_getinfo(curl_, CURLINFO_RESPONSE_CODE, 
        &iHttpRspCode);

    if ((code != CURLE_OK))
    {
        curl_slist_free_all(requestHeaders_);
        requestHeaders_ = NULL;
        //curl_easy_cleanup(curl_);
        //curl_ = NULL;
		ret = convertHttpRspCode(code, iHttpRspCode);
		SERVICE_ERROR(MODULE_NAME, ret, "request url: %s", uri_.c_str());
        return ret;
    }

    ret = parseHeaders(&param);

    if (requestHeaders_ != NULL)
    {
        curl_slist_free_all(requestHeaders_);
        requestHeaders_ = NULL;
    }

    //curl_easy_cleanup(curl_);
    //curl_ = NULL;

    httpStatus_ = convertHttpRspCode(code, iHttpRspCode);
    ret = (RT_OK != httpStatus_) ? httpStatus_ : ret;
    return ret;
}

int32_t HttpRequest::setupCurl(RequestParam &param, Configure &config)
{
    curl_easy_setopt_safe(CURLOPT_URL, uri_.c_str());

    curl_easy_setopt_safe(CURLOPT_NOSIGNAL, 1);

    curl_easy_setopt_safe(CURLOPT_NOPROGRESS, 1);

    curl_easy_setopt_safe(CURLOPT_TCP_NODELAY, 1);

    curl_easy_setopt_safe(CURLOPT_NETRC, CURL_NETRC_IGNORED);

    curl_easy_setopt_safe(CURLOPT_SSL_VERIFYPEER, 0L);
    curl_easy_setopt_safe(CURLOPT_SSL_VERIFYHOST, 0L); 

    curl_easy_setopt_safe(CURLOPT_FOLLOWLOCATION, 1);

    curl_easy_setopt_safe(CURLOPT_MAXREDIRS, 10);

    if (HttpRequestTypePUT == httpMethod_)
    {
        curl_easy_setopt_safe(CURLOPT_PUT, 1);
        curl_easy_setopt_safe(CURLOPT_UPLOAD, 1);
    }
    else if (HttpRequestTypePOST == httpMethod_)
    {
        curl_easy_setopt_safe(CURLOPT_POST, 1);
        curl_easy_setopt_safe(CURLOPT_COPYPOSTFIELDS, param.readData->pBuf);
    }
    else if (HttpRequestTypeGET == httpMethod_)
    {
        curl_easy_setopt_safe(CURLOPT_HTTPGET, 1);
    }
    else if (HttpRequestTypeDELETE == httpMethod_)
    {
        curl_easy_setopt_safe(CURLOPT_CUSTOMREQUEST, "DELETE");
    }
    else if (HttpRequestTypeHEAD == httpMethod_)
    {
        curl_easy_setopt_safe(CURLOPT_CUSTOMREQUEST, "HEAD");
    }

    if (param.readData != NULL)
    {
        curl_easy_setopt_safe(CURLOPT_INFILESIZE_LARGE, param.readData->lBufLen);
    }
    else
    {
        curl_easy_setopt_safe(CURLOPT_INFILESIZE, 0L);
    }

    curl_easy_setopt_safe(CURLOPT_READDATA, &param);
    curl_easy_setopt_safe(CURLOPT_READFUNCTION, curlReadFunc);

    curl_easy_setopt_safe(CURLOPT_WRITEDATA, &param);
    curl_easy_setopt_safe(CURLOPT_WRITEFUNCTION, curlWriteFunc);

    curl_easy_setopt_safe(CURLOPT_SEEKDATA, &param);
    curl_easy_setopt_safe(CURLOPT_SEEKFUNCTION, curlSeekFunc);

    if (param.responseHeader != NULL)
    {
        curl_easy_setopt_safe(CURLOPT_HEADERDATA, &param);
        curl_easy_setopt_safe(CURLOPT_HEADERFUNCTION, curlHeaderFunc);
    }
	
    if (config.requestTimeout())
    {
        curl_easy_setopt_safe(CURLOPT_TIMEOUT, config.requestTimeout() * 10);
        curl_easy_setopt_safe(CURLOPT_CONNECTTIMEOUT, config.requestTimeout());
    }
	
    if (config.proxyInfo().useProxy)
    {
        curl_easy_setopt_safe(CURLOPT_PROXYAUTH, CURLAUTH_ANY/*CURLAUTH_BASIC*/);
        curl_easy_setopt_safe(CURLOPT_PROXY, (Utility::String::wstring_to_utf8(config.proxyInfo().proxyServer) + ":" + 
			Utility::String::wstring_to_utf8(Utility::String::format_string(L"%d", config.proxyInfo().proxyPort))).c_str());
        curl_easy_setopt_safe(CURLOPT_PROXYTYPE, CURLPROXY_HTTP);
		if (config.proxyInfo().useProxyAthen)
		{
			curl_easy_setopt_safe(CURLOPT_PROXYUSERPWD, 
				(Utility::String::wstring_to_utf8(config.proxyInfo().proxyUserName) + ":" + 
				Utility::String::wstring_to_utf8(config.proxyInfo().proxyPassword)).c_str());
		}
    }

    //在WRITEFUNCTION中不接收头
    curl_easy_setopt_safe(CURLOPT_HEADER, 0L);

#ifdef _DEBUG
    //curl_easy_setopt_safe(CURLOPT_VERBOSE, 1L);
#endif
    return 0;
}

int32_t HttpRequest::setHeaders(const std::map<std::string, std::string> & mapHeaders)
{
	std::map<std::string, std::string>::const_iterator beg_iter = mapHeaders.begin();

	for ( ; beg_iter != mapHeaders.end(); beg_iter++)
	{
		std::string strHeader;
		std::string stHeaderKey;
		std::string strHeaderValue;

		if (isUnEncodeHeader((*beg_iter).first))
		{
			stHeaderKey = (*beg_iter).first;
			strHeaderValue = (*beg_iter).second;
		}
		else
		{
			stHeaderKey = urlEncode((*beg_iter).first);
			strHeaderValue = urlEncode((*beg_iter).second);
		}
		strHeader = stHeaderKey + ":" + strHeaderValue;
		requestHeaders_ = curl_slist_append(requestHeaders_, strHeader.c_str());
	}

	if (requestHeaders_ != NULL)
	{
		curl_easy_setopt_safe(CURLOPT_HTTPHEADER, requestHeaders_);
	}

    return RT_OK;
}

#ifdef __cplusplus
extern "C" {
#endif
static size_t curlWriteFunc(void *ptr, size_t size, size_t nmemb,
                     void *data)
{
    assert(data != NULL);
    assert(ptr != NULL);

    //curlHeaderDone(data);

    if (NULL == ptr || NULL == data)
    {
        return 0;
    }

    int32_t len = (int32_t)(size * nmemb);
    RequestParam *reqParam = (RequestParam *)data;

    if (NULL == reqParam->writeDataCallback)
    {
        return (uint32_t)(len);
    }

    int32_t dataLen = reqParam->writeDataCallback(ptr, (uint32_t)len, reqParam->writeData);
    return (uint32_t)((dataLen > 0) ? dataLen : 0);
}

static size_t curlReadFunc(void *ptr, size_t size, size_t nmemb,
                    void *data)
{
    assert(data != NULL);
    assert(ptr != NULL);

    //curlHeaderDone(data);

    if (NULL == ptr || NULL == data)
    {
        return 0;
    }

    size_t len = size * nmemb;
    RequestParam *reqParam = (RequestParam *)data;

    if (NULL == reqParam->readDataCallback)
    {
        return len;
    }

    int32_t dataLen = reqParam->readDataCallback(ptr, len, reqParam->readData);

    return (uint32_t)((dataLen > 0) ? dataLen : 0);
}

static size_t curlHeaderFunc(void *ptr, size_t size, size_t nmemb,
                      void *data)
{
    assert(data != NULL);
    assert(ptr != NULL);

    if (NULL == ptr || NULL == data)
    {
        return 0;
    }

    size_t len = size * nmemb;

    RequestParam *reqParam = (RequestParam *)data;

    if (NULL == reqParam->responseHeader)
    {
        return 0;
    }

    HeaderBuffer *header = (HeaderBuffer *)reqParam->responseHeader;

    //CURL在body传输完成后可能重复调用此函数
    if (header->done)
    {
        return len;
    }

    char * pcBuf = (char *)malloc(len + 1);
    if (NULL == pcBuf)
    {
        return 0;
    }

    memset(pcBuf, 0, len + 1);
    memcpy(pcBuf, ptr, len);

    header->strHeader += pcBuf;

    free(pcBuf);

    return len;
}

static void curlHeaderDone(void *data)
{
    assert(data != NULL);

    if (NULL == data)
    {
        return;
    }

    RequestParam *reqParam = (RequestParam *)data;

    if (NULL == reqParam->responseHeader)
    {
        return;
    }

    HeaderBuffer *header = (HeaderBuffer *)reqParam->responseHeader;

    if (header->done)
    {
        return;
    }

    header->done = true;
}

static int32_t curlSeekFunc(void *instream, curl_off_t offset, int32_t origin)
{
    assert(instream != NULL);
    if (NULL == instream)
    {
        return CURL_SEEKFUNC_CANTSEEK;
    }
    RequestParam *reqParam = (RequestParam *)instream;
    if (NULL == reqParam->readData)
    {
        return CURL_SEEKFUNC_CANTSEEK;
    }

    curl_off_t theOffset = reqParam->readData->lOffset;

    switch (origin)
    {
    case SEEK_SET:
                theOffset = offset;
                break;
    case SEEK_CUR:
                theOffset += offset;
                break;
    case SEEK_END:
               theOffset = reqParam->readData->lBufLen + offset;
                break;
    default:
        return CURL_SEEKFUNC_CANTSEEK;
    }

    if ((theOffset < 0) 
        || (theOffset > (curl_off_t)reqParam->readData->lBufLen))
    {
        return CURL_SEEKFUNC_FAIL;
    }
    reqParam->readData->lOffset = (uint32_t)theOffset;
    return CURL_SEEKFUNC_OK;
}
#ifdef __cplusplus
}
#endif

int32_t HttpRequest::parseHeaders(void *data)
{
    assert(data != NULL);

    int32_t ret = RT_OK;
    if (NULL == data)
    {
        return RT_INVALID_PARAM;
    }

    RequestParam *reqParam = (RequestParam *)data;

    //不接收响应头
    if (NULL == reqParam->responseHeader)
    {
        return RT_OK;
    }

    HeaderBuffer *header = (HeaderBuffer *)reqParam->responseHeader;

    if (!header->done)
    {
        return RT_OK;
    }

    responseHeaders_.clear();

    std::vector<std::string> vecHeaders;
	boost::split(vecHeaders, header->strHeader, boost::is_any_of("\r\n"), boost::token_compress_on);
    std::vector<std::string>::iterator beg = vecHeaders.begin();

    for (; beg != vecHeaders.end(); beg++)
    {
        std::string strMetaKey;
        std::string strMetaValue;
        std::vector<std::string> meta;
		boost::split(meta, *beg, boost::is_any_of(":"), boost::token_compress_on);

        if (meta.size() >= 2)
        {
            ret = getHeadFromVector(meta, strMetaKey, strMetaValue);
            if (RT_OK != ret)
            {
                break;
            }
            responseHeaders_[strMetaKey] = strMetaValue;
        }
    }
    return ret;
}

int32_t HttpRequest::setSpeedLimit(bool bUseSpeedLimit, int64_t ullMaxUpload, int64_t ullMaxDownload)
{
	if (bUseSpeedLimit)
	{
		setUploadSpeedLimit(ullMaxUpload);
		setDownloadSpeedLimit(ullMaxDownload);
	}

	return RT_OK;
}

int32_t HttpRequest::setUploadSpeedLimit(int64_t speed)
{
	if (speed > 0 && NULL != curl_)
	{
		curl_easy_setopt_safe(CURLOPT_MAX_SEND_SPEED_LARGE,(curl_off_t)speed);
	}
	return RT_OK;
}

int32_t HttpRequest::setDownloadSpeedLimit(int64_t speed)
{
	if (speed > 0 && NULL != curl_)
	{
		curl_easy_setopt_safe(CURLOPT_MAX_RECV_SPEED_LARGE,(curl_off_t)speed);
	}
	return RT_OK;
}

double HttpRequest::getUploadSpeed()
{
    double dUploadSpeed = 0;
	if (NULL != curl_)
	{
		curl_easy_getinfo(curl_, CURLINFO_SPEED_UPLOAD, &dUploadSpeed);
	}	
    return dUploadSpeed;
}

double HttpRequest::getDownloadSpeed()
{
	double dDownloadSpeed = 0;
	if (NULL != curl_)
	{
		curl_easy_getinfo(curl_, CURLINFO_SPEED_DOWNLOAD, &dDownloadSpeed);
	}
	return dDownloadSpeed;
}

std::map<std::string, std::string> initUnEncodeHeader()
{
	std::map<std::string, std::string> mapUnEcode;
	mapUnEcode[HEAD_CONTENT_TYPE]=HEAD_CONTENT_TYPE;
	mapUnEcode[HEAD_DATE]=HEAD_DATE;
	mapUnEcode[HEAD_AUTHORIZATION]=HEAD_AUTHORIZATION; 
	mapUnEcode[HEAD_CONTENT_LENGTH] = HEAD_CONTENT_LENGTH;
	mapUnEcode[HEAD_VARY] = HEAD_VARY;
	mapUnEcode[HEAD_TRANSFER_ENCODING] = HEAD_TRANSFER_ENCODING;
	mapUnEcode[HEAD_RANGE] = HEAD_RANGE;
	mapUnEcode[CONNECTION] = CONNECTION;
	return mapUnEcode;
}

std::map<int32_t, int32_t> initHttpStatusAsErrorCodes()
{
	std::map<int32_t, int32_t> mapCode;
	mapCode[100] = RT_OK;
	mapCode[200] = RT_OK;
	mapCode[201] = FILE_CREATED;
	mapCode[204] = RT_OK;
	mapCode[206] = RT_OK;

	mapCode[400] = HTTP_BAD_REQUEST;
	mapCode[401] = HTTP_UNAUTHORIZED;
	mapCode[403] = HTTP_FORBIDDEN;
	mapCode[404] = HTTP_NOT_FOUND;
	mapCode[405] = HTTP_NOT_ALLOWD;
	mapCode[409] = HTTP_CONFLICT;
	mapCode[412] = HTTP_PRECONDITION_FAILED;
	mapCode[417] = HTTP_EXCEPTATION_FAILED;
	mapCode[423] = HTTP_LOCKED;
	mapCode[500] = HTTP_INTERNAL_ERROR;
	mapCode[503] = HTTP_SERVICE_UNVAILABLE;
	mapCode[507] = HTTP_INSUFFICIENT_STORAGE;

	return mapCode;
}

int32_t convertHttpRspCode(CURLcode curlCode, int32_t httpCode)
{
	if (CURLE_OK == curlCode)
	{
		if (0 == httpCode)
		{
			return RT_OK; 
		}
		std::map<int32_t, int32_t>::const_iterator it = httpStatusAsErrorCodes.find(httpCode);
		if (it != httpStatusAsErrorCodes.end())
		{
			return it->second;
		}
		else
		{
			return httpCode;
		}
	}
	else
	{
		return (CURL_ERROR_CODE-curlCode);
	}
}

bool isUnEncodeHeader(const std::string &header)
{
	return (unEncodeHeaders.find(header) != unEncodeHeaders.end());
}

int32_t getHeadFromVector(const std::vector<std::string> &meta, std::string &metaKey, std::string &metaValue)
{
	const size_t imetaSize = meta.size();

	if (imetaSize < 2)
	{
		return RT_INVALID_PARAM;
	}

	if (isUnEncodeHeader(meta[0]))
	{
		metaKey = meta[0];
		for (size_t i = 1; i < imetaSize; i++)
		{
			metaValue += meta[i];
			metaValue += ((i + 1)< imetaSize) ? ":" : "";
		}
	}
	else
	{
		metaKey = meta[0];
		for (size_t i = 1; i < imetaSize; i++)
		{
			metaValue += meta[i];
			metaValue += ((i + 1)< imetaSize) ? ":" : "";
		}
	}

	return RT_OK;
}

//lint -e10 +e506
