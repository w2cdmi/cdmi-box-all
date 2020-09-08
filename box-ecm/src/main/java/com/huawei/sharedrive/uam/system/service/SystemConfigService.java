package com.huawei.sharedrive.uam.system.service;

import java.util.List;

import pw.cdmi.common.domain.SystemConfig;

/**
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-9-2
 * @see
 * @since
 */
public interface SystemConfigService
{
    /**
     * 
     * @param key
     * @param appId
     * @return
     */
    SystemConfig getSystemConfig(String key, String appId);
    
    /**
     * 
     * @param key
     * @param appId
     * @return
     */
    List<SystemConfig> listSystemConfig(String key, String appId);

    /* 保存配置项，如果不存在就生成，如果存在就更新 */
    void saveSystemConfig(SystemConfig config);
}