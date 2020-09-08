/**
 * 
 */
package com.huawei.sharedrive.isystem.authorize.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
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
import com.huawei.sharedrive.isystem.system.service.PwdConfuser;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.common.domain.MailServer;
import pw.cdmi.common.log.UserLog;

/**
 * 
 * 
 * 系统配置管理
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/authorize/mailserver")
public class MailServerController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(MailServerController.class);
    
    @Autowired
    private MailServerService mailService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model, HttpSession session)
    {
        MailServer mailServer = mailService.getMailServer();
        model.addAttribute("mailServer", mailServer);
        if (mailServer == null)
        {
            model.addAttribute("serverStr", "");
            model.addAttribute("senderName", "");
            model.addAttribute("authUserName", "");
            model.addAttribute("authUserPwd", "");
        }
        else
        {
            model.addAttribute("serverStr", mailServer.getServer());
            model.addAttribute("senderName", mailServer.getSenderName());
            model.addAttribute("authUserName", mailServer.getAuthUsername());
            if (StringUtils.isEmpty(mailServer.getAuthPassword()))
            {
                model.addAttribute("authUserPwd", "");
            }
            else
            {
                model.addAttribute("authUserPwd", PwdConfuser.DEFAULT_SHOW_PWD);
            }
        }
        return "authorizeManage/mailServer";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(MailServer mailServer, String token, HttpServletRequest request,
        HttpSession session) throws BadRquestException
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.MAIL_SAVE,
            new String[]{mailServer.getServer()});
        userLogService.saveUserLog(userLog);
        Set violations = validator.validate(mailServer);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        
        if (!FormValidateUtil.isValidEmail(mailServer.getSenderMail()))
        {
            userLog.setDetail(UserLogType.MAIL_SAVE.getErrorDetails(new String[]{mailServer.getServer()}));
            userLog.setType(UserLogType.MAIL_SAVE.getValue());
            userLogService.update(userLog);
            logger.warn("test mail address failed.");
            throw new BadRquestException();
        }
        if (!checkMailSecurity(mailServer.getMailSecurity()))
        {
            logger.error("invalid mailSercurity");
            throw new BadRquestException();
        }
        if (StringUtils.isNotBlank(mailServer.getTestMail())
            && !FormValidateUtil.isValidEmail(mailServer.getTestMail()))
        {
            userLog.setDetail(UserLogType.MAIL_SAVE.getErrorDetails(new String[]{mailServer.getServer()}));
            userLog.setType(UserLogType.MAIL_SAVE.getValue());
            userLogService.update(userLog);
            logger.warn("test mail address failed.");
            throw new BadRquestException();
        }
        if (!FormValidateUtil.isValidPort(mailServer.getPort()))
        {
            userLog.setDetail(UserLogType.MAIL_SAVE.getErrorDetails(new String[]{mailServer.getServer()}));
            userLog.setType(UserLogType.MAIL_SAVE.getValue());
            userLogService.update(userLog);
            logger.warn("test mail port failed.");
            throw new BadRquestException();
        }
        super.checkToken(token);
        
        MailServer mailServerDb = mailService.getMailServer();
        mailServer.setAuthPassword(PwdConfuser.getSysMailPwd(mailServerDb, mailServer.getAuthPassword()));
        mailService.saveMailServer(mailServer);
        userLog.setDetail(UserLogType.MAIL_SAVE.getDetails(new String[]{mailServer.getServer()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 发送测试邮件
     * 
     * @param reciver 收件人地址
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "testMail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendTestMail(MailServer mailServer, String token, HttpServletRequest request,
        HttpSession session) throws EmailException, IOException
    {
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.MAIL_TEST,
            new String[]{mailServer.getTestMail()});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        Set violations = validator.validate(mailServer);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        if (!checkMailSecurity(mailServer.getMailSecurity()))
        {
            logger.error("invalid mailSercurity");
            throw new BadRquestException();
        }
        if (StringUtils.isBlank(mailServer.getTestMail())
            || !FormValidateUtil.isValidEmail(mailServer.getTestMail()))
        {
            userLog.setDetail(UserLogType.MAIL_TEST.getErrorDetails(new String[]{mailServer.getTestMail()}));
            userLog.setType(UserLogType.MAIL_TEST.getValue());
            userLogService.update(userLog);
            logger.warn("test mail address failed.");
            throw new BadRquestException();
        }
        if (!FormValidateUtil.isValidPort(mailServer.getPort()))
        {
            userLog.setDetail(UserLogType.MAIL_TEST.getErrorDetails(new String[]{mailServer.getTestMail()}));
            userLog.setType(UserLogType.MAIL_TEST.getValue());
            userLogService.update(userLog);
            logger.warn("test mail port failed.");
            throw new BadRquestException();
        }
        
        MailServer mailServerDb = mailService.getMailServer();
        mailServer.setAuthPassword(PwdConfuser.getSysMailPwd(mailServerDb, mailServer.getAuthPassword()));
        mailService.sendTestMail(mailServer, mailServer.getTestMail());
        userLog.setDetail(UserLogType.MAIL_TEST.getDetails(new String[]{mailServer.getTestMail()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    private boolean checkMailSecurity(String mailSecurity)
    {
        if ("ssl".equals(mailSecurity) || "SSL".equals(mailSecurity))
        {
            return true;
        }
        if ("tls".equals(mailSecurity) || "TLS".equals(mailSecurity))
        {
            return true;
        }
        if ("false".equals(mailSecurity) || "False".equals(mailSecurity))
        {
            return true;
        }
        return false;
    }
}
