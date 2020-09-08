package com.huawei.sharedrive.app.security.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.RestException;
import com.huawei.sharedrive.app.exception.SecurityMatixException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.security.domain.CheckOperationAllowedRequest;
import com.huawei.sharedrive.app.security.domain.CheckOperationAllowedResponse;
import com.huawei.sharedrive.app.security.domain.GetSecurityIdResponse;
import com.huawei.sharedrive.app.security.domain.Operation;
import com.huawei.sharedrive.app.security.domain.UserSecurityResponse;
import com.huawei.sharedrive.app.security.service.SecurityMatrixHelper;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.RestConstants;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class SecurityRestClient
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityRestClient.class);
    
    @Autowired
    private RestClient uamClientService;
    
    @Autowired
    private AuthAppService authAppService;
    
    /**
     * 获取文件的安全级别
     * 
     * @return
     * @throws RestException
     */
    public byte getFileSecurityId(UserToken userToken, String ip) throws RestException
    {
        // 获得uam地址
        String authURL = getAuthURL(userToken.getAppId());
        
        String url = new StringBuffer(authURL).append(RestConstants.RESOURCE_SECURITY)
            .append("/level")
            .toString();
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headers.put(RestConstants.HEADER_AUTHORIZATION, userToken.getToken());
        if (StringUtils.isNotBlank(ip))
        {
            headers.put(Constants.HTTP_X_REAL_IP, ip);
        }
        else
        {
            LOGGER.info("userToken device ip : " + userToken.getDeviceAddress());
            headers.put(Constants.HTTP_X_REAL_IP, userToken.getDeviceAddress());
        }
        
        TextResponse response = uamClientService.performGetTextByUri(url, headers);
        if (HttpStatus.OK.value() == response.getStatusCode())
        {
            String content = response.getResponseBody();
            GetSecurityIdResponse resp = JsonUtils.stringToObject(content, GetSecurityIdResponse.class);
            return resp.getSecurityId();
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 获取文件的安全级别
     * 
     * @return
     * @throws RestException
     */
    public Integer getUserSecurityId(UserToken userToken, User user) throws RestException
    {
        // 获得uam地址
        String authURL = getAuthURL(user.getAppId());
        
        String url = new StringBuffer(authURL).append(RestConstants.RESOURCE_SECURITY)
            .append("/roles/")
            .append(user.getAccountId())
            .append('/')
            .append(user.getId())
            .toString();
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        
        headers.put(RestConstants.HEADER_AUTHORIZATION, userToken.getToken());
        
        TextResponse response = uamClientService.performGetTextByUri(url, headers);
        if (HttpStatus.OK.value() == response.getStatusCode())
        {
            String content = response.getResponseBody();
            UserSecurityResponse resp = JsonUtils.stringToObject(content, UserSecurityResponse.class);
            return resp.getSecurityRoleId();
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public Boolean isOperationAllowed(Operation operation, byte securityId, int spaceSecRoleId,
        Integer targetSecRoleId, UserToken userToken) throws RestException
    {
        // 获取uam地址
        String authURL = getAuthURL(userToken.getAppId());
        
        String url = new StringBuffer(authURL).append(RestConstants.RESOURCE_SECURITY)
            .append("/judge")
            .toString();
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        
        headers.put(RestConstants.HEADER_AUTHORIZATION, userToken.getToken());
        String ip = SecurityMatrixHelper.getRealIP();
        
        if (StringUtils.isNotBlank(ip))
        {
            LOGGER.info("Real ip : " + ip);
            headers.put(Constants.HTTP_X_REAL_IP, ip);
        }
        else
        {
            LOGGER.info("userToken device ip : " + userToken.getDeviceAddress());
            headers.put(Constants.HTTP_X_REAL_IP, userToken.getDeviceAddress());
        }
        
        CheckOperationAllowedRequest request = new CheckOperationAllowedRequest(operation.getCode(),
            securityId, spaceSecRoleId, targetSecRoleId);
        
        TextResponse response = uamClientService.performJsonPostTextResponseByUri(url, headers, request);
        if (HttpStatus.OK.value() == response.getStatusCode())
        {
            String content = response.getResponseBody();
            CheckOperationAllowedResponse resp = JsonUtils.stringToObject(content,
                CheckOperationAllowedResponse.class);
            return resp.isAllowed();
        }
        LOGGER.info("response.getResponseBody() : " + response.getResponseBody());
        throw new SecurityMatixException();
    }
    
    /**
     * 获取uam认证地址
     * 
     * @param appId
     * @return
     */
    private String getAuthURL(String appId)
    {
        String authURL = null;
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (authApp != null)
        {
            authURL = authApp.getAuthUrl();
        }
        if (StringUtils.isEmpty(authURL))
        {
            throw new SecurityMatixException("authUrl is null");
        }
        if (authURL.charAt(authURL.length() - 1) == '/')
        {
            authURL = authURL.substring(0, authURL.length() - 1);
        }
        return authURL;
    }
    
    
    /**
     * 获取机密文件对应的持有关系
     * 
     * @return
     * @throws RestException
     */
    public String getSecretFileSip(UserToken userToken, User user) throws RestException
    {
        // 获得uam地址
        String authURL = getAuthURL(user.getAppId());
        
        String url = new StringBuffer(authURL).append(RestConstants.RESOURCE_SECURITY)
            .append("/secretStaff/")
            .append(user.getAccountId())
            .toString();
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        
        headers.put(RestConstants.HEADER_AUTHORIZATION, userToken.getToken());
        
        TextResponse response = uamClientService.performGetTextByUri(url, headers);
        if (HttpStatus.OK.value() == response.getStatusCode())
        {
            String content = response.getResponseBody();
           // UserSecurityResponse resp = JsonUtils.stringToObject(content, UserSecurityResponse.class);
            return content;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }

    
}
