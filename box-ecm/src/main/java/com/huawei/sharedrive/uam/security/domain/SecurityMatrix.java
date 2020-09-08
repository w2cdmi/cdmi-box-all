package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;
import java.util.Locale;

public class SecurityMatrix implements Serializable
{
    private static final long serialVersionUID = 5702426828209982887L;
    
    private SecurityFactor appType;
    
    private SecurityFactor userType;
    
    private SecurityFactor networkType;
    
    private SecurityFactor deviceType;
    
    private SecurityFactor srcResourceType;
    
    private SecurityFactor resExtendType;
    
    private String permissionValue;
    
    private String roleName;
    
    public SecurityFactor getUserType()
    {
        return userType;
    }
    
    public void setUserType(SecurityFactor userType)
    {
        this.userType = userType;
    }
    
    public SecurityFactor getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(SecurityFactor networkType)
    {
        this.networkType = networkType;
    }
    
    public SecurityFactor getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(SecurityFactor deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public SecurityFactor getSrcResourceType()
    {
        return srcResourceType;
    }
    
    public void setSrcResourceType(SecurityFactor srcResourceType)
    {
        this.srcResourceType = srcResourceType;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    public String getPermissionValue()
    {
        return permissionValue;
    }
    
    public void setPermissionValue(String permissionValue)
    {
        this.permissionValue = permissionValue.toUpperCase(Locale.ENGLISH);
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public SecurityFactor getResExtendType()
    {
        return resExtendType;
    }
    
    public void setResExtendType(SecurityFactor resExtendType)
    {
        this.resExtendType = resExtendType;
    }
    
    public SecurityFactor getAppType()
    {
        return appType;
    }
    
    public void setAppType(SecurityFactor appType)
    {
        this.appType = appType;
    }
}
