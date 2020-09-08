package com.huawei.sharedrive.cloudapp.sso.manager.impl;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.cloudapp.client.utils.Constants;
import com.huawei.sharedrive.cloudapp.cmb.control.CMBConstants;
import com.huawei.sharedrive.cloudapp.exception.InternalServerErrorException;
import com.huawei.sharedrive.cloudapp.exception.InvalidParamException;
import com.huawei.sharedrive.cloudapp.exception.LoginAuthFailedException;
import com.huawei.sharedrive.cloudapp.httpclient.rest.request.RestLoginResponse;
import com.huawei.sharedrive.cloudapp.sso.manager.SsoManager;
import com.huawei.sharedrive.cloudapp.sso.service.SsoService;
import com.huawei.sharedrive.cloudapp.system.service.SecurityService;
import com.huawei.sharedrive.cloudapp.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.cloudapp.user.shiro.UsernamePasswordCaptchaToken;
import com.huawei.sharedrive.cloudapp.utils.RequestUtils;
import com.huawei.sharedrive.common.domain.SecurityConfig;
import com.huawei.sharedrive.common.userAgent.UserAgent;

@Component
public class SsoManagerImpl implements SsoManager
{
    private static Logger logger = LoggerFactory.getLogger(SsoManagerImpl.class);
    
    public static final String SSO_PATH = "sso/ssocmb";
    
    @Autowired
    private SsoService ssoService;
    
    @Autowired
    private SecurityService securityService;
    
    @Override
    public boolean isSSORedirectd(HttpServletRequest request, HttpServletResponse response, String ssotoken,
        String nextAction) throws IOException
    {
        boolean isRedirectd = false;
        if (!CMBConstants.isCMB)
        {
            return isRedirectd;
        }
        if (StringUtils.isBlank(ssotoken))
        {
            if (CMBConstants.IS_BUSINESS_BOX)
            {
                return isRedirectd;
            }
            else
            {
                String servletPath = request.getServletPath();
                String redirectCmbUrl = CMBConstants.CMB_URL + "?SysID=" + CMBConstants.CMB_SYS_ID
                    + "&NextAction=" + servletPath;
                response.sendRedirect(redirectCmbUrl);
                isRedirectd = true;
            }
        }
        else
        {
            String redirectUrl = "";
            try
            {
                String basePath = getBasePath(request);
                RestLoginResponse restLoginResponse = ssoService.checkTmpToken(request, ssotoken);
                login(request, restLoginResponse);
                if (StringUtils.isBlank(nextAction))
                {
                    
                    logger.info("bathPath: " + basePath);
                    redirectUrl = basePath;
                    isRedirectd = true;
                }
                else
                {
                    logger.info("bathPath: " + basePath + nextAction);
                    redirectUrl = basePath + nextAction;
                    isRedirectd = true;
                }
            }
            catch (Exception e)
            {
                logger.error("sso login failed", e);
                redirectUrl = CMBConstants.CMB_URL + "?SysID=" + CMBConstants.CMB_SYS_ID + "&NextAction="
                    + nextAction;
                isRedirectd = true;
            }
            if (isRedirectd)
            {
                response.sendRedirect(redirectUrl);
            }
        }
        return isRedirectd;
    }
    
    @Override
    public boolean isSSORedirectdWithSession(HttpServletRequest request, HttpServletResponse response,
        String ssotoken, String nextAction) throws IOException
    {
        boolean isRedirectd = false;
        if (StringUtils.isBlank(ssotoken))
        {
            return isRedirectd;
        }
        if (CMBConstants.isCMB)
        {
            String basePath = getBasePath(request);
            String redirectdUrl = "";
            try
            {
                RestLoginResponse restLoginResponse = ssoService.checkTmpToken(request, ssotoken);
                login(request, restLoginResponse);
                if (StringUtils.isBlank(nextAction))
                {
                    logger.info("bathPath: " + basePath);
                    redirectdUrl = basePath;
                    isRedirectd = true;
                }
                else
                {
                    redirectdUrl = basePath + nextAction;
                    isRedirectd = true;
                }
            }
            catch (LoginAuthFailedException | InternalServerErrorException e)
            {
                redirectdUrl = CMBConstants.CMB_URL + "?SysID=" + CMBConstants.CMB_SYS_ID + "&NextAction="
                    + nextAction;
                logger.error("sso login failed", e);
                isRedirectd = true;
            }
            if (isRedirectd)
            {
                response.sendRedirect(redirectdUrl);
            }
        }
        return isRedirectd;
    }
    
    @Override
    public boolean isCanRedirectLogin(HttpServletResponse response)
    {
        boolean isCanLogin = true;
        if (CMBConstants.isCMB)
        {
            if (!CMBConstants.IS_BUSINESS_BOX)
            {
                try
                {
                    String redirectUrl = CMBConstants.CMB_URL + "?SysID=" + CMBConstants.CMB_SYS_ID
                        + "&NextAction=";
                    response.sendRedirect(redirectUrl);
                    isCanLogin = false;
                }
                catch (IOException e)
                {
                    logger.error("redirect failed ", e);
                }
            }
        }
        return isCanLogin;
    }
    
    @Override
    public String getUrlByLoginout()
    {
        String url = "";
        if (CMBConstants.isCMB)
        {
            if (!CMBConstants.IS_BUSINESS_BOX)
            {
                url = CMBConstants.CMB_URL;
            }
        }
        return url;
    }
    
    private void login(HttpServletRequest request, RestLoginResponse restLoginResponse)
    {
        String sessionId = request.getSession().getId();
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String deviceAddress = RequestUtils.getRealIP(request);
        String proxyAddress = RequestUtils.getProxyIP(request);
        try
        {
            String deviceType = request.getHeader("deviceType");
            deviceType = deviceType == null ? UserServiceImpl.transDeviceStrType(0)
                : UserServiceImpl.transDeviceStrType(Integer.parseInt(deviceType));
        }
        catch (NumberFormatException e)
        {
            logger.error("deviceType is not number", e);
            throw new InvalidParamException();
        }
        
        String deviceAgent = userAgent.getBrowser().getName();
        String deviceOS = userAgent.getOperatingSystem().getName();
        String loginName = restLoginResponse.getLoginName();
        long enterpriseId = restLoginResponse.getEnterpriseId();
        long accountId = restLoginResponse.getAccountId();
        
        String regionIp = request.getHeader(Constants.HTTP_X_REGION_IP);
        logger.info("User login region ip: {}", regionIp);
        
        AuthenticationToken token = new UsernamePasswordCaptchaToken(loginName, "".toCharArray(), false, "",
            true, "", deviceOS, deviceAddress, proxyAddress, deviceAgent, sessionId,
            restLoginResponse.getToken(), restLoginResponse.getRefreshToken(), new Date(
                System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L), enterpriseId,
            accountId, regionIp);
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(token);
    }
    
    private String getBasePath(HttpServletRequest request)
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
