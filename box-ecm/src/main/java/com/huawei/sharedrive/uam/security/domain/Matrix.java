package com.huawei.sharedrive.uam.security.domain;

public class Matrix
{
    private Integer userType;
    
    private Integer networkType;
    
    private Integer deviceType;
    
    public Integer getUserType()
    {
        return userType;
    }
    
    public void setUserType(Integer userType)
    {
        this.userType = userType;
    }
    
    public Integer getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(Integer networkType)
    {
        this.networkType = networkType;
    }
    
    public Integer getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(Integer deviceType)
    {
        this.deviceType = deviceType;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
        result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
        result = prime * result + ((userType == null) ? 0 : userType.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Matrix)
        {
            if (this == obj)
            {
                return true;
            }
            Matrix other = (Matrix) obj;
            if (deviceType == null)
            {
                if (other.deviceType != null)
                {
                    return false;
                }
            }
            else if (!deviceType.equals(other.deviceType))
            {
                return false;
            }
            if (networkType == null)
            {
                if (other.networkType != null)
                {
                    return false;
                }
            }
            else if (!networkType.equals(other.networkType))
            {
                return false;
            }
            if (userType == null)
            {
                if (other.userType != null)
                {
                    return false;
                }
            }
            else if (!userType.equals(other.userType))
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
