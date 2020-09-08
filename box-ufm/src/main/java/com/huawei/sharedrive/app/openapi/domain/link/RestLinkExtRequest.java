package com.huawei.sharedrive.app.openapi.domain.link;

public class RestLinkExtRequest
{
    
    private String plainAccessCode;
    
    private String type;
    
    private int height;
    
    private int width;
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
    
}
