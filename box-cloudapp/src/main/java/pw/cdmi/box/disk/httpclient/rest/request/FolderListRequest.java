package pw.cdmi.box.disk.httpclient.rest.request;

public class FolderListRequest
{
    private String ownerID;
    
    private String folderID = "0";
    
    private Integer offset = 0;
    
    private Integer limit = 100;
    
    private String orderby = "modifiedAt";
    
    private String des = "des";
    
    public String getOwnerID()
    {
        return ownerID;
    }
    
    public void setOwnerID(String ownerID)
    {
        this.ownerID = ownerID;
    }
    
    public String getFolderID()
    {
        return folderID;
    }
    
    public void setFolderID(String folderID)
    {
        this.folderID = folderID;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
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
    
    public String getDes()
    {
        return des;
    }
    
    public void setDes(String des)
    {
        this.des = des;
    }
    
}
