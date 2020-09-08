package pw.cdmi.box.uam.authorize.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InternalServerErrorException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.LoginTimesCheckJobManager;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.LoginTimesService;
import pw.cdmi.common.domain.SystemLoginTimesConfig;
import pw.cdmi.common.domain.UserLoginTimesConfig;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.core.log.LogConstants;

@Controller
@RequestMapping(value = "/sys/loginAlam")
public class SysLoginAlamConfigController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SysLoginAlamConfigController.class);
    
    private static final int[] INTERVALE_LIST = {1, 5, 15, 30, 60};
    
    @Autowired
    private LoginTimesService loginTimesService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private LoginTimesCheckJobManager loginTimesCheckJobManager;
    
    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String enterSystem()
    {
        return "sys/loginManage/loginManageMain";
    }
    
    @RequestMapping(value = "systemConfig/load", method = RequestMethod.GET)
    public String loadSystem(Model model)
    {
        SystemLoginTimesConfig systemConfig = loginTimesService.getSystemLoginTimesConfig();
        if (systemConfig == null)
        {
            systemConfig = new SystemLoginTimesConfig();
            systemConfig.setAppId(LogConstants.DERAULT_APPID);
            systemConfig.setThreshold(100);
            systemConfig.setInterval(1);
            loginTimesService.saveSystemLoginTimesConfig(systemConfig);
        }
        
        model.addAttribute("threshold", systemConfig.getThreshold());
        model.addAttribute("interval", systemConfig.getInterval());
        return "sys/loginManage/systemLoginConfig";
    }
    
    @RequestMapping(value = "userConfig/load", method = RequestMethod.GET)
    public String loadUser(Model model)
    {
        UserLoginTimesConfig userConfig = loginTimesService.getUserLoginTimesConfig();
        if (userConfig == null)
        {
            userConfig = new UserLoginTimesConfig();
            userConfig.setAppId(LogConstants.DERAULT_APPID);
            userConfig.setThreshold(100000);
            userConfig.setInterval(1);
            loginTimesService.saveUserLoginTimesConfig(userConfig);
        }
        
        model.addAttribute("threshold", userConfig.getThreshold());
        model.addAttribute("interval", userConfig.getInterval());
        return "sys/loginManage/userLoginConfig";
    }
    
    @RequestMapping(value = "systemConfig/save", method = RequestMethod.POST)
    public String saveSystemConfig(SystemLoginTimesConfig systemConfig,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        
        Set violations = validator.validate(systemConfig);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.SetSysLoginTimes);
            throw new InvalidParamterException();
        }
        try
        {
            checkIntervalValid(systemConfig.getInterval());
        }
        catch (InvalidParamterException e)
        {
            saveValidateLog(request, OperateType.SetSysLoginTimes);
            throw e;
        }
        
        String[] description = new String[]{String.valueOf(systemConfig.getThreshold()),
            String.valueOf(systemConfig.getInterval())};
        String id = systemLogManager.saveFailLog(request,
            OperateType.SetSysLoginTimes,
            OperateDescription.SYS_ADMIN_SET_SYSTEM_LOGINTIMES,
            null,
            description);
        
        loginTimesService.saveSystemLoginTimesConfig(systemConfig);
        try
        {
            loginTimesCheckJobManager.addSystemJobManager(getJobCron(systemConfig.getInterval()));
        }
        catch (JobException e)
        {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException(e);
        }
        systemLogManager.updateSuccess(id);
        return "sys/loginManage/systemLoginConfig";
    }
    
    @RequestMapping(value = "userConfig/save", method = RequestMethod.POST)
    public String saveUserConfig(UserLoginTimesConfig userConfig, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        
        Set violations = validator.validate(userConfig);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.SetUserLoginTimes);
            throw new InvalidParamterException();
        }
        try
        {
            checkIntervalValid(userConfig.getInterval());
        }
        catch (InvalidParamterException e)
        {
            saveValidateLog(request, OperateType.SetUserLoginTimes);
            throw e;
        }
        
        String[] description = new String[]{String.valueOf(userConfig.getThreshold()),
            String.valueOf(userConfig.getInterval())};
        String id = systemLogManager.saveFailLog(request,
            OperateType.SetUserLoginTimes,
            OperateDescription.SYS_ADMIN_SET_USER_LOGINTIMES,
            null,
            description);
        
        loginTimesService.saveUserLoginTimesConfig(userConfig);
        try
        {
            loginTimesCheckJobManager.addUserJobManager(getJobCron(userConfig.getInterval()));
        }
        catch (JobException e)
        {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException(e);
        }
        systemLogManager.updateSuccess(id);
        return "sys/loginManage/systemLoginConfig";
    }
    
    private void checkIntervalValid(int interval)
    {
        for (int item : INTERVALE_LIST)
        {
            if (item == interval)
            {
                return;
            }
        }
        throw new InvalidParamterException();
    }
    
    private String getJobCron(int interval)
    {
        int hour = interval / 60;
        int minute = interval % 60;
        if (hour == 0)
        {
            return "0 */" + interval + " * * * ?";
        }
        if (minute != 0 || hour > 1)
        {
            throw new InvalidParamterException();
        }
        return "0 0 */" + hour + " * * ?";
    }
}
