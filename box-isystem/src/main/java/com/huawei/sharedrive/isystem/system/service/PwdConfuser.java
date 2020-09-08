package com.huawei.sharedrive.isystem.system.service;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.common.domain.MailServer;

public final class PwdConfuser
{
    public static final String DEFAULT_SHOW_PWD = "**********";
    
    private PwdConfuser()
    {
        
    }
    
    public static String getSysMailPwd(MailServer mailServer, String pwd)
    {
        if (mailServer == null)
        {
            return pwd;
        }
        
        if (StringUtils.equals(pwd, DEFAULT_SHOW_PWD))
        {
            Object res = mailServer.getAuthPassword();
            if (null == res)
            {
                return null;
            }
            return (String) res;
        }
        return pwd;
    }
}
