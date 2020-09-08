package com.huawei.sharedrive.app.openapi.domain.group;


public enum GroupOrderDirection
{
    ASC("ASC"), DESC("DESC");
    
    private String direction;
    
    GroupOrderDirection(String direction)
    {
        this.direction = direction;
    }
    
    public String getDirection()
    {
        return direction;
    }
    
}
