package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccessConfigSwitch implements Serializable
{
    
    public static final String CACHE_KEY_PREFIX_ID = "access_config_switch_id_";
    
    public static final byte STATUS_DISABLE = 1;
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    private static final long serialVersionUID = 1L;
    
    private String configSwitch;
    
    private Date createdAt;
    
    private long id;
    
    private Date modifiedAt;
    
    public String getConfigSwitch()
    {
        return configSwitch;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public void setConfigSwitch(String configSwitch)
    {
        this.configSwitch = configSwitch;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
}
