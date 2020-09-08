package com.huawei.sharedrive.app.plugins.scan.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件安全扫描任务对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-11
 * @see
 * @since
 */
public class SecurityScanTask implements Serializable
{
    public static final int MAX_TASK_NUM = 1000000;
    
    public static final int TASK_NUM_ALLOWED_TO_ADD = 800000;
    
    /** 任务状态: 0-所有 */
    public static final byte STATUS_ALL = 0;
    
    /** 任务状态: 1-等待执行 */
    public static final byte STATUS_WAIING = 1;
    
    /** 任务状态: 2-执行完毕 */
    public static final byte STATUS_COMPLETE = 2;
    
    /** 任务状态: -1-执行失败 */
    public static final byte STATUS_ERROR = -1;
    
    public static final int PRIORITY_NORMAL = 5;
    
    public static final int PRIORITY_LOW = 1;
    
    public static final int PRIORITY_HIGH = 9;
    
    private static final long serialVersionUID = 1019192290983797978L;
    
    // 任务id
    private String taskId;
    
    // 文件objectId
    private String objectId;
    
    // 文件id
    private long nodeId;
    
    // 文件名
    private String nodeName;
    
    // 文件拥有者
    private long ownedBy;
    
    // 文件所属dss id
    private int dssId;
    
    // 任务生成时间
    private Date createdAt;
    
    // 任务最后更新时间
    private Date modifiedAt;
    
    // 任务状态
    private Byte status;
    
    // 任务优先级
    private int priority;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public int getDssId()
    {
        return dssId;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public long getNodeId()
    {
        return nodeId;
    }
    
    public String getNodeName()
    {
        return nodeName;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setDssId(int dssId)
    {
        this.dssId = dssId;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
}
