package com.huawei.sharedrive.isystem.mirror.domain;

/**
 * 静态资源定义
 * 
 * @author c00287749
 * 
 */
public final class MirrorCommonStatic
{
    
    // 任务未激活状态
    public final static int TASK_STATE_NOACTIVATE = -1;
    
    // 等待执行
    public final static int TASK_STATE_WAITTING = 0;
    
    // 系统暂停
    public final static int TASK_STATE_SYSTEM_PAUSE = 4;
    
    // app范围
    public final static int POLICY_APP = 0;
    
    // APP下的USser
    public final static int POLICY_APP_USER = 1;
    
    // 状态：普通
    public final static int POLICY_STATE_COMMON = 0;
    
    public static final String TIMECONFIG_ENABLE = "timeconfig.enable";
    
    // 全局系统镜像开关
    public static final String MIRROR_GLOBAL_ENABLE = "mirror.global.enable";
    
    // 全局任务状态，对外提供暂停PAUSE 和正常
    public static final String MIRROR_GLOBAL_TASK_STATE = "mirror.global.task.state";
    
    // 全局异地复制镜像开关，根据时间控制，此开关功能和 全局镜像开关一样，但优先级小于 全局镜像开关
    public static final String MIRROR_GLOBAL_ENABLE_TIMER = "mirror.global.enable.timer";
    
    /**
     * 组合成新的复制类型。
     * 
     * @param srcType
     * @param newType
     * @return
     */
    private MirrorCommonStatic()
    {
        
    }
    
    public static int combCopyType(int srcType, int newType)
    {
        
        return srcType & newType;
    }
    
}
