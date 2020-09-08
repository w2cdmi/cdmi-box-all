package pw.cdmi.box.disk.client.domain.task;

public class ConflictNode
{
    
    private long id;
    
    private long ownerId;

    public long getId()
    {
        return id;
    }

    public long getOwnerId()
    {
        return ownerId;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
}
