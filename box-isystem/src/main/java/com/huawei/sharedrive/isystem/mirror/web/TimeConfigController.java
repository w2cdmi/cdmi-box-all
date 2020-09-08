package com.huawei.sharedrive.isystem.mirror.web;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorManager;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorQueryManager;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;


@Controller
@RequestMapping(value = "/mirror/timeConfig")
public class TimeConfigController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(TimeConfigController.class);
    
    @Autowired
    private MirrorManager mirrorManager;
    
    @Autowired
    private MirrorQueryManager mirrorQueryManager;
    
    
    @RequestMapping(value = "timeconfigEnable", method = {RequestMethod.POST})
    public ResponseEntity<String> createTimeConfig(boolean timeconfigEnable, HttpServletRequest request,
        String token)
    {
        
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.TIMECONFIG_ENABLE,
            new String[]{timeconfigEnable + ""});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        mirrorManager.setTimeConfigGlobalEnable(timeconfigEnable);
        userLog.setDetail(UserLogType.GLOBAL_ENABLE.getDetails(new String[]{timeconfigEnable + ""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
       
    @RequestMapping(value = "createTimeConfig", method = RequestMethod.POST)
    public ResponseEntity<String> createTimeConfig(String timeConfig,
        HttpServletRequest request, String token) throws ParseException
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.TIMECONFIG_CREATE, new String[]{""});
        userLogService.saveUserLog(userLog);
        super.checkToken(token);
        TimeConfig newtimeconfig = null;
        int count=mirrorQueryManager.countAllTimeConfig();
        newtimeconfig = JsonUtils.stringToObject(timeConfig, TimeConfig.class);
        if(count>=10)
        {
            return new ResponseEntity<String>("Overlimit", HttpStatus.BAD_REQUEST);
        }
        if (!checkTimeConfig(newtimeconfig))
        {
            return new ResponseEntity<String>("TimeConfigConflict", HttpStatus.BAD_REQUEST);
        }
        
        if (!mirrorManager.createTimeConfig(newtimeconfig))
        {
            return new ResponseEntity<String>("BadTimeConfigInfo", HttpStatus.BAD_REQUEST);
        }
        userLog.setDetail(UserLogType.TIMECONFIG_CREATE.getDetails(new String[]{newtimeconfig.getExeStartAt()+"-"+newtimeconfig.getExeEndAt()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "createTimeConfigPage", method = {RequestMethod.GET})
    public String createTimeConfigPage()
    {
        return "copyPolicy/createTimeConfig";
    }
    
    @RequestMapping(value = "delete", method = {RequestMethod.POST})
    public ResponseEntity<String> deleteTimeConfigForOne(String uuid, HttpServletRequest request, String token)
    {
        TimeConfig timeconfig = null;
        super.checkToken(token);
       
           UserLog userLog = userLogService.initUserLog(request, UserLogType.TIMECONFIG_DEL, new String[]{""});
            userLogService.saveUserLog(userLog);
            timeconfig = mirrorQueryManager.getTimeConfig(uuid);
            mirrorManager.deleteTimeConfig(timeconfig);
            userLog.setDetail(UserLogType.TIMECONFIG_DEL.getDetails(new String[]{timeconfig.getExeStartAt()+"-"+timeconfig.getExeEndAt()}));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            return new ResponseEntity<String>(HttpStatus.OK);

       
    }

    private Boolean checkTimeConfig(TimeConfig newtimeconfig) throws ParseException{
        List<TimeConfig> timeconfigs = mirrorQueryManager.getListTimeConfig();
        SimpleDateFormat formatter = new SimpleDateFormat( "HH:mm");
        Date newStartAt=formatter.parse(newtimeconfig.getExeStartAt());
        Date newEndAt=formatter.parse(newtimeconfig.getExeEndAt());
        Date startAt=null;
        Date endAt=null;
        boolean flag=true;
        if(timeconfigs==null||timeconfigs.isEmpty())
        {
            return true;
        }
        for(TimeConfig timeconfig:timeconfigs)
        {
             startAt=formatter.parse(timeconfig.getExeStartAt());
             endAt=formatter.parse(timeconfig.getExeEndAt());
             //已有的跨天,新增的不跨天
             if(startAt.after(endAt)&&newStartAt.before(newEndAt))
               {
                 flag=checktime1(newStartAt, newEndAt, startAt, endAt, flag);
               }
             //已有的和新增的跨天
             if(startAt.after(endAt)&&newStartAt.after(newEndAt))
             {
                flag=false;
             }
             //已有的不跨天，新增的跨天
             if(startAt.before(endAt)&&newStartAt.after(newEndAt))
               {
                 flag=checktime2(newStartAt, newEndAt, startAt, endAt, flag);
               }
             //已有的和新增的不跨天
             if(startAt.before(endAt)&&newStartAt.before(newEndAt))
             {
                 flag=checktime3(newStartAt, newEndAt, startAt, endAt, flag); 
             }
             
             if(!flag)
                 {
                 return false;
                 }
        }
        
        return true;
        
    }

    private boolean checktime3(Date newStartAt, Date newEndAt, Date startAt, Date endAt, boolean flag)
    {
        if(!((newEndAt.before(startAt)||newEndAt.equals(startAt))||(newStartAt.after(endAt)||newStartAt.equals(endAt))))  
          {
               flag=false;
          }
        return flag;
    }

    private boolean checktime2(Date newStartAt, Date newEndAt, Date startAt, Date endAt, boolean flag)
    {                
              if(!((newEndAt.before(startAt)||newEndAt.equals(startAt))&&(newStartAt.after(endAt)||newStartAt.equals(endAt))))
              {
                 flag=false;
              }
              return flag;
    }

    private boolean checktime1(Date newStartAt, Date newEndAt, Date startAt, Date endAt, boolean flag)
    {    
          if(!((endAt.before(newStartAt)||endAt.equals(newStartAt))&&(startAt.after(newEndAt)||startAt.equals(newEndAt))))
              {
                 flag=false;
              }
          return flag;
    }
}
