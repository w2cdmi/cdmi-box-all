package com.huawei.sharedrive.uam.authserver.domain;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.web.util.HtmlUtils;

public class AccountAuthserverNetwork implements Serializable
{
    private static final long serialVersionUID = -9216750520395141089L;
    
    private long id;
    
    private long authServerId;
    
    private long accountId;
    
    @Size(max = 32)
    private String ipStart;
    
    @Size(max = 32)
    private String ipEnd;
    
    private long ipStartValue = -1;
    
    private long ipEndValue = -1;
    
    public static void htmlEscape(List<AccountAuthserverNetwork> list)
    {
        if (null == list || list.size() <= 0)
        {
            return;
        }
        for (AccountAuthserverNetwork iter : list)
        {
            iter.setIpStart(HtmlUtils.htmlEscape(iter.getIpStart()));
            iter.setIpEnd(HtmlUtils.htmlEscape(iter.getIpEnd()));
        }
    }
    
    public void setAuthServerId(long authServerId)
    {
        this.authServerId = authServerId;
    }
    
    public long getAuthServerId()
    {
        return authServerId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setIpStart(String ipStart)
    {
        this.ipStart = ipStart;
    }
    
    public String getIpStart()
    {
        return ipStart;
    }
    
    public void setIpEnd(String ipEnd)
    {
        this.ipEnd = ipEnd;
    }
    
    public String getIpEnd()
    {
        return ipEnd;
    }
    
    public void setIpStartValue(long ipStartValue)
    {
        this.ipStartValue = ipStartValue;
    }
    
    public long getIpStartValue()
    {
        return ipStartValue;
    }
    
    public void setIpEndValue(long ipEndValue)
    {
        this.ipEndValue = ipEndValue;
    }
    
    public long getIpEndValue()
    {
        return ipEndValue;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
}
