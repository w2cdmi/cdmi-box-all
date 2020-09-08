package pw.cdmi.box.disk.client.domain.teamspace;

import java.io.Serializable;
import java.util.List;

public class GetTeamSpaceAttrResponse implements Serializable
{
    
    private static final long serialVersionUID = 5560538802637074065L;
    
    private List<TeamSpaceAttribute> attributes;
    
    public List<TeamSpaceAttribute> getAttributes()
    {
        return attributes;
    }
    
    public void setAttributes(List<TeamSpaceAttribute> attributes)
    {
        this.attributes = attributes;
    }
    
}
