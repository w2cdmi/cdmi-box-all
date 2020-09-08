package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;

public class CopyTask
{
    
    private int copyType;
    
    private Date createdAt;
    
    private long destINodeId;
    
    private String destObjectId;
    
    private long destOwnedBy;
    
    private int destRegionId;
    
    private int destResourceGroupId;
    
    private String exeEndAt;
    
    private int exeResult;
    
    private String exeStartAt;
    
    private int exeType;
    
    private String fileName;
    
    private boolean isPop;
    
    private Date modifiedAt;
    
    private int policyId;
    
    // 0表示普通，1表示优先级高
    private int priority;
    
    private long size;
    
    private long srcINodeId;
    
    private String srcObjectId;
    
    private long srcOwnedBy;
    
    private int srcRegionId;
    
    private int srcResourceGroupId;
    
    // 0表示等待执行，1标示正在执行，2表示执行出错，3：表示执行完成
    private int state;
    
    private String taskId;
    
    public int getCopyType()
    {
        return copyType;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getDestINodeId()
    {
        return destINodeId;
    }
    
    public String getDestObjectId()
    {
        return destObjectId;
    }
    
    public long getDestOwnedBy()
    {
        return destOwnedBy;
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public String getExeEndAt()
    {
        return exeEndAt;
    }
    
    public int getExeResult()
    {
        return exeResult;
    }
    
    public String getExeStartAt()
    {
        return exeStartAt;
    }
    
    public int getExeType()
    {
        return exeType;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public long getSrcINodeId()
    {
        return srcINodeId;
    }
    
    public String getSrcObjectId()
    {
        return srcObjectId;
    }
    
    public long getSrcOwnedBy()
    {
        return srcOwnedBy;
    }
    
    public int getSrcRegionId()
    {
        return srcRegionId;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public String getTaskStr()
    {
        return "object:" + srcObjectId + ",resourceGroup:" + srcResourceGroupId + ",dest object:"
            + destObjectId + ",dest resourceGroup:" + destResourceGroupId + ",policyid:" + policyId;
    }
    
    public boolean isPopValue()
    {
        return isPop;
    }
    
    public void setCopyType(int copyType)
    {
        this.copyType = copyType;
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
    
    public void setDestINodeId(long destINodeId)
    {
        this.destINodeId = destINodeId;
    }
    
    public void setDestObjectId(String destObjectId)
    {
        this.destObjectId = destObjectId;
    }
    
    public void setDestOwnedBy(long destOwnedBy)
    {
        this.destOwnedBy = destOwnedBy;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public void setExeEndAt(String exeEndAt)
    {
        this.exeEndAt = exeEndAt;
    }
    
    public void setExeResult(int exeResult)
    {
        this.exeResult = exeResult;
    }
    
    public void setExeStartAt(String exeStartAt)
    {
        this.exeStartAt = exeStartAt;
    }
    
    public void setExeType(int exeType)
    {
        this.exeType = exeType;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
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
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public void setPop(boolean isPop)
    {
        this.isPop = isPop;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setSrcINodeId(long srcINodeId)
    {
        this.srcINodeId = srcINodeId;
    }
    
    public void setSrcObjectId(String srcObjectId)
    {
        this.srcObjectId = srcObjectId;
    }
    
    public void setSrcOwnedBy(long srcOwnedBy)
    {
        this.srcOwnedBy = srcOwnedBy;
    }
    
    public void setSrcRegionId(int srcRegionId)
    {
        this.srcRegionId = srcRegionId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
}
