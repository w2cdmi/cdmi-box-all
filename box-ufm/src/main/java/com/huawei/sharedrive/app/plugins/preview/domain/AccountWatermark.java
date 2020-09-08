package com.huawei.sharedrive.app.plugins.preview.domain;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

public class AccountWatermark
{
    private long accountId;
    
    private byte[] watermark;
    
    private Date lastConfigTime;
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public byte[] getWatermark()
    {
        if (watermark == null)
        {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return watermark.clone();
    }
    
    public void setWatermark(byte[] watermark)
    {
        if (watermark == null)
        {
            this.watermark = ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        else
        {
            this.watermark = watermark.clone();
        }
    }
    
    public Date getLastConfigTime()
    {
        if (lastConfigTime == null)
        {
            return null;
        }
        return (Date) lastConfigTime.clone();
    }
    
    public void setLastConfigTime(Date lastConfigTime)
    {
        if (lastConfigTime == null)
        {
            this.lastConfigTime = null;
        }
        else
        {
            this.lastConfigTime = (Date) lastConfigTime.clone();
        }
    }
    
}
