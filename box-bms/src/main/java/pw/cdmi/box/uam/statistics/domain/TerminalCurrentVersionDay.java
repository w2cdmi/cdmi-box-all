package pw.cdmi.box.uam.statistics.domain;

public class TerminalCurrentVersionDay
{
    private String clientVersion;
    
    private int userCount;
    
    public TerminalCurrentVersionDay()
    {
        
    }
    
    public TerminalCurrentVersionDay(TerminalStatisticsDay statisticsDay)
    {
        clientVersion = statisticsDay.getClientVersion();
        userCount = statisticsDay.getUserCount();
    }
    
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    public void setClientVersion(String clientVersion)
    {
        this.clientVersion = clientVersion;
    }
    
    public int getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(int userCount)
    {
        this.userCount = userCount;
    }
    
}
