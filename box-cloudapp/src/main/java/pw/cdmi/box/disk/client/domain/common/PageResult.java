package pw.cdmi.box.disk.client.domain.common;



public class PageResult
{
    private int limit;
    
    private long offset;
    
    private int totalCount;
    
    public PageResult()
    {
        
    }

    public PageResult(int limit, long offset, int totalCount)
    {
        this.limit = limit;
        this.offset = offset;
        this.totalCount = totalCount;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public long getOffset()
    {
        return offset;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
