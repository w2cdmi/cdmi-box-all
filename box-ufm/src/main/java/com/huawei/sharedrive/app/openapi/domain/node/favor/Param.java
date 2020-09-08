package com.huawei.sharedrive.app.openapi.domain.node.favor;

import java.io.Serializable;

public class Param implements Serializable
{
    private static final long serialVersionUID = -7445090677886796033L;

    private String name;
    
    private String value;
    
    public Param()
    {
    }
    
    public Param(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public enum Name
    {
        ORGINNAME("orginName"), TEAMSPACENAME("teamspaceName"), SENDER("sender"), LINKCODE("linkCode"), PATH("path");
        
        private Name(String name)
        {
            this.name = name;
        }
        
        private String name;
        
        public String getName()
        {
            return name;
        }
        
    }
}
