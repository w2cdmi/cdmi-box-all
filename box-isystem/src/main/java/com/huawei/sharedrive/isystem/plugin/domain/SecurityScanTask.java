package com.huawei.sharedrive.isystem.plugin.domain;

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
    private static final long serialVersionUID = -2872234652846902623L;
    
    public static final byte STATUS_ALL = 0;
    
    /** 任务状态: 1-等待执行 */
    public static final byte STATUS_WAIING = 1;
    
    /** 任务状态: 2-执行完毕 */
    public static final byte STATUS_COMPLETE = 2;
    
    /** 任务状态: -1-执行失败 */
    public static final byte STATUS_ERROR = -1;
    
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
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public int getDssId()
    {
        return dssId;
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
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
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setDssId(int dssId)
    {
        this.dssId = dssId;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        this.modifiedAt = modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
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
