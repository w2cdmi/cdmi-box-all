package pw.cdmi.box.disk.files.domain;

import java.util.List;

import pw.cdmi.box.disk.client.domain.node.INode;

public class FileINodesList
{
    private List<INode> files;
    
    private List<INode> folders;
    
    private int limit;
    
    private int offset;
    
    private int totalCount;
    
    public List<INode> getFiles()
    {
        return files;
    }
    
    public List<INode> getFolders()
    {
        return folders;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public int getOffset()
    {
        return offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setFiles(List<INode> files)
    {
        this.files = files;
    }
    
    public void setFolders(List<INode> folders)
    {
        this.folders = folders;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
