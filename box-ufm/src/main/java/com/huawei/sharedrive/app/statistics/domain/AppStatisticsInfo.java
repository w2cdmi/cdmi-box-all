package com.huawei.sharedrive.app.statistics.domain;

public class AppStatisticsInfo
{
    
    /** 文件总数 */
    private long fileCount;
    
    /** 已经使用的空间配额 */
    private long spaceUsed;
    
    /** 空间总数 */
    private long spaceCount;
    
    public long getFileCount()
    {
        return fileCount;
    }
    
    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public long getSpaceCount()
    {
        return spaceCount;
    }
    
    public void setSpaceCount(long spaceCount)
    {
        this.spaceCount = spaceCount;
    }
    
}
