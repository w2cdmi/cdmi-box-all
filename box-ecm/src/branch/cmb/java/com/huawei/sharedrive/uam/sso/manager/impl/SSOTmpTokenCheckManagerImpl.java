package com.huawei.sharedrive.uam.sso.manager.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.sso.manager.SSOTmpTokenCheckManager;
import com.huawei.sharedrive.uam.terminal.service.impl.TerminalServiceImpl;
import com.huawei.sharedrive.uam.util.PatternRegUtil;

@Component
public class SSOTmpTokenCheckManagerImpl implements SSOTmpTokenCheckManager
{
    private static Logger logger = LoggerFactory.getLogger(SSOTmpTokenCheckManagerImpl.class);
    
    private static final int APPID_LENGTH = 20;
    
    @Override
    public void checkSsoParamter(String appId, String ssoToken, String domainName, HttpServletRequest request)
        throws InvalidParamterException
    {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(ssoToken))
        {
            logger.error("invalidate parameter loginName or appId:" + appId);
            throw new InvalidParamterException();
        }
        if (appId.length() > APPID_LENGTH)
        {
            logger.error("The login name or appId is too long appId:" + appId);
            throw new InvalidParamterException();
        }
        if (StringUtils.isNotBlank(domainName))
        {
            if (!PatternRegUtil.checkDomainRegex(domainName))
            {
                logger.error("domainName is invalid:" + domainName);
                throw new InvalidParamterException();
            }
        }
        TerminalServiceImpl.checkDeviceParamter(request);
    }
}
