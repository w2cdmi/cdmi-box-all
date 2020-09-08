package pw.cdmi.box.disk.httpclient.rest.request;

public class FolderBaseRequest extends BaseRequest
{
    private String ownerID;
    
    private String folderID = "0";
    
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
    
}
