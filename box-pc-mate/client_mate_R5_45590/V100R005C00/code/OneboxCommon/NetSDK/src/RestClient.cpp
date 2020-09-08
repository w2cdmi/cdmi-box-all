#include "RestClient.h"
#include "ErrorCode.h"
#include "HttpCbFuns.h"
#include "JsonParser.h"
#include "JsonGeneration.h"
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
	SERVICE_INFO(MODULE_NAME, RT_OK, "num_locks:%d", num_locks);
	ssl_lock_handles = (HANDLE*)OPENSSL_malloc(num_locks * sizeof(HANDLE));
	if (NULL == ssl_lock_handles)
	{
		return -1;
	}
	(void)memset_s(ssl_lock_handles, num_locks * sizeof(HANDLE), 0, num_locks * sizeof(HANDLE));
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

	std::wstring logFile = Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"OneboxSDK.log";
	if (0 != ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"),
		TP_FILE,
		Utility::String::wstring_to_string(logFile)))
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

/*****************************************************************************************
Function Name : request
Description   : 构造并发送http请求
Input         : mapProperty	请求头
strUri		请求URI
Output        : request		请求
param		请求参数
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
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

	request.setUri(serverUrl, strUri);

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
			(void)memset_s(writeBuf.pBuf, BODY_BUF_SIZE, 0, BODY_BUF_SIZE);
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
		//{"code":"SecurityMatrixForbidden","message":"The operation is prohibited by security matrix.","requestId":"szxcdac16-ap.site-tomcatThreadPool-1341-1433750292702000001","type":"error"}
		if(HTTP_FORBIDDEN==errorCode)
		{
			errorCode = (std::string::npos!=jsonStr.find("SecurityMatrixForbidden"))?SECURITY_MATRIX_FORBIDDEN:HTTP_FORBIDDEN;
		}
		//{"code":"Unauthorized","requestID":"CTU1000010191-http-apr-8080-exec-4-1434613954290000001","message":"Authentication fails, the token illegal or invalid.","type":"error"}
		if (HTTP_UNAUTHORIZED==errorCode)
		{
			errorCode = (std::string::npos!=jsonStr.find("Unauthorized"))?ACCOUNT_DISABLE:HTTP_UNAUTHORIZED;
		}

		if(HTTP_PRECONDITION_FAILED==errorCode)
		{
			errorCode = getHttpPerconditionErrorCode(jsonStr);
		}
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

/*****************************************************************************************
Function Name : login
Description   : 登录
Input         : strUseName 用户名 strPsd 用户密码 strAppId 应用ID
Output        : response 登录响应
Return        : 成功 RT_OK 失败 RT_INVALID_PARAM
Modification  :
Others        :
*******************************************************************************************/
int32_t RestClient::login(const std::string& strDomain,
						  const std::string& strUsername,
						  const std::string& strPsd,
						  LoginRespInfo& loginResp, 
						  const std::string& clientSN,
						  const std::string& clientOS,
						  const std::string& clientName,
						  const std::string& clientVersion,
						  const std::string& clientType)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::login");
	int32_t ret = RT_OK;

	if (strUsername.empty() || strPsd.empty())
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
	loginReq.loginName = strUsername;
	loginReq.password = strPsd;
	if(!strDomain.empty())
	{
		loginReq.domain = strDomain;
	}
	JsonGeneration::genLoginInfo(loginReq, readBuf);

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
		std::map<std::string,std::string> mapResponseHeader = request_.getResponseHeaders();
		if(mapResponseHeader.find(HEAD_NETWORK_TYPE) != mapResponseHeader.end())
		{
			std::string networkType = mapResponseHeader.find(HEAD_NETWORK_TYPE)->second;
			loginResp.networkType = SD::Utility::String::string_to_type<int32_t>(networkType);
		}
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

int32_t RestClient::listen(int32_t syncVersion, int32_t& lastVersion)
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

int32_t RestClient::createLdapUser(const std::string& loginName, int64_t& userId)
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

int32_t RestClient::setShareRes(const int64_t& ownerId, const int64_t& fileId, ShareNodeEx& shareNodeEx)
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

int32_t RestClient::setShareResV2(const int64_t& ownerId, const int64_t& fileId, ShareNodeEx& shareNodeEx)
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

	int32_t ret = JsonGeneration::genInviteSharesV2(shareNodeEx, readBuf);

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

int32_t RestClient::delShareResOwner(const int64_t& ownerId, const int64_t& file_id, ShareNodeEx& shareNodeEx)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareResOwner id:%I64d", file_id));

	//DELETE /api/v2/shareships/{ownerId}/{nodeId}?userId={userId}&type={type}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::stringstream strTmp;
	strTmp << "/shareships/" << ownerId << "/" << file_id;
	if(INVALID_ID!=shareNodeEx.sharedUserId())
	{
		strTmp << "?userId=" << shareNodeEx.sharedUserId() << "&type=" << shareNodeEx.sharedUserType();
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareResOwner id:%I64d", file_id);
	}

	return ret;
}

int32_t RestClient::quitShared(const int64_t& file_id, const int64_t& shareOwnerId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::quitShared file_id:%I64d", file_id));

	// DELETE /api/v1/share/reject/{ownerId}/{inodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::stringstream strTmp;
	strTmp << "/share/reject/" << shareOwnerId << "/" << file_id;

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

int32_t RestClient::listDomainUsers(const std::string& keyWord, ShareUserInfoList& shareUserInfos, int32_t limit)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::listDomainUsers " + keyWord);

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

	int32_t ret = JsonGeneration::genListDomainUserParam(keyWord, limit, readBuf);

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

int32_t RestClient::getShareLink(const int64_t& file_id, const int64_t& owner_id, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getShareLink id:%I64d", file_id));

	//GET /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/links/" << owner_id << "/" << file_id;

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

int32_t RestClient::addShareLink(const int64_t& file_id, const int64_t& owner_id, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::addShareLink file_id:%I64d", file_id));

	//POST /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode;
	int32_t ret = JsonGeneration::genShareLinkNode(tmpShareLinkNode, readBuf);

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

int32_t RestClient::addShareLink(const int64_t& file_id, const int64_t& owner_id, const std::string& accessMode, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::addShareLink file_id:%I64d", file_id));

	//POST /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode;
	tmpShareLinkNode.accesCodeMode(accessMode);
	int32_t ret = JsonGeneration::genShareLinkNodeV2(tmpShareLinkNode, readBuf);

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
		ret = JsonParser::parseShareLinkNodeV2(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::modifyShareLink(const int64_t& file_id, const int64_t& owner_id, const ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::modifyShareLink file_id:%I64d", file_id));

	//PUT /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);
	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode = convertShareLinkNodeExToShareLinkNode(shareLinkNodeEx);
	int32_t ret = JsonGeneration::genShareLinkNode(tmpShareLinkNode, readBuf);

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

int32_t RestClient::delShareLink(const int64_t& file_id, const int64_t& owner_id)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareLink id:%I64d", file_id));

	//DELETE /api/v2/link/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id;

	//Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;

	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareLink id:%I64d, ret:%d", file_id, ret);
	}

	return ret;
}


int32_t RestClient::getShareLink(const int64_t& file_id, const int64_t& owner_id, const std::string& linkCode, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getShareLink id:%I64d", file_id));

	//GET /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/links/" << owner_id << "/" << file_id << "?linkCode=" << linkCode;

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
		ret = JsonParser::parseShareLinkNodeV2(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::listShareLinkByFile(const int64_t& file_id, const int64_t& owner_id, int64_t& count, ShareLinkNodeList& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listShareLinkByFile id:%I64d", file_id));

	//GET /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/nodes/" << owner_id << "/" << file_id << "/links";

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
		ret = JsonParser::parseShareLinkNodeList(writeBuf, shareLinkNode, count);
	}

	return ret;
}

int32_t RestClient::listFilesHadShareLink(const int64_t& owner_id, const std::string& keyword, const PageParam& pageparam, int64_t& count, MyShareNodeList& nodes)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listFilesHadShareLink owner_id:%I64d", owner_id));

	//GET /api/v2/links/items
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/links/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListFilesHadShareLink(owner_id, keyword, pageparam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str().c_str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseFilesHadShareLink(writeBuf, nodes, count);
	}

	return ret;
}

//int32_t RestClient::addShareLink(const int64_t& file_id, const int64_t& owner_id, ShareLinkNode& shareLinkNode)
//{
//	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::addShareLink file_id:%I64d", file_id));
//
//	//POST /api/v2/links/{ownerId}/{nodeId}
//	HeaderBuffer rspHeader;
//	rspHeader.strHeader = "";
//	rspHeader.done = false;
//
//	std::stringstream strTmp;
//	strTmp << "/links/" << owner_id << "/" << file_id;
//
//	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
//	DataBuffer readBuf;
//
//	ShareLinkNode tmpShareLinkNode;
//	int32_t ret = JsonGeneration::genShareLinkNode(tmpShareLinkNode, readBuf);
//	if (RT_OK != ret)
//	{
//		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::addShareLink genShareLinkNode, ret:%d", ret);
//		return ret;
//	}
//
//	RequestParam param;
//	param.httpMethod = HttpRequestTypePOST;
//	param.readData = &readBuf;
//	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
//	param.writeData = &writeBuf;
//	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
//	param.responseHeader = &rspHeader;
//
//	std::map<std::string, std::string> mapHeaders;
//
//	ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);
//	if (FILE_CREATED == ret)
//	{
//		ret = JsonParser::parseShareLinkNode(writeBuf, shareLinkNode);
//	}
//
//	return ret;
//}

int32_t RestClient::modifyShareLink(const int64_t& file_id, const int64_t& owner_id, const std::string& linkCode, const ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::modifyShareLink file_id:%I64d", file_id));

	//PUT /api/v2/links/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);
	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id << "?linkCode=" << linkCode;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ShareLinkNode tmpShareLinkNode = convertShareLinkNodeExToShareLinkNode(shareLinkNodeEx);
	int32_t ret = JsonGeneration::genShareLinkNodeV2(tmpShareLinkNode, readBuf);

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
		ret = JsonParser::parseShareLinkNodeV2(writeBuf, shareLinkNode);
	}

	return ret;
}

int32_t RestClient::delShareLink(const int64_t& file_id, const int64_t& owner_id, const std::string& linkCode, const std::string& type)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::delShareLink id:%I64d", file_id));

	//DELETE /api/v2/link/{ownerId}/{nodeId}
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeDELETE);
	std::stringstream strTmp;
	strTmp << "/links/" << owner_id << "/" << file_id << "?linkCode=" << linkCode << "&type=" << type;

	//Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;

	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, strTmp.str().c_str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::delShareLink id:%I64d, ret:%d", file_id, ret);
	}

	return ret;
}

int32_t RestClient::sendShareLinkByEmail(const int64_t& file_id, const int64_t& owner_id, const std::string& linkUrl, EmailList& emails)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::sendShareLinkByEmail file_id:%I64d", file_id));

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
	strTmp << "/link/" << owner_id << "/" << file_id << "/sendemail";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genSendShareLinkByEmailParam(linkUrl, emails, readBuf);

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

int32_t RestClient::getServerSysConfig(ServerSysConfig& serverSysConfig, const OPTION_TYPE& option)
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
	case OPTION_SYSTEM_ALLOWED_BACKUP:
		option_str = "backupAllowedRule";
		break;
	case OPTION_SYSTEM_FORBIDDEN_BACKUP:
		option_str = "backupForbiddenRule";
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
		reqRoot["clientType"] = "pccloud";
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

	if (-1404==ret||-1400==ret)
	{
		int resulterrorcode=0;
		if (!errorCodeDispatch(resulterrorcode,ret,writeBuf))
		{
			ret=resulterrorcode;
		}

	}

	return ret;
}

int32_t RestClient::downloadClient(const std::string& downloadUrl, const std::string location)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::downloadClient");

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr <<"/client/download?type=" << "pccloud";

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


int32_t RestClient::getFeatureCode(const std::string& clientType, const std::string& version, std::string& featureCode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::getFeatureCode");

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr <<"/client/featurecode/" <<clientType<<"/"<<version;

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
		ret = JsonParser::parseFeatureCode(writeBuf, featureCode);
	}
	else
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getFeatureCode");
	}

	return ret;
}

int32_t RestClient::getFileInfoByShareLink(ShareLinkNode& linknode, FileItem& fileitem)
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

int32_t RestClient::createShareLink(const int64_t& fileId, const int64_t& ownerId, 
									const std::string& access, const ShareLinkNodeEx& shareLinkNodeEx, ShareLinkNode& shareLinkNode)
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

int32_t RestClient::listShareRes(const int64_t& ownerId, const int64_t& fileId, ShareNodeList& shareNodes, int64_t& nextOffset, const int64_t offset, const int32_t limit)
{
	int32_t ret = RT_OK;
	int64_t total_cnt = 0;

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
		ret = JsonParser::parseShareNodeList(writeBuf, shareNodes, total_cnt);
	}

	nextOffset = nextOffset + limit;
	if(total_cnt <= nextOffset)
	{
		nextOffset = 0;
	}
	return ret;
}

int32_t RestClient::getServerUrl(const SERVICE_TYPE& type, std::string& serverurl)
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

int32_t RestClient::listReceiveShareRes(const std::string& keyword, const PageParam& pageparam, int64_t& count, ShareNodeList& shareNodes)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listReceiveShareRes keyword:%s", keyword.c_str()));

	//POST /api/v2/shares/received
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/shares/received";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListShareParam(keyword, pageparam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseShareNodeList(writeBuf, shareNodes, count);
	}

	return ret;
}

int32_t RestClient::listDistributeShareRes(const std::string& keyword, const PageParam& pageparam, int64_t& count, MyShareNodeList& shareNodes)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listReceiveShareRes"));

	//POST /api/v2/shares/distributed
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/shares/distributed";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	int32_t ret = JsonGeneration::genListShareParam(keyword, pageparam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseMyShareNodeList(writeBuf, shareNodes, count);
	}

	return ret;
}

int32_t RestClient::getMailInfo(const int64_t ownerId, const int64_t fileId, std::string source, EmailInfoNode& emailInfoNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getMailInfo"));
	int32_t ret = RT_OK;

	//GET /api/v2/mailmsgs/ownerId/fileId?source=source
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);
	std::ostringstream ostr;
	ostr << "/mailmsgs/" << ownerId << "/" << fileId << "?source=" << source;

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
		ret = JsonParser::parseEmailInfoNode(writeBuf, emailInfoNode);
	}

	return ret;
}

int32_t RestClient::setMailInfo(const int64_t ownerId, const int64_t fileId, EmailInfoNode& emailInfoNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setMailInfo"));
	int32_t ret = RT_OK;

	//POST /api/v2/mailmsgs/ownerId/fileId
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/mailmsgs/" << ownerId << "/" << fileId;
	std::string tmpStr = ostr.str();
	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	JsonGeneration::genSetEmailInfo(emailInfoNode, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseEmailInfoNode(writeBuf, emailInfoNode);
	}

	return ret;
}

int32_t RestClient::listGroups(const std::string& keyword, const std::string& type, const PageParam& pageparam, int64_t& count, GroupNodeList& nodes)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listGroups"));
	int32_t ret = RT_OK;

	//POST /api/v2/groups/all
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/groups/all";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	JsonGeneration::genListGroups(keyword, type, pageparam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseGroupNode(writeBuf, nodes, count);
	}

	return ret;
}

int32_t RestClient::addRestTask(int64_t srcOwnerId, const std::list<int64_t>& srcNodeId,
								int64_t destOwnerId, int64_t destFolderId,
								const std::string& type, bool autoRename, std::string& taskId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::addRestTask %s", type.c_str()));
	int32_t ret = RT_OK;

	//PUT /api/v2/tasks/nodes
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/tasks/nodes";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	JsonGeneration::genTaskInfo(srcOwnerId, srcNodeId, destOwnerId, destFolderId, type, autoRename, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	//成功返回201
	if (FILE_CREATED == ret)
	{
		ret = JsonParser::parseTaskInfo(writeBuf, taskId);
	}

	return ret;
}

int32_t RestClient::queryTaskStatus(const std::string& taskId, std::string& taskStatus)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::queryTaskStatus taskId:%s", taskId.c_str()));
	int32_t ret = RT_OK;

	//GET /api/v2/tasks/nodes/taskId
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/tasks/nodes/" << taskId;

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
		ret = JsonParser::parseTaskStatus(writeBuf, taskStatus);
	}

	return ret;
}

int32_t RestClient::listSystemRole(SysRoleInfoExList& nodes)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listSystemRole"));
	int32_t ret = RT_OK;

	//POST /api/v2/roles
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/roles";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseSysRoleInfo(writeBuf, nodes);
	}

	return ret;
}

int32_t RestClient::getDeclaration(const std::string& clientType, DeclarationInfo& declarationInfo)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getDeclaration: clientType %s", clientType.c_str()));
	if (clientType.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;	
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/declaration/"<<clientType;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseDeclarationInfo(writeBuf, declarationInfo);
	}
	else
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getDeclaration");
	}

	return ret;
}

int32_t RestClient::signDeclaration(const std::string& declarationID, std::string isSign)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getDeclaration: declarationID %s", declarationID.c_str()));
	if (declarationID.empty())
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;	
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/declaration/sign";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	{		
		Json::StyledWriter writer;
		Json::Value reqRoot(Json::objectValue);
		reqRoot["id"] = declarationID;
		std::string loginJsonStr = writer.write(reqRoot);
		string2Buff(loginJsonStr, readBuf);
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseSignDeclaration(writeBuf, isSign);
	}
	else
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::signDeclaration");
	}

	return ret;
}