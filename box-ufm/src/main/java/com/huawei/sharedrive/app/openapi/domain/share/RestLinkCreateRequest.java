package com.huawei.sharedrive.app.openapi.domain.share;

import java.util.Date;

public class RestLinkCreateRequest
{
    
    private Date effectiveAt;
    
    private Date expireAt;
    
    private String plainAccessCode;
    
    public Date getEffectiveAt()
    {
        if (effectiveAt == null)
        {
            return null;
        }
        return (Date) effectiveAt.clone();
    }
    
    public Date getExpireAt()
    {
        if (expireAt == null)
        {
            return null;
        }
        return (Date) expireAt.clone();
    }
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public void setEffectiveAt(Date effectiveAt)
    {
        if (effectiveAt == null)
        {
            this.effectiveAt = null;
        }
        else
        {
            this.effectiveAt = (Date) effectiveAt.clone();
        }
    }
    
    public void setExpireAt(Date expireAt)
    {
        if (expireAt == null)
        {
            this.expireAt = null;
        }
        else
        {
            this.expireAt = (Date) expireAt.clone();
        }
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
}
