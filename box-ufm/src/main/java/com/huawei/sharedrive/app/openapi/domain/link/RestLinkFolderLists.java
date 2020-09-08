package com.huawei.sharedrive.app.openapi.domain.link;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;

public class RestLinkFolderLists
{
    private List<RestLinkFileInfo> files;
    
    private List<RestLinkFolderInfo> folders;
    
    private int limit;
    
    private long offset;
    
    private int totalCount;
    
    public RestLinkFolderLists()
    {
        
    }
    
    public RestLinkFolderLists(FileINodesList relist)
    {
        this.addToFiles(relist.getFiles());
        this.addToFolders(relist.getFolders());
        this.setTotalCount(relist.getTotalCount());
        this.setLimit(relist.getLimit());
        this.setOffset(relist.getOffset());
    }
    
    public void addToFiles(List<INode> files)
    {
        if (null == files)
        {
            return;
        }
        
        this.files = new ArrayList<RestLinkFileInfo>(files.size());
        
        RestLinkFileInfo linkFileInfo = null;
        for (INode inode : files)
        {
            linkFileInfo = new RestLinkFileInfo(inode);
            this.files.add(linkFileInfo);
        }
        
    }
    
    public void addToFolders(List<INode> folders)
    {
        if (null == folders)
        {
            return;
        }
        this.folders = new ArrayList<RestLinkFolderInfo>(folders.size());
        RestLinkFolderInfo linkFolderInfo = null;
        for (INode inode : folders)
        {
            linkFolderInfo = new RestLinkFolderInfo(inode);
            this.folders.add(linkFolderInfo);
        }
    }
    
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
