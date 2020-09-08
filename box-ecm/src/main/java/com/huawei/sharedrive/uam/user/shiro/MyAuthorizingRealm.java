package com.huawei.sharedrive.uam.user.shiro;

import java.util.*;
import java.util.Map.Entry;

import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.AuthFailedException;
import com.huawei.sharedrive.uam.exception.DisabledUserException;
import com.huawei.sharedrive.uam.exception.IncorrectCaptchaException;
import com.huawei.sharedrive.uam.exception.UserLockedWebException;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.service.SystemLoginLogService;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixObject;
import com.huawei.sharedrive.uam.security.domain.SecurityPrincipal;
import com.huawei.sharedrive.uam.security.service.impl.SecurityMatrixBuilder;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.LoginInfo;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;

import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.utils.EnvironmentUtils;

public class MyAuthorizingRealm extends AuthorizingRealm
{
    
    private String realmName;
    
    public String getRealmName()
    {
        return this.realmName;
    }
    
    private static Logger logger = LoggerFactory.getLogger(MyAuthorizingRealm.class);
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SecurityMatrixBuilder securityMatrixBuilder;
    
    @Autowired
    private SystemLoginLogService systemLoginLogService;
    
    @Autowired
    private AdminLogManager adminLogManager;

    @Autowired
    @Qualifier("wxWorkOauth2Service")
    WxWorkOauth2Service wxOauth2Service;

    @Autowired
    WxEnterpriseService wxEnterpriseService;

    public MyAuthorizingRealm()
    {
        this.realmName = super.getName();
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        if (authcToken instanceof WxWorkAuthCodeToken) {
            return doGetAuthenticationInfo((WxWorkAuthCodeToken) authcToken);
        } else {
            return doGetAuthenticationInfo((UsernamePasswordCaptchaToken) authcToken);
        }
    }

    protected AuthenticationInfo doGetAuthenticationInfo(UsernamePasswordCaptchaToken token) throws AuthenticationException {
        String username = token.getUsername();
        String password = new String(token.getPassword());
        String enterpriseName = token.getEnterpriseName();

        if (username == null || enterpriseName == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        Admin admin;
        SystemLog systemLog = new SystemLog();
        systemLog.setClientAddress(token.getHost());
        systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
        Long enterpriseId = null;
        LogOwner owner = new LogOwner();
        owner.setLoginName(username);
        try {
            saveLoginLog(token.getHost(), username);
            String captcha = token.getCaptcha();
            Session session = SecurityUtils.getSubject().getSession();
            owner.setIp(token.getHost());
            Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
            session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
            session.setAttribute("tag", true);
            if (StringUtils.isBlank(captcha) || captcha.length() != 4 || !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString())) {
                throw new IncorrectCaptchaException();
            }
            systemLog.setLoginName(username);
            systemLog.setShowName(username);
            admin = adminService.login(enterpriseName, username, password, systemLog, token);

            enterpriseId = admin.getEnterpriseId();
            owner.setEnterpriseId(enterpriseId);
        } catch (UserLockedWebException e) {
            logger.error("auth failed", e);
            systemLog.setLoginName(username);
            systemLog.setShowName(username);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] { username });
            throw e;
        } catch (AuthFailedException e) {
            logger.error("auth failed", e);
            systemLog.setShowName(username);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] { username });

            throw new AuthenticationException("Account auth failed [" + username + ']');
        } catch (DisabledUserException e) {
            logger.error("auth failed", e);
            systemLog.setShowName(username);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] { username });
            throw new AuthenticationException("Account disabled [" + username + ']');
        }

        if (!token.isNtlm()) {
            renewSession();
        }

        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.SESS_OBJ_KEY, admin.getId());
        session.setAttribute(Constants.SESS_ROLE_KEY, admin.getRoleNames());
        systemLog.setLoginName(username);
        systemLog.setShowName(username + "(" + admin.getName() + ")");
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN, new String[] { admin.getLoginName() });
        return new SimpleAuthenticationInfo(admin, password, getName());
    }

    protected AuthenticationInfo doGetAuthenticationInfo(WxWorkAuthCodeToken token) throws AuthenticationException {
        String loginName = null;
        Admin admin;
        SystemLog systemLog = new SystemLog();
        systemLog.setClientAddress(token.getHost());
        systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
        Long enterpriseId = null;
        LogOwner owner = new LogOwner();
        try {
            LoginInfo userInfo = wxOauth2Service.getLoginInfo(token.getAuthCode());
            if (userInfo == null) {
                logger.error("Failed to get user info from Weixin OAuth2 server, user info is null: authCode=" + token.getAuthCode());
                throw new AccountException("Failed to get user info from Weixin OAuth2 server.");
            }

            if(userInfo.hasError()) {
                logger.error("Failed to get user info from Weixin OAuth2 server: code={}, error={}, authCode={}", userInfo.getErrcode(), userInfo.getErrmsg(), token.getAuthCode());
                throw new AccountException("Failed to get user info from Weixin OAuth2 server.");
            }

            // 根据corpId查询内部企业ID
            String corpId = userInfo.getCorpInfo().getCorpid();
            loginName = userInfo.getUserInfo().getUserid();
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            if (wxEnterprise == null) {
                logger.error("No enterprise found: corpId={}", corpId);
                throw new AccountException("Enterprise doesn't exist");
            }

            owner.setLoginName(loginName);
            saveLoginLog(token.getHost(), loginName);

            Session session = SecurityUtils.getSubject().getSession();
            // owner.setIp(session.getHost());
            owner.setIp(token.getHost());
            session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
            session.setAttribute("tag", true);
            systemLog.setLoginName(loginName);
            systemLog.setShowName(loginName);

            //此处不检查用户是否锁定
            // 根据用户名+企业ID确认用户是否存在
            admin = adminService.getByLoginNameAndEnterpriseId(loginName, wxEnterprise.getBoxEnterpriseId());
            if (admin == null) {
                logger.error("user doesn't exist: name={}, enterpriseId={}" , loginName, wxEnterprise.getBoxEnterpriseId());
                throw new AuthFailedException("User doesn't exist.");
            }
            if (admin.getStatus() == Admin.STATUS_DISABLE) {
                logger.error("user disabled: " + admin.getName());
                throw new DisabledUserException();
            }
            HashSet<AdminRole> roles = admin.getRoles();
            if (!roles.contains(AdminRole.ENTERPRISE_MANAGER)) {
                logger.error("enterprise admin[{}] hasn't enterprise manager role." , loginName);
                throw new AuthFailedException("hasn't enterprise manager role.");
            }
            enterpriseId = admin.getEnterpriseId();
            owner.setEnterpriseId(enterpriseId);
        } catch (UserLockedWebException e) {
            logger.error("auth failed", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] {loginName});
            throw e;
        } catch (AuthFailedException e) {
            logger.error("auth failed", e);
            systemLog.setShowName(loginName);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] {loginName});

            throw new AuthenticationException("Account auth failed [" + loginName + ']');
        } catch (DisabledUserException e) {
            logger.error("auth failed", e);
            systemLog.setShowName(loginName);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_ERROR, new String[] {loginName});
            throw new AuthenticationException("Account disabled [" + loginName + ']');
        }

        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.SESS_OBJ_KEY, admin.getId());
        session.setAttribute(Constants.SESS_ROLE_KEY, admin.getRoleNames());
        systemLog.setLoginName(loginName);
        systemLog.setShowName(loginName + "(" + admin.getName() + ")");
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN, new String[] { admin.getLoginName() });
        return new SimpleAuthenticationInfo(admin, token.getPassword(), getName());
    }

    private void saveLoginLog(String host, String username)
    {
        SystemLog systemLoginLog = new SystemLog();
        systemLoginLog.setClientAddress(host);
        systemLoginLog.setClientDeviceName(EnvironmentUtils.getHostName());
        systemLoginLog.setLoginName(username);
        systemLoginLogService.create(systemLoginLog, OperateType.Login, new String[]{username});
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        //微信登录，不检查是否匹配, 其他情况继续原来的流程
        if(!(token instanceof WxWorkAuthCodeToken)) {
            super.assertCredentialsMatch(token, info);
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        SimpleAuthorizationInfo info = null;
        Object obj = principals.fromRealm(getName()).iterator().next();
        if (obj == null)
        {
            return null;
        }
        
        if (obj instanceof Admin)
        {
            Admin admin = (Admin) obj;
            info = new SimpleAuthorizationInfo();
            info.addRoles(roleSetToStrList(admin.getRoles()));
            return info;
        }
        
        if (!(obj instanceof SecurityPrincipal))
        {
            return null;
        }
        
        info = new SimpleAuthorizationInfo();
        SecurityPrincipal principal = (SecurityPrincipal) obj;
        
        SecurityMatrixObject matrix = securityMatrixBuilder.setSecurityPrincipal(principal).build();
        if (matrix == null)
        {
            return info;
        }
        String roleName = matrix.getRoleName();
        
        Set<String> havedpermission = matrix.getPermissions();
        if (roleName != null)
        {
            info.addRole(roleName);
        }
        if (!CollectionUtils.isEmpty(havedpermission))
        {
            info.addStringPermissions(havedpermission);
        }
        
        return info;
    }
    
    private List<String> roleSetToStrList(Set<AdminRole> roles)
    {
        List<String> roleList = new ArrayList<String>(10);
        for (AdminRole adminRole : roles)
        {
            roleList.add(adminRole.name());
        }
        return roleList;
    }
    
    private void renewSession()
    {
        Session sessionOld = SecurityUtils.getSubject().getSession(false);
        if (sessionOld != null)
        {
            Map<Object, Object> tmp = new HashMap<Object, Object>(10);
            for (Object key : sessionOld.getAttributeKeys())
            {
                tmp.put(key, sessionOld.getAttribute(key));
            }
            SecurityUtils.getSubject().logout();
            Session sessionNew = SecurityUtils.getSubject().getSession(true);
            for (Entry<Object, Object> entry : tmp.entrySet())
            {
                sessionNew.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
}
