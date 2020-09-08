package pw.cdmi.box.disk.user.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.huawei.common.ntlmv2.liferay.util.HttpHeaders;

import jcifs.util.Base64;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.client.domain.user.RestUserloginRsp;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.event.domain.EventType;
import pw.cdmi.box.disk.httpclient.rest.UAMRestClientService;
import pw.cdmi.box.disk.httpclient.rest.request.*;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.user.shiro.UsernamePasswordCaptchaToken;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.useragent.Browser;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.common.useragent.Version;
import pw.cdmi.core.JsonMapper;
import pw.cdmi.core.exception.*;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class UserLoginServiceImpl extends UAMRestClientService implements UserLoginService
{
    
    private static Logger logger = LoggerFactory.getLogger(UserLoginServiceImpl.class);
    
    private static final String NTLM_HEARDER = "NTLM";
    
    private static final int NTLM_HEARDER_LENGTH = 5;
    
    private static final int NTLM_MSG_CHALLENGE = 8;
    
    private static final String BROWSER_IE_NAME = "Internet Explorer";
    
    private static final int BROWSER_VERSION_8 = 8;
    
    public static final String BROWSER_VERSION_TIPS_PATH = "/browserVersionTips";
    
    private JsonMapper jsonMapper = new JsonMapper();
    
    @Autowired
    private UserService userService;
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private SecurityService securityService;
    
    /**
     * 
     * @param request
     * @param response
     * @return
     */
    @Override
    public boolean ntlmAuthen(HttpServletRequest request, HttpServletResponse response, String basePath)
    {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String regionIp = request.getHeader(Constants.HTTP_X_REGION_IP);
        
        String sessionId = request.getSession().getId();
        try
        {
            if (authorization == null || !authorization.startsWith(NTLM_HEARDER))
            {
                sendAuthenticateResponse(response, NTLM_HEARDER);
                return false;
            }
            byte[] src = Base64.decode(authorization.substring(NTLM_HEARDER_LENGTH));
            if (src[NTLM_MSG_CHALLENGE] == 1)
            {
                return returnNTLMChallengeSuccess(request, response, authorization, sessionId);
            }
            NtlmGetChallengeRequest ntlmGetChallengeRequest = new NtlmGetChallengeRequest();
            ntlmGetChallengeRequest.setKey(sessionId);
            ntlmGetChallengeRequest.setChallenge(authorization);
            ntlmGetChallengeRequest.setAppId(authAppService.getCurrentAppId());
            Map<String, String> headers = new HashMap<String, String>(3);
            String deviceType = request.getHeader("deviceType");
            headers.put("x-device-type", deviceType == null ? UserServiceImpl.transDeviceStrType(0)
                : UserServiceImpl.transDeviceStrType(Integer.parseInt(deviceType)));
            headers.put("x-real-ip", RequestUtils.getRealIP(request));
            headers.put("x-request-ip", RequestUtils.getRealIP(request));
            headers.put("x-proxy-ip", RequestUtils.getProxyIP(request));
            String deviceSN = "";
            String deviceOS = "";
            String deviceName = "";
            String deviceAgent = "";
            String deviceAddress = RequestUtils.getRealIP(request);
            String proxyAddress = RequestUtils.getProxyIP(request);
            if (null != deviceType && "1".equals(deviceType))
            {
                deviceSN = request.getHeader("deviceSN");
                deviceOS = request.getHeader("deviceOS");
                deviceName = request.getHeader("deviceName");
                deviceAgent = request.getHeader("deviceAgent");
            }
            else
            {
                UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                deviceAgent = userAgent.getBrowser().getName();
                deviceOS = userAgent.getOperatingSystem().getName();
            }
            headers.put("x-device-sn", deviceSN);
            headers.put("x-device-os", deviceOS);
            headers.put("x-device-name", deviceName);
            headers.put("x-client-version", deviceAgent);
            
            setRegionIp(regionIp, headers);
            
            TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/ntlm/token",
                headers,
                ntlmGetChallengeRequest);
            String responseBody = checkRestResponse(response, basePath, restResponse);
            RestLoginResponse restLoginResponse = JsonUtils.stringToObject(responseBody,
                RestLoginResponse.class);
            String loginName = restLoginResponse.getLoginName();
            
            if (null != deviceType && "1".equals(deviceType))
            {
                ntlmPcLogin(restLoginResponse, request, response);
                return false;
            }
            long enterpriseId = restLoginResponse.getEnterpriseId();
            long accountId = restLoginResponse.getAccountId();
            AuthenticationToken token = new UsernamePasswordCaptchaToken(loginName, "".toCharArray(), false,
                "", true, "", deviceOS, deviceAddress, proxyAddress, deviceAgent, sessionId,
                restLoginResponse.getToken(), restLoginResponse.getRefreshToken(), new Date(
                    System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L), enterpriseId,
                accountId, regionIp,restLoginResponse.getDomain());
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.login(token);
            return true;
        }
        catch (IOException e)
        {
            handleException(request, response, e);
            return false;
        }
        catch (AuthenticationException e)
        {
            handleException(request, response, e);
            return false;
        }
        catch (RuntimeException e)
        {
            handleException(request, response, e);
            return false;
        }
    }
    
    private String checkRestResponse(HttpServletResponse response, String basePath, TextResponse restResponse)
        throws IOException
    {
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            response.sendRedirect(basePath + "/login");
            logger.error("Login interface return code is not 200");
            throw new LoginAuthFailedException();
        }
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            logger.error("Login interface return code is 200 but body is null");
            throw new LoginAuthFailedException();
        }
        return responseBody;
    }
    
    private void setRegionIp(String regionIp, Map<String, String> headers)
    {
        if (StringUtils.isNotBlank(regionIp))
        {
            headers.put(Constants.HTTP_X_REGION_IP, regionIp);
        }
        logger.info("User login region ip: {}", regionIp);
    }
    
    private boolean returnNTLMChallengeSuccess(HttpServletRequest request, HttpServletResponse response,
        String authorization, String sessionId) throws IOException
    {
        NtlmGetChallengeRequest ntlmGetChallengeRequest = new NtlmGetChallengeRequest();
        ntlmGetChallengeRequest.setKey(sessionId);
        ntlmGetChallengeRequest.setUsernameHash(authorization);
        ntlmGetChallengeRequest.setAppId(authAppService.getCurrentAppId());
        Map<String, String> headers = new HashMap<String, String>(3);
        headers.put("x-real-ip", RequestUtils.getRealIP(request));
        headers.put("x-request-ip", RequestUtils.getRealIP(request));
        headers.put("x-proxy-ip", RequestUtils.getProxyIP(request));
        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/ntlm/challenge",
            headers,
            ntlmGetChallengeRequest);
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            sendAuthenticateResponse(response, "");
            return false;
        }
        NtlmGetChallengeResponse ntlmGetChallengeResponse = JsonUtils.stringToObject(responseBody,
            NtlmGetChallengeResponse.class);
        String challenge = ntlmGetChallengeResponse.getChallenge();
        sendAuthenticateResponse(response, challenge);
        return false;
    }
    
    @Override
    public RestLoginResponse checkFormUser(UserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException {
        return checkFormUserInternal(userLoginRequest, userToken, regionIp, "/api/v2/token");
    }

    @Override
    public RestLoginResponse checkFormUser(WxUserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException {
    	return checkFormUserInternal(userLoginRequest, userToken, regionIp, "/api/v2/token/wx");
    }

    @Override
    public RestLoginResponse checkFormUser(WxWorkUserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException {
        return checkFormUserInternal(userLoginRequest, userToken, regionIp, "/api/v2/token/wxwork");
    }

    protected RestLoginResponse checkFormUserInternal(Serializable userLoginRequest, UserToken userToken, String regionIp, String url) throws LoginAuthFailedException, InternalServerErrorException
    {
        Map<String, String> headers = new HashMap<>(6);
        headers.put("x-device-type", UserServiceImpl.transDeviceStrType(userToken.getDeviceType()));
        headers.put("x-device-sn", userToken.getDeviceSn());
        headers.put("x-device-os", userToken.getDeviceOS());
        headers.put("x-device-name", userToken.getDeviceName());
        headers.put("x-client-version", userToken.getDeviceAgent());
        headers.put("x-real-ip", userToken.getDeviceAddress());
        logger.info("cloudapp x-real-ip:" + userToken.getDeviceAddress());
        // headers.put("cloudapp-client-ip", userToken.getDeviceAddress());
        headers.put("x-proxy-ip", userToken.getProxyAddress());
        if (StringUtils.isNotBlank(regionIp))
        {
            headers.put(Constants.HTTP_X_REGION_IP, regionIp);
        }
        logger.info("User login region ip : {}", regionIp);

        TextResponse restResponse = uamClientService.performJsonPostTextResponse(url,
            headers,
            userLoginRequest);
        if (null == restResponse)
        {
            logger.error("Login interface return null");
            throw new LoginAuthFailedException();
        }
        else if (restResponse.getStatusCode() == 401)
        {
            RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(),
                RestException.class);
            logger.error("The return code is 401");
            if ("AD".equals(exception.getCode()))
            {
                throw new LdapLoginAuthFailedException();
            }
            throw new LoginAuthFailedException();
        }
        else if (restResponse.getStatusCode() == 200)
        {
            String responseBody = restResponse.getResponseBody();
            if (StringUtils.isBlank(responseBody))
            {
                logger.error("Login interface return code is 200 but body is null");
                throw new LoginAuthFailedException();
            }
            RestUserLoginResponse restLoginResponse = JsonUtils.stringToObject(responseBody, RestUserLoginResponse.class);
            return restLoginResponse;
        }
        else if (restResponse.getStatusCode() == 403)
        {
            RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(),
                RestException.class);
            if (null == exception)
            {
                logger.error("Login interface return code is 403 but exception is null");
                throw new LoginAuthFailedException();
            }
            if (ErrorCode.USER_LOCKED.getCode().equals(exception.getCode()))
            {
                throw new UserLockedException();
            }
            if (ErrorCode.USER_DISABLED.getCode().equals(exception.getCode()))
            {
                throw new DisabledUserApiException();
            }
            throw new SecurityMartixException();
        }
        else
        {
            logger.error("Login interface return code is " + restResponse.getStatusCode());
            throw new LoginAuthFailedException();
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @param e
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
    {
        String deviceType = request.getHeader("deviceType");
        if (null != deviceType && "1".equals(deviceType))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        logger.error("NTLM authentication failed", e.getMessage());
    }
    
    /**
     * 
     * @param userName
     * @param request
     * @param response
     * @throws IOException
     */
    private void ntlmPcLogin(RestLoginResponse restLoginResponse, HttpServletRequest request,
        HttpServletResponse response) throws IOException
    {
        try
        {
            String deviceType = request.getHeader("deviceType");
            String deviceSN = request.getHeader("deviceSN");
            String deviceOS = request.getHeader("deviceOS");
            String deviceName = request.getHeader("deviceName");
            String deviceAgent = request.getHeader("deviceAgent");
            UserToken userToken = new UserToken();
            transUser(userToken, restLoginResponse);
            userToken.setDeviceSn(deviceSN);
            userToken.setDeviceOS(deviceOS);
            userToken.setDeviceName(deviceName);
            userToken.setDeviceType(Integer.parseInt(deviceType));
            userToken.setDeviceAgent(deviceAgent);
            userToken.setDeviceAddress(RequestUtils.getRealIP(request));
            userToken.setToken(restLoginResponse.getToken());
            userToken.setExpiredAt(new Date(System.currentTimeMillis() + restLoginResponse.getTimeout()
                * 1000L));
            RestUserloginRsp userRsp = new RestUserloginRsp();
            userRsp.setToken(restLoginResponse.getToken());
            userRsp.setRefreshToken(restLoginResponse.getRefreshToken());
            userRsp.setUsername(userToken.getLoginName());
            userRsp.setUserId(userToken.getCloudUserId());
            userRsp.setToken(userToken.getToken());
            userRsp.setTokenType(userToken.getTokenType());
            userRsp.setUploadQos(restLoginResponse.getUploadQos());
            userRsp.setDownloadQos(restLoginResponse.getDownloadQos());
            userRsp.setToExpiredAt(restLoginResponse.getTimeout() * 1000L);
            userRsp.setCloudUserId(userToken.getId());
            userRsp.setLastAccessTerminal(restLoginResponse.getLastAccessTerminal());
            String body = jsonMapper.toJson(userRsp);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentLength(body.length());
            response.setHeader("USER_NAME", restLoginResponse.getLoginName());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(body);
            response.flushBuffer();
            userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
        }
        catch (DisabledTerminalStatusException e)
        {
            logger.error(e.getMessage());
            String body = jsonMapper.toJson(e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(body);
            response.flushBuffer();
        }
        catch (LoginAuthFailedException e)
        {
            logger.error(e.getMessage());
            String body = jsonMapper.toJson(e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(body);
            response.flushBuffer();
        }
    }
    
    private void sendAuthenticateResponse(HttpServletResponse response, String authenMsg) throws IOException
    {
        response.setContentLength(0);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, authenMsg);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }
    
    private void transUser(UserToken userToken, RestLoginResponse restLoginResponse)
    {
        userToken.setId(restLoginResponse.getUserId());
        userToken.setLoginName(restLoginResponse.getLoginName());
        userToken.setDomain(restLoginResponse.getDomain());
        userToken.setCloudUserId(restLoginResponse.getCloudUserId());
        userToken.setEnterpriseId(restLoginResponse.getEnterpriseId());
        userToken.setAccountId(restLoginResponse.getAccountId());
    }
    
    /**
     * 校验浏览器版本，低于IE8的版本跳转至于提示页面
     * 
     * @param request
     * @return
     */
    @Override
    public boolean checkBrowser(HttpServletRequest request)
    {
        try
        {
            boolean isCheckBrowser = Boolean.parseBoolean(PropertiesUtils.getProperty("is.check.browserVersion",
                "false"));
            // 如果配置为不做IE检测，则直接返回
            if (!isCheckBrowser)
            {
                return true;
            }
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            Browser browser = userAgent.getBrowser();
            String browserName = browser.getName();
            Version version = userAgent.getBrowserVersion();
            if (version == null)
            {
                return true;
            }
            String versionStr = version.getMajorVersion();
            int versionInt = 0;
            // 版本号
            if (StringUtils.isNotBlank(versionStr))
            {
                versionInt = Integer.parseInt(versionStr);
            }
            // 仅判断IE浏览器 IE8以下的浏览器不支持
            if (BROWSER_IE_NAME.equals(browserName))
            {
                if (versionInt != 0 && versionInt < BROWSER_VERSION_8)
                {
                    return false;
                }
            }
        }
        // 功能兼容处理，浏览器判断功能仅做提示功能，不能因此影响业务
        catch (Exception e)
        {
            logger.error("check browser failed", e);
        }
        return true;
    }
    
    /**
     * 校验浏览器版本，低于IE8的版本跳转至于提示页面
     * 
     * @param request
     * @return
     */
    @Override
    public boolean checkBrowser(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            boolean isCheckBrowser = Boolean.parseBoolean(PropertiesUtils.getProperty("is.check.browserVersion",
                "false"));
            // 如果配置为不做IE检测，则直接返回
            if (!isCheckBrowser)
            {
                return true;
            }
            String deviceType = request.getHeader("deviceType");
            // 如果是PC客户端，则直接返回，不检测版本
            if (StringUtils.isNotBlank(deviceType) && "1".equals(deviceType))
            {
                return true;
            }
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            Browser browser = userAgent.getBrowser();
            String browserName = browser.getName();
            Version version = userAgent.getBrowserVersion();
            if (version == null)
            {
                return true;
            }
            String versionStr = version.getMajorVersion();
            int versionInt = 0;
            // 版本号
            if (StringUtils.isNotBlank(versionStr))
            {
                versionInt = Integer.parseInt(versionStr);
            }
            // 仅判断IE浏览器 IE8以下的浏览器不支持
            if (BROWSER_IE_NAME.equals(browserName))
            {
                if (versionInt != 0 && versionInt < BROWSER_VERSION_8)
                {
                    String basePath = getBasePath(request);
                    response.sendRedirect(basePath + BROWSER_VERSION_TIPS_PATH);
                    return false;
                }
            }
        }
        // 功能兼容处理，浏览器判断功能仅做提示功能，不能因此影响业务
        catch (Exception e)
        {
            logger.error("check browser failed", e);
        }
        return true;
    }
    
    /**
     * 获取服务器访问地址
     * 
     * @param request
     * @return
     */
    @Override
    public String getBasePath(HttpServletRequest request)
    {
        String contentPath = request.getContextPath();
        SecurityConfig securityConfig = securityService.getSecurityConfig();
        String protocolType = securityConfig.getProtocolType();
        if (StringUtils.isBlank(protocolType))
        {
            protocolType = "https";
        }
        String basePath = protocolType + "://" + request.getServerName()
            + securityService.changePort(String.valueOf(request.getServerPort()), protocolType) + contentPath;
        return basePath;
    }
}
