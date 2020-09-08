package com.huawei.sharedrive.cloudapp.cmb.user.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.cloudapp.authapp.service.AuthAppService;
import com.huawei.sharedrive.cloudapp.cmb.user.service.UserCMBLoginService;
import com.huawei.sharedrive.cloudapp.exception.DisabledUserApiException;
import com.huawei.sharedrive.cloudapp.exception.ErrorCode;
import com.huawei.sharedrive.cloudapp.exception.InternalServerErrorException;
import com.huawei.sharedrive.cloudapp.exception.LoginAuthFailedException;
import com.huawei.sharedrive.cloudapp.exception.RestException;
import com.huawei.sharedrive.cloudapp.exception.SecurityMartixException;
import com.huawei.sharedrive.cloudapp.exception.UserLockedException;
import com.huawei.sharedrive.cloudapp.httpclient.rest.UAMRestClientService;
import com.huawei.sharedrive.cloudapp.httpclient.rest.request.RestLoginResponse;
import com.huawei.sharedrive.cloudapp.oauth2.domain.UserToken;
import com.huawei.sharedrive.cloudapp.user.service.UserService;
import com.huawei.sharedrive.cloudapp.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.cloudapp.utils.JsonUtils;
import com.huawei.sharedrive.common.restrpc.RestClient;
import com.huawei.sharedrive.common.restrpc.domain.TextResponse;

@Component
public class UserCMBLoginServiceImpl extends UAMRestClientService implements UserCMBLoginService
{
    
    private static Logger logger = LoggerFactory.getLogger(UserCMBLoginServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public RestLoginResponse authCmbUser(String data, String token, String appId, UserToken userToken)
        throws LoginAuthFailedException, InternalServerErrorException
    {
        Map<String, String> headers = new HashMap<String, String>(6);
        headers.put("x-device-type", UserServiceImpl.transDeviceStrType(userToken.getDeviceType()));
        headers.put("x-device-sn", userToken.getDeviceSn());
        headers.put("x-device-os", userToken.getDeviceOS());
        headers.put("x-device-name", userToken.getDeviceName());
        headers.put("x-client-version", userToken.getDeviceAgent());
        headers.put("x-real-ip", userToken.getDeviceAddress());
        headers.put("x-proxy-ip", userToken.getProxyAddress());
        CMBSsoAPIRequest cMBSsoAPIRequest = new CMBSsoAPIRequest();
        cMBSsoAPIRequest.setAppId(appId);
        cMBSsoAPIRequest.setData(data);
        cMBSsoAPIRequest.setToken(token);
        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/cmb/ssoauth",
            headers,
            cMBSsoAPIRequest);
        if (null == restResponse)
        {
            logger.error("Login interface return null");
            throw new LoginAuthFailedException();
        }
        else if (restResponse.getStatusCode() == 401)
        {
            logger.error("The return code is 401");
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
            RestLoginResponse restLoginResponse = JsonUtils.stringToObject(responseBody,
                RestLoginResponse.class);
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
            if (ErrorCode.USERLOCKED.getCode().equals(exception.getCode()))
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
    
}
