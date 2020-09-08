package pw.cdmi.box.uam.statistics.domain;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;

public class BasicStatisticsBean
{
    private TimePoint timePoint;
    
    public TimePoint getTimePoint()
    {
        return timePoint;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
    
}
