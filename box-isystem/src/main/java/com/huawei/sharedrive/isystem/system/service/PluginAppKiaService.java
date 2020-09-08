package com.huawei.sharedrive.isystem.system.service;

import java.util.List;

import pw.cdmi.common.domain.SystemConfig;

public interface PluginAppKiaService
{
    
    SystemConfig updateEnableKey(String value);
    
    SystemConfig updateVersionKey(String value);
    
    SystemConfig getEnbleKey();
    
    SystemConfig getVersionKey();
    
    void updateScanMode(boolean isScan, String startTime, String endTime);
    
    List<SystemConfig> getScanModel();
}
