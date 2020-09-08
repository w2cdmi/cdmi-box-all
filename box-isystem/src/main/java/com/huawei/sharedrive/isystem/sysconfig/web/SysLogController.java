/**
 * 
 */
package com.huawei.sharedrive.isystem.sysconfig.web;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.SyslogServerService;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.log.LogLanguage;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.NetCheckUtils;

/**
 * 
 * 
 * 系统Syslog配置管理
 * 
 * @author d00199602
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "/sysconfig/syslog")
public class SysLogController extends AbstractCommonController
{
    @Autowired
    private SyslogServerService syslogService;
    
    private List<String> charsets = new ArrayList<String>(10);
    
    public SysLogController()
    {
        Map<String, Charset> all = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : all.entrySet())
        {
            charsets.add(entry.getValue().name());
        }
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        
        model.addAttribute("sysLogServer", syslogService.getSyslogServer());
        
        return "sysConfigManage/sysConfigMain";
    }
    
    @RequestMapping(value = "load", method = RequestMethod.GET)
    public String load(Model model)
    {
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
        model.addAttribute("charsets", charsets);
        return "sysConfigManage/syslogSetting";
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(SysLogServer sysLogServer, HttpServletRequest request, String token)
    {
        
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.SYSLOG_CONFIG,
            logParam(sysLogServer));
        userLogService.saveUserLog(userLog);
        Set violations = validator.validate(sysLogServer);
        
        super.checkToken(token);
        
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        if (!charsets.contains(sysLogServer.getCharset()))
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("charset excetption" + sysLogServer.getCharset());
        }
        
        if (sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_TCP
            && sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_UDP)
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(violations);
        }
        if (!FormValidateUtil.isValidPort(sysLogServer.getPort()))
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("port Exception" + sysLogServer.getPort());
        }
        syslogService.saveSysLogServer(sysLogServer);
        
        userLog.setDetail(UserLogType.SYSLOG_CONFIG.getDetails(logParam(sysLogServer)));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "test", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> test(SysLogServer sysLogServer, HttpServletRequest request, String token)
    {
        
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.SYSLOG_CONFIG_TEST,
            logParam(sysLogServer));
        userLogService.saveUserLog(userLog);
        Set violations = validator.validate(sysLogServer);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        super.checkToken(token);
        
        if (!charsets.contains(sysLogServer.getCharset()))
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG_TEST.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG_TEST.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("charset excetption" + sysLogServer.getCharset());
        }
        
        if (sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_TCP
            && sysLogServer.getProtocolType() != SysLogServer.PROTOCOL_TYPE_UDP)
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG_TEST.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG_TEST.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(violations);
        }
        if (!FormValidateUtil.isValidPort(sysLogServer.getPort()))
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG_TEST.getErrorDetails(logParam(sysLogServer)));
            userLog.setType(UserLogType.SYSLOG_CONFIG_TEST.getValue());
            userLogService.update(userLog);
            throw new InvalidParameterException("port Exception" + sysLogServer.getPort());
        }
        
        if (checkSyslogConfig(sysLogServer))
        {
            userLog.setDetail(UserLogType.SYSLOG_CONFIG_TEST.getDetails(logParam(sysLogServer)));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    private boolean checkSyslogConfig(SysLogServer sysLogServer)
    {
        if (SysLogServer.PROTOCOL_TYPE_TCP == sysLogServer.getProtocolType())
        {
            return NetCheckUtils.isReachable(sysLogServer.getServer(), sysLogServer.getPort(), 5000);
        }
        return NetCheckUtils.isReachable(sysLogServer.getServer(), 5000);
    }
    
    @RequestMapping(value = "savelogLanguage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveLogLanguage(LogLanguage logLanguage, HttpServletRequest request, String token)
    {
        String logParam = logLanguage.getLanguage();
        String value = logLanguage.getConfig();
        UserLog userLog = userLogService.initUserLog(request, UserLogType.SYSLOG_LANG, new String[]{logParam,
            value});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        Set violations = validator.validate(logLanguage);
        
        if (!logLanguage.getLanguage().equals(LogLanguage.DEFAULT_LANGUAGE_EN)
            && !logLanguage.getLanguage().equals(LogLanguage.DEFAULT_LANGUAGE_ZH))
        {
            userLog.setDetail(UserLogType.SYSLOG_LANG.getErrorDetails(new String[]{logParam, value}));
            userLog.setType(UserLogType.SYSLOG_LANG.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(violations);
        }
        if (!StringUtils.equals(logLanguage.getConfig(), "0")
            && !StringUtils.equals(logLanguage.getConfig(), "1"))
        {
            userLog.setDetail(UserLogType.SYSLOG_LANG.getErrorDetails(new String[]{logParam, value}));
            userLog.setType(UserLogType.SYSLOG_LANG.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException(violations);
        }
        syslogService.setUserLanguage(logLanguage);
        userLog.setDetail(UserLogType.SYSLOG_LANG.getDetails(new String[]{logParam, value}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "loglanguage", method = RequestMethod.GET)
    public String loadLanguage(Model model)
    {
        LogLanguage logLanguage = syslogService.getLogLanguage();
        model.addAttribute("logLanguage", logLanguage);
        model.addAttribute("charsets", charsets);
        return "sysConfigManage/logLanguageConfig";
    }
    
    private String[] logParam(SysLogServer sysLogServer)
    {
        if (null == sysLogServer)
        {
            return new String[]{};
        }
        
        return new String[]{"ip " + sysLogServer.getServer() + ", port " + sysLogServer.getPort(),
            String.valueOf(sysLogServer.isSendLocalTimestamp()),
            String.valueOf(sysLogServer.isSendLocalName())};
    }
}
