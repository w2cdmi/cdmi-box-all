package com.huawei.sharedrive.isystem.license.service;

public class LicenseException extends Exception
{
    private static final long serialVersionUID = 8289649062955500631L;
    
    private LicenseExceptionType type;
    
    public LicenseException(LicenseExceptionType type, String msg)
    {
        super(msg);
        this.type = type;
    }
    
    public LicenseExceptionType getType()
    {
        return type;
    }
    
}
