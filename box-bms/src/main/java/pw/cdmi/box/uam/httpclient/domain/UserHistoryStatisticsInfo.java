package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.UUID;

public class UserHistoryStatisticsInfo implements Serializable
{
    
    private static final long serialVersionUID = -8315740495696270054L;
    
    private Integer added;
    
    private int date;
    
    private String id;
    
    private String legend;
    
    private int month;
    
    private TimePoint timePoint;
    
    private Long userCount;
    
    public UserHistoryStatisticsInfo()
    {
        id = getUUID();// use as JQbox-hw-grid.js table id
    }
    
    public Integer getAdded()
    {
        return added;
    }
    
    public int getDate()
    {
        return date;
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getLegend()
    {
        return legend;
    }
    
    public int getMonth()
    {
        return month;
    }
    
    public TimePoint getTimePoint()
    {
        return timePoint;
    }
    
    public Long getUserCount()
    {
        return userCount;
    }
    
    private String getUUID()
    {
        UUID uuid = UUID.randomUUID();
        String randomName = uuid.toString().replace("-", "");
        
        return randomName;
    }
    
    public void setAdded(Integer added)
    {
        this.added = added;
    }
    
    public void setDate(int day)
    {
        this.date = day;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setLegend(String legend)
    {
        this.legend = legend;
    }
    
    public void setMonth(int month)
    {
        this.month = month;
    }
    
    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }
    
    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }
    
}
