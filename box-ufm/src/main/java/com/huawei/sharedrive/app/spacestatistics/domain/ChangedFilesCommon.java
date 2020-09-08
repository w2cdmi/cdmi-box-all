package com.huawei.sharedrive.app.spacestatistics.domain;

import java.io.Serializable;

public class ChangedFilesCommon implements Serializable
{
    
    private static final long serialVersionUID = -5253631685306426585L;
    
    private long ownedBy;
    
    private Long nodeId;
    
    private Long accountId;
    
    private Long size;
    
    private Long fileCount;
    
    public ChangedFilesCommon(long ownedBy, Long nodeId, Long accountId, Long size)
    {
        super();
        this.ownedBy = ownedBy;
        this.nodeId = nodeId;
        this.accountId = accountId;
        this.size = size;
    }
    
    public ChangedFilesCommon(long ownedBy, Long nodeId)
    {
        super();
        this.ownedBy = ownedBy;
        this.nodeId = nodeId;
    }
    
    public ChangedFilesCommon()
    {
        super();
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
}
