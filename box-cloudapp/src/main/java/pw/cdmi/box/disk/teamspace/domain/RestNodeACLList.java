package pw.cdmi.box.disk.teamspace.domain;

import java.util.List;

public class RestNodeACLList
{
    private List<RestNodeACLInfo> acls;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<RestNodeACLInfo> getAcls()
    {
        return acls;
    }
    
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
    
    public void setAcls(List<RestNodeACLInfo> acls)
    {
        this.acls = acls;
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
    
}
