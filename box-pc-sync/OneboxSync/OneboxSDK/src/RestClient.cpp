#include "RestClient.h"
#include "HttpCbFuns.h"
#include "Utility.h"
#include "Util.h"
#include <fstream>
#include <sstream>
#include "openssl/crypto.h"
#include <Windows.h>

#ifndef MODULE_NAME
#define MODULE_NAME "RestClient"
#endif

using namespace SD;

uint32_t RestClient::refCount_ = 0;
std::string RestClient::ufmUrl_ = "";
std::string RestClient::uamUrl_ = "";

static HANDLE *ssl_lock_handles = NULL;
static void ssl_lock_callback(int32_t mode, int32_t type, char *file, int32_t line);
static uint32_t ssl_thread_id_callback(void);
static int32_t ssl_init_locks();
static void ssl_release_locks();

void ssl_lock_callback(int32_t mode, int32_t type, char *file, int32_t line)
{
	(void)file;
	(void)line;

	if (NULL == ssl_lock_handles || NULL == ssl_lock_handles[type])
	{
		return;
	}

	if (CRYPTO_LOCK & mode)
	{
		WaitForSingleObject(ssl_lock_handles[type], INFINITE);
	}
	else
	{
		ReleaseMutex(ssl_lock_handles[type]);
	}
}

uint32_t ssl_thread_id_callback(void)
{
	return (uint32_t)GetCurrentThreadId();
}

int32_t ssl_init_locks()
{
	int32_t num_locks = CRYPTO_num_locks();
	ssl_lock_handles = (HANDLE*)OPENSSL_malloc(num_locks * sizeof(HANDLE));
	if (NULL == ssl_lock_handles)
	{
		return -1;
	}
	memset(ssl_lock_handles, 0, num_locks * sizeof(HANDLE));
	HANDLE handle = NULL;
	for (int32_t i = 0; i < num_locks; ++i)
	{
		handle = CreateMutex(NULL, FALSE, NULL);
		if (NULL == handle)
		{
			return -1;
		}
		ssl_lock_handles[i] = handle;
	}

	CRYPTO_set_id_callback((unsigned long (*)())ssl_thread_id_callback);
	CRYPTO_set_locking_callback((void (*)(int32_t, int32_t, const char*, int32_t))ssl_lock_callback);

	return 0;
}

void ssl_release_locks()
{
	if (NULL == ssl_lock_handles)
	{
		return;
	}
	CRYPTO_set_locking_callback(NULL);
	HANDLE handle = NULL;
	for (int32_t i = 0; i < CRYPTO_num_locks(); ++i)
	{
		handle = ssl_lock_handles[i];
		if (NULL != handle)
		{
			CloseHandle(handle);
		}
	}

	OPENSSL_free(ssl_lock_handles);
}

int32_t RestClient::initialize()
{   
    if (refCount_++) 
    {
        return RT_OK;
    }

	if (0 != ISSP_LogInit("./log4cpp.conf", TP_FILE, "./OneboxSDK.log"))
	{
		return -1;
	}

    if (curl_global_init(CURL_GLOBAL_ALL) != RT_OK) 
    {
        --refCount_;
        return -1;
    }

	if (RT_OK != ssl_init_locks())
	{
		--refCount_;
		return -1;
	}

    return RT_OK;
}

void RestClient::deinitialize()
{
    if (--refCount_)
    {
        return;
    }

	ssl_release_locks();
    
    curl_global_cleanup();

	ISSP_LogExit();
}

RestClient::RestClient(const TOKEN& token) 
	:token_(token)
	,errorCode_(RT_OK)
	,errorMessage_("")
	,requestTime_(0)
	,request_(HttpRequestTypeGET)
{
}

RestClient::RestClient(const TOKEN& token, const Configure& configure)
	:token_(token)
	,configure_(configure)
	,errorCode_(RT_OK)
	,errorMessage_("")
	,requestTime_(0)
	,request_(HttpRequestTypeGET)
{
}

int64_t RestClient::getUploadSpeed()
{
	return (int64_t)request_.getUploadSpeed();
}

int64_t RestClient::getDownloadSpeed()
{
	return (int64_t)request_.getDownloadSpeed();
}

int32_t RestClient::setUploadSpeedLimit(int64_t speed)
{
	return request_.setUploadSpeedLimit(speed);
}

int32_t RestClient::setDownloadSpeedLimit(int64_t speed)
{
	return request_.setDownloadSpeedLimit(speed);
}

int32_t RestClient::getErrorCode()
{
	boost::mutex::scoped_lock lock(mutex_);
	return errorCode_;
}

std::string RestClient::getErrorMsg()
{
	boost::mutex::scoped_lock lock(mutex_);
	return errorMessage_;
}

uint32_t RestClient::getRequstTime()
{
	boost::mutex::scoped_lock lock(mutex_);
	return requestTime_;
}

void RestClient::setToken(const TOKEN& token)
{
	token_ = token;
}

void RestClient::setConfigure(const Configure& configure)
{
	configure_ = configure;
}

int32_t RestClient::request(const std::map<std::string, std::string>& mapProperty,
                      const std::string & strUri,
					  const SERVICE_TYPE& type,
					  HttpRequest& request,
                      RequestParam& param,
					  bool ignoreRet)
{
	int32_t ret = RT_OK;
	std::string serverUrl = "";

	ret = getServerUrl(type, serverUrl);
	if (ret != RT_OK)
	{
		return ret;
	}

	request.setHttpMethod((HttpMethod)param.httpMethod);

	ret = request.setUri(serverUrl, strUri);
    if (ret != RT_OK)
    {
        return ret;
    }

    std::map<std::string, std::string> mapHeaders(mapProperty);
    mapHeaders[HEAD_DATE] = getDate();
    mapHeaders[HEAD_AUTHORIZATION] = token_.token;
	mapHeaders[HEAD_CONTENT_TYPE] = "application/json";

	//set callback for error msg
	DataBuffer writeBuf;
	if (NULL == param.writeDataCallback)
	{
		writeBuf.pBuf = (unsigned char *)::malloc(BODY_BUF_SIZE);
		if (NULL != writeBuf.pBuf)
		{
			writeBuf.lBufLen = BODY_BUF_SIZE;
			writeBuf.pFreeFunc = &::free;
			::memset(writeBuf.pBuf, 0, BODY_BUF_SIZE);
			param.writeData = &writeBuf;
			param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
		}		
	}

	uint32_t requestTime = GetTickCount();
	int32_t errorCode = request.requestPerform(param, configure_, mapHeaders);

	//print error msg
	if((RT_OK!=errorCode)&&(FILE_CREATED!=errorCode))
	{
		std::string jsonStr;
		if ((NULL != param.writeData->pBuf) && (0 < param.writeData->lOffset))
		{
			jsonStr.assign((char *)param.writeData->pBuf);
		}
		SERVICE_ERROR(MODULE_NAME, errorCode, "writeData:%s", jsonStr.c_str());
	}

	requestTime = GetTickCount() - requestTime;
	
	boost::mutex::scoped_lock lock(mutex_);
	requestTime_ = requestTime;
	if (!ignoreRet)
	{
		errorCode_ = errorCode;
	}

	return errorCode;
}

int32_t RestClient::login(const std::string& strUseName,
						const std::string& strPsd,
						const std::string& domain,
						LoginRespInfo& loginResp, 
						const std::string& clientSN,
						const std::string& clientOS,
						const std::string& clientName,
						const std::string& clientVersion,
						const std::string& clientType)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::login UseName:" + strUseName);
    int32_t ret = RT_OK;

    if (strUseName.empty() || strPsd.empty())
    {
        return RT_INVALID_PARAM;
    }

    HeaderBuffer rspHeader;
    rspHeader.strHeader = "";
    rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
    std::string strTmp = "/login";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	LoginReqInfo loginReq;
	loginReq.loginName = strUseName;
	loginReq.password = strPsd;
	loginReq.domain = domain;
    ret = JsonGeneration::genLoginInfo(loginReq, readBuf);

    RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
    param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
    param.writeData = &writeBuf;
    param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
    param.responseHeader = &rspHeader;

    std::map<std::string, std::string> mapHeaders;

	mapHeaders[HEAD_DEVICE_SN] = clientSN;
	mapHeaders[HEAD_DEVICE_OS] = clientOS;
	mapHeaders[HEAD_DEVICE_NAME] = clientName;
	mapHeaders[HEAD_DEVICE_TYPE] = clientType;
	mapHeaders[HEAD_DEVICE_VERSION] = clientVersion;

    ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_CLOUDAPP, request_, param);
	
	if (RT_OK == ret)
    {
		ret = JsonParser::parseLoginRespInfo(writeBuf, loginResp);
    }

    return ret;
}

int32_t RestClient::logout()
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::logout");
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::string strTmp = "/token";
	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;
	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UAM, request_, param);

	if (RT_OK != ret)
    {
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::logout ret:%d", ret);
	}

	return ret;
}

int32_t RestClient::refreshToken(LoginRespInfo& loginResp)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::refreshToken");
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::string strTmp = "/token";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UAM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseLoginRespInfo(writeBuf, loginResp);
	}

	return ret;
}

int32_t RestClient::checkHealthy()
{
	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	std::map<std::string, std::string> mapHeaders;

	std::wstring serverUrl = configure_.serverUrl();
	serverUrl = serverUrl.substr(0, serverUrl.length()-std::wstring(L"/api/v1").length());
	configure_.serverUrl(serverUrl);

	return this->request(mapHeaders, "/checkRealServerHealth.28055dab3fc0a85271dddbeb0464bfdb", SERVICE_CLOUDAPP, request_, param, true);
}

int32_t RestClient::listen(const int32_t syncVersion, 
						   int32_t& lastVersion)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listen syncVersion:%d", syncVersion));
	
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v1/event/listen?syncVersion={version_id}
	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/event/listen?syncVersion=" << syncVersion;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		std::string strLastVersion;
		strLastVersion.assign((char *)writeBuf.pBuf);
		std::stringstream stream;
		stream << strLastVersion;
		stream >> lastVersion;
	}
	
	return ret;
}

int32_t RestClient::createLdapUser(const std::string& loginName, 
								   int64_t& userId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::createLdapUser loginName:%s", loginName.c_str()));
	
	//POST /api/v2/users/ldapuser
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/users/ldapuser";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genLoginName(loginName, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);

	if (FILE_CREATED == ret)
	{
		ret = JsonParser::parseShareUserId(writeBuf, userId);
	}

	return ret;
}

int32_t RestClient::setShareRes(const int64_t& ownerId, 
								const int64_t& fileId, 
								const ShareNodeEx& shareNodeEx)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setShareRes file_id:%I64d", fileId));
	
	//PUT /api/v2/shareships/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/shareships/"<< ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genInviteShares(shareNodeEx, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::setShareRes id:%I64d", fileId);
	}

	return ret;
}

//int32_t RestClient::delShareRes(const ShareNode& srcSharesNode)
//{
//	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareRes id:%I64d", srcSharesNode.id()));
//
//	//DELETE /api/v2/shareships/{ownerId}/{nodeId}?userId={userId}&type={type}
//	HeaderBuffer rspHeader;
//	rspHeader.strHeader = "";
//	rspHeader.done = false;
//
//	//HttpRequest request(HttpRequestTypeDELETE);
//	std::ostringstream ostr;
//	ostr <<"/shareships/" << srcSharesNode.ownerId() << srcSharesNode.inodeId() 
//		<< "?userId=" << srcSharesNode.sharedUserId() << "&type=" << srcSharesNode.sharedUserType();
//
//	//Malloc_Buffer writeBuf(BODY_BUF_SIZE);
//
//	RequestParam param;
//	param.httpMethod = HttpRequestTypeDELETE;
//	param.responseHeader = &rspHeader;
//
//	std::map<std::string, std::string> mapHeaders;
//
//	int32_t ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
//	if (RT_OK != ret)
//	{
//		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareRes id:%I64d, ret:%d", srcSharesNode.inodeId(), ret);
//	}
//
//	return ret;
//}

int32_t RestClient::delShareResOwner(const int64_t& ownerId, 
									 const int64_t& fileId, 
									 const ShareNodeEx& shareNodeEx)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareResOwner id:%I64d", fileId));

	//DELETE /api/v2/shareships/{ownerId}/{nodeId}?userId={userId}&type={type}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::stringstream strTmp;
	strTmp << "/shareships/" << ownerId << "/" << fileId;
	if(INVALID_ID!=shareNodeEx.sharedUserId())
	{
		strTmp << "?userId=" << shareNodeEx.sharedUserId();
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareResOwner id:%I64d", fileId);
	}

	return ret;
}

int32_t RestClient::quitShared(const int64_t& fileId, 
							   const int64_t& ownerId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::quitShared file_id:%I64d", fileId));

	// DELETE /api/v1/share/reject/{ownerId}/{inodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::stringstream strTmp;
	strTmp << "/share/reject/" << ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);

	return ret;
}

int32_t RestClient::listDomainUsers(const std::string& keyWord, 
									ShareUserInfoList& shareUserInfos)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::listDomainUsers" + keyWord);

	if (keyWord.empty())
	{
		return RT_INVALID_PARAM;
	}

	// POST /api/v2/users/search
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::string strTmp = "/users/search";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListDomainUserParam(keyWord, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareUserInfoList(writeBuf, shareUserInfos);
	}

	return ret;
}

int32_t RestClient::getShareLink(const int64_t& fileId, 
								 const int64_t& ownerId, 
								 ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getShareLink id:%I64d", fileId));

	//GET /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/links/" << ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, ostr.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareLinkNode(writeBuf, shareLinkNode);
	}
	
	return ret;
}

int32_t RestClient::addShareLink(const int64_t& fileId, 
								 const int64_t& ownerId, 
								 ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::addShareLink file_id:%I64d", fileId));

	//POST /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::stringstream strTmp;
	strTmp << "/links/" << ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode;
	int32_t ret = JsonGeneration::genShareLinkNode(tmpShareLinkNode, readBuf);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::modifyShareLink genShareLinkNode, ret:%d", ret);
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
	if (FILE_CREATED == ret)
	{
		ret = JsonParser::parseShareLinkNode(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::modifyShareLink(const int64_t& fileId, 
									const int64_t& ownerId, 
									const ShareLinkNodeEx& shareLinkNodeEx, 
									ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::modifyShareLink file_id:%I64d", fileId));

	//PUT /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);
	std::stringstream strTmp;
	strTmp << "/links/" << ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode = convertShareLinkNodeExToShareLinkNode(shareLinkNodeEx);
	int32_t ret = JsonGeneration::genShareLinkNode(tmpShareLinkNode, readBuf);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::modifyShareLink genShareLinkNode, ret:%d", ret);
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareLinkNode(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::delShareLink(const int64_t& fileId, 
								 const int64_t& ownerId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareLink id:%I64d", fileId));

	//DELETE /api/v2/link/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::stringstream strTmp;
	strTmp << "/links/" << ownerId << "/" << fileId;

	//Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;

	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
	
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareLink id:%I64d, ret:%d", fileId, ret);
	}

	return ret;
}

int32_t RestClient::sendShareLinkByEmail(const int64_t& fileId, 
										 const int64_t& ownerId, 
										 const std::string& linkUrl, 
										 EmailList& emails)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::sendShareLinkByEmail file_id:%I64d", fileId));
	
	if (linkUrl.empty())
	{
		return RT_INVALID_PARAM;
	}

	// PUT /api/v1/link/{ownerId}/{iNodeId}/sendemail
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);
	std::stringstream strTmp;
	strTmp << "/link/" << ownerId << "/" << fileId << "/sendemail";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genSendShareLinkByEmailParam(linkUrl, emails, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);

	return ret;
}

int32_t RestClient::getServerSysConfig(ServerSysConfig& serverSysConfig, 
									   const OPTION_TYPE& option)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getServerSysConfig");
	std::string option_str = "";

	//GET /api/v2/config?option={option}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	switch (option)
	{
	case OPTION_LINK_ACCESS_KEY_RULE:
		option_str = "linkAccessKeyRule";
		return RT_OK;
	case OPTION_SYSTEM_MAX_VERSIONS:
		option_str = "systemMaxVersions";
		break;
	case OPTION_ALL:
		option_str = "all";
		break;;
	default:
		return RT_INVALID_PARAM;
	}

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr <<"/config?option=" << option_str;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseServerSysConfig(writeBuf, serverSysConfig);
	}

	return ret;
}

int32_t RestClient::getIncSyncPeriod(int64_t& incSyncPeriod)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getIncSyncPeriod");
	//GET /api/v2/config?option={option}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr <<"/config?option=incSyncPeriod";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseIncSyncPeriod(writeBuf, incSyncPeriod);
	}

	return ret;
}

int32_t RestClient::getUpdateInfo(UpdateInfo& updateInfo)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getUpdateInfo");
	//POST /api/v2/client/info
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::string strTmp = "/client/info";

	DataBuffer readBuf;
	{		
		Json::StyledWriter writer;
		Json::Value reqRoot(Json::objectValue);
		reqRoot["clientType"] = "pc";
		std::string loginJsonStr = writer.write(reqRoot);
		string2Buff(loginJsonStr, readBuf);
	}

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_CLOUDAPP, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseUpdateInfo(writeBuf, updateInfo);
	}

	return ret;
}

int32_t RestClient::downloadClient(const std::string& downloadUrl, 
								   const std::string location)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::downloadClient");

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
	
	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr <<"/client/download?type=" << "pc";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);
	if (RT_OK == ret)
	{
		try
		{
			std::fstream fs(location.c_str(), std::ios::out|std::ios::binary);
			fs.write((char*)writeBuf.pBuf, writeBuf.lOffset);
			fs.close();
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "download client failed, can not write file to location");
			return RT_FILE_WRITE_ERROR;
		}
	}
	else
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::downloadClient");
	}

	return ret;
}

int32_t RestClient::getFileInfoByShareLink(ShareLinkNode& linknode, 
										   FileItem& fileitem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getFileInfoByShareLink");

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/links/node
	//CSRequest request(HttpRequestTypeGET);
	std::string strTmp = "/links/node";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseFileInfoByShareLink(writeBuf, linknode, fileitem);
	}

	return ret;
}

int32_t RestClient::createShareLink(const int64_t& fileId, 
									const int64_t& ownerId, 
									const std::string& access, 
									const ShareLinkNodeEx& shareLinkNodeEx, 
									ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getShareLink file_id:%I64d", fileId));

	if (access.empty())
	{
		return RT_INVALID_PARAM;
	}

	//GET /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/links/" << ownerId << "/" << fileId;

	DataBuffer readBuf;
	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	int ret = JsonGeneration::genCreateShareLink(access, shareLinkNodeEx, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareLinkNode(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::listShareRes(const int64_t& ownerId, 
								 const int64_t& fileId, 
								 ShareNodeList& shareNodes, 
								 int32_t& nextOffset, 
								 const int32_t offset, 
								 const int32_t limit)
{
	int32_t ret = RT_OK;
	int32_t totalCount = 0;

	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listShareRes file_id:%I64d", fileId));

	if (offset < 0 || limit < 0)
	{
		return RT_INVALID_PARAM;
	}

	//GET /sharedrive/api/v2/shareships/{ownerId}/{nodeId}?offset={offset}&limit={limit}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/shareships/"<< ownerId << "/" << fileId << "?offset=" << offset << "&limit=" <<limit;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareNodeList(writeBuf, shareNodes, totalCount);
	}

	nextOffset = nextOffset + limit;
	if(totalCount <= nextOffset)
	{
		nextOffset = 0;
	}
	return ret;
}

int32_t RestClient::getServerUrl(const SERVICE_TYPE& type, 
								 std::string& serverurl)
{
	boost::mutex::scoped_lock lock(mutex_);
	serverurl = Utility::String::wstring_to_utf8(configure_.serverUrl());
	int ret = RT_OK;
	std::string server_type = "", tmpUrl = "";

	//SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::getServerUrl:");

	if ((SERVICE_CLOUDAPP > type) || (type > SERVICE_END))
	{
		return RT_INVALID_PARAM;
	}

	switch (type)
	{
		case SERVICE_CLOUDAPP:
			server_type = "cloudapp";
			return RT_OK;
		case SERVICE_UAM:
			server_type = "uam";
			tmpUrl = uamUrl_;
			break;
		case SERVICE_UFM:
			server_type = "ufm";
			tmpUrl = ufmUrl_;
			break;
		default:
			return RT_INVALID_PARAM;
	}

	if (tmpUrl.empty())
	{
		//GET /api/v2/serverurl?type={type}
		HeaderBuffer rspHeader;
		rspHeader.strHeader = "";
		rspHeader.done = false;

		//CSRequest request(HttpRequestTypePUT);
		std::ostringstream ostr;
		ostr << "/serverurl?type="<< server_type;

		Malloc_Buffer writeBuf(BODY_BUF_SIZE);

		RequestParam param;
		param.httpMethod = HttpRequestTypeGET;
		param.writeData = &writeBuf;
		param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
		param.responseHeader = &rspHeader;

		std::map<std::string, std::string> mapHeaders;

		lock.unlock();
		ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);
		if (RT_OK != ret)
		{
			SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getServerUrl type:%s", server_type.c_str());
		}
		else
		{
			// trim the left '/'
			serverurl = serverurl.substr(serverurl.length()-sizeof(L"/api/v1")/sizeof(wchar_t)+2);
			ret = JsonParser::parseServerUrl(writeBuf, serverurl);
			lock.lock();
			switch (type)
			{
			case SERVICE_UAM:
				uamUrl_ = serverurl;
				break;
			case SERVICE_UFM:
				ufmUrl_ = serverurl;
				break;
			default:
				return RT_INVALID_PARAM;
			}
		}
	}
	else
	{
		serverurl = tmpUrl;
	}

	return ret;
}

int32_t RestClient::listAllTeamspaces(TeamspaceNodes& teamspaceNodes, 
					   int64_t& totalCount, 
					   const std::string& keyword, 
					   const int64_t offset, 
					   const int32_t limit, 
					   const std::string& orderField, 
					   const std::string& orderDirection)
{
	//POST /api/v2/teamspaces/all
	std::string strTmp = "/teamspaces/all";
	
	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;
	
	int32_t ret = JsonGeneration::genListTeamspacesParam(keyword, limit, offset, orderField, orderDirection, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}
	
	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;
	
	std::map<std::string, std::string> mapHeaders;
	
	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to list all teamspcaes.");
		return ret;
	}
	
	return JsonParser::parseTeamspaces(writeBuf, teamspaceNodes, totalCount);
}

int32_t RestClient::listTeamspacesByUser(TeamspaceMemberships& teamspaceMemberships, 
							 int64_t& totalCount, 
							 const int64_t userId, 
							 const int64_t offset, 
							 const int32_t limit, 
							 const std::string& orderField, 
							 const std::string& orderDirection)
{
	if (userId < 0)
	{
		return RT_INVALID_PARAM;
	}
	//POST /api/v2/teamspaces/items
	std::string strTmp = "/teamspaces/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListTeamspaceByUserParam(userId, limit, offset, orderField, orderDirection, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to list teamspcaes by user id, user id %I64d.", userId);
		return ret;
	}

	return JsonParser::parseTeamspaceMemberships(writeBuf, teamspaceMemberships, totalCount);
}

int32_t RestClient::createTeamspace(TeamspaceNode& teamspaceNode, 
						const std::string& name, 
						const std::string& description, 
						const int64_t spaceQuota, 
						const TeamspaceStatus status, 
						const int32_t maxVersion, 
						const int32_t maxMembers, 
						const int32_t regionId)
{
	if (name.empty())
	{
		return RT_INVALID_PARAM;
	}
	//POST /api/v2/teamspaces
	std::string strTmp = "/teamspaces";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genCreateOrUpdateTeamspaceParam(name, description, spaceQuota, status, maxVersion, maxMembers, regionId, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to create teamspcae, name %s.", name.c_str());
		return ret;
	}

	return JsonParser::parseTeamspace(writeBuf, teamspaceNode);
}

int32_t RestClient::updateTeamspace(TeamspaceNode& teamspaceNode, 
						const int64_t teamspaceId, 
						const std::string& name, 
						const std::string& description, 
						const int64_t spaceQuota, 
						const TeamspaceStatus status, 
						const int32_t maxVersion, 
						const int32_t maxMembers, 
						const int32_t regionId)
{
	if (name.empty() || teamspaceId < 0)
	{
		return RT_INVALID_PARAM;
	}
	//PUT /api/v2/teamspaces/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d", teamspaceId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genCreateOrUpdateTeamspaceParam(name, description, spaceQuota, status, maxVersion, maxMembers, regionId, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to update teamspcae, id: %I64d, name: %s.", 
			teamspaceId, name.c_str());
		return ret;
	}

	return JsonParser::parseTeamspace(writeBuf, teamspaceNode);
}

int32_t RestClient::setTeamspaceExtAttribute(const int64_t teamspaceId, 
								 const std::string& key, 
								 const std::string& value)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestClient::getTeamspaceExtAttribute(std::string& value, 
								 const int64_t teamspaceId, 
								 const std::string& key)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestClient::getTeamspace(TeamspaceNode& teamspaceNode, 
					 const int64_t teamspaceId)
{
	//GET /api/v2/teamspaces/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d", teamspaceId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.readData = NULL;
	param.readDataCallback = NULL;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to list teamspcaes.");
		return ret;
	}

	return JsonParser::parseTeamspace(writeBuf, teamspaceNode);
}

int32_t RestClient::deleteTeamspace(const int64_t teamspaceId)
{
	//DELETE /api/v2/teamspaces/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d", teamspaceId);

	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.readData = NULL;
	param.readDataCallback = NULL;
	param.writeData = NULL;
	param.writeDataCallback = NULL;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to delete teamspcae, id %I64d.", teamspaceId);
		return ret;
	}

	return ret;
}

int32_t RestClient::addTeamspaceMember(TeamspaceMembership& teamspaceMembership, 
						   const int64_t teamspaceId, 
						   const TeamspaceRoleType teamRole, 
						   const std::string& role, 
						   const int64_t memberId, 
						   const TeamspaceMemberType memberType)
{
	if (teamspaceId < 0)
	{
		return RT_INVALID_PARAM;
	}
	//POST /api/v2/teamspaces/{teamId}/memberships
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d/memberships", teamspaceId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genAddTeamspaceMemberParam(teamRole, role, memberId, memberType, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, 
			"failed to add teamspace member, teamspace id %I64d, member id %I64d, member type %s.", 
			teamspaceId, memberId, convertTeamspaceMemberType(memberType).c_str());
		return ret;
	}

	return JsonParser::parseTeamspaceMembership(writeBuf, teamspaceMembership);
}

int32_t RestClient::getTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
							   const int64_t teamspaceId, 
							   const int64_t teamspaceMemebershiId)
{
	if (teamspaceId < 0 || teamspaceMemebershiId < 0)
	{
		return RT_INVALID_PARAM;
	}
	//GET /api/v2/teamspaces/{teamId}/memberships/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d/memberships/%I64d", teamspaceId, teamspaceMemebershiId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.readData = NULL;
	param.readDataCallback = NULL;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, 
			"failed to get teamspace membership, teamspace id %I64d, membership id %I64d.", 
			teamspaceId, teamspaceMemebershiId);
		return ret;
	}

	return JsonParser::parseTeamspaceMembership(writeBuf, teamspaceMembership);
}

int32_t RestClient::updateTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
								  const int64_t teamspaceId, 
								  const int64_t teamspaceMemebershiId, 
								  const TeamspaceRoleType teamRole, 
								  const std::string& role)
{
	if (teamspaceId < 0 || teamspaceMemebershiId < 0)
	{
		return RT_INVALID_PARAM;
	}
	//PUT /api/v2/teamspaces/{teamId}/memberships/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d/memberships/%I64d", teamspaceId, teamspaceMemebershiId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genUpdateTeamspaceMemberParam(teamRole, role, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, 
			"failed to update teamspace membership, teamspace id %I64d, membership id %I64d, team role type %s, role %s.", 
			teamspaceId, teamspaceMemebershiId, convertTeamspaceRoleType(teamRole).c_str(), role.c_str());
		return ret;
	}

	return JsonParser::parseTeamspaceMembership(writeBuf, teamspaceMembership);
}

int32_t RestClient::listTeamspaceMembers(TeamspaceMemberships& teamspaceMemberships, 
							 int64_t& totalCount, 
							 const int64_t teamspaceId, 
							 const std::string& keyword, 
							 const TeamspaceRoleType roleType, 
							 const int64_t offset, 
							 const int32_t limit, 
							 const std::string& orderField, 
							 const std::string& orderDirection)
{
	//POST /api/v2/teamspaces/{teamId}/memberships/items
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d/memberships/items", teamspaceId);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListTeamspacesMembersParam(keyword, roleType, limit, offset, orderField, orderDirection, readBuf);
	if (RT_OK != ret)
	{
		return FAILED_TO_BUILDJSON;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to list teamspcae members, teamspace id %I64d.", teamspaceId);
		return ret;
	}

	return JsonParser::parseTeamspaceMemberships(writeBuf, teamspaceMemberships, totalCount);
}

int32_t RestClient::deleteTeamspaceMember(const int64_t teamspaceId, 
							  const int64_t teamspaceMemebershiId)
{
	//DELETE /api/v2/teamspaces/{teamId}/memberships/{id}
	std::string strTmp = Utility::String::format_string("/teamspaces/%I64d/memberships/%I64d", teamspaceId, teamspaceMemebershiId);

	HeaderBuffer rspHeader;	
	rspHeader.strHeader = "";
	rspHeader.done = false;	

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.readData = NULL;
	param.readDataCallback = NULL;
	param.writeData = NULL;
	param.writeDataCallback = NULL;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "failed to delete teamspcae member, teamspace id %I64d, membership id %I64d.", 
			teamspaceId, teamspaceMemebershiId);
		return ret;
	}

	return ret;
}
