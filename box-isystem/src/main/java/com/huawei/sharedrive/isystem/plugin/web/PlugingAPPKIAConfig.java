package com.huawei.sharedrive.isystem.plugin.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.isystem.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.isystem.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.isystem.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.isystem.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.exception.InternalServerErrorException;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.plugin.domain.KIAProgress;
import com.huawei.sharedrive.isystem.plugin.manager.SecurityScanManager;
import com.huawei.sharedrive.isystem.plugin.task.SystemSecurityScanTask;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.PluginAppKiaService;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.isystem.util.custom.SecurityScanUtils;
import com.sun.star.util.Date;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping("/pluginServer/KIAconfig")
public class PlugingAPPKIAConfig extends AbstractCommonController
{
    private static final String KIA_CONFIG_ENABLE_KEY = "security.scan.enable";
    
    private static final String KIA_CONFIG_VERSION_KEY = "security.scan.engine.version";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlugingAPPKIAConfig.class);
    
    private static final String TASK_TYPE_SECURITY_SCAN = "securityScan";
    
    private static final String TASK_TYPE_SECURITY_SCAN_URL = "SecurityScan";
    
    private static final String TASK_TYPE_TABLE_SCAN = "tableScan";
    
    @Autowired
    private PluginAppKiaService pluginAppKiaService;
    
    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private UserDBInfoService userDBInfoService;
    
    @RequestMapping(value = "congfig", method = {RequestMethod.GET})
    public String getAPPKIAConfig(String appId, Model model)
    {
        if (appId.equals(TASK_TYPE_SECURITY_SCAN_URL) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        SystemConfig enbaleKey = pluginAppKiaService.getEnbleKey();
        if(enbaleKey.getValue() != null && enbaleKey.getValue().length() == 1)
        {
        	enbaleKey.setValue("false");
        }
        SystemConfig verionKey = pluginAppKiaService.getVersionKey();
        List<SystemConfig> scanModel = pluginAppKiaService.getScanModel();
        model.addAttribute("enbaleKey", enbaleKey);
        model.addAttribute("verionKey", verionKey);
        model.addAttribute("scanModel", scanModel);
        return "appManage/SecurityScanConfig";
    }
    
    @RequestMapping(value = "progress", method = {RequestMethod.GET})
    public ResponseEntity<KIAProgress> getScanProcess(@RequestParam String type)
    {
        if (!SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        if (TASK_TYPE_TABLE_SCAN.equals(type))
        {
            int completed = securityScanManager.getTableScanTaskNum(SystemTask.TASK_STATE_END);
            List<UserDBInfo> dbList = userDBInfoService.listAll();
            if (CollectionUtils.isEmpty(dbList))
            {
                throw new InternalServerErrorException("Can not find the db infomation");
            }
            int total = SystemSecurityScanTask.TABLE_COUNT * dbList.size();
            KIAProgress process = new KIAProgress();
            process.setTotal(total);
            process.setWaiting(total - completed);
            process.setCompleted(completed);
            LOGGER.info("{} process : [{},{},{}]", type, total, completed, total - completed);
            return new ResponseEntity<KIAProgress>(process, HttpStatus.OK);
        }
        else if (TASK_TYPE_SECURITY_SCAN.equals(type))
        {
            int waiting = securityScanManager.getWaitingSecurityScanTaskNum();
            KIAProgress process = new KIAProgress();
            process.setWaiting(waiting);
            return new ResponseEntity<KIAProgress>(process, HttpStatus.OK);
        }
        else
        {
            throw new InvalidParamException("Invalid parameter " + type);
        }
    }
    
    @RequestMapping(value = "waitting", method = {RequestMethod.GET})
    public ResponseEntity<Integer> getUnexeTaskNum()
    {
        if (!SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        int waitingNum = securityScanManager.getTableScanTaskNum(SystemTask.TASK_STATE_BEGIN);
        // 需要减去Parent task
        waitingNum = waitingNum - 1 < 0 ? 0 : waitingNum - 1;
        return new ResponseEntity<Integer>(waitingNum, HttpStatus.OK);
    }
    
    @RequestMapping(value = "setScanning", method = {RequestMethod.POST})
    public ResponseEntity<String> setScanning(Boolean isScan, HttpServletRequest request, String token,
        String startTime, String endTime)
    {
        if (!SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        super.checkToken(token);
        if (null == isScan)
        {
            return new ResponseEntity<String>("Paramter Exception", HttpStatus.BAD_REQUEST);
        }
        
        String s = isScan ? "start" : "stop";
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.PLUGIN_SERVICE_SCAN,
            new String[]{s});
        userLogService.saveUserLog(userLog);
        
        Boolean isOK = true;
        if (isScan)
        {
            // 启动扫描
            LOGGER.info("System scan task start...");
            
            boolean timelyExecute = (startTime == null) && (endTime == null);
            int startHour;
            int endHour;
            if (!timelyExecute)
            {
                if (!checkTime(startTime, endTime))
                {
                    LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:"
                        + new Date() + ",RESULT:failed.");
                    return new ResponseEntity<String>("Paramter Exception", HttpStatus.BAD_REQUEST);
                }
                try
                {
                    startHour = checkHour(startTime);
                    endHour = checkHour(endTime);
                }
                catch (NumberFormatException e)
                {
                    LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:"
                        + new Date() + ",RESULT:failed.");
                    return new ResponseEntity<String>("Paramter Exception", HttpStatus.BAD_REQUEST);
                }
                pluginAppKiaService.updateScanMode(timelyExecute,
                    String.valueOf(startHour),
                    String.valueOf(endHour));
                securityScanManager.updateQuartzJobTask(startHour, endHour);
                securityScanManager.restartJob(TaskKeyConstant.SYSTEM_SCAN_JOBNAME);
            }
            else
            {
                pluginAppKiaService.updateScanMode(timelyExecute, null, null);
                securityScanManager.stopJob(TaskKeyConstant.SYSTEM_SCAN_JOBNAME);
            }
            SystemSecurityScanTask task = new SystemSecurityScanTask(timelyExecute);
            new Thread(task).start();
            
        }
        else
        {
            // 停止扫描
            LOGGER.info("Stop system scan task...");
            int result = systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.SYSTEM_SCAN_TASK);
            LOGGER.info("Stop system scan task success. Unexecute tasks: {}", result);
        }
        userLog.setDetail(UserLogType.PLUGIN_SERVICE_SCAN.getDetails(new String[]{s}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(isOK.toString(), HttpStatus.OK);
    }
    
    @RequestMapping(value = "setSysconfig", method = {RequestMethod.POST})
    public ResponseEntity<String> setSysconfig(SystemConfig systemConfig, Model model,
        HttpServletRequest request, String token)
    {
        if (!SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.PLUGIN_SERVICE_CONFIG, null);
        userLogService.saveUserLog(userLog);
        SystemConfig reConfig;
        if (null == systemConfig || systemConfig.getId() == null)
        {
            return new ResponseEntity<String>("null paramter", HttpStatus.BAD_REQUEST);
        }
        if (systemConfig.getId().equals(KIA_CONFIG_ENABLE_KEY))
        {
            
            userLogService.update(userLog);
            if (!FormValidateUtil.isBoolean(systemConfig.getValue()))
            {
                userLog.setDetail(UserLogType.PLUGIN_SERVICE_CONFIG.getCommonErrorParamDetails(null));
                userLog.setType(UserLogType.PLUGIN_SERVICE_CONFIG.getValue());
                userLogService.update(userLog);
                return new ResponseEntity<String>("bad paramter", HttpStatus.BAD_REQUEST);
            }
            reConfig = pluginAppKiaService.updateEnableKey(systemConfig.getValue());
            userLog.setDetail(UserLogType.PLUGIN_SERVICE_CONFIG.getDetails(null));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            
        }
        else if (systemConfig.getId().equals(KIA_CONFIG_VERSION_KEY))
        {
            if (!FormValidateUtil.isNonNegativeInteger(systemConfig.getValue()))
            {
                userLog.setDetail(UserLogType.PLUGIN_SERVICE_CONFIG.getCommonErrorParamDetails(null));
                userLog.setType(UserLogType.PLUGIN_SERVICE_CONFIG.getValue());
                userLogService.update(userLog);
                return new ResponseEntity<String>("bad paramter", HttpStatus.BAD_REQUEST);
            }
            reConfig = pluginAppKiaService.updateVersionKey(systemConfig.getValue());
            
        }
        else
        {
            return new ResponseEntity<String>("Id is not KIA id", HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.PLUGIN_SERVICE_CONFIG.getDetails(null));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(reConfig.getValue(), HttpStatus.OK);
    }
    
    private boolean checkTime(String exeStartAt, String exeEndAt)
    {
        if (exeStartAt == null && exeEndAt == null)
        {
            return true;
        }
        if (exeEndAt == null || exeStartAt == null)
        {
            return false;
        }
        if (exeStartAt.compareTo(exeEndAt) >= 0)
        {
            return false;
        }
        return true;
    }
    
    private int checkHour(String time)
    {
        if (StringUtils.isBlank(time))
        {
            throw new BadRquestException("time is blank");
        }
        
        String[] times = time.split(":");
        if (times.length != 2 || !StringUtils.equals(times[1], "00"))
        {
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        
        return Integer.parseInt(times[0]);
        
    }
}