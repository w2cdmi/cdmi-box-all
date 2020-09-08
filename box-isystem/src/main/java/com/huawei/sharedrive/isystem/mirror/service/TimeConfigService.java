package com.huawei.sharedrive.isystem.mirror.service;

import java.util.List;


import com.huawei.sharedrive.isystem.mirror.domain.TimeConfig;
public interface TimeConfigService
{
    /**
     * 获取时间设置
     * 
     * @return
     */
    List<TimeConfig> listTimeConfig();
    /**
     * 创建时间设置
     * 
     * @param timeconfig
     */
    void createTimeConfig(TimeConfig timeconfig);
    /**
     * 删除时间设置
     * 
     * @param timeconfig
     */
    void deleteTimeConfig(TimeConfig timeconfig);

    
    TimeConfig getTimeConfig(String uuid);
    
    int countAll();
    
}
