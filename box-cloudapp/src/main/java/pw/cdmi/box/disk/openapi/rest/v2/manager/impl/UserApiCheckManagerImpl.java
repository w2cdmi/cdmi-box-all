package pw.cdmi.box.disk.openapi.rest.v2.manager.impl;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.box.disk.client.domain.user.RestUserloginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.UserApiLoginRequest;
import pw.cdmi.box.disk.openapi.rest.v2.manager.UserApiCheckManager;
import pw.cdmi.box.disk.user.domain.Terminal;

@Component
public class UserApiCheckManagerImpl implements UserApiCheckManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiCheckManager.class);
    
    private static final String DOMAIN_REGEX = "[^/\\\\]{1,}";
    
    @Override
    public void checkV1LoginParam(RestUserloginRequest userlogin) throws LoginAuthFailedException,
        BadRquestException
    {
        String userloginName = userlogin.getLoginName();
        String password = userlogin.getPassword();
        String domain = userlogin.getDomain();
        if (StringUtils.isBlank(password) || StringUtils.isBlank(userloginName))
        {
            LOGGER.error("username or password is null. userName:" + userloginName);
            throw new LoginAuthFailedException();
        }
        if (userloginName.length() > 127 || password.length() > 127)
        {
            LOGGER.error("username or password out of range. userName:" + userloginName);
            throw new LoginAuthFailedException();
        }
        if (StringUtils.isNotBlank(domain))
        {
            if (domain.length() > 64 || !checkDomainRegex(domain))
            {
                LOGGER.error("domain is invalid domain:" + domain);
                throw new BadRquestException();
            }
        }
        String deviceSN = userlogin.getDeviceSn();
        String deviceName = userlogin.getDeviceName();
        int deviceType = userlogin.getDeviceType();
        String deviceOS = userlogin.getDeviceOS();
        String deviceAgent = userlogin.getDeviceAgent();
        if (StringUtils.isBlank(deviceSN) || StringUtils.isBlank(deviceName) || StringUtils.isBlank(deviceOS)
            || StringUtils.isBlank(deviceAgent))
        {
            throw new BadRquestException();
        }
        if (!(deviceType == Terminal.CLIENT_TYPE_ANDROID || deviceType == Terminal.CLIENT_TYPE_PC || deviceType == Terminal.CLIENT_TYPE_IOS))
        {
            throw new BadRquestException();
        }
    }
    
    @Override
    public void checkV2LoginParam(UserApiLoginRequest userlogin, HttpServletRequest request)
        throws LoginAuthFailedException, InvalidParamException
    {
        String loginName = userlogin.getLoginName();
        String password = userlogin.getPassword();
        String domain = userlogin.getDomain();
        String deviceTypeStr = request.getHeader("x-device-type");
        String deviceSN = request.getHeader("x-device-sn");
        String deviceOS = request.getHeader("x-device-os");
        String deviceName = request.getHeader("x-device-name");
        String deviceAgent = request.getHeader("x-client-version");
        if (StringUtils.isBlank(password) || StringUtils.isBlank(loginName))
        {
            LOGGER.error("username or password is null. userName:" + loginName);
            throw new LoginAuthFailedException();
        }
        if (loginName.length() > 127 || password.length() > 127)
        {
            LOGGER.error("username or password out of range. userName:" + loginName);
            throw new LoginAuthFailedException();
        }
        if (StringUtils.isNotBlank(domain))
        {
            if (domain.length() > 64 || !checkDomainRegex(domain))
            {
                LOGGER.error("domain is invalid domain:" + domain);
                throw new InvalidParamException();
            }
        }
        if (StringUtils.isBlank(deviceSN) || StringUtils.isBlank(deviceName) || StringUtils.isBlank(deviceOS)
            || StringUtils.isBlank(deviceAgent) || StringUtils.isBlank(deviceTypeStr))
        {
            LOGGER.error("device parameter is invalid deviceSN:" + deviceSN + " deviceName:" + deviceName
                + " deviceOS:" + deviceOS + " deviceAgent:" + deviceAgent + " deviceTypeStr:" + deviceTypeStr);
            throw new InvalidParamException();
        }
    }
    
    private boolean checkDomainRegex(String domain)
    {
        
        return Pattern.compile(DOMAIN_REGEX).matcher(domain).matches();
    }
}
