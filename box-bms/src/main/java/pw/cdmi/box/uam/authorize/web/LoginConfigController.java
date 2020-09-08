package pw.cdmi.box.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.common.domain.SystemConfig;

@Controller
@RequestMapping(value = "/sys/sysconfig/loginconfig")
public class LoginConfigController extends AbstractCommonController
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final String LOCK_TIME = "lock.time";
    
    private static final String LOCK_FAIL_TIMES = "lock.failtimes";
    
    private static final String APP_ID = "-1";
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String loadLoginConfig(Model model)
    {
        SystemConfig lockTime = systemConfigDAO.getByPriKey(APP_ID, LOCK_TIME);
        SystemConfig failTimes = systemConfigDAO.getByPriKey(APP_ID, LOCK_FAIL_TIMES);
        if (null != lockTime && null != failTimes)
        {
            model.addAttribute("failtimes", failTimes.getValue());
            model.addAttribute("locktime", lockTime.getValue());
        }
        return "sys/sysConfigManage/loginConfig";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity<String> saveLoginConfig(String lockTimeValue, String failTimesValue,
        HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        String[] description = new String[]{sessAdmin.getName(), failTimesValue, lockTimeValue};
        if (StringUtils.isBlank(lockTimeValue) || StringUtils.isBlank(failTimesValue))
        {
            saveLog(req, OperateDescription.UPDATE_LOGIN_CONFIG_ERROR, description);
            return new ResponseEntity<String>("notAllownNull", HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            int lockTimes = Integer.parseInt(lockTimeValue);
            int failTimes = Integer.parseInt(failTimesValue);
            // LOCK_FAIL_TIMES(失败次数)，LOCK_TIME(锁定时间)
            if (failTimes >= 5 && failTimes <= 20 && lockTimes >= 5 && lockTimes <= 120)
            {
                SystemConfig lockTimeConfig = new SystemConfig(APP_ID, LOCK_TIME, lockTimeValue);
                SystemConfig failTimeConfig = new SystemConfig(APP_ID, LOCK_FAIL_TIMES, failTimesValue);
                systemConfigDAO.update(lockTimeConfig);
                systemConfigDAO.update(failTimeConfig);
                systemLogManager.saveSuccessLog(req,
                    OperateType.UpdateLoginConfig,
                    OperateDescription.UPDATE_LOGIN_CONFIG,
                    null,
                    description);
                
            }
            else
            {
                saveLog(req, OperateDescription.UPDATE_LOGIN_CONFIG_ERROR, description);
                return new ResponseEntity<String>("badResquestParamter", HttpStatus.BAD_REQUEST);
            }
        }
        catch (NumberFormatException e)
        {
            saveLog(req, OperateDescription.UPDATE_LOGIN_CONFIG_ERROR, description);
            return new ResponseEntity<String>("badResquestParamter", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    protected void saveLog(HttpServletRequest req, OperateDescription operateDescription, String[] description)
    {
        systemLogManager.saveFailLog(req,
            OperateType.UpdateLoginConfig,
            operateDescription,
            null,
            description);
    }
}
