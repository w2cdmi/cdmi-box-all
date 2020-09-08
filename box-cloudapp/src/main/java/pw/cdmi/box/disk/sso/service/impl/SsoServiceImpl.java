package pw.cdmi.box.disk.sso.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.sso.domain.RestSsoLoginRequest;
import pw.cdmi.box.disk.sso.service.SsoService;
import pw.cdmi.box.disk.user.service.impl.UserServiceImpl;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.core.exception.DisabledUserApiException;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.exception.SecurityMartixException;
import pw.cdmi.core.exception.UserLockedException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Service
public class SsoServiceImpl implements SsoService
{
    private static Logger logger = LoggerFactory.getLogger(SsoServiceImpl.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Resource
    private RestClient uamClientService;
    
    @Override
    public RestLoginResponse checkTmpToken(HttpServletRequest request, String ssotoken)
        throws LoginAuthFailedException, InternalServerErrorException
    {
        Map<String, String> headers = new HashMap<String, String>(6);
        
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String deviceAddress = RequestUtils.getRealIP(request);
        String proxyAddress = RequestUtils.getProxyIP(request);
        String deviceType = request.getHeader("deviceType");
        try
        {
            deviceType = deviceType == null ? UserServiceImpl.transDeviceStrType(0)
                : UserServiceImpl.transDeviceStrType(Integer.parseInt(deviceType));
        }
        catch (NumberFormatException e)
        {
            logger.error("deviceType is not number", e);
            throw new InvalidParamException(e.getMessage());
        }
        String deviceAgent = userAgent.getBrowser().getName();
        String deviceOS = userAgent.getOperatingSystem().getName();
        headers.put("x-device-type", deviceType);
        headers.put("x-real-ip", deviceAddress);
        headers.put("x-request-ip", deviceAddress);
        headers.put("x-proxy-ip", proxyAddress);
        headers.put("x-device-os", deviceOS);
        headers.put("x-client-version", deviceAgent);
        RestSsoLoginRequest restSSOLoginRequest = new RestSsoLoginRequest();
        restSSOLoginRequest.setAppId(authAppService.getCurrentAppId());
        restSSOLoginRequest.setSsoToken(ssotoken);
        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/api/v2/ssotoken",
            headers,
            restSSOLoginRequest);
        if (null == restResponse)
        {
            logger.error("check tmp token failed return null");
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
}
