package pw.cdmi.box.disk.client.domain.node;

public class RestoreTrashRequest
{
    private Long destFolderId;
    
    private Boolean autoRename;

    public RestoreTrashRequest()
    {
    }

    public RestoreTrashRequest(Long destFolderId, boolean autoRename)
    {
        this.destFolderId = destFolderId;
        this.autoRename = autoRename;
    }
    
    public Long getDestFolderId()
    {
        return destFolderId;
    }

    public void setDestFolderId(Long destFolderId)
    {
        this.destFolderId = destFolderId;
    }

    public Boolean getAutoRename()
    {
        return autoRename;
    }

    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
    }
    
}
