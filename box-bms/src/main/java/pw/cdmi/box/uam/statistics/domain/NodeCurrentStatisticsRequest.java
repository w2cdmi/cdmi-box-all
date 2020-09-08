package pw.cdmi.box.uam.statistics.domain;

public class NodeCurrentStatisticsRequest
{
    private String appId;
    
    private String groupBy;
    
    private Integer regionId;
    
    public NodeCurrentStatisticsRequest()
    {
        
    }
    
    public NodeCurrentStatisticsRequest(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
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
    
}
