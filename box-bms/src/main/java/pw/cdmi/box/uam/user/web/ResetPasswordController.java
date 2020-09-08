package pw.cdmi.box.uam.user.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
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

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.FormValidateUtil;
import pw.cdmi.box.uam.util.PasswordValidateUtil;
import pw.cdmi.box.uam.util.PropertiesUtils;
import pw.cdmi.box.uam.util.RandomKeyGUID;
import pw.cdmi.box.uam.util.custom.ForgetPwdUtils;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/syscommon")
public class ResetPasswordController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordController.class);
    
    private static final int RESET_LINK_EXPRISE_TIME = Integer.parseInt(PropertiesUtils.getProperty("session.expire",
        "600000"));
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private MailServerService mailServerService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "enterforget", method = RequestMethod.GET)
    public String enterForgetPwd()
    {
        if (!ForgetPwdUtils.enableForget())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        return "anon/forgetPwd";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "sendlink", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sendResetLink(String loginName, String captcha, String email,
        String token, HttpServletRequest request) throws IOException
    {
        super.checkToken(token);
        if (!ForgetPwdUtils.enableForget())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(captcha)
            || !FormValidateUtil.isValidEmail(email))
        {
            throw new BadRquestException();
        }
        Session session = SecurityUtils.getSubject().getSession();
        Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
        session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
        if (captcha.length() != 4
            || !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString()))
        {
            throw new BadRquestException();
        }
        Admin admin = adminService.getAdminByLoginName(loginName);
        if (admin == null)
        {
            throw new BadRquestException();
        }
        if (admin.getDomainType() != Constants.DOMAIN_TYPE_LOCAL)
        {
            throw new BadRquestException();
        }
        if (admin.getType() == Constants.ROLE_ENTERPRISE_ADMIN)
        {
            throw new BadRquestException();
        }
        if (!email.equals(admin.getEmail()))
        {
            throw new BadRquestException();
        }
        
        String[] description = new String[]{admin.getLoginName(), admin.getEmail()};
        SystemLog systemLog = new SystemLog();
        systemLog.setClientAddress(IpUtils.getClientAddress(request));
        systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
        systemLog.setLoginName(admin.getLoginName());
        systemLog.setShowName(admin.getLoginName() + "(" + admin.getName() + ")");
        String id = systemLogManager.saveFailLog(systemLog,
            OperateType.ResetPassword,
            OperateDescription.FORGET_PWD,
            null,
            description);
        
        String validateKey = RandomKeyGUID.getSecureRandomGUID();
        
        Map<String, String> map = EDToolsEnhance.encode(validateKey);
        LOGGER.info("change crypt in bms.ResetPassword");
        adminService.updateValidKeyAndDynamicPwd(admin.getId(),
            DigestUtil.digestPassword(validateKey),
            map.get(EDToolsEnhance.ENCRYPT_KEY));
        String link = Constants.SERVICE_URL + "syscommon/reset?name=" + loginName + "&validateKey="
            + map.get(EDToolsEnhance.ENCRYPT_CONTENT);
        sendEmail(admin, link);
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "initset", method = RequestMethod.GET)
    public String initSetPwd(Model model, String name, String validateKey)
    {
        validateLink(model, name, validateKey);
        return "anon/initset";
    }
    
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String resetPwd(Model model, String name, String validateKey)
    {
        if (!ForgetPwdUtils.enableForget())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        validateLink(model, name, validateKey);
        return "anon/reset";
    }
    
    private void validateLink(Model model, String name, String validateKey)
    {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(validateKey))
        {
            throw new BadRquestException();
        }
        String loginName = name;
        Admin admin = adminService.getAdminByLoginName(loginName);
        if (admin == null || admin.getValidateKey() == null)
        {
            throw new BadRquestException();
        }
        if (Constants.ROLE_ENTERPRISE_ADMIN == admin.getType())
        {
            throw new BadRquestException();
        }
        
        String realValidateKey = EDToolsEnhance.decode(validateKey, admin.getDynamicPassword());
        if (!admin.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey)))
        {
            throw new BadRquestException();
        }
        
        Date modifiedDate = admin.getResetPasswordAt();
        long modifiedDateSeconds = 0;
        if (null != modifiedDate)
        {
            modifiedDateSeconds = modifiedDate.getTime();
        }
        long lockDateSeconds = new Date().getTime() - modifiedDateSeconds;
        if (lockDateSeconds > RESET_LINK_EXPRISE_TIME)
        {
            throw new BadRquestException();
        }
        
        model.addAttribute("loginName", name);
        model.addAttribute("validateKey", validateKey);
    }
    
    @RequestMapping(value = "doinitset", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> doInitSet(Admin inputAdmin, String token) throws BadRquestException
    {
        super.checkToken(token);
        Admin localAdmin = validateNameAndKey(inputAdmin);
        adminService.initSetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "doreset", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doReset(Admin inputAdmin, HttpServletRequest request, String token)
        throws BadRquestException
    {
        super.checkToken(token);
        if (!ForgetPwdUtils.enableForget())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        Admin localAdmin = validateNameAndKey(inputAdmin);
        String id;
        try
        {
            String[] description = new String[]{localAdmin.getLoginName(), localAdmin.getEmail()};
            SystemLog systemLog = new SystemLog();
            systemLog.setClientAddress(IpUtils.getClientAddress(request));
            systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
            systemLog.setLoginName(localAdmin.getLoginName());
            systemLog.setShowName(localAdmin.getLoginName() + "(" + localAdmin.getName() + ")");
            id = systemLogManager.saveFailLog(systemLog,
                OperateType.ResetPassword,
                OperateDescription.ADMIN_RESET_PWD,
                null,
                description);
        }
        catch (Exception e)
        {
            LOGGER.error("save reset password log failed ", e);
            id = null;
        }
        adminService.resetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
        if (null != id)
        {
            systemLogManager.updateSuccess(id);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private Admin validateNameAndKey(Admin inputAdmin) throws BadRquestException
    {
        if (StringUtils.isBlank(inputAdmin.getName()) || StringUtils.isBlank(inputAdmin.getPassword()))
        {
            throw new BadRquestException();
        }
        String loginName = inputAdmin.getLoginName();
        if (loginName == null || !loginName.equals(inputAdmin.getName()))
        {
            throw new BadRquestException();
        }
        Admin localAdmin = adminService.getAdminByLoginName(loginName);
        if (localAdmin == null || localAdmin.getValidateKey() == null)
        {
            throw new BadRquestException();
        }
        
        String realValidateKey = EDToolsEnhance.decode(inputAdmin.getValidateKey(),
            localAdmin.getDynamicPassword());
        if (!localAdmin.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey)))
        {
            throw new BadRquestException();
        }
        return localAdmin;
    }
    
    private void sendEmail(Admin admin, String link) throws IOException
    {
        MailServer mailServer = mailServerService.getDefaultMailServer();
        if (mailServer == null)
        {
            throw new BusinessException();
        }
        Map<String, Object> messageModel = new HashMap<String, Object>(2);
        messageModel.put("username", admin.getName());
        messageModel.put("link", link);
        
        String msg = mailServerService.getEmailMsgByTemplate(Constants.RESET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(Constants.RESET_PWD_MAIL_SUBJECT,
            new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(mailServer.getId(), admin.getEmail(), null, null, subject, msg);
    }
    
    @RequestMapping(value = "validpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> validPassword(Admin admin, String token) throws BadRquestException
    {
        super.checkToken(token);
        if (!PasswordValidateUtil.isValidPassword(admin.getPassword()))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "validOldpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validOldPassword(Admin admin, String token) throws BadRquestException
    {
        super.checkToken(token);
        if (!PasswordValidateUtil.isValidPassword(admin.getOldPasswd()))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "dynamicpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> validDynamicPassword(String dynamicPwd) throws BadRquestException
    {
        if (!PasswordValidateUtil.isValidPassword(dynamicPwd))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
