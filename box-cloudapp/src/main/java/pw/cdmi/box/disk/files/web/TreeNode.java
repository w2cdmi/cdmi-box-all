package pw.cdmi.box.disk.files.web;

import java.io.Serializable;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;

public class TreeNode implements Serializable
{
    private static final long serialVersionUID = -1828391397456373702L;
    
    private INode node;
    
    public TreeNode()
    {
    }
    
    public TreeNode(INode node)
    {
        this.node = node;
    }
    
    public TreeNode(RestFolderInfo folderInfo)
    {
        this.node = new INode(folderInfo);
    }
    
    public long getId()
    {
        return node.getId();
    }
    
    public Boolean getIsParent()
    {
        return Boolean.TRUE;
    }
    
    public String getName()
    {
        return node.getName();
    }
    
    public long getOwnedBy()
    {
        return node.getOwnedBy();
    }
}
