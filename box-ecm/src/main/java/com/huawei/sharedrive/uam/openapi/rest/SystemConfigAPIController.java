package com.huawei.sharedrive.uam.openapi.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.NoSuchOptionException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RestSystemConfig;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.system.service.BackupConfigService;
import com.huawei.sharedrive.uam.system.service.SecurityService;

import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/config")
public class SystemConfigAPIController
{
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private AppBasicConfigService appBasicConfigService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private BackupConfigService backupConfigService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RestSystemConfig>> getSystemConfig(
        @RequestHeader("Authorization") String authorization, @RequestParam String option)
        throws BaseRunException
    {
        
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserLog userLog = UserLogType.getUserLog(userToken);
        List<RestSystemConfig> list = new ArrayList<RestSystemConfig>(10);
        if (RestSystemConfig.OPTION_LINK_ACCESSKEY_RULE.equalsIgnoreCase(option))
        {
            addLinkCodeConfig(userToken, list);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
            return new ResponseEntity<List<RestSystemConfig>>(list, HttpStatus.OK);
        }
        else if (RestSystemConfig.OPTION_SYSTEM_MAX_VERSIONS.equalsIgnoreCase(option))
        {
            addFileVersionConfig(userToken, list);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
            
            return new ResponseEntity<List<RestSystemConfig>>(list, HttpStatus.OK);
        }
        else if (RestSystemConfig.OPTION_BACKUP_WHITE_RULE.equalsIgnoreCase(option))
        {
            addBackUpConfig(userToken, option, BackupConfigService.BACKUP_RULE_CONFIG_WHITE, list);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
            return new ResponseEntity<List<RestSystemConfig>>(list, HttpStatus.OK);
        }
        else if (RestSystemConfig.OPTION_BACKUP_BLACK_RULE.equalsIgnoreCase(option))
        {
            addBackUpConfig(userToken, option, BackupConfigService.BACKUP_RULE_CONFIG_BLACK, list);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
            return new ResponseEntity<List<RestSystemConfig>>(list, HttpStatus.OK);
        }
        else if (RestSystemConfig.OPTION_ALL.equalsIgnoreCase(option))
        {
            addLinkCodeConfig(userToken, list);
            addFileVersionConfig(userToken, list);
            addBackUpConfig(userToken,
                RestSystemConfig.OPTION_BACKUP_WHITE_RULE,
                BackupConfigService.BACKUP_RULE_CONFIG_WHITE,
                list);
            addBackUpConfig(userToken,
                RestSystemConfig.OPTION_BACKUP_BLACK_RULE,
                BackupConfigService.BACKUP_RULE_CONFIG_BLACK,
                list);
            userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG, null);
            
            return new ResponseEntity<List<RestSystemConfig>>(list, HttpStatus.OK);
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_SYSTEM_CONFIG_ERR, null);
        
        throw new NoSuchOptionException("No such option: " + option);
    }
    
    private void addFileVersionConfig(UserToken userToken, List<RestSystemConfig> list)
    {
        RestSystemConfig fileVersionConfig = new RestSystemConfig();
        fileVersionConfig.setOption(RestSystemConfig.OPTION_SYSTEM_MAX_VERSIONS);
        fileVersionConfig.setValue(String.valueOf(appBasicConfigService.getAppBasicConfig(userToken.getAppId())
            .getMaxFileVersions()));
        list.add(fileVersionConfig);
    }
    
    private void addLinkCodeConfig(UserToken userToken, List<RestSystemConfig> list)
    {
        SecurityConfig securityConfig = securityService.getSecurityConfig(userToken.getAppId());
        RestSystemConfig linkCodeConfig = new RestSystemConfig();
        linkCodeConfig.setOption(RestSystemConfig.OPTION_LINK_ACCESSKEY_RULE);
        linkCodeConfig.setValue(securityConfig.isDisableSimpleLinkCode() ? "complex" : "simple");
        list.add(linkCodeConfig);
    }
    
    private void addBackUpConfig(UserToken userToken, String option, String configId,
        List<RestSystemConfig> list)
    {
        SystemConfig config = backupConfigService.getConfigById(userToken.getAppId(), configId);
        if (config == null)
        {
            return;
        }
        RestSystemConfig backupConfig = new RestSystemConfig();
        backupConfig.setOption(option);
        backupConfig.setValue(config.getValue());
        list.add(backupConfig);
    }
    
}
