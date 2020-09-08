package pw.cdmi.box.uam.system.statistic.domain;

import java.io.Serializable;
import java.util.Date;

public class SystemStat implements Serializable
{
    private static final long serialVersionUID = 1609773054375275677L;
    
    private Date statDate;
    
    private Date createDate;
    
    private long totalUser;
    
    private long loginUserCount;
    
    private long webAccessAgentCount;
    
    private long pcAccessAgentCount;
    
    private long androidAccessCount;
    
    private long iosAccessCount;
    
    private String appId;
    
    public Date getStatDate()
    {
        return statDate == null ? null : (Date) statDate.clone();
    }
    
    public void setStatDate(Date statDate)
    {
        this.statDate = (statDate == null ? null : (Date) statDate.clone());
    }
    
    public Date getCreateDate()
    {
        return createDate == null ? null : (Date) createDate.clone();
    }
    
    public void setCreateDate(Date createDate)
    {
        this.createDate = (createDate == null ? null : (Date) createDate.clone());
    }
    
    public long getTotalUser()
    {
        return totalUser;
    }
    
    public void setTotalUser(long totalUser)
    {
        this.totalUser = totalUser;
    }
    
    public long getLoginUserCount()
    {
        return loginUserCount;
    }
    
    public void setLoginUserCount(long loginUserCount)
    {
        this.loginUserCount = loginUserCount;
    }
    
    public long getWebAccessAgentCount()
    {
        return webAccessAgentCount;
    }
    
    public void setWebAccessAgentCount(long webAccessAgentCount)
    {
        this.webAccessAgentCount = webAccessAgentCount;
    }
    
    public long getPcAccessAgentCount()
    {
        return pcAccessAgentCount;
    }
    
    public void setPcAccessAgentCount(long pcAccessAgentCount)
    {
        this.pcAccessAgentCount = pcAccessAgentCount;
    }
    
    public long getAndroidAccessCount()
    {
        return androidAccessCount;
    }
    
    public void setAndroidAccessCount(long androidAccessCount)
    {
        this.androidAccessCount = androidAccessCount;
    }
    
    public long getIosAccessCount()
    {
        return iosAccessCount;
    }
    
    public void setIosAccessCount(long iosAccessCount)
    {
        this.iosAccessCount = iosAccessCount;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
}
