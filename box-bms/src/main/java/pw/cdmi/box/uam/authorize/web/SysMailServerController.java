package pw.cdmi.box.uam.authorize.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.system.service.impl.PwdConfuser;
import pw.cdmi.box.uam.util.FormValidateUtil;

@Controller
@RequestMapping(value = "/sys/sysconfig/mailserver")
public class SysMailServerController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(SysMailServerController.class);
    
    @Autowired
    private MailServerService mailService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        return "sys/sysConfigManage/mailServerMain";
    }
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        MailServer mailServer = mailService.getDefaultMailServer();
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
        return "sys/sysConfigManage/mailServer";
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(MailServer mailServer, String token, HttpServletRequest request)
    {
        
        Set violations = validator.validate(mailServer);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeEmailServer);
            throw new ConstraintViolationException(violations);
        }
        if (!checkMailSecurity(mailServer.getMailSecurity()))
        {
            logger.error("invalid mailSercurity");
            throw new BadRquestException();
        }
        if (!FormValidateUtil.isValidEmail(mailServer.getSenderMail()))
        {
            saveValidateLog(request, OperateType.ChangeEmailServer);
            logger.warn("sender mail address invalid.");
            throw new BadRquestException();
        }
        if (StringUtils.isNotBlank(mailServer.getTestMail())
            && !FormValidateUtil.isValidEmail(mailServer.getTestMail()))
        {
            saveValidateLog(request, OperateType.ChangeEmailServer);
            logger.warn("test mail address failed.");
            throw new BadRquestException();
        }
        
        String[] description = new String[]{mailServer.getServer(), String.valueOf(mailServer.getPort()),
            mailServer.getSenderMail(), mailServer.getSenderName(), mailServer.getTestMail(),
            String.valueOf(mailServer.getMailSecurity()), String.valueOf(mailServer.isEnableAuth()),
            mailServer.getAuthUsername()};
        MailServer mailServerDb = mailService.getDefaultMailServer();
        mailServer.setAuthPassword(PwdConfuser.getSysMailPwd(mailServerDb, mailServer.getAuthPassword()));
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeEmailServer,
            OperateDescription.SYS_ADMIN_SET_EMAIL,
            null,
            description);
        
        super.checkToken(token);
        
        mailServer.setDefaultFlag(true);
        MailServer defaultMailServer = mailService.getDefaultMailServer();
        if (defaultMailServer == null)
        {
            mailService.saveMailServer(mailServer);
        }
        else
        {
            mailServer.setId(defaultMailServer.getId());
            mailService.updateMailServer(mailServer);
        }
        systemLogManager.updateSuccess(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 
     * @return
     * @throws IOException
     * @throws EmailException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "testMail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendTestMail(MailServer mailServer, String token,
        HttpServletRequest request) throws EmailException, IOException
    {
        super.checkToken(token);
        MailServer mailServerDb = mailService.getDefaultMailServer();
        mailServer.setAuthPassword(PwdConfuser.getSysMailPwd(mailServerDb, mailServer.getAuthPassword()));
        Set violations = validator.validate(mailServer);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.TestEmailServer);
            throw new ConstraintViolationException(violations);
        }
        if (!FormValidateUtil.isValidEmail(mailServer.getSenderMail()))
        {
            saveValidateLog(request, OperateType.TestEmailServer);
            logger.warn("sender mail address invalid.");
            throw new BadRquestException();
        }
        if (!checkMailSecurity(mailServer.getMailSecurity()))
        {
            logger.error("invalid mailSercurity");
            throw new BadRquestException();
        }
        if (!FormValidateUtil.isValidEmail(mailServer.getTestMail()))
        {
            saveValidateLog(request, OperateType.TestEmailServer);
            logger.warn("test mail address failed.");
            throw new BadRquestException();
        }
        
        String id = systemLogManager.saveFailLog(request,
            OperateType.TestEmailServer,
            OperateDescription.SYS_ADMIN_TEST_EMAIL,
            null,
            null);
        mailServer.setDefaultFlag(true);
        
        mailService.sendTestMail(mailServer, mailServer.getTestMail());
        systemLogManager.updateSuccess(id);
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
