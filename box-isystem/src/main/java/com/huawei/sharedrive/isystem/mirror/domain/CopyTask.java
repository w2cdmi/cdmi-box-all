package com.huawei.sharedrive.isystem.mirror.domain;

import java.util.Date;

/**
 * 复制任务
 * 
 * @author c00287749
 * 
 */
public class CopyTask
{
    // 任务ID
    private String taskId;
    
    // 源文件所属
    private long srcOwnedBy;
    
    // 源文件ID
    private long srcINodeId;
    
    // 源对象ID
    private String srcObjectId;
    
    // 文件名称
    private String fileName;
    
    // 源文件所属
    private long destOwnedBy;
    
    // 目标文件ID
    private long destINodeId;
    
    // 目标对象ID
    private String destObjectId;
    
    // 对象大小
    private long size;
    
    // 复制类型，1：表示容灾，10：表示就近访问
    private int copyType;
    
    // 执行类型，0表示及时执行，1表示定时执行
    private int exeType;
    
    private Date createdAt;
    
    /**
     * 任务修改时间
     */
    private Date modifiedAt;
    
    private boolean isPop;
    
    // 执行开始时间
    private String exeStartAt;
    
    // 执行结束时间
    private String exeEndAt;
    
    // 策略ID
    private int policyId;
    
    // 原区域ID
    private int srcRegionId;
    
    // 原资源组ID
    private int srcResourceGroupId;
    
    private int destRegionId;
    
    private int destResourceGroupId;
    
    // 0表示等待执行，1标示正在执行，2表示执行出错，3：表示执行完成
    private int state;
    
    // 0表示普通，1表示优先级高
    private int priority;
    
    private int exeResult;
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public long getSrcINodeId()
    {
        return srcINodeId;
    }
    
    public void setSrcINodeId(long srcINodeId)
    {
        this.srcINodeId = srcINodeId;
    }
    
    public String getSrcObjectId()
    {
        return srcObjectId;
    }
    
    public void setSrcObjectId(String srcObjectId)
    {
        this.srcObjectId = srcObjectId;
    }
    
    public long getDestINodeId()
    {
        return destINodeId;
    }
    
    public void setDestINodeId(long destINodeId)
    {
        this.destINodeId = destINodeId;
    }
    
    public String getDestObjectId()
    {
        return destObjectId;
    }
    
    public void setDestObjectId(String destObjectId)
    {
        this.destObjectId = destObjectId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public int getCopyType()
    {
        return copyType;
    }
    
    public void setCopyType(int copyType)
    {
        this.copyType = copyType;
    }
    
    public int getExeType()
    {
        return exeType;
    }
    
    public void setExeType(int exeType)
    {
        this.exeType = exeType;
    }
    
    public String getExeStartAt()
    {
        return exeStartAt;
    }
    
    public void setExeStartAt(String exeStartAt)
    {
        this.exeStartAt = exeStartAt;
    }
    
    public String getExeEndAt()
    {
        return exeEndAt;
    }
    
    public void setExeEndAt(String exeEndAt)
    {
        this.exeEndAt = exeEndAt;
    }
    
    public int getSrcRegionId()
    {
        return srcRegionId;
    }
    
    public void setSrcRegionId(int srcRegionId)
    {
        this.srcRegionId = srcRegionId;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public int getExeResult()
    {
        return exeResult;
    }
    
    public void setExeResult(int exeResult)
    {
        this.exeResult = exeResult;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public long getSrcOwnedBy()
    {
        return srcOwnedBy;
    }
    
    public void setSrcOwnedBy(long srcOwnedBy)
    {
        this.srcOwnedBy = srcOwnedBy;
    }
    
    public long getDestOwnedBy()
    {
        return destOwnedBy;
    }
    
    public void setDestOwnedBy(long destOwnedBy)
    {
        this.destOwnedBy = destOwnedBy;
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt != null)
        {
            this.modifiedAt = new Date(modifiedAt.getTime());
        }
    }
    
    public boolean isPopValue()
    {
        return isPop;
    }
    
    public void setPop(boolean isPop)
    {
        this.isPop = isPop;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getTaskStr()
    {
        return "object:" + srcObjectId + ",resourceGroup:" + srcResourceGroupId + ",dest object:"
            + destObjectId + ",dest resourceGroup:" + destResourceGroupId + ",policyid:" + policyId;
    }
    
}
