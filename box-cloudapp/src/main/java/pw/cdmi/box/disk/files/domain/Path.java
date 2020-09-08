package pw.cdmi.box.disk.files.domain;

import java.io.Serializable;

import pw.cdmi.box.disk.client.domain.node.INode;

public class Path implements Serializable
{
    private static final long serialVersionUID = -5976031456296361767L;
    
    private final INode node;
    
    public Path(INode node)
    {
        this.node = node;
    }
    
    public long getId()
    {
        return node.getId();
    }
    
    public String getName()
    {
        return node.getName();
    }
}
