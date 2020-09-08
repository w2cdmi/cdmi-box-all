package pw.cdmi.box.disk.system.domain.custom;

import java.io.Serializable;
import java.util.List;

public class CustomOffice implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 7560790881980722297L;
    
    private List<CustomAttribute> attList;
    
    private String name;
    
    private String status;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public List<CustomAttribute> getAttList()
    {
        return attList;
    }
    
    public void setAttList(List<CustomAttribute> attList)
    {
        this.attList = attList;
    }
    
}
