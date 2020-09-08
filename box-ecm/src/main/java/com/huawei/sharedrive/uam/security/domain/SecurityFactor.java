package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;

public class SecurityFactor implements Serializable
{
    
    private static final long serialVersionUID = -7862439476643559604L;
    
    private SecurityFactorType type;
    
    private Integer code;
    
    private String name;
    
    private String description;
    
    private Boolean isSystem;
    
    public SecurityFactor()
    {
    }
    
    public SecurityFactor(Integer code)
    {
        this.code = code;
    }
    
    public SecurityFactorType getType()
    {
        return type;
    }
    
    public void setType(SecurityFactorType type)
    {
        this.type = type;
    }
    
    public Integer getCode()
    {
        return code;
    }
    
    public void setCode(Integer code)
    {
        this.code = code;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public Boolean getIsSystem()
    {
        return isSystem;
    }
    
    public void setIsSystem(Boolean isSystem)
    {
        this.isSystem = isSystem;
    }
    
}
