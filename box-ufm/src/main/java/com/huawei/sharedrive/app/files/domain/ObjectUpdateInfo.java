package com.huawei.sharedrive.app.files.domain;

public class ObjectUpdateInfo
{
    private long length; // required
    
    private String objectId; // required
    
    private long ownerId;
    
    private String sha1; // required
    
    private String storagePath; // required
    
    public long getLength()
    {
        return length;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public String getStoragePath()
    {
        return storagePath;
    }
    
    public void setLength(long length)
    {
        this.length = length;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setStoragePath(String storagePath)
    {
        this.storagePath = storagePath;
    }
    
}
