package com.huawei.sharedrive.app.favorite.domain;

import java.io.Serializable;

public class ExtraAttr implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String grantName;
    
    private String filePath;

    public String getGrantName()
    {
        return grantName;
    }

    public void setGrantName(String grantName)
    {
        this.grantName = grantName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    
    
}
