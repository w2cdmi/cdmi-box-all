package com.huawei.sharedrive.isystem.statistics.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.statistics.domain.StatisticsAccessKey;
import com.huawei.sharedrive.isystem.statistics.service.StatisticsAccessKeyService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.util.Constants;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/statisticsmanage/statistics")
public class StatisticsController extends AbstractCommonController
{
    
    @Autowired
    private StatisticsAccessKeyService statisticsService;
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        
        return "statistics/statisticsManageMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String list(Model model)
    {
        List<StatisticsAccessKey> statisticsAccessKey = statisticsService.getList();
        for (StatisticsAccessKey accessKey : statisticsAccessKey)
        {
            accessKey.setSecretKey(Constants.DISPLAY_STAR_VALUE);
        }
        model.addAttribute("statisticsList", statisticsAccessKey);
        return "statistics/statisticsKeyList";
    }
    
    @RequestMapping(value = "create", method = {RequestMethod.POST})
    public ResponseEntity<?> create(Model model, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STATISTIC_ACCESSKEY_ADD,
            new String[]{""});
        userLogService.saveUserLog(userLog);
        
        StatisticsAccessKey statisticsAccessKey = statisticsService.create();
        if (statisticsAccessKey == null)
        {
            return new ResponseEntity<String>("ExceedMax", HttpStatus.CONFLICT);
        }
        userLog.setDetail(UserLogType.STATISTIC_ACCESSKEY_ADD.getDetails(new String[]{statisticsAccessKey.getId()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<StatisticsAccessKey>(statisticsAccessKey,HttpStatus.OK);
    }
    
    @RequestMapping(value = "delete", method = {RequestMethod.POST})
    public ResponseEntity<?> delete(String accessKey, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STATISTIC_ACCESSKEY_DEL,
            new String[]{accessKey});
        userLogService.saveUserLog(userLog);
        
        if (StringUtils.isBlank(accessKey) || accessKey.length() > 64)
        {
            userLog.setDetail(UserLogType.STATISTIC_ACCESSKEY_DEL.getErrorDetails(new String[]{accessKey}));
            userLog.setType(UserLogType.STATISTIC_ACCESSKEY_DEL.getValue());
            userLogService.update(userLog);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        statisticsService.deleteById(accessKey);
        userLog.setDetail(UserLogType.STATISTIC_ACCESSKEY_DEL.getDetails(new String[]{accessKey}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "get", method = {RequestMethod.GET})
    public String get(String id, Model model)
    {
        StatisticsAccessKey statKey = statisticsService.get(id);
        model.addAttribute("statKey", statKey);
        return "statistics/statisticsKeyDisplay";
    }
    
}
