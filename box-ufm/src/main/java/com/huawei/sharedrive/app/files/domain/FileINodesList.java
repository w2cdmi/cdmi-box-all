package com.huawei.sharedrive.app.files.domain;

import java.util.List;

public class FileINodesList
{
    /** 文件集合 */
    private List<INode> files;
    
    /** 文件夹集合 */
    private List<INode> folders;
    
    /** 分页参数：当前页数量 */
    private int limit;
    
    /** 分页参数：偏移 */
    private long offset;
    
    /** 总数 */
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
    
    public long getOffset()
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
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
