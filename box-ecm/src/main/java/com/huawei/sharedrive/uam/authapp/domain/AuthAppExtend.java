package com.huawei.sharedrive.uam.authapp.domain;

import pw.cdmi.uam.domain.AuthApp;

public class AuthAppExtend extends AuthApp
{
    
    private static final long serialVersionUID = -6702248875596432506L;
    
    private String[] includedAppId;
    
    private String[] excludeAppId;
    
    private boolean filePreview;
    
    private long accountId;
    
    public String[] getIncludedAppId()
    {
        if (null == includedAppId)
        {
            return new String[]{};
        }
        return includedAppId.clone();
    }
    
    public void setIncludedAppId(String[] includedAppId)
    {
        if (null != includedAppId)
        {
            this.includedAppId = includedAppId.clone();
        }
        else
        {
            this.includedAppId = null;
        }
    }
    
    public String[] getExcludeAppId()
    {
        if (null == excludeAppId)
        {
            return new String[]{};
        }
        return excludeAppId.clone();
    }
    
    public void setExcludeAppId(String[] excludeAppId)
    {
        if (null != excludeAppId)
        {
            this.excludeAppId = excludeAppId.clone();
        }
        else
        {
            this.excludeAppId = null;
        }
    }
    
    public boolean isFilePreview()
    {
        return filePreview;
    }
    
    public void setFilePreview(boolean filePreview)
    {
        this.filePreview = filePreview;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
