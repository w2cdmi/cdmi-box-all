package com.huawei.sharedrive.app.user.domain;

import java.io.Serializable;

/**
 * 用户配置对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-31
 * @see
 * @since
 */
public class UserConfig implements Serializable
{
    
    private static final long serialVersionUID = -738042832263954607L;
    
    // 用户id
    private long userId;
    
    // 用户配置项key
    private String name;
    
    // 用户配置项value
    private String value;
    
    public UserConfig()
    {
        
    }
    
    public UserConfig(long userId, String name, String value)
    {
        this.userId = userId;
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
}
