package com.huawei.sharedrive.uam.system.service;

import java.util.List;

import pw.cdmi.common.domain.SystemConfig;

public interface BackupConfigService
{
    String BACKUP_RULE_CONFIG_PREFIX = "folderbackup.rule.";
    
    String BACKUP_RULE_CONFIG_WHITE = "folderbackup.rule.allowed";
    
    String BACKUP_RULE_CONFIG_BLACK = "folderbackup.rule.forbidden";
    
    List<SystemConfig> getAllConfig(String appId);
    
    SystemConfig getConfigById(String appId, String id);
    
    void saveConfigList(List<SystemConfig> itemList);
    
    void saveConfig(SystemConfig item);
}
