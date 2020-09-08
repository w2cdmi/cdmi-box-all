package pw.cdmi.box.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.LogSecurityService;

@Controller
@RequestMapping(value = "/sys/sysconfig/secconfig")
public class SysLogSecurityConfigController extends AbstractCommonController
{
    @Autowired
    private LogSecurityService logSecurityService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final String TRUE = "true";
    
    private static final String FALSE = "false";
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
        model.addAttribute("logSecurity", logSecurityService.isUserLogVisible());
        return "sys/sysConfigManage/logSecurity";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> save(String logSecurity, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        if (!TRUE.equals(logSecurity) && !FALSE.equals(logSecurity))
        {
            saveValidateLog(request, OperateType.AdminLogSet);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        String[] description = new String[]{logSecurity};
        try
        {
            logSecurityService.saveLogSecurityConfig(logSecurity);
        }
        catch (RuntimeException e)
        {
            systemLogManager.saveFailLog(request,
                OperateType.AdminLogSet,
                OperateDescription.ADMIN_USERLOG_SET,
                null,
                description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        systemLogManager.saveSuccessLog(request,
            OperateType.AdminLogSet,
            OperateDescription.ADMIN_USERLOG_SET,
            null,
            description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
