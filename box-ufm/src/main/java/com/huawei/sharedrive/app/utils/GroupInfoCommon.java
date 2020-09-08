package com.huawei.sharedrive.app.utils;

import java.io.Serializable;

public class GroupInfoCommon implements Serializable
{
    
    private static final long serialVersionUID = 3567672553973026741L;
    
    private long id;
    
    private String name;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
