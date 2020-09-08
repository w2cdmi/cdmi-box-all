package pw.cdmi.box.uam.statistics.domain;

public class FileHistoryInfo
{
    private long fileNum;
    
    private long userFileNum;
    
    private String timeType;
    
    private long statisticsDay;
    
    private String imageSrc;
    
    public long getFileNum()
    {
        return fileNum;
    }
    
    public void setFileNum(long fileNum)
    {
        this.fileNum = fileNum;
    }
    
    public long getUserFileNum()
    {
        return userFileNum;
    }
    
    public void setUserFileNum(long userFileNum)
    {
        this.userFileNum = userFileNum;
    }
    
    public String getTimeType()
    {
        return timeType;
    }
    
    public void setTimeType(String timeType)
    {
        this.timeType = timeType;
    }
    
    public long getStatisticsDay()
    {
        return statisticsDay;
    }
    
    public void setStatisticsDay(long statisticsDay)
    {
        this.statisticsDay = statisticsDay;
    }
    
    public String getImageSrc()
    {
        return imageSrc;
    }
    
    public void setImageSrc(String imageSrc)
    {
        this.imageSrc = imageSrc;
    }
    
}
