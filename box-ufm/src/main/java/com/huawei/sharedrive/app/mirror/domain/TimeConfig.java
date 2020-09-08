package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;

public class TimeConfig
{
private String uuid;
    
    private Date createdAt;
      
    // 10:02:00 12:00:00
    private String exeStartAt;
    
    private String exeEndAt;
   
    
    public String getUuid()
    {
        return uuid;
    }
    
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
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
}
