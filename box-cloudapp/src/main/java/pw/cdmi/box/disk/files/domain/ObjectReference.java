package pw.cdmi.box.disk.files.domain;

import java.util.Date;

public class ObjectReference
{
    private String id;
    
    private Date lastDeleteTime;
    
    private int refCount;
    
    private int resourceGroupId;
    
    private String sha1;
    
    private long size;
    
    private int tableSuffix;
    
    public String getId()
    {
        return id;
    }
    
    public Date getLastDeleteTime()
    {
        if (lastDeleteTime == null)
        {
            return null;
        }
        return (Date) lastDeleteTime.clone();
    }
    
    public int getRefCount()
    {
        return refCount;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setLastDeleteTime(Date lastDeleteTime)
    {
        if (lastDeleteTime == null)
        {
            this.lastDeleteTime = null;
        }
        else
        {
            this.lastDeleteTime = (Date) lastDeleteTime.clone();
        }
    }
    
    public void setRefCount(int refCount)
    {
        this.refCount = refCount;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
}