package com.huawei.sharedrive.isystem.util.custom;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.util.PropertiesUtils;

public final class SecurityScanUtils
{
    private SecurityScanUtils()
    {
        
    }
    
    public static boolean enableSecurityScan()
    {
        String value = PropertiesUtils.getProperty("custom.kia.enable", "false");
        if (StringUtils.equalsIgnoreCase(value, "true"))
        {
            return true;
        }
        return false;
    }
}
