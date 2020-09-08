package pw.cdmi.box.uam.user.shiro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.uam.exception.AuthFailedException;
import pw.cdmi.box.uam.exception.DisabledUserException;
import pw.cdmi.box.uam.exception.IncorrectCaptchaException;
import pw.cdmi.box.uam.exception.UserLockedWebException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.utils.EnvironmentUtils;

public class MyAuthorizingRealm extends AuthorizingRealm
{
    
    private static Logger logger = LoggerFactory.getLogger(MyAuthorizingRealm.class);
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
        throws AuthenticationException
    {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
        String username = token.getUsername();
        String password = new String(token.getPassword());
        String loginIP = token.getHost();
        if (username == null)
        {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        Admin admin;
        SystemLog systemLog = new SystemLog();
        systemLog.setClientAddress(token.getHost());
        systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
        try
        {
            String captcha = token.getCaptcha();
            Session session = SecurityUtils.getSubject().getSession();
            Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
            session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
            if (StringUtils.isBlank(captcha) || captcha.length() != 4
                || !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString()))
            {
                throw new IncorrectCaptchaException();
            }
            systemLog.setLoginName(username);
            systemLog.setShowName(username);
            adminService.checkUserLocked(username, systemLog);
            admin = adminService.login(username, password, systemLog, loginIP);
            if (null == admin)
            {
                throw new AuthFailedException();
            }
            if (admin.getStatus().byteValue() == Admin.STATUS_DISABLE)
            {
                logger.error("user disabled " + admin.getName());
                throw new DisabledUserException();
            }
        }
        catch (UserLockedWebException e)
        {
            logger.error("auth failed", e);
            systemLog.setLoginName(username);
            systemLog.setShowName(username);
            systemLogManager.saveFailLog(systemLog,
                OperateType.Login,
                OperateDescription.ADMIN_LOGIN_FAIL,
                null,
                new String[]{username});
            throw e;
        }
        catch (AuthFailedException e)
        {
            logger.error("auth failed", e);
            systemLog.setShowName(username);
            systemLogManager.saveFailLog(systemLog,
                OperateType.Login,
                OperateDescription.ADMIN_LOGIN_FAIL,
                null,
                new String[]{username});
            adminService.addUserLocked(username, systemLog);
            try
            {
                adminService.checkUserLocked(username, systemLog);
            }
            catch (UserLockedWebException e1)
            {
                logger.error("user locked", e1);
                throw e1;
            }
            throw new AuthenticationException("Account auth failed [" + username + ']', e);
        }
        catch (DisabledUserException e)
        {
            logger.error("auth failed", e);
            systemLog.setShowName(username);
            systemLogManager.saveFailLog(systemLog,
                OperateType.Login,
                OperateDescription.ADMIN_LOGIN_FAIL,
                null,
                new String[]{username});
            adminService.addUserLocked(username, systemLog);
            throw new AuthenticationException("Account disabled [" + username + ']', e);
        }
        if (!token.isNtlm())
        {
            renewSession();
        }
        
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.SESS_OBJ_KEY, admin.getId());
        session.setAttribute(Constants.SESS_ROLE_KEY, admin.getRoleNames());
        session.setAttribute("tag", true);
        systemLog.setLoginName(username);
        systemLog.setShowName(username + "(" + admin.getName() + ")");
        String id = systemLogManager.saveSuccessLog(systemLog,
            OperateType.Login,
            OperateDescription.ADMIN_LOGIN_SUCESS,
            null,
            new String[]{username});
        systemLogManager.updateSuccess(id);
        adminService.deleteUserLocked(username);
        return new SimpleAuthenticationInfo(admin, password, getName());
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        Object obj = principals.fromRealm(getName()).iterator().next();
        if (obj == null)
        {
            return null;
        }
        
        if (obj instanceof Admin)
        {
            Admin admin = (Admin) obj;
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRoles(roleSetToStrList(admin.getRoles()));
            return info;
        }
        return null;
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
            Map<Object, Object> tmp = new HashMap<Object, Object>(sessionOld.getAttributeKeys().size());
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
