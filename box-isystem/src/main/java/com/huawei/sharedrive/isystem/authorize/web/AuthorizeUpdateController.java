/**
 * 
 */
package com.huawei.sharedrive.isystem.authorize.web;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.NoSuchUserException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.syslog.service.impl.UserLogServiceImpl;
import com.huawei.sharedrive.isystem.system.LogListener;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.ExceptionUtil;
import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

/**
 * 
 * 
 * 授权管理
 * 
 * @author d00199602
 * 
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Controller
@RequestMapping(value = "/authorize/role")
public class AuthorizeUpdateController extends AbstractCommonController
{
    private static final String LOCAL_ZH = "zh";
    
    private static final String LOCAL_ZH_CN = "zh_CN";
    
    private static Logger logger = LoggerFactory.getLogger(AuthorizeUpdateController.class);
    
    private static String getFenString()
    {
        try
        {
            return new String(new byte[]{-29, -128, -127}, "utf8");
        }
        catch (UnsupportedEncodingException e)
        {
            return ".";
        }
    }
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminUpdateService adminUpdateService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @RequestMapping(value = "disableAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> disableAdmin(String ids, HttpServletRequest request, String token)
        throws NoSuchUserException
    {
        
        super.checkToken(token);
        if (StringUtils.isBlank(ids))
        {
            throw new InvalidParameterException("null ids");
        }
        String[] idArray = ids.split(",");
        Admin admin = null;
        UserLog userLog = null;
        for (String id : idArray)
        {
            admin = adminService.get(Long.parseLong(id));
            userLog = userLogService.initUserLog(request, UserLogType.DISABLE_ADMIN, null);
            userLog.setDetail(UserLogType.DISABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                id)));
            userLogService.saveUserLog(userLog);
            if (Long.parseLong(id) < 1)
            {
                userLog.setDetail(UserLogType.DISABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                    id)));
                userLog.setType(UserLogType.DISABLE_ADMIN.getValue());
                userLogService.update(userLog);
                logger.warn("disableAdmin admin id is " + id);
                throw new NoSuchUserException();
            }
            
            if (null == admin)
            {
                userLog.setDetail(UserLogType.DISABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                    id)));
                userLog.setType(UserLogType.DISABLE_ADMIN.getValue());
                userLogService.update(userLog);
                logger.error("get userinfo failed,user is null");
                throw new NoSuchUserException();
            }
            
            adminUpdateService.updateStatus(Admin.STATUS_DISABLE, Long.parseLong(id));
            userLog.setDetail(UserLogType.DISABLE_ADMIN.getDetails(getStringArray(userLog.getLoginName(),
                admin.getLoginName())));
            userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
            userLogService.update(userLog);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "doResetAdminPwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doResetAdminPwd(Admin inputAdmin, HttpServletRequest request, String token)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.CHANGE_PWD_ADMIN, null);
        userLog.setDetail(UserLogType.CHANGE_PWD_ADMIN.getErrorDetails(new String[]{userLog.getLoginName(),
            inputAdmin.getLoginName()}));
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        Admin superAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        if (superAdmin.getId() == inputAdmin.getId())
        {
            userLog.setDetail(UserLogType.CHANGE_PWD_ADMIN.getErrorDetails(new String[]{
                userLog.getLoginName(), inputAdmin.getLoginName()}));
            userLog.setType(UserLogType.CHANGE_PWD_ADMIN.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("superAdmin conn't reset own password");
        }
        
        Admin admin = adminService.get(inputAdmin.getId());
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            userLog.setDetail(UserLogType.CHANGE_PWD_ADMIN.getErrorDetails(new String[]{
                userLog.getLoginName(), inputAdmin.getLoginName()}));
            userLog.setType(UserLogType.CHANGE_PWD_ADMIN.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("password regex");
        }
        if (inputAdmin.getId() < 1)
        {
            userLog.setDetail(UserLogType.CHANGE_PWD_ADMIN.getErrorDetails(new String[]{
                userLog.getLoginName(), inputAdmin.getLoginName()}));
            userLog.setType(UserLogType.CHANGE_PWD_ADMIN.getValue());
            userLogService.update(userLog);
            logger.warn("reset password excption admin id is" + inputAdmin.getId());
            throw new InvalidParameterException("admin id exception");
        }
        try
        {
            adminUpdateService.changePwdBySuperAdmin(inputAdmin);
        }
        catch (Exception e)
        {
            return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e), HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.CHANGE_PWD_ADMIN.getDetails(new String[]{userLog.getLoginName(),
            admin.getLoginName()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "enableAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> enableAdmin(String ids, HttpServletRequest request, String token)
        throws NoSuchUserException
    {
        super.checkToken(token);
        if (StringUtils.isBlank(ids))
        {
            throw new InvalidParameterException("null ids");
        }
        String[] idArray = ids.split(",");
        Admin admin = null;
        UserLog userLog = null;
        for (String id : idArray)
        {
            userLog = userLogService.initUserLog(request, UserLogType.ENABLE_ADMIN, null);
            userLog.setDetail(UserLogType.ENABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                id)));
            userLogService.saveUserLog(userLog);
            if (Long.parseLong(id) < 1)
            {
                userLog.setDetail(UserLogType.ENABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                    id)));
                userLog.setType(UserLogType.ENABLE_ADMIN.getValue());
                userLogService.update(userLog);
                logger.warn("enableAdmin admin id is " + id);
                throw new NoSuchUserException();
            }
            admin = adminService.get(Long.parseLong(id));
            
            if (null == admin)
            {
                userLog.setDetail(UserLogType.ENABLE_ADMIN.getErrorDetails(getStringArray(userLog.getLoginName(),
                    id)));
                userLog.setType(UserLogType.ENABLE_ADMIN.getValue());
                userLogService.update(userLog);
                logger.error("get userinfo failed,user is null");
                throw new NoSuchUserException();
            }
            
            adminUpdateService.updateStatus(Admin.STATUS_ENABLE, Long.parseLong(id));
            userLog.setDetail(UserLogType.ENABLE_ADMIN.getDetails(getStringArray(userLog.getLoginName(),
                admin.getLoginName())));
            userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
            userLogService.update(userLog);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 进入AD管理员修改角色页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.GET)
    public String enterModify(long id, String loginName, String roles, Model model)
    {
        setRoleToModel(model, roles.substring(1, roles.length() - 1));
        model.addAttribute("id", id);
        model.addAttribute("loginName", loginName);
        return "authorizeManage/modifyAdAdminUser";
    }
    
    /**
     * 修改AD管理员角色
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> modify(long id, Admin admin, HttpServletRequest request, String token)
    {
        String role = "";
        if (admin.getRoles() != null)
        {
            if (admin.getRoles().contains(AdminRole.STATISTIC_MANAGER))
            {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            role = roleToI18n(admin.getRoles(), LogListener.getLanguage());
            if ("".equals(role))
            {
                throw new InvalidParameterException("roles Excetpion");
            }
        }
        
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.MODIFY_ADMIN,
            new String[]{admin.getLoginName(), role});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        if (admin.getId() < 1)
        {
            userLog.setDetail(UserLogType.MODIFY_ADMIN.getErrorDetails(new String[]{admin.getLoginName(),
                role}));
            userLog.setType(UserLogType.MODIFY_ADMIN.getValue());
            userLogService.update(userLog);
            logger.warn("modify admin id is " + admin.getId());
            throw new InvalidParameterException("admin id exception");
        }
        Set<AdminRole> roles = admin.getRoles();
        if (roles != null && roles.contains(AdminRole.ADMIN_MANAGER))
        {
            userLog.setDetail(UserLogType.MODIFY_ADMIN.getErrorDetails(new String[]{admin.getLoginName(),
                role}));
            userLog.setType(UserLogType.MODIFY_ADMIN.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("roles Excetpion");
        }
        List<AuthApp> appList = authAppService.getAuthAppList(null, null, null);
        Admin adm = adminService.get(id);
        String authAppId = null;
        if (roles != null)
        {
            authAppId = getCreatedAppId(roles, appList, adm);
        }
        if (authAppId != null)
        {
            return new ResponseEntity("appExist", HttpStatus.BAD_REQUEST);
        }
        
        adminUpdateService.updateRoles(admin.getId(), admin.getRoles());
        admin = adminService.get(admin.getId());
        userLog.setDetail(UserLogType.MODIFY_ADMIN.getDetails(new String[]{admin.getLoginName(), role}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "resetAdminPwd", method = RequestMethod.GET)
    public String resetAdminPwd(Model model, int id)
    {
        model.addAttribute("id", id);
        return "authorizeManage/resetAdminPwd";
    }
    
    private String getCreatedAppId(Set<AdminRole> roles, List<AuthApp> appList, Admin adm)
    {
        String authAppId = null;
        for (AuthApp authApp : appList)
        {
            if (authApp.getCreateBy() != null && !roles.contains(AdminRole.APP_MANAGER))
            {
                if (authApp.getCreateBy().equals(String.valueOf(adm.getId())))
                {
                    authAppId = authApp.getAuthAppId();
                    break;
                }
            }
        }
        return authAppId;
    }
    
    private String[] getStringArray(String... args)
    {
        return args;
    }
    
    private String roleToI18n(Set<AdminRole> roles, Locale locale)
    {
        
        String language = locale.getLanguage();
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        StringBuffer buffer = null;
        String roleI18n = null;
        for (AdminRole role : roles)
        {
            roleI18n = messageSource.getMessage(role.name(), null, locale);
            if (buffer == null)
            {
                buffer = new StringBuffer();
                buffer.append(roleI18n);
            }
            else
            {
                buffer.append(getFenString());
                buffer.append(roleI18n);
            }
        }
        return buffer == null ? "" : buffer.toString();
    }
    
    private void setRoleToModel(Model model, String roles)
    {
        String[] roleArray = roles.split(",");
        String temp = null;
        for (String tmpRole : roleArray)
        {
            temp = StringUtils.trimToEmpty(tmpRole);
            if (AdminRole.CLUSTER_MANAGER.name().equals(temp))
            {
                model.addAttribute("clusterChecked", true);
            }
            if (AdminRole.APP_MANAGER.name().equals(temp))
            {
                model.addAttribute("appManageChecked", true);
            }
            if (AdminRole.SYSCONFIG_MANAGER.name().equals(temp))
            {
                model.addAttribute("sysconfigChecked", true);
            }
            if (AdminRole.LOG_MANAGER.name().equals(temp))
            {
                model.addAttribute("logManageChecked", true);
            }
            if (AdminRole.JOB_MANAGER.name().equals(temp))
            {
                model.addAttribute("jobManageChecked", true);
            }
            if (AdminRole.STATISTIC_MANAGER.name().equals(temp))
            {
                model.addAttribute("statisticManageChecked", true);
            }
        }
    }
    
}
