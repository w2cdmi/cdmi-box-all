/**
 * 
 */
package com.huawei.sharedrive.isystem.user.web;

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

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.MailServerService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.ResetPasswordRequest;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;
import com.huawei.sharedrive.isystem.util.RandomKeyGUID;
import com.huawei.sharedrive.isystem.util.custom.ForgetPwdUtils;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.IpUtils;

/**
 * 
 * 
 * 系统Logo配置管理
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/syscommon")
public class ResetPasswordController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordController.class);
    
    private static final int RESET_LINK_EXPRISE_TIME = Integer.parseInt(PropertiesUtils.getProperty("session.expire",
        "600000"));
    
    /**
     * 重置密码主题
     */
    private static final String RESET_PWD_MAIL_CONTENT = "resetPasswordContent.ftl";
    
    /**
     * 重置密码消息体
     */
    private static final String RESET_PWD_MAIL_SUBJECT = "resetPasswordSubject.ftl";
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminUpdateService adminUpdateService;
    
    @Autowired
    private MailServerService mailServerService;
    
    /**
     * 帐号初次设置密码
     * 
     * @param
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "doinitset", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doInitSet(String token, Admin inputAdmin, HttpServletRequest request)
        throws BadRquestException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.RESET_PASS,
            new String[]{inputAdmin.getLoginName(), inputAdmin.getLoginName()});
        userLogService.saveUserLog(userLog);
        Admin localAdmin;
        try
        {
            localAdmin = validateNameAndKey(inputAdmin);
        }
        catch (BadRquestException e)
        {
            userLog.setDetail(UserLogType.RESET_PASS.getErrorDetails(new String[]{inputAdmin.getLoginName(),
                inputAdmin.getLoginName()}));
            userLog.setType(UserLogType.RESET_PASS.getValue());
            userLogService.update(userLog);
            throw e;
        }
        adminService.initSetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
        userLog.setDetail(UserLogType.RESET_PASS.getDetails(new String[]{inputAdmin.getLoginName(),
            inputAdmin.getLoginName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 重置密码
     * 
     * @param
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "doreset", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doReset(String token, Admin inputAdmin, HttpServletRequest request)
        throws BadRquestException
    {
        super.checkToken(token);
        if (!ForgetPwdUtils.enableForget())
        {
            LOGGER.error("user is not allowed to use reset password function[doreset],IP:"
                + IpUtils.getClientAddress(request) + ",TIME:" + new Date() + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.RESET_PASS,
            new String[]{inputAdmin.getName(), inputAdmin.getName()});
        userLog.setLoginName("--");
        userLogService.saveUserLog(userLog);
        Admin localAdmin;
        try
        {
            localAdmin = validateNameAndKey(inputAdmin);
        }
        catch (BadRquestException e)
        {
            userLog.setDetail(UserLogType.RESET_PASS.getErrorDetails(new String[]{inputAdmin.getName(),
                inputAdmin.getName()}));
            userLog.setType(UserLogType.RESET_PASS.getValue());
            userLogService.update(userLog);
            throw e;
        }
        if (inputAdmin.getLoginName() == null
            || !PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            userLog.setDetail(UserLogType.RESET_PASS.getErrorDetails(new String[]{inputAdmin.getName(),
                inputAdmin.getName()}));
            userLog.setType(UserLogType.RESET_PASS.getValue());
            userLogService.update(userLog);
            throw new BadRquestException();
        }
        adminService.resetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
        userLog.setDetail(UserLogType.RESET_PASS.getDetails(new String[]{inputAdmin.getName(),
            inputAdmin.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 进入忘记密码
     * 
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "enterforget", method = RequestMethod.GET)
    public String enterForgetPwd(HttpServletRequest request)
    {
        if (!ForgetPwdUtils.enableForget())
        {
            LOGGER.error("user is not allowed to enter forget password page,IP:"
                + IpUtils.getClientAddress(request) + ",TIME:" + new Date() + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        return "anon/forgetPwd";
    }
    
    /**
     * 进入帐号初次设置密码
     * 
     * @param
     * @return
     */
    @RequestMapping(value = "initset", method = RequestMethod.GET)
    public String initSetPwd(Model model, String name, String validateKey) throws BadRquestException
    {
        validateLink(model, name, validateKey);
        return "anon/initset";
    }
    
    /**
     * 进入重置密码
     * 
     * @param
     * @return
     * @throws BadRquestException
     */
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String resetPwd(Model model, String name, String validateKey, HttpServletRequest request)
        throws BadRquestException
    {
        if (!ForgetPwdUtils.enableForget())
        {
            LOGGER.error("user is not allowed to enter reset password page,IP:"
                + IpUtils.getClientAddress(request) + ",TIME:" + new Date() + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        validateLink(model, name, validateKey);
        return "anon/reset";
    }
    
    @RequestMapping(value = "resetmsg", method = RequestMethod.GET)
    public String resetPwdMsg()
    {
        return "anon/forgetPwdMsg";
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "sendlink", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendResetLink(Model model, ResetPasswordRequest resetPassword,
        HttpServletRequest request, String token) throws BadRquestException, IOException
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.FORGET_PASS, new String[]{
            resetPassword.getLoginName(), resetPassword.getEmail()});
        userLog.setLoginName("--");
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        if (!ForgetPwdUtils.enableForget())
        {
            LOGGER.error("user is not allowed to use forget password function[sendResetLink],IP:"
                + IpUtils.getClientAddress(request) + ",TIME:" + new Date() + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        if (StringUtils.isBlank(resetPassword.getLoginName())
            || StringUtils.isBlank(resetPassword.getCaptcha())
            || !FormValidateUtil.isValidEmail(resetPassword.getEmail()))
        {
            userLog.setDetail(UserLogType.FORGET_PASS.getErrorDetails(new String[]{
                resetPassword.getLoginName(), resetPassword.getEmail()}));
            userLog.setType(UserLogType.FORGET_PASS.getValue());
            userLogService.update(userLog);
            throw new BadRquestException();
        }
        Session session = SecurityUtils.getSubject().getSession();
        Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
        // 获取验证码后立即失效
        session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
        if (resetPassword.getCaptcha().length() != 4
            || !resetPassword.getCaptcha().equalsIgnoreCase(captchaInSession == null ? ""
                : captchaInSession.toString()))
        {
            userLog.setDetail(UserLogType.FORGET_PASS.getErrorDetails(new String[]{
                resetPassword.getLoginName(), resetPassword.getEmail()}));
            userLog.setType(UserLogType.FORGET_PASS.getValue());
            userLogService.update(userLog);
            throw new BadRquestException();
        }
        Admin admin = adminService.getAdminByLoginName(resetPassword.getLoginName());
        if (admin == null)
        {
            throw new BadRquestException();
        }
        if (admin.getDomainType() != Constants.DOMAIN_TYPE_LOCAL)
        {
            throw new BadRquestException();
        }
        if (!resetPassword.getEmail().equals(admin.getEmail()))
        {
            throw new BadRquestException();
        }
        String validateKey = RandomKeyGUID.getSecureRandomGUID();
        Map<String, String> encodeMap = EDToolsEnhance.encode(validateKey);
        adminUpdateService.updateValidKeyAndDynamicPwd(admin.getId(),
            DigestUtil.digestPassword(validateKey),
            encodeMap.get(EDToolsEnhance.ENCRYPT_KEY));
        String link = PropertiesUtils.getServiceUrl() + "syscommon/reset?name=" + resetPassword.getLoginName()
            + "&validateKey=" + encodeMap.get(EDToolsEnhance.ENCRYPT_CONTENT);
        sendEmail(admin, link);
        userLog.setDetail(UserLogType.FORGET_PASS.getDetails(new String[]{resetPassword.getLoginName(),
            resetPassword.getEmail()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "validOldpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validOldpwd(Admin admin, String token) throws BadRquestException
    {
        super.checkToken(token);
        if (!PasswordValidateUtil.isValidPassword(admin.getOldPasswd()))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 校验密码规则
     * 
     * @param
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "validpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validPassword(Admin admin, String token) throws BadRquestException
    {
        super.checkToken(token);
        if (!PasswordValidateUtil.isValidPassword(admin.getPassword()))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private void sendEmail(Admin admin, String link) throws IOException
    {
        Map<String, Object> messageModel = new HashMap<String, Object>(2);
        messageModel.put("username", admin.getName());
        messageModel.put("link", link);
        
        String msg = mailServerService.getEmailMsgByTemplate(RESET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(RESET_PWD_MAIL_SUBJECT,
            new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(admin.getEmail(), null, null, subject, msg);
    }
    
    private void validateLink(Model model, String name, String validateKey) throws BadRquestException
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
    
}
