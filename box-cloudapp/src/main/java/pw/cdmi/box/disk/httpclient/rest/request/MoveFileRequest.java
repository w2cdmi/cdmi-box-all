package pw.cdmi.box.disk.httpclient.rest.request;

public class MoveFileRequest
{
    private String destParent = null;
    
    private String name;
    
    public String getDestParent()
    {
        return destParent;
    }
    
    public void setDestParent(String destParent)
    {
        this.destParent = destParent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return "MoveFileRequest [destParent=" + destParent + ", name=" + name + ']';
    }
    
}
