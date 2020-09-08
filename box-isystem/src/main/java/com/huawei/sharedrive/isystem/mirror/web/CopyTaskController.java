package com.huawei.sharedrive.isystem.mirror.web;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.isystem.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.isystem.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorManager;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorQueryManager;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskStatistic;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogTypeCopyTask;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/mirror/copyTask")
public class CopyTaskController extends AbstractCommonController
{
    private static final String SIZE_B = "B";
    
    private static final String SIZE_K = "KB";
    
    private static final String SIZE_M = "MB";
    
    private static final String SIZE_G = "GB";
    
    public static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyController.class);
    
    @Autowired
    private MirrorManager mirrorManager;
    
    @Autowired
    private MirrorQueryManager mirrorQueryManager;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @RequestMapping(value = "list/{id}/policy", method = {RequestMethod.GET})
    public String listCopyTask(Model model, @PathVariable Integer id)
    {
        checkCopyPlocyIsOpen();
        CopyTaskStatistic copyTaskStatistic = mirrorManager.statisticCurrentTaskInfo();
        List<CopyPolicy> policies = mirrorQueryManager.getListCopyPolicy();
        CopyTaskStatistic copytask = new CopyTaskStatistic();
        int state = mirrorQueryManager.getSystemConfig();
        model.addAttribute("state", state);
        
        if (CollectionUtils.isEmpty(policies))
        {
            LOGGER.info("policies is null");
            model.addAttribute("isDisable", "true");
            return "copyPolicy/copyTask";
        }
        
        List<SystemTask> lstSystemTask = systemTaskService.listSystemTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_MIRROR_BACK_SCAN_TASK);
        SystemTask oldSystemTask = null;
        SystemTask curtSystemTask = null;
        for(SystemTask systemTask : lstSystemTask)
        {
            if(systemTask.getState()==SystemTask.TASK_STATE_END)
            {
                oldSystemTask = systemTask;
            }
            else
            {
                curtSystemTask = systemTask;
            }
        }
        String totalTime="-:-:-";
        String startTime="-:-:-";
        Date date = null;
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format=new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        long hour = 0;
        long min = 0;
        long sec = 0;
        if (oldSystemTask != null && curtSystemTask !=null)
        {
            long delTime=curtSystemTask.getCreateTime().getTime()-oldSystemTask.getCreateTime().getTime();
            date = new Date();
            date.setTime(delTime);
            totalTime=timeformat.format(date);
            hour=delTime/(1000*60*60);
            min=(delTime/1000-hour*60*60)/60;
            sec=(delTime/1000)%60;
            totalTime=""+hour+":"+((min<10L)?("0"+min):(""+min))+":"+((sec<10L)?("0"+sec):(""+sec));
        }
        if(curtSystemTask!=null)
        {
            startTime=format.format(curtSystemTask.getCreateTime());
        }
        model.addAttribute("ScanTotalTime", totalTime);
        model.addAttribute("CurTaskStartTime", startTime);
        
        CopyPolicy policy = null;
        if (id < 0)
        {
            policy = policies.get(0);
            copytask = mirrorManager.statisticCurrentTaskInfo(policy);
            
        }
        if (id >= 0)
        {
            policy = new CopyPolicy();
            policy.setId(id);
            boolean temp = false;
            
            for (CopyPolicy copyPolicy : policies)
            {
                if (copyPolicy.getId() == id)
                {
                    temp = true;
                    
                    break;
                }
            }
            
            if (!temp)
            {
                LOGGER.info("id exception" + id);
                copytask = null;
            }
            else
            {
                copytask = mirrorManager.statisticCurrentTaskInfo(policy);
            }
        }
        model.addAttribute("copyTaskStatistic", copyTaskStatistic);
        model.addAttribute("policies", policies);
        model.addAttribute("copytask", copytask);
        model.addAttribute("policy", policy);
        
        Map<String, String> unitMapAll = computeBKMG(copyTaskStatistic);
        Map<String, String> unitMapPolicy = computeBKMG(copytask);
        model.addAttribute("allmap", unitMapAll);
        model.addAttribute("policymap", unitMapPolicy);
        
        return "copyPolicy/copyTask";
    }
    
    private Map<String, String> computeBKMG(CopyTaskStatistic copyTaskStatistic)
    {
        if(copyTaskStatistic == null)
        {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>(5);
        DecimalFormat df = new DecimalFormat("0.00");
        long []size = new long[5];
        String []bkmg = new String[]{"all", "not", "wait", "exe", "failed"};
        size[0] = copyTaskStatistic.getAllSize();
        size[1] = copyTaskStatistic.getNoactivateTaskSize();
        size[2] = copyTaskStatistic.getWaitingTaskSize();
        size[3] = copyTaskStatistic.getExeingTaskSize();
        size[4] = copyTaskStatistic.getFailedTaskSize();
        long kb = 1 * 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        
        //float B = 0;
        float k = 0;
        float m = 0;
        float g = 0;
        
        for (int i = 0; i < 5; i++)
        {
            g = size[i] / (float)gb;
            if (g > 1.0)
            {
                map.put(bkmg[i], df.format(g) + SIZE_G);
                continue;
            }
            m = size[i] / (float)mb;
            if (m > 1.0)
            {
                map.put(bkmg[i], df.format(m) + SIZE_M);
                continue;
            }
            k = size[i] / (float)kb;
            if (k > 1.0)
            {
                map.put(bkmg[i], df.format(k) + SIZE_K);
                continue;
            }
            map.put(bkmg[i], size[i] + SIZE_B);
        }
        return map;
    }
    
    @RequestMapping(value = "updateState", method = {RequestMethod.POST})
    public ResponseEntity<String> updateState(Model model, String state, HttpServletRequest request,
        String token)
    {
        checkCopyPlocyIsOpen();
        String log = state;
        if(state.equals(MirrorCommonStatic.TASK_STATE_WAITTING+""))
        {
            log = UserLogTypeCopyTask.COPYTASK_ALL_START.getDetails(new String[]{});
        }
        else if(state.equals(MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE+""))
        {
            log = UserLogTypeCopyTask.COPYTASK_ALL_STOP.getDetails(new String[]{});
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.TASK_STATE_ENABLE,
            new String[]{log});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        try
        {
            int temp = Integer.parseInt(state);
            if (temp != MirrorCommonStatic.TASK_STATE_WAITTING
                && temp != MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE)
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
        }
        catch (NumberFormatException e)
        {
            LOGGER.error("The string: " + state + "to int err");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.TASK_STATE_ENABLE.getDetails(new String[]{log}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        mirrorManager.updateTask(state);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
