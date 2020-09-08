package com.huawei.sharedrive.isystem.util.custom;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.util.PropertiesUtils;

public final class ForgetPwdUtils
{
    private ForgetPwdUtils()
    {
        
    }
    
    public static boolean enableForget()
    {
        String value = PropertiesUtils.getProperty("custom.forgetPwd", "false");
        if (StringUtils.equalsIgnoreCase(value, "true"))
        {
            return true;
        }
        return false;
    }
    
}
