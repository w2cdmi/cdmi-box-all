#include "UserInfoMgrImpl.h"
#include "SmartHandle.h"
#include <windows.h>
#include <winhttp.h>
#include <map>
#define SECURITY_WIN32
#include <security.h>

#pragma comment(lib, "winhttp.lib")
#pragma comment(lib, "Secur32.lib")

struct WinHttpParam
{
	std::wstring szServer;
	std::wstring szPath;
	std::wstring szProxyServer;
	WORD wPort;
	BOOL fUseSSL;
	BOOL fUseProxy;
	std::wstring szProxyUsername;
	std::wstring szProxyPassword;
};

static std::wstring GetDomainName()
{
	wchar_t szBuffer[MAX_PATH] = {0};
	ULONG len = MAX_PATH;
	if (0 == GetUserNameEx(NameSamCompatible, szBuffer, &len))
	{
		HSLOG_ERROR(MODULE_NAME, GetLastError(), "WinHttpAddRequestHeaders failed.");
		return L"";
	}
	std::wstring domainName=std::wstring(szBuffer);
	std::wstring::size_type pos = domainName.find_last_of(L"\\");
	if (std::wstring::npos != pos &&  (0 != pos))
	{
		domainName=domainName.substr(pos+1);
	}
	
	return domainName;
}

static int32_t ParseUrl(const std::wstring& inUrl, std::wstring& outServerName, URL_COMPONENTS& urlComp)
{
	if(!WinHttpCrackUrl(inUrl.c_str(), (DWORD)inUrl.length(), 0, &urlComp))
	{
		return GetLastError();
	}

	std::wstring wstrTemp = Utility::String::rtrim(urlComp.lpszHostName, urlComp.lpszUrlPath);
	std::wstring::size_type nLenth = wstrTemp.find(L":");

	if( nLenth !=wstrTemp.npos )
	{
		outServerName=  wstrTemp.substr(0,nLenth);
	}
	else
	{
		outServerName = wstrTemp;
	}

	return RT_OK;
}

static BOOL MakeRequestHeaders(HINTERNET hRequest, 
							   const std::wstring& sn, 
							   const std::wstring& osName, 
							   const std::wstring& pcName, 
							   const std::wstring& version)
{
	std::wstring strReqHeaders = L"";
	std::map<std::wstring, std::wstring> reqHeaders;
	reqHeaders[L"deviceType"] = Utility::String::format_string(L"%d", 1);
	reqHeaders[L"deviceSN"] = sn;
	reqHeaders[L"deviceOS"] = osName;
	reqHeaders[L"deviceName"] = pcName;
	reqHeaders[L"deviceAgent"] = version;

// 	reqHeaders[L"deviceType"] = Utility::String::format_string(L"%d", 1);
// 	reqHeaders[L"deviceSN"] = L"UzJBM0QwMzUwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA=";
// 	reqHeaders[L"deviceOS"] = L"windows.7.sp1.32bit";
// 	reqHeaders[L"deviceName"] = L"x90006438_ne12";
// 	reqHeaders[L"deviceAgent"] = L"1.2.3.3823";

// 	reqHeaders[L"deviceType"] = Utility::String::format_string(L"%d", 1);
// 	reqHeaders[L"deviceSN"] = L"UzJBM0QwMzUwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA=";
// 	reqHeaders[L"deviceOS"] = L"windows.7.sp1.32bit";
// 	reqHeaders[L"deviceName"] = L"1234";
// 	reqHeaders[L"deviceAgent"] = L"1.2.3.3823";

	for (std::map<std::wstring, std::wstring>::iterator it = reqHeaders.begin(); 
		it != reqHeaders.end(); ++it)
	{
		if (it->second.empty())
		{
			continue;
		}
		strReqHeaders = it->first + L":" + it->second;
		//strReqHeaders = Utility::String::replace_all(strReqHeaders, L" ", L"%20");
		if (!WinHttpAddRequestHeaders(
			hRequest, 
			strReqHeaders.c_str(), 
			-1,
			WINHTTP_ADDREQ_FLAG_REPLACE | WINHTTP_ADDREQ_FLAG_ADD))
		{
			HSLOG_ERROR(MODULE_NAME, GetLastError(), "WinHttpAddRequestHeaders failed.");
			return FALSE;
		}
	}

	return TRUE;
}

DWORD ChooseAuthScheme( DWORD dwSupportedSchemes )
{
	//  It is the server's responsibility only to accept 
	//  authentication schemes that provide a sufficient
	//  level of security to protect the servers resources.
	//
	//  The client is also obligated only to use an authentication
	//  scheme that adequately protects its username and password.
	//
	//  Thus, this sample code does not use Basic authentication  
	//  becaus Basic authentication exposes the client's username
	//  and password to anyone monitoring the connection.

	if ( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_BASIC )
		return WINHTTP_AUTH_SCHEME_BASIC;
	if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_NEGOTIATE )
		return WINHTTP_AUTH_SCHEME_NEGOTIATE;
	else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_NTLM )
		return WINHTTP_AUTH_SCHEME_NTLM;
	else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_PASSPORT )
		return WINHTTP_AUTH_SCHEME_PASSPORT;
	else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_DIGEST )
		return WINHTTP_AUTH_SCHEME_DIGEST;
	else
		return 0;
}

static int32_t MakeWinHttpParam(struct WinHttpParam& param, Configure* configure)
{
	DWORD dwRet = RT_OK;

	URL_COMPONENTS urlComp;
	ZeroMemory(&urlComp, sizeof(urlComp));
	urlComp.dwStructSize = sizeof(urlComp);
	urlComp.dwSchemeLength    = (DWORD)-1;
	urlComp.dwHostNameLength  = (DWORD)-1;
	urlComp.dwUrlPathLength   = (DWORD)-1;
	urlComp.dwExtraInfoLength = (DWORD)-1;

	std::wstring strServerUrl, strServerName;
	strServerUrl = configure->serverUrl();
	strServerUrl = strServerUrl.substr(0, strServerUrl.length()-std::wstring(L"/api/v1").length());

	if (RT_OK != ParseUrl(strServerUrl, strServerName, urlComp))
	{
		dwRet = RT_INVALID_PARAM;
		HSLOG_ERROR(MODULE_NAME, dwRet, "ParseUrl of %s failed.", 
			Utility::String::wstring_to_string(strServerUrl).c_str());
		return dwRet;
	}

	ProxyInfo proxyInfo = configure->proxyInfo();

	param.szServer = strServerName;
	param.szPath = urlComp.lpszUrlPath;
	param.szProxyServer = proxyInfo.proxyServer + L":" + Utility::String::format_string(L"%d", proxyInfo.proxyPort);
	param.wPort = urlComp.nPort;
	param.fUseSSL = configure->useSSL();
	param.fUseProxy = proxyInfo.useProxy;
	param.szProxyUsername = proxyInfo.proxyUserName;
	param.szProxyPassword = proxyInfo.proxyPassword;

	return dwRet;
}

int32_t UserInfoMgrImpl::domainAuthen()
{
	boost::mutex::scoped_lock lock(mutex_);

	WinHttpParam winHttpParam;
	if (RT_OK != MakeWinHttpParam(winHttpParam, userContext_->getConfigureMgr()->getConfigure()))
	{
		HSLOG_ERROR(MODULE_NAME, RT_ERROR, "MakeWinHttpParam failed.");
		return RT_ERROR;
	}

	DWORD dwRet = RT_OK;
	DWORD dwStatusCode = 0;
	DWORD dwSupportedSchemes;
	DWORD dwFirstScheme;
	DWORD dwSelectedScheme;
	DWORD dwTarget;
	DWORD dwLastStatus = 0;
	DWORD dwFlag = 0;
	DWORD dwSize = sizeof(DWORD);
	BOOL bResults = FALSE;
	BOOL bDone = FALSE;

	DWORD dwProxyAuthScheme = 0;
	SmartWinHttpHandle hSession = NULL, 
		hConnect = NULL, 
		hRequest = NULL;

	DWORD dwProxyAccessType = winHttpParam.fUseProxy ? WINHTTP_ACCESS_TYPE_NAMED_PROXY : WINHTTP_ACCESS_TYPE_NO_PROXY;
	LPCWSTR szProxyName = winHttpParam.fUseProxy ? winHttpParam.szProxyServer.c_str() : WINHTTP_NO_PROXY_NAME;

	dwRet = getDeviceInfo();
	if (RT_OK != dwRet)
	{
		return dwRet;
	}

	// Use WinHttpOpen to obtain a session handle.
	hSession = WinHttpOpen( L"WinHTTP",  
		dwProxyAccessType,
		szProxyName, 
		WINHTTP_NO_PROXY_BYPASS, 0 );

	if( NULL == hSession )
	{
		dwRet = GetLastError();
		HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpOpen failed.");
		return dwRet;
	}

	// Specify an HTTP server.
	hConnect = WinHttpConnect( hSession, 
		winHttpParam.szServer.c_str(), 
		winHttpParam.wPort, 0 );

	if( NULL == hConnect )
	{
		dwRet = GetLastError();
		HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpConnect failed.");
		return dwRet;
	}

	// Create an HTTP request handle.
	hRequest = WinHttpOpenRequest( hConnect, 
		L"GET", 
		winHttpParam.szPath.c_str(),
		NULL, 
		WINHTTP_NO_REFERER, 
		WINHTTP_DEFAULT_ACCEPT_TYPES,
		( winHttpParam.fUseSSL ) ? WINHTTP_FLAG_SECURE : 0 );

	if ( NULL == hRequest )
	{
		dwRet = GetLastError();
		HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpOpenRequest failed.");
		return dwRet;
	}

	if (winHttpParam.fUseSSL && 
		!WinHttpQueryOption(hRequest, 
		WINHTTP_OPTION_SECURITY_FLAGS, 
		(LPVOID)&dwFlag, 
		&dwSize))
	{
		dwRet = GetLastError();
		HSLOG_ERROR(MODULE_NAME, dwRet, "SSL WinHttpQueryOption failed.");
		return dwRet;
	}
	if (winHttpParam.fUseSSL)
	{
		dwFlag |= SECURITY_FLAG_IGNORE_UNKNOWN_CA;     
		dwFlag |= SECURITY_FLAG_IGNORE_CERT_DATE_INVALID;     
		dwFlag |= SECURITY_FLAG_IGNORE_CERT_CN_INVALID;
	}
	if (winHttpParam.fUseSSL && 
		!WinHttpSetOption(hRequest, 
		WINHTTP_OPTION_SECURITY_FLAGS, 
		&dwFlag, 
		sizeof(DWORD)))
	{
		dwRet = GetLastError();
		HSLOG_ERROR(MODULE_NAME, dwRet, "SSL WinHttpSetOption failed.");
		return dwRet;
	}
	
	// Continue to send a request until status code is not 401 or 407.
	while( !bDone )
	{
		//  If a proxy authentication challenge was responded to, reset
		//  those credentials before each SendRequest, because the proxy  
		//  may require re-authentication after responding to a 401 or  
		//  to a redirect. If you don't, you can get into a 
		//  407-401-407-401- loop.

		if( dwProxyAuthScheme != 0 && 
			!WinHttpSetCredentials( hRequest, 
			WINHTTP_AUTH_TARGET_PROXY, 
			dwProxyAuthScheme, 
			winHttpParam.szProxyUsername.c_str(),
			winHttpParam.szProxyPassword.c_str(),
			NULL ) )
		{
			dwRet = GetLastError();
			HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpSetCredentials failed.");
			return dwRet;
		}

		// Make request headers
		if (!MakeRequestHeaders(hRequest, 
			deviceId_, 
			osVersion_, 
			hostName_, 
			userContext_->getConfigureMgr()->getConfigure()->version()))
		{
			dwRet = GetLastError();
			HSLOG_ERROR(MODULE_NAME, dwRet, "MakeRequestHeaders failed.");
			return dwRet;
		}

		// Send a request.
		if ( !WinHttpSendRequest( hRequest, 
			WINHTTP_NO_ADDITIONAL_HEADERS,
			0,
			WINHTTP_NO_REQUEST_DATA,
			0, 
			0, 
			0 ) )
		{
			dwRet = GetLastError();
			HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpSendRequest failed.");
			return dwRet;
		}

		// End the request.
		if ( !WinHttpReceiveResponse( hRequest, NULL ) )
		{
			dwRet = GetLastError();
			// Resend the request in case of ERROR_WINHTTP_RESEND_REQUEST error.
			if ( ERROR_WINHTTP_RESEND_REQUEST == dwRet )
			{
				continue;
			}
			HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpReceiveResponse failed.");
			return dwRet;
		}

		// Check the status code.
		if ( !WinHttpQueryHeaders( hRequest, 
			WINHTTP_QUERY_STATUS_CODE |
			WINHTTP_QUERY_FLAG_NUMBER,
			NULL, 
			&dwStatusCode, 
			&dwSize, 
			NULL ) )
		{
			dwRet = GetLastError();
			HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpQueryHeaders failed.");
			return dwRet;
		}

		switch( dwStatusCode )
		{
		case 200: 
			{
				// The resource was successfully retrieved.
				// You can use WinHttpReadData to read the 
				// contents of the server's response.
				if (!WinHttpQueryDataAvailable(hRequest, &dwSize))
				{
					dwRet = GetLastError();
					HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpQueryDataAvailable failed.");
					return dwRet;
				}

				Malloc_Buffer bufferRead(dwSize+1);
				DWORD dwReadLen = 0;
				if (!WinHttpReadData( hRequest, (LPVOID)bufferRead.pBuf, dwSize, &dwReadLen))
				{
					dwRet = GetLastError();
					HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpReadData failed.");
					return dwRet;
				}

				// parse the response
				bufferRead.lOffset = dwReadLen;
				LoginRespInfo authenRsp;
				if (RT_OK != JsonParser::parseLoginRespInfoEx(bufferRead, authenRsp))
				{
					dwRet = RT_ERROR;
					HSLOG_ERROR(MODULE_NAME, dwRet, "parseLoginRespInfo failed.");
					return dwRet;
				}

				if (!authenRsp.isValid())
				{
					HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "authen response information is invalid.");
					return RT_INVALID_PARAM;
				}

				// in domain authen respond, token expire interval is milisecond
				// but the parseAuthenRsp use second, should convert
				authenRsp.expiredAt = authenRsp.expiredAt/1000;
				//userName_ = GetDomainName();
				account_ = Utility::String::utf8_to_wstring(authenRsp.login_name);
				dwRet = parseAuthenRsp(authenRsp);
				if (RT_OK != dwRet)
				{
					return dwRet;
				}

				bDone = TRUE;
			}
			break;

		case 401:
			// The server requires authentication.
			// Obtain the supported and preferred schemes.
			if ( !WinHttpQueryAuthSchemes( hRequest, 
				&dwSupportedSchemes, 
				&dwFirstScheme, 
				&dwTarget ) )
			{
				dwRet = GetLastError();
				HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpQueryAuthSchemes failed.");
				return dwRet;
			}

			// Set the credentials before resending the request.
			dwSelectedScheme = ChooseAuthScheme( dwSupportedSchemes);

			if( dwSelectedScheme == 0 )
				bDone = TRUE;
			else
			{
				dwFlag = WINHTTP_AUTOLOGON_SECURITY_LEVEL_LOW;
				if ( !WinHttpSetOption(
					hRequest, 
					WINHTTP_OPTION_AUTOLOGON_POLICY, 
					&dwFlag, 
					sizeof(DWORD) ) )
				{
					dwRet = GetLastError();
					HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpSetOption failed.");
					return dwRet;
				}
			}

			// If the same credentials are requested twice, abort the
			// request.  For simplicity, this sample does not check
			// for a repeated sequence of status codes.
			if( dwLastStatus == 401 )
				bDone = TRUE;

			break;

		case 407:
			// The proxy requires authentication.
			// Obtain the supported and preferred schemes.
			if ( !WinHttpQueryAuthSchemes( hRequest, 
				&dwSupportedSchemes, 
				&dwFirstScheme, 
				&dwTarget ) )
			{
				dwRet = GetLastError();
				HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpQueryAuthSchemes failed.");
				return dwRet;
			}

			// Set the credentials before resending the request.
			dwProxyAuthScheme = ChooseAuthScheme(dwSupportedSchemes);

			// If the same credentials are requested twice, abort the
			// request.  For simplicity, this sample does not check 
			// for a repeated sequence of status codes.
			if( dwLastStatus == 407 )
				bDone = TRUE;
			break;

		default:
			// The status code does not indicate success.
			HSLOG_ERROR(MODULE_NAME, dwStatusCode, "Error occur.");
			dwRet = dwStatusCode;
			bDone = TRUE;

			// parse the error message
			if (!WinHttpQueryDataAvailable(hRequest, &dwSize))
			{
				dwRet = GetLastError();
				HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpQueryDataAvailable failed.");
				return dwRet;
			}

			Malloc_Buffer bufferRead(dwSize+1);
			DWORD dwReadLen = 0;
			if (!WinHttpReadData( hRequest, (LPVOID)bufferRead.pBuf, dwSize, &dwReadLen))
			{
				dwRet = GetLastError();
				HSLOG_ERROR(MODULE_NAME, dwRet, "WinHttpReadData failed.");
				return dwRet;
			}
			HSLOG_ERROR(MODULE_NAME, dwRet, "%s", bufferRead.pBuf);
		}

		// Keep track of the last status code.
		dwLastStatus = dwStatusCode;
	}

	return dwRet;
}
