package pw.cdmi.box.disk.share.domain;

import java.util.List;

public class RestLinkFolderLists
{
    private List<RestLinkFileInfo> files;
    
    private List<RestLinkFolderInfo> folders;
    
    private int limit;
    
    private long offset;
    
    private int totalCount;
    
    public List<RestLinkFileInfo> getFiles()
    {
        return files;
    }
    
    public List<RestLinkFolderInfo> getFolders()
    {
        return folders;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setFiles(List<RestLinkFileInfo> files)
    {
        this.files = files;
    }
    
    public void setFolders(List<RestLinkFolderInfo> folders)
    {
        this.folders = folders;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
}
