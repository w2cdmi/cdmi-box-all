package com.huawei.sharedrive.app.mirror.domain;

import com.huawei.sharedrive.app.files.domain.INode;

/**
 * 静态资源定义
 * 
 * @author c00287749
 * 
 */
public final class MirrorCommonStatic
{
    
    private MirrorCommonStatic()
    {
        
    }
    
    public final static String COPY_POLICY_CHANGE_KEY = "miror_copy_policy_change";
    
    //异地复制时间控制时间配置改变
    public final static String TIME_CONFIG_CHANGE = "miror_time_config_change";
    
    //异地复制时间控制开关状态改变
    public final static String TIME_CONFIG_SWITCH_CHANGE = "miror_time_switch_config_change";
    
    // 用于非异步复制策略的数据复制
    public final static int DEFAULT_POLICY_ID = -1;
    
    // 需要删除的错误码任务
    public final static String ERROR_CODE_FOR_DELETE_TASK = "404,409,503";
    
    // 及时执行
    public final static int EXE_TYPE_NOW = 0;
    
    // 定时执行
    public final static int EXE_TYPE_TIME = 1;
    
    public static final int MD5_LENGTH = 32;
    
    public static final String MIRROR_COPY_OBJECT_SUCCESS_KEY = "MirrorCopyObjectSuccess";
    
    public static final String MIRROR_GLOBAL_CONFIG_CHANGE = "mirror_global_config_change";
    
    // *********************************系统级配置参数，写入到system_config表中*********//
    // 全局系统镜像开关
    public static final String MIRROR_GLOBAL_ENABLE = "mirror.global.enable";
    
    // 全局异地复制镜像开关，根据时间控制，此开关功能和 全局镜像开关一样，但优先级小于 全局镜像开关
    public static final String MIRROR_GLOBAL_ENABLE_TIMER = "mirror.global.enable.timer";
    
    // 是否开启基于配置策略的统计
    public static final String MIRROR_GLOBAL_POLICY_STATISTIC_ENABLE = "mirror.global.policy.statistic.enable";
    
    public static final String MIRROR_GLOBAL_POLICY_STATISTIC_EXE_TIMEOUT_MINUTE = "mirror.global.policy.statistic.timeout.minute";
    
    // 策略统计类型
    public static final String MIRROR_GLOBAL_POLICY_STATISTIC_TYPE = "mirror.global.policy.statistic.type";
    
    // 需要删除错误码任务
    public static final String MIRROR_GLOBAL_TASK_DELETE_CODE = "mirror.global.task.delete.code";
    
    // 任务执行超时时间
    public static final String MIRROR_GLOBAL_TASK_EXE_TIMEOUT_MINUTE = "mirror.task.exe.timeout.minute";
    
    // 发送超时时间
    public static final String MIRROR_GLOBAL_TASK_SEND_TIMEOUT_MINUTE = "mirror.task.send.timeout.minute";
    
    // 全局任务状态，对外提供暂停PAUSE 和正常
    public static final String MIRROR_GLOBAL_TASK_STATE = "mirror.global.task.state";
    
    // 就近访问配置
    public static final String NEAR_ACCESS_CONFIG_CHANGE = "near_access_config_change";
    
    //同一区域下数据中心下载优先级
    public final static String PRIORITY_CHANGE_KEY = "priority_change";
    
    //异地复制时间控制执行是否开启
    public final static String TIMECONFIG_ENABLE = "timeconfig.enable";
    
    // app范围
    public final static int POLICY_APP = 0;
    
    // APP下的USser
    public final static int POLICY_APP_USER = 1;
    
    // 策略类型：异地复制
    public final static int POLICY_COPY_TYPE_COPY = 1;
    
    // 策略类型：数据迁移
    public final static int POLICY_COPY_TYPE_MIGRATION = 4;
    
    // 状态：普通
    public final static int POLICY_STATE_COMMON = 0;
    
    // 状态：暂停
    public final static int POLICY_STATE_PAUSE = 1;
    
    // 普通优先级
    public final static int PRIORITY_TYPE_COMMON = 0;
    
    // 高优先级
    public final static int PRIORITY_TYPE_HIGH = 1;
    
    // 网络距离配置
    public static final String REGION_NETWORK_DISTANCE_CHANGE = "region_network_distance_change";
    
    public static final int SHA1_LENGTH = 40;
    
    // 就近访问配置
    public static final String SYSTEM_NEAR_ACCESS_APP_ENABLE_PREFIX = "system.near.access.app.enable.";
    
    // 系统全局配置
    public static final String SYSTEM_NEAR_ACCESS_ENABLE = "system.near.access.enable";
    
    // 执行中
    public final static int TASK_STATE_EXEING = 1;
    
    // 执行失败
    public final static int TASK_STATE_FAILED = 3;
    
    // 任务未激活状态
    public final static int TASK_STATE_NOACTIVATE = -1;
    
    // 执行成功
    public final static int TASK_STATE_SUCCESSED = 2;
    
    // 系统暂停
    public final static int TASK_STATE_SYSTEM_PAUSE = 4;
    
    // 等待执行
    public final static int TASK_STATE_WAITTING = 0;
    
    /**
     * 组合成新的复制类型。
     * 
     * @param srcType
     * @param newType
     * @return
     */
    public static int combCopyType(int srcType, int newType)
    {
        
        return srcType & newType;
    }
    
    public static INode getDestNode(CopyTask task)
    {
        if (null == task)
        {
            return null;
        }
        INode srcNode = new INode();
        srcNode.setId(task.getDestINodeId());
        srcNode.setOwnedBy(task.getDestOwnedBy());
        srcNode.setName(task.getFileName());
        srcNode.setSize(task.getSize());
        srcNode.setObjectId(task.getDestObjectId());
        srcNode.setResourceGroupId(task.getDestResourceGroupId());
        
        return srcNode;
        
    }
    
    public static INode getSrcNode(CopyTask task)
    {
        if (null == task)
        {
            return null;
        }
        INode srcNode = new INode();
        srcNode.setId(task.getSrcINodeId());
        srcNode.setOwnedBy(task.getSrcOwnedBy());
        srcNode.setName(task.getFileName());
        srcNode.setSize(task.getSize());
        srcNode.setObjectId(task.getSrcObjectId());
        srcNode.setResourceGroupId(task.getSrcResourceGroupId());
        
        return srcNode;
        
    }
    
}
