package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;
import java.util.Locale;

public class SecurityPermission implements Serializable
{
    
    private static final long serialVersionUID = -4195114929607795738L;
    
    private String keyName;
    
    private String permissionDesc;
    
    private String apiPath;
    
    public String getKeyName()
    {
        return keyName;
    }
    
    public void setKeyName(String keyName)
    {
        this.keyName = keyName.toUpperCase(Locale.ENGLISH);
    }
    
    public String getPermissionDesc()
    {
        return permissionDesc;
    }
    
    public void setPermissionDesc(String permissionDesc)
    {
        this.permissionDesc = permissionDesc.toUpperCase(Locale.ENGLISH);
    }
    
    public String getApiPath()
    {
        return apiPath;
    }
    
    public void setApiPath(String apiPath)
    {
        this.apiPath = apiPath.toUpperCase(Locale.ENGLISH);
    }
}
