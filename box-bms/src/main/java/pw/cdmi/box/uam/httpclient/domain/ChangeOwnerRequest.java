package pw.cdmi.box.uam.httpclient.domain;

public class ChangeOwnerRequest
{
    private Long newOwnerId;
    
    public Long getNewOwnerId()
    {
        return newOwnerId;
    }
    
    public void setNewOwnerId(Long newOwnerId)
    {
        this.newOwnerId = newOwnerId;
    }
    
}
