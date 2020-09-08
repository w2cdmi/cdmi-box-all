package pw.cdmi.box.uam.statistics.domain;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;
import pw.cdmi.box.uam.statistics.StatisticsHistoryNode;

public class TerminalVersionHistoryTableNode extends StatisticsHistoryNode
{
    private int userCount;
    
    private String versionName;
    
    private String date;
    
    public TerminalVersionHistoryTableNode()
    {
        
    }
    
    public TerminalVersionHistoryTableNode(int userCount, String versionName, String date, TimePoint timePoint)
    {
        this.userCount = userCount;
        this.versionName = versionName;
        this.date = date;
        super.setTimePoint(timePoint);
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public int getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
    public String getVersionName()
    {
        return versionName;
    }
    
    public void setVersionName(String versionName)
    {
        this.versionName = versionName;
    }
}
