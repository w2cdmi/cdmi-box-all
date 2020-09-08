package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;

public class Member implements Serializable
{
    
    private static final long serialVersionUID = 2263407547054265706L;
    
    private String name;
    
    private String id;
    
    private String type;
    
    public Member(String name, String id, String type)
    {
        this.name = name;
        this.id = id;
        this.type = type;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}
