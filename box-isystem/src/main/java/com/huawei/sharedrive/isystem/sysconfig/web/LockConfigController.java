package com.huawei.sharedrive.isystem.sysconfig.web;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.sysconfig.service.SystemConfigService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.UserLog;

/**
 * 修改锁定配置
 * 
 * @author w00355328
 * 
 */
@Controller
@RequestMapping(value = "/authorize/lockConfig")
public class LockConfigController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(LockConfigController.class);
    
    private static final String LOCKCOUNT = "lockcount";
    
    private static final String LOCKTIME = "locktime";
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String get(Model model)
    {
        SystemConfig systemConfigCount = systemConfigService.getSystemConfig(LOCKCOUNT);
        SystemConfig systemConfigTime = systemConfigService.getSystemConfig(LOCKTIME);
        model.addAttribute("count", systemConfigCount.getValue());
        model.addAttribute("time", systemConfigTime.getValue());
        return "sysConfigManage/lockconfig";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseEntity<String> save(String count, String time, HttpServletRequest request,
        String token)
    {
        int countInt = 0;
        int timeInt = 0;
        
        UserLog userLog = userLogService.initUserLog(request, UserLogType.LOCK_CONFIG, new String[]{count,
            time});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        try
        {
            countInt = Integer.parseInt(count);
            timeInt = Integer.parseInt(time);
            if (!(countInt < 30 && countInt > 3) || !(timeInt < (24 * 60 * 60) && timeInt > 0))
            {
                userLog.setDetail(UserLogType.LOCK_CONFIG.getErrorDetails(new String[]{count, time}));
                userLog.setType(UserLogType.LOCK_CONFIG.getValue());
                userLogService.update(userLog);
                return new ResponseEntity<String>("InParamterException", HttpStatus.BAD_REQUEST);
            }
        }
        catch (NumberFormatException e1)
        {
            logger.error("numberformat exception", e1);
            return new ResponseEntity<String>("InParamterException", HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            systemConfigService.setSystemConfig(LOCKCOUNT, count);
            systemConfigService.setSystemConfig(LOCKTIME, time);
        }
        catch (RuntimeException e)
        {
            userLog.setDetail(UserLogType.LOCK_CONFIG.getErrorDetails(new String[]{count, time}));
            userLog.setType(UserLogType.LOCK_CONFIG.getValue());
            userLogService.update(userLog);
            logger.error("save exception", e);
            throw new InvalidParameterException("save exception: " + count + " " + time + e);
        }
        
        userLog.setDetail(UserLogType.LOCK_CONFIG.getDetails(new String[]{count, time}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
