package com.huawei.sharedrive.app.files.service.job;

/**
 * 统计信息对象
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-9-9
 * @see
 * @since
 */
public class StatisticsInfo
{
    // 文件总数
    private long fileCount;
    
    // 最后一次统计时间 yyyy-mm-dd HH:mm:ss
    private String lastStatisticsTime;
    
    // 分配总空间
    private long spaceQuota;
    
    // 已用空间
    private long spaceUsed;
    
    public long getFileCount()
    {
        return fileCount;
    }
    
    public String getLastStatisticsTime()
    {
        return lastStatisticsTime;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setLastStatisticsTime(String lastStatisticsTime)
    {
        this.lastStatisticsTime = lastStatisticsTime;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
}
