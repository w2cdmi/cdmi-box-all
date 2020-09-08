package pw.cdmi.box.uam.statistics;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;

public class StatisticsHistoryNode
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
