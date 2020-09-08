package pw.cdmi.box.disk.httpclient.rest.request;

public class ListShareResourceRequest
{
    
    private int limit;
    
    private String orderby;
    
    private int offset;
    
    private boolean des;
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public String getOrderby()
    {
        return orderby;
    }
    
    public void setOrderby(String orderby)
    {
        this.orderby = orderby;
    }
    
    public int getOffset()
    {
        return offset;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public boolean isDes()
    {
        return des;
    }
    
    public void setDes(boolean des)
    {
        this.des = des;
    }
    
}
