package pw.cdmi.box.uam.authorize.web;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.manager.AdminManager;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.FormValidateUtil;
import pw.cdmi.box.uam.util.PasswordGenerateUtil;

@Controller
@RequestMapping(value = "/sys/authorize/role")
@SuppressWarnings({"rawtypes", "unchecked"})
public class AuthorizeManageController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(AuthorizeManageController.class);
    
    private static final String LOCAL_ZH_CN = "zh_CN";
    
    private static final String LOCAL_ZH = "zh";
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminManager adminManager;
    
    @Autowired
    private MailServerService mailServerService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        return "sys/authorizeManage/authorizeMain";
    }
    
    private String roleToI18n(Set<AdminRole> roles, Locale locale)
    {
        
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
                buffer.append('、' + roleI18n);
            }
        }
        return buffer == null ? "" : buffer.toString();
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model, HttpServletRequest req)
    {
        Locale locale = Locale.CHINA;
        locale = RequestContextUtils.getLocaleResolver(req).resolveLocale(req);
        String language = locale.getLanguage();
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        Admin admin = new Admin();
        admin.setType(Admin.ROLE_MANAGER);
        Page<Admin> userPage = adminService.getFilterd(admin, request);
        List<Admin> admins = new ArrayList<Admin>(1);
        long allCount = 0;
        if (userPage != null)
        {
            List<Admin> adminList = userPage.getContent();
            for (Admin selAdmin : adminList)
            {
                selAdmin.setPassword(roleToI18n(selAdmin.getRoles(), locale));
                
            }
            
            allCount = adminList.size();
            
        }
        Admin.htmlEscape(userPage == null ? admins : userPage.getContent());
        model.addAttribute("userPage", userPage);
        model.addAttribute("adminAllCount", allCount);
        return "sys/authorizeManage/adminList";
    }
    
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(String filter, Integer page, Model model, HttpServletRequest req, String token)
    
    {
        super.checkToken(token);
        Locale locale = Locale.CHINA;
        locale = RequestContextUtils.getLocaleResolver(req).resolveLocale(req);
        String language = locale.getLanguage();
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        Admin admin = new Admin();
        admin.setType(Admin.ROLE_MANAGER);
        if (StringUtils.isNotBlank(filter))
        {
            admin.setLoginName(filter);
            admin.setName(filter);
            admin.setEmail(filter);
            model.addAttribute("filter", HtmlUtils.htmlEscape(filter));
        }
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        Page<Admin> userPage = adminService.getFilterd(admin, request);
        List<Admin> admins = new ArrayList<Admin>(0);
        if (userPage != null)
        {
            List<Admin> adminList = userPage.getContent();
            for (Admin selAdmin : adminList)
            {
                selAdmin.setPassword(roleToI18n(selAdmin.getRoles(), locale));
                
            }
        }
        Admin allAdmin = new Admin();
        allAdmin.setType(Admin.ROLE_MANAGER);
        long userCount = adminService.getAll(allAdmin);
        Admin.htmlEscape((userPage == null ? admins : userPage.getContent()));
        model.addAttribute("userPage", userPage);
        model.addAttribute("adminAllCount", userCount);
        return "sys/authorizeManage/adminList";
    }
    
    @RequestMapping(value = "disableAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> disableAdmin(String ids, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        
        String[] idArray = ids.split(",");
        Admin admin;
        try
        {
            String logId;
            String[] description;
            for (String id : idArray)
            {
                if (StringUtils.equals(id, "0"))
                {
                    throw new InvalidParamterException("id exception id is " + id);
                }
                admin = adminService.get(Long.parseLong(id));
                if (null == admin)
                {
                    logger.error("get userinfo failed,user is null");
                    return new ResponseEntity<String>("userNotExist", HttpStatus.NOT_FOUND);
                }
                description = new String[]{admin.getLoginName()};
                logId = systemLogManager.saveFailLog(request,
                    OperateType.AdminDisable,
                    OperateDescription.DESCRIPTION_DISABLE_ADMIN,
                    null,
                    description);
                adminService.updateStatus(Admin.STATUS_DISABLE, Long.parseLong(id));
                systemLogManager.updateSuccess(logId);
            }
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException("String to Long exception");
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "enableAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> enableAdmin(String ids, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        
        String[] idArray = ids.split(",");
        Admin admin;
        try
        {
            String logId;
            String[] description;
            for (String id : idArray)
            {
                
                if (StringUtils.equals(id, "0"))
                {
                    throw new InvalidParamterException("id exception id is " + id);
                }
                admin = adminService.get(Long.parseLong(id));
                if (null == admin)
                {
                    logger.error("get userinfo failed,user is null");
                    return new ResponseEntity<String>("userNotExist", HttpStatus.NOT_FOUND);
                }
                description = new String[]{admin.getLoginName()};
                logId = systemLogManager.saveFailLog(request,
                    OperateType.AdminEnable,
                    OperateDescription.DESCRIPTION_ENABLE_ADMIN,
                    null,
                    description);
                adminService.updateStatus(Admin.STATUS_ENABLE, Long.parseLong(id));
                systemLogManager.updateSuccess(logId);
            }
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException("String to Long exception");
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(int id, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        Admin tempAdmin = adminService.get((long) id);
        Admin admin = null;
        if (tempAdmin != null)
        {
            admin = tempAdmin;
        }
        String loginName = (admin != null ? admin.getLoginName() : null);
        String logId = systemLogManager.saveFailLog(request,
            OperateType.DeleteAdmin,
            OperateDescription.DELETE_ADMIN,
            null,
            new String[]{loginName});
        super.checkToken(token);
        if (id < 1)
        {
            systemLogManager.saveFailLog(request,
                OperateType.DeleteAdmin,
                OperateDescription.CHECKCONFIG,
                null,
                null);
            throw new InvalidParamterException("id exception id is " + id);
        }
        adminService.delete(id);
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteList", method = RequestMethod.POST)
    public ResponseEntity<?> delete(String ids, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String[] idArray = ids.split(",");
        Admin admin = null;
        try
        {
            Admin tempAdmin = null;
            String logId;
            String[] description;
            for (String id : idArray)
            {
                if (StringUtils.equals(id, "0"))
                {
                    throw new InvalidParamterException("id exception id is " + id);
                }
                tempAdmin = adminService.get(Long.parseLong(id));
                if (tempAdmin != null)
                {
                    admin = tempAdmin;
                }
                if (admin == null)
                {
                    logger.error("get userinfo failed,user is null");
                    return new ResponseEntity<String>("userNotExist", HttpStatus.NOT_FOUND);
                }
                description = new String[]{admin.getLoginName()};
                logId = systemLogManager.saveFailLog(request,
                    OperateType.DeleteAdmin,
                    OperateDescription.DELETE_ADMIN,
                    null,
                    description);
                adminService.delete(Long.parseLong(id));
                systemLogManager.updateSuccess(logId);
            }
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException("String to Long exception");
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createAdmin", method = RequestMethod.GET)
    public String enterCreateLocal(Model model)
    {
        return "sys/authorizeManage/createAdminUser";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "createLocal", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createLocal(Admin admin, HttpServletRequest request, String token)
        throws IOException
    
    {
        Set violations = validator.validate(admin);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw new ConstraintViolationException(violations);
        }
        try
        {
            checkAdminRole(admin);
        }
        catch (ConstraintViolationException e)
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw e;
        }
        catch (InvalidParamterException e)
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw e;
        }
        
        super.checkToken(token);
        String roles = null;
        if (admin.getRoles() != null && admin.getRoles().size() != 0)
        {
            roles = admin.getRoles().toString();
        }
        String[] description = new String[]{admin.getLoginName(), admin.getName(), admin.getEmail(),
            admin.getNoteDesc(), roles};
        String id = systemLogManager.saveFailLog(request,
            OperateType.CreateLocalAdmin,
            OperateDescription.CREATE_ADMIN,
            null,
            description);
        MailServer mailServer = mailServerService.getDefaultMailServer();
        if (mailServer == null)
        {
            return new ResponseEntity<String>("MailServerNotExist", HttpStatus.BAD_REQUEST);
        }
        String randomPassword = PasswordGenerateUtil.getRandomPassword();
        admin.setPassword(randomPassword);
        admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);
        admin.setType(Admin.ROLE_MANAGER);
        adminManager.create(admin);
        systemLogManager.updateSuccess(id);
        String link = Constants.SERVICE_URL + "login";
        admin.setPassword(randomPassword);
        sendEmail(admin, link);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private void checkAdminRole(Admin admin)
    {
        if (admin.getRoles() == null || admin.getRoles().size() == 0)
        {
            throw new ConstraintViolationException(null);
        }
        String roleName = admin.getRoleNames();
        String[] splitRoleName = roleName.split(",");
        for (String str : splitRoleName)
        {
            if (!Constants.ROLE_OF_ADMIN_ACCOUNT_APP_MANAGER.equals(str)
                && !Constants.ROLE_OF_ADMIN_ACCOUNT_ENTERPRISE_BUSINESS_MANAGER.equals(str)
                && !Constants.ROLE_OF_ADMIN_ACCOUNT_SYSTEM_CONFIG.equals(str)
                && !Constants.ROLE_OF_ADMIN_ACCOUNT_ANNOUNCEMENT_MANAGER.equals(str)
                && !Constants.ROLE_OF_ADMIN_ACCOUNT_STATISTICS_MANAGER.equals(str)
                && !Constants.ROLE_OF_ADMIN_ACCOUNT_JOB_MANAGER.equals(str)
            	&& !Constants.ROLE_OF_ADMIN_ACCOUNT_FEEDBACK_MANAGER.equals(str))
            {
                throw new InvalidParamterException("invalid admin role");
            }
        }
    }
    
    private void sendEmail(Admin admin, String link) throws IOException
    {
        MailServer mailServer = mailServerService.getDefaultMailServer();
        if (mailServer == null)
        {
            throw new BusinessException();
        }
        Map<String, Object> messageModel = new HashMap<String, Object>(3);
        messageModel.put("username", admin.getName());
        messageModel.put("loginName", admin.getLoginName());
        messageModel.put("password", admin.getPassword());
        messageModel.put("link", link);
        String msg = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_SUBJECT,
            new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(mailServer.getId(), admin.getEmail(), null, null, subject, msg);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.GET)
    public String enterModify(long id, String roles, Model model)
    {
        Admin admin = adminService.get(id);
        setRoleToMode(model, roles.substring(1, roles.length() - 1));
        model.addAttribute("admin", admin);
        return "sys/authorizeManage/modifyAdminUser";
    }
    
    private void setRoleToMode(Model model, String roles)
    {
        String[] roleArray = roles.split(",");
        String temp = null;
        for (String tempRole : roleArray)
        {
            temp = StringUtils.trimToEmpty(tempRole);
            if (AdminRole.APP_MANAGER.name().equals(temp))
            {
                model.addAttribute("appmanagerChecked", true);
            }
            if (AdminRole.ENTERPRISE_BUSINESS_MANAGER.name().equals(temp))
            {
                model.addAttribute("enterpriseChecked", true);
            }
            if (AdminRole.SYSTEM_CONFIG.name().equals(temp))
            {
                model.addAttribute("systemconfigChecked", true);
            }
            if (AdminRole.STATISTICS_MANAGER.name().equals(temp))
            {
                model.addAttribute("statisticalChecked", true);
            }
            if (AdminRole.JOB_MANAGER.name().equals(temp))
            {
                model.addAttribute("jobManageChecked", true);
            }
            if (AdminRole.ANNOUNCEMENT_MANAGER.name().equals(temp))
            {
                model.addAttribute("announcementManageChecked", true);
            }
            if (AdminRole.FEEDBACK_MANAGER.name().equals(temp))
            {
            	model.addAttribute("feedbackManageChecked", true);
            }
        }
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> modify(Admin admin, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String roles = null;
        
        Set violations = validator.validate(admin);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw new ConstraintViolationException(violations);
        }
        if (!FormValidateUtil.isValidEmail(admin.getEmail()))
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        try
        {
            checkAdminRole(admin);
        }
        catch (ConstraintViolationException e)
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw e;
        }
        catch (InvalidParamterException e)
        {
            saveValidateLog(request, OperateType.CreateLocalAdmin);
            throw e;
        }
        
        if (admin.getRoles() != null && admin.getRoles().size() != 0)
        {
            roles = admin.getRoles().toString();
        }
        String[] description = new String[]{admin.getLoginName(), admin.getName(), admin.getEmail(),
            admin.getNoteDesc(), roles};
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeAdmin,
            OperateDescription.MODIFY_ADMIN,
            null,
            description);
        if (admin.getId() < 1)
        {
            throw new InvalidParamterException(" admin id Exception");
        }
        adminService.updateAdmin(admin);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
}
