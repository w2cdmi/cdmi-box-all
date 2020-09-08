package pw.cdmi.box.disk.httpclient.rest.request;

public class GetChangeMetadataRequest
{
    private long modifiedAt;
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
}
