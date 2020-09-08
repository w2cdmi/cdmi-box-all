package pw.cdmi.box.disk.user.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wcc.crypt.EncryptHelper;
import pw.cdmi.box.disk.accountbaseconfig.dao.AccountConfigDao;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.disk.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.disk.enterprisecontrol.EnterpriseAuthControlManager;
import pw.cdmi.box.disk.enterprisecontrol.impl.EnterpriseAuthControlManagerImpl;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.system.service.VerifyCodeHelper;
import pw.cdmi.box.disk.user.dao.UserLockedDao;
import pw.cdmi.box.disk.user.domain.UserLocked;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.ManagerLocked;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.common.web.RefererMatchFilter;
import pw.cdmi.core.exception.ADLoginAuthFailedException;
import pw.cdmi.core.exception.InvalidCaptchaException;
import pw.cdmi.core.exception.NoCaptchaException;
import pw.cdmi.core.utils.CookieUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormAuthenticationCaptchaFilter extends FormAuthenticationFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FormAuthenticationCaptchaFilter.class);
    
    private static final String LOCK_WAIT = "lock.time";
    
    private static final String LOCK_WAIT_TIP = "lockWaitTip";
    
    private static final String KEY_VERYCODE_UUID = "verycodeId";
    
    private static final String KEY_VERYCODE_VALUE = "captcha";
    
    private static final String CLOUDAPP = "cloudapp";
    
    private SecurityService securityService;
    
    private UserLockedDao userLockedDao;
    
    private CacheClient cacheClient;
    
    private VerifyCodeHelper verifyCodeHelper;
    
    private RefererMatchFilter refererMatchFilter;
    
    private EnterpriseAuthControlManager enterpriseAuthControlManager;
    
    private AuthAppService authAppService;
    
    private SystemConfigDAO systemConfigDao;
    
    @Autowired
    private AccountConfigDao accountConfigDao;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    public SystemConfigDAO getSystemConfigDao()
    {
        return systemConfigDao;
    }
    
    public void setSystemConfigDao(SystemConfigDAO systemConfigDao)
    {
        this.systemConfigDao = systemConfigDao;
    }
    
    public void setAuthAppService(AuthAppService authAppService)
    {
        this.authAppService = authAppService;
    }
    
    public CacheClient getCacheClient()
    {
        return cacheClient;
    }
    
    public void setCacheClient(CacheClient cacheClient)
    {
        this.cacheClient = cacheClient;
    }
    
    public void setEnterpriseAuthControlManager(EnterpriseAuthControlManager enterpriseAuthControlManager)
    {
        this.enterpriseAuthControlManager = enterpriseAuthControlManager;
    }
    
    public void setSecurityService(SecurityService securityService)
    {
        this.securityService = securityService;
    }
    
    public void setRefererMatchFilter(RefererMatchFilter refererMatchFilter)
    {
        this.refererMatchFilter = refererMatchFilter;
    }
    
    public void setUserLockedDao(UserLockedDao userLockedDao)
    {
        this.userLockedDao = userLockedDao;
    }
    
    public void setVerifyCodeHelper(VerifyCodeHelper verifyCodeHelper)
    {
        this.verifyCodeHelper = verifyCodeHelper;
    }
    
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response,String ownerDomain)
    {
        HttpServletRequest req = (HttpServletRequest) request;
        String host = getHost(request);
        String loginName = getUsername(request);
        String password = getPassword(request);
        UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));
        String deviceAgent = userAgent.getBrowser().getName();
        String deviceOS = userAgent.getOperatingSystem().getName();
        String deviceAddress = RequestUtils.getRealIP(req);
        String proxyAddress = RequestUtils.getProxyIP(req);
        String sessionId = req.getSession().getId();
        if (password == null || StringUtils.isBlank(password) || loginName == null
            || StringUtils.isBlank(loginName))
        {
            throw new AuthenticationException();
        }
        
        String regionIp = req.getHeader(Constants.HTTP_X_REGION_IP);
        LOGGER.info("User login region ip: {}", regionIp);

        String enterpriseName = WebUtils.getCleanParam(request, "enterpriseName");
        try {
            //将企业名称加入cookie
            CookieUtils.addCookie(((HttpServletResponse)response), "enterpriseName", URLEncoder.encode(enterpriseName, "UTF-8"), 30*24*60*60, "/login");
            CookieUtils.addCookie(((HttpServletResponse)response), "username", loginName, 30*24*60*60);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }

        boolean rememberMe = Boolean.parseBoolean(request.getParameter("rememberMe"));
        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(loginName, password.toCharArray(), rememberMe, host, false, "", deviceOS, deviceAddress, proxyAddress, deviceAgent, sessionId, "", "", null, 0, 0, regionIp, ownerDomain);
        token.setEnterpriseName(enterpriseName);

        return token;
    }
    
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String referer = httpRequest.getHeader("REFERER");
        AccountConfig domianConfig = accountConfigDao.getByDomainName(domainFilter(referer));
        AccountConfig subConfig = accountConfigDao.getByDomainName(domainFilter(referer).split("\\.")[0]);
        String ownerDomain = null;
        if (domianConfig != null || subConfig != null) {
            EnterpriseAccount enterpriseAccount = null;
            if (domianConfig != null) {
                enterpriseAccount = enterpriseAccountManager.getByAccountId(domianConfig.getAccountId());
            }
            if (subConfig != null) {
                enterpriseAccount = enterpriseAccountManager.getByAccountId(subConfig.getAccountId());
            }
            if (enterpriseAccount == null) {
                LOGGER.warn("unknown referer " + referer + ", reject this request");
                httpResponse.sendError(403);
                return false;
            }
            Enterprise enter = enterpriseManager.getById(enterpriseAccount.getEnterpriseId());
            if (enter == null) {
                LOGGER.warn("unknown referer " + referer + ", reject this request");
                httpResponse.sendError(403);
                return false;
            }
            ownerDomain = enter.getDomainName();
        } else {
            boolean matched = refererMatchFilter.isRefererMatched(referer);
            if (!matched) {
                LOGGER.warn("unknown referer " + referer + ", reject this request");
                httpResponse.sendError(403);
                return false;
            }
        }


        AuthenticationToken token = null;
        try {
            token = createToken(request, response, ownerDomain);
            /*checkVeryCodeValid(httpRequest);*/
            Subject subject = getSubject(request, response);
            subject.login(token);
            String saveRequestStr = request.getParameter("savedShrioStr");
            if (saveRequestStr != null) {
                String excludeUrls = PropertiesUtils.getProperty("exclude.rememberurl.redirect");

                String contentPath = httpRequest.getContextPath();
                SecurityConfig securityConfig = securityService.getSecurityConfig();
                String protocolType = securityConfig.getProtocolType();
                if (StringUtils.isBlank(protocolType)) {
                    protocolType = "https";
                }
                String basePath = protocolType + "://" + request.getServerName() + securityService.changePort(String.valueOf(request.getServerPort()), protocolType) + contentPath;

                if (StringUtils.isNotBlank(saveRequestStr)) {
                    checkExcludeUrls(request, response, saveRequestStr, excludeUrls, basePath);
                } else {
                    saveRequestStr = basePath + '/';
                    MyWebUtils.issueRedirect(request, response, saveRequestStr, null, true);
                }
            }
            return false;
        } catch (UnknownAccountException e) {
            httpResponse.sendRedirect("login");
            return onLoginFailure(token, e, request, response);
        } catch (NoCaptchaException e) {
            return onLoginFailure(token, e, request, response);
        } catch (InvalidCaptchaException e) {
            return onLoginFailure(token, e, request, response);
        } catch (ADLoginAuthFailedException e) {
            return onLoginFailure(token, e, request, response);
        } catch (AuthenticationException e) {
            setVerifyCodeResponse(request);
            return onLoginFailure(token, e, request, response);
        }
    }
    
    private void checkExcludeUrls(ServletRequest request, ServletResponse response, String saveRequestStr,
        String excludeUrls, String basePath) throws IOException
    {
        boolean excludeFlag = false;
        String[] splitExcludeUrls = excludeUrls.split(";");
        for (String str : splitExcludeUrls)
        {
            if (StringUtils.isBlank(str))
            {
                continue;
            }
            if (saveRequestStr.contains(str))
            {
                excludeFlag = true;
                break;
            }
        }
        if (excludeFlag)
        {
            String excluedeRedirectUrl = basePath + '/';
            MyWebUtils.issueRedirect(request, response, excluedeRedirectUrl, null, true);
        }
        else
        {
            MyWebUtils.issueRedirect(request, response, saveRequestStr, null, true);
        }
    }
    
    private void setVerifyCodeResponse(ServletRequest request)
    {
        String loginName = getUsername(request);
        if (!StringUtils.isBlank(loginName))
        {
            String uuid = getUUID();
            if (null == uuid)
            {
                return;
            }
            request.setAttribute(KEY_VERYCODE_UUID, uuid);
            verifyCodeHelper.setUserName(uuid, getUsername(request));
        }
    }
    
    private void checkVeryCodeValid(HttpServletRequest request) throws AuthenticationException
    {
        String lockWait = getConfigByLockWait();
        request.setAttribute(LOCK_WAIT_TIP, lockWait);
        String loginName = getUsername(request);
        Map<String, String> map = enterpriseAuthControlManager.getWebDomainLoginName(loginName);
        String selName = map.get(EnterpriseAuthControlManagerImpl.LOGIN_NAME_KEY);
        String domainName = map.get(EnterpriseAuthControlManagerImpl.DOMAIN_NAME_KEY);
        UserLocked locked = userLockedDao.getByLoginName(domainName + '/' + selName);
        String appId = authAppService.getCurrentAppId();
        if (locked == null)
        {
            ManagerLocked managerLocked = (ManagerLocked) cacheClient.getCache(CLOUDAPP + selName
                + domainName + appId);
            
            if (null == managerLocked)
            {
                return;
            }
        }
        else
        {
            if (locked.getLoginFailTimes() == 0)
            {
                return;
            }
            if (null != locked.getLockedAt()
                && new Date().getTime() - locked.getLockedAt().getTime() > Long.parseLong(lockWait) * 60 * 1000)
            {
                return;
            }
        }
        String uuid = request.getParameter(KEY_VERYCODE_UUID);
        String veryCode = request.getParameter(KEY_VERYCODE_VALUE);
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(veryCode))
        {
            String newUuid = getUUID();
            if (null == newUuid)
            {
                return;
            }
            request.setAttribute(KEY_VERYCODE_UUID, newUuid);
            verifyCodeHelper.setUserName(newUuid, getUsername(request));
            throw new NoCaptchaException();
        }
        
        if (!verifyCodeHelper.isUserNameValid(uuid, loginName))
        {
            String newUuid = getUUID();
            if (null == newUuid)
            {
                return;
            }
            request.setAttribute(KEY_VERYCODE_UUID, newUuid);
            verifyCodeHelper.setUserName(newUuid, getUsername(request));
            throw new InvalidCaptchaException();
        }
        
        if (!verifyCodeHelper.isVeryCodeValid(uuid, veryCode))
        {
            verifyCodeHelper.deleteVeryCodeCache(uuid);
            String newUuid = getUUID();
            if (null == newUuid)
            {
                return;
            }
            request.setAttribute(KEY_VERYCODE_UUID, newUuid);
            verifyCodeHelper.setUserName(newUuid, getUsername(request));
            throw new InvalidCaptchaException();
        }
    }
    
    private String getUUID()
    {
        SecureRandom sr = null;
        try
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] keyBytes = new byte[64];
            sr.nextBytes(keyBytes);
            return EncryptHelper.parseByte2HexStr(keyBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("getUUID failed:NoSuchAlgorithmException");
            return null;
        }
        
    }
    
    private String getConfigByLockWait()
    {
        String lockWait = systemConfigDao.getByPriKey("-1", LOCK_WAIT).getValue();
        return lockWait;
    }
    
    private String domainFilter(String host){
    	
    	Pattern p = Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);  
    	Matcher m = p.matcher(host); 
    	String domain = "";
    	//获取一级域名
    	while(m.find()){
    		domain = m.group();
    	}
    	return domain;
    }
}
