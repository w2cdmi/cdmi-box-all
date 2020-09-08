package com.huawei.sharedrive.isystem.mirror.service;

public interface MirrorConfigService
{
    
    /**
     * 获取系统全局开关，只有开启了，策略才有效果
     * 
     * @return
     */
    boolean isMirrorGlobalEnable();
    
    /**
     * 设置系统全局开关，只有开启了，策略才有效果
     * 
     * @param flag
     */
    void setMirrorGlobalEnable(boolean flag);
    
    /**
     * 获取系统对当前复制任务状态，允许任务允许、暂停任务、删除任务、
     * 
     * @return
     */
    int getMirrorGlobalTaskState();
    
    /**
     * 所有暂停任务
     * 
     * @param state
     */
    void setMirrorGlobalTaskState(int state);
    
    /**
     * 获取系统全局开关，只有开启了，时间策略才有效果
     * 
     * @return
     */
    boolean isTimeConfigGlobalEnable();
    
    /**
     * 根据定时器判断异地复制任务的执行与否
     * 
     * @param state
     */
    void setMirrorGlobalEnableByTimer(boolean flag);
    
    /**
     * 设置系统全局开关，只有开启了，时间策略才有效果
     * 
     * @param flag
     */
    void setTimeConfigGlobalEnable(boolean flag);
    
}
