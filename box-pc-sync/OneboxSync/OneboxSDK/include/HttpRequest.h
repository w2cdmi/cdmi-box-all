#ifndef __ONEBOX__REQUEST__H__
#define __ONEBOX__REQUEST__H__

#include "OneboxExport.h"
#include "CommonDefine.h"
#include "CommonValue.h"
#include "curl.h"
#include "Configure.h"
#include <map>

class ONEBOX_DLL_EXPORT HttpRequest
{
public:
	HttpRequest();

    HttpRequest(HttpMethod method);

	HttpRequest(const HttpRequest &rhs);

	HttpRequest &operator=(const HttpRequest &rhs);

    virtual ~HttpRequest();

public:
    int32_t setUri(const std::string &strEndpoint, const std::string  &strUri);

	void setHttpMethod(const HttpMethod& method);
    
	std::map<std::string, std::string> &getResponseHeaders();

	int32_t requestPerform(RequestParam &param, Configure &config, const std::map<std::string, std::string> &mapHeaders);

public:
	int32_t setSpeedLimit(bool bUseSpeedLimit, int64_t ullMaxUpload, int64_t ullMaxDownload);

	int32_t setUploadSpeedLimit(int64_t speed);

	int32_t setDownloadSpeedLimit(int64_t speed);

    double getUploadSpeed();

    double getDownloadSpeed();

private:
    int32_t setupCurl(RequestParam &param, Configure &config);

    int32_t setHeaders(const std::map<std::string, std::string> &mapHeaders);

    int32_t parseHeaders(void *data);

private:
    HttpMethod httpMethod_;

    int32_t httpStatus_;

    std::string httpStatusStr_;

    // The HTTP headers to use for the curl request
    struct curl_slist *requestHeaders_;

    // The CURL structure driving the request
    CURL *curl_;

    // LIBCURL requires that the uri be stored outside of the curl handle
    std::string uri_;

    std::map<std::string, std::string> responseHeaders_;
};

#endif // end of __ONEBOX__REQUEST__H__
