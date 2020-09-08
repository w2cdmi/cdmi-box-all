package pw.cdmi.box.disk.client.domain.node;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.InvalidParamException;

public class GetNodeByNameRequest
{
    private String name;
    
    public GetNodeByNameRequest()
    {
        
    }
    
    public GetNodeByNameRequest(String name)
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
    
    public void checkParameter() throws InvalidParamException
    {
        FilesCommonUtils.checkNodeNameVaild(name);
    }
}
