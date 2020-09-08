/**
 * 
 */
package com.huawei.sharedrive.isystem.sysconfig.web;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.logfile.domain.FSEndpoint;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentService;
import com.huawei.sharedrive.isystem.logfile.web.LogAgentUtils;
import com.huawei.sharedrive.isystem.sysconfig.service.SystemConfigService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.DirectChainService;
import com.huawei.sharedrive.isystem.system.service.SyslogServerService;

import pw.cdmi.common.domain.DirectChainConfig;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LogLanguage;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/sysconfig")
public class SystemConfigController extends AbstractCommonController
{
    private static final String MESSAGE_RETENTION_DAYS_KEY = "message.retention.days";
    
    private List<String> charsets = new ArrayList<String>(10);
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private DirectChainService directChainService;
    
    @Autowired
    private SyslogServerService syslogService;
    
    @Autowired
    private LogAgentUtils logAgentUtils;
    
    @Autowired
    private LogAgentService logAgentService;
    
    public SystemConfigController()
    {
        Map<String, Charset> all = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : all.entrySet())
        {
            charsets.add(entry.getValue().name());
        }
    }
    
    @RequestMapping(value = "/messageConfig", method = RequestMethod.GET)
    public String enterMsgSettingPage(Model model)
    {
        SystemConfig config = systemConfigService.getSystemConfig(MESSAGE_RETENTION_DAYS_KEY);
        model.addAttribute("messageConfig", config);
        return "sysConfigManage/messageConfig";
    }
    
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    public ResponseEntity<String> setConfig(SystemConfig config, HttpServletRequest request, String token)
    {
        
        String temp = config.getValue();
        UserLog userLog = userLogService.initUserLog(request, UserLogType.CONFIG_MESSAGE, new String[]{temp});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        try
        {
            if (StringUtils.isBlank(config.getId()))
            {
                throw new InvalidParamException("id is blank");
            }
            
            if (config.getId().length() > 255)
            {
                throw new InvalidParamException("id exceed 255");
            }
            if (StringUtils.isBlank(temp))
            {
                throw new InvalidParamException("temp is null");
            }
            int value = Integer.parseInt(temp);
            if (value < 1 || value > 30)
            {
                throw new InvalidParamException("value is not valid");
            }
            
        }
        catch (InvalidParamException e)
        {
            userLog.setDetail(UserLogType.CONFIG_MESSAGE.getErrorDetails(new String[]{temp}));
            userLog.setType(UserLogType.CONFIG_MESSAGE.getValue());
            userLogService.update(userLog);
            return new ResponseEntity<String>("InParamterException", HttpStatus.BAD_REQUEST);
        }
        systemConfigService.setSystemConfig(config.getId(), config.getValue());
        userLog.setDetail(UserLogType.CONFIG_MESSAGE.getDetails(new String[]{temp}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/load/{clusterId}", method = RequestMethod.GET)
    public String sysConfigLoad(@PathVariable(value = "clusterId") int clusterId, Model model)
    {
        DirectChainConfig directConfig = directChainService.getDirectChain();
        DirectChainConfig secmatrix = directChainService.getSecmatrixn();
        model.addAttribute("directConfig", directConfig);
        model.addAttribute("secmatrix", secmatrix);
        
        LogLanguage logLanguage = syslogService.getLogLanguage();
        model.addAttribute("logLanguage", logLanguage);
        model.addAttribute("charsets", charsets);
        
        FSEndpoint endpoint = logAgentService.getFSEndpointByClusterId(clusterId);
        if (endpoint != null)
        {
            logAgentUtils.setModelInfo(model, endpoint.getFsType(), endpoint.getEndpoint());
        }
        
        SysLogServer syslogConfig = syslogService.getSyslogServer();
        model.addAttribute("sysLogServer", syslogConfig);
        if (syslogConfig != null && StringUtils.isNotEmpty(syslogConfig.getServer()))
        {
            model.addAttribute("server", syslogConfig.getServer());
        }
        else
        {
            model.addAttribute("server", "");
        }
        
        SystemConfig config = systemConfigService.getSystemConfig(MESSAGE_RETENTION_DAYS_KEY);
        model.addAttribute("messageConfig", config);
        return "sysConfigManage/systemConfig";
    }
}
