package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestUserCurrentStatisticsRequest implements Serializable
{
    
    private static final long serialVersionUID = -5155485369631977237L;
    
    private String groupBy;
    
    private Integer regionId;
    
    private String appId;
    
    public String getGroupBy()
    {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
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
