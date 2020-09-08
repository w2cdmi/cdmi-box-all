package pw.cdmi.box.disk.httpclient.rest.request;

import org.hibernate.validator.constraints.Length;

public class RestGroupRequest
{
    @Length(max = 255)
    private String name;
    
    private String description;
    
    private String status;
    
    private String type;
    
    private Long ownedBy;
    
    private Integer maxMembers;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public Integer getMaxMembers()
    {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers)
    {
        this.maxMembers = maxMembers;
    }
    
}
