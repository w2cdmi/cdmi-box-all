/**
 * 
 */
package com.huawei.sharedrive.isystem.authorize.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.syslog.service.impl.UserLogServiceImpl;
import com.huawei.sharedrive.isystem.system.LogListener;
import com.huawei.sharedrive.isystem.system.service.MailServerService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.isystem.util.PasswordGenerateUtil;
import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;
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
public class AuthorizeManageController extends AbstractCommonController
{
    /**
     * 初次设置密码消息体
     */
    private static final String INITSET_PWD_MAIL_CONTENT = "initSetPasswordContent.ftl";
    
    /**
     * 初次设置密码主题
     */
    private static final String INITSET_PWD_MAIL_SUBJECT = "initSetPasswordSubject.ftl";
    
    private static final String LOCAL_ZH = "zh";
    
    private static final String LOCAL_ZH_CN = "zh_CN";
    
    private static Logger logger = LoggerFactory.getLogger(AuthorizeManageController.class);
    
    
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
    private AuthAppService authAppService;
    
    @Autowired
    private MailServerService mailServerService;
    
    /**
     * 创建Local管理员
     * 
     * @param admin
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @RequestMapping(value = "createLocal", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createLocal(Admin admin, HttpServletRequest request, String token)
        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
    {
        String logrol = "";
        if (admin.getRoles() != null)
        {
            if (admin.getRoles().contains(AdminRole.STATISTIC_MANAGER))
            {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            logrol = roleToI18n(admin.getRoles(), LogListener.getLanguage());
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.CREATE_ADMIN,
            new String[]{admin.getLoginName(), logrol});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        if (!validCreateParams(admin))
        {
            userLog.setDetail(UserLogType.CREATE_ADMIN.getErrorDetails(new String[]{admin.getLoginName(),
                logrol}));
            userLog.setType(UserLogType.CREATE_ADMIN.getValue());
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);
        String randomPassword = PasswordGenerateUtil.getRandomPassword();
        boolean sendMail = false;
        if (StringUtils.isEmpty(admin.getPassword()))
        {
            sendMail = true;
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(randomPassword);
            admin.setPassword(hashPassword.getHashPassword());
            admin.setIterations(hashPassword.getIterations());
            admin.setSalt(hashPassword.getSalt());
        }
        else
        {
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(admin.getPassword());
            admin.setPassword(hashPassword.getHashPassword());
            admin.setIterations(hashPassword.getIterations());
            admin.setSalt(hashPassword.getSalt());
        }
        
        adminService.create(admin);
        
        if (sendMail)
        {
            String type = UserLogType.CREATE_ADMIN.getType(null);
            UserLog userLog1 = userLogService.initUserLog(request, UserLogType.SEND_MIAL_ADMIN, new String[]{
                type, admin.getLoginName(), admin.getEmail()});
            userLog1.setDetail(UserLogType.SEND_MIAL_ADMIN.getDetails(new String[]{type,
                admin.getLoginName(), admin.getEmail()}));
            userLog1.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.saveUserLog(userLog1);
            String link = PropertiesUtils.getServiceUrl() + "login";
            admin.setPassword(randomPassword);
            sendEmail(admin, link);
            
        }
        userLog.setDetail(UserLogType.CREATE_ADMIN.getDetails(new String[]{admin.getLoginName(), logrol}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 根据ID删除指定的管理员
     * 
     * @param id 要删除的管理员ID
     * @return 删除结果
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(long id, HttpServletRequest request, String token)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DELETE_ADMIN, null);
        userLog.setDetail(UserLogType.DELETE_ADMIN.getErrorDetails(new String[]{userLog.getLoginName(),
            id + ""}));
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        if (id < 1L)
        {
            userLog.setDetail(UserLogType.DELETE_ADMIN.getErrorDetails(new String[]{userLog.getLoginName(),
                id + ""}));
            userLog.setType(UserLogType.DELETE_ADMIN.getValue());
            userLogService.update(userLog);
            logger.warn("delete admin id is " + id);
            throw new InvalidParameterException("id exception");
        }
        Admin admin = adminService.get(id);
        if (admin == null)
        {
            userLog.setDetail(UserLogType.DELETE_ADMIN.getErrorDetails(new String[]{userLog.getLoginName(),
                id + ""}));
            userLog.setType(UserLogType.DELETE_ADMIN.getValue());
            userLogService.update(userLog);
            return new ResponseEntity("noSuch", HttpStatus.BAD_REQUEST);
        }
        List<AuthApp> appList = authAppService.getAuthAppList(null, null, null);
        String authAppId = null;
        
        for (AuthApp authApp : appList)
        {
            if (StringUtils.equals(authApp.getCreateBy(), String.valueOf(admin.getId())))
            {
                authAppId = authApp.getAuthAppId();
                break;
            }
            
        }
        if (authAppId != null)
        {
            return new ResponseEntity("appExist", HttpStatus.BAD_REQUEST);
        }
        
        adminService.delete(id);
        userLog.setDetail(UserLogType.DELETE_ADMIN.getDetails(new String[]{userLog.getLoginName(),
            admin.getLoginName()}));
        userLog.setLevel(UserLogServiceImpl.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "authorizeManage/authorizeMain";
    }
    
    /**
     * 进入AD管理员创建页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String enterCreate(Model model)
    {
        return "authorizeManage/createAdAdminUser";
    }
    
    /**
     * 进入管理员创建页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createAdmin", method = RequestMethod.GET)
    public String enterCreateLocal(Model model)
    {
        return "authorizeManage/createAdminUser";
    }
    
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model, HttpServletRequest request)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        Admin filter = new Admin();
        filter.setType(Constants.ROLE_COMMON_ADMIN);
        List<Admin> adminList = adminService.getFilterd(filter, null, null);
        for (Admin admin : adminList)
        {
            admin.setPassword(roleToI18n(admin.getRoles(), locale));
            
        }
        model.addAttribute("adminList", adminList);
        return "authorizeManage/adminList";
    }
    
    @RequestMapping(value = "search", method = {RequestMethod.POST})
    public String searchAdmins(Byte selectStatus, String searchKey, Model model, HttpServletRequest request,
        String token)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        
        super.checkToken(token);
        
        Admin filter = new Admin();
        filter.setType(Constants.ROLE_COMMON_ADMIN);
        if (null != selectStatus && (selectStatus == 1 || selectStatus == 0))
        {
            filter.setStatus(selectStatus);
        }
        if (StringUtils.isNotBlank(searchKey))
        {
            filter.setLoginName(searchKey);
            filter.setName(searchKey);
            filter.setEmail(searchKey);
        }
        List<Admin> adminList = adminService.getFilterd(filter, null, null);
        for (Admin admin : adminList)
        {
            admin.setPassword(roleToI18n(admin.getRoles(), locale));
            
        }
        model.addAttribute("selectStatus", selectStatus);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("adminList", adminList);
        model.addAttribute("searchedList", "true");
        return "authorizeManage/adminList";
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
    
    private void sendEmail(Admin admin, String link) throws IOException
    {
        Map<String, Object> messageModel = new HashMap<String, Object>(3);
        messageModel.put("username", admin.getName());
        messageModel.put("loginName", admin.getLoginName());
        messageModel.put("password", admin.getPassword());
        messageModel.put("link", link);
        String msg = mailServerService.getEmailMsgByTemplate(INITSET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(INITSET_PWD_MAIL_SUBJECT,
            new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(admin.getEmail(), null, null, subject, msg);
    }
    
    private boolean validCreateParams(Admin admin)
    {
        Set violations = validator.validate(admin);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        if (!FormValidateUtil.isValidEmail(admin.getEmail()))
        {
            return false;
        }
        if (StringUtils.isNotEmpty(admin.getPassword()))
        {
            if (!PasswordValidateUtil.isValidPassword(admin.getPassword()))
            {
                return false;
            }
        }
        if (!FormValidateUtil.isValidLoginName(admin.getLoginName()))
        {
            return false;
        }
        return true;
    }
}
