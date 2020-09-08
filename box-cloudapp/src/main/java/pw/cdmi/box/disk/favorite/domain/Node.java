package pw.cdmi.box.disk.favorite.domain;

import java.io.Serializable;

public class Node implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -4766857241830047882L;
    
    public static final String FILE = "file";
    
    public static final String FOLDER = "folder";
    
    private Long ownedBy;
    
    private Long id;
    
    private String type;
    
    public Node()
    {
    }
    
    public Node(Long ownedBy, Long id)
    {
        this.ownedBy = ownedBy;
        this.id = id;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
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
