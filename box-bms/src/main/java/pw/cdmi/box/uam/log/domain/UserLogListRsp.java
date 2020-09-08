package pw.cdmi.box.uam.log.domain;

import java.util.List;

public class UserLogListRsp
{
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    private List<UserLogRes> userLogs;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public List<UserLogRes> getUserLogs()
    {
        return userLogs;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUserLogs(List<UserLogRes> userLogs)
    {
        this.userLogs = userLogs;
    }
    
}
