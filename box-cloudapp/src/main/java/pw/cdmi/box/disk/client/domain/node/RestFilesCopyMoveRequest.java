package pw.cdmi.box.disk.client.domain.node;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.BadRquestException;

public class RestFilesCopyMoveRequest
{
    
    private Long destParent;
    
    private String name;
    
    public void checkParameter() throws BadRquestException
    {
        FilesCommonUtils.checkNonNegativeIntegers(destParent);
        
        if (null != name)
        {
            FilesCommonUtils.checkNodeNameVaild(name);
        }
    }
    
    public INode getDestINode()
    {
        INode destNode = new INode();
        if (destParent != null)
        {
            destNode.setId(destParent);
        }
        return destNode;
    }
    
    public Long getDestParent()
    {
        return destParent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setDestParent(Long destParent)
    {
        this.destParent = destParent;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
}
