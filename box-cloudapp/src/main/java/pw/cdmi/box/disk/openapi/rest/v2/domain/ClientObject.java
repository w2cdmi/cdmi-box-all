package pw.cdmi.box.disk.openapi.rest.v2.domain;

import java.io.InputStream;

public class ClientObject
{
    private String name;
    
    private ClientType type;
    
    public ClientType getType()
    {
        return type;
    }
    
    public void setType(ClientType type)
    {
        this.type = type;
    }
    
    private long size;
    
    private InputStream content;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public InputStream getContent()
    {
        return content;
    }
    
    public void setContent(InputStream content)
    {
        this.content = content;
    }
    
}
