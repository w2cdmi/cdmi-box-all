/**
 * 外链request对象
 */
package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;

/**
 * @author l90005448
 * 
 */
public class LinkRequest implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5418594937509226948L;
    
    private String accessCode;
    
    private String effectiveAt;
    
    private String expireAt;
    
    private String timeZone;
    
    private String url;
    
    public String getAccessCode()
    {
        return accessCode;
    }
    
    public String getEffectiveAt()
    {
        return effectiveAt;
    }
    
    public String getExpireAt()
    {
        return expireAt;
    }
    
    public String getTimeZone()
    {
        return timeZone;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }
    
    public void setEffectiveAt(String effectiveAt)
    {
        this.effectiveAt = effectiveAt;
    }
    
    public void setExpireAt(String expireAt)
    {
        this.expireAt = expireAt;
    }
    
    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
}
