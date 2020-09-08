package com.huawei.sharedrive.isystem.user.domain;

public class WebUtilsBean
{
    private boolean contextRelative;
    
    private boolean http10Compatible;
    
    private String url;
    
    public boolean isContextRelative()
    {
        return contextRelative;
    }
    
    public void setContextRelative(boolean contextRelative)
    {
        this.contextRelative = contextRelative;
    }
    
    public boolean isHttp10Compatible()
    {
        return http10Compatible;
    }
    
    public void setHttp10Compatible(boolean http10Compatible)
    {
        this.http10Compatible = http10Compatible;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
}
