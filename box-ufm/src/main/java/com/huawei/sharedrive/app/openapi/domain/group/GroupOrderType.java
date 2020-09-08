package com.huawei.sharedrive.app.openapi.domain.group;

public enum GroupOrderType
{
    NAME("name"),
    GROUPROLE("groupRole"),
    USERNAME("username"),
    MODIFIEDAT("modifiedAt");
    
    private String type;
    
    GroupOrderType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
    
}
