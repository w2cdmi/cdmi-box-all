package com.huawei.sharedrive.isystem.mirror.appdatamigration.domain;

/**
 * 无法复制的对象
 * 
 * @author c00287749
 * 
 */
public class NeverCopyContent
{
    private String parentId;
    
    private String id;
    
    private int policyId;
    
    private String appId;
    
    private long ownedBy;
    
    private long nodeId;
    
    private String fileName;
    
    private String objectId;
    
    private long size;
    
    private String md5;
    
    private String blockMD5;
    
    private String reason;
    
    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedby)
    {
        this.ownedBy = ownedby;
    }
    
    public long getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String getMd5()
    {
        return md5;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public String getBlockMD5()
    {
        return blockMD5;
    }
    
    public void setBlockMD5(String blockMD5)
    {
        this.blockMD5 = blockMD5;
    }
    
    public String getReason()
    {
        return reason;
    }
    
    public void setReason(String reason)
    {
        this.reason = reason;
    }
    
}
