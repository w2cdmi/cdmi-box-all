package com.huawei.sharedrive.uam.statistics.domain;

public class SystemStoreHistoryTableNode
{
    private Long spaceUsed;
    
    private Long actualSpaceUsed;
    
    private String date;
    
    public SystemStoreHistoryTableNode()
    {
        
    }
    
    public SystemStoreHistoryTableNode(long spaceUsed, long actualSpaceUsed, String date)
    {
        this.spaceUsed = spaceUsed;
        this.actualSpaceUsed = actualSpaceUsed;
        this.date = date;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public Long getActualSpaceUsed()
    {
        return actualSpaceUsed;
    }
    
    public void setActualSpaceUsed(Long actualSpaceUsed)
    {
        this.actualSpaceUsed = actualSpaceUsed;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
}
