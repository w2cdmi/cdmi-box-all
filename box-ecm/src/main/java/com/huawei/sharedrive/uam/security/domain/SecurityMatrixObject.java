package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;
import java.util.Set;

public class SecurityMatrixObject implements Serializable
{
    private static final long serialVersionUID = 4698355365559507701L;
    
    private int userType;
    
    private int networkType;
    
    private int deviceType;
    
    private String roleName;
    
    private Set<String> permissions;
    
    public SecurityMatrixObject()
    {
        super();
    }
    
    public SecurityMatrixObject(int userType, int networkType, int deviceType)
    {
        super();
        this.userType = userType;
        this.networkType = networkType;
        this.deviceType = deviceType;
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public Set<String> getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(Set<String> permissions)
    {
        this.permissions = permissions;
    }
    
    public Integer getUserType()
    {
        return userType;
    }
    
    public void setUserType(int userType)
    {
        this.userType = userType;
    }
    
    public Integer getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(int networkType)
    {
        this.networkType = networkType;
    }
    
    public Integer getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SecurityMatrixObject)
        {
            if (this == obj)
            {
                return true;
            }
            SecurityMatrixObject other = (SecurityMatrixObject) obj;
            if (roleName == null)
            {
                if (other.roleName != null)
                {
                    return false;
                }
            }
            else if (!roleName.equals(other.roleName))
            {
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
        
    }
}
