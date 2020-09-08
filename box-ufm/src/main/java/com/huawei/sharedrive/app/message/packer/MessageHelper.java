package com.huawei.sharedrive.app.message.packer;

import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.common.domain.SystemConfig;

public final class MessageHelper
{
    
    // 默认消息保留周期
    private static final int DEFAULT_MESSAGE_RETENTION_DAYS = 7;
    
    private MessageHelper()
    {
        
    }
    
    /**
     * 获取消息保留周期
     * 
     * @return
     */
    static int getExpiredDays(SystemConfigService systemConfigService)
    {
        SystemConfig systemConfig = systemConfigService.getConfig("message.retention.days");
        if (systemConfig == null)
        {
            return DEFAULT_MESSAGE_RETENTION_DAYS;
        }
        return Integer.parseInt(systemConfig.getValue());
        
    }
}
