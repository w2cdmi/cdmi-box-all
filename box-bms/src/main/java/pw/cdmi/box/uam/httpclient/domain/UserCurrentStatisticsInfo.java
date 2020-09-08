package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class UserCurrentStatisticsInfo implements Serializable
{
    private static final long serialVersionUID = 4355726147383179793L;
    
    private String appId;
    
    private Integer regionId;
    
    private String regionName;
    
    private Long userCount;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public String getRegionName()
    {
        return regionName;
    }
    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
    public Long getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }
    
}
