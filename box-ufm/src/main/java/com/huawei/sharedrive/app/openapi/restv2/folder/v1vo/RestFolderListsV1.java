package com.huawei.sharedrive.app.openapi.restv2.folder.v1vo;

import java.util.List;

public class RestFolderListsV1
{
    private List<RestFileInfoV1> files;
    
    private List<RestFolderInfoV1> folders;
    
    private int limit;
    
    private long offset;
    
    private int totalCount;
    
    public List<RestFileInfoV1> getFiles()
    {
        return files;
    }
    
    public List<RestFolderInfoV1> getFolders()
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
    
    public void setFiles(List<RestFileInfoV1> files)
    {
        this.files = files;
    }
    
    public void setFolders(List<RestFolderInfoV1> folders)
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
