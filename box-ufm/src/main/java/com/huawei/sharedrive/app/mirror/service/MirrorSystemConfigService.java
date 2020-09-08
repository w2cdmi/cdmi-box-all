package com.huawei.sharedrive.app.mirror.service;

import java.util.Map;

public interface MirrorSystemConfigService
{
    
    /**
     * 获取系统是否开启这功能
     * 
     * @return
     */
    boolean isSystemMirrorEnable();
    
    /**
     * 获取异地复制时间配置是否生效开关
     * @return
     */
    boolean isTimeconfigEnable();
    
    /**
     * 获取系统根据定时器判断是否开启异地复制功能
     * 
     * @return
     */
    boolean isSystemMirrorEnableTimer();
    
    /**
     * 获取系统复制任务全局配置
     * 
     * @return
     */
    int getSystemMirrorTaskState();
    
    /**
     * 获取配置
     * 
     * @return
     */
    int getCopyTaskSendTimeout();
    
    /**
     * 
     * @return
     */
    int getCopyTaskExeTimeout();
    
    /**
     * 获取需要删除任务的错误码
     * 
     * @return
     */
    String getErrorCodeForNeedDeleteFailedTask();
    
    /**
     * 获取系统统计特性是否开启
     * 
     * @return
     */
    boolean isSystemMirrorPolicyStatisticEnable();
    
    /**
     * 获取系统统计类型
     * 
     * @return
     */
    boolean checkSystemMirrorPolicyStatisticType(int copyType);
    
    /**
     * 统计超时
     * 
     * @return
     */
    int getCopyStatisticExeTimeout();
    
    /**
     * 获取就近访问的配置
     * 
     * @return
     */
    Map<String, Boolean> lstNearAccessEnable();
    
    /**
     * 获取系统全局配置
     * 
     * @return
     */
    boolean isSystemNearAccessEnable();
    
}
