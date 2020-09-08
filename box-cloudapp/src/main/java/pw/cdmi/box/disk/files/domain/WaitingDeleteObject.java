package pw.cdmi.box.disk.files.domain;

import java.util.Date;

public class WaitingDeleteObject
{
    private Date createdAt;
    
    private String objectId;
    
    private int resourceGroupId;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
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
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
}
