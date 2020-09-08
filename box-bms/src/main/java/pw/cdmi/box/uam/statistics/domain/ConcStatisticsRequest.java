package pw.cdmi.box.uam.statistics.domain;

public class ConcStatisticsRequest
{
    public ConcStatisticsRequest()
    {
        
    }
    
    public ConcStatisticsRequest(ConcurrentHistoryRequest historyRequest)
    {
        beginTime = historyRequest.getBeginTime() != null ? historyRequest.getBeginTime().getTime() : null;
        endTime = historyRequest.getEndTime() != null ? historyRequest.getEndTime().getTime() : null;
        interval = historyRequest.getInterval();
    }
    
    private Long beginTime;
    
    private Long endTime;
    
    private String interval;
    
    public Long getBeginTime()
    {
        return beginTime;
    }
    
    public Long getEndTime()
    {
        return endTime;
    }
    
    public String getInterval()
    {
        return interval;
    }
    
    public void setBeginTime(Long beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }
    
    public void setInterval(String interval)
    {
        this.interval = interval;
    }
    
}
