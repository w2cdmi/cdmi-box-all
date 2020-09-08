package com.huawei.sharedrive.isystem.license;

public class LicenseCompareException extends Exception
{
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -7183599853615611811L;
    
    private int type;
    
    public int getType()
    {
        return type;
    }
    
    public LicenseCompareException(int type)
    {
        this.type = type;
    }
}
