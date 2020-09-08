package com.huawei.sharedrive.isystem.statistics.domain;

import java.io.Serializable;
import java.util.Date;

public class StatisticsAccessKey implements Serializable
{
    private static final long serialVersionUID = 1336495961888786843L;
    
    private Date createdAt;
    
    private String id;
    
    private String secretKey;
    
    private String secretKeyEncodeKey;
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getSecretKeyEncodeKey() {
        return secretKeyEncodeKey;
    }

    public void setSecretKeyEncodeKey(String secretKeyEncodeKey) {
        this.secretKeyEncodeKey = secretKeyEncodeKey;
    }
}
