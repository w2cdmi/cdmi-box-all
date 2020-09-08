package pw.cdmi.box.uam.httpclient.domain.statistics;

public class StatisticsExcelExportRequest
{
    
    private Long beginTime;

    private Long endTime;

    private String type;

    public Long getBeginTime()
    {
        return beginTime;
    }

    public Long getEndTime()
    {
        return endTime;
    }

    public String getType()
    {
        return type;
    }

    public void setBeginTime(Long beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
