package pw.cdmi.box.disk.httpclient.rest.request;

public class FileRenameRequest
{
    
    private String name;
    
    public FileRenameRequest(String name)
    {
        this.name = name;
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
        return "FileRenameRequest [name=" + name + ']';
    }
    
}
