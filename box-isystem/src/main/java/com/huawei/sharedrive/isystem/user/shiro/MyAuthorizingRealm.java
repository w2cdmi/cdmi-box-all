/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
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
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.isystem.exception.AuthFailedException;
import com.huawei.sharedrive.isystem.exception.UserDisabledException;
import com.huawei.sharedrive.isystem.exception.UserLockedException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.NtlmManagerService;
import com.huawei.sharedrive.isystem.util.Constants;

import pw.cdmi.common.log.UserLog;

/**
 * @author s00108907
 * 
 */
public class MyAuthorizingRealm extends AuthorizingRealm
{
    
    private static Logger logger = LoggerFactory.getLogger(MyAuthorizingRealm.class);
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private NtlmManagerService ntlmManager;
    
    @Autowired
    private CaptchaManager captchaManager;
    
    @Autowired
    private UserLogService userLogService;
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
        throws AuthenticationException
    {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
        String username = token.getUsername();
        String password = new String(token.getPassword());
        String loginIP = token.getHost();
        if (username == null || username.length() > 60)
        {
            throw new InvalidParameterException();
        }
        if (password.length() > 20)
        {
            throw new InvalidParameterException();
        }
        Admin admin = null;
        UserLog userLog = new UserLog();
        userLog.setType(UserLogType.LOGIN.getValue());
        userLog.setCreatedAt(new Date());
        userLog.setClientAddress(token.getHost());
        userLog.setId(UUID.randomUUID().toString());
        try
        {
            admin = doLogin(token, username, password, loginIP, userLog);
        }
        catch (UserLockedException e)
        {
            logger.error("auth failed", e);
            userLog.setLoginName(username);
            userLog.setLevel(UserLogService.FIAL_LEVEL);
            userLog.setType(UserLogType.LOGIN.getValue());
            userLog.setDetail(UserLogType.LOGIN.getErrorDetails(null));
            userLogService.saveUserLog(userLog);
            
            throw e;
        }
        catch (AuthFailedException e)
        {
            logger.error("auth failed", e);
            adminService.addUserLocked(username, userLog);
            userLog.setLoginName(username);
            userLog.setLevel(UserLogService.FIAL_LEVEL);
            userLog.setType(UserLogType.LOGIN.getValue());
            userLog.setDetail(UserLogType.LOGIN.getErrorDetails(null));
            userLogService.saveUserLog(userLog);
            try
            {
                adminService.checkUserLocked(username, userLog);
            }
            catch (UserLockedException t)
            {
                logger.error("user locked", t);
                throw t;
            }
            
            throw new AuthenticationException("Account auth failed [" + username + ']', e);
        }
        
        // setLocale(admin.getId());
        
        renewSession();
        
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.SESS_OBJ_KEY, admin.getId());
        session.setAttribute(Constants.SESS_ROLE_KEY, admin.getRoleNames());
        /** new add for user name */
        session.setAttribute(Constants.SESS_USER_NAME, admin.getName());
        session.setAttribute("tag", true);
        userLog.setLoginName(username);
        if (admin.getRoles() == null || admin.getRoles().size() <= 0)
        {
            userLog.setLevel(UserLogService.FIAL_LEVEL);
            userLog.setDetail(UserLogType.LOGIN_EMPTY_ROLE.getErrorDetails(null));
        }
        else
        {
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLog.setDetail(UserLogType.LOGIN.getDetails(null));
        }
        userLog.setType(UserLogType.LOGIN.getValue());
        userLogService.saveUserLog(userLog);
        
        // 鐧诲綍鎴愬姛鍚庯紝鍒犻櫎鐢ㄦ埛閿佸畾缂撳瓨
        ntlmManager.deleteUserLocked(username);
        admin.setName(HtmlUtils.htmlEscape(admin.getName()));
        return new SimpleAuthenticationInfo(admin, password, getName());
    }
    
    private Admin doLogin(UsernamePasswordCaptchaToken token, String username, String password,
        String loginIP, UserLog userLog)
    {
        captchaManager.validateCaptcha(token.getCaptcha());
        adminService.checkUserLocked(username, userLog);
        Admin admin = adminService.login(username, password, loginIP);
        if (null == admin)
        {
            throw new AuthFailedException();
        }
        if (admin.getStatus() == Admin.STATUS_DISABLE)
        {
            logger.error("Account disabled [" + username + ']');
            throw new UserDisabledException();
        }
        return admin;
    }
    
    /**
     * 鎺堟潈鏌ヨ鍥炶皟鍑芥暟, 杩涜閴存潈浣嗙紦瀛樹腑鏃犵敤鎴风殑鎺堟潈淇℃伅鏃惰皟鐢�
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        Admin admin = (Admin) principals.fromRealm(getName()).iterator().next();
        if (admin != null)
        {
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
            List<Object> list = new ArrayList<Object>(sessionOld.getAttributeKeys());
            Map<Object, Object> tmp = new HashMap<Object, Object>(list.size());
            for (Object key : list)
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
